/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.configbuilder.ConfigBuilder;

import de.ingrid.portal.Configuration;

/**
 * Provides access to the ingrid portal preferences.
 * 
 * @author joachim@wemove.com
 */
public class PortalConfig extends PropertiesConfiguration {

    /** how old can rss news be, before the are deleted from news history in days */
    public final static String RSS_HISTORY_DAYS = "rss.history.days";
    
    /** define number of rss feeds for RSSNewsTeaserPortlet */
    public final static String PORTAL_RSS_NEWS_NUMBER = "portal.rss.news.number";

    /**
     * timout for queries in ms should be larger than query.timout.ranked and
     * query.timout.unranked because ranked and unranked query is encapsulated
     * inside a threaded query
     */
    public final static String QUERY_TIMEOUT_THREADED = "query.timeout.threaded";

    /** timout for ranked queries in ms */
    public final static String QUERY_TIMEOUT_RANKED = "query.timeout.ranked";
    
    /** requested fields for search queries in portal */
    public final static String QUERY_REQUESTED_FIELDS = "portal.query.requestedfields";

    /** default timeout for sns queries in ms */
    public final static String SNS_TIMEOUT_DEFAULT = "sns.timeout.default";
    
    /** default length of chronicle hits  */
    public final static String SNS_CHRONICLE_HITS_LENGTH = "sns.chronicle.hits.length";
    
    /**
     * always read values from database or only once and then from cache, true
     * or false
     */
    public final static String ALWAYS_REREAD_DB_VALUES = "db.reread";

    public final static String EMAIL_REGISTRATION_CONFIRMATION_SENDER = "email.registration.confirmation.sender";

    public final static String EMAIL_REGISTRATION_CONFIRMATION_URL = "email.registration.confirmation.url";

    public static final String EMAIL_SMTP_SERVER = "email.smtp.server";
    public static final String EMAIL_SMTP_USER = "email.smtp.user";
    public static final String EMAIL_SMTP_PASSWORD = "email.smtp.password";
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
    
    public static final String PORTAL_ENABLE_ACCOUNT_QUESTION = "enable.account.registration.question";
    
    public static final String PORTAL_ENABLE_SNS_LOGO = "portal.enable.sns.logo";
    
    public static final String COMPONENT_MONITOR_ALERT_EMAIL_SUBJECT = "component.monitor.alert.email.subject";
        
    public static final String COMPONENT_MONITOR_ALERT_EMAIL_SENDER = "component.monitor.alert.email.sender";
    
    public static final String COMPONENT_MONITOR_DEFAULT_EMAIL = "component.monitor.default.email";
    
    public static final String COMPONENT_MONITOR_SNS_LOGIN = "component.monitor.sns.login";
    
    public static final String COMPONENT_MONITOR_SNS_PASSWORD = "component.monitor.sns.password";
    
    public static final String COMPONENT_MONITOR_UPDATE_ALERT_EMAIL_SUBJECT = "component.monitor.update.alert.email.subject";
    
    public static final String TEASER_WEATHER_DWD_PATH = "teaser.weather.dwd.path";
    
    public static final String TEASER_WEATHER_DWD_MOVIE = "teaser.weather.dwd.movie";
    
    // contains the short version of all supported languages
    public static final String LANGUAGES_SHORT = "languages.short";
    
    // the specific language is added after this variable (e.g. languages.names.de) 
    public static final String LANGUAGES_NAMES = "languages.names.";
    
    // the url to the upgrade server
    public static final String UPGRADE_SERVER_URL = "upgrade.server.url";
    
    public static final String UPGRADE_SERVER_USERNAME = "upgrade.server.username";
    
    public static final String UPGRADE_SERVER_PASSWORD = "upgrade.server.password";

    // disable button and textfield for edit partner/provider 
    public static final String DISABLE_PARTNER_PROVIDER_EDIT = "portal.disable.partner.provider.edit";
    
    // disable piwik
    public static final String ENABLE_PIWIK = "portal.enable.piwik";
    
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
    
    public static final String PORTAL_SEARCH_FACETE_MAP_LAYER = "portal.search.facete.map.layer";
    
    public static final String PORTAL_SEARCH_HIDDEN_DATATYPES = "portal.search.hidden.datatype";
    
    public static final String PORTAL_SEARCH_HIT_PARTNER_LOGO = "portal.search.hit.partner.logo";
    
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

    public static final String CATEGORY_TEASER_SEARCH_QUERY = "category.teaser.search.query";
   
    public static final String CATEGORY_TEASER_SEARCH_FACETS_TYP = "category.teaser.search.facets.typ";
    
    public static final String CATEGORY_TEASER_SEARCH_COLUMN_MAX = "category.teaser.search.column.max";
    
    public static final String PORTAL_MAPCLIENT_URL = "portal.mapclient.url";
    
    public static final String PORTAL_MAPCLIENT_QUERY = "portal.mapclient.query";
    
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN_URL = "portal.mapclient.uvp.category.dev-plan.url";
    
    public static final String PORTAL_PARTNER_LIST_QUERY = "portal.partner.list.query";

    // private stuff
    private static PortalConfig instance = null;

    private static Configuration config = null;

    private final static Logger log = LoggerFactory.getLogger(PortalConfig.class);

    public static synchronized PortalConfig getInstance() {
        if (instance == null) {
            try {
                instance = new PortalConfig();
                config = new ConfigBuilder<Configuration>(Configuration.class).build();
                config.initialize();
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(
                            "Error loading the portal config application config file. (ingrid-portal-apps.properties)",
                            e);
                }
            }
        }
        return instance;
    }

    private PortalConfig() throws Exception {
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
