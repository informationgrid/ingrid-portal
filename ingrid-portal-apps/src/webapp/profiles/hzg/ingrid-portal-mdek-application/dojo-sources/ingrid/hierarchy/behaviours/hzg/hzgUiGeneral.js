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
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-construct",
    "dojo/topic",
    "ingrid/dialog",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/menu",
    "ingrid/message",
    "ingrid/utils/UI"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, aspect, dom, construct, topic, dialog, dirty, creator, menu, message, UtilUI) {

    return declare(null, {
        title: "HZG-UI",
        description: "Anpassungen der Editor-Oberfläche für das HZG",
        defaultActive: true,
        category: "HZG",
        run: function() {
            this._generalModifications();
            this._createObservedPropertiesFields();
            var promise = this._createSensorMLFields();
            this._registerObjectClassChangeListener();

            return promise;
        },

        _generalModifications: function() {

            // Allgemeines / Vorschaugrafik
            UtilUI.setShow("uiElement5100");

            // Fachbezug / Digitale Repräsentation
            UtilUI.setShow("uiElement5062");
            aspect.after(registry.byId("ref1Representation"), "onDataChanged", function() {
                // row.title 1 is Vector and row.title 2 is Raster
                // Check if the list contains either of these entries
                var hasVectorType = array.some(this.getData(), function(row) {
                    return row.title === 1 || row.title === "1";
                });

                var hasGridType = array.some(this.getData(), function(row) {
                    // 2 === Raster, Gitter
                    return row.title === 2 || row.title === "2";
                });

                // show / hide relevant fields
                if (hasVectorType) {
                    UtilUI.setShow("ref1VFormat");
                    UtilUI.setShow("uiElement5063");
                    UtilUI.setShow("uiElementN001");
                } else {
                    UtilUI.setHide("ref1VFormat");
                }
                if (hasGridType) {
                    UtilUI.setShow("ref1GridFormat");
                } else {
                    UtilUI.setHide("ref1GridFormat");
                }
            });

            // Fachbezug / Sachdaten/Attributinformation
            UtilUI.setShow("uiElement5070");

            // Zeitbezug / Durch die Ressource Abgedeckte Zeitspanne
            UtilUI.setShow("uiElementN011");

            // Zeitbezug / Status
            UtilUI.setShow("uiElement1220");

            // Zeitbezug / Periodizität
            UtilUI.setShow("uiElement1240");
        },

        _createObservedPropertiesFields: function() {
            // Observed properties
            var category = "Observed Properties";
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var categoryContainer = creator.createRubric({
                id: "observedProperties",
                label: "Observed Properties",
                help: message.get("ui.obj.observedProperty.help")
            });
            construct.place(categoryContainer, 'links', 'after');

            // Observed Property table
            // First initialise the context menu
            this._createObsPropGridContextMenu();

            // Next define the table structure
            var structure = [
                {
                    field: "observedPropertyName",
                    name: message.get("table.observedProperty.name"),
                    editable: false,
                    width: "300px"
                },
                {
                    field: "observedPropertyXmlDescription",
                    name: message.get("table.observedProperty.xmlDescription"),
                    editable: false,
                    width: "auto"
                }
            ];

            // Finally create the table
            var id = "observedPropertiesDataGrid";
            creator.createDomDataGrid(
                {id: id, name: "Observed Properties", help:message.get("table.observedProperty.help"), style: "width: 100%", contextMenu: "OBSERVED_PROPERTIES"},
                structure, categoryContainer
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            var linkId = "observedPropertiesTableLink";
            var linkText = message.get("dialog.observedProperty.title");
            var linkOnClick = "require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('observedProperty'), 'dialogs/mdek_observed_property_dialog.jsp?c='+userLocale, 600, 350, true, {});";
            var span = this._createLinkToDialog(linkId, linkText, linkOnClick);
            var node = dom.byId(id).parentElement;
            construct.place(span, node, 'before');

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
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

        _createObsPropGridContextMenu: function() {
            var type = "OBSERVED_PROPERTIES";
            var contextMenu = menu.initContextMenu({contextMenu: type});
            contextMenu.addChild(new MenuSeparator());
            contextMenu.addChild(new MenuItem({
                id: "menuEditClicked_" + type,
                label: message.get('contextmenu.table.editClicked'),
                onClick: function() {
                    var rowData = clickedSlickGrid.getData()[clickedRow];
                    var dialogData = {
                        gridId: clickedSlickGridProperties.id,
                        selectedRow: rowData
                    };
                    dialog.showPage(message.get("dialog.operations.title"), 'dialogs/mdek_observed_property_dialog.jsp?c=' + userLocale, 600, 300, true, dialogData);
                }
            }));
        },

        _createSensorMLFields: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

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
                    help: "TODO", // TODO
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
            var structure = [
                {
                    field: "sensorIdentifierLabel",
                    name: message.get("table.sensor.identifier.label"),
                    editable: true,
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
                    style: "width: 100%",
                    help: message.get("sensor.classifier.type.help"),
                    isMandatory: true,
                    visible: true,
                    style: "width: 100%"
                }),
                classifierCategoryId, "last");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            id = "sensorClassifierDataGrid";
            var structure = [
                {
                    field: "sensorClassifierLabel",
                    name: message.get("table.sensor.classifier.label"),
                    editable: true,
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



            //  === Capabilities ===
            var capabilityContainer = creator.createRubric({
                id: capabilityCategoryId,
                label: message.get("ui.obj.capability.title"),
                help: message.get("ui.obj.capability.help")
            });
            construct.place(capabilityContainer, classifierCategoryId, "after");

            id = "sensorCapabilityDataGrid";
            var structure = [
                {
                    field: "sensorCapabilityLabel",
                    name: message.get("table.sensor.capability.label"),
                    editable: true,
                    width: "500px"
                },
                {
                    field: "sensorCapabilityLabel",
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
            var structure = [
                {
                    field: "sensorParameterLabel",
                    name: message.get("table.sensor.parameter.label"),
                    editable: true,
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
                    UtilUI.setHide("uiElement1001");
                    UtilUI.setHide("uiElement5000");
                    UtilUI.setHide("uiElement5100");
                    UtilUI.setHide("uiElement6005");

                    // Verschlagwortung
                    UtilUI.setHide("thesaurus");

                    // Raumbezugssystem
                    UtilUI.setHide("uiElement1140");
                    UtilUI.setHide("uiElement3500");

                    // Zeitbezug
                    UtilUI.setHide("uiElement1250");
                    UtilUI.setHide("uiElement1220");
                    UtilUI.setHide("uiElement1240");
                    UtilUI.setHide("uiElement1230");

                    // Zusatzinformation
                    UtilUI.setHide("extraInfo");

                    // Zeitbezug
                    UtilUI.setHide("uiElement5030");
                    //UtilUI.setOptional("uiElement5030", "required");

                    // Verfübarkeit
                    UtilUI.setHide("availability");

                    // Verweise
                    UtilUI.setHide("uiElementN017");
                    UtilUI.setHide("uiElementN018");
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
                    UtilUI.setShow("uiElement1001");
                    UtilUI.setShow("uiElement5000");
                    UtilUI.setShow("uiElement5100");
                    UtilUI.setShow("uiElement6005");

                    // Verschlagwortung
                    UtilUI.setShow("thesaurus");

                    // Raumbezugssystem
                    UtilUI.setShow("uiElement1140");
                    UtilUI.setShow("uiElement3500");

                    // Zeitbezug
                    UtilUI.setShow("uiElement1250");
                    UtilUI.setShow("uiElement1220");
                    UtilUI.setShow("uiElement1240");
                    UtilUI.setShow("uiElement1230");

                    // Zusatzinformation
                    UtilUI.setShow("extraInfo");

                    // Zeitbezug
                    UtilUI.setShow("uiElement5030");
                    //UtilUI.setMandatory("uiElement5030", "required");

                    // Verfübarkeit
                    UtilUI.setShow("availability");

                    // Verweise
                    UtilUI.setShow("uiElementN017");
                    UtilUI.setShow("uiElementN018");
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
        }
    })();
});
