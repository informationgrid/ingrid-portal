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
var uvp = {};

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
        title: "UVP: 'Negative Vorprüfungen' veröffentlichen",
        description: "Bei Auswahl, werden zusätzliche Formularfelder angezeigt, für die vollständige Erfassung einer negativen Vorprüfung.",
        defaultActive: false,
        category: "UVP",
        run: function() {
            // topic.subscribe("/onObjectClassChange", function(clazz) {
            //     if (clazz === "Class12") {
            //         query("#generalDescLabel label").addContent(message.get("uvp.form.generalDescription"), "only");
            //         domClass.remove("uiElement1010", "hide");
            //         domClass.remove(this.nominatimSearch.domNode, "hide");
            //         domClass.remove(this.prefix + "spatialValue", "hide");
            //         domClass.remove("uiElementAdduvpgCategory", "hide");
            //         domClass.remove("uiElementAdduvpNegativeApprovalDate", "hide");
            //         domClass.remove("uiElementAdduvpNegativeRelevantDocs", "hide");
            //     }
            // });
            uvp.publishNegativeExaminations = true;
        }
    })();
});