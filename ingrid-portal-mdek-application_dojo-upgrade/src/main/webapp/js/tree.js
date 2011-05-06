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
            label: message.get('tree.nodeDelete'),
            onClick: menuEventHandler.handleDelete
        }));
	}

	// connect the special menu for the root node
    dojo.connect(menuDataTree, "_openMyself", this, function(e) {
        // get a hold of, and log out, the tree node that was the source of this open event
        var tn = dijit.getEnclosingWidget(e.target);
		menuDataTree.clickedNode = tn;
        
        console.debug("check node:");
        console.debug(tn.item);
		
        var enablePaste = false;
		
		// allow/deny paste
		if (dijit.byId("dataTree").canPaste(tn.item))
			enablePaste = true;

		if (tn.item.id == "objectRoot" || tn.item.id == "addressRoot" || tn.item.id == "addressFreeRoot") {
			// contrived condition: if this tree node doesn't have any children, disable all of the menu items
            dojo.forEach(menuDataTree.getChildren(), function(i) {
				if (i.id == "menuItemNew" || i.id == "menuItemPaste" || 
						i.id == "menuItemReload" || i.id == "menuItemSeparator") {
					i.attr('class', "");
				} else {
					i.attr('class', "hidden");
				}
				if (i.id == "menuItemPaste") 
					enablePaste ?  i.setDisabled(false) : i.setDisabled(true);
            });
		} else { // for all child nodes of the objects and addresses
            dojo.forEach(menuDataTree.getChildren(), function(i) {
				if (i.id == "menuItemReload" || i.id == "menuItemSeparator") {
					i.attr('class', "hidden");
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
				
            });
		}
        /*
        // collect all items to disable
        // disable paste if not possible
      var actionsDisabled = [];
      if (dojo.lang.isObject(this.treeController) && !this.treeController.canPaste(node))
        actionsDisabled.push("menuItemPaste");

      if (node.nodeAppType == "A" && (node.objectClass == 2 || node.objectClass == 3) ) {
          actionsDisabled.push("menuItemNew");
      }

      if (node.id == "newNode") {
          actionsDisabled.push("MOVE");
          actionsDisabled.push("menuItemNew");
          actionsDisabled.push("menuItemCopy");
          actionsDisabled.push("menuItemCut");
          actionsDisabled.push("menuItemPaste");
      }

      if (!node.userWriteTreePermission && node.id != "objectRoot" && node.id != "addressRoot" && node.id != "addressFreeRoot") {
        actionsDisabled.push("MOVE");
        actionsDisabled.push("menuItemCut");
        actionsDisabled.push("DETACH");
            if (!node.userWriteSubTreePermission) {
                actionsDisabled.push("menuItemNew");
                actionsDisabled.push("menuItemPaste");
            }
      }

      if (!UtilSecurity.canCreateRootNodes() && (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot")) {
        actionsDisabled.push("menuItemNew");        
      }
      
      dojo.forEach(menuDataTree.getChildren(), function(item) {
          if (acionsDisabled.indexOf(item.id) != -1)
               item.setDisabled(true);
          else
               item.setDisabled(false);
      });
		*/
	});
	
	dojo.connect(dijit.byId("dataTree"), "onClick", ingridDataTreeHandler.onClick);
    
    // enable/disable inputs according to permission 
    igeEvents.disableInputOnWrongPermission();
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

ingridDataTreeHandler.onClick = function(item, node){
    // don't do anything if already selected node is clicked again
    //if (this.selectedNode.id == node.id)
    //    return;
    
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
		resultHandler: deferred
	}]);
    
	return deferred;
}

