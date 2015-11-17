package io.gatling.build

import sbt._

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

object FormattingPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  val baseSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences := formattingPreferences
  )

  private def formattingPreferences =
    FormattingPreferences()
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(IndentLocalDefs, true)
}
