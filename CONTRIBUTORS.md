# gatling-build-plugin tips and tricks

<a id="other_maven_repo"></a>
## Using another maven repository

`publishTo` is computed by the plugins: define the repositories instead, in a global `.sbt` file, see [Publishing repositories](./README.md#publishing-repositories).

As this file lies in your global SBT configuration, there is no means to commit it by mistake.

## PGP configuration

`~/.sbt/1.0/plugins/gpg.sbt`:

```scala
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.2")
```

`~/.sbt/1.0/gpg.sbt`:

```scala
credentials += Credentials(
  "GPG sign key", // (1)
  "pgp", // (2)
  "5470DFCDA4CB06EE1890118B28BE55DFA0E95B3D", // (3)
  "ignored" // (4)
)

useGpgPinentry := true
useGpgAgent := false
```

1. Must be unique
2. Must be `pgp`
3. The key id to use
4. The passphrase will lie in `PGP_PASSPHRASE` environment variable.

For more information, see [sbt-pgp](https://github.com/sbt/sbt-pgp).

## IntelliJ IDEA configuration

### sbt-test

IntelliJ doesn't derive sbt-test projects as SBT projects.
Add each module as a project:

- File / New / Module from Existing Sources

Then, IntelliJ doesn't reach `plugin.version` property, go to the SBT tab on the right:

- Click on "sbt Settings" (wrench)
- Set VM parameters to `-Dplugin.version=X.Y.Z-SNAPSHOT` where `X.Y.Z-SNAPSHOT` is the version in root `version.sbt`
- Reload
