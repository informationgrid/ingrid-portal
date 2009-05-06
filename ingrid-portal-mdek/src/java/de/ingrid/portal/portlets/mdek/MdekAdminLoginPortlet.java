/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.mdek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.caller.MdekCallerSecurity;
import de.ingrid.mdek.caller.MdekClientCaller;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.utils.IngridDocument;


/**
 * This portlet handles the administration processes for the portal admin
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
    private enum ACTION {ADMIN_LOGIN, IGE_LOGIN, RELOAD};

    
    private final static String CATALOG			= "CATALOG";
    private final static String USER_OF_CATALOG = "USER_OF_CATALOG";
    
    
    // Parameters set on init
    private UserManager userManager;
    private RoleManager roleManager;
    private IMdekClientCaller mdekClientCaller;

    
    public void init(PortletConfig config) throws PortletException {
    	super.init(config);

    	this.mdekClientCaller = MdekClientCaller.getInstance();
    	
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }

		try {
			if (!roleManager.roleExists("mdek")) {
				roleManager.addRole("mdek");
			}
		} catch (SecurityException e) {
			throw new PortletException(e);
		}
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
    	Context context = getContext(request);
    	
    	ResourceBundle resourceBundle = getPortletConfig().getResourceBundle(request.getLocale());
        context.put("MESSAGES", resourceBundle);
        
    	setDefaultViewPage(TEMPLATE_START);
    	
    	
        context.put("catalogList", buildConnectedCatalogList().get(CATALOG));
        context.put("userLists", buildConnectedCatalogList().get(USER_OF_CATALOG));
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
    		// Redirect to reload
    		//processActionReload(request, actionResponse);
    		break;
    	}
    }    

    private void processActionAdminLogin(ActionRequest request, ActionResponse actionResponse) {
    	try {
    		getPortletContext().setAttribute("ige.force.userName", request.getParameter("user"));
			actionResponse.sendRedirect("/ingrid-portal-mdek-application/mdek_admin_entry.jsp?debug=true");
		} catch (IOException e) {
			log.error("Could not redirect to page: /ingrid-portal-mdek-application/mdek_admin_entry.jsp?debug=true");
			e.printStackTrace();
		}
		
	}
    
    private void processActionIgeLogin(ActionRequest request, ActionResponse actionResponse) {
    	try {
    		getPortletContext().setAttribute("ige.force.userName", request.getParameter("user"));
			actionResponse.sendRedirect("/ingrid-portal-mdek-application/mdek_entry.jsp?debug=true");
		} catch (IOException e) {
			log.error("Could not redirect to page: /ingrid-portal-mdek-application/mdek_entry.jsp?debug=true");
			e.printStackTrace();
		}
		
	}

	private static ACTION getAction(ActionRequest request) {
    	if (request.getParameter(PARAMV_ACTION_DO_LOGIN_ADMIN) != null)
    		return ACTION.ADMIN_LOGIN;
    	else if (request.getParameter(PARAMV_ACTION_DO_LOGIN_IGE) != null)
    		return ACTION.IGE_LOGIN;
    	else
    		return ACTION.RELOAD;
    }
    

    private Map<String, List> buildConnectedCatalogList() {
    	Map<String,List> dataContainer = new HashMap<String,List>();
    	List<Map<String, String>> catalogList = new ArrayList<Map<String,String>>();
    	List<List> userLists = new ArrayList<List>();
    	
    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();

    	for (String plugId : this.mdekClientCaller.getRegisteredIPlugs()) {
        	List<UserData> userDataList = (List<UserData>) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).list();
        	List<String> userList = new ArrayList<String>();
        	
        	for (UserData userData : userDataList) {
				userList.add('"' + userData.getPortalLogin() + '"');
			}
        	userLists.add(userList);        	
        	
        	if (userDataList != null && userDataList.size() != 0) {
        		HashMap<String, String> catalogData = new HashMap<String, String>();
        		
        		
        		
        		UserData userData = userDataList.get(0);
        		
        		IMdekCallerCatalog mdekCallerCatalog = MdekCallerCatalog.getInstance();
        		IMdekCallerSecurity mdekCallerSecurity = MdekCallerSecurity.getInstance();

        		IngridDocument cat = mdekCallerCatalog.fetchCatalog(plugId, userData.getAddressUuid());
        		IngridDocument adm = mdekCallerSecurity.getCatalogAdmin(plugId, userData.getAddressUuid());
        		String catAdminUuid = extractCatalogAdminUuid(adm);

        		CatalogBean catBean = null;
        		try {
        			catBean = MdekCatalogUtils.extractCatalogFromResponse(cat);
        		} catch (Exception e) {
        			continue;
        		}

        		UserData catAdminUserData = (UserData) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).add(Restrictions.eq("addressUuid", catAdminUuid)).uniqueResult();

        		if (catAdminUserData == null) {
        			// The catalog admin was not found in the user table. This should never be the case!
        			// Possibly the addressUuid has changed. Display the catalog, but also display an error
            		catalogData.put("plugId", plugId);
            		catalogData.put("catName", catBean.getCatalogName());
            		catalogData.put("catAdmin", extractCatalogAdminName(adm));
            		catalogData.put("portalLogin", "ERROR: portalLogin not found!");
            		catalogData.put("partner", catBean.getPartnerName());
            		catalogData.put("provider", catBean.getProviderName());
            		catalogList.add(catalogData);

        		} else {
        			// Display the catalogData
	        		catalogData.put("plugId", plugId);
	        		catalogData.put("catName", catBean.getCatalogName());
	        		catalogData.put("catAdmin", extractCatalogAdminName(adm));
	        		catalogData.put("portalLogin", catAdminUserData.getPortalLogin());
	        		catalogData.put("partner", catBean.getPartnerName());
	        		catalogData.put("provider", catBean.getProviderName());
	        		catalogList.add(catalogData);
        		}
        	}
    	}
    	
    	s.getTransaction().commit();
    	HibernateUtil.closeSession();
    	
    	dataContainer.put(USER_OF_CATALOG, userLists);
    	dataContainer.put(CATALOG, catalogList);
    	return dataContainer;
    }

    private static String extractCatalogAdminUuid(IngridDocument catAdmin) {
    	IngridDocument result = MdekUtils.getResultFromResponse(catAdmin);
    	return result == null ? "" : (String) result.get(MdekKeys.UUID);
    }


    private static String extractCatalogAdminName(IngridDocument catAdmin) {
    	IngridDocument result = MdekUtils.getResultFromResponse(catAdmin);
    	String title = "";
    	String organisation = (String) result.get(MdekKeys.ORGANISATION);
		String name = (String) result.get(MdekKeys.NAME);
		String givenName = (String) result.get(MdekKeys.GIVEN_NAME);

    	switch((Integer) result.get(MdekKeys.CLASS)) {
    	case 0:
    		// Fall through
    	case 1:
    		title = organisation;
    		break;
    	case 2:
    		if (name != null)
    			title += name;
    		if (givenName != null)
    			title += ", "+givenName;
    		break;
    	case 3:
    		if (name != null)
    			title += name;
    		if (givenName != null)
    			title += ", "+givenName;
    		if (organisation != null)
    			title += " ("+organisation+")";
    		break;
    	}
    	return title;
    }

}