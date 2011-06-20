<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.object.assign.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">

createTree = function() {
	createCustomTree("treeAssignObj", null, "id", "title", loadObjectData);
	
	// Initialize the address tree	
	// Load initial first level of the tree from the server
	TreeService.getSubTree(null, null, function(str){
	    for (var i = 0; i < str.length; i++) {
	        str[i].id = "AssignObj_" + str[i].id;
	    }
	    
	    var tree = dijit.byId("treeAssignObj");
	    
	    // Only use 'objects' and drop 'addresses'
	    tree.model.store.newItem(str[0]);
	});
}

function loadObjectData(node, callback_function){
	console.debug("loadObjectData");
	var parentItem = node.item;
	var store = this.tree.model.store;
	
	if (parentItem.root) {
		callback_function();
		return;
	}
	var deferred = new dojo.Deferred();
	TreeService.getSubTree(parentItem.id[0].substring(10, parentItem.id[0].length)+"", parentItem.nodeAppType[0]+"", {
		callback:function(res) {
			for (var i = 0; i < res.length; i++) {
				res[i].id = "AssignObj_"+res[i].id;
				if (parentItem.root) 
                    store.newItem(res[i]);
                else 
                    store.newItem(res[i], {
                        parent: parentItem,
                        attribute: "children"
                    });
			}
			callback_function();
		},
		//			timeout:20000,
		errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
		exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
	});

	deferred.addErrback(function(res) { alert("Error while loading data from the server. Please check your connection and try again!"); return res;});
	return deferred;
}

init = function() {
	createTree();
	
	// expand tree to selected node
	var deferred = new dojo.Deferred();
	deferred.addCallback(function(pathList) {
        console.debug("pathList");
        console.debug(pathList);
        pathList = dojo.map(pathList, function(item){return 'AssignObj_' + item;});
        dijit.byId("treeAssignObj")._setPathAttr(["root_treeAssignObj", "AssignObj_objectRoot"].concat(pathList));
		/*var def = _expandPathAssignObj(pathList);
		def.addCallback(function(){
			var tree = dijit.byId("treeAssignObj");
			var treeListener = dijit.byId("treeListenerAssignObj");
			var targetNode = dijit.byId(pathList[pathList.length-1]);
			tree.selectNode(targetNode);
			tree.selectedNode = targetNode;
			dojo.publish(treeListener.eventNames.select, {node: targetNode});
			if (!dojo.isIE)				
				dojo.html.scrollIntoView(targetNode.domNode);
		
		});*/
	});
	
	dojo.publish("/getObjectPathRequest", [{
		id: dijit.byId("subPageDialog").customParams.jumpToNode,
		resultHandler: deferred,
		ignoreDirtyFlag: true
	}]);
	
};

assignObject = function() {
	var node = dijit.byId("treeAssignObj").selectedNode;
	if (node) {
		var retVal = {};
		retVal.uuid = node.item.id[0].substring(10, node.item.id[0].length);
		retVal.title = node.item.title[0];
		retVal.objectClass = node.item.nodeDocType[0].substr(5, 1);

		dijit.byId("subPageDialog").customParams.resultHandler.callback(retVal);
	}

	dijit.byId("subPageDialog").hide();
}

dojo.addOnLoad(function() {
	init();
});
dojo.addOnUnload(function() {
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (dijit.byId("subPageDialog").customParams.resultHandler.fired == -1) {
		dijit.byId("subPageDialog").customParams.resultHandler.errback();
	}
});


</script>
</head>

<body>

  <div id="catalogueObject" class="">
    <div id="winNavi" style="top:0;">
		<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-8#maintanance-of-objects-8', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	  </div>
	  <div id="objectContent" class="content">

      <!-- CONTENT START -->
      <div class="inputContainer">
      	<div dojoType="dijit.layout.ContentPane" id="treeContainerAssignObj" style="height:413px;">
      		<div id="treeAssignObj" style="height:100%;"></div>
        </div>
      </div>
      <div class="inputContainer">
        <span class="button transparent">
		  <span style="float:right;"><button dojoType="dijit.form.Button" onClick="assignObject"><fmt:message key="dialog.links.select.assign" /></button></span>
        </span>
  	  </div>
  	  <div class="fill"></div>
      <!-- CONTENT END -->

    </div>
  </div>

</body>
</html>
