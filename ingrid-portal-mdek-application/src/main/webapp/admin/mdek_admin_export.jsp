<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
dojo.require("dijit.ProgressBar");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.ValidationTextBox");

var scriptScopeExport = _container_;
var currentSelectedNode = null;

// Callback for the dwr calls to ExportService.export*
// Wait for three seconds for the call to return and refresh the process info if it does
// If we run into a timeout the job is still running and we simply refresh the process info
// If we detect that the user has running jobs, display an error message that the job could not be started
var exportServiceCallback = {
	timeout: 5000,
	preHook: showLoadingZone,
	postHook: hideLoadingZone,
	callback: function(res){
		refreshExportProcessInfo();
	},
	errorHandler: function(msg, err) {
		hideLoadingZone();
		// We expect a timeout error
		if (msg == "Timeout") {
			refreshExportProcessInfo();
		} else {
			if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
				dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
			}
		}
	}
}


var storeProps = {data: {identifier: '1',label: '0'}};
createComboBox("exportXMLCriteria", null, dojo.clone(storeProps), function(){
    return UtilSyslist.getSyslistEntry(1370);
});

dojo.connect(_container_, "onLoad", function(){
	refreshExportProcessInfo();
});

function startExportCriteria() {
	var exportCriteria = dijit.byId("exportXMLCriteria").get("value");
	console.debug("exportCriteria: "+ exportCriteria);
	if (exportCriteria) {
		exportObjectsWithCriteria(exportCriteria);

	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.export.selectExportCriteriaError' />", dialog.WARNING);
	}
}

function startExportPartial() {
	if (currentSelectedNode) {
		var exportChildren = !dijit.byId("exportTreeSelectionOnly").checked;

		if (currentSelectedNode.nodeAppType == "O") {
			startObjectExport(currentSelectedNode.uuid, exportChildren);
		} else {
			startAddressExport(currentSelectedNode.uuid, exportChildren);
		}
	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.export.selectNodeError' />", dialog.WARNING);
	}
}

function startObjectExport(uuid, exportChildren) {
	if (uuid == "objectRoot") {
		exportObjectBranch(null, exportChildren);

	} else {
		exportObjectBranch(uuid, exportChildren);
	}
}

function exportObjectBranch(uuid, exportChildren) {
	ExportService.exportObjectBranch(uuid, exportChildren, exportServiceCallback);
}

function exportObjectsWithCriteria(exportCriteria) {
	ExportService.exportObjectsWithCriteria(exportCriteria, exportServiceCallback);
}

function startAddressExport(uuid, exportChildren) {
	if (uuid == "addressRoot") {
		exportTopAddresses(exportChildren);

	} else if (uuid == "addressFreeRoot") {
		exportFreeAddresses();

	} else {
		exportAddressBranch(uuid, exportChildren);
	}
}

function exportAddressBranch(uuid, exportChildren) {
	ExportService.exportAddressBranch(uuid, exportChildren, exportServiceCallback);
}

function exportTopAddresses(exportChildren) {
	ExportService.exportTopAddresses(exportChildren, exportServiceCallback);
}

function exportFreeAddresses() {
	ExportService.exportFreeAddresses(exportServiceCallback);
}

// Button function for 'start export'. Send a request to the backend to start a new export job
scriptScopeExport.startExport = function() {
	if (dojo.byId("exportType1").checked) {
		startExportCriteria();

	} else if (dojo.byId("exportType2").checked) {
		startExportPartial();

	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.export.selectExportTypeError' />", dialog.WARNING);
		console.debug("No export type selected.");
	}
}

// Button function for the 'select dataset' link.
scriptScopeExport.selectDataset = function() {
	var deferred = new dojo.Deferred();

	deferred.addCallback(function(selectedDataset) {
		dijit.byId("exportTreeName").setValue(selectedDataset.title);
		currentSelectedNode = selectedDataset;
		dojo.byId("exportType2").checked = true;
	});

	dialog.showPage("<fmt:message key='dialog.admin.export.selectNode' />", 'admin/dialogs/mdek_admin_export_select_dataset.jsp?c='+userLocale, 522, 525, true, {
		// custom parameters
		resultHandler: deferred	
	});
}

// Reload the process info for the last run from the backend
refreshExportProcessInfo = function() {
	ExportService.getExportInfo(false, {
		callback: function(exportInfo){
			updateExportInfo(exportInfo);
			if (!jobFinished(exportInfo)) {
				setTimeout("refreshExportProcessInfo()", 2000);
			}
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+ message);
			// If there's a timeout try again
			if (err.message != "USER_LOGIN_ERROR") {
			    setTimeout("refreshExportProcessInfo()", 2000);
			}
		}
	});
}

function updateExportInfo(exportInfo) {
	if (exportInfo.entityType == "OBJECT") {
		dojo.byId("exportInfoNumExportedEntitiesContainer").innerHTML = "<fmt:message key='dialog.admin.export.numObjects' />";
	} else if (exportInfo.entityType == "ADDRESS") {
		dojo.byId("exportInfoNumExportedEntitiesContainer").innerHTML = "<fmt:message key='dialog.admin.export.numAddresses' />";
	} else {
		dojo.byId("exportInfoNumExportedEntitiesContainer").innerHTML = "<fmt:message key='dialog.admin.export.numDatasets' />";
	}

	if (jobFinished(exportInfo)) {
		dojo.byId("exportInfoTitle").innerHTML = "<fmt:message key='dialog.admin.export.lastProcessInfo' />";
		dojo.style("exportProgressBarContainer", "display", "none");
		dojo.style("cancelExportProcessButton", "display", "none");

		if (exportInfo.exception) {
			dojo.style("exportExceptionMessage", "display", "block");
			dojo.style("exportInfoDownload", "display", "none");
			dojo.style("exportInfoEndDateContainer", "display", "none");
			dojo.style("exportInfoNumExportedEntitiesContainer", "display", "none");
			dojo.byId("exportInfoNumExportedEntities").innerHTML = "";

		} else if (exportInfo.endTime) {
			//dojo.html.hide(dojo.byId("exportExceptionMessage"));
			dojo.style("exportExceptionMessage", "display", "none");
			if (exportInfo.numProcessedEntities > 0)
				dojo.style("exportInfoDownload", "display", "block");
			else
				dojo.style("exportInfoDownload", "display", "none");
			dojo.style("exportInfoEndDateContainer", "display", "block");
			dojo.style("exportInfoNumExportedEntitiesContainer", "display", "block");
			dojo.byId("exportInfoNumExportedEntities").innerHTML = exportInfo.numProcessedEntities;

		} else {
			// No job has been started yet
			dojo.style("exportExceptionMessage", "display", "none");
			dojo.style("exportInfoDownload", "display", "none");
			dojo.style("exportInfoEndDateContainer", "display", "none");
			dojo.style("exportInfoNumExportedEntitiesContainer", "display", "none");
            console.debug("set: none");
			dojo.byId("exportInfoNumExportedEntities").innerHTML = "";
		}
	} else {
		dojo.byId("exportInfoTitle").innerHTML = "<fmt:message key='dialog.admin.export.currentProcessInfo' />";
		dojo.style("exportExceptionMessage", "display", "none");
		dojo.style("exportInfoDownload", "display", "none");
		dojo.style("exportInfoEndDateContainer", "display", "none");
		dojo.style("cancelExportProcessButton", "display", "block");
		dojo.style("exportInfoNumExportedEntitiesContainer", "display", "block");
		dojo.style("exportProgressBarContainer", "display", "block");
		dojo.byId("exportInfoNumExportedEntities").innerHTML = exportInfo.numProcessedEntities + " / " + exportInfo.numEntities;

		var progressBar = dijit.byId("exportProgressBar");
        progressBar.update({
            maximum: exportInfo.numEntities,
            progress: exportInfo.numProcessedEntities
        });
	}
    
    //console.debug(exportInfo);

	if (exportInfo.startTime) {
		dojo.byId("exportInfoBeginDate").innerHTML = UtilString.getDateString(exportInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	} else {
		dojo.byId("exportInfoBeginDate").innerHTML = "";
	}

	if (exportInfo.endTime) {
		dojo.byId("exportInfoEndDate").innerHTML = UtilString.getDateString(exportInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	} else {
		dojo.byId("exportInfoEndDate").innerHTML = "";
	}
}

function jobFinished(exportInfo) {
	// endTime != null -> job has an end time means it's done
	// exception != null -> job has an exception -> done
	// startTime == null -> no start time means something went wrong (no previous job exists?)
	return (exportInfo.endTime != null || exportInfo.exception != null || exportInfo.startTime == null);
}


scriptScopeExport.downloadLastExport = function() {
	ExportService.getLastExportFile({
		callback: function(exportFile) {
			console.debug(exportFile);
			dwr.engine.openInDownload(exportFile);
		},
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
		}		
	});
}

scriptScopeExport.cancelExport = function() {
	ExportService.cancelRunningJob({
		callback: function() {
			// do nothing
		},
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
		}		
	});
}

scriptScopeExport.showJobException = function() {
	ExportService.getExportInfo(false, {
		callback: function(exportInfo){
	    	dialog.show("<fmt:message key='general.error' />", UtilGeneral.getStackTrace(exportInfo.exception), dialog.INFO, null, 800, 600);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+ message);
		}
	});
}

function showLoadingZone() {
    dojo.byId("exportLoadingZone").style.visibility = "visible";
}

function hideLoadingZone() {
    dojo.byId("exportLoadingZone").style.visibility = "hidden";
}

</script>
</head>

<body>

	<!-- CONTENT START -->

		<div class="contentBlockWhite">
			<div class="content">
			    <div class="inputContainer">
			    	<div id="winNavi" style="top:0px;">
			        	<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=import-export-1#import-export-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
			        </div>
                    <div class="spacer"></div>
           			<div id="exportProcessInfo" class="infobox">
                        <span class="icon"><img src="img/ic_info_download.gif" width="16" height="16" alt="Info" /></span>
                        <span id="exportInfoTitle" class="title"></span>
                        <div id="exportProcessInfoContent">
                            <p id="exportInfoDownload"><fmt:message key="dialog.admin.export.result" /> <a href="javascript:void(0);" onclick="javascript:scriptScopeExport.downloadLastExport();" title="<fmt:message key="dialog.admin.export.log" />"><fmt:message key="general.link" /></a></p>
                            <p id="exportExceptionMessage"><fmt:message key="dialog.admin.export.error" /> <a href="javascript:void(0);" onclick="javascript:scriptScopeExport.showJobException();" title="<fmt:message key="dialog.admin.export.errorinfo" />"><fmt:message key="general.link" /></a></p>
                            <table cellspacing="0">
                                <tr>
                                    <td><fmt:message key="dialog.admin.export.startTime" /></td>
                                    <td id="exportInfoBeginDate"></td></tr>
                                    <tr><td id="exportInfoEndDateContainer"><fmt:message key="dialog.admin.export.endTime" /></td>
                                    <td id="exportInfoEndDate"></td></tr>
                                    <tr><td id="exportInfoNumExportedEntitiesContainer"></td>
                                    <td id="exportInfoNumExportedEntities"></td></tr>
                                    <tr><td id="exportProgressBarContainer" colspan=2><div dojoType="dijit.ProgressBar" id="exportProgressBar" width="310" height="10" /></td></tr>
                            </table>
                            <span id="cancelExportProcessButton" class="button" style="height:20px !important;">
                                <span style="float:right;">
                                    <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.export.cancel" />" onClick="javascript:scriptScopeExport.cancelExport();"><fmt:message key="dialog.admin.export.cancel" /></button>
                                </span>
                            </span>
                        </div> <!-- processInfoContent end -->
                    </div> <!-- processInfo end -->
                </div> <!-- inputContainer end -->
				<div class="spacer"></div>
        		<!-- LEFT HAND SIDE CONTENT START -->
				<table class="inputContainer field grey">
					<tr>
						<td><input type="radio" name="exportType" id="exportType1" class="radio" style="vertical-align: middle;" /><label class="noRightMargin" for="exportType1" style="padding-left: 5px;" onclick="javascript:dialog.showContextHelp(arguments[0], 8081)" ><fmt:message key="dialog.admin.export.partialExport" /></label></td>
						<td><span class="rightAlign marginRight"><div toggle="plain" listId="1370" style="width:485px;" id="exportXMLCriteria"></div></span></td>
					</tr>
					<tr>
						<td><input type="radio" name="exportType" id="exportType2" class="radio" style="vertical-align: middle;" /><label class="noRightMargin" for="exportType2" style="padding-left: 5px;" onclick="javascript:dialog.showContextHelp(arguments[0], 8082)"><fmt:message key="dialog.admin.export.treeExport" /></label></td>
						<td><span class="rightAlign marginRight"><input type="text" id="exportTreeName" name="exportTreeName" style="width:100%;" disabled="true" dojoType="dijit.form.ValidationTextBox" /></span></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <span class="functionalLink marginRight" style="position:relative;margin-top: 0px;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:scriptScopeExport.selectDataset()" title="<fmt:message key="dialog.admin.export.selectTree" /> [Popup]"><fmt:message key="dialog.admin.export.selectTree" /></a></span>
						    <span class="rightAlign marginRight"><span class="input leftAlign"><input type="checkbox" name="exportTreeSelectionOnly" id="exportTreeSelectionOnly" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8083)"><fmt:message key="dialog.admin.export.selectedNodeOnly" /></label></span></span>
                        </td>
					</tr>

				</table> <!-- inputContainer end -->
				<div class="inputContainer">
					<span class="button">
						<span style="float:right;">
							<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.export.start" />" onClick="javascript:scriptScopeExport.startExport();"><fmt:message key="dialog.admin.export.start" /></button>
						</span>
						<span id="exportLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
							<img src="img/ladekreis.gif" />
						</span>
					</span>
		            <div class="fill"></div>
				</div>
				<!-- LEFT HAND SIDE CONTENT END -->
			</div> <!-- content end -->
		</div> <!-- contentBlockWhite end -->
<!-- CONTENT END -->

</body>
</html>
