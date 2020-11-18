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

package io.gatling.build.basic

import sbt._
import sbt.Keys._

object GatlingBasicInfoPlugin extends AutoPlugin {
  override def requires = empty

  override def trigger = allRequirements

  trait GatlingBasicInfoKeys {
    val githubPath = settingKey[String]("Project path on Github")
    val gatlingDevelopers = settingKey[Seq[GatlingDeveloper]]("List of contributors for this project")

    case class GatlingDeveloper(emailAddress: String, name: String, isGatlingCorp: Boolean)
  }
  object GatlingBasicInfoKeys extends GatlingBasicInfoKeys
  object autoImport extends GatlingBasicInfoKeys

  import autoImport._

  override def projectSettings =
    Seq(
      homepage := Some(url("https://gatling.io")),
      organization := "io.gatling",
      organizationName := "Gatling Corp",
      organizationHomepage := Some(url("https://gatling.io")),
      scmInfo := githubPath.?.value.map(githubPath =>
        ScmInfo(
          url(s"https://github.com/$githubPath"),
          s"scm:git:https://github.com/$githubPath.git",
          s"scm:git:git@github.com/$githubPath.git"
        )
      ),
      startYear := Some(2011),
      pomExtra := developersXml(gatlingDevelopers.value)
    )

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    gatlingDevelopers := Seq.empty
  )

  private def developersXml(devs: Seq[GatlingDeveloper]) = {
    <developers>
      {
      for (dev <- devs) yield {
        <developer>
          <id>{dev.emailAddress}</id>
          <name>{dev.name}</name>
          {
          if (dev.isGatlingCorp) {
            <organization>Gatling Corp</organization>
            <organizationUrl>https://gatling.io</organizationUrl>
          }
        }
        </developer>
      }
    }
    </developers>
  }

}
