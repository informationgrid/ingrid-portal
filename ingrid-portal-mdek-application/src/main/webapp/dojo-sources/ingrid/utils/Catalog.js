define([
    "dojo/_base/declare",
    "dojo/Deferred",
    "ingrid/message"
], function(declare, Deferred, message) {
    return declare(null, {
        // Constants that should be used by getGenericValuesDef and setGenericValuesDef
        AUTOSAVE_INTERVAL : "AUTOSAVE_INTERVAL",
        SESSION_REFRESH_INTERVAL : "SESSION_REFRESH_INTERVAL",

        // This object holds general information about the catalog (see CatalogBean for content).
        catalogData: {},
    
        // Helper function that returns the catalog language. If no language is specified, "de" is returned
        getCatalogLanguage : function() {
            if (this.catalogData && typeof(this.catalogData.languageShort) != "undefined" && this.catalogData.languageShort != null) {
                return this.catalogData.languageShort;
            } else {
                return "de";
            }
        },

        // returns a specific entry (array with three values [displayValue, entryId, default(Y or N)])from a syslist
        getSysListEntry : function(sysListId, entryId) {
            var def = new Deferred();

            // Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
            var languageCode = this.getCatalogLanguage();
            var lstIds = [sysListId];

            CatalogService.getSysLists(lstIds, languageCode, {
                callback: function(res) {
                    var sysList = res[sysListId];
                    for (var i = 0; i < sysList.length; ++i) {
                        if (sysList[i][1] == entryId) { 
                            // Syslist entry found
                            def.resolve(sysList[i]);
                            return;
                        }
                    }

                    // Syslist entry not found
                    def.reject();
                },
                errorHandler:function(mes){
                    console.error("Error: "+mes);
                    def.reject(mes);
                }
            });

            return def;
        },

        getNameForSysList : function(sysListId) {
            var messageId = "ui.sysList." + sysListId;
            var name = message.get(messageId);

            // If the name was not found (message.get returned the key), return the default name (unknown)
            // Otherwise return the name that was found
            return (name != messageId)? name : message.get("ui.sysList.unknown"); 
        },


        // Fetch generic values from the catalog
        // Input is a list of key identifiers or null for all values: ["keyId1", "keyId2", ...]
        // Returns a map with the key/value entries: { keyId1:value1, keyId2:value2, ... }
        getGenericValuesDef : function(keyNames) {
            var def = new Deferred();

            CatalogService.getSysGenericValues(keyNames, {
                callback: function(res) {
                    var genericValueMap = {};
                    for (var index = 0; index < res.length; index++) {
                        genericValueMap[res[index].key] = res[index].value;
                    }
                    def.resolve(genericValueMap);
                },
                errorHandler:function(msg){
                    console.error("Error: "+msg);
                    def.reject(msg);
                }
            });

            return def;
        },

        // Store generic values in the backend
        // Input is a map whose key/value pairs should be stored 
        // e.g. valueMap: {keyId1:value1, keyId2:value2, ...}
        storeGenericValuesDef : function(valueMap) {
            var def = new Deferred();

            var valueList = [];
            for (var key in valueMap) {
                valueList.push( { key: key, value: valueMap[key] } );
            }

            CatalogService.storeSysGenericValues(valueList, {
                callback: function() {
                    def.resolve();
                },
                errorHandler:function(msg){
                    console.error("Error: "+msg);
                    def.reject(msg);
                }
            });

            return def;
        },

        GENERIC_VALUE_CACHE : null,

        // Returns the session refresh interval or 0 if no interval has been set
        getSessionRefreshIntervalDef : function() {
            var self = this;
            var def = this.getGenericValuesDef([this.SESSION_REFRESH_INTERVAL])
            .then(function(valueMap) {
                var refreshInterval = valueMap[self.SESSION_REFRESH_INTERVAL];
                if (refreshInterval && refreshInterval > 0) {
                    return refreshInterval;

                } else {
                    return 0;
                }
            });
            return def;
        },

        //Returns the autosave interval or 0 if no interval has been set
        getAutosaveIntervalDef : function() {
            var self = this;
            var def = this.getGenericValuesDef([this.AUTOSAVE_INTERVAL])
            .then(function(valueMap) {
                var autosaveInterval = valueMap[self.AUTOSAVE_INTERVAL];
                if (autosaveInterval && autosaveInterval > 0) {
                    return autosaveInterval;

                } else {
                    return 0;
                }
            });
            return def;
        }
            
    })();
});