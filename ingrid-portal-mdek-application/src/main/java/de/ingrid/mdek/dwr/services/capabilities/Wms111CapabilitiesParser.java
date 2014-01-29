/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.geo.utils.transformation.CoordTransformUtil;
import de.ingrid.geo.utils.transformation.CoordTransformUtil.CoordType;
import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.object.ObjectReferenceBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.OperationParameterBean;
import de.ingrid.mdek.beans.object.SpatialReferenceSystemBean;
import de.ingrid.utils.xml.Wms130NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class Wms111CapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {
    
    /**
     * 
     */
    private static final String XPATH_EXP_WMS_IDENTIFIER = "/WMT_MS_Capabilities/Capability/Layer//Identifier";
    // Version 1.3.0 of the WMS uses 'WMS_Capabilities' as its root element (OGC 06-042, Chapter 7.2.4.1)
    // Version 1.1.1 uses 'WMT_MS_Capabilities'
    private final static String XPATH_EXP_WMS_1_1_1_TITLE = "/WMT_MS_Capabilities/Service[1]/Title[1]";
    private final static String XPATH_EXP_WMS_1_1_1_ABSTRACT = "/WMT_MS_Capabilities/Service[1]/Abstract[1]";
    private final static String XPATH_EXP_WMS_1_1_1_VERSION = "/WMT_MS_Capabilities/@version";
    private final static String XPATH_EXP_WMS_1_1_1_OP_GET_CAPABILITIES_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetCapabilities[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@xlink:href";
    private final static String XPATH_EXP_WMS_1_1_1_OP_GET_MAP_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetMap[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@xlink:href";
    private final static String XPATH_EXP_WMS_1_1_1_OP_GET_FEATURE_INFO_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetFeatureInfo[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@xlink:href";

    private static final String XPATH_EXP_WMS_FEES = "WMT_MS_Capabilities/Service/Fees";
    private static final String XPATH_EXP_WMS_ACCESS_CONSTRAINTS = "/WMT_MS_Capabilities/Service/AccessConstraints";
    private static final String XPATH_EXP_WMS_ONLINE_RESOURCE = "/WMT_MS_Capabilities/Service/OnlineResource";
    private static final String XPATH_EXP_WMS_KEYWORDS_LAYER = "/WMT_MS_Capabilities/Capability/Layer/Layer/Layer/KeywordList/Keyword";
    private static final String XPATH_EXT_WMS_CONTACTINFORMATION = "/WMT_MS_Capabilities/Service/ContactInformation";
    private static final String XPATH_EXP_WMS_LAYER_CRS = "/WMT_MS_Capabilities/Capability/Layer/Layer/SRS";
    private static final String XPATH_EXP_WMS_KEYWORDS = "/WMT_MS_Capabilities/Service/KeywordList/Keyword";
    
    public Wms111CapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new Wms130NamespaceContext()), syslistCache);
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();
        
        // General settings
        result.setServiceType("WMS");
        result.setDataServiceType(2); // view
        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_WMS_1_1_1_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_WMS_1_1_1_ABSTRACT));
        result.setVersions(getNodesContentAsList(doc, XPATH_EXP_WMS_1_1_1_VERSION));
        String version = result.getVersions().get(0);
        
        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_WMS_FEES));
        
        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_WMS_ACCESS_CONSTRAINTS));
        
        // Online Resources
        result.setOnlineResources(getOnlineResources(doc, XPATH_EXP_WMS_ONLINE_RESOURCE));

        // TODO: Extended Capabilities?
        
        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_WMS_KEYWORDS);
        List<String> layerKeywords = getKeywords(doc, XPATH_EXP_WMS_KEYWORDS_LAYER);
        keywords.addAll(layerKeywords);
        
        // Extended - Keywords
        //String[] extKeywords = xPathUtils.getStringArray(doc, "/csw:Capabilities/ows:OperationsMetadata/inspire_ds:ExtendedCapabilities/inspire_common:Keyword/inspire_common:KeywordValue");
        //keywords.addAll(Arrays.asList(extKeywords));
        // add found keywords to our result bean
        result.setKeywords(keywords);
        
        // get bounding boxes of each layer and create a union
        List<LocationBean> boundingBoxesFromLayers = getBoundingBoxesFromLayers(doc);
        LocationBean unionOfBoundingBoxes = null; 
        if ( !boundingBoxesFromLayers.isEmpty() ) {
            unionOfBoundingBoxes = getUnionOfBoundingBoxes(boundingBoxesFromLayers);
            unionOfBoundingBoxes.setName("Raumbezug von: " + result.getTitle());
            List<LocationBean> union = new ArrayList<LocationBean>();
            union.add(unionOfBoundingBoxes);
            result.setBoundingBoxes(union);            
        }
        
        // Coupled Resources
        //String[] references = xPathUtils.getStringArray(doc, XPATH_EXP_WMS_IDENTIFIER);
        
        // Coupled Resources
        NodeList identifierNodes = xPathUtils.getNodeList(doc, "/WMT_MS_Capabilities/Capability/Layer//Identifier");
        List<MdekDataBean> coupledResources = new ArrayList<MdekDataBean>();
        for ( int i = 0; i < identifierNodes.getLength(); i++ ) {
            String id = identifierNodes.item(i).getTextContent();
            // check for the found IDs if a metadata with this resource identifier exists
            MdekDataBean coupledResource = checkForCoupledResource(id);
            // the dataset does not exist yet
            if (coupledResource == null) {
                MdekDataBean newDataset = new MdekDataBean();
                Node layerNode = xPathUtils.getNode( identifierNodes.item(i), ".." );
                newDataset.setUuid(null);
                newDataset.setRef1ObjectIdentifier(id);
                newDataset.setTitle( xPathUtils.getString( layerNode, "Title" ) );
                newDataset.setThesaurusTermsTable( getKeywordsFromLayer( getKeywords(layerNode, "KeywordList/Keyword") ) );
                List<LocationBean> boxes = new ArrayList<LocationBean>();
                LocationBean box = getBoundingBoxFromLayer( layerNode );
                if ( box != null ) boxes.add( box );
                else if ( unionOfBoundingBoxes != null ) boxes.add( unionOfBoundingBoxes );
                newDataset.setSpatialRefLocationTable( boxes );
                coupledResources.add( newDataset );
                
            } else {
                coupledResources.add( coupledResource );
            }
        }
        result.setCoupledResources(coupledResources);
        
        // Spatial Reference Systems (SRS / CRS)
        // Note: The root <Layer> element shall include a sequence of zero or more
        // CRS elements listing all CRSs that are common to all subsidiary layers.
        // see: 7.2.4.6.7 CRS (WMS Implementation Specification, page 26)
        
        // get all root Layer coordinate Reference Systems
        // there only can be one root layer!
        result.setSpatialReferenceSystems(getSpatialReferenceSystems(doc));
        
        // get contact information
        result.setAddress(getAddress(doc));
        
        
        // Conformity
        // result.setConformities(mapToConformityBeans(doc, XPATH_EXP_CSW_CONFORMITY));

        // Operation List
        List<OperationBean> operations = new ArrayList<OperationBean>();

        // Operation - GetCapabilities
        OperationBean getCapabilitiesOp = new OperationBean();
        getCapabilitiesOp.setName("GetCapabilities");
        getCapabilitiesOp.setMethodCall("GetCapabilities");
        List<Integer> getCapabilitiesOpPlatform = new ArrayList<Integer>();
        getCapabilitiesOpPlatform.add(ID_OP_PLATFORM_HTTP_GET);
        getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
        List<String> getCapabilitiesOpAddressList = new ArrayList<String>();
        String address = xPathUtils.getString(doc, XPATH_EXP_WMS_1_1_1_OP_GET_CAPABILITIES_HREF);
        getCapabilitiesOpAddressList.add(address);
        getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

        List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
        paramList.add(new OperationParameterBean("VERSION="+version, "Request version", "", true, false));
        paramList.add(new OperationParameterBean("SERVICE=WMS", "Service type", "", false, false));
        paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Request name", "", false, false));
        paramList.add(new OperationParameterBean("UPDATESEQUENCE=number", "Sequence number for cache control", "", true, false));
        getCapabilitiesOp.setParamList(paramList);
        operations.add(getCapabilitiesOp);

        // Operation - GetMap
        OperationBean getMapOp = new OperationBean();
        getMapOp.setName("GetMap");
        getMapOp.setMethodCall("GetMap");
        List<Integer> getMapOpPlatform = new ArrayList<Integer>();
        getMapOpPlatform.add(ID_OP_PLATFORM_HTTP_GET);
        getMapOp.setPlatform(getMapOpPlatform);
        List<String> getMapOpAddressList = new ArrayList<String>();
        getMapOpAddressList.add(xPathUtils.getString(doc, XPATH_EXP_WMS_1_1_1_OP_GET_MAP_HREF));
        getMapOp.setAddressList(getMapOpAddressList);

        paramList = new ArrayList<OperationParameterBean>();
        paramList.add(new OperationParameterBean("VERSION="+version, "Request version", "", false, false));
        paramList.add(new OperationParameterBean("REQUEST=GetMap", "Request name", "", false, false));
        paramList.add(new OperationParameterBean("LAYERS=layer_list", "Comma-separated list of one or more map layers. Optional if SLD parameter is present", "", false, false));
        paramList.add(new OperationParameterBean("STYLES=style_list", "Comma-separated list of one rendering style per requested layer. Optional if SLD parameter is present", "", false, false));
        paramList.add(new OperationParameterBean("SRS=namespace:identifier", "Spatial Reference System", "", false, false));
        paramList.add(new OperationParameterBean("BBOX=minx,miny,maxx,maxy", "Bounding box corners (lower left, upper right) in SRS units", "", false, false));
        paramList.add(new OperationParameterBean("WIDTH=output_width", "Width in pixels of map picture", "", false, false));
        paramList.add(new OperationParameterBean("HEIGHT=output_height", "Height in pixels of map picture", "", false, false));
        paramList.add(new OperationParameterBean("FORMAT=output_format", "Output format of map", "", false, false));
        paramList.add(new OperationParameterBean("TRANSPARENT=TRUE|FALSE", "Background transparency of map (default=FALSE)", "", true, false));
        paramList.add(new OperationParameterBean("BGCOLOR=color_value", "Hexadecimal red-green-blue color value for the background color (default=0xFFFFFF)", "", true, false));
        paramList.add(new OperationParameterBean("EXCEPTIONS=exception_format", "The format in which exceptions are to be reported by the WMS (default=SE_XML)", "", true, false));
        paramList.add(new OperationParameterBean("TIME=time", "Time value of layer desired", "", true, false));
        paramList.add(new OperationParameterBean("ELEVATION=elevation", "Elevation of layer desired", "", true, false));
        paramList.add(new OperationParameterBean("Other sample dimension(s)", "Value of other dimensions as appropriate", "", true, false));
        paramList.add(new OperationParameterBean("Vendor-specific parameters", "Optional experimental parameters", "", true, false));
        paramList.add(new OperationParameterBean("SLD=styled_layer_descriptor_URL", "URL of Styled Layer Descriptor (as defined in SLD Specification)", "", true, false));
        paramList.add(new OperationParameterBean("WFS=web_feature_service_URL", "URL of Web Feature Service providing features to be symbolized using SLD", "", true, false));

        getMapOp.setParamList(paramList);
        operations.add(getMapOp);

        // Operation - GetFeatureInfo - optional
        String getFeatureInfoAddress = xPathUtils.getString(doc,  XPATH_EXP_WMS_1_1_1_OP_GET_FEATURE_INFO_HREF);
        if (getFeatureInfoAddress != null && getFeatureInfoAddress.length() != 0) {
            OperationBean getFeatureInfoOp = new OperationBean();
            getFeatureInfoOp.setName("GetFeatureInfo");
            getFeatureInfoOp.setMethodCall("GetFeatureInfo");
            List<Integer> getFeatureInfoOpPlatform = new ArrayList<Integer>();
            getFeatureInfoOpPlatform.add(ID_OP_PLATFORM_HTTP_GET);
            getFeatureInfoOp.setPlatform(getFeatureInfoOpPlatform);
            List<String> getFeatureInfoOpAddressList = new ArrayList<String>();
            getFeatureInfoOpAddressList.add(getFeatureInfoAddress);
            getFeatureInfoOp.setAddressList(getFeatureInfoOpAddressList);
    
            paramList = new ArrayList<OperationParameterBean>();
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
     * @param boundingBoxesFromLayers
     * @return
     */
    private LocationBean getUnionOfBoundingBoxes(List<LocationBean> boundingBoxesFromLayers) {
        LocationBean unionLocation = new LocationBean();
        
        for (LocationBean layerLocation : boundingBoxesFromLayers) {
            if (unionLocation.getLatitude1() == null) unionLocation.setLatitude1(layerLocation.getLatitude1());
            if (unionLocation.getLatitude1() > layerLocation.getLatitude1())
                unionLocation.setLatitude1(layerLocation.getLatitude1());
            
            if (unionLocation.getLongitude1() == null) unionLocation.setLongitude1(layerLocation.getLongitude1());
            if (unionLocation.getLongitude1() > layerLocation.getLongitude1())
                unionLocation.setLongitude1(layerLocation.getLongitude1());
            
            if (unionLocation.getLatitude2() == null) unionLocation.setLatitude2(layerLocation.getLatitude2());
            if (unionLocation.getLatitude2() < layerLocation.getLatitude2())
                unionLocation.setLatitude2(layerLocation.getLatitude2());
            
            if (unionLocation.getLongitude2() == null) unionLocation.setLongitude2(layerLocation.getLongitude2());
            if (unionLocation.getLongitude2() < layerLocation.getLongitude2())
                unionLocation.setLongitude2(layerLocation.getLongitude2());
        }
        
        return unionLocation;
    }

    /**
     * @param doc
     * @return
     */
    private AddressBean getAddress(Document doc) {
        AddressBean address = new AddressBean();
        String[] name = extractName(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactPersonPrimary/ContactPerson"));
        if (name != null) {
            address.setFirstname(name[0].trim());
            address.setLastname(name[1].trim());
        } else {
            address.setLastname("N/A");
        }
        
        address.setOrganisation(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactPersonPrimary/ContactOrganization"));
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactElectronicMailAddress"));
        
        // try to find address in database and set the uuid if found
        searchForAddress(address);
        
        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactAddress/Address"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactAddress/City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactAddress/PostCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactAddress/Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactAddress/StateOrProvince"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_WMS_CONTACTINFORMATION + "/ContactVoiceTelephone"));
        
        return address;
    }

    /**
     * @param doc
     * @return
     */
    private List<LocationBean> getBoundingBoxesFromLayers(Document doc) {
        List<LocationBean> bboxes = new ArrayList<LocationBean>();
        CoordTransformUtil coordUtils = CoordTransformUtil.getInstance();
        NodeList layers = xPathUtils.getNodeList(doc, "/WMT_MS_Capabilities/Capability/Layer/Layer");
        for (int i = 0; i < layers.getLength(); i++) {
            Node layer = layers.item(i);
            
            // iterate over bounding boxes until it could be transformed to WGS84
            NodeList boundingBoxesNodes = xPathUtils.getNodeList(layer, "BoundingBox");
            for (int j = 0; j < boundingBoxesNodes.getLength(); j++) {
                Node bboxNode = boundingBoxesNodes.item(j);
                LocationBean box = null;
                CoordType coordType = getCoordType(bboxNode, coordUtils);
                double[] coordinates = null;
                if (coordType == null) {
                    // if coordinate type could not be determined, then try the next available
                    // bounding box of the layer, which should use a different CRS
                    continue;
                    
                } else if (coordType.equals(CoordTransformUtil.CoordType.COORDS_WGS84)) {
                    coordinates = getBoundingBoxCoordinates(bboxNode);
                            
                } else { // TRANSFORM
                    coordinates = getBoundingBoxCoordinates(bboxNode, coordUtils, coordType);
                    
                }
                if (coordinates != null) {
                    box = new LocationBean();
                    box.setLatitude1(coordinates[1]);
                    box.setLongitude1(coordinates[0]);
                    box.setLatitude2(coordinates[3]);
                    box.setLongitude2(coordinates[2]);
                    
                    box.setName(xPathUtils.getString(layer, "Name"));
                    box.setTopicId(box.getName());
                    
                    bboxes.add(box);
                    // go to next layer!
                    break;                    
                }
                
            }
        }
        return bboxes;
    }

    /**
     * @param bboxNode
     * @param coordUtils
     * @param coordType
     * @return
     */
    private double[] getBoundingBoxCoordinates(Node bboxNode, CoordTransformUtil coordUtils, CoordType coordType) {
        double[] coordsTrans = new double[4];
        double[] coords = getBoundingBoxCoordinates(bboxNode);
                
        try {
            double[] transMin = coordUtils.transformToWGS84(coords[0], coords[1], coordType);
            double[] transMax = coordUtils.transformToWGS84(coords[2], coords[3], coordType);
            coordsTrans[0] = transMin[0];
            coordsTrans[1] = transMin[1];
            coordsTrans[2] = transMax[0];
            coordsTrans[3] = transMax[1];            
        } catch (Exception e) {
            coordsTrans = null;
            e.printStackTrace();
        }
        return coordsTrans;
    }

    /**
     * @param bboxNode
     * @return
     */
    private double[] getBoundingBoxCoordinates(Node bboxNode) {
        double[] coords = new double[4];
        coords[0] = xPathUtils.getDouble(bboxNode, "@minx");
        coords[1] = xPathUtils.getDouble(bboxNode, "@miny");
        coords[2] = xPathUtils.getDouble(bboxNode, "@maxx");
        coords[3] = xPathUtils.getDouble(bboxNode, "@maxy");
        return coords;
    }

    private CoordType getCoordType(Node bboxNode, CoordTransformUtil coordUtils) {
        String crs = xPathUtils.getString(bboxNode, "@CRS");
        CoordType coordType = null;
        if (crs != null) {
            String code = crs.split(":")[1];
            coordType = coordUtils.getCoordTypeByEPSGCode(code);
        }
        return coordType;
    }

    /**
     * @param doc
     * @return
     */
    private List<SpatialReferenceSystemBean> getSpatialReferenceSystems(Document doc) {
        List<SpatialReferenceSystemBean> result = new ArrayList<SpatialReferenceSystemBean>();
        String[] crs = xPathUtils.getStringArray(doc, XPATH_EXP_WMS_LAYER_CRS);
        
        // check codelists for matching entryIds
        for (String item : crs) {
            SpatialReferenceSystemBean srsBean = new SpatialReferenceSystemBean();
            Integer itemId = Integer.valueOf(item.split(":")[1]);
            
            String value = syslistCache.getValueFromListId(100, itemId, false);
            if (value == null || value.isEmpty()) {
                srsBean.setId(-1);
                srsBean.setName(item);            
            } else {
                srsBean.setId(itemId);
                srsBean.setName(value);            
            }
            result.add(srsBean);
        }
        
        return result;
    }

    /**
     * @param references
     * @return
     */
    private List<ObjectReferenceBean> checkForCoupledResource(String[] references) {
        List<ObjectReferenceBean> result = new ArrayList<ObjectReferenceBean>();
        for (String resourceId : references) {
            ObjectReferenceBean bean = new ObjectReferenceBean();
            bean.setUuid(resourceId);
            bean.setObjClass("1");
            bean.setTitle("to be implemented");
            result.add(bean);            
        }
        return result;
    }
    
    private LocationBean getBoundingBoxFromLayer(Node layerNode) {
        LocationBean box = null;
        CoordTransformUtil coordUtils = CoordTransformUtil.getInstance();
        
        // iterate over bounding boxes until it could be transformed to WGS84
        NodeList boundingBoxesNodes = xPathUtils.getNodeList(layerNode, "BoundingBox");
        for (int j = 0; j < boundingBoxesNodes.getLength(); j++) {
            Node bboxNode = boundingBoxesNodes.item(j);
            CoordType coordType = getCoordType(bboxNode, coordUtils);
            double[] coordinates = null;
            if (coordType == null) {
                // if coordinate type could not be determined, then try the next available
                // bounding box of the layer, which should use a different CRS
                continue;
                
            } else if (coordType.equals(CoordTransformUtil.CoordType.COORDS_WGS84)) {
                coordinates = getBoundingBoxCoordinates(bboxNode);
                        
            } else { // TRANSFORM
                coordinates = getBoundingBoxCoordinates(bboxNode, coordUtils, coordType);
                
            }
            if (coordinates != null) {
                box = new LocationBean();
                box.setLatitude1(coordinates[1]);
                box.setLongitude1(coordinates[0]);
                box.setLatitude2(coordinates[3]);
                box.setLongitude2(coordinates[2]);
                
                box.setName(xPathUtils.getString(layerNode, "Name"));
                box.setTopicId(box.getName());
                box.setType( "F" );
                
                // finished!
                break;                    
            }
            
        }
        
        return box;
    }

}
