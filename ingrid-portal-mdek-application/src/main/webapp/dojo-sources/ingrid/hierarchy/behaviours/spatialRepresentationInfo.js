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
    "dojo/_base/lang",
    "dojo/dom-class",
    "dojo/on",
    "dojo/query",
    "dojo/topic",
    "dijit/MenuItem",
    "dijit/form/Button",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Syslist",
    "ingrid/utils/Tree",
    "dojo/NodeList-traverse"
], function (declare, array, lang, domClass, on, query, topic, MenuItem, Button, registry, message, Syslist, TreeUtils) {

    return declare(null, {
        title: "Struktur der räumlichen Daten",
        description: "Wenn \"Digitale Repräsentation\" = \"Raster\", dann werden zusätzliche Felder zu Erfassung von \"georektifiziert\" bzw. \"georeferenziert\" angezeigt.",
        defaultActive: true,
        gridRepresentation: "2", // Grid
        run: function () {
            var self = this;
            on(registry.byId("ref1Representation"), "DataChanged", function (msg) {
                var hasGrid = array.some(this.data, function (element) {
                    return self.gridRepresentation === (element.title+"");
                });

                if (hasGrid) {
                    domClass.remove("ref1GridFormat", "hide");
                } else {
                    domClass.add("ref1GridFormat", "hide");
                }
                // UtilUI.showToolTip("ref1Representation", message.get("validation.digitalRepresentation.conform"));
            });
        }
    })();
});
