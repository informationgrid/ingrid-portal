# InGrid Portal

<img src="https://ingrid-oss.eu/8.2.0/assets/components/ingrid-plattform.png" alt="InformationGrid illustration" width="480" align="right">

This repository is part of **[InGrid](https://ingrid-oss.eu)**, an open-source solution for building, managing, and exposing metadata-driven information systems.

**About InGrid Portal:**
User-friendly search across the InGrid data space and services — catalog browsing, map client, time series — with faceted search, news aggregation, and profile-based customization.

## Development

Just start up the development container and develop your pages, plugins and themes.

Checkout:

```
git clone --recursive https://github.com/informationgrid/ingrid-portal.git
```

```shell
docker login docker-registry.wemove.com
docker compose up -d --build grav
```

The website will be available on http://localhost:8100 and the admin pages on http://localhost:8100/admin

### Configuration

#### Default

There's already an administrator configured with the following credentials under :
```
Username: admin
Password: admin
```

The admin user YAML is located under `develop/admin.yaml`.

As default your default theme is InGrid and you are conntected on service from https://dev.informationgrid.eu/.

* InGrid-API: https://api.dev.informationgrid.eu
* InGrid-CSW: https://dev.informationgrid.eu/csw?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&id=
* InGrid-Codelist-Repo: https://dev.informationgrid.eu/codelist-repo/rest/getCodelists
* InGrid-Geo-API: https://geo-api.informationgrid.eu/v1/convert?exportFormat=

Grav CMS is a file-based application.

To make quick changes to the settings without going through the admin GUI, it is recommended to use the "develop" directory to permanently change settings for development.

#### System-Configuration

For changes to system settings add file `system.yaml` under `develop/config`.
For example, add this content to

```
home:
    alias: '/home'               <-- To change startpage
pages:
    theme: ingrid                <-- To change active theme
```

#### Plugin-Configuration

Add YAML files under "develop/config/plugins" for development.

##### E-Mail

Default-Configuration is located under `user/plugins/email/email.yaml`.

Add YAML file `develop/config/plugins/email.yaml` to override configuration to your mail service.

For example:

```
enabled: true
from: <FROM-EMAIL>
to: <TO-EMAIL
mailer:
  engine: sendmail
  smtp:
    server: <SERVER>
    port: 587
    encryption: none
    user: <MAIL-USER-LOGIN>
    password: <MAIL-USER-PASSWORD>
  sendmail:
    bin: '/usr/sbin/sendmail -bs'
content_type: text/html
```

##### InGrid-Grav

Default-Configuration is located under `user/plugins/ingrid-grav/ingrid-grav.yaml`.

Add YAML file `develop/config/plugins/ingrid-grav.yaml` to override configuration to your specific ingrid services.

For example:

```
enabled: true
ingrid_api:
  url: 'http://host.docker.internal:9910'
  user:
  pass:
csw:
  url: 'https://dev.informationgrid.eu/csw?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&id='
rdf:
  url:
...
```

ingrid-api:

The `ingrid-api` configuration is the search interface of the portal.
`http://host.docker.internal:9910` is a connection to an SSH tunnel to connect to various InGrid APIs.
You can also enter a accessible URL to the InGrid API.
For development purposes, it is advantageous to simply deactivate and activate the individual defined SSH tunnels, instead of constantly modifying the `ingrid-grav.yaml` file and the URL.

csw:

The `csw.url` configuration is the URL to ISO-XML for hits on search and detail. The portal just add the ID/UUID of a hit to the `csw.url`-URL.
(Must also enable on active theme for search and detail.)

rdf:

Same like `csw.url`. With the `rdf.url`-configuration the portal generate a URL to the RDF-XML.
(Must also enable on active theme for search and detail.)

##### InGrid-Grav-Utils

Default-Configuration is located under `user/plugins/ingrid-grav/ingrid-grav-utils.yaml`.

Add YAML file `develop/config/plugins/ingrid-grav-utils.yaml` to override configuration to more specific ingrid services.

For example:

```
enabled: true
debug: false
codelist_api:
    url: 'https://dev.informationgrid.eu/codelist-repo/rest/getCodelists'
    user:
    pass:
...
```
The `codelist_api.url` configuration define the interface to the InGrid-Codelist.
The InGrid codelist is important for the portal, as values within the portal are translated via codelists - for example, object classes, facets, etc.

Changes to the settings are immediately visible upon reloading the browser.

#### Pages

##### Add page

Add a page in InGrid Portal using the `Grav Admin panel`:

1. Open http://localhost:8100/admin and sign in.
2. Select Pages → Add Page.
3. Enter a title and folder name, choose a parent page, and select the cms display template for a standard content page.
4. Set Visible to Yes, select Continue, and write the content in Markdown.
5. Save the page and open its URL to verify it. Check the publishing settings if it is unavailable.

Using `files`:

Create user/pages/example/cms.md:

```
---
title: Example page
visible: true
custom_title:
    html: Example custom_title.html
    content: Example custom_title.content
    menu: Example custom_title.menu
---
# Example page
This is the page content.
[Visit InGrid](https://ingrid-oss.eu?target=_blank)
```

Open http://localhost:8100/example

The filename cms.md selects the InGrid theme’s cms template. The development container mounts user/pages, so local changes are available to Grav.

For more details, see Grav’s page creation documentation on https://learn.getgrav.org/17/basics.

#### Themes

##### Add theme

Enter the docker container by executing the command via the console:

```
docker compose exec grav bash
```
Now you can add a new theme by using:

```
bin/plugin devtools new-theme

Enter Theme Name:
>  Example
Folder / slug: example

Enter Theme Description:
>  Example Description

Enter Developer Name:
>  Example Developer

Enter GitHub ID (can be blank):
>

Enter Developer Email:
>  example@developer.com

Grav Version
------------
Which Grav version should this target? [Grav 2.0 only (modern, PHP 8.3+)]:
[2.0 ] Grav 2.0 only (modern, PHP 8.3+)
[both] Grav 1.7 and 2.0 (maximum compatibility)
[1.7 ] Grav 1.7 only (legacy)
> both

Template
--------
Please choose a theme template [Basic Theme using Pure.css]:
[pure-blank ] Basic Theme using Pure.css
[blades     ] Basic Theme using the Blades semantic CSS framework (no build step)
[tailwind   ] Basic Theme using tailwind.css and including Alpine.js
[daisyui    ] Basic Theme using Tailwind CSS and daisyUI components
[inheritance] Inherit from another theme
[copy       ] Copy another theme
> inheritance

 Please choose a theme to extend:
  [0 ] bast
  [1 ] bast_intern
  [2 ] baw_doi
  [3 ] baw_mis
  [4 ] bkg_adv_mis
  [5 ] bkg_mis
  [6 ] hmdk
  [7 ] ingrid
  [8 ] itzbund_eba
  [9 ] itzbund_wsv
  [10] itzbund_zdm
  [11] krzn
  [12] lfu_mis
  [13] lubw
  [14] metaver
  [15] nlpv
  [16] numis
  [17] rlp_portalu
  [18] up_sh
  [19] uvp
 > 7

 ------------- -----------------------------------
  Setting       Value
 ------------- -----------------------------------
  Type          Theme
  Name          Example
  Template      pure-blank
  Grav target   Grav 1.7 + 2.0
  Location      /var/www/html/user/themes/example
 ------------- -----------------------------------

 Create this theme? [Y/n]  (yes/no) [yes]:
 > yes

 [OK] THEME Example created successfully
```

Fix the theme YAML file:
```
sed -i "s/&#039;/\'/g" /var/www/html/user/themes/example/example.yaml
```

Change the owner of the new theme:

```
chown -R  www-data:www-data /var/www/html/user/themes/example
```
And exit the container:

```
exit
```

http://localhost:8100/admin/themes

#### Edit new theme

Copy the following directories/files from the `user/themes/ingrid` directory to the new theme folder `user/themes/example:

* File for css changes: css/custom.css
* Directory with configs:config
  * Folder with help content: config/help
  * Folder with facet config: config/override

Copy the `form` configuration from `user/themes/ingrid/blueprints.yaml` to `user/themes/example/blueprints.yaml`.
This allows configuration to be changed via the admin GUI:

http://localhost:8100/admin/themes/example

Now active your new theme by edit the `develop/config/system.yaml` and changing to:

```
...
pages:
    theme: example
...
```

On http://localhost:8100/ you are now seeing the look of the InGrid theme, but you have your own theme that you can customize to your liking.

#### Add init theme pages

Add the folder `user/themes/example/pages/init` to create new pages for initial installation.

For development add this configuration to the `develop/config/system.yaml`:

```
pages:
    ...
    dirs:
        - page://
        - theme://pages/init
```

So the created pages will be available in the frontend.

Note:

This pages are not available in the admin GUI.

On initial server installation per docker the pages under `user/themes/example/pages/init` will be copied to the `user/pages` folder.
It will be created a file names `initialized` under `/user/config/` to mark an initial server installation.

#### Theme twig content for search and detail

The file `user/themes/example/example.php` will be important to add twig vars for search and detail hits.

Change the file to:

```
<?php
namespace Grav\Theme;

use Grav\Common\Theme;
use Grav\Plugin\IdfHelper;
use RocketTheme\Toolbox\Event\Event;

class Example extends Ingrid
{
    public function addThemeSearchHitMetadataContent(Event $event): void
    {
        // Get variables from event
        $content = $event['content'];
        $hit = $event['hit'];
        $lang = $event['lang'];

        $hit->newIndexField = ElasticsearchHelper::getValue($content, '<NEW-INDEX-FIELD>');
    }

    public function addThemeDetailMetadataContent(Event $event): void
    {
        // Get variables from event
        $content = $event['content'];
        $hit = $event['hit'];
        $esHit = $event['esHit'];
        $lang = $event['lang'];

        $objClass = ElasticsearchHelper::getValue($esHit, 't01_object.obj_class');

        $node = IdfHelper::getNode($content, '//gmd:MD_Metadata | //idf:idfMdMetadata');
        if (isset($node)) {
            $xpathExpression = '<NEW-XPATH-VALUE>';
            $hit->newXPathValue = IdfHelper::getNodeValue($node, $xpathExpression);
        }
    }
}
```

On detail:

Now add a twig template under `user/themes/example/templates/partials/detail/metadata/detail_metadata_profile_settings.html.twig`.

Note:
File `user/themes/example/templates/partials/detail/metadata/detail_metadata_profile_settings.html.twig` override the default twig template `user/themes/ingrid/templates/partials/detail/metadata/detail_metadata_profile_settings.html.twig`.

Add this content to the file:

```
{% set newXPathValue = hit.newXPathValue %}

{{ vardump(newXPathValue) }}

{% include 'partials/detail/metadata/detail_metadata_view.html.twig' %}
```

An on the detail page it will be displayed the value of `newXPathValue`.

On search:

It is the same like on detail. The var `hit.newIndexField` is now available on the twig templates under `user\themes\ingrid\templates\partials\search` or `user\themes\example\templates\partials\search`(if you override a default twig template on the new theme).

#### Theme Overriding mechanism

All files under `user/themes/ingrid` can be overridden by `user/themes/example`,
* if the file name and folder structure is the same
* if the file is not reference by file path in the theme YAML.

Note:
Don't override a twig template with large content if you want to add a snippet content.
Use the twig `include` function on the InGrid theme twig:

```
...
{% include 'partials/footer/example.html.twig' ignore missing %}
```

and add this file only to your theme. The content of the new theme twig template will only be displayed in your new theme.

The `ignore missing` setting prevents an error message from appearing in the InGrid theme.

For theme development, go to the theme directory, e.g. "user/themes/ingrid" and run

```shell
yarn
yarn run dev
```

Any changes made on the styles will be compiled and made available in the portal.

### Recommended Plugins for IntelliJ

* Php Plugin
* Twig
* Symfony Support
  * enable plugin and configure Twig/Template and add namespace:
    * Namespace: <empty>
    * Project-Path: user/themes/ingrid/templates
    * Type: ADD_PATH

### Debugging

In IntelliJ, you need to have the PHP plugin installed. Then you need to run `Run -> Start Listening for PHP Debug Connections` and set your breakpoints in your php-files or twig-templates.
For easy printing of variables, you can use the `vardump` function inside your twig-templates.
