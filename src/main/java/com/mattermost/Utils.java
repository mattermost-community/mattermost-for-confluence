package com.mattermost;

import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {
    private static final Logger LOGGER = getLogger();

    private Utils() {
    }

    public static boolean isValidUrl(final String url) {
        return (url.startsWith("https://") || url.startsWith("http://"));
    }

    /**
     * Get a logger for the caller class.
     *
     * @return
     */
    public static Logger getLogger() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        /*
         * stackTrace[0] is for Thread.currentThread().getStackTrace() stackTrace[1] is for this method log()
         */
        String className = stackTrace[2].getClassName();
        if (LOGGER != null) {
            LOGGER.trace("Get logger for class {}", className);
        }
        return LoggerFactory.getLogger(className);
    }

    public static void throwUnchecked(final Exception e) {
        throw new IllegalStateException(e);
    }

    public static String getStackTrace(final Exception e) {
        return Throwables.getStackTraceAsString(e);
    }
}
