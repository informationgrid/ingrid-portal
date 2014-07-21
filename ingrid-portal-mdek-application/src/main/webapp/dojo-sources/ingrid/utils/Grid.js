define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/aspect",
    "ingrid/dialog",
    "ingrid/message"
], function(declare, array, aspect, dialog, message) {
    return declare(null, {

        getTable: function(grid) {
            return gridManager[grid];
        },

        getSelectedFromGrid: function(keywordTable) {
            var allSelected = keywordTable.selection.getSelected();
            return array.filter(allSelected, function(item) {
                return item._isEmptyRow != "true";
            });
        },

        // ALWAYS filters if not true argument passed (and if rowFilter is set on table) !
        // So visible data is returned and frontend works the usual way with row indexes !
        getTableData: function(grid, doNOTFilter) {
            return gridManager[grid].getData(doNOTFilter);
        },

        setTableData: function(gridId, data) {
            var grid = this.getTable(gridId);
            // request UNFILTERED data ! Get full data store reference for check !
            var gridData = this.getTableData(gridId, true);
            if (gridData instanceof Array) {
                grid.setData(data, true, true); // scroll to top AND do not resize!
                // just render
                grid.resizeCanvas();
            } else {
                gridData.setItems(data);
                grid.invalidate();
            }
        },

        addTableDataRow: function(grid, item) {
            // request UNFILTERED data ! Is added to full data store reference !
            var data = this.getTableData(grid, true);
            if (data instanceof Array)
                data.push(item);
            else {
                var items = data.getItems();
                item._id = this.getUniqueId(items);
                items.push(item);
                data.refresh();
            }
            this.getTable(grid).invalidate();
            this.getTable(grid).notifyChangedData({
                type: "added",
                item: item
            });
        },

        getUniqueId: function(items) {
            var idMax = 0;
            array.forEach(items, function(item) {
                if (item._id > idMax)
                    idMax = item._id;
            });
            return idMax + 1;
        },

        updateTableDataRow: function(grid, row, item) {
            // request FILTERED data so row indexes work !
            var data = this.getTableData(grid);
            var theGrid = this.getTable(grid);

            // Check row filter on Grid. If set then data is filtered data !
            if (theGrid.getRowFilter()) {
                // Filtered data ! This is not the grid store reference !!!
                // So we determine all items and update them afterwards in real grid store !
                if (data instanceof Array === false) {
                    data = data.getItems();
                }

                this._updateItemsInTable(grid, [data[row]], [item]);
            } else {
                // data is referenced store !
                data.splice(row, 1, item);
            }

            theGrid.invalidate();
            theGrid.notifyChangedData({});
        },

        // "private" method for replacing items with other items (full item data passed).
        _updateItemsInTable: function(gridId, oldItems, newItems) {
            // Get UNFILTERED store -> store reference !
            var data = this.getTableData(gridId, true);
            var refresh = false;

            var dataArray = data;
            if (dataArray instanceof Array === false) {
                dataArray = data.getItems();
                refresh = true;
            }
            array.forEach(oldItems, function(oldItem, oldItemIndex) {
                array.some(dataArray, function(storeItem, storeItemIndex) {
                    if (storeItem === oldItem) {
                        console.debug("update item: " + storeItemIndex + " of: " + gridId + " with new item: ");
                        console.debug(newItems[oldItemIndex]);
                        dataArray.splice(storeItemIndex, 1, newItems[oldItemIndex]);
                    }
                });
            });

            if (refresh) {
                data.refresh();
            }
        },

        updateTableDataRowAttr: function(grid, row, attr, value) {
            // request FILTERED data so indexes work !
            var data = this.getTableData(grid);
            var theGrid = this.getTable(grid);

            // Check row filter on Grid. If set then data is filtered data !
            if (theGrid.getRowFilter()) {
                // Filtered data ! This is not the grid store reference !!!
                // So we determine item and update it afterwards in real grid store !
                if (data instanceof Array === false) {
                    data = data.getItems();
                }
                var itemToUpdate = [data[row]];

                // Get UNFILTERED store -> store reference !
                data = this.getTableData(grid, true);
                if (data instanceof Array === false) {
                    data = data.getItems();
                }
                array.some(data, function(storeItem) {
                    if (storeItem === itemToUpdate) {
                        console.debug("update item/value: " + attr + " of: " + grid + " with value: " + value);
                        storeItem[attr] = value;
                    }
                });

            } else {
                // data is referenced store !
                if (data instanceof Array)
                    data[row][attr] = value;
                else
                    data.getItem(row)[attr] = value;
            }

            theGrid.updateRow(row);
            // we need invalidate so table renders new !!!
            theGrid.invalidate();
            theGrid.notifyChangedData({});
        },

        getSelectedRowIndexes: function(grid) {
            return gridManager[grid].getSelectedRows();
        },

        getSelectedData: function(grid) {
            var slickGrid = gridManager[grid];
            var data = [];
            var rows = slickGrid.getSelectedRows();
            array.forEach(rows, function(row) {
                data.push(slickGrid.getDataItem(row));
            });
            return data;
        },

        clearSelection: function(grid) {
            this.getTable(grid).setSelectedRows([]);
        },

        setSelection: function(grid, selection) {
            this.getTable(grid).setSelectedRows(selection);
        },

        removeTableDataRow: function(grid, itemIndexes) {
            var refresh = false;

            // we do not allow to delete from this table, because links only go: service -> data
            if (grid == "ref1ServiceLink") {
                dialog.show( message.get( "dialog.general.info" ), message.get( "dialog.cannot.modify.table" ), dialog.INFO );
                return;
            }

            var table = this.getTable(grid);
            // request FILTERED data so indexes work !
            var data = this.getTableData(grid);
            if (data instanceof Array === false) {
                data = data.getItems();
                refresh = true;
            }

            var deletedData = [];
            // Check row filter on Grid. If set then data is filtered data !
            if (table.getRowFilter()) {
                // Filtered data ! This is not the grid store reference !!!
                // So we determine all items and delete them afterwards in real grid store !
                // get items to delete from filtered store !
                array.forEach(itemIndexes, function(rowIndex) {
                    deletedData.push(data[rowIndex]);
                });

                this._removeItemsFromTable(grid, deletedData);
            } else {
                // data is referenced store !       
                var sortedIndexes = itemIndexes.sort();
                var decr = 0; // when removing an element the selected row moved up!
                array.forEach(sortedIndexes, function(rowNr) {
                    deletedData.push(data.splice(rowNr - (decr++), 1)[0]);
                });
                if (refresh) this.getTableData(grid).refresh();
            }

            this.clearSelection(grid);
            table.resetActiveCell();
            table.invalidate();
            table.resizeCanvas();

            gridManager[grid].notifyChangedData({
                type: "deleted",
                items: deletedData
            });
            gridManager[grid].onDeleteItems({
                items: deletedData,
                grid: grid
            });
            return deletedData;
        },

        // "private" method for deleting items in store (full item data passed).
        _removeItemsFromTable: function(gridId, deletedItems) {
            var refresh = false;

            // Get UNFILTERED store -> store reference !
            var data = this.getTableData(gridId, true);

            var dataArray = data;
            if (dataArray instanceof Array === false) {
                dataArray = data.getItems();
                refresh = true;
            }
            array.forEach(deletedItems, function(deletedItem) {
                array.some(dataArray, function(item, i) {
                    //if (dojo.every(fields, function(field) { return item[field] == deletedItem[field]; })) {
                    if (item === deletedItem) {
                        console.debug("remove item: " + i + " from: " + gridId);
                        console.debug(item);
                        dataArray.splice(i, 1);
                    }
                });
            });

            if (refresh) {
                data.refresh();
            }
        },

        updateOption: function(grid, optionKey, optionValue) {
            var options = this.getTable(grid).getOptions();
            options[optionKey] = optionValue;
            this.getTable(grid).setOptions(options);
        },

        generateIDs: function(data, id) {
            var i = 0;
            array.forEach(data, function(d) {
                d[id] = i++;
            });
            return data;
        },

        synchedDelete: function(checkGrids, result, args) {
            var msg = args[0];
            // do not do anything if this was the table, that deleted an item
            //if (msg.grid == this.eval("$container[0].id")) {return;}

            array.forEach(checkGrids, function(gridId) {
                this._removeItemsFromTable(gridId, msg.items);

                var grid = this.getTable(gridId);
                grid.invalidate();
                grid.notifyChangedData({
                    type: "deleted",
                    items: msg.items
                });
            }, this);
        },

        addRowSelectionCallback: function(gridId, onSelectCallback, callbackData) {
            if (!callbackData) {
                callbackData = {};
            }
            callbackData.gridId = gridId;

            aspect.after(this.getTable(gridId), "onSelectedRowsChanged", function() {
                var selRowsData = this.getSelectedData(gridId);
                if (selRowsData.length == 1) {
                    // react only if not an empty row was selected
                    if (selRowsData[0] !== null) {
                        if (currentUdk.writePermission) {
                            callbackData.selectedRow = selRowsData[0];
                            onSelectCallback(callbackData);
                        }
                    }
                }
            });
        }
    })();
});