/*
 * Copyright 2011-2022 GatlingCorp (https://gatling.io)
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

package io.gatling.build.license

import java.nio.charset.Charset

import sbt.*
import sbt.Keys.*

object Apache2LicenseFilePlugin extends AutoPlugin {
  trait Apache2LicenseFileKeys {
    val gatlingApache2LicenseTask = taskKey[Seq[File]]("Copy Apache2 License to managed resources META-INF/LICENSE")
  }

  object autoImport extends Apache2LicenseFileKeys
  object Apache2LicenseFileKeys extends Apache2LicenseFileKeys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    Compile / gatlingApache2LicenseTask := {
      val file = (Compile / resourceManaged).value / "META-INF" / "LICENSE"
      val contents = IO.readStream(getClass.getClassLoader.getResourceAsStream("Apache2.license"), Charset.forName("UTF-8"))
      IO.write(file, contents)
      Seq(file)
    },
    Compile / resourceGenerators += (Compile / gatlingApache2LicenseTask).taskValue
  )
}
