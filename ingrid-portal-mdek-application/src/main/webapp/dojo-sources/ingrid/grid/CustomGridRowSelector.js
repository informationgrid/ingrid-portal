/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/Deferred",
    "dojo/aspect"
], function(declare, array, Deferred, aspect) {
    return declare(null, {
        //_grid: null,
        //ranges : [],
        //_self = this,
        constructor: function(grid) {
            //function init(grid) {
            //this._options = $.extend(true, {}, _defaults, options);
            this._grid = grid;
            this._ranges = [];
            this._options = {
                selectActiveRow: true
            };
            //_grid.onActiveCellChanged.subscribe(handleActiveCellChange);
            //_grid.onKeyDown.subscribe(handleKeyDown);
            var _this = this;
            aspect.after(this._grid, "onActiveCellChanged", function(result, args) {
                _this.handleActiveCellChange(args[0]);
            });
            aspect.after(this._grid, "onMyCellClick", function(result, args) {_this.handleClick(args[0]);});
        },
    
        destroy : function() {
            //_grid.onActiveCellChanged.unsubscribe(handleActiveCellChange);
            //_grid.onKeyDown.unsubscribe(handleKeyDown);
            //_grid.onClick.unsubscribe(handleClick);
        },
    
        rangesToRows : function(ranges) {
            var rows = [];
            for (var i = 0; i < ranges.length; i++) {
                for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++) {
                    rows.push(j);
                }
            }
            return rows;
        },
    
        rowsToRanges : function(rows) {
            var ranges = [];
            var lastCell = this._grid.getColumns().length - 1;
            for (var i = 0; i < rows.length; i++) {
                ranges.push(new this.Range(rows[i], 0, rows[i], lastCell));
            }
            return ranges;
        },
    
        getRowsRange : function(from,to) {
            var i, rows = [];
            for (i = from; i <= to; i++) {
                rows.push(i);
            }
            for (i = to; i < from; i++) {
                rows.push(i);
            }
            return rows;
        },
    
        getSelectedRows : function() {
            return this.rangesToRows(this._ranges);
        },
    
        setSelectedRows : function(rows) {
            this.setSelectedRanges(this.rowsToRanges(rows));
        },
    
        setSelectedRanges : function(ranges) {
            this._ranges = ranges;
            this._grid.handleSelectedRangesChanged(this._ranges);
        },
    
        getSelectedRanges : function() {
            return this._ranges;
        },
    
        handleActiveCellChange : function(data) {
            if (this._options.selectActiveRow) {
                this.setSelectedRanges([new this.Range(data.row, 0, data.row, this._grid.getColumns().length - 1)]);
            }
        },
    
        handleKeyDown : function(e) {
            var activeRow = this._grid.getActiveCell();
            if (activeRow && e.shiftKey && !e.ctrlKey && !e.altKey && !e.metaKey && (e.which == 38 || e.which == 40)) {
                var selectedRows = this.getSelectedRows();
                selectedRows.sort(function(x,y) { return x-y; });
    
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
    
                if (active >= 0 && active < this._grid.getDataLength()) {
                    this._grid.scrollRowIntoView(active);
                    this._ranges = this.rowsToRanges(this.getRowsRange(top,bottom));
                    this.setSelectedRanges(this._ranges);
                }
    
                e.preventDefault();
                e.stopPropagation();
            }
        },
    
        handleClick : function(e) {
            var cell = this._grid.getCellFromEvent(e);
            if (!cell || !this._grid.canCellBeActive(cell.row, cell.cell)) {
                return false;
            }
    
            var selection = this.rangesToRows(this._ranges);
            var idx = array.indexOf(selection, cell.row);
    
            if (!e.ctrlKey && !e.shiftKey && !e.metaKey) {
                return false;
            }
            else if (this._grid.getOptions().multiSelect) {
                if (idx === -1 && (e.ctrlKey || e.metaKey)) {
                    selection.push(cell.row);
                    this._grid.setActiveCell(cell.row, cell.cell);
                }
                else if (idx !== -1 && (e.ctrlKey || e.metaKey)) {
                    selection = array.filter(selection, function(o) { return (o !== cell.row); });
                    this._grid.setActiveCell(cell.row, cell.cell);
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
                    this._grid.setActiveCell(cell.row, cell.cell);
                }
            }
    
            this._ranges = this.rowsToRanges(selection);
            this.setSelectedRanges(this._ranges);
            //e.stopImmediatePropagation();
    
            return true;
        },

        /***
         * A structure containing a range of cells.
         * @class Range
         * @constructor
         * @param fromRow {Integer} Starting row.
         * @param fromCell {Integer} Starting cell.
         * @param toRow {Integer} Optional. Ending row. Defaults to <code>fromRow</code>.
         * @param toCell {Integer} Optional. Ending cell. Defaults to <code>fromCell</code>.
         */
        Range: function(fromRow, fromCell, toRow, toCell) {
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
            };
        }

    });
});

