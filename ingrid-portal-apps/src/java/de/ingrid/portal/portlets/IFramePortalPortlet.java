package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridFragmentPref;
import de.ingrid.portal.om.IngridFragmentPrefValue;

public class IFramePortalPortlet extends org.apache.jetspeed.portlet.IFrameGenericPortlet {

	private final static Logger log = LoggerFactory.getLogger(IFramePortalPortlet.class);
	
	public final static String DEFAULT_TEMPLATE = "/WEB-INF/templates/application.vm";

	public void init(PortletConfig config) throws PortletException {
		super.init(config);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.portlet.IFrameGenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException {
        
		Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
       
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        
        // change title
        String myTitleKey = prefs.getValue("titleKey", "");
        response.setTitle(messages.getString(myTitleKey));
        
        // get fragment id
        Fragment pw = (Fragment) request.getAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE);
        String fragmentId = pw.getId().toString();
        
        // read portlet preferences from fragment preferences 
        if(fragmentId != null){
        	Session session = HibernateUtil.currentSession();
            List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridFragmentPref.class).add(Restrictions.eq("fragmentId", Long.parseLong(fragmentId))), session, null, true);
            for(int i=0; i<entities.size();i++){
            	IngridFragmentPref pref = (IngridFragmentPref) entities.get(i);
            	Long prefId = pref.getId();
            	String prefName = pref.getFragmentName();
            	readIFramePrefsParams(prefId, prefName, request);
            }
            
            request.getPortletSession().setAttribute("fragmentIdIFrame", fragmentId);
        }
        
        super.doView(request, response);
    }

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.portlet.IFrameGenericPortlet#doEdit(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
		Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
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
		
		// change fragment preferences
        String fragmentId = (String) request.getPortletSession().getAttribute("fragmentIdIFrame");
        if(fragmentId != null){
        	Session session = HibernateUtil.currentSession();
            List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridFragmentPref.class).add(Restrictions.eq("fragmentId", Long.parseLong(fragmentId))), session, null, true);
            for(int i=0; i<entities.size();i++){
            	IngridFragmentPref pref = (IngridFragmentPref) entities.get(i);
            	Long prefId = pref.getId();
            	String prefName = pref.getFragmentName();
            	editIFramePrefsParams(prefId, prefName, request);
            }
            
        }
		
        processPreferencesAction(request, actionResponse);
	}

	/**
	 * Edit IFrame preferences as user "admin" and save preferences into table "fragment_prefs_value"
	 * 
	 * @param prefId
	 * @param prefName
	 * @param request
	 */
	private void editIFramePrefsParams(Long prefId, String prefName, PortletRequest request) {

		Session session = HibernateUtil.currentSession();
		IngridFragmentPrefValue prefValue = (IngridFragmentPrefValue) session.load(IngridFragmentPrefValue.class, prefId);
		HashMap params = (HashMap) request.getParameterMap();
		
		if(prefValue != null){
			String[] values = (String[]) params.get(prefName);
			prefValue.setPrefValue(values[0]);
			UtilsDB.updateDBObject(prefValue);
		}
	}
	
	/**
	 * Read IFrame preferences from table "fragment_prefs_value" and set it to portlet preferences.
	 * 
	 * @param prefId
	 * @param prefName
	 * @param request
	 */
	private void readIFramePrefsParams(Long prefId, String prefName, RenderRequest request) {
		
		Session session = HibernateUtil.currentSession();
		PortletPreferences prefs = request.getPreferences();
		IngridFragmentPrefValue prefValue = (IngridFragmentPrefValue) session.load(IngridFragmentPrefValue.class, prefId);
		
		if(prefs != null && prefValue != null){
				try {
					prefs.setValue(prefName, prefValue.getPrefValue());
				} catch (ReadOnlyException e) {
					log.error("Error by setting portlet prefences: " + e);
					e.printStackTrace();
				}
		}
		
	}
}
	