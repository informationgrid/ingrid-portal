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
/*
 * InGrid detail view helper. Opened with static methods
 */
define(["dojo/_base/declare", "ingrid/message"],
    function(declare, message) {

        return declare(null, {

            renderAddressEntry: function(address) {
                var entry = "";
                var block = "";
                if (this.isValid(address.organisation)) {
                    entry += address.organisation + "\n";
                }
                if (this.isValid(address.nameForm)) {
                    block += address.nameForm + " ";
                }
                if (this.isValid(address.titleOrFunction)) {
                    block += address.titleOrFunction + " ";
                }
                if (this.isValid(address.givenName)) {
                    block += address.givenName + " ";
                }
                if (this.isValid(address.name)) {
                    block += address.name;
                }
                if (block.length > 0) {
                    entry += block + "\n";
                }
                if (this.isValid(address.street)) {
                    entry += address.street + "\n";
                }
                if (this.isValid(address.city)) {
                    if (this.isValid(address.postalCode)) {
                        entry += address.postalCode + " ";
                    }
                    entry += address.city + "\n";
                }
                if (this.isValid(address.countryName)) {
                    entry += address.countryName + "\n";
                }

                if (this.isValid(address.pobox)) {
                    entry += "\n Postfach " + address.pobox + "\n";
                    if (this.isValid(address.poboxPostalCode)) {
                        entry += address.poboxPostalCode + " ";
                    }
                    if (this.isValid(address.city)) {
                        entry += address.city + "\n";
                    }
                    if (this.isValid(address.countryName)) {
                        entry += address.countryName + "\n";
                    }
                }

                // communications
                if (address.communication && address.communication.length) {
                    entry += "\n";
                    var c, i;
                    for (i = 0; i < address.communication.length; i++) {
                        c = address.communication[i];
                        entry += c.medium + ": " + c.value + "\n";
                    }
                }

                if (this.isValid(address.task)) {
                    entry += "\n<strong>" + message.get("ui.adr.details.tasks") + "</strong>";
                    entry += "\n" + address.task + "\n";
                }

                if (this.isValid(address.addressDescription)) {
                    entry += "\n<strong>" + message.get("ui.adr.details.notes") + "</strong>";
                    entry += "\n" + address.addressDescription;
                }

                return entry;
            },

            isValid: function(val) {
                if (dojo.isString(val) && val.length > 0) {
                    return true;
                } else {
                    return false;
                }
            }

        })();
    });