/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/on",
    "dojo/Deferred",
    "dijit/registry"
], function(declare, array, lang, aspect, on, Deferred, registry) {
    return declare(null, {

        getRootObject: function() {
            return {
                //id: prefix + "objectRoot",
                title: "objects",
                id: "objectRoot",
                nodeAppType: "O"
            };
        },

        getRootAddress: function() {
            return {
                title: "addresses",
                id: "addressRoot",
                nodeAppType: "A"
            };
        },

        getSubTree: function(item) {
            var deferred = new Deferred();
            var id = null;
            var type = null;

            // if node is not root get the object and address node
            id = item.id; //.substring(prefixIndex);
            type = item.nodeAppType;

            TreeService.getSubTree(id, type, userLocale, {
                //preHook: UtilDWR.enterLoadingState,
                //postHook: UtilDWR.exitLoadingState,
                callback: function(res) {
                    deferred.resolve(res);
                },
                errorHandler: function(message) {
                    //UtilDWR.exitLoadingState();
                    deferred.reject(message);
                }
            });
            return deferred.promise;
        },

        getNodeById: function(treeId, itemId) {
            var tree = registry.byId(treeId);
            return tree.getNodesByItem(itemId)[0];
        },

        selectNode: function(treeId, nodeId, /*boolean*/active) {
            var tree = registry.byId(treeId);

            // get the node from the tree internal map
            var node = this.getNodeById(treeId, nodeId);

            // set the selected node
            // do not call setSelected only on the node, since it will not be registered
            // by the tree itself and won't put it on the selected nodes list!!!
            tree.set("selectedNodes", [node]);

            if (active) {
                tree._markLoadedNode(node);
            }
        },

        selectNodeByItem: function(item) {
            item.setSelected(true);
        },

        /*reloadNode: function(treeId, node) {
            var d = new Deferred();
            var self = this;
            d.addCallback(function() {
                self.selectNode(treeId, node.item.id);
                var nodeElem = self.getNodeById(node.item.id);
                dojo.window.scrollIntoView(nodeElem.domNode);
            });
            d.addErrback(function(msg) {
                dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
                console.debug(msg);
            });
            //udkDataProxy.resetDirtyFlag();
            console.debug("Publishing event: /loadRequest(" + node.id + ", " + node.item.nodeAppType + ")");
            dojo.publish("/loadRequest", [{
                id: node.id,
                appType: node.item.nodeAppType,
                node: node.item,
                resultHandler: d
            }]);
        },*/

        /**
         * Delete a tree node with all its sub nodes. The promise will be fulfilled after
         * the item has been completely removed from the tree object.
         * 
         * @param  {String} treeId
         * @param  {String} itemId
         * @return {Promise} containing the parent node of the deleted node
         */
        deleteNode: function(treeId, /*TreeNodeItem or ID*/item) {
            //var def = new Deferred();
            var tree = registry.byId(treeId);
        
            var node;
            // convert to item if only id was given
            if (typeof item === "string") {
                node = this.getNodeById(treeId, item);
                if (!node) return;

            } else {
                node = this.getNodeById(treeId, item.id);
            }
            
            // delete all children first
            if (node) {
                this._recursiveDelete(tree.model, node.getChildren());

                // finally delete the node itself
                // delete tree.model.childrenCache[node.item.id];
                // tree.model.onDelete(node.item);
                tree.model.store.remove(node.item.id);

            }
        },

        _recursiveDelete: function(model, children) {
            if (children.length === 0) return;

            array.forEach(children, function(child) {
                this._recursiveDelete(model, child.getChildren());
                delete model.childrenCache[child.item.id];
                model.onDelete(child.item);
            }, this);
        },

        addNode: function(treeId, /*TreeNode*/parentNode, item) {
            var def = new Deferred();
            var tree = registry.byId( treeId );

            // make sure the parent node is expandable
            if (!parentNode.isExpandable) {
                parentNode.makeExpandable();
            }
            // expand node and add item to store which will automatically update the tree
            tree._expandNode(parentNode).then(function() {
                // return item after it has been added to the tree
                var handler = aspect.after(tree, "_onItemChildrenChange", function() {
                    handler.remove();
                    // return first (and only) item that was just added
                    def.resolve(this.getNodesByItem(item.id)[0]);
                });
                item.parent = parentNode.item.id;
                tree.model.store.add(item);
            });

            return def.promise;
        },

        updateNode: function(treeId, parentNode, item) {
            var def = new Deferred();
            var tree = registry.byId( treeId );

            // if no parent node is given, then just updated the tree node
            if (!parentNode) {
                tree.model.store.put(item);
                // return item after it has been added to the tree
                var handler = aspect.after(tree, "_onItemChildrenChange", function() {
                    handler.remove();
                    // return first (and only) item that was just added
                    def.resolve(this.getNodesByItem(item.id)[0]);
                });
                return def;
            }

            // make sure the parent node is expandable
            if (!parentNode.isExpandable) {
                parentNode.makeExpandable();
            }
            tree._expandNode(parentNode).then(function() {
                tree.model.store.put(item);
            });
        },

        /**
         * Refresh children of a node. If no node is given then a currently selected
         * node is used.
         */
        refreshChildren: function(treeId, node) {
            var tree = registry.byId("dataTree");
            var selectedNode = node ? node : tree.selectedNode;
            tree.refreshChildren(selectedNode);
        }

    })();
});