val SCALA_VERSION = "2.13.12"

githubPath := "user/repository"

Global / scalaVersion := SCALA_VERSION

def assertEquals[T](msg: => String, expected: T, current: T) = if (current != expected) sys.error(msg + s" [expected: '$expected', current: '$current']")

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    name := "gatling-build-plugin/scalaVersion",
    TaskKey[Unit]("check") := {
      assertEquals("scalaVersion modified", SCALA_VERSION, scalaVersion.value)
    }
  )
