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
package io.github.scms.utils

import static org.junit.jupiter.api.Assertions.assertThrows

import java.util.stream.Stream
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ThreadArgParserTest {

  static Stream<Arguments> provideThreadArgumentAndExpected() {
    def cores = ThreadArgParser.cores

    def pointNine = cores * 0.9f
    def pointNineInt = Math.round(pointNine) as int

    return Stream.of(
        Arguments.of("1", 1),
        Arguments.of("0", 1),
        Arguments.of("1.0", 1),
        Arguments.of("0.9", 1),
        Arguments.of("0.0", 1),
        Arguments.of("-0.0", 1),
        Arguments.of("15.0", 15),
        // per core tests
        Arguments.of("C", cores),
        Arguments.of("0C", 1),
        Arguments.of("2C", cores * 2),
        Arguments.of("-2C", 1),
        // per core decimal
        Arguments.of(".9C", pointNineInt),
        Arguments.of(".001C", 1),
        Arguments.of("0.001C", 1),
        )
  }

  @ParameterizedTest(name = 'parse {0} into {1}')
  @MethodSource("provideThreadArgumentAndExpected")
  void 'test thread count parsing'(String arg, int expected) {
    // when
    def threadCount = ThreadArgParser.parse(arg)

    // then
    assert threadCount == expected
  }

  static Stream<Arguments> provideInvalidThreadArgument() {
    def cores = ThreadArgParser.cores

    def pointNine = cores * 0.9f
    def pointNineInt = Math.round(pointNine) as int

    return Stream.of(
        Arguments.of(".", 1),
        // per core tests
        // per core decimal
        Arguments.of(".C", 1),
        )
  }

  @ParameterizedTest(name = 'parse {0} into IllegalArgumentException')
  @MethodSource("provideInvalidThreadArgument")
  void 'test invalid thread count parsing'(String arg) {
    // expect
    def exception = assertThrows(
        IllegalArgumentException.class,
        {ThreadArgParser.parse(arg)} as Executable
        )

    // then
    assert exception.message != null
  }
}
