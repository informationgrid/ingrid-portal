// the root mdek object
// holds all states and encapsulates common functions
if(dj_undef("mdek", this)){
	var mdek = {};
}
var mdek = {};

dojo.addOnLoad(function()
{
  // initialite debug console if necessary
  if (djConfig.isDebug)
  {
    dojo.debug("The current version of dojo is: ", dojo.version.toString());
    var console = dojo.byId("dojoDebugConsole");
    console.style.visibility = "visible";
  }

  var def = initCatalogData();
  def.addCallback(initCurrentUser);
  def.addCallback(initCurrentGroup);
  def.addCallback(initGeneralEventListener);
  def.addCallback(initToolbar);
  def.addCallback(disableInputOnWrongPermission);
  def.addCallback(initTree);
  def.addCallback(initTableValidators);
  def.addCallback(initCTS);
  def.addCallback(initFreeTermsButtons);
  def.addCallback(initReferenceTables);
  def.addCallback(initSysLists);
  def.addCallback(initOptionalFieldStates);
  def.addCallback(initForm);
  def.addCallback(initMenu);
  def.addCallback(hideSplash);	// hide the splash after everything is loaded
  def.addCallback(function() { udkDataProxy.resetDirtyFlag(); });
  def.addCallback(jumpToNodeOnInit);
  // NOTE: the undo button enable / disable function is connected to the set / resetDirtyFlag.
  // If the function is directly used as a callback (def.addCallback(resetDirtyFlag)), the connected
  // functions are never fired (dojo bug?).
  // If we create a new function that does nothing but call resetDirtyFlag, the connected functions are
  // called properly
  def.addCallback(function() { udkDataProxy.resetDirtyFlag(); });

  def.addErrback(function(err){ dojo.debug("Error: "+err); dojo.debugShallow(err); });
});

function initMenu() {
	// Hide page3Subnavi2 if QA is deactivated
	if (!UtilQA.isQAActive() || !UtilSecurity.isCurrentUserQA()) {
		dojo.byId("page3Subnavi2").style.display = "none";
	}
}

function initForm() {
  // hide address and object panel after initialization
  // this must be done because if the panels are hidden by default, the select and comboboxes will init forever
  dojo.byId("contentAddress").style.display="none";
  dojo.byId("contentObject").style.display="none";
  dojo.byId("contentNone").style.display="block";

  // attach eventhandler to object/address type combobox and select default value
  var objectSelectBox = dojo.widget.byId("objectClass");
  if (objectSelectBox) {
    dojo.event.connect(objectSelectBox, "onValueChanged", "selectUDKClass");
    objectSelectBox.setValue("Class0");
  }
  var addressSelectBox = dojo.widget.byId("addressType");
  if (addressSelectBox) {
    dojo.event.connect(addressSelectBox, "setValue", "selectUDKAddressType");
    addressSelectBox.setValue("Institution");
  }

  // set context menu on object owner listbox
/*
  var objectOwnerWidget = dojo.widget.byId('objectOwner');
  if (objectOwnerWidget) {
    var objectOwnerCM = dojo.widget.createWidget("ingrid:ContextMenu");
    objectOwnerCM.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
    objectOwnerCM.bindDomNode(objectOwnerWidget.domNode);
    objectOwnerWidget.textInputNode.style.cursor='pointer';
  }
*/

  // set context menu on last editor label
/*
  // TODO Fixme!
  var lastEditorLabel = dojo.byId('lastEditor');
  if (lastEditorLabel) {
    var lastEditorCM = dojo.widget.createWidget("ingrid:ContextMenu");
    lastEditorCM.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
    lastEditorCM.bindDomNode(lastEditorLabel);
    lastEditorLabel.style.cursor='pointer';
  }
*/
  // size right content height
  dojo.event.connect(window, "onresize", window, "sizeContent");
  sizeContent();

  var contentForm = dojo.widget.byId('contentFormObject');  
  if (contentForm) {
    contentForm.initForm();
	toggleFields("Object");
  }
  var contentForm = dojo.widget.byId('contentFormAddress');  
  if (contentForm) {
    contentForm.initForm();
	toggleFields("Address");
  }

  // Init the contentBlocks
  var contentBlockIds = ["generalContent", "ref1Content", "ref2Content", "ref3Content", "ref4Content", "ref5Content",
  			"spatialRefContent", "timeRefContent", "extraInfoContent", "availabilityContent", "thesaurusContent", "linksContent",
  			"headerAddressType0Content", "headerAddressType1Content", "headerAddressType2Content", "headerAddressType3Content",
  			"address", "adrThesaurusContent", "associatedObjContent"];

	dojo.lang.forEach(contentBlockIds, function(divId) {
		dojo.byId(divId).isExpanded = false;
	});
}


function initTree() {

	// initially load data (first hierarchy level) from server 
	TreeService.getSubTree(null, null, 1, 
		function (str) {
			var tree = dojo.widget.byId('tree');
			tree.setChildren(str);
		}
	);

/*
  -- Event Handling has been moved to a separate class 'menuEventHandler' -- 

  var contextMenu1 = dojo.widget.byId('contextMenu1');
  contextMenu1.treeController = dojo.widget.byId('treeController');
  contextMenu1.addItem(message.get('tree.nodeNew'), 'addChild', function(menuItem) {createItemClicked(menuItem)});
  contextMenu1.addItem(message.get('tree.nodePreview'), 'open', 'previewItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem(message.get('tree.nodeCut'), 'cut', 'cutItemClicked');
  contextMenu1.addItem(message.get('tree.nodeCopySingle'), 'copy', 'copySingleItemClicked');
  contextMenu1.addItem(message.get('tree.nodeCopy'), 'copy', 'copyItemClicked');
  contextMenu1.addItem(message.get('tree.nodePaste'), 'paste', 'pasteItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem(message.get('tree.nodeMarkDeleted'), 'detach', 'markDeletedItemClicked');
//  contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', 'deleteItemClicked');

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem(message.get('tree.nodeNew'), 'addChild', function(menuItem) {createItemClicked(menuItem)});
*/

  var contextMenu1 = dojo.widget.byId('contextMenu1');
  contextMenu1.treeController = dojo.widget.byId('treeController');
  contextMenu1.addItem(message.get('tree.nodeNew'), 'addChild', menuEventHandler.handleNewEntity);
  contextMenu1.addItem(message.get('tree.nodePreview'), 'open', menuEventHandler.handlePreview);
  contextMenu1.addSeparator();
  contextMenu1.addItem(message.get('tree.nodeCut'), 'cut', menuEventHandler.handleCut);
  contextMenu1.addItem(message.get('tree.nodeCopySingle'), 'copy', menuEventHandler.handleCopyEntity);
  contextMenu1.addItem(message.get('tree.nodeCopy'), 'copy', menuEventHandler.handleCopyTree);
  contextMenu1.addItem(message.get('tree.nodePaste'), 'paste', menuEventHandler.handlePaste);
  contextMenu1.addSeparator();
  if (UtilQA.isQAActive() && !UtilSecurity.isCurrentUserQA()) {
	contextMenu1.addItem(message.get('tree.nodeMarkDeleted'), 'detach', menuEventHandler.handleMarkDeleted);
  } else {
	contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', menuEventHandler.handleDelete);
  }

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem(message.get('tree.nodeNew'), 'addChild', menuEventHandler.handleNewEntity);
  contextMenu2.addItem(message.get('tree.nodePaste'), 'paste', menuEventHandler.handlePaste);
  contextMenu2.addSeparator();
  contextMenu2.addItem(message.get('tree.reload'), 'reload', menuEventHandler.reloadSubTree);



  var tree = dojo.widget.byId("tree");

  // TODO Was macht diese Verbindung?? Die 'onValueChanged' Tree Methode bzw. das Event gibt es nicht...
//  if (tree)
//    dojo.event.connect(tree, "onValueChanged", "selectUDKClass");

  // attach node selection handler
  var treeListener = dojo.widget.byId('treeListener');
  dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelected");

  // Load children of the node from server
  // Overwritten to work with dwr.
  var treeController = dojo.widget.byId('treeController');
  treeController.loadRemote = function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		TreeService.getSubTree(node.id, node.nodeAppType, 1, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
  			callback:function(res) { deferred.callback(res); },
//			timeout:10000,
			errorHandler:function(message) {
				UtilDWR.exitLoadingState();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});
		
		deferred.addCallback(function(res) {
			dojo.lang.forEach(res, function(obj){ obj.title = dojo.string.escape("html", obj.title); });
			return _this.loadProcessResponse(node,res);
		});
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
		return deferred;
	};
}

// Initialize the Coordinate Transformation Service
function initCTS() {
	
	/* Coordinate Update Handler.
	 * This object connects two tables:
	 * A source table containing coordinates (Coordinate objects).
	 * A destination table containing Coordinate objects and a srs (Spatial Reference System) entry.
	 * When the source table onSelect method is invoked the update handler does the following things:
	 *  1. updateCoordinates() is called.
	 *     This Method fetches the currently selected object from the source table and all
	 *     srs identifiers from the target table. For each entry in the target table a coordinate transformation
	 *     request is sent to the CT Service.
	 *  2. The result of the CT Service request is redirected to the updateDestinationStore method.
	 *     This method gets the correct entry from the target table and updates the stored Coordinate
	 *
	 * args is an object containing two values:  args = {srcTable: FilteringTable, dstTable: FilteringTable}
	 *  srcTable - The table containing the coordinates which should be converted
	 *  dstTable - The 'slave' table containing transformed coordinates
	 *
	 * Usage:
	 *  simply create a new CoordinateUpdateHandler like this:
	 *   new CoordinateUpdateHandler({srcTable: dojo.widget.byId("src"), dstTable: dojo.widget.byId("dst")});
	 *
	 */
	function CoordinateUpdateHandler(args){
		this.srcTable = args.srcTable;
		this.srcSelect = args.srcSelect;
		this.dstTable = args.dstTable;
		dojo.event.connectOnce(this.srcTable, "onSelect", this, "updateCoordinates");
		dojo.event.connectOnce(this.srcSelect, "onValueChanged", this, "updateCoordinates");

		this.updateDestinationStore = function(ctsResponse) {
			var data = this.dstTable.store.getData()[0];
			this.dstTable.store.update(data, "longitude1", ctsResponse.coordinate.longitude1);
			this.dstTable.store.update(data, "latitude1", ctsResponse.coordinate.latitude1);
			this.dstTable.store.update(data, "longitude2", ctsResponse.coordinate.longitude2);
			this.dstTable.store.update(data, "latitude2", ctsResponse.coordinate.latitude2);
		}

		this.clearDstStore = function() {
			var data = this.dstTable.store.getData()[0];
			var clearValue = "";
			this.dstTable.store.update(data, "longitude1", clearValue);
			this.dstTable.store.update(data, "latitude1", clearValue);
			this.dstTable.store.update(data, "longitude2", clearValue);
			this.dstTable.store.update(data, "latitude2", clearValue);
		}

		this.showError = function() {
			var data = this.dstTable.store.getData()[0];
			var clearValue = "D.n.e.";
			this.dstTable.store.update(data, "longitude1", clearValue);
			this.dstTable.store.update(data, "latitude1", clearValue);
			this.dstTable.store.update(data, "longitude2", clearValue);
			this.dstTable.store.update(data, "latitude2", clearValue);
		}

		this.updateCoordinates = function() {
			var fromSRS = "GEO84";
			var selectedData = this.srcTable.getSelectedData();
			this.clearDstStore();
			if (selectedData.length != 1) {
				return;
			}
			var coords = selectedData[0];
			var dstStore = this.dstTable.getData();
			var toSRS = this.srcSelect.getValue();
			if (coords && toSRS
					&& dojo.validate.isRealNumber(coords.longitude1)
					&& dojo.validate.isRealNumber(coords.longitude2)
					&& dojo.validate.isRealNumber(coords.latitude1)
					&& dojo.validate.isRealNumber(coords.latitude2)) {
//				dojo.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
				var _this = this;
				CTService.getCoordinates(fromSRS, toSRS, coords, {
//						preHook: UtilDWR.enterLoadingState,
//						postHook: UtilDWR.exitLoadingState,
						callback: dojo.lang.hitch(this, this.updateDestinationStore),
						timeout:8000,
						errorHandler:function(message) {
							UtilDWR.exitLoadingState();
							dojo.debug(message);
							_this.showError();
						}
					}
				);
			}
		}
	}	// StoreUpdateHandler

	
	// Connect all coordinate tables:
	new CoordinateUpdateHandler({
		srcTable: dojo.widget.byId("spatialRefAdminUnit"),
		srcSelect: dojo.widget.byId("spatialRefAdminUnitSelect"),
		dstTable: dojo.widget.byId("spatialRefAdminUnitCoords")
	});

	new CoordinateUpdateHandler({
		srcTable: dojo.widget.byId("spatialRefLocation"),
		srcSelect: dojo.widget.byId("spatialRefLocationSelect"),
		dstTable: dojo.widget.byId("spatialRefLocationCoords")
	});

}


function initFreeTermsButtons() {
	// This function executes a search for topics and adds the results to the specified lists
	var executeSearch = function() {
		var term = this._inputFieldWidget.getValue();
		term = dojo.string.trim(term);
		_this = this;
		if (term.length != 0) {
			SNSService.findTopics(term, {
				preHook: UtilDWR.enterLoadingState,
				postHook: UtilDWR.exitLoadingState,
				callback:function(topics) {
					// Remove all non-descriptors from the list
					var descriptors = [];
					var queryTerm = null;
					dojo.lang.forEach(topics, function(item){
//						dojo.debug("topic: ["+item.title+", "+item.type+"]");
						// Check if a term from the result is equal to our search query 
						if (term.toLowerCase() == item.title.toLowerCase()) {
							// The search term was found in the returned list. Save its type

							// Check if the queryTerm was already found. A term can exist as multiple types
							// If the item type is a descriptor, use it instead of the current stored item.
							// If the item is a non_descriptor and the stored item is != descriptor, use the non_descriptor
							// Otherwise ignore the item
							if (queryTerm == null) {
								queryTerm = item;								

							} else {
								if (item.type == "DESCRIPTOR") {
									queryTerm = item;
								
								} else if (item.type == "NON_DESCRIPTOR" && queryTerm.type != "DESCRIPTOR") {
									queryTerm = item;
								}
							}
						}

						// Filter all the descriptors from the result
						if (item.type == "DESCRIPTOR") {
							descriptors.push(item);
						}
					});

					// Decide what to do based on the type of the query
					// Change from http://jira.media-style.com/browse/INGRIDII-131:
					// If the term is a top term or node label, add it to the list of free terms without comment
					if (queryTerm == null || queryTerm.type == "TOP_TERM" || queryTerm.type == "NODE_LABEL") {
						// The searchTerm was not found. Simply add it to the list of free terms if it doesn't already exist
						var freeTermsStore = _this._freeTermListWidget.store;
						if (dojo.lang.every(freeTermsStore.getData(), function(item){ return item.title != term; })) {
							// If every term in the store != the entered term add it to the list
							var identifier = UtilStore.getNewKey(freeTermsStore);							
							freeTermsStore.addData( {Id: identifier, title: term} );

							// Scroll to the added descriptor
							var rows = _this._freeTermListWidget.domNode.tBodies[0].rows;
							if (!dojo.render.html.ie)				
								dojo.html.scrollIntoView(rows[rows.length-1]);
//							dialog.show(message.get("general.hint"), message.get("sns.freeTermAddHint"), dialog.INFO);			
						}
/*
					} else if (queryTerm.type == "TOP_TERM") {
						// A top term can't be added to the free terms list. Show an info dialog and clear the input field
						dialog.show(message.get("general.hint"), dojo.string.substituteParams(message.get("sns.freeTermAddTopTermHint"), term), dialog.INFO);			
					
					} else if (queryTerm.type == "NODE_LABEL") {
						// A node label can't be added to the free terms list. Show an info dialog and clear the input field
						dialog.show(message.get("general.hint"), dojo.string.substituteParams(message.get("sns.freeTermAddNodeLabelHint"), term), dialog.INFO);			
*/				
					} else if (queryTerm.type == "DESCRIPTOR") {
						// The search term is a descriptor. Show a dialog to query the user if the topic should be added to the topic list
						var topicStore = _this._termListWidget.store;
						var deferred = new dojo.Deferred();
						// If the user decides to add the topic:
						deferred.addCallback(function() {
							if (dojo.lang.every(topicStore.getData(), function(item){ return item.topicId != queryTerm.topicId; })) {
								// Topic is new. Add it to the topic list
								topicStore.addData( {Id: UtilStore.getNewKey(topicStore), topicId: queryTerm.topicId, title: queryTerm.title} );
								// Scroll to the added descriptor
								var rows = _this._termListWidget.domNode.tBodies[0].rows;
								if (!dojo.render.html.ie)				
									dojo.html.scrollIntoView(rows[rows.length-1]);
							}
						});

						// Show the dialog
//						dialog.showPage(message.get("dialog.addDescriptorTitle"), "mdek_add_descriptor_dialog.html", 342, 220, true, {descriptorTitle: term, resultHandler: deferred});
						var displayText = dojo.string.substituteParams(message.get("dialog.addDescriptorMessage"), term);
						dialog.show(message.get("dialog.addDescriptorTitle"), displayText, dialog.INFO, [
/*                        	{ caption: message.get("general.no"),  action: function() { deferred.errback(); } },	*/
                        	{ caption: message.get("general.ok"), action: function() { deferred.callback(); } }
						]);

					} else if (queryTerm.type == "NON_DESCRIPTOR") {
						// Show the 'add descriptors' dialog. The user can decide if he wants to add all the found descriptors to the topic list.
						var topicStore = _this._termListWidget.store;
						var deferred = new dojo.Deferred();
						deferred.addCallback(function() {
							dojo.lang.forEach(descriptors, function(topic) {
								if (dojo.lang.every(topicStore.getData(), function(item){ return item.topicId != topic.topicId; })) {
									// Topic is new. Add it to the topic list
									topicStore.addData( {Id: UtilStore.getNewKey(topicStore), topicId: topic.topicId, title: topic.title} );

									// Scroll to the added descriptor
									var rows = _this._termListWidget.domNode.tBodies[0].rows;
									if (!dojo.render.html.ie)				
										dojo.html.scrollIntoView(rows[rows.length-1]);
								}
							});
						});

						// Show the dialog
						dialog.showPage(message.get("dialog.addDescriptorsTitle"), "mdek_add_descriptors_dialog.html", 360, 240, true, {descriptorTitle:queryTerm.title, descriptors:descriptors, resultHandler:deferred});

						// Also add the queryTerm to the list of free terms
						var freeTermsStore = _this._freeTermListWidget.store;
						if (dojo.lang.every(freeTermsStore.getData(), function(item){ return item.title != queryTerm.title; })) {
							// If every term in the store != the entered term add it to the list
							var identifier = UtilStore.getNewKey(freeTermsStore);							
							freeTermsStore.addData( {Id: identifier, title: queryTerm.title} );

							// Scroll to the added descriptor
							var rows = _this._freeTermListWidget.domNode.tBodies[0].rows;
							if (!dojo.render.html.ie)				
								dojo.html.scrollIntoView(rows[rows.length-1]);
						}
					}
					// Clear the input field
					_this._inputFieldWidget.setValue("");

				},
				timeout:8000,
				errorHandler:function(msg) {
					UtilDWR.exitLoadingState();
					dojo.debug("Error while executing SNSService.findTopics");
				}
			});
		}	
	}

	// Add the function(s) to the button in the object form
	var button = dojo.widget.byId("thesaurusFreeTermsAddButton");

	var inputField = dojo.widget.byId("thesaurusFreeTerms");
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER && currentUdk.writePermission) {
                dojo.widget.byId("thesaurusFreeTermsAddButton").onClick();
            }
        });

	button._inputFieldWidget = dojo.widget.byId("thesaurusFreeTerms");
	button._termListWidget = dojo.widget.byId("thesaurusTerms");
	button._freeTermListWidget = dojo.widget.byId("thesaurusFreeTermsList");
	button.onClick = executeSearch;


	// Add the function(s) to the button in the address form
	button = dojo.widget.byId("thesaurusFreeTermsAddressAddButton");

	inputField = dojo.widget.byId("thesaurusFreeTermInputAddress");
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER && currentUdk.writePermission) {
                dojo.widget.byId("thesaurusFreeTermsAddressAddButton").onClick();
            }
        });

	button._inputFieldWidget = dojo.widget.byId("thesaurusFreeTermInputAddress");
	button._termListWidget = dojo.widget.byId("thesaurusTermsAddress");
	button._freeTermListWidget = dojo.widget.byId("thesaurusFreeTermsListAddress");
	button.onClick = executeSearch;

    dojo.event.connect(udkDataProxy, "onAfterLoad", function() { dojo.widget.byId("thesaurusFreeTerms").setValue(""); });
    dojo.event.connect(udkDataProxy, "onAfterLoad", function() { dojo.widget.byId("thesaurusFreeTermInputAddress").setValue(""); });
}


function initReferenceTables() {
	initObjectReferenceTables();
	initAddressReferenceTables();
}

function initObjectReferenceTables() {
	var mainStore = dojo.widget.byId("linksTo").store;

	var filterTableMap =
		[{tableId: "ref1SymbolsLink", 		filterId: 3555},		// 3555
		 {tableId: "ref1KeysLink", 			filterId: 3535},		// 3535
		 {tableId: "ref1ServiceLink", 		filterId: 5066},		// 5066
		 {tableId: "ref1BasisLink", 		filterId: 3520},		// 3520
		 {tableId: "ref1DataBasisLink", 	filterId: 3570},		// 3570
		 {tableId: "ref1ProcessLink", 		filterId: 3515},		// 3515
//		 {tableId: "ref2LocationLink", 		filterId: 3360},		// 3360 - Single Address
		 {tableId: "ref2BaseDataLink", 		filterId: 3345},		// 3345
		 {tableId: "ref3BaseDataLink", 		filterId: 3210},		// 3210
//		 {tableId: "ref4ParticipantsLink", 	filterId: 3410},		// 3410 - Single Address
//		 {tableId: "ref4PMLink", 			filterId: 3400},		// 3400 - Single Address
		 {tableId: "ref5MethodLink", 		filterId: 3100}];		// 3100


	dojo.lang.forEach(filterTableMap, function(tableMapping) {
		var filterStore = dojo.widget.byId(tableMapping.tableId).store;
		filterStore.relationTypeFilter = tableMapping.filterId;
		
		// Connect all the setData calls on the filtered table to the main (unfiltered) table
/*
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: filterStore,
			srcFunc: "onRemoveData",
			adviceObj: mainStore,
			adviceFunc: function(obj) {
				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});
*/

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onRemoveData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});

		// Connect all the setData calls on the main table to the filtered tables
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onSetData",
			adviceObj: filterStore,
			adviceFunc: function() {
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].relationType == this.relationTypeFilter) {
						var item = this.getDataByKey(data[i].Id);
						if (!item) {
							this.addData(data[i]);
						}
					}
				}
			},
			once: true,
			delay: 10
		});

	});
/*
	onClearData:function(){ },
	onAddData:function(obj){ },
	onAddDataRange:function(arr){ },
	onUpdateField:function(obj, field, val){ }
*/	
}


function initAddressReferenceTables() {
	var mainStore = dojo.widget.byId("generalAddress").store;

	var filterTableMap =
		[{ tableId: "ref2LocationLink", 	filterId: 3360, relationName: "Standort" },
		 { tableId: "ref4ParticipantsLink",	filterId: 3410, relationName: "Beteiligte" },
		 { tableId: "ref4PMLink", 			filterId: 3400, relationName: "Projektleiter"}];


	dojo.lang.forEach(filterTableMap, function(tableMapping) {
		var filterStore = dojo.widget.byId(tableMapping.tableId).store;
		filterStore.relationTypeFilter = tableMapping.filterId;
		filterStore.relationName = tableMapping.relationName;
		
		// Connect all the setData calls on the filtered table to the main (unfiltered) table

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: filterStore,
			srcFunc: "onRemoveData",
			adviceObj: mainStore,
			adviceFunc: function(obj) {
				if (obj.src.removedOnUpdateField) {
					// onUpdateField removes the entry from the filtered table if it existed.
					// Since we connect the onRemove funtion to the main store, we have to check
					// if the item was removed directly, or if it was removed because of an 'updateField'
					// call.
					obj.src.removedOnUpdateField = false;
					return;
				}

				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onRemoveData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
//				dojo.debug("onRemoveData() on "+this.relationTypeFilter);
				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onAddData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
//				dojo.debug("onAddData() on "+this.relationTypeFilter);
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].typeOfRelation == this.relationTypeFilter) {
						var item = this.getDataByKey(data[i].Id);
						if (!item) {
							this.addData(data[i]);
						}
					}
				}
			},
			once: true,
			delay: 10
		});

		// Connect all the setData calls on the main table to the filtered tables
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onSetData",
			adviceObj: filterStore,
			adviceFunc: function() {
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].typeOfRelation == this.relationTypeFilter) {
						var item = this.getDataByKey(data[i].Id);
						if (!item) {
							this.addData(data[i]);
						}
					}
				}
			},
			once: true,
			delay: 10
		});

		// If the combobox value has been updated, update the target stores depending on the new link type.
		// Get the relationName and compare it to the store values. If the value was found, add or remove it.
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onUpdateField",
			adviceObj: filterStore,
			adviceFunc: function(obj, path, val) {
				var item = this.getDataByKey(obj.Id);
				if (item != null) {
					// The item was found in the current table. Check if it should be displayed (its name equals relationName)
					if (obj.nameOfRelation != this.relationName) {
						// The relation does not belong in the current filter table. Remove it.
						item.removedOnUpdateField = true;
						this.removeData(item);
					}
				} else {
					// The item was not found in the current table. Check if it should be displayed (its name equals relationName)
					if (obj.nameOfRelation == this.relationName) {
						// The relation belongs in the current filter table. Add it.
						obj.typeOfRelation = this.relationTypeFilter;
						obj.refOfRelation = 2010;
						this.addData(obj);
					}
				}
			},
			once: true,
			delay: 10
		});
	});

}

function initToolbar() {
  // Check if qa is active
  var isQAActive = UtilQA.isQAActive();
  var isUserQA = UtilSecurity.isCurrentUserQA();

  // create toolbar buttons with tooltips
  var rightToolbar = dojo.widget.byId('rightToolbar');
  rightToolbar.addChild("img/ic_expand_required_grey.gif", "after", {
                            onClick:function(){toggleFields();},
                            caption:"Nur Pflichtfelder aufklappen",
                            widgetId:"toggleFieldsBtn"
                          });
  rightToolbar.addChild("img/ic_help.gif", "after", {
                            onClick:function(){},
                            caption:"Hilfe"
                          });
  
  var leftToolbar = dojo.widget.byId('leftToolbar');
  var newEntityButton = leftToolbar.addChild("img/ic_new.gif", "after", {
                            onClick:menuEventHandler.handleNewEntity,
                            caption:"Neu anlegen"
                          });
  var previewButton = leftToolbar.addChild("img/ic_preview.gif", "after", {
                            onClick:menuEventHandler.handlePreview,
                            caption:"Vorschau und Druckansicht"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var cutButton = leftToolbar.addChild("img/ic_cut.gif", "after", {
                            onClick:menuEventHandler.handleCut,
                            caption:"Objekt/Adresse/Teilbaum ausschneiden"
                          });
  var copyEntityButton = leftToolbar.addChild("img/ic_copy.gif", "after", {
                            onClick:menuEventHandler.handleCopyEntity,
                            caption:"Objekt/Adresse kopieren"
                          });
  var copyTreeButton = leftToolbar.addChild("img/ic_copy_tree.gif", "after", {
                            onClick:menuEventHandler.handleCopyTree,
                            caption:"Teilbaum kopieren"
                          });
  var pasteButton = leftToolbar.addChild("img/ic_paste.gif", "after", {
                            onClick:menuEventHandler.handlePaste,
                            caption:"Einfügen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var saveButton = leftToolbar.addChild("img/ic_save.gif", "after", {
                            onClick:menuEventHandler.handleSave,
                            caption:"Zwischenspeichern"
                          });
  var undoButton = leftToolbar.addChild("img/ic_undo.gif", "after", {
                            onClick:menuEventHandler.handleUndo,
                            caption:"Rückgängig"
                          });
  var discardButton = leftToolbar.addChild("img/ic_discard.gif", "after", {
                            onClick:menuEventHandler.handleDiscard,
                            caption:"Änderungen am aktuellen MD-S verwerfen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");

var reassignToAuthorButton = null;
  if (isQAActive && isUserQA) {
	reassignToAuthorButton = leftToolbar.addChild("img/ic_submit_author.gif", "after", {
                            onClick:menuEventHandler.handleReassignToAuthor,
                            caption:"An Bearbeiter rücküberweisen"
                          });
  }

  if (isQAActive && !isUserQA) {
	var finalSaveButton = leftToolbar.addChild("img/ic_submit_qs.gif", "after", {
                            onClick:menuEventHandler.handleForwardToQA,
                            caption:"An QS überweisen"
                          });
  } else {
  	var finalSaveButton = leftToolbar.addChild("img/ic_submit.gif", "after", {
                            onClick:menuEventHandler.handleFinalSave,
//                          disabled:true,
                            caption:"Abschließendes Speichern"
                          });
  }

  if (isQAActive && !isUserQA) {
  	var deleteButton = leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleMarkDeleted,
							caption:"Als gelöscht markieren"
                          });
  } else {
  	var deleteButton = leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleDelete,
                            caption:"Ausgewähltes Objekt bzw. Teilbaum löschen"
                          });  	
  }

  var removeDeleteFlagButton = null;
/*
  if (isQAActive && isUserQA) {
	removeDeleteFlagButton = leftToolbar.addChild("img/ic_delete_undo.gif", "after", {
                            onClick:menuEventHandler.handleUnmarkDeleted,
                            caption:"Löschen aufheben"
                          });
  }
*/
  var showChangesButton = leftToolbar.addChild("img/ic_original.gif", "after", {
                            onClick:menuEventHandler.handleShowChanges,
                            caption:"Änderungen anzeigen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var showCommentButton = leftToolbar.addChild("img/ic_comment.gif", "after", {
                            onClick:menuEventHandler.handleShowComment,
                            caption:"Kommentar ansehen/hinzufügen"
                          });


	// Activate/Deactivate buttons depending on the selected node
	var treeListener = dojo.widget.byId("treeListener");
	var treeController = dojo.widget.byId("treeController");

    dojo.event.topic.subscribe(treeListener.eventNames.select, function(message) {
		var hasWritePermission = message.node.userWritePermission;
		var hasWriteSinglePermission = message.node.userWriteSinglePermission;
		var hasWriteTreePermission = message.node.userWriteTreePermission;
		var hasWriteSubTreePermission = message.node.userWriteSubTreePermission;
		var canCreateRootNodes = UtilSecurity.canCreateRootNodes();
//		dojo.debug("User has write permission? "+hasWritePermission);

		var buttonList = [showChangesButton, previewButton, cutButton, copyEntityButton, copyTreeButton, discardButton,
				saveButton, finalSaveButton, reassignToAuthorButton, deleteButton, showCommentButton, newEntityButton, pasteButton];
		var enableList = [];

		// Initially disable all buttons
		dojo.lang.forEach(buttonList, function(item) { if (item != null) { item.disable(); } });

		// Build the enable list
		if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
			if (canCreateRootNodes) {
				enableList = [newEntityButton];
			} else {
				enableList = [];				
			}

		} else if (message.node.id == "newNode") {
			enableList = [previewButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton];

		} else {
			// If a 'normal' node (obj/adr that is not root) is selected, always enable the following nodes
			enableList = [showChangesButton, previewButton, copyEntityButton, showCommentButton];

			// If the node has children, enable the 'copy tree' button
			if (message.node.isFolder) {
				enableList.push(copyTreeButton);
			}
			// If the the user has write permission (single or tree), he can discard, save and publish nodes
			if (hasWritePermission) {
				enableList = enableList.concat([discardButton, saveButton, finalSaveButton]);				
			}
			// If the the user has write tree permission (tree), he can delete, move and create new nodes
			if (hasWriteTreePermission) {
				enableList = enableList.concat([deleteButton, cutButton]);
				if (message.node.nodeAppType == "O") {
					enableList.push(newEntityButton);

				} else if (message.node.nodeAppType == "A" && message.node.objectClass != 2 && message.node.objectClass != 3) {
					// For addresses, the new entity button depends on the class of the selected node
					enableList.push(newEntityButton);
				}
			}
			// If the the user has write tree permission (tree), but a node is assigned to QA, the user
			// is only allowed to create new subnodes.
			if (hasWriteSubTreePermission) {
				enableList.push(newEntityButton);
			}
			
			// If the current node is assigned to the QA enable the reassign button
			if (message.node.nodeDocType.search(/_QV/) != -1) {
				enableList.push(reassignToAuthorButton);
			}
		}

		// The paste button depends on the current selection in treeController and the current selected node
		if (treeController.canPaste(message.node)) {
			enableList.push(pasteButton);
		}

		dojo.lang.forEach(enableList, function(item) { if (item != null) { item.enable(); } });
    });

	// The undo button depends on the dirty flag
	dojo.event.connect("after", udkDataProxy, "setDirtyFlag", function() {
		undoButton.enable();
	});
	dojo.event.connect("after", udkDataProxy, "resetDirtyFlag", function() {
		undoButton.disable();
	});


	var showOrHidePasteButton = function(node) {
		// The paste button depends on the current selection in treeController and the current selected node
		if (treeController.canPaste(node)) {
			pasteButton.enable();
		} else {
			pasteButton.disable();
		}		
	};
    dojo.event.connectOnce("after", treeController, "move", showOrHidePasteButton);
    dojo.event.connectOnce("after", treeController, "prepareCopy", showOrHidePasteButton);
    dojo.event.connectOnce("after", treeController, "prepareCut", showOrHidePasteButton);

	// Initially disable all icons
	var disableList = [removeDeleteFlagButton, showChangesButton, previewButton, cutButton, copyEntityButton,
			copyTreeButton, saveButton, undoButton, discardButton, finalSaveButton, reassignToAuthorButton, deleteButton,
			showCommentButton, newEntityButton, pasteButton];	
	dojo.lang.forEach(disableList, function(item) { if (item != null) { item.disable(); } });
}

function initTableValidators() {
	var unsignedIntFlags = { min:0, max:2147483647 };

	// The coordinates in the spatial reference table must be empty or real numbers
	var table = dojo.widget.byId("spatialRefLocation");
	table.setValidationFunctions([
		{target: "longitude1", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "longitude2", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "latitude1", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "latitude2", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}}
	]);

	// The vector format table must contain an integer as 'numElements'
	var table = dojo.widget.byId("ref1VFormatDetails");
	table.setValidationFunctions([
		{target: "numElements", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}}
	]);

	// Availability media option 'Datenvolumen' is of type double 
	var table = dojo.widget.byId("availabilityMediaOptions");
	table.setValidationFunctions([
		{target: "transferSize", validateFunction: function(item) { return (item == null || item == "" || dojo.validate.isRealNumber(item)); }}
	]);

	// object class 1 - The 'Erstellungsmassstab' table must contain an integer and two doubles
	var table = dojo.widget.byId("ref1Scale");
	table.setValidationFunctions([
		{target: "scale", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}},
		{target: "groundResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "scanResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}}		
	]);

	// object class 3 - The 'Erstellungsmassstab' table must contain an integer and two doubles
	var table = dojo.widget.byId("ref3Scale");
	table.setValidationFunctions([
		{target: "scale", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}},
		{target: "groundResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "scanResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}}	
	]);

/*
	var table = dojo.widget.byId("timeRefTable");
	table.setValidationFunctions([
		{target: "date", validateFunction: function(item) {return (item != null && dojo.string.trim(item).length != 0);}},
		{target: "type", validateFunction: function(item) {return (item != null && dojo.string.trim(item).length != 0);}}
	]);
*/
}

function initCatalogData() {
	var deferred = new dojo.Deferred();

	CatalogService.getCatalogData({
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			// Update catalog Data in udkDataProxy
			catalogData = res;
			dojo.byId("currentCatalogName").innerHTML = catalogData.catalogName;
			deferred.callback();
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug(mes);
			deferred.errback();
		}
	});

	return deferred;
}

function initCurrentGroup() {
	var def = new dojo.Deferred();
	var getGroupNameDef = getCurrentGroupName();

	getGroupNameDef.addCallback(function(groupName) {
		SecurityService.getGroupDetails(groupName, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(group) {
//				dojo.debug("Group Details:");
//				dojo.debugShallow(group);
				currentGroup = group;
				def.callback();
			},

			errorHandler:function(mes){
				UtilDWR.exitLoadingState();
				dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
				dojo.debug("Error: "+mes);
				def.errback(mes);
			}
		});
	});

	return def;
}

function getCurrentGroupName() {
	var def = new dojo.Deferred();

	SecurityService.getGroups(true, {
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(groupList) {
			for (var i = 0; i < groupList.length; ++i) {
				if (groupList[i].id == currentUser.groupId) {
//					dojo.debug("Found user group:");
//					dojo.debugShallow(groupList[i]);
					def.callback(groupList[i].name);
					break;
				}
			}
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;		
}

function initCurrentUser() {
	var def = new dojo.Deferred();

	SecurityService.getCurrentUser({
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(user) {
			currentUser = user;

			var roleName = UtilSecurity.getRoleName(user.role);
			var title = UtilAddress.createAddressTitle(user.address);
			dojo.byId("currentUserName").innerHTML = title;
			dojo.byId("currentUserRole").innerHTML = roleName;

			def.callback();
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.userError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}

function initSysLists() {
	var def = new dojo.Deferred();

	var selectWidgetIDs = ["spatialRefAltVDate", "spatialRefAltMeasure", "timeRefTypeCombobox",
		"generalAddressCombobox", "geometryTypeEditor", "timeRefPeriodicity", "availabilityMediaOptionsMediumCombobox",
		"timeRefStatus", "ref1DataSet", "ref1RepresentationCombobox", "thesaurusTopicsCombobox", "ref1VFormatTopology",
		"freeReferencesEditor", "timeRefIntervalUnit", "extraInfoLegalBasicsTableEditor", "extraInfoXMLExportTableEditor",
		"thesaurusEnvCatsCombobox", "thesaurusEnvTopicsCombobox", "ref1SpatialSystem", "ref1SymbolsTitleCombobox", "ref1KeysTitleCombobox",
		"ref3ServiceType", "ref3ServiceTypeEditor", "extraInfoLangData", "extraInfoLangMetaData", "extraInfoPublishArea", "extraInfoConformityLevelEditor",
		"availabilityDataFormatName", "availabilityUsageLimitationLimitEditor",
		// Addresses
		"headerAddressType2Style", "headerAddressType3Style", "headerAddressType2Title", "headerAddressType3Title",
		"addressComType"]; 

/*
	// TODO load the initial language code map from the backend
	var locale = dojo.hostenv.normalizeLocale(dojo.locale);
	var languageCode = locale.split("-")[0];

	if (languageCode != "de" && languageCode != "en") {
		dojo.debug("Unsupported Language. Setting language to english.");
		languageCode = "en";
	}
*/
	// Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
	var languageCode = UtilCatalog.getCatalogLanguage();

	var lstIds = [];
	dojo.lang.forEach(selectWidgetIDs, function(item){
		lstIds.push(dojo.widget.byId(item).listId);
	});

	CatalogService.getSysLists(lstIds, languageCode, {
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			dojo.lang.forEach(selectWidgetIDs, function(widgetId) {
				var selectWidget = dojo.widget.byId(widgetId);
				selectWidget.dataProvider.setData(res[selectWidget.listId]);	
			});
			def.callback();
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}


function initGeneralEventListener() {
	// Disable backspace default behaviour (browser back button)
	if (dojo.render.html.ie) {
		// Registering IE onkeydown with dojo doesn't work as it should
		// Therefore we need to directly register with the documents onkeydown function
		document.onkeydown = function(e) {
			var evt = (e) ? e : window.event; 
			dojo.event.browser.fixEvent(evt);
			if (evt.keyCode == evt.KEY_BACKSPACE) {
				var tagName = evt.target.tagName.toLowerCase();
				if (!(tagName == 'input') && !(tagName == 'textarea')) {
//					dojo.debug("Preventing backspace default behaviour on "+evt.target);
					evt.preventDefault();
				}

			} else if (evt.keyCode == evt.KEY_F5) {			
				dialog.show(message.get("general.hint"), message.get("dialog.browserFunctionDisabled"), dialog.INFO);

			    evt.keyCode = 0;
			    evt.cancelBubble = true;
			    evt.returnValue = false;
			    return false;
			}
		}

	} else {
		dojo.event.browser.addListener(document, "onkeydown", function(evt){
			if (evt.keyCode == evt.KEY_BACKSPACE) {
				if (!(evt.target instanceof HTMLInputElement) && !(evt.target instanceof HTMLTextAreaElement)) {
//					dojo.debug("Preventing backspace default behaviour on "+evt.target);
					evt.preventDefault();
				}

			} else if (evt.keyCode == evt.KEY_F5) {			
				dialog.show(message.get("general.hint"), message.get("dialog.browserFunctionDisabled"), dialog.INFO);
				evt.preventDefault();
			}
		});
	}


	// Catch the window close event
	window.onbeforeunload = function(evt){
		if (dojo.render.html.ie) {
			// Catch clicks on the upper left and upper right corner. Also catch clicks on the app's 'close' button.
			if ( (event.clientY < 0 && (event.clientX > (document.documentElement.clientWidth - 15) || event.clientX < 15))
			   ||(event.clientY < 23 && event.clientX > document.documentElement.clientWidth - 172 && event.clientX < document.documentElement.clientWidth - 9)
			   ||(event.clientY < -30 && event.clientY > -60 && event.clientX > 0 && event.clientX < 60)) {
		  		event.returnValue = message.get("general.closeWindow");
			}

		} else {
			return message.get("general.closeWindow");
		}
	}
}

function _enableInputElement(widgetId) {
	var widget = dojo.widget.byId(widgetId);
	
	// Check if the enable method exists
	if (widget.enable) {
		// If it does, enable the widget
    	widget.enable();

	} else {
		dojo.debug("Can't enable widget "+widgetId+". Method not implemented for "+widget);
	}
}

function _disableInputElement(widgetId) {
	var widget = dojo.widget.byId(widgetId);

	// Check if the disable method exists
	if (widget.disable) {
		// If it does, disable the widget
    	widget.disable();

	} else {
		dojo.debug("Can't disable widget "+widgetId+". Method not implemented for "+widget);
	}
}

function _disableHtmlLink(elementId) {
	var element = dojo.byId(elementId);
	
	if (element.onClick) {
		element._disabledOnClick = element.onClick;
		element.onClick = null;

	} else if (element.onclick) {
		element._disabledOnClick = element.onclick;
		element.onclick = null;
	}
}

function _enableHtmlLink(elementId) {
	var element = dojo.byId(elementId);
	
	if (element._disabledOnClick) {
		element.onclick = element._disabledOnClick;
		element._disabledOnClick = null;
	}
}

function disableInputOnWrongPermission() {
    var treeListener = dojo.widget.byId("treeListener");

	var htmlLinks = ["generalAddressTableLink", "spatialRefAdminUnitLink", "spatialRefLocationLink", "thesaurusTermsLink", "thesaurusTermsNavigatorLink",
					 "ref1AddSymbolsLink", "ref1AddKeysLink", "ref1AddServiceLink", "ref1AddBasisLink", "ref1AddDataBasisLink", "ref1AddProcessLink",
					 "ref2AddLocationLink", "ref2AddBaseDataLink", "ref3AddBaseDataLink", "ref4AddParticipantsLink", "ref4AddPMLink", "ref5AddMethodLink"];

    dojo.event.topic.subscribe(treeListener.eventNames.select, function(message) {
		var hasWritePermission = message.node.userWritePermission;
//		var hasWriteSinglePermission = message.node.userWriteSinglePermission;
//		var hasWriteTreePermission = message.node.userWriteTreePermission;
//		dojo.debug("hasWritePermission: "+hasWritePermission);

		if (hasWritePermission) {
			// Enable all input elements
			dojo.lang.forEach(headerUiInputElements, _enableInputElement);
			dojo.lang.forEach(generalUiInputElements, _enableInputElement);
			dojo.lang.forEach(spatialUiInputElements, _enableInputElement);
			dojo.lang.forEach(timeUiInputElements, _enableInputElement);
			dojo.lang.forEach(extraUiInputElements, _enableInputElement);
			dojo.lang.forEach(availUiInputElements, _enableInputElement);
			dojo.lang.forEach(thesUiInputElements, _enableInputElement);
			dojo.lang.forEach(class0UiInputElements, _enableInputElement);
			dojo.lang.forEach(class1UiInputElements, _enableInputElement);
			dojo.lang.forEach(class2UiInputElements, _enableInputElement);
			dojo.lang.forEach(class3UiInputElements, _enableInputElement);
			dojo.lang.forEach(class4UiInputElements, _enableInputElement);
			dojo.lang.forEach(class5UiInputElements, _enableInputElement);
		
			dojo.lang.forEach(adrUiInputElements, _enableInputElement);
			dojo.lang.forEach(adrClass0UiInputElements, _enableInputElement);
			dojo.lang.forEach(adrClass1UiInputElements, _enableInputElement);
			dojo.lang.forEach(adrClass2UiInputElements, _enableInputElement);
			dojo.lang.forEach(adrClass3UiInputElements, _enableInputElement);

			dojo.lang.forEach(htmlLinks, _enableHtmlLink);

			_enableInputElement("thesaurusFreeTermsAddButton");
			_enableInputElement("thesaurusFreeTermsAddressAddButton");

		} else {
			// Disable all input elements
			dojo.lang.forEach(headerUiInputElements, _disableInputElement);
			dojo.lang.forEach(generalUiInputElements, _disableInputElement);
			dojo.lang.forEach(spatialUiInputElements, _disableInputElement);
			dojo.lang.forEach(timeUiInputElements, _disableInputElement);
			dojo.lang.forEach(extraUiInputElements, _disableInputElement);
			dojo.lang.forEach(availUiInputElements, _disableInputElement);
			dojo.lang.forEach(thesUiInputElements, _disableInputElement);
			dojo.lang.forEach(class0UiInputElements, _disableInputElement);
			dojo.lang.forEach(class1UiInputElements, _disableInputElement);
			dojo.lang.forEach(class2UiInputElements, _disableInputElement);
			dojo.lang.forEach(class3UiInputElements, _disableInputElement);
			dojo.lang.forEach(class4UiInputElements, _disableInputElement);
			dojo.lang.forEach(class5UiInputElements, _disableInputElement);
		
			dojo.lang.forEach(adrUiInputElements, _disableInputElement);
			dojo.lang.forEach(adrClass0UiInputElements, _disableInputElement);
			dojo.lang.forEach(adrClass1UiInputElements, _disableInputElement);
			dojo.lang.forEach(adrClass2UiInputElements, _disableInputElement);
			dojo.lang.forEach(adrClass3UiInputElements, _disableInputElement);

			dojo.lang.forEach(htmlLinks, _disableHtmlLink);

			_disableInputElement("thesaurusFreeTermsAddButton");
			_disableInputElement("thesaurusFreeTermsAddressAddButton");
		}
    });
}

function initOptionalFieldStates() {
	var def = getGuiIdList();

	def.addCallback(function(guiIdList) {
		dojo.lang.forEach(guiIdList, function(entry) {
			var guiId = "uiElement"+entry.id;
			var uiElement = dojo.byId(guiId);
			var dispType = "standard";

			if (uiElement) {
				if (entry.mode == "0") {
					uiElement.setAttribute("displaytype", "alwaysHide");
					dojo.html.hide(uiElement);

				} else if (entry.mode == "1") {
					uiElement.setAttribute("displaytype", "alwaysShow");
					dojo.html.show(uiElement);
				}
			}
		});
	});

	return def;
}

function jumpToNodeOnInit() {
	if (initJumpToNodeId.length != 0 && initJumpToNodeType.length != 0) {
		menuEventHandler.handleSelectNodeInTree(initJumpToNodeId, initJumpToNodeType);
	}
}

function getGuiIdList() {
	var deferred = new dojo.Deferred();

	// Get all gui ids
	CatalogService.getSysGuis(null, {
		callback: function(sysGuiList) {
			deferred.callback(sysGuiList);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
			deferred.errback(err);			
		}
	});

	return deferred;
}
