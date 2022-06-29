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

import java.time.{ Clock, ZoneId, ZonedDateTime }
import java.time.temporal.WeekFields

object GatlingBump {

  object Minor extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion)(implicit clock: Clock): GatlingVersion =
      if (gatlingVersion.qualifier.isDefined && gatlingVersion.patch == 0)
        gatlingVersion.withoutQualifier
      else
        gatlingVersion.bumpMinor.withoutQualifier
  }

  object Patch extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion)(implicit clock: Clock): GatlingVersion =
      if (gatlingVersion.qualifier.isDefined)
        gatlingVersion.withoutQualifier
      else gatlingVersion.bumpPatch
  }

  object CalVer extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion)(implicit clock: Clock): GatlingVersion = {
      val year = ZonedDateTime.ofInstant(clock.instant(), ZoneId.of("UTC")).getYear
      val week = ZonedDateTime.ofInstant(clock.instant(), ZoneId.of("UTC")).get(WeekFields.ISO.weekOfYear())

      if (gatlingVersion.major == year && gatlingVersion.minor == week) {
        Patch.bump(gatlingVersion)
      } else {
        GatlingVersion(year, week, 0, gatlingVersion.marker, None)
      }
    }
  }

  def milestone(original: GatlingBump): GatlingBump = new GatlingBump {
    override def bump(gatlingVersion: GatlingVersion)(implicit clock: Clock): GatlingVersion = original.bump(gatlingVersion).asMilestone
  }
}

trait GatlingBump {
  def bump(gatlingVersion: GatlingVersion)(implicit clock: Clock): GatlingVersion
}
