package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.portal.search.mockup.SimilarNodeFactoryMockup;
import de.ingrid.portal.search.mockup.SimilarNodeMockup;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;



public class SearchResultPortlet extends GenericVelocityPortlet
{

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    } 
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
    	PortletSession session = request.getPortletSession();
    	HashMap ps = (HashMap) session.getAttribute("page_state");
    	if (ps == null) {
    		ps = new HashMap();
    		session.setAttribute("page_state", ps);
    	}
    	
    	// initialization if no action has been processed before
    	if (!ps.containsKey("ACTION_PROCESSED")) {
    		ps.remove("Q");
    		ps.remove("OPEN_SIMILAR");
    		ps.put("SELECTED_DS", "1");
    		ps.remove("SIMILAR_ROOT_NODE");
    	}

    	String q = request.getParameter("q");
    	if (q != null && q.length() > 0) {
/*    		try {
				IngridQuery iq = QueryStringParser.parse(q);
				q = iq.getDescription();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/			
			ps.put("Q", q);
    	}
    	if (ps.containsKey("Q")) {
        	List rankedSRL = doSearch((String)ps.get("Q"), (String)ps.get("SELECTED_DS"), true);
        	List unrankedSRL = doSearch((String)ps.get("Q"), (String)ps.get("SELECTED_DS"), false);
        	context.put("rankedResultList", rankedSRL);
        	context.put("unrankedResultList", unrankedSRL);
	    }
    	
    	ps.remove("ACTION_PROCESSED");
    	context.put("ps", ps);
        
        super.doView(request, response);
    }
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {

    	String action = request.getParameter("action");
    	PortletSession session = request.getPortletSession();
    	HashMap ps = (HashMap) session.getAttribute("page_state");
    	if (ps == null) {
    		ps = new HashMap();
    		session.setAttribute("page_state", ps);
    	}

    	
        if (action == null) {
        	return;
        } else if (action.equalsIgnoreCase("doSearch")) {
            ps.remove("Q");
            String q = request.getParameter("q");
        	if (q != null && q.length() > 0) {
        		ps.put("Q", q);
    	    }
        	ps.remove("OPEN_SIMILAR");
        	ps.remove("SIMILAR_ROOT_NODE");
        } else if (action.equalsIgnoreCase("doChangeDS")) {
        	String ds = request.getParameter("ds");
        	ps.put("SELECTED_DS", ds);
        } else if (action.equalsIgnoreCase("doOpenSimilar")) {
        	ps.put("OPEN_SIMILAR", "1");
        	ps.put("SIMILAR_ROOT_NODE", SimilarNodeFactoryMockup.getSimilarNodes());
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
        	ps.remove("OPEN_SIMILAR");
        	ps.remove("SIMILAR_ROOT_NODE");
        } else if (action.equalsIgnoreCase("doOpenNode")) {
        	SimilarNodeFactoryMockup.setOpen((SimilarNodeMockup)ps.get("SIMILAR_ROOT_NODE"), request.getParameter("nodeId"), true);
        } else if (action.equalsIgnoreCase("doCloseNode")) {
        	SimilarNodeFactoryMockup.setOpen((SimilarNodeMockup)ps.get("SIMILAR_ROOT_NODE"), request.getParameter("nodeId"), false);
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
        	// TODO
        }
        
    	ps.put("ACTION_PROCESSED", "1");
    }
    
    private List doSearch(String qryStr, String ds, boolean ranking) {
    	
    	if (ranking) {
    		return SearchResultListMockup.getRankedSearchResultList();
    	} else {
    		return SearchResultListMockup.getUnrankedSearchResultList();
    	}
    }
    
}