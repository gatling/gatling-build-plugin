package io.gatling.build

import org.scalafmt.sbt.ScalafmtPlugin
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.{scalafmtConfig, scalafmtOnCompile}
import sbt._
import GatlingBuildKeys._

object GatlingAutomatedScalafmtPlugin extends AutoPlugin {

  override def requires: Plugins = ScalafmtPlugin && GatlingBuildConfigPlugin

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    scalafmtOnCompile := true
  )

  private lazy val scalafmtConfigFileSetting = resourceOnConfigDirectoryPath(".scalafmt.conf")
  private lazy val scalafmtWriteConfigFile = writeResourceOnConfigDirectoryFile(
    path = "default.scalafmt.conf",
    fileSetting = scalafmtConfigFileSetting
  )

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalafmtConfig := scalafmtWriteConfigFile.value
  )
}
