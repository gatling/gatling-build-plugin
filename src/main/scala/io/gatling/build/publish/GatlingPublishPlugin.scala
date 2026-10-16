/*
 * Copyright 2011-2026 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.build.publish

import aether.{ AetherKeys, AetherPlugin, SignedAetherPlugin }
import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import sbtdynver.DynVerPlugin.autoImport.dynverGitDescribeOutput

import sbt._
import sbt.Keys._

object GatlingPublishPlugin extends AutoPlugin {
  // SignedAetherPlugin is triggered in every project: requiring it makes these settings override its own
  override def requires: Plugins = plugins.JvmPlugin && SignedAetherPlugin

  trait GatlingPublishKeys {
    val gatlingStagingRepository = settingKey[Option[Resolver]]("Repository for public artifacts with a Maven Central compatible version")
    val gatlingMilestonesRepository = settingKey[Option[Resolver]]("Repository for other milestones (version ending with -M<n>)")
    val gatlingReleasesRepository = settingKey[Option[Resolver]]("Repository for other releases")

    // Publishing must run on every call: never cached by sbt 2
    @transient val gatlingPublish = taskKey[Unit]("Publishes the artifacts if HEAD is a release tag")
  }
  object GatlingPublishKeys extends GatlingPublishKeys
  object autoImport extends GatlingPublishKeys

  import autoImport._

  // Set to true by the plugins of public artifacts (OSS and Enterprise Component)
  val gatlingPublicArtifact = settingKey[Boolean]("true for an artifact meant for Maven Central")

  // A check must run on every call: never cached by sbt 2
  @transient val gatlingCheckSnapshotDependencies = taskKey[Unit]("Fails if a runtime dependency has a SNAPSHOT or changing version")

  sealed abstract class PublishRoute
  object PublishRoute {
    case object Staging extends PublishRoute
    case object Milestones extends PublishRoute
    case object Releases extends PublishRoute
  }

  // Repositories depend on the environment (CI, local, customer): none is defined here
  override def globalSettings: Seq[Setting[_]] = Seq(
    // Projects without this plugin, such as aggregators, publish nothing
    gatlingPublish := {},
    gatlingStagingRepository := None,
    gatlingMilestonesRepository := None,
    gatlingReleasesRepository := None
  )

  override def projectSettings: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    crossPaths := false,
    resolvers += Resolver.mavenLocal,
    // Maven Central requires a javadoc jar but its content is useless: publish an empty one and skip doc generation
    Compile / packageDoc / mappings := Seq.empty,
    gatlingPublicArtifact := false,
    publishTo := {
      val staging = gatlingStagingRepository.value
      val milestones = gatlingMilestonesRepository.value
      val releases = gatlingReleasesRepository.value
      route(gatlingPublicArtifact.value, version.value, isSnapshot.value).flatMap {
        case PublishRoute.Staging    => staging
        case PublishRoute.Milestones => milestones
        case PublishRoute.Releases   => releases
      }
    },
    gatlingCheckSnapshotDependencies := {
      val snapshots = SnapshotDependenciesCompat.runtimeModuleIds.value.filter(isSnapshotDependency)
      if (snapshots.nonEmpty) {
        sys.error(s"Snapshot dependencies detected in ${thisProject.value.id}:\n${snapshots.mkString("\n")}")
      }
    },
    gatlingPublish := Def.taskDyn {
      // Every .value is evaluated before the body, whatever the branch
      val log = streams.value.log
      val git = dynverGitDescribeOutput.value
      val module = s"${thisProject.value.id} ${version.value}"
      val skipped = (publish / skip).value || !(Compile / publishArtifact).value
      val repository = publishTo.value
      val publicArtifact = gatlingPublicArtifact.value
      val repositoryKey = route(publicArtifact, version.value, isSnapshot.value) match {
        case Some(PublishRoute.Staging)    => gatlingStagingRepository.key.label
        case Some(PublishRoute.Milestones) => gatlingMilestonesRepository.key.label
        case _                             => gatlingReleasesRepository.key.label
      }

      // A project never meant to be published is not concerned by the git state
      if (skipped) {
        Def.task(())
      } else if (git.exists(_.isDirty())) {
        sys.error(s"Modified working tree: $module is not published")
      } else if (!git.exists(_.isCleanAfterTag)) {
        log.info(s"HEAD is not a release tag: $module is not published")
        Def.task(())
      } else if (repository.isEmpty) {
        sys.error(s"No repository to publish $module: define $repositoryKey")
      } else {
        Def.taskDyn {
          gatlingCheckSnapshotDependencies.value
          if (publicArtifact) publishSigned else publish
        }
      }
    }.value
  ) ++
    // Publish with Maven's deployer, which uploads maven-metadata.xml unlike sbt's
    AetherPlugin.autoImport.overridePublishSettings ++
    SignedAetherPlugin.autoImport.overridePublishSignedSettings

  // SignedAetherPlugin signs the deployed artifacts: restore AetherPlugin's unsigned definition
  val unsignedAetherArtifact: Seq[Setting[_]] =
    AetherPlugin.projectSettings.filter(_.key.key == AetherKeys.aetherArtifact.key)

  private val MavenCentralVersion = """\d+\.\d+\.\d+(\.\d+)?(-M\d+)?""".r
  private val MilestoneVersion = """.*-M\d+""".r

  // Snapshots are never published
  def route(publicArtifact: Boolean, version: String, snapshot: Boolean): Option[PublishRoute] =
    if (snapshot) None
    else if (publicArtifact && MavenCentralVersion.pattern.matcher(version).matches) Some(PublishRoute.Staging)
    else if (MilestoneVersion.pattern.matcher(version).matches) Some(PublishRoute.Milestones)
    else Some(PublishRoute.Releases)

  // Same rule as sbt-release's releaseSnapshotDependencies
  private def isSnapshotDependency(module: ModuleID): Boolean =
    module.isChanging || module.revision.endsWith("-SNAPSHOT")
}
