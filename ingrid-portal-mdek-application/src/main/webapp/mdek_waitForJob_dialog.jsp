<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
var timer;
var requestInProgress;
var cancelJob;

_container_.addOnLoad(function() {
	dojo.require("dojo.lang.timing.Timer");

	requestInProgress = false;
	cancelJob = false;
	timer = new dojo.lang.timing.Timer(1000);	// Timer that generates 'onTick' every second

	timer.onTick = function() {
		if (requestInProgress)
			return;
		else if (cancelJob) {
			this.stop();
			requestCancelJob();
		} else {
			requestInProgress = true;
			requestJobInfo();
		}
	}

	timer.start();
});

_container_.addOnUnload(function() {
	// Stop the timer and destroy it
	timer.stop();
	delete timer;

	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (_container_.customParams.resultHandler.fired == -1) {
		requestCancelJob();
	}
});

function requestCancelJob() {
	dojo.debug("cancelling job...");
	BackendService.cancelRunningJob({
			callback: function() {
				dojo.debug("job cancelled.");
				_container_.customParams.resultHandler.callback("JOB_CANCELLED");
				_container_.closeWindow();
			},
			timeout:10000,
			errorHandler: function(err) {
				dojo.debug("error cancelling job.");
				requestInProgress = false; handleError(err);
			}
	});
}

function requestJobInfo() {
	BackendService.getRunningJobInfo({
			callback: handleUpdatedJobInfo,
			timeout:10000,
			errorHandler: function(err) { requestInProgress = false; handleError(err); }
	});
}


function handleUpdatedJobInfo(jobInfo) {
	if (jobInfo.description == null) {
		// job done, close the dialog and call the result handler
		_container_.customParams.resultHandler.callback();
		_container_.closeWindow();
	} else {
		updateProgress(jobInfo);
	}
}

function updateProgress(jobInfo) {
	var messageDiv = dojo.byId("messageDiv");
	messageDiv.innerHTML = message.get("dialog.waitForJob.opName") + " "+jobInfo.description+"<br>";
	messageDiv.innerHTML += message.get("dialog.waitForJob.objCount") + " "+jobInfo.numProcessedEntities+"<br>";
	messageDiv.innerHTML += message.get("dialog.waitForJob.numObjects") + " "+jobInfo.numEntities;

	var progressBar = dojo.widget.byId("progressBar");
	progressBar.setMaxProgressValue(jobInfo.numEntities);
	progressBar.setProgressValue(jobInfo.numProcessedEntities);

	// The progress was updated. Reset the 'requestInProgress' flag so we can start another request
	requestInProgress = false;
}


function handleError(err) {
	// Signal an error and close the dialog
	dojo.debug("Error while retrieving job info: "+err);
	_container_.customParams.resultHandler.errback(err);
	_container_.closeWindow();
}

cancelButtonFunc = function() {
	cancelJob = true;
}

</script>
</head>



<body>
<div dojoType="ContentPane">
	<div id="contentPane" layoutAlign="client" class="contentBlockWhite top half">
		<div id="dialogContent" class="content">
			<div id="messageDiv" class="field grey"></div>

			<div dojoType="ProgressBar" id="progressBar" width="310" height="10" class="field grey" />

	  	</div>
        <span style="float:right;"><button dojoType="ingrid:Button" title="Abbrechen" onClick="cancelButtonFunc"><fmt:message key="dialog.waitForJob.cancel" /></button></span>
	</div>
</div>
</body>
</html>
