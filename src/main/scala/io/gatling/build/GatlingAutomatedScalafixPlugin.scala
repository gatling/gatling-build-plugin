package io.gatling.build

import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin
import scalafix.sbt.ScalafixPlugin.autoImport.{ scalafix, scalafixDependencies }

object GatlingAutomatedScalafixPlugin extends AutoPlugin {
  override def requires = ScalafixPlugin

  object autoImport {
    val scalafixCheck = taskKey[Unit]("Check that scalafix rules have been applied")

    def automateScalafixBeforeCompile(configurations: Configuration*): Seq[Setting[_]] =
      configurations.foldLeft(List.empty[Setting[_]]) {
        _ ++ inConfig(_)(compile := compile.dependsOn(scalafix.toTask("")).value)
      }
  }

  import autoImport._

  override def projectSettings = automateScalafixBeforeCompile(Test, Compile) ++ Vector(
    )

  scalafixCheck := scalafix.toTask(" --check").value
}
