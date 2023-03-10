package org.slf4j;

import org.junit.jupiter.api.Test;

class LoggerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTest.class);

  @Test
  void info() {
    MDC.put("foo", "bar");
    LOGGER.info("baz {}", "qux");
  }

  @Test
  void flood() {
    for (int i = 0; i < 1_000_000; i++) {
      LOGGER.info("baz {}", "qux");
    }
  }
}
