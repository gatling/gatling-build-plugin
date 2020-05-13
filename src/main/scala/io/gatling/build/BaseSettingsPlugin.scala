package io.gatling.build

import sbt.Keys._
import sbt._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import scalafix.sbt.ScalafixPlugin.autoImport.{ scalafix, scalafixDependencies }

object BaseSettingsPlugin extends AutoPlugin {

  val scalafixCheck = taskKey[Unit]("Check that scalafix rules have been applied")

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  override def projectSettings =
    Seq(
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
      scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.0"
    ) ++ scalafixSettings

  private val scalafixSettings =
    scalafixBeforeCompile(Compile) ++ scalafixBeforeCompile(Test) ++
      scalafixCheckTask(Compile) ++ scalafixCheckTask(Test)

  def scalafixBeforeCompile(config: Configuration) =
    inConfig(config)(compile := compile.dependsOn(scalafix.toTask("")).value)

  def scalafixCheckTask(config: Configuration) =
    inConfig(config)(scalafixCheck := scalafix.toTask(" --check").value)

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)

}
