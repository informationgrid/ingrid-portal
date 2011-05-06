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
            dojo.require("ingrid.dijit.ThesaurusTree");
            
            var scriptScope = this;
            
            var selectedTextNode = null;
            var selectedButton = null;
            //dojo.addOnLoad(function(){
            //	if (dojo.isIE)
            //        init();
            //    else
                    dojo.connect(_container_, "onLoad", function(){
                    	init();
                	});
            //});
            
            function showLoadingZone(){
                dojo.byId('thesLoadingZone').style.visibility = "visible";
                dijit.byId("acceptTopicListButton").set("disabled", true);
                dijit.byId("addSelectedTopicButton").set("disabled", true);
                dijit.byId("findTopicButton").set("disabled", true);
            }
            
            function hideLoadingZone(){
                // in case dialog is closed before initialisation has ended
                if (dojo.byId("thesLoadingZone")) {
                    dojo.style('thesLoadingZone', "visibility", "hidden");
                    dijit.byId("acceptTopicListButton").set("disabled", false);
                    dijit.byId("addSelectedTopicButton").set("disabled", false);
                    dijit.byId("findTopicButton").set("disabled", false);
                }
            }
            
            function showStatus(msg){
                var status = dojo.byId("statusText");
                if (status) {
                    status.innerHTML = msg;
                }
            }
            
            function createDOMElements(){
                thesTreeWidget = new ingrid.dijit.ThesaurusTree({
                    domId:"thesTree", 
                    showLoadingZone:showLoadingZone, 
                    hideLoadingZone:hideLoadingZone, 
                    showStatus:showStatus 
                });
                //dojo.connect(dijit.byId("treeSearchThesaurus"), "onClick", scriptScope.handleSelectNode);
                
                console.debug("create grid");
                var thesaurusDescListStructure = [
                    {field: 'label',name: 'name',width: '247px'}
                ];
                createDataGrid("thesaurusDescList", null, thesaurusDescListStructure, null);
            }
            
            // The treeController and root Tree are initialized here
            function init(){
                createDOMElements();
                
                // Register the node click handler 
                //dojo.subscribe(dijit.byId('thesTree').eventNames.select, "handleSelectNode");
                //dojo.connect(dijit.byId('thesTree'), "onSelect", "handleSelectNode");
            
                // Enter key on the ValdiationTextbox has to start a search:
                var inputField = dojo.byId("thesSearch");
                dojo.connect(inputField, "onkeypress", function(event) {
                	if (event.keyCode == dojo.keys.ENTER) {
						findTopic();
                	}
                });
                 
            }
            
            resultTextClicked = function(topicId){
                if (selectedTextNode) {
                    dojo.removeClass(selectedTextNode, "selected");
                }
                
                selectedTextNode = dojo.byId("_resultText_" + topicId);
                dojo.addClass(selectedTextNode, "selected");
            }
            
            resultButtonClicked = function(topicId){
                if (selectedButton) {
                    selectedButton.checked = false;
                }
                
                selectedButton = dojo.byId("_resultButton_" + topicId);
                
                dijit.byId("thesResultTabContainer").selectChild("thesTreePane");
                thesTreeWidget.expandToTopicWithId(topicId);
            }
            
            
            function displaySearchResults(resultList){
                var resultContainer = dojo.byId("thesResultContainer");
                resultContainer.innerHTML = "";
                selectedTextNode = null;
                selectedButton = null;
                
                if (resultList.length == 0) {
                    resultContainer.innerHTML = "<fmt:message key='sns.noResultHint' />";
                }
                
                dojo.forEach(resultList, function(term){
                    if (term.type != "NON_DESCRIPTOR") {
                        var buttonLink = document.createElement("a");
                        buttonLink.setAttribute("id", "_resultButton_" + term.topicId);
                        buttonLink.onclick = function(){
                            resultButtonClicked(term.topicId);
                        }
                        buttonLink.setAttribute("href", "javascript:void(0);");
                        
                        buttonLink.setAttribute("title", "In Baumstruktur finden");
                        buttonLink.innerHTML = "<img src=\"img/ic_jump_tree.gif\" style=\"position: relative; top: 3px;\"/>";
                        
                        var divElement = document.createElement("div");
                        
                        var linkElement = document.createElement("a");
                        linkElement.innerHTML = term.label;
                        
                        if (term.type == "DESCRIPTOR") {
                            dojo.addClass(linkElement, "resultText");
                            linkElement.setAttribute("id", "_resultText_" + term.topicId);
                            linkElement.onclick = function(){
                                resultTextClicked(term.topicId);
                            }
                            linkElement.setAttribute("href", "javascript:void(0);");
                            
                            linkElement.setAttribute("title", "Begriff auswaehlen");
                            linkElement.topicId = term.topicId;
                            linkElement.term = term;
                        }
                        else {
                            dojo.addClass(linkElement, "notSelectable");
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
            findTopic = function(){
                var queryTerm = dijit.byId('thesSearch').getValue();
                queryTerm = dojo.trim(queryTerm);
                
                if (queryTerm == "") {
                    showStatus("");
                    return;
                }
                
                //	clearResultContainer();
                var resultPane = dijit.byId("thesResultPane");
                dijit.byId("thesResultTabContainer").selectChild(resultPane);

                SNSService.findTopics(queryTerm, {
                    preHook: function(){
                        showLoadingZone();
                    },
                    postHook: function(){
                        hideLoadingZone();
                    },
                    callback: function(result){
                        if (result) {
                            UtilList.addSNSTopicLabels(result);
                            displaySearchResults(result);
                        }
                        else {
                            //				showStatus("<fmt:message key='sns.noResultHint' />");
                        }
                    },
                    timeout: 0,
                    errorHandler: function(msg){
                        showStatus("<fmt:message key='sns.connectionError' />");
                    },
                    exceptionHandler: function(msg){
                        showStatus("<fmt:message key='sns.connectionError' />");
                    }
                });
                
            };
            
            // AddTopic Button onClick function.
            //
            // This function adds the currently selected node to the descriptorList if it isn't already in the store.
            // The function also checks if a descriptor is selected (TODO: Shouldn't be done here?)
            addSelectedTopic = function(){
                var selectedTab = dijit.byId("thesResultTabContainer").selectedChildWidget.id;
                
                if (selectedTab == "thesTreePane") {
                    var selectedNode = dijit.byId('thesTree').selectedNode;
                    if (dojo.some(UtilGrid.getTableData('thesaurusDescList'), function(item){
                        return (selectedNode.item.topicId[0] == item.topicId);
                    })) 
                        return;
                    
                    if (!selectedNode || selectedNode.item.type[0] != "DESCRIPTOR") {
                        return;
                    }
                    else {
                        UtilGrid.addTableDataRow('thesaurusDescList', itemToJS(dijit.byId('thesTree').model.store, selectedNode.item));
                    }
                    
                }
                else 
                    if (selectedTab == "thesResultPane" && selectedTextNode) {
                        if (dojo.some(UtilGrid.getTableData('thesaurusDescList'), function(item){
                            return (selectedTextNode.topicId == item.topicId);
                        })) {
                            return;
                        }
                        else {
                            UtilGrid.addTableDataRow('thesaurusDescList', selectedTextNode.term);
                        }
                    }
            };
            
            // Cancel Button onClick function.
            //
            // This function closes the dialog without saving any changes.
            closeThisDialog = function(){
                thesTreeWidget.destroy();//loadingDivId = null;
                dijit.byId("pageDialog").hide();
            }
            
            // Finish Button onClick function.
            //
            // This function copies the descriptor list to the main mdek searchtopic list
            acceptTopicList = function(){
                var destGrid = null;//dijit.byId("thesaurusTerms").store;
                
                // get the correct store from the parameter of this widget
                // since the id of this widget changes we have to search for it	
                /*var floatingPanes = dijit.byType("FloatingPane");
                 for(var x in floatingPanes){
                 if (floatingPanes[x].customParams != null && floatingPanes[x].customParams.dstTable != null) {
                 destStore = dijit.byId(floatingPanes[x].customParams.dstTable).store;
                 }
                 }*/
                var thisDialog = dijit.byId("pageDialog");
                if (thisDialog.customParams != null && thisDialog.customParams.dstTable != null) {
                	destGrid = thisDialog.customParams.dstTable;
                } else { // default
                	destGrid = "thesaurusTerms"; 
				}
                dojo.forEach(UtilGrid.getTableData("thesaurusDescList"), function(topic){
                    if (dojo.every(UtilGrid.getTableData(destGrid), function(item){
                        return item.topicId != topic.topicId;
                    })) {
                        // Topic is new. Add it to the topic list
                        //topic.Id = UtilStore.getNewKey(destStore);
                        if (topic._title) {
                            console.debug("Use _title!!!");
                            topic.title = topic._title;
                        }
                        //destStore.newItem(itemToJS(srcStore, topic));
                        UtilGrid.addTableDataRow(destGrid, topic);
                    }
                    else {
                        // Topic already exists in the destination List
                        return;
                    }
                });
                
                closeThisDialog();
            }
        </script>
    </head>
    <body>
        <div id="thesaurus" class="">
            <div id="winNavi">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=maintanance-of-objects-6#maintanance-of-objects-6', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <div id="thesaurusContent" class="content">
                <!-- LEFT HAND SIDE CONTENT START -->
                <span class="outer" style="width:60%;">
                <div class="inputContainer field grey">
                	<span class="outer"><div>
                    <span class="label">
                        <label for="thesSearch" onclick="javascript:dialog.showContextHelp(arguments[0], 7031, 'Suche nach Deskriptoren und Ordnungsbegriffen')">
                            <fmt:message key="dialog.thesaurusNavigator.title" />
                        </label>
                    </span><span class="input"><input type="text" id="thesSearch" name="thesSearch" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                    </div></span>
                    <div class="fill"></div>
                </div>
                <div class="inputContainerFooter">
                    <span class="button"><span style="float:right;">
                            <button type="button" dojoType="dijit.form.Button" id="findTopicButton" class="buttonBlue" title="<fmt:message key="dialog.thesaurusNavigator.search" />" onClick="findTopic">
                                <fmt:message key="dialog.thesaurusNavigator.search" />
                            </button>
                        </span></span>
                </div>
                <div class="inputContainer">
                    <div id="thesResultTabContainer" dojoType="dijit.layout.TabContainer" class="tabContainerWithBorderTop" style="height:200px;" selectedChild="thesTreePane">
                        <div class="grey" dojoType="dijit.layout.ContentPane" id="thesTreePane" title="<fmt:message key="dialog.thesaurusNavigator.tree" />" style="">
                            <div id="thesTree">
                            </div>
                        </div>
                        <div class="grey" dojoType="dijit.layout.ContentPane" id="thesResultPane" title="<fmt:message key="dialog.thesaurusNavigator.list" />" style="">
                            <span id="thesResultContainer"></span>
                        </div>
                    </div>
                </div>
                <div class="inputContainerFooter">
                    <span class="button"><span style="float:right;">
                            <button dojoType="dijit.form.Button" onClick="closeThisDialog">
                                <fmt:message key="dialog.thesaurusNavigator.cancel" />
                            </button>
                        </span><span style="float:right; padding-right:10px;">
                            <button type="button" dojoType="dijit.form.Button" id="addSelectedTopicButton" onClick="addSelectedTopic">
                                <fmt:message key="dialog.thesaurusNavigator.add" />
                            </button>
                        </span><span id="thesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img id="thesImageZone" src="img/ladekreis.gif" /></span><span id="statusText" style="float:left; padding-top:6px;"></span></span>
                </div>
                </span>
                <div class="fill"></div>
                <!-- LEFT HAND SIDE CONTENT END --><!-- RIGHT HAND SIDE CONTENT START -->
                 <span class="outer" style="width:40%;">
                 <div id="listThesaurus" class="inputContainer">
                    <span class="label">
                        <label for="thesaurusDescList" onclick="javascript:dialog.showContextHelp(arguments[0], 7032, 'Liste der Deskriptoren')">
                            <fmt:message key="dialog.thesaurusNavigator.descriptorList" />
                        </label>
                    </span>
                    <div class="listInfo">
                        <div class="fill">
                        </div>
                    </div>
                    <!--<div dojoType="dijit.layout.ContentPane">-->
                    <div class="tableContainer">
                        <div id="thesaurusDescList" autoHeight="6" class="hideTableHeader">
                        </div>
                    </div>
                    <!--</div>-->
                    <div class="inputContainer">
                        <span class="buttonNoBg">
                            <button dojoType="dijit.form.Button" id="acceptTopicListButton" class="buttonGrey" onClick="acceptTopicList">
                                <fmt:message key="dialog.thesaurusNavigator.apply" />
                            </button>
                        </span>
                    </div>
                </div>
                </span><!-- RIGHT HAND SIDE CONTENT END -->
            </div>
        </div>
    </body>
</html>