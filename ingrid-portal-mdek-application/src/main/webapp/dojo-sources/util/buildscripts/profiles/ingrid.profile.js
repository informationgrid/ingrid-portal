var profile = (function(){
    return {
        basePath: "../../..",
        releaseDir: "../../../../target/dojo-release/dojo-sources/release",
        releaseName: "lib",
        action: "release",
        layerOptimize: "closure",
        optimize: "closure",
        cssOptimize: "comments",
        mini: true,
        stripConsole: "warn",
        selectorEngine: "acme",
 
        defaultConfig: {
            hasCache:{
                "dojo-built": 1,
                "dojo-loader": 1,
                "dom": 1,
                "host-browser": 1,
                "config-selectorEngine": "acme"
            },
            async: 1
        },
 
        staticHasFeatures: {
            "config-deferredInstrumentation": 0,
            "config-dojo-loader-catches": 0,
            "config-tlmSiblingOfDojo": 0,
            "dojo-amd-factory-scan": 0,
            "dojo-combo-api": 0,
            "dojo-config-api": 1,
            "dojo-config-require": 0,
            "dojo-debug-messages": 0,
            "dojo-dom-ready-api": 1,
            "dojo-firebug": 0,
            "dojo-guarantee-console": 1,
            "dojo-has-api": 1,
            "dojo-inject-api": 1,
            "dojo-loader": 1,
            "dojo-log-api": 0,
            "dojo-modulePaths": 0,
            "dojo-moduleUrl": 0,
            "dojo-publish-privates": 0,
            "dojo-requirejs-api": 0,
            "dojo-sniff": 1,
            "dojo-sync-loader": 0,
            "dojo-test-sniff": 0,
            "dojo-timeout-api": 0,
            "dojo-trace-api": 0,
            "dojo-undef-api": 0,
            "dojo-v1x-i18n-Api": 1,
            "dom": 1,
            "host-browser": 1,
            "extend-dojo": 1
        },
 
        packages:[{
            name: "dojo",
            location: "dojo"
        },{
            name: "dijit",
            location: "dijit"
        },{
            name: "dojox",
            location: "dojox"
        },{
            name: "ingrid",
            location: "ingrid"
        },{
            name: "global",
            location: "../js"
        }],
 
        layers: {
            "ingrid/ingrid": {
                include: ["dojo/dojo", "dijit/layout/TabContainer", "ingrid/grid/CustomGrid", "ingrid/init"],
                //exclude: [ "ingrid/IgeActions", "ingrid/IgeEvents", "ingrid/hierarchy/rules", "ingrid/hierarchy/validation", "ingrid/hierarchy/requiredChecks" ],
                customBase: true,
                boot: true
            }/*,
            "dojo/dojo": {
                include: ["dojo/_base/declare",
                    "dojo/dom",
                    "dojo/has",
                    "dojo/_base/array",
                    "dojo/_base/lang",
                    "dojo/promise/all",
                    "dojo/Deferred",
                    "dojo/on",
                    "dojo/aspect",
                    "dojo/dom",
                    "dojo/dom-construct",
                    "dojo/_base/window",
                    "dijit/layout/StackContainer",
                    "dijit/layout/BorderContainer",
                    "dijit/layout/ContentPane",
                    "dojox/layout/ContentPane",
                    "dijit/registry"],
                customBase: true,
                boot: true
            }*/
        }
    };
})();