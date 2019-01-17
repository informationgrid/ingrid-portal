<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

    var pageUvpStatistic = _container_;
    require([
        "dojo/on",
        "dojo/dom-class",
        "dijit/registry"
    ], function(on, domClass, registry) {
        pageUvpStatistic.startDate = new Date();
        pageUvpStatistic.endDate = new Date();

        on(_container_, "Load", function() {

        });

        function download(filename, text) {
        	if (window.navigator.msSaveBlob) { // // IE hack; see http://msdn.microsoft.com/en-us/library/ie/hh779016.aspx
              var blob = new Blob(['\uFEFF' + text], { type: 'text/csv;charset=utf-8' });
        	    window.navigator.msSaveOrOpenBlob(blob, filename);
        	}
        	else {
        	    var a = window.document.createElement("a");
              a.setAttribute('href', 'data:text/plain;charset=utf8,'+ '\uFEFF' + encodeURIComponent(text));
              a.setAttribute('download', filename);
        	    document.body.appendChild(a);
        	    a.click();  // IE: "Access is denied"; see: https://connect.microsoft.com/IE/feedback/details/797361/ie-10-treats-blob-url-as-cross-origin-and-denies-access
        	    document.body.removeChild(a);
        	}
        }

        function verifyDates(startDate, endDate) {
            if (!startDate || isNaN(startDate) || !endDate || isNaN(endDate)) {
                registry.byId("btnUvpStatisticCreate").set("disabled", true);
            } else {
                registry.byId("btnUvpStatisticCreate").set("disabled", false);
            }
        }

        pageUvpStatistic.setStartDate = function(date) {
            pageUvpStatistic.startDate = date;
            registry.byId("dateEndUvpStatistic").constraints.min = date;
            verifyDates(pageUvpStatistic.startDate, pageUvpStatistic.endDate);
        }
        
        pageUvpStatistic.setEndDate = function(date) {
            pageUvpStatistic.endDate = date;
            registry.byId("dateStartUvpStatistic").constraints.max = date;
            verifyDates(pageUvpStatistic.startDate, pageUvpStatistic.endDate);
        }

        pageUvpStatistic.createStatistic = function() {
            var self = this;
            setGenerationState(false);
            StatisticService.createReport("UVP", {startDate: pageUvpStatistic.startDate, endDate: pageUvpStatistic.endDate}, function(report) {
                console.log("Report created: ", report);
                // console.log("CSW created: ", self.convertToCSV(report));
                var filename = "report-" +
                        pageUvpStatistic.startDate.toISOString().substr(0,10) +
                        "__" +
                        pageUvpStatistic.endDate.toISOString().substr(0,10) +
                        ".csv";
                download(filename, self.convertToCSV(report));
                setGenerationState(true);
            });
        }

        function setGenerationState(isFinished) {
            isFinished
                ? domClass.add("uvpStatisticProgress", "hide")
                : domClass.remove("uvpStatisticProgress", "hide");

            registry.byId("dateStartUvpStatistic").set("disabled", !isFinished);
            registry.byId("btnUvpStatisticCreate").set("disabled", !isFinished);
        }

        pageUvpStatistic.convertToCSV = function(report) {
            console.log("Convert report to CSV");
            var csv = "UVP Nummer; UVP-G Kategorie; Anzahl; Positive Vorprüfungen; Negative Vorprüfungen\n";

            csv += ";;;" + report.values.totalPositive + ";" + report.values.totalNegative + "\n";

            var totalGrouped = report.values.totalGrouped;
            var groupedKeys = Object.keys(totalGrouped);
            for (var i=0; i<groupedKeys.length; i++) {
                csv += groupedKeys[i] + ";" + totalGrouped[groupedKeys[i]][1] + ";" + totalGrouped[groupedKeys[i]][0] + "\n";
            }

            return csv;
        }
    });

</script>
</head>

<body>
    <div data-dojo-type="dijit/layout/ContentPane" class="contentContainer" id="uvpStatisticsContainer" style="padding: 20px;">
        <label class="inActive">
            <fmt:message key="uvp.statistic.dateFrom" />:
        </label>
        <input id="dateStartUvpStatistic"
            data-dojo-type="dijit/form/DateTextBox" 
            data-dojo-props="
                value: new Date(),
                constraints: { 
                    max: new Date()
                }, 
                onChange:function(ev){ pageUvpStatistic.setStartDate(ev); }" />

        <label class="inActive">
            <fmt:message key="uvp.statistic.dateTo" />:
        </label>
        <input id="dateEndUvpStatistic"
            data-dojo-type="dijit/form/DateTextBox" 
            data-dojo-props="
                value: new Date(),
                constraints: {
                    min: new Date(),
                    max: new Date()
                }, 
                onChange:function(ev){ pageUvpStatistic.setEndDate(ev); }" />

        <button id="btnUvpStatisticCreate" data-dojo-type="dijit/form/Button" onclick="pageUvpStatistic.createStatistic()">
            <fmt:message key="uvp.statistic.createStatistic" />
        </button>
        <span id="uvpStatisticProgress" class="hide">
            <img src="img/ladekreis.gif" />
            <fmt:message key="uvp.statistic.generation.progress" />
        </span>
    </div>
</body>
</html>
