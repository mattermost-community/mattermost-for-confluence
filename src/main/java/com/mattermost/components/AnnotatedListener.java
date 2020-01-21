package com.mattermost.components;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ AnnotatedListener.class })
@Named
public class AnnotatedListener implements DisposableBean, InitializingBean {
    private static final String PLUGIN_STORAGE_KEY = "com.mattermost";
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ConfluenceImport
    private EventPublisher eventPublisher;

    @Inject
    public AnnotatedListener(final EventPublisher eventPublisher,
            final PluginSettingsFactory pluginSettingsFactory) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsFactory = pluginSettingsFactory;
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
            System.out.println(response.toString());
        }
    }

    /**
     * Called when the plugin has been enabled.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // Register ourselves with the EventPublisher
        eventPublisher.register(this);

        System.out.println("Listener Initialized");
    }

    // Unregister the listener if the plugin is uninstalled or disabled.
    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onPageCreateEvent(final PageCreateEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void onPageUpdateEvent(final PageUpdateEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void pageRemoveEvent(final PageRemoveEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void pageTrashedEvent(final PageTrashedEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void pageRestoreEvent(final PageRestoreEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void commentCreateEvent(final CommentCreateEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void commentUpdateEvent(final CommentUpdateEvent event) {
        sendActivity(event);
    }

    @EventListener
    public void commentRemoveEvent(final CommentRemoveEvent event) {
        sendActivity(event);
    }

    // Renders the event to be sent
    // TO-DO: Create serializers for each content type.
    private JsonObject renderEvent(final ContentEvent event) {
        final JsonObject renderedEvent = new JsonObject();
        ContentEntityObject content = event.getContent();
        renderedEvent.setProperty("ID", content.getId());
        renderedEvent.setProperty("Body", content.getBodyAsString());
        renderedEvent.setProperty("Title", content.getTitle());
        renderedEvent.setProperty("URL", content.getUrlPath());
        renderedEvent.setProperty("Type", content.getType());
        return renderedEvent;
    }

    // Sends an event
    private void sendActivity(final ContentEvent event) {
        Thread t = new Thread(() -> {
            try {
                postData(getWebhookURL(), renderEvent(event));
            } catch (IOException e) {
                System.out.println("Error while sending POST request to mattermost:" + e.getMessage());
            }
        });
        t.start();
    }

    private URL getWebhookURL() throws MalformedURLException {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        Object webhookURL = pluginSettings.get(PLUGIN_STORAGE_KEY + ".webhookURL");
        return new URL(webhookURL.toString());
    }
}
