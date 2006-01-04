package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.SearchResultPageState;
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
    	SearchResultPageState ps = (SearchResultPageState) session.getAttribute("page_state");
    	if (ps == null) {
    		ps = new SearchResultPageState();
    		session.setAttribute("page_state", ps);
    	}
    	
    	// initialization if no action has been processed before
    	if (!ps.isActionProcessed()) {
    		ps.setQuery(null);
    		ps.setSimilarOpen(false);
    		ps.setSelectedDS("1");
    		ps.setSimilarRoot(null);
    		ps.setRankedNavStart(0);
    		ps.setRankedNavLimit(1);
    		ps.setUnrankedNavStart(0);
    		ps.setUnrankedNavLimit(1);
    	}
    	ps.setActionProcessed(false);

    	String q = request.getParameter("q");
    	if (q != null && q.length() > 0) {
			ps.setQuery(q);
    	}
    	if (ps.getQuery() != null) {
    		SearchResultList rankedSRL = doSearch(ps.getQuery(), ps.getSelectedDS(), ps.getRankedNavStart(), ps.getRankedNavLimit(), true);
    		SearchResultList unrankedSRL = doSearch(ps.getQuery(), ps.getSelectedDS(), ps.getUnrankedNavStart(), ps.getUnrankedNavLimit(), false);
        	ps.setRankedCurrentPage(ps.getRankedNavStart() / ps.getRankedNavLimit() + 1);
    		ps.setRankedNumberOfPages(rankedSRL.getNumberOfHits() / ps.getRankedNavLimit() + (int)Math.ceil(rankedSRL.getNumberOfHits() % ps.getRankedNavLimit()));
        	ps.setUnrankedCurrentPage(ps.getUnrankedNavStart() / ps.getUnrankedNavLimit() + 1);
    		ps.setUnrankedNumberOfPages(unrankedSRL.getNumberOfHits() / ps.getUnrankedNavLimit() + (int)Math.ceil(unrankedSRL.getNumberOfHits() % ps.getUnrankedNavLimit()));
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
    	SearchResultPageState ps = (SearchResultPageState) session.getAttribute("page_state");
    	if (ps == null) {
    		ps = new SearchResultPageState();
    		session.setAttribute("page_state", ps);
    	}
    	
        if (action == null) {
        	action = "";
        } else if (action.equalsIgnoreCase("doSearch")) {
            ps.setQuery(null);
            String q = request.getParameter("q");
        	if (q != null && q.length() > 0) {
                ps.setQuery(q);
    	    }
        	ps.setSimilarOpen(false);
        	ps.setSimilarRoot(null);
        	ps.setRankedNavStart(0);
        	ps.setUnrankedNavStart(0);
        } else if (action.equalsIgnoreCase("doChangeDS")) {
        	String ds = request.getParameter("ds");
        	ps.setSelectedDS(ds);
        } else if (action.equalsIgnoreCase("doOpenSimilar")) {
        	ps.setSimilarOpen(true);
        	ps.setSimilarRoot(SimilarNodeFactoryMockup.getSimilarNodes());
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
        	ps.setSimilarOpen(false);
        	ps.setSimilarRoot(null);
        } else if (action.equalsIgnoreCase("doOpenNode")) {
        	SimilarNodeFactoryMockup.setOpen(ps.getSimilarRoot(), request.getParameter("nodeId"), true);
        } else if (action.equalsIgnoreCase("doCloseNode")) {
        	SimilarNodeFactoryMockup.setOpen(ps.getSimilarRoot(), request.getParameter("nodeId"), false);
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
        	// TODO
        }
    	try {
    		ps.setRankedNavStart(Integer.parseInt(request.getParameter("rstart")));
    	} catch (NumberFormatException e) {
    		//ps.setRankedNavStart(0);
    	}
    	try {
    		ps.setUnrankedNavStart(Integer.parseInt(request.getParameter("nrstart")));
    	} catch (NumberFormatException e) {
    		//ps.setUnrankedNavStart(0);
    	}
        
    	ps.setActionProcessed(true);
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
    
}