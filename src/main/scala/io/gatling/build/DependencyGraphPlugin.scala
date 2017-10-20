package io.gatling.build

import sbt._

import net.virtualvoid.sbt.graph.DependencyGraphSettings.graphSettings

object DependencyGraphPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = graphSettings

}
