package io.gatling.build

import sbt.Keys._
import sbt._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import scalafix.sbt.ScalafixPlugin.autoImport.{ scalafix, scalafixDependencies }

object BaseSettingsPlugin extends AutoPlugin {

  val scalafixCheck = taskKey[Unit]("Check that scalafix rules have been applied")

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  override def projectSettings = Seq(
    homepage := Some(url("https://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(url("https://gatling.io")),
    startYear := Some(2011),
    scalaVersion := "2.12.11",
    scalafmtOnCompile := true,
    updateOptions := configureUpdateOptions(updateOptions.value),
    javacOptions := Seq(
      "-source",
      "1.8",
      "-target",
      "1.8"
    ),
    javacOptions in (Compile, doc) := Seq(
      "-source",
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
    ),
    scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.5.0",
    (compile in Compile) := (compile in Compile).dependsOn((scalafix in Compile).toTask("")).value,
    compile in Test := (compile in Test).dependsOn((scalafix in Test).toTask("")).value,
  ) ++ scalafixCheckSettings

  private val scalafixCheckSettings = {
    val scalafixCheckTask = (scalafixCheck := scalafix.toTask(" --check").value)
    inConfig(Compile)(scalafixCheckTask) ++ inConfig(Test)(scalafixCheckTask)
  }

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)

}
