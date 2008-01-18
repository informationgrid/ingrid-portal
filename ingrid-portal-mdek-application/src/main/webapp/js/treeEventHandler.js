/*
 * Tree Event Handler. Static Methods
 */

var treeEventHandler = {};


// ----- Add the listener -> TODO move to mdek_entry.jsp -----
function nodeSelectListener(message)
{
	var nodeId = message.node.id;
	var nodeAppType = message.node.nodeAppType;
	if (nodeId != 'objectRoot') {
		var deferred = new dojo.Deferred();
		dojo.debug("Publishing event: /loadRequest("+nodeId+", "+nodeAppType+")");
		dojo.event.topic.publish("/loadRequest", {id: nodeId, appType: nodeAppType, resultHandler: deferred});
		deferred.addErrback(function(mes) {
			dojo.debug(mes);
			dialog.show(message.get("general.error"), message.get("tree.nodeLoadError"), dialog.WARNING);
		});
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