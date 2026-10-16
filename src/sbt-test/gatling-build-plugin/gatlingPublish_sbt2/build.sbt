scalaVersion := "2.13.16"

githubPath := "user/repository"

ThisBuild / gatlingReleasesRepository := Some("local" at ((ThisBuild / baseDirectory).value / "repo").toURI.toString)

lazy val root = rootProject
  .enablePlugins(GatlingCorpPlugin)
  .settings(
    name := "publish-corp"
  )

lazy val skipped = project
  .enablePlugins(GatlingCorpPlugin)
  .settings(
    publish / skip := true
  )
