/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
    "dojo/on",
    "dojo/topic",
    "ingrid/dialog",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/menu",
    "ingrid/message",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist",
    "module"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, dom, domClass, construct, on, topic, dialog, Editors, Formatters, dirty, creator, menu, message, UtilStore, UtilSyslist, module) {

    return declare(null, {
        title: "BAW-Allgemein",
        description: "Allgemeine UI Anpassungen für die BAW",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var promise = this._createCustomFields();

            topic.subscribe("/onObjectClassChange", function(data) {

                var isNewItem = "newNode" === currentUdk.uuid;

                // ========================================
                // Modifications to existing fields
                // ========================================

                // ----------------------------------------
                // Allgemeines
                // ----------------------------------------
                // Kurzbezeichnung
                domClass.add("uiElement5000", "hide");
                // AdV kompatibel checkbox
                domClass.add("uiElement6005", "hide");
                // Open Data checkbox
                /*
                 * opendata.js runs after this script and removes the 'hide'
                 * class. So add the 'hidden' class instead!
                 */
                domClass.add("uiElement6010", "hidden");

                // Kategorien (opendata)
                var ogdCategoriesTableId = "categoriesOpenData";
                var ogdCategoriesTable = registry.byId(ogdCategoriesTableId);

                domClass.add("uiElement6020", "required");
                domClass.remove("uiElement6020", "hide");
                domClass.remove("uiElement6020", "halfWidth");
                ogdCategoriesTable.reinitLastColumn(true);

                if (isNewItem && data.objClass !== "Class0") {
                    var listId = 6400;
                    var entryId = "10";
                    var entryTitle = UtilSyslist.getSyslistEntryName(listId, entryId);

                    var ogdCategoriesTableData = ogdCategoriesTable.data;
                    var existing = ogdCategoriesTableData.find(function (value) {
                        return value.title === entryTitle;
                    });
                    if (!existing) {
                        ogdCategoriesTableData.push({title: entryTitle });
                        UtilStore.updateWriteStore(ogdCategoriesTableId, ogdCategoriesTableData);
                    }
                }

                // ----------------------------------------
                // Verschlagwortung
                // ----------------------------------------
                // AdV-Produktgruppe
                domClass.add("uiElement5170", "hide");


                // INSPIRE Themen
                // Automatically add entry for "Gewässernetz" to new items
                var inspireThemeTableId = "thesaurusInspire";
                var inspireThemeTable = registry.byId(inspireThemeTableId);
                if (isNewItem && data.objClass !== "Class0") {
                    var inspireThemeTableData = inspireThemeTable.data;
                    inspireThemeTableData.push({ title: "108" }); // Gewässernetz
                    UtilStore.updateWriteStore(inspireThemeTableId, inspireThemeTableData);
                }

                // ISO-Themenkategorie
                // For new items, add "transportation" as category automatically
                var topicsTableId = "thesaurusTopics";
                var topicsTableNodeId = "uiElement5060";
                if (isNewItem && !domClass.contains(topicsTableNodeId, "hide")) {
                    var topicsTableData = registry.byId(topicsTableId).data;
                    topicsTableData.push({title: "18"});
                    UtilStore.updateWriteStore(topicsTableId, topicsTableData);
                }

                // Umweltthemen
                domClass.add("uiElementN014", "hide");

                // ----------------------------------------
                // Fachbezug
                // ----------------------------------------
                // Datensatz/Datenserie
                domClass.add("uiElement5061", "hide");
                // Digitale Repräsentation
                domClass.add("uiElement5062", "hide");
                // Vektorformat
                domClass.add("uiElementN005", "hide");
                // Symbolkatalog
                domClass.add("uiElement3555", "hide");
                // Schlüsselkatalog
                domClass.add("uiElement3535", "hide");
                // Sachdaten/Attributinformation
                domClass.add("uiElement5070", "hide");
                // Darstellender Dienst
                domClass.add("uiElementN003", "hide");
                // Herstellungsprozess
                domClass.add("uiElement3515", "hide");

                // ----------------------------------------
                // Datenqualität
                // ----------------------------------------
                // Hide the entire category
                domClass.add("refClass1DQ", "hide");

                // ----------------------------------------
                // Raumbezugssystem
                // ----------------------------------------
                // Höhe
                domClass.add("uiElementN010", "hide");
                // Erläuterungen
                domClass.add("uiElement1140", "hide");

                // ----------------------------------------
                // Zeitbezug
                // ----------------------------------------
                // Zeitbezug der Ressource
                domClass.remove("uiElement5030", "halfWidth");
                registry.byId("timeRefTable").reinitLastColumn(true);
                // Erläuterungen
                domClass.add("uiElement1250", "hide");
                // Status
                domClass.add("uiElement1220", "hide");
                // Periodizität
                domClass.add("uiElement1240", "hide");
                // Im Intervall
                domClass.add("uiElement1230", "hide");



                // ----------------------------------------
                // Zusatzinformation
                // ----------------------------------------
                // Zeichensatz des Datensatzes
                // Make the node mandatory if it has not been hidden by some
                // other rule
                var datasetCharsetNodeId = "uiElement5043";
                if(!domClass.contains(datasetCharsetNodeId, "hide")) {
                    domClass.add(datasetCharsetNodeId, "required");
                }

                // If the dataset is new (not saved yet), then initialise the
                // value as "utf8"
                var datasetCharsetUtf8Value = "4";
                var datasetCharsetWidgetId = "extraInfoCharSetData";

                var datasetCharsetWidget = registry.byId(datasetCharsetWidgetId);
                if (isNewItem
                    && datasetCharsetWidget
                    && !datasetCharsetWidget.get("value")) {
                    datasetCharsetWidget.set("value", datasetCharsetUtf8Value);
                }

                // XML-Export-Kriterium
                domClass.add("uiElementN012", "hide");
                // Herstellungszweck
                domClass.add("uiElementN013", "hide");
                // Konformität
                domClass.remove("uiElementN024", "required");
                // Rechtliche Grundlagen
                domClass.add("uiElement1350", "hide");
                // Eignung/Nutzung
                domClass.add("uiElement5040", "hide");



                // ----------------------------------------
                // Verfügbarkeit
                // ----------------------------------------
                // Kodierungsschema der geografischen Daten
                // Not necessarily a required field. See #1273
                domClass.remove("uiElement1315", "required");
                domClass.add("uiElement1315", "hidden");
                // Medienoption
                domClass.add("uiElement1310", "hidden");
                // Bestellinformation
                domClass.add("uiElement5052", "hidden");

            });

            return promise;
        },

        _createCustomFields: function () {
            var self = require(module.id);
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // TODO mark fields as "required" if not Auftrag

            var id;

            id = "bawHierarchyLevelName";
            construct.place(
                creator.createDomSelectBox({
                    id: id,
                    name: message.get("ui.obj.baw.hierarchy.level.name.title"),
                    help: message.get("ui.obj.baw.hierarchy.level.name.help"),
                    isMandatory: true,
                    useSyslist: 3950002,
                    style: "width: 100%"
                }),
                "uiElement5100", "before"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            on(registry.byId(id), "Change", function (newVal) {
                //self._handleHierarchyLevelChange(newVal);
                var key = UtilSyslist.getSyslistEntryKey(3950002, newVal);
                var isSimulationRelated = key === "6" // datei
                    || key === "18"  // simulationslauf
                    || key === "19"  // simulationsmodell
                    || key === "22"  // szenario
                    || key === "24";  // variante
                topic.publish("onBawHierarchyLevelNameChange", { isSimulationRelated: isSimulationRelated })
            });

            // Create elements in the reverse order instead of deriving ids
            // of the wrapped DOM elements.
            id = "bawAuftragsnummer";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("ui.obj.baw.auftragsnummer.title"),
                    help: message.get("ui.obj.baw.auftragsnummer.help"),
                    visible: "optional",
                    style: "width: 100%"
                }),
                "uiElement1000", "after");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("bawAuftragsnummer", isMandatory);
            });

            id = "bawAuftragstitel";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("ui.obj.baw.auftragstitel.title"),
                    help: message.get("ui.obj.baw.auftragstitel.help"),
                    visible: "optional",
                    style: "width: 100%"
                }),
                "uiElement1000", "after");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("bawAuftragstitel", isMandatory);
            });

            id = "simModelTypeTable";
            var structure;
            structure = [
                {
                    field: "simModelType",
                    name: message.get("ui.obj.baw.simulation.model.type.table.title"),
                    editable: true,
                    type: Editors.SelectboxEditor,
                    options: [],
                    values: [],
                    listId: 3950003,
                    isMandatory: true,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, 3950003),
                    partialSearch: true,
                    style: "width: auto"
                }
            ];
            creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.baw.simulation.model.type.table.title"),
                help: message.get("ui.obj.baw.simulation.model.type.table.help"),
                //isMandatory: true,
                style: "width: 100%"
            }, structure, "refClass1");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("simModelTypeTable", isMandatory);
            });

            id = "simProcess";
            construct.place(
                creator.createDomSelectBox({
                    id: id,
                    name: message.get("ui.obj.baw.simulation.process.title"),
                    help: message.get("ui.obj.baw.simulation.process.help"),
                    visible: "optional",
                    useSyslist: 3950001,
                    style: "width: 50%"
                }),
                "uiElement3520", "before"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("simProcess", isMandatory);
            });

            id = "simSpatialDimension";
            construct.place(
                creator.createDomSelectBox({
                    id: id,
                    name: message.get("ui.obj.baw.simulation.spatial.dimensionality.title"),
                    help: message.get("ui.obj.baw.simulation.spatial.dimensionality.help"),
                    visible: "optional",
                    useSyslist: "3950000",
                    style: "width: 50%"
                }),
                "uiElement3520", "before"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("simSpatialDimension", isMandatory);
            });

            id = "dqAccTimeMeas";
            construct.place(
                creator.createDomNumberTextbox({
                    id: id,
                    name: message.get("ui.obj.baw.simulation.timestep.title") + " [s]",
                    help: message.get("ui.obj.baw.simulation.timestep.help"),
                    visible: "optional",
                    formatter: Formatters.LocalizedNumberFormatter,
                    style: "width: 50%"
                }),
                "uiElement3520", "before"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("dqAccTimeMeas", isMandatory);
            });

            // Create context menu for the simulation parameter table
            this._createBawSimulationParameterTableContextMenu();

            // Define structure for the simulation parameter table
            id = "simParamTable";
            structure = [
                {
                    field: "simParamName",
                    name: message.get("ui.obj.baw.simulation.parameter.table.column.name"),
                    editable: false,
                    isMandatory: true,
                    width: "300px"
                },
                {
                    field: "simParamType",
                    name: message.get("ui.obj.baw.simulation.parameter.table.column.role"),
                    editable: false,
                    isMandatory: true,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, 3950004),
                    partialSearch: false,
                    width: "150px"
                },
                {
                    field: "simParamValue",
                    name: message.get("ui.obj.baw.simulation.parameter.table.column.value"),
                    editable: false,
                    isMandatory: true,
                    width: "150px"
                    // Also see comment below
                },
                {
                    field: "simParamUnit",
                    name: message.get("ui.obj.baw.simulation.parameter.table.column.units"),
                    editable: false,
                    //isMandatory: true,
                    width: "auto"
                }
                /*
                 * Besides the visible columns listed above, the table contains
                 * data entered using the dialog box, which isn't directly
                 * visible in this table. This data is:
                 * - hasDiscreteValues -> Depends on which checkbox is selected
                 *                        in the dialog box. True means that
                 *                        discrete values option is active
                 * - values -> Array containing:
                 *             - row data from discrete values table
                 *             - two entries: one each from the minimum and
                 *               maximum value text fields.
                 *
                 * Additionally, the value displayed in the simParamValue column
                 * is derived using the simulationParameterValueArrayToString
                 * function below. Since this value is a derived one, it can be
                 * recreated every time to avoid inconsistency with the actual
                 * values.
                 */
            ];

            // Create the simulation table parameter
            creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.baw.simulation.parameter.table.title"),
                help: message.get("ui.obj.baw.simulation.parameter.table.help"),
                contextMenu: "BAW_SIMULATION_PARAMETER",
                style: "width: 100%"
            }, structure, "refClass1");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("simParamTable", isMandatory);
            });

            // Add link for creating a new entry to the simulation parameter table
            this._createAppendSimulationParameterLink();

            // Special handling of nested table data originating from the dialog box
            topic.subscribe("beforeFinishGettingObjectNodeData", function (nodeData) {
                /*
                 * We manipulate the additional field data to add another nested
                 * level of values for storing the simulation parameter values
                 * that have been entered in the dialog box.
                 * See also the comment in the structure of this table.
                 */
                var simParamTableData = registry.byId(id).data;
                var simParamNodeAdditionalValues = nodeData.additionalFields.find(function (row) {
                    return row.identifier === id;
                });

                var newTableRows = [];

                if (simParamTableData.length > 0) {
                    simParamNodeAdditionalValues.tableRows.forEach(function (row, idx) {
                        // Remove existing simParamValue entry
                        var rowToEdit = row.filter(function (r) {
                            return r.identifier !== "simParamValue"
                        });

                        // Prepare values to add to the new nested level
                        var values = simParamTableData[idx].values;
                        var valueTableRows = array.map(values, function (valueItem) {
                            return [{
                                identifier: "simParamValue",
                                listId: "-1",
                                tableRows: null,
                                value: valueItem
                            }];
                        });

                        // Add additional rows
                        var hasDiscreteValues = simParamTableData[idx].hasDiscreteValues;
                        rowToEdit.push({
                            identifier: "simParamHasDiscreteValues",
                            listId: "-1",
                            tableRows: null,
                            value: JSON.stringify(hasDiscreteValues)
                            //value: hasDiscreteValues
                        });
                        rowToEdit.push({
                            identifier: "simParamValueArray",
                            listId: -1,
                            tableRows: valueTableRows,
                            value: null
                        });
                        newTableRows.push(rowToEdit);
                    });
                }

                simParamNodeAdditionalValues.tableRows = newTableRows;
            });

            topic.subscribe("beforeFinishApplyingObjectNodeData", function (nodeData) {
                /*
                 * Since values are stored as additional fields, some columns
                 * have been automatically handled by this point. We just need
                 * to handle two hidden fields here. See also the comment on the
                 * structure for the table above.
                 */
                var simParamTableData = registry.byId(id).data;
                var nodeAdditionalFieldData = nodeData.additionalFields.find(function (row) {
                    return row.identifier === id;
                });

                // If no additional values for the desired id are found then there is nothing to do
                if (!nodeAdditionalFieldData || !nodeAdditionalFieldData.tableRows) return;

                nodeAdditionalFieldData.tableRows.forEach(function (additionalFieldRow, idx) {
                    var simParamTableRow = simParamTableData[idx];

                    // We can use the value that has already been set wrongly
                    var hasDiscreteValues = JSON.parse(simParamTableRow.simParamHasDiscreteValues);
                    simParamTableRow["hasDiscreteValues"] = hasDiscreteValues;
                    delete simParamTableRow["simParamHasDiscreteValues"];
                    delete simParamTableRow["simParamValueArray"];

                    var valuesEntry = additionalFieldRow.find(function (row) {
                        return row.identifier === "simParamValueArray";
                    });

                    var values = array.map(valuesEntry.tableRows, function (entry) {
                        return entry[0].value;
                    });
                    simParamTableRow["values"] = values;
                    simParamTableRow["simParamValue"] = self.simulationParameterValueArrayToString(values, hasDiscreteValues);
                });
                UtilStore.updateWriteStore(id, simParamTableData);
            });

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(id).promiseInit;
        },

        _createBawSimulationParameterTableContextMenu: function () {
            var type = "BAW_SIMULATION_PARAMETER";
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
                    dialog.showPage(message.get("dialog.simulation.parameter.title"), 'dialogs/mdek_baw_simulation_parameter_dialog.jsp?c=' + userLocale, 600, 300, true, dialogData);
                }
            }));
        },

        _createAppendSimulationParameterLink: function () {
            var linkId = "simParamTableLink";
            var linkText = message.get("ui.obj.baw.simulation.parameter.table.new.row");
            var linkOnClick = "require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('simParamValue'), 'dialogs/mdek_baw_simulation_parameter_dialog.jsp?c=' + userLocale, 600, 300, true, {});";

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

            var node = dom.byId("simParamTable").parentElement;
            construct.place(span, node, 'before');
        },

        /**
         * Mark or unmark a widget's DOM wrapper as required.
         *
         * @param widgetId widget to mark or unmark as required
         * @param isMandatory true if an entry is mandatory, false if not
         * @private
         */
        _setMandatory: function (widgetId, isMandatory) {
            var domPrefix = "uiElementAdd";
            var domElementId = domPrefix + widgetId;
            if (isMandatory) {
                domClass.add(domElementId, "required");
                domClass.remove(domElementId, "optional");
            } else {
                domClass.remove(domElementId, "required");
                domClass.add(domElementId, "optional");
            }
    },

    /*
     * Create a string representation for the simulation parameters as
     * follows:
     * - A single discrete value is displayed as is,
     * - Multiple discrete values are displayed as the stringified
     *   form of the "values" array e.g. [ 1, 2, 3 ], and
     * - Min and max values (not discrete) are displayed in square brackets
     *   and delimited by two dots e.g. [ 0 .. 5 ]
     */
        simulationParameterValueArrayToString: function (values, hasDiscreteValues) {
            var valuesString = "";
            if (hasDiscreteValues) {
                if (values.length == 1) {
                    valuesString += values[0];
                } else {
                    valuesString = JSON.stringify(values);
                }
            } else {
                var min = values[0];
                var max = values[1];
                valuesString = "[" + min + " .. " + max + "]";
            }
            return valuesString;
        }

    })();
});

