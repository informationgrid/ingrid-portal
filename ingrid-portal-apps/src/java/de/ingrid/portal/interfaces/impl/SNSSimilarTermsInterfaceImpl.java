/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.util.ArrayList;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.SimilarTermsInterface;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.tool.SNSUtil;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class SNSSimilarTermsInterfaceImpl implements SimilarTermsInterface {

    private final static Logger log = LoggerFactory.getLogger(SNSSimilarTermsInterfaceImpl.class);

    private static SimilarTermsInterface instance = null;

    public static synchronized SimilarTermsInterface getInstance() {
        if (instance == null) {
            try {
                instance = new SNSSimilarTermsInterfaceImpl();
            } catch (Exception e) {
                log.error("Error initiating the SNSSimilarTerms interface.");
                e.printStackTrace();
            }
        }
        return instance;
    }

    private SNSSimilarTermsInterfaceImpl() throws Exception {
        super();
    }

    public IngridHit[] getTopicsFromText(String term, String filter, Locale language) {
        try {
        	// enclose term in '"' if the term has a space, otherwise no results will be returned from SNS
        	if (term.indexOf(" ") != -1 && !term.startsWith("\"") && !term.endsWith("\"")) {
        		term = "\"".concat(term).concat("\"");
        	}
        	IngridQuery query = QueryStringParser.parse(term);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            if (filter != null) {
                query.put("filter", filter);
            }
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TEXT);

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    public IngridHit[] getSimilarTerms(String term, Locale language) {
        try {
            IngridQuery query = QueryStringParser.parse(term);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.SIMILARTERMS_FROM_TOPIC);

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    public IngridHitDetail[] getSimilarDetailedTerms(String term, IngridHit[] hits) {
        try {
            IngridQuery query = QueryStringParser.parse(term);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.SIMILARTERMS_FROM_TOPIC);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            return iBus.getDetails(hits, query, null);
        } catch (Exception e) {
            log.error("Exception while querying sns for similar detailed terms.", e);
            return null;
        }
    }

    public IngridHitDetail[] getDetailedTopics(String term, IngridHit[] hits, int queryType) {
        try {
            IngridQuery query = QueryStringParser.parse(term);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, queryType);
            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            return iBus.getDetails(hits, query, null);
        } catch (Exception e) {
            log.error("Exception while querying sns for detailed terms.", e);
            return null;
        }
    }

    public IngridHit[] getTopicSimilarLocationsFromTopic(String topicId, Locale language) {
        try {
        	IngridQuery query = QueryStringParser.parse(topicId);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.SIMILARLOCATIONS_FROM_TOPIC);

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    public IngridHit[] getTopicsFromTopic(String topicId, Locale language) {
        try {
    		String marshalledTopicId = SNSUtil.marshallTopicId(topicId);

            IngridQuery query = QueryStringParser.parse(marshalledTopicId);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TOPIC);

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    public IngridHit[] getHierarchy(String topicId, String includeSiblings, String association, String depth,
    		String direction, Locale language) {
    	// We set the number of hits large enough to get all hits in one query.
    	// If not we have to query the SNS multiple times which is VERY costly! 
    	final int CHUNK_SIZE = 1500;
    	
    	ArrayList result = new ArrayList();
    	try {
    		String marshalledTopicId = SNSUtil.marshallTopicId(topicId);

            IngridQuery query = QueryStringParser.parse(marshalledTopicId);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.addField(new FieldQuery(true, false, "lang", "de"));
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_HIERACHY);
            query.put("includeSiblings", includeSiblings);
            query.put("association", association);
            query.put("depth", depth);
            query.put("direction", direction);

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            int page = 0;
            IngridHits queryResult = null;
            IngridHit[] hits = null;
            do
            {
            	page++;
            	queryResult = iBus.search(query, CHUNK_SIZE, page, (page-1) * CHUNK_SIZE,
            			PortalConfig.getInstance().getInt(PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));
            	hits = queryResult.getHits();

            	for (int i = 0; i < hits.length; ++i)
            		result.add(hits[i]);

            } while (hits.length == CHUNK_SIZE);

            hits = new IngridHit[result.size()];
            for (int i = 0; i < result.size(); ++i)
            	hits[i] = (IngridHit) result.get(i);
            return hits;

    	} catch (Exception e) {
            log.error("Exception while querying sns for hierarchy.", e);
            return null;
        }
    }

    
    public IngridHitDetail getDetailsTopic(IngridHit hit, String filter, Locale language) {
        try {
            IngridQuery query = new IngridQuery();
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TOPIC);
            if (filter != null) {
                query.put("filter", filter);
            }

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHitDetail detail = iBus.getDetail(hit, query, null);

            return detail;
        } catch (Exception e) {
            log.error("Exception while querying sns for detailed topic.", e);
            return null;
        }
    }
    
    public IngridHit[] getTopicFromID(String topicId, Locale language) {
    	// We set the number of hits large enough to get all hits in one query.
    	// If not we have to query the SNS multiple times which is VERY costly! 
    	final int CHUNK_SIZE = 1500;
    	
    	ArrayList result = new ArrayList();
    	try {
    		String marshalledTopicId = SNSUtil.marshallTopicId(topicId);
    		IngridQuery query = QueryStringParser.parse(marshalledTopicId);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.addField(new FieldQuery(true, false, "lang", language.getLanguage()));
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_ID);
            query.put("filter", "/thesa");
            
            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            int page = 0;
            IngridHits queryResult = null;
            IngridHit[] hits = null;
            do
            {
            	page++;
            	queryResult = iBus.search(query, CHUNK_SIZE, page, (page-1) * CHUNK_SIZE,
            			PortalConfig.getInstance().getInt(PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));
            	hits = queryResult.getHits();

            	for (int i = 0; i < hits.length; ++i)
            		result.add(hits[i]);

            } while (hits.length == CHUNK_SIZE);

            hits = new IngridHit[result.size()];
            for (int i = 0; i < result.size(); ++i)
            	hits[i] = (IngridHit) result.get(i);
            return hits;

    	} catch (Exception e) {
            log.error("Exception while querying sns for terms by id.", e);
            return null;
        }
    }
}
