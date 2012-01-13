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
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.om.WMSSearchDescriptor;
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

    private static final String CURRENT_TOPIC_GEOTHESAURUS = "facete_current_topic_geothesaurus";
    private static final String TOPICS_GEOTHESAURUS = "facete_topics_geothesaurus";
    private static final String SIMILAR_TOPICS_GEOTHESAURUS = "facete_similar_topics_geothesaurus";
    private static final String ERROR_GEOTHESAURUS = "facete_error_geothesaurus";
    private static final String LIST_SIZE_GEOTHESAURUS = "facete_list_size_geothesaurus";
    
    private static final String CURRENT_TOPIC_THESAURUS = "facete_current_topic_thesaurus";
    private static final String TOPICS_THESAURUS = "facete_topics_thesaurus";
    private static final String SIMILAR_TOPICS_THESAURUS = "facete_similar_topics_thesaurus";
    private static final String ERROR_THESAURUS = "facete_error_thesaurus";
    private static final String LIST_SIZE_THESAURUS = "facete_list_size_thesaurus";
    
    private static final String ELEMENTS_PROVIDER = "elementsProvider";
    private static final String ELEMENTS_PARTNER = "elementsPartner";
    private static final String ELEMENTS_TOPIC = "elementsTopic";
    private static final String ELEMENTS_DATATYPE = "elementsDatatype";
    private static final String ELEMENTS_METACLASS = "elementsMetaclass";
    
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
    	
    	addTopicToQuery(request, query);
	    addMeasuresToQuery(request, query);
        addDatatypeToQuery(request, query);
        addServiceToQuery(request, query);
        addMetaclassToQuery(request, query);
        addPartnerToQuery(request, query);
        addProviderToQuery(request, query);
        addTimeToQuery(request, query);
        addMapToQuery(request, query);
        addGeothesaurusToQuery(request, query);
        addThesaurusToQuery(request, query);
        addAttributeToQuery(request, query);
        addAreaAddressToQuery(request, query);
        
        if(query.get("FACETS") == null){
            ArrayList<IngridDocument> list = new ArrayList<IngridDocument>();
	        setFaceteQueryParamsService(list);
	        setFaceteQueryParamsMeasure(list);
	        setFaceteQueryParamsMetaclass(list);
	        setFaceteQueryParamsPartner(list);
	        setFaceteQueryParamsProvider(list);
			setFaceteQueryParamsDatatype(list);
	        setFaceteQueryParamsTopic(list, request);
	        query.put("FACETS", list);
        }
        
        if(log.isDebugEnabled()){
        	log.debug("Query Facete: " + query);
        }
    }
    
	public static void setParamsToContext(RenderRequest request, Context context) {
		
		setTopicParamsToContext(request, context);
		setMetaclassParamsToContext(request, context);
		setDatatypeParamsToContext(request, context);
		setServiceParamsToContext(request, context);
		setMeasuresParamsToContext(request, context);
		setPartnerParamsToContext(request, context);
		setProviderParamsToContext(request, context);
		setMapParamsToContext(request, context);
        setGeothesaurusParamsToContext(request, context);
        setThesaurusParamsToContext(request, context);
        setTimeParamsToContext(request, context);
        setAttributeParamsToContext(request, context);
        setAreaAddressParamsToContext(request, context);
	}

	public static void setFaceteParamsToSessionByAction(ActionRequest request) {
		
		setFaceteTopicParamsToSession(request);
		setFaceteMetaclassParamsToSession(request);
		setFaceteDatatypeParamsToSession(request);
		setFaceteServiceParamsToSession(request);
		setFaceteMeasuresParamsToSession(request);
		setFacetePartnerParamsToSession(request);
		setFaceteProviderParamsToSession(request);
		setFaceteTimeParamsToSession(request);
        setFaceteMapParamsToSession(request);
        setFaceteGeothesaurusParamsToSession(request);
        setFaceteThesaurusParamsToSession(request);
        setFaceteAttributeParamsToSession(request);
        setFaceteAreaAddressParamsToSession(request);
        
	}

	public static void checkForExistingFacete(IngridDocument facetes, PortletRequest request) {
		
		HashMap<String, Long> elementsProvider = null;
		HashMap<String, Long> elementsPartner = null;
		HashMap<String, Long> elementsTopic = null;
		HashMap<String, Long> elementsDatatype = null;
		HashMap<String, Long> elementsMetaclass = null;
		
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

				}
			}
		}		
		
		if (elementsDatatype != null){
			setAttributeToSession(request, ELEMENTS_DATATYPE, sortHashMapAsArrayList(elementsDatatype));
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
		
	}

	/***************************** THEMEN **********************************************/
	
	private static void setFaceteQueryParamsTopic(ArrayList<IngridDocument> list, PortletRequest request) {
		
		IngridDocument facete = new IngridDocument();
        
        facete.put("id", "topic");
        facete.put("field", "topic");
        
        list.add(facete);
	}

	private static void setFaceteTopicParamsToSession(ActionRequest request) {
		
		String doAddTopic = request.getParameter("doAddTopic");
		String doAddTopicChb = request.getParameter("doAddTopicChb");
		String doRemoveTopic = request.getParameter("doRemoveTopic");
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
	}
	
	private static void setTopicParamsToContext (RenderRequest request, Context context){
		
		ArrayList<HashMap<String, Long>> elementsTopic = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		HashMap<String, Long> elementsTopicWithFacete = null;
		HashMap<String, Long> elementsTopicSelect = null;
		
		ArrayList<String> selectedTopics = (ArrayList<String>) getAttributeFromSession(request, SELECTED_TOPIC);
		ArrayList<IngridEnvTopic> enableFaceteTopicsList = null;
		List<IngridEnvTopic> unselectedTopics = null;
		
		ResourceBundle bundle = ResourceBundle.getBundle("de.ingrid.portal.resources.EnvironmentSearchResources", Locale.GERMAN);
		IngridResourceBundle resources = new IngridResourceBundle(bundle);
		unselectedTopics = UtilsDB.getEnvTopics(resources);
		
		if(unselectedTopics != null && elementsTopic != null){
			for(int i=0; i < elementsTopic.size(); i++){
    			HashMap<String, Long> map = elementsTopic.get(i);
    			List<String> keys = new ArrayList<String>(map.keySet());
    			String ident = keys.get(0);
    			
    			for(int j=0; j < unselectedTopics.size(); j++){
    				if(ident != null){
    					IngridEnvTopic top = unselectedTopics.get(j);
    					String topIdent = top.getQueryValue(); 
    					
        				if(ident.equals(topIdent.toLowerCase())){
        					
        					if(elementsTopicWithFacete == null){
        						elementsTopicWithFacete = new HashMap<String,Long>();
        					}
        						
        					elementsTopicWithFacete.put(top.getFormValue(), map.get(ident));
        					
        					if(enableFaceteTopicsList == null){
        						enableFaceteTopicsList = new ArrayList<IngridEnvTopic>();
            				}
        					enableFaceteTopicsList.add(top);
        					unselectedTopics.remove(j);
        					j = j-1;
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
        					j = j-1;
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
								j = j-1;
								break;
							}
						}
					}
				}
			}
			
		}
		
    	context.put("enableFaceteTopicsList", enableFaceteTopicsList);
    	context.put("elementsTopic", elementsTopicWithFacete);
    	if(elementsTopicSelect != null){
    		context.put("elementsTopicSelect", sortHashMapAsArrayList(elementsTopicSelect));
    	}
    	context.put("enableFaceteTopicCount", PortalConfig.getInstance().getInt("portal.search.facete.topics.count", 3));
    	context.put("unselectedTopics", unselectedTopics);
    	
    	if(selectedTopics != null && selectedTopics.size() > 0){
        	context.put("isTopicSelect", true);
    		context.put("selectedTopics", selectedTopics);
    	} else{
    		context.put("isTopicSelect", false);
    		context.remove("selectedTopics");
    	}
	}
	
	private static void addTopicToQuery(PortletRequest request, IngridQuery query) {
		
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

	private static void setFaceteQueryParamsMetaclass(ArrayList<IngridDocument> list) {
		IngridDocument faceteEntry = new IngridDocument();
        faceteEntry.put("id", "metaclass");
        faceteEntry.put("field", "t01_object.obj_class");
        list.add(faceteEntry);
        
        faceteEntry = new IngridDocument();
        faceteEntry.put("id", "t01_object.obj_class");
        
        list.add(faceteEntry);
	}

	private static void setFaceteMetaclassParamsToSession(ActionRequest request) {
		
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
	private static void setMetaclassParamsToContext (RenderRequest request, Context context){
		
		ArrayList<String> selectedMetaclass = (ArrayList<String>) getAttributeFromSession(request, SELECTED_METACLASS);
		ArrayList<String> enableFaceteMetaClass = (ArrayList<String>) getAttributeFromSession(request, ELEMENTS_METACLASS);
		
    	if(enableFaceteMetaClass != null){
    		context.put("isMetaclassSelect", true);
    		context.put("enableFaceteMetaClass", enableFaceteMetaClass);
        }else{
        	context.put("isMetaclassSelect", false);
        }
		
		if(selectedMetaclass != null){
    		context.put("isMetaclassSelect", true);
    		context.put("selectedMetaclass", selectedMetaclass);
    	} else{
    		context.put("isMetaclassSelect", false);
    		context.remove("selectedMetaclass");
    	}
	}
	
	private static void addMetaclassToQuery(PortletRequest request, IngridQuery query) {
		
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
	        
        HashMap<String, String> faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "www");
        faceteEntry.put("query", "datatype:www");
        faceteList.add(faceteEntry);
        
        faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "map");
        faceteEntry.put("query", "t011_obj_serv_op_connpoint.connect_point:http*");
        faceteList.add(faceteEntry);
        
        faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "metadata");
        faceteEntry.put("query", "datatype:metadata");
        faceteList.add(faceteEntry);
        
        faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "fis");
        faceteEntry.put("query", "datatype:fis");
        faceteList.add(faceteEntry);
        
        faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "service");
        faceteEntry.put("query", "datatype:service");
        faceteList.add(faceteEntry);
        
        faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "measure");
        faceteEntry.put("query", "datatype:measure");
        faceteList.add(faceteEntry);
        
        faceteEntry = new HashMap<String, String>();
        faceteEntry.put("id", "topics");
        faceteEntry.put("query", "datatype:topics");
        faceteList.add(faceteEntry);
        
        facete.put("id", "type");
        facete.put("classes", faceteList);
        
        list.add(facete);
		
	}
	
	private static void setFaceteDatatypeParamsToSession(ActionRequest request) {
		
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
			}else if(doRemoveDatatype.equals("topics")){
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
	
	private static void setDatatypeParamsToContext (RenderRequest request, Context context){
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
		ArrayList<String> elementsDatatype = (ArrayList<String>) getAttributeFromSession(request, ELEMENTS_DATATYPE);
    	
		if(elementsDatatype != null){
    		context.put("isDatatypeSelect", true);
    		context.put("enableFaceteDatatype", elementsDatatype);
        }else{
        	context.put("isDatatypeSelect", false);
        }
		
		if(selectedDatatype != null){
    		context.put("isDatatypeSelect", true);
    		context.put("selectedDatatype", selectedDatatype);
    	} else{
    		context.put("isDatatypeSelect", false);
    		context.remove("selectedDatatype");
    	}
    	
	}
	
	private static void addDatatypeToQuery(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedDatatype = (ArrayList<String>) getAttributeFromSession(request, SELECTED_DATATYPE);
    	
		if(selectedDatatype != null && selectedDatatype.size() > 0){
        	for(int i = 0; i < selectedDatatype.size(); i++){
        		if(selectedDatatype.get(i).equals("map")){
        			query.addWildCardFieldQuery(new WildCardFieldQuery(true, false, "t011_obj_serv_op_connpoint.connect_point", "http*"));
        		}else{
        			query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, selectedDatatype.get(i).toString()));
        		}
            }
        }else{
        	query.removeField(Settings.QFIELD_DATATYPE);
        }
	}
	
	/***************************** SERVICE *********************************************/

	private static void setFaceteQueryParamsService(ArrayList<IngridDocument> list) {
		
		IngridDocument facete = new IngridDocument();
        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
        
        if(dbServices == null){
        	dbServices = UtilsDB.getServiceRubrics();
        }
        HashMap<String, String> faceteEntry = null;
        
        for(int i=0; i < dbServices.size(); i++){
        	IngridServiceRubric service = dbServices.get(i);
        	faceteEntry = new HashMap<String, String>();
        	faceteEntry.put("id", service.getFormValue());
            faceteEntry.put("query", "topic:" + service.getFormValue());
            faceteList.add(faceteEntry);
        }
        
        facete.put("id", "service");
        facete.put("classes", faceteList);
        
        list.add(facete);
	}
	
	private static void setFaceteServiceParamsToSession(ActionRequest request) {
		
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
	
	private static void setServiceParamsToContext (RenderRequest request, Context context){
		
		ArrayList<String> selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
		ArrayList<HashMap<String, Long>> elementsService = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<HashMap<String, Long>> enableFaceteService = null;
		
		if(dbServices == null){
        	dbServices = UtilsDB.getServiceRubrics();
        }
		
		if(elementsService != null){
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
		
		if(selectedService != null){
    		context.put("isServiceSelect", true);
    		context.put("selectedService", selectedService);
    	} else{
    		context.put("isServiceSelect", false);
    		context.remove("selectedService");
    	}
    	
    	
	}
	
	private static void addServiceToQuery(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedService = (ArrayList<String>) getAttributeFromSession(request, SELECTED_SERVICE);
    	
		if(selectedService != null && selectedService.size() > 0){
	    	query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
	                Settings.QVALUE_DATATYPE_AREA_SERVICE));
	    	
	    	String queryValue = null;
	        ClauseQuery cq  = new ClauseQuery(true, false);
	        
           	for (int i = 0; i < selectedService.size(); i++) {
           		queryValue = UtilsDB.getServiceRubricFromKey((String) selectedService.get(i));
           		cq.addField(new FieldQuery(true, false, Settings.QFIELD_RUBRIC, queryValue));
            }
            query.addClause(cq);
	    }
	}
	
	/***************************** MESSWERTE *******************************************/

	private static void setFaceteQueryParamsMeasure(ArrayList<IngridDocument> list) {
		
		IngridDocument facete = new IngridDocument();
        ArrayList<HashMap<String, String>> faceteList = new ArrayList<HashMap<String, String>> ();
        HashMap<String, String> faceteEntry = null;
        
        if(dbMeasures == null){
        	dbMeasures = UtilsDB.getMeasuresRubrics();
        }
        
        for(int i=0; i < dbMeasures.size(); i++){
        	IngridMeasuresRubric measure = dbMeasures.get(i);
        	faceteEntry = new HashMap<String, String>();
        	faceteEntry.put("id", measure.getFormValue());
            faceteEntry.put("query", "topic:" + measure.getFormValue());
            faceteList.add(faceteEntry);
        }
         
        facete.put("id", "measure");
        facete.put("classes", faceteList);
        
        list.add(facete);
	}

	private static void setFaceteMeasuresParamsToSession(ActionRequest request) {
		
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
		
	
	private static void setMeasuresParamsToContext (RenderRequest request, Context context){
		
		ArrayList<String> selectedMeasures = (ArrayList<String>) getAttributeFromSession(request, SELECTED_MEASURES);
		ArrayList<HashMap<String, Long>> elementsMeasure = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_TOPIC);
		ArrayList<HashMap<String, Long>> enableFaceteMeasures = null;
				
		if(dbMeasures == null){
        	dbMeasures = UtilsDB.getMeasuresRubrics();
        }
        
		if(elementsMeasure != null){
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
		
    	if(selectedMeasures != null){
    		context.put("isMeasuresSelect", true);
    		context.put("selectedMeasures", selectedMeasures);
    	} else{
    		context.put("isMeasuresSelect", false);
    		context.remove("selectedMeasures");
    	}
    	
	}
	
	private static void addMeasuresToQuery(PortletRequest request, IngridQuery query) {
		
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
	
	private static void setFacetePartnerParamsToSession(ActionRequest request) {
		
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
		
	private static void setPartnerParamsToContext (RenderRequest request, Context context){
		
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
		context.put("enableFacetePartnerListCount", PortalConfig.getInstance().getInt("portal.search.facete.partner.count", 3));
	}
	
	
	private static void addPartnerToQuery(PortletRequest request, IngridQuery query) {
		
		ArrayList<IngridPartner> selectedPartner = (ArrayList<IngridPartner>) getAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);
    	
		if(selectedPartner != null && selectedPartner.size() > 0){
			for(int i=0; i<selectedPartner.size();i++){
				IngridPartner partner = (IngridPartner) selectedPartner.get(i);
				query.addField(new FieldQuery(true, false, Settings.QFIELD_PARTNER, partner.getIdent()));
			}
		}
	}
   
	/***************************** ANBIETER ********************************************/

	private static void setFaceteQueryParamsProvider(ArrayList<IngridDocument> list) {
		IngridDocument faceteEntry = new IngridDocument();
        faceteEntry.put("id", "provider");
        
        list.add(faceteEntry);
	}

	private static void setFaceteProviderParamsToSession(ActionRequest request) {
		
		String doAddProvider = request.getParameter("doAddProvider");
		String doAddProviderChb = request.getParameter("doAddProviderChb");
		String doRemoveProvider = request.getParameter("doRemoveProvider");
		
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
	}

	private static void setProviderParamsToContext (RenderRequest request, Context context){
		
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
        					j = j-1;
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
    	
    	if(selectedProviders != null && selectedProviders.size() > 0){
    		context.put("isProviderSelect", true);
    		context.put("selectedProvider", selectedProviders);
    		context.put("unselectedProvider", providers);
    	} else{
    		context.put("isProviderSelect", false);
    		context.remove("selectedProvider");
    	}
	}
	
	private static void addProviderToQuery(PortletRequest request, IngridQuery query) {
		
		ArrayList<String> selectedProvider = (ArrayList<String>) getAttributeFromSession(request, SELECTED_PROVIDER);
		
		if(selectedProvider != null && selectedProvider.size() > 0){
			for(int i=0; i<selectedProvider.size();i++){
				query.addField(new FieldQuery(true, false, Settings.QFIELD_PROVIDER, selectedProvider.get(i)));
			}
		}
	}
	
	
	/***************************** ZEITBEZUG *******************************************/

	private static void setFaceteTimeParamsToSession(ActionRequest request) {
		
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
	
	private static void setTimeParamsToContext (RenderRequest request, Context context){
		String doTime = (String) getAttributeFromSession(request, "doTime");
		
		if(doTime != null)
			context.put("doTime", doTime);
	}
	
	private static void addTimeToQuery(PortletRequest request, IngridQuery query) {
		String doTime = (String) getAttributeFromSession(request, "doTime");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = new GregorianCalendar();
		ClauseQuery cq = null;
        
		if(doTime != null){
            cq = new ClauseQuery(true, false);
            
            if (doTime.equals("1")) {
            	// letzten Monat
            	cal.add(Calendar.MONTH, -1);
            }else if(doTime.equals("2")){
            	// letzten 3 Monate
            	cal.add(Calendar.MONTH, -3);
            }else if(doTime.equals("3")){
            	// letztes Jahr
            	cal.add(Calendar.YEAR, -1);
            }else if(doTime.equals("4")){
            	// letzte 5 Jahre
            	cal.add(Calendar.YEAR, -5);
            }
            cq.addField(new FieldQuery(false, false, Settings.QFIELD_DATE_FROM, df.format(cal.getTime())));
            cq.addField(new FieldQuery(false, false, Settings.QFIELD_DATE_TO, df.format(new GregorianCalendar().getTime())));
            cq.addField(new FieldQuery(false, false, "time", "include"));
            query.addClause(cq);
        }	
	}
	
	/***************************** KARTE ***********************************************/
	
	private static void setFaceteMapParamsToSession(ActionRequest request) {
		
		String doAddMap = request.getParameter("doAddMap");
		String doRemoveMap = request.getParameter("doRemoveMap");
		HashMap<String, String> doMapCoords = null;
		ArrayList<String> coordOptions = null;
		
		if(doAddMap != null){
			
			coordOptions = new ArrayList<String>();
        	if(request.getParameter("chk_1") != null){
        		coordOptions.add(request.getParameter("chk_1"));
        	}
        	if(request.getParameter("chk_2") != null){
        		coordOptions.add(request.getParameter("chk_2"));
        	}
        	
        	if(request.getParameter("chk_3") != null){
        		coordOptions.add(request.getParameter("chk_3"));
        		
        	}
			if(coordOptions != null && coordOptions.size() > 0){
				WMSSearchDescriptor wmsDescriptor = WMSInterfaceImpl.getInstance().getWMSSearchParameter(request.getPortletSession().getId());
				doMapCoords = new HashMap<String, String>();
	            if(wmsDescriptor != null){
	        		for(int i=0; i < coordOptions.size(); i++){
	        			String searchTerm = "";
	                    if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_BBOX) {
	                        searchTerm = Double.toString(wmsDescriptor.getMinX()).concat("' O / ");
	                        searchTerm = searchTerm.concat(Double.toString(wmsDescriptor.getMinY())).concat("' N");
	                        searchTerm = searchTerm.concat("<br>");
	                        searchTerm = searchTerm.concat(Double.toString(wmsDescriptor.getMaxX())).concat("' O / ");
	                        searchTerm = searchTerm.concat(Double.toString(wmsDescriptor.getMaxY())).concat("' N");
	                        searchTerm = searchTerm.concat("<br>" +  coordOptions.get(i));
	                    } else if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE) {
	                        searchTerm = searchTerm.concat("areaid:").concat(wmsDescriptor.getCommunityCode());
	                    }
	                    doMapCoords.put(coordOptions.get(i), searchTerm);
	        		}
	        	}
			}
		}
		
		if(doRemoveMap != null){
			doMapCoords = (HashMap<String, String>) getAttributeFromSession(request, "doMapCoords");
			doMapCoords.remove(doRemoveMap);
			
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
		}
		
		if(doMapCoords != null){
			setAttributeToSession(request, "doMapCoords", doMapCoords);
		}
		
		if(coordOptions != null){
			setAttributeToSession(request, "coordOptions", coordOptions);
		}
		
			
	}
	
	private static void setMapParamsToContext (RenderRequest request, Context context){

		String wmsURL = UtilsSearch.getWMSURL(request, request.getParameter("wms_url"), false);
        context.put("wmsURL", wmsURL);
        HashMap<String, String> doMapCoords = (HashMap<String, String>) getAttributeFromSession(request, "doMapCoords");
        
        if(doMapCoords != null && doMapCoords.size() > 0){
        	context.put("doMapCoords", getAttributeFromSession(request, "doMapCoords"));
        }
	}
	
	private static void addMapToQuery(PortletRequest request, IngridQuery query) {
		
		HashMap<String, String> doMapCoords = (HashMap<String, String>) getAttributeFromSession(request, "doMapCoords");
    	
		if (doMapCoords != null && doMapCoords.size() > 0){
	    	WMSSearchDescriptor wmsDescriptor = WMSInterfaceImpl.getInstance().getWMSSearchParameter(
	                request.getPortletSession().getId());
	    	
	    	ArrayList<String> coordOptions = (ArrayList<String>) getAttributeFromSession(request, "coordOptions");
	    	if(coordOptions != null){
	    		for(int i=0; i < coordOptions.size(); i++){
			    	ClauseQuery cq = null;
			        
			    	if(wmsDescriptor != null){
			    		cq = new ClauseQuery(true, false);
			            
			    		if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_BBOX) {
			                cq.addField(new FieldQuery(true, false, "x1", Double.toString(wmsDescriptor.getMinX())));
			                cq.addField(new FieldQuery(true, false, "y1", Double.toString(wmsDescriptor.getMinY())));
			                cq.addField(new FieldQuery(true, false, "x2", Double.toString(wmsDescriptor.getMaxX())));
			                cq.addField(new FieldQuery(true, false, "y2", Double.toString(wmsDescriptor.getMaxY())));
			                cq.addField(new FieldQuery(true, false, "coord", coordOptions.get(i)));
			            } else if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE) {
			            	cq.addField(new FieldQuery(true, false, "areaid", wmsDescriptor.getCommunityCode()));
			            }
			            query.addClause(cq);
			    	}
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
	
	private static void setFaceteGeothesaurusParamsToSession(ActionRequest request) {
		
		String doGeothesaurus = request.getParameter("doGeothesaurus");
		String doCancelGeothesaurus = request.getParameter("doCancelGeothesaurus");
		String doAddGeothesaurus = request.getParameter("doAddGeothesaurus");
		String doSearchGeothesaurus = request.getParameter("doSearchGeothesaurus");
		String doBrowseGeothesaurus = request.getParameter("doBrowseGeothesaurus");
		String doBrowseSimilarGeothesaurus = request.getParameter("doBrowseSimilarGeothesaurus");
		String doRemoveGeothesaurus = request.getParameter("doRemoveGeothesaurus");
		
		if(doCancelGeothesaurus != null){
			setAttributeToSession(request, "doGeothesaurus", false);
        }else{
			if(doGeothesaurus != null){
				if(doGeothesaurus.equals("true")){
					setAttributeToSession(request, "doGeothesaurus", true);
				}else{
					setAttributeToSession(request, "doGeothesaurus", false);
				}
			}
			
			if(doAddGeothesaurus != null){
				setAttributeToSession(request, "doGeothesaurus", false);
				if(getAttributeFromSession(request, LIST_SIZE_GEOTHESAURUS) != null){
			    	String listSize = getAttributeFromSession(request, LIST_SIZE_GEOTHESAURUS).toString();
			        ArrayList<String> selectedIds = new ArrayList<String>();
			        if(listSize != null){
			        	for (int i=0; i< Integer.parseInt(listSize); i++) {
			        		String chkVal = request.getParameter("chk"+(i+1));
			                if (chkVal != null) {
			                	selectedIds.add(chkVal);
			                }
			            }
			        }
			        
			        if(selectedIds != null){
			        	setAttributeToSession(request, "geothesaurusSelectTopicsId", selectedIds);
			        }
				}
			}
			
			if(doRemoveGeothesaurus != null && doAddGeothesaurus == null){
				if(doRemoveGeothesaurus.equals("all")){
					removeAttributeFromSession(request, "geothesaurusSelectTopicsId");
				}else{
					ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, "geothesaurusSelectTopicsId");
					if (selectedIds != null){
						for(int i = 0; i < selectedIds.size(); i++){
							if(selectedIds.get(i).equals(doRemoveGeothesaurus)){
								selectedIds.remove(i);
							}
						}
						setAttributeToSession(request, "geothesaurusSelectTopicsId", selectedIds);
					}
				}
				setAttributeToSession(request, "doGeothesaurus", false);
			}
			
			if (doSearchGeothesaurus != null){
				setAttributeToSession(request, "doGeothesaurus", true);
				removeAttributeFromSession(request, TOPICS_GEOTHESAURUS);
				removeAttributeFromSession(request, SIMILAR_TOPICS_GEOTHESAURUS);
				removeAttributeFromSession(request, CURRENT_TOPIC_GEOTHESAURUS);
				
				String geothesaurusTerm = null;
				SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);        
	            f.clearErrors();
	            f.populate(request);
	            if (f.validate()) {
	            	geothesaurusTerm = f.getInput(SearchExtEnvPlaceGeothesaurusForm.FIELD_SEARCH_TERM);
	            	if(geothesaurusTerm != null){
	            		setAttributeToSession(request, "geothesaurusTerm", geothesaurusTerm);
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
	                    }
	                	setAttributeToSession(request, TOPICS_GEOTHESAURUS, topics);
	                	setAttributeToSession(request, LIST_SIZE_GEOTHESAURUS, topics.length);
	                	setAttributeToSession(request, ERROR_GEOTHESAURUS, false);
	                }else{
	                	setAttributeToSession(request, ERROR_GEOTHESAURUS, true);
	                }
	            }
	        }
	        
	        if(doBrowseGeothesaurus != null || doBrowseSimilarGeothesaurus != null){
	        	IngridHit[] topics = null;
	            if (doBrowseGeothesaurus != null) {
	                topics = (IngridHit[]) getAttributeFromSession(request, TOPICS_GEOTHESAURUS);
	            } else {
	                doBrowseGeothesaurus = doBrowseSimilarGeothesaurus;
	                if (doBrowseGeothesaurus != null) {
	                    topics = (IngridHit[]) getAttributeFromSession(request, SIMILAR_TOPICS_GEOTHESAURUS);
	                	
	                }
	            }
	
	            if (topics != null && topics.length > 0) {
	            	int list_size = 0;
	                for (int i=0; i<topics.length; i++) {
	                	String tid = UtilsSearch.getDetailValue(topics[i], "topicID");
	                    if (tid != null && tid.equals(doBrowseGeothesaurus)) {
	                    	list_size = list_size + 1;
	                        setAttributeToSession(request, CURRENT_TOPIC_GEOTHESAURUS, topics[i]);
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
	                        }
	                        setAttributeToSession(request, SIMILAR_TOPICS_GEOTHESAURUS, similarTopics);
	                        break;
	                    }
	                }
	                setAttributeToSession(request, LIST_SIZE_GEOTHESAURUS, list_size);
	            }
	        }
	        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request);
	        setAttributeToSession(request, "geothesaurusSelectTopics", geothesaurusSelectTopics);
        }
	}
	
	private static void setGeothesaurusParamsToContext (RenderRequest request, Context context){

		// Nach Raumbezug suchen
        IngridHit[] geothesaurusTopics = (IngridHit []) getAttributeFromSession(request, TOPICS_GEOTHESAURUS);
        context.put("geothesaurusTopics", geothesaurusTopics);
        context.put("geothesaurusTopicsBrowse", getAttributeFromSession(request, SIMILAR_TOPICS_GEOTHESAURUS));
        context.put("geothesaurusCurrentTopic", getAttributeFromSession(request, CURRENT_TOPIC_GEOTHESAURUS));
        context.put("list_size", getAttributeFromSession(request, LIST_SIZE_GEOTHESAURUS));
        
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request);
        context.put("geothesaurusSelectTopics", geothesaurusSelectTopics);
        if(getAttributeFromSession(request, "doGeothesaurus") != null){
        	context.put("doGeothesaurus", getAttributeFromSession(request, "doGeothesaurus"));
        }
        context.put("geothesaurusTerm", getAttributeFromSession(request, "geothesaurusTerm"));
        context.put("geothesaurusError", getAttributeFromSession(request, ERROR_GEOTHESAURUS));
	}

	
	private static void addGeothesaurusToQuery(PortletRequest request, IngridQuery query) {

		ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request);
        
		if(geothesaurusSelectTopics != null && geothesaurusSelectTopics.size() > 0){
	    	if(query != null){
	    	   for (int i = 0; i < geothesaurusSelectTopics.size(); i++) {
	    		   HashMap<String, String> map = (HashMap<String, String>) geothesaurusSelectTopics.get(i);
	    		   String topicId = (String) map.get("topicId");
	               if (topicId != null && topicId.length() > 0) {
	            	   query.addField(new FieldQuery(true, false, "areaid", topicId));
	               }
	           }
	    	}
	    }
	}
	
	private static ArrayList<HashMap<String, String>> getSelectedGeothesaurusTopics(PortletRequest request){
		IngridHit[] geothesaurusTopics = (IngridHit []) getAttributeFromSession(request, TOPICS_GEOTHESAURUS);
        Topic currentTopic = (Topic) getAttributeFromSession(request, CURRENT_TOPIC_GEOTHESAURUS);
		IngridHit[] assocTopics = (IngridHit []) getAttributeFromSession(request, SIMILAR_TOPICS_GEOTHESAURUS);
        
		ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, "geothesaurusSelectTopicsId");
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = new ArrayList<HashMap<String, String>> ();
        if(selectedIds != null){
        	boolean foundTopicId = false;
        	for(int i = 0; i < selectedIds.size(); i++){
        		if(geothesaurusTopics != null){
                	for(int j = 0; j < geothesaurusTopics.length; j++){
	            		Topic topic = (Topic) geothesaurusTopics[j];
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
	                			foundTopicId = true;
	                			break;
	                		}
	        			}
                	}
            	}
            	if(assocTopics != null){
        			if(!foundTopicId){
        				for(int j = 0; j < assocTopics.length; j++){
                    		Topic topic = (Topic) assocTopics[j];
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
                        			foundTopicId = true;
    	                			break;
                        		}
                			}
                    	}
                	}
        		}
            	
        		if(currentTopic != null){
        			if(!foundTopicId){
        				if(currentTopic.getTopicID().indexOf((String)selectedIds.get(i)) > -1){
        					HashMap<String, String> map = new HashMap<String, String>();
                			map.put("topicTitle", currentTopic.getTopicName().toString());
                			map.put("topicId", (String)selectedIds.get(i));
                			geothesaurusSelectTopics.add(map);
                			foundTopicId = true;
        				}
                	}
        		}
        		foundTopicId = false;
            }
        }
       
        return geothesaurusSelectTopics;
	}
	
	/***************************** Thesaurus *****************************************/
	
	private static void setFaceteThesaurusParamsToSession(ActionRequest request) {
		
		String doThesaurus = request.getParameter("doThesaurus");
		String doCancelThesaurus = request.getParameter("doCancelThesaurus");
		String doAddThesaurus = request.getParameter("doAddThesaurus");
		String doSearchThesaurus = request.getParameter("doSearchThesaurus");
		String doBrowseThesaurus = request.getParameter("doBrowseThesaurus");
		String doBrowseSimilarThesaurus = request.getParameter("doBrowseSimilarThesaurus");
		String doRemoveThesaurus = request.getParameter("doRemoveThesaurus");
		
		if(doCancelThesaurus != null){
			setAttributeToSession(request, "doThesaurus", false);
        
		}else{
        	
        	if(doThesaurus != null){
				if(doThesaurus.equals("true")){
					setAttributeToSession(request, "doThesaurus", true);
				}else{
					setAttributeToSession(request, "doThesaurus", false);
				}
			}
			
			if(doAddThesaurus != null){
				setAttributeToSession(request, "doThesaurus", false);
				if(getAttributeFromSession(request, LIST_SIZE_THESAURUS) != null){
		        	String listSize = getAttributeFromSession(request, LIST_SIZE_THESAURUS).toString();
	        		ArrayList<String> selectedIds = new ArrayList<String> ();
	                for (int i=0; i< Integer.parseInt(listSize); i++) {
	                    String chkVal = request.getParameter("chk"+(i+1));
	                    if (chkVal != null) {
	                    	selectedIds.add(chkVal);
	                    }
	                }
	                if(selectedIds != null){
	                	setAttributeToSession(request, "thesaurusSelectTopicsId", selectedIds);
	                }
	        	}
				
				ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
		        setAttributeToSession(request, "thesaurusSelectTopics", thesaurusSelectTopics);
			}
			
			if(doRemoveThesaurus != null && doAddThesaurus == null){
				if(doRemoveThesaurus.equals("all")){
					removeAttributeFromSession(request, "thesaurusSelectTopicsId");
				}else{
					ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, "thesaurusSelectTopicsId");
					if (selectedIds != null){
						for(int i = 0; i < selectedIds.size(); i++){
							if(selectedIds.get(i).equals(doRemoveThesaurus)){
								selectedIds.remove(i);
							}
						}
						setAttributeToSession(request, "thesaurusSelectTopicsId", selectedIds);
					}
				}
				setAttributeToSession(request, "doThesaurus", false);
			}
			if (doSearchThesaurus != null){
				setAttributeToSession(request, "doThesaurus", true);
				removeAttributeFromSession(request, TOPICS_THESAURUS);
				removeAttributeFromSession(request, SIMILAR_TOPICS_THESAURUS);
				removeAttributeFromSession(request, CURRENT_TOPIC_THESAURUS);
				
				String thesaurusTerm = null;
				SearchExtEnvTopicThesaurusForm f = (SearchExtEnvTopicThesaurusForm) Utils.getActionForm(request, SearchExtEnvTopicThesaurusForm.SESSION_KEY, SearchExtEnvTopicThesaurusForm.class);        
	            f.clearErrors();
	            f.populate(request);
	            if (f.validate()) {
	            	thesaurusTerm = f.getInput(SearchExtEnvTopicThesaurusForm.FIELD_SEARCH_TERM);
	            	if(thesaurusTerm != null){
	            		setAttributeToSession(request, "thesaurusTerm", thesaurusTerm);
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
	                    }
	                	setAttributeToSession(request, TOPICS_THESAURUS, topics);
	                	setAttributeToSession(request, LIST_SIZE_THESAURUS, topics.length);
	                	setAttributeToSession(request, ERROR_THESAURUS, false);
	                }else{
	                	setAttributeToSession(request, ERROR_THESAURUS, true);
	                }
	            }
	        }
	        
	        if(doBrowseThesaurus != null || doBrowseSimilarThesaurus != null){
	        	setAttributeToSession(request, "doThesaurus", true);
	        	String topicID = request.getParameter("topicID");
	            String plugID = request.getParameter("plugID");
	            String docID = request.getParameter("docID");
	            
	            Topic topic = new Topic();
	            topic.setDocumentId(Integer.parseInt(docID));
	            topic.setPlugId(plugID);
	            topic.setTopicID(topicID);
	            
	            Topic currentTopic = (Topic)SNSSimilarTermsInterfaceImpl.getInstance().getDetailsTopic(topic, "/thesa", request.getLocale());
	            setAttributeToSession(request, CURRENT_TOPIC_THESAURUS, currentTopic);
	        	IngridHit[] assocTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(topicID, request.getLocale());
	        	setAttributeToSession(request, SIMILAR_TOPICS_THESAURUS, Arrays.asList(assocTopics));
	        	setAttributeToSession(request, LIST_SIZE_THESAURUS, assocTopics.length + 1);
	        }
	        
	    	ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
	        setAttributeToSession(request, "thesaurusSelectTopics", thesaurusSelectTopics);
		}
	}
	
	private static void setThesaurusParamsToContext (RenderRequest request, Context context){

		// Nach Raumbezug suchen
        IngridHit[] thesaurusTopics = (IngridHit []) getAttributeFromSession(request, TOPICS_THESAURUS);
        context.put("thesaurusTopics", thesaurusTopics);
        context.put("thesaurusTopicsBrowse", getAttributeFromSession(request, SIMILAR_TOPICS_THESAURUS));
        context.put("thesaurusCurrentTopic", getAttributeFromSession(request, CURRENT_TOPIC_THESAURUS));
        context.put("list_size", getAttributeFromSession(request, LIST_SIZE_THESAURUS));
        context.put("thesaurusSelectTopics", getAttributeFromSession(request, "thesaurusSelectTopics"));
        context.put("doThesaurus", getAttributeFromSession(request, "doThesaurus"));
        context.put("thesaurusTerm", getAttributeFromSession(request, "thesaurusTerm"));
        
        ArrayList<Topic> narrowerTermAssoc = new ArrayList<Topic>();
        ArrayList<Topic> widerTermAssoc = new ArrayList<Topic>();
        ArrayList<Topic> synonymAssoc = new ArrayList<Topic>();
        ArrayList<Topic> relatedTermsAssoc = new ArrayList<Topic>();
        
        Object obj = getAttributeFromSession(request, SIMILAR_TOPICS_THESAURUS);
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
        context.put("thesaurusNarrowerTerm", narrowerTermAssoc);
        context.put("thesaurusWiderTerm", widerTermAssoc);
        context.put("thesaurusSynonym", synonymAssoc);
        context.put("thesaurusRelatedTerms", relatedTermsAssoc);
        context.put("thesaurusError", getAttributeFromSession(request, ERROR_THESAURUS));
	}

	
	private static void addThesaurusToQuery(PortletRequest request, IngridQuery query) {

		ArrayList<HashMap<String, String>> thesaurusSelectTopics = getSelectedThesaurusTopics(request);
        
		if(thesaurusSelectTopics != null && thesaurusSelectTopics.size() > 0){
    		ClauseQuery cq = null;
    		for (int i = 0; i < thesaurusSelectTopics.size(); i++) {
    		  HashMap<String, String> map = (HashMap<String, String>) thesaurusSelectTopics.get(i);
    		  cq = new ClauseQuery(true, false);
   	            
   	    	   if (map != null) {
            	   String term = (String) map.get("topicTitle");
            	   if(term != null){
            		   cq.addTerm(new TermQuery(false, false, term));
            	   }
               }
           }
    	   if(cq != null){
    		   query.addClause(cq);   
    	   }
	    }
	}
	
	private static ArrayList<HashMap<String, String>> getSelectedThesaurusTopics(PortletRequest request){
		IngridHit[] thesaurusTopics = (IngridHit []) getAttributeFromSession(request, TOPICS_THESAURUS);
        Topic currentTopic = (Topic) getAttributeFromSession(request, CURRENT_TOPIC_THESAURUS);
		Object assocTopics = getAttributeFromSession(request, SIMILAR_TOPICS_THESAURUS);
        
		ArrayList<String> selectedIds = (ArrayList<String>) getAttributeFromSession(request, "thesaurusSelectTopicsId");
        ArrayList<HashMap<String, String>> thesaurusSelectTopics = new ArrayList<HashMap<String, String>>();
        if(selectedIds != null){
        	boolean foundTopicId = false;
        	for(int i = 0; i < selectedIds.size(); i++){
        		if(thesaurusTopics != null){
                	for(int j = 0; j < thesaurusTopics.length; j++){
	            		Topic topic = (Topic) thesaurusTopics[j];
	        			String topicId = (String) topic.get("topicID");
	        			if(topicId != null){
	        				if(topicId.indexOf((String)selectedIds.get(i)) > -1){
	                        	HashMap<String, String> map = new HashMap<String, String>();
	                			map.put("topicTitle", topic.get("topicName").toString());
	                			map.put("topicId", (String)selectedIds.get(i));
	                			thesaurusSelectTopics.add(map);
	                			foundTopicId = true;
	                			break;
	                		}
	        			}
                	}
            	}
            	if(assocTopics != null){
        			if(!foundTopicId){
        				List topics = (List)assocTopics;
                		for(int j = 0; j < topics.size(); j++){
                    		Topic topic = (Topic) topics.get(j);
                			String topicId = (String) topic.get("topicID");
                			if(topicId != null){
                				if(topicId.indexOf((String)selectedIds.get(i)) > -1){
                                	HashMap<String, String> map = new HashMap<String, String>();
                        			map.put("topicTitle", topic.get("topicName").toString());
                        			map.put("topicId", (String)selectedIds.get(i));
                        			thesaurusSelectTopics.add(map);
                        			foundTopicId = true;
    	                			break;
                        		}
                			}
                    	}
                	}
        		}
            	
        		if(currentTopic != null){
        			if(!foundTopicId){
        				if(currentTopic.getTopicID().indexOf((String)selectedIds.get(i)) > -1){
        					HashMap<String, String> map = new HashMap<String, String>();
                			map.put("topicTitle", currentTopic.getTopicName().toString());
                			map.put("topicId", (String)selectedIds.get(i));
                			thesaurusSelectTopics.add(map);
                			foundTopicId = true;
        				}
                	}
        		}
        		foundTopicId = false;
            }
        }
       
        return thesaurusSelectTopics;
	}
	
	/***************************** Attribute ****************************************/
	
	private static void setFaceteAttributeParamsToSession(ActionRequest request) {
		
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
	
	private static void setAttributeParamsToContext (RenderRequest request, Context context){

		context.put("doAddAttribute", getAttributeFromSession(request, "doAddAttribute"));
	}
	
	private static void addAttributeToQuery(PortletRequest request, IngridQuery query) {
		
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
	
	private static void setFaceteAreaAddressParamsToSession(ActionRequest request) {
		
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
	
	private static void setAreaAddressParamsToContext (RenderRequest request, Context context){

		context.put("doAddAreaAddress", getAttributeFromSession(request, "doAddAreaAddress")); 
	}
	
	private static void addAreaAddressToQuery(PortletRequest request, IngridQuery query) {
		
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
	}
	
	private static ArrayList<HashMap<String, Long>> sortHashMapAsArrayList(HashMap<String, Long> input){
		List<String> keys = new ArrayList<String>(input.keySet());
		final Map<String, Long> tmpInput = input;
        
		Collections.sort(keys,new Comparator(){
            public int compare(Object left, Object right){
                String leftKey = (String)left;
                String rightKey = (String)right;
  
                Long leftValue = (Long)tmpInput.get(leftKey);
                Long rightValue = (Long)tmpInput.get(rightKey);
                return leftValue.compareTo(rightValue) * -1;
            }
        });
		ArrayList<HashMap<String, Long>> sortedInput = new ArrayList<HashMap<String, Long>>();
        
		for(Iterator<String> i=keys.iterator(); i.hasNext();){
            String k = i.next();
            HashMap<String, Long> map = new HashMap<String, Long>();
            map.put(k, tmpInput.get(k));
            sortedInput.add(map); 
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
				removeAttributeFromSession(request, SELECTED_PROVIDER);
				removeAttributeFromSession(request, SELECTED_TOPIC);
				removeAttributeFromSession(request, SELECTED_METACLASS);
				removeAttributeFromSession(request, SELECTED_DATATYPE);
				removeAttributeFromSession(request, SELECTED_MEASURES);
				removeAttributeFromSession(request, SELECTED_SERVICE);
				removeAttributeFromSession(request, "doGeothesaurus");  
				removeAttributeFromSession(request, "geothesaurusSelectTopicsId");  
				removeAttributeFromSession(request, "geothesaurusTerm");  
				removeAttributeFromSession(request, "geothesaurusSelectTopics");  
				removeAttributeFromSession(request, TOPICS_GEOTHESAURUS);  
				removeAttributeFromSession(request, ERROR_GEOTHESAURUS);  
				removeAttributeFromSession(request, CURRENT_TOPIC_GEOTHESAURUS);  
				removeAttributeFromSession(request, SIMILAR_TOPICS_GEOTHESAURUS);  
				removeAttributeFromSession(request, LIST_SIZE_GEOTHESAURUS);  
				removeAttributeFromSession(request, "doThesaurus");  
				removeAttributeFromSession(request, "thesaurusSelectTopicsId");  
				removeAttributeFromSession(request, "thesaurusSelectTopics");  
				removeAttributeFromSession(request, "thesaurusTerm");  
				removeAttributeFromSession(request, TOPICS_THESAURUS);  
				removeAttributeFromSession(request, LIST_SIZE_THESAURUS);  
				removeAttributeFromSession(request, ERROR_THESAURUS);  
				removeAttributeFromSession(request, CURRENT_TOPIC_THESAURUS);  
				removeAttributeFromSession(request, SIMILAR_TOPICS_THESAURUS);  
				removeAttributeFromSession(request, ENABLE_FACETE_PARTNER_LIST);  
				removeAttributeFromSession(request, "doTime");  
				removeAttributeFromSession(request, "doMapCoords");  
				removeAttributeFromSession(request, "coordOptions");  
				removeAttributeFromSession(request, "doAddAttribute");  
				removeAttributeFromSession(request, "doAddAreaAddress");
			}
			setAttributeToSession(request, "faceteTerm", portalTerm);
			setAttributeToSession(request, "faceteDS", portalDS);
		}
	}
	
}



