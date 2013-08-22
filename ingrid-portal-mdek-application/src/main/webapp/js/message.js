/*
 * InGrid dialogs. Localized with static methods
 */
dojo.requireLocalization("ingrid", "messages");
dojo.requireLocalization("ingrid", "profile_messages");

var message = {};

/*
 * Get a message in the current locale
 */
message.get = function(messageId) {

    // try first the profile
    var messages = dojo.i18n.getLocalization("ingrid", "profile_messages", locale);
    var msg = messages[messageId];
    if (!msg) {
        messages = dojo.i18n.getLocalization("ingrid", "messages", locale);
        msg = messages[messageId];
    }

    // If the localized text could not be found return the key
    return (typeof (msg) == "undefined") ? messageId : msg;
}
