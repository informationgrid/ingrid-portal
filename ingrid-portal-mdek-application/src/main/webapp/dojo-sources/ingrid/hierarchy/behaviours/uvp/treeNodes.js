/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
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
    "dojo/_base/array",
    "dojo/Deferred",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Catalog"
], function(declare, array, Deferred, topic, registry, message, Catalog) {

    return declare(null, {
        title: "UVP: Verhalten der Baumknoten",
        description: "Definition des Verhaltens, was bei der Auswahl des Wurzelknotens und der darunterliegenden Ordner geschehen soll.",
        defaultActive: true,
        category: "UVP",
        type: "SYSTEM", // execute on IGE page load
        run: function() {
            var self = this;
            topic.subscribe("/onPageInitialized", function(page) {
                if (page === "Hiearchy") {
                    self.handleTreeOperations();
                }
            });
            
            
            this.setTreeSortFunction();
        },

        handleTreeOperations: function() {
            var self = this;

            // subscribe a bit later to move behaviour after folders-behaviour!
            setTimeout(function() {
                topic.subscribe("/selectNode", function(message) {
                    if (message.id === "dataTree") {
                        // do not allow to add new objects directly under the root node
                        if (message.node.id === "objectRoot") {
                            console.log("disable create/paste new object");
                            registry.byId("toolbarBtnNewDoc").set("disabled", true);
                            registry.byId("toolbarBtnPaste").set("disabled", true);
                            registry.byId("toolbarBtnNewFolder").set("disabled", true);
                        } else if (message.node.parent === "objectRoot") {
                            registry.byId("toolbarBtnNewFolder").set("disabled", false);
                            // do not allow to rename or delete the folders directly under the root node
                            registry.byId("toolbarBtnCut").set("disabled", true);
                            registry.byId("toolbarBtnCopy").set("disabled", true);
                            registry.byId("toolbarBtnCopySubTree").set("disabled", true);
                            // activity of paste-option is handled by below function
                            // registry.byId("toolbarBtnPaste").set("disabled", true);
                            registry.byId("toolbarBtnSave").set("disabled", true);
                            registry.byId("toolbarBtnDelSubTree").set("disabled", true);
                            // also disable editing of object name
                            registry.byId("objectName").set("disabled", true);
                        } else if (message.node.nodeAppType === "O") {
                            registry.byId("objectName").set("disabled", false);
                        }

                        var tree = registry.byId("dataTree");
                        var nodes = tree.nodesToCopy ? tree.nodesToCopy : tree.nodesToCut;
                        if (nodes) {
                            var node = tree.getNodesByItem(message.node.id)[0];
                            var hasValidParent = self._checkValidParent(node, nodes);
                            
                            if (!hasValidParent) registry.byId("toolbarBtnPaste").set("disabled", true);
                        }
                    }
                });
            }, 0);

            topic.subscribe("/onTreeContextMenu", function(node) {
                
                // handle actions on root node and folders directly beneath it
                if (node.item.id === "objectRoot") {
                    registry.byId("menuItemNew").set("disabled", true);
                    registry.byId("menuItemPaste").set("disabled", true);
                } else if (node.item.parent === "objectRoot") {
                    registry.byId("menuItemPreview").set("disabled", true);
                    registry.byId("menuItemCut").set("disabled", true);
                    registry.byId("menuItemCopySingle").set("disabled", true);
                    registry.byId("menuItemCopy").set("disabled", true);
                    // activity of paste-option is handled by below function
                    // registry.byId("menuItemPaste").set("disabled", true);
                    registry.byId("menuItemDelete").set("disabled", true);
                    registry.byId("menuItemPublicationCondition1").set("disabled", true);
                    registry.byId("menuItemPublicationCondition2").set("disabled", true);
                    registry.byId("menuItemPublicationCondition3").set("disabled", true);
                }

                // check if copied/cut nodes can be inserted (under correct node)
                // we should be able to paste children of this node under this top node, but not
                // nodes from other folders where only certain classes may appear
                var tree = registry.byId("dataTree");
                var nodes = tree.nodesToCopy ? tree.nodesToCopy : tree.nodesToCut;
                if (nodes) {
                    var hasValidParent = self._checkValidParent(node, nodes);
                    if (!hasValidParent) registry.byId("menuItemPaste").set("disabled", true);
                }
            });
        },

        setTreeSortFunction: function() {
            Catalog.catalogData.treeSortFunction = function(query, children) {
                console.log("query", query);
                console.log("children", children);
                if (query.parent === "objectRoot") {
                    var sortOrder = [
                        message.get("uvp.form.categories.uvp"),
                        message.get("uvp.form.categories.uvpInFront"),
                        message.get("uvp.form.categories.uvpNegative"),
                        message.get("uvp.form.categories.uvpForeign")
                    ];
                    return children.sort(function(child1, child2) {
                        var pos1 = sortOrder.indexOf(child1.title)+"";
                        var pos2 = sortOrder.indexOf(child2.title);
                        return pos1.localeCompare(pos2, {kn: true}, {numeric: true});
                    });
                }
                return children;
            };
        },

        _getTopParentNode: function(node) {
            var tree = registry.byId("dataTree");
            while (node && node.item.parent !== "objectRoot") {
                node = tree.getNodesByItem(node.item.parent)[0];
            }
            return node;
        },

        /**
         * Check if under the selected node a list of nodes can be inserted.
         * 
         * @param {object} selectedNode is the node to insert other nodes
         * @param {object[]} nodesToInsert are the nodes to be inserted
         */
        _checkValidParent: function(selectedNode, nodesToInsert) {
            var rootFolderNode = this._getTopParentNode(selectedNode);

            // if we want to insert under the top root node then we won't allow it
            if (!rootFolderNode) return false;

            var rootFolderLabel = rootFolderNode.label;
            return array.every(nodesToInsert, function(nodeToInsert) {
                var insertClass = nodeToInsert.objectClass;

                // just check if each copied node is under the same UVP top folder
                return this._getTopParentNode({item: {parent: nodeToInsert}}).label === rootFolderLabel;
            }, this);
        }
    })();
});