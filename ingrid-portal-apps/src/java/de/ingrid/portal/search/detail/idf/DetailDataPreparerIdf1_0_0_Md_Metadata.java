package de.ingrid.portal.search.detail.idf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeAlle;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeInterval;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0_Md_Metadata extends DetailDataPreparerIdf1_0_0 {
	
	private final static Logger	log	= LoggerFactory.getLogger(DetailDataPreparerIdf1_0_0_Md_Metadata.class);
	private final static String	UDK_OBJ_CLASS_TYPE = "UDK_OBJ_CLASS_TYPE";
	
	private String firstGetCapabiltiesUrl = null;
	
	public DetailDataPreparerIdf1_0_0_Md_Metadata(Node node, Context context, RenderRequest request, String iPlugId, RenderResponse response) {
		super(node, context, request, iPlugId, response);
		this.rootNode = node;
		this.context = context;
		this.request = request;
		this.iPlugId = iPlugId;
		this.response = response;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public void prepare(ArrayList data) {
		
		if (rootNode != null) {
			if(log.isDebugEnabled()){
				log.debug("Start parsing of: '"+ rootNode.getLocalName() +"'");
			}
			initialArrayLists();
			
			HashMap general = new HashMap();
			ArrayList udkElements = new ArrayList();
			
			String xpathExpression;
			String subXPathExpression;
			
			// check for hierachyLevelName
			getUdkObjClass(general, udkElements);
			
			// Title
			xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title";
			getTitle(xpathExpression, general);
			
			// "last modification"
			xpathExpression = "./gmd:dateStamp";
			if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
				Node node = XPathUtils.getNode(rootNode, xpathExpression);
				String modTime = "";
				if(XPathUtils.nodeExists(node, "./gco:DateTime")){
					modTime = UtilsDate.convertDateString(XPathUtils.getString(node, "./gco:DateTime").trim(), "yyyy-MM-dd", "dd.MM.yyyy");
				}else if(XPathUtils.nodeExists(node, "./gco:Date")){
					modTime = UtilsDate.convertDateString(XPathUtils.getString(node, "./gco:Date").trim(), "yyyy-MM-dd", "dd.MM.yyyy");
				}
				if(modTime.length() > 0){
					general.put("modTime", modTime);	
				}
			}
			
	// Tab "General"
			// Description
			xpathExpression = "./gmd:identificationInfo/*";
			getGeneralTab(elementsGeneral, xpathExpression);
			
			// Addresses
			xpathExpression = "./gmd:identificationInfo/*/gmd:pointOfContact";
			getAddresses(elementsGeneral, xpathExpression, false);
			
	// Tab "Verweise"
			ArrayList referenceList = new ArrayList();
			// "Verweise"
			xpathExpression ="./idf:crossReference";
			getReference(referenceList, xpathExpression, ReferenceType.CROSS);
			
			// "Externe Webseiten"
			xpathExpression = "./gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions";
			getExternLinks(referenceList, xpathExpression);
			
			if(referenceList.size() > 0){
				elementsReference = addTabData(messages.getString("references"), referenceList);
			}
			// "Verschlagwortung"
			xpathExpression = "./gmd:identificationInfo/*/gmd:descriptiveKeywords";
			getIndexInformationKeywords(elementsReference, xpathExpression);
			
	// Tab "Verfügbarkeit"
			// "Datenformat"
			xpathExpression = "./gmd:distributionInfo/gmd:MD_Distribution";
			getAvailability(elementsAvailability, xpathExpression);
			
			// "Zugangsbeschränkungen"
			ArrayList elementsAccessConstraints = new ArrayList();
			xpathExpression = "./gmd:identificationInfo/*/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints";
			subXPathExpression = ".";
			getNodeListValueList(elementsAccessConstraints, xpathExpression, subXPathExpression, "6010", "textList");
			
			if(elementsAccessConstraints.size() > 0){
				HashMap element = new HashMap();
	        	element.put("type", "textList");
	        	element.put("title", messages.getString("object_access.restriction_value"));
	        	element.put("textList", elementsAccessConstraints);
	        	elementsAvailability.add(element);
			}
			
			// "Nutzungsbedingungen"
			ArrayList elementsUseConstraints = new ArrayList();
			xpathExpression = "./gmd:identificationInfo/*/gmd:resourceConstraints/*/gmd:useLimitation";
			subXPathExpression = ".";
			getNodeListValueList(elementsUseConstraints, xpathExpression, subXPathExpression, null, "textList");
			
			xpathExpression = "./gmd:identificationInfo/*/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue";
			subXPathExpression = ".";
			getNodeListValueList(elementsUseConstraints, xpathExpression, subXPathExpression, "524", "textList");
			
			if(elementsUseConstraints.size() > 0){
				HashMap element = new HashMap();
	        	element.put("type", "textList");
	        	element.put("title", messages.getString("object_access.terms_of_use"));
	        	element.put("textList", elementsUseConstraints);
	        	elementsAvailability.add(element);
			}
			
	// Tab "Raum/Zeit"
			// "Raumbezugssystem"
			xpathExpression = "./gmd:identificationInfo/*/*/gmd:EX_Extent";
			getAreaSection(elementsAreaTime, xpathExpression);
			
			// "Zeitbezug"
			xpathExpression = "./gmd:identificationInfo/*/*/gmd:EX_Extent";
			getTimeSection(elementsAreaTime, xpathExpression);
			
	// Tab "Zusätzliche Info"
			
			// "Sprache des Metadatensatzes"
			xpathExpression = "./gmd:language";
			subXPathExpression = "gmd:LanguageCode/@codeListValue";
			getNodeListValuesLanguage(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t01_object.metadata_language"), "textList");
			
			// "Sprache des Datensatzes"
			xpathExpression = "./gmd:identificationInfo/*/gmd:language";
			subXPathExpression = "./gmd:LanguageCode/@codeListValue";
			getNodeListValuesLanguage(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t01_object.data_language"), "textList");
			
			// "Veröffentlichung"
			xpathExpression = "./gmd:identificationInfo/*";
			getClassificationInformation(elementsAdditionalInfo, xpathExpression);
			
			// "Eignung/Nutzung"
			xpathExpression = "./gmd:identificationInfo/*/gmd:resourceSpecificUsage/gmd:MD_Usage/gmd:specificUsage";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.dataset_usage"));
			
			// "Herstellungszweck"
			xpathExpression = "./gmd:identificationInfo/*/gmd:purpose";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.info_note"), null, null, LabelType.ABOVE);
			
			// "Rechtliche Grundlagen"
			xpathExpression = "./gmd:identificationInfo/*/gmd:resourceConstraints/idf:idfLegalBasisConstraints/gmd:otherConstraints";
			subXPathExpression = ".";
			getNodeListValues(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t015_legist.name"), "textList");
			
			// "XML-Export-Kriterium"
			xpathExpression = "./idf:exportCriteria";
			subXPathExpression = ".";
			getNodeListValues(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t014_info_impart.name"), "textList");
			
			// "Konformität"
			xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report";
			getConformityData(elementsAdditionalInfo, xpathExpression);
			
			// "Objekt-ID"
			xpathExpression = "gmd:fileIdentifier";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.obj_id"));
					
			// "Elternobjekt"
			/* NOT IN USED
			xpathExpression = "gmd:parentIdentifier";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t012_obj_obj.object_from_id"));
			*/
			
			// "Zeichensatz des Metadatensatzes"
			/* NOT IN USED
			xpathExpression = "./gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_character_set"), "510");
			*/
			
			// "Zeichensatz des Datensatzes"
			// NOT IN USED
			// xpathExpression = "./gmd:identificationInfo/*/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue";
			//getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.dataset_character_set"), "510");
						
			// "ID der Objektklasse" 
			/* NOT IN USED
			xpathExpression = "gmd:hierarchyLevelName";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.obj_class"));
			*/
			
			// "Geändert am"
			/* NOT IN USED
			xpathExpression = "gmd:dateStamp";
			getNodeValueForDate(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.mod_time"));
			 */
			
			// "Metadatenstandardname"
			/* NOT IN USED
			xpathExpression = "gmd:metadataStandardName";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_standard_name"));
			*/
			
			// "Metadatenstandardversion"
			/* NOT IN USED
			xpathExpression = "gmd:metadataStandardVersion";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_standard_version"));
			*/

			if(elementsAdditionalInfo != null && elementsAdditionalInfo.size() > 0){
				addSpace(elementsAdditionalInfo);
			}
			// Addresse (Metadatum)
			xpathExpression = "./gmd:identificationInfo/*/gmd:pointOfContact";
			getAddresses(elementsAdditionalInfo, xpathExpression, true);

			
	// Tab "Fachbezug"
			
			if (context.get(UDK_OBJ_CLASS_TYPE) != null){
    			switch (Integer.parseInt((String) context.get(UDK_OBJ_CLASS_TYPE))) {
    				case 1:
    					// "Fachbezug - Geo-Information/Karte"
    	        		getThematicReferenceClass1(elementsSubject);
    	    			break;
    				case 2:
    					// "Fachbezug - Dokument/Bericht/Literatur"
    	        		getThematicReferenceClass2(elementsSubject);
    	    			break;
    				case 3:
    					// "Fachbezug - Geodatendienst"
    	        		getThematicReferenceClass3(elementsSubject);
    	    			break;
    				case 4:
    					// "Fachbezug - Vorhaben/Projekt/Programm"
    	        		getThematicReferenceClass4(elementsSubject);
    	    			break;
    				case 5:
    					// "Fachbezug - Datensammlung/Datenbank"
    	        		getThematicReferenceClass5(elementsSubject);
    	    			break;
    				case 6:
    					// "Fachbezug - Dienst/Anwendung/Informationssystem"
    	        		getThematicReferenceClass6(elementsSubject);
						break;
					default:
						break;
				}
    		}
    		
		
	// Tab "Datenqualität"
			
			// "Erfassungsgrad (neu = Datendefizit) / Lagegenauigkeit / Höhengenauigkeit"
			xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report";	
			getGeoReport(elementsDataQuality, xpathExpression);
			
			xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality";
    		getDataQualityClass(elementsDataQuality, xpathExpression);
    		
   // "Tab Zusätzliche Felder"
    		
    		xpathExpression ="./idf:additionalDataSection";
			getAdditionalFields(elementsAdditionalField, xpathExpression);
			
    		
			if(elementsReference.size() > 0){
				content.put(DATA_TAB_REFERENCE, elementsReference);
			}
			if(elementsAvailability.size() > 0){
				content.put(DATA_TAB_AVAILABILITY, addTabData(messages.getString("availability"),elementsAvailability));
			}
			if(elementsAdditionalInfo.size() > 0){
				content.put(DATA_TAB_ADDITIONAL_INFO, addTabData(messages.getString("additional_information"),elementsAdditionalInfo));
			}
			if(elementsSubject.size() > 0){
				content.put(DATA_TAB_SUBJECT, addTabData(messages.getString("subject_reference"),elementsSubject));
			}
			if(elementsDataQuality.size() > 0){
				content.put(DATA_TAB_DATA_QUALITY, addTabData(messages.getString("data_quality"), elementsDataQuality) );
			}
			
			content.put(DATA_TAB_GENERAL, elementsGeneral);
			content.put(DATA_TAB_AREA_TIME, elementsAreaTime);
			
			if(elementsAdditionalField.size() > 0){
				content.put(DATA_TAB_ADDITIONAL_FIELD, elementsAdditionalField);
			}
			
			if(content!=null){
				HashMap element = new HashMap();
				element.put("type", "gmd");
				element.put("body", content);
				element.put("general", general);
				element.put("udk", udkElements);
				data.add(element);
			}
		}
	}
	
	/** NOTICE: replaces all "\n" with "<br/>" because can have multiple lines (text areas, e.g. gmd:useLimitation). */
	private void getNodeListValueList(ArrayList elements, String xpathExpression, String subXPathExpression, String codeListId, String type) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i<nodeList.getLength();i++){
        		if(XPathUtils.nodeExists(nodeList.item(i), subXPathExpression)){
        			Node node = XPathUtils.getNode(nodeList.item(i), subXPathExpression);
	        		String value = XPathUtils.getString(node, ".").trim();
					HashMap listEntry = new HashMap();
					listEntry.put("type", type);
					if(codeListId != null){
						String tmpValue = sysCodeList.getNameByCodeListValue(codeListId, value);
						if(tmpValue != null && tmpValue.length() > 0){
							value = tmpValue;
						} else {
							if(log.isDebugEnabled()){
								log.debug("Codelist ID: " + codeListId + " return no value for '" + value+"'!");
							}
						}
					}
					value = value.replaceAll("\n", "<br/>");
					listEntry.put("body", value);
					if (!isEmptyList(listEntry)) {
						elements.add(listEntry);
					}
        		}
        	}
		}
	}

	private void getDataQualityClass(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			String dataQualityXPath = ""; 

			// "Datenüberschuss"
			dataQualityXPath = "./gmd:report/gmd:DQ_CompletenessCommission";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table109.title"), "7109");
			}
			
			// "Konzeptionelle Konsistenz"
			dataQualityXPath = "./gmd:report/gmd:DQ_ConceptualConsistency";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table112.title"), "7112");
			}

			// "Konsistenz des Wertebereichs"
			dataQualityXPath = "./gmd:report/gmd:DQ_DomainConsistency";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table113.title"), "7113");
			}

			// "Formatkonsistenz"
			dataQualityXPath = "./gmd:report/gmd:DQ_FormatConsistency";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table114.title"), "7114");
			}

			// "Topologische Konsistenz"
			dataQualityXPath = "./gmd:report/gmd:DQ_TopologicalConsistency";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table115.title"), "7115");
			}

			// "Zeitliche Genauigkeit"
			dataQualityXPath = "./gmd:report/gmd:DQ_TemporalConsistency";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table120.title"), "7120");
			}

			// "Korrektheit der thematischen Klassifizierung"
			dataQualityXPath = "./gmd:report/gmd:DQ_ThematicClassificationCorrectness";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table125.title"), "7125");
			}

			// "Genauigkeit nicht-quantitativer Attribute"
			dataQualityXPath = "./gmd:report/gmd:DQ_NonQuantitativeAttributeAccuracy";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table126.title"), "7126");
			}

			// "Genauigkeit quantitativer Attribute"
			dataQualityXPath = "./gmd:report/gmd:DQ_QuantitativeAttributeAccuracy";
			if(XPathUtils.nodeExists(node, dataQualityXPath)){
				createDataQualityTable(elements, XPathUtils.getNodeList(node, dataQualityXPath), messages.getString("data_quality.table127.title"), "7127");
			}

		}
	}
	
	private void createDataQualityTable(ArrayList elements, NodeList nodeList, String title, String codeListId) {
		HashMap element = new HashMap();
		element.put("type", "table");
		element.put("title", title);
		
		ArrayList head = new ArrayList();
		head.add(messages.getString("data_quality.table.header1"));
		head.add(messages.getString("data_quality.table.header2"));
		head.add(messages.getString("data_quality.table.header3"));
		element.put("head", head);
		ArrayList body = new ArrayList();
		element.put("body", body);
		
		for(int i=0; i < nodeList.getLength(); i++){
			ArrayList row = new ArrayList();
			Node node = nodeList.item(i);
			String xpathExpression = "";
			// Check if data quality node for omission is a subject node.
			boolean isSubjectOmission = false;
			xpathExpression = "./gmd:measureDescription";
			if(XPathUtils.nodeExists(node, xpathExpression)){
				String description = XPathUtils.getString(node, xpathExpression).trim();
				if(description.equals("completeness omission (rec_grade)")){
					isSubjectOmission = true;
				}
			}
				
			if(!isSubjectOmission){
				// "Art der Messung"
				xpathExpression = "./gmd:nameOfMeasure";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(sysCodeList.getName(codeListId, XPathUtils.getString(node, xpathExpression).trim()));
				}else{
					row.add("");
				}
				
				// "Wert"
				xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression).trim()); 
				}else{
					row.add("");
				}
				
				// "Parameter"
				xpathExpression = "./gmd:measureDescription";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression).trim());
				}else{
					row.add("");
				}
			}
			if (!isEmptyRow(row)) {
    			body.add(row);
    		}
		}
		if (body.size() > 0) {
			
	    	elements.add(element);
	    }
	}

	
	private void getThematicReferenceClass1(ArrayList elements) {
		// "Fachliche Grundlage"
		String xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.special_base"));
		
		// "Datensatz/Datenserie"
		xpathExpression = "./gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.hierarchy_level"), "525");
		
		// "Digitale Repräsentation"
		xpathExpression = "./gmd:identificationInfo/*/gmd:spatialRepresentationType";
		String subXPathExpression = "gmd:MD_SpatialRepresentationTypeCode/@codeListValue";
		getNodeListValues(elements, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_spatial_rep.type"), "textList", "526");
		
		// "Topologieinformation"
		xpathExpression ="./gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:topologyLevel/gmd:MD_TopologyLevelCode/@codeListValue";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.vector_topology_level"), "528");
		
		// "Vektorformat"
		xpathExpression = "./gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:geometricObjects";
		getGeometries(elements, xpathExpression);
		
		// "Erstellungsmaßstab"
		xpathExpression = "./gmd:identificationInfo/*";
		getReferenceObject(elements, xpathExpression);
		
		// "Symbolkatalog"
		xpathExpression = "./gmd:portrayalCatalogueInfo";
		getSymCatalog(elements, xpathExpression);
		
		// "Schlüsselkatalog: im Datensatz vorhanden"
		xpathExpression = "./gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:includedWithDataset";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.keyc_incl_w_dataset"));
		
		// "Schlüsselkatalog"
		xpathExpression = "./gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureCatalogueCitation/gmd:CI_Citation";
		getKeyCatalogTable(elements, xpathExpression);
		
		// "Datengrundlage"			
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.data_base"));
		
		// "Sachdaten/Attributinformation"
		xpathExpression ="./gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureTypes";
		subXPathExpression =".";
		getNodeListValues(elements, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_supplinfo.feature_type"), "textList");
		
		// "Herstellungsprozess"
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.method"));
		
		// "Identifikator der Datenquelle"
		// mapped differently in IDF 3.2.0 (gmd:MD_Identifier) than before (gmd:RS_Identifier). We map both !
		// IDF 3.2.0
		xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.datasource_uuid"));
		// IDF older
		xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:RS_Identifier/gmd:code";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.datasource_uuid"));	
		
	}

	private void getThematicReferenceClass2(ArrayList elements) {
		String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty";
		NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			
			String type = "";
			
			xpathExpression = "./gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode/@codeListValue";
			if(XPathUtils.nodeExists(node, xpathExpression)){
				type = XPathUtils.getString(node, xpathExpression).trim();
			}
			
			if(type.equals("originator")){
				// "Autor"
				xpathExpression = "./gmd:CI_ResponsibleParty/gmd:individualName";
				getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.autor"), null, node);
			}
			
			if(type.equals("publisher")){
				// "Herausgeber"
				xpathExpression = "./gmd:CI_ResponsibleParty/gmd:individualName";
				getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.publisher"), null, node);
				
				// "Erscheinungsort"
				xpathExpression = "./gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
				getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.publish_loc"), null, node);
			}
			
			if(type.equals("distribute")){
				// "Verlag"
				xpathExpression = "./gmd:CI_ResponsibleParty/organisationName";
				getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.publishing"), null, node);
			}
			
			if(type.equals("resourceProvider")){
				// "Standort"
				xpathExpression ="./gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:contactInstructions";
				getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.loc"), null, node);
			}
		}
		
		// "Erschienen in"
		xpathExpression ="./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:name";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.publish_in"));
		
		// "Erscheinungsjahr"
		xpathExpression ="./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:editionDate";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.publish_year"));

		// "Band, Heft"
		xpathExpression ="./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:issueIdentification";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.volume"));
		
		// "Seiten"
		xpathExpression ="./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:page";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.sides"));
		
		// "ISBN-Nummer des Dokumentes"
		xpathExpression ="./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:ISBN";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.isbn"));
		
		// "Dokumententyp"
		xpathExpression = "./gmd:identificationInfo/*/gmd:resourceFormat/gmd:MD_Format/gmd:name";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.typ"));
		
		// "Basisdaten"
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.base"));
		
		// "Weitere bibliographische Angaben"
		xpathExpression ="./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:otherCitationDetails";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.doc_info"));
		
		// "Erläuterung zum Fachbezug"
		xpathExpression = "./gmd:identificationInfo/*/gmd:supplementalInformation";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_literatur.description"));
	}
	
	private void getThematicReferenceClass3(ArrayList elements) {
		
		// "Fachliche Grundlage"
		String xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.special_base"));
		
		// "Klassifikation des Dienstes"
		xpathExpression = "./gmd:identificationInfo/*/gmd:descriptiveKeywords";
		getServiceClassification(elements, xpathExpression);
		
		// Typ des Dienstes
		xpathExpression = "./gmd:identificationInfo/*/srv:serviceType";
		String serviceType = getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.type"), "5100");

		// Version des Services
		xpathExpression = "./gmd:identificationInfo/*/srv:serviceTypeVersion";
		String subXPathExpression = ".";
		getNodeListValues(elements, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_version.version"), "textList");
		
		// "Erstellungsmaßstab"
		xpathExpression = "./gmd:identificationInfo/*";
		getReferenceObject(elements, xpathExpression);
		
		// "Systemumgebung"
		xpathExpression = "./gmd:identificationInfo/*/gmd:environmentDescription";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.environment"));
		
		// Historie
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.history"));
		
		// Basisdaten
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.base"));
		
		// "Erläuterung zum Fachbezug"
		xpathExpression = "./gmd:identificationInfo/*/gmd:supplementalInformation";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.description"));
		
		// "Erläuterung zum Fachbezug (*)"
		xpathExpression = "./gmd:identificationInfo/*/gmd:supplementalInformation/gmd:abstract";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.description"));
		
		// Operationen
		xpathExpression = "./gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata";
		getServiceOperations(elements, xpathExpression);
		
		// "Operation -> Zugriffsadresse"
		xpathExpression = "./gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint";
		getConnectionPoints(serviceType, elements, xpathExpression);
		
		// "Abhängigkeit"
		/* NOT IN USED*/
		// xpathExpression ="gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:dependsOn";
		/*
		subXPathExpression ="srv:SV_OperationMetadata";
		getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_depends.depends_on"), "textList");
		*/
		
		// "Unterstützte Plattformen"
		/* NOT IN USED */
		// xpathExpression ="gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:dependsOn/srv:DCP/srv:DCPList/@codeListValue";
		/*
		getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv_op_platform.platform"));
		*/
		
		// "Parametername"
		/* NOT IN USED */
		// xpathExpression ="*/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters/srv:SV_Parameter/srv:name/gmd:aName";
		/* 
		getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv_op_para.name"));
		*/
		
		// "Parameter > Richtung"
		/* NOT IN USED */
		//xpathExpression ="*/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
		/*
		subXPathExpression = "srv:SV_Parameter/srv:direction/gmd:SV_ParameterDirection";
		getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.direction"), "textList");
		*/
		
		// "Parameter > Beschreibung"
		/* NOT IN USED */
		// xpathExpression ="*/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
		/*
		subXPathExpression = "srv:SV_Parameter/srv:description";
		getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.descr"), "textList");
		*/
		
		// "Parameter > Ist Optional"
		/* NOT IN USED */
		// xpathExpression ="*/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
		/*
		subXPathExpression = "srv:SV_Parameter/srv:optionality";
		getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.optional"), "textList");
		*/
		
		// "Parameter > Mehrfachangabe möglich"
		/* NOT IN USED */
		// xpathExpression ="*/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
		/*
		subXPathExpression = "srv:SV_Parameter/srv:repeatability";
		getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.repeatability"), "textList");
		*/
		
	}

	private void getThematicReferenceClass4(ArrayList elements) {
		
		String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty";
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i<nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				String role = "";
				xpathExpression = "./gmd:role/gmd:CI_RoleCode/@codeListValue";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					role = XPathUtils.getString(node, xpathExpression).trim();
				}
				xpathExpression = "./gmd:individualName";
				if(role.equals("projectManager")){
					// Projektleiter
					getNodeValue(elements, xpathExpression, messages.getString("t011_obj_project.leader"), null, node);
				}else if (role.equals("projectParticipant")){
					// Beteiligte
					getNodeValue(elements, xpathExpression, messages.getString("t011_obj_project.member"), null, node);
				}
			}
		}
		
		// Erläuterung des Fachbezug
		xpathExpression = "./gmd:identificationInfo/*/gmd:supplementalInformation";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_project.description"));
	}

	private void getThematicReferenceClass5(ArrayList elements) {
		
		// "Schlüsselkatalog: im Datensatz vorhanden"
		String xpathExpression = "./gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:includedWithDataset";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.keyc_incl_w_dataset"));
		
		// "Schlüsselkatalog"
		xpathExpression = "./gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureCatalogueCitation/gmd:CI_Citation";
		getKeyCatalogTable(elements, xpathExpression);
		
		// Inhalte der Datensammlung/Datenbank
		xpathExpression ="./gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureTypes";
		String subXPathExpression =".";
		getParameterTable(elements, xpathExpression, subXPathExpression, messages.getString("t011_obj_data_para"));
		
		// Methode/Datengrundlage
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_data.base"));
		
		// Erläuterungen zum Fachbezug
		xpathExpression = "./gmd:identificationInfo/*/gmd:supplementalInformation";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_data.description"));
	}

	private void getThematicReferenceClass6(ArrayList elements) {

		// "Klassifikation des Dienstes"
		String xpathExpression = "./gmd:identificationInfo/*/gmd:descriptiveKeywords";
		getServiceClassification(elements, xpathExpression);
		
		// "Art des Dienstes"
		xpathExpression = "./gmd:identificationInfo/*/srv:serviceType";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.type"), "5300");

		// "Version"
		xpathExpression = "./gmd:identificationInfo/*/srv:serviceTypeVersion";
		String subXPathExpression = ".";
		getNodeListValues(elements, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_version.version"), "textList");
		
		// "Systemumgebung"
		xpathExpression = "./gmd:identificationInfo/*/gmd:environmentDescription";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.environment"));
		
		// Historie
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.history"));
		
		// Basisdaten
		xpathExpression = "./gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_serv.base"));
		
		// "Erläuterung zum Fachbezug"
		xpathExpression = "./gmd:identificationInfo/*/gmd:supplementalInformation";
		getNodeValue(elements, xpathExpression, messages.getString("t011_obj_data.description"));
		
		// Service-URL
		xpathExpression = "./gmd:identificationInfo/*/srv:containsOperations";
		getServiceUrlTable(elements, xpathExpression);
	}
	
	private void getSymCatalog(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("t011_obj_geo_symc"));
			
	    	ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_geo_symc.symbol_cat"));
			head.add(messages.getString("t011_obj_geo_symc.symbol_date"));
			head.add(messages.getString("t011_obj_geo_symc.edition"));
			element.put("head", head);
			
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for(int i=0; i<nodeList.getLength();i++){
				ArrayList row = new ArrayList();
				Node node = nodeList.item(i);
				
				// "Titel"
				xpathExpression = "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:title";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression).trim());
				}else{
					row.add("");
				}
				
				// "Datum"
				xpathExpression = "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String date = UtilsDate.convertDateString(XPathUtils.getString(node, xpathExpression).trim(), "yyyy-MM-dd", "dd.MM.yyyy");
					row.add(date);
				}else{
					row.add("");
				}
				
				// "Version"
				xpathExpression = "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:edition";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression).trim());
				}else{
					row.add("");
				}
				
				if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
			}
			if (body.size() > 0) {
				
		    	elements.add(element);
		    }
		}
	}

	private void getGeometries(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("t011_obj_geo_vector"));
			
	    	ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_geo_vector.geometric_object_type"));
			head.add(messages.getString("t011_obj_geo_vector.geometric_object_count"));
			element.put("head", head);
			
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for(int i=0; i<nodeList.getLength();i++){
				ArrayList row = new ArrayList();
				Node node = nodeList.item(i);
				
				// "Geometrietyp"
				xpathExpression = "./gmd:MD_GeometricObjects/gmd:geometricObjectType/gmd:MD_GeometricObjectTypeCode/@codeListValue";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(sysCodeList.getNameByCodeListValue("515", XPathUtils.getString(node, xpathExpression).trim()));
				}else{
					row.add("");
				}
				
				// "Elementanzahl"
				xpathExpression = "./gmd:MD_GeometricObjects/gmd:geometricObjectCount";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression).trim());
				}else{
					row.add("");
				}
				
				if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
			}
			if (body.size() > 0) {
				
		    	elements.add(element);
		    }
		}
	}

	private void getGeoReport(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				Node subNode = null;
				String symbol = null;
				String nameOfMeasure = null;
				String description = null;
				String value = "";
				String title = "";
				
				if(XPathUtils.nodeExists(node, "./gmd:DQ_AbsoluteExternalPositionalAccuracy")){
					subNode = XPathUtils.getNode(node, "./gmd:DQ_AbsoluteExternalPositionalAccuracy");
				}else if(XPathUtils.nodeExists(node, "./gmd:DQ_CompletenessOmission")){
					subNode = XPathUtils.getNode(node, "./gmd:DQ_CompletenessOmission");
				}
				if(subNode != null){
					xpathExpression = "./gmd:nameOfMeasure";
					if(XPathUtils.nodeExists(subNode, xpathExpression)){
						nameOfMeasure = XPathUtils.getString(subNode, xpathExpression).trim();
					}
					xpathExpression = "./gmd:measureDescription";
					if(XPathUtils.nodeExists(subNode, xpathExpression)){
						description = XPathUtils.getString(subNode, xpathExpression).trim();
					}
					xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
					if(XPathUtils.nodeExists(subNode, xpathExpression)){
						value = XPathUtils.getString(subNode, xpathExpression).trim();
					}
					xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:UnitDefinition/gml:catalogSymbol";
					if(XPathUtils.nodeExists(subNode, xpathExpression)){
						symbol = XPathUtils.getString(subNode, xpathExpression).trim();
					}
					
					if(symbol != null){
						if(description != null || nameOfMeasure != null){
							if ("vertical".equals(description) || "Mean value of positional uncertainties (1D)".equalsIgnoreCase(nameOfMeasure)){
								// "Höhengenauigkeit"
								title = messages.getString("t011_obj_geo.pos_accuracy_vertical");
							}else if ("geographic".equals(description) || "Mean value of positional uncertainties (2D)".equalsIgnoreCase(nameOfMeasure)){
								// "Lagegenauigkeit"
								title = messages.getString("t011_obj_geo.rec_exact");
							}else if ("completeness omission (rec_grade)".equals(description) || "Rate of missing items".equalsIgnoreCase(nameOfMeasure)){
								// "Datendefizit"
								title = messages.getString("t011_obj_geo.coverage");
							}
							if(title.length() > 0){
								addElementEntryLabelLeft(elements, value + " " + symbol, title);	
							}
						}
					}
				}
			}
		}
	}

	private void getKeyCatalogTable(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("t011_obj_geo_keyc"));
			
	    	ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_geo_keyc.subject_cat"));
			head.add(messages.getString("t011_obj_geo_keyc.key_date"));
			head.add(messages.getString("t011_obj_geo_keyc.edition"));
			element.put("head", head);
			
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for(int i=0; i<nodeList.getLength();i++){
				ArrayList row = new ArrayList();
				Node node = nodeList.item(i);
				
				// "Schlüsselkatalog: Titel"
				xpathExpression = "./gmd:title";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression));
				}else{
					row.add("");
				}
				
				// "Schlüsselkatalog: Datum"
				xpathExpression = "./gmd:date/gmd:CI_Date/gmd:date";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String date = UtilsDate.convertDateString(XPathUtils.getString(node, xpathExpression).trim(), "yyyy-MM-dd", "dd.MM.yyyy");
					row.add(date);
				}else{
					row.add("");
				}
				
				// "Schlüsselkatalog: Version"
				xpathExpression = "./gmd:edition";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					row.add(XPathUtils.getString(node, xpathExpression));
				}else{
					row.add("");
				}
				
				if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
			}
			if (body.size() > 0) {
				
		    	elements.add(element);
		    }
		}
	}

	private void getParameterTable(ArrayList elements, String xpathExpression, String subXPathExpression, String title) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			/* 
			 * Table for parameter and unit isn't possible because both are in the same node
			 *  
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", title);
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_data_para.parameter"));
			head.add(messages.getString("t011_obj_data_para.unit"));
			element.put("head", head);
			
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	
			for (int i=0; i<nodeList.getLength();i++){
        		if(XPathUtils.nodeExists(nodeList.item(i), subXPathExpression)){
        			Node node = XPathUtils.getNode(nodeList.item(i), subXPathExpression);
	        		String value = XPathUtils.getString(node, ".").trim();
	        		ArrayList row = new ArrayList();
		    		row.add(value);
		    		row.add("");
		    		if (!isEmptyRow(row)) {
		    			body.add(row);
		    		}
        		}
        	}
			if (body.size() > 0) {
				
				elements.add(element);
		    }
		    */
			
			for (int i=0; i<nodeList.getLength();i++){
				getNodeValue(elements, ".", messages.getString("t011_obj_data_para.parameter") +  " (" + messages.getString("t011_obj_data_para.unit") + ")", null, nodeList.item(i));
			}
        }
	}

	private void getReference(ArrayList elements, String xpathExpression, ReferenceType referenceType) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList linkList = new ArrayList();
			for (int i=0; i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				String uuid = "";
				String title = "";
				String type = "";
				String attachedToField = "";
				String description = "";
				
				xpathExpression = "./@uuid";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					uuid = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:objectName";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					title = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:objectType";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					type = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:attachedToField";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					attachedToField = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:description";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					description = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				HashMap link = new HashMap();
	        	link.put("hasLinkIcon", new Boolean(true));
	        	link.put("isExtern", new Boolean(false));
	        	link.put("title", title);
	        	link.put("objectClass", type);
  	        	if (description.length() > 0) {
  	        		link.put("description", description);
  	        	}
  	        	if (attachedToField.length() > 0) {
  	        		link.put("attachedToField", attachedToField);
  	        	}          	        	
	        	if(this.iPlugId != null){
	        		if(uuid != null){
	        			if(log.isDebugEnabled()){
	        				log.debug("Create URL with iPlug: '" + this.iPlugId + "' and UUID: '" + uuid +"'");
	        			}
	        			PortletURL actionUrl = response.createActionURL();
			        	actionUrl.setParameter("cmd", "doShowDocument");
			    		actionUrl.setParameter("docuuid", uuid);
			    		actionUrl.setParameter("plugid", this.iPlugId);
			    		link.put("href", actionUrl.toString());
			        	
		        	}else{
		        		link.put("href", "");
		        	}
	        	}else{
	        		link.put("href", "");
	        	}
	        	// add map links to data objects from services
	        	if (context.get(UDK_OBJ_CLASS_TYPE).equals("3") && type.equals("1")) {
	        	    // get link from operation (unique one)
	        	    link.put("mapLink", getCapabilityUrl());
	        	} else if (this.firstGetCapabiltiesUrl != null && context.get(UDK_OBJ_CLASS_TYPE).equals("1") && type.equals("3")) {
	        	    // get link from online resource (possibilty it's wrong?)
	        	    link.put("mapLink", this.firstGetCapabiltiesUrl);
	        	}
	        	linkList.add(link);
			}
			if (linkList != null && linkList.size() > 0){
				switch (referenceType) {
					case SUPERIOR:
						addReferenceToElement(elements, linkList, null, messages.getString("superior_references"));
						break;
					case SUBORDINATE:
						addReferenceToElement(elements, linkList, null, messages.getString("subordinated_references"));
						break;
					case CROSS:
						if (!linkList.isEmpty()) {
							HashMap element = new HashMap();
							element.put("type", "linkList");
							element.put("title", messages.getString("cross_references"));
							element.put("linkList", linkList);
							elements.add(element);
						}
						break;
					default:
						break;
				}
			}
		}
	}

	/**
	 * This function is only used by service objects (class 3) and returns the
	 * capabilities Url, which is retrieved by the getConnectionPoint function.
	 * 
	 * @return
	 */
	private String getCapabilityUrl() {
	    String url = null;
	    
	    String xpathExpression = "./gmd:identificationInfo/*/srv:serviceType";
        String serviceType = getNodeValue(new ArrayList(), xpathExpression, messages.getString("t011_obj_serv.type"), "5100");
        
	    ArrayList<HashMap> tempElements = new ArrayList<HashMap>();
	    
	    String opXPath = "./gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint";
	    getConnectionPoints(serviceType, tempElements, opXPath);

	    if (tempElements.size() > 0) {
	        HashMap link = (HashMap) tempElements.get(0).get("link");
	        if (link != null) {
	            url = (String) link.get("href");
	            url = url.substring(url.indexOf("http"));
	        }
	    }
        return url;
    }

    private void getAdditionalFields(ArrayList elements, String xpathExpression) {
		getAdditionalFields(elements, xpathExpression, null);
	}

	private void getAdditionalFields(ArrayList elements, String xpathExpression, TimeSpatialType timeSpatialType) {

		String lang = request.getLocale().getLanguage().toString();
		String id = "";
		HashMap additionalField = new HashMap();
		ArrayList list = new ArrayList();
		boolean newAdditionalRubric = false;
		
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				String tmpId = "";
				xpathExpression = "./@id";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					tmpId = XPathUtils.getString(node, xpathExpression).trim();
				}
				if(!id.equals(tmpId)){
					newAdditionalRubric = true;
					additionalField = new HashMap();
					list = new ArrayList();
				}else{
					newAdditionalRubric = false;
				}
				if(node.getLocalName()!= null){
					if(node.hasChildNodes() && node.getLocalName().equals("additionalDataSection") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
						String isLegacy = "";
						xpathExpression = "./@isLegacy";
						if(XPathUtils.nodeExists(node, xpathExpression)){
							isLegacy = XPathUtils.getString(node, xpathExpression).trim();
						}
						if(!isLegacy.equals("true")){
							additionalField.put("title", getNodeIdfTitle(node, lang));
							NodeList childNodeList = node.getChildNodes();
							getAdditionalFieldsForRubric(list, childNodeList, lang);
							
							if(list.size() > 0 ){
								if(newAdditionalRubric){
									id = tmpId;
									additionalField.put("type", "additionalField");
									additionalField.put("body", list);
									if(additionalField.size()>0){
										elements.add(additionalField);
									}	
								}
							}
						}else{
							String legacyId = "";
							xpathExpression = "./@id";
							if(XPathUtils.nodeExists(node, xpathExpression)){
								isLegacy = XPathUtils.getString(node, xpathExpression).trim();
							}
							NodeList childNodeList = node.getChildNodes();
							
							if(timeSpatialType != null){
								switch (timeSpatialType) {
									case TIME:
										if(legacyId.equals("timeRef")){
											// "Tab Raum/Zeit"
											getAdditionalFieldsForRubric(elements, childNodeList, lang);
										}
										break;
									case SPATIAL:
										if(legacyId.equals("spatialRef")){
											// "Tab Raum/Zeit"
											getAdditionalFieldsForRubric(elements, childNodeList, lang);
										}
										break;
									default:
										break;
								}
							}else{
								if(legacyId.equals("general")){
									// "Tab Allgemeines"
									getAdditionalFieldsForRubric(elementsGeneral, childNodeList, lang);
								}else if(legacyId.equals("refClass1DQ") || legacyId.equals("refClass2DQ") || legacyId.equals("refClass1DQ")
										|| legacyId.equals("refClass4DQ") || legacyId.equals("refClass5DQ") || legacyId.equals("refClass6DQ")){
									// "Tab Datenqualität"
									getAdditionalFieldsForRubric(elementsDataQuality, childNodeList, lang);
								}else if(legacyId.equals("extraInfo")){
									// "Tab Zusätzliche Info"
									getAdditionalFieldsForRubric(elementsAdditionalInfo, childNodeList, lang);
								}else if(legacyId.equals("availability")){
									// "Tab Verfügbarkeit"
									getAdditionalFieldsForRubric(elementsAvailability, childNodeList, lang);
								}else if(legacyId.equals("thesaurus") || legacyId.equals("links")){
									// "Tab Verweise"
									getAdditionalFieldsForRubric(elementsReference, childNodeList, lang);
								}else if(legacyId.equals("refClass1") || legacyId.equals("refClass2") || legacyId.equals("refClass3")
										|| legacyId.equals("refClass4") || legacyId.equals("refClass5") || legacyId.equals("refClass6")){
									// "Tab Fachbezug"
									getAdditionalFieldsForRubric(elementsSubject, childNodeList, lang);
								}else{
									if(!legacyId.equals("timeRef") && !legacyId.equals("spatialRef")){
										// "Tab Zusätzliche Info"
										getAdditionalFieldsForRubric(elementsAdditionalInfo, childNodeList, lang);
									}
								}
							}
						}
					}	
				}
			}
		}
	}	

	private void getAdditionalFieldsForRubric(ArrayList list, NodeList nodeList, String lang){
		for (int j=0; j < nodeList.getLength(); j++){
			Node childNode = nodeList.item(j);
			if(childNode.getLocalName()!= null){
				if(childNode.getLocalName().equals("additionalDataField") && childNode.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
					String title = getNodeIdfTitle(childNode, lang);
					String postfix = getNodeIdfPostfix(childNode, lang);
					String body = "";
					String xpathExpression = "./idf:data";
					if(XPathUtils.nodeExists(childNode, xpathExpression)){
						body = XPathUtils.getString(childNode, xpathExpression).trim();
					}
					
					if(body.length() > 0){
						if(postfix.length() > 0){
							addElementEntryLabelLeft(list, body + " " + postfix, title);	
						}else{
							addElementEntryLabelLeft(list, body, title);
						}
					}
				}else if(childNode.getLocalName().equals("additionalDataTable") && childNode.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
					String title = getNodeIdfTitle(childNode, lang);
					
					HashMap element = new HashMap();
					element.put("type", "table");
					element.put("title", title);
					
					ArrayList head = new ArrayList();
					element.put("head", head);
					ArrayList body = new ArrayList();
					element.put("body", body);
					
					int countRows=0;
					NodeList tableColumnNodeList = XPathUtils.getNodeList(childNode, "./idf:tableColumn"); 
					// Create header
					for (int iHead=0; iHead < tableColumnNodeList.getLength(); iHead++){
						Node headNode = tableColumnNodeList.item(iHead);
						head.add(getNodeIdfTitle(headNode, lang));
						NodeList rowNodeList = XPathUtils.getNodeList(headNode, "./idf:data");
						if(countRows < rowNodeList.getLength()){
							countRows = rowNodeList.getLength();
						}
					}
					// Create row
					for (int iRow=1; iRow<=countRows;iRow++){
						ArrayList row = new ArrayList();
						for (int iCell=0; iCell < tableColumnNodeList.getLength(); iCell++){
							Node tableColumnNode = tableColumnNodeList.item(iCell);
							if(XPathUtils.nodeExists(tableColumnNode, "./idf:data["+iRow+"]")){
								Node cellNode = XPathUtils.getNode(tableColumnNode, "./idf:data["+iRow+"]");
								String value = "";
								if(cellNode.getFirstChild() != null){
									if(cellNode.getFirstChild().getNodeValue() != null){
										value = cellNode.getFirstChild().getNodeValue().trim();
									}
									if(value!=null){
										row.add(value);
									}
								}else{
									row.add("");
								}
							}else{
								row.add("");
							}
						}
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
					if (body.size() > 0) {
						list.add(element);
					}	
				}
			}
		}
	}
	
	private String getNodeIdfPostfix(Node node, String lang) {
		String prefix; 
		if(XPathUtils.nodeExists(node, "./idf:postfix[@lang='"+ lang +"']")){
			prefix = XPathUtils.getString(node, "./idf:postfix[@lang='"+ lang +"']");
		}else if(XPathUtils.nodeExists(node, "./idf:postfix/@lang")){
			prefix = XPathUtils.getString(node, "./idf:postfix/@lang");
		}else if(XPathUtils.nodeExists(node, "./idf:postfix")){
			prefix = XPathUtils.getString(node, "./idf:postfix");
		}else{
			prefix = "";
		}
		return prefix.trim();
	}

	private String getNodeIdfTitle(Node node, String lang) {
		String title; 
		if(XPathUtils.nodeExists(node, "./idf:title[@lang='"+ lang +"']")){
			title = XPathUtils.getString(node, "./idf:title[@lang='"+ lang +"']");
		}else if(XPathUtils.nodeExists(node, "./idf:title/@lang")){
			title = XPathUtils.getString(node, "./idf:title/@lang");
		}else if(XPathUtils.nodeExists(node, "./idf:title")){
			title = XPathUtils.getString(node, "./idf:title");
		}else{
			title = "";
		}
		return title.trim();
	}

	private void getNodeListValuesLanguage(ArrayList elements, String xpathExpression, String subXPathExpression, String title, String type) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			if(nodeList.getLength() > 1){
				ArrayList list = new ArrayList();
		    	HashMap element = new HashMap();
	        	element.put("type", type);
	        	element.put("title", title);
	        	element.put(type, list);
	        	for (int i=0; i<nodeList.getLength();i++){
	        		if(XPathUtils.nodeExists(nodeList.item(i), subXPathExpression)){
	        			Node node = XPathUtils.getNode(nodeList.item(i), subXPathExpression);
		        		String value = XPathUtils.getString(node, ".").trim();
						HashMap listEntry = new HashMap();
						listEntry.put("type", type);
						listEntry.put("body", UtilsLanguageCodelist.getNameFromIso639_2(value, this.request.getLocale().getLanguage().toString()));
						if (!isEmptyList(listEntry)) {
							list.add(listEntry);
						}
	        		}
	        	}
	        	elements.add(element);
			}else{
				Node node = nodeList.item(0);
				if (XPathUtils.nodeExists(node, subXPathExpression)) {
					String body = XPathUtils.getString(node, subXPathExpression).trim();
					if(body.length() > 0){
						addElementEntryLabelLeft(elements, UtilsLanguageCodelist.getNameFromIso639_2(body, this.request.getLocale().getLanguage().toString()), title);
					}
				}
			}
        }
	}

	private void getNodeListValues(ArrayList elements, String xpathExpression, String subXPathExpression, String title, String type) {
		getNodeListValues(elements, xpathExpression, subXPathExpression, title, type, null);
	}
		
	private void getNodeListValues(ArrayList elements, String xpathExpression, String subXPathExpression, String title, String type, String codeListId) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList list = new ArrayList();
	    	HashMap element = new HashMap();
        	element.put("type", type);
        	element.put("title", title);
        	element.put(type, list);
        	for (int i=0; i<nodeList.getLength();i++){
        		if(XPathUtils.nodeExists(nodeList.item(i), subXPathExpression)){
        			Node node = XPathUtils.getNode(nodeList.item(i), subXPathExpression);
	        		String value = XPathUtils.getString(node, ".").trim();
					HashMap listEntry = new HashMap();
					listEntry.put("type", type);
					if(codeListId != null){
						String tmpValue = sysCodeList.getNameByCodeListValue(codeListId, value);
						if(tmpValue.length() < 1){
							if(log.isDebugEnabled()){
								log.debug("Codelist ID: " + codeListId + " return no value for '" + value+"'!");
							}
							tmpValue = value;
						}
						
						listEntry.put("body", tmpValue);
					}else{
						listEntry.put("body", value);
					}
					if (!isEmptyList(listEntry)) {
						list.add(listEntry);
					}
        		}
        	}
        	elements.add(element);
		}
	}

	private void getNodeValue(ArrayList elements, String xpathExpression, String title) {
		getNodeValue(elements, xpathExpression, title, null);
	}
	
	private String getNodeValue(ArrayList elements, String xpathExpression, String title, String codeListId) {
		return getNodeValue(elements, xpathExpression, title, codeListId, null, LabelType.LEFT);
	}
	
	private String getNodeValue(ArrayList elements, String xpathExpression, String title, String codeListId, Node node) {
		return getNodeValue(elements, xpathExpression, title, codeListId, node, LabelType.LEFT);
	}
	
	private String getNodeValue(ArrayList elements, String xpathExpression, String title, String codeListId, Node node, LabelType labelType) {
		String retValue = null;

		Node tmpNode;
		if(node != null){
			tmpNode = node;
		}else{
			tmpNode = rootNode;
		}
		if (XPathUtils.nodeExists(tmpNode, xpathExpression)) {
			String value = XPathUtils.getString(tmpNode, xpathExpression).trim();
			retValue = value;
			String tmpValue = "";
			if(value.length() > 0){
				if(codeListId != null){
					tmpValue= sysCodeList.getNameByCodeListValue(codeListId, value);
				}else{
					if(value.equals("false")){
						tmpValue = messages.getString("general.no"); 
					}else if(value.equals("true")){
						tmpValue = messages.getString("general.yes");
					}
				}
				
				if(tmpValue.length() < 1){
					value = XPathUtils.getString(tmpNode, xpathExpression).trim();
				}else{
					value = tmpValue;
				}
				switch (labelType) {
					case LEFT:
						addElementEntryLabelLeft(elements, value, title);
						break;
					case ABOVE:
						addElementEntryLabelAbove(elements, value, title, true);
						break;
					case DURING:
						addElementEntryLabelDuring(elements, value, title);
						break;
					default:
						break;
				}
			}
		}
		
		return retValue;
	}

	private void getExternLinks(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList linkList = new ArrayList();
	    	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("www_references"));
        	element.put("linkList", linkList);
        	for (int i=0; i<nodeList.getLength();i++){
        		String url = "";
        		String name = "";
        		String description = "";
        		String function = "";
        		String attachedToField = "";
        		String size = "";
        		float roundSize = 0;
        		
        		NodeList onLineList = XPathUtils.getNodeList(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine");
        		
        		for (int j=0; j<onLineList.getLength();j++){
        		    
        		//if(XPathUtils.nodeExists(nodeList.item(i),"./gmd:MD_DigitalTransferOptions/gmd:onLine")){
        			xpathExpression = "./*/gmd:linkage/gmd:URL";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			url = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/gmd:function/gmd:CI_OnLineFunctionCode/@codeListValue";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			function = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/gmd:description";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			description = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/gmd:name";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			name = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/idf:attachedToField";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			attachedToField = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}

            		// also mapped by T0112_media_option !!!
            		xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
            		if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
            			size = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
            			roundSize = Float.valueOf(size).floatValue();
            			roundSize = (float) (Math.round(roundSize * 1000) / 1000.0);
            		}
            		
            		
            		if(url.length() > 0){
            			HashMap link = new HashMap();			
        				link.put("hasLinkIcon", new Boolean(true));
          	        	link.put("isExtern", new Boolean(true));
          	        	link.put("href", url);

          	        	if(name.length() > 0){
          	        		link.put("title", name);
          	  	        }else{
          	  	        	link.put("title", url);
          	        	}
          	        	if (description.length() > 0) {
          	        		link.put("description", description);
          	        	}
          	        	if (attachedToField.length() > 0) {
          	        		link.put("attachedToField", attachedToField);
          	        	}          	        	
          	        	if (size.length() > 0) {
          	        		link.put("linkInfo", "[" + roundSize + " MB]");
          	        	}
          	        	
          	        	linkList.add(link);
            		}
        		}
        	}
        	if(linkList.size() > 0){
        		elements.add(element);
            }
        	
		}
	}
	
	private void getCapabilityUrls(ArrayList elements, String xpathExpression) {
	    if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            for (int i=0; i<nodeList.getLength();i++){
                if(XPathUtils.nodeExists(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/idf:idfOnlineResource/gmd:linkage/gmd:URL")){
                    Node node = XPathUtils.getNode(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/idf:idfOnlineResource/gmd:linkage/gmd:URL");                
                    String urlValue = XPathUtils.getString(node, ".").trim();
                    // do not display empty URLs
                    if (urlValue == null || urlValue.length() == 0) {
                        continue;
                    }
                    if (urlValue.toLowerCase().indexOf("request=getcapabilities") != -1) {
                        addBigMapLink(elements, urlValue);
                        // ADD FIRST ONE FOUND !!!
                        break;
                    }
                }
            }
	    }
	}
	
	private void addBigMapLink(ArrayList elements, String urlValue) {
	    if (urlValue != null) {
    	    HashMap elementCapabilities = new HashMap();
            elementCapabilities.put("type", "multiLine");
            HashMap elementMapLink = new HashMap();
            elementMapLink.put("type", "linkLine");
            elementMapLink.put("isMapLink", new Boolean(true));
            elementMapLink.put("isExtern", new Boolean(false));
            elementMapLink.put("title", messages.getString("common.result.showMap"));
            this.firstGetCapabiltiesUrl = /*"portal/main-maps.psml?wms_url=" +*/ UtilsVelocity.urlencode(urlValue);
            elementMapLink.put("href", this.firstGetCapabiltiesUrl);
            // put link in a list so that it is aligned correctly in detail view (<div class="width_two_thirds">)
            ArrayList list = new ArrayList();
            list.add(elementMapLink);
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
            elements.add(elementCapabilities);
	    }        
	}

	/** ONLY CALLED FOR CLASS 3 GEODATENDIENST !!! */
	private void getConnectionPoints(String serviceType, ArrayList elements, String xpathExpression) {
		// only render operations for Karte !?
		if (!"view".equals(serviceType)) {
			return;
		}

		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
	    	for (int i=0; i<nodeList.getLength();i++){
				if(XPathUtils.nodeExists(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL")){
					Node node = XPathUtils.getNode(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL");				
					String urlValue = XPathUtils.getString(node, ".").trim();
					// do not display empty URLs
					if (urlValue == null || urlValue.length() == 0) {
					    continue;
					}

					// do not display empty operations
					String operationName = XPathUtils.getString(nodeList.item(i), "./../srv:operationName").trim();
					if (operationName == null || operationName.length() == 0) {
					    continue;
					}

					if (operationName.equals("GetCapabilities")) {
						if (urlValue.toLowerCase().indexOf("request=getcapabilities") == -1) {
			    			if (urlValue.indexOf("?") == -1) {
			    				urlValue = urlValue + "?";
			    			}
			    			if (!urlValue.endsWith("?") && !urlValue.endsWith("&")) {
			    				urlValue = urlValue + "&";
			    			}
			    			urlValue = urlValue + "REQUEST=GetCapabilities&SERVICE=WMS";
			    		}

						HashMap elementCapabilities = new HashMap();
    					elementCapabilities.put("type", "textLabelLeft");
    					elementCapabilities.put("line", true);
//    					elementCapabilities.put("body", value.split("\\?")[0].toString());

    					HashMap elementCapabilitiesLink = new HashMap();
    					elementCapabilitiesLink.put("type", "linkLine");
    					elementCapabilitiesLink.put("hasLinkIcon", new Boolean(true));
    					elementCapabilitiesLink.put("isExtern", new Boolean(true));
    					elementCapabilitiesLink.put("title", urlValue);
    					elementCapabilitiesLink.put("href", urlValue);
//    					elementCapabilitiesLink.put("href", UtilsVelocity.urlencode(urlValue));
  						elementCapabilities.put("body", elementCapabilitiesLink);


  						boolean hasAccessConstraints = false;
  						xpathExpression = "./idf:hasAccessConstraint";
  						if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
  							String hasAccessConstraintsValue = XPathUtils.getString(rootNode, xpathExpression).trim();
  							if(hasAccessConstraintsValue.length() > 0){
  								hasAccessConstraints = Boolean.parseBoolean(hasAccessConstraintsValue);	
  							}
  						}

    					if (!hasAccessConstraints) {
    						elementCapabilities.put("title", messages.getString("common.result.showGetCapabilityUrl"));
    						if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)){
    	  			        	HashMap elementMapLink = new HashMap();
	    						elementMapLink.put("type", "linkLine");
	    						elementMapLink.put("hasLinkIcon", new Boolean(true));
	    						elementMapLink.put("isExtern", new Boolean(false));
	    						elementMapLink.put("title", messages.getString("common.result.showMap"));
	    						elementMapLink.put("href", "portal/main-maps.psml?wms_url=" + UtilsVelocity.urlencode(urlValue));
    	  						elementCapabilities.put("link", elementMapLink);
    	    					elementCapabilities.put("linkLeft", true);
    						}
			        	} else {
	    					// do not display "show in map" link if the map has access constraints
			        		elementCapabilities.put("title", messages.getString("common.result.showGetCapabilityUrlRestricted"));
			        	}

    					elements.add(elementCapabilities);
    					// ADD FIRST ONE FOUND !!!
    					break;
					}
				}
	    	}
		}
	}

	private ArrayList addTabData(String title, ArrayList elements) {
		ArrayList tmp = new ArrayList();
		if(elements.size() > 0){
			addSectionTitle(tmp, title);
			for(int i=0; i<elements.size();i++){
				tmp.add(elements.get(i));
			}
			closeDiv(tmp);
		}
		return tmp;
	}

	private void getServiceOperations(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList childNodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", messages.getString("t011_obj_serv_operation"));
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_serv_operation.name"));
			head.add(messages.getString("t011_obj_serv_operation.descr"));
			head.add(messages.getString("t011_obj_serv_operation.invocation_name"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i = 0; i < childNodeList.getLength(); i++) {
				ArrayList row = new ArrayList();
				xpathExpression = "./srv:operationName";
				if (XPathUtils.nodeExists(childNodeList.item(i), xpathExpression)) {
					String value = XPathUtils.getString(childNodeList.item(i), xpathExpression).trim();
					if (value.length() > 0){
						row.add(value);
					}else{
						row.add("");
					}
				}else{
					row.add("");
				}
				
				xpathExpression = "./srv:operationDescription";
				if (XPathUtils.nodeExists(childNodeList.item(i), xpathExpression)) {
					String value = XPathUtils.getString(childNodeList.item(i), xpathExpression).trim();
					if (value.length() > 0){
						row.add(value);
					}else{
						row.add("");
					}
				}else{
					row.add("");
				}
				
				xpathExpression = "./srv:invocationName";
				if (XPathUtils.nodeExists(childNodeList.item(i), xpathExpression)) {
					String value = XPathUtils.getString(childNodeList.item(i), xpathExpression).trim();
					if (value.length() > 0){
						row.add(value);
					}else{
						row.add("");
					}
				}else{
					row.add("");
				}
				
				if (!isEmptyRow(row)) {
					body.add(row);
				}
			}
			if (body.size() > 0) {
				elements.add(element);
			}
		}
	}
	
	private void getServiceUrlTable(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", messages.getString("t011_obj_serv_url"));
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_serv_url.name"));
			head.add(messages.getString("t011_obj_serv_url.url"));
			head.add(messages.getString("t011_obj_serv_url.description"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i=0; i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				ArrayList row = new ArrayList();
				
				// Name
				xpathExpression = "./srv:SV_OperationMetadata/srv:operationName";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String value = XPathUtils.getString(node, xpathExpression);
					row.add(value);
				}else{
					row.add("");
				}

				// URL
				xpathExpression = "./srv:SV_OperationMetadata/srv:connectPoint/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String value = XPathUtils.getString(node, xpathExpression);
					row.add(value);
				}else{
					row.add("");
				}
				
				// Beschreibung
				xpathExpression = "./srv:SV_OperationMetadata/srv:operationDescription";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String value = XPathUtils.getString(node, xpathExpression);
					row.add(value);
				}else{
					row.add("");
				}
				
				if (!isEmptyRow(row)) {
					body.add(row);
				}
			}
			if (body.size() > 0) {
				
				elements.add(element);
			}
		}
	}

	
	private void getAvailability(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				xpathExpression = "./gmd:distributionFormat/gmd:MD_Format";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
					HashMap element = new HashMap();
					element.put("type", "table");
					element.put("title", messages.getString("data_format"));
					
					ArrayList head = new ArrayList();
					head.add(messages.getString("t0110_avail_format.name"));
					head.add(messages.getString("t0110_avail_format.version"));
					head.add(messages.getString("t0110_avail_format.file_decompression_technique"));
					head.add(messages.getString("t0110_avail_format.specification"));
					element.put("head", head);
					ArrayList body = new ArrayList();
					element.put("body", body);
					
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node childNode = nodeList.item(i);
						ArrayList row = new ArrayList();
						
						String name = "";
						String version = "";
						
						xpathExpression = "./gmd:name";
						if (XPathUtils.nodeExists(childNode, xpathExpression)) {
							name = XPathUtils.getString(childNode, xpathExpression).trim();
						} 						
						
						xpathExpression = "./gmd:version";
						if (XPathUtils.nodeExists(childNode, xpathExpression)) {
							version = XPathUtils.getString(childNode, xpathExpression).trim();
						}
						
						if(!name.equals("Geographic Markup Language (GML)") && !version.equals("unknown")){
							xpathExpression = "./gmd:name";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								row.add(notNull(name));
							} else {
								row.add("");
							}
							
							xpathExpression = "./gmd:version";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								row.add(notNull(version));
							} else {
								row.add("");
							}
							
							xpathExpression = "./gmd:fileDecompressionTechnique";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								String value = XPathUtils.getString(childNode, xpathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
							
							xpathExpression = "./gmd:specification";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								String value = XPathUtils.getString(childNode, xpathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
						}
						
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
					if (body.size() > 0) {
						elements.add(element);
					}
				}
				xpathExpression = "./gmd:transferOptions";
				getMediumOptions(elements, xpathExpression, node);
				
				xpathExpression = "./gmd:distributor/gmd:MD_Distributor";
				getOrderingInformation(elements, xpathExpression, node);
			}
		}
	}
	
	private void getConformityData(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", messages.getString("object_conformity"));
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("object_conformity.specification"));
			head.add(messages.getString("object_conformity.degree_value"));
			head.add(messages.getString("object_conformity.publication_date"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if(XPathUtils.nodeExists(node, "./gmd:DQ_DomainConsistency")){
					ArrayList row = new ArrayList();
					
					xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:title";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						row.add(notNull(value));
					} else {
						row.add("");
					}
					
					xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:pass";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						if(XPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
							row.add(notNull(sysCodeList.getName("6000", "3")));	
						}else{
							if(value.equals("true")){
								row.add(notNull(sysCodeList.getName("6000", "1")));	
							}else if(value.equals("false")){
								row.add(notNull(sysCodeList.getName("6000", "2")));	
							}else{
								row.add("");
							}
						}
					} else {
						row.add("");	
					}
					
					xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						if(XPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
							row.add("");
						}else {
							String value = XPathUtils.getString(node, xpathExpression).trim();
							row.add(notNull(UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy")));
						}
					} else {
						row.add("");
					}
					
					if (!isEmptyRow(row)) {
						body.add(row);
					}
				}
			}
			if (body.size() > 0) {
				
				elements.add(element);
			}
		}
	}
	
	// "Veröffentlichung"
	private void getClassificationInformation(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String value = "";
				xpathExpression = "./gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:classification/gmd:MD_ClassificationCode";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					Node childNode = XPathUtils.getNode(node, xpathExpression);
					value = XPathUtils.getString(childNode, ".").trim();
				}
				if(value.length() < 1){
					xpathExpression = "./gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:classification/gmd:MD_ClassificationCode/@codeListValue";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						Node childNode = XPathUtils.getNode(node, xpathExpression);
						value = XPathUtils.getString(childNode, ".").trim();
					}
				}
				
				if (value.length() > 0) {
					String publishId = "";
					if (value.equals("unclassified")) {
						publishId = "1";
					} else if (value.equals("restricted")) {
						publishId = "2";
					} else if (value.equals("confidential")) {
						publishId = "3";
					}
					if (publishId.length() > 0) {
						addElementEntryLabelLeft(elements, messages.getString("t01_object.publish_id_" + publishId), messages.getString("t01_object.publish_id"));
					}
				}
			}
		}
	}
	
	// "Bestellinformationen"
	private void getOrderingInformation(ArrayList elements, String xpathExpression, Node node) {
		if (XPathUtils.nodeExists(node, xpathExpression)) {
			NodeList orderingNodeList = XPathUtils.getNodeList(node, xpathExpression);
			for (int i = 0; i < orderingNodeList.getLength(); i++) {
				Node childNode = orderingNodeList.item(i);
				xpathExpression = "./gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions";
				if (XPathUtils.nodeExists(childNode, xpathExpression)) {
					String content = XPathUtils.getString(childNode, xpathExpression).trim();
					addElementEntryLabelLeft(elements, content, messages.getString("t01_object.ordering_instructions"));
				}
			}
			
		}
	}
	
	// "Medien"
	private void getMediumOptions(ArrayList elements, String xpathExpression, Node parentNode) {
		if (XPathUtils.nodeExists(parentNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(parentNode, xpathExpression);
			
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", messages.getString("t0112_media_option.medium"));
			ArrayList head = new ArrayList();
			head.add(messages.getString("t0112_media_option.medium_name"));
			head.add(messages.getString("t0112_media_option.transfer_size") + " [MB]");
			head.add(messages.getString("t0112_media_option.medium_note"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				ArrayList row = new ArrayList();
				if(XPathUtils.nodeExists(node, "./gmd:MD_DigitalTransferOptions/gmd:offLine")){
					
					xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						row.add(notNull(sysCodeList.getNameByCodeListValue("520", value)));
					} else {
						row.add("");
					}
					
					xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						row.add(notNull(XPathUtils.getString(node, xpathExpression)).trim());
					} else {
						row.add("");
					}
					
					xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:mediumNote";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						row.add(notNull(XPathUtils.getString(node, xpathExpression)).trim());
					} else {
						row.add("");
					}
					
					if (!isEmptyRow(row)) {
						body.add(row);
					}
				}
			}
			if (body.size() > 0) {
				
				elements.add(element);
			}
		}
	}
	
	private void getReferenceObject(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for(int k=0; k<nodeList.getLength(); k++){
				Node node = nodeList.item(k);
				if (node.hasChildNodes()) {
					String nodeXPathExpression;
					nodeXPathExpression = "./gmd:spatialResolution/gmd:MD_Resolution";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
						ArrayList listDominator = new ArrayList();
						ArrayList listMeter = new ArrayList();
						ArrayList listDpi = new ArrayList();
						for (int j = 0; j < childNodeList.getLength(); j++) {
							xpathExpression = "./gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator";
							if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
								listDominator.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
							}
							
							xpathExpression = "./gmd:distance/gco:Distance[@uom='meter']";
							if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
								listMeter.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
							}
							
							xpathExpression = "./gmd:distance/gco:Distance[@uom='dpi']";
							if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
								listDpi.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
							}
						}
						
						HashMap element = new HashMap();
						element.put("type", "table");
						element.put("title", messages.getString("t011_obj_serv_scale"));
						
						ArrayList head = new ArrayList();
						head.add(messages.getString("t011_obj_serv_scale.scale").concat(" 1:x"));
						head.add(messages.getString("t011_obj_serv_scale.resolution_ground").concat(" m"));
						head.add(messages.getString("t011_obj_serv_scale.resolution_scan").concat(" dpi"));
						element.put("head", head);
						ArrayList body = new ArrayList();
						element.put("body", body);
						
						for (int i = 0; i < listDominator.size(); i++) {
							ArrayList row = new ArrayList();
							int value;
							
							value = listDominator.size();
							if (value != 0) {
								if(value > i){
									row.add(notNull((String) listDominator.get(i)));
								}else{
									row.add("");
								}
							} else {
								row.add("");
							}
							
							value = listMeter.size();
							if (value != 0) {
								if(value > i){
									row.add(notNull((String) listMeter.get(i)));	
								}else {
									row.add("");
								}
							} else {
								row.add("");
							}
							
							value = listDpi.size();
							if (value != 0) {
								if(value > i){
									row.add(notNull((String) listDpi.get(i)));									
								}else{
									row.add("");
								}
							} else {
								row.add("");
							}
							
							if (!isEmptyRow(row)) {
								body.add(row);
							}
						}
						if (body.size() > 0) {
							
							elements.add(element);
						}
					}
				}
			}
		}
	}
	
	private void getIndexInformationKeywords(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			addSectionTitle(elements, messages.getString("thesaurus"));
			ArrayList elementsSearch = new ArrayList();
			ArrayList elementsInspire = new ArrayList();
			ArrayList elementsGemet = new ArrayList();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String type = "";
				String thesaurusName = "";
				
				// type
				xpathExpression = "./gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					type = XPathUtils.getString(node, xpathExpression);
				}
				
				// thesaurus
				xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					thesaurusName = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// keywords
				xpathExpression = "./gmd:MD_Keywords/gmd:keyword";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList keywordNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int j = 0; j < keywordNodeList.getLength(); j++) {
						Node keywordNode = keywordNodeList.item(j);
						String value = XPathUtils.getString(keywordNode, ".").trim(); 
						if(value.length() < 1){
							value = XPathUtils.getString(keywordNode, ".").trim();
						}
						
						HashMap listEntry = new HashMap();
						listEntry.put("type", "textList");
						
						// "Service Classification, version 1.0"
						if (thesaurusName.indexOf("Service") < 0) {
							
							if(log.isDebugEnabled()){
								log.debug("Thesaurus Keywords: Thesaurus name '"+thesaurusName+"' and type '"+type+"' for value '"+value+"'");
							}
							
							// "UMTHES Thesaurus"
							if (thesaurusName.indexOf("UMTHES") > -1) {
								listEntry.put("body", value);
								if (!isEmptyList(listEntry)) {
									elementsSearch.add(listEntry);
								}
							// "GEMET - Concepts, version 2.1"
							} else if (thesaurusName.indexOf("Concepts") > -1) {
								String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
								if(tmpValue.length() < 1){
									tmpValue = value;
								}
								listEntry.put("body", tmpValue);
								if (!isEmptyList(listEntry)) {
									elementsGemet.add(listEntry);
								}
								// "GEMET - INSPIRE themes, version 1.0"
							} else if (thesaurusName.indexOf("INSPIRE") > -1) {
								String tmpValue = sysCodeList.getNameByCodeListValue("6100", value);
								if(tmpValue.length() < 1){
									tmpValue = value;
								}
								listEntry.put("body", tmpValue);
								if (!isEmptyList(listEntry)) {
									elementsInspire.add(listEntry);
								}
								// "German Environmental Classification - Category, version 1.0"
							} else if (thesaurusName.indexOf("German Environmental Classification") > -1) {
								// do not used in detail view.
								
							} else if (thesaurusName.length() < 1 && type.length() < 1) {
								listEntry.put("body", value);
								if (!isEmptyList(listEntry)) {
									elementsSearch.add(listEntry);
								}
							} else{
								listEntry.put("body", value);
								if (!isEmptyList(listEntry)) {
									elementsSearch.add(listEntry);
								}
							}
						}
					}
				}
			}
			
			if (elementsSearch.size() > 0) {
				HashMap element = new HashMap();
				element.put("type", "textList");
				element.put("title", messages.getString("search_terms"));
				element.put("textList", elementsSearch);
				elements.add(element);
			}
			
			if (elementsInspire.size() > 0) {
				HashMap element = new HashMap();
				element.put("type", "textList");
				element.put("title", messages.getString("inspire_themes"));
				element.put("textList", elementsInspire);
				elements.add(element);
			}
			
			if (elementsGemet.size() > 0) {
				HashMap element = new HashMap();
				element.put("type", "textList");
				element.put("title", "GEMET - Concepts");
				element.put("textList", elementsGemet);
				elements.add(element);
			}
			
			// "ISO-Themenkategorien"
			xpathExpression = "./gmd:identificationInfo/*/gmd:topicCategory";
			String subXPathExpression = "./gmd:MD_TopicCategoryCode";
			getNodeListValues(elements, xpathExpression, subXPathExpression , messages.getString("t011_obj_geo_topic_cat.topic_category"), "textList", "527");
			closeDiv(elements);
		}
	}
	
	
	private void getServiceClassification(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList elementsService = new ArrayList();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String type = "";
				String thesaurusName = "";
				
				// type
				xpathExpression = "./gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					type = XPathUtils.getString(node, xpathExpression);
				}
				
				// thesaurus
				xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					thesaurusName = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// keywords
				xpathExpression = "./gmd:MD_Keywords/gmd:keyword";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList keywordNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int j = 0; j < keywordNodeList.getLength(); j++) {
						Node keywordNode = keywordNodeList.item(j);
						String value = XPathUtils.getString(keywordNode, ".").trim(); 
						if(value.length() < 1){
							value = XPathUtils.getString(keywordNode, ".").trim();
						}
						
						HashMap listEntry = new HashMap();
						listEntry.put("type", "textList");
						
						// "Service Classification, version 1.0"
						if (thesaurusName.indexOf("Service") > -1) {
							
							if(log.isDebugEnabled()){
								log.debug("Service Classification: Thesaurus name '"+thesaurusName+"' and type '"+type+"' for value '"+value+"'");
							}
							
							String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
							if(tmpValue.length() < 1){
								tmpValue = value;
							}
							listEntry.put("body", tmpValue);
							if (!isEmptyList(listEntry)) {
								elementsService.add(listEntry);
							}
						} 
					}
				}
			}
			
			if (elementsService.size() > 0) {
				HashMap element = new HashMap();
				element.put("type", "textList");
				element.put("title", messages.getString("t011_obj_serv_type"));
				element.put("textList", elementsService);
				elements.add(element);
			}
		}
	}
	
    // "Addressen"
    private void getAddresses(ArrayList elements, String xpathExpression, boolean isForAdditionalInfo) {
        ArrayList elementsAddress = new ArrayList();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                if (childNode.hasChildNodes()) {
                    xpathExpression = "./idf:idfResponsibleParty";
                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
                        Node subNode = XPathUtils.getNode(childNode, xpathExpression);
                        addSingleAddress(elementsAddress, subNode, isForAdditionalInfo);    
                    }
                    xpathExpression = "./gmd:CI_ResponsibleParty";
                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
                        Node subNode = XPathUtils.getNode(childNode, xpathExpression);
                        addSingleAddress(elementsAddress, subNode, isForAdditionalInfo);    
                    }
                }
            }
        }
        
        // "Kontakte für idfResponsibleParty"
        xpathExpression = "./gmd:contact/idf:idfResponsibleParty";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++){
                addSingleAddress(elementsAddress, nodeList.item(i), isForAdditionalInfo);
            }
            
        }
        
        // "Kontakte für CI_ResponsibleParty"
        xpathExpression = "./gmd:contact/gmd:CI_ResponsibleParty";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++){
                addSingleAddress(elementsAddress, nodeList.item(i), isForAdditionalInfo);
            }
            
        }
        
        xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/idf:idfResponsibleParty";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++){
                addSingleAddress(elementsAddress, nodeList.item(i), isForAdditionalInfo);
            }
        }
        
        if(isForAdditionalInfo){
            if(elementsAddress != null && elementsAddress.size() > 0){
            	HashMap a = (HashMap) elementsAddress.get(0);
            	if(a != null){
        			ArrayList ae = (ArrayList) a.get("elementsAddress");
        			if(ae != null && ae.size() > 0){
        				elements.add(a);
        			}
        		}
            }
        }else{
        	HashMap elementAddress = new HashMap();
        	if(elementsAddress != null){
	        	switch (elementsAddress.size()) {
	        	case 1:
	        		HashMap a = (HashMap) elementsAddress.get(0);
	        		if(a != null){
	        			ArrayList ae = (ArrayList) a.get("elements");
	        			if(ae != null && ae.size() > 0){
	        				elementAddress.put("type", "multiLineAddresses");
	                        elementAddress.put("title", messages.getString("addresses"));
	                        elementAddress.put("id", "addresses_id");
	                        elementAddress.put("elementsAddress", elementsAddress);
	                        elements.add(elementAddress);
	        			}
	        		}
					break;
	
				default:
					if(elementsAddress.size() > 0){
						elementAddress.put("type", "multiLineAddresses");
		                elementAddress.put("title", messages.getString("addresses"));
		                elementAddress.put("id", "addresses_id");
		                elementAddress.put("elementsAddress", elementsAddress);
		                elements.add(elementAddress);
					}
					break;
	        	}
        	}
            
        }
    }
    
    private void addSingleAddress(ArrayList elementsAddress, Node node, boolean isForAdditionalInfo) {
        if (node != null) {
            HashMap element;
            String xpathExpression;
            boolean isIdfResponsibleParty = false;
            
            if(node.getLocalName().trim().equals("idfResponsibleParty")){
                isIdfResponsibleParty = true;
            }else{
                isIdfResponsibleParty = false;
            }
            
            xpathExpression = "./gmd:role/gmd:CI_RoleCode/@codeListValue";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                String role = XPathUtils.getString(node, xpathExpression).trim();
                String title = sysCodeList.getNameByCodeListValue("505", role);
                boolean isMetadatumContact = false;
                if(role.equals("pointOfContact")){
                    if(node.getParentNode().getNodeName().trim().equals("gmd:contact")){
                        isMetadatumContact = true;
                        title = title + " (" + messages.getString("pointofcontact_metadata")+ ")";
                    }
                }
                
                if (title.length() < 1) {
                    String newTitle = role;

                    if(role.equals("projectManager")){
                        newTitle = messages.getString("t011_obj_project.leader");
                    } else if (role.equals("projectParticipant")) {
                        newTitle = messages.getString("t011_obj_project.member");
                    }
                    title = newTitle;
                }
                
                
                if((isForAdditionalInfo && isMetadatumContact)){
                	// render only email of MetadatumContact, see INGRID32-146
                    addSingleMetaAddress(elementsAddress, node, title, isIdfResponsibleParty, true);
                }else if (!isForAdditionalInfo && !isMetadatumContact){
                    element = addElementAddress("multiLine", title, "", "false", new ArrayList());
                    ArrayList elements = (ArrayList) element.get("elements");
                    
                    addressEvaluateNode(elements, node, isIdfResponsibleParty, false);
                    
                    elementsAddress.add(element);
                }
            }
        }
    }
    
    private void addSingleMetaAddress(ArrayList elementsAddress, Node node, String title, Boolean isIdfResponsibleParty, boolean renderOnlyEmail) {
        
        ArrayList elementsMetaAddress = new ArrayList();
        String xpathExpression = "";
        ArrayList elements = new ArrayList();
        
        addressEvaluateNode(elements,node, isIdfResponsibleParty, renderOnlyEmail);
        if(elements.size() > 0){
        	HashMap element = addElementAddress("multiLine", null, "", "false", elements);
            elementsMetaAddress.add(element);
        }
        
        HashMap elementAddress = new HashMap();
        elementAddress.put("type", "multiLineAddresses");
        elementAddress.put("title", title);
        elementAddress.put("id", "addresses_id");
        elementAddress.put("elementsAddress", elementsMetaAddress);
        elementsAddress.add(elementAddress);
        
    }

    private void addressEvaluateNode(ArrayList elements, Node node, boolean isIdfResponsibleParty, boolean renderOnlyEmail) {

        String xpathExpression = "";
        
        // first extract email addresses to be used here ore later !
        // "Mail"
        ArrayList<HashMap> elementsEmail = new ArrayList<HashMap>();
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(node, xpathExpression);
            for (int i = 0; i < electronicMailAddressNodeList.getLength(); i++) {
                String value = XPathUtils.getString(electronicMailAddressNodeList.item(i), ".").trim();
                if(value != null && value.length() > 0){
                	elementsEmail.add(addElementEmailWeb(sysCodeList.getName("4430", "3"), value, value, value, LinkType.EMAIL));
                }
            }
        }
        if (renderOnlyEmail) {
            for (HashMap elementEmail : elementsEmail) {
            	elements.add(elementEmail);
            }
            return;
        }

        boolean dataAdded = false;

        if(isIdfResponsibleParty){
            if(XPathUtils.nodeExists(node, "./idf:hierarchyParty")){
                NodeList hierarchyPartyNodeList = XPathUtils.getNodeList(node, "./idf:hierarchyParty");
                for(int i=hierarchyPartyNodeList.getLength(); 0 < i ;i--){
                    Node hierarchyPartyNode = hierarchyPartyNodeList.item(i-1);
                    String uuid = "";
                    String value = "";
                    String type = "";
                    xpathExpression = "./@uuid";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        uuid = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                    }
                    // "Type"
                    xpathExpression = "./idf:addressType";
                    if(XPathUtils.nodeExists(hierarchyPartyNode, xpathExpression)){
                        type = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                    }
                    
                    if(type.equals("2")){
                    	// Person -> "Name"
                        xpathExpression = "./idf:addressIndividualName";
                        if(XPathUtils.nodeExists(hierarchyPartyNode, xpathExpression)){
                            value = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), getIndividualName(value), uuid));
                        }
                    }else if(type.equals("3")){
                    	// Free Address -> "Name" or "Organisation"
                        if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressIndividualName")){
                            value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressIndividualName").trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), getIndividualName(value), uuid));
                        }else if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressOrganisationName")){
                            value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressOrganisationName").trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
                        }
                    }else{
                        // "Organisation"
                        xpathExpression = "./idf:addressOrganisationName";
                        if(XPathUtils.nodeExists(hierarchyPartyNode, xpathExpression)){
                            value = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
                        }
                    }
                    dataAdded = true;
                }
            }else{
                // Organistation
                xpathExpression = "./gmd:organisationName";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    String value = XPathUtils.getString(node, xpathExpression).trim();
                    StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
                    for(int i=0; i<valueTokenizer.countTokens();i++) {
                        addElement(elements, "textLine", valueTokenizer.nextToken());
                    }
                    dataAdded = true;
                }
                
                // Name
                xpathExpression = "./gmd:individualName";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    String value = XPathUtils.getString(node, xpathExpression).trim();
                    addElement(elements, "textLine", getIndividualName(value));
                    dataAdded = true;
                }
            }
        }else{
            // Organistation
            xpathExpression = "./gmd:organisationName";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                String value = XPathUtils.getString(node, xpathExpression).trim();
                StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
                for(int i=0; i<valueTokenizer.countTokens();i++) {
                    addElement(elements, "textLine", valueTokenizer.nextToken());
                }
                dataAdded = true;
            }
            
            // Name
            xpathExpression = "./gmd:individualName";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                String value = XPathUtils.getString(node, xpathExpression).trim();
                addElement(elements, "textLine", getIndividualName(value));
                dataAdded = true;
            }
        }
        
        // "Position"
        /* NOT IN USED
        xpathExpression = "./gmd:positionName";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String position = XPathUtils.getString(node, xpathExpression).trim();
            if (position != null) {
                position = position.replaceAll("\n", "<br/>");
                position = position.replaceAll("&lt;", "<");
                position = position.replaceAll("&gt;", ">");
            }
            addElement(elements, "textLine", position);
        }
        */
        if (XPathUtils.nodeExists(node, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address")){
        	if (dataAdded) {
                addSpace(elements);        		
            	dataAdded = false;
        	}
        }
        
        // "Street"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
            for(int i=0; i<nodeList.getLength();i++){
                String deliveryPoint = XPathUtils.getString(nodeList.item(i), ".").trim();
                if(deliveryPoint.startsWith("Postbox")){
                    String[] postbox = deliveryPoint.split(",");
                    for(int j=0; j < postbox.length; j++){
                        if(postbox[j].startsWith("Postbox")){
                            addElement(elements, "textLine", messages.getString("postbox_label") + " " + postbox[j].replace("Postbox ", ""));
                        }else{
                            addElement(elements, "textLine", postbox[j]);
                        }
                    }
                    addSpace(elements);
                }else if(deliveryPoint.matches("\\d*")){
                    addElement(elements, "textLine", messages.getString("postbox_label") + " " + deliveryPoint);
                }else{
                    addElement(elements, "textLine", deliveryPoint);
                }
            }
            dataAdded = true;
        }
        
        String postalCode = "";
        String city = "";
        
        // "Postcode"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            postalCode = XPathUtils.getString(node, xpathExpression).trim();
        }
        
        // "City"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            city = XPathUtils.getString(node, xpathExpression).trim();
        }
        
        if (postalCode.length() > 0 || city.length() > 0) {
            addElement(elements, "textLine", postalCode.concat(" ").concat(city));
            dataAdded = true;
        }
        
        // "Country"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String country = XPathUtils.getString(node, xpathExpression).trim();
            if(country != null){
                String value = UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(country), this.request.getLocale().getLanguage().toString());
                if(value != null){
                    addElement(elements, "textLine", value);
                }else{
                    addElement(elements, "textLine", country);
                }
            }
            dataAdded = true;
        }

        if (dataAdded) {
            addSpace(elements);
        }
        // "Mail"
        // already extracted above !
        for (HashMap elementEmail : elementsEmail) {
        	elements.add(elementEmail);
        }
        
        // "Telefon"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String value = XPathUtils.getString(node, xpathExpression).trim();
            if(value != null && value.length() > 0){
                addElement(elements, "textLine", value, sysCodeList.getName("4430", "1"));
            }
        }
        
        // "Fax"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String value = XPathUtils.getString(node, xpathExpression).trim();
            if(value != null && value.length() > 0){
                addElement(elements, "textLine", value, sysCodeList.getName("4430", "2"));
            }
        }
        
        // "URL"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String value = XPathUtils.getString(node, xpathExpression).trim();
            if(value != null && value.length() > 0){
                elements.add(addElementEmailWeb(sysCodeList.getName("4430", "4"), value, value, value, LinkType.WWW_URL));
            }
        }
        
    }

    private void getTimeSection(ArrayList elements, String xpathExpression) {
        ArrayList timeElements = new ArrayList();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for(int j=0; j<nodeList.getLength();j++){
                Node node = nodeList.item(j);
                NodeList nodeListTemporalElement = XPathUtils.getNodeList(node, "./gmd:temporalElement");
                for(int i=0; i<nodeListTemporalElement.getLength();i++){
                    Node childNode = nodeListTemporalElement.item(i);
                    String beginPosition = "";
                    String endPosition = "";
                    xpathExpression = "./gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition";
                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
                        beginPosition = XPathUtils.getString(childNode, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition";
                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
                        endPosition = XPathUtils.getString(childNode, xpathExpression).trim();
                    }
                    String entryLine = "";
                    if (beginPosition.length() > 0 || endPosition.length() > 0) {
                        if (beginPosition.equals(endPosition)) {
                            entryLine = entryLine.concat(messages.getString("search.detail.time.at")).concat(": ");
                            entryLine = entryLine.concat(UtilsDate.convertDateString(beginPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
                        } else if (beginPosition.length() > 0 && endPosition.length() < 1) {
                            entryLine = entryLine.concat(messages.getString("search.detail.time.since")).concat(": ");
                            entryLine = entryLine.concat(UtilsDate.convertDateString(beginPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
                        } else if (beginPosition.length() < 1 && endPosition.length() > 0) {
                            entryLine = entryLine.concat(messages.getString("search.detail.time.to")).concat(": ");
                            entryLine = entryLine.concat(UtilsDate.convertDateString(endPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
                        } else if (!beginPosition.equals(endPosition)) {
                            entryLine = entryLine.concat(messages.getString("search.detail.time.from")).concat(": ");
                            entryLine = entryLine.concat(UtilsDate.convertDateString(beginPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
                            entryLine = entryLine.concat(" ");
                            entryLine = entryLine.concat(messages.getString("search.detail.time.to")).concat(": ");
                            entryLine = entryLine.concat(UtilsDate.convertDateString(endPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
                        }
                        if (entryLine.length() > 0) {
                            // "Durch die Ressource abgedeckte Zeitspanne"
                            addElementEntryLabelLeft(timeElements, entryLine, messages.getString("time_reference_content"));
                        }
                    }
                }
            }
        }
                
        // "Status"
        xpathExpression = "./*/*/gmd:status/gmd:MD_ProgressCode";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node childNode = XPathUtils.getNode(rootNode, xpathExpression);
            String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
            addElementEntryLabelLeft(timeElements, sysCodeList.getNameByCodeListValue("523", domainValue), messages.getString("t01_object.time_status"));
        }
        
        // "Periodizität"
        xpathExpression = "./*/*/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node childNode = XPathUtils.getNode(rootNode, xpathExpression);
            String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
            addElementEntryLabelLeft(timeElements, sysCodeList.getNameByCodeListValue("518", domainValue), messages.getString("t01_object.time_period"));
        }
        
        // "Intervall der Erhebung"
        xpathExpression = "./*/*/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:userDefinedMaintenanceFrequency";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node childNode = XPathUtils.getNode(rootNode, xpathExpression);
            String content = XPathUtils.getString(childNode, ".").trim();
            String value = new TM_PeriodDurationToTimeAlle().parse(content);
            String unit = new TM_PeriodDurationToTimeInterval().parse(content);
            addElementEntryLabelLeft(timeElements, value.concat(" ").concat(unit), messages.getString("t01_object.time_interval"));
        }
        
        // "Zeitbezug der Ressource"
        xpathExpression = "./*/*/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList childNodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < childNodeList.getLength(); i++) {
                String time = "";
                xpathExpression = "./gmd:date";
                if (XPathUtils.nodeExists(childNodeList.item(i), xpathExpression)) {
                    time = XPathUtils.getString(childNodeList.item(i), xpathExpression).trim();
                }
                String type = "";
                xpathExpression = "./gmd:dateType/gmd:CI_DateTypeCode/@codeListValue";
                if (XPathUtils.nodeExists(childNodeList.item(i), xpathExpression)) {
                    type = XPathUtils.getString(childNodeList.item(i), xpathExpression).trim();
                }
                
                String typeId = "";
                if (type.equals("creation")) {
                    typeId = "1";
                }else if (type.equals("publication")) {
                    typeId = "2";
                }else if (type.equals("revision")) {
                    typeId = "3";
                }
                addElementEntryLabelLeft(timeElements, UtilsDate.convertDateString(time, "yyyy-MM-dd", "dd.MM.yyyy"), sysCodeList.getName("502", typeId));
            }
        }
        
        // "Erläuterung zum Zeitbezug"
        xpathExpression = "./*/*/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceNote";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node childNode = XPathUtils.getNode(rootNode, xpathExpression);
            addElementEntryLabelLeft(timeElements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.time_descr"));
        }
        
        // "Raum/Zeit - Zusätzliche Felder"
        xpathExpression ="./idf:additionalDataSection";
        getAdditionalFields(timeElements, xpathExpression, TimeSpatialType.TIME);
        
        if(timeElements.size()>0){
            addSectionTitle(elements, messages.getString("time_reference"));
            for(int i=0; i < timeElements.size(); i++){
                elements.add(timeElements.get(i));
            }
            closeDiv(elements); 
        }
    }
    
    private void getAreaSection(ArrayList elements, String xpathExpression) {
        ArrayList spatialElements = new ArrayList();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList subjectEntries = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                // "Administrative Einheit (Gemeindenummer)"
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code";
                Node node = nodeList.item(i);
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList childNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    HashMap element = new HashMap();
                    element.put("type", "textList");
                    element.put("title", messages.getString("t011_township.township_no"));
                    ArrayList textListEntries = new ArrayList();
                    element.put("textList", textListEntries);
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        String domainValue = XPathUtils.getString(childNodeList.item(j), ".").trim();
                        HashMap listEntry = new HashMap();
                        listEntry.put("type", "textList");
                        listEntry.put("body", domainValue);
                        subjectEntries.add(domainValue);
                        if (!isEmptyList(listEntry)) {
                            textListEntries.add(listEntry);
                        }
                    }
                    spatialElements.add(element);
                    
                }
                
                // "Geothesaurus-Raumbezug"
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicBoundingBox";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList subNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    HashMap element = new HashMap();
                    element.put("type", "table");
                    element.put("title", messages.getString("geothesaurus_spacial_reference"));
                    
                    ArrayList head = new ArrayList();
                    head.add(messages.getString("geothesaurus_spacial_reference"));
                    head.add(messages.getString("spatial_ref_value_x1"));
                    head.add(messages.getString("spatial_ref_value_y1"));
                    head.add(messages.getString("spatial_ref_value_x2"));
                    head.add(messages.getString("spatial_ref_value_y2"));
                    element.put("head", head);
                    ArrayList body = new ArrayList();
                    element.put("body", body);
                    
                    ArrayList numbers = new ArrayList();
                    numbers.add(subjectEntries.size());
                    numbers.add(subNodeList.getLength());
                    int maxRows = getGreatestInt(numbers); 
                    
                    
                    for (int j = 0; j < maxRows; j++) {
                        Node childNode = subNodeList.item(j);
                        ArrayList row = new ArrayList();
                        
                        if(subjectEntries.size() > 0){
                            if (subjectEntries.get(j)!= null) {
                                row.add(subjectEntries.get(j));
                            }else {
                                row.add("");
                            }
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:westBoundLongitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:southBoundLatitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:eastBoundLongitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:northBoundLatitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        if (!isEmptyRow(row)) {
                            body.add(row);
                        }
                    }
                    
                    if (body.size() > 0) {
                        
                        spatialElements.add(element);
                    }
                }
                
                // "Höhe"
                xpathExpression = "./gmd:verticalElement";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    HashMap element = new HashMap();
                    element.put("type", "table");
                    element.put("title", messages.getString("t01_object.vertical_extent"));
                    
                    ArrayList head = new ArrayList();
                    head.add(messages.getString("t01_object.vertical_extent_maximum"));
                    head.add(messages.getString("t01_object.vertical_extent_minimum"));
                    head.add(messages.getString("t01_object.vertical_extent_unit"));
                    head.add(messages.getString("t01_object.vertical_extent_vdatum"));
                    element.put("head", head);
                    ArrayList body = new ArrayList();
                    element.put("body", body);
                    if (XPathUtils.nodeExists(node, "./gmd:verticalElement/gmd:EX_VerticalExtent")) {
                        NodeList subNodeList = XPathUtils.getNodeList(node, "./gmd:verticalElement/gmd:EX_VerticalExtent");
                        for (int j = 0; j < subNodeList.getLength(); j++) {
                            Node childNode = subNodeList.item(j);
                            ArrayList row = new ArrayList();
                            
                            // "Maximum"
                            xpathExpression = "./gmd:maximumValue";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                row.add(notNull(XPathUtils.getString(childNode, xpathExpression)).trim());
                            }
                            
                            // "Minimum"
                            xpathExpression = "./gmd:minimumValue";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                row.add(notNull(XPathUtils.getString(childNode, xpathExpression)).trim());
                            }
                            
                            String rowValue;
                            // "Maßeinheit"
                            xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
                            }else{
                                row.add("");
                            }
                            
                            // "Vertikaldatum"
                            String verticalDatum = "" ; 
                            xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                    verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                }else{
                                    verticalDatum = rowValue;
                                }
                            }
                            
                            if(verticalDatum.length() < 1)
                                xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:identifier";
                                if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                    rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                    if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                        verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                    }else{
                                        verticalDatum = rowValue;
                                    }
                                }
                            if(verticalDatum.length() < 1){
                                xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:name";
                                if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                    rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                    if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                        verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                    }else{
                                        verticalDatum = rowValue;
                                    }
                                }
                            }
                            
                            row.add(verticalDatum);
                            if (!isEmptyRow(row)) {
                                body.add(row);
                            }
                        }
                    }
                    if (body.size() > 0) {
                        
                        spatialElements.add(element);
                    }
                    
                }
                
                // "Erläuterung zum Raumbezug"
                xpathExpression = "./gmd:description";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    Node childNode = XPathUtils.getNode(node, xpathExpression);
                    addElementEntryLabelLeft(spatialElements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.loc_descr"));
                }
            }
        }
        
        // "Raumbezugssystem"
        xpathExpression = "./gmd:referenceSystemInfo";
        getNodeListValueReferenceSystem(spatialElements, xpathExpression, messages.getString("t011_obj_geo.referencesystem_id"));
        
        // "Raum/Zeit - Zusätzliche Felder" 
        xpathExpression ="./idf:additionalDataSection";
        getAdditionalFields(spatialElements, xpathExpression, TimeSpatialType.SPATIAL);
        
        if(spatialElements.size() > 0){
            addSectionTitle(elements, messages.getString("t011_obj_geo.coord"));
            for(int i=0; i < spatialElements.size(); i++){
                elements.add(spatialElements.get(i));
            }
            closeDiv(elements); 
        }
    }
    
    private void getNodeListValueReferenceSystem(ArrayList elements, String xpathExpression, String title) {
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList referenceSystem = new ArrayList();
            for (int i=0; i<nodeList.getLength(); i++){
                String codeSpace = ""; 
                String code = "";
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code";
                if (XPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    code = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }
                
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:codeSpace";
                if (XPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    codeSpace = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }
                
                String value = "";
                if(code.length() > 0 && codeSpace.length() > 0){
                    if(code.indexOf("EPSG") > -1){
                        value = code;
                    }else{
                        value = codeSpace.concat(": " + code);
                    }
                }else if(codeSpace.length() > 0){
                    value = codeSpace;
                }else if(code.length() > 0){
                    value = code;
                }
                
                if(value.length() > 0){
                    HashMap listEntry = new HashMap();
                    listEntry.put("type", "textList");
                    listEntry.put("body", value);
                    if (!isEmptyList(listEntry)) {
                        referenceSystem.add(listEntry);
                    }
                }
            }
            if(referenceSystem.size() > 0){
                HashMap element = new HashMap();
                element.put("type", "textList");
                element.put("title", title);
                element.put("textList", referenceSystem);
                
                elements.add(element);
            }
        }
    }

    private void getGeneralTab(ArrayList elements, String xpathExpression) {
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if (node.hasChildNodes()) {
                xpathExpression = "./gmd:abstract";
                
                // Beschreibung
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    String alternateName = "";
                    String description = "";
                    
                    // alternate name
                    xpathExpression = "./gmd:citation/gmd:CI_Citation/gmd:alternateTitle";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        alternateName = XPathUtils.getString(node, xpathExpression).trim();
                    }
                    
                    // description
                    xpathExpression = "./gmd:abstract";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        description = XPathUtils.getString(node, xpathExpression).trim();
                        
                        if (description != null) {
                            description = description.replaceAll("\n", "<br/>");
                            description = description.replaceAll("&lt;", "<");
                            description = description.replaceAll("&gt;", ">");
                        }
                    }
                    
                    String capabilitiesUrl = getCapabilityUrl();
                    if ((description.length() > 0) || alternateName.length() > 0 || capabilitiesUrl != null) {
                        addSectionTitle(elements, messages.getString("detail_description"));
                        
                        // showMap-Link
                        if (context.get(UDK_OBJ_CLASS_TYPE).equals("1")) {
                            // search for it in onlineResources
                            xpathExpression = "./gmd:distributionInfo/*/gmd:transferOptions";
                            getCapabilityUrls(elements, xpathExpression);
                        } else if (context.get(UDK_OBJ_CLASS_TYPE).equals("3")) {
                            // get it directly from the operation
                            addBigMapLink(elements, capabilitiesUrl);
                        }
                        
                        addElementEntryLabelAbove(elements, description, alternateName, false);
                        if(context.get("description") == null){
                            context.put("description", description);
                        }
                        
                        // "Übergeordnete Objekte"
                        xpathExpression ="./idf:superiorReference";
                        getReference(elements, xpathExpression, ReferenceType.SUPERIOR);
                        
                        // "Untergeordnete Objekte"
                        xpathExpression ="./idf:subordinatedReference";
                        getReference(elements, xpathExpression, ReferenceType.SUBORDINATE);
                        
                        // close description
                        closeDiv(elements);
                    }
                }
                
                xpathExpression = "./gmd:descriptiveKeywords";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    addReference(node);
                }
            }
        }
    }
    
    // "Querverweise"
    private HashMap addReference(Node node) {
        ArrayList keywords = new ArrayList();
        String xpathExpression = "//gmd:keyword";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            NodeList nodeListKeywords = XPathUtils.getNodeList(node, xpathExpression);
            for (int i = 0; i < nodeListKeywords.getLength(); i++) {
                Node keywordNode = nodeListKeywords.item(i);
                HashMap listEntry = new HashMap();
                listEntry.put("type", "textList");
                listEntry.put("body", XPathUtils.getString(keywordNode, ".").trim());
                keywords.add(listEntry);
            }
        }
        HashMap element = new HashMap();
        element.put("type", "textList");
        element.put("title", messages.getString("cross_references"));
        element.put("textList", keywords);
        
        return element;
    }
    
    private HashMap addElementEmailWeb(String title, String href, String body, String altText, LinkType linkType) {
        HashMap element = new HashMap();
        element.put("type", "textLinkLine");
        element.put("title", title);
        element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(href)));
        element.put("body", UtilsString.htmlescapeAll(body));
        element.put("altText", UtilsString.htmlescapeAll(altText));
        switch (linkType) {
            case EMAIL:
                element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(href)));
                break;
            case WWW_URL:
                if (href.startsWith("http")) {
                    element.put("href", href);
                } else {
                    element.put("href", "http://".concat(href));
                }
                break;
            default:
                break;
        }
        
        return element;
    }
    
    private void addElement(ArrayList elements, String type, String body) {
        addElement(elements, type, body, null);
    }
    
    private void addElement(ArrayList elements, String type, String body, String title) {
        HashMap element = new HashMap();
        if (type != null)
            element.put("type", type);
        if (body != null)
            element.put("body", body);
        if (title != null)
            element.put("title", title);
        
        elements.add(element);
    }
    
    
    private HashMap addElementLink(String type, Boolean hasLinkIcon, Boolean isExtern, String title, String uuid) {
        HashMap element = new HashMap();
        if (type != null)
            element.put("type", type);
        if (title != null)
            element.put("title", title);
        if (hasLinkIcon)
            element.put("sort", hasLinkIcon);
        if (isExtern != null)
            element.put("isExtern", isExtern);
        
        if (this.iPlugId != null){
            if(uuid != null){
                if(log.isDebugEnabled()){
                    log.debug("Create URL with iPlug: '" + this.iPlugId + "' and UUID: '" + uuid +"'");
                }
                PortletURL actionUrl = response.createActionURL();
                actionUrl.setParameter("cmd", "doShowAddressDetail");
                actionUrl.setParameter("addrId", uuid);
                actionUrl.setParameter("plugid", this.iPlugId);
                element.put("href", actionUrl.toString());
            }else{
                element.put("href", "");
            }
        }else{
            element.put("href", "");
        }
            
        return element;
    }
    
    private HashMap addElementAddress(String type, String title, String body, String sort, ArrayList elements) {
        HashMap element = new HashMap();
        if (type != null)
            element.put("type", type);
        if (title != null)
            element.put("title", title);
        if (sort != null)
            element.put("sort", sort);
        if (body != null)
            element.put("body", body);
        if (elements != null)
            element.put("elements", elements);
        
        return element;
    }
    
    public int getGreatestInt(ArrayList numbers) {
        int i = 0;
        int maximum = Integer.parseInt(numbers.get(i).toString());
        while (i < numbers.size()) {
            if (Integer.parseInt(numbers.get(i).toString()) > maximum) {
                maximum =  Integer.parseInt(numbers.get(i).toString());
            }
            i++;
        }
        return maximum;
    }
    
    private void getUdkObjClass(HashMap element, ArrayList elements) {
        if (rootNode != null) {
            getUdkObjectClassType(".");
            element.put("udkObjClass", (String) context.get(UDK_OBJ_CLASS_TYPE));
            if(context.get("udkObjClass") == null){
                context.put("udkObjClass", (String) context.get(UDK_OBJ_CLASS_TYPE));
            }
            if(context.get("udkObjClassName") == null){
                context.put("udkObjClassName", messages.getString("udk_obj_class_name_".concat((String) context.get(UDK_OBJ_CLASS_TYPE))));
            }
            addElementUdkClass(elements,(String) context.get(UDK_OBJ_CLASS_TYPE));
        }
    }

    private void getTitle(String xpathExpression, HashMap element) {
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            String title = XPathUtils.getString(rootNode, xpathExpression).trim();
            element.put("title", title);
            if(context.get("title") == null){
                context.put("title", title);
            }
        }else{
            element.put("title", "No title");
        }
    }

    private void getUdkObjectClassType(String xpathExpression) {
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(node.hasChildNodes()){
                String hierachyLevel = "";
                String hierachyLevelName = "";
                
                xpathExpression = "./gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevel = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                xpathExpression = "./gmd:hierarchyLevelName";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevelName = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                if(log.isDebugEnabled()){
                    log.debug("IDF hierachyLevel: '" + hierachyLevel + "' and IDF hierachyLevelName: '" + hierachyLevelName+ "'");
                }
                
                if(hierachyLevel.equals("service")){
                    context.put(UDK_OBJ_CLASS_TYPE, "3");
                }else if(hierachyLevel.equals("application")){
                    context.put(UDK_OBJ_CLASS_TYPE, "6");
                }else if(hierachyLevelName.equals("job") && hierachyLevel.equals("nonGeographicDataset")){
                    context.put(UDK_OBJ_CLASS_TYPE, "0");
                }else if(hierachyLevelName.equals("document") && hierachyLevel.equals("nonGeographicDataset")){
                    context.put(UDK_OBJ_CLASS_TYPE, "2");
                }else if(hierachyLevelName.equals("project") && hierachyLevel.equals("nonGeographicDataset")){
                    context.put(UDK_OBJ_CLASS_TYPE, "4");
                }else if(hierachyLevelName.equals("database") && hierachyLevel.equals("nonGeographicDataset")){
                    context.put(UDK_OBJ_CLASS_TYPE, "5");
                }else if(hierachyLevel.equals("dataset") || hierachyLevel.equals("series")){
                    context.put(UDK_OBJ_CLASS_TYPE, "1");
                }
            }
        }       
    }
}