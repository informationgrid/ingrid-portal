package de.ingrid.portal.portlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.interfaces.impl.DBAnniversaryInterfaceImpl;
import de.ingrid.utils.IngridHitDetail;

public class ChronicleTeaserPortlet extends GenericVelocityPortlet {

    public final static String TITLE_KEY = "teaser.chronicle.title";

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        response.setTitle(messages.getString(TITLE_KEY));

        IngridHitDetail[] details = DBAnniversaryInterfaceImpl.getInstance().getAnniversaries(new Date());

        HashMap result = new HashMap();
        if (details.length > 0) {
            int entry = (int) (Math.random() * details.length);
            DetailedTopic detail = (DetailedTopic) details[entry];
            result.put("title", detail.get("topicName"));
            if (detail.get("from") != null) {
                result.put("from", UtilsDate.parseDateToLocale(detail.get("from").toString(), request.getLocale()));
            }
            if (detail.get("until") != null && !detail.get("until").equals(detail.get("from"))) {
                result.put("until", UtilsDate.parseDateToLocale(detail.get("until").toString(), request.getLocale()));
            }
            result.put("topicId", detail.get("topicId"));
            if (detail.get("from") != null) {
                int years = UtilsDate.yearsBetween(detail.get("from").toString(), new SimpleDateFormat("yyyy-MM-dd")
                        .format(new Date()));
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