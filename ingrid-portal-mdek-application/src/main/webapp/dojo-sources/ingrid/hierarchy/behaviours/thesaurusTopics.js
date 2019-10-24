/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
define([
    "dojo/_base/declare",
    "dojo/aspect",
    "dojo/on",
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry"
], function (declare, aspect, on, domClass, topic, registry) {
    return declare(null, {

        title: "ISO-Themenkategorie",
        description: "Für Geodatensätze wird dieses Feld zum Pflichtfeld ansonsten ist es versteckt.",
        category: "Felder",
        defaultActive: true,
        run: function () {

            topic.subscribe("/onObjectClassChange", function (msg) {
                if (msg.objClass === "Class1") {
                    domClass.add("uiElement5060", "required");
                    domClass.remove("uiElement5060", "hide");
                } else {
                    domClass.remove("uiElement5060", "required");
                    domClass.add("uiElement5060", "hide");
                }
            });
        }
    })();
});
