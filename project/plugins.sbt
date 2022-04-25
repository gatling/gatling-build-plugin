// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix" % "0.10.0")
addSbtPlugin("com.github.sbt"    % "sbt-release"  % "1.1.0")
addSbtPlugin("com.github.sbt"    % "sbt-pgp"      % "2.1.2")
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.7.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.10")
addSbtPlugin("com.github.sbt"    % "sbt-git"      % "2.0.0")

// This project is its own plugin :)
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"
Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "resources"
