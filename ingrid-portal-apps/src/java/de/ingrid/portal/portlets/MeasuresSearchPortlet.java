package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.MeasuresSearchForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class MeasuresSearchPortlet extends AbstractVelocityMessagingPortlet {

    /** Keys of parameters in session/request */
    private final static String PARAM_TEASER_CALL = "teaser";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_MEASURES);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        MeasuresSearchForm af = (MeasuresSearchForm) Utils.getActionForm(request, MeasuresSearchForm.SESSION_KEY,
                MeasuresSearchForm.class);
        // when called from teaser take over search criteria and initiate query
        String teaserCall = request.getParameter(PARAM_TEASER_CALL);
        if (teaserCall != null) {
            af.init();
            // populate doesn't clear !!!
            af.populate(request);
            doQuery(af, request);
        }
        context.put("actionForm", af);

        // get data base stuff
        List partners = UtilsDB.getPartners();
        context.put("partnerList", partners);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // remove old query message for result portlet
        cancelRenderMessage(request, Settings.MSG_QUERY);
        // also set a message that a new query was performed, so former render parameters are ignored
        publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);

        // check form input
        MeasuresSearchForm af = (MeasuresSearchForm) Utils.getActionForm(request, MeasuresSearchForm.SESSION_KEY,
                MeasuresSearchForm.class);
        // populate doesn't clear !!!
        af.clearInput();
        af.populate(request);
        if (!af.validate()) {
            return;
        }

        doQuery(af, request);
    }

    public void doQuery(MeasuresSearchForm sf, PortletRequest request) {
        // TODO Create IngridQuery from form input !
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse("to do");
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // set query message for result portlet
        publishRenderMessage(request, Settings.MSG_QUERY, query);
    }
}