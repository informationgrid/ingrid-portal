/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/cookie",
    "dojo/Deferred",
    "dojo/on",
    "ingrid/dialog",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist"
], function(array, declare, cookie, Deferred, on, dialog, message, UtilGrid, UtilSyslist) {

    return declare(null, {

        COOKIE_HIDE_INSPIRE_CONFORMITY_HINT: "ingrid.inspire.conformity.hint",

        inspireConformityHint: message.get("hint.inspireConformity"),

        inspireConformityHintDeleted: message.get("hint.inspireConformity.deleted"),

        addConformity: function(isFromInspireList, name, level, explanation) {
            console.log("Add conformity");
            var listId = 6005;
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");
            var publicationDate = UtilSyslist.getSyslistEntryData(listId, name);

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            conformityData.push({
                isInspire: true,
                specification: name,
                level: level,
                explanation: explanation,
                publicationDate: publicationDate && publicationDate.length > 0 ? new Date(publicationDate) : null
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeConformity: function(name) {
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeEvents: function(events) {
            array.forEach(events, function(event) {
                if (event !== null) {
                    event.remove();
                }
            });
            // empty array
            events.splice(null);
        },

        showConfirmDialog: function(dialogMessage, cookieId) {
            var def = new Deferred();
            if (cookie(cookieId) !== "true") {
                var h = dialog.show(message.get("dialog.general.info"), dialogMessage, dialog.INFO,
                    [
                        {
                            caption: message.get("general.ok.hide.next.time"), type: "checkbox",
                            action: function (newValue) {
                                cookie(cookieId, newValue, {expires: 730});
                            }
                        },
                        {
                            caption: message.get("general.cancel"),
                            action: function () {
                                def.reject();
                            }
                        },
                        {
                            caption: message.get("general.ok"),
                            action: function () {
                                def.resolve();
                            }
                        }
                    ]);
                on(h, "Hide", function() {
                     def.reject();
                });
            } else {
                def.resolve();
            }
            return def.promise;
        }

    })();
});
