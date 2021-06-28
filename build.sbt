lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin, GatlingOssPlugin)
  .settings(
    name := "gatling-build-plugin",
    homepage := Some(new URL("https://gatling.io")),
    organization := "io.gatling",
    organizationHomepage := Some(new URL("https://gatling.io")),
    startYear := Some(2011),
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-target:jvm-1.8",
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
      GatlingDeveloper("pdalpra@gatling.io", "Pierre Dal-Pra", true),
      GatlingDeveloper("gcorre@gatling.io", "Guillaume Corre", true),
      GatlingDeveloper("tpetillot@gatling.io", "Thomas Petillot", true),
      GatlingDeveloper("sbrevet@gatling.io", "Sébastien Brevet", true)
    ),
// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
    addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.4.2"),
    addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.9.29"),
    addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.1.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.1.2"),
    addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.6.0"),
    addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.7"),
    addSbtPlugin("com.typesafe.sbt"  % "sbt-git"      % "1.0.1"),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
  )

addCommandAlias(
  "ci-checks",
  List(
    "all clean",
    "all scalafmtSbtCheck scalafmtCheckAll",
    "all gatlingScalafixCheck",
    "test",
    "scripted"
  ).mkString(";", ";", "")
)
