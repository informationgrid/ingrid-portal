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

  /*
   * Add default context menus
   */ 
  initialize: function() {
    // parse properties from string if necessary
    this.multiple = this.multiple == "true" || this.multiple == true;
    this.customContextMenu = this.customContextMenu == "true" || this.customContextMenu == true;
	
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

  /*
   * Overidden to connect double click event (opens cell editor) and add 
   */
  postCreate: function() {
    // add editor to metadata
    this._meta['editor'] = "";

    if(dojo.html.hasClass(this.domNode, 'interactive') && !dojo.html.hasClass(this.domNode, 'readonly')) {
      // connect the couble click event for editing
      dojo.event.connect(this.domNode.tBodies[0], "ondblclick", this, "onDblClick");

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
    dojo.widget.FilteringTable.prototype.postCreate.apply(this, arguments);
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
	    var rows = body.rows;
	    while (rows.length < this.minRows) {
	      this.addEmptyRow();
	      rows = body.rows;
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
/*
		var rows = this.domNode.tBodies[0].rows;
		for(var i=0; i<rows.length; i++){
			for (var j=0; j<this.columns.length; j++) {
				if (this.columns[j].editor) {
					var currentEditor = this.columns[j].editor;
					var rowData = this.getDataByRow(rows[i]);
					if (rowData) {
						var fieldContent = rowData[this.columns[j].getField()];
						currentEditor.setValue(fieldContent);
						if (currentEditor.getDisplayValue) {
					       	var displayValue = currentEditor.getDisplayValue();
      						this.fillCell(rows[i].cells[j], this.columns[j], displayValue);
					    }
					}
				}
			}
		}
*/
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

  hasData: function() {
    return this.store.getData().length > 0;
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

  beginEdit: function(e) {
    // return if an already edited cell is double clicked
    if (this.isEditing && e.target == this.curTD)
      return;

    // close current editor
    if (this.curEditor)
      this.cancelEdit();

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
    dojo.event.browser.addListener(this.curEditor.domNode, "onKey", dojo.lang.hitch(this, this.onKey));

    // show the editor
    this.curEditor.setValue(this.origValue);
    this.showEditor(this.curTD);

    this.isEditing = true;
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
      var field = this.columns[this.curColumn].getField();
      if (this.editData != null) {
        // update
        this.store.update(this.editData, field, newValue);
        this.onValueChanged(this.editData, field);
      }
      else {
        // add
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

      // if the editor implements the method getDisplayValue, use its value
      var displayValue = newValue;
      if (this.curEditor.getDisplayValue)
        displayValue = this.curEditor.getDisplayValue();

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
      this.fillCell(this.getRow(this.editData).cells[this.curColumn], this.columns[this.curColumn], result);
    }

    // reset state
    this.origValue = "";
    this.editData = null;
    this.curRow = null;
    this.curColumn = null;
    this.curEditor = null;
    this.curTD = null;
    this.isEditing = false;

    this.resetSelections();
    this.renderSelections();

    //this.debugData();
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
