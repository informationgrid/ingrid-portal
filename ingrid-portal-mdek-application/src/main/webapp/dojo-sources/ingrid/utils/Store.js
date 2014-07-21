
define([
        "dojo/_base/declare",
        "dijit/registry",
        "dojo/Deferred",
        "dojo/_base/array",
        "ingrid/utils/Grid"
    ], function(declare, registry, Deferred, array, UtilGrid){
        return declare(null, {
        
            getItemByAttribute: function(store, attr, value) {
                var def = new Deferred();
                
                var gotItems = function(items, request) {
                    array.forEach(items, function(item) {
                        if (item[attr] == value) {
                            def.resolve(item);
                            return;
                        }
                    });
                };
                store.fetch({onComplete: gotItems});
                return def;
            },

            updateReadStore: function(id, data) {
                var updatedStore = new ItemFileReadStore({
                    data: { items: data }
                });
                registry.byId(id).setStore(updatedStore);
                return updatedStore;
            },

            updateWriteStore: function(/*(name of) element that has store*/id, /*Array of items*/ data, /*StoreProperties?*/storeProps) {
                //var updatedStore = new dojo.data.ItemFileWriteStore({
                /*var updatedStore = new ingrid.data.CustomItemFileWriteStore({
                    data: { items: data },
                    structure: registry.byId(id).store.structure,
                    minRows: registry.byId(id).store.minRows,
                    interactive: registry.byId(id).store.isInteractive,
                });*/
                
                var idGrid = id;

                // set empty data if data is null or undefined
                if (!data) data = [];
                
                // if it's a SlickGrid
                if (gridManager[idGrid]) {
                    UtilGrid.setTableData(idGrid, data);

                } else {
                    var widget = registry.byId(id);
                    if (!storeProps)
                        var storeProps = {label:"label"};
                    
                    //var updatedStore = id;
                    storeProps.items = data;
                    
                    var updatedStore = new dojo.data.ItemFileWriteStore({data:storeProps});
                    if (widget.setStore) {
                        widget.setStore(updatedStore, widget.query);
                    } else {
                        widget.set("store", updatedStore);//, widget.query);
                    }
                }
                return id;
            /*
                if (!storeProps)
                    var storeProps = {label:"label"};
                
                //var updatedStore = id;
                storeProps.items = data;
                var updatedStore = new dojo.data.ItemFileWriteStore({data:storeProps});
                
                var widget = id;
                if (typeof(id) == "string") {
                    widget = registry.byId(id);
                    //if (!registry.byId(id)) console.debug("id does not exist: " + id);
                    //updatedStore = registry.byId(id).store;
                    var canEdit = widget.get("_canEdit");
                    widget.setStore(updatedStore, widget.query);
                    if (canEdit != undefined)
                        widget.set("_canEdit", canEdit);
                }
            */
                /*
                    
                if (updatedStore == null) {
                    console.debug("updatedStore is null!!! id: " + id);
                    updatedStore = new dojo.data.ItemFileWriteStore({});
                }
                // if it's writable
                if (updatedStore.save)
                    updatedStore.save();
                
                updatedStore.data = storeProps;
                updatedStore.data.items = data;
                updatedStore.close();
                    
                if (typeof(id) == "string")
                    registry.byId(id).setStore(updatedStore, registry.byId(id).query);
                else
                    updatedStore.fetch();
            */
                // resize grid to make use of autoHeight!
                //registry.byId(id).resize();
                
            //  return updatedStore;
            },

            convertItemsToJS: function(store, /*Array*/items) {
                if (items == undefined)
                    items = store._arrayOfTopLevelItems;
                var convertedItems = [];
                array.forEach(items, function(item) {
                    convertedItems.push(itemToJS(store, item));
                });
                return convertedItems;
            }

            
    })();
});