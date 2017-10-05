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
    "dojo/on",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dijit/registry",
    "ingrid/widgets/MultiInputInfoField"
], function(declare, on, dom, domClass, construct, registry, MultiInputInfoField) {

    // issue: 556
    return declare(null, {
        title: "Nutzungsbedingungen",
        description: "Fügt ein neues Feld zur Eingabe von BKG spezifischen Nutzungsbedingungen hinzu.",
        defaultActive: true,
        category: "BKG",
        run: function() {
            var rubric = "availabilityContent";

            var targetNode = dom.byId("uiElementN027");

            // make old use constraints full width
            domClass.remove(targetNode, "halfWidth");
            registry.byId("availabilityUseAccessConstraints").reinitLastColumn(true);

            // create div element to insert new field at correct place
            var insertNode = construct.create("div");
            targetNode.parentNode.insertBefore(insertNode, targetNode);

            var multiInputInfoFieldWidget = new MultiInputInfoField({
                id: "bkg_useConstraints",
                label: "Nutzungsbedingungen",
                selectRequired: true,
                codelist: 10003,
                codelistForText: 10004
            }).placeAt(insertNode);

            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            additionalFields.push(multiInputInfoFieldWidget);
        }
    })();
});