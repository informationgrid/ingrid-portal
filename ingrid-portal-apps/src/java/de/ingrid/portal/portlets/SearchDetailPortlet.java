/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import de.ingrid.utils.*;
import org.apache.commons.io.IOUtils;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.EscapeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.geo.utils.transformation.GmlToWktTransformUtil;
import de.ingrid.geo.utils.transformation.WktToGeoJsonTransformUtil;
import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UniversalSorter;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsPortletServeResources;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsUvpZipDownload;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.IPlugVersionInspector;
import de.ingrid.portal.search.detail.DetailDataPreparer;
import de.ingrid.portal.search.detail.DetailDataPreparerFactory;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class SearchDetailPortlet extends GenericVelocityPortlet {
    private static final Logger log = LoggerFactory.getLogger(SearchDetailPortlet.class);

    private static final String TEMPLATE_DETAIL_GENERIC = "/WEB-INF/templates/detail/search_detail_generic.vm";

    private static final String TEMPLATE_DETAIL_IDF_2_0_0 = "/WEB-INF/templates/detail/search_detail_idf_2_0.vm";

    // ecs fields that represent a date, used for date parsing and formating
    private List dateFields = null;

    private HashMap replacementFields = new HashMap();

    protected XPathUtils xPathUtils = null;

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        String paramURL = request.getParameter( "url" );

        String login = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN);
        String password = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_PASSWORD);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());

        if(paramURL != null){
            if (resourceID.equals( "httpURL" )) {
                UtilsPortletServeResources.getHttpUrlLength(paramURL, login, password, response);
            }
            if (resourceID.equals( "httpURLDataType" )) {
                UtilsPortletServeResources.getHttpUrlDatatype(paramURL, login, password, response);
            }
            if (resourceID.equals( "httpURLImage" )) {
                UtilsPortletServeResources.getHttpUrlImage(paramURL, response, resourceID);
            }
        }

        if (resourceID.equals( "httpURLDownloadUVP" )) {
            String uuid = request.getParameter( "docuuid" );
            String plugid = request.getParameter( "plugid" );
            File zip = UtilsUvpZipDownload.searchFilesToCreateZip(uuid, plugid, messages, xPathUtils);
            if(zip != null) {
                try(
                    OutputStream outputStream = response.getPortletOutputStream();
                    FileInputStream fileInputStream = new FileInputStream(zip.getAbsoluteFile());
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                ){
                    response.setContentType( "application/zip" );
                    response.addProperty("Content-Disposition", "attachment; filename=\"" + zip.getName() + "\"");
                    response.addProperty("Content-Length", Long.toString(zip.length()));
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    while ((len = bufferedInputStream.read(buffer)) != -1) {
                      outputStream.write(buffer, 0, len);
                    }
                }
            }
        }

        if (resourceID.equals( "httpURLDownloadUVPCreate" )) {
            String uuid = request.getParameter( "docuuid" );
            String plugid = request.getParameter( "plugid" );
            File zip = UtilsUvpZipDownload.searchFilesToCreateZip(uuid, plugid, messages, xPathUtils);
            if(zip != null && zip.exists()) {
                response.setContentType( "text/plain" );
                response.getWriter().write( Files.size(zip.toPath()) + "" );
            }
        }
    }

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // get fields from config that should be treated as date fields
        dateFields = Arrays.asList(PortalConfig.getInstance().getStringArray(PortalConfig.UDK_FIELDS_DATE));

        xPathUtils = new XPathUtils(new IDFNamespaceContext());
        // get translation(replacement) rules from config file
        // map(map)
        String[] translations = PortalConfig.getInstance().getStringArray(PortalConfig.UDK_FIELDS_TRANSLATE);
        if (translations != null) {
            for (int i=0; i<translations.length; i++) {
                String[] translation = translations[i].split("\\|");
                if (!replacementFields.containsKey(translation[0])) {
                    replacementFields.put(translation[0], new HashMap());
                }
                Map replacements = (Map)replacementFields.get(translation[0]);
                replacements.put(translation[1], convertLangCode(translation[2]));
            }
        }
    }

    private String convertLangCode(String oldCode) {
        if (oldCode.equals("121"))
            return Integer.toString(UtilsLanguageCodelist.getCodeFromShortcut("de"));
        else if (oldCode.equals("94"))
            return Integer.toString(UtilsLanguageCodelist.getCodeFromShortcut("en"));
        else {
            return oldCode;
        }
    }

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        long startTimer = 0;

        if (log.isDebugEnabled()) {
            log.debug("Start building detail view.");
            startTimer = System.currentTimeMillis();
        }

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        context.put("lang", "de".equalsIgnoreCase(request.getLocale().getLanguage().toLowerCase()) ? "" : "en");
        context.put("Codelists", CodeListServiceFactory.instance());

        // add velocity utils class
        context.put("tool", new UtilsVelocity());
        context.put("stringTool", new UtilsString());
        context.put("escTool", new EscapeTool());
        context.put("sorter", new UniversalSorter(Locale.GERMAN) );

        // Geotools
        context.put("geoGmlToWkt", GmlToWktTransformUtil.class);
        context.put("geoWktToGeoJson", WktToGeoJsonTransformUtil.class);

        context.put("transformCoupledCSWUrl", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_TRANSFORM_COUPLED_CSW_URL, false));

        context.put("enableMapLink", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false));
        context.put("mapLinksNewTab", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPS_LINKS_NEW_TAB, false ));

        context.put("leafletEpsg", PortalConfig.getInstance().getString( PortalConfig.PORTAL_MAPCLIENT_LEAFLET_EPSG, "3857"));

        context.put( "leafletBgLayerWMTS", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS));
        context.put( "leafletBgLayerAttribution", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION));
        context.put( "leafletBgLayerOpacity", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_OPACITY));

        context.put("showHitPartnerLogo", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_PARTNER_LOGO, false));

        boolean detailUseParamPlugid = PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_DETAIL_USE_PARAMETER_PLUGID);
        context.put("detailUseParamPlugid", detailUseParamPlugid);

        String [] leafletBgLayerWMS = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMS);
        String leafletBgLayerWMSURL = leafletBgLayerWMS[0];
        if(leafletBgLayerWMSURL.length() > 0 && leafletBgLayerWMS.length > 1){
            context.put( "leafletBgLayerWMSUrl", leafletBgLayerWMSURL);
            StringBuilder leafletBgLayerWMSName = new StringBuilder("");
            for (int i = 1; i < leafletBgLayerWMS.length; i++) {
                leafletBgLayerWMSName.append(leafletBgLayerWMS[i]);
                if(i < (leafletBgLayerWMS.length - 1)) {
                    leafletBgLayerWMSName.append(",");
                }
            }
            context.put( "leafletBgLayerWMSName", leafletBgLayerWMSName.toString());
        }

        ResourceURL restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURL" );
        request.setAttribute( "restUrlHttpGet", restUrl.toString() );

        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLImage" );
        request.setAttribute( "restUrlHttpGetImage", restUrl.toString() );

        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLDataType" );
        request.setAttribute( "restUrlHttpGetDataType", restUrl.toString() );

        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLDownloadUVPCreate" );
        request.setAttribute( "restUrlHttpDownloadUVPCreate", restUrl.toString() );

        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLDownloadUVP" );
        request.setAttribute( "restUrlHttpDownloadUVP", restUrl.toString() );

        try {
            // check whether we come from google (no IngridSessionPreferences)
            boolean noIngridSession = false;
            IngridSessionPreferences ingridPrefs =
                (IngridSessionPreferences) request.getPortletSession().getAttribute(
                    IngridSessionPreferences.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
            if (ingridPrefs == null) {
                noIngridSession = true;
            }
            context.put("noIngridSession", noIngridSession);

            String testIDF = request.getParameter("testIDF");
            String cswURL = request.getParameter("cswURL");
            String docUuid = request.getParameter("docuuid");
            String oac = request.getParameter("oac");
            if (oac == null) {
                // be lenient here, we don't care about capitalization :)
                oac = request.getParameter("OAC");
            }
            String iplugId = request.getParameter("plugid");
            if(iplugId != null) {
                iplugId = iplugId.toLowerCase();
            }
            boolean isAddress = "address".equals( request.getParameter("type") );
            IngridHit hit = null;
            PlugDescription plugDescription = null;
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            String iPlugVersion = null;
            Record record = null;

            if (iplugId != null && iplugId.length() > 0) {
                plugDescription = ibus.getIPlug(iplugId);
                iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
            }
            // try to get the result for a objects UUID
            if (docUuid != null && docUuid.length() > 0) {
                // remove possible invalid characters
                docUuid = UtilsQueryString.normalizeUuid(docUuid);
                String qStr = null;
                String qPlugId = "";
                if(detailUseParamPlugid) {
                    qPlugId = "\" iplugs:\"" + iplugId.trim();
                }
                if (isAddress) {
                    qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":\"" + docUuid.trim() + qPlugId + "\" ranking:score datatype:address";
                } else {
                    qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + qPlugId + "\" ranking:score";
                }

                IngridQuery q = QueryStringParser.parse(qStr);
                IngridHits hits = ibus.searchAndDetail(q, 1, 1, 0, 3000, PortalConfig.getInstance().getStringArray(PortalConfig.QUERY_DETAIL_REQUESTED_FIELDS));

                if (hits.length() == 0) {
                    log.error("No record found for document uuid:" + docUuid.trim() + (detailUseParamPlugid ? " using iplug: " + iplugId.trim() : ""));

                    qStr = Settings.HIT_KEY_ORG_OBJ_ID + ":\"" + docUuid.trim() + qPlugId + "\" ranking:score";
                    q = QueryStringParser.parse(qStr);
                    hits = ibus.searchAndDetail(q, 1, 1, 0, 3000, PortalConfig.getInstance().getStringArray(PortalConfig.QUERY_DETAIL_REQUESTED_FIELDS));
                    if(hits.length() == 0){
                        log.error("No object record found for document uuid:" + docUuid.trim() + " for field: " + Settings.HIT_KEY_ORG_OBJ_ID);
                        if (isAddress) {
                            qStr = Settings.HIT_KEY_OBJ_ID + ":" + docUuid.trim() + " ranking:score";
                        } else {
                            qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":" + docUuid.trim() + " ranking:score";
                        }
                          q = QueryStringParser.parse(qStr);
                        hits = ibus.searchAndDetail(q, 1, 1, 0, 3000, PortalConfig.getInstance().getStringArray(PortalConfig.QUERY_DETAIL_REQUESTED_FIELDS));
                        if(hits.length() == 0){
                            log.error("No record found for document uuid:" + docUuid.trim());
                        }else{
                            hit = hits.getHits()[0];
                        }
                    }else{
                        hit = hits.getHits()[0];
                    }
                } else {
                    hit = hits.getHits()[0];
                }
            }
            // if no UUID is set, but an OAC is set, retrieve the document and the UUID from there
            else if (oac != null && oac.length() > 0) {
                // remove possible invalid characters
                oac = UtilsQueryString.normalizeUuid(oac);
                String qStr = "oac:\"" + oac + "\" ranking:score";

                IngridQuery q = QueryStringParser.parse(qStr);
                IngridHits hits = ibus.searchAndDetail(q, 1, 1, 0, 3000, PortalConfig.getInstance().getStringArray(PortalConfig.QUERY_DETAIL_REQUESTED_FIELDS));

                if (hits.length() == 0) {
                    log.error("No record found for document oac:" + oac.trim());
                } else {
                    hit = hits.getHits()[0];
                    docUuid = hit.getString(IngridDocument.DOCUMENT_ID);
                }
            } else {
                String documentId = request.getParameter("docid");
                if(documentId != null) {
                    hit = new IngridHit();
                    hit.setDocumentId(documentId);
                    hit.setPlugId(iplugId);
                    context.put("docId", documentId);
                    // backward compatibilty where docId was integer
                    try {
                        hit.putInt( 0, Integer.valueOf( documentId ) );
                    } catch (NumberFormatException ex) { /* ignore */ }
                }
            }

            if (hit != null) {
                if(plugDescription == null){
                    iplugId = hit.getPlugId();
                    plugDescription = ibus.getIPlug(iplugId);
                    iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
                }
                IngridHitDetail detail = hit.getHitDetail();
                if(detail != null) {
                    String title = detail.getTitle();
                    if(title != null) {
                        context.put("title", title);
                    }
                }
                context.put("docUuid", docUuid);
                context.put("plugId", iplugId);
                context.put("docId", hit.getDocumentId());

                String[] partners = plugDescription.getPartners();
                String plugPartner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER, "bund");
                if(partners != null && partners.length > 0) {
                    if(partners.length > 1) {
                        List<String> tmpPartners = new ArrayList<String>(Arrays.asList(partners));
                        tmpPartners.remove("bund");
                        partners = tmpPartners.toArray(new String[0]);
                    }
                    plugPartner = partners[0];
                    context.put("plugPartner", plugPartner);
                    String[] excludeExtendPartners = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_DETAIL_EXCLUDE_EXTEND_PARTNER);
                    boolean hasToExclude = false;
                    for (String excludeExtendPartner : excludeExtendPartners) {
                        if(excludeExtendPartner.equals(plugPartner)){
                            hasToExclude = true;
                        }
                    }
                    if(!hasToExclude){
                        context.put("plugPartnerString", UtilsDB.getPartnerFromKey(plugPartner));
                    }
                }

                if(plugDescription.getProviders() != null) {
                    ArrayList<String> plugProviders = new ArrayList<>();
                    for (String provider : plugDescription.getProviders()) {
                        String dbProvider = UtilsDB.getProviderFromKey(provider);
                        if(dbProvider != null) {
                            plugProviders.add(dbProvider);
                        }
                    }
                    context.put("plugProviders", plugProviders);
                }

                if(plugDescription.getDataSourceName() != null) {
                    context.put("plugDataSourceName", plugDescription.getDataSourceName());
                }
                record = ibus.getRecord(hit);
            // TODO: remove code after the iplugs deliver IDF records
            } else if (testIDF != null) {
                // create IDF record, see below how the record will be filled
                record = new Record();
                iPlugVersion = IPlugVersionInspector.VERSION_IDF_2_0_0_OBJECT;
            } else if (cswURL != null) {
                record = new Record();
                iPlugVersion = IPlugVersionInspector.VERSION_IDF_2_0_0_OBJECT;
            }

            if (record == null) {
                   log.error("No record found for document id:" + (hit != null ? hit.getDocumentId() : null) + " using iplug: " + iplugId + " for request: " + ((RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE)).getRequest().getRequestURL() + "?" + ((RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE)).getRequest().getQueryString());
            } else {

                // set language code list
                HashMap sysLangHashs = new HashMap();

                HashMap sysLangHash = new HashMap();
                sysLangHash.put("sys_language.lang_id", Integer.toString(UtilsLanguageCodelist.getCodeFromShortcut("de")));
                sysLangHash.put("sys_language.name", "Deutsch");
                sysLangHash.put("sys_language.description", "Deutsch");
                sysLangHash.put("sys_language.def_lang", "1");
                sysLangHashs.put(Integer.toString(UtilsLanguageCodelist.getCodeFromShortcut("de")), sysLangHash);

                sysLangHash = new HashMap();
                sysLangHash.put("sys_language.lang_id", Integer.toString(UtilsLanguageCodelist.getCodeFromShortcut("en")));
                sysLangHash.put("sys_language.name", "English");
                sysLangHash.put("sys_language.description", "English");
                sysLangHash.put("sys_language.def_lang", "1");
                sysLangHashs.put(Integer.toString(UtilsLanguageCodelist.getCodeFromShortcut("en")), sysLangHash);

                context.put("sysLangList", sysLangHashs);

                // put codelist fetcher into context
                context.put("codeList", new IngridSysCodeList(request.getLocale()));

                DetailDataPreparerFactory ddpf = new DetailDataPreparerFactory(context, iplugId, dateFields, request, response, replacementFields);

                if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDF_2_0_0_OBJECT) || iPlugVersion.equals(IPlugVersionInspector.VERSION_IDF_2_0_0_ADDRESS)) {
                    setDefaultViewPage(TEMPLATE_DETAIL_IDF_2_0_0);
                } else {
                    setDefaultViewPage(TEMPLATE_DETAIL_GENERIC);
                }

                // if "testIDF"-Parameter exist, use DetailDataPreparer for "IDF" version
                // TODO: remove code after the iplugs deliver IDF records
                if (testIDF != null) {
                    File file = new File(testIDF);
                    if(file.exists()){
                        StringBuilder stringBuilder = new StringBuilder();
                        Scanner scanner = new Scanner(file);
                        try {
                            while(scanner.hasNextLine()) {
                                stringBuilder.append(scanner.nextLine() + "\n");
                            }
                        } finally {
                            scanner.close();
                        }
                        record.put("data", stringBuilder.toString());
                        record.put("compressed", "false");
                    }

                    DetailDataPreparer detailPreparer;
                    detailPreparer = ddpf.getDetailDataPreparer(IPlugVersionInspector.VERSION_IDF_2_0_0_OBJECT);
                    detailPreparer.prepare(record);
                } else if (cswURL != null) {
                    URL url = new URL(cswURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if(br != null) {
                        record.put("data", IOUtils.toString(br));
                        record.put("compressed", "false");
                    }
                    DetailDataPreparer detailPreparer;
                    detailPreparer = ddpf.getDetailDataPreparer(IPlugVersionInspector.VERSION_IDF_2_0_0_OBJECT);
                    detailPreparer.prepare(record);
                } else {
                    long startTimer2 = 0;
                    if (log.isDebugEnabled()) {
                        startTimer2 = System.currentTimeMillis();
                    }
                    DetailDataPreparer detailPreparer = ddpf.getDetailDataPreparer(iPlugVersion);
                    detailPreparer.prepare(record);
                    if (log.isDebugEnabled()) {
                        log.debug("Executed detail preparer '" + detailPreparer.getClass().getName() + "' within " + (System.currentTimeMillis() - startTimer2) + "ms.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error getting result record. doc id is no valid number", e);
            } else if (log.isInfoEnabled()) {
                log.info("Error getting result record. doc id is no valid number" + "(switch to debug mode for exception.)");
            }
        } catch (Throwable t) {
            log.error("Error getting result record.", t);
        }

        if (log.isDebugEnabled()) {
            log.debug("Finished preparing detail data for view within " + (System.currentTimeMillis() - startTimer) + "ms.");
            startTimer = System.currentTimeMillis();
        }

        super.doView(request, response);

        // Add page title by hit title
        if(context.get("title") != null){
            String title = (String) context.get("title");
            // Remove localisation
            if(title != null) {
                title = title.split("#locale-")[0];
            }
            org.w3c.dom.Element titleElement = response.createElement("title");
            titleElement.setTextContent(title + " - " + messages.getString("search.detail.portal.institution"));
            response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, titleElement);
        }
        // Add page doi by hit for dublin-core
        if(context.get("doiHeadMeta") != null){
            org.w3c.dom.Element link = response.createElement("link");
            link.setAttribute("rel", "schema.DC");
            link.setAttribute("href", "http://purl.org/dc/elements/1.1/");
            response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, link);
            if(context.get("title") != null){
                org.w3c.dom.Element meta = response.createElement("meta");
                meta.setAttribute("name", "DC.title");
                meta.setAttribute("content", (String) context.get("title"));
                response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, meta);
            }
            org.w3c.dom.Element meta = response.createElement("meta");
            meta.setAttribute("name", "DC.identifier");
            meta.setAttribute("content", (String) context.get("doiHeadMeta"));
            response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, meta);
        }
        if (log.isDebugEnabled()) {
            log.debug("Finished rendering detail data view within " + (System.currentTimeMillis() - startTimer) + "ms.");
        }
    }
}
