<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
        "dijit/registry"
    ], function(on, registry) {
        var dateStart = null;

        on(_container_, "Load", function() {

        });

        pageUvpStatistic.setDate = function(date) {
            this.dateStart = date;
            if (!date || isNaN(date)) {
                registry.byId("btnUvpStatisticCreate").set("disabled", true);
            } else {
                registry.byId("btnUvpStatisticCreate").set("disabled", false);
            }
        }

        pageUvpStatistic.createStatistic = function() {
            StatisticService.createReport("UVP", {startDate: this.dateStart}, function(report) {
                console.log("Report created: ", report);
            });
        }
    });

</script>
</head>

<body>

    <div data-dojo-type="dijit/layout/ContentPane" class="contentContainer" id="uvpStatisticsContainer">
        <label class="inActive" for="datePublish">
            <fmt:message key="uvp.statistic.dateFrom" />:
        </label>
        <input data-dojo-type="dijit/form/DateTextBox" 
               data-dojo-props="
                constraints: { 
                    max: new Date()
                }, 
                onChange:function(ev){ pageUvpStatistic.setDate(ev); }" />

        <button id="btnUvpStatisticCreate" data-dojo-type="dijit/form/Button" onclick="pageUvpStatistic.createStatistic()" disabled>
            <fmt:message key="uvp.statistic.createStatistic" />
        </button>
    </div>

</body>
</html>
