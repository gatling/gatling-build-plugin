lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin, GatlingOssPlugin)
  .settings(
    name := "gatling-build-plugin",
    organization := "io.gatling",
    startYear := Some(2011),
    sbtPluginPublishLegacyMavenStyle := false,
    // Scala version for SBT 2 must match what SBT 2 uses internally (3.8.x).
    // Using 3.6.x causes TASTy forward-incompatibility reading SBT's own class files.
    crossScalaVersions := List("2.12.21", "3.8.4"),
    scalaVersion := "2.12.21",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.12.13"
        case "3"    => "2.0.1"
      }
    },
    scalacOptions := {
      val common = Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked")
      // Scala 3.8.x dropped support for -release 11; minimum Java output version is now 17
      val release = if (scalaBinaryVersion.value == "3") {
        "17"
      } else {
        "11"
      }
      // -source:3.0-migration lets Scala 3 accept Scala 2.12 syntax (Setting[_], imports._, etc.)
      val extra = if (scalaBinaryVersion.value == "3") {
        Seq("-source:3.0-migration")
      } else {
        Seq.empty
      }
      common ++ extra ++ Seq("-release", release)
    },
    Compile / unmanagedSourceDirectories += {
      if (scalaBinaryVersion.value == "2.12") {
        (Compile / sourceDirectory).value / "scala-2.12"
      } else {
        (Compile / sourceDirectory).value / "scala-3"
      }
    },
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
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test,
    // XML literals in GatlingBasicInfoPlugin need scala-xml on the Scala 3 classpath.
    // In Scala 2.12 it is bundled with the language; in Scala 3 it must be explicit.
    libraryDependencies ++= {
      if (scalaBinaryVersion.value == "3")
        Seq("org.scala-lang.modules" %% "scala-xml" % "2.3.0" % Provided)
      else Seq.empty
    }
  )
