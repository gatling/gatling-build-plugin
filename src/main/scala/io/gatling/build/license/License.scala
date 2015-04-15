package io.gatling.build.license

import java.net.URL

trait License {
  def name: String
  def url: URL
  def headers: HeadersMap

  override lazy val toString =
    super.getClass.getSimpleName.replace("$", "")
}
