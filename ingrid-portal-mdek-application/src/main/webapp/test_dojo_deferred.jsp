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
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>


<script>

dojo.addOnLoad(function()
{
  init();
});


function init() {}

function findTopic(topic){
	dojo.debug("findTopic("+topic+")");
	var def = new dojo.Deferred();
	SNSService.findTopic(topic,
	{
		callback: function(result) {
			dojo.debug("findTopic("+topic+") -> "+result);
			def.callback(result);
		},
		errorHandler: function(result) {
			dojo.debug("findTopic("+topic+") -> (Error) "+result);
			def.errback(result);
		},	
		timeout: 10000
	});
	return def;
}


function testDeferred() {
	var deferred = new dojo.Deferred();

	dojo.debug("Attaching callback funcs.");

	deferred.addCallback(function() {
		dojo.debug("findTopic(Wasser)");
		return findTopic("Wasser");
	});
	deferred.addCallback(function() {
		dojo.debug("findTopic(Auto)");
		return findTopic("Auto");
	});
	deferred.addCallback(function() {
		dojo.debug("findTopic(test)");
		return findTopic("test");
	});

	deferred.addErrback(function(val) {
		dojo.debug("errback: "+val);
		return;
	});

	dojo.debug("Firing callback...");
	deferred.callback();
	dojo.debug("Callback fired.");
}

function testDeferredList() {
	var d1 = findTopic("Auto");
	var d2 = findTopic("Test");
	var d3 = findTopic("Wurstwasser");
//	var d4 = findTopic("Hallo sagen!?!");
	var d4 = new dojo.Deferred();
	var d5 = findTopic("Bla bla noch ein Anfrage");

	var l1 = new dojo.DeferredList([d1, d2, d3, d4, d5], false, false, true);
	l1.addCallback(function (resultList) {
		for(var i=0;i<resultList.length;i++) {
			dojo.debug(i+':'+resultList[i]);
		}
	});

	setTimeout(function() {d4.errback();}, 10000);
}

</script>

<body>

<button dojoType="Button" onClick="testDeferred">Test Deferred</button>
<button dojoType="Button" onClick="testDeferredList">Test DeferredList</button>
</body>
</html>
