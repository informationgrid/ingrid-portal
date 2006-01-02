package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.search.mockup.SearchResultListMockup;



public class SimpleSearchPortlet extends GenericVelocityPortlet
{

	private Integer selectedDataSource = new Integer(1);
	private String queryStr = null;
	private boolean actionProcessed = false;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    } 
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
    	// reset queryStr if NO action was processed
    	if (!actionProcessed) {
    		queryStr = null;
    	}

    	String q = request.getParameter("q");
    	if (q != null) {
    		queryStr = q;
    	}
    	if (queryStr != null) {
        	List rankedSRL = doSearch(queryStr, selectedDataSource.toString(), true);
        	List unrankedSRL = doSearch(queryStr, selectedDataSource.toString(), false);
        	context.put("rankedResultList", rankedSRL);
        	context.put("unrankedResultList", unrankedSRL);
        	context.put("q", queryStr);
	    }
    	context.put("ds", selectedDataSource);
    	actionProcessed = false;
        
        super.doView(request, response);
    }
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {

    	String action = request.getParameter("action");
        
        if (action == null) {
        	return;
        } else if (action.equalsIgnoreCase("doChangeDS")) {
        	String ds = request.getParameter("ds");
        	selectedDataSource = Integer.decode(ds);
        }
        
    	String q = request.getParameter("q");
    	if (q != null) {
    		queryStr = q;
	    }
    	actionProcessed = true;
    }
    
    private List doSearch(String qryStr, String ds, boolean ranking) {
    	
    	if (ranking) {
    		return SearchResultListMockup.getRankedSearchResultList();
    	} else {
    		return SearchResultListMockup.getUnrankedSearchResultList();
    	}
    }
    
}