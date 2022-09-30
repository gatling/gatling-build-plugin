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

package io.gatling.build.environment

import java.io.File

import scala.io.Source
import scala.util.{ Try, Using }

object EnvironmentUtils {
  private val LINE_REGEX =
    """(?xms)
       (?:^|\A)           # start of line
       (                  # start variable name (captured)
         [a-zA-Z_]        # single alphabetic or underscore character
         [a-zA-Z0-9_.-]*  # zero or more alphnumeric, underscore, period or hyphen
       )                  # end variable name (captured)
       (?:=)              # assignment
       (                  # start variable value (captured)
         [^\r\n]*         # variable
       )                  # end variable value (captured)
       (?:$|\z)           # end of line
    """.r

  /**
   * Expects each line in an env file to be in VAR=VAL format. Lines beginning with # are processed as comments and ignored. Blank lines are ignored. There is
   * no special handling of quotation marks. This means that they are part of the VAL.
   *
   * @param file
   *   the env file
   * @return
   *   all env value as a map
   */
  def readEnvFile(file: File): Try[Map[String, String]] =
    Using(Source.fromFile(file)) { source =>
      readEnv(source.mkString)
    }

  def readEnv(string: String) =
    LINE_REGEX.findAllMatchIn(string).map(kv => (kv.group(1), kv.group(2))).toMap
}
