/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/topic",
    "dijit/form/Button",
    "dijit/registry",
    "dijit/MenuItem",
    "dijit/MenuSeparator",
    "ingrid/layoutCreator",
    "ingrid/dialog",
    "ingrid/hierarchy/dirty",
    "ingrid/message",
    "ingrid/menu",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
], function (declare, array, lang, domClass, construct, topic, Button, registry, MenuItem, MenuSeparator, layoutCreator, dialog, dirty, message, menu, Editors, Formatters) {

    return declare(null, {
        title: "Unterstützung der Erfassung von 'Geometry Context' in Geodatensätzen",
        description: "Ermöglicht die Erfassung von 'Geometry Context' Informationen für einen Datensatz",
        defaultActive: false,
        // type: "SYSTEM",
        category: "Nokis",
        run: function () {
            var self = this;

            this.createNokisContextMenus();
            this.createFields();

            topic.subscribe("/onObjectClassChange", function (data) {
                if (data.objClass === "Class1") {
                    domClass.remove("uiElementAddgeometryContext", "hide");
                } else {
                    domClass.add("uiElementAddgeometryContext", "hide");
                }
            });

            topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
                self.validate(notPublishableIDs);
            });

        },

        createFields: function () {

            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var id = "geometryContext";
            var geometryContextStructure = [
                {
                    field: 'geometryType',
                    name: message.get("nokis.form.geometryContext.geometryType") + "*",
                    width: '100px',
                    editable: true
                },
                {field: 'name', name: message.get("nokis.form.geometryContext.name") + "*", width: '100px', editable: true},
                {
                    field: 'featureType',
                    name: message.get("nokis.form.geometryContext.featureType") + "*",
                    width: '100px',
                    editable: true,
                    type: Editors.SelectboxEditor,
                    options: ["nominal", "ordinal", "skalar", "sonstiges"],
                    values: ["nominal", "ordinal", "scalar", "other"],
                    formatter: Formatters.ListCellFormatter
                },
                {
                    field: 'dataType',
                    name: message.get("nokis.form.geometryContext.dataType") + "*",
                    width: '130px',
                    editable: true
                },
                {
                    field: 'description',
                    name: message.get("nokis.form.geometryContext.description") + "*",
                    width: 'auto',
                    editable: true
                },
                {field: 'min', hidden: true},
                {field: 'max', hidden: true},
                {field: 'unit', hidden: true},
                {field: 'attributes', hidden: true}
            ];

            var table = layoutCreator.createDomDataGrid({
                id: id,
                name: message.get("nokis.form.geometryContext"),
                help: message.get("nokis.form.geometryContext.help"),
                contextMenu: "NOKIS_GEOMETRYCONTEXT_PARAMETER",
                isMandatory: true,
                style: "width: 100%"
            }, geometryContextStructure, "refClass1");
            construct.place(table, "ref1VFormat", "after");

            newFieldsToDirtyCheck.push(id);
            var tableWidget = registry.byId(id);
            tableWidget.reinitLastColumn(true);
            additionalFields.push(tableWidget);

            var linkContainer = construct.create("span", {
                "class": "functionalLink",
                innerHTML: "<img src='img/ic_fl_popup.gif' width='10' height='9' alt='Popup' />"
            }, tableWidget.domNode.parentNode, "before");

            construct.create("a", {
                innerHTML: message.get("nokis.form.button.addGeometryContext"),
                style: {
                    cursor: "pointer"
                },
                onclick: lang.hitch(this, function () {
                    dialog.showPage(message.get("nokis.dialog.geometryContext.nominal"), "dialogs/mdek_nokis_geometryContext_dialog.jsp", 800, 300, true, {
                        gridId: "geometryContext"
                    });
                })
            }, linkContainer);

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

        },

        createNokisContextMenus: function () {
            var type = "NOKIS_GEOMETRYCONTEXT_PARAMETER";
            var contextMenu = menu.initContextMenu({id: "geometryContext", contextMenu: type});
            contextMenu.addChild(new MenuSeparator());
            contextMenu.addChild(new MenuItem({
                id: "menuEditClicked_" + type,
                label: message.get('nokis.contextmenu.geometryContext'),
                onClick: function () {
                    var rowData = clickedSlickGrid.getData()[clickedRow];
                    var dialogData = {
                        gridId: clickedSlickGridProperties.id,
                        selectedRow: rowData,
                        rowIndex: clickedRow
                    };
                    dialog.showPage(message.get("nokis.dialog.geometryContext.nominal"), 'dialogs/mdek_nokis_geometryContext_dialog.jsp?c=' + userLocale, 800, 300, true, dialogData);
                }
            }));
        },

        validate: function (notPublishableIDs) {
            var table = registry.byId("geometryContext");
            var hasIncompleteRows = false;

            array.forEach(table.getData(), function (row) {
                var isComplete = array.every(table.columns, function(col) {
                    var columnIsNotEmpty = row[col.field] !== null && row[col.field] !== undefined && lang.trim(row[col.field] + "") !== "";
                    return col.hidden || columnIsNotEmpty;
                });
                if (!isComplete) {
                    hasIncompleteRows = true;
                }

                if (row.featureType === "scalar" && !row.unit) {
                    notPublishableIDs.push(["geometryContext", "Ein skalares Element benötigt eine Einheit! Bitte Zeile bearbeiten."])
                }
            });

            if (hasIncompleteRows) {
                notPublishableIDs.push(["geometryContext", "Zu jedem Kontext müssen alle Pflichtspalten ausgefüllt sein"])
            }
        }
    })();
})
