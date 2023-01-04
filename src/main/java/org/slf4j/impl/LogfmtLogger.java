package org.slf4j.impl;

import static org.slf4j.event.EventConstants.*;
import static org.slf4j.event.Level.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

public final class LogfmtLogger implements Logger {
  private static final Level LEVEL =
      Level.valueOf(System.getenv().getOrDefault("LOG_LEVEL", "info").toUpperCase());
  private static final boolean ERROR_ENABLED = LEVEL.toInt() >= ERROR_INT;
  private static final boolean WARN_ENABLED = LEVEL.toInt() >= WARN_INT;
  private static final boolean INFO_ENABLED = LEVEL.toInt() >= INFO_INT;
  private static final boolean DEBUG_ENABLED = LEVEL.toInt() >= DEBUG_INT;
  private static final boolean TRACE_ENABLED = LEVEL.toInt() > TRACE_INT;
  private static final Object[] NONE = new Object[0];
  private final String name;

  private static final int queueSize =
      Integer.parseInt(System.getenv().getOrDefault("LOG_QUEUE_SIZE", "256"));
  private static final BlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(queueSize);

  private static final String POISON = "";

  static {
    new Thread(
            () -> {
              String take = null;
              //noinspection StringEquality
              do {
                try {
                  take = QUEUE.take();
                  //noinspection StringEquality
                  if (take != POISON) System.out.println(take);
                } catch (InterruptedException e) {
                  // noop
                }
              } while (take != POISON);
            })
        .start();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    QUEUE.put(POISON);
                  } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                  }
                }));
  }

  public LogfmtLogger(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isTraceEnabled() {
    return TRACE_ENABLED;
  }

  @Override
  public void trace(String msg) {
    println(TRACE, msg, NONE);
  }

  @Override
  public void trace(String format, Object arg) {
    println(TRACE, format, new Object[] {arg});
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    println(TRACE, format, new Object[] {arg1, arg2});
  }

  @Override
  public void trace(String format, Object... arguments) {
    println(TRACE, format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    println(TRACE, msg, new Throwable[] {t});
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return isTraceEnabled();
  }

  @Override
  public void trace(Marker marker, String msg) {
    println(TRACE, msg, NONE);
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    println(TRACE, format, new Object[] {arg});
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    println(TRACE, format, new Object[] {arg1, arg2});
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    println(TRACE, format, argArray);
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    println(TRACE, msg, new Throwable[] {t});
  }

  @Override
  public boolean isDebugEnabled() {
    return DEBUG_ENABLED;
  }

  private void println(Level level, String msg, Object[] args) {
    if (level.toInt() < LEVEL.toInt()) {
      return;
    }
    StringBuilder out = new StringBuilder();
    out.append("time=");
    out.append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
    out.append(" level=");
    out.append(level.toString().toLowerCase());
    out.append(" thread=\"");
    out.append(Thread.currentThread().getName().replace("\"", "\\\""));
    out.append("\"");
    out.append(" logger=\"");
    out.append(name);
    out.append("\"");
    out.append(" msg=\"");
    out.append(MessageFormatter.arrayFormat(msg, args).getMessage().replace("\"", "\\\""));
    out.append("\"");
    Map<String, String> contexMap = MDC.getCopyOfContextMap();
    if (contexMap != null) {
      for (Map.Entry<String, String> e : contexMap.entrySet()) {
        out.append(" ");
        out.append(e.getKey());
        out.append("=\"");
        out.append(String.valueOf(e.getValue()).replace("\"", "\\\""));
        out.append("\"");
      }
    }
    try {
      QUEUE.put(out.toString());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void debug(String msg) {
    println(DEBUG, msg, NONE);
  }

  @Override
  public void debug(String format, Object arg) {
    println(DEBUG, format, new Object[] {arg});
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    println(DEBUG, format, new Object[] {arg1, arg2});
  }

  @Override
  public void debug(String format, Object... arguments) {
    println(DEBUG, format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    println(DEBUG, msg, new Throwable[] {t});
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return DEBUG_ENABLED;
  }

  @Override
  public void debug(Marker marker, String msg) {
    println(DEBUG, msg, NONE);
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    println(DEBUG, format, new Object[] {arg});
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    println(DEBUG, format, new Object[] {arg1, arg2});
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    println(DEBUG, format, arguments);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    println(DEBUG, msg, new Throwable[] {t});
  }

  @Override
  public boolean isInfoEnabled() {
    return INFO_ENABLED;
  }

  @Override
  public void info(String msg) {
    println(INFO, msg, NONE);
  }

  @Override
  public void info(String format, Object arg) {
    println(INFO, format, new Object[] {arg});
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    println(INFO, format, new Object[] {arg1, arg2});
  }

  @Override
  public void info(String format, Object... arguments) {
    println(INFO, format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    println(INFO, msg, new Throwable[] {t});
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return INFO_ENABLED;
  }

  @Override
  public void info(Marker marker, String msg) {
    println(INFO, msg, NONE);
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    println(INFO, format, new Object[] {arg});
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    println(INFO, format, new Object[] {arg1, arg2});
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    println(INFO, format, arguments);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    println(INFO, msg, new Throwable[] {t});
  }

  @Override
  public boolean isWarnEnabled() {
    return WARN_ENABLED;
  }

  @Override
  public void warn(String msg) {
    println(INFO, msg, NONE);
  }

  @Override
  public void warn(String format, Object arg) {
    println(INFO, format, new Object[] {arg});
  }

  @Override
  public void warn(String format, Object... arguments) {
    println(INFO, format, arguments);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    println(INFO, format, new Object[] {arg1, arg2});
  }

  @Override
  public void warn(String msg, Throwable t) {
    println(INFO, msg, new Throwable[] {t});
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return WARN_ENABLED;
  }

  @Override
  public void warn(Marker marker, String msg) {
    println(INFO, msg, NONE);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    println(INFO, format, new Object[] {arg});
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    println(INFO, format, new Object[] {arg1, arg2});
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    println(INFO, format, arguments);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    println(INFO, msg, new Throwable[] {t});
  }

  @Override
  public boolean isErrorEnabled() {
    return ERROR_ENABLED;
  }

  @Override
  public void error(String msg) {
    println(INFO, msg, NONE);
  }

  @Override
  public void error(String format, Object arg) {
    println(INFO, format, new Object[] {arg});
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    println(INFO, format, new Object[] {arg1, arg2});
  }

  @Override
  public void error(String format, Object... arguments) {
    println(INFO, format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    println(INFO, msg, new Throwable[] {t});
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return ERROR_ENABLED;
  }

  @Override
  public void error(Marker marker, String msg) {
    println(INFO, msg, NONE);
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    println(INFO, format, new Object[] {arg});
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    println(INFO, format, new Object[] {arg1, arg2});
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    println(INFO, format, arguments);
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    println(INFO, msg, new Throwable[] {t});
  }
}
