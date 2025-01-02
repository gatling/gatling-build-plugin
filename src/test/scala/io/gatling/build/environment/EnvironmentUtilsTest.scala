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

package io.gatling.build.environment

import java.io.File

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EnvironmentUtilsTest extends AnyFunSpec with Matchers {
  private val envs = EnvironmentUtils.readEnvFile(new File(getClass.getResource("environment-file").getFile)).get

  describe("A gatling environment file") {
    it("should be in VAR=VAL format") {
      envs.get("frontline.postgres.jdbc") shouldBe Some("jdbc:postgresql://localhost:5432/frontline?user=postgres&password=alreadychanged")
      envs.get("BLA") shouldBe Some("BLI")
      envs.get("question") shouldBe Some("answer")
      envs.get("antislash") shouldBe Some("\\ok")
      envs.get("FOO") shouldBe Some("$BAR")
      envs.get("REDIRECT_URL") shouldBe Some("https://auth.cloud.gatling.io/login#register")
    }

    it("should ignore lines beginning with #") {
      envs.get("commented") shouldBe None
      envs.get("#commented") shouldBe None
    }

    it("should ignore blank lines") {
      envs.get("") shouldBe None
    }

    it("should not handle quotation marks") {
      envs.get("doublequoted") shouldBe Some("\"doublequoted\"")
      envs.get("singlequoted") shouldBe Some("'singlequoted'")
    }
  }
}
