<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript" src="js/detail_helper.js"></script>
<script type="text/javascript">
_container_.addOnLoad(function() {
	var tree = dojo.widget.byId("tree");
	var nodeDataNew = udkDataProxy._getData();
	var nodeOld = dojo.widget.byId(_container_.customParams.selectedNodeId);

	ObjectService.getPublishedNodeData(nodeOld.id,
		{
			callback:function(res) { renderNodeData(res, nodeDataNew); },
			errorHandler:function(message) {dojo.debug("Error in mdek_compare_view_dialog.jsp: Error while waiting for published nodeData: " + message); }
		}
	);
});

function renderNodeData(nodeDataOld, nodeDataNew) {
	// define date conversion renderer function
	function formatDate(val) {
		return dojo.date.format(val, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
	}

	renderTitel(nodeDataOld.objectName, nodeDataNew.objectName);

	renderText(nodeDataOld.generalShortDescription, nodeDataNew.generalShortDescription);
	renderTextWithTitle(dojo.widget.byId("objectClass")._getDisplayValueForValue("Class"+nodeDataOld.objectClass),
						dojo.widget.byId("objectClass")._getDisplayValueForValue("Class"+nodeDataNew.objectClass), message.get("ui.obj.header.objectClass"));
	renderTextWithTitle(nodeDataOld.generalDescription, nodeDataNew.generalDescription, message.get("ui.obj.general.description"));

	UtilList.addAddressTitles(nodeDataOld.generalAddressTable);
	UtilList.addAddressLinkLabels(nodeDataOld.generalAddressTable);
	renderTable(nodeDataOld.generalAddressTable, nodeDataNew.generalAddressTable, ["nameOfRelation", "linkLabel"], [message.get("dialog.compare.object.addressLink.type"), message.get("dialog.compare.object.addressLink.title")], message.get("ui.obj.general.addressTable.title"));

    renderList(nodeDataOld.thesaurusInspireTermsList, nodeDataNew.thesaurusInspireTermsList, message.get("ui.obj.thesaurus.terms.inspire"), null, function (val) { return dojo.widget.byId("thesaurusInspireCombobox")._getDisplayValueForValue(val);});

	// technical domains
	if (nodeDataOld.objectClass != nodeDataNew.objectClass) {
		// TODO Don't display class specific data when the objects have different classes?
	}
	
	if (nodeDataNew.objectClass == 1) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Objekt, Karte
		renderTextWithTitle(nodeDataOld.ref1BasisText, nodeDataNew.ref1BasisText, message.get("ui.obj.type1.technicalBasisTable.title"));
		renderTextWithTitle(nodeDataOld.ref1ObjectIdentifier, nodeDataNew.ref1ObjectIdentifier, message.get("ui.obj.type1.identifier"));
		renderTextWithTitle(dojo.widget.byId("ref1DataSet")._getDisplayValueForValue(nodeDataOld.ref1DataSet), dojo.widget.byId("ref1DataSet")._getDisplayValueForValue(nodeDataNew.ref1DataSet), message.get("ui.obj.type1.dataset"));
		renderList(nodeDataOld.ref1Representation, nodeDataNew.ref1Representation, message.get("ui.obj.type1.digitalRepresentation"), null, function(val) { return dojo.widget.byId("ref1RepresentationCombobox")._getDisplayValueForValue(val); });
		renderTextWithTitle(nodeDataOld.ref1Coverage, nodeDataNew.ref1Coverage, message.get("ui.obj.type1.coverage") + " [%]");
		renderTextWithTitle(dojo.widget.byId("ref1VFormatTopology")._getDisplayValueForValue(nodeDataOld.ref1VFormatTopology), dojo.widget.byId("ref1VFormatTopology")._getDisplayValueForValue(nodeDataNew.ref1VFormatTopology), message.get("ui.obj.type1.vectorFormat.topology"));
		renderTable(nodeDataOld.ref1VFormatDetails, nodeDataNew.ref1VFormatDetails, ["geometryType", "numElements"], [message.get("ui.obj.type1.vectorFormat.detailsTable.header.geoType"), message.get("ui.obj.type1.vectorFormat.detailsTable.header.elementCount")], message.get("ui.obj.type1.vectorFormat.title"), [function(val) {return dojo.widget.byId("geometryTypeEditor")._getDisplayValueForValue(val);}, null]);
// moved to general "Raumbezug" section, class independent !
//		renderTextWithTitle(nodeDataOld.ref1SpatialSystem, nodeDataNew.ref1SpatialSystem, message.get("ui.obj.type1.spatialSystem"));
		renderTable(nodeDataOld.ref1Scale, nodeDataNew.ref1Scale, ["scale", "groundResolution", "scanResolution"], [message.get("ui.obj.type1.scaleTable.header.scale"), message.get("ui.obj.type1.scaleTable.header.groundResolution"), message.get("ui.obj.type1.scaleTable.header.scanResolution")], message.get("ui.obj.type1.scaleTable.title"));
		renderTextWithTitle(nodeDataOld.ref1AltAccuracy, nodeDataNew.ref1AltAccuracy, message.get("ui.obj.type1.sizeAccuracy"));
		renderTextWithTitle(nodeDataOld.ref1PosAccuracy, nodeDataNew.ref1PosAccuracy, message.get("ui.obj.type1.posAccuracy"));
		renderTable(nodeDataOld.ref1SymbolsText, nodeDataNew.ref1SymbolsText, ["title", "date", "version"], [message.get("ui.obj.type1.symbolCatTable.header.title"), message.get("ui.obj.type1.symbolCatTable.header.date"), message.get("ui.obj.type1.symbolCatTable.header.version")], message.get("ui.obj.type1.symbolCatTable.title"), [null, formatDate, null]);
		renderTable(nodeDataOld.ref1KeysText, nodeDataNew.ref1KeysText, ["title", "date", "version"], [message.get("ui.obj.type1.keyCatTable.header.title"), message.get("ui.obj.type1.keyCatTable.header.date"), message.get("ui.obj.type1.keyCatTable.header.version")], message.get("ui.obj.type1.keyCatTable.title"), [null, formatDate, null]);
		renderTextWithTitle(nodeDataOld.ref1DataBasisText, nodeDataNew.ref1DataBasisText, message.get("ui.obj.type1.dataBasisTable.title"));
		renderList(nodeDataOld.ref1Data, nodeDataNew.ref1Data, message.get("ui.obj.type1.attributes"));
		renderTextWithTitle(nodeDataOld.ref1ProcessText, nodeDataNew.ref1ProcessText, message.get("ui.obj.type1.processTable.title"));

        // DQ
		renderSectionTitel(message.get("ui.obj.dq"));
        renderTable(nodeDataOld.dq109Table, nodeDataNew.dq109Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table109.title"));
        renderTable(nodeDataOld.dq110Table, nodeDataNew.dq110Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table110.title"));
        renderTable(nodeDataOld.dq112Table, nodeDataNew.dq112Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table112.title"));
        renderTable(nodeDataOld.dq113Table, nodeDataNew.dq113Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table113.title"));
        renderTable(nodeDataOld.dq114Table, nodeDataNew.dq114Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table114.title"));
        renderTable(nodeDataOld.dq115Table, nodeDataNew.dq115Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table115.title"));
        renderTable(nodeDataOld.dq117Table, nodeDataNew.dq117Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table117.title"));
        renderTable(nodeDataOld.dq120Table, nodeDataNew.dq120Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table120.title"));
        renderTable(nodeDataOld.dq125Table, nodeDataNew.dq125Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table125.title"));
        renderTable(nodeDataOld.dq126Table, nodeDataNew.dq126Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table126.title"));
        renderTable(nodeDataOld.dq127Table, nodeDataNew.dq127Table, ["nameOfMeasure", "resultValue", "measureDescription"], [message.get("ui.obj.dq.table.header1"), message.get("ui.obj.dq.table.header2"), message.get("ui.obj.dq.table.header3")], message.get("ui.obj.dq.table127.title"));

	} else if (nodeDataNew.objectClass == 2) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Literature
		renderTextWithTitle(nodeDataOld.ref2Author, nodeDataNew.ref2Author, message.get("ui.obj.type2.author"));
		renderTextWithTitle(nodeDataOld.ref2Publisher, nodeDataNew.ref2Publisher, message.get("ui.obj.type2.editor"));
		renderTextWithTitle(nodeDataOld.ref2PublishedIn, nodeDataNew.ref2PublishedIn, message.get("ui.obj.type2.publishedIn"));
		renderTextWithTitle(nodeDataOld.ref2PublishLocation, nodeDataNew.ref2PublishLocation, message.get("ui.obj.type2.publishedLocation"));
		renderTextWithTitle(nodeDataOld.ref2PublishedInIssue, nodeDataNew.ref2PublishedInIssue, message.get("ui.obj.type2.issue"));
		renderTextWithTitle(nodeDataOld.ref2PublishedInPages, nodeDataNew.ref2PublishedInPages, message.get("ui.obj.type2.pages"));
		renderTextWithTitle(nodeDataOld.ref2PublishedInYear, nodeDataNew.ref2PublishedInYear, message.get("ui.obj.type2.publishedYear"));
		renderTextWithTitle(nodeDataOld.ref2PublishedISBN, nodeDataNew.ref2PublishedISBN, message.get("ui.obj.type2.isbn"));
		renderTextWithTitle(nodeDataOld.ref2PublishedPublisher, nodeDataNew.ref2PublishedPublisher, message.get("ui.obj.type2.publisher"));
		renderTextWithTitle(nodeDataOld.ref2LocationText, nodeDataNew.ref2LocationText, message.get("ui.obj.type2.locationTable.title"));
		renderTextWithTitle(nodeDataOld.ref2DocumentType, nodeDataNew.ref2DocumentType, message.get("ui.obj.type2.documentType"));
		renderTextWithTitle(nodeDataOld.ref2BaseDataText, nodeDataNew.ref2BaseDataText, message.get("ui.obj.type2.generalDataTable.title"));
		renderTextWithTitle(nodeDataOld.ref2BibData, nodeDataNew.ref2BibData, message.get("ui.obj.type2.additionalBibInfo"));
		renderTextWithTitle(nodeDataOld.ref2Explanation, nodeDataNew.ref2Explanation, message.get("ui.obj.type2.description"));
	} else if (nodeDataNew.objectClass == 3) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Geodatendienst
		renderList(nodeDataOld.ref3ServiceTypeTable, nodeDataNew.ref3ServiceTypeTable, message.get("ui.obj.type3.ref3ServiceTypeTable.title"), null, function(val) { return dojo.widget.byId("ref3ServiceTypeEditor")._getDisplayValueForValue(val); });
		renderTextWithTitle(dojo.widget.byId("ref3ServiceType")._getDisplayValueForValue(nodeDataOld.ref3ServiceType), dojo.widget.byId("ref3ServiceType")._getDisplayValueForValue(nodeDataNew.ref3ServiceType), message.get("ui.obj.type3.ref3ServiceTypeTable.title"));
		renderList(nodeDataOld.ref3ServiceVersion, nodeDataNew.ref3ServiceVersion, message.get("ui.obj.type3.serviceVersion"));
		renderTextWithTitle(nodeDataOld.ref3SystemEnv, nodeDataNew.ref3SystemEnv, message.get("ui.obj.type3.environment"));
		renderTextWithTitle(nodeDataOld.ref3History, nodeDataNew.ref3History, message.get("ui.obj.type3.history"));
		renderTextWithTitle(nodeDataOld.ref3BaseDataText, nodeDataNew.ref3BaseDataText, message.get("ui.obj.type3.generalDataTable.title") + " (" + message.get("ui.obj.type3.generalDataTable.tab.text") + ")");
		renderTable(nodeDataOld.ref3Scale, nodeDataNew.ref3Scale, ["scale", "groundResolution", "scanResolution"], [message.get("ui.obj.type1.scaleTable.header.scale"), message.get("ui.obj.type1.scaleTable.header.groundResolution"), message.get("ui.obj.type1.scaleTable.header.scanResolution")], message.get("ui.obj.type3.scaleTable.title"));
		renderTextWithTitle(nodeDataOld.ref3HasAccessConstraint ? message.get("general.yes"): message.get("general.no"), nodeDataNew.ref3HasAccessConstraint ? message.get("general.yes"): message.get("general.no"), message.get("ui.obj.type3.ref3HasAccessConstraint.title"));
//		renderOperations(nodeData.ref3Operation);
	} else if (nodeDataNew.objectClass == 4) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Vorhaben
		renderTextWithTitle(nodeDataOld.ref4ParticipantsText, nodeDataNew.ref4ParticipantsText, message.get("ui.obj.type4.participantsTable.title"));
		renderTextWithTitle(nodeDataOld.ref4PMText, nodeDataNew.ref4PMText, message.get("ui.obj.type4.projectManagerTable.title"));
		renderTextWithTitle(nodeDataOld.ref4Explanation, nodeDataNew.ref4Explanation, message.get("Erl&auml;uterungen"));
	} else if (nodeDataNew.objectClass == 5) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Datensammlung/Datenbank
		renderTable(nodeDataOld.ref5dbContent, nodeDataNew.ref5dbContent, ["parameter", "additionalData"], [message.get("ui.obj.type5.contentTable.header.parameter"), message.get("ui.obj.type5.contentTable.header.additionalData")], message.get("ui.obj.type5.contentTable.header.additionalData"));
		renderTextWithTitle(nodeDataOld.ref5MethodText, nodeDataNew.ref5MethodText, message.get("ui.obj.type5.methodTable.title"));
		renderTextWithTitle(nodeDataOld.ref5Explanation, nodeDataNew.ref5Explanation, message.get("ui.obj.type5.description"));
	} else if (nodeDataNew.objectClass == 6) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Dienst/Anwendung/Informationssystem
		renderTextWithTitle(dojo.widget.byId("ref6ServiceType")._getDisplayValueForValue(nodeDataOld.ref6ServiceType), dojo.widget.byId("ref6ServiceType")._getDisplayValueForValue(nodeDataNew.ref6ServiceType), message.get("ui.obj.type6.ref6ServiceType"));
		renderList(nodeDataOld.ref6ServiceVersion, nodeDataNew.ref6ServiceVersion, message.get("ui.obj.type6.serviceVersion"));
		renderTextWithTitle(nodeDataOld.ref6SystemEnv, nodeDataNew.ref6SystemEnv, message.get("ui.obj.type6.environment"));
		renderTextWithTitle(nodeDataOld.ref6History, nodeDataNew.ref6History, message.get("ui.obj.type6.history"));
		renderTextWithTitle(nodeDataOld.ref6BaseDataText, nodeDataNew.ref6BaseDataText, message.get("ui.obj.type6.generalDataTable.title") + " (" + message.get("ui.obj.type6.generalDataTable.tab.text") + ")");
		renderTable(nodeDataOld.ref6UrlList, nodeDataNew.ref6UrlList, ["name", "url", "urlDescription"], [message.get("ui.obj.type6.urlList.header.name"), message.get("ui.obj.type6.urlList.header.url"), message.get("ui.obj.type6.urlList.header.urlDescription")], message.get("ui.obj.type6.urlList"));
	}

	// spatial reference
	renderSectionTitel(message.get("ui.obj.spatial.title"));
	UtilList.addSNSLocationLabels(nodeDataOld.spatialRefAdminUnitTable);
	renderTable(nodeDataOld.spatialRefAdminUnitTable, nodeDataNew.spatialRefAdminUnitTable, ["label", "nativeKey", "longitude1", "latitude1", "longitude2", "latitude2"], [message.get("ui.obj.spatial.geoThesTable.header.name"), message.get("ui.obj.spatial.geoThesTable.header.nativeKey"), message.get("ui.obj.spatial.geoThesTable.header.longitude1"), message.get("ui.obj.spatial.geoThesTable.header.latitude1"), message.get("ui.obj.spatial.geoThesTable.header.longitude2"), message.get("ui.obj.spatial.geoThesTable.header.latitude2")], message.get("dialog.compare.object.spatialTable.title"));
	renderTable(nodeDataOld.spatialRefLocationTable, nodeDataNew.spatialRefLocationTable, ["name", "longitude1", "latitude1", "longitude2", "latitude2"], [message.get("ui.obj.spatial.geoTable.header.name"), message.get("ui.obj.spatial.geoTable.header.longitude1"), message.get("ui.obj.spatial.geoTable.header.latitude1"), message.get("ui.obj.spatial.geoTable.header.longitude2"), message.get("ui.obj.spatial.geoTable.header.latitude2")], message.get("ui.obj.spatial.geoTable.title"));
    // former class 1, now general "Raumbezug"
    renderTextWithTitle(nodeDataOld.ref1SpatialSystem, nodeDataNew.ref1SpatialSystem, message.get("ui.obj.type1.spatialSystem"));

	var altitudeDataOld = [nodeDataOld];
	var altitudeDataNew = [nodeDataNew];
	// create cell render functions
	function lookupSpatialRefAltMeasure(val) {
		return dojo.widget.byId("spatialRefAltMeasure")._getDisplayValueForValue(val);
	}
	function lookupSpatialRefAltVDate(val) {
		return dojo.widget.byId("spatialRefAltVDate")._getDisplayValueForValue(val);
	}

	if (altitudeDataOld[0].spatialRefAltMin == null) { altitudeDataOld[0].spatialRefAltMin = ""; }
	if (altitudeDataOld[0].spatialRefAltMax == null) { altitudeDataOld[0].spatialRefAltMax = ""; }
	if (altitudeDataOld[0].spatialRefAltMeasure = null) { altitudeDataOld[0].spatialRefAltMeasure = ""; }
	if (altitudeDataOld[0].spatialRefAltVDate == null) { altitudeDataOld[0].spatialRefAltVDate = ""; }
	renderTable(altitudeDataOld, altitudeDataNew, ["spatialRefAltMin", "spatialRefAltMax", "spatialRefAltMeasure", "spatialRefAltVDate"], [message.get("ui.obj.spatial.height.min"), message.get("ui.obj.spatial.height.max"), message.get("ui.obj.spatial.height.unit"), message.get("ui.obj.spatial.height.geodeticSystem")], message.get("ui.obj.spatial.height"), [null, null, lookupSpatialRefAltMeasure, lookupSpatialRefAltVDate]);
	renderTextWithTitle(nodeDataOld.spatialRefExplanation, nodeDataNew.spatialRefExplanation, message.get("ui.obj.spatial.description"));

	// temporal reference
	renderSectionTitel(message.get("ui.obj.time.title"));
	var timeRefTxtOld;
	var timeRefTxtNew;
	if (nodeDataOld.timeRefDate1) {
		if (nodeDataOld.timeRefType && nodeDataOld.timeRefType == "von") {
			timeRefTxtOld = "von "+dojo.date.format(nodeDataOld.timeRefDate1, {selector:'dateOnly', datePattern:'dd.MM.yyyy'})+" bis "+dojo.date.format(nodeDataOld.timeRefDate2, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
		} else if (nodeDataOld.timeRefType) {
			timeRefTxtOld = nodeDataOld.timeRefType+" "+dojo.date.format(nodeDataOld.timeRefDate1, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
		}
	}
	if (nodeDataNew.timeRefDate1) {
		if (nodeDataNew.timeRefType && nodeDataNew.timeRefType == "von") {
			timeRefTxtNew = "von "+dojo.date.format(nodeDataNew.timeRefDate1, {selector:'dateOnly', datePattern:'dd.MM.yyyy'})+" bis "+dojo.date.format(nodeDataNew.timeRefDate2, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
		} else if (nodeDataNew.timeRefType) {
			timeRefTxtNew = nodeDataNew.timeRefType+" "+dojo.date.format(nodeDataNew.timeRefDate1, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
		}
	}

	renderTextWithTitle(timeRefTxtOld, timeRefTxtNew, message.get("ui.obj.time.timeRefContent"));
	renderTextWithTitle(dojo.widget.byId("timeRefStatus")._getDisplayValueForValue(nodeDataOld.timeRefStatus), dojo.widget.byId("timeRefStatus")._getDisplayValueForValue(nodeDataNew.timeRefStatus), message.get("ui.obj.time.state"));
	renderTextWithTitle(dojo.widget.byId("timeRefPeriodicity")._getDisplayValueForValue(nodeDataOld.timeRefPeriodicity), dojo.widget.byId("timeRefPeriodicity")._getDisplayValueForValue(nodeDataNew.timeRefPeriodicity), message.get("ui.obj.time.periodicity"));
	if ((nodeDataOld.timeRefIntervalNum && nodeDataOld.timeRefIntervalUnit) || (nodeDataNew.timeRefIntervalNum && nodeDataNew.timeRefIntervalUnit)) {
		var textOld = message.get("ui.obj.time.interval.each")+ " "+nodeDataOld.timeRefIntervalNum+" "+dojo.widget.byId("timeRefIntervalUnit")._getDisplayValueForValue(nodeDataOld.timeRefIntervalUnit);
		var textNew = message.get("ui.obj.time.interval.each")+ " "+nodeDataNew.timeRefIntervalNum+" "+dojo.widget.byId("timeRefIntervalUnit")._getDisplayValueForValue(nodeDataNew.timeRefIntervalUnit);
		renderTextWithTitle(textOld, textNew, message.get("ui.obj.time.interval"));
	}
	// create cell render functions
	function lookupTimeRefType(val) {
		return dojo.widget.byId("timeRefTypeCombobox")._getDisplayValueForValue(val);
	}
	renderTable(nodeDataOld.timeRefTable, nodeDataNew.timeRefTable, ["date", "type"], [message.get("ui.obj.time.timeRefTable.header.date"), message.get("ui.obj.time.timeRefTable.header.type")], message.get("ui.obj.time.timeRefTable.title"), [formatDate, lookupTimeRefType]);
	renderTextWithTitle(nodeDataOld.timeRefExplanation, nodeDataNew.timeRefExplanation, message.get("ui.obj.time.description"));
	
	// additional information
	renderSectionTitel(message.get("ui.obj.additionalInfo.title"));
	renderTextWithTitle(dojo.widget.byId("extraInfoLangMetaData")._getDisplayValueForValue(nodeDataOld.extraInfoLangMetaDataCode), dojo.widget.byId("extraInfoLangMetaData")._getDisplayValueForValue(nodeDataNew.extraInfoLangMetaDataCode), message.get("ui.obj.additionalInfo.language.metadata"));
	renderTextWithTitle(dojo.widget.byId("extraInfoLangData")._getDisplayValueForValue(nodeDataOld.extraInfoLangDataCode), dojo.widget.byId("extraInfoLangData")._getDisplayValueForValue(nodeDataNew.extraInfoLangDataCode), message.get("ui.obj.additionalInfo.language.data"));
	renderTextWithTitle(dojo.widget.byId("extraInfoPublishArea")._getDisplayValueForValue(nodeDataOld.extraInfoPublishArea), dojo.widget.byId("extraInfoPublishArea")._getDisplayValueForValue(nodeDataNew.extraInfoPublishArea), message.get("ui.obj.additionalInfo.publicationCondition"));
    renderTextWithTitle(dojo.widget.byId("extraInfoCharSetData")._getDisplayValueForValue(nodeDataOld.extraInfoCharSetDataCode), dojo.widget.byId("extraInfoCharSetData")._getDisplayValueForValue(nodeDataNew.extraInfoCharSetDataCode), message.get("ui.obj.additionalInfo.charSet.data"));
	// Table is only displayed for object classes 1 and 3
	if (nodeDataNew.objectClass == 1 || nodeDataNew.objectClass == 3) {
		renderTable(nodeDataOld.extraInfoConformityTable, nodeDataNew.extraInfoConformityTable, ["level", "specification", "date"],
					[message.get("ui.obj.additionalInfo.conformityTable.header.level"), message.get("ui.obj.additionalInfo.conformityTable.header.specification"), message.get("ui.obj.additionalInfo.conformityTable.header.date")],
					message.get("ui.obj.additionalInfo.conformityTable.title"),
					[function(val) { return dojo.widget.byId("extraInfoConformityLevelEditor")._getDisplayValueForValue(val); }, null, formatDate]);
	}
	renderList(nodeDataOld.extraInfoXMLExportTable, nodeDataNew.extraInfoXMLExportTable, message.get("ui.obj.additionalInfo.xmlExportCriteria"));
	renderList(nodeDataOld.extraInfoLegalBasicsTable, nodeDataNew.extraInfoLegalBasicsTable, message.get("ui.obj.additionalInfo.legalBasis"));
	renderTextWithTitle(nodeDataOld.extraInfoPurpose, nodeDataNew.extraInfoPurpose, message.get("ui.obj.additionalInfo.purpose"));
	renderTextWithTitle(nodeDataOld.extraInfoUse, nodeDataNew.extraInfoUse, message.get("ui.obj.additionalInfo.suitability"));
	
	// availability
	renderSectionTitel(message.get("ui.obj.availability.title"));
    renderList(nodeDataOld.availabilityAccessConstraints, nodeDataNew.availabilityAccessConstraints, message.get("ui.obj.availability.accessConstraints"), null, function (val) { return dojo.widget.byId("availabilityAccessConstraintsEditor")._getDisplayValueForValue(val);});
    renderList(nodeDataOld.availabilityUseConstraints, nodeDataNew.availabilityUseConstraints, message.get("ui.obj.availability.useConstraints"));

	renderTable(nodeDataOld.availabilityDataFormatTable, nodeDataNew.availabilityDataFormatTable, ["name", "version", "compression", "pixelDepth"], [message.get("ui.obj.availability.dataFormatTable.header.name"), message.get("ui.obj.availability.dataFormatTable.header.version"), message.get("ui.obj.availability.dataFormatTable.header.compression"), message.get("ui.obj.availability.dataFormatTable.header.depth")], message.get("ui.obj.availability.dataFormatTable.title"));
	renderTable(nodeDataOld.availabilityMediaOptionsTable, nodeDataNew.availabilityMediaOptionsTable, ["name", "transferSize", "location"], [message.get("ui.obj.availability.mediaOptionTable.header.type"), message.get("ui.obj.availability.mediaOptionTable.header.amount"), message.get("ui.obj.availability.mediaOptionTable.header.location")], message.get("ui.obj.availability.mediaOptionTable.title"), [function(val) { return dojo.widget.byId("availabilityMediaOptionsMediumCombobox")._getDisplayValueForValue(val); }, null, null]);
	renderTextWithTitle(nodeDataOld.availabilityOrderInfo, nodeDataNew.availabilityOrderInfo, message.get("ui.obj.availability.orderInfo"));

	// indexing
	renderSectionTitel(message.get("ui.obj.thesaurus.title"));
	renderList(nodeDataOld.thesaurusTermsTable, nodeDataNew.thesaurusTermsTable, message.get("ui.adr.thesaurus.terms"), "title");
	renderList(nodeDataOld.thesaurusTopicsList, nodeDataNew.thesaurusTopicsList, message.get("ui.obj.thesaurus.terms.category"), null, function (val) { return dojo.widget.byId("thesaurusTopicsCombobox")._getDisplayValueForValue(val);});
	renderTextWithTitle(nodeDataOld.thesaurusEnvExtRes ? message.get("general.yes"): message.get("general.no"), nodeDataNew.thesaurusEnvExtRes ? message.get("general.yes"): message.get("general.no"), message.get("ui.obj.thesaurus.terms.enviromental.displayCatalogPage"));
	renderList(nodeDataOld.thesaurusEnvTopicsList, nodeDataNew.thesaurusEnvTopicsList, message.get("ui.obj.thesaurus.terms.enviromental.title")+ " - " + message.get("ui.obj.thesaurus.terms.enviromental.topics"), null, function (val) { return dojo.widget.byId("thesaurusEnvTopicsCombobox")._getDisplayValueForValue(val);});
	renderList(nodeDataOld.thesaurusEnvCatsList, nodeDataNew.thesaurusEnvCatsList, message.get("ui.obj.thesaurus.terms.enviromental.title")+ " - " + message.get("ui.obj.thesaurus.terms.enviromental.categories"), null, function (val) { return dojo.widget.byId("thesaurusEnvCatsCombobox")._getDisplayValueForValue(val);});

	// references
	renderSectionTitel(message.get("ui.obj.links.title"));
	renderList(nodeDataOld.linksFromObjectTable, nodeDataNew.linksFromObjectTable, message.get("dialog.compare.object.linksFromTable.title"), "title")
	renderList(nodeDataOld.linksToObjectTable, nodeDataNew.linksToObjectTable, message.get("dialog.compare.object.linksToTable.title"), "title")
	renderUrlLinkList(nodeDataOld.linksToUrlTable, nodeDataNew.linksToUrlTable);
	
	// administrative data
	renderSectionTitel(message.get("dialog.compare.object.administrative"));
	renderTextWithTitle(nodeDataOld.uuid, nodeDataNew.uuid, message.get("dialog.compare.object.id"));
	renderTextWithTitle(catalogData.catalogName, catalogData.catalogName, message.get("dialog.compare.object.catalog"));
}

function renderSectionTitel(val) {
	dojo.byId("diffContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
	dojo.byId("oldContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
	dojo.byId("currentContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
}

function renderTitel(oldVal, newVal) {
	if (!oldVal) {
		oldVal = "";
	}
	if (!newVal) {
		newVal = "";
	}
	dojo.byId("diffContent").innerHTML += "<strong>" + diffString(oldVal, newVal)+"</strong><br/><br/>";
	dojo.byId("oldContent").innerHTML += "<strong>" + oldVal + "</strong><br/><br/>";
	dojo.byId("currentContent").innerHTML += "<strong>" + newVal + "</strong><br/><br/>";
}

function renderTextWithTitle(oldVal, newVal, title) {
	if (!detailHelper.isValid(oldVal) && !detailHelper.isValid(newVal)) {
		return;
	}

	if (oldVal == null) {
		oldVal = "";
	}
	if (newVal == null) {
		newVal = "";
	}

	oldVal += "";
	newVal += "";

	dojo.byId("diffContent").innerHTML += "<strong>" + title + "</strong><p>" + diffString(oldVal, newVal) + "</p><br/>";
	dojo.byId("oldContent").innerHTML += "<strong>" + title + "</strong><p>" + oldVal.replace(/\n/g, "<br />") + "</p><br/>";
	dojo.byId("currentContent").innerHTML += "<strong>" + title + "</strong><p>" + newVal.replace(/\n/g, "<br />") + "</p><br/>";
}

function renderLinkedObjectListWithTitle(oldList, newList, title) {
	if (oldList.length > 0) {
		var t = "<strong>" + title + "</strong><br/><br/><p>";
		for (var i = 0; i < oldList.length; ++i) {
			t += "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+oldList[i].uuid+"\");'"+
			                    "title='"+oldList[i].title+"'>"+oldList[i].title+"</a>" + "<br/>";
		}
		dojo.byId("oldContent").innerHTML += t+"</p><br/><br/>";
	}
	if (newList.length > 0) {
		var t = "<strong>" + title + "</strong><br/><br/><p>";
		for (var i = 0; i < newList.length; ++i) {
			t += "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+newList[i].uuid+"\");'"+
			                    "title='"+newList[i].title+"'>"+newList[i].title+"</a>" + "<br/>";
		}
		dojo.byId("currentContent").innerHTML += t+"</p><br/><br/>";
	}
	if (oldList.length > 0 || newList.length > 0) {
		var t = "<strong>" + title + "</strong><br/><br/><p>";
		var len = (newList.length > oldList.length)? newList.length : oldList.length;
		for (var i = 0; i < len; ++i) {
			if (i > (oldList.length-1)) {
				t += diffString("", newList[i].title)+"<br/>";
			} else if (i > (newList.length-1)) {
				t += diffString(oldList[i].title, "")+"<br/>";
			} else {
				t += diffString(oldList[i].title, newList[i].title)+"<br/>";
			}
		}
		dojo.byId("diffContent").innerHTML += t+"</p><br/><br/>";
	}
}

function renderAddressList(oldList, newList) {
	var oldText = "";
	var newText = "";
	if (oldList.length > 0) {
		for (var i = 0; i < oldList.length; ++i) {
			oldText += oldList[i].nameOfRelation+ ": ";
			oldText += UtilAddress.createAddressTitle(oldList[i])+ "\n";
		}
		dojo.byId("oldContent").innerHTML += oldText.replace(/\n/g, '<br />') + "<br /><br />";
	}

	if (newList.length > 0) {
		for (var i = 0; i < newList.length; ++i) {
			newText += newList[i].nameOfRelation+ ": ";
			newText += UtilAddress.createAddressTitle(newList[i])+ "\n";
		}
		dojo.byId("currentContent").innerHTML += newText.replace(/\n/g, '<br />') + "<br /><br />";
	}

	var diffText = diffString(oldText, newText);
	diffText = diffText.replace(/\n/g, '<br />').replace(/&para;/g, '');

	dojo.byId("diffContent").innerHTML += diffText + "<br /><br />";
}

function renderText(oldVal, newVal) {
	if (oldVal == null) {
		oldVal = "";
	}
	if (newVal == null) {
		newVal = "";
	}

	oldVal += "";
	newVal += "";

	dojo.byId("diffContent").innerHTML += "<p>" + diffString(oldVal, newVal) + "</p><br/>";
	dojo.byId("oldContent").innerHTML += "<p>" + oldVal.replace(/\n/g, "<br />") + "</p><br/>";
	dojo.byId("currentContent").innerHTML += "<p>" + newVal.replace(/\n/g, "<br />") + "</p><br/>";
}

function diffString(oldText, newText) {
	return WDiffString(oldText, newText);
}

function buildListHead(title) {
	var t = "<p>";
	if (detailHelper.isValid(title)) {
		t += "<strong>" + title + "</strong><br/>";
	}
	return t;
}

function buildListBody(list, rowProperty, renderFunction) {
	var valList = "";
	for (var i=0; i<list.length; i++) {
		var val = "";
		if (rowProperty) {
			val = list[i][rowProperty];
		} else {
			val = list[i];
		}
		if (renderFunction) {
			val = renderFunction.call(this, val);
		}
		if (val && val != "") {
			valList += val + "<br/>";
		}
	}
	return valList;
}

function buildListBodyForDiff(diff, rowProperty, renderFunction) {
	var diffList = diff.eq.concat(diff.ins.concat(diff.del));

	var valList = "";
	for (var i=0; i<diffList.length; i++) {
		var val = "";
		var prefix = "";
		var suffix = "";
		if (arrayContains(diff.ins, diffList[i])) {
			prefix = "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;'>";
			suffix = "</span>";
		} else if (arrayContains(diff.del, diffList[i])) {
			prefix = "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;'>";
			suffix = "</span>";
		}

		if (rowProperty) {
			val = diffList[i][rowProperty];
		} else {
			val = diffList[i];
		}
		if (renderFunction) {
			val = renderFunction.call(this, val);
		}
		if (val && val != "") {
			valList += prefix + val + suffix + "<br/>";
		}
	}
	return valList;
}

function renderList(oldList, newList, title, rowProperty, renderFunction) {
	if (oldList && oldList.length > 0) {
		var t = buildListHead(title);
		var valList = buildListBody(oldList, rowProperty, renderFunction);

		if (valList != "") {
			dojo.byId("oldContent").innerHTML += t + valList + "</p><br/>";
		}
	}	

	if (newList && newList.length > 0) {
		var t = buildListHead(title);
		var valList = buildListBody(newList, rowProperty, renderFunction);

		if (valList != "") {
			dojo.byId("currentContent").innerHTML += t + valList + "</p><br/>";
		}
	}	

	var diff = compareTable(oldList, newList, rowProperty);

	if ((oldList && oldList.length > 0) || (newList && newList.length > 0)) {
		var t = buildListHead(title);
		var valList = buildListBodyForDiff(diff, rowProperty, renderFunction);

		if (valList != "") {
			dojo.byId("diffContent").innerHTML += t + valList + "</p><br/>";
		}
	}
}

function renderTable(oldList, newList, rowProperties, listHeader, title, cellRenderFunction) {
	if (oldList && oldList.length > 0) {
		var t = buildTableHead(listHeader, title);
		t += buildTableBody(oldList, rowProperties, cellRenderFunction);

		dojo.byId("oldContent").innerHTML += t + "<br/><br/>";
	}

	if (newList && newList.length > 0) {
		var t = buildTableHead(listHeader, title);
		t += buildTableBody(newList, rowProperties, cellRenderFunction);

		dojo.byId("currentContent").innerHTML += t + "<br/><br/>";
	}

	var diff = compareTable(oldList, newList, rowProperties);

	if ((oldList && oldList.length > 0) || (newList && newList.length > 0)) {
		var t = buildTableHead(listHeader, title);
		t += buildTableBodyForDiff(diff, rowProperties, cellRenderFunction);

		dojo.byId("diffContent").innerHTML += t + "<br/><br/>";
	}
}

function buildTableHead(listHeader, title) {
	var t = "";
	if (detailHelper.isValid(title)) {
		t += "<strong>" + title + "</strong><br/><br/>";
	}
	t += "<p><table class=\"filteringTable\" cellspacing=\"0\">";
	if (listHeader && listHeader.length > 0) {
		t += "<thead class=\"fixedHeader\"><tr>";
		for (i=0; i<listHeader.length; i++) {
			t += "<th style=\"padding-right:4px\">"+listHeader[i]+"</th>";
		}
		t += "</tr></thead>";
	}
	return t;
}

function buildTableBody(list, rowProperties, cellRenderFunction) {
	var t = "<tbody>";
	for (var i=0; i<list.length; i++) {
		if (i % 2) {
			t += "<tr class=\"alt\">";
		} else {
			t += "<tr>";
		}
		for (var j=0; j<rowProperties.length; j++) {
			if (cellRenderFunction && cellRenderFunction[j]) {
				t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, list[i][rowProperties[j]])+"</td>";
			} else {
				t += "<td style=\"padding-right:4px\">"+list[i][rowProperties[j]]+"</td>";
			}
		}
		t += "</tr>";
		
	}
	t += "</tbody></table></p>";
	return t;
}

function buildTableBodyForDiff(diff, rowProperties, cellRenderFunction) {
	var diffList = diff.eq.concat(diff.ins.concat(diff.del));

	var t = "<tbody>";
	for (var i=0; i<diffList.length; i++) {
		if (i % 2) {
			t += "<tr class=\"alt\">";
		} else {
			t += "<tr>";
		}
		for (var j=0; j<rowProperties.length; j++) {
			if (cellRenderFunction && cellRenderFunction[j]) {
				t += "<td style=\"padding-right:4px\">";
				if (arrayContains(diff.ins, diffList[i])) {
					t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;'>";
					t += cellRenderFunction[j].call(this, diffList[i][rowProperties[j]]);
					t += "</span>";
				} else if (arrayContains(diff.del, diffList[i])) {
					t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;'>";
					t += cellRenderFunction[j].call(this, diffList[i][rowProperties[j]]);
					t += "</span>";
				} else {
					t += cellRenderFunction[j].call(this, diffList[i][rowProperties[j]]);
				}

				t += "</td>";
			} else {
				t += "<td style=\"padding-right:4px\">";
				if (arrayContains(diff.ins, diffList[i])) {
					t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;'>";
					t += diffList[i][rowProperties[j]]
					t += "</span>";
				} else if (arrayContains(diff.del, diffList[i])) {
					t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;'>";
					t += diffList[i][rowProperties[j]]
					t += "</span>";
				} else {
					t += diffList[i][rowProperties[j]]
				}
			}
		}
		t += "</tr>";
		
	}
	t += "</tbody></table></p>";
	return t;
}

function renderUrlLinkList(oldList, newList) {
	var oldUrlList = [];
	var newUrlList = [];

	if (oldList && oldList.length > 0) {
		for (var i = 0; i < oldList.length; i++) {
			oldUrlList.push("<a href=\"" + oldList[i].url + "\" target=\"new\">" + oldList[i].name + "</a><br/>");
		}
	}
	if (newList && newList.length > 0) {
		for (var i = 0; i < newList.length; i++) {
			newUrlList.push("<a href=\"" + newList[i].url + "\" target=\"new\">" + newList[i].name + "</a><br/>");
		}
	}

	renderList(oldUrlList, newUrlList, "URL Verweise");
}


// Compare two tables containing objects with 'properties'.
// The result is returned as an object containing the following information:
// { eq:  array of objects that are in table1 and table2,
//   ins: array of objects that are in table2 but not in table1,
//   del: array of objects that are in table1 but not in table2 } 
function compareTable(table1, table2, properties) {
	var diff = {eq:[], ins:[], del:[]};

	// Iterate over all objects in table1 and compare them to the objects in table2
	// If a match is found, add the object to diff.eq
	// Otherwise add it to diff.del
	for (var i in table1) {
		obj1 = table1[i];
		var found = false;

		for (var j in table2) {
			var obj2 = table2[j];
			if (!arrayContains(diff.eq, obj2) && compareObj(obj1, obj2, properties)) {
				diff.eq.push(obj2);
				found = true;
				break;
			}
		}
		if (!found) {
			diff.del.push(obj1);
		}
	}

	// All remaining elements that have not been added to diff.eq or diff.del are new
	// objects and have to be added to diff.ins
	for (var j in table2) {
		var obj = table2[j];
		if (!arrayContains(diff.eq, obj)) {
			diff.ins.push(obj);
		}
	}

	return diff;
}

// Compare two objects according to their properties
function compareObj(obj1, obj2, properties) {
	if (properties == null) {
		return (obj1+"" == obj2+"");
	}
	if (typeof(properties) == "string") {
		properties = [properties];
	}

	for (var i in properties) {
		var prop1 = obj1[properties[i]] || "";
		var prop2 = obj2[properties[i]] || "";
		// Compare as strings so date objects are handled properly
		if (prop1+"" != prop2+"") {
			return false;
		}
	}
	return true;
}

// Returns whether the array 'arr' contains the object 'obj'
function arrayContains(arr, obj) {
	var len = arr.length;
	for (var i = 0; i < len; i++){
		if(arr[i]===obj){return true;}
	}
	return false;
}

</script>
</head>

<body>
  <div dojoType="ContentPane">
    <div id="contentPane" layoutAlign="client" class="contentBlockWhite top">
      <div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=hierarchy-maintenance-3#hierarchy-maintenance-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
  	  </div>
  	  <div id="dialogContent" class="content">
        <!-- MAIN TAB CONTAINER START -->
        <div class="spacer"></div>
      	<div id="compareViews" dojoType="ingrid:TabContainer" doLayout="false" class="full" selectedChild="diffView">
          <!-- MAIN TAB 1 START -->
      		<div id="diffView" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.compare.compare" />">
              <div id="diffContent" class="inputContainer field grey"></div>
              <div id="diffContentLegend" class="inputContainer field grey"><span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.insertedText" /><br/>
			  			<span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.deletedText" /></div>
      		</div>
          <!-- MAIN TAB 1 END -->
      		
          <!-- MAIN TAB 2 START -->
      		<div id="oldView" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.compare.original" />">
              <div id="oldContent" class="inputContainer field grey"></div>
      		</div>
          <!-- MAIN TAB 2 END -->

          <!-- MAIN TAB 3 START -->
      		<div id="currentView" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.compare.modified" />">
              <div id="currentContent" class="inputContainer field grey"></div>
      		</div>
          <!-- MAIN TAB 3 END -->

      	</div>
        <!-- MAIN TAB CONTAINER END -->
  
      </div>
    </div>
  </div>
  <!-- CONTENT END -->
</body>
</html>
