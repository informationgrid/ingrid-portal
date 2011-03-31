package de.ingrid.portal.search.detail.idf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0_Md_Metadata extends DetailDataPreparerIdf1_0_0 {
	
	private final static Log	log	= LogFactory.getLog(DetailDataPreparerIdf1_0_0_Md_Metadata.class);
	private final static String	UDK_OBJ_CLASS_TYPE = "UDK_OBJ_CLASS_TYPE";
	
	public enum LinkType {
		EMAIL, WWW_URL
	}
	
	public enum ReferenceType {
		SUBORDINATE, SUPERIOR, CROSS
	}
	
	public enum LabelType {
		LEFT, ABOVE, DURING
	}
	
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
				log.debug("Parsing of MD Metadata Node!");
			}
			initialArrayLists();
			
			HashMap general = new HashMap();
			ArrayList udkElements = new ArrayList();
			
			String xpathExpression;
			String subXPathExpression;
			
			// check for hierachyLevelName
			getUdkObjClass(general, udkElements);
			
			String metadataDataNodePath = "";
			if (context.get(UDK_OBJ_CLASS_TYPE) != null) {
				if(log.isDebugEnabled()){
					log.debug("UDK_OBJ_CLASS_TYPE: '" + context.get(UDK_OBJ_CLASS_TYPE) +"'");
				}
				if (context.get(UDK_OBJ_CLASS_TYPE).equals("6") || context.get(UDK_OBJ_CLASS_TYPE).equals("3")) {
					metadataDataNodePath = "srv:SV_ServiceIdentification";
				} else {
					metadataDataNodePath = "gmd:MD_DataIdentification";
				}
			}else{
				metadataDataNodePath = "gmd:MD_DataIdentification";
			}
			
			// Title
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:citation/gmd:CI_Citation/gmd:title";
			getTitle(xpathExpression, general);
	
			
	// Tab "General"
			// Description
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath;
			getGeneralTab(elementsGeneral, xpathExpression);
			
			// Addresses
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:pointOfContact";
			getAddresses(elementsGeneral, xpathExpression);
			
	// Tab "Verweise"
			ArrayList referenceList = new ArrayList();
			// "Verweise"
			xpathExpression ="./idf:crossReference";
			getReference(referenceList, xpathExpression, ReferenceType.CROSS);
			
			// "Externe Webseiten"
			xpathExpression = "gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions";
			getExternLinks(referenceList, xpathExpression);
			
			if(referenceList.size() > 0){
				elementsReference = addTabData(messages.getString("references"), referenceList);
			}
			// "Verschlagwortung"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:descriptiveKeywords";
			getIndexInformationKeywords(elementsReference, xpathExpression, metadataDataNodePath);
			
	// Tab "Verfügbarkeit"
			// "Datenformat"
			xpathExpression = "gmd:distributionInfo/gmd:MD_Distribution";
			getAvailability(elementsAvailability, xpathExpression);
			
			// "Zugangsbeschränkungen"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints";
			subXPathExpression = ".";
			getNodeListValues(elementsAvailability, xpathExpression, subXPathExpression, messages.getString("object_access.restriction_value"), "textList", "6010");
			
			// "Nutzungsbedingungen"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation";
			subXPathExpression = ".";
			getNodeListValues(elementsAvailability, xpathExpression, subXPathExpression, messages.getString("object_access.terms_of_use"), "textList");
			
			
			
	// Tab "Raum/Zeit"
			// "Raumbezugssystem"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/*/gmd:EX_Extent";
			getAreaSection(elementsAreaTime, xpathExpression);
			
			// "Zeitbezug"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/*/gmd:EX_Extent";
			getTimeSection(elementsAreaTime, xpathExpression);
			
	// Tab "Zusätzliche Info"
			// "Sprache des Metadatensatzes"
			xpathExpression = "gmd:language";
			subXPathExpression = "gmd:LanguageCode/@codeListValue";
			getNodeListValuesLanguage(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t01_object.metadata_language"), "textList");
			
			// "Sprache des Datensatzes"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:language";
			subXPathExpression = "gmd:LanguageCode/@codeListValue";
			getNodeListValuesLanguage(elementsAdditionalInfo, xpathExpression, subXPathExpression, messages.getString("t01_object.data_language"), "textList");
			
			// "Veröffentlichung"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath;
			getClassificationInformation(elementsAdditionalInfo, xpathExpression);
			
			// "Eignung/Nutzung"
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:resourceSpecificUsage/gmd:MD_Usage/gmd:specificUsage";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.dataset_usage"));
			
			// "Herstellungszweck"
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:purpose";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.info_note.t015_legist.name"), null, LabelType.ABOVE);
			
			// "Konformität"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality";
			getConformityData(elementsAdditionalInfo, xpathExpression);
			
			// "Objekt-ID"
			/*
			xpathExpression = "gmd:fileIdentifier";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.obj_id"));
			*/
			
			// "Elternobjekt"
			/*
			xpathExpression = "gmd:parentIdentifier";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t012_obj_obj.object_from_id"));
			*/
			
			// "Zeichensatz des Metadatensatzes"
			xpathExpression = "gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_character_set"), "510");
			
			// "Zeichensatz des Datensatzes"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.dataset_character_set"), "510");
			
			// "ID der Objektklasse" 
			/*
			xpathExpression = "gmd:hierarchyLevelName";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.obj_class"));
			*/
			
			// "Geändert am"
			/*
			xpathExpression = "gmd:dateStamp";
			getNodeValueForDate(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.mod_time"));
			 */
			
			// TODO: "XML-Export-Kriterium" messages.getString("t014_info_impart.name")
			
			// "Metadatenstandardname"
			/*
			xpathExpression = "gmd:metadataStandardName";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_standard_name"));
			*/
			
			// "Metadatenstandardversion"
			/*
			xpathExpression = "gmd:metadataStandardVersion";
			getNodeValue(elementsAdditionalInfo, xpathExpression, messages.getString("t01_object.metadata_standard_version"));
			*/
			
	// Tab "Fachbezug"
			// "Fachliche Grundlage"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.special_base"));
			
			// "Datensatz/Datenserie"
			xpathExpression = "gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.hierarchy_level"), "525");
			
			// "Symbolkatalog"
			xpathExpression = "gmd:portrayalCatalogueInfo";
			getSymCatalog(elementsSubject, xpathExpression);
			
			// "Schlüsselkatalog: im Datensatz vorhanden"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:includedWithDataset";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.keyc_incl_w_dataset"));
			
			// "Schlüsselkatalog"
			xpathExpression = "gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureCatalogueCitation/gmd:CI_Citation";
			getKeyCatalogTable(elementsSubject, xpathExpression);
			
			// "Art des Dienstes"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/srv:serviceType";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.type"), "5100");

			// "Systemumgebung"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:environmentDescription";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.environment"));
			
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:abstract";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.environment"));
			
			// "Version"
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceTypeVersion";
			subXPathExpression = ".";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_version.version"), "textList");
			
			// "Operation"
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations";
			getServiceOperations(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint";
			getConnectionPoints(elementsSubject, xpathExpression);
			
			// "Digitale Repräsentation"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType";
			subXPathExpression = "gmd:MD_SpatialRepresentationTypeCode/@codeListValue";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_spatial_rep.type"), "textList", "526");
			
			// "Topologieinformation"
			xpathExpression ="gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:topologyLevel/gmd:MD_TopologyLevelCode/@codeListValue";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.vector_topology_level"), "528");
			
			// "Vektorformat"
			xpathExpression = "gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:geometricObjects";
			getGeometries(elementsSubject, xpathExpression);
			
			// "Erstellungsmaßstab"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification";
			getReferenceObject(elementsSubject, xpathExpression);
			
			// "Erfassungsgrad / Lagegenauigkeit / Höhengenauigkeit"
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report";	
			getGeoReport(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
			if(context.get(UDK_OBJ_CLASS_TYPE).equals("1")){
				// "Datengrundlage"			
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.data_base"));
			}else if(context.get(UDK_OBJ_CLASS_TYPE).equals("5")) {
				// "Methode/Datengrundlage"
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_data.base"));
			}else if(context.get(UDK_OBJ_CLASS_TYPE).equals("2")) {
				// "Basisdaten"
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.base"));
			}else{
				// "Basisdaten"
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.base"));
			}
			
			xpathExpression ="gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureTypes";
			subXPathExpression =".";
			if(context.get(UDK_OBJ_CLASS_TYPE).equals("5")){
				// "Inhalte der Datensammlung/Datenbank "
				getParameterTable(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_data_para"));
			}else{
				// "Sachdaten/Attributinformation"
				getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_supplinfo.feature_type"), "textList");
			}
			
			// TODO: to check
			// "Erläuterung zum Fachbezug"
    		xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:supplementalInformation";
			if(context.get(UDK_OBJ_CLASS_TYPE).equals("5")){
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_data.description"));
			}else if(context.get(UDK_OBJ_CLASS_TYPE).equals("4")) {
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_project.description"));
			}else if(context.get(UDK_OBJ_CLASS_TYPE).equals("3")) {
				// "Erläuterung zum Fachbezug"
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.description"));
				// "Erläuterung zum Fachbezug (srv:SV_ServiceIdentification)"
				xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:supplementalInformation/gmd:abstract";
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.description"));
			}else if(context.get(UDK_OBJ_CLASS_TYPE).equals("2")) {
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literature.description"));
			}else{
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_data.description"));
			}
			
			// "Autor"
    		xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:individualName";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.autor"));
			
			// "Herausgeber"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:individualName";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publisher"));
			
			// "Erschienen in"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:name";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publish_in"));
			
			// "Band, Heft"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:issueIdentification";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.volume"));
			
			// "Seiten"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:series/gmd:CI_Series/gmd:page";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.sides"));
			
			// "Erscheinungsjahr"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:editionDate";
			getNodeValueForDate(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publish_year"));

			// "ISBN-Nummer des Dokumentes"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:ISBN";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.isbn"));
			
			// "Verlag"
    		xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/organisationName";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publishing"));
			
			// "Verlag Ort"
    		xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:itedResponsibleParty/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.publish_loc"));
			
			// "Dokumenttyp"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:language";
			subXPathExpression ="./gmd:LanguageCode[@codeListValue]";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_literatur.typ"), "textList", "3385");
			
			// "Weitere bibliographische Angaben"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:otherCitationDetails";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.doc_info"));
			
			// "Standort"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:contactInstructions";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_literatur.loc"));
			
			// "Digitale Repräsentation"
			xpathExpression ="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType";
			subXPathExpression ="./gmd:MD_SpatialRepresentationTypeCode[@codeListValue]";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_geo_spatial_rep.type"), "textList", "526");
			
			// TODO: operatesOn
			
			// "Abhängigkeit"
			xpathExpression ="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:dependsOn";
			subXPathExpression ="srv:SV_OperationMetadata";
			getNodeListValues(elementsSubject, xpathExpression, subXPathExpression, messages.getString("t011_obj_serv_op_depends.depends_on"), "textList");
			
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
			
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description";
			if(context.get(UDK_OBJ_CLASS_TYPE).equals("1")){
				// "Herstellungsprozess"
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.method"));
			}else{
				// "Historie"
				getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_serv.history"));
			}
			
			// "Identifikator der Datenquelle"
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:RS_Identifier/gmd:code";
			getNodeValue(elementsSubject, xpathExpression, messages.getString("t011_obj_geo.datasource_uuid"));	
			
			
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
			content.put(DATA_TAB_GENERAL, elementsGeneral);
			content.put(DATA_TAB_AREA_TIME, elementsAreaTime);
			
			// "Zusätzliche Felder"
			xpathExpression ="idf:additionalDataSection";
			getAdditionalFields(elementsAdditionalField, xpathExpression);
			
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
				if(XPathUtils.nodeExists(node, "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:title")){
					row.add(XPathUtils.getString(node, "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:title").trim());
				}else{
					row.add("");
				}
				
				// "Datum"
				if(XPathUtils.nodeExists(node, "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date")){
					row.add(XPathUtils.getString(node, "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date").trim());
				}else{
					row.add("");
				}
				
				// "Version"
				if(XPathUtils.nodeExists(node, "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:edition")){
					row.add(XPathUtils.getString(node, "./gmd:MD_PortrayalCatalogueReference/gmd:portrayalCatalogueCitation/gmd:CI_Citation/gmd:edition").trim());
				}else{
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
				if(XPathUtils.nodeExists(node, "./gmd:MD_GeometricObjects/gmd:geometricObjectType/gmd:MD_GeometricObjectTypeCode/@codeListValue")){
					row.add(sysCodeList.getNameByCodeListValue("515", XPathUtils.getString(node, "./gmd:MD_GeometricObjects/gmd:geometricObjectType/gmd:MD_GeometricObjectTypeCode/@codeListValue").trim()));
				}else{
					row.add("");
				}
				
				// "Elementanzahl"
				if(XPathUtils.nodeExists(node, "./gmd:MD_GeometricObjects/gmd:geometricObjectCount")){
					row.add(XPathUtils.getString(node, "./gmd:MD_GeometricObjects/gmd:geometricObjectCount").trim());
				}else{
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

	private void getGeoReport(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				Node subNode = null;
				String symbol = "";
				String value = "";
				String description;
				String title = "";
				
				if(XPathUtils.nodeExists(node, "./gmd:DQ_CompletenessCommission")){
					subNode = XPathUtils.getNode(node, "./gmd:DQ_CompletenessCommission");
				}else if(XPathUtils.nodeExists(node, "./gmd:DQ_RelativeInternalPositionalAccuracy")){
					subNode = XPathUtils.getNode(node, "./gmd:DQ_RelativeInternalPositionalAccuracy");
				}
				if(subNode != null){
					if(XPathUtils.nodeExists(subNode, "./gmd:measureDescription")){}{
						description = XPathUtils.getString(subNode, "./gmd:measureDescription").trim();
					}
					if(XPathUtils.nodeExists(subNode, "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value")){
						value = XPathUtils.getString(subNode, "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value").trim();
					}
					if(XPathUtils.nodeExists(subNode, "./gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:UnitDefinition/gml:catalogSymbol")){
						symbol = XPathUtils.getString(subNode, "./gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:UnitDefinition/gml:catalogSymbol").trim();
					}
					
					if(description != null){
						if (description.equals("completeness")){
							title = messages.getString("t011_obj_geo.rec_grade");
						}else if (description.equals("vertical")){
							title = messages.getString("t011_obj_geo.pos_accuracy_vertical");
						}else if (description.equals("geographic")){
							title = messages.getString("t011_obj_geo.rec_exact");
						}
						if(title.length() > 0){
							addElementEntryLabelLeft(elements, value + " " + symbol, title);	
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
				if(XPathUtils.nodeExists(node, "gmd:title")){
					row.add(XPathUtils.getString(node, "gmd:title"));
				}else{
					row.add("");
				}
				
				// "Schlüsselkatalog: Datum"
				if(XPathUtils.nodeExists(node, "gmd:date/gmd:CI_Date/gmd:date")){
					row.add(XPathUtils.getString(node, "gmd:date/gmd:CI_Date/gmd:date"));
				}else{
					row.add("");
				}
				
				// "Schlüsselkatalog: Version"
				if(XPathUtils.nodeExists(node, "gmd:edition")){
					row.add(XPathUtils.getString(node, "gmd:edition"));
				}else{
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

	private void getParameterTable(ArrayList elements, String xpathExpression, String subXPathExpression, String title) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			if(nodeList.getLength() > 1){
				ArrayList list = new ArrayList();
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
					addSpace(elements);
					elements.add(element);
			    }
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
				
				if(XPathUtils.nodeExists(node, "@uuid")){
					uuid = XPathUtils.getString(node, "@uuid").trim();
				}
				
				if(XPathUtils.nodeExists(node, "./idf:objectName")){
					title = XPathUtils.getString(node, "./idf:objectName").trim();
				}
				
				if(XPathUtils.nodeExists(node, "./idf:objectType")){
					type = XPathUtils.getString(node, "./idf:objectType").trim();
				}
				
				HashMap link = new HashMap();
	        	link.put("hasLinkIcon", new Boolean(true));
	        	link.put("isExtern", new Boolean(false));
	        	link.put("title", title);
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
	        	linkList.add(link);
			}
			if (linkList != null && linkList.size() > 0){
				switch (referenceType) {
					case SUPERIOR:
						addSuperiorObjects(elements, linkList);
						break;
					case SUBORDINATE:
						addSubordinatedObjects(elements, linkList);
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

	private void getAdditionalFields(ArrayList elements, String xpathExpression) {

		String lang = request.getLocale().toString();
		
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				HashMap additionalField = new HashMap();
				additionalField.put("type", "additionalField");
				ArrayList list = new ArrayList();
				additionalField.put("body", list);
				if(node.getLocalName()!= null){
					if(node.hasChildNodes() && node.getLocalName().equals("additionalDataSection") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
						additionalField.put("title", getNodeIdfTitle(node, lang));
						NodeList childNodeList = node.getChildNodes();
						for (int j=0; j < childNodeList.getLength(); j++){
							Node childNode = childNodeList.item(j);
							if(childNode.getLocalName()!= null){
								if(childNode.getLocalName().equals("additionalDataField") && childNode.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
									String title = getNodeIdfTitle(childNode, lang);
									String body = XPathUtils.getString(childNode, "./idf:data");
									boolean isLegacy = false;
									
									if(XPathUtils.nodeExists(childNode, "./@isLegacy")){
										isLegacy = Boolean.getBoolean(XPathUtils.getString(childNode, "./@isLegacy"));
									}
									
									if(body.length() > 0){
										if(isLegacy){
											addElementEntryLabelLeft(elementsAdditionalInfo, body, title);
										}else{
											addElementEntryLabelLeft(list, body, title);
										}
									}
								}else if(childNode.getLocalName().equals("additionalDataTable") && childNode.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
									String title = getNodeIdfTitle(childNode, lang);
									boolean isLegacy = false;
									
									if(XPathUtils.nodeExists(childNode, "./@isLegacy")){
										isLegacy = Boolean.getBoolean(XPathUtils.getString(childNode, "./@isLegacy"));
									}
									
									HashMap element = new HashMap();
									element.put("type", "table");
									element.put("title", title);
									
									ArrayList head = new ArrayList();
									element.put("head", head);
									ArrayList body = new ArrayList();
									element.put("body", body);
									
									int rows=0;
									NodeList tableColumnNodeList = XPathUtils.getNodeList(childNode, "./idf:tableColumn"); 
									for (int iHead=0; iHead < tableColumnNodeList.getLength(); iHead++){
										Node headNode = tableColumnNodeList.item(iHead);
										String headName = getNodeIdfTitle(headNode, lang); 
										head.add(headName);
										NodeList rowNodeList = XPathUtils.getNodeList(headNode, "./idf:data");
										if(rows < rowNodeList.getLength()){
											rows = rowNodeList.getLength();
										}
									}
									int header=head.size();
									log.debug("header"+ header);
									log.debug("rows"+ rows);
									
									for (int iRow=1; iRow<=rows;iRow++){
										NodeList dataList = XPathUtils.getNodeList(childNode, "./*/idf:data["+iRow+"]");
										log.debug("dataList"+ dataList.getLength());
										ArrayList row = new ArrayList();
										for(int iRowCell=0; iRowCell<dataList.getLength();iRowCell++){
											Node rowCellNode = dataList.item(iRowCell);
											String value = "";
											if(rowCellNode.getFirstChild() != null){
												if(rowCellNode.getFirstChild().getNodeValue() != null){
													value = rowCellNode.getFirstChild().getNodeValue().trim();
												}
											}
											if(value != null){
												row.add(value);
											}
										}
										if (!isEmptyRow(row)) {
											body.add(row);
										}
									}
									if (body.size() > 0) {
										addSpace(list);
										if(isLegacy){
											elementsAdditionalInfo.add(element);
										}else{
											list.add(element);
										}
									}	
								}
							}
						}
					}	
				}
				
				if(additionalField.size()>0){
					elements.add(additionalField);	
				}
			}
		}
	}	

	private String getNodeIdfTitle(Node node, String lang) {
		String title; 
		if(XPathUtils.nodeExists(node, "./idf:title[@lang='"+ lang +"']")){
			title = XPathUtils.getString(node, "./idf:title[@lang='"+ lang +"']");
		}else if(XPathUtils.nodeExists(node, "./idf:title[@lang]")){
			title = XPathUtils.getString(node, "./idf:title[@lang]");
		}else if(XPathUtils.nodeExists(node, "./idf:title")){
			title = XPathUtils.getString(node, "./idf:title");
		}else{
			title = "";
		}
		return title;
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
		getNodeValue(elements, xpathExpression, title, codeListId, LabelType.LEFT);
	}
	
	private void getNodeValue(ArrayList elements, String xpathExpression, String title, String codeListId, LabelType labelType) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			String value = XPathUtils.getString(rootNode, xpathExpression).trim();
			if(value.length() > 0){
				if(codeListId != null){
					value = sysCodeList.getNameByCodeListValue(codeListId, value);
				}else{
					if(value.equals("false")){
						value = messages.getString("general.no"); 
					}else if(value.equals("true")){
						value = messages.getString("general.yes");
					}
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
        		
        		xpathExpression = "gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:name";
        		if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
        			name = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
        		}
        		
        		if(url.length() > 0){
        			HashMap link = new HashMap();			
    				link.put("hasLinkIcon", new Boolean(true));
      	        	link.put("isExtern", new Boolean(true));
      	        	if(name.length() > 0){
      	        		link.put("title", name);
      	  	        }else{
      	  	        	link.put("title", url);
      	        	}
      	        	link.put("href", url);
      	        	linkList.add(link);
        		}
        	}
        	if(linkList.size() > 0){
        		elements.add(element);
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
						if (listName.size() > i)
							row.add(notNull((String) listName.get(i)));
						else
							row.add("");
						
						if (listDescr.size() > i)
							row.add(notNull((String) listDescr.get(i)));
						else
							row.add("");
						
						if (listInvocation.size() > i)
							row.add(notNull((String) listInvocation.get(i)));
						else
							row.add("");
						
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
				addSpace(elements);
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
						addSpace(elements);
						elements.add(element);
					}
				}
			}
		}
	}
	
	private void getIndexInformationKeywords(ArrayList elements, String xpathExpression, String metadataDataNodePath) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			addSectionTitle(elements, messages.getString("thesaurus"));
			ArrayList elementsSearch = new ArrayList();
			ArrayList elementsInspire = new ArrayList();
			ArrayList elementsGemet = new ArrayList();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				ArrayList keywordList;
				String type = "";
				String thesaurusName = "";
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
						
						if(log.isDebugEnabled()){
							log.debug("Thesaurus name: '"+thesaurusName+"' and type: '"+type+"' for value: '"+value+"'");
						}
						
						if (thesaurusName.equals("Service Classification, version 1.0")) {
							if (!isEmptyList(listEntry)) {
								elementsSearch.add(listEntry);
							}
						} else if (thesaurusName.equals("UMTHES Thesaurus")) {
							if (!isEmptyList(listEntry)) {
								elementsSearch.add(listEntry);
							}
						} else if (thesaurusName.equals("GEMET - Concepts, version 2.1")) {
							if (!isEmptyList(listEntry)) {
								elementsGemet.add(listEntry);
							}
						} else if (thesaurusName.equals("GEMET - INSPIRE themes, version 1.0")) {
							if (!isEmptyList(listEntry)) {
								elementsSearch.add(listEntry);
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
			
			// "ISO-Themenkategorien"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath + "/gmd:topicCategory";
			String subXPathExpression = "gmd:MD_TopicCategoryCode";
			getNodeListValues(elementsReference, xpathExpression, subXPathExpression , messages.getString("t011_obj_geo_topic_cat.topic_category"), "textList", "527");
			closeDiv(elements);
		}
	}
	
	// "Addressen"
	private void getAddresses(ArrayList elements, String xpathExpression) {
		ArrayList elementsAddress = new ArrayList();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node childNode = nodeList.item(i);
				if (childNode.hasChildNodes()) {
					String xpathExpressionContact = "idf:idfResponsibleParty";
					if(XPathUtils.nodeExists(childNode, xpathExpressionContact)){
						Node subNode = XPathUtils.getNode(childNode, xpathExpressionContact);
						addSingleAddress(elementsAddress, subNode);	
					}
				}
			}
		}
		
		xpathExpression = "gmd:contact/idf:idfResponsibleParty";
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i = 0; i < nodeList.getLength(); i++){
				addSingleAddress(elementsAddress, nodeList.item(i));
			}
			
		}
		
		xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty";
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			addCitedResponsibleAddress(elementsAddress, node);
		}
		
		if(elementsAddress.size() > 0){
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
					String[] valueSplitter = value.split(",");
					for(int i=0; i < valueSplitter.length;i++) {
						if(XPathUtils.nodeExists(node, "./gmd:individualName[@uuid]")){
							elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), valueSplitter[i].trim(), XPathUtils.getString(node, "./gmd:individualName[@uuid]")));
						}else{
							elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), valueSplitter[i].trim()));
						}
					}
				}
				
				xpathExpression = XPathUtils.getString(node, "gmd:organisationName");
				if(XPathUtils.nodeExists(node, xpathExpression)){
					String value = XPathUtils.getString(node, xpathExpression).trim();
					StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
					String[] valueSplitter = value.split(",");
					for(int i=0; i < valueSplitter.length;i++) {
						if(XPathUtils.nodeExists(node, "./gmd:organisationName[@uuid]")){
							elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), valueSplitter[i].trim(), XPathUtils.getString(node, "./gmd:organisationName[@uuid]")));
						}else{
							elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), valueSplitter[i].trim()));
						}
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
				String title = sysCodeList.getNameByCodeListValue("505", role);
				if(title == null || title.length() < 1){
					title = role;
				}
				
				element = addElementAddress("multiLine", title, "", "false", new ArrayList());
				ArrayList elements = (ArrayList) element.get("elements");
				if(XPathUtils.nodeExists(node, "./idf:hierarchyParty")){
					NodeList hierarchyPartyNodeList = XPathUtils.getNodeList(node, "./idf:hierarchyParty");
					for(int i=hierarchyPartyNodeList.getLength(); 0 < i ;i--){
						Node hierarchyPartyNode = hierarchyPartyNodeList.item(i-1);
						String uuid = "";
						String value = "";
						String type = "";
						uuid = XPathUtils.getString(hierarchyPartyNode, "./@uuid").trim();
						
						// "Type"
						if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressIndividualName")){
							type = XPathUtils.getString(hierarchyPartyNode, "./idf:addressType").trim();
						}
						
						// "Name"
						if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressIndividualName")){
							value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressIndividualName").trim();
							String[] valueSpitter = value.split(",");
							
							String firstName = "";
							String lastName = "";
							String nameTitle = "";
							
							for (int j=0;j<valueSpitter.length;j++){
								switch (j) {
									case 0:
										lastName = valueSpitter[j].trim();
										break;
									case 1:
										firstName = valueSpitter[j].trim();
										break;
									case 2:
										for (int k=0;k<valueSpitter[j].trim().split(" ").length;k++)
										switch (k) {
											case 0:
												nameTitle = valueSpitter[j].trim().split(" ")[k];
												break;
											case 1:
												nameTitle = valueSpitter[j].trim().split(" ")[k] + " " + nameTitle;
												break;
											default:
												break;
										}
										break;
									default:
										nameTitle = lastName + " " + valueSpitter[j].trim();
										break;
								}	
									
							}
							elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), nameTitle + " " + firstName + " " + lastName, uuid));
						}
						
						// "Organisation"
						if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressOrganisationName")){
							value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressOrganisationName").trim();
							elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
						}
					}
				}
				
				// "Position"
				xpathExpression = "./gmd:positionName";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String position = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", position);
				}
				
				// "Delivery point"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String deliveryPoint = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", deliveryPoint);
				}
				
				String postalCode = "";
				String city = "";
				
				// "Postcode"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					postalCode = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// "City"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					city = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				if (postalCode.length() > 0 || city.length() > 0) {
					addElement(elements, "textLine", postalCode.concat(" ").concat(city));
				}
				
				// "Country"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String country = XPathUtils.getString(node, xpathExpression).trim();
					if(country != null){
						String value = UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(country), this.request.getLocale().toString());
						if(value != null){
							addElement(elements, "textLine", value);
						}else{
							addElement(elements, "textLine", country);
						}
					}
				}
				addSpace(elements);
				
				// "Mail"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int i = 0; i < electronicMailAddressNodeList.getLength(); i++) {
						String email = XPathUtils.getString(electronicMailAddressNodeList.item(i), ".").trim();
						elements.add(addElementEmailWeb("Email:", email, email, email, LinkType.EMAIL));
					}
				}
				
				// "Telefon"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Telefon:");
				}
				
				// "Fax"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Fax:");
				}
				
				// "URL"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					elements.add(addElementEmailWeb("URL:", value, value, value, LinkType.WWW_URL));
				}
				
				elementsAddress.add(element);
			}
		}
	}
	
	private void getTimeSection(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			ArrayList timeElements = new ArrayList();
			if (node.hasChildNodes()) {
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
							addElementEntryLabelLeft(timeElements, entryLine, messages.getString("time_reference_content"));
						}
					}
				}
				
				// "Status"
				nodeXPathExpression = "../../gmd:status/gmd:MD_ProgressCode";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
					addElementEntryLabelLeft(timeElements, sysCodeList.getNameByCodeListValue("523", domainValue), messages.getString("t01_object.time_status"));
				}
				
				// "Periodizität"
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
					addElementEntryLabelLeft(timeElements, sysCodeList.getNameByCodeListValue("518", domainValue), messages.getString("t01_object.time_period"));
				}
				
				// "Intervall der Erhebung"
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:userDefinedMaintenanceFrequency";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String content = XPathUtils.getString(childNode, ".").trim();
					String value = new TM_PeriodDurationToTimeAlle().parse(content);
					String unit = new TM_PeriodDurationToTimeInterval().parse(content);
					addElementEntryLabelLeft(timeElements, value.concat(" ").concat(unit), messages.getString("t01_object.time_interval"));
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
						
						if (typeId.equals("1")) {
							addElementEntryLabelLeft(timeElements, UtilsDate.convertDateString(time, "yyyy-MM-dd", "dd.MM.yyyy"), sysCodeList.getName("502", typeId));
						}
					}
				}
				
				// "Erläuterung zum Zeitbezug"
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceNote";
				if (XPathUtils.nodeExists(node, nodeXPathExpression)) {
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					addElementEntryLabelLeft(timeElements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.time_descr"));
				}
				
				if(timeElements.size()>0){
					addSectionTitle(elements, messages.getString("time_reference"));
					for(int i=0; i < timeElements.size(); i++){
						elements.add(timeElements.get(i));
					}
					closeDiv(elements);	
				}
				
			}
		}
	}
	
	// TODO: Rewrite
	
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
							addSpace(elements);
							elements.add(element);
						}
					}
					
					// "Raumbezugssystem"
					xpathExpression = "gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code";
					getNodeValue(elements, xpathExpression, messages.getString("t011_obj_geo.referencesystem_id"));
					
					// "Höhe"
					nodeXPathExpression = "gmd:verticalElement";
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
						if (XPathUtils.nodeExists(node, "gmd:verticalElement/gmd:EX_VerticalExtent")) {
							NodeList subNodeList = XPathUtils.getNodeList(node, "gmd:verticalElement/gmd:EX_VerticalExtent");
							for (int j = 0; j < subNodeList.getLength(); j++) {
								Node childNode = subNodeList.item(j);
								ArrayList row = new ArrayList();
								row.add(notNull(XPathUtils.getString(childNode, "gmd:maximumValue")).trim());
								row.add(notNull(XPathUtils.getString(childNode, "gmd:minimumValue")).trim());
								String rowValue;
								if(XPathUtils.nodeExists(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom")){
									rowValue = XPathUtils.getString(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom").trim();
									row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
								}else{
									row.add("");
								}
								
								if(XPathUtils.nodeExists(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name")){
									rowValue = XPathUtils.getString(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name").trim();
									row.add(notNull(sysCodeList.getNameByCodeListValue("101", rowValue)));
								}else{
									row.add("");
								}
								if (!isEmptyRow(row)) {
									body.add(row);
								}
							}
						}
						if (body.size() > 0) {
							addSpace(elements);
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
						
						// "Übergeordnete Objekte"
						xpathExpression ="./idf:superiorReference";
						getReference(elementsGeneral, xpathExpression, ReferenceType.SUPERIOR);
						
						// "Untergeordnete Objekte"
						xpathExpression ="./idf:subordinatedReference";
						getReference(elementsGeneral, xpathExpression, ReferenceType.SUBORDINATE);
						
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
	
	private HashMap addElementLink(String type, Boolean hasLinkIcon, Boolean isExtern, String title) {
		return addElementLink(type, hasLinkIcon, isExtern, title, null);
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
	
	private void getUdkObjClass(HashMap element, ArrayList elements) {
		if (rootNode != null) {
			getUdkObjectClassType(".");
			element.put("udkObjClass", (String) context.get(UDK_OBJ_CLASS_TYPE));
			addElementUdkClass(elements,(String) context.get(UDK_OBJ_CLASS_TYPE));
		}
	}

	private void getTitle(String xpathExpression, HashMap element) {
		boolean nodeExist = XPathUtils.nodeExists(rootNode, xpathExpression);
		if (nodeExist) {
			String title = XPathUtils.getString(rootNode, xpathExpression);
			element.put("title", title);
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
				String hierachyLevelExpression ="";
				
				hierachyLevelExpression = "gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
				if(XPathUtils.nodeExists(node, hierachyLevelExpression)){
					hierachyLevel = XPathUtils.getString(node, hierachyLevelExpression).trim();
				}
				
				hierachyLevelExpression = "gmd:hierarchyLevelName";
				if(XPathUtils.nodeExists(node, hierachyLevelExpression)){
					hierachyLevelName = XPathUtils.getString(node, hierachyLevelExpression).trim();
				}
				
				if(log.isDebugEnabled()){
					log.debug("IDF hierachyLevel: '" + hierachyLevel + "' and IDF hierachyLevelName: '" + hierachyLevelName+ "'");
				}
				
				if(hierachyLevelName.equals("service") && hierachyLevel.equals("service")){
					context.put(UDK_OBJ_CLASS_TYPE, "6");
				}else if(hierachyLevelName.equals("application") && hierachyLevel.equals("application")){
					context.put(UDK_OBJ_CLASS_TYPE, "3");
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
