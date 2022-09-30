/*
 * Copyright 2011-2022 GatlingCorp (https://gatling.io)
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

package io.gatling.build

import io.gatling.build.automated.GatlingAutomatedScalafixPlugin
import io.gatling.build.automated.GatlingAutomatedScalafmtPlugin
import io.gatling.build.basic.GatlingBasicInfoPlugin
import io.gatling.build.basic.GatlingBasicInfoPlugin.GatlingBasicInfoKeys._
import io.gatling.build.compile.GatlingCompilerSettingsPlugin
import io.gatling.build.license._
import io.gatling.build.publish.GatlingPublishPlugin
import io.gatling.build.release.GatlingReleasePlugin
import io.gatling.build.release.GatlingReleasePlugin.GatlingReleaseKeys._
import io.gatling.build.versioning.GatlingVersioningPlugin

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import de.heikoseeberger.sbtheader.AutomateHeaderPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations.reapply
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys._

import sbt._
import sbt.Keys._

object GatlingOssPlugin extends AutoPlugin {
  override def requires =
    GatlingAutomatedScalafixPlugin &&
      GatlingAutomatedScalafmtPlugin &&
      GatlingVersioningPlugin &&
      GatlingBasicInfoPlugin &&
      GatlingCompilerSettingsPlugin &&
      GatlingPublishPlugin &&
      GatlingReleasePlugin &&
      AutomateHeaderPlugin &&
      Apache2LicenseFilePlugin &&
      Sonatype

  trait GatlingOssKeys {
    val gatlingPublishToSonatype = settingKey[Boolean]("true to publish to sonatype")
  }
  object GatlingOssKeys extends GatlingOssKeys
  object autoImport extends GatlingOssKeys

  import autoImport._

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    gatlingPublishToSonatype := ensurePublishableVersion(version.value)
  )

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    headerLicense := ApacheV2License,
    sonatypeProfileName := "io.gatling",
    publishTo := {
      if (gatlingPublishToSonatype.value) {
        sonatypePublishTo.value
      } else {
        publishTo.value
      }
    },
    sonatypeSessionName := s"[sbt-sonatype] ${githubPath.value} ${(ThisBuild / version).value}",
    releasePublishArtifactsAction := {
      if (gatlingPublishToSonatype.value) {
        publishSigned.value
      } else {
        Keys.publish.value
      }
    },
    gatlingReleasePublishStep := conditionalPublishStep
  )

  val conditionalPublishStep: ReleaseStep = { state: State =>
    if (Project.extract(state).get(gatlingPublishToSonatype)) {
      publishStep(state)
    } else {
      GatlingReleasePlugin.publishStep(state)
    }
  }

  val publishStep: ReleaseStep = { state: State =>
    /*
     * Issues:
     *  - sbt-sonatype plugin only declares commands (not tasks)
     *  - sonatypeOpen command calls appendWithoutSession, and release version is reset to its -SNAPSHOT
     *
     * Workaround:
     *  - retrieve sonatypePublishTo settings after applying sonatypeOpen command
     *  - inject it to the state needed by publishSigned task
     *  - call sonatypeClose command with full state from sonatypeOpen
     */
    state.log.info(s"Opening sonatype staging")
    val sonatypeOpenState = releaseStepCommandAndRemaining("sonatypeOpen")(state)
    val sonatypeTargetRepositoryProfileValue = sonatypeOpenState.getSetting(sonatypeTargetRepositoryProfile).get

    val startStateWithSonatypeConf = reapply(
      Project.extract(state).currentProject.referenced.flatMap { ref =>
        ref / sonatypeTargetRepositoryProfile := sonatypeTargetRepositoryProfileValue
      } ++
        Seq(
          sonatypeTargetRepositoryProfile := sonatypeTargetRepositoryProfileValue
        ),
      state
    )

    state.log.info("compile, package, sign and publish")
    val endState = {
      val extracted = Project.extract(startStateWithSonatypeConf)
      extracted.runAggregated(
        extracted.currentRef / releasePublishArtifactsAction,
        startStateWithSonatypeConf
      )
    }

    state.log.info("Closing sonatype staging")
    Def.unit(releaseStepCommandAndRemaining("sonatypeClose")(sonatypeOpenState))
    endState
  }

  def ensurePublishableVersion(str: String): Boolean = str.matches("\\d+\\.\\d+\\.\\d+(-M\\d+)?")
}
