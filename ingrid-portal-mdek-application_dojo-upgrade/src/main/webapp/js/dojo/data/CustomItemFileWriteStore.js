dojo.provide("ingrid.data.CustomItemFileWriteStore");
dojo.require("dojo.data.ItemFileWriteStore");

dojo.declare("ingrid.data.CustomItemFileWriteStore", dojo.data.ItemFileWriteStore, {
	structure: [], // is needed when loading another store to copy old structure
	emptyItem: null,
	//minItems: 3,
	firstEmptyRow: 0,
	minRows: 4,
	isInteractive: false,
	
	constructor: function(/* object */ keywordParameters){
		//console.debug("my CustomItemFileWriteStore constructor");
		
		// add more rows to have the minimum
		var itemsNum = this._jsonData.items.length;
		
		// set the position for the first empty row to be drawn
		// (-> row = Array[row]-1)
		this.firstEmptyRow = itemsNum;
		
		if (keywordParameters.minRows)
			this.minRows = keywordParameters.minRows; 
			
		if (keywordParameters.interactive)
			this.isInteractive = keywordParameters.interactive;
		
		this.structure = keywordParameters.structure;
		
		//this.members = ["title", "date", "version"];
		var emptyItem = { _isEmptyRow: true };
		dojo.forEach(keywordParameters.structure, function(m) {emptyItem[m] = "";});
		
		this.emptyItem = emptyItem;
		
		// create at least minRows empty items that can be used to fill up
		for (var i = 0; i < this.minRows; i++) { // +1??? for extra row?
			this._jsonData.items.push(dojo.clone(this.emptyItem));
		}
		/*
		if (this.isInteractive && this.firstEmptyRow == this.minRows) { // && isInteractive!!!
			this._jsonData.items.push(dojo.clone(this.emptyItem));
		}*/
	},
	
	init: function() {
		for (var i = 0; i < this.minRows; i++) {
			this.data.items.push(dojo.clone(this.emptyItem));
		}
	},
	
	//_getItemsArray: function(/*object?*/queryOptions){
	/*	//	summary: 
		//		Internal function to determine which list of items to search over.
		//	queryOptions: The query options parameter, if any.
		var itemsArray = null;
		if (queryOptions && queryOptions.deep) {
			itemsArray = this._arrayOfAllItems;
		}
		else {
			itemsArray = this._arrayOfTopLevelItems;
		}
		//return this._prepareEmptyRows(UtilStore.convertItemsToJS(this, itemsArray));
		var copyOfItems = itemsArray.slice(0, this._arrayOfTopLevelItems.length);
		return this._prepareEmptyRows(copyOfItems);
	},*/
	
	_prepareEmptyRows: function(itemsArray) {
		var numItems = itemsArray.length;//this._arrayOfTopLevelItems.length;
		var nonEmptyItems = dojo.filter(itemsArray, function(item) {return item._isEmptyRow != "true"}).length;//this._getDataItemsArray().length;
		
		/*for (var i = numItems; i < this.minRows; i++) {
			result.store.newItem(dojo.clone(this.emptyItem));
		}*/
		
		// remove all empty rows from the end of the array
		if (nonEmptyItems < this.minRows) {
			for (var i = 0; i < nonEmptyItems; i++) {
				itemsArray.pop();
			}
		} else {
			var keepOneRow = this.isInteractive ? 1 : 0;
			for (var i = 0; i < this.minRows - keepOneRow; i++) {
				itemsArray.pop();
			}
		}
		return itemsArray;
	},
	
	newItem: function(/* Object? */ keywordArgs, /* Object? */ parentInfo){
		// summary: See dojo.data.api.Write.newItem()

		var aNewItem = this.inherited(arguments);
		
		// move new item to the first empty row
		this._moveItemToFirstEmptyRow(aNewItem, true, false);
		
		// call it again after modification
		this.onNew(aNewItem);
		
	},
	
	deleteItem: function(/* item */ item) {
		// do not delete empty rows
		// they will be filtered out when items are requested
		if (item._isEmptyRow)
			return;
			
		this.inherited(arguments);
	},
	

	_findAndSetFirstEmptyRow: function() {
		var itemIdDescriptor = this._itemNumPropName;
		var pos = 0;
		dojo.some(this._arrayOfTopLevelItems, function(item) {
			if (item._isEmptyRow) {
				return true;
			}
			pos++;
		});
		// if none was found add an extra row here!
		/*if (this.isInteractive && newValue == -1) {
			this._addEmptyRow();
			//newValue = this._arrayOfTopLevelItems.length-1;
			newValue = this._arrayOfAllItems.length-1;
		}*/
		
		this.firstEmptyRow = pos;
	},
	
	_moveItemToFirstEmptyRow: function(item, isNew, updateTopLevelItems) {
		// check if we need a new row or if we need to remove one (minRows!)
		// might influence the second splice parameter 
		this._findAndSetFirstEmptyRow();
					
		
		//console.debug("first empty row: " + this.firstEmptyRow);
		
		// item already is added to the end of the list
		if (this.firstEmptyRow == -1) return;
		
		// if isNew!!!
		//this._arrayOfAllItems.splice(this.firstEmptyRow, 0, item);
		//if (updateTopLevelItems)
			this._arrayOfTopLevelItems.splice(this.firstEmptyRow, 0, item);
			// and now remove item
			this._arrayOfTopLevelItems.pop();
/*
		for (var i=this.firstEmptyRow; i<this._arrayOfAllItems.length;i++) {
			if (this._arrayOfAllItems[i] != null)
				this._arrayOfAllItems[i][this._itemNumPropName] = i;
		}*/
		//this.firstEmptyRow++;
	},
	
	
	setValue: function(/* item */ item, /* attribute-name-string */ attribute, /* almost anything */ value){
		// summary: See dojo.data.api.Write.set()
		// instead of changing the attribute of this item we add a new item with the attribute
		// so we keep the empty row!
		if (!item._isEmptyRow)
			var result = this._setValueOrValues(item, attribute, value, true); // boolean
		
		// remove empty row flag when setting value
		if (item._isEmptyRow && value != null && value != "") {
			var aNewItem = dojo.clone(this.emptyItem);
			delete aNewItem._isEmptyRow;
			aNewItem[attribute] = value;
			this.newItem(aNewItem);
			//this.onNew();

		}
		
		return result;
	},

	_containsValue: function(	/* item */ item, 
								/* attribute-name-string */ attribute, 
								/* anything */ value,
								/* RegExp?*/ regexp){
		/* see ItemFileReadStore.js */
		// if item is empty row return true
		if (item._isEmptyRow)
			return true;
			
		return this.inherited(arguments);
	},
	
	_fetchItems: function(	/* Object */ keywordArgs, 
							/* Function */ findCallback, 
							/* Function */ errorCallback){
								
		// call another function that prepares the items before the callback is called
		// here the correct numbers of rows will be calculated (also after a query
		// was evaluated)
		var _beforeFunction = function(findCallback, items, requestObject) {
			findCallback(this._prepareEmptyRows(items), requestObject);
		};
		arguments[1] = dojo.partial(dojo.hitch(this,_beforeFunction), findCallback);
		this.inherited(arguments);								
	},
	
	_getDataItemsArray: function(/*object?*/queryOptions){
		//	summary: 
		//		Internal function to determine which list of items to search over.
		//	queryOptions: The query options parameter, if any.
		if(queryOptions && queryOptions.deep){
			return dojo.filter(this._arrayOfAllItems, function(item) {return item._isEmptyRow != "true"}); 
		}
		
		// remove empty items from result list
		return dojo.filter(this._arrayOfTopLevelItems, function(item) {return item._isEmptyRow != "true"});
	}

});