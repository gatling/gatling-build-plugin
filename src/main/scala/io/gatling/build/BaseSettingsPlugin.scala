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
        if (v.startsWith("2.10"))
          Seq("-target:jvm-1.7")
        else if (v.startsWith("2.11"))
          Seq("-target:jvm-1.8", "-Ybackend:GenBCode", "-Ydelambdafy:method")
        else
          Seq("-target:jvm-1.8")
      )
  }

  val baseSettings = Seq(
    homepage := Some(url("http://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(url("http://gatling.io")),
    startYear := Some(2011),
    scalaVersion := "2.12.1",
    updateOptions := updateOptions.value.withCachedResolution(true),
    javacOptions := Seq("-Xlint:-options", "-source", "1.8", "-target", "1.8"),
    resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
    scalacOptions := scalacOptionsVersion(scalaVersion.value)
  )
}
