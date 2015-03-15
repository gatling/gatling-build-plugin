package io.gatling.build

import sbt._
import sbt.Keys._

object PromptPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  val baseSettings = Seq(
    shellPrompt := { state => Project.extract(state).currentProject.id + " > " }
  )
}
