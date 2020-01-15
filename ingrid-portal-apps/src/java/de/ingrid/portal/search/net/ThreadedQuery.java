/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.search.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(ThreadedQuery.class);

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
    @Override
    public void run() {

    	IngridHits hits = null;
        IngridHitsWrapper hitsWrapper = null;

        long startTime = System.currentTimeMillis();

        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();

            if (qd.isGetDetails()) {
            	hits = ibus.searchAndDetail(qd.getQuery(), qd.getHitsPerPage(), qd.getCurrentPage(), qd.getStartHit(), qd
                        .getTimeout(), qd.getRequestedFields());
            } else {
            	hits = ibus.search(qd.getQuery(), qd.getHitsPerPage(), qd.getCurrentPage(), qd.getStartHit(), qd
                        .getTimeout());
            }
            if (qd.isGetDetails() || qd.isGetPlugDescription()) {
            	IngridHit[] hitArray = hits.getHits();

                hitsWrapper = new IngridHitsWrapper(hits);
                IngridHitWrapper[] hitArrayWrapper = hitsWrapper.getWrapperHits();
                IngridHit[] subHitArray = null;
                for (int i = 0; i < hitArrayWrapper.length; i++) {
                	IngridHitWrapper topHitWrapper = hitArrayWrapper[i];
                	
                    // get Plug Description only for top Hits (grouped by iPlugs in right column)
                    if (qd.isGetPlugDescription()) {
                    	topHitWrapper.put(Settings.RESULT_KEY_PLUG_DESCRIPTION, ibus.getIPlug(hitArrayWrapper[i].getHit().getPlugId()));
                    }
                    // get details for all hits to display
                    if (qd.isGetDetails()) {
                    	topHitWrapper.put(Settings.RESULT_KEY_DETAIL, hitArray[i].getHitDetail());
                    	// dummy hit if no results, but unranked iPlug should be displayed 
                    	if (hitArray[i].isDummyHit()) {
                            topHitWrapper.put(Settings.RESULT_KEY_NO_OF_HITS, 0);
                    	}

                        // check for further hits (grouping)
                        subHitArray = topHitWrapper.getHit().getGroupHits();
                        if (subHitArray != null && subHitArray.length > 0) {
                        	
                        	// determine number of hits in group
                        	String groupLength = null;
                        	boolean groupLengthException = false;
                        	try {
                        		// set in backend when grouping by domain (datasource)
                            	groupLength = Integer.toString(topHitWrapper.getHit().getGroupTotalHitLength());
                        	} catch (Exception ex) {
                        		// EXCEPTION MIGHT OCCUR DEPENDENT HOW HIT WAS SET UP IN BACKEND !
                        		groupLengthException = true;
                        	}
                        	// set in "normal" grouping (partner, provider ...)
                            if (groupLength == null) {
                                groupLength = (String)topHitWrapper.get(Settings.RESULT_KEY_NO_OF_HITS);                            	

                                if (groupLength == null) {
                                	groupLength = (String)hitArray[i].getHitDetail().get(Settings.RESULT_KEY_NO_OF_HITS);

                                	if (groupLength == null) {
                                    	groupLength = String.valueOf(subHitArray.length + 1);
                                    }
                                }
                            }

                            topHitWrapper.put(Settings.RESULT_KEY_NO_OF_HITS, groupLength);
                            // default grouping: only top hit is shown, so we always have more hits
                            topHitWrapper.putBoolean("moreHits", true);
                            
                            // grouping by domain ? -> one sub hit is shown, we need details of first sub hit ...
                            String grouping = (String) qd.getQuery().get(Settings.QFIELD_GROUPED);
                            if (IngridQuery.GROUPED_BY_DATASOURCE.equals(grouping)) {
                            	IngridHitWrapper subHitWrapper = new IngridHitWrapper(subHitArray[0]);
                            	subHitWrapper.put(Settings.RESULT_KEY_PLUG_DESCRIPTION, topHitWrapper.get(Settings.RESULT_KEY_PLUG_DESCRIPTION));
                                IngridHitDetail subDetail = ibus.getDetail(subHitWrapper.getHit(), qd.getQuery(), qd.getRequestedFields());
                                subHitWrapper.put(Settings.RESULT_KEY_DETAIL, subDetail);
                                topHitWrapper.put(Settings.RESULT_KEY_SUB_HIT, subHitWrapper);
                                
                                // 2 hits already shown, do we have more ?
                                if (groupLengthException || Integer.parseInt(groupLength) > 2) {
                                	// problems extracting group length -> assume we have more hits !
                                	subHitWrapper.putBoolean("moreHits", true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception t) {
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
