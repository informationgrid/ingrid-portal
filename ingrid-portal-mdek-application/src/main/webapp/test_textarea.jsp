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
dojo.require("dojo.widget.Form");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Textbox");
dojo.require("ingrid.widget.Form");
dojo.require("ingrid.widget.Select");
dojo.require("ingrid.widget.ValidationTextbox");

dojo.addOnLoad(function()
{
	dojo.widget.byId("generalDescBox").isValid = function(){
		return (this.getValue() != "test");
	}
});

function buttonFunc()
{
  var textareaWidget = dojo.widget.byId('generalDescArea');

  textareaWidget.setValue('Some text');

}
</script>


<body>

<!-- Textbox Widget -->
<div class="inputContainer">
	<span class="input"><input type="text" id="generalDescBox" name="generalDescBox" class="w668 h055" dojoType="ingrid:ValidationTextbox" /></span> 
</div>
<div class="inputContainer">
	<span class="input"><input type="text" mode="textarea" id="generalDescArea" name="generalDescArea" class="w668 h055" dojoType="ingrid:ValidationTextbox" /></span> 
</div>

<button dojoType="Button" widgetID="myButton" onclick="buttonFunc">Set Text</button>
</body>
</html>
