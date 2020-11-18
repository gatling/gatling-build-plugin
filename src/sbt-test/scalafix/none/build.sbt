scalaVersion := "2.12.3"

def assertContentsEqual(file: File, expected: String): Unit = {
  val obtained =
    scala.io.Source.fromFile(file).getLines().mkString("\n")

  if (obtained.trim != expected.trim) {
    val msg =
      s"""File: $file
         |Obtained output:
         |$obtained
         |Expected:
         |$expected
         |""".stripMargin
    System.err.println(msg)
    throw new Exception(msg)
  }
}

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    TaskKey[Unit]("check") := {
      // imports should NOT have be sorted
      assertContentsEqual(file("src/main/scala/Hello.scala"),
        """
          |import scala.io.Source
          |import java.util.Random
          |
          |object Hello extends App {
          |  val src = Source.fromString("Hello").mkString("\n")
          |  val random = (new Random()).nextInt(100)
          |  println(src + random)
          |}
          |
          |""".stripMargin)
    }
  )
