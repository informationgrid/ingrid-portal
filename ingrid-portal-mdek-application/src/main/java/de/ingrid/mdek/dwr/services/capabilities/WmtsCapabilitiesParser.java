/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.*;
import de.ingrid.utils.xml.WmtsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;
import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author André Wallat
 *
 */
public class WmtsCapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {

    private static final String XPATH_EXP_WMTS_FEES = "/wmts:Capabilities/ows11:ServiceIdentification/ows11:Fees";
    private static final String XPATH_EXP_WMTS_ACCESS_CONSTRAINTS = "/wmts:Capabilities/ows11:ServiceIdentification/ows11:AccessConstraints";
    private static final String XPATH_EXP_WMTS_KEYWORDS = "/wmts:Capabilities/ows11:ServiceIdentification/ows11:Keywords/ows11:Keyword";

    private static final String XPATH_EXP_WMTS_TITLE = "/wmts:Capabilities/ows11:ServiceIdentification[1]/ows11:Title[1]";
    private static final String XPATH_EXP_WMTS_ABSTRACT = "/wmts:Capabilities/ows11:ServiceIdentification[1]/ows11:Abstract[1]";
    private static final String XPATH_EXP_WMTS_VERSION = "/wmts:Capabilities/@version";

    private static final String XPATH_EXP_WMTS_OP_GET_CAPABILITIES_GET_HREF1 = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";
    private static final String XPATH_EXP_WMTS_OP_GET_CAPABILITIES_GET_HREF2 = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[2]/@xlink:href";
    private static final String XPATH_EXP_WMTS_OP_GET_CAPABILITIES_GET_HREF3 = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[3]/@xlink:href";
    private static final String XPATH_EXP_WMTS_OP_GET_CAPABILITIES_POST_HREF1 = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[1]/@xlink:href";
    private static final String XPATH_EXP_WMTS_OP_GET_CAPABILITIES_POST_HREF2 = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[2]/@xlink:href";
    private static final String XPATH_EXP_WMTS_OP_GET_CAPABILITIES_POST_HREF3 = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetCapabilities']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Post[3]/@xlink:href";

    private static final String XPATH_EXP_WMTS_OP_GET_FEATURE_INFO_HREF = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetFeatureInfo']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";

    private static final String XPATH_EXP_WMTS_OP_GET_TILE_HREF = "/wmts:Capabilities/ows11:OperationsMetadata[1]/ows11:Operation[@name='GetTile']/ows11:DCP[1]/ows11:HTTP[1]/ows11:Get[1]/@xlink:href";

    private static final String XPATH_EXT_WMTS_SERVICECONTACT = "/wmts:Capabilities/ows11:ServiceProvider/ows11:ServiceContact";

    private Map<String, Integer> versionSyslistMap;

    public WmtsCapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new WmtsNamespaceContext()), syslistCache);
        
        versionSyslistMap = new HashMap<>();
        versionSyslistMap.put( "1.0.0", 3 );
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();
        
        // General settings
        result.setServiceType("WMTS");
        result.setDataServiceType(2); // Darstellungsdienst
        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_WMTS_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_WMTS_ABSTRACT));
        
        List<String> versionList = getNodesContentAsList(doc, XPATH_EXP_WMTS_VERSION);
        List<String> mappedVersionList = mapVersionsFromCodelist(MdekSysList.OBJ_SERV_VERSION_WMS.getDbValue(), versionList, versionSyslistMap);
        result.setVersions(mappedVersionList);

        String version = versionList.get(0);
        
        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_WMTS_FEES));
        
        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_WMTS_ACCESS_CONSTRAINTS));
        
        // TODO: Resource Locator / Type
        // ...
        
        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_WMTS_KEYWORDS);
        
        // Extended - Keywords
        // add found keywords to our result bean
        result.setKeywords(keywords);
        
        // get contact information
        result.setAddress(getAddress(doc));
        
        // Operation List
        List<OperationBean> operations = new ArrayList<>();

        // Spatial reference
        List<LocationBean> boundingBoxesFromLayers = getBoundingBoxesFromLayers(doc);
        result.setBoundingBoxes( boundingBoxesFromLayers );

        // Spatial reference system
        List<SpatialReferenceSystemBean> spatialReferenceSystems = getSpatialReferenceSystems(doc);
        result.setSpatialReferenceSystems(spatialReferenceSystems);

        // Operation - GetCapabilities
        OperationBean getCapabilitiesOp = mapToOperationBean(doc,
                new String[]{
                        XPATH_EXP_WMTS_OP_GET_CAPABILITIES_GET_HREF1,
                        XPATH_EXP_WMTS_OP_GET_CAPABILITIES_GET_HREF2,
                        XPATH_EXP_WMTS_OP_GET_CAPABILITIES_GET_HREF3,
                        XPATH_EXP_WMTS_OP_GET_CAPABILITIES_POST_HREF1,
                        XPATH_EXP_WMTS_OP_GET_CAPABILITIES_POST_HREF2,
                        XPATH_EXP_WMTS_OP_GET_CAPABILITIES_POST_HREF3
                },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_GET,
                        ID_OP_PLATFORM_HTTP_POST, ID_OP_PLATFORM_HTTP_POST, ID_OP_PLATFORM_HTTP_POST });
        if (!getCapabilitiesOp.getAddressList().isEmpty()) {
            getCapabilitiesOp.setName("GetCapabilities");
            getCapabilitiesOp.setMethodCall("GetCapabilities");
    
            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("SERVICE=WMTS", "Service type", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
            paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
            getCapabilitiesOp.setParamList(paramList);
            operations.add(getCapabilitiesOp);
        }

        // Operation - GetTile
        OperationBean getTileOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_WMTS_OP_GET_TILE_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET });
        if (!getTileOp.getAddressList().isEmpty()) {
            getTileOp.setName("GetTile");
            getTileOp.setMethodCall("GetTile");

            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("SERVICE=WMTS", "Service type", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetTile", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("VERSION=1.0.0", "", "", true, false));
            paramList.add(new OperationParameterBean("Layer", "The layers available from the Online Catalogs; if more than one layer is requested they are in a comma-separated list. Available layers are advertised in the GetCapabilities response.", "", true, false));
            paramList.add(new OperationParameterBean("Style=default", "Some layers can be rendered in different ways; check the capabilities document for allowed values on a layer-by-layer basis.", "", true, false));
            paramList.add(new OperationParameterBean("Format=image/png", "The tile format to return.", "", true, false));
            paramList.add(new OperationParameterBean("TileMatrixSet=EPSG:3857", "The Tile Matrix Set to be used to generate the response", "", true, false));
            paramList.add(new OperationParameterBean("TileMatrix=EPSG:3857", "The Tile Matrix identifier of the tileMatrix in the tileMatrixSet requested that has the desired scale denominator that you want to request. 4326 is WGS84, that is, uses latitude/longitude, while 3857 provides tiles in the spherical mercator projection", "", true, false));
            paramList.add(new OperationParameterBean("TileRow=X", "The Row location of the tile in the defined tileMatrixSet. The value must be in the valid range provided in the capabilities response.", "", true, false));
            paramList.add(new OperationParameterBean("TileCol=Y", "The Column location of the tile in the defined tileMatrixSet. The value must be in the valid range provided in the capabilities response.", "", true, false));
            getTileOp.setParamList(paramList);
            operations.add(getTileOp);
        }

        // Operation - GetFeatureInfo - optional
        String getFeatureInfoAddress = xPathUtils.getString(doc,  XPATH_EXP_WMTS_OP_GET_FEATURE_INFO_HREF);
        if (getFeatureInfoAddress != null && getFeatureInfoAddress.length() != 0) {
            OperationBean getFeatureInfoOp = new OperationBean();
            getFeatureInfoOp.setName("GetFeatureInfo");
            getFeatureInfoOp.setMethodCall("GetFeatureInfo");
            List<Integer> getFeatureInfoOpPlatform = new ArrayList<>();
            getFeatureInfoOpPlatform.add(ID_OP_PLATFORM_HTTP_GET);
            getFeatureInfoOp.setPlatform(getFeatureInfoOpPlatform);
            List<String> getFeatureInfoOpAddressList = new ArrayList<>();
            getFeatureInfoOpAddressList.add(getFeatureInfoAddress);
            getFeatureInfoOp.setAddressList(getFeatureInfoOpAddressList);

            List<OperationParameterBean> paramList = new ArrayList<>();
            paramList.add(new OperationParameterBean("VERSION="+version, "Request version", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetFeatureInfo", "Request name", "", false, false));
            paramList.add(new OperationParameterBean("(map_request_copy)", "Partial copy of the Map request parameters that generated the map for which information is desired", "", false, false));
            paramList.add(new OperationParameterBean("QUERY_LAYERS=layer_list", "Comma-separated list of one or more layers to be queried", "", false, false));
            paramList.add(new OperationParameterBean("INFO_FORMAT=output_format", "Return format of feature information (MIME type)", "", true, false));
            paramList.add(new OperationParameterBean("FEATURE_COUNT=number", "Number of features about which to return information (default=1)", "", true, false));
            paramList.add(new OperationParameterBean("X=pixel_column", "X coordinate in pixels of feature (measured from upper left corner=0)", "", false, false));
            paramList.add(new OperationParameterBean("Y=pixel_row", "Y coordinate in pixels of feature (measured from upper left corner=0)", "", false, false));
            paramList.add(new OperationParameterBean("EXCEPTIONS=exception_format", "The format in which exceptions are to be reported by the WMS (default=application/vnd.ogc.se_xml)", "", true, false));
            paramList.add(new OperationParameterBean("Vendor-specific parameters", "Optional experimental parameters", "", true, false));

            getFeatureInfoOp.setParamList(paramList);
            operations.add(getFeatureInfoOp);
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
        setNameInAddressBean(address, xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:IndividualName"));
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:ElectronicMailAddress"));
        
        // try to find address in database and set the uuid if found
        searchForAddress(address);
        
        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:DeliveryPoint"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:PostalCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Address/ows11:AdministrativeArea"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_WMTS_SERVICECONTACT + "/ows11:ContactInfo/ows11:Phone/ows11:Voice"));
        
        return address;
    }

    private List<LocationBean> getBoundingBoxesFromLayers(Document doc) {
        List<LocationBean> bboxes = new ArrayList<>();
        NodeList layers = xPathUtils.getNodeList(doc, "/wmts:Capabilities/wmts:Contents/wmts:Layer");
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
            String title = xPathUtils.getString(layer, "ows11:Title");

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
        String[] crs = xPathUtils.getStringArray(doc, "/wmts:Capabilities/wmts:Contents/wmts:TileMatrixSet/ows11:SupportedCRS");

        List<String> uniqueCrs = new ArrayList<>();

        // check codelists for matching entryIds
        for (String item : crs) {
            SpatialReferenceSystemBean srsBean = new SpatialReferenceSystemBean();

            String[] splittedItem = item.split(":");
            Integer itemId = Integer.valueOf(splittedItem[splittedItem.length-1]);

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
