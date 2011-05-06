dojo.provide("ingrid.dijit.CustomEnhancedDataGrid");

dojo.require("dojox.grid.EnhancedGrid");

// modify the default value when showing an empty row
dojo.extend(dojox.grid.cells._Base, {defaultValue: "."});

dojo.declare("ingrid.dijit.CustomEnhancedDataGrid", dojox.grid.EnhancedGrid, {
	emptyItem: {},
	minRows: 4,
	interactive: "false",
	
	_isAddingEmptyRow: false,
	
	_copyAttr: function(idx, attr){
		var row = {};
		var backstop = {};
		var src = this.getItem(idx);
		if (src == null) {
			this._isAddingEmptyRow = true;
			src = this.store.newItem(this.emptyItem);
			this._addItem(src, idx);
			//this._isAddingEmptyRow = false;
		}
		return this.store.getValue(src, attr);
	},
	
    _onFetchBegin: function(size, req){
        // use correct size with empty rows
        size = this._calcItemsLength(size);
        if(!this.scroller){ return; }
        if(this.rowCount != size){
            if(req.isRender){
                this.scroller.init(size, this.keepRows, this.rowsPerPage);
                this.rowCount = size;
                this._setAutoHeightAttr(this.autoHeight, true);
                this._skipRowRenormalize = true;
                this.prerender();
                this._skipRowRenormalize = false;
            }else{
                this.updateRowCount(size);
            }
        }
        if(!size){
            this.views.render();
            this._resize();
            this.showMessage(this.noDataMessage);
            this.focus.initFocusView();
        }else{
            this.showMessage();
        }
    },
    
	_onFetchComplete: function(items, req){
		if(!this.scroller){ return; }
		//-------------------------
		// calculate length with empty items and set new row-count
        // need to update all in case not the next empty line was edit
        // but a later one
		//-------------------------
		var length = this._calcItemsLength(items.length);
		
		if (items && items.length > 0) {
			dojo.forEach(items, function(item, idx){
				this._addItem(item, req.start + idx, true);
			}, this);
			//if (this._autoHeight) {
				//this._skipRowRenormalize = true;
			//}
			//this.updateRowCount(length);
			this.updateRows(req.start, length);
			//if (this._autoHeight) {
				//this._skipRowRenormalize = false;
			//}
			if (req.isRender) {
				this.setScrollTop(0);
				this.postrender();
			}
			else 
				if (this._lastScrollTop) {
					this.setScrollTop(this._lastScrollTop);
				}
		}
		else {
			//this.updateRowCount(length);
		}
		delete this._lastScrollTop;
		if(!this._isLoaded){
			this._isLoading = false;
			this._isLoaded = true;
		}
		this._pending_requests[req.start] = false;
        
	},
	
	_calcItemsLength: function(itemLength) {
		if (!itemLength)
			itemLength = this.store._arrayOfTopLevelItems.length;
			
		if (itemLength > this.minRows)
			if (this.interactive == "true")
				return itemLength+1;
			else
				return itemLength;
		else if (itemLength == this.minRows)
			if (this.interactive == "true")
				return parseInt(this.minRows)+1;
			else
				return parseInt(this.minRows);
		else
			return parseInt(this.minRows);
	},
	
	
	_onNew: function(item, parentInfo){
		if (this._isAddingEmptyRow)
			return;
		
		//this._fetch(0, true);
		this.render();		
	},
	
	_onDelete: function(item){
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
			var length = this._calcItemsLength(this.store._arrayOfTopLevelItems.length);
			this.updateRowCount(length);
			if(this.get('rowCount') === 0){
				this.showMessage(this.noDataMessage);
			}
		}
		//this.updateRowCount(length);
		
	},
	
	removeSelectedRows: function(){
		// summary:
		//		Remove the selected rows from the grid.
		if(this._canEdit){
			this.edit.apply();
			var fx = dojo.hitch(this, function(items){
				if(items.length){
					dojo.forEach(items, function(item) {
						if (item) { // in case of an empty fake row
							this.save();
							this.deleteItem(item);
						}
					}, this.store);
					this.selection.clear();
				}			
			});
			if(this.allItemsSelected){
				this.store.fetch({
							query: this.query, 
							queryOptions: this.queryOptions,
							onComplete: fx});
			}else{
				fx(this.selection.getSelected());
			}
		}
	},

	onApplyCellEdit: function(inValue, inRowIndex, inFieldIndex){
		this._isAddingEmptyRow = false;
		var numItems = this.store._arrayOfTopLevelItems.length;
		var calcItems = this._calcItemsLength(this.store._arrayOfTopLevelItems.length);
		if (numItems >= this.minRows && calcItems != this.rowCount && numItems < calcItems) { // should differ only by one!
			this.addRow();
		}
		
		// if a new row other than the next empty one was edited
		// -> do a refresh
		if (inRowIndex > numItems - 1) {
            var thisTable = this;
            // refresh need to be called delayed
            setTimeout(function(){thisTable._refresh(true);}, 100);//this._refresh(true);
        }
	},
	
	onCancelEdit: function(inRowIndex) {
		// remove item from store if it was an empty one
		if (this._isAddingEmptyRow) {
			var src = this.getItem(inRowIndex);
			this.store.deleteItem(src);
			this._isAddingEmptyRow = false;
		}
	},
	
	// leave edit state of any editable cell when grid lost focus
	onBlur: function() {
		this.edit.apply();
	},
	
	clear: function() {
		// save changes (losing revert possibility)
		this.store.save();
		// prepare new data
		this.store.data = {items:[]};
		this.store.close();
		// update datagrid
		this._refresh();
	},
	
    _refresh: function(isRender){
        // improve update speed ... consequences?
        this.beginUpdate();
        this._clearData();
        this.updating = false;
        this._fetch(0, isRender);
    },
    
	onSetStore: function(/* DataStore */ store){
		// summary: See dojo.data.api.Notification.onNew()
		
		// No need to do anything. This method is here just so that the 
		// client code can connect observers to it. 
	},
	
	setStore: function(store, query, queryOptions){
		this.inherited(arguments);
		this.onSetStore(store);
	}
    
});