/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "ingrid/IgeEvents",
    "ingrid/layoutCreator",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/behaviours",
    "ingrid/utils/Syslist",
    "ingrid/widgets/UvpPhases"
], function (array, lang, dom, domClass, domStyle, query, topic, registry, IgeEvents, creator, Editors, Formatters, behaviours, UtilSyslist, UvpPhases) {

    return lang.mixin(behaviours, {

        uvpDocumentTypes: {
            title: "UVP Dokumenten Typen",
            description: "Definition der Dokumententypen: UVP, ...",
            defaultActive: true,
            type: "SYSTEM",
            run: function() {

                topic.subscribe("/afterInitDialog/ChooseWizard", function(data) {
                    // remove all assistants
                    data.assistants.splice(0, data.assistants.length);
                });

                // load custom syslists
                UtilSyslist.readSysListData(9000).then(function(entry) {
                    sysLists[9000] = entry;
                });
                UtilSyslist.readSysListData(8001).then(function(entry) {
                    sysLists[8001] = entry;
                });

                // get availbale object classes from codelist 8001
                UtilSyslist.listIdObjectClass = 8001;

                this.hideMenuItems();

                this.handleTreeOperations();

            },

            handleTreeOperations: function() {
                topic.subscribe("/selectNode", function(message) {
                    if (message.id === "dataTree") {
                        if (message.node.id === "objectRoot") {
                            console.log("disable create/paste new object");
                            registry.byId("toolbarBtnNewDoc").set("disabled", true);
                            registry.byId("toolbarBtnPaste").set("disabled", true);
                        }
                    }
                });

                topic.subscribe("/onTreeContextMenu", function(node) {
                    console.log("context menu called from:", node);
                    if (node.item.id === "objectRoot") {
                        registry.byId("menuItemNew").set("disabled", true);
                        registry.byId("menuItemPaste").set("disabled", true);
                    }
                });
            },

            hideMenuItems: function() {
                topic.subscribe("/onMenuBarCreate", function(excludedItems) {
                    excludedItems.push("menuPageStatistics", "menuPageQualityEditor", "menuPageQualityAssurance",
                        "menuPageResearchThesaurus");
                });
                // TODO: remove stack container or do not let them initialized
                // registry.byId("stackContainer").removeChild(registry.byId("pageStatistics"));
            }

        },

        uvpPhaseField: {
            title: "UVP Phasen Feld",
            description: "Hinzufügen von dynamischen Feldern",
            defaultActive: true,
            prefix: "uvp_",
            run: function () {

                // rename default fields
                query("#generalDescLabel label").addContent("Bekanntmachungstext", "only");
                query("#objectNameLabel label").addContent("Vorhabenbezeichnung", "only");
                query("#general .titleBar .titleCaption").addContent("Bekanntmachung", "only");
                query("#general .titleBar").attr("title", "Für die allgemeine Vorhabenbeschreibung sollte der Einfachheit halber der Text der Bekanntmachung verwendet werden.");
                dom.byId("generalAddressTableLabelText").innerHTML = "Federführende Behörde";
                
                // rename Objekte root node
                registry.byId("dataTree").rootNode.getChildren()[0].set("label", "Vorhaben");
                
                // do not override my address title
                IgeEvents.setGeneralAddressLabel = function() {};
                
                this.hideDefaultFields();
                
                this.createFields();
                
                // TODO: additional fields according to #490 and #473

                var self = this;
                topic.subscribe("/onObjectClassChange", function(clazz) {
                    self.prepareDocument(clazz);
                });
            },

            prepareDocument: function(classInfo) {
                console.log("Prepare document for class: ", classInfo);
                var objClass = classInfo.objClass;
                if (objClass === "Class10") {

                } else if (objClass === "Class11") {

                } else if (objClass === "Class12") {
                    
                }
            },
            
            hideDefaultFields: function() {
                domStyle.set("widget_objectName", "width", "550px");
                
                domClass.add(dom.byId("objectClassLabel").parentNode, "hide");
                domClass.add(dom.byId("objectOwnerLabel").parentNode, "hide");
                
                domClass.add(registry.byId("toolbarBtnISO").domNode, "hide");

                domClass.add("uiElement5000", "hide");
                domClass.add("uiElement5100", "hide");
                domClass.add("uiElement5105", "hide");
                domClass.add("uiElement6010", "hide");
                
                // hide all rubrics
                query(".rubric", "contentFrameBodyObject").forEach(function (item) {
                    if (item.id !== "general") {
                        domClass.add(item, "hide");
                    }
                });
            },
            
            createFields: function() {
                var rubric = "general";
                
                new UvpPhases().placeAt("generalContent");
                
                /**
                 * Vorhabensnummer
                 */
                var structure = [
                    { 
                        field: 'categoryId', 
                        name: 'Kategorie', 
                        type: Editors.SelectboxEditor, 
                        editable: true, 
                        listId: 9000,
                        formatter: lang.partial(Formatters.SyslistCellFormatter, 9000),
                        partialSearch: true
                    }
                ];
                var id = "uvpgCategory";
                creator.createDomDataGrid(
                    { id: id, name: "Vorhabensnummer", help: "...", isMandatory: true, visible: "optional", rows: "4", forceGridHeight: false, style: "width:100%" },
                    structure, rubric
                );
                var categoryWidget = registry.byId(id);
                domClass.add(categoryWidget.domNode, "hideTableHeader");
                
                require("ingrid/IgeActions").additionalFieldWidgets.push(categoryWidget);
            }
        }

    });
});
