package ut.com.mattermost.serializer;

import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.mattermost.serializer.EventRenderer;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link com.mattermost.serializer.EventRenderer}
 */
@RunWith(MockitoJUnitRunner.class)
public class EventRendererTest extends TestCase {
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
    }

    @Test
    public void testEventRenderer() {
        EventRenderer.renderEvent(commentCreateEvent);
        EventRenderer.renderEvent(commentUpdateEvent);
        EventRenderer.renderEvent(commentRemoveEvent);
        EventRenderer.renderEvent(pageCreateEvent);
        EventRenderer.renderEvent(pageUpdateEvent);
        EventRenderer.renderEvent(pageRemoveEvent);
        EventRenderer.renderEvent(pageRestoreEvent);
        EventRenderer.renderEvent(pageTrashedEvent);
    }
}
