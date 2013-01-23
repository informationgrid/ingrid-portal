<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    dojo.require("dijit.layout.TabContainer");
    dojo.require("dijit.form.CheckBox");
    dojo.require("dijit.form.DateTextBox");
    
researchScriptScope = _container_;

var resultsPerPage = 10;
var objPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("objSearchResultsInfo"), pagingSpan:dojo.byId("objSearchResultsPaging") });
var adrPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("adrSearchResultsInfo"), pagingSpan:dojo.byId("adrSearchResultsPaging") });

var curObjQuery = null;     // string representing the current object query
var curAdrQuery = null;     // string representing the current address query

var lastQuery   = null;

    createDomElements();

//dojo.addOnLoad(function() {
    
    //if (dojo.isIE)
    //    researchScriptScope.connectKeyEvents();
    //else {
        dojo.connect(_container_, "onLoad", function(){
            researchScriptScope.connectKeyEvents();
        });
            
    //}
//});
        
researchScriptScope.connectKeyEvents = function() {
        console.debug("page research onload");
        // Pressing 'enter' on the address input field is equal to a button click.
        dojo.connect(dojo.byId("adrSearchInput"), "onkeypress",
            function(event) {
                if (event.keyCode == dojo.keys.ENTER) {
                    // Check if we have to do a normal or an extended search 
                    if (isExtendedSearchActive("adr")) {
                        scriptScopeResearchExtAddr.startNewSearch();
                    } else {
                        researchScriptScope.startNewAddressSearch();
                    }
                }
        });
        
        // Pressing 'enter' on the object input field is equal to a button click.
        dojo.connect(dojo.byId("objSearchInput"), "onkeypress",
            function(event) {
                if (event.keyCode == dojo.keys.ENTER) {
                    console.debug("enter");
                    // Check if we have to do a normal or an extended search 
                    if (isExtendedSearchActive("obj")) {
                        scriptScopeResearchExtObj.startNewSearch();
                    } else {
                        researchScriptScope.startNewObjectSearch();
                    }
                }
        });
    

        // Synchronize address and object search input
        dojo.connect(dojo.byId("objSearchInput"), "update",
            function() {
                var newVal = dijit.byId("objSearchInput").getValue();
                if (dijit.byId("adrSearchInput").getValue() != newVal) {
                    dijit.byId("adrSearchInput").setValue(newVal);
                }
        });
        dojo.connect(dijit.byId("adrSearchInput"), "update",
            function() {
                var newVal = dijit.byId("adrSearchInput").getValue();
                if (dijit.byId("objSearchInput").getValue() != newVal) {
                    dijit.byId("objSearchInput").setValue(newVal);
                }
        });
    
        dojo.connect(objPageNav, "onPageSelected", function() { startObjectSearch(); });
        dojo.connect(adrPageNav, "onPageSelected", function() { startAddressSearch(); });
}

function createDomElements() {
    var objectSearchResultsStructure = [
       {field: 'icon',name: '&nbsp;',width: '23px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.research.name' />",width: '700px'}
    ];
    createDataGrid("objectSearchResults", null, objectSearchResultsStructure, null);
    
    var addressSearchResultsStructure = [
       {field: 'icon',name: '&nbsp;',width: '23px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.research.name' />",width: '700px'}
    ];
    createDataGrid("addressSearchResults", null, addressSearchResultsStructure, null);
    
}

// Starts a search with the current parameters stored in 'curObjQuery' and 'objPageNav'
function startObjectSearch() {
    if (curObjQuery == null || dojo.trim(curObjQuery) == "") {
        return;
    }
    
    researchScriptScope.lastObjSearchType = "queryObjectsFullText";
    
    console.debug("query:");
    console.debug(curObjQuery);
    var def = queryObjectsFullText(objPageNav.getStartHit(), resultsPerPage);
    def.addCallback(function(res) {
        updateObjectResultTable(res); 
        updateObjectPageNavigation(res);
    });
}

function queryObjectsFullText(start, howMany) {
    var def = new dojo.Deferred();
    
    QueryService.queryObjectsFullText(curObjQuery, start, howMany, {
        preHook: showObjectLoadingZone,
        postHook: hideObjectLoadingZone,
        callback: def.callback,
        errorHandler: function(errMsg, err) {displayErrorMessage(err);}       
    });
    
    return def;
}

// Starts a search with the current parameters stored in 'curAdrQuery' and 'adrPageNav'
function startAddressSearch() {
    if (curAdrQuery == null || dojo.trim(curAdrQuery) == "") {
        return;
    }
    
    researchScriptScope.lastAddrSearchType = "queryAddressesFullText";

    var def = queryAddressesFullText(adrPageNav.getStartHit(), resultsPerPage);
    def.addCallback(function(res) {
        updateAddressResultTable(res); 
        updateAddressPageNavigation(res);
    });
}

function queryAddressesFullText(start, howMany) {
    var def = new dojo.Deferred();
    
    QueryService.queryAddressesFullText(curAdrQuery, start, howMany, {
        preHook: showAddressLoadingZone,
        postHook: hideAddressLoadingZone,
        callback: def.callback,
        errorHandler: function(errMsg, err) {displayErrorMessage(err);}       
    });
    
    return def;
}

function updateObjectResultTable(res) {
    var resultList = res.resultList;
    UtilList.addObjectLinkLabels(resultList);  
    UtilList.addIcons(resultList);

    UtilStore.updateWriteStore("objectSearchResults", resultList);
}
function updateAddressResultTable(res) {
    var resultList = res.resultList;
    UtilList.addAddressTitles(resultList);
    UtilList.addAddressLinkLabels(resultList);  
    UtilList.addIcons(resultList);

    UtilStore.updateWriteStore("addressSearchResults", resultList);
}


// Update the info and paging dom nodes after a search
function updateObjectPageNavigation(res) {
    objPageNav.setTotalNumHits(res.totalNumHits);
    objPageNav.updateDomNodes();
}
function updateAddressPageNavigation(res) {
    adrPageNav.setTotalNumHits(res.totalNumHits);
    adrPageNav.updateDomNodes();
}


// Object Search button function. Creates a new query and starts a new search
researchScriptScope.startNewObjectSearch = function() {
    curObjQuery = dojo.trim(dijit.byId("objSearchInput").getValue());
    objPageNav.reset();
    startObjectSearch();
}
// Address Search button function. Creates a new query and starts a new search
researchScriptScope.startNewAddressSearch = function() {
    curAdrQuery = dojo.trim(dijit.byId("adrSearchInput").getValue());
    adrPageNav.reset();
    startAddressSearch();
}



function toggleContent(type){
    if (type == "obj") {
        var contentArea = "extContentObj";
        var contentURL = "mdek_research_ext_obj.jsp";

        var contentAreaNode = dojo.byId("extContentObj");
        var searchFieldWidget = dijit.byId("objSearchInput");
        var searchButtonContainer = dojo.byId("objectSearchButtonContainer");
        var searchResultContainer = dojo.byId("objectSearchResultsContainer");

    } else if (type == "adr") {
        var contentArea = "extContentAdr";
        var contentURL = "mdek_research_ext_adr.jsp";

        var contentAreaNode = dojo.byId("extContentAdr");
        var searchFieldWidget = dijit.byId("adrSearchInput");
        var searchButtonContainer = dojo.byId("addressSearchButtonContainer");
        var searchResultContainer = dojo.byId("addressSearchResultsContainer");
    }
    // expand arrow
    var arrow = dojo.byId(contentArea + "ToggleArrow");
    // toggle arrow and content
    if (arrow.src.indexOf("ic_info_expand.gif") != -1){
        // load or toggle content for extended search
        var contentPane = dijit.byId(contentArea);
        if (contentPane.isLoaded == false){
            // load content
            contentPane.setHref(contentURL);
        }
        dojo.style(contentAreaNode, "display", "block");
        arrow.src = "img/ic_info_deflate.gif";
        dojo.style(searchButtonContainer, "display", "none")

    } else {
        dojo.style(contentAreaNode, "display", "none")
        arrow.src = "img/ic_info_expand.gif";

        dojo.style(searchButtonContainer, "display", "block")
    }
}


function isExtendedSearchActive(type) {
    if (type == "obj") {
        return (dojo.byId("extContentObjToggleArrow").src.indexOf("ic_info_expand.gif") == -1);
    } else if (type == "adr") {
        return (dojo.byId("extContentAdrToggleArrow").src.indexOf("ic_info_expand.gif") == -1);
    }
}

function showObjectLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("objectSearchLoadingZone"), "visible");
    dojo.style("objectSearchLoadingZone", "visibility", "visible");
}
function hideObjectLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("objectSearchLoadingZone"), "hidden");
    dojo.style("objectSearchLoadingZone", "visibility", "hidden");
}

function showAddressLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("addressSearchLoadingZone"), "visible");
    dojo.style("addressSearchLoadingZone", "visibility", "visible");
}
function hideAddressLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("addressSearchLoadingZone"), "hidden");
    dojo.style("addressSearchLoadingZone", "visibility", "hidden");
}

function showPreviewDialog() {
    var def = null;
    var isObjectSearch = dijit.byId("researchTabContainer").selectedChildWidget.id == "objSearch";
    
    if (isObjectSearch) {
        if (researchScriptScope.lastObjSearchType === "queryObjectsFullText") {
            def = queryObjectsFullText(0, 10000);
        } else if (researchScriptScope.lastObjSearchType === "queryObjectsExtended") {
            def = queryObjectsExtended(0, 10000);
        }
    } else {
        if (researchScriptScope.lastAddrSearchType === "queryAddressesFullText") {
            def = queryAddressesFullText(0, 10000);
        } else if (researchScriptScope.lastAddrSearchType === "queryAddressesExtended") {
            def = queryAddressesExtended(0, 10000);
        }
    }
    
    if (def === null) {
        console.debug("not yet searched or type not supported: " + researchScriptScope.lastObjSearchType + " : " + researchScriptScope.lastAddrSearchType);
        return;
    }
    
    def.addCallback(function(searchResult) {
        // params for the first (really delete object query) dialog.
        var params = {
            useDirtyData: false,
            searchResult: searchResult
        };
     
        if (isObjectSearch) {
            dialog.showPage(message.get("dialog.object.detailView.title"), "dialogs/mdek_detail_view_dialog.jsp?c="+userLocale, 755, 600, true, params);
        } else {
            dialog.showPage(message.get("dialog.address.detailView.title"), "dialogs/mdek_detail_view_address_dialog.jsp?c="+userLocale, 755, 600, true, params);
        }
    });
}

</script>

</head>
<body>
  <!-- CONTENT START -->
    <div id="researchSearchContentSection" class="contentBlockWhite top">
      <div id="winNavi">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=search-1#search-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
      </div>
      <div id="search" class="content">

        <!-- MAIN TAB CONTAINER START -->
        <div id="researchTabContainer" dojoType="dijit.layout.TabContainer" doLayout="false" selectedChild="objSearch">
          <!-- MAIN TAB 1 START -->
            <div id="objSearch" dojoType="dijit.layout.ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.research.objects" />">
              <div class="inputContainer field grey noSpaceBelow">
              <span class="input"><input type="text" id="objSearchInput" name="objSearchInput" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/></span>
              <div class="expandContent"><a href="javascript:void(0);" onclick="javascript:toggleContent('obj');" title="<fmt:message key="dialog.research.extSearch" />"><img id="extContentObjToggleArrow" src="img/ic_info_expand.gif" width="8" height="8" alt="Pfeil" /> <fmt:message key="dialog.research.extSearch" /></a></div>
              <div class="spacer"></div>
              </div>

             <div id="objectSearchButtonContainer" class="inputContainer" style="display:block;">
              <span class="button">
                <span style="float:right;">
                  <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.research.search" />" onClick="javascript:researchScriptScope.startNewObjectSearch();"><fmt:message key="dialog.research.search" /></button>
                </span>
                <span id="objectSearchLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                    <img src="img/ladekreis.gif" />
                </span>
              </span>
            </div>

            <div dojoType="dojox.layout.ContentPane" id="extContentObj" style="margin-top:5px;" executeScripts="true" loadingMessage="<fmt:message key="general.loading.data" /> ..."></div>

            <!-- OBJECT SEARCH RESULT LIST START -->
            <div class="spacer"></div>
            <div id="objectSearchResultsContainer" class="inputContainer noSpaceBelow">
              <span class="label left"><fmt:message key="dialog.research.result" /></span>
              <span class="label right" style="padding-right: 5px;"><a href="#" onclick="showPreviewDialog();"><img src="img/ic_fl_print.gif" width="11" height="11" alt="print" /><fmt:message key="dialog.research.preview" /></a></span>
              <div class="listInfo">
                <span id="objSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                <span id="objSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                  <div class="fill"></div>
              </div>
    
              <div class="tableContainer">
                  <div id="objectSearchResults" autoHeight="10" contextMenu="none" defaultHideScrollbar="true"></div>
               </div>
              </div>
            <!-- OBJECT SEARCH RESULT LIST END -->

            </div>
          <!-- MAIN TAB 1 END -->
            
          <!-- MAIN TAB 2 START -->
            <div id="adrSearch" dojoType="dijit.layout.ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.research.addresses" />">
  
            <div class="inputContainer field grey noSpaceBelow">
              <span class="input"><input type="text" id="adrSearchInput" name="adrSearchInput" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/></span>
              <div class="expandContent"><a href="javascript:void(0);" onclick="javascript:toggleContent('adr');" title="<fmt:message key="dialog.research.extSearch" />"><img id="extContentAdrToggleArrow" src="img/ic_info_expand.gif" width="8" height="8" alt="Pfeil" /> <fmt:message key="dialog.research.extSearch" /></a></div>
              <div class="spacer"></div>
              </div>

             <div id="addressSearchButtonContainer" class="inputContainer" style="display:block;">
              <span class="button">
                <span style="float:right;">
                  <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.research.search" />" onClick="javascript:researchScriptScope.startNewAddressSearch();"><fmt:message key="dialog.research.search" /></button>
                </span>
                <span id="addressSearchLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                    <img src="img/ladekreis.gif" />
                </span>
              </span>
            </div>

            <div dojoType="dojox.layout.ContentPane" widgetId="extContentAdr" id="extContentAdr" style="margin-top:5px;" executeScripts="true" loadingMessage="<fmt:message key="general.loading.data" />"></div>

            <!-- ADDRESS SEARCH RESULT LIST START -->
            <div class="spacer"></div>
            <div id="addressSearchResultsContainer" class="inputContainer noSpaceBelow">
              <span class="label left"><fmt:message key="dialog.research.result" /></span>
              <span class="label right" style="padding-right: 5px;"><a href="#" onclick="showPreviewDialog();"><img src="img/ic_fl_print.gif" width="11" height="11" alt="print" /><fmt:message key="dialog.research.preview" /></a></span>
              <div class="listInfo">
                <span id="adrSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                <span id="adrSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                  <div class="fill"></div>
              </div>
    
              <div class="tableContainer">
                  <div id="addressSearchResults" autoHeight="10" contextMenu="none" defaultHideScrollbar="true"></div>
                </div>
              </div>
            <!-- ADDRESS SEARCH RESULT LIST END -->

            </div>
          <!-- MAIN TAB 2 END -->
        </div>
        <!-- MAIN TAB CONTAINER END -->  
      </div>
    </div>
  <!-- CONTENT END -->
</body>
</html>
