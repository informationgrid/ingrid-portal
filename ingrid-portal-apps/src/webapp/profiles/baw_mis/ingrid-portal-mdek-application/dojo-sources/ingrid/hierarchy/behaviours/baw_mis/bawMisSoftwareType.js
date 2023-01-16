/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
    "dijit/form/RadioButton",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist"
], function (registry, array, declare, lang, dom, domClass, domConstruct, on, topic, RadioButton, Editors, Formatters, dirty, creator, message, UtilGrid, UtilSyslist) {

    subscriptions = [];

    origCodelist2000 = [];
    
    return declare(null, {
        title: "Software-Klasse",
        description: "Einführung einer neuen Objektklasse 'Software'",
        defaultActive: true,
        category: "BAW-MIS",

        run: function () {
            // add alternative codelist for address types and link types
            sysLists[-505] = [["Verfahrensbetreuung","20","N",""],["Entwickler","21","N",""],["Eigentümer","3","N",""],["Vertrieb","5","N",""]];
            sysLists[-2000] = [["Quellcode-Repository","20","N","6"],["Dokumentation","21","N","6"]];
            origCodelist2000 = sysLists[2000];
            
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
                    
                    handleRequiredState();
                    
                    hideDoi();
                } else {
                    removeBehaviours();
                    showDoi();
                }
            });
        }
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
            "userGroup",
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
            "productiveUse",
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

        creator.addToSection("refClass6", creator.addOutlinedSection("supplementaryModule", message.get("ui.obj.baw.software.supplementaryModule.label"), "", [
            creator.addRadioSurroundingContainer(
                [hasSupplementaryModule.domNode, hasNotSupplementaryModule.domNode],
                {
                    id: "supplementaryModule",
                    isMandatory: true,
                    name: [message.get("ui.obj.baw.software.supplementaryModule.yes"), message.get("ui.obj.baw.software.supplementaryModule.no")]
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

        creator.addToSection("refClass6", creator.addOutlinedSection(
            "operatingSystem",
            message.get("ui.obj.baw.software.operatingSystem.title"),
            message.get("ui.obj.baw.software.operatingSystem.help"),
            [
                creator.createDomCheckbox({
                    id: "operatingSystemWindows",
                    name: message.get("ui.obj.baw.software.operatingSystem.windows"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "operatingSystemLinux",
                    name: message.get("ui.obj.baw.software.operatingSystem.linux"),
                    style: "width: 15%",
                    disableHelp: true
                }),
                creator.createDomTextarea({
                    id: "operatingSystemNotes",
                    name: message.get("ui.obj.baw.software.operatingSystem.notes.title"),
                    help: message.get("ui.obj.baw.software.operatingSystem.notes.help"),
                    style: "width: 100%"
                })
            ]));
        var ids = ["operatingSystemWindows", "operatingSystemLinux", "operatingSystemNotes"];
        array.forEach(ids, function(id) {
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
        });
        
    }
    
    function addProgrammiersprache(newFieldsToDirtyCheck, additionalFields) {
        var structure = [
            {
                field: "programmingLanguage",
                name: message.get("ui.obj.baw.software.programmingLanguage.title"),
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950030),
                listId: 3950030,
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
            style: "width: 100%"
        }, structure, "refClass6");
        newFieldsToDirtyCheck.push("libraries");
        additionalFields.push(registry.byId("libraries"));
    }
    
    function addErstellungsvertrag(newFieldsToDirtyCheck, additionalFields) {

        creator.addToSection("refClass6",
            creator.addOutlinedSection(
                "creationContract",
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
                "supportContract",
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
        var structureHlrName = [
            {
                field: "text",
                name: message.get("ui.obj.baw.software.installation.hlrName"),
                type: Editors.ComboboxEditor,
                formatter: lang.partial(Formatters.SyslistCellFormatter, 3950033),
                listId: 3950033,
                editable: true
            }
        ];
        creator.createDomDataGrid({
            id: "installationHLRNames",
            name: message.get("ui.obj.baw.software.installationHLRNames.title"),
            help: message.get("ui.obj.baw.software.installationHLRNames.help"),
            style: "width: 85%"
        }, structureHlrName, "refClass6");
        var structureServerName = [
            {
                field: "text",
                name: message.get("ui.obj.baw.software.installation.serverName"),
                editable: true
            }
        ];
        creator.createDomDataGrid({
            id: "installationServerNames",
            name: message.get("ui.obj.baw.software.installationServerNames.title"),
            help: message.get("ui.obj.baw.software.installationServerNames.help"),
            style: "width: 85%"
        }, structureServerName, "refClass6");
        creator.addToSection("refClass6", creator.addOutlinedSection(
            "installation",
            message.get("ui.obj.baw.software.installation.title"),
            message.get("ui.obj.baw.software.installation.help"), [
                creator.createDomCheckbox({
                    id: "installationLocal",
                    name: message.get("ui.obj.baw.software.installation.local"),
                    style: "width: 100%",
                    disableHelp: true
                }),
                creator.createDomCheckbox({
                    id: "installationHLR",
                    name: message.get("ui.obj.baw.software.installation.hlr"),
                    disableHelp: true,
                    style: "width: 15%",
                }),
                creator.createDomCheckbox({
                    id: "installationServer",
                    name: message.get("ui.obj.baw.software.installation.server"),
                    disableHelp: true,
                    style: "width: 15%",
                })
            ]));
        
        domConstruct.place("uiElementAddinstallationHLRNames", "uiElementAddinstallationHLR", "after");
        domConstruct.place("uiElementAddinstallationServerNames", "uiElementAddinstallationServer", "after");
        registry.byId("installationHLRNames").reinitLastColumn(true);
        registry.byId("installationServerNames").reinitLastColumn(true);
        
        var ids = ["installationLocal", "installationHLR", "installationHLRNames", "installationServer", "installationServerNames"];
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
            useSyslist: 3950032,
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

        creator.addToSection("availability", creator.addOutlinedSection("sourceRights",
            message.get("ui.obj.baw.software.sourceRights.label"),
            "",
            [
                creator.addRadioSurroundingContainer(
                    [hasSourceRights.domNode, hasNotSourceRights.domNode], {
                        id: "sourceRights",
                        isMandatory: true,
                        name: [message.get("ui.obj.baw.software.sourceRights.yes"), message.get("ui.obj.baw.software.sourceRights.no")]
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
            name: "usageRights"
        });
        var hasNotUsageRights = new RadioButton({
            id: "hasNotUsageRights",
            checked: false,
            value: "false",
            name: "usageRights"
        });

        creator.addToSection("availability", creator.addOutlinedSection(
            "usageRights",
            message.get("ui.obj.baw.software.usageRights.label"),
            "",
            [
                creator.addRadioSurroundingContainer(
                    [hasUsageRights.domNode, hasNotUsageRights.domNode], {
                        id: "usageRights",
                        isMandatory: true,
                        name: [message.get("ui.obj.baw.software.usageRights.yes"), message.get("ui.obj.baw.software.usageRights.no")]
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
        sysLists[2000] = sysLists[-2000];

        // reset filter to get all data
        UtilGrid.getTable("linksTo").setRowFilter(null);
        
        // update relation type names because of changed codelist
        var data = UtilGrid.getTableData("linksTo");
        array.forEach(data, function (entry) {
            var entryName = UtilSyslist.getSyslistEntryName(2000, entry.relationType);
            if (entryName != entry.relationType) {
                entry.relationTypeName = entryName;
            }
        });
        UtilGrid.setTableData("linksTo", data);

        require("ingrid/IgeEvents").setLinksToRelationTypeFilterContent({objClass: "Class" + currentUdk.objectClass});
    }
    
    function handleRequiredState() {
        
        // Marktsoftware
        var radioSupplementaryYes = registry.byId("hasSupplementaryModule");
        var radioSupplementaryNo = registry.byId("hasNotSupplementaryModule");
        // BAW-Rechte
        var radioSourceRightsYes = registry.byId("hasSourceRights");
        var radioSourceRightsNo = registry.byId("hasNotSourceRights");
        // Nutzung durch Dritte
        var radioUsageRightsYes = registry.byId("hasUsageRights");
        var radioUsageRightsNo = registry.byId("hasNotUsageRights");
        
        var objectClass = registry.byId("objectClass");
        
        var publishSubscription = topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
            if (objectClass.value === "Class6") {
                if (!radioSupplementaryYes.checked && !radioSupplementaryNo.checked) {
                    notPublishableIDs.push(["uiElementAddsupplementaryModule", message.get("validation.baw.supplementaryModule.required")]);
                }
                if (!radioSourceRightsYes.checked && !radioSourceRightsNo.checked) {
                    notPublishableIDs.push(["uiElementAddsourceRights", message.get("validation.baw.supplementaryModule.required")]);
                }
                if (!radioUsageRightsYes.checked && !radioUsageRightsNo.checked) {
                    notPublishableIDs.push(["uiElementAddusageRights", message.get("validation.baw.supplementaryModule.required")]);
                }
            }
        });
        subscriptions.push(publishSubscription);
        
        var requiredOnCheck = function(target, checked) {
            if (checked) domClass.add(target, "required"); else domClass.remove(target, "required");
        }

        on(radioSupplementaryYes, "change", lang.partial(requiredOnCheck, "uiElementAddnameOfSoftware"));
        
        var handleRequiredState = function(field1, field2) {
            var value1 = registry.byId(field1).value;
            var value2 = registry.byId(field2).value;
            if (value1 || value2) {
                domClass.add("uiElementAdd" + field1, "required");
                domClass.add("uiElementAdd" + field2, "required");
            } else {
                domClass.remove("uiElementAdd" + field1, "required");
                domClass.remove("uiElementAdd" + field2, "required");
            }
        }
        
        // Erstellungsvertrag
        on(registry.byId("creationContractNumber"), "change", lang.partial(handleRequiredState, "creationContractNumber", "creationContractDate"));
        on(registry.byId("creationContractDate"), "change", lang.partial(handleRequiredState, "creationContractNumber", "creationContractDate"));
        
        // Supportvertrag
        on(registry.byId("supportContractNumber"), "change", lang.partial(handleRequiredState, "supportContractNumber", "supportContractDate"));
        on(registry.byId("supportContractDate"), "change", lang.partial(handleRequiredState, "supportContractNumber", "supportContractDate"));
        
        // Installationsort
        on(registry.byId("installationHLR"), "change", lang.partial(requiredOnCheck, "uiElementAddinstallationHLRNames"));
        on(registry.byId("installationServer"), "change", lang.partial(requiredOnCheck, "uiElementAddinstallationServerNames"));
        
    }
    
    function hideDoi() {
        if (dom.byId("uiElementAdddoiDataCite")) {
            domClass.add("uiElementAdddoiDataCite", "hide");
        }
    }
    
    function showDoi() {
        if (dom.byId("uiElementAdddoiDataCite")) {
            domClass.remove("uiElementAdddoiDataCite", "hide");
        }
    }
    
    function removeBehaviours() {
        registry.byId("generalAddress").columns[0].listId = 505;
        sysLists[2000] = origCodelist2000;

        array.forEach(subscriptions, function (subscription) {
            subscription.remove();
        });

        require("ingrid/IgeEvents").setLinksToRelationTypeFilterContent({objClass: "Class" + currentUdk.objectClass});
    }
});

