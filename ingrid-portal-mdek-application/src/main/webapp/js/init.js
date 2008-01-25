// the root mdek object
// holds all states and encapsulates common functions
if(dj_undef("mdek", this)){
	var mdek = {};
}

var mdek = {};

// holds all functions for the entry page
mdek.entry = {};
// holds the type of object to display on entry page  ('o' - object, 'a' - address)
mdek.entry.type="o";
mdek.entry.isTypeObject = function()
{
//  Only display objects
//  return (mdek.entry.type == "o");
	return true;
}

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
  initFreeTermsButton();
  initSpatialFreeReferencesComboBox();
  initRef1SpatialSystemDataProvider();
  initReferenceTables();
  hideSplash();

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

  mdek.entry.type = "o";
  var contentForm = dojo.widget.byId('contentFormObject');  
  if (contentForm) {
    contentForm.initForm();
  }
  var contentForm = dojo.widget.byId('contentFormAddress');  
  mdek.entry.type = "a";
  if (contentForm) {
    contentForm.initForm();
  }
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
		this.dstTable = args.dstTable;
		dojo.event.connectOnce(args.srcTable, "onSelect", this, "updateCoordinates");

		this.updateDestinationStore = function(ctsResponse) {
			var data = this.dstTable.store.getData();
			dojo.debug("Updating destination store srs: "+ctsResponse.spatialReferenceSystem);
			for (var i = 0; i < data.length; i++) {
				dojo.debug("data.srs: "+data[i].srs);
				if (data[i].srs == ctsResponse.spatialReferenceSystem) {
					dojo.debug("Correct entry found. Updating...");
					this.dstTable.store.update(data[i], "longitude1", ctsResponse.coordinate.longitude1);
					this.dstTable.store.update(data[i], "latitude1", ctsResponse.coordinate.latitude1);
					this.dstTable.store.update(data[i], "longitude2", ctsResponse.coordinate.longitude2);
					this.dstTable.store.update(data[i], "latitude2", ctsResponse.coordinate.latitude2);
				}
			}
		}

		this.clearDstStoreCoord = function(srs) {
			var data = this.dstTable.store.getData();
			var clearValue = "------";
			dojo.debug("Clearing destination store srs: "+srs);
			for (var i = 0; i < data.length; i++) {
				if (data[i].srs == srs) {
					this.dstTable.store.update(data[i], "longitude1", clearValue);
					this.dstTable.store.update(data[i], "latitude1", clearValue);
					this.dstTable.store.update(data[i], "longitude2", clearValue);
					this.dstTable.store.update(data[i], "latitude2", clearValue);
				}
			}
		}

		this.clearDstStore = function() {
			var data = this.dstTable.store.getData();
			var clearValue = "------";
			for (var i = 0; i < data.length; i++) {
				this.dstTable.store.update(data[i], "longitude1", clearValue);
				this.dstTable.store.update(data[i], "latitude1", clearValue);
				this.dstTable.store.update(data[i], "longitude2", clearValue);
				this.dstTable.store.update(data[i], "latitude2", clearValue);
			}
		}


		this.updateCoordinates = function() {
			var fromSRS = "GEO84";
			var selectedData = this.srcTable.getSelectedData();
			this.clearDstStore();
			if (selectedData.length != 1) {
				this.clearDstStore();
				return;
			}
			var coords = selectedData[0];
			var dstStore = this.dstTable.getData();
			if (coords && dojo.validate.isRealNumber(coords.longitude1)
					   && dojo.validate.isRealNumber(coords.longitude2)
					   && dojo.validate.isRealNumber(coords.latitude1)
					   && dojo.validate.isRealNumber(coords.latitude2)) {
				for (var i = 0; i < dstStore.length; i++) {
					var toSRS = dstStore[i].srs;
					dojo.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
					CTService.getCoordinates(fromSRS, toSRS, coords, {
							callback: dojo.lang.hitch(this, this.updateDestinationStore),
							timeout:8000,
							errorHandler:function(message) {
								dojo.debug(message);
								// There was an error while accessing the CTS
								// TODO do something...
							}
						}
					);
				}
			}
		}
	}	// StoreUpdateHandler

	
	// Connect all coordinate tables:
	new CoordinateUpdateHandler({
		srcTable: dojo.widget.byId("spatialRefAdminUnit"),
		dstTable: dojo.widget.byId("spatialRefAdminUnitCoords")
	});

	new CoordinateUpdateHandler({
		srcTable: dojo.widget.byId("spatialRefLocation"),
		dstTable: dojo.widget.byId("spatialRefLocationCoords")
	});
}


function initFreeTermsButton() {
	var button = dojo.widget.byId("thesaurusFreeTermsAddButton");
	
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


	button.onClick = function() {
		var term = dojo.widget.byId("thesaurusFreeTerms").getValue();
		term = dojo.string.trim(term);
		if (term) {
			SNSService.findTopic(term, {
				callback:function(topic) {
					if (topic != null && topic.type == "DESCRIPTOR") {
						// Topic found. Add the result to the topic list
						var topicStore = dojo.widget.byId("thesaurusTerms").store;
						if (topicStore.getByKey(topic.topicId)) {
							// Topic already exists in the topic List
							return;
						} else {
							// Topic is new. Add it to the topic list
							topicStore.addData({Id: topic.topicId, title: topic.title}, topic.topicId);
						}
					} else {
						// Topic not found in the sns. Add the result to the free term list
						var freeTermsStore = dojo.widget.byId("thesaurusFreeTermsList").store;
						if (dojo.lang.every(freeTermsStore.getData(), function(item){return item.title != term;})) {
							// If every term in the store != the entered term add it to the list
							var identifier = getNewKey(freeTermsStore);							
							freeTermsStore.addData({Id: identifier, title: term}, identifier);
						}
					}},
				timeout:8000,
				errorHandler:function(msg) {dojo.debug("Error while executing SNSService.findTopic");}
			});
		}
	}
}


function initReferenceTables() {
	var mainStore = dojo.widget.byId("linksTo").store;

// TODO Fix Me! Insert the correct filters here
	var filterTableMap =
		[{tableId: "ref1SymbolsLinks", 		filterId: "Symbolkatalog"},	
		 {tableId: "ref1KeysLinks", 		filterId: "Schlüsselkatalog"},
		 {tableId: "ref1ServiceLink", 		filterId: "Verweis zu Dienst"},
		 {tableId: "ref1BasisLink", 		filterId: "Fachliche Grundlage"},	// 3520
		 {tableId: "ref1DataBasisLink", 	filterId: "Datengrundlage"},		// 3570
		 {tableId: "ref1ProcessLink", 		filterId: "Herstellungsprozess"},
		 {tableId: "ref2LocationLink", 		filterId: "Basisdaten"},			//?3345
		 {tableId: "ref2BaseDataLink", 		filterId: "Standort"},				// Single Address
		 {tableId: "ref3BaseDataLink", 		filterId: "Basisdaten"},			// 3210
		 {tableId: "ref4ParticipantsLink", 	filterId: "Beteiligte"},			// Single Address
		 {tableId: "ref4PMLink", 			filterId: "Projektleiter"}];		// Single Address

	dojo.lang.forEach(filterTableMap, function(tableMapping) {
		var filterStore = dojo.widget.byId(tableMapping.tableId).store;
		filterStore.relationTypeNameFilter = tableMapping.filterId;
		
		// Connect all the setData calls on the filtered table to the main (unfiltered) table
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: filterStore,
			srcFunc: "onSetData",
			adviceObj: mainStore,
			adviceFunc: function() {
				var data = filterStore.getData();
				for (i in data) {
					var item = this.getDataByKey(data[i].Id);
					if (!item) {
						this.addData(data[i]);
					}
				}
			},
			once: true,
			delay: 10
		});

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
				for (i in data) {
					if (data[i].relationTypeName == this.relationTypeNameFilter) {
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

function initSpatialFreeReferencesComboBox() {
	var dp = dojo.widget.byId("freeReferencesEditor").dataProvider;
	var def = new dojo.Deferred();
	var kw = {
		adviceType: "after",
		srcObj: dp,
		srcFunc: "setData",
		adviceObj: def,
		adviceFunc: "callback",
		once: true
	}

	def.addCallback(function() {
		dojo.event.kwDisconnect(kw);
		EntryService.getUiListValues({
			callback: function(res) {
				var values = [];
				dojo.lang.forEach(res.ui_freeSpatialReferences, function(item) {
					values.push([item]);
				});
				var editor = dojo.widget.byId("freeReferencesEditor");
				editor.dataProvider.setData(values);
			},
			errorHandler:function(mes){
				dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
				dojo.debug(mes);
			}
		});
	});

	dojo.event.kwConnect(kw);
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
    dojo.event.topic.subscribe(treeListener.eventNames.select, function(message) {
		var disableList = [];
		var enableList = [];
		if (message.node.id == "objectRoot") {
			disableList = [previewButton, cutButton, copyEntityButton, copyTreeButton, discardButton, saveButton, finalSaveButton, deleteButton, showCommentButton];
			enableList = [newEntityButton];
		} else if (message.node.isFolder) {
			disableList = [];
			enableList = [previewButton, cutButton, copyEntityButton, copyTreeButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton, newEntityButton];
		} else {
			disableList = [copyTreeButton];
			enableList = [previewButton, cutButton, copyEntityButton, saveButton, discardButton, finalSaveButton, deleteButton, showCommentButton, newEntityButton];
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



	var treeController = dojo.widget.byId("treeController");
	var showOrHidePasteButton = function() {
		// The paste button depends on the current selection in treeController
		if (treeController.nodeToCopy != null || treeController.nodeToCut != null) {
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
		dataUrl: "js/data/ref1SpatialSystemType.js"
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

}