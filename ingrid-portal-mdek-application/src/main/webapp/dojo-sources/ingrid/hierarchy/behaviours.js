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
        "ingrid/utils/Syslist",
        "ingrid/hierarchy/behaviours/isInspireRelevant",
        "ingrid/hierarchy/behaviours/advCompatible"
], function(declare, array, Deferred, lang, style, topic, query, string, on, aspect, dom, domClass, registry, cookie, message, dialog, UtilGrid, UtilUI, UtilList, UtilSyslist, isInspireRelevant, advCompatible) {

    return declare(null, {
        
        advCompatible : advCompatible,
        
        inspireRelevant: isInspireRelevant,
        
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
