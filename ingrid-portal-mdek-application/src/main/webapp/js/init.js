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
  return (mdek.entry.type == "o");
}


dojo.addOnLoad(function()
{
  // initialite debug console if necessary
  if (djConfig.isDebug)
  {
    dojo.debug("The current version of dojo is: ", dojo.version.toString());
    var console = dojo.byId("dojoDebugConsole");
    console.style.visibility = "visible";
  }

  initToolbar();
  initTree();
  initForm();
  initCTS();
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
  contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', menuEventHandler.handleMarkDeleted);

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem(message.get('tree.nodeNew'), 'addChild', menuEventHandler.handleNewEntity);



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
			timeout:5000,
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { alert("Error while loading data from the server. Please check your connection and try again!"); return res;});
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
//			dojo.debug("Updating destination store srs: "+ctsResponse.spatialReferenceSystem);
			for (var i = 0; i < data.length; i++) {
				if (data[i].srs == ctsResponse.spatialReferenceSystem) {
//					dojo.debug("Correct entry found. Updating...");
					this.dstTable.store.update(data[i], "longitude1", ctsResponse.coordinate.longitude1);
					this.dstTable.store.update(data[i], "latitude1", ctsResponse.coordinate.latitude1);
					this.dstTable.store.update(data[i], "longitude2", ctsResponse.coordinate.longitude2);
					this.dstTable.store.update(data[i], "latitude2", ctsResponse.coordinate.latitude2);
				}
			}
		}

		this.updateCoordinates = function() {
			var fromSRS = "GEO84";
			var coords = this.srcTable.getSelectedData()[0];
			var dstStore = this.dstTable.getData();
			if (coords) {
				for (var i = 0; i < dstStore.length; i++) {
					var toSRS = dstStore[i].srs;
	//				dojo.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
					CTService.getCoordinates(fromSRS, toSRS, coords, {
							callback: dojo.lang.hitch(this, this.updateDestinationStore),
							timeout:5000,
							errorHandler:function(message) {alert(message); }
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
  leftToolbar.addChild("img/ic_new.gif", "after", {
                            onClick:menuEventHandler.handleNewEntity,
                            caption:"Neu anlegen"
                          });
  leftToolbar.addChild("img/ic_preview.gif", "after", {
                            onClick:menuEventHandler.handlePreview,
                            caption:"Vorschau und Druckansicht"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  leftToolbar.addChild("img/ic_cut.gif", "after", {
                            onClick:menuEventHandler.handleCut,
                            caption:"Objekt/Adresse/Teilbaum ausschneiden"
                          });
  leftToolbar.addChild("img/ic_copy.gif", "after", {
                            onClick:menuEventHandler.handleCopyEntity,
                            caption:"Objekt/Adresse kopieren"
                          });
  leftToolbar.addChild("img/ic_copy_tree.gif", "after", {
                            onClick:menuEventHandler.handleCopyTree,
                            caption:"Teilbaum kopieren"
                          });
  leftToolbar.addChild("img/ic_paste.gif", "after", {
                            onClick:menuEventHandler.handlePaste,
                            caption:"Einf�gen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  leftToolbar.addChild("img/ic_save.gif", "after", {
                            onClick:menuEventHandler.handleSave,
                            caption:"Zwischenspeichern"
                          });
  leftToolbar.addChild("img/ic_undo.gif", "after", {
                            onClick:menuEventHandler.handleUndo,
                            caption:"R�ckg�ngig"
                          });
  leftToolbar.addChild("img/ic_discard.gif", "after", {
                            onClick:menuEventHandler.handleDiscard,
                            caption:"�nderungen am aktuellen MD-S verwerfen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
/*
  leftToolbar.addChild("img/ic_submit_qs.gif", "after", {
                            onClick:menuEventHandler.handleForwardToQS,
                            caption:"An QS �berweisen"
                          });
*/
  leftToolbar.addChild("img/ic_submit_inact.gif", "after", {
                            onClick:menuEventHandler.handleFinalSave,
//                          disabled:true,
                            caption:"Abschlie�endes Speichern"
                          });
  leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleMarkDeleted,
                            caption:"Ausgew�hltes Objekt bzw. Teilbaum l�schen"
//                            caption:"Als gel�scht markieren"
                          });
/*
  leftToolbar.addChild("img/ic_delete_undo.gif", "after", {
                            onClick:menuEventHandler.handleUnmarkDeleted,
                            caption:"L�schen aufheben"
                          });
*/
/*
  leftToolbar.addChild("img/ic_original.gif", "after", {
                            onClick:menuEventHandler.handleShowChanges,
                            caption:"�nderungen anzeigen"
                          });
*/
  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  leftToolbar.addChild("img/ic_comment.gif", "after", {
                            onClick:menuEventHandler.handleShowComment,
                            caption:"Kommentar ansehen/hinzuf�gen"
                          });

}