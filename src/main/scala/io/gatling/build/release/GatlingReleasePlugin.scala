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

package io.gatling.build.release

import io.gatling.build.publish.GatlingPublishPlugin

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import sbtrelease.ReleasePlugin
import sbtrelease.ReleasePlugin.autoImport._

import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser

object GatlingReleasePlugin extends AutoPlugin {
  override def requires: Plugins = GatlingPublishPlugin && ReleasePlugin

  trait GatlingReleaseKeys {
    lazy val skipSnapshotDepsCheck = settingKey[Boolean]("Skip snapshot dependencies check during release")
    lazy val gatlingReleasePublishStep = settingKey[ReleaseStep]("releaseStep to execute")
  }

  object GatlingReleaseKeys extends GatlingReleaseKeys
  object autoImport extends GatlingReleaseKeys

  import autoImport._

  private lazy val minorParser: Parser[GatlingReleaseProcess] =
    (Space ~> token("minor")) ^^^ GatlingReleaseProcess.Minor

  private lazy val patchParser: Parser[GatlingReleaseProcess] =
    (Space ~> token("patch")) ^^^ GatlingReleaseProcess.Patch

  private lazy val milestoneParser: Parser[GatlingReleaseProcess] =
    (Space ~> token("milestone")) ^^^ GatlingReleaseProcess.Milestone

  private lazy val releaseProcessParser: Parser[GatlingReleaseProcess] = minorParser | patchParser | milestoneParser

  def gatlingRelease =
    Command("gatling-release", ("gatling-release <minor|patch|milestone>", "release in Gatling way"), "release in Gatling way")(_ => releaseProcessParser) {
      (state, gatlingReleaseProcess) =>
        val extracted = Project.extract(state)
        val stateWithReleaseVersionBump = extracted.appendWithSession(
          Seq(
            releaseVersion := gatlingReleaseProcess.releaseVersion,
            releaseNextVersion := gatlingReleaseProcess.releaseNextVersion,
            releaseProcess := gatlingReleaseProcess.releaseSteps.value
          ),
          state
        )

        Command.process("release with-defaults", stateWithReleaseVersionBump)
    }

  override def projectSettings: Seq[Setting[_]] = Seq(
    skipSnapshotDepsCheck := false,
    releaseCrossBuild := false,
    commands += gatlingRelease,
    gatlingReleasePublishStep := publishStep
  )

  val publishStep: ReleaseStep = ReleaseStep { state: State =>
    val extracted = Project.extract(state)
    extracted.runAggregated(releasePublishArtifactsAction in Global in extracted.currentRef, state)
  }
}
