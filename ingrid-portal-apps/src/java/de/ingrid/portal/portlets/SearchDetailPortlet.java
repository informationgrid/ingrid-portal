package de.ingrid.portal.portlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UniversalSorter;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.IPlugVersionInspector;
import de.ingrid.portal.search.detail.DetailDataPreparer;
import de.ingrid.portal.search.detail.DetailDataPreparerFactory;
import de.ingrid.portal.search.detail.DetailDataPreparerHelper;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.udk.UtilsLanguageCodelist;

public class SearchDetailPortlet extends GenericVelocityPortlet {
    private final static Log log = LogFactory.getLog(SearchDetailPortlet.class);

    private final static String TEMPLATE_DETAIL_GENERIC = "/WEB-INF/templates/search_detail_generic.vm";

    private final static String TEMPLATE_DETAIL_UNIVERSAL = "/WEB-INF/templates/search_detail_universal.vm";

    private final static String TEMPLATE_DETAIL_ECS = "/WEB-INF/templates/search_detail.vm";

    private final static String TEMPLATE_DETAIL_ECS_ADDRESS = "/WEB-INF/templates/search_detail_address.vm";

    private final static String TEMPLATE_DETAIL_IDF = "/WEB-INF/templates/search_detail_idf.vm";

    // ecs fields that represent a date, used for date parsing and formating
    private List dateFields = null;
    
    private HashMap replacementFields = new HashMap();

    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // get fields from config that should be treated as date fields
        dateFields = Arrays.asList(PortalConfig.getInstance().getStringArray(PortalConfig.UDK_FIELDS_DATE));
        
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

	public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // add velocity utils class
        context.put("tool", new UtilsVelocity());
        context.put("stringTool", new UtilsString());
        context.put("sorter", new UniversalSorter(Locale.GERMAN) );
        context.put("piwik", PortalConfig.getInstance().getString(PortalConfig.ENABLE_PIWIK));
        
        try {
        	// check whether we come from google (no IngridSessionPreferences)
        	boolean noIngridSession = false;
    		IngridSessionPreferences ingridPrefs =
    			(IngridSessionPreferences) request.getPortletSession().getAttribute(
    				IngridSessionPreferences.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
    		if (ingridPrefs == null) {
            	noIngridSession = true;    			
    		}
            context.put("noIngridSession", new Boolean(noIngridSession));
            	
            // TODO: Path of testing IDF xml file 
            String testIDF = request.getParameter("testIDF");
            
            String docUuid = request.getParameter("docuuid");
            String altDocumentId = request.getParameter("altdocid");
            String iplugId = request.getParameter("plugid");
            IngridHit hit = null;
            PlugDescription plugDescription = null;
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            String iPlugVersion = null;
            Record record = null;
            
            if (iplugId != null && iplugId.length() > 0) {

	            context.put("docUuid", docUuid);
	            context.put("plugId", iplugId);
	            
	            plugDescription = ibus.getIPlug(iplugId);
	            iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
	            
	            // try to get the result for a objects UUID
	            if (docUuid != null && docUuid.length() > 0) {
	            	// remove possible invalid characters
	            	docUuid = UtilsQueryString.normalizeUuid(docUuid);
	            	String qStr = null;
	                if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_9_DSC_OBJECT)) {
                	  qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
                  	} else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_8_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
	            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_ADDRESS)) {
	            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	            	} else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
	            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_OBJECT)){
	                	qStr = Settings.HIT_KEY_OBJ_ID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_ADDRESS)){
	                	qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":\"" + docUuid.trim() + "\" iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                } else {
	            		qStr = docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                }
	
	            	IngridQuery q = QueryStringParser.parse(qStr);
	            	IngridHits hits = ibus.search(q, 1, 1, 0, 3000);
	            	
	            	if (hits.length() < 1) {
	            		log.error("No record found for document uuid:" + docUuid.trim() + " using iplug: " + iplugId.trim());
	            		
	            	    qStr = Settings.HIT_KEY_OBJ_ID + ":" + docUuid.trim() + " ranking:score";
	  	                q = QueryStringParser.parse(qStr);
		            	hits = ibus.search(q, 1, 1, 0, 3000);
		            	if(hits.length() < 1){
		            		log.error("No object record found for document uuid:" + docUuid.trim());
		            		
		            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":" + docUuid.trim() + " ranking:score datatype:address";
		  	                q = QueryStringParser.parse(qStr);
			            	hits = ibus.search(q, 1, 1, 0, 3000);
			            	if(hits.length() < 1){
			            		log.error("No object record found for document uuid:" + docUuid.trim());
			            	}else{
			            		hit = hits.getHits()[0];
			            		if(plugDescription == null){
			            			iplugId = hit.getPlugId();
			            			plugDescription = ibus.getIPlug(iplugId);
			        	            iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
			            		}
			            	}
		            	}else{
		            		hit = hits.getHits()[0];
		            		if(plugDescription == null){
		            			iplugId = hit.getPlugId();
		            			plugDescription = ibus.getIPlug(iplugId);
		        	            iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
		            		}
		            	}
	            	} else {
	            		hit = hits.getHits()[0];
	            	}
	            } else if (altDocumentId != null && altDocumentId.length() > 0) {
	                hit = new IngridHit();
                    hit.put("alt_document_id", altDocumentId);
	                hit.setPlugId(iplugId);
	                hit.setDocumentId(0);
	            
	            } else {
	                int documentId = Integer.parseInt(request.getParameter("docid"));
	                hit = new IngridHit();
	                hit.setDocumentId(documentId);
	                hit.setPlugId(iplugId);
	                context.put("docId", documentId);
	            }
            }else{
            	log.error("No plugId set for detail.");
        		if(docUuid != null && docUuid.length() > 0){
        			String qStr = Settings.HIT_KEY_OBJ_ID + ":" + docUuid.trim() + " ranking:score";
        			IngridQuery q = QueryStringParser.parse(qStr);
        			IngridHits hits = ibus.search(q, 1, 1, 0, 3000);
	            	if(hits.length() < 1){
	            		log.error("No object record found for document uuid:" + docUuid.trim());
	            		
	            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":" + docUuid.trim() + " ranking:score datatype:address";
	  	                q = QueryStringParser.parse(qStr);
		            	hits = ibus.search(q, 1, 1, 0, 3000);
		            	if(hits.length() < 1){
		            		log.error("No object record found for document uuid:" + docUuid.trim());
		            	}else{
		            		hit = hits.getHits()[0];
		            		if(plugDescription == null){
		            			iplugId = hit.getPlugId();
		            			plugDescription = ibus.getIPlug(iplugId);
		        	            iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
		            		}
		            	}
	            	}else{
	            		hit = hits.getHits()[0];
	            		if(plugDescription == null){
	            			iplugId = hit.getPlugId();
	            			plugDescription = ibus.getIPlug(iplugId);
	        	            iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
	            		}
	            	}
            	}
            }

            if (hit != null) {
	            record = ibus.getRecord(hit);
            // TODO: remove code after the iplugs deliver IDF records
            } else if (testIDF != null) {
            	// create IDF record, see below how the record will be filled
            	record = new Record();
            	iPlugVersion = IPlugVersionInspector.VERSION_IDF_1_0_0_OBJECT;
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
                
                if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_9_DSC_OBJECT)) {
                  setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_8_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_ECS);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_ECS_ADDRESS);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_ADDRESS)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                }else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_OBJECT) || iPlugVersion.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_ADDRESS)) {
                    	setDefaultViewPage(TEMPLATE_DETAIL_IDF);
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
                	DetailDataPreparer detailPreparer = ddpf.getDetailDataPreparer(IPlugVersionInspector.VERSION_IDF_1_0_0_OBJECT);
	                detailPreparer.prepare(record);
                } else {
	                DetailDataPreparer detailPreparer = ddpf.getDetailDataPreparer(iPlugVersion);
	                detailPreparer.prepare(record);
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

        super.doView(request, response);
    }





    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String cmd = request.getParameter("cmd");
        if (cmd == null) {
            return;
        } else if (cmd.equals("doShowAddressDetail")) {
            String addrId = request.getParameter("addrId");
            String plugId = DetailDataPreparerHelper.getAddressPlugIdFromPlugId(request.getParameter("plugid"));
            if(log.isDebugEnabled()){
            	log.debug("doShowAddressDetail addrId: " + addrId);
            	log.debug("doShowAddressDetail plugid: " + plugId);
            }
            try {
                IngridHit hit = getAddressHit(addrId, plugId);
                response.setRenderParameter("docuuid", addrId);
                response.setRenderParameter("plugid", hit.getPlugId());
                if(log.isDebugEnabled()){
                	log.debug("doShowAddressDetail hit.getPlugId(): " + hit.getPlugId());
                }
                if (hit.get(".alt_document_id") != null) {
                    response.setRenderParameter("altdocid", (String) hit.get("alt_document_id"));
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error fetching address data for address id: " + addrId, e);
                } else if (log.isInfoEnabled()) {
                    log.info("Error fetching address data for address id: " + addrId + "(switch to debug mode for exception.)");
                }
            }
        } else if (cmd.equals("doShowObjectDetail")) {
            String objId = request.getParameter("objId");
            String plugId = DetailDataPreparerHelper.getPlugIdFromAddressPlugId(request.getParameter("plugid"));
            if(log.isDebugEnabled()){
            	log.debug("doShowObjectDetail objId: " + objId);
            	log.debug("doShowObjectDetail plugid: " + plugId);
            }
            try {
                IngridHit hit = getObjectHit(objId, plugId);
                response.setRenderParameter("docuuid", objId);
                response.setRenderParameter("plugid", hit.getPlugId());
                if(log.isDebugEnabled()){
                	log.debug("doShowObjectDetail hit.getPlugId(): " + hit.getPlugId());
                }
                if (hit.get(".alt_document_id") != null) {
                    response.setRenderParameter("altdocid", (String) hit.get("alt_document_id"));
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error fetching object data for object id: " + objId, e);
                } else {
                    log.info("Error fetching object data for object id: " + objId + "(switch to debug mode for exception.)");
                }
            }
        } else if (cmd.equals("doShowDocument")) {
        	if (request.getParameter("docid") != null) {
            	response.setRenderParameter("docid", request.getParameter("docid"));
            }
            if (request.getParameter("docuuid") != null) {
            	response.setRenderParameter("docuuid", request.getParameter("docuuid"));
            }
            response.setRenderParameter("plugid", request.getParameter("plugid"));
            if (request.getParameter("alt_document_id") != null) {
                response.setRenderParameter("altdocid", request.getParameter("alt_document_id"));
            }
            if (request.getParameter("maxORs") != null) {
                response.setRenderParameter("maxORs", request.getParameter("maxORs"));
            }
            if (request.getParameter("maxSubORs") != null) {
                response.setRenderParameter("maxSubORs", request.getParameter("maxSubORs"));
            }
        }
    }



    private IngridHit getAddressHit(String addrId, String iPlugId) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        ArrayList result = DetailDataPreparerHelper.getHits("T02_address.adr_id:".concat(addrId).concat(" iplugs:\"".concat(DetailDataPreparerHelper.getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, null);
        if (result.size() > 0) {
            return (IngridHit) result.get(0);
        } else {
            return null;
        }
    }

    private IngridHit getObjectHit(String objId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        ArrayList result = DetailDataPreparerHelper.getHits("T01_object.obj_id:".concat(objId).concat(" iplugs:\"".concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
                requestedMetadata, null);
        if (result.size() > 0) {
            return (IngridHit) result.get(0);
        } else {
            return null;
        }
    }


    
}