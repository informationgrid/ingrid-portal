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
    "dojo/on",
    "dijit/registry",
    "dojo/topic"
], function (declare, on, registry, topic) {
    return declare(null, {

        title: "Durch die Ressource abgedeckte Zeitspanne",
        description: "Prüft die von-bis Datumswerte",
        category: "Felder",
        defaultActive: true,
        run: function () {
            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                timeRefDatePublishable(notPublishableIDs);
            });

            on(registry.byId("timeRefType"), "Change", function (value) {
                applyRule4(value);
            });
            applyRule4("");
            on(registry.byId("timeRefSubType"), "Change", function (value) {
                applyRule3(value);
            });
            applyRule3("");
        }
    })();
});
