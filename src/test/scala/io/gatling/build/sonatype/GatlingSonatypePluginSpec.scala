/*
 * Copyright 2011-2025 GatlingCorp (https://gatling.io)
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

package io.gatling.build.sonatype

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GatlingSonatypePluginSpec extends AnyWordSpec with Matchers {
  "GatlingSonatypePlugin" when {
    "ensure publishable version" should {
      "accept 4.0.0" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0") should be(true)
      }
      "accept simple milestone" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-M1") should be(true)
      }
      "accept dated milestone" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-M20210401223248") should be(true)
      }
      "accept 4 components version number" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0.0") should be(true)
      }
      "accept 4 components version number with simple milestone" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0.0-M1") should be(true)
      }
      "accept 4 components version number with dated milestone" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0.0-M20210401223248") should be(true)
      }

      "refuse milestone without date" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-M") should be(false)
      }

      "refuse milestone like" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-M20210401223248abc") should be(false)
      }
      "refuse snapshot" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-SNAPSHOT") should be(false)
      }
      "refuse committed" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-2-abcdef12") should be(false)
      }
      "refuse dirty" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0-dirty-SNAPSHOT") should be(false)
      }
      "refuse private marker" in {
        GatlingSonatypePlugin.ensurePublishableVersion("4.0.0.PV") should be(false)
      }
    }
  }
}
