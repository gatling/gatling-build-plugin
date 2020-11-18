/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
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

package io.gatling.build.release

import io.gatling.build.release.GatlingReleaseStep._

import GatlingReleasePlugin.GatlingReleaseKeys._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

import sbt._

sealed trait GatlingReleaseProcess {
  val releaseSteps: Def.Initialize[Seq[ReleaseStep]]
  val releaseVersion: String => String = gatlingVersion(_.withoutQualifier)
  val releaseNextVersion: String => String
}

object GatlingReleaseProcess {
  private def checkSnapshotDeps = Def.setting {
    if (!skipSnapshotDepsCheck.value) checkSnapshotDependencies else noop
  }

  case object Minor extends GatlingReleaseProcess {
    override val releaseNextVersion: String => String = gatlingVersion(_.bumpMinor.asSnapshot)

    override def toString: String = "minor"

    override val releaseSteps: Def.Initialize[Seq[ReleaseStep]] = Def.setting {
      Seq(
        checkSnapshotDeps.value,
        checkMinorVersion,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        gatlingReleasePublishStep.value,
        writeCurrentVersion,
        pushChanges,
        createBugfixBranch,
        setNextVersion,
        commitNextVersion,
        pushChanges
      )
    }
  }

  case object Patch extends GatlingReleaseProcess {
    override val releaseNextVersion: String => String = gatlingVersion(_.bumpPatch.asSnapshot)

    override def toString: String = "patch"

    override val releaseSteps: Def.Initialize[Seq[ReleaseStep]] = Def.setting {
      Seq(
        checkSnapshotDeps.value,
        checkPatchVersion,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        gatlingReleasePublishStep.value,
        writeCurrentVersion,
        pushChanges,
        setNextVersion,
        commitNextVersion,
        pushChanges
      )
    }
  }

  case object Milestone extends GatlingReleaseProcess {
    override val releaseVersion: String => String = gatlingVersion(_.asMilestone)
    override val releaseNextVersion: String => String = gatlingVersion(_.asSnapshot)
    override def toString: String = "milestone"

    override val releaseSteps: Def.Initialize[Seq[ReleaseStep]] = Def.setting {
      Seq(
        checkSnapshotDeps.value,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        tagRelease,
        gatlingReleasePublishStep.value,
        writeCurrentVersion,
        pushChanges,
        setNextVersion
      )
    }
  }
}
