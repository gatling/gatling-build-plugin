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

package io.gatling.build.config

import sbt._
import sbt.Keys._

object GatlingBuildConfigPlugin extends AutoPlugin {
  override def requires = empty

  trait GatlingBuildConfigKeys {
    val gatlingBuildConfigDirectory = settingKey[File]("Location where to put configuration from gatling-build-plugin. Defaults to target/gatling-build-config")

    def writeResourceOnConfigDirectoryFile(path: String, to: Def.Initialize[File]): Def.Initialize[Task[File]] = Def.task {
      val resourceInputStream = getClass.getResourceAsStream(path)
      val file = to.value
      IO.transfer(resourceInputStream, file)
      file
    }
  }
  object GatlingBuildConfigKeys extends GatlingBuildConfigKeys

  import GatlingBuildConfigKeys._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    gatlingBuildConfigDirectory := target.value / "gatling-build-config"
  )
}
