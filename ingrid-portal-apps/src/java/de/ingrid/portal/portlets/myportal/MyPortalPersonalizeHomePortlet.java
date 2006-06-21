/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.myportal;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class MyPortalPersonalizeHomePortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MyPortalPersonalizeHomePortlet.class);

    private PageManager pageManager;

    private PortletRegistry registry;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }
        registry = (PortletRegistry) getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry) {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }

    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "searchSettings.title.rankingAndGrouping");
        response.setTitle(messages.getString(titleKey));

        Principal principal = request.getUserPrincipal();

        try {
            Page homePage = pageManager.getPage(Folder.USER_FOLDER + principal.getName() + "/default-page.psml");
            Fragment root = homePage.getRootFragment();
            List fragments = root.getFragments();
            ArrayList rightColumnFragments = new ArrayList();
            ArrayList leftColumnFragments = new ArrayList();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment f = (Fragment) fragments.get(i);
                String resourceBundle = registry.getPortletDefinitionByUniqueName(f.getName()).getResourceBundle();
                IngridResourceBundle res = new IngridResourceBundle(PropertyResourceBundle.getBundle(resourceBundle,
                        request.getLocale()));
                HashMap portletProperties = new HashMap();
                portletProperties.put("fragment", f);
                List fragmentPrefs = f.getPreferences();
                for (int j = 0; j < fragmentPrefs.size(); j++) {
                    FragmentPreference pref = (FragmentPreference) fragmentPrefs.get(j);
                    String name = pref.getName();
                    if (name.equals("titleKey")) {
                        portletProperties.put("title", res.getString((String) pref.getValueList().get(0)));
                    }
                }
                if (f.getLayoutColumn() == 0) {
                    rightColumnFragments.add(portletProperties);
                } else if (f.getLayoutColumn() == 1) {
                    leftColumnFragments.add(portletProperties);
                }
            }
            context.put("rightColumnFragments", rightColumnFragments);
            context.put("leftColumnFragments", leftColumnFragments);
        } catch (PageNotFoundException e) {
            log.error("Error page not found '" + Folder.USER_FOLDER + principal.getName() + "/default-page.psml" + "'",
                    e);
            context.put("errorcode", "ERROR_GETTING_HOME_PAGE");
        } catch (NodeException e) {
            log
                    .error("Error getting page '" + Folder.USER_FOLDER + principal.getName() + "/default-page.psml"
                            + "'", e);
            context.put("errorcode", "ERROR_GETTING_HOME_PAGE");
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // TODO Auto-generated method stub
        super.processAction(request, actionResponse);
    }

}
