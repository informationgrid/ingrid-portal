<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Test Find topics for list</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<link rel="StyleSheet" href="css/main.css" type="text/css" />

<script type="text/javascript">
var djConfig = { isDebug: true };
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>
<script type="text/javascript" src="js/dialog.js"></script>


<script type="text/javascript">

go = function() {
	var deferred = new dojo.Deferred();

	for (var i = 0; i < 3; ++i) {

		// Use a closure where the parameter index does not change after execution
		// (fixed parameter for the longRunningFunction)
		// The parameter index is initialized with i on each iteration
		(function(index) {
			deferred.addCallback(function() { return longRunningFunction(index); });
		})(i)

		
		// This does NOT work! since the parameter i is NOT directly evaluated in the function.
		// Therefore we would end up calling longRunningFunction(3) three times, since 3 is the final
		// value for i after the loop is done.
/*
		deferred.addCallback(function() {
			return longRunningFunction(i);
		});
*/	
	}

	deferred.callback();
}

function getFunctionPointerForLongRunningFunction(param) {
	return function() {
		return longRunningFunction(param);
	}
}

function longRunningFunction(i) {
	var deferred = new dojo.Deferred();

	dialog.show("Test", "Input parameter: "+ i, dialog.INFO, [
		{ caption: "Ok", action: function() { deferred.callback(); } }
	]);

	return deferred;
}

</script>


<body>
<button id="goButton" onClick="go();" dojoType="ingrid:Button">Go</button>
</body>
</html>
