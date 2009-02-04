/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.IngridHitWrapper;
import de.ingrid.portal.global.IngridHitsWrapper;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * Class for threaded ibus search. Queries the ibus in the run method and optionally
 * get the details for the found hits.
 *
 * @author joachim@wemove.com
 */
public class ThreadedQuery extends Thread {

    private final static Log log = LogFactory.getLog(ThreadedQuery.class);

    private String key;

    private QueryDescriptor qd;

    private ThreadedQueryController controller;

    /**
     * Constructor.
     * 
     * @param myKey The key of the issued query.
     * @param myQueryDescriptor The Descriptor of the query.
     * @param myController The controller to report the results back.
     */
    public ThreadedQuery(String myKey, QueryDescriptor myQueryDescriptor, ThreadedQueryController myController) {
        this.key = myKey;
        this.qd = myQueryDescriptor;
        this.controller = myController;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

    	IngridHits hits = null;
        IngridHitsWrapper hitsWrapper = null;

        long startTime = System.currentTimeMillis();

        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();

            hits = ibus.search(qd.getQuery(), qd.getHitsPerPage(), qd.getCurrentPage(), qd.getStartHit(), qd
                    .getTimeout());
            if (qd.isGetDetails() || qd.isGetPlugDescription()) {
                //IngridHitWrapper[] hitArray = hits.getHits();
            	IngridHit[] hitArray = hits.getHits();
            	IngridHitDetail[] details = null;
                if (qd.isGetDetails()) {
                    details = ibus.getDetails(hitArray, qd.getQuery(), qd.getRequestedFields());
                }

                hitsWrapper = new IngridHitsWrapper(hits);
                IngridHitWrapper[] hitArrayWrapper = hitsWrapper.getWrapperHits();
                IngridHit[] subHitArray = null;
                for (int i = 0; i < hitArrayWrapper.length; i++) {
                	//IngridHit topHit = hitArray[i];
                	//IngridHitWrapper topHitWrapper = new IngridHitWrapper(hitArray[i]);
                	IngridHitWrapper topHitWrapper = hitArrayWrapper[i];
                	
                    // get Plug Description only for top Hits (grouped by iPlugs in right column)
                    if (qd.isGetPlugDescription()) {
                    	topHitWrapper.put("plugDescr", ibus.getIPlug(hitArrayWrapper[i].getHit().getPlugId()));
                    }
                    // get details for all hits to display
                    if (qd.isGetDetails()) {
                    	topHitWrapper.put(Settings.RESULT_KEY_DETAIL, details[i]);

                        // check for further hits (grouping)
                        subHitArray = topHitWrapper.getHit().getGroupHits();
                        if (subHitArray != null && subHitArray.length > 0) {
                        	
                        	// determine number of hits in group
                        	String groupLength = null;
                        	boolean groupLengthException = false;
                        	try {
                        		// set in backend when grouping by domain (datasource)
                            	groupLength = new Integer(topHitWrapper.getHit().getGroupTotalHitLength()).toString();
                        	} catch (Exception ex) {
                        		// EXCEPTION MIGHT OCCUR DEPENDENT HOW HIT WAS SET UP IN BACKEND !
                        		groupLengthException = true;
                        	}
                        	// set in "normal" grouping (partner, provider ...)
                            if (groupLength == null) {
                                groupLength = (String)topHitWrapper.get("no_of_hits");                            	

                                if (groupLength == null) {
                                	groupLength = (String)details[i].get("no_of_hits");

                                	if (groupLength == null) {
                                    	groupLength = String.valueOf(subHitArray.length + 1);
                                    }
                                }
                            }

                            topHitWrapper.put("no_of_hits", groupLength);
                            // default grouping: only top hit is shown, so we always have more hits
                            topHitWrapper.putBoolean("moreHits", true);
                            
                            // grouping by domain ? -> one sub hit is shown, we need details of first sub hit ...
                            String grouping = (String) qd.getQuery().get(Settings.QFIELD_GROUPED);
                            if (IngridQuery.GROUPED_BY_DATASOURCE.equals(grouping)) {
                            	//IngridHit subHit = subHitArray[0];
                            	IngridHitWrapper subHitWrapper = new IngridHitWrapper(subHitArray[0]);
                            	subHitWrapper.put("plugDescr", topHitWrapper.get("plugDescr"));
                                IngridHitDetail subDetail = ibus.getDetail(subHitWrapper.getHit(), qd.getQuery(), qd.getRequestedFields());
                                subHitWrapper.put(Settings.RESULT_KEY_DETAIL, subDetail);
                                topHitWrapper.put(Settings.RESULT_KEY_SUB_HIT, subHitWrapper);
                                
                                // 2 hits already shown, do we have more ?
                                if (groupLengthException) {
                                	// problems extracting group length -> assume we have more hits !
                                	subHitWrapper.putBoolean("moreHits", true);
                                } else if (new Integer(groupLength).intValue() > 2) {
                                	subHitWrapper.putBoolean("moreHits", true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Error while querying the ibus.", t);
            } else {
                log.info("Error while querying the ibus. Switch to log level debug for exception details.");
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Finished search '" + this.key + "' in " + (System.currentTimeMillis() - startTime) + "ms.");
            }
            this.controller.addResultSet(this.key, hitsWrapper);
        }
    }

    /**
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }
}
