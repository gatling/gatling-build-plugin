/*
 * Copyright 2011-2023 GatlingCorp (https://gatling.io)
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

package io.gatling.build.automated

import io.gatling.build.config.GatlingBuildConfigPlugin

import scalafix.sbt.ScalafixPlugin
import scalafix.sbt.ScalafixPlugin.autoImport._

import sbt.{ Def, _ }

object GatlingAutomatedScalafixPlugin extends AutoPlugin {
  override def requires: Plugins = ScalafixPlugin && GatlingBuildConfigPlugin

  trait GatlingAutomatedScalafixKeys {
    val gatlingScalafixConfigFile = settingKey[File]("Location of the scalafix configuration file")
    val gatlingScalafixWriteTask = taskKey[File]("write scalafix file")

    def automateScalafixBeforeCompile(configurations: Configuration*): Seq[Setting[_]] =
      configurations.toSeq.flatMap(
        inConfig(_)(
          Seq(
            scalafix := scalafix.dependsOn(gatlingScalafixWriteTask).evaluated
          )
        )
      )
  }
  object GatlingAutomatedScalafixKeys extends GatlingAutomatedScalafixKeys
  object autoImport extends GatlingAutomatedScalafixKeys

  import GatlingBuildConfigPlugin.GatlingBuildConfigKeys._
  import autoImport._

  override def projectSettings: Seq[sbt.Setting[_]] =
    automateScalafixBeforeCompile(Test, Compile) ++
      Seq(
        scalafixAll := scalafixAll.dependsOn(gatlingScalafixWriteTask).evaluated,
        scalafixOnCompile := !sys.env.getOrElse("CI", "false").toBoolean,
        scalafixConfig := Some(gatlingScalafixConfigFile.value),
        gatlingScalafixConfigFile := gatlingBuildConfigDirectory.value / ".scalafix.conf",
        gatlingScalafixWriteTask := {
          writeResourceOnConfigDirectoryFile(
            path = "/default.scalafix.conf",
            to = gatlingScalafixConfigFile.value
          )
        }
      )

  override def buildSettings: Seq[Def.Setting[_]] =
    ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.6.1"
}
