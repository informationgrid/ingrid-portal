package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.ServiceForm;
import de.ingrid.portal.search.PageState;

public class ServiceSearchPortlet extends GenericVelocityPortlet {
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        ServiceForm sf = getActionForm(request);
        context.put("serviceForm", sf);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        // take over form parameter in action form
        ServiceForm sf = getActionForm(request);
        sf.populate(request);

        // PageState ps = getPageState(request);

        if (action == null) {
            return;
        }
    }

    private ServiceForm getActionForm(PortletRequest request) {
        PortletSession session = request.getPortletSession();
        ServiceForm sf = (ServiceForm) session.getAttribute(ServiceForm.SESS_ATTRIB);
        if (sf == null) {
            sf = new ServiceForm();
            sf.init();
            session.setAttribute(ServiceForm.SESS_ATTRIB, sf);
        }

        return sf;
    }

    private PageState getPageState(PortletRequest request) {
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("service_search_portlet_page_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("service_search_portlet_page_state", ps);
        }

        return ps;
    }

    private PageState initPageState(PageState ps) {
        return ps;
    }

}