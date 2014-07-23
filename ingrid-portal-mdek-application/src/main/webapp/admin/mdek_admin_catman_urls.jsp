<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">

var pageValidateUrls = _container_;

require([
    "dojo/on",
    "dojo/dom",
    "dojo/dom-style",
    "dijit/registry",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/Deferred",
    "ingrid/utils/Grid",
    "ingrid/layoutCreator",
    "ingrid/utils/String",
    "ingrid/utils/List",
    "ingrid/utils/LoadingZone",
    "ingrid/dialog"
], function(on, dom, style, registry, lang, array, Deferred, UtilGrid, layoutCreator, UtilString, UtilList, LoadingZone, dialog) {
    
        // Use the same store for both tables. The First table has to be reinitialised so the new store
        // gets registered properly
        //var mainStore = registry.byId("urlListTable1").store;
        //registry.byId("urlListTable2").setStore(mainStore);
        
        var urlListTable1Structure = [
        	{field: 'errorCode',name: "<fmt:message key='dialog.admin.catalog.management.urls.error' />",width: '125px'},
        	{field: 'icon',name: '&nbsp;',width: '23px'},
        	{field: 'objectName',name: "<fmt:message key='dialog.admin.catalog.management.urls.objectName' />",width: '214px'},
        	{field: 'url',name: 'URL',width: '374px'},
        	{field: 'urlReferenceDescription',name: "<fmt:message key='dialog.admin.catalog.management.urls.description' />",width: '200px'}
        ];
        layoutCreator.createDataGrid("urlListTable1", null, urlListTable1Structure, null);
        layoutCreator.createDataGrid("urlListTable2", null, lang.clone(urlListTable1Structure), null);
        layoutCreator.createDataGrid("urlListTableCap", null, lang.clone(urlListTable1Structure), null);
        //var data = UtilGrid.getTableData("urlListTable1");
        //UtilGrid.getTableData("urlListTable2").setItems(data);
        //UtilGrid.getTableData("urlListTable2").setFilter(function(item) { return ("VALID" != item.errorCode); });
        
        var urlListTable3Structure = [
            {field: 'errorCode',name: "<fmt:message key='dialog.admin.catalog.management.urls.error' />",width: '120px'},
            {field: 'count',name: "<fmt:message key='dialog.admin.catalog.management.urls.count' />",width: '500px'}
        ];
        layoutCreator.createDataGrid("urlListTable3", null, lang.clone(urlListTable3Structure), null);
            
        on(_container_, "Load", function() {
            // only show textbox and button for replacing url if first two tables are selected
            on(registry.byId("urlList3"), "Show", function() {
                style.set("replaceContainer", "display", "none");
            });
            on(registry.byId("urlList3"), "Hide", function() {
                style.set("replaceContainer", "display", "block");
            });
        
        	// script global variables to remember tab names where the number of read URLs will be attached
            allUrlsTitle = registry.byId("urlList1").title;
            allErrorUrlsTitle = registry.byId("urlList2").title;
            summaryErrorUrlsTitle = registry.byId("urlList3").title;
            capabilitiesUrlsTitle = registry.byId("urlListCap").title;
        	style.set("urlsProgressBarContainer", "display", "none");
        	refreshUrlProcessInfo();
        });
        
        
        pageValidateUrls.startUrlsJob = function() {
        	UtilGrid.setTableData("urlListTable1", []);
        	UtilGrid.setTableData("urlListTable2", []);
            UtilGrid.setTableData("urlListTable3", []);
            UtilGrid.setTableData("urlListTableCap", []);
        
        	CatalogManagementService.startUrlValidatorJob({
        		preHook: LoadingZone.show,
        		postHook: LoadingZone.hide,
        		callback: function() {
        			setTimeout(refreshUrlProcessInfo, 2000);
        		},
        		errorHandler: function(msg, err) {
        		    displayErrorMessage(err);
        			console.debug("Error: "+msg);
        		}
        	});
        };
        
        pageValidateUrls.cancelUrlsJob = function() {
        	CatalogManagementService.stopUrlValidatorJob({
        		errorHandler: function(msg, err) {
        		    displayErrorMessage(err);
        			console.debug("Error: "+msg);
        		}
        	});
        };
        
        pageValidateUrls.replaceUrl = function() {
            var currentTab   = getCurrentTabName();
        	var urlTable     = getCurrentTable();
        	var selectedData = UtilGrid.getSelectedData(urlTable);
        	var replaceUrl   = lang.trim(registry.byId("urlReplace").getValue());
        	// TODO send data to backend
        
        	if (selectedData && selectedData.length != 0 && replaceUrl.length != 0) {
        		var sourceUrls = [];
        		array.forEach(selectedData, function(element) {
        			sourceUrls.push( { objectUuid: element.objectUuid, url:element.url } );
        		});
        
        		var def = replaceUrlDef(sourceUrls, replaceUrl);
        		def.then(function() { return updateDBUrlJobInfoDef(sourceUrls, replaceUrl); });
        		def.then(refreshUrlProcessInfo);
        		def.then(function() { dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.management.urls.success' />", dialog.INFO) });
        	}
        };
        
        function replaceUrlDef(sourceUrls, targetUrl) {
        	var def = new Deferred();
        	var type = (getCurrentTabName() == "urlListCap") ? "capabilities" : "urls"; 
        
        	CatalogManagementService.replaceUrls(sourceUrls, targetUrl, type, {
        		preHook: LoadingZone.show,
        		postHook: LoadingZone.hide,
        		callback: function() {
        			def.resolve();
        		},
        		errorHandler: function(msg, err) {
        		    displayErrorMessage(err);
        			console.debug("Error: "+msg);
        			def.reject();
        		}
        	});
        
        	return def;
        }
        
        function updateDBUrlJobInfoDef(sourceUrls, targetUrl) {
        	var def = new Deferred();
        
        	CatalogManagementService.updateDBUrlJobInfo(sourceUrls, targetUrl, {
        		preHook: LoadingZone.show,
        		postHook: LoadingZone.hide,
        		callback: function() {
        			def.resolve();
        		},
        		errorHandler: function(msg, err) {
        		    displayErrorMessage(err);
        			console.debug("Error: "+msg);
        			def.reject();
        		}
        	});
        
        	return def;
        }
        
        function getCurrentTabName() {
            return registry.byId("urlLists").selectedChildWidget.id;
        }
        
        function getCurrentTable() {
        	var currentTab = getCurrentTabName();
        	if (currentTab == "urlList1") {
        		return "urlListTable1";
        
        	} else if (currentTab == "urlList2") {
        		return "urlListTable2";
            
            } else if (currentTab == "urlList3") {
                return "urlListTable3";
                
            } else if (currentTab == "urlListCap") {
                return "urlListTableCap";
        
        	} else {
        		console.debug("unknown tab selected: '"+currentTab+"'");
        		return null;
        	}
        }
        
        function refreshUrlProcessInfo() {
        	CatalogManagementService.getUrlValidatorJobInfo({
        		preHook: LoadingZone.show,
        		postHook: LoadingZone.hide,
        		callback: function(jobInfo) {
        			updateUrlJobInfo(jobInfo);
        			if (!jobFinished(jobInfo)) {
        				setTimeout(refreshUrlProcessInfo, 3000);
        			} else {
        				updateUrlTables(jobInfo.urlObjectReferences, jobInfo.capabilitiesReferences);
        			}
        		},
        		errorHandler: function(msg, err) {
        		    displayErrorMessage(err);
        			console.debug("Error: "+msg);
        			// If there's a timeout try again
        			if (err.message != "USER_LOGIN_ERROR") {
        			    setTimeout(refreshUrlProcessInfo, 3000);
        			}
        		}
        	});
        };
        
        function jobFinished(jobInfo) {
        	// Job is finished if it has never been started (startTime == null) or it has an end time (endTime != null)
        	return (jobInfo.startTime == null || jobInfo.endTime != null);
        }
        
        function updateUrlJobInfo(jobInfo) {
        	if (jobFinished(jobInfo)) {
        		dom.byId("urlsInfoTitle").innerHTML = "<fmt:message key='dialog.admin.management.urls.lastProcessInfo' />";
        		style.set("urlsProgressBarContainer", "display", "none");
        		style.set("cancelUrlsProcessButton", "display", "none");
        		style.set("urlsInfoNumProcessedUrlsContainer", "display", "none");
        		dom.byId("urlsInfoNumProcessedUrls").innerHTML = "";
        
        		if (jobInfo.endTime) {
        			style.set("urlsInfoEndDateContainer", "display", "block");			
        		}
        
        	} else {
        		dom.byId("urlsInfoTitle").innerHTML = "<fmt:message key='dialog.admin.management.urls.currentProcessInfo' />";
        		style.set("urlsInfoEndDateContainer", "display", "none");
        		style.set("cancelUrlsProcessButton", "display", "block");
        		style.set("urlsInfoNumProcessedUrlsContainer", "display", "block");
        		style.set("urlsProgressBarContainer", "display", "block");
        		dom.byId("urlsInfoNumProcessedUrls").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
        
        		var progressBar = registry.byId("urlsProgressBar");
        		progressBar.set('maximum', (jobInfo.numEntities + jobInfo.numEntities/100)); // so that 100% is never reached!
        		progressBar.set('progress', jobInfo.numProcessedEntities);
        		progressBar.update();
        	}
        
        	if (jobInfo.startTime) {
        		dom.byId("urlsInfoBeginDate").innerHTML = UtilString.getDateString(jobInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
        	} else {
        		dom.byId("urlsInfoBeginDate").innerHTML = "";
        	}
        
        	if (jobInfo.endTime) {
        		dom.byId("urlsInfoEndDate").innerHTML = UtilString.getDateString(jobInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
        	} else {
        		dom.byId("urlsInfoEndDate").innerHTML = "";
        	}
        }
        
        
        function updateUrlTables(urlObjectReferenceList, capabilitiesReferenceList) {
        	if (urlObjectReferenceList) {
        	    console.debug("list-size: " + urlObjectReferenceList.length);
        		UtilList.addIcons(urlObjectReferenceList);
        		UtilList.addIcons(capabilitiesReferenceList);
        		
        		// show the first 100 entries and first 100 errors
        		var maxListSize   = 10000;
        		var errors        = 0;
        		var allErrors     = 0;
        		var capErrors     = 0;
        		var numUrls       = 0;
        		var numCap        = 0;
        		
        		var objList             = new Array();
        		var objErrorList        = new Array();
                var errorSummary        = new Object();
                var objErrorSummaryList = new Array();
                var capabilitiesList = new Array();
        		
                var prepareData = function(data) {
                    var list         = new Array();
                    var errorList    = new Array();
                    //var errorSummary = new Array();
                    var num = 0, errors = 0;
                    
            		for (var i = 0; i < data.length; ++i) {
                        addUrlTableInfo(data[i]);
            		    if (i<maxListSize) {
            			    data[i].objectName = "<a href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\""+data[i].objectUuid+"\", \"O\"); return false;' title='"+data[i].objectName+"' target='_new'>"+data[i].objectName+"</a>";
            			    list.push(data[i]);
            			    num++;
            		    }
            		    // just show in error table those who are not valid but have been checked for invalidity
            		    if (data[i].urlState.state != "VALID" && data[i].urlState.state != "NOT_CHECKED") {
            		        allErrors++;
            		        if (errors < maxListSize) {		        
                		        if (i >= maxListSize) {
                                    data[i].objectName = "<a href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\""+data[i].objectUuid+"\", \"O\"); return false;' title='"+data[i].objectName+"' target='_new'>"+data[i].objectName+"</a>";
                		        }
                		        errorList.push(data[i]);
                		        errors++;
            		        }
                            if (!errorSummary[data[i].errorCode]) {
                                errorSummary[data[i].errorCode] = 0;
                            }
                            errorSummary[data[i].errorCode]++;
            		    }
            		}
            		
            		return [num, errors, list, errorList, errorSummary];
                }
                
                var resultUrls = prepareData(urlObjectReferenceList);
                var resultCaps = prepareData(capabilitiesReferenceList);
        
        		
                for (errorCode in errorSummary) { 
                    objErrorSummaryList.push({'errorCode':errorCode, 'count':errorSummary[errorCode]}); 
                }
        
        		registry.byId("urlList1").set('title', allUrlsTitle + " (" + /*numUrls + "/" +*/ urlObjectReferenceList.length + ")");
                registry.byId("urlList2").set('title', allErrorUrlsTitle + " (" + /*errors + "/" +*/ resultUrls[1] + ")");
                registry.byId("urlList3").set('title', summaryErrorUrlsTitle/* + " (" + allErrors + ")"*/);
                registry.byId("urlListCap").set('title', capabilitiesUrlsTitle + " (" + resultCaps[1] + ")");
            
                //UtilGrid.generateIDs(objList, "_id");
                UtilGrid.setTableData("urlListTable1", resultUrls[2]);
                UtilGrid.setTableData("urlListTable2", array.filter(resultUrls[2], function(item) {return ("VALID" != item.errorCode);}));
                UtilGrid.setTableData("urlListTable3", objErrorSummaryList);
                UtilGrid.setTableData("urlListTableCap", resultCaps[3]);
        	} else {
        		UtilGrid.setTableData("urlListTable1", []);
        		UtilGrid.setTableData("urlListTable2", []);
                UtilGrid.setTableData("urlListTable3", []);
                UtilGrid.setTableData("urlListTableCap", []);
        	}
        	UtilGrid.getTable("urlListTable1").invalidate();
            UtilGrid.getTable("urlListTable2").invalidate();
            UtilGrid.getTable("urlListTableCap").invalidate();
            //UtilGrid.getTable("urlListTable3").invalidate();
        	UtilGrid.getTable("urlListTable1").resizeCanvas();
        	UtilGrid.getTable("urlListTable2").resizeCanvas();
        	UtilGrid.getTable("urlListTableCap").resizeCanvas();
            //UtilGrid.getTable("urlListTable3").resizeCanvas();
        }
        
        function addUrlTableInfo(urlObjectReference) {
        	urlObjectReference.url = urlObjectReference.urlState.url;
        	if (urlObjectReference.urlState.state == "HTTP_ERROR") {
        		urlObjectReference.errorCode = urlObjectReference.urlState.state + "("+urlObjectReference.urlState.responseCode+")";
        	} else {
        		urlObjectReference.errorCode = urlObjectReference.urlState.state;
        	}
        }

});
</script>
</head>

<body>
<!-- CONTENT START -->
	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-3#overall-catalog-management-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<div id="urlsContent" class="content">

			<!-- INFO START -->
			<div class="inputContainer">
				<button data-dojo-type="dijit/form/Button" id="urlStartAnalysis" title="<fmt:message key="dialog.admin.catalog.management.urls.refresh" />" onclick="pageValidateUrls.startUrlsJob()"><fmt:message key="dialog.admin.catalog.management.urls.refresh" /></button>
			</div>

			<div class="inputContainer noSpaceBelow">
				<div id="urlsProcessInfo" class="infobox">
					<span class="icon"><img src="img/ic_info_download.gif" width="16" height="16" alt="Info" /></span>
					<span id="urlsInfoTitle" class="title"></span>
					<div id="urlsProcessInfoContent">
						<table cellspacing="0">
							<tr>
								<td><fmt:message key="dialog.admin.catalog.management.urls.startTime" /></td>
								<td id="urlsInfoBeginDate"></td></tr>
								<tr><td id="urlsInfoEndDateContainer"><fmt:message key="dialog.admin.catalog.management.urls.endTime" /></td>
								<td id="urlsInfoEndDate"></td></tr>
								<tr><td id="urlsInfoNumProcessedUrlsContainer"><fmt:message key="dialog.admin.catalog.management.urls.numUrls" /></td>
								<td id="urlsInfoNumProcessedUrls"></td></tr>
								<tr><td id="urlsProgressBarContainer" colspan=2><div data-dojo-type="dijit/ProgressBar" id="urlsProgressBar" width="310" height="10" /></td></tr>
						</table>
						<span id="cancelUrlsProcessButton" class="button">
							<span style="float:right;">
								<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.urls.cancel" />" onclick="pageValidateUrls.cancelUrlsJob()"><fmt:message key="dialog.admin.catalog.management.urls.cancel" /></button>
							</span>
						</span>
					</div> <!-- processInfoContent end -->
				</div> <!-- processInfo end -->
			</div> <!-- inputContainer end -->


			<!-- LEFT HAND SIDE CONTENT START -->
			<div id="urlListContainer" class="inputContainer noSpaceBelow" style="padding-top:15px;">
				<span class="label required"><fmt:message key="dialog.admin.catalog.management.urls.result" /></span>
				<div id="urlLists" data-dojo-type="dijit/layout/TabContainer" style="height:500px;" selectedChild="urlList1">

					<!-- TAB 1 START -->
					<div id="urlList1" data-dojo-type="dijit/layout/ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.allUrls" />">
					    <div>
							<div id="urlListTable1" autoHeight="20" forceGridHeight="true"  contextMenu="none" query=""></div>
                        </div>
					</div> <!-- TAB 1 END -->
        		
					<!-- TAB 2 START -->
					<div id="urlList2" data-dojo-type="dijit/layout/ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.invalidUrls" />">
							<div id="urlListTable2" autoHeight="20" forceGridHeight="true"  contextMenu="none" query=""></div>
					</div> <!-- TAB 2 END -->
                    <!-- TAB Capabilities START -->
                    <div id="urlListCap" data-dojo-type="dijit/layout/ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.capabilities" />">
                        <div id="urlListTableCap" autoHeight="20" forceGridHeight="true" contextMenu="none"></div>
                    </div> <!-- TAB Capabilities END -->
                    <!-- TAB Summary START -->
                    <div id="urlList3" data-dojo-type="dijit/layout/ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.summaryUrls" />">
                        <div id="urlListTable3" autoHeight="20" forceGridHeight="true" contextMenu="none"></div>
                    </div> <!-- TAB Summary END -->
				</div>
			</div>
			<div id="replaceContainer" class="inputContainer grey field">
			    <span class="outer"><div>
				<span class="label">
				    <label for="urlReplace" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8034)"><fmt:message key="dialog.admin.catalog.management.urls.replaceUrlsWith" /></label>
                </span>
				<span class="input">
					<input type="text" id="urlReplace" maxLength="255" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;"/>
				</span>
				<span style="float:right;">
					<button data-dojo-type="dijit/form/Button" id="urlBtnReplace" title="<fmt:message key="dialog.admin.catalog.management.urls.replace" />" onclick="pageValidateUrls.replaceUrl()"><fmt:message key="dialog.admin.catalog.management.urls.replace" /></button>
				</span>
				<span id="replaceUrlLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
					<img src="img/ladekreis.gif" />
				</span>
                </div></span>
                <div class="fill"></div>
			</div>
		</div>
	</div>
<!-- CONTENT END -->

</body>
</html>
