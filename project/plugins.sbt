// ------------------ //
// -- DEPENDENCIES -- //
// ------------------ //
PluginDependencies.deps

// This project is its own plugin :)
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"
Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "resources"

Compile / unmanagedSources += baseDirectory.value / "project" / "PluginDependencies.scala"
