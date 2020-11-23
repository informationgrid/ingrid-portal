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
    "ingrid/utils/Store",
    "module"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, dom, domClass, construct, topic, dialog, Editors, Formatters, dirty, creator, menu, message, UtilStore, module) {

    const SIM_MODEL_TYPE_TABLE_ID = "simModelTypeTable";
    const SIM_PROCESS_ID = "simProcess";
    const SIM_SPATIAL_DIMENSION_ID = "simSpatialDimension";
    const TIMESTEP_SIZE_ID = "dqAccTimeMeas";
    const SIM_PARAM_TABLE_ID = "simParamTable";

    const DISCRETE_NUMERIC = "DISCRETE_NUMERIC";
    const DISCRETE_STRING = "DISCRETE_STRING";
    const RANGE_NUMERIC = "RANGE_NUMERIC";

    return declare(null, {
        title: "Simulationsmetadatenfelder",
        description: "Metadaten zum Erfassen von Simulationsmetadaten",
        defaultActive: true,
        category: "BAW-MIS",

        run: function() {
            var self = require(module.id);
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

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
                id: SIM_MODEL_TYPE_TABLE_ID,
                name: message.get("ui.obj.baw.simulation.model.type.table.title"),
                help: message.get("ui.obj.baw.simulation.model.type.table.help"),
                visible: "optional",
                style: "width: 100%"
            }, structure, "refClass1");
            newFieldsToDirtyCheck.push(SIM_MODEL_TYPE_TABLE_ID);
            additionalFields.push(registry.byId(SIM_MODEL_TYPE_TABLE_ID));

            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRelated;
                self._setMandatory(SIM_MODEL_TYPE_TABLE_ID, isMandatory);
                registry.byId(SIM_MODEL_TYPE_TABLE_ID).reinitLastColumn(true);
            });




            // --------------------
            construct.place(
                creator.createDomSelectBox({
                    id: SIM_PROCESS_ID,
                    name: message.get("ui.obj.baw.simulation.process.title"),
                    help: message.get("ui.obj.baw.simulation.process.help"),
                    visible: "optional",
                    useSyslist: 3950001,
                    style: "width: 50%"
                }),
                "uiElement3520", "before"
            );
            newFieldsToDirtyCheck.push(SIM_PROCESS_ID);
            additionalFields.push(registry.byId(SIM_PROCESS_ID));

            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRunOrFile;
                self._setMandatory(SIM_PROCESS_ID, isMandatory);
            });




            // --------------------
            construct.place(
                creator.createDomSelectBox({
                    id: SIM_SPATIAL_DIMENSION_ID,
                    name: message.get("ui.obj.baw.simulation.spatial.dimensionality.title"),
                    help: message.get("ui.obj.baw.simulation.spatial.dimensionality.help"),
                    visible: "optional",
                    useSyslist: "3950000",
                    style: "width: 50%"
                }),
                "uiElement3520", "before"
            );
            newFieldsToDirtyCheck.push(SIM_SPATIAL_DIMENSION_ID);
            additionalFields.push(registry.byId(SIM_SPATIAL_DIMENSION_ID));
            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRunOrFile;
                self._setMandatory(SIM_SPATIAL_DIMENSION_ID, isMandatory);
            });




            // --------------------
            construct.place(
                creator.createDomNumberTextbox({
                    id: TIMESTEP_SIZE_ID,
                    name: message.get("ui.obj.baw.simulation.timestep.title") + " [s]",
                    help: message.get("ui.obj.baw.simulation.timestep.help"),
                    visible: "optional",
                    formatter: Formatters.LocalizedNumberFormatter,
                    style: "width: 50%"
                }),
                "uiElement3520", "before"
            );
            newFieldsToDirtyCheck.push(TIMESTEP_SIZE_ID);
            additionalFields.push(registry.byId(TIMESTEP_SIZE_ID));

            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRunOrFile;
                self._setMandatory(TIMESTEP_SIZE_ID, isMandatory);
            });



            // --------------------
            // Create context menu for the simulation parameter table
            this._createBawSimulationParameterTableContextMenu();

            // Define structure for the simulation parameter table
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
                id: SIM_PARAM_TABLE_ID,
                name: message.get("ui.obj.baw.simulation.parameter.table.title"),
                help: message.get("ui.obj.baw.simulation.parameter.table.help"),
                contextMenu: "BAW_SIMULATION_PARAMETER",
                visible: "optional",
                style: "width: 100%"
            }, structure, "refClass1");
            newFieldsToDirtyCheck.push(SIM_PARAM_TABLE_ID);
            additionalFields.push(registry.byId(SIM_PARAM_TABLE_ID));

            topic.subscribe("onBawHierarchyLevelNameChange", function (args) {
                var isMandatory = args.isSimulationRunOrFile;
                self._setMandatory(SIM_PARAM_TABLE_ID, isMandatory);
                registry.byId(SIM_PARAM_TABLE_ID).reinitLastColumn(true);
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
                var simParamTableData = registry.byId(SIM_PARAM_TABLE_ID).data;
                var simParamNodeAdditionalValues = nodeData.additionalFields.find(function (row) {
                    return row.identifier === SIM_PARAM_TABLE_ID;
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
                var simParamTableData = registry.byId(SIM_PARAM_TABLE_ID).data;
                var nodeAdditionalFieldData = nodeData.additionalFields.find(function (row) {
                    return row.identifier === SIM_PARAM_TABLE_ID;
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
                UtilStore.updateWriteStore(SIM_PARAM_TABLE_ID, simParamTableData);
            });

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(SIM_PARAM_TABLE_ID).promiseInit;
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

            var node = dom.byId(SIM_PARAM_TABLE_ID).parentElement;
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

