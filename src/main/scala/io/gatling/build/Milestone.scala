package io.gatling.build

import java.text.SimpleDateFormat
import java.util.Date

import sbtrelease.Version

import scala.language.implicitConversions
import scala.util.Try

object Milestone {

  lazy val MilestoneQualifierPrefix = "-M"
  lazy val MilestoneFormatterPattern = "yyyyMMddhhmmss"
  lazy val MilestoneFormatter = new SimpleDateFormat(MilestoneFormatterPattern)

  implicit class MilestoneVersion(version: Version) {
    def isMilestone: Boolean =
      version.qualifier.exists { qualifier =>
        qualifier.length == MilestoneFormatterPattern.length + MilestoneQualifierPrefix.length &&
          qualifier.startsWith(MilestoneQualifierPrefix) &&
          Try(MilestoneFormatter.parse(qualifier.substring(MilestoneQualifierPrefix.length))).isSuccess
      }

    def asMilestone: Version = {
      val qualifier = MilestoneQualifierPrefix + MilestoneFormatter.format(new Date())
      version.copy(qualifier = Some(qualifier))
    }
  }
}
