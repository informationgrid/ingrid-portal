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
    dojo.require("dijit.form.ValidationTextBox");
    dojo.require("dijit.layout.TabContainer");
    dojo.require("ingrid.dijit.ThesaurusTree");
    dojo.require("ingrid.dijit.CustomTree");
    dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
    
    
var scriptScopeResearchTh = _container_;

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
var objPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("searchThesaurusObjResultsInfo"), pagingSpan:dojo.byId("searchThesaurusObjResultsPaging") });
var adrPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("searchThesaurusAdrResultsInfo"), pagingSpan:dojo.byId("searchThesaurusAdrResultsPaging") });


dojo.connect(_container_, "onLoad", function() {
    createDOMElements();
	
    //initTree();

    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dojo.byId("thesaurusSearch"), "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                findTopic();
            }
	});

	dojo.connect(dijit.byId("searchThesaurusNavResultContainer"), "onSelectChild", startSearch);
	dojo.connect(objPageNav, "onPageSelected", function() { objResultsLoaded = false; startSearch(); });
	dojo.connect(adrPageNav, "onPageSelected", function() { adrResultsLoaded = false; startSearch(); });
    
    dijit.byId("contentPane").resize();
});

dojo.connect(_container_, "onUnload", function() {
	//dojo.unsubscribe(dijit("treeListenerSearchThesaurus").eventNames.select, scriptScopeResearchTh, "handleSelectNode");
});

function createDOMElements() {
    var searchThesaurusNavAddressesListStructure = [
       {field: 'icon',name: '&nbsp;',width: '23px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.research.thes.name' />",width: '320px'}
    ];
    createDataGrid("searchThesaurusNavObjectsList", null, searchThesaurusNavAddressesListStructure, null);
    
    createDataGrid("searchThesaurusNavAddressesList", null, searchThesaurusNavAddressesListStructure, null);
    
    treeSearchThesaurusWidget = new ingrid.dijit.ThesaurusTree({domId:"treeSearchThesaurus", loadingDivId:"thesaurusSearchLoadingZone" });
    dojo.connect(dijit.byId("treeSearchThesaurus"), "onClick", scriptScopeResearchTh.handleSelectNode);
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
	if (currentQuery == null)
		return;

    // always load both infos!
	//var selectedTab = dijit.byId("searchThesaurusNavResultContainer").selectedChildWidget.id;

	//if (selectedTab == "searchThesaurusNavObjects" && !objResultsLoaded) {
//		console.debug("Starting object query for "+currentQuery);
//		console.debug("startHit: "+objPageNav.getStartHit());
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
//		console.debug("Starting address query for "+currentQuery);
//		console.debug("startHit: "+adrPageNav.getStartHit());
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
	UtilList.addAddressTitles(res.resultList);
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
scriptScopeResearchTh.handleSelectNode = function(treeNode, node) {
//	console.debug("node selected: "+treeNode.node.topicId);
	if (selectedTextNode) {
		dojo.removeClass(selectedTextNode, "selected");
	}
	startNewQuery(treeNode.topicId[0]);
}


// Displays the given topics in resultList in the second result tab
function displayAssociatedTopics(resultList) {
    var _this = dijit.byId("pageResearchThesaurus");
	var resultContainer = dojo.byId("thesaurusResultContainer");
	resultContainer.innerHTML = "";

	if (resultList.length == 0) {
		resultContainer.innerHTML = "<fmt:message key='sns.noResultHint' />";
	}

	dojo.forEach(resultList, function(term) {
		if (term.type != "NON_DESCRIPTOR") {
			var buttonLink = document.createElement("a"); 

			buttonLink.onclick = function() {
				_this.topicButtonClicked(term.topicId);
			}
			buttonLink.setAttribute("href", "javascript:void(0);");

			buttonLink.setAttribute("title", "In Baumstruktur finden");
			buttonLink.innerHTML = "<img src=\"img/ic_jump_tree.gif\" style=\"position: relative; top: 3px;\"/>";

			var divElement = document.createElement("div");

			var linkElement = document.createElement("a"); 
			linkElement.innerHTML = term.label;

			if (term.type == "DESCRIPTOR") {
				dojo.addClass(linkElement, "resultText");
				linkElement.setAttribute("id", "_researchThesLabel_"+term.topicId);
				linkElement.onclick = function() {
					_this.topicLabelClicked(term.topicId);
				}
				linkElement.setAttribute("href", "javascript:void(0);");

				linkElement.setAttribute("title", "Begriff auswaehlen");
				linkElement.topicId = term.topicId;
			} else {
				dojo.addClass(linkElement, "notSelectable");
			}
			
			resultContainer.appendChild(divElement);
			divElement.appendChild(buttonLink);
			divElement.appendChild(linkElement);
		}
	});
}

// Button onclick function for the associated topics. Switch the tab pane and
// expand the tree to the given topicId
scriptScopeResearchTh.topicButtonClicked = function(topicId) {
    dijit.byId("researchThesaurusTabContainer").selectChild("thesaurusTreeContainer");
	var def = treeSearchThesaurusWidget.expandToTopicWithId(topicId);
    def.addCallback(function(){
        startNewQuery(topicId);
    });
}

// Start a search for objects/addresses with 'topicId'
scriptScopeResearchTh.topicLabelClicked = function(topicId) {
//	console.debug("clicked label: "+topicId);
	if (selectedTextNode) {
		dojo.removeClass(selectedTextNode, "selected");
	}

	selectedTextNode = dojo.byId("_researchThesLabel_"+topicId);
	dojo.addClass(selectedTextNode, "selected");

	startNewQuery(topicId);
}

// Search Button onClick function.
//
// This function reads the value of the TextBox 'thesSearch' and expands the tree from the root
// to the corresponding node
function findTopic() {
	var queryTerm = dojo.trim(dijit.byId("thesaurusSearch").getValue());

	if (queryTerm.length == 0) {
		return;
	}

	dijit.byId("researchThesaurusTabContainer").selectChild("thesaurusResultPane");

	SNSService.findTopics(queryTerm, {
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

};

function showLoadingZone() {
    dojo.style("thesaurusSearchLoadingZone", "visibility", "visible");
}

function hideLoadingZone() {
    dojo.style("thesaurusSearchLoadingZone", "visibility", "hidden");
}


</script>
</head>

<body>
  <!-- CONTENT START -->
    <div id="researchThesaurusContentSection" dojoType="dijit.layout.BorderContainer" design="sidebar" class="content" style="height:100%;" layoutAlign="client" >
        <!-- LEFT HAND SIDE CONTENT START -->
        <div dojoType="dijit.layout.ContentPane" region="top">
            <div id="winNavi" style="top:0;">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=search-2#search-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <span class="outer"><div>
            <span class="label">
                <label for="thesaurusSearch" onclick="javascript:dialog.showContextHelp(arguments[0], 7062)">
                    <fmt:message key="dialog.research.thes.title" />
                </label>
            </span>
            <div class="inputContainer field grey">
                <span class="input">
                    <input type="text" id="thesaurusSearch" style="width:100%;" dojoType="dijit.form.ValidationTextBox" />
                </span>
            </div>
            </div></span>
            <div class="inputContainer">
                <span class="button" style="height:20px !important;">
                    <span style="float:right;">
                        <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.research.thes.search" />" onClick="javascript:scriptScopeResearchTh.findTopic();">
                            <fmt:message key="dialog.research.thes.search" />
                        </button>
                    </span>
                    <span id="thesaurusSearchLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                        <img src="img/ladekreis.gif" />
                    </span>
                </span>
            </div>
        </div>

            <div dojoType="dijit.layout.ContentPane" region="center" class="innerPadding">
    		<div id="researchThesaurusTabContainer" dojoType="dijit.layout.TabContainer" class="tabContainerWithBorderTop" selectedChild="thesaurusTreeContainer">
    		  <!-- first tab, tree view -->
    		  <div class="grey" dojoType="dijit.layout.ContentPane" id="thesaurusTreeContainer" title="<fmt:message key="dialog.research.thes.tree" />">
                    <div id="treeSearchThesaurus"></div>
            	</div>
    
    		    <!-- second tab, associated topics -->
    	        <div class="grey" dojoType="dijit.layout.ContentPane" id="thesaurusResultPane" title="<fmt:message key="dialog.research.thes.list" />">
    			  <span id="thesaurusResultContainer"></span>
    	        </div>
            </div>
            </div>
            
        <!-- LEFT HAND SIDE CONTENT END -->
        
        <!-- RIGHT HAND SIDE CONTENT START -->
        <div id="resultsThesaurus" dojoType="dijit.layout.ContentPane" region="right" class="inputContainer innerPadding" style="width:400px;">
          <span class="label"><label class="inActive" for="thesaurusDescList"><fmt:message key="dialog.research.thes.result" /></label></span>
        
         	<div id="searchThesaurusNavResultContainer" dojoType="dijit.layout.TabContainer" doLayout="false" selectedChild="searchThesaurusNavObjects">

            <!-- TAB 1 START -->
        	<div id="searchThesaurusNavObjects" dojoType="dijit.layout.ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.research.thes.objects" />">
              <div class="listInfo">
                <span id="searchThesaurusObjResultsInfo" class="searchResultsInfo">&nbsp;</span>
                <span id="searchThesaurusObjResultsPaging" class="searchResultsPaging">&nbsp;</span>
            	<div class="fill"></div>
              </div>

				<div class="tableContainer">
				    <div id="searchThesaurusNavObjectsList" autoHeight="22" contextMenu="none"></div>
                </div>
          	</div>
            <!-- TAB 1 END -->

            <!-- TAB 2 START -->
       		<div id="searchThesaurusNavAddresses" dojoType="dijit.layout.ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.research.thes.addresses" />">
              <div class="listInfo">
                <span id="searchThesaurusAdrResultsInfo" class="searchResultsInfo">&nbsp;</span>
                <span id="searchThesaurusAdrResultsPaging" class="searchResultsPaging">&nbsp;</span>
            	  <div class="fill"></div>
              </div>

				<div class="tableContainer">
				    <div id="searchThesaurusNavAddressesList" autoHeight="22" contextMenu="none"></div>
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
