/*
 * This proxy is used to read from and write to the different gui elements.
 * Additional checks are performed to ensure that no data is lost in the process (e.g.
 * asking the user if unsaved changes should really be discarded)
 *
 * The proxy is called indirectly via the following topics (dojo.event.topic.publish(topic, message)):
 *   topic = '/loadRequest' - argument: {id: nodeUuid, appType: appType}
 *     nodeUuid - The Uuid of the node
 *     appType  - 'A' for Address, 'O' for Object
 *
 *   topic = '/saveRequest' - argument:
 *   ...
 */


var udkDataProxy = {};

// This flag is set when any value in the gui changes
var dirtyFlag = false;


// In currentUdk we cache the currently loaded udk in it's original representation.
// Changes are not tracked here! We need it to access static information that is not
// displayed in the gui (e.g. nodeUUID).
var currentUdk = {}; 


// TODO Move Dirty Flag handling to another file? 
dojo.addOnLoad(function()
  {
    dojo.event.topic.subscribe("/loadRequest", udkDataProxy, 'handleLoadRequest');
    dojo.event.topic.subscribe("/saveRequest", udkDataProxy, 'handleSaveRequest');

	var treeListener = dojo.widget.byId("treeListener");
	dojo.event.topic.subscribe(treeListener.eventNames.deselect, function(arg) {
//			arg.node:this.selectedNode, arg.target:event.target	
//			dojo.debug("Setting prev selected node to: "+arg.node.id);
			udkDataProxy.prevSelectedNode = arg.node;
		}
	);


	// Connect the widgets onChange methods to the setDirtyFlag Method
    dojo.event.connect(dojo.widget.byId('objectName'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('objectClass'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('objectOwner'), 'onValueChanged', 'setDirtyFlag');

    dojo.event.connect(dojo.widget.byId('generalShortDesc'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('generalDesc'), 'onkeyup', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('generalAddress').store);

    dojo.event.connect(dojo.widget.byId('ref1DataSet'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref1Coverage'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref1Representation'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref1VFormatTopology'), 'onValueChanged', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1VFormatDetails').store);
    dojo.event.connect(dojo.widget.byId('ref1SpatialSystem'), 'onValueChanged', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1Scale').store);
    dojo.event.connect(dojo.widget.byId('ref1AltAccuracy'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref1PosAccuracy'), 'onkeyup', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1SymbolsText').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1KeysText').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1ServiceLink').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1BasisLink').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1DataBasisLink').store);
    dojo.event.connect(dojo.widget.byId('ref1Data'), 'onkeyup', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref1ProcessLink').store);

    dojo.event.connect(dojo.widget.byId('ref2Author'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2Publisher'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishedIn'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishLocation'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishedInIssue'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishedInPages'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishedInYear'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishedISBN'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2PublishedPublisher'), 'onkeyup', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref2LocationLink').store);
    dojo.event.connect(dojo.widget.byId('ref2DocumentType'), 'onValueChanged', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref2BaseDataLink').store);
    dojo.event.connect(dojo.widget.byId('ref2BibDataIn'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref2Explanation'), 'onkeyup', 'setDirtyFlag');

	_connectStoreWithDirtyFlag(dojo.widget.byId('ref3ServiceType').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref3ServiceVersion').store);
    dojo.event.connect(dojo.widget.byId('ref3SystemEnv'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('ref3History'), 'onkeyup', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref3BaseDataLink').store);
    dojo.event.connect(dojo.widget.byId('ref3Explanation'), 'onkeyup', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref3Operation').store);

	_connectStoreWithDirtyFlag(dojo.widget.byId('ref4ParticipantsLink').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref4PMLink').store);

	_connectStoreWithDirtyFlag(dojo.widget.byId('ref5Scale').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('ref5MethodLink').store);
    dojo.event.connect(dojo.widget.byId('ref5Explanation'), 'onkeyup', 'setDirtyFlag');

	_connectStoreWithDirtyFlag(dojo.widget.byId('spatialRefAdminUnit').store);
//	_connectStoreWithDirtyFlag(dojo.widget.byId('spatialRefCoordsAdminUnit').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('spatialRefLocation').store);
//	_connectStoreWithDirtyFlag(dojo.widget.byId('spatialRefCoordsLocation').store);
    dojo.event.connect(dojo.widget.byId('spatialRefAltMin'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('spatialRefAltMax'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('spatialRefAltMeasure'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('spatialRefAltVDate'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('spatialRefExplanation'), 'onkeyup', 'setDirtyFlag');

    dojo.event.connect(dojo.widget.byId('timeRefType'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('timeRefDate1'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('timeRefDate2'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('timeRefStatus'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('timeRefPeriodicity'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('timeRefIntervalNum'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('timeRefIntervalUnit'), 'onValueChanged', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('timeRefTable').store);
    dojo.event.connect(dojo.widget.byId('timeRefExplanation'), 'onkeyup', 'setDirtyFlag');

    dojo.event.connect(dojo.widget.byId('extraInfoLangMetaData'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('extraInfoLangData'), 'onValueChanged', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('extraInfoPublishArea'), 'onValueChanged', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('extraInfoXMLExport').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('extraInfoLegalBasics').store);
    dojo.event.connect(dojo.widget.byId('extraInfoPurpose'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('extraInfoUse'), 'onkeyup', 'setDirtyFlag');

	_connectStoreWithDirtyFlag(dojo.widget.byId('availabilityDataFormat').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('availabilityMediaOptions').store);
    dojo.event.connect(dojo.widget.byId('availabilityOrderInfo'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('availabilityNoteUse'), 'onkeyup', 'setDirtyFlag');
    dojo.event.connect(dojo.widget.byId('availabilityCosts'), 'onkeyup', 'setDirtyFlag');

	_connectStoreWithDirtyFlag(dojo.widget.byId('thesaurusTerms').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('thesaurusTopics').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('thesaurusFreeTermsList').store);
    dojo.event.connect(dojo.widget.byId('thesaurusEnvExtRes'), 'onValueChanged', 'setDirtyFlag');
	_connectStoreWithDirtyFlag(dojo.widget.byId('thesaurusEnvTopics').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('thesaurusEnvCats').store);

	_connectStoreWithDirtyFlag(dojo.widget.byId('linksTo').store);
	_connectStoreWithDirtyFlag(dojo.widget.byId('linksFrom').store);

	dojo.event.connect(udkDataProxy, '_setData', 'resetDirtyFlag');
/*
	// Table
	_connectStoreWithDirtyFlag(dojo.widget.byId('TABLENAME').store);
	// SelectBox
    dojo.event.connect(dojo.widget.byId('SELECTBOXNAME'), 'onValueChanged', 'setDirtyFlag');
	// ValidationTextbox
    dojo.event.connect(dojo.widget.byId('TEXTBOXNAME'), 'onkeyup', 'setDirtyFlag');
*/
  }
);

setDirtyFlag = function()
{
	dirtyFlag = true;
}

resetDirtyFlag = function()
{
	dirtyFlag = false;
}

_connectStoreWithDirtyFlag = function(store)
{
	dojo.event.connect(store, 'onSetData', 'setDirtyFlag');
	dojo.event.connect(store, 'onAddData', 'setDirtyFlag');
	dojo.event.connect(store, 'onAddDataRange', 'setDirtyFlag');
	dojo.event.connect(store, 'onRemoveData', 'setDirtyFlag');
	dojo.event.connect(store, 'onUpdateField', 'setDirtyFlag');
	dojo.event.connect(store, 'onSetData', 'setDirtyFlag');
}


udkDataProxy.handleLoadRequest = function(node)
{
	// TODO Check if we are in a state where it's safe to load data.
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	if (dirtyFlag == true) {
		dialog.showPage('Save changes', 'mdek_save_changes.html', 342, 220, true);
		udkDataProxy.lastNodeLoadRequest = node;
		return;
	} else {
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
}


udkDataProxy.handleSaveRequest = function()
{
	/* TODO Check if we are in a state where it's safe to save data (always?)
	 *      If we are, read all the fields and send the collected data to
	 *      the EntryService. If not delay the call and bounce back the message (e.g. query user).
	 */
	
	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();
	
	// ---- DWR call to store the data ----
	dojo.debug('udkDataProxy calling EntryService.saveNodeData('+nodeData.uuid);
	EntryService.saveNodeData(nodeData, 'false',
		{
			callback: function(){resetDirtyFlag(); udkDataProxy._updateTree(nodeData); udkDataProxy.onAfterSave();},
			timeout:5000,
			errorHandler:function(message) {alert("Error in js/udkDataProxy.js: Error while saving nodeData: " + message); }
		}
	);
}

// event.connect point. Called when data has been saved 
udkDataProxy.onAfterSave = function() {}

udkDataProxy._setData = function(nodeData)
{
  currentUdk = nodeData;
/*
  dojo.debug("NodeData Properties: ");
  for (property in nodeData)
  {
    dojo.debug(property+": "+ nodeData[property]);
  }
*/
  // -- We check if we received an Address or Object and call the corresponding function --
  switch (nodeData.nodeAppType.toUpperCase())
  {
    case 'A':
      udkDataProxy._setAddressData(nodeData);
      break;
    case 'O':
      udkDataProxy._setObjectData(nodeData);
      udkDataProxy._updateTree(nodeData);
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
  dojo.widget.byId('generalAddress').render();

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

//  dojo.debug("ContentFormObject after setting values: " + dojo.json.serialize(formWidget.getValues()));

// The values are set with the corresponding setter methods
// We could also set the values through the form
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




/*******************************************
 * Methods for sending data to the backend *
 *******************************************/

udkDataProxy._getData = function()
{
  var nodeData = {};

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
   *    nodeUuid, id, hasChildren
   * 
   * 2. Get the data common to all objects which is:
   *    Header, General, Spatial, Time, Extra Info,
   *    Availability, Thesaurus and Links
   *
   * 3. Get the variable information depending on the object class
   *
   */

  // ------------- General Static Data -------------
  nodeData.uuid = currentUdk.uuid;
  nodeData.id = currentUdk.id;
  nodeData.hasChildren = currentUdk.hasChildren; // Do we need to store this?


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

  dojo.debug("------ NODE DATA ------");
  dojo.debugShallow(nodeData);
  dojo.debug("------ NODE DATA END ------");

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


/*******************************************
 *            Helper functions             *
 *******************************************/

// Looks for the node widget with uuid = nodeData.uuid and updates the
// tree data (label, type, etc.) according to the given nodeData
udkDataProxy._updateTree = function(nodeData) {
	var node = dojo.widget.byId(nodeData.uuid);
	node.nodeDocType = nodeData.nodeDocType;	
	node.setTitle(nodeData.objectName);
}


// Returns an array representing the data of the table with name 'tableName'
// The keys are stored in the fields named: 'Id' 
udkDataProxy._getTableData = function(tableName)
{
  return dojo.widget.byId(tableName).store.getData();
}