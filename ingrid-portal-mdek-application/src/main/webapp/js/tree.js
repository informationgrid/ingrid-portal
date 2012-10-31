ingridDataTree = new Object();

/*********************************************************
 * OBJECT
 *********************************************************/

/*
 * Create the tree 
 */
ingridDataTree.createTree = function(){
	/////////////////////////////////////////////////////////////
	// create the context menu for the tree, which contains all
	// items. when clicking on a node it depends on the type, which
	// items will be shown
	// shown when right-cicking somewhere in the tree
	/////////////////////////////////////////////////////////////
	menuDataTree = new dijit.Menu({
		id: "menuDataTree",
    	targetNodeIds: ["dataTree"]
    });
	//iconClass: "dijitEditorIcon dijitEditorIconCut",
	menuDataTree.addChild(new dijit.MenuItem({
		id: "menuItemNew",
        label: message.get('tree.nodeNew'),
        onClick: menuEventHandler.handleNewEntity
    }));
	menuDataTree.addChild(new dijit.MenuItem({
        label: message.get('tree.nodePreview'),
        onClick: menuEventHandler.handlePreview
    }));
	menuDataTree.addChild(new dijit.MenuSeparator());
	menuDataTree.addChild(new dijit.MenuItem({
        id: "menuItemCut",
        label: message.get('tree.nodeCut'),
        onClick: menuEventHandler.handleCut
    }));
	menuDataTree.addChild(new dijit.MenuItem({
	    id: "menuItemCopySingle",
        label: message.get('tree.nodeCopySingle'),
        onClick: menuEventHandler.handleCopyEntity
    }));
	menuDataTree.addChild(new dijit.MenuItem({
        id: "menuItemCopy",
        label: message.get('tree.nodeCopy'),
        onClick: menuEventHandler.handleCopyTree
    }));
	menuDataTree.addChild(new dijit.MenuItem({
		id: "menuItemPaste",
        label: message.get('tree.nodePaste'),
        onClick: menuEventHandler.handlePaste
    }));
	menuDataTree.addChild(new dijit.MenuItem({
        label: message.get('tree.subReload'),
        onClick: menuEventHandler.reloadSubTree
    }));
	menuDataTree.addChild(new dijit.MenuSeparator({id: "menuItemSeparator"}));
	menuDataTree.addChild(new dijit.MenuItem({
		id: "menuItemReload",
        label: message.get('tree.reload'),
        onClick: menuEventHandler.reloadSubTree
    }));
	menuDataTree.addChild(new dijit.MenuSeparator());
	if (UtilQA.isQAActive() && !UtilSecurity.isCurrentUserQA()) {
		menuDataTree.addChild(new dijit.MenuItem({
            id: "menuItemDetach",
            label: message.get('tree.nodeMarkDeleted'),
            onClick: menuEventHandler.handleMarkDeleted
        }));
	} else {
		menuDataTree.addChild(new dijit.MenuItem({
		    id: "menuItemDelete",
            label: message.get('tree.nodeDelete'),
            onClick: menuEventHandler.handleDelete
        }));
	}
    menuDataTree.addChild(new dijit.MenuSeparator({id: "menuItemSeparatorPublicationCondition"}));
    menuDataTree.addChild(new dijit.MenuItem({
        id: "menuItemPublicationCondition1",
        label: message.get('tree.publicationInternet'),
        onClick: dojo.partial(menuEventHandler.changePublicationCondition, 1)
    }));
    menuDataTree.addChild(new dijit.MenuItem({
        id: "menuItemPublicationCondition2",
        label: message.get('tree.publicationIntranet'),
        onClick: dojo.partial(menuEventHandler.changePublicationCondition, 2)
    }));
    menuDataTree.addChild(new dijit.MenuItem({
        id: "menuItemPublicationCondition3",
        label: message.get('tree.publicationInternal'),
        onClick: dojo.partial(menuEventHandler.changePublicationCondition, 3)
    }));
    menuDataTree.addChild(new dijit.MenuItem({
        id: "menuItemInheritAddress",
        label: message.get('tree.inheritAddress'),
        onClick: menuEventHandler.inheritAddressToChildren
    }));

	// connect the special menu for the root node
    dojo.connect(menuDataTree, "_openMyself", this, function(e) {
        // get a hold of, and log out, the tree node that was the source of this open event
        var tn = dijit.getEnclosingWidget(e.target);
		menuDataTree.clickedNode = tn;
        
        var enablePaste = multiSelection = isAddressNode = false;
        var dataTree = dijit.byId("dataTree");
		
		// 1) keep multi selection if clicked on one selected node
        // 2) change selection to only clicked node if it isn't selected
        var clickedNodeIsSelected = dojo.some(dataTree.allFocusedNodes, function(node) { 
            return node.id == menuDataTree.clickedNode.id;
        });
        if (!clickedNodeIsSelected) {
            //dataTree.focusNode(menuDataTree.clickedNode);
            dataTree.resetSelection(menuDataTree.clickedNode);
            menuDataTree.clickedNode.setSelected(true);
        }
        
        // allow/deny paste
        if (dataTree.canPaste(tn.item))
            enablePaste = true;
        
		// menu for multiple selected nodes
		if (dataTree.allFocusedNodes.length > 1)
            multiSelection = true;
		
		if (!multiSelection && tn.item.nodeAppType == "A")
		    isAddressNode = true;
		
		if (tn.item.id == "objectRoot" || tn.item.id == "addressRoot" || tn.item.id == "addressFreeRoot") {
			// contrived condition: if this tree node doesn't have any children, disable all of the menu items
            dojo.forEach(menuDataTree.getChildren(), function(i) {
				if (i.id == "menuItemNew" || i.id == "menuItemPaste" || 
						i.id == "menuItemReload" || i.id == "menuItemSeparator") {
					i.attr('class', "");
					if (i.id != "menuItemSeparator") i.setDisabled(false);
				} else {
					i.attr('class', "hidden");
				}
				if (i.id == "menuItemPaste") 
					enablePaste ?  i.setDisabled(false) : i.setDisabled(true);
            });
		} else { // for all child nodes of the objects and addresses
	        
            dojo.forEach(menuDataTree.getChildren(), function(i) {
				if (i.id == "menuItemReload" || i.id == "menuItemSeparator" || (!isAddressNode && i.id == "menuItemInheritAddress")) {
					i.attr('class', "hidden");
					return;
				} else {
					i.attr('class', "");
				}
				
				//console.debug("menuitem id: " + i.id);
				if (i.isInstanceOf(dijit.MenuSeparator)) {
					return;
				}
				if (tn.item.nodeAppType+"" == "A" && (tn.item.objectClass+"" == 2 || tn.item.objectClass+"" == 3) ) {
					if (i.id == "menuItemNew")
						i.setDisabled(true);
					else
						i.setDisabled(false);
			    } else {
					i.setDisabled(false);
				}
				if (i.id == "menuItemPaste") 
					enablePaste ?  i.setDisabled(false) : i.setDisabled(true);
			    
                if (tn.item.userWriteTreePermission && (tn.item.userWriteTreePermission[0] == false)) {
                    if (i.id == "menuItemCut")
                        i.setDisabled(true);
                        
                    if (!tn.item.userWriteSubTreePermission[0] && !tn.item.userWriteSubNodePermission[0] && (i.id == "menuItemNew" || i.id == "menuItemPaste")) {
                        console.debug(tn.item);
                        i.setDisabled(true);
                    }
                }
                
                if (!tn.item.userMovePermission[0] && (i.id == "menuItemMove" || i.id == "menuItemCut" || i.id == "menuItemDetach")) {
                    i.setDisabled(true);
                }

                if (!isAddressNode) {
                    if (tn.item.publicationCondition && tn.item.publicationCondition[0]) {
                        if (i.id.indexOf("PublicationCondition") != -1) {
                            i.attr('class', "");
                            i.setDisabled(false);
                        }
                        if (i.id == "menuItemPublicationCondition" + tn.item.publicationCondition[0]) {
                            i.setDisabled(true);
                        }
                    } else {
                        if (i.id.indexOf("PublicationCondition") != -1) {
                            i.attr('class', "hidden");
                        }
                    }
                } else {
                    if (i.id.indexOf("PublicationCondition") != -1) {
                        i.attr('class', "hidden");
                    }
                }
                
                if (i.id == "menuItemInheritAddress" && tn.item.isFolder[0] != true) {
                    i.setDisabled(true);
                }
                
                // only enable these menu entries on multi selection
                if (multiSelection) {
                    if (i.id == "menuItemMove" || i.id == "menuItemCut" || i.id == "menuItemDelete" || 
                            i.id == "menuItemCopy" || i.id == "menuItemCopySingle" || i.id == "menuItemDetach") {
                        // do not modify state, since they already should have the right one!
                    } else {
                        // disable all other entries
                        i.setDisabled(true);
                    }
                }
            });
		}
	});
	
    var dataTree = dijit.byId("dataTree");
	dojo.connect(dataTree, "onClick",     ingridDataTreeHandler.onClick);
	dojo.connect(dataTree, "onMouseOver", ingridDataTreeHandler.onMouseOver);
	
	// enable multi-selection of tree nodes for group operations
	dataTree.multiSelect = true;
}

ingridDataTree.loadData = function(node, callback_function){
	var parentItem = node.item;  
	var store = this.tree.model.store; 
	
	var def = UtilTree.getSubTree(parentItem, 0);
	
	def.addCallback(function(data) {
		dojo.forEach(data, function(entry) {
			if (parentItem.root)
				store.newItem(entry);
			else
				store.newItem(entry, {parent: parentItem, attribute:"children"} );
		});
		// make changes permanent in memory -> checkpoint for reverts!
		store.save();
		callback_function();
	});
    def.addErrback(function(err) {
        console.debug("Tree node could not be expanded!");
        console.error(err);
        displayErrorMessage(err);
    });
	
}

ingridDataTreeHandler = new Object();
/******************** *************************************
 * HANDLER 
 *********************************************************/

ingridDataTreeHandler.onClick = function(item, node, oEvent){
    // don't do anything if already selected node is clicked again
    //if (this.selectedNode.id == node.id)
    //    return;
    
    // check for multiple selection mode (with CTRL-key)
    if (oEvent.ctrlKey === true) {
        
    } else {
        
    	var deferred = new dojo.Deferred();
    	console.debug("Publishing event: /loadRequest(" + item.id[0] + ", " + item.nodeAppType[0] + ")");
        
        deferred.addErrback(function(err) {
            displayErrorMessage(err);
        });
        deferred.addCallback(function(){
            console.debug("resize border container");
            dijit.byId("dataFormContainer").resize();
        });
        
    	dojo.publish("/loadRequest", [{
    		id: item.id[0],
    		appType: item.nodeAppType[0],
            node: item,
    		resultHandler: deferred
    	}]);
        
    	return deferred;
    }
}

ingridDataTreeHandler.onMouseOver = function(oEvent) {
    if (oEvent.ctrlKey === true) {
        this.domNode.style.cursor = "pointer";
    } else {
        this.domNode.style.cursor = "default";
    }
}


