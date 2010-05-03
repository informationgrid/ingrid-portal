<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

var resultsPerPage = 20;
var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("snsLocationUpdateResultInfo"), pagingSpan:dojo.byId("snsLocationUpdateResultPaging") });

var snsLocationUpdateResult = [];

_container_.addOnLoad(function() {
	refreshSNSLocationUpdateProcessInfo();

	dojo.event.connect("after", pageNav, "onPageSelected", updateSNSLocationTable);
});

scriptScope.startSNSLocationUpdateJob = function() {
	var file = dwr.util.getValue("snsLocationUpdateFile");

	CatalogManagementService.startSNSLocationUpdateJob(file, {
		preHook: showLoadingZone,
		callback: function() {
			setTimeout("refreshSNSLocationUpdateProcessInfo()", 1000);
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.locations.jobStartHint"), dialog.INFO);
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			dojo.debug("error: " + errMsg);
			dojo.debugShallow(err);
		}
	});
}

scriptScope.cancelSNSLocationUpdateJob = function() {
	CatalogManagementService.stopSNSLocationUpdateJob( {
		callback: function() {
			dojo.debug("Job stopped.");
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			dojo.debug("error: " + errMsg);
			dojo.debugShallow(err);
		}
	});
}


refreshSNSLocationUpdateProcessInfo = function() {
	CatalogManagementService.getSNSLocationUpdateJobInfo( {
		callback: function(jobInfo){
			updateSNSLocationUpdateJobInfo(jobInfo);
			if (!jobFinished(jobInfo)) {
				setTimeout("refreshSNSLocationUpdateProcessInfo()", 1000);
			}
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			console.log("Error: "+ message);
			// If there's a timeout try again
			if (err.message != "USER_LOGIN_ERROR") {
			    setTimeout("refreshSNSLocationUpdateProcessInfo()", 1000);
			}
		}
	});
};

function updateSNSLocationUpdateJobInfo(jobInfo) {
	if (jobFinished(jobInfo)) {
		hideLoadingZone();

		dojo.byId("snsLocationUpdateProcessInfo").innerHTML = message.get("dialog.admin.catalog.management.locations.updateLastProcessInfo");
		dojo.html.setVisibility("cancelSNSLocationUpdateProcessButton", false);

		if (jobInfo.startTime == null) {
			// Job was never executed
			dojo.byId("snsLocationUpdateProcessStart").innerHTML = "";
			dojo.byId("snsLocationUpdateProcessEnd").innerHTML = "";
			dojo.byId("snsLocationUpdateProcessNumEntities").innerHTML = "";

		} else {
			dojo.byId("snsLocationUpdateProcessStart").innerHTML = jobInfo.startTime;
			dojo.byId("snsLocationUpdateProcessEnd").innerHTML = jobInfo.endTime;
			dojo.byId("snsLocationUpdateProcessNumEntities").innerHTML = jobInfo.numEntities;
		}

		// TODO Remove test code
		var numTerms = jobInfo.snsUpdateResults.length;
		snsLocationUpdateResult = jobInfo.snsUpdateResults;

		pageNav.reset();
		pageNav.setTotalNumHits(numTerms);
		updateSNSLocationTable();

	} else {
		showLoadingZone();
		dojo.html.setVisibility("cancelSNSLocationUpdateProcessButton", true);
		dojo.byId("snsLocationUpdateProcessInfo").innerHTML = message.get("dialog.admin.catalog.management.locations.updateCurrentProcessInfo");
		dojo.byId("snsLocationUpdateProcessStart").innerHTML = jobInfo.startTime;
		dojo.byId("snsLocationUpdateProcessEnd").innerHTML = "";
		dojo.byId("snsLocationUpdateProcessNumEntities").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
	}
}

function jobFinished(jobInfo) {
	return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
}

function showLoadingZone() {
    dojo.html.setVisibility("snsLocationUpdateLoadingZone", true);
}

function hideLoadingZone() {
    dojo.html.setVisibility("snsLocationUpdateLoadingZone", false);
}

// Paging
function updateSNSLocationTable() {
	var startHit = pageNav.getStartHit();
	var currentView = snsLocationUpdateResult.slice(startHit, startHit + resultsPerPage);
	dojo.widget.byId("locationsResultTable").store.setData(currentView);
	pageNav.updateDomNodes();
}

scriptScope.downloadAsCSV = function() {
	CatalogManagementService.getSNSLocationUpdateResultAsCSV({
		callback: function(csvFile) {
			dwr.engine.openInDownload(csvFile);
		}
	});
}

</script>
</head>

<body>

<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=overall-catalog-management-8#overall-catalog-management-8', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<div id="spacialRefContent" class="content">

			<!-- INFO START -->
			<div class="spacer"></div>
			<div class="spacer"></div>

			<div class="inputContainer grey field w948">
				<span class="label"><label for="importFile" onclick="javascript:dialog.showContextHelp(arguments[0], 8057, '<fmt:message key="dialog.admin.catalog.management.locations.selectUpdateDataset" />')"><fmt:message key="dialog.admin.catalog.management.locations.selectUpdateDataset" /></label></span>
				<span>
					<input type="file" id="snsLocationUpdateFile" size="80" />
				</span>
				<br />
				<br />
				<span><fmt:message key="dialog.admin.catalog.management.locations.updateHint" /></span>
			</div>

			<div class="inputContainer w965">
				<span class="button" style="height:20px !important;">
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.catalog.management.locations.startUpdate" />" onClick="javascript:scriptScope.startSNSLocationUpdateJob();"><fmt:message key="dialog.admin.catalog.management.locations.startUpdate" /></button>
					</span>
					<span id="snsLocationUpdateLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>

			<div class="inputContainer noSpaceBelow">
				<div id="spacialRefInfo" class="infobox w950">
					<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
					<span class="title" id="snsLocationUpdateProcessInfo"></span>
					<a href="javascript:toggleInfo('spacialRefInfo');" title="<fmt:message key="general.info.open" />"><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
					</span>
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
						<span id="cancelSNSLocationUpdateProcessButton" class="button" style="height:20px !important;">
							<span style="float:right;">
								<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.catalog.management.locations.cancel" />" onClick="javascript:scriptScope.cancelSNSLocationUpdateJob();"><fmt:message key="dialog.admin.catalog.management.locations.cancel" /></button>
							</span>
						</span>
					</div>
				</div>
				<div class="spacer"></div>
			</div> <!-- INFO END -->

			<div class="spacer"></div>

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="inputContainer noSpaceBelow">
				<div class="inputContainer w964 noSpaceBelow">
					<span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.downloadAsCSV();" title="<fmt:message key="dialog.admin.catalog.management.locations.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.locations.saveAsCSV" /></a></span>

					<div class="listInfo w964">
						<span id="snsLocationUpdateResultInfo" class="searchResultsInfo">&nbsp;</span>
						<span id="snsLocationUpdateResultPaging" class="searchResultsPaging">&nbsp;</span>
						<div class="fill"></div>
					</div>

					<table id="locationsResultTable" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable w964 relativePos">
						<thead>
							<tr>
								<th field="title" dataType="String" width="370" noSort="true" sort="asc"><fmt:message key="dialog.admin.catalog.management.locations.spatialRef" /></th>
								<th field="code" dataType="String" width="80" noSort="true"><fmt:message key="dialog.admin.catalog.management.locations.id" /></th>
								<th field="action" dataType="String" width="454" noSort="true"><fmt:message key="dialog.admin.catalog.management.locations.action" /></th>
								<th field="objects" dataType="String" width="60" noSort="true"><fmt:message key="dialog.admin.catalog.management.locations.objects" /></th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>

				</div>
			</div> <!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div>
</div> <!-- CONTENT END -->

</body>
</html>
