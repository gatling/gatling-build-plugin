package io.gatling.build

import sbt.Keys._
import sbt._

object BaseSettingsPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  def scalacOptionsVersion(v: String) = {
    Seq(
      "-encoding", "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xfuture"
    ) ++ (
        if (v.startsWith("2.10")) Seq("-target:jvm-1.7") else Seq("-target:jvm-1.8", "-Ybackend:GenBCode", "-Ydelambdafy:method")
      )
  }

  val baseSettings = Seq(
    homepage := Some(url("http://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(url("http://gatling.io")),
    startYear := Some(2011),
    scalaVersion := "2.11.8",
    updateOptions := updateOptions.value.withCachedResolution(true),
    javacOptions := Seq("-Xlint:-options", "-source", "1.8", "-target", "1.8"),
    resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
    scalacOptions := Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.7",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xfuture"
    ),
    scalacOptions := scalacOptionsVersion(scalaVersion.value)
  )
}
