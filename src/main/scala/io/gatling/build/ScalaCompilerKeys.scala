package io.gatling.build

import sbt.SettingKey

object ScalaCompilerKeys {

  val defaultScalacOptions = SettingKey[Seq[String]]("Default options used by the scala compiler")
}
