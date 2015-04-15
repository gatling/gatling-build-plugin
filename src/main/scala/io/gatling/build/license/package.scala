package io.gatling.build

import java.util.Calendar

import scala.util.matching.Regex

package object license {

  type HeadersMap = Map[String, (Regex, String)]

  val javaStyleBlockComment = """(?s)(/\*(?:\*?).*?\*/(?:\n|\r|\r\n)?)(.*)""".r

  def currentYear = Calendar.getInstance().get(Calendar.YEAR)
}
