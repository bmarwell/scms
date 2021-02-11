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
package io.github.scms.renderer.freemarker

import io.github.scms.api.DefaultRenderRequest
import io.github.scms.api.DefaultResource
import org.junit.jupiter.api.Test

class FreemarkerFileRendererTest {

  static final def renderer = new FreemarkerRendererFactory().create()

  @Test
  void 'test rendering a simple example'() {
    def template = '''
                   |  <p>${exampleObject.name} by ${exampleObject.developer}</p>
                   |
                   |  <ul>
                   |    <#list systems as system>
                   |      <li>${system_index + 1}. ${system.name} from ${system.developer}</li>
                   |    </#list>
                   |  </ul>
                   |'''.stripMargin()
    def model = new HashMap()
    model.put('exampleObject', new ExampleObject(name: 'FreemarkerFileRendererTest', developer: 'Bob'))
    model.put('systems', [
      new System(name: 'scms', developer: 'lhazelwood'),
      new System(name: 'scms-github-action', developer: 'bmarwell'),
    ])

    def reader = new StringReader(template)
    def resource = new DefaultResource('helloworld.html', reader)
    def writer = new StringWriter()
    def request = new DefaultRenderRequest(model, resource, writer)

    // when
    renderer.render(request)

    // then
    def htmlOut = writer.getBuffer().toString()
    assert htmlOut.contains('scms from lhazelwood</li>')
    assert htmlOut.contains('scms-github-action from bmarwell</li>')
  }

  static class ExampleObject {
    String name
    String developer
  }

  static class System {
    String name
    String developer
  }
}
