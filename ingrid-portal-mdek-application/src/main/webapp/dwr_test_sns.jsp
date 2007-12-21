<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script
	src='/ingrid-portal-mdek-application/dwr/interface/SNSService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>


<script>
dojo.require("dojo.widget.TreeSelectorV3");
dojo.require("ingrid.widget.Tree");
dojo.require("ingrid.widget.TreeNode");
dojo.require("ingrid.widget.TreeContextMenu");
dojo.require("ingrid.widget.TreeController");
dojo.require("ingrid.widget.TreeDocIcons");
dojo.require("ingrid.widget.TreeListener");
dojo.require("ingrid.widget.TreeDecorator");	
dojo.require("ingrid.widget.TreeExpandOnSelect");	

dojo.addOnLoad(function()
{
  init();
});


// The treeController and root Tree are initialized here
function init() {
	var treeController = dojo.widget.byId('snsTreeController');

	treeController.loadRemote = function(node, sync) {

		var _this = this;
		var deferred = new dojo.Deferred();

		SNSService.getSubTopics(node.topicId, '2', 'down', {
  			callback:function(res) { deferred.callback(res); },
			timeout:0,
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});

  		deferred.addCallback(function(res) {
  			for (i in res) {
  				res[i].isFolder = (res[i].children.length > 0);
  				res[i].children = [];
  			}
  			return _this.loadProcessResponse(node,res);
  		});
		deferred.addErrback(function(res) { alert(res.message); });
		return deferred;
	};

	// initially load data (first hierachy level) from server 
	SNSService.getRootTopics(handleRootTopics);

	var treeListener = dojo.widget.byId('snsTreeListener');
	if (treeListener)
		dojo.event.topic.subscribe(treeListener.eventNames.select, "handleSelectNode");
	else
		alert("Error: Widget snsTreeListener could not be found.");
}

// Handler for SelectNode events (node title clicked)
// Currently displays the information about the clicked node and path to/from a TopTerm
function handleSelectNode(msg) {
	dojo.debug(msg.node.topicId+' '+msg.node.title+' '+msg.node.type);	
	SNSService.getSubTopics(msg.node.topicId, '0', 'up', showPath);
}

// Displays a path from the first node in the nodeList to a topterm
// This method is designed to work with the output from the DWR call 'SNSService.getSubTopics()'
// and needs a valid 'SNSTopic' tree/graph (topic.children != null && last children.type == TOP_TERM) 
function showPath(nodeList)
{
	if (nodeList == null || nodeList.length == 0) {
		return;
	}

	var node = nodeList[0];
	
	dojo.debug(node.topicId+' '+node.title+' '+node.type);

	if (node.type == 'TOP_TERM' || node.children.length == 0) {
		return;
	}

	showPath(node.children);
}


// Expands the tree to the topic with id 'topicID'
// A valid topicID can be acquired by calling SNSService.findTopic()
function expandToTopicWithId(topicID) {
	SNSService.getSubTopicsWithRoot(topicID, '0', 'up', function(res) {
		var tree = dojo.widget.byId('snsTree');
		var topTerm = getTopTermNode(res[0]);
		dojo.debug('Top Term: '+topTerm.topicId);
		dojo.debug('Target Node: '+res[0].topicId);
		expandPath(tree, topTerm, res[0]);	
	});	
}


// Goes through a 'SNSTopic' structure (from SNSService.getSubTopics()) and returns a TOP_TERM
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

// Expands the tree
// This is an recursive internal function and should not be called. Call expandToTopicWithId(topicID) instead
// @param tree - Tree/TreeNode. Current position in the tree   
// @param currentNode - SNSTopic. The current node we are looking for to expand
// @param targetNode - SNSTopic. Last node in the tree. return when we found this node. 
//
// The function iterates over tree.children and locates a TreeNode with topicID == currentNode.topicID
// The found node is expanded and passed to expandPath recursively in a callback function.
function expandPath(tree, currentNode, targetNode) {
	if (currentNode.topicId == targetNode.topicId) {
		for(i in tree.children) {
			if (tree.children[i].topicId == currentNode.topicId) {
				dojo.widget.byId('snsTree').selectNode(tree.children[i]);
				return;
			}
		}
		return;
	}

	for(i in tree.children) {
		var curTreeNode = tree.children[i];
		if (curTreeNode.topicId == currentNode.topicId)
		{
			if (!curTreeNode.isExpanded && curTreeNode.children.length == 0) {
				var treeController = dojo.widget.byId('snsTreeController');

//				dojo.debug('Passed the following node to callback: '+curTreeNode.topicId+' '+curTreeNode.widgetId);
				var widgetId = curTreeNode.widgetId;
				treeController.expand(curTreeNode).addCallback(function(res) {
//					dojo.debug('Callback got the following node: '+dojo.widget.byId(widgetId).topicId+' '+widgetId);
					expandPath(dojo.widget.byId(widgetId), currentNode.parents[0], targetNode);
				});
			}
			else {
				curTreeNode.expand();
				expandPath(curTreeNode, currentNode.parents[0], targetNode);
			}
		}
	}
}

function handleRootTopics(topicList) {
	for (i in topicList) {
//		topicList[i].isFolder = (topicList[i].children.length > 0);
		topicList[i].isFolder = true;
	}

	var tree = dojo.widget.byId('snsTree');
	tree.setChildren(topicList);
}

function findTopics() {

	var textBox = dojo.widget.byId('textBox');

	SNSService.findTopic(textBox.textbox.value, function(result) {
//		dojo.debug('Expanding tree to: '+result.topicId);
		expandToTopicWithId(result.topicId);
	});
}


function expandAll(node) {
	var tree = dojo.widget.byId('snsTree');
	var treeController = dojo.widget.byId('snsTreeController');

	treeController.expandToLevel(tree, 10);
}



</script>

<body>

<!-- tree components -->
<!-- RpcUrl="server/treelistener.php">  -->
<div dojoType="ingrid:TreeController" widgetId="snsTreeController"></div>
<div dojoType="ingrid:TreeListener" widgetId="snsTreeListener"></div>

<!-- 
<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIcons"></div>
<div dojoType="ingrid:TreeDecorator" listener="snsTreeListener"></div>
-->

<!-- context menus -->
<!-- 
<div dojoType="ingrid:TreeContextMenu" toggle="plain"
	contextMenuForWindow="false" widgetId="contextMenu1"></div>
<div dojoType="ingrid:TreeContextMenu" toggle="plain"
	contextMenuForWindow="false" widgetId="contextMenu2"></div>
-->

<div dojoType="Textbox" disabled="true" value="Warmwasserspeicherung"
	id="textBox" type="text"></div>
<button dojoType="Button" onClick="findTopics">Find Topic</button>
<button dojoType="Button" onClick="expandAll">Expand All</button>


<!-- tree -->
<!-- listeners="treeController;treeListener;contextMenu1;contextMenu2;treeDocIcons"  -->
<div dojoType="ingrid:Tree"
	listeners="snsTreeController;snsTreeListener" widgetId="snsTree"></div>
</body>
</html>
