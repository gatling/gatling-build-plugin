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

package io.gatling.build.release

import io.gatling.build.publish.GatlingPublishPlugin

import sbtrelease.ReleasePlugin
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations.{ checkSnapshotDependencies, inquireVersions, runClean, runTest }
import sbtrelease.Utilities._

import sbt._
import sbt.Keys._

object GatlingReleasePlugin extends AutoPlugin {

  override def requires: Plugins = GatlingPublishPlugin && ReleasePlugin

  trait GatlingReleaseKeys {
    lazy val gatlingReleasePublishStep = settingKey[ReleaseStep]("releaseStep to execute")
  }

  object GatlingReleaseKeys extends GatlingReleaseKeys
  object autoImport extends GatlingReleaseKeys

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    releaseCrossBuild := false,
    gatlingReleasePublishStep := publishStep,
    releaseProcess := gatlingReleaseProcess.value,
    releaseVersion := identity
  )

  val publishStep: ReleaseStep = ReleaseStep { state: State =>
    val extracted = Project.extract(state)
    extracted.runAggregated(extracted.currentRef / releasePublishArtifactsAction, state)
  }

  lazy val writeCurrentVersion: ReleaseStep = { st: State =>
    IO.write(st.extract.get(target) / "release-info", st.extract.get(version))
    st
  }

  val gatlingReleaseProcess: Def.Initialize[Seq[ReleaseStep]] = Def.setting {
    Seq(
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      writeCurrentVersion,
      gatlingReleasePublishStep.value
    )
  }
}
