ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin, GatlingOssPlugin)
  .settings(
    name := "gatling-build-plugin",
    homepage := Some(new URL("https://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(new URL("https://gatling.io")),
    startYear := Some(2011),
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    scalaVersion := "2.12.18",
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-release",
      "8",
      "-deprecation",
      "-feature",
      "-unchecked"
    ) ++ (if (scala.util.Properties.javaVersion.startsWith("1.8")) Nil else Seq("-release", "8")),
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
    addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.5.2"),
    addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.11.1"),
    addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.1.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.2.1"),
    addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.10.0"),
    addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.21"),
    addSbtPlugin("com.github.sbt"    % "sbt-git"      % "2.0.1"),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
  )
