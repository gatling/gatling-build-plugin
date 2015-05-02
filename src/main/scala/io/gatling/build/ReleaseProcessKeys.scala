package io.gatling.build

import scala.util.Properties._

import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseVersion
import sbtrelease.ReleaseStep
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._

object ReleaseProcessKeys {
  val skipSnapshotDepsCheck = settingKey[Boolean]("Skip snapshot dependencies chech during release")

  val gatlingReleaseSettings = releaseSettings ++ Seq(
    skipSnapshotDepsCheck := false,
    releaseVersion := { _ => propOrEmpty("releaseVersion") },
    nextVersion := { _ => propOrEmpty("developmentVersion") },
    releaseProcess := (if (skipSnapshotDepsCheck.value) noSnapshotsCheckReleaseProcess else releaseProcess.value)
  )

  private def noSnapshotsCheckReleaseProcess = {
    val publishStep = ReleaseStep(
      action = st => {
        val extracted = Project.extract(st)
        val ref = extracted.get(thisProjectRef)
        extracted.runAggregated(publishArtifactsAction in Global in ref, st)
      })

    Seq[ReleaseStep](
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishStep,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  }
}
