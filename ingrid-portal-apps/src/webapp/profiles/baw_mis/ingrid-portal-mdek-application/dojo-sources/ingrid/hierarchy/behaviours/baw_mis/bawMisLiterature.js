/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dijit/MenuItem",
    "dijit/MenuSeparator",
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/Deferred",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/promise/all",
    "dojo/topic",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/menu",
    "ingrid/message",
    "ingrid/utils/General",
    "ingrid/utils/Store"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, Deferred, dom, domClass, construct, all, topic,
            Editors, Formatters, dirty, creator, menu, message, UtilGeneral, UtilStore) {

    const ROLE_CODE_SYSLIST = 505;

    return declare(null, {
        title: "Literatur und Querverweise",
        description: "Literatur und Querverweise",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            this._customiseTimeRef();
            this._createCustomFields();
        },

        _customiseTimeRef: function() {
            topic.subscribe("/onObjectClassChange", function(data) {

                var isNewItem = "newNode" === currentUdk.uuid;
                var publishArea = registry.byId("extraInfoPublishArea");

                var isLiterature = data.objClass === "Class2";
                if (isLiterature) {
                    domClass.remove("timeRef", "hide");

                    // Durch die Ressource abgedeckte Zeitspanne
                    domClass.add("uiElementN011", "hide");

                    if (isNewItem && publishArea) {
                        publishArea.set("value", "2"); // Intranet
                    }
                } else {
                    domClass.remove("uiElementN011", "hide");

                    if (isNewItem && publishArea) {
                        publishArea.set("value", "1"); // Internet
                    }
                }
            });

            this._addPublicationDateValidationRule();
        },

        _addPublicationDateValidationRule: function() {
            var id = "timeRefTable";
            // Validation rules
            topic.subscribe("/onBeforeObjectPublish", function (notPublishableIDs) {
                if (currentUdk.objectClass != 2) return;

                var publicationDates = array.filter(registry.byId(id).data, function (row) {
                    return row.type == 2;
                });

                // Check that exactly on publication date exists
                if (publicationDates.length !== 1) {
                    notPublishableIDs.push([id, message.get("validation.baw.literature.publication.date.count")]);
                }
            });
        },

        _createCustomFields: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];


            // Handles
            var handleTextboxId = this._initHandleTextbox();

            newFieldsToDirtyCheck.push(handleTextboxId);
            additionalFields.push(registry.byId(handleTextboxId));


            // Cross-references to literature objects in Geodata class
            var literatureTableId = this._initLiteratureXrefTable();
            newFieldsToDirtyCheck.push(literatureTableId);
            additionalFields.push(registry.byId(literatureTableId));
            registry.byId(literatureTableId).reinitLastColumn(true);

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

            return registry.byId(literatureTableId).promiseInit;
        },

        _initHandleTextbox: function () {
            var id = "bawLiteratureHandle";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("ui.obj.baw.literature.handle.title"),
                    help: message.get("ui.obj.baw.literature.handle.help"),
                    style: "width: 100%"
                }), "links");

            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class2") {
                    domClass.remove("uiElementAdd" + id, "hidden");
                } else {
                    domClass.add("uiElementAdd" + id, "hidden");
                }
            });

            return id;
        },

        _initLiteratureXrefTable: function() {
            var id = "bawLiteratureXrefTable";
            var structure = [
                {
                    field: "bawLiteratureNodeDocType",
                    editable: false,
                    formatter: Formatters.renderIconClass,
                    width: "23px",
                    hidden: false
                },
                {
                    field: "bawLiteratureXrefLink",
                    name: message.get("ui.obj.baw.literature.xref.table.row.title"),
                    editable: false,
                    width: "663px"
                },
                {
                    field: "bawLiteratureXrefUuid",
                    editable: false,
                    width: "0px",
                    hidden: true
                },
                {
                    field: "bawLiteratureXrefTitle",
                    editable: false,
                    width: "0px",
                    hidden: true
                }
            ];

            this._createLiteratureXrefTableContextMenu();
            var authorsTable = creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.baw.literature.xref.table.title"),
                help: message.get("ui.obj.baw.literature.xref.table.help"),
                contextMenu: "BAW_LITERATURE_CROSS_REFERENCE",
                visible: "optional",
                style: "width: 100%"
            }, structure, "links");

            var node = dom.byId("uiElementN017").parentElement;
            construct.place(authorsTable, node, 'before');

            // Add link for creating a new entry
            creator.createTableLink(
                id,
                message.get("ui.obj.baw.literature.xref.table.new.row"),
                'dialogs/mdek_baw_literature_xref_dialog.jsp',
                message.get('ui.obj.baw.literature.xref.table.new.row'),
                message.get('ui.obj.baw.literature.xref.table.new.row.tooltip')
            );

            topic.subscribe("/onObjectClassChange", function(data) {
                var isGeodata = data.objClass === "Class1";
                var isProject = data.objClass === "Class4";

                if (isGeodata || isProject) {
                    domClass.remove("uiElementAdd" + id, "hide");
                } else {
                    domClass.add("uiElementAdd" + id, "hide");
                }
            });

            topic.subscribe("/selectNode", function () {
                var table = registry.byId(id);
                if (!table) return;

                var promises = [];
                var data = table.data;

                array.forEach(data, function (row) {
                    var deferred = new Deferred();
                    promises.push(deferred);

                    var uuid = row.bawLiteratureXrefUuid;
                    if (uuid) {
                        ObjectService.getNodeData(uuid, "O", "true", {
                            callback: function (obj) {
                                row.bawLiteratureNodeDocType = obj.nodeDocType;
                                deferred.resolve();
                            },

                            errorHandler: function (err) {
                                console.error(err);
                                deferred.resolve();
                            }
                        });
                    }
                });

                all(promises).then(function () {
                    UtilStore.updateWriteStore(id, data);
                    table.reinitLastColumn(true);
                });
            });

            return id;
        },

        _createLiteratureXrefTableContextMenu: function () {
            var type = "BAW_LITERATURE_CROSS_REFERENCE";
            var contextMenu = menu.initContextMenu({contextMenu: type});
            contextMenu.addChild(new MenuSeparator());
            contextMenu.addChild(new MenuItem({
                id: "menuEditClicked_" + type,
                label: message.get('contextmenu.table.editClicked'),
                onClick: function () {
                    var rowData = clickedSlickGrid.getData()[clickedRow];
                    var dialogData = {
                        gridId: clickedSlickGridProperties.id,
                        selectedRow: rowData
                    };
                    dialog.showPage(message.get("ui.obj.baw.literature.xref.table.edit.row"), 'dialogs/mdek_baw_literature_xref_dialog.jsp?c=' + userLocale, 400, 300, true, dialogData);
                }
            }));
        }

    })();
});

