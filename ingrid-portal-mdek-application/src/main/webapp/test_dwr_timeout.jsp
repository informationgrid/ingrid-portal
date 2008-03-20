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
var timer = setInterval(pollResult, 1000);

dwr.engine.setPreHook(showDWRLoading);
dwr.engine.setPostHook(hideDWRLoading);

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
		preHook: showDWRFunctionLoading,
		postHook: hideDWRFunctionLoading,
		callback: processResults,
		timeout: 5000,
		errorHandler: function() {clearInterval(timer); output("timeout"); requestInProgress = false; hideDWRFunctionLoading();}
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

function showDWRFunctionLoading() {
	document.getElementById("DWRFuncLoading").innerHTML = "function loading...";
}
function hideDWRFunctionLoading() {
	document.getElementById("DWRFuncLoading").innerHTML = "";
}

function showDWRLoading() {
	document.getElementById("DWRLoading").innerHTML = "dwr loading...";
}
function hideDWRLoading() {
	document.getElementById("DWRLoading").innerHTML = "";
}

</script>


<body>
<!-- Select Widget -->
<button onclick="buttonFunc();">Go</button>
<div id="DWRLoading"> </div>
<div id="DWRFuncLoading"> </div>
<div id="text"> </div>
</body>
</html>
