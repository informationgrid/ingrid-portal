<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Objekt zuordnen</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	init();
});
_container_.addOnUnload(function() {
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (_container_.customParams.resultHandler.fired == -1) {
		_container_.customParams.resultHandler.errback();
	}
});

function init() {
	// Deferred object which is called when the Tree has been initialized.
	// After the tree has been initialized, the tree is expanded to the targetNode
	// specified in the customParameter 'jumpToNode' 
	var treeInitDef = new dojo.Deferred();

	// Load initial first level of the tree from the server
	TreeService.getSubTree(null, null, 1, 
		function (rootNodeList) {
			var tree = dojo.widget.byId('selectDatasetTree');

			dojo.lang.forEach(rootNodeList, function(rootNode){
				rootNode.title = dojo.string.escape("html", rootNode.title);
				rootNode.uuid = rootNode.id;
				rootNode.id = null;
			});

			tree.setChildren(rootNodeList);
	});

	// Load children of the node from server
	// Overwritten to work with dwr.
	var treeController = dojo.widget.byId("selectDatasetTreeController");
	treeController.loadRemote = function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		deferred.addCallback(function(res) {
			// Remove all objects from the list which are not published 
			res = dojo.lang.filter(res, function(obj){
				return obj.isPublished;
			});
			dojo.lang.forEach(res, function(obj){
				obj.title = dojo.string.escape("html", obj.title);
				obj.uuid = obj.id;
				obj.id = null;
			});
			if (res.length != 0) {
				return _this.loadProcessResponse(node,res);
			} else {
				node.unsetFolder();
				return;
			}
		});
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});

		TreeService.getSubTree(node.uuid, node.nodeAppType, 1, {
  			callback:function(res) { deferred.callback(res); },
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});

		return deferred;
	};
};

scriptScope.selectDataset = function() {
	var node = dojo.widget.byId("selectDatasetTree").selectedNode;
	if (node) {
		var retVal = {};
		retVal.uuid = node.uuid;
		retVal.title = node.title;
		retVal.nodeAppType = node.nodeAppType;

		_container_.customParams.resultHandler.callback(retVal);
	}

	_container_.closeWindow(); 
}

</script>
</head>

<body>

<div dojoType="ContentPane">

  <div id="catalogueObject" class="contentBlockWhite top w478">
    <div id="winNavi">
      <a href="#" title="Hilfe">[?]</a>
	  </div>
	  <div id="objectContent" class="content">

      <!-- CONTENT START -->
      <div class="inputContainer w478 h413 scrollable">
      	<div dojoType="ContentPane" id="treeContainerAssignObj">
          <!-- tree components -->
          <div dojoType="ingrid:TreeController" widgetId="selectDatasetTreeController" RpcUrl="server/treelistener.php"></div>
          <div dojoType="ingrid:TreeListener" widgetId="selectDatasetTreeListener"></div>	
          <div dojoType="ingrid:TreeDocIcons" widgetId="selectDatasetTreeDocIcons"></div>	
          <div dojoType="ingrid:TreeDecorator" listener="selectDatasetTreeListener"></div>
          
          <!-- tree -->
          <div dojoType="ingrid:Tree" listeners="selectDatasetTreeController;selectDatasetTreeListener;selectDatasetTreeDocIcons" widgetId="selectDatasetTree">
		  </div>
        </div>
        <div class="spacer"></div>
      </div>
		<div class="inputContainer w478">
		<span><fmt:message key="dialog.admin.export.selectNode.note" /></span>
		</div>
      <div class="inputContainer w478">
        <span class="button w442 transparent">
		  <span style="float:right;"><button dojoType="ingrid:Button" onClick="javascript:scriptScope.selectDataset()"><fmt:message key="dialog.admin.export.selectNode.select" /></button></span>
        </span>
  	  </div>
      <!-- CONTENT END -->

    </div>
  </div>
</div>

</body>
</html>
