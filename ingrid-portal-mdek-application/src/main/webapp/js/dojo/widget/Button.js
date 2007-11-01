dojo.provide("ingrid.widget.Button");

dojo.require("dojo.widget.Button");

/*
 * Pushbutton
 */
dojo.widget.defineWidget(
	"ingrid.widget.Button",
	dojo.widget.Button,
	{
		templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/ButtonTemplate.css"),
		templatePath: dojo.uri.moduleUri("ingrid", "widget/templates/ButtonTemplate.html"),
		
		inactiveImg: "",
		activeImg: "",
		pressedImg: "",
		disabledImg: "",

		_setImage: function(/*String*/ prefix){}
});
