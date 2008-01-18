package de.ingrid.mdek.dwr.sns;

import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.slb.taxi.webservice.xtm.stubs.FieldsType;
import com.slb.taxi.webservice.xtm.stubs.SearchType;
import com.slb.taxi.webservice.xtm.stubs.TopicMapFragment;

import de.ingrid.iplug.sns.SNSClient;
import de.ingrid.iplug.sns.SNSController;
import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
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
	private static final String SNS_NATIVE_KEY_PREFIX = "rs:"; 
    private static final String THESAURUS_LANGUAGE_FILTER = "de";
    private static final int MAX_NUM_RESULTS = 100;
    
    // Settings and language specific values
    private ResourceBundle resourceBundle; 
    private SNSController snsController;
    private SNSClient snsClient;
    
	// Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
		resourceBundle = ResourceBundle.getBundle("sns");

    	snsClient = new SNSClient(
    			resourceBundle.getString("sns.username"),
    			resourceBundle.getString("sns.password"),
    			resourceBundle.getString("sns.language"),
        		new URL(resourceBundle.getString("sns.serviceURL")));
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
    	log.debug("getRootTopicsWithRoot("+topicID+", "+depth+", "+direction+")");
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
        for (int i = 0; i < hitsArray.length; i++) {
            Topic hit = (Topic) hitsArray[i];

            final List successors = hit.getSuccessors();
            
            // TODO The returned root structure is invalid (?)
            if (topicID == SNS_ROOT_TOPIC) {
            	resultList = buildTopicRootStructure(successors);
            } else {
            	if (includeRootNode) {
                	ArrayList<Topic> topNode = new ArrayList<Topic>();
                	topNode.add(hit);
                	resultList = buildTopicStructure(topNode);            	            		
            	} else {
            		resultList = buildTopicStructure(successors);
            	}
            }
        }
    	log.debug("  done creating return values. getSubTopics() returning values.");        
        return resultList;
    }


    private static ArrayList<SNSTopic> buildTopicStructure(List<Topic> topics) {
    	ArrayList<SNSTopic> result = new ArrayList<SNSTopic>(); 

    	for (Topic topic : topics) {
    		if (topic.getLanguage().equalsIgnoreCase(THESAURUS_LANGUAGE_FILTER)) {	// Only add 'german' terms
	    		SNSTopic resultTopic = new SNSTopic(getTypeFromTopic(topic), topic.getTopicID(), topic.getTopicName());
	    		List<Topic> succ = topic.getSuccessors();
	
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

    	TreeSet<Topic> topics = new TreeSet<Topic>(new SNSTopicComparator());
    	topics.addAll(topicList);

    	for (Topic topic : topics) {
    		SNSTopic resultTopic = new SNSTopic(getTypeFromTopic(topic), topic.getTopicID(), topic.getTopicName());
    		result.add(resultTopic);
    	}
    	return result;
    }

    
    private static Type getTypeFromTopic(Topic t) {
    	String nodeType = t.getSummary();

		if (nodeType.indexOf("topTermType") != -1) 
			return Type.TOP_TERM;
		else if (nodeType.indexOf("nodeLabelType") != -1) 
			return Type.NODE_LABEL;
		else if (nodeType.indexOf("descriptorType") != -1) 
			return Type.DESCRIPTOR;
		else
			return Type.TOP_TERM;
    }
    
    private static Type getTypeFromTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic t) {
    	String nodeType = t.getInstanceOf(0).getTopicRef().getHref();

		if (nodeType.indexOf("topTermType") != -1) 
			return Type.TOP_TERM;
		else if (nodeType.indexOf("nodeLabelType") != -1) 
			return Type.NODE_LABEL;
		else if (nodeType.indexOf("descriptorType") != -1) 
			return Type.DESCRIPTOR;
		else
			return Type.TOP_TERM;
    }
/*    
    public ArrayList<SNSTopic> findTopics(String q) {
    	ArrayList<SNSTopic> results = new ArrayList<SNSTopic>();

    	IngridQuery query = null;
    	try {
    		query = QueryStringParser.parse(q);
    	} catch (ParseException e) {log.error(e);}

    	query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
//        query.addField(new FieldQuery(true, false, "lang", "de"));
        query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TERM);
//        IngridHits hits = plug.search(query, 0, 600);
        IngridHits hits = null;
        IngridHit[] hitsArray = hits.getHits();

        if (hitsArray != null)
        {
        	for (int i = 0; i < hitsArray.length; ++i) {
        		Topic t = (Topic) hitsArray[i];
        		System.out.println("Found: "+t.getTopicID()+", "+t.getTopicName());
        		results.add(new SNSTopic(getTypeFromTopic(t), t.getTopicID(), t.getTopicName()));
        	}
        }
        return results;
    }
*/

    public void testIncludeSiblings() {
        // getHierarchy?root=uba_thes_27118&user=ingrid_test&password=ingrid_test&depth=2&direction=up&includeSiblings=true
    	// false liefert drei Knoten
    	// true liefert 10 Knoten
    
        IngridHit[] hitsArray = getHierarchy("uba_thes_27118", 2, "up", true);

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
	            FieldsType.names, 0, THESAURUS_LANGUAGE_FILTER);
	    } catch (Exception e) {
	    	log.error(e);
	    }
	    
	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	        if ((null != topics) && (topics.length == 1)) {
	            return new SNSTopic(getTypeFromTopic(topics[0]), topics[0].getId(), topics[0].getBaseName(0).getBaseNameString().get_value());
	        }
	    }
	    return null;
    }

    public ArrayList<SNSTopic> getSimilarTerms(String[] queryTerms) {
    	return getSimilarTerms(queryTerms, MAX_NUM_RESULTS);
    }

    public ArrayList<SNSTopic> getSimilarTerms(String[] queryTerms, int numResults) {
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
	    		resultList.add(new SNSTopic(getTypeFromTopic(topic), topic.getTopicID(), topic.getTopicName()));
	    	}
	    }
	    return resultList;
    }

    
    public ArrayList<SNSLocationTopic> getLocationTopics(String queryTerm) {
    	ArrayList<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
    	int[] totalSize = new int[] {0};
	    Topic[] snsResults = new Topic[0];
	    try {
		    // Get the locations for the given queryTerm
	    	snsResults = snsController.getTopicsForText(queryTerm, Integer.valueOf(resourceBundle.getString("sns.maxWordAnalyzing")),
	                "/location", "mdek", "de", totalSize, false);
	    }
	    catch (Exception e) {log.error(e);}

	    totalSize[0] = snsResults.length;
	    IngridHits res = new IngridHits("mdek", totalSize[0], snsResults, false);
	    IngridHit[] hits = res.getHits();

	    if (hits != null) {
            for (IngridHit ingridHit : hits) {
            	DetailedTopic dt = (DetailedTopic) ingridHit;
                SNSLocationTopic topic = new SNSLocationTopic();
                String[] type = dt.getSummary().split("#");
                topic.setName(dt.getTitle());
                topic.setTopicId(dt.getTopicID());
                topic.setType(resourceBundle.getString("sns.topic.ref."+type[type.length-1]));
                topic.setNativeKey(dt.getTopicNativeKey());
                resultList.add(topic);
            }
        	Collections.sort(resultList, new SNSLocationTopicComparator());
	    }
	    return resultList;
/*
	    // Iterate over the result array and query for additional data (wgs84 coordinates)
	    // TODO We don't really need to get the detailed information for all the topics here
	    //		Construct incomplete SNSLocationTopics (without wgs84box and qualifier) here?
	    if (hits != null) {
            for (IngridHit ingridHit : hits) {
                DetailedTopic dt = (DetailedTopic) ingridHit;
                SNSLocationTopic topic = getDetailedLocationTopicInfo(dt.getTopicID());
                if (topic != null) {
//					log.debug(topic);
                    resultList.add(topic);
                }
            }
        }
	    return resultList;
*/
    }

    
    public SNSLocationTopic getDetailedLocationTopicInfo(String topicID) {
    	TopicMapFragment mapFragment = null;
    	try {
    		// Query for additional info
    		mapFragment = snsClient.getPSI(topicID, 0, "/location");
	    } catch (Exception e) {
	    	log.error(e);
	    }

	    if (null != mapFragment) {
	    	com.slb.taxi.webservice.xtm.stubs.xtm.Topic[] topics = mapFragment.getTopicMap().getTopic();
	    	for (com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic : topics) {
	            if (topic.getId().equals(topicID)) {
	            	// A match was found. Add the type from the href parameter
	            	//   and check if there are wgs84 coordinates.
//	            	log.debug("Topic Found: "+topicID);
//	            	log.debug("Type: "+topic.getInstanceOf(0).getTopicRef().getHref());
	            	return createLocationTopic(topic);
	            }
	        }
	    }
    	return null;
    }

    private SNSLocationTopic createLocationTopic(com.slb.taxi.webservice.xtm.stubs.xtm.Topic topic) {
    	if (topic.getOccurrence() == null)
    		return null;

    	SNSLocationTopic result = new SNSLocationTopic();
    	result.setTopicId(topic.getId());
    	result.setName(topic.getBaseName(0).getBaseNameString().get_value());
    	String type = topic.getInstanceOf(0).getTopicRef().getHref();
    	type = type.substring(type.lastIndexOf("#")+1);
    	result.setType(resourceBundle.getString("sns.topic.ref."+type));

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
    		}
    	}
    	if (result.getQualifier() == null)
    		result.setQualifier(result.getType());
    	return result;
    }
    
    private static void printTopic(Topic t) {
    	System.out.println("ID: "+t.getTopicID());
    	
		List<Topic> succList = t.getSuccessors();
		if (succList != null && !succList.isEmpty()) {
			for (Topic succ : succList) {
				System.out.print(" ");
				printTopic(succ);
			}
		}
    }

    static public class SNSTopicComparator implements Comparator<Topic> {
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
