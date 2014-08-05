/*********************************************************
 * OBJECT
 *********************************************************/

define([
    "dojo/_base/declare",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/on",
    "dojo/aspect",
    "dojo/topic",
    "dojo/window",
    "dojo/Deferred",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dijit/registry",
    "dijit/Menu",
    "dijit/MenuItem",
    "dijit/MenuBar",
    "dijit/MenuBarItem",
    "dijit/MenuSeparator",
    "dijit/PopupMenuBarItem",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/QA",
    "ingrid/utils/Security",
    "ingrid/utils/UI",
    "ingrid/utils/Grid"
], function(declare, dom, domClass, on, aspect, topic, wnd, Deferred, lang, array, registry, Menu, MenuItem, MenuBar, MenuBarItem, MenuSeparator, PopupMenuBarItem, message, dialog, UtilQA, UtilSecurity, UtilUI, UtilGrid) {
    return declare(null, {

        selectChild: function(child) {
            // set label of menu item to show where we are
            dom.byId("currentPageName").innerHTML = this.getMenuString(child);

            // switch to menu page
            registry.byId("stackContainer").selectChild(registry.byId(child));
        },

        getMenuString: function(entry) {
            return message.get("general.page") + ": " + message.get("page.breadcrumb." + entry);
        },

        create: function(pane) {
            var menu = new MenuBar({}).placeAt(pane.domNode);
            var self = this;
            menu.addChild(new MenuBarItem({
                id: "menuPageDashboard",
                label: "<div class='image18px iconDashboard'></div>",
                onClick: function() {
                    self.selectChild("pageDashboard");
                }
            }));
            menu.addChild(new MenuBarItem({
                id: "menuPageHierarchy",
                label: message.get("menu.main.hierarchyAcquisition"),
                onClick: function() {
                    self.selectChild("pageHierarchy");
                }
            }));

            var researchMenu = new Menu({});
            researchMenu.addChild(new MenuItem({
                id: "menuPageResearch",
                label: message.get("menu.main.research.search"),
                onClick: function() {
                    self.selectChild("pageResearch");
                }
            }));
            researchMenu.addChild(new MenuItem({
                id: "menuPageResearchThesaurus",
                label: message.get("menu.main.research.thesaurusNavigator"),
                onClick: function() {
                    self.selectChild("pageResearchThesaurus");
                }
            }));
            researchMenu.addChild(new MenuItem({
                id: "menuPageResearchDB",
                label: message.get("menu.main.research.databaseSearch"),
                onClick: function() {
                    self.selectChild("pageResearchDB");
                }
            }));
            menu.addChild(new dijit.PopupMenuBarItem({
                id: "submenuResearch",
                label: message.get("menu.main.research"),
                popup: researchMenu
            }));

            var qualityMenu = new Menu({});
            qualityMenu.addChild(new MenuItem({
                id: "menuPageQualityEditor",
                label: message.get("menu.main.qualityAssurance.editor"),
                onClick: function() {
                    self.selectChild("pageQualityEditor");
                }
            }));
            if (UtilQA.isQAActive() && UtilSecurity.isCurrentUserQA()) {
                qualityMenu.addChild(new MenuItem({
                    id: "menuPageQualityAssurance",
                    label: message.get("menu.main.qualityAssurance.qa"),
                    onClick: function() {
                        self.selectChild("pageQualityAssurance");
                    }
                }));
            }

            menu.addChild(new dijit.PopupMenuBarItem({
                id: "submenuQualityAssurance",
                label: message.get("menu.main.qualityAssurance"),
                popup: qualityMenu
            }));

            menu.addChild(new MenuBarItem({
                id: "menuPageStatistics",
                label: message.get("menu.main.statistics"),
                onClick: function() {
                    self.selectChild("pageStatistics");
                }
            }));

            //---------------------------------------
            // add the Administration with submenus
            //---------------------------------------

            // a separator
            menu.addChild(new MenuBarItem({
                label: "|",
                disabled: true,
                style: "padding:0 3px;"
            }));

            //---------------------------------------
            var adminMenuCatalog = new Menu({});
            adminMenuCatalog.addChild(new MenuItem({
                id: "menuPageCatalogSettings",
                label: message.get("menu.admin.main.catalog.settings"),
                onClick: function() {
                    self.selectChild("catalogSettings");
                }
            }));
            adminMenuCatalog.addChild(new MenuItem({
                id: "menuPageGeneralSettings",
                label: message.get("menu.admin.main.catalog.generalSettings"),
                onClick: function() {
                    self.selectChild("generalSettings");
                }
            }));

            //---------------------------------------
            var adminMenuUserManagement = new Menu({});
            adminMenuUserManagement.addChild(new MenuItem({
                id: "menuPageUserManagement",
                label: message.get("menu.admin.main.user.userAdmin"),
                onClick: function() {
                    self.selectChild("userManagement");
                }
            }));
            adminMenuUserManagement.addChild(new MenuItem({
                id: "menuPageGroupManagement",
                label: message.get("menu.admin.main.user.groupAdmin"),
                onClick: function() {
                    self.selectChild("groupManagement");
                }
            }));
            adminMenuUserManagement.addChild(new MenuItem({
                id: "menuPagePermissionOverview",
                label: message.get("menu.admin.main.user.permissions"),
                onClick: function() {
                    self.selectChild("permissionOverview");
                }
            }));

            //---------------------------------------
            var adminMenuCatManagement = new Menu({});
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminAnalysis",
                label: message.get("menu.admin.main.management.analysis"),
                onClick: function() {
                    self.selectChild("adminAnalysis");
                }
            }));
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminDublet",
                label: message.get("menu.admin.main.management.duplicates"),
                onClick: function() {
                    self.selectChild("adminDublet");
                }
            }));
            adminMenuCatManagement.addChild(new MenuItem({
                label: message.get("menu.admin.main.management.urls"),
                id: "menuPageAdminURL",
                onClick: function() {
                    self.selectChild("adminURL");
                }
            }));
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminCodeLists",
                label: message.get("menu.admin.main.management.codelists"),
                onClick: function() {
                    self.selectChild("adminCodeLists");
                }
            }));
            /*adminMenuCatManagement.addChild(new MenuItem({
                label: "Zusätzliche Felder",
				onClick: function(){
					self.selectChild("adminAdditionalFields");
				}	
            }));*/
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminFormFields",
                label: message.get("menu.admin.main.management.additionalFields"),
                onClick: function() {
                    self.selectChild("adminFormFields");
                }
            }));
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminDeleteAddress",
                label: message.get("menu.admin.main.management.deleteAddress"),
                onClick: function() {
                    self.selectChild("adminDeleteAddress");
                }
            }));
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminSearchTerms",
                label: message.get("menu.admin.main.management.searchTerms"),
                onClick: function() {
                    self.selectChild("adminSearchTerms");
                }
            }));
            adminMenuCatManagement.addChild(new MenuItem({
                id: "menuPageAdminLocations",
                label: message.get("menu.admin.main.management.spatialSearchTerms"),
                onClick: function() {
                    self.selectChild("adminLocations");
                }
            }));

            //---------------------------------------
            var adminMenuImport = new Menu({});
            adminMenuImport.addChild(new MenuItem({
                id: "menuPageAdminExport",
                label: message.get("menu.admin.main.importExport.export"),
                onClick: function() {
                    self.selectChild("adminExport");
                }
            }));
            adminMenuImport.addChild(new MenuItem({
                id: "menuPageAdminImport",
                label: message.get("menu.admin.main.importExport.import"),
                onClick: function() {
                    self.selectChild("adminImport");
                }
            }));

            //menu.addChild(new MenuSeparator());

            // only show all menus to mdek admin!
            if (UtilSecurity.currentUser.role == 1) {
                menu.addChild(new dijit.PopupMenuBarItem({
                    id: "menuPageAdminMenuCatalog",
                    label: message.get("menu.admin.main.catalog"),
                    popup: adminMenuCatalog
                }));
            }

            // no user management for Authors
            if (UtilSecurity.currentUser.role != 3) {
                menu.addChild(new dijit.PopupMenuBarItem({
                    id: "menuPageAdminMenuUserManagement",
                    label: message.get("menu.admin.main.user"),
                    popup: adminMenuUserManagement
                }));
            }

            // only all menus for cat-admin
            if (UtilSecurity.currentUser.role == 1) {
                menu.addChild(new dijit.PopupMenuBarItem({
                    id: "menuPageAdminMenuCatManagement",
                    label: message.get("menu.admin.main.management"),
                    popup: adminMenuCatManagement
                }));

                menu.addChild(new dijit.PopupMenuBarItem({
                    id: "menuPageAdminMenuImport",
                    label: message.get("menu.admin.main.importExport"),
                    popup: adminMenuImport
                }));
            }
        },


        // ------------------------- Context Menus -------------------------


        // holds all different context menus
        contextMenu: {},

        initContextMenu: function(gridProperties) {
            var type = gridProperties.contextMenu;
            var menu = new Menu({
                id: "contextMenu_" + type
            });

            var self = this;
            if (type == "DUPLICATE_GRID") {
                menu.addChild(new MenuItem({
                    id: "menuShowInTree_" + type,
                    label: message.get('contextmenu.table.showInTree'),
                    onClick: pageDuplicates.selectObjectInTree // pageDuplicates is a global var
                }));
                menu.addChild(new MenuItem({
                    id: "menuJumpToObject_" + type,
                    label: message.get('contextmenu.table.jumpToObject'),
                    onClick: function() {
                        self.handleSelectNodeInTree(clickedSlickGrid.getDataItem(clickedRow).uuid, "O");
                    }
                }));
            } else {
                menu.addChild(new MenuItem({
                    id: "menuSelectAll_" + type,
                    label: message.get('contextmenu.table.selectAll'),
                    onClick: function() {
                        var rows = [];
                        var length = clickedSlickGrid.getData().length;
                        for (var i = 0; i < length; i++) {
                            rows.push(i);
                        }
                        clickedSlickGrid.setSelectedRows(rows);
                    }
                }));

                menu.addChild(new MenuItem({
                    id: "menuDeselectAll_" + type,
                    label: message.get('contextmenu.table.deselectAll'),
                    onClick: function() {
                        clickedSlickGrid.setSelectedRows([]);
                    }
                }));

                menu.addChild(new MenuSeparator());

                menu.addChild(new MenuItem({
                    id: "menuRemoveSelected_" + type,
                    label: message.get('contextmenu.table.removeSelected'),
                    onClick: function() {
                        UtilGrid.removeTableDataRow(clickedSlickGrid.id, clickedSlickGrid.getSelectedRows());
                        clickedSlickGrid.setSelectedRows([]);
                    }
                }));

                menu.addChild(new MenuItem({
                    id: "menuRemoveClicked_" + type,
                    label: message.get('contextmenu.table.removeClicked'),
                    onClick: function() {
                        UtilGrid.removeTableDataRow(clickedSlickGrid.id, [clickedRow]);
                    }
                }));

                // connect the special menu for the root node
                aspect.after(menu, "_openMyself", function(result, args) {
                    var e = args[0];
                    var findGrid = function(element) {
                        while (element) {
                            if (domClass.contains(element, "ui-widget")) {
                                return element.id;
                            }
                            element = element.parentNode;
                        }
                    };

                    // all items have to be disabled if user has no permission if hierarchy page is used!
                    var isHierarchyPage = registry.byId("stackContainer").selectedChildWidget.id == "pageHierarchy";

                    if (!isHierarchyPage || currentUdk.writePermission) {
                        var gridId = findGrid(e.target);
                        var grid = UtilGrid.getTable(gridId);
                        var somethingIsSelected = UtilGrid.getSelectedRowIndexes(gridId).length > 0;
                        var somethingIsCopied = grid.copiedAddress !== null && grid.copiedAddress !== undefined;

                        array.forEach(menu.getChildren(), function(i) {
                            // reset item to active first!
                            if (!(i instanceof MenuSeparator)) i.set("disabled", false);

                            if (!somethingIsSelected) {
                                if (i.id.indexOf("menuDeselectAll") != -1) i.set("disabled", true);
                                if (i.id.indexOf("menuRemoveSelected") != -1) i.set("disabled", true);
                            }
                            if (!clickedSlickGrid.getData()[clickedRow]) {
                                if (i.id.indexOf("menuRemoveClicked") != -1) i.set("disabled", true);
                                if (i.id.indexOf("menuEditClicked") != -1) i.set("disabled", true);
                                if (i.id.indexOf("menuShowAddress") != -1) i.set("disabled", true);
                                if (i.id.indexOf("menuCopyAddress") != -1) i.set("disabled", true);
                            }
                            if (!somethingIsCopied) {
                                if (i.id.indexOf("menuPasteAddress") != -1) i.set("disabled", true);
                            }
                        });
                    } else {
                        array.forEach(menu.getChildren(), function(i) {
                            if (i.declaredClass != "MenuSeparator")
                                i.set("disabled", true);
                        });
                    }
                });
            }

            if (type == "GENERAL_ADDRESS") {
                menu.addChild(new MenuSeparator());
                menu.addChild(new MenuItem({
                    id: "menuCopyAddress_" + type,
                    label: message.get('contextmenu.table.copyAddress'),
                    onClick: function() {
                        var rowData = clickedSlickGrid.getData()[clickedRow];
                        clickedSlickGrid.copiedAddress = rowData;
                        console.debug(clickedSlickGrid.copiedAddress);
                        //dialog.showPage('Adresse', 'dialogs/mdek_address_preview_dialog.html', 500, 240, false, { data:rowData });
                    }
                }));
                menu.addChild(new MenuItem({
                    id: "menuPasteAddress_" + type,
                    label: message.get('contextmenu.table.pasteAddress'),
                    onClick: function() {
                        var rowData = clickedSlickGrid.getData()[clickedRow];
                        console.debug(rowData);
                        // make a copy of the address which is going to be copied
                        var copyClone = lang.clone(clickedSlickGrid.copiedAddress);
                        // ignore role if address is being replaced
                        if (rowData) {
                            // remember role first
                            var type = rowData.nameOfRelation;
                            UtilGrid.updateTableDataRow(clickedSlickGrid.id, clickedRow, copyClone);
                            UtilGrid.updateTableDataRowAttr(clickedSlickGrid.id, clickedRow, "nameOfRelation", type);
                        } else {
                            UtilGrid.addTableDataRow(clickedSlickGrid.id, copyClone);
                        }
                    }
                }));
                menu.addChild(new MenuSeparator());
                menu.addChild(new MenuItem({
                    id: "menuShowAddress_" + type,
                    label: message.get('contextmenu.table.showAddress'),
                    onClick: function() {
                        var rowData = clickedSlickGrid.getData()[clickedRow];
                        console.debug(rowData);
                        dialog.showPage('Adresse', 'dialogs/mdek_address_preview_dialog.html', 500, 240, false, {
                            data: rowData
                        });
                    }
                }));
            }

            if (type == "EDIT_OPERATION") {
                menu.addChild(new MenuSeparator());
                menu.addChild(new MenuItem({
                    id: "menuEditClicked_" + type,
                    label: message.get('contextmenu.table.editClicked'),
                    onClick: function() {
                        var rowData = clickedSlickGrid.getData()[clickedRow];
                        var dialogData = {
                            gridId: clickedSlickGridProperties.id,
                            selectedRow: rowData
                        };
                        dialog.showPage(message.get("dialog.operations.title"), 'dialogs/mdek_operation_dialog.jsp?c=' + userLocale, 735, 745, true, dialogData);
                    }
                }));
            }

            if (type == "EDIT_LINK") {
                menu.addChild(new MenuSeparator());
                menu.addChild(new MenuItem({
                    id: "menuEditClicked_" + type,
                    label: message.get('contextmenu.table.editClicked'),
                    onClick: function() {
                        console.debug(clickedSlickGridProperties);
                        if (clickedSlickGridProperties.id == "ref1ServiceLink") {
                            dialog.show( message.get( "dialog.general.info" ), message.get( "dialog.cannot.modify.table" ), dialog.INFO );
                            return;
                        }
                        var rowData = clickedSlickGrid.getData()[clickedRow];
                        self.openLinkDialog({
                            gridId: clickedSlickGridProperties.id,
                            filter: clickedSlickGridProperties.relation_filter,
                            selectedRow: rowData
                        });
                    }
                }));
            }

            this.contextMenu[type] = menu;
            return menu;
        },

        _getClickedRowFromTarget: function(target, iteration) {
            var attr = target.parentNode.getAttribute("row");
            if (attr === null && iteration > 0)
                return this._getClickedRowFromTarget(target.parentNode, iteration - 1);
            return attr;
        },

        createContextMenu: function(grid, gridId, gridProperties) {
            var type = gridProperties.contextMenu;
            if (type === undefined)
                type = gridProperties.contextMenu = "DEFAULT";
            else if (type == "none") {
                return;
            }

            var self = this;
            on(grid.domNode, "contextmenu", lang.hitch(grid, function(evt) {
                evt.preventDefault();
                //var cell = this.getCellFromEvent(e);
                clickedRow = self._getClickedRowFromTarget(evt.target, 5); // look for row property within next 5 parents of target
                clickedSlickGrid = this; // needed for event when menuitem was clicked
                clickedSlickGridProperties = gridProperties;
            }));

            var m = this.contextMenu[type];
            if (m === undefined) {
                console.debug("create new context menu");
                m = this.initContextMenu(gridProperties);
            }
            m.bindDomNode(dom.byId(gridId));
        },

        // Selects and loads a node in the tree. The path to the node is expanded step by step.
        handleSelectNodeInTree: function(nodeId, nodeAppType/*, formIdJump*/) {
            var defNodeLoaded = new Deferred();
            require("ingrid/menu").selectChild("pageHierarchy");

            if (!UtilUI.isContainerNodeId(nodeId) && !UtilUI.isNewNodeId(nodeId)) {
                var self = this;
                // Get the path to the node depending on its type
                var getPathDef = new Deferred();
                var rootNodes = ["ObjectsAndAddressesRoot"];
                if (nodeAppType == "O") {
                    topic.publish("/getObjectPathRequest", {
                        id: nodeId,
                        resultHandler: getPathDef
                    });
                    rootNodes.push("objectRoot");

                } else if (nodeAppType == "A") {
                    topic.publish("/getAddressPathRequest", {
                        id: nodeId,
                        resultHandler: getPathDef
                    });
                    rootNodes.push("addressRoot");
                }

                // Expand the nodes along the path.
                //getPathDef.then(dojo.lang.curry(menuEventHandler, menuEventHandler._expandPath, nodeAppType));
                //getPathDef.then(dojo.partial(menuEventHandler._expandPath, nodeAppType));
                getPathDef.then(function(path) {
                    // wait for the page loaded
                    var def = new Deferred();
                    // first let the hierarchy-page load 
                    registry.byId("pageHierarchy").onLoadDeferred.promise.then(
                        function() {
                            // and then wait for the tree loaded
                            registry.byId("pageHierarchy").dataTreePromise.then(function() {
                                def.resolve(path);
                            });
                        });
                    return def.promise;
                })
                    .then(function(path) {
                        registry.byId("dataTree").set("paths", [rootNodes.concat(path)])
                            .then(function() {
                                self._loadNode(nodeId)
                                .then(defNodeLoaded.resolve);
                            }, function() {
                                // try free addresses!
                                registry.byId("dataTree").set("paths", [rootNodes.concat(["addressFreeRoot"].concat(path))])
                                    .then(function() {
                                        self._loadNode(nodeId)
                                        .then(defNodeLoaded.resolve);
                                    });
                            });

                    });

            }
            return defNodeLoaded;
        },

        // Loads a node with id 'nodeId'
        // The TreeNode with widgetId must exist before the node can be loaded
        _loadNode: function() {
            var node = registry.byId("dataTree").get("selectedNode");
            wnd.scrollIntoView(node.domNode);
            console.debug("Publishing event: /loadRequest(" + node.item.id + ", " + node.item.nodeAppType + ")");
            var deferred = new Deferred();
            topic.publish("/loadRequest", {
                id: node.item.id,
                appType: node.item.nodeAppType,
                node: node.item,
                resultHandler: deferred
            });
            return deferred.then(function() {
                registry.byId("dataFormContainer").resize();
            });
        },

        // called now via context menu
        openLinkDialog: function(dialogData) {
            dialog.showPage(message.get("dialog.links.title.edit"), 'dialogs/mdek_links_dialog.jsp?c=' + userLocale, 1010, 680, true, dialogData);
        }

    })();
});