package io.gatling.build

import scala.util.Properties._

import sbt._
import sbt.Keys._

object MavenPublishKeys {
  val githubPath = settingKey[String]("Project path on Github")
  val projectDevelopers = settingKey[Seq[GatlingDeveloper]]("List of contributors for this project")
  var useSonatypeRepositories = settingKey[Boolean]("Use Sonatype repositories for CI or during release process")

  case class GatlingDeveloper(emailAddress: String, name: String, isEbiz: Boolean)
}
object MavenPublishPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin && SonatypeReleasePlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  val autoImport = MavenPublishKeys

  import autoImport._

  private val baseSettings = Seq(
    useSonatypeRepositories := false,
    crossPaths              := false,
    pomExtra                := mavenScmBlock(githubPath.value) ++ developersXml(projectDevelopers.value),
    publishMavenStyle       := true,
    pomIncludeRepository    := { _ => false },
    publishTo               := Some(if(isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
    resolvers               ++= (if(useSonatypeRepositories.value) sonatypeRepositories else Seq.empty) :+ Resolver.mavenLocal,
    credentials             += Credentials(Path.userHome / ".sbt" / ".credentials")
  )

  private def sonatypeRepositories: Seq[Resolver] = Seq(
    envOrNone("CI").map(_ => Opts.resolver.sonatypeSnapshots),
    propOrNone("release").map(_ => Opts.resolver.sonatypeReleases)
  ).flatten

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
