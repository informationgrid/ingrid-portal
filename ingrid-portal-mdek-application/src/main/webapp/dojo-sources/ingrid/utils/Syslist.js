
define([
    "dojo/_base/declare",
    "dojo/Deferred",
    "dojo/_base/array",
    "ingrid/utils/Catalog"
], function(declare, Deferred, array, UtilCatalog){
        return declare(null, {
        
             // Utility functions for DOM

            // we have to clone the data, so that modification to the returned
            // value won't change the original data
            // the valuePrefix is used to add a prefix to each value of the syslist
            // this is used for the object class select box for compatibility reason
            getSyslistEntry: function(id, valuePrefix) {
                var def = new Deferred();
                if (sysLists[id].length == 0)
                    console.debug("empty syslist: " + id);
                var syslist = dojo.clone(sysLists[id]);
                // add a prefix to each value
                if (valuePrefix) {
                    array.forEach(syslist, function(item) {
                        item[1] = valuePrefix + item[1];
                    });
                }
                def.resolve(syslist);
                return def;
            },

            getSyslistEntryName: function(syslist, value) {
                if (value == undefined) return value;
                
                var result = value+"";
                
                var list = sysLists[syslist];
                if (list != undefined) {
                    array.some(list, function(item) {
                        if (item[1] == result) {
                            result = item[0];
                            return true;
                        }
                    });
                } else {
                    displayErrorMessage("SyslistID: " +  syslist + " not loaded!");
                    return null;
                }
                return result;
            },

            getSyslistEntryKey: function(syslist, value) {
                if (value == undefined) return value;
                
                var result = value+"";
                
                var list = sysLists[syslist];
                if (list != undefined) {
                    array.some(list, function(item) {
                        if (item[0] == result) {
                            result = item[1];
                            return true;
                        }
                    });
                } else {
                    displayErrorMessage("SyslistID: " +  syslist + " not loaded!");
                    return null;
                }
                return result;
            },

            getSyslistEntryData: function(syslist, value) {
                if (value == undefined) return null;
                
                var result = value+"";
                
                var list = sysLists[syslist];
                if (list != undefined) {
                    array.some(list, function(item) {
                        if (item[0] == result) {
                            result = item[3];
                            return true;
                        }
                    });
                } else {
                    displayErrorMessage("SyslistID: " +  syslist + " not loaded!");
                    return null;
                }
                return result;
            },

            readSysListData: function(listId) {
                var def = new Deferred();
                var languageCode = UtilCatalog.getCatalogLanguage();
                CatalogService.getSysLists([listId], languageCode, {
                    callback: function(res) {
                        def.resolve(res[listId]);
                    }
                });
                return def.promise;
            },

            // Input is a list of string arrays of the following form:
            // [ [listEntry, entryId, isDefault], [...] ]
            // e.g [ ["Inch", "4", "N"], ["Meter", "9001", "N"], ["Fuss", "9002", "N"], ["Kilometer", "9036", "N"] ]
            // Output is [ {name:listItems[0][0], entryId: listItems[0][1], isDefault: listItems[0][2] == "Y"}, ...
            convertSysListToTableData: function(listItems) {
                // Prepare the list items for display
                var listData = [];
                for (var index = 0; index < listItems.length; ++index) {
                    listData.push({
                        name: listItems[index][0],
                        entryId: listItems[index][1],
                        isDefault: listItems[index][2] == "Y",
                        data: listItems[index][3]
                    });
                }
                 
                return listData;
            }

            
        })();
    }
);