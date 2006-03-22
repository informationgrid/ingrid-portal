package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.ContactForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

public class ContactPortlet extends GenericVelocityPortlet {
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity macros work !
        ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
        context.put("actionForm", cf);

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        // clear form if render request not after action request (e.g. page entered from other page)
        String actionCall = request.getParameter(Settings.PARAM_IS_ACTION);
        if (actionCall == null) {
            cf.clear();
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // check form input
        ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
        cf.populate(request);
        if (!cf.validate()) {
            // add URL parameter indicating that portlet action was called before render request
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_IS_ACTION, Settings.MSGV_TRUE);
            actionResponse.sendRedirect(Settings.PAGE_CONTACT + urlViewParam);

            return;
        }

        // TODO: implement functionality
        // temporarily show same page with content
        String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_IS_ACTION, Settings.MSGV_TRUE);
        actionResponse.sendRedirect(Settings.PAGE_CONTACT + urlViewParam);
    }
}