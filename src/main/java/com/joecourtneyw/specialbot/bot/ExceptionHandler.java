package com.joecourtneyw.specialbot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Logger LOGGER;

    public ExceptionHandler() {
        this(null);
    }

    public ExceptionHandler(Class<?> clazz) {
        if (clazz == null) {
            clazz = ExceptionHandler.class;
        }
        LOGGER = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("UnhandledException in [" + t.getName() + "]", e);
    }

}