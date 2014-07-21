<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var pageLocations = _container_;

require([
    "dojo/on",
    "dojo/aspect",
    "dojo/dom",
    "ingrid/layoutCreator",
    "ingrid/utils/LoadingZone",
    "ingrid/utils/Store",
    "ingrid/utils/PageNavigation",
    "ingrid/utils/List",
    "ingrid/utils/UI",
    "ingrid/dialog"
], function(on, aspect, dom, layoutCreator, LoadingZone, UtilStore, navigation, UtilList, UtilUI, dialog) {
    
        var resultsPerPage = 20;
        var pageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("snsLocationUpdateResultInfo"), pagingSpan:dom.byId("snsLocationUpdateResultPaging") });
        
        var snsLocationUpdateResult = [];
        
        createDOMElements();
        
        on(_container_, "Load", function(){
        	refreshSNSLocationUpdateProcessInfo();
        	aspect.after(pageNav, "onPageSelected", updateSNSLocationTable);
        });
        
        
        function createDOMElements(){
        	var locationsResultTableStructure = [
        		{field: 'title',name: "<fmt:message key='dialog.admin.catalog.management.locations.spatialRef' />",width: '370px', editable: true},
        		{field: 'code',name: "<fmt:message key='dialog.admin.catalog.management.locations.id' />",width: '80px', editable: true},
        		{field: 'action',name: "<fmt:message key='dialog.admin.catalog.management.locations.action' />",width: '454px', editable: true},
        		{field: 'objects',name: "<fmt:message key='dialog.admin.catalog.management.locations.objects' />",width: 'auto', editable: true}
        	];
            
            layoutCreator.createDataGrid("locationsResultTable", null, locationsResultTableStructure, null);
        }
        
        pageLocations.startSNSLocationUpdateJob = function() {
        	var file = dwr.util.getValue("snsLocationUpdateFile");
        
	CatalogManagementService.startSNSLocationUpdateJob(userLocale, {
        		preHook: showLoadingZone,
                postHook: hideLoadingZone,        
        		callback: function() {
        			setTimeout(refreshSNSLocationUpdateProcessInfo, 1000);
        			dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.locations.jobStartHint' />", dialog.INFO);
        		},
        		errorHandler: function(errMsg, err) {
        		    displayErrorMessage(err);
        			console.debug("error: " + errMsg);
        		}
        	});
        }
        
        pageLocations.cancelSNSLocationUpdateJob = function() {
        	CatalogManagementService.stopSNSLocationUpdateJob( {
        		callback: function() {
        			console.debug("Job stopped.");
        		},
        		errorHandler: function(errMsg, err) {
        		    displayErrorMessage(err);
        			console.debug("error: " + errMsg);
        		}
        	});
        }
        
        
        function refreshSNSLocationUpdateProcessInfo() {
        	CatalogManagementService.getSNSLocationUpdateJobInfo( {
                preHook: showLoadingZone,
                postHook: hideLoadingZone,
        		callback: function(jobInfo){
        			updateSNSLocationUpdateJobInfo(jobInfo);
        			if (!jobFinished(jobInfo)) {
        				setTimeout(refreshSNSLocationUpdateProcessInfo, 1000);
        			}
        		},
        		errorHandler: function(message, err) {
        		    displayErrorMessage(err);
        			console.log("Error: "+ message);
        			// If there's a timeout try again
        			if (err.message != "USER_LOGIN_ERROR") {
        			    setTimeout(refreshSNSLocationUpdateProcessInfo, 1000);
        			}
        		}
        	});
        };
        
        function updateSNSLocationUpdateJobInfo(jobInfo) {
        	if (jobFinished(jobInfo)) {
        		hideLoadingZone();
        
        		dom.byId("snsLocationUpdateProcessInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.locations.updateLastProcessInfo' />";
        		//dojo.html.setVisibility("cancelSNSLocationUpdateProcessButton", false);
                dom.byId("cancelSNSLocationUpdateProcessButton").style.visibility = "hidden";
        
        		if (jobInfo.startTime == null) {
        			// Job was never executed
        			dom.byId("snsLocationUpdateProcessStart").innerHTML = "";
        			dom.byId("snsLocationUpdateProcessEnd").innerHTML = "";
        			dom.byId("snsLocationUpdateProcessNumEntities").innerHTML = "";
        
        		} else {
        			dom.byId("snsLocationUpdateProcessStart").innerHTML = jobInfo.startTime;
        			dom.byId("snsLocationUpdateProcessEnd").innerHTML = jobInfo.endTime;
        			dom.byId("snsLocationUpdateProcessNumEntities").innerHTML = jobInfo.numEntities;
        		}
        
        		// TODO Remove test code
        		var numTerms = jobInfo.snsUpdateResults.length;
        		snsLocationUpdateResult = jobInfo.snsUpdateResults;
        
        		pageNav.reset();
        		pageNav.setTotalNumHits(numTerms);
        		updateSNSLocationTable();
        
        	} else {
        		showLoadingZone();
        		//dojo.html.setVisibility("cancelSNSLocationUpdateProcessButton", true);
                dom.byId("cancelSNSLocationUpdateProcessButton").style.visibility = "visible";
        		dom.byId("snsLocationUpdateProcessInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.locations.updateCurrentProcessInfo' />";
        		dom.byId("snsLocationUpdateProcessStart").innerHTML = jobInfo.startTime;
        		dom.byId("snsLocationUpdateProcessEnd").innerHTML = "";
        		dom.byId("snsLocationUpdateProcessNumEntities").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
        	}
        }
        
        function jobFinished(jobInfo) {
        	return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
        }
        
        function showLoadingZone() {
            //dojo.html.setVisibility("snsLocationUpdateLoadingZone", true);
            dom.byId("snsLocationUpdateLoadingZone").style.visibility = "visible";
        }
        
        function hideLoadingZone() {
            //dojo.html.setVisibility("snsLocationUpdateLoadingZone", false);
            dom.byId("snsLocationUpdateLoadingZone").style.visibility = "hidden";
        }
        
        // Paging
        function updateSNSLocationTable() {
        	var startHit = pageNav.getStartHit();
        	var currentView = snsLocationUpdateResult.slice(startHit, startHit + resultsPerPage);
        	//registry.byId("locationsResultTable").store.setData(currentView);
        	UtilStore.updateWriteStore("locationsResultTable", currentView);
        	pageNav.updateDomNodes();
        }
        
        pageLocations.downloadAsCSV = function() {
        	CatalogManagementService.getSNSLocationUpdateResultAsCSV({
        		callback: function(csvFile) {
        			dwr.engine.openInDownload(csvFile);
        		}
        	});
        }
});
</script>
</head>

<body>

<!-- CONTENT START -->
	<div id="contentSection" class="contentBlockWhite">
		<div id="spacialRefContent" class="content">

			<!-- INFO START -->
				<div id="winNavi" style="top:0;">
	               <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-8#overall-catalog-management-8', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	           </div>
			<div class="inputContainer" style="padding-bottom: 10px;">
						<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.locations.startUpdate" />" onclick="pageLocations.startSNSLocationUpdateJob()"><fmt:message key="dialog.admin.catalog.management.locations.startUpdate" /></button>
					<span id="snsLocationUpdateLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
			</div>

			<div class="inputContainer noSpaceBelow">
				<div id="spacialRefInfo" class="infobox">
					<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
					<span class="title" id="snsLocationUpdateProcessInfo"></span>
					<a href="#" onclick="require('ingrid/utils/UI').toggleInfo('spacialRefInfo')" title="<fmt:message key="general.info.open" />"><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
					<div id="spacialRefInfoContent">
						<table cellspacing="0">
							<tr>
								<td><fmt:message key="dialog.admin.catalog.management.locations.startTime" /></td>
								<td id="snsLocationUpdateProcessStart"></td>
							</tr>
							<tr>
								<td><fmt:message key="dialog.admin.catalog.management.locations.endTime" /></td>
								<td id="snsLocationUpdateProcessEnd"></td>
							</tr>
							<tr>
								<td><fmt:message key="dialog.admin.catalog.management.locations.numTerms" /></td>
								<td id="snsLocationUpdateProcessNumEntities"></td>
							</tr>
						</table>
						<span id="cancelSNSLocationUpdateProcessButton" class="button">
							<span style="float:right;">
								<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.locations.cancel" />" onclick="pageLocations.cancelSNSLocationUpdateJob()"><fmt:message key="dialog.admin.catalog.management.locations.cancel" /></button>
							</span>
						</span>
					</div>
				</div>
				<div class="spacer"></div>
			</div> <!-- INFO END -->

			<div class="spacer"></div>

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="inputContainer">
				<div class="inputContainer">
					<span class="functionalLink" style="margin-top:0;"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="pageLocations.downloadAsCSV()" title="<fmt:message key="dialog.admin.catalog.management.locations.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.locations.saveAsCSV" /></a></span>

					<div class="listInfo">
						<span id="snsLocationUpdateResultInfo" class="searchResultsInfo">&nbsp;</span>
						<span id="snsLocationUpdateResultPaging" class="searchResultsPaging">&nbsp;</span>
						<div class="fill"></div>
					</div>

					<div id="locationsResultTable" autoHeight="20" contextMenu="none" defaultHideScrollbar="true">
                    </div>
				</div>
			</div> <!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div>
 <!-- CONTENT END -->

</body>
</html>
