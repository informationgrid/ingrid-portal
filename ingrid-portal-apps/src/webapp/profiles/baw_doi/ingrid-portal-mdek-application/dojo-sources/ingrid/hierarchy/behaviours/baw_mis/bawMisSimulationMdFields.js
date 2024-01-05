/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
    "ingrid/message"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, dom, domClass, construct, topic, dialog, Editors, Formatters, dirty, creator, menu, message) {

    const SIM_MODEL_TYPE_TABLE_ID = "simModelTypeTable";
    const SIM_PROCESS_ID = "simProcess";
    const SIM_SPATIAL_DIMENSION_ID = "simSpatialDimension";
    const TIMESTEP_SIZE_ID = "dqAccTimeMeas";
    const SIM_PARAM_TABLE_ID = "simParamTable";

    return declare(null, {
        title: "Simulationsmetadatenfelder",
        description: "Metadaten zum Erfassen von Simulationsmetadaten",
        defaultActive: true,
        category: "BAW-MIS",

        run: function() {
            var self = this;
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
                    field: "simParamValuesFormatted",
                    name: message.get("ui.obj.baw.simulation.parameter.table.column.value"),
                    editable: false,
                    isMandatory: true,
                    width: "150px"
                },
                {
                    field: "simParamUnit",
                    name: message.get("ui.obj.baw.simulation.parameter.table.column.units"),
                    editable: false,
                    width: "auto"
                },
                {
                    field: "simParamValueType",
                    hidden: true
                },
                {
                    field: "simParamValues",
                    hidden: true
                }
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
            creator.createTableLink(
                "simParamTable",
                message.get("ui.obj.baw.simulation.parameter.table.new.row"),
                'dialogs/mdek_baw_simulation_parameter_dialog.jsp',
                message.get('dialog.simulation.parameter.title')
            );

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
        }

    })();
});

