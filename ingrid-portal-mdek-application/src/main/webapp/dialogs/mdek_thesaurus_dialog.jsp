<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <style>
            a.notSelectable {
                margin-left: 5px;
                text-decoration: none;
                color: #666666;
            }
            
            a.resultText {
                margin-left: 5px;
                text-decoration: none;
                color: #000;
            }
            
            a.resultText:hover {
                text-decoration: underline;
            }
            
            a.resultText.selected {
                color: #C21100;
            }
        </style>
        <script type="text/javascript">
            var pageThesaurusDlg = _container_;

            require([
                "dojo/_base/array",
                "dojo/_base/lang",
                "dojo/on",
                "dojo/topic",
                "dojo/keys",
                "dojo/query",
                "dojo/dom",
                "dojo/dom-class",
                "dojo/window",
                "dojo/dom-style",
                "dijit/registry",
                "ingrid/layoutCreator",
                "ingrid/utils/Grid",
                "ingrid/utils/List",
                "ingrid/utils/Tree",
                "ingrid/utils/Events",
                "ingrid/tree/ThesaurusTree"
            ], function(array, lang, on, topic, keys, query, dom, domClass, wnd, style, registry, layoutCreator, UtilGrid, UtilList, UtilTree, UtilEvents, ThesaurusTree) {


                    var selectedTextNode = null;
                    var selectedButton = null;

                    var customParams = null;
                    var service = null;

                    on(_container_, "Load", function() {
                        customParams = this.customParams;
                        if (customParams.service == "rdf") {
                            // hide search components
                            query(".search, div[widgetid='thesResultTabContainer_tablist_thesResultPane']").forEach(function(item) {
                                domClass.add(item, "hide");
                            });
                        }

                        init();

                        registry.byId("thesSearch").focus();

		                console.log("Publishing event: '/afterInitDialog/Thesaurus'");
        		        topic.publish("/afterInitDialog/Thesaurus");
                    });

                    function showLoadingZone() {
                        style.set('thesLoadingZone', "visibility", "visible");
                        registry.byId("acceptTopicListButton").set("disabled", true);
                        registry.byId("addSelectedTopicButton").set("disabled", true);
                        registry.byId("findTopicButton").set("disabled", true);
                    }

                    function hideLoadingZone() {
                        // in case dialog is closed before initialisation has ended
                        if (dom.byId("thesLoadingZone")) {
                            style.set('thesLoadingZone', "visibility", "hidden");
                            registry.byId("acceptTopicListButton").set("disabled", false);
                            registry.byId("addSelectedTopicButton").set("disabled", false);
                            registry.byId("findTopicButton").set("disabled", false);
                        }
                    }

                    function showStatus(msg) {
                        var status = dom.byId("statusText");
                        if (status) {
                            status.innerHTML = msg;
                        }
                    }

                    function createDOMElements() {
                        var service = "sns";
                        if (customParams.service)
                            service = customParams.service;

                        new ThesaurusTree({
                            showRoot: false,
                            service: service,
                            rootUrl: customParams.rootUrl,
                            showLoadingZone: showLoadingZone,
                            hideLoadingZone: hideLoadingZone,
                            showStatus: showStatus
                        }, "thesTree");

                        console.debug("create grid");
                        var thesaurusDescListStructure = [{
                            field: 'title',
                            name: 'name',
                            width: '245px'
                        }];
                        layoutCreator.createDataGrid("thesaurusDescList", null, thesaurusDescListStructure, null);
                    }

                    // The treeController and root Tree are initialized here
                    function init() {
                        createDOMElements();

                        // Enter key on the ValdiationTextbox has to start a search:
                        var inputField = dom.byId("thesSearch");
                        on(inputField, "keypress", function(event) {
                            if (event.keyCode == keys.ENTER) {
                                findTopic();
                            }
                        });

                    }

                    function resultTextClicked() {
                        if (selectedTextNode) {
                            domClass.remove(selectedTextNode, "selected");
                        }

                        selectedTextNode = this;
                        domClass.add(selectedTextNode, "selected");
                    }

                    function resultButtonClicked(topicId) {
                        if (selectedButton) {
                            selectedButton.checked = false;
                        }

                        selectedButton = dom.byId("_resultButton_" + topicId);

                        registry.byId("thesResultTabContainer").selectChild("thesTreePane");
                        // thesTreeWidget.expandToTopicWithId(topicId);

                        var tree = registry.byId("thesTree");
                        tree.expandToTopicWithId(topicId);
                    }

                    function displaySearchResults(resultList) {
                        var resultContainer = dom.byId("thesResultContainer");
                        resultContainer.innerHTML = "";
                        selectedTextNode = null;
                        selectedButton = null;

                        if (resultList.length === 0) {
                            resultContainer.innerHTML = "<fmt:message key='sns.noResultHint' />";
                        }

                        array.forEach(resultList, function(term) {
                            if (term.type != "NON_DESCRIPTOR") {
                                var buttonLink = document.createElement("a");
                                buttonLink.setAttribute("id", "_resultButton_" + term.topicId);
                                buttonLink.onclick = function() {
                                    resultButtonClicked(term.topicId);
                                };
                                buttonLink.setAttribute("href", "#");

                                buttonLink.setAttribute("title", "In Baumstruktur finden");
                                buttonLink.innerHTML = "<img src=\"img/ic_jump_tree.gif\" style=\"position: relative; top: 3px;\"/>";

                                var divElement = document.createElement("div");

                                var linkElement = document.createElement("a");
                                linkElement.innerHTML = term.label;

                                if (term.type == "DESCRIPTOR") {
                                    domClass.add(linkElement, "resultText");
                                    linkElement.setAttribute("id", "_resultText_" + term.topicId);
                                    linkElement.onclick = lang.hitch( linkElement, resultTextClicked );
                                    linkElement.setAttribute("href", "#");

                                    linkElement.setAttribute("title", "Begriff auswaehlen");
                                    linkElement.topicId = term.topicId;
                                    linkElement.term = term;
                                } else {
                                    domClass.add(linkElement, "notSelectable");
                                }

                                resultContainer.appendChild(divElement);
                                divElement.appendChild(buttonLink);
                                divElement.appendChild(linkElement);
                            }
                        });
                    }

                    // Search Button onClick function.
                    //
                    // This function reads the value of the TextBox 'thesSearch' and expands the tree from the root
                    // to the corresponding node
                    function findTopic() {
                        var queryTerm = registry.byId('thesSearch').getValue();
                        queryTerm = lang.trim(queryTerm);

                        if (queryTerm == "") {
                            showStatus("");
                            return;
                        }

                        //  clearResultContainer();
                        var resultPane = registry.byId("thesResultPane");
                        registry.byId("thesResultTabContainer").selectChild(resultPane);

                        var service = registry.byId("thesTree").getService();
                        service.findTopicsContains(queryTerm, userLocale, {
                            preHook: showLoadingZone,
                            postHook: hideLoadingZone,
                            callback: function(result) {
                                if (result) {
                                    UtilList.addSNSTopicLabels(result);
                                    displaySearchResults(result);
                                } else {
                                    // showStatus("<fmt:message key='sns.noResultHint' />");
                                }
                            },
                            errorHandler: function() {
                                showStatus("<fmt:message key='sns.connectionError' />");
                            },
                            exceptionHandler: function() {
                                showStatus("<fmt:message key='sns.connectionError' />");
                            }
                        });

                    }

                    // AddTopic Button onClick function.
                    //
                    // This function adds the currently selected node to the descriptorList if it isn't already in the store.
                    // The function also checks if a descriptor is selected (TODO: Shouldn't be done here?)
                    function addSelectedTopic() {
                        var selectedTab = registry.byId("thesResultTabContainer").selectedChildWidget.id;

                        if (selectedTab == "thesTreePane") {
                            var selectedNode = registry.byId('thesTree').selectedNode;
                            if (array.some(UtilGrid.getTableData('thesaurusDescList'), function(item) {
                                return (selectedNode.item.topicId == item.topicId);
                            }))
                                return;

                   			//if (!selectedNode) { // || selectedNode.item.type[0] != "DESCRIPTOR") {
                            if (!selectedNode || selectedNode.item.type != "DESCRIPTOR") {
                                return;
                            } else {
                                UtilGrid.addTableDataRow('thesaurusDescList', selectedNode.item);
                            }

                        } else
                        if (selectedTab == "thesResultPane" && selectedTextNode) {
                            if (array.some(UtilGrid.getTableData('thesaurusDescList'), function(item) {
                                return (selectedTextNode.topicId == item.topicId);
                            })) {
                                return;
                            } else {
                                UtilGrid.addTableDataRow('thesaurusDescList', selectedTextNode.term);
                            }
                        }
                    }

                    // Cancel Button onClick function.
                    //
                    // This function closes the dialog without saving any changes.
                    function closeThisDialog() {
                        registry.byId("thesTree").destroy();
                        registry.byId("pageDialog").hide();
                    }

                    // Finish Button onClick function.
                    //
                    // This function copies the descriptor list to the main mdek searchtopic list
                    function acceptTopicList() {
						if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/Thesaurus")) return;
                        var destGrid = null; //registry.byId("thesaurusTerms").store;

                        // get the correct store from the parameter of this widget
                        // since the id of this widget changes we have to search for it 
                        /*var floatingPanes = dijit.byType("FloatingPane");
                     for(var x in floatingPanes){
                     if (floatingPanes[x].customParams != null && floatingPanes[x].customParams.dstTable != null) {
                     destStore = registry.byId(floatingPanes[x].customParams.dstTable).store;
                     }
                     }*/
                        // var thisDialog = registry.byId("pageDialog");
                        if (customParams !== null && customParams.dstTable !== null) {
                            destGrid = customParams.dstTable;
                        } else { // default
                            destGrid = "thesaurusTerms";
                        }

                        var data = UtilGrid.getTableData("thesaurusDescList");
                        UtilList.addSNSTopicLabels(data);

                        array.forEach(data, function(topic) {
                            if (dojo.every(UtilGrid.getTableData(destGrid), function(item) {
                                return item.topicId != topic.topicId;
                            })) {
                                // Topic is new. Add it to the topic list
                                // TODO: Still needed??? Would be set in SnsStore now!
                                if (topic._title) {
                                    console.debug("Use _title!!!");
                                    topic.title = topic._title;
                                }
                                UtilGrid.addTableDataRow(destGrid, topic);
                            } else {
                                // Topic already exists in the destination List
                                return;
                            }
                        });

                        closeThisDialog();
                    }

                    pageThesaurusDlg = {
                        findTopic: findTopic,
                        closeThisDialog: closeThisDialog,
                        addSelectedTopic: addSelectedTopic,
                        acceptTopicList: acceptTopicList
                    };
                });
        </script>
    </head>
    <body>
        <!-- <div id="thesaurus" class=""> -->
            <!-- <div id="winNaviAbs" style="padding-bottom: 0px;">
                <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-6#maintanance-of-objects-6', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
            </div> -->
            <!-- <div id="thesaurusContent" class="content"> -->
            <div id="thesaurusContent" data-dojo-type="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" style="height:500px;">
                <!-- LEFT HAND SIDE CONTENT START -->
                <!-- <span class="outer" style="width:60%;"> -->
                <div data-dojo-type="dijit.layout.BorderContainer" splitter="false" region="leading" style="width: 60%;">
                    <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="top">
                        <div class="inputContainer field grey search">
                        	<span class="outer"><div>
                            <span class="label">
                                <label for="thesSearch" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7031)">
                                    <fmt:message key="dialog.thesaurusNavigator.title" />
                                </label>
                            </span><span class="input"><input type="text" id="thesSearch" name="thesSearch" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" /></span>
                            </div></span>
                            <div class="fill"></div>
                        </div>
                        <div class="inputContainerFooter search">
                            <span class="button"><span style="float:right;">
                                    <button type="button" data-dojo-type="dijit/form/Button" id="findTopicButton" class="buttonBlue" title="<fmt:message key="dialog.thesaurusNavigator.search" />" onclick="pageThesaurusDlg.findTopic">
                                        <fmt:message key="dialog.thesaurusNavigator.search" />
                                    </button>
                                </span></span>
                        </div>
                    </div>
                    <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="center" class="inputContainer">
                        <div id="thesResultTabContainer" data-dojo-type="dijit/layout/TabContainer" class="tabContainerWithBorderTop" style="height:200px;" selectedChild="thesTreePane">
                            <div class="grey" data-dojo-type="dijit/layout/ContentPane" id="thesTreePane" title="<fmt:message key="dialog.thesaurusNavigator.tree" />" style="">
                                <div id="thesTree">
                                </div>
                            </div>
                            <div class="grey search" data-dojo-type="dijit/layout/ContentPane" id="thesResultPane" title="<fmt:message key="dialog.thesaurusNavigator.list" />" style="">
                                <span id="thesResultContainer"></span>
                            </div>
                        </div>
                    </div>
                    <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="bottom" class="inputContainerFooter">
                        <span class="button"><span style="float:right;">
                                <button data-dojo-type="dijit/form/Button" onclick="pageThesaurusDlg.closeThisDialog">
                                    <fmt:message key="dialog.thesaurusNavigator.cancel" />
                                </button>
                            </span><span style="float:right; padding-right:10px;">
                                <button type="button" data-dojo-type="dijit/form/Button" id="addSelectedTopicButton" onclick="pageThesaurusDlg.addSelectedTopic()">
                                    <fmt:message key="dialog.thesaurusNavigator.add" />
                                </button>
                            </span><span id="thesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img id="thesImageZone" src="img/ladekreis.gif" /></span><span id="statusText" style="float:left; padding-top:6px;"></span></span>
                    </div>
                </div>
                <!-- <div class="fill"></div> -->
                 <!-- <span class="outer" style="width:1%; height: 100px;">
                     <div class="fill"></div>
                 </span> -->
                <!-- <div data-dojo-type="dijit/layout/BorderContainer" region="center" style="width:50px;">&nbsp;</div> -->
                <!-- LEFT HAND SIDE CONTENT END -->
                <!-- RIGHT HAND SIDE CONTENT START -->
                 <!-- <span class="outer" style="width:39%;"> -->
                 <div data-dojo-type="dijit.layout.BorderContainer" splitter="false" region="trailing" style="width: 38%;">
                     <div id="listThesaurus" data-dojo-type="dijit/layout/ContentPane" splitter="false" region="top" class="inputContainer">
                        <span class="label">
                            <label for="thesaurusDescList" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7032)">
                                <fmt:message key="dialog.thesaurusNavigator.descriptorList" />
                            </label>
                        </span>
                    </div>
                        <!-- <div class="listInfo">
                            <div class="fill">
                            </div>
                        </div> -->
                        <!--<div data-dojo-type="dijit/layout/ContentPane">-->
                    <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="center" class="inputContainer">
                        <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="center" class="tableContainer">
                            <div id="thesaurusDescList" autoHeight="19" class="hideTableHeader">
                            </div>
                        </div>
                    </div>
                        <!--</div>-->
                    <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="bottom" class="inputContainerFooter">
                        <span class="buttonNoBg" style="float:right;">
                            <button data-dojo-type="dijit/form/Button" id="acceptTopicListButton" class="buttonGrey" onclick="pageThesaurusDlg.acceptTopicList">
                                <fmt:message key="dialog.thesaurusNavigator.apply" />
                            </button>
                        </span>
                    </div>
                </div><!-- RIGHT HAND SIDE CONTENT END -->
                <!-- <div class="fill"></div> -->
            </div>
        <!-- </div> -->
    </body>
</html>