package io.gatling.build

import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import xerial.sbt.Sonatype.SonatypeKeys._
import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import xerial.sbt.Sonatype

object SonatypeReleasePlugin extends AutoPlugin {

  import ReleaseProcessKeys._

  override def requires = Sonatype

  override def projectSettings = gatlingReleaseSettings ++ Seq(
    releaseCrossBuild := false,
    releasePublishArtifactsAction := publishSigned.value,
    sonatypeProfileName := "io.gatling"
  )
}
