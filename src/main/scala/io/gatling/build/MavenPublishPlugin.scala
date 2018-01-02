package io.gatling.build

import java.io.FileInputStream
import java.util.Properties

import scala.collection.JavaConverters._
import scala.util.Properties._

import sbt._
import sbt.Keys._

object MavenPublishKeys {

  private[build] val SbtHome = Path.userHome / ".sbt"
  private[build] val PrivateNexusCredentialsFile = SbtHome / ".private-nexus-credentials"

  private val PrivateNexusRepositoriesRoot: Option[String] = {
    if (PrivateNexusCredentialsFile.exists) {
      val is = new FileInputStream(PrivateNexusCredentialsFile)
      val credProps: Map[String, String] =
        try {
          val props = new Properties
          props.load(is)
          props.asScala.map { case (k, v) => (k.toString, v.toString.trim) }.toMap
        } finally {
          is.close()
        }

      for {
        scheme <- credProps.get("scheme")
        host <- credProps.get("host")
      } yield s"$scheme://$host/content/repositories"

    } else {
      None
    }
  }

  private[build] val PrivateNexusReleases: Option[MavenRepository] = PrivateNexusRepositoriesRoot.map("Private Nexus Releases" at _ + "/releases/")
  private[build] val PrivateNexusSnapshots: Option[MavenRepository] = PrivateNexusRepositoriesRoot.map("Private Nexus Snapshots" at _ + "/snapshots/")
  private[build] def publicNexusResository(isSnapshot: Boolean): MavenRepository = if (isSnapshot) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging
  private[build] def privateNexusRepository(isSnapshot: Boolean): Option[MavenRepository] = if (isSnapshot) PrivateNexusSnapshots else PrivateNexusReleases

  val githubPath = settingKey[String]("Project path on Github")
  val projectDevelopers = settingKey[Seq[GatlingDeveloper]]("List of contributors for this project")
  var useSonatypeRepositories = settingKey[Boolean]("Use Sonatype repositories for CI or during release process")
  val pushToPrivateNexus = settingKey[Boolean]("Should this project's artifacts be pushed to our private Nexus ?")

  case class GatlingDeveloper(emailAddress: String, name: String, isGatlingCorp: Boolean)
}

object MavenPublishPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin && SonatypeReleasePlugin
  override def trigger = allRequirements
  override def projectSettings = baseSettings

  import MavenPublishKeys._

  private val baseSettings = Seq(
    useSonatypeRepositories := false,
    crossPaths := false,
    pushToPrivateNexus := false,
    publishTo := {
      val privateRepo = privateNexusRepository(isSnapshot.value)
      val publicRepo = publicNexusResository(isSnapshot.value)
      if (pushToPrivateNexus.value) privateRepo else Some(publicRepo)
    },
    pomExtra := mavenScmBlock(githubPath.value) ++ developersXml(projectDevelopers.value),
    resolvers ++= (if (useSonatypeRepositories.value) sonatypeRepositories else Seq.empty) :+ Resolver.mavenLocal,
    credentials += Credentials(if (pushToPrivateNexus.value) PrivateNexusCredentialsFile else SbtHome / ".credentials")
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
            { if (dev.isGatlingCorp) <organization>Gatling Corp</organization> }
          </developer>
        }
      }
    </developers>
  }
}
