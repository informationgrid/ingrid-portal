<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
			var infoData = {};
			
			var def1 = getCatalogInfo();
			
			dojo.connect(_container_, "onLoad", function(){
				var def2 = createGrids();
				//var def1 = getObjectInformation();
				
				var defList = new dojo.DeferredList([def1, def2]);
				defList.addCallback(updateInfo);
			});
			
			function getCatalogInfo() {
				var def = new dojo.Deferred();
				var def2 = new dojo.Deferred();
				
				CatalogService.getCatalogData({
					callback: function(res){
						infoData.catalogName = res.catalogName;
						infoData.location = res.location.name;
						//infoData.numUser = res.totalNumHits;
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
			    var def1 = createDataGrid("objectInfo", null, structure, receiveWorkObjects);
				
				var addrStructure = [
			        { field: 'nodeDocType', name: "&nbsp;", width: '23px',formatter:renderIconClass },
				    { field: 'linkLabel', name: "<fmt:message key='ui.dashboard.name' />", width: '370px' },
					{ field: 'workState', name: "<fmt:message key='ui.dashboard.status' />", width: '105px' },
					{ field: 'modificationTime', name: "<fmt:message key='ui.dashboard.changed' />", width: 'auto' }
			    ];
			    var def2 = createDataGrid("addressInfo", null, addrStructure, receiveWorkAddresses);
				
				var defList = new dojo.DeferredList([def1, def2]);
				return defList;
			}
			
			
			function receiveWorkObjects() {
				var def = new dojo.Deferred();
				
				ObjectService.getWorkObjects("PORTAL_QUICKLIST", "DATE", true, 0, 20, {
					callback: function(res){
						infoData.numObjects = res.totalNumHits;
                        UtilList.addObjectLinkLabels(res.resultList);
						def.callback(res.resultList);
					}
				});
				return def;
			}
			
			function receiveWorkAddresses() {
				var def = new dojo.Deferred();
				AddressService.getWorkAddresses("PORTAL_QUICKLIST", "DATE", true, 0, 20, {
					callback: function(res){
						infoData.numAddresses = res.totalNumHits;
                        
                        dojo.forEach(res.resultList, function(adr) {
                            adr.title = UtilAddress.createAddressTitle(adr);
                        });
                        
                        UtilList.addAddressLinkLabels(res.resultList);
						def.callback(res.resultList);
					}
				});
				return def;
			}
			
            function updateDashboard() {
                var def1 = receiveWorkObjects();
                def1.addCallback(function(data){
                    UtilGrid.setTableData("objectInfo", data);
                });
                var def2 = receiveWorkAddresses();
                def2.addCallback(function(data){
                    UtilGrid.setTableData("addressInfo", data);
                });
                getCatalogInfo().addCallback(updateInfo);
            }
            
			function updateInfo() {
				dojo.byId("catalogName").innerHTML = infoData.catalogName;
				dojo.byId("location").innerHTML = infoData.location;
				dojo.byId("numObjects").innerHTML = infoData.numObjects;
				dojo.byId("numAddresses").innerHTML = infoData.numAddresses;
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
	            	<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=editor-design-1#editor-design-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
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
	                <div class="inputContainer field">
	                    <h2><fmt:message key="ui.dashboard.title.objectsDraft" /></h2>
	                    <div class="tableContainer">
							<div id="objectInfo" autoHeight="10" contextMenu="none"></div>
	                    </div>
	                </div>
					<div class="inputContainer field">
	                    <h2><fmt:message key="ui.dashboard.title.addressesDraft" /></h2>
	                    <div class="tableContainer">
							<div id="addressInfo" autoHeight="10" contextMenu="none"></div>
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