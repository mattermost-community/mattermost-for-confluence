package ut.com.mattermost.adminui;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.mattermost.adminui.AdminServlet;
import com.mattermost.store.ConfigStore;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminServletTest {
    @Mock
    private ConfigStore mockConfigStore;

    @Mock
    private UserManager userManager;

    @Mock
    private LoginUriProvider loginUriProvider;

    @Mock
    private TemplateRenderer mockTemplateRenderer;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private UserKey SOME_USER1_KEY;
    private AdminServlet adminServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SOME_USER1_KEY = new UserKey("userKey");
        adminServlet = new AdminServlet(mockConfigStore, userManager, loginUriProvider, mockTemplateRenderer);
    }

    @Test
    public void testUserMustBeLoggedIn() throws Exception {
        when(userManager.getRemoteUser(request)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090"));
        when(request.getQueryString()).thenReturn("querystring");
        when(loginUriProvider.getLoginUri(any(URI.class))).thenReturn(new URI("/login"));

        adminServlet.doGet(request, response);
        verify(response).sendRedirect("/login");
    }

    @Test
    public void testUserMustBeAdministrator() throws Exception {
        when(userManager.getRemoteUser(request)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090"));
        when(request.getQueryString()).thenReturn("querystring");
        when(loginUriProvider.getLoginUri(any(URI.class))).thenReturn(new URI("/login"));

        UserProfile profile = mock(UserProfile.class);
        when(profile.getUserKey()).thenReturn(SOME_USER1_KEY);
        when(userManager.getRemoteUser(request)).thenReturn(profile);
        when(userManager.isAdmin(profile.getUserKey())).thenReturn(false);
        adminServlet.doGet(request, response);
        verify(response).sendRedirect("/login");
    }
}
