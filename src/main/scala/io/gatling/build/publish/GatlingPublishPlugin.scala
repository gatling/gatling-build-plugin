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

package io.gatling.build.publish

import sbt._
import sbt.Keys._

object GatlingPublishPlugin extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  // A check must run on every call: never cached by sbt 2
  @transient val gatlingCheckSnapshotDependencies = taskKey[Unit]("Fails if a runtime dependency has a SNAPSHOT or changing version")

  override def projectSettings: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    crossPaths := false,
    resolvers += Resolver.mavenLocal,
    // Maven Central requires a javadoc jar but its content is useless: publish an empty one and skip doc generation
    Compile / packageDoc / mappings := Seq.empty,
    gatlingCheckSnapshotDependencies := {
      val snapshots = SnapshotDependenciesCompat.runtimeModuleIds.value.filter(isSnapshotDependency)
      if (snapshots.nonEmpty) {
        sys.error(s"Snapshot dependencies detected in ${thisProject.value.id}:\n${snapshots.mkString("\n")}")
      }
    }
  )

  // Same rule as sbt-release's releaseSnapshotDependencies
  private def isSnapshotDependency(module: ModuleID): Boolean =
    module.isChanging || module.revision.endsWith("-SNAPSHOT")
}
