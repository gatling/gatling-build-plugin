package io.gatling.build

import sbt._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object FormattingPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  import autoImport._

  val baseSettings = scalariformSettings(autoformat = true) ++ Seq(
    ScalariformKeys.preferences := formattingPreferences
  )

  private def formattingPreferences =
    FormattingPreferences()
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(IndentLocalDefs, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
}
