package io.gatling.build

import sbt.Keys._
import sbt._

object BaseSettingsPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  import ScalaCompilerKeys._

  val baseSettings = Seq(
    homepage := Some(url("http://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(url("http://gatling.io")),
    startYear := Some(2011),
    scalaVersion := "2.11.8",
    updateOptions := updateOptions.value.withCachedResolution(true),
    javacOptions := Seq("-Xlint:-options", "-source", "1.8", "-target", "1.8"),
    resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
    defaultScalacOptions := Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.7",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xfuture"
    ),
    scalacOptions := defaultScalacOptions.value ++ Seq("-target:jvm-1.8", "-Ybackend:GenBCode", "-Ydelambdafy:method")
  )
}
