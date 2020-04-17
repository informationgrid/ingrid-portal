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
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/topic",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/UI"
], function(registry, array, declare, lang, dom, domClass, construct, topic, Editors, Formatters, dirty, creator, message, UtilUI) {

    return declare(null, {
        title: "Plattform/Sensor-UI",
        description: "Anpassungen der Editor-Oberfläche für Plattform-/Sensor-Metadaten",
        defaultActive: true,
        category: "Plattform/Sensor",
        run: function() {
            var promise = this._createSensorMLFields();
            this._registerObjectClassChangeListener();

            return promise;
        },

        _createLinkToDialog: function(linkId, linkText, linkOnClick) {
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

            return span;
        },

        _createSensorMLFields: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];
            var tableIdsForValidation = [];

            var identifierCategoryId = "sensorIdentifier";
            var classifierCategoryId = "sensorClassifier";
            var capabilityCategoryId = "sensorCapability";
            var parameterCategoryId = "sensorParameter";

            // === Identifiers ===
            var identifierContainer = creator.createRubric({
                id: identifierCategoryId,
                label: message.get("ui.obj.identifier.title"),
                help: message.get("ui.obj.identifier.help")
            });
            construct.place(identifierContainer, "general", "after");


            var id = "identifierId";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("sensor.identifier.id") + "*",
                    style: "width: 100%",
                    help: message.get("sensor.identifier.id.help"),
                    isMandatory: true,
                    visible: true,
                    style: "width: 100%"
                }),
                identifierCategoryId, "last");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "identifierUrn";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("sensor.identifier.urn") + "*",
                    style: "width: 100%",
                    help: message.get("sensor.identifier.urn.help"),
                    isMandatory: true,
                    visible: true,
                    style: "width: 100%"
                }),
                identifierCategoryId, "last");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "identifierShortName";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("sensor.identifier.shortName") + "*",
                    style: "width: 100%",
                    help: message.get("sensor.identifier.shortName.help"),
                    isMandatory: true,
                    visible: true,
                    style: "width: 100%"
                }),
                identifierCategoryId, "last");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "identifierManufacturer";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("sensor.identifier.manufacturer") + "*",
                    style: "width: 100%",
                    help: message.get("sensor.identifier.manufacturer.help"),
                    isMandatory: true,
                    visible: true,
                    style: "width: 100%"
                }),
                identifierCategoryId, "last");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "sensorIdentifierDataGrid";
            var listOptions = ["Call sign", "ICES-Code", "IMO-Code", "WMO-Code" ];
            var listValues = listOptions;
            var structure = [
                {
                    field: "sensorIdentifierLabel",
                    name: message.get("table.sensor.identifier.label"),
                    type: Editors.SelectboxEditor,
                    editable: true,
                    options: listOptions,
                    values: listValues,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: "200px"
                },
                {
                    field: "sensorIdentifierValue",
                    name: message.get("table.sensor.identifier.value"),
                    editable: true,
                    width: "auto"
                }
            ];
            creator.createDomDataGrid(
                {id: id, name: message.get("table.sensor.identifier"), help: message.get("table.sensor.identifier.help"), style: "width: 100%"},
                structure, identifierCategoryId
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            tableIdsForValidation.push(id);



            // === Classifiers ===
            var classifierContainer = creator.createRubric({
                id: classifierCategoryId,
                label: message.get("ui.obj.classifier.title"),
                help: message.get("ui.obj.classifier.help")
            });
            construct.place(classifierContainer, identifierCategoryId, "after");

            id = "sensorType";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("sensor.classifier.type") + "*",
                    help: message.get("sensor.classifier.type.help"),
                    isMandatory: true,
                    visible: true,
                    style: "width: 100%"
                }),
                classifierCategoryId, "last");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "sensorClassifierDataGrid";
            listOptions = ["DeviceCategory"];
            listValues = listOptions;
            structure = [
                {
                    field: "sensorClassifierLabel",
                    name: message.get("table.sensor.classifier.label"),
                    editable: true,
                    type: Editors.ComboboxEditor,
                    options: listOptions,
                    values: listValues,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: "200px"
                },
                {
                    field: "sensorClassifierValue",
                    name: message.get("table.sensor.classifier.value"),
                    editable: true,
                    width: "auto"
                }
            ];
            creator.createDomDataGrid(
                {id: id, name: message.get("table.sensor.classifier"), help: message.get("table.sensor.classifier.help"), style: "width: 100%"},
                structure, classifierCategoryId
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            tableIdsForValidation.push(id);



            //  === Capabilities ===
            var capabilityContainer = creator.createRubric({
                id: capabilityCategoryId,
                label: message.get("ui.obj.capability.title"),
                help: message.get("ui.obj.capability.help")
            });
            construct.place(capabilityContainer, classifierCategoryId, "after");

            id = "sensorCapabilityDataGrid";
            listOptions = ["Präzision", "Sensitivität"];
            listValues = listOptions;
            structure = [
                {
                    field: "sensorCapabilityLabel",
                    name: message.get("table.sensor.capability.label"),
                    editable: true,
                    type: Editors.ComboboxEditor,
                    options: listOptions,
                    values: listValues,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: "500px"
                },
                {
                    field: "sensorCapabilityValue",
                    name: message.get("table.sensor.capability.value"),
                    editable: true,
                    width: "100px"
                },
                {
                    field: "sensorCapabilityUnits",
                    name: message.get("table.sensor.capability.units"),
                    editable: true,
                    width: "auto"
                }
            ];
            creator.createDomDataGrid(
                {id: id, name: message.get("table.sensor.capability"), help: message.get("table.sensor.capability.help"), style: "width: 100%"},
                structure, capabilityCategoryId
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            tableIdsForValidation.push(id);



            //  === Parameters ===
            var parameterContainer = creator.createRubric({
                id: parameterCategoryId,
                label: message.get("ui.obj.parameter.title"),
                help: message.get("ui.obj.parameter.help")
            });
            construct.place(parameterContainer, capabilityCategoryId, "after");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "sensorParameterDataGrid";
            listOptions = [
                "chlorophyll_concentration_in_sea_water",
                "fractional_saturation_of_oxygen_in_sea_water",
                "sea_surface_salinity",
                "sea_surface_temperature",
                "sea_surface_wave_significant_height",
                "sea_water_salinity",
                "sea_water_temperature",
                "sea_water_turbidity",
                "wind_from_direction",
                "wind_speed"
            ];
            listValues = listOptions;
            structure = [
                {
                    field: "sensorParameterLabel",
                    name: message.get("table.sensor.parameter.label"),
                    editable: true,
                    type: Editors.ComboboxEditor,
                    options: listOptions,
                    values: listValues,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: "200px"
                },
                {
                    field: "sensorParameterUnits",
                    name: message.get("table.sensor.parameter.units"),
                    editable: true,
                    width: "100px"
                },
                {
                    field: "sensorParameterDescription",
                    name: message.get("table.sensor.parameter.description"),
                    editable: true,
                    width: "auto"
                }
            ];
            creator.createDomDataGrid(
                {id: id, name: message.get("table.sensor.parameter"), help: message.get("table.sensor.parameter.help"), style: "width: 100%"},
                structure, parameterCategoryId
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            tableIdsForValidation.push(id);


            // === Verweise ===
            structure = [
                {
                    field: "subSensorObjectLink",
                    name: message.get("table.link.subSensor.object"),
                    editable: false,
                    width: "686px"
                },
                {
                    field: "subSensorObjectUuid",
                    editable: false,
                    width: "0px",
                    hidden: true
                },
                {
                    field: "subSensorObjectName",
                    editable: false,
                    width: "0px",
                    hidden: true
                }
            ];

            id = "subSensorDataGrid";
            creator.createDomDataGrid(
                {id: id, name: message.get("table.link.subSensor"), help: message.get("table.link.subSensor.help"), style: "width: 100%"},// contextMenu: "OBSERVED_PROPERTIES"},
                structure, "links"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            // Add link for dialog box
            var linkId = "subSensorTableLink";
            var linkText = message.get("dialog.link.subSensor.title");
            var linkOnClick = "require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('subSensorObject'), 'dialogs/mdek_subsensor_link_dialog.jsp?c='+userLocale, 600, 350, true, {});";
            var span = this._createLinkToDialog(linkId, linkText, linkOnClick);
            var node = dom.byId(id).parentElement;
            construct.place(span, node, 'before');

            structure = [
                {
                    field: "sensorDocumentationRole",
                    name: message.get("table.link.sensor.documentation.role"),
                    editable: true,
                    width: "150px"
                },
                {
                    field: "sensorDocumentationName",
                    name: message.get("table.link.sensor.documentation.name"),
                    editable: true,
                    width: "250px"
                },
                {
                    field: "sensorDocumentationUrl",
                    name: message.get("table.link.sensor.documentation.url"),
                    editable: true,
                    width: "auto"
                }
            ];

            id = "sensorDocumentationDataGrid";
            creator.createDomDataGrid(
                {id: id, name: message.get("table.link.sensor.documentation"), help: message.get("table.link.sensor.documentation.help"), style: "width: 100%"},// contextMenu: "OBSERVED_PROPERTIES"},
                structure, "links"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            tableIdsForValidation.push(id);

            this._setValidationFunctionFor(tableIdsForValidation);

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

            return registry.byId(id).promiseInit;
        },

        _registerObjectClassChangeListener: function() {
            var identifierCategoryId = "sensorIdentifier";
            var classifierCategoryId = "sensorClassifier";
            var capabilityCategoryId = "sensorCapability";
            var parameterCategoryId = "sensorParameter";

            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class20") {
                    // Allgemeines
                    domClass.add("uiElement1001", "hide");
                    domClass.add("uiElement5000", "hide");
                    domClass.add("uiElement6005", "hide");

                    // Verschlagwortung
                    UtilUI.setHide("thesaurus");

                    // Raumbezugssystem
                    domClass.add("uiElement1140", "hide");
                    domClass.add("uiElement3500", "hide");

                    // Zeitbezug
                    domClass.add("uiElement1250", "hide");
                    domClass.add("uiElement1220", "hide");
                    domClass.add("uiElement1240", "hide");
                    domClass.add("uiElement1230", "hide");

                    // Zusatzinformation
                    UtilUI.setHide("extraInfo");

                    // Zeitbezug
                    domClass.add("uiElement5030", "hide");

                    // Verfübarkeit
                    UtilUI.setHide("availability");

                    // Verweise
                    domClass.add("uiElementN017", "hide");
                    domClass.add("uiElementN018", "hide");
                    UtilUI.setShow("uiElementAddsubSensorDataGrid");
                    UtilUI.setShow("uiElementAddsensorDocumentationDataGrid");

                    // Observed properties
                    UtilUI.setHide("observedProperties");

                    // SensorML specific categories
                    UtilUI.setShow(identifierCategoryId);
                    UtilUI.setShow(classifierCategoryId);
                    UtilUI.setShow(capabilityCategoryId);
                    UtilUI.setShow(parameterCategoryId);
                } else {
                    // Allgemeines
                    domClass.remove("uiElement1001", "hide");
                    domClass.remove("uiElement5000", "hide");
                    domClass.remove("uiElement6005", "hide");

                    // Verschlagwortung
                    UtilUI.setShow("thesaurus");

                    // Raumbezugssystem
                    domClass.remove("uiElement1140", "hide");
                    domClass.remove("uiElement3500", "hide");

                    // Zeitbezug
                    domClass.remove("uiElement1250", "hide");
                    domClass.remove("uiElement1220", "hide");
                    domClass.remove("uiElement1240", "hide");
                    UtilUI.setShow("uiElement1230", "hide");

                    // Zusatzinformation
                    UtilUI.setShow("extraInfo");

                    // Zeitbezug
                    domClass.remove("uiElement5030", "hide");

                    // Verfübarkeit
                    domClass.remove("availability");

                    // Verweise
                    domClass.remove("uiElementN017", "hide");
                    domClass.remove("uiElementN018", "hide");
                    UtilUI.setHide("uiElementAddsubSensorDataGrid");
                    UtilUI.setHide("uiElementAddsensorDocumentationDataGrid");

                    // Observed properties
                    UtilUI.setShow("observedProperties");

                    // SensorML specific categories
                    UtilUI.setHide(identifierCategoryId);
                    UtilUI.setHide(classifierCategoryId);
                    UtilUI.setHide(capabilityCategoryId);
                    UtilUI.setHide(parameterCategoryId);
                }

            });
        },

        _setValidationFunctionFor: function(tableIds) {
            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                tableIds.forEach(function(id) {
                    var isDataValid = true;

                    var table = registry.byId(id);
                    var data = table.data;
                    var columnFields = table.columns.map(function (column) {
                        return column.field;
                    });

                    for (var i=0; i<data.length; i++) {
                        var isRowValid = true;
                        for (var j=0; j<columnFields.length && isRowValid; j++) {
                            var field = columnFields[j];
                            var columnValue = data[i][field];
                            isRowValid = isRowValid
                                && columnValue !== undefined
                                && columnValue !== null
                                && columnValue.trim() !== "";
                        }
                        isDataValid = isDataValid && isRowValid;
                    }
                    if (!isDataValid) {
                        notPublishableIDs.push([id, message.get("validation.sensorml.table.missingValues")]);
                    }
                });
            });
        }
    })();
});
