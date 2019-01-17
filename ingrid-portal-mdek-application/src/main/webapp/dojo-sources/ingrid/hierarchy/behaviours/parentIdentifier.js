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
define(["dijit/registry",
    "dojo/_base/declare",
    "dojo/aspect",
    "dojo/dom-class"
], function (registry, declare, aspect, domClass) {

    // ATTENTION: THIS WILL NOT BE EXECUTED SINCE IT'S NOT ASSIGNED IN "behaviours.js"
    return declare(null, {
        title: "Identifikator des übergeordneten Metadatensatzes",
        description: "Hier wird die Sichtbarkeit des Feldes gesteuert. Dieses soll nur für Root-Objekte und solche unter direkt unter einem Ordner sichtbar sein.",
        defaultActive: true,
        run: function () {
            var igeActions = require("ingrid/IgeActions");
            aspect.after(igeActions, "onAfterLoad", this.handleFieldToggle);
            aspect.after(igeActions, "onAfterCreate", this.handleFieldToggle);
        },

        handleFieldToggle: function() {
            var isRootObject = currentUdk.parentUuid === null;
            var isBelowFolder = registry.byId("dataTree").selectedNode.getParent().item.objectClass === 1000; // check if parent is a folder

            // show field for parent identifier if root object or directly below folder
            if (isRootObject || isBelowFolder) {
                domClass.remove("uiElement1001", "hide");
            } else {
                domClass.add("uiElement1001", "hide");
            }
        }
    })();
});
