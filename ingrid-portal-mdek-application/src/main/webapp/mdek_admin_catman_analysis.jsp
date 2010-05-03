<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	scriptScope.startAnalysisJob();
});


// Button function for the 'Start' button
scriptScope.startAnalysisJob = function() {
	var def = analyzeDef();

	def.addCallback(function(analyzeJobInfo) {
		setAnalysisResult(analyzeJobInfo);
		updateJobInfo(analyzeJobInfo);
	});


	def.addErrback(function(err) {
	    displayErrorMessage(err);
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
	});
}


function setAnalysisResult(analyzeJobInfo) {
	dojo.widget.byId("analysisResultTable").store.clearData();

	if (analyzeJobInfo && analyzeJobInfo.errorReports) {
//		UtilList.addTableIndices(analyzeJobInfo.errorReports);
		dojo.widget.byId("analysisResultTable").store.setData(analyzeJobInfo.errorReports);
	}

	return "test";
}


function updateJobInfo(analyzeJobInfo) {
	dojo.byId("analysisJobBeginDate").innerHTML = analyzeJobInfo.startTime;
	dojo.byId("analysisJobEndDate").innerHTML = analyzeJobInfo.endTime;

	if (analyzeJobInfo.errorReports && analyzeJobInfo.errorReports.length > 0) {
		dojo.byId("analysisJobNumErrors").innerHTML = analyzeJobInfo.errorReports.length;
	} else {
		dojo.byId("analysisJobNumErrors").innerHTML = message.get("dialog.admin.management.analysis.noErrorsFound");
	}
}

function analyzeDef() {
	var def = new dojo.Deferred();

	CatalogManagementService.analyze({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,

		callback: function(result) {
			def.callback(result);
		},
		errorHandler: function(error) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+error);
			def.errback(error);
		}
	});

	return def;
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("analysisLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("analysisLoadingZone"), "hidden");
}

</script>
</head>


<body>
	<!-- CONTENT START -->
	<div dojoType="ContentPane" layoutAlign="client">

		<div id="contentSection" class="contentBlockWhite top">
			<div id="winNavi">
				<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=overall-catalog-management-1#overall-catalog-management-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
			</div>

			<div id="analysisContent" class="content">

				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="spacer"></div>
				<div class="spacer"></div>
				<div class="inputContainer">
					<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.catalog.management.analysis.start" />" onClick="javascript:scriptScope.startAnalysisJob();"><fmt:message key="dialog.admin.catalog.management.analysis.start" /></button>
				</div>

				<div class="inputContainer noSpaceBelow w964">
					<div id="analysisInfo" class="infobox w950">
						<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
						<span class="title"><a href="javascript:toggleInfo('analysisInfo');" title="<fmt:message key="general.info.open" />"><fmt:message key="dialog.admin.catalog.management.analysis.processInfo" />
							<img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
						<span id="analysisLoadingZone" style="visibility:hidden;" class="processInfo"><img src="img/ladekreis.gif" width="20" height="20" alt="Prozess l&auml;uft" /></span>
						<div id="analysisInfoContent">
							<table cellspacing="0">
								<tr>
									<td><fmt:message key="dialog.admin.catalog.management.analysis.startTime" /></td>
									<td id="analysisJobBeginDate"></td>
								</tr>
								<tr>
									<td><fmt:message key="dialog.admin.catalog.management.analysis.endTime" /></td>
									<td id="analysisJobEndDate"></td>
								</tr>
								<tr>
									<td><fmt:message key="dialog.admin.catalog.management.analysis.numErrors" /></td>
									<td id="analysisJobNumErrors"></td>
								</tr>
								<tr>
							</table>
						</div>
					</div>
				</div>

				<div class="inputContainer">
					<span class="label"><label class="inActive" for="analysisResultTable"><fmt:message key="dialog.admin.catalog.management.analysis.result" /></label></span>
					<div class="tableContainer rows20 w964">
						<table id="analysisResultTable" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable nosort w964">
							<thead>
								<tr>
									<th nosort="true" field="message" dataType="String"><fmt:message key="dialog.admin.catalog.management.analysis.error" /></th>
									<th nosort="true" field="solution" dataType="String"><fmt:message key="dialog.admin.catalog.management.analysis.solution" /></th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>

				<!-- LEFT HAND SIDE CONTENT END -->        
			</div>
		</div>
	</div>
	<!-- CONTENT END -->

</body>
</html>
