/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.mdek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.MdekCaller;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.caller.MdekCallerSecurity;
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
public class MdekPortalAdminPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MdekPortalAdminPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/mdek/mdek_portal_admin.vm";
    private final static String TEMPLATE_NEW = "/WEB-INF/templates/mdek/mdek_portal_admin_create_catalog.vm";

    // Portlet State
    private enum STATE {START, NEW};
    private STATE state;

    // Possible Actions
    private final static String PARAMV_ACTION_DO_DELETE = "doDelete";
    private final static String PARAMV_ACTION_DO_NEW = "doNew";
    private final static String PARAMV_ACTION_DO_RELOAD = "doReload";
    private final static String PARAMV_ACTION_DO_CREATE_CATALOG = "doCreateCatalog";
    private final static String PARAMV_ACTION_DO_CANCEL = "doCancel";
    private enum ACTION {DELETE, NEW, RELOAD, CREATE_CATALOG, CANCEL};

    // Parameters set on init
    private UserManager userManager;
    private RoleManager roleManager;
    private IMdekCaller mdekCaller;

    
    public void init(PortletConfig config) throws PortletException {
    	super.init(config);

    	this.state = STATE.START;

    	this.mdekCaller = MdekCaller.getInstance();
    	
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


    public void doViewStart(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
    throws PortletException, IOException {
		setDefaultViewPage(TEMPLATE_START);
    	
    	Context context = getContext(request);
        context.put("catalogList", buildConnectedCatalogList());
    }


    public void doViewNew(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
    throws PortletException, IOException {
		setDefaultViewPage(TEMPLATE_NEW);

    	Context context = getContext(request);
        List<String> plugIdList = getUnconnectedPlugIdList(request);
        List<String> userNameList = getUnconnectedUserList();
        Collections.sort(plugIdList);
        Collections.sort(userNameList);

        context.put("plugIdList", plugIdList);
        context.put("userNameList", userNameList);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {

    	if (this.state == STATE.START) {
    		doViewStart(request, response);
    	} else {
    		doViewNew(request, response);
    	}

    	super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

    	log.debug("Parameters:");
    	Enumeration<String> e = request.getParameterNames();
    	while (e.hasMoreElements()) {
    		String parameterName = e.nextElement();
    		log.debug(parameterName+": "+request.getParameter(parameterName));
    	}

    	switch (getAction(request)) {
    	case NEW:
    		processActionNew(request, actionResponse);
    		break;
    	
    	case DELETE:
    		processActionDelete(request, actionResponse);
    		break;
    	
    	case RELOAD:
    		processActionReload(request, actionResponse);
    		break;
    	
    	case CREATE_CATALOG:
    		processActionCreateCatalog(request, actionResponse);
    		break;
    	
    	case CANCEL:
    		// Redirect to reload
    		processActionReload(request, actionResponse);
    		break;

    	default:
    		// Redirect to reload
    		processActionReload(request, actionResponse);
    		break;
    	}
    }

    public void processActionNew(ActionRequest request, ActionResponse actionResponse) throws PortletException,
    IOException {
		this.state = STATE.NEW;
    }

    public void processActionDelete(ActionRequest request, ActionResponse actionResponse) throws PortletException,
    IOException {
    	String[] plugIdList = request.getParameterValues("id");

    	if (plugIdList == null) {
    		return;
    	}

    	// Remove all connections from the givenlist
    	for (String plugId : plugIdList) {
    		removeConnectedIPlug(plugId);
    	}
    }

    public void processActionReload(ActionRequest request, ActionResponse actionResponse) throws PortletException,
    IOException {
		this.state = STATE.START;
    }

    public void processActionCreateCatalog(ActionRequest request, ActionResponse actionResponse) throws PortletException,
    IOException {
    	String plugId = request.getParameter("plugId");
    	String userName = request.getParameter("userName");

    	if (plugId == null || userName == null) {
    		return;
    	}

    	// Create a new UserData object
    	IMdekCallerSecurity mdekCallerSecurity = MdekCallerSecurity.getInstance();
    	IngridDocument catAdminDoc = mdekCallerSecurity.getCatalogAdmin(plugId, userName);
    	IngridDocument catAdmin = MdekUtils.getResultFromResponse(catAdminDoc);

    	UserData user = new UserData();
    	user.setAddressUuid((String) catAdmin.get(MdekKeys.UUID));
    	user.setPlugId(plugId);
    	user.setPortalLogin(userName);

    	Session s = HibernateUtil.currentSession();

    	try {    	
        	s.beginTransaction();
        	s.persist(user);
        	roleManager.addRoleToUser(userName, "mdek");
        	s.getTransaction().commit();

    	} catch (SecurityException e) {
    		if (s.getTransaction() != null) {
    			s.getTransaction().rollback();
    		}
    		throw new PortletException(e);

    	} finally {    		
    		HibernateUtil.closeSession();
    	}

    	this.state = STATE.START;
    }

    private void removeConnectedIPlug(String plugId) throws PortletException {
    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();
    	try {
	    	List<UserData> userDataList = (List<UserData>) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).list();
	    	if (userDataList != null && userDataList.size() != 0) {
	    		// TODO Delete all idc users in the catalog?
	    		for(UserData userData : userDataList) {
		    		s.delete(userData);
		    		roleManager.removeRoleFromUser(userData.getPortalLogin(), "mdek");
	    		}
	        	s.getTransaction().commit();
	    	}

    	} catch (SecurityException e) {
    		if (s.getTransaction() != null) {
    			s.getTransaction().rollback();
    		}
    		throw new PortletException(e);

    	} finally {    		
    		HibernateUtil.closeSession();
    	}
    }

    private static ACTION getAction(ActionRequest request) {
    	if (request.getParameter(PARAMV_ACTION_DO_NEW) != null)
    		return ACTION.NEW;
    	else if (request.getParameter(PARAMV_ACTION_DO_DELETE) != null)
    		return ACTION.DELETE;
    	else if (request.getParameter(PARAMV_ACTION_DO_RELOAD) != null)
    		return ACTION.RELOAD;
    	else if (request.getParameter(PARAMV_ACTION_DO_CREATE_CATALOG) != null)
    		return ACTION.CREATE_CATALOG;
    	else if (request.getParameter(PARAMV_ACTION_DO_CANCEL) != null)
    		return ACTION.CANCEL;
    	else
    		return ACTION.RELOAD;
    }

    private List<String> getUnconnectedUserList() throws PortletException {
        try {
        	Iterator<String> users = userManager.getUserNames("");
            List<String> userNameList = new ArrayList<String>();

            while (users.hasNext()) {
                String user = (String) users.next();
                if (canBecomeCatalogAdmin(user)) {
                	log.debug("User '"+user+"' can become catAdmin.");
                	userNameList.add(user);
                } else {
                	log.debug("User '"+user+"' can't become catAdmin.");
                }
            }
            return userNameList;

        } catch (SecurityException se) {
        	throw new PortletException(se);
        }
    }

    private List<String> getUnconnectedPlugIdList(javax.portlet.RenderRequest request) throws PortletException {
    	List<String> plugIdList = new ArrayList<String>();
    
    	for (String plugId : this.mdekCaller.getRegisteredIPlugs()) {
    		if (!hasCatalogAdmin(request, plugId)) {
            	log.debug("Catalog '"+plugId+"' does not have a catAdmin.");
    			plugIdList.add(plugId);

    		} else {
            	log.debug("Catalog '"+plugId+"' has a catAdmin.");
    		}
    	}
    	return plugIdList;
    }

    private List<Map<String, String>> buildConnectedCatalogList() {
    	List<Map<String, String>> catalogList = new ArrayList<Map<String,String>>();
    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();

    	for (String plugId : this.mdekCaller.getRegisteredIPlugs()) {
        	List<UserData> userDataList = (List<UserData>) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).list();
        	if (userDataList != null && userDataList.size() != 0) {
        		HashMap<String, String> catalogData = new HashMap<String, String>();
        		UserData userData = userDataList.get(0);
        		
        		IMdekCallerCatalog mdekCallerCatalog = MdekCallerCatalog.getInstance();
        		IMdekCallerSecurity mdekCallerSecurity = MdekCallerSecurity.getInstance();

        		IngridDocument cat = mdekCallerCatalog.fetchCatalog(plugId, userData.getAddressUuid());
        		IngridDocument adm = mdekCallerSecurity.getCatalogAdmin(plugId, userData.getAddressUuid());
        		String catAdminUuid = extractCatalogAdminUuid(adm);
        		
        		CatalogBean catBean = MdekCatalogUtils.extractCatalogFromResponse(cat);


        		UserData catAdminUserData = (UserData) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).add(Restrictions.eq("addressUuid", catAdminUuid)).uniqueResult();
        		
        		
        		catalogData.put("plugId", plugId);
        		catalogData.put("catName", catBean.getCatalogName());
        		catalogData.put("catAdmin", extractCatalogAdminName(adm));
        		catalogData.put("portalLogin", catAdminUserData.getPortalLogin());
        		catalogData.put("partner", catBean.getPartnerName());
        		catalogData.put("provider", catBean.getProviderName());
        		catalogList.add(catalogData);
        	}
    	}
    	
    	s.getTransaction().commit();
    	HibernateUtil.closeSession();
    	return catalogList;
    }

    private static String extractCatalogAdminUuid(IngridDocument catAdmin) {
    	IngridDocument result = MdekUtils.getResultFromResponse(catAdmin);
    	return (String) result.get(MdekKeys.UUID);
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

    private boolean hasCatalogAdmin(javax.portlet.RenderRequest request, String plugId) throws PortletException {
    	IMdekCallerSecurity mdekCallerSecurity = MdekCallerSecurity.getInstance();
		IngridDocument response = mdekCallerSecurity.getCatalogAdmin(plugId, request.getUserPrincipal().getName());
    	IngridDocument catAdmin = MdekUtils.getResultFromResponse(response);
    	String addressUuid = (String) catAdmin.get(MdekKeys.UUID);

    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();
    	List<UserData> userDataList = (List<UserData>) s.createCriteria(UserData.class).add(Restrictions.eq("plugId", plugId)).add(Restrictions.eq("addressUuid", addressUuid)).list();

    	s.getTransaction().commit();
    	HibernateUtil.closeSession();

    	if (userDataList != null && userDataList.size() != 0) {
    		return true;
    	}

    	return false;
    }


    private boolean canBecomeCatalogAdmin(String userName) throws SecurityException {
    	return (!userName.equals("admin") && !userName.equals("guest") && !userName.equals("devmgr")
       	     && !roleManager.isUserInRole(userName, "admin")
    	     && !roleManager.isUserInRole(userName, "admin-portal")
    		 && !roleManager.isUserInRole(userName, "mdek"));
    }
}