package io.gatling.build

import _root_.io.gatling.build.GatlingReleasePlugin.autoimport._
import _root_.io.gatling.build.Milestone._
import io.gatling.build.MavenPublishKeys.pushToPrivateNexus
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseKeys._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.Utilities._
import sbtrelease.Version.Bump
import sbtrelease.{Git, Version, versionFormatError}
import xerial.sbt.Sonatype.SonatypeCommand.sonatypeReleaseAll

import scala.sys.process.ProcessLogger

object ReleaseProcessKeys {

  def stdErrorToStdOut(delegate: ProcessLogger): ProcessLogger = new ProcessLogger {
    override def out(s: => String): Unit = delegate.out(s)
    override def err(s: => String): Unit = delegate.out(s)
    override def buffer[T](f: => T): T = delegate.buffer(f)
  }

  sealed trait GatlingReleaseProcess {
    def bump: Option[Bump]
  }
  object GatlingReleaseProcess {
    case object Minor extends GatlingReleaseProcess {
      override def bump: Option[Bump] = Some(Bump.Minor)
      override def toString: String = "minor"
    }
    case object Patch extends GatlingReleaseProcess {
      override def bump: Option[Bump] = Some(Bump.Bugfix)
      override def toString: String = "patch"
    }
    case object Milestone extends GatlingReleaseProcess {
      override def bump: Option[Bump] = None
      override def toString: String = "milestone"
    }
  }

  val gatlingReleaseSettings = Seq(
    skipSnapshotDepsCheck := false,
    gatlingReleaseProcessSetting := GatlingReleaseProcess.Patch,
    pushToPrivateNexus := isMilestone.value || pushToPrivateNexus.value,
    releaseProcess := {
      val releaseOnSonatype = publishMavenStyle.value && !(pushToPrivateNexus ?? false).value
      fullReleaseProcess(
        thisProjectRef.value,
        skipSnapshotDepsCheck.value,
        releaseOnSonatype,
        gatlingReleaseProcessSetting.value)
    }
  )

  private def extractGitVcs(st: State): Git = {
    val vcs = st.extract.get(releaseVcs)
      .getOrElse(sys.error("Aborting release. Working directory is not a repository of a recognized VCS."))

    vcs match {
      case git: Git => git
      case _ => sys.error("Aborting release. VCS is not a Git repository")
    }
  }

  private lazy val createBugfixBranch: ReleaseStep = { st: State =>
    val git = extractGitVcs(st)
    val masterVersions = st.get(versions).getOrElse(sys.error("Versions should have already been inquired"))
    val extracted = st.extract

    val rawModuleVersion = extracted.get(version)
    val moduleVersion = Version(rawModuleVersion).getOrElse(versionFormatError(rawModuleVersion))
    val branchName = moduleVersion.withoutQualifier.string

    val minorBugFixBranchState = extracted.appendWithSession(Seq(
      releaseVersionBump := Bump.Bugfix
    ), st)
    val inquireBugFixState = inquireVersions.action(minorBugFixBranchState)
    val bugFixState = setNextVersion.action(inquireBugFixState)

    val gitLog = stdErrorToStdOut(st.log) // Git outputs to standard error, so use a logger that redirects stderr to info
    val originBranch = git.currentBranch
    git.cmd("branch", branchName) ! gitLog
    git.cmd("checkout", branchName) ! gitLog

    val committedBugFixState = commitNextVersion.action(bugFixState)

    git.cmd("push", "--set-upstream" , "origin", s"$branchName:$branchName") ! gitLog

    git.cmd("checkout", originBranch) ! gitLog
    committedBugFixState.put(versions, masterVersions)
  }

  private lazy val pushTagAndReset: ReleaseStep = { st: State =>
    val git = extractGitVcs(st)
    val gitLog = stdErrorToStdOut(st.log) // Git outputs to standard error, so use a logger that redirects stderr to info
    val (releaseTagNameState, tagName) = st.extract.runTask(releaseTagName, st)
    git.cmd("push", "origin", tagName) ! gitLog
    git.cmd("reset", "--hard", s"origin/${git.currentBranch}") ! gitLog
    releaseTagNameState
  }

  private lazy val setMilestoneReleaseVersion: ReleaseStep = { st: State =>
    st.extract.appendWithSession(Seq(
      releaseVersion := { rawCurrentVersion: String =>
        val currentVersion = Version(rawCurrentVersion).getOrElse(sys.error(s"Invalid version format ($rawCurrentVersion)"))
        currentVersion.asMilestone.string
      },
    ), st)
  }

  private def checkVersionStep(validate: Version => Boolean, error: Version => String): ReleaseStep =
    ReleaseStep(identity, { st: State =>
      val rawCurrentVersion = st.extract.get(version)
      val currentVersion = Version(rawCurrentVersion).getOrElse(sys.error(s"Invalid version format $rawCurrentVersion"))
      if (validate(currentVersion)) {
        sys.error(error(currentVersion))
      }
      st
    })

  private def fullReleaseProcess(
    ref: ProjectRef,
    skipSnapshotDepsCheck: Boolean,
    releaseOnSonatype: Boolean,
    gatlingReleaseProcess: GatlingReleaseProcess
  ): Seq[ReleaseStep] = {
    val checkSnapshotDeps = if (!skipSnapshotDepsCheck) Seq(checkSnapshotDependencies) else Seq.empty
    val publishStep = ReleaseStep(releaseStepTaskAggregated(releasePublishArtifactsAction in Global in ref))
    val sonatypeRelease = if (releaseOnSonatype) Seq(ReleaseStep(releaseStepCommand(sonatypeReleaseAll))) else Seq.empty


    val inquireTagPublish = Seq(
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishStep,
    )

    val commonProcess = gatlingReleaseProcess match {
      case GatlingReleaseProcess.Minor =>
        val checkVersion = checkVersionStep(
          version => version.subversions.isDefinedAt(1) && version.subversions(1) != 0,
          version => s"Cannot release a minor version when current version is patch (${version.string})"
        )

        (checkVersion +: inquireTagPublish) ++ Seq(
          pushChanges,
          createBugfixBranch,
          setNextVersion,
          commitNextVersion,
          pushChanges
        )
      case GatlingReleaseProcess.Patch =>
        val checkVersion = checkVersionStep(
          version => version.subversions.isDefinedAt(1) && version.subversions(1) == 0,
          version => s"Cannot release a patch version when current version is minor (${version.string})"
        )

        (checkVersion +: inquireTagPublish) ++ Seq(
          pushChanges,
          setNextVersion,
          commitNextVersion,
          pushChanges
        )
      case GatlingReleaseProcess.Milestone =>
        (setMilestoneReleaseVersion +: inquireTagPublish) ++ Seq(
          pushTagAndReset
        )
    }

    checkSnapshotDeps ++ commonProcess ++ sonatypeRelease
  }
}
