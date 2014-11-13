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
    "ingrid/message",
    "ingrid/utils/General"
], function(declare, message, UtilGeneral) {
    return declare(null, {


        compare: function(a, b) {
            a = a.replace("ö", "oe").replace("ä", "ae").replace("ü", "ue").replace("ß", "ss").replace("Ä", "Ae").replace("Ö", "Oe").replace("Ü", "Ue");
            b = b.replace("ö", "oe").replace("ä", "ae").replace("ü", "ue").replace("ß", "ss").replace("Ä", "Ae").replace("Ö", "Oe").replace("Ü", "Ue");
            return a == b ? 0 : (a < b ? -1 : 1);
        },

        compareIgnoreCase: function(a, b) {
            return this.compare(a.toLowerCase(), b.toLowerCase());
        },

        /*
         * This function converts the long date format created and used by dojo
         * to a shorter and readable format often used in form elements
         */
        convertDate: function(dateLong, rowIdx, cell) {
            var result = "";
            if (dateLong !== null && dateLong != "")
                result = this.getDateString(dateLong, "dd.MM.yyyy");
            else
                cell.customClasses.push("importantBackground");
            return result;
        },

        getDateString: function(dateLong, pattern) {
            if (dateLong === undefined) dateLong = new Date();
            return dojo.date.locale.format(dateLong, {
                selector: "date",
                datePattern: pattern
            });
        },

        emptyIfNull: function(value) {
            if (!value || value === null)
                return "";
            else
                return value;
        },

        //check passed string: returns false if undefined or null or length==0 
        hasValue: function(stringValue) {
            return UtilGeneral.hasValue(stringValue);
        },

        // value starting from 0..n
        _getCapabilitiesType: function(value) {
            // null, Suchdienste, Darstellungsdienste, Download, Sonstige, null, Sonstige
            var types = [null, "CSW", "WMS", "WFS", "WCTS", null, "WCS"];
            return types[value];
        },

        addCapabilitiesParameter: function(type, connUrl, paramServiceType) {
            var mappedType = this._getCapabilitiesType(type);
            if (!mappedType) console.error("Not supported Service Type: " + type);

            if (connUrl.toLowerCase().indexOf("request=getcapabilities") == -1) {
                if (connUrl.indexOf("?") == -1) {
                    connUrl = connUrl + "?";
                }
                if (!(connUrl.lastIndexOf("?") == connUrl.length - 1) && !(connUrl.lastIndexOf("&") == connUrl.length - 1)) {
                    connUrl = connUrl + "&";
                }

                connUrl += "REQUEST=GetCapabilities";
            }
            if (connUrl.toLowerCase().indexOf("service=") == -1) {
                if (paramServiceType) {
                    connUrl += "&" + paramServiceType;
                } else {
                    connUrl += "&SERVICE=" + mappedType;
                }
            }
            return connUrl;
        }
    })();
});