package org.rapidpm.dependencies.core.logger.factory;


import java.util.logging.Level;

import org.rapidpm.dependencies.core.logger.AbstractLogger;
import org.rapidpm.dependencies.core.logger.LoggerFactorySupport;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Slf4jFactory extends LoggerFactorySupport {

  @Override
  protected LoggingService createLogger(String name) {
    Logger logger = LoggerFactory.getLogger(name);
    return new Slf4jLogger(logger);
  }

  static class Slf4jLogger extends AbstractLogger {

    private final Logger logger;

    Slf4jLogger(Logger logger) {
      this.logger = logger;
    }

    @Override
    public void log(Level level , String message) {
      if (level == Level.FINEST) {
        logger.trace(message);
      } else if (level == Level.FINER || level == Level.FINE) {
        logger.debug(message);
      } else if (level == Level.CONFIG || level == Level.INFO) {
        logger.info(message);
      } else if (level == Level.WARNING) {
        logger.warn(message);
      } else if (level == Level.SEVERE) {
        logger.error(message);
      } else if (level != Level.OFF) {
        logger.info(message);
      }
    }

    @Override
    public void log(Level level , String message , Throwable thrown) {
      if (level == Level.FINEST) {
        logger.trace(message , thrown);
      } else if (level == Level.FINER || level == Level.FINE) {
        logger.debug(message , thrown);
      } else if (level == Level.CONFIG || level == Level.INFO) {
        logger.info(message , thrown);
      } else if (level == Level.WARNING) {
        logger.warn(message , thrown);
      } else if (level == Level.SEVERE) {
        logger.error(message , thrown);
      } else if (level != Level.OFF) {
        logger.info(message , thrown);
      }
    }

    @Override
    public Level getLevel() {
      return logger.isTraceEnabled() ? Level.FINEST
                                     : logger.isDebugEnabled() ? Level.FINE
                                                               : logger.isInfoEnabled() ? Level.INFO
                                                                                        : logger.isWarnEnabled() ? Level.WARNING
                                                                                                                 : logger.isErrorEnabled() ? Level.SEVERE
                                                                                                                                           : Level.OFF;
    }

    @Override
    public boolean isLoggable(Level level) {
      return level == Level.FINEST ? logger.isTraceEnabled()
                                   : level == Level.FINER ? logger.isDebugEnabled()
                                                          : level == Level.FINE ? logger.isDebugEnabled()
                                                                                : level == Level.CONFIG ? logger.isInfoEnabled()
                                                                                                        : level == Level.INFO ? logger.isInfoEnabled()
                                                                                                                              : level == Level.WARNING ? logger.isWarnEnabled()
                                                                                                                                                       : level == Level.SEVERE ? logger.isErrorEnabled()
                                                                                                                                                                               : level != Level.OFF && logger.isInfoEnabled();
    }


  }
}