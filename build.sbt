homepage             := Some(new URL("http://gatling.io"))
organization         := "io.gatling"
organizationHomepage := Some(new URL("http://gatling.io"))
startYear            := Some(2011)
licenses             := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

sbtPlugin := true

scalacOptions := Seq(
  "-encoding",
  "UTF-8",
  "-target:jvm-1.7",
  "-deprecation",
  "-feature",
  "-unchecked"
)

releaseSettings

// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.2.0")
