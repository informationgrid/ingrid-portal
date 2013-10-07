<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
            dojo.require("dijit.layout.TabContainer");
        
			var infoData = {};
            var resultsPerPage = 10;
            var pubObjPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("pubObjSearchResultsInfo"), pagingSpan:dojo.byId("pubObjSearchResultsPaging") });
			
			var def1 = getCatalogInfo();
			
			dojo.connect(_container_, "onLoad", function(){
                pubObjPageNav.reset();

				var def2 = createGrids();
				
				var defList = new dojo.DeferredList([def1, def2]);
				defList.addCallback(updateInfo);
				
				dojo.connect(dijit.byId("dashboardTab"), "selectChild", updateInfo);
                
                // if page in navigation clicked then update
                dojo.connect(pubObjPageNav, "onPageSelected", function() { navigatePublishedObjects(); });
			});
			
			function getCatalogInfo() {
				var def = new dojo.Deferred();
				var def2 = new dojo.Deferred();
				
				CatalogService.getCatalogData({
					callback: function(res){
						infoData.catalogName = res.catalogName;
						infoData.location = res.location.name;
						def.callback();
					}
				});
				
				SecurityService.getUserNumForPlugId(null, {
					callback: function(res){
						infoData.numUsers = res;
						def2.callback();
					}
				});
				
				return new dojo.DeferredList([def, def2]);
			}
			
			function createGrids() {
				
				var structure = [
			        { field: 'nodeDocType', name: "&nbsp;", width: '23px', formatter:renderIconClass },
				    { field: 'linkLabel', name: "<fmt:message key='ui.dashboard.name' />", width: '370px' },
					{ field: 'workState', name: "<fmt:message key='ui.dashboard.status' />", width: '105px' },
					{ field: 'modificationTime', name: "<fmt:message key='ui.dashboard.changed' />", width: 'auto' }
			    ];
			    var def1 = createDataGrid("objectInfo", null, structure, dojo.partial(receiveWorkObjects, "PORTAL_QUICKLIST"));
				
                var def5 = createDataGrid("publishedObjectInfo", null, structure, dojo.partial(receivePublishedObjectsResultList, "PORTAL_QUICKLIST_PUBLISHED", 0, resultsPerPage));

				var addrStructure = [
			        { field: 'nodeDocType', name: "&nbsp;", width: '23px',formatter:renderIconClass },
				    { field: 'linkLabel', name: "<fmt:message key='ui.dashboard.name' />", width: '370px' },
					{ field: 'workState', name: "<fmt:message key='ui.dashboard.status' />", width: '105px' },
					{ field: 'modificationTime', name: "<fmt:message key='ui.dashboard.changed' />", width: 'auto' }
			    ];
			    var def2 = createDataGrid("addressInfo", null, addrStructure, dojo.partial(receiveWorkAddresses, "PORTAL_QUICKLIST"));
				
			    var def3 = createDataGrid("globalObjectInfo", null, structure, dojo.partial(receiveWorkObjects, "PORTAL_QUICKLIST_ALL_USERS"));
			    var def4 = createDataGrid("globalAddressInfo", null, addrStructure, dojo.partial(receiveWorkAddresses, "PORTAL_QUICKLIST_ALL_USERS"));
			    
				var defList = new dojo.DeferredList([def1, def2, def3, def4, def5]);
				return defList;
			}
			
			
			function receiveWorkObjects(selection) {
				var def = new dojo.Deferred();
				
				ObjectService.getWorkObjects(selection, "DATE", true, 0, 20, {
					callback: function(res){
					    if (selection == "PORTAL_QUICKLIST") {
					        infoData.numObjects = res.totalNumHits;
					        UtilList.addObjectLinkLabels(res.resultList);
					    } else {
					        infoData.numObjectsAll = res.totalNumHits;
					        UtilList.addObjectLinkLabels(res.resultList, null, true);
					    }
                            
						def.callback(res.resultList);
					}
				});
				return def;
			}
			
            function receivePublishedObjectsResult(selection, startHit, numHits) {
                var def = new dojo.Deferred();
                
                ObjectService.getWorkObjects(selection, "DATE", true, startHit, numHits, {
                    callback: function(res){
                        if (selection == "PORTAL_QUICKLIST_PUBLISHED") {
                            infoData.numObjectsPublished = res.totalNumHits;
                            UtilList.addObjectLinkLabels(res.resultList);
                        } else {
//                            infoData.numObjectsPublishedAll = res.totalNumHits;
                            UtilList.addObjectLinkLabels(res.resultList, null, true);
                        }
                            
                        pubObjPageNav.setTotalNumHits(res.totalNumHits);
                        pubObjPageNav.updateDomNodes();

                        def.callback(res);
                    }
                });
                return def;
            }

            function receivePublishedObjectsResultList(selection, startHit, numHits) {
                var def2 = new dojo.Deferred();

                var def = receivePublishedObjectsResult(selection, startHit, numHits);
                def.addCallback(function(res) {
                    def2.callback(res.resultList);
                });

                return def2;
            }

			function receiveWorkAddresses(selection) {
				var def = new dojo.Deferred();
				AddressService.getWorkAddresses(selection, "DATE", true, 0, 20, {
					callback: function(res){
					    dojo.forEach(res.resultList, function(adr) {
                            adr.title = UtilAddress.createAddressTitle(adr);
                        });
					    
					    if (selection == "PORTAL_QUICKLIST") {
					        infoData.numAddresses = res.totalNumHits;
					        UtilList.addAddressLinkLabels(res.resultList);
					    } else {
					        infoData.numAddressesAll = res.totalNumHits;
					        UtilList.addAddressLinkLabels(res.resultList, true);
					    }
                        
						def.callback(res.resultList);
					}
				});
				return def;
			}
			
            function navigatePublishedObjects() {
                var startHit = pubObjPageNav.getStartHit();

                var def = receivePublishedObjectsResultList("PORTAL_QUICKLIST_PUBLISHED", startHit, resultsPerPage);
                def.addCallback(function(data){
                    UtilGrid.setTableData("publishedObjectInfo", data);
                });
            }

            function updateDashboard() {
                var def1 = receiveWorkObjects("PORTAL_QUICKLIST");
                def1.addCallback(function(data){
                    UtilGrid.setTableData("objectInfo", data);
                });

                pubObjPageNav.reset();
                var def5 = receivePublishedObjectsResultList("PORTAL_QUICKLIST_PUBLISHED", 0, resultsPerPage);
                def5.addCallback(function(data){
                    UtilGrid.setTableData("publishedObjectInfo", data);
                });

                var def2 = receiveWorkAddresses("PORTAL_QUICKLIST");
                def2.addCallback(function(data){
                    UtilGrid.setTableData("addressInfo", data);
                });
                var def3 = receiveWorkObjects("PORTAL_QUICKLIST_ALL_USERS");
                def3.addCallback(function(data){
                    UtilGrid.setTableData("globalObjectInfo", data);
                });
                var def4 = receiveWorkAddresses("PORTAL_QUICKLIST_ALL_USERS");
                def4.addCallback(function(data){
                    UtilGrid.setTableData("globalAddressInfo", data);
                });
                getCatalogInfo().addCallback(updateInfo);
            }
            
			function updateInfo() {
				dojo.byId("catalogName").innerHTML = infoData.catalogName;
				dojo.byId("location").innerHTML = infoData.location;
				if (dijit.byId("dashboardTab").selectedChildWidget.id == "userSpace") {
    				dojo.byId("numObjects").innerHTML = infoData.numObjects;
                    dojo.byId("numObjectsPublished").innerHTML = infoData.numObjectsPublished;
    				dojo.byId("numAddresses").innerHTML = infoData.numAddresses;
				} else {
				    dojo.byId("numObjects").innerHTML = infoData.numObjectsAll;
                    dojo.byId("numObjectsPublished").innerHTML = "";
                    dojo.byId("numAddresses").innerHTML = infoData.numAddressesAll;
				}
				dojo.byId("numUsers").innerHTML = infoData.numUsers;
			}
			
			function renderIconClass(row, cell, value, columnDef, dataContext) {
				return "<div class=\"TreeIcon TreeIcon" + value + "\"></div>";
			}

		</script>
    </head>
    <body>
        <div id="" class="contentBlockWhite top">
	            <div id="winNavi">
	            	<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=dashboard-1#editor-design-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	            </div>
	            <div id="catalogueAdminFields" class="content">
	            <!-- LEFT HAND SIDE CONTENT START -->
                    <div class="inputContainer">
                        <button dojoType="dijit.form.Button" title="<fmt:message key="ui.dashboard.update" />" onClick="javascript:updateDashboard();"><fmt:message key="ui.dashboard.update" /></button>
                    </div>
	               	<div class="infoboxWidth field">
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
                    <div id="dashboardTab" dojoType="dijit.layout.TabContainer" doLayout="false" style="width:100%;" selectedChild="userSpace">
                        <!-- MAIN TAB 1 START -->
                        <div id="userSpace" dojoType="dijit.layout.ContentPane" title="<fmt:message key="ui.dashboard.myObjAdr" />" class="tab" style="overflow:hidden;">
        	                <div class="inputContainer field">
        	                    <h2><fmt:message key="ui.dashboard.title.objectsDraft" /></h2>
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
        	                    <div class="tableContainer">
        							<div id="addressInfo" autoHeight="4" contextMenu="none"></div>
        	                    </div>
        	                </div>
                        </div>
                        <div id="globalSpace" dojoType="dijit.layout.ContentPane" title="<fmt:message key="ui.dashboard.allObjAdr" />" class="tab" style="overflow:hidden;">
                            <div class="inputContainer field">
                                <h2><fmt:message key="ui.dashboard.title.objectsDraft" /></h2>
                                <div class="tableContainer">
                                    <div id="globalObjectInfo" autoHeight="10" contextMenu="none"></div>
                                </div>
                            </div>
                            <div class="inputContainer field">
                                <h2><fmt:message key="ui.dashboard.title.addressesDraft" /></h2>
                                <div class="tableContainer">
                                    <div id="globalAddressInfo" autoHeight="10" contextMenu="none"></div>
                                </div>
                            </div>
                        </div>
                    </div>
	                <div class="inputContainer">
	                    <span class="button"><span style="float:right;">
	                        </span><span id="catalogueFieldSettingsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
	                </div>
            </div><!-- LEFT HAND SIDE CONTENT END -->
        </div><!-- CONTENT END -->
    </body>
</html>