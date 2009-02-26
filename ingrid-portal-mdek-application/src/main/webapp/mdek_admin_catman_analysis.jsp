<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	resetAnalysisResult();
});

function resetAnalysisResult() {

}


// Button function for the 'Start' button
scriptScope.startAnalysisJob = function() {
	var def = analyzeDef();

	def.addCallback(setAnalysisResult);
}


function setAnalysisResult(analyzeJobInfo) {
	if (analyzeJobInfo && analyzeJobInfo.errorReports) {
		var errorReports = analyzeJobInfo.errorReports;
		dojo.lang.forEach(errorReports, dojo.debugShallow);
	}
}


function analyzeDef() {
	var def = new dojo.Deferred();

	CatalogService.analyze({
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


scriptScope.cancelAnalysisJob = function() {
	// TODO implement
}

scriptScope.showAnalysisInfo = function(tableIndex) {
	dialog.show(message.get("general.info"), "showAnalysisInfo not implemented yet.", dialog.INFO);
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

				<div class="inputContainer noSpaceBelow">
					<div id="analysisInfo" class="infobox w652">
						<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
						<span class="title"><a href="javascript:toggleInfo('analysisInfo');" title="Info aufklappen">Informationen zum letzten Prozess:
							<img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
						<span class="processInfo">Der Prozess wird momentan ausgef&uuml;hrt. <a href="#" title="Hilfe">Hilfe</a>?<img src="img/ladekreis.gif" width="20" height="20" alt="Prozess l&auml;uft" /></span>
						<div id="analysisInfoContent">
							<table cellspacing="0">
								<tr>
								<td>Gestartet am:</td>
								<td>13.2.2007, 00:02 Uhr</td></tr>
								<td>Beendet am:</td>
								<td>Noch nicht beendet</td></tr>
								<td>Anzahl Objekte:</td>
								<td>132110</td></tr>
								<td>Anzahl Adressen:</td>
								<td>10</td></tr>
							</table>
							<span id="cancelAnalysisProcessButton" class="button" style="height:20px !important;">
								<span style="float:right;">
									<button dojoType="ingrid:Button" title="Prozess abbrechen" onClick="javascript:scriptScope.cancelAnalysisJob();">Prozess abbrechen</button>
								</span>
							</span>
						</div>
					</div>
				</div>

				<div id="analysisResultContainer" class="inputContainer">
					<span class="label">Ergebnis der Analyse des Datenbestandes</span>
				</div>
				<!-- LEFT HAND SIDE CONTENT END -->
        
			</div>
		</div>
	</div>
	<!-- CONTENT END -->

</body>
</html>
