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
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    var pageResearchSearch = _container_;
    require([
        "dojo/on",
        "dojo/aspect",
        "dojo/_base/lang",
        "dojo/dom",
        "dojo/keys",
        "dijit/registry",
        "dojo/dom-style",
        "dojo/Deferred",
        "ingrid/dialog",
        "ingrid/message",
        "ingrid/utils/PageNavigation",
        "ingrid/utils/List",
        "ingrid/utils/Store",
        "ingrid/utils/Address",
        "ingrid/layoutCreator",
        "ingrid/utils/LoadingZone"
    ], function(on, aspect, lang, dom, keys, registry, style, Deferred,
                dialog, message, navigation, UtilList, UtilStore, UtilAddress, layoutCreator, LoadingZone) {
        var resultsPerPage = 10;
        var objPageNav = new navigation.PageNavigation( {
            resultsPerPage: resultsPerPage,
            infoSpan: dom.byId( "objSearchResultsInfo" ),
            pagingSpan: dom.byId( "objSearchResultsPaging" )
        } );
        var adrPageNav = new navigation.PageNavigation( {
            resultsPerPage: resultsPerPage,
            infoSpan: dom.byId( "adrSearchResultsInfo" ),
            pagingSpan: dom.byId( "adrSearchResultsPaging" )
        } );

        var curObjQuery = null; // string representing the current object query
        var curAdrQuery = null; // string representing the current address query

        createDomElements();

        on( _container_, "Load", function() {
            connectKeyEvents();
        } );

        function connectKeyEvents() {
            console.debug( "page research onload" );
            // Pressing 'enter' on the address input field is equal to a button click.
            on( dom.byId( "adrSearchInput" ), "keypress", function(event) {
                if (event.keyCode == keys.ENTER) {
                    // Check if we have to do a normal or an extended search 
                    if (isExtendedSearchActive( "adr" )) {
                        pageResearchSearchExtAddr.startNewSearch();
                    } else {
                        pageResearchSearch.startNewAddressSearch();
                    }
                }
            } );

            // Pressing 'enter' on the object input field is equal to a button click.
            on( dom.byId( "objSearchInput" ), "keypress", function(event) {
                if (event.keyCode == keys.ENTER) {
                    console.debug( "enter" );
                    // Check if we have to do a normal or an extended search 
                    if (isExtendedSearchActive( "obj" )) {
                        pageResearchSearchExtObj.startNewSearch();
                    } else {
                        startNewObjectSearch();
                    }
                }
            } );

            // Synchronize address and object search input
            on( dom.byId( "objSearchInput" ), "update", function() {
                var newVal = registry.byId( "objSearchInput" ).getValue();
                if (registry.byId( "adrSearchInput" ).getValue() != newVal) {
                    registry.byId( "adrSearchInput" ).setValue( newVal );
                }
            } );
            on( registry.byId( "adrSearchInput" ), "update", function() {
                var newVal = registry.byId( "adrSearchInput" ).getValue();
                if (registry.byId( "objSearchInput" ).getValue() != newVal) {
                    registry.byId( "objSearchInput" ).setValue( newVal );
                }
            } );

            aspect.after( objPageNav, "onPageSelected", startObjectSearch );
            aspect.after( adrPageNav, "onPageSelected", startAddressSearch );
        }

        function createDomElements() {
            var objectSearchResultsStructure = [ {
                field: 'icon',
                name: '&nbsp;',
                width: '23px'
            }, {
                field: 'linkLabel',
                name: "<fmt:message key='dialog.research.name' />",
                width: '700px'
            } ];
            layoutCreator.createDataGrid( "objectSearchResults", null, objectSearchResultsStructure, null );

            var addressSearchResultsStructure = [ {
                field: 'icon',
                name: '&nbsp;',
                width: '23px'
            }, {
                field: 'linkLabel',
                name: "<fmt:message key='dialog.research.name' />",
                width: '700px'
            } ];
            layoutCreator.createDataGrid( "addressSearchResults", null, addressSearchResultsStructure, null );

        }

        // Starts a search with the current parameters stored in 'curObjQuery' and 'objPageNav'
        function startObjectSearch() {
            if (curObjQuery == null || lang.trim( curObjQuery ) == "") {
                return;
            }

            pageResearchSearch.lastObjSearchType = "queryObjectsFullText";

            console.debug( "query:" );
            console.debug( curObjQuery );
            var def = queryObjectsFullText( objPageNav.getStartHit(), resultsPerPage );
            def.then( function(res) {
                updateObjectResultTable( res );
                updateObjectPageNavigation( res );
            } );
        }

        function queryObjectsFullText(start, howMany) {
            var def = new Deferred();

            QueryService.queryObjectsFullText( curObjQuery, start, howMany, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback: def.resolve,
                errorHandler: function(errMsg, err) {
                    displayErrorMessage( err );
                }
            } );

            return def.promise;
        }

        // Starts a search with the current parameters stored in 'curAdrQuery' and 'adrPageNav'
        function startAddressSearch() {
            if (curAdrQuery == null || lang.trim( curAdrQuery ) == "") {
                return;
            }

            pageResearchSearch.lastAddrSearchType = "queryAddressesFullText";

            var def = queryAddressesFullText( adrPageNav.getStartHit(), resultsPerPage );
            def.then( function(res) {
                updateAddressResultTable( res );
                updateAddressPageNavigation( res );
            } );
        }

        function queryAddressesFullText(start, howMany) {
            var def = new Deferred();

            QueryService.queryAddressesFullText( curAdrQuery, start, howMany, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback: def.resolve,
                errorHandler: function(errMsg, err) {
                    displayErrorMessage( err );
                }
            } );

            return def.promise;
        }

        function updateObjectResultTable(res) {
            var resultList = res.resultList;
            UtilList.addObjectLinkLabels( resultList );
            UtilList.addIcons( resultList );

            UtilStore.updateWriteStore( "objectSearchResults", resultList );
        }
        function updateAddressResultTable(res) {
            var resultList = res.resultList;
            UtilAddress.addAddressTitles( resultList );
            UtilList.addAddressLinkLabels( resultList );
            UtilList.addIcons( resultList );

            UtilStore.updateWriteStore( "addressSearchResults", resultList );
        }

        // Update the info and paging dom nodes after a search
        function updateObjectPageNavigation(res) {
            objPageNav.setTotalNumHits( res.totalNumHits );
            objPageNav.updateDomNodes();
        }
        function updateAddressPageNavigation(res) {
            adrPageNav.setTotalNumHits( res.totalNumHits );
            adrPageNav.updateDomNodes();
        }

        // Object Search button function. Creates a new query and starts a new search
        function startNewObjectSearch() {
            curObjQuery = lang.trim( registry.byId( "objSearchInput" ).getValue() );
            objPageNav.reset();
            startObjectSearch();
        }
        // Address Search button function. Creates a new query and starts a new search
        function startNewAddressSearch() {
            curAdrQuery = lang.trim( registry.byId( "adrSearchInput" ).getValue() );
            adrPageNav.reset();
            startAddressSearch();
        }

        function toggleContent(type) {
            var contentArea, contentURL, contentAreaNode, searchFieldWidget, searchButtonContainer, searchResultContainer;
            if (type == "obj") {
                contentArea = "extContentObj";
                contentURL = "mdek_research_ext_obj.jsp";
                contentAreaNode = dom.byId( "extContentObj" );
                searchFieldWidget = registry.byId( "objSearchInput" );
                searchButtonContainer = dom.byId( "objectSearchButtonContainer" );
                searchResultContainer = dom.byId( "objectSearchResultsContainer" );

            } else if (type == "adr") {
                contentArea = "extContentAdr";
                contentURL = "mdek_research_ext_adr.jsp";
                contentAreaNode = dom.byId( "extContentAdr" );
                searchFieldWidget = registry.byId( "adrSearchInput" );
                searchButtonContainer = dom.byId( "addressSearchButtonContainer" );
                searchResultContainer = dom.byId( "addressSearchResultsContainer" );
            }
            // expand arrow
            var arrow = dom.byId( contentArea + "ToggleArrow" );
            // toggle arrow and content
            if (arrow.src.indexOf( "ic_info_expand.gif" ) != -1) {
                // load or toggle content for extended search
                var contentPane = registry.byId( contentArea );
                if (contentPane.href == "") {
                    // load content
                    contentPane.setHref( contentURL );
                }
                style.set( contentAreaNode, "display", "block" );
                arrow.src = "img/ic_info_deflate.gif";
                style.set( searchButtonContainer, "display", "none" );

            } else {
                style.set( contentAreaNode, "display", "none" );
                arrow.src = "img/ic_info_expand.gif";

                style.set( searchButtonContainer, "display", "block" );
            }
        }

        function isExtendedSearchActive(type) {
            if (type == "obj") {
                return (dom.byId( "extContentObjToggleArrow" ).src.indexOf( "ic_info_expand.gif" ) == -1);
            } else if (type == "adr") {
                return (dom.byId( "extContentAdrToggleArrow" ).src.indexOf( "ic_info_expand.gif" ) == -1);
            }
        }

        function showPreviewDialog() {
            var def = null;
            var isObjectSearch = registry.byId( "researchTabContainer" ).selectedChildWidget.id == "objSearch";

            if (isObjectSearch) {
                if (pageResearchSearch.lastObjSearchType === "queryObjectsFullText") {
                    def = queryObjectsFullText( 0, 10000 );
                } else if (pageResearchSearch.lastObjSearchType === "queryObjectsExtended") {
                    def = pageResearchSearchExtObj.queryObjectsExtended( 0, 10000 );
                }
            } else {
                if (pageResearchSearch.lastAddrSearchType === "queryAddressesFullText") {
                    def = queryAddressesFullText( 0, 10000 );
                } else if (pageResearchSearch.lastAddrSearchType === "queryAddressesExtended") {
                    def = pageResearchSearchExtAddr.queryAddressesExtended( 0, 10000 );
                }
            }

            if (def === null) {
                console.debug( "not yet searched or type not supported: " + pageResearchSearch.lastObjSearchType +
                    " : " + pageResearchSearch.lastAddrSearchType );
                return;
            }

            def.then( function(searchResult) {
                // params for the first (really delete object query) dialog.
                var params = {
                    useDirtyData: false,
                    searchResult: searchResult
                };

                if (isObjectSearch) {
                    dialog.showPage( message.get( "dialog.object.detailView.title" ),
                            "dialogs/mdek_detail_view_dialog.jsp?c=" + userLocale, 755, 600, true, params );
                } else {
                    dialog.showPage( message.get( "dialog.address.detailView.title" ),
                            "dialogs/mdek_detail_view_address_dialog.jsp?c=" + userLocale, 755, 600, true, params );
                }
            } );
        }

        /**
         * PUBLIC METHODS
         */
        pageResearchSearch.showPreviewDialog = showPreviewDialog;
        pageResearchSearch.startNewObjectSearch = startNewObjectSearch;
        pageResearchSearch.startNewAddressSearch = startNewAddressSearch;
        pageResearchSearch.toggleContent = toggleContent;

    } );
</script>

</head>
<body>
    <!-- CONTENT START -->
    <div id="researchSearchContentSection" class="contentBlockWhite top">
        <div id="winNavi">
            <a href="javascript:void(0);"
                onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=search-1#search-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');"
                title="<fmt:message key="general.help" />">[?]</a>
        </div>
        <div id="search" class="content">

            <!-- MAIN TAB CONTAINER START -->
            <div id="researchTabContainer" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="objSearch">
                <!-- MAIN TAB 1 START -->
                <div id="objSearch" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder"
                    title="<fmt:message key="dialog.research.objects" />">
                    <div class="inputContainer field grey noSpaceBelow">
                        <span class="input">
                            <input type="text" id="objSearchInput" name="objSearchInput" data-dojo-type="dijit/form/ValidationTextBox"
                                style="width: 100%;" />
                        </span>
                        <div class="expandContent">
                            <a href="javascript:void(0);" onclick="pageResearchSearch.toggleContent('obj');"
                                title="<fmt:message key="dialog.research.extSearch" />"><img id="extContentObjToggleArrow"
                                src="img/ic_info_expand.gif" width="8" height="8" alt="Pfeil" /> <fmt:message
                                    key="dialog.research.extSearch" /></a>
                        </div>
                        <div class="spacer"></div>
                    </div>

                    <div id="objectSearchButtonContainer" class="inputContainer" style="display: block;">
                        <span class="button">
                            <span style="float: right;">
                                <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.search" />"
                                    onclick="pageResearchSearch.startNewObjectSearch();">
                                    <fmt:message key="dialog.research.search" />
                                </button>
                            </span>
                            <span id="objectSearchLoadingZone" style="float: left; margin-top: 1px; z-index: 100; visibility: hidden">
                                <img src="img/ladekreis.gif" />
                            </span>
                        </span>
                    </div>

                    <div data-dojo-type="dojox/layout/ContentPane" id="extContentObj"
                       style="margin-top:5px;" executeScripts="true" scriptHasHooks="true" loadingMessage="<fmt:message key="general.loading.data" /> 

                    <!-- OBJECT SEARCH RESULT LIST START -->
                    <div class="spacer"></div>
                    <div id="objectSearchResultsContainer" class="inputContainer noSpaceBelow">
                        <span class="label left">
                            <fmt:message key="dialog.research.result" />
                        </span>
                        <span class="label right" style="padding-right: 5px;">
                            <a href="#" onclick="pageResearchSearch.showPreviewDialog()"><img src="img/ic_fl_print.gif" width="11" height="11" alt="print" />
                                <fmt:message key="dialog.research.preview" /></a>
                        </span>
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
                <div id="adrSearch" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder"
                    title="<fmt:message key="dialog.research.addresses" />">

                    <div class="inputContainer field grey noSpaceBelow">
                        <span class="input">
                            <input type="text" id="adrSearchInput" name="adrSearchInput" data-dojo-type="dijit/form/ValidationTextBox"
                                style="width: 100%;" />
                        </span>
                        <div class="expandContent">
                            <a href="javascript:void(0);" onclick="pageResearchSearch.toggleContent('adr');"
                                title="<fmt:message key="dialog.research.extSearch" />"><img id="extContentAdrToggleArrow"
                                src="img/ic_info_expand.gif" width="8" height="8" alt="Pfeil" /> <fmt:message
                                    key="dialog.research.extSearch" /></a>
                        </div>
                        <div class="spacer"></div>
                    </div>

                    <div id="addressSearchButtonContainer" class="inputContainer" style="display: block;">
                        <span class="button">
                            <span style="float: right;">
                                <button data-dojo-type="dijit/form/Button" id="researchSearch"
                                    title="<fmt:message key="dialog.research.search" />"
                                    onclick="pageResearchSearch.startNewAddressSearch();">
                                    <fmt:message key="dialog.research.search" />
                                </button>
                            </span>
                            <span id="addressSearchLoadingZone" style="float: left; margin-top: 1px; z-index: 100; visibility: hidden">
                                <img src="img/ladekreis.gif" />
                            </span>
                        </span>
                    </div>

                    <div data-dojo-type="dojox.layout.ContentPane" widgetId="extContentAdr" id="extContentAdr" style="margin-top: 5px;"
                        executeScripts="true" scriptHasHooks="true" loadingMessage="<fmt:message key="general.loading.data" />"></div>

                    <!-- ADDRESS SEARCH RESULT LIST START -->
                    <div class="spacer"></div>
                    <div id="addressSearchResultsContainer" class="inputContainer noSpaceBelow">
                        <span class="label left">
                            <fmt:message key="dialog.research.result" />
                        </span>
                        <span class="label right" style="padding-right: 5px;">
                            <a href="#" onclick="pageResearchSearch.showPreviewDialog()"><img src="img/ic_fl_print.gif" width="11" height="11" alt="print" />
                                <fmt:message key="dialog.research.preview" /></a>
                        </span>
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
