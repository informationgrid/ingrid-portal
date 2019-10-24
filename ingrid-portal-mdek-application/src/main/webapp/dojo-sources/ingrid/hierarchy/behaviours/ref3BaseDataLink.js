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
    "dojo/on",
    "dijit/registry",
    "ingrid/utils/UI"
], function (declare, on, registry, UtilUI) {
    return declare(null, {

        title: "Dargestellte Daten",
        description: "Wenn der Kopplungstyp auf 'tight' steht, wird dieses Feld zum Pflichtfeld.",
        category: "Felder",
        defaultActive: true,
        run: function () {

            // make 'Basisdaten' mandatory on input 'tight'
            on(registry.byId("ref3CouplingType"), "Change", function(value) {
                if (value === "tight") {
                    UtilUI.setMandatory("uiElement3345");
                } else {
                    UtilUI.setOptional("uiElement3345");
                }
            });
        }
    })();
});
