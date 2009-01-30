package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.ingrid.portal.forms.EnvironmentSearchForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
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

public class SearchDetailPortlet extends GenericVelocityPortlet {
    private final static Log log = LogFactory.getLog(SearchDetailPortlet.class);

    private final static String TEMPLATE_DETAIL_GENERIC = "/WEB-INF/templates/search_detail_generic.vm";

    private final static String TEMPLATE_DETAIL_UNIVERSAL = "/WEB-INF/templates/search_detail_universal.vm";

    private final static String TEMPLATE_DETAIL_ECS = "/WEB-INF/templates/search_detail.vm";

    private final static String TEMPLATE_DETAIL_ECS_ADDRESS = "/WEB-INF/templates/search_detail_address.vm";

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
                replacements.put(translation[1], translation[2]);
            }
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
            
            String docUuid = request.getParameter("docuuid");
            String altDocumentId = request.getParameter("altdocid");
            String iplugId = request.getParameter("plugid");
            IngridHit hit = null;
            PlugDescription plugDescription = null;
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            String iPlugVersion = null;
            
            if (iplugId != null && iplugId.length() > 0) {

	            context.put("docUuid", docUuid);
	            context.put("plugId", iplugId);
	            
	            plugDescription = ibus.getIPlug(iplugId);
	            iPlugVersion = IPlugVersionInspector.getIPlugVersion(plugDescription);
	            
	            // try to get the result for a objects UUID
	            if (docUuid != null) {
	                String qStr = null;
	            	if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":" + docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score cache:off";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":" + docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score cache:off";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
	            		qStr = Settings.HIT_KEY_OBJ_ID + ":" + docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score cache:off";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
	            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":" + docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score cache:off";
	                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
	            		qStr = Settings.HIT_KEY_ADDRESS_ADDRID + ":" + docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score cache:off";
	                } else {
	            		qStr = docUuid.trim() + " iplugs:\"" + iplugId.trim() + "\" ranking:score";
	                }
	
	            	IngridQuery q = QueryStringParser.parse(qStr);
	            	IngridHits hits = ibus.search(q, 1, 1, 0, 3000);
	            	if (hits.length() < 1) {
	            		log.error("No record found for document uuid:" + docUuid + " using iplug: " + iplugId);
	            	} else {
	            		hit = hits.getHits()[0];
	            	}
	            } else {
	                int documentId = Integer.parseInt(request.getParameter("docid"));
	                hit = new IngridHit();
	                hit.setDocumentId(documentId);
	                hit.setPlugId(iplugId);
	                context.put("docId", documentId);
	            }
            }

            Record record = null;
            if (hit != null) {
                if (altDocumentId != null) {
                    hit.put("alt_document_id", altDocumentId);
                }
	            record = ibus.getRecord(hit);
            }
            
            if (record == null) {
               	log.error("No record found for document id:" + (hit != null ? hit.getDocumentId() : null) + " using iplug: " + iplugId + " for request: " + ((RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE)).getRequest().getRequestURL() + "?" + ((RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE)).getRequest().getQueryString());
            } else {

                // set language code list
                HashMap sysLangHashs = new HashMap();

                HashMap sysLangHash = new HashMap();
                sysLangHash.put("sys_language.lang_id", "121");
                sysLangHash.put("sys_language.name", "Deutsch");
                sysLangHash.put("sys_language.description", "Deutsch");
                sysLangHash.put("sys_language.def_lang", "1");
                sysLangHashs.put("121", sysLangHash);

                sysLangHash = new HashMap();
                sysLangHash.put("sys_language.lang_id", "94");
                sysLangHash.put("sys_language.name", "English");
                sysLangHash.put("sys_language.description", "English");
                sysLangHash.put("sys_language.def_lang", "1");
                sysLangHashs.put("94", sysLangHash);

                context.put("sysLangList", sysLangHashs);

                // put codelist fetcher into context
                context.put("codeList", new IngridSysCodeList(request.getLocale()));

                DetailDataPreparerFactory ddpf = new DetailDataPreparerFactory(context, iplugId, dateFields, request, response, replacementFields);
                
                if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_ECS);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_ECS_ADDRESS);
                } else if (iPlugVersion.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
                	setDefaultViewPage(TEMPLATE_DETAIL_UNIVERSAL);
                } else {
                	setDefaultViewPage(TEMPLATE_DETAIL_GENERIC);
                }
                DetailDataPreparer detailPreparer = ddpf.getDetailDataPreparer(iPlugVersion);
                detailPreparer.prepare(record);
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
            try {
                IngridHit hit = getAddressHit(addrId, plugId);
                response.setRenderParameter("docuuid", addrId);
                response.setRenderParameter("plugid", hit.getPlugId());
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
            try {
                IngridHit hit = getObjectHit(objId, plugId);
                response.setRenderParameter("docuuid", objId);
                response.setRenderParameter("plugid", hit.getPlugId());
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