gatling-build-plugin
=====================

A SBT plugin to share common settings across Gatling's projects' builds


## IntelliJ IDEA configuration

### sbt-test

IntelliJ doesn't derivate sbt-test projects as SBT projects.
Add each module as a project:
- File / New / Module from Existing Sources

Then, IntelliJ doesn't reach plugin.version property, go to the SBT tab on the right:
- Click on `sbt Settings` (wrench)
- Set VM parameters to `-Dplugin.version=[version in root version.sbt]`
- Reload
