scalaVersion := "2.12.3"

lazy val root = (project in file("."))
  .enablePlugins(GatlingAutomatedScalafixPlugin)
  .settings(
    version := "0.1",
    name := "scalafix/automated"
  )
