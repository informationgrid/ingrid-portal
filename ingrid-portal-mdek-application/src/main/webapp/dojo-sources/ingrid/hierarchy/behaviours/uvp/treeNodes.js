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
define(["dojo/_base/declare",
    "dojo/Deferred",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Catalog"
], function(declare, Deferred, topic, registry, message, Catalog) {

    return declare(null, {
        title: "UVP: Verhalten der Baumknoten",
        description: "Definition des Verhaltens, was bei der Auswahl des Wurzelknotens und der darunterliegenden Ordner geschehen soll.",
        defaultActive: true,
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
            topic.subscribe("/selectNode", function(message) {
                if (message.id === "dataTree") {
                    // do not allow to add new objects directly under the root node
                    if (message.node.id === "objectRoot") {
                        console.log("disable create/paste new object");
                        registry.byId("toolbarBtnNewDoc").set("disabled", true);
                        registry.byId("toolbarBtnPaste").set("disabled", true);
                    } else if (message.node.parent === "objectRoot") {
                        // do not allow to rename or delete the folders directly under the root node
                        registry.byId("toolbarBtnCut").set("disabled", true);
                        registry.byId("toolbarBtnCopy").set("disabled", true);
                        registry.byId("toolbarBtnCopySubTree").set("disabled", true);
                        // TODO: allow paste for children of this node (see below)
                        registry.byId("toolbarBtnPaste").set("disabled", true);
                        registry.byId("toolbarBtnSave").set("disabled", true);
                        registry.byId("toolbarBtnDelSubTree").set("disabled", true);
                        // also disable editing of object name
                        registry.byId("objectName").set("disabled", true);
                    } else if (message.node.nodeAppType === "O") {
                        registry.byId("objectName").set("disabled", false);
                    }
                }
            });

            topic.subscribe("/onTreeContextMenu", function(node) {
                console.log("context menu called from:", node);
                if (node.item.id === "objectRoot") {
                    registry.byId("menuItemNew").set("disabled", true);
                    registry.byId("menuItemPaste").set("disabled", true);
                } else if (node.item.parent === "objectRoot") {
                    registry.byId("menuItemPreview").set("disabled", true);
                    registry.byId("menuItemCut").set("disabled", true);
                    registry.byId("menuItemCopySingle").set("disabled", true);
                    registry.byId("menuItemCopy").set("disabled", true);
                    // TODO: should we able to paste nodes into another root folder with a different document type?
                    // we should be able to paste children of this node under this top node, but not
                    // nodes from other folders where only certain classes may appear
                    registry.byId("menuItemPaste").set("disabled", true);
                    registry.byId("menuItemDelete").set("disabled", true);
                    registry.byId("menuItemPublicationCondition1").set("disabled", true);
                    registry.byId("menuItemPublicationCondition2").set("disabled", true);
                    registry.byId("menuItemPublicationCondition3").set("disabled", true);
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
        }
    })();
});