/*
 * Copyright 2011-2024 GatlingCorp (https://gatling.io)
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec

class GatlingBumpSpec extends AnyWordSpec with Matchers with FixedClock {
  import GatlingBump._
  private val Reference = Table(
    // format: OFF
    ("oldVersion"                        , "patch"             , "minor"                   , "calver"      , "patch-milestone"                    , "minor-milestone"                          , "calver-milestone"),
    // stable
    ("1.0.0"                             , "1.0.1"             , "1.1.0"                   , s"$YYYY.$WW.0", s"1.0.1-M$YYYYMMDDHHMMSS"            , s"1.1.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    ("1.0.1"                             , "1.0.2"             , "1.1.0"                   , s"$YYYY.$WW.0", s"1.0.2-M$YYYYMMDDHHMMSS"            , s"1.1.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    ("1.2.3"                             , "1.2.4"             , "1.3.0"                   , s"$YYYY.$WW.0", s"1.2.4-M$YYYYMMDDHHMMSS"            , s"1.3.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"${YYYY - 1}.$WW.0"                , s"${YYYY - 1}.$WW.1", s"${YYYY - 1}.${WW + 1}.0", s"$YYYY.$WW.0", s"${YYYY - 1}.$WW.1-M$YYYYMMDDHHMMSS", s"${YYYY - 1}.${WW + 1}.0-M$YYYYMMDDHHMMSS", s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"$YYYY.$WW.0"                      , s"$YYYY.$WW.1"      , s"$YYYY.${WW + 1}.0"      , s"$YYYY.$WW.1", s"$YYYY.$WW.1-M$YYYYMMDDHHMMSS"      , s"$YYYY.${WW + 1}.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.1-M$YYYYMMDDHHMMSS"),
    // milestones
    ("1.0.0-M19401225154535"             , "1.0.0"             , "1.0.0"                   , s"$YYYY.$WW.0", s"1.0.0-M$YYYYMMDDHHMMSS"            , s"1.0.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    ("1.0.1-M19401225154535"             , "1.0.1"             , "1.1.0"                   , s"$YYYY.$WW.0", s"1.0.1-M$YYYYMMDDHHMMSS"            , s"1.1.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    ("1.2.3-M19401225154535"             , "1.2.3"             , "1.3.0"                   , s"$YYYY.$WW.0", s"1.2.3-M$YYYYMMDDHHMMSS"            , s"1.3.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"${YYYY - 1}.$WW.0-M19401225154535", s"${YYYY - 1}.$WW.0", s"${YYYY - 1}.$WW.0"      , s"$YYYY.$WW.0", s"${YYYY - 1}.$WW.0-M$YYYYMMDDHHMMSS", s"${YYYY - 1}.$WW.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"$YYYY.$WW.0-M19401225154535"      , s"$YYYY.$WW.0"      , s"$YYYY.$WW.0"            , s"$YYYY.$WW.0", s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"            , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"$YYYY.$WW.1-M19401225154535"      , s"$YYYY.$WW.1"      , s"$YYYY.${WW + 1}.0"      , s"$YYYY.$WW.1", s"$YYYY.$WW.1-M$YYYYMMDDHHMMSS"      , s"$YYYY.${WW + 1}.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.1-M$YYYYMMDDHHMMSS"),
    // snapshots same rules as milestones
    ("1.0.0-SNAPSHOT"                    , "1.0.0"             , "1.0.0"                   , s"$YYYY.$WW.0", s"1.0.0-M$YYYYMMDDHHMMSS"            , s"1.0.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    ("1.0.1-SNAPSHOT"                    , "1.0.1"             , "1.1.0"                   , s"$YYYY.$WW.0", s"1.0.1-M$YYYYMMDDHHMMSS"            , s"1.1.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    ("1.2.3-SNAPSHOT"                    , "1.2.3"             , "1.3.0"                   , s"$YYYY.$WW.0", s"1.2.3-M$YYYYMMDDHHMMSS"            , s"1.3.0-M$YYYYMMDDHHMMSS"                  , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"${YYYY - 1}.$WW.0-SNAPSHOT"       , s"${YYYY - 1}.$WW.0", s"${YYYY - 1}.$WW.0"      , s"$YYYY.$WW.0", s"${YYYY - 1}.$WW.0-M$YYYYMMDDHHMMSS", s"${YYYY - 1}.$WW.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"$YYYY.$WW.0-SNAPSHOT"             , s"$YYYY.$WW.0"      , s"$YYYY.$WW.0"            , s"$YYYY.$WW.0", s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"            , s"$YYYY.$WW.0-M$YYYYMMDDHHMMSS"),
    (s"$YYYY.$WW.1-SNAPSHOT"             , s"$YYYY.$WW.1"      , s"$YYYY.${WW + 1}.0"      , s"$YYYY.$WW.1", s"$YYYY.$WW.1-M$YYYYMMDDHHMMSS"      , s"$YYYY.${WW + 1}.0-M$YYYYMMDDHHMMSS"      , s"$YYYY.$WW.1-M$YYYYMMDDHHMMSS"),
    // format: ON
  )

  private def shouldBump(toGatlingVersion: String => GatlingVersion) =
    forAll(Reference) {
      (oldVersion: String, patch: String, minor: String, calVer: String, patchMilestone: String, minorMilestone: String, calVerMilestone: String) =>
        val oldGatlingVersion = toGatlingVersion(oldVersion)
        withClue("patch: ") {
          Patch.bump(oldGatlingVersion) shouldBe toGatlingVersion(patch)
        }
        withClue("minor: ") {
          Minor.bump(oldGatlingVersion) shouldBe toGatlingVersion(minor)
        }
        withClue("calver: ") {
          CalVer.bump(oldGatlingVersion) shouldBe toGatlingVersion(calVer)
        }
        withClue("patchMilestone: ") {
          milestone(Patch).bump(oldGatlingVersion) shouldBe toGatlingVersion(patchMilestone)
        }
        withClue("minorMilestone: ") {
          milestone(Minor).bump(oldGatlingVersion) shouldBe toGatlingVersion(minorMilestone)
        }
        withClue("calverMilestone: ") {
          milestone(CalVer).bump(oldGatlingVersion) shouldBe toGatlingVersion(calVerMilestone)
        }
    }

  "Simple version" in {
    shouldBump(str => GatlingVersion.apply(str).get)
  }

  "Custom marked version" in {
    shouldBump(str => GatlingVersion.apply(str).get.copy(marker = Some("CUSTOM")))
  }
}
