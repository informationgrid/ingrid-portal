/*
 * Tree Event Handler. Static Methods
 */

var treeEventHandler = {};


// ----- Add the listener -> TODO move to mdek_entry.jsp -----
function nodeSelectListener(message)
{
  if (message.node.id != 'objectRoot') {
    dojo.debug('Publishing event: /loadRequest('+message.node.id+', '+message.node.nodeAppType+')');
    dojo.event.topic.publish("/loadRequest", {id: message.node.id, appType: message.node.nodeAppType});
  }
}

dojo.addOnLoad(function()
{
  var treeListener = dojo.widget.byId('treeListener');
  if (treeListener)
    dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelectListener");
  else
    alert("Error in js/treeEventHandler.js: Widget treeListener could not be found.");
});