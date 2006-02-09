package de.ingrid.portal.portlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.interfaces.impl.SNSInterfaceImpl;

public class ChronicleTeaserPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(ChronicleTeaserPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        // setTopic(Settings.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        DetailedTopic detail = SNSInterfaceImpl.getInstance().getAnniversary(new Date());

        HashMap result = new HashMap();
        if (detail != null) {
            result.put("title", detail.get("topicName"));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat portalFormat = new SimpleDateFormat("d MMM yyyy", request.getLocale());
            Date untilDate = null;
            Date fromDate = null;
            if (detail.get("from") != null) {
                try {
                    fromDate = df.parse((String) detail.get("from"));
                    result.put("from", portalFormat.format(fromDate));
                } catch (ParseException e) {
                    log.warn("error parsing from date.", e);
                }
            }
            if (detail.get("until") != null) {
                try {
                    untilDate = df.parse((String) detail.get("until"));
                    result.put("until", portalFormat.format(untilDate));
                } catch (ParseException e) {
                    log.warn("error parsing until date.", e);
                }
            }
            result.put("topicId", detail.get("topicId"));
            if (fromDate != null) {
                int years = new Date().getYear() - fromDate.getYear();
                result.put("years", new Integer(years));
            }
        }

        context.put("snsAnniversray", result);
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }

}