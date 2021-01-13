package io.gatling.build

import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin
import scalafix.sbt.ScalafixPlugin.autoImport._
import GatlingBuildKeys._

object GatlingAutomatedScalafixPlugin extends AutoPlugin {
  override def requires: Plugins = ScalafixPlugin && GatlingBuildConfigPlugin

  object autoImport {
    val gatlingScalafixCheck = taskKey[Unit]("Check that gatling scalafix rules have been applied")

    def automateScalafixBeforeCompile(configurations: Configuration*): Seq[Setting[_]] =
      configurations.foldLeft(List.empty[Setting[_]]) {
        _ ++ inConfig(_)(
          Seq(
            compile := compile.dependsOn(scalafix.toTask("").dependsOn(scalafixWriteConfigFile)).value
          )
        )
      }
  }

  import autoImport._

  private lazy val scalafixConfigFileSetting = resourceOnConfigDirectoryPath(".scalafix.conf")
  private lazy val scalafixWriteConfigFile = writeResourceOnConfigDirectoryFile(
    path = "default.scalafix.conf",
    fileSetting = scalafixConfigFileSetting
  )

  override def projectSettings: Seq[sbt.Setting[_]] =
    automateScalafixBeforeCompile(Test, Compile) ++
      Seq(
        scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.4",
        scalafixConfig := Some(scalafixConfigFileSetting.value),
        gatlingScalafixCheck := scalafixAll.toTask(" --check").dependsOn(scalafixWriteConfigFile).value
      )
}
