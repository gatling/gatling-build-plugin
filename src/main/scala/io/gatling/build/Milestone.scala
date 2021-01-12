package io.gatling.build

import java.text.SimpleDateFormat
import java.util.Date

import sbtrelease.Version

import scala.language.implicitConversions
import scala.util.Try

object Milestone {

  lazy val milestoneQualifierPrefix = "-M"
  lazy val milestoneFormatterPattern = "yyyyMMddhhmmss"
  lazy val milestoneFormatter = new SimpleDateFormat(milestoneFormatterPattern)

  class MilestoneVersion(version: Version) {
    def isMilestone: Boolean =
      version.qualifier.exists { qualifier =>
        qualifier.length == milestoneFormatterPattern.length + milestoneQualifierPrefix.length &&
          qualifier.startsWith(milestoneQualifierPrefix) &&
          Try(milestoneFormatter.parse(qualifier.substring(2))).isSuccess
      }

    def asMilestone: Version = {
      val qualifier = milestoneQualifierPrefix + milestoneFormatter.format(new Date())
      version.copy(qualifier = Some(qualifier))
    }
  }

  implicit def milestoneVersion(version: Version): MilestoneVersion =
    new MilestoneVersion(version)
}
