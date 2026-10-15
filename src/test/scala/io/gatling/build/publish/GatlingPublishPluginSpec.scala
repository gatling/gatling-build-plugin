/*
 * Copyright 2011-2026 GatlingCorp (https://gatling.io)
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

import io.gatling.build.publish.GatlingPublishPlugin.PublishRoute

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GatlingPublishPluginSpec extends AnyWordSpec with Matchers {
  private def publicRoute(version: String) = GatlingPublishPlugin.route(publicArtifact = true, version, snapshot = false)
  private def internalRoute(version: String) = GatlingPublishPlugin.route(publicArtifact = false, version, snapshot = false)

  "GatlingPublishPlugin" when {
    "routing a public artifact" should {
      "publish 4.0.0 to staging" in {
        publicRoute("4.0.0") shouldBe Some(PublishRoute.Staging)
      }
      "publish a 4 components version number to staging" in {
        publicRoute("4.0.0.0") shouldBe Some(PublishRoute.Staging)
      }
      "publish a simple milestone to staging" in {
        publicRoute("4.0.0-M1") shouldBe Some(PublishRoute.Staging)
      }
      "publish a dated milestone to staging" in {
        publicRoute("4.0.0-M202610141200") shouldBe Some(PublishRoute.Staging)
      }
      "publish a 4 components version number with a milestone to staging" in {
        publicRoute("4.0.0.0-M1") shouldBe Some(PublishRoute.Staging)
      }
      "publish a private marker version to releases" in {
        publicRoute("4.0.0.FL") shouldBe Some(PublishRoute.Releases)
      }
      "publish a private marker milestone to milestones" in {
        publicRoute("4.0.0.FL-M1") shouldBe Some(PublishRoute.Milestones)
      }
      "publish a milestone without number to releases" in {
        publicRoute("4.0.0-M") shouldBe Some(PublishRoute.Releases)
      }
      "never publish a snapshot" in {
        GatlingPublishPlugin.route(publicArtifact = true, "4.0.0+2-abcdef12-SNAPSHOT", snapshot = true) shouldBe None
      }
    }

    "routing an internal artifact" should {
      "publish 4.0.0 to releases" in {
        internalRoute("4.0.0") shouldBe Some(PublishRoute.Releases)
      }
      "publish a milestone to milestones" in {
        internalRoute("4.0.0-M202610141200") shouldBe Some(PublishRoute.Milestones)
      }
      "publish a private marker version to releases" in {
        internalRoute("4.0.0.FL") shouldBe Some(PublishRoute.Releases)
      }
      "never publish a snapshot" in {
        GatlingPublishPlugin.route(publicArtifact = false, "4.0.0+2-abcdef12-SNAPSHOT", snapshot = true) shouldBe None
      }
    }
  }
}
