/*
 * Menu Event Handler. Static Methods
 */

var menuEventHandler = {};


menuEventHandler.handleNewEntity = function() {
// TODO Dialog fuer neue Objekte / Adressen anzeigen. Reicht der Objektbezeichner im Baum?
//      Muss eine Nachricht an das Backend gesendet werden?
//      Nachricht an Backend ja? Id generiert in der Datenbank.
  var tree = dojo.widget.byId('tree');
  var selectedNode = tree.selectedNode;
  if (!selectedNode)
    dialog.show(message.get('general.hint'), message.get('tree.selectNodeHint'), dialog.WARNING);
  else {
    var dlg = 'erfassung_objekt_anlegen.html';
    if (selectedNode.nodeAppType == "A")
      dlg = 'erfassung_adresse_anlegen.html';
    dialog.showPage(message.get('tree.nodeNew'), dlg, 502, 130, true, {treeId:'tree', controllerId:'treeController', nodeId:selectedNode.widgetId});
  }
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

menuEventHandler.handleCut = function() {alertNotImplementedYet();}
menuEventHandler.handleCopyEntity = function() {alertNotImplementedYet();}
menuEventHandler.handleCopyTree = function() {alertNotImplementedYet();}
menuEventHandler.handlePaste = function() {alertNotImplementedYet();}

menuEventHandler.handleSave = function() {alertNotImplementedYet();}
//                                dialog.show("Zwischenspeichern", 'Der aktuelle Datensatz befindet sich in der Bearbeitung. Wollen Sie wirklich speichern?', dialog.WARNING, 
//                                      [{caption:"OK",action:function(){alert("OK")}},{caption:"Cancel",action:dialog.CLOSE_ACTION}]);},

menuEventHandler.handleUndo = function() {alertNotImplementedYet();}
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


function alertNotImplementedYet()
{
  alert("Diese Funktionalität ist noch nicht implementiert.");
}