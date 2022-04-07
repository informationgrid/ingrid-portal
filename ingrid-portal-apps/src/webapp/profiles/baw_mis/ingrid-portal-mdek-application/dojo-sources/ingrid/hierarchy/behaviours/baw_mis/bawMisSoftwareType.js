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
    "dojo/on",
    "dojo/topic",
    "dijit/form/RadioButton",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message"
], function (registry, array, declare, lang, dom, domClass, on, topic, RadioButton, Editors, Formatters, dirty, creator, message) {

    
    return declare(null, {
        title: "Software-Klasse",
        description: "Einführung einer neuen Objektklasse 'Software'",
        defaultActive: true,
        category: "BAW-MIS",

        run: function () {
            this._createCustomFields();
            
            // this._createBehaviours();
            
            /*on(registry.byId("bawHierarchyLevelName"), "Change", function(value) {
                if (value === "Messdaten") {
                    handleHierarchyLevelNameIsMeasurementData();
                    hideFields(nonMeasurementFields);
                } else {
                    handleHierarchyLevelNameIsNotMeasurementData();
                    if (levelNameForSimulation.indexOf(value) !== -1) {
                        showFields(nonMeasurementFields);
                    }
                }
            });*/
        },

        _createCustomFields: function () {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];
            
            // Allgemeines
            addEinsatzzweck(newFieldsToDirtyCheck, additionalFields);
            addNutzerkreis(newFieldsToDirtyCheck, additionalFields);
            addProduktiverEinsatz(newFieldsToDirtyCheck, additionalFields);
            
            // Fachbezug
            addErganzungsmodul(newFieldsToDirtyCheck, additionalFields);
            addBetriebssystem(newFieldsToDirtyCheck, additionalFields);
            addProgrammiersprache(newFieldsToDirtyCheck, additionalFields);
            addEntwicklungsumgebung(newFieldsToDirtyCheck, additionalFields);
            addBibliotheken(newFieldsToDirtyCheck, additionalFields);
            addErstellungsvertrag(newFieldsToDirtyCheck, additionalFields);
            addSupportvertrag(newFieldsToDirtyCheck, additionalFields);
            addInstallation(newFieldsToDirtyCheck, additionalFields);
            
            // Verfügbarkeit
            addQuellenrechte(newFieldsToDirtyCheck, additionalFields);
            addNutzungsrechte(newFieldsToDirtyCheck, additionalFields);
            
            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },

        _createBehaviours: function() {
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class6") {
                    // Adressen: Nur folgende Adresstypen sollen vorhanden sein (entweder Codeliste tauschen oder die Standard-Adressentabelle durch eine neue ersetzen):
                        // Verfahrensbetreuung
                        // Entwickler
                        // Vertrieb
                        // Eigentümer
                    // Validierung: Für alle Adresstypen außer Vertrieb soll mindestens eine Adresse vorhanden sein.
                    
                    // Folgende Verweisetypen sollen angegeben werden können:
                        // Quellcode-Repository
                        // Dokumentation
                    // Entweder die bestehende Verweise-Tabelle und -Dialogbox verwenden oder durch eine neue ersetzen.
                } else {

                }
            });
            
            // TODO: Name der Marktsoftware required if radio button is yes
            
            
        },
    })();

    function addEinsatzzweck(newFieldsToDirtyCheck, additionalFields) {
        creator.addToSection("general", creator.createDomTextbox({
            id: "purpose",
            name: message.get("ui.obj.baw.software.purpose.title"),
            help: message.get("ui.obj.baw.software.purpose.help"),
            style: "width: 100%"
        }));
        newFieldsToDirtyCheck.push("purpose");
        additionalFields.push(registry.byId("purpose"));
    }
    
    function addNutzerkreis(newFieldsToDirtyCheck, additionalFields) {
        creator.addToSection("general", creator.createDomSelectBox({
            id: "userGroup",
            name: message.get("ui.obj.baw.software.usergroup.title"),
            help: message.get("ui.obj.baw.software.usergroup.help"),
            isMandatory: true,
            // useSyslist: 0,
            listEntries: [],
            style: "width: 100%"
        }));
        newFieldsToDirtyCheck.push("userGroup");
        additionalFields.push(registry.byId("userGroup"));
    }
    
    function addProduktiverEinsatz(newFieldsToDirtyCheck, additionalFields) {
        creator.addToSection("general", creator.createDomSelectBox({
            id: "productiveUse",
            name: message.get("ui.obj.baw.software.productiveUse.title"),
            help: message.get("ui.obj.baw.software.productiveUse.help"),
            isMandatory: true,
            useSyslist: 0,
            listEntries: [],
            style: "width: 100%"
        }));
        newFieldsToDirtyCheck.push("productiveUse");
        additionalFields.push(registry.byId("productiveUse"));
    }
    
    function addErganzungsmodul(newFieldsToDirtyCheck, additionalFields) {
        var hasSupplementaryModule = new RadioButton({
            id: "hasSupplementaryModule",
            checked: false,
            value: "true",
            name: "supplementaryModule"
        });
        var hasNotSupplementaryModule = new RadioButton({
            id: "hasNotSupplementaryModule",
            checked: false,
            value: "false",
            name: "supplementaryModule"
        });

        creator.addToSection("refClass6", creator.addOutlinedSection("", "", [
            creator.addRadioSurroundingContainer(
                [hasSupplementaryModule.domNode, hasNotSupplementaryModule.domNode],
                {
                    id: "supplementaryModule",
                    isMandatory: true,
                    name: [message.get("ui.obj.baw.software.supplementaryModule.yes"), message.get("ui.obj.baw.software.supplementaryModule.no")],
                    label: message.get("ui.obj.baw.software.supplementaryModule.label")
                }
            ),
            creator.createDomTextarea({
                id: "nameOfSoftware",
                name: message.get("ui.obj.baw.software.name.title"),
                help: message.get("ui.obj.baw.software.name.help"),
                style: "width: 100%"
            })
        ]));

        newFieldsToDirtyCheck.push("hasSupplementaryModule");
        newFieldsToDirtyCheck.push("hasNotSupplementaryModule");
        newFieldsToDirtyCheck.push("nameOfSoftware");
        additionalFields.push(hasSupplementaryModule);
        additionalFields.push(hasNotSupplementaryModule);
        additionalFields.push(registry.byId("nameOfSoftware"));
    }
    
    function addBetriebssystem(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "operatingSystem",
                name: message.get("ui.obj.baw.software.operatingSystem.title") + "*",
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 0),
                listId: 0,
                isMandatory: true,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "programmingLanguage",
            name: message.get("ui.obj.baw.software.programmingLanguage.title"),
            help: message.get("ui.obj.baw.software.programmingLanguage.help"),
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("programmingLanguage");
        additionalFields.push(registry.byId("programmingLanguage"));
    }
    
    function addProgrammiersprache(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "operatingSystem",
                name: message.get("ui.obj.baw.software.operatingSystem.title") + "*",
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 0),
                listId: 0,
                isMandatory: true,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "operatingSystem",
            name: message.get("ui.obj.baw.software.operatingSystem.title"),
            help: message.get("ui.obj.baw.software.operatingSystem.help"),
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("operatingSystem");
        additionalFields.push(registry.byId("operatingSystem"));
    }
    
    function addEntwicklungsumgebung(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "developmentEnvironment",
                name: message.get("ui.obj.baw.software.developmentEnvironment.title") + "*",
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 0),
                listId: 0,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "developmentEnvironment",
            name: message.get("ui.obj.baw.software.developmentEnvironment.title"),
            help: message.get("ui.obj.baw.software.developmentEnvironment.help"),
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("developmentEnvironment");
        additionalFields.push(registry.byId("developmentEnvironment"));
    }
    
    function addBibliotheken(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "library",
                name: message.get("ui.obj.baw.software.libraries.library") + "*",
                isMandatory: true,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "libraries",
            name: message.get("ui.obj.baw.software.libraries.title"),
            help: message.get("ui.obj.baw.software.libraries.help"),
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("libraries");
        additionalFields.push(registry.byId("libraries"));
    }
    
    function addErstellungsvertrag(newFieldsToDirtyCheck, additionalFields) {

        creator.addToSection("refClass6",
            creator.addOutlinedSection(
                message.get("ui.obj.baw.software.creationContract.title"),
                message.get("ui.obj.baw.software.creationContract.help"),
                [
                    creator.createDomTextbox({
                        id: "creationContractNumber",
                        name: message.get("ui.obj.baw.software.creationContract.number.title"),
                        help: message.get("ui.obj.baw.software.creationContract.number.help"),
                        style: "width: 75%"
                    }),
                    creator.createDomDatebox({
                        id: "creationContractDate",
                        name: message.get("ui.obj.baw.software.creationContract.date.title"),
                        help: message.get("ui.obj.baw.software.creationContract.date.help"),
                        style: "width: 25%"
                    })
                ]
            )
        );

        newFieldsToDirtyCheck.push("creationContractNumber");
        newFieldsToDirtyCheck.push("creationContractDate");
        additionalFields.push(registry.byId("creationContractNumber"));
        additionalFields.push(registry.byId("creationContractDate"));
    }
    
    function addSupportvertrag(newFieldsToDirtyCheck, additionalFields) {

        creator.addToSection("refClass6",
            creator.addOutlinedSection(
                message.get("ui.obj.baw.software.supportContract.title"),
                message.get("ui.obj.baw.software.supportContract.help"),
                [
                    creator.createDomTextbox({
                        id: "supportContractNumber",
                        name: message.get("ui.obj.baw.software.supportContract.number.title"),
                        help: message.get("ui.obj.baw.software.supportContract.number.help"),
                        style: "width: 75%"
                    }),
                    creator.createDomDatebox({
                        id: "supportContractDate",
                        name: message.get("ui.obj.baw.software.supportContract.date.title"),
                        help: message.get("ui.obj.baw.software.supportContract.date.help"),
                        style: "width: 25%"
                    }),
                    creator.createDomTextarea({
                        id: "supportContractNotes",
                        name: message.get("ui.obj.baw.software.supportContract.notes.title"),
                        help: message.get("ui.obj.baw.software.supportContract.notes.help"),
                        style: "width: 100%"
                    })
                ]
            )
        );

        newFieldsToDirtyCheck.push("supportContractNumber");
        newFieldsToDirtyCheck.push("supportContractDate");
        additionalFields.push(registry.byId("supportContractNumber"));
        additionalFields.push(registry.byId("supportContractDate"));
        
    }
    
    function addInstallation(newFieldsToDirtyCheck, additionalFields) {
        creator.addToSection("refClass6",
            creator.createDomSelectBox({
                id: "installation",
                name: message.get("ui.obj.baw.software.installation.title"),
                help: message.get("ui.obj.baw.software.installation.help"),
                isMandatory: true,
                useSyslist: 0,
                listEntries: [],
                style: "width: 100%"
            }));
        newFieldsToDirtyCheck.push("installation");
        additionalFields.push(registry.byId("installation"));
    }
    
    function addQuellenrechte(newFieldsToDirtyCheck, additionalFields) {
        var hasSourceRights = new RadioButton({
            id: "hasSourceRights",
            checked: false,
            value: "true",
            name: "sourceRights"
        });
        var hasNotSourceRights = new RadioButton({
            id: "hasNotSourceRights",
            checked: false,
            value: "false",
            name: "sourceRights"
        });

        creator.addToSection("availability", creator.addOutlinedSection("", "", [
            creator.addRadioSurroundingContainer(
                [hasSourceRights.domNode, hasNotSourceRights.domNode], {
                    id: "sourceRights",
                    isMandatory: true,
                    name: [message.get("ui.obj.baw.software.sourceRights.yes"), message.get("ui.obj.baw.software.sourceRights.no")],
                    label: message.get("ui.obj.baw.software.sourceRights.label")
                }
            ),
            creator.createDomTextarea({
                id: "sourceRightsNotes",
                name: message.get("ui.obj.baw.software.sourceRightsNotes.title"),
                help: message.get("ui.obj.baw.software.sourceRightsNotes.help"),
                style: "width: 100%"
            })
        ]));
        newFieldsToDirtyCheck.push("hasSourceRights");
        newFieldsToDirtyCheck.push("hasNotSourceRights");
        newFieldsToDirtyCheck.push("sourceRightsNotes");
        additionalFields.push(hasSourceRights);
        additionalFields.push(hasNotSourceRights);
        additionalFields.push(registry.byId("sourceRightsNotes"));
    }
    
    function addNutzungsrechte(newFieldsToDirtyCheck, additionalFields) {
        var hasUsageRights = new RadioButton({
            id: "hasUsageRights",
            checked: false,
            value: "true",
            name: "sourceRights"
        });
        var hasNotUsageRights = new RadioButton({
            id: "hasNotUsageRights",
            checked: false,
            value: "false",
            name: "sourceRights"
        });

        creator.addToSection("availability", creator.addOutlinedSection("", "", [
            creator.addRadioSurroundingContainer(
                [hasUsageRights.domNode, hasNotUsageRights.domNode], {
                    id: "usageRights",
                    isMandatory: true,
                    name: [message.get("ui.obj.baw.software.usageRights.yes"), message.get("ui.obj.baw.software.usageRights.no")],
                    label: message.get("ui.obj.baw.software.usageRights.label")
                }
            ),
            creator.createDomTextarea({
                id: "usageRightsNotes",
                name: message.get("ui.obj.baw.software.usageRightsNotes.title"),
                help: message.get("ui.obj.baw.software.usageRightsNotes.help"),
                style: "width: 100%"
            })
        ]));
        
        newFieldsToDirtyCheck.push("hasUsageRights");
        newFieldsToDirtyCheck.push("hasNotUsageRights");
        newFieldsToDirtyCheck.push("usageRightsNotes");
        additionalFields.push(hasUsageRights);
        additionalFields.push(hasNotUsageRights);
        additionalFields.push(registry.byId("usageRightsNotes"));
    }
    
});

