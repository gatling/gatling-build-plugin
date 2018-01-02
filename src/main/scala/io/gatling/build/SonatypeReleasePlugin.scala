package io.gatling.build

import sbt._

import sbtrelease.ReleasePlugin.autoImport._
import xerial.sbt.Sonatype.SonatypeKeys._
import com.typesafe.sbt.pgp.PgpKeys.publishSigned

object SonatypeReleasePlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def projectSettings = baseSettings

  import ReleaseProcessKeys._

  private val baseSettings = gatlingReleaseSettings ++ Seq(
    releaseCrossBuild := false,
    releasePublishArtifactsAction := publishSigned.value,
    sonatypeProfileName := "io.gatling"
  )
}
