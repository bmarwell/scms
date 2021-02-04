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
package io.github.scms.renderer.asciidoc

import static java.util.Collections.emptyMap
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.matchesPattern
import static org.hamcrest.core.StringContains.containsString
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.mockito.Mockito.CALLS_REAL_METHODS
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.when
import static org.mockito.Mockito.withSettings

import io.github.scms.api.DefaultRenderRequest
import io.github.scms.api.DefaultResource
import java.nio.charset.StandardCharsets
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.mockito.Mockito
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AsciiDocRendererTest {

  private static final AsciiDoctorRenderer renderer = new AsciiDoctorRenderer(Asciidoctor.Factory.create())

  @BeforeAll
  static void setup() {
  }

  @Test
  void testRendererNull() {
    // given
    def reader = new InputStreamReader(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)))
    def writer = mock Writer.class
    def resource = new DefaultResource("test", reader)
    def request = new DefaultRenderRequest(emptyMap(), resource, writer)

    // expect
    assertThrows(
        NoSuchElementException.class,
        { renderer.render(request) } as Executable
        )
  }

  @Test
  void testRendererHeading() {
    // given
    def input = "== Heading 2\n"
    def reader = new StringReader(input)
    def writer = new StringWriter()
    def resource = new DefaultResource("heading", reader)
    def request = new DefaultRenderRequest(emptyMap(), resource, writer)

    // when
    renderer.render(request)

    // then
    def outHtml = writer.getBuffer().toString()
    assertThat(outHtml, containsString(">Heading 2</h2>"))
  }

  def <T> T verboseSpy(T realObject) {
    return mock(realObject.class, withSettings()
        .verboseLogging()
        .spiedInstance(realObject)
        .defaultAnswer(CALLS_REAL_METHODS))
  }

}
