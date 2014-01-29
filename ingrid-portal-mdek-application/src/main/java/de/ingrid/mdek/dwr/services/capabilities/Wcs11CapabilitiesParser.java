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
import de.ingrid.utils.xml.Wcs11NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class Wcs11CapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {
    
    private static final String XPATH_EXT_WCS_SERVICECONTACT = "/wcs11:Capabilities/ows11:ServiceProvider/ows11:ServiceContact";
    private static final String XPATH_EXP_WCS_FEES = "/wcs11:Capabilities/ows11:ServiceIdentification/ows11:Fees";
    private static final String XPATH_EXP_WCS_ACCESS_CONSTRAINTS = "/wcs11:Capabilities/ows11:ServiceIdentification/ows11:AccessConstraints";
    private static final String XPATH_EXP_WCS_ONLINE_RESOURCE = "/wcs11:Capabilities/ows11:ServiceProvider/ows11:ServiceContact/ows11:ContactInfo/ows11:OnlineResource";

    private final static String XPATH_EXP_WCS_TITLE = "/wcs11:Capabilities/ows11:ServiceIdentification[1]/ows11:Title[1]";
    private final static String XPATH_EXP_WCS_ABSTRACT = "/wcs11:Capabilities/ows11:ServiceIdentification[1]/ows11:Abstract[1]";
    private final static String XPATH_EXP_WCS_VERSION = "/wcs11:Capabilities/ows11:ServiceIdentification/ows11:ServiceTypeVersion";
    private final static String XPATH_EXP_WCS_OP_GET_CAPABILITIES_GET_HREF = "/wcs11:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCS_OP_GET_CAPABILITIES_POST_HREF = "/wcs11:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private final static String XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_GET_HREF = "/wcs11:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='DescribeCoverage']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_POST_HREF = "/wcs11:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='DescribeCoverage']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private final static String XPATH_EXP_WCS_OP_GET_COVERAGE_GET_HREF = "/wcs11:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCoverage']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private final static String XPATH_EXP_WCS_OP_GET_COVERAGE_POST_HREF = "/wcs11:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCoverage']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_KEYWORDS = "/wcs11:Capabilities/ows11:ServiceIdentification/ows11:Keywords/ows11:Keyword";

    
    public Wcs11CapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new Wcs11NamespaceContext()), syslistCache);
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();
        
        // General settings
        result.setServiceType("WCS");
        result.setDataServiceType(6);
        
        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_WCS_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_WCS_ABSTRACT));
        result.setVersions(getNodesContentAsList(doc, XPATH_EXP_WCS_VERSION));
        
        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_WCS_FEES));
        
        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_WCS_ACCESS_CONSTRAINTS));
        
        // Online Resources
        result.setOnlineResources(getOnlineResources(doc, XPATH_EXP_WCS_ONLINE_RESOURCE));

        // TODO: Resource Locator / Type
        // ...
        
        // Spatial Data Type
        //result.setDataServiceType(xPathUtils.getString(doc,  XPATH_EXP_WCS_SPATIAL_DATA_TYPE));
        
        // add Temporal References if available
        //result.addTimeReference(mapToTimeReferenceBean(doc, XPATH_EXP_WCS_DATE_CREATED));
        //result.addTimeReference(mapToTimeReferenceBean(doc, XPATH_EXP_WCS_DATE_PUBLISHED));
        //result.addTimeReference(mapToTimeReferenceBean(doc, XPATH_EXP_WCS_DATE_LAST_REVISION));
        
        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_WCS_KEYWORDS);
        
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
                new String[]{
                    XPATH_EXP_WCS_OP_GET_CAPABILITIES_GET_HREF,
                    XPATH_EXP_WCS_OP_GET_CAPABILITIES_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getCapabilitiesOp.getAddressList().isEmpty()) {
            getCapabilitiesOp.setName("GetCapabilities");
            getCapabilitiesOp.setMethodCall("GetCapabilities");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("SERVICE=WCS", "Service type", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
            paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
            getCapabilitiesOp.setParamList(paramList);
            operations.add(getCapabilitiesOp);
        }

        // Operation - DescribeCoverage
        OperationBean describeCoverageOp = mapToOperationBean(doc,
                new String[]{
                    XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_GET_HREF,
                    XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!describeCoverageOp.getAddressList().isEmpty()) {
            describeCoverageOp.setName("DescribeCoverage");
            describeCoverageOp.setMethodCall("DescribeCoverage");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCS", "Service name. Shall be WCS", "", false, false));
            paramList.add(new OperationParameterBean("request=DescribeCoverage", "Request name. Shall be DescribeCoverage", "", false, false));
            paramList.add(new OperationParameterBean("version=1.1.2", "Request protocol version", "", false, false));
            paramList.add(new OperationParameterBean("identifiers=identifier1, identifier2, ...", "A comma-separated list of coverage identifiers to describe (identified by their identifier values in the Capabilities document)", "", false, false));
            describeCoverageOp.setParamList(paramList);
            operations.add(describeCoverageOp);
        }
        
        // Operation - GetCoverage
        OperationBean getCoverageOp = mapToOperationBean(doc,
                new String[]{
                    XPATH_EXP_WCS_OP_GET_COVERAGE_GET_HREF,
                    XPATH_EXP_WCS_OP_GET_COVERAGE_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getCoverageOp.getAddressList().isEmpty()) {
            getCoverageOp.setName("GetCoverage");
            getCoverageOp.setMethodCall("GetCoverage");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=WCS", "Service name. Shall be WCS", "", false, false));
            paramList.add(new OperationParameterBean("request=DescribeCoverage", "Request name. Shall be GetCoverage", "", false, false));
            paramList.add(new OperationParameterBean("version=1.1.2", "Request protocol version", "", false, false));
            paramList.add(new OperationParameterBean("identifier=identifier", "Unique identifier of an available coverage", "", false, false));
            paramList.add(new OperationParameterBean("BoundingBox=47,-71, - 51,66, urn:ogc:def:crs:EPSG:6.6:63266405", "Request a coverage subset defined by the specified bounding box in the referenced coordinate reference system", "", false, false));
            paramList.add(new OperationParameterBean("TimeSequence= 20060801, 20060811, ... OR TimeSequence = 20060801/ 2006-0901 / P1D, ...", "Request a subset corresponding to the specified time instants or intervals, expressed in the extended ISO 8601 syntax defined in Annex D of [OGC 06-042]. (See 9.3.2.4.)", "", true, false));
            paramList.add(new OperationParameterBean("RangeSubset=temp:nearest; radiance[band[1,2,5]]", "Request only some fields OR subsets of some fields (OR list fields to request non-default interpolation on them).", "", true, false));
            paramList.add(new OperationParameterBean("format=image/netcdf", "Requested output format of Coverage. Shall be one of those listed in the description of the selected coverage", "", false, false));
            paramList.add(new OperationParameterBean("store=true", "Specifies whether response coverage should be stored, remotely from client at network URL, a boolean value", "", true, false));
            paramList.add(new OperationParameterBean("GridBaseCRS=urn:ogc:def:crs:EPSG:6.6:32618", "Reference to GridBaseCRS of desired output GridCRS, a URN", "", true, false));
            paramList.add(new OperationParameterBean("GridType=urn:ogc:def:method:WCS:1.1:2dGridIn2dCrs", "Reference to grid type of desired output GridCRS, a URN", "", true, false));
            paramList.add(new OperationParameterBean("GridCS=urn:ogc:def:cs:OGC:0.0:Grid2dSquareCS", "Reference to coordinate system of desired output GridCRS, a URN", "", true, false));
            paramList.add(new OperationParameterBean("GridOrigin=0,0", "Position coordinates of one possible grid origin, in GridBaseCRS of desired output GridCRS", "", true, false));
            paramList.add(new OperationParameterBean("GridOffsets=0.0707, -0.0707,0.1414,0.1414&", "Offsets between adjacent grid points, in GridBaseCRS of desired output GridCRS", "", true, false));
            getCoverageOp.setParamList(paramList);
            operations.add(getCoverageOp);
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
        String[] name = extractName(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:IndividualName"));
        if (name != null) {
            address.setFirstname(name[0].trim());
            address.setLastname(name[1].trim());
        } else {
            address.setLastname("N/A");
        }
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:ElectronicMailAddress"));
        
        // try to find address in database and set the uuid if found
        searchForAddress(address);
        
        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:DeliveryPoint"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:PostalCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:AdministrativeArea"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Phone/ows11:Voice"));
        
        return address;
    }

}
