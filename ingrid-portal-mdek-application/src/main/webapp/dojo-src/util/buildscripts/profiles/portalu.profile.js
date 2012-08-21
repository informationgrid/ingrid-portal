dependencies = {
    layers: [
        {
            // This is a specially named layer, literally 'dojo.js'
            // adding dependencies to this layer will include the modules
            // in addition to the standard dojo.js base APIs. 
            name: "dojo.js",
            dependencies: [
                "dijit.layout.ContentPane", 
                "dijit.layout.TabContainer",
                "dijit.layout.BorderContainer",
                "dijit.form.Button",
                "dijit.Dialog",
                "dojo.Animation"
            ]
        }
    ],

    prefixes: [
        [ "dijit", "../dijit" ],
        [ "dojox", "../dojox" ]
    ]
}