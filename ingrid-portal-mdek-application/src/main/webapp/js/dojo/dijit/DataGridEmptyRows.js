dojo.provide("ingrid.dijit.DataGridEmptyRows");

dojo.require("dojox.grid.DataGrid");

dojo.declare("ingrid.dijit.DataGridEmptyRows", dojox.grid.DataGrid, {
	//emptyItem: {},
	//minRows: 4,

	_onNew: function(item, parentInfo){
		this._checkUpdateStatus();
		this._updateRowCount();
			
		/*var rowCount = this.get('rowCount');
		this._addingItem = true;
			this.updateRowCount(rowCount+1);
		this._addingItem = false;
		this._addItem(item, rowCount);*/
		this.showMessage();

		this._fetch(0, true);
		//this.render();		
	},
	
	
	_onDelete: function(item){
		this._updateRowCount();
		//this.inherited(arguments);
		//return;
		//this._fetch(0, true);
		//this.render();
		
		this._checkUpdateStatus();
		var idx = this._getItemIndex(item, true);

		if(idx >= 0){
			// When a row is deleted, all rest rows are shifted down,
			// and migrate from page to page. If some page is not 
			// loaded yet empty rows can migrate to initialized pages
			// without refreshing. It causes empty rows in some pages, see:
			// http://bugs.dojotoolkit.org/ticket/6818
			// this code fix this problem by reseting loaded page info
			this._pages = [];
			this._bop = -1;
			this._eop = -1;

			var o = this._by_idx[idx];
			this._by_idx.splice(idx, 1);
			delete this._by_idty[o.idty];
			this.updateRowCount(this.get('rowCount'));//-1);
			if(this.get('rowCount') === 0){
				this.showMessage(this.noDataMessage);
			}
			
			//this._fetch(idx, true);
			//this.render();
		}
		//this.render();
	},
	
	_updateRowCount: function() {
		var nonEmptyDataItems = this.store._getDataItemsArray().length;
		if (nonEmptyDataItems > this.store.minRows)
			if (this.store.isInteractive)
				this.rowCount = nonEmptyDataItems+2;
			else
				this.rowCount = nonEmptyDataItems;
		else if (nonEmptyDataItems == this.store.minRows)
			if (this.store.isInteractive)
				this.rowCount = parseInt(this.store.minRows)+2;
			else
				this.rowCount = parseInt(this.store.minRows);
		else
			this.rowCount = parseInt(this.store.minRows);
	},
	
	clear: function() {
		// save changes (losing revert possibility)
		this.store.save();
		// prepare new data
		this.store.data = {items:[]};
		// fill with empty rows
		this.store.init();
		this.store.close();
		// update datagrid
		this._refresh();
	}
});