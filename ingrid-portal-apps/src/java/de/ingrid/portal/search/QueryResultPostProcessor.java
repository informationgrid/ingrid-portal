/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridHitWrapper;
import de.ingrid.portal.global.IngridHitsWrapper;
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

    private final static Logger log = LoggerFactory.getLogger(QueryResultPostProcessor.class);

    public static IngridHitsWrapper processRankedHits(IngridHitsWrapper hits, String ds) {
        try {
            if (hits == null) {
                return null;
            }
            IngridHitWrapper[] hitArray = hits.getWrapperHits();

            IngridHitWrapper hit = null;
            IngridHitDetail detail = null;

            for (int i = 0; i < hitArray.length; i++) {
                try {
                    hit = hitArray[i];
                    detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);

                    processRankedHit(hit, detail, ds);
                    
                    // also process group sub hits !
                    hit = (IngridHitWrapper) hit.get(Settings.RESULT_KEY_SUB_HIT);
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

    private static void processRankedHit(IngridHitWrapper hit, IngridHitDetail detail, String ds) {
        // if no detail, skip processing OF THIS HIT !
        if (detail == null) {
            if (log.isErrorEnabled()) {
                log.error("Ranked Hit Detail is NULL !!!!!!!!!!!!!!!!! hit=" + hit);
            }
            return;
        }

        UtilsSearch.transferHitDetails(hit, detail);

        PlugDescription plugDescr = (PlugDescription) hit.get(Settings.RESULT_KEY_PLUG_DESCRIPTION);
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

        if (tmpString.endsWith("DSCSearcher") || tmpString.endsWith("DscSearchPlug")) {
            processDSCHit(hit, detail, ds);
        } else if (tmpString.equals("de.ingrid.iplug.se.NutchSearcher")) {
            hit.put(Settings.RESULT_KEY_TYPE, "www-style");
        } else if (tmpString.equals("de.ingrid.iplug.tamino.TaminoSearcher")) {
            hit.put(Settings.RESULT_KEY_URL_TYPE, "dsc");
            hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
        } else if (tmpString.equals("de.ingrid.iplug.opensearch.OpenSearchPlug")) {
            hit.put(Settings.RESULT_KEY_URL_TYPE, "opensearch");
        } else if (plugDescr.containsDataType("dsc_other")){
            hit.put(Settings.RESULT_KEY_URL_TYPE, "dsc");
            hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
        } else {
            hit.put(Settings.RESULT_KEY_TYPE, "unknown-style");
        }
    }

    public static IngridHitsWrapper processUnrankedHits(IngridHitsWrapper hits, String selectedDS) {

        try {

            // check for null hits
            if (hits == null) {
                return null;
            }

            IngridHitWrapper[] hitArray = hits.getWrapperHits();

            // TODO: Why not fetch all Details at Once like
            // IngridHitDetail[] details = ibus.getDetails(hitArray, query, null); ?????????

            String tmpString = null;
            IngridHitWrapper hit = null;
            IngridHitDetail detail = null;
            PlugDescription plugDescr = null;
            for (int i = 0; i < hitArray.length; i++) {
                try {
                    // top hit of group !
                    hit = hitArray[i];

                    plugDescr = (PlugDescription) hit.get(Settings.RESULT_KEY_PLUG_DESCRIPTION);
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
                    } else if (tmpString.endsWith("DSCSearcher") || tmpString.endsWith("DscSearchPlug")) {
                        processDSCHit(hit, detail, selectedDS);

                    // JUST FOR TESTING, SHOULD NEVER BE UNRANKED !
                    // Times have changed!!!
                    // Customer wants to show DSC-results in unranked column
                    } else if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                        processDSCHit(hit, detail, selectedDS);

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
    private static void processDSCHit(IngridHitWrapper hit, IngridHitDetail detail, String ds) {
        String tmpString = null;

        try {
            hit.put(Settings.RESULT_KEY_URL_TYPE, "dsc");
            if (hit.get(Settings.RESULT_KEY_URL) != null) {
                hit.put(Settings.RESULT_KEY_TYPE, "www-style");
            } else {
                hit.put(Settings.RESULT_KEY_TYPE, "detail-style");
            }

            // read for all dsc iplugs

            Object additionalHtml = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_ADDITIONAL_HTML_1);
            hit.put(Settings.RESULT_KEY_ADDITIONAL_HTML_1, additionalHtml);

            // services !
            
            // WMS, only process if Viewer is specified !
            String[] servicesArray = (String[]) detail.get(Settings.HIT_KEY_WMS_URL);
            
            if (servicesArray == null || servicesArray.length < 1) {
            	// there has been a change in the case of this key
            	servicesArray = (String[]) detail.get(Settings.HIT_KEY_WMS_URL.toLowerCase());
            }
            
            if (servicesArray != null && WMSInterfaceImpl.getInstance().hasWMSViewer()) {
                boolean objServHasAccessConstraint = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_OBJ_SERV_HAS_ACCESS_CONSTRAINT).equals("Y") ? true : false;
                
                if (!objServHasAccessConstraint) {
  
                  Object serviceTypeArray = detail.get(Settings.HIT_KEY_OBJ_SERV_TYPE);
                  Object serviceTypeKeyArray = detail.get(Settings.HIT_KEY_OBJ_SERV_TYPE_KEY);
                  String serviceType = "";
                  String serviceTypeKey = "";
                  if (serviceTypeArray instanceof String[] && ((String[])serviceTypeArray).length > 0) {
                      serviceType = ((String[])serviceTypeArray)[0];
                  }
                  if (serviceTypeKeyArray instanceof String[] && ((String[])serviceTypeKeyArray).length > 0) {
                	  serviceTypeKey = ((String[])serviceTypeKeyArray)[0];
                  }  
                  tmpString = "";
                  if (servicesArray instanceof String[]) {
                      // PATCH for wrong data in database -> service string was passed multiple times !
                  	// only show FIRST (!!!! This is an assumption that has been made to reduce the effort!!! ) WMS getCapabilities URL
                  	
                       for (int j = 0; j < servicesArray.length; j++) {
                           if (serviceTypeKey.toLowerCase().equals("2") || serviceType.toLowerCase().indexOf("wms") != -1 || serviceType.toLowerCase().indexOf("view") != -1 || (servicesArray[j].toLowerCase().indexOf("service=wms") > -1 && servicesArray[j].toLowerCase().indexOf("request=getcapabilities") > -1)) {
                           	tmpString = servicesArray[j];
                           	break;
                           }
                       }
                  } else {
                      tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_WMS_URL);
                      // only show WMS getCapabilities URL
                      if (serviceType.indexOf("wms") != -1 || serviceType.indexOf("view") != -1 || (tmpString.toLowerCase().indexOf("service=wms") == -1 || tmpString.toLowerCase().indexOf("request=getcapabilities") == -1)) {
                      	tmpString = "";
                      }
                  }
                  
                  if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)){
	                  if (tmpString.length() > 0) {
	                      try {
	                          if (tmpString.toLowerCase().indexOf("request=getcapabilities") == -1) {
	                              if (tmpString.indexOf("?") == -1) {
	                                  tmpString = tmpString + "?";
	                              }
	                              if (!tmpString.endsWith("?")) {
	                                  tmpString = tmpString + "&";
	                              }
	                              tmpString = tmpString + "REQUEST=GetCapabilities&SERVICE=WMS&VERSION=1.1.1";
	                          } else if (tmpString.toLowerCase().indexOf("version=") == -1){
	                              if (!tmpString.endsWith("&")) {
	                                  tmpString = tmpString + "&";
	                              }
	                              tmpString = tmpString + "VERSION=1.1.1";
	                          }
	                          hit.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(tmpString.trim(), "UTF-8"));
	                      } catch (UnsupportedEncodingException e) {
	                          log.error("Error url encoding wms URL!", e);
	                      }
	                  }
                  }
                }
            }
            
            PlugDescription plugDescr = (PlugDescription) hit.get(Settings.RESULT_KEY_PLUG_DESCRIPTION);
            
            if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)){
	              if(UtilsSearch.getDetailValue(detail, "kml").length() > 0){
	            	  hit.put(Settings.RESULT_KEY_WMS_COORD, "action=doTmpService&" + Settings.RESULT_KEY_PLUG_ID + "=" + hit.getPlugId() + "&" + Settings.RESULT_KEY_DOC_ID + "=" + hit.getDocumentId());
	              }
            }
            // determine type of hit dependent from plug description !!!
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
                } else {
                    tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_UDK_CLASS.toLowerCase());
                    if (tmpString.length() > 0) {
                        hit.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                    }
                }
                
                tmpString = null;
                tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ORG_OBJ_ID);
                
                if(tmpString == null){
                	tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_OBJ_ID);
                }
                
                if (tmpString.length() > 0) {
                    hit.put(Settings.RESULT_KEY_DOC_UUID, tmpString);
                } else {
                    tmpString = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_OBJ_ID.toLowerCase());
                    if (tmpString.length() > 0) {
                        hit.put(Settings.RESULT_KEY_DOC_UUID, tmpString);
                    }
                }
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
                        IngridHits hits = IBUSInterfaceImpl.getInstance().searchAndDetail(query, 10, 1, 0,
                                PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000),
                                new String[] { Settings.HIT_KEY_ADDRESS_ADDRID, Settings.HIT_KEY_ADDRESS_CLASS });
                        IngridHit[] hitArray = hits.getHits();
                        if (hitArray.length > 0) {
                            // find first parent of the address in the result set
                        	for (int j = 0; j < hitArray.length; j++) {
                                IngridHitDetail addrDetail = (IngridHitDetail) hitArray[j].getHitDetail();
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
