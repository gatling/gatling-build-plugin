ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin, GatlingOssPlugin)
  .settings(
    name := "gatling-build-plugin",
    homepage := Some(new URI("https://gatling.io").toURL),
    organization := "io.gatling",
    organizationHomepage := Some(new URI("https://gatling.io").toURL),
    startYear := Some(2011),
    licenses := Seq("Apache-2.0" -> new URI("http://www.apache.org/licenses/LICENSE-2.0.html").toURL),
    scalaVersion := "2.12.19",
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-release",
      "11",
      "-deprecation",
      "-feature",
      "-unchecked"
    ),
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    githubPath := "gatling/gatling-build-plugin",
    gatlingDevelopers := Seq(
      GatlingDeveloper("slandelle@gatling.io", "Stéphane Landelle", true),
      GatlingDeveloper("pdalpra@xxxx.yy", "Pierre Dal-Pra", false),
      GatlingDeveloper("gcorre@gatling.io", "Guillaume Corre", true),
      GatlingDeveloper("tpetillot@gatling.io", "Thomas Petillot", true),
      GatlingDeveloper("sbrevet@gatling.io", "Sébastien Brevet", true)
    ),
// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
    PluginDependencies.deps,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
