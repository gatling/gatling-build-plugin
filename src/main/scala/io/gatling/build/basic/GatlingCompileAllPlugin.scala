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

package io.gatling.build.basic

import sbt._
import sbt.Keys._

object GatlingCompileAllPlugin extends AutoPlugin {
  override def requires = empty

  override def trigger = allRequirements

  trait GatlingCompileAllKeys {
    val compileAll = taskKey[Unit]("compile all configurations")
  }
  object GatlingCompileAllKeys extends GatlingCompileAllKeys
  object autoImport extends GatlingCompileAllKeys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    compileAll := compile.?.all(
      ScopeFilter(
        configurations = inAnyConfiguration
      )
    ).value
  )
}
