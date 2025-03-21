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

import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.*;
import de.ingrid.utils.xml.Wcs201NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author André Wallat
 */
public class Wcs201CapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {

    private static final String XPATH_EXT_WCS_SERVICECONTACT = "/wcs201:Capabilities/ows20:ServiceProvider/ows20:ServiceContact";
    private static final String XPATH_EXP_WCS_FEES = "/wcs201:Capabilities/ows20:ServiceIdentification/ows20:Fees";
    private static final String XPATH_EXP_WCS_ACCESS_CONSTRAINTS = "/wcs201:Capabilities/ows20:ServiceIdentification/ows20:AccessConstraints";
    private static final String XPATH_EXP_WCS_ONLINE_RESOURCE = "/wcs201:Capabilities/ows20:ServiceProvider/ows20:ServiceContact/ows20:ContactInfo/ows20:OnlineResource";

    private static final String XPATH_EXP_WCS_TITLE = "/wcs201:Capabilities/ows20:ServiceIdentification[1]/ows20:Title[1]";
    private static final String XPATH_EXP_WCS_ABSTRACT = "/wcs201:Capabilities/ows20:ServiceIdentification[1]/ows20:Abstract[1]";
    private static final String XPATH_EXP_WCS_VERSION = "/wcs201:Capabilities/ows20:ServiceIdentification/ows20:ServiceTypeVersion";
    private static final String XPATH_EXP_WCS_OP_GET_CAPABILITIES_GET_HREF = "/wcs201:Capabilities/ows20:OperationsMetadata[1]/ows20:Operation[@name='GetCapabilities']/ows20:DCP[1]/ows20:HTTP[1]/ows20:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_OP_GET_CAPABILITIES_POST_HREF = "/wcs201:Capabilities/ows20:OperationsMetadata[1]/ows20:Operation[@name='GetCapabilities']/ows20:DCP[1]/ows20:HTTP[1]/ows20:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_GET_HREF = "/wcs201:Capabilities/ows20:OperationsMetadata[1]/ows20:Operation[@name='DescribeCoverage']/ows20:DCP[1]/ows20:HTTP[1]/ows20:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_POST_HREF = "/wcs201:Capabilities/ows20:OperationsMetadata[1]/ows20:Operation[@name='DescribeCoverage']/ows20:DCP[1]/ows20:HTTP[1]/ows20:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_OP_GET_COVERAGE_GET_HREF = "/wcs201:Capabilities/ows20:OperationsMetadata[1]/ows20:Operation[@name='GetCoverage']/ows20:DCP[1]/ows20:HTTP[1]/ows20:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_OP_GET_COVERAGE_POST_HREF = "/wcs201:Capabilities/ows20:OperationsMetadata[1]/ows20:Operation[@name='GetCoverage']/ows20:DCP[1]/ows20:HTTP[1]/ows20:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WCS_KEYWORDS = "/wcs201:Capabilities/ows20:ServiceIdentification/ows20:Keywords/ows20:Keyword";


    public Wcs201CapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new Wcs201NamespaceContext()), syslistCache);
    }

    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();

        // General settings
        result.setServiceType("WCS");
        result.setDataServiceType(3); // download

        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_WCS_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_WCS_ABSTRACT));
        result.setVersions(addOGCtoVersions(getNodesContentAsList(doc, XPATH_EXP_WCS_VERSION)));

        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_WCS_FEES));

        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_WCS_ACCESS_CONSTRAINTS));

        // Online Resources
        result.setOnlineResources(getOnlineResources(doc, XPATH_EXP_WCS_ONLINE_RESOURCE));

        // TODO: Resource Locator / Type
        // ...

        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_WCS_KEYWORDS);

        // Extended - Keywords
        // add found keywords to our result bean
        result.setKeywords(keywords);

        // get contact information
        result.setAddress(getAddress(doc));

        // Operation List
        List<OperationBean> operations = new ArrayList<>();


        // Operation - GetCapabilities
        OperationBean getCapabilitiesOp = mapToOperationBean(doc,
                new String[]{
                        XPATH_EXP_WCS_OP_GET_CAPABILITIES_GET_HREF,
                        XPATH_EXP_WCS_OP_GET_CAPABILITIES_POST_HREF},
                new Integer[]{ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST});
        if (!getCapabilitiesOp.getAddressList().isEmpty()) {
            getCapabilitiesOp.setName("GetCapabilities");
            getCapabilitiesOp.setMethodCall("GetCapabilities");

            List<OperationParameterBean> paramList = new ArrayList<>();
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
                        XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_POST_HREF},
                new Integer[]{ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST});
        if (!describeCoverageOp.getAddressList().isEmpty()) {
            describeCoverageOp.setName("DescribeCoverage");
            describeCoverageOp.setMethodCall("DescribeCoverage");

            List<OperationParameterBean> paramList = new ArrayList<>();
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
                        XPATH_EXP_WCS_OP_GET_COVERAGE_POST_HREF},
                new Integer[]{ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST});
        if (!getCoverageOp.getAddressList().isEmpty()) {
            getCoverageOp.setName("GetCoverage");
            getCoverageOp.setMethodCall("GetCoverage");

            List<OperationParameterBean> paramList = new ArrayList<>();
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

        List<LocationBean> union = getBoundingBoxes(doc);
        result.setBoundingBoxes( union );

        List<SpatialReferenceSystemBean> spatialReferenceSystems = getSpatialReferenceSystems( doc );
        result.setSpatialReferenceSystems( spatialReferenceSystems );
        
        return result;

    }

    /**
     * @param doc
     * @return
     */
    private AddressBean getAddress(Document doc) {
        AddressBean address = new AddressBean();
        setNameInAddressBean(address, xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:IndividualName"));
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Address/ows20:ElectronicMailAddress"));

        // try to find address in database and set the uuid if found
        searchForAddress(address);

        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Address/ows20:DeliveryPoint"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Address/ows20:City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Address/ows20:PostalCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Address/ows20:Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Address/ows20:AdministrativeArea"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_WCS_SERVICECONTACT + "/ows20:ContactInfo/ows20:Phone/ows20:Voice"));

        return address;
    }

    private List<LocationBean> getBoundingBoxes(Document doc) {
        List<LocationBean> bboxes = new ArrayList<>();
        String title = xPathUtils.getString(doc, "/wcs201:Capabilities/wcs201:Contents/wcs201:CoverageSummary/ows20:Title");
        NodeList layers = xPathUtils.getNodeList(doc, "/wcs201:Capabilities/wcs201:Contents/wcs201:CoverageSummary/ows20:WGS84BoundingBox");
        
        for (int i = 0; i < layers.getLength(); i++) {
            Node layer = layers.item(i);

            String[] lower = xPathUtils.getString( layer, "ows20:LowerCorner" ).split( " " );
            String[] upper = xPathUtils.getString( layer, "ows20:UpperCorner" ).split( " " );

            LocationBean box = new LocationBean();
            box.setLatitude1(Double.valueOf( lower[0] ));
            box.setLongitude1(Double.valueOf( lower[1] ));
            box.setLatitude2(Double.valueOf( upper[0] ));
            box.setLongitude2(Double.valueOf( upper[1] ));

            // add a fallback for the name, since it's mandatory

            box.setName(title);
            // shall be a free spatial reference, but needs an ID to check for duplications!
            box.setTopicId(box.getName());
            box.setType( MdekUtils.SpatialReferenceType.FREI.getDbValue() );

            bboxes.add(box);
        }
        return bboxes;
    }
    
    private List<SpatialReferenceSystemBean> getSpatialReferenceSystems(Document doc) {
        List<SpatialReferenceSystemBean> result = new ArrayList<>();
        String[] crs = xPathUtils.getStringArray(doc, "/wcs201:Capabilities/wcs201:ServiceMetadata/wcs201:Extension/crs:CrsMetadata/crs:crsSupported");
        List<String> uniqueCrs = new ArrayList<>();
        
        for (String item : crs) {
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

}
