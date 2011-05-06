/*
 * This proxy is used to read from and write to the different gui elements.
 * Additional checks are performed to ensure that no data is lost in the process (e.g.
 * asking the user if unsaved changes should really be discarded)
 *
 * The proxy is called indirectly via the following topics (dojo.publish(topic, message)):
 *   topic = '/loadRequest' - argument: {id: nodeUuid, appType: appType, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node
 *     appType  - 'A' for Address, 'O' for Object
 *     resultHandler - (optional) A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/createObjectRequest' - argument: {id: parentUuid, resultHandler: deferred}
 *     parentUuid - The Uuid of the objects parent
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/createAddressRequest' - argument: {id: parentUuid, addressClass: int, resultHandler: deferred}
 *     parentUuid - The Uuid of the addresses parent
 *     addressClass - The class of the newly created address
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/saveRequest' - argument:
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *	   forcePublicationCondition - Tell the backend to adjust the publication condition of subnodes
 *
 *   topic = '/publishObjectRequest' - argument: {resultHandler: deferred}
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/publishAddressRequest' - argument: {resultHandler: deferred}
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/deleteRequest' - argument: {id: nodeUuid, forceDelete: bool, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be deleted
 *     forceDelete - The object is deleted even if it is referenced by other objects
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/deleteWorkingCopyRequest' - argument: {id: nodeUuid, forceDelete: bool, resultHandler: deferred}
 *     nodeUuid - The Uuid of the working copy node which should be deleted
 *     forceDelete - The object is deleted even if it is referenced by other objects
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/canCutObjectRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be marked for a cut operation
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/canCutAddressRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the address which should be marked for a cut operation
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/canCopyObjectRequest' - argument: {id: nodeUuid, copyTree: boolean resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be marked for a copied operation
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/canCopyAddressRequest' - argument: {id: nodeUuid, copyTree: boolean resultHandler: deferred}
 *     nodeUuid - The Uuid of the address which should be marked for a copied operation
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/cutObjectRequest' - argument: {srcId: srcUuid, dstId: dstUuid, resultHandler: deferred}
 *     srcId - The Uuid of the node which should be cut
 *     dstId - The Uuid of the target node where the srcNode should be attached
 *	   forcePublicationCondition - Tell the backend to adjust the publication condition of subnodes
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/cutAddressRequest' - argument: {srcId: srcUuid, dstId: dstUuid, resultHandler: deferred}
 *     srcId - The Uuid of the address which should be cut
 *     dstId - The Uuid of the target address where the srcNode should be attached
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/copyObjectRequest' - argument: {srcId: srcUuid, dstId: dstUuid, copyTree: boolean, resultHandler: deferred}
 *     srcId - The Uuid of the node which should be copied
 *     dstId - The Uuid of the target node where the srcNode should be attached
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/copyAddressRequest' - argument: {srcId: srcUuid, dstId: dstUuid, copyTree: boolean, resultHandler: deferred}
 *     srcId - The Uuid of the address which should be copied
 *     dstId - The Uuid of the target address where the srcNode should be attached
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *
 *   topic = '/getObjectPathRequest' - argument: {id: nodeUuid, resultHandler: deferred, ignoreDirtyFlag: bool}
 *     nodeUuid - The Uuid of the target Node
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *     ignoreDirtyFlag - boolean value indicating if the dirty flag should be ignored
 *
 *   topic = '/getAddressPathRequest' - argument: {id: nodeUuid, resultHandler: deferred, ignoreDirtyFlag: bool}
 *     nodeUuid - The Uuid of the target Address
 *     resultHandler - A dojo.Deferred which is called when the request has been processed
 *     ignoreDirtyFlag - boolean value indicating if the dirty flag should be ignored
 */


var udkDataProxy = {};

// This flag is set when any value in the gui changes
udkDataProxy.dirtyFlag = false;


// In currentUdk we cache the currently loaded udk in it's original representation.
// Changes are not tracked here! We need it to access static information that is not
// displayed in the gui (e.g. nodeUUID).
var currentUdk = {}; 

// This store holds the comments which are added/modified via the 'add comment' dialog.
var commentStore = [];

// This object holds general information about the catalog (see CatalogBean for content).
var catalogData = {};

// This object holds the current user (See de.ingrid.mdek.beans.security.User for content)
var currentUser = {};

//This object holds the current user permissions (See de.ingrid.mdek.beans.security.Permission for content)
var currentUserPermissions = [];

// This object holds the groups of the current user (See de.ingrid.mdek.beans.security.Group for content)
var currentGroups = [];

// This list holds all additionalField widgets (if any exist) for easy access
// The list is initialized in init.js/initAdditionalFields
var additionalFieldWidgets = [];

// TODO Move Dirty Flag handling to another file? 
dojo.addOnLoad(function()
{
	// Common requests
    dojo.subscribe("/loadRequest", udkDataProxy, "handleLoadRequest");
    dojo.subscribe("/saveRequest", udkDataProxy, "handleSaveRequest");
    dojo.subscribe("/deleteRequest", udkDataProxy, "handleDeleteRequest");
    dojo.subscribe("/deleteWorkingCopyRequest", udkDataProxy, "handleDeleteWorkingCopyRequest");
	
	dojo.subscribe("/selectNode", igeEvents, "handleSelectNode");
    

	// Object requests
    dojo.subscribe("/publishObjectRequest", udkDataProxy, "handlePublishObjectRequest");
    dojo.subscribe("/createObjectRequest", udkDataProxy, "handleCreateObjectRequest");
    dojo.subscribe("/canCutObjectRequest", udkDataProxy, "handleCanCutObjectRequest");
	dojo.subscribe("/canCopyObjectRequest", udkDataProxy, "handleCanCopyObjectRequest");
    dojo.subscribe("/cutObjectRequest", udkDataProxy, "handleCutObjectRequest");
    dojo.subscribe("/copyObjectRequest", udkDataProxy, "handleCopyObjectRequest");
    dojo.subscribe("/getObjectPathRequest", udkDataProxy, "handleGetObjectPathRequest");

	// Address requests
    dojo.subscribe("/publishAddressRequest", udkDataProxy, "handlePublishAddressRequest");
    dojo.subscribe("/createAddressRequest", udkDataProxy, "handleCreateAddressRequest");
    dojo.subscribe("/canCutAddressRequest", udkDataProxy, "handleCanCutAddressRequest");
	dojo.subscribe("/canCopyAddressRequest", udkDataProxy, "handleCanCopyAddressRequest");
    dojo.subscribe("/cutAddressRequest", udkDataProxy, "handleCutAddressRequest");
    dojo.subscribe("/copyAddressRequest", udkDataProxy, "handleCopyAddressRequest");
    dojo.subscribe("/getAddressPathRequest", udkDataProxy, "handleGetAddressPathRequest");

	// QA requests
    dojo.subscribe("/forwardObjectToQARequest", udkDataProxy, "handleForwardObjectToQARequest");
    dojo.subscribe("/forwardAddressToQARequest", udkDataProxy, "handleForwardAddressToQARequest");
    dojo.subscribe("/forwardObjectToAuthorRequest", udkDataProxy, "handleForwardObjectToAuthorRequest");
    dojo.subscribe("/forwardAddressToAuthorRequest", udkDataProxy, "handleForwardAddressToAuthorRequest");


	// Set initial values
	udkDataProxy.dirtyFlag = false;
  }
);

// since onChange-event is called via a setTimeout
// the setting of data is in an unpredictable order and time
// while setting data is going on we do not set the dirty flag
// after 100ms without any new onChange-event, we allow to set
// the dirty flag again
udkDataProxy._onSetDirtyFlag = null;
udkDataProxy.enableSetDirtyFlag = function() {
    udkDataProxy.isSettingData = false;
}

udkDataProxy.setDirtyFlag = function()
{
    if (udkDataProxy.isSettingData) {
        // first clear an already set timeout
        if (udkDataProxy._onSetDirtyFlag) {
            clearTimeout(udkDataProxy._onSetDirtyFlag);
        }
        udkDataProxy._onSetDirtyFlag = setTimeout(udkDataProxy.enableSetDirtyFlag, 100);
    } else {
        udkDataProxy.setDirtyFlagNow();
    }
}

udkDataProxy.setDirtyFlagNow = function()
{
    udkDataProxy.dirtyFlag = true;
}

udkDataProxy.resetDirtyFlag = function()
{
	udkDataProxy.dirtyFlag = false;
}


function _connectWidgetWithDirtyFlag(widgetId) {
	// We don't need to connect the 'Link' tables. If those tables are changed, the 'master' table (linksTo)
	// will be changed and set the dirty flag 
	var startPos = widgetId.length - "Link".length;
    if (startPos >= 0 && widgetId.lastIndexOf("Link") == startPos) {
	//if (dojo.string.endsWith(widgetId, "Link")) {
		return;
	}

	var widget = dijit.byId(widgetId);
	if (widget instanceof dijit.form.NumberTextBox) {
    	dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof dijit.form.CheckBox) {
    	dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof dijit.form.DateTextBox) {
	    dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof dijit.form.ValidationTextBox) {
    	dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof dijit.form.SimpleTextarea) {
    	dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof dijit.form.Select) {
	    dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof dijit.form.ComboBox) {
	    dojo.connect(widget, "onChange", udkDataProxy, "setDirtyFlag");
	} else if (widget instanceof ingrid.dijit.CustomGrid) {
        dojo.connect(widget, "onDataChanged", udkDataProxy.setDirtyFlag);
	} else {
		console.debug("Can't connect widget "+widgetId+" with dirty flag. Method not implemented for "+widget);
	}
}

function _connectStoreWithDirtyFlag(store) {
	dojo.connect(store, "onDelete", udkDataProxy, "setDirtyFlag");
	dojo.connect(store, "onNew", udkDataProxy, "setDirtyFlag");
	dojo.connect(store, "onSet", udkDataProxy, "setDirtyFlag");
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
	console.debug("Check for unsaved changes called.");
	var deferred = new dojo.Deferred();

	// If the current user does not have write permission on the current obj/adr, don't display a dialog,
	// clear the dirty flag and return as normal
	if (currentUdk.writePermission == false && currentUdk.uuid != "newNode") {
		this.resetDirtyFlag();
		deferred.callback();
		return deferred;
	}


	if (this.dirtyFlag == true) {
//		dialog.showPage(message.get("dialog.saveChangesTitle"), "mdek_save_changes.html", 350, 145, true, {resultHandler: deferred});
		var displayText = "";
		if (currentUdk.nodeAppType == "O")
			displayText = message.get("dialog.object.saveChangesHint");
		else
			displayText = message.get("dialog.address.saveChangesHint");

		dialog.show(message.get("dialog.saveChangesTitle"), displayText, dialog.INFO, [
        	{ caption: message.get("general.cancel"), action: function(){
                UtilTree.selectNode("dataTree", currentUdk.uuid);
				deferred.errback(new Error("LOAD_CANCELLED"));
			}},
        	{ caption: message.get("general.no"),     action: function() {
        		udkDataProxy.resetDirtyFlag();
				deferred.callback("DISCARD");
        	}},
        	{ caption: message.get("general.yes"),    action: function() {
				var def = new dojo.Deferred();
				def.addCallback(function(){ deferred.callback("SAVE"); }); 
				def.addErrback(function(errMsg){ deferred.errback(errMsg); });

				dojo.publish("/saveRequest", [{resultHandler:def}]);
        	}}
		]);

		// If the user was editing a newly created node and he wants to discard the changes
		// delete the newly created node.
		if (currentUdk.uuid == "newNode" && nodeId != "newNode") {
			deferred.addCallback(function(arg) {
				if (arg == "DISCARD") {
					console.debug("Discarding the newly created node.");
					var newNode = dijit.byId("newNode");
					//newNode.destroy();
					if (newNode != null) {
                        // a revert creates problems here when adding new address, discarding it and 
                        // creating it again and then do a save!
						//dijit.byId("dataTree").model.store.revert();
                        dijit.byId("dataTree").model.store.deleteItem(newNode.item);
                        dijit.byId("dataTree").model.store.save();
					} else {
						console.debug("NewNode shouldn't be null! There must have been an error before!");
					}
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
	console.debug("About to be loaded: "+msg.id);

	// Don't process newNode and objectRoot load requests.
	if (msg.id == "newNode" || msg.id == "objectRoot" || msg.id == "addressRoot" || msg.id == "addressFreeRoot") {
		dojo.publish("/selectNode", [{node: dijit.byId(msg.id).item}]);
		msg.resultHandler.callback();
		return;
	}
    
	var nodeId = msg.id;
	var nodeAppType = msg.appType;
	var resultHandler = msg.resultHandler;
	// TODO Check if we are in a state where it's safe to load data.
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function(err) {
		if (typeof(resultHandler) != "undefined") {
			resultHandler.errback(err);
		}
	}
	var loadCallback = function() {
		console.debug("udkDataProxy calling ObjectService.getNodeData("+nodeId+", "+nodeAppType+")");
		// ---- DWR call to load the data ----
		if (nodeAppType == "O") {
			ObjectService.getNodeData(nodeId, nodeAppType, "false",
				{
					preHook: UtilDWR.enterLoadingState,
//					postHook: UtilDWR.exitLoadingState,
					callback:function(res){
						console.debug("in callback");
							if (res != null) {
								console.debug("set data");
								var def = udkDataProxy._setData(res);
								def.addCallback(function(arg) {
									console.debug("update Tree");
									udkDataProxy._updateTree(res);
									resetRequiredFields();
									console.debug("call resultHandler");
									if (resultHandler)
										resultHandler.callback();
									console.debug("reset dirty flag after 0.1s");
									// since onChange events are fired asynchronously 
									// we have to wait a bit to reset the dirty flag 
									setTimeout(udkDataProxy.resetDirtyFlag, 100);
									console.debug("on after load");
									udkDataProxy.onAfterLoad();
									console.debug("exit loading state");
									UtilDWR.exitLoadingState();
								});
							} else {
	//							console.debug(resultHandler);
								if (typeof(resultHandler) != "undefined") {
									resultHandler.errback("Error loading object. The object with the specified id doesn't exist!");
								}
							}
							return res;
						},
//					timeout:20000,
					errorHandler:function(message, err) {
						UtilDWR.exitLoadingState();
						console.debug("Error in js/udkDataProxy.js: Error while waiting for nodeData: " + message);
						console.debug(err);
						resultHandler.errback(new Error(message));
					}
				});
		} else if (nodeAppType == "A") {
			AddressService.getAddressData(nodeId, "false",
				{
					preHook: UtilDWR.enterLoadingState,
//					postHook: UtilDWR.exitLoadingState,
					callback:function(res){
							if (res != null) {
								var def = udkDataProxy._setData(res);
								def.addCallback(function(arg){
									udkDataProxy._updateTree(res);
									resetRequiredFields();
									if (resultHandler)
										resultHandler.callback();
									// since onChange events are fired asynchronously 
									// we have to wait a bit to reset the dirty flag 
									setTimeout(udkDataProxy.resetDirtyFlag, 100);
									udkDataProxy.onAfterLoad();
									UtilDWR.exitLoadingState();
								});
							} else {
								if (typeof(resultHandler) != "undefined") {
									resultHandler.errback("Error loading Address. The Address with the specified id doesn't exist!");
								}
							}
							return res;
						},
//					timeout:20000,
					errorHandler:function(message, err) {
						UtilDWR.exitLoadingState();
						console.debug("Error in js/udkDataProxy.js: Error while waiting for addressData: " + message);
						resultHandler.errback(new Error(message));
					}
				});
		}
	};

	deferred.addCallbacks(loadCallback, loadErrback);
}

udkDataProxy.handleCreateObjectRequest = function(msg)
{
	console.debug("create Object Request");
	var nodeId = msg.id+"";
	if (msg.id == "objectRoot") {
		nodeId = null;
	}

	// TODO Check if we are in a state where it's safe to create a node?
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function(err) {msg.resultHandler.errback(err);}
	console.debug("createNode under id: " + nodeId);
	var loadCallback = function() {
		ObjectService.createNewNode(nodeId,
			{
				preHook: UtilDWR.enterLoadingState,
				postHook: UtilDWR.exitLoadingState,
				callback: function(res){
						msg.resultHandler.callback(res);
						console.debug("set data");
						var def = udkDataProxy._setData(res);
						console.debug("set data ... finished");
                        // we must set dirty flag here!
						def.addCallback(udkDataProxy.setDirtyFlagNow);
					},
//				timeout:10000,
				errorHandler:function(message, err) {
					UtilDWR.exitLoadingState();
//					msg.resultHandler.errback("Error in js/udkDataProxy.js: Error while creating a new node.");
					console.debug("Error in js/udkDataProxy.js: Error while creating a new node.");
					msg.resultHandler.errback(message);
				}
			}
		);	
	}
	deferred.addCallbacks(loadCallback, loadErrback);
}

udkDataProxy.handleCreateAddressRequest = function(msg)
{
	console.debug("udkDataProxy.handleCreateAddressRequest()");
	
	var nodeId = msg.id+"";
	if (msg.id == "addressRoot" || msg.id == "addressFreeRoot") {
		nodeId = null;
	}

	// TODO Check if we are in a state where it's safe to create a node?
	//      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
	var deferred = udkDataProxy.checkForUnsavedChanges();
	var loadErrback = function(err) {msg.resultHandler.errback(err);}
	var loadCallback = function() {
		AddressService.createNewAddress(nodeId,
			{
				preHook: UtilDWR.enterLoadingState,
				postHook: UtilDWR.exitLoadingState,
				callback: function(res){
						res.addressClass = msg.addressClass;
						if (res.addressClass == 0) { res.nodeDocType = "Institution_B"; }
						else if (res.addressClass == 1) { res.nodeDocType = "InstitutionUnit_B"; }
						else if (res.addressClass == 2) { res.nodeDocType = "InstitutionPerson_B"; }
						else if (res.addressClass == 3) { res.nodeDocType = "PersonAddress_B"; }

						msg.resultHandler.callback(res);
						var def = udkDataProxy._setData(res);
						def.addCallback(udkDataProxy.setDirtyFlagNow);
					},
//				timeout:10000,
				errorHandler:function(message, err) {
					UtilDWR.exitLoadingState();
//					msg.resultHandler.errback("Error in js/udkDataProxy.js: Error while creating a new address.");
					console.debug("Error in js/udkDataProxy.js: Error while creating a new address.");
					msg.resultHandler.errback(new Error(message));
				}
			}
		);	
	}
	deferred.addCallbacks(loadCallback, loadErrback);
}


udkDataProxy.handleSaveRequest = function(msg)
{
	if (currentUdk.nodeAppType == "O")
		udkDataProxy._handleSaveObjectRequest(msg);
	else if (currentUdk.nodeAppType == "A")
		udkDataProxy._handleSaveAddressRequest(msg);
}

udkDataProxy._handleSaveAddressRequest = function(msg) {
	// Address validity check	
	var valid = checkValidityOfAddressInputElements();
	if (valid != "VALID"){
		if (msg && msg.resultHandler) {
			msg.resultHandler.errback(new Error(valid));
		}
		return;
	}

	// Construct an MdekAddressBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred obj for the main save operation. The passed resulthandler is called with the appropriate result
	var onSaveDef = new dojo.Deferred();
	onSaveDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
        def.addCallback(function() {
    		udkDataProxy._updateTree(res, nodeData.uuid);
            setTimeout(udkDataProxy.resetDirtyFlag,100);
    		udkDataProxy.onAfterSave();
    		msg.resultHandler.callback(res);
        });	
	});
	onSaveDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling AddressService.saveAddressData("+nodeData.uuid+", true)");
	AddressService.saveAddressData(nodeData, "true",
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onSaveDef.callback(res); },
//			timeout:10000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while saving addressData:");
				onSaveDef.errback(err);
			}
		}
	);
}


udkDataProxy._handleSaveObjectRequest = function(msg) {
	var valid = checkValidityOfInputElements();
	if (valid != "VALID"){
		if (msg && msg.resultHandler) {
			msg.resultHandler.errback(new Error(valid));
		}
		return;
	}

	// Determine if the publication condition should be adjusted (forced) for subnodes
	var forcePubCond = false;
	if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
		forcePubCond = msg.forcePublicationCondition;
	}

	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred obj for the main save operation. The passed resulthandler is called with the appropriate result
	var onSaveDef = new dojo.Deferred();
	onSaveDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
        def.addCallback(function() {
    		udkDataProxy._updateTree(res, nodeData.uuid);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
            console.debug("flag resetted");
    		udkDataProxy.onAfterSave();
    		msg.resultHandler.callback(res);
        });	
	});
	onSaveDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling ObjectService.saveNodeData("+nodeData.uuid+", true, "+forcePubCond+")");
	ObjectService.saveNodeData(nodeData, "true", forcePubCond,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onSaveDef.callback(res); },
//			timeout:10000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				// Check for the publication condition error
				// TODO: A normal save operation shouldn't trigger it?
				if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
					var onForceSaveDef = new dojo.Deferred();
					
					// If the user wants to save the object anyway, set force save and start another request
					onForceSaveDef.addCallback(function() {
						msg.forcePublicationCondition = true;
						dojo.publish("/saveRequest", [msg]);
					});
					// If the user cancelled the operation notify the result handler
					onForceSaveDef.addErrback(onSaveDef.errback);

					// Display the 'publication condition' dialog with the attached resultHandler
//					dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"SAVE", resultHandler:onForceSaveDef});
					var displayText = message.get("operation.hint.publicationConditionSaveHint");
					dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [
                    	{ caption: message.get("general.cancel"),  action: function() { onForceSaveDef.errback(); } },
                    	{ caption: message.get("general.save"), action: function() { onForceSaveDef.callback(); } }
					]);

				} else {
					console.debug("Error in js/udkDataProxy.js: Error while saving nodeData:");
					onSaveDef.errback(err);
				}
			}
		}
	);
}


udkDataProxy.handlePublishObjectRequest = function(msg) {
	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();

	var forcePubCond = false;
	if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
		forcePubCond = msg.forcePublicationCondition;
	}

	// Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
	var onPublishDef = new dojo.Deferred();
	onPublishDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
        def.addCallback(function() {
    		udkDataProxy._updateTree(res, nodeData.uuid);
    		udkDataProxy.onAfterSave();
    		msg.resultHandler.callback(res);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
        });
	});
	onPublishDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling ObjectService.saveNodeData("+nodeData.uuid+", false, "+forcePubCond+")");
	ObjectService.saveNodeData(nodeData, "false", forcePubCond,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onPublishDef.callback(res); },
//			timeout:10000,
			errorHandler:function(err, ex) {
				UtilDWR.exitLoadingState();
				// Check for the publication condition error
				if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
					var onForcePublishDef = new dojo.Deferred();
					
					// If the user wants to publish the object anyway, set force publish and start another request
					onForcePublishDef.addCallback(function() {
						msg.forcePublicationCondition = true;
						dojo.publish("/publishObjectRequest", [msg]);
					});
					// If the user cancelled the operation notify the result handler
					onForcePublishDef.addErrback(onPublishDef.errback);

					// Display the 'publication condition' dialog with the attached resultHandler
//					dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"SAVE", resultHandler:onForcePublishDef});
					var displayText = message.get("operation.hint.publicationConditionSaveHint");
					dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [
                    	{ caption: message.get("general.cancel"),  action: function() { onForcePublishDef.errback(); } },
                    	{ caption: message.get("general.save"), action: function() { onForcePublishDef.callback(); } }
					], 382, 220);

				} else {
					console.debug("Error in js/udkDataProxy.js: Error while publishing nodeData:");
					onPublishDef.errback(ex);
				}
			}
		}
	);
}

udkDataProxy.handlePublishAddressRequest = function(msg) {
	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
	var onPublishDef = new dojo.Deferred();
	onPublishDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
		def.addCallback(function() {
            udkDataProxy._updateTree(res, nodeData.uuid);
		    udkDataProxy.onAfterSave();
		    msg.resultHandler.callback(res);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
        });	
	});
	onPublishDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling AddressService.saveAddressData("+nodeData.uuid+", false)");
	AddressService.saveAddressData(nodeData, "false",
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onPublishDef.callback(res); },
//			timeout:10000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while publishing address:");
				onPublishDef.errback(err);
			}
		}
	);
}

udkDataProxy.handleDeleteWorkingCopyRequest = function(msg) {
	if (msg.id == "newNode") {
		msg.resultHandler.callback();
		return;
	}

	var nodeAppType = dijit.byId(msg.id).item.nodeAppType[0];
	if (nodeAppType == "O")
		udkDataProxy._handleDeleteObjectWorkingCopyRequest(msg);
	else if (nodeAppType == "A")
		udkDataProxy._handleDeleteAddressWorkingCopyRequest(msg);
}

udkDataProxy._handleDeleteAddressWorkingCopyRequest = function(msg) {
	var title = dijit.byId(msg.id).title;

	var forceDelete = false;
	if (msg && typeof(msg.forceDelete) != "undefined") {
		forceDelete = msg.forceDelete;
	}

	console.debug("udkDataProxy calling AddressService.deleteAddressWorkingCopy("+msg.id+", "+forceDelete+")");
	AddressService.deleteAddressWorkingCopy(msg.id, forceDelete, "false",
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res){
				if (res != null) {
					var def = udkDataProxy._setData(res);
                    def.addCallback(function() {
    					udkDataProxy._updateTree(res, msg.id);
    					udkDataProxy.onAfterSave();
                        setTimeout(udkDataProxy.resetDirtyFlag, 100);
                    });
				}
				msg.resultHandler.callback(res);
			},
//			timeout:10000,
			errorHandler:function(errMsg, err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while deleting address working copy: "+errMsg);
				// Wrap the dwr error in a javscript Error object
				//var e = new Error(errMsg);
				//e.message = err;
				msg.resultHandler.errback(err);
			}
		}
	);
}

udkDataProxy._handleDeleteObjectWorkingCopyRequest = function(msg) {
	console.debug("udkDataProxy calling ObjectService.deleteObjectWorkingCopy("+msg.id+")");
	var title = dijit.byId(msg.id).title;

	var forceDelete = false;
	if (msg && typeof(msg.forceDelete) != "undefined") {
		forceDelete = msg.forceDelete;
	}

	ObjectService.deleteObjectWorkingCopy(msg.id, forceDelete, "false",
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res){
				if (res != null) {
					var def = udkDataProxy._setData(res);
                    def.addCallback(function() {
    					udkDataProxy._updateTree(res, msg.id);
    					udkDataProxy.onAfterSave();
                        setTimeout(udkDataProxy.resetDirtyFlag, 100);
                    });
				}
				msg.resultHandler.callback(res);
			},
//			timeout:10000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				if (err.indexOf("ENTITY_REFERENCED_BY_OBJ") != -1) {
					var onForceDeleteDef = new dojo.Deferred();

					// If the user wants to delete the object anyway, set force delete and start another request
					onForceDeleteDef.addCallback(function() {
						msg.forceDelete = true;
						udkDataProxy.handleDeleteRequest(msg);
						//dojo.publish("/cutObjectRequest", msg);
					});
					// If the user cancelled the operation notify the result handler
					onForceDeleteDef.addErrback(msg.resultHandler.errback);

					// Display the 'force delete' dialog with the attached resultHandler
//					dialog.showPage(message.get("general.warning"), "mdek_forceDelete_dialog.html", 382, 220, true, {nodeAppType:"O", nodeTitle:title, resultHandler:onForceDeleteDef});
					var displayText = dojo.string.substitute(message.get("operation.hint.forceDeleteObjectHint"), [title]);

					dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [
			        	{ caption: message.get("general.no"),  action: function() { onForceDeleteDef.errback(); } },
			        	{ caption: message.get("general.yes"), action: function() { onForceDeleteDef.callback(); } }
					]);
				} else {
					console.debug("Error in js/udkDataProxy.js: Error while deleting object: "+err);
					msg.resultHandler.errback(err);
				}
			}
		}
	);
}


udkDataProxy.handleDeleteRequest = function(msg) {
	var nodeAppType = dijit.byId(msg.id).item.nodeAppType[0];
	var title = dijit.byId(msg.id).item.title[0];

	var forceDelete = false;
	if (msg && msg.forceDelete && typeof(msg.forceDelete) != "undefined") {
		forceDelete = msg.forceDelete;
	}

	if (nodeAppType == "O") {
		console.debug("udkDataProxy calling ObjectService.deleteNode("+msg.id+", "+forceDelete+")");
		ObjectService.deleteNode(msg.id, forceDelete, "false", {
				preHook: UtilDWR.enterLoadingState,
				postHook: UtilDWR.exitLoadingState,
				callback: function(res){msg.resultHandler.callback(res);},
//				timeout:10000,
				errorHandler:function(err) {
					UtilDWR.exitLoadingState();
					if (err.indexOf("ENTITY_REFERENCED_BY_OBJ") != -1) {
						var onForceDeleteDef = new dojo.Deferred();
	
						// If the user wants to delete the object anyway, set force delete and start another request
						onForceDeleteDef.addCallback(function() {
							msg.forceDelete = true;
							udkDataProxy.handleDeleteRequest(msg);
							//dojo.publish("/cutObjectRequest", msg);
						});
						// If the user cancelled the operation notify the result handler
						onForceDeleteDef.addErrback(function() { msg.resultHandler.errback("OPERATION_CANCELLED"); });

						// Display the 'force delete' dialog with the attached resultHandler
//						dialog.showPage(message.get("general.warning"), "mdek_forceDelete_dialog.html", 382, 220, true, {nodeAppType:"O", nodeTitle:title, resultHandler:onForceDeleteDef});
						var displayText = dojo.string.substitute(message.get("operation.hint.forceDeleteObjectHint"), [title]);
	
						dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [
				        	{ caption: message.get("general.no"),  action: function() { onForceDeleteDef.errback(); } },
				        	{ caption: message.get("general.yes"), action: function() { onForceDeleteDef.callback(); } }
						]);

					} else {
						console.debug("Error in js/udkDataProxy.js: Error while deleting object: "+err);
						msg.resultHandler.errback(err);
					}
				}
			}
		);
	
	} else if (nodeAppType == "A") {
		console.debug("udkDataProxy calling AddressService.deleteAddress("+msg.id+")");
		AddressService.deleteAddress(msg.id, forceDelete, "false",
			{
				preHook: UtilDWR.enterLoadingState,
				postHook: UtilDWR.exitLoadingState,
				callback: function(res){msg.resultHandler.callback(res);},
//				timeout:10000,
				errorHandler:function(errMsg, err) {
					UtilDWR.exitLoadingState();
//					console.debug("Error in js/udkDataProxy.js: Error while deleting address: "+err);
					// Wrap the dwr error in a javscript Error object
					//var e = new Error(errMsg);
					//e.message = err;
					msg.resultHandler.errback(err);
				}
			}
		);
	}
}

udkDataProxy.handleCanCutObjectRequest = function(msg) {
	console.debug("udkDataProxy calling ObjectService.canCutNode("+msg.id+")");	

	ObjectService.canCutObject(msg.id,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res){msg.resultHandler.callback();},
//			timeout:10000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while marking a node for a cut operation: " + err);
                displayErrorMessage(err);
				msg.resultHandler.errback(err);
			}
		}
	);
}

udkDataProxy.handleCanCutAddressRequest = function(msg) {
	console.debug("udkDataProxy calling AddressService.canCutAddress("+msg.id+")");	

	AddressService.canCutAddress(msg.id,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res){msg.resultHandler.callback();},
//			timeout:10000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while marking an address for a cut operation: " + err);
				msg.resultHandler.errback(err);
			}
		}
	);
}


udkDataProxy.handleCanCopyObjectRequest = function(msg) {
	console.debug("udkDataProxy calling ObjectService.canCopyNode("+msg.id+", "+msg.copyTree+")");	

	ObjectService.canCopyObject(msg.id,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res){msg.resultHandler.callback();},
//			timeout:30000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while marking a node for a copy operation: " + err);
				msg.resultHandler.errback(err);
			}
		}
	);
}

udkDataProxy.handleCanCopyAddressRequest = function(msg) {
	console.debug("udkDataProxy calling AddressService.canCopyAddress("+msg.id+", "+msg.copyTree+")");	

	AddressService.canCopyAddress(msg.id,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res){msg.resultHandler.callback();},
//			timeout:30000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while marking an address for a copy operation: " + err);
				msg.resultHandler.errback(err);
			}
		}
	);
}

udkDataProxy.handleCutObjectRequest = function(msg) {
	if(msg.dstId == "objectRoot") {
		msg.dstId = null;
	}

	var forcePubCond = false;
	if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
		forcePubCond = msg.forcePublicationCondition;
	}

	console.debug("udkDataProxy calling ObjectService.moveNode("+msg.srcId+", "+msg.parentUuid+", "+msg.dstId+", "+forcePubCond+")");	
	ObjectService.moveNode(msg.srcId, msg.parentUuid, msg.dstId, forcePubCond,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { msg.resultHandler.callback(res); },
//			timeout:30000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				// Check for the publication condition error
				if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
					var onForceMoveDef = new dojo.Deferred();

					// If the user wants to publish the object anyway, set force publish and start another request
					onForceMoveDef.addCallback(function() {
						msg.forcePublicationCondition = true;
						dojo.publish("/cutObjectRequest", [msg]);
					});
					// If the user cancelled the operation notify the result handler
					onForceMoveDef.addErrback(msg.resultHandler.errback);

					// Display the 'publication condition' dialog with the attached resultHandler
//					dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"MOVE", resultHandler:onForceMoveDef});
					var displayText = message.get("operation.hint.publicationConditionMoveHint");
					dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [
                    	{ caption: message.get("general.cancel"),  action: function() { onForceMoveDef.errback(); } },
                    	{ caption: message.get("general.move"),    action: function() { onForceMoveDef.callback(); } }
					], 382, 220);

				} else {
					console.debug("Error in js/udkDataProxy.js: Error while moving nodeData:");
					msg.resultHandler.errback(err);
				}
			}
		}
	);
}

udkDataProxy.handleCutAddressRequest = function(msg) {
	var srcId = msg.srcId;
	var dstId = msg.dstId;
	var parentUuid = msg.parentUuid;
	var moveToFreeAddress = false;

	if (dstId == "addressRoot") {
		dstId = null;
	} else if (dstId == "addressFreeRoot") {
		dstId = null;
		moveToFreeAddress = true;
	}

	console.debug("udkDataProxy calling AddressService.moveAddress("+srcId+", "+parentUuid+", "+dstId+", "+moveToFreeAddress+")");	
	AddressService.moveAddress(srcId, parentUuid, dstId, moveToFreeAddress,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { msg.resultHandler.callback(res); },
//			timeout:30000,
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while moving address:");
				msg.resultHandler.errback(err);
			}
		}
	);
}

udkDataProxy.handleCopyObjectRequest = function(msg) {
	console.debug("udkDataProxy calling ObjectService.copyNode("+msg.srcId+", "+msg.dstId+", "+msg.copyTree+")");	

	var srcId = msg.srcId;
	var dstId = msg.dstId;

	if (dstId == "objectRoot") {
		dstId = null;
	}

	var onCopyDef = new dojo.Deferred();
	onCopyDef.addCallback(function(res) {
		// Copy operation was successful. Pass the copied node to the result handler
		msg.resultHandler.callback(res);
	});
	onCopyDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});

	ObjectService.copyNode(srcId, dstId, msg.copyTree,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onCopyDef.callback(res); },
			timeout:3000,	// Wait three seconds for the call to finish and display the 'please wait' dialog afterwards 
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				if (err == "Timeout") {
					var onCopyOpFinishedDef = new dojo.Deferred();
					// TODO we need to return some information about the copied node!
					onCopyOpFinishedDef.addCallback(function (res) {
						if (res == "JOB_CANCELLED") {
							onCopyDef.callback(null);
						} else {
							onCopyDef.callback(res);
						}
					});
					onCopyOpFinishedDef.addErrback(function (err) {onCopyDef.errback(err);});
					dialog.showPage(message.get("general.hint"), "dialogs/mdek_waitForJob_dialog.jsp", 350, 155, true, {resultHandler:onCopyOpFinishedDef});
				} else {
					console.debug("Error in js/udkDataProxy.js: Error while copying nodes: " + err);
					onCopyDef.errback(err);
				}
			}
		}
	);
}

udkDataProxy.handleCopyAddressRequest = function(msg) {
	var srcId = msg.srcId;
	var dstId = msg.dstId;
	var copyToFreeAddress = false;

	if (dstId == "addressRoot") {
		dstId = null;
	} else if (dstId == "addressFreeRoot") {
		dstId = null;
		copyToFreeAddress = true;
		msg.copyTree = false;
	}

	var onCopyDef = new dojo.Deferred();
	onCopyDef.addCallback(function(res) {
		// Copy operation was successful. Pass the copied node to the result handler
		msg.resultHandler.callback(res);
	});
	onCopyDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});

	console.debug("udkDataProxy calling AddressService.copyAddress("+msg.srcId+", "+msg.dstId+", "+msg.copyTree+", "+copyToFreeAddress+")");	
	AddressService.copyAddress(srcId, dstId, msg.copyTree, copyToFreeAddress,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onCopyDef.callback(res); },
			timeout:3000,	// Wait three seconds for the call to finish and display the 'please wait' dialog afterwards 
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				if (err == "Timeout") {
					var onCopyOpFinishedDef = new dojo.Deferred();
					// TODO we need to return some information about the copied node!
					onCopyOpFinishedDef.addCallback(function (res) {
						if (res == "JOB_CANCELLED") {
							onCopyDef.callback(null);
						} else {
							onCopyDef.callback(res);
						}
					});
					onCopyOpFinishedDef.addErrback(function (err) {onCopyDef.errback(err);});
					dialog.showPage(message.get("general.hint"), "dialogs/mdek_waitForJob_dialog.jsp", 350, 155, true, {resultHandler:onCopyOpFinishedDef});
				} else {
					console.debug("Error in js/udkDataProxy.js: Error while copying addresses: " + err);
					onCopyDef.errback(err);
				}
			}
		}
	);
}

udkDataProxy.handleForwardObjectToQARequest = function(msg) {
	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
	var onForwardDef = new dojo.Deferred();
	onForwardDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
		def.addCallback(function() {
            udkDataProxy._updateTree(res, nodeData.uuid);
		    udkDataProxy.onAfterSave();
		    msg.resultHandler.callback(res);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
        });
	});
	onForwardDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling ObjectService.assignObjectToQA("+nodeData.uuid+")");
	ObjectService.assignObjectToQA(nodeData,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onForwardDef.callback(res); },
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while assigning node to QA: "+err);
				onForwardDef.errback(err);
			}
		}
	);
}

udkDataProxy.handleForwardAddressToQARequest = function(msg) {
	// Construct an MdekAddressBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred adr for the main publish operation. The passed resulthandler is called with the appropriate result
	var onPublishDef = new dojo.Deferred();
	onPublishDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
        def.addCallback(function() {
    		udkDataProxy._updateTree(res, nodeData.uuid);
    		udkDataProxy.onAfterSave();
    		msg.resultHandler.callback(res);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
        });
	});
	onPublishDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling AddressService.assignAddressToQA("+nodeData.uuid+")");
	AddressService.assignAddressToQA(nodeData,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onPublishDef.callback(res); },
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while assigning address to QA: "+err);
				onPublishDef.errback(err);
			}
		}
	);
}


udkDataProxy.handleForwardObjectToAuthorRequest = function(msg) {
	// Construct an MdekDataBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
	var onForwardDef = new dojo.Deferred();
	onForwardDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
        def.addCallback(function() {
    		udkDataProxy._updateTree(res, nodeData.uuid);
    		udkDataProxy.onAfterSave();
    		msg.resultHandler.callback(res);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
        });
	});
	onForwardDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling ObjectService.reassignObjectToAuthor("+nodeData.uuid+")");
	ObjectService.reassignObjectToAuthor(nodeData,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onForwardDef.callback(res); },
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while reassigning node to Author: "+err);
				onForwardDef.errback(err);
			}
		}
	);
}

udkDataProxy.handleForwardAddressToAuthorRequest = function(msg) {
	// Construct an MdekAddressBean from the available data
	var nodeData = udkDataProxy._getData();

	// Deferred adr for the main publish operation. The passed resulthandler is called with the appropriate result
	var onPublishDef = new dojo.Deferred();
	onPublishDef.addCallback(function(res) {
		var def = udkDataProxy._setData(res);
        def.addCallback(function() {
    		udkDataProxy._updateTree(res, nodeData.uuid);
    		udkDataProxy.onAfterSave();
    		msg.resultHandler.callback(res);
            setTimeout(udkDataProxy.resetDirtyFlag, 100);
        });	
	});
	onPublishDef.addErrback(function(err) {
		msg.resultHandler.errback(err);
	});


	// ---- DWR call to store the data ----
	console.debug("udkDataProxy calling AddressService.reassignAddressToAuthor("+nodeData.uuid+")");
	AddressService.reassignAddressToAuthor(nodeData,
		{
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(res) { onPublishDef.callback(res); },
			errorHandler:function(err) {
				UtilDWR.exitLoadingState();
				console.debug("Error in js/udkDataProxy.js: Error while reassigning address to Author: "+err);
				onPublishDef.errback(err);
			}
		}
	);
}


udkDataProxy.handleGetObjectPathRequest = function(msg) {
	var loadErrback = function() {msg.resultHandler.errback();}
	var loadCallback = function() {
		console.debug("udkDataProxy calling ObjectService.getPathToObject("+msg.id+")");	
		ObjectService.getPathToObject(msg.id, {
				//preHook: UtilDWR.enterLoadingState,
				//postHook: UtilDWR.exitLoadingState,
				callback: function(res){msg.resultHandler.callback(res);},
//				timeout:10000,
				errorHandler:function(message) {
					//UtilDWR.exitLoadingState();
					console.debug("Error in js/udkDataProxy.js: Error while getting path to node: " + message);
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

udkDataProxy.handleGetAddressPathRequest = function(msg) {
	var loadErrback = function() {msg.resultHandler.errback();}
	var loadCallback = function() {
		console.debug("udkDataProxy calling AddressService.getPathToAddress("+msg.id+")");	
		AddressService.getPathToAddress(msg.id, {
				//preHook: UtilDWR.enterLoadingState,
				//postHook: UtilDWR.exitLoadingState,
				callback: function(res){msg.resultHandler.callback(res);},
//				timeout:10000,
				errorHandler:function(message) {
					//UtilDWR.exitLoadingState();
					console.debug("Error in js/udkDataProxy.js: Error while getting path to address: " + message);
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
udkDataProxy.onAfterSave = function() {  }
udkDataProxy.onAfterPublish = function() {  }
udkDataProxy.onAfterLoad = function() { }

udkDataProxy.isSettingData = false;

udkDataProxy._setData = function(nodeData)
{
    // set a flag so that no dirty flag is set!
    udkDataProxy.isSettingData = true;
    
	currentUdk = nodeData;

	// -- We check if we received an Address or Object and call the corresponding function --
	switch (nodeData.nodeAppType.toUpperCase())
	{
	case 'A':
		var def = ingridAddressLayout.create();
		def.addCallback(dojo.partial(udkDataProxy._initResponsibleUserAddressList,nodeData));
		def.addCallback(udkDataProxy._setAddressData);
		break;
	case 'O':
	    var def = ingridObjectLayout.create();
		def.addCallback(dojo.partial(udkDataProxy._initResponsibleUserObjectList,nodeData));
		def.addCallback(udkDataProxy._setObjectData);
		break;
	default:
		console.debug("Error in udkDataProxy._setData - Node Type must be \'A\' or \'O\'!");
		break;
	}  

	def.addCallback(udkDataProxy.resetDirtyFlag);

	return def;
}

udkDataProxy._setAddressData = function(nodeData)
{
	// Set the address type list values depending on the parent class
	var parentClass = nodeData.parentClass;
	if (parentClass == null) {
		if (nodeData.addressClass == 3)
			parentClass = -2;
		else 
			parentClass = -1;
	}

	// ------------------ Header ------------------
	dijit.byId("addressTitle").attr("value", UtilAddress.createAddressTitle(nodeData), true);
	dijit.byId("addressType").attr("value", UtilAddress.getAddressType(nodeData.addressClass), true); // call onChange when changing this value!
	dijit.byId("addressOwner").attr("value", nodeData.addressOwner, true);

	var workStateStr = message.get("general.workState."+nodeData.workState);
	dojo.byId("addressWorkState").innerHTML = (nodeData.isMarkedDeleted ? workStateStr+"<br>("+message.get("general.state.markedDeleted")+")" : workStateStr);

	dojo.byId("addressCreationTime").innerHTML = nodeData.creationTime;
	dojo.byId("addressModificationTime").innerHTML = nodeData.modificationTime;
	
	if (nodeData.lastEditor != null && UtilAddress.hasValidTitle(nodeData.lastEditor)) {
		dojo.byId("addressLastEditor").innerHTML = UtilAddress.createAddressTitle(nodeData.lastEditor);
	} else {
		dojo.byId("addressLastEditor").innerHTML = message.get("general.unknown");
	}

	if (nodeData.writePermission == true) {
		dojo.byId("permissionAdrLock").style.display = "none";
	} else {
		dojo.byId("permissionAdrLock").style.display = "block";
	}

	// ------------------ Address and Function ------------------
	dijit.byId("addressStreet").attr("value", nodeData.street, true);
	dijit.byId("addressCountry").attr("value", nodeData.countryCode == -1 ? null : nodeData.countryCode , true);
	dijit.byId("addressZipCode").attr("value", nodeData.postalCode, true);
	dijit.byId("addressCity").attr("value", nodeData.city, true);
	dijit.byId("addressPOBox").attr("value", nodeData.pobox, true);
	dijit.byId("addressZipPOBox").attr("value", nodeData.poboxPostalCode, true);
	dijit.byId("addressNotes").attr("value", nodeData.addressDescription, true);
	dijit.byId("addressTasks").attr("value", nodeData.task, true);
	UtilStore.updateWriteStore("addressCom", nodeData.communication, false);

	// -- Thesaurus --
	
	UtilList.addSNSTopicLabels(nodeData.thesaurusTermsTable);
	UtilStore.updateWriteStore("thesaurusTermsAddress", nodeData.thesaurusTermsTable);

	// -- Links --
	var unpubLinkTable = nodeData.linksFromObjectTable;
	var pubLinkTable = nodeData.linksFromPublishedObjectTable;
	dojo.forEach(pubLinkTable, function(link) { link.pubOnly = true; } );
	// AOR change
	var linkTable = pubLinkTable.concat(unpubLinkTable);

	// Initialize the object address reference table with the links received from the backend
	// TODO Modify to correct value when it's implemented in the backend
	var numReferences = nodeData.totalNumReferences || 0;
	UtilAddress.initObjectAddressReferenceTable(linkTable, numReferences);


	// Comments
	var commentData = UtilList.addDisplayDates(nodeData.commentTable);
	dojo.forEach(commentData, function(data, i) {
		commentData[i].title = UtilAddress.createAddressTitle(data.user);
	});
	
    commentStore = commentData;

/*
// Don't display all 'institutions', only the first one that is found (http://jira.media-style.com/browse/INGRIDII-130)
	var institution = "";
	dojo.forEach(nodeData.parentInstitutions, function(item) {
		if (item.addressClass == 0) {
			institution += item.organisation+"\n";
		} else if (item.addressClass == 1) {
			institution += "\t"+item.organisation+"\n";
		}
	});
	institution = dojo.string.trim(institution);
*/

	var institution = "";
	for (var i = nodeData.parentInstitutions.length-1; i >= 0; --i) {
		if (nodeData.parentInstitutions[i].addressClass == 0) {
			// Only display the first institution we encounter and break
			institution = nodeData.parentInstitutions[i].organisation+"\n"+institution;
			break;

		} else if (nodeData.parentInstitutions[i].addressClass == 1) {
			institution = "\t"+nodeData.parentInstitutions[i].organisation+"\n"+institution;
		}
	}
	institution = dojo.trim(institution);

//	var addressFields = ["headerAddressType0Institution", "headerAddressType0Unit", "headerAddressType1Institution", "headerAddressType1Unit",
	var addressFields = ["headerAddressType0Unit", "headerAddressType1Institution", "headerAddressType1Unit",
		"headerAddressType2Institution", "headerAddressType2Lastname", "headerAddressType2Firstname", "headerAddressType2Style",
		"headerAddressType2Title", "headerAddressType3Lastname", "headerAddressType3Firstname", "headerAddressType3Style",
		"headerAddressType3Title", "headerAddressType3Institution"];

	dojo.forEach(addressFields, function(field){ dijit.byId(field).attr("value", "", true); });

	// ------------------ Class specific content ------------------
	switch(nodeData.addressClass) {
		case 0:
//			dijit.byId("headerAddressType0Institution").attr("value", institution);
			dijit.byId("headerAddressType0Unit").attr("value", nodeData.organisation, true);
			break;
		case 1:
			dijit.byId("headerAddressType1Institution").attr("value", institution, true);
			dijit.byId("headerAddressType1Unit").attr("value", nodeData.organisation, true);
			break;
		case 2:
			dijit.byId("headerAddressType2Institution").attr("value", institution, true);
			dijit.byId("headerAddressType2Lastname").attr("value", nodeData.name, true);
			dijit.byId("headerAddressType2Firstname").attr("value", nodeData.givenName, true);
			dijit.byId("headerAddressType2Style").attr("value", nodeData.nameForm, true);
			dijit.byId("headerAddressType2Title").attr("value", nodeData.titleOrFunction, true);
			break;
		case 3:
			//dijit.byId("headerAddressType3Lastname").attr("value", nodeData.name, true);
			dijit.byId("headerAddressType3Lastname").attr("value", nodeData.name, true);
			dijit.byId("headerAddressType3Firstname").attr("value", nodeData.givenName, true);
			dijit.byId("headerAddressType3Style").attr("value", nodeData.nameForm, true);
			dijit.byId("headerAddressType3Title").attr("value", nodeData.titleOrFunction, true);
			dijit.byId("headerAddressType3Institution").attr("value", nodeData.organisation, true);
			break;
		default:
			console.debug("Error in udkDataProxy._setAddressData - Address Class must be 0, 1, 2 or 3. Wrong value: "+nodeData.addressClass);
			break;
	}
	
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
  
  if (nodeData.objectName == null)
  	dijit.byId("objectName").attr("value", message.get("tree.newNodeName"), true);
  else
  	dijit.byId("objectName").attr("value", nodeData.objectName, true);

  // onchange event
  dijit.byId("objectClass").attr("value", "Class"+nodeData.objectClass, true);

  var workStateStr = message.get("general.workState."+nodeData.workState);
  dojo.byId("workState").innerHTML = (nodeData.isMarkedDeleted ? workStateStr+"<br>("+message.get("general.state.markedDeleted")+")" : workStateStr);
  dojo.byId("creationTime").innerHTML = nodeData.creationTime;
  dojo.byId("modificationTime").innerHTML = nodeData.modificationTime;

  if (nodeData.lastEditor != null && UtilAddress.hasValidTitle(nodeData.lastEditor)) {
	dojo.byId("lastEditor").innerHTML = UtilAddress.createAddressTitle(nodeData.lastEditor);
  } else {
    dojo.byId("lastEditor").innerHTML = message.get("general.unknown");
  }

  dijit.byId("objectOwner").attr("value", nodeData.objectOwner, true);

  if (nodeData.writePermission == true) {
	dojo.style("permissionObjLock", 'display', 'none');
  } else {
	dojo.style("permissionObjLock", 'display', 'block');
  }

  // ------------------ Object Content ------------------
  formWidget = dijit.byId("contentFormObject");
//  console.debug("ContentFormObject before setting values: " + dojo.json.serialize(formWidget.getValues()));

  // --- General ---
  dijit.byId("generalShortDesc").attr("value", nodeData.generalShortDescription, true);
  dijit.byId("generalDesc").attr("value", nodeData.generalDescription, true);
  var addressTable = nodeData.generalAddressTable;
  //UtilList.addTableIndices(addressTable);
  UtilList.addIcons(addressTable);
  UtilList.addAddressTitles(addressTable);
  UtilList.addAddressLinkLabels(addressTable);
  UtilStore.updateWriteStore("generalAddress", addressTable);

  // Comments
  var commentData = UtilList.addDisplayDates(nodeData.commentTable);
  dojo.forEach(commentData, function(data, i) {
    commentData[i].title = UtilAddress.createAddressTitle(data.user);
  });
  //commentStore = UtilStore.updateWriteStore(commentStore, commentData, {label:"title"});
  commentStore = commentData;

  // -- Spatial --
  // The table containing entries from the sns is indexed by their topicID
  // The label is a combination of 'name' and 'topicType'
  UtilList.addSNSLocationLabels(nodeData.spatialRefAdminUnitTable);
  UtilList.markExpiredSNSLocations(nodeData.spatialRefAdminUnitTable);
  UtilStore.updateWriteStore("spatialRefAdminUnit", nodeData.spatialRefAdminUnitTable);
  // The table containing free entries needs generated indices
  UtilStore.updateWriteStore("spatialRefLocation", nodeData.spatialRefLocationTable);

  // need to set true here so that rule is applied onChange-event
  dijit.byId("spatialRefAltMin").attr("value", nodeData.spatialRefAltMin, true);
  dijit.byId("spatialRefAltMax").attr("value", nodeData.spatialRefAltMax, true);
  dijit.byId("spatialRefAltMeasure").attr("value", nodeData.spatialRefAltMeasure, true);
  dijit.byId("spatialRefAltVDate").attr("value", nodeData.spatialRefAltVDate, true);
  dijit.byId("spatialRefExplanation").attr("value", nodeData.spatialRefExplanation, true);

  // -- Time --
  dijit.byId("timeRefType").attr("value", nodeData.timeRefType, true);
  if (nodeData.timeRefType == "bis") {
	if (nodeData.timeRefDate2) { dijit.byId("timeRefDate1").attr("value", nodeData.timeRefDate2, true); }
	else { dijit.byId("timeRefDate1").attr("value", "", true);; }
	dijit.byId("timeRefDate2").attr("value", "", true);;  		

  } else {
	if (nodeData.timeRefDate1) { dijit.byId("timeRefDate1").attr("value", nodeData.timeRefDate1, true); }
	else { dijit.byId("timeRefDate1").attr("value", "", true);; }
	if (nodeData.timeRefDate2) { dijit.byId("timeRefDate2").attr("value", nodeData.timeRefDate2, true); }
	else { dijit.byId("timeRefDate2").attr("value", "", true);; }  	
  }

  dijit.byId("timeRefStatus").attr("value", nodeData.timeRefStatus, true);
  dijit.byId("timeRefPeriodicity").attr("value", nodeData.timeRefPeriodicity, true);
  dijit.byId("timeRefIntervalNum").attr("value", nodeData.timeRefIntervalNum, true);
  // TODO Temporarily read the display value from the db till it is changed in the backend
  dijit.byId("timeRefIntervalUnit").attr("displayedValue", UtilString.emptyIfNull(nodeData.timeRefIntervalUnit), false);//attr("value", timeRefValue);


  dijit.byId("timeRefExplanation").attr("value", nodeData.timeRefExplanation, true);
  UtilStore.updateWriteStore("timeRefTable", nodeData.timeRefTable);

  // -- Extra Info --
  dijit.byId("extraInfoLangMetaData").attr("value", nodeData.extraInfoLangMetaDataCode, true);
  dijit.byId("extraInfoLangData").attr("value", nodeData.extraInfoLangDataCode, true);
  dijit.byId("extraInfoPublishArea").attr("value", nodeData.extraInfoPublishArea, true);
  dijit.byId("extraInfoCharSetData").attr("value", nodeData.extraInfoCharSetDataCode,  false);
  UtilStore.updateWriteStore("extraInfoConformityTable", nodeData.extraInfoConformityTable);
  dijit.byId("extraInfoPurpose").attr("value", nodeData.extraInfoPurpose, true);
  dijit.byId("extraInfoUse").attr("value", nodeData.extraInfoUse, true);
  UtilStore.updateWriteStore("extraInfoXMLExportTable", UtilList.listToTableData(nodeData.extraInfoXMLExportTable));
  UtilStore.updateWriteStore("extraInfoLegalBasicsTable", UtilList.listToTableData(nodeData.extraInfoLegalBasicsTable));

  // -- Availability --
  UtilStore.updateWriteStore("availabilityAccessConstraints", UtilList.listToTableData(nodeData.availabilityAccessConstraints));
  UtilStore.updateWriteStore("availabilityUseConstraints", UtilList.listToTableData(nodeData.availabilityUseConstraints));
  dijit.byId("availabilityOrderInfo").attr("value", nodeData.availabilityOrderInfo, true);
  UtilStore.updateWriteStore("availabilityDataFormat", nodeData.availabilityDataFormatTable);
  UtilStore.updateWriteStore("availabilityMediaOptions", nodeData.availabilityMediaOptionsTable);


  // -- Thesaurus --
  UtilList.addSNSTopicLabels(nodeData.thesaurusTermsTable);
  
  UtilStore.updateWriteStore("thesaurusTerms", nodeData.thesaurusTermsTable);
  UtilStore.updateWriteStore("thesaurusTopics", UtilList.listToTableData(nodeData.thesaurusTopicsList));
  UtilStore.updateWriteStore("thesaurusInspire", UtilList.listToTableData(nodeData.thesaurusInspireTermsList));
  UtilStore.updateWriteStore("thesaurusEnvTopics", UtilList.listToTableData(nodeData.thesaurusEnvTopicsList));
  UtilStore.updateWriteStore("thesaurusEnvCats", UtilList.listToTableData(nodeData.thesaurusEnvCatsList));
  dijit.byId("thesaurusEnvExtRes").attr("value", nodeData.thesaurusEnvExtRes, true);


  // -- Links --
  var objLinkTable = nodeData.linksToObjectTable;
  var urlLinkTable = nodeData.linksToUrlTable;
  var linkTable = objLinkTable.concat(urlLinkTable);

  UtilList.addObjectLinkLabels(linkTable);
  UtilList.addUrlLinkLabels(linkTable);
  UtilList.addIcons(linkTable);
  	
  var updatedStore = UtilStore.updateWriteStore("linksTo", linkTable);
  
  // update data grids that display a sub set of this reference table
  udkDataProxy._connectSharedStore();

  var unpubLinkTable = nodeData.linksFromObjectTable;
  var pubLinkTable = nodeData.linksFromPublishedObjectTable;
  dojo.forEach(pubLinkTable, function(link) { link.pubOnly = true; } );
  linkTable = unpubLinkTable.concat(pubLinkTable);

  UtilList.addObjectLinkLabels(linkTable);
  UtilList.addIcons(linkTable);
  UtilStore.updateWriteStore("linksFrom", linkTable);

  // Additional Fields
  // -- Clear all fields
  if (additionalFieldWidgets) {
	  dojo.forEach(additionalFieldWidgets, function(currentFieldWidget) {
          if (currentFieldWidget instanceof ingrid.dijit.CustomGrid) {
              currentFieldWidget.setData([]);
              currentFieldWidget.invalidate();
          } else 
              currentFieldWidget.attr("value", "", true);
	  });
  }

  // -- Set data
  var additionalFields = nodeData.additionalFields;
  if (additionalFields) {
	  for (var index = 0; index < additionalFields.length; ++index) {
		  var currentField = additionalFields[index];
		  var currentFieldWidget = dijit.byId(currentField.identifier);
          if (currentFieldWidget instanceof ingrid.dijit.CustomGrid) {
              //console.debug("additional field cannot be set: " + currentField.identifier);
              // it must be a slickGrid
              var grid = gridManager[currentField.identifier];
              if (grid == null) {
                  console.debug("additional field cannot be set: " + currentField.identifier);
                  break;
              }
              var rowData = [];
              dojo.forEach(currentField.tableRows, function(row) {
                  var columnData = {};
                  dojo.forEach(row, function(col) {
                      if (col.listId == -1)
                          columnData[col.identifier] = col.value;
                      else
                          columnData[col.identifier] = col.listId;
                  });
                  rowData.push(columnData);
              });
              grid.setData(rowData);
              grid.render();
          }
          else {
              if (currentFieldWidget.store && currentField.listId != -1) {
            	  // distinguish between combo and select box!
            	  // combo boxes always cope with values instead of keys
            	  if (currentFieldWidget instanceof dijit.form.ComboBox)
            		  currentFieldWidget.attr("value", currentField.value, true);
            	  else
            		  currentFieldWidget.attr("value", currentField.listId, true);
              } else if (currentFieldWidget instanceof dijit.form.DateTextBox) {
                  currentFieldWidget.attr("value", dojo.date.locale.parse(currentField.value, {datePattern: "dd.MM.yyyy", selector: "date"}), true);
              } else
                  currentFieldWidget.attr("value", currentField.value, true);
          }
	  }
  }

  // Clear all object classes
	udkDataProxy._setObjectDataClass0(nodeData);
	udkDataProxy._setObjectDataClass1(nodeData);
	udkDataProxy._setObjectDataClass2(nodeData);
	udkDataProxy._setObjectDataClass3(nodeData);
	udkDataProxy._setObjectDataClass4(nodeData);
	udkDataProxy._setObjectDataClass5(nodeData);
    udkDataProxy._setObjectDataClass6(nodeData);

//  console.debug("ContentFormObject after setting values: " + dojo.json.serialize(formWidget.getValues()));

// The values are set with the corresponding setter methods
// We could also set the values through the form
//  dijit.byId("contentFormObject").setValues(myObj);
//  dijit.byId("headerFormAddress").setValues(myObj);
//  dijit.byId("contentFormAddress").setValues(myObj);

}

udkDataProxy._connectSharedStore = function() {
	var ids = [["ref1KeysLink", "3535"], ["ref1ServiceLink", "5066"], ["ref1DataBasisLink", "3570"], ["ref1BasisLink", "3520"],
		["ref1SymbolsLink", "3555"], ["ref1ProcessLink", "3515"], ["ref2BaseDataLink", "3345"], ["ref3BaseDataLink", "3210"],
		["ref5MethodLink", "3100"], ["ref6BaseDataLink", "3210"]];
	var idsLinkAddresses = [["ref2LocationLink", "3360"], ["ref4ParticipantsLink","3410"], ["ref4PMLink","3400"]];
	
	var linksToTableData = UtilGrid.getTableData("linksTo");
	dojo.forEach(ids, function(id){
		//UtilGrid.getTableData(id).setItems(linksToStore, "_id");
		UtilGrid.setTableData(id[0], dojo.filter(linksToTableData, function(item) {return item.relationType == id[1];}));
        UtilGrid.getTable(id[0]).invalidate();
	});
   
	
	var linksToAddressesTableData = UtilGrid.getTableData("generalAddress");
	dojo.forEach(idsLinkAddresses, function(id){
		//UtilGrid.getTableData(id).setItems(linksToAddresses, "_id");
		UtilGrid.setTableData(id[0], dojo.filter(linksToAddressesTableData, function(item) {return item.typeOfRelation == id[1];}));
		UtilGrid.getTable(id[0]).invalidate();
	});
}

udkDataProxy._setObjectDataClass0 = function(nodeData) {}

udkDataProxy._setObjectDataClass1 = function(nodeData) {
    dijit.byId("isInspireRelevant").attr("value", nodeData.inspireRelevant, true);
	dijit.byId("ref1ObjectIdentifier").attr("value", nodeData.ref1ObjectIdentifier, true);
	dijit.byId("ref1DataSet").attr("value", nodeData.ref1DataSet, true);
	dijit.byId("ref1Coverage").attr("value", nodeData.ref1Coverage, true);
	dijit.byId("ref1VFormatTopology").attr("value", nodeData.ref1VFormatTopology, true);

	// The spatial system table is a combobox that allows free entries but also entries associated with IDs
	// If the reference system ID == -1 then we receive a free entry, otherwise we have to resolve the id
	var value = nodeData.ref1SpatialSystem;//-1;
	  /*dojo.some(dijit.byId("ref1SpatialSystem").getOptions(), function(entry){
	  	if (entry.label == nodeData.ref1SpatialSystem) {
	  		value = entry.value;
	  		return true;
	  	}
	  });*/
	dijit.byId("ref1SpatialSystem").attr("value", value, true);

	dijit.byId("ref1AltAccuracy").attr("value", nodeData.ref1AltAccuracy, true);
	dijit.byId("ref1PosAccuracy").attr("value", nodeData.ref1PosAccuracy, true);
	dijit.byId("ref1BasisText").attr("value", nodeData.ref1BasisText, true);
	dijit.byId("ref1DataBasisText").attr("value", nodeData.ref1DataBasisText, true);
	dijit.byId("ref1ProcessText").attr("value", nodeData.ref1ProcessText, true);

	UtilStore.updateWriteStore("ref1Representation", UtilList.listToTableData(nodeData.ref1Representation));
	
	UtilStore.updateWriteStore("ref1Data", UtilList.listToTableData(nodeData.ref1Data));
	UtilStore.updateWriteStore("ref1VFormatDetails", nodeData.ref1VFormatDetails);
	UtilStore.updateWriteStore("ref1Scale", nodeData.ref1Scale);
	UtilStore.updateWriteStore("ref1SymbolsText", nodeData.ref1SymbolsText);
	UtilStore.updateWriteStore("ref1KeysText", nodeData.ref1KeysText);
    
    
    dojo.forEach(dqUiTableElements, function(dqTableId) {
       UtilStore.updateWriteStore(dqTableId, nodeData[dqTableId]);
    });

    dijit.byId("availabilityDataFormatInspire").attr("value", nodeData.availabilityDataFormatInspire, true);

}

udkDataProxy._setObjectDataClass2 = function(nodeData) {
	dijit.byId("ref2Author").attr("value", nodeData.ref2Author, true);
	dijit.byId("ref2Publisher").attr("value", nodeData.ref2Publisher, true);
	dijit.byId("ref2PublishedIn").attr("value", nodeData.ref2PublishedIn, true);
	dijit.byId("ref2PublishLocation").attr("value", nodeData.ref2PublishLocation, true);
	dijit.byId("ref2PublishedInIssue").attr("value", nodeData.ref2PublishedInIssue, true);
	dijit.byId("ref2PublishedInPages").attr("value", nodeData.ref2PublishedInPages, true);
	dijit.byId("ref2PublishedInYear").attr("value", nodeData.ref2PublishedInYear, true);
	dijit.byId("ref2PublishedISBN").attr("value", nodeData.ref2PublishedISBN, true);
	dijit.byId("ref2PublishedPublisher").attr("value", nodeData.ref2PublishedPublisher, true);
	dijit.byId("ref2LocationText").attr("value", nodeData.ref2LocationText, true);
	dijit.byId("ref2DocumentType").attr("value", nodeData.ref2DocumentType, true);
	dijit.byId("ref2BaseDataText").attr("value", nodeData.ref2BaseDataText, true);
	dijit.byId("ref2BibData").attr("value", nodeData.ref2BibData, true);
	dijit.byId("ref2Explanation").attr("value", nodeData.ref2Explanation, true);
}

udkDataProxy._setObjectDataClass3 = function(nodeData) {
    dijit.byId("isInspireRelevant").attr("value", nodeData.inspireRelevant, true);
	dijit.byId("ref3ServiceType").attr("value", nodeData.ref3ServiceType, true);
	dijit.byId("ref3SystemEnv").attr("value", nodeData.ref3SystemEnv, true);
	dijit.byId("ref3History").attr("value", nodeData.ref3History, true);
	dijit.byId("ref3BaseDataText").attr("value", nodeData.ref3BaseDataText, true);
	dijit.byId("ref3Explanation").attr("value", nodeData.ref3Explanation, true);

//	console.debug("Setting service version to: "+UtilList.addTableIndices(UtilList.listToTableData(nodeData.ref3ServiceVersion)));

	UtilStore.updateWriteStore("ref3ServiceTypeTable", UtilList.listToTableData(nodeData.ref3ServiceTypeTable));

	UtilStore.updateWriteStore("ref3ServiceVersion", UtilList.listToTableData(nodeData.ref3ServiceVersion));

	UtilStore.updateWriteStore("ref3Scale", nodeData.ref3Scale);

	// Prepare the operation table for display.
	// Add table indices to the main obj and paramList
	// Add table indices and convert to tableData: platform, addressList and dependencies
	if (nodeData.ref3Operation) {
		for (var i = 0; i < nodeData.ref3Operation.length; ++i) {
			//UtilList.addTableIndices(nodeData.ref3Operation[i].paramList);
			nodeData.ref3Operation[i].platform = UtilList.listToTableData(nodeData.ref3Operation[i].platform);
			nodeData.ref3Operation[i].addressList = UtilList.listToTableData(nodeData.ref3Operation[i].addressList);
			nodeData.ref3Operation[i].dependencies = UtilList.listToTableData(nodeData.ref3Operation[i].dependencies);		
		}
	}

	UtilStore.updateWriteStore("ref3Operation", nodeData.ref3Operation);
    dijit.byId("ref3HasAccessConstraint").attr("value", nodeData.ref3HasAccessConstraint, true);
}

udkDataProxy._setObjectDataClass4 = function(nodeData) {
	dijit.byId("ref4ParticipantsText").attr("value", nodeData.ref4ParticipantsText, true);
	dijit.byId("ref4PMText").attr("value", nodeData.ref4PMText, true);
	dijit.byId("ref4Explanation").attr("value", nodeData.ref4Explanation, true);
}

udkDataProxy._setObjectDataClass5 = function(nodeData) {
	dijit.byId("ref5MethodText").attr("value", nodeData.ref5MethodText, true);
	dijit.byId("ref5Explanation").attr("value", nodeData.ref5Explanation, true);

	UtilStore.updateWriteStore("ref5dbContent", nodeData.ref5dbContent);
}

udkDataProxy._setObjectDataClass6 = function(nodeData) {
    dijit.byId("ref6ServiceType").attr("value", nodeData.ref6ServiceType, true);
    dijit.byId("ref6SystemEnv").attr("value", nodeData.ref6SystemEnv, true);
    dijit.byId("ref6History").attr("value", nodeData.ref6History, true);
    dijit.byId("ref6BaseDataText").attr("value", nodeData.ref6BaseDataText, true);
    dijit.byId("ref6Explanation").attr("value", nodeData.ref6Explanation, true);

//  dojo.debug("Setting service version to: "+UtilList.addTableIndices(UtilList.listToTableData(nodeData.ref3ServiceVersion)));
    //console.debug("nodeData.ref6ServiceVersion: " + nodeData.ref6ServiceVersion);
    //console.debug("UtilList.listToTableData(nodeData.ref6ServiceVersion): " + UtilList.listToTableData(nodeData.ref6ServiceVersion));
    //console.debug("UtilList.addTableIndices(UtilList.listToTableData(nodeData.ref6ServiceVersion)): " + UtilList.listToTableData(nodeData.ref6ServiceVersion));
    UtilStore.updateWriteStore("ref6ServiceVersion", UtilList.listToTableData(nodeData.ref6ServiceVersion));

    UtilStore.updateWriteStore("ref6UrlList", nodeData.ref6UrlList);
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
      console.debug("Error in udkDataProxy._getData - Node Type must be \'A\' or \'O\'!");
      break;
  }

  return (nodeData);
}

udkDataProxy._getAddressData = function(nodeData) {

	// ------------- General Static Data -------------
	nodeData.uuid = currentUdk.uuid;
	nodeData.workState = currentUdk.workState;
	var parentUuid = dijit.byId(currentUdk.uuid).getParent().id[0];
	if (parentUuid != "addressRoot" && parentUuid != "addressFreeRoot") {
		nodeData.parentUuid = parentUuid;
	}
	nodeData.addressOwner = dijit.byId("addressOwner").getValue();

	// ------------------ Header ------------------
	nodeData.addressClass = UtilAddress.getAddressClass();

	// ------------------ Address and Function ------------------
	nodeData.street = dijit.byId("addressStreet").getValue();
	nodeData.countryCode = dijit.byId("addressCountry").getValue();
	nodeData.countryName = dijit.byId("addressCountry").get("displayedValue");//UtilList.getSelectDisplayValue(dijit.byId("addressCountry"), dijit.byId("addressCountry").get('value'));//dijit.byId("addressCountry").getDisplayValue();
	nodeData.postalCode = dijit.byId("addressZipCode").getValue();
	nodeData.city = dijit.byId("addressCity").getValue();
	nodeData.pobox = dijit.byId("addressPOBox").getValue();
	nodeData.poboxPostalCode = dijit.byId("addressZipPOBox").getValue();
	nodeData.addressDescription = dijit.byId("addressNotes").getValue();
	nodeData.task = dijit.byId("addressTasks").getValue();
	nodeData.communication = udkDataProxy._getTableData("addressCom");
    
    // replace syslist entries with name and leave it if it's a free entry
    dojo.forEach(nodeData.communication, function(entry) {
        entry.medium = UtilSyslist.getSyslistEntryName(4430, entry.medium);
    });

	// -- Thesaurus --
	nodeData.thesaurusTermsTable = udkDataProxy._getTableData("thesaurusTermsAddress");

	// -- Links --
	nodeData.linksFromObjectTable = udkDataProxy._getTableData("associatedObjName");

	// Comments
	//nodeData.commentTable = UtilStore.convertItemsToJS(commentStore, commentStore._arrayOfTopLevelItems);
    nodeData.commentTable = commentStore;


  // ------------------ Class specific content ------------------
	switch(nodeData.addressClass) {
		case 0:
//			nodeData.organisation = dijit.byId("headerAddressType0Institution").getValue();
			nodeData.organisation = dijit.byId("headerAddressType0Unit").getValue();
			break;
		case 1:
//			dijit.byId("headerAddressType1Institution").attr("value", nodeData.organisation);
			nodeData.organisation = dijit.byId("headerAddressType1Unit").getValue();
			break;
		case 2:
//			dijit.byId("headerAddressType2Institution").attr("value", nodeData.organisation);
			nodeData.name = dijit.byId("headerAddressType2Lastname").getValue();
			nodeData.givenName = dijit.byId("headerAddressType2Firstname").getValue();
			nodeData.nameForm = dijit.byId("headerAddressType2Style").getValue();
			nodeData.titleOrFunction = dijit.byId("headerAddressType2Title").getValue();
			//nodeData.organisation = dijit.byId("headerAddressType2Institution").getValue();
			break;
		case 3:
			nodeData.name = dijit.byId("headerAddressType3Lastname").getValue();
			nodeData.givenName = dijit.byId("headerAddressType3Firstname").getValue();
			nodeData.nameForm = dijit.byId("headerAddressType3Style").getValue();
			nodeData.titleOrFunction = dijit.byId("headerAddressType3Title").getValue();
			nodeData.organisation = dijit.byId("headerAddressType3Institution").getValue();
			break;
		default:
			console.debug("Error in udkDataProxy._getAddressData - Address Class must be 0, 1, 2 or 3!");
			break;
	}

  console.debug("------ ADDRESS DATA ------");
  console.debug(nodeData);
  console.debug("------ ADDRESS DATA END ------");
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
  nodeData.workState = currentUdk.workState;
  var parentUuid = dijit.byId(currentUdk.uuid).getParent().id[0];
  if (parentUuid != "objectRoot") {
  	nodeData.parentUuid = parentUuid;
  }
  nodeData.objectOwner = dijit.byId("objectOwner").getValue();

  // ------------------ Header ------------------
  nodeData.objectName = dijit.byId("objectName").getValue();
//  nodeData.last_editor = dijit.byId("last_editor").getValue();

  // ------------------ Object Content ------------------
  // --- General ---
  nodeData.generalShortDescription = dijit.byId("generalShortDesc").getValue();
  nodeData.generalDescription = dijit.byId("generalDesc").getValue();
  nodeData.objectClass = dijit.byId("objectClass").getValue().substr(5, 1); // Value is a string: "Classx" where x is the class
  nodeData.generalAddressTable = udkDataProxy._getTableData("generalAddress");
  // Comments
  //nodeData.commentTable = UtilStore.convertItemsToJS(commentStore, commentStore._arrayOfTopLevelItems);
  nodeData.commentTable = commentStore;

  // -- Spatial --
  nodeData.spatialRefAdminUnitTable = udkDataProxy._getTableData("spatialRefAdminUnit");
  nodeData.spatialRefLocationTable = udkDataProxy._getTableData("spatialRefLocation");

  nodeData.spatialRefAltMin = (dijit.byId("spatialRefAltMin").get('value')+"" == "NaN") ? "" : dijit.byId("spatialRefAltMin").get('value');
  nodeData.spatialRefAltMax = (dijit.byId("spatialRefAltMax").get('value')+"" == "NaN") ? "" : dijit.byId("spatialRefAltMax").get('value');
  nodeData.spatialRefAltMeasure = dijit.byId("spatialRefAltMeasure").get('value');
  nodeData.spatialRefAltVDate = dijit.byId("spatialRefAltVDate").get('value');
  nodeData.spatialRefExplanation = dijit.byId("spatialRefExplanation").get('value');

  // -- Time --
  nodeData.timeRefType = dijit.byId("timeRefType").getValue();
  var dirty = this.dirtyFlag;
  var timeFrom = dijit.byId("timeRefDate1").getValue();
  var timeTo = dijit.byId("timeRefDate2").getValue();
  // ??? what's the use of this?
  //if (dirty) { this.setDirtyFlag(); } else { this.resetDirtyFlag(); }

  if (nodeData.timeRefType == "bis") {	
	  if (timeFrom != "") {
		  nodeData.timeRefDate1 = null;
		  nodeData.timeRefDate2 = timeFrom;
	  }
  } else if (nodeData.timeRefType == "am") {
	  if (timeFrom != "") {
		  nodeData.timeRefDate1 = timeFrom;
		  nodeData.timeRefDate2 = timeFrom;
	  }
  } else if (nodeData.timeRefType == "seit") {
	  if (timeFrom != "") {
		  nodeData.timeRefDate1 = timeFrom;
		  nodeData.timeRefDate2 = null;
	  }
  } else if (nodeData.timeRefType == "von") {
	  if (timeFrom != "") {
		  nodeData.timeRefDate1 = timeFrom;
	  }
	  if (timeTo != "") {
		  nodeData.timeRefDate2 = timeTo;
	  }
  }

  nodeData.timeRefStatus = dijit.byId("timeRefStatus").getValue();
  nodeData.timeRefPeriodicity = dijit.byId("timeRefPeriodicity").getValue();
  nodeData.timeRefIntervalNum = dijit.byId("timeRefIntervalNum").get("displayedValue");
  // TODO Temporarily store the display value in the database till it is changed in the backend
  nodeData.timeRefIntervalUnit = dijit.byId("timeRefIntervalUnit").get("displayedValue");

  nodeData.timeRefExplanation = dijit.byId("timeRefExplanation").getValue();
  nodeData.timeRefTable = udkDataProxy._getTableData("timeRefTable");

  // -- Extra Info --
  nodeData.extraInfoLangMetaDataCode = dijit.byId("extraInfoLangMetaData").getValue();
  nodeData.extraInfoLangDataCode = dijit.byId("extraInfoLangData").getValue();
  nodeData.extraInfoPublishArea = dijit.byId("extraInfoPublishArea").getValue();
  nodeData.extraInfoCharSetDataCode = dijit.byId("extraInfoCharSetData").getValue();
  nodeData.extraInfoConformityTable = udkDataProxy._getTableData("extraInfoConformityTable");
  nodeData.extraInfoPurpose = dijit.byId("extraInfoPurpose").getValue();
  nodeData.extraInfoUse = dijit.byId("extraInfoUse").getValue();
	
  nodeData.extraInfoXMLExportTable = UtilList.tableDataToList(udkDataProxy._getTableData("extraInfoXMLExportTable"));
  var valuesExtraInfoLegalBasicsTable = 
  nodeData.extraInfoLegalBasicsTable = UtilList.tableDataToList(udkDataProxy._getTableData("extraInfoLegalBasicsTable"));

  // -- Availability --
//  nodeData.availabilityUsageLimitationTable = udkDataProxy._getTableData("availabilityUsageLimitationTable");
  nodeData.availabilityAccessConstraints = UtilList.tableDataToList(udkDataProxy._getTableData("availabilityAccessConstraints"));
  nodeData.availabilityUseConstraints = UtilList.tableDataToList(udkDataProxy._getTableData("availabilityUseConstraints"));

  nodeData.availabilityOrderInfo = dijit.byId("availabilityOrderInfo").getValue();
  nodeData.availabilityDataFormatTable = udkDataProxy._getTableData("availabilityDataFormat");
  nodeData.availabilityMediaOptionsTable = udkDataProxy._getTableData("availabilityMediaOptions");

  // -- Thesaurus --
  nodeData.thesaurusTermsTable = udkDataProxy._getTableData("thesaurusTerms");
  nodeData.thesaurusTopicsList = UtilList.tableDataToList(udkDataProxy._getTableData("thesaurusTopics"));
  nodeData.thesaurusInspireTermsList = UtilList.tableDataToList(udkDataProxy._getTableData("thesaurusInspire"));
  nodeData.thesaurusEnvTopicsList = UtilList.tableDataToList(udkDataProxy._getTableData("thesaurusEnvTopics"));
  nodeData.thesaurusEnvCatsList = UtilList.tableDataToList(udkDataProxy._getTableData("thesaurusEnvCats"));
  nodeData.thesaurusEnvExtRes = dijit.byId("thesaurusEnvExtRes").checked;


  // -- Links --
  var linksToTable = udkDataProxy._getTableData("linksTo");
  var objLinks = [];
  var urlLinks = [];
  dojo.forEach(linksToTable, function(link) {
	if (link.url) {
  		urlLinks.push(link);
  	} else {
  		objLinks.push(link);  	
  	}
  });

  nodeData.linksToObjectTable = objLinks;
  nodeData.linksToUrlTable = urlLinks;
  nodeData.linksFromObjectTable = udkDataProxy._getTableData("linksFrom");

  // Additional Fields
  nodeData.additionalFields = [];
  
  if (additionalFieldWidgets) {
	  for (var nr = 0; nr < additionalFieldWidgets.length; nr++) {
		  var currentField = additionalFieldWidgets[nr];
		  var identifier = currentField.id;
          
          // check if field is a table and handle differently
          if (currentField instanceof ingrid.dijit.CustomGrid) { //currentField.keepRows != undefined) {
              //identifier = currentField;
              //currentField = gridManager[currentField];
              // get column ids
              var tableData = [];
              var columnIds = [];
              //for (var i = 0; i < currentField.getColumns().length; i++) {
              dojo.forEach(currentField.getColumns(), function(column) {
                  columnIds.push(column.field);
              });
              //for (var i = 0; i < currentField._by_idx.length; i++) {
              dojo.forEach(currentField.getData(), function(row) {
                  //var row = currentField.getItem(i);
                  var rowData = [];
                  for (var j = 0; j < columnIds.length; j++) {
                      var value = row[columnIds[j]];
                      if (value == undefined || value == null) continue;
                      
                      //if (value instanceof Date)
                      //    value = UtilString.getDateString(value, "dd.MM.yyyy");
                      
                      var listId = "-1";
                      // get listId from structure element of grid in case it is a list
                      if (currentField.getColumns()[j].values) {
                          var index = dojo.indexOf(currentField.getColumns()[j].values, value);
                          if (index == -1) index = dojo.indexOf(currentField.getColumns()[j].options, value);
                          if (index != -1) {
                              var listId = currentField.getColumns()[j].values[index];
                              value = currentField.getColumns()[j].options[index];
                          }
                      }
                                            
                      var columnData = {
                          identifier: columnIds[j],
                          value: value,
                          listId: listId,
                          tableRows: null
                      };
                      rowData.push(columnData);
                  }
                  tableData.push(rowData);
              });
              
              // add empty rows so that table can be made empty
              if (tableData.length == 0) {
                  tableData.push([]);
              }
              
              nodeData.additionalFields.push({ identifier: currentField.id, value: null, listId: null, tableRows: tableData} );
          } else {
              // if it's a select box we need to get listId and value
              if (currentField instanceof dijit.form.FilteringSelect ||
                  currentField instanceof dijit.form.ComboBox) {
                  var listId = -1;
                  var item = additionalFieldWidgets[nr].item;
                  if (item != null) {
                      listId = item.id[0];
                  }
                  
                  // for lists to get value
                  //var listId = additionalFieldWidgets[nr].get("value");
                  var value = additionalFieldWidgets[nr].get("value");//UtilList.getSelectDisplayValue(additionalFieldWidgets[nr], listId);
                  
              } else if (currentField instanceof dijit.form.DateTextBox) {
                 var value = currentField.get("value");
                 if (value != null)
                    value = UtilString.getDateString(currentField.get("value"), "dd.MM.yyyy");
                 
              } else {
                  var value = currentField.get("displayedValue");
              }
              
              if (value != null && dojo.trim(value+"").length != 0) {
                  nodeData.additionalFields.push( { identifier: identifier, value: value, listId: listId, tableRows: null } );
              }
          }
	  }
  }
  
  // last modified date
  nodeData.modificationTime = dojo.byId("modificationTime").innerHTML;

    // NOTICE: some stuff was moved from class specific domain map ("Fachbezug") to general sections (in GUI).
  // this stuff has to be processed here, before doing class specific stuff !

  // former class 1, now general "Raumbezug"
    // The spatial system table is a combobox that allows free entries but also entries associated with IDs
    // If we have a free entry the reference system ID = -1
//  nodeData.ref1SpatialSystemId = dojo.widget.byId("ref1SpatialSystem").getIdValue();
//  if (nodeData.ref1SpatialSystemId == -1)
//      nodeData.ref1SpatialSystem = dojo.widget.byId("ref1SpatialSystem").getValue();
  nodeData.ref1SpatialSystem = dijit.byId("ref1SpatialSystem").getValue();

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
    case '6':
      udkDataProxy._getObjectDataClass6(nodeData);
      break;
    default:
      console.debug("Error in udkDataProxy._getObjectData - Object Class must be 0...5!");
      break;
  }

  console.debug("------ OBJECT DATA ------");
  console.debug(nodeData);
  console.debug("------ OBJECT DATA END ------");
}

udkDataProxy._getObjectDataClass0 = function(nodeData) {};

udkDataProxy._getObjectDataClass1 = function(nodeData) {
    nodeData.inspireRelevant = dijit.byId("isInspireRelevant").checked ? true : false; // in case value is NULL!
	nodeData.ref1ObjectIdentifier = dijit.byId("ref1ObjectIdentifier").getValue();
	nodeData.ref1DataSet = dijit.byId("ref1DataSet").getValue();
	nodeData.ref1Coverage = dijit.byId("ref1Coverage").get("displayedValue")
	nodeData.ref1VFormatTopology = dijit.byId("ref1VFormatTopology").getValue();

	nodeData.ref1AltAccuracy = dijit.byId("ref1AltAccuracy").get("displayedValue");
	nodeData.ref1PosAccuracy = dijit.byId("ref1PosAccuracy").get("displayedValue");
	nodeData.ref1BasisText = dijit.byId("ref1BasisText").getValue();
	nodeData.ref1DataBasisText = dijit.byId("ref1DataBasisText").getValue();
	nodeData.ref1ProcessText = dijit.byId("ref1ProcessText").getValue();


	nodeData.ref1Representation = UtilList.tableDataToList(udkDataProxy._getTableData("ref1Representation"));
	nodeData.ref1Data = UtilList.tableDataToList(udkDataProxy._getTableData("ref1Data"));

	nodeData.ref1VFormatDetails = udkDataProxy._getTableData("ref1VFormatDetails");
	nodeData.ref1Scale = udkDataProxy._getTableData("ref1Scale");
	nodeData.ref1SymbolsText = udkDataProxy._getTableData("ref1SymbolsText");
	nodeData.ref1KeysText = udkDataProxy._getTableData("ref1KeysText");
    
    var dqUiTableElements = dojo.query("#refClass1DQ span:not(.hide) .ui-widget", "contentFrameBodyObject").map(function(item) {return item.id;});
    dojo.forEach(dqUiTableElements, function(dqTableId) {
        // only map data of DQ tables shown ! remove other data !
        // -> only visible dqTables are selected!!! 
        //if (!dojo.hasClass(dqTableId, "hide")) {
            nodeData[dqTableId] = udkDataProxy._getTableData(dqTableId);
        //} else {
        //    UtilGrid.setTableData(dqTableId, []);
        //}
    });

    nodeData.availabilityDataFormatInspire = dijit.byId("availabilityDataFormatInspire").get("value");
};

udkDataProxy._getObjectDataClass2 = function(nodeData) {
	nodeData.ref2Author = dijit.byId("ref2Author").getValue();
	nodeData.ref2Publisher = dijit.byId("ref2Publisher").getValue();
	nodeData.ref2PublishedIn = dijit.byId("ref2PublishedIn").getValue();
	nodeData.ref2PublishLocation = dijit.byId("ref2PublishLocation").getValue();
	nodeData.ref2PublishedInIssue = dijit.byId("ref2PublishedInIssue").getValue();
	nodeData.ref2PublishedInPages = dijit.byId("ref2PublishedInPages").getValue();
	nodeData.ref2PublishedInYear = dijit.byId("ref2PublishedInYear").getValue();
	nodeData.ref2PublishedISBN = dijit.byId("ref2PublishedISBN").getValue();
	nodeData.ref2PublishedPublisher = dijit.byId("ref2PublishedPublisher").getValue();
	nodeData.ref2LocationText = dijit.byId("ref2LocationText").getValue();
	nodeData.ref2DocumentType = dijit.byId("ref2DocumentType").getValue();
	nodeData.ref2BaseDataText = dijit.byId("ref2BaseDataText").getValue();
	nodeData.ref2BibData = dijit.byId("ref2BibData").getValue();
	nodeData.ref2Explanation = dijit.byId("ref2Explanation").getValue();
};


udkDataProxy._getObjectDataClass3 = function(nodeData) {
    nodeData.inspireRelevant = dijit.byId("isInspireRelevant").checked ? true : false; // in case value is NULL!
	nodeData.ref3ServiceType = dijit.byId("ref3ServiceType").getValue();
	nodeData.ref3SystemEnv = dijit.byId("ref3SystemEnv").getValue();
	nodeData.ref3History = dijit.byId("ref3History").getValue();
	nodeData.ref3BaseDataText = dijit.byId("ref3BaseDataText").getValue();
	nodeData.ref3Explanation = dijit.byId("ref3Explanation").getValue();

	nodeData.ref3ServiceTypeTable = UtilList.tableDataToList(udkDataProxy._getTableData("ref3ServiceTypeTable"));
	nodeData.ref3ServiceVersion = UtilList.tableDataToList(udkDataProxy._getTableData("ref3ServiceVersion"));

	nodeData.ref3Scale = udkDataProxy._getTableData("ref3Scale");

	// Convert the containing operation tables to lists
	// Add table indices and convert to tableData: platform, addressList and dependencies
	nodeData.ref3Operation = [];
	var op = udkDataProxy._getTableData("ref3Operation");
	if (op) {
        for (var i = 0; i < op.length; ++i) {
            var operationData = {};
            operationData.name = op[i].name;
            operationData.description = op[i].description;
            //			operationData.operationsCall = op[i].operationsCall;
            operationData.methodCall = op[i].methodCall;
            operationData.paramList = dojo.isArray(op[i].paramList) ? op[i].paramList : [op[i].paramList];
            operationData.platform = UtilList.tableDataToList(op[i].platform);
            operationData.addressList = UtilList.tableDataToList(op[i].addressList);
            operationData.dependencies = UtilList.tableDataToList(op[i].dependencies);
            
            nodeData.ref3Operation.push(operationData);
        }
    }
    nodeData.ref3Explanation = dijit.byId("ref3Explanation").getValue();
    nodeData.ref3HasAccessConstraint = dijit.byId("ref3HasAccessConstraint").checked;
};

udkDataProxy._getObjectDataClass4 = function(nodeData) {
	nodeData.ref4ParticipantsText = dijit.byId("ref4ParticipantsText").getValue();
	nodeData.ref4PMText = dijit.byId("ref4PMText").getValue();
	nodeData.ref4Explanation = dijit.byId("ref4Explanation").getValue();
};

udkDataProxy._getObjectDataClass5 = function(nodeData) {
	nodeData.ref5MethodText = dijit.byId("ref5MethodText").getValue();
	nodeData.ref5Explanation = dijit.byId("ref5Explanation").getValue();

	nodeData.ref5dbContent = udkDataProxy._getTableData("ref5dbContent");
};

udkDataProxy._getObjectDataClass6 = function(nodeData) {
    nodeData.ref6ServiceType = dijit.byId("ref6ServiceType").getValue();
    nodeData.ref6SystemEnv = dijit.byId("ref6SystemEnv").getValue();
    nodeData.ref6History = dijit.byId("ref6History").getValue();
    nodeData.ref6BaseDataText = dijit.byId("ref6BaseDataText").getValue();
    nodeData.ref6Explanation = dijit.byId("ref6Explanation").getValue();

    console.debug("ref6ServiceVersion tabledata: " + udkDataProxy._getTableData("ref6ServiceVersion"))
    nodeData.ref6ServiceVersion = UtilList.tableDataToList(udkDataProxy._getTableData("ref6ServiceVersion"));
    console.debug("nodeData.ref6ServiceVersion: " + nodeData.ref6ServiceVersion);

    nodeData.ref6UrlList = udkDataProxy._getTableData("ref6UrlList");
};



/*******************************************
 *            Helper functions             *
 *******************************************/

udkDataProxy._initResponsibleUserObjectList = function(nodeData) {
	var def = new dojo.Deferred();

    if (nodeData.uuid == "newNode") {
		var selectWidget = dijit.byId("objectOwner");

		var parentUuid = nodeData.parentUuid;

		if (parentUuid != null) {
			// new node && not root
			SecurityService.getResponsibleUsersForNewObject(parentUuid, false, true, {
				callback: function(userList) {
					var list = [];
                    dojo.forEach(userList, function(user){
                        var title = UtilAddress.createAddressTitle(user.address);
                        var uuid = user.address.uuid;
                        list.push([title, uuid]);
                    });
                    UtilStore.updateWriteStore("objectOwner", list, { identifier: '1', label: '0', items: list });
					
					def.callback(nodeData);
				},
				errorHandler: function(errMsg, err) {
					console.debug(errMsg);
					console.debug(err);
					def.errback(err);
				}
			});
		} else {
			// new root node
	    	// get all users from the current users groups that have root permission and the catalog admin
            var getUsersDef = UtilSecurity.getUsersFromCurrentGroupsWithRootPermission();
            var getCatAdminDef = UtilSecurity.getCatAdmin();

			var defList = new dojo.DeferredList([getUsersDef, getCatAdminDef], false, false, true);
			defList.addCallback(function(resultList) {
				var list = [];
				
				// Add all users from the current group
				for (var i in resultList[0][1]) {
					// Iterate over the users from the current group
					var user = resultList[0][1][i];
					var title = UtilAddress.createAddressTitle(user.address);
					var uuid = user.address.uuid;
					list.push([title, uuid]);
				}

				// Add the catalog administrator
				// only if the current user is not the cat admin himself
				if (currentUser.role != 1) {
					var catAdmin = resultList[1][1];
					var catAdminTitle = UtilAddress.createAddressTitle(catAdmin.address);
					var catAdminUuid = catAdmin.address.uuid;
					list.push([catAdminTitle, catAdminUuid]);
				}

				UtilStore.updateWriteStore("objectOwner", list, { identifier: '1', label: '0', items: list });	
				def.callback(nodeData);
			});
			defList.addErrback(function(errMsg, err) { def.errback(err); });
		}

    	return def;
    }


	SecurityService.getUsersWithWritePermissionForObject(nodeData.uuid, false, false, {
		callback: function(userList) {
			var list = [];
			dojo.forEach(userList, function(user){
				var title = UtilAddress.createAddressTitle(user.address);
				var uuid = user.address.uuid;
				list.push([title, uuid]);
			});
			var selectWidget = dijit.byId("objectOwner");
			var updatedStore = new dojo.data.ItemFileReadStore({
				// list is an array of arrays where first entry(0) is the name
				// and the second entry(1) the uuid
				data: {	identifier: '1', label: '0', items: list }
			});
			//selectWidget.store.data = list;
			selectWidget.setStore(updatedStore);
			def.callback(nodeData);
		},
		errorHandler: function(errMsg, err) {
			console.debug(errMsg);
			console.debug(err);
			def.errback(err);
		}
	});

	return def;
}

udkDataProxy._initResponsibleUserAddressList = function(nodeData) {
	var def = new dojo.Deferred();

    if (nodeData.uuid == "newNode") {
		var selectWidget = dijit.byId("addressOwner");

		var parentUuid = nodeData.parentUuid;

		if (parentUuid != null) {
			// new node && not root
			SecurityService.getResponsibleUsersForNewAddress(parentUuid, false, true, {
				callback: function(userList) {
					var list = [];
                    dojo.forEach(userList, function(user){
                        var title = UtilAddress.createAddressTitle(user.address);
                        var uuid = user.address.uuid;
                        list.push([title, uuid]);
                    });
					UtilStore.updateWriteStore("addressOwner", list, {identifier: '1',label: '0'});
					def.callback(nodeData);
				},
				errorHandler: function(errMsg, err) {
					console.debug(errMsg);
					console.debug(err);
					def.errback(err);
				}
			});
		} else {
			// new root node
	    	// get all users from the users groups that have root permission and the catalog admin
            var getUsersDef = UtilSecurity.getUsersFromCurrentGroupsWithRootPermission();
            var getCatAdminDef = UtilSecurity.getCatAdmin();

			var defList = new dojo.DeferredList([getUsersDef, getCatAdminDef], false, false, true);
			defList.addCallback(function(resultList) {
				var list = [];
				
				// Add all users from the current group
				for (var i in resultList[0][1]) {
					// Iterate over the users from the current group
					var user = resultList[0][1][i];
					var title = UtilAddress.createAddressTitle(user.address);
					var uuid = user.address.uuid;
					list.push([title, uuid]);
				}

				// Add the catalog administrator
				// only if the current user is not the cat admin himself
				if (currentUser.role != 1) {
					var catAdmin = resultList[1][1];
					var catAdminTitle = UtilAddress.createAddressTitle(catAdmin.address);
					var catAdminUuid = catAdmin.address.uuid;
					list.push([catAdminTitle, catAdminUuid]);
				}

				UtilStore.updateWriteStore("addressOwner", list, {identifier: '1',label: '0'});	
				def.callback(nodeData);
			});
			defList.addErrback(function(errMsg, err) { def.errback(err); });
		}

    	return def;
    }



	SecurityService.getUsersWithWritePermissionForAddress(nodeData.uuid, false, false, {
		callback: function(userList) {
			var list = [];
			dojo.forEach(userList, function(user){
				var title = UtilAddress.createAddressTitle(user.address);
				var uuid = user.address.uuid;
				list.push([title, uuid]);
			});
			UtilStore.updateWriteStore("addressOwner", list, {label:'0', identifier:'1'});
			def.callback(nodeData);
		},
		errorHandler: function(errMsg, err) {
			console.debug(errMsg);
			console.debug(err);
			def.errback(err);
		}
	});

	return def;	
}

// Looks for the node widget with uuid = nodeData.uuid and updates the
// tree data (label, type, etc.) according to the given nodeData
udkDataProxy._updateTree = function(nodeData, oldUuid) {
	console.debug("_updateTree("+nodeData.uuid+", "+oldUuid+")");
	if (typeof(oldUuid) == "undefined") {
		oldUuid = nodeData.uuid;
	}

	var title = "";
	var objClass; 
	if (nodeData.nodeAppType == "O") {
		//title = dojo.string.escape("html", nodeData.objectName);
		// FIXME: escape function does not exist anymore
		title = nodeData.objectName;
		objClass = nodeData.objectClass;
	} else if (nodeData.nodeAppType == "A") {
		title = UtilAddress.createAddressTitle(nodeData);
		objClass = nodeData.addressClass;
	}

	// If we change the uuid (= widgetId) of a node the treeNode has to be created again
	// because otherwise dojo doesn't 'register' the changed widgetId 
	// Currently a changed uuid is only possible if a new Node is updated.
	if (nodeData.uuid != oldUuid && oldUuid == "newNode") {
		var oldWidget = dijit.byId(oldUuid);
		var parent = oldWidget.getParent();

		var tree = dijit.byId("dataTree");//treeController");
		// A new node is a leaf node. It's safe to destroy it
		//oldWidget.destroy();
		tree.model.store.deleteItem(oldWidget.item);
        tree.model.store.save();
		
		tree.model.store.newItem(
			{
				//contextMenu: 'contextMenu1',
				isFolder: false,
				isPublished: nodeData.isPublished,
				nodeDocType: nodeData.nodeDocType,
				title: title,
				objectClass: objClass,
				dojoType: 'ingrid:TreeNode',
				nodeAppType: nodeData.nodeAppType,
				userWritePermission: nodeData.writePermission,
                userMovePermission: nodeData.movePermission,
				userWriteSinglePermission: nodeData.writeSinglePermission,
				userWriteTreePermission: nodeData.writeTreePermission,
                userWriteSubNodePermission: nodeData.writeSubNodePermission,
				userWriteSubTreePermission: nodeData.writeSubTreePermission,
				id: nodeData.uuid
			}, {parent: parent.item, attribute:"children"} );

		tree.model.store.save();
		
		var newNode = dijit.byId(nodeData.uuid);
		UtilTree.selectNode("dataTree", nodeData.uuid);
		//if (!dojo.isIE)				
		//	dojo.window.scrollIntoView(newNode.domNode);
		dojo.publish("/selectNode", [{node: newNode.item}]);

	} else {
		//alert("Get widget: " + oldUuid);
		// find the node ... check all children of tree for item.id == oldUuid!
		var node = dijit.byId(oldUuid).item;
		if (node) {
			node.nodeDocType = [nodeData.nodeDocType];
			//dijit.byId("treeDocIcons").setnodeDocTypeClass(node);
			//node.setTitle(title);
			dijit.byId("dataTree").model.store.setValue(node, "title", title);
			node.objectClass = [objClass],
			node.id = [nodeData.uuid];	
			// update permissions
			node.isPublished = [nodeData.isPublished];
			node.userWritePermission = [nodeData.writePermission];
            node.userMovePermission = [nodeData.movePermission];
			node.userWriteSinglePermission = [nodeData.writeSinglePermission];
			node.userWriteTreePermission = [nodeData.writeTreePermission];
            node.userWriteSubNodePermission = [nodeData.writeSubNodePermission];
			node.userWriteSubTreePermission = [nodeData.writeSubTreePermission];
			dojo.publish("/selectNode", [{node: node}]);
		} else {
			console.debug("Error in _updateTree: TreeNode widget not found. ID: "+nodeData.uuid);
		}
	}
}

// Returns an array representing the data of the table with name 'tableName'
// The keys are stored in the fields named: 'Id' 
udkDataProxy._getTableData = function(tableName)
{
    var widget = dijit.byId(tableName);
    // if it's a slickgrid
    if (widget == undefined) {
        return gridManager[tableName].getData();
    } else {
        //var store = widget.store;
        //return UtilStore.convertItemsToJS(store, store._arrayOfTopLevelItems);
    	return widget.getData();
    }
	//return dijit.byId(tableName).store._arrayOfTopLevelItems;
}

var igeEvents = {};
var toggleContainer = ["refClass1", "refClass1DQ", "refClass2", "refClass3", "refClass4", "refClass5", "refClass6"];
var toggleContainerPrefix = "ref";
var toggleContainerAddress = ["headerAddressType0", "headerAddressType1", "headerAddressType2", "headerAddressType3"];
var toggleContainerAddressPrefix = "header";

igeEvents.handleSelectNode = function(message) {
  if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
	dojo.style("sectionTopObject", "display", "none");
	dojo.style("contentFrameBodyObject", "display", "none");
	dojo.style("sectionTopAddress", "display", "none");
	dojo.style("contentFrameBodyAddress", "display", "none");
    dojo.style("contentNone", "display", "block");
  }
  else if (message.node.nodeAppType == "A") {
  	dojo.style("sectionTopObject", "display", "none");
	dojo.style("contentFrameBodyObject", "display", "none");
	dojo.style("sectionTopAddress", "display", "block");
	dojo.style("contentFrameBodyAddress", "display", "block");
    dojo.style("contentNone", "display", "none");
  }
  else if (message.node.nodeAppType == "O") {
  	dojo.style("sectionTopObject", "display", "block");
	dojo.style("contentFrameBodyObject", "display", "block");
	dojo.style("sectionTopAddress", "display", "none");
	dojo.style("contentFrameBodyAddress", "display", "none");
	dojo.style("contentNone", "display", "none");
  }
  //sizeContent();
}

igeEvents.selectUDKClass = function() {
	var val = dijit.byId("objectClass").getValue();
	if (val) {
	    igeEvents.setSelectedClass(val);
		igeEvents.refreshTabContainers("contentFrameBodyObject");
        dojo.publish("/onObjectClassChange", [{objClass: val}]);
	}
}

igeEvents.selectUDKAddressType = function(addressType) {
    console.debug("change addressType" + addressType);
	var val = UtilAddress.getAddressClass(addressType);
	if (val != -1) {
		igeEvents.setSelectedClass("AddressType"+val);
		igeEvents.refreshTabContainers("contentFrameBodyAddress");
	}
}

igeEvents.setSelectedClass = function(/* name of the object class/address type */clazz) {
	console.debug("selected class: " + clazz);
	var div;
    var isObjectClass = true;
	// hide all classes first
	// and show new one
	if (clazz.indexOf("AddressType") == -1) {
		dojo.forEach(toggleContainer, function(container){
            dojo.addClass(container, "hide");
		});
		div = dojo.byId(toggleContainerPrefix + clazz);
	} else {
		dojo.forEach(toggleContainerAddress, function(container){
            dojo.addClass(container, "hide");
		});
		div = dojo.byId(toggleContainerAddressPrefix + clazz);
        isObjectClass = false;
	}
	
	//var div = dojo.byId(toggleContainerPrefix + clazz);
    // Class0 not exists as widget (means no Fachbezug) so we have to check here
	if (div)
      dojo.removeClass(div, "hide");
      
    // hide section 'Verfuegbarkeit' if 'Organisationseinheit/Fachaufgabe' (Class0) is selected
    // fields do not need to be emptied, since it's not mapped in MDEK-Mapper for class 0! 
    var availabilityContainer = dojo.byId('availability');
    if (availabilityContainer) {
      if (clazz == "Class0")
        dojo.addClass(availabilityContainer, "hide");
      else if (isObjectClass)
        dojo.removeClass(availabilityContainer, "hide");
    }
    
    // show conformity-table only for class 1 and 3 
    if (clazz == "Class1" || clazz == "Class3") {
        dojo.removeClass("uiElementN024", "hide");
        dojo.addClass("uiElementN024", "required");
    } else if (isObjectClass) {
        dojo.removeClass("uiElementN024", "required");
        dojo.addClass("uiElementN024", "hide");
        UtilGrid.setTableData("extraInfoConformityTable", []);
    }

       // class specials !
    
    // Fields only mandatory for Geoinformation/Karte(1)
    // NOTICE: div excluded from normal show/hide mechanism (displaytype="exclude")
    if (clazz == "Class1") {
        // "Kodierungsschema der geographischen Daten" 
        UtilUI.setRequiredState(dojo.byId("uiElement1315"), true);
        dojo.removeClass("uiElement1315", "hide");

        // show / hide DQ input dependent from INSPIRE Thema !
        applyRule7();

        // "ISO-Themenkategorie" only mandatory in class 1 
        UtilUI.setRequiredState(dojo.byId("uiElement5060"), true);

    } else if (isObjectClass) {
       // "Kodierungsschema der geographischen Daten" only in class 1
        UtilUI.setRequiredState(dojo.byId("uiElement1315"), false);
        dojo.addClass("uiElement1315", "hide");
        dijit.byId("availabilityDataFormatInspire").set("value", "");

        // "INSPIRE-Thema" also in other classes
        //UtilUI.setRequiredState(dojo.byId("uiElement5064"), false);
        // DO NOT HIDE to avoid vanishing field ...

        UtilUI.setRequiredState(dojo.byId("uiElement5060"), false);
        // DO NOT HIDE to avoid vanishing field ...
    }

    // Fields only mandatory for Geoinformation/Karte(1) and Geodatendienst(3)
    if (clazz == "Class1" || clazz == "Class3") {
        // "Raumbezugssystem"
        UtilUI.setRequiredState(dojo.byId("uiElement3500"), true);
        //dojo.style("uiElement3500", "display", "block");
        
        // "INSPIRE-Thema"
        UtilUI.setRequiredState(dojo.byId("uiElement5064"), true);
        dojo.removeClass("uiElement6000", "hide");
        
        // change general address label
        setGeneralAddressLabel(true);

    } else if (isObjectClass) {
        UtilUI.setRequiredState(dojo.byId("uiElement3500"), false);
        // DO NOT HIDE to avoid vanishing field ...
        
        // "INSPIRE-Thema"
        UtilUI.setRequiredState(dojo.byId("uiElement5064"), false);
        // no reset needed -> handled in MDEK-Mapper
        dojo.addClass("uiElement6000", "hide");
        
        // change general address label
        setGeneralAddressLabel(false);
    }
    
    // Fields for Geo-Information/Karte(1), Dokument/Bericht/Literatur(2)and Datensammlung/Datenbank(5)
    if (clazz == "Class1" || clazz == "Class2" || clazz == "Class5") {
        // "Zeichensatz des Datensatzes" 
        var isRequired = false;
        if (this.selectedClass == "Class1") {
          isRequired = true;
        }
        UtilUI.setRequiredState(dojo.byId("uiElement5043"), isRequired);

    } else if (isObjectClass) {
       // "Zeichensatz des Datensatzes" only in class 1,2,5
        UtilUI.setRequiredState(dojo.byId("uiElement5043"), false);
    }
    
    // fields that are not stored in class 3 and 6
    if (clazz == "Class3" || clazz == "Class6") {
        dojo.addClass("uiElement5060", "hide");
        dojo.addClass("uiElement5043", "hide");
        dojo.addClass("uiElement5042", "hide");
    } else if (isObjectClass) {
        dojo.removeClass("uiElement5060", "hide");
        dojo.removeClass("uiElement5043", "hide");
        dojo.removeClass("uiElement5042", "hide");
    }

}

igeEvents.refreshTabContainers = function(section) {
    dojo.query(".required .dijitTabContainer", section).forEach(function(node) { dijit.byId(node.id).resize(); });
    dojo.query(".expanded .optional .dijitTabContainer", section).forEach(function(node) { dijit.byId(node.id).resize(); });
}

igeEvents.toggleFields = function(section, /* optional */ mode, /* optional flag */ refreshContainers) {
//	dojo.debug("toggleFields("+section+", "+mode+")");

	if (typeof(refreshContainers) == "undefined") {
		refreshContainers = true;
	}

	var sectionElement;
	var allSpanElements;
	var allDivElements;

	if (typeof(section) != "undefined" && section != "Object" && section != "Address") {
		sectionElement = dojo.byId(section);
		allSpanElements = sectionElement.getElementsByTagName("span");
		allDivElements = sectionElement.getElementsByTagName("div");

//		dojo.debug("number of div elements: "+allDivElements.length);
//		dojo.debug("number of span elements: "+allSpanElements.length);

		if (typeof(mode) == "undefined") {
			if (dojo.hasClass(sectionElement, "expanded")) {
				mode = "showRequired";
			} else {
				mode = "showAll";
			}			
		}

		if (mode == "showAll") {
            dojo.addClass(sectionElement, "expanded");
            // refresh grids and tab container when making visible
            dojo.query(".optional .dijitTabContainer", sectionElement).forEach(function(node) { dijit.byId(node.id).resize(); });

		} else if (mode == "showRequired") {
            dojo.removeClass(sectionElement, "expanded");
		}

//		dojo.debug("mode: "+mode);
//		dojo.debug("sectionElement will be expanded: "+sectionElement.isExpanded);

		if (sectionElement.getElementsByTagName('img')[0] && sectionElement.getElementsByTagName('img')[0].src.indexOf("expand") != -1) {
			var btnImage = sectionElement.getElementsByTagName('img')[0];
			var link = sectionElement.getElementsByTagName('a')[0];
			this.toggleButton(section, mode);
		}

	} else {
		var sectionDivId;

		if (section == "Address" || currentUdk && currentUdk.nodeAppType == "A") {
			sectionDivId = "contentFrameBodyAddress";
  		} else {
			sectionDivId = "contentFrameBodyObject";
  		}
		var sectionDiv = dojo.byId(sectionDivId);

		if (typeof(sectionDiv.isExpanded) == "undefined") {
			sectionDiv.isExpanded = true;
		}

		if (sectionDiv.isExpanded == false) {
			mode = "showAll";
			sectionDiv.isExpanded = true;

		} else {
			mode = "showRequired";
			sectionDiv.isExpanded = false;			
		}

		var toggleBtn = dijit.byId('toggleFieldsBtn');
		var btnImage = toggleBtn.domNode.getElementsByTagName('img')[0];
		this.toggleButton(section, mode);

		dojo.query(".rubric", sectionDivId).forEach(function(section) {
//			console.log("calling toggleFields("+section.id+", "+mode+").");
			igeEvents.toggleFields(section.id, mode, false);
		});
		
		return;
	}
    
}

igeEvents.toggleButton = function(section, mode, labelElement) {
    console.debug("check " + section);
    
    if (!section) {
        console.debug("it is undefined");
        var divIcon = dojo.query("#toggleFieldsBtn .tabIconExpand")[0];
        if (mode == "showAll") {
            dojo.addClass(divIcon, "tabIconExpandOn");
        } else {
            dojo.removeClass(divIcon, "tabIconExpandOn");
        }
    }      
}

igeEvents.executeSearch = function() {
	var _this = this;
	var termList = UtilThesaurus.parseQueryTerm(this._inputFieldWidget.getValue());
	var inspireTopics = [];

	// For object search terms...
	if (_this.id == "thesaurusFreeTermsAddButton" && termList && termList.length > 0) {
		// Check if the termList contains inspire topics
		// If so, tell the user and wait for confirmation. Then proceed with the normal search
		termList = dojo.filter(termList, function(t) {
			if (UtilThesaurus.isInspireTopic(t)) {
				inspireTopics.push(t);
				return false;
			} else {
				return true;
			}
		});
	}
	console.debug("term list: "+termList);
	console.debug("inspire topics: "+inspireTopics);

	var def = new dojo.Deferred();
	// add inspire topics to the list
	if (inspireTopics.length > 0) {
		UtilThesaurus.addInspireTopics(inspireTopics);
		var displayText = dojo.string.substitute(message.get("dialog.addInspireTopics.message"), [inspireTopics.join(", ")]);
		dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.INFO, [
		    { caption: message.get("general.ok"), action: function() { return def.callback(); } }
		]);
	} else {
		// Otherwise just execute the callback to continue
		def.callback();
	}

	if (termList && termList.length > 0) {
		def.addCallback(function() {
			// search for topics in SNS and put results into findTopicDefList
			var findTopicDefList = [];
			for (var i = 0; i < termList.length; ++i) {
				findTopicDefList.push(UtilThesaurus.findTopicsDef(termList[i]));
			}

			UtilDWR.enterLoadingState();
			var defList = new dojo.DeferredList(findTopicDefList, false, false, true);
			defList.addErrback( function(err) { UtilDWR.exitLoadingState(); handleFindTopicsError(err); } );
			defList.addCallback(function(resultList) {
				UtilDWR.exitLoadingState();
				var snsTopicList = [];
				//for (var i = 0; i < resultList[0][1].length; ++i) {
                dojo.forEach(resultList, function(item) {
					// TODO handle sns timeout errors?
					snsTopicList.push(item[1]);
				});

				UtilThesaurus.handleFindTopicsResult(termList, snsTopicList, _this._termListWidget);
			});
		});
	}
	
	// empty field with user added entries
	this._inputFieldWidget.attr("value", "", true);
}

igeEvents.disableInputOnWrongPermission = function() {
    UtilUI._uiElementsActiveA = true;
    UtilUI._uiElementsActiveO = true;

    dojo.subscribe("/selectNode", function(message) {
        var hasWritePermission = message.node.userWritePermission[0];
        console.debug("received notification: handle permission on form: " + hasWritePermission);

        var nodeType = message.node.nodeAppType[0].toUpperCase();
        var activeDiv = "contentFrameBodyObject";
        
        if (nodeType == "A")
            activeDiv = "contentFrameBodyAddress";
        else if (nodeType != "O")
            return;
        
        if (hasWritePermission) {
            if (!UtilUI["_uiElementsActive"+nodeType]) {
                // elements that always shall be disabled
                var ignoreElements = ["headerAddressType1Institution", "headerAddressType2Institution"];
                
                var allInputs = dojo.query(".required .input >, .optional .input >, .show .input >", activeDiv);
                dojo.forEach(allInputs, function(input) { 
                    if (dojo.indexOf(ignoreElements, input.id) != -1)
                        return;
                        
                    if (UtilGrid.getTable(input.id)) {
                        UtilGrid.updateOption(input.id, "editable", true)
                    }
                    else {
                        var widget = dijit.getEnclosingWidget(input);
                        // is it a table element then disable input differently
                        //if (widget.structure) //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!SlickGrid
                        //    widget.set('_canEdit', true);
                        //else
                        widget.set("disabled", false);
                    }
                });
                
                // enable links
                dojo.query(".functionalLink a", activeDiv).forEach(UtilUI.enableHtmlLink);
                
                // enable all buttons
                igeEvents._toggleButtonsAccessibility(false);
                
                // enable header
                igeEvents._toggleHeaderAccessibility(false);

                UtilUI["_uiElementsActive"+nodeType] = true;
            }
            
        } else {
            if (UtilUI["_uiElementsActive"+nodeType]) {
                var allInputs = dojo.query(".required .input >, .optional .input >, .show .input >", activeDiv);
                dojo.forEach(allInputs, function(input) { 
                    if (UtilGrid.getTable(input.id)) {
                        UtilGrid.updateOption(input.id, "editable", false)
                    }
                    else {
                        var widget = dijit.getEnclosingWidget(input);
                        // is it a table element then disable input differently
                        //if (widget.structure)
                        //    widget.set('_canEdit', false);
                        //else
                        widget.set("disabled", true);
                    }
                });
                
                // disable links
                dojo.query(".functionalLink a", activeDiv).forEach(UtilUI.disableHtmlLink);
                
                // disable all buttons
                igeEvents._toggleButtonsAccessibility(true);
                
                // disable header
                igeEvents._toggleHeaderAccessibility(true);
                
                UtilUI["_uiElementsActive"+nodeType] = false;
            }
        }
    });
}

igeEvents._toggleButtonsAccessibility = function(/*boolean*/disable) {
    var widgets = ["thesaurusFreeTermsAddButton","thesaurusFreeTermsAddressAddButton"]
    dojo.forEach(widgets, function(w) {
        var wid = dijit.byId(w);
        if (wid)
            wid.set("disabled", disable);
    });
}

igeEvents._toggleHeaderAccessibility = function(/*boolean*/disable) {
    var widgets = ["objectName","objectClass","objectOwner",/*"addressTitle","addressType",*/"addressOwner"];
    
    dojo.forEach(widgets, function(w) {
        var wid = dijit.byId(w);
        if (wid)
            wid.set("disabled", disable);
    });
}

igeEvents.nextError = 0;
igeEvents.showNextError = function() {
	var errors = dojo.query(".importantBackground");
	
	if (errors == 0) {
		// remove error message
		return;
	}
	
	// scroll to the position
	dojo.window.scrollIntoView(errors[igeEvents.nextError % errors.length]);
	
	// update error menu entry in toolbar
	
}
