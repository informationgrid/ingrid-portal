/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry"
], function(declare, domClass, topic, registry) {

    return declare(null, {
        title: "UVP: Adressenänderungen",
        description: "Hier werden die Verschlagwortung, Aufgabe und Servicezeiten entfernt.",
        defaultActive: true,
        forAddress: true,
        run: function() {
            domClass.add("adrThesaurus", "hide");  // Verschlagwortung
            domClass.add("uiElement4440", "hide"); // Aufgaben
            domClass.add("uiElement4450", "hide"); // Servicezeiten

            // set "Deutschland" as default when creating a new address
            topic.subscribe("/createAddressRequest", function() {
                // TODO: try to attach to setData-function to set value at correct time
                setTimeout(function() { registry.byId("addressCountry").set("value", "276"); }, 500);
            });
        }
    })();
});