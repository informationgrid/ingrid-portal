/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.pluto.core.impl.PortletSessionImpl;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.codelists.CodeListService;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtAdrPlaceReferenceForm;
import de.ingrid.portal.forms.SearchExtEnvPlaceGeothesaurusForm;
import de.ingrid.portal.forms.SearchExtResTopicAttributesForm;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridMeasuresRubric;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.om.IngridServiceRubric;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.RangeQuery;
import de.ingrid.utils.query.WildCardFieldQuery;
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
    
    /*
    private static final String THESAURUS_CURRENT_TOPIC = "facete_current_topic_thesaurus";
    private static final String THESAURUS_TOPICS = "facete_topics_thesaurus";
    private static final String THESAURUS_SIMILAR_TOPICS = "facete_similar_topics_thesaurus";
    private static final String THESAURUS_ERROR = "facete_error_thesaurus";
    private static final String THESAURUS_LIST_SIZE = "facete_list_size_thesaurus";
    private static final String THESAURUS_DO = "doThesaurus";
    private static final String THESAURUS_SELECTED_TOPICS_IDS = "thesaurusSelectTopicsId";
    private static final String THESAURUS_TERM = "thesaurusTerm";
    private static final String THESAURUS_SELECTED_TOPICS = "thesaurusSelectTopics";
    private static final String THESAURUS_ALL_TOPICS = "allThesaurusTopics";
    */
    
    private static final String ELEMENTS_PROVIDER = "elementsProvider";
    private static final String ELEMENTS_PARTNER = "elementsPartner";
    private static final String ELEMENTS_TOPIC = "elementsTopic";
    private static final String ELEMENTS_DATATYPE = "elementsDatatype";
    private static final String ELEMENTS_METACLASS = "elementsMetaclass";
    private static final String ELEMENTS_TIME = "elementsTime";
    private static final String ELEMENTS_GEOTHESAURUS = "elementsGeothesaurus";
    private static final String ELEMENTS_MAP = "elementsMap";
    
    private static final String ENABLE_FACETE_PARTNER_LIST = "enableFacetePartnerList";
    
    private static final String SELECTED_PROVIDER = "selectedProvider";
    private static final String SELECTED_TOPIC = "selectedTopics";
    private static final String SELECTED_METACLASS = "selectedMetaclass";
    private static final String SELECTED_DATATYPE = "selectedDatatype";
    private static final String SELECTED_MEASURES = "selectedMeasures";
    private static final String SELECTED_SERVICE = "selectedService";
    private static final String SELECTED_GEOTHESAURUS = "selectedGeothesaurus";
    private static final String SELECTED_MAP = "selectedMap";
    
    private static final String PARAMS_TOPIC = "topic";
    private static final String PARAMS_SERVICE = "service";
    private static final String PARAMS_MEASURE = "measure";
    private static final String PARAMS_DATATYPE = "type";
    private static final String PARAMS_PROVIDER = "provider";
    private static final String PARAMS_PARTNER = "partner";
    private static final String PARAMS_GEOTHESAURUS = "geothesaurus";
    private static final String PARAMS_METACLASS = "metaclass";
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
    private static final String PARAMS_MODTIME = "modtime";
    
    public static final String SESSION_PARAMS_READ_FACET_FROM_SESSION = "readFacetFromSession";
    public static final String SESSION_PARAMS_FACET_GROUPING = "facet_grouping";
    
    private static final String[] defaultDatatypes = {"www","fis","metadata","map","address","law","research","topic","measure","service","other"};
    private static final String[] defaultMetaclasses = {"job","service","map","document","geoservice","project","database","inspire"};
    
    /**
     * Prepare query by facet activity
     * 
     * @param ps
     * @param query
     */
    public static void facetePrepareInGridQuery (PortletRequest request, IngridQuery query){
    	
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
    	
    	addToQueryTopic(request, query);
	    addToQueryMeasures(request, query);
        addToQueryDatatype(request, query);
        addToQueryService(request, query);
        addToQueryMetaclass(request, query);
        addToQueryPartner(request, query);
        addToQueryProvider(request, query);
        addToQueryTime(request, query);
        addToQueryMap(request, query);
        addToQueryGeothesaurus(request, query);
        //addToQueryThesaurus(request, query);
        addToQueryAttribute(request, query);
        addToQueryAreaAddress(request, query);
        
        if(query.get("FACETS") == null){
            ArrayList<IngridDocument> list = new ArrayList<IngridDocument>();
	        setFaceteQueryParamsMetaclass(list, request);
	        setFaceteQueryParamsPartner(list);
	        setFaceteQueryParamsProvider(list, request);
			setFaceteQueryParamsDatatype(list);
	        setFaceteQueryParamsTopic(list, request);
	        setFaceteQueryParamsTime(list, request);
	        //setFaceteQueryParamsMap(list, request);
	        setFaceteQueryParamsGeothesaurus(list, request);
	        query.put("FACETS", list);
	        
	        setAttributeToSession(request, "FACETS_QUERY", list, false);
        }
        
        setAttributeToSession(request, "SEARCH_QUERY", query, false);
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
	public static void setParamsToContext(RenderRequest request, Context context, IngridDocument facete) {
		
		if(facete == null){
			removeFaceteElementsFromSession(request);
		}
		
		getFacetAttributsParamsFromUrl(request);
		setParamsToContextTopic(request, context);
		setParamsToContextMetaclass(request, context);
		setParamsToContextDatatype(request, context);
		setParamsToContextService(request, context);
		setParamsToContextMeasures(request, context);
		setParamsToContextPartner(request, context);
		setParamsToContextProvider(request, context);
		setParamsToContextMap(request, context);
        setParamsToContextGeothesaurus(request, context);
        //setParamsToContextThesaurus(request, context);
        setParamsToContextTime(request, context);
        setParamsToContextAttribute(request, context);
        setParamsToContextAreaAddress(request, context);
        
        context.put("facetsQuery", getAttributeFromSession(request, "FACETS_QUERY"));
        context.put("searchQuery", getAttributeFromSession(request, "SEARCH_QUERY"));
        context.put("enableFacetSelection", isFacetSelection(request));
	}

	/**
	 * Set action parameters to facet session
	 * 
	 * @param request
	 * @param reponse
	 * @return URL with facet parameters
	 */
	public static String setFaceteParamsToSessionByAction(ActionRequest request, ActionResponse reponse) {
		
		StringBuffer facetUrl = new StringBuffer("&f=");

		general(request);
		
		setFaceteParamsToSessionTopic(request);
		setFaceteParamsToSessionMetaclass(request);
		setFaceteParamsToSessionDatatype(request);
		setFaceteParamsToSessionService(request);
		setFaceteParamsToSessionMeasures(request);
		setFaceteParamsToSessionPartner(request);
		setFaceteParamsToSessionProvider(request);
		setFaceteParamsToSessionTime(request);
        setFaceteParamsToSessionMap(request);
        setFaceteParamsToSessionGeothesaurus(request);
        //setFaceteParamsToSessionThesaurus(request);
        setFaceteParamsToSessionAttribute(request);
        setFaceteParamsToSessionAreaAddress(request);
        
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
	public static void checkForExistingFacete(IngridDocument facetes, PortletRequest request) {
		
		HashMap<String, Long> elementsProvider = null;
		HashMap<String, Long> elementsPartner = null;
		HashMap<String, Long> elementsTopic = null;
		HashMap<String, Long> elementsDatatype = null;
		HashMap<String, Long> elementsMetaclass = null;
		HashMap<String, Long> elementsTime = null;
		HashMap<String, Long> elementsGeothesaurus = null;
		HashMap<String, Long> elementsMap = null;
		
		removeFaceteElementsFromSession(request);
		
		for (Iterator<String> iterator = facetes.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Long value = (Long) facetes.get(key);
			
			if(value > 0){
				if (key.startsWith("provider")){
					if(elementsProvider == null){
						elementsProvider = new HashMap<String, Long>();
					}
					elementsProvider.put(key.replace("provider:", ""), value);
					
				}else if(key.startsWith("partner")){
					if(elementsPartner == null){
						elementsPartner = new HashMap<String, Long>();
					}
					elementsPartner.put(key.replace("partner:", ""), value);
					
				}else if(key.startsWith("topic")){
					if(elementsTopic == null){
						elementsTopic = new HashMap<String, Long>();
					}
					elementsTopic.put(key.replace("topic:", ""), value);
					
				}else if(key.startsWith("metaclass")){
					if(elementsMetaclass == null){
						elementsMetaclass = new HashMap<String, Long>();
					}
					
					String replaceKey = key.replace("metaclass:", "");
					ArrayList<String> selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
					if(selectedMetaclass != null && selectedMetaclass.size() > 0){
						String metaclass = selectedMetaclass.get(0);
						if(replaceKey.equals(metaclass)){
							elementsMetaclass.put(replaceKey, value);	
						}
					}else{
						elementsMetaclass.put(replaceKey, value);
					}
				}else if(key.startsWith("type:")){
					if(elementsDatatype == null){
						elementsDatatype = new HashMap<String, Long>();
					}
					elementsDatatype.put(key.replace("type:", ""), value);

				}else if(key.startsWith("modtime:")){
					if(elementsTime == null){
						elementsTime = new HashMap<String, Long>();
					}
					elementsTime.put(key.replace("modtime:", ""), value);

				}else if(key.startsWith("geothesaurus:")){
					if(elementsGeothesaurus == null){
						elementsGeothesaurus = new HashMap<String, Long>();
					}
					elementsGeothesaurus.put(key.replace("geothesaurus:", ""), value);

				}else if(key.startsWith("coords:")){
					if(elementsMap == null){
						elementsMap = new HashMap<String, Long>();
					}
					elementsMap.put(key.replace("coords:", ""), value);

				}
			}
		}		
		
		if (elementsDatatype != null){
			String[] sortedRanking = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_SEARCH_FACETS_DATATYPE);
			if(sortedRanking.length==defaultDatatypes.length){
				setAttributeToSession(request, ELEMENTS_DATATYPE, sortHashMapAsArrayList(elementsDatatype, sortedRanking));
			}else{
				setAttributeToSession(request, ELEMENTS_DATATYPE, sortHashMapAsArrayList(elementsDatatype));
			}
			
			
		}
		
		if (elementsProvider != null){
			setAttributeToSession(request, ELEMENTS_PROVIDER, sortHashMapAsArrayList(elementsProvider));
		}
		
		if (elementsTopic != null){
			setAttributeToSession(request, ELEMENTS_TOPIC, sortHashMapAsArrayList(elementsTopic));
		}
		
		if (elementsPartner != null){
			setAttributeToSession(request, ELEMENTS_PARTNER, sortHashMapAsArrayList(elementsPartner));
		}
		
		if (elementsMetaclass != null){
			String[] sortedRanking = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_SEARCH_FACETS_METACLASS);
			if(sortedRanking.length==defaultMetaclasses.length){
				setAttributeToSession(request, ELEMENTS_METACLASS, sortHashMapAsArrayList(elementsMetaclass, sortedRanking));
			}else{
				setAttributeToSession(request, ELEMENTS_METACLASS, sortHashMapAsArrayList(elementsMetaclass));
			}
		}
		
		if (elementsTime != null){
			setAttributeToSession(request, ELEMENTS_TIME, sortHashMapAsArrayList(elementsTime));
		}
		
		if (elementsGeothesaurus != null){
			setAttributeToSession(request, ELEMENTS_GEOTHESAURUS, sortHashMapAsArrayList(elementsGeothesaurus));
		}
	
		if (elementsMap != null){
			setAttributeToSession(request, ELEMENTS_MAP, sortHashMapAsArrayList(elementsMap));
		}
	
	}

	/***************************** THEMEN **********************************************/
	
	private static void setFaceteQueryParamsTopic(ArrayList<IngridDocument> list, PortletRequest request) {
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("topic") || selectedDatatype.get(i).equals("service") || selectedDatatype.get(i).equals("measure")){
					IngridDocument facete = new IngridDocument();
			        facete.put("id", "topic");
			        facete.put("field", "topic");
			        list.add(facete);
					break;
				}
			}
		}
	}

	
	private static void setFaceteParamsToSessionTopic(ActionRequest request) {
		
		String doAddTopic = request.getParameter("doAddTopic");
		String doAddTopicChb = request.getParameter("doAddTopicChb");
		String doRemoveTopic = request.getParameter("doRemoveTopic");
		String doRemoveAllTopics = request.getParameter("doRemoveAllTopics");
		ArrayList<String> selectedTopics = null;
		
		if(doAddTopic != null){
			Map map = request.getParameterMap();
			List<String> mapKeys = new ArrayList<String>(map.keySet());
			for (int i=0; i< mapKeys.size(); i++) {
				String key = mapKeys.get(i);
				if(selectedTopics == null){
					selectedTopics = new ArrayList<String>();
				}
				if(key.startsWith("chk")){
					String [] topicsId = (String[]) map.get(key);
					selectedTopics.add(topicsId[0]);
				}
			}
		}
		
		if(doAddTopicChb != null){
			selectedTopics = new ArrayList<String> ();
			selectedTopics.add(doAddTopicChb);
		}
		
		if(doRemoveTopic != null){
			selectedTopics = (ArrayList<String>) getAttributeFromSession(request, SELECTED_TOPIC);
			if (selectedTopics != null){
				for(int i = 0; i < selectedTopics.size(); i++){
					if(selectedTopics.get(i).equals(doRemoveTopic)){
						selectedTopics.remove(i);
					}
				}
			}
		}
		
		if(selectedTopics != null){
        	setAttributeToSession(request, SELECTED_TOPIC, selectedTopics, true);
        }
		
		if(doRemoveAllTopics != null){
			removeAttributeFromSession(request, SELECTED_TOPIC);
		}
	}
	
	private static void setParamsToContextTopic (RenderRequest request, Context context){
		
		ArrayList<HashMap<String, Long>> elementsTopic = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		ArrayList<String> selectedTopics = (ArrayList<String>) getAttributeFromSession(request, SELECTED_TOPIC);
		
		HashMap<String, Long> elementsTopicWithFacete = null;
		HashMap<String, Long> elementsTopicSelect = null;
		HashMap<String, String> topicsLocalisation = null;
		
		ArrayList<IngridEnvTopic> enableFaceteTopicsList = null;
		List<IngridEnvTopic> unselectedTopics = null;
		CodeListService codelistService = CodeListServiceFactory.instance();
		
		ResourceBundle bundle = ResourceBundle.getBundle("de.ingrid.portal.resources.EnvironmentSearchResources", Locale.GERMAN);
		IngridResourceBundle resources = new IngridResourceBundle(bundle);
		if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("topic")){
					unselectedTopics = UtilsDB.getEnvTopics(resources);	
					
					for(int j=0; j < unselectedTopics.size(); j++){
		    			if(topicsLocalisation == null){
		    				topicsLocalisation  = new HashMap<String, String>();
		    			}
		    			IngridEnvTopic topic = unselectedTopics.get(j);
						
		    			String entryId = codelistService.getCodeListEntryId("1410", UtilsDB.getTopicFromKey(topic.getFormValue()), UtilsUDKCodeLists.LANG_ID_INGRID_QUERY_VALUE);
						String localizedValue = codelistService.getCodeListValue("1410", entryId, request.getLocale().getLanguage());
						topicsLocalisation.put(topic.getFormValue(), localizedValue);
						
		    		}
					break;
				}
			}
		}
		
		if(unselectedTopics != null && elementsTopic != null){
			for(int i=0; i < elementsTopic.size(); i++){
    			HashMap<String, Long> map = elementsTopic.get(i);
    			List<String> keys = new ArrayList<String>(map.keySet());
    			String ident = keys.get(0);
    			
    			for(int j=0; j < unselectedTopics.size(); j++){
    				if(ident != null){
    					IngridEnvTopic top = unselectedTopics.get(j);
    					String topIdent = UtilsDB.getTopicFromKey(top.getFormValue()); 
    					
        				if(ident.equals(topIdent)){
        					if(j >= unselectedTopics.size()){
        						break;
        					}
        					if(elementsTopicWithFacete == null){
        						elementsTopicWithFacete = new HashMap<String,Long>();
        					}
        					elementsTopicWithFacete.put(top.getFormValue(), map.get(ident));

        					if(enableFaceteTopicsList == null){
        						enableFaceteTopicsList = new ArrayList<IngridEnvTopic>();
            				}
        					enableFaceteTopicsList.add(top);
        					unselectedTopics.remove(j);
        					break;
        				}
        			}
    			}
    		}
	    }
		
		if(selectedTopics != null){
			for(int i=0; i< selectedTopics.size(); i++){
				String selectedKey = selectedTopics.get(i);
				boolean foundKey = false;
				if(elementsTopicWithFacete != null){
					for(int j=0; j< elementsTopicWithFacete.size(); j++){
						for (Iterator<String> iterator = elementsTopicWithFacete.keySet().iterator(); iterator.hasNext();) {
							String key = iterator.next();
							if(key.equals(selectedKey)){
								foundKey = true;
								break;
							}
						}
						if(foundKey){
							break;
						}
					}
					if(!foundKey){
						elementsTopicWithFacete.put(selectedKey, (long) 0);
					}
				}else{
					elementsTopicWithFacete = new HashMap<String,Long>();
					elementsTopicWithFacete.put(selectedKey, (long) 0);
				}
			}
		}
		
		if(elementsTopicWithFacete != null && elementsTopicWithFacete.size() > 0){
			for(int i=0; i < elementsTopicWithFacete.size(); i++){
    			List<String> keys = new ArrayList<String>(elementsTopicWithFacete.keySet());
    			String ident = keys.get(i);
    			if(unselectedTopics != null){
    				for(int j=0; j < unselectedTopics.size(); j++){
        				if(ident != null){
        					IngridEnvTopic top = unselectedTopics.get(j);
        					String topIdent = top.getFormValue(); 
        					
            				if(ident.equals(topIdent.toLowerCase())){
            					unselectedTopics.remove(j);
            					break;
            				}
            			}
        			}
    			}
    		}
			
			if(selectedTopics != null){
				for(int i=0; i < selectedTopics.size(); i++){
					String selectedKey = selectedTopics.get(i);
					if(selectedKey != null){
						for(int j=0; j < elementsTopicWithFacete.size(); j++){
							List<String> mapKeys = new ArrayList<String>(elementsTopicWithFacete.keySet());
							String mapKey = mapKeys.get(j);
							if(mapKey.equals(selectedKey)){
								if(elementsTopicSelect == null){
									elementsTopicSelect = new HashMap<String,Long>();
								}
								elementsTopicSelect.put(mapKey, elementsTopicWithFacete.get(mapKey));
								elementsTopicWithFacete.remove(mapKey);
								break;
							}
						}
					}
				}
			}
			
		}
		
		if(enableFaceteTopicsList != null && enableFaceteTopicsList.size() > 0){
	    	context.put("enableFaceteTopicsList", enableFaceteTopicsList);
		}
    	if(elementsTopicWithFacete != null && elementsTopicWithFacete.size() > 0){
    		context.put("elementsTopicUnused", sortHashMapAsArrayList(elementsTopicWithFacete));
    	}
    	
    	if(elementsTopicSelect != null && elementsTopicSelect.size() > 0){
    		context.put("elementsTopicSelect", sortHashMapAsArrayList(elementsTopicSelect));
    	}
    	if(unselectedTopics != null && unselectedTopics.size() > 0){
    		context.put("unselectedTopics", unselectedTopics);
    	}
    	
    	if(topicsLocalisation != null && topicsLocalisation.size() > 0){
            context.put("topicsLocalisation", topicsLocalisation);
        }
    	
    	if(selectedTopics != null && selectedTopics.size() > 0){
        	setFacetSelectionState(context, request, "isTopicSelect", true);
    		context.put("selectedTopics", selectedTopics);
    	} else{
    		setFacetSelectionState(context, request, "isTopicSelect", false);
    		context.remove("selectedTopics");
    	}
    	
    	context.put("enableFaceteTopicCount", PortalConfig.getInstance().getInt("portal.search.facete.topics.count", 3));
	}
	
	private static void addToQueryTopic(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedTopics = (ArrayList<String>) getAttributeFromSession(request, SELECTED_TOPIC);
    	
		if(selectedTopics != null && selectedTopics.size() > 0){
			
        	// TOPIC
            ClauseQuery cq  = new ClauseQuery(true, false);
            for (int i = 0; i < selectedTopics.size(); i++) {
            	String queryValue = UtilsDB.getTopicFromKey(selectedTopics.get(i));
                cq.addField(new FieldQuery(false, false, Settings.QFIELD_TOPIC, queryValue));
            }
            query.addClause(cq);
        }
	}
	
	/***************************** METACLASS *******************************************/

	private static void setFaceteQueryParamsMetaclass(ArrayList<IngridDocument> list, PortletRequest request) {
		IngridDocument facete = new IngridDocument();
        boolean isMetadataSelect = false;
        
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
		if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("metadata")){
					isMetadataSelect = true;
					break;
				}
			}
		}
		if(isMetadataSelect){
			String[] availableMetaclass = defaultMetaclasses;
			ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
		    
			for(int i=0; i < availableMetaclass.length; i++){
				String key = availableMetaclass[i];
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
				faceteEntry.put("id", key);
				if(key.equals("inspire")){
			    	faceteEntry.put("query", "t04_search.searchterm:inspireidentifiziert");
			    }else{
			    	int value = 0;
					if(key.equals("job")){
						value = 0;
					}else if(key.equals("map")){
						value = 1;
					}else if(key.equals("document")){
						value = 2;
					}else if(key.equals("geoservice")){
						value = 3;
					}else if(key.equals("project")){
						value = 4;
					}else if(key.equals("database")){
						value = 5;
					}else if(key.equals("service")){
						value = 6;
					}					
					faceteEntry.put("query", "t01_object.obj_class:"+ value);
			    }
				faceteList.add(faceteEntry);
			}
	       facete.put("id", "metaclass");
	       facete.put("classes", faceteList);
	        
	       list.add(facete);
		}
	}

	private static void setFaceteParamsToSessionMetaclass(ActionRequest request) {
		
		String doAddMetaclass = request.getParameter("doAddMetaclass");
		String doRemoveMetaclass = request.getParameter("doRemoveMetaclass");
		ArrayList<String> selectedMetaclass = null;
		
		if(doAddMetaclass != null){
			selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);;
			if(selectedMetaclass == null){
				selectedMetaclass = new ArrayList<String>();
			}
			selectedMetaclass.add(doAddMetaclass);
		}
		
		if(doRemoveMetaclass != null){
			selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
			if (selectedMetaclass != null){
				for(int i = 0; i < selectedMetaclass.size(); i++){
					if(selectedMetaclass.get(i).equals(doRemoveMetaclass)){
						selectedMetaclass.remove(i);
					}
				}
			}
		}
		
		if(selectedMetaclass != null){
			setAttributeToSession(request, SELECTED_METACLASS, selectedMetaclass, true);
		}
	}
	private static void setParamsToContextMetaclass (RenderRequest request, Context context){
		
		ArrayList<String> selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
		ArrayList<String> enableFaceteMetaClass = (ArrayList<String>) getAttributeFromSession(request, ELEMENTS_METACLASS);
		
    	if(enableFaceteMetaClass != null){
    		context.put("enableFaceteMetaClass", enableFaceteMetaClass);
        }
    	
		if(selectedMetaclass != null && selectedMetaclass.size() > 0){
			setFacetSelectionState(context, request, "isMetaclassSelect", true);
    		context.put("selectedMetaclass", selectedMetaclass);
    	} else{
    		setFacetSelectionState(context, request, "isMetaclassSelect", false);
    		context.remove("selectedMetaclass");
    	}
	}
	
	private static void addToQueryMetaclass(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
    	
		if(selectedMetaclass != null && selectedMetaclass.size() > 0){
    	   for (int i = 0; i < selectedMetaclass.size(); i++) {
    		   if(selectedMetaclass.get(i).equals("inspire")){
    			   query.addField(new FieldQuery(true, false, "t04_search.searchterm", "inspireidentifiziert"));
    		   }else{
    			   query.addField(new FieldQuery(true, false, Settings.QFIELD_METACLASS, selectedMetaclass.get(i)));
    		   }
	       }
	    }
	}
	
	/***************************** DATENTYPEN *********************************************/
	
	private static void setFaceteQueryParamsDatatype(ArrayList<IngridDocument> list) {
		IngridDocument facete = new IngridDocument();
        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
	    
        String[] enableDatatypes = defaultDatatypes;
        for(int i=0; i < enableDatatypes.length; i++){
			String key = enableDatatypes[i];
			if(key.equals("map")){
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
		        faceteEntry.put("id", "map");
		        faceteEntry.put("query", "(capabilities_url:http* OR t011_obj_serv_op_connpoint.connect_point:http*)");
		        faceteList.add(faceteEntry);
			}else if(key.equals("topic")){
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
		        faceteEntry.put("id", "topic");
		        faceteEntry.put("query", "datatype:topics");
		        faceteList.add(faceteEntry);
			}else if(key.equals("other")){
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
		        faceteEntry.put("id", "other");
		        faceteEntry.put("query", "datatype:dsc_other");
		        faceteList.add(faceteEntry);
			}else{
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
		        faceteEntry.put("id", key);
		        faceteEntry.put("query", "datatype:"+ key);
		        faceteList.add(faceteEntry);	
			}
	   }
       facete.put("id", "type");
       facete.put("classes", faceteList);
        
       list.add(facete);
	}
	
	private static void setFaceteParamsToSessionDatatype(ActionRequest request) {
		
		String doAddDatatype = request.getParameter("doAddDatatype");
		String doRemoveDatatype = request.getParameter("doRemoveDatatype");
		ArrayList<String> selectedDatatype = null;
		
		if(doAddDatatype != null){
			selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
			if(selectedDatatype == null){
				selectedDatatype = new ArrayList<String>();
			}
			selectedDatatype.add(doAddDatatype);
		}
		
		if(doRemoveDatatype != null){
			selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
			if (selectedDatatype != null){
				for(int i = 0; i < selectedDatatype.size(); i++){
					if(selectedDatatype.get(i).equals(doRemoveDatatype)){
						selectedDatatype.remove(i);
					}
				}
			}
			if(doRemoveDatatype.equals("metadata")){
				removeAttributeFromSession(request, SELECTED_METACLASS);
				removeAttributeFromSession(request, "doTime");
			}else if(doRemoveDatatype.equals("topic")){
				removeAttributeFromSession(request, SELECTED_TOPIC);
			}else if(doRemoveDatatype.equals("measure")){
				removeAttributeFromSession(request, SELECTED_MEASURES);
			}else if(doRemoveDatatype.equals("service")){
				removeAttributeFromSession(request, SELECTED_SERVICE);
			}
		}
		
		if(selectedDatatype != null){
			setAttributeToSession(request, SELECTED_DATATYPE, selectedDatatype, true);
		}
	}
	
	private static void setParamsToContextDatatype (RenderRequest request, Context context){
		
		ArrayList<String> selectedDatatypes = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
		ArrayList<HashMap<String, String>> elementsDatatypes = (ArrayList<HashMap<String, String>>) getAttributeFromSession(request, ELEMENTS_DATATYPE);
    	
		if(selectedDatatypes != null && selectedDatatypes.size() > 0){
			for(int i=0; i<selectedDatatypes.size(); i++){
				String selectedDatatype = selectedDatatypes.get(i);
				// Remove special data types depend of selected data type.  
				if(elementsDatatypes != null && elementsDatatypes.size() > 0){
					for (int j=0; j<elementsDatatypes.size(); j++) {
						HashMap<String, String> elementsDatatype = elementsDatatypes.get(j);
						if(selectedDatatype.equals("law")){
							if(elementsDatatype.containsKey("www") || elementsDatatype.containsKey("research")){
								elementsDatatypes.remove(j);
								j--;
							}
						}else if(selectedDatatype.equals("research")){
							if(elementsDatatype.containsKey("www") || elementsDatatype.containsKey("law")){
								elementsDatatypes.remove(j);
								j--;
							}
						}else if(selectedDatatype.equals("www")){
							if(elementsDatatype.containsKey("research") || elementsDatatype.containsKey("law")){
								elementsDatatypes.remove(j);
								j--;
							}
						}else if(selectedDatatype.equals("topic")){
							if(elementsDatatype.containsKey("research") || elementsDatatype.containsKey("law")){
								elementsDatatypes.remove(j);
								j--;
							}
						}
					}
				}
			}
			setFacetSelectionState(context, request, "isDatatypeSelect", true);
    		context.put("selectedDatatype", selectedDatatypes);
    	} else{
    		setFacetSelectionState(context, request, "isDatatypeSelect", false);
    		context.remove("selectedDatatype");
    	}
		
		if(elementsDatatypes != null){
    		context.put("enableFaceteDatatype", elementsDatatypes);
        }
    	
	}
	
	private static void addToQueryDatatype(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
		String searchQuery = request.getParameter("q");
    	String searchDs = request.getParameter("ds");
    	boolean isWww=false;
		boolean isMetadata=false;
		boolean isAddress=false;
		boolean isLaw=false;
		boolean isResearch=false;
		boolean isFis=false;
		boolean isTopic=false;
		boolean isMeasure=false;
		boolean isService=false;
		boolean isMap=false;
		
		if(selectedDatatype != null && selectedDatatype.size() > 0){

			for(int i = 0; i < selectedDatatype.size(); i++){
        		if(selectedDatatype.get(i).equals("map")){
        		    ClauseQuery cq = new ClauseQuery(true, false);
        		    cq.addWildCardFieldQuery(new WildCardFieldQuery(false, false, "capabilities_url", "http*"));
        		    cq.addWildCardFieldQuery(new WildCardFieldQuery(false, false, "t011_obj_serv_op_connpoint.connect_point", "http*"));
        		    query.addClause(cq);
        		}else if(selectedDatatype.get(i).equals("topic")){
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_AREA_ENVTOPICS));
        		}else if(selectedDatatype.get(i).equals("other")){
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_IPLUG_DSC_OTHER));
                }else{
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, selectedDatatype.get(i)));
        		}
            }
			
			if(query.getDataTypes() != null){
	        	FieldQuery[] fieldQueries = query.getDataTypes();
				for(int i=0; i < fieldQueries.length; i++){
					FieldQuery fieldQuery = fieldQueries[i];
					String value = fieldQuery.getFieldValue();
					if(value.equals(Settings.QVALUE_DATATYPE_SOURCE_METADATA)){
						isMetadata=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_SOURCE_WWW)){
	    				isWww=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_AREA_ENVTOPICS)){
	    				isTopic=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_AREA_ADDRESS)){
	    				isAddress=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_AREA_RESEARCH)){
	    				isResearch=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_AREA_LAW)){
	    				isLaw=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_SOURCE_FIS)){
	    				isFis=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_AREA_SERVICE)){
	    				isService=true;
	    			}else if(value.equals(Settings.QVALUE_DATATYPE_AREA_MEASURES)){
	    				isMeasure=true;
	    			}
				}
				
				
			}
			
			ClauseQuery[] clauses = query.getClauses();
			for (ClauseQuery clauseQuery : clauses) {
    			if(clauseQuery.getWildCardFieldQueries() !=null){
    				WildCardFieldQuery[] wildCardFieldQueries = clauseQuery.getWildCardFieldQueries();
    				for(int i=0; i < wildCardFieldQueries.length; i++){
    					WildCardFieldQuery wildCardFieldQuery = wildCardFieldQueries[i];
    					String value = wildCardFieldQuery.getFieldName();
    					if(value.indexOf("t011_obj_serv_op_connpoint.connect_point") != -1){
    						isMap=true;
    					}
    				}
    			}
			}
			
			/* Add or Remove "datatypes" from query after selection*/
			if(isMetadata){
				query.addField(new FieldQuery(true, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_WWW));
			}
			
			if(isWww){
				query.addField(new FieldQuery(true, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_METADATA));
			}
			
			if(isAddress){
			}
			
			if(isResearch){
			}
			
			if(isLaw){
			}
			
			if(isFis){
			}

			if(isService){
			}

			if(isMeasure){
			}
			
			if(isMap){
				query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_METADATA));
				query.addField(new FieldQuery(true, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_WWW));
			}
			
			if(isTopic){
			}
        }
	}
	
	/***************************** SERVICE *********************************************/

	private static void setFaceteParamsToSessionService(ActionRequest request) {
		
		String doAddService = request.getParameter("doAddService");
		String doRemoveService = request.getParameter("doRemoveService");
		ArrayList<String> selectedService = null;
		
		if(doAddService != null){
			selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
			if(selectedService == null){
				selectedService = new ArrayList<String>();
			}
			
			boolean isFound = false;
			for(int i=0; i < selectedService.size(); i++){
				String topic = selectedService.get(i);
				if(doAddService.equals(topic)){
					isFound = true; 
					break;
				}
			}
			
			if(!isFound){
				selectedService.add(doAddService);
			}
		}
		
		if(doRemoveService != null){
			selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
			if (selectedService != null){
				for(int i = 0; i < selectedService.size(); i++){
					if(selectedService.get(i).equals(doRemoveService)){
						selectedService.remove(i);
					}
				}
			}
		}
		
		if(selectedService != null){
			setAttributeToSession(request, SELECTED_SERVICE, selectedService, true);
		}
	}
	
	private static void setParamsToContextService (RenderRequest request, Context context){
		
		ArrayList<String> selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
		ArrayList<HashMap<String, Long>> elementsService = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<HashMap<String, Long>> enableFaceteService = null;
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		List<IngridServiceRubric> dbServices = null;
				 
        if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("service")){
					if(dbServices == null){
			        	dbServices = UtilsDB.getServiceRubrics();
			        }
					break;
				}
			}
        }
		
        if(dbServices != null && elementsService != null){
			for(int i=0; i < elementsService.size();i++){
				HashMap<String, Long> topic = elementsService.get(i);
				boolean found = false;
				for (Iterator<String> iterator = topic.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					for(int j=0; j < dbServices.size(); j++){
						IngridServiceRubric service = dbServices.get(j);
						String ident = service.getQueryValue();
						if(key.equals(ident)){
							found = true;
							break;
						}
					}
				}
				if(found){
					if(enableFaceteService == null){
						enableFaceteService = new ArrayList<HashMap<String,Long>>();
					}
					enableFaceteService.add(topic);
				}
			}
		}
		
		if(enableFaceteService != null){
    		context.put("enableFaceteService", enableFaceteService);
		}
		
		if(selectedService != null && selectedService.size() > 0){
			setFacetSelectionState(context, request, "isServiceSelect", true);
    		context.put("selectedService", selectedService);
    	} else{
    		setFacetSelectionState(context, request, "isServiceSelect", false);
    		context.remove("selectedService");
    	}
    	
    	
	}
	
	private static void addToQueryService(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
    	
		if(selectedService != null && selectedService.size() > 0){
	    	
	    	ClauseQuery cq  = new ClauseQuery(true, false);
	        
           	for (int i = 0; i < selectedService.size(); i++) {
           		String queryValue = UtilsDB.getServiceRubricFromKey((String) selectedService.get(i));
           		cq.addField(new FieldQuery(false, false, Settings.QFIELD_RUBRIC, queryValue));
            }
            query.addClause(cq);
	    }
	}
	
	/***************************** MESSWERTE *******************************************/

	private static void setFaceteParamsToSessionMeasures(ActionRequest request) {
		
		String doAddMeasures = request.getParameter("doAddMeasures");
		String doRemoveMeasures = request.getParameter("doRemoveMeasures");
		ArrayList<String> selectedMeasures = null;
		
		if(doAddMeasures != null){
			selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
			if(selectedMeasures == null){
				selectedMeasures = new ArrayList<String>();
			}
			
			boolean isFound = false;
			for(int i=0; i < selectedMeasures.size(); i++){
				String topic = selectedMeasures.get(i);
				if(doAddMeasures.equals(topic)){
					isFound = true; 
					break;
				}
			}
			
			if(!isFound){
				selectedMeasures.add(doAddMeasures);
			}
		}
		
		if(doRemoveMeasures != null){
			selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
			if (selectedMeasures != null){
				for(int i = 0; i < selectedMeasures.size(); i++){
					if(selectedMeasures.get(i).equals(doRemoveMeasures)){
						selectedMeasures.remove(i);
					}
				}
			}
		}

		if(selectedMeasures != null){
			setAttributeToSession(request, SELECTED_MEASURES, selectedMeasures, true);
		}
	}
		
	
	private static void setParamsToContextMeasures (RenderRequest request, Context context){
		
		ArrayList<String> selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
		ArrayList<HashMap<String, Long>> elementsMeasure = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<HashMap<String, Long>> enableFaceteMeasures = null;
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		List<IngridMeasuresRubric> dbMeasures = null;
		
        if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("measure")){
					if(dbMeasures == null){
			        	dbMeasures = UtilsDB.getMeasuresRubrics();
			        }
			        break;
				}
			}
        }
				
		if(dbMeasures != null && elementsMeasure != null){
			for(int i=0; i < elementsMeasure.size();i++){
				HashMap<String, Long> topic = elementsMeasure.get(i);
				boolean found = false;
				for (Iterator<String> iterator = topic.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					for(int j=0; j < dbMeasures.size(); j++){
						IngridMeasuresRubric measure = dbMeasures.get(j);
						String ident = measure.getQueryValue();
						if(key.equals(ident)){
							found = true;
							break;
						}
					}
				}
				if(found){
					if(enableFaceteMeasures == null){
						enableFaceteMeasures = new ArrayList<HashMap<String,Long>>();
					}
					enableFaceteMeasures.add(topic);
				}
			}
		}
		
		if(enableFaceteMeasures != null){
    		context.put("enableFaceteMeasures", enableFaceteMeasures);
		}
		
    	if(selectedMeasures != null && selectedMeasures.size() > 0){
    		setFacetSelectionState(context, request, "isMeasuresSelect", true);
    		context.put("selectedMeasures", selectedMeasures);
    	} else{
    		setFacetSelectionState(context, request, "isMeasuresSelect", false);
    		context.remove("selectedMeasures");
    	}
    	
	}
	
	private static void addToQueryMeasures(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
    	
		if(selectedMeasures != null && selectedMeasures.size() > 0){
	    	
			// RUBRIC
	        ClauseQuery cq = new ClauseQuery(true, false);
            for (int i = 0; i < selectedMeasures.size(); i++) {
            	String queryValue = UtilsDB.getMeasuresRubricFromKey((String) selectedMeasures.get(i));
                cq.addField(new FieldQuery(false, false, Settings.QFIELD_RUBRIC, queryValue));
            }
            query.addClause(cq);
	    }
	}
		
	/***************************** PARTNER *********************************************/

	private static void setFaceteQueryParamsPartner(ArrayList<IngridDocument> list) {
		IngridDocument faceteEntry = new IngridDocument();
        faceteEntry.put("id", "partner");
        
        list.add(faceteEntry);
	}
	
	private static void setFaceteParamsToSessionPartner(ActionRequest request) {
		
		String doPartner = request.getParameter("doPartner");
		String doRemovePartner = request.getParameter("doRemovePartner");
		
		if(doPartner != null){
			List<IngridPartner> partners = UtilsDB.getPartners();
			ArrayList<IngridPartner> selectedPartner = new ArrayList<IngridPartner>();
			for(int j=0; j < partners.size(); j++){
				IngridPartner partner = (IngridPartner) partners.get(j);
				if(partner.getIdent().equals(doPartner)){
					selectedPartner.add(partner);
				}
			}
			if(selectedPartner != null){
				setAttributeToSession(request, ENABLE_FACETE_PARTNER_LIST, selectedPartner, true);
			}
        }
		
		if(doRemovePartner != null){
			removeAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
			removeAttributeFromSession(request, SELECTED_PROVIDER);
		}
	}
		
	private static void setParamsToContextPartner (RenderRequest request, Context context){
		
		ArrayList<IngridPartner> enableFacetePartnerList = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
		
		if(enableFacetePartnerList != null){
    		context.put("enableFacetePartnerList", enableFacetePartnerList);
    		setFacetSelectionState(context, request, "isPartnerSelect", true);
		}else{
			setFacetSelectionState(context, request, "isPartnerSelect", false);
			ArrayList<HashMap<String, Long>> elementsPartner = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_PARTNER);
			if(elementsPartner != null){
				ArrayList<String> keys = new ArrayList<String>();
				for(int i=0; i < elementsPartner.size(); i++){
					List<String> mapKeys = new ArrayList<String>(elementsPartner.get(i).keySet());
					for(int j=0; j < mapKeys.size(); j++){
						keys.add(mapKeys.get(j));
					}
				}

				if(keys != null){
					LinkedHashMap partnerProvider = UtilsDB.getPartnerProviderMap(keys); 
					enableFacetePartnerList = new ArrayList<IngridPartner>();
					if(partnerProvider != null){
						for (int i=0; i < keys.size(); i++){
							HashMap partner = (HashMap) partnerProvider.get(keys.get(i));
							if(partner != null){
								enableFacetePartnerList.add((IngridPartner)partner.get("partner"));
							}
						}
					}
					context.put("enableFacetePartnerList",  enableFacetePartnerList);
					context.put("elementsPartner",  elementsPartner);
				}
				
			}
		}
	}
	
	
	private static void addToQueryPartner(PortletRequest request, IngridQuery query) {
		
		ArrayList<IngridPartner> selectedPartner = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
    	
		if(selectedPartner != null && selectedPartner.size() > 0){
			for(int i=0; i<selectedPartner.size();i++){
				IngridPartner partner = (IngridPartner) selectedPartner.get(i);
				query.addField(new FieldQuery(true, false, Settings.QFIELD_PARTNER, partner.getIdent()));
			}
		}
	}
   
	/***************************** ANBIETER ********************************************/

	private static void setFaceteQueryParamsProvider(ArrayList<IngridDocument> list, PortletRequest request) {
		List<IngridPartner> partners = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
		if(partners != null && partners.size() > 0){
			IngridDocument faceteEntry = new IngridDocument();
	        faceteEntry.put("id", "provider");
	        
	        list.add(faceteEntry);
		}
	}

	private static void setFaceteParamsToSessionProvider(ActionRequest request) {
		
		String doAddProvider = request.getParameter("doAddProvider");
		String doAddProviderChb = request.getParameter("doAddProviderChb");
		String doRemoveProvider = request.getParameter("doRemoveProvider");
		String doRemoveAllProvider = request.getParameter("doRemoveAllProvider");
		
		ArrayList<String> selectedIds  = null;
		
		ArrayList<IngridProvider> selectedPartnerProviders = new ArrayList<IngridProvider>();
		ArrayList<IngridPartner> selectedPartner = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
		if(selectedPartner != null){
			for(int i=0; i < selectedPartner.size(); i++){
				IngridPartner partner = selectedPartner.get(i);
				List<IngridProvider> providers = UtilsDB.getProviders();
				for(int j=0; j<providers.size(); j++){
					IngridProvider provider = providers.get(j);
					if(provider != null){
						if (partner.getSortkey() == provider.getSortkeyPartner()){
							selectedPartnerProviders.add(provider);
						}
					}
				}
			}
		}
		
		if(doAddProvider != null){
			int listSize = selectedPartnerProviders.size();
			selectedIds = new ArrayList<String>();
            for (int i=0; i< listSize; i++) {
                String chkVal = request.getParameter("chk"+(i+1));
                if (chkVal != null) {
                	selectedIds.add(chkVal);
                }
            }
            
            // Set grouping to "grouped_off"
            if(SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING) != null){
            	String grouping = SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING).toString();
                if(grouping != null){
                	if(grouping != IngridQuery.GROUPED_OFF && !grouping.equals(IngridQuery.GROUPED_OFF)){
                		setAttributeToSession(request, SESSION_PARAMS_FACET_GROUPING, grouping);
                	}
                }
            }
            SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, IngridQuery.GROUPED_OFF, Settings.MSG_TOPIC_SEARCH);
		}
		
		if(doAddProviderChb != null){
			selectedIds = new ArrayList<String>();
            selectedIds.add(doAddProviderChb);
            
            // Set grouping to "grouped_off"
            if(SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING) != null){
	            String grouping = SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING).toString();
	            if(grouping != null){
	            	if(grouping != IngridQuery.GROUPED_OFF && !grouping.equals(IngridQuery.GROUPED_OFF)){
	            		setAttributeToSession(request, SESSION_PARAMS_FACET_GROUPING, grouping);
	            	}
	            }
            }
            SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, IngridQuery.GROUPED_OFF, Settings.MSG_TOPIC_SEARCH);
            
		}
		
		if(doRemoveProvider != null){
			selectedIds = (ArrayList<String>) getAttributeFromSession(request, SELECTED_PROVIDER);
			if (selectedIds != null){
				for(int i = 0; i < selectedIds.size(); i++){
					if(selectedIds.get(i).equals(doRemoveProvider)){
						selectedIds.remove(i);
					}
				}
			}
			
            // Revert setting of grouping to "grouped_off"
			if(selectedIds != null && selectedIds.size() == 0){
			   	String grouping = (String) getAttributeFromSession(request, SESSION_PARAMS_FACET_GROUPING);
	           	if(grouping != null){
	           		SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, grouping, Settings.MSG_TOPIC_SEARCH);
	           	}
			}
		}
		
		if(selectedIds != null){
        	setAttributeToSession(request, SELECTED_PROVIDER, selectedIds, true);
        }
		
		if(doRemoveAllProvider != null){
			removeAttributeFromSession(request, SELECTED_PROVIDER);
		}
	}

	private static void setParamsToContextProvider (RenderRequest request, Context context){
		
		ArrayList<String> selectedProviders = (ArrayList<String>) getAttributeFromSession(request, SELECTED_PROVIDER);
		List<IngridPartner> partners = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
		List<IngridProvider> providers = null;
		ArrayList<IngridProvider> enableFaceteProviderList = null;
		
		// TODO: (Facete) Add providers to facete
		if(partners != null && partners.size() == 1) {
	    	providers = UtilsDB.getProvidersFromPartnerKey(partners.get(0).getIdent());
		}
		
		ArrayList<HashMap<String, Long>> elementsProvider = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_PROVIDER);
		
		// Remove unselected provider with facets (selection return more than one provider facet) 
		if(selectedProviders != null && elementsProvider != null){
			for(int j=0; j<elementsProvider.size();j++){
				HashMap<String, Long> elementProvider = elementsProvider.get(j);
				for (Iterator<String> iterator = elementProvider.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					boolean foundKey = false;
					if(selectedProviders!= null && selectedProviders.size() > 0){
						for (int i=0; i<selectedProviders.size(); i++){
							String providerKey = selectedProviders.get(i);
							if(key.equals(providerKey)){
								foundKey = true;
								break;
							}
						}
						if(foundKey){
							break;
						}else{
							elementsProvider.remove(j);
						}
					}
				}
			}
		}
		
		// Add zero facet number for selected provider (selection have no facet)
		if(selectedProviders != null){
			for(int i=0; i< selectedProviders.size(); i++){
				String selectedKey = selectedProviders.get(i);
				boolean foundKey = false;
				if(elementsProvider != null){
					for(int j=0; j< elementsProvider.size(); j++){
						HashMap<String, Long> provider = elementsProvider.get(j);
						for (Iterator<String> iterator = provider.keySet().iterator(); iterator.hasNext();) {
							String key = iterator.next();
							if(key.equals(selectedKey)){
								foundKey = true;
								break;
							}
						}
						if(foundKey){
							break;
						}
					}
					if(!foundKey){
						HashMap<String, Long> addingProvider = new HashMap<String, Long>();
						addingProvider.put(selectedKey, (long) 0);
						elementsProvider.add(addingProvider);
					}
				}else{
					elementsProvider = new ArrayList<HashMap<String,Long>>();
					HashMap<String, Long> addingProvider = new HashMap<String, Long>();
					addingProvider.put(selectedKey, (long) 0);
					elementsProvider.add(addingProvider);
				}
			}
		}
		
    	if(providers != null && elementsProvider != null){
    		for(int i=0; i < elementsProvider.size(); i++){
    			HashMap<String, Long> map = elementsProvider.get(i);
    			List<String> keys = new ArrayList<String>(map.keySet());
    			String ident = keys.get(0);
    			for(int j=0; j < providers.size(); j++){
    				if(ident != null){
    					IngridProvider prov = providers.get(j);
    					String provIdent = prov.getIdent(); 
    					
        				if(ident.equals(provIdent)){
        					if(enableFaceteProviderList == null){
        						enableFaceteProviderList = new ArrayList<IngridProvider>();
            				}
        					enableFaceteProviderList.add(prov);
        					providers.remove(j);
        					break;
        				}
        			}
    			}
    		}
	    }
		
    	if(enableFaceteProviderList == null){
    		ArrayList<HashMap<String, Long>> elementsPartner = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_PARTNER);
    		if(elementsPartner != null && elementsPartner.size() > 0){
				for(int i=0; i < elementsPartner.size(); i++){
					HashMap<String, Long> map = elementsPartner.get(i);
	    			List<String> keys = new ArrayList<String>(map.keySet());
		    		enableFaceteProviderList = (ArrayList<IngridProvider>) UtilsDB.getProvidersFromPartnerKey(keys.get(0));
				}
			}
		}
		
    	context.put("enableFaceteProviderList", enableFaceteProviderList);
    	context.put("elementsProvider", elementsProvider);
    	context.put("enableFaceteProviderCount", PortalConfig.getInstance().getInt("portal.search.facete.provider.count", 3));
    	
    	if(selectedProviders != null && selectedProviders.size() > 0){
    		setFacetSelectionState(context, request, "isProviderSelect", true);
    		context.put("selectedProvider", selectedProviders);
    		if(providers != null && providers.size() > 0){
    			context.put("unselectedProvider", providers);
    		}
    	} else{
    		setFacetSelectionState(context, request, "isProviderSelect", false);
    		context.remove("selectedProvider");
    	}
	}
	
	private static void addToQueryProvider(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedProvider = (ArrayList<String>) getAttributeFromSession(request, SELECTED_PROVIDER);
		
		if(selectedProvider != null && selectedProvider.size() > 0){
			for(int i=0; i<selectedProvider.size();i++){
				query.addField(new FieldQuery(true, false, Settings.QFIELD_PROVIDER, selectedProvider.get(i)));
			}
			//query.addField(new FieldQuery(true, false, Settings.QFIELD_GROUPED, "grouped_off"));
		}
	}
	
	/***************************** ZEITBEZUG *******************************************/

	private static void setFaceteQueryParamsTime(ArrayList<IngridDocument> list, PortletRequest request) {
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("metadata")){
					IngridDocument facete = new IngridDocument();
			        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
				    
			        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					Calendar cal;
					
					cal = new GregorianCalendar();
			   		String timeNow = df.format(cal.getTime());
			        
			       	HashMap<String, String> faceteEntry = new HashMap<String, String>();
			       	
			       	if((String) getAttributeFromSession(request, "doTime") != null){
			       		int doTime = Integer.valueOf((String) getAttributeFromSession(request, "doTime"));
				       	switch (doTime) {
				       		case 1:
				       			faceteEntry.put("id", "modtime1");
						   		cal = new GregorianCalendar();
						   		cal.add(Calendar.MONTH, -1);
							    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
							    faceteList.add(faceteEntry);

								break;
				       		case 2:
				       			faceteEntry = new HashMap<String, String>();
							    faceteEntry.put("id", "modtime2");
							    cal = new GregorianCalendar();
						   		cal.add(Calendar.MONTH, -1);
						   		cal.add(Calendar.DAY_OF_MONTH, -1);
						   		timeNow = df.format(cal.getTime());
							    cal = new GregorianCalendar();
							    cal.add(Calendar.MONTH, -3);
							    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
							    faceteList.add(faceteEntry);	

								break;
				       		case 3:
				       			faceteEntry = new HashMap<String, String>();
							    faceteEntry.put("id", "modtime3");
							    cal = new GregorianCalendar();
						   		cal.add(Calendar.MONTH, -3);
						   		cal.add(Calendar.DAY_OF_MONTH, -1);
						   		timeNow = df.format(cal.getTime());
							    cal = new GregorianCalendar();
							    cal.add(Calendar.YEAR, -1);
							    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
							    faceteList.add(faceteEntry);	

								break;
				       		case 4:
				       			faceteEntry = new HashMap<String, String>();
							    faceteEntry.put("id", "modtime4");
							    cal = new GregorianCalendar();
							    cal.add(Calendar.YEAR, -1);
							    cal.add(Calendar.DAY_OF_MONTH, -1);
						   		timeNow = df.format(cal.getTime());
							    cal = new GregorianCalendar();
							    cal.add(Calendar.YEAR, -5);
							    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
							    faceteList.add(faceteEntry);
							    
								break;
			
							default:
							    
							    break;
						}
			       	}else{
			       		faceteEntry.put("id", "modtime1");
				   		cal = new GregorianCalendar();
				   		cal.add(Calendar.MONTH, -1);
					    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
					    faceteList.add(faceteEntry);
					    
					    faceteEntry = new HashMap<String, String>();
					    faceteEntry.put("id", "modtime2");
					    cal = new GregorianCalendar();
				   		cal.add(Calendar.MONTH, -1);
				   		cal.add(Calendar.DAY_OF_MONTH, -1);
				   		timeNow = df.format(cal.getTime());
					    cal = new GregorianCalendar();
					    cal.add(Calendar.MONTH, -3);
					    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
					    faceteList.add(faceteEntry);	
					
					    faceteEntry = new HashMap<String, String>();
					    faceteEntry.put("id", "modtime3");
					    cal = new GregorianCalendar();
				   		cal.add(Calendar.MONTH, -3);
				   		cal.add(Calendar.DAY_OF_MONTH, -1);
				   		timeNow = df.format(cal.getTime());
					    cal = new GregorianCalendar();
					    cal.add(Calendar.YEAR, -1);
					    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
					    faceteList.add(faceteEntry);	
					
					    faceteEntry = new HashMap<String, String>();
					    faceteEntry.put("id", "modtime4");
					    cal = new GregorianCalendar();
					    cal.add(Calendar.YEAR, -1);
					    cal.add(Calendar.DAY_OF_MONTH, -1);
				   		timeNow = df.format(cal.getTime());
				   		cal = new GregorianCalendar();
					    cal.add(Calendar.YEAR, -5);
					    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
					    faceteList.add(faceteEntry);
			       	}
			       	
			        facete.put("id", "modtime");
			        facete.put("classes", faceteList);
			        
			        list.add(facete);
				}
			}
		}
	}
	
	private static void setFaceteParamsToSessionTime(ActionRequest request) {
		
		String doTime = request.getParameter("doTime");
		
		if(doTime != null && doTime.length() > 0){
			if(doTime.equals("0")){
				if(getAttributeFromSession(request, "doTime") != null){
					removeAttributeFromSession(request, "doTime");
				}
			}else{
				setAttributeToSession(request, "doTime", doTime, true);
			}
		}
	}
	
	private static void setParamsToContextTime (RenderRequest request, Context context){
		String doTime = (String) getAttributeFromSession(request, "doTime");
		
		if(doTime != null && doTime.length() > 0){
			setFacetSelectionState(context, request, "isTimeSelect", true);
			context.put("doTime", doTime);
		}else{
			setFacetSelectionState(context, request, "isTimeSelect", false);
		}
	}
	
	private static void addToQueryTime(PortletRequest request, IngridQuery query) {
		String doTime = (String) getAttributeFromSession(request, "doTime");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar cal;
		
		cal = new GregorianCalendar();
   		String timeNow = df.format(cal.getTime());
        
		
		if(doTime != null){
            if (doTime.equals("1")) {
            	// letzten Monat
            	cal = new GregorianCalendar();
            	cal.add(Calendar.MONTH, -1);
            }else if(doTime.equals("2")){
            	cal.add(Calendar.MONTH, -1);
		   		cal.add(Calendar.DAY_OF_MONTH, -1);
		   		timeNow = df.format(cal.getTime());
			    // letzten 3 Monate
            	cal = new GregorianCalendar();
            	cal.add(Calendar.MONTH, -3);
            }else if(doTime.equals("3")){
            	cal.add(Calendar.MONTH, -3);
		   		cal.add(Calendar.DAY_OF_MONTH, -1);
		   		timeNow = df.format(cal.getTime());
			    // letztes Jahr
            	cal = new GregorianCalendar();
            	cal.add(Calendar.YEAR, -1);
            }else if(doTime.equals("4")){
            	cal.add(Calendar.YEAR, -1);
		   		cal.add(Calendar.DAY_OF_MONTH, -1);
		   		timeNow = df.format(cal.getTime());
			    // letzte 5 Jahre
            	cal = new GregorianCalendar();
            	cal.add(Calendar.YEAR, -5);
            }
            query.addRangeQuery(new RangeQuery(true, false, "t0113_dataset_reference.reference_date", df.format(cal.getTime()) + "0*", timeNow + "9*", true));
        }	
	}
	
	/***************************** KARTE ***********************************************/
	/*
	private static void setFaceteQueryParamsMap(ArrayList<IngridDocument> list, PortletRequest request) {
		IngridDocument facete = new IngridDocument();
        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
	    
        HashMap selectedMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);
		if(selectedMap != null){
			ArrayList<String> coordOptions = (ArrayList<String>) selectedMap.get("coordOptions");
			HashMap<String, String> webmapclientCoords = (HashMap<String, String>) selectedMap.get("webmapclientCoords");
			if(coordOptions != null && coordOptions.size() > 0 
					&& webmapclientCoords != null && webmapclientCoords.size() > 0){
				for(int i=0; i<coordOptions.size(); i++){
					String coordOption = coordOptions.get(i);
					
					HashMap<String, String> faceteEntry = new HashMap<String, String>();
				    faceteEntry.put("id", coordOption);
				    faceteEntry.put("query", "x1:" + webmapclientCoords.get("x1") + " y1:" + webmapclientCoords.get("y1") + " x2:" + webmapclientCoords.get("x2") + " y2:" + webmapclientCoords.get("y2") + " coord:" + coordOption);
				    faceteList.add(faceteEntry);
				}
				facete.put("id", "coords");
				facete.put("classes", faceteList);
		        
				list.add(facete);

			}
		}
	}
	*/
	
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
	
	private static void setFaceteQueryParamsGeothesaurus(ArrayList<IngridDocument> list, PortletRequest request) {
		IngridDocument facete = new IngridDocument();
        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
	    
        HashMap selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
		if(selectedGeothesaurus != null && selectedGeothesaurus.size() > 0){
			ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get("geothesaurusSelectTopicsId");
			if(selectedIds != null){
				for(int i=0; i < selectedIds.size(); i++){
					String id = selectedIds.get(i);
					HashMap<String, String> faceteEntry = new HashMap<String, String>();
				    faceteEntry.put("id", id);
				    faceteEntry.put("query", "areaid:" + id);
				    faceteList.add(faceteEntry);
				}
				facete.put("id", "geothesaurus");
				facete.put("classes", faceteList);
		        
				list.add(facete);
			}
		}
	}

	
	/**
	 * Action Params for "Search Geothesaurus
	 * 
	 * @param request
	 */
	
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

	/***************************** Thesaurus *****************************************/
	/*  
	 * Not in used
	*/
	/*
	private static void setFaceteParamsToSessionThesaurus(ActionRequest request) {
		
		String doThesaurus = request.getParameter("doThesaurus");
		String doCancelThesaurus = request.getParameter("doCancelThesaurus");
		String doAddThesaurus = request.getParameter("doAddThesaurus");
		String doSearchThesaurus = request.getParameter("doSearchThesaurus");
		String doBrowseThesaurus = request.getParameter("doBrowseThesaurus");
		String doBrowseSimilarThesaurus = request.getParameter("doBrowseSimilarThesaurus");
		String doRemoveThesaurus = request.getParameter("doRemoveThesaurus");
		
		if(doCancelThesaurus != null){
			setAttributeToSession(request, THESAURUS_DO, false);
			request.removeAttribute("doCancelThesaurus");
		}else{
        	
        	if(doThesaurus != null){
				if(doThesaurus.equals("true")){
					setAttributeToSession(request, THESAURUS_DO, true);
				}else{
					setAttributeToSession(request, THESAURUS_DO, false);
				}
			}
			
			if(doAddThesaurus != null){
				setAttributeToSession(request, THESAURUS_DO, false);
				if(getAttributeFromSession(request, THESAURUS_LIST_SIZE) != null){
		        	String listSize = getAttributeFromSession(request, THESAURUS_LIST_SIZE).toString();
	        		ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, THESAURUS_SELECTED_TOPICS_IDS);
	        			if(selectedIds == null){
	        				selectedIds = new ArrayList<String> ();
	        			}
	                for (int i=0; i< Integer.parseInt(listSize); i++) {
	                    String chkVal = request.getParameter("chk"+(i+1));
	                    if (chkVal != null) {
	                    	selectedIds.add(chkVal);
	                    }
	                }
	                if(selectedIds != null){
	                	setAttributeToSession(request, THESAURUS_SELECTED_TOPICS_IDS, selectedIds, true);
	                }
	        	}
				
				ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
				if(thesaurusSelectTopics != null && thesaurusSelectTopics.size() > 0){
					setAttributeToSession(request, THESAURUS_SELECTED_TOPICS, thesaurusSelectTopics);
				}
			}
			
			if(doRemoveThesaurus != null && doAddThesaurus == null){
				if(doRemoveThesaurus.equals("all")){
					removeAttributeFromSession(request, THESAURUS_SELECTED_TOPICS_IDS);
				}else{
					ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, THESAURUS_SELECTED_TOPICS_IDS);
					if (selectedIds != null){
						for(int i = 0; i < selectedIds.size(); i++){
							if(selectedIds.get(i).equals(doRemoveThesaurus)){
								selectedIds.remove(i);
							}
						}
						setAttributeToSession(request, THESAURUS_SELECTED_TOPICS_IDS, selectedIds, true);
					}
				}
				setAttributeToSession(request, THESAURUS_DO, false);
			}
			if (doSearchThesaurus != null){
				setAttributeToSession(request, THESAURUS_DO, true);
				removeAttributeFromSession(request, THESAURUS_TOPICS);
				removeAttributeFromSession(request, THESAURUS_SIMILAR_TOPICS);
				removeAttributeFromSession(request, THESAURUS_CURRENT_TOPIC);
				
				String thesaurusTerm = null;
				SearchExtEnvTopicThesaurusForm f = (SearchExtEnvTopicThesaurusForm) Utils.getActionForm(request, SearchExtEnvTopicThesaurusForm.SESSION_KEY, SearchExtEnvTopicThesaurusForm.class);        
	            f.clearErrors();
	            f.populate(request);
	            if (f.validate()) {
	            	thesaurusTerm = f.getInput(SearchExtEnvTopicThesaurusForm.FIELD_SEARCH_TERM);
	            	if(thesaurusTerm != null){
	            		setAttributeToSession(request, THESAURUS_TERM, thesaurusTerm);
	        		}
	        		
	            }
	            
	            if(thesaurusTerm != null){
	            	IngridHit[] topics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(thesaurusTerm, "/thesa", request.getLocale());
	                if (topics != null && topics.length > 0) {
	                	for (int i=0; i<topics.length; i++) {
	                		String href = UtilsSearch.getDetailValue(topics[i], "href");
	                		if (href != null && href.lastIndexOf("#") != -1) {
	                			topics[i].put("topic_ref", href.substring(href.lastIndexOf("#")+1));
	                		}
	                		addToListOfTopicsThesaurus(topics[i], request);
	                    }
	                	setAttributeToSession(request, THESAURUS_TOPICS, topics);
	                	setAttributeToSession(request, THESAURUS_LIST_SIZE, topics.length);
	                	setAttributeToSession(request, THESAURUS_ERROR, false);
	                }else{
	                	setAttributeToSession(request, THESAURUS_ERROR, true);
	                }
	            }
	        }
	        
	        if(doBrowseThesaurus != null || doBrowseSimilarThesaurus != null){
	        	setAttributeToSession(request, THESAURUS_DO, true);
	        	String topicID = request.getParameter("topicID");
	            String plugID = request.getParameter("plugID");
	            String docID = request.getParameter("docID");
	            
	            Topic topic = new Topic();
	            topic.setDocumentId(Integer.parseInt(docID));
	            topic.setPlugId(plugID);
	            topic.setTopicID(topicID);
	            
	            Topic currentTopic = (Topic)SNSSimilarTermsInterfaceImpl.getInstance().getDetailsTopic(topic, "/thesa", request.getLocale());
	            setAttributeToSession(request, THESAURUS_CURRENT_TOPIC, currentTopic);
	        	IngridHit[] assocTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(topicID, request.getLocale());
	        	for (int j=0; j<assocTopics.length; j++) {
	        		addToListOfTopicsThesaurus(assocTopics[j], request);
                }
	        	setAttributeToSession(request, THESAURUS_SIMILAR_TOPICS, Arrays.asList(assocTopics));
	        	setAttributeToSession(request, THESAURUS_LIST_SIZE, assocTopics.length + 1);
	        }
	        
	    	ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
	        setAttributeToSession(request, THESAURUS_SELECTED_TOPICS, thesaurusSelectTopics);
		}
	}
	
	private static void setParamsToContextThesaurus (RenderRequest request, Context context){

		// Nach Raumbezug suchen
        IngridHit[] thesaurusTopics = (IngridHit []) getAttributeFromSession(request, THESAURUS_TOPICS);
        context.put("thesaurusTopics", thesaurusTopics);
        context.put("thesaurusTopicsBrowse", getAttributeFromSession(request, THESAURUS_SIMILAR_TOPICS));
        context.put("thesaurusCurrentTopic", getAttributeFromSession(request, THESAURUS_CURRENT_TOPIC));
        context.put("list_size", getAttributeFromSession(request, THESAURUS_LIST_SIZE));
        ArrayList<HashMap<String, String>> thesaurusSelectTopics = (ArrayList<HashMap<String, String>>) getAttributeFromSession(request, THESAURUS_SELECTED_TOPICS);
        if(thesaurusSelectTopics != null && thesaurusSelectTopics.size() > 0){
        	setFacetSelectionState(context, request, "isThesaurusSelect", true);
        	context.put("thesaurusSelectTopics", getAttributeFromSession(request, THESAURUS_SELECTED_TOPICS));
        }else{
        	setFacetSelectionState(context, request, "isThesaurusSelect", false);
        }
        context.put("doThesaurus", getAttributeFromSession(request, THESAURUS_DO));
        context.put("thesaurusTerm", getAttributeFromSession(request, THESAURUS_TERM));
        
        ArrayList<Topic> narrowerTermAssoc = new ArrayList<Topic>();
        ArrayList<Topic> widerTermAssoc = new ArrayList<Topic>();
        ArrayList<Topic> synonymAssoc = new ArrayList<Topic>();
        ArrayList<Topic> relatedTermsAssoc = new ArrayList<Topic>();
        
        Object obj = getAttributeFromSession(request, THESAURUS_SIMILAR_TOPICS);
        if (obj != null) {
            List topics = (List)obj;
            for (int i=0; i<topics.size(); i++) {
                Topic t = (Topic)topics.get(i);
                String assoc = t.getTopicAssoc();
                if (assoc.indexOf("narrowerTermMember") != -1) {
                    narrowerTermAssoc.add(t);
                } else if (assoc.indexOf("widerTermMember") != -1) {
                    widerTermAssoc.add(t);
                } else if (assoc.indexOf("synonymMember") != -1) {
                    synonymAssoc.add(t);
                } else if (assoc.indexOf("descriptorMember") != -1) {
                    relatedTermsAssoc.add(t);
                }
            }
            context.put("list_size", new Integer(topics.size() + 1));
        }
        
        if(narrowerTermAssoc != null && narrowerTermAssoc.size() > 0)
        	context.put("thesaurusNarrowerTerm", narrowerTermAssoc);
        if(widerTermAssoc != null && widerTermAssoc.size() > 0)
        	context.put("thesaurusWiderTerm", widerTermAssoc);
        if(synonymAssoc != null && synonymAssoc.size() > 0)
        	context.put("thesaurusSynonym", synonymAssoc);
        if(relatedTermsAssoc != null && relatedTermsAssoc.size() > 0)
            context.put("thesaurusRelatedTerms", relatedTermsAssoc);
        
        context.put("thesaurusError", getAttributeFromSession(request, THESAURUS_ERROR));
	}

	
	private static void addToQueryThesaurus(PortletRequest request, IngridQuery query) {

		ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
        
		if(thesaurusSelectTopics != null && thesaurusSelectTopics.size() > 0){
			ClauseQuery cq = null;
    		for (int i = 0; i < thesaurusSelectTopics.size(); i++) {
    			HashMap<String, String> map = (HashMap<String, String>) thesaurusSelectTopics.get(i);
    			if (map != null) {
    				String term = (String) map.get("topicTitle");
    				switch (i) {
					case 0:
						if(thesaurusSelectTopics.size() == 1){
		    				if(term != null){
			            		query.addTerm(new TermQuery(true, false, term));
			            	}
	    				}else{
	    					if(term != null){
	    						if(cq == null){
	        						cq = new ClauseQuery(true, false);
	        					}
	        		   	       	cq.addTerm(new TermQuery(true, false, term));
	    					}
	    				}
						break;
					default:
						cq.setLastQueryUnrequired(); 
						cq.setLastWasAnd(false);
						cq.addTerm(new TermQuery(false, false, term));
						break;
					}
	    		}
    		}
    		if(cq != null){
    			query.addClause(cq);
    		}
	    }
	}
	
	private static ArrayList<HashMap<String, String>> getSelectedThesaurusTopics(PortletRequest request){
		ArrayList<IngridHit> allThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, THESAURUS_ALL_TOPICS);
		ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, THESAURUS_SELECTED_TOPICS_IDS);
		ArrayList<HashMap<String, String>> thesaurusSelectTopics = new ArrayList<HashMap<String, String>>();
		
		if(allThesaurusTopics != null && selectedIds != null){
			for(int i=0; i<selectedIds.size(); i++){
				for(int j = 0; j < allThesaurusTopics.size(); j++){
					Topic topic = (Topic) allThesaurusTopics.get(j);
					String topicId = (String) topic.get("topicID");
        			if(topicId != null){
        				if(topicId.indexOf((String)selectedIds.get(i)) > -1){
                        	HashMap<String, String> map = new HashMap<String, String>();
                			map.put("topicTitle", topic.get("topicName").toString());
                			map.put("topicId", topicId);
                			thesaurusSelectTopics.add(map);
                			break;
                		}
        			}
				}
        	}
		}
        return thesaurusSelectTopics;
	}
	
	private static void addToListOfTopicsThesaurus(IngridHit ingridHit, ActionRequest request) {
		ArrayList<IngridHit> allThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, THESAURUS_ALL_TOPICS); 
		if(allThesaurusTopics == null){
			allThesaurusTopics = new ArrayList<IngridHit>();
		}
		
		boolean isFound = false;
		for(int i=0; i < allThesaurusTopics.size(); i++){
			IngridHit hit = allThesaurusTopics.get(i);
			if(ingridHit.getId()==hit.getId()){
				isFound = true;
				break;
			}
		}
		if(!isFound){
			allThesaurusTopics.add(ingridHit);	
		}
		setAttributeToSession(request, THESAURUS_ALL_TOPICS, allThesaurusTopics);
	}
	 */
	
	
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
		request.getPortletSession().removeAttribute(key, PortletSessionImpl.APPLICATION_SCOPE);
	}
	
	public static void setAttributeToSession(PortletRequest request, String key, Object value){
		setAttributeToSession(request, key, value, false);
	}
	
	private static void setAttributeToSession(PortletRequest request, String key, Object value, boolean isSelection){
		ArrayList<String> faceteSessionKeys = (ArrayList<String>) request.getPortletSession().getAttribute("faceteSessionKeys");
		
		if(faceteSessionKeys == null){
			faceteSessionKeys = new ArrayList<String>();
		}
		if(faceteSessionKeys.size() == 0){
			faceteSessionKeys.add(key);
		}else{
			boolean isFound = false;
			for(int i=0; i < faceteSessionKeys.size(); i++){
				String faceteSessionKey = faceteSessionKeys.get(i);
				if(faceteSessionKey.equals(key)){
					isFound = true;
					break;
				}
			}
			if(!isFound){
				faceteSessionKeys.add(key);
			}
		}
		
		request.getPortletSession().setAttribute("faceteSessionKeys", faceteSessionKeys, PortletSessionImpl.APPLICATION_SCOPE);
		if(isSelection){
			HashMap faceteLastSelection = new HashMap();
			faceteLastSelection.put(key, value);
			request.getPortletSession().setAttribute("faceteLastSelection", faceteLastSelection, PortletSessionImpl.APPLICATION_SCOPE);
		}
		request.getPortletSession().setAttribute(key, value, PortletSessionImpl.APPLICATION_SCOPE);
	}
	
	private static Object getAttributeFromSession(PortletRequest request, String key){
		
		return request.getPortletSession().getAttribute(key, PortletSessionImpl.APPLICATION_SCOPE);
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
	
	public static void removeAllFaceteSelections(PortletRequest request){
		ArrayList<String> faceteSessionKeys = (ArrayList<String>) request.getPortletSession().getAttribute("faceteSessionKeys");
		if(faceteSessionKeys != null){
			for(int i=0; i < faceteSessionKeys.size(); i++){
				removeAttributeFromSession(request, faceteSessionKeys.get(i));
			}
		}
		removeAttributeFromSession(request, "faceteSessionKeys");
		removeAttributeFromSession(request, "faceteLastSelection");
		removeAttributeFromSession(request, "facetSelectionState");
		removeFaceteElementsFromSession(request);
	}
	
	public static void removeLastFaceteSelection(ActionRequest request) {

		HashMap faceteLastSelection = (HashMap) request.getPortletSession().getAttribute("faceteLastSelection");
		if(faceteLastSelection != null){
			for (Iterator<String> iterator = faceteLastSelection.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				removeAttributeFromSession(request, key);
			}
		}
	}
	
	private static void removeFaceteElementsFromSession(PortletRequest request){
		
		removeAttributeFromSession(request, ELEMENTS_DATATYPE);
		removeAttributeFromSession(request, ELEMENTS_PROVIDER);
		removeAttributeFromSession(request, ELEMENTS_TOPIC);
		removeAttributeFromSession(request, ELEMENTS_PARTNER);
		removeAttributeFromSession(request, ELEMENTS_METACLASS);
		removeAttributeFromSession(request, ELEMENTS_TIME);
		removeAttributeFromSession(request, ELEMENTS_GEOTHESAURUS);
		removeAttributeFromSession(request, ELEMENTS_MAP);
	}
	
	private static ArrayList<HashMap<String, Long>> sortHashMapAsArrayList(HashMap<String, Long> input){
		return sortHashMapAsArrayList(input, null);
	}
	
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
		PortletSession ps = request.getPortletSession();
		HashMap<String, Boolean> facetSelectionState = (HashMap<String, Boolean>) ps.getAttribute("facetSelectionState");
		if(facetSelectionState == null){
			facetSelectionState = new HashMap<String, Boolean>();
		}
		facetSelectionState.put(key, value);
		ps.setAttribute("facetSelectionState", facetSelectionState, PortletSessionImpl.APPLICATION_SCOPE);
	}
	
	private static HashMap<String, Boolean> getFacetSelectionState(PortletRequest request){
		PortletSession ps = request.getPortletSession();
		HashMap<String, Boolean> facetSelectionState = (HashMap<String, Boolean>) ps.getAttribute("facetSelectionState", PortletSessionImpl.APPLICATION_SCOPE);
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
	private static void getFacetAttributsParamsFromUrl(PortletRequest request){
		
		String paramsFacet = request.getParameter("f");
		
		if(paramsFacet != null){
			// Topics
			ArrayList<String> paramsTopics = getFacetParamsList(paramsFacet, PARAMS_TOPIC);
			if(paramsTopics != null){
				setAttributeToSession(request, SELECTED_TOPIC, paramsTopics);
			}
					
			// Datatype
			ArrayList<String> paramsDatatype = getFacetParamsList(paramsFacet ,PARAMS_DATATYPE);
			if(paramsDatatype != null){
				setAttributeToSession(request, SELECTED_DATATYPE, paramsDatatype);
			}

			// Service
			ArrayList<String> paramsService = getFacetParamsList(paramsFacet, PARAMS_SERVICE);
			if(paramsService != null){
				setAttributeToSession(request, SELECTED_SERVICE, paramsService);
			}

			// Measures
			ArrayList<String> paramsMeasure = getFacetParamsList(paramsFacet, PARAMS_MEASURE);
			if(paramsMeasure != null){
				setAttributeToSession(request, SELECTED_MEASURES, paramsMeasure);
			}

			// Metaclass
			ArrayList<String> paramsMetaclasses = getFacetParamsList(paramsFacet, PARAMS_METACLASS);
			if(paramsMetaclasses != null){
				setAttributeToSession(request, SELECTED_METACLASS, paramsMetaclasses);
			}
			
			// Partner
			String paramsPartner = getFacetParam(paramsFacet, PARAMS_PARTNER);
			if(paramsPartner != null){
				ArrayList<IngridPartner> selectedPartner = null; 
				List<IngridPartner> partners = UtilsDB.getPartners();
				for(int i=0; i < partners.size();i++){
					IngridPartner partner = partners.get(i);
					if(paramsPartner.equals(partner.getIdent())){
						if(selectedPartner == null){
							selectedPartner = new ArrayList<IngridPartner>();
						}
						selectedPartner.add(partner);
						break;
					}
				}
				setAttributeToSession(request, ENABLE_FACETE_PARTNER_LIST, selectedPartner);
			}
			
			// Provider
			ArrayList<String> paramsProvider = getFacetParamsList(paramsFacet, PARAMS_PROVIDER);
			if(paramsProvider != null){
				setAttributeToSession(request, SELECTED_PROVIDER, paramsProvider);
			}
			
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
			
			// Time
			String paramsModtime = getFacetParam(paramsFacet, PARAMS_MODTIME);
			if(paramsModtime != null){
				setAttributeToSession(request, "modtime", paramsModtime);
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
	 */
	private static void setFacetUrlParamsToUrl(ActionRequest request, StringBuffer facetUrl) {
		
		// Topic
		if(getAttributeFromSession(request, SELECTED_TOPIC) != null){
        	addUrlParameterForFacet(facetUrl, PARAMS_TOPIC, (ArrayList<String>) getAttributeFromSession(request, SELECTED_TOPIC));
        }
		
		// Datatype
        if(getAttributeFromSession(request, SELECTED_DATATYPE) != null){
        	addUrlParameterForFacet(facetUrl, PARAMS_DATATYPE, (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE));
        }
        
        // Service
        if(getAttributeFromSession(request, SELECTED_SERVICE) != null){
        	addUrlParameterForFacet(facetUrl, PARAMS_SERVICE, (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE));
        }
        
        // Measure
        if(getAttributeFromSession(request, SELECTED_MEASURES) != null){
        	addUrlParameterForFacet(facetUrl, PARAMS_MEASURE, (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES));
        }
        
        // Metaclass
        if(getAttributeFromSession(request, SELECTED_METACLASS) != null){
        	addUrlParameterForFacet(facetUrl, PARAMS_METACLASS, (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS));
        }
        
        // Partner
        if(getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST) != null){
        	ArrayList<IngridPartner> partners = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
        	for(int i=0; i<partners.size();i++){
        		IngridPartner partner = partners.get(i);
        		appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_PARTNER, partner.getIdent()));
        	}
        }
        
        // Provider
        if(getAttributeFromSession(request, SELECTED_PROVIDER) != null){
        	addUrlParameterForFacet(facetUrl, PARAMS_PROVIDER, (ArrayList<String>) getAttributeFromSession(request, SELECTED_PROVIDER));
        }
        
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
        
        // Time
        if(getAttributeFromSession(request, "doTime") != null){
        	String modtime = (String) getAttributeFromSession(request, "doTime");
        	appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MODTIME, modtime));
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
	
}



