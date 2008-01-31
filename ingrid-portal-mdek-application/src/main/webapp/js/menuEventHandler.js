
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
			deferred.addErrback(function(err){
				dialog.show(message.get('general.error'), message.get('tree.nodeCreateError'), dialog.WARNING);
			});
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
	def.addErrback(function(mes){
		// If we got an error while attaching the node we still check if the node exists and select it
		// TODO do we still need this?
		var newNode = dojo.widget.byId("newNode");
		if (newNode) {
			newNode.parent.expand();
			tree.selectNode(newNode);
			tree.selectedNode = newNode;
			dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});
		} else {
			dialog.show(message.get("general.error"), message.get("tree.nodeCreateLocalError"), dialog.WARNING);
			dojo.debug(mes);
		}
	});
}



menuEventHandler.handlePreview = function(message) {
// Message parameter is the either the Context Menu Item (TreeMenuItemV3) or the Widget (dojo:toolbarbutton)
//   where the event originated
  dojo.debug('Message parameter: '+message);

  var selectedNode;
  var useDirtyData = false;
  
  if (message instanceof dojo.widget.TreeMenuItemV3)  {
    selectedNode = message.getTreeNode();
  }  else  {
  	var tree = dojo.widget.byId('tree');
    selectedNode = tree.selectedNode;
    useDirtyData = true;
  }
  
	// params for the first (really delete object query) dialog.
	var params = {
		useDirtyData: useDirtyData,
		selectedNodeId: selectedNode.id
	};
	
	dialog.showPage("Detail View", "mdek_detail_view_dialog.html", 755, 600, false, params);
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
		deferred.addErrback(displayErrorMessage);

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
		deferred.addErrback(displayErrorMessage);

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
    		dialog.show(message.get("general.error"), message.get("tree.nodeCanCopyError"), dialog.WARNING);		
		});

  		dojo.event.topic.publish("/canCopyObjectRequest", {id: selectedNode.id, copyTree: true, resultHandler: deferred});
	}
}

menuEventHandler.handlePaste = function(msg) {
	var targetNode = getSelectedNode(msg);
	if (!targetNode || targetNode.id == "newNode") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodePasteHint"), dialog.WARNING);
	} else {
		var tree = dojo.widget.byId("tree");
		var treeListener = dojo.widget.byId("treeListener");
		var treeController = dojo.widget.byId("treeController");		

		if (treeController.nodeToCut != null) {
//			if (targetNode == treeController.nodeToCut || _isChildOf(targetNode, treeController.nodeToCut)) {
				// If an invalid target is selected (same node or child of node to cut)
//				dialog.show(message.get("general.hint"), message.get("tree.nodePasteInvalidHint"), dialog.WARNING);
//				return;
//			} else {
				// Valid target was selected. Start the request
				var deferred = new dojo.Deferred();
				deferred.addCallback(function() {
					// Move was successful. Update the tree
					treeController.move(treeController.nodeToCut, targetNode, 0);
				});
				// Error Handler. Move was unsuccessful. Notify user and do nothing.
				deferred.addErrback(displayErrorMessage);

				// Open the target node before moving a node. If the targetNode would be expanded afterwards,
				// a widget collision would be possible (nodeToCut already exists in the target after expand)
				var def = treeController.expand(targetNode);
				def.addCallback(function() {
					dojo.event.topic.publish("/cutObjectRequest", {srcId: treeController.nodeToCut.id, dstId: targetNode.id, resultHandler: deferred});
				});
//			}
		} else if (treeController.nodeToCopy != null) {
			// If a newNode currently exists and is included in the copy operation abort the copy operation with
			// an error message
			var newNode = dojo.widget.byId("newNode");
			if (newNode && treeController.copySubTree && _isChildOf(newNode, treeController.nodeToCopy)) {
				dialog.show(message.get("general.hint"), message.get("tree.saveNewNodeHint"), dialog.WARNING);
				return;
			} 

			// A node can be inserted everywhere. Start the paste request.
			var deferred = new dojo.Deferred();
			deferred.addCallback(function(res) {
				// Copy was successful. Update the tree.
				treeController.createChild(targetNode, "last", res);
				treeController.expand(targetNode);
				// TODO Which node to select?
				// Selection is lost (somehow) on 'createChild'. Reselect the currently selected node
				tree.selectedNode.viewUnselect();
				tree.selectedNode.viewSelect();
			});
			deferred.addErrback(function(mes) {
				// Copy was unsuccessful. Notify user(?) and do nothing.
				dialog.show(message.get("general.error"), message.get("tree.nodeCopyError"), dialog.WARNING);
				dojo.debug("Copy operation failed: "+mes);
			});

			// Open the target node before copying a node. 
			var def = treeController.expand(targetNode);
			def.addCallback(function() {
				dojo.event.topic.publish("/copyObjectRequest", {
					srcId: treeController.nodeToCopy.id,
					dstId: targetNode.id,
					copyTree: treeController.copySubTree,
					resultHandler: deferred
				});				
			});
		} else {
	    	dialog.show(message.get("general.hint"), message.get("tree.nodePasteNoCutCopyHint"), dialog.WARNING);
		}
	}
}

menuEventHandler.handleSave = function() {
//                                dialog.show("Zwischenspeichern", 'Der aktuelle Datensatz befindet sich in der Bearbeitung. Wollen Sie wirklich speichern?', dialog.WARNING, 
//                                      [{caption:"OK",action:function(){alert("OK")}},{caption:"Cancel",action:dialog.CLOSE_ACTION}]);},
	var deferred = new dojo.Deferred();
	deferred.addCallback(function(res) {
		var tree = dojo.widget.byId("tree");

		tree.selectedNode = dojo.widget.byId(res.uuid);
		tree.selectedNode.viewUnselect();
		tree.selectedNode.viewSelect();
		resetRequiredFields();
	});
	
	deferred.addErrback(function(err) {
		// TODO ask user if he wants to force the publication cond on the proper error
		displayErrorMessage(err);
	});
	
	dojo.debug('Publishing event: /saveRequest');
	dojo.event.topic.publish("/saveRequest", {resultHandler: deferred});
}

menuEventHandler.handleUndo = function(mes) {
	var selectedNode = getSelectedNode(mes);

	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCutHint"), dialog.WARNING);
	}
	else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var def = new dojo.Deferred();
			def.addErrback(function(msg){
				dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
				dojo.debug(msg);
			});
			udkDataProxy.resetDirtyFlag();
    		dojo.debug("Publishing event: /loadRequest("+selectedNode.id+", "+selectedNode.nodeAppType+")");
	    	dojo.event.topic.publish("/loadRequest", {id: selectedNode.id, appType: selectedNode.nodeAppType, resultHandler:def});
		});
		dialog.showPage(message.get("dialog.undoChangesTitle"), "mdek_delete_working_copy_dialog.html", 342, 220, true, {resultHandler:deferred, action:"UNDO"});
	}
}


menuEventHandler.handleDiscard = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeDeleteHint"), dialog.WARNING);
	} else {
		// If a selected node was found do the following:
		// 1. Query the user if he really wants to delete the working copy of the selected object
		//    This is accomplished by creating a deferred obj 'deferred' and passing it to the
		//    delete_working_copy dialog. If the user clicks yes, the attached callback function is executed.
		// 2. The attached callback function publishes a delete working copy request which is picked up by the
		//    udkDataProxy and sent to the backend. We need another deferred obj 'deleteObjDef' for this
		//    so we can see if the delete operation was successful.
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
	    	var deleteObjDef = new dojo.Deferred();
	    	deleteObjDef.addCallback(function(res) {
	    		// This function is called when the user has selected yes and the working copy was successfully
				// deleted from the database
				// The result 'res' is either:
				// null - indicating that no other version of the object existed. In this case the object
				//        has to be removed from the tree
				// a valid MdekDataBean - indicating that the working copy has been deleted, but another
				//        version of the object still exists. In this case the object has already been loaded
				//        by the dataProxy. 
				if (res == null) {
					// The node has to be deleted from the tree
					var tree = dojo.widget.byId("tree");
					if (tree.selectedNode == selectedNode || _isChildOf(tree.selectedNode, selectedNode)) {
						// If the currently selected Node is a child of the deleted node, we select it's parent after deletion
						var newSelectNode = selectedNode.parent;
						var treeListener = dojo.widget.byId("treeListener");
			    		selectedNode.destroy();

						var d = new dojo.Deferred();
						d.addCallback(function(){				
							tree.selectNode(newSelectNode);
							tree.selectedNode = newSelectNode;
							dojo.html.scrollIntoView(newSelectNode.domNode);
						});
						d.addErrback(function(msg){
							dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
							dojo.debug(msg);
						});

						// We also have to reset the dirty flag since the 'dirty' ndoe is deleted anyway
						udkDataProxy.resetDirtyFlag();
			    		dojo.debug("Publishing event: /loadRequest("+newSelectNode.id+", "+newSelectNode.nodeAppType+")");
				    	dojo.event.topic.publish("/loadRequest", {id: newSelectNode.id, appType: newSelectNode.nodeAppType, resultHandler:d});
					} else {
						// The selection does not have to be altered. Delete the node.
						selectedNode.destroy();
					}
				} else {
					// Another version of the node still exists. Just update the dirty flag(?).
				}
	    	});
			deleteObjDef.addErrback(function(mes) {
				dialog.show(message.get("general.error"), message.get("tree.nodeDiscardError"), dialog.WARNING);
				dojo.debug(mes);
			});

			// Tell the backend to delete the selected node.
	    	dojo.debug("Publishing event: /deleteWorkingCopyRequest("+selectedNode.id+", "+selectedNode.nodeAppType+")");
	    	dojo.event.topic.publish("/deleteWorkingCopyRequest", {id: selectedNode.id, resultHandler: deleteObjDef});				
		});

		// params for the first (really delete object query) dialog.
		var params = {
			nodeTitle: selectedNode.title,
			nodeHasChildren: selectedNode.isFolder,
			isPublished: selectedNode.isPublished,
			resultHandler: deferred
		};

		if (selectedNode.isPublished) {
			dialog.showPage(message.get("dialog.discardPubExistTitle"), "mdek_delete_working_copy_dialog.html", 342, 220, true, params);
		} else {
			dialog.showPage(message.get("dialog.discardPubNotExistTitle"), "mdek_delete_working_copy_dialog.html", 342, 220, true, params);
		}
		
	}
}


menuEventHandler.handleDelete = function(msg) {
	// Get the selected node from the message
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
				if (tree.selectedNode == selectedNode || (tree.selectedNode && _isChildOf(tree.selectedNode, selectedNode))) {
					// If the currently selected Node is a child of the deleted node, we select it's parent after deletion
					var newSelectNode = selectedNode.parent;
					var treeListener = dojo.widget.byId("treeListener");
		    		selectedNode.destroy();

					var d = new dojo.Deferred();
					d.addCallback(function(){				
						tree.selectNode(newSelectNode);
						tree.selectedNode = newSelectNode;
						dojo.html.scrollIntoView(newSelectNode.domNode);
					});
					d.addErrback(function(msg){
						dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
						dojo.debug(msg);
					});

					// We also have to reset the dirty flag since the 'dirty' ndoe is deleted anyway
					udkDataProxy.resetDirtyFlag();
		    		dojo.debug("Publishing event: /loadRequest("+newSelectNode.id+", "+newSelectNode.nodeAppType+")");
			    	dojo.event.topic.publish("/loadRequest", {id: newSelectNode.id, appType: newSelectNode.nodeAppType, resultHandler:d});
				} else {
					// Otherwise we just delete the node
					selectedNode.destroy();
				}
	    	});
			deleteObjDef.addErrback(function(mes) {
				dialog.show(message.get("general.error"), message.get("tree.nodeDeleteError"), dialog.WARNING);
				dojo.debug(mes);
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

// Reloads the tree structure for the selected root node
// TODO: adapt to reload only the selected root node
//       at the moment the whole tree is reloaded
menuEventHandler.reloadSubTree = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);
	var treeListener = dojo.widget.byId("treeListener");
	dojo.event.topic.publish(treeListener.eventNames.select, {node: selectedNode});
	if (selectedNode && selectedNode.id == "objectRoot") {
		selectedNode.destroy();
		EntryService.getSubTree(null, null, 1, 
			function (str) {
				var tree = dojo.widget.byId('tree');
				tree.setChildren(str);
			}
		);
	}
}


menuEventHandler.handleFinalSave = function() {
	if (!checkValidityOfInputElements()){
		dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
		return;
	}

	var nodeData = udkDataProxy._getData();
	if (isObjectPublishable(nodeData)) {
		var deferred = new dojo.Deferred();
		deferred.addErrback(displayErrorMessage);

		dojo.debug("Publishing event: /publishObjectRequest");
		dojo.event.topic.publish("/publishObjectRequest", {resultHandler: deferred});		
	} else {
  		dialog.show(message.get("general.hint"), message.get("tree.nodeCanPublishHint"), dialog.WARNING);
	}
}



menuEventHandler.handleForwardToQS = function() {alertNotImplementedYet();}
menuEventHandler.handleMarkDeleted = function(msg) {alertNotImplementedYet();}
menuEventHandler.handleUnmarkDeleted = function() {alertNotImplementedYet();}

menuEventHandler.handleShowChanges = function(msg) {
	// Message parameter is the either the Context Menu Item (TreeMenuItemV3) or the Widget (dojo:toolbarbutton)
	//   where the event originated
  dojo.debug('Message parameter: '+msg);

 	var tree = dojo.widget.byId('tree');
  var selectedNode = tree.selectedNode;
  dojo.debug('selectedNode id: '+ selectedNode.id);
  
	// params for the first (really delete object query) dialog.
	var params = {
		selectedNodeId: selectedNode.id
	};
	
  dojo.debug('done.');
	dialog.showPage("Compare View", "mdek_compare_view_dialog.html", 755, 600, false, params);
  dojo.debug('opened.');
}

menuEventHandler.handleShowComment = function() {
	dialog.showPage("Kommentar ansehen/hinzufügen", "mdek_comments_dialog.html", 1010, 470, false);
/*
	var nodeId = prompt("Jump to node with uuid", "5CE671D3-5475-11D3-A172-08002B9A1D1D");
 	if (nodeId) {
 		menuEventHandler.handleSelectNodeInTree(nodeId);
 	}
*/
}


// Expands the tree according to the nodeIds in pathList.
// pathList should be a list containing node IDs from the top element to the target node.
// index is the index where the expand process is started
// resultHandler is an optional deferred obj which is called when all nodes have been expanded
_expandPathRec = function(pathList, index, resultHandler) {
	if (index >= pathList.length) {
		resultHandler.callback();
		return;
	} else {
		if (!dojo.widget.byId("objectRoot").isExpanded) {
			// Expand the root object if it isn't already expanded
			var treeController = dojo.widget.byId("treeController");
			var deferred = treeController.expand(dojo.widget.byId("objectRoot"));
			deferred.addCallback(function() {
				_expandPathRec(pathList, 0, resultHandler);
			});
		} else {
			var treeController = dojo.widget.byId("treeController");
			var deferred = treeController.expand(dojo.widget.byId(pathList[index]));
			deferred.addCallback(function() {
				_expandPathRec(pathList, index+1, resultHandler);
			});
		}
	}
}

// Expands the tree according to the nodeIds in pathList.
// pathList should be a list containing node IDs from the top element to the target node.
// The return value is a deferred object which is invoked after all nodes have been expanded
_expandPath = function(pathList) {
	var deferred = new dojo.Deferred();
	_expandPathRec(pathList, 0, deferred);
	return deferred;
}

menuEventHandler.handleSelectNodeInTree = function(nodeId) {
	if (nodeId != "newNode" && nodeId != "objectRoot") {
		var deferred = new dojo.Deferred();
		var treeController = dojo.widget.byId("treeController");

		deferred.addCallback(function(pathList) {
			var def = _expandPath(pathList);
			def.addCallback(function(){
				var tree = dojo.widget.byId("tree");
				var treeListener = dojo.widget.byId("treeListener");
				var targetNode = dojo.widget.byId(pathList[pathList.length-1]);

				var d = new dojo.Deferred();
				d.addCallback(function(){				
					tree.selectNode(targetNode);
					tree.selectedNode = targetNode;
					dojo.html.scrollIntoView(targetNode.domNode);
				});
				d.addErrback(function(msg){
					dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
					dojo.debug(msg);
				});

	    		dojo.debug("Publishing event: /loadRequest("+targetNode.id+", "+targetNode.nodeAppType+")");
		    	dojo.event.topic.publish("/loadRequest", {id: targetNode.id, appType: targetNode.nodeAppType, resultHandler:d});
			});
		});
    	dojo.event.topic.publish("/getObjectPathRequest", {id: nodeId, resultHandler: deferred});		
	}
}

// ------------------------- Helper functions -------------------------

function displayErrorMessage(err) {
	// Show errors depending on outcome
	if (err && err.message) {
		if (err.message.indexOf("INPUT_INVALID_ERROR") != -1) {
	    	dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
		} else if (err.message.indexOf("PARENT_NOT_PUBLISHED") != -1) {
			dialog.show(message.get("general.error"), message.get("operation.error.parentNotPublishedError"), dialog.WARNING);
		} else if (err.message.indexOf("TARGET_IS_SUBNODE_OF_SOURCE") != -1) {
			dialog.show(message.get("general.error"), message.get("operation.error.targetIsSubnodeOfSourceError"), dialog.WARNING);
		} else if (err.message.indexOf("SUBTREE_HAS_WORKING_COPIES") != -1) {
			dialog.show(message.get("general.error"), message.get("operation.error.subTreeHasWorkingCopiesError"), dialog.WARNING);
		} else {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), err.message), dialog.WARNING);				
		}
	} else {
		// Show error message if we can't determine what went wrong
		dialog.show(message.get("general.error"), message.get("dialog.undefinedError"), dialog.WARNING);
	}
}


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
			isFolder: obj.isFolder,
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
