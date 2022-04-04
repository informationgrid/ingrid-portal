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
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/on",
    "dojo/topic",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/Grid"
], function (registry, array, declare, lang, dom, domClass, construct, on, topic, Editors, Formatters, dirty, creator, message, UtilGrid) {

    var measurementFields = ["measuringMethod", "spatiality", "measuringDepth", "unitOfMeasurement", "averageWaterLevel",
        "zeroLevel", "drainMin", "drainMax", "gauge", "targetParameters"];

    var nonMeasurementFields = ["simProcess", "simSpatialDimension", "simModelTypeTable", "simParamTable"];
    
    return declare(null, {
        title: "Messdaten",
        description: "Formularfelder für die Eingabe von Messdaten",
        defaultActive: true,
        category: "BAW-MIS",

        run: function () {
            this._createCustomFields();
            
            this._createBehaviours();
            
            var levelNameForSimulation = ["Datei", "Simulationslauf", "Simulationsmodell", "Szenario ", "Variante"];
            on(registry.byId("bawHierarchyLevelName"), "Change", function(value) {
                if (value === "Messdaten") {
                    handleHierarchyLevelNameIsMeasurementData();
                    hideFields(nonMeasurementFields);
                } else {
                    handleHierarchyLevelNameIsNotMeasurementData();
                    if (levelNameForSimulation.indexOf(value) !== -1) {
                        showFields(nonMeasurementFields);
                    }
                }
            });
        },

        _createCustomFields: function () {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // Fachbezug
            addMeasuringMethod(newFieldsToDirtyCheck, additionalFields);

            addSpatiality(newFieldsToDirtyCheck, additionalFields)

            addMeasuringDepth(newFieldsToDirtyCheck, additionalFields);

            addAverageWaterLevel(newFieldsToDirtyCheck, additionalFields);
            
            addZeroLevel(newFieldsToDirtyCheck, additionalFields);
            
            addDrain(newFieldsToDirtyCheck, additionalFields);
            
            addGauge(newFieldsToDirtyCheck, additionalFields);
            
            addTargetParameters(newFieldsToDirtyCheck, additionalFields);

            // Datenqualität
            addDQDescription(newFieldsToDirtyCheck, additionalFields);
                        
            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },

        _createBehaviours: function() {
            addMeasuringDepthBehaviour();
            
            addTableValidations();
        }
    })();

    function addMeasuringMethod(newFieldsToDirtyCheck, additionalFields) {
        construct.place(
            creator.createDomSelectBox({
                id: "measuringMethod",
                name: message.get("ui.obj.baw.measuring.method.title"),
                help: message.get("ui.obj.baw.measuring.method.help"),
                isMandatory: true,
                useSyslist: 3950011,
                style: "width: 100%",
                isExtendable: true
            }), "refClass1");
        newFieldsToDirtyCheck.push("measuringMethod");
        additionalFields.push(registry.byId("measuringMethod"));
    }

    function addSpatiality(newFieldsToDirtyCheck, additionalFields) {
        construct.place(
            creator.createDomSelectBox({
                id: "spatiality",
                name: message.get("ui.obj.baw.measuring.spatiality.title"),
                help: message.get("ui.obj.baw.measuring.spatiality.help"),
                useSyslist: 3950012,
                style: "width: 100%"
            }), "refClass1");
        newFieldsToDirtyCheck.push("spatiality");
        additionalFields.push(registry.byId("spatiality"));
    }

    function addMeasuringDepth(newFieldsToDirtyCheck, additionalFields) {
        construct.place(
            creator.createDomNumberTextbox({
                id: "measuringDepth",
                name: message.get("ui.obj.baw.measuring.measuringDepth.title"),
                help: message.get("ui.obj.baw.measuring.measuringDepth.help"),
                style: "width: 50%"
            }), "refClass1");
        newFieldsToDirtyCheck.push("measuringDepth");
        additionalFields.push(registry.byId("measuringDepth"));

        construct.place(
            creator.createDomSelectBox({
                id: "unitOfMeasurement",
                name: message.get("ui.obj.baw.measuring.unitOfMeasurement.title"),
                help: message.get("ui.obj.baw.measuring.unitOfMeasurement.help"),
                useSyslist: 3950020,
                style: "width: 50%",
                isExtendable: true
            }), "refClass1");
        newFieldsToDirtyCheck.push("unitOfMeasurement");
        additionalFields.push(registry.byId("unitOfMeasurement"));
    }

    function addAverageWaterLevel(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "waterLevel",
                name: message.get("ui.obj.baw.measuring.averageWaterLevel.waterLevel") + "*",
                type: Editors.DecimalCellEditor,
                isMandatory: true,
                editable: true,
                width: "250px"
            },
            {
                field: "unitOfMeasurement",
                name: message.get("ui.obj.baw.measuring.averageWaterLevel.unitOfMeasurement") + "*",
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950020),
                listId: 3950020,
                isMandatory: true,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "averageWaterLevel",
            name: message.get("ui.obj.baw.measuring.averageWaterLevel.title"),
            help: message.get("ui.obj.baw.measuring.averageWaterLevel.help"),
            style: "width: 100%"
        }, structure, "refClass1");
        newFieldsToDirtyCheck.push("averageWaterLevel");
        additionalFields.push(registry.byId("averageWaterLevel"));
    }
    
    function addZeroLevel(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "zeroLevel",
                name: message.get("ui.obj.baw.measuring.zeroLevel.zeroLevel") + "*",
                type: Editors.DecimalCellEditor,
                isMandatory: true,
                editable: true,
                width: "100px"
            },
            {
                field: "unitOfMeasurement",
                name: message.get("ui.obj.baw.measuring.zeroLevel.unitOfMeasurement") + "*",
                type: Editors.ComboboxEditor,
                listId: 3950020,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950020),
                isMandatory: true,
                editable: true,
                width: "100px"
            },
            {
                field: "verticalCoordinateReferenceSystem",
                name: message.get("ui.obj.baw.measuring.zeroLevel.verticalCoordinateReferenceSystem") + "*",
                type: Editors.SelectboxEditor,
                listId: 101,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 101),
                isMandatory: true,
                editable: true,
                width: "200px"
            },
            {
                field: "description",
                name: message.get("ui.obj.baw.measuring.zeroLevel.description"),
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "zeroLevel",
            name: message.get("ui.obj.baw.measuring.zeroLevel.title"),
            help: message.get("ui.obj.baw.measuring.zeroLevel.help"),
            style: "width: 100%"
        }, structure, "refClass1");
        newFieldsToDirtyCheck.push("zeroLevel");
        additionalFields.push(registry.byId("zeroLevel"));
    }
    
    function addDrain(newFieldsToDirtyCheck, additionalFields) {
        construct.place(
            creator.createDomNumberTextbox({
                id: "drainMin",
                name: message.get("ui.obj.baw.measuring.drainMin.title"),
                help: message.get("ui.obj.baw.measuring.drainMin.help"),
                style: "width: 50%"
            }), "refClass1");
        newFieldsToDirtyCheck.push("drainMin");
        additionalFields.push(registry.byId("drainMin"));

        construct.place(
            creator.createDomNumberTextbox({
                id: "drainMax",
                name: message.get("ui.obj.baw.measuring.drainMax.title"),
                help: message.get("ui.obj.baw.measuring.drainMax.help"),
                style: "width: 50%",
            }), "refClass1");
        newFieldsToDirtyCheck.push("drainMax");
        additionalFields.push(registry.byId("drainMax"));
    }

    function addGauge(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "name",
                name: message.get("ui.obj.baw.measuring.gauge.name") + "*",
                type: Editors.ComboboxEditor,
                listId: 3950013,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950013),
                isMandatory: true,
                editable: true,
                width: "150px"
            },
            {
                field: "id",
                name: message.get("ui.obj.baw.measuring.gauge.id"),
                editable: true,
                width: "100px"
            },
            {
                field: "model",
                name: message.get("ui.obj.baw.measuring.gauge.model"),
                editable: true,
                width: "100px"
            },
            {
                field: "description",
                name: message.get("ui.obj.baw.measuring.gauge.description"),
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "gauge",
            name: message.get("ui.obj.baw.measuring.gauge.title"),
            help: message.get("ui.obj.baw.measuring.gauge.help"),
            style: "width: 100%"
        }, structure, "refClass1");
        newFieldsToDirtyCheck.push("gauge");
        additionalFields.push(registry.byId("gauge"));
    }

    function addTargetParameters(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "name",
                name: message.get("ui.obj.baw.measuring.targetParameters.name") + "*",
                isMandatory: true,
                editable: true,
                width: "100px"
            },
            {
                field: "type",
                name: message.get("ui.obj.baw.measuring.targetParameters.type") + "*",
                type: Editors.SelectboxEditor,
                listId: 3950021,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950021),
                isMandatory: true,
                editable: true,
                width: "100px"
            },
            {
                field: "unitOfMeasurement",
                name: message.get("ui.obj.baw.measuring.targetParameters.unitOfMeasurement") + "*",
                type: Editors.ComboboxEditor,
                listId: 3950020,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950020),
                isMandatory: true,
                editable: true,
                width: "200px"
            },
            {
                field: "formula",
                name: message.get("ui.obj.baw.measuring.targetParameters.formula"),
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "targetParameters",
            name: message.get("ui.obj.baw.measuring.targetParameters.title"),
            help: message.get("ui.obj.baw.measuring.targetParameters.help"),
            style: "width: 100%",
            isMandatory: true
        }, structure, "refClass1");
        newFieldsToDirtyCheck.push("targetParameters");
        additionalFields.push(registry.byId("targetParameters"));
    }
    
    function addDQDescription(newFieldsToDirtyCheck, additionalFields) {
        construct.place(
            creator.createDomNumberTextbox({
                id: "dataQualityDescription",
                name: message.get("ui.obj.baw.measuring.dataQualityDescription.title"),
                help: message.get("ui.obj.baw.measuring.dataQualityDescription.help"),
                style: "width: 100%"
            }), "refClass1DQ");
        newFieldsToDirtyCheck.push("dataQualityDescription");
        additionalFields.push(registry.byId("dataQualityDescription"));
    }
    
    function handleHierarchyLevelNameIsMeasurementData() {
        showFields(measurementFields);
        
        // show DQ-rubric
        domClass.remove("refClass1DQ", "hidden");
        domClass.add("ref1ContentDQTables", "hide");
        domClass.add("uiElement3565", "hide");
        domClass.add("uiElement5071", "hide");
        domClass.add("uiElement5069", "hide");
        domClass.add("uiElement3530", "hide");
        
        // TODO: "Durch die Ressource abgedeckte Zeitspanne" required
    }
    
    function handleHierarchyLevelNameIsNotMeasurementData() {
        hideFields(measurementFields);
        
        // TODO: show other fields depending on value (behaviour could already exist!)
        
        // hide DQ-rubric
        domClass.add("refClass1DQ", "hidden");
        domClass.remove("ref1ContentDQTables", "hide");
        domClass.remove("uiElement3565", "hide");
        domClass.remove("uiElement5071", "hide");
        domClass.remove("uiElement5069", "hide");
        domClass.remove("uiElement3530", "hide");

        // TODO: "Durch die Ressource abgedeckte Zeitspanne" optional
    }

    function showFields(fields) {
        array.forEach(fields, function(field) {
            domClass.remove("uiElementAdd" + field, "hide");
        })
    }

    function hideFields(fields) {
        array.forEach(fields, function(field) {
            domClass.add("uiElementAdd" + field, "hide");
        })
    }

    function addMeasuringDepthBehaviour() {
        var depth = registry.byId("measuringDepth")
        var unit = registry.byId("unitOfMeasurement")
        
        var toggleState = function(value) {
            const required = value !== "";
            if (required) {
                domClass.add("uiElementAddmeasuringDepth", "required");
                domClass.add("uiElementAddunitOfMeasurement", "required");
            } else {
                domClass.remove("uiElementAddmeasuringDepth", "required");
                domClass.remove("uiElementAddunitOfMeasurement", "required");
            }
        };
        
        on(depth, "Change", toggleState);
        on(unit, "Change", toggleState);
    }
    
    function addTableValidations() {
        topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
            array.forEach(UtilGrid.getTableData("averageWaterLevel"), function (row) {
                if (!row.waterLevel || !row.unitOfMeasurement) {
                    notPublishableIDs.push(["averageWaterLevel", message.get("validation.baw.averageWaterLevel.incomplete")]);
                }
            });
            
            array.forEach(UtilGrid.getTableData("zeroLevel"), function (row) {
                if (!row.zeroLevel || !row.unitOfMeasurement || !row.verticalCoordinateReferenceSystem) {
                    notPublishableIDs.push(["zeroLevel", message.get("validation.baw.zeroLevel.incomplete")]);
                }
            });
            
            array.forEach(UtilGrid.getTableData("gauge"), function (row) {
                if (!row.name) {
                    notPublishableIDs.push(["gauge", message.get("validation.baw.gauge.incomplete")]);
                }
            });
            
            array.forEach(UtilGrid.getTableData("targetParameters"), function (row) {
                if (!row.name || !row.type || !row.unitOfMeasurement) {
                    notPublishableIDs.push(["targetParameters", message.get("validation.baw.targetParameters.incomplete")]);
                }
            });
        });
    }
});

