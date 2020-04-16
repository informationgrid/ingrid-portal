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
    "dijit/registry",
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/message",
    "ingrid/utils/Syslist",
    "ingrid/utils/UDK"
], function(registry, declare, topic, message, Syslist, UtilUdk) {

    return declare(null, {
        title: "Plattform-Verweis-Typ", // TODO localisation
        description: "Verweistyp 'Plattform' im 'Verweis anlege' Dialog hinzufügen",
        defaultActive: true,
        category: "Plattform/Sensor",
        type: "SYSTEM",

        run: function() {
            this._addObjectClassForPlatform();
        },

        _addObjectClassForPlatform: function() {
            var relationType = "8001";
            var relationTypeName = message.get("dialog.link.type.platform");
            var relationTypeData = "1";

            // RelationTypeName in linksToTable is set from codelist 2000. So modify the codelist.
            topic.subscribe("/additionalSyslistsLoaded", function() {
                var linkTypes = sysLists[2000];
                if (linkTypes) {
                    linkTypes.push([relationTypeName, relationType, "N", relationTypeData]);
                } else {
                    alert("Syslist not found: 2000");
                }
            });

            // The combobox is populated with the codelist data stored in the database
            // Mirror the changes in JS made to the codelist above after the combobox has been initialised
            topic.subscribe("/afterInitDialog/LinksDialog", function() {
                if (UtilUdk.getCurrentObjectClass() === "1") {
                    var linkTypeCombobox = registry.byId("linksFromFieldName");
                    var store = linkTypeCombobox.get("store");
                    store.data.push({
                        name: relationTypeName,
                        entryId: relationType,
                        isDefault: false,
                        data: relationTypeData
                    });
                    linkTypeCombobox.set("store", store);
                }
            });
        }

    })();
});

