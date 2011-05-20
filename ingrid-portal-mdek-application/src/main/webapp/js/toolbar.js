ingridToolbar = new Object();

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
			window.open('mdek_help.jsp?hkey=hierarchy-maintenance-1#hierarchy-maintenance-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');
		}],
		["Expand", function(){igeEvents.toggleFields();}, "toggleFieldsBtn"]
	];
	
	var buttons = this._createToolbarButtons(toolbar, entries, false);
    // add another button which is displayed when a validation error occured
    // when trying to save or publish
    toolbar.addChild(ingridToolbar._createErrorButton());
    
    this._createToolbarButtons(toolbar, entriesRight, true);
    
    this.addToolbarEvents(isQAActive, isUserQA, buttons);
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
    var buttons = {};
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
            buttons[entry[0]] = button;
			toolbar.addChild(button);
		}
    });
    
    return buttons;
}

/**
 * 
 * @param {Object} isQAActive
 * @param {Object} isUserQA
 */
ingridToolbar.addToolbarEvents = function(isQAActive, isUserQA, buttons){
    // Modify button tooltips depending on whether the current node is marked deleted
    if (isQAActive && isUserQA) {
        dojo.subscribe("/selectNode", function(msg){
            var markedDeleted = msg.node.isMarkedDeleted+"" == true;
            if (markedDeleted) {
                buttons.FinalSave.domNode.setAttribute("title", message.get("ui.toolbar.discardDeleteCaption"));
                buttons.DelSubTree.domNode.setAttribute("title", message.get("ui.toolbar.finalDeleteCaption"));
            }
            else {
                buttons.FinalSave.domNode.setAttribute("title", message.get("ui.toolbar.publishCaption"));
                buttons.DelSubTree.domNode.setAttribute("title", message.get("ui.toolbar.deleteCaption"));
            }
        });
    }
    
    // Show/hide toolbar buttons depending on the user rights
    dojo.subscribe("/selectNode", function(message) {
        var hasWritePermission = message.node.userWritePermission+"";
        var hasMovePermission = message.node.userMovePermission+"";
        var hasWriteSinglePermission = message.node.userWriteSinglePermission+"";
        var hasWriteTreePermission = message.node.userWriteTreePermission+"";
        var hasWriteSubNodePermission = message.node.userWriteSubNodePermission+"";
        var hasWriteSubTreePermission = message.node.userWriteSubTreePermission+"";
        var isPublished = message.node.isPublished+"";
        var canCreateRootNodes = UtilSecurity.canCreateRootNodes();
//      dojo.debug("User has write permission? "+hasWritePermission);

        var enableList = [];

        // Initially disable all buttons
        //dojo.forEach(buttonList, function(item) { if (item != null) { item.disable(); } });
        for (i in buttons) {
            buttons[i].set("disabled", true);
        }

        // Build the enable list
        if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
            if (canCreateRootNodes) {
                enableList.push(buttons.NewDoc);
            }

        } else if (message.node.id == "newNode") {
            enableList = enableList.concat([buttons.PrintDoc, buttons.Save, buttons.FinalSave, buttons.assignToQa, buttons.DelSubTree, buttons.ShowComments]);

        } else {
            // If a 'normal' node (obj/adr that is not root) is selected, always enable the following nodes
            enableList = enableList.concat([buttons.PrintDoc, buttons.Copy, buttons.ShowComments]);

            // Only show the compare view dialog if a published version exists. Otherwise there's nothing to compare to
            if (isPublished == "true") {
                enableList.push(buttons.ShowChanges);
            }

            // If the node has children, enable the 'copy tree' button
            if (message.node.isFolder[0]) {
                enableList.push(buttons.CopySubTree);
            }
            // If the the user has write permission (single or tree), he can discard, save and publish nodes
            if (hasWritePermission == "true") {
                enableList = enableList.concat([buttons.Save, buttons.FinalSave, buttons.assignToQa]);              

                // The discard button is only enabled if the user has write permission, a published version exists and an edited version exists
                if (isPublished && message.node.nodeDocType[0].search(/_.V/) != -1) {
                    enableList.push(buttons.Discard);
                }
            }
            // If the the user has move permission, he can move the node
            if (hasMovePermission == "true") {
                // add delete Button here as well, because move permission means
                // write-tree, which is exactly the condition for hasDeletePermission
                enableList = enableList.concat([buttons.DelSubTree, buttons.Cut]);
            }
            // If the the user has write tree permission (tree), he can delete, move and create new nodes
            if (hasWriteTreePermission == "true") {
                //enableList = enableList.concat([buttons.DelSubTree, buttons.Cut]);
                if (message.node.nodeAppType == "O") {
                    enableList.push(buttons.NewDoc);

                } else if (message.node.nodeAppType == "A" && message.node.objectClass != 2 && message.node.objectClass != 3) {
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
            if (message.node.nodeDocType[0].search(/_Q/) != -1) {
                enableList.push(buttons.reassign);
            }
        }

        // The paste button depends on the current selection in treeController and the current selected node
        if (dijit.byId("dataTree").canPaste(message.node)) {
            enableList.push(buttons.Paste);
        }

        dojo.forEach(enableList, function(item) { if (item != null) { item.set("disabled", false); } });
    });

    // The undo button depends on the dirty flag
    dojo.connect(udkDataProxy, "setDirtyFlagNow", function() {
        buttons.Undo.set("disabled", false);
    });
    dojo.connect(udkDataProxy, "resetDirtyFlag", function() {
        buttons.Undo.set("disabled", true);
    });


    var showOrHidePasteButton = function(node) {
        // The paste button depends on the current selection in treeController and the current selected node
        if (dijit.byId("dataTree").canPaste(node)) {
            buttons.Paste.set("disabled", false);
        } else {
            buttons.Paste.set("disabled", true);
        }       
    };
    //!!!dojo.connect(treeController, "move", showOrHidePasteButton);
    //dojo.connect(treeController, "prepareCopy", showOrHidePasteButton);
    dojo.subscribe("/prepareCopy", showOrHidePasteButton);
    //dojo.connect(treeController, "prepareCut", showOrHidePasteButton);
    dojo.subscribe("/prepareCut", showOrHidePasteButton);

    // Initially disable all icons
    for (i in buttons) {
        buttons[i].set("disabled", true);
    }
}
