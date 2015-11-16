package io.gatling.build

import scala.util.Properties._

import sbt._
import sbt.Keys._

object MavenPublishKeys {
  private val PrivateNexusRoot = sys.env.get("PRIVATE_NEXUS_BASE_URL")
  private[build] val PrivateNexusReleases = PrivateNexusRoot.map("Private Nexus Releases" at _ + "content/repositories/releases/")
  private[build] val PrivateNexusSnapshots = PrivateNexusRoot.map("Private Nexus Snapshots" at _ + "content/repositories/snapshots/")

  val githubPath = settingKey[String]("Project path on Github")
  val projectDevelopers = settingKey[Seq[GatlingDeveloper]]("List of contributors for this project")
  var useSonatypeRepositories = settingKey[Boolean]("Use Sonatype repositories for CI or during release process")
  val pushToPrivateNexus = settingKey[Boolean]("Should this project's artifacts be pushed to our private Nexus ?")

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
    crossPaths := false,
    pushToPrivateNexus := false,
    publishTo := (if (pushToPrivateNexus.value) privateNexusRepository(isSnapshot.value) else publishTo.value),
    pomExtra := mavenScmBlock(githubPath.value) ++ developersXml(projectDevelopers.value),
    resolvers ++= (if (useSonatypeRepositories.value) sonatypeRepositories else Seq.empty) :+ Resolver.mavenLocal,
    credentials += Credentials(Path.userHome / ".sbt" / (if (pushToPrivateNexus.value) ".private-nexus-credentials" else ".credentials"))
  )

  private def sonatypeRepositories: Seq[Resolver] = Seq(
    envOrNone("CI").map(_ => Opts.resolver.sonatypeSnapshots),
    propOrNone("release").map(_ => Opts.resolver.sonatypeReleases)
  ).flatten

  private def mavenScmBlock(githubPath: String) =
    <scm>
      <connection>scm:git:git@github.com:{ githubPath }.git</connection>
      <developerConnection>scm:git:git@github.com:{ githubPath }.git</developerConnection>
      <url>https://github.com/{ githubPath }</url>
      <tag>HEAD</tag>
    </scm>

  private def developersXml(devs: Seq[GatlingDeveloper]) = {
    <developers>
      {
        for (dev <- devs) yield {
          <developer>
            <id>{ dev.emailAddress }</id>
            <name>{ dev.name }</name>
            { if (dev.isEbiz) <organization>eBusiness Information, Excilys Group</organization> }
          </developer>
        }
      }
    </developers>
  }

  private def privateNexusRepository(isSnapshot: Boolean) =
    if (isSnapshot) PrivateNexusSnapshots else PrivateNexusReleases
}
