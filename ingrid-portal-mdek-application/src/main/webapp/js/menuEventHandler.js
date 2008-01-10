
/*
 * Menu Event Handler. Static Methods
 */

var menuEventHandler = {};

// Singleton
// menuEventHandler = new function MenuEventHandler() {}

menuEventHandler.handleNewEntity = function(mes) {
	var deferred = new dojo.Deferred();

	var selectedNode = getSelectedNode(mes);
	if (!selectedNode) {
	  		dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
	} else {
		if (selectedNode.id == "newNode") {
	  		dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
		} else {
			// publish a createObject request and attach the newly created node if it was successful
			deferred.addCallback(function(res){attachNewNode(selectedNode, res);});
			dojo.debug("Publishing event: /createObjectRequest("+selectedNode.id+")");
	  		dojo.event.topic.publish("/createObjectRequest", {id: selectedNode.id, resultHandler: deferred});
		}
	}
}

attachNewNode = function(selectedNode, res) {
    var tree = dojo.widget.byId("tree");
    var treeListener = dojo.widget.byId('treeListener');

	var treeController = dojo.widget.byId("treeController");
	var def = treeController.createChild(selectedNode, "last", _createNewNode(res));
	def.addCallback(function(res){
		tree.selectNode(res);
		tree.selectedNode = res;
		dojo.event.topic.publish(treeListener.eventNames.select, {node: res});
	});
	def.addErrback(function(){
		// If we got an error while attaching the node we still check if the node exists and select it
		var newNode = dojo.widget.byId("newNode");
		if (newNode) {
			newNode.parent.expand();
			tree.selectNode(newNode);
			tree.selectedNode = newNode;
			dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});
		}
	});
}



menuEventHandler.handlePreview = function(message) {
// Message parameter is the either the Context Menu Item (TreeMenuItemV3) or the Widget (dojo:toolbarbutton)
//   where the event originated
  dojo.debug('Message parameter: '+message);

  var selectedNode;
  
  if (message instanceof dojo.widget.TreeMenuItemV3)
  {
    selectedNode = message.getTreeNode();
    dojo.debug('Tree Node: '+selectedNode);

    var info = selectedNode.getInfo();
    dojo.debug("Widget ID: "+info.widgetId);
    dojo.debug("Object ID: "+info.objectId);
    dojo.debug("Context Menu: "+info.contextMenu);
    dojo.debug("App Type: "+info.nodeAppType);
  }
  else
  {
    var tree = dojo.widget.byId('tree');
    selectedNode = tree.selectedNode;
  }
  
  if (selectedNode)
    dojo.debug('Selected node: '+selectedNode);
//  alertNotImplementedYet();
}

menuEventHandler.handleCut = function(mes) {
	var selectedNode = getSelectedNode(mes);
	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCutHint"), dialog.WARNING);
	} else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var treeController = dojo.widget.byId("treeController");
			treeController.prepareCut(selectedNode);
		});
		deferred.addErrback(function() {
    		dialog.show(message.get("general.hint"), message.get("tree.nodeCanCutError"), dialog.WARNING);		
		});

  		dojo.event.topic.publish("/canCutObjectRequest", {id: selectedNode.id, resultHandler: deferred});
	}
}


menuEventHandler.handleCopyEntity = function(msg) {
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCopyHint"), dialog.WARNING);	
	} else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var treeController = dojo.widget.byId("treeController");
			treeController.prepareCopy(selectedNode, false);
		});
		deferred.addErrback(function() {
    		dialog.show(message.get("general.hint"), message.get("tree.nodeCanCopyError"), dialog.WARNING);		
		});

  		dojo.event.topic.publish("/canCopyObjectRequest", {id: selectedNode.id, copyTree: false, resultHandler: deferred});
	}
}

menuEventHandler.handleCopyTree = function(msg) {
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCopyHint"), dialog.WARNING);	
	} else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var treeController = dojo.widget.byId("treeController");
			treeController.prepareCopy(selectedNode, true);
		});
		deferred.addErrback(function() {
    		dialog.show(message.get("general.hint"), message.get("tree.nodeCanCopyError"), dialog.WARNING);		
		});

  		dojo.event.topic.publish("/canCopyObjectRequest", {id: selectedNode.id, copyTree: true, resultHandler: deferred});
	}
}

menuEventHandler.handlePaste = function(msg) {
	var targetNode = getSelectedNode(msg);
	if (!targetNode) {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodePasteHint"), dialog.WARNING);
	} else {
		var treeController = dojo.widget.byId("treeController");		
		if (treeController.nodeToCut != null) {
			if (targetNode == treeController.nodeToCut || _isChildOf(targetNode, treeController.nodeToCut)) {
				// If an invalid target is selected (same node or child of node to cut)
				dialog.show(message.get("general.hint"), message.get("tree.nodePasteInvalidHint"), dialog.WARNING);
			} else {
				// Valid target was selected. Start the request
				var deferred = new dojo.Deferred();
				deferred.addCallback(function() {
					// Move was successful. Update the tree
					treeController.move(treeController.nodeToCut, targetNode, 0);
				});
				dojo.event.topic.publish("/cutObjectRequest", {srcId: treeController.nodeToCut.id, dstId: targetNode.id, resultHandler: deferred});
			}
		} else if (treeController.nodeToCopy != null) {
				// A node can be inserted everywhere. Start the paste request.
				var deferred = new dojo.Deferred();
				deferred.addCallback(function(res) {
					// Copy was successful. Update the tree.
					// TODO: write paste logic
					//   We either create a new node from the result or clone the copied node 
					treeController.clone(
						treeController.nodeToCopy,
						targetNode,
						0,
						treeController.copySubTree);
				});
				dojo.event.topic.publish("/copyObjectRequest", {
					srcId: treeController.nodeToCopy.id,
					dstId: targetNode.id,
					copyTree: treeController.copySubTree,
					resultHandler: deferred
				});
		} else {
	    	dialog.show(message.get("general.hint"), message.get("tree.nodePasteNoCutCopyHint"), dialog.WARNING);
		}
	}
}

menuEventHandler.handleSave = function() {
//                                dialog.show("Zwischenspeichern", 'Der aktuelle Datensatz befindet sich in der Bearbeitung. Wollen Sie wirklich speichern?', dialog.WARNING, 
//                                      [{caption:"OK",action:function(){alert("OK")}},{caption:"Cancel",action:dialog.CLOSE_ACTION}]);},
  dojo.debug('Publishing event: /saveRequest');
  dojo.event.topic.publish("/saveRequest");
}

menuEventHandler.handleUndo = function(mes) {
	var selectedNode = getSelectedNode(mes);

	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCutHint"), dialog.WARNING);
	}
	else {
    	dojo.debug("Publishing event: /loadRequest("+selectedNode.id+", "+selectedNode.nodeAppType+")");
    	dojo.event.topic.publish("/loadRequest", {id: selectedNode.id, appType: selectedNode.nodeAppType});
	}
}


menuEventHandler.handleDiscard = function() {alertNotImplementedYet();}

menuEventHandler.handleForwardToQS = function() {alertNotImplementedYet();}
menuEventHandler.handleFinalSave = function() {alertNotImplementedYet();}


menuEventHandler.handleMarkDeleted = function(msg) {
	// Get teh selected node from the message
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeDeleteHint"), dialog.WARNING);
	} else {
		// If a selected node was found do the following:
		// 1. Query the user if he really wants to delete the selected object
		//    This is accomplished by creating a deferred obj 'deferred' and passing it to the
		//    delete_object dialog. If the user clicks yes, the attached callback function is executed.
		// 2. The attached callback function publishes a delete request which is picked up by the
		//    udkDataProxy and sent to the backend. We need another deferred obj 'deleteObjDef' for this
		//    so we can see if the delete operation was successful.
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
	    	var deleteObjDef = new dojo.Deferred();
	    	deleteObjDef.addCallback(function() {
	    		// This function is called when the user has selected yes and the node was successfully
				// deleted from the database
				var tree = dojo.widget.byId("tree");
				if (tree.selectedNode == selectedNode || _isChildOf(tree.selectedNode, selectedNode)) {
					// If the currently selected Node is a child of the deleted node, we select it's parent after deletion
					var newSelectNode = selectedNode.parent;
					var treeListener = dojo.widget.byId("treeListener");
		    		selectedNode.destroy();
					tree.selectNode(newSelectNode);
					tree.selectedNode = newSelectNode;
					// We also have to reset the dirty flag since the 'dirty' ndoe is deleted anyway
					resetDirtyFlag();
					dojo.event.topic.publish(treeListener.eventNames.select, {node: newSelectNode});
				} else {
					// Otherwise we just delete the node
					selectedNode.destroy();
				}
	    	});
			// Tell the backend to delete the selected node.
	    	dojo.debug("Publishing event: /deleteRequest("+selectedNode.id+", "+selectedNode.nodeAppType+")");
	    	dojo.event.topic.publish("/deleteRequest", {id: selectedNode.id, resultHandler: deleteObjDef});				
		});

		// params for the first (really delete object query) dialog.
		var params = {
			nodeTitle: selectedNode.title,
			nodeHasChildren: selectedNode.isFolder,
			resultHandler: deferred
		};

		dialog.showPage("Delete node", "mdek_delete_object_dialog.html", 342, 220, true, params);
	}
}


menuEventHandler.handleUnmarkDeleted = function() {alertNotImplementedYet();}
menuEventHandler.handleShowChanges = function() {alertNotImplementedYet();}
//                            		var win = window.open("qs_vergleich.html", 'preview', 'width=720, height=690, resizable=yes, scrollbars=yes, status=yes');
//                            		win.focus();

menuEventHandler.handleShowComment = function() {
    dojo.debug("Publishing event: /loadRequest(5CE671D3-5475-11D3-A172-08002B9A1D1D, O)");
    dojo.event.topic.publish("/loadRequest", {id: "5CE671D3-5475-11D3-A172-08002B9A1D1D", appType: "O"});
//	alertNotImplementedYet();
}
//                                dialog.showPage("Kommentar ansehen/hinzufügen", "erfassung_modal_kommentar.html", 1010, 470, false);},



// ------------------------- Helper functions -------------------------

function alertNotImplementedYet()
{
  alert("Diese Funktionalität ist noch nicht implementiert.");
}

function getSelectedNode(message) {
  if (message instanceof dojo.widget.TreeMenuItemV3)
  {
    return message.getTreeNode();
  }
  else
  {
    return dojo.widget.byId("tree").selectedNode;
  }
}

function _createNewNode(obj)
{
	return {contextMenu: 'contextMenu1',
			isFolder: false,
			nodeDocType: obj.nodeDocType,	// Initial display type of a new node
			title: obj.objectName,
			dojoType: 'ingrid:TreeNode',
			nodeAppType: obj.nodeAppType,
			id: obj.uuid};	// "newNode"
}

function _isChildOf(childNode, targetNode) {
	if (!childNode.parent) {
		return false; 
	} else if (childNode.parent.id == targetNode.id) {
		return true;
	} else if (childNode.parent.id == "objectRoot") {
		return false;
	} else {
		return _isChildOf(childNode.parent, targetNode);
	}
}


