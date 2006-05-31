/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

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

        long startTime = System.currentTimeMillis();

        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();

            hits = ibus.search(qd.getQuery(), qd.getHitsPerPage(), qd.getCurrentPage(), qd.getStartHit(), qd
                    .getTimeout());
            if (qd.isGetDetails() || qd.isGetPlugDescription()) {
                IngridHit[] hitArray = hits.getHits();
                IngridHitDetail[] details = null;
                if (qd.isGetDetails()) {
                    details = ibus.getDetails(hitArray, qd.getQuery(), qd.getRequestedFields());
                }

                IngridHit[] subHitArray = null;
                IngridHitDetail[] subDetailArray = null;
                for (int i = 0; i < hitArray.length; i++) {
                    // get Plug Description only for top Hits (grouped by iPlugs in right column)
                    if (qd.isGetPlugDescription()) {
                        hitArray[i].put("plugDescr", ibus.getIPlug(hitArray[i].getPlugId()));
                    }
                    // get details for all hits to display
                    if (qd.isGetDetails()) {
                        hitArray[i].put("detail", details[i]);
                        //                    hitArray[i].put("detail", ibus.getDetail(hitArray[i], qd.getQuery(),
                        //                            qd.getRequestedFields()));

                        // check for grouping and get details of "sub hits"
                        subHitArray = hitArray[i].getGroupHits();
                        if (subHitArray.length > 0 && Settings.SEARCH_NUM_HITS_PER_GROUP > 1) {
                            // only get Details of the hits we need to render !
                            int numNeededSubHits = Settings.SEARCH_NUM_HITS_PER_GROUP - 1;
                            if (subHitArray.length > numNeededSubHits) {
                                IngridHit[] tmpHitArray = new IngridHit[numNeededSubHits];
                                System.arraycopy(subHitArray, 0, tmpHitArray, 0, numNeededSubHits);
                                subHitArray = tmpHitArray;
                                hitArray[i].putBoolean("moreHits", true);
                            }
                            // separate the subHitArray to render in map !  
                            hitArray[i].put("subHits", subHitArray);
                            subDetailArray = ibus.getDetails(subHitArray, qd.getQuery(), qd.getRequestedFields());
                            for (int j = 0; j < subDetailArray.length; j++) {
                                subHitArray[j].put("detail", subDetailArray[j]);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.warn("Error while querying the ibus.", t);
        } finally {
            log.info("Finished search '" + this.key + "' in " + (System.currentTimeMillis() - startTime) + "ms.");
            this.controller.addResultSet(this.key, hits);
        }
    }
}
