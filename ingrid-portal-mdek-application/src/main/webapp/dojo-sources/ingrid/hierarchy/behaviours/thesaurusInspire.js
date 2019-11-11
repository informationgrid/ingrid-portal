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
], function(declare, aspect, on, domClass, topic, registry) {
    return declare(null, {

        title : "INSPIRE-Themen",
        description : "Die INSPIRE-Themen sind verpflichtend für Geodatensätze, die INSPIRE-relevant sind.",
        category: "Felder",
        defaultActive : true,
        run : function() {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");

            applyRule7();

            aspect.after(registry.byId("thesaurusInspire"), "onDataChanged", function() {
                var objClass = registry.byId("objectClass").get("value");
                if (objClass === 'Class1') {
                    // Show/hide DQ tables in class 1 depending on themes
                    applyRule7();
                }
            });

            topic.subscribe("/onObjectClassChange",  function(msg) {
                if (msg.objClass === "Class1") {
                    // initial set needed, since inspireRelevantWidget might not have change event
                    // when coming from another class where this widget is also available
                    self.handleInspireRelevant(inspireRelevantWidget.checked);

                    on(inspireRelevantWidget, "Change", function(isChecked) {
                        self.handleInspireRelevant(isChecked);
                    });
                } else {
                    self.resetField();
                }
            });
        },
        handleInspireRelevant: function(isChecked) {
            if (isChecked) {
                // Make inspire themes required
                domClass.add("uiElement5064", "required");
                domClass.remove("uiElement5064", "optional");
                registry.byId("thesaurusInspire").reinitLastColumn();
            } else {
                this.resetField();
            }
        },
        resetField: function() {
            // Make inspire themes optional
            domClass.remove("uiElement5064", "required");
            domClass.add("uiElement5064", "optional");
        }
    })();
});
