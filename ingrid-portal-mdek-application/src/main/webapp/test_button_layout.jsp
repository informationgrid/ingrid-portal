<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<style type="text/css">
	span.test { float : left; }
</style>

</head>

<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>

<script>
dojo.require("dojo.widget.Button");
</script>


<body>
<!-- Button Widgets -->
<span class="test">
	<button dojoType="Button" id="myButton">First Button</button>
</span>
<span class="test">
	<button dojoType="Button" widgetID="myButton2">Second Button</button>
</span>

<div style="position:relative; clear:left;"><button>Button 1</button></div>
<button>Button 2</button>

</body>
</html>
