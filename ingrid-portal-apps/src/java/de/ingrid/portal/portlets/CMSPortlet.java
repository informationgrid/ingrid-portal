/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
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

    /** InfoPortlet default template if not set via PSML */
    public final static String DEFAULT_TEMPLATE = "/WEB-INF/templates/default_cms.vm";
    public final static String SHORTCUT_TEMPLATE = "/WEB-INF/templates/shortcut_teaser_cms.vm";

    public final static String CMS_DEFAULT_KEY = "default.key";

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // read preferences
        PortletPreferences prefs = request.getPreferences();

        String myKey = prefs.getValue("cmsKey", CMS_DEFAULT_KEY);
        String myView;
        
        if(myKey.equals("portal.teaser.shortcut")){
        	myView= prefs.getValue("infoTemplate", SHORTCUT_TEMPLATE);
        }else{
        	myView= prefs.getValue("infoTemplate", DEFAULT_TEMPLATE);	
        }
        
        setDefaultViewPage(myView);

        Session session = HibernateUtil.currentSession();

        List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridCMS.class).add(
                Restrictions.eq("itemKey", myKey)), session, null, true);

        if (entities.size() > 0) {
            IngridCMS entry = (IngridCMS) entities.get(0);
            String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
            IngridCMSItem localizedItem = entry.getLocalizedEntry(lang);
            
            if(localizedItem == null ||localizedItem.getItemTitle().length() < 1 || localizedItem.getItemValue().length() < 1){
            	localizedItem = entry.getLocalizedEntry(Locale.getDefault().getLanguage());
            }
            
            response.setTitle(localizedItem.getItemTitle());
            context.put("cmsItemTitle", localizedItem.getItemTitle());
            context.put("cmsItemValue", localizedItem.getItemValue());
            
        	if(myKey.equals("portalu.about")){
        		String user = request.getRemoteUser();
                if(user != null && user.equals("admin")){
                	if(request.getPortalContext().getProperty("applicationRoot") != null){
                		context.put("version", Utils.getPortalVersion(request.getPortalContext().getProperty("applicationRoot")));
                    }
                	context.put("tool", new UtilsVelocity());
                }
            } else if (myKey.equals("portalu.teaser.inform")) {
                context.put("showNewsletter", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_NEWSLETTER_CMS_INFO, false));
            }
        }

        super.doView(request, response);
    }

}
