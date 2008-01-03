<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>

<script>
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.Select");
dojo.require("ingrid.widget.Select");

dojo.addOnLoad(function()
{
});

function buttonFunc()
{
	var selectWidget = dojo.widget.byId('select');
	selectWidget.setValue('Class1');
}
</script>


<body>
<!-- Select Widget -->
<div class="inputContainer">
  <select dojoType="ingrid:Select" autoComplete="false" style="width:386px;" id="select" name="select" dataUrl="js/data/objectclasses.js">
  </select>
</div>

<button dojoType="Button" widgetID="myButton" onclick="buttonFunc">Set Value</button>

</body>
</html>
