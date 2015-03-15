package io.gatling.build

import scala.util.Properties.propOrEmpty

import sbt._

import com.typesafe.sbt.pgp.PgpKeys._
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseVersion
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._

object SonatypeReleasePlugin extends AutoPlugin {

  override def projectSettings = baseSettings

  private val baseSettings = releaseSettings ++ Seq(
    crossBuild := false,
    publishArtifactsAction := publishSigned.value,
    releaseVersion := { _ => propOrEmpty("releaseVersion") },
    nextVersion := { _ => propOrEmpty("developmentVersion") }
  )
}
