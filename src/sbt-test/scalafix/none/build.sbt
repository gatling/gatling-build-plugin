scalaVersion := "2.13.16"
githubPath := "user/repository"

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    name := "scalafix/none"
  )
