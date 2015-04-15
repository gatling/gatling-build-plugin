package io.gatling.build.license

import java.net.URL

object ApacheV2 extends License {

  override val name = "Apache 2"
  override val url = new URL("http://www.apache.org/licenses/LICENSE-2.0.html")
  override val headers = Map("scala" -> scalaHeader)

  def scalaHeader = (javaStyleBlockComment,
    s"""/**
       | * Copyright 2011-$currentYear eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
       | *
       | * Licensed under the Apache License, Version 2.0 (the "License");
       | * you may not use this file except in compliance with the License.
       | * You may obtain a copy of the License at
       | *
       | *  http://www.apache.org/licenses/LICENSE-2.0
       | *
       | * Unless required by applicable law or agreed to in writing, software
       | * distributed under the License is distributed on an "AS IS" BASIS,
       | * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       | * See the License for the specific language governing permissions and
       | * limitations under the License.
       | */
       |""".stripMargin)
}
