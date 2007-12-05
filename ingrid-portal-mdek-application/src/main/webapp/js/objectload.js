dojo.addOnLoad(function()
{
  var treeListener = dojo.widget.byId('treeListener');
  if (treeListener)
    dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelectListener");
  else
    alert("Error in js/objectload.js: Widget treeListener could not be found.");
});

function nodeSelectListener(message)
{
  EntryService.getNodeData(message.node.id, message.node.nodeAppType, 'false',
    {
      callback:populateFields,	// Callback Funktion die aufgerufen wird sobald Daten verfuegbar sind
      timeout:5000,
      errorHandler:function(message) {alert("Error in js/objectload.js: Timeout while waiting for nodeData: " + message); }
    }
  );
}

// Diese callback Funktion fuellt die Felder des MDEK mit Daten
function populateFields(nodeData)
{
  dojo.debug("Node Data received (from callback): [ID="+nodeData.id +", Type="+nodeData.nodeAppType+", title="+nodeData.title+"]");

  var myObj = {objectName: nodeData.title};
  dojo.widget.byId('headerFormObject').setValues(myObj);

//  dojo.widget.byId('contentFormObject').setValues(myObj);
//  dojo.widget.byId('headerFormAddress').setValues(myObj);
//  dojo.widget.byId('contentFormAddress').setValues(myObj);
}
