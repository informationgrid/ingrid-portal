/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
    "dijit/form/NumberTextBox",
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/topic",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/Grid"
], function(NumberTextBox, registry, array, declare, lang, domClass, construct, topic, Editors, Formatters, dirty, creator, message, UtilGrid) {

    return declare(null, {
        title: "Wasserstraßenstrecken-Tabelle",
        description: "Tabelle für die Bundeswasserstraßenstrecken",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var promise = this._createCustomFields();

            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class1") {
                    domClass.add("uiElementAddbwastrTable", "required");
                    domClass.remove("uiElementAddbwastrTable", "hidden");
                } else {
                    domClass.remove("uiElementAddbwastrTable", "required");
                    domClass.add("uiElementAddbwastrTable", "hidden");
                }
            });

            return promise;
        },

        _createCustomFields: function () {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var bwastrTableId = "bwastrTable";
            var structure = [
                {
                    field: "bwastr_name",
                    name: message.get("ui.obj.baw.bwastr.table.column.name") + "*",
                    type: Editors.SelectboxEditor,
                    options: [],
                    values: [],
                    listId: 3950010,
                    editable: true,
                    isMandatory: true,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, 3950010),
                    partialSearch: true,
                    width: "450px"
                },
                {
                    field: "bwastr_km_start",
                    name: message.get("ui.obj.baw.bwastr.table.column.km_start"),
                    editable: true,
                    isMandatory: false,
                    type: Editors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: Formatters.LocalizedNumberFormatter,
                    partialSearch: false,
                    width: "150px"
                },
                {
                    field: "bwastr_km_end",
                    name: message.get("ui.obj.baw.bwastr.table.column.km_end"),
                    editable: true,
                    isMandatory: false,
                    type: Editors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: Formatters.LocalizedNumberFormatter,
                    partialSearch: false,
                    width: "150px"
                }
            ];

            creator.createDomDataGrid({
                id: bwastrTableId,
                name: message.get("ui.obj.baw.bwastr.table.title"),
                help: message.get("ui.obj.baw.bwastr.table.help"),
                isMandatory: true,
                style: "width: 100%"
            }, structure, "spatialRef");
            newFieldsToDirtyCheck.push(bwastrTableId);
            additionalFields.push(registry.byId(bwastrTableId));

            topic.subscribe("/onBeforeObjectPublish", function (notPublishableIDs) {
                array.forEach(UtilGrid.getTableData(bwastrTableId), function (row) {
                    if (!row.bwastr_name) {
                        notPublishableIDs.push([bwastrTableId, message.get("validation.baw.bwastr_name.missing")]);
                    }

                    var kmStart = row.bwastr_km_start;
                    var kmEnd = row.bwastr_km_end;
                    if ((kmStart && !kmEnd) || (!kmStart && kmEnd)) {
                        notPublishableIDs.push([bwastrTableId, message.get("validation.baw.bwastr_km.entry.missing")]);
                    }
                });
            });

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(bwastrTableId).promiseInit;
        }

    })();
});

