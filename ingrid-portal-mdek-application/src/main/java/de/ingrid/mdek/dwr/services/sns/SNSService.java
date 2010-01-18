package de.ingrid.mdek.dwr.services.sns;

import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.ingrid.external.FullClassifyService;
import de.ingrid.external.GazetteerService;
import de.ingrid.external.ThesaurusService;
import de.ingrid.external.GazetteerService.QueryType;
import de.ingrid.external.om.Event;
import de.ingrid.external.om.FullClassifyResult;
import de.ingrid.external.om.Location;
import de.ingrid.external.om.RelatedTerm;
import de.ingrid.external.om.Term;
import de.ingrid.external.om.TreeTerm;
import de.ingrid.external.om.RelatedTerm.RelationType;
import de.ingrid.external.om.Term.TermType;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;

public class SNSService {

	private final static Logger log = Logger.getLogger(SNSService.class);	

    private static final int MAX_NUM_RESULTS = 100;
    private static final int MAX_ANALYZED_WORDS = 1000;

    // Error string for the frontend
    private static String ERROR_SNS_INVALID_URL = "SNS_INVALID_URL";
    
    private ThesaurusService thesaurusService;
    private GazetteerService gazetteerService;
    private FullClassifyService fullClassifyService;

    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {}

	public void setThesaurusService(ThesaurusService thesaurusService) {
		this.thesaurusService = thesaurusService;
	}
	public void setGazetteerService(GazetteerService gazetteerService) {
		this.gazetteerService = gazetteerService;
	}
	public void setFullClassifyService(FullClassifyService fullClassifyService) {
		this.fullClassifyService = fullClassifyService;
	}

    public List<SNSTopic> getRootTopics() {
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from null (toplevel)");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm[] treeTerms = thesaurusService.getHierarchyNextLevel(null, Locale.GERMAN);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator());
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// NO ADDING OF CHILDREN !!!!!!!!! Otherwise wrong behavior in JSP !
    		SNSTopic resultTopic = convertTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }

    /** This one is only called with direction "down" in JSPs !!!
     * So we call thesaurusService.getHierarchyNextLevel() */
    public List<SNSTopic> getSubTopics(String topicID, long depth, String direction) {
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from "+topicID+", "+depth+", "+direction+")");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm[] treeTerms = thesaurusService.getHierarchyNextLevel(topicID, Locale.GERMAN);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator());
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// ADDING OF CHILDREN !!!!!!!!! For right behavior in JSP !
    		SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }

    /** This one is only called with direction "up" in JSPs !!!
     * So we call thesaurusService.getHierarchyPathToTop() */
    public List<SNSTopic> getSubTopicsWithRoot(String topicID, long depth, String direction) {
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyPathToTop() from "+topicID+", "+depth+", "+direction+")");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm lastTerm = thesaurusService.getHierarchyPathToTop(topicID, Locale.GERMAN);

    	// Notice we have to build different structure for return list !
    	// parent is encapsulated in CHILD list on every level
    	if (lastTerm != null) {
    		SNSTopic lastTopic = convertTermToSNSTopic(lastTerm);
    		resultList.add(lastTopic);

    		// we use first parent on every level for hierarchy path ! 
    		while (lastTerm.getParents() != null) {
    			lastTerm = lastTerm.getParents().get(0);
    			SNSTopic currentTopic = convertTermToSNSTopic(lastTerm);

    			// set last topic as parent in current topic (but is child in hierarchy !)
    			List<SNSTopic> parents = new ArrayList<SNSTopic>();
    			parents.add(lastTopic);
    			currentTopic.setParents(parents);

    			// set current topic as child in last topic (but is parent in hierarchy !)
    			List<SNSTopic> children = new ArrayList<SNSTopic>();
    			children.add(currentTopic);
    			lastTopic.setChildren(children);
    			
    			lastTopic = currentTopic;
    		}
    	}

    	return resultList;
    }

    /**
     * find the topic with name 'queryTerm'. If no topic is found with the given name, null is returned
     * @param queryTerm topic name to search for
     * @return the SNSTopic if it exists, null otherwise
     */
    public SNSTopic findTopic(String queryTerm) {
    	log.debug("     !!!!!!!!!! thesaurusService.findTermsFromQueryTerm() from " + queryTerm);
    	
    	Term[] terms = thesaurusService.findTermsFromQueryTerm(queryTerm,
    			de.ingrid.external.ThesaurusService.MatchingType.EXACT, true, Locale.GERMAN);

    	SNSTopic result = null;
    	for (Term term : terms) {
    		if (queryTerm.equalsIgnoreCase(term.getName())) {
        		result = convertTermToSNSTopic(term);
        		break;
    		}
    	}

    	return result;
    }

    /**
     * Find all topics for a given query string
     * @param queryTerm topic name to search for
     * @return All topics returned by the SNS, converted to SNSTopics 
     */
    public List<SNSTopic> findTopics(String queryTerm) {
    	log.debug("     !!!!!!!!!! thesaurusService.findTermsFromQueryTerm() from " + queryTerm);
    	
    	Term[] terms = thesaurusService.findTermsFromQueryTerm(queryTerm,
    			de.ingrid.external.ThesaurusService.MatchingType.EXACT, true, Locale.GERMAN);

    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	for (Term term : terms) {
    		resultList.add(convertTermToSNSTopic(term));
    	}

	    Collections.sort(resultList, new SNSTopicComparator());
	    return resultList;
    }

    /**
     * getPSI for 'topicId'. Returns the SNSTopic of given id !
     * @param topicId topic id to search for
     * @return the SNSTopic if it exists, null otherwise
     * @throws Exception if there was a connection/communication error with the SNS
     */
    public SNSTopic getPSI(String topicId) throws Exception {
    	log.debug("     !!!!!!!!!! thesaurusService.getTerm() from " + topicId);
    	
    	Term term = thesaurusService.getTerm(topicId, Locale.GERMAN);

    	SNSTopic result = null;
		if (term != null) {
    		result = convertTermToSNSTopic(term);
		}

    	return result;
    }

    /**
     * getPSI for location topics 'topicId'.
     * @param topicId topic id to search for
     * @return null if location is EXPIRED or not found, otherwise the SNSLocationTopic
     * @throws Exception if there was a connection/communication error with the SNS
     */
    public SNSLocationTopic getLocationPSI(String topicId) throws Exception {
    	log.debug("     !!!!!!!!!! gazetteerService.getLocation() from " + topicId);
    	
    	Location location = gazetteerService.getLocation(topicId, Locale.GERMAN);

    	SNSLocationTopic result = null;
		if (location != null) {
			// NULL if expired !
    		result = convertLocationToSNSLocationTopic(location);
		}

    	return result;
    }

    public List<SNSTopic> getSimilarTerms(String[] queryTerms) {
    	return getSimilarTerms(queryTerms, MAX_NUM_RESULTS);
    }

    private List<SNSTopic> getSimilarTerms(String[] queryTerms, int numResults) {
    	log.debug("     !!!!!!!!!! thesaurusService.getSimilarTermsFromNames()");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	
    	Term[] terms = thesaurusService.getSimilarTermsFromNames(queryTerms, true, Locale.GERMAN);

    	int count = 0;
    	for (Term term : terms) {
    		if (term.getType() == TermType.DESCRIPTOR) {
        		SNSTopic resultTopic = convertTermToSNSTopic(term);
        		resultList.add(resultTopic);
        		count++;
        		if (count == numResults) {
        			break;
        		}
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
    	log.debug("     !!!!!!!!!! thesaurusService.getTermsFromText()");
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	
    	Term[] terms = thesaurusService.getTermsFromText(queryTerm, MAX_ANALYZED_WORDS,
    			false, Locale.GERMAN);

    	for (Term term : terms) {
    		if (term.getType() == TermType.DESCRIPTOR) {
        		SNSTopic resultTopic = convertTermToSNSTopic(term);
        		resultList.add(resultTopic);
    		}
    	}

//	    log.debug("Number of descriptors in the result: "+resultList.size());
    	return resultList;
    }

    /** Returns the topicID encapsulated in a SNSTopic with the parents, children and synonyms attached */
    public SNSTopic getTopicsForTopic(String topicId) {
    	log.debug("     !!!!!!!!!! thesaurusService.getRelatedTermsFromTerm() from " + topicId);
    	
    	RelatedTerm[] relatedTerms = thesaurusService.getRelatedTermsFromTerm(topicId, Locale.GERMAN);

    	SNSTopic result = null;
    	if (relatedTerms.length > 0) {
        	List<SNSTopic> synonyms = new ArrayList<SNSTopic>();
        	List<SNSTopic> parents = new ArrayList<SNSTopic>();
        	List<SNSTopic> children = new ArrayList<SNSTopic>();

        	for (RelatedTerm relatedTerm : relatedTerms) {
        		SNSTopic t = convertTermToSNSTopic(relatedTerm);
            	RelationType relationType = relatedTerm.getRelationType();
            	if (relationType == RelationType.CHILD) {
            		children.add(t);
            	} else if (relationType == RelationType.PARENT) {
	    			parents.add(t);
            	} else if (relationType == RelationType.RELATIVE) {
            		// check type of term !
            		if (relatedTerm.getType() == TermType.DESCRIPTOR) {
            			// !!!!!!!
    	    			result = t;
    	    			break;
            		} else {
    	    			synonyms.add(t);
            		}
            	}
        	}
        	
        	if (result == null) {
    			result = new SNSTopic(Type.DESCRIPTOR, Source.UMTHES, topicId, null, null, null);
    		    result.setChildren(children);
    		    result.setParents(parents);
    		    result.setSynonyms(synonyms);        		
        	}
    	}

    	return result;
    }

    public List<SNSLocationTopic> getLocationTopics(String queryTerm, String searchTypeStr, String pathStr) {
    	de.ingrid.external.GazetteerService.MatchingType matching =
    		getGazetteerMatchingType(searchTypeStr);
    	QueryType queryType = getGazetteerQueryType(pathStr);

    	log.debug("     !!!!!!!!!! gazetteerService.findLocationsFromQueryTerm() " + queryTerm +
    			" " + queryType + " " + matching);

    	Location[] locations = gazetteerService.findLocationsFromQueryTerm(queryTerm, queryType,
    			matching, Locale.GERMAN);

    	List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
    	for (Location location : locations) {
        	SNSLocationTopic t = convertLocationToSNSLocationTopic(location);
        	if (t != null) {
        		resultList.add(t);
        	}
    	}

	    return resultList;
    }

    /** Returns all related locations including the one with passed id ! */
    public List<SNSLocationTopic> getLocationTopicsById(String topicID) {
    	log.debug("     !!!!!!!!!! gazetteerService.getRelatedLocationsFromLocation() from " + topicID);
    	
    	Location[] locations = gazetteerService.getRelatedLocationsFromLocation(topicID, true, Locale.GERMAN);

    	List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
    	for (Location location : locations) {
        	SNSLocationTopic t = convertLocationToSNSLocationTopic(location);
        	if (t != null) {
        		resultList.add(t);
        	}
    	}

	    return resultList;
    }

    /** SNS autoClassify operation for URLs */
    public SNSTopicMap autoClassifyURL(String urlStr, int analyzeMaxWords, String filter, boolean ignoreCase, String lang) {   	
    	URL url = createURL(urlStr);
    	if (url == null) {
   			throw new RuntimeException(ERROR_SNS_INVALID_URL);
    	}
		de.ingrid.external.FullClassifyService.FilterType filterType = getFullClassifyFilterType(filter);

    	log.debug("     !!!!!!!!!! fullClassifyService.autoClassifyURL() " + url + " " + filter + " " + lang);
    	FullClassifyResult classifyResult =
    		fullClassifyService.autoClassifyURL(url, analyzeMaxWords, ignoreCase, filterType, Locale.GERMAN);

    	SNSTopicMap result = new SNSTopicMap();
		result.setIndexedDocument(new IndexedDocument(classifyResult.getIndexedDocument()));

		// terms
    	List<SNSTopic> thesaTopics = new ArrayList<SNSTopic>();
    	for(Term term : classifyResult.getTerms()) {
    		thesaTopics.add(convertTermToSNSTopic(term));
    	}
    	result.setThesaTopics(thesaTopics);

    	// locations
    	List<SNSLocationTopic> locationTopics = new ArrayList<SNSLocationTopic>();
    	for(Location location : classifyResult.getLocations()) {
        	SNSLocationTopic t = convertLocationToSNSLocationTopic(location);
    		// returns null for expired locations
        	if (t != null) {
        		locationTopics.add(t);
        	}
    	}
    	result.setLocationTopics(locationTopics);

    	// events
    	List<SNSEventTopic> eventTopics = new ArrayList<SNSEventTopic>();
    	for(Event event : classifyResult.getEvents()) {
    		eventTopics.add(convertEventToSNSEventTopic(event));
    	}
    	result.setEventTopics(eventTopics);

	    return result;
    }

    /** NO adding of children !
    /* NOTICE: Type.TOP_TERM can only be determined if term is TreeTerm !!!!!! */
    private SNSTopic convertTermToSNSTopic(Term term) {
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
    private SNSTopic convertTreeTermToSNSTopic(TreeTerm treeTerm) {
    	SNSTopic resultTopic = convertTermToSNSTopic(treeTerm);

    	List<TreeTerm> childTerms = treeTerm.getChildren();
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

    /**
     * @return NULL if location is Expired !!!
     */
    private SNSLocationTopic convertLocationToSNSLocationTopic(Location location) {
    	if (location.getIsExpired()) {
    		return null;
    	}

    	SNSLocationTopic result = new SNSLocationTopic();

    	result.setTopicId(location.getId());
    	result.setName(location.getName());
    	
    	// null if not set !
    	result.setTypeId(location.getTypeId());
    	result.setType(location.getTypeName());
    	result.setBoundingBox(location.getBoundingBox());
   		result.setQualifier(location.getQualifier());
   		result.setNativeKey(location.getNativeKey());
		
    	return result;
    }

    private SNSEventTopic convertEventToSNSEventTopic(Event event) {
    	SNSEventTopic t = new SNSEventTopic();
    	t.setTopicId(event.getId());
    	t.setName(event.getTitle());
    	t.setDescription(event.getDescription());
    	t.setAt(event.getTimeAt());
    	t.setFrom(event.getTimeRangeFrom());
    	t.setTo(event.getTimeRangeTo());
		
    	return t;
    }

    private static Source getSourceFromTerm(Term term) {
    	if (term.getAlternateId() != null) {
    		return Source.GEMET;
    	} else {
    		return Source.UMTHES;
    	}
    }

    /** NOTICE: Type.TOP_TERM can only be determined if term is TreeTerm !!!!!! */
    private static Type getTypeFromTerm(Term term) {
    	// first check whether we have a tree term ! Only then we can determine whether top node !
		if (TreeTerm.class.isAssignableFrom(term.getClass())) {
	    	if (((TreeTerm)term).getParents() == null) {
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

	/** Determine Gazetteer MatchingType from passed SNS search type string. */
	private de.ingrid.external.GazetteerService.MatchingType getGazetteerMatchingType(String searchTypeStr) {
		de.ingrid.external.GazetteerService.MatchingType matching;
		if (searchTypeStr == null) {
			matching = de.ingrid.external.GazetteerService.MatchingType.EXACT;
    	} else if ("exact".equalsIgnoreCase(searchTypeStr)) {
    		matching = de.ingrid.external.GazetteerService.MatchingType.EXACT;
    	} else if ("contains".equalsIgnoreCase(searchTypeStr)) {
    		matching = de.ingrid.external.GazetteerService.MatchingType.CONTAINS;
    	} else {
    		matching = de.ingrid.external.GazetteerService.MatchingType.BEGINS_WITH;    		
    	}
		
    	return matching;
	}

	/** Determine Gazetteer QueryType from passed SNS search path. */
	private QueryType getGazetteerQueryType(String pathStr) {

		QueryType queryType = QueryType.ALL_LOCATIONS;
		if ("/location/admin".equals(pathStr)) {
			queryType = QueryType.ONLY_ADMINISTRATIVE_LOCATIONS;
    	}
		
    	return queryType;
	}

	/** Determine FullClassifyService.FilterType from passed SNS filter. */
	private de.ingrid.external.FullClassifyService.FilterType getFullClassifyFilterType(String filterStr) {
		de.ingrid.external.FullClassifyService.FilterType filterType = null;
		if ("/thesa".equals(filterStr)) {
			filterType = de.ingrid.external.FullClassifyService.FilterType.ONLY_TERMS;
    	} else if ("/location".equals(filterStr)) {
			filterType = de.ingrid.external.FullClassifyService.FilterType.ONLY_LOCATIONS;
    	} else  if ("/event".equals(filterStr)) {
			filterType = de.ingrid.external.FullClassifyService.FilterType.ONLY_EVENTS;
    	} 

    	return filterType;
	}

	/** Create URL from url String. Returns null if problems !!! */
	private URL createURL(String urlStr) {
    	URL url = null;
    	try {
    		url = new URL(urlStr);
    	} catch (Exception ex) {
    		log.warn("Error building URL " + urlStr, ex);
    	}
    	
    	return url;
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
}
