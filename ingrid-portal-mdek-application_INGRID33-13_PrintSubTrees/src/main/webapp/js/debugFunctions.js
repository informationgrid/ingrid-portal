
function onClickEvent(e) {
	console.debug("onClickEvent");
	//selRowNode = dijit.byId(e.currentTarget.id);
	//selGrid = dijit.byId(e.currentTarget.id);
}

function onCellDouble(e) {
	console.debug("onCellDoubleEvent");
	/*var index = e.rowNode.gridRowIndex;
	var item = e.grid.getItem(index);
	
	if (item == null) {
		e.grid.store.newItem();
	}
	e.grid.onCellDblClickParent(e);*/
}

function gridBlur(){
	dojo.connect(dijit.byId("tableTest2"), "onBlur", function(){
		this.onCellFocus();
	});
}

// just for testing!


			
