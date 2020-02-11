package ut.com.mattermost.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.event.api.EventPublisher;
import com.mattermost.client.HttpClient;
import com.mattermost.listener.AnnotatedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AnnotatedListenerTest {
    @Mock
    private HttpClient httpClient;

    @Mock
    private EventPublisher eventPublisher;
    private AnnotatedListener annotatedListener;

    @Mock
    private CommentCreateEvent commentCreateEvent;

    @Mock
    private CommentUpdateEvent commentUpdateEvent;

    @Mock
    private CommentRemoveEvent commentRemoveEvent;

    @Mock
    private PageCreateEvent pageCreateEvent;

    @Mock
    private PageUpdateEvent pageUpdateEvent;

    @Mock
    private PageRemoveEvent pageRemoveEvent;

    @Mock
    private PageRestoreEvent pageRestoreEvent;

    @Mock
    private PageTrashedEvent pageTrashedEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        annotatedListener = new AnnotatedListener(httpClient, eventPublisher);
    }

    @Test
    public void afterPropertiesSet() throws Exception {
        annotatedListener.afterPropertiesSet();
        verify(eventPublisher, times(1)).register(any(AnnotatedListener.class));
    }

    @Test
    public void destroy() throws Exception {
        annotatedListener.destroy();
        verify(eventPublisher, times(1)).unregister(any(AnnotatedListener.class));
    }

//    @Test
//    public void testOnPageCreateEvent() {
//        annotatedListener.onPageCreateEvent(pageCreateEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testOnPageUpdateEvent() {
//        annotatedListener.onPageUpdateEvent(pageUpdateEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testPageRemoveEvent() {
//        annotatedListener.pageRemoveEvent(pageRemoveEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testPageTrashedEvent() {
//        annotatedListener.pageTrashedEvent(pageTrashedEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testPageRestoreEvent() {
//        annotatedListener.pageRestoreEvent(pageRestoreEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testCommentCreateEvent() {
//        annotatedListener.commentCreateEvent(commentCreateEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testCommentUpdateEvent() {
//        annotatedListener.commentUpdateEvent(commentUpdateEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
//
//    @Test
//    public void testCommentRemoveEvent() {
//        annotatedListener.commentRemoveEvent(commentRemoveEvent);
//        verify(httpClient, times(1)).sendEventToServer(any(JsonObject.class));
//    }
}
