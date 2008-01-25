/*
 * Tree Event Handler. Static Methods
 */

var treeEventHandler = {};


// ----- Add the listener -> TODO move to mdek_entry.jsp -----
/*
function nodeSelectListener(message)
{
	var nodeId = message.node.id;
	var nodeAppType = message.node.nodeAppType;

	if (nodeId != "objectRoot") {
		var deferred = new dojo.Deferred();
		dojo.debug("Publishing event: /loadRequest("+nodeId+", "+nodeAppType+")");
		dojo.event.topic.publish("/loadRequest", {id: nodeId, appType: nodeAppType, resultHandler: deferred});
		deferred.addErrback(function(mes) {
			dojo.debug(mes);
			dialog.show(message.get("general.error"), message.get("tree.nodeLoadError"), dialog.WARNING);
		});
 	}
}
*/

aroundSelectNode = function(invocation) {
	// Continue with invocation without loading if:
	// 		target nodeId == objectRoot
	//		target nodeId == null (new node, invocation[0] == undefined)
	// 		target nodeId == newNode (if a new node is selected and clicked again)
	//		target nodeId == sourceId
	//
	// Otherwise try to load the clicked node and proceed on success

    if (!dojo.html.hasClass(invocation.args[1].target, "TreeLabel")) {
    	return invocation.proceed();
    }


	var curSelectedNode = dojo.widget.byId("tree").selectedNode;
	var curSelectedNodeId = null;
	if (curSelectedNode) {
//		dojo.debug("curSelectedNode: "+curSelectedNode);
		curSelectedNodeId = curSelectedNode.id;
//		dojo.debug("curSelectedNodeId: "+curSelectedNodeId);
	}

//	dojo.debug("invocation.args: "+invocation.args);
//	dojo.debug("invocation.args[0]: "+invocation.args[0]);
	if (typeof(invocation.args[0]) != "undefined") {
		var targetNode =  invocation.args[0];
		var targetNodeId = invocation.args[0].id;
		var targetNodeAppType = invocation.args[0].nodeAppType;
		
		if (targetNodeId == null || targetNodeId == "objectRoot" || targetNodeId == "newNode" || targetNodeId == curSelectedNodeId) {
			return invocation.proceed();
		} else {
			var deferred = new dojo.Deferred();
			dojo.debug("Publishing event: /loadRequest("+targetNodeId+", "+targetNodeAppType+")");
			dojo.event.topic.publish("/loadRequest", {id: targetNodeId, appType: targetNodeAppType, resultHandler: deferred});

			deferred.addCallback(function(msg) {
//				dojo.debug("Obj loaded. Proceeding...");
				var retVal = invocation.proceed();
				return retVal;
			});
			deferred.addErrback(function(msg) {
//				dojo.debug("Obj load failed. Aborting...");
				dojo.debug(msg);
				dialog.show(message.get("general.error"), message.get("tree.nodeLoadError"), dialog.WARNING);
			});			
		}
	} else {
//		dojo.debug("invocation == undefined. Proceeding...");
//		return invocation.proceed();
	}

/*
	if (invocation.args[0].id != "objectRoot") {
		return invocation.proceed();
	} else {
		var deferred = udkDataProxy.checkForUnsavedChanges(invocation.args[0].id);
		var stdCallback = function() {return invocation.proceed();};
		var errorCallback = function() {dojo.debug("Select cancelled.");};
	}
	deferred.addCallbacks(stdCallback, errorCallback);
	return deferred;
*/
}


dojo.addOnLoad(function()
{
/*
  var treeListener = dojo.widget.byId('treeListener');
  if (treeListener)
    dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelectListener");
  else
    alert("Error in js/treeEventHandler.js: Widget treeListener could not be found.");
*/

	var treeListener = dojo.widget.byId("treeListener");
	dojo.event.connect("around", treeListener, "processNode", aroundSelectNode);

});