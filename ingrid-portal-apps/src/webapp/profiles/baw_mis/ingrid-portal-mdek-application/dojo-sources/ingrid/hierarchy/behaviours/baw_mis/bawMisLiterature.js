/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
    "ingrid/utils/Store",
    "ingrid/utils/Syslist"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, Deferred, dom, domClass, construct, all, topic,
            Editors, Formatters, dirty, creator, menu, message, UtilGeneral, UtilStore, UtilSyslist) {

    const ROLE_CODE_SYSLIST = 505;

    return declare(null, {
        title: "Literatur und Querverweise",
        description: "Literatur und Querverweise",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // Authors table
            var authorsTableId = this._initObjClass2AuthorsTable();
            this._addAuthorsTableValidationRules();

            newFieldsToDirtyCheck.push(authorsTableId);
            additionalFields.push(registry.byId(authorsTableId));
            registry.byId(authorsTableId).reinitLastColumn(true);


            // Publisher
            var publisherTextboxId = this._initPublisherTextbox();
            this._addPublisherValidationRules();

            newFieldsToDirtyCheck.push(publisherTextboxId);
            additionalFields.push(registry.byId(publisherTextboxId));


            // Cross-references
            var literatureTableId = this._initLiteratureXrefTable();
            newFieldsToDirtyCheck.push(literatureTableId);
            additionalFields.push(registry.byId(literatureTableId));
            registry.byId(literatureTableId).reinitLastColumn(true);

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

            return registry.byId(authorsTableId).promiseInit;
        },

        _initObjClass2AuthorsTable: function() {
            var id = "bawLiteratureAuthorsTable";
            var structure = [
                {
                    field: "authorGivenName",
                    name: message.get("ui.obj.baw.literature.author.table.column.given.name"),
                    type: Editors.TextCellEditor,
                    editable: true,
                    isMandatory: false,
                    width: "230px"
                },
                {
                    field: "authorFamilyName",
                    name: message.get("ui.obj.baw.literature.author.table.column.family.name"),
                    type: Editors.TextCellEditor,
                    editable: true,
                    isMandatory: false,
                    width: "230px"
                },
                {
                    field: "authorOrganisation",
                    name: message.get("ui.obj.baw.literature.author.table.column.organisation"),
                    type: Editors.TextCellEditor,
                    editable: true,
                    isMandatory: false,
                    width: "auto"
                }
            ];
            creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.baw.literature.author.table.title"),
                help: message.get("ui.obj.baw.literature.author.table.help"),
                style: "width: 100%"
            }, structure, "general");

            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class2") {
                    domClass.remove("uiElementAdd" + id, "hidden");
                } else {
                    domClass.add("uiElementAdd" + id, "hidden");
                }
            });

            return id;
        },

        _addAuthorsTableValidationRules: function() {
            var id = "bawLiteratureAuthorsTable";
            // Validation rules
            topic.subscribe("/onBeforeObjectPublish", function (notPublishableIDs) {
                // Check that persons have both given and last names defined
                var authorsData = registry.byId(id).data;

                var hasInvalidRows = array.some(authorsData, function (row) {
                    var hasGivenName = UtilGeneral.hasValue(row.authorGivenName);
                    var hasFamilyName = UtilGeneral.hasValue(row.authorFamilyName);

                    return (hasGivenName && !hasFamilyName)
                        || (!hasGivenName && hasFamilyName);
                })

                if (hasInvalidRows) {
                    notPublishableIDs.push([id, message.get("validation.baw.literature.authors.names")]);
                }

                // Check that at least one author is defined
                if (authorsData.length === 0) { // No authors in the custom table for authors
                    var authorEntryIdx = 11;
                    var authorEntryName = UtilSyslist.getSyslistEntryName(ROLE_CODE_SYSLIST, authorEntryIdx);

                    var addressTableId = "generalAddress";
                    var addressTableHasAuthors = array.some(registry.byId(addressTableId).data, function (row) {
                        return row.nameOfRelation === authorEntryName;
                    });

                    // No authors even in the addresses table
                    if (!addressTableHasAuthors) {
                        notPublishableIDs.push([id, message.get("validation.baw.literature.authors.count")]);
                    }
                }
            });
        },

        _initPublisherTextbox: function () {
            var id = "bawLiteraturePublisherTextbox";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("ui.obj.baw.literature.publisher.title"),
                    help: message.get("ui.obj.baw.literature.publisher.help"),
                    style: "width: 100%"
                }), "general");

            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class2") {
                    domClass.remove("uiElementAdd" + id, "hidden");
                } else {
                    domClass.add("uiElementAdd" + id, "hidden");
                }
            });

            return id;
        },

        _addPublisherValidationRules: function () {
            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                var publisherEntryIdx = 10;
                var publisherEntryName = UtilSyslist.getSyslistEntryName(ROLE_CODE_SYSLIST, publisherEntryIdx);

                var addressTableId = "generalAddress";
                var publishers = array.filter(registry.byId(addressTableId).data, function (row) {
                    return row.nameOfRelation === publisherEntryName;
                });

                var publisherTextboxId = "bawLiteraturePublisherTextbox";
                var publisherTextbox = registry.byId(publisherTextboxId);

                console.log(publishers);
                console.log(publisherCount);
                var publisherCount = publishers.length;
                if (UtilGeneral.hasValue(publisherTextbox.get("value"))) {
                    publisherCount++;
                    console.log(publisherCount);
                }

                if (publisherCount !== 1) {
                    var msg = message.get("validation.baw.literature.publishers");
                    notPublishableIDs.push([addressTableId, msg]);
                    notPublishableIDs.push([publisherTextboxId, msg]);
                }
            });
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
                if (data.objClass === "Class1") {
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

