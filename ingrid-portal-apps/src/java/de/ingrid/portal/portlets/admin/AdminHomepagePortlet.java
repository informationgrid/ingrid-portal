/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.pluto.om.common.Preference;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsPageLayout;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminHomepagePortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(AdminHomepagePortlet.class);

    private PageManager pageManager;

    private PortletRegistry registry;

    private ArrayList rightColumnPortlets = new ArrayList();

    private ArrayList leftColumnPortlets = new ArrayList();

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

        try {
            // get all portlets from the portlet registry
            getIngridPortlet(request);

            context.put("rightColumnPortlets", rightColumnPortlets);
            context.put("leftColumnPortlets", leftColumnPortlets);

            // get all fragments from the users page
            Page homePage = pageManager.getPage(Folder.PATH_SEPARATOR + "default-page.psml");
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
            log.error("Error page not found '" + Folder.PATH_SEPARATOR + "default-page.psml" + "'", e);
            context.put("errorcode", "ERROR_GETTING_HOME_PAGE");
        } catch (NodeException e) {
            log.error("Error getting page '" + Folder.PATH_SEPARATOR + "default-page.psml" + "'", e);
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
                Page homePage = pageManager.getPage(Folder.PATH_SEPARATOR + "default-page.psml");

                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 1);

                // iterate over the left portlets
                for (int i = 0; i < leftColumnPortlets.size(); i++) {
                    PortletDefinitionComposite p = (PortletDefinitionComposite) ((HashMap) leftColumnPortlets.get(i))
                            .get("portlet");
                    String portletName = p.getUniqueName();
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, homePage.getRootFragment(),
                            portletName, i, 0);
                }
                // iterate over the right portlets
                for (int i = 0; i < rightColumnPortlets.size(); i++) {
                    PortletDefinitionComposite p = (PortletDefinitionComposite) ((HashMap) rightColumnPortlets.get(i))
                            .get("portlet");
                    String portletName = p.getUniqueName();
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, homePage.getRootFragment(),
                            portletName, i, 1);
                }
                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 1);

                pageManager.updatePage(homePage);

            } else {
                Page homePage = pageManager.getPage(Folder.PATH_SEPARATOR + "default-page.psml");

                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 1);

                // iterate over the left portlets
                for (int i = 0; i < leftColumnPortlets.size(); i++) {
                    // get the configured portlet from the request params
                    String slotVal = request.getParameter("c1r" + (i + 1));
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, homePage.getRootFragment(), slotVal,
                            i, 0);
                }

                // iterate over the right portlets
                for (int i = 0; i < rightColumnPortlets.size(); i++) {
                    // get the configured portlet from the request params
                    String slotVal = request.getParameter("c2r" + (i + 1));
                    UtilsPageLayout.positionPortletOnPage(pageManager, homePage, homePage.getRootFragment(), slotVal,
                            i, 1);
                }
                // defragmentation
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 0);
                UtilsPageLayout.defragmentLayoutColumn(homePage.getRootFragment(), 1);

                pageManager.updatePage(homePage);
            }

        } catch (PageNotFoundException e) {
            log.error("Error page not found '" + Folder.PATH_SEPARATOR + "default-page.psml" + "'", e);
        } catch (NodeException e) {
            log.error("Error getting page '" + Folder.PATH_SEPARATOR + "default-page.psml" + "'", e);
        } catch (Exception e) {
            log.error("General Error handling '" + Folder.PATH_SEPARATOR + "default-page.psml" + "'", e);
        }
    }

    private void getIngridPortlet(PortletRequest request) {
        Collection portletDefs = registry.getPortletApplication("ingrid-portal-apps").getPortletDefinitions();
        leftColumnPortlets.clear();
        rightColumnPortlets.clear();
        Iterator it = portletDefs.iterator();

        while (it.hasNext()) {
            PortletDefinitionComposite portlet = (PortletDefinitionComposite) it.next();
            Preference p = portlet.getPreferenceSet().get("portlet-type");
            if (p != null) {
                String type = (String) p.getValues().next();
                p = portlet.getPreferenceSet().get("default-vertical-position");
                int defaultPos = Integer.parseInt((String) p.getValues().next());
                String resourceBundle = portlet.getResourceBundle();
                IngridResourceBundle res = new IngridResourceBundle(PropertyResourceBundle.getBundle(resourceBundle,
                        request.getLocale()));
                p = portlet.getPreferenceSet().get("titleKey");
                String portletTitle = res.getString((String) p.getValues().next());
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
