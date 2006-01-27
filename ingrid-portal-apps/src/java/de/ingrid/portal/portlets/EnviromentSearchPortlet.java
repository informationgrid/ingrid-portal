package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.EnvironmentSearchForm;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.search.PageState;

public class EnviromentSearchPortlet extends GenericVelocityPortlet {

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        EnvironmentSearchForm af = (EnvironmentSearchForm) Utils.getActionForm(request,
                EnvironmentSearchForm.SESSION_KEY, EnvironmentSearchForm.class);
        context.put("actionForm", af);

        // get data base stuff
        List partners = UtilsDB.getPartners();
        context.put("partnerList", partners);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        // check form input
        EnvironmentSearchForm af = (EnvironmentSearchForm) Utils.getActionForm(request,
                EnvironmentSearchForm.SESSION_KEY, EnvironmentSearchForm.class);
        af.populate(request);
        if (!af.validate()) {
            return;
        }

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("enviroment_search_portlet_page_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("enviroment_search_portlet_page_state", ps);
        }

        if (action == null) {
            return;
        }
    }

    private PageState initPageState(PageState ps) {
        return ps;
    }

}