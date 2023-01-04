package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public final class LogfmtLoggerFactory implements ILoggerFactory {
  @Override
  public Logger getLogger(String name) {
    return new LogfmtLogger(name);
  }
}
