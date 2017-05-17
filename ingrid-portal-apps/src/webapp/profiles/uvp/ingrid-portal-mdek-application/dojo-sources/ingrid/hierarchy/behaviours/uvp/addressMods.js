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
    "dojo/dom",
    "dojo/dom-class",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message"
], function(declare, dom, domClass, query, topic, registry, message) {

    return declare(null, {
        title: "UVP: Adressenänderungen",
        description: "Hier werden die Verschlagwortung, Aufgabe und Servicezeiten entfernt.",
        defaultActive: true,
        category: "UVP",
        forAddress: true, // execute only on address form initialization
        run: function() {
            // domClass.add("uiElement4571_at0", "hide"); // Veröffentlichung
            domClass.add("adrThesaurus", "hide");  // Verschlagwortung
            domClass.add("uiElement4440", "hide"); // Aufgaben
            domClass.add("uiElement4450", "hide"); // Servicezeiten
            domClass.add("uiElement4571_at0", "hide"); // Veröffentlichung (#683)
            domClass.add("uiElement4435", "hide"); // Administrative Area (#670)

            // since folder behaviour also toggles address owner field we have to listen
            // for the same event. Since we register to a later time, this handler is also called later.
            topic.subscribe("/onAddressClassChange", function(data) {
                // if it's not a folder
                if (data.addressClass !== 1000) {
                    domClass.add(dom.byId("addressOwnerLabel").parentNode, "hide"); // Veröffentlichung (#682)
                }
            });

            query("#address .titleCaption").addContent(message.get("uvp.address.form.categories.address"), "only");

            // set "Deutschland" as default when creating a new address
            topic.subscribe("/createAddressRequest", function() {
                // TODO: try to attach to setData-function to set value at correct time
                setTimeout(function() { registry.byId("addressCountry").set("value", "276"); }, 500);
            });
        }
    })();
});