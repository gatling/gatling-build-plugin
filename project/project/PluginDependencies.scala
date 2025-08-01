import sbt.*
import sbt.Keys.*

object PluginDependencies {
  val deps = Seq(
    // ------------------ //
    // -- DEPENDENCIES -- //
    // ------------------ //
    addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.5.5"),
    addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.14.3"),
    addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.4.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.3.1"),
    addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.10.0"),
    addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.12.2"),
    addSbtPlugin("com.github.sbt"    % "sbt-dynver"   % "5.1.1"),
    libraryDependencies ++= Seq(
      "org.eclipse.jgit"          % "org.eclipse.jgit" % "7.3.0.202506031305-r", // sbt-scalafix
      "ch.qos.logback"            % "logback-core"     % "1.5.18", // sbt-sonatype
      "com.google.guava"          % "guava"            % "33.4.8-jre", // sbt-sonatype
      "org.apache.httpcomponents" % "httpclient"       % "4.5.14" // sbt-sonatype
    )
  )
}
