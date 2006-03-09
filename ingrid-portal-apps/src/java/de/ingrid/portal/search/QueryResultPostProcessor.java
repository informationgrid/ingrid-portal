/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.Settings;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class QueryResultPostProcessor {

    private final static Log log = LogFactory.getLog(QueryResultPostProcessor.class);

    public static IngridHits processRankedHits(IngridHits hits, String ds) {
        try {
            IngridHit[] hitArray = hits.getHits();
            String tmpString = null;

            for (int i = 0; i < hitArray.length; i++) {
                IngridHit hit = hitArray[i];
                IngridHitDetail detail = (IngridHitDetail) hit.get("detail");
                UtilsSearch.transferHitDetails(hit, detail);
                tmpString = detail.getIplugClassName();
                if (tmpString == null) {
                    tmpString = "";
                    if (log.isErrorEnabled()) {
                        log.error("Hit Detail has no IplugClassName !!!! hit=" + hit + ", detail=" + detail);
                    }
                }

                if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                    hit.put(Settings.RESULT_KEY_TYPE, "dsc");

                    // read for all dsc iplugs
                    tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_WMS_URL);
                    if (tmpString.length() > 0) {
                        try {
                            hit.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(tmpString, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            log.error("Error url encoding wms URL!", e);
                        }
                    }

                    // check our selected "data source" and read
                    // according data
                    if (ds.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
                        tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_UDK_CLASS);
                        if (tmpString.length() > 0) {
                            hit.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                        }
                    } else if (ds.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
                        tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_CLASS);
                        if (tmpString.length() > 0) {
                            // add as udk class, template renders correctly
                            hit.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                        }
                    }
                } else if (tmpString.equals("de.ingrid.iplug.se.NutchSearcher")) {
                    hit.put(Settings.RESULT_KEY_TYPE, "nutch");
                } else {
                    hit.put(Settings.RESULT_KEY_TYPE, "unknown");
                }
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing Ranked Hits !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hits = " + hits, ex);
            }
        }

        return hits;
    }

    public static IngridHits processUnrankedHits(IngridHits hits, String selectedDS) {

        try {
            IngridHit[] hitArray = hits.getHits();

            IngridHit hit = null;
            IngridHitDetail detail = null;
            for (int i = 0; i < hitArray.length; i++) {
                try {
                    hit = hitArray[i];
                    detail = (IngridHitDetail) hit.get("detail");

                    if (hit == null) {
                        continue;
                    }
                    if (detail != null) {
                        UtilsSearch.transferHitDetails(hit, detail);
                    }
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Hit, hit = " + hit + ", detail = " + detail, t);
                    }
                }
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing Unranked Hits !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hits = " + hits, ex);
            }
        }
        return hits;
    }

}
