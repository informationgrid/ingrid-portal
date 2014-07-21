<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<style>
a.notSelectable { margin-left:5px; text-decoration:none; color:#666666; }
a.resultText { margin-left:5px; text-decoration:none; color:#000; }
a.resultText:hover { text-decoration:underline; }
a.resultText.selected { color:#C21100; }
</style>

<script type="text/javascript">
    var pageResearchThesaurus = _container_;
    require( [
        "dojo/on",
        "dojo/aspect",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom",
        "dojo/keys",
        "dojo/dom-class",
        "dijit/registry",
        "dojo/dom-style",
        "dojo/Deferred",
        "ingrid/dialog",
        "ingrid/message",
        "ingrid/utils/PageNavigation",
        "ingrid/utils/List",
        "ingrid/utils/Grid",
        "ingrid/utils/Address",
        "ingrid/tree/ThesaurusTree",
        "ingrid/layoutCreator"
    ], function(on, aspect, lang, array, dom, keys, domClass, registry, style, Deferred,
            dialog, message, navigation, UtilList, UtilGrid, UtilAddress, ThesaurusTree, layoutCreator) {
                
                
            // The currently selected textNode. Used for colorization
            var selectedTextNode = null;
            // Number of rows of the result table(s)
            var resultsPerPage = 20;

            // Result caching so the results don't have to be reloaded when the user switches tabs
            var objResultsLoaded = false;
            var adrResultsLoaded = false;

            // The current topicId that was searched for
            var currentQuery = null;

            // Object and Address result navigation bars
            var objPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("searchThesaurusObjResultsInfo"), pagingSpan:dom.byId("searchThesaurusObjResultsPaging") });
            var adrPageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("searchThesaurusAdrResultsInfo"), pagingSpan:dom.byId("searchThesaurusAdrResultsPaging") });


            on(_container_, "Load", function() {
                createDOMElements();
                
                //initTree();

                // Pressing 'enter' on the input field is equal to a button click
                on(dom.byId("thesaurusSearch"), "keypress", function(event) {
                    if (event.keyCode == keys.ENTER) {
                        findTopic();
                    }
                });

                on(registry.byId("searchThesaurusNavResultContainer"), "SelectChild", startSearch);
                aspect.after(objPageNav, "onPageSelected", function() { objResultsLoaded = false; startSearch(); });
                aspect.after(adrPageNav, "onPageSelected", function() { adrResultsLoaded = false; startSearch(); });
                
                registry.byId("contentPane").resize();
            });

            function createDOMElements() {
                var searchThesaurusNavAddressesListStructure = [
                    {field: 'icon',name: '&nbsp;',width: '23px'},
                    {field: 'linkLabel',name: "<fmt:message key='dialog.research.thes.name' />",width: '320px'}
                ];
                layoutCreator.createDataGrid("searchThesaurusNavObjectsList", null, searchThesaurusNavAddressesListStructure, null);
                
                layoutCreator.createDataGrid("searchThesaurusNavAddressesList", null, searchThesaurusNavAddressesListStructure, null);
                pageResearchThesaurus.treeSearchThesaurusWidget = new ThesaurusTree({showRoot: false, service: "sns"}, "treeSearchThesaurus");
                on(registry.byId("treeSearchThesaurus"), "click", handleSelectNode);
            }

            function startNewQuery(topicId) {
                // build query
                currentQuery = topicId;
                objResultsLoaded = false;
                adrResultsLoaded = false;

                objPageNav.reset();
                adrPageNav.reset();

                startSearch();
            }

            function startSearch() {
                if (currentQuery === null)
                    return;

                // always load both infos!
                //var selectedTab = registry.byId("searchThesaurusNavResultContainer").selectedChildWidget.id;

                //if (selectedTab == "searchThesaurusNavObjects" && !objResultsLoaded) {
            //      console.debug("Starting object query for "+currentQuery);
            //      console.debug("startHit: "+objPageNav.getStartHit());
                QueryService.queryObjectsThesaurusTerm(currentQuery, objPageNav.getStartHit(), resultsPerPage, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(res){
                        objResultsLoaded = true;
                        updateObjectResults(res);
                        updateObjectNavigation(res);
                    },
                    timeout:30000,
                    errorHandler:function(message) {console.debug("Error in mdek_research_thesaurus.jsp: Error while searching for objects:"+message); }
                });
                //} else if (selectedTab == "searchThesaurusNavAddresses" && !adrResultsLoaded){
            //      console.debug("Starting address query for "+currentQuery);
            //      console.debug("startHit: "+adrPageNav.getStartHit());
                QueryService.queryAddressesThesaurusTerm(currentQuery, adrPageNav.getStartHit(), resultsPerPage, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(res){
                        adrResultsLoaded = true;
                        updateAddressResults(res);
                        updateAddressNavigation(res);
                    },
                    timeout:30000,
                    errorHandler:function(message) {console.debug("Error in mdek_research_thesaurus.jsp: Error while searching for addresses:"+message); }
                });
                //}
            }

            function updateObjectResults(res) {
                UtilList.addObjectLinkLabels(res.resultList);
                UtilList.addIcons(res.resultList);

                UtilGrid.setTableData("searchThesaurusNavObjectsList", res.resultList);
            }

            function updateAddressResults(res) {
                UtilList.addIcons(res.resultList);
                UtilAddress.addAddressTitles(res.resultList);
                UtilList.addAddressLinkLabels(res.resultList);
                UtilList.addTableIndices(res.resultList);

                UtilGrid.setTableData("searchThesaurusNavAddressesList", res.resultList);
            }

            function updateObjectNavigation(res) {
                objPageNav.setTotalNumHits(res.totalNumHits);
                objPageNav.updateDomNodes();
            }

            function updateAddressNavigation(res) {
                adrPageNav.setTotalNumHits(res.totalNumHits);
                adrPageNav.updateDomNodes();
            }


            // Node click handler
            function handleSelectNode(treeNode, node) {
            //  console.debug("node selected: "+treeNode.node.topicId);
                if (selectedTextNode) {
                    domClass.remove(selectedTextNode, "selected");
                }
                startNewQuery(treeNode.topicId);
            }


            // Displays the given topics in resultList in the second result tab
            function displayAssociatedTopics(resultList) {
                var _this = registry.byId("pageResearchThesaurus");
                var resultContainer = dom.byId("thesaurusResultContainer");
                resultContainer.innerHTML = "";

                if (resultList.length === 0) {
                    resultContainer.innerHTML = "<fmt:message key='sns.noResultHint' />";
                }

                array.forEach(resultList, function(term) {
                    if (term.type != "NON_DESCRIPTOR") {
                        var buttonLink = document.createElement("a");

                        buttonLink.onclick = function() {
                            pageResearchThesaurus.topicButtonClicked(term.topicId);
                        };
                        buttonLink.setAttribute("href", "javascript:void(0);");

                        buttonLink.setAttribute("title", "In Baumstruktur finden");
                        buttonLink.innerHTML = "<img src=\"img/ic_jump_tree.gif\" style=\"position: relative; top: 3px;\"/>";

                        var divElement = document.createElement("div");

                        var linkElement = document.createElement("a");
                        linkElement.innerHTML = term.label;

                        if (term.type == "DESCRIPTOR") {
                            domClass.add(linkElement, "resultText");
                            linkElement.setAttribute("id", "_researchThesLabel_"+term.topicId);
                            linkElement.onclick = function() {
                                pageResearchThesaurus.topicLabelClicked(term.topicId);
                            };
                            linkElement.setAttribute("href", "javascript:void(0);");

                            linkElement.setAttribute("title", "Begriff auswaehlen");
                            linkElement.topicId = term.topicId;
                        } else {
                            domClass.add(linkElement, "notSelectable");
                        }
                        
                        resultContainer.appendChild(divElement);
                        divElement.appendChild(buttonLink);
                        divElement.appendChild(linkElement);
                    }
                });
            }

            // Button onclick function for the associated topics. Switch the tab pane and
            // expand the tree to the given topicId
            function topicButtonClicked(topicId) {
                registry.byId("researchThesaurusTabContainer").selectChild("thesaurusTreeContainer");
                pageResearchThesaurus.treeSearchThesaurusWidget.expandToTopicWithId(topicId)
                .then(function(){
                    pageResearchThesaurus.startNewQuery(topicId);
                });
            }

            // Start a search for objects/addresses with 'topicId'
            function topicLabelClicked(topicId) {
            //  console.debug("clicked label: "+topicId);
                if (selectedTextNode) {
                    domClass.remove(selectedTextNode, "selected");
                }

                selectedTextNode = dom.byId("_researchThesLabel_"+topicId);
                domClass.add(selectedTextNode, "selected");

                startNewQuery(topicId);
            }

            // Search Button onClick function.
            //
            // This function reads the value of the TextBox 'thesSearch' and expands the tree from the root
            // to the corresponding node
            function findTopic() {
                var queryTerm = lang.trim(registry.byId("thesaurusSearch").getValue());

                if (queryTerm.length === 0) {
                    return;
                }

                registry.byId("researchThesaurusTabContainer").selectChild("thesaurusResultPane");

                SNSService.findTopics(queryTerm, userLocale, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback:function(result) {
                        if (result) {
                            UtilList.addSNSTopicLabels(result);
                            displayAssociatedTopics(result);
                        }},
                    timeout:0,
                    errorHandler:function(msg) { dialog.show("<fmt:message key='general.error' />", "<fmt:message key='sns.connectionError' />", dialog.WARNING); console.debug(msg); }
                });

            }

            function showLoadingZone() {
                style.set("thesaurusSearchLoadingZone", "visibility", "visible");
            }

            function hideLoadingZone() {
                style.set("thesaurusSearchLoadingZone", "visibility", "hidden");
            }

            /**
             * PUBLIC METHODS
             */
            pageResearchThesaurus.findTopic = findTopic;
            pageResearchThesaurus.topicButtonClicked = topicButtonClicked;
            pageResearchThesaurus.topicLabelClicked = topicLabelClicked;
            pageResearchThesaurus.startNewQuery = startNewQuery;
        }
    );

</script>
</head>

<body>
  <!-- CONTENT START -->
    <div id="researchThesaurusContentSection" data-dojo-type="dijit.layout.BorderContainer" design="sidebar" class="content" style="height:100%;" layoutAlign="client" >
        <!-- LEFT HAND SIDE CONTENT START -->
        <div data-dojo-type="dijit/layout/ContentPane" region="top">
            <div id="winNavi" style="top:0;">
                <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=search-2#search-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <span class="outer"><div>
            <span class="label">
                <label for="thesaurusSearch" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7062)">
                    <fmt:message key="dialog.research.thes.title" />
                </label>
            </span>
            <div class="inputContainer field grey">
                <span class="input">
                    <input type="text" id="thesaurusSearch" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" />
                </span>
            </div>
            </div></span>
            <div class="inputContainer">
                <span class="button" style="height:20px !important;">
                    <span style="float:right;">
                        <button data-dojo-type="dijit/form/Button" id="researchThesSearch" title="<fmt:message key="dialog.research.thes.search" />" onclick="pageResearchThesaurus.findTopic()">
                            <fmt:message key="dialog.research.thes.search" />
                        </button>
                    </span>
                    <span id="thesaurusSearchLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                        <img src="img/ladekreis.gif" />
                    </span>
                </span>
            </div>
        </div>

            <div data-dojo-type="dijit/layout/ContentPane" region="center" class="innerPadding">
            <div id="researchThesaurusTabContainer" data-dojo-type="dijit/layout/TabContainer" class="tabContainerWithBorderTop" style="height: 100%; border: 0;" selectedChild="thesaurusTreeContainer">
              <!-- first tab, tree view -->
              <div class="grey" data-dojo-type="dijit/layout/ContentPane" id="thesaurusTreeContainer" title="<fmt:message key="dialog.research.thes.tree" />">
                    <div id="treeSearchThesaurus"></div>
                </div>
    
                <!-- second tab, associated topics -->
                <div class="grey" data-dojo-type="dijit/layout/ContentPane" id="thesaurusResultPane" title="<fmt:message key="dialog.research.thes.list" />">
                  <span id="thesaurusResultContainer"></span>
                </div>
            </div>
            </div>
            
        <!-- LEFT HAND SIDE CONTENT END -->
        
        <!-- RIGHT HAND SIDE CONTENT START -->
        <div id="resultsThesaurus" data-dojo-type="dijit/layout/ContentPane" region="right" class="inputContainer innerPadding" style="width:400px;">
          <span class="label"><label class="inActive" for="thesaurusDescList"><fmt:message key="dialog.research.thes.result" /></label></span>
        
            <div id="searchThesaurusNavResultContainer" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="searchThesaurusNavObjects">

            <!-- TAB 1 START -->
            <div id="searchThesaurusNavObjects" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.research.thes.objects" />">
              <div class="listInfo">
                <span id="searchThesaurusObjResultsInfo" class="searchResultsInfo">&nbsp;</span>
                <span id="searchThesaurusObjResultsPaging" class="searchResultsPaging">&nbsp;</span>
                <div class="fill"></div>
              </div>

                <div class="tableContainer">
                    <div id="searchThesaurusNavObjectsList" autoHeight="22" contextMenu="none" defaultHideScrollbar="true"></div>
                </div>
            </div>
            <!-- TAB 1 END -->

            <!-- TAB 2 START -->
            <div id="searchThesaurusNavAddresses" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.research.thes.addresses" />">
              <div class="listInfo">
                <span id="searchThesaurusAdrResultsInfo" class="searchResultsInfo">&nbsp;</span>
                <span id="searchThesaurusAdrResultsPaging" class="searchResultsPaging">&nbsp;</span>
                  <div class="fill"></div>
              </div>

                <div class="tableContainer">
                    <div id="searchThesaurusNavAddressesList" autoHeight="22" contextMenu="none" defaultHideScrollbar="true"></div>
                </div>
            </div>
            <!-- TAB 2 END -->

            </div>
          </div>
        <!-- RIGHT HAND SIDE CONTENT END -->
      </div>
  <!-- CONTENT END -->

</body>
</html>
