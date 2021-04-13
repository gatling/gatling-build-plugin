/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
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
    val isMilestone = settingKey[Boolean]("Indicate if release process is milestone")
    type GatlingVersion = _root_.io.gatling.build.versioning.GatlingVersion
  }
  object GatlingVersioningKeys extends GatlingVersioningKeys
  object autoimport extends GatlingVersioningKeys

  import autoimport._

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    git.gitDescribePatterns := Seq("v*"),
    git.useGitDescribe := true,
    git.uncommittedSignifier := Some("dirty"),
    version := {
      if (isSnapshot.value) s"${version.value}-SNAPSHOT" else version.value
    },
    isMilestone := version(GatlingVersion(_).exists(_.isMilestone)).value,
    gatlingBumpVersion := defaultBumpVersion.evaluated
  )

  private lazy val minorParser: Parser[GatlingBump] =
    (Space ~> token("minor")) ^^^ GatlingBump.Minor

  private lazy val patchParser: Parser[GatlingBump] =
    (Space ~> token("patch")) ^^^ GatlingBump.Patch

  private lazy val milestoneParser: Parser[GatlingBump] =
    (Space ~> token("milestone")) ^^^ GatlingBump.Milestone

  private lazy val calverParser: Parser[GatlingBump] =
    (Space ~> token("calver")) ^^^ GatlingBump.CalVer

  private lazy val bumpParser: Parser[GatlingBump] = minorParser | patchParser | milestoneParser | calverParser

  val defaultBumpVersion = Def.inputTaskDyn {
    val bump = bumpParser.parsed
    Def.task[String] {
      GatlingVersion(version.value).map(bump.bump).map(_.string).getOrElse(throw new IllegalStateException("Cannot bump unparsable version"))
    }
  }
}
