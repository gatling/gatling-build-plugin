package io.gatling.build

import scala.collection.JavaConverters._

import java.util.Properties

import sbt._

object Repositories {

  private val SbtHome = Path.userHome / ".sbt"
  private val PublicNexusCredentialsFile = SbtHome / ".credentials"
  private val PrivateNexusCredentialsFile = SbtHome / ".private-nexus-credentials"

  private val PrivateNexusRepositoriesRoot = {
    val props = new Properties
    IO.load(props, PrivateNexusCredentialsFile)
    val propsAsMap = props.asScala.map { case (k, v) => (k.toString, v.toString.trim) }
    for {
      scheme <- propsAsMap.get("scheme")
      host <- propsAsMap.get("host")
    } yield s"$scheme://$host/content/repositories"
  }

  private val PrivateNexusReleases = PrivateNexusRepositoriesRoot.map("Private Nexus Releases" at _ + "/releases/")
  private val PrivateNexusSnapshots = PrivateNexusRepositoriesRoot.map("Private Nexus Snapshots" at _ + "/snapshots/")

  private def publicNexusRepository(isSnapshot: Boolean) =
    if (isSnapshot) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging

  private def privateNexusRepository(isSnapshot: Boolean) =
    if (isSnapshot) PrivateNexusSnapshots else PrivateNexusReleases

  def nexusRepository(isSnapshot: Boolean, usePrivate: Boolean): Option[MavenRepository] =
    if (usePrivate) privateNexusRepository(isSnapshot) else Some(publicNexusRepository(isSnapshot))

  def credentials(usePrivate: Boolean): Credentials =
    Credentials(if (usePrivate) PrivateNexusCredentialsFile else PublicNexusCredentialsFile)
}
