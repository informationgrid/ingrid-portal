/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
define([
    "dojo/_base/declare",
    "dojo/on",
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry",
    "ingrid/utils/Grid"
], function(declare, on, domClass, topic, registry, UtilGrid) {
    return declare(null, {
        
        title : "Verwaltungsgebiet verpflichtend",
        description : "Wenn aktiviert, dann muss jeder Ansprechpartner in einem Datensatz ein Verwaltungsgebiet eingetragen haben, bevor dieser veröffentlicht werden kann. Wenn kein Ansprechpartner eingetragen ist, so wird ebenfalls ein Fehler angezeigt.",
        defaultActive : true,
        category: "AdV Kompatibel",
        description: "",
        run : function() {
            // check if administrative area is set, when checkbox is activated
            var subscription = null;
            
            var doSubscribe = function() {
                subscription = topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                    var pointOfContacts = UtilGrid.getTableData("generalAddress").filter(function(row) { return row.nameOfRelation === "Ansprechpartner" || row.nameOfRelation === "Point of Contact" });
                    
                    if (pointOfContacts.length === 0) {
                        notPublishableIDs.push( ["generalAddress", "Es muss mindestens ein 'Ansprechpartner' eingetragen sein."] );
                    }
                    
                    pointOfContacts.forEach(function(contact) {
                        if (!contact.administrativeArea || contact.administrativeArea.trim().length === 0) {
                            notPublishableIDs.push( ["generalAddress", "Bitte ergänzen Sie das Verwaltungsgebiet in mind. einer Adresse der Rolle 'Ansprechpartner'."] );
                        }
                    });
                });
            };
            
            on(registry.byId("isAdvCompatible"), "Change", function(isChecked) {
                if (isChecked) {
                    doSubscribe();
                } else {
                    if (subscription) {
                        subscription.remove();
                        subscription = null;
                    }
                }
            });
            
            
            // TODO: AdV-Produktgruppe wird bei Aktivierung der Checkbox
            // "AdV kompatibel" verpflichtend.
            
            // TODO: Bei De-Aktivierung der Checkbox "AdV kompatibel"
            // sollten die Einträge der Produktgruppe wieder entfernt
            // werden
        }
        
    })();
});
