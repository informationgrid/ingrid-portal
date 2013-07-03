<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">
dojo.require("dijit.form.ValidationTextBox");
dojo.require("dijit.form.SimpleTextarea");

var scriptScopeResearchDb = _container_;

var resultsPerPage = 20;
var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("searchDatabaseResultsInfo"), pagingSpan:dojo.byId("searchDatabaseResultsPaging") });

var currentQuery = null;

dojo.connect(_container_, "onLoad", function() {
    createDOMElements();
	dojo.connect(pageNav, "onPageSelected", function() { startSearch(); });
});

function createDOMElements() {
    var datbaseSearchResultsStructure = [
       {field: 'icon',name: '&nbsp;',width: '23px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.research.db.name' />",width: '600px'}
    ];
    createDataGrid("datbaseSearchResults", null, datbaseSearchResultsStructure, null);
}


function updateResultTable(res) {
//	dojo.debugShallow(res);
	var resultList = [];

	if (res.objectSearchResult != null) {
		// Objects found
//		dojo.debugShallow(res.objectSearchResult);
		resultList = res.objectSearchResult.resultList;
		UtilList.addObjectLinkLabels(resultList);  

	} else if (res.addressSearchResult != null) {
		// Addresses found
//		dojo.debugShallow(res.addressSearchResult);
		resultList = res.addressSearchResult.resultList;
		UtilList.addAddressTitles(resultList);
		UtilList.addAddressLinkLabels(resultList);  
	}

	UtilList.addIcons(resultList);

	UtilGrid.setTableData("datbaseSearchResults", resultList);
}


// starts a database search with the given input
function startSearch() {
	if (currentQuery == null)
		return;

	QueryService.queryHQL(currentQuery, pageNav.getStartHit(), resultsPerPage, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(res) { updateResultTable(res); updatePageNavigation(res); },
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
//			dialog.show("<fmt:message key='general.error' />", dojo.string.substituteParams("<fmt:message key='dialog.generalError' />", errMsg), dialog.WARNING);				
		}		
	});
}

// Button onClick function
// Reads the input field, resets the navigation and starts a new query
scriptScopeResearchDb.startNewSearch = function() {
	currentQuery = dojo.trim(dijit.byId("databaseSearch").getValue());
	pageNav.reset();
	startSearch();
}

function updatePageNavigation(res) {
	if (res.objectSearchResult != null) {
		// Objects found
		pageNav.setTotalNumHits(res.objectSearchResult.totalNumHits);

	} else if (res.addressSearchResult != null) {
		// Addresses found
		pageNav.setTotalNumHits(res.addressSearchResult.totalNumHits);
	}

	pageNav.updateDomNodes();
}


// Save as CSV values link 'onclick' function
scriptScopeResearchDb.saveAsCSV = function() {
	if (currentQuery == null)
		return;
	
	if (dojo.isIE) {
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
    dojo.style("databaseSearchLoadingZone", "visibility", "visible");
}

function hideLoadingZone() {
    dojo.style("databaseSearchLoadingZone", "visibility", "hidden");
}

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
        <span class="label"><label for="databaseSearch" onclick="javascript:dialog.showContextHelp(arguments[0], 7064)"><fmt:message key="dialog.research.db.title" /></label></span>
        <div class="inputContainer field grey">
          <span class="input"><input type="text" rows="10" mode="textarea" id="databaseSearch" style="width:100%;" dojoType="dijit.form.SimpleTextarea" /></span> 

        <div class="inputContainer">
          <span class="button">
            <span style="float:right;">
              <button dojoType="dijit.form.Button" id="researchDbSearch" title="<fmt:message key="dialog.research.db.search" />" onClick="javascript:scriptScopeResearchDb.startNewSearch();"><fmt:message key="dialog.research.db.search" /></button>
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
          <span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScopeResearchDb.saveAsCSV();" title="<fmt:message key="dialog.research.db.saveAsCSV" />"><fmt:message key="dialog.research.db.saveAsCSV" /></a></span>
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
