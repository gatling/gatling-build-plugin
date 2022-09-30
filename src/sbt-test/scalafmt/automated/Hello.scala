object Hello extends App {
  final implicit val a: Long = 0L
  val b: Int = { 1 } // Remove useless curly braces

  def min(p1: Int, p2: Int): Int = if(p1 < p2) {
    p1
  }
  else {
    p2
  }

  def remain(p1: Int, p2: Int): Boolean = try {
      p1 == p1 / p2 * p2
    } catch {
      case _: ArithmeticException => false
    }

  def withFor(n: Int): Seq[Int] = for{
    i <- 1 to n
  } yield { n }
}
