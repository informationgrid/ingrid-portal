<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script src='/ingrid-portal-mdek-application/dwr/interface/HttpService.js'></script>
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
dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.widget.Button");
</script>

<script type="text/javascript">
function getHtmlBody() {
	var url = dojo.string.trim(dojo.widget.byId("url").getValue());
	var getBodyDef = getBody(url);

	getBodyDef.addCallback(function(rawBody) {
		dojo.byId("htmlBody").innerHTML = removeEvilTags(rawBody);
	});
	getBodyDef.addErrback(function(err) {
		dojo.byId("htmlBody").innerHTML = err;
	});
}

function removeEvilTags(val) {
	if (val) {
		return val.replace(/<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/gi, '');
	} else {
		return "";
	}
}

function getBody(url) {
	var def = new dojo.Deferred();

	HttpService.getHtmlBody(url, {
		callback: function(rawBody) { def.callback(rawBody); },
		errorHandler: function(message, err) {
			def.errback(new Error(message));
		}
	});
	return def;
}

</script>
</head>

<body>
<input type="text" maxlength="255" id="url" dojoType="dojo:ValidationTextBox" />

<button dojoType="Button" onclick="javascript:getHtmlBody();">Fetch HTML Body</button>

<div id="htmlBody">
</div>

</body>
</html>
