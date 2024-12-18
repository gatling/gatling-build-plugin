import sbt.*
import sbt.Keys.*

object PluginDependencies {
  val deps = Seq(
    // ------------------ //
    // -- DEPENDENCIES -- //
    // ------------------ //
    addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.5.2"),
    addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.13.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.4.0"),
    addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.3.1"),
    addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.10.0"),
    addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.12.2"),
    addSbtPlugin("com.github.sbt"    % "sbt-dynver"   % "5.1.0"),
    libraryDependencies ++= Seq(
      "org.eclipse.jgit"          % "org.eclipse.jgit" % "7.1.0.202411261347-r", // sbt-scalafix
      "ch.qos.logback"            % "logback-core"     % "1.5.12", // sbt-sonatype
      "com.google.guava"          % "guava"            % "33.4.0-jre", // sbt-sonatype
      "org.apache.httpcomponents" % "httpclient"       % "4.5.14" // sbt-sonatype
    )
  )
}
