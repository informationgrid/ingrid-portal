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
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

public class EnvironmentSearchPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(EnvironmentSearchPortlet.class);

    /** Keys of parameters in session/request */
    private final static String PARAM_TEASER_CALL = "teaser";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_ENVIRONMENT);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        EnvironmentSearchForm af = (EnvironmentSearchForm) Utils.getActionForm(request,
                EnvironmentSearchForm.SESSION_KEY, EnvironmentSearchForm.class);
        // when called from teaser take over search criteria and initiate query
        String teaserCall = request.getParameter(PARAM_TEASER_CALL);
        if (teaserCall != null) {
            af.init();
            // populate doesn't clear !!!
            af.populate(request);
            if (!af.validate()) {
                return;
            }
            setupQuery(af, request);
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
        EnvironmentSearchForm af = (EnvironmentSearchForm) Utils.getActionForm(request,
                EnvironmentSearchForm.SESSION_KEY, EnvironmentSearchForm.class);
        // populate doesn't clear !!!
        af.clearInput();
        af.populate(request);
        if (!af.validate()) {
            return;
        }

        setupQuery(af, request);
    }

    public void setupQuery(EnvironmentSearchForm af, PortletRequest request) {

        String QUERY_VALUE_DATATYPE = "www_topic";
        //        String QUERY_PARSE_DATATYPE = "datatype:www_topic";
        String QUERY_FIELD_TOPIC = "topic";
        String QUERY_FIELD_FUNCT_CATEGORY = "funct_category";
        String QUERY_FIELD_PARTNER = "partner";

        String FORM_VALUE_ALL = "all";

        IngridQuery query = null;

        try {
            //            query = QueryStringParser.parse(QUERY_PARSE_DATATYPE);
            query = new IngridQuery();
            query.setDataType(QUERY_VALUE_DATATYPE);

            // set parameters !
            String[] topics = request.getParameterValues(af.FIELD_TOPIC);
            String[] functCategories = request.getParameterValues(af.FIELD_FUNCT_CATEGORY);
            String[] partners = request.getParameterValues(af.FIELD_PARTNER);

            // TOPIC
            String topic = null;
            for (int i = 0; i < topics.length; i++) {
                // TODO at the moment we only use first TOPIC, backend can't handle OR yet
                topic = topics[i];
                break;
            }
            if (topic != null && !topic.equals(FORM_VALUE_ALL)) {
                query.addField(new FieldQuery(IngridQuery.AND, QUERY_FIELD_TOPIC, topic));
            }

            // FUNCT_CATEGORY
            String functCategory = null;
            for (int i = 0; i < functCategories.length; i++) {
                // TODO at the moment we only use first FUNCT_CATEGORY, backend can't handle OR yet
                functCategory = functCategories[i];
                break;
            }
            if (functCategory != null && !functCategory.equals(FORM_VALUE_ALL)) {
                query.addField(new FieldQuery(IngridQuery.AND, QUERY_FIELD_FUNCT_CATEGORY, functCategory));
            }

            // PARTNER
            String partner = null;
            for (int i = 0; i < partners.length; i++) {
                // TODO at the moment we only use first PARTNER, backend can't handle OR yet
                partner = partners[i];
                break;
            }
            if (partner != null && !partner.equals(FORM_VALUE_ALL)) {
                query.addField(new FieldQuery(IngridQuery.AND, QUERY_FIELD_PARTNER, partner));
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