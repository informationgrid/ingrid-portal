/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsPortletServeResources;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridCMS;
import de.ingrid.portal.om.IngridCMSItem;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CMSPortlet extends GenericVelocityPortlet {
    private static final Logger log = LoggerFactory.getLogger(CMSPortlet.class);

    /** InfoPortlet default template if not set via PSML */
    public static final String DEFAULT_TEMPLATE = "/WEB-INF/templates/default_cms.vm";
    public static final String SHORTCUT_TEMPLATE = "/WEB-INF/templates/shortcut_teaser_cms.vm";

    public static final String CMS_DEFAULT_KEY = "default.key";

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        String paramURL = request.getParameter( "url" );

        if(paramURL != null) {
            if (resourceID.equals( "httpURLImage" )) {
                UtilsPortletServeResources.getHttpUrlImage(paramURL, response, resourceID);
            }
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

        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String helpKey = prefs.getValue( "helpKey", "");
        context.put( "helpKey", helpKey );

        String myKey = prefs.getValue("cmsKey", CMS_DEFAULT_KEY);
        String myView;
        
        if(myKey.equals("portal.teaser.shortcut")){
            myView= prefs.getValue("infoTemplate", SHORTCUT_TEMPLATE);
        }else{
            myView= prefs.getValue("infoTemplate", DEFAULT_TEMPLATE);    
        }

        ResourceURL restUrl = response.createResourceURL();
        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLImage" );
        request.setAttribute( "restUrlHttpGetImage", restUrl.toString() );

        setDefaultViewPage(myView);

        Session session = HibernateUtil.currentSession();

        List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridCMS.class).add(
                Restrictions.eq("itemKey", myKey)), session, null, true);

        if (!entities.isEmpty()) {
            IngridCMS entry = (IngridCMS) entities.get(0);
            String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
            IngridCMSItem localizedItem = entry.getLocalizedEntry(lang);
            
            if(localizedItem == null ||localizedItem.getItemTitle().length() < 1 || localizedItem.getItemValue().length() < 1){
                localizedItem = entry.getLocalizedEntry(Locale.getDefault().getLanguage());
            }
            
            response.setTitle(localizedItem.getItemTitle());
            context.put("cmsItemTitle", localizedItem.getItemTitle());
            context.put("cmsItemValue", localizedItem.getItemValue());
            
            if(myKey.equals("ingrid.about")){
                String user = request.getRemoteUser();
                if(user != null && user.equals("admin")){
                    if(request.getPortalContext().getProperty("applicationRoot") != null){
                        context.put("version", Utils.getPortalVersion(request.getPortalContext().getProperty("applicationRoot")));
                    }
                    context.put("tool", new UtilsVelocity());
                }
            }
        }

        super.doView(request, response);
    }

}
