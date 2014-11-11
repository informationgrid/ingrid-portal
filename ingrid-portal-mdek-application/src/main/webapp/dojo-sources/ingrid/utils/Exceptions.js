/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
    "dojo/_base/lang",
    "dojo/string",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/Address"
], function(declare, lang, string, message, dialog, UtilAddress) {
    return declare(null, {
        handleEntityReferencedException: function(err) {
            var addressTitle = UtilAddress.createAddressTitle(err.targetAddress);
            var objectTitles = "<br><br>";

            for (var i = 0; i < err.sourceObjects.length; ++i) {
                objectTitles += "- " + err.sourceObjects[i].title + "<br>";
            }
            objectTitles = lang.trim(objectTitles);

            dialog.show(message.get("general.error"), string.substitute(message.get("operation.hint.addressReferenceHint"), [addressTitle, objectTitles]), dialog.WARNING, null, 320, 300);
        },

        handleAddressNeverPublishedException: function(err) {
            var addresses = [];

            for (var i = 0; i < err.notPublishedAddresses.length; ++i) {
                var address = err.notPublishedAddresses[i];
                addresses.push(address.organisation ? address.organisation : address.name + ", " + address.givenName);
            }
            dialog.show(message.get("general.error"), string.substitute(message.get("operation.hint.addressNotPublishedHint"), [addresses.join(",")]), dialog.WARNING, null, 320, 300);
        }
    });
});