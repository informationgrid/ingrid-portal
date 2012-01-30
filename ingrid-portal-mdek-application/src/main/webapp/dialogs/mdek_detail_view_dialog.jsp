<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<!--<script type="text/javascript" src="js/detail_helper.js"></script>-->
<script type="text/javascript">

var selectedNode = null;

dojo.connect(_container_, "onLoad", function(){
	if (this.customParams.useDirtyData == true) {
		console.debug("use dirty data");
		selectedNode = dijit.byId("dataTree").selectedNode;
		// get dirty data from proxy
		var nodeData = udkDataProxy._getData();
		// extract uuids from all addresses within the object
		var uuids = getAddressUuids(nodeData.generalAddressTable);
		enrichNodeDataWithInstitutions(uuids, nodeData);
		
	} else {
		console.debug("load data");
		// make sure the object forms are already loaded since they are needed
		// for syslist values for example
		ingridObjectLayout.create();
		
		// Construct an MdekDataBean from the available data
		selectedNode = dijit.byId(this.customParams.selectedNode);
		ObjectService.getNodeData(selectedNode.item.id[0], selectedNode.item.nodeAppType[0], "false",
			{
				callback:function(res) {
				    var uuids = getAddressUuids(res.generalAddressTable);
				    enrichNodeDataWithInstitutions(uuids, res);
				    //renderNodeData(res); 
				},
//				timeout:5000,
				errorHandler:function(message) {
					displayErrorMessage(new Error(message));
//					console.debug("Error in mdek_detail_view_dialog.jsp: Error while waiting for nodeData: " + message);
				}
			}
		);
	}
});

function getAddressUuids(addressTable){
    var uuids = new Array();
    for (var i = 0; i < addressTable.length; i++) {
        uuids[i] = addressTable[i].uuid;
    }
    return uuids;
}

function enrichNodeDataWithInstitutions(uuids, nodeData) {
    AddressService.getAddressInstitutions(uuids, {
        callback:function(res){ 
            console.debug("enrich node");
            for (var i = 0; i < res.length; i++) {
                nodeData.generalAddressTable[i].organisation = res[i];
            }
            renderNodeData(nodeData);
        },
        errorHandler:function(message) {
            displayErrorMessage(new Error(message));
        }
    });
}

function renderNodeData(nodeData) {
    var additionalFields = nodeData.additionalFields;
    
	renderObjectTitel(nodeData.objectName);

	renderText(nodeData.generalShortDescription);
	renderTextWithTitle(UtilList.getSelectDisplayValue(dijit.byId("objectClass"), "Class"+nodeData.objectClass), "<fmt:message key='ui.obj.header.objectClass' />");
	renderTextWithTitle(removeEvilTags(nodeData.generalDescription), "<fmt:message key='ui.obj.general.description' />");
	// addresses
	renderAddressList(nodeData.generalAddressTable);
    
    renderAdditionalFieldsForRubric("general", additionalFields);

	// add superior elements
	var superiorElements = renderSuperiorElements(selectedNode.getParent());
	renderList(superiorElements.split("@@"), "<fmt:message key='ui.obj.general.superior.objects' />");//, rowProperty, renderFunction)
	
	// add subordinated elements
	var deferred = renderSubordinatedElements(selectedNode.item);
	deferred.addCallback(function(res) {
		var sortedList = res.split("@@").sort(function(a,b) {return UtilString.compareIgnoreCase(a,b);});
		renderList(sortedList, "<fmt:message key='ui.obj.general.subordinated.objects' />");
	});

	deferred.addCallback(function(res) {
		
		// define date conversion renderer function
		function formatDate(val) {
            if (typeof val == "undefined" || val == null || val == "") {
                return "";
            }
			return dojo.date.locale.format(val, {selector:'date', datePattern:'dd.MM.yyyy'});
		}
	
		// technical domains
		if (nodeData.objectClass == 1) {
			renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Objekt, Karte
			renderTextWithTitle(nodeData.ref1BasisText, "<fmt:message key='ui.obj.type1.technicalBasisTable.title' />");
			renderTextWithTitle(nodeData.ref1ObjectIdentifier, "<fmt:message key='ui.obj.type1.identifier' />");
			renderTextWithTitle(UtilSyslist.getSyslistEntryName(525, nodeData.ref1DataSet), "<fmt:message key='ui.obj.type1.dataset' />");
			renderList(nodeData.ref1Representation, "<fmt:message key='ui.obj.type1.digitalRepresentation' />", null, function(val) { return UtilSyslist.getSyslistEntryName(526, val); });
			renderTextWithTitle(UtilSyslist.getSyslistEntryName(528, nodeData.ref1VFormatTopology), "<fmt:message key='ui.obj.type1.vectorFormat.topology' />");
			renderTable(nodeData.ref1VFormatDetails, ["geometryType", "numElements"], ["<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.geoType' />", "<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.elementCount' />"], "<fmt:message key='ui.obj.type1.vectorFormat.title' />", [function(val) {return UtilSyslist.getSyslistEntryName(515, val);}, null]);
			// NOTICE: moved to general section "Raumbezug"
            //renderTextWithTitle(nodeData.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
			renderTable(nodeData.ref1Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type1.scaleTable.title' />");
			renderTable(nodeData.ref1SymbolsText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.symbolCatTable.header.title' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.date' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.symbolCatTable.title' />", [null, formatDate, null]);
			renderTable(nodeData.ref1KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.keyCatTable.title' />", [null, formatDate, null]);
            renderList(nodeData.ref1Data, "<fmt:message key='ui.obj.type1.attributes' />");
			renderTextWithTitle(nodeData.ref1DataBasisText, "<fmt:message key='ui.obj.type1.dataBasisTable.title' />");
			renderTextWithTitle(nodeData.ref1ProcessText, "<fmt:message key='ui.obj.type1.processTable.title' />");
            
            renderAdditionalFieldsForRubric("refClass1", additionalFields);
            
            // DQ
            renderSectionTitel("<fmt:message key='ui.obj.dq' />");
            renderTextWithTitle(nodeData.ref1Coverage, "<fmt:message key='ui.obj.type1.coverage' />" + " [%]");
            renderTextWithTitle(nodeData.ref1AltAccuracy, "<fmt:message key='ui.obj.type1.sizeAccuracy' />");
            renderTextWithTitle(nodeData.ref1PosAccuracy, "<fmt:message key='ui.obj.type1.posAccuracy' />");
            renderTable(nodeData.dq109Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table109.title' />");
            renderTable(nodeData.dq112Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table112.title' />");
            renderTable(nodeData.dq113Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table113.title' />");
            renderTable(nodeData.dq114Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table114.title' />");
            renderTable(nodeData.dq115Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table115.title' />");
            renderTable(nodeData.dq120Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table120.title' />");
            renderTable(nodeData.dq125Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table125.title' />");
            renderTable(nodeData.dq126Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table126.title' />");
            renderTable(nodeData.dq127Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table127.title' />");
            
            renderAdditionalFieldsForRubric("refClass1DQ", additionalFields);
		} else if (nodeData.objectClass == 2) {
			renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Literature
			renderTextWithTitle(nodeData.ref2Author, "<fmt:message key='ui.obj.type2.author' />");
			renderTextWithTitle(nodeData.ref2Publisher, "<fmt:message key='ui.obj.type2.editor' />");
			renderTextWithTitle(nodeData.ref2PublishedIn, "<fmt:message key='ui.obj.type2.publishedIn' />");
			renderTextWithTitle(nodeData.ref2PublishLocation, "<fmt:message key='ui.obj.type2.publishedLocation' />");
			renderTextWithTitle(nodeData.ref2PublishedInIssue, "<fmt:message key='ui.obj.type2.issue' />");
			renderTextWithTitle(nodeData.ref2PublishedInPages, "<fmt:message key='ui.obj.type2.pages' />");
			renderTextWithTitle(nodeData.ref2PublishedInYear, "<fmt:message key='ui.obj.type2.publishedYear' />");
			renderTextWithTitle(nodeData.ref2PublishedISBN, "<fmt:message key='ui.obj.type2.isbn' />");
			renderTextWithTitle(nodeData.ref2PublishedPublisher, "<fmt:message key='ui.obj.type2.publisher' />");
			renderTextWithTitle(nodeData.ref2LocationText, "<fmt:message key='ui.obj.type2.locationTable.title' />");
			renderTextWithTitle(nodeData.ref2DocumentType, "<fmt:message key='ui.obj.type2.documentType' />");
			renderTextWithTitle(nodeData.ref2BaseDataText, "<fmt:message key='ui.obj.type2.generalDataTable.title' />");
			renderTextWithTitle(nodeData.ref2BibData, "<fmt:message key='ui.obj.type2.additionalBibInfo' />");
			renderTextWithTitle(nodeData.ref2Explanation, "<fmt:message key='ui.obj.type2.description' />");
            
            renderAdditionalFieldsForRubric("refClass2", additionalFields);
		} else if (nodeData.objectClass == 3) {
			renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Geodatendienst
			renderList(nodeData.ref3ServiceTypeTable, "<fmt:message key='ui.obj.type3.ref3ServiceTypeTable.title' />", null, function(val) { return UtilSyslist.getSyslistEntryName(5200, val); });
			renderTextWithTitle(UtilSyslist.getSyslistEntryName(5100, nodeData.ref3ServiceType), "<fmt:message key='ui.obj.type3.serviceType' />");
			renderList(nodeData.ref3ServiceVersion, "<fmt:message key='ui.obj.type3.serviceVersion' />");
			renderTextWithTitle(nodeData.ref3SystemEnv, "<fmt:message key='ui.obj.type3.environment' />");
			renderTextWithTitle(nodeData.ref3History, "<fmt:message key='ui.obj.type3.history' />");
			renderTextWithTitle(nodeData.ref3BaseDataText, "<fmt:message key='ui.obj.type3.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type3.generalDataTable.tab.text' />" + ")");
			renderTextWithTitle(nodeData.ref3Explanation, "<fmt:message key='ui.obj.type3.description' />");
            renderOperations(nodeData.ref3Operation);
			renderTable(nodeData.ref3Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type3.scaleTable.title' />");
            renderTextWithTitle(nodeData.ref3HasAccessConstraint ? "<fmt:message key='general.yes' />": "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.type3.ref3HasAccessConstraint' />");
            
            renderAdditionalFieldsForRubric("refClass3", additionalFields);
		} else if (nodeData.objectClass == 4) {
			renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Vorhaben
			renderTextWithTitle(nodeData.ref4ParticipantsText, "<fmt:message key='ui.obj.type4.participantsTable.title' />");
			renderTextWithTitle(nodeData.ref4PMText, "<fmt:message key='ui.obj.type4.projectManagerTable.title' />");
			renderTextWithTitle(nodeData.ref4Explanation, "<fmt:message key='ui.obj.type4.description' />");
            
            renderAdditionalFieldsForRubric("refClass4", additionalFields);
		} else if (nodeData.objectClass == 5) {
			renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Datensammlung/Datenbank
            renderTable(nodeData.ref5KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type5.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type5.keyCatTable.title' />", [null, formatDate, null]);
			renderTable(nodeData.ref5dbContent, ["parameter", "additionalData"], ["<fmt:message key='ui.obj.type5.contentTable.header.parameter' />", "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />"], "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />");
			renderTextWithTitle(nodeData.ref5MethodText, "<fmt:message key='ui.obj.type5.methodTable.title' />");
			renderTextWithTitle(nodeData.ref5Explanation, "<fmt:message key='ui.obj.type5.description' />");
            
            renderAdditionalFieldsForRubric("refClass5", additionalFields);
		} else if (nodeData.objectClass == 6) {
            renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
            // Dienst/Anwendung/Informationssystem
            renderTextWithTitle(dijit.byId("ref6ServiceType").get("displayedValue"), "<fmt:message key='ui.obj.type6.serviceType' />");
            renderList(nodeData.ref6ServiceVersion, "<fmt:message key='ui.obj.type6.serviceVersion' />");
            renderTextWithTitle(nodeData.ref6SystemEnv, "<fmt:message key='ui.obj.type6.environment' />");
            renderTextWithTitle(nodeData.ref6History, "<fmt:message key='ui.obj.type6.history' />");
            renderTextWithTitle(nodeData.ref6BaseDataText, "<fmt:message key='ui.obj.type6.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type6.generalDataTable.tab.text' />" + ")");
            renderTextWithTitle(nodeData.ref6Explanation, "<fmt:message key='ui.obj.type6.description' />");
            renderTable(nodeData.ref6UrlList, ["name", "url", "urlDescription"], ["<fmt:message key='ui.obj.type6.urlList.header.name' />", "<fmt:message key='ui.obj.type6.urlList.header.url' />", "<fmt:message key='ui.obj.type6.urlList.header.urlDescription' />"], "<fmt:message key='ui.obj.type6.urlList' />");
            
            renderAdditionalFieldsForRubric("refClass6", additionalFields);
        }
		
		// spatial reference
		renderSectionTitel("<fmt:message key='ui.obj.spatial.title' />");
		UtilList.addSNSLocationLabels(nodeData.spatialRefAdminUnitTable);
		renderTable(nodeData.spatialRefAdminUnitTable, ["label", "nativeKey", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoThesTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.nativeKey' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude2' />"], "<fmt:message key='dialog.compare.object.spatialTable.title' />");
		renderTable(nodeData.spatialRefLocationTable, ["name", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude2' />"], "<fmt:message key='ui.obj.spatial.geoTable.title' />");
		
        // NOTICE: moved from class 1 to general "Raumbezug"
        //renderTextWithTitle(nodeData.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
        renderList(nodeData.ref1SpatialSystemTable, "<fmt:message key='ui.obj.type1.spatialSystem' />");
        
        // create cell render functions
		function lookupSpatialRefAltMeasure(val) {
			return UtilSyslist.getSyslistEntryName(102, val);
		}
		function lookupSpatialRefAltVDate(val) {
			return UtilSyslist.getSyslistEntryName(101, val);
		}
		
		var altitudeData = []; // empty list means no rendering!
		if (nodeData.spatialRefAltMin || nodeData.spatialRefAltMax || lookupSpatialRefAltMeasure(nodeData.spatialRefAltMeasure) || lookupSpatialRefAltVDate(nodeData.spatialRefAltVDate) ) {
			altitudeData = [nodeData]; // add nodeData to the list so that it's rendered with values from it
		}
		renderTable(altitudeData, ["spatialRefAltMin", "spatialRefAltMax", "spatialRefAltMeasure", "spatialRefAltVDate"], ["<fmt:message key='ui.obj.spatial.height.min' />", "<fmt:message key='ui.obj.spatial.height.max' />", "<fmt:message key='ui.obj.spatial.height.unit' />", "<fmt:message key='ui.obj.spatial.height.geodeticSystem' />"], "<fmt:message key='ui.obj.spatial.height' />", [null, null, lookupSpatialRefAltMeasure, lookupSpatialRefAltVDate]);
		renderTextWithTitle(nodeData.spatialRefExplanation, "<fmt:message key='ui.obj.spatial.description' />");
	
        renderAdditionalFieldsForRubric("spatialRef", additionalFields);
        
		// temporal reference
		renderSectionTitel("<fmt:message key='ui.obj.time.title' />");
		var timeRefTxt;
		if (nodeData.timeRefDate1) {
			if (nodeData.timeRefType && nodeData.timeRefType == "von") {
				timeRefTxt = "von "+formatDate(nodeData.timeRefDate1)+" bis "+formatDate(nodeData.timeRefDate2);
			} else if (nodeData.timeRefType) {
				timeRefTxt = nodeData.timeRefType+" "+formatDate(nodeData.timeRefDate1);
			}
		}
		
		renderTextWithTitle(timeRefTxt, "<fmt:message key='ui.obj.time.timeRefContent' />");
		renderTextWithTitle(UtilSyslist.getSyslistEntryName(523, nodeData.timeRefStatus), "<fmt:message key='ui.obj.time.state' />");
		renderTextWithTitle(UtilSyslist.getSyslistEntryName(518, nodeData.timeRefPeriodicity), "<fmt:message key='ui.obj.time.periodicity' />");
		if (nodeData.timeRefIntervalNum && nodeData.timeRefIntervalUnit) {
			// Do NOT use selectedResult[...] !!! selectedResult IS NULL !!! only not null if once selected by user interaction !!!  
            // renderTextWithTitle("<fmt:message key='ui.obj.time.interval.each' />" + " "+nodeData.timeRefIntervalNum+" "+dojo.widget.byId("timeRefIntervalUnit").selectedResult[0], "<fmt:message key='ui.obj.time.interval' />"); //_getDisplayValueForValue(nodeData.timeRefIntervalUnit)
            renderTextWithTitle("<fmt:message key='ui.obj.time.interval.each' />" + " "+nodeData.timeRefIntervalNum+" "+dijit.byId("timeRefIntervalUnit").get("displayedValue"), "<fmt:message key='ui.obj.time.interval' />");
		}
		// create cell render functions
		function lookupTimeRefType(val) {
			return UtilSyslist.getSyslistEntryName(502, val);
		}
		renderTable(nodeData.timeRefTable, ["date", "type"], ["<fmt:message key='ui.obj.time.timeRefTable.header.date' />", "<fmt:message key='ui.obj.time.timeRefTable.header.type' />"], "<fmt:message key='ui.obj.time.timeRefTable.title' />", [formatDate, lookupTimeRefType]);
		renderTextWithTitle(nodeData.timeRefExplanation, "<fmt:message key='ui.obj.time.description' />");
        
        renderAdditionalFieldsForRubric("timeRef", additionalFields);
		
		// additional information
		renderSectionTitel("<fmt:message key='ui.obj.additionalInfo.title' />");
		renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeData.extraInfoLangMetaDataCode), "<fmt:message key='ui.obj.additionalInfo.language.metadata' />");
		renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeData.extraInfoLangDataCode), "<fmt:message key='ui.obj.additionalInfo.language.data' />");
		renderTextWithTitle(UtilSyslist.getSyslistEntryName(3571, nodeData.extraInfoPublishArea), "<fmt:message key='ui.obj.additionalInfo.publicationCondition' />");
		renderTextWithTitle(dijit.byId("extraInfoCharSetData").get("displayedValue"), "<fmt:message key='ui.obj.additionalInfo.charSet.data' />");
        // Table is only displayed for object classes 1 and 3
		if (nodeData.objectClass == 1 || nodeData.objectClass == 3) {
			renderTable(nodeData.extraInfoConformityTable, ["specification", "level"],
						["<fmt:message key='ui.obj.additionalInfo.conformityTable.header.specification' />", "<fmt:message key='ui.obj.additionalInfo.conformityTable.header.level' />"],
						"<fmt:message key='ui.obj.additionalInfo.conformityTable.title' />",
						[function(val) { return UtilSyslist.getSyslistEntryName(6005, val); }, function(val) { return UtilSyslist.getSyslistEntryName(6000, val); }]);
		}
		renderList(nodeData.extraInfoXMLExportTable, "<fmt:message key='ui.obj.additionalInfo.xmlExportCriteria' />");
		renderList(nodeData.extraInfoLegalBasicsTable, "<fmt:message key='ui.obj.additionalInfo.legalBasis' />");
		renderTextWithTitle(nodeData.extraInfoPurpose, "<fmt:message key='ui.obj.additionalInfo.purpose' />");
		renderTextWithTitle(nodeData.extraInfoUse, "<fmt:message key='ui.obj.additionalInfo.suitability' />");
        
        renderAdditionalFieldsForRubric("extraInfo", additionalFields);
		
		// availability
		renderSectionTitel("<fmt:message key='ui.obj.availability.title' />");
        renderList(nodeData.availabilityAccessConstraints, "<fmt:message key='ui.obj.availability.accessConstraints' />", null, function(val){
            return UtilSyslist.getSyslistEntryName(6010, val);
        });
        renderTextWithTitle(nodeData.availabilityUseConstraints, "<fmt:message key='ui.obj.availability.useConstraints' />");
        renderTextWithTitle(UtilSyslist.getSyslistEntryName(6300, nodeData.availabilityDataFormatInspire), "<fmt:message key='ui.obj.availability.dataFormatInspire' />");
		renderTable(nodeData.availabilityDataFormatTable, ["name", "version", "compression", "pixelDepth"], ["<fmt:message key='ui.obj.availability.dataFormatTable.header.name' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.version' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.compression' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.depth' />"], "<fmt:message key='ui.obj.availability.dataFormatTable.title' />", [function(val) { return UtilSyslist.getSyslistEntryName(1320, val); }, null, null, null]);
		renderTable(nodeData.availabilityMediaOptionsTable, ["name", "transferSize", "location"], ["<fmt:message key='ui.obj.availability.mediaOptionTable.header.type' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.amount' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.location' />"], "<fmt:message key='ui.obj.availability.mediaOptionTable.title' />", [function(val) { return UtilSyslist.getSyslistEntryName(520, val); }, null, null]);
		renderTextWithTitle(nodeData.availabilityOrderInfo, "<fmt:message key='ui.obj.availability.orderInfo' />");
        
        renderAdditionalFieldsForRubric("availability", additionalFields);
	
		// indexing
		renderSectionTitel("<fmt:message key='ui.obj.thesaurus.title' />");
		var sortedList = nodeData.thesaurusTermsTable.sort(function(a,b) {return UtilString.compareIgnoreCase(a.title,b.title);});
		renderList(sortedList, "<fmt:message key='ui.adr.thesaurus.terms' />", "title");
		sortedList = nodeData.thesaurusTopicsList.sort(function(a,b) {return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(527, a),UtilSyslist.getSyslistEntryName(527, b));}); 
		renderList(sortedList, "<fmt:message key='ui.obj.thesaurus.terms.category' />", null, function (val) { return UtilSyslist.getSyslistEntryName(527, val);});
		sortedList = nodeData.thesaurusInspireTermsList.sort(function(a,b) {return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(6100, a),UtilSyslist.getSyslistEntryName(6100, b));});
		renderList(sortedList, "<fmt:message key='ui.obj.thesaurus.terms.inspire' />", null, function (val) { return UtilSyslist.getSyslistEntryName(6100, val);});
		renderTextWithTitle(nodeData.thesaurusEnvExtRes ? "<fmt:message key='general.yes' />": "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.thesaurus.terms.enviromental.displayCatalogPage' />");
		renderList(nodeData.thesaurusEnvTopicsList, "<fmt:message key='ui.obj.thesaurus.terms.enviromental.title' />"+ " - " + "<fmt:message key='ui.obj.thesaurus.terms.enviromental.topics' />", null, function (val) { return UtilSyslist.getSyslistEntryName(1410, val);});
        
        renderAdditionalFieldsForRubric("thesaurus", additionalFields);
	
		// references
		if (nodeData.linksFromObjectTable.length > 0 || nodeData.linksToObjectTable.length > 0 || nodeData.linksToUrlTable.length > 0) {
	    	renderSectionTitel("<fmt:message key='ui.obj.links.title' />");
	    	sortedList = nodeData.linksFromObjectTable.sort(function(a,b) {return (b.title<a.title)-(a.title<b.title);});
	    	renderList(sortedList, "<fmt:message key='dialog.compare.object.linksFromTable.title' />", "title")
	    	sortedList = nodeData.linksToObjectTable.sort(function(a,b) {return (b.title<a.title)-(a.title<b.title);});
	    	renderList(sortedList, "<fmt:message key='dialog.compare.object.linksToTable.title' />", "title")
	    	renderUrlLinkList(nodeData.linksToUrlTable);
	    }
        
        renderAdditionalFieldsForRubric("links", additionalFields);
        
        renderAdditionalRubrics(additionalFields);
        console.debug(nodeData);
		
		// administrative data
		renderSectionTitel("<fmt:message key='dialog.compare.object.administrative' />");
		renderTextWithTitle(nodeData.uuid, "<fmt:message key='dialog.compare.object.id' />");
		renderTextWithTitle(catalogData.catalogName, "<fmt:message key='dialog.compare.object.catalog' />");
	
		// additional fields
		/*var addFields = nodeData.additionalFields;
		if (addFields.length > 0) {
	    	renderSectionTitel("<fmt:message key='ui.obj.additionalFields.title' />");
	    	for(var i=0; i<addFields.length; i++) {
	    		renderTextWithTitle(addFields[i].value, addFields[i].name);
	    	}
		}*/

		// modification time
		renderText("<fmt:message key='ui.obj.header.modificationTime' />" + ": " + nodeData.modificationTime);
	}); // deferred callback handler
}

function renderAdditionalFieldsForRubric(rubric, nodeData) {
    //console.debug("add additional fields info of rubric: " + rubric);
    var addDomWidgets = dojo.query(".additionalField", rubric);
    dojo.forEach(addDomWidgets, function(domWidget) {
        var widgetId = domWidget.getAttribute("widgetId");
        if (!widgetId) widgetId = domWidget.id;
        var label = searchLabelFrom(domWidget)
        var data = getValueFromAdditional(widgetId, nodeData);
        // if it is a table
        if (data && typeof(data) == "object") {
            var columnFields = getColumnFields(widgetId);
            var columnNames = getColumnNames(widgetId);
            var formatters = getColumnFormatters(widgetId);
            renderTable(data, columnFields, columnNames, label, formatters, true, UtilGrid.getTable(widgetId).getColumns());
        } else {
            renderTextWithTitle(data, label);
        }
    });
}

function getValueFromAdditional(id, additionalFields) {
    var result = null;
    dojo.some(additionalFields, function(field) {
        //console.debug("check: " + id + " with "+field.identifier);
        if (field.identifier === id) {
            // special handling of tables
            if (field.tableRows != null) {
                result = prepareAdditionalTable(field.tableRows);
                return true;
            }
            else {
                result = field.value + getUnitFromField(id);
                return true;
            }
        }
    });
    return result;
}

function getUnitFromField(id) {
    var widget = dijit.byId(id);
    var unit = widget ? widget.domNode.getAttribute("unit") : null;
    if (unit)
        return " " + unit;
    else
        return "";
}

function getColumnFields(id) {
    return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {return col.field;});
}

function getColumnNames(id) {
    return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {return col.name;});
}
function getColumnFormatters(id) {
    return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {return col.formatter;});
}

function prepareAdditionalTable(rows) {
    var tableList = [];
    dojo.forEach(rows, function(row) {
        if (row.length == 0) return; // empty table
        var item = {};
        dojo.forEach(row, function(rowItem) {
            item[rowItem.identifier] = rowItem.value ? rowItem.value : ""; // TODO: listIds? mapping?
        });
        tableList.push(item)
    });
    return tableList;
    
}

function searchLabelFrom(element) {
    while (!dojo.hasClass(element, "input")) {
        element = element.parentNode;
    }
    element = element.previousSibling;
    while (!dojo.hasClass(element, "label")) {
        element = element.previousSibling;
    }
    
    if (element != null) {
        var text = dojo.isFF ? element.textContent : element.innerText;
        if (text[text.length-1] === "*")
            return text.slice(0,-1);
        return text;
    } else 
        return "???";    
}

function renderAdditionalRubrics(additionalFields) {
    var addDomRubrics = dojo.query(".rubric.additional", "contentFrameBodyObject");
    dojo.forEach(addDomRubrics, function(domRubric) {
        var rubricText = dojo.isFF ? domRubric.children[0].textContent : domRubric.children[0].innerText;
        renderSectionTitel(rubricText);
        renderAdditionalFieldsForRubric(domRubric.id, additionalFields);
    });
}

function renderYesNo(val) {
	if (val == 1) {
		return "<fmt:message key='general.yes' />";
	} else {
		return "<fmt:message key='general.no' />";
	}
}

function renderSuperiorElements(node) {
	if (node.id[0] == "objectRoot") {
		return "";
	} else {
		return renderSuperiorElements(node.getParent()) + "@@" + node.item.title[0];
	}
}

function renderSubordinatedElements(node) {
	// don't get children from tree, since children might not be loaded yet
	var children = node.children;

	var deferred = new dojo.Deferred();
	
	TreeService.getAllSubTreeChildren(node.id[0], node.nodeAppType[0], {
        callback:function(res) { deferred.callback(res); },
        errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
        exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
    });

	deferred.addCallback(function(children) {
		var subList = "";
        for (var i=0; i<children.length; i++) {
            //console.debug("Got Children: " + children[i].title);
            subList = children[i].title + "@@" + subList;
        }
        //console.debug("return list: " + subList);
        return subList;
	});

	deferred.addErrback(function(res) { alert("Error while loading data from the server. Please check your connection and try again!"); return "";});
	return deferred;
}

function renderOperations(list) {
	renderTable(list, ["name", "addressList"], ["<fmt:message key='ui.obj.type3.operationTable.header.name' />", "<fmt:message key='ui.obj.type3.operationTable.header.address' />"], "<fmt:message key='ui.obj.type3.operationTable.title' />", [null, renderFirstElement]);
	for(var i=0; i<list.length; i++) {
		var op = list[i];
        renderTitle("Operation " + op.name);
		renderText(op.description);
        renderList(op.addressList, "Zugriffsadressen");
		renderList(op.platform, "unterst&uuml;tzte Plattformen");
		renderTextWithTitle(op.methodCall, "Aufruf");
		renderTable(op.paramList, ["name", "direction", "description", "optional", "multiple"], ["Name", "Richtung", "Beschreibung", "Optional", "Mehrfacheingabe"], "Parameter", [null, null, null, renderYesNo, renderYesNo]);
		renderList(op.dependencies, "Abh&auml;ngigkeiten");
	}
}

function renderUrlLinkList(list) {
	if (list && list.length > 0) {
		var t = "<p><strong>URL Verweise</strong><br/>";
		for (var i = 0; i < list.length; i++) {
			t += "<a href=\"" + list[i].url + "\" target=\"new\">" + list[i].name + "</a><br/>";
		}
		dojo.byId("detailViewContent").innerHTML += t + "<br/><br/>";
	}
}


function renderObjectTitel(val) {
	dojo.byId("detailViewContent").innerHTML += "<h1>" + val + "</h1><br/><br/>";
}

function renderSectionTitel(val) {
	dojo.byId("detailViewContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
}

function renderTextWithTitle(val, title) {
	// compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
    if (val == null) val = "";
    val += "";
	if (detailHelper.isValid(val)) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		if (detailHelper.isValid(title)) {
			dojo.byId("detailViewContent").innerHTML += "<p><strong>" + title + "</strong><br/>" + val + "</p><br/>";
		} else {
			dojo.byId("detailViewContent").innerHTML += "<p>" + val + "</p><br/>";
		}
	}
}

function renderTitle(title) {
    if (detailHelper.isValid(title)) {
        dojo.byId("detailViewContent").innerHTML += "<p><strong>" + title + "</strong><br/></p><br/>";
    }
}

function removeEvilTags(val) {
	if (val) {
		return val.replace(/<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/gi, '');
	} else {
		return "";
	}
}

function renderAddressList(list) {
	if (list.length > 0) {
		var t = "";
		for (var i = 0; i < list.length; i++) {
			t += "<strong>" + list[i].nameOfRelation+ "</strong><br/><br/>";
			t += "<p>";
			t += detailHelper.renderAddressEntry(list[i]).replace(/\n/g, '<br />');
			t += "</p><br/>";
		}
		dojo.byId("detailViewContent").innerHTML += t;
	}
}

function renderText(val) {
	// compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
    if (val == null) val = "";
    val += "";
	if (val && val.length>0) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		dojo.byId("detailViewContent").innerHTML += "<p>" + val + "</p><br/>";
	}
}

function renderList(list, title, rowProperty, renderFunction) {
	if (list && list.length > 0) {
		var t = "<p>";
		if (detailHelper.isValid(title)) {
			t += "<strong>" + title + "</strong><br/>";
		}
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
		if (valList != "") {
			dojo.byId("detailViewContent").innerHTML += t + valList + "</p><br/>";
		}
	}	
}

function renderTable(list, rowProperties, listHeader, title, cellRenderFunction, useGridFormatter, columns) {
	if (list && list.length > 0) {
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
		t += "<tbody>";
		for (var i=0; i<list.length; i++) {
			if (i % 2) {
				t += "<tr class=\"alt\">";
			} else {
				t += "<tr>";
			}
			for (var j=0; j<rowProperties.length; j++) {
                var value = list[i][rowProperties[j]] ? list[i][rowProperties[j]] : "";
				if (cellRenderFunction && cellRenderFunction[j]) {
                    if (useGridFormatter)
                        t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, i, j, value, columns[j])+"</td>";
                    else
					    t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, value)+"</td>";
				} else {
					t += "<td style=\"padding-right:4px\">"+value+"</td>";
				}
			}
			t += "</tr>";
			
		}
		t += "</tbody></table></p>";
		dojo.byId("detailViewContent").innerHTML += t + "<br/><br/>";
	}
}

function renderFirstElement(val) {
    var retVal = "";
    if (val && dojo.isArray(val) && val.length > 0) {
        retVal = val[0];
    }
    return retVal;
}

</script>
</head>

<body>
    <div id="contentPane" layoutAlign="client" class="contentBlockWhite" style="width:700px;">
      <div id="winNavi">
        <a href="javascript:printDivContent('detailViewContent')" title="<fmt:message key="dialog.detail.print" />">[<fmt:message key="dialog.detail.print" />]</a>
  	  </div>
  	  <div id="dialogContent" class="content">
        <!-- MAIN TAB CONTAINER START -->
        <div class="spacer"></div>
      	<div id="detailViewContainer" dojoType="dijit.layout.TabContainer" style="height:528px;" selectedChild="detailView">
          <!-- MAIN TAB 1 START -->
      		<div id="detailView" dojoType="dijit.layout.ContentPane" class="blueTopBorder" style="width:500px;" title="<fmt:message key="dialog.detail.title" />">
              <div id="detailViewContent" class="detailViewContentContainer grey"></div>
      		</div>
          <!-- MAIN TAB 1 END -->
      	</div>
        <!-- MAIN TAB CONTAINER END -->
      </div>
  </div>
  <!-- CONTENT END -->
</body>
</html>
