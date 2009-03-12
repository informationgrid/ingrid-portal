<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<style type="text/css">
/* If the select box (AND/OR select) is opened for the first time a scrollbar is shown for the main tab.
   to prevent this behaviour, the overflow is made invisible */
div.dojoTabPaneWrapper { overflow:visible; }
</style>

<script type="text/javascript">
var scriptScope = this;

var resultsPerPage = 10;
//var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("adrSearchExtResultsInfo"), pagingSpan:dojo.byId("adrSearchExtResultsPaging") });
var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("adrSearchResultsInfo"), pagingSpan:dojo.byId("adrSearchResultsPaging") });

var currentQuery = {	// Map of strings/integers defining the query. See 'AddressExtSearchParamsBean.java' for details.
	queryTerm: null,		// representing the search query
	relation: null,			// relation that should be used to analyze the the query (AND, OR, ...)
	searchType: null,		// search whole word or substring (exact/like)
	searchRange: null,		// search all fields or specific fields
	street: null,			// street of the address
	zipCode: null,			// zipcode of the address
	city: null				// city of the address
}


_container_.addOnLoad(function() {
	// Initially select the first tab on load
	scriptScope.navInnerTab(0);
	dojo.widget.byId("addrTopicInputBool").setValue(0);
/*
    // Pressing 'enter' on the input field is equal to a button click
    dojo.event.connect(dojo.widget.byId("addrTopicInput").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startNewSearch();
            }
	});
*/
    // Pressing 'enter' on the street field is equal to a button click
    dojo.event.connect(dojo.widget.byId("addrSpaceStreet").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startNewSearch();
            }
	});
    // Pressing 'enter' on the zip code field is equal to a button click
    dojo.event.connect(dojo.widget.byId("addrSpaceZip").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startNewSearch();
            }
	});
    // Pressing 'enter' on the city field is equal to a button click
    dojo.event.connect(dojo.widget.byId("addrSpaceCity").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startNewSearch();
            }
	});

	dojo.event.connect("after", pageNav, "onPageSelected", function() { startSearch(); });
});


// Starts a search with the current parameters stored in 'currentQuery' and 'pageNav'
function startSearch() {
	QueryService.queryAddressesExtended(currentQuery, pageNav.getStartHit(), resultsPerPage, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(res) { updateResultTable(res); updatePageNavigation(res); },
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
		}		
	});
}

function updateResultTable(res) {
//	dojo.debugShallow(res);
	var resultList = res.resultList;
	UtilList.addAddressTitles(resultList);
	UtilList.addAddressLinkLabels(resultList);  
	UtilList.addTableIndices(resultList);
	UtilList.addIcons(resultList);

//	dojo.widget.byId("addressSearchExtResults").store.setData(resultList);
	dojo.widget.byId("addressSearchResults").store.setData(resultList);
}

function updatePageNavigation(res) {
	pageNav.setTotalNumHits(res.totalNumHits);
	pageNav.updateDomNodes();
}


// Search button function. Creates a new query and starts a new search
this.startNewSearch = function() {
	readQueryFromInput();
	pageNav.reset();
	startSearch();
}

// Reset-Button function. Reset all the input fields to their initial values and reset 'currentQuery'
this.resetInput = function() {
	dojo.byId("addrMode1").checked = true;
	dojo.byId("addrFields1").checked = true;
	dojo.widget.byId("adrSearchInput").setValue("");
	dojo.widget.byId("addrTopicInputBool").setValue(0);
	dojo.widget.byId("addrSpaceStreet").setValue("");
	dojo.widget.byId("addrSpaceZip").setValue("");
	dojo.widget.byId("addrSpaceCity").setValue("");

	readQueryFromInput();
}


// Subnavigation. 'subSectionIndex' specifies the index of the tab that should be selected
this.navInnerTab = function(subSectionIndex){
	// number of subsections == 1
	for (var i = 0; i < 1; i++) {
		if (i == subSectionIndex) {
			dojo.html.show(dojo.byId("addrTopic"+i));
		} else {
			dojo.html.hide(dojo.byId("addrTopic"+i));
		}
	}
}

function readQueryFromInput() {
	var searchTypeExact = dojo.byId("addrMode1").checked;
	var searchAllFields = dojo.byId("addrFields1").checked;

	currentQuery.queryTerm = dojo.string.trim(dojo.widget.byId("adrSearchInput").getValue());
	currentQuery.relation = dojo.widget.byId("addrTopicInputBool").getValue();
//	currentQuery.searchType = searchTypeExact ? "exact" : "contains";
//	currentQuery.searchRange = searchAllFields ? "all" : "some";
	currentQuery.searchType = searchTypeExact ? 0 : 1;	// Convert to int
	currentQuery.searchRange = searchAllFields ? 0 : 1;	// Convert to int

	currentQuery.street = dojo.string.trim(dojo.widget.byId("addrSpaceStreet").getValue());
	currentQuery.zipCode = dojo.string.trim(dojo.widget.byId("addrSpaceZip").getValue());
	currentQuery.city = dojo.string.trim(dojo.widget.byId("addrSpaceCity").getValue());

	dojo.debugShallow(currentQuery);
}

function resetInput() {
	dojo.byId("addrMode1").checked = true;
	dojo.byId("addrFields1").checked = true;

	dojo.widget.byId("adrSearchInput").setValue("");
	dojo.widget.byId("addrTopicInputBool").setValue(0);

	dojo.widget.byId("addrSpaceStreet").setValue("");
	dojo.widget.byId("addrSpaceZip").setValue("");
	dojo.widget.byId("addrSpaceCity").setValue("");
}


function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("addressSearchExtLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("addressSearchExtLoadingZone"), "hidden");
}

</script>
</head>

<body>
	<!-- EXTENDED SEARCH START -->
	<div id="extContentAdrContent" class="inputContainer noSpaceBelow">
	
	  <!-- EXTENDED SEARCH TAB CONTAINER START -->
		<div id="addr" dojoType="ingrid:TabContainer" doLayout="false" class="w845" selectedChild="addrTopic">
	
	    <!-- EXTENDED SEARCH TAB 1 START -->
			<div id="addrTopic" dojoType="ContentPane" class="blueTopBorder grey" label="<fmt:message key="dialog.research.ext.adr.theme" />">
	      <!-- EXTENDED SEARCH TAB 1 SUB 2 START -->
			<div id="addrTopic0">
	        <div class="tabContainerSubNavi">
	    	    <ul>
	    	      <li><a nohref="nohref" class="current" title="Suchmodus"><fmt:message key="dialog.research.ext.adr.mode" /></a></li>
	    	    </ul>
	    	  </div>
	        <div class="inputContainer field noSpaceBelow">
	          <span class="note"><b><fmt:message key="dialog.research.ext.adr.description" /></b></span>
	          <div class="spacer"></div>

	          <span class="label noSpaceBelow"><label class="inActive" for="addrTopicInputBool"><fmt:message key="dialog.research.ext.adr.contains" /></label>
	            <select dojoType="ingrid:Select" style="width:174px;" id="addrTopicInputBool">
	              <!-- TODO: fill in jsp -->
	            	<option value="0"><fmt:message key="dialog.research.ext.adr.contains.all" /></option>
	            	<option value="1"><fmt:message key="dialog.research.ext.adr.contains.one" /></option>
	            </select></span>

	          <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 7054, 'Suchmodus')"><fmt:message key="dialog.research.ext.adr.mode" /></label></span>
	          <div class="checkboxContainer">
	            <input type="radio" name="addrMode" id="addrMode1" class="radio entry first" checked />
	            <label class="inActive entry closer w116" for="addrMode1"><fmt:message key="dialog.research.ext.adr.full" /></label>
	            <input type="radio" name="addrMode" id="addrMode2" class="radio entry" />
	            <label class="inActive entry closer" for="addrMode2"><fmt:message key="dialog.research.ext.adr.substring" /></label>
	          </div>
	          <div class="fill spacer"></div>
	          <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 7055, 'Suchmodus')"><fmt:message key="dialog.research.ext.adr.fields" /></label></span>
	          <div class="checkboxContainer">
	            <input type="radio" name="addrFields" id="addrFields1" class="radio entry first" checked />
	            <label class="inActive entry closer w116" for="addrFields1"><fmt:message key="dialog.research.ext.adr.allTextFields" /></label>
	            <input type="radio" name="addrFields" id="addrFields2" class="radio entry" />
	            <label class="inActive entry closer" for="addrFields2"><fmt:message key="dialog.research.ext.adr.specialFields" /></label>
	          </div>
	    	  </div>
	    	</div>
	      <div class="fill"></div>
	      <div class="spacerField"></div>
	      <!-- EXTENDED SEARCH TAB 1 SUB 2 END -->
			</div>
	    <!-- EXTENDED SEARCH TAB 1 END -->

	    <!-- EXTENDED SEARCH TAB 2 START -->
			<div id="addrSpace" dojoType="ContentPane" class="blueTopBorder grey" label="<fmt:message key="dialog.research.ext.adr.location" />">
	      <!-- EXTENDED SEARCH TAB 2 SUB 1 START -->
			  <div id="addrSpace0">
	        <div class="tabContainerSubNavi">
	    	    <ul>
	    	      <li><a nohref="nohref" class="current" title="Raumbezug"><fmt:message key="dialog.research.ext.adr.locationRef" /></a></li>
	    	    </ul>
	    	  </div>
	        <div class="inputContainer field grey noSpaceBelow">
	          <span class="label"><label for="addrSpaceAdminUnit" onclick="javascript:dialog.showContextHelp(arguments[0], 7056, 'Adressinformationen')"><fmt:message key="dialog.research.ext.adr.locationDescription" /></label></span>
	          <span class="label"><label for="addrSpaceStreet" onclick="javascript:dialog.showContextHelp(arguments[0], 7057, 'Stra&szlig;e')"><fmt:message key="dialog.research.ext.adr.street" /></label></span>
	          <span class="input spaceBelow"><input type="text" id="addrSpaceStreet" name="addrSpaceStreet" class="w800" dojoType="ingrid:ValidationTextBox" /></span>
	          <span class="label"><label for="addrSpaceZip" onclick="javascript:dialog.showContextHelp(arguments[0], 7058, 'PLZ')"><fmt:message key="dialog.research.ext.adr.postCode" /></label></span>
	          <span class="input spaceBelow"><input type="text" id="addrSpaceZip" name="addrSpaceZip" class="w800" dojoType="ingrid:ValidationTextBox" /></span>
	          <span class="label"><label for="addrSpaceCity" onclick="javascript:dialog.showContextHelp(arguments[0], 7059, 'Ort')"><fmt:message key="dialog.research.ext.adr.city" /></label></span>
	          <span class="input"><input type="text" id="addrSpaceCity" name="addrSpaceCity" class="w800" dojoType="ingrid:ValidationTextBox" /></span>
	    	  </div>
	        <div class="spacerField"></div>
	     	</div>
	      <!-- EXTENDED SEARCH TAB 2 SUB 1 END -->
			</div>
	    <!-- EXTENDED SEARCH TAB 2 END -->

		</div>
	  <!-- EXTENDED SEARCH TAB CONTAINER END -->
	
	  <div class="inputContainer">
        <span class="button w805" style="height:20px !important;">
          <span style="float:right;">
            <button dojoType="ingrid:Button" title="Suchen" onClick="javascript:scriptScope.startNewSearch();"><fmt:message key="dialog.research.ext.adr.search" /></button>
		  </span>
          <span style="float:right;">
            <button dojoType="ingrid:Button" title="Zur&uuml;cksetzen" onClick="javascript:scriptScope.resetInput();"><fmt:message key="dialog.research.ext.adr.reset" /></button>
		  </span>
		  <span id="addressSearchExtLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
			<img src="img/ladekreis.gif" />
		  </span>
	    </span>
	  </div>

	</div>
	<!-- EXTENDED SEARCH END -->
</body>
