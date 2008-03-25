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

  initGeneralEventListener();
  initToolbar();
  initTree();
  initForm();
  initTableValidators();
  initCTS();
  initFreeTermsButtons();
  initReferenceTables();
  var deferred = initCatalogData();
  deferred.addCallback(initSysLists);
  hideSplash();
  udkDataProxy.resetDirtyFlag();
});

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
    dojo.event.connect(addressSelectBox, "onValueChanged", "selectUDKAddressType");
    addressSelectBox.setValue("AddressType0");
  }

  // set context menu on object owner listbox
  var objectOwnerWidget = dojo.widget.byId('objectOwner');
  if (objectOwnerWidget) {
    var objectOwnerCM = dojo.widget.createWidget("ingrid:ContextMenu");
    objectOwnerCM.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
    objectOwnerCM.bindDomNode(objectOwnerWidget.domNode);
    objectOwnerWidget.textInputNode.style.cursor='pointer';
  }

  // set context menu on last editor label
  var lastEditorLabel = dojo.byId('last_editor');
  if (lastEditorLabel) {
    var lastEditorCM = dojo.widget.createWidget("ingrid:ContextMenu");
    lastEditorCM.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
    lastEditorCM.bindDomNode(lastEditorLabel);
    lastEditorLabel.style.cursor='pointer';
  }

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
	EntryService.getSubTree(null, null, 1, 
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
//  contextMenu1.addItem(message.get('tree.nodeMarkDeleted'), 'detach', menuEventHandler.handleMarkDeleted);
  contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', menuEventHandler.handleDelete);

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

		EntryService.getSubTree(node.id, node.nodeAppType, 1, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
  			callback:function(res) { deferred.callback(res); },
			timeout:10000,
			errorHandler:function(message) {
				UtilDWR.exitLoadingState();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
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
						preHook: UtilDWR.enterLoadingState,
						postHook: UtilDWR.exitLoadingState,
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
							queryTerm = item;
						}

						// Filter all the descriptors from the result
						if (item.type == "DESCRIPTOR") {
							descriptors.push(item);
						}
					});

					// Decide what to do based on the type of the query
					if (queryTerm == null) {
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
					
					} else if (queryTerm.type == "TOP_TERM") {
						// A top term can't be added to the free terms list. Show an info dialog and clear the input field
						dialog.show(message.get("general.hint"), dojo.string.substituteParams(message.get("sns.freeTermAddTopTermHint"), term), dialog.INFO);			
					
					} else if (queryTerm.type == "NODE_LABEL") {
						// A node label can't be added to the free terms list. Show an info dialog and clear the input field
						dialog.show(message.get("general.hint"), dojo.string.substituteParams(message.get("sns.freeTermAddNodeLabelHint"), term), dialog.INFO);			
					
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
                        	{ caption: message.get("general.no"),  action: function() { deferred.errback(); } },
                        	{ caption: message.get("general.yes"), action: function() { deferred.callback(); } }
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
            if (event.keyCode == event.KEY_ENTER) {
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
            if (event.keyCode == event.KEY_ENTER) {
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
/*
  leftToolbar.addChild("img/ic_submit_qs.gif", "after", {
                            onClick:menuEventHandler.handleForwardToQS,
                            caption:"An QS überweisen"
                          });
*/
  var finalSaveButton = leftToolbar.addChild("img/ic_submit.gif", "after", {
                            onClick:menuEventHandler.handleFinalSave,
//                          disabled:true,
                            caption:"Abschließendes Speichern"
                          });
  var deleteButton = leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleDelete,
                            caption:"Ausgewähltes Objekt bzw. Teilbaum löschen"
//                            caption:"Als gelöscht markieren"
                          });
/*
  leftToolbar.addChild("img/ic_delete_undo.gif", "after", {
                            onClick:menuEventHandler.handleUnmarkDeleted,
                            caption:"Löschen aufheben"
                          });
*/

/*
  leftToolbar.addChild("img/ic_original.gif", "after", {
                            onClick:menuEventHandler.handleShowChanges,
                            caption:"Änderungen anzeigen"
                          });
*/
  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var showCommentButton = leftToolbar.addChild("img/ic_comment.gif", "after", {
                            onClick:menuEventHandler.handleShowComment,
                            caption:"Kommentar ansehen/hinzufügen"
                          });



	// Activate/Deactivate buttons depending on the selected node
	var treeListener = dojo.widget.byId("treeListener");
	var treeController = dojo.widget.byId("treeController");

    dojo.event.topic.subscribe(treeListener.eventNames.select, function(message) {
		var disableList = [];
		var enableList = [];
		if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
			disableList = [previewButton, cutButton, copyEntityButton, copyTreeButton, discardButton, saveButton, finalSaveButton, deleteButton, showCommentButton];
			enableList = [newEntityButton];

		} else if (message.node.id == "newNode") {
			disableList = [copyTreeButton, cutButton, copyEntityButton, newEntityButton];
			enableList = [previewButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton];

		} else if (message.node.isFolder) {
			disableList = [];
			enableList = [previewButton, cutButton, copyEntityButton, copyTreeButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton, newEntityButton];

		} else {
			disableList = [copyTreeButton];
			enableList = [previewButton, cutButton, copyEntityButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton];

			if (message.node.nodeAppType == "O") {
				enableList.unshift(newEntityButton);				

			} else if (message.node.nodeAppType == "A") {
				// For addresses, the new entity button depends on the class of the selected node
				if (message.node.objectClass == 2 || message.node.objectClass == 3) {
					disableList.unshift(newEntityButton);
				} else {
					enableList.unshift(newEntityButton);
				}
			}
		}

		// The paste button depends on the current selection in treeController and the current selected node
		if (treeController.canPaste(message.node)) {
			enableList.push(pasteButton);			
		} else {
			disableList.push(pasteButton);
		}

		dojo.lang.forEach(disableList, function(item) {item.disable()});
		dojo.lang.forEach(enableList, function(item) {item.enable()});
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
	var disableList = [previewButton, cutButton, copyEntityButton, copyTreeButton, saveButton, undoButton, discardButton, finalSaveButton, deleteButton, showCommentButton, newEntityButton, pasteButton];	
	dojo.lang.forEach(disableList, function(item) {item.disable()});
}

function initTableValidators() {
	var unsignedIntFlags = { min:0, max:2147483647 }

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
		{target: "transferSize", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
	]);

	// object class 1 - The 'Erstellungsmassstab' table must contain an integer and two doubles
	var table = dojo.widget.byId("ref1Scale");
	table.setValidationFunctions([
		{target: "scale", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}},
		{target: "groundResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "scanResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},		
	]);
}

function initCatalogData() {
	var deferred = new dojo.Deferred();

	EntryService.getCatalogData({
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			// Update catalog Data in udkDataProxy
			catalogData = res;
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


function initSysLists() {
	var selectWidgetIDs = ["spatialRefAltVDate", "spatialRefAltMeasure", "timeRefTypeCombobox",
		"generalAddressCombobox", "geometryTypeEditor", "timeRefPeriodicity", "availabilityMediaOptionsMediumCombobox",
		"timeRefStatus", "ref1DataSet", "ref1RepresentationCombobox", "thesaurusTopicsCombobox", "ref1VFormatTopology",
		"freeReferencesEditor", "timeRefIntervalUnit", "extraInfoLegalBasicsTableEditor", "extraInfoXMLExportTableEditor",
		"thesaurusEnvCatsCombobox", "thesaurusEnvTopicsCombobox", "ref1SpatialSystem", "ref1SymbolsTitleCombobox", "ref1KeysTitleCombobox",
		"ref3ServiceType", "extraInfoLangData", "extraInfoLangMetaData", "extraInfoPublishArea", "availabilityDataFormatName",
		// Addresses
		"headerAddressType2Style", "headerAddressType3Style", "headerAddressType2Title", "headerAddressType3Title",
		"addressComType"]; 

/*
	// TODO load the initial language code map from the backend
	var locale = dojo.hostenv.normalizeLocale(dojo.locale);
	var language = locale.split("-")[0];
	var languageCode = 94;

	if (language == "de") {
		languageCode = 121;
	} else if (language == "en") {
		languageCode = 94;
	} else {
		dojo.debug("Unsupported Language. Setting language to english.");
	}
*/
	// Setting the language code to 121. Uncomment the previous block to enable language specific settings depending on the browser language
	var languageCode = 121;


	var lstIds = [];
	dojo.lang.forEach(selectWidgetIDs, function(item){
		lstIds.push(dojo.widget.byId(item).listId);
	});

	EntryService.getSysLists(lstIds, languageCode, {
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			dojo.lang.forEach(selectWidgetIDs, function(widgetId) {
				var selectWidget = dojo.widget.byId(widgetId);
				selectWidget.dataProvider.setData(res[selectWidget.listId]);	
			});
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
		}
	});
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
			}
		}
	} else {
		dojo.event.browser.addListener(document, "onkeydown", function(evt){
			if (evt.keyCode == evt.KEY_BACKSPACE) {
				if (!(evt.target instanceof HTMLInputElement) && !(evt.target instanceof HTMLTextAreaElement)) {
//					dojo.debug("Preventing backspace default behaviour on "+evt.target);
					evt.preventDefault();
				}
			}
		});
	}


	// Catch the window close event
	window.onbeforeunload = function(evt){
		if (dojo.render.html.ie) {
			if (event.clientY < 0 && (event.clientX > (document.documentElement.clientWidth - 15) || event.clientX < 15) ) {
		  		event.returnValue = message.get("general.closeWindow");
			}
		} else {
			evt.returnValue = message.get("general.closeWindow");
		}
	}
}