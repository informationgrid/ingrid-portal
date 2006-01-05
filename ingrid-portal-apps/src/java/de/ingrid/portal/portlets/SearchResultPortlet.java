package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.SimilarTreeNode;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.portal.search.mockup.SimilarNodeFactoryMockup;

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
    	PageState ps = (PageState) session.getAttribute("page_state");
    	if (ps == null) {
    		ps = new PageState(this.getClass().getName());
    		ps = initPageState(ps);
    		session.setAttribute("page_state", ps);
    	}
    	
    	// initialization if no action has been processed before
    	if (!ps.getBoolean("isActionProcessed")) {
    		ps = initPageState(ps);
    	}
    	ps.setBoolean("isActionProcessed", false);

    	String q = request.getParameter("q");
    	if (q != null && q.length() > 0) {
			ps.put("query", q);
    	}
    	if (ps.get("query") != null) {
    		SearchResultList rankedSRL = doSearch(ps.getString("query"), ps.getString("selectedDS"), ps.getInt("rankedNavStart"), ps.getInt("rankedNavLimit"), true);
    		SearchResultList unrankedSRL = doSearch(ps.getString("query"), ps.getString("selectedDS"), ps.getInt("unrankedNavStart"), ps.getInt("unrankedNavLimit"), false);
        	ps.putInt("rankedCurrentPage", ps.getInt("rankedNavStart") / ps.getInt("rankedNavLimit") + 1);
        	ps.putInt("rankedNumberOfPages", rankedSRL.getNumberOfHits() / ps.getInt("rankedNavLimit") + (int)Math.ceil(rankedSRL.getNumberOfHits() % ps.getInt("rankedNavLimit")));
        	ps.putInt("unrankedCurrentPage", ps.getInt("unrankedNavStart") / ps.getInt("unrankedNavLimit") + 1);
        	ps.putInt("unrankedNumberOfPages", unrankedSRL.getNumberOfHits() / ps.getInt("unrankedNavLimit") + (int)Math.ceil(unrankedSRL.getNumberOfHits() % ps.getInt("unrankedNavLimit")));
        	context.put("rankedResultList", rankedSRL);
        	context.put("unrankedResultList", unrankedSRL);
	    }
    	
    	context.put("ps", ps);
        
        super.doView(request, response);
    }
    
    
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {

    	String action = request.getParameter("action");
    	PortletSession session = request.getPortletSession();
    	PageState ps = (PageState) session.getAttribute("page_state");
    	if (ps == null) {
    		ps = new PageState(this.getClass().getName());
    		ps = initPageState(ps);
    		session.setAttribute("page_state", ps);
    	}
    	
        if (action == null) {
        	action = "";
        } else if (action.equalsIgnoreCase("doSearch")) {
            ps.putString("query", null);
            String q = request.getParameter("q");
        	if (q != null && q.length() > 0) {
                ps.putString("query", q);
    	    }
        	ps.setBoolean("isSimilarOpen", false);
        	ps.put("similarRoot", null);
        	ps.putInt("rankedNavStart",0);
        	ps.putInt("unrankedNavStart",0);
        } else if (action.equalsIgnoreCase("doChangeDS")) {
        	String ds = request.getParameter("ds");
        	ps.putString("selectedDS", ds);
        } else if (action.equalsIgnoreCase("doOpenSimilar")) {
        	ps.setBoolean("isSimilarOpen", true);
        	ps.put("similarRoot", SimilarNodeFactoryMockup.getSimilarNodes());
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
        	ps.setBoolean("isSimilarOpen", false);
        	ps.put("similarRoot", null);
        } else if (action.equalsIgnoreCase("doOpenNode")) {
        	SimilarNodeFactoryMockup.setOpen((SimilarTreeNode)ps.get("similarRoot"), request.getParameter("nodeId"), true);
        } else if (action.equalsIgnoreCase("doCloseNode")) {
        	SimilarNodeFactoryMockup.setOpen((SimilarTreeNode)ps.get("similarRoot"), request.getParameter("nodeId"), false);
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
        	// TODO
        }
    	try {
    		ps.putInt("rankedNavStart",Integer.parseInt(request.getParameter("rstart")));
    	} catch (NumberFormatException e) {
    		//ps.setRankedNavStart(0);
    	}
    	try {
    		ps.putInt("unrankedNavStart",Integer.parseInt(request.getParameter("nrstart")));
    	} catch (NumberFormatException e) {
    		//ps.setUnrankedNavStart(0);
    	}
        
    	ps.setBoolean("isActionProcessed", true);
    }
    
    private SearchResultList doSearch(String qryStr, String ds, int start, int limit, boolean ranking) {
    	
    	SearchResultList result = new SearchResultList();
    	
    	if (ranking) {
    		SearchResultList l = SearchResultListMockup.getRankedSearchResultList();
    		if (start > l.size())
    			start = l.size() - limit - 1;
    		for (int i=start; i<start + limit; i++) {
    			if (i >= l.size())
    				break;
    			result.add(l.get(i));
    		}
    		result.setNumberOfHits(l.getNumberOfHits());
    	} else {
    		SearchResultList l = SearchResultListMockup.getUnrankedSearchResultList();
    		if (start > l.size())
    			start = l.size() - limit - 1;
    		for (int i=start; i<start + limit; i++) {
    			if (i >= l.size())
    				break;
    			result.add(l.get(i));
    		}
    		result.setNumberOfHits(l.getNumberOfHits());
    	}
    	return result;
    }
    
    private PageState initPageState(PageState ps) {
		ps.setBoolean("isActionProcessed", false);
		ps.put("query", null);
		ps.setBoolean("isSimilarOpen", false);
		ps.put("selectedDS", "1");
		ps.put("similarRoot", null);
		ps.putInt("rankedNavStart", 0);
		ps.putInt("rankedNavLimit", 1);
		ps.putInt("unrankedNavStart", 0);
		ps.putInt("unrankedNavLimit", 1);
		return ps;
    }
    
}