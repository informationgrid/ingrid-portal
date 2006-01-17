package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.ServiceForm;
import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.utils.Utils;

public class ServiceSearchPortlet extends GenericVelocityPortlet {

    HibernateManager fHibernateManager = null;

    
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        fHibernateManager = HibernateManager.getInstance();
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        ServiceForm sf = (ServiceForm) Utils.getActionForm(request, ServiceForm.SESSION_KEY, ServiceForm.class);
        // use variable name "actionForm" so velocity macros work !
        context.put("actionForm", sf);
        
        List relations = this.fHibernateManager.loadAllData(IngridPartner.class, 0);
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        // check form input
        ServiceForm sf = (ServiceForm) Utils.getActionForm(request, ServiceForm.SESSION_KEY, ServiceForm.class);
        sf.populate(request);
        if (!sf.validate()) {
            return;
        }

        // PageState ps = getPageState(request);

        if (action == null) {
            return;
        }
    }

    /*
     * private PageState getPageState(PortletRequest request) { PortletSession
     * session = request.getPortletSession(); PageState ps = (PageState)
     * session.getAttribute("service_search_portlet_page_state"); if (ps ==
     * null) { ps = new PageState(this.getClass().getName()); ps =
     * initPageState(ps);
     * session.setAttribute("service_search_portlet_page_state", ps); }
     * 
     * return ps; }
     * 
     * private PageState initPageState(PageState ps) { return ps; }
     */
}