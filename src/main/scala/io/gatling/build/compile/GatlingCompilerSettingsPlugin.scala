/*
 * Copyright 2011-2024 GatlingCorp (https://gatling.io)
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

package io.gatling.build.compile

import sbt._
import sbt.Keys._

object GatlingCompilerSettingsPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  trait GatlingCompilerSettingsKey {
    val gatlingCompilerRelease = settingKey[Int]("target release option")
  }

  object GatlingCompilerSettingsKey extends GatlingCompilerSettingsKey
  object autoImport extends GatlingCompilerSettingsKey

  import autoImport._

  private def validReleaseOption(value: Int): String = {
    require(value >= 11, "gatlingCompilerRelease must be >= 11")
    value.toString
  }

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      updateOptions := configureUpdateOptions(updateOptions.value),
      javacOptions := Seq("--release", validReleaseOption(gatlingCompilerRelease.value)),
      Compile / doc / javacOptions := Seq("--release", validReleaseOption(gatlingCompilerRelease.value)),
      resolvers := Seq(DefaultMavenRepository),
      gatlingCompilerRelease := 11,
      scalacOptions := Seq(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:implicitConversions",
        "-release",
        validReleaseOption(gatlingCompilerRelease.value)
      )
    )

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)
}
