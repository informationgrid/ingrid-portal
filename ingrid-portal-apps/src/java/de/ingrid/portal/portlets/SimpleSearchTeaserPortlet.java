package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.SimpleSearchForm;
import de.ingrid.portal.utils.Utils;

/**
 * TODO Please add comments!
 * 
 * created 11.01.2006
 * 
 * @author joachim
 * @version
 * 
 */
public class SimpleSearchTeaserPortlet extends GenericVelocityPortlet {
    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        ResourceBundle messages = getPortletConfig().getResourceBundle(request.getLocale());
        context.put("MESSAGES", messages);

        // get ActionForm, we use get method without instantiation, so we can do
        // special initialisation
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SimpleSearchForm) Utils.addActionForm(request, SimpleSearchForm.SESSION_KEY, SimpleSearchForm.class,
                    PortletSession.APPLICATION_SCOPE);
            af.setINITIAL_QUERY(messages.getString("simpleSearch.query.initial"));
            af.init();
        }
        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        context.put("actionForm", af);

        // String action = request.getParameter("action");
        // if (action != null && action.equalsIgnoreCase("doSearch")) {
        // // we are on the result psml page !!!
        // af.populate(request);
        // }

        PortletSession session = request.getPortletSession();
        String selectedDS = (String) session.getAttribute("selectedDS");
        if (selectedDS == null || selectedDS.length() == 0) {
            selectedDS = "1";
        }
        context.put("ds", selectedDS);

        super.doView(request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        // check form input
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        af.populate(request);
        if (!af.validate()) {
            return;
        }

        PortletSession session = request.getPortletSession();
        if (action == null) {
            return;
        } else if (action.equalsIgnoreCase("doChangeDS")) {
            session.setAttribute("selectedDS", request.getParameter("ds"), PortletSession.APPLICATION_SCOPE);
        }

    }

}
