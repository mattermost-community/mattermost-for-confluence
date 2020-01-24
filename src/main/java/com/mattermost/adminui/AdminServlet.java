package com.mattermost.adminui;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

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
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public AdminServlet(
            final UserManager userManager,
            final LoginUriProvider loginUriProvider,
            final TemplateRenderer templateRenderer,
            final PluginSettingsFactory pluginSettingsFactory
    ) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        UserKey userkey = userManager.getRemoteUserKey(request);
        if (userkey == null || !userManager.isSystemAdmin(userkey)) {
            redirectToLogin(request, response);
            return;
        }

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        Map<String, Object> context = new HashMap<>();
        context.put("webhookURL", pluginSettings.get(PLUGIN_STORAGE_KEY + ".webhookURL"));

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("templates/admin.vm", context, response.getWriter());
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
        String webhookURL = StringUtils.trimToNull(request.getParameter("webhookURL"));
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".webhookURL", webhookURL);
        response.sendRedirect("/plugins/servlet/mattermost/configurePlugin.action");
    }
}
