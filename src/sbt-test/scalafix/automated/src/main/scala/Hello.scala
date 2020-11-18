import scala.io.Source
import java.util.Random

object Hello extends App {
  val src = Source.fromString("Hello").mkString("\n")
  val random = (new Random()).nextInt(100)
  println(src + random)
}
