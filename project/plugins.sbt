// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.11.1")
addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.1.0")
addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.2.1")
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.10.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.21")
addSbtPlugin("com.github.sbt"    % "sbt-git"      % "2.0.1")

// This project is its own plugin :)
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"
Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "resources"
