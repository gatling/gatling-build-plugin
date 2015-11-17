package io.gatling.build.license

import java.net.URL

object AllRightsReserved extends License {

  override val name = "All Rights Reserved"
  override val url = new URL("http://gatling.io")
  override val headers = Map("scala" -> scalaHeader)

  def scalaHeader = (
    javaStyleBlockComment,
    s"""/**
       | * Copyright 2011-$currentYear GatlingCorp (http://gatling.io)
       | *
       | * All rights reserved.
       | */
       |""".stripMargin
  )
}
