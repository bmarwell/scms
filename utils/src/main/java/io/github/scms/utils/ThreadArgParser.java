/*
 * Copyright 2021 Les Hazlewood, scms contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.scms.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ThreadArgParser {

  private static final Pattern CORE_PATTERN_MATCHER =
      Pattern.compile("^(?<num>(-)?[0-9]*(\\.[0-9]*)?)(?<percore>C)?$");

  private ThreadArgParser() {
    // util
  }

  public static int parse(String threadArg) {
    Matcher matcher = CORE_PATTERN_MATCHER.matcher(threadArg);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          "Thread arg must comply to " + CORE_PATTERN_MATCHER.pattern());
    }

    float count = 1.0f;
    String num = matcher.group("num");
    if (num != null && !num.isEmpty()) {
      if (num.equals(".")) {
        throw new IllegalArgumentException("you must specify a valid number!");
      }
      count = Float.parseFloat(num);
    }

    String perCore = matcher.group("percore");
    if (perCore == null || perCore.isEmpty()) {
      return toIntMin1(count);
    }

    float wanted = count * getCores();

    return toIntMin1(wanted);
  }

  public static int toIntMin1(float wanted) {
    if (wanted < 1.0) {
      return 1;
    }

    return Math.round(wanted);
  }

  public static int getCores() {
    return Runtime.getRuntime().availableProcessors();
  }
}
