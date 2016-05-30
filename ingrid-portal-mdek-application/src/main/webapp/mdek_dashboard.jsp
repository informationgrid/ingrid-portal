<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        
    </head>
    <body>
        <div id="" class="contentBlockWhite top">
	            <div id="winNavi">
	            	<a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=dashboard-1#editor-design-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	            </div>
	            <div id="catalogueAdminFields" class="content">
	            <!-- LEFT HAND SIDE CONTENT START -->
                    <div class="inputContainer">
                        <button data-dojo-type="dijit/form/Button" id="btnUpdateDashboard" title="<fmt:message key="ui.dashboard.update" />" onclick="pageDashboard.updateDashboard()"><fmt:message key="ui.dashboard.update" /></button>
                    </div>
	               	<div class="infoboxWidth">
	               		<div class="infobox">
			                <table border="0">
								<tr>
									<td><fmt:message key="ui.dashboard.catalogName" />:</td>
									<td id="catalogName"></td>
								</tr>
								<tr>
									<td><fmt:message key="ui.dashboard.location" />:</td>
									<td id="location"></td>
								</tr>
								<tr>
									<td><fmt:message key="ui.dashboard.numObjects" />:</td>
									<td id="numObjects"></td>
								</tr>
                                <tr>
                                    <td><fmt:message key="ui.dashboard.numObjectsPublished" />:</td>
                                    <td id="numObjectsPublished"></td>
                                </tr>
								<tr>
									<td><fmt:message key="ui.dashboard.numAddresses" />:</td>
									<td id="numAddresses"></td>
								</tr>
								<tr>
									<td><fmt:message key="ui.dashboard.numUser" />:</td>
									<td id="numUsers"></td>
								</tr>
							</table>
						</div>
					</div>
                    <div id="dashboardTab" data-dojo-type="dijit/layout/TabContainer" doLayout="false" style="width:100%;" selectedChild="userSpace">
                        <!-- MAIN TAB 1 START -->
                        <div id="userSpace" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="ui.dashboard.myObjAdr" />" class="tab" style="overflow:hidden;">
        	                <div class="inputContainer field">
        	                    <h2><fmt:message key="ui.dashboard.title.objectsDraft" /></h2>
                                <div class="listInfo">
                                    <span id="workObjSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                                    <span id="workObjSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                                    <div class="fill"></div>
                                </div>
        	                    <div class="tableContainer">
        							<div id="objectInfo" autoHeight="6" contextMenu="none"></div>
        	                    </div>
        	                </div>
                            <div class="inputContainer field" style="padding-top:2px">
                                <h2><fmt:message key="ui.dashboard.title.objectsPublished" /></h2>
                                <div class="listInfo">
                                    <span id="pubObjSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                                    <span id="pubObjSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                                    <div class="fill"></div>
                                </div>
                                <div class="tableContainer">
                                    <div id="publishedObjectInfo" autoHeight="6" contextMenu="none"></div>
                                </div>
                            </div>
        					<div class="inputContainer field">
        	                    <h2><fmt:message key="ui.dashboard.title.addressesDraft" /></h2>
                                <div class="listInfo">
                                    <span id="workAddrSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                                    <span id="workAddrSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                                    <div class="fill"></div>
                                </div>
        	                    <div class="tableContainer">
        							<div id="addressInfo" autoHeight="4" contextMenu="none"></div>
        	                    </div>
        	                </div>
                        </div>
                        <div id="globalSpace" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="ui.dashboard.allObjAdr" />" class="tab" style="overflow:hidden;">
                            <div class="inputContainer field">
                                <h2><fmt:message key="ui.dashboard.title.objectsDraft" /></h2>
                                <div class="listInfo">
                                    <span id="globalWorkObjSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                                    <span id="globalWorkObjSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                                    <div class="fill"></div>
                                </div>
                                <div class="tableContainer">
                                    <div id="globalObjectInfo" autoHeight="10" contextMenu="none"></div>
                                </div>
                            </div>
                            <div class="inputContainer field">
                                <h2><fmt:message key="ui.dashboard.title.addressesDraft" /></h2>
                                <div class="listInfo">
                                    <span id="globalWorkAddrSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                                    <span id="globalWorkAddrSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                                    <div class="fill"></div>
                                </div>
                                <div class="tableContainer">
                                    <div id="globalAddressInfo" autoHeight="10" contextMenu="none"></div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div><!-- LEFT HAND SIDE CONTENT END -->
        </div><!-- CONTENT END -->
        
        
        <script type="text/javascript">
            var pageDashboard = _container_;
            require([
                "dojo/parser",
                "ingrid/utils/PageNavigation",
                "ingrid/utils/Grid",
                "dojo/aspect",
                "dojo/_base/array",
                "dojo/_base/lang",
                "dojo/promise/all",
                "dojo/Deferred",
                "dijit/registry",
                "dojo/dom",
                "dojo/on",
                "ingrid/layoutCreator",
                "ingrid/utils/List",
                "ingrid/utils/Address",
                "ingrid/utils/LoadingZone",
                "ingrid/grid/CustomGridFormatters",
                "dojo/domReady!"
            ], function(parser, navigation, UtilGrid, aspect, array, lang, all, Deferred, registry, dom, on, layoutCreator, UtilList, UtilAddress, LoadingZone, gridFormatters) {
                
                var infoData = {};
                var resultsPerPage = 10;
                var pubObjPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("pubObjSearchResultsInfo"), pagingSpan:dom.byId("pubObjSearchResultsPaging") });
                var workObjPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("workObjSearchResultsInfo"), pagingSpan:dom.byId("workObjSearchResultsPaging") });
                var workAddrPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("workAddrSearchResultsInfo"), pagingSpan:dom.byId("workAddrSearchResultsPaging") });
                var globalWorkObjPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("globalWorkObjSearchResultsInfo"), pagingSpan:dom.byId("globalWorkObjSearchResultsPaging") });
                var globalWorkAddrPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("globalWorkAddrSearchResultsInfo"), pagingSpan:dom.byId("globalWorkAddrSearchResultsPaging") });
                
                parser.parse();
                
                on(_container_, "load", function() {
                    try {
                        console.log("loaded");
                        LoadingZone.show();
                        var def1 = getCatalogInfo();
                        
                        pubObjPageNav.reset();workObjPageNav.reset();workAddrPageNav.reset();
                        globalWorkObjPageNav.reset();globalWorkAddrPageNav.reset();
                        var def2 = createGrids();
                        
                        all([def1, def2]).then(updateInfo)
                        .then(function() {
                            registry.byId("dashboardTab").resize();
                            registry.byId("objectInfo").reinitLastColumn(true);
                            registry.byId("publishedObjectInfo").reinitLastColumn(true);
                            registry.byId("addressInfo").reinitLastColumn(true);
                        });

                        // make sure grids are rendered correctly after initially shown
                        var tab = registry.byId("dashboardTab");
                        var handle = tab.watch("selectedChildWidget", function() {
                            registry.byId("globalObjectInfo").reinitLastColumn();
                            registry.byId("globalAddressInfo").reinitLastColumn();
                            handle.unwatch();
                        });

                        tab.watch("selectedChildWidget", updateInfo);

                        // if page in navigation clicked then update
                        aspect.after( pubObjPageNav, "onPageSelected", navigatePublishedObjects );
                        aspect.after( workObjPageNav, "onPageSelected", navigateWorkingObjects );
                        aspect.after( workAddrPageNav, "onPageSelected", navigateWorkingAddresses );
                        aspect.after( globalWorkObjPageNav, "onPageSelected", navigateGlobalWorkingObjects );
                        aspect.after( globalWorkAddrPageNav, "onPageSelected", navigateGlobalWorkingAddresses );
                    } catch(error) {
                        console.error("Error:", error);
                    }
                });
            
                function getCatalogInfo() {
                    var def = new Deferred();
                    var def2 = new Deferred();
                    
                    CatalogService.getCatalogData({
                        callback: function(res){
                            infoData.catalogName = res.catalogName;
                            infoData.location = res.location.name;
                            def.resolve();
                        },
                        errorHandler: function(mes) {
                            def.reject();
                        }
                    });
                    SecurityService.getUserNumForPlugId(null, {
                        callback: function(res){
                            infoData.numUsers = res;
                            def2.resolve();
                        },
                        errorHandler: function(mes) {
                            def.reject();
                        }
                    });
                    
                    return all([def, def2]);
                }
            
                function createGrids() {
                    var structure = [
                        { field: 'nodeDocType', name: "&nbsp;", width: '23px', formatter:gridFormatters.renderIconClass },
                        { field: 'linkLabel', name: "<fmt:message key='ui.dashboard.name' />", width: '370px' },
                        { field: 'workState', name: "<fmt:message key='ui.dashboard.status' />", width: '105px' },
                        { field: 'modificationTime', name: "<fmt:message key='ui.dashboard.changed' />", width: 'auto' }
                    ];
                    var def1 = layoutCreator.createDataGrid("objectInfo", null, structure, lang.partial(receiveWorkObjects, "PORTAL_QUICKLIST", 0, resultsPerPage));
                    
                    var def5 = layoutCreator.createDataGrid("publishedObjectInfo", null, structure, lang.partial(receivePublishedObjectsResultList, "PORTAL_QUICKLIST_PUBLISHED", 0, resultsPerPage));
    
                    var addrStructure = [
                        { field: 'nodeDocType', name: "&nbsp;", width: '23px',formatter:gridFormatters.renderIconClass },
                        { field: 'linkLabel', name: "<fmt:message key='ui.dashboard.name' />", width: '370px' },
                        { field: 'workState', name: "<fmt:message key='ui.dashboard.status' />", width: '105px' },
                        { field: 'modificationTime', name: "<fmt:message key='ui.dashboard.changed' />", width: 'auto' }
                    ];
                    var def2 = layoutCreator.createDataGrid("addressInfo", null, addrStructure, lang.partial(receiveWorkAddresses, "PORTAL_QUICKLIST", 0, resultsPerPage));
                    
                    var def3 = layoutCreator.createDataGrid("globalObjectInfo", null, structure, lang.partial(receiveWorkObjects, "PORTAL_QUICKLIST_ALL_USERS", 0, resultsPerPage));
                    var def4 = layoutCreator.createDataGrid("globalAddressInfo", null, addrStructure, lang.partial(receiveWorkAddresses, "PORTAL_QUICKLIST_ALL_USERS", 0, resultsPerPage));

                    return all([def1, def2, def3, def4, def5]);
                }
                
                
                function receiveWorkObjects(selection, startHit, numHits) {
                    var def = new Deferred();
                    ObjectService.getWorkObjects(selection, "DATE", true, startHit, numHits, {
                        callback: function(res){
                            if (selection == "PORTAL_QUICKLIST") {
                                infoData.numObjects = res.totalNumHits;
                                UtilList.addObjectLinkLabels(res.resultList);
                                workObjPageNav.setTotalNumHits(res.totalNumHits);
                                workObjPageNav.updateDomNodes();
                            } else {
                                infoData.numObjectsAll = res.totalNumHits;
                                UtilList.addObjectLinkLabels(res.resultList, null, true);
                                globalWorkObjPageNav.setTotalNumHits(res.totalNumHits);
                                globalWorkObjPageNav.updateDomNodes();
                            }
                            
                            def.resolve(res.resultList);
                        },
                        errorHandler: function(mes) {
                            def.reject();
                        }
                    });
                    return def;
                }
                
                function receivePublishedObjectsResult(selection, startHit, numHits) {
                    var def = new Deferred();
                    
                    ObjectService.getWorkObjects(selection, "DATE", true, startHit, numHits, {
                        callback: function(res){
                            if (selection == "PORTAL_QUICKLIST_PUBLISHED") {
                                infoData.numObjectsPublished = res.totalNumHits;
                                UtilList.addObjectLinkLabels(res.resultList);
                                pubObjPageNav.setTotalNumHits(res.totalNumHits);
                                pubObjPageNav.updateDomNodes();
                            } else {
    //                            infoData.numObjectsPublishedAll = res.totalNumHits;
                                UtilList.addObjectLinkLabels(res.resultList, null, true);
                            }
                                
                            def.resolve(res);
                        },
                        errorHandler: function(mes) {
                            def.reject();
                        }
                    });
                    return def.promise;
                }
    
                function receivePublishedObjectsResultList(selection, startHit, numHits) {
                    var def = new Deferred();
    
                    receivePublishedObjectsResult(selection, startHit, numHits)
                    .then(function(res) {
                        def.resolve( res.resultList );
                    });
                    return def;
                }
    
                function receiveWorkAddresses(selection, startHit, numHits) {
                    var def = new Deferred();
                    AddressService.getWorkAddresses(selection, "DATE", true, startHit, numHits, {
                        callback: function(res){
                            array.forEach(res.resultList, function(adr) {
                                adr.title = UtilAddress.createAddressTitle(adr);
                            });
                            
                            if (selection == "PORTAL_QUICKLIST") {
                                infoData.numAddresses = res.totalNumHits;
                                UtilList.addAddressLinkLabels(res.resultList);
                                workAddrPageNav.setTotalNumHits(res.totalNumHits);
                                workAddrPageNav.updateDomNodes();
                            } else {
                                infoData.numAddressesAll = res.totalNumHits;
                                UtilList.addAddressLinkLabels(res.resultList, true);
                                globalWorkAddrPageNav.setTotalNumHits(res.totalNumHits);
                                globalWorkAddrPageNav.updateDomNodes();
                            }
                            def.resolve(res.resultList);
                        },
                        errorHandler: function(mes) {
                            def.reject();
                        }
                    });
                    return def;
                }
                
                function navigatePublishedObjects() {
                    var startHit = pubObjPageNav.getStartHit();
    
                    receivePublishedObjectsResultList("PORTAL_QUICKLIST_PUBLISHED", startHit, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("publishedObjectInfo", data);
                    });
                }
                function navigateWorkingObjects() {
                    var startHit = workObjPageNav.getStartHit();
    
                    receiveWorkObjects("PORTAL_QUICKLIST", startHit, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("objectInfo", data);
                    });
                }
                function navigateWorkingAddresses() {
                    var startHit = workAddrPageNav.getStartHit();
    
                    receiveWorkAddresses("PORTAL_QUICKLIST", startHit, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("addressInfo", data);
                    });
                }
                function navigateGlobalWorkingObjects() {
                    var startHit = globalWorkObjPageNav.getStartHit();
    
                    receiveWorkObjects("PORTAL_QUICKLIST_ALL_USERS", startHit, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("globalObjectInfo", data);
                    });
                }
                function navigateGlobalWorkingAddresses() {
                    var startHit = globalWorkAddrPageNav.getStartHit();
    
                    receiveWorkAddresses("PORTAL_QUICKLIST_ALL_USERS", startHit, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("globalAddressInfo", data);
                    });
                }
    
                // this function has to be available as an onclick-handler
                function updateDashboard() {
                    LoadingZone.show();

                    receiveWorkObjects("PORTAL_QUICKLIST", 0, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("objectInfo", data);
                    });
    
                    pubObjPageNav.reset();workObjPageNav.reset();workAddrPageNav.reset();
                    globalWorkObjPageNav.reset();globalWorkAddrPageNav.reset();
                    receivePublishedObjectsResultList("PORTAL_QUICKLIST_PUBLISHED", 0, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("publishedObjectInfo", data);
                    });
    
                    receiveWorkAddresses("PORTAL_QUICKLIST", 0, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("addressInfo", data);
                    });
                    receiveWorkObjects("PORTAL_QUICKLIST_ALL_USERS", 0, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("globalObjectInfo", data);
                    });
                    receiveWorkAddresses("PORTAL_QUICKLIST_ALL_USERS", 0, resultsPerPage)
                    .then(function(data){
                        UtilGrid.setTableData("globalAddressInfo", data);
                    });
                    getCatalogInfo().then(updateInfo);
                }
                
                function updateInfo() {
                    dom.byId("catalogName").innerHTML = infoData.catalogName;
                    dom.byId("location").innerHTML = infoData.location;
                    if (registry.byId("dashboardTab").selectedChildWidget.id == "userSpace") {
                        dom.byId("numObjects").innerHTML = infoData.numObjects;
                        dom.byId("numObjectsPublished").innerHTML = infoData.numObjectsPublished;
                        dom.byId("numAddresses").innerHTML = infoData.numAddresses;
                    } else {
                        dom.byId("numObjects").innerHTML = infoData.numObjectsAll;
                        dom.byId("numObjectsPublished").innerHTML = "";
                        dom.byId("numAddresses").innerHTML = infoData.numAddressesAll;
                    }
                    dom.byId("numUsers").innerHTML = infoData.numUsers;
                    LoadingZone.hide();
                }

                /**
                 * PUBLIC METHODS
                 */
                
                pageDashboard.updateDashboard = updateDashboard;
            });

        </script>
        
    </body>
</html>
