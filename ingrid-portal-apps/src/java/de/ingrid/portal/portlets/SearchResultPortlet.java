package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.ibus.Bus;
import de.ingrid.ibus.DummyProxyFactory;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.SimilarTreeNode;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.portal.search.mockup.SimilarNodeFactoryMockup;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Please add comments!
 *
 * created 11.01.2006
 *
 * @author joachim
 * @version
 * 
 */
public class SearchResultPortlet extends GenericVelocityPortlet
{

    private int rankedHitsPerPage = 10;
    private int unrankedHitsPerPage = 10;
    
    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        
    } 
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
    	PortletSession session = request.getPortletSession();
    	String selectedDS = (String) session.getAttribute("selectedDS");
    	if (selectedDS == null || selectedDS.length() == 0) {
    	    selectedDS = "1";
    	}
    	context.put("ds", selectedDS);
    	
    	PageState ps = (PageState) session.getAttribute("portlet_state");
    	if (ps == null) {
    		ps = new PageState(this.getClass().getName());
    		ps = initPageState(ps);
    		session.setAttribute("portlet_state", ps);
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
    		
    	    int currentSelectorPage;
    	    int numberOfPages;
    	    int numberOfSelectorPages;
    	    int firstSelectorPage;
    	    int lastSelectorPage;
    	    boolean selectorHasPreviousPage;
    	    boolean selectorHasNextPage;
    	    int numberOfFirstHit;
    	    int numberOfLastHit;
    	    int numberOfHits;
    	    
        	// ranked results	
    	    SearchResultList rankedSRL = doSearch(ps.getString("query"), ps.getString("selectedDS"), ps.getInt("rankedNavStart"), rankedHitsPerPage, true);
        	numberOfHits = rankedSRL.getNumberOfHits();
    	    
    	    currentSelectorPage = ps.getInt("rankedNavStart") / rankedHitsPerPage + 1;
        	numberOfPages = numberOfHits / rankedHitsPerPage + (int)Math.ceil(numberOfHits % rankedHitsPerPage);
        	numberOfSelectorPages = 5;
        	firstSelectorPage = 1;
        	selectorHasPreviousPage = false;
        	if (currentSelectorPage > numberOfSelectorPages) {
        	    firstSelectorPage = currentSelectorPage - (int)(numberOfSelectorPages/2); 
            	selectorHasPreviousPage = true;
    		}
        	lastSelectorPage = firstSelectorPage + numberOfSelectorPages - 1;
        	selectorHasNextPage = true;
        	if (numberOfPages < lastSelectorPage) {
        	    lastSelectorPage = numberOfPages;
        	    selectorHasNextPage = false;
        	}
        	numberOfFirstHit = (currentSelectorPage - 1) * rankedHitsPerPage + 1;
        	numberOfLastHit = numberOfFirstHit + rankedHitsPerPage - 1;
        	
        	if (numberOfLastHit > numberOfHits) {
        	    numberOfLastHit = numberOfHits;
        	}
        	
        	HashMap pageSelector = new HashMap();
        	pageSelector.put("currentSelectorPage", new Integer(currentSelectorPage));
        	pageSelector.put("numberOfPages", new Integer(numberOfPages));
        	pageSelector.put("numberOfSelectorPages", new Integer(numberOfSelectorPages));
        	pageSelector.put("firstSelectorPage", new Integer(firstSelectorPage));
        	pageSelector.put("lastSelectorPage", new Integer(lastSelectorPage));
        	pageSelector.put("selectorHasPreviousPage", new Boolean(selectorHasPreviousPage));
        	pageSelector.put("selectorHasNextPage", new Boolean(selectorHasNextPage));
        	pageSelector.put("hitsPerPage", new Integer(rankedHitsPerPage));
        	pageSelector.put("numberOfFirstHit", new Integer(numberOfFirstHit));
        	pageSelector.put("numberOfLastHit", new Integer(numberOfLastHit));
        	pageSelector.put("numberOfHits", new Integer(numberOfHits));
        	
        	context.put("rankedPageSelector", pageSelector);
        	
        	// unranked results	
    		SearchResultList unrankedSRL = doSearch(ps.getString("query"), ps.getString("selectedDS"), ps.getInt("unrankedNavStart"), unrankedHitsPerPage, false);
        	numberOfHits = unrankedSRL.getNumberOfHits();
        	
        	currentSelectorPage = ps.getInt("unrankedNavStart") / unrankedHitsPerPage + 1;
        	numberOfPages = numberOfHits / unrankedHitsPerPage + (int)Math.ceil(numberOfHits % unrankedHitsPerPage);
        	numberOfSelectorPages = 3;
        	firstSelectorPage = 1;
        	selectorHasPreviousPage = false;
        	if (currentSelectorPage > numberOfSelectorPages) {
        	    firstSelectorPage = currentSelectorPage - (int)(numberOfSelectorPages/2); 
            	selectorHasPreviousPage = true;
    		}
        	lastSelectorPage = firstSelectorPage + numberOfSelectorPages - 1;
        	selectorHasNextPage = true;
        	if (numberOfPages < lastSelectorPage) {
        	    lastSelectorPage = numberOfPages;
        	    selectorHasNextPage = false;
        	}
        	numberOfFirstHit = (currentSelectorPage - 1) * unrankedHitsPerPage + 1;
        	numberOfLastHit = numberOfFirstHit + unrankedHitsPerPage - 1;
        	
        	if (numberOfLastHit > numberOfHits) {
        	    numberOfLastHit = numberOfHits;
        	}
        	
        	pageSelector = new HashMap();
        	pageSelector.put("currentSelectorPage", new Integer(currentSelectorPage));
        	pageSelector.put("numberOfPages", new Integer(numberOfPages));
        	pageSelector.put("numberOfSelectorPages", new Integer(numberOfSelectorPages));
        	pageSelector.put("firstSelectorPage", new Integer(firstSelectorPage));
        	pageSelector.put("lastSelectorPage", new Integer(lastSelectorPage));
        	pageSelector.put("selectorHasPreviousPage", new Boolean(selectorHasPreviousPage));
        	pageSelector.put("selectorHasNextPage", new Boolean(selectorHasNextPage));
        	pageSelector.put("hitsPerPage", new Integer(rankedHitsPerPage));
        	pageSelector.put("numberOfFirstHit", new Integer(numberOfFirstHit));
        	pageSelector.put("numberOfLastHit", new Integer(numberOfLastHit));
        	pageSelector.put("numberOfHits", new Integer(numberOfHits));
        	
        	context.put("unrankedPageSelector", pageSelector);        	
        	
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
    	PageState ps = (PageState) session.getAttribute("portlet_state");
    	if (ps == null) {
    		ps = new PageState(this.getClass().getName());
    		ps = initPageState(ps);
    		session.setAttribute("portlet_state", ps);
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
        	session.setAttribute("selectedDS", request.getParameter("ds"), PortletSession.APPLICATION_SCOPE);
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
        	ArrayList nodes = getNodeChildrenArray((SimilarTreeNode)ps.get("similarRoot"));
        	Iterator it = nodes.iterator();
        	StringBuffer sbQuery = new StringBuffer(ps.getString("query"));
        	while (it.hasNext()) {
        		SimilarTreeNode node = (SimilarTreeNode)it.next();
        		if (request.getParameter("chk_"+node.getId()) != null && sbQuery.indexOf(node.getName()) == -1) {
        			sbQuery.append(" AND ").append(node.getName());
        		} else if (node.getDepth() == 2) {
        			node.setOpen(false);
        		}
        	}
        	ps.put("query", sbQuery.toString());
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

            Bus bus = new Bus(new DummyProxyFactory());
            PlugDescription[] plugDescriptions = new PlugDescription[3];
            for (int i = 0; i < plugDescriptions.length; i++) {
                plugDescriptions[i] = new PlugDescription();
                plugDescriptions[i].setPlugId("" + i);
                plugDescriptions[i].setOrganisation("Bundesumweltamt");
                plugDescriptions[i].setDataType("webiste");
                bus.getIPlugRegistry().addIPlug(plugDescriptions[i]);
            }
            IngridQuery query;
            IngridHits hits = null;
            IngridDocument[] documents = null;
            try {
                query = QueryStringParser.parse("fische ort:halle");
                hits = bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
                documents = hits.getHits();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (documents != null) {
                int hitsOnPage;
                if (documents.length > limit) {
                    hitsOnPage = limit;
                } else {
                    hitsOnPage = documents.length;
                }
                
                for (int i =0; i< hitsOnPage; i++) {
                    result.add(documents[i]);
                }
                result.setNumberOfHits((int)hits.length());
            } else {
                result.setNumberOfHits(0);
            }
/*            
            SearchResultList l = SearchResultListMockup.getRankedSearchResultList();
            if (start > l.size())
                start = l.size() - limit - 1;
            for (int i=start; i<start + limit; i++) {
                if (i >= l.size())
                    break;
                result.add(l.get(i));
            }
            result.setNumberOfHits(l.getNumberOfHits());
*/            
    	} else {
    		SearchResultList l = SearchResultListMockup.getUnrankedSearchResultList();
    		for (int i=0; i<limit; i++) {
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
		ps.put("similarRoot", null);
		ps.putInt("rankedNavStart", 0);
		ps.putInt("rankedNavLimit", 10);
		ps.putInt("unrankedNavStart", 0);
		ps.putInt("unrankedNavLimit", 10);
		return ps;
    }
    
    private ArrayList getNodeChildrenArray(SimilarTreeNode n) {
    	
    	ArrayList ret = new ArrayList();
    	Iterator it = n.getChildren().iterator();
    	while (it != null && it.hasNext()) {
    		ret.addAll(getNodeChildrenArray((SimilarTreeNode) it.next()));
    	}
		ret.add(n);
    	
		return ret;
    }
    
}