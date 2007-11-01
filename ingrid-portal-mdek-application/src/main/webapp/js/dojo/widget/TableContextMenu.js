dojo.provide("ingrid.widget.TableContextMenu");

dojo.require("ingrid.widget.ContextMenu");

/**
 * TableContextMenu provides the context menu for table rows
 */
dojo.widget.defineWidget(
	"ingrid.widget.TableContextMenu",
	ingrid.widget.ContextMenu,
{
  /*
   * Predefined context menu actions
   */
	actions: {
    SELECT_ALL: {caption:message.get('table.selectAll'), method:'selectAllItemsClicked'},
    DESELECT_ALL: {caption:message.get('table.deselectAll'), method:'deselectItemsClicked'},
    DELETE_SELECTED: {caption:message.get('table.deleteSelected'), method:'deleteSelectedItemsClicked', params:{confirm:message.get('table.confirmDeleteSelected')}},
    DELETE: {caption:message.get('table.rowDelete'), method:'deleteItemClicked', params:{confirm:message.get('table.confirmDelete')}}
	},

  /*
   * The table that this context menu is attached to
   */
  table: "",

  /*
   * Get the data belonging to the clicked row
   */
	getRowData: function() {
    var source = this.getTopOpenEvent().target;
    if (source.tagName.toLowerCase() == "td") {
      return this.table.getDataByRow(source.parentNode);
    }
    return null;
	},

  /*
   * Overidden to highlight selected row
   */
	open: function() {
    // enable/disble menu items
    this.enableAllItem();
    if (this.table.getSelectedData().length == 0) {
      this.disableItem(message.get('table.deselectAll'));
      this.disableItem(message.get('table.deleteSelected'));
    }
    if (this.table.getSelectedData().length == this.table.getData().length) {
      this.disableItem(message.get('table.selectAll'));
    }

    var rowData = this.getRowData();
    if (rowData) {
      this.table.focus(rowData);
      return dojo.widget.PopupMenu2.prototype.open.apply(this, arguments);
    }
	},

  /*
   * Menu item event handler
   */
  selectAllItemsClicked: function(menuItem) {
    this.table.selectAll();
    this.table.renderSelections();
  },

  deselectItemsClicked: function(menuItem) {
    this.table.resetSelections();
    this.table.renderSelections();
  },

  deleteSelectedItemsClicked: function(menuItem) {
    var rows = this.table.getSelectedData();
    if (!menuItem.confirm)
      menuItem.confirm = message.get('table.confirmDeleteSelected');
    var _this = this;
/*
		dialog.show(
        message.get('general.delete'),
        menuItem.confirm,
        dialog.WARNING, [
          {caption:message.get('general.ok'),action:function() {
            for(var i=0; i<rows.length; i++)
              _this.table.deleteRow(rows[i]);
          }},
          {caption:message.get('general.cancel'),action:dialog.CLOSE_ACTION}
        ]
    );
*/
    for(var i=0; i<rows.length; i++)
      _this.table.deleteRow(rows[i]);
  },

  deleteItemClicked: function(menuItem) {
    var rowData = this.getRowData();
    if (!menuItem.confirm)
      menuItem.confirm = message.get('table.confirmDelete');
    var _this = this;
/*
		dialog.show(
        message.get('general.delete'),
        dojo.string.substituteParams(menuItem.confirm, _this.table.getDiplayString(rowData)),
        dialog.WARNING, [
          {caption:message.get('general.ok'),action:function() {
            _this.table.deleteRow(rowData);
          }},
          {caption:message.get('general.cancel'),action:dialog.CLOSE_ACTION}
        ]
    );
*/
    _this.table.deleteRow(rowData);
  }
});
