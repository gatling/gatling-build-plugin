# Changelog

## 3.0.0

First version with this file

 * Remove `scalaVersion` management
 * Remove the scalafmt plugin
 * Disable auto trigger for Scalafix. Projects have to enable `GatlingAutomatedScalafixPlugin`.
 * Remove the dependencies to sort-import (scalafix dependencies)
 * GatlingBasicInfoPlugin to set common (informational) settings
 * GatlingCompilerSettingsPlugin to set javac and scalac options