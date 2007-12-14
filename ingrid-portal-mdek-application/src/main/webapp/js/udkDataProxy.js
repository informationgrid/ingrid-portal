/*
 * This proxy is used to read from and write to the different gui elements.
 * Additional checks are performed to ensure that no data is lost in the process (e.g.
 * asking the user if unsaved changes should be discarded)
 *
 * The proxy is called indirectly via the following topics (dojo.event.topic.publish(topic, message)):
 *   topic = '/loadRequest' - argument: {id: nodeUuid, appType: appType}
 *     nodeUuid - The Uuid of the node
 *     appType  - 'A' for Address, 'O' for Object
 *
 *   topic = '/saveRequest' - argument:
 *   ...
 */

dojo.addOnLoad(function()
  {
    dojo.event.topic.subscribe("/loadRequest", udkDataProxy, 'handleLoadRequest');
    dojo.event.topic.subscribe("/saveRequest", udkDataProxy, 'handleSaveRequest');
  }
);


var udkDataProxy = {};

// In currentUdk we cache the currently loaded udk in it's original representation.
// Changes are not tracked here! We need it to access static information that is not
// displayed in the gui (e.g. nodeUUID).
var currentUdk = {}; 

udkDataProxy.handleLoadRequest = function(node)
{
  // TODO Check if we are in a state where it's safe to load data.
  //      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).

  dojo.debug('udkDataProxy calling EntryService.getNodeData('+node.id+', '+node.appType+')');
  // ---- DWR call to load the data ----
  EntryService.getNodeData(node.id, node.appType, 'false',
    {
      callback:udkDataProxy._setData,
      timeout:5000,
      errorHandler:function(message) {alert("Error in js/udkDataProxy.js: Error while waiting for nodeData: " + message); }
    }
  );
}


udkDataProxy.handleSaveRequest = function()
{
 /* TODO Check if we are in a state where it's safe to save data.
  *      If we are, read all the fields and send the collected data to
  *      the EntryService. If not delay the call and bounce back the message (e.g. query user).
  */

  // Construct an MdekDataBean from the available data
  var nodeData = udkDataProxy._getData();

  // ---- DWR call to store the data ----
  EntryService.saveNodeData(nodeData, 'false',
    {
      callback: function(){dojo.debug('Data saved successfully.');},
      timeout:5000,
      errorHandler:function(message) {alert("Error in js/udkDataProxy.js: Error while saving nodeData: " + message); }
    }
  );
}


udkDataProxy._setData = function(nodeData)
{
  currentUdk = nodeData;

  dojo.debug("NodeData Properties: ");
  for (property in nodeData)
  {
    dojo.debug(property+": "+ nodeData[property]);
  }

//  dojo.debug("Node Data received (from callback): [ID="+nodeData.id +", Type="+nodeData.nodeAppType+", title="+nodeData.title+"]");

  // -- We check if we received an Address or Object and call the corresponding function --
  switch (nodeData.nodeAppType.toUpperCase())
  {
    case 'A':
      udkDataProxy._setAddressData(nodeData);
      break;
    case 'O':
      udkDataProxy._setObjectData(nodeData);
      break;
    default:
      dojo.debug('Error in udkDataProxy._setData - Node Type must be \'A\' or \'O\'!');
      break;
  }  
}

udkDataProxy._setAddressData = function(nodeData)
{
}

udkDataProxy._setObjectData = function(nodeData)
{
  /* 
   * 1. Set the data common to all objects which is:
   *   Header, General, Spatial, Time, Extra Info,
   *   Availability, Thesaurus and Links
   *
   * 2. Set the variable information depending on the object class
   *
   */

  // ------------------ Header ------------------
  var formWidget = dojo.widget.byId('headerFormObject');
  
//  dojo.debug("HeaderObjectForm before setting values: " + dojo.json.serialize(formWidget.getValues()));

  dojo.widget.byId('objectName').setValue(nodeData.objectName);
  dojo.widget.byId('objectClass').setValue(nodeData.nodeDocType);
  //  dojo.widget.byId('last_editor').setValue('test last editor');

//  dojo.debug("HeaderObjectForm after setting values: " + dojo.json.serialize(formWidget.getValues()));


  // ------------------ Object Content ------------------
  formWidget = dojo.widget.byId('contentFormObject');
//  dojo.debug("ContentFormObject before setting values: " + dojo.json.serialize(formWidget.getValues()));

  // --- General ---
  dojo.widget.byId('generalShortDesc').setValue(nodeData.generalShortDescription);
  dojo.widget.byId('generalDesc').setValue(nodeData.generalDescription);
  dojo.widget.byId('generalAddress').store.setData(nodeData.generalAddressTable);

  // -- Spatial --
//  dojo.widget.byId('spatialRefAdminUnit').store.setData(nodeData.spatialRefAdminUnitTable);
//  dojo.widget.byId('spatialRefCoordsAdminUnit').store.setData(nodeData.spatialRefCoordsAdminUnitTable);
//  dojo.widget.byId('spatialRefLocation').store.setData(nodeData.spatialRefLocationTable);
//  dojo.widget.byId('spatialRefCoordsLocation').store.setData(nodeData.spatialRefCoordsLocationTable);

  dojo.widget.byId('spatialRefAltMin').setValue(nodeData.spatialRefAltMin);
  dojo.widget.byId('spatialRefAltMax').setValue(nodeData.spatialRefAltMax);
  dojo.widget.byId('spatialRefAltMeasure').setValue(nodeData.spatialRefAltMeasure);
  dojo.widget.byId('spatialRefAltVDate').setValue(nodeData.spatialRefAltVDate);
  dojo.widget.byId('spatialRefExplanation').setValue(nodeData.spatialRefExplanation);

/*
  //  var tableId = dojo.widget.byId('spatialRefAdminUnit').valueField;
  dojo.widget.byId('spatialRefAdminUnit').store.clearData();
  dojo.widget.byId('spatialRefAdminUnit').store.addData({Id: '1', information:'info one', latitude1:'3.14', longitude1:'0.1123', latitude2:'1.234', longitude2:'2.345'});
  dojo.widget.byId('spatialRefAdminUnit').store.addData({Id: '2', information:'info two', latitude1:'1.14', longitude1:'2.1123', latitude2:'4.434', longitude2:'1.2'});

  // -- Time --
  
  // -- Extra Info --
  dojo.widget.byId('extraInfoLangMetaData').setValue('Englisch');
  dojo.widget.byId('extraInfoLangData').setValue('Englisch');

  // -- Availability --

  // -- Thesaurus --
  //  var tableId = dojo.widget.byId('thesaurusTerms').valueField;
  dojo.widget.byId('thesaurusTerms').store.clearData();
  dojo.widget.byId('thesaurusTerms').store.addData({Id: '1', term:'Topographie'});
  dojo.widget.byId('thesaurusTerms').store.addData({Id: '2', term:'Kartographie'});
  dojo.widget.byId('thesaurusTerms').store.addData({Id: '3', term:'Wasser'});

  //  var tableId = dojo.widget.byId('thesaurusTopics').valueField;
  dojo.widget.byId('thesaurusTopics').store.clearData();
  dojo.widget.byId('thesaurusTopics').store.addData({Id: '1', topics:'Kategorie eins'});
  dojo.widget.byId('thesaurusTopics').store.addData({Id: '2', topics:'Kategorie zwei'});
  dojo.widget.byId('thesaurusTopics').store.addData({Id: '3', topics:'Kategorie drei'});

  //  var tableId = dojo.widget.byId('thesaurusFreeTermsList').valueField;
  dojo.widget.byId('thesaurusFreeTermsList').store.clearData();
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({Id: '1', freeTerms:'Freier Term eins'});
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({Id: '2', freeTerms:'Freier Term zwei'});
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({Id: '3', freeTerms:'Freier Term drei'});

  // -- Links --
*/

  // -- Check which object type was received and fill the appropriate fields --
  switch (nodeData.objectClass)
  {
    case 0:
      udkDataProxy._setObjectDataClass0(nodeData);
      break;
    case 1:
      udkDataProxy._setObjectDataClass1(nodeData);
      break;
    case 2:
      udkDataProxy._setObjectDataClass2(nodeData);
      break;
    case 3:
      udkDataProxy._setObjectDataClass3(nodeData);
      break;
    case 4:
      udkDataProxy._setObjectDataClass4(nodeData);
      break;
    case 5:
      udkDataProxy._setObjectDataClass5(nodeData);
      break;
    default:
      dojo.debug('Error in udkDataProxy._setObjectData - Object Class must be 0...5!');
      break;
  }  

  /* 
   * Select box values can't be set through the form(?).   
   * We set all values in the Widgets directly.
   */


//  dojo.debug("ContentFormObject after setting values: " + dojo.json.serialize(formWidget.getValues()));


//  dojo.widget.byId('contentFormObject').setValues(myObj);
//  dojo.widget.byId('headerFormAddress').setValues(myObj);
//  dojo.widget.byId('contentFormAddress').setValues(myObj);
}

udkDataProxy._setObjectDataClass0 = function(nodeData)
{
}

udkDataProxy._setObjectDataClass1 = function(nodeData)
{
  dojo.widget.byId('ref1DataSet').setValue('2');

  dojo.widget.byId('ref1Representation').store.clearData();
  dojo.widget.byId('ref1Representation').store.addData({Id: '1', representation:'Rasterdaten'});
  dojo.widget.byId('ref1Representation').store.addData({Id: '2', representation:'Another Representation'});
}

udkDataProxy._setObjectDataClass2 = function(nodeData)
{
}

udkDataProxy._setObjectDataClass3 = function(nodeData)
{
}

udkDataProxy._setObjectDataClass4 = function(nodeData)
{
}

udkDataProxy._setObjectDataClass5 = function(nodeData)
{
}




/********************************************
 * Methods for sending data to the database *
 ********************************************/

udkDataProxy._getData = function()
{
  var nodeData = {};

  // Safety check if the currentUdk really is the selected Node
  var node = dojo.widget.byId('tree').selectedNode;
  if (!node) {
    dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
    return nodeData;
  }
  else if (node.id != currentUdk.id) {
    dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
    return nodeData;
  }

  // The currentUdk is the selected node so we can continue loading  
  nodeData.nodeAppType = currentUdk.nodeAppType;

  // -- We check which node needs to get saved --
  switch (nodeData.nodeAppType.toUpperCase())
  {
    case 'A':
      udkDataProxy._getAddressData(nodeData);
      break;
    case 'O':
      udkDataProxy._getObjectData(nodeData);
      break;
    default:
      dojo.debug('Error in udkDataProxy._getData - Node Type must be \'A\' or \'O\'!');
      break;
  }

  return (nodeData);
}

udkDataProxy._getObjectData = function(nodeData)
{
  /* 
   * 1. Get the static data that is not displayed in the gui which is:
   *    nodeUuid, hasChildren
   * 
   * 2. Get the data common to all objects which is:
   *    Header, General, Spatial, Time, Extra Info,
   *    Availability, Thesaurus and Links
   *
   * 3. Get the variable information depending on the object class
   *
   */

  // ------------- General Static Data -------------
  nodeData.id = currentUdk.id;
  nodeData.hasChildren = currentUdk.hasChildren; 


  // ------------------ Header ------------------
  var formWidget = dojo.widget.byId('headerFormObject');

//  dojo.debug("HeaderObjectForm values: " + dojo.json.serialize(formWidget.getValues()));

  nodeData.objectName = dojo.widget.byId('objectName').getValue();
  nodeData.nodeDocType = dojo.widget.byId('objectClass').getValue();
//  nodeData.last_editor = dojo.widget.byId('last_editor').getValue();

  // ------------------ Object Content ------------------
  formWidget = dojo.widget.byId('contentFormObject');
//  dojo.debug("ContentFormObject values: " + dojo.json.serialize(formWidget.getValues()));

  // --- General ---
  nodeData.generalShortDescription = dojo.widget.byId('generalShortDesc').getValue();
  nodeData.generalDescription = dojo.widget.byId('generalDesc').getValue();

  nodeData.generalAddressTable = udkDataProxy._getTableData('generalAddress');

/*
  // -- Spatial --
  //  var tableId = dojo.widget.byId('spatialRefAdminUnit').valueField;
  dojo.widget.byId('spatialRefAdminUnit').store.clearData();
  dojo.widget.byId('spatialRefAdminUnit').store.addData({Id: '1', information:'info one', latitude1:'3.14', longitude1:'0.1123', latitude2:'1.234', longitude2:'2.345'});
  dojo.widget.byId('spatialRefAdminUnit').store.addData({Id: '2', information:'info two', latitude1:'1.14', longitude1:'2.1123', latitude2:'4.434', longitude2:'1.2'});

  // -- Time --
  
  // -- Extra Info --
  dojo.widget.byId('extraInfoLangMetaData').setValue('Englisch');
  dojo.widget.byId('extraInfoLangData').setValue('Englisch');

  // -- Availability --

  // -- Thesaurus --
  //  var tableId = dojo.widget.byId('thesaurusTerms').valueField;
  dojo.widget.byId('thesaurusTerms').store.clearData();
  dojo.widget.byId('thesaurusTerms').store.addData({Id: '1', term:'Topographie'});
  dojo.widget.byId('thesaurusTerms').store.addData({Id: '2', term:'Kartographie'});
  dojo.widget.byId('thesaurusTerms').store.addData({Id: '3', term:'Wasser'});

  //  var tableId = dojo.widget.byId('thesaurusTopics').valueField;
  dojo.widget.byId('thesaurusTopics').store.clearData();
  dojo.widget.byId('thesaurusTopics').store.addData({Id: '1', topics:'Kategorie eins'});
  dojo.widget.byId('thesaurusTopics').store.addData({Id: '2', topics:'Kategorie zwei'});
  dojo.widget.byId('thesaurusTopics').store.addData({Id: '3', topics:'Kategorie drei'});

  //  var tableId = dojo.widget.byId('thesaurusFreeTermsList').valueField;
  dojo.widget.byId('thesaurusFreeTermsList').store.clearData();
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({Id: '1', freeTerms:'Freier Term eins'});
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({Id: '2', freeTerms:'Freier Term zwei'});
  dojo.widget.byId('thesaurusFreeTermsList').store.addData({Id: '3', freeTerms:'Freier Term drei'});

  // -- Links --
*/

  // -- Check which object type was received and fill the appropriate fields --
  switch (nodeData.objectClass)
  {
    case 0:
      udkDataProxy._getObjectDataClass0(nodeData);
      break;
    case 1:
      udkDataProxy._getObjectDataClass1(nodeData);
      break;
    case 2:
      udkDataProxy._getObjectDataClass2(nodeData);
      break;
    case 3:
      udkDataProxy._getObjectDataClass3(nodeData);
      break;
    case 4:
      udkDataProxy._getObjectDataClass4(nodeData);
      break;
    case 5:
      udkDataProxy._getObjectDataClass5(nodeData);
      break;
    default:
      dojo.debug('Error in udkDataProxy._getObjectData - Object Class must be 0...5!');
      break;
  }  

  /* 
   * Select box values can't be set through the form(?).   
   * We set all values in the Widgets directly.
   */


//  dojo.widget.byId('contentFormObject').getValues(myObj);
//  dojo.widget.byId('headerFormAddress').getValues(myObj);
//  dojo.widget.byId('contentFormAddress').getValues(myObj);
}

udkDataProxy._getObjectDataClass0 = function(nodeData) {};
udkDataProxy._getObjectDataClass1 = function(nodeData) {};
udkDataProxy._getObjectDataClass2 = function(nodeData) {};
udkDataProxy._getObjectDataClass3 = function(nodeData) {};
udkDataProxy._getObjectDataClass4 = function(nodeData) {};
udkDataProxy._getObjectDataClass5 = function(nodeData) {};


// ---------------- Helper Functions ----------------

// Returns an array representing the data of the table with name 'tableName'
// The keys are stored in the fields named: 'Id' 
udkDataProxy._getTableData = function(tableName)
{
  return dojo.widget.byId(tableName).store.getData();
}