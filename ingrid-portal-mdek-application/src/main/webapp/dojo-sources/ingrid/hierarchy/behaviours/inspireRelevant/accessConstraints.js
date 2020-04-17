/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/on",
    "dojo/dom-class",
    "dijit/registry"
], function(array, declare, on, domClass, registry) {

    // issue: 1529
    return declare(null, {
        title: "Zugriffsbeschränkungen",
        description: "Wird nur zum Pflichtfeld, wenn INSPIRE-relevant.",
        defaultActive: true,
        run: function() {
            var self = this;
            // delay execution to execute initial profile setup first
            setTimeout(function() {
                var inspireRelevantWidget = registry.byId("isInspireRelevant");
                domClass.remove("uiElementN025", "optional");
                self.handleInspireChange(inspireRelevantWidget.checked);

                on(inspireRelevantWidget, "Change", function (isChecked) {
                    self.handleInspireChange(isChecked);
                });
            });
        },

        handleInspireChange: function (isChecked) {
            if (isChecked) {
                domClass.add("uiElementN025", "required");
            } else {
                domClass.remove("uiElementN025", "required");
            }
        }
    })();
});
