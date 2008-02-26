// the root mdek object
// holds all states and encapsulates common functions
if(dj_undef("mdek", this)){
	var mdek = {};
}
var mdek = {};

var ref1SpatialSystemDP = null;


dojo.addOnLoad(function()
{
  // initialite debug console if necessary
  if (djConfig.isDebug)
  {
    dojo.debug("The current version of dojo is: ", dojo.version.toString());
    var console = dojo.byId("dojoDebugConsole");
    console.style.visibility = "visible";
  }
  dwr.engine.setPreHook(function() {
    var console = dojo.byId("loadingZone");
    console.style.visibility = "visible";
  });
  dwr.engine.setPostHook(function() {
    var console = dojo.byId("loadingZone");
    console.style.visibility = "hidden";
  });

  initToolbar();
  initTree();
  initForm();
  initTableValidators();
  initCTS();
  initFreeTermsButtons();
  initRef1SpatialSystemDataProvider();
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
  			callback:function(res) { deferred.callback(res); },
			timeout:10000,
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
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
				dojo.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
				var _this = this;
				CTService.getCoordinates(fromSRS, toSRS, coords, {
						callback: dojo.lang.hitch(this, this.updateDestinationStore),
						timeout:8000,
						errorHandler:function(message) {
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
// Helper function that iterates over all entries in a store and returns a key that is not in use yet
// We need this function to add terms to the free Terms list
// TODO Move to a helper class if we need it more often
	var getNewKey = function(store) {
		var key = 0;
		var data = store.get();
		for(var i=0; i < data.length; i++){
			if(data[i].key >= key){
				key = data[i].key + 1;
			}
		}
		return key;
	}

	// This function executes a search for topics and adds the results to the specified lists
	var executeSearch = function() {
		var term = this._inputFieldWidget.getValue();
		term = dojo.string.trim(term);
		_this = this;
		if (term) {
			SNSService.findTopics(term, {
				callback:function(topics) {
					// Remove all non-descriptors from the list
					var descriptors = [];
					dojo.lang.forEach(topics, function(item){
						if (item.type == "DESCRIPTOR")
							descriptors.push(item);
					});

					if (descriptors.length != 0) {
						// Topics found. Add the results to the topic list
						var topicStore = _this._termListWidget.store;
						// If the term we searched for was a descriptor and is contained in the result list, only add it
						// to the list and ignore the rest
						for (var i = 0; i < descriptors.length; ++i) {
							if (term == descriptors[i].title) {
								if (dojo.lang.every(topicStore.getData(), function(item){ return item.topicId != descriptors[i].topicId; })) {
									// Topic is new. Add it to the topic list
									topicStore.addData( {Id: getNewKey(topicStore), topicId: descriptors[i].topicId, title: descriptors[i].title} );

									// Scroll to the added descriptor
									var rows = _this._termListWidget.domNode.tBodies[0].rows;
									dojo.html.scrollIntoView(rows[rows.length-1]);
								}
								return;
							}
						}

						// Always add the synonym to the free terms list (if it doesn't exist already) 
						// Afterwards a window is opened to query if more descriptors should be added
						var freeTermsStore = _this._freeTermListWidget.store;
						if (dojo.lang.every(freeTermsStore.getData(), function(item){return item.title != term;})) {
							// If every term in the store != the entered term add it to the list
							var identifier = getNewKey(freeTermsStore);							
							freeTermsStore.addData( {Id: identifier, title: term} );

							// Scroll to the added descriptor
							var rows = _this._freeTermListWidget.domNode.tBodies[0].rows;
							dojo.html.scrollIntoView(rows[rows.length-1]);
						}


						// Open a new window to query the user if he wants to add all the descriptors to the list
						var deferred = new dojo.Deferred();

						deferred.addCallback(function() {
							dojo.lang.forEach(descriptors, function(topic) {
								if (dojo.lang.every(topicStore.getData(), function(item){ return item.topicId != topic.topicId; })) {
									// Topic is new. Add it to the topic list
									topicStore.addData( {Id: getNewKey(topicStore), topicId: topic.topicId, title: topic.title} );
									
									// Scroll to the added descriptor
									var rows = _this._termListWidget.domNode.tBodies[0].rows;
									dojo.html.scrollIntoView(rows[rows.length-1]);
								} else {
									// Topic already exists in the topic List
									return;
								}
							});
						});
						// Do nothing on error...

						dialog.showPage(message.get("dialog.addDescriptorsTitle"), "mdek_add_descriptors_dialog.html", 360, 240, true, {descriptors:descriptors, resultHandler:deferred});
					} else {
						// Topic not found in the sns. Add the result to the free term list
						var freeTermsStore = _this._freeTermListWidget.store;
						if (dojo.lang.every(freeTermsStore.getData(), function(item){return item.title != term;})) {
							// If every term in the store != the entered term add it to the list
							var identifier = getNewKey(freeTermsStore);							
							freeTermsStore.addData( {Id: identifier, title: term} );

							// Scroll to the added descriptor
							var rows = _this._freeTermListWidget.domNode.tBodies[0].rows;
							dojo.html.scrollIntoView(rows[rows.length-1]);
						}
					}},
				timeout:8000,
				errorHandler:function(msg) {dojo.debug("Error while executing SNSService.findTopics");}
			});
		}	
	}

	// Add the function(s) to the button in the object form
	var button = dojo.widget.byId("thesaurusFreeTermsAddButton");

	var inputField = dojo.widget.byId("thesaurusFreeTerms");
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                button.onClick();
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
                button.onClick();
            }
        });

	button._inputFieldWidget = dojo.widget.byId("thesaurusFreeTermInputAddress");
	button._termListWidget = dojo.widget.byId("thesaurusTermsAddress");
	button._freeTermListWidget = dojo.widget.byId("thesaurusFreeTermsListAddress");
	button.onClick = executeSearch;

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
		[{tableId: "ref2LocationLink", 		filterId: 3360},		// 3360 - Single Address
		 {tableId: "ref4ParticipantsLink", 	filterId: 3410},		// 3410 - Single Address
		 {tableId: "ref4PMLink", 			filterId: 3400}];		// 3400 - Single Address


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

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onAddData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].refOfRelation == this.relationTypeFilter) {
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
					if (data[i].refOfRelation == this.relationTypeFilter) {
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

					var relName = obj.nameOfRelation;

					switch (this.relationTypeFilter) {
						case 3360: if (relName == "Standort") break;
						case 3400: if (relName == "Projektleiter") break;
						case 3410: if (relName == "Beteiligte") break;
						default:
							relRef = null;
							this.removeData(item);								
							break;
					}
				} else {
					var relName = obj.nameOfRelation;

					switch (this.relationTypeFilter) {
						case 3360:
							if (relName == "Standort") {
								obj.refOfRelation = 3360;
								this.addData(obj);
							}
							break;
						case 3400:
							if (relName == "Projektleiter") {
								obj.refOfRelation = 3400;
								this.addData(obj);
							}
							break;
						case 3410:
							if (relName == "Beteiligte") {
								obj.refOfRelation = 3410;
								this.addData(obj);
							}
							break;
						default:
							break;
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
*/	
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
		} else if (message.node.isFolder) {
			disableList = [];
			enableList = [previewButton, cutButton, copyEntityButton, copyTreeButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton, newEntityButton];
		} else {
			disableList = [copyTreeButton];
			enableList = [previewButton, cutButton, copyEntityButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton, newEntityButton];
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

function initRef1SpatialSystemDataProvider() {
	// Data provider for the combobox
	ref1SpatialSystemDP = new dojo.widget.basicComboBoxDataProvider({
	// Values are now initialised in initSysLists()
//		dataUrl: "js/data/ref1SpatialSystemType.js"
	});

	// Attach the neccessary functions to get Display Values for a given value
	ref1SpatialSystemDP.getDisplayValueForValue = function(value) {
		for (var i=0; i<this._data.length; i++) {
			if (this._data[i][1] == value) {
				return this._data[i][0];
			}
		}
		return null;
	}
	ref1SpatialSystemDP.getValueForDisplayValue = function(dispValue) {
		for (var i=0; i<this._data.length; i++) {
			if (this._data[i][0] == dispValue) {
				return this._data[i][1];
			}
		}
		return null;
	}
}

function initTableValidators() {
	
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
		{target: "numElements", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isInteger(item));}}
	]);

	// Availability media option 'Datenvolumen' is of type double 
	var table = dojo.widget.byId("availabilityMediaOptions");
	table.setValidationFunctions([
		{target: "transferSize", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
	]);

	// object class 1 - The 'Erstellungsmassstab' table must contain an integer and two doubles
	var table = dojo.widget.byId("ref1Scale");
	table.setValidationFunctions([
		{target: "scale", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isInteger(item));}},
		{target: "groundResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "scanResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},		
	]);
}

function initCatalogData() {
	var deferred = new dojo.Deferred();

	EntryService.getCatalogData({
		callback: function(res) {
			// Update catalog Data in udkDataProxy
			catalogData = res;
			deferred.callback();
		},
		errorHandler:function(mes){
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug(mes);
			deferred.errback();
		}
	});

	return deferred;
}


function initSysLists() {
	var selectWidgetIDs = ["ref1SpatialSystem", "spatialRefAltVDate", "spatialRefAltMeasure", "timeRefTypeCombobox",
		"generalAddressCombobox", "geometryTypeEditor", "timeRefPeriodicity", "availabilityMediaOptionsMediumCombobox",
		"timeRefStatus", "ref1DataSet", "ref1RepresentationCombobox", "thesaurusTopicsCombobox", "ref1VFormatTopology",
		"freeReferencesEditor", "timeRefIntervalUnit", "extraInfoLegalBasicsTableEditor", "extraInfoXMLExportTableCriteriaEditor",
		"thesaurusEnvCatsCombobox", "thesaurusEnvTopicsCombobox", "ref1SymbolsTitleCombobox", "ref1KeysTitleCombobox",
		"ref3ServiceType", "extraInfoLangData", "extraInfoLangMetaData",
		// This select box also gets initialised on object load
		"extraInfoPublishArea",
		// Addresses
		"headerAddressType2Style", "headerAddressType3Style", "headerAddressType2Title", "headerAddressType3Title",
		"addressComType"]; 


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

	var lstIds = [100];		// list id 100 is for the static DP re1SpatialSystemDP
	dojo.lang.forEach(selectWidgetIDs, function(item){
		lstIds.push(dojo.widget.byId(item).listId);
	});

	EntryService.getSysLists(lstIds, languageCode, {
		callback: function(res) {
			dojo.lang.forEach(selectWidgetIDs, function(widgetId) {
				var selectWidget = dojo.widget.byId(widgetId);
				selectWidget.dataProvider.setData(res[selectWidget.listId]);	
			});

			// Init the standalone DPs
			ref1SpatialSystemDP.setData(res[100]);
		},
		errorHandler:function(mes){
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
		}
	});
}
