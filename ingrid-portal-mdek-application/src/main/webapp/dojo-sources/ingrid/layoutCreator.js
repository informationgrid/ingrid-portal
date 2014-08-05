define([
    "dojo/_base/declare",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/request",
    "dojo/has",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/Deferred",
    "dojo/when",
    "dojo/on",
    "dojo/query",
    "ingrid/grid/CustomGrid",
    "ingrid/menu",
    "dijit/registry",
    "dijit/form/ValidationTextBox",
    "dijit/form/ComboBox",
    "dijit/form/Select",
    "dijit/form/DateTextBox",
    "dijit/form/NumberSpinner",
    "dijit/form/SimpleTextarea",
    "dijit/form/FilteringSelect",
    "dojo/data/ItemFileWriteStore",
    "dojo/data/ItemFileReadStore",
    "ingrid/utils/Dom",
    "dojo/store/Memory"
], function(declare, dom, domClass, construct, request, has, array, lang, Deferred, when, on, query, Grid, menu, registry,
        ValidationTextBox, ComboBox, Selectbox, DateTextBox, NumberSpinner, SimpleTextarea, FilteringSelect,
        ItemFileWriteStore, ItemFileReadStore, UtilDOM, Memory){

    gridManager = {};
        
    return declare(null, {

            uniqueGridId: 0,

            //dojo.require("ingrid.dijit.CustomGrid");
            createDataGrid: function(id, node, structure, initDataCallback, gridProperties, url) {
                var deferred, gridDeferred = new Deferred();
                var self = this;

                if (!gridProperties)
                    gridProperties = UtilDOM.getHTMLAttributes(id);

                if (url) {
                    deferred = new Deferred();
                    request.get(url, {
                        handleAs: "json"
                    }).then(function(data) {
                        deferred.resolve(data.items);
                    }, function(e) {
                        console.error("Error while fetching url", e);
                    });
                }

                // if initial data does NOT come from an URL
                if (!url) {
                    if (initDataCallback == null) {
                        deferred = [];
                    } else {
                        deferred = initDataCallback();
                    }
                }

                var gridData = deferred; // can be a promise or the actual data! 
                when(gridData, function(data) {
                    var struct = [];
                    var sortable = false;
                    array.forEach(structure, function(item) {
                        var col = lang.clone(item); //{};
                        col.editor = item.editor ? item.editor : self.mapGridCellType(item.type);
                        col.id = item.field;
                        //col.name = item.name;
                        //col.field = item.field;
                        //col.formatter = item.formatter;
                        //col.options = item.options;
                        //col.values = item.values;
                        //col.sortable = item.sortable; // needs more stuff to do (see example13!)
                        if (col.sortable === true)
                            sortable = true;

                        col.cannotTriggerInsert = col.unselectable = item.editable ? false : true;

                        //if (item.listId)
                        //    col.listId = item.listId;

                        if (item.width == "auto")
                            col.width = 100;
                        else
                            col.width = parseInt(item.width, 10);

                        struct.push(col);
                    });


                    var options = {
                        headerHeight: 18,
                        rowHeight: 22,
                        editable: false,
                        enableCellNavigation: true,
                        asyncEditorLoading: false,
                        autoEdit: false,
                        forceFitColumns: false,
                        autoHeight: true
                    };

                    if (gridProperties.autoedit == "true") options.autoEdit = true;

                    if (gridProperties.autoHeight)
                        options.visibleRowsInViewport = gridProperties.autoHeight;
                    if (gridProperties.interactive) {
                        options.enableAddRow = gridProperties.interactive == "true";
                        options.editable = true;
                    }
                    if (gridProperties.forceGridHeight) {
                        options.forceGridHeight = gridProperties.forceGridHeight == "true";
                    }
                    if (gridProperties.defaultHideScrollbar) {
                        options.defaultHideScrollbar = gridProperties.defaultHideScrollbar == "true";
                    }
                    if (gridProperties.moveRows) {
                        options.enableMoveRows = gridProperties.moveRows == "true";
                    }
                    if (gridProperties.allowEmptyRows) {
                        options.allowEmptyRows = gridProperties.allowEmptyRows == "true";
                    }

                    var myDataGrid = new Grid({
                        id: id,
                        data: data,
                        columns: struct,
                        customOptions: options
                    }, id);

                    menu.createContextMenu(myDataGrid, id, gridProperties);
                    gridManager[id] = myDataGrid;
                    gridDeferred.resolve();

                }, function(error) {
                    console.error("Error during grid creation:", error);
                });

                return gridDeferred.promise;
            },

            mapGridCellType: function(type) {
                var result = type;
                require(["ingrid/grid/CustomGridEditors"], function(editors) {
                    if (type == undefined)
                        result = editors.TextCellEditor;
                    else if (type.prototype.declaredClass == "dojox.grid.cells.ComboBox")
                        result = editors.ComboboxEditor;
                    else if (type.prototype.declaredClass == "dojox.grid.cells.Select")
                        result = editors.SelectboxEditor;
                    else if (type.prototype.declaredClass == "ingrid.dijit.GridDateTextBox")
                        result = editors.DateCellEditor;
                    else if (type.prototype.declaredClass == "dojox.grid.cells._Widget")
                        result = editors.DecimalCellEditor;
                });
                return result;
            },

            createDataGridWidget: function(id, structure, gridProperties) {
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
                array.forEach(structure, function(item) {
                    gridProperties.emptyItem[item.field] = "";
                });

                return new ingrid.dijit.CustomDataGrid(gridProperties);
            },

            /*
             *
             */
            createSelectBox: function(id, node, storeProps, initDataCallback, url) {
                var selectProperties = UtilDOM.getHTMLAttributes(id);
                var def = this.getFileStoreInit("combo", storeProps, initDataCallback, url)
                .then(function(store) {
                    // add store to params
                    selectProperties.id = id;
                    selectProperties.store = store;
                    // selectProperties.intermediateChanges = true;
                    selectProperties.labelAttr = storeProps.data.label;

                    var mySelect;
                    if (node === null) {
                        mySelect = new Selectbox(selectProperties, id);
                    } else {
                        mySelect = new Selectbox(selectProperties).placeAt(node);
                    }

                    mySelect.startup();
                });
                return def;
            },

            /*
             *
             */
            createFilteringSelect: function(id, node, storeProps, initDataCallback, url) {
                var selectProperties = UtilDOM.getHTMLAttributes(id);
                var def2 = new Deferred();
                this.getFileStoreInit("combo", storeProps, initDataCallback, url)
                .then(function(store) {

                    // add store to params
                    selectProperties.store = store;
                    selectProperties.searchAttr = storeProps.data.label;
                    selectProperties.required = selectProperties.required === "true" ? true : false;
                    // selectProperties.intermediateChanges = true;

                    var mySelect;
                    if (node === null) {
                        mySelect = new FilteringSelect(selectProperties, id);
                    } else {
                        mySelect = new FilteringSelect(selectProperties).placeAt(node);
                    }

                    mySelect.startup();
                    def2.resolve();
                });
                return def2;
            },


            /*
             *
             */
            createComboBox: function(id, node, storeProps, initDataCallback, url) {
                var comboProperties = UtilDOM.getHTMLAttributes(id);

                var def = this.getFileStoreInit("combo", storeProps, initDataCallback, url)
                .then(function(store) {

                    // add store to params
                    comboProperties.store = store;
                    comboProperties.searchAttr = '0';

                    var mySelect;
                    if (node === null) {
                        mySelect = new ComboBox(comboProperties, id);
                    } else {
                        mySelect = new ComboBox(comboProperties).placeAt(node);
                    }

                    mySelect.startup();
                });
                return def;
            },

            getFileStoreInit: function(storeType, storeProps, initDataCallback, url) {
                // use two deferreds here, otherwise the callback of the
                // sub function messes up the order
                var deferred;
                var deferred2 = new Deferred();

                if (url) {
                    deferred = new Deferred();
                    request.get(url, {
                        handleAs: "json"
                    }).then(function(data){
                        deferred.resolve(data);
                    }, function(e){
                        // handle error
                        console.error(e);
                    });
                } else {

                    // if a function is given to fill grid with initial data then use it
                    // otherwise use an empty entry
                    if (initDataCallback === null) {
                        deferred = [];
                    } else {
                        deferred = initDataCallback();
                    }
                }

                when(deferred, function(data) {
                    // make sure not to have a reference of the properties!!!
                    // otherwise it might be overwritten
                    var newProps = lang.clone(storeProps);

                    if (url)
                        newProps.url = url;

                    // add the initial data to the store properties
                    if (!newProps.data)
                        newProps.data = {};
                    newProps.data = data;

                    newProps.idProperty = storeProps.data.identifier;

                    // newProps.clearOnClose = true;
                    var store = new Memory(newProps);

                    deferred2.resolve(store);
                });

                return deferred2;
            },

            // ----------------------------------
            // DOM Creation
            // ----------------------------------
            additionalFieldPrefix: "",

            addElementToObjectForm: function(e) {
                dom.byId("contentFrameBodyObject").appendChild(e);
            },

            addToSection: function(section, domElement) {
                dom.byId(section).appendChild(domElement);
            },

            addEmptySpan: function(section) {
                var emptySpan = document.createElement("span");
                emptySpan.innerHTML = "&nbsp;";
                emptySpan.setAttribute("style", "clear: both; display: block;");
                dom.byId(section).appendChild(emptySpan);
            },

            createDivElement: function(clazz, style) {
                var div = document.createElement("div");
                domClass.add(div, clazz);
                if (style)
                    div.setAttribute("style", style);
                return div;
            },

            createRubricIcon: function(rubric) {
                var link = document.createElement("a");
                link.setAttribute("title", "ui.general.openAllFieldsTooltip");
                link.setAttribute("onclick", "require('ingrid/IgeEvents').toggleFields('" + rubric.id + "')");
                var image = document.createElement("div");
                domClass.add(image, "image18px titleIcon");
                link.appendChild(image);
                return link;
            },

            addContextHelp: function(divElement, help) {
                // TODO: change function showContextHelp to be able to set help text from here
                divElement.setAttribute("onclick", "require('ingrid/dialog').showContextHelp(arguments[0], \"" + help + "\")");
            },

            createRubricUp: function() {
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
            },

            createRubric: function(rubric) {
                // add a fill div before a rubric (IE7!)
                this.addElementToObjectForm(construct.create("div", {
                    'class': "fill"
                }));
                var rubricDiv = document.createElement("div");
                domClass.add(rubricDiv, "rubric additional content");
                rubricDiv.setAttribute("id", rubric.id);
                var outerDiv = this.createDivElement("titleBar");
                var labelDiv = this.createDivElement("titleCaption");
                var upDiv = this.createDivElement("titleUp");

                //prepare icon
                outerDiv.appendChild(this.createRubricIcon(rubric));

                // prepare label
                this.addContextHelp(labelDiv, rubric.help);
                labelDiv.innerHTML = rubric.label;

                // prepare up link
                upDiv.appendChild(this.createRubricUp());

                outerDiv.appendChild(labelDiv);
                outerDiv.appendChild(upDiv);
                rubricDiv.appendChild(outerDiv);

                return rubricDiv;
            },

            createDomTextbox: function(additionalField) {
                var inputWidget = new ValidationTextBox({
                    id: this.additionalFieldPrefix + additionalField.id,
                    maxLength: additionalField.size,
                    name: additionalField.name,
                    style: "width:100%;"
                });
                return this.addSurroundingContainer(inputWidget.domNode, additionalField);
            },

            createDomNumberbox: function(additionalField) {
                var inputWidget = new NumberSpinner({
                    id: this.additionalFieldPrefix + additionalField.id,
                    maxLength: additionalField.size,
                    name: additionalField.name,
                    style: "width:100%;"
                });
                return this.addSurroundingContainer(inputWidget.domNode, additionalField, lang.trim(additionalField.unit) == "" ? "" : "Numberbox");
            },

            createDomDatebox: function(additionalField) {
                var inputWidget = new DateTextBox({
                    id: this.additionalFieldPrefix + additionalField.id,
                    name: additionalField.name,
                    style: "width:100%;"
                });
                return this.addSurroundingContainer(inputWidget.domNode, additionalField);
            },

            createDomTextarea: function(additionalField) {
                // if a more specific height was given
                if (additionalField.height)
                    var height = "height:" + additionalField.height + "px;";

                var inputWidget = new SimpleTextarea({
                    id: this.additionalFieldPrefix + additionalField.id,
                    name: additionalField.name,
                    style: "width:100%;" + height,
                    rows: additionalField.rows
                });
                return this.addSurroundingContainer(inputWidget.domNode, additionalField);
            },

            createDomSelectBox: function(additionalField) {
                // Set the correct select values via the contained data provider
                /*var data = [];
            if (additionalField.listEntries) {
                for (var entryIndex = 0; entryIndex < additionalField.listEntries.length; ++entryIndex) {
                    var currentEntry = additionalField.listEntries[entryIndex];
                    data.push([currentEntry.id, currentEntry.value]);
                }
            }*/

                var storeProps = {
                    data: {
                        identifier: 'id',
                        label: 'value'
                    }
                };
                storeProps.data.items = additionalField.listEntries; //data;
                var store = new ItemFileReadStore(storeProps);

                var elementProperties = {
                    id: this.additionalFieldPrefix + additionalField.id,
                    name: additionalField.name,
                    searchAttr: 'value',
                    store: store,
                    style: "width:100%;",
                    autoComplete: "false",
                    required: false
                    //sortByLabel: false
                };

                if (additionalField.isExtendable == true)
                    var inputWidget = new ComboBox(elementProperties);
                else
                    var inputWidget = new FilteringSelect(elementProperties);

                inputWidget.startup();

                return this.addSurroundingContainer(inputWidget.domNode, additionalField);
            },

            createDomDataGrid: function(additionalField, structure, section) {
                //var gridWidget = createDataGridWidget(additionalField.id, structure, {interactive:"true", autoHeight:4});
                var div = document.createElement("div");
                div.setAttribute("id", additionalField.id);
                var surrDiv = this.addSurroundingContainer(div, additionalField, true);
                this.addToSection(section, surrDiv);
                var gridWidget = this.createDataGrid(additionalField.id, null, structure, null, {
                    interactive: "true",
                    autoHeight: additionalField.rows
                });
            },

            createDomCheckbox: function(additionalField, section) {
                var inputWidget = new dijit.form.CheckBox({
                    id: this.additionalFieldPrefix + additionalField.id,
                    name: additionalField.name
                });

                // the checkbox's label is inline so we need to contruct a new surrounding
                return this.addCheckSurroundingContainer(inputWidget.domNode, additionalField);
            },

            createThesaurusGrid: function(additionalField, section) {
                //var gridWidget = createDataGridWidget(additionalField.id, structure, {interactive:"true", autoHeight:4});
                var div = document.createElement("div");
                div.setAttribute("id", additionalField.id)
                var surrDiv = this.addSurroundingContainer(div, additionalField, true, true);
                this.addToSection(section, surrDiv);

                // relative width
                var isHalfWidth = additionalField.style.indexOf("width:50%") != -1;
                var w = isHalfWidth ? 0.49 : 1;

                var thesaurusTermsStructure = [{
                    field: 'label',
                    name: 'label',
                    width: 450 * w + 'px'
                }, {
                    field: 'topicId',
                    name: 'topicId',
                    width: (258 * w) + 'px'
                }];
                this.createDataGrid(additionalField.id, null, thesaurusTermsStructure, null);
                domClass.add(additionalField.id, "hideTableHeader");
                registry.byId(additionalField.id).hideColumn("topicId");
            },

            addSurroundingContainer: function( /*DomNode*/ nodeToInsert, additionalField, type, linkInfo) {
                // Create the following dom structure:
                // <span id="uiElementAdd${additionalField.id}" class="outer" type="optional">
                //   <div>
                //     <span class="label">
                //       <label class="inActive">
                //         ${additionalField.name}
                //       </label>
                //     </span>
                //     <span class="functional_link">
                //       <img></img>
                //       <a></a>
                //     </span>
                //     <span class="input">
                //       < ingrid:ValidationTextbox or ingrid:Select (or ...) depending on ${additionalField.type} /> 
                //     </span>
                //   </div>
                // </span>

                // Create dom nodes
                var uiElementSpan = document.createElement("span");
                domClass.add(uiElementSpan, "outer additional content");
                uiElementSpan.id = "uiElementAdd" + additionalField.id;

                // mark field as additional for easier saving data
                domClass.add(nodeToInsert, "additionalField");

                if (additionalField.isMandatory)
                    domClass.add(uiElementSpan, "required");
                else {
                    domClass.add(uiElementSpan, additionalField.visible);
                }

                uiElementSpan.setAttribute("style", additionalField.style);
                var uiPaddingDiv = document.createElement("div");
                var labelSpanElement = document.createElement("span");
                domClass.add(labelSpanElement, "label left");
                var labelElement = document.createElement("label");
                //domClass.add(labelElement, "inActive");
                labelElement.setAttribute("onclick", "require('ingrid/dialog').showContextHelp(arguments[0], \"" + additionalField.help + "\")");
                labelElement.innerHTML = additionalField.name;

                if (linkInfo) {
                    var linkSpanElement = construct.create("span", {
                        "class": "functionalLink",
                        innerHTML: "<a href='#' onclick=\"require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('thesaurusNavigator'), 'dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: '" + additionalField.id + "', rootUrl: '" + additionalField.rootUrl + "'});\">" + additionalField.linkLabel + "</a>"
                    });
                }

                /*if (isGrid) {
                var inputSpanElement = document.createElement("div");
                domClass.add(inputSpanElement, "rows4");
            }
            else {*/
                var inputSpanElement = document.createElement("span");
                domClass.add(inputSpanElement, "input clear");
                //}

                // Build the complete structure
                labelSpanElement.appendChild(labelElement);
                uiElementSpan.appendChild(uiPaddingDiv);
                uiPaddingDiv.appendChild(labelSpanElement);
                if (linkSpanElement) uiPaddingDiv.appendChild(linkSpanElement);
                if (type == "Numberbox") {
                    var tableContainer = construct.create("table", {
                        style: {
                            width: "100%"
                        },
                        innerHTML: "<tr><td></td><td style='width:1px; padding-left:5px;'>" + additionalField.unit + "</td></tr>"
                    });
                    inputSpanElement.appendChild(tableContainer);
                    construct.place(nodeToInsert, query("td", tableContainer)[0]);
                    // remember unit also in numberbox for detail/print view
                    nodeToInsert.setAttribute("unit", additionalField.unit);
                } else {
                    inputSpanElement.appendChild(nodeToInsert);
                }
                uiPaddingDiv.appendChild(inputSpanElement);
                return uiElementSpan;
            },

            addCheckSurroundingContainer: function( /*DomNode*/ nodeToInsert, additionalField, type, linkInfo) {
                // Create the following dom structure:
                // <span id="uiElementAdd${additionalField.id}" class="outer" type="optional">
                //   <div>
                //     < ingrid:Checkbox or ... depending on ${additionalField.type} /> 
                //     <label class="inActive">
                //       ${additionalField.name}
                //     </label>
                //   </div>
                // </span>

                // Create dom nodes
                var uiElementSpan = document.createElement("span");
                domClass.add(uiElementSpan, "outer additional content");
                uiElementSpan.id = "uiElementAdd" + additionalField.id;

                // mark field as additional for easier saving data
                domClass.add(nodeToInsert, "additionalField");

                if (additionalField.isMandatory)
                    domClass.add(uiElementSpan, "required");
                else {
                    domClass.add(uiElementSpan, additionalField.visible);
                }

                uiElementSpan.setAttribute("style", additionalField.style);
                var uiPaddingDiv = document.createElement("div");
                domClass.add(uiPaddingDiv, "input checkboxContainer");
                var labelElement = document.createElement("label");
                //domClass.add(labelElement, "inActive");
                labelElement.setAttribute("onclick", "require('ingrid/dialog').showContextHelp(arguments[0], \"" + additionalField.help + "\")");
                labelElement.innerHTML = additionalField.name;

                // Build the complete structure
                uiElementSpan.appendChild(uiPaddingDiv);
                uiPaddingDiv.appendChild(nodeToInsert);
                uiPaddingDiv.appendChild(labelElement);

                return uiElementSpan;
            }
    })();
});