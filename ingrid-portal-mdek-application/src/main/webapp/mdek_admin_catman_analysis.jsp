<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
		dojo.byId("analysisJobNumErrors").innerHTML = "Keine Fehler gefunden!";
	}
}

function analyzeDef() {
	var def = new dojo.Deferred();

	CatalogService.analyze({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,

		callback: function(result) {
			def.callback(result);
		},
		errorHandler: function(error) {
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
				<a href="#" title="Hilfe">[?]</a>
			</div>

			<div id="analysisContent" class="content">

				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="spacer"></div>
				<div class="spacer"></div>
				<div class="inputContainer">
					<button dojoType="ingrid:Button" title="Pr&uuml;fung starten" onClick="javascript:scriptScope.startAnalysisJob();">Pr&uuml;fung starten</button>
				</div>

				<div class="inputContainer noSpaceBelow w964">
					<div id="analysisInfo" class="infobox w950">
						<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
						<span class="title"><a href="javascript:toggleInfo('analysisInfo');" title="Info aufklappen">Informationen zum letzten Prozess:
							<img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
						<span id="analysisLoadingZone" style="visibility:hidden;" class="processInfo"><img src="img/ladekreis.gif" width="20" height="20" alt="Prozess l&auml;uft" /></span>
						<div id="analysisInfoContent">
							<table cellspacing="0">
								<tr>
									<td>Gestartet am:</td>
									<td id="analysisJobBeginDate"></td>
								</tr>
								<tr>
									<td>Beendet am:</td>
									<td id="analysisJobEndDate"></td>
								</tr>
								<tr>
									<td>Anzahl der gefundenen Fehler:</td>
									<td id="analysisJobNumErrors"></td>
								</tr>
								<tr>
							</table>
						</div>
					</div>
				</div>

				<div class="inputContainer">
					<span class="label"><label class="inActive" for="analysisResultTable">Ergebnis der Pr&uuml;fung</label></span>
					<div class="tableContainer rows20 w964">
						<table id="analysisResultTable" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable nosort w964">
							<thead>
								<tr>
									<th nosort="true" field="message" dataType="String">Fehlermeldung</th>
									<th nosort="true" field="solution" dataType="String">L&ouml;sungsvorschlag</th>
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
