/*
 * InGrid dialogs. Localized with static methods
 */
//dojo.requireLocalization("ingrid", "messages");
//dojo.requireLocalization("ingrid", "profile_messages");

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

