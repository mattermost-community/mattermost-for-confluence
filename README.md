# Mattermost for Confluence
[![CircleCI branch](https://img.shields.io/circleci/project/github/Brightscout/mattermost-for-confluence/master.svg)](https://circleci.com/gh/Brightscout/mattermost-for-confluence)

Publish Confluence Server events to Mattermost.

#### Manual Installation
1. Download the Mattermost Confluence Plugin OBR file from the [download page](https://github.com/Brightscout/mattermost-for-confluence/releases).
2. Open the **Confluence administration** menu and select **Manage apps** (must have Admin rights).
   
   ![Add-ons](https://i.imgur.com/uCNhnur.png)

3. Log in with your Confluence Admin credentials.
4. Select **Upload app**.

   ![UploadAddOn](https://i.imgur.com/eIrnfC3.png)

5. Browse your computer for the Mattermost Confluence Plugin OBR file you downloaded in Step 1 and click **Upload**.
6. The plugin will be uploaded to the Confluence Server and will be automatically installed. Check the **Manage apps** screen to ensure that the plugin is available.

#### Development
Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-help  -- prints description for all commands in the SDK

#### Other Useful Commands
  * `atlas-version`
  * `atlas-clean` or `atlas-mvn clean`
  * `atlas-mvn compile`
  * `atlas-mvn package`
  * `atlas-mvn checkstyle:check`
  * `atlas-mvn idea:idea`

Note: Full documentation is always available in the [Docs](https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK).
