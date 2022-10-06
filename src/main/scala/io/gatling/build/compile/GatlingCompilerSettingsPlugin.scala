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

package io.gatling.build.compile

import sbt._
import sbt.Keys._

object GatlingCompilerSettingsPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  trait GatlingCompilerSettingsKey {
    val gatlingCompilerRelease = settingKey[Option[Int]]("target release option")
  }

  object GatlingCompilerSettingsKey extends GatlingCompilerSettingsKey
  object autoImport extends GatlingCompilerSettingsKey

  import autoImport._

  private val isJava8 = scala.util.Properties.javaVersion.startsWith("1.8")

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      updateOptions := configureUpdateOptions(updateOptions.value),
      javacOptions := {
        if (isJava8) {
          Seq("-source", "1.8", "-target", "1.8")
        } else {
          gatlingCompilerRelease.value.toList.flatMap(v => List("--release", v.toString))
        }
      },
      Compile / doc / javacOptions := Seq(
        "-source",
        "1.8"
      ),
      resolvers := Seq(DefaultMavenRepository),
      gatlingCompilerRelease := Some(8).filterNot(_ => isJava8),
      scalacOptions := Seq(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:implicitConversions"
      ) ++ gatlingCompilerRelease.value.toList.flatMap(v => List("-release", v.toString))
    )

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)
}
