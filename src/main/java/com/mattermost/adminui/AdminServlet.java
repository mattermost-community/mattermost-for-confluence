package com.mattermost.adminui;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.mattermost.Utils;
import com.mattermost.store.ConfigStore;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Scanned
public class AdminServlet extends HttpServlet {
    static final String PLUGIN_STORAGE_KEY = "com.mattermost";

    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final ConfigStore configStore;

    @Inject
    public AdminServlet(
            final ConfigStore configStore,
            final UserManager userManager,
            final LoginUriProvider loginUriProvider,
            final TemplateRenderer templateRenderer
    ) {
        this.configStore = configStore;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        UserKey userkey = userManager.getRemoteUserKey(request);
        if (userkey == null || !userManager.isSystemAdmin(userkey)) {
            redirectToLogin(request, response);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("webhookURL", configStore.getWebhookURL());

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("templates/admin.vm", params, response.getWriter());
    }

    private void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(final HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String webhookURL = StringUtils.trimToEmpty(request.getParameter("webhookURL"));
        if (StringUtils.isNotEmpty(webhookURL) && Utils.isValidUrl(webhookURL)) {
            configStore.setWebhookURL(webhookURL);
        }

        response.sendRedirect("/plugins/servlet/mattermost/configurePlugin.action");
    }
}
