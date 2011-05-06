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
		
		} else if (selectedNode.item.nodeAppType == "O") {
			// publish a createObject request and attach the newly created node if it was successful
			deferred.addCallback(function(res){
				attachNewNode(selectedNode, res);
				menuEventHandler.openCreateObjectWizardDialog();
			});
			deferred.addErrback(function(err){
//				dialog.show(message.get('general.error'), message.get('tree.nodeCreateError'), dialog.WARNING);
				displayErrorMessage(err);
			});
			console.debug("Publishing event: /createObjectRequest("+selectedNode.id+")");
	  		dojo.publish("/createObjectRequest", [{id: selectedNode.id, resultHandler: deferred}]);

		} else if (selectedNode.item.nodeAppType == "A") {
			if (selectedNode.objectClass == 2 || selectedNode.objectClass == 3) {
				// TODO: Show error message
				return;
			}

			// Determine the class of the newly created Address.
			// If the selected node if is 'addressFreeRoot' we can directly create the address without presenting the dialog
			if (selectedNode.id == "addressFreeRoot") {
				menuEventHandler._createNewAddress(3, selectedNode);				
			} else if (selectedNode.id == "addressRoot") {
				menuEventHandler._createNewAddress(0, selectedNode);
			} else {
				var selectClassDef = new dojo.Deferred();
				selectClassDef.addCallback(function(addressClass){ menuEventHandler._createNewAddress(addressClass, selectedNode); });
	
				var params = { parentId: selectedNode.id, parentClass: selectedNode.item.objectClass, resultHandler: selectClassDef }
				dialog.showPage(message.get("dialog.createAddressTitle"), "dialogs/mdek_address_select_class_dialog.jsp", 350, 160, false, params);
			}
		}
	}
}

menuEventHandler._createNewAddress = function(addressClass, parentNode) {
//	console.debug("_createNewAddress("+addressClass+", "+parentNode.id+")");
	var parentId = parentNode.id[0];

	if (addressClass == 3 && parentId == "addressRoot") {
		var treeController = dijit.byId("treeController");
		var def = treeController.expand(dijit.byId("addressRoot"));
		def.addCallback(function() {
			menuEventHandler._createNewAddress(addressClass, dijit.byId("addressFreeRoot"));
		});
		return;
	}

	var deferred = new dojo.Deferred();

	// publish a createNode request and attach the newly created node if it was successful
	deferred.addCallback(function(res){ 
		attachNewNode(dijit.byId(parentId), res);
	});
	deferred.addErrback(function(err){
//		dialog.show(message.get('general.error'), message.get('tree.nodeCreateError'), dialog.WARNING);
		displayErrorMessage(err);
	});
	console.debug("Publishing event: /createAddressRequest("+parentId+")");
	dojo.publish("/createAddressRequest", [{id: parentId, addressClass: addressClass, resultHandler: deferred}]);
}


attachNewNode = function(selectedNode, res) {
    var tree = dijit.byId("dataTree");
    //var treeListener = dijit.byId('treeListener');

	//var treeController = dijit.byId("treeController");
	

	//var def = treeController.createChild(selectedNode, "last", _createNewNode(res, selectedNode.objectClass));
	var newNode = _createNewNode(res, selectedNode.item.objectClass[0]);
	
	// expand tree node if not already done!
	var def;
	var selNodeWidget = dijit.byId(selectedNode.id[0]);
	if (!selNodeWidget.isExpanded) {
		// need a deferred here!
		//def = selNodeWidget.expand();
		def = tree._expandNode(selNodeWidget, false);
	} else {
		def = new dojo.Deferred();
		def.callback();
	}
	
					
	//var def = new dojo.Deferred();
	def.addCallback(function(){
        //tree.model.store.save();
		var item = tree.model.store.newItem(newNode, {
			parent: selectedNode.item,
			attribute: "children"
		});
		
		//tree.model.store.save(); // does not store it in subnode!!!
		var newItem = dijit.byId(item.id[0]);
        UtilTree.selectNode("dataTree", item.id[0]);
		dojo.publish("/selectNode", [{node: item}]);
		dojo.window.scrollIntoView(newItem.domNode);
        dijit.byId("dataFormContainer").resize();
	});
	def.addErrback(function(mes){
		// If we got an error while attaching the node we still check if the node exists and select it
		// TODO do we still need this?
		var newNode = dijit.byId("newNode");
		if (newNode) {
			newNode.getParent().expand();
            UtilTree.selectNode("dataTree", "newNode");
			dojo.publish("/selectNode", [{node: newNode.item}]);
			dojo.window.scrollIntoView(res.domNode);
		} else {
			dialog.show(message.get("general.error"), message.get("tree.nodeCreateLocalError"), dialog.WARNING);
			console.debug(mes);
		}
	});
}

menuEventHandler.handlePreview = function(msg) {
    var selectedNode = getSelectedNode(msg);
    var useDirtyData = false;
  
    // check if the preview was called via the context menu or directly via the menu button
    if (dojo.isIE) {
      if (msg.srcElement.className.indexOf("dijitMenuItemLabel") == -1) {
          useDirtyData = true;
      }
    } else {
      if (!msg.target.classList.contains("dijitMenuItemLabel")) {
          useDirtyData = true;
      }
    }
  
	// params for the first (really delete object query) dialog.
	var params = {
		useDirtyData: useDirtyData,
		selectedNode: selectedNode
	};
	
	if (selectedNode.item.nodeAppType == "O") {
  	console.debug('Show object preview.');
		dialog.showPage(message.get("dialog.object.detailView.title"), "dialogs/mdek_detail_view_dialog.jsp", 755, 600, true, params);
	} else if (selectedNode.item.nodeAppType == "A") {
  	console.debug('Show address preview.');
		dialog.showPage(message.get("dialog.address.detailView.title"), "dialogs/mdek_detail_view_address_dialog.jsp", 755, 600, true, params);
	}
}

menuEventHandler.handleCut = function(mes) {
	var selectedNode = getSelectedNode(mes);
	if (!selectedNode || selectedNode.id == "objectRoot" || selectedNode.id == "addressRoot" || selectedNode.id == "addressFreeRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCutHint"), dialog.WARNING);
	} else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var tree = dijit.byId("dataTree");
			tree.prepareCut(selectedNode.item);
		});

		deferred.callback();
		// Removed subtree check so address and object working copies can be moved
/*
		deferred.addErrback(function (err) {
			if (typeof(err) != "undefined")
				err.nodeAppType = selectedNode.nodeAppType;
			displayErrorMessage(err);
		});

		if (selectedNode.nodeAppType == "O") {
  			dojo.publish("/canCutObjectRequest", {id: selectedNode.id, resultHandler: deferred});
  		} else if (selectedNode.nodeAppType == "A") {
  			dojo.publish("/canCutAddressRequest", {id: selectedNode.id, resultHandler: deferred});
  		}
*/
	}
}


menuEventHandler.handleCopyEntity = function(msg) {
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id == "objectRoot" || selectedNode.id == "addressRoot" || selectedNode.id == "addressFreeRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCopyHint"), dialog.WARNING);	
	} else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var tree = dijit.byId("dataTree");
			tree.prepareCopy(selectedNode.item, false);
		});
		deferred.addErrback(displayErrorMessage);

		if (selectedNode.item.nodeAppType == "O") {
	  		dojo.publish("/canCopyObjectRequest", [{id: selectedNode.id[0], copyTree: false, resultHandler: deferred}]);
  		} else if (selectedNode.item.nodeAppType == "A") {
  			dojo.publish("/canCopyAddressRequest", [{id: selectedNode.id[0], copyTree: false, resultHandler: deferred}]);
  		}
	}
}

menuEventHandler.handleCopyTree = function(msg) {
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id[0] == "objectRoot" || selectedNode.id[0] == "addressRoot" || selectedNode.id[0] == "addressFreeRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCopyHint"), dialog.WARNING);	
	} else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var tree = dijit.byId("dataTree");
			tree.prepareCopy(selectedNode.item, true);
		});
		deferred.addErrback(function() {
    		dialog.show(message.get("general.error"), message.get("tree.nodeCanCopyError"), dialog.WARNING);		
		});

		if (selectedNode.item.nodeAppType == "O") {
	  		dojo.publish("/canCopyObjectRequest", [{id: selectedNode.id[0], copyTree: true, resultHandler: deferred}]);
  		} else if (selectedNode.item.nodeAppType == "A") {
	  		dojo.publish("/canCopyAddressRequest", [{id: selectedNode.id[0], copyTree: true, resultHandler: deferred}]);
  		}
	}
}

menuEventHandler.handlePaste = function(msg) {
	//var targetNodeItem = getSelectedNode(msg);
	var targetNode = getSelectedNode(msg);//dijit.byId(targetNodeItem.id[0]);
	
	if (!targetNode || targetNode.id == "newNode") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodePasteHint"), dialog.WARNING);
	} else {
		var tree = dijit.byId("dataTree");

		if (tree.nodeToCut != null) {
			if (targetNode.item == tree.nodeToCut || _isChildOf(targetNode, tree.nodeToCut)) {
				// If an invalid target is selected (same node or child of node to cut)
				dialog.show(message.get("general.hint"), message.get("tree.nodePasteInvalidHint"), dialog.WARNING);
				return;
			} else {
				var cutNodeWidget = dijit.byId(tree.nodeToCut.id[0]);
				var appType = tree.nodeToCut.nodeAppType[0];
				// Valid target was selected. Start the request
				var deferred = new dojo.Deferred();
				deferred.addCallback(function() {
					// Move was successful. Update the tree
					//tree.model.store.deleteItem(tree.nodeToCut);
                    UtilTree.deleteNode("dataTree", cutNodeWidget);
					tree.model.store.save();
					tree.refreshChildren(targetNode);
					var nodeToCut = tree.nodeToCut;
					if (appType == "A") {
						// If we moved an address from/to the freeAddress part of the tree, the icon has to be updated
						if (targetNode.id == "addressFreeRoot") {
							nodeToCut.nodeDocType[0] = "PersonAddress";	
							nodeToCut.objectClass[0] = 3;
						} else {
							if (nodeToCut.nodeDocType[0].indexOf("PersonAddress") === 0) {
								nodeToCut.nodeDocType[0] = "InstitutionPerson";	
								nodeToCut.objectClass[0] = 2;
							}
						}
					}
					tree.doPaste();
				});
				// Error Handler. Move was unsuccessful. Notify user and do nothing.
				deferred.addErrback(displayErrorMessage);

				// Open the target node before moving a node. If the targetNode would be expanded afterwards,
				// a widget collision would be possible (nodeToCut already exists in the target after expand)
				var def = tree._expandNode(targetNode);
				if (appType == "O") {
					def.addCallback(function() {
						var parentUuid = cutNodeWidget.getParent().id[0];
						if (parentUuid == "objectRoot") {
							parentUuid = null;
						}
						dojo.publish("/cutObjectRequest", [{srcId: tree.nodeToCut.id[0], parentUuid: parentUuid, dstId: targetNode.id[0], forcePublicationCondition: false, resultHandler: deferred}]);
					});
				} else if (appType == "A") {
					def.addCallback(function() {
						var parentUuid = cutNodeWidget.getParent().id[0];
						if (parentUuid == "addressRoot" || parentUuid == "addressFreeRoot") {
							parentUuid = null;
						}
						dojo.publish("/cutAddressRequest", [{srcId: tree.nodeToCut.id[0], parentUuid: parentUuid, dstId: targetNode.id[0], resultHandler: deferred}]);
					});
				}
			}
		} else if (tree.nodeToCopy != null) {
			// If a newNode currently exists and is included in the copy operation abort the copy operation with
			// an error message
			var newNode = dijit.byId("newNode");
			if (newNode && tree.copySubTree && _isChildOf(newNode, tree.nodeToCopy)) {
				dialog.show(message.get("general.hint"), message.get("tree.saveNewNodeHint"), dialog.WARNING);
				return;
			} 

			// A node can be inserted everywhere. Start the paste request.
			var deferred = new dojo.Deferred();
			deferred.addCallback(function(res) {
				// Copy was successful. Update the tree.
				tree.refreshChildren(targetNode);
				tree.doPaste();

				// TODO Which node to select?
				// Selection is lost (somehow) on 'createChild'. Reselect the currently selected node
				/*if (tree.selectedNode) {
					tree.selectedNode.viewUnselect();
					tree.selectedNode.viewSelect();
				}*/
			});
			// If copy was unsuccessful notify user and do nothing.
			deferred.addErrback(displayErrorMessage);

			// Open the target node before copying a node. 
			var def = tree._expandNode(targetNode);
			var appType = tree.nodeToCopy.nodeAppType;
			if (appType == "O") {
				def.addCallback(function() {
					dojo.publish("/copyObjectRequest", [{
						srcId: tree.nodeToCopy.id[0],
						dstId: targetNode.id[0],
						copyTree: tree.copySubTree,
						resultHandler: deferred
					}]);				
				});
				
			} else if (appType == "A") {
				def.addCallback(function() {
					dojo.publish("/copyAddressRequest", [{
						srcId: tree.nodeToCopy.id[0],
						dstId: targetNode.id[0],
						copyTree: tree.copySubTree,
						resultHandler: deferred
					}]);				
				});
			}
		} else {
	    	dialog.show(message.get("general.hint"), message.get("tree.nodePasteNoCutCopyHint"), dialog.WARNING);
		}
	}
}

menuEventHandler.handleSave = function() {
	var deferred = new dojo.Deferred();
	deferred.addCallback(function(res) {
		var tree = dijit.byId("dataTree");

		tree.selectedNode = dijit.byId(res.uuid);
		
		dojo.publish("/selectNode", [{node: tree.selectedNode.item}]);

		resetRequiredFields();
	});
	
	deferred.addErrback(function(err) {
		if (err.message != "undefined") {
			displayErrorMessage(err);
		}
	});
	
	console.debug('Publishing event: /saveRequest');
	dojo.publish("/saveRequest", [{resultHandler: deferred}]);
}

menuEventHandler.handleUndo = function(mes) {
	var selectedNode = getSelectedNode(mes);

	if (selectedNode.id == "newNode") {
		menuEventHandler.handleDiscard(mes);
		return;
	}

	if (!selectedNode || selectedNode.id == "objectRoot") {
    	dialog.show(message.get("general.hint"), message.get("tree.selectNodeCutHint"), dialog.WARNING);
	}
	else {
		var deferred = new dojo.Deferred();
		deferred.addCallback(function() {
			var def = new dojo.Deferred();
			def.addErrback(function(msg){
				dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
				console.debug(msg);
			});
			udkDataProxy.resetDirtyFlag();
    		console.debug("Publishing event: /loadRequest("+selectedNode.id+", "+selectedNode.item.nodeAppType+")");
	    	dojo.publish("/loadRequest", [{id: selectedNode.id[0], appType: selectedNode.item.nodeAppType[0], resultHandler:def}]);
		});
//		dialog.showPage(message.get("dialog.undoChangesTitle"), "mdek_delete_working_copy_dialog.html", 342, 220, true, {resultHandler:deferred, action:"UNDO"});
		var displayText = "";
		if (selectedNode.item.nodeAppType == "O")
			displayText = message.get("dialog.object.undoChangesMessage");
		else
			displayText = message.get("dialog.address.undoChangesMessage");

		dialog.show(message.get("dialog.undoChangesTitle"), displayText, dialog.INFO, [
        	{ caption: message.get("general.cancel"),  action: function() { deferred.errback(); } },
        	{ caption: message.get("general.yes"),     action: function() { deferred.callback(); } }
		]);
	}
}


menuEventHandler.handleDiscard = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);
	if (!selectedNode || selectedNode.id[0] == "objectRoot") {
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
                // The node has to be deleted from the tree
                var tree = dijit.byId("dataTree");
				if (res == null) {
                    // save store before removing anything ... needed for new nodes
                    // that are not included in store yet
                    tree.model.store.save();
					if (tree.selectedNode == selectedNode || _isChildOf(tree.selectedNode, selectedNode)) {
						// If the currently selected Node is a child of the deleted node, we select it's parent after deletion
						var newSelectNode = selectedNode.getParent();
						//tree.model.store.deleteItem(selectedNode.item);
                        UtilTree.deleteNode("dataTree", selectedNode);

						var d = new dojo.Deferred();
						d.addCallback(function(){
							UtilTree.selectNode("dataTree", newSelectNode.id[0]);
							dojo.publish("/selectNode", [{node: newSelectNode.item}]);
							//if (!dojo.isIE)				
							dojo.window.scrollIntoView(newSelectNode.domNode);
						});
						d.addErrback(function(msg){
							dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
							console.debug(msg);
						});

						// We also have to reset the dirty flag since the 'dirty' ndoe is deleted anyway
						udkDataProxy.resetDirtyFlag();
			    		console.debug("Publishing event: /loadRequest("+newSelectNode.id+", "+newSelectNode.item.nodeAppType+")");
				    	dojo.publish("/loadRequest", [{id: newSelectNode.id[0], appType: newSelectNode.item.nodeAppType, resultHandler:d}]);
					} else {
						// The selection does not have to be altered. Delete the node.
						//tree.model.store.deleteItem(selectedNode.item);
                        UtilTree.deleteNode("dataTree", selectedNode);
					}
				} else {
					// Another version of the node still exists. Just update the dirty flag(?).
				}
                // save again to be able to create a node with same id
                // otherwise object is still present in store (_pending->deletedItems)
                tree.model.store.save();
	    	});
            
            deferred.addErrback(function() {});
			deleteObjDef.addErrback(displayErrorMessage);

			// Tell the backend to delete the selected node.
	    	console.debug("Publishing event: /deleteWorkingCopyRequest("+selectedNode.id+", "+selectedNode.item.nodeAppType+")");
	    	dojo.publish("/deleteWorkingCopyRequest", [{id: selectedNode.id[0], resultHandler: deleteObjDef}]);				
		});

/*
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
*/	
		// Build the dialog parameters
		// messageKey = dialog.<object|address>.discardPub<Not>ExistMessage
		var titleText = "";
		var displayText = "";
		var messageKey = "dialog.";
		if (selectedNode.item.nodeAppType == "O") {
			messageKey += "object.";
		} else {
			messageKey += "address.";
		}

        // if it's a new node then isPublished property does not exist
		if (selectedNode.item.isPublished && selectedNode.item.isPublished[0]) {
			titleText = message.get("dialog.discardPubExistTitle");
			messageKey += "discardPubExistMessage";

		} else {
			titleText = message.get("dialog.discardPubNotExistTitle");
			messageKey += "discardPubNotExistMessage";
		}
		displayText = dojo.string.substitute(message.get(messageKey), [selectedNode.item.title[0]]);

		dialog.show(titleText, displayText, dialog.INFO, [
        	{ caption: message.get("general.cancel"),  action: function() { deferred.errback(); } },
        	{ caption: message.get("general.yes"),     action: function() { deferred.callback(); } }
		]);
	}
}


menuEventHandler.handleDelete = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);

	if (selectedNode && selectedNode.id == "newNode") {
		menuEventHandler.handleDiscard(msg);
		return;
	}

	if (!selectedNode || selectedNode.id == "objectRoot" || selectedNode == "addressRoot" || selectedNode == "addressFreeRoot") {
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
				var tree = dijit.byId("dataTree");
				if (tree.selectedNode == selectedNode || (tree.selectedNode && _isChildOf(tree.selectedNode, selectedNode))) {
					// If the currently selected Node is a child of the deleted node, we select it's parent after deletion
					var newSelectNode = dijit.byId(selectedNode.id[0]).getParent();
					//tree.model.store.deleteItem(selectedNode.item);
                    UtilTree.deleteNode("dataTree", selectedNode);

					var d = new dojo.Deferred();
					d.addCallback(function(){				
                        UtilTree.selectNode("dataTree", newSelectNode.id[0], true);
						dojo.publish("/selectNode", [{node: newSelectNode.item}]);
						dojo.window.scrollIntoView(newSelectNode.domNode);
					});
					d.addErrback(function(msg){
						dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
						console.debug(msg);
					});

					// We also have to reset the dirty flag since the 'dirty' ndoe is deleted anyway
					udkDataProxy.resetDirtyFlag();
		    		console.debug("Publishing event: /loadRequest("+newSelectNode.id+", "+newSelectNode.item.nodeAppType+")");
			    	dojo.publish("/loadRequest", [{id: newSelectNode.id[0], appType: newSelectNode.item.nodeAppType[0], resultHandler:d}]);
				} else {
					// Otherwise we just delete the node
					//tree.model.store.deleteItem(selectedNode.item);
                    UtilTree.deleteNode("dataTree", selectedNode);
				}
                tree.model.store.save();
	    	});
			deleteObjDef.addErrback(displayErrorMessage);

			// Tell the backend to delete the selected node.
	    	console.debug("Publishing event: /deleteRequest("+selectedNode.id+", "+selectedNode.nodeAppType+")");
	    	dojo.publish("/deleteRequest", [{id: selectedNode.id[0], resultHandler: deleteObjDef}]);				
		});

		// Build the message key (dialog.<object|address>.delete<Children>Message)
		var messageKey = "dialog.";
		if (selectedNode.item.nodeAppType == "O")
			messageKey += "object.delete";
		else
			messageKey += "address.delete";

		if (selectedNode.item.isFolder)
			messageKey += "Children";

		messageKey += "Message";
		var displayText = dojo.string.substitute(message.get(messageKey), [selectedNode.item.title]);

		dialog.show(message.get("general.delete"), displayText, dialog.INFO, [
                        { caption: message.get("general.cancel"), action: function() { /*deferred.errback();*/ } },
                        { caption: message.get("general.ok"),     action: function() { deferred.callback(); } }
		]);
	}
}

// Reloads the tree structure for the selected root node
// TODO: adapt to reload only the selected root node
//       at the moment the whole tree is reloaded
menuEventHandler.reloadSubTree = function(msg) {
	// Get the selected node from the message
	//var selectedNodeItem = getSelectedNode(msg);
	var selectedNode = getSelectedNode(msg);//dijit.byId(selectedNodeItem.id[0]);
	var tree = dijit.byId("dataTree");
	//var treeListener = dijit.byId("treeListener");
	//var treeController = dijit.byId("treeController");

	if (selectedNode) {
		//selectedNode.state = "UNCHECKED";
		udkDataProxy.resetDirtyFlag();
		//tree.selectNode(selectedNode);
		UtilTree.selectNode("dataTree", selectedNode.id[0]);
		//tree.selectedNode = selectedNode;
		dojo.publish("/selectNode", [{node: selectedNode.item}]);
		tree.refreshChildren(selectedNode);
	}
}


menuEventHandler.handleFinalSave = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);
	
	if (selectedNode.item.nodeAppType == "O") {
		menuEventHandler._handleFinalSaveObject(msg);
	} else if (selectedNode.item.nodeAppType == "A") {
		menuEventHandler._handleFinalSaveAddress(msg);
	}
}	

menuEventHandler._handleFinalSaveObject = function(msg) {
	/*var valid = checkValidityOfInputElements();

	if (valid != "VALID"){
		if (valid == "INVALID_INPUT_HTML_TAG_INVALID") {
			dialog.show(message.get("general.error"), message.get("dialog.inputInvalidHtmlTagError"), dialog.WARNING);
			
		} else {
			dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
		}
		return;
	}*/

	//var nodeData = udkDataProxy._getData();
	if (isObjectPublishable()) {
		var deferred = new dojo.Deferred();
		deferred.addErrback(displayErrorMessage);

		var dialogText = currentUdk.isMarkedDeleted ? message.get("dialog.object.markedDeleted.finalSaveMessage") : message.get("dialog.object.finalSaveMessage");

		// Show a dialog to query the user before publishing
		dialog.show(message.get("dialog.finalSaveTitle"), dialogText, dialog.INFO, [
        	{ caption: message.get("general.no"),  action: function() { deferred.callback(); } },
        	{ caption: message.get("general.yes"), action: function() {
					console.debug("Publishing event: /publishObjectRequest");
					dojo.publish("/publishObjectRequest", [{resultHandler: deferred}]);
        		}
        	}
		]);

	} else {
  		dialog.show(message.get("general.hint"), message.get("tree.nodeCanPublishHint"), dialog.WARNING);
	}
}
menuEventHandler._handleFinalSaveAddress = function(msg) {
	var nodeData = udkDataProxy._getData();
	if (isAddressPublishable(nodeData)) {
		var deferred = new dojo.Deferred();
		deferred.addErrback(displayErrorMessage);

		var dialogText = currentUdk.isMarkedDeleted ? message.get("dialog.address.markedDeleted.finalSaveMessage") : message.get("dialog.address.finalSaveMessage");

		// Show a dialog to query the user before publishing
		dialog.show(message.get("dialog.finalSaveTitle"), dialogText, dialog.INFO, [
        	{ caption: message.get("general.no"),  action: function() { deferred.callback(); } },
        	{ caption: message.get("general.yes"), action: function() {
					console.debug("Publishing event: /publishAddressRequest");
					dojo.publish("/publishAddressRequest", [{resultHandler: deferred}]);		
        		}
        	}
		]);

	} else {
  		dialog.show(message.get("general.hint"), message.get("tree.nodeCanPublishHint"), dialog.WARNING);
	}
}


menuEventHandler.handleForwardToQA = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);
	
	if (selectedNode.item.nodeAppType == "O") {
		menuEventHandler._handleForwardObjectToQA(msg);
	} else if (selectedNode.item.nodeAppType == "A") {
		menuEventHandler._handleForwardAddressToQA(msg);
	}
}	


menuEventHandler._handleForwardObjectToQA = function(msg) {
	// Forward the current object to the QA.
	/*var valid = checkValidityOfInputElements();

	if (valid != "VALID"){
		if (valid == "INVALID_INPUT_HTML_TAG_INVALID") {
			dialog.show(message.get("general.error"), message.get("dialog.inputInvalidHtmlTagError"), dialog.WARNING);
			
		} else {
			dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
		}
		return;
	}*/

	//var nodeData = udkDataProxy._getData();
	if (isObjectPublishable()) {
		var deferred = new dojo.Deferred();
		deferred.addErrback(displayErrorMessage);

		// Show a dialog to query the user before publishing
		dialog.show(message.get("dialog.forwardToQATitle"), message.get("dialog.object.forwardToQAMessage"), dialog.INFO, [
        	{ caption: message.get("general.no"),  action: function() { deferred.callback(); } },
        	{ caption: message.get("general.yes"), action: function() {
					console.debug("Publishing event: /forwardObjectToQARequest");
					dojo.publish("/forwardObjectToQARequest", [{resultHandler: deferred}]);
        		}
        	}
		]);

	} else {
  		dialog.show(message.get("general.hint"), message.get("tree.forwardToQAHint"), dialog.WARNING);
	}
}

menuEventHandler._handleForwardAddressToQA = function(msg) {
	// Forward the current address to the QA.
	var valid = checkValidityOfAddressInputElements();

	if (valid != "VALID"){
		if (valid == "INVALID_INPUT_HTML_TAG_INVALID") {
			dialog.show(message.get("general.error"), message.get("dialog.inputInvalidHtmlTagError"), dialog.WARNING);
			
		} else {
			dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
		}
		return;
	}

	var nodeData = udkDataProxy._getData();
	if (isAddressPublishable(nodeData)) {
		var deferred = new dojo.Deferred();
		deferred.addErrback(displayErrorMessage);

		// Show a dialog to query the user before forwarding
		dialog.show(message.get("dialog.forwardToQATitle"), message.get("dialog.address.forwardToQAMessage"), dialog.INFO, [
        	{ caption: message.get("general.no"),  action: function() { deferred.callback(); } },
        	{ caption: message.get("general.yes"), action: function() {
					console.debug("Publishing event: /forwardAddressToQARequest");
					dojo.publish("/forwardAddressToQARequest", [{resultHandler: deferred}]);		
        		}
        	}
		]);

	} else {
  		dialog.show(message.get("general.hint"), message.get("tree.forwardToQAHint"), dialog.WARNING);
	}
}


menuEventHandler.handleReassignToAuthor = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);
	
	if (selectedNode.item.nodeAppType == "O") {
		menuEventHandler._handleReassignObjectToAuthor(msg);
	} else if (selectedNode.item.nodeAppType == "A") {
		menuEventHandler._handleReassignAddressToAuthor(msg);
	}
}	


menuEventHandler._handleReassignObjectToAuthor = function(msg) {
	var valid = checkValidityOfInputElements();
	if (valid != "VALID") {
		displayErrorMessage(valid);
		console.debug("input invalid: "+valid);
		return;
	}

	// Forward the current object to the Author.
	//var nodeData = udkDataProxy._getData();
	var deferred = new dojo.Deferred();
	deferred.addErrback(displayErrorMessage);

	// Show a dialog to query the user before reassigning
	dialog.show(message.get("dialog.reassignToAuthorTitle"), message.get("dialog.object.reassignToAuthorMessage"), dialog.INFO, [
    	{ caption: message.get("general.no"),  action: function() { deferred.callback(); } },
    	{ caption: message.get("general.yes"), action: function() {
				console.debug("Publishing event: /forwardObjectToAuthorRequest");
				dojo.publish("/forwardObjectToAuthorRequest", [{resultHandler: deferred}]);
    		}
    	}
	]);
}

menuEventHandler._handleReassignAddressToAuthor = function(msg) {
	var valid = checkValidityOfAddressInputElements();
	if (valid != "VALID") {
		displayErrorMessage(new Error(valid));
		console.debug("input invalid: "+valid);
		return;
	}

	var nodeData = udkDataProxy._getData();
	var deferred = new dojo.Deferred();
	deferred.addErrback(displayErrorMessage);

	// Show a dialog to query the user before forwarding
	dialog.show(message.get("dialog.reassignToAuthorTitle"), message.get("dialog.address.reassignToAuthorMessage"), dialog.INFO, [
    	{ caption: message.get("general.no"),  action: function() { deferred.callback(); } },
    	{ caption: message.get("general.yes"), action: function() {
				console.debug("Publishing event: /forwardAddressToAuthorRequest");
				dojo.publish("/forwardAddressToAuthorRequest", [{resultHandler: deferred}]);		
    		}
    	}
	]);
}


menuEventHandler.handleMarkDeleted = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);

	if (selectedNode.id == "newNode") {
		menuEventHandler.handleDiscard(msg);
		return;
	}

	if (!selectedNode.item.isPublished) {
		menuEventHandler.handleDelete(msg);
		return;
	}

	if (!selectedNode || selectedNode.id == "objectRoot" || selectedNode.id == "addressRoot" || selectedNode.id == "addressFreeRoot") {
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
				// marked as deleted
				var tree = dijit.byId("dataTree");
				// TODO: !!! determine selected node here
				if (tree.selectedNode == selectedNode) {
					// If the current node was marked as deleted, reload the current node (updates permissions, etc.)
					var newSelectNode = selectedNode;
					var treeListener = dijit.byId("treeListener");

					var d = new dojo.Deferred();
					d.addCallback(function(){				
						//tree.selectNode(newSelectNode);
						UtilTree.selectNode("dataTree", newSelectNode.id[0]);
						//tree.selectedNode = newSelectNode;
						dojo.publish("/selectNode", [{node: newSelectNode.item}]);
						if (!dojo.isIE)				
							dojo.window.scrollIntoView(newSelectNode.domNode);
					});
					d.addErrback(function(msg){
						dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
						console.debug(msg);
					});

					udkDataProxy.resetDirtyFlag();
		    		console.debug("Publishing event: /loadRequest("+newSelectNode.id+", "+newSelectNode.item.nodeAppType+")");
			    	dojo.publish("/loadRequest", [{id: newSelectNode.id[0], appType: newSelectNode.item.nodeAppType[0], resultHandler:d}]);

				} else {
					// Otherwise update the node that was marked as deleted
					// The nodeDocType and permissions have to be updated
					if (selectedNode.item.nodeDocType.search(/_BV|_RV/) != -1) {
						// If the nodeDocType ends with _BV or _RV, replace it with _QV
						selectedNode.item.nodeDocType = selectedNode.nodeDocType.replace(/_BV|_RV/, "_QV");
					} else {
						// else add _QV to the end of the string
						selectedNode.item.nodeDocType = selectedNode.nodeDocType + "_QV";
					}
					//dijit.byId("treeDocIcons").setnodeDocTypeClass(selectedNode);

					// update permissions
					selectedNode.item.userWriteSubTreePermission = selectedNode.item.userWriteTreePermission;
					selectedNode.item.userWritePermission = false;
                    selectedNode.userMovePermission = false;
					selectedNode.item.userWriteSinglePermission = false;
					selectedNode.item.userWriteTreePermission = false;
                    selectedNode.userWriteSubNodePermission = false;
				}
	    	});
			deleteObjDef.addErrback(displayErrorMessage);

			// Tell the backend to delete the selected node.
	    	console.debug("Publishing event: /deleteRequest("+selectedNode.id+", "+selectedNode.item.nodeAppType+")");
	    	dojo.publish("/deleteRequest", [{id: selectedNode.id[0], resultHandler: deleteObjDef}]);				
		});

		// Build the message key (dialog.<object|address>.delete<Children>Message)
		var messageKey = "dialog.";
		if (selectedNode.item.nodeAppType == "O")
			messageKey += "object.markDelete";
		else
			messageKey += "address.markDelete";

		if (selectedNode.item.isFolder)
			messageKey += "Children";

		messageKey += "Message";
		var displayText = dojo.string.substitute(message.get(messageKey), [selectedNode.item.title]);

		dialog.show(message.get("general.delete"), displayText, dialog.INFO, [
                        { caption: message.get("general.cancel"), action: function() { deferred.errback(); } },
                        { caption: message.get("general.ok"),     action: function() { deferred.callback(); } }
		]);
	}
}


menuEventHandler.handleUnmarkDeleted = function(msg) {
	// Only available for the person in charge of QS.
	// Removes the flag which marks the obj/adr for deletion
	alertNotImplementedYet();
}


menuEventHandler.handleShowChanges = function(msg) {
	// Get the selected node from the message
	var selectedNode = getSelectedNode(msg);

	var params = {
		selectedNodeId: selectedNode.id[0]
	};

	if (selectedNode.item.nodeAppType == "O") {
		dialog.showPage(message.get("dialog.compareView.title"), "dialogs/mdek_compare_view_dialog.jsp", 755, 600, true, params);
	} else if (selectedNode.item.nodeAppType == "A") {
		dialog.showPage(message.get("dialog.compareView.title"), "dialogs/mdek_compare_view_address_dialog.jsp", 755, 600, true, params);
	}
}

menuEventHandler.handleShowComment = function() {
	dialog.showPage(message.get("dialog.showComments.title"), "dialogs/mdek_comments_dialog.jsp", 1010, 470, true);
/*
	var nodeId = prompt("Jump to node with uuid", "5CE671D3-5475-11D3-A172-08002B9A1D1D");
 	if (nodeId) {
 		menuEventHandler.handleSelectNodeInTree(nodeId);
 	}
*/
}

// Selects and loads a node in the tree. The path to the node is expanded step by step.
menuEventHandler.handleSelectNodeInTree = function(nodeId, nodeAppType) {
    igeMenuBar.selectChild("pageHierarchy");

	if (!UtilUI.isContainerNodeId(nodeId) && !UtilUI.isNewNodeId(nodeId)) {
		// Get the path to the node depending on its type
		var getPathDef = new dojo.Deferred();
		var rootNodes = ["root_dataTree"];
    	if (nodeAppType == "O") {
    		dojo.publish("/getObjectPathRequest", [{id: nodeId, resultHandler: getPathDef}]);
			rootNodes.push("objectRoot");		

		} else if (nodeAppType == "A" ){
    		dojo.publish("/getAddressPathRequest", [{id: nodeId, resultHandler: getPathDef}]);
			rootNodes.push("addressRoot");		
		}

		// Expand the nodes along the path.
		//getPathDef.addCallback(dojo.lang.curry(menuEventHandler, menuEventHandler._expandPath, nodeAppType));
		//getPathDef.addCallback(dojo.partial(menuEventHandler._expandPath, nodeAppType));
        getPathDef.addCallback(function(path){
            var defTree = new dojo.Deferred();
            menuEventHandler._waitForDataTree(10, defTree, path);
            return defTree;
        });
        
		getPathDef.addCallback(function(path) {
            var def = new dojo.Deferred();
            
            //dijit.byId("dataTree")._setPathAttr(rootNodes.concat(path)); // problem with deferred (knowing when finished)
            /*var nodes = rootNodes.concat(path);
            // remove first node (root)
            nodes.shift();
            dojo.forEach(nodes, function(node) {
                def.addCallback(function() {return dijit.byId("dataTree")._expandNode(dijit.byId(node));});
            });
            def.callback();
            
            return def;*/
           
            var def = dijit.byId("dataTree")._setPathAttr(rootNodes.concat(path));
            def.addCallback(dojo.partial(menuEventHandler._loadNode, nodeId));
            
		});
		
		/*getPathDef.addCallback(function() {
            return menuEventHandler._loadNode(nodeId); 
        });*/
	}
}

menuEventHandler._waitForNode = function(iterations, nodeId, def) {
    console.debug("wait for node");
    if (iterations > 0 && dijit.byId(nodeId) == undefined) {
        setTimeout(function(){
            menuEventHandler._waitForNode(iterations - 1, nodeId, def);
        }, 500);
    } else {
        def.callback();
    }
}

menuEventHandler._waitForDataTree = function(iterations, def, path) {
    console.debug("wait for datatree");
    if (iterations > 0 && (dijit.byId("dataTree") == undefined || !dijit.byId("dataTree").finishedCreate)) {
        setTimeout(function(){
            menuEventHandler._waitForDataTree(iterations - 1, def, path);
        }, 500);
    } else {
        def.callback(path);
    }

}

// Loads a node with id 'nodeId'
// The TreeNode with widgetId must exist before the node can be loaded
menuEventHandler._loadNode = function(nodeId) {
	var tree = dijit.byId("dataTree");
	var targetNode = dijit.byId(nodeId);
    var loadNodeDef = new dojo.Deferred();
	if (targetNode) {
		
		loadNodeDef.addCallback(function(){				
			UtilTree.selectNode("dataTree", nodeId);
			dojo.window.scrollIntoView(targetNode.domNode);
			dojo.publish("/selectNode", [{node: targetNode.item}]);
            dijit.byId("dataFormContainer").resize();
		});
		loadNodeDef.addErrback(function(msg){
			dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
			console.debug(msg);
		});

		console.debug("Publishing event: /loadRequest("+targetNode.id+", "+targetNode.item.nodeAppType+")");
		dojo.publish("/loadRequest", [{id: targetNode.id[0], appType: targetNode.item.nodeAppType[0], resultHandler:loadNodeDef}]);
	}
    return loadNodeDef;
}


menuEventHandler.switchLanguage = function() {
	document.location.href="start.jsp?lang="+dijit.byId("languageBox").get("value");
}

menuEventHandler.openCreateObjectWizardDialog = function() {
	dialog.showPage(message.get("dialog.wizard.selectTitle"), "dialogs/mdek_select_wizard_dialog.jsp", 350, 170, true);
}


// ------------------------- Helper functions -------------------------

function handleEntityReferencedException(err) {
	var addressTitle = UtilAddress.createAddressTitle(err.targetAddress);
	var objectTitles = "<br><br>";
	
	for (var i = 0; i < err.sourceObjects.length; ++i) {
		objectTitles += "- "+err.sourceObjects[i].title +"<br>";
	}
	objectTitles = dojo.trim(objectTitles);

	dialog.show(message.get("general.error"), dojo.string.substitute(message.get("operation.hint.addressReferenceHint"), [addressTitle, objectTitles]), dialog.WARNING, null, 320, 300);
}

function handleAddressNeverPublishedException(err) {
    var addresses = [];
    
    for (var i = 0; i < err.notPublishedAddresses.length; ++i) {
        addresses.push(err.notPublishedAddresses[i].organisation);
    }
    dialog.show(message.get("general.error"), dojo.string.substitute(message.get("operation.hint.addressNotPublishedHint"), [addresses.join(",")]), dialog.WARNING, null, 320, 300);
}

function alertNotImplementedYet()
{
  alert("Diese Funktionalit�t ist noch nicht implementiert.");
}

function getSelectedNode(msg) {
  // if it was called from a submenu after a click on the tree
  var fromContextMenu = false;
  if (dojo.isIE) {
      if (msg.srcElement.className.indexOf("dijitMenuItemLabel") != -1) {
        fromContextMenu = true;
      }
  } else {
      if (msg.target.classList.contains("dijitMenuItemLabel")) {
        fromContextMenu = true;
      }
  }
  if (fromContextMenu) {
    return dijit.byId("menuDataTree").clickedNode;
  } else { // or from the toolbar
 	if (dijit.byId("dataTree").selectedNode)
    	return dijit.byId("dataTree").selectedNode;
	else
		return null;
  }
}

function _createNewNode(obj, parentClass)
{
	var title;
	var objClass;

	if (obj.nodeAppType == "O") {
		title = message.get("tree.newNodeName");
	} else if (obj.nodeAppType == "A") {
		title = message.get("tree.newAddressName");
	}

	return {isFolder: obj.isFolder,
			nodeDocType: obj.nodeDocType,	// Initial display type of a new node
			title: title,
			//dojoType: 'ingrid:TreeNode',
			nodeAppType: obj.nodeAppType,
			userWritePermission: true,
            userMovePermission: true,
			id: obj.uuid};	// "newNode"
}

function _isChildOf(childNode, targetNode) {
	if (!childNode.getParent()) {
		return false; 
	} else if (childNode.getParent().id == targetNode.id) {
		return true;
	} else if (childNode.getParent().id == "objectRoot" || childNode.getParent().id == "addressRoot" || childNode.getParent().id == "addressFreeRoot") {
		return false;
	} else {
		return _isChildOf(childNode.getParent(), targetNode);
	}
}
