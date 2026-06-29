// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
PluginDependencies.deps

// This project is its own plugin :)
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"
// Meta-build runs on Scala 3 (SBT 2); include the scala-3 source dir for version-specific helpers (e.g. LicenseHelper).
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala-3"
Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "resources"

Compile / unmanagedSources += baseDirectory.value / "project" / "PluginDependencies.scala"
