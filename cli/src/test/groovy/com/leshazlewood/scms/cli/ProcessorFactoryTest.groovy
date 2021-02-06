package com.leshazlewood.scms.cli

import com.leshazlewood.scms.core.DefaultProcessor
import com.leshazlewood.scms.core.ParallelProcessor
import org.junit.jupiter.api.Test

class ProcessorFactoryTest {

  @Test
  void 'processor factory returns default processor for 1'() {
    // when
    def processor = ProcessorFactory.makeProcessor(1)

    // then
    assert processor instanceof DefaultProcessor
    assert !(processor instanceof ParallelProcessor)
  }

  @Test
  void 'processor factory returns default processor for 0'() {
    // when
    def processor = ProcessorFactory.makeProcessor(0)

    // then
    assert processor instanceof DefaultProcessor
    assert !(processor instanceof ParallelProcessor)
  }

  @Test
  void 'processor factory returns default processor for 2'() {
    // when
    def processor = ProcessorFactory.makeProcessor(2)

    // then
    assert processor instanceof ParallelProcessor
  }
}
