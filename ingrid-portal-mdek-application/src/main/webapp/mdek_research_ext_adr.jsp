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
    var pageResearchSearchExtAddr = _container_;

    require(["dojo/_base/lang", "dojo/on", "dojo/dom", "dojo/Deferred", "dojo/aspect", "dojo/dom-style", "dijit/registry",
         "ingrid/utils/List", "ingrid/utils/Address", "ingrid/utils/Store", "ingrid/utils/LoadingZone", "ingrid/utils/PageNavigation"],
         function(lang, on, dom, Deferred, aspect, style, registry, UtilList, UtilAddress, UtilStore, LoadingZone, navigation) {
    
        var resultsPerPage = 10;
        //var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("adrSearchExtResultsInfo"), pagingSpan:dom.byId("adrSearchExtResultsPaging") });
        var pageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("adrSearchResultsInfo"), pagingSpan:dom.byId("adrSearchResultsPaging") });
        
        var currentQuery = {    // Map of strings/integers defining the query. See 'AddressExtSearchParamsBean.java' for details.
            queryTerm: null,        // representing the search query
            relation: null,         // relation that should be used to analyze the the query (AND, OR, ...)
            searchType: null,       // search whole word or substring (exact/like)
            searchRange: null,      // search all fields or specific fields
            street: null,           // street of the address
            zipCode: null,          // zipcode of the address
            city: null              // city of the address
        };
        
        
        on(_container_, "Load", function() {
            // Initially select the first tab on load
            navInnerTab(0);
            registry.byId("addrTopicInputBool").setValue(0);
        
            // Pressing 'enter' on the street field is equal to a button click
            on(registry.byId("addrSpaceStreet").domNode, "keypress", function(event) {
                if (event.keyCode == event.KEY_ENTER) {
                    pageResearchSearchExtAddr.startNewSearch();
                }
            });
            // Pressing 'enter' on the zip code field is equal to a button click
            on(registry.byId("addrSpaceZip").domNode, "keypress", function(event) {
                if (event.keyCode == event.KEY_ENTER) {
                    pageResearchSearchExtAddr.startNewSearch();
                }
            });
            // Pressing 'enter' on the city field is equal to a button click
            on(registry.byId("addrSpaceCity").domNode, "keypress", function(event) {
                if (event.keyCode == event.KEY_ENTER) {
                    pageResearchSearchExtAddr.startNewSearch();
                }
            });
        
            aspect.after(pageNav, "onPageSelected", startSearch);
        });
        
        
        // Starts a search with the current parameters stored in 'currentQuery' and 'pageNav'
        function startSearch() {
            pageResearchSearch.lastAddrSearchType = "queryAddressesExtended";
            
            var def = queryAddressesExtended(pageNav.getStartHit(), resultsPerPage);
            def.then(function(res) {
                updateResultTable(res);
                updatePageNavigation(res);
            });
        }
        
        function queryAddressesExtended(start, howMany) {
            var def = new Deferred();
            
            QueryService.queryAddressesExtended(currentQuery, start, howMany, {
                preHook: lang.partial(LoadingZone.show, "addressSearchExtLoadingZone"),
                postHook: lang.partial(LoadingZone.hide, "addressSearchExtLoadingZone"),
                callback: def.resolve,
                errorHandler: function(errMsg, err) {displayErrorMessage(err);}
            });
            
            return def;
        }
        
        function updateResultTable(res) {
            var resultList = res.resultList;
            UtilAddress.addAddressTitles(resultList);
            UtilList.addAddressLinkLabels(resultList);
            UtilList.addIcons(resultList);
        
        //  registry.byId("addressSearchExtResults").store.setData(resultList);
            //registry.byId("addressSearchResults").store.setData(resultList);
            UtilStore.updateWriteStore("addressSearchResults", resultList);
        }
        
        function updatePageNavigation(res) {
            pageNav.setTotalNumHits(res.totalNumHits);
            pageNav.updateDomNodes();
        }
        
        
        // Search button function. Creates a new query and starts a new search
        function startNewSearch() {
            readQueryFromInput();
            pageNav.reset();
            startSearch();
        };
        
        // Reset-Button function. Reset all the input fields to their initial values and reset 'currentQuery'
        function resetInput() {
            dom.byId("addrMode2").checked = true;
            dom.byId("addrFields1").checked = true;
            registry.byId("adrSearchInput").setValue("");
            registry.byId("addrTopicInputBool").setValue(0);
            registry.byId("addrSpaceStreet").setValue("");
            registry.byId("addrSpaceZip").setValue("");
            registry.byId("addrSpaceCity").setValue("");
        
            readQueryFromInput();
        };
        
        
        // Subnavigation. 'subSectionIndex' specifies the index of the tab that should be selected
        function navInnerTab(subSectionIndex) {
            // number of subsections == 1
            for (var i = 0; i < 1; i++) {
                if (i == subSectionIndex) {
                    style.set("addrTopic"+i, "display", "block");
                } else {
                    style.set("addrTopic"+i, "display", "none");
                }
            }
        }
        
        function readQueryFromInput() {
            var searchTypeExact = dom.byId("addrMode1").checked;
            var searchAllFields = dom.byId("addrFields1").checked;
        
            currentQuery.queryTerm = dojo.string.trim(registry.byId("adrSearchInput").getValue());
            currentQuery.relation = registry.byId("addrTopicInputBool").getValue();
        //  currentQuery.searchType = searchTypeExact ? "exact" : "contains";
        //  currentQuery.searchRange = searchAllFields ? "all" : "some";
            currentQuery.searchType = searchTypeExact ? 0 : 1;  // Convert to int
            currentQuery.searchRange = searchAllFields ? 0 : 1; // Convert to int
        
            currentQuery.street = dojo.string.trim(registry.byId("addrSpaceStreet").getValue());
            currentQuery.zipCode = dojo.string.trim(registry.byId("addrSpaceZip").getValue());
            currentQuery.city = dojo.string.trim(registry.byId("addrSpaceCity").getValue());
        
        }
        
/*         function resetInput() {
            dom.byId("addrMode1").checked = true;
            dom.byId("addrFields1").checked = true;
        
            registry.byId("adrSearchInput").setValue("");
            registry.byId("addrTopicInputBool").setValue(0);
        
            registry.byId("addrSpaceStreet").setValue("");
            registry.byId("addrSpaceZip").setValue("");
            registry.byId("addrSpaceCity").setValue("");
        }
         */
        
        pageResearchSearchExtAddr = {
            queryAddressesExtended: queryAddressesExtended,
            startNewSearch: startNewSearch,
            resetInput: resetInput
        }
    });
</script>
</head>

<body>
    <!-- EXTENDED SEARCH START -->
      <!-- EXTENDED SEARCH TAB CONTAINER START -->
        <div id="addr" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="addrTopic">
    
        <!-- EXTENDED SEARCH TAB 1 START -->
            <div id="addrTopic" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder grey" title="<fmt:message key="dialog.research.ext.adr.theme" />">
          <!-- EXTENDED SEARCH TAB 1 SUB 2 START -->
            <div id="addrTopic0">
            <div class="tabContainerSubNavi">
                <ul>
                  <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.adr.mode" />"><fmt:message key="dialog.research.ext.adr.mode" /></a></li>
                </ul>
              </div>
            <div class="spacer"></div>
            <div class="inputContainer field inTabWithMenu noSpaceBelow">
              <span class="note"><b><fmt:message key="dialog.research.ext.adr.description" /></b></span>
              <div class="spacer"></div>

              <span class="label noSpaceBelow"><label class="inActive" for="addrTopicInputBool"><fmt:message key="dialog.research.ext.adr.contains" /></label>
                <select data-dojo-type="dijit/form/Select" style="width:190px;" id="addrTopicInputBool"><option value="0"><fmt:message key="dialog.research.ext.adr.contains.all" /></option><option value="1"><fmt:message key="dialog.research.ext.adr.contains.one" /></option></select>
              </span>

              <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7054)"><fmt:message key="dialog.research.ext.adr.mode" /></label></span>
              <div class="checkboxContainer">
                <input type="radio" name="addrMode" id="addrMode1" class="radio entry first" />
                <label class="inActive entry closer w116" for="addrMode1"><fmt:message key="dialog.research.ext.adr.full" /></label>
                <input type="radio" name="addrMode" id="addrMode2" class="radio entry" checked />
                <label class="inActive entry closer" for="addrMode2"><fmt:message key="dialog.research.ext.adr.substring" /></label>
              </div>
              <div class="fill spacer"></div>
              <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7055)"><fmt:message key="dialog.research.ext.adr.fields" /></label></span>
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
            <div id="addrSpace" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder grey" title="<fmt:message key="dialog.research.ext.adr.location" />">
          <!-- EXTENDED SEARCH TAB 2 SUB 1 START -->
              <div id="addrSpace0">
            <div class="tabContainerSubNavi">
                <ul>
                  <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.adr.locationRef" />"><fmt:message key="dialog.research.ext.adr.locationRef" /></a></li>
                </ul>
              </div>
            <div class="spacer"></div>
            <div class="inputContainer field inTabWithMenu grey noSpaceBelow">
              <span class="label"><label for="addrSpaceAdminUnit" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7056)"><fmt:message key="dialog.research.ext.adr.locationDescription" /></label></span>
              <span class="label"><label for="addrSpaceStreet" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7057)"><fmt:message key="dialog.research.ext.adr.street" /></label></span>
              <span class="input spaceBelow"><input type="text" id="addrSpaceStreet" name="addrSpaceStreet" class="w800" data-dojo-type="dijit/form/ValidationTextBox" /></span>
              <span class="label"><label for="addrSpaceZip" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7058)"><fmt:message key="dialog.research.ext.adr.postCode" /></label></span>
              <span class="input spaceBelow"><input type="text" id="addrSpaceZip" name="addrSpaceZip" class="w800" data-dojo-type="dijit/form/ValidationTextBox" /></span>
              <span class="label"><label for="addrSpaceCity" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7059)"><fmt:message key="dialog.research.ext.adr.city" /></label></span>
              <span class="input"><input type="text" id="addrSpaceCity" name="addrSpaceCity" class="w800" data-dojo-type="dijit/form/ValidationTextBox" /></span>
              </div>
            <div class="spacerField"></div>
            </div>
          <!-- EXTENDED SEARCH TAB 2 SUB 1 END -->
            </div>
        <!-- EXTENDED SEARCH TAB 2 END -->

        </div>
      <!-- EXTENDED SEARCH TAB CONTAINER END -->
    
      <div class="inputContainer">
        <span class="button">
          <span style="float:right;">
            <button data-dojo-type="dijit/form/Button" id="researchExtAddrSearch" title="<fmt:message key="dialog.research.ext.adr.search" />" onclick="pageResearchSearchExtAddr.startNewSearch()"><fmt:message key="dialog.research.ext.adr.search" /></button>
          </span>
          <span style="float:right;">
            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.ext.adr.reset" />" onclick="pageResearchSearchExtAddr.resetInput()"><fmt:message key="dialog.research.ext.adr.reset" /></button>
          </span>
          <span id="addressSearchExtLoadingZone" style="float:left; margin-top:5px; z-index: 100; visibility:hidden">
            <img src="img/ladekreis.gif" />
          </span>
        </span>
      </div>
    <!-- EXTENDED SEARCH END -->
</body>
