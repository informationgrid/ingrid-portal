/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
    public static final String QUERY_DETAIL_REQUESTED_FIELDS = "portal.query.detail.requestedfields";

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
    public static final String EMAIL_CONTACT_FORM_SENDER = "email.contact.form.sender";

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

    public static final String PORTAL_SEARCH_EXPORT_CSV = "portal.search.export.csv";

    public static final String PORTAL_SEARCH_EXPORT_CSV_REQUESTED_FIELDS = "portal.search.export.csv.requestedfields";

    public static final String PORTAL_SEARCH_RESTRICT_PARTNER_LEVEL = "portal.search.restrict.partner.level";

    public static final String PORTAL_HIERARCHY_CATALOGNAME_HIDDEN = "portal.hierarchy.catalogname.hidden";

    public static final String PORTAL_HIERARCHY_CATALOG_ADDRESS_CLOSE = "portal.hierarchy.catalog.address.close";

    public static final String PORTAL_LOGGER_RESOURCE = "portal.logger.resource";

    public static final String PORTAL_ENABLE_MEASURE = "portal.enable.measure";

    public static final String PORTAL_ENABLE_ABOUT = "portal.enable.about";

    public static final String PORTAL_ENABLE_PARTNER = "portal.enable.partner";

    public static final String PORTAL_ENABLE_SOURCES = "portal.enable.sources";

    public static final String PORTAL_ENABLE_RSS = "portal.enable.rss";

    public static final String PORTAL_ENABLE_MAPS = "portal.enable.maps";

    public static final String PORTAL_ENABLE_DISCLAIMER = "portal.enable.disclaimer";

    public static final String PORTAL_ENABLE_PRIVACY = "portal.enable.privacy";

    public static final String PORTAL_ENABLE_ACCESSIBILITY = "portal.enable.accessibility";

    public static final String PORTAL_ENABLE_RSS_PAGE = "portal.enable.rsspage";

    public static final String PORTAL_ENABLE_DEFAULT_GROUPING_DOMAIN = "portal.enable.default.grouping.domain";

    public static final String PORTAL_ENABLE_SNS_LOGO = "portal.enable.sns.logo";

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
    public static final String PORTAL_ENABLE_SEARCH= "portal.enable.search";
    public static final String PORTAL_ENABLE_CONTACT= "portal.enable.contact";

    // Hide CMS items for changing on admin page
    public static final String PORTAL_ADMIN_HIDE_CMS_ITEMS = "portal.admin.hide.cms.items";

    // Enable facete on search hits
    public static final String PORTAL_ENABLE_SEARCH_FACETE = "portal.search.facete.enable";

    public static final String PORTAL_SEARCH_FACETE_SUB_COUNT = "portal.search.facete.sub.count";

    public static final String PORTAL_SEARCH_FACETE_MAP_CENTER = "portal.search.facete.map.center";

    public static final String PORTAL_SEARCH_FACETE_DISPLAY_DATE_SELECTION = "portal.search.facete.display.date.selection";

    public static final String PORTAL_SEARCH_FACETE_MULTISELECTION = "portal.search.facete.multiselection";

    public static final String PORTAL_SEARCH_FACETE_MULTISELECTION_DISPLAY_EMPTY = "portal.search.facete.multiselection.display.empty";

    public static final String PORTAL_SEARCH_HIDDEN_DATATYPES = "portal.search.hidden.datatype";

    public static final String PORTAL_SEARCH_HIT_PARTNER_LOGO = "portal.search.hit.partner.logo";

    public static final String PORTAL_SEARCH_HIT_CUT_TITLE = "portal.search.hit.cut.title";

    public static final String PORTAL_SEARCH_HIT_CUT_SUMMARY = "portal.search.hit.cut.summary";

    public static final String PORTAL_SEARCH_HIT_CUT_SUMMARY_LINES = "portal.search.hit.cut.summary.lines";

    public static final String PORTAL_SEARCH_HIT_TRANSFORM_COUPLED_CSW_URL = "portal.search.hit.transform.coupled.csw.url";

    public static final String PORTAL_SEARCH_HIT_HIDE_GEODATASET_ON_OPENDATA = "portal.search.hit.hide.geodataset.on.opendata";

    public static final String PORTAL_SEARCH_HIT_HVD_DISPLAY_ICON = "portal.search.hit.hvd.display.icon";

    public static final String PORTAL_SEARCH_HIT_SUMMARY_ABSTRACT_FIELD = "portal.search.hit.summary.abstract.field";

    public static final String PORTAL_SEARCH_HIT_CUTTED_SUMMARY_HTML_NEW_LINE = "portal.search.hit.cutted.summary.html.new.line";

    public static final String PORTAL_ADMIN_NUMBER_ROW_PROVIDER = "portal.admin.number.row.provider";

    public static final String PORTAL_ADMIN_NUMBER_ROW_PARTNER = "portal.admin.number.row.partner";

    // Link to CSW Interface
    public static final String CSW_INTERFACE_URL = "csw.interface.url";

    public static final String RDF_INTERFACE_URL = "rdf.interface.url";
    
    public static final String PORTAL_CONTACT_UPLOAD_ENABLE = "email.contact.upload.enable";
    public static final String PORTAL_CONTACT_UPLOAD_SIZE = "email.contact.upload.size";

    public static final String PORTAL_CONTACT_PORTAL = "email.contact.portal";

    public static final String PORTAL_CONTACT_REMOVE_PARTNERS = "email.contact.remove.partners";

    public static final String PORTAL_DETAIL_VIEW_HIDDEN_KEYWORDS = "portal.detail.view.hidden.keywords";

    public static final String PORTAL_DETAIL_VIEW_LIMIT_REFERENCES = "portal.detail.view.limit.references";

    public static final String PORTAL_DETAIL_VIEW_UNLINK_ADDRESS = "portal.detail.view.unlink.address";

    public static final String PORTAL_DETAIL_REFERENCE_SYSTEM_LINK = "portal.detail.reference.system.link";
    public static final String PORTAL_DETAIL_REFERENCE_SYSTEM_LINK_REPLACE = "portal.detail.reference.system.link.replace";

    public static final String PORTAL_DETAIL_USE_PARAMETER_PLUGID = "portal.detail.use.parameter.plugid";

    public static final String PORTAL_DETAIL_EXCLUDE_EXTEND_PARTNER = "portal.detail.exclude.extend.partner";

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

    public static final String ATOM_DOWNLOAD_CLIENT_URL = "atom.download.client.url";
    public static final String ATOM_DOWNLOAD_CLIENT_PREFIX = "atom.download.client.prefix";

    public static final String PORTAL_MAPCLIENT_QUERY = "portal.mapclient.query";
    public static final String PORTAL_MAPCLIENT_QUERY_ADDITIONAL = "portal.mapclient.query.additional";
    public static final String PORTAL_MAPCLIENT_QUERY_2 = "portal.mapclient.query.2";
    public static final String PORTAL_MAPCLIENT_QUERY_2_ADDITIONAL = "portal.mapclient.query.2.additional";
    public static final String PORTAL_MAPCLIENT_QUERY_3 = "portal.mapclient.query.3";
    public static final String PORTAL_MAPCLIENT_QUERY_3_ADDITIONAL = "portal.mapclient.query.3.additional";
    public static final String PORTAL_MAPCLIENT_QUERY_4 = "portal.mapclient.query.4";
    public static final String PORTAL_MAPCLIENT_QUERY_4_ADDITIONAL = "portal.mapclient.query.4.additional";
    public static final String PORTAL_MAPCLIENT_QUERY_5 = "portal.mapclient.query.5";
    public static final String PORTAL_MAPCLIENT_QUERY_5_ADDITIONAL = "portal.mapclient.query.5.additional";

    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_10_CHECKED = "portal.mapclient.uvp.category.10.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_11_CHECKED = "portal.mapclient.uvp.category.11.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_12_CHECKED = "portal.mapclient.uvp.category.12.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_13_CHECKED = "portal.mapclient.uvp.category.13.checked";
    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_14_CHECKED = "portal.mapclient.uvp.category.14.checked";

    public static final String PORTAL_MAPCLIENT_LEAFLET_POSITION = "portal.mapclient.leaflet.position";
    public static final String PORTAL_MAPCLIENT_LEAFLET_EPSG = "portal.mapclient.leaflet.epsg";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS = "portal.mapclient.leaflet.bg.layer.wmts";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMS = "portal.mapclient.leaflet.bg.layer.wms";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION = "portal.mapclient.leaflet.bg.layer.attribution";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_OPACITY = "portal.mapclient.leaflet.bg.layer.opacity";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BBOX_INVERTED = "portal.mapclient.leaflet.bbox.inverted";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BBOX_COLOR = "portal.mapclient.leaflet.bbox.color";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BBOX_FILLOPACITY = "portal.mapclient.leaflet.bbox.fillOpacity";
    public static final String PORTAL_MAPCLIENT_LEAFLET_BBOX_WEIGHT = "portal.mapclient.leaflet.bbox.weight";

    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN = "portal.mapclient.uvp.category.dev.plan";

    public static final String PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN_CHECKED = "portal.mapclient.uvp.category.dev.plan.checked";

    public static final String PORTAL_MAPCLIENT_UVP_QUERY_LEGEND = "portal.mapclient.uvp.query.legend";

    public static final String PORTAL_PARTNER_LIST_QUERY = "portal.partner.list.query";

    public static final String PORTAL_FORM_REGEX_CHECK_LOGIN = "portal.form.regex.check.login";

    public static final String PORTAL_FORM_REGEX_CHECK_MAIL = "portal.form.regex.check.mail";

    public static final String PORTAL_FORM_LENGTH_CHECK_LOGIN = "portal.form.length.check.login";

    public static final String PORTAL_FORM_STRENGTH_CHECK_PW = "portal.form.strength.check.password";

    public static final String PORTAL_DETAIL_UPLOAD_PATH_INDEX= "portal.detail.upload.path.index";

    public static final String PORTAL_FORM_VALID_FILLOUT_DURATION_MIN = "portal.form.valid.fillout.duration.min";

    public static final String PORTAL_BWASTR_LOCATOR_INFO = "portal.bwastr.locator.info";
    public static final String PORTAL_BWASTR_LOCATOR_GEOK = "portal.bwastr.locator.geok";
    public static final String PORTAL_BWASTR_LOCATOR_EPSG = "portal.bwastr.locator.epsg";
    public static final String PORTAL_BWASTR_LOCATOR_GET_DATA_LOWER = "portal.bwastr.locator.get.data.lower";

    public static final String PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER = "portal.mapclient.uvp.requested.fields.marker";
    public static final String PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_DETAIL = "portal.mapclient.uvp.requested.fields.marker.detail";
    public static final String PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_BBOX = "portal.mapclient.uvp.requested.fields.marker.bbox";
    public static final String PORTAL_UVP_MAP_REQUESTED_FIELDS_BLP_MARKER = "portal.mapclient.uvp.requested.fields.blp.marker";
    public static final String PORTAL_UVP_MAP_REQUESTED_FIELDS_BLP_MARKER_DETAIL = "portal.mapclient.uvp.requested.fields.blp.marker.detail";
    public static final String PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_NUM = "portal.mapclient.uvp.requested.fields.marker.num";

    public static final String PORTAL_LOGIN_AUTH_FAILURES_LIMIT = "portal.login.auth.failures.limit";
    public static final String PORTAL_LOGIN_AUTH_FAILURES_TIME = "portal.login.auth.failures.time";
    public static final String PORTAL_LOGIN_REDIRECT = "portal.login.redirect";

    public static final String PORTAL_DETAIL_UVP_ZIP_PATH = "portal.detail.uvp.zip.path";
    public static final String PORTAL_DETAIL_UVP_ZIP_UPDATE_MIN = "portal.detail.uvp.zip.update.min";
    public static final String PORTAL_DETAIL_UVP_ZIP_DELETE_MIN = "portal.detail.uvp.zip.delete.min";

    public static final String PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN = "portal.detail.uvp.documents.htaccess.login";
    public static final String PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_PASSWORD = "portal.detail.uvp.documents.htaccess.password";

    public static final String PORTAL_DETAIL_DISTANCE_DPI = "portal.detail.distance.dpi";
    public static final String PORTAL_DETAIL_DISTANCE_METER = "portal.detail.distance.meter";

    public static final String PORTAL_MAPS_LINKS_NEW_TAB = "portal.map.links.new.tab";

    public static final String PORTAL_ADMINISTRATION_USER_ENABLED_OLDER_THAN = "portal.administration.user.enabled.older.than";
    public static final String PORTAL_ADMINISTRATION_USER_UNENABLED_OLDER_THAN = "portal.administration.user.unabled.older.than";
    public static final String PORTAL_ADMINISTRATION_USER_UNLOGGED_OLDER_THAN = "portal.administration.user.unlogged.older.than";

    public static final String PORTAL_MEASURE_URL = "portal.measure.url";
    public static final String PORTAL_MEASURE_NETWORKS_EXCLUDE = "portal.measure.networks.exclude";
    public static final String PORTAL_MEASURE_NETWORKS_INITIAL = "portal.measure.networks.initial";

    public static final String PORTAL_MAPCLIENT_UVP_GEOCODER_SERVICE_URL = "portal.mapclient.uvp.geocoder.service.url";

    public static final String PORTAL_IS_UVP = "portal.is.uvp";

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
