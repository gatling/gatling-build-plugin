package io.gatling.build

import sbt._
import sbt.Keys._

object GatlingBasicInfoPlugin extends AutoPlugin {
  override def requires = empty

  override def trigger = allRequirements

  override def projectSettings =
    Seq(
      homepage := Some(url("https://gatling.io")),
      organization := "io.gatling",
      organizationHomepage := Some(url("https://gatling.io")),
      startYear := Some(2011)
    )
}
