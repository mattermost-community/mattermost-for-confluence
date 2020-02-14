package it.com.mattermost;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.sal.api.ApplicationProperties;
import org.junit.runner.RunWith;

@RunWith(AtlassianPluginsTestRunner.class)
public class MyComponentWiredTest
{
    private final ApplicationProperties applicationProperties;

    public MyComponentWiredTest(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }
}
