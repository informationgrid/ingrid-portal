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
define("ingrid/tree/LFSTree", [
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dijit/registry",
    "dijit/Tree",
    "ingrid/tree/LFSStore",
    "ingrid/utils/Security",
    "ingrid/utils/Tree",
    "dojo/store/Observable",
    "dijit/tree/ObjectStoreModel"
], function (declare, array, lang, domClass, registry, Tree, LFSStore, UtilSecurity, UtilTree, Observable, ObjectStoreModel) {

    /*declare("LFSTreeNode", Tree._TreeNode, {
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
    });*/

    return declare("ingrid.tree.LFSTree", [Tree], {

        /**
         * This can be one of: receipt, filing
         */
        treeType: "receipt",

        lastFocusedNode: null, // To store the currently focused node in the tree.

        lastLoadedNode: null, // remember the last loaded node for correct deselection

        // register a function to decide which nodes not to make selectable
        excludeFunction: null,

        // use special sort function to determine the order of the nodes
        sortFunction: function (/*Array*/children) {
            return children.sort(function (a, b) {
                return a.name.localeCompare(b.name)
            });
        },

        getIconClass: function (/* dojo.data.Item */item, /* Boolean */opened) {
            var icon = item.type === "container" ? "Class1000_B" : "Class0";
            var myClass = "TreeIcon " + "TreeIcon" + icon;
            if (item.userWritePermission && item.userWritePermission[0] === false)
                myClass += " IconDisabled";
            return myClass;
        },

        postMixInProperties: function () {
            var self = this;
            var memoryStore = new LFSStore({
                data: [],
                storeType: this.treeType,
                sortFn: this.sortFunction,
                getChildren: function (object) {
                    // Add a getChildren() method to store for the data model where
                    // children objects point to their parent (aka relational model)
                    return this.query({
                        parent: object.id,
                        path: object.path
                    });
                }
            });

            var observableStore = new Observable(memoryStore);

            this.model = new ObjectStoreModel({
                store: observableStore,
                deferItemLoadingUntilExpand: true,
                childrenAttrs: ["children"],
                labelAttr: "name",
                query: {parent: null},
                mayHaveChildren: function (item) {
                    return item.type === "container";
                }
            });

            this.inherited(arguments);
        }
    });
});
