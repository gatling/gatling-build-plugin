scalaVersion := "2.13.16"

githubPath := "user/repository"

ThisBuild / gatlingReleasesRepository := Some("local" at ((ThisBuild / baseDirectory).value / "repo").toURI.toString)

lazy val root = rootProject
  .enablePlugins(GatlingCorpPlugin)
  .settings(
    name := "publish-corp",
    // Like the private repositories defined in a global .sbt file: must not end up in the published POM
    resolvers += "Private Repository" at "https://private.example.com/maven/releases"
  )

lazy val skipped = project
  .enablePlugins(GatlingCorpPlugin)
  .settings(
    publish / skip := true
  )
