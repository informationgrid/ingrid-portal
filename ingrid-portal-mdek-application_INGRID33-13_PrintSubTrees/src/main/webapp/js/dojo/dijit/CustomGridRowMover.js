RowMover = function(gridId) {
	this.x = 1;
	var _dragging = false;
	var _grid = dijit.byId(gridId);
	var _canvas = _grid.canvas;
	var _proxy;
	var _guide;
	var _insertBefore;
	
    /*dojo.connect(dijit.byId("content").canvas, "onFirstMove", this, function(mover, e) {
   	 console.debug("rm.");
    });*/
	var _this = this;
	dojo.connect(_grid.domNode, "mousedown", function(e) {_this.handleDragStart(e);})
	dojo.connect(_grid.domNode, "mousemove", function(e) {_this.handleDrag(e); })
	dojo.connect(_grid.domNode, "mouseup", function(e) {_this.handleDragEnd(e); console.debug("m");})

	
	this.handleDragStart = function(e,dd) {
        var cell = _grid.getCellFromEvent(e);
        if (!cell) return;
        if (_grid.getEditorLock().isActive() || !/move|selectAndMove/.test(_grid.getColumns()[cell.cell].behavior)) {
            return false;
        }

        _dragging = true;
        //e.stopImmediatePropagation();

        /*var selectedRows = _grid.getSelectedRows();

        if (selectedRows.length == 0 || $.inArray(cell.row, selectedRows) == -1) {
            selectedRows = [cell.row];
            _grid.setSelectedRows(selectedRows);
        }*/

        var rowHeight = _grid.getOptions().rowHeight;

        _selectedRows = [cell.row];

        _proxy = dojo.create("div", {'class':'slick-reorder-proxy', 
        		style:"position:absolute; zIndex:99999; width:"+dojo.style(_canvas, "width")+
        		"px; height:"+rowHeight+"px;top:"+ (e.pageY - dojo.position(_canvas).y)+"px;"}, _canvas);

        _guide = dojo.create("div", {'class':'slick-reorder-guide', 
        		style:"position:absolute; zIndex:99998; width:"+dojo.style(_canvas, "width")+
        		"px; top:-1000px;"}, _canvas);

        _insertBefore = -1;

        //return $("<div></div>").appendTo(_canvas);
    }

    this.handleDrag = function(e,dd) {
        if (!_dragging) {
            return;
        }

        //e.stopImmediatePropagation();

        var top = e.pageY - dojo.position(_canvas).y;//$(_canvas).offset().top;
        dojo.style(_proxy, "top", top-5+"px");

        var insertBefore = Math.max(0,Math.min(Math.round(top/_grid.getOptions().rowHeight),_grid.getDataLength()));
        if (insertBefore !== _insertBefore) {
            /*var eventData = {
                "rows":         _selectedRows,
                "insertBefore": insertBefore
            };

            if (_self.onBeforeMoveRows.notify(eventData) === false) {
                dojo.style(_guide, "top", -1000);
                _canMove = false;
            }
            else {*/
                dojo.style(_guide, "top", insertBefore*_grid.getOptions().rowHeight+"px");
                //_canMove = true;
            //}

            _insertBefore = insertBefore;
        }
    }

    this.handleDragEnd = function(e,dd) {
        if (!_dragging) {
            return;
        }
        _dragging = false;
        //e.stopImmediatePropagation();

        dojo.destroy(_guide);
        dojo.destroy(_proxy);

        //if (dd.canMove) {
        if (_insertBefore != -1) {
            var eventData = {
                "rows":         _selectedRows,
                "insertBefore": _insertBefore
            };
            // TODO:  _grid.remapCellCssClasses ?
            this.moveRow(eventData);
        }
        
        console.debug("move row to: " + _insertBefore);
    },
    
    this.moveRow = function(args) {
		var extractedRows = [], left, right;
        var rows = args.rows;
        var insertBefore = args.insertBefore;
		left = _grid.data.slice(0,insertBefore);
		right = _grid.data.slice(insertBefore,_grid.data.length);

		for (var i=0; i<rows.length; i++) {
			extractedRows.push(_grid.data[rows[i]]);
		}

		rows.sort().reverse();

		for (var i=0; i<rows.length; i++) {
			var row = rows[i];
			if (row < insertBefore)
				left.splice(row,1);
			else
				right.splice(row-insertBefore,1);
		}

		data = left.concat(extractedRows.concat(right));

		var selectedRows = [];
		for (var i=0; i<rows.length; i++)
			selectedRows.push(left.length+i);

		_grid.resetActiveCell();
		_grid.setData(data);
		//_grid.setSelectedRows(selectedRows);
		_grid.render();
    }
};