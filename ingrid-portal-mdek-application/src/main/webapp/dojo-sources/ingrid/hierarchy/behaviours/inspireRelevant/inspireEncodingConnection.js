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
    "dojo/string",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist"
], function(declare, on, string, registry, message, UtilGrid, UtilUI, UtilSyslist) {
    
    return declare(null, {
        title: "Inspire / Encoding - Connection",
        description: "Laut der GDI_DE Konventionen, wird eine ISO Kategorie automatisch zu einem dazugehörigen INSPIRE-Kodierungsschema hinzugefügt.",
        defaultActive : true,
        category: "INSPIRE relevant",
        run: function() {
                    
            // mapped INSPIRE-topic IDs to encoding scheme IDs
            var mapping = {103: 18, 104: 16, 105: 15, 106: 17, 107: 7, 108: 2, 109: 14, 201: 19,
                202: 20, 203: 21, 204: 22, 301: 23, 302: 24, 303: 25, 304: 26, 305: 27, 306: 28,
                307: 29, 308: 30, 309: 31, 310: 32, 311: 33, 312: 34, 313: 35, 314: 35, 315: 36, 316: 37,
                317: 38, 318: 39, 319: 40, 320: 41, 321: 42};
            
            // react when inspire topics has been added
            on(UtilGrid.getTable("thesaurusInspire"), "CellChange", function(msg) {
                var objClass = registry.byId("objectClass").get("value");
                // only react if class == 1
                if (objClass === "Class1") {
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", mapping[msg.item.title]);
                    var name = UtilSyslist.getSyslistEntryName(6300, mapping[msg.item.title]);
                    UtilUI.showToolTip( "thesaurusInspire", string.substitute(message.get("validation.encoding.added"), [name]) );
                }
            });
        }
    })();
});
