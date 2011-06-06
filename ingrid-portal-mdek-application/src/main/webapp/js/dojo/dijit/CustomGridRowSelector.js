RowSelector = function(gridId) {
	var _grid;
    var _ranges = [];
    var _self = this;
    var _options = {
        selectActiveRow: true
    };

    //function init(grid) {
        //_options = $.extend(true, {}, _defaults, options);
        _grid = dijit.byId(gridId);
        //_grid.onActiveCellChanged.subscribe(handleActiveCellChange);
        //_grid.onKeyDown.subscribe(handleKeyDown);
        var _this = this;
        dojo.connect(_grid, "onActiveCellChanged", function(e) {_this.handleActiveCellChange(e);});
        dojo.connect(_grid, "onMyCellClick", function(e) {_this.handleClick(e);});
    //}

    this.destroy = function() {
        //_grid.onActiveCellChanged.unsubscribe(handleActiveCellChange);
        //_grid.onKeyDown.unsubscribe(handleKeyDown);
        //_grid.onClick.unsubscribe(handleClick);
    }

    this.rangesToRows = function(ranges) {
        var rows = [];
        for (var i = 0; i < ranges.length; i++) {
            for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++) {
                rows.push(j);
            }
        }
        return rows;
    }

    this.rowsToRanges = function(rows) {
        var ranges = [];
        var lastCell = _grid.getColumns().length - 1;
        for (var i = 0; i < rows.length; i++) {
            ranges.push(new Range(rows[i], 0, rows[i], lastCell));
        }
        return ranges;
    }

    this.getRowsRange = function(from,to) {
        var i, rows = [];
        for (i = from; i <= to; i++) {
            rows.push(i);
        }
        for (i = to; i < from; i++) {
            rows.push(i);
        }
        return rows;
    }

    this.getSelectedRows = function() {
        return rangesToRows(_ranges);
    }

    this.setSelectedRows = function(rows) {
    	this.setSelectedRanges(rowsToRanges(rows));
    }

    this.setSelectedRanges = function(ranges) {
        _ranges = ranges;
        _grid.handleSelectedRangesChanged(_ranges);
    }

    this.getSelectedRanges = function() {
        return _ranges;
    }

    this.handleActiveCellChange = function(data) {
        if (_options.selectActiveRow) {
        	this.setSelectedRanges([new Range(data.row, 0, data.row, _grid.getColumns().length - 1)]);
        }
    }

    this.handleKeyDown = function(e) {
        var activeRow = _grid.getActiveCell();
        if (activeRow && e.shiftKey && !e.ctrlKey && !e.altKey && !e.metaKey && (e.which == 38 || e.which == 40)) {
            var selectedRows = this.getSelectedRows();
            selectedRows.sort(function(x,y) { return x-y });

            if (!selectedRows.length) {
                selectedRows = [activeRow.row];
            }

            var top = selectedRows[0];
            var bottom = selectedRows[selectedRows.length - 1];
            var active;

            if (e.which == 40) {
                active = activeRow.row < bottom || top == bottom ? ++bottom : ++top;
            }
            else {
                active = activeRow.row < bottom ? --bottom : --top;
            }

            if (active >= 0 && active < _grid.getDataLength()) {
                _grid.scrollRowIntoView(active);
                _ranges = this.rowsToRanges(this.getRowsRange(top,bottom));
                this.setSelectedRanges(_ranges);
            }

            e.preventDefault();
            e.stopPropagation();
        }
    }

    this.handleClick = function(e) {
        var cell = _grid.getCellFromEvent(e);
        if (!cell || !_grid.canCellBeActive(cell.row, cell.cell)) {
            return false;
        }

        var selection = this.rangesToRows(_ranges);
        var idx = dojo.indexOf(selection, cell.row);

        if (!e.ctrlKey && !e.shiftKey && !e.metaKey) {
            return false;
        }
        else if (_grid.getOptions().multiSelect) {
            if (idx === -1 && (e.ctrlKey || e.metaKey)) {
                selection.push(cell.row);
                _grid.setActiveCell(cell.row, cell.cell);
            }
            else if (idx !== -1 && (e.ctrlKey || e.metaKey)) {
                selection = dojo.filter(selection, function(o) { return (o !== cell.row); });
                _grid.setActiveCell(cell.row, cell.cell);
            }
            else if (selection.length && e.shiftKey) {
                var last = selection.pop();
                var from = Math.min(cell.row, last);
                var to = Math.max(cell.row, last);
                selection = [];
                for (var i = from; i <= to; i++) {
                    if (i !== last) {
                        selection.push(i);
                    }
                }
                selection.push(last);
                _grid.setActiveCell(cell.row, cell.cell);
            }
        }

        _ranges = this.rowsToRanges(selection);
        this.setSelectedRanges(_ranges);
        //e.stopImmediatePropagation();

        return true;
    }
}

/***
 * A structure containing a range of cells.
 * @class Range
 * @constructor
 * @param fromRow {Integer} Starting row.
 * @param fromCell {Integer} Starting cell.
 * @param toRow {Integer} Optional. Ending row. Defaults to <code>fromRow</code>.
 * @param toCell {Integer} Optional. Ending cell. Defaults to <code>fromCell</code>.
 */
function Range(fromRow, fromCell, toRow, toCell) {
    if (toRow === undefined && toCell === undefined) {
        toRow = fromRow;
        toCell = fromCell;
    }

    /***
     * @property fromRow
     * @type {Integer}
     */
    this.fromRow = Math.min(fromRow, toRow);

    /***
     * @property fromCell
     * @type {Integer}
     */
    this.fromCell = Math.min(fromCell, toCell);

    /***
     * @property toRow
     * @type {Integer}
     */
    this.toRow = Math.max(fromRow, toRow);

    /***
     * @property toCell
     * @type {Integer}
     */
    this.toCell = Math.max(fromCell, toCell);

    /***
     * Returns whether a range represents a single row.
     * @method isSingleRow
     * @return {Boolean}
     */
    this.isSingleRow = function() {
        return this.fromRow == this.toRow;
    };

    /***
     * Returns whether a range represents a single cell.
     * @method isSingleCell
     * @return {Boolean}
     */
    this.isSingleCell = function() {
        return this.fromRow == this.toRow && this.fromCell == this.toCell;
    };

    /***
     * Returns whether a range contains a given cell.
     * @method contains
     * @param row {Integer}
     * @param cell {Integer}
     * @return {Boolean}
     */
    this.contains = function(row, cell) {
        return row >= this.fromRow && row <= this.toRow &&
               cell >= this.fromCell && cell <= this.toCell;
    };

    /***
     * Returns a readable representation of a range.
     * @method toString
     * @return {String}
     */
    this.toString = function() {
        if (this.isSingleCell()) {
            return "(" + this.fromRow + ":" + this.fromCell + ")";
        }
        else {
            return "(" + this.fromRow + ":" + this.fromCell + " - " + this.toRow + ":" + this.toCell + ")";
        }
    }
}