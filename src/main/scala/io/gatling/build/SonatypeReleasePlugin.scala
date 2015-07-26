package io.gatling.build

import sbt._

import sbtrelease.ReleasePlugin.autoImport._
import xerial.sbt.Sonatype.SonatypeKeys._

object SonatypeReleasePlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  val autoImport = ReleaseProcessKeys
  override def projectSettings = baseSettings

  import autoImport._

  private val baseSettings = gatlingReleaseSettings ++ Seq(
    releaseCrossBuild := false,
    sonatypeProfileName := "io.gatling"
  )
}
