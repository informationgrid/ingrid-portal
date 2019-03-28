/*
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.portlets.admin;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsPageLayout;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.*;
import java.io.IOException;
import java.util.*;

/**
 * Abstract parent class for configuration of home page via GUI !
 * 
 * @author joachim@wemove.com
 */
public abstract class ConfigureHomepagePortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ConfigureHomepagePortlet.class);

    private PageManager pageManager;

    private PortletRegistry registry;

    private ArrayList rightColumnPortlets = new ArrayList();

    private ArrayList leftColumnPortlets = new ArrayList();
    
    /** path to page will be set by subclass */
    protected String pagePath = null;

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

        try {
            // get all portlets from the portlet registry
            getIngridPortlet(request);

            context.put("rightColumnPortlets", rightColumnPortlets);
            context.put("leftColumnPortlets", leftColumnPortlets);

            // get all fragments from the users page
            Page homePage = pageManager.getPage(pagePath);
            Fragment root = (Fragment) homePage.getRootFragment();
            List fragments = root.getFragments();
            ArrayList rightColumnFragments = new ArrayList();
            ArrayList leftColumnFragments = new ArrayList();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment f = (Fragment) fragments.get(i);
                String resourceBundle = registry.getPortletDefinitionByUniqueName(f.getName()).getResourceBundle();
                IngridResourceBundle res = new IngridResourceBundle(PropertyResourceBundle.getBundle(resourceBundle,
                        request.getLocale()), request.getLocale());
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
                    Utils.ensureArraySize(leftColumnFragments, f.getLayoutRow() + 1);
                    leftColumnFragments.set(f.getLayoutRow(), portletProperties);
                } else if (f.getLayoutColumn() == 1) {
                    Utils.ensureArraySize(rightColumnFragments, f.getLayoutRow() + 1);
                    rightColumnFragments.set(f.getLayoutRow(), portletProperties);
                }
            }
            context.put("rightColumnFragments", rightColumnFragments);
            context.put("leftColumnFragments", leftColumnFragments);
        } catch (PageNotFoundException e) {
            log.error("Error page not found '" + pagePath + "'", e);
            context.put("errorcode", "ERROR_GETTING_HOME_PAGE");
        } catch (NodeException e) {
            log.error("Error getting page '" + pagePath + "'", e);
            context.put("errorcode", "ERROR_GETTING_HOME_PAGE");
        }
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        try {
            if (action.equalsIgnoreCase("doOriginalSettings")) {
                // get ingrid portlets from the portlet registry
                Page homePage = pageManager.getPage(pagePath);

                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 1);

                // iterate over the left portlets
                for (int i = 0; i < leftColumnPortlets.size(); i++) {
                	PortletDefinition p = (PortletDefinition) ((HashMap) leftColumnPortlets.get(i))
                            .get("portlet");
                    String portletName = p.getUniqueName();
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, (Fragment) homePage.getRootFragment(),
                            portletName, i, 0);
                }
                // iterate over the right portlets
                for (int i = 0; i < rightColumnPortlets.size(); i++) {
                	PortletDefinition p = (PortletDefinition) ((HashMap) rightColumnPortlets.get(i))
                            .get("portlet");
                    String portletName = p.getUniqueName();
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, (Fragment) homePage.getRootFragment(),
                            portletName, i, 1);
                }
                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 1);

                pageManager.updatePage(homePage);

            } else {
                Page homePage = pageManager.getPage(pagePath);

                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 1);

                // iterate over the left portlets
                for (int i = 0; i < leftColumnPortlets.size(); i++) {
                    // get the configured portlet from the request params
                    String slotVal = request.getParameter("c1r" + (i + 1));
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, (Fragment) homePage.getRootFragment(), slotVal,
                            i, 0);
                }

                // iterate over the right portlets
                for (int i = 0; i < rightColumnPortlets.size(); i++) {
                    // get the configured portlet from the request params
                    String slotVal = request.getParameter("c2r" + (i + 1));
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, (Fragment) homePage.getRootFragment(), slotVal,
                            i, 1);
                }
                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn((Fragment) homePage.getRootFragment(), 1);

                pageManager.updatePage(homePage);
            }

        } catch (PageNotFoundException e) {
            log.error("Error page not found '" + pagePath + "'", e);
        } catch (NodeException e) {
            log.error("Error getting page '" + pagePath + "'", e);
        } catch (Exception e) {
            log.error("General Error handling '" + pagePath + "'", e);
        }
    }

    private void getIngridPortlet(PortletRequest request) {
        Collection portletDefs = registry.getPortletApplication("ingrid-portal-apps").getPortlets();
        leftColumnPortlets.clear();
        rightColumnPortlets.clear();
        Iterator it = portletDefs.iterator();

        while (it.hasNext()) {
            PortletDefinition portlet = (PortletDefinition) it.next();
            Preference p = portlet.getPortletPreferences().getPortletPreference("portlet-type");
            if (p != null) {
                String type = p.getValues().get(0);
                p = portlet.getPortletPreferences().getPortletPreference("default-vertical-position");
                int defaultPos = Integer.parseInt((String) p.getValues().get(0));
                String resourceBundle = portlet.getResourceBundle();
                IngridResourceBundle res = new IngridResourceBundle(PropertyResourceBundle.getBundle(resourceBundle,
                        request.getLocale()), request.getLocale());
                p = portlet.getPortletPreferences().getPortletPreference("titleKey");
                String portletTitle = res.getString((String) p.getValues().get(0));
                if (type.equals("ingrid-home")) {
                    HashMap portletProperties = new HashMap();
                    portletProperties.put("portlet", portlet);
                    portletProperties.put("title", portletTitle);
                    synchronized (this) {
                        Utils.ensureArraySize(leftColumnPortlets, defaultPos + 1);
                        leftColumnPortlets.set(defaultPos, portletProperties);
                    }
                } else if (type.equals("ingrid-home-marginal")) {
                    HashMap portletProperties = new HashMap();
                    portletProperties.put("portlet", portlet);
                    portletProperties.put("title", portletTitle);
                    synchronized (this) {
                        Utils.ensureArraySize(rightColumnPortlets, defaultPos + 1);
                        rightColumnPortlets.set(defaultPos, portletProperties);
                    }
                }
            }
        }
    }
}
