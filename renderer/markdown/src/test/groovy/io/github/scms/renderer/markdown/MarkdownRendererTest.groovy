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

class MarkdownRendererTest {

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
             |
             |<input type="hidden" id="ghEditPage" value="10-minute-tutorial.md.vtl"></input>
             '''.stripMargin()
    def resource = new DefaultResource("10-minute-tutorial", new StringReader(md))
    def writer = new StringWriter()
    def request = new DefaultRenderRequest(emptyMap(), resource, writer)

    // when
    RENDERER.render(request)

    // then
    def outHtml = writer.getBuffer().toString()
    assert outHtml.contains('<p>What is Apache Shiro?</p>')
    assert outHtml.contains('>Introduction</h2>')
  }

  @Test
  void 'test rendering tables'() {
    // given
    def md = '''
             |Import the Shiro Spring configurations:
             |
             |``` java
             |@Configuration
             |@Import({ShiroBeanConfiguration.class,
             |         ShiroConfiguration.class,
             |         ShiroAnnotationProcessorConfiguration.class})
             |public class CliAppConfig {
             |   ...
             |}
             |```
             |
             |The above configurations do the following:
             |
             || Configuration Class | Description |
             || ------------------- | ----------- |
             || org.apache.shiro.spring.config.ShiroBeanConfiguration | Configures Shiro's lifecycle and events |
             || org.apache.shiro.spring.config.ShiroConfiguration | Configures Shiro Beans (SecurityManager, SessionManager, etc)  |
             || org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration | Enables Shiro's annotation processing |
             |'''.stripMargin()
    def resource = new DefaultResource("10-minute-tutorial", new StringReader(md))
    def writer = new StringWriter()
    def request = new DefaultRenderRequest(emptyMap(), resource, writer)

    // when
    // when
    RENDERER.render(request)

    // then
    def outHtml = writer.getBuffer().toString()
    assert outHtml.contains('<p>The above configurations do the following:</p>')
    assert outHtml.contains('<td>org.apache.shiro.spring.config.ShiroBeanConfiguration</td>')
  }
}
