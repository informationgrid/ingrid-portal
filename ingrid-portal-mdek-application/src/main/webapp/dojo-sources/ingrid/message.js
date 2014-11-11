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
 * InGrid dialogs. Localized with static methods
 */
define([
    "dojo/_base/declare",
    "dojo/i18n!ingrid/nls/messages",
    "dojo/i18n!ingrid/nls/profile_messages"
], function(declare, i18n, i18nProfile){
    return declare(null, {
        /*
         * Get a message in the current locale
         */
        get: function(messageId) {
            var msg;
            //console.log(i18n[messageId]);
        
            // try first the profile
            //var messages = i18n.getLocalization("ingrid", "profile_messages", locale);
            msg = i18nProfile[messageId];
            if (!msg) {
                msg = i18n[messageId];
            }
        
            // If the localized text could not be found return the key
            return (typeof (msg) == "undefined") ? messageId : msg;
        }

    })();
});
