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

package io.gatling.build.automated

import io.gatling.build.config.GatlingBuildConfigPlugin

import org.scalafmt.sbt.ScalafmtPlugin
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._

import sbt._

object GatlingAutomatedScalafmtPlugin extends AutoPlugin {

  override def requires: Plugins = ScalafmtPlugin && GatlingBuildConfigPlugin

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    scalafmtOnCompile := true
  )

  import GatlingBuildConfigPlugin.GatlingBuildConfigKeys._

  private lazy val scalafmtConfigFileSetting = Def.setting { gatlingBuildConfigDirectory.value / ".scalafmt.conf" }
  private lazy val scalafmtWriteConfigFile = writeResourceOnConfigDirectoryFile(
    path = "/default.scalafmt.conf",
    to = scalafmtConfigFileSetting
  )

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalafmtConfig := scalafmtWriteConfigFile.value
  )
}
