/*
 * This proxy is used to read from and write to the different gui elements.
 * Additional checks are performed to ensure that no data is lost in the process (e.g.
 * asking the user if unsaved changes should really be discarded)
 *
 * The proxy is called indirectly via the following topics (dojo.event.topic.publish(topic, message)):
 *   topic = '/loadRequest' - argument: {id: nodeUuid, appType: appType, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node
 *     appType  - 'A' for Address, 'O' for Object
 *     resultHandler - (optional) A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/createObjectRequest' - argument: {id: parentUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *	// TODO: Use a result Handler for save requests(?)
 *   topic = '/saveRequest' - argument:
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/publishObjectRequest' - argument: {resultHandler: deferred}
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
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
udkDataProxy.dirtyFlag = false;


// In currentUdk we cache the currently loaded udk in it's original representation.
// Changes are not tracked here! We need it to access static information that is not
// displayed in the gui (e.g. nodeUUID).
var currentUdk = {}; 

// This store holds the comments which are added/modified via the 'add comment' dialog.
var commentStore = {};


// TODO Move Dirty Flag handling to another file? 
dojo.addOnLoad(function()
{
    dojo.event.topic.subscribe("/loadRequest", udkDataProxy, "handleLoadRequest");
    dojo.event.topic.subscribe("/saveRequest", udkDataProxy, "handleSaveRequest");
    dojo.event.topic.subscribe("/publishObjectRequest", udkDataProxy, "handlePublishObjectRequest");
    dojo.event.topic.subscribe("/createObjectRequest", udkDataProxy, "handleCreateObjectRequest");
    dojo.event.topic.subscribe("/deleteRequest", udkDataProxy, "handleDeleteRequest");
    dojo.event.topic.subscribe("/deleteWorkingCopyRequest", udkDataProxy, "handleDeleteWorkingCopyRequest");
    dojo.event.topic.subscribe("/canCutObjectRequest", udkDataProxy, "handleCanCutObjectRequest");
	dojo.event.topic.subscribe("/canCopyObjectRequest", udkDataProxy, "handleCanCopyObjectRequest");
    dojo.event.topic.subscribe("/cutObjectRequest", udkDataProxy, "handleCutObjectRequest");
    dojo.event.topic.subscribe("/copyObjectRequest", udkDataProxy, "handleCopyObjectRequest");
    dojo.event.topic.subscribe("/getObjectPathRequest", udkDataProxy, "handleGetObjectPathRequest");

	// Set initial values
	dirtyFlag = false;
	commentStore = new dojo.collections.Store();	


	// Before a node is selected we ask the user if he wants to save unsaved changes.
	// Connection to the onClick function does not work for some odd reasons (?)
	// We connect to the processNode function instead.
	var treeListener = dojo.widget.byId("treeListener");
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
    dojo.event.connect(dojo.widget.byId("objectName"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("objectClass"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("objectOwner"), "onValueChanged", udkDataProxy, "setDirtyFlag");

    dojo.event.connect(dojo.widget.byId("generalShortDesc"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("generalDesc"), "onkeyup", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("generalAddress").store);

    dojo.event.connect(dojo.widget.byId("ref1DataSet"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1Coverage"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1Representation"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1VFormatTopology"), "onValueChanged", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1VFormatDetails").store);
    dojo.event.connect(dojo.widget.byId("ref1SpatialSystem"), "onValueChanged", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1Scale").store);
    dojo.event.connect(dojo.widget.byId("ref1AltAccuracy"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref1PosAccuracy"), "onkeyup", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1SymbolsText").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1KeysText").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1ServiceLink").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1BasisLink").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1DataBasisLink").store);
    dojo.event.connect(dojo.widget.byId("ref1Data"), "onkeyup", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref1ProcessLink").store);

    dojo.event.connect(dojo.widget.byId("ref2Author"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2Publisher"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedIn"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishLocation"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedInIssue"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedInPages"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedInYear"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedISBN"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2PublishedPublisher"), "onkeyup", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref2LocationLink").store);
    dojo.event.connect(dojo.widget.byId("ref2DocumentType"), "onValueChanged", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref2BaseDataLink").store);
    dojo.event.connect(dojo.widget.byId("ref2BibData"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref2Explanation"), "onkeyup", udkDataProxy, "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3ServiceType").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3ServiceVersion").store);
    dojo.event.connect(dojo.widget.byId("ref3SystemEnv"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("ref3History"), "onkeyup", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3BaseDataLink").store);
    dojo.event.connect(dojo.widget.byId("ref3Explanation"), "onkeyup", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref3Operation").store);

	_connectStoreWithDirtyFlag(dojo.widget.byId("ref4ParticipantsLink").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref4PMLink").store);

	_connectStoreWithDirtyFlag(dojo.widget.byId("ref5dbContent").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("ref5MethodLink").store);
    dojo.event.connect(dojo.widget.byId("ref5Explanation"), "onkeyup", udkDataProxy, "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefAdminUnit").store);
//	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefCoordsAdminUnit").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefLocation").store);
//	_connectStoreWithDirtyFlag(dojo.widget.byId("spatialRefCoordsLocation").store);
    dojo.event.connect(dojo.widget.byId("spatialRefAltMin"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefAltMax"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefAltMeasure"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefAltVDate"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("spatialRefExplanation"), "onkeyup", udkDataProxy, "setDirtyFlag");

    dojo.event.connect(dojo.widget.byId("timeRefType"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefDate1"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefDate2"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefStatus"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefPeriodicity"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefIntervalNum"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("timeRefIntervalUnit"), "onValueChanged", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("timeRefTable").store);
    dojo.event.connect(dojo.widget.byId("timeRefExplanation"), "onkeyup", udkDataProxy, "setDirtyFlag");

    dojo.event.connect(dojo.widget.byId("extraInfoLangMetaData"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("extraInfoLangData"), "onValueChanged", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("extraInfoPublishArea"), "onValueChanged", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("extraInfoXMLExportTable").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("extraInfoLegalBasicsTable").store);
    dojo.event.connect(dojo.widget.byId("extraInfoPurpose"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("extraInfoUse"), "onkeyup", udkDataProxy, "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("availabilityDataFormat").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("availabilityMediaOptions").store);
    dojo.event.connect(dojo.widget.byId("availabilityOrderInfo"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("availabilityNoteUse"), "onkeyup", udkDataProxy, "setDirtyFlag");
    dojo.event.connect(dojo.widget.byId("availabilityCosts"), "onkeyup", udkDataProxy, "setDirtyFlag");

	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusTerms").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusTopics").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusFreeTermsList").store);
    dojo.event.connect(dojo.widget.byId("thesaurusEnvExtRes"), "onValueChanged", udkDataProxy, "setDirtyFlag");
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusEnvTopics").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("thesaurusEnvCats").store);

	_connectStoreWithDirtyFlag(dojo.widget.byId("linksTo").store);
	_connectStoreWithDirtyFlag(dojo.widget.byId("linksFrom").store);

	dojo.event.connect(udkDataProxy, "_setData", udkDataProxy, "resetDirtyFlag");
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

udkDataProxy.setDirtyFlag = function()
{
	dirtyFlag = true;
}

udkDataProxy.resetDirtyFlag = function()
{
	dirtyFlag = false;
}

_connectStoreWithDirtyFlag = function(store)
{
	dojo.event.connect(store, "onSetData", udkDataProxy, "setDirtyFlag");
	dojo.event.connect(store, "onAddData", udkDataProxy, "setDirtyFlag");
	dojo.event.connect(store, "onAddDataRange", udkDataProxy, "setDirtyFlag");
	dojo.event.connect(store, "onRemoveData", udkDataProxy, "setDirtyFlag");
	dojo.event.connect(store, "onUpdateField", udkDataProxy, "setDirtyFlag");
	dojo.event.connect(store, "onSetData", udkDataProxy, "setDirtyFlag");
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
		dialog.showPage(message.get("dialog.saveChangesTitle"), "mdek_save_changes.html", 342, 130, true, {resultHandler: deferred});

		// If the user was editing a newly created node and he wants to discard the changes
		// delete the newly created node.
		if (currentUdk.uuid == "newNode" && nodeId != "newNode") {
			deferred.addCallback(function(arg) {
				if (arg == "DISCARD") {
					dojo.debug("Discarding the newly created node.");
					var newNode = dojo.widget.byId("newNode");
					newNode.destroy();
				}			
			});
		}
	} else {
		deferred.callback();
	}

	return deferred;
}


udkDataProxy.handleLoadRequest = function(msg)
{
	dojo.debug("About to be loaded: "+msg.id);

	// Don't process newNode load requests. We have this request because we
	// select new nodes in the tree after creating them
	if (msg.id == "newNode") {
		return;
	}

	var nodeId = msg.id;
	var nodeAppType = msg.appType;
	var resultHandler = msg.resultHandler;
	// TODO Check if we are in a state where it's safe to load data.
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function() {
		if (typeof(resultHandler) != "undefined") {
			resultHandler.errback(new Error("LOAD_CANCEL_ERROR"));		
		}
	}
	var loadCallback = function() {
		dojo.debug("udkDataProxy calling EntryService.getNodeData("+nodeId+", "+nodeAppType+")");
		// ---- DWR call to load the data ----
		EntryService.getNodeData(nodeId, nodeAppType, "false",
			{
				callback:function(res){
						if (res != null) {
							udkDataProxy._setData(res);
							udkDataProxy._updateTree(res);
							if (resultHandler)
								resultHandler.callback();
							resetRequiredFields();
						} else {
//							dojo.debug(resultHandler);
							if (typeof(resultHandler) != "undefined") {
								resultHandler.errback("Error loading object. The object with the specified id doesn't exist!");
							}
						}
					},
				timeout:10000,
				errorHandler:function(message) {dojo.debug("Error in js/udkDataProxy.js: Error while waiting for nodeData: " + message); },
				exceptionHandler:function(message) {dojo.debug("Exception in js/udkDataProxy.js: Error while waiting for nodeData: " + message); }				
			}
		);
	};

	deferred.addCallbacks(loadCallback, loadErrback);
}

udkDataProxy.handleCreateObjectRequest = function(msg)
{
	var nodeId = msg.id;
	if (msg.id == "objectRoot") {
		nodeId = null;
	}

	// TODO Check if we are in a state where it's safe to create a node?
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function() {msg.resultHandler.errback();}
	var loadCallback = function() {
		EntryService.createNewNode(nodeId,
			{
				callback: function(res){
						msg.resultHandler.callback(res);
						udkDataProxy._setData(res);
//						udkDataProxy._updateTree(res);
						udkDataProxy.setDirtyFlag();
					},
				timeout:10000,
				errorHandler:function(message) {msg.resultHandler.errback("Error in js/udkDataProxy.js: Error while creating a new node."); }
			}
		);	
	}
	deferred.addCallbacks(loadCallback, loadErrback);
}

udkDataProxy.handleSaveRequest = function(msg)
{
	/* TODO Check if we are in a state where it's safe to save data
	 *      If we are, read all the fields and send the collected data to
	 *      the EntryService. If not delay the call and bounce back the message (e.g. query user).
	 */
	if (!checkValidityOfInputElements()){
		if (msg && msg.resultHandler) {
			msg.resultHandler.errback(new Error("INPUT_INVALID_ERROR"));
		}
		return;
	}

	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();

	// ---- DWR call to store the data ----
	dojo.debug("udkDataProxy calling EntryService.saveNodeData("+nodeData.uuid+", true)");
	EntryService.saveNodeData(nodeData, "true",
		{
			callback: function(res){
				udkDataProxy.resetDirtyFlag();
				udkDataProxy._setData(res);
				udkDataProxy._updateTree(res, nodeData.uuid);
				udkDataProxy.onAfterSave();
				if (msg && msg.resultHandler) {
					msg.resultHandler.callback(res);
				}
			},
			timeout:10000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while saving nodeData: " + message);
				if (msg && msg.resultHandler) {
					msg.resultHandler.errback(message);
				}				
			}
		}
	);
}

udkDataProxy.handlePublishObjectRequest = function(msg) {
	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();
	
	// ---- DWR call to store the data ----
	dojo.debug("udkDataProxy calling EntryService.saveNodeData("+nodeData.uuid+", false)");
	EntryService.saveNodeData(nodeData, "false",
		{
			callback: function(res){
				udkDataProxy.resetDirtyFlag();
				udkDataProxy._setData(res);
				udkDataProxy._updateTree(res, nodeData.uuid);
				udkDataProxy.onAfterPublish();
				msg.resultHandler.callback(res);
			},
			timeout:10000,
			errorHandler:function(message) {
				msg.resultHandler.errback(message);
				dojo.debug("Error in js/udkDataProxy.js: Error while publishing nodeData: " + message);
			}
		}
	);
}

udkDataProxy.handleDeleteWorkingCopyRequest = function(msg) {
	dojo.debug("udkDataProxy calling EntryService.deleteObjectWorkingCopy("+msg.id+")");
	EntryService.deleteObjectWorkingCopy(msg.id, "false",
		{
			callback: function(res){
				if (res != null) {
					udkDataProxy.resetDirtyFlag();
					udkDataProxy._setData(res);
					udkDataProxy._updateTree(res, msg.id);
					udkDataProxy.onAfterSave();
				}
				msg.resultHandler.callback(res);
			},
			timeout:10000,
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
			timeout:10000,
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
			timeout:10000,
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
			timeout:10000,
			errorHandler:function(message) {
				alert("Error in js/udkDataProxy.js: Error while marking a node for a copy operation: " + message);
				msg.resultHandler.errback();
			}
		}
	);
}

udkDataProxy.handleCutObjectRequest = function(msg) {
	if(msg.dstId == "objectRoot") {
		msg.dstId = null;
	}
	dojo.debug("udkDataProxy calling EntryService.cutNode("+msg.srcId+", "+msg.dstId+")");	

	EntryService.moveNode(msg.srcId, msg.dstId,
		{
			callback: function(success){
				if (success) {
					msg.resultHandler.callback();
				} else {
					msg.resultHandler.errback("Error in js/udkDataProxy.js: Move node operation failed.");
				}
			},
			timeout:30000,
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
			callback: function(res){
				if (res != null) {
					msg.resultHandler.callback(res);
				} else {
					msg.resultHandler.errback("Node Copy operation failed.");
				}
			},
			timeout:30000,
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
				timeout:10000,
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
udkDataProxy.onAfterPublish = function() { dojo.debug("onAfterPublish()"); }

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
  var addressTable = nodeData.generalAddressTable;
  udkDataProxy._addTableIndices(addressTable);
  udkDataProxy._addIcons(addressTable);
  dojo.widget.byId("generalAddress").store.setData(addressTable);
  // Comments
  commentStore.setData(udkDataProxy._addTableIndices(udkDataProxy._addDisplayDates(nodeData.commentTable)));

  // -- Spatial --
  // The table containing entries from the sns is indexed by their topicID
  dojo.widget.byId("spatialRefAdminUnit").store.setData(udkDataProxy._addTableIndices(nodeData.spatialRefAdminUnitTable));
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
  dojo.widget.byId("timeRefTable").store.setData(udkDataProxy._addTableIndices(nodeData.timeRefTable));

  // -- Extra Info --
  dojo.widget.byId("extraInfoLangMetaData").setValue(nodeData.extraInfoLangMetaData);
  dojo.widget.byId("extraInfoLangData").setValue(nodeData.extraInfoLangData);
  dojo.widget.byId("extraInfoPublishArea").setValue(nodeData.extraInfoPublishArea);
  dojo.widget.byId("extraInfoPurpose").setValue(nodeData.extraInfoPurpose);
  dojo.widget.byId("extraInfoUse").setValue(nodeData.extraInfoUse);
  dojo.widget.byId("extraInfoXMLExportTable").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.extraInfoXMLExportTable)));
  dojo.widget.byId("extraInfoLegalBasicsTable").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.extraInfoLegalBasicsTable)));

  // -- Availability --
  dojo.widget.byId("availabilityOrderInfo").setValue(nodeData.availabilityOrderInfo);
  dojo.widget.byId("availabilityNoteUse").setValue(nodeData.availabilityNoteUse);
  dojo.widget.byId("availabilityCosts").setValue(nodeData.availabilityCosts);
  dojo.widget.byId("availabilityDataFormat").store.setData(udkDataProxy._addTableIndices(nodeData.availabilityDataFormatTable));
  dojo.widget.byId("availabilityMediaOptions").store.setData(udkDataProxy._addTableIndices(nodeData.availabilityMediaOptionsTable));


  // -- Thesaurus --
  dojo.widget.byId("thesaurusTerms").store.setData(nodeData.thesaurusTermsTable);
  dojo.widget.byId("thesaurusFreeTermsList").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.thesaurusFreeTermsTable)));
  dojo.widget.byId("thesaurusTopics").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.thesaurusTopicsList)));
  dojo.widget.byId("thesaurusEnvTopics").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.thesaurusEnvTopicsList)));
  dojo.widget.byId("thesaurusEnvCats").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.thesaurusEnvCatsList)));
  dojo.widget.byId("thesaurusEnvExtRes").setValue(nodeData.thesaurusEnvExtRes);

  // -- Links --
  var objLinkTable = nodeData.linksToObjectTable;
  var urlLinkTable = nodeData.linksToUrlTable;
  var linkTable = objLinkTable.concat(urlLinkTable);

  udkDataProxy._addTableIndices(linkTable);
  udkDataProxy._addObjectLinkLabels(linkTable);
  udkDataProxy._addUrlLinkLabels(linkTable);
  udkDataProxy._addIcons(linkTable);
  dojo.widget.byId("linksTo").store.setData(linkTable);

  linkTable = nodeData.linksFromObjectTable;
  udkDataProxy._addTableIndices(linkTable);
  udkDataProxy._addObjectLinkLabels(linkTable);  
  udkDataProxy._addIcons(linkTable);
  dojo.widget.byId("linksFrom").store.setData(linkTable);

  // -- Check which object type was received and fill the appropriate fields --
  // Fall through to clear all object classes
  switch (nodeData.objectClass)
  {
    case 0:
      udkDataProxy._setObjectDataClass0(nodeData);
//      break;
    case 1:
      udkDataProxy._setObjectDataClass1(nodeData);
//      break;
    case 2:
      udkDataProxy._setObjectDataClass2(nodeData);
//      break;
    case 3:
      udkDataProxy._setObjectDataClass3(nodeData);
//      break;
    case 4:
      udkDataProxy._setObjectDataClass4(nodeData);
//      break;
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

udkDataProxy._setObjectDataClass0 = function(nodeData) {}

udkDataProxy._setObjectDataClass1 = function(nodeData) {
	dojo.widget.byId("ref1DataSet").setValue(nodeData.ref1DataSet);
	dojo.widget.byId("ref1Coverage").setValue(nodeData.ref1Coverage);
	dojo.widget.byId("ref1VFormatTopology").setValue(nodeData.ref1VFormatTopology);

	// The spatial system table is a combobox that allows free entries but also entries associated with IDs
	// If the reference system ID == -1 then we receive a free entry, otherwise we have to resolve the id
	if (nodeData.ref1SpatialSystemId == -1) {
		dojo.widget.byId("ref1SpatialSystem").setValue(nodeData.ref1SpatialSystem);
	} else {
		var dispVal = ref1SpatialSystemDP.getDisplayValueForValue(nodeData.ref1SpatialSystemId);
		if (dispVal != null) {
			dojo.widget.byId("ref1SpatialSystem").setValue(dispVal);
		} else {
			dojo.widget.byId("ref1SpatialSystem").setValue("");
		}	
	}

	dojo.widget.byId("ref1AltAccuracy").setValue(nodeData.ref1AltAccuracy);
	dojo.widget.byId("ref1PosAccuracy").setValue(nodeData.ref1PosAccuracy);
	dojo.widget.byId("ref1BasisText").setValue(nodeData.ref1BasisText);
	dojo.widget.byId("ref1DataBasisText").setValue(nodeData.ref1DataBasisText);
	dojo.widget.byId("ref1ProcessText").setValue(nodeData.ref1ProcessText);

	dojo.widget.byId("ref1Representation").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref1Representation)));
	dojo.widget.byId("ref1Data").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref1Data)));

	dojo.widget.byId("ref1VFormatDetails").store.setData(udkDataProxy._addTableIndices(nodeData.ref1VFormatDetails));
	dojo.widget.byId("ref1Scale").store.setData(udkDataProxy._addTableIndices(nodeData.ref1Scale));
	dojo.widget.byId("ref1SymbolsText").store.setData(udkDataProxy._addTableIndices(nodeData.ref1SymbolsText));
	dojo.widget.byId("ref1KeysText").store.setData(udkDataProxy._addTableIndices(nodeData.ref1KeysText));
}

udkDataProxy._setObjectDataClass2 = function(nodeData) {
	dojo.widget.byId("ref2Author").setValue(nodeData.ref2Author);
	dojo.widget.byId("ref2Publisher").setValue(nodeData.ref2Publisher);
	dojo.widget.byId("ref2PublishedIn").setValue(nodeData.ref2PublishedIn);
	dojo.widget.byId("ref2PublishLocation").setValue(nodeData.ref2PublishLocation);
	dojo.widget.byId("ref2PublishedInIssue").setValue(nodeData.ref2PublishedInIssue);
	dojo.widget.byId("ref2PublishedInPages").setValue(nodeData.ref2PublishedInPages);
	dojo.widget.byId("ref2PublishedInYear").setValue(nodeData.ref2PublishedInYear);
	dojo.widget.byId("ref2PublishedISBN").setValue(nodeData.ref2PublishedISBN);
	dojo.widget.byId("ref2PublishedPublisher").setValue(nodeData.ref2PublishedPublisher);
	dojo.widget.byId("ref2LocationText").setValue(nodeData.ref2LocationText);
	dojo.widget.byId("ref2DocumentType").setValue(nodeData.ref2DocumentType);
	dojo.widget.byId("ref2BaseDataText").setValue(nodeData.ref2BaseDataText);
	dojo.widget.byId("ref2BibData").setValue(nodeData.ref2BibData);
	dojo.widget.byId("ref2Explanation").setValue(nodeData.ref2Explanation);
}

udkDataProxy._setObjectDataClass3 = function(nodeData) {
	dojo.widget.byId("ref3ServiceType").setValue(nodeData.ref3ServiceType);
	dojo.widget.byId("ref3SystemEnv").setValue(nodeData.ref3SystemEnv);
	dojo.widget.byId("ref3History").setValue(nodeData.ref3History);
	dojo.widget.byId("ref3BaseDataText").setValue(nodeData.ref3BaseDataText);
	dojo.widget.byId("ref3Explanation").setValue(nodeData.ref3Explanation);

	dojo.debug("Setting service version to: "+udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref3ServiceVersion)));
	dojo.widget.byId("ref3ServiceVersion").store.setData(udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref3ServiceVersion)));

	// Prepare the operation table for display.
	// Add table indices to the main obj and paramList
	// Add table indices and convert to tableData: platform, addressList and dependencies
	if (nodeData.ref3Operation) {
		for (var i = 0; i < nodeData.ref3Operation.length; ++i) {
			udkDataProxy._addTableIndices(nodeData.ref3Operation[i].paramList);
			nodeData.ref3Operation[i].platform = udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref3Operation[i].platform));
			nodeData.ref3Operation[i].addressList = udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref3Operation[i].addressList));
			nodeData.ref3Operation[i].dependencies = udkDataProxy._addTableIndices(udkDataProxy._listToTableData(nodeData.ref3Operation[i].dependencies));		
		}
	}	
	dojo.widget.byId("ref3Operation").store.setData(udkDataProxy._addTableIndices(nodeData.ref3Operation));
}

udkDataProxy._setObjectDataClass4 = function(nodeData) {
	dojo.widget.byId("ref4ParticipantsText").setValue(nodeData.ref4ParticipantsText);
	dojo.widget.byId("ref4PMText").setValue(nodeData.ref4PMText);
	dojo.widget.byId("ref4Explanation").setValue(nodeData.ref4Explanation);
}

udkDataProxy._setObjectDataClass5 = function(nodeData) {
	dojo.widget.byId("ref5MethodText").setValue(nodeData.ref5MethodText);
	dojo.widget.byId("ref5Explanation").setValue(nodeData.ref5Explanation);

	dojo.widget.byId("ref5dbContent").store.setData(udkDataProxy._addTableIndices(nodeData.ref5dbContent));
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
  var parentUuid = dojo.widget.byId(currentUdk.uuid).parent.id;
  if (parentUuid != "objectRoot") {
  	nodeData.parentUuid = parentUuid;
  }

  // ------------- Catalog Data -------------
  nodeData.catalogUuid = currentUdk.catalogUuid;
  nodeData.catalogName = currentUdk.catalogName;


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
  // Comments
  nodeData.commentTable = commentStore.getData();

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
  nodeData.timeRefTable = udkDataProxy._getTableData("timeRefTable");

  // -- Extra Info --
  nodeData.extraInfoLangMetaData = dojo.widget.byId("extraInfoLangMetaData").getValue();
  nodeData.extraInfoLangData = dojo.widget.byId("extraInfoLangData").getValue();
  nodeData.extraInfoPublishArea = dojo.widget.byId("extraInfoPublishArea").getValue();
  nodeData.extraInfoPurpose = dojo.widget.byId("extraInfoPurpose").getValue();
  nodeData.extraInfoUse = dojo.widget.byId("extraInfoUse").getValue();
	
  nodeData.extraInfoXMLExportTable = udkDataProxy._tableDataToList(udkDataProxy._getTableData("extraInfoXMLExportTable"));
  nodeData.extraInfoLegalBasicsTable = udkDataProxy._tableDataToList(udkDataProxy._getTableData("extraInfoLegalBasicsTable"));

  // -- Availability --
  nodeData.availabilityOrderInfo = dojo.widget.byId("availabilityOrderInfo").getValue();
  nodeData.availabilityNoteUse = dojo.widget.byId("availabilityNoteUse").getValue();
  nodeData.availabilityCosts = dojo.widget.byId("availabilityCosts").getValue();
  nodeData.availabilityDataFormatTable = udkDataProxy._getTableData("availabilityDataFormat");
  nodeData.availabilityMediaOptionsTable = udkDataProxy._getTableData("availabilityMediaOptions");

  // -- Thesaurus --
  nodeData.thesaurusTermsTable = udkDataProxy._getTableData("thesaurusTerms");
  nodeData.thesaurusFreeTermsTable = udkDataProxy._tableDataToList(udkDataProxy._getTableData("thesaurusFreeTermsList"));
  nodeData.thesaurusTopicsList = udkDataProxy._tableDataToList(udkDataProxy._getTableData("thesaurusTopics"));
  nodeData.thesaurusEnvTopicsList = udkDataProxy._tableDataToList(udkDataProxy._getTableData("thesaurusEnvTopics"));
  nodeData.thesaurusEnvCatsList = udkDataProxy._tableDataToList(udkDataProxy._getTableData("thesaurusEnvCats"));
  nodeData.thesaurusEnvExtRes = dojo.widget.byId("thesaurusEnvExtRes").checked;


  // -- Links --
  var linksToTable = udkDataProxy._getTableData("linksTo");
  var objLinks = [];
  var urlLinks = [];
  dojo.lang.forEach(linksToTable, function(link) {
	if (link.url) {
  		urlLinks.push(link);
  	} else {
  		objLinks.push(link);  	
  	}
  });

  nodeData.linksToObjectTable = objLinks;
  nodeData.linksToUrlTable = urlLinks;
  nodeData.linksFromObjectTable = udkDataProxy._getTableData("linksFrom");


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

  dojo.debug("------ NODE DATA ------");
  dojo.debugShallow(nodeData);
  dojo.debug("------ NODE DATA END ------");
}

udkDataProxy._getObjectDataClass0 = function(nodeData) {};

udkDataProxy._getObjectDataClass1 = function(nodeData) {
	nodeData.ref1DataSet = dojo.widget.byId("ref1DataSet").getValue();
	nodeData.ref1Coverage = dojo.widget.byId("ref1Coverage").getValue();
	nodeData.ref1VFormatTopology = dojo.widget.byId("ref1VFormatTopology").getValue();

	// The spatial system table is a combobox that allows free entries but also entries associated with IDs
	// If we have a free entry the reference system ID = -1
	nodeData.ref1SpatialSystem = dojo.widget.byId("ref1SpatialSystem").getValue();

	var val = ref1SpatialSystemDP.getValueForDisplayValue(nodeData.ref1SpatialSystem);
	if (val != null) {
		nodeData.ref1SpatialSystemId = val;
	} else {
		nodeData.ref1SpatialSystemId = -1;
	}	

	nodeData.ref1AltAccuracy = dojo.widget.byId("ref1AltAccuracy").getValue();
	nodeData.ref1PosAccuracy = dojo.widget.byId("ref1PosAccuracy").getValue();
	nodeData.ref1BasisText = dojo.widget.byId("ref1BasisText").getValue();
	nodeData.ref1DataBasisText = dojo.widget.byId("ref1DataBasisText").getValue();
	nodeData.ref1ProcessText = dojo.widget.byId("ref1ProcessText").getValue();


	nodeData.ref1Representation = udkDataProxy._tableDataToList(udkDataProxy._getTableData("ref1Representation"));
	nodeData.ref1Data = udkDataProxy._tableDataToList(udkDataProxy._getTableData("ref1Data"));

	nodeData.ref1VFormatDetails = udkDataProxy._getTableData("ref1VFormatDetails");
	nodeData.ref1Scale = udkDataProxy._getTableData("ref1Scale");
	nodeData.ref1SymbolsText = udkDataProxy._getTableData("ref1SymbolsText");
	nodeData.ref1KeysText = udkDataProxy._getTableData("ref1KeysText");
};

udkDataProxy._getObjectDataClass2 = function(nodeData) {
	nodeData.ref2Author = dojo.widget.byId("ref2Author").getValue();
	nodeData.ref2Publisher = dojo.widget.byId("ref2Publisher").getValue();
	nodeData.ref2PublishedIn = dojo.widget.byId("ref2PublishedIn").getValue();
	nodeData.ref2PublishLocation = dojo.widget.byId("ref2PublishLocation").getValue();
	nodeData.ref2PublishedInIssue = dojo.widget.byId("ref2PublishedInIssue").getValue();
	nodeData.ref2PublishedInPages = dojo.widget.byId("ref2PublishedInPages").getValue();
	nodeData.ref2PublishedInYear = dojo.widget.byId("ref2PublishedInYear").getValue();
	nodeData.ref2PublishedISBN = dojo.widget.byId("ref2PublishedISBN").getValue();
	nodeData.ref2PublishedPublisher = dojo.widget.byId("ref2PublishedPublisher").getValue();
	nodeData.ref2LocationText = dojo.widget.byId("ref2LocationText").getValue();
	nodeData.ref2DocumentType = dojo.widget.byId("ref2DocumentType").getValue();
	nodeData.ref2BaseDataText = dojo.widget.byId("ref2BaseDataText").getValue();
	nodeData.ref2BibData = dojo.widget.byId("ref2BibData").getValue();
	nodeData.ref2Explanation = dojo.widget.byId("ref2Explanation").getValue();
};


udkDataProxy._getObjectDataClass3 = function(nodeData) {
	nodeData.ref3ServiceType = dojo.widget.byId("ref3ServiceType").getValue();
	nodeData.ref3SystemEnv = dojo.widget.byId("ref3SystemEnv").getValue();
	nodeData.ref3History = dojo.widget.byId("ref3History").getValue();
	nodeData.ref3BaseDataText = dojo.widget.byId("ref3BaseDataText").getValue();
	nodeData.ref3Explanation = dojo.widget.byId("ref3Explanation").getValue();

	dojo.debug(udkDataProxy._getTableData("ref3ServiceVersion"));
	nodeData.ref3ServiceVersion = udkDataProxy._tableDataToList(udkDataProxy._getTableData("ref3ServiceVersion"));

	// Convert the containing operation tables to lists
	// Add table indices and convert to tableData: platform, addressList and dependencies
	var op = udkDataProxy._getTableData("ref3Operation");
	if (op) {
		for (var i = 0; i < op.length; ++i) {
//			op[i].paramList = udkDataProxy._tableDataToList(op[i].paramList);
			op[i].platform = udkDataProxy._tableDataToList(op[i].platform);
			op[i].addressList = udkDataProxy._tableDataToList(op[i].addressList);
			op[i].dependencies = udkDataProxy._tableDataToList(op[i].dependencies);
		}
	}	

	nodeData.ref3Operation = op;
};

udkDataProxy._getObjectDataClass4 = function(nodeData) {
	nodeData.ref4ParticipantsText = dojo.widget.byId("ref4ParticipantsText").getValue();
	nodeData.ref4PMText = dojo.widget.byId("ref4PMText").getValue();
	nodeData.ref4Explanation = dojo.widget.byId("ref4Explanation").getValue();
};

udkDataProxy._getObjectDataClass5 = function(nodeData) {
	nodeData.ref5MethodText = dojo.widget.byId("ref5MethodText").getValue();
	nodeData.ref5Explanation = dojo.widget.byId("ref5Explanation").getValue();

	nodeData.ref5dbContent = udkDataProxy._getTableData("ref5dbContent");
};


/*******************************************
 *            Helper functions             *
 *******************************************/

// Looks for the node widget with uuid = nodeData.uuid and updates the
// tree data (label, type, etc.) according to the given nodeData
udkDataProxy._updateTree = function(nodeData, oldUuid) {
	dojo.debug("_updateTree("+nodeData.uuid+", "+oldUuid+")");
	if (typeof(oldUuid) == "undefined") {
		oldUuid = nodeData.uuid;
	}

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

// Creates table data from a list of values.
// ["a", "b", "c"] -> [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}]
udkDataProxy._listToTableData = function(list, identifier) {
	var resultList = [];
	if (typeof(identifier) == "undefined")
		identifier = "title";

	dojo.lang.forEach(list, function(item){
		var x = {};
		x[identifier] = item;
		resultList.push(x);
	});
	return resultList;
}


//Creates a list from table data
// [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}] -> ["a", "b", "c"] 
udkDataProxy._tableDataToList = function(tableData, identifier) {
	var resultList = [];
	if (typeof(identifier) == "undefined") {
		identifier = "title";
	}

	for (var i = 0; i < tableData.length; ++i) {
		resultList.push(tableData[i][identifier]);
	}

	return resultList;
}

// Add Indices (Id values) to a passed list
udkDataProxy._addTableIndices = function(list) {
	if (list) {
		for (var i = 0; i < list.length; ++i) {
			list[i].Id = i;
		}
		return list;
	} else {
		return [];
	}
}

udkDataProxy._addDisplayDates = function(list) {
	if (list) {
		for (var i = 0; i < list.length; ++i) {
			list[i].displayDate = list[i].date.toLocaleString();
		}
		return list;
	} else {
		return [];
	}
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

udkDataProxy._addUrlLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		if (list[i].url) {
			list[i].linkLabel = "<a href='"+list[i].url+"' target=\"_blank\" title='"+list[i].name+"'>"+list[i].name+"</a>";
		}
	}
	return list;
}

udkDataProxy._addIcons = function(list) {
	for (var i = 0; i < list.length; ++i) {
		if (typeof(list[i].objectClass) != "undefined") {
			list[i].icon = "<img src='img/UDK/udk_class"+list[i].objectClass+".gif' width=\"16\" height=\"16\" alt=\"Object\" />";
		} else if (typeof(list[i].addressClass) != "undefined") {
			list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";		
		} else if (typeof(list[i].url) != "undefined") {
			list[i].icon = "<img src='img/UDK/url.gif' width=\"16\" height=\"16\" alt=\"Url\" />";		
		} else {
			list[i].icon = "noIcon";
		}
	}
	return list;
}
