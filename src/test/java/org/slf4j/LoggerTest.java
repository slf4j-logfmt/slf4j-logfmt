package org.slf4j;

import org.junit.jupiter.api.Test;

class LoggerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTest.class);

  @Test
  void name() {
    MDC.put("foo", "bar");
    LOGGER.info("baz {}", "qux");
  }
}
