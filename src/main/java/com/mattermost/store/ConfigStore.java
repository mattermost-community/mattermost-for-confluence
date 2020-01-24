package com.mattermost.store;

/**
 * Interface for storing Plugin Configuration.
 * Implementations can decide if they should be backed by a persistent store or not.
 */
public interface ConfigStore {
    String getWebhookURL();

    void setWebhookURL(String webhookURL);
}
