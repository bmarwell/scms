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
package io.github.scms.core.config.groovy

import static java.util.stream.Collectors.toSet

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class GroovyConfigTest {

  static final def basePath = "/" + GroovyConfigTest.class.package.name.replaceAll("\\.", "/")

  @Test
  void 'test exception on invalid config'() {
    // given
    def file = getClass().getResource(basePath + "/invalid_not_map.groovy").toURI()

    // expect
    Assertions.assertThrows(
        IllegalArgumentException.class,
        { new GroovyFileConfig(file) } as Executable
        )
  }

  @Test
  void 'test empty config'() {
    // given
    def file = getClass().getResource(basePath + '/empty.groovy').toURI()

    // when
    def config = new GroovyFileConfig(file)

    // then
    assert config.asMap().isEmpty()
  }

  @Test
  void 'pattern model should overwrite base model'() {
    // given
    def file = getClass().getResource(basePath + '/pattern_with_model.groovy').toURI()

    // when
    def config = new GroovyFileConfig(file)

    // then
    assert config.getBaseModel().size() == 2
    assert config.getBaseModel().get('year') == '2021'

    assert config.forFile('index.adoc').model.get('year') == '2021'
    assert config.forFile('index.md').model.get('year') == '1910'
  }

  @Test
  void 'links from base model should be found'() {
    given:
    def file = getClass().getResource(basePath + '/pattern_with_model.groovy').toURI()

    when:
    def config = new GroovyFileConfig(file)

    then:
    assert config.getBaseModel().size() == 2
    def links = config.getBaseModel().get('links')
    assert links != null
    assert links instanceof Collection
    def linkCol = links as Collection
    linkCol.forEach{ link -> assert link.target != null && link.name != null }
  }
}
