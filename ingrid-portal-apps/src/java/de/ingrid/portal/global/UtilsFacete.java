/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.pluto.core.impl.PortletSessionImpl;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtAdrPlaceReferenceForm;
import de.ingrid.portal.forms.SearchExtEnvPlaceGeothesaurusForm;
import de.ingrid.portal.forms.SearchExtEnvTopicThesaurusForm;
import de.ingrid.portal.forms.SearchExtResTopicAttributesForm;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridMeasuresRubric;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.om.IngridServiceRubric;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.RangeQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.query.WildCardFieldQuery;
import de.ingrid.utils.udk.UtilsDate;

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
    
    private static final String ELEMENTS_PROVIDER = "elementsProvider";
    private static final String ELEMENTS_PARTNER = "elementsPartner";
    private static final String ELEMENTS_TOPIC = "elementsTopic";
    private static final String ELEMENTS_DATATYPE = "elementsDatatype";
    private static final String ELEMENTS_METACLASS = "elementsMetaclass";
    private static final String ELEMENTS_TIME = "elementsTime";
    
    private static final String ENABLE_FACETE_PARTNER_LIST = "enableFacetePartnerList";
    
    private static final String SELECTED_PROVIDER = "selectedProvider";
    private static final String SELECTED_TOPIC = "selectedTopics";
    private static final String SELECTED_METACLASS = "selectedMetaclass";
    private static final String SELECTED_DATATYPE = "selectedDatatype";
    private static final String SELECTED_MEASURES = "selectedMeasures";
    private static final String SELECTED_SERVICE = "selectedService";
    
    private static List<IngridServiceRubric> dbServices = null; 
    private static List<IngridMeasuresRubric> dbMeasures = null;
    
    /**
     * Prepare query by facete activity
     * 
     * @param ps
     * @param query
     */
    public static void facetePrepareInGridQuery (PortletRequest request, IngridQuery query){
    	
    	//removeFaceteElementsFromSession(request);
    	checkSessionForNewSearchTerm(request);
    	
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
        addToQueryThesaurus(request, query);
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
	        query.put("FACETS", list);
	        
	        setAttributeToSession(request, "FACETS_QUERY", list);
        }
        
        if(log.isDebugEnabled()){
        	log.debug("Query Facete: " + query);
        }
    }
    
	public static void setParamsToContext(RenderRequest request, Context context, IngridDocument facete) {
		
		if(facete == null){
			//removeFaceteElementsFromSession(request);
		}
		
		setParamsToContextTopic(request, context);
		setParamsToContextMetaclass(request, context);
		setParamsToContextDatatype(request, context);
		setParamsToContextService(request, context);
		setParamsToContextMeasures(request, context);
		setParamsToContextPartner(request, context);
		setParamsToContextProvider(request, context);
		setParamsToContextMap(request, context);
        setParamsToContextGeothesaurus(request, context);
        setParamsToContextThesaurus(request, context);
        setParamsToContextTime(request, context);
        setParamsToContextAttribute(request, context);
        setParamsToContextAreaAddress(request, context);
        
        context.put("facetsQuery", getAttributeFromSession(request, "FACETS_QUERY"));
	}

	public static void setFaceteParamsToSessionByAction(ActionRequest request) {
		
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
        setFaceteParamsToSessionThesaurus(request);
        setFaceteParamsToSessionAttribute(request);
        setFaceteParamsToSessionAreaAddress(request);
        
	}

	public static void checkForExistingFacete(IngridDocument facetes, PortletRequest request) {
		
		HashMap<String, Long> elementsProvider = null;
		HashMap<String, Long> elementsPartner = null;
		HashMap<String, Long> elementsTopic = null;
		HashMap<String, Long> elementsDatatype = null;
		HashMap<String, Long> elementsMetaclass = null;
		HashMap<String, Long> elementsTime = null;
		
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
					
				}else if(key.startsWith("t01_object.obj_class:")){
					if(elementsMetaclass == null){
						elementsMetaclass = new HashMap<String, Long>();
					}
					if(key.endsWith(":0")){
						elementsMetaclass.put("job", value);
					} else if(key.endsWith(":1")){
						elementsMetaclass.put("map", value);
					} else if(key.endsWith(":2")){
						elementsMetaclass.put("document", value);
					} else if(key.endsWith(":3")){
						elementsMetaclass.put("geoservice", value);
					} else if(key.endsWith(":4")){
						elementsMetaclass.put("project", value);
					} else if(key.endsWith(":5")){
						elementsMetaclass.put("database", value);
					} else if(key.endsWith(":6")){
						elementsMetaclass.put("service", value);
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

				}
			}
		}		
		
		if (elementsDatatype != null){
			String[] sortedRanking = PortalConfig.getInstance().getStringArray("portal.search.facete.sort.ranking.datatype");
			setAttributeToSession(request, ELEMENTS_DATATYPE, sortHashMapAsArrayList(elementsDatatype, sortedRanking));
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
			setAttributeToSession(request, ELEMENTS_METACLASS, sortHashMapAsArrayList(elementsMetaclass));
		}
		
		if (elementsTime != null){
			setAttributeToSession(request, ELEMENTS_TIME, sortHashMapAsArrayList(elementsTime));
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
        	setAttributeToSession(request, SELECTED_TOPIC, selectedTopics);
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
		
		ArrayList<IngridEnvTopic> enableFaceteTopicsList = null;
		List<IngridEnvTopic> unselectedTopics = null;
		
		ResourceBundle bundle = ResourceBundle.getBundle("de.ingrid.portal.resources.EnvironmentSearchResources", Locale.GERMAN);
		IngridResourceBundle resources = new IngridResourceBundle(bundle);
		if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("topic")){
					unselectedTopics = UtilsDB.getEnvTopics(resources);	
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
    	
    	if(selectedTopics != null && selectedTopics.size() > 0){
        	context.put("isTopicSelect", true);
    		context.put("selectedTopics", selectedTopics);
    	} else{
    		context.put("isTopicSelect", false);
    		context.remove("selectedTopics");
    	}
    	
    	context.put("enableFaceteTopicCount", PortalConfig.getInstance().getInt("portal.search.facete.topics.count", 3));
	}
	
	private static void addToQueryTopic(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedTopics = (ArrayList<String>) getAttributeFromSession(request, SELECTED_TOPIC);
    	
		if(selectedTopics != null && selectedTopics.size() > 0){
        	query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                    Settings.QVALUE_DATATYPE_AREA_ENVTOPICS));

        	// TOPIC
            String queryValue = null;
            ClauseQuery cq  = new ClauseQuery(true, false);
            for (int i = 0; i < selectedTopics.size(); i++) {
                queryValue = UtilsDB.getTopicFromKey(selectedTopics.get(i));
                cq.addField(new FieldQuery(false, false, Settings.QFIELD_TOPIC, queryValue));
            }
            query.addClause(cq);
        }
	}
	
	/***************************** METACLASS *******************************************/

	private static void setFaceteQueryParamsMetaclass(ArrayList<IngridDocument> list, PortletRequest request) {
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		if(selectedDatatype != null){
			for(int i=0; i < selectedDatatype.size(); i++){
				if(selectedDatatype.get(i).equals("metadata")){
					IngridDocument faceteEntry = new IngridDocument();
			        faceteEntry.put("id", "metaclass");
			        faceteEntry.put("field", "t01_object.obj_class");
			        list.add(faceteEntry);
			        
			        faceteEntry = new IngridDocument();
			        faceteEntry.put("id", "t01_object.obj_class");
			        
			        list.add(faceteEntry);
					break;
				}
			}
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
			setAttributeToSession(request, SELECTED_METACLASS, selectedMetaclass);
		}
	}
	private static void setParamsToContextMetaclass (RenderRequest request, Context context){
		
		ArrayList<String> selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
		ArrayList<String> enableFaceteMetaClass = (ArrayList<String>) getAttributeFromSession(request, ELEMENTS_METACLASS);
		
    	if(enableFaceteMetaClass != null){
    		context.put("enableFaceteMetaClass", enableFaceteMetaClass);
        }
    	
		if(selectedMetaclass != null && selectedMetaclass.size() > 0){
    		context.put("isMetaclassSelect", true);
    		context.put("selectedMetaclass", selectedMetaclass);
    	} else{
    		context.put("isMetaclassSelect", false);
    		context.remove("selectedMetaclass");
    	}
	}
	
	private static void addToQueryMetaclass(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
    	
		if(selectedMetaclass != null && selectedMetaclass.size() > 0){
    	   for (int i = 0; i < selectedMetaclass.size(); i++) {
               query.addField(new FieldQuery(true, false, Settings.QFIELD_METACLASS, selectedMetaclass.get(i).toString()));
	       }
	    }
	}
	
	/***************************** DATENTYPEN *********************************************/
	
	private static void setFaceteQueryParamsDatatype(ArrayList<IngridDocument> list) {
		IngridDocument facete = new IngridDocument();
        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
	    
        String[] sortedRanking = PortalConfig.getInstance().getStringArray("portal.search.facete.sort.ranking.datatype");
		for(int i=0; i < sortedRanking.length; i++){
			String key = sortedRanking[i];
			if(key.equals("map")){
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
		        faceteEntry.put("id", "map");
		        faceteEntry.put("query", "t011_obj_serv_op_connpoint.connect_point:http*");
		        faceteList.add(faceteEntry);
			}else if(key.equals("topic")){
				HashMap<String, String> faceteEntry = new HashMap<String, String>();
		        faceteEntry.put("id", "topic");
		        faceteEntry.put("query", "datatype:topics");
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
			setAttributeToSession(request, SELECTED_DATATYPE, selectedDatatype);
		}
	}
	
	private static void setParamsToContextDatatype (RenderRequest request, Context context){
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
		ArrayList<String> elementsDatatype = (ArrayList<String>) getAttributeFromSession(request, ELEMENTS_DATATYPE);
    	
		if(elementsDatatype != null){
    		context.put("enableFaceteDatatype", elementsDatatype);
        }
		
		if(selectedDatatype != null && selectedDatatype.size() > 0){
    		context.put("isDatatypeSelect", true);
    		context.put("selectedDatatype", selectedDatatype);
    	} else{
    		context.put("isDatatypeSelect", false);
    		context.remove("selectedDatatype");
    	}
    	
	}
	
	private static void addToQueryDatatype(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
    	
		if(selectedDatatype != null && selectedDatatype.size() > 0){
        	for(int i = 0; i < selectedDatatype.size(); i++){
        		if(selectedDatatype.get(i).equals("map")){
        			query.addWildCardFieldQuery(new WildCardFieldQuery(true, false, "t011_obj_serv_op_connpoint.connect_point", "http*"));
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_METADATA));
        			query.addField(new FieldQuery(true, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_WWW));
        		}else if(selectedDatatype.get(i).equals("topic")){
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_AREA_ENVTOPICS));
                }else{
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, selectedDatatype.get(i)));
        			if(selectedDatatype.get(i).equals("metadata")){
        				query.addField(new FieldQuery(true, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_WWW));
        			}else if(selectedDatatype.get(i).equals("www")){
        				query.addField(new FieldQuery(true, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_SOURCE_METADATA));
        			}
        		}
            }
        }else{
        	query.removeField(Settings.QFIELD_DATATYPE);
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
			selectedService.add(doAddService);
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
			setAttributeToSession(request, SELECTED_SERVICE, selectedService);
		}
	}
	
	private static void setParamsToContextService (RenderRequest request, Context context){
		
		ArrayList<String> selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
		ArrayList<HashMap<String, Long>> elementsService = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<HashMap<String, Long>> enableFaceteService = null;
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
			
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
    		context.put("isServiceSelect", true);
    		context.put("selectedService", selectedService);
    	} else{
    		context.put("isServiceSelect", false);
    		context.remove("selectedService");
    	}
    	
    	
	}
	
	private static void addToQueryService(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
    	
		if(selectedService != null && selectedService.size() > 0){
	    	query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
	                Settings.QVALUE_DATATYPE_AREA_SERVICE));
	    	
	    	String queryValue = null;
	        ClauseQuery cq  = new ClauseQuery(true, false);
	        
           	for (int i = 0; i < selectedService.size(); i++) {
           		queryValue = UtilsDB.getServiceRubricFromKey((String) selectedService.get(i));
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
			selectedMeasures.add(doAddMeasures);
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
			setAttributeToSession(request, SELECTED_MEASURES, selectedMeasures);
		}
	}
		
	
	private static void setParamsToContextMeasures (RenderRequest request, Context context){
		
		ArrayList<String> selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
		ArrayList<HashMap<String, Long>> elementsMeasure = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<HashMap<String, Long>> enableFaceteMeasures = null;
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE); 
		
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
    		context.put("isMeasuresSelect", true);
    		context.put("selectedMeasures", selectedMeasures);
    	} else{
    		context.put("isMeasuresSelect", false);
    		context.remove("selectedMeasures");
    	}
    	
	}
	
	private static void addToQueryMeasures(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
    	
		if(selectedMeasures != null && selectedMeasures.size() > 0){
	    	query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
	                Settings.QVALUE_DATATYPE_AREA_MEASURES));
	
	    	// RUBRIC
	        String queryValue = null;
	        ClauseQuery cq = new ClauseQuery(true, false);
            for (int i = 0; i < selectedMeasures.size(); i++) {
                queryValue = UtilsDB.getMeasuresRubricFromKey((String) selectedMeasures.get(i));
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
				setAttributeToSession(request, ENABLE_FACETE_PARTNER_LIST, selectedPartner);
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
    		context.put("isPartnerSelect", true);
		}else{
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
		}
		
		if(doAddProviderChb != null){
			selectedIds = new ArrayList<String>();
            selectedIds.add(doAddProviderChb);
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
		}
		
		if(selectedIds != null){
        	setAttributeToSession(request, SELECTED_PROVIDER, selectedIds);
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
    		context.put("isProviderSelect", true);
    		context.put("selectedProvider", selectedProviders);
    		context.put("unselectedProvider", providers);
    	} else{
    		context.put("isProviderSelect", false);
    		context.remove("selectedProvider");
    	}
	}
	
	private static void addToQueryProvider(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedProvider = (ArrayList<String>) getAttributeFromSession(request, SELECTED_PROVIDER);
		
		if(selectedProvider != null && selectedProvider.size() > 0){
			for(int i=0; i<selectedProvider.size();i++){
				query.addField(new FieldQuery(true, false, Settings.QFIELD_PROVIDER, selectedProvider.get(i)));
			}
			query.addField(new FieldQuery(true, false, Settings.QFIELD_GROUPED, "grouped_off"));
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
							    cal.add(Calendar.MONTH, -3);
							    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
							    faceteList.add(faceteEntry);	

								break;
				       		case 3:
				       			faceteEntry = new HashMap<String, String>();
							    faceteEntry.put("id", "modtime3");
							    cal = new GregorianCalendar();
							    cal.add(Calendar.YEAR, -1);
							    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
							    faceteList.add(faceteEntry);	

								break;
				       		case 4:
				       			faceteEntry = new HashMap<String, String>();
							    faceteEntry.put("id", "modtime4");
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
					    cal.add(Calendar.MONTH, -3);
					    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
					    faceteList.add(faceteEntry);	
					
					    faceteEntry = new HashMap<String, String>();
					    faceteEntry.put("id", "modtime3");
					    cal = new GregorianCalendar();
					    cal.add(Calendar.YEAR, -1);
					    faceteEntry.put("query", "t0113_dataset_reference.reference_date:[" + df.format(cal.getTime()) +  "0* TO " + timeNow + "9*]");
					    faceteList.add(faceteEntry);	
					
					    faceteEntry = new HashMap<String, String>();
					    faceteEntry.put("id", "modtime4");
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
				setAttributeToSession(request, "doTime", doTime);
			}
		}
	}
	
	private static void setParamsToContextTime (RenderRequest request, Context context){
		String doTime = (String) getAttributeFromSession(request, "doTime");
		
		if(doTime != null && doTime.length() > 0){
			context.put("isTimeSelect", true);
			context.put("doTime", doTime);
		}else{
			context.put("isTimeSelect", false);
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
            	// letzten 3 Monate
            	cal = new GregorianCalendar();
            	cal.add(Calendar.MONTH, -3);
            }else if(doTime.equals("3")){
            	// letztes Jahr
            	cal = new GregorianCalendar();
            	cal.add(Calendar.YEAR, -1);
            }else if(doTime.equals("4")){
            	// letzte 5 Jahre
            	cal = new GregorianCalendar();
            	cal.add(Calendar.YEAR, -5);
            }
            query.addRangeQuery(new RangeQuery(true, false, "t0113_dataset_reference.reference_date", df.format(cal.getTime()) + "0*", timeNow + "9*", true));
        }	
	}
	
	/***************************** KARTE ***********************************************/
	
	private static void setFaceteParamsToSessionMap(ActionRequest request) {
		
		String doAddMap = request.getParameter("doAddMap");
		String doRemoveMap = request.getParameter("doRemoveMap");
		HashMap<String, String> doMapCoords = null;
		HashMap<String, String> webmapclientCoords = null;
		ArrayList<String> coordOptions = null;
		
		if(doAddMap != null){
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
                    	if(request.getParameter("x2") != null){
                    		searchTerm = searchTerm.concat(webmapclientCoords.get("x2")).concat("' N");	
                    	}                
                        searchTerm = searchTerm.concat("<br>");
                    	if(request.getParameter("y1") != null){
                    		searchTerm = searchTerm.concat(webmapclientCoords.get("y1")).concat("' O / ");                    		
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
			setAttributeToSession(request, "doMapCoords", doMapCoords);
		}
		if(coordOptions != null){
			setAttributeToSession(request, "coordOptions", coordOptions);
		}
		if(webmapclientCoords != null){
			setAttributeToSession(request, "webmapclientCoords", webmapclientCoords);
		}
		
		if(doRemoveMap != null){
			if(doRemoveMap.equals("all")){
				removeAttributeFromSession(request, "doMapCoords");
				removeAttributeFromSession(request, "webmapclientCoords");
			}else{
				doMapCoords = (HashMap<String, String>) getAttributeFromSession(request, "doMapCoords");
				doMapCoords.remove(doRemoveMap);
				webmapclientCoords = (HashMap<String, String>) getAttributeFromSession(request, "webmapclientCoords");
				coordOptions = (ArrayList<String>) getAttributeFromSession(request, "coordOptions");
				
	        	if(coordOptions != null){
	        		for(int i=0; i < coordOptions.size(); i++){
	        			if(coordOptions.get(i).equals(doRemoveMap)){
	        				coordOptions.remove(i);
	        			}
	        		}
	        	}else{
	        		coordOptions = new ArrayList<String>();
	        	}
	        	setAttributeToSession(request, "doMapCoords", doMapCoords);
	        	setAttributeToSession(request, "coordOptions", coordOptions);
	        	setAttributeToSession(request, "webmapclientCoords", webmapclientCoords);
			}
		}
	}
	
	private static void setParamsToContextMap (RenderRequest request, Context context){
		
		HashMap<String, String> doMapCoords = (HashMap<String, String>) getAttributeFromSession(request, "doMapCoords");

		if(doMapCoords != null && doMapCoords.size() > 0){
        	context.put("isMapSelect", true);
        	context.put("doMapCoords", doMapCoords);
        }else{
        	context.put("isMapSelect", false);
        }
        context.put("webmapDebugMode", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_WEBMAPCLIENT_DEBUG, false));
	}
	
	private static void addToQueryMap(PortletRequest request, IngridQuery query) {
		
		HashMap<String, String> webmapclientCoords = (HashMap<String, String>) getAttributeFromSession(request, "webmapclientCoords");
		
		if (webmapclientCoords != null && webmapclientCoords.size() > 0){
			ArrayList<String> coordOptions = (ArrayList<String>) getAttributeFromSession(request, "coordOptions");
	    	if(coordOptions != null && coordOptions.size() > 0){
	    		for(int i=0; i < coordOptions.size(); i++){
			    	ClauseQuery cq = new ClauseQuery(true, false);
//TODO implement areaid in map			            
//			    		if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_BBOX) {

                    		cq.addField(new FieldQuery(true, false, "x1", webmapclientCoords.get("x1")));
                    		cq.addField(new FieldQuery(true, false, "y1", webmapclientCoords.get("y1")));
                    		cq.addField(new FieldQuery(true, false, "x2", webmapclientCoords.get("x2")));
                    		cq.addField(new FieldQuery(true, false, "y2", webmapclientCoords.get("y2")));
			                cq.addField(new FieldQuery(true, false, "coord", coordOptions.get(i)));
//			            } else if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE) {
//			            	cq.addField(new FieldQuery(true, false, "areaid", wmsDescriptor.getCommunityCode()));
//			            }
			            query.addClause(cq);
		    	}
	    	}
	    }
	}
	
	/***************************** GEOTHESAURUS ****************************************/
	
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
			    	String listSize = getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE).toString();
			        ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, GEOTHESAURUS_SELECTED_TOPICS_IDS);
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
			        	setAttributeToSession(request, GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
			        }
				}
			}
			
			if(doRemoveGeothesaurus != null && doAddGeothesaurus == null){
				if(doRemoveGeothesaurus.equals("all")){
					removeAttributeFromSession(request, GEOTHESAURUS_SELECTED_TOPICS_IDS);
				}else{
					ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, GEOTHESAURUS_SELECTED_TOPICS_IDS);
					if (selectedIds != null){
						for(int i = 0; i < selectedIds.size(); i++){
							if(selectedIds.get(i).equals(doRemoveGeothesaurus)){
								selectedIds.remove(i);
							}
						}
						setAttributeToSession(request, GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
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
	        setAttributeToSession(request, GEOTHESAURUS_SELECTED_TOPICS, geothesaurusSelectTopics);
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
        if(geothesaurusSelectTopics != null && geothesaurusSelectTopics.size() > 0){
        	context.put("geothesaurusSelectTopics", geothesaurusSelectTopics);
            context.put("isGeothesaurusSelect", true);
        }else{
        	context.put("isGeothesaurusSelect", false);
        }
        if(getAttributeFromSession(request, GEOTHESAURUS_DO) != null){
        	context.put("doGeothesaurus", getAttributeFromSession(request, GEOTHESAURUS_DO));
        }
        context.put("geothesaurusTerm", getAttributeFromSession(request, GEOTHESAURUS_TERM));
        context.put("geothesaurusError", getAttributeFromSession(request, GEOTHESAURUS_ERROR));
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
		ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, GEOTHESAURUS_SELECTED_TOPICS_IDS);
        ArrayList<IngridHit> allGeoThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS);
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = new ArrayList<HashMap<String, String>> ();
        
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
        return geothesaurusSelectTopics;
	}
	
	private static void addToListOfTopicsGeoThesaurus(IngridHit ingridHit, ActionRequest request) {
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
		
		setAttributeToSession(request, GEOTHESAURUS_ALL_TOPICS, allGeoThesaurusTopics);
	}

	/***************************** Thesaurus *****************************************/
	
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
	                	setAttributeToSession(request, THESAURUS_SELECTED_TOPICS_IDS, selectedIds);
	                }
	        	}
				
				ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
		        setAttributeToSession(request, THESAURUS_SELECTED_TOPICS, thesaurusSelectTopics);
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
						setAttributeToSession(request, THESAURUS_SELECTED_TOPICS_IDS, selectedIds);
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
        	context.put("isThesaurusSelect", true);
        	context.put("thesaurusSelectTopics", getAttributeFromSession(request, THESAURUS_SELECTED_TOPICS));
        }else{
        	context.put("isThesaurusSelect", false);
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
			setAttributeToSession(request, "doAddAttribute", attribute);
		}
	}
	
	private static void setParamsToContextAttribute (RenderRequest request, Context context){
		HashMap<String, String>  attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");
		if(attribute != null && attribute.size() > 0 ){
			context.put("isAttributeSelect", true);
			context.put("doAddAttribute", getAttributeFromSession(request, "doAddAttribute"));	
		}else{
			context.put("isAttributeSelect", false);
		}
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
			setAttributeToSession(request, "doAddAreaAddress", areaAddress);
		}
	}
	
	private static void setParamsToContextAreaAddress (RenderRequest request, Context context){
		HashMap<String, String> areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
		if(areaAddress != null && areaAddress.size() > 0 ){
			context.put("isAreaAddressSelect", true);
			context.put("doAddAreaAddress", getAttributeFromSession(request, "doAddAreaAddress"));	
		}else{
			context.put("isAreaAddressSelect", false);
		}
		 
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
	
	private static void setAttributeToSession(PortletRequest request, String key, Object value){

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
		request.getPortletSession().setAttribute(key, value, PortletSessionImpl.APPLICATION_SCOPE);
	}
	
	private static Object getAttributeFromSession(PortletRequest request, String key){
		
		return request.getPortletSession().getAttribute(key, PortletSessionImpl.APPLICATION_SCOPE);
	}
	
	private static void removeFaceteElementsFromSession(PortletRequest request){
		
		removeAttributeFromSession(request, ELEMENTS_DATATYPE);
		removeAttributeFromSession(request, ELEMENTS_PROVIDER);
		removeAttributeFromSession(request, ELEMENTS_TOPIC);
		removeAttributeFromSession(request, ELEMENTS_PARTNER);
		removeAttributeFromSession(request, ELEMENTS_METACLASS);
		removeAttributeFromSession(request, ELEMENTS_TIME);
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
	
	private static void checkSessionForNewSearchTerm(PortletRequest request){
		String portalTerm = request.getParameter("q");
		String portalDS = request.getParameter("ds");

		if(portalTerm != null && portalDS != null){
			String faceteTerm  = (String) getAttributeFromSession(request, "faceteTerm");
			String faceteDS  = (String) getAttributeFromSession(request, "faceteDS");
			
			if(faceteTerm == null){
				faceteTerm = portalTerm;
				setAttributeToSession(request, "faceteTerm", faceteTerm);
			}
			
			if(faceteDS == null){
				faceteDS = portalDS;
				setAttributeToSession(request, "faceteDS", faceteDS);
			}
			
			if(!portalTerm.equals(faceteTerm) || !portalDS.equals(faceteDS)){
				removeAllFaceteSelections(request);
			}
			setAttributeToSession(request, "faceteTerm", portalTerm);
			setAttributeToSession(request, "faceteDS", portalDS);
		}
	}
	
	private static void removeAllFaceteSelections(PortletRequest request){
		ArrayList<String> faceteSessionKeys = (ArrayList<String>) request.getPortletSession().getAttribute("faceteSessionKeys");
		if(faceteSessionKeys != null){
			for(int i=0; i < faceteSessionKeys.size(); i++){
				removeAttributeFromSession(request, faceteSessionKeys.get(i));
			}
		}
		removeAttributeFromSession(request, "faceteSessionKeys");
	}
	
	private static void general(ActionRequest request) {
		String doRemoveAll = request.getParameter("doRemoveAll");
		if(doRemoveAll != null){
			removeAllFaceteSelections(request);
			removeFaceteElementsFromSession(request);
		}
	}
}



