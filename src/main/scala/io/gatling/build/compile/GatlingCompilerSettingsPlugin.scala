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

  trait GatlingCompilerSettingsKeys {
    val javaReleaseVersion = settingKey[String]("Java release releaseVersion")
  }

  object GatlingCompilerSettingsKeys extends GatlingCompilerSettingsKeys
  object autoImport extends GatlingCompilerSettingsKeys

  import autoImport._

  private val DefaultJavaReleaseVersion = "1.8"
  private val isJava8 = scala.util.Properties.javaVersion.startsWith("1.8")

  private val JavacOptions = Def.setting {
    val version = javaReleaseVersion.value
    if (version == DefaultJavaReleaseVersion) {
      if (isJava8) {
        Seq("-source", "1.8", "-target", "1.8")
      } else {
        Seq("--release", "1.8")
      }
    } else {
      // When user overwrite java release version, we leave him the responsibility
      Seq.empty
    }
  }

  private val ScalacOptions = Def.setting {
    val releaseVersion = javaReleaseVersion.value
    val base =
      Seq(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:implicitConversions"
      )

    if (releaseVersion != DefaultJavaReleaseVersion) {
      // When user overwrite java release version, we leave him the responsibility
      base
    } else {
      val baseWithTarget = base :+ "-target:jvm-1.8"
      if (isJava8) {
        baseWithTarget
      } else {
        baseWithTarget ++ Seq("-release", "8")
      }
    }
  }

  override def projectSettings =
    Seq(
      updateOptions := configureUpdateOptions(updateOptions.value),
      javacOptions := JavacOptions.value,
      Compile / doc / javacOptions := Seq(
        "-source",
        "1.8"
      ),
      resolvers := Seq(DefaultMavenRepository),
      scalacOptions := ScalacOptions.value,
      javaReleaseVersion := DefaultJavaReleaseVersion
    )

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)
}
