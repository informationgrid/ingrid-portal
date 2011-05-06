<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script
	src='/ingrid-portal-mdek-application/dwr/interface/SNSService.js'></script>
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


function testCallback() {
	var testString = 'test';

	dojo.debug('---- DEFERRED CALLBACK TEST ----');
	var deferred = new dojo.Deferred();

	deferred.addCallback(function(result) {
		dojo.debug(result);
		dojo.debug(testString);
	});
	deferred.callback('argument');
	dojo.debug('---- DEFERRED CALLBACK TEST END ----');

/*
	dojo.debug('---- DWR CALLBACK TEST ----');
	SNSService.findTopic(dojo.widget.byId('textBox').textbox.value, function(result) {
		dojo.debug('ID: '+result.topicId);
		dojo.debug(testString);
	});
	dojo.debug('---- DWR CALLBACK TEST END ----');
*/

	dojo.debug('---- DOJO CONNECT TEST ----');
	
	var myObj = {
		functionOne: function() {
			dojo.debug('Function One');
		},

		functionTwo: function(argOne) {
			dojo.debug('Function Two');
			dojo.debug('arg: '+argOne);
		}
	}

	var d = new dojo.Deferred();
	d.addCallback(function(res) {
		dojo.event.disconnect('after', myObj, 'functionOne', d, 'callback');
		dojo.debug('Deferred obj called.');
		myObj.functionTwo(testString);
	});

	// Event gets disconnected in the deferred method
	dojo.event.connectOnce('after', myObj, 'functionOne', d, 'callback');

	myObj.functionOne();

	myObj.functionOne();

	dojo.debug('---- DOJO CONNECT TEST END ----');
}


</script>

<body>

<div dojoType="Textbox" id="textBox" value="Wasser" type="text"></div>
<button dojoType="Button" onClick="testCallback">Go</button>
</body>
</html>
