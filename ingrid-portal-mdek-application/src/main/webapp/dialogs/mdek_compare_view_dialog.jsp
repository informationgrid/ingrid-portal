<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
	<script type="text/javascript">
// var pageCompareViewObjects = _container_;

require([
    "dojo/on",
    "dojo/query",
    "dijit/registry",
    "dojo/dom",
    "dojo/topic",
    "dojo/dom-class",
    "dojo/_base/array",
    "ingrid/IgeActions",
    "ingrid/IgeEvents",
    "ingrid/hierarchy/detail_helper",
    "ingrid/utils/String",
    "ingrid/utils/Syslist",
    "ingrid/utils/List",
    "ingrid/utils/Grid",
    "ingrid/utils/Address",
    "ingrid/utils/Catalog",
    "ingrid/message"
], function(on, query, registry, dom, topic, domClass, array, IgeActions, IgeEvents, detailHelper, UtilString, UtilSyslist, UtilList, UtilGrid, UtilAddress, UtilCatalog, message) {


        on(_container_, "Load", function() {

            var nodeDataNew = IgeActions._getData();
            var nodeUuid = this.customParams.selectedNodeId;

            ObjectService.getPublishedNodeData(nodeUuid, {
                callback: function(res) {
                    renderNodeData(res, nodeDataNew);

	                console.log("Publishing event: '/afterInitDialog/ObjectCompare'");
    	            topic.publish("/afterInitDialog/ObjectCompare");
                },
                errorHandler: function(message) {
                    console.debug("Error in mdek_compare_view_dialog.jsp: Error while waiting for published nodeData: " + message);
                    displayErrorMessage(message);
                }
            });
        });

        function renderNodeData(nodeDataOld, nodeDataNew) {
            var oldAdditionalFields = nodeDataOld.additionalFields;
            var newAdditionalFields = nodeDataNew.additionalFields;

            // define date conversion renderer function
            function formatDate(val) {
                if (typeof val == "undefined" || val === null || val === "") {
                    return "";
                }
                return dojo.date.locale.format(val, {
                    selector: "date",
                    datePattern: "dd.MM.yyyy"
                });
            }

            renderTitel(nodeDataOld.objectName, nodeDataNew.objectName);

            renderText(nodeDataOld.generalShortDescription, nodeDataNew.generalShortDescription);
            renderTextWithTitle(UtilList.getSelectDisplayValue(registry.byId("objectClass"), "Class" + nodeDataOld.objectClass),
                UtilList.getSelectDisplayValue(registry.byId("objectClass"), "Class" + nodeDataNew.objectClass), "<fmt:message key='ui.obj.header.objectClass' />");
            renderTextWithTitle(nodeDataOld.generalDescription, nodeDataNew.generalDescription, "<fmt:message key='ui.obj.general.description' />");

            UtilAddress.addAddressTitles(nodeDataOld.generalAddressTable);
            UtilList.addAddressLinkLabels(nodeDataOld.generalAddressTable);
            renderTable(nodeDataOld.generalAddressTable, nodeDataNew.generalAddressTable, ["nameOfRelation", "linkLabel"], ["<fmt:message key='dialog.compare.object.addressLink.type' />", "<fmt:message key='dialog.compare.object.addressLink.title' />"], message.get("ui.obj.general.addressTable.title"));
            renderList(nodeDataOld.thesaurusInspireTermsList, nodeDataNew.thesaurusInspireTermsList, "<fmt:message key='ui.obj.thesaurus.terms.inspire' />", null, function(val) {
                return UtilSyslist.getSyslistEntryName(6100, val);
            });

            // preview image
            var previewImageUrlOld = IgeEvents._filterPreviewImage(nodeDataOld.linksToUrlTable);
            var previewImageUrlNew = IgeEvents._filterPreviewImage(nodeDataNew.linksToUrlTable);
            renderTextWithTitle(previewImageUrlOld, previewImageUrlNew, "<fmt:message key='ui.obj.general.previewImage' />");

            renderTextWithTitle(nodeDataOld.inspireRelevant ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", nodeDataNew.inspireRelevant ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.general.inspireRelevant' />");
            renderTextWithTitle(nodeDataOld.openData ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", nodeDataNew.openData ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.general.openData' />");
            renderList(nodeDataOld.openDataCategories, nodeDataNew.openDataCategories, "<fmt:message key='ui.obj.general.categoriesOpenData' />");

            renderAdditionalFieldsForRubric("general", oldAdditionalFields, newAdditionalFields);

            // technical domains
            if (nodeDataOld.objectClass != nodeDataNew.objectClass) {
                // TODO Don't display class specific data when the objects have different classes?
            }

            if (nodeDataNew.objectClass == 1) {
                renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                // Objekt, Karte
                renderTextWithTitle(nodeDataOld.ref1BasisText, nodeDataNew.ref1BasisText, "<fmt:message key='ui.obj.type1.technicalBasisTable.title' />");
                renderTextWithTitle(nodeDataOld.ref1ObjectIdentifier, nodeDataNew.ref1ObjectIdentifier, "<fmt:message key='ui.obj.type1.identifier' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(525, nodeDataOld.ref1DataSet), UtilSyslist.getSyslistEntryName(525, nodeDataNew.ref1DataSet), "<fmt:message key='ui.obj.type1.dataset' />");
                renderList(nodeDataOld.ref1Representation, nodeDataNew.ref1Representation, "<fmt:message key='ui.obj.type1.digitalRepresentation' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(526, val);
                });
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(528, nodeDataOld.ref1VFormatTopology), UtilSyslist.getSyslistEntryName(528, nodeDataNew.ref1VFormatTopology), "<fmt:message key='ui.obj.type1.vectorFormat.topology' />");
                renderTable(nodeDataOld.ref1VFormatDetails, nodeDataNew.ref1VFormatDetails, ["geometryType", "numElements"], ["<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.geoType' />", "<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.elementCount' />"], "<fmt:message key='ui.obj.type1.vectorFormat.title' />", [

                    function(val) {
                        return UtilSyslist.getSyslistEntryName(515, val);
                    },
                    null
                ]);
                // moved to general "Raumbezug" section, class independent !
                //renderTextWithTitle(nodeDataOld.ref1SpatialSystem, nodeDataNew.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
                renderTable(nodeDataOld.ref1Scale, nodeDataNew.ref1Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type1.scaleTable.title' />");
                renderTable(nodeDataOld.ref1SymbolsText, nodeDataNew.ref1SymbolsText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.symbolCatTable.header.title' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.date' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.symbolCatTable.title' />", [null, formatDate, null]);
                renderTable(nodeDataOld.ref1KeysText, nodeDataNew.ref1KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.keyCatTable.title' />", [null, formatDate, null]);
                renderList(nodeDataOld.ref1Data, nodeDataNew.ref1Data, "<fmt:message key='ui.obj.type1.attributes' />");
                renderTextWithTitle(nodeDataOld.ref1DataBasisText, nodeDataNew.ref1DataBasisText, "<fmt:message key='ui.obj.type1.dataBasisTable.title' />");
                renderTextWithTitle(nodeDataOld.ref1ProcessText, nodeDataNew.ref1ProcessText, "<fmt:message key='ui.obj.type1.processTable.title' />");

                renderAdditionalFieldsForRubric("refClass1", oldAdditionalFields, newAdditionalFields);

                // DQ
                renderSectionTitel("<fmt:message key='ui.obj.dq' />");
                renderTextWithTitle(nodeDataOld.ref1Coverage, nodeDataNew.ref1Coverage, "<fmt:message key='ui.obj.type1.coverage' />" + " [%]");
                renderTextWithTitle(nodeDataOld.ref1AltAccuracy, nodeDataNew.ref1AltAccuracy, "<fmt:message key='ui.obj.type1.sizeAccuracy' />");
                renderTextWithTitle(nodeDataOld.ref1PosAccuracy, nodeDataNew.ref1PosAccuracy, "<fmt:message key='ui.obj.type1.posAccuracy' />");
                renderTable(nodeDataOld.dq109Table, nodeDataNew.dq109Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table109.title' />");
                renderTable(nodeDataOld.dq112Table, nodeDataNew.dq112Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table112.title' />");
                renderTable(nodeDataOld.dq113Table, nodeDataNew.dq113Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table113.title' />");
                renderTable(nodeDataOld.dq114Table, nodeDataNew.dq114Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table114.title' />");
                renderTable(nodeDataOld.dq115Table, nodeDataNew.dq115Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table115.title' />");
                renderTable(nodeDataOld.dq120Table, nodeDataNew.dq120Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table120.title' />");
                renderTable(nodeDataOld.dq125Table, nodeDataNew.dq125Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table125.title' />");
                renderTable(nodeDataOld.dq126Table, nodeDataNew.dq126Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table126.title' />");
                renderTable(nodeDataOld.dq127Table, nodeDataNew.dq127Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table127.title' />");

                renderAdditionalFieldsForRubric("refClass1DQ", oldAdditionalFields, newAdditionalFields);
            } else if (nodeDataNew.objectClass == 2) {
                renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                // Literature
                renderTextWithTitle(nodeDataOld.ref2Author, nodeDataNew.ref2Author, "<fmt:message key='ui.obj.type2.author' />");
                renderTextWithTitle(nodeDataOld.ref2Publisher, nodeDataNew.ref2Publisher, "<fmt:message key='ui.obj.type2.editor' />");
                renderTextWithTitle(nodeDataOld.ref2PublishedIn, nodeDataNew.ref2PublishedIn, "<fmt:message key='ui.obj.type2.publishedIn' />");
                renderTextWithTitle(nodeDataOld.ref2PublishLocation, nodeDataNew.ref2PublishLocation, "<fmt:message key='ui.obj.type2.publishedLocation' />");
                renderTextWithTitle(nodeDataOld.ref2PublishedInIssue, nodeDataNew.ref2PublishedInIssue, "<fmt:message key='ui.obj.type2.issue' />");
                renderTextWithTitle(nodeDataOld.ref2PublishedInPages, nodeDataNew.ref2PublishedInPages, "<fmt:message key='ui.obj.type2.pages' />");
                renderTextWithTitle(nodeDataOld.ref2PublishedInYear, nodeDataNew.ref2PublishedInYear, "<fmt:message key='ui.obj.type2.publishedYear' />");
                renderTextWithTitle(nodeDataOld.ref2PublishedISBN, nodeDataNew.ref2PublishedISBN, "<fmt:message key='ui.obj.type2.isbn' />");
                renderTextWithTitle(nodeDataOld.ref2PublishedPublisher, nodeDataNew.ref2PublishedPublisher, "<fmt:message key='ui.obj.type2.publisher' />");
                renderTextWithTitle(nodeDataOld.ref2LocationText, nodeDataNew.ref2LocationText, "<fmt:message key='ui.obj.type2.locationTable.title' />");
                renderTextWithTitle(nodeDataOld.ref2DocumentType, nodeDataNew.ref2DocumentType, "<fmt:message key='ui.obj.type2.documentType' />");
                renderTextWithTitle(nodeDataOld.ref2BaseDataText, nodeDataNew.ref2BaseDataText, "<fmt:message key='ui.obj.type2.generalDataTable.title' />");
                renderTextWithTitle(nodeDataOld.ref2BibData, nodeDataNew.ref2BibData, "<fmt:message key='ui.obj.type2.additionalBibInfo' />");
                renderTextWithTitle(nodeDataOld.ref2Explanation, nodeDataNew.ref2Explanation, "<fmt:message key='ui.obj.type2.description' />");

                renderAdditionalFieldsForRubric("refClass2", oldAdditionalFields, newAdditionalFields);
            } else if (nodeDataNew.objectClass == 3) {
                renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                // Geodatendienst
                renderList(nodeDataOld.ref3ServiceTypeTable, nodeDataNew.ref3ServiceTypeTable, "<fmt:message key='ui.obj.type3.ref3ServiceTypeTable.title' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(5200, val);
                });
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(5100, nodeDataOld.ref3ServiceType), UtilSyslist.getSyslistEntryName(5100, nodeDataNew.ref3ServiceType), "<fmt:message key='ui.obj.type3.ref3ServiceTypeTable.title' />");
                renderTextWithTitle(nodeDataOld.ref3CouplingType, nodeDataNew.ref3CouplingType, "<fmt:message key='ui.obj.type3.couplingType' />");
                renderList(nodeDataOld.ref3ServiceVersion, nodeDataNew.ref3ServiceVersion, "<fmt:message key='ui.obj.type3.serviceVersion' />");
                renderTextWithTitle(nodeDataOld.ref3SystemEnv, nodeDataNew.ref3SystemEnv, "<fmt:message key='ui.obj.type3.environment' />");
                renderTextWithTitle(nodeDataOld.ref3History, nodeDataNew.ref3History, "<fmt:message key='ui.obj.type3.history' />");
                renderTextWithTitle(nodeDataOld.ref3BaseDataText, nodeDataNew.ref3BaseDataText, "<fmt:message key='ui.obj.type3.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type3.generalDataTable.tab.text' />" + ")");
                renderOperations(nodeDataOld.ref3Operation, nodeDataNew.ref3Operation);
                renderTable(nodeDataOld.ref3Scale, nodeDataNew.ref3Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type3.scaleTable.title' />");
                renderTextWithTitle(nodeDataOld.ref3HasAccessConstraint ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", nodeDataNew.ref3HasAccessConstraint ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.type3.ref3HasAccessConstraint' />");

                renderAdditionalFieldsForRubric("refClass3", oldAdditionalFields, newAdditionalFields);
            } else if (nodeDataNew.objectClass == 4) {
                renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                // Vorhaben
                renderTextWithTitle(nodeDataOld.ref4ParticipantsText, nodeDataNew.ref4ParticipantsText, "<fmt:message key='ui.obj.type4.participantsTable.title' />");
                renderTextWithTitle(nodeDataOld.ref4PMText, nodeDataNew.ref4PMText, "<fmt:message key='ui.obj.type4.projectManagerTable.title' />");
                renderTextWithTitle(nodeDataOld.ref4Explanation, nodeDataNew.ref4Explanation, "<fmt:message key='ui.obj.type4.description' />");

                renderAdditionalFieldsForRubric("refClass4", oldAdditionalFields, newAdditionalFields);
            } else if (nodeDataNew.objectClass == 5) {
                renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                // Datensammlung/Datenbank
                renderTable(nodeDataOld.ref5KeysText, nodeDataNew.ref5KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type5.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type5.keyCatTable.title' />", [null, formatDate, null]);
                renderTable(nodeDataOld.ref5dbContent, nodeDataNew.ref5dbContent, ["parameter", "additionalData"], ["<fmt:message key='ui.obj.type5.contentTable.header.parameter' />", "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />"], "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />");
                renderTextWithTitle(nodeDataOld.ref5MethodText, nodeDataNew.ref5MethodText, "<fmt:message key='ui.obj.type5.methodTable.title' />");
                renderTextWithTitle(nodeDataOld.ref5Explanation, nodeDataNew.ref5Explanation, "<fmt:message key='ui.obj.type5.description' />");

                renderAdditionalFieldsForRubric("refClass5", oldAdditionalFields, newAdditionalFields);
            } else if (nodeDataNew.objectClass == 6) {
                renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                // Dienst/Anwendung/Informationssystem
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(5300, nodeDataOld.ref6ServiceType), registry.byId("ref6ServiceType").get("displayedValue"), "<fmt:message key='ui.obj.type6.serviceType' />");
                renderList(nodeDataOld.ref6ServiceVersion, nodeDataNew.ref6ServiceVersion, "<fmt:message key='ui.obj.type6.serviceVersion' />");
                renderTextWithTitle(nodeDataOld.ref6SystemEnv, nodeDataNew.ref6SystemEnv, "<fmt:message key='ui.obj.type6.environment' />");
                renderTextWithTitle(nodeDataOld.ref6History, nodeDataNew.ref6History, "<fmt:message key='ui.obj.type6.history' />");
                renderTextWithTitle(nodeDataOld.ref6BaseDataText, nodeDataNew.ref6BaseDataText, "<fmt:message key='ui.obj.type6.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type6.generalDataTable.tab.text' />" + ")");
                renderTextWithTitle(nodeDataOld.ref6Explanation, nodeDataNew.ref6Explanation, "<fmt:message key='ui.obj.type6.description' />");
                renderTable(nodeDataOld.ref6UrlList, nodeDataNew.ref6UrlList, ["name", "url", "urlDescription"], ["<fmt:message key='ui.obj.type6.urlList.header.name' />", "<fmt:message key='ui.obj.type6.urlList.header.url' />", "<fmt:message key='ui.obj.type6.urlList.header.urlDescription' />"], "<fmt:message key='ui.obj.type6.urlList' />");

                renderAdditionalFieldsForRubric("refClass6", oldAdditionalFields, newAdditionalFields);
            }

            // spatial reference
            renderSectionTitel("<fmt:message key='ui.obj.spatial.title' />");
            UtilList.addSNSLocationLabels(nodeDataOld.spatialRefAdminUnitTable);
            renderTable(nodeDataOld.spatialRefAdminUnitTable, nodeDataNew.spatialRefAdminUnitTable, ["label", "nativeKey", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoThesTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.nativeKey' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude2' />"], "<fmt:message key='dialog.compare.object.spatialTable.title' />");
            renderTable(nodeDataOld.spatialRefLocationTable, nodeDataNew.spatialRefLocationTable, ["name", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude2' />"], "<fmt:message key='ui.obj.spatial.geoTable.title' />");

            // former class 1, now general "Raumbezug"
            //renderTextWithTitle(nodeDataOld.ref1SpatialSystem, nodeDataNew.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
            renderList(nodeDataOld.ref1SpatialSystemTable, nodeDataNew.ref1SpatialSystemTable, "<fmt:message key='ui.obj.type1.spatialSystem' />");

            var altitudeDataOld = [nodeDataOld];
            var altitudeDataNew = [nodeDataNew];
            // create cell render functions
            function lookupSpatialRefAltMeasure(val) {
                return UtilSyslist.getSyslistEntryName(102, val);
            }

            function lookupSpatialRefAltVDate(val) {
                return UtilSyslist.getSyslistEntryName(101, val);
            }

            if (altitudeDataOld[0].spatialRefAltMin === null) {
                altitudeDataOld[0].spatialRefAltMin = "";
            }
            if (altitudeDataOld[0].spatialRefAltMax === null) {
                altitudeDataOld[0].spatialRefAltMax = "";
            }
            if (altitudeDataOld[0].spatialRefAltMeasure === null) {
                altitudeDataOld[0].spatialRefAltMeasure = "";
            }
            if (altitudeDataOld[0].spatialRefAltVDate === null) {
                altitudeDataOld[0].spatialRefAltVDate = "";
            }
            if (altitudeDataOld[0].spatialRefAltMin != altitudeDataNew[0].spatialRefAltMin ||
                altitudeDataOld[0].spatialRefAltMax != altitudeDataNew[0].spatialRefAltMax ||
                altitudeDataOld[0].spatialRefAltMeasure != altitudeDataNew[0].spatialRefAltMeasure ||
                altitudeDataOld[0].spatialRefAltVDate != altitudeDataNew[0].spatialRefAltVDate) {

                renderTable(altitudeDataOld, altitudeDataNew, ["spatialRefAltMin", "spatialRefAltMax", "spatialRefAltMeasure", "spatialRefAltVDate"], ["<fmt:message key='ui.obj.spatial.height.min' />", "<fmt:message key='ui.obj.spatial.height.max' />", "<fmt:message key='ui.obj.spatial.height.unit' />", "<fmt:message key='ui.obj.spatial.height.geodeticSystem' />"], "<fmt:message key='ui.obj.spatial.height' />", [null, null, lookupSpatialRefAltMeasure, lookupSpatialRefAltVDate]);
            }
            renderTextWithTitle(nodeDataOld.spatialRefExplanation, nodeDataNew.spatialRefExplanation, "<fmt:message key='ui.obj.spatial.description' />");

            renderAdditionalFieldsForRubric("spatialRef", oldAdditionalFields, newAdditionalFields);

            // temporal reference
            renderSectionTitel("<fmt:message key='ui.obj.time.title' />");
            var timeRefTxtOld;
            var timeRefTxtNew;
            if (nodeDataOld.timeRefDate1) {
                if (nodeDataOld.timeRefType && nodeDataOld.timeRefType == "von") {
                    timeRefTxtOld = "von " + formatDate(nodeDataOld.timeRefDate1) + " bis " + formatDate(nodeDataOld.timeRefDate2);
                } else if (nodeDataOld.timeRefType) {
                    timeRefTxtOld = nodeDataOld.timeRefType + " " + formatDate(nodeDataOld.timeRefDate1);
                }
            }
            if (nodeDataNew.timeRefDate1) {
                if (nodeDataNew.timeRefType && nodeDataNew.timeRefType == "von") {
                    timeRefTxtNew = "von " + formatDate(nodeDataNew.timeRefDate1) + " bis " + formatDate(nodeDataNew.timeRefDate2);
                } else if (nodeDataNew.timeRefType) {
                    timeRefTxtNew = nodeDataNew.timeRefType + " " + formatDate(nodeDataNew.timeRefDate1);
                }
            }

            renderTextWithTitle(timeRefTxtOld, timeRefTxtNew, "<fmt:message key='ui.obj.time.timeRefContent' />");
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(523, nodeDataOld.timeRefStatus), UtilSyslist.getSyslistEntryName(523, nodeDataNew.timeRefStatus), "<fmt:message key='ui.obj.time.state' />");
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(518, nodeDataOld.timeRefPeriodicity), UtilSyslist.getSyslistEntryName(518, nodeDataNew.timeRefPeriodicity), "<fmt:message key='ui.obj.time.periodicity' />");
            if ((nodeDataOld.timeRefIntervalNum && nodeDataOld.timeRefIntervalUnit) || (nodeDataNew.timeRefIntervalNum && nodeDataNew.timeRefIntervalUnit)) {
                var textOld = "<fmt:message key='ui.obj.time.interval.each' />" + " " + nodeDataOld.timeRefIntervalNum + " " + UtilSyslist.getSyslistEntryName(1230, nodeDataOld.timeRefIntervalUnit);
                var textNew = "<fmt:message key='ui.obj.time.interval.each' />" + " " + nodeDataNew.timeRefIntervalNum + " " + UtilSyslist.getSyslistEntryName(1230, nodeDataNew.timeRefIntervalUnit);
                renderTextWithTitle(textOld, textNew, "<fmt:message key='ui.obj.time.interval' />");
            }

            // create cell render functions
            function lookupTimeRefType(val) {
                return UtilSyslist.getSyslistEntryName(502, val);
            }
            renderTable(nodeDataOld.timeRefTable, nodeDataNew.timeRefTable, ["date", "type"], ["<fmt:message key='ui.obj.time.timeRefTable.header.date' />", "<fmt:message key='ui.obj.time.timeRefTable.header.type' />"], "<fmt:message key='ui.obj.time.timeRefTable.title' />", [formatDate, lookupTimeRefType]);
            renderTextWithTitle(nodeDataOld.timeRefExplanation, nodeDataNew.timeRefExplanation, "<fmt:message key='ui.obj.time.description' />");

            renderAdditionalFieldsForRubric("timeRef", oldAdditionalFields, newAdditionalFields);

            // additional information
            renderSectionTitel("<fmt:message key='ui.obj.additionalInfo.title' />");
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeDataOld.extraInfoLangMetaDataCode), UtilSyslist.getSyslistEntryName(99999999, nodeDataNew.extraInfoLangMetaDataCode), "<fmt:message key='ui.obj.additionalInfo.language.metadata' />");
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeDataOld.extraInfoLangDataCode), UtilSyslist.getSyslistEntryName(99999999, nodeDataNew.extraInfoLangDataCode), "<fmt:message key='ui.obj.additionalInfo.language.data' />");
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(3571, nodeDataOld.extraInfoPublishArea), UtilSyslist.getSyslistEntryName(3571, nodeDataNew.extraInfoPublishArea), "<fmt:message key='ui.obj.additionalInfo.publicationCondition' />");
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(510, nodeDataOld.extraInfoCharSetDataCode), UtilSyslist.getSyslistEntryName(510, nodeDataNew.extraInfoCharSetDataCode), "<fmt:message key='ui.obj.additionalInfo.charSet.data' />");
            // Table is only displayed for object classes 1 and 3
            if (nodeDataNew.objectClass == 1 || nodeDataNew.objectClass == 3) {
                renderTable(nodeDataOld.extraInfoConformityTable, nodeDataNew.extraInfoConformityTable, ["specification", "level"], ["<fmt:message key='ui.obj.additionalInfo.conformityTable.header.specification' />", "<fmt:message key='ui.obj.additionalInfo.conformityTable.header.level' />"],
                    "<fmt:message key='ui.obj.additionalInfo.conformityTable.title' />", [

                        function(val) {
                            return UtilSyslist.getSyslistEntryName(6005, val);
                        },
                        function(val) {
                            return UtilSyslist.getSyslistEntryName(6000, val);
                        }
                    ]);
            }
            renderList(nodeDataOld.extraInfoXMLExportTable, nodeDataNew.extraInfoXMLExportTable, "<fmt:message key='ui.obj.additionalInfo.xmlExportCriteria' />");
            renderList(nodeDataOld.extraInfoLegalBasicsTable, nodeDataNew.extraInfoLegalBasicsTable, "<fmt:message key='ui.obj.additionalInfo.legalBasis' />");
            renderTextWithTitle(nodeDataOld.extraInfoPurpose, nodeDataNew.extraInfoPurpose, "<fmt:message key='ui.obj.additionalInfo.purpose' />");
            renderTextWithTitle(nodeDataOld.extraInfoUse, nodeDataNew.extraInfoUse, "<fmt:message key='ui.obj.additionalInfo.suitability' />");

            renderAdditionalFieldsForRubric("extraInfo", oldAdditionalFields, newAdditionalFields);

            // availability
            renderSectionTitel("<fmt:message key='ui.obj.availability.title' />");
            renderList(nodeDataOld.availabilityAccessConstraints, nodeDataNew.availabilityAccessConstraints, "<fmt:message key='ui.obj.availability.accessConstraints' />", null, function(val) {
                return UtilSyslist.getSyslistEntryName(6010, val);
            });
            renderTextWithTitle(nodeDataOld.availabilityUseConstraints, nodeDataNew.availabilityUseConstraints, "<fmt:message key='ui.obj.availability.useConstraints' />");

            // only show "availabilityDataFormatInspire" in class 1 ... by default a default value is mapped to 
            // this field which shall not be shown here
            if (nodeDataOld.objectClass == 1 || nodeDataNew.objectClass == 1) {
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(6300, nodeDataOld.availabilityDataFormatInspire), UtilSyslist.getSyslistEntryName(6300, nodeDataNew.availabilityDataFormatInspire), "<fmt:message key='ui.obj.availability.dataFormatInspire' />");
            }
            renderTable(nodeDataOld.availabilityDataFormatTable, nodeDataNew.availabilityDataFormatTable, ["name", "version", "compression", "pixelDepth"], ["<fmt:message key='ui.obj.availability.dataFormatTable.header.name' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.version' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.compression' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.depth' />"], "<fmt:message key='ui.obj.availability.dataFormatTable.title' />");
            renderTable(nodeDataOld.availabilityMediaOptionsTable, nodeDataNew.availabilityMediaOptionsTable, ["name", "transferSize", "location"], ["<fmt:message key='ui.obj.availability.mediaOptionTable.header.type' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.amount' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.location' />"], "<fmt:message key='ui.obj.availability.mediaOptionTable.title' />", [

                function(val) {
                    return UtilSyslist.getSyslistEntryName(520, val);
                },
                null, null
            ]);
            renderTextWithTitle(nodeDataOld.availabilityOrderInfo, nodeDataNew.availabilityOrderInfo, "<fmt:message key='ui.obj.availability.orderInfo' />");

            renderAdditionalFieldsForRubric("availability", oldAdditionalFields, newAdditionalFields);

            // indexing
            renderSectionTitel("<fmt:message key='ui.obj.thesaurus.title' />");
            renderList(nodeDataOld.thesaurusTermsTable, nodeDataNew.thesaurusTermsTable, "<fmt:message key='ui.adr.thesaurus.terms' />", "title");
            renderList(nodeDataOld.thesaurusTopicsList, nodeDataNew.thesaurusTopicsList, "<fmt:message key='ui.obj.thesaurus.terms.category' />", null, function(val) {
                return UtilSyslist.getSyslistEntryName(527, val);
            });
            renderTextWithTitle(nodeDataOld.thesaurusEnvExtRes ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", nodeDataNew.thesaurusEnvExtRes ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.thesaurus.terms.enviromental.displayCatalogPage' />");
            renderList(nodeDataOld.thesaurusEnvTopicsList, nodeDataNew.thesaurusEnvTopicsList, "<fmt:message key='ui.obj.thesaurus.terms.enviromental.title' />" + " - " + "<fmt:message key='ui.obj.thesaurus.terms.enviromental.topics' />", null, function(val) {
                return UtilSyslist.getSyslistEntryName(1410, val);
            });

            renderAdditionalFieldsForRubric("thesaurus", oldAdditionalFields, newAdditionalFields);

            // references
            renderSectionTitel("<fmt:message key='ui.obj.links.title' />");
            renderList(nodeDataOld.linksFromObjectTable, nodeDataNew.linksFromObjectTable, "<fmt:message key='dialog.compare.object.linksFromTable.title' />", "title");
            renderLinkList(nodeDataOld.linksToObjectTable, nodeDataNew.linksToObjectTable, "<fmt:message key='dialog.compare.object.linksToTable.title' />", false);
            renderLinkList(nodeDataOld.linksToUrlTable, nodeDataNew.linksToUrlTable, "<fmt:message key='dialog.compare.object.linksToUrlTable.title' />", true);

            renderAdditionalFieldsForRubric("links", oldAdditionalFields, newAdditionalFields);

            renderAdditionalRubrics(oldAdditionalFields, newAdditionalFields);

            // administrative data
            renderSectionTitel("<fmt:message key='dialog.compare.object.administrative' />");
            var oldObjId = nodeDataOld.orgObjId ? nodeDataOld.orgObjId : nodeDataOld.uuid;
            var newObjId = nodeDataNew.orgObjId ? nodeDataNew.orgObjId : nodeDataNew.uuid;
            renderTextWithTitle(oldObjId, newObjId, "<fmt:message key='dialog.compare.object.id' />");
            renderTextWithTitle(UtilCatalog.catalogData.catalogName, UtilCatalog.catalogData.catalogName, "<fmt:message key='dialog.compare.object.catalog' />");
        }

        function renderAdditionalFieldsForRubric(rubric, nodeDataOld, nodeDataNew) {
            console.debug("add additional fields info of rubric: " + rubric);
            var addDomWidgets = query(".additionalField", rubric);
            array.forEach(addDomWidgets, function(domWidget) {
                var widgetId = domWidget.getAttribute("widgetId");
                if (!widgetId) widgetId = domWidget.id;
                var label = searchLabelFrom(domWidget);
                var dataOld = getValueFromAdditional(widgetId, nodeDataOld);
                var dataNew = getValueFromAdditional(widgetId, nodeDataNew);
                // if it is a table
                if ((dataOld && typeof(dataOld) == "object") || (dataNew && typeof(dataNew) == "object")) {
                    console.debug("widget id: " + widgetId);
                    var columnFields = getColumnFields(widgetId);
                    var columnNames = getColumnNames(widgetId);
                    var formatters = getColumnFormatters(widgetId);
                    renderTable(dataOld, dataNew, columnFields, columnNames, label, formatters, true, UtilGrid.getTable(widgetId).getColumns());
                } else {
                    renderTextWithTitle(dataOld, dataNew, label);
                }
            });
        }

        function getValueFromAdditional(id, additionalFields) {
            var result = null;
            array.some(additionalFields, function(field) {
                if (field.identifier === id) {
                    // special handling of tables
                    if (field.tableRows !== null) {
                        result = prepareAdditionalTable(field.tableRows);
                        return true;
                    } else {
                        if (field.value !== null) {
                            result = field.value + getUnitFromField(id);
                        } else {
                            result = null;
                        }
                        return true;
                    }
                }
            });
            // convert boolean values to text
            if (result == "true") result = "<fmt:message key='general.yes' />";
            if (result == "false") result = "<fmt:message key='general.no' />";
            return result;
        }

        function getUnitFromField(id) {
            var widget = registry.byId(id);
            var unit = widget ? widget.domNode.getAttribute("unit") : null;
            if (unit)
                return " " + unit;
            else
                return "";
        }

        function getColumnFields(id) {
            return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {
                return col.field;
            });
        }

        function getColumnNames(id) {
            return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {
                return col.name;
            });
        }

        function getColumnFormatters(id) {
            return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {
                return col.formatter;
            });
        }

        function prepareAdditionalTable(rows) {
            var tableList = [];
            array.forEach(rows, function(row) {
                if (row.length === 0) return; // empty table
                var item = {};
                array.forEach(row, function(rowItem) {
                    item[rowItem.identifier] = rowItem.value ? rowItem.value : ""; // TODO: listIds? mapping?
                });
                tableList.push(item);
            });
            return tableList;

        }

        function searchLabelFrom(element) {
            while (!domClass.contains(element, "input") && !domClass.contains(element, "dijitCheckBox")) {
                element = element.parentNode;
            }
            if (domClass.contains(element, "dijitCheckBox")) {
                element = element.nextSibling;
            } else {
                element = element.previousSibling;
                while (!domClass.contains(element, "label")) {
                    element = element.previousSibling;
                }
            }

            if (element !== null) {
                var text = dojo.isFF ? element.textContent : element.innerText;
                if (text[text.length - 1] === "*")
                    return text.slice(0, -1);
                return text;
            } else
                return "???";
        }

        function renderAdditionalRubrics(oldAdditionalFields, newAdditionalFields) {
            var addDomRubrics = query(".rubric.additional", "contentFrameBodyObject");
            array.forEach(addDomRubrics, function(domRubric) {
                var rubricText = dojo.isFF ? domRubric.children[0].textContent : domRubric.children[0].innerText;
                renderSectionTitel(rubricText);
                renderAdditionalFieldsForRubric(domRubric.id, oldAdditionalFields, newAdditionalFields);
            });
        }

        function renderSectionTitel(val) {
            dom.byId("diffContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
            dom.byId("oldContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
            dom.byId("currentContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
        }

        function renderTitel(oldVal, newVal) {
            // compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
            if (oldVal === null) oldVal = "";
            if (newVal === null) newVal = "";
            oldVal += "";
            newVal += "";

            dom.byId("diffContent").innerHTML += "<strong>" + diffString(oldVal, newVal) + "</strong><br/><br/>";
            dom.byId("oldContent").innerHTML += "<strong>" + oldVal + "</strong><br/><br/>";
            dom.byId("currentContent").innerHTML += "<strong>" + newVal + "</strong><br/><br/>";
        }

        function renderTextWithTitle(oldVal, newVal, title) {
            // compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
            if (oldVal === null) oldVal = "";
            if (newVal === null) newVal = "";
            oldVal += "";
            newVal += "";

            if (!detailHelper.isValid(oldVal) && !detailHelper.isValid(newVal)) {
                return;
            }

            dom.byId("diffContent").innerHTML += "<strong>" + title + "</strong><p>" + diffString(oldVal, newVal) + "</p><br/>";
            dom.byId("oldContent").innerHTML += "<strong>" + title + "</strong><p>" + oldVal.replace(/\n/g, "<br />") + "</p><br/>";
            dom.byId("currentContent").innerHTML += "<strong>" + title + "</strong><p>" + newVal.replace(/\n/g, "<br />") + "</p><br/>";
        }

/*
        function renderLinkedObjectListWithTitle(oldList, newList, title) {
            var t, i;
            if (oldList.length > 0) {
                t = "<strong>" + title + "</strong><br/><br/><p>";
                for (i = 0; i < oldList.length; ++i) {
                    t += "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\"" + oldList[i].uuid + "\");'" +
                        "title='" + oldList[i].title + "'>" + oldList[i].title + "</a>" + "<br/>";
                }
                dom.byId("oldContent").innerHTML += t + "</p><br/><br/>";
            }
            if (newList.length > 0) {
                t = "<strong>" + title + "</strong><br/><br/><p>";
                for (i = 0; i < newList.length; ++i) {
                    t += "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\"" + newList[i].uuid + "\");'" +
                        "title='" + newList[i].title + "'>" + newList[i].title + "</a>" + "<br/>";
                }
                dom.byId("currentContent").innerHTML += t + "</p><br/><br/>";
            }
            if (oldList.length > 0 || newList.length > 0) {
                t = "<strong>" + title + "</strong><br/><br/><p>";
                var len = (newList.length > oldList.length) ? newList.length : oldList.length;
                for (i = 0; i < len; ++i) {
                    if (i > (oldList.length - 1)) {
                        t += diffString("", newList[i].title) + "<br/>";
                    } else if (i > (newList.length - 1)) {
                        t += diffString(oldList[i].title, "") + "<br/>";
                    } else {
                        t += diffString(oldList[i].title, newList[i].title) + "<br/>";
                    }
                }
                dom.byId("diffContent").innerHTML += t + "</p><br/><br/>";
            }
        }

        function renderAddressList(oldList, newList) {
            var oldText = "";
            var newText = "";
            var i;
            if (oldList.length > 0) {
                for (i = 0; i < oldList.length; ++i) {
                    oldText += oldList[i].nameOfRelation + ": ";
                    oldText += UtilAddress.createAddressTitle(oldList[i]) + "\n";
                }
                dom.byId("oldContent").innerHTML += oldText.replace(/\n/g, '<br />') + "<br /><br />";
            }

            if (newList.length > 0) {
                for (i = 0; i < newList.length; ++i) {
                    newText += newList[i].nameOfRelation + ": ";
                    newText += UtilAddress.createAddressTitle(newList[i]) + "\n";
                }
                dom.byId("currentContent").innerHTML += newText.replace(/\n/g, '<br />') + "<br /><br />";
            }

            var diffText = diffString(oldText, newText);
            diffText = diffText.replace(/\n/g, '<br />').replace(/&para;/g, '');

            dom.byId("diffContent").innerHTML += diffText + "<br /><br />";
        }
*/

        function renderText(oldVal, newVal) {
            if (oldVal === null) {
                oldVal = "";
            }
            if (newVal === null) {
                newVal = "";
            }

            oldVal += "";
            newVal += "";

            dom.byId("diffContent").innerHTML += "<p>" + diffString(oldVal, newVal) + "</p><br/>";
            dom.byId("oldContent").innerHTML += "<p>" + oldVal.replace(/\n/g, "<br />") + "</p><br/>";
            dom.byId("currentContent").innerHTML += "<p>" + newVal.replace(/\n/g, "<br />") + "</p><br/>";
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
            for (var i = 0; i < list.length; i++) {
                var val = "";
                if (rowProperty) {
                    val = list[i][rowProperty];
                } else {
                    val = list[i];
                }
                if (renderFunction) {
                    val = renderFunction.call(this, val);
                }
                if (val && val !== "") {
                    valList += val + "<br/>";
                }
            }
            return valList;
        }

        function buildListBodyForDiff(diff, rowProperty, renderFunction) {
            var diffList = diff.eq.concat(diff.ins.concat(diff.del));

            var valList = "";
            for (var i = 0; i < diffList.length; i++) {
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
                if (val && val !== "") {
                    valList += prefix + val + suffix + "<br/>";
                }
            }
            return valList;
        }

        function renderList(oldList, newList, title, rowProperty, renderFunction) {
            var t, valList;
            if (oldList && oldList.length > 0) {
                t = buildListHead(title);
                valList = buildListBody(oldList, rowProperty, renderFunction);

                if (valList !== "") {
                    dom.byId("oldContent").innerHTML += t + valList + "</p><br/>";
                }
            }

            if (newList && newList.length > 0) {
                t = buildListHead(title);
                valList = buildListBody(newList, rowProperty, renderFunction);

                if (valList !== "") {
                    dom.byId("currentContent").innerHTML += t + valList + "</p><br/>";
                }
            }

            var diff = compareTable(oldList, newList, rowProperty);

            if ((oldList && oldList.length > 0) || (newList && newList.length > 0)) {
                t = buildListHead(title);
                valList = buildListBodyForDiff(diff, rowProperty, renderFunction);

                if (valList !== "") {
                    dom.byId("diffContent").innerHTML += t + valList + "</p><br/>";
                }
            }
        }

        function renderTable(oldList, newList, rowProperties, listHeader, title, cellRenderFunction, useGridFormatter, columns) {
            var t;
            if (oldList && oldList.length > 0) {
                t = buildTableHead(listHeader, title);
                t += buildTableBody(oldList, rowProperties, cellRenderFunction, useGridFormatter, columns);

                dom.byId("oldContent").innerHTML += t + "<br/><br/>";
            }

            if (newList && newList.length > 0) {
                t = buildTableHead(listHeader, title);
                t += buildTableBody(newList, rowProperties, cellRenderFunction, useGridFormatter, columns);

                dom.byId("currentContent").innerHTML += t + "<br/><br/>";
            }

            var diff = compareTable(oldList, newList, rowProperties);

            if ((oldList && oldList.length > 0) || (newList && newList.length > 0)) {
                t = buildTableHead(listHeader, title);
                t += buildTableBodyForDiff(diff, rowProperties, cellRenderFunction, useGridFormatter, columns);

                dom.byId("diffContent").innerHTML += t + "<br/><br/>";
            }
        }

        function renderOperations(oldList, newList) {
            if (!oldList) {
                oldList = [];
            }
            if (!newList) {
                newList = [];
            }
            if (oldList.length === 0 && newList.length === 0) {
                return;
            }
            renderTable(oldList, newList, ["name", "addressList"], ["<fmt:message key='ui.obj.type3.operationTable.header.name' />", "<fmt:message key='ui.obj.type3.operationTable.header.address' />"], "<fmt:message key='ui.obj.type3.operationTable.title' />", [null, renderFirstElement]);

            // render Operation details
            var emptyOp = {
                platform: "",
                methodCall: "",
                paramList: [],
                addressList: [],
                dependencies: []
            };

            var syslist5180Formatter = function(val) {
                return UtilSyslist.getSyslistEntryName(5180, val);
            };

            var maxLength = (oldList.length > newList.length) ? oldList.length : newList.length;
            for (var i = 0; i < maxLength; i++) {
                var opOld = oldList[i];
                if (!opOld) {
                    opOld = emptyOp;
                }
                var opNew = newList[i];
                if (!opNew) {
                    opNew = emptyOp;
                }

                renderTextWithTitle(opOld.name, opNew.name, "<fmt:message key='dialog.operation.opName' />");
                renderTextWithTitle(opOld.description, opNew.description, "<fmt:message key='dialog.operation.description' />");
                renderList(opOld.addressList, opNew.addressList, "<fmt:message key='dialog.operation.address' />");
                renderList(opOld.platform, opNew.platform, "<fmt:message key='dialog.operation.platforms' />", null, syslist5180Formatter);
                renderTextWithTitle(opOld.methodCall, opNew.methodCall, "<fmt:message key='dialog.operation.call' />");
                renderTable(opOld.paramList, opNew.paramList, ["name", "direction", "description", "optional", "multiple"], ["<fmt:message key='dialog.operation.name' />", "<fmt:message key='dialog.operation.direction' />", "<fmt:message key='dialog.operation.description' />", "<fmt:message key='dialog.operation.optional' />", "<fmt:message key='dialog.operation.multiplicity' />"], "<fmt:message key='dialog.operation.parameter' />", [null, null, null, renderYesNo, renderYesNo]);
                renderList(opOld.dependencies, opNew.dependencies, "<fmt:message key='dialog.operation.dependencies' />");
            }
        }

        function buildTableHead(listHeader, title) {
            var t = "", i;
            if (detailHelper.isValid(title)) {
                t += "<strong>" + title + "</strong><br/><br/>";
            }
            t += "<p><table class=\"filteringTable\" cellspacing=\"0\">";
            if (listHeader && listHeader.length > 0) {
                t += "<thead class=\"fixedHeader\"><tr>";
                for (i = 0; i < listHeader.length; i++) {
                    t += "<th style=\"padding-right:4px\">" + listHeader[i] + "</th>";
                }
                t += "</tr></thead>";
            }
            return t;
        }

        function buildTableBody(list, rowProperties, cellRenderFunction, useGridFormatter, columns) {
            var t = "<tbody>";
            for (var i = 0; i < list.length; i++) {
                if (i % 2) {
                    t += "<tr class=\"alt\">";
                } else {
                    t += "<tr>";
                }
                for (var j = 0; j < rowProperties.length; j++) {
                    var value = list[i][rowProperties[j]] ? list[i][rowProperties[j]] : "";
                    if (cellRenderFunction && cellRenderFunction[j]) {
                        if (useGridFormatter)
                            t += "<td style=\"padding-right:4px\">" + cellRenderFunction[j].call(this, i, j, value, columns[j]) + "</td>";
                        else
                            t += "<td style=\"padding-right:4px\">" + cellRenderFunction[j].call(this, value) + "</td>";
                    } else {
                        t += "<td style=\"padding-right:4px\">" + value + "</td>";
                    }
                }
                t += "</tr>";

            }
            t += "</tbody></table></p>";
            return t;
        }

        function buildTableBodyForDiff(diff, rowProperties, cellRenderFunction, useGridFormatter, columns) {
            var diffList = diff.eq.concat(diff.ins.concat(diff.del));

            var t = "<tbody>";
            for (var i = 0; i < diffList.length; i++) {
                if (i % 2) {
                    t += "<tr class=\"alt\">";
                } else {
                    t += "<tr>";
                }
                for (var j = 0; j < rowProperties.length; j++) {
                    var value = diffList[i][rowProperties[j]] ? diffList[i][rowProperties[j]] : "";
                    if (cellRenderFunction && cellRenderFunction[j]) {
                        t += "<td style=\"padding-right:4px\">";
                        if (arrayContains(diff.ins, diffList[i])) {
                            t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;'>";
                            t += useGridFormatter ? cellRenderFunction[j].call(this, i, j, value, columns[j]) : cellRenderFunction[j].call(this, value);
                            t += "</span>";
                        } else if (arrayContains(diff.del, diffList[i])) {
                            t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;'>";
                            t += useGridFormatter ? cellRenderFunction[j].call(this, i, j, value, columns[j]) : cellRenderFunction[j].call(this, value);
                            t += "</span>";
                        } else {
                            t += useGridFormatter ? cellRenderFunction[j].call(this, i, j, value, columns[j]) : cellRenderFunction[j].call(this, value);
                        }

                        t += "</td>";
                    } else {
                        t += "<td style=\"padding-right:4px\">";
                        if (arrayContains(diff.ins, diffList[i])) {
                            t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;'>";
                            t += diffList[i][rowProperties[j]];
                            t += "</span>";
                        } else if (arrayContains(diff.del, diffList[i])) {
                            t += "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;'>";
                            t += diffList[i][rowProperties[j]];
                            t += "</span>";
                        } else {
                            t += diffList[i][rowProperties[j]];
                        }
                    }
                }
                t += "</tr>";

            }
            t += "</tbody></table></p>";
            return t;
        }

        function renderLinkList(oldList, newList, title, isUrl) {
            var oldListProcessed = [];
            var newListProcessed = [];
            var val, i;

            if (oldList && oldList.length > 0) {
                for (i = 0; i < oldList.length; i++) {
                    if (isUrl) {
                        val = "<a href=\"" + oldList[i].url + "\" target=\"new\">" + oldList[i].name + "</a>";
                    } else {
                        val = "" + oldList[i].title;
                    }
                    if (UtilString.hasValue(oldList[i].relationTypeName)) {
                        val += " (" + oldList[i].relationTypeName + ")";
                    }
                    oldListProcessed.push(val + "<br/>");
                }
            }
            if (newList && newList.length > 0) {
                for (i = 0; i < newList.length; i++) {
                    if (isUrl) {
                        val = "<a href=\"" + newList[i].url + "\" target=\"new\">" + newList[i].name + "</a>";
                    } else {
                        val = "" + newList[i].title;
                    }
                    if (UtilString.hasValue(newList[i].relationTypeName)) {
                        val += " (" + newList[i].relationTypeName + ")";
                    }
                    newListProcessed.push(val + "<br/>");
                }
            }

            renderList(oldListProcessed, newListProcessed, title);
        }

        // Compare two tables containing objects with 'properties'.
        // The result is returned as an object containing the following information:
        // { eq:  array of objects that are in table1 and table2,
        //   ins: array of objects that are in table2 but not in table1,
        //   del: array of objects that are in table1 but not in table2 } 
        function compareTable(table1, table2, properties) {
            var diff = {
                eq: [],
                ins: [],
                del: []
            };

            // Iterate over all objects in table1 and compare them to the objects in table2
            // If a match is found, add the object to diff.eq
            // Otherwise add it to diff.del
            var i,j;
            for (i in table1) {
                var obj1 = table1[i];
                var found = false;

                for (j in table2) {
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
            for (j in table2) {
                var obj = table2[j];
                if (!arrayContains(diff.eq, obj)) {
                    diff.ins.push(obj);
                }
            }

            return diff;
        }

        // Compare two objects according to their properties
        function compareObj(obj1, obj2, properties) {
            if (properties === null) {
                return (obj1 + "" == obj2 + "");
            }
            if (typeof(properties) == "string") {
                properties = [properties];
            }

            for (var i in properties) {
                var prop1 = obj1[properties[i]] || "";
                var prop2 = obj2[properties[i]] || "";
                // Compare as strings so date objects are handled properly
                if (prop1 + "" != prop2 + "") {
                    return false;
                }
            }
            return true;
        }

        // Returns whether the array 'arr' contains the object 'obj'
        function arrayContains(arr, obj) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                if (arr[i] === obj) {
                    return true;
                }
            }
            return false;
        }

        function renderFirstElement(val) {
            var retVal = "";
            if (val && dojo.isArray(val) && val.length > 0) {
                retVal = val[0];
            }
            return retVal;
        }

        function renderYesNo(val) {
            if (val == 1) {
                return "<fmt:message key='general.yes' />";
            } else {
                return "<fmt:message key='general.no' />";
            }
        }
    });
</script>
</head>

<body>
	<div data-dojo-type="dijit/layout/ContentPane">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=hierarchy-maintenance-3#hierarchy-maintenance-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<!-- MAIN TAB CONTAINER START -->
		<div id="compareViews" data-dojo-type="dijit/layout/TabContainer" selectedChild="diffView" style="height:600px; width:100%;" >
			<!-- MAIN TAB 1 START -->
			<div id="diffView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.compare.compare" />">
			<div id="diffContent" class="inputContainer field grey"></div>
			<div id="diffContentLegend" class="inputContainer field grey"><span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.insertedText" /><br/>
				<span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.deletedText" /></div>
			</div>
			<!-- MAIN TAB 1 END -->

			<!-- MAIN TAB 2 START -->
			<div id="oldView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.compare.original" />">
			<div id="oldContent" class="inputContainer field grey"></div>
		</div>
		<!-- MAIN TAB 2 END -->

		<!-- MAIN TAB 3 START -->
		<div id="currentView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.compare.modified" />">
		<div id="currentContent" class="inputContainer field grey"></div>
	</div>
	<!-- MAIN TAB 3 END -->

</div>
<!-- MAIN TAB CONTAINER END -->
</div>
<!-- CONTENT END -->
</body>
</html>
