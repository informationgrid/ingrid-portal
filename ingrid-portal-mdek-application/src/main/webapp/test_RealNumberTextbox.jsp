<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<link rel="StyleSheet" href="css/main.css" type="text/css" />
<link rel="StyleSheet" href="css/recherche.css" type="text/css" />
<link rel="StyleSheet" href="css/erfassung.css" type="text/css" />

</head>

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
dojo.require("ingrid.widget.Button");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.RealNumberTextbox");

dojo.addOnLoad(function() {});
</script>


<body>

<input type="text" id="testInput" decimal="," dojoType="ingrid:RealNumberTextbox" />

</body>
</html>
