<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var pageQaA = _container_;

require([
    "dojo/on",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-style",
    "dojo/Deferred",
    "dojo/promise/all",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dijit/registry",
    "ingrid/utils/Grid",
    "ingrid/layoutCreator",
    "ingrid/utils/PageNavigation",
    "ingrid/utils/List",
    "ingrid/message",
    "ingrid/utils/Store",
    "ingrid/utils/Address",
    "ingrid/utils/Catalog",
    "ingrid/grid/CustomGridFormatters"
], function(on, aspect, dom, style, Deferred, all, array, lang, registry, UtilGrid, layoutCreator, navigation, UtilList, message, UtilStore, UtilAddress, UtilCatalog, GridFormatters) {


        // display 10 datasets per page
        var resultsPerPage = 10;
        var tableIdList = ["qaAssignedObjTable", "qaAssignedAdrTable", "qaModifiedObjTable", "qaModifiedAdrTable",
            "qaExpiredObjTable", "qaExpiredAdrTable", "qaSpatialObjTable"];

        on(_container_, "Load", function() {
            try {
                createDomElements();
                initTables();
                initSortFunctions();
                reloadPage();
                
            } catch (ex) {
                console.error(ex);
            }
        });

        function createDomElements(){
            var qaAssignedObjTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
               {field: 'assignerUserTitle',name: "<fmt:message key='dialog.qa.assignedBy' />",width: '130px'},
               {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
               {field: 'assignTime',name: "<fmt:message key='dialog.qa.assignedAt' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaAssignedObjTable", null, qaAssignedObjTableStructure, null);
            
            var qaAssignedAdrTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
               {field: 'assignerUserTitle',name: "<fmt:message key='dialog.qa.assignedBy' />",width: '130px'},
               {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
               {field: 'assignTime',name: "<fmt:message key='dialog.qa.assignedAt' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaAssignedAdrTable", null, qaAssignedAdrTableStructure, null);
            
            var qaModifiedObjTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
               {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
               {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
               {field: 'date',name: "<fmt:message key='dialog.qa.date' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaModifiedObjTable", null, qaModifiedObjTableStructure, null);
            
            var qaModifiedAdrTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
               {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
               {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
               {field: 'date',name: "<fmt:message key='dialog.qa.date' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaModifiedAdrTable", null, qaModifiedAdrTableStructure, null);
            
            var qaExpiredObjTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '700px'},
               {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
               {field: 'expiryDate',name: "<fmt:message key='dialog.qa.expiredAt' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaExpiredObjTable", null, qaExpiredObjTableStructure, null);
            
            var qaExpiredAdrTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '700px'},
               {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
               {field: 'expiryDate',name: "<fmt:message key='dialog.qa.expiredAt' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaExpiredAdrTable", null, qaExpiredAdrTableStructure, null);
            
            var qaSpatialObjTableStructure = [
               {field: 'icon',name: '&nbsp;',width: '32px'},
               {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '700px'},
               {field: 'expiryDate',name: "<fmt:message key='dialog.qa.expiredAt' />",width: 'auto', formatter: GridFormatters.DateCellFormatter}
            ];
            layoutCreator.createDataGrid("qaSpatialObjTable", null, qaSpatialObjTableStructure, null);
        }   
           

        // Initialise the tables with PageNavs, sortParams, etc.
        function initTables() {
            // Add PageNavs to tables
            array.forEach(tableIdList, function(tableId) {
                UtilGrid.getTable(tableId).pageNav = new navigation.PageNavigation({
                        resultsPerPage: resultsPerPage,
                        infoSpan:dom.byId(tableId+"Info"),
                        pagingSpan:dom.byId(tableId+"Paging") });
            });

            // Add query parameters to tables.
            UtilGrid.getTable("qaAssignedObjTable").queryParameters = { workState: "QS_UEBERWIESEN", selectionType: null };
            UtilGrid.getTable("qaAssignedAdrTable").queryParameters = { workState: "QS_UEBERWIESEN", selectionType: null };
            UtilGrid.getTable("qaModifiedObjTable").queryParameters = { workState: "IN_BEARBEITUNG", selectionType: null };
            UtilGrid.getTable("qaModifiedAdrTable").queryParameters = { workState: "IN_BEARBEITUNG", selectionType: null };
            UtilGrid.getTable("qaExpiredObjTable").queryParameters = { workState: null, selectionType: "EXPIRED" };
            UtilGrid.getTable("qaExpiredAdrTable").queryParameters = { workState: null, selectionType: "EXPIRED" };
            UtilGrid.getTable("qaSpatialObjTable").queryParameters = { workState: null, selectionType: "SPATIAL_REF_EXPIRED" };

            // Add initial sortParams to tables
            array.forEach(tableIdList, function(tableId) {
                UtilGrid.getTable(tableId).sortParams = { sortBy: "NAME", sortAsc: true };
            });

            // Connect PageNavs with navigate functions
            array.forEach(tableIdList, function(tableId) {
                aspect.after(UtilGrid.getTable(tableId).pageNav, "onPageSelected", function() { navigateTable(tableId); });
            });

            var handle = registry.byId("qaAssignedDSTabContainer").watch("selectedChildWidget", function() {
                registry.byId("qaAssignedAdrTable").reinitLastColumn();
                handle.unwatch();
            });
            var handle2 = registry.byId("qaModifiedDSTabContainer").watch("selectedChildWidget", function() {
                registry.byId("qaModifiedAdrTable").reinitLastColumn();
                handle2.unwatch();
            });
            var handle3 = registry.byId("qaExpiredDSTabContainer").watch("selectedChildWidget", function() {
                registry.byId("qaExpiredAdrTable").reinitLastColumn();
                handle3.unwatch();
            });
        }

        // Init the sort functions
        function initSortFunctions() {
            array.forEach(tableIdList, function(tableId) {
                var table = UtilGrid.getTable(tableId);
                //!!!table.onSort = dojo.curry(table, sortTable, tableId);
                // remove the standard sorter since we sort by starting a new query
                table.createSorter = function(){return null;};
            });
        }


        // If a table header is clicked, the sortTable function is invoked with the corresponding table
        function sortTable(tableId, e) {
            var table = UtilGrid.getTable(tableId);
            var field = this.getFieldFromTableHeaderClickEvent(table, e);
            var sortBy = this.getSortIdentifierFor(field);

            if (sortBy == "") {
                return;

            } else if (sortBy == table.sortParams.sortBy) {
                table.sortParams.sortAsc = !table.sortParams.sortAsc;

            } else {
                table.sortParams.sortBy = sortBy;
                table.sortParams.sortAsc = true;
            }

            this.navigateTable(tableId);
        }


        // Convert table field identifiers to sort identifiers (de.ingrid.mdek.MdekUtils.IdcEntityOrderBy)
        function getSortIdentifierFor(field) {
        //  console.debug("getSortIdentifierFor("+field+") called");

            if (field == "icon") {
                return "CLASS";

            } else if (field == "linkLabel") {
                return "NAME";

            } else if (field == "expiryDate" || field == "date" || field == "assignTime") {
                return "DATE";

            } else if (field == "modUserTitle" || field == "assignerUserTitle") {
                return "USER";

            } else if (field == "state") {
                return "STATE";

            } else {
                return "";
            }
        }

        // Get the column identifier from the ClickEvent.
        function getFieldFromTableHeaderClickEvent(table, e) {
            var source = e.target;
            var row = dojo.html.getParentByType(source,"tr");
            var cellTag = "td";
            if(row.getElementsByTagName(cellTag).length === 0){
                cellTag = "th";
            }

            var headers = row.getElementsByTagName(cellTag);
            var header = dojo.html.getParentByType(source,cellTag);

            for(var i=0; i<headers.length; i++){
                if(headers[i] == header){
                    var meta = table.columns[i];
                    return meta.getField();
                }
            }
            // Field was not found (should not happen), return an empty string
            return "";
        }

        // Reset PageNavs
        function resetAllPageNavs() {
            array.forEach(tableIdList, function(tableId) {
                UtilGrid.getTable(tableId).pageNav.reset();
            });
        }

        // Reset the tables sorting parameters
        function resetAllSortParams() {
            array.forEach(tableIdList, function(tableId) {
                var tableWidget = UtilGrid.getTable(tableId);
                tableWidget.sortParams.sortBy = "NAME";
                tableWidget.sortParams.sortAsc = true;
            });
        }

        // Reload all the tables on the page
        function reloadTables() {
            resetAllPageNavs();
            resetAllSortParams();

            var defList = [];
            array.forEach(tableIdList, function(tableId) {
                defList.push(navigateTable(tableId));
            }, this);

            enterLoadingState();

            all(defList)
            .then(function (resultList) {
                exitLoadingState();
            }, function(err) {
                console.debug("Error: "+err);
                exitLoadingState();
            });
        }

        // NavigateTable should be called whenever the page or sortParameters have changed
        // A new request will be started with the new starHit (from PageNav) and sortParameters (from the Table)
        function navigateTable(tableId) {
            var table = UtilGrid.getTable(tableId);
            var startHit = table.pageNav.getStartHit();

            var def;
            if (tableId.indexOf("Obj") != -1) {
                def = getQAObjects(table.queryParameters.workState, table.queryParameters.selectionType, table.sortParams.sortBy,
                    table.sortParams.sortAsc, startHit, resultsPerPage);

            } else {
                def = getQAAddresses(table.queryParameters.workState, table.queryParameters.selectionType, table.sortParams.sortBy,
                    table.sortParams.sortAsc, startHit, resultsPerPage);
            }

            // After the data is loaded from the backend, update the table and return the result for the
            // following callback functions
            def.then(function(res) {updateTable(tableId, res); return res; });

            return def;
        }


        // Update a table with new data from SearchResult (see de.ingrid.mdek.beans.query.(Object|Address)SearchResultBean)
        // The connected PageNav is updated automatically
        function updateTable(tableId, searchResult) {
            var table = UtilGrid.getTable(tableId);
            var resList = searchResult.resultList;
            var numHits = searchResult.numHits;
            var totalNumHits = searchResult.totalNumHits;

            if (tableId.indexOf("Obj") != -1) {
                addObjectTableIdentifiers(resList);
            } else {
                addAddressTableIdentifiers(resList);
            }
            addCommonTableIdentifiers(resList);

            //table.store.setData(resList);
            UtilStore.updateWriteStore(tableId, resList);
            table.pageNav.setTotalNumHits(totalNumHits);
            table.pageNav.updateDomNodes();
        }

        // Get QA Objects from the backend. The result is returned in a deferred object.
        // workState - de.ingrid.mdek.MdekUtils.WorkState
        // selectionType - de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType
        // sortBy - de.ingrid.mdek.MdekUtils.IdcEntityOrderBy
        // sortAsc - boolean
        // startHit - int, where to start
        // numHits - int, limit number of results
        function getQAObjects(workState, selectionType, sortBy, sortAsc, startHit, numHits) {
            var def = new Deferred();

            ObjectService.getQAObjects(workState, selectionType, sortBy, sortAsc, startHit, numHits, {
                callback: function(result) {
                    def.resolve(result);
                },
                errorHandler: function(errMsg, err) {
                    console.debug("Error: "+errMsg);
                    def.reject(err);
                }
            });
            return def;
        }

        // Get QA Addresses from the backend. The result is returned in a deferred object.
        // workState - de.ingrid.mdek.MdekUtils.WorkState
        // selectionType - de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType
        // sortBy - de.ingrid.mdek.MdekUtils.IdcEntityOrderBy
        // sortAsc - boolean
        // startHit - int, where to start
        // numHits - int, limit number of results
        function getQAAddresses(workState, selectionType, sortBy, sortAsc, startHit, numHits) {
            var def = new Deferred();

            AddressService.getQAAddresses(workState, selectionType, sortBy, sortAsc, startHit, numHits, {
                callback: function(result) {
                    def.resolve(result);
                },
                errorHandler: function(errMsg, err) {
                    console.debug("Error: "+errMsg);
                    def.reject(err);
                }
            });
            return def;
        }

        // Add labels to each dataset in dsList, so they are properly displayed in the table.
        // Properties that will be set:
        // ds.lastEditor, ds.assignerUser, ds.date (last modification), ds.expiryData, ds.userOperation, ds.workState
        function addCommonTableIdentifiers(dsList) {
            if (dsList) {
                array.forEach(dsList, function(ds) {
                    // modUser
                    if (ds.lastEditor) {
                        var modUserAddressTitle = UtilAddress.createAddressTitle(ds.lastEditor);
                        ds.modUserTitle = UtilAddress.createAddressLinkLabel(ds.lastEditor.uuid, modUserAddressTitle);
                    }
                    // assignerUser
                    if (ds.assignerUser) {
                        var assignerUserAddressTitle = UtilAddress.createAddressTitle(ds.assignerUser);
                        ds.assignerUserTitle = UtilAddress.createAddressLinkLabel(ds.assignerUser.uuid, assignerUserAddressTitle);
                    }
                    // date & expiryDate
                    ds.date = dojo.date.locale.parse(ds.modificationTime, {datePattern: "dd.MM.yyyy", selector: "date"});
                    ds.expiryDate = dojo.date.locale.parse(ds.modificationTime, {datePattern: "dd.MM.yyyy", selector: "date"});
                    ds.expiryDate.setDate(ds.expiryDate.getDate()+UtilCatalog.catalogData.expiryDuration);
                    // state
                    if (ds.userOperation) {
                        ds.type = getUserOperationText(ds.userOperation);
                    }
                    if (ds.workState) {
                        ds.state = getWorkStateText(ds.workState);
                    }
                });
            }
        }

        // Add titles to all objects in objList (escape obj.objectName and write it to obj.title)
        function addObjectTableIdentifiers(objList) {
            if (objList) {
                array.forEach(objList, function(obj) {
                    //!!!obj.title = dojo.string.escape("html", obj.objectName);
                });
                UtilList.addObjectLinkLabels(objList);
                UtilList.addIcons(objList);
            }
        }

        // Add titles to all objects in objList (escape adr title and write it to adr.title)
        function addAddressTableIdentifiers(adrList) {
            if (adrList) {
                array.forEach(adrList, function(adr) {
                    adr.title = UtilAddress.createAddressTitle(adr);
                });
                UtilList.addAddressLinkLabels(adrList);
                UtilList.addIcons(adrList);
            }

        }

        // Get The localized text for a userOperation (NEW, EDITED, DELETED)
        function getUserOperationText(userOperation) {
            return message.get('general.userOperation.'+ userOperation);
        }

        // Get The localized text for a workState (Q, R)
        function getWorkStateText(workState) {
            return message.get('general.workState.'+ workState);
        }


        function enterLoadingState() {
            style.set("qaLoadingZone", "display", "block");
            style.set("qaContent", "visibility", "hidden");
        }

        function exitLoadingState() {
            style.set("qaLoadingZone", "display", "none");
            style.set("qaContent", "visibility", "visible");
        }

        function reloadPage() {
            lang.hitch(pageQaA, reloadTables)();
        }

        pageQaA.reloadPage = reloadPage;
        // pageQaA.global = _container_;
    });
</script>
</head>

<body>
    <div class="contentBlockWhite">
        <div id="winNavi">
            <a href="#" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=quality-assurance-0#quality-assurance-0', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
        </div>

        <span class="label" id="qaLoadingZone">
            <div z-index="100">
                <img src="img/ladekreis.gif" style="background-color:#FFFFFF;" />
                <label><fmt:message key="general.loading.data.long" /> ...</label>
            </div>
        </span>

        <div class="content" id="qaContent" style="visibility:hidden;">
        
            <div class="spacer"></div>
            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.qa.refresh" />" onclick="pageQaA.reloadPage()"><fmt:message key="dialog.qa.refresh" /></button>
            <div class="spacer"></div>

            <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7070)"><fmt:message key="dialog.qa.assignedObjects" /></label></span>

            <div id="qaAssignedDSTabContainer" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="qaAssignedObjContentPane">

                <div id="qaAssignedObjContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
                    <div class="inputContainer">
                        <div class="listInfo wide">
                            <span id="qaAssignedObjTableInfo" class="searchResultsInfo">&nbsp;</span>
                            <span id="qaAssignedObjTablePaging" class="searchResultsPaging">&nbsp;</span>
                            <div class="fill"></div>
                        </div>
        
                        <div class="tableContainer">
                            <div id="qaAssignedObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                        </div> <!-- tableContainer end -->
                    </div> <!-- inputContainer end -->
                </div> <!-- ContentPane -->

                <div id="qaAssignedAdrContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.addresses" />" style="overflow:hidden;">
                    <div class="inputContainer">
                        <div class="listInfo wide">
                            <span id="qaAssignedAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
                            <span id="qaAssignedAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
                            <div class="fill"></div>
                        </div>
        
                        <div class="tableContainer">
                            <div id="qaAssignedAdrTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                        </div> <!-- tableContainer end -->
                    </div> <!-- inputContainer end -->
                </div> <!-- ContentPane -->
            </div> <!-- TabContainer -->
            <div class="spacer"></div>
            <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7071)"><fmt:message key="dialog.qa.modifiedObjects" /></label></span>

            <div id="qaModifiedDSTabContainer" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="qaModifiedObjContentPane">

                <div id="qaModifiedObjContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
                    <div class="inputContainer">
                        <div class="listInfo wide">
                            <span id="qaModifiedObjTableInfo" class="searchResultsInfo">&nbsp;</span>
                            <span id="qaModifiedObjTablePaging" class="searchResultsPaging">&nbsp;</span>
                            <div class="fill"></div>
                        </div>
        
                        <div class="tableContainer">
                            <div id="qaModifiedObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                        </div> <!-- tableContainer end -->
                    </div> <!-- inputContainer end -->
                </div> <!-- ContentPane -->

                <div id="qaModifiedAdrContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.addresses" />" style="overflow:hidden;">
                    <div class="inputContainer">
                        <div class="listInfo wide">
                            <span id="qaModifiedAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
                            <span id="qaModifiedAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
                            <div class="fill"></div>
                        </div>
        
                        <div class="tableContainer">
                            <div id="qaModifiedAdrTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                        </div> <!-- tableContainer end -->
                    </div> <!-- inputContainer end -->
                </div> <!-- ContentPane -->
            </div> <!-- TabContainer -->
            <div class="spacer"></div>
            <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7072)"><fmt:message key="dialog.qa.expiredObjects" /></label></span>

            <div id="qaExpiredDSTabContainer" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="qaExpiredObjContentPane">

                <div id="qaExpiredObjContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
                    <div class="inputContainer">
                        <div class="listInfo wide">
                            <span id="qaExpiredObjTableInfo" class="searchResultsInfo">&nbsp;</span>
                            <span id="qaExpiredObjTablePaging" class="searchResultsPaging">&nbsp;</span>
                            <div class="fill"></div>
                        </div>
        
                        <div class="tableContainer">
                            <div id="qaExpiredObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                        </div> <!-- tableContainer end -->
                    </div> <!-- inputContainer end -->
                </div> <!-- ContentPane -->

                <div id="qaExpiredAdrContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.addresses" />" style="overflow:hidden;">
                    <div class="inputContainer">
                        <div class="listInfo wide">
                            <span id="qaExpiredAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
                            <span id="qaExpiredAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
                            <div class="fill"></div>
                        </div>
        
                        <div class="tableContainer">
                            <div id="qaExpiredAdrTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                        </div> <!-- tableContainer end -->
                    </div> <!-- inputContainer end -->
                </div> <!-- ContentPane -->
            </div> <!-- TabContainer -->
            <div class="spacer"></div>

            <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7073)"><fmt:message key="dialog.qa.spatialObjects" /></label></span>

            <div id="qaSpatialObjContentPane" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
                <div class="inputContainer">
                    <div class="listInfo wide">
                        <span id="qaSpatialObjTableInfo" class="searchResultsInfo">&nbsp;</span>
                        <span id="qaSpatialObjTablePaging" class="searchResultsPaging">&nbsp;</span>
                        <div class="fill"></div>
                    </div>
    
                    <div class="tableContainer">
                        <div id="qaSpatialObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
                    </div> <!-- tableContainer end -->
                </div> <!-- inputContainer end -->
            </div> <!-- ContentPane -->

        </div> <!-- content end -->
    </div> <!-- contentBlock end -->
</body>
</html>
