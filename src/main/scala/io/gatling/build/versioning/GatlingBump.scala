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
