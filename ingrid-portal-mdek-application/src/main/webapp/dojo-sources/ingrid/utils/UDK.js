// Utility functions for handling misc data from the frontend

define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dijit/registry",
    "dojo/Deferred",
    "ingrid/utils/Address",
    "ingrid/utils/Catalog"
], function(declare, array, registry, Deferred, UtilAddress, UtilCatalog){
        return declare(null, {
        

            isObjectSelected: function() {
                var treeWidget = registry.byId("tree");

                if (typeof(treeWidget) == "undefined") {
                    return true;
                }

                var node = treeWidget.selectedNode;
                if (typeof(node) != "undefined" && node != null) {
                    return (node.nodeAppType == "O");
                }
            },

            //check whether INSPIRE terms are in thesaurusInspireTermsList (from nodeData)  
            isInspire: function(thesaurusInspireTermsList) {
                if (array.some(thesaurusInspireTermsList, function(iTermKey) {
                   return (iTermKey != 99999); })) {
                   return true;
                }   
                return false;
            },

            //returns "1", "2" ...
            getCurrentObjectClass: function() {
                var objectClassWidget = registry.byId("objectClass");
                if (typeof(objectClassWidget) == "undefined") {
                    return 0;
                }

                return objectClassWidget.getValue().substring(5);
            },

            // returns "Class1", "Class2" ...
            getObjectClass: function() {   
                var objectClassWidget = registry.byId("objectClass");
                if (typeof(objectClassWidget) == "undefined") {
                    return 0;
                }

                return objectClassWidget.getValue();
            },

            getCurrentAddressClass: function() {   
                return UtilAddress.getAddressClass();   
            },

            // Load a helpMessage for the specified guiId from the server.
            // Returns a dojo deferred. The callback is invoked with a HelpMessage object
            loadHelpMessage: function(guiId) {
                var deferred = new Deferred();
                // Load the help message for 'guiId' from the backend.
                // First we need the current object/address class
                var cls = this.isObjectSelected() ? this.getCurrentObjectClass() : this.getCurrentAddressClass(); 

                // Then load the help message via HelpService
                // Use current locale as target language (e.g. maybe switched to english)
                // Use catalog language as default language (e.g. current locale is not de/en, maybe different one from browser)
                var helpLanguage = userLocale;
                var defaultLanguage = "en";
                if (UtilCatalog.catalogData && UtilCatalog.catalogData.languageShort) {
                    defaultLanguage = UtilCatalog.catalogData.languageShort;
                }
                HelpService.getHelpEntry(guiId, cls, helpLanguage, defaultLanguage, {
                    callback: function(helpEntry) {
                        if (helpEntry) {
                            deferred.resolve(helpEntry);
                        } else {
                            deferred.errback();
                        }
                    },
                    errorHandler: function(errMsg, err) {
                        deferred.errback(new Error(errMsg));
                    }
                });

                return deferred;
            }

    })();
});