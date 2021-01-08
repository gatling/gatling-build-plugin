package io.gatling.build

import sbt.{Def, _}
import sbt.Keys._

import scala.io.Source

object GatlingBuildKeys {
  val gatlingBuildConfigDirectory = settingKey[File]("Location where to put configuration from gatling-build-plugin. Defaults to target/gatling-build-config")

  def resourceOnConfigDirectoryPath(name: String): Def.Initialize[File] = Def.setting {
    gatlingBuildConfigDirectory.value / name
  }

  def writeResourceOnConfigDirectoryFile(path: String, fileSetting: Def.Initialize[File]): Def.Initialize[Task[File]] = Def.task {
    val file = fileSetting.value
    val resourceUrl = getClass.getResource(s"/$path")
    val resource = Source.fromURL(resourceUrl)
    resource.getLines.foreach { line =>
      IO.write(file, s"$line\n", IO.defaultCharset, append = true)
    }
    resource.close()
    file
  }
}

object GatlingBuildConfigPlugin extends AutoPlugin {
  override def requires = empty

  override def trigger = allRequirements

  object autoImport {
    val gatlingBuildConfigDirectory = GatlingBuildKeys.gatlingBuildConfigDirectory
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    gatlingBuildConfigDirectory := (target in LocalRootProject).value / "gatling-build-config"
  )
}
