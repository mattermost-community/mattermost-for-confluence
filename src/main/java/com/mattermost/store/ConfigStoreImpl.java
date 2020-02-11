package com.mattermost.store;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.mattermost.Constants;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ConfigStore.class})
@Named("configStore")
public class ConfigStoreImpl implements ConfigStore {
    private static final String KEY_WEBHOOK_URL = Constants.PLUGIN_STORAGE_KEY + ".webhookURL";

    @ConfluenceImport
    private PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public ConfigStoreImpl(final PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public String getWebhookURL() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(KEY_WEBHOOK_URL);
    }

    @Override
    public void setWebhookURL(final String url) {
        pluginSettingsFactory.createGlobalSettings().put(KEY_WEBHOOK_URL, url);
    }
}
