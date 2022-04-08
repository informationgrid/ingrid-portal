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

    subscriptions = [];
    
    return declare(null, {
        title: "Software-Klasse",
        description: "Einführung einer neuen Objektklasse 'Software'",
        defaultActive: true,
        category: "BAW-MIS",

        run: function () {
            // add alternative codelist for address types
            sysLists[-505] = [["Verfahrensbetreuung","20","N",""],["Entwickler","21","N",""],["Eigentümer","3","N",""],["Vertrieb","5","N",""]];
            
            this._createCustomFields();
            
            this._createBehaviours();
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
            addInstallationWith(newFieldsToDirtyCheck, additionalFields);
            
            // Verfügbarkeit
            addQuellenrechte(newFieldsToDirtyCheck, additionalFields);
            addNutzungsrechte(newFieldsToDirtyCheck, additionalFields);
            
            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },

        _createBehaviours: function() {
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class6") {
                    adaptAddresses();

                    adaptLinks();
                    // Folgende Verweisetypen sollen angegeben werden können:
                        // Quellcode-Repository
                        // Dokumentation
                    // Entweder die bestehende Verweise-Tabelle und -Dialogbox verwenden oder durch eine neue ersetzen.
                    
                    // Name der Marktsoftware mandatory if radio is yes
                    handleRequiredState();
                    
                    // Erstellungsvertrag -> Vertragsnr. mandatory if date is set
                    // Erstellungsvertrag -> Datum mandatory if Verstragsnr. is set

                    // Supportvertrag -> Vertragsnr. mandatory if date is set
                    // Supportvertrag -> Datum mandatory if Verstragsnr. is set
                    
                    // HPC text mandatory if checkbox active
                    // Server text mandatory if checkbox active
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

        creator.addToSection("general", creator.addOutlinedSection(
            message.get("ui.obj.baw.software.usergroup.title"),
            message.get("ui.obj.baw.software.usergroup.help"),
            [
                creator.createDomCheckbox({
                    id: "userGroupBAW",
                    name: message.get("ui.obj.baw.software.usergroup.baw"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "userGroupWSV",
                    name: message.get("ui.obj.baw.software.usergroup.wsv"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "userGroupExtern",
                    name: message.get("ui.obj.baw.software.usergroup.extern"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomTextarea({
                    id: "userGroupNotes",
                    name: message.get("ui.obj.baw.software.usergroup.notes.title"),
                    help: message.get("ui.obj.baw.software.usergroup.notes.help"),
                    style: "width: 100%"
                })
            ]));
        var ids = ["userGroupBAW", "userGroupWSV", "userGroupExtern", "userGroupNotes"];
        array.forEach(ids, function(id) {
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
        });
    }
    
    function addProduktiverEinsatz(newFieldsToDirtyCheck, additionalFields) {
        creator.addToSection("general", creator.addOutlinedSection(
            message.get("ui.obj.baw.software.productiveUse.title"),
            message.get("ui.obj.baw.software.productiveUse.help"),
            [
                creator.createDomCheckbox({
                    id: "productiveUseWSVContract",
                    name: message.get("ui.obj.baw.software.productiveUse.wsvContract"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "productiveUseFuE",
                    name: message.get("ui.obj.baw.software.productiveUse.fue"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "productiveUseOther",
                    name: message.get("ui.obj.baw.software.productiveUse.other"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomTextarea({
                    id: "productiveUseNotes",
                    name: message.get("ui.obj.baw.software.productiveUse.notes.title"),
                    help: message.get("ui.obj.baw.software.productiveUse.notes.help"),
                    style: "width: 100%"
                })
            ]));
        var ids = ["productiveUseWSVContract", "productiveUseFuE", "productiveUseOther", "productiveUseNotes"];
        array.forEach(ids, function(id) {
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
        });
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
                field: "operatingSystemWindows",
                name: message.get("ui.obj.baw.software.operatingSystem.windows"),
                type: Editors.YesNoCheckboxCellEditor,
                formatter: Formatters.BoolCellFormatter,
                editable: true,
                initValue: false, // make checkbox appear on cell click
                width: "100px"
            },
            {
                field: "operatingSystemLinux",
                name: message.get("ui.obj.baw.software.operatingSystem.linux"),
                type: Editors.YesNoCheckboxCellEditor,
                formatter: Formatters.BoolCellFormatter,
                editable: true,
                initValue: false, // make checkbox appear on cell click
                width: "100px"
            },
            {
                field: "operatingSystemNotes",
                name: message.get("ui.obj.baw.software.operatingSystem.notes"),
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "operatingSystem",
            name: message.get("ui.obj.baw.software.operatingSystem.title"),
            help: message.get("ui.obj.baw.software.operatingSystem.help"),
            isMandatory: true,
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("operatingSystem");
        additionalFields.push(registry.byId("operatingSystem"));
    }
    
    function addProgrammiersprache(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "programmingLanguage",
                name: message.get("ui.obj.baw.software.programmingLanguage.title"),
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 395030),
                listId: 395030,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "programmingLanguage",
            name: message.get("ui.obj.baw.software.programmingLanguage.title"),
            help: message.get("ui.obj.baw.software.programmingLanguage.help"),
            isMandatory: true,
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("programmingLanguage");
        additionalFields.push(registry.byId("programmingLanguage"));
    }
    
    function addEntwicklungsumgebung(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "developmentEnvironment",
                name: message.get("ui.obj.baw.software.developmentEnvironment.title"),
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950031),
                listId: 3950031,
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
                name: message.get("ui.obj.baw.software.libraries.library"),
                isMandatory: true,
                editable: true
            }
        ];

        creator.createDomDataGrid({
            id: "libraries",
            name: message.get("ui.obj.baw.software.libraries.title"),
            help: message.get("ui.obj.baw.software.libraries.help"),
            isMandatory: true,
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
        creator.addToSection("refClass6", creator.addOutlinedSection(
            message.get("ui.obj.baw.software.installation.title"),
            message.get("ui.obj.baw.software.installation.help"), [
                creator.createDomCheckbox({
                    id: "installationLocal",
                    name: message.get("ui.obj.baw.software.installation.local"),
                    style: "width: 100%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "installationHPC",
                    name: message.get("ui.obj.baw.software.installation.hpc"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomTextarea({
                    id: "installationHPCNotes",
                    style: "width: 85%"
                }),
                creator.createDomCheckbox({
                    id: "installationServer",
                    name: message.get("ui.obj.baw.software.installation.server"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomTextarea({
                    id: "installationServerNotes",
                    style: "width: 85%"
                })
            ]));
        var ids = ["installationLocal", "installationHPC", "installationHPCNotes", "installationServer", "installationServerNotes"];
        array.forEach(ids, function (id) {
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
        });
    }

    function addInstallationWith(newFieldsToDirtyCheck, additionalFields) {
        creator.addToSection("refClass6", creator.createDomSelectBox({
            id: "installBy",
            name: message.get("ui.obj.baw.software.installBy.title"),
            help: message.get("ui.obj.baw.software.installBy.help"),
            isMandatory: true,
            useSyslist: 395032,
            style: "width: 100%"
        }));
        
        newFieldsToDirtyCheck.push("installBy");
        additionalFields.push(registry.byId("installBy"));
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

    function adaptAddresses() {
        // Adressen: Nur folgende Adresstypen sollen vorhanden sein:
        // Verfahrensbetreuung,Entwickler,Vertrieb,Eigentümer
        registry.byId("generalAddress").columns[0].listId = -505
        
        // Validierung: Für alle Adresstypen außer Vertrieb soll mindestens eine Adresse vorhanden sein.
        var subscription = topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
            var addressTypes = array.map(UtilGrid.getTableData("generalAddress"), function (row) {
                return row.nameOfRelation;
            });

            var requiredAddressTypes = ["Verfahrensbetreuung", "Entwickler", "Eigentümer"];
            var containsAllRequiredAddresses = array.every(requiredAddressTypes, function (type) {
                return addressTypes.indexOf(type) !== -1
            });
            if (!containsAllRequiredAddresses) {
                notPublishableIDs.push(["generalAddress", message.get("validation.baw.address.software")]);
            }
        });
        
        subscriptions.push(subscription);
    }

    function adaptLinks() {
        // codelist 2000
    }
    
    function handleRequiredState() {
        
    }
});

