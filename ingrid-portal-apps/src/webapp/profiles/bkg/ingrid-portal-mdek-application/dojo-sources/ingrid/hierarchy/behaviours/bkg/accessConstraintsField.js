/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dojo/dom-class",
    "dijit/registry",
    "ingrid/widgets/MultiInputInfoField"
], function(declare, on, domClass, registry, MultiInputInfoField) {

    // issue: 556
    return declare(null, {
        title: "Zugriffsbeschränkungen",
        description: "Fügt ein neues Feld zur Eingabe von BKG spezifischen Zugriffsbeschränkungen hinzu.",
        defaultActive: true,
        category: "BKG",
        run: function() {
            var rubric = "availabilityContent";

            // make old access constraints full width
            domClass.remove("uiElementN025", "halfWidth");
            registry.byId("availabilityAccessConstraints").reinitLastColumn(true);

            var multiInputInfoFieldWidget = new MultiInputInfoField({
                id: "bkg_accessConstraints",
                label: "Zugriffsbeschränkungen",
                codelist: 10001,
                codelistForText: 10002,
                showSourceNote: false
            }).placeAt(rubric, "first");

            domClass.add(multiInputInfoFieldWidget.domNode, "required");

            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            additionalFields.push(multiInputInfoFieldWidget);

            on(registry.byId("isInspireRelevant") , "change", function(isChecked) {
                if (isChecked) {
                    domClass.remove( "bkg_accessConstraints", "required" );
                } else {
                    domClass.add( "bkg_accessConstraints", "required" );
                }
            });
        }
    })();
});
