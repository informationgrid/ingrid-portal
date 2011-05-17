function createCustomTree(id, node, identifier, label, expandLoadFunction){
            
    var data = {
        identifier: identifier,
        label: label,
        items: []
    };
    
    var store = new dojo.data.ItemFileWriteStore({
        data: data
    });
    
    var treeModel = new ingrid.dijit.tree.LazyTreeStoreModel({
        store: store,
        rootId: "root_"+id,
        deferItemLoadingUntilExpand: true,
        rootLabel: "Data",
        labelAttr: "title",
        childrenAttrs: ["children"]
    });
    
	var myTree;
	if (node == null) {
		myTree = new ingrid.dijit.CustomTree({
			id: id,
			model: treeModel,
			persist: false,
			showRoot: false,
			openOnClick: false,
            onLoad: function() {this.finishedCreate = true; },
			lazyLoadItems: expandLoadFunction
		}, id);
	} else {
		myTree = new ingrid.dijit.CustomTree({
			id: id,
			model: treeModel,
			persist: false,
			showRoot: false,
			openOnClick: false,
            onLoad: function() {this.finishedCreate = true; },
			lazyLoadItems: expandLoadFunction
		}).placeAt(node);
	}
	
	myTree.startup();
    
}

/*
 * id: the id of a DOM element which shall be replaced by the grid
 * node: the id of a DOM element where to attach this grid
 * structure: [{
                    field: 'name',
                    name: 'Gruppe',
                    width: '100%'
                },{...},...]
 * initDataCallback: the function to call for table initialisation
 */

gridManager = {};
uniqueGridId = 0;

dojo.require("ingrid.dijit.CustomGrid");

function createDataGrid(id, node, structure, initDataCallback, gridProperties, url) {
    var deferred;
    
	if (!gridProperties)
	   gridProperties = UtilDOM.getHTMLAttributes(id);
       
    if (url) {
        deferred = new dojo.Deferred();
        dojo.xhrGet({
            url: url,
            handleAs: "json",
            load: function(data){
                deferred.callback(data.items);
            }
        });
    }
	
	// if a function is given to fill grid with initial data then use it
	// otherwise use an empty entry
	var storeProps = {};
    
        /* ========JQuery Test ================= */
        if (!url) {
            if (initDataCallback == null) {
                deferred = getEmptyData();
            }
            else {
                deferred = initDataCallback();
            }
        }
        deferred.addCallback(function(data) {
            var struct = [];
            var sortable = false;
            dojo.forEach(structure, function(item) {
                var col = dojo.clone(item);//{};
                col.editor = item.editor ? item.editor : mapGridCellType(item.type);
                col.id = item.field;
                //col.name = item.name;
                //col.field = item.field;
                //col.formatter = item.formatter;
                //col.options = item.options;
                //col.values = item.values;
                //col.sortable = item.sortable; // needs more stuff to do (see example13!)
                if (col.sortable == true)
                    sortable = true;
                
                col.cannotTriggerInsert = col.unselectable = item.editable ? false : true;
                
                //if (item.listId)
                //    col.listId = item.listId;
                
                if (item.width == "auto")
                    col.width = 100;
                else
                    col.width = parseInt(item.width,10);
                    
                struct.push(col);
            });

            
                var options = {headerHeight:18, rowHeight: 22, editable: false,
                    enableCellNavigation: true,asyncEditorLoading: false,
                    autoEdit: false, forceFitColumns: false, autoHeight:true};
                
                if (gridProperties.autoHeight)
                    options.visibleRowsInViewport = gridProperties.autoHeight;
                if (gridProperties.interactive) {
                    options.enableAddRow = gridProperties.interactive == "true";
                    options.editable = true;
                }
                if (gridProperties.forceGridHeight) {
                	options.forceGridHeight = gridProperties.forceGridHeight == "true";
                }
                if (gridProperties.moveRows) {
                    options.enableMoveRows = gridProperties.moveRows == "true";
                }
                
                //if (sortable)
                //    prepareGridForSort();
     
                var myDataGrid = new ingrid.dijit.CustomGrid({id: id, data: data, columns: struct, customOptions: options}, id);
                
                if (gridProperties.contextMenu == undefined)
                	createContextMenu(myDataGrid, id);
                else if (gridProperties.contextMenu != "none")
                	createContextMenu(myDataGrid, id, gridProperties.contextMenu);
                
                gridManager[id] = myDataGrid;
            });
        
        return deferred;
}

function mapGridCellType(type) {
    if (type == undefined)
        return TextCellEditor;
        
    if (type.prototype.declaredClass == "dojox.grid.cells.ComboBox")
        return ComboboxEditor;
    else if (type.prototype.declaredClass == "dojox.grid.cells.Select")
        return SelectboxEditor;
    else if (type.prototype.declaredClass == "ingrid.dijit.GridDateTextBox")
        return DateCellEditor;
    else if (type.prototype.declaredClass == "dojox.grid.cells._Widget")
        return IntegerCellEditor;
    else
        return type;
}

function createDataGridWidget(id, structure, gridProperties){
	var storeProps = {};
	storeProps.data = {};
    storeProps.data.items = [];
    storeProps.clearOnClose = true;
	
	gridProperties.id = id;
	gridProperties.store = new dojo.data.ItemFileWriteStore(storeProps);
	gridProperties.structure = structure;
	gridProperties.escapeHTMLInData = false;
	gridProperties.rowHeight = 22;
	gridProperties.autoRender = true;
	gridProperties.selectable = true;
	// if a query is present then convert the string to an object
	if (gridProperties.query && typeof(gridProperties.query) != "object") {
		gridProperties.query = eval("(" + gridProperties.query + ")");
		//gridProperties.query = {relationType:'3520'};
	}
	
	// create object for emptyItem representation
	gridProperties.emptyItem = {};
	dojo.forEach(structure, function(item){
		gridProperties.emptyItem[item.field] = "";
	});
	
	return new ingrid.dijit.CustomDataGrid(gridProperties);
}

/*
 * 
 */
function createSelectBox(id, node, storeProps, initDataCallback, url) {
	var def = getFileStoreInit("combo", storeProps, initDataCallback, url);
	
	var selectProperties = UtilDOM.getHTMLAttributes(id);
	def.then(function(store) {
		
		// add store to params
        selectProperties.id = id;
		selectProperties.store = store;
        selectProperties.intermediateChanges = true;
		//selectProperties.styles = "width:100%;";
		
		var mySelect;
		if (node == null) {
			mySelect = new dijit.form.Select(selectProperties, id);
		} else {
			mySelect = new dijit.form.Select(selectProperties).placeAt(node);
		}
    	
		mySelect.startup();
	});
	return def;
}

/*
 * 
 */
function createFilteringSelect(id, node, storeProps, initDataCallback, url) {
	var def = getFileStoreInit("combo", storeProps, initDataCallback, url);
	
	var selectProperties = UtilDOM.getHTMLAttributes(id);
	def.then(function(store) {
		
		// add store to params
		selectProperties.store = store;
		selectProperties.searchAttr = '0';
		selectProperties.required = false;
        selectProperties.intermediateChanges = true;
		
		var mySelect;
		if (node == null) {
			mySelect = new dijit.form.FilteringSelect(selectProperties, id);
		} else {
			mySelect = new dijit.form.FilteringSelect(selectProperties).placeAt(node);
		}
    	
		mySelect.startup();
	});
	return def;
}
 

/*
 * 
 */
function createComboBox(id, node, storeProps, initDataCallback, url) {
	var def = getFileStoreInit("combo", storeProps, initDataCallback, url);
	
	var comboProperties = UtilDOM.getHTMLAttributes(id);
	
	def.then(function(store) {
		
		// add store to params
		comboProperties.store = store;
		comboProperties.searchAttr = '0';
				
		var mySelect;
		if (node == null) {
			mySelect = new dijit.form.ComboBox(comboProperties, id);
		} else {
			mySelect = new dijit.form.ComboBox(comboProperties).placeAt(node);
		}
    	
		mySelect.startup();
	});
	return def;
}

function getFileStoreInit(storeType, storeProps, initDataCallback, url) {
	// use two deferreds here, otherwise the callback of the
	// sub function messes up the order
	var deferred;
	var deferred2 = new dojo.Deferred();
	
	if (url) {
		deferred = new dojo.Deferred();
		deferred.callback();
	}
	else {
	
		// if a function is given to fill grid with initial data then use it
		// otherwise use an empty entry
		if (initDataCallback == null) {
			deferred = getEmptyData();
		}
		else {
			deferred = initDataCallback();
		}
	}
		
	deferred.then(function(data) {
        // make sure not to have a reference of the properties!!!
        // otherwise it might be overwritten
        var newProps = dojo.clone(storeProps);
        
        
		if (url) 
			newProps.url = url;

		// add the initial data to the store properties
		if (!newProps.data) 
			newProps.data = {};
		newProps.data.items = data;

		newProps.clearOnClose = true;
		var store;
    	//if (storeType == "dataGrid") {
			//store = new ingrid.data.CustomItemFileWriteStore(storeProps);
			store = new dojo.data.ItemFileWriteStore(newProps);
		//}
		//else {
			//store = new dojo.data.ItemFileWriteStore(storeProps);
		//	store = new dojo.data.ItemFileReadStore(storeProps);
		//}
		
		deferred2.callback(store);
	});
	
	return deferred2;
}

function getEmptyData() {
	var def = new dojo.Deferred();
	def.callback([]);
	return def;
}


// ----------------------------------
// DOM Creation
// ----------------------------------
var additionalFieldPrefix = "";

function addElementToObjectForm(e){
    dojo.byId("contentFrameBodyObject").appendChild(e);
}

function addToSection(section, domElement){
    dojo.byId(section).appendChild(domElement);
}

function addEmptySpan(section){
    var emptySpan = document.createElement("span");
    emptySpan.innerHTML = "&nbsp;";
    emptySpan.setAttribute("style", "clear: both; display: block;");
    dojo.byId(section).appendChild(emptySpan);
}

function createDivElement(clazz, style){
    var div = document.createElement("div");
    dojo.addClass(div, clazz);
    if (style) 
        div.setAttribute("style", style);
    return div;
}

function createRubricIcon(rubric) {
    var link = document.createElement("a");
    link.setAttribute("title", "ui.general.openAllFieldsTooltip");
    link.setAttribute("href", "javascript:igeEvents.toggleFields('"+rubric.id+"')");
    var image = document.createElement("div");
    dojo.addClass(image, "image18px titleIcon");    
    link.appendChild(image);
    return link;
}

function addContextHelp(divElement, help) {
    // TODO: change function showContextHelp to be able to set help text from here
    divElement.setAttribute("onclick", "javascript:dialog.showContextHelp(arguments[0], \""+help+"\")");
}

function createRubricUp() {
    var link = document.createElement("a");
    link.setAttribute("title", "nach oben");
    link.setAttribute("href", "#sectionBottomContent");
    var image = document.createElement("img");
    image.setAttribute("width", "9");
    image.setAttribute("height", "6");
    image.setAttribute("alt", "^");
    image.setAttribute("src", "img/ic_up_blue.gif");
    
    link.appendChild(image);
    return link;    
}

function createRubric(rubric) {
    // add a fill div before a rubric (IE7!)
    addElementToObjectForm(dojo.create("div", {'class':"fill"}));
	var rubricDiv = document.createElement("div");
    dojo.addClass(rubricDiv, "rubric additional content");
	rubricDiv.setAttribute("id", rubric.id);
    var outerDiv = createDivElement("titleBar");
    var labelDiv = createDivElement("titleCaption");
    var upDiv = createDivElement("titleUp");
    
    //prepare icon
    outerDiv.appendChild(createRubricIcon(rubric));
    
    // prepare label
    addContextHelp(labelDiv, rubric.help);
    labelDiv.innerHTML = rubric.label;
    
    // prepare up link
    upDiv.appendChild(createRubricUp());
    
    outerDiv.appendChild(labelDiv);
    outerDiv.appendChild(upDiv);
    rubricDiv.appendChild(outerDiv);
	
    return rubricDiv;
}

function createDomTextbox(additionalField) {
    var inputWidget = new dijit.form.ValidationTextBox({
        id: additionalFieldPrefix + additionalField.id,
        maxLength: additionalField.size,
        name: additionalField.name,
        style: "width:100%;"
    });
    return addSurroundingContainer(inputWidget.domNode, additionalField);
}

function createDomNumberbox(additionalField) {
    var inputWidget = new dijit.form.NumberTextBox({
        id: additionalFieldPrefix + additionalField.id,
        maxLength: additionalField.size,
        name: additionalField.name,
        style: "width:100%;"
    });
    return addSurroundingContainer(inputWidget.domNode, additionalField, dojo.trim(additionalField.unit)=="" ? "" : "Numberbox");
}

function createDomDatebox(additionalField) {
    var inputWidget = new dijit.form.DateTextBox({
        id: additionalFieldPrefix + additionalField.id,
        name: additionalField.name,
        style: "width:100%;"
    });
    return addSurroundingContainer(inputWidget.domNode, additionalField);
}

function createDomTextarea(additionalField) {
    // if a more specific height was given
    if (additionalField.height)
       var height = "height:"+additionalField.height+"px;";
       
    var inputWidget = new dijit.form.SimpleTextarea({
        id: additionalFieldPrefix + additionalField.id,
        name: additionalField.name,
        style: "width:100%;"+height,
        rows: additionalField.rows
    });
    return addSurroundingContainer(inputWidget.domNode, additionalField);
}

function createDomSelectBox(additionalField) {
    // Set the correct select values via the contained data provider
    /*var data = [];
    if (additionalField.listEntries) {
        for (var entryIndex = 0; entryIndex < additionalField.listEntries.length; ++entryIndex) {
            var currentEntry = additionalField.listEntries[entryIndex];
            data.push([currentEntry.id, currentEntry.value]);
        }
    }*/
    
    var storeProps = {
        data: {identifier: 'id',label: 'value'}
    };
    storeProps.data.items = additionalField.listEntries;//data;
    var store = new dojo.data.ItemFileReadStore(storeProps);
    
    var elementProperties = {
        id: additionalFieldPrefix + additionalField.id,
        name: additionalField.name,
        searchAttr: 'value',
        store: store,
        style: "width:100%;",
        autoComplete: "false",
        required: false
        //sortByLabel: false
    };
    
    if (additionalField.isExtendable == true)
        var inputWidget = new dijit.form.ComboBox(elementProperties);
    else
        var inputWidget = new dijit.form.FilteringSelect(elementProperties);
        
    inputWidget.startup();
        
    return addSurroundingContainer(inputWidget.domNode, additionalField);
}

function createDomDataGrid(additionalField, structure, section) {
    //var gridWidget = createDataGridWidget(additionalField.id, structure, {interactive:"true", autoHeight:4});
    var div = document.createElement("div");
    div.setAttribute("id", additionalField.id)
    var surrDiv = addSurroundingContainer(div, additionalField, true);
    addToSection(section, surrDiv);
    var gridWidget = createDataGrid(additionalField.id, null, structure, null, {interactive:"true", autoHeight:additionalField.rows});
}

function addSurroundingContainer(/*DomNode*/nodeToInsert, additionalField, type) {
    // Create the following dom structure:
    // <span id="uiElementAdd${additionalField.id}" class="outer" type="optional">
    //   <div>
    //     <span class="label">
    //       <label class="inActive">
    //         ${additionalField.name}
    //       </label>
    //     </span>
    //     <span class="input">
    //       < ingrid:ValidationTextbox or ingrid:Select (or ...) depending on ${additionalField.type} /> 
    //     </span>
    //   </div>
    // </span>
    
    // Create dom nodes
    var uiElementSpan = document.createElement("span");
    dojo.addClass(uiElementSpan, "outer additional");
    uiElementSpan.id = "uiElementAdd" + additionalField.id;
   
    // mark field as additional for easier saving data
    dojo.addClass(nodeToInsert, "additionalField");
   
    if (additionalField.isMandatory) 
        dojo.addClass(uiElementSpan, "required");
    else {
        dojo.addClass(uiElementSpan, additionalField.visible);
    }
    
    uiElementSpan.setAttribute("style", additionalField.style);
    var uiPaddingDiv = document.createElement("div");
    var labelSpanElement = document.createElement("span");
    dojo.addClass(labelSpanElement, "label");
    var labelElement = document.createElement("label");
    //dojo.addClass(labelElement, "inActive");
    labelElement.setAttribute("onclick", "javascript:dialog.showContextHelp(arguments[0], \""+additionalField.help+"\")");
    labelElement.innerHTML = additionalField.name;
    
    /*if (isGrid) {
        var inputSpanElement = document.createElement("div");
        dojo.addClass(inputSpanElement, "rows4");
    }
    else {*/
        var inputSpanElement = document.createElement("span");
        dojo.addClass(inputSpanElement, "input");
    //}
    
    // Build the complete structure
    labelSpanElement.appendChild(labelElement);
    uiElementSpan.appendChild(uiPaddingDiv);
    uiPaddingDiv.appendChild(labelSpanElement);
    if (type == "Numberbox") {
        var tableContainer = dojo.create("table", {
            style: {width:"100%"},
            innerHTML: "<tr><td></td><td style='width:1px; padding-left:5px;'>" + additionalField.unit + "</td></tr>"
        }, inputSpanElement);
        dojo.place(nodeToInsert, dojo.query("td", tableContainer)[0]);
        // remember unit also in numberbox for detail/print view
        nodeToInsert.setAttribute("unit", additionalField.unit);
    } else {
        inputSpanElement.appendChild(nodeToInsert);
    }
    uiPaddingDiv.appendChild(inputSpanElement);
    return uiElementSpan;
}
    