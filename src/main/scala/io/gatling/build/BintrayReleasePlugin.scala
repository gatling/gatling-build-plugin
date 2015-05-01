package io.gatling.build

import sbt._
import sbt.Keys._

import bintray.Plugin._
import bintray.Keys._

object BintrayReleasePlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  val autoImport = ReleaseProcessKeys
  override def projectSettings = baseSettings

  import autoImport._

  val baseSettings = bintrayPublishSettings ++ gatlingReleaseSettings ++ Seq(
    publishMavenStyle := false,
    bintrayOrganization in bintray := None,
    repository in bintray := "sbt-plugins"
  )
}
