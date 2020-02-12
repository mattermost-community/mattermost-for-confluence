package ut.com.mattermost.adminui;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.mattermost.adminui.AdminServlet;
import com.mattermost.store.ConfigStore;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link com.mattermost.adminui.AdminServlet}
 */
@RunWith(MockitoJUnitRunner.class)
public class AdminServletTest extends TestCase {
    @Mock
    private ConfigStore configStore;
    @Mock
    private UserManager userManager;
    @Mock
    private LoginUriProvider loginUriProvider;
    @Mock
    private TemplateRenderer templateRenderer;
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
        adminServlet = new AdminServlet(configStore, userManager, loginUriProvider,
            templateRenderer);
    }

    @Test
    public void testUserMustBeLoggedIn() throws IOException, ServletException, URISyntaxException {
        when(userManager.getRemoteUserKey(request)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090"));
        when(request.getQueryString()).thenReturn("querystring");
        when(loginUriProvider.getLoginUri(any(URI.class))).thenReturn(new URI("/login"));

        adminServlet.doGet(request, response);
        verify(response).sendRedirect("/login");
    }

    @Test
    public void testUserMustBeAdministrator() throws IOException, ServletException, URISyntaxException {
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090"));
        when(request.getQueryString()).thenReturn("querystring");
        when(loginUriProvider.getLoginUri(any(URI.class))).thenReturn(new URI("/login"));
        when(userManager.getRemoteUserKey(request)).thenReturn(SOME_USER1_KEY);
        when(userManager.isSystemAdmin(SOME_USER1_KEY)).thenReturn(false);

        adminServlet.doGet(request, response);
        verify(response).sendRedirect("/login");
    }

    @Test
    public void testDoGetSuccess() throws IOException, ServletException {
        when(userManager.getRemoteUserKey(request)).thenReturn(SOME_USER1_KEY);
        when(userManager.isSystemAdmin(SOME_USER1_KEY)).thenReturn(true);

        adminServlet.doGet(request, response);
        verifyZeroInteractions(loginUriProvider);
        verify(templateRenderer).render(anyString(), anyMapOf(String.class, Object.class), any(PrintWriter.class));
    }

    @Test
    public void testDoPostSuccess() throws IOException {
        when(request.getParameter("webhookURL")).thenReturn("https://test.webhook.url");
        adminServlet.doPost(request, response);
        verify(configStore).setWebhookURL("https://test.webhook.url");
    }

    @Test
    public void testDoPostInvalidURL() throws IOException {
        when(request.getParameter("webhookURL")).thenReturn("invalid.webhook.url");
        adminServlet.doPost(request, response);
        verifyZeroInteractions(configStore);
    }
}
