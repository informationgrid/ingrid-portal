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
 *   topic = '/createObjectRequest' - argument: {id: parentUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/saveRequest' - argument:
 *
 *   topic = '/deleteRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be deleted
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/deleteWorkingCopyRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the working copy node which should be deleted
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/canCutObjectRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be marked for a cut operation
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/canCopyObjectRequest' - argument: {id: nodeUuid, copyTree: boolean resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be marked for a copied operation
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/cutObjectRequest' - argument: {srcId: srcUuid, dstId: dstUuid, resultHandler: deferred}
 *     srcId - The Uuid of the node which should be cut
 *     dstId - The Uuid of the target node where the srcNode should be attached
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/copyObjectRequest' - argument: {srcId: srcUuid, dstId: dstUuid, copyTree: boolean, resultHandler: deferred}
 *     srcId - The Uuid of the node which should be copied
 *     dstId - The Uuid of the target node where the srcNode should be attached
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/getObjectPathRequest' - argument: {id: nodeUuid, resultHandler: deferred, ignoreDirtyFlag: bool}
 *     nodeUuid - The Uuid of the target Node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *     ignoreDirtyFlag - boolean value indicating if the dirty flag should be ignored
 *
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
    dojo.event.topic.subscribe("/loadRequest", udkDataProxy, "handleLoadRequest");
    dojo.event.topic.subscribe("/saveRequest", udkDataProxy, "handleSaveRequest");
    dojo.event.topic.subscribe("/createObjectRequest", udkDataProxy, "handleCreateObjectRequest");
    dojo.event.topic.subscribe("/deleteRequest", udkDataProxy, "handleDeleteRequest");
    dojo.event.topic.subscribe("/deleteWorkingCopyRequest", udkDataProxy, "handleDeleteWorkingCopyRequest");
    dojo.event.topic.subscribe("/canCutObjectRequest", udkDataProxy, "handleCanCutObjectRequest");
	dojo.event.topic.subscribe("/canCopyObjectRequest", udkDataProxy, "handleCanCopyObjectRequest");
    dojo.event.topic.subscribe("/cutObjectRequest", udkDataProxy, "handleCutObjectRequest");
    dojo.event.topic.subscribe("/copyObjectRequest", udkDataProxy, "handleCopyObjectRequest");
    dojo.event.topic.subscribe("/getObjectPathRequest", udkDataProxy, "handleGetObjectPathRequest");


	var treeListener = dojo.widget.byId("treeListener");


	// Before a node is selected we ask the user if he wants to save unsaved changes.
	// Connection to the onClick function does not work for some odd reasons (?)
	// We connect to the processNode function instead.
	var aroundTreeClick = function(invocation) {
    	if (dojo.html.hasClass(invocation.args[1].target, "TreeLabel")) {	
			if (invocation.args[0].id == "newNode") {
				// Don't display the dialog if a node with id newNode was clicked.
				// This is to prevent the node from being deleted when the user clicks
				// a newly created node
				return invocation.proceed();
			} else {
				var deferred = udkDataProxy.checkForUnsavedChanges(invocation.args[0].id);
				var stdCallback = function() {return invocation.proceed();};
				var errorCallback = function() {dojo.debug("Select cancelled.");};
			}
			deferred.addCallbacks(stdCallback, errorCallback);
			return deferred;
		} else {
			return invocation.proceed();
		}	
	}
	dojo.event.connect("around", treeListener, "processNode", aroundTreeClick);


	// Connect the widgets onChange methods to the setDirtyFlag Method
    dojo.event.connect(dojo.widget.byId("objectName"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("objectClass"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("objectOwner"), "onValueChanged", "setDirtyFlag");

    dojo.event.connect(dojo.widget.byId("generalShortDesc"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("generalDesc"), "onkeyup", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("generalAddress").store);

    dojo.event.connect(dojo.widget.byId("ref1DataSet"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1Coverage"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1Representation"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1VFormatTopology"), "onValueChanged", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1VFormatDetails").store);
    dojo.event.connect(dojo.widget.byId("ref1SpatialSystem"), "onValueChanged", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1Scale").store);
    dojo.event.connect(dojo.widget.byId("ref1AltAccuracy"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1PosAccuracy"), "onkeyup", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1SymbolsText").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1KeysText").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1ServiceLink").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1BasisLink").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1DataBasisLink").store);
    dojo.event.connect(dojo.widget.byId("ref1Data"), "onkeyup", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1ProcessLink").store);

    dojo.event.connect(dojo.widget.byId("ref2Author"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2Publisher"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedIn"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishLocation"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedInIssue"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedInPages"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedInYear"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedISBN"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedPublisher"), "onkeyup", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref2LocationLink").store);
    dojo.event.connect(dojo.widget.byId("ref2DocumentType"), "onValueChanged", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref2BaseDataLink").store);
    dojo.event.connect(dojo.widget.byId("ref2BibDataIn"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2Explanation"), "onkeyup", "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3ServiceType").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3ServiceVersion").store);
    dojo.event.connect(dojo.widget.byId("ref3SystemEnv"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref3History"), "onkeyup", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3BaseDataLink").store);
    dojo.event.connect(dojo.widget.byId("ref3Explanation"), "onkeyup", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3Operation").store);

	_connectStoreWithDirtyFlag(dojo.widget.byId("ref4ParticipantsLink").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref4PMLink").store);

	_connectStoreWithDirtyFlag(dojo.widget.byId("ref5Scale").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref5MethodLink").store);
    dojo.event.connect(dojo.widget.byId("ref5Explanation"), "onkeyup", "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefAdminUnit").store);
//	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefCoordsAdminUnit").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefLocation").store);
//	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefCoordsLocation").store);
    dojo.event.connect(dojo.widget.byId("spatialRefAltMin"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefAltMax"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefAltMeasure"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefAltVDate"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefExplanation"), "onkeyup", "setDirtyFlag");

    dojo.event.connect(dojo.widget.byId("timeRefType"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefDate1"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefDate2"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefStatus"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefPeriodicity"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefIntervalNum"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefIntervalUnit"), "onValueChanged", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("timeRefTable").store);
    dojo.event.connect(dojo.widget.byId("timeRefExplanation"), "onkeyup", "setDirtyFlag");

    dojo.event.connect(dojo.widget.byId("extraInfoLangMetaData"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("extraInfoLangData"), "onValueChanged", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("extraInfoPublishArea"), "onValueChanged", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("extraInfoXMLExport").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("extraInfoLegalBasics").store);
    dojo.event.connect(dojo.widget.byId("extraInfoPurpose"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("extraInfoUse"), "onkeyup", "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("availabilityDataFormat").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("availabilityMediaOptions").store);
    dojo.event.connect(dojo.widget.byId("availabilityOrderInfo"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("availabilityNoteUse"), "onkeyup", "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("availabilityCosts"), "onkeyup", "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusTerms").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusTopics").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusFreeTermsList").store);
    dojo.event.connect(dojo.widget.byId("thesaurusEnvExtRes"), "onValueChanged", "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusEnvTopics").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusEnvCats").store);

	_connectStoreWithDirtyFlag(dojo.widget.byId("linksTo").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("linksFrom").store);

	dojo.event.connect(udkDataProxy, "_setData", "resetDirtyFlag");
/*
	// Table
	_connectStoreWithDirtyFlag(dojo.widget.byId("TABLENAME").store);
	// SelectBox
    dojo.event.connect(dojo.widget.byId("SELECTBOXNAME"), "onValueChanged", "setDirtyFlag");
	// ValidationTextbox
    dojo.event.connect(dojo.widget.byId("TEXTBOXNAME"), "onkeyup", "setDirtyFlag");
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
	dojo.event.connect(store, "onSetData", "setDirtyFlag");
	dojo.event.connect(store, "onAddData", "setDirtyFlag");
	dojo.event.connect(store, "onAddDataRange", "setDirtyFlag");
	dojo.event.connect(store, "onRemoveData", "setDirtyFlag");
	dojo.event.connect(store, "onUpdateField", "setDirtyFlag");
	dojo.event.connect(store, "onSetData", "setDirtyFlag");
}

// This function has to be called before any UI functions that are about to change the
// state of the tree or the currently displayed dataset. It checks if there are unsaved changes
// and queries the user if he wants to save changes, discard them or abort.
// The function returns a deferred object users can attach callbacks to.
// The deferred object signals an error if the user canceled the operation. No state changes
// should be done in this case.
// @arg nodeId - optional parameter that specifies the node that is about to be loaded
udkDataProxy.checkForUnsavedChanges = function(nodeId)
{
	dojo.debug("Check for unsaved changes called.");

	var deferred = new dojo.Deferred();
	if (dirtyFlag == true) {
		dialog.showPage("Save changes", "mdek_save_changes.html", 342, 220, true, {resultHandler: deferred});

		// If the user was editing a newly created node and he wants to discard the changes
		// delete the newly created node.
		if (currentUdk.uuid == "newNode" && nodeId != "newNode") {
			var discardNewNode = function(arg) {
				if (arg == "DISCARD") {
					dojo.debug("Discarding the newly created node.");
					var newNode = dojo.widget.byId("newNode");
					newNode.destroy();
				}
			};
			deferred.addCallback(discardNewNode);
		}
	} else {
		deferred.callback();
	}

	return deferred;
}


udkDataProxy.handleLoadRequest = function(node)
{
	dojo.debug("About to be loaded: "+node.id);

	// Don't process newNode load requests. We have this request because we
	// select new nodes in the tree after creating them
	if (node.id == "newNode") {
		return;
	}

	// TODO Check if we are in a state where it's safe to load data.
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function() {return;}
	var loadCallback = function() {
		dojo.debug("udkDataProxy calling EntryService.getNodeData("+node.id+", "+node.appType+")");
		// ---- DWR call to load the data ----
		EntryService.getNodeData(node.id, node.appType, "false",
			{
				callback:udkDataProxy._setData,
				timeout:5000,
				errorHandler:function(message) {dojo.debug("Error in js/udkDataProxy.js: Error while waiting for nodeData: " + message); },
				exceptionHandler:function(message) {dojo.debug("Exception in js/udkDataProxy.js: Error while waiting for nodeData: " + message); }				
			}
		);
	};

	deferred.addCallbacks(loadCallback, loadErrback);
}

udkDataProxy.handleCreateObjectRequest = function(msg)
{
	// TODO Check if we are in a state where it's safe to create a node?
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function() {msg.resultHandler.errback();}
	var loadCallback = function() {
		EntryService.createNewNode(msg.id,
			{
				callback: function(res){
						msg.resultHandler.callback(res);
						udkDataProxy._setData(res);
//						udkDataProxy._updateTree(res);
						setDirtyFlag();
					},
				timeout:5000,
				errorHandler:function(message) {msg.resultHandler.errback(); alert("Error in js/udkDataProxy.js: Error while creating a new node: " + message); }
			}
		);	
	}
	deferred.addCallbacks(loadCallback, loadErrback);
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
	dojo.debug("udkDataProxy calling EntryService.saveNodeData("+nodeData.uuid+")");
	EntryService.saveNodeData(nodeData, "false",
		{
			callback: function(res){
				resetDirtyFlag();
				udkDataProxy._setData(res);
				udkDataProxy._updateTree(res, nodeData.uuid);
				udkDataProxy.onAfterSave();
			},
			timeout:5000,
			errorHandler:function(message) {alert("Error in js/udkDataProxy.js: Error while saving nodeData: " + message); }
		}
	);
}


udkDataProxy.handleDeleteWorkingCopyRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.deleteObjectWorkingCopy("+msg.id+")");
	EntryService.deleteObjectWorkingCopy(msg.id, "false",
		{
			callback: function(res){
				if (res != null) {
					resetDirtyFlag();
					udkDataProxy._setData(res);
					udkDataProxy._updateTree(res, msg.id);
					udkDataProxy.onAfterSave();
				}
				msg.resultHandler.callback(res);
			},
			timeout:5000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while deleting working copy: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleDeleteRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.deleteNode("+msg.id+")");
	EntryService.deleteNode(msg.id, "false",
		{
			callback: function(res){msg.resultHandler.callback();},
			timeout:5000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while deleting node: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleCanCutObjectRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.canCutNode("+msg.id+")");	

	EntryService.canCutObject(msg.id,
		{
			callback: function(res){msg.resultHandler.callback();},
			timeout:5000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while marking a node for a cut operation: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleCanCopyObjectRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.canCopyNode("+msg.id+", "+msg.copyTree+")");	

	EntryService.canCopyObject(msg.id,
		{
			callback: function(res){msg.resultHandler.callback();},
			timeout:5000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while marking a node for a copy operation: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleCutObjectRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.cutNode("+msg.srcId+", "+msg.dstId+")");	

	EntryService.moveNode(msg.srcId, msg.dstId,
		{
			callback: function(res){msg.resultHandler.callback();},
			timeout:5000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while moving nodes: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleCopyObjectRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.copyNode("+msg.srcId+", "+msg.dstId+", "+msg.copyTree+")");	

	EntryService.copyNode(msg.srcId, msg.dstId, msg.copyTree,
		{
			callback: function(res){msg.resultHandler.callback();},
			timeout:5000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while copying nodes: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleGetObjectPathRequest = function(msg) {
	var loadErrback = function() {msg.resultHandler.errback();}
	var loadCallback = function() {
		dojo.debug("udkDataProxy calling EntryService.getPathToObject("+msg.id+")");	
		EntryService.getPathToObject(msg.id, {
				callback: function(res){msg.resultHandler.callback(res);},
				timeout:5000,
				errorHandler:function(message) {
					alert("Error in js/udkDataProxy.js: Error while getting path to node: " + message);
					msg.resultHandler.errback();
				}
			}
		);	
	}

	if (msg.ignoreDirtyFlag) {
		// If the dirty flag is ignored, the request can be started
		loadCallback(); 
	} else {
		// Otherwise check for unsaved changes and start the request afterwards
		var deferred = udkDataProxy.checkForUnsavedChanges();
		deferred.addCallbacks(loadCallback, loadErrback);
	}
}


// event.connect point. Called when data has been saved 
udkDataProxy.onAfterSave = function() { dojo.debug("onAfterSave()"); }

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
      break;
    default:
      dojo.debug("Error in udkDataProxy._setData - Node Type must be \'A\' or \'O\'!");
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
  var formWidget = dojo.widget.byId("headerFormObject");

//  dojo.debug("HeaderObjectForm before setting values: " + dojo.json.serialize(formWidget.getValues()));

  dojo.widget.byId("objectName").setValue(nodeData.objectName);
  dojo.widget.byId("objectClass").setValue("Class"+nodeData.objectClass);
  dojo.byId("workState").innerHTML = nodeData.workState;
  dojo.byId("creationTime").innerHTML = nodeData.creationTime;
  dojo.byId("modificationTime").innerHTML = nodeData.modificationTime;

//  dojo.widget.byId("last_editor").setValue("test last editor");

//  dojo.debug("HeaderObjectForm after setting values: " + dojo.json.serialize(formWidget.getValues()));


  // ------------------ Object Content ------------------
  formWidget = dojo.widget.byId("contentFormObject");
//  dojo.debug("ContentFormObject before setting values: " + dojo.json.serialize(formWidget.getValues()));

  // --- General ---
  dojo.widget.byId("generalShortDesc").setValue(nodeData.generalShortDescription);
  dojo.widget.byId("generalDesc").setValue(nodeData.generalDescription);
  dojo.widget.byId("generalAddress").store.setData(udkDataProxy._addTableIndices(nodeData.generalAddressTable));

  // -- Spatial --
  // The table containing entries from the sns is indexed by their topicID
  dojo.widget.byId("spatialRefAdminUnit").store.setData(nodeData.spatialRefAdminUnitTable);
  // The table containing free entries needs generated indices
  dojo.widget.byId("spatialRefLocation").store.setData(udkDataProxy._addTableIndices(nodeData.spatialRefLocationTable));

  dojo.widget.byId("spatialRefAltMin").setValue(nodeData.spatialRefAltMin);
  dojo.widget.byId("spatialRefAltMax").setValue(nodeData.spatialRefAltMax);
  dojo.widget.byId("spatialRefAltMeasure").setValue(nodeData.spatialRefAltMeasure);
  dojo.widget.byId("spatialRefAltVDate").setValue(nodeData.spatialRefAltVDate);
  dojo.widget.byId("spatialRefExplanation").setValue(nodeData.spatialRefExplanation);

  // -- Time --
  dojo.widget.byId("timeRefType").setValue(nodeData.timeRefType);
//  if (nodeData.timeRefType) { dojo.widget.byId("timeRefType").setValue(nodeData.timeRefType); }
//  else { dojo.widget.byId("timeRefType").setValue(""); }
  // If we don't receive a date set the date to 'today'. The dojo DatePicker doesn't support clearing
  // the displayed date.
  // TODO create an ingrid:DropdownDatePicker and add a 'clearDate' method
  if (nodeData.timeRefDate1) { dojo.widget.byId("timeRefDate1").setValue(nodeData.timeRefDate1); }
  else { dojo.widget.byId("timeRefDate1").setDate(new Date()); }
  if (nodeData.timeRefDate2) { dojo.widget.byId("timeRefDate2").setValue(nodeData.timeRefDate2); }
  else { dojo.widget.byId("timeRefDate2").setDate(new Date()); }
  dojo.widget.byId("timeRefStatus").setValue(nodeData.timeRefStatus);
  dojo.widget.byId("timeRefPeriodicity").setValue(nodeData.timeRefPeriodicity);
  dojo.widget.byId("timeRefIntervalNum").setValue(nodeData.timeRefIntervalNum);
  dojo.widget.byId("timeRefIntervalUnit").setValue(nodeData.timeRefIntervalUnit);
  dojo.widget.byId("timeRefExplanation").setValue(nodeData.timeRefExplanation);

  // -- Extra Info --
  dojo.widget.byId("extraInfoLangMetaData").setValue(nodeData.extraInfoLangMetaData);
  dojo.widget.byId("extraInfoLangData").setValue(nodeData.extraInfoLangData);
  dojo.widget.byId("extraInfoPublishArea").setValue(nodeData.extraInfoPublishArea);
  dojo.widget.byId("extraInfoPurpose").setValue(nodeData.extraInfoPurpose);
  dojo.widget.byId("extraInfoUse").setValue(nodeData.extraInfoUse);

  // -- Availability --
  dojo.widget.byId("availabilityOrderInfo").setValue(nodeData.availabilityOrderInfo);
  dojo.widget.byId("availabilityNoteUse").setValue(nodeData.availabilityNoteUse);
  dojo.widget.byId("availabilityCosts").setValue(nodeData.availabilityCosts);

/*
  //  var tableId = dojo.widget.byId("spatialRefAdminUnit").valueField;
  dojo.widget.byId("spatialRefAdminUnit").store.clearData();
  dojo.widget.byId("spatialRefAdminUnit").store.addData({Id: "1", information:"info one", latitude1:"3.14", longitude1:"0.1123", latitude2:"1.234", longitude2:"2.345"});
  dojo.widget.byId("spatialRefAdminUnit").store.addData({Id: "2", information:"info two", latitude1:"1.14", longitude1:"2.1123", latitude2:"4.434", longitude2:"1.2"});

  

  // -- Thesaurus --
  //  var tableId = dojo.widget.byId("thesaurusTerms").valueField;
  dojo.widget.byId("thesaurusTerms").store.clearData();
  dojo.widget.byId("thesaurusTerms").store.addData({Id: "1", term:"Topographie"});
  dojo.widget.byId("thesaurusTerms").store.addData({Id: "2", term:"Kartographie"});
  dojo.widget.byId("thesaurusTerms").store.addData({Id: "3", term:"Wasser"});

  //  var tableId = dojo.widget.byId("thesaurusTopics").valueField;
  dojo.widget.byId("thesaurusTopics").store.clearData();
  dojo.widget.byId("thesaurusTopics").store.addData({Id: "1", topics:"Kategorie eins"});
  dojo.widget.byId("thesaurusTopics").store.addData({Id: "2", topics:"Kategorie zwei"});
  dojo.widget.byId("thesaurusTopics").store.addData({Id: "3", topics:"Kategorie drei"});

  //  var tableId = dojo.widget.byId("thesaurusFreeTermsList").valueField;
  dojo.widget.byId("thesaurusFreeTermsList").store.clearData();
  dojo.widget.byId("thesaurusFreeTermsList").store.addData({Id: "1", freeTerms:"Freier Term eins"});
  dojo.widget.byId("thesaurusFreeTermsList").store.addData({Id: "2", freeTerms:"Freier Term zwei"});
  dojo.widget.byId("thesaurusFreeTermsList").store.addData({Id: "3", freeTerms:"Freier Term drei"});

*/
  // -- Links --
  var linkTable = nodeData.linksToObjectTable;
  udkDataProxy._addTableIndices(linkTable);
  udkDataProxy._addObjectLinkLabels(linkTable);
  dojo.widget.byId("linksTo").store.setData(linkTable);

  linkTable = nodeData.linksFromObjectTable;
  udkDataProxy._addTableIndices(linkTable);
  udkDataProxy._addObjectLinkLabels(linkTable);  
  dojo.widget.byId("linksFrom").store.setData(linkTable);

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
      dojo.debug("Error in udkDataProxy._setObjectData - Object Class must be 0...5!");
      break;
  }

//  dojo.debug("ContentFormObject after setting values: " + dojo.json.serialize(formWidget.getValues()));

// The values are set with the corresponding setter methods
// We could also set the values through the form
//  dojo.widget.byId("contentFormObject").setValues(myObj);
//  dojo.widget.byId("headerFormAddress").setValues(myObj);
//  dojo.widget.byId("contentFormAddress").setValues(myObj);
}

udkDataProxy._setObjectDataClass0 = function(nodeData)
{
}

udkDataProxy._setObjectDataClass1 = function(nodeData)
{
  dojo.widget.byId("ref1DataSet").setValue("2");

  dojo.widget.byId("ref1Representation").store.clearData();
  dojo.widget.byId("ref1Representation").store.addData({Id: "1", representation:"Rasterdaten"});
  dojo.widget.byId("ref1Representation").store.addData({Id: "2", representation:"Another Representation"});
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
      dojo.debug("Error in udkDataProxy._getData - Node Type must be \'A\' or \'O\'!");
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
  nodeData.hasChildren = currentUdk.hasChildren; // Do we need to store this?
  nodeData.parentUuid = dojo.widget.byId(currentUdk.uuid).parent.id;


  // ------------------ Header ------------------
  var formWidget = dojo.widget.byId("headerFormObject");

//  dojo.debug("HeaderObjectForm values: " + dojo.json.serialize(formWidget.getValues()));

  nodeData.objectName = dojo.widget.byId("objectName").getValue();
//  nodeData.last_editor = dojo.widget.byId("last_editor").getValue();

  // ------------------ Object Content ------------------
  formWidget = dojo.widget.byId("contentFormObject");
//  dojo.debug("ContentFormObject values: " + dojo.json.serialize(formWidget.getValues()));

  // --- General ---
  nodeData.generalShortDescription = dojo.widget.byId("generalShortDesc").getValue();
  nodeData.generalDescription = dojo.widget.byId("generalDesc").getValue();
  nodeData.objectClass = dojo.widget.byId("objectClass").getValue()[5]; // Value is a string: "Classx" where x is the class

  nodeData.generalAddressTable = udkDataProxy._getTableData("generalAddress");

  // -- Spatial --
  nodeData.spatialRefAdminUnitTable = udkDataProxy._getTableData("spatialRefAdminUnit");
  nodeData.spatialRefLocationTable = udkDataProxy._getTableData("spatialRefLocation");

  nodeData.spatialRefAltMin = dojo.widget.byId("spatialRefAltMin").getValue();
  nodeData.spatialRefAltMax = dojo.widget.byId("spatialRefAltMax").getValue();
  nodeData.spatialRefAltMeasure = dojo.widget.byId("spatialRefAltMeasure").getValue();
  nodeData.spatialRefAltVDate = dojo.widget.byId("spatialRefAltVDate").getValue();
  nodeData.spatialRefExplanation = dojo.widget.byId("spatialRefExplanation").getValue();

  // -- Time --
  nodeData.timeRefType = dojo.widget.byId("timeRefType").getValue();
//  dojo.debug("Value: "+dojo.widget.byId("timeRefDate1").getValue());
//  dojo.debug("Date: "+dojo.widget.byId("timeRefDate1").getDate());

  if (dojo.widget.byId("timeRefType").getValue() != "") {
	  if (dojo.widget.byId("timeRefDate1").getValue() != "") {
		  nodeData.timeRefDate1 = dojo.widget.byId("timeRefDate1").getDate();
	  }
	  if (dojo.widget.byId("timeRefDate2").getValue() != "") {
		  nodeData.timeRefDate2 = dojo.widget.byId("timeRefDate2").getDate();
	  }
  }

  nodeData.timeRefStatus = dojo.widget.byId("timeRefStatus").getValue();
  nodeData.timeRefPeriodicity = dojo.widget.byId("timeRefPeriodicity").getValue();
  nodeData.timeRefIntervalNum = dojo.widget.byId("timeRefIntervalNum").getValue();
  nodeData.timeRefIntervalUnit = dojo.widget.byId("timeRefIntervalUnit").getValue();
  nodeData.timeRefExplanation = dojo.widget.byId("timeRefExplanation").getValue();

  // -- Extra Info --
  nodeData.extraInfoLangMetaData = dojo.widget.byId("extraInfoLangMetaData").getValue();
  nodeData.extraInfoLangData = dojo.widget.byId("extraInfoLangData").getValue();
  nodeData.extraInfoPublishArea = dojo.widget.byId("extraInfoPublishArea").getValue();
  nodeData.extraInfoPurpose = dojo.widget.byId("extraInfoPurpose").getValue();
  nodeData.extraInfoUse = dojo.widget.byId("extraInfoUse").getValue();

  // -- Availability --
  nodeData.availabilityOrderInfo = dojo.widget.byId("availabilityOrderInfo").getValue();
  nodeData.availabilityNoteUse = dojo.widget.byId("availabilityNoteUse").getValue();
  nodeData.availabilityCosts = dojo.widget.byId("availabilityCosts").getValue();


/*
  //  var tableId = dojo.widget.byId("spatialRefAdminUnit").valueField;
  dojo.widget.byId("spatialRefAdminUnit").store.clearData();
  dojo.widget.byId("spatialRefAdminUnit").store.addData({Id: "1", information:"info one", latitude1:"3.14", longitude1:"0.1123", latitude2:"1.234", longitude2:"2.345"});
  dojo.widget.byId("spatialRefAdminUnit").store.addData({Id: "2", information:"info two", latitude1:"1.14", longitude1:"2.1123", latitude2:"4.434", longitude2:"1.2"});

  // -- Time --
  
  // -- Extra Info --
  dojo.widget.byId("extraInfoLangMetaData").setValue("Englisch");
  dojo.widget.byId("extraInfoLangData").setValue("Englisch");

  // -- Thesaurus --
  //  var tableId = dojo.widget.byId("thesaurusTerms").valueField;
  dojo.widget.byId("thesaurusTerms").store.clearData();
  dojo.widget.byId("thesaurusTerms").store.addData({Id: "1", term:"Topographie"});
  dojo.widget.byId("thesaurusTerms").store.addData({Id: "2", term:"Kartographie"});
  dojo.widget.byId("thesaurusTerms").store.addData({Id: "3", term:"Wasser"});

  //  var tableId = dojo.widget.byId("thesaurusTopics").valueField;
  dojo.widget.byId("thesaurusTopics").store.clearData();
  dojo.widget.byId("thesaurusTopics").store.addData({Id: "1", topics:"Kategorie eins"});
  dojo.widget.byId("thesaurusTopics").store.addData({Id: "2", topics:"Kategorie zwei"});
  dojo.widget.byId("thesaurusTopics").store.addData({Id: "3", topics:"Kategorie drei"});

  //  var tableId = dojo.widget.byId("thesaurusFreeTermsList").valueField;
  dojo.widget.byId("thesaurusFreeTermsList").store.clearData();
  dojo.widget.byId("thesaurusFreeTermsList").store.addData({Id: "1", freeTerms:"Freier Term eins"});
  dojo.widget.byId("thesaurusFreeTermsList").store.addData({Id: "2", freeTerms:"Freier Term zwei"});
  dojo.widget.byId("thesaurusFreeTermsList").store.addData({Id: "3", freeTerms:"Freier Term drei"});
*/

  // -- Links --
  nodeData.linksToObjectTable = udkDataProxy._getTableData("linksTo");
  nodeData.linksFromObjectTable = udkDataProxy._getTableData("linksFrom");


  dojo.debug("------ NODE DATA ------");
  dojo.debugShallow(nodeData);
  dojo.debug("------ NODE DATA END ------");
 
  // -- Check which object type was received and fill the appropriate fields --
  switch (nodeData.objectClass)
  {
    case '0':
      udkDataProxy._getObjectDataClass0(nodeData);
      break;
    case '1':
      udkDataProxy._getObjectDataClass1(nodeData);
      break;
    case '2':
      udkDataProxy._getObjectDataClass2(nodeData);
      break;
    case '3':
      udkDataProxy._getObjectDataClass3(nodeData);
      break;
    case '4':
      udkDataProxy._getObjectDataClass4(nodeData);
      break;
    case '5':
      udkDataProxy._getObjectDataClass5(nodeData);
      break;
    default:
      dojo.debug("Error in udkDataProxy._getObjectData - Object Class must be 0...5!");
      break;
  }  

  /* 
   * Select box values can't be set through the form(?).   
   * We set all values in the Widgets directly.
   */


//  dojo.widget.byId("contentFormObject").getValues(myObj);
//  dojo.widget.byId("headerFormAddress").getValues(myObj);
//  dojo.widget.byId("contentFormAddress").getValues(myObj);
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
udkDataProxy._updateTree = function(nodeData, oldUuid) {
	dojo.debug("_updateTree("+nodeData.uuid+", "+oldUuid+")");

	// If we change the uuid (= widgetId) of a node the treeNode has to be created again
	// because otherwise dojo doesn't 'register' the changed widgetId 
	// Currently a changed uuid is only possible if a new Node is updated.
	if (nodeData.uuid != oldUuid && oldUuid == "newNode") {
		var oldWidget = dojo.widget.byId(oldUuid);
		var parent = oldWidget.parent;

		// A new node is a leaf node. It's safe to destroy it
		oldWidget.destroy();
		var treeController = dojo.widget.byId("treeController");
		treeController.createChild(parent, "last", {
			contextMenu: 'contextMenu1',
			isFolder: false,
			nodeDocType: nodeData.nodeDocType,
			title: nodeData.objectName,
			dojoType: 'ingrid:TreeNode',
			nodeAppType: nodeData.nodeAppType,
			id: nodeData.uuid
		});
	} else {
		var node = dojo.widget.byId(oldUuid);
		if (node) {
			dojo.debug("nodeDocType: "+nodeData.nodeDocType);
			node.nodeDocType = nodeData.nodeDocType;	
			dojo.widget.byId("treeDocIcons").setnodeDocTypeClass(node);
			node.setTitle(nodeData.objectName);
			node.id = nodeData.uuid;	
		} else {
			dojo.debug("Error in _updateTree: TreeNode widget not found. ID: "+nodeData.uuid);
		}
	}
}


// Returns an array representing the data of the table with name 'tableName'
// The keys are stored in the fields named: 'Id' 
udkDataProxy._getTableData = function(tableName)
{
  return dojo.widget.byId(tableName).store.getData();
}

// Add Indices (Id values) to a passed list
udkDataProxy._addTableIndices = function(list) {
	for (var i = 0; i < list.length; ++i) {
		list[i].Id = i;
	}
	return list;
}

// Add object link labels to a passed list.
// This function iterates over all entries in the list and adds a value: 'linkLabel' to each node
// which is a href to the menuEventHandler 'selectNodeInTree' function
udkDataProxy._addObjectLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		list[i].linkLabel = "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";
	}
	return list;
}
