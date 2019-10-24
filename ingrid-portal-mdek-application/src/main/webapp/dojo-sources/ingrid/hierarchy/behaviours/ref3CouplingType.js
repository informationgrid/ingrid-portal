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
    "dijit/registry"
], function (declare, aspect, registry) {
    return declare(null, {

        title: "Kopplungstyp",
        description: "Wenn in den Dargestellten Daten ein Eintrag hinzugefügt wird, wird der Kopplungstyp auf 'tight' gesetzt, ansonsten auf 'loose'.",
        category: "Felder",
        defaultActive: true,
        run: function () {

            aspect.after(registry.byId("ref3BaseDataLink"), "onDataChanged", function() {
                var couplingType = registry.byId("ref3CouplingType");
                var isMixed = couplingType.value === "mixed";
                if (isMixed !== true) {
                    if (this.data.length > 0) {
                        couplingType.set("value", "tight");
                    } else {
                        couplingType.set("value", "loose");
                    }
                }
            });

        }
    })();
});
