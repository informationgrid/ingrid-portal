/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
