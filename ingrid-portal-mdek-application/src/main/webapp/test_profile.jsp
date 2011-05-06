<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>

<script>
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Form");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.Textbox");


var obj = null;

function testDojoWidgetById() {
	for (var i = 0; i < 100; ++i) {
		var widget = dojo.widget.byId("widget"+i);
	}
}

function testSetValue() {
	if (this.obj != null) {
		dojo.widget.byId("textBoxes").setValues(this.obj);
	} else {		
		for (var i = 0; i < 100; ++i) {
			var widget = dojo.widget.byId("widget"+i);
			widget.setDate(new Date(i));
		}
	}
}

function testGetValue() {
/*
	for (var i = 0; i < 100; ++i) {
		var widget = dojo.widget.byId("widget"+i);
		var value = widget.getValue();
	}
*/

	this.obj = dojo.widget.byId("textBoxes").getValues();
	dojo.debugShallow(this.obj);
}


function createWidgets() {
	var textBoxDiv = dojo.byId("textBoxes");

	for (var i = 0; i < 100; ++i) {
		var newWidget = dojo.widget.createWidget("dropdowndatepicker", {id:"widget"+i, name:"widget"+i});
		textBoxDiv.appendChild(newWidget.domNode);
	}
}

</script>


<body>
<!-- Select Widget -->
<button dojoType="Button" onclick="createWidgets">Create Widgets</button>
<button dojoType="Button" onclick="testDojoWidgetById">Test dojo.widget.byId()</button>
<button dojoType="Button" onclick="testGetValue">Test Get Value</button>
<button dojoType="Button" onclick="testSetValue">Test Set Value</button>

<form dojoType="Form" id="textBoxes">
</form>

</body>
</html>
