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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class"
], function(declare, array, lang, dom, domClass) {
    return declare(null, {
        title: "Formularfelder für Adressen",
        description: "Hier werden die zusätzlichen Felder im Adress-Formular erzeugt sowie überflüssige ausgeblendet.",
        defaultActive: true,
        category: "mcloud",
        prefix: "mcloud_",
        forAddress: true, // execute only on address form initialization
        run: function() {

            this.hideDefaultFieldsFromAddresses();

        },

        hideDefaultFieldsFromAddresses: function() {
            // hide publication in addresses
            domClass.add("uiElement4571_at0", "hide");
            domClass.add("uiElement4571_at1", "hide");
            domClass.add("uiElement4571_at2", "hide");
            domClass.add("uiElement4571_at3", "hide");

            // hide rubric Thesaurus
            domClass.add("adrThesaurus", "hide");

            // hide rubric Associated Objects
            domClass.add("associatedObj", "hide");

            // hide Tasks
            domClass.add("uiElement4440", "hide");

            // hide Service time
            domClass.add("uiElement4450", "hide");

            // set address rubric as expanded initially (#2039)
            domClass.add("address", "expanded");

        }

    })();
});
