/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
        "ingrid/utils/Syslist",
        "ingrid/hierarchy/behaviours/opendata",
        "ingrid/hierarchy/behaviours/folders",
        "ingrid/hierarchy/behaviours/inspireRelevant/conformFields",
        "ingrid/hierarchy/behaviours/inspireRelevant/geoservice",
        "ingrid/hierarchy/behaviours/inspireRelevant/inspireIsoConnection",
        "ingrid/hierarchy/behaviours/inspireRelevant/inspireEncodingConnection",
        "ingrid/hierarchy/behaviours/inspireRelevant/inspireConformityConnection",
        "ingrid/hierarchy/behaviours/advCompatible",
        "ingrid/hierarchy/behaviours/administrativeArea",
        "ingrid/hierarchy/behaviours/advProductGroup",
        "ingrid/hierarchy/behaviours/spatialRepresentationInfo",
        "ingrid/hierarchy/behaviours/parentIdentifier"
], function(declare, array, Deferred, lang, style, topic, query, string, on, aspect, dom, domClass, registry, cookie, message, dialog, UtilGrid, UtilUI, UtilList, UtilSyslist,
            openData, foldersInHierarchy, conformityFields, inspireGeoservice, inspireIsoConnection, inspireEncodingConnection, inspireConformityConnection, advCompatible, adminitrativeArea, advProductGroup,
            spatialRepresentationInfo, parentIdentifier) {

    return declare(null, {
        
        advCompatible : advCompatible,

        advProductGroup : advProductGroup,
        
        administrativeArea: adminitrativeArea,
        
        conformityFields: conformityFields,

        inspireGeoservice: inspireGeoservice,
        
        inspireIsoConnection: inspireIsoConnection,

        inspireEncodingConnection: inspireEncodingConnection,

        inspireConformityConnection: inspireConformityConnection,

        spatialRepresentationInfo: spatialRepresentationInfo,

        parentIdentifier: parentIdentifier,
        
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
                var subscription = topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
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
