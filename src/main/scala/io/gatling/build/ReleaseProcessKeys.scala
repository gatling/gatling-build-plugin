package io.gatling.build

import scala.util.Properties

import io.gatling.build.MavenPublishKeys.pushToPrivateNexus

import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype.SonatypeCommand.sonatypeReleaseAll

object ReleaseProcessKeys {

  val skipSnapshotDepsCheck = settingKey[Boolean]("Skip snapshot dependencies check during release")

  val gatlingReleaseSettings = Seq(
    skipSnapshotDepsCheck := false,
    releaseVersion := propToVersionFunOrDefault("releaseVersion", releaseVersion.value),
    releaseNextVersion := propToVersionFunOrDefault("developmentVersion", releaseNextVersion.value),
    releaseProcess := {
      val releaseOnSonatype = publishMavenStyle.value && !(pushToPrivateNexus ?? false).value
      fullReleaseProcess(thisProjectRef.value, skipSnapshotDepsCheck.value, releaseOnSonatype)
    }
  )

  private def fullReleaseProcess(ref: ProjectRef, skipSnapshotDepsCheck: Boolean, releaseOnSonatype: Boolean) = {
    val checkSnapshotDeps = if (!skipSnapshotDepsCheck) Seq(checkSnapshotDependencies) else Seq.empty
    val publishStep = ReleaseStep(releaseStepTaskAggregated(releasePublishArtifactsAction in Global in ref))
    val sonatypeRelease = if (releaseOnSonatype) Seq(ReleaseStep(releaseStepCommand(sonatypeReleaseAll))) else Seq.empty
    val commonProcess = Seq(
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

    checkSnapshotDeps ++ commonProcess ++ sonatypeRelease
  }

  private def propToVersionFunOrDefault(propName: String, default: String => String) =
    Properties.propOrNone(propName).map(Function.const) getOrElse default
}
