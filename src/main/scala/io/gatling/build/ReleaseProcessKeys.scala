package io.gatling.build

import scala.util.Properties

import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

object ReleaseProcessKeys {
  val skipSnapshotDepsCheck = settingKey[Boolean]("Skip snapshot dependencies check during release")

  val gatlingReleaseSettings = Seq(
    skipSnapshotDepsCheck := false,
    releaseVersion := propToVersionFunOrDefault("releaseVersion", releaseVersion.value),
    releaseNextVersion := propToVersionFunOrDefault("developmentVersion", releaseNextVersion.value),
    releaseProcess := fullReleaseProcess(skipSnapshotDepsCheck.value, publishMavenStyle.value)
  )

  private def fullReleaseProcess(skipSnapshotDepsCheck: Boolean, releaseOnSonatype: Boolean) = {
    val checkSnapshotDeps = if (!skipSnapshotDepsCheck) Seq(checkSnapshotDependencies) else Seq.empty
    val publishStep = ReleaseStep(action = Command.process("publishSigned", _))
    val sonatypeRelease = if (releaseOnSonatype) Seq(ReleaseStep(Command.process("sonatypeReleaseAll", _))) else Seq.empty
    val commonProcess = Seq(
      inquireVersions, runClean, runTest, setReleaseVersion, commitReleaseVersion,
      tagRelease, publishStep, setNextVersion, commitNextVersion, pushChanges
    )

    checkSnapshotDeps ++ commonProcess ++ sonatypeRelease
  }

  private def propToVersionFunOrDefault(propName: String, default: String => String) =
    Properties.propOrNone(propName).map(s => { (_: String) => s }) getOrElse default
}
