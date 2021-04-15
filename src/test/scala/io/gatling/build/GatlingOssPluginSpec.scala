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

package io.gatling.build

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GatlingOssPluginSpec extends AnyWordSpec with Matchers {
  "GatlingOssPlugin" when {
    "ensure stable version" should {
      "agree 4.0.0" in {
        GatlingOssPlugin.ensureStableVersion("4.0.0") should be(true)
      }
      "disagree milestone" in {
        GatlingOssPlugin.ensureStableVersion("4.0.0-M20210401223248") should be(false)
      }
      "disagree snapshot" in {
        GatlingOssPlugin.ensureStableVersion("4.0.0-SNAPSHOT") should be(false)
      }
      "disagree committed" in {
        GatlingOssPlugin.ensureStableVersion("4.0.0-2-abcdef12") should be(false)
      }
      "disagree dirty" in {
        GatlingOssPlugin.ensureStableVersion("4.0.0-dirty-SNAPSHOT") should be(false)
      }
      "disagree private marker" in {
        GatlingOssPlugin.ensureStableVersion("4.0.0.PV") should be(false)
      }
    }
  }
}
