/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.OperationParameterBean;
import de.ingrid.utils.xml.WctsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class WctsCapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {
    
    private static final String XPATH_EXP_WCTS_FEES = "/wcts:Capabilities/owsgeo:ServiceIdentification/owsgeo:Fees";
    private static final String XPATH_EXP_WCTS_ACCESS_CONSTRAINTS = "/wcts:Capabilities/owsgeo:ServiceIdentification/owsgeo:AccessConstraints";
    //private static final String XPATH_EXP_WCTS_ONLINE_RESOURCE = "/wcts:";
    private static final String XPATH_EXP_WCTS_KEYWORDS = "/wcts:Capabilities/owsgeo:ServiceIdentification/owsgeo:Keywords/owsgeo:Keyword";

    private final static String XPATH_EXP_WCTS_TITLE = "/wcts:Capabilities/owsgeo:ServiceIdentification[1]/owsgeo:Title[1]";
    private final static String XPATH_EXP_WCTS_ABSTRACT = "/wcts:Capabilities/owsgeo:ServiceIdentification[1]/owsgeo:Abstract[1]";
    private final static String XPATH_EXP_WCTS_VERSION = "/wcts:Capabilities/@version";

    private final static String XPATH_EXP_WCTS_OP_GET_CAPABILITIES_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='GetCapabilities']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_GET_CAPABILITIES_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='GetCapabilities']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";
    
    private final static String XPATH_EXP_WCTS_OP_TRANSFORM_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='Transform']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_TRANSFORM_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='Transform']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";

    private final static String XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='IsTransformable']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='IsTransformable']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";

    private final static String XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='GetTransformation']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='GetTransformation']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";

    private final static String XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='DescribeTransformation']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='DescribeTransformation']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";

    private final static String XPATH_EXP_WCTS_OP_DESCRIBE_CRS_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='DescribeCRS']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_DESCRIBE_CRS_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='DescribeCRS']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";

    private final static String XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_GET_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='DescribeMethod']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_POST_HREF = "/wcts:Capabilities/owsgeo:OperationsMetadata[1]/owsgeo:Operation[@name='DescribeMethod']/owsgeo:DCP[1]/owsgeo:HTTP[1]/owsgeo:Post[1]/@xlink:href";
    private static final String XPATH_EXT_WCTS_SERVICECONTACT = "/wcts:Capabilities/owsgeo:ServiceProvider/owsgeo:ServiceContact";

    
    public WctsCapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new WctsNamespaceContext()), syslistCache);
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();
        
        // General settings
        result.setServiceType("WCTS");
        result.setDataServiceType(4); // transformation
        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_WCTS_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_WCTS_ABSTRACT));
        result.setVersions(getNodesContentAsList(doc, XPATH_EXP_WCTS_VERSION));
        
        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_WCTS_FEES));
        
        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_WCTS_ACCESS_CONSTRAINTS));
        
        // Online Resources
        //result.setOnlineResources(getOnlineResources(doc, XPATH_EXP_WCTS_ONLINE_RESOURCE));

        // TODO: Resource Locator / Type
        // ...
        
        // Spatial Data Type
        //result.setDataServiceType(xPathUtils.getString(doc,  XPATH_EXP_WCS_SPATIAL_DATA_TYPE));
        
        // add Temporal References if available
        //result.addTimeReference(mapToTimeReferenceBean(doc, XPATH_EXP_WCS_DATE_CREATED));
        //result.addTimeReference(mapToTimeReferenceBean(doc, XPATH_EXP_WCS_DATE_PUBLISHED));
        //result.addTimeReference(mapToTimeReferenceBean(doc, XPATH_EXP_WCS_DATE_LAST_REVISION));
        
        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_WCTS_KEYWORDS);
        
        // Extended - Keywords
        //String[] extKeywords = xPathUtils.getStringArray(doc, XPATH_EXP_CSW_KEYWORDS_EXTENDED);
        //keywords.addAll(Arrays.asList(extKeywords));
        // add found keywords to our result bean
        result.setKeywords(keywords);
        
        // get contact information
        result.setAddress(getAddress(doc));
        
        // Operation List
        List<OperationBean> operations = new ArrayList<OperationBean>();

        // Operation - GetCapabilities
        OperationBean getCapabilitiesOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WCTS_OP_GET_CAPABILITIES_GET_HREF, XPATH_EXP_WCTS_OP_GET_CAPABILITIES_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getCapabilitiesOp.getAddressList().isEmpty()) {
            getCapabilitiesOp.setName("GetCapabilities");
            getCapabilitiesOp.setMethodCall("GetCapabilities");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("SERVICE=WCTS", "Service type", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
            paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
            getCapabilitiesOp.setParamList(paramList);
            operations.add(getCapabilitiesOp);
        }

        // Operation - Transform
        OperationBean transformOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WCTS_OP_TRANSFORM_GET_HREF, XPATH_EXP_WCTS_OP_TRANSFORM_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!transformOp.getAddressList().isEmpty()) {
            transformOp.setName("Transform");
            transformOp.setMethodCall("Transform");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
            paramList.add(new OperationParameterBean("request=Transform", "Operation name", "", false, false));
            paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
            paramList.add(new OperationParameterBean("InputData=TBD", "Data to be transformed encoded in a format supported by the WCTS, either encoded inline as GML or referenced using a URL", "", false, false));
            paramList.add(new OperationParameterBean("SourceCRS=urn:ogc:def:crs:EPSG:6.3:4326", "Identifier URI of input coordinate reference system", "", true, false));
            paramList.add(new OperationParameterBean("TargetCRS=urn:ogc:def:crs:EPSG:6.3:32611", "Identifier URI of desired output coordinate reference system", "", true, false));
            paramList.add(new OperationParameterBean("Transformation=urn:ogc:def:coordinateOperation:EPSG:6.3:TBD", "Identifier URI of desired coordinate operation", "", true, false));
            paramList.add(new OperationParameterBean("InterpolationMethod=bilinear", "Identifier of interpolation method which should be used to transform a coverage", "", true, false));
            paramList.add(new OperationParameterBean("OutputFormat=TBD", "Identifier of output format to be used for the transformed features or coverage", "", true, false));
            paramList.add(new OperationParameterBean("store=true", "Boolean (true and false values) used to indicate if the transformed data needs to be stored on a remote resource or returned directly in the response. By default, it is set to false. (return directly in response)", "", true, false));
            transformOp.setParamList(paramList);
            operations.add(transformOp);
        }


        // Operation - IsTransformable
        OperationBean isTransformableOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_GET_HREF, XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!isTransformableOp.getAddressList().isEmpty()) {
            isTransformableOp.setName("IsTransformable");
            isTransformableOp.setMethodCall("IsTransformable");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
            paramList.add(new OperationParameterBean("request=IsTransformable", "Operation name", "", false, false));
            paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
            paramList.add(new OperationParameterBean("SourceCRS=urn:ogc:def:crs:EPSG:6.3:4326", "Identifier URI of input coordinate reference system", "", true, false));
            paramList.add(new OperationParameterBean("TargetCRS=urn:ogc:def:crs:EPSG:6.3:32611", "Identifier URI of desired output coordinate reference system", "", true, false));
            paramList.add(new OperationParameterBean("Transformation=urn:ogc:def:coordinateOperation:EPSG:6.3:TBD", "Identifier URI of desired coordinate operation", "", true, false));
            paramList.add(new OperationParameterBean("Method=urn:ogc:def:method:EPSG:6.3:TBD", "Identifier URI of operation method to be used in user-defined coordinate transformation", "", true, false));
            paramList.add(new OperationParameterBean("GeometryTypes=TBD", "Unordered list of types of GML 3 geometric primitives that will be requested to be transformed, separated by commas", "", true, false));
            paramList.add(new OperationParameterBean("CoverageTypes=TBD", "Unordered list of coverage types that will be requested to be transformed, separated by commas", "", true, false));
            paramList.add(new OperationParameterBean("InterpolationMethods=bilinear", "Unordered list of interpolation methods which could be used to transform coverages, separated by commas", "", true, false));
            isTransformableOp.setParamList(paramList);
            operations.add(isTransformableOp);
        }

        // Operation - GetTransformation
        OperationBean getTransformationOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_GET_HREF, XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getTransformationOp.getAddressList().isEmpty()) {
            getTransformationOp.setName("GetTransformation");
            getTransformationOp.setMethodCall("GetTransformation");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
            paramList.add(new OperationParameterBean("request=GetTransformation", "Operation name", "", false, false));
            paramList.add(new OperationParameterBean("version=0.0.20", "Specification and schema version for this operation", "", false, false));
            paramList.add(new OperationParameterBean("SourceCRS=urn:ogc:def:crs:EPSG:6.3:4326", "Identifier URI of input coordinate reference system", "", false, false));
            paramList.add(new OperationParameterBean("TargetCRS=urn:ogc:def:crs:EPSG:6.3:32611", "Identifier URI of desired output coordinate reference system", "", false, false));
            getTransformationOp.setParamList(paramList);
            operations.add(getTransformationOp);
        }

        // Operation - DescribeTransformation
        OperationBean describeTransformationOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_GET_HREF, XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!describeTransformationOp.getAddressList().isEmpty()) {
            describeTransformationOp.setName("DescribeTransformation");
            describeTransformationOp.setMethodCall("DescribeTransformation");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
            paramList.add(new OperationParameterBean("request=DescribeTransformation", "Operation name", "", false, false));
            paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
            paramList.add(new OperationParameterBean("Transformations=urn:ogc:def:coordinateOperation:EPSG:6.3:19916", "Identifier URIs of one or more coordinate operations, comma-separated list", "", false, false));
            describeTransformationOp.setParamList(paramList);
            operations.add(describeTransformationOp);
        }
        
        // Operation - DescribeCRS
        OperationBean describeCRSOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WCTS_OP_DESCRIBE_CRS_GET_HREF, XPATH_EXP_WCTS_OP_DESCRIBE_CRS_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!describeCRSOp.getAddressList().isEmpty()) {
            describeCRSOp.setName("DescribeCRS");
            describeCRSOp.setMethodCall("DescribeCRS");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
            paramList.add(new OperationParameterBean("request=DescribeCRS", "Operation name", "", false, false));
            paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
            paramList.add(new OperationParameterBean("CRSs=urn:ogc:def:crs:EPSG:6.3:4277", "Identifier URIs of one or more desired coordinate reference systems, comma separated list", "", false, false));
            describeCRSOp.setParamList(paramList);
            operations.add(describeCRSOp);
        }
        
        // Operation - DescribeMethod
        OperationBean describeMethodOp = mapToOperationBean(doc,
            new String[]{ XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_GET_HREF, XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_POST_HREF },
            new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!describeMethodOp.getAddressList().isEmpty()) {
            describeMethodOp.setName("DescribeMethod");
            describeMethodOp.setMethodCall("DescribeMethod");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
            paramList.add(new OperationParameterBean("request=DescribeMethod", "Operation name", "", false, false));
            paramList.add(new OperationParameterBean("version=0.0.20", "Specification and schema version for this operation", "", false, false));
            paramList.add(new OperationParameterBean("methods=urn:ogc:def:method:EPSG:6.3:9807", "Identifier URIs of one or more desired operation methods, comma separated", "", false, false));
            describeMethodOp.setParamList(paramList);
            operations.add(describeMethodOp);
        }
        
        result.setOperations(operations);
        return result;
        
    }

    /**
     * @param doc
     * @return
     */
    private AddressBean getAddress(Document doc) {
        AddressBean address = new AddressBean();
        String[] name = extractName(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:IndividualName"));
        if (name != null) {
            address.setFirstname(name[0].trim());
            address.setLastname(name[1].trim());
        } else {
            address.setLastname("N/A");
        }
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Address/owsgeo:ElectronicMailAddress"));
        
        // try to find address in database and set the uuid if found
        searchForAddress(address);
        
        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Address/owsgeo:DeliveryPoint"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Address/owsgeo:City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Address/owsgeo:PostalCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Address/owsgeo:Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Address/owsgeo:AdministrativeArea"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_WCTS_SERVICECONTACT + "/owsgeo:ContactInfo/owsgeo:Phone/owsgeo:Voice"));
        
        return address;
    }

}
