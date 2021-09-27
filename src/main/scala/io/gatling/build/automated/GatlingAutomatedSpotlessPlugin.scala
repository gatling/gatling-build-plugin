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

import net.moznion.sbt.SbtSpotless
import net.moznion.sbt.SbtSpotless.autoImport.{ spotless, spotlessJava, spotlessKotlin }
import net.moznion.sbt.spotless.config.{ GoogleJavaFormatConfig, JavaConfig, KotlinConfig, SpotlessConfig }

import sbt.{ AutoPlugin, Def, Plugins }

object GatlingAutomatedSpotlessPlugin extends AutoPlugin {
  override def requires: Plugins = SbtSpotless

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    spotless := SpotlessConfig(
      applyOnCompile = !sys.env.getOrElse("CI", "false").toBoolean
    ),
    spotlessJava := JavaConfig(
      googleJavaFormat = GoogleJavaFormatConfig()
    ),
    spotlessKotlin := KotlinConfig()
  )
}
