/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class SNSSimilarTermsInterfaceImpl implements SimilarTermsInterface {

    private final static Log log = LogFactory.getLog(SNSSimilarTermsInterfaceImpl.class);

    private static SimilarTermsInterface instance = null;

    public static synchronized SimilarTermsInterface getInstance() {
        if (instance == null) {
            try {
                instance = new SNSSimilarTermsInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the SNSSimilarTerms interface.");
                e.printStackTrace();
            }
        }
        return instance;
    }

    private SNSSimilarTermsInterfaceImpl() throws Exception {
        super();
    }

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getTopicsFromText(java.lang.String, java.lang.String)
     */
    public IngridHit[] getTopicsFromText(String term, String filter) {
        try {
            IngridQuery query = QueryStringParser.parse(term);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            if (filter != null) {
                query.put("filter", filter);
            }
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TEXT);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getSimilarTerms(java.lang.String)
     */
    public IngridHit[] getSimilarTerms(String term, Locale language) {
        try {
            IngridQuery query = QueryStringParser.parse(term);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.SIMILARTERMS_FROM_TOPIC);

            // Language
            UtilsSearch.processLanguage(query, language);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 10, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getSimilarDetailedTerms(java.lang.String, de.ingrid.utils.IngridHit[])
     */
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

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getDetailedTopics(java.lang.String, de.ingrid.utils.IngridHit[], int)
     */
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

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getTopicSimilarLocationsFromTopic(java.lang.String)
     */
    public IngridHit[] getTopicSimilarLocationsFromTopic(String topicId) {
        try {
            IngridQuery query = QueryStringParser.parse(topicId);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.SIMILARLOCATIONS_FROM_TOPIC);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    public IngridHit[] getTopicsFromTopic(String topicId) {
        try {
            IngridQuery query = QueryStringParser.parse(topicId);
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TOPIC);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    PortalConfig.SNS_TIMEOUT_DEFAULT, 60000));

            return hits.getHits();
        } catch (Exception e) {
            log.error("Exception while querying sns for similar terms.", e);
            return null;
        }
    }

    public IngridHitDetail getDetailsTopic(IngridHit hit) {
        try {
            IngridQuery query = new IngridQuery();
            query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TOPIC);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHitDetail detail = iBus.getDetail(hit, query, null);

            return detail;
        } catch (Exception e) {
            log.error("Exception while querying sns for detailed topic.", e);
            return null;
        }

    }

}
