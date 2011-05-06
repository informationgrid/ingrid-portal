package de.ingrid.mdek.dwr.services;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.OperationParameterBean;


public class GetCapabilitiesService {

	private final static Logger log = Logger.getLogger(GetCapabilitiesService.class);	

	private enum ServiceType { WMS, WFS, WCS, CSW, WCTS }

	private final static String SERVICE_TYPE_WMS = "WMS";
	private final static String SERVICE_TYPE_WFS = "WFS";
	private final static String SERVICE_TYPE_WCS = "WCS";
	private final static String SERVICE_TYPE_CSW = "CSW";
	private final static String SERVICE_TYPE_WCTS = "WCTS";

    private static String ERROR_GETCAP_INVALID_URL = "ERROR_GETCAP_INVALID_URL";
    private static String ERROR_GETCAP_XPATH = "ERROR_GETCAP_XPATH";
    private static String ERROR_GETCAP = "ERROR_GETCAP_ERROR";

	// Version 1.3.0 of the WMS uses 'WMS_Capabilities' as its root element (OGC 06-042, Chapter 7.2.4.1)
    // Version 1.1.1 uses 'WMT_MS_Capabilities'
    private final static String XPATH_EXP_WMS_1_1_1_TITLE = "/WMT_MS_Capabilities/Service[1]/Title[1]";
	private final static String XPATH_EXP_WMS_1_1_1_ABSTRACT = "/WMT_MS_Capabilities/Service[1]/Abstract[1]";
	private final static String XPATH_EXP_WMS_1_1_1_VERSION = "/WMT_MS_Capabilities/@version";
	private final static String XPATH_EXP_WMS_1_1_1_OP_GET_CAPABILITIES_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetCapabilities[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	private final static String XPATH_EXP_WMS_1_1_1_OP_GET_MAP_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetMap[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	private final static String XPATH_EXP_WMS_1_1_1_OP_GET_FEATURE_INFO_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetFeatureInfo[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";

	private final static String XPATH_EXP_WMS_1_3_0_TITLE = "/WMS_Capabilities/Service[1]/Title[1]";
	private final static String XPATH_EXP_WMS_1_3_0_ABSTRACT = "/WMS_Capabilities/Service[1]/Abstract[1]";
	private final static String XPATH_EXP_WMS_1_3_0_VERSION = "/WMS_Capabilities/@version";
	private final static String XPATH_EXP_WMS_1_3_0_OP_GET_CAPABILITIES_HREF = "/WMS_Capabilities/Capability[1]/Request[1]/GetCapabilities[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	private final static String XPATH_EXP_WMS_1_3_0_OP_GET_MAP_HREF = "/WMS_Capabilities/Capability[1]/Request[1]/GetMap[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	private final static String XPATH_EXP_WMS_1_3_0_OP_GET_FEATURE_INFO_HREF = "/WMS_Capabilities/Capability[1]/Request[1]/GetFeatureInfo[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";

	private final static String XPATH_EXP_WFS_TITLE = "/WFS_Capabilities/ServiceIdentification[1]/Title[1]";
	private final static String XPATH_EXP_WFS_ABSTRACT = "/WFS_Capabilities/ServiceIdentification[1]/Abstract[1]";
	private final static String XPATH_EXP_WFS_VERSION = "/WFS_Capabilities/ServiceIdentification[1]/ServiceTypeVersion[1]";
	private final static String XPATH_EXP_WFS_OP_GET_CAPABILITIES_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_GET_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='DescribeFeatureType']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_POST_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='DescribeFeatureType']/DCP[1]/HTTP[1]/Post[1]/@href";
	private final static String XPATH_EXP_WFS_OP_GET_FEATURE_GET_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='GetFeature']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WFS_OP_GET_FEATURE_POST_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='GetFeature']/DCP[1]/HTTP[1]/Post[1]/@href";
	private final static String XPATH_EXP_WFS_OP_GET_GML_OBJECT_GET_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='GetGmlObject']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WFS_OP_GET_GML_OBJECT_POST_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='GetGmlObject']/DCP[1]/HTTP[1]/Post[1]/@href";
	private final static String XPATH_EXP_WFS_OP_LOCK_FEATURE_GET_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='LockFeature']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WFS_OP_LOCK_FEATURE_POST_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='LockFeature']/DCP[1]/HTTP[1]/Post[1]/@href";
	private final static String XPATH_EXP_WFS_OP_TRANSACTION_GET_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='Transaction']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WFS_OP_TRANSACTION_POST_HREF = "/WFS_Capabilities/OperationsMetadata[1]/Operation[@name='Transaction']/DCP[1]/HTTP[1]/Post[1]/@href";

	// Version 1.0.0 uses 'WCS_Capabilities' as root element (see http://schemas.opengis.net/wcs/1.0.0/wcsCapabilities.xsd)
	// Version 1.1.0 uses 'Capabilities'
	private final static String XPATH_EXP_WCS_1_0_0_TITLE = "/WCS_Capabilities/Service/name";
	private final static String XPATH_EXP_WCS_1_0_0_ABSTRACT = "/WCS_Capabilities/Service/description";
	private final static String XPATH_EXP_WCS_1_0_0_VERSION = "/WCS_Capabilities/@version";
	private final static String XPATH_EXP_WCS_1_0_0_OP_GET_CAPABILITIES_GET_HREF = "/WCS_Capabilities/Capability/Request/GetCapabilities/DCPType/HTTP/Get/OnlineResource/@href";
	private final static String XPATH_EXP_WCS_1_0_0_OP_GET_CAPABILITIES_POST_HREF = "/WCS_Capabilities/Capability/Request/GetCapabilities/DCPType/HTTP/Post/OnlineResource/@href";
	private final static String XPATH_EXP_WCS_1_0_0_OP_DESCRIBE_COVERAGE_GET_HREF = "/WCS_Capabilities/Capability/Request/DescribeCoverage/DCPType/HTTP/Get/OnlineResource/@href";
	private final static String XPATH_EXP_WCS_1_0_0_OP_DESCRIBE_COVERAGE_POST_HREF = "/WCS_Capabilities/Capability/Request/DescribeCoverage/DCPType/HTTP/Post/OnlineResource/@href";
	private final static String XPATH_EXP_WCS_1_0_0_OP_GET_COVERAGE_GET_HREF = "/WCS_Capabilities/Capability/Request/GetCoverage/DCPType/HTTP/Get/OnlineResource/@href";
	private final static String XPATH_EXP_WCS_1_0_0_OP_GET_COVERAGE_POST_HREF = "/WCS_Capabilities/Capability/Request/GetCoverage/DCPType/HTTP/Post/OnlineResource/@href";

	private final static String XPATH_EXP_WCS_1_1_0_TITLE = "/Capabilities/ServiceIdentification[1]/Title[1]";
	private final static String XPATH_EXP_WCS_1_1_0_ABSTRACT = "/Capabilities/ServiceIdentification[1]/Abstract[1]";
	private final static String XPATH_EXP_WCS_1_1_0_VERSION = "/Capabilities/@version";
	private final static String XPATH_EXP_WCS_1_1_0_OP_GET_CAPABILITIES_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCS_1_1_0_OP_GET_CAPABILITIES_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Post[1]/@href";
	private final static String XPATH_EXP_WCS_1_1_0_OP_DESCRIBE_COVERAGE_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeCoverage']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCS_1_1_0_OP_DESCRIBE_COVERAGE_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeCoverage']/DCP[1]/HTTP[1]/Post[1]/@href";
	private final static String XPATH_EXP_WCS_1_1_0_OP_GET_COVERAGE_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCoverage']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCS_1_1_0_OP_GET_COVERAGE_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCoverage']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_CSW_TITLE = "/Capabilities/ServiceIdentification[1]/Title[1]";
	private final static String XPATH_EXP_CSW_ABSTRACT = "/Capabilities/ServiceIdentification[1]/Abstract[1]";
	private final static String XPATH_EXP_CSW_VERSION = "/Capabilities/@version";

	private final static String XPATH_EXP_CSW_OP_GET_CAPABILITIES_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_CSW_OP_GET_CAPABILITIES_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_CSW_OP_DESCRIBE_RECORD_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeRecord']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_CSW_OP_DESCRIBE_RECORD_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeRecord']/DCP[1]/HTTP[1]/Post[1]/@href";
	
	private final static String XPATH_EXP_CSW_OP_GET_DOMAIN_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetDomain']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_CSW_OP_GET_DOMAIN_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetDomain']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_CSW_OP_GET_RECORDS_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetRecords']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_CSW_OP_GET_RECORDS_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetRecords']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetRecordById']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetRecordById']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_CSW_OP_HARVEST_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='Harvest']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_CSW_OP_HARVEST_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='Harvest']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCTS_TITLE = "/Capabilities/ServiceIdentification[1]/Title[1]";
	private final static String XPATH_EXP_WCTS_ABSTRACT = "/Capabilities/ServiceIdentification[1]/Abstract[1]";
	private final static String XPATH_EXP_WCTS_VERSION = "/Capabilities/@version";

	private final static String XPATH_EXP_WCTS_OP_GET_CAPABILITIES_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_GET_CAPABILITIES_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Post[1]/@href";
	
	private final static String XPATH_EXP_WCTS_OP_TRANSFORM_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='Transform']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_TRANSFORM_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='Transform']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='IsTransformable']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='IsTransformable']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetTransformation']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetTransformation']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeTransformation']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeTransformation']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCTS_OP_DESCRIBE_CRS_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeCRS']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_DESCRIBE_CRS_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeCRS']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeMethod']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeMethod']/DCP[1]/HTTP[1]/Post[1]/@href";

	private XPath xPath = null;
	
    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
    	XPathFactory factory = XPathFactory.newInstance();
    	xPath = factory.newXPath();
    }

    public CapabilitiesBean getCapabilities(String urlStr) {
    	try {
    		URL url = new URL(urlStr);
        	InputSource inputSource = new InputSource(url.openStream());

        	// Build a document from the xml response
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	// nameSpaceAware is false by default. Otherwise we would have to query for the correct namespace for every evaluation
//        	factory.setNamespaceAware(false);
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document doc = builder.parse(inputSource);

        	ServiceType serviceType = getServiceType(doc);
        	switch (serviceType) {
	        	case WMS: return getCapabilitiesWMS(doc, getServiceVersionWMS(doc));
	        	case WFS: return getCapabilitiesWFS(doc);
	        	case WCS: return getCapabilitiesWCS(doc, getServiceVersionWCS(doc));
	        	case CSW: return getCapabilitiesCSW(doc);
	        	case WCTS: return getCapabilitiesWCTS(doc);
	        	default:
	        		throw new RuntimeException("Unknown Service Type.");
        	}

    	} catch (MalformedURLException e) {
    		log.debug("", e);
    		throw new RuntimeException(ERROR_GETCAP_INVALID_URL, e);

    	} catch (IOException e) {
    		log.debug("", e);
    		throw new RuntimeException(ERROR_GETCAP, e);

    	} catch (XPathExpressionException e) {
    		log.debug("", e);
    		throw new RuntimeException(ERROR_GETCAP_XPATH, e);

    	} catch (Exception e) {
    		log.debug("", e);
    		throw new RuntimeException(ERROR_GETCAP, e);
    	}    
    }

    public CapabilitiesBean getCapabilitiesWMS(Document doc, String serviceVersion) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WMS");
    	result.setTitle(xPath.evaluate(getXPathExpressionFor(ServiceType.WMS, serviceVersion, "TITLE"), doc));
    	result.setDescription(xPath.evaluate(getXPathExpressionFor(ServiceType.WMS, serviceVersion, "ABSTRACT"), doc));
    	String version = xPath.evaluate(getXPathExpressionFor(ServiceType.WMS, serviceVersion, "VERSION"), doc);
    	List<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	getCapabilitiesOp.setName("GetCapabilities");
    	List<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	List<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	String address = xPath.evaluate(getXPathExpressionFor(ServiceType.WMS, serviceVersion, "OP_GET_CAPABILITIES_HREF"), doc);
    	getCapabilitiesOpAddressList.add(appendGetCapabilitiesParameterToWmsServiceUrl(address,version));
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
    	List<String> getMapOpPlatform = new ArrayList<String>();
    	getMapOpPlatform.add("HTTP GET");
    	getMapOp.setPlatform(getMapOpPlatform);
    	getMapOp.setMethodCall("GetMap");
    	List<String> getMapOpAddressList = new ArrayList<String>();
    	getMapOpAddressList.add(appendVersionParameterToWmsServiceUrl(xPath.evaluate(getXPathExpressionFor(ServiceType.WMS, serviceVersion, "OP_GET_MAP_HREF"), doc),version));
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
    	String getFeatureInfoAddress = appendVersionParameterToWmsServiceUrl(xPath.evaluate(getXPathExpressionFor(ServiceType.WMS, serviceVersion, "OP_GET_FEATURE_INFO_HREF"), doc),version);
    	if (getFeatureInfoAddress != null && getFeatureInfoAddress.length() != 0) {
	    	OperationBean getFeatureInfoOp = new OperationBean();
	    	getFeatureInfoOp.setName("GetFeatureInfo");
	    	List<String> getFeatureInfoOpPlatform = new ArrayList<String>();
	    	getFeatureInfoOpPlatform.add("HTTP GET");
	    	getFeatureInfoOp.setPlatform(getFeatureInfoOpPlatform);
	    	getFeatureInfoOp.setMethodCall("GetFeatureInfo");
	    	List<String> getFeatureInfoOpAddressList = new ArrayList<String>();
	    	getFeatureInfoOpAddressList.add(getFeatureInfoAddress);
	    	getFeatureInfoOp.setAddressList(getFeatureInfoOpAddressList);
	
	    	paramList = new ArrayList<OperationParameterBean>();
	    	paramList.add(new OperationParameterBean("VERSION="+version, "Request version", "", false, false));
	    	paramList.add(new OperationParameterBean("REQUEST=GetFeatureInfo", "Request name", "", false, false));
	    	paramList.add(new OperationParameterBean("<map_request_copy>", "Partial copy of the Map request parameters that generated the map for which information is desired", "", false, false));
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

    private String appendVersionParameterToWmsServiceUrl(String baseUrl, String version) {
    	StringBuilder url = new StringBuilder(baseUrl);
    	if (url.lastIndexOf("?") != url.length() - 1) {
    		url.append('?');
    	}

    	url.append("version="+version);
    	return url.toString();
	}

	private String appendGetCapabilitiesParameterToWmsServiceUrl(String baseUrl, String version) {
    	StringBuilder url = new StringBuilder(baseUrl);
    	if (url.lastIndexOf("?") != url.length() - 1) {
    		url.append('?');
    	}

    	url.append("SERVICE=WMS&REQUEST=GetCapabilities&version="+version);
    	return url.toString();
    }


    public CapabilitiesBean getCapabilitiesWFS(Document doc) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WFS");
    	result.setTitle(xPath.evaluate(XPATH_EXP_WFS_TITLE, doc));
    	result.setDescription(xPath.evaluate(XPATH_EXP_WFS_ABSTRACT, doc));
    	String version = xPath.evaluate(XPATH_EXP_WFS_VERSION, doc);
    	List<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	getCapabilitiesOp.setName("GetCapabilities");
    	List<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	List<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(xPath.evaluate(XPATH_EXP_WFS_OP_GET_CAPABILITIES_HREF, doc));
    	getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

    	List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("VERSION=version", "Request version", "", true, false));
    	paramList.add(new OperationParameterBean("SERVICE=WFS", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=string", "Sequence number or string for cache control", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);

    	// Operation - DescribeFeatureType
    	OperationBean describeFeatureTypeOp = new OperationBean();
    	String describeFeatureTypeGet = xPath.evaluate(XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_GET_HREF, doc);
    	String describeFeatureTypePost = xPath.evaluate(XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_POST_HREF, doc);
    	describeFeatureTypeOp.setName("DescribeFeatureType");
    	describeFeatureTypeOp.setMethodCall("DescribeFeatureType");
    	List<String> describeFeatureTypeOpAddressList = new ArrayList<String>();
    	describeFeatureTypeOpAddressList.add(describeFeatureTypeGet);
    	List<String> describeFeatureTypeOpPlatform = new ArrayList<String>();
    	describeFeatureTypeOpPlatform.add("HTTP GET");
    	if (describeFeatureTypePost != null && describeFeatureTypePost.length() != 0) {
        	describeFeatureTypeOpAddressList.add(describeFeatureTypePost);
    		describeFeatureTypeOpPlatform.add("HTTP POST");
    	}
    	describeFeatureTypeOp.setPlatform(describeFeatureTypeOpPlatform);
    	describeFeatureTypeOp.setAddressList(describeFeatureTypeOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("REQUEST=DescribeFeatureType", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("TYPENAME", "A comma separated list of feature types to describe. If no value is specified that is to be interpreted as all feature types", "", true, false));
    	paramList.add(new OperationParameterBean("OUTPUTFORMAT", "The output format to use to describe feature types. text/xml; subtype=gml/3.1.1 must be supported. Other output formats, such as DTD are possible", "", true, false));
    	describeFeatureTypeOp.setParamList(paramList);
    	operations.add(describeFeatureTypeOp);

    	// Operation - GetFeature
    	OperationBean getFeatureOp = new OperationBean();
    	String getFeatureGet = xPath.evaluate(XPATH_EXP_WFS_OP_GET_FEATURE_GET_HREF, doc);
    	String getFeaturePost = xPath.evaluate(XPATH_EXP_WFS_OP_GET_FEATURE_POST_HREF, doc);
    	getFeatureOp.setName("GetFeature");
    	getFeatureOp.setMethodCall("GetFeature");
    	List<String> getFeatureOpAddressList = new ArrayList<String>();
    	getFeatureOpAddressList.add(getFeatureGet);
    	List<String> getFeatureOpPlatform = new ArrayList<String>();
    	getFeatureOpPlatform.add("HTTP GET");
    	if (getFeaturePost != null && getFeaturePost.length() != 0) {
        	getFeatureOpAddressList.add(getFeaturePost);
    		getFeatureOpPlatform.add("HTTP POST");
    	}
    	getFeatureOp.setPlatform(getFeatureOpPlatform);
    	getFeatureOp.setAddressList(getFeatureOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("outputFormat=GML2", "This value is kept for backward compatability and indicates that an XML instance document must be generated that validates against a GML2 application schema", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat=text/xml; subtype=gml/2.1.2", "Same as GML2", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat=text/xml; subtype=gml/3.1.1; subtype=gml/2.1.2", "This value indicates that an XML instance document must be generated that validates against a GML3 application schema. This is the default values of the outputFormat attribute if the attribute is not specified in the GetFeature request", "", true, false));
    	paramList.add(new OperationParameterBean("resultType=Results", "The default value results indicates that a web feature service should generate a complete response that contains all the features that satisfy the request. The exact structure of the response is defined in clause 9.3", "", true, false));
    	paramList.add(new OperationParameterBean("resultType=Hits", "The value hits indicates that a web feature service should process the GetFeature request and rather than return the entire result set, it should simply indicate the number of feature instance of the requested feature type(s) that satisfy the request. That is that the count should only include instances of feature types specified in the typeName attribute (i.e. GetFeature/Query/@typeName). The exact way in which the feature count is conveyed to a client application is described in clause 9.3", "", true, false));

    	getFeatureOp.setParamList(paramList);
    	operations.add(getFeatureOp);

    	// Operation - GetGmlObject - optional
    	String getGmlObjectGet = xPath.evaluate(XPATH_EXP_WFS_OP_GET_GML_OBJECT_GET_HREF, doc);
    	String getGmlObjectPost = xPath.evaluate(XPATH_EXP_WFS_OP_GET_GML_OBJECT_POST_HREF, doc);
    	if (getGmlObjectGet != null && getGmlObjectGet.length() != 0) {
	    	OperationBean getGmlObjectOp = new OperationBean();
	    	getGmlObjectOp.setName("GetGmlObject");
	    	getGmlObjectOp.setMethodCall("GetGmlObject");
	    	List<String> getGmlObjectAddressList = new ArrayList<String>();
	    	getGmlObjectAddressList.add(getGmlObjectGet);
	    	List<String> getGmlObjectOpPlatform = new ArrayList<String>();
	    	getGmlObjectOpPlatform.add("HTTP GET");
	    	if (getGmlObjectPost != null && getGmlObjectPost.length() != 0) {
	        	getGmlObjectAddressList.add(getGmlObjectPost);
	    		getGmlObjectOpPlatform.add("HTTP POST");
	    	}
	    	getGmlObjectOp.setPlatform(getGmlObjectOpPlatform);
	    	getGmlObjectOp.setAddressList(getGmlObjectAddressList);
	
	    	paramList = new ArrayList<OperationParameterBean>();
	    	paramList.add(new OperationParameterBean("REQUEST=[GetGmlObject]", "The name of the WFS request", "", false, false));
	    	paramList.add(new OperationParameterBean("TRAVERSEXLINKDEPTH", "The depth to which nested property XLink linking element locator attribute (href) XLinks are traversed and resolved if possible. The range of valid values consists of positive integers plus \"*\" for unlimited", "", false, false));
	    	paramList.add(new OperationParameterBean("TRAVERSEXLINKEXPIRY", "The number of minutes a WFS should wait to receive a response to a nested GetGmlObject request.. If no value is specified then the period is implementation dependent", "", true, false));
	    	paramList.add(new OperationParameterBean("GMLOBJECTID", "The XML ID of the element to fetch", "", false, false));

	    	getGmlObjectOp.setParamList(paramList);
	    	operations.add(getGmlObjectOp);
    	}

    	// Operation - LockFeature - optional
    	String lockFeatureGet = xPath.evaluate(XPATH_EXP_WFS_OP_LOCK_FEATURE_GET_HREF, doc);
    	String lockFeaturePost = xPath.evaluate(XPATH_EXP_WFS_OP_LOCK_FEATURE_POST_HREF, doc);
    	if (lockFeatureGet != null && lockFeatureGet.length() != 0) {
	    	OperationBean lockFeatureOp = new OperationBean();
	    	lockFeatureOp.setName("LockFeature");
	    	lockFeatureOp.setMethodCall("LockFeature");
	    	List<String> lockFeatureAddressList = new ArrayList<String>();
	    	lockFeatureAddressList.add(lockFeatureGet);
	    	List<String> lockFeatureOpPlatform = new ArrayList<String>();
	    	lockFeatureOpPlatform.add("HTTP GET");
	    	if (lockFeaturePost != null && lockFeaturePost.length() != 0) {
	        	lockFeatureAddressList.add(lockFeaturePost);
	    		lockFeatureOpPlatform.add("HTTP POST");
	    	}
	    	lockFeatureOp.setPlatform(lockFeatureOpPlatform);
	    	lockFeatureOp.setAddressList(lockFeatureAddressList);

	    	paramList = new ArrayList<OperationParameterBean>();
	    	paramList.add(new OperationParameterBean("REQUEST=[LockFeature]", "The name of the WFS request", "", false, false));
	    	paramList.add(new OperationParameterBean("TRAVERSEXLINKDEPTH", "The depth to which nested property XLink linking element locator attribute (href) XLinks are traversed and resolved if possible. The range of valid values consists of positive integers plus \"*\" for unlimited", "", false, false));
	    	paramList.add(new OperationParameterBean("TRAVERSEXLINKEXPIRY", "The number of minutes a WFS should wait to receive a response to a nested GetGmlObject request.. If no value is specified then the period is implementation dependent", "", true, false));
	    	paramList.add(new OperationParameterBean("GMLOBJECTID", "The XML ID of the element to fetch", "", false, false));

	    	lockFeatureOp.setParamList(paramList);
	    	operations.add(lockFeatureOp);
    	}

    	// Operation - Transaction - optional
    	String transactionGet = xPath.evaluate(XPATH_EXP_WFS_OP_TRANSACTION_GET_HREF, doc);
    	String transactionPost = xPath.evaluate(XPATH_EXP_WFS_OP_TRANSACTION_POST_HREF, doc);
    	if (transactionGet != null && transactionGet.length() != 0) {
	    	OperationBean transactionOp = new OperationBean();
	    	transactionOp.setName("Transaction");
	    	transactionOp.setMethodCall("Transaction");
	    	List<String> transactionAddressList = new ArrayList<String>();
	    	transactionAddressList.add(transactionGet);
	    	List<String> transactionOpPlatform = new ArrayList<String>();
	    	transactionOpPlatform.add("HTTP GET");
	    	if (transactionPost != null && transactionPost.length() != 0) {
	        	transactionAddressList.add(transactionPost);
	    		transactionOpPlatform.add("HTTP POST");
	    	}
	    	transactionOp.setPlatform(transactionOpPlatform);
	    	transactionOp.setAddressList(transactionAddressList);

	    	paramList = new ArrayList<OperationParameterBean>();
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


    public CapabilitiesBean getCapabilitiesWCS(Document doc, String serviceVersion) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WCS");
    	result.setTitle(xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "TITLE"), doc));
    	result.setDescription(xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "ABSTRACT"), doc));
    	String version = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "VERSION"), doc);
    	List<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();


    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	String getCapabilitiesGet = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_CAPABILITIES_GET_HREF"), doc);
    	String getCapabilitiesPost = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_CAPABILITIES_POST_HREF"), doc);
    	getCapabilitiesOp.setName("GetCapabilities");
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	List<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(getCapabilitiesGet);
    	List<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	if (getCapabilitiesPost != null && getCapabilitiesPost.length() != 0) {
    		getCapabilitiesOpAddressList.add(getCapabilitiesPost);
    		getCapabilitiesOpPlatform.add("HTTP POST");
    	}
		getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
		getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

    	List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("SERVICE=WCS", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
    	paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);


    	// Operation - DescribeCoverage
    	OperationBean describeCoverageOp = new OperationBean();
    	String describeCoverageGet = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_DESCRIBE_COVERAGE_GET_HREF"), doc);
    	String describeCoveragePost = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_DESCRIBE_COVERAGE_POST_HREF"), doc);
    	describeCoverageOp.setName("DescribeCoverage");
    	describeCoverageOp.setMethodCall("DescribeCoverage");
    	List<String> describeCoverageOpAddressList = new ArrayList<String>();
    	describeCoverageOpAddressList.add(describeCoverageGet);
    	List<String> describeCoverageOpPlatform = new ArrayList<String>();
    	describeCoverageOpPlatform.add("HTTP GET");
    	if (describeCoveragePost != null && describeCoveragePost.length() != 0) {
        	describeCoverageOpAddressList.add(describeCoveragePost);
    		describeCoverageOpPlatform.add("HTTP POST");
    	}
    	describeCoverageOp.setPlatform(describeCoverageOpPlatform);
    	describeCoverageOp.setAddressList(describeCoverageOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCS", "Service name. Shall be WCS", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeCoverage", "Request name. Shall be DescribeCoverage", "", false, false));
    	paramList.add(new OperationParameterBean("version=1.1.2", "Request protocol version", "", false, false));
    	paramList.add(new OperationParameterBean("identifiers=identifier1, identifier2, ...", "A comma-separated list of coverage identifiers to describe (identified by their identifier values in the Capabilities document)", "", false, false));
    	describeCoverageOp.setParamList(paramList);
    	operations.add(describeCoverageOp);

    	
    	// Operation - GetCoverage
    	OperationBean getCoverageOp = new OperationBean();
    	String getCoverageGet = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_COVERAGE_GET_HREF"), doc);
    	String getCoveragePost = xPath.evaluate(getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_COVERAGE_POST_HREF"), doc);
    	getCoverageOp.setName("GetCoverage");
    	getCoverageOp.setMethodCall("GetCoverage");
    	List<String> getCoverageOpAddressList = new ArrayList<String>();
    	getCoverageOpAddressList.add(getCoverageGet);
    	List<String> getCoverageOpPlatform = new ArrayList<String>();
    	getCoverageOpPlatform.add("HTTP GET");
    	if (getCoveragePost != null && getCoveragePost.length() != 0) {
        	getCoverageOpAddressList.add(getCoveragePost);
    		getCoverageOpPlatform.add("HTTP POST");
    	}
    	getCoverageOp.setPlatform(getCoverageOpPlatform);
    	getCoverageOp.setAddressList(getCoverageOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
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

    	result.setOperations(operations);
    	return result;
    }

    public CapabilitiesBean getCapabilitiesCSW(Document doc) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("CSW");
    	result.setTitle(xPath.evaluate(XPATH_EXP_CSW_TITLE, doc));
    	result.setDescription(xPath.evaluate(XPATH_EXP_CSW_ABSTRACT, doc));
    	String version = xPath.evaluate(XPATH_EXP_CSW_VERSION, doc);
    	List<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	String getCapabilitiesGet = xPath.evaluate(XPATH_EXP_CSW_OP_GET_CAPABILITIES_GET_HREF, doc);
    	String getCapabilitiesPost = xPath.evaluate(XPATH_EXP_CSW_OP_GET_CAPABILITIES_POST_HREF, doc);
    	getCapabilitiesOp.setName("GetCapabilities");
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	List<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(getCapabilitiesGet);
    	List<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	if (getCapabilitiesPost != null && getCapabilitiesPost.length() != 0) {
    		getCapabilitiesOpAddressList.add(getCapabilitiesPost);
    		getCapabilitiesOpPlatform.add("HTTP POST");
    	}
		getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
		getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

    	List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("SERVICE=CSW", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
    	paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);


    	// Operation - DescribeRecord
    	OperationBean describeRecordOp = new OperationBean();
    	String describeRecordGet = xPath.evaluate(XPATH_EXP_CSW_OP_DESCRIBE_RECORD_GET_HREF, doc);
    	String describeRecordPost = xPath.evaluate(XPATH_EXP_CSW_OP_DESCRIBE_RECORD_POST_HREF, doc);
    	describeRecordOp.setName("DescribeRecord");
    	describeRecordOp.setMethodCall("DescribeRecord");
    	List<String> describeRecordOpAddressList = new ArrayList<String>();
    	describeRecordOpAddressList.add(describeRecordGet);
    	List<String> describeRecordOpPlatform = new ArrayList<String>();
    	describeRecordOpPlatform.add("HTTP GET");
    	if (describeRecordPost != null && describeRecordPost.length() != 0) {
        	describeRecordOpAddressList.add(describeRecordPost);
    		describeRecordOpPlatform.add("HTTP POST");
    	}
    	describeRecordOp.setPlatform(describeRecordOpPlatform);
    	describeRecordOp.setAddressList(describeRecordOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=CSW", "Service name. Shall be CSW", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeRecord", "Fixed value of DescribeRecord, case insensitive", "", false, false));
    	paramList.add(new OperationParameterBean("version=2.0.2", "Fixed value of 2.0.2", "", false, false));
    	paramList.add(new OperationParameterBean("NAMESPACE", "List of Character String, comma separated. Used to specify namespace(s) and their prefix(es). Format is xmlns([prefix=]namespace-url). If prefix is not specified, then this is the default namespace.", "", true, false));
    	paramList.add(new OperationParameterBean("TypeName", "List of Character String, comma separated. One or more qualified type names to be described", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat", "Character String. A MIME type indicating the format that the output document should have", "", true, false));
    	paramList.add(new OperationParameterBean("schemaLanguage", "Character String", "", true, false));
    	describeRecordOp.setParamList(paramList);
    	operations.add(describeRecordOp);


    	// Operation - GetDomain
    	OperationBean getDomainOp = new OperationBean();
    	String getDomainGet = xPath.evaluate(XPATH_EXP_CSW_OP_GET_DOMAIN_GET_HREF, doc);
    	String getDomainPost = xPath.evaluate(XPATH_EXP_CSW_OP_GET_DOMAIN_POST_HREF, doc);
    	getDomainOp.setName("GetDomain");
    	getDomainOp.setMethodCall("GetDomain");
    	List<String> getDomainOpAddressList = new ArrayList<String>();
    	getDomainOpAddressList.add(getDomainGet);
    	List<String> getDomainOpPlatform = new ArrayList<String>();
    	getDomainOpPlatform.add("HTTP GET");
    	if (getDomainPost != null && getDomainPost.length() != 0) {
        	getDomainOpAddressList.add(getDomainPost);
    		getDomainOpPlatform.add("HTTP POST");
    	}
    	getDomainOp.setPlatform(getDomainOpPlatform);
    	getDomainOp.setAddressList(getDomainOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=CSW", "Service name. Shall be CSW", "", false, false));
    	paramList.add(new OperationParameterBean("request=GetDomain", "Fixed value of GetDomain, case insensitive", "", false, false));
    	paramList.add(new OperationParameterBean("version=2.0.2", "Fixed value of 2.0.2", "", false, false));
    	paramList.add(new OperationParameterBean("ParameterName", "List of Character String, comma separated. Unordered list of names of requested parameters, of the form OperationName.ParameterName", "", true, false));
    	paramList.add(new OperationParameterBean("PropertyName", "List of Character String, comma separated. Unordered list of names of requested properties, from the information model that the catalogue is using", "", true, false));
    	getDomainOp.setParamList(paramList);
    	operations.add(getDomainOp);

    	// Operation - GetRecords
    	OperationBean getRecordsOp = new OperationBean();
    	String getRecordsGet = xPath.evaluate(XPATH_EXP_CSW_OP_GET_RECORDS_GET_HREF, doc);
    	String getRecordsPost = xPath.evaluate(XPATH_EXP_CSW_OP_GET_RECORDS_POST_HREF, doc);
    	getRecordsOp.setName("GetRecords");
    	getRecordsOp.setMethodCall("GetRecords");
    	List<String> getRecordsOpAddressList = new ArrayList<String>();
    	getRecordsOpAddressList.add(getRecordsGet);
    	List<String> getRecordsOpPlatform = new ArrayList<String>();
    	getRecordsOpPlatform.add("HTTP GET");
    	if (getRecordsPost != null && getRecordsPost.length() != 0) {
        	getRecordsOpAddressList.add(getRecordsPost);
    		getRecordsOpPlatform.add("HTTP POST");
    	}
    	getRecordsOp.setPlatform(getRecordsOpPlatform);
    	getRecordsOp.setAddressList(getRecordsOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=CSW", "Service name. Shall be CSW", "", false, false));
    	paramList.add(new OperationParameterBean("request=GetRecords", "Fixed value of GetRecords, case insensitive", "", false, false));
    	paramList.add(new OperationParameterBean("version=2.0.2", "Fixed value of 2.0.2", "", false, false));
    	paramList.add(new OperationParameterBean("typeNames", "List of Character String, comma separated. Unordered List of object types implicated in the query", "", false, false));
    	paramList.add(new OperationParameterBean("NAMESPACE", "List of Character String, comma separated. Used to specify namespace(s) and their prefix(es). Format is xmlns([prefix=]namespace-url). If prefix is not specified, then this is the default namespace.", "", true, false));
    	paramList.add(new OperationParameterBean("resultType", "CodeList with allowed values: 'hits', 'results' or 'validate'", "", true, false));
    	paramList.add(new OperationParameterBean("requestId", "URI", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat", "Character String. Value is Mime type. The only value that is required to be supported is application/xml. Other supported values may include text/html and text/plain", "", true, false));
    	paramList.add(new OperationParameterBean("outputSchema", "Any URI", "", true, false));
    	paramList.add(new OperationParameterBean("startPosition", "Non-Zero Positive Integer", "", true, false));
    	paramList.add(new OperationParameterBean("maxRecords", "Positive Integer", "", true, false));
    	paramList.add(new OperationParameterBean("ElementSetName", "List of Character String", "", true, false));
    	paramList.add(new OperationParameterBean("ElementName", "List of Character String", "", true, false));
    	paramList.add(new OperationParameterBean("CONSTRAINTLANGUAGE", "CodeList with allowed values: CQL_TEXT or FILTER", "", true, false));
    	paramList.add(new OperationParameterBean("Constraint", "Character String. Predicate expression specified in the language indicated by the CONSTRAINTLANGUAGE parameter", "", true, false));
    	paramList.add(new OperationParameterBean("SortBy", "List of Character String, comma separated. Ordered list of names of metadata elements to use for sorting the response. Format of each list item is metadata_element_name:A indicating an ascending sort or metadata_ element_name:D indicating descending sort", "", true, false));
    	paramList.add(new OperationParameterBean("DistributedSearch", "Boolean", "", true, false));
    	paramList.add(new OperationParameterBean("hopCount", "Integer", "", true, false));
    	paramList.add(new OperationParameterBean("ResponseHandler", "Any URI", "", true, false));
    	getRecordsOp.setParamList(paramList);
    	operations.add(getRecordsOp);
    	
    	// Operation - GetRecordById
    	OperationBean getRecordByIdOp = new OperationBean();
    	String getRecordByIdGet = xPath.evaluate(XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_GET_HREF, doc);
    	String getRecordByIdPost = xPath.evaluate(XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_POST_HREF, doc);
    	getRecordByIdOp.setName("GetRecordById");
    	getRecordByIdOp.setMethodCall("GetRecordById");
    	List<String> getRecordByIdOpAddressList = new ArrayList<String>();
    	getRecordByIdOpAddressList.add(getRecordByIdGet);
    	List<String> getRecordByIdOpPlatform = new ArrayList<String>();
    	getRecordByIdOpPlatform.add("HTTP GET");
    	if (getRecordByIdPost != null && getRecordByIdPost.length() != 0) {
        	getRecordByIdOpAddressList.add(getRecordByIdPost);
    		getRecordByIdOpPlatform.add("HTTP POST");
    	}
    	getRecordByIdOp.setPlatform(getRecordByIdOpPlatform);
    	getRecordByIdOp.setAddressList(getRecordByIdOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("request=GetRecordById", "Fixed value of GetRecordById, case insensitive", "", false, false));
    	paramList.add(new OperationParameterBean("Id", "Comma separated list of anyURI", "", false, false));
    	paramList.add(new OperationParameterBean("ElementSetName", "CodeList with allowed values: 'brief', 'summary' or 'full'", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat", "Character String. Value is Mime type. The only value that is required to be supported is application/xml. Other supported values may include text/html and text/plain", "", true, false));
    	paramList.add(new OperationParameterBean("outputSchema", "Reference to the preferred schema of the response", "", true, false));
    	getRecordByIdOp.setParamList(paramList);
    	operations.add(getRecordByIdOp);

    	// Operation - Harvest
    	OperationBean harvestOp = new OperationBean();
    	String harvestGet = xPath.evaluate(XPATH_EXP_CSW_OP_HARVEST_GET_HREF, doc);
    	String harvestPost = xPath.evaluate(XPATH_EXP_CSW_OP_HARVEST_POST_HREF, doc);
    	harvestOp.setName("Harvest");
    	harvestOp.setMethodCall("Harvest");
    	List<String> harvestOpAddressList = new ArrayList<String>();
    	harvestOpAddressList.add(harvestGet);
    	List<String> harvestOpPlatform = new ArrayList<String>();
    	harvestOpPlatform.add("HTTP GET");
    	if (harvestPost != null && harvestPost.length() != 0) {
        	harvestOpAddressList.add(harvestPost);
    		harvestOpPlatform.add("HTTP POST");
    	}
    	harvestOp.setPlatform(harvestOpPlatform);
    	harvestOp.setAddressList(harvestOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("request=Harvest", "Fixed value of Harvest, case insensitive", "", false, false));
    	paramList.add(new OperationParameterBean("service=CSW", "Service name. Shall be CSW", "", false, false));
    	paramList.add(new OperationParameterBean("version=2.0.2", "Fixed value of 2.0.2", "", false, false));
    	paramList.add(new OperationParameterBean("Source", "URI. Reference to the source from which the resource is to be harvested", "", false, false));
    	paramList.add(new OperationParameterBean("ResourceType", "Character String. Reference to the type of resource being harvested", "", false, false));
    	paramList.add(new OperationParameterBean("NAMESPACE", "List of Character String, comma separated. Used to specify namespace(s) and their prefix(es). Format is xmlns([prefix=]namespace-url). If prefix is not specified, then this is the default namespace.", "", true, false));
    	paramList.add(new OperationParameterBean("ResourceFormat", "Character String. MIME type indicating format of the resource being harvested", "", true, false));
    	paramList.add(new OperationParameterBean("ResponseHandler", "URL. A reference to a person or entity that the CSW should respond to when it has completed processing Harvest request asynchronously", "", true, false));
    	paramList.add(new OperationParameterBean("HarvestInterval", "Period. Must conform to ISO8601 Period syntax.", "", true, false));
    	harvestOp.setParamList(paramList);
    	operations.add(harvestOp);

    	
    	result.setOperations(operations);
    	return result;
    }

    public CapabilitiesBean getCapabilitiesWCTS(Document doc) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WCTS");
    	result.setTitle(xPath.evaluate(XPATH_EXP_WCTS_TITLE, doc));
    	result.setDescription(xPath.evaluate(XPATH_EXP_WCTS_ABSTRACT, doc));
    	String version = xPath.evaluate(XPATH_EXP_WCTS_VERSION, doc);
    	List<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	String getCapabilitiesGet = xPath.evaluate(XPATH_EXP_WCTS_OP_GET_CAPABILITIES_GET_HREF, doc);
    	String getCapabilitiesPost = xPath.evaluate(XPATH_EXP_WCTS_OP_GET_CAPABILITIES_POST_HREF, doc);
    	getCapabilitiesOp.setName("GetCapabilities");
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	List<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(getCapabilitiesGet);
    	List<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	if (getCapabilitiesPost != null && getCapabilitiesPost.length() != 0) {
    		getCapabilitiesOpAddressList.add(getCapabilitiesPost);
    		getCapabilitiesOpPlatform.add("HTTP POST");
    	}
		getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
		getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

    	List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("SERVICE=WCTS", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
    	paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);


    	// Operation - Transform
    	OperationBean transformOp = new OperationBean();
    	String transformGet = xPath.evaluate(XPATH_EXP_WCTS_OP_TRANSFORM_GET_HREF, doc);
    	String transformPost = xPath.evaluate(XPATH_EXP_WCTS_OP_TRANSFORM_POST_HREF, doc);
    	transformOp.setName("Transform");
    	transformOp.setMethodCall("Transform");
    	List<String> transformOpAddressList = new ArrayList<String>();
    	transformOpAddressList.add(transformGet);
    	List<String> transformOpPlatform = new ArrayList<String>();
    	transformOpPlatform.add("HTTP GET");
    	if (transformPost != null && transformPost.length() != 0) {
        	transformOpAddressList.add(transformPost);
    		transformOpPlatform.add("HTTP POST");
    	}
    	transformOp.setPlatform(transformOpPlatform);
    	transformOp.setAddressList(transformOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
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


    	// Operation - IsTransformable
    	OperationBean isTransformableOp = new OperationBean();
    	String isTransformableGet = xPath.evaluate(XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_GET_HREF, doc);
    	String isTransformablePost = xPath.evaluate(XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_POST_HREF, doc);
    	isTransformableOp.setName("IsTransformable");
    	isTransformableOp.setMethodCall("IsTransformable");
    	List<String> isTransformableOpAddressList = new ArrayList<String>();
    	isTransformableOpAddressList.add(isTransformableGet);
    	List<String> isTransformableOpPlatform = new ArrayList<String>();
    	isTransformableOpPlatform.add("HTTP GET");
    	if (isTransformablePost != null && isTransformablePost.length() != 0) {
        	isTransformableOpAddressList.add(isTransformablePost);
    		isTransformableOpPlatform.add("HTTP POST");
    	}
    	isTransformableOp.setPlatform(isTransformableOpPlatform);
    	isTransformableOp.setAddressList(isTransformableOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
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


    	// Operation - GetTransformation
    	OperationBean getTransformationOp = new OperationBean();
    	String getTransformationGet = xPath.evaluate(XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_GET_HREF, doc);
    	String getTransformationPost = xPath.evaluate(XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_POST_HREF, doc);
    	getTransformationOp.setName("GetTransformation");
    	getTransformationOp.setMethodCall("GetTransformation");
    	List<String> getTransformationOpAddressList = new ArrayList<String>();
    	getTransformationOpAddressList.add(getTransformationGet);
    	List<String> getTransformationOpPlatform = new ArrayList<String>();
    	getTransformationOpPlatform.add("HTTP GET");
    	if (getTransformationPost != null && getTransformationPost.length() != 0) {
        	getTransformationOpAddressList.add(getTransformationPost);
    		getTransformationOpPlatform.add("HTTP POST");
    	}
    	getTransformationOp.setPlatform(getTransformationOpPlatform);
    	getTransformationOp.setAddressList(getTransformationOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=GetTransformation", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.20", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("SourceCRS=urn:ogc:def:crs:EPSG:6.3:4326", "Identifier URI of input coordinate reference system", "", false, false));
    	paramList.add(new OperationParameterBean("TargetCRS=urn:ogc:def:crs:EPSG:6.3:32611", "Identifier URI of desired output coordinate reference system", "", false, false));
    	getTransformationOp.setParamList(paramList);
    	operations.add(getTransformationOp);

    	// Operation - DescribeTransformation
    	OperationBean describeTransformationOp = new OperationBean();
    	String describeTransformationGet = xPath.evaluate(XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_GET_HREF, doc);
    	String describeTransformationPost = xPath.evaluate(XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_POST_HREF, doc);
    	describeTransformationOp.setName("DescribeTransformation");
    	describeTransformationOp.setMethodCall("DescribeTransformation");
    	List<String> describeTransformationOpAddressList = new ArrayList<String>();
    	describeTransformationOpAddressList.add(describeTransformationGet);
    	List<String> describeTransformationOpPlatform = new ArrayList<String>();
    	describeTransformationOpPlatform.add("HTTP GET");
    	if (describeTransformationPost != null && describeTransformationPost.length() != 0) {
        	describeTransformationOpAddressList.add(describeTransformationPost);
    		describeTransformationOpPlatform.add("HTTP POST");
    	}
    	describeTransformationOp.setPlatform(describeTransformationOpPlatform);
    	describeTransformationOp.setAddressList(describeTransformationOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeTransformation", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("Transformations=urn:ogc:def:coordinateOperation:EPSG:6.3:19916", "Identifier URIs of one or more coordinate operations, comma-separated list", "", false, false));
    	describeTransformationOp.setParamList(paramList);
    	operations.add(describeTransformationOp);

    	// Operation - DescribeCRS
    	OperationBean describeCRSOp = new OperationBean();
    	String describeCRSGet = xPath.evaluate(XPATH_EXP_WCTS_OP_DESCRIBE_CRS_GET_HREF, doc);
    	String describeCRSPost = xPath.evaluate(XPATH_EXP_WCTS_OP_DESCRIBE_CRS_POST_HREF, doc);
    	describeCRSOp.setName("DescribeCRS");
    	describeCRSOp.setMethodCall("DescribeCRS");
    	List<String> describeCRSOpAddressList = new ArrayList<String>();
    	describeCRSOpAddressList.add(describeCRSGet);
    	List<String> describeCRSOpPlatform = new ArrayList<String>();
    	describeCRSOpPlatform.add("HTTP GET");
    	if (describeCRSPost != null && describeCRSPost.length() != 0) {
        	describeCRSOpAddressList.add(describeCRSPost);
    		describeCRSOpPlatform.add("HTTP POST");
    	}
    	describeCRSOp.setPlatform(describeCRSOpPlatform);
    	describeCRSOp.setAddressList(describeCRSOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeCRS", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("CRSs=urn:ogc:def:crs:EPSG:6.3:4277", "Identifier URIs of one or more desired coordinate reference systems, comma separated list", "", false, false));
    	describeCRSOp.setParamList(paramList);
    	operations.add(describeCRSOp);
    	
    	// Operation - DescribeMethod
    	OperationBean describeMethodOp = new OperationBean();
    	String describeMethodGet = xPath.evaluate(XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_GET_HREF, doc);
    	String describeMethodPost = xPath.evaluate(XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_POST_HREF, doc);
    	describeMethodOp.setName("DescribeMethod");
    	describeMethodOp.setMethodCall("DescribeMethod");
    	List<String> describeMethodOpAddressList = new ArrayList<String>();
    	describeMethodOpAddressList.add(describeMethodGet);
    	List<String> describeMethodOpPlatform = new ArrayList<String>();
    	describeMethodOpPlatform.add("HTTP GET");
    	if (describeMethodPost != null && describeMethodPost.length() != 0) {
        	describeMethodOpAddressList.add(describeMethodPost);
    		describeMethodOpPlatform.add("HTTP POST");
    	}
    	describeMethodOp.setPlatform(describeMethodOpPlatform);
    	describeMethodOp.setAddressList(describeMethodOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeMethod", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.20", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("methods=urn:ogc:def:method:EPSG:6.3:9807", "Identifier URIs of one or more desired operation methods, comma separated", "", false, false));
    	describeMethodOp.setParamList(paramList);
    	operations.add(describeMethodOp);
    	
    	result.setOperations(operations);
    	return result;
    }

    private String getServiceVersionWMS(Document doc) throws XPathExpressionException {
    	// WMS Version 1.3.0
    	String serviceType = xPath.evaluate("/WMS_Capabilities/Service/Name[1]", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return "1_3_0";
    	}
    	// WMS Version 1.1.1
    	serviceType = xPath.evaluate("/WMT_MS_Capabilities/Service/Name[1]", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return "1_1_1";
    	}

    	throw new RuntimeException("Could not determine WMS Service Version.");
    }
    	
    private String getServiceVersionWCS(Document doc) throws XPathExpressionException {
    	// WCS Version 1.0.0. Doesn't have a Service or ServiceType/Name Element. Just check if WCS_Capabilities exists
    	String serviceType = xPath.evaluate("/WCS_Capabilities", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return "1_0_0";
    	}
    	// WCS Version 1.1.0
    	serviceType = xPath.evaluate("/Capabilities/ServiceIdentification/ServiceType[1]", doc);
    	if (serviceType != null && serviceType.contains(SERVICE_TYPE_WCS)) {
    		return "1_1_0";
    	}

    	throw new RuntimeException("Could not determine WCS Service Version.");
    }

    private ServiceType getServiceType(Document doc) throws XPathExpressionException {
    	// WMS Version 1.3.0
    	String serviceType = xPath.evaluate("/WMS_Capabilities/Service/Name[1]", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return ServiceType.WMS;
    	}
    	// WMS Version 1.1.1
    	serviceType = xPath.evaluate("/WMT_MS_Capabilities/Service/Name[1]", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return ServiceType.WMS;
    	}
    	// WCS Version 1.0.0. Doesn't have a Service or ServiceType/Name Element. Just check if WCS_Capabilities exists
    	serviceType = xPath.evaluate("/WCS_Capabilities", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return ServiceType.WCS;
    	}
    	// WFS
    	serviceType = xPath.evaluate("/WFS_Capabilities/ServiceIdentification/ServiceType[1]", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		return ServiceType.WFS;
    	}

    	// All other services have can be evaluated via '/Capabilities/ServiceIdentification/ServiceType[1]'
    	serviceType = xPath.evaluate("/Capabilities/ServiceIdentification/ServiceType[1]", doc);
    	if (serviceType != null && serviceType.length() != 0) {
    		if (serviceType.contains(SERVICE_TYPE_WMS)) {
    			return ServiceType.WMS;

    		} else if (serviceType.contains(SERVICE_TYPE_WFS)) {
    			return ServiceType.WFS;

    		} else if (serviceType.contains(SERVICE_TYPE_WCS)) {
    			return ServiceType.WCS;

    		} else if (serviceType.contains(SERVICE_TYPE_CSW)) {
    			return ServiceType.CSW;

    		} else if (serviceType.contains(SERVICE_TYPE_WCTS)) {
    			return ServiceType.WCTS;

    		} else {
        		log.debug("Invalid service type: "+serviceType);
        		throw new RuntimeException("Invalid service type: "+serviceType);
    		}

    	} else {
			// Could not determine ServiceType
    		log.debug("Could not evaluate service type.");
    		throw new RuntimeException("Could not evaluate service type.");
    	}
    }


    private static String getXPathExpressionFor(ServiceType serviceType, String ver, String postfix) {
    	String fieldId = "XPATH_EXP_"+serviceType+"_"+ver+"_"+postfix;
    	try {
    		final Field fields[] = GetCapabilitiesService.class.getDeclaredFields();
    		for (Field f : fields) {
    	      if (fieldId.equals(f.getName())) {
    	        f.setAccessible(true);
    	        return (String) f.get(null);
    	      }
    	    }

    	} catch (IllegalAccessException e) {
    		log.debug("Could not access field for xpathExpression.", e);
    	}

    	return null;
    }
}