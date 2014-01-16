/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import de.ingrid.mdek.MdekUtils.AddressType;
import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.OperationParameterBean;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class CswCapabilitiesParser extends GeneralCapabilitiesParser implements ICapabilitiesParser {
    
    private static final String XPATH_EXP_CSW_EXTENDED_CAPABILITIES = "/csw:Capabilities/ows:OperationsMetadata/inspire_ds:ExtendedCapabilities";
    private static final String XPATH_EXT_CSW_SERVICECONTACT = "/csw:Capabilities/ows:ServiceProvider/ows:ServiceContact";
    private static final String XPATH_EXP_CSW_KEYWORDS = "/csw:Capabilities/ows:ServiceIdentification/ows:Keywords/ows:Keyword/text()";
    private final static String XPATH_EXP_CSW_TITLE = "/csw:Capabilities/ows:ServiceIdentification[1]/ows:Title[1]";
    private final static String XPATH_EXP_CSW_ABSTRACT = "/csw:Capabilities/ows:ServiceIdentification[1]/ows:Abstract[1]";
    private final static String XPATH_EXP_CSW_VERSION = "/csw:Capabilities/@version";

    private final static String XPATH_EXP_CSW_OP_GET_CAPABILITIES_GET_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetCapabilities']/ows:DCP[1]/ows:HTTP[1]/ows:Get[1]/@xlink:href";
    private final static String XPATH_EXP_CSW_OP_GET_CAPABILITIES_POST_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetCapabilities']/ows:DCP[1]/ows:HTTP[1]/ows:Post[1]/@xlink:href";

    private final static String XPATH_EXP_CSW_OP_DESCRIBE_RECORD_GET_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='DescribeRecord']/ows:DCP[1]/ows:HTTP[1]/ows:Get[1]/@xlink:href";
    private final static String XPATH_EXP_CSW_OP_DESCRIBE_RECORD_POST_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='DescribeRecord']/ows:DCP[1]/ows:HTTP[1]/ows:Post[1]/@xlink:href";
    
    private final static String XPATH_EXP_CSW_OP_GET_DOMAIN_GET_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetDomain']/ows:DCP[1]/ows:HTTP[1]/ows:Get[1]/@xlink:href";
    private final static String XPATH_EXP_CSW_OP_GET_DOMAIN_POST_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetDomain']/ows:DCP[1]/ows:HTTP[1]/ows:Post[1]/@xlink:href";

    private final static String XPATH_EXP_CSW_OP_GET_RECORDS_GET_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetRecords']/ows:DCP[1]/ows:HTTP[1]/ows:Get[1]/@xlink:href";
    private final static String XPATH_EXP_CSW_OP_GET_RECORDS_POST_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetRecords']/ows:DCP[1]/ows:HTTP[1]/ows:Post[1]/@xlink:href";

    private final static String XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_GET_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetRecordById']/ows:DCP[1]/ows:HTTP[1]/ows:Get[1]/@xlink:href";
    private final static String XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_POST_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='GetRecordById']/ows:DCP[1]/ows:HTTP[1]/ows:Post[1]/@xlink:href";

    private final static String XPATH_EXP_CSW_OP_HARVEST_GET_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='Harvest']/ows:DCP[1]/ows:HTTP[1]/ows:Get[1]/@xlink:href";
    private final static String XPATH_EXP_CSW_OP_HARVEST_POST_HREF = "/csw:Capabilities/ows:OperationsMetadata[1]/ows:Operation[@name='Harvest']/ows:DCP[1]/ows:HTTP[1]/ows:Post[1]/@xlink:href";
    
    private final static String XPATH_EXP_CSW_FEES = "/csw:Capabilities/ows:ServiceIdentification/ows:Fees";
    private final static String XPATH_EXP_CSW_ACCESS_CONSTRAINTS = "/csw:Capabilities/ows:ServiceIdentification/ows:AccessConstraints";
    private final static String XPATH_EXP_CSW_ONLINE_RESOURCE = "/csw:Capabilities/ows:ServiceProvider/ows:ServiceContact/ows:ContactInfo/ows:OnlineResource";
    
    public CswCapabilitiesParser(SysListCache syslistCache) {
        super(new XPathUtils(new Csw202NamespaceContext()), syslistCache);
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.capabilities.ICapabilityDocument#setTitle(org.w3c.dom.Document)
     */
    @Override
    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        CapabilitiesBean result = new CapabilitiesBean();
        
        // General settings
        result.setServiceType("CSW");
        result.setDataServiceType(1); // discovery
        result.setTitle(xPathUtils.getString(doc, XPATH_EXP_CSW_TITLE));
        result.setDescription(xPathUtils.getString(doc, XPATH_EXP_CSW_ABSTRACT));
        result.setVersions(getNodesContentAsList(doc, XPATH_EXP_CSW_VERSION));
        
        // Fees
        result.setFees(xPathUtils.getString(doc, XPATH_EXP_CSW_FEES));
        
        // Access Constraints
        result.setAccessConstraints(getNodesContentAsList(doc, XPATH_EXP_CSW_ACCESS_CONSTRAINTS));
        
        // Online Resources
        result.setOnlineResources(getOnlineResources(doc, XPATH_EXP_CSW_ONLINE_RESOURCE));

        // add extended capabilities to the bean which contains even more information to be used
        addExtendedCapabilities(result, doc, XPATH_EXP_CSW_EXTENDED_CAPABILITIES);
        
        // Keywords
        List<String> keywords = getKeywords(doc, XPATH_EXP_CSW_KEYWORDS);
        
        // add found keywords to our result bean
        result.getKeywords().addAll(keywords);
        
        // get contact information
        result.setAddress(getAddress(doc));
        
        // Operation List
        List<OperationBean> operations = new ArrayList<OperationBean>();

        // Operation - GetCapabilities
        OperationBean getCapabilitiesOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_CSW_OP_GET_CAPABILITIES_GET_HREF, XPATH_EXP_CSW_OP_GET_CAPABILITIES_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getCapabilitiesOp.getAddressList().isEmpty()) {
            getCapabilitiesOp.setName("GetCapabilities");
            getCapabilitiesOp.setMethodCall("GetCapabilities");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("SERVICE=CSW", "Service type", "", false, false));
            paramList.add(new OperationParameterBean("REQUEST=GetCapabilities", "Name of request", "", false, false));
            paramList.add(new OperationParameterBean("ACCEPTVERSIONS=1.0.0,0.8.3", "Comma-separated prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first", "", true, false));
            paramList.add(new OperationParameterBean("SECTIONS=Contents", "Comma-separated unordered list of zero or more names of sections of service metadata document to be returned in service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("UPDATESEQUENCE=XXX (where XXX is character string previously provided by server)", "Service metadata document version, value is \"increased\" whenever any change is made in complete service metadata document", "", true, false));
            paramList.add(new OperationParameterBean("ACCEPTFORMATS= text/xml", "Comma-separated prioritized sequence of zero or more response formats desired by client, with preferred formats listed first", "", true, false));
            getCapabilitiesOp.setParamList(paramList);
            operations.add(getCapabilitiesOp);
        }


        // Operation - DescribeRecord
        OperationBean describeRecordOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_CSW_OP_DESCRIBE_RECORD_GET_HREF, XPATH_EXP_CSW_OP_DESCRIBE_RECORD_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!describeRecordOp.getAddressList().isEmpty()) {
            describeRecordOp.setName("DescribeRecord");
            describeRecordOp.setMethodCall("DescribeRecord");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=CSW", "Service name. Shall be CSW", "", false, false));
            paramList.add(new OperationParameterBean("request=DescribeRecord", "Fixed value of DescribeRecord, case insensitive", "", false, false));
            paramList.add(new OperationParameterBean("version=2.0.2", "Fixed value of 2.0.2", "", false, false));
            paramList.add(new OperationParameterBean("NAMESPACE", "List of Character String, comma separated. Used to specify namespace(s) and their prefix(es). Format is xmlns([prefix=]namespace-url). If prefix is not specified, then this is the default namespace.", "", true, false));
            paramList.add(new OperationParameterBean("TypeName", "List of Character String, comma separated. One or more qualified type names to be described", "", true, false));
            paramList.add(new OperationParameterBean("outputFormat", "Character String. A MIME type indicating the format that the output document should have", "", true, false));
            paramList.add(new OperationParameterBean("schemaLanguage", "Character String", "", true, false));
            describeRecordOp.setParamList(paramList);
            operations.add(describeRecordOp);
        }


        // Operation - GetDomain
        OperationBean getDomainOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_CSW_OP_GET_DOMAIN_GET_HREF, XPATH_EXP_CSW_OP_GET_DOMAIN_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getDomainOp.getAddressList().isEmpty()) {
            getDomainOp.setName("GetDomain");
            getDomainOp.setMethodCall("GetDomain");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("service=CSW", "Service name. Shall be CSW", "", false, false));
            paramList.add(new OperationParameterBean("request=GetDomain", "Fixed value of GetDomain, case insensitive", "", false, false));
            paramList.add(new OperationParameterBean("version=2.0.2", "Fixed value of 2.0.2", "", false, false));
            paramList.add(new OperationParameterBean("ParameterName", "List of Character String, comma separated. Unordered list of names of requested parameters, of the form OperationName.ParameterName", "", true, false));
            paramList.add(new OperationParameterBean("PropertyName", "List of Character String, comma separated. Unordered list of names of requested properties, from the information model that the catalogue is using", "", true, false));
            getDomainOp.setParamList(paramList);
            operations.add(getDomainOp);
        }

        // Operation - GetRecords
        OperationBean getRecordsOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_CSW_OP_GET_RECORDS_GET_HREF, XPATH_EXP_CSW_OP_GET_RECORDS_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getRecordsOp.getAddressList().isEmpty()) {
            getRecordsOp.setName("GetRecords");
            getRecordsOp.setMethodCall("GetRecords");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
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
        }
        
        // Operation - GetRecordById
        OperationBean getRecordByIdOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_GET_HREF, XPATH_EXP_CSW_OP_GET_RECORD_BY_ID_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!getRecordByIdOp.getAddressList().isEmpty()) {
            getRecordByIdOp.setName("GetRecordById");
            getRecordByIdOp.setMethodCall("GetRecordById");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
            paramList.add(new OperationParameterBean("request=GetRecordById", "Fixed value of GetRecordById, case insensitive", "", false, false));
            paramList.add(new OperationParameterBean("Id", "Comma separated list of anyURI", "", false, false));
            paramList.add(new OperationParameterBean("ElementSetName", "CodeList with allowed values: 'brief', 'summary' or 'full'", "", true, false));
            paramList.add(new OperationParameterBean("outputFormat", "Character String. Value is Mime type. The only value that is required to be supported is application/xml. Other supported values may include text/html and text/plain", "", true, false));
            paramList.add(new OperationParameterBean("outputSchema", "Reference to the preferred schema of the response", "", true, false));
            getRecordByIdOp.setParamList(paramList);
            operations.add(getRecordByIdOp);
        }

        // Operation - Harvest
        OperationBean harvestOp = mapToOperationBean(doc,
                new String[]{ XPATH_EXP_CSW_OP_HARVEST_GET_HREF, XPATH_EXP_CSW_OP_HARVEST_POST_HREF },
                new Integer[]{ ID_OP_PLATFORM_HTTP_GET, ID_OP_PLATFORM_HTTP_POST });
        if (!harvestOp.getAddressList().isEmpty()) {
            harvestOp.setName("Harvest");
            harvestOp.setMethodCall("Harvest");
    
            List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
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
        String[] name = extractName(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT+"/ows:IndividualName"));
        if (name == null) {
        	return null;
        } else {
            address.setFirstname(name[0].trim());
            address.setLastname(name[1].trim());
        }
        address.setEmail(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Address/ows:ElectronicMailAddress"));
        
        // try to find address in database and set the uuid if found
        searchForAddress(address);
        
        address.setStreet(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Address/ows:DeliveryPoint"));
        address.setCity(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Address/ows:City"));
        address.setPostcode(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Address/ows:PostalCode"));
        address.setCountry(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Address/ows:Country"));
        address.setState(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Address/ows:AdministrativeArea"));
        address.setPhone(xPathUtils.getString(doc, XPATH_EXT_CSW_SERVICECONTACT + "/ows:ContactInfo/ows:Phone/ows:Voice"));
        return address;
    }

}
