/*
 * Copyright 2011-2025 GatlingCorp (https://gatling.io)
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

import io.gatling.build.automated.{ GatlingAutomatedScalafixPlugin, GatlingAutomatedScalafmtPlugin }
import io.gatling.build.basic.GatlingCompileAllPlugin
import io.gatling.build.compile.GatlingCompilerSettingsPlugin
import io.gatling.build.environment.GatlingEnvPlugin
import io.gatling.build.license._
import io.gatling.build.sonatype.GatlingSonatypePlugin
import io.gatling.build.versioning.GatlingVersioningPlugin

import sbtheader.AutomateHeaderPlugin
import sbtheader.HeaderPlugin.autoImport.headerLicense

import sbt._
import sbt.Keys._

/**
 * Used for external components for Gatling Enterprise (closed source components published under the Gatling Enterprise Components License).
 */
object GatlingEnterpriseComponentPlugin extends AutoPlugin {
  override def requires =
    GatlingAutomatedScalafixPlugin &&
      GatlingAutomatedScalafmtPlugin &&
      GatlingVersioningPlugin &&
      GatlingCompilerSettingsPlugin &&
      GatlingCompileAllPlugin &&
      AutomateHeaderPlugin &&
      GatlingEnvPlugin &&
      GatlingEnterpriseComponentLicenseFilePlugin &&
      GatlingSonatypePlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    licenses := Seq("Gatling Enterprise Component License" -> url("https://docs.gatling.io/project/licenses/enterprise-component/")),
    headerLicense := AllRightsReservedLicense
  )
}
