<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">

    var pageResearchDb = _container_;

    require([
        "dojo/aspect",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/has",
        "dojo/dom",
        "dojo/dom-style",
        "dojo/string",
        "dijit/registry",
        "ingrid/layoutCreator",
        "ingrid/utils/Grid",
        "ingrid/utils/List",
        "ingrid/utils/Address",
        "ingrid/utils/PageNavigation"
    ], function(aspect, lang, on, has, dom, style, string, registry, layoutCreator, UtilGrid, UtilList, UtilAddress, navigation) {

            var resultsPerPage = 20;
            var pageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("searchDatabaseResultsInfo"), pagingSpan:dom.byId("searchDatabaseResultsPaging") });

            var currentQuery = null;

            on(_container_, "Load", function() {
                createDOMElements();
                aspect.after(pageNav, "onPageSelected", function() { startSearch(); });
            });

            function createDOMElements() {
                var datbaseSearchResultsStructure = [
                    {field: 'icon',name: '&nbsp;',width: '23px'},
                    {field: 'linkLabel',name: "<fmt:message key='dialog.research.db.name' />",width: '600px'}
                ];
                layoutCreator.createDataGrid("datbaseSearchResults", null, datbaseSearchResultsStructure, null);
            }


            function updateResultTable(res) {
            //  dojo.debugShallow(res);
                var resultList = [];

                if (res.objectSearchResult !== null) {
                    // Objects found
            //      dojo.debugShallow(res.objectSearchResult);
                    resultList = res.objectSearchResult.resultList;
                    UtilList.addObjectLinkLabels(resultList);

                } else if (res.addressSearchResult !== null) {
                    // Addresses found
            //      dojo.debugShallow(res.addressSearchResult);
                    resultList = res.addressSearchResult.resultList;
                    UtilAddress.addAddressTitles(resultList);
                    UtilList.addAddressLinkLabels(resultList);
                }

                UtilList.addIcons(resultList);

                UtilGrid.setTableData("datbaseSearchResults", resultList);
            }


            // starts a database search with the given input
            function startSearch() {
                if (currentQuery === null)
                    return;

                QueryService.queryHQL(currentQuery, pageNav.getStartHit(), resultsPerPage, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(res) { updateResultTable(res); updatePageNavigation(res); },
                    errorHandler: function(errMsg, err) {
                        displayErrorMessage(err);
            //          dialog.show("<fmt:message key='general.error' />", string.substituteParams("<fmt:message key='dialog.generalError' />", errMsg), dialog.WARNING);              
                    }
                });
            }

            // Button onClick function
            // Reads the input field, resets the navigation and starts a new query
            function startNewSearch() {
                currentQuery = lang.trim(registry.byId("databaseSearch").getValue());
                pageNav.reset();
                startSearch();
            }

            function updatePageNavigation(res) {
                if (res.objectSearchResult !== null) {
                    // Objects found
                    pageNav.setTotalNumHits(res.objectSearchResult.totalNumHits);

                } else if (res.addressSearchResult !== null) {
                    // Addresses found
                    pageNav.setTotalNumHits(res.addressSearchResult.totalNumHits);
                }

                pageNav.updateDomNodes();
            }


            // Save as CSV values link 'onclick' function
            function saveAsCSV() {
                if (currentQuery === null)
                    return;
                
                if (has("ie")) {
                    var query = currentQuery.replace(/\s/g, " ");
                    window.open("mdek_download_csv_ie.html?query="+query, 'mywin', 'left=20,top=20,width=500,height=500,toolbar=0,menubar=0');

                } else {
                    QueryService.queryHQLToCSV(currentQuery, {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(data) {
                            dwr.engine.openInDownload(data);
                        },
                        errorHandler: function(errMsg, err) {
                            displayErrorMessage(err);
                        }
                    });
                }
            }

            function showLoadingZone() {
                style.set("databaseSearchLoadingZone", "visibility", "visible");
            }

            function hideLoadingZone() {
                style.set("databaseSearchLoadingZone", "visibility", "hidden");
            }

            /**
             * PUBLIC METHODS
             */
            
            pageResearchDb.saveAsCSV = saveAsCSV;
            pageResearchDb.startNewSearch = startNewSearch;

        }
    );

</script>
</head>

<body>

  <!-- CONTENT START -->
  
    <div id="researchDatabaseContentSection" class="contentBlockWhite">
      <div id="winNavi" style="top:0;">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=search-3#search-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
      </div>
      <div class="content">

        <!-- LEFT HAND SIDE CONTENT START -->
        <span class="label"><label for="databaseSearch" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7064)"><fmt:message key="dialog.research.db.title" /></label></span>
        <div class="inputContainer field grey">
          <span class="input"><input type="text" rows="10" mode="textarea" id="databaseSearch" style="width:100%;" data-dojo-type="dijit/form/SimpleTextarea" /></span> 

        <div class="inputContainer">
          <span class="button">
            <span style="float:right;">
              <button data-dojo-type="dijit/form/Button" id="researchDbSearch" title="<fmt:message key="dialog.research.db.search" />" onclick="pageResearchDb.startNewSearch()"><fmt:message key="dialog.research.db.search" /></button>
            </span>
            <span id="databaseSearchLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                <img src="img/ladekreis.gif" />
            </span>
          </span>
        </div>

        <!-- SEARCH RESULT LIST START -->
        <div class="spacer"></div>
        <div id="results" class="inputContainer">
          <span class="label"><fmt:message key="dialog.research.db.result" /></span>
          <span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="pageResearchDb.saveAsCSV()" title="<fmt:message key="dialog.research.db.saveAsCSV" />"><fmt:message key="dialog.research.db.saveAsCSV" /></a></span>
          <div class="listInfo">
            <span id="searchDatabaseResultsInfo" class="searchResultsInfo">&nbsp;</span>
            <span id="searchDatabaseResultsPaging" class="searchResultsPaging">&nbsp;</span>
            <div class="fill"></div>
          </div>

            <div class="tableContainer">
                <div id="datbaseSearchResults" autoHeight="20" contextMenu="none" defaultHideScrollbar="true"></div>
            </div>
          </div>
        <!-- SEARCH RESULT LIST END -->

        <!-- LEFT HAND SIDE CONTENT END -->
        
      </div>
    </div>
  <!-- CONTENT END -->
</body>
</html>
