/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
define("ingrid/tree/MetadataTree", [
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dijit/registry",
    "dijit/Tree",
    "ingrid/tree/DwrStore",
    "ingrid/utils/Security",
    "ingrid/utils/Tree",
    "dojo/store/Observable",
    "dijit/tree/ObjectStoreModel"
], function(declare, array, lang, domClass, registry, Tree, DwrStore, UtilSecurity, UtilTree, Observable, ObjectStoreModel) {

    declare("CustomTreeNode", Tree._TreeNode, {
        templateString: dojo.cache("ingrid.tree", "templates/CustomTreeNode.html"),

        _updateItemClasses: function(item){
            // summary:
            //      Set appropriate CSS classes for icon and label dom node
            //      (used to allow for item updates to change respective CSS)
            // tags:
            //      private
            var tree = this.tree, model = tree.model;
            if(tree._v10Compat && item === model.root){
                // For back-compat with 1.0, need to use null to specify root item (TODO: remove in 2.0)
                item = null;
            }
            this._applyClassAndStyle(item, "preIcon", "PreIcon");
            this._applyClassAndStyle(item, "icon", "Icon");
            this._applyClassAndStyle(item, "label", "Label");
            this._applyClassAndStyle(item, "row", "Row");
        }
    });

    var MDTree = declare( "ingrid.tree.MetadataTree", [ Tree ], {

        /**
         * This can be one of: Objects Addresses ObjectsAndAddresses Users
         */
        treeType: "ObjectsAndAddresses",

        lastFocusedNode: null, // To store the currently focused node in the tree.
        
        lastLoadedNode: null, // remember the last loaded node for correct deselection

        nodesToCopy: null,
        
        nodesToCut : null,

        // register a function to decide which nodes not to make selectable
        excludeFunction: null,
        
        // sort nodes in tree by their object class
        sortByClass: false,

        // use special sort function to determine the order of the nodes
        sortFunction: null,

        getPreIconClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
            var myClass = "TreePreIcon";
            if (item.publicationCondition != null) {
                myClass += " TreePreIcon" + item.publicationCondition;
                if (item.userWritePermission == false)
                    myClass += " IconDisabled";
            }
            return myClass;
        },

        getPreIconStyle: function(/*dojo.data.Item*/ item, /*Boolean*/ opened){
            // summary:
            //      Overridable function to return CSS styles to display preIcon
            // returns:
            //      Object suitable for input to dojo.style() like {color: "red", background: "green"}
            // tags:
            //      extension
        },

        getIconClass: function(/* dojo.data.Item */item, /* Boolean */opened) {
            var myClass = "TreeIcon " + "TreeIcon" + item.nodeDocType;
            if (item.userWritePermission && item.userWritePermission[0] === false)
                myClass += " IconDisabled";
            return myClass;
        },

        getLabelClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
            var myClass = "";
            if (item.labelClass)
                myClass = item.labelClass;
            // check explicitly if set to false ! (can also be null in top nodes ...)
            if (item.userWritePermission === false) {
                myClass += " TreeNodeNotWritable";
            }
            if (this.itemNotAllowed(item)) {
                myClass += " TreeNodeNotSelectable";
            }
            return myClass;
        },

        postMixInProperties: function() {
            var self = this;
            var memoryStore = new DwrStore ( {
                data: [],
                storeType: this.treeType,
                getChildren: function(object){
                    // Add a getChildren() method to store for the data model where
                    // children objects point to their parent (aka relational model)
                    return this.query({parent: object.id, nodeAppType: object.nodeAppType}, { sortByClass: self.sortByClass, sortFunction: self.sortFunction });
                }
            } );

            var observableStore = new Observable( memoryStore );

            this.model = new ObjectStoreModel( {
                store: observableStore,
                // rootId: "root_"+id,
                deferItemLoadingUntilExpand: true,
                childrenAttrs: [ "children" ],
                labelAttr: "title",
                query: { parent: null },//, nodeAppType: "O", isRoot: true },
                //root: data,
                mayHaveChildren: function(item) {
                    return item.isFolder;
                }
            } );

            this.inherited( arguments );
        },

        /*
         * call of an external function that is responsible for disabled nodes in the tree 
         */
        itemNotAllowed: function(item) {
            return this.excludeFunction ? this.excludeFunction(item) : false;
        },

        _createTreeNode: function(/*Object*/ args){
            // summary:
            //      creates a TreeNode
            // description:
            //      Developers can override this method to define their own TreeNode class;
            //      However it will probably be removed in a future release in favor of a way
            //      of just specifying a widget for the label, rather than one that contains
            //      the children too.
            return new CustomTreeNode(args);
        },
        
        refreshChildren: function(/*TreeNode*/node) {
            var self = this;
            
            // remove all children, which can contain other nodes which need to be updated
            // this will also collapse all children
            node.setChildItems([]);
            
            // also remove any children loaded inside the cache, so that when a node is expanded
            // new data is requested instead of showing old state
            self.model.childrenCache = {};
            
            return this.model.store.getChildren(node.item).then(function(updatedChildren) {
                try {
                    array.forEach(updatedChildren, function(copiedNode) {
                        self.model.store.put(copiedNode);
                    });
                    node.setChildItems(updatedChildren);
                } catch(ex) {
                    // FIXME: sometime the element this.expandoNode is null when publishing a new address
                    console.error( "Error during updating tree nodes", ex);
                    // ignore error message since user does not need to be informed about this
                    // displayErrorMessage(ex);
                }
            });
        },

        _markLoadedNode: function(/*TreeNode*/node) {
            var selectedNode = node ? node : this.selectedNode;
            // set de-/selection css classes
            if (this.lastLoadedNode && this.lastLoadedNode.domNode !== null) domClass.remove(this.lastLoadedNode.domNode, "TreeNodeSelect");
            domClass.add(selectedNode.domNode, "TreeNodeSelect");
            this.lastLoadedNode = selectedNode;
        },

        prepareCopy: function(/*TreeNodeItem[]*/nodes, /*boolean*/copySubTree) {
            this.nodesToCopy = nodes;
            this.copySubTree = copySubTree;
            if (this.nodesToCut) {
                array.forEach(this.nodesToCut, function(nodeItem) {
                    var node = UtilTree.getNodeById("dataTree", nodeItem.id);
                    domClass.remove(node.id, "nodeCut");
                });
                this.nodesToCut = null;
            }
            dojo.publish("/prepareCopy", [nodes]);
        },
        
        prepareCut: function(/*TreeNodeItem[]*/nodes) {
            // if another node was chosen for cut
            // -> reset it
            if (this.nodesToCut) {
                array.forEach(this.nodesToCut, function(nodeItem) {
                    var node = UtilTree.getNodeById("dataTree", nodeItem.id);
                    domClass.remove(node.id, "nodeCut");
                });
            }
                
            this.nodesToCut = nodes;
            array.forEach(this.nodesToCut, function(nodeItem) {
                var node = UtilTree.getNodeById("dataTree", nodeItem.id);
                domClass.add(node.id, "nodeCut");
            });
            this.nodesToCopy = null;
            dojo.publish("/prepareCut", [nodes]);
        },
        
        doPaste: function() {
            this.nodesToCut = null;
        },
        
        canPaste: function(node) {
            var srcNodes;
            var canBePasted = true;
            
            // cannot paste if multiple nodes are selected!
            if (this.selectedNodes.length > 1)
                return false;
            
            if (this.nodesToCopy !== null)
                srcNodes = this.nodesToCopy;
            else if (this.nodesToCut !== null)
                srcNodes = this.nodesToCut;
            else
                return false;
    
            var dstIsRootNode = (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot");
    
            array.forEach(srcNodes, function(srcNode) {
                if (node !== null) {
                    if (node.id == "newNode" || (!dstIsRootNode && (!node.userWriteTreePermission && !node.userWriteSubTreePermission && !node.userWriteSubNodePermission)))
                        canBePasted = false;
        
                    if (dstIsRootNode && !UtilSecurity.canCreateRootNodes())
                        canBePasted = false;
        
                    if (node.nodeAppType == srcNode.nodeAppType) {
                        if (node.nodeAppType == "O") {
                            //return true; // Objects can be pasted anywhere below objects
                        } else if (node.nodeAppType == "A") {
                            var srcType = srcNode.objectClass;
                            var dstType = node.objectClass;
                            if (typeof(dstType) == "undefined" || dstType === null) {
                                // Target is either addressRoot or addressFreeRoot
                                if (node.id == "addressFreeRoot") {
                                    canBePasted = canBePasted && (srcType >= 2);  // Only Addresses can be converted to free addresses
                                } else if (node.id == "addressRoot") {
                                    canBePasted = canBePasted && (srcType === 0 || srcType === 1000); // Only Institutions and folders are allowed below the root node
                                }
                            }

                            if (srcType === 3 && node.id !== "addressFreeRoot"){
                                // Free Addresses can only be pasted under addressFreeRoot #2213
                                canBePasted = false;
                            }

                            // The target node is no root node and no folder. Compare the src and dst types:
                            if (dstType !== 1000 && (dstType >= 2 || (srcType === 0 && dstType === 1))) {
                                canBePasted = false;
                            }
                            
                        }
                    } else {
                        // src and destination type are not equal
                        canBePasted = false;
                    }
                }
            });
            return canBePasted;
        }

    } );

    return MDTree;
} );
