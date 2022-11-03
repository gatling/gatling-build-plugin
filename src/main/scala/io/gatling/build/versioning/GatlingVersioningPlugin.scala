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

package io.gatling.build.versioning

import java.time.Clock

import scala.util.Try

import com.github.sbt.git.GitVersioning
import com.github.sbt.git.JGit
import com.github.sbt.git.SbtGit.git

import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser

object GatlingVersioningPlugin extends AutoPlugin {
  override def requires: Plugins = GitVersioning

  trait GatlingVersioningKeys {
    val gatlingBumpVersion = inputKey[String]("What will be the version")
    val gatlingWriteBumpVersion = inputKey[File]("Write what will be the version in target/gatlingNextVersion")
    val isMilestone = settingKey[Boolean]("Indicate if release process is milestone")
    type GatlingVersion = _root_.io.gatling.build.versioning.GatlingVersion
  }
  object GatlingVersioningKeys extends GatlingVersioningKeys
  object autoImport extends GatlingVersioningKeys

  import autoImport._

  private val gitInternalDescribedVersion = SettingKey.local[Option[String]]

  private def gatlingReleaseVersion(currentTags: Seq[String], releaseTagVersion: String => Option[String], suffix: String): Option[String] = {
    val versions =
      for {
        tag <- currentTags
        version <- releaseTagVersion(tag)
        gatlingVersion <- GatlingVersion(version)
      } yield gatlingVersion

    // NOTE - Selecting the last tag or the first tag should be an option.
    val highestVersion = versions.sorted.lastOption
    highestVersion.map(_.string + suffix)
  }

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    git.gitDescribePatterns := Seq("v*"),
    git.useGitDescribe := true,
    git.uncommittedSignifier := Some("dirty"),
    gitInternalDescribedVersion := {
      Try(
        Option(
          JGit(baseDirectory.value).porcelain
            .describe()
            .setTags(false) // Only annotated tags not any tags
            .call()
        )
      )
        .getOrElse(None)
        .map(v => git.gitTagToVersionNumber.value(v).getOrElse(v))
    },
    git.gitDescribedVersion := {
      implicit val clock: Clock = Clock.systemUTC()
      for {
        current <- gitInternalDescribedVersion.value
        onlyTag = current.split("-").toList.reverse.drop(2).reverse.mkString("-")
        oldGatlingVersion <- GatlingVersion(onlyTag)
      } yield GatlingBump.Patch.bump(oldGatlingVersion).string
    },
    version := {
      val overrideVersion =
        git.overrideVersion(git.versionProperty.value)
      val uncommittedSuffix =
        git.makeUncommittedSignifierSuffix(git.gitUncommittedChanges.value, git.uncommittedSignifier.value)
      val releaseVersion =
        gatlingReleaseVersion(git.gitCurrentTags.value, git.gitTagToVersionNumber.value, uncommittedSuffix)
      val describedVersion =
        git.flaggedOptional(git.useGitDescribe.value, git.describeVersion(git.gitDescribedVersion.value, uncommittedSuffix))
      val datedVersion = git.formattedDateVersion.value
      val commitVersion = git.formattedShaVersion.value
      //Now we fall through the potential version numbers...
      git.makeVersion(Seq(
        overrideVersion,
        releaseVersion,
        describedVersion,
        commitVersion
      )) getOrElse datedVersion // For when git isn't there at all.
    },
    version := {
      if (isSnapshot.value) s"${version.value}-SNAPSHOT" else version.value
    },
    isMilestone := version(GatlingVersion(_).exists(_.isMilestone)).value
  )

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    gatlingBumpVersion := defaultBumpVersion.evaluated,
    gatlingWriteBumpVersion := defaultWriteBumpVersion.evaluated
  )

  private lazy val bumpParser: Parser[GatlingBump] =
    Space ~> (token("minor") ^^^ GatlingBump.Minor |
      token("patch") ^^^ GatlingBump.Patch |
      token("calver") ^^^ GatlingBump.CalVer |
      token("patch-milestone") ^^^ GatlingBump.milestone(GatlingBump.Patch) |
      token("minor-milestone") ^^^ GatlingBump.milestone(GatlingBump.Minor) |
      token("calver-milestone") ^^^ GatlingBump.milestone(GatlingBump.CalVer))

  val defaultBumpVersion = Def.inputTaskDyn {
    val bump = bumpParser.parsed
    Def.task[String] {
      implicit val clock: Clock = Clock.systemUTC()
      val currentVersion = (ThisBuild / version).value
      GatlingVersion(currentVersion)
        .map(bump.bump)
        .map(_.string)
        .getOrElse(throw new IllegalStateException(s"Cannot bump unparsable version (got: '$currentVersion')"))
    }
  }

  val defaultWriteBumpVersion = Def.inputTask {
    val nextVersion = defaultBumpVersion.evaluated
    val versionFile = target.value / "gatlingNextVersion"
    IO.write(versionFile, nextVersion)
    versionFile
  }
}
