<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var pageAnalysis = _container_;

require([
    "dojo/on",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/dom",
    "dojo/Deferred",
    "ingrid/utils/Grid",
    "ingrid/utils/Store",
    "ingrid/utils/String",
    "ingrid/layoutCreator",
    "ingrid/utils/LoadingZone",
    /*needed for toggleInfo*/ "ingrid/utils/UI"
], function(on, lang, array, dom, Deferred, UtilGrid, UtilStore, UtilString, layoutCreator, LoadingZone) {


        var analysisResultTableStructure = [{
            field: 'message',
            name: "<fmt:message key='dialog.admin.catalog.management.analysis.error' />",
            width: '200px'
        }, {
            field: 'solution',
            name: "<fmt:message key='dialog.admin.catalog.management.analysis.solution' />",
            width: '500px'
        }];
        layoutCreator.createDataGrid("analysisResultTable", null, analysisResultTableStructure, null);

        on(_container_, "Load", function() {
            startAnalysisJob();
        });


        // Button function for the 'Start' button
        function startAnalysisJob() {
            analyzeDef()
                .then(setAnalysisResult)
                .then(updateJobInfo, function(err) {
                    displayErrorMessage(err);
                    console.debug("Error: " + err);
                });
        }


        function setAnalysisResult(analyzeJobInfo) {
            UtilGrid.clearSelection("analysisResultTable");

            //console.debug(analyzeJobInfo.errorReports);
            if (analyzeJobInfo && analyzeJobInfo.errorReports) {
                addObjectLinks(analyzeJobInfo.errorReports);
                UtilStore.updateWriteStore("analysisResultTable", analyzeJobInfo.errorReports);
            }
            return analyzeJobInfo;
        }

        function addObjectLinks(report) {
            array.forEach(report, function(info) {
                info.message = info.message.replace(/(.{8}-.{4}-.{4}-.{4}-.{12})/, "<a href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\"$1\", \"O\");'>$1</a>");
                info.solution = info.solution.replace(/(.{8}-.{4}-.{4}-.{4}-.{12})/, "<a href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\"$1\", \"O\");'>$1</a>");
            });
        }

        function updateJobInfo(analyzeJobInfo) {
            dom.byId("analysisJobBeginDate").innerHTML = UtilString.getDateString(analyzeJobInfo.startTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
            dom.byId("analysisJobEndDate").innerHTML = UtilString.getDateString(analyzeJobInfo.endTime, "EEEE, dd. MMMM yyyy HH:mm:ss");

            if (analyzeJobInfo.errorReports && analyzeJobInfo.errorReports.length > 0) {
                dom.byId("analysisJobNumErrors").innerHTML = analyzeJobInfo.errorReports.length;
            } else {
                dom.byId("analysisJobNumErrors").innerHTML = "<fmt:message key='dialog.admin.management.analysis.noErrorsFound' />";
            }
        }

        function analyzeDef() {
            var def = new Deferred();

            CatalogManagementService.analyze({
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,

                callback: function(result) {
                    def.resolve(result);
                },
                errorHandler: function(error) {
                    displayErrorMessage(error);
                    console.debug("Error: " + error);
                    def.reject(error);
                }
            });

            return def;
        }

        /**
         * PUBLIC METHODS
         */
        
        pageAnalysis = {
            startAnalysisJob: startAnalysisJob
        };

    });
</script>
</head>


<body>
	<!-- CONTENT START -->
		<div id="contentSection" class="contentBlockWhite top">
			<div id="winNavi">
				<a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-1#overall-catalog-management-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
			</div>

			<div id="analysisContent" class="content">

				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="inputContainer">
					<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.analysis.start" />" onclick="pageAnalysis.startAnalysisJob()"><fmt:message key="dialog.admin.catalog.management.analysis.start" /></button>
				</div>

				<div class="inputContainer noSpaceBelow">
					<div id="analysisInfo" class="infobox">
						<span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
						<span class="title"><a href="#" onclick="require('ingrid/utils/UI').toggleInfo('analysisInfo')" title="<fmt:message key="general.info.open" />"><fmt:message key="dialog.admin.catalog.management.analysis.processInfo" />
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
