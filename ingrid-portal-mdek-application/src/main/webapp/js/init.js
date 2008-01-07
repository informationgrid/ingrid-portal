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
  contextMenu1.addItem(message.get('tree.nodeMarkDeleted'), 'detach', menuEventHandler.handleMarkDeleted);

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem(message.get('tree.nodeNew'), 'addChild', menuEventHandler.handleNewEntity);



  var tree = dojo.widget.byId("tree");

  // TODO Was macht diese Verbindung?? Die 'onValueChanged' Tree Methode bzw. das Event gibt es nicht...
//  if (tree)
//    dojo.event.connect(tree, "onValueChanged", "selectUDKClass");

  // attach node selection handler
  var treeListener = dojo.widget.byId('treeListener');
  dojo.event.topic.subscribe(treeListener.eventNames.select, 'nodeSelected');

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
                            caption:"Einfügen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  leftToolbar.addChild("img/ic_save.gif", "after", {
                            onClick:menuEventHandler.handleSave,
                            caption:"Zwischenspeichern"
                          });
  leftToolbar.addChild("img/ic_undo.gif", "after", {
                            onClick:menuEventHandler.handleUndo,
                            caption:"Rückgängig"
                          });
  leftToolbar.addChild("img/ic_discard.gif", "after", {
                            onClick:menuEventHandler.handleDiscard,
                            caption:"Änderungen am aktuellen MD-S verwerfen"
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  leftToolbar.addChild("img/ic_submit_qs.gif", "after", {
                            onClick:menuEventHandler.handleForwardToQS,
                            caption:"An QS überweisen"
                          });
  leftToolbar.addChild("img/ic_submit_inact.gif", "after", {
                            onClick:menuEventHandler.handleFinalSave,
                            disabled:true,
                            caption:"Abschließendes Speichern"
                          });
  leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleMarkDeleted,
                            caption:"Als gelöscht markieren"
                          });
  leftToolbar.addChild("img/ic_delete_undo.gif", "after", {
                            onClick:menuEventHandler.handleUnmarkDeleted,
                            caption:"Löschen aufheben"
                          });
  leftToolbar.addChild("img/ic_original.gif", "after", {
                            onClick:menuEventHandler.handleShowChanges,
                            caption:"Änderungen anzeigen"
                          });
  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  leftToolbar.addChild("img/ic_comment.gif", "after", {
                            onClick:menuEventHandler.handleShowComment,
                            caption:"Kommentar ansehen/hinzufügen"
                          });

}