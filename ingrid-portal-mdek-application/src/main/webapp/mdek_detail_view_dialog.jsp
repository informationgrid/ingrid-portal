<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript" src="js/detail_helper.js"></script>
<script type="text/javascript">
_container_.addOnLoad(function() {
	if (_container_.customParams.useDirtyData == true) {
		// get dirty data from proxy
		var nodeData = udkDataProxy._getData();
		// extract uuids from all addresses within the object
		var uuids = getAddressUuids(nodeData.generalAddressTable);
		enrichNodeDataWithInstitutions(uuids, nodeData);
		
	} else {
		// Construct an MdekDataBean from the available data
		var node = dojo.widget.byId(_container_.customParams.selectedNodeId);
		ObjectService.getNodeData(node.id, node.nodeAppType, "false",
			{
				callback:function(res) {
				    var uuids = getAddressUuids(res.generalAddressTable);
				    enrichNodeDataWithInstitutions(uuids, res);
				    //renderNodeData(res); 
				},
//				timeout:5000,
				errorHandler:function(message) {
					displayErrorMessage(new Error(message));
//					dojo.debug("Error in mdek_detail_view_dialog.jsp: Error while waiting for nodeData: " + message);
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
                dojo.debug("enrich node");
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
//	dojo.debugShallow(nodeData);
	renderObjectTitel(nodeData.objectName);

	renderText(nodeData.generalShortDescription);
	renderTextWithTitle(dojo.widget.byId("objectClass")._getDisplayValueForValue("Class"+nodeData.objectClass), message.get("ui.obj.header.objectClass"));
	renderTextWithTitle(removeEvilTags(nodeData.generalDescription), message.get("ui.obj.general.description"));
	// addresses
	renderAddressList(nodeData.generalAddressTable);
	
	// define date conversion renderer function
	function formatDate(val) {
		return dojo.date.format(val, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
	}

	// technical domains
	if (nodeData.objectClass == 1) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Objekt, Karte
		renderTextWithTitle(nodeData.ref1BasisText, message.get("ui.obj.type1.technicalBasisTable.title"));
		renderTextWithTitle(nodeData.ref1ObjectIdentifier, message.get("ui.obj.type1.identifier"));
		renderTextWithTitle(dojo.widget.byId("ref1DataSet")._getDisplayValueForValue(nodeData.ref1DataSet), message.get("ui.obj.type1.dataset"));
		renderList(nodeData.ref1Representation, message.get("ui.obj.type1.digitalRepresentation"), null, function(val) { return dojo.widget.byId("ref1RepresentationCombobox")._getDisplayValueForValue(val); });
		renderTextWithTitle(nodeData.ref1Coverage, message.get("ui.obj.type1.coverage") + " [%]");
		renderTextWithTitle(dojo.widget.byId("ref1VFormatTopology")._getDisplayValueForValue(nodeData.ref1VFormatTopology), message.get("ui.obj.type1.vectorFormat.topology"));
		renderTable(nodeData.ref1VFormatDetails, ["geometryType", "numElements"], [message.get("ui.obj.type1.vectorFormat.detailsTable.header.geoType"), message.get("ui.obj.type1.vectorFormat.detailsTable.header.elementCount")], message.get("ui.obj.type1.vectorFormat.title"), [function(val) {return dojo.widget.byId("geometryTypeEditor")._getDisplayValueForValue(val);}, null]);
		renderTextWithTitle(nodeData.ref1SpatialSystem, message.get("ui.obj.type1.spatialSystem"));
		renderTable(nodeData.ref1Scale, ["scale", "groundResolution", "scanResolution"], [message.get("ui.obj.type1.scaleTable.header.scale"), message.get("ui.obj.type1.scaleTable.header.groundResolution"), message.get("ui.obj.type1.scaleTable.header.scanResolution")], message.get("ui.obj.type1.scaleTable.title"));
		renderTextWithTitle(nodeData.ref1AltAccuracy, message.get("ui.obj.type1.sizeAccuracy"));
		renderTextWithTitle(nodeData.ref1PosAccuracy, message.get("ui.obj.type1.posAccuracy"));
		renderTable(nodeData.ref1SymbolsText, ["title", "date", "version"], [message.get("ui.obj.type1.symbolCatTable.header.title"), message.get("ui.obj.type1.symbolCatTable.header.date"), message.get("ui.obj.type1.symbolCatTable.header.version")], message.get("ui.obj.type1.symbolCatTable.title"), [null, formatDate, null]);
		renderTable(nodeData.ref1KeysText, ["title", "date", "version"], [message.get("ui.obj.type1.keyCatTable.header.title"), message.get("ui.obj.type1.keyCatTable.header.date"), message.get("ui.obj.type1.keyCatTable.header.version")], message.get("ui.obj.type1.keyCatTable.title"), [null, formatDate, null]);
		renderTextWithTitle(nodeData.ref1DataBasisText, message.get("ui.obj.type1.dataBasisTable.title"));
		renderList(nodeData.ref1Data, message.get("ui.obj.type1.attributes"));
		renderTextWithTitle(nodeData.ref1ProcessText, message.get("ui.obj.type1.processTable.title"));
	} else if (nodeData.objectClass == 2) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Literature
		renderTextWithTitle(nodeData.ref2Author, message.get("ui.obj.type2.author"));
		renderTextWithTitle(nodeData.ref2Publisher, message.get("ui.obj.type2.editor"));
		renderTextWithTitle(nodeData.ref2PublishedIn, message.get("ui.obj.type2.publishedIn"));
		renderTextWithTitle(nodeData.ref2PublishLocation, message.get("ui.obj.type2.publishedLocation"));
		renderTextWithTitle(nodeData.ref2PublishedInIssue, message.get("ui.obj.type2.issue"));
		renderTextWithTitle(nodeData.ref2PublishedInPages, message.get("ui.obj.type2.pages"));
		renderTextWithTitle(nodeData.ref2PublishedInYear, message.get("ui.obj.type2.publishedYear"));
		renderTextWithTitle(nodeData.ref2PublishedISBN, message.get("ui.obj.type2.isbn"));
		renderTextWithTitle(nodeData.ref2PublishedPublisher, message.get("ui.obj.type2.publisher"));
		renderTextWithTitle(nodeData.ref2LocationText, message.get("ui.obj.type2.locationTable.title"));
		renderTextWithTitle(nodeData.ref2DocumentType, message.get("ui.obj.type2.documentType"));
		renderTextWithTitle(nodeData.ref2BaseDataText, message.get("ui.obj.type2.generalDataTable.title"));
		renderTextWithTitle(nodeData.ref2BibData, message.get("ui.obj.type2.additionalBibInfo"));
		renderTextWithTitle(nodeData.ref2Explanation, message.get("ui.obj.type2.description"));
	} else if (nodeData.objectClass == 3) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Dienst/Anwendung/Informationssystem
		renderList(nodeData.ref3ServiceTypeTable, message.get("ui.obj.type3.ref3ServiceTypeTable.title"), null, function(val) { return dojo.widget.byId("ref3ServiceTypeEditor")._getDisplayValueForValue(val); });
		renderTextWithTitle(dojo.widget.byId("ref3ServiceType")._getDisplayValueForValue(nodeData.ref3ServiceType), message.get("ui.obj.type3.ref3ServiceTypeTable.title"));
		renderList(nodeData.ref3ServiceVersion, message.get("ui.obj.type3.serviceVersion"));
		renderTextWithTitle(nodeData.ref3SystemEnv, message.get("ui.obj.type3.environment"));
		renderTextWithTitle(nodeData.ref3History, message.get("ui.obj.type3.history"));
		renderTextWithTitle(nodeData.ref3BaseDataText, message.get("ui.obj.type3.generalDataTable.title") + " (" + message.get("ui.obj.type3.generalDataTable.tab.text") + ")");
		renderTextWithTitle(nodeData.ref3Explanation, message.get("ui.obj.type3.description"));
		renderTable(nodeData.ref3Scale, ["scale", "groundResolution", "scanResolution"], [message.get("ui.obj.type1.scaleTable.header.scale"), message.get("ui.obj.type1.scaleTable.header.groundResolution"), message.get("ui.obj.type1.scaleTable.header.scanResolution")], message.get("ui.obj.type3.scaleTable.title"));
		renderOperations(nodeData.ref3Operation);
	} else if (nodeData.objectClass == 4) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Vorhaben
		renderTextWithTitle(nodeData.ref4ParticipantsText, message.get("ui.obj.type4.participantsTable.title"));
		renderTextWithTitle(nodeData.ref4PMText, message.get("ui.obj.type4.projectManagerTable.title"));
		renderTextWithTitle(nodeData.ref4Explanation, message.get("Erl&auml;uterungen"));
	} else if (nodeData.objectClass == 5) {
		renderSectionTitel(message.get("ui.obj.relevance"));
		// Datensammlung/Datenbank
		renderTable(nodeData.ref5dbContent, ["parameter", "additionalData"], [message.get("ui.obj.type5.contentTable.header.parameter"), message.get("ui.obj.type5.contentTable.header.additionalData")], message.get("ui.obj.type5.contentTable.header.additionalData"));
		renderTextWithTitle(nodeData.ref5MethodText, message.get("ui.obj.type5.methodTable.title"));
		renderTextWithTitle(nodeData.ref5Explanation, message.get("ui.obj.type5.description"));
	}
	
	// spatial reference
	renderSectionTitel(message.get("ui.obj.spatial.title"));
	UtilList.addSNSLocationLabels(nodeData.spatialRefAdminUnitTable);
	renderTable(nodeData.spatialRefAdminUnitTable, ["label", "nativeKey", "longitude1", "latitude1", "longitude2", "latitude2"], [message.get("ui.obj.spatial.geoThesTable.header.name"), message.get("ui.obj.spatial.geoThesTable.header.nativeKey"), message.get("ui.obj.spatial.geoThesTable.header.longitude1"), message.get("ui.obj.spatial.geoThesTable.header.latitude1"), message.get("ui.obj.spatial.geoThesTable.header.longitude2"), message.get("ui.obj.spatial.geoThesTable.header.latitude2")], message.get("dialog.compare.object.spatialTable.title"));
	renderTable(nodeData.spatialRefLocationTable, ["name", "longitude1", "latitude1", "longitude2", "latitude2"], [message.get("ui.obj.spatial.geoTable.header.name"), message.get("ui.obj.spatial.geoTable.header.longitude1"), message.get("ui.obj.spatial.geoTable.header.latitude1"), message.get("ui.obj.spatial.geoTable.header.longitude2"), message.get("ui.obj.spatial.geoTable.header.latitude2")], message.get("ui.obj.spatial.geoTable.title"));
	// create cell render functions
	function lookupSpatialRefAltMeasure(val) {
		return dojo.widget.byId("spatialRefAltMeasure")._getDisplayValueForValue(val);
	}
	function lookupSpatialRefAltVDate(val) {
		return dojo.widget.byId("spatialRefAltVDate")._getDisplayValueForValue(val);
	}
	
	var altitudeData = []; // empty list means no rendering!
	if (nodeData.spatialRefAltMin || nodeData.spatialRefAltMax || lookupSpatialRefAltMeasure(nodeData.spatialRefAltMeasure) || lookupSpatialRefAltVDate(nodeData.spatialRefAltVDate) ) {
		altitudeData = [nodeData]; // add nodeData to the list so that it's rendered with values from it
	}
	renderTable(altitudeData, ["spatialRefAltMin", "spatialRefAltMax", "spatialRefAltMeasure", "spatialRefAltVDate"], [message.get("ui.obj.spatial.height.min"), message.get("ui.obj.spatial.height.max"), message.get("ui.obj.spatial.height.unit"), message.get("ui.obj.spatial.height.geodeticSystem")], message.get("ui.obj.spatial.height"), [null, null, lookupSpatialRefAltMeasure, lookupSpatialRefAltVDate]);
	renderTextWithTitle(nodeData.spatialRefExplanation, message.get("ui.obj.spatial.description"));

	// temporal reference
	renderSectionTitel(message.get("ui.obj.time.title"));
	var timeRefTxt;
	if (nodeData.timeRefDate1) {
		if (nodeData.timeRefType && nodeData.timeRefType == "von") {
			timeRefTxt = "von "+dojo.date.format(nodeData.timeRefDate1, {selector:'dateOnly', datePattern:'dd.MM.yyyy'})+" bis "+dojo.date.format(nodeData.timeRefDate2, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
		} else if (nodeData.timeRefType) {
			timeRefTxt = nodeData.timeRefType+" "+dojo.date.format(nodeData.timeRefDate1, {selector:'dateOnly', datePattern:'dd.MM.yyyy'});
		}
	}
	
	renderTextWithTitle(timeRefTxt, message.get("ui.obj.time.timeRefContent"));
	renderTextWithTitle(dojo.widget.byId("timeRefStatus")._getDisplayValueForValue(nodeData.timeRefStatus), message.get("ui.obj.time.state"));
	renderTextWithTitle(dojo.widget.byId("timeRefPeriodicity")._getDisplayValueForValue(nodeData.timeRefPeriodicity), message.get("ui.obj.time.periodicity"));
	if (nodeData.timeRefIntervalNum && nodeData.timeRefIntervalUnit) {
		renderTextWithTitle(message.get("ui.obj.time.interval.each") + " "+nodeData.timeRefIntervalNum+" "+dojo.widget.byId("timeRefIntervalUnit")._getDisplayValueForValue(nodeData.timeRefIntervalUnit), message.get("ui.obj.time.interval"));
	}
	// create cell render functions
	function lookupTimeRefType(val) {
		return dojo.widget.byId("timeRefTypeCombobox")._getDisplayValueForValue(val);
	}
	renderTable(nodeData.timeRefTable, ["date", "type"], [message.get("ui.obj.time.timeRefTable.header.date"), message.get("ui.obj.time.timeRefTable.header.type")], message.get("ui.obj.time.timeRefTable.title"), [formatDate, lookupTimeRefType]);
	renderTextWithTitle(nodeData.timeRefExplanation, message.get("ui.obj.time.description"));
	
	// additional information
	renderSectionTitel(message.get("ui.obj.additionalInfo.title"));
	renderTextWithTitle(dojo.widget.byId("extraInfoLangMetaData")._getDisplayValueForValue(nodeData.extraInfoLangMetaDataCode), message.get("ui.obj.additionalInfo.language.metadata"));
	renderTextWithTitle(dojo.widget.byId("extraInfoLangData")._getDisplayValueForValue(nodeData.extraInfoLangDataCode), message.get("ui.obj.additionalInfo.language.data"));
	renderTextWithTitle(dojo.widget.byId("extraInfoPublishArea")._getDisplayValueForValue(nodeData.extraInfoPublishArea), message.get("ui.obj.additionalInfo.publicationCondition"));
	// Table is only displayed for object classes 1 and 3
	if (nodeData.objectClass == 1 || nodeData.objectClass == 3) {
		renderTable(nodeData.extraInfoConformityTable, ["level", "specification", "date"],
					[message.get("ui.obj.additionalInfo.conformityTable.header.level"), message.get("ui.obj.additionalInfo.conformityTable.header.specification"), message.get("ui.obj.additionalInfo.conformityTable.header.date")],
					message.get("ui.obj.additionalInfo.conformityTable.title"),
					[function(val) { return dojo.widget.byId("extraInfoConformityLevelEditor")._getDisplayValueForValue(val); }, null, formatDate]);
	}
	renderList(nodeData.extraInfoXMLExportTable, message.get("ui.obj.additionalInfo.xmlExportCriteria"));
	renderList(nodeData.extraInfoLegalBasicsTable, message.get("ui.obj.additionalInfo.legalBasis"));
	renderTextWithTitle(nodeData.extraInfoPurpose, message.get("ui.obj.additionalInfo.purpose"));
	renderTextWithTitle(nodeData.extraInfoUse, message.get("ui.obj.additionalInfo.suitability"));
	
	// availability
	renderSectionTitel(message.get("ui.obj.availability.title"));
	renderTable(nodeData.availabilityUsageLimitationTable, ["limit", "requirement"],
					[message.get("ui.obj.availability.usageLimitationTable.header.limit"), message.get("ui.obj.availability.usageLimitationTable.header.requirement")],
					message.get("ui.obj.availability.usageLimitationTable.title"),
					[function(val) { return dojo.widget.byId("availabilityUsageLimitationLimitEditor")._getDisplayValueForValue(val); }, null]);

	renderTable(nodeData.availabilityDataFormatTable, ["name", "version", "compression", "pixelDepth"], [message.get("ui.obj.availability.dataFormatTable.header.name"), message.get("ui.obj.availability.dataFormatTable.header.version"), message.get("ui.obj.availability.dataFormatTable.header.compression"), message.get("ui.obj.availability.dataFormatTable.header.depth")], message.get("ui.obj.availability.dataFormatTable.title"));
	renderTable(nodeData.availabilityMediaOptionsTable, ["name", "transferSize", "location"], [message.get("ui.obj.availability.mediaOptionTable.header.type"), message.get("ui.obj.availability.mediaOptionTable.header.amount"), message.get("ui.obj.availability.mediaOptionTable.header.location")], message.get("ui.obj.availability.mediaOptionTable.title"), [function(val) { return dojo.widget.byId("availabilityMediaOptionsMediumCombobox")._getDisplayValueForValue(val); }, null, null]);
	renderTextWithTitle(nodeData.availabilityOrderInfo, message.get("ui.obj.availability.orderInfo"));

	// indexing
	renderSectionTitel(message.get("ui.obj.thesaurus.title"));
	renderList(nodeData.thesaurusTermsTable, message.get("ui.adr.thesaurus.terms"), "title");
	renderList(nodeData.thesaurusTopicsList, message.get("ui.obj.thesaurus.terms.category"), null, function (val) { return dojo.widget.byId("thesaurusTopicsCombobox")._getDisplayValueForValue(val);});
	renderList(nodeData.thesaurusInspireTermsList, message.get("ui.obj.thesaurus.terms.inspire"), null, function (val) { return dojo.widget.byId("thesaurusInspireCombobox")._getDisplayValueForValue(val);});
	renderTextWithTitle(nodeData.thesaurusEnvExtRes ? message.get("general.yes"): message.get("general.no"), message.get("ui.obj.thesaurus.terms.enviromental.displayCatalogPage"));
	renderList(nodeData.thesaurusEnvTopicsList, message.get("ui.obj.thesaurus.terms.enviromental.title")+ " - " + message.get("ui.obj.thesaurus.terms.enviromental.topics"), null, function (val) { return dojo.widget.byId("thesaurusEnvTopicsCombobox")._getDisplayValueForValue(val);});
	renderList(nodeData.thesaurusEnvCatsList, message.get("ui.obj.thesaurus.terms.enviromental.title")+ " - " + message.get("ui.obj.thesaurus.terms.enviromental.categories"), null, function (val) { return dojo.widget.byId("thesaurusEnvCatsCombobox")._getDisplayValueForValue(val);});

	// references
	if (nodeData.linksFromObjectTable.length > 0 || nodeData.linksToObjectTable.length > 0 || nodeData.linksToUrlTable.length > 0) {
    	renderSectionTitel(message.get("ui.obj.links.title"));
    	renderList(nodeData.linksFromObjectTable, message.get("dialog.compare.object.linksFromTable.title"), "title")
    	renderList(nodeData.linksToObjectTable, message.get("dialog.compare.object.linksToTable.title"), "title")
    	renderUrlLinkList(nodeData.linksToUrlTable);
    }
	
	// administrative data
	renderSectionTitel(message.get("dialog.compare.object.administrative"));
	renderTextWithTitle(nodeData.uuid, message.get("dialog.compare.object.id"));
	renderTextWithTitle(catalogData.catalogName, message.get("dialog.compare.object.catalog"));

	// additional fields
	var addFields = nodeData.additionalFields;
	if (addFields.length > 0) {
    	renderSectionTitel(message.get("ui.obj.additionalFields.title"));
    	for(var i=0; i<addFields.length; i++) {
    		renderTextWithTitle(addFields[i].value, addFields[i].name);
    	}
	}
}

function renderYesNo(val) {
	if (val == 1) {
		return message.get("general.yes");
	} else {
		return message.get("general.no");
	}
}

function renderOperations(list) {
	renderTable(list, ["name", "description"], [message.get("ui.obj.type3.operationTable.header.name"), message.get("ui.obj.type3.operationTable.header.name")], message.get("ui.obj.type3.operationTable.title"));
	for(var i=0; i<list.length; i++) {
		var op = list[i];
		renderTextWithTitle(op.description, "Operation " + op.name);
		renderList(op.platform, "unterst&uuml;tzte Plattformen");
		renderTextWithTitle(op.methodCall, "Aufruf");
		renderTable(op.paramList, ["name", "direction", "description", "optional", "multiple"], ["Name", "Richtung", "Beschreibung", "Optional", "Mehrfacheingabe"], "Parameter", [null, null, null, renderYesNo, renderYesNo]);
		renderList(op.addressList, "Zugriffsadressen");
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
		dojo.byId("detailViewContent").innerHTML += t + "<br/><br/>";
	}
}

function renderText(val) {
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

function renderTable(list, rowProperties, listHeader, title, cellRenderFunction) {
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
				if (cellRenderFunction && cellRenderFunction[j]) {
					t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, list[i][rowProperties[j]])+"</td>";
				} else {
					t += "<td style=\"padding-right:4px\">"+list[i][rowProperties[j]]+"</td>";
				}
			}
			t += "</tr>";
			
		}
		t += "</tbody></table></p>";
		dojo.byId("detailViewContent").innerHTML += t + "<br/><br/>";
	}
}

</script>
</head>

<body>
  <div dojoType="ContentPane">
    <div id="contentPane" layoutAlign="client" class="contentBlockWhite top">
      <div id="winNavi">
        <a href="javascript:printDivContent('detailViewContent')" title="Drucken">[<fmt:message key="dialog.detail.print" />]</a>
  	  </div>
  	  <div id="dialogContent" class="content">
        <!-- MAIN TAB CONTAINER START -->
        <div class="spacer"></div>
      	<div id="detailViewContainer" dojoType="ingrid:TabContainer" doLayout="false" class="full" selectedChild="detailView">
          <!-- MAIN TAB 1 START -->
      		<div id="detailView" dojoType="ContentPane" class="blueTopBorder h500" label="<fmt:message key="dialog.detail.title" />">
              <div id="detailViewContent" class="detailViewContentContainer w652 grey"></div>
      		</div>
          <!-- MAIN TAB 1 END -->
      	</div>
        <!-- MAIN TAB CONTAINER END -->
      </div>
    </div>
  </div>
  <!-- CONTENT END -->
</body>
</html>
