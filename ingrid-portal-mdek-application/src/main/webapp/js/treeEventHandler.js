/*
 * Tree Event Handler. Static Methods
 */

var treeEventHandler = {};

treeEventHandler.handleNodeSelected = function(nodeData)
{
//  dojo.debug("Node Data received (from callback): [ID="+nodeData.id +", Type="+nodeData.nodeAppType+", title="+nodeData.title+"]");
  var headerObjectData = {objectName: 'test Name'};

  dojo.widget.byId('objectClass').setValue(nodeData.nodeDocType);
//  dojo.widget.byId('objectClass').setValue('Class'+ 1);
  dojo.widget.byId('objectOwner').setValue('1');
  dojo.widget.byId('headerFormObject').setValues(headerObjectData);
//  dojo.widget.byId('last_editor').setValue('test last editor');

  var contentObjectData = {generalDesc: 'Description Text...',
                           ref1Coverage: '75'
                          };

  dojo.widget.byId('generalShortDesc').setValue('general short description');
  dojo.widget.byId('contentFormObject').setValues(contentObjectData);

  dojo.widget.byId('generalAddress').store.clearData();
  dojo.widget.byId('generalAddress').store.addData({information:'info one', icon:'x', names:'some test name'});
  dojo.widget.byId('generalAddress').store.addData({information:'info two', icon:'y', names:'another test name'});

  dojo.widget.byId('ref1DataSet').setValue('2');

  dojo.widget.byId('spatialRefAdminUnit').store.clearData();
  dojo.widget.byId('spatialRefAdminUnit').store.addData({information:'info one', latitude1:'3.14', longitude1:'0.1123', latitude2:'1.234', longitude2:'2.345'});
  dojo.widget.byId('spatialRefAdminUnit').store.addData({information:'info two', latitude1:'1.14', longitude1:'2.1123', latitude2:'4.434', longitude2:'1.2'});

  dojo.widget.byId('ref1Representation').store.clearData();
  dojo.widget.byId('ref1Representation').store.addData({representation:'Rasterdaten'});
  dojo.widget.byId('ref1Representation').store.addData({representation:'Another Representation'});

  dojo.widget.byId('extraInfoLangMetaData').setValue('Englisch');
  dojo.widget.byId('extraInfoLangData').setValue('Englisch');

  dojo.widget.byId('thesaurusTerms').store.clearData();
  dojo.widget.byId('thesaurusTerms').store.addData({term:'Topographie'});
  dojo.widget.byId('thesaurusTerms').store.addData({term:'Kartographie'});
  dojo.widget.byId('thesaurusTerms').store.addData({term:'Wasser'});

  dojo.widget.byId('thesaurusTopics').store.clearData();
  dojo.widget.byId('thesaurusTopics').store.addData({topics:'Kategorie eins'});
  dojo.widget.byId('thesaurusTopics').store.addData({topics:'Kategorie zwei'});
  dojo.widget.byId('thesaurusTopics').store.addData({topics:'Kategorie drei'});

  dojo.widget.byId('thesaurusFreeTermsList').store.clearData();
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({freeTerms:'Freier Term eins'});
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({freeTerms:'Freier Term zwei'});
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({freeTerms:'Freier Term drei'});

//  dojo.widget.byId('contentFormObject').setValues(myObj);
//  dojo.widget.byId('headerFormAddress').setValues(myObj);
//  dojo.widget.byId('contentFormAddress').setValues(myObj);  

//   var testValues = dojo.widget.byId('headerFormObject').getValues();
//   dojo.debug("Object is: " + dojo.json.serialize(testValues));

}


// ----- Add the listener -> TODO move to mdek_entry.jsp -----
function nodeSelectListener(message)
{
  EntryService.getNodeData(message.node.id, message.node.nodeAppType, 'false',
    {
      callback:treeEventHandler.handleNodeSelected,	// Callback Funktion die aufgerufen wird sobald Daten verfuegbar sind
      timeout:5000,
      errorHandler:function(message) {alert("Error in js/treeEventHandler.js: Timeout while waiting for nodeData: " + message); }
    }
  );
}

dojo.addOnLoad(function()
{
  var treeListener = dojo.widget.byId('treeListener');
  if (treeListener)
    dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelectListener");
  else
    alert("Error in js/treeEventHandler.js: Widget treeListener could not be found.");
});
