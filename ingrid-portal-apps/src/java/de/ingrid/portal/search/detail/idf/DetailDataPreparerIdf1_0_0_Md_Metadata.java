package de.ingrid.portal.search.detail.idf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeAlle;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeInterval;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.xml.XPathUtils;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0_Md_Metadata extends DetailDataPreparerIdf1_0_0 {
	
	private final static Log	log	= LogFactory.getLog(DetailDataPreparerIdf1_0_0_Md_Metadata.class);
	
	public enum LinkType {
		EMAIL, WWW_URL
	}
	
	public DetailDataPreparerIdf1_0_0_Md_Metadata(Node node, Context context, RenderRequest request) {
		super(node, context, request);
		this.rootNode = node;
		this.context = context;
		this.request = request;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public void prepare(HashMap data) {
		
		if (rootNode != null) {
			
			initialArrayLists(data);
			// check for hierachyLevelName
			String metadataDataNodePath = "";
			if (context.get(UDK_OBJ_CLASS_TYPE) != null) {
				if (context.get(UDK_OBJ_CLASS_TYPE).equals("6")) {
					metadataDataNodePath = "srv:SV_ServiceIdentification";
				} else {
					metadataDataNodePath = "gmd:MD_DataIdentification";
				}
			}
			
			String xpathExpression;
			String subXPathExpression;
			
	// Tab "General"
			// Description
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath;
			getGeneralTab(elementsGeneral, xpathExpression);
			
			// Addresses
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:pointOfContact";
			getAddresses(elementsGeneral, xpathExpression);
			
	// Tab "Verweise"
			// "ISO-Themenkategorien"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:topicCategory";
			subXPathExpression = "gmd:MD_TopicCategoryCode";
			getNodeListValues(elementsReference, xpathExpression, subXPathExpression , messages.getString("t011_obj_geo_topic_cat.topic_category"), "textList", "527");
			
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:descriptiveKeywords";
			getIndexInformationKeywords(elementsReference, xpathExpression);
			
	// Tab "Verfügbarkeit"
			// "Datenformat"
			xpathExpression = "gmd:distributionInfo/gmd:MD_Distribution";
			getAvailability(elementsAvailability, xpathExpression);
			
			// "Nutzungsbedingungen"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation";
			subXPathExpression = ".";
			getNodeListValues(elementsAvailability, xpathExpression, subXPathExpression, messages.getString("object_access.terms_of_use"), "textList");
			
			// "Zugangsbeschränkungen"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints";
			subXPathExpression = ".";
			getNodeListValues(elementsAvailability, xpathExpression, subXPathExpression, messages.getString("object_access.restriction_value"), "textList", "6010");
			
			
	// Tab "Raum/Zeit"
			// AREA
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/*/gmd:EX_Extent";
			getAreaSection(elementsAreaTime, xpathExpression);
			
			// TIME
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/*/gmd:EX_Extent";
			getTimeSection(elementsAreaTime, xpathExpression);
			
	// Tab "Zusätzliche Info"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath;
			getClassificationInformation(elementsAdditionalInfo, xpathExpression);
			
			// "Sprache des Metadatensatzes"
			xpathExpression = "gmd:language";
			subXPathExpression = "gmd:LanguageCode/@codeListValue";
			getNodeListValuesLanguage(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t01_object.metadata_language"), "textList");
			
			// "Sprache des Datensatzes"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:language";
			subXPathExpression = "gmd:LanguageCode/@codeListValue";
			getNodeListValuesLanguage(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t01_object.data_language"), "textList");
			
			// "Eignung/Nutzung"
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:resourceSpecificUsage/gmd:MD_Usage/gmd:specificUsage";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.dataset_usage"));
			
			// "Herstellungszweck"
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:purpose";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.info_note"));
			
			// "Konformität"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality";
			getConformityData(elementsAdditionalInfo, xpathExpression);
			
			// "Objekt-ID"
			xpathExpression = "gmd:fileIdentifier";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.obj_id"));
			
			// "Elternobjekt"
			xpathExpression = "gmd:parentIdentifier";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t012_obj_obj.object_from_id"));
			
			// "Zeichensatz des Metadatensatzes"
			xpathExpression = "gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_character_set"), "510");
			
			// "Zeichensatz des Datensatzes"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.dataset_character_set"), "510");
			
			// "Datensatz/Datenserie"
			xpathExpression = "gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t011_obj_geo.hierarchy_level"), "525");
			
			// "ID der Objektklasse" 
			xpathExpression = "gmd:hierarchyLevelName";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.obj_class"));
			
			// "Geändert am"
			xpathExpression = "gmd:dateStamp";
			getNodeValueForDate(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.mod_time"));
			
			// "Metadatenstandardname"
			xpathExpression = "gmd:metadataStandardName";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_standard_name"));
			
			// "Metadatenstandardversion"
			xpathExpression = "gmd:metadataStandardVersion";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_standard_version"));
			
			// "Dokumenttyp"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceFormat";
			subXPathExpression = "gmd:MD_Format/gmd:name";
			getNodeListValues(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t011_obj_literatur.typ"), "textList");
			
			// "Digitale Repräsentation"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType";
			subXPathExpression = "gmd:MD_SpatialRepresentationTypeCode/@codeListValue";
			getNodeListValues(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_spatial_rep.type"), "textList", "526");
			
	// Tab "Fachbezug"
			// "Art des Dienstes"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/srv:serviceType";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.type"), "5100");

			// "Systemumgebung"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:environmentDescription";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.environment"));
			
			// "Historie"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.history"));
			
			// "Basisdaten"			
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.base"));
			
			// "Fachliche Grundlage"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.special_base"));
			
			// "Erläuterung zum Fachbezug"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:environmentDescription";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.description"));
			
			// "Erläuterung zum Fachbezug"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:supplementalInformation";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_data.description"));
			
			// "Version"
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceTypeVersion";
			subXPathExpression = ".";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_version.version"), "textList");
			
			// "Erstellungsmaßstab"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification";
			getReferenceObject(elementsSubject, xpathExpression);
			
			// "Operation"
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations";
			getServiceOperations(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint";
			getConnectionPoints(elementsSubject, xpathExpression);
			
			// "Identifikator der Datenquelle"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier";
			subXPathExpression ="gmd:RS_Identifier/gmd:code";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo.datasource_uuid"), "textList");	
				
			// "Erfassungsgrad"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_CompletenessCommission/gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.rec_grade"));
			
			// "Lagegenauigkeit"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_RelativeInternalPositionalAccuracy/gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.rec_exact"));
			
			// "Topologieinformation"
			xpathExpression ="gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:topologyLevel/gmd:MD_TopologyLevelCode/@codeListValue";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.vector_topology_level"), "528");
			
			// "Geometrietyp"
			xpathExpression = "gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:geometricObjects";
			subXPathExpression = "gmd:MD_GeometricObjects/gmd:geometricObjectType/gmd:MD_GeometricObjectTypeCode/@codeListValue";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_vector.geometric_object_type"), "textList", "515");
			
			// "Elementanzahl"
			xpathExpression = "gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:geometricObjects";
			subXPathExpression = "MD_GeometricObjects/geometricObjectCount";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_vector.geometric_object_count"), "textList");
			
			// "Raumbezugssystem"
			xpathExpression = "gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.referencesystem_id"));
			
			// "Schlüsselkatalog: im Datensatz vorhanden"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:includedWithDataset";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.keyc_incl_w_dataset"));
			
			// "Parameter"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureTypes";
			subXPathExpression = ".";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_data_para.parameter"), "textList");
			
			// "Schlüsselkatalog: Titel"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureCatalogueCitation/gmd:CI_Citation/gmd:title";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo_keyc.subject_cat"));
			
			// "Schlüsselkatalog: Datum"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureCatalogueCitation/gmd:CI_Citation/gmd:date";
			subXPathExpression = "gmd:CI_Date/gmd:date";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_keyc.key_date"), "textList");

			// "Schlüsselkatalog: Version"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureCatalogueCitation/gmd:CI_Citation/gmd:edition";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo_keyc.edition"));
			
			// "Titel"
			xpathExpression = "gmd:portrayalCatalogueInfo/gmd:PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:title";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo_symc.symbol_cat"));
			
			// "Datum"
			xpathExpression = "gmd:portrayalCatalogueInfo/gmd:PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:date";
			subXPathExpression = "gmd:CI_Date/gmd:date";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_symc.symbol_date"), "textList");
			
			// "Version"
			xpathExpression = "gmd:portrayalCatalogueInfo/gmd:PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:edition";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo_symc.edition"));
			
			// "Erscheinungsjahr"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:editionDate";
			getNodeValueForDate(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publish_year"));

			// "Weitere bibliographische Angaben"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:otherCitationDetails";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.doc_info"));
			
			// "ISBN-Nummer des Dokumentes"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:ISBN";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.isbn"));
			
			//TODO: CI_ResponsibleParty/role/CI_RoleCode/@codeListValue = "projectParticipant" -> individualName ...
			// "Version"
			
			//TODO: CI_ResponsibleParty/role/CI_RoleCode/@codeListValue = "prjectParticipant" -> issueIdentification ...
			// "Herausgeber"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:individualName";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publisher"));
			
			//TODO: CI_ResponsibleParty/role/CI_RoleCode/@codeListValue = "projectParticipant" -> organisationName ...
			// "Verlag"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:organisationName";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publishing"));
			
			// "Standort"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:contactInstructions";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.loc"));
			
			// "Erschienen in"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:publish_in";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publish_in"));
			
			// "Band, Heft"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:issueIdentification";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.volume"));
			
			// "Seiten"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:page";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.sides"));
			
			// TODO: operatesOn
			
			// "Abhängigkeit"
			xpathExpression ="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:dependsOn/srv:SV_OperationMetadata";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv_op_depends.depends_on"));
			
			// "Unterstützte Plattformen"
			xpathExpression ="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:dependsOn/srv:DCP/srv:DCPList/@codeListValue";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv_op_platform.platform"));
			
			// "Parametername"
			xpathExpression ="srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters/srv:SV_Parameter/srv:name/gmd:aName";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv_op_para.name"));
			
			// "Parameter > Richtung"
			xpathExpression ="srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
			subXPathExpression = "srv:SV_Parameter/srv:direction/gmd:SV_ParameterDirection";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.direction"), "textList");
			
			// "Parameter > Beschreibung"
			xpathExpression ="srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
			subXPathExpression = "srv:SV_Parameter/srv:description";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.descr"), "textList");
			
			// "Parameter > Ist Optional"
			xpathExpression ="srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
			subXPathExpression = "srv:SV_Parameter/srv:optionality";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.optional"), "textList");
			
			// "Parameter > Mehrfachangabe möglich"
			xpathExpression ="srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:parameters";
			subXPathExpression = "srv:SV_Parameter/srv:repeatability";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_para.repeatability"), "textList");
			
			xpathExpression = "gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions";
			getExternLinks(elementsSubject, xpathExpression);
			
			
			if(elementsReference.size() > 0){
				data.put(DATA_TAB_REFERENCE, addTabData(messages.getString("time_reference"), elementsReference));
			}
			if(elementsAvailability.size() > 0){
				data.put(DATA_TAB_AVAILABILITY, addTabData(messages.getString("availability"),elementsAvailability));
			}
			if(elementsAdditionalInfo.size() > 0){
				data.put(DATA_TAB_ADDITIIONAL_INFO, addTabData(messages.getString("additional_information"),elementsAdditionalInfo));
			}
			if(elementsSubject.size() > 0){
				data.put(DATA_TAB_SUBJECT, addTabData(messages.getString("subject_reference"),elementsSubject));
			}
			
			data.put(DATA_TAB_GENERAL, elementsGeneral);
			data.put(DATA_TAB_AREA_TIME, elementsAreaTime);
		}
	}
	
	private void getIndividualName() {
		String xpathExpression = "";
		String xpathExpressionRole = "";
		
		xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:individualName";
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo_symc.edition"));
		}
		
		xpathExpressionRole = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode/@codeListValue";
		if(XPathUtils.nodeExists(rootNode, xpathExpressionRole)){
			getNodeValue(elementsSubject, xpathExpressionRole, messages.getString("t011_obj_geo_symc.edition"), "505");	
		}
		
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
						listEntry.put("body", UtilsLanguageCodelist.getNameFromIso639_2(value, this.request.getLocale().toString()));
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
						addElementEntryLabelLeft(elements, UtilsLanguageCodelist.getNameFromIso639_2(body, this.request.getLocale().toString()), title);
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
						if(codeListId != null){
							listEntry.put("body", sysCodeList.getNameByCodeListValue(codeListId, value));
						}else{
							listEntry.put("body", value);
						}
						if (!isEmptyList(listEntry)) {
							list.add(listEntry);
						}
	        		}
	        	}
	        	elements.add(element);
			}else{
				Node node = nodeList.item(0);
				if (XPathUtils.nodeExists(node, subXPathExpression)) {
					String value = XPathUtils.getString(node, subXPathExpression).trim();
					if(codeListId != null ){
						value = sysCodeList.getNameByCodeListValue(codeListId, value);
					} 
					if(value.length() > 0){
						addElementEntryLabelLeft(elements, value, title);
					}
				}
			}
        }
	}

	private void getNodeValueForDate(ArrayList elements, String xpathExpression, String title) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			String body = XPathUtils.getString(rootNode, xpathExpression).trim();
			if(body.length() > 0){
				addElementEntryLabelLeft(elements, UtilsDate.convertDateString(body, "yyyy-MM-dd", "dd.MM.yyyy"), title);
			}
		}
		
	}

	private void getNodeValue(ArrayList elements, String xpathExpression, String title) {
		getNodeValue(elements, xpathExpression, title, null);
	}
	
	private void getNodeValue(ArrayList elements, String xpathExpression, String title, String codeListId) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			String value = XPathUtils.getString(rootNode, xpathExpression).trim();
			if(value.length() > 0){
				if(codeListId != null){
					addElementEntryLabelLeft(elements, sysCodeList.getNameByCodeListValue(codeListId, value), title);	
				}else{
					addElementEntryLabelLeft(elements, value, title);
				}
			}
		}
	}
	
	// TODO: Edit link title
	private void getExternLinks(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList linkList = new ArrayList();
	    	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", "Externe Webseiten");
        	element.put("linkList", linkList);
        	elements.add(element);
        	for (int i=0; i<nodeList.getLength();i++){
        		String url = "";
        		String description = "";
        		String function = "";
        		
        		xpathExpression = "gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
        		if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
        			url = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
        		}
        		
        		xpathExpression = "gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:function/gmd:CI_OnLineFunctionCode/@codeListValue";
        		if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
        			function = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
        		}
        		
        		xpathExpression = "gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:description";
        		if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
        			description = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
        		}
        		
        		if(url.length() > 0){
        			HashMap link = new HashMap();			
    				link.put("hasLinkIcon", new Boolean(true));
      	        	link.put("isExtern", new Boolean(true));
      	        	if(function.length() > 0){
      	        		link.put("title", url + " (" +  function + ")");
      	  	        }else{
      	  	        	link.put("title", url);
      	        	}
      	        	link.put("href", url);
      	        	linkList.add(link);
        		}
        	}
		}
	}

	// TODO: WMS URL kommt mehrfach vor !!!
	private void getConnectionPoints(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList linkList = new ArrayList();
	    	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("linkList", linkList);
        	elements.add(element);
	    	for (int i=0; i<nodeList.getLength();i++){
				Node node = XPathUtils.getNode(nodeList.item(i), "gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
				String value = XPathUtils.getString(node, ".").trim();
				if (value != null && value.toLowerCase().indexOf("request=getcapabilities") == -1) {
	    			if (value.indexOf("?") == -1) {
	    				value = value + "?";
	    			}
	    			if (!value.endsWith("?")) {
	    				value = value + "&";
	    			}
	    			value = value + "REQUEST=GetCapabilities&SERVICE=WMS";
	    		}
	    		HashMap link = new HashMap();			
				link.put("hasLinkIcon", new Boolean(true));
  	        	link.put("isExtern", new Boolean(false));
  	        	link.put("title", messages.getString("common.result.showMap"));
  	        	link.put("href", "main-maps.psml?wms_url=" + UtilsVelocity.urlencode(value));
  	        	linkList.add(link);
			}
		}
	}

	private ArrayList addTabData(String title, ArrayList elements) {
		ArrayList tmp = new ArrayList();
		addSectionTitle(tmp, title);
		for(int i=0; i<elements.size();i++){
			tmp.add(elements.get(i));
		}
		closeDiv(tmp);
		return tmp;
	}

	private void getServiceOperations(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String nodeXPathExpression;
				nodeXPathExpression = "srv:SV_OperationMetadata";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					ArrayList listName = new ArrayList();
					ArrayList listDescr = new ArrayList();
					ArrayList listInvocation = new ArrayList();
					for (int j = 0; j < childNodeList.getLength(); j++) {
						if (XPathUtils.nodeExists(childNodeList.item(j), "srv:operationName")) {
							String values = XPathUtils.getString(childNodeList.item(j), "srv:operationName").trim();
							StringTokenizer stValues = new StringTokenizer(values, ",");
							while (stValues.hasMoreTokens()) {
								listName.add(stValues.nextToken());
							}
						}
						
						if (XPathUtils.nodeExists(childNodeList.item(j), "srv:operationDescription")) {
							String values = XPathUtils.getString(childNodeList.item(j), "srv:operationDescription").trim();
							StringTokenizer stValues = new StringTokenizer(values, ",");
							while (stValues.hasMoreTokens()) {
								listDescr.add(stValues.nextToken());
							}
						}
						
						if (XPathUtils.nodeExists(childNodeList.item(j), "srv:invocationName")) {
							String values = XPathUtils.getString(childNodeList.item(j), "srv:invocationName").trim();
							StringTokenizer stValues = new StringTokenizer(values, ",");
							while (stValues.hasMoreTokens()) {
								listInvocation.add(stValues.nextToken());
							}
						}
					}
					
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
					
					ArrayList numbers = new ArrayList();
					numbers.add(listName.size());
					numbers.add(listDescr.size());
					numbers.add(listInvocation.size());
					int maxRows = getGreatestInt(numbers); 
					
					for (int i = 0; i < maxRows; i++) {
						ArrayList row = new ArrayList();
						if (listName.get(i) != null)
							row.add(notNull((String) listName.get(i)));
						else
							row.add("");
						
						if (listDescr.get(i) != null)
							row.add(notNull((String) listDescr.get(i)));
						else
							row.add("");
						
						if (listInvocation.get(i) != null)
							row.add(notNull((String) listInvocation.get(i)));
						else
							row.add("");
						
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
					if (body.size() > 0) {
						elements.add(element);
						addSpace(elements);
					}
				}
			}
		}
	}
	
	private void getAvailability(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:distributionFormat/gmd:MD_Format";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					NodeList nodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
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
						
						if (XPathUtils.nodeExists(childNode, "gmd:name")) {
							String value = XPathUtils.getString(childNode, "gmd:name").trim();
							row.add(notNull(value));
						} else {
							row.add("");
						}
						
						if (XPathUtils.nodeExists(childNode, "gmd:version")) {
							String value = XPathUtils.getString(childNode, "gmd:version").trim();
							row.add(notNull(value));
						} else {
							row.add("");
						}
						
						if (XPathUtils.nodeExists(childNode, "gmd:fileDecompressionTechnique")) {
							String value = XPathUtils.getString(childNode, "gmd:fileDecompressionTechnique").trim();
							row.add(notNull(value));
						} else {
							row.add("");
						}
						
						if (XPathUtils.nodeExists(childNode, "gmd:specification")) {
							String value = XPathUtils.getString(childNode, "gmd:specification").trim();
							row.add(notNull(value));
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
				nodeXPathExpression = "gmd:transferOptions/gmd:MD_DigitalTransferOptions";
				getMediumOptions(elements, nodeXPathExpression, node);
				
				nodeXPathExpression = "gmd:distributor/gmd:MD_Distributor";
				getOrderingInformation(elements, nodeXPathExpression, node);
			}
		}
	}
	
	private void getConformityData(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:report";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					NodeList dataQualityNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
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
					
					for (int i = 0; i < dataQualityNodeList.getLength(); i++) {
						Node childNode = XPathUtils.getNode(dataQualityNodeList.item(i), "gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation");
						ArrayList row = new ArrayList();
						
						String childXPathExpression = "gmd:title";
						if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
							String value = XPathUtils.getString(childNode, childXPathExpression).trim();
							row.add(notNull(value));
						} else {
							row.add("");
						}
						
						childXPathExpression = "gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue";
						if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
							String value = XPathUtils.getString(childNode, childXPathExpression).trim();
							row.add(notNull(value));
						} else {
							row.add("");
						}
						
						childXPathExpression = "gmd:date/gmd:CI_Date/gmd:date";
						if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
							String value = XPathUtils.getString(childNode, childXPathExpression).trim();
							row.add(notNull(value));
						} else {
							row.add("");
						}
						
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
					if (body.size() > 0) {
						addSpace(elements);
						elements.add(element);
					}
				}
			}
		}
	}
	
	// "Veröffentlichung"
	private void getClassificationInformation(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String nodeXPathExpression;
				
				nodeXPathExpression = "gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:classification/gmd:MD_ClassificationCode/@codeListValue";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String value = XPathUtils.getString(childNode, ".").trim();
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
				String childXPathExpression;
				childXPathExpression = "gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions";
				if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
					String content = XPathUtils.getString(childNode, childXPathExpression).trim();
					addElementEntryLabelLeft(elements, content, messages.getString("t01_object.ordering_instructions"));
				}
			}
			
		}
	}
	
	// "Medien"
	private void getMediumOptions(ArrayList elements, String xpathExpression, Node node) {
		if (XPathUtils.nodeExists(node, xpathExpression)) {
			NodeList mediaNodeList = XPathUtils.getNodeList(node, xpathExpression);
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", messages.getString("t0112_media_option.medium"));
			ArrayList head = new ArrayList();
			head.add(messages.getString("t0112_media_option.medium_name"));
			head.add(messages.getString("t0112_media_option.transfer_size"));
			head.add(messages.getString("t0112_media_option.medium_note"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			for (int i = 0; i < mediaNodeList.getLength(); i++) {
				Node childNode = mediaNodeList.item(i);
				ArrayList row = new ArrayList();
				
				String childXPathExpression = "gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue";
				if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
					String value = XPathUtils.getString(childNode, childXPathExpression).trim();
					row.add(notNull(sysCodeList.getNameByCodeListValue("520", value)));
				} else {
					row.add("");
				}
				
				childXPathExpression = "gmd:transferSize";
				if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
					row.add(notNull(XPathUtils.getString(childNode, childXPathExpression)).trim());
				} else {
					row.add("");
				}
				
				childXPathExpression = "gmd:offLine/gmd:MD_Medium/gmd:mediumNote";
				if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
					row.add(notNull(XPathUtils.getString(childNode, childXPathExpression)).trim());
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
	
	private void getReferenceObject(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:spatialResolution/gmd:MD_Resolution";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					ArrayList listDominator = new ArrayList();
					ArrayList listMeter = new ArrayList();
					ArrayList listDpi = new ArrayList();
					for (int j = 0; j < childNodeList.getLength(); j++) {
						if (XPathUtils.nodeExists(childNodeList.item(j), "gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator")) {
							listDominator.add(XPathUtils.getString(childNodeList.item(j), "gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator").trim());
						}
						
						if (XPathUtils.nodeExists(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='meter']")) {
							listMeter.add(XPathUtils.getString(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='meter']").trim());
						}
						
						if (XPathUtils.nodeExists(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='dpi']")) {
							listDpi.add(XPathUtils.getString(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='dpi']").trim());
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
							row.add(notNull((String) listDominator.get(i)));
						} else {
							row.add("");
						}
						
						value = listMeter.size();
						if (value != 0) {
							row.add(notNull((String) listMeter.get(i)));
						} else {
							row.add("");
						}
						
						value = listDpi.size();
						if (value != 0) {
							row.add(notNull((String) listDpi.get(i)));
						} else {
							row.add("");
						}
						
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
					if (body.size() > 0) {
						elements.add(element);
						addSpace(elements);
					}
				}
			}
		}
	}
	
	private void getIndexInformationKeywords(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			ArrayList elementsSearch = new ArrayList();
			ArrayList elementsInspire = new ArrayList();
			ArrayList elementsUmthes = new ArrayList();
			ArrayList elementsGemet = new ArrayList();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				ArrayList keywordList;
				String type = "";
				String thesaurusName = "";
				String thesaurusDate = "";
				String thesaurusType = "";
				String nodeXPathExpression;
				
				// type
				nodeXPathExpression = "gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
				boolean existType = XPathUtils.nodeExists(node, nodeXPathExpression);
				if (existType) {
					type = XPathUtils.getString(node, nodeXPathExpression);
				}
				
				// thesaurus
				nodeXPathExpression = "gmd:MD_Keywords/gmd:thesaurusName";
				boolean existThesaurus = XPathUtils.nodeExists(node, nodeXPathExpression);
				if (existThesaurus) {
					thesaurusName = XPathUtils.getString(node, nodeXPathExpression + "/gmd:CI_Citation/gmd:title").trim();
					thesaurusDate = XPathUtils.getString(node, nodeXPathExpression + "/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date").trim();
					thesaurusType = XPathUtils.getString(node, nodeXPathExpression + "/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue").trim();
				}
				
				// keywords
				nodeXPathExpression = "gmd:MD_Keywords/gmd:keyword";
				boolean existKeyword = XPathUtils.nodeExists(node, nodeXPathExpression);
				if (existKeyword) {
					keywordList = new ArrayList();
					NodeList keywordNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					for (int j = 0; j < keywordNodeList.getLength(); j++) {
						Node keywordNode = keywordNodeList.item(j);
						String value = XPathUtils.getString(keywordNode, ".").trim();
						keywordList.add(value);
						
						HashMap listEntry = new HashMap();
						listEntry.put("type", "textList");
						listEntry.put("body", value);
						
						if (thesaurusName.equals("Service Classification, version 1.0")) {
							if (!isEmptyList(listEntry)) {
								elementsSearch.add(listEntry);
							}
						} else if (thesaurusName.equals("UMTHES Thesaurus")) {
							if (!isEmptyList(listEntry)) {
								elementsUmthes.add(listEntry);
							}
						} else if (thesaurusName.equals("GEMET - Concepts, version 2.1")) {
							if (!isEmptyList(listEntry)) {
								elementsGemet.add(listEntry);
							}
						} else if (thesaurusName.equals("GEMET - INSPIRE themes, version 1.0")) {
							if (!isEmptyList(listEntry)) {
								elementsInspire.add(listEntry);
							}
						} else if (type.equals("theme")) {
							if (!isEmptyList(listEntry)) {
								elementsInspire.add(listEntry);
							}
						} else {
							if (!isEmptyList(listEntry)) {
								elementsSearch.add(listEntry);
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
			
			if (elementsUmthes.size() > 0) {
				HashMap element = new HashMap();
				element.put("type", "textList");
				element.put("title", "UMTHES Thesaurus");
				element.put("textList", elementsUmthes);
				elements.add(element);
			}
			
		}
	}
	
	// "Addressen"
	private void getAddresses(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList elementsAddress = new ArrayList();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node childNode = nodeList.item(i);
				if (childNode.hasChildNodes()) {
					String xpathExpressionContact = "gmd:CI_ResponsibleParty";
					Node subNode = XPathUtils.getNode(childNode, xpathExpressionContact);
					addSingleAddress(elementsAddress, subNode);
				}
			}
			
			xpathExpression = "gmd:contact/gmd:CI_ResponsibleParty";
			if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
				Node node = XPathUtils.getNode(rootNode, xpathExpression);
				addSingleAddress(elementsAddress, node);
			}
			
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty";
			if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
				Node node = XPathUtils.getNode(rootNode, xpathExpression);
				addCitedResponsibleAddress(elementsAddress, node);
			}
			
			addSpace(elementsGeneral);
			HashMap elementAddress = new HashMap();
			elementAddress.put("type", "multiLineAddresses");
			elementAddress.put("title", messages.getString("addresses"));
			elementAddress.put("id", "addresses_id");
			elementAddress.put("elementsAddress", elementsAddress);
			elementsGeneral.add(elementAddress);
		}
	}
	
	private void addCitedResponsibleAddress(ArrayList elementsAddress, Node node) {
		if(node != null){
			HashMap element = null;
			
			String xpathExpression = "";
			
			xpathExpression = "gmd:role/gmd:CI_RoleCode/@codeListValue";
			if(XPathUtils.nodeExists(rootNode, xpathExpression)){
				String role = XPathUtils.getString(node, xpathExpression).trim();	
				role = sysCodeList.getNameByCodeListValue("505", role);
				
				element = addElementAddress("multiLine", role, "", "false", new ArrayList());
				ArrayList elements = (ArrayList) element.get("elements");
				
				xpathExpression = XPathUtils.getString(node, "gmd:individualName");
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String value = XPathUtils.getString(node, xpathExpression).trim();
					StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
					for(int i=0; i<valueTokenizer.countTokens();i++) {
						elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), valueTokenizer.nextToken()));
					}
				}
			}
			if(element != null){
				elementsAddress.add(element);	
			}
		}
		
	}

	private void addSingleAddress(ArrayList elementsAddress, Node node) {
		if (node != null) {
			HashMap element;
			String xpathExpression;
			
			xpathExpression = "gmd:role/gmd:CI_RoleCode/@codeListValue";
			if (XPathUtils.nodeExists(node, xpathExpression)) {
				String role = XPathUtils.getString(node, xpathExpression).trim();
				element = addElementAddress("multiLine", sysCodeList.getNameByCodeListValue("505", role), "", "false", new ArrayList());
				ArrayList elements = (ArrayList) element.get("elements");
				
				// Organistation
				xpathExpression = "gmd:organisationName";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
					for(int i=0; i<valueTokenizer.countTokens();i++) {
						elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), valueTokenizer.nextToken()));
					}
				}
				
				// Name
				xpathExpression = "gmd:individualName";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
					int i = 0;
					String firstName = "";
					String lastName = "";
					String nameTitle = "";
					
					while (valueTokenizer.hasMoreTokens()) {
						switch (i) {
							case 0:
								lastName = valueTokenizer.nextToken();
								i++;
								break;
							case 1:
								firstName = valueTokenizer.nextToken();
								i++;
								break;
							case 2:
								StringTokenizer tokenizer = new StringTokenizer(valueTokenizer.nextToken(), " ");
								String firstTitle = "";
								String secTitle = "";
								int j = 0;
								while (tokenizer.hasMoreTokens()) {
									switch (j) {
										case 0:
											firstTitle = tokenizer.nextToken();
											j++;
											break;
										case 1:
											secTitle = tokenizer.nextToken();
											j++;
											break;
										default:
											break;
									}
								}
								nameTitle = secTitle.concat(" ").concat(firstTitle);
								i++;
								break;
							default:
								break;
						}
					}
					elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), nameTitle.concat(" ").concat(firstName).concat(" ").concat(lastName)));
				}
				
				// Delivery point
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String deliveryPoint = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", deliveryPoint);
				}
				
				String postalCode = "";
				String city = "";
				
				// Postcode
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					postalCode = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// City
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					city = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				if (postalCode.length() > 0 || city.length() > 0) {
					addElement(elements, "textLine", postalCode.concat(" ").concat(city));
				}
				
				// Country
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String country = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(country), this.request.getLocale().toString()));
				}
				addSpace(elements);
				
				// Mail
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int i = 0; i < electronicMailAddressNodeList.getLength(); i++) {
						String email = XPathUtils.getString(electronicMailAddressNodeList.item(i), ".").trim();
						elements.add(addElementEmail("textLinkLine", "Email:", email, email, email, LinkType.EMAIL));
					}
				}
				
				// Phone
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Telefon:");
				}
				
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Fax:");
				}
				elementsAddress.add(element);
			}
		}
	}
	
	private void getTimeSection(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				addSectionTitle(elements, messages.getString("time_reference"));
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String beginPosition = "";
					String endPosition = "";
					if(XPathUtils.nodeExists(childNode, "gml:beginPosition")){
						beginPosition = XPathUtils.getString(childNode, "gml:beginPosition").trim();
					}
					if(XPathUtils.nodeExists(childNode, "gml:endPosition")){
						endPosition = XPathUtils.getString(childNode, "gml:endPosition").trim();
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
							// "Erläuterung zum Raumbezug"
							addElementEntryLabelLeft(elements, entryLine, messages.getString("time_reference_content"));
						}
					}
				}
				
				// "Status"
				nodeXPathExpression = "../../gmd:status/gmd:MD_ProgressCode";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
					addElementEntryLabelLeft(elements, sysCodeList.getNameByCodeListValue("523", domainValue), messages.getString("t01_object.time_status"));
				}
				
				// "Periodizität"
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
					addElementEntryLabelLeft(elements, sysCodeList.getNameByCodeListValue("518", domainValue), messages.getString("t01_object.time_period"));
				}
				
				// "Intervall der Erhebung"
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:userDefinedMaintenanceFrequency";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String content = XPathUtils.getString(childNode, ".").trim();
					String value = new TM_PeriodDurationToTimeAlle().parse(content);
					String unit = new TM_PeriodDurationToTimeInterval().parse(content);
					addElementEntryLabelLeft(elements, value.concat(" ").concat(unit), messages.getString("t01_object.time_interval"));
				}
				
				nodeXPathExpression = "../../gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					for (int i = 0; i < childNodeList.getLength(); i++) {
						String time = XPathUtils.getString(childNodeList.item(i), "gmd:date").trim();
						String type = XPathUtils.getString(childNodeList.item(i), "gmd:dateType/gmd:CI_DateTypeCode/@codeListValue").trim();
						String typeId = "";
						if (type.equals("creation")) {
							typeId = "1";
						}else if (type.equals("publication")) {
							typeId = "2";
						}else if (type.equals("revision")) {
							typeId = "3";
						}
						
						if (time.indexOf("T") == -1) {
							addElementEntryLabelLeft(elements, UtilsDate.convertDateString(time, "yyyy-MM-dd", "dd.MM.yyyy"), sysCodeList.getName("502", typeId));
						} else {
							addElementEntryLabelLeft(elements, UtilsDate.convertDateString(time.replaceAll("T", ""), "yyyy-MM-ddHH:mm:ss", "dd.MM.yyyy"), sysCodeList.getName("502", typeId));
						}
					}
				}
				
				// "Erläuterung zum Zeitbezug"
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceNote";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.time_descr"));
				}
				closeDiv(elements);
			}
		}
	}
	
	private void getAreaSection(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				addSectionTitle(elements, messages.getString("t011_obj_geo.coord"));
				String nodeXPathExpression;
				NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
				ArrayList subjectEntries = new ArrayList();
				for (int i = 0; i < nodeList.getLength(); i++) {
					// "Administrative Einheit (Gemeindenummer)"
					nodeXPathExpression = "gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code";
					node = nodeList.item(i);
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
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
						elements.add(element);
						
					}
					
					// "Geothesaurus-Raumbezug"
					nodeXPathExpression = "gmd:geographicElement/gmd:EX_GeographicBoundingBox";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						NodeList subNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
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
							
							if (subjectEntries.get(j)!= null) {
								row.add(subjectEntries.get(j));
							} else {
								row.add("");
							}
							
							String childXPathExpression = "gmd:westBoundLongitude";
							if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
								String value = XPathUtils.getString(childNode, childXPathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
							
							childXPathExpression = "gmd:southBoundLatitude";
							if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
								String value = XPathUtils.getString(childNode, childXPathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
							
							childXPathExpression = "gmd:eastBoundLongitude";
							if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
								String value = XPathUtils.getString(childNode, childXPathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
							
							childXPathExpression = "gmd:northBoundLatitude";
							if (XPathUtils.nodeExists(childNode, childXPathExpression)) {
								String value = XPathUtils.getString(childNode, childXPathExpression).trim();
								row.add(notNull(value));
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
					
					// TODO: "Region oder Naturraum" missing
					// "Region oder Naturraum"
					nodeXPathExpression = "gmd:description";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
						HashMap element = new HashMap();
						element.put("type", "textList");
						element.put("title", messages.getString(messages.getString("t019_coordinates.bezug")));
						ArrayList textListEntries = new ArrayList();
						element.put("textList", textListEntries);
						for (int ij = 0; ij < childNodeList.getLength(); ij++) {
							String domainValue = XPathUtils.getString(childNodeList.item(ij), ".").trim();
							HashMap listEntry = new HashMap();
							listEntry.put("type", "textList");
							listEntry.put("body", domainValue);
							if (!isEmptyList(listEntry)) {
								textListEntries.add(listEntry);
							}
						}
						elements.add(element);
					}
					
					// "Höhe"
					nodeXPathExpression = "gmd:verticalElement/gmd:EX_VerticalExtent";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
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
						for (int j = 0; j < nodeList.getLength(); j++) {
							Node childNode = nodeList.item(j);
							ArrayList row = new ArrayList();
							row.add(notNull(XPathUtils.getString(childNode, "gmd:maximumValue")).trim());
							row.add(notNull(XPathUtils.getString(childNode, "gmd:minimumValue")).trim());
							String rowValue;
							rowValue = XPathUtils.getString(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom").trim();
							row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
							rowValue = XPathUtils.getString(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name").trim();
							row.add(notNull(sysCodeList.getNameByCodeListValue("101", rowValue)));
							if (!isEmptyRow(row)) {
								body.add(row);
							}
						}
						
						if (body.size() > 0) {
							elements.add(element);
						}
						
					}
					
					// "Erläuterung zum Raumbezug"
					nodeXPathExpression = "gmd:description";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
						addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.loc_descr"));
					}
				}
				closeDiv(elements);
			}
		}
	}
	
	private void getGeneralTab(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:abstract";
				
				// Beschreibung
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					String alternateName = "";
					String description = "";
					
					// alternate name
					nodeXPathExpression = "gmd:citation/gmd:CI_Citation/gmd:alternateTitle";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						alternateName = XPathUtils.getString(node, nodeXPathExpression).trim();
					}
					
					// description
					nodeXPathExpression = "gmd:abstract";
					if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
						description = XPathUtils.getString(node, nodeXPathExpression).trim();
						
						if (description != null) {
							description = description.replaceAll("\n", "<br/>");
							description = description.replaceAll("<(?!b>|/b>|i>|/i>|u>|/u>|p>|/p>|br>|br/>|br />|strong>|/strong>|ul>|/ul>|ol>|/ol>|li>|/li>)[^>]*>", "");
						}
					}
					if ((description.length() > 0) || alternateName.length() > 0) {
						addSectionTitle(elements, messages.getString("detail_description"));
						addElementEntryLabelAbove(elements, description, alternateName, false);
						// TODO: superior objects
						// addSuperiorObjects(elementsGeneral,
						// listSuperiorObjects);
						// TODO: subordinated objects
						// addSubordinatedObjects(elementsGeneral,
						// listSubordinatedObjects);
						// close description
						closeDiv(elements);
					}
				}
				
				nodeXPathExpression = "gmd:descriptiveKeywords";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					addReference(node);
				}
			}
		}
	}
	
	// "Querverweise"
	private HashMap addReference(Node node) {
		ArrayList keywords = new ArrayList();
		if (XPathUtils.nodeExists(node, "//gmd:keyword")) {
			NodeList nodeListKeywords = XPathUtils.getNodeList(node, "//gmd:keyword");
			for (int i = 0; i < nodeListKeywords.getLength(); i++) {
				Node keywordNode = nodeListKeywords.item(i);
				HashMap listEntry = new HashMap();
				listEntry.put("type", "textList");
				listEntry.put("body", XPathUtils.getString(keywordNode, ".").trim());
				keywords.add(listEntry);
			}
		}
		String type = XPathUtils.getString(node, "//gmd:MD_KeywordTypeCode/@codeListValue").trim();
		HashMap element = new HashMap();
		element.put("type", "textList");
		element.put("title", messages.getString("cross_references"));
		element.put("textList", keywords);
		
		return element;
	}
	
	private HashMap addElementEmail(String type, String title, String href, String body, String altText, LinkType linkType) {
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
				if (title.startsWith("http")) {
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
	
	private HashMap addElementLink(String type, Boolean hasLinkIcon, Boolean isExtern, String title) {
		HashMap element = new HashMap();
		if (type != null)
			element.put("type", type);
		if (title != null)
			element.put("title", title);
		if (hasLinkIcon)
			element.put("sort", hasLinkIcon);
		if (isExtern != null)
			element.put("isExtern", isExtern);
		
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
		int maximum = Integer.parseInt(numbers.get(0).toString());
		int i = 1;
		while (i < numbers.size()) {
			if (Integer.parseInt(numbers.get(0).toString()) > maximum) {
				maximum =  Integer.parseInt(numbers.get(i).toString());
			}
			i++;
		}
		return maximum;
	}
}
