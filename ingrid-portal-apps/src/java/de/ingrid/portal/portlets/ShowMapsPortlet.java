package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsMapServiceManager;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.om.IngridTinyUrlSource;
import de.ingrid.portal.search.UtilsSearch;

public class ShowMapsPortlet extends GenericVelocityPortlet {

	private final static Logger log = LoggerFactory.getLogger(ShowMapsPortlet.class);

	public void init(PortletConfig config) throws PortletException {
		super.init(config);
	}

	public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        response.setTitle(messages.getString("maps.page.title"));

        String wmsURL = UtilsSearch.getWMSURL(request, request.getParameter("wms_url"), true);
        
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }
        
        if(Utils.getLoggedOn(request)){
        	context.put("logged", "true");        	
        }
        
        if(request.getParameter("t") != null){
        	try {
        		Session session = HibernateUtil.currentSession();
            	ProjectionList projList = Projections.projectionList();
            	projList.add(Projections.groupProperty("tinyConfig"));
            	Criteria crit =session.createCriteria(IngridTinyUrlSource.class)
                .setProjection(projList)
                .add(Restrictions.eq("tinyKey", request.getParameter("t")));
               
        		List foundData = UtilsDB.getValuesFromDB(crit, session, null, true);
        		
        		if (foundData.size() > 0) {
                    String entry = (String) foundData.get(0);
                    if (entry != null && entry.length() > 0) {
        				WMSInterfaceImpl.getInstance().setWMCDocument(entry, request.getPortletSession().getId());
        			}
                }
        	} catch (Exception ex) {
    			if (log.isErrorEnabled()) {
    				log.error("Problems processing default view:", ex);
    			}
    		}
        }
       
        if(request.getParameter("action") != null){
        	if(request.getParameter("action").equals("doTmpService")){
        		String coordType = request.getParameter("coordType");
        		ArrayList<HashMap<String, String>> wms_coords;
        		wms_coords = new ArrayList<HashMap<String,String>>();
        		wms_coords = UtilsMapServiceManager.getCoordinatesDetails(request.getParameter(Settings.RESULT_KEY_PLUG_ID), Integer.parseInt(request.getParameter(Settings.RESULT_KEY_DOC_ID)), messages.getString("common.result.showCoord.unknown"));
        		
        		if(wms_coords.size() > 0){
        			try {
        				String mapFileURL;
        				String mapFilePath;
        				mapFilePath = UtilsMapServiceManager.getTmpDirectory().concat(UtilsMapServiceManager.createTemporaryMapService(request.getParameter("title"),request.getParameter(Settings.RESULT_KEY_PLUG_ID).concat(request.getParameter(Settings.RESULT_KEY_DOC_ID)), wms_coords, coordType));
        				mapFileURL = UtilsMapServiceManager.getConfig().getString("temp_service_server").concat(mapFilePath).concat("&REQUEST=GetCapabilities&SERVICE=WMS&VERSION=1.1.1");
						wmsURL =  UtilsSearch.getWMSURL(request, mapFileURL, true);
					} catch (ConfigurationException e) {
						if(log.isErrorEnabled()){
							log.error("ConfigurationException by create temporary service: " + e);
						}
					} catch (Exception e) {
						if(log.isErrorEnabled()){
							log.error("Exception by create temporary service: " + e);
						}
					}
                }else{
                	if(log.isDebugEnabled()){
        				log.debug("No coordinates in hit!");
        			}
                }
        	}
        }    
        
        if (log.isDebugEnabled()) {
        	log.debug("Open WMS viewer with the following URL: " + wmsURL);
        }
       
        context.put("wmsURL", wmsURL);
        super.doView(request, response);
    }

	public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
		
	}

}
