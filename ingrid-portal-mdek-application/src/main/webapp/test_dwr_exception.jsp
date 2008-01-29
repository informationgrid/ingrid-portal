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

dojo.addOnLoad(function()
{});

function buttonFunc()
{
	EntryService.getSysLists([], 0, {
		callback: function(msg) {
			dojo.debug("Callback: "+msg);
		},
		errorHandler: function(msg){
			dojo.debug("Error: "+msg);
		},
		exceptionHandler: function(msg){
			dojo.debug("Exception:");
			dojo.debugShallow(msg);
		}
	});
}
</script>


<body>
<!-- Select Widget -->
<button dojoType="Button" widgetID="myButton" onclick="buttonFunc">Go</button>
</body>
</html>
