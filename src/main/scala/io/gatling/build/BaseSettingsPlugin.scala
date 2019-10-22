package io.gatling.build

import sbt.Keys._
import sbt._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile

object BaseSettingsPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = Seq(
    homepage := Some(url("https://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(url("https://gatling.io")),
    startYear := Some(2011),
    scalaVersion := "2.12.10",
    scalafmtOnCompile := true,
    updateOptions := configureUpdateOptions(updateOptions.value),
    javacOptions := Seq(
      "-Xlint:none",
      "-source",
      "1.8",
      "-target",
      "1.8"
    ),
    resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xfuture",
      "-target:jvm-1.8"
    )
  )

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)

}
