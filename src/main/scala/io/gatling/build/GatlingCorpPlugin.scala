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

package io.gatling.build

import io.gatling.build.automated.GatlingAutomatedScalafixPlugin
import io.gatling.build.automated.GatlingAutomatedScalafmtPlugin
import io.gatling.build.basic.GatlingBasicInfoPlugin
import io.gatling.build.compile.GatlingCompilerSettingsPlugin
import io.gatling.build.license._
import io.gatling.build.publish.GatlingPublishPlugin
import io.gatling.build.release.GatlingReleasePlugin

import de.heikoseeberger.sbtheader.AutomateHeaderPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction

import sbt._

object GatlingCorpPlugin extends AutoPlugin {
  override def requires =
    GatlingAutomatedScalafixPlugin &&
      GatlingAutomatedScalafmtPlugin &&
      GatlingBasicInfoPlugin &&
      GatlingCompilerSettingsPlugin &&
      GatlingPublishPlugin &&
      GatlingReleasePlugin &&
      AutomateHeaderPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    headerLicense := AllRightsReservedLicense,
    releasePublishArtifactsAction := Keys.publish.value
  )
}
