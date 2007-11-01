/*
 * InGrid dialogs. Localized with static methods
 */
dojo.requireLocalization("ingrid", "messages");

var message={};

/*
 * Get a message in the current locale
 */
message.get = function(messageId)
{
  var messages = dojo.i18n.getLocalization("ingrid", "messages", locale);
  return messages[messageId];
}
