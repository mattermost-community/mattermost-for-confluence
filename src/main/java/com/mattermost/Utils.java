package com.mattermost;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utils {
    private Utils() {
    }

    public static boolean isValidUrl(final String url) {
        return (url.startsWith("https://") || url.startsWith("http://"));
    }

    private static String now() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
    }

    public static void log(final String message) {
        System.out.println(now() + ": com.mattermost: " + message);
    }

    public static void log(final String message, final Throwable throwable) {
        String messageToLog = message;
        if (throwable != null && message != null) {
            messageToLog += " : " + Utils.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            messageToLog = Utils.getStackTraceString(throwable);
        }
        log(messageToLog);
    }

    /**
     * @return Stack trace in form of String
     */
    private static String getStackTraceString(final Throwable tr) {
        if (tr == null) {
            return "";
        }

        // if network is unavailable
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "network error.";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
