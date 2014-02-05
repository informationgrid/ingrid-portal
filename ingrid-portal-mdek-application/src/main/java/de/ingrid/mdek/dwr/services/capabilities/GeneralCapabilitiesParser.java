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

import javax.xml.xpath.XPathExpressionException;

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
    
    private final static Logger log = Logger.getLogger(GeneralCapabilitiesParser.class);
    
    /** ID of syslist entry "HTTPGet" in Syslist 5180 */
    protected final static Integer ID_OP_PLATFORM_HTTP_GET = 7;
    /** ID of syslist entry "HTTPPost" in Syslist 5180 */
    protected final static Integer ID_OP_PLATFORM_HTTP_POST = 8;
    
    protected XPathUtils xPathUtils;
    
    protected SysListCache syslistCache;

    protected ConnectionFacade connectionFacade;

    public GeneralCapabilitiesParser(XPathUtils xPathUtils, SysListCache syslistCache) {
        this.xPathUtils = xPathUtils;
        this.syslistCache = syslistCache;
        this.connectionFacade = syslistCache.getConnectionFacade();
    }

    protected List<String> getKeywords(Node doc, String xpath) throws XPathExpressionException {
        String[] keywordsArray = xPathUtils.getStringArray(doc, xpath);
        // explicit conversion into an ArrayList to support the addAll-Method
        return new ArrayList<String>(Arrays.asList(keywordsArray));
    }
    
    protected List<SNSTopic> getKeywordsFromLayer(List<String> keywords) throws XPathExpressionException {
        List<SNSTopic> snsTopics = new ArrayList<SNSTopic>();
        for (String keyword : keywords) {
            SNSTopic snsTopic = new SNSTopic(SNSTopic.Type.NON_DESCRIPTOR, null, null, keyword, null, null);
            snsTopics.add(snsTopic);
        }
        return snsTopics;
    }
    
    protected OperationBean mapToOperationBean(Document doc, String[] xPathsOfMethods, Integer[] platformsOfMethods) throws XPathExpressionException {
        OperationBean opBean = new OperationBean();
        List<String> methodAddresses = new ArrayList<String>();
        List<Integer> methodPlatforms = new ArrayList<Integer>();
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
        } catch (ParseException e) {}
        return d;
    }
    
    private List<ConformityBean> mapToConformityBeans(Document doc, String xPath) {
        List<ConformityBean> beans = new ArrayList<ConformityBean>();
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
        
        // syslist has no ISO code yet, so we compare hard coded!
        /*Integer confKey = syslistCache.getKeyFromListId(6000, value);

        // fallback to English ... should be ISO value to check for!
        if (confKey == null) {
            List<String[]> syslists = syslistCache.getSyslistByLanguage(6000, "en");
            if (syslists.size() > 0) {
                for (String[] entry : syslists) {
                    if (entry[0].trim().equalsIgnoreCase(value.trim())) {
                        return Integer.valueOf(entry[1]);
                    }
                }
            }
            // default set to "not evaluated" (3)
            confKey = 3;
        }*/
        
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
        if (name == null) return null;
        
        String[] splitByComma = name.split(",");
        if (splitByComma.length > 1) {
            String[] result = {splitByComma[1], splitByComma[0]}; 
            return result;
        } else {
            String[] splitBySpace = name.split(" ");
            if (splitBySpace.length == 2) {
                String[] result = {splitBySpace[0], splitBySpace[1]}; 
                return result;
            } else if (splitBySpace.length > 2) {
                String[] result = {splitBySpace[splitBySpace.length-2], splitBySpace[splitBySpace.length-1]}; 
                return result;
                
            }
        }
        return null;
    }
    
    protected List<String> getNodesContentAsList(Document doc, String xPath) {
        NodeList versionNodes = xPathUtils.getNodeList(doc, xPath);
        List<String> list = new ArrayList<String>();
        if ( versionNodes == null ) return list;
        for (int i = 0; i < versionNodes.getLength(); i++) {
            String content = versionNodes.item(i).getTextContent();
            if (content.trim().length() > 0) {
                list.add(content);
            }
        }
        return list;
    }
    
    protected List<UrlBean> getOnlineResources(Document doc, String xPath) {
        NodeList orNodes = xPathUtils.getNodeList(doc, xPath);
        List<UrlBean> urls = new ArrayList<UrlBean>();
        if (orNodes != null) {
            for (int i = 0; i < orNodes.getLength(); i++) {
                UrlBean url = new UrlBean();
                String link = xPathUtils.getString(orNodes.item(i), "@xlink:href");
                if (link != null) url.setUrl(link);
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
        List<UrlBean> locators = new ArrayList<UrlBean>();
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
                List<TimeReferenceBean> timeSpans = new ArrayList<TimeReferenceBean>();
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
        if (syslists.size() > 0) {
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
                AddressType.getHQLExcludeIGEUsersViaNode("aNode") + // exclude hidden user addresses !
                " AND ((addr.lastname = '" + address.getLastname() + "'" +
                    " AND addr.firstname = '" + address.getFirstname() + "' ) " +
                    " OR addr.institution = '" + address.getOrganisation() + "' ) " +
                " AND comm.commtypeKey = 3 " +  // type: email
                " AND comm.commValue = '" + address.getEmail() + "'";

        IngridDocument response = connectionFacade.getMdekCallerQuery().queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, null, "");
        IngridDocument result = MdekUtils.getResultFromResponse(response);
        if (result != null) {
            List<IngridDocument> addresses = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
            
            // add the found uuid to the address object which marks it as found
            // if there are more than one results, then use the first one!
            if (addresses != null && addresses.size() > 0) {
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
            List<IngridDocument> objects = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
            if (objects != null && objects.size() > 0) {
                resultBean.setRef1ObjectIdentifier( id );
                resultBean.setObjectClass( objects.get( 0 ).getInt( "obj.objClass" ) );
                resultBean.setUuid( objects.get( 0 ).getString( "oNode.objUuid" ) );
                resultBean.setTitle( objects.get( 0 ).getString( "obj.objName" ) );
                return resultBean;
            }
        }
        
        return null;
    }
}
