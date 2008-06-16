dojo.provide("ingrid.widget.FilteringTable");

dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");

/**
 * Table implementation, enhanced with context menu and cell editors
 * - To set a context menu add a contextMenu attribute to the table tag
 * - To set a cell editor add a editor attribute to the th tag
 */
dojo.widget.defineWidget(
  "ingrid.widget.FilteringTable",
  dojo.widget.FilteringTable,
  function() {
    this.rowFocusedClass="focused";
    // Allow multiple selections.
  	this.multiple="true";
  	// Maximum number of columns allowed for sorting at one time.
  	this.maxSortable=1;  // how many columns can be sorted at once.
  	// Use alternate row CSS classes to show zebra striping.
  	this.alternateRows="true";
  	// Filter function map contains objects with two values:
  	//  target - string identifying the target field for the filter func
  	//  filterFunction - function with one argument (value) which returns true if the row should be displayed
  	//                   and false otherwise
  	this.filterFunctionMap=[];
	
	// Validate function map contains objects with two values:
  	//  target - string identifying the target field for the filter func
  	//  validateFunction - function with one argument (value) which returns true if the entry is valid and
	//					   false otherwise
	this.validateFunctionMap=[];
	this.fieldInvalidClass="tableFieldInvalid";
  },
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/FilteringTable.css"),

  /*
   * The focused row (HTMLTableRow)
   */
  focusedRow: "",

  /*
   * The field that is used when displaying a row (see getDiplayString)
   */
  displayField: "Id",

  /*
   * Set this true to use a custom context menu (use setContextMenu to set it)
   */
  customContextMenu: false,

  /*
   * The context menu (use setContextMenu to set it)
   */
  _contextMenuInst: null,

  /*
   * Set the context menu for the table
   */
  setContextMenu: function(menuId) {
    var menu = dojo.widget.byId(menuId);
    if (menu) {
      if (menu.table)
        dojo.debug("context menu "+menuId+" is already attached to "+menu.table);
      menu.table = this;
      menu.bindDomNode(this.domNode);
      this._contextMenuInst = menu;
    }
  },

  /*
   * Get the context menu for the table
   */
  getContextMenu: function() {
    return this._contextMenuInst;
  },

  removeContextMenu: function() {
    if (this._contextMenuInst) {
      this._contextMenuInst.unBindDomNode(this.domNode);
      this._contextMenuInst = null;
    }
  },

  disableContextMenu: function() {
    if (this._contextMenuInst) {
      this._contextMenuInst.unBindDomNode(this.domNode);
    }
  },

  enableContextMenu: function() {
    if (this._contextMenuInst) {
      this._contextMenuInst.bindDomNode(this.domNode);
    }
  },


  /*
   * Add default context menus
   */ 
  initialize: function() {
    // parse properties from string if necessary
    this.multiple = this.multiple == "true" || this.multiple == true;
    this.customContextMenu = this.customContextMenu == "true" || this.customContextMenu == true;

	var self=this;
	//	connect up binding listeners here.
	dojo.event.connect("before", this.store, "onSetData", function(){
		self._disconnectEditorEvents();
		self.save();
	});

    ingrid.widget.FilteringTable.superclass.initialize.apply(this, arguments);
  },

  /*
   * callback when value changes, for user to attach to
   */
  onValueChanged: function(obj, field){
  },

  /*
   * callback when value is added, for user to attach to
   */
  onValueAdded: function(obj){
  },

  /*
   * callback when value is deleted, for user to attach to
   */
  onValueDeleted: function(obj){
  },
  
	//	override default behaviour for selections
	onSelect: function(/* HTMLEvent */e){
		//	summary
		//	Handles the onclick event of any element.
		var row = dojo.html.getParentByType(e.target,"tr");
		if(dojo.html.hasAttribute(row,"emptyRow")){
			return;
		}
		var body = dojo.html.getParentByType(row,"tbody");
		if(this.multiple){
			if(e.shiftKey){
				var startRow;
				var rows=body.rows;
				for(var i=0;i<rows.length;i++){
					if(rows[i]==row){
						break;
					}
					if(this.isRowSelected(rows[i])){
						startRow=rows[i];
					}
				}
				if(!startRow){
					startRow = row;
					for(; i<rows.length; i++){
						if(this.isRowSelected(rows[i])){
							row = rows[i];
							break;
						}
					}
				}
				this.resetSelections();
				if(startRow == row){
					this.toggleSelectionByRow(row);
				} else {
					var doSelect = false;
					for(var i=0; i<rows.length; i++){
						if(rows[i] == startRow){
							doSelect=true;
						}
						if(doSelect){
							this.selectByRow(rows[i]);
						}
						if(rows[i] == row){
							doSelect = false;
						}
					}
				}
			} else if (e.ctrlKey) {
				this.toggleSelectionByRow(row);
			} else {
				this.resetSelections();
				this.toggleSelectionByRow(row);
			}
		} else {
			this.resetSelections();
			this.toggleSelectionByRow(row);
		}
		this.renderSelections();
	},
	
  /*
   * Overidden to connect double click event (opens cell editor) and add 
   */
  postCreate: function() {
    // add editor to metadata
    this._meta['editor'] = "";

    if(dojo.html.hasClass(this.domNode, 'interactive') && !dojo.html.hasClass(this.domNode, 'readonly')) {
      // connect the couble click event for editing
      dojo.event.connectOnce(this.domNode.tBodies[0], "ondblclick", this, "onDblClick");

      if (!this.customContextMenu) {
        // set default context menu on interactive/writable tables
        var contextMenu = dojo.widget.createWidget("ingrid:TableContextMenu");
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.SELECT_ALL);
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DESELECT_ALL);
        contextMenu.addSeparator();
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DELETE_SELECTED);
        contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DELETE);
        this.setContextMenu(contextMenu.widgetId);
      }
    }
    
    if (dojo.html.hasClass(this.domNode, 'nosort')) {
		this.createSorter = function(info) {
			var self=this;
			return function(rowA, rowB) {
				if(dojo.html.hasAttribute(rowA,"emptyRow")){ return 1; }
				if(dojo.html.hasAttribute(rowB,"emptyRow")){ return -1; }
				// TODO use store.valueField?
				var a = self.store.getField(self.getDataByRow(rowA), self.displayField);
				var b = self.store.getField(self.getDataByRow(rowB), self.displayField);
				var ret = 0;
				if(a > b) ret = 1;
				if(a < b) ret = -1;
				return ret;
			}
		}
    }

/* BEGIN COPY */
/* Copy of postCreate from the superclass to enable text selection */

	//	summary
	//	finish widget initialization.
	this.store.keyField = this.valueField;

	if(this.domNode){
		//	start by making sure domNode is a table element;
		if(this.domNode.nodeName.toLowerCase() != "table"){
		}

		//	see if there is columns set up already
		if(this.domNode.getElementsByTagName("thead")[0]){
			var head=this.domNode.getElementsByTagName("thead")[0];
			if(this.headClass.length > 0){
				head.className = this.headClass;
			}
//			dojo.html.disableSelection(this.domNode);
			this.parseMetadata(head);

			var header="td";
			if(head.getElementsByTagName(header).length==0){
				header="th";
			}
			var headers = head.getElementsByTagName(header);
			for(var i=0; i<headers.length; i++){
				if(!this.columns[i].noSort){
					dojo.event.connect(headers[i], "onclick", this, "onSort");
				}
			}
		} else {
			this.domNode.appendChild(document.createElement("thead"));
		}

		// if the table doesn't have a tbody already, add one and grab a reference to it
		if (this.domNode.tBodies.length < 1) {
			var body = document.createElement("tbody");
			this.domNode.appendChild(body);
		} else {
			var body = this.domNode.tBodies[0];
		}

		if (this.tbodyClass.length > 0){
			body.className = this.tbodyClass;
		}
		dojo.event.connect(body, "onclick", this, "onSelect");
		this.parseData(body);
	}
/* END COPY */
  },

  /*
   * Overidden to disconnect context menu
   */
	destroy: function(finalize) {
    if (this._contextMenuInst)
      this._contextMenuInst.unBindDomNode(this.domNode);
    dojo.widget.FilteringTable.prototype.destroy.apply(this, arguments);
  },

  /*
   * Overidden to parse in cell editors
   */
  parseMetadata: function(/* HTMLTableHead */head){
    dojo.widget.FilteringTable.prototype.parseMetadata.apply(this,arguments);

    // TODO: speed up by not iterating over cells twice
		var row = head.getElementsByTagName("tr")[0];
		var cells = row.getElementsByTagName("td");
		if (cells.length == 0){
			cells = row.getElementsByTagName("th");
		}
    for(var i=0; i<cells.length; i++){
      // editor attribute
      if(dojo.html.hasAttribute(cells[i], "editor")) {
        var editorId = dojo.html.getAttribute(cells[i], "editor");
        var editor = dojo.widget.byId(editorId);
        if (editor)
        {
          editor.hide();
          dojo.event.connect(editor, "onShow", this, "focusEditor");

          // check if the editor is embedded into a div with class 'cellEditors' and id widgetId+'Editors'
          var editorContainerElem = editor.domNode.parentNode;
          var editorContainerId = this.widgetId+'Editors';
          if (!dojo.html.hasClass(editorContainerElem, 'cellEditors') || (editorContainerElem.nodeName.toLowerCase() != 'div') || 
              (editorContainerElem.getAttribute('id') != editorContainerId)) {
            dojo.debug("Editor "+editorId+" is not embedded into a div with class 'cellEditors' and id '"+editorContainerId+"'. The editor will be ignored.");
            continue;
          }

          // check if the cell editor container and the table are in the same div with class 'tableContainer'
          var editorContainerParentElem = editorContainerElem.parentNode;
          var tableContainerElem = this.domNode.parentNode;
          if (editorContainerParentElem != tableContainerElem || !dojo.html.hasClass(tableContainerElem, 'tableContainer')) {
            dojo.debug("Editor container '"+editorContainerId+"' and table '"+this.widgetId+
              "' are not embedded into the same div with class 'tableContainer'. The editor will be ignored.");
            continue;
          }

          // no error -> attach the editor
          this.columns[i].editor = editor;

          // check if we have a create-only editor
          this.columns[i].createOnly = false;
          if(dojo.html.hasAttribute(cells[i], "createOnly")) {
            var createOnly = dojo.html.getAttribute(cells[i], "createOnly");
            if (createOnly)
              this.columns[i].createOnly = true;
          }
        }
      }
    }
  },

  /*
   * Overidden to guarantee minRows in table
   */
	render: function() {
	    ingrid.widget.FilteringTable.superclass.render.apply(this, arguments);
	    var body = this.domNode.tBodies[0];

	    // remove empty rows
	    if (body.rows.length > 0) {
	      var lastRow = body.rows[body.rows.length-1];
	  		while (lastRow && dojo.html.hasAttribute(lastRow, "emptyRow")) {
	  			body.removeChild(lastRow);
	        if (body.rows.length > 0)
	          lastRow = body.rows[body.rows.length-1];
	        else
	          lastRow = null;
	  		}
	    }
	
	    // if the table is interactive add at least one empty ror for insert
	    if(dojo.html.hasClass(this.domNode, 'interactive') && !dojo.html.hasClass(this.domNode, 'readonly'))
	      this.addEmptyRow();
	
	    // add additional rows to fill minRows
		// While adding rows to reach minRows the hidden rows have to be included too.
		// First we check how many rows are hidden and add enough rows to reach 'this.minRows' number of rows
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
		}
	},


	// Returns a filtermap entry with the specified field as target
	getFilter: function(/* string */field) {
		for(var i=0; i<this.filterFunctionMap.length; i++){
			if(this.filterFunctionMap[i].target == field){
				return this.filterFunctionMap[i];
			}
		}
	},

	setFilter: function(/* string */field, /* function */fn){
		//	summary
		//	set a filtering function on the passed field.
		var filter = this.getFilter(field);
		if (filter) {
			dojo.debug("Filter for field '"+field+"' already defined. Overwriting filter.");
			filter.filterFunction = fn;
		} else {
			this.filterFunctionMap.push({target: field, filterFunction: fn});
		}
		this.applyFilters();
	},

	applyFilters: function(){
		//	summary
		//	apply all filters to the table.
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			row.style.display = "";
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				// Apply all filter functions
				for (var j = 0; j < this.filterFunctionMap.length; j++) {
					var filter = this.filterFunctionMap[j];
					// Search for the field associated with the current filter
					var value = this.store.getField(rowData, filter.target);
					if (value) {
						// If the corresponding value was found, apply the filter
						if (!filter.filterFunction(value)) {
							row.style.display = "none";
						}
					} else {
						dojo.debug("Error in ingrid:FilteringTable.applyFilters(). A matching value for the field "+filter.target+" could not be found.");
					}
				}
			}
		}

		this.onFilter();
	},

	clearFilter: function(/* string */field) {
		var newFilterMap = [];
		for (var i = 0; i < this.filterFunctionMap.length; i++) {
			if (this.filterFunctionMap[i].target != field) {
				newFilterMap.push(this.filterFunctionMap[i]);
			}
		}
		this.filterFunctionMap = newFilterMap;
		this.applyFilters();
		this.render();
	},

	clearFilters: function() {
		this.filterFunctionMap = [];
		this.applyFilters();	
		this.render();
	},

	isValid: function() {
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				// Apply all validate functions
				for (var j = 0; j < this.validateFunctionMap.length; j++) {
					var validate = this.validateFunctionMap[j];
					// Search for the field associated with the current validationFunc
					var value = this.store.getField(rowData, validate.target);
					var columnIndex = this.getColumnIndex(validate.target);
					if (columnIndex != -1) {
						// If the corresponding value was found, and is invalid return false
						if (!validate.validateFunction(value)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	},


	applyValidation: function() {
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				// Apply all validate functions
				for (var j = 0; j < this.validateFunctionMap.length; j++) {
					var validate = this.validateFunctionMap[j];
					// Search for the field associated with the current validationFunc
					var value = this.store.getField(rowData, validate.target);
					var columnIndex = this.getColumnIndex(validate.target);
					if (columnIndex != -1) {
						// If the corresponding value was found, apply the filter
						if (!validate.validateFunction(value)) {
							dojo.html.addClass(row.cells[columnIndex], this.fieldInvalidClass);
						} else {
							dojo.html.removeClass(row.cells[columnIndex], this.fieldInvalidClass);
						}
					} else {
						dojo.debug("Error in ingrid:FilteringTable.validate(). A matching value for the field "+validate.target+" could not be found.");
					}
				}
			}
		}
	},

	// Returns a validation map entry with the specified field as target
	getValidation: function(/* string */field) {
		for(var i=0; i<this.validateFunctionMap.length; i++){
			if(this.validateFunctionMap[i].target == field){
				return this.validateFunctionMap[i];
			}
		}
	},


	setValidationFunction: function(/* string */field, /* function */fn){
		//	summary
		//	set validation function on the passed field.
		var validation = this.getValidation(field);
		if (validation) {
			dojo.debug("Validation Function for field '"+field+"' already defined. Overwriting function.");
			validation.validationFunction = fn;
		} else {
			this.validateFunctionMap.push({target: field, validationFunction: fn});
		}
		this.applyValidation();
	},

	setValidationFunctions: function(/* function map */fnMap){
		//	summary
		//	setter for the validationFunction Map
		this.validateFunctionMap = fnMap;
	},
	
	
  addEmptyRow: function() {
    var body = this.domNode.tBodies[0];
    var row = document.createElement("tr");
    if(this.alternateRows){
      dojo.html[((body.rows.length % 2 == 1)?"addClass":"removeClass")](row, this.rowAlternateClass);
    }
    row.setAttribute("emptyRow","true");
    for(var j=0; j<this.columns.length; j++) {
      var cell = document.createElement("td");
      cell.innerHTML = "&nbsp;";
      row.appendChild(cell);
    }
    body.appendChild(row);
  },

  /*
   * Open the cell editor if existing
   */ 
  onDblClick: function(/* HTMLEvent */e) {
    dojo.widget.FilteringTable.prototype.onSelect.apply(this, arguments);
    //dojo.debug("dblclicked: "+this.getSelectedData().Id+"|"+e.target.cellIndex);
    var column = this.columns[e.target.cellIndex];
    if (column && column.editor)
      this.beginEdit(e);
  },

  /*
   * Focus the row holding obj data
   */
  focus: function(/*object*/ obj) {
    this.focusedRow = this.getRow(obj);
    // render focus
    var body=this.domNode.tBodies[0];
    for(var i=0; i<body.rows.length; i++){
      dojo.html[(this.isRowFocused(body.rows[i])?"addClass":"removeClass")](body.rows[i], this.rowFocusedClass);
    }
  },

  /*
   * Check if a row is focused
   */
  isRowFocused: function(/*HTMLTableRow*/ row) {
    return (row == this.focusedRow);
  },

  /*
   * Data management
   */
  deleteRow: function(/*obj*/ obj) {
    this.store.removeData(obj);
    this.onValueDeleted(obj);
  },

  /*
   * Data management
   */
	clear: function() {
		var data=this.store.get();
		for(var i=0; i<data.length; i++){
			this.deleteRow(data[i].src);
		}
	},

  getData: function() {
    return this.store.getData();
  },

  getDiplayString: function(rowData) {
    return rowData[this.displayField];
  },

  /*
   * Inline editing
   */

  origValue: "",
  editData: null,
  curRow: null,
  curColumn: null,
  curEditor: null,
  curTD: null,
  isEditing: false,
  curFP: null,

  beginEdit: function(e) {
    // return if an already edited cell is double clicked
    if (this.isEditing && e.target == this.curTD)
      return;

    // close current editor
    if (this.curEditor)
      this.save();

    // determine selection
    this.curColumn = e.target.cellIndex;
    this.editData = this.getDataByRow(e.target.parentNode);
    if (this.editData) {
      // the already row exists
      if (this.columns[this.curColumn].createOnly)
        return;

      this.curRow = this.editData[this.valueField];
      this.origValue = this.editData[this.columns[this.curColumn].getField()];
    }
    else {
      // new row
      this.editData = null;
      this.curRow = null;
      this.origValue = "";
    }
    this.curTD = e.target;

    // prepare the editor
    this.curEditor = dojo.widget.byId(this.columns[this.curColumn].editor);
    this.curFP = dojo.lang.hitch(this, this.onKey);
    dojo.event.browser.addListener(this.curEditor.domNode, "onKey", this.curFP);

    // show the editor
    this.curEditor.setValue(this.origValue);
    this.showEditor(this.curTD);

    this.isEditing = true;

	if (this.curEditor instanceof dojo.widget.DropdownDatePicker) {
		dojo.event.connectOnce("after", this.curEditor, "onValueChanged", this, "onEditorFocusLost");
		// Didn't get this to work on IE. We can't determine if the user clicked the ddp icon after the onblur event
		// On mozilla we can check event.explicitOriginalTarget
		dojo.event.connectOnce("after", this.curEditor.inputNode, "onblur", this, "onEditorFocusLost");
	} else if (this.curEditor instanceof ingrid.widget.Select) {
		dojo.event.connectOnce("after", this.curEditor.textInputNode, "onblur", this, "onEditorFocusLost");
	} else if (this.curEditor instanceof ingrid.widget.ComboBox) {
		dojo.event.connectOnce("after", this.curEditor.textInputNode, "onblur", this, "onEditorFocusLost");
	} else if (this.curEditor instanceof dojo.widget.Textbox) {
		dojo.event.connectOnce("after", this.curEditor.textbox, "onblur", this, "onEditorFocusLost");
	}
  },

  // usually called by the onBlur event of the cell editor ( see beginEdit() )
  onEditorFocusLost: function(e) {
	// A click on the date picker icon should not close the editor
	if (this.curEditor instanceof dojo.widget.DropdownDatePicker) {
		if (e.explicitOriginalTarget) {
			// Mozilla specific
			var target = e.explicitOriginalTarget;
			var ddp = this.curEditor.domNode;
			if (ddp.isSameNode(target.parentNode) && target instanceof HTMLImageElement) {
				return;
			}
		} else if (dojo.render.html.ie){
			// IE specific
			if (document.activeElement == this.curEditor.domNode.parentNode)
				return;
		}
	// if the select box is open during onBlur, this is no EditorLostFocus event
	// the onBlur event was probabely generated while opening the select box
	} else if (this.curEditor instanceof ingrid.widget.Select || this.curEditor instanceof ingrid.widget.ComboBox) {
		if (this.curEditor.popupWidget.isShowingNow) {
			return;
		}
	}

	this._disconnectEditorEvents();
	this.save();
  },

  _disconnectEditorEvents: function() {
	if (this.curEditor) {
		if (this.curEditor instanceof dojo.widget.DropdownDatePicker) {
			dojo.event.disconnect("after", this.curEditor, "onValueChanged", this, "onEditorFocusLost");
			dojo.event.disconnect("after", this.curEditor.inputNode, "onblur", this, "onEditorFocusLost");
		} else if (this.curEditor instanceof ingrid.widget.Select) {
			dojo.event.disconnect("after", this.curEditor, "setValue", this, "onEditorFocusLost");
			dojo.event.disconnect("after", this.curEditor.textInputNode, "onblur", this, "onEditorFocusLost");
		} else if (this.curEditor instanceof ingrid.widget.ComboBox) {
			dojo.event.disconnect("after", this.curEditor.textInputNode, "onblur", this, "onEditorFocusLost");
		} else if (this.curEditor instanceof dojo.widget.Textbox) {
			dojo.event.disconnect("after", this.curEditor.textbox, "onblur", this, "onEditorFocusLost");
		}
	}
  },

  showEditor: function(/* HTMLElement */node) {
    var editorDiv = dojo.byId(this.widgetId+"Editors");
    if (!editorDiv)
      return;

    // replace the table cell with the editor node
    dojo.dom.replaceChildren(node, this.curEditor.domNode);
    this.curEditor.show();
  },

  hideEditor: function() {
    var editorDiv = dojo.byId(this.widgetId+"Editors");
    if (!editorDiv)
      return;

    dojo.html.setStyle(editorDiv, 'z-index', 0);

    // if not done already, append editor node to editor div again
    this.curEditor.hide();
    editorDiv.appendChild(this.curEditor.domNode);
  },

  focusEditor: function() {
    try {
      var inputNodes = this.curEditor.domNode.getElementsByTagName("input");
      if (inputNodes.length == 1) {
        inputNodes[0].focus();
      }
    }
    catch(e) {}
  },

  onKey: function(e) {
    //if (!e.key || e.ctrkKey || e.altKey) { return; }
    
    switch(e.key) {
      case e.KEY_ENTER:
      case e.KEY_TAB:
        this.save();
        dojo.event.browser.stopEvent(e);
        break;
      case e.KEY_ESCAPE:
        this.cancelEdit();
        dojo.event.browser.stopEvent(e);
        break;
    }
  },

  cancelEdit: function() {
    // close the editor
    var displayValue = this.origValue;

	if (this.curEditor.getDisplayValue && this.origValue != "") {
    	displayValue = this.curEditor.getDisplayValue();
	}

    this.endEdit(displayValue);
  },

  save: function() {
    if (this.curEditor) {
      // first remove the editor dom node from the table to avoid lost
      // after table update
      var editorDiv = dojo.byId(this.widgetId+"Editors");
      editorDiv.appendChild(this.curEditor.domNode);

      // set the value in the connected store and notify listeners
      var newValue = this.curEditor.getValue();

      if (typeof(this.columns[this.curColumn]) == "undefined")
		return;

      var field = this.columns[this.curColumn].getField();
      if (this.editData != null) {
        // update
        // only if the entry is not empty
        var rowEmpty = true;
        
		if (dojo.string.trim(newValue).length != 0) {
			// If the new value is not an empty string, the row is not empty
			rowEmpty = false;
		} else {
	        // Iterate over all properties of the current row
	        for (var prop in this.editData) {
	        	// If the property is the value field (Id) or the edited field (old value that is overwritten with newValue)
	        	// && If the column is an editable field of the table (exclude additional data)
	        	// && If the value is not an empty string
	        	//   then the current row is not empty
	        	if (prop != this.valueField && prop != field && 
	        			this.getColumnIndex(prop) != -1 &&
	        			this.editData[prop] != null && dojo.string.trim(this.editData[prop]).length != 0) {
	        		rowEmpty = false;
	        	}
	        }
		}

        if (rowEmpty) {
			// If the row is empty, remove it
        	this.store.removeData(this.editData);
        } else {
        	// Otherwise update the new value
        	this.store.update(this.editData, field, newValue);
        	this.onValueChanged(this.editData, field);
        }
      }
      else {
        // add
		// only if the added value is not an empty string
		if (dojo.string.trim(newValue).length != 0) {
			var newData = {};
	    	for(var i=0; i<this.columns.length; i++)
					newData[this.columns[i].getField()] = '';
	        newData[field] = newValue;
	        newData[this.valueField] = new Date().getTime();
	
	        this.store.addData(newData);
	        this.onValueAdded(newData);
	        this.select(newData);
	        this.editData = newData;
	    }
      }


      // if the editor implements the method getDisplayValue, use its value
    var displayValue = newValue;
	if (this.curEditor.getDisplayValue) {
    	displayValue = this.curEditor.getDisplayValue();
	}

      // close the editor
      this.endEdit(displayValue);
    }
  },

  /*
   * Hide the editor and reset the state
   */
  endEdit: function(result) {
    if (this.curEditor) {
      this.hideEditor();
      if (this.getRow(this.editData) != null)
      	this.fillCell(this.getRow(this.editData).cells[this.curColumn], this.columns[this.curColumn], result);

	  dojo.event.browser.removeListener(this.curEditor.domNode, "onKey", this.curFP);
    
      this._disconnectEditorEvents();
    }

    // reset state
    this.origValue = "";
    this.editData = null;
    this.curRow = null;
    this.curColumn = null;
    this.curEditor = null;
    this.curTD = null;
    this.isEditing = false;
    this.curFP = null;

	this.applyValidation();
    this.resetSelections();
    this.renderSelections();

    //this.debugData();
  },

  enable: function() {
  	dojo.html.addClass(this.domNode, "interactive");
    if(!dojo.html.hasClass(this.domNode, 'readonly'))
	    dojo.event.connectOnce(this.domNode.tBodies[0], "ondblclick", this, "onDblClick");
  	this.enableContextMenu();
  },

  disable: function() {
  	dojo.html.removeClass(this.domNode, "interactive");
	dojo.event.disconnect(this.domNode.tBodies[0], "ondblclick", this, "onDblClick");
  	this.disableContextMenu();
  },
	
	/* overwritten because of the display of "Invalid Date" for empty date cells */
	fillCell: function(/* HTMLTableCell */cell, /* object */meta, /* object */val){
		//	summary
		//	Fill the passed cell with value, based on the passed meta object.
		if(meta.sortType=="__markup__"){
			cell.innerHTML=val;
		} else {
			if(meta.getType()==Date) {
				var displayDate = new Date(val);
				if(val != null && !isNaN(displayDate)){
					var format = this.defaultDateFormat;
					if(meta.format){
						format = meta.format;
					}
					cell.innerHTML = dojo.date.strftime(displayDate, format);
				} else {
					// write empty string if we do not have a valid date
					// was cell.innerHTML = val;
					cell.innerHTML = "";
				}
			} else if ("Number number int Integer float Float".indexOf(meta.getType())>-1){
				//	TODO: number formatting
				if(val.length == 0){
					val="0";
				}
				var n = parseFloat(val, 10) + "";
				//	TODO: numeric formatting + rounding :)
				if(n.indexOf(".")>-1){
					n = dojo.math.round(parseFloat(val,10),2);
				}
				cell.innerHTML = n;
			}else{
				cell.innerHTML = val;
			}
		}
	},

	createRow: function(/* object */obj){
		//	summary
		//	Create an HTML row based on the passed object
		var row=document.createElement("tr");
//		dojo.html.disableSelection(row);
		if(obj.key != null){
			row.setAttribute("value", obj.key);
		}
		for(var j=0; j<this.columns.length; j++){
			var cell=document.createElement("td");
			cell.setAttribute("align", this.columns[j].align);
			cell.setAttribute("valign", this.columns[j].valign);
//			dojo.html.disableSelection(cell);
			var val = this.store.getField(obj.src, this.columns[j].getField());
			if(typeof(val)=="undefined"){
				val="";
			}
			this.fillCell(cell, this.columns[j], val);
			row.appendChild(cell);
		}
		return row;	//	HTMLTableRow
	},

	init: function(){
		//	summary
		//	initializes the table of data
		this.isInitialized=false;

		//	if there is no thead, create it now.
		var head=this.domNode.getElementsByTagName("thead")[0];
		if(head.getElementsByTagName("tr").length == 0){
			//	render the column code.
			var row=document.createElement("tr");
			for(var i=0; i<this.columns.length; i++){
				var cell=document.createElement("td");
				cell.setAttribute("align", this.columns[i].align);
				cell.setAttribute("valign", this.columns[i].valign);
//				dojo.html.disableSelection(cell);
				cell.innerHTML=this.columns[i].label;
				row.appendChild(cell);

				//	attach the events.
				if(!this.columns[i].noSort){
					dojo.event.connect(cell, "onclick", this, "onSort");
				}
			}
			dojo.html.prependChild(row, head);
		}
		
		if(this.store.get().length == 0){
			return false;
		}

		var idx=this.domNode.tBodies[0].rows.length;
		if(!idx || idx==0 || this.domNode.tBodies[0].rows[0].getAttribute("emptyRow")=="true"){
			idx = 0;
			var body = this.domNode.tBodies[0];
			while(body.childNodes.length>0){
				body.removeChild(body.childNodes[0]);
			}

			var data = this.store.get();
			for(var i=0; i<data.length; i++){
				var row = this.createRow(data[i]);
				body.appendChild(row);
				idx++;
			}
		}

		//	add empty rows
		if(this.minRows > 0 && idx < this.minRows){
			idx = this.minRows - idx;
			for(var i=0; i<idx; i++){
				row=document.createElement("tr");
				row.setAttribute("emptyRow","true");
				for(var j=0; j<this.columns.length; j++){
					cell=document.createElement("td");
					cell.innerHTML="&nbsp;";
					row.appendChild(cell);
				}
				body.appendChild(row);
			}
		}

		//	last but not least, show any columns that have sorting already on them.
		var row=this.domNode.getElementsByTagName("thead")[0].rows[0];
		var cellTag="td";
		if(row.getElementsByTagName(cellTag).length==0) cellTag="th";
		var headers=row.getElementsByTagName(cellTag);
		for(var i=0; i<headers.length; i++){
			dojo.html.setClass(headers[i], this.headerClass);
		}
		for(var i=0; i<this.sortInformation.length; i++){
			var idx=this.sortInformation[i].index;
			var dir=(~this.sortInformation[i].direction)&1;
			dojo.html.setClass(headers[idx], dir==0?this.headerDownClass:this.headerUpClass);
		}

		this.isInitialized=true;
		return this.isInitialized;
	},


  debugData: function() {
    dojo.debug('--------------------------------------------------');
    var str = '';
		this.store.forEach(function(element) {
      str = '|';
      for(var prop in element.src) {
        str += element.src[prop]+'|';
      }
      dojo.debug(str);
		});
    dojo.debug('--------------------------------------------------');
  }
});
