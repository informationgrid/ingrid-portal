/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
define(["dojo/_base/declare",
        "dojo/_base/array", 
        "dojo/Deferred", 
        "dojo/_base/lang", 
        "dojo/dom-style", 
        "dojo/topic", 
        "dojo/query",
        "dojo/string",
        "dojo/on", 
        "dojo/aspect", 
        "dojo/dom", 
        "dojo/dom-class",
        "dijit/registry", 
        "dojo/cookie",
        "ingrid/message", 
        "ingrid/dialog",
        "ingrid/utils/Grid", 
        "ingrid/utils/UI", 
        "ingrid/utils/List", 
        "ingrid/utils/Syslist"
], function(declare, array, Deferred, lang, style, topic, query, string, on, aspect, dom, domClass, registry, cookie, message, dialog, UtilGrid, UtilUI, UtilList, UtilSyslist) {

    return declare(null, {
        
        inspireIsoConnection: {
            title: "Inspire / ISO - Connection",
            description: "Laut der GDI_DE Konventionen, wird eine ISO Kategorie automatisch zu einem dazugehörigen INSPIRE-Thema hinzugefügt. " +
            		"Diese Kategorie bleibt so lange bestehen, wie auch das INSPIRE-Thema vorhanden ist. Erst wenn das INSPIRE-Thema entfernt " +
            		"wurde, kann auch die Kategorie entfernt werden.",
            description_en: "According to the GDI_DE Conventions, an ISO categorie is added automatically to a corresponding INSPIRE-topic. " +
            		"The category cannot be removed until the INSPIRE topic is present. If an INSPIRE topic is removed, then the ISO " +
            		"category also will be removed.",
            defaultActive: true,
            run: function() {
                
                // mapped INSPIRE-topic IDs to ISO-category IDs
                var mapping = {101: 13, 103: 13, 104: 3, 105: 13, 106: 15, 107: 18, 108: 12, 109: 7, 201: 6, 202: 10,
                        203: 10, 204: 8, 301: 3, 302: 17, 303: 8, 304: 15, 305: 9, 306: 19, 307: 17, 308: 17, 309: 1, 310: 16,
                        311: 15, 312: 8, 313: 4, 315: 14, 316: 14, 317: 2, 318: 2, 319: 2, 320: 5, 321: 5};
                
                var updateIsoCategories = function(id, deleteEntry) {
                    var mappedEntry = mapping[id];
                    if (!mappedEntry) return;
                    
                    var entry = UtilSyslist.getSyslistEntryName( 527, mappedEntry );
                    if (deleteEntry) {
                        // remove all automatically added entries
                        var itemIndexes = [];
                        array.forEach( UtilGrid.getTableData( "thesaurusTopics" ), function(row, index) {
                            if (row.title === mappedEntry)
                                itemIndexes.push( index );
                        } );
                        UtilGrid.removeTableDataRow( "thesaurusTopics", itemIndexes );

                    } else {
                        // check if entry already exists in table
                        var exists = dojo.some( UtilGrid.getTableData( "thesaurusTopics" ), function(row) {
                            return row.title == mappedEntry; // String-Number comparison
                        } );

                        // add entry to table if it doesn't already exist
                        if (!exists) {
                            UtilGrid.addTableDataRow( "thesaurusTopics", { title: mappedEntry } );
                            return entry;
                        }
                    }
                    return false;
                };
                
                // react when inspire topics has been added
                var inspireGridChanges = function(result, args) {
                    console.log("Inspire data behaviour");
                    var msg = args[0];
                    var objClass = registry.byId("objectClass").get("value");
                    // only react if class == 1
                    if (objClass == "Class1") {
                        // remove old dependent values
                        if (msg.oldItem) {
                            updateIsoCategories(msg.oldItem.title, true);
                        }
                        // add new dependent value
                        if (msg.type !== "deleted") {
                            var added = updateIsoCategories(msg.item.title, false);
                            if (added) {
                                UtilUI.showToolTip( "thesaurusInspire", string.substitute(message.get("validation.isocategory.added"), [added]) );
                            }
                        }
                    }
                };
                
                aspect.after(UtilGrid.getTable("thesaurusInspire"), "onCellChange", inspireGridChanges);
                aspect.after(UtilGrid.getTable("thesaurusInspire"), "notifyChangedData", inspireGridChanges);
                
                // if a category is removed that belongs to a set INSPIRE topic, then we won't allow it
                // -> instead we have to remove the topic first
                var isoCategoriesChanges = function(result, args) {
                    var msg = args[0];
                    // if cell is changed, then we must not allow to remove the old item
                    if (msg.oldItem) msg.items = [msg.oldItem];
                    var objClass = registry.byId("objectClass").get("value");
                    // only react if class == 1
                    if (objClass == "Class1") {
                        var topics = UtilGrid.getTableData("thesaurusInspire");
                        array.forEach(msg.items, function(item) {
                            // check if the deleted item is a connected category from an INSPIRE-topic
                            array.some(topics, function(topic) {
                                var mappedId = mapping[topic.title];
                                var id = UtilSyslist.getSyslistEntryKey( 527, item.title );
                                if (mappedId == id) { // Integer and String comparison!!!
                                    // re-insert removed category again
                                    if (msg.oldItem) {
                                        UtilGrid.updateTableDataRow( "thesaurusTopics", msg.row, msg.oldItem );
                                    } else {
                                        UtilGrid.addTableDataRow( "thesaurusTopics", { title: item.title } );
                                    }
                                    
                                    // show tooltip for explanation
                                    var inspireName = UtilSyslist.getSyslistEntryName( 6100, topic.title );
                                    UtilUI.showToolTip( "thesaurusTopics",  string.substitute(message.get("validation.isocategory.delete.dependent"), [inspireName]) );
                                    return true;
                                }
                            });
                        });
                    }
                };
                
                aspect.after(UtilGrid.getTable("thesaurusTopics"), "onCellChange", isoCategoriesChanges );
                aspect.after(UtilGrid.getTable("thesaurusTopics"), "onDeleteItems", isoCategoriesChanges );
            }
        },
        
        coupledResourceDownloadDataCheck: {
            title: "Gekoppelte Daten - Überprüfung auf Download-Daten",
            title_en: "Coupled Resources - Check for download data",
            description: "Wenn eine externe gekoppelte Ressource hinzugefügt wird, dann überprüfe, dass diese Download-Daten enthält.",
            description_en: "When an external coupled resource is being added then also check if the resource contains download data.",
            issue: "https://dev.informationgrid.eu/redmine/issues/153",
            defaultActive: false,
            run: function() {
                topic.subscribe("/afterInitDialog/SelectObject", function(config) {
                    config.ignoreDownloadData = false;
                });
            }
        },
        
        requireUseConstraints: {
            title: "Nutzungsbedingungen - Pflichtfeld bei INSPIRE / Open Data",
            title_en: "Use Constraints - Required on INSPIRE / Open Data",
            description: "Das Feld \"Nutzungsbedingungen\" (ISO: useConstraints + useLimitation) wird verpflichtend, wenn die Checkbox \"INSPIRE-relevanter Datensatz\" oder \"Open Data\" angeklickt wird.",
            description_en: "Input of field \"Use Constraints\" (ISO: useConstraints + useLimitation) is required if checkbox \"INSPIRE-relevant dataset\" or \"Open data\" is set.",
            issue: "https://dev.informationgrid.eu/redmine/issues/223",
            defaultActive: true,
        	run: function() {
        		// define our useConstraints handler
                var updateUseConstraintsBehaviour = function(isChecked) {
		            if (isChecked) {
		                domClass.add("uiElementN027", "required");
		                
		            } else {
		                // remove required field if INSPIRE and open data checkbox not selected
		                if (!registry.byId("isInspireRelevant").checked &&
		                	!registry.byId("isOpenData").checked) {
		                    domClass.remove("uiElementN027", "required");
		                }
		            }
		        };

		        on(registry.byId("isInspireRelevant"), "Change", updateUseConstraintsBehaviour);
		        on(registry.byId("isOpenData"), "Change", updateUseConstraintsBehaviour);
            }
        },
        
        showFileDescription: {
            title: "Dateibeschreibung - Einblenden bei vorhandenem Bild",
            description: "Das Feld \"Dateibeschreibung\" wird nur dann eingeblendet, wenn auch ein Link zur Vorschaugrafik eingegeben worden ist.",
            defaultActive: true,
            run: function() {
                // set field initially hidden
                domClass.add("uiElement5105", "hidden");
                
                // react when object is loaded (passive)
                on(registry.byId("generalPreviewImage"), "Change", function(value) {
                    if (value.trim().length === 0) {
                        domClass.add("uiElement5105", "hidden");
                    } else {
                        domClass.remove("uiElement5105", "hidden");
                    }
                });
                
                // react on user input (active)
                on(registry.byId("generalPreviewImage"), "KeyUp", function() {
                    if (this.get("value").trim().length === 0) {
                        domClass.add("uiElement5105", "hidden");
                    } else {
                        domClass.remove("uiElement5105", "hidden");
                    }
                });
            }
        },
        
        encodingSchemeForGeodatasets: {
            title: "Kodierungsschema nur für Geodatensätze",
            description: "Für Geodatensätze wird das Feld \"Kodierungsschema der geographischen Daten\" angezeigt, für andere Klassen ist es ausgeblendet.",
            defaultActive: true,
            run: function() {
                topic.subscribe("/onObjectClassChange", function(data) {
                    if (data.objClass === "Class1") {
                        // set field initially hidden
                        // "Kodierungsschema der geographischen Daten" 
                        domClass.remove("uiElement1315", "hide");
    
                    } else {
                        // "Kodierungsschema der geographischen Daten" only in class 1
                        domClass.add("uiElement1315", "hide");
                        // remove any previous value from now hidden field
                        registry.byId("availabilityDataFormatInspire").set("value", "");
                    }
                });
            }
        },
        
        foldersInHierarchy: {
            title: "Ordnerstruktur in Hierarchiebaum",
            description: "Fügt die Auswahl einer Klasse vom Typ Ordner hinzu, so dass Daten besser strukturiert werden können.",
            defaultActive: true,
            type: "SYSTEM",
            run: function() {
                // add new object class
                sysLists[8000].push(["Ordner", "1000", "N", ""]);
                
                // handle folder class selection
                topic.subscribe("/onObjectClassChange", function(data) {
                    if (data.objClass === "Class1000") {
                        domClass.add("contentFrameBodyObject", "hide");
    
                    } else {
                        domClass.remove("contentFrameBodyObject", "hide");
                    }
                });
                
                // handle toolbar when folder is selected
                // -> only disable toolbar buttons that are not needed (be careful with IgeToolbar-Class-behaviour)
                topic.subscribe("/selectNode", function(message) {
                    // do not handle if another tree was selected!
                    if (message.id && message.id != "dataTree") return;

                    var selectedNode = message.node;
                    
                    // if we didn't select a folder then leave
                    if (selectedNode.objectClass !== 1000) return;
                    
                    // disable all buttons when a folder was clicked
                    registry.byId("toolbarBtnShowComments").set("disabled", true);
                    registry.byId("toolbarBtnShowChanges").set("disabled", true);
                    registry.byId("toolbarBtnFinalSave").set("disabled", true);
                    registry.byId("toolbarBtnreassign").set("disabled", true);
                    registry.byId("toolbarBtnDiscard").set("disabled", true);
                    registry.byId("toolbarBtnISO").set("disabled", true);
                    registry.byId("toolbarBtnPrintDoc").set("disabled", true);
                });
            }
        }
        
        /*
         * ABORTED: The ATOM URL has to be maintained when automatically inserted into document. It's better to adapt the context help
         * to let the user know that the URL is added to the IDF.
        atomOperationConnection: {
            title: "ATOM - Operations Connection",
            description: "When a service is of type ATOM then the operation 'Get Download Service Metadata' is mandatory.",
            issue: "https://dev.informationgrid.eu/redmine/issues/84",
            run: function() {
                var handler = null;
                on(registry.byId("ref3IsAtomDownload"), "change", function(isChecked) {
                    if (isChecked) {
                        handler = topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                            console.log("atom check before publish");
                            var data = UtilGrid.getTableData("ref3Operation");
                            var hasOperationDownloadServiceMeta = data.some(function(item) { return item.name === "Get Download Service Metadata"; });
                            if (!hasOperationDownloadServiceMeta) {
                                notPublishableIDs.push( ["ref3Operation", message.get("validation.error.missing.operation.download")] );
                            }
                        });
                    } else {
                        if (handler) {
                            handler.remove();
                            handler = null;
                        }
                    }
                });
            }
        }*/
        
    } )();
});
