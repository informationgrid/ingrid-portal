dojo.provide("ingrid.dijit.CustomDataGridRender");

dojo.require("dojox.grid.DataGrid");

dojo.declare("ingrid.dijit.CustomDataGridRender", dojox.grid.DataGrid, {
	emptyItem: {},
	minRows: 4,

    render: function() {
		this.inherited(arguments);
	    //ingrid.widget.FilteringTable.superclass.render.apply(this, arguments);
	    /*var body = this.domNode.tBodies[0];

	    // remove empty rows
	    if (body.rows.length > 0) {
	      var lastRow = body.rows[body.rows.length-1];
	  		while (lastRow && dojo.hasAttribute(lastRow, "emptyRow")) {
	  			body.removeChild(lastRow);
	        if (body.rows.length > 0)
	          lastRow = body.rows[body.rows.length-1];
	        else
	          lastRow = null;
	  		}
	    }
		*/
	    // if the table is interactive add at least one empty ror for insert
	    //if(dojo.hasClass(this.domNode, 'interactive') && !dojo.hasClass(this.domNode, 'readonly'))
//	      this.addEmptyRow();
		  
	
	    // add additional rows to fill minRows
		// While adding rows to reach minRows the hidden rows have to be included too.
		// First we check how many rows are hidden and add enough rows to reach 'this.minRows' number of rows
	    /*
		var rows = body.rows;
		numHiddenRows = 0;
		for (var i = 0; i < rows.length; ++i) {
			if (rows[i].style.display == "none")
				numHiddenRows++;
		}
	    while (rows.length - numHiddenRows < this.minRows) {
	      this.addEmptyRow();
	      rows = body.rows;
	    }

		// The overriden function resets the alternate row classes but it doesn't take filtered
		// entries into account. The result is a false display.
		// Because of this the rows have to be reevaluated while properly taking the row.style.display
		// element into account.

		// Reapply alternate row classes
		if (this.alternateRows) {
			var displayIndex = 0;
			// Iterate over all the rows and check if they are displayed or not
			for(var i = 0; i < rows.length; i++){
				var row = rows[i];
				// If the current row is displayed, apply the proper display class and increase the displayIndex
				if (row.style.display == "") {
					dojo.html[((displayIndex % 2 == 1)?"addClass":"removeClass")](row, this.rowAlternateClass);
					displayIndex++;
				}
			}
		}

		// If the user is currently editing don't update the Display Values
		if (this.isEditing) {
			return;
		}
		// Update the displayed values. This needs to be done so we can use the various
		// functions from the underlying store (e.g. setData). Otherwise the displayed values
		// will not be resolved properly (key is displayed instead of the 'displayValue()').

		// Iterate over all columns and check if the column is associated with an editor
		for (var j=0; j<this.columns.length; j++) {
			if (this.columns[j].editor) {
				// the current column j has an editor attached
				var currentEditor = this.columns[j].editor;
				var rows = this.domNode.tBodies[0].rows;
				// Iterate over all rows and update the displayed values
				for(var i=0; i<rows.length; i++){
					var rowData = this.getDataByRow(rows[i]);
					// We have to check if the row exists(?)
					if (rowData) {
						var fieldContent = rowData[this.columns[j].getField()];
						// Set the value of the current Editor, get the displayed value
						// and update the displayed value of the table 
						currentEditor.setValue(fieldContent);
						if (currentEditor.getDisplayValue) {
					       	var displayValue = currentEditor.getDisplayValue();
      						this.fillCell(rows[i].cells[j], this.columns[j], displayValue);
					    }
					}
				}
			}
		}*/
	},
	
	addEmptyRow: function() {
		var body = dojo.query("#"+this.id+" .dojoxGridMasterView .dojoxGridRowTable")[0].tBodies[0];//this.domNode.tBodies[0];
		var row = document.createElement("tr");
		/*if(this.alternateRows){
			dojo.html[((body.rows.length % 2 == 1)?"addClass":"removeClass")](row, this.rowAlternateClass);
		}*/
		row.setAttribute("emptyRow","true");
		for(var j=0; j<this.structure.length; j++) {
			var cell = document.createElement("td");
			cell.innerHTML = "&nbsp;";
			row.appendChild(cell);
		}
		body.appendChild(row);
		this.addRow();
	}

	
});