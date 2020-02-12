package com.mattermost.client;

import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.mattermost.Utils;
import com.mattermost.store.ConfigStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.inject.Named;

@ExportAsService({HttpClient.class})
@Named("httpClient")
public final class HttpClientImpl implements HttpClient {
    private final ConfigStore configStore;

    public HttpClientImpl(final ConfigStore configStore) {
        Utils.log("HttpClient Initialized.");
        this.configStore = configStore;
    }

    @Override
    public void sendEventToServer(final JsonObject eventData) {
        try {
            postData(getWebhookURL(), eventData);
        } catch (IOException e) {
            Utils.log("Error while sending the Mattermost notification", e);
        }
    }

    private URL getWebhookURL() throws MalformedURLException {
        return new URL(configStore.getWebhookURL());
    }

    private static void postData(final URL requestURL, final JsonObject params) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        String jsonInputString = params.serialize();

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Utils.log(response.toString());
        }
    }
}
