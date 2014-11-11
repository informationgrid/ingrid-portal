/*
 * **************************************************-
 * Ingrid Portal Mdek
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.mdek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.caller.MdekClientCaller;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.portlets.mdek.utils.MdekPortletUtils;
import de.ingrid.utils.IngridDocument;


/**
 * This portlet allows the portal admin to login as an arbitrary mdek user to
 * the IGE without the need of a password.
 *
 * @author michael.benz@wemove.com
 */
public class MdekAdminLoginPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MdekAdminLoginPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/mdek/mdek_admin_login.vm";

    // Possible Actions
    private final static String PARAMV_ACTION_DO_LOGIN_ADMIN 	= "doLoginAdmin";
    private final static String PARAMV_ACTION_DO_LOGIN_IGE 		= "doLoginIGE";
    private enum ACTION {ADMIN_LOGIN, IGE_LOGIN, UNKNOWN};

    
    private final static String CATALOG			= "CATALOG";
    private final static String USER_OF_CATALOG = "USER_OF_CATALOG";
    
    
    // Parameters set on init
    private IMdekClientCaller mdekClientCaller;
    private IMdekCallerCatalog mdekCallerCatalog;

    
    public void init(PortletConfig config) throws PortletException {
    	super.init(config);

    	this.mdekClientCaller = MdekClientCaller.getInstance();
		this.mdekCallerCatalog = MdekCallerCatalog.getInstance();
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
    	Context context = getContext(request);
    	
    	ResourceBundle resourceBundle = getPortletConfig().getResourceBundle(request.getLocale());
        context.put("MESSAGES", resourceBundle);
        
    	setDefaultViewPage(TEMPLATE_START);
    	
    	PortletPreferences prefs = request.getPreferences();
    	String myTitleKey = prefs.getValue("titleKey", "mdek.title.adminlogin");
    	response.setTitle(resourceBundle.getString(myTitleKey));

		String error = (String) getPortletContext().getAttribute("ige.error");
		if (error != null) {
	        context.put("igeError", error);
    		// clear error !
    		getPortletContext().removeAttribute("ige.error");
		}

    	Map<String, List> catalogInfo = buildConnectedCatalogList();
        context.put("catalogList", catalogInfo.get(CATALOG));
        context.put("userLists", catalogInfo.get(USER_OF_CATALOG));
    	super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

    	switch (getAction(request)) {
    	case ADMIN_LOGIN:
    		processActionAdminLogin(request, actionResponse);
    		break;
    	case IGE_LOGIN:
    		processActionIgeLogin(request, actionResponse);
    		break;
    	default:
    		log.warn("UNKNOWN ACTION in MdekAdminLoginPortlet");
    		break;
    	}
    }    

    /**
     * Login to the administration page with a chosen mdek user
     * @param request
     * @param actionResponse
     */
    private void processActionAdminLogin(ActionRequest request, ActionResponse actionResponse) {
    	try {
    		String plugId = request.getParameter("catalog");

    		// first check compatibility
    		try {
        		MdekPortletUtils.checkIGCCompatibility(plugId, mdekCallerCatalog);    			
    		} catch (Throwable e) {
    			log.error("Problems login IGE catalog-administration", e);
        		getPortletContext().setAttribute("ige.error", e.getMessage());
                return;
    		}
    		
    		log.info("Portal-Administrator logs into catalog-administration of catalog " + plugId +
    			"as user: " + request.getParameter("user"));
    		// put the user name of the mdek-user into the context
    		// this name will be retrieved from mdek-application later
    		// (this had to be done this way, since sessions are not application wide!)
    		getPortletContext().setAttribute("ige.force.userName", request.getParameter("user"));
			actionResponse.sendRedirect("/ingrid-portal-mdek-application/start.jsp?debug=true&lang="+ request.getLocale().getLanguage());
		} catch (IOException e) {
			log.error("Could not redirect to page: /ingrid-portal-mdek-application/start.jsp?debug=true", e);
		}
	}
    
    /**
     * Login to the IngridEditor page with a chosen mdek user
     * @param request
     * @param actionResponse
     */
    private void processActionIgeLogin(ActionRequest request, ActionResponse actionResponse) {
    	try {
    		String plugId = request.getParameter("catalog");

    		// first check compatibility
    		try {
        		MdekPortletUtils.checkIGCCompatibility(plugId, mdekCallerCatalog);    			
    		} catch (Throwable e) {
    			log.error("Problems login IGE", e);
        		getPortletContext().setAttribute("ige.error", e.getMessage());
                return;
    		}

    		log.info("Portal-Administrator logs into IGE of catalog " + plugId +
    			" as user: " + request.getParameter("user"));
    		// put the user name of the mdek-user into the context
    		// this name will be retrieved from mdek-application later
    		// (this had to be done this way, since sessions are not application wide!)
    		getPortletContext().setAttribute("ige.force.userName", request.getParameter("user"));
			actionResponse.sendRedirect("/ingrid-portal-mdek-application/start.jsp?debug=true&lang=" + request.getLocale().getLanguage());
		} catch (IOException e) {
			log.error("Could not redirect to page: /ingrid-portal-mdek-application/start.jsp?debug=true", e);
		}
	}

	private static ACTION getAction(ActionRequest request) {
    	if (request.getParameter(PARAMV_ACTION_DO_LOGIN_ADMIN) != null)
    		return ACTION.ADMIN_LOGIN;
    	else if (request.getParameter(PARAMV_ACTION_DO_LOGIN_IGE) != null)
    		return ACTION.IGE_LOGIN;
    	else
    		return ACTION.UNKNOWN;
    }
    

	/**
	 * This function looks for all connected catalogs and its' users and returns them.
	 * 
	 * @return a hashmap with
	 *   CATALOG: contains information of found catalogs
	 *   USER_OF_CATALOG: contains all users found in each catalog; it's a list of a list
	 *       and has the same order as the catalogs, which means that the first list contains
	 *       the users for the first catalog, the second list the users for the second catalog, ...
	 */
    private Map<String, List> buildConnectedCatalogList() {
    	Map<String,List> dataContainer = new HashMap<String,List>();
    	List<Map<String, String>> catalogList = new ArrayList<Map<String,String>>();
    	List<List> userLists = new ArrayList<List>();
    	
    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();

    	// query all connected iPlugs/catalogs
    	for (String plugId : this.mdekClientCaller.getRegisteredIPlugs()) {
        	List<UserData> userDataList = (List<UserData>) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).list();
        	List<String> userList = new ArrayList<String>();
        	
        	// get users that belong to the iPlug (are mdek user)
        	for (UserData userData : userDataList) {
				userList.add('"' + userData.getPortalLogin() + '"');
			}
        	
        	if (log.isDebugEnabled()) {
        		log.debug("Total users found: " + userList.size() + " ("+plugId+")");
        	}
        	
        	// sort users
        	Collections.sort(userList, String.CASE_INSENSITIVE_ORDER);
        	userLists.add(userList);        	
        	
        	if (userDataList != null && userDataList.size() != 0) {
        		HashMap<String, String> catalogData = new HashMap<String, String>();
        		
        		UserData userData = userDataList.get(0);
        		
        		IMdekCallerCatalog mdekCallerCatalog = MdekCallerCatalog.getInstance();

        		IngridDocument cat = mdekCallerCatalog.fetchCatalog(plugId, userData.getAddressUuid());

        		String catName = "";
        		try {
        			CatalogBean catBean = MdekCatalogUtils.extractCatalogFromResponse(cat);
        			catName = catBean.getCatalogName();
        		} catch (Exception e) {
            		log.error("Problems extracting catalog data for iPlug " + plugId, e);
            		catName = "ERROR, see log !";
        		}

    			// Display the catalogData
        		catalogData.put("plugId", plugId);
        		catalogData.put("catName", catName);
        		catalogList.add(catalogData);
        	}
    	}
    	
    	s.getTransaction().commit();
    	HibernateUtil.closeSession();
    	
    	// put all necessary data in another hashmap
    	dataContainer.put(USER_OF_CATALOG, userLists);
    	dataContainer.put(CATALOG, catalogList);
    	return dataContainer;
    }
}