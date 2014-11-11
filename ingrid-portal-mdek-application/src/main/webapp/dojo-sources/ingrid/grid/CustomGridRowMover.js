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
    "dojo/on",
    "dojo/dom-construct",
    "dojo/dom-geometry",
    "dojo/dom-style"
], function(declare, array, Deferred, on, construct, geometry, style) {
    return declare(null, {
        x: 1,
        _dragging: false,
        _grid: null,
        _canvas: null,
        _proxy: null,
        _guide: null,
        _insertBefore: null,
        _selectedRows: null,
        
        constructor: function(gridId) {
            
            /*on(dijit.byId("content").canvas, "onFirstMove", this, function(mover, e) {
             console.debug("rm.");
            });*/
            var _this = this;
            this._grid = dijit.byId(gridId);
            this._canvas = this._grid.canvas;
            on(this._grid.domNode, "mousedown", function(e) {_this.handleDragStart(e);});
            on(this._grid.domNode, "mousemove", function(e) {_this.handleDrag(e); });
            on(this._grid.domNode, "mouseup", function(e) {_this.handleDragEnd(e); console.debug("m");});
        },
            
        handleDragStart: function(e,dd) {
            var cell = this._grid.getCellFromEvent(e);
            if (!cell) return;
            if (this._grid.getEditorLock().isActive() || !/move|selectAndMove/.test(this._grid.getColumns()[cell.cell].behavior)) {
                return false;
            }
    
            this._dragging = true;
            //e.stopImmediatePropagation();
    
            /*var selectedRows = this._grid.getSelectedRows();
    
            if (selectedRows.length == 0 || $.inArray(cell.row, selectedRows) == -1) {
                selectedRows = [cell.row];
                this._grid.setSelectedRows(selectedRows);
            }*/
    
            var rowHeight = this._grid.getOptions().rowHeight;
    
            this._selectedRows = [cell.row];
    
            this._proxy = construct.create("div", {'class':'slick-reorder-proxy',
                    style:"position:absolute; zIndex:99999; width:"+style.get(this._canvas, "width")+
                    "px; height:"+rowHeight+"px;top:"+ (e.pageY - geometry.position(this._canvas).y)+"px;"}, this._canvas);
    
            this._guide = construct.create("div", {'class':'slick-reorder-guide',
                    style:"position:absolute; zIndex:99998; width:"+style.get(this._canvas, "width")+
                    "px; top:-1000px;"}, this._canvas);
    
            this._insertBefore = -1;
    
            //return $("<div></div>").appendTo(this._canvas);
        },
        
        handleDrag: function(e,dd) {
            if (!this._dragging) {
                return;
            }
    
            //e.stopImmediatePropagation();
    
            var top = e.pageY - geometry.position(this._canvas).y;//$(this._canvas).offset().top;
            style.set(this._proxy, "top", top-5+"px");
    
            var insertBefore = Math.max(0,Math.min(Math.round(top/this._grid.getOptions().rowHeight),this._grid.getDataLength()));
            if (insertBefore !== this._insertBefore) {
                /*var eventData = {
                    "rows":         this._selectedRows,
                    "insertBefore": insertBefore
                };
    
                if (_self.onBeforeMoveRows.notify(eventData) === false) {
                    style(this._guide, "top", -1000);
                    _canMove = false;
                }
                else {*/
                style.set(this._guide, "top", insertBefore*this._grid.getOptions().rowHeight+"px");
                    //_canMove = true;
                //}
    
                this._insertBefore = insertBefore;
            }
        },
    
        handleDragEnd: function(e,dd) {
            if (!this._dragging) {
                return;
            }
            this._dragging = false;
            //e.stopImmediatePropagation();
    
            construct.destroy(this._guide);
            construct.destroy(this._proxy);
    
            //if (dd.canMove) {
            if (this._insertBefore != -1) {
                var eventData = {
                    "rows":         this._selectedRows,
                    "insertBefore": this._insertBefore
                };
                // TODO:  this._grid.remapCellCssClasses ?
                this.moveRow(eventData);
            }
            
            console.debug("move row to: " + this._insertBefore);
        },
        
        moveRow: function(args) {
            var extractedRows = [], left, right;
            var rows = args.rows;
            var insertBefore = args.insertBefore;
            left = this._grid.data.slice(0,insertBefore);
            right = this._grid.data.slice(insertBefore,this._grid.data.length);
    
            var i;
            for (i=0; i<rows.length; i++) {
                extractedRows.push(this._grid.data[rows[i]]);
            }
    
            rows.sort().reverse();
    
            for (i=0; i<rows.length; i++) {
                var row = rows[i];
                if (row < insertBefore)
                    left.splice(row,1);
                else
                    right.splice(row-insertBefore,1);
            }
    
            var data = left.concat(extractedRows.concat(right));
    
            var selectedRows = [];
            for (i=0; i<rows.length; i++)
                selectedRows.push(left.length+i);
    
            this._grid.resetActiveCell();
            this._grid.setData(data);
            //this._grid.setSelectedRows(selectedRows);
            this._grid.render();
        }
    });
});