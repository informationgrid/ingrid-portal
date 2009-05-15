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
var scriptScope = this;

var selectedTextNode = null;
var selectedButton = null;

_container_.addOnLoad(function() {
	init();
});

function disableUiElements() {
	dojo.widget.byId("acceptTopicListButton").disable();
	dojo.widget.byId("addSelectedTopicButton").disable();
	dojo.widget.byId("findTopicButton").disable();
}
function enableUiElements() {
	dojo.widget.byId("acceptTopicListButton").enable();
	dojo.widget.byId("addSelectedTopicButton").enable();
	dojo.widget.byId("findTopicButton").enable();
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("thesLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("thesLoadingZone"), "hidden");
}

function showStatus(msg) {
	var status = dojo.byId("statusText");
	if (status) {
		status.innerHTML = msg;
	}
}

// The treeController and root Tree are initialized here
function init() {
	// initially load data (first hierachy level) from server 
	SNSService.getRootTopics({
		preHook: function() { showLoadingZone(); disableUiElements(); },
	  	postHook: function() { hideLoadingZone(); enableUiElements(); },
		callback:function(res) { showStatus(""); handleRootTopics(res); },
		timeout:0,
		errorHandler:function(msg) { deferred.errback(new dojo.RpcError(msg, this)); showStatus(message.get("sns.connectionError"));},
		exceptionHandler:function(msg) { deferred.errback(new dojo.RpcError(msg, this)); showStatus(message.get("sns.connectionError"));}
	});

	var treeController = dojo.widget.byId("thesTreeController");
	treeController.loadRemote = function(node, sync) {

		var _this = this;
		var deferred = new dojo.Deferred();

		SNSService.getSubTopics(node.topicId, '2', 'down', {
			preHook: function() { showLoadingZone(); disableUiElements(); },
		  	postHook: function() { hideLoadingZone(); enableUiElements(); },
  			callback:function(res) { deferred.callback(res); showStatus("");},
			timeout:0,
			errorHandler:function(msg) { dojo.debug(msg); showStatus(message.get("sns.connectionError")); deferred.errback(new dojo.RpcError(msg, this));}
  		});

  		deferred.addCallback(function(res) {
  			UtilList.addSNSTopicLabels(res);
  			for (i in res) {
  				res[i]._title = res[i].title;
  				res[i].title = res[i].label;
  				res[i].isFolder = (res[i].children.length > 0);
				res[i].nodeDocType = res[i].type;
  				res[i].children = [];
  			}
  			var resp = _this.loadProcessResponse(node,res);
  			// Make NODE_LABELs unselectable
  			dojo.lang.forEach(node.children, function(widget){
				if (widget.nodeDocType == "NODE_LABEL" || widget.nodeDocType == "NON_DESCRIPTOR") {
					dojo.html.addClass(widget.labelNode, "TreeNodeNotSelectable");
  					widget.viewMouseOver = function() {};
  				}
  			});
  			return resp;
  		});

		deferred.addErrback(function(res) { alert(res.message); });
		return deferred;
	};

	// Register the node click handler 
	var treeListener = dojo.widget.byId('thesTreeListener');
	dojo.event.topic.subscribe(treeListener.eventNames.select, "handleSelectNode");


	// Enter key on the ValdiationTextbox has to start a search:
	var inputField = dojo.widget.byId("thesSearch");
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                findTopic();
            }
        });
}

resultTextClicked = function(topicId) {
	if (selectedTextNode) {
		dojo.html.removeClass(selectedTextNode, "selected");
	}

	selectedTextNode = dojo.byId("_resultText_"+topicId);
	dojo.html.addClass(selectedTextNode, "selected");
} 

resultButtonClicked = function(topicId) {
	if (selectedButton) {
		selectedButton.checked = false;
	}

	selectedButton = dojo.byId("_resultButton_"+topicId);

	expandToTopicWithId(topicId);
}


function displaySearchResults(resultList) {
	var resultContainer = dojo.byId("thesResultContainer");
	resultContainer.innerHTML = "";
	selectedTextNode = null;
	selectedButton = null;

	if (resultList.length == 0) {
		resultContainer.innerHTML = message.get("sns.noResultHint");
	}

	dojo.lang.forEach(resultList, function(term) {
		if (term.type != "NON_DESCRIPTOR") {
			var buttonLink = document.createElement("a"); 
			buttonLink.setAttribute("id", "_resultButton_"+term.topicId);
			buttonLink.onclick = function() {
				resultButtonClicked(term.topicId);
			}
			buttonLink.setAttribute("href", "javascript:void(0);");

			buttonLink.setAttribute("title", "In Baumstruktur finden");
			buttonLink.innerHTML = "<img src=\"img/ic_jump_tree.gif\" style=\"position: relative; top: 3px;\"/>";

			var divElement = document.createElement("div");

			var linkElement = document.createElement("a"); 
			linkElement.innerHTML = term.label;

			if (term.type == "DESCRIPTOR") {
				dojo.html.addClass(linkElement, "resultText");
				linkElement.setAttribute("id", "_resultText_"+term.topicId);
				linkElement.onclick = function() {
					resultTextClicked(term.topicId);
				}
				linkElement.setAttribute("href", "javascript:void(0);");

				linkElement.setAttribute("title", "Begriff auswaehlen");
				linkElement.topicId = term.topicId;
				linkElement.term = term;
			} else {
				dojo.html.addClass(linkElement, "notSelectable");
			}
			
			resultContainer.appendChild(divElement);
			divElement.appendChild(buttonLink);
			divElement.appendChild(linkElement);
		}
	});
}


// Callback Handler for the root topics.
// This function gets called when the tree is initialized.
// @param topicList - List of Topics from the SNSService   
//
// The function adds the topics in 'topicList' as children to the tree 
function handleRootTopics(topicList) {
	for (var i in topicList) {
		topicList[i].isFolder = true;
		topicList[i].nodeDocType = topicList[i].type;
		// Top Terms are not selectable. Add the proper class to make them grey
		topicList[i].labelClass = "TreeNodeNotSelectable";
	}

	var tree = dojo.widget.byId('thesTree');
	// Check if the tree exists. If the user closed the dialog it won't.
	if (tree) {
		tree.setChildren(topicList);

		// Iterate over all added child widgets and remove the mouseOver function.
		// This is done so the node labels won't be underlined on mouseOver.
		for (var i in tree.children) {
			tree.children[i].viewMouseOver = function() {};
		}
	}
}

// Expands the tree to the topic with id 'topicID'
// A valid topicID can be acquired by calling SNSService.findTopic()
function expandToTopicWithId(topicID) {
	var treePane = dojo.widget.byId("thesTreePane");
	dojo.widget.byId("thesResultTabContainer").selectChild(treePane);

	SNSService.getSubTopicsWithRoot(topicID, '0', 'up', {
		preHook: function() { showLoadingZone(); disableUiElements(); },
	  	postHook: function() { hideLoadingZone(); enableUiElements(); },
		callback:function(res) {
			showStatus("");
			var tree = dojo.widget.byId('thesTree');
			var topTerm = getTopTermNode(res[0]);
			expandPath(tree, topTerm, res[0]); },
		timeout:0,
		errorHandler:function(msg) {showStatus(message.get("sns.connectionError"));},
		exceptionHandler:function(msg) { showStatus(message.get("sns.connectionError"));}
	});	
}


// Expands the tree
// This is an internal function and should not be called. Call expandToTopicWithId(topicID) instead
// @param tree - Tree/TreeNode. Current position in the tree   
// @param currentNode - SNSTopic. The current node we are looking for to expand
// @param targetNode - SNSTopic. Last node in the tree. return when we found this node. 
//
// The function iterates over tree.children and locates a TreeNode with topicID == currentNode.topicID
// This node is then expanded and passed to expandPath recursively in a callback function.
function expandPath(tree, currentNode, targetNode) {
	
	// Break Condition
	if (currentNode.topicId == targetNode.topicId) {
		// Mark the target node as selected
		for(i in tree.children) {
			if (tree.children[i].topicId == currentNode.topicId) {
				dojo.widget.byId('thesTree').selectNode(tree.children[i]);
				// Scroll the contentPane to tree.children[i]
				if (!dojo.render.html.ie)				
					dojo.html.scrollIntoView(tree.children[i].domNode);

				return;
			}
		}
		return;
	}

	// Iterate over tree.children and locate the node next node in the path
	for(i in tree.children) {
		var curTreeNode = tree.children[i];
		if (curTreeNode.topicId == currentNode.topicId)
		{
			// A node with the correct topicId was found. If the node is not expanded and
			// it's children have not been loaded yet, load the children via callback and
			// do the recursion afterwards.
			if (!curTreeNode.isExpanded && curTreeNode.children.length == 0) {
				var treeController = dojo.widget.byId('thesTreeController');
//				dojo.debug('Passed the following node to callback: '+curTreeNode.topicId+' '+curTreeNode.widgetId);
				var widgetId = curTreeNode.widgetId;
				treeController.expand(curTreeNode).addCallback(function(res) {
//					dojo.debug('Callback got the following node: '+dojo.widget.byId(widgetId).topicId+' '+widgetId);
					expandPath(dojo.widget.byId(widgetId), currentNode.parents[0], targetNode);
				});
			}
			// If the children have been loaded, expand the node and continue with the recursion
			else {
				curTreeNode.expand();
				expandPath(curTreeNode, currentNode.parents[0], targetNode);
			}
		}
	}
}


// Walks through a 'SNSTopic' structure (from SNSService.getSubTopics()) and returns a TOP_TERM
function getTopTermNode(node) {
	if (node.type == 'TOP_TERM') {
		return node;
	}
	else {
		if (node.children != null && node.children.length != 0) {
			return getTopTermNode(node.children[0]);
		}
		else {
			dojo.debug('Error in getTopTermNode: No parent of type TOP_TERM found.');
			return node;
		}
	}
}


// Search Button onClick function.
//
// This function reads the value of the TextBox 'thesSearch' and expands the tree from the root
// to the corresponding node
findTopic = function() {
	var queryTerm = dojo.widget.byId('thesSearch').getValue();
	queryTerm = dojo.string.trim(queryTerm);

	if (queryTerm == "") {
		showStatus("");
		return;
	}

//	clearResultContainer();
	var resultPane = dojo.widget.byId("thesResultPane");
	dojo.widget.byId("thesResultTabContainer").selectChild(resultPane);

	SNSService.findTopics(queryTerm, {
		preHook: function() { showLoadingZone(); disableUiElements(); },
	  	postHook: function() { hideLoadingZone(); enableUiElements(); },
		callback:function(result) {
			if (result) {
				UtilList.addSNSTopicLabels(result);
				displaySearchResults(result);
			} else {
//				showStatus(message.get("sns.noResultHint"));
			}},
		timeout:0,
		errorHandler:function(msg) {showStatus(message.get("sns.connectionError"));},
		exceptionHandler:function(msg) { showStatus(message.get("sns.connectionError"));}
	});

};

// AddTopic Button onClick function.
//
// This function adds the currently selected node to the descriptorList if it isn't already in the store.
// The function also checks if a descriptor is selected (TODO: Shouldn't be done here?)
addSelectedTopic = function() {
	var selectedTab = dojo.widget.byId("thesResultTabContainer").selectedChild;
    var descStore = dojo.widget.byId('thesaurusDescList').store;

	if (selectedTab == "thesTreePane") {
		var selectedNode = dojo.widget.byId('thesTree').selectedNode;
		if (dojo.lang.some(descStore.getData(), function(item){ return (selectedNode.topicId == item.topicId); }))
			return;

		if (!selectedNode || selectedNode.type != "DESCRIPTOR") {
			return;
		} else {
			selectedNode.Id = UtilStore.getNewKey(descStore);
			descStore.addData( selectedNode );
		}

	} else if (selectedTab == "thesResultPane" && selectedTextNode) {
		if (dojo.lang.some(descStore.getData(), function(item){ return (selectedTextNode.topicId == item.topicId); })) {
			return;
		} else {
			selectedTextNode.term.Id = UtilStore.getNewKey(descStore);
			descStore.addData( selectedTextNode.term );
		}
	}
};

// Cancel Button onClick function.
//
// This function closes the dialog without saving any changes.
closeDialog = function() {
	_container_.closeWindow();
}

// Finish Button onClick function.
//
// This function copies the descriptor list to the main mdek searchtopic list
acceptTopicList = function() {
	var srcStore = dojo.widget.byId("thesaurusDescList").store;
	var destStore = dojo.widget.byId("thesaurusTerms").store;

	dojo.lang.forEach(srcStore.getData(), function(topic){
		if (dojo.lang.every(destStore.getData(), function(item){ return item.topicId != topic.topicId; })) {
			// Topic is new. Add it to the topic list
			topic.Id = UtilStore.getNewKey(destStore);
			if (topic._title) {
				dojo.debug("Use _title!!!");
				topic.title = topic._title;
			}
			//dojo.debugShallow(topic);
			destStore.addData( topic );
		} else {
			// Topic already exists in the destination List
			return;
		}
	});

	closeDialog();
}

</script>

</head>




<body>

<div dojoType="ContentPane">

  <div id="thesaurus" class="contentBlockWhite top wideBlock">
    <div id="winNavi">
		<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=maintanance-of-objects-6#maintanance-of-objects-6', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="Hilfe">[?]</a>
	  </div>
	  <div id="thesaurusContent" class="content">

      <!-- LEFT HAND SIDE CONTENT START -->
      <div class="spacer"></div>
      <div class="inputContainer field grey noSpaceBelow fullField">

        <span class="label"><label for="thesSearch" onclick="javascript:dialog.showContextHelp(arguments[0], 7031, 'Suche nach Deskriptoren und Ordnungsbegriffen')"><fmt:message key="dialog.thesaurusNavigator.title" /></label></span>
        <span class="input"><input type="text" id="thesSearch" name="thesSearch" class="w640" dojoType="ingrid:ValidationTextBox" /></span>
        <div class="spacerField"></div>
  	  </div>

      <div class="inputContainer full">
        <span class="button w644" style="height:20px !important;">
          <span style="float:right;">
            <button dojoType="ingrid:Button" id="findTopicButton" class="buttonBlue" title="In Thesaurus suchen" onClick="findTopic"><fmt:message key="dialog.thesaurusNavigator.search" /></button></span>
   	      </span>
   	  </div>

      <div class="inputContainer full noSpaceBelow">
		<div id="thesResultTabContainer" dojoType="ingrid:TabContainer" class="h199 tabContainerWithBorderTop" selectedChild="thesTreePane">

			<div class="grey" dojoType="ContentPane" id="thesTreePane" label="<fmt:message key="dialog.thesaurusNavigator.tree" />" style="height: 200px; padding-left:5px; padding-top:2px; width: 100%; overflow: auto;">
	          <!-- tree components -->
				<div dojoType="ingrid:TreeController" widgetId="thesTreeController"></div>
				<div dojoType="ingrid:TreeListener" widgetId="thesTreeListener"></div>	
				<div dojoType="ingrid:TreeDecorator" listener="thesTreeListener"></div>
				<div dojoType="ingrid:TreeDocIcons" widgetId="thesTreeDocIcons"></div>	
	          <!-- tree -->
	         <div dojoType="ingrid:Tree" listeners="thesTreeController;thesTreeListener;thesTreeDocIcons" widgetId="thesTree">
	           </div>
	      	</div>

			<div class="grey" dojoType="ContentPane" id="thesResultPane" label="<fmt:message key="dialog.thesaurusNavigator.list" />" style="height: 200px; padding-left:5px; width: 100%; overflow: auto;">
				<span id="thesResultContainer"></span>
	      	</div>

		</div>

      </div>

      <div class="inputContainer full">
         <span class="button w644" style="height:20px !important;">
         	<span style="float:right;"><button dojoType="ingrid:Button" onClick="closeDialog"><fmt:message key="dialog.thesaurusNavigator.cancel" /></button></span>
         	<span style="float:right; padding-right:10px;"><button dojoType="ingrid:Button" id="addSelectedTopicButton" onClick="addSelectedTopic"><fmt:message key="dialog.thesaurusNavigator.add" /></button></span>
			
			<span id="thesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
				<img id="thesImageZone" src="img/ladekreis.gif" />
			</span>
			
			<span id="statusText" style="float:left; padding-top:6px;"></span>
         </span>
  	  </div>
      <!-- LEFT HAND SIDE CONTENT END -->

      <!-- RIGHT HAND SIDE CONTENT START -->
      <div id="listThesaurus" class="inputContainer">
        <span class="label"><label for="thesaurusDescList" onclick="javascript:dialog.showContextHelp(arguments[0], 7032, 'Liste der Deskriptoren')"><fmt:message key="dialog.thesaurusNavigator.descriptorList" /></label></span>
        <div class="listInfo third">
      	  <div class="fill"></div>
        </div>

        <div dojoType="ContentPane">
			<div class="tableContainer headHiddenRows6 third">
    	    <table id="thesaurusDescList" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive relativePos">
    	      <thead>
    		      <tr>
          			<th nosort="true" field="label" dataType="String" width="255" sort="desc">&nbsp;</th>
    		      </tr>
    	      </thead>
    	    <tbody>
    	    </tbody>
    	    </table>
        	</div>
        </div>

        <div class="inputContainer w264 noSpaceBelow">
          <span class="buttonNoBg w264"><button dojoType="ingrid:Button" id="acceptTopicListButton" class="buttonGrey" onClick="acceptTopicList"><fmt:message key="dialog.thesaurusNavigator.apply" /></button></span>
    	  </div>
    	</div>
      <!-- RIGHT HAND SIDE CONTENT END -->

    </div>
  </div>
</div>

</body>
</html>
