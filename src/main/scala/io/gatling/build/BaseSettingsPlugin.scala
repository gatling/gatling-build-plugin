package io.gatling.build

import sbt.Keys._
import sbt._

object BaseSettingsPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  val baseSettings = Seq(
    homepage := Some(url("https://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(url("https://gatling.io")),
    startYear := Some(2011),
    scalaVersion := "2.12.5",
    updateOptions := updateOptions.value.withCachedResolution(true),
    javacOptions := Seq(
      "-Xlint:-options",
      "-source", "1.8",
      "-target", "1.8"
    ),
    resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
    scalacOptions := Seq(
      "-encoding", "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xfuture",
      "-target:jvm-1.8"
    )
  )
}
