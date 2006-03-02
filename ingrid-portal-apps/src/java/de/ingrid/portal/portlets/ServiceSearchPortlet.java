package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.EnvironmentSearchForm;
import de.ingrid.portal.forms.ServiceSearchForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

public class ServiceSearchPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(ServiceSearchPortlet.class);

    /** Keys of parameters in session/request */
    private final static String PARAM_TEASER_CALL = "teaser";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        ServiceSearchForm sf = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class);
        // when called from teaser take over search criteria and initiate query
        String teaserCall = request.getParameter(PARAM_TEASER_CALL);
        if (teaserCall != null) {
            sf.init();
            // populate doesn't clear !!!
            sf.populate(request);
            if (!sf.validate()) {
                return;
            }
            setupQuery(request);
        }
        context.put("actionForm", sf);

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
        publishRenderMessage(request, Settings.MSG_QUERY_STATE, Settings.MSGV_NEW_QUERY);

        // check form input
        ServiceSearchForm sf = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class);
        // populate doesn't clear !!!
        sf.clearInput();
        sf.populate(request);
        if (!sf.validate()) {
            return;
        }

        setupQuery(request);
    }

    public void setupQuery(PortletRequest request) {
        // remove old query message for result portlet
        cancelRenderMessage(request, Settings.MSG_QUERY);
        // also set a message that a new query was performed, so former render parameters are ignored
        publishRenderMessage(request, Settings.MSG_QUERY_STATE, Settings.MSGV_NEW_QUERY);

        String FORM_VALUE_ALL = "all";

        IngridQuery query = null;
        try {
            query = new IngridQuery();
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SERVICE));
            /*
             // RUBRIC
             String[] rubrics = request.getParameterValues(ServiceSearchForm.FIELD_RUBRIC);
             // don't set anything if "all" is selected
             if (rubrics != null && Utils.getPosInArray(rubrics, FORM_VALUE_ALL) == -1) {
             for (int i = 0; i < rubrics.length; i++) {
             if (rubrics[i] != null) {
             query.addField(new FieldQuery(IngridQuery.AND, Settings.QFIELD_TOPIC, rubrics[i]));
             // TODO at the moment we only use first selection value, backend can't handle multiple OR yet
             break;
             }
             }
             }
             */
            // PARTNER
            String[] partners = request.getParameterValues(ServiceSearchForm.FIELD_PARTNER);
            // don't set anything if "all" is selected
            if (partners != null && Utils.getPosInArray(partners, FORM_VALUE_ALL) == -1) {
                for (int i = 0; i < partners.length; i++) {
                    if (partners[i] != null) {
                        query.addField(new FieldQuery(true, false, Settings.QFIELD_PARTNER, partners[i]));
                        // TODO at the moment we only use first selection value, backend can't handle multiple OR yet
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems setting up Query !", t);
            }
        }
        // set query message for result portlet
        publishRenderMessage(request, Settings.MSG_QUERY, query);
    }
}