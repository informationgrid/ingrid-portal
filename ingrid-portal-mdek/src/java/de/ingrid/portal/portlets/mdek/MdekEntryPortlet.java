/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.mdek;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.MdekCaller;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.portal.hibernate.HibernateUtil;


/**
 * This portlet handles the entry to the mdek application
 *
 * @author michael.benz@wemove.com
 */
public class MdekEntryPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MdekEntryPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/mdek/mdek_entry.vm";
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
    	 
    	 PortletSession sess = request.getPortletSession();
    	log.debug("PortletSession ID:"+sess.getId());
    	getPortletContext().setAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT, getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT));
    	getPortletContext().setAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT, getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT));
    	getPortletContext().setAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER, getPortletContext().getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER));

    	IMdekCaller mdekCaller = MdekCaller.getInstance();
    	List<String> strList = mdekCaller.getRegisteredIPlugs();

    	Session s = HibernateUtil.currentSession();
    	s.beginTransaction();
    	List<UserData> userDataList = (List<UserData>) s.createCriteria(UserData.class).list();
    	s.getTransaction().commit();
    	HibernateUtil.closeSession();

    	if (userDataList != null) {
    		for (UserData userData : userDataList) {
    			log.debug("User:");
    			log.debug(userData.getPortalLogin());
    			log.debug(userData.getAddressUuid());
    			log.debug(userData.getPlugId());
    		}
    	}
/*
    	sess.setAttribute("PSCOPE", "Portlet Scoped var", PortletSession.PORTLET_SCOPE);
    	sess.setAttribute("ASCOPE", "App Scoped var", PortletSession.APPLICATION_SCOPE);
    	getPortletContext().setAttribute(sess.getId()+"REMOTE_USER", request.getRemoteUser());
    	getPortletContext().setAttribute(sess.getId()+"USER_PRINCIPAL", request.getUserPrincipal());
*/
//    	PortletRequestDispatcher dis = getPortletContext().getRequestDispatcher("/ingrid-portal-mdek-application/dump_session.jsp");
//    	dis.include(request, response);
//    	return;

    	setDefaultViewPage(TEMPLATE_START);
        super.doView(request, response);
	}

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

    }
}