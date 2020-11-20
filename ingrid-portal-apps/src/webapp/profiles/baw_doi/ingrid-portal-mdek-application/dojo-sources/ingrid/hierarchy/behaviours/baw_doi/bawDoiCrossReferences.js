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

define([
    "dijit/MenuItem",
    "dijit/MenuSeparator",
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/topic",
    "ingrid/dialog",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/menu",
    "ingrid/message",
    "ingrid/utils/Store"
], function (MenuItem, MenuSeparator, registry, array, declare, lang, dom, domClass, construct, topic, dialog, Editors, Formatters, dirty, creator, menu, message, UtilStore) {

    return declare(null, {
        title: "Literaturverweise-Tabelle",
        description: "Tabelle für die Literaturverweise",
        defaultActive: true,
        category: "BAW-Datenrepository",

        run: function () {
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass == "Class1") {
                    domClass.remove("uiElementAdddoiCrossReferenceTable", "hide");
                } else {
                    domClass.add("uiElementAdddoiCrossReferenceTable", "hide");
                }
            });

            var promise = this._createDoiCrossReferenceTable();
            return promise;
        },

        _createDoiCrossReferenceTable: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var id = "doiCrossReferenceTable";
            var structure = [
                {
                    field: "doiCrossReferenceTitle",
                    name: message.get("ui.obj.doi.xref.table.row.title") + "*",
                    editable: false,
                    isMandatory: true,
                    width: "150px"
                },
                {
                    field: "doiCrossReferenceDate",
                    name: message.get("ui.obj.doi.xref.table.row.date") + "*",
                    type: Editors.DateCellEditor,
                    formatter: Formatters.DateCellFormatter,
                    editable: false,
                    isMandatory: true,
                    width: "75px"
                },
                {
                    field: "doiCrossReferenceAuthor",
                    name: message.get("ui.obj.doi.xref.table.row.author") + "*",
                    editable: false,
                    isMandatory: true,
                    width: "150px"
                },
                {
                    field: "doiCrossReferenceIdentifier",
                    name: message.get("ui.obj.doi.xref.table.row.identifier") + "*",
                    editable: false,
                    isMandatory: true,
                    width: "150px"
                },
                {
                    field: "doiCrossReferencePublisher",
                    name: message.get("ui.obj.doi.xref.table.row.publisher"),
                    editable: false,
                    isMandatory: false,
                    width: "auto"
                }
            ];

            this._createDoiCrossReferenceTableContextMenu();
            creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.doi.xref.table.title"),
                help: message.get("ui.obj.doi.xref.table.help"),
                contextMenu: "DOI_CROSS_REFERENCE",
                style: "width: 100%"
            }, structure, "links");
            this._createAppendDoiCrossReferenceLink();

            topic.subscribe("beforeFinishApplyingObjectNodeData", function (nodeData) {
                var xrefData = registry.byId(id).data;
                xrefData.forEach(function (row) {
                    var dt = row.doiCrossReferenceDate;
                    console.log(dt);
                    console.log(typeof dt);
                    if (typeof dt === "string" || dt instanceof String) {
                        row.doiCrossReferenceDate = new Date(parseInt(dt));
                    }
                });
                UtilStore.updateWriteStore(id, xrefData);
            });

            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            registry.byId(id).reinitLastColumn(true);

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(id).promiseInit;
        },

        _createDoiCrossReferenceTableContextMenu: function () {
            var type = "DOI_CROSS_REFERENCE";
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
                    dialog.showPage(message.get("dialog.doi.xref.title"), 'dialogs/mdek_doi_cross_reference_dialog.jsp?c=' + userLocale, 400, 300, true, dialogData);
                }
            }));
        },

        _createAppendDoiCrossReferenceLink: function () {
            var linkId = "doiCrossReferenceLink";
            var linkText = message.get("ui.obj.doi.xref.table.new.row");
            var linkOnClick = "require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('doiCrossReference'), 'dialogs/mdek_doi_cross_reference_dialog.jsp?c=' + userLocale, 400, 300, true, {});";

            var span = document.createElement("span");
            span.setAttribute("class", "functionalLink");

            var img = document.createElement("img");
            img.setAttribute("src", "img/ic_fl_popup.gif");
            img.setAttribute("width", "10");
            img.setAttribute("height", "9");
            img.setAttribute("alt", "Popup");

            var link = document.createElement("a");
            link.setAttribute("id", linkId);
            link.setAttribute("href", "javascript:void(0);");
            link.setAttribute("onclick", linkOnClick);
            link.setAttribute("title", linkText + " [Popup]");
            link.textContent = linkText;

            span.appendChild(img);
            span.appendChild(link);

            var node = dom.byId("doiCrossReferenceTable").parentElement;
            construct.place(span, node, 'before');
        }

    })();
});
