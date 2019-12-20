/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.config;

import com.tngtech.configbuilder.ConfigBuilder;
import de.ingrid.portal.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

/**
 * Provides access to the ingrid portal preferences.
 * 
 * @author joachim@wemove.com
 */
public class PortalConfig extends PropertiesConfiguration {

    /** how old can rss news be, before the are deleted from news history in days */
    public static final String RSS_HISTORY_DAYS = "rss.history.days";
    
    /** define number of rss feeds for RSSNewsTeaserPortlet */
    public static final String PORTAL_RSS_NEWS_NUMBER = "portal.rss.news.number";

    /**
     * timout for queries in ms should be larger than query.timout.ranked and
     * query.timout.unranked because ranked and unranked query is encapsulated
     * inside a threaded query
     */
    public static final String QUERY_TIMEOUT_THREADED = "query.timeout.threaded";

    /** timout for ranked queries in ms */
    public static final String QUERY_TIMEOUT_RANKED = "query.timeout.ranked";
    
    /** requested fields for search queries in portal */
    public static final String QUERY_REQUESTED_FIELDS = "portal.query.requestedfields";

    /** default timeout for sns queries in ms */
    public static final String SNS_TIMEOUT_DEFAULT = "sns.timeout.default";
    
    /** default length of chronicle hits  */
    public static final String SNS_CHRONICLE_HITS_LENGTH = "sns.chronicle.hits.length";
    
    /**
     * always read values from database or only once and then from cache, true
     * or false
     */
    public static final String ALWAYS_REREAD_DB_VALUES = "db.reread";

    public static final String EMAIL_REGISTRATION_CONFIRMATION_SENDER = "email.registration.confirmation.sender";

    public static final String EMAIL_REGISTRATION_CONFIRMATION_URL = "email.registration.confirmation.url";

    public static final String EMAIL_SMTP_SERVER = "email.smtp.server";
    public static final String EMAIL_SMTP_USER = "email.smtp.user";
    public static final String EMAIL_SMTP_PW = "email.smtp.password";
    public static final String EMAIL_SMTP_PORT = "email.smtp.port";
    public static final String EMAIL_SMTP_SSL = "email.smtp.ssl";
    public static final String EMAIL_SMTP_PROTOCOL = "email.smtp.protocol";
   
    public static final String EMAIL_WEBMASTER = "email.webmaster";

    public static final String EMAIL_CONTACT_FORM_RECEIVER = "email.contact.form.receiver";

    public static final String QUERY_HISTORY_DISPLAY_SIZE = "query.history.display.size";

    public static final String DETAILS_GENERIC_UCFIRST_STOPWORDS = "detail.generic.ucfirst.stopwords";

    public static final String SAVE_ENTRIES_MAX_NUMBER = "save.entries.max.number";

    public static final String WMS_MAPBENDER_ADMIN_URL = "wms.mapbender.admin.url";

    public static final String WMS_MAPLAB_ADMIN_URL = "wms.maplab.admin.url";

    public static final String APACHE_STATISTICS_URL = "apache.statistics.url";
    
    public static final String UDK_FIELDS_DATE = "udk.fields.date";

    public static final String UDK_FIELDS_TRANSLATE = "udk.fields.translate";
    
    public static final String PORTAL_PROFILES = "portal.profiles";

    public static final String PORTAL_PROFILE = "portal.profile";

    public static final String PORTAL_ENABLE_SEARCH_CATALOG = "portal.enable.search.catalog";

    public static final String PORTAL_SEARCH_RESTRICT_PARTNER = "portal.search.restrict.partner";
    
    public static final String PORTAL_SEARCH_DEFAULT_DATASOURCE = "portal.search.default.datasource";
    
    public static final String PORTAL_SEARCH_RESTRICT_PARTNER_LEVEL = "portal.search.restrict.partner.level";
    
    public static final String PORTAL_HIERARCHY_CATALOGNAME_HIDDEN = "portal.hierarchy.catalogname.hidden";
    
    public static final String PORTAL_HIERARCHY_CATALOG_ADDRESS_CLOSE = "portal.hierarchy.catalog.address.close";
    
    public static final String PORTAL_LOGGER_RESOURCE = "portal.logger.resource";

    public static final String PORTAL_ENABLE_MEASURE = "portal.enable.measure";

    public static final String PORTAL_ENABLE_ABOUT = "portal.enable.about";
    
    public static final String PORTAL_ENABLE_PARTNER = "portal.enable.partner";
    
    public static final String PORTAL_ENABLE_SOURCES = "portal.enable.sources";
    
    public static final String PORTAL_ENABLE_RSS = "portal.enable.rss";
    
    public static final String PORTAL_ENABLE_CHRONICLE = "portal.enable.chronicle";
    
    public static final String PORTAL_ENABLE_MAPS = "portal.enable.maps";
    
    public static final String PORTAL_ENABLE_DISCLAIMER = "portal.enable.disclaimer";
    
    public static final String PORTAL_ENABLE_PRIVACY = "portal.enable.privacy";
    
    public static final String PORTAL_ENABLE_DEFAULT_GROUPING_DOMAIN = "portal.enable.default.grouping.domain";
    
    public static final String PORTAL_ENABLE_SNS_LOGO = "portal.enable.sns.logo";
    
    public static final String PORTAL_ENABLE_NEW_USER = "portal.enable.new.user";
    
    public static final String COMPONENT_MONITOR_ALERT_EMAIL_SUBJECT = "component.monitor.alert.email.subject";
        
    public static final String COMPONENT_MONITOR_ALERT_EMAIL_SENDER = "component.monitor.alert.email.sender";
    
    public static final String COMPONENT_MONITOR_DEFAULT_EMAIL = "component.monitor.default.email";
    
    public static final String COMPONENT_MONITOR_SNS_LOGIN = "component.monitor.sns.login";
    
    public static final String COMPONENT_MONITOR_SNS_PW = "component.monitor.sns.password";
    
    // contains the short version of all supported languages
    public static final String LANGUAGES_SHORT = "languages.short";
    
    // the specific language is added after this variable (e.g. languages.names.de) 
    public static final String LANGUAGES_NAMES = "languages.names.";
    
    // the url to the upgrade server
    public static final String UPGRADE_SERVER_URL = "upgrade.server.url";
    
    public static final String UPGRADE_SERVER_USERNAME = "upgrade.server.username";
    
    public static final String UPGRADE_SERVER_PW = "upgrade.server.password";

    // disable button and textfield for edit partner/provider 
    public static final String DISABLE_PARTNER_PROVIDER_EDIT = "portal.disable.partner.provider.edit";

    // Hidden iPlug ID list
    public static final String HIDE_IPLUG_ID_LIST = "hide.in.connected.iplugs";
    
    // User admin: show max row of users in a table
    public static final String USER_ADMIN_MAX_ROW = "admin.user.max.row";
    
    // User admin: sort column by default
    public static final String USER_ADMIN_SORT_COLUMN = "admin.user.sort.column";
    
    public static final String PORTAL_SEARCH_LANGUAGE_INDEPENDENT = "portal.search.language.independent";

    public static final String PORTAL_ENABLE_APPLICATION= "portal.enable.application";
    
    // Hide CMS items for changing on admin page
    public static final String PORTAL_ADMIN_HIDE_CMS_ITEMS = "portal.admin.hide.cms.items";
    
    // Enable facete on search hits
    public static final String PORTAL_ENABLE_SEARCH_FACETE = "portal.search.facete.enable";
    
    public static final String PORTAL_SEARCH_FACETE_SUB_COUNT = "portal.search.facete.sub.count";
    
    public static final String PORTAL_SEARCH_FACETE_MAP_CENTER = "portal.search.facete.map.center";
    
    public static final String PORTAL_SEARCH_HIDDEN_DATATYPES = "portal.search.hidden.datatype";
    
    public static final String PORTAL_SEARCH_HIT_PARTNER_LOGO = "portal.search.hit.partner.logo";
    
    public static final String PORTAL_SEARCH_HIT_CUT_TITLE = "portal.search.hit.cut.title";
    
    public static final String PORTAL_ADMIN_NUMBER_ROW_PROVIDER = "portal.admin.number.row.provider";
    
    public static final String PORTAL_ADMIN_NUMBER_ROW_PARTNER = "portal.admin.number.row.partner";
    
    // Link to CSW Interface
    public static final String CSW_INTERFACE_URL = "csw.interface.url";
    
    public static final String PORTAL_CONTACT_UPLOAD_ENABLE = "email.contact.upload.enable";
    public static final String PORTAL_CONTACT_UPLOAD_SIZE = "email.contact.upload.size";
    
    public static final String PORTAL_DETAIL_VIEW_HIDDEN_KEYWORDS = "portal.detail.view.hidden.keywords";
    
    public static final String PORTAL_DETAIL_VIEW_LIMIT_REFERENCES = "portal.detail.view.limit.references";
    
    public static final String PORTAL_SEARCH_EMPTY_QUERY = "portal.search.empty.query";

    public static final String PORTAL_SEARCH_EXTEND_QUERY = "portal.search.extend.query";

    public static final String PORTAL_SEARCH_RESET_QUERY = "portal.search.reset.query";

    public static final String CATEGORY_TEASER_SEARCH_QUERY = "category.teaser.search.query";
    
    public static final String HIT_TEASER_SEARCH_QUERY = "hit.teaser.search.query";

    public static final String HIT_TEASER_SEARCH_REQUESTEDFIELDS = "hit.teaser.search.requestedfields";

    public static final String HIT_TEASER_SEARCH_COUNT = "hit.teaser.search.count";
    
    public static final String CATEGORY_TEASER_SEARCH_FACETS_TYP = "category.teaser.search.facets.typ";
    
    public static final String CATEGORY_TEASER_SEARCH_COLUMN_MAX = "category.teaser.search.column.max";
    
    public static final String PORTAL_MAPCLIENT_URL = "portal.mapclient.url";
    
    public static final String PORTAL_MAPCLIENT_QUERY = "portal.mapclient.query";
    public static final String PORTAL_MAPCLIENT_QUERY_2 = "portal.mapclient.query.2";
    public static final String PORTAL_MAPCLIENT_QUERY_3 = "portal.mapclient.query.3";
    public static final String PORTAL_MAPCLIENT_QUERY_4 = "portal.mapclient.query.4";
    
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_10_CHECKED = "portal.mapclient.uvp.category.10.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_11_CHECKED = "portal.mapclient.uvp.category.11.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_12_CHECKED = "portal.mapclient.uvp.category.12.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_1314_CHECKED = "portal.mapclient.uvp.category.1314.checked";

    public static final String PORTAL_MAPCLIENT_LEAFLET_POSITION = "portal.mapclient.leaflet.position";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS = "portal.mapclient.leaflet.bg.layer.wmts";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMS = "portal.mapclient.leaflet.bg.layer.wms";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION = "portal.mapclient.leaflet.bg.layer.attribution";
    
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN = "portal.mapclient.uvp.category.dev.plan";

    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN_CHECKED = "portal.mapclient.uvp.category.dev.plan.checked";

    public static final String PORTAL_MAPCLIENT_UVP_QUERY_LEGEND = "portal.mapclient.uvp.query.legend";

    public static final String PORTAL_PARTNER_LIST_QUERY = "portal.partner.list.query";

    public static final String PORTAL_FORM_REGEX_CHECK_LOGIN = "portal.form.regex.check.login";

    public static final String PORTAL_FORM_REGEX_CHECK_MAIL = "portal.form.regex.check.mail";

    public static final String PORTAL_FORM_LENGTH_CHECK_LOGIN = "portal.form.length.check.login";

    public static final String PORTAL_FORM_STRENGTH_CHECK_PW = "portal.form.strength.check.passsword";

    public static final String PORTAL_DETAIL_UPLOAD_PATH_INDEX= "portal.detail.upload.path.index";

    // private stuff
    private static PortalConfig instance = null;

    private static final Logger log = LoggerFactory.getLogger(PortalConfig.class);

    public static synchronized PortalConfig getInstance() {
        if (instance == null) {
            try {
                instance = new PortalConfig();
                Configuration config = new ConfigBuilder<Configuration>(Configuration.class).build();
                config.initialize();
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Error loading the portal config application config file. (ingrid-portal-apps.properties)", e);
                }
            }
        }
        return instance;
    }

    private PortalConfig() throws ConfigurationException {
        super("ingrid-portal-apps.properties");
        //this.setReloadingStrategy(ReloadingStrategy)
        URL urlProfile = this.getClass().getResource("/ingrid-portal-apps.profile.properties");
        URL urlOverride = this.getClass().getResource("/ingrid-portal-apps.override.properties");
        updateProperties(urlProfile);
        updateProperties(urlOverride);
    }
    
    private void updateProperties(URL url) throws ConfigurationException {
        if (url != null) {
            File f = new File(url.getPath());
            PropertiesConfiguration userConfig = new PropertiesConfiguration(f);
            Iterator<String> it = userConfig.getKeys();
            while (it.hasNext()) {
                String key = it.next();
                this.setProperty(key, userConfig.getProperty(key));
            }
        }
    }
}
