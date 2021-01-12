package io.gatling.build

import scala.util.Properties._
import sbt._
import sbt.Keys._
import GatlingReleasePlugin.autoimport._
import Repositories.ReleaseStatus

object MavenPublishKeys {
  val githubPath = settingKey[String]("Project path on Github")
  val projectDevelopers = settingKey[Seq[GatlingDeveloper]]("List of contributors for this project")
  var useSonatypeRepositories = settingKey[Boolean]("Use Sonatype repositories for CI or during release process")
  val pushToPrivateNexus = settingKey[Boolean]("Should this project's artifacts be pushed to our private Nexus ?")

  case class GatlingDeveloper(emailAddress: String, name: String, isGatlingCorp: Boolean)
}

object MavenPublishPlugin extends AutoPlugin {

  override def requires: Plugins = plugins.JvmPlugin && GatlingReleasePlugin
  override def trigger: PluginTrigger = allRequirements
  override def projectSettings: Seq[Setting[_]] = baseSettings

  import MavenPublishKeys._

  private val baseSettings = Seq(
    useSonatypeRepositories := false,
    crossPaths := false,
    pushToPrivateNexus := false,
    publishTo := {
      val status =
        if (isSnapshot.value) ReleaseStatus.Snapshot
        else if (isMilestone.value) ReleaseStatus.Milestone
        else ReleaseStatus.Release
      Repositories.nexusRepository(status, pushToPrivateNexus.value)
    },
    pomExtra := mavenScmBlock(githubPath.value) ++ developersXml(projectDevelopers.value),
    resolvers ++= (if (useSonatypeRepositories.value) sonatypeRepositories else Seq.empty) :+ Resolver.mavenLocal,
    credentials += Repositories.credentials(pushToPrivateNexus.value)
  )

  private def sonatypeRepositories: Seq[Resolver] =
    Seq(
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
      for (dev <- devs) yield {
        <developer>
            <id>{dev.emailAddress}</id>
            <name>{dev.name}</name>
            {if (dev.isGatlingCorp) <organization>Gatling Corp</organization>}
        </developer>
      }
    }
    </developers>
  }
}
