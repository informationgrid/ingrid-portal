package de.ingrid.portal.portlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.global.DateUtil;
import de.ingrid.portal.interfaces.impl.SNSInterfaceImpl;

public class ChronicleTeaserPortlet extends AbstractVelocityMessagingPortlet {

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        // setTopic(Settings.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        DetailedTopic[] details = SNSInterfaceImpl.getInstance().getAnniversaries(new Date());

        HashMap result = new HashMap();
        if (details.length > 0) {
            int entry = (int)(Math.random() * details.length);
            DetailedTopic detail = (DetailedTopic) details[entry];
            result.put("title", detail.get("topicName"));
            if (detail.get("from") != null) {
                result.put("from", DateUtil.parseDateToLocale(detail.get("from").toString(), request.getLocale()));
            }
            if (detail.get("until") != null) {
                result.put("until", DateUtil.parseDateToLocale(detail.get("until").toString(), request.getLocale()));
            }
            result.put("topicId", detail.get("topicId"));
            if (detail.get("from") != null) {
                int years = DateUtil.yearsBetween(detail.get("from").toString(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
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