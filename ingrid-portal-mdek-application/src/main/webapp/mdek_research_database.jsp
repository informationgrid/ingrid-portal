<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">

var scriptScope = this;

var resultsPerPage = 20;
var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("searchDatabaseResultsInfo"), pagingSpan:dojo.byId("searchDatabaseResultsPaging") });

var currentQuery = null;

_container_.addOnLoad(function() {
	dojo.event.connect("after", pageNav, "onPageSelected", function() { startSearch(); });
});


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

	UtilList.addTableIndices(resultList);
	UtilList.addIcons(resultList);

	dojo.widget.byId("datbaseSearchResults").store.setData(resultList);
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
//			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), errMsg), dialog.WARNING);				
		}		
	});
}

// Button onClick function
// Reads the input field, resets the navigation and starts a new query
this.startNewSearch = function() {
	currentQuery = dojo.string.trim(dojo.widget.byId("databaseSearch").getValue());
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
this.saveAsCSV = function() {
	if (currentQuery == null)
		return;
	
	if (dojo.render.html.ie) {
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
    dojo.html.setVisibility(dojo.byId("databaseSearchLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("databaseSearchLoadingZone"), "hidden");
}

</script>
</head>

<body>

  <!-- CONTENT START -->
  <div dojoType="ContentPane" layoutAlign="client">
  
    <div id="researchDatabaseContentSection" class="contentBlockWhite top">
      <div id="winNavi">
		<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 7063)" title="Hilfe">[?]</a>
  	  </div>
  	  <div class="content">

        <!-- LEFT HAND SIDE CONTENT START -->
        <span class="label"><label for="databaseSearch" onclick="javascript:dialog.showContextHelp(arguments[0], 7064, 'Datenbank-Suche')"><fmt:message key="dialog.research.db.title" /></label></span>
        <div class="inputContainer field grey noSpaceBelow">
          <span class="input"><input type="text" mode="textarea" id="databaseSearch" class="w640 h062" dojoType="ingrid:ValidationTextbox" /></span> 

          <div class="spacerField"></div>
    	  </div>

        <div class="inputContainer">
          <span class="button w644" style="height:20px !important;">
            <span style="float:right;">
              <button dojoType="ingrid:Button" title="Suchen" onClick="javascript:scriptScope.startNewSearch();"><fmt:message key="dialog.research.db.search" /></button>
    		</span>
			<span id="databaseSearchLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
				<img src="img/ladekreis.gif" />
			</span>
    	  </span>
    	</div>

        <!-- SEARCH RESULT LIST START -->
        <div class="spacer"></div>
        <div id="results" class="inputContainer noSpaceBelow full">
          <span class="label"><fmt:message key="dialog.research.db.result" /></span>
          <span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.saveAsCSV();" title="Trefferliste als CSV-Datei speichern"><fmt:message key="dialog.research.db.saveAsCSV" /></a></span>
          <div class="listInfo full">
            <span id="searchDatabaseResultsInfo" class="searchResultsInfo">&nbsp;</span>
            <span id="searchDatabaseResultsPaging" class="searchResultsPaging">&nbsp;</span>
        	<div class="fill"></div>
          </div>

			<div class="tableContainer rows20 full">
	    	    <table id="datbaseSearchResults" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable nosort relativePos">
	    	      <thead>
	    		      <tr>
	          			<th nosort="true" field="icon" dataType="String" width="23" nosort="true"></th>
	          			<th nosort="true" field="linkLabel" dataType="String" width="634" nosort="true">Name</th>
	    		      </tr>
	    	      </thead>
			      <colgroup>
				    <col width="23">
				    <col width="634">
			      </colgroup>
	    	      <tbody>
	    	      </tbody>
	    	    </table>
			</div>
	   	  </div>
        <!-- SEARCH RESULT LIST END -->

        <!-- LEFT HAND SIDE CONTENT END -->
        
      </div>
    </div>
  </div>
  <!-- CONTENT END -->
</body>
</html>
