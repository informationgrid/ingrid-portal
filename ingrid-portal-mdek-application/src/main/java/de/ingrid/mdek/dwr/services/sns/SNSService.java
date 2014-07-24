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
import org.springframework.beans.factory.annotation.Qualifier;

import de.ingrid.external.FullClassifyService;
import de.ingrid.external.GazetteerService;
import de.ingrid.external.GazetteerService.QueryType;
import de.ingrid.external.ThesaurusService;
import de.ingrid.external.ThesaurusService.MatchingType;
import de.ingrid.external.om.Event;
import de.ingrid.external.om.FullClassifyResult;
import de.ingrid.external.om.Location;
import de.ingrid.external.om.RelatedTerm;
import de.ingrid.external.om.RelatedTerm.RelationType;
import de.ingrid.external.om.Term;
import de.ingrid.external.om.Term.TermType;
import de.ingrid.external.om.TreeTerm;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;

@Qualifier("sns")
public class SNSService {

	private final static Logger log = Logger.getLogger(SNSService.class);	

    private static final int MAX_NUM_RESULTS = 100;
    private static final int MAX_ANALYZED_WORDS = 1000;

    // Error string for the frontend
    private static String ERROR_SNS_INVALID_URL = "SNS_INVALID_URL";
    
    protected ThesaurusService thesaurusService;
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

    public List<SNSTopic> getRootTopics(Locale locale) {
    	return getRootTopics(null, locale);
    }
    
    public List<SNSTopic> getRootTopics(String rootUrl, Locale locale) {
    	//Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from: " + rootUrl + ", " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	String doc = null;
    	if (rootUrl != null) {
    		// if the rootUrl actually is a sub term then split the root url
    		// from the doc id
    		String docId = rootUrl.substring(rootUrl.lastIndexOf("/")+1);
    		if (!docId.isEmpty()) doc = rootUrl;
    		rootUrl = rootUrl.substring(0, rootUrl.lastIndexOf("/")); 
    	}
    	
    	TreeTerm[] treeTerms = thesaurusService.getHierarchyNextLevel(rootUrl, doc, locale);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator( locale ));
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// NO ADDING OF CHILDREN !!!!!!!!! Otherwise wrong behavior in JSP !
    		SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }

    public List<SNSTopic> getSubTopics(String topicID, long depth, String direction, Locale locale) {
    	return getSubTopics(null, topicID, depth, direction, locale);
    }
    
    /** This one is only called with direction "down" in JSPs !!!
     * So we call thesaurusService.getHierarchyNextLevel() 
     * @param locale */
    public List<SNSTopic> getSubTopics(String url, String topicID, long depth, String direction, Locale locale) {
    	//Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from "
    		+topicID+", "+depth+", "+direction+", " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	TreeTerm[] treeTerms = null;
    	
    	if (url == null)
    		treeTerms = thesaurusService.getHierarchyNextLevel(topicID, locale);
    	else
    		treeTerms = thesaurusService.getHierarchyNextLevel(url, topicID, locale);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator( locale ));
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// ADDING OF CHILDREN !!!!!!!!! For right behavior in JSP !
    		SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }

    /** This one is only called with direction "up" in JSPs !!!
     * So we call thesaurusService.getHierarchyPathToTop() 
     * @param locale */
    public List<SNSTopic> getSubTopicsWithRoot(String topicID, long depth, String direction, Locale locale) {
    	return getSubTopicsWithRoot(null, topicID, depth, direction, locale);
    }
    
    public List<SNSTopic> getSubTopicsWithRoot(String url, String topicID, long depth, String direction, Locale locale) {
    	//Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyPathToTop() from "
    		+topicID+", "+depth+", "+direction+", " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm lastTerm = thesaurusService.getHierarchyPathToTop(url, topicID, locale);

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
     * @param locale 
     * @return the SNSTopic if it exists, null otherwise
     */
    public SNSTopic findTopic(String queryTerm, Locale locale) {
    	//Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.findTermsFromQueryTerm() from "
    		+ queryTerm + ", EXACT, true, " + locale.getLanguage());
    	
    	Term[] terms = thesaurusService.findTermsFromQueryTerm(queryTerm,
    			de.ingrid.external.ThesaurusService.MatchingType.EXACT, true, locale);

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
     * Find all topics for a given query string which does an exact search.
     * @param queryTerm topic name to search for
     * @param locale 
     * @return All topics returned by the SNS, converted to SNSTopics 
     */
    public List<SNSTopic> findTopics(String queryTerm, Locale locale) {
    	return findTopics(null, queryTerm, locale, MatchingType.EXACT);
    }
    
    /**
     * Find all topics containing the given query string. 
     * @param url
     * @param queryTerm
     * @param locale
     * @return
     */
    public List<SNSTopic> findTopicsContains(String queryTerm, Locale locale) {
    	return findTopics( null, queryTerm, locale, MatchingType.CONTAINS );
    }
    
    public List<SNSTopic> findTopics(String url, String queryTerm, Locale locale, MatchingType searchType) {
    	// Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.findTermsFromQueryTerm() from "
    		+ queryTerm + ", " + searchType + ", true, " + locale.getLanguage());

    	if (url != null) {
    		// if the rootUrl actually is a sub term then split the root url
    		// from the doc id
    		url = url.substring(0, url.lastIndexOf("/")); 
    	}
    	
    	// TODO: use "contains" here since exact only delivers one term! CHECK!!!
    	Term[] terms = thesaurusService.findTermsFromQueryTerm(url, queryTerm,
    			searchType, true, locale);

    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	for (Term term : terms) {
    		resultList.add(convertTermToSNSTopic(term));
    	}

	    Collections.sort(resultList, new SNSTopicComparator(locale));
	    return resultList;
    }

    /**
     * getPSI for 'topicId'. Returns the SNSTopic of given id !
     * @param topicId topic id to search for
     * @param locale 
     * @return the SNSTopic if it exists, null otherwise
     * @throws Exception if there was a connection/communication error with the SNS
     */
    public SNSTopic getPSI(String topicId, Locale locale) throws Exception {
    	// Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.getTerm() from " + topicId + ", " + locale.getLanguage());
    	
    	Term term = thesaurusService.getTerm(topicId, locale);

    	SNSTopic result = null;
		if (term != null) {
    		result = convertTermToSNSTopic(term);
		}

    	return result;
    }

    /**
     * getPSI for location topics 'topicId'.
     * @param topicId topic id to search for
     * @param addedLocations TODO
     * @return null if location is EXPIRED or not found, otherwise the SNSLocationTopic
     * @throws Exception if there was a connection/communication error with the SNS
     */
    public SNSLocationTopic getLocationPSI(String topicId, Locale locale, List<String> addedLocations) throws Exception {
    	log.debug("     !!!!!!!!!! gazetteerService.getLocation() from " + topicId + ", " + locale.getLanguage());
    	
    	Location location = gazetteerService.getLocation(topicId, locale);

    	SNSLocationTopic result = null;
		if (location != null) {
		    result = new SNSLocationTopic();
		    // empty and marked if expired !
		    if (location.getIsExpired()) {
		        result.setExpiredDate( location.getExpiredDate() );
		        result.setExpired( true );
		    } else {
		        result = convertLocationToSNSLocationTopic(location);
		    }
		        
		    // check for successors
            String[] successorIds = location.getSuccessorIds();
            List<SNSLocationTopic> successorTopics = new ArrayList<SNSLocationTopic>();
            for (String successorId : successorIds) {
                // call me again with the successor
                if (addedLocations == null) {
                    addedLocations = new ArrayList<String>();
                }
                // check if successor was already added since cyclic dependencies can occur
                if (addedLocations.contains( successorId )) {
                    log.debug( "SKIP ... Successor was already added: " + successorId );
                    continue;
                }
                
                // add the topic id of the new topic, which might occur in one of the successors
                addedLocations.add( topicId );
                SNSLocationTopic succLocation = getLocationPSI(successorId, locale, addedLocations);
                successorTopics.add( succLocation );
            }
            
            // add found successors to the location
            if (!successorTopics.isEmpty()) {
                result.setSuccessors( successorTopics );
            }
		}

    	return result;
    }

    public List<SNSTopic> getSimilarTerms(String[] queryTerms, Locale locale) {
    	return getSimilarTerms(queryTerms, MAX_NUM_RESULTS, locale);
    }

    private List<SNSTopic> getSimilarTerms(String[] queryTerms, int numResults, Locale locale) {
    	log.debug("     !!!!!!!!!! thesaurusService.getSimilarTermsFromNames() from " +
    			queryTerms + ", true, " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	
    	Term[] terms = thesaurusService.getSimilarTermsFromNames(queryTerms, true, locale);

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

    public List<SNSTopic> getSimilarDescriptors(String queryTerm, Locale locale) {
    	String[] words = queryTerm.split(" ");
    	List<SNSTopic> result = new ArrayList<SNSTopic>();
    	
    	for (int i = 0; i < words.length; i+=MAX_ANALYZED_WORDS) {
    		String queryStr = "";
    		for (int j = i; j < words.length && j < i+MAX_ANALYZED_WORDS; j++)
    			queryStr += words[j]+" ";

        	log.debug("sns query for words starting at: "+i);
    		log.debug("Query String: "+queryStr);
        	result.addAll(getTopicsForText(queryStr, 100, locale));
    	}

    	return result;
    }

    public List<SNSTopic> getTopicsForText(String queryTerm, int maxNum, Locale locale) {
    	log.debug("     !!!!!!!!!! thesaurusService.getTermsFromText() from " +
    			queryTerm + ", " + MAX_ANALYZED_WORDS + ", false, " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>();
    	
    	Term[] terms = thesaurusService.getTermsFromText(queryTerm, MAX_ANALYZED_WORDS,
    			false, locale);

    	int num = 0;
    	for (Term term : terms) {
    		if (term.getType() == TermType.DESCRIPTOR) {
        		SNSTopic resultTopic = convertTermToSNSTopic(term);
        		resultList.add(resultTopic);
        		num++;
        		if (num == maxNum) {
        			break;
        		}
    		}
    	}

//	    log.debug("Number of descriptors in the result: "+resultList.size());
    	return resultList;
    }

    /** Returns the topicID encapsulated in a SNSTopic with the parents, children and synonyms attached */
    public SNSTopic getTopicsForTopic(String topicId, Locale locale) {
    	// Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.getRelatedTermsFromTerm() from "
    			+ topicId + ", " + locale.getLanguage());
    	
    	RelatedTerm[] relatedTerms = thesaurusService.getRelatedTermsFromTerm(topicId, locale);

    	SNSTopic result = null;
    	if (relatedTerms.length > 0) {
        	List<SNSTopic> synonyms = new ArrayList<SNSTopic>();
        	List<SNSTopic> parents = new ArrayList<SNSTopic>();
        	List<SNSTopic> children = new ArrayList<SNSTopic>();
        	List<SNSTopic> descriptors = new ArrayList<SNSTopic>();

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
            			descriptors.add(t);
            		} else {
    	    			synonyms.add(t);
            		}
            	}
        	}
        	
        	// create topic containing lists ! use default type UMTHES ...
			result = new SNSTopic(Type.DESCRIPTOR, Source.UMTHES, topicId, null, null, null);
		    result.setChildren(children);
		    result.setParents(parents);
		    result.setSynonyms(synonyms);        		
		    result.setDescriptors(descriptors);        		
    	}

    	return result;
    }

    public List<SNSLocationTopic> getLocationTopics(String queryTerm, String searchTypeStr, String pathStr, Locale locale) {
    	de.ingrid.external.GazetteerService.MatchingType matching =
    		getGazetteerMatchingType(searchTypeStr);
    	QueryType queryType = getGazetteerQueryType(pathStr);
    	
    	log.debug("     !!!!!!!!!! gazetteerService.findLocationsFromQueryTerm() " + queryTerm +
    			" " + queryType + " " + matching+ ", " + locale.getLanguage());

    	Location[] locations = gazetteerService.findLocationsFromQueryTerm(queryTerm, queryType,
    			matching, locale);

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
    public List<SNSLocationTopic> getLocationTopicsById(String topicID, Locale locale) {
    	log.debug("     !!!!!!!!!! gazetteerService.getRelatedLocationsFromLocation() from "
    			+ topicID + ", true, " + locale.getLanguage());
    	
    	Location[] locations = gazetteerService.getRelatedLocationsFromLocation(topicID, true, locale);

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
    public SNSTopicMap autoClassifyURL(String urlStr, int analyzeMaxWords, String filter, boolean ignoreCase, String lang,
    		int maxNum, Locale locale) {   	
    	URL url = createURL(urlStr);
    	if (url == null) {
   			throw new RuntimeException(ERROR_SNS_INVALID_URL);
    	}
		de.ingrid.external.FullClassifyService.FilterType filterType = getFullClassifyFilterType(filter);

    	log.debug("     !!!!!!!!!! fullClassifyService.autoClassifyURL() " + url + ", " + filter + ", " + locale.getLanguage());
    	FullClassifyResult classifyResult =
    		fullClassifyService.autoClassifyURL(url, analyzeMaxWords, ignoreCase, filterType, locale);

    	SNSTopicMap result = new SNSTopicMap();
		result.setIndexedDocument(new IndexedDocument(classifyResult.getIndexedDocument()));

		// terms
    	List<SNSTopic> thesaTopics = new ArrayList<SNSTopic>();
    	if (classifyResult.getTerms() != null) {
        	int num = 0;
        	for (Term term : classifyResult.getTerms()) {
                if (term == null || term.getName() == null || term.getName().trim().isEmpty()) {
                	continue;
                }
        		thesaTopics.add(convertTermToSNSTopic(term));
        		num++;
        		if (num == maxNum) {
        			break;
        		}
        	}    		
    	}
    	result.setThesaTopics(thesaTopics);

    	// locations
    	List<SNSLocationTopic> locationTopics = new ArrayList<SNSLocationTopic>();
    	if (classifyResult.getLocations() != null) {
        	int num = 0;
        	for (Location location : classifyResult.getLocations()) {
                if (location == null || location.getName() == null || location.getName().trim().isEmpty()) {
                	continue;
                }
            	SNSLocationTopic t = convertLocationToSNSLocationTopic(location);
        		// returns null for expired locations
            	if (t != null) {
            		locationTopics.add(t);
            		num++;
            		if (num == maxNum) {
            			break;
            		}
            	}
        	}    		
    	}
    	result.setLocationTopics(locationTopics);

    	// events
    	List<SNSEventTopic> eventTopics = new ArrayList<SNSEventTopic>();
    	if (classifyResult.getEvents() != null) {
        	for (Event event : classifyResult.getEvents()) {
        		eventTopics.add(convertEventToSNSEventTopic(event));
        	}    		
    	}
    	result.setEventTopics(eventTopics);

	    return result;
    }

    /** NO adding of children !
    /* NOTICE: Type.TOP_TERM can only be determined if term is TreeTerm !!!!!! */
    protected SNSTopic convertTermToSNSTopic(Term term) {
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

    	// always set Inspire themes list in result, never null !
    	if (term.getInspireThemes() != null) {
    		resultTopic.setInspireList(term.getInspireThemes());
    	} else {
    		resultTopic.setInspireList(new ArrayList<String>());
    	}
		
    	return resultTopic;
    }

    /** Also adds children ! */
    protected SNSTopic convertTreeTermToSNSTopic(TreeTerm treeTerm) {
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
     * @return NULL if location is Expired !!! OR IF ID OR NAME IS NULL (happens with GSSoil gazetteer ?)
     */
    private SNSLocationTopic convertLocationToSNSLocationTopic(Location location) {
    	if (location.getIsExpired()) {
    		return null;
    	}
    	
    	// also check id and name ! GS Soil gazetteer returns null results ?
    	String locId = location.getId();
    	String locName = location.getName();
    	if (locId == null || locName == null) {
    		if (log.isDebugEnabled()) {
    			log.debug("Location with NULL value ! we skip this one: id=" + locId + ", name=" + locName);
    		}
    		return null;
    	}

    	SNSLocationTopic result = new SNSLocationTopic();

    	result.setTopicId(locId);
    	result.setName(locName);
    	
    	// null if not set !
    	result.setTypeId(location.getTypeId());
    	result.setType(location.getTypeName());
    	result.setBoundingBox(location.getBoundingBox());
   		result.setQualifier(location.getQualifier());
   		result.setNativeKey(location.getNativeKey());
   		result.setExpired( location.getIsExpired() );
	
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
        private Locale locale;
        public TermComparator( Locale locale ) {
            this.locale = locale;
        }
        
    	public final int compare(Term termA, Term termB) {
            try {
            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(locale);
            	return gerCollator.compare(termA.getName(), termB.getName());
            } catch (Exception e) {
                return 0;
            }
        }
    }
    static public class SNSTopicComparator implements Comparator<SNSTopic> {
        private Locale locale;
    	public SNSTopicComparator( Locale locale ) {
            this.locale = locale;
        }

        public final int compare(SNSTopic topicA, SNSTopic topicB) {
            try {
            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(locale);
            	return gerCollator.compare(topicA.getTitle(), topicB.getTitle());
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
