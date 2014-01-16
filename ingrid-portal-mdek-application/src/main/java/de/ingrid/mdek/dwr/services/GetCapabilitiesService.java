package de.ingrid.mdek.dwr.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.dwr.services.capabilities.CapabilitiesParserFactory;
import de.ingrid.mdek.dwr.services.capabilities.ICapabilitiesParser;


public class GetCapabilitiesService {

	private final static Logger log = Logger.getLogger(GetCapabilitiesService.class);	

    private static String ERROR_GETCAP_INVALID_URL = "ERROR_GETCAP_INVALID_URL";
    private static String ERROR_GETCAP = "ERROR_GETCAP_ERROR";

	@Autowired
	private SysListCache sysListMapper;
	
    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
    }

    public CapabilitiesBean getCapabilities(String urlStr) {
        
    	try {
    		URL url = new URL(urlStr);
    		// get the content in UTF-8 format, to avoid "MalformedByteSequenceException: Invalid byte 1 of 1-byte UTF-8 sequence"
    		Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
    		InputSource inputSource = new InputSource(reader);

        	// Build a document from the xml response
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	// nameSpaceAware is false by default. Otherwise we would have to query for the correct namespace for every evaluation
        	factory.setNamespaceAware(true);
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document doc = builder.parse(inputSource);

        	return getCapabilitiesData(doc);

    	} catch (MalformedURLException e) {
    		log.debug("URL is malformed: " + urlStr, e);
    		throw new RuntimeException(ERROR_GETCAP_INVALID_URL, e);

    	} catch (IOException e) {
    		log.debug("IO-Exception occured with url: " + urlStr, e);
    		throw new RuntimeException(ERROR_GETCAP, e);

    	} catch (Exception e) {
    		log.debug("A general exception occured with url: " + urlStr, e);
    		throw new RuntimeException(ERROR_GETCAP, e);
    	}    
    }

    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        ICapabilitiesParser capDoc = CapabilitiesParserFactory.getDocument(doc, sysListMapper);
        return capDoc.getCapabilitiesData(doc);
    }
    
    
    /*
    // not used anymore (INGRID-2215)
    private String appendVersionParameterToWmsServiceUrl(String baseUrl, String version) {
        StringBuilder url = appendUrlRequestParameterSymbols(baseUrl);
    	
    	url.append("version="+version);
    	return url.toString();
	}

    
	private String appendGetCapabilitiesParameterToWmsServiceUrl(String baseUrl, String version) {
    	StringBuilder url = appendUrlRequestParameterSymbols(baseUrl);

    	url.append("SERVICE=WMS&REQUEST=GetCapabilities&version="+version);
    	return url.toString();
    }
	
	private StringBuilder appendUrlRequestParameterSymbols(String url) {
	    StringBuilder paramUrl = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            paramUrl.append('?');
        } else if ((url.lastIndexOf("&") != url.length() - 1) && (url.lastIndexOf("?") != url.length() - 1)) {
            paramUrl.append('&');
        }
        return paramUrl;
	}
	*/

/*
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

    	// Keywords
    	List<String> keywords = getKeywords(doc);
    	result.setKeywords(keywords);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WFS_OP_GET_CAPABILITIES_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET });
    	getCapabilitiesOp.setName("GetCapabilities");
    	getCapabilitiesOp.setMethodCall("GetCapabilities");

    	List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("VERSION=version", "Request version", "", true, false));
    	paramList.add(new OperationParameterBean("SERVICE=WFS", "Service type", "", false, false));
    	paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("UPDATESEQUENCE=string", "Sequence number or string for cache control", "", true, false));
    	getCapabilitiesOp.setParamList(paramList);
    	operations.add(getCapabilitiesOp);

    	// Operation - DescribeFeatureType
    	OperationBean describeFeatureTypeOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_GET_HREF, XPATH_EXP_WFS_OP_DESCRIBE_FEATURE_TYPE_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	describeFeatureTypeOp.setName("DescribeFeatureType");
    	describeFeatureTypeOp.setMethodCall("DescribeFeatureType");

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("REQUEST=DescribeFeatureType", "Name of request", "", false, false));
    	paramList.add(new OperationParameterBean("TYPENAME", "A comma separated list of feature types to describe. If no value is specified that is to be interpreted as all feature types", "", true, false));
    	paramList.add(new OperationParameterBean("OUTPUTFORMAT", "The output format to use to describe feature types. text/xml; subtype=gml/3.1.1 must be supported. Other output formats, such as DTD are possible", "", true, false));
    	describeFeatureTypeOp.setParamList(paramList);
    	operations.add(describeFeatureTypeOp);

    	// Operation - GetFeature
    	OperationBean getFeatureOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WFS_OP_GET_FEATURE_GET_HREF, XPATH_EXP_WFS_OP_GET_FEATURE_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	getFeatureOp.setName("GetFeature");
    	getFeatureOp.setMethodCall("GetFeature");

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("outputFormat=GML2", "This value is kept for backward compatability and indicates that an XML instance document must be generated that validates against a GML2 application schema", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat=text/xml; subtype=gml/2.1.2", "Same as GML2", "", true, false));
    	paramList.add(new OperationParameterBean("outputFormat=text/xml; subtype=gml/3.1.1; subtype=gml/2.1.2", "This value indicates that an XML instance document must be generated that validates against a GML3 application schema. This is the default values of the outputFormat attribute if the attribute is not specified in the GetFeature request", "", true, false));
    	paramList.add(new OperationParameterBean("resultType=Results", "The default value results indicates that a web feature service should generate a complete response that contains all the features that satisfy the request. The exact structure of the response is defined in clause 9.3", "", true, false));
    	paramList.add(new OperationParameterBean("resultType=Hits", "The value hits indicates that a web feature service should process the GetFeature request and rather than return the entire result set, it should simply indicate the number of feature instance of the requested feature type(s) that satisfy the request. That is that the count should only include instances of feature types specified in the typeName attribute (i.e. GetFeature/Query/@typeName). The exact way in which the feature count is conveyed to a client application is described in clause 9.3", "", true, false));

    	getFeatureOp.setParamList(paramList);
    	operations.add(getFeatureOp);

    	// Operation - GetGmlObject - optional
    	OperationBean getGmlObjectOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WFS_OP_GET_GML_OBJECT_GET_HREF, XPATH_EXP_WFS_OP_GET_GML_OBJECT_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	if (getGmlObjectOp.getAddressList().size() > 0) {
	    	getGmlObjectOp.setName("GetGmlObject");
	    	getGmlObjectOp.setMethodCall("GetGmlObject");

	    	paramList = new ArrayList<OperationParameterBean>();
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
    	if (lockFeatureOp.getAddressList().size() > 0) {
	    	lockFeatureOp.setName("LockFeature");
	    	lockFeatureOp.setMethodCall("LockFeature");

	    	paramList = new ArrayList<OperationParameterBean>();
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
    	if (transactionOp.getAddressList().size() > 0) {
	    	transactionOp.setName("Transaction");
	    	transactionOp.setMethodCall("Transaction");

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
*/
/*
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

    	// Keywords
    	List<String> keywords = getKeywords(doc);
    	result.setKeywords(keywords);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();


    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = mapToOperationBean(doc,
        		new String[]{
    				getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_CAPABILITIES_GET_HREF"),
    				getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_CAPABILITIES_POST_HREF") },
           		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
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


    	// Operation - DescribeCoverage
    	OperationBean describeCoverageOp = mapToOperationBean(doc,
        		new String[]{
    				getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_DESCRIBE_COVERAGE_GET_HREF"),
    				getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_DESCRIBE_COVERAGE_POST_HREF") },
           		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	describeCoverageOp.setName("DescribeCoverage");
    	describeCoverageOp.setMethodCall("DescribeCoverage");

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCS", "Service name. Shall be WCS", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeCoverage", "Request name. Shall be DescribeCoverage", "", false, false));
    	paramList.add(new OperationParameterBean("version=1.1.2", "Request protocol version", "", false, false));
    	paramList.add(new OperationParameterBean("identifiers=identifier1, identifier2, ...", "A comma-separated list of coverage identifiers to describe (identified by their identifier values in the Capabilities document)", "", false, false));
    	describeCoverageOp.setParamList(paramList);
    	operations.add(describeCoverageOp);

    	
    	// Operation - GetCoverage
    	OperationBean getCoverageOp = mapToOperationBean(doc,
        		new String[]{
    				getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_COVERAGE_GET_HREF"),
    				getXPathExpressionFor(ServiceType.WCS, serviceVersion, "OP_GET_COVERAGE_POST_HREF") },
           		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	getCoverageOp.setName("GetCoverage");
    	getCoverageOp.setMethodCall("GetCoverage");

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
*/

/*
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

    	// Keywords
    	List<String> keywords = getKeywords(doc);
    	result.setKeywords(keywords);

    	// Operation List
    	List<OperationBean> operations = new ArrayList<OperationBean>();

    	// Operation - GetCapabilities
    	OperationBean getCapabilitiesOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WCTS_OP_GET_CAPABILITIES_GET_HREF, XPATH_EXP_WCTS_OP_GET_CAPABILITIES_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
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


    	// Operation - Transform
    	OperationBean transformOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WCTS_OP_TRANSFORM_GET_HREF, XPATH_EXP_WCTS_OP_TRANSFORM_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	transformOp.setName("Transform");
    	transformOp.setMethodCall("Transform");

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
    	OperationBean isTransformableOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_GET_HREF, XPATH_EXP_WCTS_OP_IS_TRANSFORMABLE_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	isTransformableOp.setName("IsTransformable");
    	isTransformableOp.setMethodCall("IsTransformable");

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
    	OperationBean getTransformationOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_GET_HREF, XPATH_EXP_WCTS_OP_GET_TRANSFORMATION_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	getTransformationOp.setName("GetTransformation");
    	getTransformationOp.setMethodCall("GetTransformation");

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=GetTransformation", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.20", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("SourceCRS=urn:ogc:def:crs:EPSG:6.3:4326", "Identifier URI of input coordinate reference system", "", false, false));
    	paramList.add(new OperationParameterBean("TargetCRS=urn:ogc:def:crs:EPSG:6.3:32611", "Identifier URI of desired output coordinate reference system", "", false, false));
    	getTransformationOp.setParamList(paramList);
    	operations.add(getTransformationOp);

    	// Operation - DescribeTransformation
    	OperationBean describeTransformationOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_GET_HREF, XPATH_EXP_WCTS_OP_DESCRIBE_TRANSFORMATION_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	describeTransformationOp.setName("DescribeTransformation");
    	describeTransformationOp.setMethodCall("DescribeTransformation");

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeTransformation", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("Transformations=urn:ogc:def:coordinateOperation:EPSG:6.3:19916", "Identifier URIs of one or more coordinate operations, comma-separated list", "", false, false));
    	describeTransformationOp.setParamList(paramList);
    	operations.add(describeTransformationOp);

    	// Operation - DescribeCRS
    	OperationBean describeCRSOp = mapToOperationBean(doc,
        		new String[]{ XPATH_EXP_WCTS_OP_DESCRIBE_CRS_GET_HREF, XPATH_EXP_WCTS_OP_DESCRIBE_CRS_POST_HREF },
        		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });

    	describeCRSOp.setName("DescribeCRS");
    	describeCRSOp.setMethodCall("DescribeCRS");

    	paramList = new ArrayList<OperationParameterBean>();
    	paramList.add(new OperationParameterBean("service=WCTS", "Service type identifier", "", false, false));
    	paramList.add(new OperationParameterBean("request=DescribeCRS", "Operation name", "", false, false));
    	paramList.add(new OperationParameterBean("version=0.0.0", "Specification and schema version for this operation", "", false, false));
    	paramList.add(new OperationParameterBean("CRSs=urn:ogc:def:crs:EPSG:6.3:4277", "Identifier URIs of one or more desired coordinate reference systems, comma separated list", "", false, false));
    	describeCRSOp.setParamList(paramList);
    	operations.add(describeCRSOp);
    	
    	// Operation - DescribeMethod
    	OperationBean describeMethodOp = mapToOperationBean(doc,
    		new String[]{ XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_GET_HREF, XPATH_EXP_WCTS_OP_DESCRIBE_METHOD_POST_HREF },
    		new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
    	describeMethodOp.setName("DescribeMethod");
    	describeMethodOp.setMethodCall("DescribeMethod");

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
*/
    /** Length of passed xPathsOfMethods and platformsOfMethods has to be the same !!! */


    
    

    public SysListCache getSysListMapper() {
        return sysListMapper;
    }

    public void setSysListMapper(SysListCache syslistCache) {
        this.sysListMapper = syslistCache;
    }

}