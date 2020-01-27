package com.mattermost.client;

import com.atlassian.confluence.json.json.JsonObject;

public interface HttpClient {
    void sendEventToServer(JsonObject eventData);
}
