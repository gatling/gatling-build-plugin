// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.9.34")
addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.1.0")
addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.1.2")
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.6.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.11")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"      % "1.0.2")

// This project is its own plugin :)
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"
Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "resources"
