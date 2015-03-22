package io.gatling.build

import sbt._
import sbt.Keys._

object MavenPublishPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin && SonatypeReleasePlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  object autoImport {
    case class GatlingDeveloper(emailAddress: String, name: String, isEbiz: Boolean)

    val githubPath = settingKey[String]("Project path on Github")
    val projectDevelopers = settingKey[Seq[GatlingDeveloper]]("List of contributors for this project")
  }

  import autoImport._


  private val baseSettings = Seq(
    crossPaths           := false,
    pomExtra             := mavenScmBlock(githubPath.value) ++ developersXml(projectDevelopers.value),
    publishMavenStyle    := true,
    pomIncludeRepository := { _ => false },
    publishTo            := Some(if(isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
    credentials          += Credentials(Path.userHome / ".sbt" / ".credentials")
  )


  private def mavenScmBlock(githubPath: String) =
    <scm>
      <connection>scm:git:git@github.com:{githubPath}.git</connection>
      <developerConnection>scm:git:git@github.com:{githubPath}.git</developerConnection>
      <url>https://github.com/{githubPath}</url>
      <tag>HEAD</tag>
    </scm>

  private def developersXml(devs: Seq[GatlingDeveloper]) = {
    <developers>
      {
      for(dev <- devs)
      yield {
        <developer>
          <id>{dev.emailAddress}</id>
          <name>{dev.name}</name>
          { if (dev.isEbiz) <organization>eBusiness Information, Excilys Group</organization> }
        </developer>
      }
      }
    </developers>
  }
}
