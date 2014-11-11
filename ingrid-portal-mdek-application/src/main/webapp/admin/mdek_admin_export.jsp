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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var pageExport = _container_;

require([
    "dojo/on",
    "dojo/dom",
    "dojo/dom-style",
    "dojo/Deferred",
    "dijit/registry",
    "ingrid/layoutCreator",
    "ingrid/utils/LoadingZone",
    "ingrid/utils/General",
    "ingrid/utils/Syslist",
    "ingrid/utils/String",
    "ingrid/utils/UI",
    "ingrid/dialog"
], function(on, dom, style, Deferred, registry, layoutCreator, LoadingZone, UtilGeneral, UtilSyslist, UtilString, UtilUI, dialog) {
    
    console.debug("Page export loaded");
    var currentSelectedNode = null;
    
    // Callback for the dwr calls to ExportService.export*
    // Wait for three seconds for the call to return and refresh the process info if it does
    // If we run into a timeout the job is still running and we simply refresh the process info
    // If we detect that the user has running jobs, display an error message that the job could not be started
    var exportServiceCallback = {
    	timeout: 5000,
    	preHook: LoadingZone.show,
    	postHook: LoadingZone.hide,
    	callback: function(res){
    		refreshExportProcessInfo();
    	},
    	errorHandler: function(msg, err) {
    		// We expect a timeout error
    		if (msg == "Timeout") {
    			refreshExportProcessInfo();
    		} else {
    			if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
    				dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
    			} else {
    			    displayErrorMessage(msg);
    			}
    		}
    	}
    }
    
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    layoutCreator.createComboBox("exportXMLCriteria", null, storeProps, function() {
        return UtilSyslist.getSyslistEntry(1370);
    });
    
    on(_container_, "Load", function(){
    	refreshExportProcessInfo();
    });
    
    function startExportCriteria() {
    	var exportCriteria      = registry.byId("exportXMLCriteria").get("value");
    	var exportWorkingCopies = registry.byId("exportWorkingCopies").checked;
    	
    	console.debug("exportCriteria: "+ exportCriteria);
    	if (exportCriteria) {
    		exportObjectsWithCriteria(exportCriteria, exportWorkingCopies);
    
    	} else {
    		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.export.selectExportCriteriaError' />", dialog.WARNING);
    	}
    }
    
    function startExportPartial() {
    	if (currentSelectedNode) {
    		var exportChildren      = !registry.byId("exportTreeSelectionOnly").checked;
    		var exportWorkingCopies = registry.byId("exportWorkingCopies").checked;
    		
    		if (currentSelectedNode.nodeAppType == "O") {
    			startObjectExport(currentSelectedNode.uuid, exportChildren, exportWorkingCopies);
    		} else {
    			startAddressExport(currentSelectedNode.uuid, exportChildren, exportWorkingCopies);
    		}
    	} else {
    		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.export.selectNodeError' />", dialog.WARNING);
    	}
    }
    
    function startObjectExport(uuid, exportChildren, exportWorkingCopies) {
    	if (uuid == "objectRoot") {
    		exportObjectBranch(null, exportChildren, exportWorkingCopies);
    
    	} else {
    		exportObjectBranch(uuid, exportChildren, exportWorkingCopies);
    	}
    }
    
    function exportObjectBranch(uuid, exportChildren, exportWorkingCopies) {
    	ExportService.exportObjectBranch(uuid, exportChildren, exportWorkingCopies, exportServiceCallback);
    }
    
    function exportObjectsWithCriteria(exportCriteria, exportWorkingCopies) {
    	ExportService.exportObjectsWithCriteria(exportCriteria, exportWorkingCopies, exportServiceCallback);
    }
    
    function startAddressExport(uuid, exportChildren, exportWorkingCopies) {
    	if (uuid == "addressRoot") {
    		exportTopAddresses(exportChildren, exportWorkingCopies);
    
    	} else if (uuid == "addressFreeRoot") {
    		exportFreeAddresses(exportWorkingCopies);
    
    	} else {
    		exportAddressBranch(uuid, exportChildren, exportWorkingCopies);
    	}
    }
    
    function exportAddressBranch(uuid, exportChildren, exportWorkingCopies) {
    	ExportService.exportAddressBranch(uuid, exportChildren, exportWorkingCopies, exportServiceCallback);
    }
    
    function exportTopAddresses(exportChildren, exportWorkingCopies) {
    	ExportService.exportTopAddresses(exportChildren, exportWorkingCopies, exportServiceCallback);
    }
    
    function exportFreeAddresses(exportWorkingCopies) {
    	ExportService.exportFreeAddresses(exportWorkingCopies, exportServiceCallback);
    }
    
    // Button function for 'start export'. Send a request to the backend to start a new export job
    pageExport.startExport = function() {
    	if (dom.byId("exportType1").checked) {
    		startExportCriteria();
    
    	} else if (dom.byId("exportType2").checked) {
    		startExportPartial();
    
    	} else {
    		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.export.selectExportTypeError' />", dialog.WARNING);
    		console.debug("No export type selected.");
    	}
    };
    
    // Button function for the 'select dataset' link.
    pageExport.selectDataset = function() {
    	var deferred = new Deferred();
    
    	deferred.then(function(selectedDataset) {
    		registry.byId("exportTreeName").setValue(selectedDataset.title);
    		currentSelectedNode = selectedDataset;
    		dom.byId("exportType2").checked = true;
    	});
    
    	dialog.showPage("<fmt:message key='dialog.admin.export.selectNode' />", 'admin/dialogs/mdek_admin_export_select_dataset.jsp?c='+userLocale, 522, 525, true, {
    		// custom parameters
    		resultHandler: deferred	
    	});
    };
    
    // Reload the process info for the last run from the backend
    function refreshExportProcessInfo() {
    	ExportService.getExportInfo(false, {
    		callback: function(exportInfo){
    			updateExportInfo(exportInfo);
    			if (!jobFinished(exportInfo)) {
    				setTimeout(refreshExportProcessInfo, 2000);
    			}
    		},
    		errorHandler: function(message, err) {
    		    displayErrorMessage(err);
    			console.debug("Error: "+ message);
    			// If there's a timeout try again
    			if (err.message != "USER_LOGIN_ERROR") {
    			    setTimeout(refreshExportProcessInfo, 2000);
    			}
    		}
    	});
    }
    
    function updateExportInfo(exportInfo) {
    	if (exportInfo.entityType == "OBJECT") {
    		dom.byId("exportInfoNumExportedEntitiesContainer").innerHTML = "<fmt:message key='dialog.admin.export.numObjects' />";
    	} else if (exportInfo.entityType == "ADDRESS") {
    		dom.byId("exportInfoNumExportedEntitiesContainer").innerHTML = "<fmt:message key='dialog.admin.export.numAddresses' />";
    	} else {
    		dom.byId("exportInfoNumExportedEntitiesContainer").innerHTML = "<fmt:message key='dialog.admin.export.numDatasets' />";
    	}
    
    	if (jobFinished(exportInfo)) {
    		dom.byId("exportInfoTitle").innerHTML = "<fmt:message key='dialog.admin.export.lastProcessInfo' />";
    		style.set("exportProgressBarContainer", "display", "none");
    		style.set("cancelExportProcessButton", "display", "none");
    
    		if (exportInfo.exception) {
    			style.set("exportExceptionMessage", "display", "block");
    			style.set("exportInfoDownload", "display", "none");
    			style.set("exportInfoEndDateContainer", "display", "none");
    			style.set("exportInfoNumExportedEntitiesContainer", "display", "none");
    			dom.byId("exportInfoNumExportedEntities").innerHTML = "";
    
    		} else if (exportInfo.endTime) {
    			//dojo.html.hide(dom.byId("exportExceptionMessage"));
    			style.set("exportExceptionMessage", "display", "none");
    			if (exportInfo.numProcessedEntities > 0)
    				style.set("exportInfoDownload", "display", "block");
    			else
    				style.set("exportInfoDownload", "display", "none");
    			style.set("exportInfoEndDateContainer", "display", "block");
    			style.set("exportInfoNumExportedEntitiesContainer", "display", "block");
    			dom.byId("exportInfoNumExportedEntities").innerHTML = exportInfo.numProcessedEntities;
    
    		} else {
    			// No job has been started yet
    			style.set("exportExceptionMessage", "display", "none");
    			style.set("exportInfoDownload", "display", "none");
    			style.set("exportInfoEndDateContainer", "display", "none");
    			style.set("exportInfoNumExportedEntitiesContainer", "display", "none");
                console.debug("set: none");
    			dom.byId("exportInfoNumExportedEntities").innerHTML = "";
    		}
    	} else {
    		dom.byId("exportInfoTitle").innerHTML = "<fmt:message key='dialog.admin.export.currentProcessInfo' />";
    		style.set("exportExceptionMessage", "display", "none");
    		style.set("exportInfoDownload", "display", "none");
    		style.set("exportInfoEndDateContainer", "display", "none");
    		style.set("cancelExportProcessButton", "display", "block");
    		style.set("exportInfoNumExportedEntitiesContainer", "display", "block");
    		style.set("exportProgressBarContainer", "display", "block");
    		dom.byId("exportInfoNumExportedEntities").innerHTML = exportInfo.numProcessedEntities + " / " + exportInfo.numEntities;
    
    		var progressBar = registry.byId("exportProgressBar");
            progressBar.update({
                maximum: exportInfo.numEntities,
                progress: exportInfo.numProcessedEntities
            });
    	}
        
        //console.debug(exportInfo);
    
    	if (exportInfo.startTime) {
    		dom.byId("exportInfoBeginDate").innerHTML = UtilString.getDateString(exportInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
    	} else {
    		dom.byId("exportInfoBeginDate").innerHTML = "";
    	}
    
    	if (exportInfo.endTime) {
    		dom.byId("exportInfoEndDate").innerHTML = UtilString.getDateString(exportInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
    	} else {
    		dom.byId("exportInfoEndDate").innerHTML = "";
    	}
    }
    
    function jobFinished(exportInfo) {
    	// endTime != null -> job has an end time means it's done
    	// exception != null -> job has an exception -> done
    	// startTime == null -> no start time means something went wrong (no previous job exists?)
    	return (exportInfo.endTime != null || exportInfo.exception != null || exportInfo.startTime == null);
    }
    
    
    pageExport.downloadLastExport = function() {
    	ExportService.getLastExportFile({
    		callback: function(exportFile) {
    			console.debug(exportFile);
    			dwr.engine.openInDownload(exportFile);
    		},
    		errorHandler: function(errMsg, err) {
    			displayErrorMessage(err);
    		}		
    	});
    };
    
    pageExport.cancelExport = function() {
    	ExportService.cancelRunningJob({
    		callback: function() {
    			// do nothing
    		},
    		errorHandler: function(errMsg, err) {
    			displayErrorMessage(err);
    		}		
    	});
    };
    
    pageExport.showJobException = function() {
    	ExportService.getExportInfo(false, {
    		callback: function(exportInfo){
    	    	dialog.show("<fmt:message key='general.error' />", UtilGeneral.getStackTrace(exportInfo.exception), dialog.INFO, null, 800, 600);
    		},
    		errorHandler: function(message, err) {
    		    displayErrorMessage(err);
    			console.debug("Error: "+ message);
    		}
    	});
    };
    
    /* function showLoadingZone() {
        dom.byId("exportLoadingZone").style.visibility = "visible";
    }
    
    function hideLoadingZone() {
        dom.byId("exportLoadingZone").style.visibility = "hidden";
    } */
});
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
                            <p id="exportInfoDownload"><fmt:message key="dialog.admin.export.result" /> <a href="javascript:void(0);" onclick="pageExport.downloadLastExport()" title="<fmt:message key="dialog.admin.export.log" />"><fmt:message key="general.link" /></a></p>
                            <p id="exportExceptionMessage"><fmt:message key="dialog.admin.export.error" /> <a href="javascript:void(0);" onclick="pageExport.showJobException()" title="<fmt:message key="dialog.admin.export.errorinfo" />"><fmt:message key="general.link" /></a></p>
                            <table cellspacing="0">
                                <tr>
                                    <td><fmt:message key="dialog.admin.export.startTime" /></td>
                                    <td id="exportInfoBeginDate"></td></tr>
                                    <tr><td id="exportInfoEndDateContainer"><fmt:message key="dialog.admin.export.endTime" /></td>
                                    <td id="exportInfoEndDate"></td></tr>
                                    <tr><td id="exportInfoNumExportedEntitiesContainer"></td>
                                    <td id="exportInfoNumExportedEntities"></td></tr>
                                    <tr><td id="exportProgressBarContainer" colspan=2><div data-dojo-type="dijit/ProgressBar" id="exportProgressBar" width="310" height="10" /></td></tr>
                            </table>
                            <span id="cancelExportProcessButton" class="button" style="height:20px !important;">
                                <span style="float:right;">
                                    <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.export.cancel" />" onclick="pageExport.cancelExport()"><fmt:message key="dialog.admin.export.cancel" /></button>
                                </span>
                            </span>
                        </div> <!-- processInfoContent end -->
                    </div> <!-- processInfo end -->
                </div> <!-- inputContainer end -->
				<div class="spacer"></div>
        		<!-- LEFT HAND SIDE CONTENT START -->
				<table class="inputContainer field grey">
					<tr>
						<td><input type="radio" name="exportType" id="exportType1" class="radio" style="vertical-align: middle;" /><label class="noRightMargin" for="exportType1" style="padding-left: 5px;" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8081)" ><fmt:message key="dialog.admin.export.partialExport" /></label></td>
						<td><span class="rightAlign marginRight"><div toggle="plain" listId="1370" style="width:485px;" id="exportXMLCriteria"></div></span></td>
					</tr>
					<tr>
						<td><input type="radio" name="exportType" id="exportType2" class="radio" style="vertical-align: middle;" /><label class="noRightMargin" for="exportType2" style="padding-left: 5px;" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8082)"><fmt:message key="dialog.admin.export.treeExport" /></label></td>
						<td><span class="rightAlign marginRight"><input type="text" id="exportTreeName" name="exportTreeName" style="width:100%;" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" /></span></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <span class="functionalLink marginRight" style="position:relative;margin-top: 0px;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="#" onclick="pageExport.selectDataset()" title="<fmt:message key="dialog.admin.export.selectTree" /> [Popup]"><fmt:message key="dialog.admin.export.selectTree" /></a></span>
						    <span class="rightAlign marginRight"><span class="input leftAlign" style="padding-bottom: 5px;"><input type="checkbox" name="exportTreeSelectionOnly" id="exportTreeSelectionOnly" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8083)"><fmt:message key="dialog.admin.export.selectedNodeOnly" /></label></span></span>
                            <span class="rightAlign marginRight"><span class="input leftAlign"><input type="checkbox" name="exportWorkingCopies" id="exportWorkingCopies" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8208)"><fmt:message key="dialog.admin.export.workingCopies" /></label></span></span>
                        </td>
					</tr>

				</table> <!-- inputContainer end -->
				<div class="inputContainer">
					<span class="button">
						<span style="float:right;">
							<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.export.start" />" onclick="pageExport.startExport()"><fmt:message key="dialog.admin.export.start" /></button>
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
