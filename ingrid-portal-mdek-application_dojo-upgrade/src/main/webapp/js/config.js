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


// predefined global variables for java script syntax check of additional fields
var globalJSVariables  = {predef: ["dojo", "dijit", "UtilGrid"]};
var globalIDFVariables = {predef: ["importPackage", "Packages", "sourceRecord", "DatabaseSourceRecord", "IllegalArgumentException", "XPATHUtils", "igcProfileControlNode", "SQL", "XPATH", "idfDoc", "DOM"]};



