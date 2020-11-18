/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
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

package io.gatling.build.publish

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GatlingVersionSpec extends AnyWordSpec with Matchers {
  "A version" when {
    "minor" should {
      val version = GatlingVersion("1.12.0").get

      "be a minor" in {
        version.isMinor shouldBe true
      }

      "not be a patch" in {
        version.isPatch shouldBe false
      }

      "have a branch name" in {
        version.branchName shouldBe "1.12"
      }
    }

    "patch" should {
      val version = GatlingVersion("1.12.1").get

      "not be a minor" in {
        version.isMilestone shouldBe false
      }

      "be a patch" in {
        version.isPatch shouldBe true
      }

      "have a branch name" in {
        version.branchName shouldBe "1.12"
      }
    }

    "specific minor" should {
      val version = GatlingVersion("3.4.0.CUSTOMER").get

      "be a minor" in {
        version.isMinor shouldBe true
      }

      "not be a patch" in {
        version.isPatch shouldBe false
      }

      "have a branch name" in {
        version.branchName shouldBe "3.4"
      }

      "bump" in {
        version.bumpPatch.string shouldBe "3.4.1.CUSTOMER"
      }

      "asSnapshot" in {
        version.asSnapshot.string shouldBe "3.4.0.CUSTOMER-SNAPSHOT"
      }

      "asMilestone" in {
        version.asMilestone.string should startWith("3.4.0.CUSTOMER-M")
      }
    }

    "a snapshot with marker" should {
      val version = GatlingVersion("3.4.1.CUSTOM-SNAPSHOT").get

      "be well parsed" in {
        version shouldBe GatlingVersion(3, 4, 1, Some(".CUSTOM"), Some("-SNAPSHOT"))
      }

      "remove qualifier" in {
        version.withoutQualifier.string shouldBe "3.4.1.CUSTOM"
      }
    }

    "a snapshot without marker" should {
      val version = GatlingVersion("3.4.1-SNAPSHOT").get

      "be well parsed" in {
        version shouldBe GatlingVersion(3, 4, 1, None, Some("-SNAPSHOT"))
      }

      "remove qualifier" in {
        version.withoutQualifier.string shouldBe "3.4.1"
      }
    }
  }
}
