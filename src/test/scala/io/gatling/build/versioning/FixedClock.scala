/*
 * Copyright 2011-2022 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.build.versioning

import java.time.{ Clock, Instant, ZoneId }

trait FixedClock {
  // 20210401041810 - 2021-04-01 04:18:10
  implicit val clock: Clock = Clock.fixed(Instant.ofEpochSecond(1617250690L), ZoneId.of("UTC"))
  val YYYY = 2021
  val WW = 13
  val YYYYMMDDHHMMSS = "20210401041810"
  val FIXED_FORMATTED_BEFORE_TIME = "20210315062255"
}
