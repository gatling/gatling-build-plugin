import sbt.*
import sbt.Keys.*

object PluginDependencies {
  val deps = Seq(
    // ------------------ //
    // -- DEPENDENCIES -- //
    // ------------------ //
    addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.5.2"),
    addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.11.1"),
    addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.1.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.2.1"),
    addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.10.0"),
    addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.10.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-dynver"   % "5.0.1"),
    libraryDependencies ++= Seq(
      "org.eclipse.jgit"          % "org.eclipse.jgit" % "6.8.0.202311291450-r", // sbt-scalafix
      "ch.qos.logback"            % "logback-core"     % "1.4.14", // sbt-sonatype
      "com.google.guava"          % "guava"            % "33.0.0-jre", // sbt-sonatype
      "org.apache.httpcomponents" % "httpclient"       % "4.5.14" // sbt-sonatype
    )
  )
}
