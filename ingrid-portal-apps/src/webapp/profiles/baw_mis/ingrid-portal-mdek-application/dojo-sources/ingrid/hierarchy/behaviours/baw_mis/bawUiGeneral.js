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
    "dijit/form/NumberTextBox",
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
    "ingrid/utils/Grid",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist",
    "module"
], function(NumberTextBox, MenuItem, MenuSeparator, registry, array, declare, lang, dom, domClass, construct, on, topic, dialog, Editors, Formatters, dirty, creator, menu, message, UtilGrid, UtilStore, UtilSyslist, module) {

    const DISCRETE_NUMERIC = "DISCRETE_NUMERIC";
    const DISCRETE_STRING = "DISCRETE_STRING";
    const RANGE_NUMERIC = "RANGE_NUMERIC";

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

            var structure;

            const bawKeywordCatalogueTableId = "bawKeywordCatalogueTable";
            structure = [
                {
                    field: "bawKeywordCatalogueEntry",
                    name: message.get("ui.obj.baw.keyword.catalogue.row.title"),
                    editable: true,
                    type: Editors.SelectboxEditor,
                    options: [],
                    values: [],
                    listId: 3950005,
                    isMandatory: true,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, 3950005),
                    partialSearch: false,
                    style: "width: auto"
                }
            ];
            creator.createDomDataGrid({
                id: bawKeywordCatalogueTableId,
                name: message.get("ui.obj.baw.keyword.catalogue.table.title"),
                help: message.get("ui.obj.baw.keyword.catalogue.table.help"),
                //isMandatory: true,
                style: "width: 100%"
            }, structure, "thesaurus");
            newFieldsToDirtyCheck.push(bawKeywordCatalogueTableId);
            additionalFields.push(registry.byId(bawKeywordCatalogueTableId));

            id = "simModelTypeTable";
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
            var simParamTableId = "simParamTable";
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
                 * - simParamValueType -> Depends on which checkbox is selected
                 *                        in the dialog box.
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
                id: simParamTableId,
                name: message.get("ui.obj.baw.simulation.parameter.table.title"),
                help: message.get("ui.obj.baw.simulation.parameter.table.help"),
                contextMenu: "BAW_SIMULATION_PARAMETER",
                style: "width: 100%"
            }, structure, "refClass1");
            newFieldsToDirtyCheck.push(simParamTableId);
            additionalFields.push(registry.byId(simParamTableId));

            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory("simParamTable", isMandatory);
            });

            // Add link for creating a new entry to the simulation parameter table
            this._createAppendSimulationParameterLink();

            // Special handling of nested table data originating from the dialog box
            topic.subscribe("beforeFinishGettingObjectNodeData", function (nodeData) {
                /*
                 * We manipulate the additional field data to add values that
                 * have been added in the dialog box. Ideally one would add
                 * another nested level. However, that doesn't work perfectly
                 * right now at the IGC level because if multiple rows at the
                 * intermediate level have the same key, then it is impossible
                 * to map the nested values to the correct row.
                 *
                 * See also the comment in the structure of this table.
                 */
                var simParamTableData = registry.byId(simParamTableId).data;
                var simParamNodeAdditionalValues = nodeData.additionalFields.find(function (row) {
                    return row.identifier === simParamTableId;
                });

                var newTableRows = [];

                if (simParamTableData.length > 0) {
                    simParamNodeAdditionalValues.tableRows.forEach(function (row, idx) {
                        // Remove existing simParamValue entry
                        const rowToEdit = row.filter(function (r) {
                            return r.identifier !== "simParamValue"
                        });

                        const simParamValueType = simParamTableData[idx].simParamValueType;
                        rowToEdit.push({
                            identifier: "simParamValueType",
                            listId: "-1",
                            tableRows: null,
                            value: simParamValueType
                        });

                        // Add values to the additional values object
                        simParamTableData[idx].values.forEach(function (value, validx) {
                            rowToEdit.push({
                                identifier: "simParamValue." + (validx + 1), // Start count at 1 instead of 0
                                listId: "-1",
                                tableRows: null,
                                value: value
                            });
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
                var simParamTableData = registry.byId(simParamTableId).data;
                var nodeAdditionalFieldData = nodeData.additionalFields.find(function (row) {
                    return row.identifier === simParamTableId;
                });

                // If no additional values for the desired id are found then there is nothing to do
                if (!nodeAdditionalFieldData || !nodeAdditionalFieldData.tableRows) return;

                nodeAdditionalFieldData.tableRows.forEach(function (additionalFieldRow, idx) {
                    const simParamTableRow = simParamTableData[idx];

                    const valuesEntries = additionalFieldRow.filter(function (row) {
                        return row.identifier.startsWith("simParamValue.");
                    });

                    if (valuesEntries) {
                        var simParamValueType = simParamTableRow.simParamValueType;
                        const hasNumericValues = self.hasNumericValues(simParamValueType);
                        var values = [];
                        for (var i = 0; i < valuesEntries.length; i++) {
                            const identifier = "simParamValue." + (i + 1);
                            const row = valuesEntries.find(function (row) {
                                return row.identifier === identifier;
                            });

                            if (row) {
                                const v = hasNumericValues ? JSON.parse(row.value) : row.value;
                                values.push(v);
                            }
                            delete simParamTableRow[identifier];
                        }

                        simParamTableRow["values"] = values;
                        simParamTableRow["simParamValue"] = self.simulationParameterValueArrayToString(values, simParamValueType);
                    }
                });
                UtilStore.updateWriteStore(simParamTableId, simParamTableData);
            });


            var bwastrTableId = "bwastrTable";
            structure = [
                {
                    field: "bwastr_name",
                    name: message.get("ui.obj.baw.bwastr.table.column.name"),
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
                //contextMenu: "BAW_SIMULATION_PARAMETER",
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
        simulationParameterValueArrayToString: function (values, simParamValueType) {
            const hasDiscreteValues = this.hasDiscreteValues(simParamValueType);
            const formatted = values.map(function (val) {
                var v = dojo.number.format(val);
                return v ? v : val;
            });

            var valuesString = "";
            if (hasDiscreteValues) {
                if (formatted.length === 1) {
                    valuesString += formatted[0];
                } else {
                    valuesString = JSON.stringify(formatted);
                }
            } else {
                const min = formatted[0];
                const max = formatted[1];
                valuesString = "[" + min + " .. " + max + "]";
            }
            return valuesString;
        },

        hasNumericValues(simParamValueType) {
            return simParamValueType === DISCRETE_NUMERIC || simParamValueType === RANGE_NUMERIC;
        },

        hasDiscreteValues(simParamValueType) {
            return simParamValueType === DISCRETE_NUMERIC || simParamValueType === DISCRETE_STRING;
        }

    })();
});

