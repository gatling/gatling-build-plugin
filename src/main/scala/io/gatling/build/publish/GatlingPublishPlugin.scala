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

package io.gatling.build.publish

import scala.util.Properties.{ envOrNone, propOrNone }

import sbt._
import sbt.Keys._

object GatlingPublishPlugin extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  trait GatlingPublishKeys {
    val gatlingPublishAddSonatypeResolvers = settingKey[Boolean]("Use Sonatype repositories for CI or during release process")
    val isMilestone = settingKey[Boolean]("Indicate if release process is milestone")

    type GatlingVersion = _root_.io.gatling.build.publish.GatlingVersion
  }

  object GatlingPublishKeys extends GatlingPublishKeys
  object autoImport extends GatlingPublishKeys

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    gatlingPublishAddSonatypeResolvers := false,
    crossPaths := false,
    isMilestone := version(GatlingVersion(_).exists(_.isMilestone)).value,
    resolvers ++= (if (gatlingPublishAddSonatypeResolvers.value) sonatypeRepositories else Seq.empty) :+ Resolver.mavenLocal
  )

  private def sonatypeRepositories: Seq[Resolver] =
    Seq(
      envOrNone("CI").map(_ => Opts.resolver.sonatypeSnapshots),
      propOrNone("release").map(_ => Opts.resolver.sonatypeReleases)
    ).flatten
}
