<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

var resultsPerPage = 20;
var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("snsUpdateResultInfo"), pagingSpan:dojo.byId("snsUpdateResultPaging") });

var snsUpdateResult = [];

_container_.addOnLoad(function() {
	refreshSNSUpdateProcessInfo();

	dojo.event.connect("after", pageNav, "onPageSelected", updateSNSResultTable);
});

scriptScope.startSNSUpdateJob = function() {
	var file = dwr.util.getValue("snsUpdateFile");

	CatalogManagementService.startSNSUpdateJob(file, {
		preHook: showLoadingZone,
		callback: function() {
			setTimeout("refreshSNSUpdateProcessInfo()", 1000);
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.searchTerms.jobStartHint"), dialog.INFO);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug("error: " + errMsg);
			dojo.debugShallow(err);
		}
	});
}

scriptScope.cancelSNSUpdateJob = function() {
	CatalogManagementService.stopSNSUpdateJob( {
		callback: function() {
			dojo.debug("Job stopped.");
		},
		errorHandler: function(errMsg, err) {
			dojo.debug("error: " + errMsg);
			dojo.debugShallow(err);
		}
	});
}


refreshSNSUpdateProcessInfo = function() {
	CatalogManagementService.getSNSUpdateJobInfo( {
		callback: function(jobInfo){
			updateSNSUpdateJobInfo(jobInfo);
			if (!jobFinished(jobInfo)) {
				setTimeout("refreshSNSUpdateProcessInfo()", 1000);
			}
		},
		errorHandler: function(message, err) {
			console.log("Error: "+ message);
			// If there's a timeout try again
			setTimeout("refreshSNSUpdateProcessInfo()", 1000);
		}
	});
};

function updateSNSUpdateJobInfo(jobInfo) {
	if (jobFinished(jobInfo)) {
		hideLoadingZone();

		dojo.byId("snsUpdateProcessInfo").innerHTML = message.get("dialog.admin.catalog.management.searchTerms.updateLastProcessInfo");
		dojo.html.setVisibility("cancelSNSUpdateProcessButton", false);
		dojo.html.setVisibility("snsUpdateProcessStatusRow", false);

		if (jobInfo.startTime == null) {
			// Job was never executed
			dojo.byId("snsUpdateProcessStart").innerHTML = "";
			dojo.byId("snsUpdateProcessEnd").innerHTML = "";
			dojo.byId("snsUpdateProcessNumEntities").innerHTML = "";

		} else {
			dojo.byId("snsUpdateProcessStart").innerHTML = jobInfo.startTime;
			dojo.byId("snsUpdateProcessEnd").innerHTML = jobInfo.endTime;
			dojo.byId("snsUpdateProcessNumEntities").innerHTML = jobInfo.numEntities;
		}

		var numTerms = jobInfo.snsUpdateResults.length;
		snsUpdateResult = jobInfo.snsUpdateResults;

		pageNav.reset();
		pageNav.setTotalNumHits(numTerms);
		updateSNSResultTable();

	} else {
		showLoadingZone();

		dojo.html.setVisibility("cancelSNSUpdateProcessButton", true);
		dojo.html.setVisibility("snsUpdateProcessStatusRow", true);
		dojo.byId("snsUpdateProcessInfo").innerHTML = message.get("dialog.admin.catalog.management.searchTerms.updateCurrentProcessInfo");

		if (jobInfo.description != null && jobInfo.description.indexOf("snsUpdateJob") != -1) { 
			dojo.byId("snsUpdateProcessStatus").innerHTML = message.get("dialog.admin.catalog.management.searchTerms.stateSNSUpdate");

		} else {
			dojo.byId("snsUpdateProcessStatus").innerHTML = jobInfo.description;
		}

		dojo.byId("snsUpdateProcessStart").innerHTML = jobInfo.startTime;
		dojo.byId("snsUpdateProcessEnd").innerHTML = "";
		dojo.byId("snsUpdateProcessNumEntities").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
	}
}

function jobFinished(jobInfo) {
	return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
}

function showLoadingZone() {
    dojo.html.setVisibility("snsUpdateLoadingZone", true);
}

function hideLoadingZone() {
    dojo.html.setVisibility("snsUpdateLoadingZone", false);
}

// Paging
function updateSNSResultTable() {
	var startHit = pageNav.getStartHit();
	var currentView = snsUpdateResult.slice(startHit, startHit + resultsPerPage);
	dojo.widget.byId("resultListThesaurusTable").store.setData(currentView);
	pageNav.updateDomNodes();
}

scriptScope.downloadAsCSV = function() {
	CatalogManagementService.getSNSUpdateResultAsCSV({
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
			<a href="#" title="Hilfe">[?]</a>
		</div>
			<div id="searchTermsContent" class="content">
	
				<!-- INFO START -->
				<div class="spacer"></div>
				<div class="spacer"></div>

				<div class="inputContainer grey field w948">
					<span class="label"><label for="importFile" onclick="javascript:dialog.showContextHelp(arguments[0], '<fmt:message key="dialog.admin.catalog.management.searchTerms.selectUpdateDataset" />')"><fmt:message key="dialog.admin.catalog.management.searchTerms.selectUpdateDataset" /></label></span>
					<span>
						<input type="file" id="snsUpdateFile" size="80" />
					</span>
					<br />
					<br />
					<span><fmt:message key="dialog.admin.catalog.management.searchTerms.updateHint" /></span>
				</div>

				<div class="inputContainer w965">
					<span class="button" style="height:20px !important;">
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.startUpdate" />" onClick="javascript:scriptScope.startSNSUpdateJob();"><fmt:message key="dialog.admin.catalog.management.searchTerms.startUpdate" /></button>
						</span>
						<span id="snsUpdateLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
							<img src="img/ladekreis.gif" />
						</span>
					</span>
				</div>

				<div class="inputContainer noSpaceBelow">
					<div id="searchTermsInfo" class="infobox w950">
						<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
						<span class="title" id="snsUpdateProcessInfo"></span>
						<a href="javascript:toggleInfo('searchTermsInfo');" title="Info aufklappen"><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
						<div id="searchTermsInfoContent">
							<table cellspacing="0">
								<tr id="snsUpdateProcessStatusRow">
									<td><fmt:message key="dialog.admin.catalog.management.searchTerms.status" /></td>
									<td id="snsUpdateProcessStatus"></td>
								</tr>
								<tr>
									<td><fmt:message key="dialog.admin.catalog.management.searchTerms.startTime" /></td>
									<td id="snsUpdateProcessStart"></td>
								</tr>
								<tr>
									<td><fmt:message key="dialog.admin.catalog.management.searchTerms.endTime" /></td>
									<td id="snsUpdateProcessEnd"></td>
								</tr>
								<tr>
									<td><fmt:message key="dialog.admin.catalog.management.searchTerms.numTerms" /></td>
									<td id="snsUpdateProcessNumEntities"></td>
								</tr>
							</table>
							<span id="cancelSNSUpdateProcessButton" class="button" style="height:20px !important;">
								<span style="float:right;">
									<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.cancel" />" onClick="javascript:scriptScope.cancelSNSUpdateJob();"><fmt:message key="dialog.admin.catalog.management.searchTerms.cancel" /></button>
								</span>
							</span>
						</div>
					</div>
					<div class="spacer"></div>
				</div> <!-- INFO END -->
	
				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="inputContainer noSpaceBelow">
					<!-- TABLE START -->
					<div class="inputContainer w964 noSpaceBelow">

						<span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.downloadAsCSV();" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" /></a></span>
						<div class="listInfo w964">
							<span id="snsUpdateResultInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="snsUpdateResultPaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>

						<table id="resultListThesaurusTable" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable w964 relativePos">
							<thead>
								<tr>
									<th field="term" dataType="String" width="254" noSort="true" sort="asc"><fmt:message key="dialog.admin.catalog.management.searchTerms.description" /></th>
									<th field="type" dataType="String" width="100" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.type" /></th>
									<th field="action" dataType="String" width="450" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.action" /></th>
									<th field="objects" dataType="String" width="80" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.objects" /></th>
									<th field="addresses" dataType="String" width="80" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.addresses" /></th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div> <!-- TABLE END -->

				</div>  <!-- LEFT HAND SIDE CONTENT END -->

			</div>
		</div>
	</div> <!-- CONTENT END -->
</div>

</body>
</html>
