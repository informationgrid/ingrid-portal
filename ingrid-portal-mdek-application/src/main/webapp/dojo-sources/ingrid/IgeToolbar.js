/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/aspect",
    "dojo/topic",
    "dijit/registry",
    "dijit/Toolbar",
    "dijit/ToolbarSeparator",
    "dijit/form/Button",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/QA",
    "ingrid/utils/Security", 
    "ingrid/utils/UI", 
    "ingrid/MenuActions",
    "ingrid/IgeEvents",
    "ingrid/IgeActions",
    "ingrid/hierarchy/dirty",
    "ingrid/hierarchy/History"
], function(declare, lang, array, aspect, topic, registry, Toolbar, ToolbarSeparator, Button, message, dialog, UtilQA, UtilSecurity, UtilUI, MenuActions, igeEvents, igeActions, dirty, History) {
    return declare(null, {

        buttons: {},

        createToolbar: function(pane) {
            var isQAActive = UtilQA.isQAActive();
            var isUserQA = UtilSecurity.isCurrentUserQA();

            var toolbar = new Toolbar({
                id: 'myToolBar',
                style: "top: 0; left: 0;"
            }).placeAt(pane.domNode);
            var entries = [
                ["NewDoc", lang.hitch(MenuActions, MenuActions.handleNewEntity)],
                ["PrintDoc", lang.hitch(MenuActions, MenuActions.handlePreview)],
                ["ISO", function() {
                    dialog.showPage(message.get("dialog.xml.title"), 'dialogs/mdek_xml_dialog.jsp?c=' + userLocale, 800, 800, true, { uuid: currentUdk.uuid, type: currentUdk.nodeAppType } );
                }],
                ["Separator", null],
                ["Cut", lang.hitch(MenuActions, MenuActions.handleCut)],
                ["Copy", lang.hitch(MenuActions, MenuActions.handleCopyEntity)],
                ["CopySubTree", lang.hitch(MenuActions, MenuActions.handleCopyTree)],
                ["Paste", lang.hitch(MenuActions, MenuActions.handlePaste)],
                ["Separator", null],
                ["Save", lang.hitch(MenuActions, MenuActions.handleSave)],
                ["Undo", lang.hitch(MenuActions, MenuActions.handleUndo)],
                ["Discard", lang.hitch(MenuActions, MenuActions.handleDiscard)],
                ["Separator", null]
            ];

            // var reassignToAuthorButton = null;
            if (isQAActive && isUserQA)
                entries.push(["reassign", lang.hitch(MenuActions, MenuActions.handleReassignToAuthor)]);

            if (isQAActive && !isUserQA)
                entries.push(["assignToQa", lang.hitch(MenuActions, MenuActions.handleForwardToQA)]);
            else
                entries.push(["FinalSave", lang.hitch(MenuActions, MenuActions.handleFinalSave)]);

            if (isQAActive && !isUserQA)
                entries.push(["markDeleted", lang.hitch(MenuActions, MenuActions.handleMarkDeleted)]);
            else
                entries.push(["DelSubTree", lang.hitch(MenuActions, MenuActions.handleDelete)]);


            entries.push(["ShowChanges", lang.hitch(MenuActions, MenuActions.handleShowChanges)]);
            entries.push(["Separator", null]);
            entries.push(["ShowComments", lang.hitch(MenuActions, MenuActions.handleShowComment)]);
            
            entries.push(["Separator", null]);
            entries.push(["Previous", lang.hitch(History, History.goBack)]);
            entries.push(["Next", lang.hitch(History, History.goNext)]);

            var entriesRight = [
                ["Help",
                    function() {
                        window.open('mdek_help.jsp?lang=' + userLocale + '&hkey=hierarchy-maintenance-1#hierarchy-maintenance-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');
                    }
                ],
                ["Expand",
                    function() {
                        igeEvents.toggleFields();
                    }, "toggleFieldsBtn"
                ]
            ];

            this._createToolbarButtons(toolbar, entries, false);

            this._createToolbarButtons(toolbar, entriesRight, true);

            // add another button which is displayed when a validation error occured
            // when trying to save or publish
            toolbar.addChild(this._createErrorButton());

            this.addToolbarEvents(isQAActive, isUserQA);
        },

        _createErrorButton: function() {
            var errorButton = new Button({
                id: "bShowNextError",
                label: message.get("ui.toolbar.showErrorCaption"),
                style: "background-color:#F0A000;border: 1px solid black; visibility: hidden; float: right;"
            });
            errorButton.pos = 0;
            errorButton.onClick = function() {
                var id = this.invalidIds[this.pos++ % this.invalidIds.length];
                if (id instanceof Array) {
                    UtilUI.showNextError(id[0], id[1]);
                } else {
                    UtilUI.showNextError(id);
                }
            };
            return errorButton;
        },
        
        /**
         *
         * @param {Object} toolbar
         * @param {Object} entries
         * @param {Object} rightAligned
         */
        _createToolbarButtons: function(toolbar, entries, rightAligned) {
            var aligned = "left";
            if (rightAligned) {
                aligned = "right";
            }

            var self = this;
            array.forEach(entries, function(entry) {
                if (entry[0] == "Separator") {
                    toolbar.addChild(new ToolbarSeparator({
                        style: "float: " + aligned
                    }));
                } else {
                    var params = {};
                    params.label = message.get("ui.toolbar." + entry[0] + "Caption");
                    // note: should always specify a label, for accessibility reasons.
                    // Just set showLabel=false if you don't want it to be displayed normally
                    params.showLabel = false;
                    params.style = "float: " + aligned;
                    params.onClick = entry[1];
                    params.iconClass = "image18px tabIcon" + entry[0];
                    params.id = "toolbarBtn" + entry[0];
                    if (entry[2])
                        params.id = entry[2];

                    var button = new Button(params);
                    // remember button for later connection to events
                    self.buttons[entry[0]] = button;
                    toolbar.addChild(button);
                }
            });
        },

        /**
         *
         * @param {Object} isQAActive
         * @param {Object} isUserQA
         */
        addToolbarEvents: function(isQAActive, isUserQA) {
            // Modify button tooltips depending on whether the current node is marked deleted
            var self = this;
            if (isQAActive && isUserQA) {
                topic.subscribe("/selectNode", function(msg) {
                    if (msg.node && (message.id === undefined || message.id == "dataTree")) {
                        var markedDeleted = msg.node.isMarkedDeleted === true;
                        if (markedDeleted) {
                            self.buttons.FinalSave.domNode.setAttribute("title", message.get("ui.toolbar.discardDeleteCaption"));
                            self.buttons.DelSubTree.domNode.setAttribute("title", message.get("ui.toolbar.finalDeleteCaption"));
                        } else {
                            self.buttons.FinalSave.domNode.setAttribute("title", message.get("ui.toolbar.publishCaption"));
                            self.buttons.DelSubTree.domNode.setAttribute("title", message.get("ui.toolbar.deleteCaption"));
                        }
                    }
                });
            }

            // Show/hide toolbar buttons depending on the user rights
            topic.subscribe("/selectNode", function(message) {
                // do not handle if another tree was selected!
                if (message.id && message.id != "dataTree") return;

                var selectedNode = message.node;
                // Initially disable all buttons
                var i;
                for (i in self.buttons) {
                    self.buttons[i].set("disabled", true);
                }

                // always show help and expand button
                self.buttons.Help.set("disabled", false);

                var dataTree = registry.byId("dataTree");
                // if active loaded node has been reselected (as the only selected node!)
                if (!selectedNode &&
                    (dataTree.allFocusedNodes.length === 0 ||
                        (dataTree.allFocusedNodes.length === 1 && dataTree.selectedNode == dataTree.allFocusedNodes[0]))) {
                    selectedNode = dataTree.selectedNode ? dataTree.selectedNode.item : null;
                }

                if (dataTree.selectedItems.length === 1 && selectedNode) {
                    self._handleSingleSelection(selectedNode);
                } else {
                    self._handleMultiSelection();
                }
                
                // TODO: the selectNode-event is not called when clicking the root nodes!
                if (History.hasPrevious()) {
                    self.buttons.Previous.set("disabled", false);
                }
                if (History.hasNext()) {
                    self.buttons.Next.set("disabled", false);
                }
            });

            // The undo button depends on the dirty flag
            aspect.after(dirty, "setDirtyFlag", function() {
                self.buttons.Undo.set("disabled", false);
            });
            aspect.after(dirty, "resetDirtyFlag", function() {
                self.buttons.Undo.set("disabled", true);
            });

            // Initially disable all icons
            var i;
            for (i in this.buttons) {
                this.buttons[i].set("disabled", true);
            }
            this.buttons.Help.set("disabled", false);
            
            History.addPreviousButton( self.buttons.Previous );
            History.addNextButton( self.buttons.Next );
        },
        
        _handleMultiSelection: function() {
            var enableList = [];
            var buttons = this.buttons;

            var selectedNodes = registry.byId("dataTree").selectedItems;
            if (selectedNodes.length === 0) return;

            var containsRoot = array.some(selectedNodes, function(node) {
                if (node.id === "objectRoot" || node.id === "addressRoot" || node.id === "addressFreeRoot")
                    return true;
            });

            // return immediately if a root node has been selected (disable toolbar!)
            if (containsRoot) return;

            enableList = enableList.concat([buttons.Copy, buttons.Expand]);

            // If the node has children, enable the 'copy tree' button
            var atLeastOneNodeIsFolder = array.some(selectedNodes, function(node) {
                return node.isFolder;
            });
            if (atLeastOneNodeIsFolder) {
                enableList.push(buttons.CopySubTree);
            }

            // If the the user has move permission, he can move the node
            var allHaveMovePermission = dojo.every(selectedNodes, function(node) {
                return node.userMovePermission;
            });
            if (allHaveMovePermission) {
                // add delete Button here as well, because move permission means
                // write-tree, which is exactly the condition for hasDeletePermission
                enableList = enableList.concat([buttons.Cut, buttons.markDeleted]);

                // only if there's no node having children allow delete button to be active
                var selectionContainsChildren = array.some(selectedNodes, function(node) {
                    return node.isFolder;
                });
                if (igeOptions.deleteNonEmptyFolders || !selectionContainsChildren) {
                    enableList.push(buttons.DelSubTree);
                }

            }


            // enable all possible toolbar buttons
            array.forEach(enableList, function(item) {
                if (item) {
                    item.set("disabled", false);
                }
            });
        },

        _handleSingleSelection: function(node) {
            // var dataTree = registry.byId("dataTree");
            var hasWritePermission = node.userWritePermission + "";
            var hasMovePermission = node.userMovePermission + "";
            // var hasWriteSinglePermission = node.userWriteSinglePermission + "";
            var hasWriteTreePermission = node.userWriteTreePermission + "";
            var hasWriteSubNodePermission = node.userWriteSubNodePermission + "";
            var hasWriteSubTreePermission = node.userWriteSubTreePermission + "";
            var isPublished = node.isPublished + "";
            var canCreateRootNodes = UtilSecurity.canCreateRootNodes();
            //dojo.debug("User has write permission? "+hasWritePermission);

            var enableList = [];
            var buttons = this.buttons;

            // Build the enable list
            if (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot") {
                if (canCreateRootNodes) {
                    enableList.push(buttons.NewDoc);
                }

            } else if (node.id == "newNode") {
                enableList = enableList.concat([buttons.PrintDoc, buttons.Save, buttons.FinalSave, buttons.assignToQa, buttons.DelSubTree, buttons.ShowComments, buttons.Expand]);

            } else {
                // If a 'normal' node (obj/adr that is not root) [is] selected, always enable the following nodes
                enableList = enableList.concat([buttons.PrintDoc, buttons.Copy, buttons.ShowComments, buttons.Expand, buttons.ISO]);

                // Only show the compare view dialog if a published version exists. Otherwise there's nothing to compare to
                if (isPublished == "true") {
                    enableList.push(buttons.ShowChanges);
                }

                // If the node has children, enable the 'copy tree' button
                if (node.isFolder) {
                    enableList.push(buttons.CopySubTree);
                }
                // If the the user has write permission (single or tree), he can discard, save and publish nodes
                if (hasWritePermission == "true") {
                    enableList = enableList.concat([buttons.Save, buttons.FinalSave, buttons.assignToQa]);

                    // The discard button is only enabled if the user has write permission, a published version exists and an edited version exists
                    if (isPublished && node.nodeDocType.search(/_.V/) != -1) {
                        enableList.push(buttons.Discard);
                    }
                }
                // If the the user has move permission, he can move the node
                if (hasMovePermission == "true") {
                    // add delete Button here as well, because move permission means
                    // write-tree, which is exactly the condition for hasDeletePermission
                    enableList = enableList.concat([buttons.Cut, buttons.markDeleted]);
                    if (igeOptions.deleteNonEmptyFolders || !node.isFolder) {
                        enableList.push(buttons.DelSubTree);
                    }
                }
                // If the the user has write tree permission (tree), he can delete, move and create new nodes
                if (hasWriteTreePermission == "true") {
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
                if (node.nodeDocType.search(/_Q/) != -1) {
                    enableList.push(buttons.reassign);
                }
            }

            // The paste button depends on the current selection in treeController and the current selected node
            if (registry.byId("dataTree").canPaste(node)) {
                enableList.push(buttons.Paste);
            }

            // enable all possible toolbar buttons
            array.forEach(enableList, function(item) {
                if (item) {
                    item.set("disabled", false);
                }
            });
        }
    })();
});
