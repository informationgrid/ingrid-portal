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
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.impl.DBAnniversaryInterfaceImpl;
import de.ingrid.portal.search.SearchState;
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
            result.put("title", detail.getTopicName());
            result.put("type", detail.getType());
            String topicFrom = detail.getFrom();
            if (topicFrom != null) {
                result.put("from", UtilsDate.parseDateToLocale(topicFrom, request.getLocale()));
            }
            String topicTo = detail.getTo();
            if (topicTo != null && !topicTo.equals(topicFrom)) {
                result.put("to", UtilsDate.parseDateToLocale(topicTo, request.getLocale()));
            }
            result.put("topicId", detail.get("topicId"));
            if (topicFrom != null) {
                int years = UtilsDate.yearsBetween(topicFrom, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                result.put("years", new Integer(years));
            }
            // fetch search term
            result.put("term", UtilsString.getSearchTerm(detail.getTopicName(), " "));

            /*
             String searchData = (String) detail.get(DetailedTopic.ASSICIATED_OCC);
             if (searchData != null) {
             String searchTerm = searchData;
             int endIndex = searchData.indexOf(',');
             if (endIndex != -1) {
             searchTerm = searchData.substring(0, endIndex);
             }
             if (searchTerm.charAt(0) == '"') {
             searchTerm = searchTerm.substring(1, searchTerm.length());
             }
             if (searchTerm.charAt(searchTerm.length() - 1) == '"') {
             searchTerm = searchTerm.substring(0, searchTerm.length() - 1);
             }
             result.put("term", searchTerm);
             } else {
             result.put("term", detail.getTopicName());
             }
             */
            /*
             ArrayList list = detail.getArrayList(DetailedTopic.INSTANCE_OF);
             System.out.println(list);
             */
        }

        context.put("snsAnniversary", result);
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        // redirect to our page with URL parameters for bookmarking
        actionResponse.sendRedirect(Settings.PAGE_CHRONICLE + SearchState.getURLParamsCatalogueSearch(request, null));
    }
}