package io.github.scms.core.config.groovy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable;

class GroovyConfigTest {

  static final def basePath = "/" + GroovyConfigTest.class.package.name.replaceAll("\\.", "/");

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
    assert config.getBaseModel().size() == 1
    assert config.getBaseModel().get('year') == '2021'

    assert config.forFile('index.adoc').model.get('year') == '2021'
    assert config.forFile('index.md').model.get('year') == '1910'
  }
}
