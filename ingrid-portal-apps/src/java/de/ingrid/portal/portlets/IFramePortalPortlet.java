/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.portals.applications.webcontent.portlet.IFrameGenericPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridResourceBundle;

public class IFramePortalPortlet extends IFrameGenericPortlet {

	private final static Logger log = LoggerFactory.getLogger(IFramePortalPortlet.class);
	
	private PageManager pageManager;
	
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
		 pageManager = (PageManager)getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
	        if (null == pageManager)
	        {
	            throw new PortletException("Failed to find the Page Manager on portlet initialization");
	        }
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.portlet.IFrameGenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException {
        
		Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
       
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        
        // change title
        String myTitleKey = prefs.getValue("titleKey", "");
        response.setTitle(messages.getString(myTitleKey));
        
        // Use fragment_pref instead of portlet_preference
        ContentPage pw = (ContentPage) request.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE);
        Page myPage = null;
		try {
			myPage = pageManager.getPage(pw.getPath());
			Fragment root = (Fragment) myPage.getRootFragment();
			List<BaseFragmentElement> fragments = root.getFragments();
			 for (int i = 0; i < fragments.size(); i++) {
	                Fragment f = (Fragment) fragments.get(i);
	                for (int j = 0; j < f.getPreferences().size(); j++) {
						FragmentPreference fp = f.getPreferences().get(j);
						prefs.setValue(fp.getName(), fp.getValueList().get(0));
					};
			 }
		} catch (PageNotFoundException e) {
            log.error("Error page not found '" + pw.getPath() + "'", e);
        } catch (NodeException e) {
            log.error("Error getting page '" + pw.getPath() + "'", e);
        }
        
        super.doView(request, response);
    }

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.portlet.IFrameGenericPortlet#doEdit(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
		Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        
        // change title
         String myTitleKey = prefs.getValue("titleKey", "");
        response.setTitle(messages.getString(myTitleKey));
        
		response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }
	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.portlet.IFrameGenericPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
		
		// Edit fragment_pref 
        ContentPage pw = (ContentPage) request.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE);
        Page myPage = null;
		try {
			myPage = pageManager.getPage(pw.getPath());
			Fragment root = (Fragment) myPage.getRootFragment();
			List<BaseFragmentElement> fragments = root.getFragments();
			 for (int i = 0; i < fragments.size(); i++) {
	                Fragment f = (Fragment) fragments.get(i);
	                for (int j = 0; j < f.getPreferences().size(); j++) {
						FragmentPreference fp = f.getPreferences().get(j);
						Map<String, String[]> map = request.getParameterMap();
						List<String> l = new ArrayList<String>();
						l.add(map.get(fp.getName())[0]);
						fp.setValueList(l);
					};
			 }
			 pageManager.updatePage(myPage);
		} catch (PageNotFoundException e) {
            log.error("Error page not found '" + pw.getPath() + "'", e);
        } catch (NodeException e) {
            log.error("Error getting page '" + pw.getPath() + "'", e);
        }
        processPreferencesAction(request, actionResponse);
	}
}
	