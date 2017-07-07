/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/on",
    "dojo/string",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist"
], function(declare, array, on, string, registry, message, UtilGrid, UtilUI, UtilSyslist) {
    
    return declare(null, {
        title: "Inspire / Conformity - Connection",
        description: "Laut der GDI_DE Konventionen, wird eine ISO Kategorie automatisch zu einem dazugehörigen INSPIRE-Konformität hinzugefügt.",
        defaultActive: true,
        category: "INSPIRE relevant",
        run: function() {
                    
            // mapped INSPIRE-topic IDs to conformity IDs
            var mapping = {103: 4, 104: 2, 105: 1, 106: 3, 107: 7, 108: 5, 109: 6, 201: 14,
                202: 15, 203: 16, 204: 17, 301: 18, 302: 19, 303: 20, 304: 21, 305: 22, 306: 23,
                307: 24, 308: 25, 309: 26, 310: 27, 311: 28, 312: 29, 313: 30, 314: 30, 315: 31,
                316: 32, 317: 33, 318: 34, 319: 35, 320: 36, 321: 37};

            var applySpecification = function(inspireId, deleteEntry) {
                UtilUI.updateEntryToConformityTable(mapping[inspireId], deleteEntry);
            };

            var isInspireRelevantWidget = registry.byId("isInspireRelevant");
            var isConformWidget = registry.byId("isInspireConform");

            // react when inspire topics has been added
            on(UtilGrid.getTable("thesaurusInspire"), "CellChange", function(msg) {
                var objClass = registry.byId("objectClass").get("value");
                // only react if class == 1
                if (objClass == "Class1" && isInspireRelevantWidget.get("checked") && isConformWidget.get("checked")) {
                    // remove old dependent values
                    if (msg.oldItem) {
                        applySpecification(msg.oldItem.title, true);
                    }
                    // add new dependent value
                    applySpecification(msg.item.title, false);
                    var name = UtilSyslist.getSyslistEntryName(6005, mapping[msg.item.title]);
                    UtilUI.showToolTip( "thesaurusInspire", string.substitute(message.get("validation.specification.added"), [name]) );
                }
            });

            // remove specific entry from conformity table when inspire topic was deleted
            on(UtilGrid.getTable("thesaurusInspire"), "DeleteItems", function(msg) {
                var objClass = registry.byId("objectClass").get("value");
                // only react if class == 1
                if (objClass == "Class1" && isInspireRelevantWidget.get("checked") && isConformWidget.get("checked")) {
                    var names = [];
                    array.forEach(msg.items, function(item) {
                        applySpecification(item.title, true);
                        names.push( UtilSyslist.getSyslistEntryName(6005, mapping[item.title]) );
                    });
                    var formattedNames = "<li>" + names.join("</li><li>") + "</li>";
                    UtilUI.showToolTip( "thesaurusInspire", string.substitute(message.get("validation.specification.delete.depend"), [formattedNames]) );
                }
            });
        }
    })();
});
