package de.ingrid.mdek.dwr.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
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

	private final static String SERVICE_TYPE_WMS = "OGC:WMS";
	private final static String SERVICE_TYPE_WFS = "WFS";
	private final static String SERVICE_TYPE_WCS = "WCS";

    private static String ERROR_GETCAP_INVALID_URL = "ERROR_GETCAP_INVALID_URL";
    private static String ERROR_GETCAP_XPATH = "ERROR_GETCAP_XPATH";
    private static String ERROR_GETCAP = "ERROR_GETCAP_ERROR";

	private final static String XPATH_EXP_WMS_TITLE = "/WMT_MS_Capabilities/Service[1]/Title[1]";
	private final static String XPATH_EXP_WMS_ABSTRACT = "/WMT_MS_Capabilities/Service[1]/Abstract[1]";
	private final static String XPATH_EXP_WMS_VERSION = "/WMT_MS_Capabilities/@version";
	private final static String XPATH_EXP_WMS_OP_GET_CAPABILITIES_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetCapabilities[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	private final static String XPATH_EXP_WMS_OP_GET_MAP_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetMap[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	private final static String XPATH_EXP_WMS_OP_GET_FEATURE_INFO_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetFeatureInfo[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";

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

	private final static String XPATH_EXP_WCS_TITLE = "/Capabilities/ServiceIdentification[1]/Title[1]";
	private final static String XPATH_EXP_WCS_ABSTRACT = "/Capabilities/ServiceIdentification[1]/Title[1]";
	private final static String XPATH_EXP_WCS_VERSION = "/Capabilities/@version";

	private final static String XPATH_EXP_WCS_OP_GET_CAPABILITIES_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCS_OP_GET_CAPABILITIES_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCapabilities']/DCP[1]/HTTP[1]/Post[1]/@href";

	private final static String XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeCoverage']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='DescribeCoverage']/DCP[1]/HTTP[1]/Post[1]/@href";
	
	private final static String XPATH_EXP_WCS_OP_GET_COVERAGE_GET_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCoverage']/DCP[1]/HTTP[1]/Get[1]/@href";
	private final static String XPATH_EXP_WCS_OP_GET_COVERAGE_POST_HREF = "/Capabilities/OperationsMetadata[1]/Operation[@name='GetCoverage']/DCP[1]/HTTP[1]/Post[1]/@href";

	private XPath xPath = null;
	
    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
    	XPathFactory factory = XPathFactory.newInstance();
    	xPath = factory.newXPath();
    }

    public String getHtmlBody(String urlStr) {
    	try {
	    	URL url = new URL(urlStr);
        	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        	char[] cbuf = new char[65536];
        	StringBuffer stringbuf = new StringBuffer();

        	int read_this_time = 0;
        	while (read_this_time != -1) {
        		read_this_time = in.read(cbuf, 0, 65536);
        		if (read_this_time != -1)
        			stringbuf.append(cbuf, 0, read_this_time);
        	}

        	String result = stringbuf.toString();
        	int beginIndex = result.toLowerCase().indexOf("<body");
        	beginIndex = result.indexOf('>', beginIndex)+1;
        	int endIndex = result.toLowerCase().indexOf("</body>");
        	if (beginIndex < 0 || beginIndex >= result.length()) {
        		beginIndex = 0;
        	}
        	if (endIndex < 0 || endIndex >= result.length()) {
        		endIndex = result.length() - 1;
        	}

        	result = result.substring(beginIndex, endIndex);
        	return result;
        	
    	} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
    	}
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

        	// Check for the service type. First evaluate the wms xPath expr., then the wfs
        	String serviceType = null;
        	XPathExpression xeWmsServiceType = xPath.compile("/WMT_MS_Capabilities/Service/Name[1]");
        	serviceType = xeWmsServiceType.evaluate(doc);

        	if (serviceType == null || serviceType.length() == 0) {
            	XPathExpression xeWfsServiceType = xPath.compile("/WFS_Capabilities/ServiceIdentification/ServiceType[1]");
            	serviceType = xeWfsServiceType.evaluate(doc);
        	}

        	if (serviceType == null || serviceType.length() == 0) {
            	XPathExpression xeWcsServiceType = xPath.compile("/Capabilities/ServiceIdentification/ServiceType[1]");
            	serviceType = xeWcsServiceType.evaluate(doc);
        	}

        	if (serviceType == null || serviceType.length() == 0) {
        		// Could not evaluate serviceType
        		log.debug("Could not evaluate service type.");
//        		throw new RuntimeException(ERROR_GETCAP);
        		throw new RuntimeException("Could not evaluate service type.");

        	} else if (serviceType.contains(SERVICE_TYPE_WMS)) {
        		return getCapabilitiesWMS(doc);

        	} else if (serviceType.contains(SERVICE_TYPE_WFS)) {
            	return getCapabilitiesWFS(doc);        		

        	} else if (serviceType.contains(SERVICE_TYPE_WCS)) {
            	return getCapabilitiesWCS(doc);        		

        	} else {
        		log.debug("Invalid service type: "+serviceType);
//        		throw new RuntimeException(ERROR_GETCAP);
        		throw new RuntimeException("Invalid service type: "+serviceType);
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

    public CapabilitiesBean getCapabilitiesWMS(Document doc) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WMS");
    	result.setTitle(xPath.evaluate(XPATH_EXP_WMS_TITLE, doc));
    	result.setDescription(xPath.evaluate(XPATH_EXP_WMS_ABSTRACT, doc));
    	String version = xPath.evaluate(XPATH_EXP_WMS_VERSION, doc);
    	ArrayList<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	ArrayList<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	getCapabilitiesOp.setName("GetCapabilities");
    	ArrayList<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	ArrayList<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(xPath.evaluate(XPATH_EXP_WMS_OP_GET_CAPABILITIES_HREF, doc));
    	getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);
    	
    	ArrayList<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("VERSION=version", "Request version", "", true, false));
    	paramList.add(new OperationParameterBean("SERVICE=WMS", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Request name", "", false, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=number", "Sequence number for cache control", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);

    	// Operation - GetMap
    	OperationBean getMapOp = new OperationBean();
    	getMapOp.setName("GetMap");
    	ArrayList<String> getMapOpPlatform = new ArrayList<String>();
    	getMapOpPlatform.add("HTTP GET");
    	getMapOp.setPlatform(getMapOpPlatform);
    	getMapOp.setMethodCall("GetMap");
    	ArrayList<String> getMapOpAddressList = new ArrayList<String>();
    	getMapOpAddressList.add(xPath.evaluate(XPATH_EXP_WMS_OP_GET_MAP_HREF, doc));
    	getMapOp.setAddressList(getMapOpAddressList);

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("VERSION=version", "Request version", "", false, false));
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
    	String getFeatureInfoAddress = xPath.evaluate(XPATH_EXP_WMS_OP_GET_FEATURE_INFO_HREF, doc);
    	if (getFeatureInfoAddress != null && getFeatureInfoAddress.length() != 0) {
	    	OperationBean getFeatureInfoOp = new OperationBean();
	    	getFeatureInfoOp.setName("GetFeatureInfo");
	    	ArrayList<String> getFeatureInfoOpPlatform = new ArrayList<String>();
	    	getFeatureInfoOpPlatform.add("HTTP GET");
	    	getFeatureInfoOp.setPlatform(getFeatureInfoOpPlatform);
	    	getFeatureInfoOp.setMethodCall("GetFeatureInfo");
	    	ArrayList<String> getFeatureInfoOpAddressList = new ArrayList<String>();
	    	getFeatureInfoOpAddressList.add(getFeatureInfoAddress);
	    	getFeatureInfoOp.setAddressList(getFeatureInfoOpAddressList);
	
	    	paramList = new ArrayList<OperationParameterBean>();
	    	paramList.add(new OperationParameterBean("VERSION=version", "Request version", "", false, false));
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

    public CapabilitiesBean getCapabilitiesWFS(Document doc) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WFS");
    	result.setTitle(xPath.evaluate(XPATH_EXP_WFS_TITLE, doc));
    	result.setDescription(xPath.evaluate(XPATH_EXP_WFS_ABSTRACT, doc));
    	String version = xPath.evaluate(XPATH_EXP_WFS_VERSION, doc);
    	ArrayList<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	ArrayList<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	getCapabilitiesOp.setName("GetCapabilities");
    	ArrayList<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	ArrayList<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(xPath.evaluate(XPATH_EXP_WFS_OP_GET_CAPABILITIES_HREF, doc));
    	getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

    	ArrayList<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
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
    	ArrayList<String> describeFeatureTypeOpAddressList = new ArrayList<String>();
    	describeFeatureTypeOpAddressList.add(describeFeatureTypeGet);
    	ArrayList<String> describeFeatureTypeOpPlatform = new ArrayList<String>();
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
    	ArrayList<String> getFeatureOpAddressList = new ArrayList<String>();
    	getFeatureOpAddressList.add(getFeatureGet);
    	ArrayList<String> getFeatureOpPlatform = new ArrayList<String>();
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
	    	ArrayList<String> getGmlObjectAddressList = new ArrayList<String>();
	    	getGmlObjectAddressList.add(getGmlObjectGet);
	    	ArrayList<String> getGmlObjectOpPlatform = new ArrayList<String>();
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
	    	ArrayList<String> lockFeatureAddressList = new ArrayList<String>();
	    	lockFeatureAddressList.add(lockFeatureGet);
	    	ArrayList<String> lockFeatureOpPlatform = new ArrayList<String>();
	    	lockFeatureOpPlatform.add("HTTP GET");
	    	if (lockFeaturePost != null && lockFeaturePost.length() != 0) {
	        	lockFeatureAddressList.add(lockFeaturePost);
	    		lockFeatureOpPlatform.add("HTTP POST");
	    	}
	    	lockFeatureOp.setPlatform(lockFeatureOpPlatform);
	    	lockFeatureOp.setAddressList(lockFeatureAddressList);

	    	paramList = new ArrayList<OperationParameterBean>();
	    	paramList.add(new OperationParameterBean("REQUEST=[GetGmlObject]", "The name of the WFS request", "", false, false));
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
	    	ArrayList<String> transactionAddressList = new ArrayList<String>();
	    	transactionAddressList.add(transactionGet);
	    	ArrayList<String> transactionOpPlatform = new ArrayList<String>();
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


    public CapabilitiesBean getCapabilitiesWCS(Document doc) throws XPathExpressionException {
    	CapabilitiesBean result = new CapabilitiesBean();

    	// General settings
    	result.setServiceType("WCS");
    	result.setTitle(xPath.evaluate(XPATH_EXP_WCS_TITLE, doc));
    	result.setDescription(xPath.evaluate(XPATH_EXP_WCS_ABSTRACT, doc));
    	String version = xPath.evaluate(XPATH_EXP_WCS_VERSION, doc);
    	ArrayList<String> versions = new ArrayList<String>();
    	versions.add(version);
    	result.setVersions(versions);

    	// Operation List
    	ArrayList<OperationBean> operations = new ArrayList<OperationBean>();


    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = new OperationBean();
    	String getCapabilitiesGet = xPath.evaluate(XPATH_EXP_WCS_OP_GET_CAPABILITIES_GET_HREF, doc);
    	String getCapabilitiesPost = xPath.evaluate(XPATH_EXP_WCS_OP_GET_CAPABILITIES_POST_HREF, doc);
    	getCapabilitiesOp.setName("GetCapabilities");
    	getCapabilitiesOp.setMethodCall("GetCapabilities");
    	ArrayList<String> getCapabilitiesOpAddressList = new ArrayList<String>();
    	getCapabilitiesOpAddressList.add(getCapabilitiesGet);
    	ArrayList<String> getCapabilitiesOpPlatform = new ArrayList<String>();
    	getCapabilitiesOpPlatform.add("HTTP GET");
    	if (getCapabilitiesPost != null && getCapabilitiesPost.length() != 0) {
    		getCapabilitiesOpAddressList.add(getCapabilitiesPost);
    		getCapabilitiesOpPlatform.add("HTTP POST");
    	}
		getCapabilitiesOp.setPlatform(getCapabilitiesOpPlatform);
		getCapabilitiesOp.setAddressList(getCapabilitiesOpAddressList);

    	ArrayList<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("SERVICE=WCS", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
    	paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is “increased” whenever any change is made in complete service metadata document", "", true, false));
    	paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);


    	// Operation - DescribeCoverage
    	OperationBean describeCoverageOp = new OperationBean();
    	String describeCoverageGet = xPath.evaluate(XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_GET_HREF, doc);
    	String describeCoveragePost = xPath.evaluate(XPATH_EXP_WCS_OP_DESCRIBE_COVERAGE_POST_HREF, doc);
    	describeCoverageOp.setName("DescribeCoverage");
    	describeCoverageOp.setMethodCall("DescribeCoverage");
    	ArrayList<String> describeCoverageOpAddressList = new ArrayList<String>();
    	describeCoverageOpAddressList.add(describeCoverageGet);
    	ArrayList<String> describeCoverageOpPlatform = new ArrayList<String>();
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
    	String getCoverageGet = xPath.evaluate(XPATH_EXP_WCS_OP_GET_COVERAGE_GET_HREF, doc);
    	String getCoveragePost = xPath.evaluate(XPATH_EXP_WCS_OP_GET_COVERAGE_POST_HREF, doc);
    	getCoverageOp.setName("GetCoverage");
    	getCoverageOp.setMethodCall("GetCoverage");
    	ArrayList<String> getCoverageOpAddressList = new ArrayList<String>();
    	getCoverageOpAddressList.add(getCoverageGet);
    	ArrayList<String> getCoverageOpPlatform = new ArrayList<String>();
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
}