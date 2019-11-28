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

// this global variable can be used to add information of an activated behaviour, e.g. it has
// been activated, so that this information can be used in another function
var igeOptions = {};

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
        "ingrid/utils/Syslist",
        "ingrid/hierarchy/behaviours/addresses",
        "ingrid/hierarchy/behaviours/opendata",
        "ingrid/hierarchy/behaviours/folders",
        "ingrid/hierarchy/behaviours/inspireRelevant/conformFields",
        "ingrid/hierarchy/behaviours/inspireRelevant/dataformat",
        "ingrid/hierarchy/behaviours/inspireRelevant/spatialSystems",
        "ingrid/hierarchy/behaviours/inspireRelevant/geoservice",
        "ingrid/hierarchy/behaviours/inspireRelevant/inspireIsoConnection",
        "ingrid/hierarchy/behaviours/advCompatible",
        "ingrid/hierarchy/behaviours/administrativeArea",
        "ingrid/hierarchy/behaviours/advProductGroup",
        "ingrid/hierarchy/behaviours/inspireRelevant",
        "ingrid/hierarchy/behaviours/thesaurusInspire",
        "ingrid/hierarchy/behaviours/thesaurusTopics",
        "ingrid/hierarchy/behaviours/thesaurusEnvironment",
        "ingrid/hierarchy/behaviours/ref1Representation",
        "ingrid/hierarchy/behaviours/ref1SymbolsText",
        "ingrid/hierarchy/behaviours/ref1KeysText",
        "ingrid/hierarchy/behaviours/dataQualitySection",
        "ingrid/hierarchy/behaviours/ref3BaseDataLink",
        "ingrid/hierarchy/behaviours/ref3Operations",
        "ingrid/hierarchy/behaviours/ref3CouplingType",
        "ingrid/hierarchy/behaviours/ref5KeysText",
        "ingrid/hierarchy/behaviours/serviceUrls",
        "ingrid/hierarchy/behaviours/spatialRefAdminUnit",
        "ingrid/hierarchy/behaviours/spatialRefLocation",
        "ingrid/hierarchy/behaviours/spatialRefHeight",
        "ingrid/hierarchy/behaviours/spatialRepresentationInfo",
        "ingrid/hierarchy/behaviours/timeRefTable",
        "ingrid/hierarchy/behaviours/timeRefDate",
        "ingrid/hierarchy/behaviours/timeRefIntervalUnit",
        "ingrid/hierarchy/behaviours/extraInfoLangData",
        "ingrid/hierarchy/behaviours/extraInfoCharSetData",
        "ingrid/hierarchy/behaviours/parentIdentifier",
        "ingrid/hierarchy/behaviours/deleteNonEmptyFolders",
        "ingrid/hierarchy/behaviours/inspireRelevant/accessConstraints"
], function(declare, array, Deferred, lang, style, topic, query, string, on, aspect, dom, domClass, registry, cookie, message,
            dialog, UtilGrid, UtilUI, UtilList, UtilSyslist,
            addresses, openData, foldersInHierarchy, conformityFields, dataformat, spatialSystems, inspireGeoservice, inspireIsoConnection,
            advCompatible, adminitrativeArea, advProductGroup, inspireRelevant, thesaurusInspire, thesaurusTopics,
            thesaurusEnvironment, ref1Representation, ref1SymbolsText, ref1KeysText, dataQualitySection,
            ref3BaseDataLink, ref3Operations, ref3CouplingType, ref5KeysText,
            serviceUrls, spatialRefAdminUnit, spatialRefLocation, spatialRefHeight,
            timeRefTable, timeRefDate, timeRefIntervalUnit,
            extraInfoLangData, extraInfoCharSetData,
            spatialRepresentationInfo, parentIdentifier, deleteNonEmptyFolders, accessConstraints) {

    return declare(null, {
        
        addresses : addresses,

        advCompatible : advCompatible,

        advProductGroup : advProductGroup,

        inspireRelevant : inspireRelevant,

        thesaurusInspire : thesaurusInspire,

        thesaurusTopics: thesaurusTopics,

        thesaurusEnvironment: thesaurusEnvironment,

        ref1Representation: ref1Representation,

        ref1SymbolsText: ref1SymbolsText,

        ref1KeysText: ref1KeysText,

        dataQualitySection: dataQualitySection,

        ref3BaseDataLink: ref3BaseDataLink,

        ref3Operations: ref3Operations,

        ref3CouplingType: ref3CouplingType,

        ref5KeysText: ref5KeysText,

        serviceUrls: serviceUrls,

        spatialRefAdminUnit: spatialRefAdminUnit,

        spatialRefLocation: spatialRefLocation,

        spatialRefHeight: spatialRefHeight,

        timeRefTable: timeRefTable,

        timeRefDate: timeRefDate,

        timeRefIntervalUnit: timeRefIntervalUnit,

        extraInfoLangData: extraInfoLangData,

        extraInfoCharSetData: extraInfoCharSetData,

        administrativeArea: adminitrativeArea,
        
        conformityFields: conformityFields,

        dataformat: dataformat,

        spatialSystems: spatialSystems,

        inspireGeoservice: inspireGeoservice,
        
        inspireIsoConnection: inspireIsoConnection,

        // Not needed anymore since specifications have been removed and cannot be mapped
        // inspireConformityConnection: inspireConformityConnection,

        spatialRepresentationInfo: spatialRepresentationInfo,

        deleteNonEmptyFolders: deleteNonEmptyFolders,

        accessContraintsField: accessConstraints,

        // REMOVED: see https://redmine.informationgrid.eu/issues/364#note-11
        // parentIdentifier: parentIdentifier,
        
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
            title: "Nutzungsbedingungen - Pflichtfeld für Geodatensatz und -dienst",
            description: "Das Feld \"Nutzungsbedingungen\" (ISO: useConstraints + useLimitation) wird verpflichtend, wenn die die Objektklasse 1 oder 3 ausgewählt wurde.",
            issue: "https://dev.informationgrid.eu/redmine/issues/223,1362",
            defaultActive: true,
        	run: function() {
                topic.subscribe("/onObjectClassChange", function(data) {
                    if (data.objClass === "Class1" || data.objClass === "Class3") {
                        domClass.add("uiElementN027", "required");
                    } else {
                        domClass.remove("uiElementN027", "required");
                    }
                });
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
        
        dqGriddedDataPositionalAccuracy: {
            title: "Verhalten für die Rasterpositionsgenauigkeit",
            description: "Das Element ist optional und wird nicht per default eingeblendet. Es wird nur aktiviert, wenn \"Digitale Repräsentation\" den Wert \"Raster\" hat.",
            defaultActive: true,
            run: function() {
                aspect.after(registry.byId("ref1Representation"), "onDataChanged", function() {
                    console.log("data: ", this.getData());
                    var hasGridType = array.some(this.getData(), function(row) {
                        // 2 === Raster, Gitter
                        return row.title === 2 || row.title === "2"; 
                    });
                    
                    // show field if grid type was found in table, otherwise hide it
                    if (hasGridType) {
                        domClass.remove("uiElement5071", "hide");
                    } else {
                        domClass.add("uiElement5071", "hide");
                    }
                });
            }
        },
        
        dataFormat: {
            title : "Name von Datenformat verpflichtend",
            description : "Wenn aktiviert, muss jeder Eintrag in der Tabelle \"Datenformat\" mind. einen Namen enthalten.",
            defaultActive : true,
            run : function() {
                topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                        if (array.some(UtilGrid.getTableData("availabilityDataFormat"), function(dataFormat) {
                            return (typeof(dataFormat.name) == "undefined" ||
                            	dataFormat.name === null ||
                                lang.trim(dataFormat.name + "").length === 0);
                        })) {
                            notPublishableIDs.push( ["availabilityDataFormat", message.get("validation.error.data.format")] );
                        }
                    });
            }
        	
        },
        
        foldersInHierarchy: foldersInHierarchy,

        openData: openData
        
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
