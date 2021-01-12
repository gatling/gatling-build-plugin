package io.gatling.build

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import sbt.Keys._
import sbt._
import _root_.io.gatling.build.Milestone._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.Version
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys._

object GatlingReleasePlugin extends AutoPlugin {

  import ReleaseProcessKeys._

  override def requires: Plugins = Sonatype

  object autoimport {
    lazy val gatlingReleaseProcessSetting = settingKey[GatlingReleaseProcess]("Gatling release process setting")
    lazy val skipSnapshotDepsCheck = settingKey[Boolean]("Skip snapshot dependencies check during release")
    lazy val isMilestone = settingKey[Boolean]("Indicate if release process is milestone")
  }

  import autoimport._

  private lazy val releaseProcessParser: Parser[GatlingReleaseProcess] =
    Space ~> token(
      "minor"     ^^^ GatlingReleaseProcess.Minor |
      "patch"     ^^^ GatlingReleaseProcess.Patch |
      "milestone" ^^^ GatlingReleaseProcess.Milestone,
      description = "minor|patch|milestone"
    )

  def gatlingRelease = Command("gatling-release")(_ => releaseProcessParser) { (state, releaseProcess) =>
    val extracted = Project.extract(state)
    val stateWithReleaseVersionBump = extracted.appendWithSession(Seq(
      releaseVersionBump := releaseProcess.bump.getOrElse(releaseVersionBump.value),
      gatlingReleaseProcessSetting := releaseProcess
    ), state)

    Command.process("release with-defaults", stateWithReleaseVersionBump)
  }

  override def projectSettings: Seq[Setting[_]] = gatlingReleaseSettings ++ Seq(
    releaseCrossBuild := false,
    releasePublishArtifactsAction := publishSigned.value,
    sonatypeProfileName := "io.gatling",
    isMilestone := version(Version(_).exists(_.isMilestone)).value,
    commands += gatlingRelease
  )
}
