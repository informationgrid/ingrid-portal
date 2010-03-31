/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsPageLayout;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminPortalProfilePortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(AdminPortalProfilePortlet.class);

    private PageManager pageManager;

    private PortletRegistry portletRegistry;
    
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }
        portletRegistry = (PortletRegistry) getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == portletRegistry) {
            throw new PortletException("Failed to find the PortletRegistry Manager on portlet initialization");
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
        
        // set localized title for this page
        response.setTitle(messages.getString("admin.portal.profile.title"));

        String[] portalProfiles = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_PROFILES);
        ArrayList profiles = new ArrayList();
        for (int i = 0; i < portalProfiles.length; i++) {
            profiles.add(new HashMap());
            HashMap map = (HashMap) profiles.get(i);
            map.put("id", portalProfiles[i]);
            map.put("title_key", PortalConfig.getInstance().getString(
                    PortalConfig.PORTAL_PROFILE.concat(".").concat(portalProfiles[i]).concat(".title")));
        }

        context.put("profiles", profiles);

        context.put("switchedToProfile", request.getParameter("switchedToProfile"));

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        String action = request.getParameter("action");

        if (action != null && action.equals("switchProfile")) {
            String profileName = request.getParameter("profile");
            String profileDescriptor = getPortletConfig().getPortletContext().getRealPath(
                    "/profiles/" + profileName + "/profile.xml");
            String pageName = null;
            try {
                XMLConfiguration profile = new XMLConfiguration(profileDescriptor);
                // set page configurations
                // this info will be held in the database
                List pages = profile.getList("pages.page.name");
                for (int i = 0; i < pages.size(); i++) {
                    pageName = (String) pages.get(i);

                    // set visibility of the page
                    boolean hidden;
                    Page p = pageManager.getPage(Folder.PATH_SEPARATOR + pageName);
                    try {
                        hidden = profile.getBoolean("pages.page(" + i + ").hidden");
                        p.setHidden(hidden);
                    } catch (ConversionException e) {
                        log.warn("No tag 'hidden' found for page '" + pageName + "'", e);
                    }
                    pageManager.updatePage(p);

                    // set page layout configuration
                    List portletNames = profile.getList("pages.page(" + i + ").portlets.portlet.name");
                    if (portletNames != null && portletNames.size() > 0) {
                        // defragmentation
                        UtilsPageLayout.defragmentLayoutColumn(p.getRootFragment(), 0);
                        UtilsPageLayout.defragmentLayoutColumn(p.getRootFragment(), 1);
                        // remove all fragments
                        UtilsPageLayout.removeAllFragmentsInColumn(p, p.getRootFragment(), 0);
                        UtilsPageLayout.removeAllFragmentsInColumn(p, p.getRootFragment(), 1);
                        for (int j = 0; j < portletNames.size(); j++) {
                            String portletName = (String) portletNames.get(j);
                            //portletRegistry.getPortletDefinitionByIdentifier("IngridInformPortlet").getPreferenceSet();//portletRegistry.getAllPortletDefinitions();
                            try {
                            	List portletPrefsNames  = null;
                            	List<FragmentPreference> prefs = new ArrayList<FragmentPreference>();
                            	
                                int row = profile.getInt("pages.page(" + i + ").portlets.portlet(" + j + ")[@row]");
                                int col = profile.getInt("pages.page(" + i + ").portlets.portlet(" + j + ")[@col]");
                                try {
                                	// get the hidden information if it is available
                                	portletPrefsNames = profile.getList("pages.page(" + i + ").portlets.portlet("+j+").preference[@name]");
                                	for (int k = 0; k < portletPrefsNames.size(); k++) {
                                		FragmentPreference f = pageManager.newFragmentPreference();
                                		// set the name of the preference
                                		f.setName((String)portletPrefsNames.get(k));
                                		// get the values for this preference
                                		List<String> pl = profile.getList("pages.page(" + i + ").portlets.portlet("+j+").preference("+k+").value");
                                		f.setValueList(pl);
                                		prefs.add(f);
                                	}
                                } catch (Exception e) {
                                	log.error(e);
                                }
                                UtilsPageLayout.positionPortletOnPage(pageManager, p, p.getRootFragment(), portletName,
                                        row, col, prefs);
                            } catch (ConversionException e) {
                                log.warn("No 'x' or 'y' attribute found for portlet '" + portletName + "' on page '"
                                        + pageName + "'", e);
                            }
                        }
                        pageManager.updatePage(p);
                    }
                }

                // process files copy actions
                List fileActions = profile.getList("files.file.action");
                for (int i = 0; i < fileActions.size(); i++) {
                    String actionName = (String) fileActions.get(i);
                    String src = profile.getString("files.file(" + i + ").src");
                    String dst = profile.getString("files.file(" + i + ").dst");
                    if (dst == null) {
                        dst = src;
                    }
                    String srcFileName = getPortletConfig().getPortletContext().getRealPath(
                            "/profiles/" + profileName + "/" + src);
                    String dstContext = dst.substring(0, dst.indexOf("/"));
                    String dstPath = dst.substring(dst.indexOf("/") + 1);
                    String dstFileName = ((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV))
                            .getConfig().getServletContext().getContext("/" + dstContext).getRealPath(dstPath);
                    
                    if (actionName.equalsIgnoreCase("copy")) {
                        if (!srcFileName.equals(dstFileName)) {
                            copy(srcFileName, dstFileName);
                        }
                    }else if(actionName.equalsIgnoreCase("copy-dir")){
                    	if (!srcFileName.equals(dstFileName)) {
                    		copyDir(srcFileName, dstFileName);
                        }
                    }
                }

                // process sql actions
                List sqlActions = profile.getList("sql.execute");
                for (int i = 0; i < sqlActions.size(); i++) {
                    String sqlAction = (String) sqlActions.get(i);
                    UtilsDB.executeRawUpdateSQL(sqlAction);
                }

                response.setRenderParameter("switchedToProfile", profileName);

            } catch (ConfigurationException e) {
                log.error("Error reading profile configuration (" + profileDescriptor + ")", e);
            } catch (PageNotFoundException e) {
                log.error("Page not found from  (" + Folder.PATH_SEPARATOR + pageName + ")", e);
            } catch (NodeException e) {
                log.error("Error reading page (" + Folder.PATH_SEPARATOR + pageName + ")", e);
            }
        }
    }

    /**
     * Copy Files in file system.
     * 
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copy(String source, String dest) throws IOException {
        copy(new File(source), new File(dest));
    }

    /**
     * Copy Files in file system.
     * 
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copy(File source, File dest) throws IOException {
        FileChannel in = null, out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();

            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();
            out.transferFrom(in, 0, in.size());
        } catch (Exception e) {
            log.error("Error copy files ('" + source.getAbsolutePath() + "' -> '" + dest.getAbsolutePath() + "')", e);
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    /**
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copyDir(String source, String dest) throws IOException {
        File sourceDir = new File(source);
        File[] sourceFiles = sourceDir.listFiles();
        
    	for (int i = 0; i < sourceFiles.length; i++) {
    		File destFile = new File(dest.concat("/").concat(sourceFiles[i].getName()));
    		copy(sourceFiles[i], destFile);
    	}
    }
}
