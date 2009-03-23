<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

var resultsPerPage = 20;
var pageNavTab1 = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("snsUpdateResultTab1Info"), pagingSpan:dojo.byId("snsUpdateResultTab1Paging") });
var pageNavTab2 = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("snsUpdateResultTab2Info"), pagingSpan:dojo.byId("snsUpdateResultTab2Paging") });

var snsUpdateResult = [];

_container_.addOnLoad(function() {
	refreshSNSUpdateProcessInfo();

	dojo.event.connect("after", pageNavTab1, "onPageSelected", updateSNSRetroIndexTable);
	dojo.event.connect("after", pageNavTab2, "onPageSelected", updateSNSResultTable);
});

scriptScope.startSNSUpdateJob = function() {
	var file = dwr.util.getValue("snsUpdateFile");

	CatalogManagementService.startSNSUpdateJob(file, {
		preHook: showLoadingZone,
		callback: function() {
			setTimeout("refreshSNSUpdateProcessInfo()", 1000);
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

		// TODO Remove test code
		var numTerms = Math.floor((Math.random() * 3000) + 100);
		var result = [];
		for (var i = 0; i < numTerms; ++i) {
			result.push({
				thesaurusId: generateRandomString(Math.floor(Math.random()*21) + 1),
				oldTerm: generateRandomString(Math.floor(Math.random()*21) + 1),
				newTerm: generateRandomString(Math.floor(Math.random()*21) + 1),
				name: generateRandomString(Math.floor(Math.random()*21) + 1),
				type: generateRandomString(Math.floor(Math.random()*21) + 1),
				action: generateRandomString(Math.floor(Math.random()*21) + 1),
				objects: Math.floor(Math.random()*4000) + 1,
				addresses: Math.floor(Math.random()*4000) + 1
			});
		}

		snsUpdateResult = result;

		pageNavTab1.reset();
		pageNavTab1.setTotalNumHits(numTerms);
		updateSNSRetroIndexTable();

		pageNavTab2.reset();
		pageNavTab2.setTotalNumHits(numTerms);
		updateSNSResultTable();

	} else {
		showLoadingZone();

		dojo.html.setVisibility("cancelSNSUpdateProcessButton", true);
		dojo.byId("snsUpdateProcessInfo").innerHTML = message.get("dialog.admin.catalog.management.searchTerms.updateCurrentProcessInfo");
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
function updateSNSRetroIndexTable() {
	var startHit = pageNavTab1.getStartHit();
	var currentView = snsUpdateResult.slice(startHit, startHit + resultsPerPage);
	dojo.widget.byId("resultListRetroIndexTable").store.setData(currentView);
	pageNavTab1.updateDomNodes();
}
function updateSNSResultTable() {
	var startHit = pageNavTab2.getStartHit();
	var currentView = snsUpdateResult.slice(startHit, startHit + resultsPerPage);
	dojo.widget.byId("resultListThesaurusTable").store.setData(currentView);
	pageNavTab2.updateDomNodes();
}


function generateRandomString(strLength) {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var string_length = strLength;
	var str = '';
	for (var i=0; i<string_length; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		str += chars.substring(rnum,rnum+1);
	}
	return str;
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
				<div id="searchTermsResultContainer" class="inputContainer noSpaceBelow">
					<span class="functionalLink onTab"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="#" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" /></a></span>
					<div id="resultLists" dojoType="ingrid:TabContainer" class="w964 h565" selectedChild="resultListRetroIndex">
	
						<!-- TAB 1 START -->
						<div id="resultListRetroIndex" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.admin.catalog.management.searchTerms.retroResult" />">
							<div class="inputContainer w964 noSpaceBelow">

								<div class="listInfo w964">
									<span id="snsUpdateResultTab1Info" class="searchResultsInfo">&nbsp;</span>
									<span id="snsUpdateResultTab1Paging" class="searchResultsPaging">&nbsp;</span>
									<div class="fill"></div>
								</div>

								<table id="resultListRetroIndexTable" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable w964">
									<thead>
										<tr>
											<th field="thesaurusId" dataType="String" width="104" noSort="true" sort="asc"><fmt:message key="dialog.admin.catalog.management.searchTerms.id" /></th>
											<th field="oldTerm" dataType="String" width="430" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.oldTerm" /></th>
											<th field="newTerm" dataType="String" width="430" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.newTerm" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>

							</div>
						</div> <!-- TAB 1 END -->
	
						<!-- TAB 2 START -->
						<div id="resultListThesaurus" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.admin.catalog.management.searchTerms.updateResult" />">
							<div class="inputContainer w964 noSpaceBelow">
	
								<div class="listInfo w964">
									<span id="snsUpdateResultTab2Info" class="searchResultsInfo">&nbsp;</span>
									<span id="snsUpdateResultTab2Paging" class="searchResultsPaging">&nbsp;</span>
									<div class="fill"></div>
								</div>

								<table id="resultListThesaurusTable" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable w964 relativePos">
									<thead>
										<tr>
											<th field="name" dataType="String" width="354" noSort="true" sort="asc"><fmt:message key="dialog.admin.catalog.management.searchTerms.description" /></th>
											<th field="type" dataType="String" width="200" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.type" /></th>
											<th field="action" dataType="String" width="250" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.action" /></th>
											<th field="objects" dataType="String" width="80" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.objects" /></th>
											<th field="addresses" dataType="String" width="80" noSort="true"><fmt:message key="dialog.admin.catalog.management.searchTerms.addresses" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
	
						</div>
	
					</div> <!-- TAB 2 END -->
				</div>
			</div> <!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div> <!-- CONTENT END -->
</div>

</body>
</html>
