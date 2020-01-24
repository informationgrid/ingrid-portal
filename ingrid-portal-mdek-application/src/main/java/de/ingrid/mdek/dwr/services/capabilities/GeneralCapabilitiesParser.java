/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.AddressType;
import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.beans.object.ConformityBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.TimeReferenceBean;
import de.ingrid.mdek.beans.object.UrlBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André
 *
 */
public class GeneralCapabilitiesParser {
    
    private static final Logger log = Logger.getLogger(GeneralCapabilitiesParser.class);
    
    /** ID of syslist entry "HTTPGet" in Syslist 5180 */
    protected static final Integer ID_OP_PLATFORM_HTTP_GET = 7;
    /** ID of syslist entry "HTTPPost" in Syslist 5180 */
    protected static final Integer ID_OP_PLATFORM_HTTP_POST = 8;
    
    protected XPathUtils xPathUtils;
    
    protected SysListCache syslistCache;

    protected ConnectionFacade connectionFacade;
    
    public GeneralCapabilitiesParser(XPathUtils xPathUtils, SysListCache syslistCache) {
        this.xPathUtils = xPathUtils;
        this.syslistCache = syslistCache;
        this.connectionFacade = syslistCache.getConnectionFacade();
    }

    protected List<String> getKeywords(Node doc, String xpath) {
        String[] keywordsArray = xPathUtils.getStringArray(doc, xpath);
        // explicit conversion into an ArrayList to support the addAll-Method
        return new ArrayList<>(Arrays.asList(keywordsArray));
    }
    
    protected List<SNSTopic> transformKeywordListToSNSTopics(List<String> keywords) {
        List<SNSTopic> snsTopics = new ArrayList<>();
        for (String keyword : keywords) {
            SNSTopic snsTopic = new SNSTopic(SNSTopic.Type.NON_DESCRIPTOR, null, null, keyword, null, null);
            snsTopics.add(snsTopic);
        }
        return snsTopics;
    }
    
    protected OperationBean mapToOperationBean(Document doc, String[] xPathsOfMethods, Integer[] platformsOfMethods) {
        OperationBean opBean = new OperationBean();
        List<String> methodAddresses = new ArrayList<>();
        List<Integer> methodPlatforms = new ArrayList<>();
        for (int i=0; i < xPathsOfMethods.length; i++) {
            String methodAddress = xPathUtils.getString(doc, xPathsOfMethods[i]);
            if (methodAddress != null && methodAddress.length() != 0) {
                methodAddresses.add(methodAddress);
                methodPlatforms.add(platformsOfMethods[i]);
            }
        }
        opBean.setPlatform(methodPlatforms);
        opBean.setAddressList(methodAddresses);
        
        return opBean;
    }
    
    protected TimeReferenceBean mapToTimeReferenceBean(Document doc, String xPath) {
        TimeReferenceBean timeRef = null;
        
        String date = xPathUtils.getString(doc, xPath);
        // determine type of date 
        Integer dateType = getDateType(xPath);
        
        if (date != null && dateType != null) {
            timeRef = new TimeReferenceBean();
            timeRef.setType(dateType);            
            
            Date dateObj = getSimpleDate(date);
            if (dateObj != null)
                timeRef.setDate(dateObj);
        }
        
        return timeRef;
    }
    
    protected Date getSimpleDate(String date) {
        // add date to time reference
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = formatter.parse(date);
        } catch (ParseException e) {
            log.debug("Error on getSimpleDate", e);
        }
        return d;
    }
    
    private List<ConformityBean> mapToConformityBeans(Document doc, String xPath) {
        List<ConformityBean> beans = new ArrayList<>();
        NodeList conformityNodes = xPathUtils.getNodeList(doc, xPath);
        if ( conformityNodes != null ) {
            for (int index = 0; index < conformityNodes.getLength(); ++index) {
                ConformityBean bean = new ConformityBean();
                Integer degree = getConformityDegree(xPathUtils.getString(conformityNodes.item(index), "inspire_common:Degree"));
                bean.setLevel(degree);
                // TODO: convert title to specific language!?
                bean.setSpecification(xPathUtils.getString(conformityNodes.item(index), "inspire_common:Specification/inspire_common:Title"));
                beans.add(bean);
            }
        }
        
        return beans;
    }
    
    /**
     * @param string
     * @return
     */
    private Integer getConformityDegree(String value) {
        if (value == null) return null;
        
        // set result to "not evaluated" by default!
        Integer confKey = 3;
        if ("conformant".equals(value)) {
            confKey = 1;
        } else if ("not conformant".equals(value)) {
            confKey = 2;
        }
        
        return confKey;
    }

    private Integer getDateType(String xPath) {
        if (xPath.indexOf("DateOfPublication") != -1)
            return 2;
        else if (xPath.indexOf("DateOfCreation") != -1)
            return 1;
        else if (xPath.indexOf("DateOfLastRevision") != -1)
            return 3;
        
        return null;
    }
    
    /**
     * This method extracts first- and lastname from a string. Depending on if there
     * is a "," the order is reversed! 
     * @param name
     * @return
     */
    protected String[] extractName(String name) {
        if (name != null){
            String[] splitByComma = name.trim().split(",");
            if (splitByComma.length > 1) {
                String[] result = {splitByComma[1].trim(), splitByComma[0].trim()}; 
                return result;
            } else {
                String[] splitBySpace = name.split(" ");
                if (splitBySpace.length == 2) {
                    String[] result = {splitBySpace[0].trim(), splitBySpace[1].trim()}; 
                    return result;
                } else if (splitBySpace.length > 2) {
                    String[] sub = Arrays.copyOfRange(splitBySpace, 0, splitBySpace.length-1);
                    String[] result = {String.join( " ", sub ), splitBySpace[splitBySpace.length-1].trim()};
                    return result;
                } else {
                    String[] result = {"", name.trim()};
                    return result; 
                }
            }
        }
        return new String[0];
    }
    
    /**
     * Extract first and last name from name string and deploy a given 
     * AddressBean with it. If no first name can be detected, use the 
     * complete name string as last name 
     * 
     * @param ab
     * @param address
     * @return
     */
    protected AddressBean setNameInAddressBean(AddressBean ab, String name) {
        String[] nameParts = this.extractName(name);
        if (nameParts == null || nameParts.length == 0) {
            ab.setLastname("N/A");
        } else if (nameParts.length == 1) {
            ab.setLastname(nameParts[0]);
        } else if (nameParts.length == 2) {
            ab.setFirstname(nameParts[0].trim());
            ab.setLastname(nameParts[1].trim());
        }
        return ab;
    }
    
    protected List<String> getNodesContentAsList(Document doc, String xPath) {
        NodeList versionNodes = xPathUtils.getNodeList(doc, xPath);
        List<String> list = new ArrayList<>();
        if ( versionNodes == null ) return list;
        for (int i = 0; i < versionNodes.getLength(); i++) {
            String content = versionNodes.item(i).getTextContent();
            if (content.trim().length() > 0) {
                    list.add( content );
            }
        }
        return list;
    }
    
    protected List<String> mapVersionsFromCodelist(Integer listId, List<String> versionList, Map<String, Integer> versionSyslistMap) {
        List<String> mappedVersionList = new ArrayList<>(); 
        for (String version : versionList) {
            Integer entryId = versionSyslistMap.get( version );
            String value = version;
            
            if (entryId != null) {
                value = syslistCache.getValueFromListId( listId, entryId, true );
                if (value == null) {
                    log.warn( "Version could not be mapped!" );
                }
            }
            
            if (value != null) {
                mappedVersionList.add( value );
            }
        }
        return mappedVersionList;
    }
    
    protected List<UrlBean> getOnlineResources(Document doc, String xPath) {
        NodeList orNodes = xPathUtils.getNodeList(doc, xPath);
        List<UrlBean> urls = new ArrayList<>();
        if (orNodes != null) {
            for (int i = 0; i < orNodes.getLength(); i++) {
                UrlBean url = new UrlBean();
                String link = xPathUtils.getString(orNodes.item(i), "@xlink:href");
                
                // do not add link if there's none (#781)
                if (link == null || link.trim().equals( "" )) continue;
                
                url.setUrl(link);
                String type = xPathUtils.getString(orNodes.item(i), "@xlink:type");
                if (type != null) url.setDatatype(type);
                
                urls.add(url);
            }
        }
        return urls;
    }
    
    /**
     * @param doc
     * @param xPathExtCap, the path to the extended capabilities element
     * @return
     */
    protected List<UrlBean> getResourceLocators(Document doc, String xPathExtCap) {
        List<UrlBean> locators = new ArrayList<>();
        NodeList url = xPathUtils.getNodeList(doc, xPathExtCap + "/inspire_common:ResourceLocator/inspire_common:URL");
        if ( url != null ) {
            for (int i = 0; i < url.getLength(); i++) {
                UrlBean urlBean = new UrlBean();
                String type = xPathUtils.getString(doc, xPathExtCap + "/inspire_common:ResourceType["+(i+1)+"]");
                urlBean.setUrl(url.item(i).getTextContent());
                if (type != null) {
                    urlBean.setRelationType(getRelationType(type));
                    urlBean.setRelationTypeName(syslistCache.getValueFromListId(2000, urlBean.getRelationType(), false));
                } else {
                    // use previously used type!
                    if (i > 0) {
                        urlBean.setRelationType(locators.get(i-1).getRelationType());
                        urlBean.setRelationTypeName(locators.get(i-1).getRelationTypeName());
                    }
                }
                locators.add(urlBean);            
            }
        }
        
        return locators;
    }
    
    /**
     * @param type
     * @return
     */
    private Integer getRelationType(String type) {
        if ("service".equals(type))
            return 5066; // Link to Service
        
        // else unspecified link
        return 9999;
    }
    
    /**
     * @param bean
     * @param doc
     * @param xpathExtCap
     */
    protected void addExtendedCapabilities(CapabilitiesBean bean, Document doc, String xpathExtCap) {
        if (xPathUtils.nodeExists(doc, xpathExtCap)) {
            // Resource Locator / Type
            bean.setResourceLocators(getResourceLocators(doc, xpathExtCap));
            // Spatial Data Type
            // overwrite service type if defined here
            String type = xPathUtils.getString(doc, xpathExtCap + "/inspire_common:SpatialDataServiceType");
            if (type != null) {
                Integer mappedType = mapServiceTypeToKey(type);
                if (mappedType != null) {
                    bean.setDataServiceType(mappedType);
                } else {
                    log.warn("ServiceType could not be identified from ISO-value: " + type);
                }
            }
            
            // add Temporal References if available
            bean.addTimeReference(mapToTimeReferenceBean(doc, xpathExtCap + "/inspire_common:TemporalReference/inspire_common:DateOfCreation"));
            bean.addTimeReference(mapToTimeReferenceBean(doc, xpathExtCap + "/inspire_common:TemporalReference/inspire_common:DateOfPublication"));
            bean.addTimeReference(mapToTimeReferenceBean(doc, xpathExtCap + "/inspire_common:TemporalReference/inspire_common:DateOfLastRevision"));
            
            // add Timespan if available
            String startDate = xPathUtils.getString(doc, xpathExtCap + "/inspire_common:TemporalReference/inspire_common:TemporalExtent/inspire_common:IntervalOfDates/inspire_common:StartingDate");
            String endDate = xPathUtils.getString(doc, xpathExtCap + "/inspire_common:TemporalReference/inspire_common:TemporalExtent/inspire_common:IntervalOfDates/inspire_common:EndDate");
            if ( startDate != null|| endDate != null ) {
                List<TimeReferenceBean> timeSpans = new ArrayList<>();
                TimeReferenceBean tr = new TimeReferenceBean();
                Date dateObj = getSimpleDate(startDate);
                if (dateObj != null) tr.setFrom(dateObj);
                dateObj = getSimpleDate(endDate);
                if (dateObj != null) tr.setTo(dateObj);
                timeSpans.add(tr);
                bean.setTimeSpans(timeSpans);            
            }
            
            // Extended - Keywords
            String[] extKeywords = xPathUtils.getStringArray(doc, xpathExtCap + "/inspire_common:Keyword/inspire_common:KeywordValue");
            bean.getKeywords().addAll(Arrays.asList(extKeywords));
            
            // Conformity
            bean.setConformities(mapToConformityBeans(doc, xpathExtCap + "/inspire_common:Conformity"));
        }
    }

    /**
     * @param type
     * @return
     */
    private Integer mapServiceTypeToKey(String type) {
        List<String[]> syslists = syslistCache.getSyslistByLanguage(5100, "iso");
        if (!syslists.isEmpty()) {
            for (String[] entry : syslists) {
                if (entry[0].trim().equalsIgnoreCase(type.trim())) {
                    return Integer.valueOf(entry[1]);
                }
            }
        }
        return null;
    }

    /**
     * Search for an existing address by equal firstname, lastname and email OR institution and email.
     * @param address
     */
    protected void searchForAddress( AddressBean address ) {
        String qString = "select aNode.addrUuid, addr.adrType " +
                "from AddressNode aNode " +
                "inner join aNode.t02AddressWork addr " +
                "inner join addr.t021Communications comm " +
                "where " +
                AddressType.getHQLExcludeIGEUsersViaNode("aNode", "addr") + // exclude hidden user addresses !
                " AND ((addr.lastname = '" + address.getLastname() + "'" +
                    " AND addr.firstname = '" + address.getFirstname() + "' ) " +
                    " OR addr.institution = '" + address.getOrganisation() + "' ) " +
                " AND comm.commtypeKey = 3 " +  // type: email
                " AND comm.commValue = '" + address.getEmail() + "'";

        IngridDocument response = connectionFacade.getMdekCallerQuery().queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, null, "");
        IngridDocument result = MdekUtils.getResultFromResponse(response);
        if (result != null) {
            @SuppressWarnings("unchecked")
            List<IngridDocument> addresses = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
            
            // add the found uuid to the address object which marks it as found
            // if there are more than one results, then use the first one!
            if (addresses != null && !addresses.isEmpty()) {
                address.setUuid( addresses.get( 0 ).getString( "aNode.addrUuid" ) );
                address.setType( addresses.get( 0 ).getInt( "addr.adrType" ) );
            }
        }
    }
    
    /**
     * @param id
     * @return
     */
    protected MdekDataBean checkForCoupledResource(String id) {
        MdekDataBean resultBean = new MdekDataBean();
        
        String qString = "select oNode.objUuid, obj.objName, obj.objClass " +
        "from ObjectNode oNode " +
        "inner join oNode.t01ObjectWork obj " +
        "inner join obj.t011ObjGeos oGeo " +
        "where " +
        "oGeo.datasourceUuid = '" + id + "'";
        
        IngridDocument response = connectionFacade.getMdekCallerQuery().queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, null, "");
        IngridDocument result = MdekUtils.getResultFromResponse(response);
        if (result != null) {
            @SuppressWarnings("unchecked")
            List<IngridDocument> objects = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
            if (objects != null && !objects.isEmpty()) {
                resultBean.setRef1ObjectIdentifier( id );
                resultBean.setObjectClass( objects.get( 0 ).getInt( "obj.objClass" ) );
                resultBean.setUuid( objects.get( 0 ).getString( "oNode.objUuid" ) );
                resultBean.setTitle( objects.get( 0 ).getString( "obj.objName" ) );
                return resultBean;
            } else {
            	// if no dataset was found then try another search if a namespace exists in the id
            	// In this case remove the namespace search again (INGRID34-6)
            	int seperatorPos = id.indexOf( '#' );
            	if (seperatorPos != -1) {
            		return checkForCoupledResource( id.substring( seperatorPos + 1 ) );
            	}
            }
        }
        
        return null;
    }
}
