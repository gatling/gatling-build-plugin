package io.gatling.build

import scala.util.Properties.propOrEmpty

import sbt._
import sbt.Keys._

import bintray.Plugin._
import bintray.Keys._
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseVersion
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._

object BintrayReleasePlugin extends AutoPlugin {

  override def projectSettings = baseSettings

  val baseSettings = bintrayPublishSettings ++ releaseSettings ++ Seq(
    publishMavenStyle := false,
    bintrayOrganization in bintray := None,
    repository in bintray := "sbt-plugins",
    releaseVersion := { _ => propOrEmpty("releaseVersion") },
    nextVersion := { _ => propOrEmpty("developmentVersion") }
  )
}
