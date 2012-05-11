dojo.provide("ingrid.dijit.CustomGrid");

dojo.require("dijit._Widget");
dojo.require("ingrid.dijit.EditorLock");
dojo.require("dojo.dnd.Moveable");
//dojo.require("dojox.html.entities");

// onselectstart="return false" on table/cell to prevent selection of text

var scrollbarDimensions = null; // shared across all grids on this page

dojo.declare("ingrid.dijit.CustomGrid", [dijit._Widget], {
	optionsDefault: {
		headerHeight: 25,
	    rowHeight: 25,
	    defaultColumnWidth: 80,
	    enableAddRow: false,
	    leaveSpaceForNewRows: false,
	    editable: false,
	    autoEdit: true,
	    enableCellNavigation: true,
	    enableCellRangeSelection: false,
	    enableColumnReorder: true,
	    asyncEditorLoading: false,
	    asyncEditorLoadDelay: 100,
	    forceFitColumns: false,
	    enableAsyncPostRender: false,
	    asyncPostRenderDelay: 60,
	    autoHeight: false,
	    //editorLock: new ingrid.dijit.EditorLock(),
	    showHeaderRow: false,
	    headerRowHeight: 25,
	    showTopPanel: false,
	    topPanelHeight: 25,
	    formatterFactory: null,
	    editorFactory: null,
	    cellFlashingCssClass: "flashing",
	    selectedCellCssClass: "selected",
	    multiSelect: true,
	    visibleRowsInViewport: 4,
	    enableMoveRows: false,
        forceGridHeight: true,
        defaultHideScrollbar: false
	},
    editorLock: new ingrid.dijit.EditorLock(),
	
	options: null,
    
    //uid: "slickgrid_" + Math.round(1000000 * Math.random()),
    headers: null,
    headerColumnWidthDiff: null, headerColumnHeightDiff: null, cellWidthDiff: null, cellHeightDiff : null,
    viewport: null,
    canvas: null,
    data: [],
    container: null,
    viewportHasHScroll: false,
    
    absoluteColumnMinWidth: null,
    
    //rowsCache: {},
    renderedRows: 0,
    numVisibleRows: null,
    prevScrollTop: 0,
    scrollTop: 0,
    lastRenderedScrollTop: 0,
    prevScrollLeft: 0,
    avgRowRenderTime: 10,
    
    selectionModel: null,
    selectedRows: [],

    plugins: [],
    cellCssClasses: {},

    columnsById: {},
    sortColumnId: null,
    sortAsc: true,

    // async call handles
    h_editorLoader: null,
    h_render: null,
    h_postrender: null,
    postProcessedRows: {},
    postProcessToRow: null,
    postProcessFromRow: null,

    // perf counters
    counter_rows_rendered: 0,
    counter_rows_removed: 0,
    
 // scroller
    maxSupportedCssHeight: null,      // browser's breaking point
    th: null,                         // virtual height
    h: null,                          // real scrollable height
    ph: null,                         // page height
    n: null,                          // number of pages
    cj: null,                         // "jumpiness" coefficient

    page: 0,                   // current page
    offset: 0,                 // current page offset
    scrollDir: 1,
    
    viewportH: null,
    viewportW: null,
    
    activePosX: null,
    activeRow: null, activeCell: null,
    activeCellNode: null,
    currentEditor: null,
    serializedEditorValue: null,
    editController: null,
    
    mouse_is_inside: false,
    
    columnDefaults: {
        name: "",
        resizable: true,
        sortable: false,
        minWidth: 30,
        rerenderOnResize: false,
        headerCssClass: null
    },
    
    columnRowMoveHandler: {
       id: "#",
       name: "",
       width: 40,
       behavior: "selectAndMove",
       selectable: false,
       resizable: false,
       cssClass: "cell-reorder dnd"
   },
    
    postMixInProperties:function(){
	    this.options = dojo.clone(this.optionsDefault);
        this.options.editorLock = this.editorLock; // use same editor lock for all grids
    	dojo.mixin(this.options, this.customOptions);
    	this.inherited(arguments);
    },
    
    constructor: function() {
    	this.rowsCache = {};
		this.columnsById = {};
		this.data = [];
		this.postProcessedRows = [];
		this.selectedRows = [];
		this.cellCssClasses = {};
		this.uid = "slickgrid_" + Math.round(1000000 * Math.random());
		this.activeRow = this.activeCell = this.activeCellNode = null;
        this.headers = new Object();
    },
    
    postCreate: function() {
		this.container = this.domNode;
		dojo.addClass(this.domNode, this.uid+' ui-widget');
		dojo.style(this.domNode, "overflow", "hidden");
		dojo.style(this.domNode, "outline", "0");
		//.attr("tabIndex",0)
        //.attr("hideFocus",true)
		
		this.maxSupportedCssHeight = this.getMaxSupportedCssHeight();
		
		this.columnDefaults.width = this.options.defaultColumnWidth;
		
		this.editController = {
            "commitCurrentEdit": dojo.hitch(this, this.commitCurrentEdit),
            "cancelCurrentEdit": dojo.hitch(this, this.cancelCurrentEdit),
            "fromTable": this.id
        };
		
		scrollbarDimensions = scrollbarDimensions || this.measureScrollbar(); // skip measurement if already have dimensions
		
        this.headerScroller = dojo.create("div", {'class': 'slick-header ui-state-default', style: {overflow:"hidden",position:"relative"}}, this.container);
        this.headers = dojo.create("div", {'class': 'slick-header-columns', style: {width:"10000px", left:"-1000px"}}, this.headerScroller);
        
        //this.viewport = dojo.create("div", {'class': 'slick-viewport', tabIndex:'0', hideFocus: "", style:{width:"100%", overflow:"auto", outline:"0",position:"relative"}}, this.container);
        if (this.options.forceGridHeight && !this.options.defaultHideScrollbar) {
            this.viewport = dojo.create("div", {'class': 'slick-viewport', tabIndex:'0', hideFocus: "", style:{width:"100%", overflowY:"scroll", overflowX:"auto", outline:"0",position:"relative"}}, this.container);
        } else {
            this.viewport = dojo.create("div", {'class': 'slick-viewport', tabIndex:'0', hideFocus: "", style:{width:"100%", overflowY:"auto", overflowX:"auto", outline:"0",position:"relative"}}, this.container);
        }
        this.canvas = dojo.create("div", {'class': 'grid-canvas', tabIndex:'0', hideFocus: ""}, this.viewport);
        
        // header columns and cells may have different padding/border skewing width calculations (box-sizing, hello?)
        // calculate the diff so we can set consistent sizes
        this.measureCellPaddingAndBorder();
        
     // for usability reasons, all text selection in SlickGrid is disabled
        // with the exception of input and textarea elements (selection must
        // be enabled there so that editors work as expected); note that
        // selection in grid cells (grid body) is already unavailable in
        // all browsers except IE
        this.disableSelection(this.headers); // disable all text selection in header (including input and textarea)
        dojo.connect(this.viewport, "selectstart", dojo.stopEvent); // disable text selection in grid cells except in input and textarea elements (this is IE-specific, because selectstart event will only fire in IE)
        
        this.viewportW = parseFloat(dojo.style(this.domNode, "width"));
        
        
        // create row mover after creation of canvas and before creation of column headers!
        if (this.options.enableMoveRows) {
            new RowMover(this.id);
            this.columns.splice(0, 0, this.columnRowMoveHandler);
        }
        
		this.createColumnHeaders();
        this.setupColumnSort();
		this.createCssRules();
		this.resizeAndRender();
		
		this.selectionModel = new RowSelector(this.id);
        
		dojo.connect(this.headerScroller, "click.slickgrid", dojo.hitch(this, this.handleHeaderClick));
		
		//bindAncestorScrollEvents();
        dojo.connect(this.viewport, "onscroll", dojo.hitch(this, this.handleScroll));
		dojo.connect(this.canvas, "click", dojo.hitch(this, this.handleClick));
		dojo.connect(this.canvas, "dblclick", dojo.hitch(this, this.handleDblClick));
		dojo.connect(this.canvas, "onkeydown", dojo.hitch(this, this.handleKeyDown));
		dojo.connect(this.canvas, "onFirstMove", dojo.hitch(this, this.handleKeyDown));
		
		dojo.subscribe("/resetColumns", this, this.reinitLastColumn);
		//dojo.connect(window, "onresize", this, this.reinitLastColumn);
		
		this.inherited(arguments);
	},
	
	reinitLastColumn: function() {
		
		console.debug("onShow -> resize last column");
		var totalColWidth = 0, lastColumn = this.columns.length-1;
		this.columns.forEach(function(c, i) {if (i < lastColumn) totalColWidth += c.width;});
		this.columns[lastColumn].width = dojo.style(this.domNode, "width") - totalColWidth - scrollbarDimensions.width;
		
		//dojo.query(".slick-header-column")
		this.applyColumnHeaderWidths();
		this.applyColumnWidths();
		/*var rowWidth = this.getRowWidth();
		
		var rule = this.findCssRule("." + this.uid + " .c" + lastColumn);
        rule.style.width = this.columns[lastColumn].width + "px";
        
        rule = this.findCssRule("." + this.uid + " .l" + i);
        rule.style.left = x + "px";

        rule = this.findCssRule("." + this.uid + " .r" + i);
        rule.style.right = (rowWidth - x - w) + "px";*/
	},
	
	// TODO:  this is static.  need to handle page mutation.
    bindAncestorScrollEvents: function() {
        var elem = this.canvas;
        while ((elem = elem.parentNode) != document.body) {
            // bind to scroll containers only
            if (elem == this.viewport || elem.scrollWidth != elem.clientWidth || elem.scrollHeight != elem.clientHeight)
                //$(elem).bind("scroll.slickgrid", handleActiveCellPositionChange);
            	dojo.connect(elem, "onscroll", this.handleActiveCellPositionChange);
        }
    },
	
	createCssRules: function() {
        var style = dojo.create("style", { type:'text/css', rel:'stylesheet'}, dojo.query("head")[0]);
        var rowHeight = (this.options.rowHeight - this.cellHeightDiff);

        var rules = [
            "." + this.uid + " .slick-header-column { left: 1000px; }",
            "." + this.uid + " .slick-top-panel { height:" + this.options.topPanelHeight + "px; }",
            "." + this.uid + " .slick-headerrow-columns { height:" + this.options.headerRowHeight + "px; }",
            "." + this.uid + " .slick-cell { height:" + rowHeight + "px; }",
            "." + this.uid + " .slick-row { width:" + this.getRowWidth() + "px; }",
            "." + this.uid + " .lr { float:none; position:absolute; }"
        ];

        var rowWidth = this.getRowWidth();
        var x = 0, w;
        for (var i=0; i<this.columns.length; i++) {
            w = this.columns[i].width;

            rules.push("." + this.uid + " .l" + i + " { left: " + x + "px; }");
            rules.push("." + this.uid + " .r" + i + " { right: " + (rowWidth - x - w) + "px; }");
            rules.push("." + this.uid + " .c" + i + " { width:" + (w - this.cellWidthDiff) + "px; }");
            // use max width of editable cell ... is there a better solution? (also do it when
            // changing column size!)
            //rules.push("." + this.uid + " .c" + i + ".editable { width:" + w + "px; }");

            x += this.columns[i].width;
        }

				this.stylesheet = document.styleSheets[document.styleSheets.length-1];
        if (style.styleSheet) { // IE
            //style.styleSheet.cssText = rules.join(" ");
						this.stylesheet.cssText += rules.join(" ");
        }
        else {
            style.appendChild(document.createTextNode(rules.join(" ")));
        }

        var sheets = document.styleSheets;
        for (var i=0; i<sheets.length; i++) {
            if ((sheets[i].ownerNode || sheets[i].owningElement) == style) {
                this.stylesheet = sheets[i];
                break;
            }
        }
    },
	
	measureCellPaddingAndBorder: function() {
        var el;
        var h = ["borderLeftWidth", "borderRightWidth", "paddingLeft", "paddingRight"];
        var v = ["borderTopWidth", "borderBottomWidth", "paddingTop", "paddingBottom"];

        el = dojo.create("div", {'class':'ui-state-default slick-header-column', style:{visibility:"hidden"}}, this.headers);
        this.headerColumnWidthDiff = this.headerColumnHeightDiff = 0;
        dojo.forEach(h, function(val, n) { 
        	this.headerColumnWidthDiff += parseFloat(dojo.style(el, val)) || 0; 
        	}, this);
        dojo.forEach(v, function(val, n) { this.headerColumnHeightDiff += parseFloat(dojo.style(el, val)) || 0; }, this);
        dojo.destroy(el);//.remove();

        var r = dojo.create("div", {'class':'slick-row'}, this.canvas);
        el = dojo.create("div", {'class':'slick-cell', id:'', style: {visibility:"hidden"}}, r);
        this.cellWidthDiff = this.cellHeightDiff = 0;
        dojo.forEach(h, function(val, n) { this.cellWidthDiff += parseFloat(dojo.style(el, val)) || 0; }, this);
        dojo.forEach(v, function(val, n) { this.cellHeightDiff += parseFloat(dojo.style(el, val)) || 0; }, this);
        dojo.destroy(r);//.remove();

        this.absoluteColumnMinWidth = Math.max(this.headerColumnWidthDiff,this.cellWidthDiff);
    },
	
	createColumnHeaders: function() {
        var i;

        function hoverBegin() {
            dojo.addClass(this, "ui-state-hover");
        }
        function hoverEnd() {
            dojo.removeClass(this, "ui-state-hover");
        }

        //$headers.empty();
        //$headerRow.empty();
        this.columnsById = {};

        var totalColumnsWidth = 0;
        for (i = 0; i < this.columns.length; i++) {
        	var defCol = dojo.clone(this.columnDefaults);
            var m = this.columns[i] = dojo.mixin(defCol, this.columns[i]);
            this.columnsById[m.id] = i;
            
            //if (i == this.columns.length-1)
            //	m.width = dojo.style(this.domNode, "width")-totalColumnsWidth-scrollbarDimensions.width;
            //else
            	//var colWidth = m.width-this.headerColumnWidthDiff;
            
            var header = dojo.create("div", {
            	'class': (m.headerCssClass ? m.headerCssClass : "") + ' ui-state-default slick-header-column',
            	id: this.uid + m.id,
            	title: m.toolTip || m.name || "",
            	//data: "fieldId="+ m.id,
            	style: {width: (m.width-this.headerColumnWidthDiff)+"px"}}, this.headers);
            header.data = {fieldId: m.id};
                
            dojo.create("span", {'class': 'slick-column-name', innerHTML: m.name}, header);
            
            totalColumnsWidth += m.width;

            if (this.options.enableColumnReorder || m.sortable) {
                //dojo.connect(header, "hover", (hoverBegin, hoverEnd));
            }

            if (m.sortable) {
                dojo.create("span", {'class':'slick-sort-indicator'}, header);
            }

            //if (options.showHeaderRow) {
            //    $("<div class='ui-state-default slick-headerrow-column c" + i + "'></div>").appendTo($headerRow);
            //}
        }

        this.setSortColumn(this.sortColumnId,this.sortAsc);
        this.setupColumnResize();
        if (this.options.enableColumnReorder) {
            //this.setupColumnReorder();
        }
    },
    
    setData: function(newData, scrollToTop, noRender) {
        this.invalidateAllRows();
        this.data = newData;
        if (scrollToTop)
            this.scrollTo(0);
        if (!noRender)
            this.resizeCanvas();
        this.onDataChanged({});
    },
    
    getData: function() {
        return this.data;
    },

    getDataLength: function() {
        if (this.data.getLength) {
            return this.data.getLength();
        }
        else {
            return this.data.length;
        }
    },

    getDataItem: function(i) {
        if (this.data.getItem) {
            return this.data.getItem(i);
        }
        else {
            return this.data[i];
        }
    },
    
    invalidate: function() {
        this.updateRowCount();
        this.invalidateAllRows();
        this.render();
     },

     invalidateAllRows: function() {
         if (this.currentEditor) {
             this.makeActiveCellNormal();
         }
         for (var row in this.rowsCache) {
             this.removeRowFromCache(row);
         }
     },

     removeRowFromCache: function(row) {
         var node = this.rowsCache[row];
         if (!node) { return; }
         this.canvas.removeChild(node);

         delete this.rowsCache[row];
         delete this.postProcessedRows[row];
         this.renderedRows--;
         this.counter_rows_removed++;
     },

     invalidateRows: function(rows) {
         var i, rl;
         if (!rows || !rows.length) { return; }
         scrollDir = 0;
         for (i=0, rl=rows.length; i<rl; i++) {
             if (this.currentEditor && this.activeRow === i) {
                 this.makeActiveCellNormal();
             }

             if (this.rowsCache[rows[i]]) {
                 this.removeRowFromCache(rows[i]);
             }
         }
     },

     invalidateRow: function(row) {
         this.invalidateRows([row]);
     },

     updateCell: function(row,cell) {
         var cellNode = this.getCellNode(row,cell);
         if (!cellNode) {
             return;
         }

         var m = this.columns[cell], d = this.getDataItem(row);
         if (this.currentEditor && this.activeRow === row && this.activeCell === cell) {
             this.currentEditor.loadValue(d);
         }
         else {
             cellNode.innerHTML = d ? this.getFormatter(row, m)(row, cell, d[m.field], m, d) : "";
             this.invalidatePostProcessingResults(row);
         }
     },
     
     invalidatePostProcessingResults: function(row) {
         delete this.postProcessedRows[row];
         this.postProcessFromRow = Math.min(this.postProcessFromRow,row);
         this.postProcessToRow = Math.max(this.postProcessToRow,row);
         this.startPostProcessing();
     },

     updateRow: function(row) {
         if (!this.rowsCache[row]) { return; }

         dojo.forEach(this.rowsCache[row].children, function(elem, i) {
             var m = this.columns[i];
             if (row === this.activeRow && i === this.activeCell && this.currentEditor) {
            	 this.currentEditor.loadValue(this.getDataItem(this.activeRow));
             }
             else if (this.getDataItem(row)) {
                 this.innerHTML = this.getFormatter(row, m)(row, i, this.getDataItem(row)[m.field], m, this.getDataItem(row));
             }
             else {
                 this.innerHTML = "";
             }
         }, this);

         this.invalidatePostProcessingResults(row);
     },
     
     render: function() {
         var visible = this.getVisibleRange();
         var rendered = this.getRenderedRange();

         // remove rows no longer in the viewport
         this.cleanupRows(rendered);

         // add new rows
         this.renderRows(rendered);

         postProcessFromRow = visible.top;
         
         var actualRows = this.options.enableAddRow ? this.options.visibleRowsInViewport-1 : this.options.visibleRowsInViewport;
         postProcessToRow = Math.min(this.getDataLength() <= actualRows ? actualRows : this.getDataLength(), visible.bottom);
         this.startPostProcessing();

         lastRenderedScrollTop = this.scrollTop;
         h_render = null;
     },
     
     renderRows: function(range) {
         var i, l,
             parentNode = this.canvas,
             rowsBefore = this.renderedRows,
             stringArray = [],
             rows = [],
             startTimestamp = new Date(),
             needToReselectCell = false;

         for (i = range.top; i <= range.bottom; i++) {
             if (this.rowsCache[i]) { continue; }
             this.renderedRows++;
             rows.push(i);
             this.appendRowHtml(stringArray,i);
             if (this.activeCellNode && this.activeRow === i) {
                 needToReselectCell = true;
             }
             this.counter_rows_rendered++;
         }

         var x = document.createElement("div");
         x.innerHTML = stringArray.join("");

         for (i = 0, l = x.childNodes.length; i < l; i++) {
             this.rowsCache[rows[i]] = parentNode.appendChild(x.firstChild);
         }

         if (needToReselectCell) {
             this.activeCellNode = this.getCellNode(this.activeRow,this.activeCell);
         }

         if (this.renderedRows - rowsBefore > 5) {
             this.avgRowRenderTime = (new Date() - startTimestamp) / (this.renderedRows - rowsBefore);
         }
     },
     
     appendRowHtml: function(stringArray, row) {
         var d = this.getDataItem(row);
         var dataLoading = row < this.getDataLength() && !d;
         var cellCss;
         var rowCss = "slick-row " +
             (dataLoading ? " loading" : "") +
             (row % 2 == 1 ? ' odd' : ' even');

         var metadata = this.data.getItemMetadata && this.data.getItemMetadata(row);

         if (metadata && metadata.cssClasses) {
             rowCss += " " + metadata.cssClasses;
         }

         stringArray.push("<div class='ui-widget-content " + rowCss + "' row='" + row + "' style='top:" + (this.options.rowHeight*row-this.offset) + "px'>");

         var colspan;
         var rowHasColumnData = metadata && metadata.columns;

         for (var i=0, cols=this.columns.length; i<cols; i++) {
             var m = this.columns[i];
             colspan = this.getColspan(row, i);  // TODO:  don't calc unless we have to
/*
             if (true || rowHasColumnData) {
                 cellCss = "slick-cell lr l" + i + " r" + Math.min(this.columns.length -1, i + colspan - 1) + (m.cssClass ? " " + m.cssClass : "");
             }
             else {
                 cellCss = "slick-cell c" + i + (m.cssClass ? " " + m.cssClass : "");
             }

             if (row === this.activeRow && i === this.activeCell) {
                 cellCss += (" active");
             }*/

             //cellCss = "slick-cell c" + i + (m.cssClass ? " " + m.cssClass : "");
             cellCss = "slick-cell c"+i+" lr l" + i + " r" + Math.min(this.columns.length -1, i + colspan - 1) + (m.cssClass ? " " + m.cssClass : "");
             if (row === this.activeRow && i === this.activeCell) {
                 cellCss += (" active");
             }

             // TODO:  merge them together in the setter
             for (var key in this.cellCssClasses) {
                 if (this.cellCssClasses[key][row] && this.cellCssClasses[key][row][m.id]) {
                     cellCss += (" " + this.cellCssClasses[key][row][m.id]);
                 }
             }

             stringArray.push("<div class='" + cellCss + "'>");

             // if there is a corresponding row (if not, this is the Add New row or this data hasn't been loaded yet)
             if (d) {
                 stringArray.push(this.getFormatter(row, m)(row, i, d[m.field], m, d));
             }

             stringArray.push("</div>");

             if (colspan)
                 i += (colspan - 1);
         }

         stringArray.push("</div>");
     },
     
     getViewportHeight: function() {
         return parseFloat(dojo.style(this.container, "height")) -
             this.options.headerHeight// -
             //this.getVBoxDelta(this.headers) //-
             //(this.options.showTopPanel ? this.options.topPanelHeight + this.getVBoxDelta(this.topPanelScroller) : 0) -
             //(this.options.showHeaderRow ? this.options.headerRowHeight + this.getVBoxDelta(this.headerRowScroller) : 0)
             ;
     },
     
     resizeCanvas: function() {
         var showRows = this.options.visibleRowsInViewport;
         if (!this.options.forceGridHeight) {
        	 
        	 showRows = (this.getDataLength() + (this.options.enableAddRow ? 1 : 0) + (this.options.leaveSpaceForNewRows? this.numVisibleRows - 1 : 0));
        	 if (showRows < this.options.visibleRowsInViewport) {
        		 showRows = this.options.visibleRowsInViewport;
        	 }
         }

         this.viewportH = this.options.rowHeight * showRows;

         this.numVisibleRows = Math.ceil(this.viewportH / this.options.rowHeight);
         this.viewportW = parseFloat(dojo.style(this.container, "width"));
         dojo.style(this.viewport, "height", this.viewportH+"px");

         var w = 0, i = this.columns.length;
         while (i--) {
             w += this.columns[i].width;
         }
         this.setCanvasWidth(w);

         this.updateRowCount();
         this.render();
     },
     
     resizeAndRender: function() {
         if (this.options.forceFitColumns) {
             this.autosizeColumns();
         } else {
             this.resizeCanvas();
         }
     },
     
     setCanvasWidth: function(width) {
    	 dojo.style(this.canvas, "width", width+"px");
         this.viewportHasHScroll = (width > this.viewportW - scrollbarDimensions.width);
     },
     
     measureScrollbar: function() {
         /// <summary>
         /// Measure width of a vertical scrollbar
         /// and height of a horizontal scrollbar.
         /// </summary
         /// <returns>
         /// { width: pixelWidth, height: pixelHeight }
         /// </returns>
         var c = dojo.create("div", {style:{position:"absolute", top:"-10000px", left:"-10000px", width:"100px", height:"100px", overflow:"scroll"}, innerHTML:"<div style='width:100%;height:100%;'></div>"}, dojo.body());
        
         var cont = dojo.isFF || dojo.isIE ? c.children[0] : c;
         var dim = { width: 100 - dojo.style(cont, "width"), height: 100 - dojo.style(cont, "height") };
         dojo.destroy(c);
         return dim;
     },
     
     updateRowCount: function() {
         var newRowCount = this.getDataLength() + (this.options.enableAddRow?1:0) + (this.options.leaveSpaceForNewRows?numVisibleRows-1:0);
         var oldH = this.h;

         // remove the rows that are now outside of the data range
         // this helps avoid redundant calls to .removeRow() when the size of the data decreased by thousands of rows
         var l = this.options.enableAddRow ? this.getDataLength() : this.getDataLength() - 1;
         for (var i in this.rowsCache) {
             if (i >= l) {
                 this.removeRowFromCache(i);
             }
         }
         this.th = Math.max(this.options.rowHeight * newRowCount, this.viewportH - scrollbarDimensions.height);
         if (this.th < this.maxSupportedCssHeight) {
             // just one page
        	 this.h = this.ph = this.th;
        	 this.n = 1;
        	 this.cj = 0;
         }
         else {
             // break into pages
        	 this.h = this.maxSupportedCssHeight;
        	 this.ph = this.h / 100;
        	 this.n = Math.floor(this.th / this.ph);
             this.cj = (this.th - this.h) / (this.n - 1);
         }

         if (this.h !== oldH) {
             dojo.style(this.canvas, "height", this.h+"px");
             this.scrollTop = this.viewport.scrollTop;
         }

         var oldScrollTopInRange = (this.scrollTop + this.offset <= this.th - this.viewportH);

         if (this.th == 0 || this.scrollTop == 0) {
             this.page = this.offset = 0;
         }
         else if (oldScrollTopInRange) {
             // maintain virtual position
             this.scrollTo(this.scrollTop+this.offset);
         }
         else {
             // scroll to bottom
             this.scrollTo(this.th-this.viewportH);
         }

         if (this.h != oldH && this.options.autoHeight) {
             this.resizeCanvas();
         }
     },
     
//////////////////////////////////////////////////////////////////////////////////////////////
     // Rendering / Scrolling

     scrollTo: function(y) {
         var oldOffset = this.offset;

         this.page = Math.min(this.n-1, Math.floor(y / this.ph));
         this.offset = Math.round(this.page * this.cj);
         var newScrollTop = y - this.offset;

         if (this.offset != oldOffset) {
             var range = this.getVisibleRange(newScrollTop);
             this.cleanupRows(range.top,range.bottom);
             this.updateRowPositions();
         }

         if (this.prevScrollTop != newScrollTop) {
        	 this.scrollDir = (this.prevScrollTop + oldOffset < newScrollTop + this.offset) ? 1 : -1;
        	 this.viewport.scrollTop = (this.lastRenderedScrollTop = this.scrollTop = this.prevScrollTop = newScrollTop);

             this.onViewportChanged(null, {});
         }
     },
     
     scrollRowIntoView: function(row, doPaging) {
         var rowAtTop = row * this.options.rowHeight;
         var rowAtBottom = (row + 1) * this.options.rowHeight - this.viewportH + (this.viewportHasHScroll?scrollbarDimensions.height:0);

         // need to page down?
         if ((row + 1) * this.options.rowHeight > this.scrollTop + this.viewportH + this.offset) {
             this.scrollTo(doPaging ? rowAtTop : rowAtBottom);
             this.render();
         }

         // or page up?
         else if (row * this.options.rowHeight < this.scrollTop + this.offset) {
             this.scrollTo(doPaging ? rowAtBottom : rowAtTop);
             this.render();
         }
     },
     
     getVisibleRange: function(viewportTop) {
         if (this.viewportTop == null)
             this.viewportTop = this.scrollTop;

         return {
             top: Math.floor((this.scrollTop+this.offset)/this.options.rowHeight),
             bottom: Math.ceil((this.scrollTop+this.offset+this.viewportH)/this.options.rowHeight)
         };
     },

     getRenderedRange: function(viewportTop) {
         var range = this.getVisibleRange(viewportTop);
         var buffer = Math.round(this.viewportH/this.options.rowHeight);
         var minBuffer = 3;

         if (this.scrollDir == -1) {
             range.top -= buffer;
             range.bottom += minBuffer;
         }
         else if (this.scrollDir == 1) {
             range.top -= minBuffer;
             range.bottom += buffer;
         }
         else {
             range.top -= minBuffer;
             range.bottom += minBuffer;
         }

         range.top = Math.max(0,range.top);
         range.bottom = Math.min(this.options.enableAddRow ? this.getDataLength() : this.getDataLength() - 1,range.bottom);

         var actualRows = this.options.visibleRowsInViewport;
         if (range.bottom < actualRows - 1) {
        	 range.bottom = actualRows - 1;
         }
         
         return range;
     },
     
     cleanupRows: function(rangeToKeep) {
         for (var i in this.rowsCache) {
             if (((i = parseInt(i, 10)) !== this.activeRow) && (i < rangeToKeep.top || i > rangeToKeep.bottom)) {
                 this.removeRowFromCache(i);
             }
         }
     },
     
     startPostProcessing: function() {
         if (!this.options.enableAsyncPostRender) { return; }
         clearTimeout(this.h_postrender);
         this.h_postrender = setTimeout(this.asyncPostProcessRows, this.options.asyncPostRenderDelay);
     },
     
     getColspan: function(row, cell) {
         var metadata = this.data.getItemMetadata && this.data.getItemMetadata(row);
         if (!metadata || !metadata.columns) {
             return 1;
         }

         var columnData = metadata.columns[columns[cell].id] || metadata.columns[cell];
         var colspan = (columnData && columnData.colspan);
         if (colspan === "*") {
             colspan = this.columns.length - cell;
         }
         return (colspan || 1);
     },
     
     getFormatter: function(row, column) {
         var rowMetadata = this.data.getItemMetadata && this.data.getItemMetadata(row);

         // look up by id, then index
         var columnOverrides = rowMetadata &&
                 rowMetadata.columns &&
                 (rowMetadata.columns[column.id] || rowMetadata.columns[this.getColumnIndex(column.id)]);

         return (columnOverrides && columnOverrides.formatter) ||
                 (rowMetadata && rowMetadata.formatter) ||
                 column.formatter ||
                 (this.options.formatterFactory && this.options.formatterFactory.getFormatter(column)) ||
                 this.defaultFormatter;
     },
     
     defaultFormatter: function(row, cell, value, columnDef, dataContext) {
//         return (value === null || value === undefined) ? "" : dojox.html.entities.encode(value);
         return (value === null || value === undefined) ? "" : value;
     },
     
     getColumnIndex: function(id) {
         return this.columnsById[id];
     },
     
     getMaxSupportedCssHeight: function() {
         var increment = 1000000;
         var supportedHeight = 0;
         // FF reports the height back but still renders blank after ~6M px
         var testUpTo = /*(dojo.isIE) ? 5000000 : */1000000000;
         var div = dojo.create("div", {style:{display:"none"}}, dojo.body());

         while (supportedHeight <= testUpTo) {
             dojo.style(div, "height", supportedHeight + increment + "px");
             if (dojo.style(div, "height") !== supportedHeight + increment)
                 break;
             else
                 supportedHeight += increment;
         }

         dojo.destroy(div);//.remove();
         return supportedHeight;
     },
     
     getRowWidth: function() {
         var rowWidth = 0;
         var i = this.columns.length;
         while (i--) {
             rowWidth += (this.columns[i].width || this.columnDefaults.width);
         }
         return rowWidth;
     },
     
     handleKeyDown: function(e) {
         //trigger(self.onKeyDown, {}, e);
         var handled = false;//e.isImmediatePropagationStopped();

         if (!handled) {
             if (!e.shiftKey && !e.altKey && !e.ctrlKey) {
                 if (e.which == 27) {
                     if (!this.getEditorLock().isActive()) {
                         return; // no editing mode to cancel, allow bubbling and default processing (exit without cancelling the event)
                     }
                     this.cancelEditAndSetFocus();
                 }
                 else if (e.which == 37 && !this.getEditorLock().isActive()) {
                	 this.navigateLeft();
                 }
                 else if (e.which == 39 && !this.getEditorLock().isActive()) {
                	 this.navigateRight();
                 }
                 else if (e.which == 38) {
                	 this.navigateUp();
                 }
                 else if (e.which == 40) {
                	 this.navigateDown();
                 }
                 else if (e.which == 9) {
                	 this.navigateNext();
                 }
                 else if (e.which == 13) {
                     if (this.options.editable) {
                         if (this.currentEditor) {
                             // adding new row
                             if (this.activeRow === this.getDataLength()) {
                            	 this.navigateRight();
                            	 this.setFocus();
                             }
                             else {
                            	 this.commitEditAndSetFocus();
                             }
                         } else {
                             if (this.getEditorLock().commitCurrentEdit()) {
                            	 this.makeActiveCellEditable();
                             }
                         }
                     }
                 }
                 else
                     return;
             }
             else if (e.which == 9 && e.shiftKey && !e.ctrlKey && !e.altKey) {
            	 this.navigatePrev();
             }
             else
                 return;
         }

         // the event has been handled so don't let parent element (bubbling/propagation) or browser (default) handle it
         e.stopPropagation();
         e.preventDefault();
         try {
             e.originalEvent.keyCode = 0; // prevent default behaviour for special keys in IE browsers (F3, F5, etc.)
         }
         catch (error) {} // ignore exceptions - setting the original event's keycode throws access denied exception for "Ctrl" (hitting control key only, nothing else), "Shift" (maybe others)
     },
     
     handleClick: function(e) {
         var cell = this.getCellFromEvent(e);
         if (!cell || (this.currentEditor !== null && this.activeRow == cell.row && this.activeCell == cell.cell)) {
             this.onMouseClickOutsideHandler();
             return;
         }

         //trigger(self.onClick, {row:cell.row, cell:cell.cell}, e);
         this.onMyCellClick(e, {row:cell.row, cell:cell.cell});
         //if (e.isImmediatePropagationStopped()) {
         //    return;
         //}
         var thisCell = this.getCellNode(cell.row,cell.cell);
         //if (this.canCellBeActive(cell.row, cell.cell)) {
         if (this.options.enableCellNavigation) {// && !columns[cell.cell].unselectable) {
             if (!this.getEditorLock().isActive() || this.getEditorLock().commitCurrentEdit()) {
                 //this.scrollRowIntoView(cell.row,false);
                 this.setActiveCellInternal(thisCell, (cell.row === this.getDataLength()) || this.options.autoEdit);
             }
         }
         
         // show info of long text which is necessary if no edit mode is available
         // if (dojo.query(".slick-cell.l2.r2")[1].scrollWidth > cellWidth)
         if (this.getDataItem(cell.row) && thisCell.scrollWidth > this.columns[cell.cell].width) {
        	 var tooltip = dijit.showTooltip(this.getDataItem(cell.row)[this.columns[cell.cell].field], thisCell);
        	 dojo.connect(thisCell, "onmouseout", function() {dijit.hideTooltip(thisCell);});
        	 //dojo.connect(tooltip.domNode, "onmouseenter", function() {console.debug("entered");});
         }
         
         // start listener for click outside grid to make sure editor is closed
         this.onMouseClickOutsideHandler();
     },
     
     onMouseClickOutsideHandler: function() {
         if (!this.mouse_is_inside) {
             this.mouse_is_inside = true;
             var mouseOver = dojo.connect(this.domNode, "onmouseenter", this, function(){
                 this.mouse_is_inside = true;
             });
             var mouseOut = dojo.connect(this.domNode, "onmouseleave", this, function(){
                 this.mouse_is_inside = false;
             });
             
             var eventWndClick = dojo.connect(dojo.isIE ? document : window, "onclick", this, function(evt){
                 if (!this.mouse_is_inside) {
                     var _this = this;
                     // popup inputs need to commit data first, otherwise value won't change
                     // -> not true anymore since event bubbling is stopped in datepicker!!!
                     //setTimeout(function(){
                        try {
                            if (_this.getEditorLock().activeEditController && _this.getEditorLock().activeEditController.fromTable == _this.id) {
                                var correct = _this.getEditorLock().commitCurrentEdit();
                                // if input was invalid we should return from cell editor by escaping!
                                if (correct) {
                                    if (_this.currentEditor) 
                                        _this.currentEditor.destroy();
                                    dojo.removeClass(_this.activeCellNode, "active");
                                }
                            }
                            _this.mouse_is_inside = false;
                        } 
                        catch (e) {
                            console.debug("Error");
                            console.debug(e);
                        }
                    //}, 100);
                    // end listener
                    dojo.disconnect(eventWndClick);
                    dojo.disconnect(mouseOver);
                    dojo.disconnect(mouseOut);
                }
            });
        }
     },
		 
    clickWithinDomNode: function(evt, node) {
        var target = evt.target;
        while (target != null) {
            if (target == node)
                return true;
            else
                target = target.parentNode;
        }
        return false;
    },
     
     handleHeaderClick: function(e) {
    	 var widget = dijit.getEnclosingWidget(e.target);
         var header = this._findParentElementByClass(e.target, ".slick-header-column");//e.target.closest(".slick-cell", this.canvas);
         
         //var header = $(e.target).closest(".slick-header-column", ".slick-header-columns");
         var column = header && this.columns[this.getColumnIndex(this.header.data("fieldId"))];
         //trigger(self.onHeaderClick, {column: column}, e);
     },
     
     canCellBeActive: function(row, cell) {
         if (!this.options.enableCellNavigation || row >= this.getDataLength() + (this.options.enableAddRow ? 1 : 0) || row < 0 || cell >= this.columns.length || cell < 0) {
             return false;
         }

         var rowMetadata = this.data.getItemMetadata && this.data.getItemMetadata(row);
         if (rowMetadata && typeof rowMetadata.focusable === "boolean") {
             return rowMetadata.focusable;
         }

         var columnMetadata = rowMetadata && rowMetadata.columns;
         if (columnMetadata && columnMetadata[columns[cell].id] && typeof columnMetadata[columns[cell].id].focusable === "boolean") {
             return columnMetadata[columns[cell].id].focusable;
         }
         if (columnMetadata && columnMetadata[cell] && typeof columnMetadata[cell].focusable === "boolean") {
             return columnMetadata[cell].focusable;
         }

         if (typeof this.columns[cell].focusable === "boolean") {
             return this.columns[cell].focusable;
         }

         return true;
     },

     canCellBeSelected: function(row, cell) {
         if (row >= this.getDataLength() || row < 0 || cell >= this.columns.length || cell < 0) {
             return false;
         }

         var rowMetadata = this.data.getItemMetadata && this.data.getItemMetadata(row);
         if (rowMetadata && typeof rowMetadata.selectable === "boolean") {
             return rowMetadata.selectable;
         }

         var columnMetadata = rowMetadata && rowMetadata.columns && (rowMetadata.columns[columns[cell].id] || rowMetadata.columns[cell]);
         if (columnMetadata && typeof columnMetadata.selectable === "boolean") {
             return columnMetadata.selectable;
         }

         if (typeof this.columns[cell].selectable === "boolean") {
             return this.columns[cell].selectable;
         }

         return true;
     },
     
     handleActiveCellPositionChange: function() {
         if (!this.activeCellNode) return;
         var cellBox;

         //trigger(self.onActiveCellPositionChanged, {});

         if (this.currentEditor) {
             cellBox = cellBox || this.getActiveCellPosition();
             if (this.currentEditor.show && this.currentEditor.hide) {
                 if (!cellBox.visible)
                	 this.currentEditor.hide();
                 else
                	 this.currentEditor.show();
             }

             if (this.currentEditor.position)
            	 this.currentEditor.position(cellBox);
         }
     },
     
     getActiveCellPosition: function(){
         return this.absBox(this.activeCellNode);
     },
     
     getActiveCell: function() {
         if (!this.activeCellNode)
             return null;
         else
             return {row: this.activeRow, cell: this.activeCell};
     },
     
     setActiveCell: function(row, cell) {
         if (row > this.getDataLength() || row < 0 || cell >= this.columns.length || cell < 0) {
             return;
         }

         if (!this.options.enableCellNavigation) {
             return;
         }

         this.scrollRowIntoView(row,false);
         this.setActiveCellInternal(this.getCellNode(row,cell),false);
     },
     
     setActiveCellInternal: function(newCell, editMode) {
         if (this.activeCellNode !== null) {
             this.makeActiveCellNormal();
             dojo.removeClass(this.activeCellNode, "active");
         }

         var activeCellChanged = (this.activeCellNode !== newCell);
         this.activeCellNode = newCell;

         if (this.activeCellNode != null) {
             this.activeRow = parseInt(this.activeCellNode.parentNode.getAttribute("row"));
             this.activeCell = this.activePosX = this.getCellFromNode(this.activeCellNode);

             dojo.addClass(this.activeCellNode, "active");

             if (this.options.editable && editMode && this.isCellPotentiallyEditable(this.activeRow,this.activeCell)) {
                 //clearTimeout(h_editorLoader);

                 //if (options.asyncEditorLoading) {
                 //    h_editorLoader = setTimeout(function() { makeActiveCellEditable(); }, options.asyncEditorLoadDelay);
                 //}
                 //else {
                     this.makeActiveCellEditable();
                 //}
             }
             else {
                   this.setFocus();
             }
         }
         else {
             this.activeRow = this.activeCell = null;
         }

         if (activeCellChanged) {
             this.scrollActiveCellIntoView();
						 var activeCellNow = this.getActiveCell();
             this.onActiveCellChanged(activeCellNow ? activeCellNow : {});
         }
     },
     
     isCellPotentiallyEditable: function(row, cell) {
         // is the data for this row loaded?
         if (row < this.getDataLength() && !this.getDataItem(row)) {
             return false;
         }

         // are we in the Add New row?  can we create new from this cell?
         if (this.columns[cell].cannotTriggerInsert/* && row >= this.getDataLength()*/) {
             return false;
         }

         // does this cell have an editor?
         if (!this.getEditor(row, cell)) {
             return false;
         }

         return true;
     },
     
     makeActiveCellEditable: function(editor) {
         if (!this.activeCellNode) { return; }
         if (!this.options.editable) {
             throw "Grid : makeActiveCellEditable : should never get called when options.editable is false";
         }

         // cancel pending async call if there is one
         clearTimeout(this.h_editorLoader);

         if (!this.isCellPotentiallyEditable(this.activeRow,this.activeCell)) {
             return;
         }

         var columnDef = this.columns[this.activeCell];
         var item = this.getDataItem(this.activeRow);

         /*if (trigger(self.onBeforeEditCell, {row:activeRow, cell:activeCell, item:item, column:columnDef}) === false) {
        	 this.setFocus();
             return;
         }*/

         this.getEditorLock().activate(this.editController);
         dojo.addClass(this.activeCellNode, "editable");

         // don't clear the cell if a custom editor is passed through
         if (!this.editor) {
        	 this.activeCellNode.innerHTML = "";
         }

         this.currentEditor = new (this.editor || this.getEditor(this.activeRow, this.activeCell))({
             grid: this,
             gridPosition: this.absBox(this.container),
             position: this.absBox(this.activeCellNode),
             container: this.activeCellNode,
             column: columnDef,
             item: item || {},
             commitChanges: this.commitEditAndSetFocus,
             cancelChanges: this.cancelEditAndSetFocus
         });
         
         if (item)
        	 this.currentEditor.loadValue(item);

         this.serializedEditorValue = this.currentEditor.serializeValue();

         if (this.currentEditor.position)
        	 this.handleActiveCellPositionChange();
     },
     
     commitEditAndSetFocus: function() {
         // if the commit fails, it would do so due to a validation error
         // if so, do not steal the focus from the editor
         if (this.getEditorLock().commitCurrentEdit()) {
             this.setFocus();

             if (this.options.autoEdit) {
                 this.navigateDown();
             }
         }
     },

     cancelEditAndSetFocus: function() {
         if (this.getEditorLock().cancelCurrentEdit()) {
             this.setFocus();
         }
     },
     
     absBox: function(elem) {
         var box = {top:elem.offsetTop, left:elem.offsetLeft, bottom:0, right:0, width:dojo.style(elem, "outerWidth"), height:dojo.style(elem, "outerHeight"), visible:true};
         box.bottom = box.top + box.height;
         box.right = box.left + box.width;

         // walk up the tree
         var offsetParent = elem.offsetParent;
         while (((elem = elem.parentNode) != document.body) && elem != null) {
             if (box.visible && elem.scrollHeight != elem.offsetHeight && dojo.style(elem, "overflowY") != "visible")
                 box.visible = box.bottom > elem.scrollTop && box.top < elem.scrollTop + elem.clientHeight;

             if (box.visible && elem.scrollWidth != elem.offsetWidth && dojo.style(elem, "overflowX") != "visible")
                 box.visible = box.right > elem.scrollLeft && box.left < elem.scrollLeft + elem.clientWidth;

             box.left -= elem.scrollLeft;
             box.top -= elem.scrollTop;

             if (elem === offsetParent) {
                 box.left += elem.offsetLeft;
                 box.top += elem.offsetTop;
                 offsetParent = elem.offsetParent;
             }

             box.bottom = box.top + box.height;
             box.right = box.left + box.width;
         }

         return box;
     },
     
     scrollActiveCellIntoView: function() {
         /*if (this.activeCellNode) {
             var left = this.activeCellNode.position().left,
                 right = left + this.activeCellNode.outerWidth(),
                 scrollLeft = this.viewport.scrollLeft(),
                 scrollRight = scrollLeft + this.viewport.width();

             if (left < scrollLeft)
            	 this.viewport.scrollLeft(left);
             else if (right > scrollRight)
            	 this.viewport.scrollLeft(Math.min(left, right - this.viewport.clientWidth));
         }*/
     },
     
     getEditorLock: function() {
         return this.options.editorLock;
     },
     
     getEditor: function(row, cell) {
         var column = this.columns[cell];
         var rowMetadata = this.data.getItemMetadata && this.data.getItemMetadata(row);
         var columnMetadata = rowMetadata && rowMetadata.columns;

         if (columnMetadata && columnMetadata[column.id] && columnMetadata[column.id].editor !== undefined) {
             return columnMetadata[column.id].editor;
         }
         if (columnMetadata && columnMetadata[cell] && columnMetadata[cell].editor !== undefined) {
             return columnMetadata[cell].editor;
         }

         return column.editor || (this.options.editorFactory && this.options.editorFactory.getEditor(column));
     },
     
     setFocus: function() {
         // IE tries to scroll the viewport so that the item being focused is aligned to the left border
         // IE-specific .setActive() sets the focus, but doesn't scroll
         if (dojo.isIE) {
             this.canvas.setActive();
         }
         else {
             this.canvas.focus();
         }
     },
/*
     handleContextMenu: function(e) {
         var cell = $(e.target).closest(".slick-cell", $canvas);
         if (cell.length === 0) { return; }

         // are we editing this cell?
         if (this.activeCellNode === cell && this.currentEditor !== null) { return; }

         //trigger(self.onContextMenu, {}, e);
     },
*/
     handleDblClick: function(e) {
         var cell = this.getCellFromEvent(e);
         if (!cell || (this.currentEditor !== null && this.activeRow == cell.row && this.activeCell == cell.cell)) {
             return;
         }

         //trigger(self.onDblClick, {row:cell.row, cell:cell.cell}, e);
         //if (e.isImmediatePropagationStopped()) {
         //    return;
         //}

         if (this.options.editable && !this.columns[cell.cell].cannotTriggerInsert) {
             this.gotoCell(cell.row, cell.cell, true); 
         }
     },
     
     gotoCell: function(row, cell, forceEdit) {
         if (!this.canCellBeActive(row, cell)) {
             return;
         }

         if (!this.getEditorLock().commitCurrentEdit()) { return; }

         //this.scrollRowIntoView(row,false);

         var newCell = this.getCellNode(row, cell);

         // if selecting the 'add new' row, start editing right away
         this.setActiveCellInternal(newCell, forceEdit || (row === this.getDataLength()) || this.options.autoEdit);

         // if no editor was created, set the focus back on the grid
         if (!this.currentEditor) {
        	 this.setFocus();
         }
     },
     
     getCellFromNode: function(node) {
         // read column number from .l1 or .c1 CSS classes
         var cls = /l\d+/.exec(node.className) || /c\d+/.exec(node.className);
         if (!cls)
             throw "getCellFromNode: cannot get cell - " + node.className;
         return parseInt(cls[0].substr(1, cls[0].length-1), 10);
     },

     getCellFromEvent: function(e) {
    	 var widget = dijit.getEnclosingWidget(e.target);
         var cell = this._findParentElementByClass(e.target, "slick-cell");//e.target.closest(".slick-cell", this.canvas);
         if (!cell)
             return null;

         return {
             row: cell.parentNode.getAttribute("row") | 0,
             cell: this.getCellFromNode(cell)
         };
     },
     
     _findParentElementByClass: function(element, clazz) {
    	 while (element) {
    		 if (dojo.hasClass(element, clazz))
    			 return element;
    		 
    		 element = element.parentNode;
    	 }
    	 return null;
     },
     
     getCellNode: function(row, cell) {
         if (this.rowsCache[row]) {
             var cells = this.rowsCache[row].children;
             var nodeCell;
             for (var i = 0; i < cells.length; i++) {
                 nodeCell = this.getCellFromNode(cells[i]);
                 if (nodeCell === cell) {
                     return cells[i];
                 }
                 else if (nodeCell > cell) {
                     return null;
                 }
             }
         }
         return null;
     },
     
     makeActiveCellNormal: function() {
         if (!this.currentEditor) { return; }
         //trigger(self.onBeforeCellEditorDestroy, {editor:currentEditor});
         this.currentEditor.destroy();
         this.currentEditor = null;

         if (this.activeCellNode) {
             dojo.removeClass(this.activeCellNode, "editable invalid");

             if (this.getDataItem(this.activeRow)) {
                 var column = this.columns[this.activeCell];
                 this.activeCellNode.innerHTML = this.getFormatter(this.activeRow, column)(this.activeRow, this.activeCell, this.getDataItem(this.activeRow)[column.field], column, this.getDataItem(this.activeRow));
                 this.invalidatePostProcessingResults(this.activeRow);
             }
         }

         // if there previously was text selected on a page (such as selected text in the edit cell just removed),
         // IE can't set focus to anything else correctly
         if (dojo.isIE) { this.clearTextSelection(); }

         this.getEditorLock().deactivate(this.editController);
     },
		 
		 clearTextSelection: function() {
				if (document.selection && document.selection.empty) {
						document.selection.empty();
				}
				else if (window.getSelection) {
						var sel = window.getSelection();
						if (sel && sel.removeAllRanges) {
								sel.removeAllRanges();
						}
				}
		},
     
     handleScroll: function() {
    	 this.scrollTop = this.viewport.scrollTop;
         var scrollLeft = this.viewport.scrollLeft;
         var scrollDist = Math.abs(this.scrollTop - this.prevScrollTop);

         if (scrollLeft !== this.prevScrollLeft) {
        	 this.prevScrollLeft = scrollLeft;
        	 this.headerScroller.scrollLeft = scrollLeft;
        	 //this.topPanelScroller.scrollLeft = scrollLeft;
        	 //this.headerRowScroller.scrollLeft = scrollLeft;
         }

         if (scrollDist) {
        	 this.scrollDir = this.prevScrollTop < this.scrollTop ? 1 : -1;
        	 this.prevScrollTop = this.scrollTop;

             // switch virtual pages if needed
             if (scrollDist < this.viewportH) {
            	 this.scrollTo(this.scrollTop + this.offset);
             }
             else {
                 var oldOffset = this.offset;
                 this.page = Math.min(this.n - 1, Math.floor(this.scrollTop * ((this.th - this.viewportH) / (this.h - this.viewportH)) * (1 / this.ph)));
                 this.offset = Math.round(this.page * this.cj);
                 if (oldOffset != this.offset)
                	 this.invalidateAllRows();
             }

             if (this.h_render)
            	 clearTimeout(this.h_render);

             if (Math.abs(this.lastRenderedScrollTop - this.scrollTop) < this.viewportH)
            	 this.render();
             else
            	 this.h_render = setTimeout(dojo.hitch(this, this.render), 50);

             //trigger(self.onViewportChanged, {});
         }

         //trigger(self.onScroll, {scrollLeft:scrollLeft, scrollTop:scrollTop});
     },
     
//////////////////////////////////////////////////////////////////////////////////////////////
     // IEditor implementation for the editor lock

     commitCurrentEdit: function() {
         var item = this.getDataItem(this.activeRow);
         var column = this.columns[this.activeCell];

         if (this.currentEditor) {
             if (this.currentEditor.isValueChanged()) {
                 var validationResults = this.currentEditor.validate();

                 if (validationResults.valid) {
                     if (this.activeRow < this.getDataLength()) {
                    	 var _this = this;
                         var editCommand = {
                             row: this.activeRow,
                             cell: this.activeCell,
                             editor: this.currentEditor,
                             serializedValue: this.currentEditor.serializeValue(),
                             prevSerializedValue: this.serializedEditorValue,
                             execute: function() {
                                 this.editor.applyValue(item,this.serializedValue);
                                 _this.updateRow(this.row);
                             },
                             undo: function() {
                                 this.editor.applyValue(item,this.prevSerializedValue);
                                 _this.updateRow(this.row);
                             }
                         };

                         if (this.options.editCommandHandler) {
                        	 this.makeActiveCellNormal();
                        	 this.options.editCommandHandler(item,column,editCommand);

                         }
                         else {
                        	 editCommand.execute();
                        	 this.makeActiveCellNormal();
                         }
                     }
                     else {
                         var newItem = {};
                         this.currentEditor.applyValue(newItem,this.currentEditor.serializeValue());
                         this.makeActiveCellNormal();
                         this.onAddNewRow({item:newItem, column:column, row:this.activeRow});
                         this.invalidateRow(this.data.length);
                         this.data.push(newItem);
                         this.updateRowCount();
                         this.render();
                     }
                     
                     this.onCellChange({
                         row: this.activeRow,
                         cell: this.activeCell,
                         item: newItem ? newItem : item
                     });
                     
                     // notify all subscribers who want to know that data has changed interactively
                     this.onDataChanged({});

                     // check whether the lock has been re-acquired by event handlers
                     return !this.getEditorLock().isActive();
                 }
                 else {
                     // TODO: remove and put in onValidationError handlers in examples
                	 dojo.addClass(this.activeCellNode, "invalid");
                     //$(activeCellNode).stop(true,true).effect("highlight", {color:"red"}, 300);

                	 
                     this.onValidationError({
                         editor: this.currentEditor,
                         cellNode: this.activeCellNode,
                         validationResults: validationResults,
                         row: this.activeRow,
                         cell: this.activeCell,
                         column: column
                     });

                	 this.currentEditor.focus();
                     return false;
                 }
             }

             this.makeActiveCellNormal();
         }
         return true;
     },

     cancelCurrentEdit: function() {
         this.makeActiveCellNormal();
         return true;
     },
     
     setupColumnResize: function() {
         var col, j, c, pageX, columnElements, minPageX, maxPageX, firstResizable, lastResizable, originalCanvasWidth;
         columnElements = this.headers.children;
         dojo.query(".slick-resizable-handle", this.headers).forEach(function(e) {dojo.destroy(e);});//columnElements.find().remove();
         dojo.forEach(columnElements, function(e,i) {
             if (this.columns[i].resizable) {
                 if (firstResizable === undefined) { firstResizable = i; }
                 lastResizable = i;
             }
         }, this);
         if (firstResizable === undefined) {
             return;
         }
         dojo.forEach(columnElements, function(e, i) {
             if (i < firstResizable || (this.options.forceFitColumns && i >= lastResizable)) { return; }
             //$col = $(e);
             var colResizer = dojo.create("div", {'class':'slick-resizable-handle'}, e);
             var resizerHandle = new dojo.dnd.Moveable(colResizer);
             //.appendTo(e)
             //.bind("dragstart", function(e,dd) {
             /*dojo.connect(colResizer, "dragstart", this, function(e, dd) {*/
             dojo.connect(resizerHandle, "onFirstMove", this, function(mover, e) {
            	 //console.debug(".");
                 if (!this.getEditorLock().commitCurrentEdit()) { return false; }
                 pageX = e.pageX;
                 dojo.addClass(this.domNode.parentNode, "slick-header-column-active");
                 var shrinkLeewayOnRight = null, stretchLeewayOnRight = null;
                 // lock each column's width option to current width
                 dojo.forEach(columnElements, function(el,i) { this.columns[i].previousWidth = dojo.style(el, "width")+this.headerColumnWidthDiff; }, this);
                 if (this.options.forceFitColumns) {
                     shrinkLeewayOnRight = 0;
                     stretchLeewayOnRight = 0;
                     // colums on right affect maxPageX/minPageX
                     for (j = i + 1; j < columnElements.length; j++) {
                         c = this.columns[j];
                         if (c.resizable) {
                             if (stretchLeewayOnRight !== null) {
                                 if (c.maxWidth) {
                                     stretchLeewayOnRight += c.maxWidth - c.previousWidth;
                                 }
                                 else {
                                     stretchLeewayOnRight = null;
                                 }
                             }
                             shrinkLeewayOnRight += c.previousWidth - Math.max(c.minWidth || 0, this.absoluteColumnMinWidth);
                         }
                     }
                 }
                 var shrinkLeewayOnLeft = 0, stretchLeewayOnLeft = 0;
                 for (j = 0; j <= i; j++) {
                     // columns on left only affect minPageX
                     c = this.columns[j];
                     if (c.resizable) {
                         if (stretchLeewayOnLeft !== null) {
                             if (c.maxWidth) {
                                 stretchLeewayOnLeft += c.maxWidth - c.previousWidth;
                             }
                             else {
                                 stretchLeewayOnLeft = null;
                             }
                         }
                         shrinkLeewayOnLeft += c.previousWidth - Math.max(c.minWidth || 0, this.absoluteColumnMinWidth);
                     }
                 }
                 if (shrinkLeewayOnRight === null) { shrinkLeewayOnRight = 100000; }
                 if (shrinkLeewayOnLeft === null) { shrinkLeewayOnLeft = 100000; }
                 if (stretchLeewayOnRight === null) { stretchLeewayOnRight = 100000; }
                 if (stretchLeewayOnLeft === null) { stretchLeewayOnLeft = 100000; }
                 maxPageX = pageX + Math.min(shrinkLeewayOnRight, stretchLeewayOnLeft);
                 minPageX = pageX - Math.min(shrinkLeewayOnLeft, stretchLeewayOnRight);
                 originalCanvasWidth = dojo.style(this.canvas, "width");
             });
            
             //.bind("drag", function(e,dd) {
             //dojo.connect(colResizer, "drag", this, function(e, dd) {\
             dojo.connect(resizerHandle, "onMove", this, function(mover, leftTop, e) {
                 var actualMinWidth, d = Math.min(maxPageX, Math.max(minPageX, e.pageX)) - pageX, x, ci;
                 if (d < 0) { // shrink column
                     //console.debug("-");
                     x = d;
                     for (j = i; j >= 0; j--) {
                         c = this.columns[j];
                         if (c.resizable) {
                             actualMinWidth = Math.max(c.minWidth || 0, this.absoluteColumnMinWidth);
                             if (x && c.previousWidth + x < actualMinWidth) {
                                 x += c.previousWidth - actualMinWidth;
                                 c.width = actualMinWidth;
                             } else {
                                 c.width = c.previousWidth + x;
                                 x = 0;
                             }
                         }
                     }

                     if (this.options.forceFitColumns) {
                         x = -d;
                         for (j = i + 1; j < columnElements.length; j++) {
                             c = this.columns[j];
                             if (c.resizable) {
                                 if (x && c.maxWidth && (c.maxWidth - c.previousWidth < x)) {
                                     x -= c.maxWidth - c.previousWidth;
                                     c.width = c.maxWidth;
                                 } else {
                                     c.width =  c.previousWidth + x;
                                     x = 0;
                                 }
                             }
                         }
                     } else if (this.options.syncColumnCellResize) {
                    	 this.setCanvasWidth(originalCanvasWidth + d);
                     }
                 } else { // stretch column
                     //console.debug("+: " + d + " (x="+pageX+")");
                     x = d;
                     for (j = i; j >= 0; j--) {
                         c = this.columns[j];
                         if (c.resizable) {
                             if (x && c.maxWidth && (c.maxWidth - c.previousWidth < x)) {
                                 x -= c.maxWidth - c.previousWidth;
                                 c.width = c.maxWidth;
                             } else {
                                 c.width = c.previousWidth + x;
                                 x = 0;
                             }
                         }
                     }

                     if (this.options.forceFitColumns) {
                         x = -d;
                         for (j = i + 1; j < columnElements.length; j++) {
                             c = this.columns[j];
                             if (c.resizable) {
                                 actualMinWidth = Math.max(c.minWidth || 0, this.absoluteColumnMinWidth);
                                 if (x && c.previousWidth + x < actualMinWidth) {
                                     x += c.previousWidth - actualMinWidth;
                                     c.width = actualMinWidth;
                                 } else {
                                     c.width = c.previousWidth + x;
                                     x = 0;
                                 }
                             }
                         }
                     } else if (this.options.syncColumnCellResize) {
                    	 this.setCanvasWidth(originalCanvasWidth + d);
                     }
                 }
                 this.applyColumnHeaderWidths();
                 if (this.options.syncColumnCellResize) {
                	 this.applyColumnWidths();
                 }
             });
             
             //.bind("dragend", function(e,dd) {
             //dojo.connect(colResizer, "dragend", this, function(e, dd) {
             dojo.connect(resizerHandle, "onMoveStop", this, function(mover, e) {
            	 //console.debug("|");
                 var newWidth;
                 dojo.removeClass(this.domNode.parentNode, "slick-header-column-active");
                 for (j = 0; j < columnElements.length; j++) {
                     c = this.columns[j];
                     newWidth = dojo.style(columnElements[j], "width")+this.headerColumnWidthDiff;

                     if (c.previousWidth !== newWidth && c.rerenderOnResize) {
                    	 this.invalidateAllRows();
                     }
                 }
                 // reposition drag handle
                 mover.node.style.cssText = "";
                 
                 this.applyColumnWidths();
                 this.resizeCanvas();
                 this.onColumnsResized({});
             });
         }, this);
         
     },
     
     applyColumnHeaderWidths: function() {
         var h;
         for (var i = 0, headers = this.headers.children, ii = headers.length; i < ii; i++) {
             h = headers[i];
             if (dojo.style(h, "width") !== this.columns[i].width - this.headerColumnWidthDiff) {
            	 dojo.style(h, "width", this.columns[i].width - this.headerColumnWidthDiff + "px");
             }
         }
     },
     
     applyColumnWidths: function() {
         var rowWidth = this.getRowWidth();
         var x = 0, w, rule;
         for (var i = 0; i < this.columns.length; i++) {
             w = this.columns[i].width;

             rule = this.findCssRule("." + this.uid + " .c" + i);
             rule.style.width = (w - this.cellWidthDiff) + "px";

             rule = this.findCssRule("." + this.uid + " .l" + i);
             rule.style.left = x + "px";

             rule = this.findCssRule("." + this.uid + " .r" + i);
             rule.style.right = (rowWidth - x - w) + "px";

             x += this.columns[i].width;
         }

         rule = this.findCssRule("." + this.uid + " .slick-row");
         rule.style.width = rowWidth + "px";
     },
     
     findCssRule: function(selector) {
         var rules = (this.stylesheet.cssRules || this.stylesheet.rules);

         for (var i=0; i<rules.length; i++) {
             if (rules[i].selectorText == selector)
                 return rules[i];
         }

         return null;
     },
     
     disableSelection: function(target) {
         /// <summary>
         /// Disable text selection (using mouse) in
         /// the specified target.
         /// </summary
         //if (target && $target.jquery) {
         //    $target.attr('unselectable', 'on').css('MozUserSelect', 'none').bind('selectstart.ui', function() { return false; }); // from jquery:ui.core.js 1.7.2
         //}
    	 dojo.connect(target, "selectstart", dojo.stopEvent);
     },
     
     getColspan: function(row, cell) {
         var metadata = this.data.getItemMetadata && this.data.getItemMetadata(row);
         if (!metadata || !metadata.columns) {
             return 1;
         }

         var columnData = metadata.columns[columns[cell].id] || metadata.columns[cell];
         var colspan = (columnData && columnData.colspan);
         if (colspan === "*") {
             colspan = this.columns.length - cell;
         }
         return (colspan || 1);
     },

     findFirstFocusableCell: function(row) {
         var cell = 0;
         while (cell < this.columns.length) {
             if (this.canCellBeActive(row, cell)) {
                 return cell;
             }
             cell += this.getColspan(row, cell);
         }
         return null;
     },

     findLastFocusableCell: function(row) {
         var cell = 0;
         var lastFocusableCell = null;
         while (cell < this.columns.length) {
             if (this.canCellBeActive(row, cell)) {
                 lastFocusableCell = cell;
             }
             cell += this.getColspan(row, cell);
         }
         return lastFocusableCell;
     },
     
     gotoRight: function(row, cell, posX) {
         if (cell >= this.columns.length) {
             return null;
         }

         do {
             cell += this.getColspan(row, cell);
         }
         while (cell < this.columns.length && !this.canCellBeActive(row, cell));

         if (cell < this.columns.length) {
             return {
                 "row": row,
                 "cell": cell,
                 "posX": cell
             };
         }
         return null;
     },

     gotoLeft: function(row, cell, posX) {
         if (cell <= 0) {
             return null;
         }

         var firstFocusableCell = this.findFirstFocusableCell(row);
         if (firstFocusableCell === null || firstFocusableCell >= cell) {
             return null;
         }

         var prev = {
             "row": row,
             "cell": firstFocusableCell,
             "posX": firstFocusableCell
         };
         var pos;
         while (true) {
             pos = this.gotoRight(prev.row, prev.cell, prev.posX);
             if (!pos) {
                 return null;
             }
             if (pos.cell >= cell) {
                 return prev;
             }
             prev = pos;
         }
     },

     gotoDown: function(row, cell, posX) {
         var prevCell;
         while (true) {
             if (++row >= this.getDataLength() + (this.options.enableAddRow ? 1 : 0)) {
                 return null;
             }

             prevCell = cell = 0;
             while (cell <= posX) {
                 prevCell = cell;
                 cell += this.getColspan(row, cell);
             }

             if (this.canCellBeActive(row, prevCell)) {
                 return {
                     "row": row,
                     "cell": prevCell,
                     "posX": posX
                 };
             }
         }
     },

     gotoUp: function(row, cell, posX) {
         var prevCell;
         while (true) {
             if (--row < 0) {
                 return null;
             }

             prevCell = cell = 0;
             while (cell <= posX) {
                 prevCell = cell;
                 cell += this.getColspan(row, cell);
             }

             if (this.canCellBeActive(row, prevCell)) {
                 return {
                     "row": row,
                     "cell": prevCell,
                     "posX": posX
                 };
             }
         }
     },

     gotoNext: function(row, cell, posX) {
         var pos = this.gotoRight(row, cell, posX);
         if (pos) {
             return pos;
         }

         var firstFocusableCell = null;
         while (++row < this.getDataLength() + (this.options.enableAddRow ? 1 : 0)) {
             firstFocusableCell = this.findFirstFocusableCell(row);
             if (firstFocusableCell !== null) {
                 return {
                     "row": row,
                     "cell": firstFocusableCell,
                     "posX": firstFocusableCell
                 };
             }
         }
         return null;
     },

     gotoPrev: function(row, cell, posX) {
         var pos;
         var lastSelectableCell;
         while (!pos) {
             pos = this.gotoLeft(row, cell, posX);
             if (pos) {
                 break;
             }
             if (--row < 0) {
                 return null;
             }

             cell = 0;
             lastSelectableCell = this.findLastFocusableCell(row);
             if (lastSelectableCell !== null) {
                 pos = {
                     "row": row,
                     "cell": lastSelectableCell,
                     "posX": lastSelectableCell
                 };
             }
         }
         return pos;
     },
     
     navigateRight: function() {
         this.navigate("right");
     },

     navigateLeft: function() {
    	 this.navigate("left");
     },

     navigateDown: function() {
    	 this.navigate("down");
     },

     navigateUp: function() {
    	 this.navigate("up");
     },

     navigateNext: function() {
    	 this.navigate("next");
     },

     navigatePrev: function() {
    	 this.navigate("prev");
     },

     navigate: function(dir) {
         if (!this.activeCellNode || !this.options.enableCellNavigation) { return; }
         if (!this.getEditorLock().commitCurrentEdit()) { return; }

         var stepFunctions = {
             "up":       this.gotoUp,
             "down":     this.gotoDown,
             "left":     this.gotoLeft,
             "right":    this.gotoRight,
             "prev":     this.gotoPrev,
             "next":     this.gotoNext
         };
         var stepFn = dojo.hitch(this, stepFunctions[dir]);
         var pos = stepFn(this.activeRow, this.activeCell, this.activePosX);
         if (pos) {
             var isAddNewRow = (pos.row == this.getDataLength());
             this.scrollRowIntoView(pos.row, !isAddNewRow);
             this.setActiveCellInternal(this.getCellNode(pos.row, pos.cell), true/*isAddNewRow || this.options.autoEdit*/);
             this.activePosX = pos.posX;
         }
     },
     
     getColumns: function() {
         return this.columns;
     },
     
     rowsToRanges: function(rows) {
         var ranges = [];
         var lastCell = this.columns.length - 1;
         for (var i = 0; i < rows.length; i++) {
             ranges.push(new Range(rows[i], 0, rows[i], lastCell));
         }
         return ranges;
     },
     
     setSelectedRows: function(rows) {
         if (!this.selectionModel) {
             throw "Selection model is not set";
         }
         this.selectionModel.setSelectedRanges(this.rowsToRanges(rows));
     },
     
     getSelectedRows: function() {
         if (!this.selectionModel) {
             throw "Selection model is not set";
         }
         return this.selectedRows;
     },
     
     getOptions: function() {
         return this.options;
     },
     
     resetActiveCell: function() {
         this.setActiveCellInternal(null,false);
     },
     
     handleSelectedRangesChanged: function(ranges) {
         this.selectedRows = [];
         var hash = {};
         for (var i = 0; i < ranges.length; i++) {
             for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++) {
                 if (!hash[j]) {  // prevent duplicates
                     this.selectedRows.push(j);
                 }
                 hash[j] = {};
                 for (var k = ranges[i].fromCell; k <= ranges[i].toCell; k++) {
                     if (this.canCellBeSelected(j, k)) {
                         hash[j][this.columns[k].id] = this.options.selectedCellCssClass;
                     }
                 }
             }
         }

         this.setCellCssStyles(this.options.selectedCellCssClass, hash);

         this.onSelectedRowsChanged({rows:this.getSelectedRows()});//, e);
     },
     
     addCellCssStyles: function(key,hash) {
         if (this.cellCssClasses[key]) {
             throw "addCellCssStyles: cell CSS hash with key '" + key + "' already exists.";
         }

         this.cellCssClasses[key] = hash;

         var node;
         for (var row in this.rowsCache) {
             if (hash[row]) {
                 for (var columnId in hash[row]) {
                     node = this.getCellNode(row, this.getColumnIndex(columnId));
                     if (node) {
                         dojo.addClass(node, hash[row][columnId]);
                     }
                 }
             }
         }
     },

     removeCellCssStyles: function(key) {
         if (!this.cellCssClasses[key]) {
             return;
         }

         var node;
         for (var row in this.rowsCache) {
             if (this.cellCssClasses[key][row]) {
                 for (var columnId in this.cellCssClasses[key][row]) {
                     node = this.getCellNode(row, this.getColumnIndex(columnId));
                     if (node) {
                         dojo.removeClass(node ,this.cellCssClasses[key][row][columnId]);
                     }
                 }
             }
         }

         delete this.cellCssClasses[key];
     },

     setCellCssStyles: function(key,hash) {
         this.removeCellCssStyles(key);
         this.addCellCssStyles(key,hash);
     },
     
     setOptions: function(args) {
         if (!this.getEditorLock().commitCurrentEdit()) {
             return;
         }

         this.makeActiveCellNormal();

         if (this.options.enableAddRow !== args.enableAddRow) {
             this.invalidateRow(getDataLength());
         }

         dojo.mixin(this.options, args);

         this.render();
     },
     
     // sort functionality
     setSortColumn: function(columnId, ascending) {
         this.sortColumnId = columnId;
         this.sortAsc = ascending;
         /*var columnIndex = getColumnIndex(this.sortColumnId);
         
         $headers.children().removeClass("slick-header-column-sorted");
         $headers.find(".slick-sort-indicator").removeClass("slick-sort-indicator-asc slick-sort-indicator-desc");
        
         if (columnIndex != null) {
             $headers.children().eq(columnIndex)
                .addClass("slick-header-column-sorted")
                .find(".slick-sort-indicator")
                    .addClass(sortAsc ? "slick-sort-indicator-asc" : "slick-sort-indicator-desc");
         }*/
     },
     
     setupColumnSort: function() {
            dojo.connect(this.headers, "onclick", this, function(e) {
                if (dojo.hasClass(e.target, "slick-resizable-handle")) {
                    return;
                }

                /*var $col = $(e.target).closest(".slick-header-column");
                if (!$col.length)
                    return;
*/
                var col = this._findParentElementByClass(e.target, "slick-header-column");
                if (!col)
                    return;
                var column = this.columns[this.getColumnIndex(col.data["fieldId"])];
                if (column.sortable) {
                    if (!this.getEditorLock().commitCurrentEdit())
                        return;

                    if (column.id === this.sortColumnId) {
                        this.sortAsc = !this.sortAsc;
                    }
                    else {
                        this.sortColumnId = column.id;
                        this.sortAsc = true;
                    }

                    this.setSortColumn(this.sortColumnId,this.sortAsc);
                    this.onSort(column.sortColField ? column.sortColField : column.field,this.sortAsc);
                }
            });
        },
        
        onSort: function(col, asc) {
            console.debug("sorting now!");
            var comp = function(a,b) {
                var x = a[col], y = b[col];
                return (x == y ? 0 : (x > y ? 1 : -1));
            };
            //this.setData(
            this.data.sort(comp);
            if (asc === false) this.data.reverse();
            this.invalidate();
        },
     
     // EVENTS
     notifyChangedData: function(msg) {
    	 this.onDataChanged(msg);
     },
     onDeleteItems: function(msg) {
    	 console.debug("onDeleteItems");
     },
     
     onAddNewRow: function() {},
     onDataChanged: function(msg) {},
     onSelectedRangesChanged: function() {},
     onActiveCellChanged: function(data) {},
     onMyCellClick: function(data) {},
     onDeleteRow: function() {},
     onSelectedRowsChanged: function() {},
     onContextMenu: function() {},
     onCellChange: function() {},
     onColumnsResized: function() {},
     onValidationError: function() {},
     onViewportChanged: function() {}     
     
});
