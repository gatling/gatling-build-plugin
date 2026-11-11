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

package io.gatling.build.config

import java.nio.file.{ Files, StandardCopyOption }

import sbt.{ Def, _ }
import sbt.Keys._

object GatlingBuildConfigPlugin extends AutoPlugin {
  override def requires = empty

  trait GatlingBuildConfigKeys {
    val gatlingBuildConfigDirectory =
      settingKey[File]("Location where to put configuration from gatling-build-plugin. Defaults to target/gatling-build-config in the build's root directory")

    /**
     * Copies a classpath resource to `to`, only if `to` doesn't already have the same content.
     *
     * Leaving an up-to-date file untouched keeps its last modified time, so tools that cache their configuration per file and timestamp (such as sbt-scalafmt)
     * don't reload it on every run. The write goes through a temporary file and an atomic move, as projects of the same build write the same file concurrently.
     */
    def writeResourceOnConfigDirectoryFile(path: String, to: File): File = {
      val resourceInputStream = getClass.getResourceAsStream(path)
      val content =
        try IO.readBytes(resourceInputStream)
        finally resourceInputStream.close()
      if (!to.isFile || !java.util.Arrays.equals(IO.readBytes(to), content)) {
        IO.createDirectory(to.getParentFile)
        val tmp = Files.createTempFile(to.getParentFile.toPath, to.getName, ".tmp")
        try {
          Files.write(tmp, content)
          Files.move(tmp, to.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } finally Files.deleteIfExists(tmp)
      }
      to
    }
  }
  object GatlingBuildConfigKeys extends GatlingBuildConfigKeys

  import GatlingBuildConfigKeys._

  // One directory for the whole build rather than one per project: sbt-scalafmt keeps a scalafmt-core
  // class loader per configuration file path, so one file per project costs one class loader (and
  // ~10 MB of metaspace) per project in a long-lived sbt server.
  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    gatlingBuildConfigDirectory := (ThisBuild / baseDirectory).value / "target" / "gatling-build-config"
  )
}
