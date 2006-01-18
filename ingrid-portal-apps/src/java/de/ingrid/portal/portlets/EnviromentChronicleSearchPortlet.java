package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.ChronicleSearchForm;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.utils.Utils;

public class EnviromentChronicleSearchPortlet extends GenericVelocityPortlet {
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        ChronicleSearchForm cf = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY, ChronicleSearchForm.class);
        context.put("actionForm", cf);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        // check form input
        ChronicleSearchForm cf = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY, ChronicleSearchForm.class);
        cf.populate(request);
        if (!cf.validate()) {
            return;
        }

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("enviroment_chronicle_search_portlet_page_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("enviroment_chronicle_search_portlet_page_state", ps);
        }

        if (action == null) {
            return;
        }
    }

    private PageState initPageState(PageState ps) {
        return ps;
    }

}