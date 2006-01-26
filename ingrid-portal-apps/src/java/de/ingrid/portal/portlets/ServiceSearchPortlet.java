package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.ServiceSearchForm;
import de.ingrid.portal.resources.PortletApplicationResources;
import de.ingrid.portal.utils.Utils;
import de.ingrid.portal.utils.UtilsDB;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class ServiceSearchPortlet extends AbstractVelocityMessagingPortlet {

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(PortletApplicationResources.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        ServiceSearchForm sf = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class);
        context.put("actionForm", sf);

        // get data base stuff
        List partners = UtilsDB.getPartners();
        context.put("partnerList", partners);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // check form input
        ServiceSearchForm sf = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class);
        sf.populate(request);
        if (!sf.validate()) {
            cancelRenderMessage(request, PortletApplicationResources.MSG_QUERY);
            return;
        }

        // TODO Create IngridQuery from form input !
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse("to do");
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        publishRenderMessage(request, PortletApplicationResources.MSG_QUERY, query);
    }
}