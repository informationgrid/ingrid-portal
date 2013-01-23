<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScopeAnalysis = _container_;

var analysisResultTableStructure = [
    {field: 'message',name: "<fmt:message key='dialog.admin.catalog.management.analysis.error' />",width: '200px'},
    {field: 'solution',name: "<fmt:message key='dialog.admin.catalog.management.analysis.solution' />",width: '500px'}
];
createDataGrid("analysisResultTable", null, analysisResultTableStructure, null);

dojo.connect(_container_, "onLoad", function(){
	scriptScopeAnalysis.startAnalysisJob();
});


// Button function for the 'Start' button
scriptScopeAnalysis.startAnalysisJob = function() {
	var def = this.analyzeDef();

	def.addCallback(dojo.hitch(this, this.setAnalysisResult));
	def.addCallback(this.updateJobInfo);
	//});


	def.addErrback(function(err) {
	    displayErrorMessage(err);
		console.debug("Error: "+err);
	});
}


scriptScopeAnalysis.setAnalysisResult = function(analyzeJobInfo) {
	UtilGrid.clearSelection("analysisResultTable");
    
    //console.debug(analyzeJobInfo.errorReports);
	if (analyzeJobInfo && analyzeJobInfo.errorReports) {
        this.addObjectLinks(analyzeJobInfo.errorReports);
		UtilStore.updateWriteStore("analysisResultTable", analyzeJobInfo.errorReports);
	}
}

scriptScopeAnalysis.addObjectLinks = function (report) {
    dojo.forEach(report, function(info) {
        info.message  =  info.message.replace(/(.{8}-.{4}-.{4}-.{4}-.{12})/, "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\"$1\", \"O\");'>$1</a>");
        info.solution = info.solution.replace(/(.{8}-.{4}-.{4}-.{4}-.{12})/, "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\"$1\", \"O\");'>$1</a>");
    });
}

scriptScopeAnalysis.updateJobInfo = function(analyzeJobInfo) {
	dojo.byId("analysisJobBeginDate").innerHTML = UtilString.getDateString(analyzeJobInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	dojo.byId("analysisJobEndDate").innerHTML = UtilString.getDateString(analyzeJobInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");

	if (analyzeJobInfo.errorReports && analyzeJobInfo.errorReports.length > 0) {
		dojo.byId("analysisJobNumErrors").innerHTML = analyzeJobInfo.errorReports.length;
	} else {
		dojo.byId("analysisJobNumErrors").innerHTML = "<fmt:message key='dialog.admin.management.analysis.noErrorsFound' />";
	}
}

scriptScopeAnalysis.analyzeDef = function() {
	var def = new dojo.Deferred();

	CatalogManagementService.analyze({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,

		callback: function(result) {
			def.callback(result);
		},
		errorHandler: function(error) {
		    displayErrorMessage(error);
			console.debug("Error: "+error);
			def.errback(error);
		}
	});

	return def;
}

function showLoadingZone() {
    dojo.byId("analysisLoadingZone").style.visibility = "visible";
}

function hideLoadingZone() {
    dojo.byId("analysisLoadingZone").style.visibility = "hidden";
}

</script>
</head>


<body>
	<!-- CONTENT START -->
		<div id="contentSection" class="contentBlockWhite top">
			<div id="winNavi">
				<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-1#overall-catalog-management-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
			</div>

			<div id="analysisContent" class="content">

				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="inputContainer">
					<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.analysis.start" />" onClick="javascript:scriptScopeAnalysis.startAnalysisJob();"><fmt:message key="dialog.admin.catalog.management.analysis.start" /></button>
				</div>

				<div class="inputContainer noSpaceBelow">
					<div id="analysisInfo" class="infobox">
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
							</table>
						</div>
					</div>
				</div>

				<div class="inputContainer" style="padding-top: 15px;">
					<span class="label required"><label class="inActive" for="analysisResultTable"><fmt:message key="dialog.admin.catalog.management.analysis.result" /></label></span>
					<div class="tableContainer">
						<div id="analysisResultTable" autoHeight="20" contextMenu="none" ></div>
					</div>
				</div>

				<!-- LEFT HAND SIDE CONTENT END -->        
			</div>
		</div>
	<!-- CONTENT END -->

</body>
</html>
