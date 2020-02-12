package ut.com.mattermost.client;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlassian.confluence.json.json.JsonObject;
import com.mattermost.client.HttpClient;
import com.mattermost.client.HttpClientImpl;
import com.mattermost.store.ConfigStore;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link com.mattermost.client.HttpClientImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpClientTest extends TestCase {
    @Mock
    private ConfigStore configStore;

    private HttpClient httpClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        httpClient = new HttpClientImpl(configStore);
    }

    @Test
    public void testSendEventToServer() throws IOException {
        JsonObject jsonObject = new JsonObject();
        when(configStore.getWebhookURL()).thenReturn("test.webhook.url");
        httpClient.sendEventToServer(jsonObject);
        verify(configStore, times(1)).getWebhookURL();
    }
}
