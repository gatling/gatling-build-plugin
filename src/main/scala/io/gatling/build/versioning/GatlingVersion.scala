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

package io.gatling.build.versioning

import java.time.{ Clock, LocalDateTime, ZoneOffset }
import java.time.format.DateTimeFormatter

import scala.util.Try

object GatlingVersion {

  private[GatlingVersion] lazy val MilestoneFormatterPattern = "'-M'yyyyMMddHHmmss"
  // def as SimpleDateFormat is not Thread safe
  private[GatlingVersion] def milestoneFormatter = DateTimeFormatter.ofPattern(MilestoneFormatterPattern)
  private[this] val GatlingVersionR = "(\\d+)\\.(\\d+)\\.(\\d+)(\\..*?)?(-.*)?".r

  def apply(str: String): Option[GatlingVersion] =
    str match {
      case GatlingVersionR(major, minor, patch, marker, qualifier) =>
        Some(GatlingVersion(major.toInt, minor.toInt, patch.toInt, Option(marker).filterNot(_.isEmpty), Option(qualifier).filterNot(_.isEmpty)))
      case _ => None
    }
}

case class GatlingVersion(major: Int, minor: Int, patch: Int, marker: Option[String] = None, qualifier: Option[String] = None) {

  import GatlingVersion._

  def isMilestone: Boolean = qualifier.exists(qual => Try(milestoneFormatter.parse(qual)).isSuccess)

  def asMilestone(implicit clock: Clock): GatlingVersion =
    copy(qualifier = Some(milestoneFormatter.format(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC))))

  def isSnapshot: Boolean = qualifier.contains("-SNAPSHOT")

  def asSnapshot: GatlingVersion = copy(qualifier = Some("-SNAPSHOT"))

  def isMinor: Boolean = patch == 0

  def isPatch: Boolean = patch != 0

  def branchName: String = s"$major.$minor"

  def withoutQualifier: GatlingVersion = copy(qualifier = None)

  def string: String = s"$major.$minor.$patch" + marker.getOrElse("") + qualifier.getOrElse("")

  def bumpMinor: GatlingVersion = copy(minor = minor + 1, patch = 0)

  def bumpPatch: GatlingVersion = copy(patch = patch + 1)
}
