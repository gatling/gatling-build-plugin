package io.gatling.build

import sbt.Keys._
import sbt._

object BaseSettingsPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  private val isJava8 = scala.util.Properties.javaVersion.startsWith("1.8")

  private val JavacOptions =
    if (isJava8) {
      Seq("-source", "1.8", "-target", "1.8")
    } else {
      Seq("--release", "8")
    }

  private val ScalacOptions = {
    val base =
      Seq(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:implicitConversions",
        "-target:jvm-1.8"
      )

    if (isJava8) {
      base
    } else {
      base ++ Seq("-release", "8")
    }
  }

  override def projectSettings =
    Seq(
      homepage := Some(url("https://gatling.io")),
      organization := "io.gatling",
      organizationHomepage := Some(url("https://gatling.io")),
      startYear := Some(2011),
      updateOptions := configureUpdateOptions(updateOptions.value),
      javacOptions := JavacOptions,
      javacOptions in (Compile, doc) := Seq(
        "-source",
        "1.8"
      ),
      resolvers := Seq(DefaultMavenRepository, Resolver.jcenterRepo),
      scalacOptions := ScalacOptions
    )

  private def configureUpdateOptions(options: UpdateOptions): UpdateOptions =
    options
      .withCachedResolution(true)
      .withGigahorse(false)
      .withLatestSnapshots(false)
}
