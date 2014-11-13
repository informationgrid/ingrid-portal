<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script src='dwr/interface/BackendService.js'></script>

<script type="text/javascript">
dojo.require("dijit.ProgressBar");
var canceled;
var requestInProgress;
var cancelJob;

on(_container_, "onLoad", function() {
	//dojo.require("dojo.lang.timing.Timer");

	requestInProgress = false;
	cancelJob = false;
    canceled = false;

    setTimeout(requestJobInfo, 3000);
});

on(_container_, "onUnload", function() {
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (this.customParams.resultHandler.fired == -1) {
		requestCancelJob(this.customParams.resultHandler);
	}
});

function requestCancelJob(resultHandler) {
	console.debug("cancelling job...");
	BackendService.cancelRunningJob({
			callback: function() {
				console.debug("job cancelled.");
                canceled = true;
				resultHandler.resolve("JOB_CANCELLED");
			},
			timeout:10000,
			errorHandler: function(err) {
				console.debug("error cancelling job.");
				requestInProgress = false; handleError(err);
			}
	});
}

function requestJobInfo() {
	BackendService.getRunningJobInfo({
			callback: handleUpdatedJobInfo,
			timeout:30000,
			errorHandler: function(err) { requestInProgress = false; handleError(err); }
	});
}


function handleUpdatedJobInfo(jobInfo) {
	if (jobInfo.description == null) {
		// job done, close the dialog and call the result handler
        if (!canceled) {
            _container_.customParams.resultHandler.resolve();
            _container_.hide();
        }
	} else {
		updateProgress(jobInfo);
        if (!canceled)
            setTimeout(requestJobInfo, 3000);
	}
}

function updateProgress(jobInfo) {
	var messageDiv = dom.byId("messageDiv");
	messageDiv.innerHTML = "<fmt:message key='dialog.waitForJob.opName' />" + " "+jobInfo.description+"<br>";
	messageDiv.innerHTML += "<fmt:message key='dialog.waitForJob.objCount' />" + " "+jobInfo.numProcessedEntities+"<br>";
	messageDiv.innerHTML += "<fmt:message key='dialog.waitForJob.numObjects' />" + " "+jobInfo.numEntities;

	var progressBar = registry.byId("progressBar");
	progressBar.set("maximum", jobInfo.numEntities);
	progressBar.set("value", jobInfo.numProcessedEntities);

	// The progress was updated. Reset the 'requestInProgress' flag so we can start another request
	requestInProgress = false;
}


function handleError(err) {
	// Signal an error and close the dialog
	console.debug("Error while retrieving job info: "+err);
	_container_.customParams.resultHandler.reject(err);
	_container_.hide();
}

cancelButtonFunc = function() {
	requestCancelJob(_container_.customParams.resultHandler);
    _container_.hide();
}

</script>
</head>



<body>
	<div id="contentPane" layoutAlign="client" class="">
		<div id="dialogContent" class="content">
			<div id="messageDiv" class="field grey"></div>

			<div data-dojo-type="dijit/ProgressBar" id="progressBar" width="310" height="10" class="field grey" /></div>

	  	</div>
        <span style="float:right;"><button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.waitForJob.cancel" />" onclick="cancelButtonFunc"><fmt:message key="dialog.waitForJob.cancel" /></button></span>
	</div>
</body>
</html>
