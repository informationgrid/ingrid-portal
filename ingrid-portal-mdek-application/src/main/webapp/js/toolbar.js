ingridToolbar = new Object();

ingridToolbar.buttons = {};

dojo.require("dijit.Toolbar");

/*
 * The ingridToolbar-object creates and handles all events of the toolbar
 */


ingridToolbar.createToolbar = function(pane) {
    var isQAActive = UtilQA.isQAActive();
    var isUserQA = UtilSecurity.isCurrentUserQA();
    
	var toolbar = new dijit.Toolbar({id: 'myToolBar'}).placeAt(pane.domNode);
	var entries = [
		["NewDoc",       menuEventHandler.handleNewEntity],
		["PrintDoc",     menuEventHandler.handlePreview],
		["Separator",    null],
		["Cut",          menuEventHandler.handleCut],
		["Copy",         menuEventHandler.handleCopyEntity],
		["CopySubTree",  menuEventHandler.handleCopyTree],
		["Paste",        menuEventHandler.handlePaste],
		["Separator",    null],
		["Save",         menuEventHandler.handleSave],
		["Undo",         menuEventHandler.handleUndo],
		["Discard",      menuEventHandler.handleDiscard],
		["Separator",    null]
	];
    
    var reassignToAuthorButton = null;
    if (isQAActive && isUserQA)
        entries.push(["reassign", menuEventHandler.handleReassignToAuthor]);

    if (isQAActive && !isUserQA)
        entries.push(["assignToQa", menuEventHandler.handleForwardToQA]);
    else
        entries.push(["FinalSave", menuEventHandler.handleFinalSave]);

    if (isQAActive && !isUserQA)
        entries.push(["markDeleted", menuEventHandler.handleMarkDeleted]);
    else
        entries.push(["DelSubTree", menuEventHandler.handleDelete]);
        
        
    entries.push(["ShowChanges",  menuEventHandler.handleShowChanges]);
    entries.push(["Separator",    null]);
    entries.push(["ShowComments", menuEventHandler.handleShowComment]);
    //entries.push(["ShowNextError", menuEventHandler.handleShowComment]);
		
	var entriesRight = [
		["Help", function(){
			window.open('mdek_help.jsp?lang='+userLocale+'&hkey=hierarchy-maintenance-1#hierarchy-maintenance-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');
		}],
		["Expand", function(){igeEvents.toggleFields();}, "toggleFieldsBtn"]
	];
	
	this._createToolbarButtons(toolbar, entries, false);
    // add another button which is displayed when a validation error occured
    // when trying to save or publish
    toolbar.addChild(ingridToolbar._createErrorButton());
    
    this._createToolbarButtons(toolbar, entriesRight, true);
    
    this.addToolbarEvents(isQAActive, isUserQA);
}

ingridToolbar._createErrorButton = function() {
    var errorButton = new dijit.form.Button({id:"bShowNextError", label:"Show Next Error", style:"background-color:#B00000;border: 1px solid black;display:none;"});
    errorButton.pos = 0;
    errorButton.onClick = function(){
        UtilUI.showNextError(this.invalidIds[this.pos++%this.invalidIds.length]);
    }
    return errorButton;
}

/**
 * 
 * @param {Object} toolbar
 * @param {Object} entries
 * @param {Object} rightAligned
 */
ingridToolbar._createToolbarButtons = function(toolbar, entries, rightAligned) {
	var aligned = "left";
	if (rightAligned) {
		aligned = "right";
	}
	
	dojo.forEach(entries, function(entry) {
		if (entry[0] == "Separator") {
			toolbar.addChild(new dijit.ToolbarSeparator({style:"float: " + aligned}));
		} else {
			var params = {};
			params.label = message.get("ui.toolbar."+entry[0]+"Caption");
			// note: should always specify a label, for accessibility reasons.
			// Just set showLabel=false if you don't want it to be displayed normally
			params.showLabel = false;
			params.style = "float: " + aligned;
			params.onClick = entry[1];
			params.iconClass = "image18px tabIcon" + entry[0];
			if (entry[2])
				params.id = entry[2];
			
			var button = new dijit.form.Button(params);
            // remember button for later connection to events
			ingridToolbar.buttons[entry[0]] = button;
			toolbar.addChild(button);
		}
    });
}

/**
 * 
 * @param {Object} isQAActive
 * @param {Object} isUserQA
 */
ingridToolbar.addToolbarEvents = function(isQAActive, isUserQA){
    // Modify button tooltips depending on whether the current node is marked deleted
    if (isQAActive && isUserQA) {
        dojo.subscribe("/selectNode", function(msg){
            if (msg.node && (message.id == undefined || message.id == "dataTree")) {
                var markedDeleted = msg.node.isMarkedDeleted+"" == true;
                if (markedDeleted) {
                    ingridToolbar.buttons.FinalSave.domNode.setAttribute("title", message.get("ui.toolbar.discardDeleteCaption"));
                    ingridToolbar.buttons.DelSubTree.domNode.setAttribute("title", message.get("ui.toolbar.finalDeleteCaption"));
                }
                else {
                    ingridToolbar.buttons.FinalSave.domNode.setAttribute("title", message.get("ui.toolbar.publishCaption"));
                    ingridToolbar.buttons.DelSubTree.domNode.setAttribute("title", message.get("ui.toolbar.deleteCaption"));
                }
            }
        });
    }
    
    // Show/hide toolbar buttons depending on the user rights
    dojo.subscribe("/selectNode", function(message) {
        // do not handle if another tree was selected!
        if (message.id && message.id != "dataTree") return;
        
        var selectedNode = message.node;
        // Initially disable all buttons
        for (i in ingridToolbar.buttons) {
            ingridToolbar.buttons[i].set("disabled", true);
        }
        
        // always show help and expand button
        ingridToolbar.buttons.Help.set("disabled", false);
        
        var dataTree = dijit.byId("dataTree");
        // if active loaded node has been reselected (as the only selected node!)
        if (!selectedNode &&
             (dataTree.allFocusedNodes.length === 0 ||
             (dataTree.allFocusedNodes.length === 1 && dataTree.selectedNode == dataTree.allFocusedNodes[0]))) {
            selectedNode = dataTree.selectedNode ? dataTree.selectedNode.item : null;
        }
        
        if (selectedNode) {
            ingridToolbar._handleSingleSelection(selectedNode);
        } else {
            ingridToolbar._handleMultiSelection();
        }
    });
        
    // The undo button depends on the dirty flag
    dojo.connect(udkDataProxy, "setDirtyFlagNow", function() {
        ingridToolbar.buttons.Undo.set("disabled", false);
    });
    dojo.connect(udkDataProxy, "resetDirtyFlag", function() {
        ingridToolbar.buttons.Undo.set("disabled", true);
    });

    // Initially disable all icons
    for (i in ingridToolbar.buttons) {
        ingridToolbar.buttons[i].set("disabled", true);
    }
    ingridToolbar.buttons.Help.set("disabled", false);
}

ingridToolbar._handleMultiSelection = function() {
    var enableList = [];
    var buttons = ingridToolbar.buttons;
    
    var selectedNodes = dijit.byId("dataTree").getSelectedItems();
    if (selectedNodes.length == 0) return;
    
    var containsRoot = dojo.some(selectedNodes, function(node) {
        if (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot")
            return true;
    });
    
    // return immediately if a root node has been selected (disable toolbar!)
    if (containsRoot) return;
    
    enableList = enableList.concat([buttons.Copy, buttons.Expand]);
    
    // If the node has children, enable the 'copy tree' button
    var atLeastOneNodeIsFolder = dojo.some(selectedNodes, function(node) {return node.isFolder[0];});
    if (atLeastOneNodeIsFolder) {
        enableList.push(buttons.CopySubTree);
    }
    
    // If the the user has move permission, he can move the node
    var allHaveMovePermission = dojo.every(selectedNodes, function(node) {return node.userMovePermission[0];});
    if (allHaveMovePermission) {
        // add delete Button here as well, because move permission means
        // write-tree, which is exactly the condition for hasDeletePermission
        enableList = enableList.concat([buttons.DelSubTree, buttons.Cut, buttons.markDeleted]);
    }
    
    
    // enable all possible toolbar buttons
    dojo.forEach(enableList, function(item) { if (item != null) { item.set("disabled", false); } });
}

ingridToolbar._handleSingleSelection = function(node) {
    var dataTree = dijit.byId("dataTree");
    var hasWritePermission = node.userWritePermission+"";
    var hasMovePermission = node.userMovePermission+"";
    var hasWriteSinglePermission = node.userWriteSinglePermission+"";
    var hasWriteTreePermission = node.userWriteTreePermission+"";
    var hasWriteSubNodePermission = node.userWriteSubNodePermission+"";
    var hasWriteSubTreePermission = node.userWriteSubTreePermission+"";
    var isPublished = node.isPublished+"";
    var canCreateRootNodes = UtilSecurity.canCreateRootNodes();
    //dojo.debug("User has write permission? "+hasWritePermission);
    
    var enableList = [];
    var buttons = ingridToolbar.buttons;
    
    // Build the enable list
    if (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot") {
        if (canCreateRootNodes) {
            enableList.push(buttons.NewDoc);
        }
        // if at least one node is selected and none of them is a special node (objectRoot, addressRoot, addressFreeRoot)
        if (node.id != "objectRoot" && node.id != "addressRoot" && node.id != "addressFreeRoot") {
            enableList.push(buttons.DelSubTree);
        }
    
    } else if (node.id == "newNode") {
        enableList = enableList.concat([buttons.PrintDoc, buttons.Save, buttons.FinalSave, buttons.assignToQa, buttons.DelSubTree, buttons.ShowComments, buttons.Expand]);
    
    } else {
        // If a 'normal' node (obj/adr that is not root) is selected, always enable the following nodes
        enableList = enableList.concat([buttons.PrintDoc, buttons.Copy, buttons.ShowComments, buttons.Expand]);
    
        // Only show the compare view dialog if a published version exists. Otherwise there's nothing to compare to
        if (isPublished == "true") {
            enableList.push(buttons.ShowChanges);
        }
    
        // If the node has children, enable the 'copy tree' button
        if (node.isFolder[0]) {
            enableList.push(buttons.CopySubTree);
        }
        // If the the user has write permission (single or tree), he can discard, save and publish nodes
        if (hasWritePermission == "true") {
            enableList = enableList.concat([buttons.Save, buttons.FinalSave, buttons.assignToQa]);              
    
            // The discard button is only enabled if the user has write permission, a published version exists and an edited version exists
            if (isPublished && node.nodeDocType[0].search(/_.V/) != -1) {
                enableList.push(buttons.Discard);
            }
        }
        // If the the user has move permission, he can move the node
        if (hasMovePermission == "true") {
            // add delete Button here as well, because move permission means
            // write-tree, which is exactly the condition for hasDeletePermission
            enableList = enableList.concat([buttons.DelSubTree, buttons.Cut, buttons.markDeleted]);
        }
        // If the the user has write tree permission (tree), he can delete, move and create new nodes
        if (hasWriteTreePermission == "true") {
            //enableList = enableList.concat([buttons.DelSubTree, buttons.Cut]);
            if (node.nodeAppType == "O") {
                enableList.push(buttons.NewDoc);
    
            } else if (node.nodeAppType == "A" && node.objectClass != 2 && node.objectClass != 3) {
                // For addresses, the new entity button depends on the class of the selected node
                enableList.push(buttons.NewDoc);
            }
        }
        // If the the user has write tree permission (tree), but a node is assigned to QA, the user
        // is only allowed to create new subnodes -> hasWriteSubTreePermission
        // If the user can only create direct subnodes -> hasWriteSubNodePermission
        if (hasWriteSubTreePermission == "true" || hasWriteSubNodePermission == "true") {
            enableList.push(buttons.NewDoc);
        }
        
        // If the current node is assigned to the QA enable the reassign button
        if (node.nodeDocType[0].search(/_Q/) != -1) {
            enableList.push(buttons.reassign);
        }
    }
    
    // The paste button depends on the current selection in treeController and the current selected node
    if (dijit.byId("dataTree").canPaste(node)) {
        enableList.push(buttons.Paste);
    }
    
    // enable all possible toolbar buttons
    dojo.forEach(enableList, function(item) { if (item != null) { item.set("disabled", false); } });
}
