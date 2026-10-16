# gatling-build-plugin

![build](https://github.com/gatling/gatling-build-plugin/actions/workflows/build.yml/badge.svg?branch=main)

SBT plugins to share common settings across Gatling's projects' builds.

Supports both sbt 1.x and sbt 2.x.

## GatlingOssPlugin

Common settings for OSS project in `io.gatling` group.

### How to use

`build.sbt`:

```scala
inScope(Global) {
  Seq(
    githubPath := "gatling/my-awesome-project", // (1)
    gatlingDevelopers := Seq( // (2)
      GatlingDeveloper("jsmith@gatling.io", "John Smith", isGatlingCorp = false)
    )
  )
}

lazy val root = (project in file("."))
  .enablePlugins(GatlingOssPlugin)
  .settings(
    name := "my-awesome-project" // (3)
  )
```

1. github path for `pom.xml` SCM info
2. developers and contributors for `pom.xml` developers
3. artifact name

### Versioning

The version is computed from git tags with [sbt-dynver](https://github.com/sbt/sbt-dynver) (`v` prefixed tags, e.g. `v1.2.3`), so no `version.sbt` is needed.

### Releasing

Releases are driven by tags:

1. the "Create release" workflow computes the next version with [version-bump](https://github.com/gatling/internal-actions/tree/main/version-bump) and pushes the matching `v<version>` git tag
2. on tag push, CI runs `sbt "test; gatlingPublish"` (`+gatlingPublish` for a cross-built project), see [Publishing a release](#publishing-a-release)

## GatlingCorpPlugin

The purpose is internal only.

## GatlingEnterpriseComponentPlugin

For closed source components published under the Gatling Enterprise Component License.

## Publishing repositories

`publish` and `publishSigned` deploy with [sbt-aether-deploy](https://github.com/arktekk/sbt-aether-deploy), which also uploads `maven-metadata.xml`, as Maven does.
Public artifacts are signed, internal ones (`GatlingCorpPlugin`) are not.

`publishTo` is computed from the kind of artifact and its version:

| Artifact | Version | Repository |
|---|---|---|
| any | snapshot | none: never published |
| public (`GatlingOssPlugin`, `GatlingEnterpriseComponentPlugin`) | `x.y.z` or `x.y.z.w`, optionally followed by `-M<n>` | `gatlingStagingRepository` |
| any other | ending with `-M<n>` | `gatlingMilestonesRepository` |
| any other | any other | `gatlingReleasesRepository` |

These repositories depend on the environment (CI, local machine...), so they all default to `None` and must be provided, typically by a global `.sbt` file (`~/.sbt/1.0/` for sbt 1, `~/.config/sbt/2/` for sbt 2).
Global `.sbt` files do not see the keys of the build's plugins: redeclare them with the same name and type.

```scala
val gatlingStagingRepository = settingKey[Option[Resolver]]("")
val gatlingMilestonesRepository = settingKey[Option[Resolver]]("")
val gatlingReleasesRepository = settingKey[Option[Resolver]]("")

Global / gatlingStagingRepository := Some("staging" at "https://repository.example.com/maven/staging")
Global / gatlingMilestonesRepository := Some("milestones" at "https://repository.example.com/maven/milestones")
Global / gatlingReleasesRepository := Some("releases" at "https://repository.example.com/maven/releases")

credentials += Credentials("realm", "repository.example.com", "user", "password")
```

## Publishing a release

`gatlingPublish` publishes the version of the current git tag, and is meant to run in CI on tag push:

1. projects with `publish / skip := true` or `Compile / publishArtifact := false` publish nothing, whatever the git state
2. a modified working tree (tracked files) fails, and nothing is published
3. if HEAD is not a release tag, nothing is published
4. otherwise, it fails if the repository is not defined or on snapshot dependencies, then runs `publishSigned` for public artifacts or `publish` for internal ones

Projects without these plugins, such as aggregators, publish nothing.

## License

This project is licensed under Apache License version 2, see [LICENSE.txt](./LICENSE.txt).
