package com.leshazlewood.scms.core

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

import java.nio.file.Files
import java.util.function.Function
import java.util.function.Supplier;

class DefaultProcessorTest {

  File sourceDir
  File destDir
  private String className
  private String testName

  @BeforeEach
  void setup(TestInfo testInfo) throws IOException {
    className = testInfo.testClass.orElseThrow({ new NoSuchElementException() } as Supplier).getSimpleName()
    testName = testInfo.testMethod.orElseThrow({ new NoSuchElementException() } as Supplier).getName()
    this.sourceDir = new File('target/test-classes/' + className + '_' + testName);
    String testMethodName =
        testInfo.getTestMethod().orElseThrow({ new NoSuchElementException() } as Supplier).getName();
    this.destDir = new File("target/tmp/", testMethodName).absoluteFile
    if (this.destDir.exists()) {
      Files.walk(this.destDir.toPath())
          .sorted(Comparator.reverseOrder())
          .forEach({ it -> it.toFile().delete() })

      Files.deleteIfExists(this.destDir.toPath());
    }

    boolean mkdirs = this.destDir.mkdirs();
    if (!mkdirs) {
      throw new IllegalStateException("Could not initialize tmp dir: " + destDir.getAbsolutePath());
    }
  }

  @Test
  void renderFileTest() {
    // given
    def defaultProcessor = new DefaultProcessor()
    defaultProcessor.sourceDir = this.sourceDir.absoluteFile
    defaultProcessor.destDir = this.destDir.absoluteFile
    defaultProcessor.init()

    def file = new File(sourceDir, "file.adoc").absoluteFile

    // when
    defaultProcessor.doRenderFile file
  }

}
