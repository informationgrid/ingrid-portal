<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script src='/ingrid-portal-mdek-application/dwr/interface/SecurityService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>

<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>


<script type="text/javascript">
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");
</script>

<script type="text/javascript">
var requestInProgress = false;
var timer = null;

function pollResult() {
	if (requestInProgress) {
		return;
	} else {
		requestInProgress = true;
	}

	dojo.debug("["+new Date()+"] - Starting query...");
	var def = getCurrentUser();
	def.addCallback(function(user) { dojo.debug("["+new Date()+"] - Current user: "+user.addressUuid); requestInProgress = false; });
	def.addErrback(function(err) { dojo.debug("["+new Date()+"] - Error:"); dojo.debugShallow(err); stopPoll(); requestInProgress = false; });
}

function getCurrentUser() {
	var def = new dojo.Deferred();

	SecurityService.getCurrentUser({
		callback: function(user) {
			def.callback(user);
		},
		errorHandler:function(mes, err){
			def.errback(err);
		}
	});
	return def;
}

function startPoll() {
	dojo.debug("["+new Date()+"] - Starting poll mechanism...");
	timer = setInterval(pollResult, 10000);
}

function stopPoll() {
	if (timer != null) {
		dojo.debug("["+new Date()+"] - Stopping poll mechanism.");
		clearInterval(timer);
		timer = null;
	}
}


</script>
</head>

<body>
	<button onclick="startPoll();">Go</button>
	<button onclick="stopPoll();">Stop</button>
</body>
</html>
