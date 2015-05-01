package io.gatling.build

import sbt._

import com.typesafe.sbt.pgp.PgpKeys._
import sbtrelease.ReleasePlugin.ReleaseKeys._

object SonatypeReleasePlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  val autoImport = ReleaseProcessKeys
  override def projectSettings = baseSettings

  import autoImport._

  private val baseSettings = gatlingReleaseSettings ++ Seq(
    crossBuild := false,
    publishArtifactsAction := publishSigned.value
  )
}
