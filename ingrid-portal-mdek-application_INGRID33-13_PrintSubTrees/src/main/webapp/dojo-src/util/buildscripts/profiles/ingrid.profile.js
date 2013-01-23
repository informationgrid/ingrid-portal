dependencies = {
    layers: [{
        // This is a specially named layer, literally 'dojo.js'
        // adding dependencies to this layer will include the modules
        // in addition to the standard dojo.js base APIs. 
        name: "dijit/dijit.js",
        resourceName: "dijit.dijit",
        layerDependencies: ["string.discard"],
        dependencies: ["dijit.dijit"        /*"dijit._Widget", 
         "dojox.grid.DataGrid",
         "dojo.data.ItemFileReadStore",
         "dojo.data.ItemFileWriteStore",
         "dijit.layout.ContentPane",
         "dojox.layout.ContentPane",
         "dijit.layout.BorderContainer",
         "dijit.layout.StackContainer",
         "dijit.Toolbar"*/
        ]
    }, {
        name: "../custom/layer.js",
        resourceName: "custom.layer",
        dependencies: ["custom.layer"]
    
    }, {
        name: "../custom/layer_forms.js",
        resourceName: "custom.layer_forms",
        dependencies: ["custom.layer_forms"]
    
    }],
    
    prefixes: [
		["dijit", "../dijit"],
		["dojox", "../dojox"], 
		["ingrid", "../../js/dojo"], 
		["custom", "../custom"], 
		["css", "../../css"]
	]
}
