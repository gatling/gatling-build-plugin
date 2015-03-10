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
