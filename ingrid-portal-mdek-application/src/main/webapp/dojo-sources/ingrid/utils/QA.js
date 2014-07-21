define([
    "dojo/_base/declare"
], function(declare) {
    return declare(null, {

        global: this,

        // Utility functions needed for workflow control
        // Returns whether workflow control is activated for the current catalog
        isQAActive: function() {
            if (this.global.catalogData
                && typeof(this.global.catalogData.workflowControl) != "undefined"
                && this.global.catalogData.workflowControl == "Y") {
                return true;
            
            } else {
                return false;
            }
        }

    })();
});