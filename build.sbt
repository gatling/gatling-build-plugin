homepage             := Some(new URL("https://gatling.io"))
organization         := "io.gatling"
organizationHomepage := Some(new URL("https://gatling.io"))
startYear            := Some(2011)
licenses             := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

sbtPlugin := true

scalacOptions := Seq(
  "-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-deprecation",
  "-feature",
  "-unchecked"
)

// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "4.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
