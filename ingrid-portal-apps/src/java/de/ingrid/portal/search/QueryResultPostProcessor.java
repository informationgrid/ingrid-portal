/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
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

            IngridHit hit = null;
            IngridHitDetail detail = null;

            for (int i = 0; i < hitArray.length; i++) {
                try {
                    hit = hitArray[i];
                    detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);

                    processRankedHit(hit, detail, ds);
                    
                    // also process group sub hits !
                    hit = (IngridHit) hit.get(Settings.RESULT_KEY_SUB_HIT);
                    if (hit != null) {
                        detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);

                        processRankedHit(hit, detail, ds);
                    }

                } catch (Exception ex) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Ranked Hit ! hit = " + hit + ", detail=" + detail, ex);
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

    private static void processRankedHit(IngridHit hit, IngridHitDetail detail, String ds) {
        // if no detail, skip processing OF THIS HIT !
        if (detail == null) {
            if (log.isErrorEnabled()) {
                log.error("Ranked Hit Detail is NULL !!!!!!!!!!!!!!!!! hit=" + hit);
            }
            return;
        }

        UtilsSearch.transferHitDetails(hit, detail);

        PlugDescription plugDescr = (PlugDescription) hit.get("plugDescr");
        if (plugDescr != null) {
            UtilsSearch.transferPlugDescription(hit, plugDescr);
        }
        String tmpString = detail.getIplugClassName();
        if (tmpString == null) {
            tmpString = "";
            if (log.isErrorEnabled()) {
                log.error("Ranked Hit Detail has no IplugClassName !!!! hit=" + hit + ", detail=" + detail);
            }
        }

        if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher") || tmpString.equals("de.ingrid.iplug.csw.dsc.index.DSCSearcher")) {
            processDSCHit(hit, detail, ds);
        } else if (tmpString.equals("de.ingrid.iplug.se.NutchSearcher")) {
            hit.put(Settings.RESULT_KEY_TYPE, "www-style");
        } else if (tmpString.equals("de.ingrid.iplug.tamino.TaminoSearcher")) {
            hit.put(Settings.RESULT_KEY_URL_TYPE, "dsc");
            hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
        } else {
            hit.put(Settings.RESULT_KEY_TYPE, "unknown-style");
        }
    }

    public static IngridHits processUnrankedHits(IngridHits hits, String selectedDS) {

        try {

            // check for null hits
            if (hits == null) {
                return null;
            }

            IngridHit[] hitArray = hits.getHits();

            // TODO: Why not fetch all Details at Once like
            // IngridHitDetail[] details = ibus.getDetails(hitArray, query, null); ?????????

            String tmpString = null;
            IngridHit hit = null;
            IngridHitDetail detail = null;
            PlugDescription plugDescr = null;
            for (int i = 0; i < hitArray.length; i++) {
                try {
                    // top hit of group !
                    hit = hitArray[i];

                    plugDescr = (PlugDescription) hit.get("plugDescr");
                    if (plugDescr != null) {
                        UtilsSearch.transferPlugDescription(hit, plugDescr);
                    }

                    detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);

                    // if no detail, skip processing OF THIS HIT !
                    if (detail == null) {
                        if (log.isErrorEnabled()) {
                            log.error("Unranked Hit Detail is NULL !!!!!!!!!!!!!!!!! hit=" + hit);
                        }
                        continue;
                    }

                    UtilsSearch.transferHitDetails(hit, detail);

                    /*
                     // NOT NEEDED ANYMORE, WE ALWAYS RENDER ONLY FIRST HIT !!!!

                     // check for grouping and transfer details of "sub hits"
                     subHitArray = hit.getGroupHits();
                     if (subHitArray.length > 0) {
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

                     detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
                     if (detail != null) {
                     UtilsSearch.transferHitDetails(hit, detail);
                     }
                     }
                     }
                     */

                    tmpString = detail.getIplugClassName();
                    if (tmpString == null) {
                        tmpString = "";
                        if (log.isErrorEnabled()) {
                            log.error("Hit Detail has no IplugClassName !!!! hit=" + hit + ", detail=" + detail);
                        }
                    }

                    if (tmpString.equals("de.ingrid.iplug.ecs.UDKPlug")) {
                        processDSCHit(hit, detail, selectedDS);
                    } else if (tmpString.equals("de.ingrid.iplug.csw.CSWPlug")) {
                        processDSCHit(hit, detail, selectedDS);

                        // JUST FOR TESTING, SHOULD NEVER BE UNRANKED !
                        //                    } else if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                        //                        processDSCHit(hit, detail, selectedDS);

                    } else {
                        hit.put(Settings.RESULT_KEY_TYPE, "www-style");
                    }
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Unranked Hit, hit = " + hit + ", detail = " + detail, t);
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

    /**
     * Process DSC Hit (Data Source Client).
     * NOTICE: Also called from non DSC iPlugs which behave like a DSC ! (UDKPlug, CSWPlug ...)
     * 
     * @param hit
     * @param detail
     * @param ds
     */
    private static void processDSCHit(IngridHit hit, IngridHitDetail detail, String ds) {
        String tmpString = null;

        try {
            hit.put(Settings.RESULT_KEY_URL_TYPE, "dsc");
            if (hit.get(Settings.RESULT_KEY_URL) != null) {
                hit.put(Settings.RESULT_KEY_TYPE, "www-style");
            } else {
                hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
            }

            // read for all dsc iplugs

            // services !
            
            // WMS, only process if Viewer is specified !
            Object obj = detail.get(Settings.HIT_KEY_WMS_URL);
            if (obj != null && WMSInterfaceImpl.getInstance().hasWMSViewer()) {
                tmpString = "";
                if (obj instanceof String[]) {
                    // PATCH for wrong data in database -> service string was passed multiple times !
                	// only show FIRST (!!!! This is an assumption that has been made to reduce the effort!!! ) WMS getCapabilities URL
                	String[] servicesArray = (String[]) obj;
                     for (int j = 0; j < servicesArray.length; j++) {
                         if (servicesArray[j].toLowerCase().indexOf("service=wms") > -1 && servicesArray[j].toLowerCase().indexOf("request=getcapabilities") > -1) {
                         	tmpString = servicesArray[j];
                         	break;
                         }
                     }
                } else {
                    tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_WMS_URL);
                    // only show WMS getCapabilities URL
                    if (tmpString.toLowerCase().indexOf("service=wms") == -1 || tmpString.toLowerCase().indexOf("request=getcapabilities") == -1) {
                    	tmpString = "";
                    }
                }
                if (tmpString.length() > 0) {
                    try {
                        hit.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(tmpString.trim(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        log.error("Error url encoding wms URL!", e);
                    }
                }
            }

            // determine type of hit dependent from plug description !!!
            PlugDescription plugDescr = (PlugDescription) hit.get("plugDescr");
            boolean isObject = true;
            if (plugDescr != null) {
            	List typesPlug = Arrays.asList(plugDescr.getDataTypes());
            	for (int i=0; i < Settings.QVALUES_DATATYPES_ADDRESS.length; i++) {
                	if (typesPlug.contains(Settings.QVALUES_DATATYPES_ADDRESS[i])) {
                		isObject = false;
                		break;
                	}
            	}
            }
            if (isObject) {
                hit.put(Settings.RESULT_KEY_UDK_IS_ADDRESS, new Boolean(false));
                tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_UDK_CLASS);
                if (tmpString.length() > 0) {
                    hit.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                }
                tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_OBJ_ID);
                hit.put(Settings.RESULT_KEY_DOC_UUID, tmpString);
            } else {
                hit.put(Settings.RESULT_KEY_UDK_IS_ADDRESS, new Boolean(true));
                tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDRID);
                hit.put(Settings.RESULT_KEY_DOC_UUID, tmpString);

                String addrClass = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_CLASS);
                hit.put(Settings.RESULT_KEY_UDK_CLASS, addrClass);
                
                if (addrClass.equals("2") || addrClass.equals("3")) {
                    // person and free person entry 
                	hit.put(Settings.RESULT_KEY_UDK_ADDRESS_FIRSTNAME, UtilsSearch.getDetailValue(detail,
                            Settings.HIT_KEY_ADDRESS_FIRSTNAME));
                    hit.put(Settings.RESULT_KEY_UDK_ADDRESS_LASTNAME, UtilsSearch.getDetailValue(detail,
                            Settings.HIT_KEY_ADDRESS_LASTNAME));
                    hit.put(Settings.RESULT_KEY_UDK_ADDRESS_TITLE, UtilsSearch.getDetailValue(detail,
                            Settings.HIT_KEY_ADDRESS_TITLE));
                    hit.put(Settings.RESULT_KEY_UDK_ADDRESS_SALUTATION, UtilsSearch.getDetailValue(detail,
                            Settings.HIT_KEY_ADDRESS_ADDRESS));
                } 
                
                if (addrClass.equals("1") || addrClass.equals("0") || addrClass.equals("2")) {
                    // person, unit, institution
                	String currentAddressId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDRID);
                    String newAddressId = null;
                    boolean skipSearch = false;
                    
                    String tmpAddressInstitution = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_INSTITUTION2);
                    String tmpAddrClass = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_CLASS2);
                    String tmpAddressId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDRID2);
                    String tmpTitle = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_INSTITUTION);
                    if (tmpTitle == null) {
                    	tmpTitle = "";
                    }
                    
                    if (tmpAddressId != null && tmpAddressId.length()>0) {
        	            // we do have parent addresses included in the original result
                    	for (int i=0; i<2; i++) {
        	            	if (i==1) {
        	                    tmpAddressInstitution = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_INSTITUTION3);
        	                    tmpAddrClass = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_CLASS3);
        	                    tmpAddressId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDRID3);
        	                    String tmpAddressFromId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDR_FROM_ID3);
        	                    // check for more address parents, skip further querying if no more parents are available
        	                    if (tmpAddressFromId == null || tmpAddressFromId.length() == 0) {
        	                    	skipSearch = true;
        	                    }
        	            	}
        	                if (tmpAddressInstitution != null && tmpAddressInstitution.length() > 0) {
        	                    if (tmpTitle.length() > 0) {
        	                    	tmpTitle = tmpAddressInstitution.concat(
        	                                ", ").concat(tmpTitle);
        	                    } else {
        	                    	tmpTitle = tmpAddressInstitution;
        	                    }
        	                }
        	                if (tmpAddrClass == null || tmpAddrClass.length() == 0) {
        	              		// no more parent addresses available than included in original results, skip parent address retrieval
        	               		skipSearch = true;
        	                	break;
        	                }
        	            }
                    } else {
                        String tmpAddressFromId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDR_FROM_ID);
                        // check for more address parents, skip further querying if no more parents are available
                        if (tmpAddressFromId == null || tmpAddressFromId.length() == 0 ||  tmpAddressFromId.equals(currentAddressId)) {
                        	skipSearch = true;
                        }
                    }                    
                    
    	            // if a parent address id was included in orinal request, use this for further querying
                	if (tmpAddressId != null && tmpAddressId.length() > 0) {
    	            	currentAddressId = tmpAddressId;
    	            }

                	while (!skipSearch) {
                        IngridQuery query = QueryStringParser.parse("T022_adr_adr.adr_to_id:".concat(currentAddressId)
                                .concat(" datatype:address ranking:score"));
                        IngridHits results = IBUSInterfaceImpl.getInstance().search(query, 10, 1, 0,
                                PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
                        if (results.getHits().length > 0) {
                            IngridHitDetail details[] = IBUSInterfaceImpl.getInstance().getDetails(results.getHits(),
                                    query,
                                    new String[] { Settings.HIT_KEY_ADDRESS_ADDRID, Settings.HIT_KEY_ADDRESS_CLASS });
                            // find first parent of the address in the result set
                            for (int j = 0; j < details.length; j++) {
                                IngridHitDetail addrDetail = (IngridHitDetail) details[j];
                                addrClass = UtilsSearch.getDetailValue(addrDetail, Settings.HIT_KEY_ADDRESS_CLASS);
                                newAddressId = UtilsSearch.getDetailValue(addrDetail, Settings.HIT_KEY_ADDRESS_ADDRID);
                                if ((addrClass.equals("0") || addrClass.equals("1"))
                                        && !currentAddressId.equals(newAddressId)) {
                                	tmpAddressInstitution = UtilsSearch.getDetailValue(addrDetail, Settings.HIT_KEY_ADDRESS_INSTITUTION);
                                	if (tmpTitle.length() > 0) {
            	                    	tmpTitle = tmpAddressInstitution.concat(
            	                                ", ").concat(tmpTitle);
            	                    } else {
            	                    	tmpTitle = tmpAddressInstitution;
            	                    }
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
                    hit.put(Settings.RESULT_KEY_UDK_TITLE, tmpTitle);
                }
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing DSC Hit ! hit = " + hit + ", detail=" + detail, ex);
            }
        }
    }

}
