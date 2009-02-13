package de.ingrid.mdek.dwr.services.sns;

import java.net.URL;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import de.ingrid.iplug.sns.SNSClient;
import de.ingrid.iplug.sns.SNSController;
import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class SNSService {

	private final static Logger log = Logger.getLogger(SNSService.class);	

	private static final String SNS_ROOT_TOPIC = "toplevel"; 
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
    }
	
    public ArrayList<SNSTopic> getRootTopics() {
    	log.debug("getRootTopics()");
    	return getSubTopics(SNS_ROOT_TOPIC, 1, "down");
    }
    
    public ArrayList<SNSTopic> getSubTopics(String topicID, long depth, String direction) {
    	log.debug("getSubTopics("+topicID+", "+depth+", "+direction+")");
    	return getSubTopics(topicID, depth, direction, false, false);
    }


    public ArrayList<SNSTopic> getSubTopicsWithRoot(String topicID, long depth, String direction) {
    	log.debug("getSubTopicsWithRoot("+topicID+", "+depth+", "+direction+")");
    	return getSubTopics(topicID, depth, direction, true, true);
    }
    
    private ArrayList<SNSTopic> getSubTopics(String topicID, long depth, String direction, boolean includeSiblings, boolean includeRootNode) {
    	ArrayList<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	log.debug("getSubTopics("+topicID+", "+depth+", "+direction+", "+includeSiblings+", "+includeRootNode+")");
    	
    	log.debug(" Creating query...");
    	// Create the Query
    	IngridQuery query = null;
    	try {
    		query = QueryStringParser.parse(topicID);
    	} catch (ParseException e) {log.error(e);}
    	log.debug("  Adding fields to query...");

    	query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
        query.addField(new FieldQuery(true, false, "lang", "de"));
        query.put("includeSiblings", includeSiblings);
        query.put("association", "narrowerTermAssoc");
        query.put("depth", depth);
        query.put("direction", direction);
        query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_HIERACHY);

    	log.debug(" calling getHierarchy(...)");        
        IngridHit[] hitsArray = getHierarchy(topicID, depth, direction, includeSiblings);
    	log.debug(" getHierarchy() call returned.");        

    	log.debug(" creating return values...");        
        // TODO Build correct tree structure
        // TODO Check Language
        if (topicID.equals(SNS_ROOT_TOPIC)) {
        	resultList = buildTopicRootStructure(getSuccessors((Topic) hitsArray[0]));
        } else {
        	for (int i = 0; i < hitsArray.length; i++) {
                Topic hit = (Topic) hitsArray[i];
                if (hit.getTopicID().equals(topicID)) {
	                final List successors = getSuccessors(hit);

	                // TODO The returned root structure is invalid (?)
	            	if (includeRootNode) {
	                	ArrayList<Topic> topNode = new ArrayList<Topic>();
	                	topNode.add(hit);
	                	resultList = buildTopicStructure(topNode);            	            		
	            	} else {
	            		resultList = buildTopicStructure(successors);
	            	}
	            	log.debug("  done creating return values. getSubTopics() returning values.");
	                Collections.sort(resultList, new SNSTopicComparator());
	            	return resultList;
                }
            }
        }

        return resultList;
    }


    private static ArrayList<SNSTopic> buildTopicStructure(List<Topic> topics) {
    	ArrayList<SNSTopic> result = new ArrayList<SNSTopic>(); 

    	for (Topic topic : topics) {
    		if (topic.getLanguage().equalsIgnoreCase(THESAURUS_LANGUAGE_FILTER)) {	// Only add 'german' terms
	    		SNSTopic resultTopic = convertTopicToSNSTopic(topic);
	    		List<Topic> succ = getSuccessors(topic);
	
	    		if (succ != null && !succ.isEmpty())
	    		{
	        		ArrayList<SNSTopic> children = buildTopicStructure(succ); 
	    			resultTopic.setChildren(children);
	
	    			for (SNSTopic child : children) {
	        			ArrayList<SNSTopic> parents = new ArrayList<SNSTopic>();
	        			parents.add(resultTopic);
	    				child.setParents(parents);
	    			}
	    		}
	    		result.add(resultTopic);
    		}
    	}
    	return result;
    }

    private static ArrayList<SNSTopic> buildTopicRootStructure(List<Topic> topicList) {
    	ArrayList<SNSTopic> result = new ArrayList<SNSTopic>(); 

    	TreeSet<Topic> topics = new TreeSet<Topic>(new TopicComparator());
    	topics.addAll(topicList);

    	for (Topic topic : topics) {
    		SNSTopic resultTopic = convertTopicToSNSTopic(topic);
    		result.add(resultTopic);
    	}
    	return result;
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

    public ArrayList<SNSTopic> findTopics(String queryTerm) {
    	ArrayList<SNSTopic> resultList = new ArrayList<SNSTopic>();
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
	            	log.debug("Adding: ["+getTypeFromTopic(topic)+", "+getSourceFromTopic(topic)+", "+topic.getId()+", "+topic.getBaseName(0).getBaseNameString().get_value()+"]");
				}
	        }
	    }

	    Collections.sort(resultList, new SNSTopicComparator());
	    return resultList;
    }

    public ArrayList<SNSTopic> getSimilarTerms(String[] queryTerms) {
    	return getSimilarTerms(queryTerms, MAX_NUM_RESULTS);
    }

    private ArrayList<SNSTopic> getSimilarTerms(String[] queryTerms, int numResults) {
    	ArrayList<SNSTopic> resultList = new ArrayList<SNSTopic>();
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

    public ArrayList<SNSTopic> getSimilarDescriptors(String queryTerm) {
    	String[] words = queryTerm.split(" ");
    	ArrayList<SNSTopic> result = new ArrayList<SNSTopic>();
    	
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


    public ArrayList<SNSTopic> getTopicsForText(String queryTerm) {
    	ArrayList<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	int[] totalSize = new int[] {0};
	    DetailedTopic[] snsResults = new DetailedTopic[0];
    	try {
    		snsResults = snsController.getTopicsForText(queryTerm, MAX_ANALYZED_WORDS, "/thesa", "mdek", THESAURUS_LANGUAGE_FILTER, totalSize, false);
    	} catch (AxisFault f) {
    		throw new RuntimeException(ERROR_SNS_TIMEOUT);
    	} catch (Exception e) {
	    	log.error("Error calling snsController.getTopicsForText", e);
    	}

	    totalSize[0] = snsResults.length;
	    IngridHits res = new IngridHits("mdek", totalSize[0], snsResults, false);
	    Topic[] topics = (Topic[]) res.getHits();

	    for (Topic topic : topics) {
	    	if (getTypeFromTopic(topic) == Type.DESCRIPTOR) {
	    		resultList.add(convertTopicToSNSTopic(topic));
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

    	ArrayList<SNSTopic> synonyms = new ArrayList<SNSTopic>();
    	ArrayList<SNSTopic> parents = new ArrayList<SNSTopic>();
    	ArrayList<SNSTopic> children = new ArrayList<SNSTopic>();

	    for (Topic topic : snsResults) {
    		SNSTopic t = convertTopicToSNSTopic(topic);
        	log.debug("Found: ["+getAssociationFromTopic(topic)+", "+getTypeFromTopic(topic)+", "+getSourceFromTopic(topic)+", "+topic.getTopicID()+", "+topic.getTopicName()+"]");

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

	    SNSTopic result = new SNSTopic(Type.DESCRIPTOR, Source.UMTHES, topicId, null);
	    result.setChildren(children);
	    result.setParents(parents);
	    result.setSynonyms(synonyms);

	    return result;
    }

    
    public ArrayList<SNSLocationTopic> getLocationTopics(String queryTerm, String searchTypeStr, String pathStr) {
    	ArrayList<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
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

    public ArrayList<SNSLocationTopic> getLocationTopicsById(String topicID) {
    	ArrayList<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
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

	    	ArrayList<SNSEventTopic> eventTopics = new ArrayList<SNSEventTopic>();
	    	ArrayList<SNSLocationTopic> locationTopics = new ArrayList<SNSLocationTopic>();
	    	ArrayList<SNSTopic> thesaTopics = new ArrayList<SNSTopic>();

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
    	return new SNSTopic(getTypeFromTopic(topic), getSourceFromTopic(topic), topic.getTopicID(), topic.getTopicName());
    }
    private static SNSTopic convertTopicToSNSTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	String topicName = topic.getBaseName(0).getBaseNameString().get_value();
    	return new SNSTopic(getTypeFromTopic(topic), getSourceFromTopic(topic), topic.getId(), topicName);
    }

    private static Source getSourceFromTopic(Topic topic) {
    	// TODO Implement
    	return Source.UMTHES;
    }

    private static Source getSourceFromTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	// TODO Implement
    	return Source.UMTHES;
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
