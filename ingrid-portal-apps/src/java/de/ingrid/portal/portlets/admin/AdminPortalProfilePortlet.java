/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.portlets.admin;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsPageLayout;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author joachim@wemove.com
 */
public class AdminPortalProfilePortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(AdminPortalProfilePortlet.class);

    private PageManager pageManager;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        PortletRegistry portletRegistry;
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
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        
        // set localized title for this page
        response.setTitle(messages.getString("admin.portal.profile.title"));

        String[] portalProfiles = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_PROFILES);
        ArrayList<HashMap<String, String>> profiles = new ArrayList<>();
        for (int i = 0; i < portalProfiles.length; i++) {
            profiles.add(new HashMap<>());
            HashMap<String,String> map = profiles.get(i);
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
    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        String action = request.getParameter("action");

        if (action != null && action.equals("switchProfile")) {
            String profileName = request.getParameter("profile");
            String databasePostfix = "_mysql";
            if (UtilsDB.isOracle()) {
                databasePostfix = "_oracle";
            } else if (UtilsDB.isPostgres()) {
                databasePostfix = "_postgres";
            }
            String profileDescriptor = getPortletConfig().getPortletContext().getRealPath(
                    "/profiles/" + profileName + "/profile" + databasePostfix + ".xml");
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
                    if (portletNames != null && !portletNames.isEmpty()) {
                        // defragmentation
                        UtilsPageLayout.defragmentLayoutColumn((Fragment) p.getRootFragment(), 0);
                        UtilsPageLayout.defragmentLayoutColumn((Fragment) p.getRootFragment(), 1);
                        // remove all fragments
                        UtilsPageLayout.removeAllFragmentsInColumn(p, (Fragment) p.getRootFragment(), 0);
                        UtilsPageLayout.removeAllFragmentsInColumn(p, (Fragment) p.getRootFragment(), 1);
                        for (int j = 0; j < portletNames.size(); j++) {
                            String portletName = (String) portletNames.get(j);
                            try {
                                List portletPrefsNames  = null;
                                List<FragmentPreference> prefs = new ArrayList<>();
                                
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
                                        List<String> pl = (List<String>)(List<?>) profile.getList("pages.page(" + i + ").portlets.portlet("+j+").preference("+k+").value");
                                        f.setValueList(pl);
                                        prefs.add(f);
                                    }
                                } catch (Exception e) {
                                    log.error(e.toString());
                                }
                                UtilsPageLayout.positionPortletOnPage(pageManager, p, (Fragment) p.getRootFragment(), portletName,
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
                    String dstContext = dst.substring(0, dst.indexOf('/'));
                    String dstPath = dst.substring(dst.indexOf('/') + 1);
                    String dstFileName = ((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV))
                            .getConfig().getServletContext().getContext("/" + dstContext).getRealPath(dstPath);
                    
                    if (actionName.equalsIgnoreCase("copy") && !srcFileName.equals(dstFileName)) {
                        copy(srcFileName, dstFileName);
                    }else if(actionName.equalsIgnoreCase("copy-dir") && !srcFileName.equals(dstFileName)){
                        copyDir(srcFileName, dstFileName);
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
        try (
            FileChannel in = new FileInputStream(source).getChannel();
            FileChannel out = new FileOutputStream(dest).getChannel();
        ){
            out.transferFrom(in, 0, in.size());
        } catch (Exception e) {
            log.error("Error copy files ('" + source.getAbsolutePath() + "' -> '" + dest.getAbsolutePath() + "')", e);
        }
    }

    /**
     * Copy directory to file system.
     * 
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copyDir(String source, String dest) throws IOException {
        File sourceDir = new File(source);
        File destDir = new File(dest);
        File[] sourceFiles = sourceDir.listFiles();
        
        if(!destDir.exists() && sourceDir.isDirectory()){
            destDir.mkdir();
        }
        
        for (int i = 0; i < sourceFiles.length; i++) {
            File sourceFile = sourceFiles[i];
            File destFile = new File(dest.concat("/").concat(sourceFile.getName()));
            
            if(sourceFile.isDirectory()){
                copyDir(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            }else{
                copy(sourceFile, destFile);
            }
        }
    }
}
