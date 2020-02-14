package ut.com.mattermost;

import com.mattermost.Utils;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link com.mattermost.Utils}
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilsTest  extends TestCase {
    @Test
    public void testIsValidURL()
    {
        assertTrue("Should return true for valid URL", Utils.isValidUrl("https://www.example.com"));
        assertTrue("Should return true for valid URL", Utils.isValidUrl("http://www.example.com"));
        assertFalse("Should return false for invalid URL", Utils.isValidUrl("htt://www.example.com"));
    }
}
