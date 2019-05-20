/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dijit/registry",
    "dojo/_base/declare",
    "dojo/dom-class",
    "dojo/topic"
], function(registry, declare, domClass, topic) {

    return declare(null, {
        title: "BAW-Allgemein",
        description: "Allgemeine UI Anpassungen für die BAW",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {

            topic.subscribe("/onObjectClassChange", function(data) {
                // ========================================
                // Zusatzinformation: Zeichensatz des Datensatzes
                // ========================================

                // Make the node mandatory if it has not been hidden by some
                // other rule
                var datasetCharsetNodeId = "uiElement5043";
                if(!domClass.contains(datasetCharsetNodeId, "hide")) {
                    domClass.add(datasetCharsetNodeId, "required");
                }

                // If the dataset is new (not saved yet), then initialise the
                // value as "utf8"
                var datasetCharsetUtf8Value = "4";
                var datasetCharsetWidgetId = "extraInfoCharSetData";

                var newUuid = "newNode";
                var uuid = currentUdk.uuid;

                var datasetCharsetWidget = registry.byId(datasetCharsetWidgetId);
                if (uuid === newUuid
                        && datasetCharsetWidget
                        && !datasetCharsetWidget.get("value")) {
                    datasetCharsetWidget.set("value", datasetCharsetUtf8Value);
                }
            });
        }})();
});

