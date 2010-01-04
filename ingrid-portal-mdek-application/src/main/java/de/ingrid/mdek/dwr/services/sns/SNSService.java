package de.ingrid.mdek.dwr.services.sns;

import java.net.URL;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;

import com.slb.taxi.webservice.xtm.stubs.FieldsType;
import com.slb.taxi.webservice.xtm.stubs.SearchType;
import com.slb.taxi.webservice.xtm.stubs.TopicMapFragment;
import com.slb.taxi.webservice.xtm.stubs.TopicMapFragmentIndexedDocument;
import com.slb.taxi.webservice.xtm.stubs.xtm.Occurrence;

import de.ingrid.external.FullClassifyService;
import de.ingrid.external.GazetteerService;
import de.ingrid.external.ThesaurusService;
import de.ingrid.external.om.Term;
import de.ingrid.external.om.TreeTerm;
import de.ingrid.external.om.Term.TermType;
import de.ingrid.iplug.sns.SNSClient;
import de.ingrid.iplug.sns.SNSController;
import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;

public class SNSService {

	private final static Logger log = Logger.getLogger(SNSService.class);	

	// Switch to "rs:" when the native key changes
	// private static final String SNS_NATIVE_KEY_PREFIX = "rs:"; 
	private static final String SNS_NATIVE_KEY_PREFIX = "ags:"; 
    private static final String THESAURUS_LANGUAGE_FILTER = "de";
    private static final int MAX_NUM_RESULTS = 100;
    private static final int MAX_ANALYZED_WORDS = 1000;
    private static final int SNS_TIMEOUT = 30000;

    // Error string for the frontend
    private static String ERROR_SNS_TIMEOUT = "SNS_TIMEOUT";
    private static String ERROR_SNS_INVALID_URL = "SNS_INVALID_URL";
    
    private static final SimpleDateFormat expiredDateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    // Settings and language specific values
    private ResourceBundle resourceBundle; 
    private SNSController snsController;
    private SNSClient snsClient;

    private ThesaurusService thesaurusService;
    private GazetteerService gazetteerService;
    private FullClassifyService fullClassifyService;

    // The three main SNS topic types
    private enum TopicType {EVENT, LOCATION, THESA}


    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
		resourceBundle = ResourceBundle.getBundle("sns");

    	snsClient = new SNSClient(
    			resourceBundle.getString("sns.username"),
    			resourceBundle.getString("sns.password"),
    			resourceBundle.getString("sns.language"),
        		new URL(resourceBundle.getString("sns.serviceURL")));
    	snsClient.setTimeout(SNS_TIMEOUT);
    	snsController = new SNSController(snsClient, SNS_NATIVE_KEY_PREFIX);

    	de.ingrid.external.sns.SNSService snsService = new de.ingrid.external.sns.SNSService();
		try {
			snsService.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		thesaurusService = snsService;
		gazetteerService = snsService;
		fullClassifyService = snsService;
    }

    public List<SNSTopic> getRootTopics() {
    	log.debug("getRootTopics()");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm[] treeTerms = thesaurusService.getHierarchyNextLevel(null, Locale.GERMAN);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator());
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// NO ADDING OF CHILDREN !!!!!!!!!
    		SNSTopic resultTopic = convertTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }

    public List<SNSTopic> getSubTopics(String topicID, long depth, String direction) {
    	log.debug("getSubTopics("+topicID+", "+depth+", "+direction+")");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm[] treeTerms = thesaurusService.getHierarchyNextLevel(topicID, Locale.GERMAN);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator());
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// ADDING OF CHILDREN !!!!!!!!!
    		SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }

    public List<SNSTopic> getSubTopicsWithRoot(String topicID, long depth, String direction) {
    	log.debug("getSubTopicsWithRoot("+topicID+", "+depth+", "+direction+")");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm[] treeTerms = thesaurusService.getHierarchyPathToTop(topicID, Locale.GERMAN);

    	// Notice we have to build different structure for return list !
    	// if topnode then empty list is returned
    	// if subnode then parent is encapsulated in child list !
    	if (treeTerms.length > 1) {
    		SNSTopic lastTopic = null;
        	for (TreeTerm treeTerm : treeTerms) {
        		SNSTopic currentTopic = convertTermToSNSTopic(treeTerm);
        		if (lastTopic == null) {
            		resultList.add(currentTopic);
        		} else {
        			// set last topic as parent in current topic (but is child in hierarchy !)
        			List<SNSTopic> parents = new ArrayList<SNSTopic>();
        			parents.add(lastTopic);
        			currentTopic.setParents(parents);

        			// set current topic as child in last topic (but is parent in hierarchy !)
        			List<SNSTopic> children = new ArrayList<SNSTopic>();
        			children.add(currentTopic);
        			lastTopic.setChildren(children);
        		}
        		lastTopic = currentTopic;
        	}
    	}

    	return resultList;
    }

    private static Type getTypeFromTopic(Topic t) {
    	String nodeType = t.getSummary();
    	return getTypeForNodeType(nodeType);
    }
    
    private static Type getTypeFromTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic t) {
    	String nodeType = t.getInstanceOf(0).getTopicRef().getHref();
    	return getTypeForNodeType(nodeType);
    }

    private static Type getTypeForNodeType(String nodeType) {
		if (nodeType.indexOf("topTermType") != -1) 
			return Type.TOP_TERM;
		else if (nodeType.indexOf("nodeLabelType") != -1) 
			return Type.NODE_LABEL;
		else if (nodeType.indexOf("descriptorType") != -1) 
			return Type.DESCRIPTOR;
		else if (nodeType.indexOf("nonDescriptorType") != -1) 
			return Type.NON_DESCRIPTOR;
		else
			return Type.TOP_TERM;
    }

    private static Type getTypeFromTerm(Term term) {
    	// first check whether we have a tree term ! Only then we can determine whether top node !
		if (TreeTerm.class.isAssignableFrom(term.getClass())) {
	    	if (((TreeTerm)term).getParent() == null) {
	    		return Type.TOP_TERM;
	    	}    	
		}

    	TermType termType = term.getType();
    	if (termType == TermType.NODE_LABEL) 
			return Type.NODE_LABEL;
		if (termType == TermType.DESCRIPTOR) 
			return Type.DESCRIPTOR;
		if (termType == TermType.NON_DESCRIPTOR) 
			return Type.NON_DESCRIPTOR;
		return Type.TOP_TERM;
    }

    private static String getAssociationFromTopic(Topic t) {
    	String assoc = t.getTopicAssoc();
    	return assoc.split("#")[1];
    }

    
    public void testIncludeSiblings(boolean includeSiblings) {
        // getHierarchy?root=uba_thes_27118&user=ingrid_test&password=ingrid_test&depth=2&direction=up&includeSiblings=true
    	// false liefert drei Knoten
    	// true liefert 10 Knoten
    
        IngridHit[] hitsArray = getHierarchy("uba_thes_27118", 2, "up", includeSiblings);

        for (IngridHit hit : hitsArray) {
        	printTopic((Topic) hit);
        	
        }
    }

    private IngridHit[] getHierarchy(String topicID, long depth, String direction, boolean includeSiblings) {
    	log.debug("getHierarchy("+topicID+", "+depth+", "+direction+", "+includeSiblings+")");
    	
    	int[] totalSize = new int[1];
	    totalSize[0] = 0;
	    Topic[] snsResults = new Topic[0];
	    try {
	    	log.debug(" calling snsController.getTopicHierarchy("+totalSize+", \"narrowerTermAssoc\", "+depth+", "+direction+", "+includeSiblings+", \"de\", "+topicID+", false, \"mdek\")");
	    	snsResults = snsController.getTopicHierachy(totalSize, "narrowerTermAssoc", depth, direction, includeSiblings, "de", topicID, false, "mdek");
	    	log.debug(" call snsController.getTopicHierarchy() returned successfully");
	    }
	    catch (Exception e) {log.error(e);}
	
	    totalSize[0] = snsResults.length;
	    IngridHits hits = new IngridHits("mdek", totalSize[0], snsResults, false);

	    return hits.getHits();
    }

    /**
     * find the topic with name 'queryTerm'. If no topic is found with the given name, null is returned
     * @param queryTerm topic name to search for
     * @return the SNSTopic if it exists, null otherwise
     */
    public SNSTopic findTopic(String queryTerm) {
    	TopicMapFragment mapFragment = null;
    	try {
    		mapFragment = snsClient.findTopics(queryTerm, "/thesa", SearchType.exact,
    	            FieldsType.names, 0, THESAURUS_LANGUAGE_FILTER, true);
    	} catch (Exception e) {
	    	log.error(e);
	    }
	    
	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics)) {
	            for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
					String topicName = topic.getBaseName(0).getBaseNameString().get_value();
	            	if (topicName.equalsIgnoreCase(queryTerm))
	            		return convertTopicToSNSTopic(topic);
	            }
	        }
	    }
	    return null;
    }

    /**
     * Find all topics for a given query string
     * @param queryTerm topic name to search for
     * @return All topics returned by the SNS, converted to SNSTopics 
     */
    public List<SNSTopic> findTopics(String queryTerm) {
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	TopicMapFragment mapFragment = null;
    	try {
    		mapFragment = snsClient.findTopics(queryTerm, "/thesa", SearchType.exact,
    	            FieldsType.captors, 0, THESAURUS_LANGUAGE_FILTER, true);
    	} catch (Exception e) {
	    	log.error(e);
	    }

	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics)) {
	            for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
	            	resultList.add(convertTopicToSNSTopic(topic));
//	            	log.debug("Adding: ["+getTypeFromTopic(topic)+", "+getSourceFromTopic(topic)+", "+topic.getId()+", "+topic.getBaseName(0).getBaseNameString().get_value()+"]");
				}
	        }
	    }

	    Collections.sort(resultList, new SNSTopicComparator());
	    return resultList;
    }

    /**
     * getPSI for 'topicId'.
     * @param topicId topic id to search for
     * @return the SNSTopic if it exists, null otherwise
     * @throws Exception if there was a connection/communication error with the SNS
     */
    public SNSTopic getPSI(String topicId) throws Exception {
    	TopicMapFragment mapFragment = null;
   		mapFragment = snsClient.getPSI(topicId, 0, "/thesa");

	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics)) {
	            for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
	            	if (topic.getId().equals(topicId)) {
	            		return convertTopicToSNSTopic(topic);
	            	}
	            }
	        }
	    }
	    return null;
    }

    /**
     * getPSI for location topics 'topicId'.
     * @param topicId topic id to search for
     * @return the SNSLocationTopic if it exists, null otherwise
     * @throws Exception if there was a connection/communication error with the SNS
     */
    public SNSLocationTopic getLocationPSI(String topicId) throws Exception {
    	TopicMapFragment mapFragment = null;
   		mapFragment = snsClient.getPSI(topicId, 0, "/location");

	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics)) {
	            for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
	            	if (topic.getId().equals(topicId)) {
		            	return createLocationTopic(topic);
	            	}
	            }
	        }
	    }
	    return null;
    }
    
    public List<SNSTopic> getSimilarTerms(String[] queryTerms) {
    	return getSimilarTerms(queryTerms, MAX_NUM_RESULTS);
    }

    private List<SNSTopic> getSimilarTerms(String[] queryTerms, int numResults) {
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	int[] totalSize = new int[] {0};
	    Topic[] snsResults = new Topic[0];
    	try {
    		snsResults = snsController.getSimilarTermsFromTopic(queryTerms, numResults, "mdek", totalSize, THESAURUS_LANGUAGE_FILTER);
    	} catch (Exception e) {
	    	log.error(e);
	    }

	    totalSize[0] = snsResults.length;
	    IngridHits res = new IngridHits("mdek", totalSize[0], snsResults, false);
	    Topic[] topics = (Topic[]) res.getHits();

	    for (Topic topic : topics) {
	    	if (getTypeFromTopic(topic) == Type.DESCRIPTOR) {
	    		resultList.add(convertTopicToSNSTopic(topic));
	    	}
	    }

	    return resultList;
    }

    public List<SNSTopic> getSimilarDescriptors(String queryTerm) {
    	String[] words = queryTerm.split(" ");
    	List<SNSTopic> result = new ArrayList<SNSTopic>();
    	
    	for (int i = 0; i < words.length; i+=MAX_ANALYZED_WORDS) {
    		String queryStr = "";
    		for (int j = i; j < words.length && j < i+MAX_ANALYZED_WORDS; j++)
    			queryStr += words[j]+" ";

        	log.debug("sns query for words starting at: "+i);
    		log.debug("Query String: "+queryStr);
        	result.addAll(getTopicsForText(queryStr));
    	}

    	return result;
    }


    public List<SNSTopic> getTopicsForText(String queryTerm) {
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	int[] totalSize = new int[] {0};
    	TopicMapFragment mapFragment = null;
    	
    	try {
    		mapFragment = snsClient.autoClassify(queryTerm, MAX_ANALYZED_WORDS, "/thesa", false, THESAURUS_LANGUAGE_FILTER);
	    } catch (Exception e) {
	    	log.error(e);
	    }
	    
	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics)) {
	            for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
	            	if (getTypeFromTopic(topic) == Type.DESCRIPTOR) {
	            		resultList.add(convertTopicToSNSTopic(topic));
	            	}
				}
	        }
	    }

//	    log.debug("Number of descriptors in the result: "+resultList.size());
	    return resultList;
    }
    
    // Returns the topicID encapsulated in a SNSTopic with the parents, children and synonyms attached
    public SNSTopic getTopicsForTopic(String topicId) {
    	int[] totalSize = new int[] {0};
	    Topic[] snsResults = new Topic[0];
    	try {
    		snsResults = snsController.getTopicsForTopic(topicId, MAX_NUM_RESULTS, "mdek", totalSize, false);
    	} catch (AxisFault f) {
    		throw new RuntimeException(ERROR_SNS_TIMEOUT);
    	} catch (Exception e) {
	    	log.error("Error calling snsController.getTopicsForTopic", e);
    	}

    	List<SNSTopic> synonyms = new ArrayList<SNSTopic>();
    	List<SNSTopic> parents = new ArrayList<SNSTopic>();
    	List<SNSTopic> children = new ArrayList<SNSTopic>();

    	if (snsResults != null && snsResults.length != 0) {
		    for (Topic topic : snsResults) {
	    		SNSTopic t = convertTopicToSNSTopic(topic);
	        	log.debug("Found: " + topic);
	
	        	String assoc = getAssociationFromTopic(topic);
	    		if (assoc.equals("widerTermMember")) {
	    			parents.add(t);
	
	    		} else if (assoc.equals("narrowerTermMember")) {
	    			children.add(t);
	    			
	    		} else if (assoc.equals("synonymMember")) {
	    			synonyms.add(t);
	
	    		} else if (assoc.equals("descriptorMember")) {
	    			return t;
	
	    		}
		    }

			SNSTopic result = new SNSTopic(Type.DESCRIPTOR, Source.UMTHES, topicId, null, null, null);
		    result.setChildren(children);
		    result.setParents(parents);
		    result.setSynonyms(synonyms);
	
		    return result;

    	} else {
    		return null;
    	}
    }

    
    public List<SNSLocationTopic> getLocationTopics(String queryTerm, String searchTypeStr, String pathStr) {
    	List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
    	SearchType searchType = getSearchType(searchTypeStr);
    	String path = (pathStr == null) ? "/location" : pathStr;

    	TopicMapFragment mapFragment = null;
    	try {
    		mapFragment = snsClient.findTopics(queryTerm, path, searchType,
    	            FieldsType.captors, 0, THESAURUS_LANGUAGE_FILTER, false);
    	} catch (Exception e) {
	    	log.error(e);
	    }
	    
	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics)) {
	            for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
	            	SNSLocationTopic t = createLocationTopic(topic);
	            	if (t != null) {
	            		resultList.add(t);
	            	}
				}
	        }
	    }
	    return resultList;
    }

    public List<SNSLocationTopic> getLocationTopicsById(String topicID) {
    	List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
    	TopicMapFragment mapFragment = null;
    	try {
    		mapFragment = snsClient.getPSI(topicID, 0, "/location");
	    } catch (Exception e) {
	    	log.error(e);
	    }

	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	    	for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
            	SNSLocationTopic t = createLocationTopic(topic);
            	if (t != null) {
            		resultList.add(t);
            	} else {
            		// log.debug("Couldn't create location topic for result: "+topic.getId());
            	}
	        }
	    }
    	return resultList;
    }

    // SNS autoClassify operation for URLs
    public SNSTopicMap autoClassifyURL(String url, int analyzeMaxWords, String filter, boolean ignoreCase, String lang) {
    	SNSTopicMap result = new SNSTopicMap();
    	TopicMapFragment mapFragment = null;

    	try {
    		mapFragment = snsClient.autoClassifyToUrl(url, analyzeMaxWords, filter, ignoreCase, lang);

    	} catch (AxisFault f) {
    		log.debug("Error while calling autoClassifyToUrl.", f);
    		if (f.getFaultString().contains("Timeout"))
    			throw new RuntimeException(ERROR_SNS_TIMEOUT);
    		else
    			throw new RuntimeException(ERROR_SNS_INVALID_URL);

    	} catch (Exception e) {
	    	log.error("Error calling snsClient.autoClassifyToUrl", e);
    	}

    	if (null != mapFragment) {
    		TopicMapFragmentIndexedDocument indexedDocument = mapFragment.getIndexedDocument();
    		result.setIndexedDocument(new IndexedDocument(indexedDocument));

	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	    	if (null == topics) {
	    		return result;
	    	}

	    	List<SNSEventTopic> eventTopics = new ArrayList<SNSEventTopic>();
	    	List<SNSLocationTopic> locationTopics = new ArrayList<SNSLocationTopic>();
	    	List<SNSTopic> thesaTopics = new ArrayList<SNSTopic>();

	    	for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
            	switch (getTopicType(topic)) {
            	case EVENT:
            		eventTopics.add(createEventTopic(topic));
            		break;
            	case LOCATION:
            		SNSLocationTopic locTopic = createLocationTopic(topic);
            		// createLocationTopic returns null for expired topics
            		if (null != locTopic) {
            			locationTopics.add(locTopic);
            		}

            		break;
            	case THESA:
            		thesaTopics.add(convertTopicToSNSTopic(topic));
            		break;
            	}
	    	}
        	result.setEventTopics(eventTopics);
        	result.setLocationTopics(locationTopics);
        	result.setThesaTopics(thesaTopics);
    	}
    	return result;
    }

    private TopicType getTopicType(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
		String instance = topic.getInstanceOf()[0].getTopicRef().getHref();
//		log.debug("InstanceOf: "+instance);
		if (instance.indexOf("topTermType") != -1 || instance.indexOf("nodeLabelType") != -1
		 || instance.indexOf("descriptorType") != -1 || instance.indexOf("nonDescriptorType") != -1) {
			return TopicType.THESA;

		} else if (instance.indexOf("activityType") != -1 || instance.indexOf("anniversaryType") != -1
				 || instance.indexOf("conferenceType") != -1 || instance.indexOf("disasterType") != -1
				 || instance.indexOf("historicalType") != -1 || instance.indexOf("interYearType") != -1
				 || instance.indexOf("legalType") != -1 || instance.indexOf("observationType") != -1
				 || instance.indexOf("natureOfTheYearType") != -1 || instance.indexOf("publicationType") != -1) {
			return TopicType.EVENT;

		} else { // if instance.indexOf("nationType") != -1 || ...
			return TopicType.LOCATION;
		}
    }

    private SNSEventTopic createEventTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	SNSEventTopic t = new SNSEventTopic();
    	t.setTopicId(topic.getId());
    	t.setName(topic.getBaseName()[0].getBaseNameString().get_value());

    	for (Occurrence occ: topic.getOccurrence()) {
    		if (occ.getInstanceOf().getTopicRef().getHref().endsWith("descriptionOcc")) {
    			if (occ.getScope().getTopicRef()[0].getHref().endsWith("de") && occ.getResourceData() != null)
    				t.setDescription(occ.getResourceData().get_value());

    		} else if (occ.getInstanceOf().getTopicRef().getHref().endsWith("temporalAtOcc")) {        		
    			log.debug("Temporal at: "+occ.getResourceData().get_value());
    			t.setAt(convertTemporalValueToDate(occ.getResourceData().get_value()));

    		} else if (occ.getInstanceOf().getTopicRef().getHref().endsWith("temporalFromOcc")) {        		
    			log.debug("Temporal from: "+occ.getResourceData().get_value());
    			t.setFrom(convertTemporalValueToDate(occ.getResourceData().get_value()));

    		} else if (occ.getInstanceOf().getTopicRef().getHref().endsWith("temporalToOcc")) {        		
    			log.debug("Temporal to: "+occ.getResourceData().get_value());
    			t.setTo(convertTemporalValueToDate(occ.getResourceData().get_value()));
        	}
    	}

    	return t;
    }

    private static SNSTopic convertTopicToSNSTopic(Topic topic) {
    	Source topicSource = getSourceFromTopic(topic);
//    	log.debug("topic source: " + topicSource);
//    	log.debug("topic type: " + getTypeFromTopic(topic));
    	if (Source.UMTHES.equals(topicSource)) {
        	return new SNSTopic(getTypeFromTopic(topic), topicSource, topic.getTopicID(), topic.getTopicName(), null, null);

    	} else {
    		// GEMET
    		return new SNSTopic(getTypeFromTopic(topic), topicSource, topic.getTopicID(), getGemetTitleFromTopic(topic), topic.getTopicName(), getGemetIdFromTopic(topic));
    	}
    }

    private static SNSTopic convertTopicToSNSTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	Source topicSource = getSourceFromTopic(topic);
    	
//    	log.debug("topic source: " + topicSource);
//    	log.debug("topic type: " + getTypeFromTopic(topic));
    	String topicName = topic.getBaseName(0).getBaseNameString().get_value();
    	if (Source.UMTHES.equals(topicSource)) {
    		SNSTopic snsTopic = new SNSTopic(getTypeFromTopic(topic), topicSource, topic.getId(), topicName, null, null);
    		snsTopic.setInspireList(findInspireTopics(topic));
        	return snsTopic;

    	} else {
    		// if GEMET, then the title is used for the title in SNSTopic and, in case UMTHES is different
    		// the UMTHES value is stored in alternateTitle
    		SNSTopic snsTopic = new SNSTopic(getTypeFromTopic(topic), topicSource, topic.getId(), getGemetTitleFromTopic(topic), topicName, getGemetIdFromTopic(topic));
    		snsTopic.setInspireList(findInspireTopics(topic));
        	return snsTopic;
    	}
    }

    /** NO adding of children ! */
    private static SNSTopic convertTermToSNSTopic(Term term) {
    	Type type = getTypeFromTerm(term);
    	String id = term.getId();
    	String name = term.getName();
    	Source topicSource = getSourceFromTerm(term);

    	SNSTopic resultTopic;
    	if (Source.UMTHES.equals(topicSource)) {
    		resultTopic = new SNSTopic(type, topicSource, id, name, null, null);

    	} else {
    		// GEMET
    		resultTopic = new SNSTopic(type, topicSource, id, name, term.getAlternateName(), term.getAlternateId());
    	}

		resultTopic.setInspireList(term.getInspireThemes());
		
    	return resultTopic;
    }

    /** Also adds children ! */
    private static SNSTopic convertTreeTermToSNSTopic(TreeTerm treeTerm) {
    	SNSTopic resultTopic = convertTermToSNSTopic(treeTerm);

    	List<Term> childTerms = treeTerm.getChildren();
		if (childTerms != null) {
			// set up list of mapped children
			List<SNSTopic> childTopics = new ArrayList<SNSTopic>();
			for (Term childTerm : childTerms) {
				SNSTopic childTopic = convertTermToSNSTopic(childTerm);

				// set parent in child
    			List<SNSTopic> parents = new ArrayList<SNSTopic>();
    			parents.add(resultTopic);
    			childTopic.setParents(parents);
				
				childTopics.add(childTopic);
			}
			
			// set children in result
			resultTopic.setChildren(childTopics);
		}

    	return resultTopic;
    }

    private static List<String> findInspireTopics(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	List<String> inspireTopics = new ArrayList<String>();
    	
    	if (null != topic.getOccurrence()) {
	    	for (Occurrence occ: topic.getOccurrence()) {
	    		if (occ.getInstanceOf().getTopicRef().getHref().endsWith("iTheme2007")) {
	    			inspireTopics.add(getInspireTitleFromOccurenceString(occ.getResourceData().get_value()));
	    		}
	    	}
    	}
    	
		return inspireTopics;
	}

    // TODO AW: ENGLISH also!!!
	private static String getInspireTitleFromOccurenceString(String inspireOccurence) {
		if (inspireOccurence != null) {
    		String[] inspireParts = inspireOccurence.split("@");
    		return inspireParts[1];
    	}
		return null;
	}

	private static Source getSourceFromTopic(Topic topic) {
    	return (topic.get(DetailedTopic.GEMET_OCC) != null ? Source.GEMET : Source.UMTHES);
    }

    private static Source getSourceFromTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	Occurrence occ = getOccurrence(topic, "gemet1.0");
    	if (null != occ) {
    		return Source.GEMET;
    	} else {
        	// If there is no occurence of type 'gemet1.0'
    		return Source.UMTHES;
    	}
    }

    private static Source getSourceFromTerm(Term term) {
    	if (term.getAlternateId() != null) {
    		return Source.GEMET;
    	} else {
    		return Source.UMTHES;
    	}
    }

    private static String getGemetTitleFromTopic(Topic topic) {
    	String gemetOccurence = (String) topic.get(DetailedTopic.GEMET_OCC);
    	return getGemetTitleFromOccurenceString(gemetOccurence);
    }

    private static String getGemetTitleFromTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	Occurrence occ = getOccurrence(topic, "gemet1.0");
    	if (null != occ) {
    		return getGemetTitleFromOccurenceString(occ.getResourceData().get_value());

    	} else {
    		return null;
    	}
    }

    private static String getGemetTitleFromOccurenceString(String gemetOccurence) {
//    	log.debug("gemet occurence string: "+gemetOccurence);
    	// gemetOccurence consists of: ID@GERMAN_TITLE@ENGLISH_TITLE
    	// We return the german title
    	if (gemetOccurence != null) {
    		String[] gemetParts = gemetOccurence.split("@");
//        	log.debug("gemet title: "+gemetParts[1]);
    		return gemetParts[1];

    	} else {
    		return null;
    	}
    }

    private static String getGemetIdFromTopic(Topic topic) {
    	String gemetOccurence = (String) topic.get(DetailedTopic.GEMET_OCC);
    	return getGemetIdFromOccurenceString(gemetOccurence);
    }

    private static String getGemetIdFromTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	Occurrence occ = getOccurrence(topic, "gemet1.0");
    	if (null != occ) {
    		return getGemetIdFromOccurenceString(occ.getResourceData().get_value());

    	} else {
    		return null;
    	}
    }

    private static String getGemetIdFromOccurenceString(String gemetOccurence) {
//    	log.debug("gemet occurence string: "+gemetOccurence);
    	if (gemetOccurence != null) {
    		String[] gemetParts = gemetOccurence.split("@");
//        	log.debug("gemet id: "+gemetParts[0]);
    		return gemetParts[0];

    	} else {
    		return null;
    	}
    }


    private static Occurrence getOccurrence(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic, String occurrenceType) {
    	if (null != topic.getOccurrence()) {
	    	for (Occurrence occ: topic.getOccurrence()) {
	    		if (occ.getInstanceOf().getTopicRef().getHref().endsWith(occurrenceType)) {
	    			return occ;
	    		}
	    	}
    	}

    	// If the occurence was not found
    	return null;
    }

    private Date convertTemporalValueToDate(String dateString) {
		SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat yearMonthDateFormat = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    	try { return standardDateFormat.parse(dateString); }
    	catch (java.text.ParseException pe) { log.debug(pe); }

    	try { return yearMonthDateFormat.parse(dateString); }
    	catch (java.text.ParseException pe) { log.debug(pe); }

    	try { return yearDateFormat.parse(dateString); }
    	catch (java.text.ParseException pe) { log.debug(pe); }

    	log.error("Error parsing date: "+dateString);
    	return null;
    }


    private SNSLocationTopic createLocationTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	SNSLocationTopic result = new SNSLocationTopic();
    	result.setTopicId(topic.getId());
    	result.setName(topic.getBaseName(0).getBaseNameString().get_value());
    	String type = topic.getInstanceOf(0).getTopicRef().getHref();
    	type = type.substring(type.lastIndexOf("#")+1);
    	result.setTypeId(type);
    	result.setType(resourceBundle.getString("sns.topic.ref."+type));

    	// If the topic doesn't contain any more information return the basic info
    	if (topic.getOccurrence() == null) {
    		return result;
    	}


    	// Iterate over all occurrences and extract the relevant information (bounding box wgs84 coords and the qualifier)
    	for(int i = 0; i < topic.getOccurrence().length; ++i) {
//    		log.debug(topic.getOccurrence(i).getInstanceOf().getTopicRef().getHref());
    		if (topic.getOccurrence(i).getInstanceOf().getTopicRef().getHref().endsWith("wgs84BoxOcc")) {
//    			log.debug("WGS84 Coordinates: "+topic.getOccurrence(i).getResourceData().get_value());        	            			
        		String coords = topic.getOccurrence(i).getResourceData().get_value();
        		String[] ar = coords.split("\\s|,");
        		if (ar.length == 4) {
        			result.setBoundingBox(new Float(ar[0]), new Float(ar[1]), new Float(ar[2]), new Float(ar[3]));
        		}
    		} else if (topic.getOccurrence(i).getInstanceOf().getTopicRef().getHref().endsWith("qualifier")) {
//    			log.debug("Qualifier: "+topic.getOccurrence(i).getResourceData().get_value());        	            			
        		result.setQualifier(topic.getOccurrence(i).getResourceData().get_value());
    		} else if (topic.getOccurrence(i).getInstanceOf().getTopicRef().getHref().endsWith("nativeKeyOcc")) {
    			String nativeKeyOcc = topic.getOccurrence(i).getResourceData().get_value();
    			String[] keys = nativeKeyOcc.split(" ");
    			for (String nativeKey : keys) {
    				if (nativeKey.startsWith(SNS_NATIVE_KEY_PREFIX)) {
    					result.setNativeKey(nativeKey.substring(SNS_NATIVE_KEY_PREFIX.length()));
    				}
    			}
    		} else if (topic.getOccurrence(i).getInstanceOf().getTopicRef().getHref().endsWith("expiredOcc")) {
                try {
                    Date expiredDate = expiredDateParser.parse(topic.getOccurrence(i).getResourceData().get_value());
                    if ((null != expiredDate) && expiredDate.before(new Date())) {
                        return null;
                    }
                } catch (java.text.ParseException e) {
                    log.error("Not expected date format in sns expiredOcc.", e);
                }
    		}
    	}
    	if (result.getQualifier() == null)
    		result.setQualifier(result.getType());
    	return result;
    }

    private SearchType getSearchType(String searchTypeStr) {
    	if (searchTypeStr == null) {
    		return SearchType.exact;
    		
    	} else if (searchTypeStr.equalsIgnoreCase("exact")) {
    		return SearchType.exact;

    	} else if (searchTypeStr.equalsIgnoreCase("contains")) {
    		return SearchType.contains;

    	} else {
    		return SearchType.beginsWith;
    	}
    }


    private static void printTopic(Topic t) {
    	System.out.println("Title: "+t.getTitle()+" ID: "+t.getTopicID());
    	
		List<Topic> succList = getSuccessors(t);
		if (succList != null && !succList.isEmpty()) {
			for (Topic succ : succList) {
				System.out.print(" ");
				printTopic(succ);
			}
		}
    }

    private static List<Topic> getSuccessors(Topic t) {
    	Set<Topic> successors = t.getSuccessors();
    	return new ArrayList<Topic>(successors);
    }

    static public class TermComparator implements Comparator<Term> {
    	public final int compare(Term termA, Term termB) {
            try {
            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(Locale.GERMAN);
            	return gerCollator.compare(termA.getName(), termB.getName());
            } catch (Exception e) {
                return 0;
            }
        }
    }
    static public class TopicComparator implements Comparator<Topic> {
    	public final int compare(Topic topicA, Topic topicB) {
            try {
            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(Locale.GERMAN);
            	return gerCollator.compare(topicA.getTopicName(), topicB.getTopicName());
            } catch (Exception e) {
                return 0;
            }
        }
    }
    static public class SNSTopicComparator implements Comparator<SNSTopic> {
    	public final int compare(SNSTopic topicA, SNSTopic topicB) {
            try {
            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(Locale.GERMAN);
            	return gerCollator.compare(topicA.getTitle(), topicB.getTitle());
            } catch (Exception e) {
                return 0;
            }
        }
    }
    static public class SNSLocationTopicComparator implements Comparator<SNSLocationTopic> {
    	public final int compare(SNSLocationTopic topicA, SNSLocationTopic topicB) {
            try {
            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(Locale.GERMAN);
            	String labelA = topicA.getName()+" "+topicA.getQualifier();
            	String labelB = topicB.getName()+" "+topicB.getQualifier();
            	return gerCollator.compare(labelA, labelB);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
