/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/UI"
], function(declare, on, domClass, topic, registry, message, UtilUI) {
    return declare(null, {
        
        title : "Produktgruppe",
        description : "Wenn aktiviert, wird die Liste für die AdV-Produktgruppe angezeigt und verwendet. Des Weiteren wird sie verpflichtend sobald \"AdV kompatibel\" ausgewählt wurde.",
        defaultActive : true,
        category: "AdV Kompatibel",
        run : function() {
            // show table for the product group
            domClass.remove("uiElement5170", "hidden");
            var advProductGroup = registry.byId("advProductGroup");
            advProductGroup.reinitLastColumn();

            on(registry.byId("isAdvCompatible"), "change", function(checked) {
                if (checked) {
                    domClass.add("uiElement5170", "required");
                    registry.byId("advProductGroup").reinitLastColumn();
                } else {
                    domClass.remove("uiElement5170", "required");
                }
            } );

            on(registry.byId("isAdvCompatible"), "click", function() {
                if (!this.checked) {
                    advProductGroup.setData([]);
                    UtilUI.showToolTip("isAdvCompatible", message.get('hint.advProductGroupCleared'));
                }
            });
        }
    })();
});
