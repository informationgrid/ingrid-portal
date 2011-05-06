<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>SNS Update test</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script src='/ingrid-portal-mdek-application/dwr/interface/CatalogManagementService.js'></script>

<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/util.js'> </script>

<script type="text/javascript">
var djConfig = { locale: "de", isDebug: true, debugAtAllCosts: false };
</script>

<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>

<script type="text/javascript">
dojo.addOnLoad(function() {
	refreshSNSUpdateProcessInfo();
});


go = function() {
	var file = dwr.util.getValue("updateFile");

	CatalogManagementService.startSNSUpdateJob(file, {
		callback: function() {
			setTimeout("refreshSNSUpdateProcessInfo()", 1000);
		},
		errorHandler: function(errMsg, err) {
			console.log("error: " + errMsg);
		}
	});
};

stop = function() {
	CatalogManagementService.stopSNSUpdateJob( {
		callback: function() {
			console.log("Job stopped.");
		},
		errorHandler: function(errMsg, err) {
			console.log("error: " + errMsg);
		}
	});
};

refreshSNSUpdateProcessInfo = function() {
	CatalogManagementService.getSNSUpdateJobInfo( {
		callback: function(jobInfo){
			updateSNSUpdateJobInfo(jobInfo);
			if (!jobFinished(jobInfo)) {
				setTimeout("refreshSNSUpdateProcessInfo()", 1000);
			}
		},
		errorHandler: function(message, err) {
			console.log("Error: "+ message);
			// If there's a timeout try again
			setTimeout("refreshSNSUpdateProcessInfo()", 1000);
		}
	});
};

function updateSNSUpdateJobInfo(jobInfo) {
	dojo.byId("jobInfo").innerHTML = 
		"Description: " + jobInfo.description + "<br />" +
		"Datens&auml;tze: " + jobInfo.numProcessedEntities + " / " + jobInfo.numEntities + "<br />" +
		"Zeit: " + jobInfo.startTime + " - " + jobInfo.endTime + "<br />";
}

function jobFinished(jobInfo) {
	return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
}

</script>


<body>
<input type="file" id="updateFile" size="80" />
<button id="goButton" onClick="go();" dojoType="ingrid:Button">Go</button>
<button id="stopButton" onClick="stop();" dojoType="ingrid:Button">Stop</button>
<div id="jobInfo"></div>
</body>
</html>
