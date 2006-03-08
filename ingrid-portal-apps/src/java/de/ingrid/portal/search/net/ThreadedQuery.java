/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
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
            hits = IBUSInterfaceImpl.getInstance().search(qd.getQuery(), qd.getHitsPerPage(), qd.getCurrentPage(),
                    qd.getRequestedHits(), qd.getTimeout());
            if (qd.isGetDetails()) {
                IngridHit[] hitArray = hits.getHits();
                //                IngridHitDetail[] details = IBUSInterfaceImpl.getInstance().getDetails(hits.getHits(), qd.getQuery(),
                //                        qd.getRequestedFields());
                for (int i = 0; i < hitArray.length; i++) {
                    //                    hitArray[i].put("detail", details[i]);
                    hitArray[i].put("detail", IBUSInterfaceImpl.getInstance().getDetail(hitArray[i], qd.getQuery(),
                            qd.getRequestedFields()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("Finished search '" + this.key + "' in " + (System.currentTimeMillis() - startTime) + "ms.");
            this.controller.addResultSet(this.key, hits);
        }
    }

}
