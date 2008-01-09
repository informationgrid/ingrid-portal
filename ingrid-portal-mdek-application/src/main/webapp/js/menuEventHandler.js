<script type="text/javascript" src="js/udkDataProxy.js"></script>

/*
 * Menu Event Handler. Static Methods
 */

var menuEventHandler = {};

// Singleton
// menuEventHandler = new function MenuEventHandler() {}

/*
menuEventHandler.handleNewEntity = function(mes) {
// TODO Dialog fuer neue Objekte / Adressen anzeigen. Reicht der Objektbezeichner im Baum?
//      Muss eine Nachricht an das Backend gesendet werden?
	var deferred = udkDataProxy.checkForUnsavedChanges();

	var createNodeErrback = function(){dojo.debug("New node operation cancelled.");};
	var createNodeCallback = function() {
		dojo.debug("Deferred callback");
		var selectedNode = getSelectedNode(mes);
		if (!selectedNode) {
    		dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
		} else {
			if (!selectedNode.isFolder) {
				selectedNode.setFolder();
				// TODO? Don't load children from the db. The new node is the only children.
				selectedNode.expand();
				menuEventHandler.selectedNode = selectedNode;
				attachNewNode();
			} else if (!selectedNode.isExpanded) {
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
	};

	deferred.addCallbacks(createNodeCallback, createNodeErrback);
}
*/

menuEventHandler.handleNewEntity = function(mes) {
	var deferred = new dojo.Deferred();

	var selectedNode = getSelectedNode(mes);
	if (!selectedNode) {
	  		dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
	} else {
		if (!selectedNode.isFolder) {
			selectedNode.setFolder();
			// TODO? Don't load children from the db. The new node is the only children.
			selectedNode.expand();
		} else if (!selectedNode.isExpanded) {
			var tree = selectedNode.tree;
			var treeController = dojo.widget.byId("treeController");
			var def = treeController.expand(selectedNode);
			// Wait till the treeController is done with node.expand and attach the new node if
			// it was successful
			def.addCallback(function(){
				deferred.addCallback(function(res){attachNewNode(selectedNode, res);});
   				dojo.debug("Publishing event: /createObjectRequest("+selectedNode.id+")");
   				dojo.event.topic.publish("/createObjectRequest", {id: selectedNode.id, resultHandler: deferred});
			});
			return;
		}

		// publish a createObject request and attach the newly created node if it was successful
		deferred.addCallback(function(res){attachNewNode(selectedNode, res);});
		dojo.debug("Publishing event: /createObjectRequest("+selectedNode.id+")");
  		dojo.event.topic.publish("/createObjectRequest", {id: selectedNode.id, resultHandler: deferred});
	}
}

attachNewNode = function(selectedNode, res) {
    dojo.debug("attachNewNode("+selectedNode.id+")");
	dojo.debugShallow(res);
    
    
    var tree = dojo.widget.byId("tree");
    var treeListener = dojo.widget.byId('treeListener');

	var newNode = tree.createNode(_createNewNode(res));
	selectedNode.addChild(newNode);

	var treeController = dojo.widget.byId("treeController");
/*
	tree.selectNode(newNode);
    dojo.event.topic.publish(treeListener.eventNames.deselect, {node: selectedNode});    
    dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});
*/
	tree.selectNode(newNode);
	tree.selectedNode = newNode;
    dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});
	// TODO Add New Node event
//   	dojo.debug('Publishing event: /loadRequest('+newNode.id+', '+newNode.nodeAppType+')');
//   	dojo.event.topic.publish("/loadRequest", {id: newNode.id, appType: newNode.nodeAppType});
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
menuEventHandler.handleMarkDeleted = function() {
	// If QS ...
	
	alertNotImplementedYet();
}


menuEventHandler.handleUnmarkDeleted = function() {alertNotImplementedYet();}
menuEventHandler.handleShowChanges = function() {alertNotImplementedYet();}
//                            		var win = window.open("qs_vergleich.html", 'preview', 'width=720, height=690, resizable=yes, scrollbars=yes, status=yes');
//                            		win.focus();

menuEventHandler.handleShowComment = function() {
    dojo.debug("Publishing event: /loadRequest(38664792-B449-11D2-9A86-080000507261, O)");
    dojo.event.topic.publish("/loadRequest", {id: "38664792-B449-11D2-9A86-080000507261", appType: "O"});
//	alertNotImplementedYet();
}
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

function _createNewNode(obj)
{
	return {contextMenu: 'contextMenu1',
			isFolder: false,
			nodeDocType: obj.nodeDocType,	// Initial display type of a new node
			title: obj.objectName,
			dojoType: 'ingrid:TreeNode',
			nodeAppType: obj.nodeAppType,
			id: obj.uuid};	// "newNode"
}