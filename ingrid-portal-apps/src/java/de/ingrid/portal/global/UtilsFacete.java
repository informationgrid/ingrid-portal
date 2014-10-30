/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.codelists.CodeListService;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.FacetsConfig;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtAdrPlaceReferenceForm;
import de.ingrid.portal.forms.SearchExtEnvPlaceGeothesaurusForm;
import de.ingrid.portal.forms.SearchExtResTopicAttributesForm;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.FuzzyFieldQuery;
import de.ingrid.utils.query.FuzzyTermQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.RangeQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.query.WildCardFieldQuery;
import de.ingrid.utils.query.WildCardTermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author ktt@wemove.com
 */


@SuppressWarnings("unchecked")
public class UtilsFacete {

	private final static Logger log = LoggerFactory.getLogger(UtilsFacete.class);

    private static final String GEOTHESAURUS_CURRENT_TOPIC = "facete_current_topic_geothesaurus";
    private static final String GEOTHESAURUS_TOPICS = "facete_topics_geothesaurus";
    private static final String GEOTHESAURUS_SIMILAR_TOPICS = "facete_similar_topics_geothesaurus";
    private static final String GEOTHESAURUS_ERROR = "facete_error_geothesaurus";
    private static final String GEOTHESAURUS_LIST_SIZE = "facete_list_size_geothesaurus";
    private static final String GEOTHESAURUS_DO = "doGeothesaurus";
    private static final String GEOTHESAURUS_SELECTED_TOPICS_IDS = "geothesaurusSelectTopicsId";
    private static final String GEOTHESAURUS_TERM = "geothesaurusTerm";
    private static final String GEOTHESAURUS_SELECTED_TOPICS = "geothesaurusSelectTopics";
    private static final String GEOTHESAURUS_ALL_TOPICS = "allGeoThesaurusTopics";
    
    private static final String ELEMENTS_GEOTHESAURUS = "elementsGeothesaurus";
    private static final String ELEMENTS_MAP = "elementsMap";
    
    private static final String SELECTED_GEOTHESAURUS = "selectedGeothesaurus";
    private static final String SELECTED_MAP = "selectedMap";
    
    private static final String PARAMS_GEOTHESAURUS = "geothesaurus";
    private static final String PARAMS_MAP_X1 = "x1";
    private static final String PARAMS_MAP_X2 = "x2";
    private static final String PARAMS_MAP_Y1 = "y1";
    private static final String PARAMS_MAP_Y2 = "y2";
    private static final String PARAMS_MAP_OPTIONS = "options";
    private static final String PARAMS_AREA_ADDRESS_STREET = "street";
    private static final String PARAMS_AREA_ADDRESS_ZIP = "zip";
    private static final String PARAMS_AREA_ADDRESS_CITY = "city";
    private static final String PARAMS_ATTRIBUTE_TITLE = "db_title";
    private static final String PARAMS_ATTRIBUTE_INSTITUTE = "db_institute";
    private static final String PARAMS_ATTRIBUTE_PM = "db_pm";
    private static final String PARAMS_ATTRIBUTE_STAFF = "db_staff";
    private static final String PARAMS_ATTRIBUTE_ORG = "db_org";
    private static final String PARAMS_ATTRIBUTE_TERM_FROM = "term_from";
    private static final String PARAMS_ATTRIBUTE_TERM_TO = "term_to";
    
    private static final String FACET_CONFIG = "config";
    
    public static final String SESSION_PARAMS_READ_FACET_FROM_SESSION = "readFacetFromSession";
    public static final String SESSION_PARAMS_FACET_GROUPING = "facet_grouping";
    
    private static Set<String> keys = null;
    
    /**
     * Prepare query by facet activity
     * 
     * @param ps
     * @param query
     * @throws ParseException 
     */
    public static void facetePrepareInGridQuery (PortletRequest request, IngridQuery query) throws ParseException{
    	
    	// Check for pre prozess doAction.
     	String portalTerm = request.getParameter("q");
		String facetTerm  = (String) getAttributeFromSession(request, "faceteTerm");
		
		boolean readFacetFromSession = false;
    	if(getAttributeFromSession(request, SESSION_PARAMS_READ_FACET_FROM_SESSION) != null){
    		readFacetFromSession = (Boolean) getAttributeFromSession(request, SESSION_PARAMS_READ_FACET_FROM_SESSION);
    	}
    	if(readFacetFromSession){
    		setAttributeToSession(request, SESSION_PARAMS_READ_FACET_FROM_SESSION, false);
    	}else{
    		if(request.getParameter("f") != null){
    			getFacetAttributsParamsFromUrl(request);
    		}else{
    			if(request.getParameter("js_ranked") == null){
    				String action = request.getParameter("action"); 
    				if(facetTerm != null && portalTerm != null && action != null){
	    				if((portalTerm.equals(facetTerm) && !action.equals("doSearch")) 
	    						|| (portalTerm.equals(facetTerm) && action == null)){
	    					removeAllFaceteSelections(request);
	    					removeFaceteElementsFromSession(request);
	    				}
    				}
    			}
    		}
    	}
 		
    	if(portalTerm != null){
			setAttributeToSession(request, "faceteTerm", portalTerm);
		}
    	
    	// Create config object
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
        if(config == null){
			config = FacetsConfig.getFacets();
			addDefaultIngridFacets(request, config);
			setAttributeToSession(request, FACET_CONFIG, config);
		}else{
			if(portalTerm != null && facetTerm != null){
				if(!portalTerm.equals(facetTerm)){
					//Reset config facet values
					resetFacetConfigValues(config, null);
					setAttributeToSession(request, FACET_CONFIG, config);
				}
			}
		}
        
        // Get all existing selection keys
      	if(keys == null){
      		keys = getExistingSelectionKeys(request);
      	}
      		
        // Set selection to query
 		setFacetQuery(portalTerm, config, request, query);
 		
 		addToQueryMap(request, query);
        addToQueryGeothesaurus(request, query);
        addToQueryAttribute(request, query);
        addToQueryAreaAddress(request, query);
        
        // Get facet query from config file.
        if(query.get("FACETS") == null){
        	ArrayList<IngridDocument> facetQueries = new ArrayList<IngridDocument>();
        	getConfigFacetQuery(config, facetQueries, true, null);
        	if(facetQueries != null){
        		query.put("FACETS", facetQueries);
    	        setAttributeToSession(request, "FACETS_QUERY", facetQueries);
        	}
        }
        
        setAttributeToSession(request, "SEARCH_QUERY", query);
        if(log.isDebugEnabled()){
        	log.debug("Query Facete: " + query);
        }
    }
    
    /**
	 * Set facet to context
	 * 
	 * @param request
	 * @param context
	 * @param facete
	 */
	public static void setParamsToContext(RenderRequest request, Context context, boolean deleteFacets) {
		
		getFacetAttributsParamsFromUrl(request);

		setParamsToContextMap(request, context);
        setParamsToContextGeothesaurus(request, context);
        setParamsToContextAttribute(request, context);
        setParamsToContextAreaAddress(request, context);
        
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
        sortingFacet(config);
		context.put("facetConfig", config);
        context.put("facetKeys", keys);
        
        context.put("facetsQuery", getAttributeFromSession(request, "FACETS_QUERY"));
        context.put("searchQuery", getAttributeFromSession(request, "SEARCH_QUERY"));
        context.put("enableFacetSelection", isFacetSelection(request));
        // Set flag to check if facet component is select
        if(getAttributeFromSession(request, "isSelection") == null){
        	context.put("isSelection", isAnyFacetConfigSelect(config, false));
        }else{
        	context.put("isSelection", isAnyFacetConfigSelect(config, (Boolean) getAttributeFromSession(request, "isSelection")));
        }
        // Remove flag
        removeAttributeFromSession(request, "isSelection");
	}

	/**
	 * Set action parameters to facet session
	 * 
	 * @param request
	 * @param reponse
	 * @return URL with facet parameters
	 */
	@SuppressWarnings("rawtypes")
	public static String setFaceteParamsToSessionByAction(ActionRequest request, ActionResponse reponse) {
		StringBuffer facetUrl = new StringBuffer("&f=");

		general(request);
		ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
		String doRemove = request.getParameter("doRemove");
		if(doRemove != null){
			String[] split = doRemove.split(":");
			IngridFacet tmpFacetKey = getFacetById(config, split[0]);
			if(tmpFacetKey != null){
				if(tmpFacetKey.getFacets() != null){
					IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), split[1]);
					if(tmpFacetValue != null){
						if(tmpFacetValue.getFacets() != null){
							resetFacetConfigSelect(tmpFacetValue.getFacets());
						}
					}
				}
			}
		}else{
			// Get all existing selection keys
			if(keys == null){
				keys = getExistingSelectionKeys(request);
			}
			Set set = new TreeSet(request.getParameterMap().keySet());
			if(config != null){
				HashMap<String, String> lastSelection = null;
				boolean facetIsSelect = false; 
				for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = request.getParameter(key);
					
					if(key.startsWith("addFromDialog_")){
						// Reset dialog facets
						String dialogId = key.replace("addFromDialog_", "");
						IngridFacet tmpFacet = getFacetById(config, dialogId);
						if(tmpFacet.getFacets() != null){
							for(IngridFacet tmpSubFacet : tmpFacet.getFacets()){
								tmpSubFacet.setSelect(false);
							}
						}
					}else if (key.indexOf("_box_") > 0){
						// Dialog checkbox selection
						String[] split = key.split("_box_");
						if(split.length > 1){
							resetFacetConfigValues(config, split[1]);
							IngridFacet tmpFacetKey = getFacetById(config, split[1]);
							if(tmpFacetKey != null){
								IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), value);
								if(tmpFacetValue != null){
									if(tmpFacetValue.isSelect()){
										tmpFacetValue.setSelect(false);
										if(tmpFacetValue.getFacets() != null){
											for(IngridFacet tmpSubFacet : tmpFacetValue.getFacets()){
												tmpSubFacet.setSelect(false);
											}
										}
									}else{
										tmpFacetValue.setSelect(true);
										facetIsSelect = true;
										// Set last selection
										if(lastSelection == null){
											lastSelection = new HashMap<String, String>();
										}
										lastSelection.put(tmpFacetKey.getId() + ":" + tmpFacetValue.getId(), tmpFacetValue.getId());
									}
								}
							}
						}
					}else{
						// Set facet selection
						if(value != null){
							resetFacetConfigValues(config, null);
							IngridFacet tmpFacetKey = getFacetById(config, key);
							if(tmpFacetKey != null){
								IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), value);
								if(tmpFacetValue != null){
									if(tmpFacetValue.isSelect()){
										tmpFacetValue.setSelect(false);
										if(tmpFacetValue.getFacets() != null){
											for(IngridFacet tmpSubFacet : tmpFacetValue.getFacets()){
												tmpSubFacet.setSelect(false);
											}
										}
									}else{
										tmpFacetValue.setSelect(true);
										facetIsSelect = true;
										// Set last selection
										if(lastSelection == null){
											lastSelection = new HashMap<String, String>();
										}
										lastSelection.put(tmpFacetKey.getId() + ":" + tmpFacetValue.getId(), tmpFacetValue.getId());
									}
									if(isFacetConfigSelect(tmpFacetKey.getFacets())){
										tmpFacetKey.setSelect(facetIsSelect);	
									}
									resetFacetConfigValues(config, null);
								}
							}
						}
					}
					// Set dependency selection
					boolean isOldIPlug = false;
					ArrayList<IngridFacet> facetDepList = new ArrayList<IngridFacet>();
					IngridFacet tmpFacetKey = getFacetById(config, key);
					if(tmpFacetKey != null){
						IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), value);
						if(tmpFacetValue != null){
							if(tmpFacetValue.isOldIPlug()){
								isOldIPlug = true;
							}
						}
					}
					
					if(!isOldIPlug){
						getDependencyFacetById(config, facetDepList, value);
						for(IngridFacet facetDep : facetDepList){
							if(facetIsSelect){
								facetDep.setDependencySelect(true);
							}else{
								facetDep.setDependencySelect(false);
							}
						}
					}
					
					// Set hidden selection
					ArrayList<IngridFacet> facetHidList = new ArrayList<IngridFacet>();
					getHiddenFacetById(config, facetHidList, value);
					for(IngridFacet facetHid : facetHidList){
						if(facetIsSelect){
							facetHid.setHiddenSelect(true);
						}else{
							facetHid.setHiddenSelect(false);
						}
					}
				}
				// Set last selection
				if(lastSelection != null){
					setAttributeToSession(request, "lastConfigSelection", lastSelection, true);
				}
				// Set flag for selection
				setAttributeToSession(request, "isSelection", facetIsSelect);
			}
		}
		setAttributeToSession(request, FACET_CONFIG, config);
        setFaceteParamsToSessionMap(request);
        setFaceteParamsToSessionGeothesaurus(request);
        setFaceteParamsToSessionAttribute(request);
        setFaceteParamsToSessionAreaAddress(request);
        
        // Create facet params URL
        setFacetUrlParamsToUrl(request, facetUrl);
        
        String sFacetUrl = facetUrl.toString();
        if(sFacetUrl.equals("&f=") == false){
        	return facetUrl.toString();	
        }
        return "";
	}

	/**
	 * Check getting facets for categories 
	 * 
	 * @param facetes
	 * @param request
	 */
	public static void checkForExistingFacete(IngridHitsWrapper hits, PortletRequest request) {
		HashMap<String, Long> elementsGeothesaurus = null;
		HashMap<String, Long> elementsMap = null;
		IngridDocument facets = (IngridDocument) hits.get("FACETS");
		if(facets != null){
			ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
			for (Iterator<String> iterator = facets.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				Long value = (Long) facets.get(key);
				
				if(value > 0){
					// Default facet
					if(key.startsWith("geothesaurus:")){
						if(elementsGeothesaurus == null){
							elementsGeothesaurus = new HashMap<String, Long>();
						}
						elementsGeothesaurus.put(key.replace("geothesaurus:", ""), value);
	
					}else if(key.startsWith("coords:")){
						if(elementsMap == null){
							elementsMap = new HashMap<String, Long>();
						}
						elementsMap.put(key.replace("coords:", ""), value);
	
					}else{
						// Generic facet by config
						for (Iterator<String> iteratorKeys = keys.iterator(); iteratorKeys.hasNext();) {
							String facetKey = iteratorKeys.next();
							if(key.startsWith("partner")){
								IngridFacet ingridFacet = getFacetById(config, key.replace("partner:", ""));
								if(ingridFacet != null){
									ingridFacet.setFacetValue(value.toString());
								}
							}else if(key.startsWith(facetKey)){
								if(config != null){
									IngridFacet ingridFacet = getFacetById(config, facetKey);
									if(ingridFacet != null){
										if(ingridFacet.getFacets() != null){
											String facetSubkey = key.replace(facetKey + ":", "");
											String queryType = ingridFacet.getQueryType();
											IngridFacet facet = getFacetById(ingridFacet.getFacets(), facetSubkey);
											if(facet != null){
												if(queryType == null){
													facet.setFacetValue(value.toString());
												}else if(queryType.equals("OR") || queryType.equals("OR_DIALOG")){
													if(facet.getFacetValue() == null){
														facet.setFacetValue(value.toString());
													}
												}
											}
										}
									}
								}
							}
						}
						setAttributeToSession(request, FACET_CONFIG, config);
					}
				}
			}		
			
			if (elementsMap != null){
				setAttributeToSession(request, ELEMENTS_MAP, sortHashMapAsArrayList(elementsMap));
			} else{
				removeAttributeFromSession(request, ELEMENTS_MAP);
			}
		}
		// Add facets from older iplugs without facets values
		checkNonFacetsIplugs(hits.getHits(), request);
	}

	/***************************** KARTE ***********************************************/
	
	@SuppressWarnings("rawtypes")
	private static void setFaceteParamsToSessionMap(ActionRequest request) {
		
		String doAddMap = request.getParameter("doAddMap");
		String doRemoveMap = request.getParameter("doRemoveMap");
		HashMap<String, String> doMapCoords = null;
		HashMap<String, String> webmapclientCoords = null;
		ArrayList<String> coordOptions = null;
		HashMap selectedMap = null;
		
		if(doAddMap != null){
			if(!request.getParameter("areaid").equals("")){
				if(selectedMap == null)
				selectedMap = new HashMap();
					doMapCoords = new HashMap<String, String>();
					doMapCoords.put("inside", "AGS: "+request.getParameter("areaid"));
					selectedMap.put("areaid", request.getParameter("areaid"));
			}else{
				coordOptions = new ArrayList<String>();
				webmapclientCoords = new HashMap<String, String>();
				
	        	if(request.getParameter("chk_1") != null){
	        		coordOptions.add(request.getParameter("chk_1"));
	        	}
	        	if(request.getParameter("chk_2") != null){
	        		coordOptions.add(request.getParameter("chk_2"));
	        	}
	        	if(request.getParameter("chk_3") != null){
	        		coordOptions.add(request.getParameter("chk_3"));
	        	}
	        	if(request.getParameter("x1") != null){
	        		webmapclientCoords.put("x1", request.getParameter("x1"));
	        	}
	        	if(request.getParameter("x2") != null){
	        		webmapclientCoords.put("x2", request.getParameter("x2"));	
	        	}                
	        	if(request.getParameter("y1") != null){
	        		webmapclientCoords.put("y1", request.getParameter("y1"));                    		
	        	}
	        	if(request.getParameter("y2") != null){
	        		webmapclientCoords.put("y2", request.getParameter("y2"));
	        	} 
				if(coordOptions != null && coordOptions.size() > 0){
					doMapCoords = new HashMap<String, String>();
	        		for(int i=0; i < coordOptions.size(); i++){
	        			String searchTerm = "";
	        			//TODO implement areaid search
	//	                    if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_BBOX) {
	                    	if(request.getParameter("x1") != null){
	                    		searchTerm = webmapclientCoords.get("x1").concat("' O / ");
	                    	}
	                    	if(request.getParameter("y1") != null){
	                    		searchTerm = searchTerm.concat(webmapclientCoords.get("y1")).concat("' N");	
	                    	}                
	                        searchTerm = searchTerm.concat("<br>");
	                    	if(request.getParameter("x2") != null){
	                    		searchTerm = searchTerm.concat(webmapclientCoords.get("x2")).concat("' O / ");                    		
	                    	}
	                    	if(request.getParameter("y2") != null){
	                    		searchTerm = searchTerm.concat(webmapclientCoords.get("y2")).concat("' N");
	                    	} 
	                        
	                        searchTerm = searchTerm.concat("<br>" +  coordOptions.get(i));
	//	                    } else if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE) {
	//	                        searchTerm = searchTerm.concat("areaid:").concat(wmsDescriptor.getCommunityCode());
	//	                    }
	                    doMapCoords.put(coordOptions.get(i), searchTerm);
	        		}
				}
			}

			if(doMapCoords != null){
				if(selectedMap == null){
					selectedMap = new HashMap();
				}
				selectedMap.put("doMapCoords", doMapCoords);
				//setAttributeToSession(request, "doMapCoords", doMapCoords, true);
			}
			if(coordOptions != null){
				if(selectedMap == null){
					selectedMap = new HashMap();
				}
				selectedMap.put("coordOptions", coordOptions);
				//setAttributeToSession(request, "coordOptions", coordOptions);
			}
			if(webmapclientCoords != null){
				if(selectedMap == null){
					selectedMap = new HashMap();
				}
				selectedMap.put("webmapclientCoords", webmapclientCoords);
				//setAttributeToSession(request, "webmapclientCoords", webmapclientCoords);
			}
		}
		if(selectedMap != null){
			setAttributeToSession(request, SELECTED_MAP, selectedMap, true);
		}
		if(doRemoveMap != null){
			if(doRemoveMap.equals("all")){
				removeAttributeFromSession(request, SELECTED_MAP);
			}else{
				selectedMap = (HashMap<String, String>) getAttributeFromSession(request, SELECTED_MAP);
				if(selectedMap != null){
					doMapCoords = (HashMap<String, String>) selectedMap.get("doMapCoords");
					doMapCoords.remove(doRemoveMap);
					webmapclientCoords = (HashMap<String, String>) selectedMap.get("webmapclientCoords");
					coordOptions = (ArrayList<String>) selectedMap.get("coordOptions");
					
		        	if(coordOptions != null){
		        		for(int i=0; i < coordOptions.size(); i++){
		        			if(coordOptions.get(i).equals(doRemoveMap)){
		        				coordOptions.remove(i);
		        			}
		        		}
		        	}else{
		        		coordOptions = new ArrayList<String>();
		        	}
				}
				
				if(coordOptions != null && coordOptions.size() > 0){
					if(doMapCoords != null || webmapclientCoords != null){
						if(selectedMap == null){
							selectedMap = new HashMap();
						}
					}
					if(doMapCoords != null){
						selectedMap.put("doMapCoords", doMapCoords);
					}
					if(coordOptions != null){
						selectedMap.put("coordOptions", coordOptions);
					}
					if(webmapclientCoords != null){
						selectedMap.put("webmapclientCoords", webmapclientCoords);
					}
				}
				
				if(selectedMap != null && coordOptions != null && coordOptions.size() > 0){
					setAttributeToSession(request, SELECTED_MAP, selectedMap, true);
				}else{
					selectedMap.remove("webmapclientCoords");
					selectedMap.remove("areaid");
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void setParamsToContextMap (RenderRequest request, Context context){
		
		HashMap selectedMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);

		if(selectedMap != null && selectedMap.size() > 0){
			setFacetSelectionState(context, request, "isMapSelect", true);
        	context.put("selectedMap", selectedMap);
        	context.put("elementsMap", getAttributeFromSession(request, ELEMENTS_MAP));
        }else{
        	setFacetSelectionState(context, request, "isMapSelect", false);
        }
        context.put("webmapDebugMode", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_WEBMAPCLIENT_DEBUG, false));
	}
	
	@SuppressWarnings("rawtypes")
	private static void addToQueryMap(PortletRequest request, IngridQuery query) {
		
		HashMap selectedMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);
		if(selectedMap != null){
			HashMap<String, String> webmapclientCoords = (HashMap<String, String>) selectedMap.get("webmapclientCoords");
			if (webmapclientCoords != null && webmapclientCoords.size() > 0){
				ArrayList<String> coordOptions = (ArrayList<String>) selectedMap.get("coordOptions");
		    	if(coordOptions != null && coordOptions.size() > 0){
		    		ClauseQuery cq = new ClauseQuery(true, false);
		    		if(coordOptions.size() == 1) {
						cq.addField(new FieldQuery(true, false, "x1", webmapclientCoords.get("x1")));
			    		cq.addField(new FieldQuery(true, false, "y1", webmapclientCoords.get("y1")));
			    		cq.addField(new FieldQuery(true, false, "x2", webmapclientCoords.get("x2")));
			    		cq.addField(new FieldQuery(true, false, "y2", webmapclientCoords.get("y2")));
			    		cq.addField(new FieldQuery(true, false, "coord", coordOptions.get(0)));
			    		query.addClause(cq);
		    		}else{
		    			for(int i=0; i<coordOptions.size();i++){
		    				ClauseQuery coordQuery = new ClauseQuery(false, false);
		    				coordQuery.addField(new FieldQuery(true, false, "x1", webmapclientCoords.get("x1")));
		    				coordQuery.addField(new FieldQuery(true, false, "y1", webmapclientCoords.get("y1")));
		    				coordQuery.addField(new FieldQuery(true, false, "x2", webmapclientCoords.get("x2")));
		    				coordQuery.addField(new FieldQuery(true, false, "y2", webmapclientCoords.get("y2")));
		    				coordQuery.addField(new FieldQuery(true, false, "coord", coordOptions.get(i)));
				    		cq.addClause(coordQuery);
		    			}
		    			query.addClause(cq);
					}
		    	}
		    }else if(selectedMap.get("areaid") != null){
		    	ClauseQuery cq = new ClauseQuery(true, false);
		    	cq.addField(new FieldQuery(true, false, "areaid", (String)selectedMap.get("areaid")));
		    	query.addClause(cq);
		    }
		}
	}
	
	/***************************** GEOTHESAURUS ****************************************/
	
	/**
	 * Action Params for "Search Geothesaurus
	 * 
	 * @param request
	 */
	
	@SuppressWarnings("rawtypes")
	private static void setFaceteParamsToSessionGeothesaurus(ActionRequest request) {
		String doGeothesaurus = request.getParameter("doGeothesaurus");
		String doCancelGeothesaurus = request.getParameter("doCancelGeothesaurus");
		String doAddGeothesaurus = request.getParameter("doAddGeothesaurus");
		String doSearchGeothesaurus = request.getParameter("doSearchGeothesaurus");
		String doBrowseGeothesaurus = request.getParameter("doBrowseGeothesaurus");
		String doBrowseSimilarGeothesaurus = request.getParameter("doBrowseSimilarGeothesaurus");
		String doRemoveGeothesaurus = request.getParameter("doRemoveGeothesaurus");

		if(doCancelGeothesaurus != null){
			setAttributeToSession(request, GEOTHESAURUS_DO, false);
			request.removeAttribute("doCancelGeothesaurus");
        }else{
        	HashMap selectedGeothesaurus = null; 
        	
	    	if(doGeothesaurus != null){
				if(doGeothesaurus.equals("true")){
					setAttributeToSession(request, GEOTHESAURUS_DO, true);
				}else{
					setAttributeToSession(request, GEOTHESAURUS_DO, false);
				}
			}
			
			if(doAddGeothesaurus != null){
				setAttributeToSession(request, GEOTHESAURUS_DO, false);
				if(getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE) != null){
					selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
		        	if(selectedGeothesaurus == null){
			    		selectedGeothesaurus = new HashMap();
			    	}
			    	String listSize = getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE).toString();
			    	ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
			        if(selectedIds == null){
			        	selectedIds = new ArrayList<String>();
			        }
			        if(listSize != null){
			        	for (int i=0; i< Integer.parseInt(listSize); i++) {
			        		String chkVal = request.getParameter("chk"+(i+1));
			        		boolean isFound = false;
			                if (chkVal != null) {
			                	for(int j = 0; j < selectedIds.size(); j++){
			                		String selectedId = selectedIds.get(j);
			                		if(selectedId.equals(chkVal)){
			                			isFound = true;
			                			break;
			                		}
			                	}
			                	if(!isFound){
				                	selectedIds.add(chkVal);
				                }
			                }
			            }
			        }
			        if(selectedIds != null){
			        	selectedGeothesaurus.put(GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
			        }
				}
			}
			
			if(doRemoveGeothesaurus != null && doAddGeothesaurus == null){
				if(doRemoveGeothesaurus.equals("all")){
					removeAttributeFromSession(request, SELECTED_GEOTHESAURUS);
				}else{
					selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
					if(selectedGeothesaurus != null){
						ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
						if (selectedIds != null){
							for(int i = 0; i < selectedIds.size(); i++){
								if(selectedIds.get(i).equals(doRemoveGeothesaurus)){
									selectedIds.remove(i);
								}
							}
							selectedGeothesaurus.put(GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
							setAttributeToSession(request, SELECTED_GEOTHESAURUS, selectedGeothesaurus, true);
						}	
					}
				}
				setAttributeToSession(request, GEOTHESAURUS_DO, false);
			}
			
			if (doSearchGeothesaurus != null){
				setAttributeToSession(request, GEOTHESAURUS_DO, true);
				removeAttributeFromSession(request, GEOTHESAURUS_TOPICS);
				removeAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS);
				removeAttributeFromSession(request, GEOTHESAURUS_CURRENT_TOPIC);
				
				String geothesaurusTerm = null;
				SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);        
	            f.clearErrors();
	            f.populate(request);
	            if (f.validate()) {
	            	geothesaurusTerm = f.getInput(SearchExtEnvPlaceGeothesaurusForm.FIELD_SEARCH_TERM);
	            	if(geothesaurusTerm != null){
	            		setAttributeToSession(request, GEOTHESAURUS_TERM, geothesaurusTerm);
	            	}
	            }
	            
	            if(geothesaurusTerm != null){
	            	IngridHit[] topics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(geothesaurusTerm, "/location", request.getLocale());
	                if (topics != null && topics.length > 0) {
	                	for (int i=0; i<topics.length; i++) {
	                		String href = UtilsSearch.getDetailValue(topics[i], "href");
	                		if (href != null && href.lastIndexOf("#") != -1) {
	                			topics[i].put("topic_ref", href.substring(href.lastIndexOf("#")+1));
	                		}
	                		addToListOfTopicsGeoThesaurus(topics[i], request);
	                    }
	                	setAttributeToSession(request, GEOTHESAURUS_TOPICS, topics);
	                	setAttributeToSession(request, GEOTHESAURUS_LIST_SIZE, topics.length);
	                	setAttributeToSession(request, GEOTHESAURUS_ERROR, false);
	                }else{
	                	setAttributeToSession(request, GEOTHESAURUS_ERROR, true);
	                }
	            }
	        }
	        
	        if(doBrowseGeothesaurus != null || doBrowseSimilarGeothesaurus != null){
	        	IngridHit[] topics = null;
	            if (doBrowseGeothesaurus != null) {
	                topics = (IngridHit[]) getAttributeFromSession(request, GEOTHESAURUS_TOPICS);
	            } else {
	                doBrowseGeothesaurus = doBrowseSimilarGeothesaurus;
	                if (doBrowseGeothesaurus != null) {
	                    topics = (IngridHit[]) getAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS);
	                	
	                }
	            }
	
	            if (topics != null && topics.length > 0) {
	            	int list_size = 0;
	                for (int i=0; i<topics.length; i++) {
	                	String tid = UtilsSearch.getDetailValue(topics[i], "topicID");
	                    if (tid != null && tid.equals(doBrowseGeothesaurus)) {
	                    	list_size = list_size + 1;
	                        setAttributeToSession(request, GEOTHESAURUS_CURRENT_TOPIC, topics[i]);
	                        IngridHit[] similarTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicSimilarLocationsFromTopic(doBrowseGeothesaurus, request.getLocale());
	                        if (similarTopics == null) {
	                            SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);
	                            f.setError("", "searchExtEnvPlaceGeothesaurus.error.no_term_found");
	                            break;
	                        }
	                        for (int j=0; j<similarTopics.length; j++) {
	                        	list_size = list_size + 1;
	                        	String href = UtilsSearch.getDetailValue(similarTopics[j], "abstract");
	                            if (href != null && href.lastIndexOf("#") != -1) {
	                                similarTopics[j].put("topic_ref", href.substring(href.lastIndexOf("#")+1));
	                            }
	                            addToListOfTopicsGeoThesaurus(similarTopics[j], request);
	                        }
	                        setAttributeToSession(request, GEOTHESAURUS_SIMILAR_TOPICS, similarTopics);
	                        break;
	                    }
	                }
	                setAttributeToSession(request, GEOTHESAURUS_LIST_SIZE, list_size);
	            }
	        }
	        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request);
	        if(selectedGeothesaurus != null){
	        	selectedGeothesaurus.put(GEOTHESAURUS_SELECTED_TOPICS, geothesaurusSelectTopics);
		        setAttributeToSession(request, SELECTED_GEOTHESAURUS, selectedGeothesaurus, true);
	        }
        }
	}
	
	private static void setParamsToContextGeothesaurus (RenderRequest request, Context context){
		// Nach Raumbezug suchen
        IngridHit[] geothesaurusTopics = (IngridHit []) getAttributeFromSession(request, GEOTHESAURUS_TOPICS);
        context.put("geothesaurusTopics", geothesaurusTopics);
        context.put("geothesaurusTopicsBrowse", getAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS));
        context.put("geothesaurusCurrentTopic", getAttributeFromSession(request, GEOTHESAURUS_CURRENT_TOPIC));
        context.put("list_size", getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE));
        
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request);
        ArrayList<HashMap<String, String>> geothesaurusSelectTopicsSorted = new ArrayList<HashMap<String,String>>();
        ArrayList<HashMap<String, Long>> elementsGeothesaurus = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_GEOTHESAURUS);
        
        if(elementsGeothesaurus != null){
    		for(int i=0; i<elementsGeothesaurus.size(); i++){
    			HashMap<String, Long> geothesaurus = elementsGeothesaurus.get(i);
    			String geothesaurusFacetId = null;
    			for (Iterator<String> iterator = geothesaurus.keySet().iterator(); iterator.hasNext();) {
    				geothesaurusFacetId = iterator.next();
    			}
    			if(geothesaurusFacetId != null){
    				for(int j=0; j<geothesaurusSelectTopics.size();j++){
    					HashMap <String, String> geothesaurusSelectTopic = geothesaurusSelectTopics.get(j);
    					String geothesaurusSelectId = geothesaurusSelectTopic.get("topicId");
    					if(geothesaurusFacetId != null && geothesaurusSelectId != null){
    						if(geothesaurusFacetId.equals(geothesaurusSelectId)){
    							geothesaurusSelectTopicsSorted.add(geothesaurusSelectTopic);
    							geothesaurusSelectTopics.remove(j);
        						break;
        					}	
    					}
    				}
    			}
    		}
    		geothesaurusSelectTopicsSorted.addAll(geothesaurusSelectTopics);
    	}else{
    		geothesaurusSelectTopicsSorted.addAll(geothesaurusSelectTopics);
    	}
        if(geothesaurusSelectTopicsSorted != null && geothesaurusSelectTopicsSorted.size() > 0){
        	
        	context.put("geothesaurusSelectTopics", geothesaurusSelectTopicsSorted);
        	setFacetSelectionState(context, request, "isGeothesaurusSelect", true);
        }else{
        	setFacetSelectionState(context, request, "isGeothesaurusSelect", false);
        }
        if(getAttributeFromSession(request, GEOTHESAURUS_DO) != null){
        	context.put("doGeothesaurus", getAttributeFromSession(request, GEOTHESAURUS_DO));
        }
        context.put("geothesaurusTerm", getAttributeFromSession(request, GEOTHESAURUS_TERM));
        context.put("geothesaurusError", getAttributeFromSession(request, GEOTHESAURUS_ERROR));
        context.put("elementsGeothesaurus", elementsGeothesaurus);
	}

	
	private static void addToQueryGeothesaurus(PortletRequest request, IngridQuery query) {
		ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request);
		if(geothesaurusSelectTopics != null && geothesaurusSelectTopics.size() > 0){
			ClauseQuery cq = null;
			
    		for (int i = 0; i < geothesaurusSelectTopics.size(); i++) {
    			HashMap<String, String> map = (HashMap<String, String>) geothesaurusSelectTopics.get(i);
    			if (map != null) {
    				String topicId = (String) map.get("topicId");
    				switch (i) {
					case 0:
						if(geothesaurusSelectTopics.size() == 1){
		    				if(topicId != null){
		    					query.addField(new FieldQuery(true, false, "areaid", topicId));
			            	}
	    				}else{
	    					if(topicId != null){
	    						if(cq == null){
	        						cq = new ClauseQuery(true, false);
	        					}
	        		   	       	cq.addField(new FieldQuery(true, false, "areaid", topicId));
	    					}
	    				}
						break;
					default:
						cq.setLastQueryUnrequired();
						cq.addField(new FieldQuery(false, false, "areaid", topicId));
						break;
					}
	    		}
    		}
    		if(cq != null){
    			query.addClause(cq);
    		}
	    }
	}
	
	@SuppressWarnings("rawtypes")
	private static ArrayList<HashMap<String, String>> getSelectedGeothesaurusTopics(PortletRequest request){
		HashMap selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
		ArrayList<IngridHit> allGeoThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS);
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = new ArrayList<HashMap<String, String>> ();
        
        if(selectedGeothesaurus != null){
        	ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
        	if(allGeoThesaurusTopics != null &&  selectedIds != null){
    			for(int i = 0; i < selectedIds.size(); i++){
    				for(int j = 0; j < allGeoThesaurusTopics.size(); j++){
    					Topic topic = (Topic) allGeoThesaurusTopics.get(j);
    					String topicId = (String) topic.get("topicID");
            			if(topic.getTopicNativeKey() != null){
            				topicId = topic.getTopicNativeKey();
            			}
            			if(topicId != null){
            				if(topicId.indexOf((String)selectedIds.get(i)) > -1){
                            	HashMap<String, String> map = new HashMap<String, String>();
                    			map.put("topicTitle", topic.get("topicName").toString());
                    			map.put("topicId", (String)selectedIds.get(i));
                    			geothesaurusSelectTopics.add(map);
                    			break;
                    		}
            			}
    				}
    			}
    		}
    	}
        return geothesaurusSelectTopics;
	}
	
	private static void addToListOfTopicsGeoThesaurus(IngridHit ingridHit, PortletRequest request) {
		ArrayList<IngridHit> allGeoThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS); 
		if(allGeoThesaurusTopics == null){
			allGeoThesaurusTopics = new ArrayList<IngridHit>();
		}
		
		boolean isFound = false;
		for(int i=0; i < allGeoThesaurusTopics.size(); i++){
			IngridHit hit = allGeoThesaurusTopics.get(i);
			if(ingridHit.getId()==hit.getId()){
				isFound = true;
				break;
			}
		}
		if(!isFound){
			allGeoThesaurusTopics.add(ingridHit);	
		}
		setAttributeToSession(request, GEOTHESAURUS_ALL_TOPICS, allGeoThesaurusTopics, false);
	}

	/***************************** Attribute ****************************************/
	
	private static void setFaceteParamsToSessionAttribute(ActionRequest request) {
		String doAddAttribute = request.getParameter("doAddAttribute");
		String doRemoveAttribute = request.getParameter("doRemoveAttribute");
		HashMap<String, String>  attribute = null;
		
		if(doAddAttribute != null){
			SearchExtResTopicAttributesForm f = (SearchExtResTopicAttributesForm) Utils.getActionForm(request,
		             SearchExtResTopicAttributesForm.SESSION_KEY, SearchExtResTopicAttributesForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }
            
            attribute = new HashMap<String, String> ();
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_ORG)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_ORG, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_ORG));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_PM)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_PM, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_PM));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_STAFF, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_STAFF));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_TITLE, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_TITLE));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_TERM_FROM)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_FROM, f.getInput(SearchExtResTopicAttributesForm.FIELD_TERM_FROM));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_TERM_TO)) {
            	attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_TO, f.getInput(SearchExtResTopicAttributesForm.FIELD_TERM_TO));
            }
            f.clear();
		}
		
		if(doRemoveAttribute != null){
			if(doRemoveAttribute.equals("all")){
				removeAttributeFromSession(request, "doAddAttribute");
			}else {
				attribute = new HashMap<String, String> ();
				attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");
				
				if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE);
	            }
	            if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_ORG)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_ORG);
	            }
	            if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_PM)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_PM);
	            }
	            if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_STAFF);
	            }
	            if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_TITLE);
	            }
	            if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_TERM_FROM)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_TERM_FROM);
	            }
	            if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_TERM_TO)) {
	            	attribute.remove(SearchExtResTopicAttributesForm.FIELD_TERM_TO);
	            }
			}	
		}
		
		if(attribute != null){
			setAttributeToSession(request, "doAddAttributeInput", attribute, false);
			setAttributeToSession(request, "doAddAttribute", attribute, true);
		}
	}
	
	private static void setParamsToContextAttribute (RenderRequest request, Context context){
		HashMap<String, String>  attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");
		if(attribute != null && attribute.size() > 0 ){
			setFacetSelectionState(context, request, "isAttributeSelect", true);
			context.put("doAddAttribute", getAttributeFromSession(request, "doAddAttribute"));	
		}else{
			setFacetSelectionState(context, request, "isAttributeSelect", false);
		}
		context.put("doAddAttributeInput", getAttributeFromSession(request, "doAddAttributeInput"));
	}
	
	private static void addToQueryAttribute(PortletRequest request, IngridQuery query) {
		HashMap<String, String>  doAddAttribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");
    	
		if (doAddAttribute != null && doAddAttribute.size() > 0){
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE) != null)
    			query.addField(new FieldQuery(false, false, "title", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)));
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE) != null)
	    		query.addField(new FieldQuery(false, false, "fs_institution", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)));
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM) != null)
	    		query.addField(new FieldQuery(false, false, "fs_projectleader", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM)));
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF) != null)
	    		query.addField(new FieldQuery(false, false, "fs_participants", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)));
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG) != null)
	    		query.addField(new FieldQuery(false, false, "fs_project_executing_organisation", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG)));
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM) != null)
	    		query.addField(new FieldQuery(false, false, "fs_runtime_from", UtilsDate.convertDateString((String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM), "dd.MM.yyyy", "yyyy-MM-dd")));
    		if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO) != null)
	    		query.addField(new FieldQuery(false, false, "fs_runtime_to", UtilsDate.convertDateString((String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO), "dd.MM.yyyy", "yyyy-MM-dd")));
	    }
	}

	
	/***************************** Raumbezug - Addressen ****************************************/
	
	private static void setFaceteParamsToSessionAreaAddress(ActionRequest request) {
		String doAddAreaAddress = request.getParameter("doAddAreaAddress");
		String doRemoveAreaAddress = request.getParameter("doRemoveAreaAddress");
		HashMap<String, String> areaAddress = null;
		
		if(doAddAreaAddress != null){
			SearchExtAdrPlaceReferenceForm f = (SearchExtAdrPlaceReferenceForm) Utils.getActionForm(request, SearchExtAdrPlaceReferenceForm.SESSION_KEY, SearchExtAdrPlaceReferenceForm.class);        
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }
            
            areaAddress = new HashMap<String, String>();
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_STREET)) {
                areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_STREET, f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_STREET));
            }
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)) {
                areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_ZIP, f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_ZIP));
            }
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_CITY)) {
                areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_CITY, f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_CITY));
            }
            f.clear();
		}
		
		if(doRemoveAreaAddress != null){
			if(doRemoveAreaAddress.equals("all")){
				removeAttributeFromSession(request, "doAddAreaAddress");
			}else {
				areaAddress = new HashMap<String, String> ();
				areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
				
				if (doRemoveAreaAddress.equals(SearchExtAdrPlaceReferenceForm.FIELD_STREET)) {
	            	areaAddress.remove(SearchExtAdrPlaceReferenceForm.FIELD_STREET);
	            }
	            if (doRemoveAreaAddress.equals(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)) {
	            	areaAddress.remove(SearchExtAdrPlaceReferenceForm.FIELD_ZIP);
	            }
	            if (doRemoveAreaAddress.equals(SearchExtAdrPlaceReferenceForm.FIELD_CITY)) {
	            	areaAddress.remove(SearchExtAdrPlaceReferenceForm.FIELD_CITY);
	            }
			}	
		}
		
		if(areaAddress != null){
			setAttributeToSession(request, "doAddAreaAddress", areaAddress, true);
			setAttributeToSession(request, "doAddAreaAddressInput", areaAddress, false);
		}
	}
	
	private static void setParamsToContextAreaAddress (RenderRequest request, Context context){
		HashMap<String, String> areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
		if(areaAddress != null && areaAddress.size() > 0 ){
			setFacetSelectionState(context, request, "isAreaAddressSelect", true);
			context.put("doAddAreaAddress", getAttributeFromSession(request, "doAddAreaAddress"));	
		}else{
			setFacetSelectionState(context, request, "isAreaAddressSelect", false);
		}
		context.put("doAddAreaAddressInput", getAttributeFromSession(request, "doAddAreaAddressInput"));
	}
	
	private static void addToQueryAreaAddress(PortletRequest request, IngridQuery query) {
		HashMap<String, String> doAddAreaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
    	
		if (doAddAreaAddress != null && doAddAreaAddress.size() > 0){
	    	if(query != null){
	    		if(doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET) != null)
	    			query.addField(new FieldQuery(true, false, "street", (String) doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET)));
	    		if(doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP) != null)
		    		query.addField(new FieldQuery(true, false, "zip", (String) doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)));
	    		if(doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY) != null)
		    		query.addField(new FieldQuery(true, false, "city", (String) doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY)));
	    	}
	    }
	}
	
	
	/***************************** Funktionen ****************************************/
	
	private static void removeAttributeFromSession(PortletRequest request, String key){
		request.getPortletSession().removeAttribute(key, PortletSession.APPLICATION_SCOPE);
	}
	
	public static void setAttributeToSession(PortletRequest request, String key, Object value){
		setAttributeToSession(request, key, value, false);
	}
	
	@SuppressWarnings("rawtypes")
	private static void setAttributeToSession(PortletRequest request, String key, Object value, boolean isSelection){
		Set<String> faceteSessionKeys =
			(Set<String>) getAttributeFromSession(request, "faceteSessionKeys");
		
		if(faceteSessionKeys == null){
			faceteSessionKeys = new HashSet<String>();
			request.getPortletSession().setAttribute("faceteSessionKeys", faceteSessionKeys, PortletSession.APPLICATION_SCOPE);
		}

		// remember all our set keys
		faceteSessionKeys.add(key);
		// and set in session
		request.getPortletSession().setAttribute(key, value, PortletSession.APPLICATION_SCOPE);

		// remember last facet selection in special way
		if(isSelection){
			ArrayList<HashMap> faceteLastSelection =
				(ArrayList<HashMap>) getAttributeFromSession(request, "faceteLastSelection");
			if(faceteLastSelection == null){
				faceteLastSelection = new ArrayList<HashMap>();
				setAttributeToSession(request, "faceteLastSelection", faceteLastSelection);
			}
			
			HashMap lastSelection = new HashMap();
			lastSelection.put(key, value);
			faceteLastSelection.add(lastSelection);
			// Set selection flag of facet to session 
			setAttributeToSession(request, "isSelection", true);
		}
	}
	
	private static Object getAttributeFromSession(PortletRequest request, String key){
		return request.getPortletSession().getAttribute(key, PortletSession.APPLICATION_SCOPE);
	}
	
	private static void general(ActionRequest request) {
		
		String doRemoveAll = request.getParameter("doRemoveAll");
		String doRemoveLast = request.getParameter("doRemoveLast");
		if(doRemoveAll != null){
			removeAllFaceteSelections(request);
			removeFaceteElementsFromSession(request);
		}
		if(doRemoveLast != null){
			removeLastFaceteSelection(request);
		}
		setAttributeToSession(request, SESSION_PARAMS_READ_FACET_FROM_SESSION, true);
	}
	
	private static void removeAllFaceteSelections(PortletRequest request){
		Set<String> faceteSessionKeys = 
			(Set<String>) getAttributeFromSession(request, "faceteSessionKeys");
		if (faceteSessionKeys != null) {
			for (String sessionKey : faceteSessionKeys) {
				removeAttributeFromSession(request, sessionKey);
			}
		}
		removeAttributeFromSession(request, "faceteSessionKeys");
		removeFaceteElementsFromSession(request);
	}
	
	@SuppressWarnings("rawtypes")
	private static void removeLastFaceteSelection(ActionRequest request) {

		ArrayList<HashMap>  faceteLastSelection =
			(ArrayList<HashMap>) getAttributeFromSession(request, "faceteLastSelection");
		if(faceteLastSelection != null){
			HashMap lastSelection = faceteLastSelection.get(faceteLastSelection.size() - 1);
			if(lastSelection != null){
				for (Iterator<String> iterator = lastSelection.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					removeAttributeFromSession(request, key);
					if(key.equals("lastConfigSelection")){
						HashMap<String, String> facets = (HashMap<String, String>) lastSelection.get(key);
						if(facets!= null){
							ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
							if(config != null){
								for (Iterator<String> facetsIterator = facets.keySet().iterator(); facetsIterator.hasNext();) {
									String facetsKey = facetsIterator.next();
									String[] split = facetsKey.split(":");
									IngridFacet tmpFacetKey = getFacetById(config, split[0]);
									if(tmpFacetKey != null){
										IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), split[1]);
										if(tmpFacetValue != null){
											if(tmpFacetValue.isSelect()){
												tmpFacetValue.setSelect(false);
											}
										}
									}
								}
								setAttributeToSession(request, FACET_CONFIG, config);
							}
						}
					}
				}
			}
			faceteLastSelection.remove(faceteLastSelection.size() - 1);
		}
	}
	
	private static void removeFaceteElementsFromSession(PortletRequest request){
		
		removeAttributeFromSession(request, ELEMENTS_GEOTHESAURUS);
		removeAttributeFromSession(request, ELEMENTS_MAP);
	}
	
	private static ArrayList<HashMap<String, Long>> sortHashMapAsArrayList(HashMap<String, Long> input){
		return sortHashMapAsArrayList(input, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static ArrayList<HashMap<String, Long>> sortHashMapAsArrayList(HashMap<String, Long> input, String[] sortedRanking){
		List<String> keys = new ArrayList<String>(input.keySet());
		final Map<String, Long> tmpInput = input;
		ArrayList<HashMap<String, Long>> sortedInput = new ArrayList<HashMap<String, Long>>();
        
		if(sortedRanking != null && sortedRanking.length > 0){
			if(sortedRanking[0].length() > 0){
				for(int i=0; i < sortedRanking.length; i++){
					String key = sortedRanking[i];
					Long value = input.get(key);
					if(key != null && value != null){
						HashMap<String, Long> map = new HashMap<String, Long>();
						map.put(key, input.get(key));
			            sortedInput.add(map);	
					}
		           	
				}
			}else{
				Collections.sort(keys,new Comparator(){
		            public int compare(Object left, Object right){
		                String leftKey = (String)left;
		                String rightKey = (String)right;
		  
		                Long leftValue = (Long)tmpInput.get(leftKey);
		                Long rightValue = (Long)tmpInput.get(rightKey);
		                return leftValue.compareTo(rightValue) * -1;
		            }
		        });
			
				for(Iterator<String> i=keys.iterator(); i.hasNext();){
		            String k = i.next();
		            HashMap<String, Long> map = new HashMap<String, Long>();
		           	map.put(k, tmpInput.get(k));
		            sortedInput.add(map); 
		        }
			}
		}else{
			Collections.sort(keys,new Comparator(){
	            public int compare(Object left, Object right){
	                String leftKey = (String)left;
	                String rightKey = (String)right;
	  
	                Long leftValue = (Long)tmpInput.get(leftKey);
	                Long rightValue = (Long)tmpInput.get(rightKey);
	                return leftValue.compareTo(rightValue) * -1;
	            }
	        });
		
			for(Iterator<String> i=keys.iterator(); i.hasNext();){
	            String k = i.next();
	            HashMap<String, Long> map = new HashMap<String, Long>();
	           	map.put(k, tmpInput.get(k));
	            sortedInput.add(map); 
	        }
		}
		
		return sortedInput;
	}
	
	private static void setFacetSelectionState(Context context, PortletRequest request, String key, boolean value) {
		context.put(key, value);
		HashMap<String, Boolean> facetSelectionState =
			(HashMap<String, Boolean>) getAttributeFromSession(request, "facetSelectionState");
		if(facetSelectionState == null){
			facetSelectionState = new HashMap<String, Boolean>();
			setAttributeToSession(request, "facetSelectionState", facetSelectionState);
		}
		facetSelectionState.put(key, value);
	}
	
	private static HashMap<String, Boolean> getFacetSelectionState(PortletRequest request){
		HashMap<String, Boolean> facetSelectionState =
			(HashMap<String, Boolean>) getAttributeFromSession(request, "facetSelectionState");
		return facetSelectionState;
	}
	
	private static int isFacetSelection(PortletRequest request){
		HashMap<String, Boolean> facetSelectionState = getFacetSelectionState(request);
		int i=0; 
		if(facetSelectionState != null){
			for (Iterator<String> iterator = facetSelectionState.keySet().iterator(); iterator.hasNext();) {
	    		String key = iterator.next();
	    		if(facetSelectionState.get(key)){
	    			i=i+1;
	    			if(i>1){
	    				break;	
	    			}
	    		}
	    	}	
		}
		return i;
	}
	
	/**
	 * Create facet session by URL parameters
	 * 
	 * @param request
	 */
	@SuppressWarnings("rawtypes")
	private static void getFacetAttributsParamsFromUrl(PortletRequest request){
		
		String paramsFacet = request.getParameter("f");
		
		if(paramsFacet != null){
			// Geothesaurus
			ArrayList<String> paramsGeothesaurus = getFacetParamsList(paramsFacet, PARAMS_GEOTHESAURUS);
			if(paramsGeothesaurus != null){
				HashMap geothesaurusHashMap = new HashMap();
				ArrayList<String> selectedIds = null;
				ArrayList<HashMap<String, String>> geothesaurusSelectTopics = null;
				for(int i=0; i<paramsGeothesaurus.size(); i++){
					if(selectedIds == null){
						selectedIds = new ArrayList<String>();
					}
					if(geothesaurusSelectTopics == null){
						geothesaurusSelectTopics = new ArrayList<HashMap<String,String>>();
					}
					String geothesaurus = paramsGeothesaurus.get(i); 
					String topicId = geothesaurus.split(",")[0];
					String topicName = geothesaurus.split(",")[1];
					selectedIds.add(topicId);
					
					IngridHit[] topics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(topicName, "/location", request.getLocale());
					for(int j=0; j<topics.length; j++){
						Topic topic = (Topic) topics[j];
						addToListOfTopicsGeoThesaurus((IngridHit)topics[j], request);
						if(topicId.equals(topic.getTopicNativeKey())){
							HashMap<String, String> addedTopic = new HashMap<String, String>();
							addedTopic.put("topicId", topic.getTopicNativeKey());
							addedTopic.put("topicTitle", topic.getTopicName());
							geothesaurusSelectTopics.add(addedTopic);
						}
					}
					
				}
				geothesaurusHashMap.put(GEOTHESAURUS_SELECTED_TOPICS, geothesaurusSelectTopics);
		        geothesaurusHashMap.put(GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
				setAttributeToSession(request, SELECTED_GEOTHESAURUS, geothesaurusHashMap);
			}
			
			// AreaAddress
			String paramsAreaAddressStreet = getFacetParam(paramsFacet, PARAMS_AREA_ADDRESS_STREET);
			String paramsAreaAddressZip = getFacetParam(paramsFacet, PARAMS_AREA_ADDRESS_ZIP);
			String paramsAreaAddressCity = getFacetParam(paramsFacet, PARAMS_AREA_ADDRESS_CITY);
			HashMap<String, String> areaAddress = null;
			
			if(paramsAreaAddressStreet != null || paramsAreaAddressZip != null || paramsAreaAddressCity != null){
				areaAddress = new HashMap<String, String>();
				if(paramsAreaAddressStreet != null){
					areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_STREET, paramsAreaAddressStreet);
				}
				if(paramsAreaAddressZip != null){
					areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_ZIP, paramsAreaAddressZip);
				}
				if(paramsAreaAddressCity != null){
					areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_CITY, paramsAreaAddressCity);
				}
				setAttributeToSession(request, "doAddAreaAddress", areaAddress);
			}
			
			// Attribute
			String paramsAttributeInstitute = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_INSTITUTE);
			String paramsAttributeOrg = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_ORG);
			String paramsAttributePm = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_PM);
			String paramsAttributeStaff = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_STAFF);
			String paramsAttributeTitle = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_TITLE);
			String paramsAttributeTermForm = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_TERM_FROM);
			String paramsAttributeTermTo = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_TERM_TO);
			HashMap<String, String> attribute = null;
			
			if(paramsAttributeInstitute != null || paramsAttributeOrg != null || paramsAttributePm != null
					|| paramsAttributeStaff != null || paramsAttributeTitle != null || paramsAttributeTermForm != null 
					|| paramsAttributeTermTo != null){
				attribute = new HashMap<String, String>();
				if(paramsAreaAddressStreet != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE, paramsAttributeInstitute);
				}
				if(paramsAreaAddressZip != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_ORG, paramsAttributeOrg);
				}
				if(paramsAreaAddressCity != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_PM, paramsAttributePm);
				}
				if(paramsAreaAddressStreet != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_STAFF, paramsAttributeStaff);
				}
				if(paramsAreaAddressZip != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_TITLE, paramsAttributeTitle);
				}
				if(paramsAreaAddressCity != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_FROM, paramsAttributeTermForm);
				}
				if(paramsAreaAddressCity != null){
					attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_TO, paramsAttributeTermTo);
				}
				setAttributeToSession(request, "doAddAttribute", attribute);
			}
			
	        // Map
			ArrayList<String> paramsOptions = getFacetParamsList(paramsFacet, PARAMS_MAP_OPTIONS);
			String paramsMapX1 = getFacetParam(paramsFacet, PARAMS_MAP_X1);
			String paramsMapX2 = getFacetParam(paramsFacet, PARAMS_MAP_X2);
			String paramsMapY1 = getFacetParam(paramsFacet, PARAMS_MAP_Y1);
			String paramsMapY2 = getFacetParam(paramsFacet, PARAMS_MAP_Y2);
			
			HashMap<String,String> coords = null;
			
			if(paramsMapX1 != null && paramsMapX2 != null && paramsMapY1 != null && paramsMapY2 != null){
				coords = new HashMap<String, String>();
				
				coords.put("x1", paramsMapX1);
				coords.put("y1", paramsMapY1);
				coords.put("x2", paramsMapX2);
				coords.put("y2", paramsMapY2);
			}
			
			if(paramsOptions != null && coords != null){
				HashMap<String, String> selectedMap = new HashMap<String, String>();
	    		for(int i=0; i < paramsOptions.size(); i++){
	    			String value = "";
	            	if(getFacetParam(paramsFacet, "x1") != null){
	            		value = coords.get("x1").concat("' O / ");
	            	}
	            	if(getFacetParam(paramsFacet, "y1") != null){
	            		value = value.concat(coords.get("y1")).concat("' N");	
	            	}                
	                value = value.concat("<br>");
	            	if(getFacetParam(paramsFacet, "x2") != null){
	            		value = value.concat(coords.get("x2")).concat("' O / ");                    		
	            	}
	            	if(getFacetParam(paramsFacet, "y2") != null){
	            		value = value.concat(coords.get("y2")).concat("' N");
	            	} 
	                
	                value = value.concat("<br>" +  paramsOptions.get(i));
	                selectedMap.put(paramsOptions.get(i), value);
	    		}
				HashMap mapHashMap = new HashMap();
				mapHashMap.put("webmapclientCoords", coords);
				mapHashMap.put("coordOptions", paramsOptions);
				mapHashMap.put("doMapCoords", selectedMap);
				setAttributeToSession(request, SELECTED_MAP, mapHashMap);
			}
			
			// Config
			ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
			if(config == null){
				config = FacetsConfig.getFacets();
				addDefaultIngridFacets(request, config);
				setAttributeToSession(request, FACET_CONFIG, config);
			}
			if(config != null){
				String[] paramsSplits = paramsFacet.split(";");
				resetFacetConfigSelect(config);
				for(String paramsSplit : paramsSplits){
					String[] split = paramsSplit.split(":");
					IngridFacet tmpFacetKey = getFacetById(config, split[0]);
					if(tmpFacetKey != null){
						//Set facet parent isSelect
						//tmpFacetKey.setSelect(true);
						if(tmpFacetKey.getFacets() != null){
							//Set facet isSelect
							IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), split[1]);
							tmpFacetValue.setSelect(true);
						}
					}
				}
				setAttributeToSession(request, FACET_CONFIG, config);
			}
		}
	}
	
	/**
	 * Get facet parameters list by key
	 * 
	 * @param facetParams
	 * @param facetKey
	 * @return list of facet parameters.
	 */
	private static ArrayList<String> getFacetParamsList(String facetParams, String facetKey) {
		String[] params = facetParams.split(";");
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0; i<params.length; i++){
			String[] param = params[i].split(":");
			if(param != null && param.length == 2){
				String key = param[0];
				String value = param[1];
				
				if(key != null && value != null){
					if(key.equals(facetKey)){
						list.add(value);
					}
				}
			}
		}
		
		return list;
	}

	/**
	 * Get facet parameter by key
	 * 
	 * @param facetParams
	 * @param facetKey
	 * @return facet parameter.
	 */
	private static String getFacetParam(String facetParams, String facetKey) {
		String[] params = facetParams.split(";");
		for(int i=0; i<params.length; i++){
			String[] param = params[i].split(":");
			if(param != null && param.length == 2){
				String key = param[0];
				String value = param[1];
				if(key != null && value != null){
					if(key.equals(facetKey)){
						return value;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Create URL by facet session
	 * 
	 * @param request
	 * @param facetUrl
	 * @param config 
	 */
	@SuppressWarnings("rawtypes")
	private static void setFacetUrlParamsToUrl(ActionRequest request, StringBuffer facetUrl) {
		
        // Geothesaurus
        if(getAttributeFromSession(request, SELECTED_GEOTHESAURUS) != null){
        	HashMap geothesaurusHashMap = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
        	if(geothesaurusHashMap.get("geothesaurusSelectTopicsId") != null){
        		ArrayList<String> selectedTopicIds = (ArrayList<String>) geothesaurusHashMap.get("geothesaurusSelectTopicsId");
        		if(selectedTopicIds != null){
        			for(int i=0; i<selectedTopicIds.size(); i++){
        				String selectedTopicId = selectedTopicIds.get(i);
            			ArrayList<IngridHit> topics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS);
            			if(topics != null){
            				for(int j=0; j<topics.size();j++){
            					Topic topic = (Topic) topics.get(j);
            					String topicId = topic.getTopicNativeKey();
            					String topicName = topic.getTopicName();
            					
            					if(topicId == null){
            						topicId = topic.getTopicID();
            					}
            					if(selectedTopicId.equals(topicId)){
            						appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_GEOTHESAURUS, topicId +","+topicName));
            						break;
            					}
            				}
                		}
            		}
        		}
        	}
        }
        
        // Map
        if(getAttributeFromSession(request, SELECTED_MAP) != null){
        	HashMap mapHashMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);
        	ArrayList<String> options = (ArrayList<String>) mapHashMap.get("coordOptions");
        	HashMap<String,String> coords = (HashMap<String,String>) mapHashMap.get("webmapclientCoords");
        	if(coords != null){
        		appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_X1, coords.get("x1")));
        		appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_Y1, coords.get("y1")));
        		appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_X2, coords.get("x2")));
        		appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_Y2, coords.get("y2")));
        	}
        	
        	if(options != null){
        		addUrlParameterForFacet(facetUrl, PARAMS_MAP_OPTIONS, (ArrayList<String>) options);
        	}
        }
       
        // AreaAddress
        if(getAttributeFromSession(request, "doAddAreaAddress") != null){
        	HashMap<String, String> areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
        	if (areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_AREA_ADDRESS_STREET, areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET)));
            }
            if (areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_AREA_ADDRESS_ZIP, areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)));
            }
            if (areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_AREA_ADDRESS_CITY, areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY)));
            }
        }
        
        // Attribute
        if(getAttributeFromSession(request, "doAddAttribute") != null){
        	HashMap<String, String> attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");

        	if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE) != null) {
        		appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_INSTITUTE, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG) != null) {
            	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_ORG, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM) != null) {
            	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_PM, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF) != null) {
            	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_STAFF, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE) != null) {
            	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_TITLE, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM) != null) {
            	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_TERM_FROM, attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO) != null) {
            	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_TERM_TO, attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO)));
            }
        }
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
		if(config != null){
			addConfigParamsToURL(config, facetUrl, null);
        }
	}

	private static void addConfigParamsToURL(ArrayList<IngridFacet> config, StringBuffer facetUrl, String parentId){
		if(config != null){
        	for(IngridFacet facet : config){
        		if(parentId != null){
        			if(facet.isSelect()){
           				appendURLParameterFacet(facetUrl, toURLParamFacet(facet.getParent().getId(), facet.getId()));
            		}
        		}
        		if(facet.getFacets() != null){
        			addConfigParamsToURL(facet.getFacets(), facetUrl, facet.getId());
        		}
        	}
        }
	}

	/**
	 * Set parameters from session to URL
	 * 
	 * @param url
	 * @param paramsKey
	 * @param parameterFromSession
	 */
	private static void addUrlParameterForFacet(StringBuffer url, String paramsKey, ArrayList<String> parameterFromSession) {
		for(int i=0; i<parameterFromSession.size();i++){
    		appendURLParameterFacet(url, toURLParamFacet(paramsKey, parameterFromSession.get(i)));
    	}
	}
	
	
	/**
	 * Append a new facet urlParameter to the given URL Parameters.
	 * 
	 * @param currentURLParams
	 * @param newURLParam
	 */
	private static void appendURLParameterFacet(StringBuffer currentURLParams, String newURLParam) {
		if (newURLParam != null && newURLParam.length() > 0) {
			if (!currentURLParams.toString().equals("?")) {
				currentURLParams.append("");
			}
			currentURLParams.append(newURLParam);
			currentURLParams.append(";");
		}
	}
	
	/**
	 * Returns the facet "GET" Parameter representation for the URL 
	 * 
	 * @param values
	 * @param paramName
	 * @return
	 */
	private static String toURLParamFacet(String paramName, String value) {
		if (value == null || value.length() == 0) {
			return "";
		}

		String urlParam = null;
		try {
			urlParam = paramName + ":" + URLEncoder.encode(value, "UTF-8");
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems generating URL representation of parameter: " + paramName + "=" + value
						+ "We generate NO Parameter !", ex);
			}
			urlParam = "";
		}

		return urlParam;
	}
	
	private static void sortingFacet(ArrayList<IngridFacet> config){
		if(config != null){
			for(IngridFacet facet : config){
				if(facet.getSort() != null){
					String sorting = facet.getSort();
					if(facet.getFacets() != null){
						sortFacetConfig(facet.getFacets(), sorting);
					}
				}
				if(facet.getFacets() != null){
					sortingFacet(facet.getFacets());
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void sortFacetConfig(ArrayList<IngridFacet> config, String sorting){
		if(sorting.equals("SORT_BY_VALUE_ASC")){
			Collections.sort(config ,new Comparator(){
	            public int compare(Object left, Object right){
	            	Long leftValue = 0L;
	            	Long rightValue = 0L;
	            	if(left != null){
	                	if(((IngridFacet) left).getFacetValue() != null){
	                		leftValue = Long.parseLong(((IngridFacet) left).getFacetValue());
	                	}
	                }
	                if(right != null){
	                	if(((IngridFacet) right).getFacetValue() != null){
	                		rightValue = Long.parseLong(((IngridFacet) right).getFacetValue());
	                	}
	                }
            		return leftValue.compareTo(rightValue);	
	            }
		    });
		}else if(sorting.equals("SORT_BY_VALUE_DESC")){
			Collections.sort(config ,new Comparator(){
	            public int compare(Object left, Object right){
	            	Long leftValue = 0L;
	            	Long rightValue = 0L;
	            	if(left != null){
	                	if(((IngridFacet) left).getFacetValue() != null){
	                		leftValue = Long.parseLong(((IngridFacet) left).getFacetValue());
	                	}
	                }
	                if(right != null){
	                	if(((IngridFacet) right).getFacetValue() != null){
	                		rightValue = Long.parseLong(((IngridFacet) right).getFacetValue());
	                	}
	                }
            		return leftValue.compareTo(rightValue) * (-1);	
	            }
		    });
		}else if(sorting.equals("SORT_BY_NAME")){
			Collections.sort(config ,new Comparator(){
	            public int compare(Object left, Object right){
	            	String leftValue = "";
	            	String rightValue = "";
	            	if(left != null){
	            		if(((IngridFacet) left).getName() != null){
							leftValue = ((IngridFacet) left).getName(); 
						}
	            	}
	            	if(right != null){
	            		if(((IngridFacet) right).getName() != null){
	            			rightValue = ((IngridFacet) right).getName(); 
						}
	            	}
	            	return leftValue.compareTo(rightValue);	
	            }
	        });
		}
	}
	
	private static void addDefaultIngridFacets(PortletRequest request, ArrayList<IngridFacet> config) {
		if(config != null){
			for(IngridFacet facet : config){
				if(facet.getId().equals("topic")){
					ArrayList<IngridFacet> list = facet.getFacets();
					facet.setQueryType("OR_DIALOG");
					facet.setSort("SORT_BY_VALUE_DESC");
					CodeListService codelistService = CodeListServiceFactory.instance();
					ResourceBundle bundle = ResourceBundle.getBundle("de.ingrid.portal.resources.EnvironmentSearchResources", Locale.GERMAN);
					IngridResourceBundle resources = new IngridResourceBundle(bundle, Locale.GERMAN);
					List<IngridEnvTopic> topics = UtilsDB.getEnvTopics(resources);
					for(IngridEnvTopic topic : topics){
						IngridFacet tmpFacet = new IngridFacet();
						String id = UtilsDB.getTopicFromKey(topic.getFormValue());
						tmpFacet.setId(id);
						tmpFacet.setQuery("topic:"+ id);
						String entryId = codelistService.getCodeListEntryId("1410", id, UtilsUDKCodeLists.LANG_ID_INGRID_QUERY_VALUE);
						String localizedValue = codelistService.getCodeListValue("1410", entryId, request.getLocale().getLanguage());
						tmpFacet.setName(localizedValue);
						tmpFacet.setParent(facet);
						if(list == null){
							list = new ArrayList<IngridFacet>();
						}
						list.add(tmpFacet);
					}
					facet.setFacets(list);
				}else if(facet.getId().equals("provider")){
					ArrayList<IngridFacet> list = facet.getFacets();
					String restrictPartner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
					List<IngridPartner> partners = UtilsDB.getPartners();
					if(partners != null){
						if(restrictPartner != null && restrictPartner.length() > 0){
							// Partner restriction (set tmpFacet.setParentHidden(true)) 
							for(IngridPartner partner : partners){
								if(partner.getIdent().equals(restrictPartner)){
									IngridFacet tmpFacet = new IngridFacet();
									tmpFacet.setId(partner.getIdent());
									tmpFacet.setQueryType("OR_DIALOG");
									tmpFacet.setQuery("partner:"+ partner.getIdent());
									tmpFacet.setName(partner.getName());
									tmpFacet.setSort("SORT_BY_VALUE_DESC");
									tmpFacet.setParent(facet);
									// Partner restriction is define.
									tmpFacet.setParentHidden(true);
									List<IngridProvider> providers = UtilsDB.getProvidersFromPartnerKey(partner.getIdent());
									ArrayList<IngridFacet> listProviders = null;
									for(IngridProvider provider : providers){
										IngridFacet tmpProvidersFacet = new IngridFacet();
										tmpProvidersFacet.setId(provider.getIdent());
										tmpProvidersFacet.setQuery("provider:"+ provider.getIdent());
										tmpProvidersFacet.setName(provider.getName());
										tmpProvidersFacet.setParent(tmpFacet);
										if(listProviders == null){
											listProviders = new ArrayList<IngridFacet>();
										}
										listProviders.add(tmpProvidersFacet);
									}
									if(listProviders != null){
										tmpFacet.setFacets(listProviders);
									}
									if(list == null){
										list = new ArrayList<IngridFacet>();
									}
									list.add(tmpFacet);
									break;
								}
							}
						}else{
							for(IngridPartner partner : partners){
								IngridFacet tmpFacet = new IngridFacet();
								tmpFacet.setId(partner.getIdent());
								tmpFacet.setQueryType("OR_DIALOG");
								tmpFacet.setQuery("partner:"+ partner.getIdent());
								tmpFacet.setName(partner.getName());
								tmpFacet.setSort("SORT_BY_VALUE_DESC");
								tmpFacet.setParent(facet);
								List<IngridProvider> providers = UtilsDB.getProvidersFromPartnerKey(partner.getIdent());
								ArrayList<IngridFacet> listProviders = null;
								for(IngridProvider provider : providers){
									IngridFacet tmpProvidersFacet = new IngridFacet();
									tmpProvidersFacet.setId(provider.getIdent());
									tmpProvidersFacet.setQuery("provider:"+ provider.getIdent());
									tmpProvidersFacet.setName(provider.getName());
									tmpProvidersFacet.setParent(tmpFacet);
									if(listProviders == null){
										listProviders = new ArrayList<IngridFacet>();
									}
									listProviders.add(tmpProvidersFacet);
								}
								if(listProviders != null){
									tmpFacet.setFacets(listProviders);
								}
								if(list == null){
									list = new ArrayList<IngridFacet>();
								}
								list.add(tmpFacet);
							}
						}
						facet.setFacets(list);
					}
				}
				if(facet.getFacets()!= null){
					addDefaultIngridFacets(request, facet.getFacets());
				}
			}
		}
	}

	private static void getConfigFacetQuery(ArrayList<IngridFacet> configNode, ArrayList<IngridDocument> facetQueries, boolean isDefault, String mainFacetId){
		IngridDocument facet = null;
		if(configNode != null){
			ArrayList<HashMap<String, String>> facetList = new ArrayList<HashMap<String, String>> ();
		    for(IngridFacet ingridFacet : configNode){
				String facetId = ingridFacet.getId();
				String facetQuery = ingridFacet.getQuery();
				if(ingridFacet.getParent() != null){
					if(!ingridFacet.getParent().getId().equals("topic") && !ingridFacet.getParent().getId().equals("partner") && !ingridFacet.getParent().getId().equals("provider")){
						if(facetId != null){
							if(ingridFacet.getParent() != null){
								if(ingridFacet.getParent().getDependency() == null){
									// Set sub facets by no define dependency
									HashMap<String, String> facetEntry = new HashMap<String, String>();
								    facetEntry.put("id", facetId);
								    facetEntry.put("query", facetQuery);
								    facetList.add(facetEntry);
								}else{
									// Set sub facets only by selected dependency
									if(ingridFacet.getParent().isDependencySelect()){
										HashMap<String, String> facetEntry = new HashMap<String, String>();
									    facetEntry.put("id", facetId);
									    facetEntry.put("query", facetQuery);
									    facetList.add(facetEntry);
									}
								}
							}
							// Set sub facets only by selected
							if(ingridFacet.isSelect()){
								if(ingridFacet.getFacets() != null){
									getConfigFacetQuery(ingridFacet.getFacets(), facetQueries, false, facetId);
								}
							}
						}
					}
				}else{
					if(facetId != null){
						if(ingridFacet.getParent() != null){
							HashMap<String, String> facetEntry = new HashMap<String, String>();
						    facetEntry.put("id", facetId);
						    facetEntry.put("query", facetQuery);
						    facetList.add(facetEntry);
						}
						if(ingridFacet.getFacets() != null){
					      	getConfigFacetQuery(ingridFacet.getFacets(), facetQueries, false, facetId);
						}
					}
				}
			}
		    if(facetList.size() > 0){
		    	if(mainFacetId != null){
					facet = new IngridDocument();
			        facet.put("id", mainFacetId);
				    facet.put("classes", facetList);
				    facetQueries.add(facet);
				}
		    }
		}
		
		if(isDefault){
			// Set facet partner
			IngridDocument faceteEntry = new IngridDocument();
			faceteEntry.put("id", "partner");
			facetQueries.add(faceteEntry);

			// Set facet provider
			String restrictPartner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
			if(restrictPartner != null && restrictPartner.length() > 0){
				// Personalized partner restriction in portal: set always facet for defined partner  
				faceteEntry = new IngridDocument();
				faceteEntry.put("id", "provider");
				faceteEntry.put("field", "provider");
				faceteEntry.put("query", "partner:" + restrictPartner);
				facetQueries.add(faceteEntry);
			}else{
				IngridFacet tmpFacets = getFacetById(configNode, "provider");
				IngridFacet tmpFacet = null;
				if(tmpFacets != null){
					for(IngridFacet tmpFacetEntry : tmpFacets.getFacets()){
						if(tmpFacetEntry.isSelect()){
							tmpFacet = tmpFacetEntry;
							break;
						}
					}
					if(tmpFacet != null){
						faceteEntry = new IngridDocument();
						// NEW FACET ID ! Facet is restricted to providers of partner, so we 
						// have to use NEW ID to separate from normal "provider" facet !
						// This way caching works in backend, e.g. SE-iPlug where id is cache key !
						// So facet id is e.g. "provider_by" when "by" is selected as partner.
						faceteEntry.put("id", "provider_" + tmpFacet.getId());
						faceteEntry.put("field", "provider");
						faceteEntry.put("query", "partner:" + tmpFacet.getId());
						facetQueries.add(faceteEntry);
					}
				}
			}
			
			// Set facet topic
			IngridFacet tmpFacets = getFacetById(configNode, "topic");
			if(tmpFacets != null){
				if(tmpFacets.isSelect()){
					faceteEntry = new IngridDocument();
					faceteEntry.put("id", "topic");
					faceteEntry.put("field", "topic");
					facetQueries.add(faceteEntry);
				}
			}
		}
	}
    
    private static void setFacetQuery(String term, ArrayList<IngridFacet> configNode, PortletRequest request, IngridQuery query) throws ParseException{
    	if(term != null){
    		for (IngridFacet ingridFacet : configNode){
				if(ingridFacet.getFacets() != null){
					term = getQuerySelection(term, ingridFacet.getQueryType(), ingridFacet.getFacets());
				}
        	}
    		IngridQuery tmpQuery = QueryStringParser.parse(term);
    		
    		query.remove("term");
    		
    		for(TermQuery tmp :tmpQuery.getTerms()){
    			query.addTerm(tmp);
    		}
    		for(ClauseQuery tmp : tmpQuery.getClauses()){
				query.addClause(tmp);	
			}
			for(FieldQuery tmp : tmpQuery.getDataTypes()){
				query.addField(tmp);	
			}
			for(FieldQuery tmp : tmpQuery.getFields()){
				query.addField(tmp);	
			}
			for(WildCardFieldQuery tmp : tmpQuery.getWildCardFieldQueries()){
				query.addWildCardFieldQuery(tmp);	
			}
			for(WildCardTermQuery tmp : tmpQuery.getWildCardTermQueries()){
				query.addWildCardTermQuery(tmp);	
			}
			for(FuzzyFieldQuery tmp : tmpQuery.getFuzzyFieldQueries()){
				query.addFuzzyFieldQuery(tmp);
			}
			for(FuzzyTermQuery tmp : tmpQuery.getFuzzyTermQueries()){
				query.addFuzzyTermQuery(tmp);
			}
			for(RangeQuery tmp : tmpQuery.getRangeQueries()){
				query.addRangeQuery(tmp);	
			}
			if(tmpQuery.get("partner") != null){
				for(FieldQuery tmp : (ArrayList<FieldQuery>)tmpQuery.get("partner")){
					query.addField(tmp);	
				}
					
			}
			if(tmpQuery.get("provider") != null){
				for(FieldQuery tmp : (ArrayList<FieldQuery>)tmpQuery.get("provider")){
					query.addField(tmp);	
				}
			}
    	}
    } 
    
    private static String getQuerySelection(String term, String type, ArrayList<IngridFacet> facets) {
    	if(facets != null){
			if(type != null){
				// OR
				if(type.equals("OR") || type.equals("OR_DIALOG")){
					String orQuery = "()";
					for(IngridFacet ingridFacet : facets){
						if(ingridFacet.isSelect() || ingridFacet.isParentHidden()){
							if(ingridFacet.getQuery() != null){
								if(orQuery.equals("()")){
									orQuery = orQuery.replace(")", ingridFacet.getQuery().toLowerCase()+")");
								}else{
									orQuery = orQuery.replace(")", " OR " + ingridFacet.getQuery().toLowerCase()+")");
								}
							}
						}
					}
					if(!orQuery.equals("()")){
						term = term + " " + orQuery;
					}
				}
			}else{
				// AND
				for(IngridFacet ingridFacet : facets){
					if(ingridFacet.isSelect() || ingridFacet.isParentHidden()){
						if(ingridFacet.getQuery() != null){
							term = term + " " + ingridFacet.getQuery();
							if(ingridFacet.getFacets() != null){
								term = getQuerySelection(term, ingridFacet.getQueryType(), ingridFacet.getFacets());
							}
						}
					}
				} 
			}
		}
    	return term;
	}

	private static void getActionKeysFromConfig(ArrayList<IngridFacet> configNode, HashMap<String, String> configKeys) {
		if(configNode != null){
			for (IngridFacet ingridFacet : configNode){
	    		IngridFacet ingridFacetParent = ingridFacet.getParent();
	    		if(ingridFacetParent != null){
	    			String key = ingridFacetParent.getId();
	        		if(key != null){
	        			configKeys.put(key, key);
	        		}
	    		}
	    		if(ingridFacet.getFacets() != null){
	    			getActionKeysFromConfig(ingridFacet.getFacets(), configKeys);
	    	    }
	    	}
		}
	}
    
    private static Set<String> getExistingSelectionKeys(PortletRequest request){
    	HashMap<String, String> configIds = new HashMap<String, String>();
		getActionKeysFromConfig((ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG), configIds);
		return configIds.keySet();
    }
    
    private static IngridFacet getFacetById(ArrayList<IngridFacet> facets, String key){
    	IngridFacet ingridFacet = null;
    	if(facets != null){
    		for(IngridFacet tmpIngridFacet: facets){
    			if(tmpIngridFacet.getId() != null){
    				if(key.equals(tmpIngridFacet.getId())){
    					ingridFacet = tmpIngridFacet;
    				}
    			}
    			if(ingridFacet == null){
    				if(tmpIngridFacet.getFacets() != null){
        				ingridFacet = getFacetById(tmpIngridFacet.getFacets(), key);
        			}
            	}
    		}
    	}
    	return ingridFacet;
    }
    
    private static ArrayList<IngridFacet> getDependencyFacetById(ArrayList<IngridFacet> facets, ArrayList<IngridFacet> list, String key){
    	if(facets != null){
    		for(IngridFacet tmpIngridFacet: facets){
    			if(tmpIngridFacet.getDependency() != null){
    				if(key.indexOf(tmpIngridFacet.getDependency()) > -1){
    					if(list == null){
    						list = new ArrayList<IngridFacet>();
    					}
    					list.add(tmpIngridFacet);
    				}
    			}
				if(tmpIngridFacet.getFacets() != null){
    				list = getDependencyFacetById(tmpIngridFacet.getFacets(), list, key);
    			}
    		}
    	}
    	return list;
    }
    
    private static ArrayList<IngridFacet> getHiddenFacetById(ArrayList<IngridFacet> facets, ArrayList<IngridFacet> list, String key){
    	if(facets != null){
    		for(IngridFacet tmpIngridFacet: facets){
    			if(tmpIngridFacet.getHidden() != null){
    				if(key.indexOf(tmpIngridFacet.getHidden()) > -1){
    					if(list == null){
    						list = new ArrayList<IngridFacet>();
    					}
    					list.add(tmpIngridFacet);
    				}
    			}
				if(tmpIngridFacet.getFacets() != null){
    				list = getHiddenFacetById(tmpIngridFacet.getFacets(), list, key);
    			}
    		}
    	}
    	return list;
    }
    
    private static boolean isAnyFacetConfigSelect(ArrayList<IngridFacet> config, boolean sessionSelect) {
		boolean isSelect = false;
		for(IngridFacet facet : config){
			if(facet.isSelect()){
				isSelect = true;
				break;
			}
			if(facet.getFacets() != null){
				isSelect = isAnyFacetConfigSelect(facet.getFacets(), false);
				if(isSelect){
					break;
				}
			}
		}
		if(!isSelect){
			isSelect = sessionSelect ? sessionSelect : false;
		}
		return isSelect;
	}
	
	private static boolean isFacetConfigSelect(ArrayList<IngridFacet> config) {
		boolean isSelect = false;
		for(IngridFacet facet : config){
			if(facet.isSelect()){
				isSelect = true;
				break;
			}
		}
		return isSelect;
	}

	
	private static void resetFacetConfigValues(ArrayList<IngridFacet> config, String key) {
		if(config != null){
			for(IngridFacet facet : config){
				boolean isOrSelect = false;
				if(key != null){
					if(facet.getId().equals(key)){
						if(facet.getQueryType() != null){
							if(facet.getQueryType().equals("OR") || facet.getQueryType().equals("OR_DIALOG")){
								isOrSelect = true;
							}
						}
					}
				}
				if(!isOrSelect){
					facet.setFacetValue(null);
					facet.setOldIPlug(false);
					if(facet.getFacets() != null){
						resetFacetConfigValues(facet.getFacets(), key);
					}
				}
			}
		}
	}
	
	private static void resetFacetConfigSelect(ArrayList<IngridFacet> config) {
		if(config != null){
			for(IngridFacet facet : config){
				facet.setSelect(false);
				if(facet.getFacets() != null){
					resetFacetConfigSelect(facet.getFacets());
				}
			}
		}
	}

	private static void checkNonFacetsIplugs(IngridHit[] hits, PortletRequest request) {
		ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
		
		if(hits != null){
			if(config != null){
				for (IngridHit hit : hits){
					if(hit.getHitDetail() != null){
						PlugDescription pd = (PlugDescription) hit.get("plugDescr");
						if(pd != null){
							String[] partners = pd.getPartners();
							if(partners != null){
								for(String partner : partners){
									IngridFacet facetParent = getFacetById(config, "provider");
									if(facetParent != null){
										if(facetParent.getFacets() != null){
											IngridFacet facet = getFacetById(facetParent.getFacets(), partner);
											if(facet != null){
												if(facet.getFacetValue() == null){
													facet.setOldIPlug(true);
												}
											}
										}
									}
								}
							}
							String[] providers = pd.getProviders();
							if(providers != null){
								for(String provider : providers){
									IngridFacet facetParent = getFacetById(config, "provider");
									if(facetParent != null){
										if(facetParent.getFacets() != null){
											IngridFacet facet = getFacetById(facetParent.getFacets(), provider);
											if(facet != null){
												if(facet.getFacetValue() == null){
													facet.setOldIPlug(true);
												}
											}
										}
									}
								}
							}
							String[] datatypes = pd.getDataTypes();
							if(datatypes != null){
								for(String datatype : datatypes){
									IngridFacet facetParent = getFacetById(config, "type");
									if(facetParent != null){
										if(facetParent.getFacets() != null){
											if(datatype.equals("www") || datatype.equals("metadata") || datatype.equals("address")){
												IngridFacet facet = getFacetById(facetParent.getFacets(), datatype);
												if(facet != null){
													if(facet.getFacetValue() == null){
														facet.setOldIPlug(true);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				setAttributeToSession(request, FACET_CONFIG, config);
			}
		}
	}
}