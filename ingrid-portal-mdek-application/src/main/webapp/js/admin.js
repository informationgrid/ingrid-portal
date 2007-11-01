dojo.addOnLoad(function()
{
  if (dojo.byId('catalogueAdminNamePartner'))
    dojo.byId('catalogueAdminNamePartner').disabled = true;
  
  if (dojo.byId('catalogueAdminNameProvider'))
    dojo.byId('catalogueAdminNameProvider').disabled = true;

  // special context menus for analyse tables
  var analysisContent = dojo.byId('analysisContent');
  if (analysisContent) {
    // default context menus
    var tableIds = ["analysisList1", "analysisList2", "analysisList4", "analysisList5", "analysisList7"];
    for(var i=0; i<tableIds.length; i++) {
      var table = dojo.widget.byId(tableIds[i]);
      if (table) {
        var contextMenu = dojo.widget.createWidget("ingrid:TableContextMenu");
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.SELECT_ALL);
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DESELECT_ALL);
        contextMenu.addSeparator();
        contextMenu.addItemObject({caption:message.get('table.deleteSelectedObj'), method:'deleteSelectedItemsClicked', params:{confirm:message.get('table.confirmDeleteSelectedObj')}});
        contextMenu.addItemObject({caption:message.get('table.rowDeleteObj'), method:'deleteItemClicked', params:{confirm:message.get('table.confirmDeleteObj')}});
        table.setContextMenu(contextMenu);
      }
    }

    // special context menus
    var table = dojo.widget.byId("analysisList3");
    if (table) {
      var contextMenu = dojo.widget.createWidget("ingrid:TableContextMenu");
      contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.SELECT_ALL);
      contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DESELECT_ALL);
      contextMenu.addSeparator();
      contextMenu.addItemObject({caption:message.get('table.deleteSelectedAddressReference'), method:'', params:{confirm:message.get('table.confirmDeleteSelectedAddressReference')}});
      contextMenu.addItemObject({caption:message.get('table.rowDeleteAddressReference'), method:'', params:{confirm:message.get('table.confirmDeleteAddressReference')}});
      table.setContextMenu(contextMenu);
    }
    var table = dojo.widget.byId("analysisList6");
    if (table) {
      var contextMenu = dojo.widget.createWidget("ingrid:TableContextMenu");
      contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.SELECT_ALL);
      contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DESELECT_ALL);
      contextMenu.addSeparator();
      contextMenu.addItemObject({caption:message.get('table.deleteSelectedReference'), method:'', params:{confirm:message.get('table.confirmDeleteSelectedReference')}});
      contextMenu.addItemObject({caption:message.get('table.rowDeleteReference'), method:'', params:{confirm:message.get('table.confirmDeleteReference')}});
      table.setContextMenu(contextMenu);
    }
  }

  // special context menus for url tables
  var urlLists = dojo.byId('urlLists');
  if (urlLists) {
    var tableIds = ["urlListTable1", "urlListTable2"];
    for(var i=0; i<tableIds.length; i++) {
      var table = dojo.widget.byId(tableIds[i]);
      if (table) {
        var contextMenu = dojo.widget.createWidget("ingrid:TableContextMenu");
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.SELECT_ALL);
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DESELECT_ALL);
        table.setContextMenu(contextMenu);
      }
    }
  }

  // TODO: DEMO ONLY - REMOVE LATER
  var tree = dojo.widget.byId('duplicatesTree');
  if (tree)
    tree.selectNode('o1/o2/o3/o31');
});
