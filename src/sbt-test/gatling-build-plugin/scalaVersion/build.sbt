val SCALA_VERSION = "2.12.3"

scalaVersion in Global := SCALA_VERSION

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    TaskKey[Unit]("check") := {
      def assertEquals[T](msg: => String, expected: T, current: T) = if(current != expected) sys.error(msg + s" [expected: '${expected}', current: '${current}']")

      assertEquals("scalaVersion modified", SCALA_VERSION, scalaVersion.value)
    }
  )
