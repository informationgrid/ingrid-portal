/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.MdekUtils.SpatialReferenceType;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.OperationParameterBean;
import de.ingrid.mdek.beans.object.SpatialReferenceSystemBean;
import de.ingrid.utils.xml.Wfs200NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class Wfs200CapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {
    
    private static final String XPATH_EXP_WFS_KEYWORDS_FEATURE_TYPE = "/wfs20:WFS_Capabilities/wfs20:FeatureTypeList/wfs20:FeatureType/ows11:Keywords/ows11:Keyword";
    private static final String XPATH_EXP_WFS_TITLE = "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:Title";
    private static final String XPATH_EXP_WFS_ABSTRACT = "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:Abstract";
    private static final String XPATH_EXP_WFS_VERSION = "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:ServiceTypeVersion";
    
    private static final String XPATH_EXP_WFS_OP_GET_CAPABILITIES_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_GET_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='DescribeFeatureType']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_POST_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='DescribeFeatureType']/ows11:DCP[1]/ows11:HTTP[1]/Post[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_GET_FEATURE_GET_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetFeature']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_GET_FEATURE_POST_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetFeature']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_GET_GML_OBJECT_GET_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetGmlObject']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_GET_GML_OBJECT_POST_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetGmlObject']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_LOCK_FEATURE_GET_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='LockFeature']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_LOCK_FEATURE_POST_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='LockFeature']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_TRANSACTION_GET_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='Transaction']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_OP_TRANSACTION_POST_HREF = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='Transaction']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WFS_FEES = "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:Fees";
    private static final String XPATH_EXP_WFS_ACCESS_CONSTRAINTS = "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:AccessConstraints";
    private static final String XPATH_EXP_WFS_ONLINE_RESOURCE = "/wfs20:WFS_Capabilities/ows11:ServiceProvider/ows11:ServiceContact/ows11:ContactInfo/ows11:OnlineResource";
    private static final String XPATH_EXP_WFS_KEYWORDS = "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:Keywords/ows11:Keyword";
    private static final String XPATH_EXT_WFS_SERVICECONTACT = "/wfs20:WFS_Capabilities/ows11:ServiceProvider/ows11:ServiceContact";
    private static final String XPATH_EXP_WFS_EXTENDED_CAPABILITIES = "/wfs20:WFS_Capabilities/ows11:OperationsMetadata/ows11:ExtendedCapabilities/inspire_dls:ExtendedCapabilities";

    private Map<String, Integer> versionSyslistMap;

    public Wfs200CapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new Wfs200NamespaceContext()), syslistCache);
        
        versionSyslistMap = new HashMap<>();
        versionSyslistMap.put( "1.1.0", 1 );
        versionSyslistMap.put( "2.0", 2 );
        versionSyslistMap.put( "2.0.0", 2 );
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();
        
        // General settings
        result.setServiceType("WFS");
        result.setDataServiceType(3); // download
        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_WFS_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_WFS_ABSTRACT));
        
        List<String> versionList = getNodesContentAsList(doc, XPATH_EXP_WFS_VERSION);
        List<String> mappedVersionList = mapVersionsFromCodelist(MdekSysList.OBJ_SERV_VERSION_WFS.getDbValue(), versionList, versionSyslistMap);
        result.setVersions(mappedVersionList);
        
        String version = versionList.get(0);
        
        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_WFS_FEES));
        
        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_WFS_ACCESS_CONSTRAINTS));
        
        // Online Resources
        result.setOnlineResources(getOnlineResources(doc, XPATH_EXP_WFS_ONLINE_RESOURCE));

        // add extended capabilities to the bean which contains even more information to be used
        addExtendedCapabilities(result, doc, XPATH_EXP_WFS_EXTENDED_CAPABILITIES);
        
        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_WFS_KEYWORDS);
        
        // add keywords from feature types
        List<String> keywordsFeatureType = getKeywords(doc, XPATH_EXP_WFS_KEYWORDS_FEATURE_TYPE);
        
        // add found keywords to our result bean
        keywords.addAll(keywordsFeatureType);
        result.getKeywords().addAll(keywords);
        
        List<LocationBean> union = getBoundingBoxesFromLayers(doc);
        result.setBoundingBoxes( union );
        
        List<SpatialReferenceSystemBean> spatialReferenceSystems = getSpatialReferenceSystems( doc );
        result.setSpatialReferenceSystems( spatialReferenceSystems );
        
        // get contact information
        result.setAddress(getAddress(doc));
        
        // Operation List
        List<OperationBean> operations = new ArrayList<>();

        // Operation - GetCapabilities
        OperationBean getCapabilitiesOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WFS_OP_GET_CAPABILITIES_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET });
        if (!getCapabilitiesOp.getAddressList().isEmpty()) {
            getCapabilitiesOp.setName("GetCapabilities");
            getCapabilitiesOp.setMethodCall("GetCapabilities");
    
            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("VERSION=" + version, "Request version", "", true, false));
            paramList.add(new OperationParameterBean("SERVICE=WFS", "Service type", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("UPDATESEQUENCE=string", "Sequence number or string for cache control", "", true, false));
            getCapabilitiesOp.setParamList(paramList);
            operations.add(getCapabilitiesOp);
        }
        
        // Operation - DescribeFeatureType
        OperationBean describeFeatureTypeOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_GET_HREF, XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!describeFeatureTypeOp.getAddressList().isEmpty()) {
            describeFeatureTypeOp.setName("DescribeFeatureType");
            describeFeatureTypeOp.setMethodCall("DescribeFeatureType");
    
            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("REQUEST=DescribeFeatureType", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("TYPENAME", "A comma separated list of feature types to describe. If no value is specified that is to be interpreted as all feature types", "", true, false));
            paramList.add(new OperationParameterBean("OUTPUTFORMAT", "The output format to use to describe feature types. text/xml; subtype=gml/3.1.1 must be supported. Other output formats, such as DTD are possible", "", true, false));
            describeFeatureTypeOp.setParamList(paramList);
            operations.add(describeFeatureTypeOp);
        }

        // Operation - GetFeature
        OperationBean getFeatureOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WFS_OP_GET_FEATURE_GET_HREF, XPATH_EXP_WFS_OP_GET_FEATURE_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getFeatureOp.getAddressList().isEmpty()) {
            getFeatureOp.setName("GetFeature");
            getFeatureOp.setMethodCall("GetFeature");
    
            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("outputFormat=GML2", "This value is kept for backward compatability and indicates that an XML instance document must be generated that validates against a GML2 application schema", "", true, false));
            paramList.add(new OperationParameterBean("outputFormat=text/xml; subtype=gml/2.1.2", "Same as GML2", "", true, false));
            paramList.add(new OperationParameterBean("outputFormat=text/xml; subtype=gml/3.1.1; subtype=gml/2.1.2", "This value indicates that an XML instance document must be generated that validates against a GML3 application schema. This is the default values of the outputFormat attribute if the attribute is not specified in the GetFeature request", "", true, false));
            paramList.add(new OperationParameterBean("resultType=Results", "The default value results indicates that a web feature service should generate a complete response that contains all the features that satisfy the request. The exact structure of the response is defined in clause 9.3", "", true, false));
            paramList.add(new OperationParameterBean("resultType=Hits", "The value hits indicates that a web feature service should process the GetFeature request and rather than return the entire result set, it should simply indicate the number of feature instance of the requested feature type(s) that satisfy the request. That is that the count should only include instances of feature types specified in the typeName attribute (i.e. GetFeature/Query/@typeName). The exact way in which the feature count is conveyed to a client application is described in clause 9.3", "", true, false));
    
            getFeatureOp.setParamList(paramList);
            operations.add(getFeatureOp);
        }
        
        // Operation - GetGmlObject - optional
        OperationBean getGmlObjectOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WFS_OP_GET_GML_OBJECT_GET_HREF, XPATH_EXP_WFS_OP_GET_GML_OBJECT_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getGmlObjectOp.getAddressList().isEmpty()) {
            getGmlObjectOp.setName("GetGmlObject");
            getGmlObjectOp.setMethodCall("GetGmlObject");

            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("REQUEST=[GetGmlObject]", "The name of the WFS request", "", false, false));
            paramList.add(new OperationParameterBean("TRAVERSEXLINKDEPTH", "The depth to which nested property XLink linking element locator attribute (href) XLinks are traversed and resolved if possible. The range of valid values consists of positive integers plus \"*\" for unlimited", "", false, false));
            paramList.add(new OperationParameterBean("TRAVERSEXLINKEXPIRY", "The number of minutes a WFS should wait to receive a response to a nested GetGmlObject request.. If no value is specified then the period is implementation dependent", "", true, false));
            paramList.add(new OperationParameterBean("GMLOBJECTID", "The XML ID of the element to fetch", "", false, false));

            getGmlObjectOp.setParamList(paramList);
            operations.add(getGmlObjectOp);
        }

        // Operation - LockFeature - optional
        OperationBean lockFeatureOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WFS_OP_LOCK_FEATURE_GET_HREF, XPATH_EXP_WFS_OP_LOCK_FEATURE_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!lockFeatureOp.getAddressList().isEmpty()) {
            lockFeatureOp.setName("LockFeature");
            lockFeatureOp.setMethodCall("LockFeature");

            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("REQUEST=[LockFeature]", "The name of the WFS request", "", false, false));
            paramList.add(new OperationParameterBean("TRAVERSEXLINKDEPTH", "The depth to which nested property XLink linking element locator attribute (href) XLinks are traversed and resolved if possible. The range of valid values consists of positive integers plus \"*\" for unlimited", "", false, false));
            paramList.add(new OperationParameterBean("TRAVERSEXLINKEXPIRY", "The number of minutes a WFS should wait to receive a response to a nested GetGmlObject request.. If no value is specified then the period is implementation dependent", "", true, false));
            paramList.add(new OperationParameterBean("GMLOBJECTID", "The XML ID of the element to fetch", "", false, false));

            lockFeatureOp.setParamList(paramList);
            operations.add(lockFeatureOp);
        }

        // Operation - Transaction - optional
        OperationBean transactionOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WFS_OP_TRANSACTION_GET_HREF, XPATH_EXP_WFS_OP_TRANSACTION_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!transactionOp.getAddressList().isEmpty()) {
            transactionOp.setName("Transaction");
            transactionOp.setMethodCall("Transaction");

            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("REQUEST=Transaction", "The name of the WFS request", "", false, false));
            paramList.add(new OperationParameterBean("OPERATION=Delete", "Transaction operation to execute. Currently only Delete is defined", "", false, false));
            paramList.add(new OperationParameterBean("TYPENAME (Optional if FEATUREID is specified.)", "A list of feature types upon which to apply the operation", "", false, false));
            paramList.add(new OperationParameterBean("RELEASEACTION=[ALL|SOME]", "A value of ALL indicates that all feature locks should be released when a transaction terminates. A value of SOME indicates that only those records that are modified should be released. The remaining locks are maintained", "", true, false));
            paramList.add(new OperationParameterBean("FEATUREID (Mutually exclusive with FILTER and BBOX)", "A list of feature identifiers upon which the specified operation shall be applied. Optional. No default.", "", true, false));
            paramList.add(new OperationParameterBean("FILTER (Prerequisite: TYPENAME) (Mutually exclusive with FEATUREID and BBOX)", "A filter specification describes a set of features to operate upon. The format of the filter is defined in the Filter Encoding Specification [3]. If the FILTER parameter is used, one filter must be specified for each feature type listed in the TYPENAME parameter. Individual filters encoded in the FILTER parameter are enclosed in parentheses \"(\" and \")\"", "", true, false));
            paramList.add(new OperationParameterBean("BBOX (Prerequisite: TYPENAME) (Mutually exclusive with FILTER and FEATUREID)", "In lieu of a FEATUREID or FILTER, a client may specify a bounding box as described in subclause 13.3.3", "", true, false));

            transactionOp.setParamList(paramList);
            operations.add(transactionOp);
        }

        result.setOperations(operations);
        return result;
        
    }
    
    /**
     * @param doc
     * @return
     */
    private List<LocationBean> getBoundingBoxesFromLayers(Document doc) {
        List<LocationBean> bboxes = new ArrayList<>();
        NodeList layers = xPathUtils.getNodeList(doc, "/wfs20:WFS_Capabilities/wfs20:FeatureTypeList/wfs20:FeatureType");
        for (int i = 0; i < layers.getLength(); i++) {
            Node layer = layers.item(i);
            
            String[] lower = xPathUtils.getString( layer, "ows11:WGS84BoundingBox/ows11:LowerCorner" ).split( " " );
            String[] upper = xPathUtils.getString( layer, "ows11:WGS84BoundingBox/ows11:UpperCorner" ).split( " " );
            
            LocationBean box = new LocationBean();
            box.setLatitude1(Double.valueOf( lower[0] ));
            box.setLongitude1(Double.valueOf( lower[1] ));
            box.setLatitude2(Double.valueOf( upper[0] ));
            box.setLongitude2(Double.valueOf( upper[1] ));
            
            // add a fallback for the name, since it's mandatory
            String title = xPathUtils.getString(layer, "wfs20:Title");
            
            box.setName(title);
            // shall be a free spatial reference, but needs an ID to check for duplications!
            box.setTopicId(box.getName());
            box.setType( SpatialReferenceType.FREI.getDbValue() );
            
            bboxes.add(box);
        }
        return bboxes;
    }
    
    /**
     * @param doc
     * @return
     */
    private List<SpatialReferenceSystemBean> getSpatialReferenceSystems(Document doc) {
        List<SpatialReferenceSystemBean> result = new ArrayList<>();
        String[] crs = xPathUtils.getStringArray(doc, "/wfs20:WFS_Capabilities/wfs20:FeatureTypeList/wfs20:FeatureType/wfs20:DefaultCRS");
        String[] crsOther = xPathUtils.getStringArray(doc, "/wfs20:WFS_Capabilities/wfs20:FeatureTypeList/wfs20:FeatureType/wfs20:OtherCRS");
        String[] crsAll = (String[]) ArrayUtils.addAll( crs, crsOther );
        
        List<String> uniqueCrs = new ArrayList<>();
        
        // check codelists for matching entryIds
        for (String item : crsAll) {
            SpatialReferenceSystemBean srsBean = new SpatialReferenceSystemBean();

            Integer itemId;
            try{
                String[] splittedItem = item.split(":");
                itemId = Integer.valueOf(splittedItem[splittedItem.length-1]);
            } catch (NumberFormatException e) {
                // also detect crs like: http://www.opengis.net/def/crs/[epsg|ogc]/0/{code} (REDMINE-2108)
                String[] splittedItem = item.split("/");
                itemId = Integer.valueOf(splittedItem[splittedItem.length-1]);
            }

            String value = syslistCache.getValueFromListId(100, itemId, false);
            if (value == null || value.isEmpty()) {
                srsBean.setId(-1);
                srsBean.setName(item);            
            } else {
                srsBean.setId(itemId);
                srsBean.setName(value);            
            }
            if (!uniqueCrs.contains( srsBean.getName() )) {
                result.add(srsBean);
                uniqueCrs.add( srsBean.getName() );
            }
        }
        
        return result;
    }

    /**
     * @param doc
     * @return
     */
    private AddressBean getAddress(Document doc) {
        AddressBean address = new AddressBean();
        setNameInAddressBean(address, xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:IndividualName"));
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:ElectronicMailAddress"));
        
        // try to find address in database and set the uuid if found
        searchForAddress(address);
        
        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:DeliveryPoint"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:PostalCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:AdministrativeArea"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_WFS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Phone/ows11:Voice"));
        
        return address;
    }

}
