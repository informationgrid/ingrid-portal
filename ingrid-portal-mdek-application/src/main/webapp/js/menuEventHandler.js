/*
 * Menu Event Handler. Static Methods
 */

var menuEventHandler = {};

// Singleton
// menuEventHandler = new function MenuEventHandler() {}

menuEventHandler.selectedNode = {};


menuEventHandler.handleNewEntity = function(mes) {
// TODO Dialog fuer neue Objekte / Adressen anzeigen. Reicht der Objektbezeichner im Baum?
//      Muss eine Nachricht an das Backend gesendet werden?
//      Nachricht an Backend ja? Id generiert in der Datenbank.
	dojo.debug('handleNewEntity()');
	var selectedNode = getSelectedNode(mes);

	if (!selectedNode) {
    	dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
	} else {
/*
    	var dlg = 'erfassung_objekt_anlegen.html';
    	if (selectedNode.nodeAppType == "A")
      	dlg = 'erfassung_adresse_anlegen.html';
    	dialog.showPage(message.get('tree.nodeNew'), dlg, 502, 130, true, {treeId:'tree', controllerId:'treeController', nodeId:selectedNode.widgetId});
*/
		// TODO if current node was not saved... 

		if (!selectedNode.isFolder) {
			selectedNode.setFolder();
			// TODO? Don't load children from the db. The new node is the only children.
			selectedNode.expand();
			menuEventHandler.selectedNode = selectedNode;
			attachNewNode();
		}
		else if (!selectedNode.isExpanded) {
    		var tree = selectedNode.tree;
    		var treeController = dojo.widget.byId('treeController');
			menuEventHandler.selectedNode = selectedNode;

			dojo.event.topic.subscribe(tree.eventNames.afterExpand, 'attachNewNode');
			treeController.expand(selectedNode);
  		}
  		else {
			menuEventHandler.selectedNode = selectedNode;
			attachNewNode();
  		}
  	}
}


attachNewNode = function() {
    var tree = dojo.widget.byId('tree');
    var treeListener = dojo.widget.byId('treeListener');
    var selectedNode = menuEventHandler.selectedNode;

	dojo.event.topic.unsubscribe(tree.eventNames.afterExpand, 'attachNewNode');
//	dojo.event.disconnect('after', selectedNode, 'setChildren', menuEventHandler, 'attachNewNode');
	dojo.debug('testMethod()');

	var newNode = tree.createNode(createNewNode());
	selectedNode.addChild(newNode);

	var treeController = dojo.widget.byId('treeController');

	tree.selectNode(newNode);
    dojo.event.topic.publish(treeListener.eventNames.deselect, {node: selectedNode});    
    dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});

//	tree.selectedNode = newNode;
}

menuEventHandler.handlePreview = function(message) {
// Message parameter is the either the Context Menu Item (TreeMenuItemV3) or the Widget (dojo:toolbarbutton)
//   where the event originated
  dojo.debug('Message parameter: '+message);

  var selectedNode;
  
  if (message instanceof dojo.widget.TreeMenuItemV3)
  {
    selectedNode = message.getTreeNode();
    dojo.debug('Tree Node: '+selectedNode);

    var info = selectedNode.getInfo();
    dojo.debug("Widget ID: "+info.widgetId);
    dojo.debug("Object ID: "+info.objectId);
    dojo.debug("Context Menu: "+info.contextMenu);
    dojo.debug("App Type: "+info.nodeAppType);
  }
  else
  {
    var tree = dojo.widget.byId('tree');
    selectedNode = tree.selectedNode;
  }
  
  if (selectedNode)
    dojo.debug('Selected node: '+selectedNode);
//  alertNotImplementedYet();
}

menuEventHandler.handleCut = function(mes) {
	var selectedNode = getSelectedNode(mes);
	if (!selectedNode || selectedNode.id == 'objectRoot') {
    	dialog.show(message.get('general.hint'), message.get('tree.selectNodeCutHint'), dialog.WARNING);
	}
	else {
		if (!selectedNode.isFolder) {
			var treeController = dojo.widget.byId('treeController');
			treeController.cut(selectedNode);
		} else {
    		dialog.show(message.get('general.hint'), message.get('tree.selectNodeCutHint'), dialog.WARNING);
		}
	}
}


menuEventHandler.handleCopyEntity = function() {alertNotImplementedYet();}
menuEventHandler.handleCopyTree = function() {alertNotImplementedYet();}
menuEventHandler.handlePaste = function() {alertNotImplementedYet();}

menuEventHandler.handleSave = function() {
//                                dialog.show("Zwischenspeichern", 'Der aktuelle Datensatz befindet sich in der Bearbeitung. Wollen Sie wirklich speichern?', dialog.WARNING, 
//                                      [{caption:"OK",action:function(){alert("OK")}},{caption:"Cancel",action:dialog.CLOSE_ACTION}]);},
  dojo.debug('Publishing event: /saveRequest');
  dojo.event.topic.publish("/saveRequest");
}

menuEventHandler.handleUndo = function(mes) {
	var selectedNode = getSelectedNode(mes);

	if (!selectedNode || selectedNode.id == 'objectRoot') {
    	dialog.show(message.get('general.hint'), message.get('tree.selectNodeCutHint'), dialog.WARNING);
	}
	else {
    	dojo.debug('Publishing event: /loadRequest('+selectedNode.id+', '+selectedNode.nodeAppType+')');
    	dojo.event.topic.publish("/loadRequest", {id: selectedNode.id, appType: selectedNode.nodeAppType});
	}
}


menuEventHandler.handleDiscard = function() {alertNotImplementedYet();}

menuEventHandler.handleForwardToQS = function() {alertNotImplementedYet();}
menuEventHandler.handleFinalSave = function() {alertNotImplementedYet();}
menuEventHandler.handleMarkDeleted = function() {alertNotImplementedYet();}
menuEventHandler.handleUnmarkDeleted = function() {alertNotImplementedYet();}
menuEventHandler.handleShowChanges = function() {alertNotImplementedYet();}
//                            		var win = window.open("qs_vergleich.html", 'preview', 'width=720, height=690, resizable=yes, scrollbars=yes, status=yes');
//                            		win.focus();

menuEventHandler.handleShowComment = function() {alertNotImplementedYet();}
//                                dialog.showPage("Kommentar ansehen/hinzufügen", "erfassung_modal_kommentar.html", 1010, 470, false);},



// ------------------------- Helper functions -------------------------

function alertNotImplementedYet()
{
  alert("Diese Funktionalität ist noch nicht implementiert.");
}

function getSelectedNode(message) {
  if (message instanceof dojo.widget.TreeMenuItemV3)
  {
    return message.getTreeNode();
  }
  else
  {
    return dojo.widget.byId('tree').selectedNode;
  }
}

function createNewNode()
{
	return {contextMenu: 'contextMenu1',
			isFolder: false,
			nodeDocType: 'Class1',
			title: message.get('tree.newNodeName'),
			dojoType: 'ingrid:TreeNode',
			nodeAppType: 'O',
			id: 'newNode'};
}