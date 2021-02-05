package io.github.scms.utils;

import org.junit.jupiter.api.Test

class FileUtilsTest {

  @Test
  void 'file in subdirectory should return two full stops'() {
    // when
    String relativeDirectoryPath = FileUtils.relativize 'templates/default.vtl'

    // then
    assert relativeDirectoryPath == '..'
  }

  @Test
  void 'file without dirs should return full stop'() {
    // when
    String relativeDirectoryPath = FileUtils.relativize 'file.adoc'

    // then
    assert relativeDirectoryPath == '.'
  }

  @Test
  void 'file in explicit local directory should return full stop'() {
    // when
    String relativeDirectoryPath = FileUtils.relativize './file.adoc'

    // then
    assert relativeDirectoryPath == '.'
  }

  @Test
  void 'multiple local directories should return full stop'() {
    // when
    String relativeDirectoryPath = FileUtils.relativize '././././file.adoc'

    // then
    assert relativeDirectoryPath == '.'
  }

  @Test
  void 'test mixed path of local directory and subdirectories'() {
    // when
    String relativeDirectoryPath = FileUtils.relativize './dir/././file.adoc'

    // then
    assert relativeDirectoryPath == '..'
  }

}
