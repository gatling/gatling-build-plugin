package io.gatling.build

import java.util.Calendar

import de.heikoseeberger.sbtheader._

package object license {

  private def currentYear = Calendar.getInstance.get(Calendar.YEAR)

  val ApacheV2License = Some(License.Custom(
    s"""|Copyright 2011-$currentYear GatlingCorp (https://gatling.io)
        |
        |Licensed under the Apache License, Version 2.0 (the "License");
        |you may not use this file except in compliance with the License.
        |You may obtain a copy of the License at
        |
        | http://www.apache.org/licenses/LICENSE-2.0
        |
        |Unless required by applicable law or agreed to in writing, software
        |distributed under the License is distributed on an "AS IS" BASIS,
        |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        |See the License for the specific language governing permissions and
        |limitations under the License.""".stripMargin
  ))

  val GatlingHighChartsLicense = Some(License.Custom(
    s"""|Copyright 2011-$currentYear GatlingCorp (https://gatling.io)
        |
        |Licensed under the Gatling Highcharts License""".stripMargin
  ))

  val AllRightsReservedLicense = Some(License.Custom(
    s"""|Copyright 2011-$currentYear GatlingCorp (https://gatling.io)
        |
        |All rights reserved.""".stripMargin
  ))
}
