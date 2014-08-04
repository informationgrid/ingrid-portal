define([
    "dojo/_base/declare",
    "ingrid/utils/Catalog"
], function(declare, UtilCatalog) {
    return declare(null, {

        global: this,

        // Utility functions needed for workflow control
        // Returns whether workflow control is activated for the current catalog
        isQAActive: function() {
            if (UtilCatalog.catalogData
                && typeof(UtilCatalog.catalogData.workflowControl) != "undefined"
                && UtilCatalog.catalogData.workflowControl == "Y") {
                return true;
            
            } else {
                return false;
            }
        }

    })();
});