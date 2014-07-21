define(["dojo/_base/declare", "dojo/_base/array", "dojo/_base/lang", "dojo/Deferred", "dojo/topic", "dojo/on", "dojo/aspect", "dojo/dom-class",
        "dijit/registry", "dijit/Menu", "dijit/MenuItem", "dijit/MenuSeparator",
        "ingrid/MenuActions", "ingrid/IgeActions",
        "ingrid/utils/QA", "ingrid/utils/Security", "ingrid/utils/Tree",
        "ingrid/message"
    ],
    function(declare, array, lang, Deferred, topic, on, aspect, domClass, registry, Menu, MenuItem, MenuSeparator,
        menuEventHandler, IgeActions, UtilQA, UtilSecurity, UtilTree, message) {

        return declare(null, {

            /*********************************************************
             * OBJECT
             *********************************************************/

             menu: null,

            /*
             * Create the tree
             */
            createTreeMenu: function() {
                /////////////////////////////////////////////////////////////
                // create the context menu for the tree, which contains all
                // items. when clicking on a node it depends on the type, which
                // items will be shown
                // shown when right-cicking somewhere in the tree
                /////////////////////////////////////////////////////////////
                var menuDataTree = new Menu({
                    id: "menuDataTree",
                    targetNodeIds: ["dataTree"]
                });
                //iconClass: "dijitEditorIcon dijitEditorIconCut",
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemNew",
                    label: message.get("tree.nodeNew"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.handleNewEntity)
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemPreview",
                    label: message.get("tree.nodePreview"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.handlePreview)
                }));
                menuDataTree.addChild(new MenuSeparator());
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemCut",
                    label: message.get("tree.nodeCut"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.handleCut)
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemCopySingle",
                    label: message.get("tree.nodeCopySingle"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.handleCopyEntity)
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemCopy",
                    label: message.get("tree.nodeCopy"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.handleCopyTree)
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemPaste",
                    label: message.get("tree.nodePaste"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.handlePaste)
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemReloadSub",
                    label: message.get("tree.subReload"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.reloadSubTree)
                }));
                menuDataTree.addChild(new MenuSeparator({
                    id: "menuItemSeparator"
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemReload",
                    label: message.get("tree.reload"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.reloadSubTree)
                }));
                menuDataTree.addChild(new MenuSeparator());
                if (UtilQA.isQAActive() && !UtilSecurity.isCurrentUserQA()) {
                    menuDataTree.addChild(new MenuItem({
                        id: "menuItemDetach",
                        label: message.get("tree.nodeMarkDeleted"),
                        onClick: lang.hitch(menuEventHandler, menuEventHandler.handleMarkDeleted)
                    }));
                } else {
                    menuDataTree.addChild(new MenuItem({
                        id: "menuItemDelete",
                        label: message.get("tree.nodeDelete"),
                        onClick: lang.hitch(menuEventHandler, menuEventHandler.handleDelete)
                    }));
                }
                menuDataTree.addChild(new MenuSeparator({
                    id: "menuItemSeparatorPublicationCondition"
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemPublicationCondition1",
                    label: message.get("tree.publicationInternet"),
                    onClick: lang.hitch(menuEventHandler, lang.partial(menuEventHandler.changePublicationCondition, 1))
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemPublicationCondition2",
                    label: message.get("tree.publicationIntranet"),
                    onClick: lang.hitch(menuEventHandler, lang.partial(menuEventHandler.changePublicationCondition, 2))
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemPublicationCondition3",
                    label: message.get("tree.publicationInternal"),
                    onClick: lang.hitch(menuEventHandler, lang.partial(menuEventHandler.changePublicationCondition, 3))
                }));
                menuDataTree.addChild(new MenuItem({
                    id: "menuItemInheritAddress",
                    label: message.get("tree.inheritAddress"),
                    onClick: lang.hitch(menuEventHandler, menuEventHandler.inheritAddressToChildren)
                }));

                // connect the special menu for the root node
                // aspect.before(menuDataTree, "_openMyself", lang.hitch(this, function(e) {
                    
                // }));

                this.menu = menuDataTree;
                var dataTree = registry.byId("dataTree");
                //on(dataTree, "Click", ingridDataTreeHandler.onClick);
                //on(dataTree, "MouseOver", ingridDataTreeHandler.onMouseOver);

                // enable multi-selection of tree nodes for group operations
                dataTree.multiSelect = true;

                aspect.after(IgeActions, "onAfterLoad", lang.hitch(dataTree, function() {
                    this._markLoadedNode();
                }));
            },


            /******************** *************************************
             * HANDLER
             *********************************************************/

            clickHandler: function(item, node, oEvent) {
                // don't do anything if already selected node is clicked again
                //if (this.selectedNode.id == node.id)
                //    return;

                var doLoad = function() {
                    var deferred = new Deferred();
                    console.debug("Publishing event: /loadRequest(" + item.id + ", " + item.nodeAppType + ")");

                    deferred.then(function() {
                        console.debug("resize border container");
                        registry.byId("dataFormContainer").resize();

                    }, function(err) {
                        displayErrorMessage(err);
                    });

                    topic.publish("/loadRequest", {
                        id: item.id,
                        appType: item.nodeAppType,
                        node: item,
                        resultHandler: deferred
                    });

                    return deferred.promise;
                };

                // check for multiple selection mode (with CTRL-key)
                if (oEvent.ctrlKey === true || oEvent.shiftKey === true) {

                } else {
                    if (IgeActions.hasUnsavedChanged()) {
                        // select previous selected node to 
                        UtilTree.selectNode("dataTree", this.lastFocusedNode.item.id);
                        IgeActions.showUnsavedChangesDialog().then(function() {
                            // continue to the clicked object
                            //var widget = self.lastFocusedNode;
                            //UtilTree.selectNode("dataTree", widget.item.id);
                            UtilTree.selectNode("dataTree", item.id);
                            doLoad();
                        }, function() {
                            // cancel and stay where we are
                        });
                    } else {
                        doLoad();
                    }
                    
                }
            },

            mouseDownHandler: function(self, evt){
                if(evt.button==2){ // right-click
                    var node = registry.getEnclosingWidget(evt.target);
                    self._contextMenuPreparer(node);
                }
                this.lastFocusedNode = this.selectedNode;
            },

            mouseOverHandler: function(oEvent) {
                if (oEvent.ctrlKey === true) {
                    this.domNode.style.cursor = "pointer";
                } else {
                    this.domNode.style.cursor = "default";
                }
            },

            _contextMenuPreparer: function(clickedNode) {
                // get a hold of, and log out, the tree node that was the source of this open event
                // var tn = registry.getEnclosingWidget(e.target);
                this.menu.clickedNode = clickedNode;
                var self = this;

                var enablePaste, multiSelection, isAddressNode;
                enablePaste = multiSelection = isAddressNode = false;
                var dataTree = registry.byId("dataTree");

                // 1) keep multi selection if clicked on one selected node
                // 2) change selection to only clicked node if it isn't selected
                var clickedNodeIsSelected = array.some(dataTree.selectedNodes, function(node) {
                    return node.id == self.menu.clickedNode.id;
                });
                if (!clickedNodeIsSelected) {
                    // dataTree.resetSelection(this.menu.clickedNode);
                    dataTree.set("selectedNodes", [clickedNode]);
                }

                // allow/deny paste
                if (dataTree.canPaste(clickedNode.item))
                    enablePaste = true;

                // menu for multiple selected nodes
                if (dataTree.selectedNodes.length > 1)
                    multiSelection = true;

                if (!multiSelection && clickedNode.item.nodeAppType == "A")
                    isAddressNode = true;

                if (clickedNode.item.id == "objectRoot" || clickedNode.item.id == "addressRoot" || clickedNode.item.id == "addressFreeRoot") {
                    // contrived condition: if this tree node doesn't have any children, disable all of the menu items
                    array.forEach(this.menu.getChildren(), function(i) {
                        if (i.id == "menuItemNew" || i.id == "menuItemPaste" ||
                            i.id == "menuItemReload" || i.id == "menuItemSeparator") {
                            domClass.remove(i.domNode, "hidden");
                            if (i.id != "menuItemSeparator") i.setDisabled(false);
                        } else {
                            domClass.add(i.domNode, "hidden");
                        }
                        if (i.id == "menuItemPaste") {
                            enablePaste ? i.setDisabled(false) : i.setDisabled(true);
                        }
                    });
                } else { // for all child nodes of the objects and addresses

                    array.forEach(this.menu.getChildren(), function(i) {
                        if (i.id == "menuItemReload" || i.id == "menuItemSeparator" || (!isAddressNode && i.id == "menuItemInheritAddress")) {
                            domClass.add(i.domNode, "hidden");
                            return;
                        } else {
                            domClass.remove(i.domNode, "hidden");
                        }

                        //console.debug("menuitem id: " + i.id);
                        if (i.isInstanceOf(MenuSeparator)) {
                            return;
                        }
                        if (clickedNode.item.nodeAppType == "A" && (clickedNode.item.objectClass == 2 || clickedNode.item.objectClass == 3)) {
                            if (i.id == "menuItemNew")
                                i.setDisabled(true);
                            else
                                i.setDisabled(false);
                        } else {
                            i.setDisabled(false);
                        }
                        if (i.id == "menuItemPaste")
                            enablePaste ? i.setDisabled(false) : i.setDisabled(true);

                        if (clickedNode.item.userWriteTreePermission && (clickedNode.item.userWriteTreePermission === false)) {
                            if (i.id == "menuItemCut")
                                i.setDisabled(true);

                            if (!clickedNode.item.userWriteSubTreePermission && !clickedNode.item.userWriteSubNodePermission && (i.id == "menuItemNew" || i.id == "menuItemPaste")) {
                                console.debug(clickedNode.item);
                                i.setDisabled(true);
                            }
                        }

                        if (!clickedNode.item.userMovePermission && (i.id == "menuItemMove" || i.id == "menuItemCut" || i.id == "menuItemDetach")) {
                            i.setDisabled(true);
                        }

                        if (!isAddressNode) {
                            if (clickedNode.item.publicationCondition && clickedNode.item.publicationCondition) {
                                if (i.id.indexOf("PublicationCondition") != -1) {
                                    domClass.remove(i.domNode, "hidden");
                                    i.setDisabled(false);
                                }
                                if (i.id == "menuItemPublicationCondition" + clickedNode.item.publicationCondition) {
                                    i.setDisabled(true);
                                }
                            } else {
                                if (i.id.indexOf("PublicationCondition") != -1) {
                                    domClass.add(i.domNode, "hidden");
                                }
                            }
                        } else {
                            if (i.id.indexOf("PublicationCondition") != -1) {
                                domClass.add(i.domNode, "hidden");
                            }
                        }

                        if (i.id == "menuItemInheritAddress" && clickedNode.item.isFolder !== true) {
                            i.setDisabled(true);
                        }

                        // only enable these menu entries on multi selection
                        if (multiSelection) {
                            if (i.id == "menuItemMove" || i.id == "menuItemCut" || i.id == "menuItemDelete" ||
                                i.id == "menuItemCopy" || i.id == "menuItemCopySingle" || i.id == "menuItemDetach") {
                                // do not modify state, since they already should have the right one!
                            } else {
                                // disable all other entries
                                i.setDisabled(true);
                            }
                        }
                    });
                }
            }

        })();

    });