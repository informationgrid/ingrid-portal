var locale = "de", ingridJsPath, dojoConfig;

if (!this.isRelease) {
    console.debug("This is a setup for development!");
    // for development
    ingridJsPath = "../../js/";
    //dojo.registerModulePath("ingrid", ingridJsPath+"dojo");
    dojoConfig = {
        async: true,
        isDebug: true,
        parseOnLoad: false,
        selectorEngine: "acme",
        useDeferredInstrumentation: true,
        locale: this.userLocale,
        packages: [
            //{ name: "ingrid", location: ingridJsPath+"dojo" }
            { name: "dwr", location: "../../dwr" }
            // { name: "global", location: "../../js" }
        ]
    };
} else {
    // for release version
    // var ingridJsPath = "../../dojo/ingrid";
    //dojo.registerModulePath("ingrid", ingridJsPath);
    ingridJsPath = "../../js/";
    //dojo.registerModulePath("ingrid", ingridJsPath+"dojo");
    dojoConfig = {
        async: true,
        isDebug: false,
        parseOnLoad: false,
        selectorEngine: "acme",
        useDeferredInstrumentation: false,
        locale: this.userLocale,
        packages: [
            //{ name: "ingrid", location: ingridJsPath+"dojo" }
            // { name: "dwr", location: "../../../../dwr" },
            // { name: "global", location: "dojo-sources/app/lib/global" },
            { name: "ingrid", location: "dojo-sources/release/lib/ingrid" },
            { name: "dojo", location: "dojo-sources/release/lib/dojo" },
            { name: "dijit", location: "dojo-sources/release/lib/dijit" },
            { name: "dojox", location: "dojo-sources/release/lib/dojox" }
        ]
    };
}


// predefined global variables and options for java script syntax check of additional fields
// options see http://www.jslint.com/lint.html#options
var globalJSVariables  = {predef: ["require", "currentUdk", "framework"], sloppy:true, white:true, eqeq:true, vars:true};
var globalIDFVariables = {predef: ["importPackage", "Packages", "sourceRecord", "DatabaseSourceRecord", "IllegalArgumentException", "XPATHUtils", "igcProfileControlNode", "SQL", "XPATH", "idfDoc", "DOM", "log"], sloppy:true, white:true, eqeq:true, vars:true};
