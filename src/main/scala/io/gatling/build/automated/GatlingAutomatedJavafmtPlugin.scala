/*
 * Copyright 2011-2026 GatlingCorp (https://gatling.io)
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

import com.github.sbt.JavaFormatterPlugin
import com.github.sbt.JavaFormatterPlugin.autoImport._
import com.google.googlejavaformat.java.JavaFormatterOptions

import sbt._

object GatlingAutomatedJavafmtPlugin extends AutoPlugin {
  override def requires: Plugins = JavaFormatterPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    javafmtOnCompile := false, // the one to set to true for each project
    javafmtFormatterCompatibleJavaVersion := 21,
    javafmtStyle := JavaFormatterOptions.Style.GOOGLE,
    javafmtReflowLongStrings := false
  )
}
