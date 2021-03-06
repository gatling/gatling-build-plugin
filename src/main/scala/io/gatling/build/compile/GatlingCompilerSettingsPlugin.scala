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

package io.gatling.build.compile

import sbt._
import sbt.Keys._

object GatlingCompilerSettingsPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  private val isJava8 = scala.util.Properties.javaVersion.startsWith("1.8")

  private val JavacOptions =
    if (isJava8) {
      Seq("-source", "1.8", "-target", "1.8")
    } else {
      Seq("--release", "8")
    }

  private val ScalacOptions = {
    val base =
      Seq(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:implicitConversions",
        "-target:jvm-1.8"
      )

    if (isJava8) {
      base
    } else {
      base ++ Seq("-release", "8")
    }
  }

  override def projectSettings =
    Seq(
      updateOptions := configureUpdateOptions(updateOptions.value),
      javacOptions := JavacOptions,
      Compile / doc / javacOptions := Seq(
        "-source",
        "1.8"
      ),
      resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
      scalacOptions := ScalacOptions
    )

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)
}
