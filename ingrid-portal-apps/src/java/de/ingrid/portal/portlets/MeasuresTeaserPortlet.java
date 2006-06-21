package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.search.SearchState;

public class MeasuresTeaserPortlet extends GenericVelocityPortlet {

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // get rubrics
        List rubrics = UtilsDB.getMeasuresRubrics();
        context.put("rubricList", rubrics);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "teaser.measures.title");
        response.setTitle(messages.getString(titleKey));

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        // redirect to our page with URL parameters for bookmarking
        actionResponse.sendRedirect(Settings.PAGE_MEASURES + SearchState.getURLParamsCatalogueSearch(request, null));
    }
}