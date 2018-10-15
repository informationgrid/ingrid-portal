/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
define([
    "dijit/registry",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/string",
    "dojo/topic",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist",
    "ingrid/utils/UI"
], function(registry, declare, lang, aspect, string, topic, message, UtilGrid, UtilSyslist, UtilUI) {

    return declare(null, {
        title: "http:// oder https:// URLs für Adressen > Kommunikation",
        description: "Stellt sicher, dass unter Adressen die Kommunikation URLs entweder mit http:// oder https:// starten.",
        defaultActive: true,
        forAddress: true,
        run: function() {
            var isUrlValid = function(url) {
                return url && (url.indexOf('http://') === 0 || url.indexOf('https://') === 0);
            };

            topic.subscribe("/onBeforeAddressPublish", function( /*Array*/ notPublishableIDs) {
                // Check if all entries in the address table are valid
                var addressData = UtilGrid.getTableData("addressCom");

                // Get the string (from the syslist) that is used to identify email entries
                var urlString = UtilSyslist.getSyslistEntryName(4430, 4);
                /*
                // Check if at least one entry exists with type email
                if (!dojo.every(addressData, function(adr) {
                    return lang.trim(adr.medium + '') != urlString
                            || isUrlValid(adr.value);
                })) {
                    console.debug('URLs must start with http:// or https://');
                    var errorTxt = string.substitute( message.get("validation.error.email.table"), [urlString] );
                    registry.byId("addressCom").invalidMessage = errorTxt;
                    notPublishableIDs.push( ["addressCom", errorTxt] );
                }
                */
                var msgs = [];
                addressData.forEach(function(addr) {
                    if (lang.trim(addr.medium + '') === urlString && !isUrlValid(addr.value)) {
                        var errorTxt = string.substitute( message.get("validation.error.url"), [addr.value] );
                        msgs.push(errorTxt);
                    }
                });
                if (msgs.length > 0) {
                    console.debug('URLs must start with http:// or https://');
                    var errorTxt = msgs.join('<br />');
                    errorTxt += '<br />' + message.get("validation.error.url.table");
                    registry.byId("addressCom").invalidMessage = errorTxt;
                    notPublishableIDs.push( ["addressCom", errorTxt] );
                }
            });
            aspect.after(registry.byId("addressCom"), "onCellChange", function(result, args) {
                //console.log(args);
                // Check if all entries in the address table are valid
                var addressData = UtilGrid.getTableData("addressCom");

                // Get the string (from the syslist) that is used to identify email entries
                var urlString = UtilSyslist.getSyslistEntryName(4430, 4);

                var msg = args[0];
                var item = msg.item;
                if (item && item.medium === urlString && !isUrlValid(item.value)) {
                    UtilUI.markCells("ERROR", "addressCom", msg.row, [1]);
                    UtilUI.showToolTip("addressCom", message.get("validation.error.url.table"));
                } else {
                    UtilUI.markCells("VALID", "addressCom", msg.row, [1]);
                }
            });
        }

    })();
});
