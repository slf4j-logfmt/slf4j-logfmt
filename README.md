# slf4j-logfmt

An implementation of `logfmt` and [Semantic Logging Spec](https://github.com/semantic-logs/spec) for Java SLF4J v1.

SLF4J v2 is not yet more popular than v1.

Benefits:

* Fast - simple formatting.
* Concise - 30% smaller than JSON.
* Simple.
* Structured and therefore parseable.

```java
class LoggerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTest.class);

  @Test
  void info() {
    MDC.put("foo", "bar");
    LOGGER.info("baz {}", "qux");
  }
}
```

Output:

```
time=2023-01-04T11:18:43-08:00 level=info thread="main" msg="baz qux" foo="bar"
```