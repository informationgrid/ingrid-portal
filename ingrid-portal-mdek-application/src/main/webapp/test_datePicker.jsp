<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script src='/ingrid-portal-mdek-application/dwr/interface/EntryService.js'></script>
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
dojo.require("dojo.widget.Button");
dojo.require("ingrid.widget.DropdownDatePicker");

dojo.addOnLoad(function() {
});

function clear() {
	var dp = dojo.widget.byId("datePicker");
	dp.valueNode.value = "";
	dp.datePicker.value = "";
	dp._updateText();
}

function setValue() {
	var dp = dojo.widget.byId("datePicker");
	dp.setValue("");
}

function setDate() {
	var dp = dojo.widget.byId("datePicker");
	dp.setDate(null);
}

function getValue() {
	var dp = dojo.widget.byId("datePicker");
	dojo.debug(dp.getValue());
}

function getDate() {
	var dp = dojo.widget.byId("datePicker");
	dojo.debug(dp.getDate());
}

</script>


<body>

<div dojoType="ingrid:DropdownDatePicker" id="datePicker"  displayFormat="dd.MM.yyyy"></div>

<button dojoType="Button" onclick="clear">Clear</button>
<button dojoType="Button" onclick="setValue">Set Value</button>
<button dojoType="Button" onclick="setDate">Set Date</button>
<button dojoType="Button" onclick="getValue">Get Value</button>
<button dojoType="Button" onclick="getDate">Get Date</button>

</body>
</html>
