var locale = "de";

if (!isRelease) {
	console.debug("This is a setup for development!");
	// for development
	var ingridJsPath = "../../js/";
	dojo.registerModulePath("ingrid", ingridJsPath+"dojo");
} else {
	// for release version
	var ingridJsPath = "../../dojo/ingrid";
	dojo.registerModulePath("ingrid", ingridJsPath);
}


// predefined global variables and options for java script syntax check of additional fields
// options see http://www.jslint.com/lint.html#options
var globalJSVariables  = {predef: ["dojo", "dijit", "UtilGrid", "console", "Validation", "UtilUI", "UtilList", "UtilUdk"], sloppy:true, white:true, eqeq:true, vars:true};
var globalIDFVariables = {predef: ["importPackage", "Packages", "sourceRecord", "DatabaseSourceRecord", "IllegalArgumentException", "XPATHUtils", "igcProfileControlNode", "SQL", "XPATH", "idfDoc", "DOM", "log"], sloppy:true, white:true, eqeq:true, vars:true};
