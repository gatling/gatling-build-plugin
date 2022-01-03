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

import java.time.{ ZoneOffset, ZonedDateTime }
import java.time.temporal.WeekFields

object GatlingBump {
  object Minor extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion): GatlingVersion = gatlingVersion.bumpMinor.withoutQualifier
  }
  object Patch extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion): GatlingVersion = gatlingVersion.bumpPatch.withoutQualifier
  }
  object CalVer extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion): GatlingVersion = {
      val now = ZonedDateTime.now(ZoneOffset.UTC)
      val year = now.getYear
      val week = now.get(WeekFields.ISO.weekOfYear())

      if (gatlingVersion.major == year && gatlingVersion.minor == week) {
        gatlingVersion.bumpPatch.withoutQualifier
      } else {
        GatlingVersion(year, week, 0, gatlingVersion.marker, None)
      }
    }
  }
  object Milestone extends GatlingBump {
    override def bump(gatlingVersion: GatlingVersion): GatlingVersion = gatlingVersion.asMilestone
  }
}

trait GatlingBump {
  def bump(gatlingVersion: GatlingVersion): GatlingVersion
}
