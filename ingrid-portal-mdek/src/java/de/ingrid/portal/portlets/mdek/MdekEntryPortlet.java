/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.mdek;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.MdekCallerSecurity;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.utils.IngridDocument;


/**
 * This portlet handles the entry to the mdek application
 *
 * @author michael.benz@wemove.com
 */
public class MdekEntryPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MdekEntryPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/mdek/mdek_entry.vm";

    // Parameters set on init
    private UserManager userManager;
    private RoleManager roleManager;
	IMdekCallerSecurity mdekCallerSecurity;

    public void init(PortletConfig config) throws PortletException {
    	super.init(config);

    	mdekCallerSecurity = MdekCallerSecurity.getInstance();

        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }

    	// Add the user and role manager to the context
    	// This has to be done so we can access the jetspeed managers in the mdek app
        getPortletContext().setAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT, userManager);
    	getPortletContext().setAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT, roleManager);
    }


    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
    	Context context = getContext(request);
    	
    	String userName = request.getRemoteUser();
    	try {
    		if (hasUserAccessToMdekAdmin(userName)) {
    			context.put("showMdekAdmin", true);    		
    		} else {
    			context.put("showMdekAdmin", false);
    		}
    	} catch (SecurityException e) {
    		log.debug(e);
    	}

    	setDefaultViewPage(TEMPLATE_START);
        super.doView(request, response);
	}

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }
    
    private boolean hasUserAccessToMdekAdmin(String userName) throws SecurityException {
    		if (!roleManager.isUserInRole(userName, "mdek")) {
    		return false;
    	}

    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();
    	UserData userData = (UserData) s.createCriteria(UserData.class).add(Restrictions.eq("portalLogin", userName)).uniqueResult();
    	s.getTransaction().commit();
    	HibernateUtil.closeSession();

    	// Check for the idcRole of the user
    	IngridDocument response = mdekCallerSecurity.getUserDetails(userData.getPlugId(), userData.getAddressUuid(), userData.getAddressUuid());
		IngridDocument userDoc = MdekUtils.getResultFromResponse(response);
		Integer role = (Integer) userDoc.get(MdekKeysSecurity.IDC_ROLE);

		return (role == 1 || role == 2);
    }
}