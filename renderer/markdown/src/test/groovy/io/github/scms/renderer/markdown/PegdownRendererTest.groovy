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
package io.github.scms.renderer.markdown

import static java.util.Collections.emptyMap

import io.github.scms.api.DefaultRenderRequest
import io.github.scms.api.DefaultResource
import org.junit.jupiter.api.Test

class PegdownRendererTest {

  static final def RENDERER = new MarkdownRendererFactory().create()

  @Test
  void 'test markdown produces html'() {
    // given
    def md = '''
             |Introduction
             |------------
             |
             |Welcome to Apache Shiro's 10 Minute Tutorial!
             |
             |By going through this quick and simple tutorial you should fully understand how a developer uses Shiro in their application. And you should be able to do it in under 10 minutes.
             |
             |<a name="10MinuteTutorial-Overview"></a>
             |Overview
             |--------
             |
             |What is Apache Shiro?
             '''.stripMargin()
    def resource = new DefaultResource("10-minute-tutorial", new StringReader(md))
    def writer = new StringWriter()
    def request = new DefaultRenderRequest(emptyMap(), resource, writer)

    // when
    RENDERER.render(request)

    // then
    def outHtml = writer.getBuffer().toString()
    assert outHtml.contains('<p>What is Apache Shiro?</p>')
    assert outHtml.contains('>Introduction</a></h2>')

  }
}
