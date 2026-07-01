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

package io.gatling.build.sonatype

import io.gatling.build.basic.GatlingBasicInfoPlugin
import io.gatling.build.publish.GatlingPublishPlugin
import io.gatling.build.release.GatlingReleasePlugin
import io.gatling.build.release.GatlingReleasePlugin.GatlingReleaseKeys._

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import sbtrelease.ReleasePlugin.autoImport._

import sbt._
import sbt.Keys._

object GatlingSonatypePlugin extends AutoPlugin {
  override def requires: Plugins =
    GatlingBasicInfoPlugin &&
      GatlingReleasePlugin &&
      GatlingPublishPlugin

  trait GatlingSonatypeKeys {
    val gatlingPublishToSonatype = settingKey[Boolean]("true to publish to sonatype central")
  }
  object GatlingSonatypeKeys extends GatlingSonatypeKeys
  object autoImport extends GatlingSonatypeKeys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    gatlingPublishToSonatype := ensurePublishableVersion(version.value),
    publishTo := {
      if (gatlingPublishToSonatype.value) localStaging.value
      else publishTo.value
    },
    releasePublishArtifactsAction := {
      if (gatlingPublishToSonatype.value) publishSigned.value
      else Keys.publish.value
    },
    gatlingReleasePublishStep := conditionalPublishStep
  )

  val conditionalPublishStep: ReleaseStep = { (state: State) =>
    val extractedState = Project.extract(state)
    if (extractedState.get(gatlingPublishToSonatype)) publishStep(state)
    else GatlingReleasePlugin.publishStep(state)
  }

  val publishStep: ReleaseStep = { (state: State) =>
    state.log.info("compile, package, sign and publish")
    // releasePublishArtifactsAction must run once per scalaVersion in crossScalaVersions, so it gets
    // published for both sbt 1.x (Scala 2.12) and sbt 2.x (Scala 3), per pluginCrossBuild / sbtVersion.
    val endState = releaseStepCommandAndRemaining(s"+${releasePublishArtifactsAction.key.label}")(state)
    state.log.info("upload and release to Sonatype Central")
    releaseStepCommandAndRemaining("sonaRelease")(endState)
    endState
  }

  def ensurePublishableVersion(str: String): Boolean = str.matches("""\d+\.\d+\.\d+(\.\d+)?(-M\d+)?""")
}
