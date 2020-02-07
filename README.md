# Mattermost for  Confluence
[![CircleCI branch](https://img.shields.io/circleci/project/github/Brightscout/mattermost-for-confluence/master.svg)](https://circleci.com/gh/Brightscout/mattermost-for-confluence)

This plugin integrates Atlassian Confluence to Mattermost. Requires the Mattermost Plugin to be installed and configured on your Mattermost instance.

#### Developer Setup
- Refer Instructions [here](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/) to Install the Atlassian SDK on a ([Windows](https://developer.atlassian.com/server/framework/atlassian-sdk/install-the-atlassian-sdk-on-a-windows-system/)) or ([Linux or Mac](https://developer.atlassian.com/server/framework/atlassian-sdk/install-the-atlassian-sdk-on-a-linux-or-mac-system/)) system.
- Refer Instructions [here](https://confluence.atlassian.com/doc/confluence-setup-guide-135691.html) to setup Confluence server locally OR alternatively, you may use the [Docker image](https://hub.docker.com/r/atlassian/confluence-server/).
  ```
  docker run -v /data/your-confluence-home:/var/atlassian/application-data/confluence --name="confluence" -d -p 8090:8090 -p 8091:8091 atlassian/confluence-server
  ```

#### Development
Here are the SDK commands you'll use immediately:

* `atlas-version`
    -- Displays version and runtime information for the Atlassian Plugin SDK. Useful to check if the SDK is installed.
* `atlas-clean`
    -- Removes files generated during the build-time in a project's directory (runs mvn clean).
* `atlas-mvn package`
    -- Packages the plugin artifacts.
* `atlas-mvn checkstyle:check`
    -- Runs checkstyle against all packages.
* `atlas-help`
    -- Prints description for all commands in the SDK.

Note: Full documentation is always available in the [Docs](https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK).

#### Manual Installation
1. Download the Mattermost Confluence Plugin OBR file from the [download page](https://github.com/Brightscout/mattermost-for-confluence/releases).
2. Open the **Confluence administration** menu and select **Manage apps** (must have Admin rights).
![Add-ons](https://i.imgur.com/uCNhnur.png)
3. Log in with your Confluence Admin credentials.
4. Select **Upload app**.
![UploadAddOn](https://i.imgur.com/eIrnfC3.png)
5. Browse your computer for the Mattermost Confluence Plugin OBR file you downloaded in step 1 and click **Upload**.
6. The plugin will be uploaded to Confluence and will be automatically installed. Check the **Manage apps** screen to ensure that the plugin is available.

#### Configuration
- Make sure you have installed the plugin in your Confluence Instance.
- Click on **Configure** from the **Manage apps** page.
    ![ManageAddOns](https://i.imgur.com/wzuLXE6.png)
- Set the webhook URL: `https://SITEURL/plugins/com.mattermost.confluence/api/v1/server/webhook?secret=WEBHOOKSECRET`.
    - Replace `SITEURL` with the site URL of your Mattermost instance, and `WEBHOOKSECRET` with the secret generated in Mattermost via **System Console > Plugins > Confluence**.
