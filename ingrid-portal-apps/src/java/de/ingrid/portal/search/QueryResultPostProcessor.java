/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class QueryResultPostProcessor {

    private final static Log log = LogFactory.getLog(QueryResultPostProcessor.class);

    public static IngridHits processRankedHits(IngridHits hits, String ds) {
        try {
            if (hits == null) {
                return null;
            }
            IngridHit[] hitArray = hits.getHits();
            String tmpString = null;
            IngridHit hit = null;
            IngridHitDetail detail = null;

            for (int i = 0; i < hitArray.length; i++) {
                try {
                    hit = hitArray[i];
                    
                    detail = (IngridHitDetail) hit.get("detail");

                    // if no detail, skip processing OF THIS HIT !
                    if (detail == null) {
                        if (log.isErrorEnabled()) {
                            log.error("Hit Detail is NULL !!!!!!!!!!!!!!!!! hit=" + hit + ", detail=" + detail);
                        }
                        continue;
                    }

                    UtilsSearch.transferHitDetails(hit, detail);
                    tmpString = detail.getIplugClassName();
                    if (tmpString == null) {
                        tmpString = "";
                        if (log.isErrorEnabled()) {
                            log.error("Hit Detail has no IplugClassName !!!! hit=" + hit + ", detail=" + detail);
                        }
                    }

                    if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                        if (ds.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
                            hit.put(Settings.RESULT_KEY_TYPE, "www-style");
                        } else {
                            hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
                        }
                        
                        // read for all dsc iplugs

                        // services !
                        Object obj = detail.get(Settings.HIT_KEY_WMS_URL);
                        if (obj != null) {
                            tmpString = "";
                            if (obj instanceof String[]) {
                                // PATCH for wrong data in database -> service string was passed multiple times !
                                String[] servicesArray = (String[]) obj;
                                /*
                                 for (int j = 0; j < servicesArray.length; j++) {
                                 if (j != 0) {
                                 tmpString = tmpString.concat("&");
                                 }
                                 tmpString = tmpString.concat(servicesArray[j]);
                                 }
                                 */
                                tmpString = servicesArray[0];
                            } else {
                                tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_WMS_URL);
                            }
                            if (tmpString.length() > 0) {
                                try {
                                    hit.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(tmpString, "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    log.error("Error url encoding wms URL!", e);
                                }
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
                            String addrClass = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_CLASS);
                            hit.put(Settings.RESULT_KEY_UDK_CLASS, addrClass);
                            if (addrClass.equals("2") || addrClass.equals("3")) {
                                hit.put(Settings.RESULT_KEY_UDK_ADDRESS_FIRSTNAME, UtilsSearch.getDetailValue(detail,
                                        Settings.HIT_KEY_ADDRESS_FIRSTNAME));
                                hit.put(Settings.RESULT_KEY_UDK_ADDRESS_LASTNAME, UtilsSearch.getDetailValue(detail,
                                        Settings.HIT_KEY_ADDRESS_LASTNAME));
                                hit.put(Settings.RESULT_KEY_UDK_ADDRESS_TITLE, UtilsSearch.getDetailValue(detail,
                                        Settings.HIT_KEY_ADDRESS_TITLE));
                                hit.put(Settings.RESULT_KEY_UDK_ADDRESS_SALUTATION, UtilsSearch.getDetailValue(detail,
                                        Settings.HIT_KEY_ADDRESS_ADDRESS));
                            } else if (addrClass.equals("1")) {
                                String currentAddressId = UtilsSearch.getDetailValue(detail,
                                        Settings.HIT_KEY_ADDRESS_ADDRID);
                                String newAddressId = null;
                                ArrayList parentAddr = new ArrayList();
                                boolean skipSearch = false;
                                while (!skipSearch) {
                                    IngridQuery query = QueryStringParser.parse("T022_adr_adr.adr_to_id:".concat(
                                            currentAddressId).concat(" datatype:address ranking:score"));
                                    IngridHits results = IBUSInterfaceImpl.getInstance().search(query, 10, 1, 10,
                                            PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
                                    if (results.getHits().length > 0) {
                                        IngridHitDetail details[] = IBUSInterfaceImpl.getInstance().getDetails(
                                                results.getHits(),
                                                query,
                                                new String[] { Settings.HIT_KEY_ADDRESS_ADDRID,
                                                        Settings.HIT_KEY_ADDRESS_CLASS });
                                        // find first parent of the address in the result set
                                        for (int j = 0; j < details.length; j++) {
                                            IngridHitDetail addrDetail = (IngridHitDetail) details[j];
                                            addrClass = UtilsSearch.getDetailValue(addrDetail,
                                                    Settings.HIT_KEY_ADDRESS_CLASS);
                                            newAddressId = UtilsSearch.getDetailValue(addrDetail,
                                                    Settings.HIT_KEY_ADDRESS_ADDRID);
                                            if ((addrClass.equals("0") || addrClass.equals("1"))
                                                    && !currentAddressId.equals(newAddressId)) {
                                                parentAddr.add(0, addrDetail);
                                                break;
                                            }
                                        }
                                        // check for search skip
                                        if (!addrClass.equals("1") || currentAddressId.equals(newAddressId)) {
                                            skipSearch = true;
                                        } else {
                                            currentAddressId = newAddressId; 
                                        }
                                    } else {
                                        skipSearch = true;
                                    }
                                }
                                hit.put(Settings.RESULT_KEY_UDK_ADDRESS_PARENTS, parentAddr);
                            }

                        }
                    } else if (tmpString.equals("de.ingrid.iplug.se.NutchSearcher")) {
                        hit.put(Settings.RESULT_KEY_TYPE, "www-style");
                    } else if (tmpString.equals("de.ingrid.iplug.tamino.TaminoSearcher")) {
                        hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
                    } else {
                        hit.put(Settings.RESULT_KEY_TYPE, "unknown-style");
                    }
                } catch (Exception ex) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Hit ! hit = " + hit + ", detail=" + detail, ex);
                    }
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

            // check for null hits
            if (hits == null) {
                return null;
            }

            IngridHit[] hitArray = hits.getHits();

            IngridHit hit = null;
            IngridHitDetail detail = null;
            PlugDescription plugDescr = null;
            IngridHit[] subHitArray = null;
            for (int i = 0; i < hitArray.length; i++) {
                try {
                    // top hit of group !
                    hit = hitArray[i];
                    if (hit == null) {
                        continue;
                    }

                    plugDescr = (PlugDescription) hit.get("plugDescr");
                    if (plugDescr != null) {
                        UtilsSearch.transferPlugDescription(hit, plugDescr);
                    }

                    detail = (IngridHitDetail) hit.get("detail");
                    if (detail != null) {
                        UtilsSearch.transferHitDetails(hit, detail);

                        // check for grouping and get details of "sub hits"
                        subHitArray = hit.getGroupHits();
                        if (subHitArray.length > 0) {
                            hit.put("no_of_hits", new Integer(subHitArray.length + 1).toString());
                            // only get Details of the hits we need to render !
                            if (Settings.SEARCH_NUM_HITS_PER_GROUP > 1) {
                                int numNeededSubHits = Settings.SEARCH_NUM_HITS_PER_GROUP - 1;
                                if (numNeededSubHits > subHitArray.length) {
                                    numNeededSubHits = subHitArray.length;
                                }
                                for (int j = 0; j < numNeededSubHits; j++) {
                                    hit = subHitArray[j];
                                    if (hit == null) {
                                        continue;
                                    }

                                    detail = (IngridHitDetail) hit.get("detail");
                                    if (detail != null) {
                                        UtilsSearch.transferHitDetails(hit, detail);
                                    }
                                }
                            }
                        }
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
