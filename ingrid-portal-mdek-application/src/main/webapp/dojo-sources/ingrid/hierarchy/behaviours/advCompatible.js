/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
    // "dojo/_base/lang",
    "dojo/on",
    "dojo/dom-class",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "ingrid/utils/Grid"
], function(declare, on, domClass, query, topic, registry, UtilGrid) {
    return declare(null, {

        title : "Checkbox anzeigen",
        description : "Wenn aktiviert, wird die Checkbox \"AdV kompatibel\" angezeigt.",
        defaultActive : true,
        category: "AdV Kompatibel",
        run : function() {
            // show checkbox AdV compatible
            topic.subscribe("/onObjectClassChange", function(data) {
                // Geodatendienst, Geodatensatz und Informationssysteme
                if (data.objClass === "Class1" || data.objClass === "Class3" || data.objClass === "Class6") {
                    domClass.remove("uiElement6005", "hidden");
                    // show different help text for conformity table
                    var conformityLabel = query("#extraInfoConformityTableLabel label")[0];
                    on(registry.byId("isAdvCompatible"), "Change", function(isChecked) {
                        if (isChecked) {
                            conformityLabel.onclick = function() {
                                require('ingrid/dialog').showContextHelp(arguments[0], 10034);
                            };
                        } else {
                            conformityLabel.onclick = function() {
                                require('ingrid/dialog').showContextHelp(arguments[0], 10024);
                            };
                        }
                    });
                } else {
                    domClass.add("uiElement6005", "hidden");
                }
            });
        }
    })();
});
