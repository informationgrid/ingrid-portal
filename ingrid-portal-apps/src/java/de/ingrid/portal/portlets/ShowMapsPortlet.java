package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.om.IngridTinyUrlSource;
import de.ingrid.portal.search.UtilsSearch;

public class ShowMapsPortlet extends GenericVelocityPortlet {

	private final static Log log = LogFactory.getLog(ShowMapsPortlet.class);

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
        
        if (log.isDebugEnabled()) {
        	log.debug("Open WMS viewer with the following URL: " + wmsURL);
        }
        context.put("wmsURL", wmsURL);

        
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
        super.doView(request, response);
    }

	public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
		
	}

}
