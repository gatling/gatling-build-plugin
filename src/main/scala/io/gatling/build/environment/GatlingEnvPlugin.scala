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

package io.gatling.build.environment

import java.io.File

import sbt.{ settingKey, taskKey, AutoPlugin, Def }
import sbt.nio.Keys.fileInputs
import sbt.nio.file.Glob

object GatlingEnvPlugin extends AutoPlugin {
  trait GatlingEnvPluginKeys {
    val gatlingEnvFiles = settingKey[Seq[File]]("Environment files to load")
    val gatlingEnvVars = taskKey[Map[String, String]]("Environment variables loaded from environment files")
  }

  object autoImport extends GatlingEnvPluginKeys
  object GatlingEnvPluginKeys extends GatlingEnvPluginKeys

  import autoImport._

  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq(
    gatlingEnvFiles := Seq.empty
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    gatlingEnvVars / fileInputs ++= gatlingEnvFiles.value.map(Glob.apply),
    gatlingEnvVars := gatlingEnvVars.inputFiles
      .map(path => EnvironmentUtils.readEnvFile(path.toFile).fold(throw _, identity))
      .foldLeft(Map.empty[String, String])(_ ++ _)
  )
}
