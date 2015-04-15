package io.gatling.build

import io.gatling.build.license._

import de.heikoseeberger.sbtheader._
import de.heikoseeberger.sbtheader.HeaderKey._

import sbt._
import sbt.Keys._

object LicenseKeys {
  val license = settingKey[License]("The project's license")
}
object LicensePlugin extends AutoPlugin {

  override def requires = HeaderPlugin

  override def trigger = allRequirements

  override def projectSettings = baseSettings

  val autoImport = LicenseKeys

  import autoImport._

  val baseSettings = AutomateHeaderPlugin.projectSettings ++ Seq(
    license := ApacheV2,
    licenses := Seq(license.value.name -> license.value.url),
    headers := license.value.headers
  )
}
