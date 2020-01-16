package com.mattermost;

public final class Utils {

    private Utils() {
    }

    public static boolean isValidUrl(final String url) {
        return (url.startsWith("https://") || url.startsWith("http://"));
    }
}
