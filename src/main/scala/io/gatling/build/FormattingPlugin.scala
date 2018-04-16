package io.gatling.build

import sbt._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object FormattingPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = Seq(
    ScalariformKeys.preferences := formattingPreferences,
    ScalariformKeys.autoformat := true
  )

  private def formattingPreferences =
    FormattingPreferences()
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DanglingCloseParenthesis, Force)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(NewlineAtEndOfFile, true)
}
