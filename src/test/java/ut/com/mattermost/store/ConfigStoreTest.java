package ut.com.mattermost.store;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.mattermost.Constants;
import com.mattermost.store.ConfigStore;
import com.mattermost.store.ConfigStoreImpl;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link com.mattermost.store.ConfigStoreImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigStoreTest extends TestCase {
    @Mock
    private PluginSettings pluginSettings;
    @Mock
    private PluginSettingsFactory pluginSettingsFactory;

    private ConfigStore configStore;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
        configStore = new ConfigStoreImpl(pluginSettingsFactory);
    }

    @Test
    public void testGetWebhookURL() {
        when(pluginSettings.get(Constants.PLUGIN_STORAGE_KEY + ".webhookURL")).thenReturn("https://test.webhook.url");
        assertEquals("Webhook URL does not match.", "https://test.webhook.url", configStore.getWebhookURL());
    }

    @Test
    public void testSetWebhookURL() {
        configStore.setWebhookURL("https://test.webhook.url");
        verify(pluginSettings).put(Constants.PLUGIN_STORAGE_KEY + ".webhookURL", "https://test.webhook.url");
    }
}
