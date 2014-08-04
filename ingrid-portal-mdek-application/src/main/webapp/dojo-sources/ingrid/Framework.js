define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/on",
    "dojo/aspect",
    "dojo/topic",
    "dijit/registry",
    "ingrid/utils/UI",
    "ingrid/utils/UDK",
    "ingrid/utils/Grid"
], function(declare, lang, dom, on, aspect, topic, registry, UtilUI, UtilUdk, UtilGrid) {

    return declare(null, {

        // dijit.byId
        getWidget: function(id) {
            return registry.byId( id );
        },

        // dojo.byId
        getDom: function(id) {
            return dom.byId( id );
        },

        // dojo.trim
        trim: function(text) {
            return lang.trim( text );
        },

        // dojo.partial
        partial: function(obj, param) {
            return lang.partial( obj, param );
        },

        // dojo.connect
        connect: function(widget, eventName, func) {
            // DISTINGUISH BETWEEN ON() AND ASPECT()!!!
            // use aspect on grids always!
            if (widget.setSelectedRows === undefined) {
                on( widget, eventName, func );
                
            } else {
                aspect.after( widget, eventName, func );
            }
        },

        // dojo.subscribe
        subscribe: function(evnt, func) {
            topic.subscribe( evnt, func );
        },
        
        // dojo.publish
        // NOT USED YET
        publish: function(evnt) {
            topic.publish( evnt );
        },

        // UtilUI.setShow
        show: function(id) {
            UtilUI.setShow( id );
        },

        // UtilUI.setHide
        hide: function(id) {
            UtilUI.setHide( id );
        },

        // UtilUdk.getObjectClass
        getObjectClass: function() {
            return UtilUdk.getObjectClass();
        },
        
        // UtilUI.setOptional
        setOptional: function(id) {
            UtilUI.setOptional( id );
        },

        // UtilUI.setMandatory
        setMandatory: function(id) {
            UtilUI.setMandatory( id );
        },
        
        // UtilGrid.getTable
        getTable: function(id) {
            return UtilGrid.getTable( id );
        },

        // UtilGrid.getTableData
        getTableData: function(id) {
            return UtilGrid.getTableData( id );
        }

        // dqTablesPublishable, ref3OperationPublishable, generalAddressPublishable, spatialRefAdminUnitPublishable, timeRefTablePublishable, extraInfoConformityPublishable, availabilityAccessPublishable
        // applyRef6UrlListValidation, addServiceUrlValidation, minMaxBoundingBoxValidation, spatialRefLocationValidation, addMinMaxValidation, applyTimeRefIntervalValidation, 
        // applyRule1, applyRule2, applyRule3, applyRule5, applyRule6, applyRule7, applyRuleThesaurusInspire,
    })();

});