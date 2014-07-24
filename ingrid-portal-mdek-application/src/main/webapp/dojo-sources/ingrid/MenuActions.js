/*
 * Menu Event Handler. Static Methods
 */

/*jshint strict:false */

define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/topic",
    "dojo/dom",
    "dojo/Deferred",
    "dojo/window",
    "dojo/has",
    "dojo/cookie",
    "dojo/string",
    "dijit/registry",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/UI",
    "ingrid/utils/Tree",
    "ingrid/utils/Address",
    "ingrid/IgeActions",
    "ingrid/hierarchy/dirty",
    "ingrid/hierarchy/requiredChecks"
], function(declare, array, lang, topic, dom, Deferred, wnd, has, cookie, string, registry, message, dialog, UtilUI, UtilTree, UtilAddress, IgeActions, dirty, checks) {
    return declare(null, {

        COOKIE_HIDE_COPY_HINT: "ingrid.ige.copy.hint",

        global: this,

        // Singleton
        // menuEventHandler = new function MenuEventHandler() {}

        handleNewEntity: function(mes) {
            var deferred = new Deferred();
            var self = this;

            var selectedNode = this.getSelectedNode(mes);
            var uuid = selectedNode.item.id;

            if (!selectedNode) {
                dialog.show(message.get("general.hint"), message.get("tree.selectNodeHint"), dialog.WARNING);
            } else {
                if (uuid == "newNode") {
                    dialog.show(message.get("general.hint"), message.get("tree.selectNodeHint"), dialog.WARNING);

                } else if (selectedNode.item.nodeAppType == "O") {
                    // publish a createObject request and attach the newly created node if it was successful
                    deferred.then(function(res) {
                        self.attachNewNode(selectedNode, res);
                        selectedNode.setSelected(false);
                        // topic.publish("/selectNode", {
                        //     id: "dataTree",
                        //     node: res
                        // });
                        self.openCreateObjectWizardDialog();
                    }, function(err) {
                        displayErrorMessage(err);
                    });
                    console.debug("Publishing event: /createObjectRequest(" + uuid + ")");
                    topic.publish("/createObjectRequest", {
                        id: uuid,
                        resultHandler: deferred
                    });

                } else if (selectedNode.item.nodeAppType == "A") {
                    if (selectedNode.objectClass == 2 || selectedNode.objectClass == 3) {
                        // TODO: Show error message
                        return;
                    }

                    // Determine the class of the newly created Address.
                    // If the selected node if is 'addressFreeRoot' we can directly create the address without presenting the dialog
                    if (uuid == "addressFreeRoot") {
                        this._createNewAddress(3, selectedNode);
                    } else if (uuid == "addressRoot") {
                        this._createNewAddress(0, selectedNode);
                    } else {
                        var selectClassDef = new Deferred();
                        selectClassDef.then(function(addressClass) {
                            self._createNewAddress(addressClass, selectedNode);
                        });

                        var params = {
                            parentId: uuid,
                            parentClass: selectedNode.item.objectClass,
                            resultHandler: selectClassDef
                        };
                        dialog.showPage(message.get("dialog.createAddressTitle"), "dialogs/mdek_address_select_class_dialog.jsp?c=" + userLocale, 350, 160, false, params);
                    }
                }
            }
        },

        _createNewAddress: function(addressClass, parentNode) {
            //  console.debug("_createNewAddress("+addressClass+", "+parentNode.id+")");
            var parentId = parentNode.item.id;

            var self = this;
            if (addressClass == 3 && parentId == "addressRoot") {
                alert("This was not expected!");
                // var treeController = registry.byId("treeController");
                // treeController.expand(UtilTree.getNodeById("dataTree", "addressRoot"))
                // .then(function() {
                //     self._createNewAddress(addressClass, UtilTree.getNodeById("dataTree", "addressFreeRoot"));
                // });
                return;
            }

            var deferred = new Deferred();

            // publish a createNode request and attach the newly created node if it was successful
            deferred.then(function(res) {
                self.attachNewNode(UtilTree.getNodeById("dataTree", parentId), res);
                parentNode.setSelected(false);
                // topic.publish("/selectNode", {
                //     id: "dataTree",
                //     node: res
                // });
            }, function(err) {
                //      dialog.show(message.get('general.error'), message.get('tree.nodeCreateError'), dialog.WARNING);
                displayErrorMessage(err);
            });
            console.debug("Publishing event: /createAddressRequest(" + parentId + ")");
            topic.publish("/createAddressRequest", {
                id: parentId,
                addressClass: addressClass,
                resultHandler: deferred
            });
        },


        attachNewNode: function(selectedNode, res) {
            var tree = registry.byId("dataTree");
            var newNode = this._createNewNode(res, selectedNode.item.objectClass);

            // expand tree node if not already done!
            var def;
            var selNodeWidget = tree.selectedNode;
            if (selNodeWidget.isExpandable && !selNodeWidget.isExpanded) {
                def = selNodeWidget.expand();
            } else {
                def = new Deferred();
                def.resolve();
            }

            def.then(function() {
                UtilTree.addNode( "dataTree", selectedNode, newNode)
                .then(function(node) {
                    UtilTree.selectNode("dataTree", node.item.id, true);
                    topic.publish("/selectNode", {
                        id: "dataTree",
                        node: node.item
                    });
                    wnd.scrollIntoView(node.domNode);
                    registry.byId("dataFormContainer").resize();
                });
            });
        },

        handlePreview: function(msg) {
            var selectedNode = this.getSelectedNode(msg);
            var useDirtyData = false;

            // check if the preview was called via the context menu or directly via the menu button
            if (has("ie")) {
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
                console.debug("Show object preview.");
                dialog.showPage(message.get("dialog.object.detailView.title"), "dialogs/mdek_detail_view_dialog.jsp?c=" + userLocale, 755, 600, true, params);
            } else if (selectedNode.item.nodeAppType == "A") {
                console.debug("Show address preview.");
                dialog.showPage(message.get("dialog.address.detailView.title"), "dialogs/mdek_detail_view_address_dialog.jsp?c=" + userLocale, 755, 600, true, params);
            }
        },

        // no distinction between single and tree mode since only the whole tree can be cutted
        handleCut: function(mes) {
            var selectedNodes = this.getSelectedNodes(mes);
            var notAllowedSelection = array.filter(selectedNodes, function(node) {
                return node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot";
            });

            if (selectedNodes.length > 0 && notAllowedSelection.length > 0) {
                dialog.show(message.get("dialog.general.warning"), message.get("tree.selectNodeCopyHint"), dialog.WARNING);
            } else {
                var deferred = new Deferred();
                deferred.then(function() {
                    var tree = registry.byId("dataTree");
                    tree.prepareCut(tree.selectedItems);
                });

                deferred.resolve();
            }
        },


        handleCopyEntity: function(msg) {
            this._copyWithSubtree(msg, false);
        },

        handleCopyTree: function(msg) {
            this._copyWithSubtree(msg, true);
        },

        _copyWithSubtree: function(msg, withSubtree) {
            var selectedNodes = this.getSelectedNodes(msg);
            var notAllowedSelection = array.filter(selectedNodes, function(node) {
                return node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot";
            });

            if (selectedNodes.length > 0 && notAllowedSelection.length > 0) {
                dialog.show(message.get("dialog.general.warning"), message.get("tree.selectNodeCopyHint"), dialog.WARNING);
            } else {
                var deferred = new Deferred();
                var self = this;
                // show info box about copying and and giving new UUID (https://redmine.wemove.com/issues/131)
                // unless a cookie contains information to skip this message
                if (cookie(this.COOKIE_HIDE_COPY_HINT) !== "true") {
                    dialog.show(message.get("dialog.general.info"), message.get("tree.selectNodeCopyUuidHint"), dialog.INFO, [{
                        caption: message.get("general.ok.hide.next.time"),
                        type: "checkbox",
                        action: function(newValue) {
                            console.debug("cookie: " + newValue);
                            cookie(self.COOKIE_HIDE_COPY_HINT, newValue, {
                                expires: 730
                            });
                        }
                    }, {
                        caption: message.get("general.ok"),
                        action: function() {}
                    }]);
                }

                deferred.then(function() {
                    var tree = registry.byId("dataTree");
                    tree.prepareCopy(tree.selectedItems, withSubtree);
                }, function() {
                    dialog.show(message.get("general.error"), message.get("tree.nodeCanCopyError"), dialog.WARNING);
                });

                var nodeIds = [];
                array.forEach(selectedNodes, function(node) {
                    nodeIds.push(node.id);
                });

                if (selectedNodes[0].item.nodeAppType == "O") {
                    topic.publish("/canCopyObjectRequest", {
                        nodeIds: nodeIds,
                        copyTree: withSubtree,
                        resultHandler: deferred
                    });
                } else if (selectedNodes[0].item.nodeAppType == "A") {
                    topic.publish("/canCopyAddressRequest", {
                        nodeIds: nodeIds,
                        copyTree: withSubtree,
                        resultHandler: deferred
                    });
                }
            }
        },

        handlePaste: function(msg) {
            var targetNode = this.getSelectedNode(msg);
            var newNode = null;
            var deferred, def, appType, nodeIds;

            if (!targetNode || targetNode.item.id == "newNode") {
                dialog.show(message.get("general.hint"), message.get("tree.selectNodePasteHint"), dialog.WARNING);
            } else {
                var tree = registry.byId("dataTree");

                if (tree.nodesToCut !== null) {
                    var invalidPaste = array.some(tree.nodesToCut, function(nodeItem) {
                        return (targetNode.item == nodeItem || this._isChildOf(targetNode, nodeItem));
                    }, this);

                    // check if new node is going to be pasted
                    newNode = UtilTree.getNodeById("dataTree", "newNode");
                    if (newNode) {
                        var newNodeToBeCut = array.some(tree.nodesToCut, function(node) {
                            return node == newNode.item;
                        });
                        if (newNodeToBeCut) {
                            dialog.show(message.get("general.hint"), message.get("tree.saveNewNodeHint"), dialog.WARNING);
                            return;
                        }
                    }

                    if (invalidPaste) {
                        // If an invalid target is selected (same node or child of node to cut)
                        dialog.show(message.get("general.hint"), message.get("tree.nodePasteInvalidHint"), dialog.WARNING);
                        return;
                    } else {
                        //              var cutNodeWidget = registry.byId(tree.nodeToCut.id);
                        appType = tree.nodesToCut[0].nodeAppType;
                        // Valid target was selected. Start the request
                        deferred = new Deferred();
                        deferred.then(function() {
                            // Move was successful. Update the tree
                            array.forEach(tree.nodesToCut, function(nodeItem) {
                                UtilTree.deleteNode("dataTree", nodeItem);
                            });
                            //tree.model.store.save();
                            tree.refreshChildren(targetNode);
                            if (appType == "A") {
                                array.forEach(tree.nodesToCut, function(nodeToCut) {
                                    // If we moved an address from/to the freeAddress part of the tree, the icon has to be updated
                                    if (targetNode.item.id == "addressFreeRoot") {
                                        nodeToCut.nodeDocType = "PersonAddress";
                                        nodeToCut.objectClass = 3;
                                    } else {
                                        if (nodeToCut.nodeDocType.indexOf("PersonAddress") === 0) {
                                            nodeToCut.nodeDocType = "InstitutionPerson";
                                            nodeToCut.objectClass = 2;
                                        }
                                    }
                                });
                            }
                            tree.doPaste();
                            // Error Handler. Move was unsuccessful. Notify user and do nothing.
                        }, displayErrorMessage);

                        // Open the target node before moving a node. If the targetNode would be expanded afterwards,
                        // a widget collision would be possible (nodeToCut already exists in the target after expand)
                        def = tree._expandNode(targetNode);

                        nodeIds = [];
                        var parentNodeIds = [];
                        array.forEach(tree.nodesToCut, function(nodeItem) {
                            nodeIds.push(nodeItem.id);
                            var parentId = UtilTree.getNodeById("dataTree", nodeItem.id).getParent().item.id;
                            if (parentId == "objectRoot" || parentId == "addressRoot" || parentId == "addressFreeRoot")
                                parentNodeIds.push(null);
                            else
                                parentNodeIds.push(parentId);
                        });

                        if (appType == "O") {
                            def.then(function() {
                                topic.publish("/cutObjectRequest", {
                                    srcIds: nodeIds,
                                    parentUuids: parentNodeIds,
                                    dstId: targetNode.item.id,
                                    forcePublicationCondition: false,
                                    resultHandler: deferred
                                });
                            });
                        } else if (appType == "A") {
                            def.then(function() {
                                topic.publish("/cutAddressRequest", {
                                    srcIds: nodeIds,
                                    parentUuids: parentNodeIds,
                                    dstId: targetNode.item.id,
                                    resultHandler: deferred
                                });
                            });
                        }
                    }
                } else if (tree.nodesToCopy !== null) {
                    // If a newNode currently exists and is included in the copy operation abort the copy operation with
                    // an error message
                    newNode = UtilTree.getNodeById("dataTree", "newNode");
                    if (newNode) {
                        if (tree.copySubTree) {
                            var parentOfNewNode = array.some(tree.nodesToCopy, function(nodeItem) {
                                return this._isChildOf(newNode, nodeItem);
                            });
                            if (parentOfNewNode) {
                                dialog.show(message.get("general.hint"), message.get("tree.saveNewNodeHint"), dialog.WARNING);
                                return;
                            }
                        }

                        var newNodeToBeCopied = array.some(tree.nodesToCopy, function(nodeItem) {
                            return nodeItem == newNode.item;
                        });
                        if (newNodeToBeCopied) {
                            dialog.show(message.get("general.hint"), message.get("tree.saveNewNodeHint"), dialog.WARNING);
                            return;
                        }
                    }

                    // A node can be inserted everywhere. Start the paste request.
                    deferred = new Deferred();
                    deferred.then(function() {
                        // Copy was successful. Update the tree.
                        tree.refreshChildren(targetNode);
                        tree.doPaste();
                        // If copy was unsuccessful notify user and do nothing.
                    }, displayErrorMessage);

                    // Open the target node before copying a node. 
                    def = tree._expandNode(targetNode);
                    appType = tree.nodesToCopy[0].nodeAppType;
                    nodeIds = [];
                    array.forEach(tree.nodesToCopy, function(node) {
                        nodeIds.push(node.id);
                    });
                    if (appType == "O") {
                        def.then(function() {
                            topic.publish("/copyObjectRequest", {
                                srcIds: nodeIds,
                                dstId: targetNode.item.id,
                                copyTree: tree.copySubTree,
                                resultHandler: deferred
                            });
                        });

                    } else if (appType == "A") {
                        def.then(function() {
                            topic.publish("/copyAddressRequest", {
                                srcIds: nodeIds,
                                dstId: targetNode.item.id,
                                copyTree: tree.copySubTree,
                                resultHandler: deferred
                            });
                        });
                    }
                } else {
                    dialog.show(message.get("general.hint"), message.get("tree.nodePasteNoCutCopyHint"), dialog.WARNING);
                }
            }
        },

        handleSave: function() {
            var deferred = new Deferred();
            deferred.then(function() {
                var tree = registry.byId("dataTree");

                //tree.selectedNode = registry.byId(res.uuid);

                // manually publish select event, because during save operation there
                // is no tree-select-event!
                topic.publish("/selectNode", {
                    id: "dataTree",
                    node: tree.selectedNode.item
                });

                checks.resetRequiredFields();
            }, function(err) {
                if (err.message != "undefined") {
                    displayErrorMessage(err);
                }
            });

            console.debug("Publishing event: /saveRequest");
            topic.publish("/saveRequest", {
                resultHandler: deferred
            });
        },

        handleUndo: function(mes) {
            var selectedNode = this.getSelectedNode(mes);
            var uuid = selectedNode.item.id;

            if (uuid == "newNode") {
                this.handleDiscard(mes);
                return;
            }

            if (!selectedNode || uuid == "objectRoot") {
                dialog.show(message.get("general.hint"), message.get("tree.selectNodeCutHint"), dialog.WARNING);
            } else {
                var deferred = new Deferred();
                deferred.then(function() {
                    var def = new Deferred();
                    def.then(null, function(msg) {
                        dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
                        console.debug(msg);
                    });
                    dirty.resetDirtyFlag();
                    console.debug("Publishing event: /loadRequest(" + uuid + ", " + selectedNode.item.nodeAppType + ")");
                    topic.publish("/loadRequest", {
                        id: uuid,
                        appType: selectedNode.item.nodeAppType,
                        node: selectedNode.item,
                        resultHandler: def
                    });
                });
                //      dialog.showPage(message.get("dialog.undoChangesTitle"), "mdek_delete_working_copy_dialog.html", 342, 220, true, {resultHandler:deferred, action:"UNDO"});
                var displayText = "";
                if (selectedNode.item.nodeAppType == "O")
                    displayText = message.get("dialog.object.undoChangesMessage");
                else
                    displayText = message.get("dialog.address.undoChangesMessage");

                dialog.show(message.get("dialog.undoChangesTitle"), displayText, dialog.INFO, [{
                    caption: message.get("general.cancel"),
                    action: function() {
                        deferred.reject();
                    }
                }, {
                    caption: message.get("general.yes"),
                    action: function() {
                        deferred.resolve();
                    }
                }]);
            }
        },


        handleDiscard: function(msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);
            this._discardNode(selectedNode);
        },

        _discardNode: function(selectedNode) {
            var discardDeferred = new Deferred();
            var uuid = selectedNode.item.id;
            var self = this;

            if (!selectedNode || uuid == "objectRoot") {
                dialog.show(message.get("general.hint"), message.get("tree.selectNodeDeleteHint"), dialog.WARNING);
            } else {
                // If a selected node was found do the following:
                // 1. Query the user if he really wants to delete the working copy of the selected object
                //    This is accomplished by creating a deferred obj 'deferred' and passing it to the
                //    delete_working_copy dialog. If the user clicks yes, the attached callback function is executed.
                // 2. The attached callback function publishes a delete working copy request which is picked up by the
                //    udkDataProxy and sent to the backend. We need another deferred obj 'deleteObjDef' for this
                //    so we can see if the delete operation was successful.
                var deferred = new Deferred();
                deferred.then(function() {
                    var deleteObjDef = new Deferred();
                    deleteObjDef.then(function(res) {
                        // This function is called when the user has selected yes and the working copy was successfully
                        // deleted from the database
                        // The result 'res' is either:
                        // null - indicating that no other version of the object existed. In this case the object
                        //        has to be removed from the tree
                        // a valid MdekDataBean - indicating that the working copy has been deleted, but another
                        //        version of the object still exists. In this case the object has already been loaded
                        //        by the dataProxy. 
                        // The node has to be deleted from the tree
                        var tree = registry.byId("dataTree");
                        if (!res) {
                            // save store before removing anything ... needed for new nodes
                            // that are not included in store yet
                            //tree.model.store.save();
                            if (tree.selectedNode == selectedNode || self._isChildOf(tree.selectedNode, selectedNode)) {
                                // If the currently selected Node is a child of the deleted node, we select it's parent after deletion
                                var newSelectNode = selectedNode.getParent();
                                UtilTree.deleteNode("dataTree", uuid);

                                var d = new Deferred();
                                d.then(function() {
                                    UtilTree.selectNode("dataTree", newSelectNode.item.id);
                                    topic.publish("/selectNode", {
                                        id: "dataTree",
                                        node: newSelectNode.item
                                    });
                                    wnd.scrollIntoView(newSelectNode.domNode);
                                    discardDeferred.resolve();
                                }, function(msg) {
                                    dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
                                    console.debug(msg);
                                });

                                // We also have to reset the dirty flag since the 'dirty' ndoe is deleted anyway
                                dirty.resetDirtyFlag();
                                console.debug("Publishing event: /loadRequest(" + newSelectNode.item.id + ", " + newSelectNode.item.nodeAppType + ")");
                                topic.publish("/loadRequest", {
                                    id: newSelectNode.item.id,
                                    appType: newSelectNode.item.nodeAppType,
                                    node: newSelectNode.item,
                                    resultHandler: d
                                });
                            } else {
                                // The selection does not have to be altered. Delete the node.
                                UtilTree.deleteNode("dataTree", uuid);
                            }
                        } else {
                            // Another version of the node still exists. Just update the dirty flag(?).
                        }
                        // save again to be able to create a node with same id
                        // otherwise object is still present in store (_pending->deletedItems)
                        //tree.model.store.save();
                    }, displayErrorMessage);

                    // Tell the backend to delete the selected node.
                    console.debug("Publishing event: /deleteWorkingCopyRequest(" + uuid + ", " + selectedNode.item.nodeAppType + ")");
                    topic.publish("/deleteWorkingCopyRequest", {
                        id: uuid,
                        resultHandler: deleteObjDef
                    });
                });

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
                if (selectedNode.item.isPublished) {
                    titleText = message.get("dialog.discardPubExistTitle");
                    messageKey += "discardPubExistMessage";

                } else {
                    titleText = message.get("dialog.discardPubNotExistTitle");
                    messageKey += "discardPubNotExistMessage";
                }
                displayText = string.substitute(message.get(messageKey), [selectedNode.item.title]);

                dialog.show(titleText, displayText, dialog.INFO, [{
                    caption: message.get("general.cancel"),
                    action: function() {}
                }, {
                    caption: message.get("general.yes"),
                    action: function() {
                        deferred.resolve();
                    }
                }]);
            }
            return discardDeferred;
        },


        handleDelete: function(msg) {
            var deferred = new Deferred();
            // Get the selected nodes from the message
            var selectedNodes = this.getSelectedNodes(msg);

            var selectedNonNewNodes = [];
            var self = this;
            // start the execution immediately
            deferred.resolve();
            array.forEach(selectedNodes, function(node) {
                if (node.item.id == "newNode") {
                    deferred = deferred.then(lang.partial(self._discardNode, node));
                } else {
                    selectedNonNewNodes.push(node);
                }
            });
            if (selectedNonNewNodes.length > 0) {
                deferred = deferred.then(lang.hitch(this, lang.partial(this._deleteNodes, selectedNonNewNodes)));
            }
            return deferred;
        },

        _deleteNodes: function(selectedNonNewNodes) {
            var deleteDeferred = new Deferred();
            var self = this;

            var notAllowedSelection = array.filter(selectedNonNewNodes, function(node) {
                return node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot";
            });

            if (notAllowedSelection.length > 0) {
                dialog.show(message.get("dialog.general.warning"), message.get("tree.selectNodeDeleteHint"), dialog.WARNING);
                //allDeferred.reject();
            } else {
                // If a selected node was found do the following:
                // 1. Query the user if he really wants to delete the selected object
                //    This is accomplished by creating a deferred obj 'deferred' and passing it to the
                //    delete_object dialog. If the user clicks yes, the attached callback function is executed.
                // 2. The attached callback function publishes a delete request which is picked up by the
                //    udkDataProxy and sent to the backend. We need another deferred obj 'deleteObjDef' for this
                //    so we can see if the delete operation was successful.
                var deleteFunction = function(nodeToDelete) {
                    var deleteObjDef = new Deferred();
                    // the node could have been deleted already if parent node was deleted before (multi selection!)
                    // in IE8 only widgetId is valid (id is not correctly set)!!!
                    if (UtilTree.getNodeById("dataTree", nodeToDelete.item.id)) {
                        deleteObjDef.then(function() {
                            // This function is called when the user has selected yes and the node was successfully
                            // deleted from the database
                            // var tree = registry.byId("dataTree");

                            // Otherwise we just delete the node
                            UtilTree.deleteNode("dataTree", nodeToDelete.item);

                        }, displayErrorMessage);

                        // Tell the backend to delete the selected node.
                        console.debug("Publishing event: /deleteRequest(" + nodeToDelete.item.id + ", " + nodeToDelete.item.nodeAppType + ")");

                        topic.publish("/deleteRequest", {
                            id: nodeToDelete.item.id,
                            resultHandler: deleteObjDef
                        });
                    } else {
                        deleteObjDef.resolve();
                    }
                    return deleteObjDef.promise;
                };

                // a function to jump to the root object/address if currently loaded 
                // node has been deleted
                var loadParentFunction = function() {

                    // if currently loaded node does not exist anymore
                    // -> switch to root node
                    var currentNode = UtilTree.getNodeById("dataTree", self.global.currentUdk.uuid);
                    if (currentNode && dom.byId(currentNode.id)) {
                        UtilTree.selectNode("dataTree", currentNode.item.id);

                    } else {
                        // check if the selected node is still on the tree
                        //UtilTree.getNodeById("dataTree", self.currentUdk.uuid);
                        var selectedNode = registry.byId("dataTree").selectedNode;
                        if (!selectedNode || !dom.byId( selectedNode.id )) {
                        //if (self.currentUdk.uuid && !UtilTree.getNodeById("dataTree", self.currentUdk.uuid)) {
                            var root = null;
                            if (self.global.currentUdk.nodeAppType === "O") {
                                root = UtilTree.getNodeById("dataTree", "objectRoot");
                            } else {
                                root = UtilTree.getNodeById("dataTree", "addressRoot");
                            }
                            UtilTree.selectNode("dataTree", root.item.id);
                            topic.publish("/selectNode", {
                                id: "dataTree",
                                node: root.item
                            });
                            wnd.scrollIntoView(root.domNode);
                        }
                        
                    }

                    deleteDeferred.resolve();
                };

                // 'schedule' all nodes for deletion
                var deferred = new Deferred();
                var firstDeferred = deferred;
                array.forEach(selectedNonNewNodes, function(node) {
                    // then returns a new promise otherwise the last function "loadParentFunction" won't be 
                    // loaded in the end but in between!
                    deferred = deferred.then(lang.partial(deleteFunction, node));
                });

                // check if parent from loaded node was deleted
                // -> load the parent instead!
                deferred.then(loadParentFunction);

                // Build the message key (dialog.<object|address>.delete<Children>Message)
                var messageKey = "dialog.";
                if (selectedNonNewNodes[0].item.nodeAppType == "O")
                    messageKey += "object.delete";
                else
                    messageKey += "address.delete";

                var selectionContainsFolder = array.some(selectedNonNewNodes, function(node) {
                    return node.item.isFolder;
                });
                if (selectionContainsFolder)
                    messageKey += "Children";

                messageKey += "Message";

                var nodeTitles = "<ul>";
                array.forEach(selectedNonNewNodes, function(node) {
                    nodeTitles += "<li>" + node.item.title + "</li>";
                });
                nodeTitles += "</ul>";
                var displayText = string.substitute(message.get(messageKey), [nodeTitles]);

                dialog.show(message.get("general.delete"), displayText, dialog.INFO, [{
                    caption: message.get("general.cancel"),
                    action: function() { /*deferred.reject();*/ }
                }, {
                    caption: message.get("general.ok"),
                    action: function() {
                        firstDeferred.resolve();
                    }
                }]);
            }
            return deleteDeferred;
        },

        changePublicationCondition: function(newPubCondition, msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);
            var uuid = selectedNode.item.id;

            console.debug("changePublicationCondition to " + newPubCondition);
            console.debug(selectedNode);

            if (!selectedNode || uuid == "objectRoot" || selectedNode == "addressRoot" || selectedNode == "addressFreeRoot") {
                dialog.show(message.get("general.hint"), message.get("tree.selectNodePubConditionHint"), dialog.WARNING);
            } else {
                // If a selected node was found do the following:
                // 1. Publish a change request which is picked up by the
                //    udkDataProxy and sent to the backend. We need a deferred obj 'changeObjDef' for this
                //    so we can see if the change operation was successful.

                var self = this;
                var changeObjDef = new Deferred();
                changeObjDef.then(function(res) {
                    console.debug("callback after /changePublicationCondition");
                    // This function is called when the node was successfully changed
                    var tree = registry.byId("dataTree");
                    var treeSelectedNode;
                    if (tree.selectedNode) {
                        // fetch again to avoid null domNode !
                        treeSelectedNode = tree.selectedNode;
                    }
                    console.debug("fetched treeSelectedNode");
                    console.debug(treeSelectedNode);

                    // we update the tree after all functionality is done via triggering this deferred !
                    var defUpdateTree = new Deferred();
                    defUpdateTree.then(function() {
                        // update tree data but do NOT select node, to avoid display of right content if not initialized yet ! 
                        console.debug("_updateTree on right clicked tree node, NO \"/selectNode\" event should occur!");
                        IgeActions._updateTree(res, null);

                        if (selectedNode.isExpanded) {
                            console.debug("refreshChildren");
                            tree.refreshChildren(selectedNode);
                        }
                    }, displayErrorMessage);

                    // reload the current displayed node (treeSelectedNode) if it is the one changed or it is a subnode of the one
                    // changed (maybe also applied to subnodes !)
                    if (treeSelectedNode == selectedNode || (treeSelectedNode && self._isChildOf(treeSelectedNode, selectedNode))) {
                        // do NOT reset dirty flag, if the node was edited, ask user to save before reload !
                        //                udkDataProxy.resetDirtyFlag();

                        console.debug("Publishing event: /loadRequest(" + treeSelectedNode.item.id + ", " + treeSelectedNode.item.nodeAppType + ")");
                        topic.publish("/loadRequest", {
                            id: treeSelectedNode.item.id,
                            appType: treeSelectedNode.item.nodeAppType,
                            node: treeSelectedNode.item,
                            resultHandler: defUpdateTree
                        });
                    } else {
                        // if no reload of displayed node necessary, just update the according node in tree and its children !
                        defUpdateTree.resolve();
                    }

                }, displayErrorMessage);

                // Tell the backend to change the selected node.
                var forcePubCondition = false;
                var useWorkingCopy = ("V" !== selectedNode.item.workState);
                console.debug("Publishing event: /changePublicationCondition(" + selectedNode.item.id + ", useWorkingCopy=" + useWorkingCopy + ", newPubCondition=" + newPubCondition + ", forcePubCondition=" + forcePubCondition + ")");
                topic.publish("/changePublicationCondition", {
                    id: selectedNode.item.id,
                    useWorkingCopy: useWorkingCopy,
                    publicationCondition: newPubCondition,
                    forcePublicationCondition: forcePubCondition,
                    resultHandler: changeObjDef
                });
            }
        },

        // Reloads the tree structure for the selected root node
        reloadSubTree: function(msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);
            this.reloadSubTreeByNode(selectedNode, false);
        },

        reloadSubTreeByNode: function(node, /*boolean*/ doNotSelectNode) {
            // Get the selected node from the message
            var tree = registry.byId("dataTree");

            // check for changes first, since we do not want to do any refresh with
            // any unsaved changes. After the refresh, the clicked node will be selected.
            if (tree.lastLoadedNode) {
                UtilTree.selectNode("dataTree", tree.lastLoadedNode.item.id);
            }
            
            IgeActions.checkForUnsavedChanges().then(function() {
                if (node) {
                    if (!doNotSelectNode) {
                        dirty.resetDirtyFlag();
                        UtilTree.selectNode("dataTree", node.item.id);
                        topic.publish("/selectNode", {
                            id: "dataTree",
                            node: node.item
                        });
                    }

                    tree.refreshChildren(node).then(function() {
                        topic.publish("/loadRequest", {
                            id: node.item.id,
                            appType: node.item.nodeAppType,
                            node: node.item
                        });
                    });
                }
            }, function() {
                // CANCELED
            });
        },

        handleFinalSave: function(msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);

            if (selectedNode.item.nodeAppType == "O") {
                this._handleFinalSaveObject(msg);
            } else if (selectedNode.item.nodeAppType == "A") {
                this._handleFinalSaveAddress(msg);
            }
        },

        _handleFinalSaveObject: function() {
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
            if (checks.isObjectPublishable()) {
                var deferred = new Deferred();
                deferred.then(null, displayErrorMessage);

                var dialogText = this.global.currentUdk.isMarkedDeleted ? message.get("dialog.object.markedDeleted.finalSaveMessage") : message.get("dialog.object.finalSaveMessage");

                // Show a dialog to query the user before publishing
                dialog.show(message.get("dialog.finalSaveTitle"), dialogText, dialog.INFO, [{
                    caption: message.get("general.no"),
                    action: function() {
                        deferred.resolve();
                    }
                }, {
                    caption: message.get("general.yes"),
                    action: function() {
                        console.debug("Publishing event: /publishObjectRequest");
                        topic.publish("/publishObjectRequest", {
                            resultHandler: deferred
                        });
                    }
                }]);

            } else {
                dialog.show(message.get("general.hint"), message.get("tree.nodeCanPublishHint"), dialog.WARNING);
            }
        },

        _handleFinalSaveAddress: function() {
            var nodeData = IgeActions._getData();
            if (checks.isAddressPublishable(nodeData)) {
                var deferred = new Deferred();
                deferred.then(null, displayErrorMessage);

                var dialogText = this.global.currentUdk.isMarkedDeleted ? message.get("dialog.address.markedDeleted.finalSaveMessage") : message.get("dialog.address.finalSaveMessage");

                // Show a dialog to query the user before publishing
                dialog.show(message.get("dialog.finalSaveTitle"), dialogText, dialog.INFO, [{
                    caption: message.get("general.no"),
                    action: function() {
                        deferred.resolve();
                    }
                }, {
                    caption: message.get("general.yes"),
                    action: function() {
                        console.debug("Publishing event: /publishAddressRequest");
                        topic.publish("/publishAddressRequest", {
                            resultHandler: deferred
                        });
                    }
                }]);

            } else {
                dialog.show(message.get("general.hint"), message.get("tree.nodeCanPublishHint"), dialog.WARNING);
            }
        },


        handleForwardToQA: function(msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);

            if (selectedNode.item.nodeAppType == "O") {
                this._handleForwardObjectToQA(msg);
            } else if (selectedNode.item.nodeAppType == "A") {
                this._handleForwardAddressToQA(msg);
            }
        },


        _handleForwardObjectToQA: function() {
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
            if (checks.isObjectPublishable()) {
                var deferred = new Deferred();
                deferred.then(null, displayErrorMessage);

                // Show a dialog to query the user before publishing
                dialog.show(message.get("dialog.forwardToQATitle"), message.get("dialog.object.forwardToQAMessage"), dialog.INFO, [{
                    caption: message.get("general.no"),
                    action: function() {
                        deferred.resolve();
                    }
                }, {
                    caption: message.get("general.yes"),
                    action: function() {
                        console.debug("Publishing event: /forwardObjectToQARequest");
                        topic.publish("/forwardObjectToQARequest", {
                            resultHandler: deferred
                        });
                    }
                }]);

            } else {
                dialog.show(message.get("general.hint"), message.get("tree.forwardToQAHint"), dialog.WARNING);
            }
        },

        _handleForwardAddressToQA: function() {
            // Forward the current address to the QA.
            var valid = checks.checkValidityOfAddressInputElements();

            if (valid != "VALID") {
                if (valid == "INVALID_INPUT_HTML_TAG_INVALID") {
                    dialog.show(message.get("general.error"), message.get("dialog.inputInvalidHtmlTagError"), dialog.WARNING);

                } else {
                    dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
                }
                return;
            }

            var nodeData = IgeActions._getData();
            if (checks.isAddressPublishable(nodeData)) {
                var deferred = new Deferred();
                deferred.then(null, displayErrorMessage);

                // Show a dialog to query the user before forwarding
                dialog.show(message.get("dialog.forwardToQATitle"), message.get("dialog.address.forwardToQAMessage"), dialog.INFO, [{
                    caption: message.get("general.no"),
                    action: function() {
                        deferred.resolve();
                    }
                }, {
                    caption: message.get("general.yes"),
                    action: function() {
                        console.debug("Publishing event: /forwardAddressToQARequest");
                        topic.publish("/forwardAddressToQARequest", {
                            resultHandler: deferred
                        });
                    }
                }]);

            } else {
                dialog.show(message.get("general.hint"), message.get("tree.forwardToQAHint"), dialog.WARNING);
            }
        },


        handleReassignToAuthor: function(msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);

            if (selectedNode.item.nodeAppType == "O") {
                this._handleReassignObjectToAuthor(msg);
            } else if (selectedNode.item.nodeAppType == "A") {
                this._handleReassignAddressToAuthor(msg);
            }
        },


        _handleReassignObjectToAuthor: function() {
            var valid = checks.checkValidityOfInputElements();
            if (valid != "VALID") {
                displayErrorMessage(valid);
                console.debug("input invalid: " + valid);
                return;
            }

            // Forward the current object to the Author.
            //var nodeData = udkDataProxy._getData();
            var deferred = new Deferred();
            deferred.then(null, displayErrorMessage);

            // Show a dialog to query the user before reassigning
            dialog.show(message.get("dialog.reassignToAuthorTitle"), message.get("dialog.object.reassignToAuthorMessage"), dialog.INFO, [{
                caption: message.get("general.no"),
                action: function() {
                    deferred.resolve();
                }
            }, {
                caption: message.get("general.yes"),
                action: function() {
                    console.debug("Publishing event: /forwardObjectToAuthorRequest");
                    topic.publish("/forwardObjectToAuthorRequest", {
                        resultHandler: deferred
                    });
                }
            }]);
        },

        _handleReassignAddressToAuthor: function() {
            var valid = checks.checkValidityOfAddressInputElements();
            if (valid != "VALID") {
                displayErrorMessage(new Error(valid));
                console.debug("input invalid: " + valid);
                return;
            }

            // var nodeData = IgeActions._getData();
            var deferred = new Deferred();
            deferred.then(null, displayErrorMessage);

            // Show a dialog to query the user before forwarding
            dialog.show(message.get("dialog.reassignToAuthorTitle"), message.get("dialog.address.reassignToAuthorMessage"), dialog.INFO, [{
                caption: message.get("general.no"),
                action: function() {
                    deferred.resolve();
                }
            }, {
                caption: message.get("general.yes"),
                action: function() {
                    console.debug("Publishing event: /forwardAddressToAuthorRequest");
                    topic.publish("/forwardAddressToAuthorRequest", {
                        resultHandler: deferred
                    });
                }
            }]);
        },


        handleMarkDeleted: function(msg) {
            var markDeferred = new Deferred();
            // Get the selected node from the message
            var selectedNodes = this.getSelectedNodes(msg);

            var selectedPublishedNodes = [];
            var selectedNonPublishedNodes = [];
            array.forEach(selectedNodes, function(node) {
                if (node.id == "newNode") {
                    this.handleDiscard(msg);
                } else if (!node.item.isPublished) {
                    selectedNonPublishedNodes.push(node);
                } else {
                    selectedPublishedNodes.push(node);
                }
            });
            if (selectedNonPublishedNodes.length > 0)
                markDeferred.then(dojo.partial(this._deleteNodes, selectedNonPublishedNodes));

            var notAllowedSelection = array.filter(selectedPublishedNodes, function(node) {
                return node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot";
            });

            if (notAllowedSelection.length > 0) {
                dialog.show(message.get("general.hint"), message.get("tree.selectNodeDeleteHint"), dialog.WARNING);
            } else {
                // If a selected node was found do the following:
                // 1. Query the user if he really wants to delete the selected object
                //    This is accomplished by creating a deferred obj 'deferred' and passing it to the
                //    delete_object dialog. If the user clicks yes, the attached callback function is executed.
                // 2. The attached callback function publishes a delete request which is picked up by the
                //    udkDataProxy and sent to the backend. We need another deferred obj 'deleteObjDef' for this
                //    so we can see if the delete operation was successful.
                var deferred = new Deferred();
                var deleteFunction = function(nodeToDelete) {
                    var deleteObjDef = new Deferred();
                    if (registry.byId(nodeToDelete.id)) {
                        deleteObjDef.then(function() {
                            // This function is called when the user has selected yes and the node was successfully
                            // marked as deleted
                            var tree = registry.byId("dataTree");
                            if (tree.selectedNode == nodeToDelete) {
                                // If the current node was marked as deleted, reload the
                                // current node (updates permissions, etc.)
                                UtilTree.reloadNode("dataTree", nodeToDelete);
                                tree.model.store.setValue(nodeToDelete.item, "userWritePermission", false);
                            } else {
                                // Otherwise update the node that was marked as deleted
                                // The nodeDocType and permissions have to be updated
                                //var treeNode = registry.byId(nodeToDelete.id).item;

                                if (nodeToDelete) {
                                    if (nodeToDelete.item.nodeDocType.search(/_BV|_RV/) != -1) {
                                        // If the nodeDocType ends with _BV or _RV, replace it with _QV
                                        nodeToDelete.item.nodeDocType = nodeToDelete.item.nodeDocType.replace(/_BV|_RV/, "_QV");
                                    } else {
                                        // else add _QV to the end of the string
                                        nodeToDelete.item.nodeDocType = nodeToDelete.item.nodeDocType + "_QV";
                                    }

                                    // update permissions
                                    nodeToDelete.item.userWriteSubTreePermission = nodeToDelete.item.userWriteTreePermission;
                                    nodeToDelete.item.userWritePermission = false;
                                    nodeToDelete.userMovePermission = false;
                                    nodeToDelete.item.userWriteSinglePermission = false;
                                    nodeToDelete.userWriteSubNodePermission = false;
                                    // set last item change via the model to trigger onChange-method which updates
                                    // the node style in the tree
                                    tree.model.store.setValue(nodeToDelete.item, "userWriteTreePermission", false);
                                }
                            }
                            //tree.refreshChildren(nodeToDelete.getParent());
                        }, displayErrorMessage);

                        // Tell the backend to delete the selected node.
                        console.debug("Publishing event: /deleteRequest(" + nodeToDelete.id + ", " + nodeToDelete.item.nodeAppType + ")");
                        topic.publish("/deleteRequest", {
                            id: nodeToDelete.item.id,
                            resultHandler: deleteObjDef
                        });
                    } else {
                        deleteObjDef.resolve();
                    }
                    return deleteObjDef;
                };

                // 'schedule' all nodes for deletion
                array.forEach(selectedPublishedNodes, function(node) {
                    deferred.then(dojo.partial(deleteFunction, node));
                });

                // check if parent from loaded node was deleted
                // -> load the parent instead!
                //deferred.then(loadParentFunction);   

                if (selectedPublishedNodes.length > 0) {
                    markDeferred.then(function() {
                        // Build the message key (dialog.<object|address>.delete<Children>Message)
                        var messageKey = "dialog.";
                        if (selectedPublishedNodes[0].item.nodeAppType == "O")
                            messageKey += "object.markDelete";
                        else
                            messageKey += "address.markDelete";

                        var selectionContainsFolder = array.some(selectedPublishedNodes, function(node) {
                            return node.item.isFolder;
                        });
                        if (selectionContainsFolder)
                            messageKey += "Children";

                        messageKey += "Message";

                        var nodeTitles = "<ul>";
                        array.forEach(selectedPublishedNodes, function(node) {
                            nodeTitles += "<li>" + node.item.title + "</li>";
                        });
                        nodeTitles += "</ul>";
                        var displayText = string.substitute(message.get(messageKey), [nodeTitles]);

                        dialog.show(message.get("general.delete"), displayText, dialog.INFO, [{
                            caption: message.get("general.cancel"),
                            action: function() { /*deferred.reject();*/ }
                        }, {
                            caption: message.get("general.ok"),
                            action: function() {
                                deferred.resolve();
                            }
                        }]);
                    });
                }
                markDeferred.resolve();
                return markDeferred;
            }
        },


        handleUnmarkDeleted: function() {
            // Only available for the person in charge of QS.
            // Removes the flag which marks the obj/adr for deletion
            this.alertNotImplementedYet();
        },


        handleShowChanges: function(msg) {
            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);

            var params = {
                selectedNodeId: selectedNode.item.id
            };

            if (selectedNode.item.nodeAppType == "O") {
                dialog.showPage(message.get("dialog.compareView.title"), "dialogs/mdek_compare_view_dialog.jsp?c=" + userLocale, 755, 600, true, params);
            } else if (selectedNode.item.nodeAppType == "A") {
                dialog.showPage(message.get("dialog.compareView.title"), "dialogs/mdek_compare_view_address_dialog.jsp?c=" + userLocale, 755, 600, true, params);
            }
        },

        handleShowComment: function() {
            dialog.showPage(message.get("dialog.showComments.title"), "dialogs/mdek_comments_dialog.jsp?c=" + userLocale, 1010, 470, true);
            /*
    var nodeId = prompt("Jump to node with uuid", "5CE671D3-5475-11D3-A172-08002B9A1D1D");
    if (nodeId) {
        menuEventHandler.handleSelectNodeInTree(nodeId);
    }
*/
        },

        switchLanguage: function() {
            document.location.href = "start.jsp?lang=" + registry.byId("languageBox").get("value");
        },

        openCreateObjectWizardDialog: function() {
            dialog.showPage(message.get("dialog.wizard.selectTitle"), "dialogs/mdek_select_wizard_dialog.jsp?c=" + userLocale, 350, 170, true);
        },

        inheritAddressToChildren: function(msg) {
            var defMain = new Deferred();
            var def = new Deferred();

            // Get the selected node from the message
            var selectedNode = this.getSelectedNode(msg);

            defMain.then(function(res) {
                dialog.show(message.get("general.info"), string.substitute(message.get("info.address.inherit.to.children"), [res]), dialog.INFO);
            });

            dialog.show(message.get("general.warning"), string.substitute(message.get("warning.address.inherit.to.children"), [selectedNode.label]), dialog.WARNING, [{
                caption: message.get("general.cancel"),
                action: function() { /**onForceSaveDef.reject();*/ }
            }, {
                caption: message.get("general.ok"),
                action: function() {
                    def.resolve();
                }
            }]);

            def.then(function() {
                console.debug("Publishing event: /inheritAddressToChildren(" + selectedNode.item.id + ")");
                topic.publish("/inheritAddressToChildren", {
                    id: selectedNode.item.id,
                    resultHandler: defMain
                });
            });
            return defMain;
        },


        // ------------------------- Helper functions -------------------------

        alertNotImplementedYet: function() {
            alert("Diese Funktionalität ist noch nicht implementiert.");
        },

        /**
         * Check if the function was initiated by the context menu or from toolbar
         * @param msg
         * @returns true, if conext menu was used
         */
        calledFromContextMenu: function(msg) {
            var fromContextMenu = false;
            // if it was called from a submenu after a click on the tree
            if (has("ie")) {
                if (msg.srcElement.className.indexOf("dijitMenuItemLabel") != -1) {
                    fromContextMenu = true;
                }
            } else {
                if (msg.target.classList.contains("dijitMenuItemLabel")) {
                    fromContextMenu = true;
                }
            }
            return fromContextMenu;
        },

        getSelectedNode: function(msg) {
            if (this.calledFromContextMenu(msg)) {
                return registry.byId("menuDataTree").clickedNode;
            } else { // or from the toolbar
                var tree = registry.byId("dataTree");
                if (tree.selectedNode)
                    return tree.selectedNode;
                else
                    return null;
            }
        },

        getTargetNode: function(msg) {
            return this.getSelectedNode(msg);
        },

        getSelectedNodes: function() {
            //    var selectedNodes = registry.byId("dataTree").allFocusedNodes;
            //    if (calledFromContextMenu(msg)) {
            //        var clickedAdditionalNode = registry.byId("menuDataTree").clickedNode;
            //        // add last clicked nodes (where context menu was called) to selected nodes
            //        if (array.indexOf(selectedNodes, clickedAdditionalNode) === -1) {
            //            selectedNodes.push(clickedAdditionalNode);
            //        }
            //    }
            // since the change of selecting and activating nodes in a tree
            // it's now easier to access all selected nodes
            return registry.byId("dataTree").selectedNodes;
        },

        _createNewNode: function(obj) {
            var title;
            // var objClass;

            if (obj.nodeAppType == "O") {
                title = message.get("tree.newNodeName");
            } else if (obj.nodeAppType == "A") {
                title = message.get("tree.newAddressName");
            }

            return {
                isFolder: obj.isFolder,
                nodeDocType: obj.nodeDocType, // Initial display type of a new node
                title: title,
                //dojoType: 'ingrid:TreeNode',
                nodeAppType: obj.nodeAppType,
                userWritePermission: true,
                userMovePermission: true,
                id: obj.uuid
            }; // "newNode"
        },

        _isChildOf: function(childNode, targetNode) {
            if (!childNode.getParent()) {
                return false;
            } else if (childNode.getParent().item.id == targetNode.id) {
                return true;
            } else if (childNode.getParent().item.id == "objectRoot" || childNode.getParent().item.id == "addressRoot" || childNode.getParent().item.id == "addressFreeRoot") {
                return false;
            } else {
                return this._isChildOf(childNode.getParent(), targetNode);
            }
        }
    })();
});