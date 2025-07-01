scalaVersion := "2.13.16"
ThisBuild / scalafixDependencies += "org.scalameta" % "sbt-scalafmt" % "2.5.5"
githubPath := "user/repository"

lazy val root = (project in file("."))
  .enablePlugins(GatlingAutomatedScalafmtPlugin)
  .settings(
    version := "0.1",
    name := "scalafmt/automated"
  )
