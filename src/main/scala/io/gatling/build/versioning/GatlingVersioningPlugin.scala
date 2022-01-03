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

import com.typesafe.sbt.GitVersioning
import com.typesafe.sbt.SbtGit.git

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

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    git.gitDescribePatterns := Seq("v*"),
    git.useGitDescribe := true,
    git.uncommittedSignifier := Some("dirty"),
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
      token("milestone") ^^^ GatlingBump.Milestone |
      token("calver") ^^^ GatlingBump.CalVer)

  val defaultBumpVersion = Def.inputTaskDyn {
    val bump = bumpParser.parsed
    Def.task[String] {
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
