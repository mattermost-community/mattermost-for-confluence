package com.mattermost.listener;

import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.mattermost.Utils;
import com.mattermost.client.HttpClient;
import com.mattermost.serializer.EventRenderer;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({AnnotatedListener.class})
@Named("annotatedListener")
@Scanned
public class AnnotatedListener implements DisposableBean, InitializingBean {
    private final HttpClient httpClient;
    @ConfluenceImport
    private EventPublisher eventPublisher;

    @Inject
    public AnnotatedListener(
            final HttpClient httpClient,
            final EventPublisher eventPublisher
    ) {
        this.httpClient = httpClient;
        this.eventPublisher = eventPublisher;
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
        Utils.log("Listener Initialized.");
    }

    // Unregister the listener if the plugin is uninstalled or disabled.
    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
        Utils.log("Listener Un-initialized.");
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

    // Sends an event
    private void sendActivity(final ContentEvent event) {
        httpClient.sendEventToServer(EventRenderer.renderEvent(event));
    }
}
