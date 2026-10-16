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
2. on tag push, CI runs `sbt "release with-defaults"` to build and publish

## GatlingCorpPlugin

The purpose is internal only.

## GatlingEnterpriseComponentPlugin

For closed source components published under the Gatling Enterprise Component License.

## License

This project is licensed under Apache License version 2, see [LICENSE.txt](./LICENSE.txt).
