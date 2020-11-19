scalaVersion := "2.12.3"

def assertEquals[T](msg: => String, expected: T, current: T) = if(current != expected) sys.error(msg + s" [expected: '${expected}', current: '${current}']")

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    TaskKey[Unit]("check") := {
      assertEquals("organization should be set", "io.gatling", organization.value)
      assertEquals("homepage should be set", Some(url("https://gatling.io")), homepage.value)
      assertEquals("organization homepage should be set", Some(url("https://gatling.io")), organizationHomepage.value)
      assertEquals("startYear should be set",   Some(2011), startYear.value)
    }
  )
