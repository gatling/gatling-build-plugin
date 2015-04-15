package io.gatling.build.license

import java.net.URL

object HighCharts extends License {

  override val name = "Gatling Highcharts License"
  override val url = new URL("https://raw.github.com/gatling/gatling-highcharts/master/src/main/resources/META-INF/LICENSE")
  override val headers = Map("scala" -> scalaHeader)

  def scalaHeader = (javaStyleBlockComment,
    s"""/**
       | * Copyright 2011-$currentYear eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
       | *
       | * Licensed under the Gatling Highcharts License
       | */
       |""".stripMargin)
}
