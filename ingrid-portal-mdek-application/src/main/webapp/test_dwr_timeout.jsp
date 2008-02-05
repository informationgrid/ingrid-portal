<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script src='/ingrid-portal-mdek-application/dwr/interface/SNSService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>


<script type="text/javascript">

var requestInProgress = false;

setInterval(pollResult, 1000);

function output(str) {
	document.getElementById("text").innerHTML += str+"<br>";
}

function pollResult() {
	if (requestInProgress) {
		return;
	} else {
		requestInProgress = true;
	}

	output("Starting query.");
	SNSService.findTopics("Wasser", {
		callback: processResults,
		timeout: 5000,
		errorHandler: function() {output("timeout"); requestInProgress = false; }
	});
}

function processResults(res) {
	output("Processing results.");
	requestInProgress = false;
}

function buttonFunc() {
	output("setInterval()");
	setInterval(pollResult, 1000);
}
</script>


<body>
<!-- Select Widget -->
<button onclick="buttonFunc();">Go</button>
<div id=text> </div>
</body>
</html>
