import sbt._
import sbt.Keys._

object PluginDependencies {
  val deps = Seq(
    // ------------------ //
    // -- DEPENDENCIES -- //
    // ------------------ //
    addSbtPlugin("org.scalameta"  % "sbt-scalafmt" % "2.6.1"),
    addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix" % "0.14.7"),
    addSbtPlugin("com.github.sbt" % "sbt-release"  % "1.5.0"),
    addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.3.1"),
    addSbtPlugin("com.github.sbt" % "sbt-header"   % "5.11.0"),
    addSbtPlugin("com.github.sbt" % "sbt-dynver"   % "5.1.1"),
    libraryDependencies ++= Seq(
      "org.eclipse.jgit" % "org.eclipse.jgit" % "7.7.0.202606012155-r" // sbt-scalafix
    )
  )
}
