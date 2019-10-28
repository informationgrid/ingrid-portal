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
    "dojo/topic",
    "dojo/on",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/UI"
], function (declare, topic, on, registry, message, UtilUI) {
    return declare(null, {

        title: "Datum (Anwendungsschema)",
        description: "Wenn im Anwendungsschema ein Eintrag vorgenommen wurde, wird dieses Feld zum Pflichtfeld.",
        category: "Felder",
        defaultActive: true,
        run: function () {
            on(registry.byId("availabilityDataFormatInspire"), "Change", function (value) {
                if (value.trim().length > 0) {
                    UtilUI.setShow("uiElement1314");
                    UtilUI.setShow("uiElement1315");
                    UtilUI.setMandatory("uiElement1316");
                } else {
                    UtilUI.setOptional("uiElement1314");
                    UtilUI.setOptional("uiElement1315");
                    UtilUI.setOptional("uiElement1316");
                }
            });

            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                var title = registry.byId("availabilityDataFormatInspire").get("value");
                var date = registry.byId("availabilityDataFormatInspireDate").get("value");

                // if a date is set but no title then show an error
                if (date && (!title || title.trim().length === 0)) {
                    notPublishableIDs.push(["availabilityDataFormatInspireDate", message.get("validation.error.date.without.title")]);
                }
            });
        }
    })();
});
