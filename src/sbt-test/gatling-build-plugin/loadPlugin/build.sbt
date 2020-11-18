scalaVersion := "2.12.3"

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    TaskKey[Unit]("check") := {
      // Check if the plugin is loaded by search for a specific class
      Class.forName("io.gatling.build.BaseSettingsPlugin")
    }
  )
