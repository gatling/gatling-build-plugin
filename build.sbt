homepage             := Some(new URL("http://gatling.io"))
organization         := "io.gatling"
organizationHomepage := Some(new URL("http://gatling.io"))
startYear            := Some(2011)
licenses             := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

sbtPlugin := true

scalacOptions := Seq(
  "-encoding",
  "UTF-8",
  "-target:jvm-1.6",
  "-deprecation",
  "-feature",
  "-unchecked"
)

// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.4.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.3.0")
