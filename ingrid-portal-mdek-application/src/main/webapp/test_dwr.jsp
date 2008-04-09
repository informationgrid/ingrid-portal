<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script
	src='/ingrid-portal-mdek-application/dwr/interface/TreeService.js'></script>
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
  initTree();
});

function initTree() {
  var contextMenu1 = dojo.widget.byId('contextMenu1');
  contextMenu1.treeController = dojo.widget.byId('treeController');
  contextMenu1.addItem('new', 'addChild', function(menuItem) {createItemClicked(menuItem)});
  contextMenu1.addItem('preview', 'open', 'previewItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem('cut', 'cut', 'cutItemClicked');
  contextMenu1.addItem('copy', 'copy', 'copySingleItemClicked');
  contextMenu1.addItem('paste', 'copy', 'copyItemClicked');
  contextMenu1.addItem('paste as node', 'paste', 'pasteItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem('mark deleted', 'detach', 'markDeletedItemClicked');
//  contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', 'deleteItemClicked');

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem('new', 'addChild', function(menuItem) {createItemClicked(menuItem)});

  // attach node selection handler
  var treeListener = dojo.widget.byId('treeListener');
  dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelected");
  
  // Load children of the node from server
  // Overwritten to work with dwr.
  var treeController = dojo.widget.byId('treeController');
  treeController.loadRemote = function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();
		
		TreeService.getSubTree(node.id, node.nodeAppType, 1, {
  			callback:function(res) { deferred.callback(res); },
			timeout:5000,
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { alert(res.message); });
		return deferred;
	};
}

// initially load data (first hierachy level) from server 
TreeService.getSubTree(null, null, 1, 
	function (str) {
		var tree = dojo.widget.byId('tree');
		tree.setChildren(str);
	});

</script>

<body>

<!-- tree components -->
<div dojoType="ingrid:TreeController" widgetId="treeController"
	RpcUrl="server/treelistener.php"></div>
<div dojoType="ingrid:TreeListener" widgetId="treeListener"></div>
<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIcons"></div>
<div dojoType="ingrid:TreeDecorator" listener="treeListener"></div>

<!-- context menus -->
<div dojoType="ingrid:TreeContextMenu" toggle="plain"
	contextMenuForWindow="false" widgetId="contextMenu1"></div>
<div dojoType="ingrid:TreeContextMenu" toggle="plain"
	contextMenuForWindow="false" widgetId="contextMenu2"></div>

<!-- tree -->
<div dojoType="ingrid:Tree"
	listeners="treeController;treeListener;contextMenu1;contextMenu2;treeDocIcons"
	widgetId="tree"></div>

</body>
</html>
