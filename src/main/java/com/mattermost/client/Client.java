package com.mattermost.client;

import com.atlassian.confluence.json.json.JsonObject;

public interface Client {
    void sendEventToServer(JsonObject eventData);
}
