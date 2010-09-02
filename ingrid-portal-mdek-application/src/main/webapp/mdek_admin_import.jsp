<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<style>
span.subtreeImport div.dojoButton {
	margin-bottom:32px;
}

</style>
<script type="text/javascript">
var scriptScope = this;

var importTreeSelectedParentDataset = null;
var importTreeSelectedParentAddress = null;

// Callback for the dwr calls to ImportService.importEntities
// We don't set a timeout since we don't know how long the fileupload will take.
// The java method returns when the fileupload is finished. The file is then transferred to the backend and the
// import job is started. Since we don't know how long this takes, we have to estimate a timespan (3s for now).
// After that we start querying the backend for information about the current running job
var importServiceCallback = {
	preHook: showLoadingZone,
	postHook: hideLoadingZone,
	callback: function(res){
		refreshImportProcessInfo();
	},
	errorHandler: function(msg, err) {
		dojo.debugShallow(err);
		hideLoadingZone();
		if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
			dialog.show(message.get("general.error"), message.get("operation.error.userHasRunningJobs"), dialog.WARNING);
		} else {
		    displayErrorMessage(err);
		}
	}
}

var importTransformationCallback = {
	preHook: showLoadingZone,
	postHook: hideLoadingZone,
	callback: function(res){
		checkImportTransformationStatus();
	},
	errorHandler: function(msg, err) {
		dojo.debugShallow(err);
		hideLoadingZone();
		if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
			dialog.show(message.get("general.error"), message.get("operation.error.userHasRunningJobs"), dialog.WARNING);
		} else {
		    displayErrorMessage(err);
		}
	}
}

_container_.addOnLoad(function() {
	initTree();
	initCheckboxBehaviour();
	refreshImportProcessInfo();
});

function initCheckboxBehaviour() {
	// Only one of the checkboxes is allowed to be active
	var pubImportCheckbox = dojo.widget.byId("publishImportedDatasetsCheckbox");
	var sepImportCheckbox = dojo.widget.byId("separateImportCheckbox");
	var importFileType    = dojo.widget.byId("importFileType");

	dojo.event.connect(pubImportCheckbox, "onClick", function(){
		if (sepImportCheckbox.checked) {
			sepImportCheckbox.setValue(false);
		}
	});
	dojo.event.connect(sepImportCheckbox, "onClick", function(){
		if (pubImportCheckbox.checked) {
			pubImportCheckbox.setValue(false);
		}
	});
	
	importFileType.setValue("igc");
}

scriptScope.resetImport = function() {
	dojo.widget.byId("importTreeParentDataset").setValue("");
	dojo.widget.byId("importTreeParentAddress").setValue("");
	dojo.widget.byId("publishImportedDatasetsCheckbox").setValue(false);
	dojo.widget.byId("separateImportCheckbox").setValue(false);
	dojo.widget.byId("importFileType").setValue("igc");
	importTreeSelectedParentDataset = null;
	importTreeSelectedParentAddress = null;
}

scriptScope.selectParentDatasetForTreeImport = function() {
	var selectedNode = dojo.widget.byId("treeImportTree").selectedNode;

	if (selectedNode && selectedNode.nodeAppType == "O" && selectedNode.uuid != "objectRoot") {
		dojo.widget.byId("importTreeParentDataset").setValue(selectedNode.title);
		importTreeSelectedParentDataset = selectedNode;

	} else {
		dialog.show(message.get("general.error"), message.get("dialog.admin.import.selectNodeError"), dialog.WARNING);
	}
}

scriptScope.selectParentAddressForTreeImport = function() {
	var selectedNode = dojo.widget.byId("treeImportTree").selectedNode;

	if (selectedNode && selectedNode.nodeAppType == "A" && selectedNode.objectClass == 0) {
		dojo.widget.byId("importTreeParentAddress").setValue(selectedNode.title);
		importTreeSelectedParentAddress = selectedNode;

	} else {
		dialog.show(message.get("general.error"), message.get("dialog.admin.import.selectInstitutionError"), dialog.WARNING);
	}
}

scriptScope.downloadImportLog = function() {
	ImportService.getLastImportLog({
		callback: function(importLog) {
			dwr.engine.openInDownload(importLog);
		},
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
		}		
	});
}

scriptScope.showJobException = function() {
	ImportService.getImportInfo({
		callback: function(importInfo){
	    	dialog.show(message.get("general.error"), UtilGeneral.getStackTrace(importInfo.exception), dialog.INFO, null, 800, 600);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+ message);
		}
	});
}

function startTreeImport() {
	var file = dwr.util.getValue("importFile");
	var fileType = dojo.widget.byId("importFileType").getValue();
	var parentObjectUuid = (importTreeSelectedParentDataset != null && importTreeSelectedParentDataset.uuid != "objectRoot") ? importTreeSelectedParentDataset.uuid : null;
	var parentAddressUuid = (importTreeSelectedParentAddress != null) ? importTreeSelectedParentAddress.uuid : null;
	var publishImportedDatasets = dojo.widget.byId("publishImportedDatasetsCheckbox").checked;
	var separateImport = dojo.widget.byId("separateImportCheckbox").checked;

	dojo.debug("file: "+file);
	dojo.debug("parent object uuid: "+parentObjectUuid);
	dojo.debug("parent address uuid: "+parentAddressUuid);

	ImportService.startImportThread(file, fileType, parentObjectUuid, parentAddressUuid,
		publishImportedDatasets, separateImport, {
			callback: function(res) {
				refreshImportProcessInfo();
			},
			errorHandler: function(msg, err){
				dojo.debugShallow(err);
				hideLoadingZone();
				if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
					dialog.show(message.get("general.error"), message.get("operation.error.userHasRunningJobs"), dialog.WARNING);
				} else {
				    displayErrorMessage(err);
				}
			}
		});
}

function startTreeImportProtocol() {
	var file = dwr.util.getValue("importFile");
	var fileType = dojo.widget.byId("importFileType").getValue();
	var parentObjectUuid = (importTreeSelectedParentDataset != null && importTreeSelectedParentDataset.uuid != "objectRoot") ? importTreeSelectedParentDataset.uuid : null;
	var parentAddressUuid = (importTreeSelectedParentAddress != null) ? importTreeSelectedParentAddress.uuid : null;
	var publishImportedDatasets = dojo.widget.byId("publishImportedDatasetsCheckbox").checked;
	var separateImport = dojo.widget.byId("separateImportCheckbox").checked;

	dojo.debug("file: "+file);
	dojo.debug("parent object uuid: "+parentObjectUuid);
	dojo.debug("parent address uuid: "+parentAddressUuid);

	ImportService.importEntities(file, fileType, parentObjectUuid, parentAddressUuid, publishImportedDatasets, separateImport, {
		errorHandler: function(msg, err){
			dojo.debugShallow(err);
			hideLoadingZone();
		    displayErrorMessage(err);
		}
	});
	
	//dojo.debug("check periodically status");
	setTimeout("checkImportTransformationStatus()", 2000);
}

scriptScope.startImportProtocol = function() {
	if (dojo.byId("importFile").value && validImportNodesSelected()) {
		startTreeImportProtocol();
	} else {
		dialog.show(message.get("general.error"), message.get("dialog.admin.import.selectParentsError"), dialog.WARNING);
	}
}

function validImportNodesSelected() {
	return (importTreeSelectedParentDataset != null
			&& importTreeSelectedParentDataset.uuid != "objectRoot"
			&& importTreeSelectedParentAddress != null);
}

scriptScope.cancelImport = function() {
	ImportService.cancelRunningJob({
		callback: function() {
			// do nothing
		},
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
		}		
	});
}

function initTree() {
	// Load initial first level of the tree from the server
	TreeService.getSubTree(null, null, 
		function (rootNodeList) {
			var importTree = dojo.widget.byId("treeImportTree");

			dojo.lang.forEach(rootNodeList, function(rootNode){
				rootNode.title = dojo.string.escape("html", rootNode.title);
				rootNode.uuid = rootNode.id;
				rootNode.id = null;
			});

			importTree.setChildren(rootNodeList);
	});

	// Function to load children of the node from server
	var loadRemote = function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		deferred.addCallback(function(res) {
			dojo.lang.forEach(res, function(obj){
				obj.title = dojo.string.escape("html", obj.title);
				obj.uuid = obj.id;
				obj.id = null;
			});
			return _this.loadProcessResponse(node,res);
		});
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});

		TreeService.getSubTree(node.uuid, node.nodeAppType, {
  			callback:function(res) { deferred.callback(res); },
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});

		return deferred;
	};

	// Attach load remote function to the tree controllers
	var importTreeController = dojo.widget.byId("treeImportTreeController");
	importTreeController.loadRemote = loadRemote;
}

//Reload the process info for the last run from the backend
refreshImportProcessInfo = function() {
	ImportService.getImportInfo({
		callback: function(importInfo){
			updateImportInfo(importInfo);
			if (!jobFinished(importInfo)) {
				setTimeout("refreshImportProcessInfo()", 2000);
			}
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+ message);
			// If there's a timeout try again
			if (err.message != "USER_LOGIN_ERROR") {
			    setTimeout("refreshImportProcessInfo()", 2000);
			}
		}
	});
}

//Check for transformation status
checkImportTransformationStatus = function() {
	dojo.debug("check protocol now");
	ImportService.getProtocolInfo({
		callback: function(infoBean){
			updateReadInputInfo(infoBean);
			dojo.debug("Status: " + infoBean.finished);
			if (infoBean.finished != true) {
				dojo.debug("call again later");
				setTimeout("checkImportTransformationStatus()", 2000);
			} else {
				showProtocol(infoBean);
			}
		}
	});
}

function showProtocol(infoBean) {
	if(infoBean.inputType != "igc"){
		var protocolMessageToHtml = returnToBr(infoBean.protocol);
		if(protocolMessageToHtml != null && protocolMessageToHtml.length > 0){
			dialog.show(message.get("protocol.title"), protocolMessageToHtml, dialog.MESSAGE, [{caption:message.get("protocol.import"),action:function(){startTreeImport()}}, {caption:message.get("protocol.cancel"),action:dialog.CLOSE_ACTION}], 500, 500);
		}else{
			dialog.show(message.get("protocol.title"), message.get("protocol.empty"), dialog.MESSAGE, [{caption:message.get("protocol.import"),action:function(){startTreeImport()}}, {caption:message.get("protocol.cancel"),action:dialog.CLOSE_ACTION}], 500, 500);
		}
	} else {
		startTreeImport();
	}
}

function updateReadInputInfo(infoBean) {
	dojo.html.hide(dojo.byId("importExceptionMessage"));
	dojo.html.hide(dojo.byId("importInfoDownload"));
	dojo.html.hide(dojo.byId("importInfoStartDateContainer"));
	dojo.html.hide(dojo.byId("importInfoBeginDate"));
	dojo.html.hide(dojo.byId("importInfoEndDateContainer"));
	dojo.html.hide(dojo.byId("importInfoEndDate"));
	dojo.html.hide(dojo.byId("importInfoNumImportedAddressContainer"));
	dojo.html.hide(dojo.byId("importInfoNumImportedAddresses"));
	
	dojo.byId("importInfoNumImportedObjectContainer").innerHTML = message.get("dialog.admin.import.progress");
	
	// conversion is finished
	if (infoBean.finished == true) {
		dojo.widget.byId('importButton').disabled = false;
		dojo.byId("importInfoTitle").innerHTML = message.get("dialog.admin.import.lastProcessInfo");
		dojo.html.hide(dojo.byId("importProgressBarContainer"));
		dojo.byId("importInfoNumImportedObjects").innerHTML = infoBean.dataProcessed + "%";
	// conversion is still in progress
	} else {
		dojo.widget.byId('importButton').disabled = true;
		dojo.byId("importInfoTitle").innerHTML = message.get("dialog.admin.import.currentProcessInfo");
		dojo.html.show(dojo.byId("importInfoNumImportedObjectContainer"));
		dojo.html.show(dojo.byId("importProgressBarContainer"));
		
		dojo.byId("importInfoNumImportedObjects").innerHTML = infoBean.dataProcessed + " / 100%";
		
		var progressBar = dojo.widget.byId("importProgressBar");
		progressBar.setMaxProgressValue(100);
		progressBar.setProgressValue(infoBean.dataProcessed);
	}
}

function updateImportInfo(importInfo) {
	dojo.byId("importInfoNumImportedObjectContainer").innerHTML = message.get("dialog.admin.import.numObjects");
	dojo.byId("importInfoNumImportedAddressContainer").innerHTML = message.get("dialog.admin.import.numAddresses");
	dojo.html.show(dojo.byId("importInfoStartDateContainer"));
	dojo.html.show(dojo.byId("importInfoBeginDate"));
	dojo.html.show(dojo.byId("importInfoNumImportedAddresses"));
	
	if (jobFinished(importInfo)) {
		dojo.byId("importInfoTitle").innerHTML = message.get("dialog.admin.import.lastProcessInfo");
		dojo.html.hide(dojo.byId("importProgressBarContainer"));
		dojo.html.hide(dojo.byId("cancelImportProcessButton"));

		if (importInfo.exception) {
			dojo.html.show(dojo.byId("importExceptionMessage"));
			dojo.html.hide(dojo.byId("importInfoDownload"));
			dojo.html.hide(dojo.byId("importInfoEndDateContainer"));
			dojo.html.hide(dojo.byId("importInfoNumImportedObjectContainer"));
			dojo.html.hide(dojo.byId("importInfoNumImportedAddressContainer"));
			dojo.byId("importInfoNumImportedObjects").innerHTML = "";
			dojo.byId("importInfoNumImportedAddresses").innerHTML = "";

		} else if (importInfo.endTime) {
			dojo.html.hide(dojo.byId("importExceptionMessage"));
			dojo.html.show(dojo.byId("importInfoDownload"));
			dojo.html.show(dojo.byId("importInfoEndDateContainer"));
			dojo.html.show(dojo.byId("importInfoEndDate"));
			dojo.html.show(dojo.byId("importInfoNumImportedObjectContainer"));
			dojo.html.show(dojo.byId("importInfoNumImportedAddressContainer"));
			dojo.byId("importInfoNumImportedObjects").innerHTML = importInfo.numProcessedObjects;
			dojo.byId("importInfoNumImportedAddresses").innerHTML = importInfo.numProcessedAddresses;

		} else {
			// No job has been started yet
			dojo.html.hide(dojo.byId("importExceptionMessage"));
			dojo.html.hide(dojo.byId("importInfoDownload"));
			dojo.html.hide(dojo.byId("importInfoEndDateContainer"));
			dojo.html.hide(dojo.byId("importInfoNumImportedObjectContainer"));
			dojo.html.hide(dojo.byId("importInfoNumImportedAddressContainer"));
			dojo.byId("importInfoNumImportedObjects").innerHTML = "";
			dojo.byId("importInfoNumImportedAddresses").innerHTML = "";
		}
	} else {
		dojo.byId("importInfoTitle").innerHTML = message.get("dialog.admin.import.currentProcessInfo");
		dojo.html.hide(dojo.byId("importExceptionMessage"));
		dojo.html.hide(dojo.byId("importInfoDownload"));
		dojo.html.hide(dojo.byId("importInfoEndDateContainer"));
		dojo.html.show(dojo.byId("cancelImportProcessButton"));
		dojo.html.show(dojo.byId("importInfoNumImportedAddressContainer"));
		dojo.html.show(dojo.byId("importInfoNumImportedObjectContainer"));
		dojo.html.show(dojo.byId("importProgressBarContainer"));
		dojo.byId("importInfoNumImportedObjects").innerHTML = importInfo.numProcessedObjects + " / " + importInfo.numObjects;
		dojo.byId("importInfoNumImportedAddresses").innerHTML = importInfo.numProcessedAddresses + " / " + importInfo.numAddresses;

		var progressBar = dojo.widget.byId("importProgressBar");
		progressBar.setMaxProgressValue(importInfo.numObjects + importInfo.numAddresses);
		progressBar.setProgressValue(importInfo.numProcessedObjects + importInfo.numProcessedAddresses);
	}

	if (importInfo.startTime) {
		dojo.byId("importInfoBeginDate").innerHTML = importInfo.startTime.toLocaleString();
	} else {
		dojo.byId("importInfoBeginDate").innerHTML = "";
	}

	if (importInfo.endTime) {
		dojo.byId("importInfoEndDate").innerHTML = importInfo.endTime.toLocaleString();
	} else {
		dojo.byId("importInfoEndDate").innerHTML = "";
	}
}

function jobFinished(importInfo) {
	// endTime != null -> job has an end time means it's done
	// exception != null -> job has an exception -> done
	// startTime == null -> no start time means something went wrong (no previous job exists?)
	return (importInfo.endTime != null || importInfo.exception != null || importInfo.startTime == null);
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("importLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("importLoadingZone"), "hidden");
}

function returnToBr(string) {
	if(string != null){
    	return string.replace(/[\r\n]/g, "<br />");
	}else{
		return null;
	}
}


</script>
</head>

<body>

	<!-- CONTENT START -->
	<div dojoType="ContentPane" layoutAlign="client">

		<div id="contentSection" class="contentBlockWhite top">
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=import-export-2#import-export-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
			</div>
			<div id="import" class="content">
				<div class="inputContainer noSpaceBelow">
					<div id="ImportProcessInfo" class="infobox w941">
						<span class="icon"><img src="img/ic_info_download.gif" width="16" height="16" alt="Info" /></span>
						<span id="importInfoTitle" class="title"></span>
						<div id="ImportProcessInfoContent">
							<p id="importInfoDownload"><fmt:message key="dialog.admin.import.result" /> <a href="javascript:void(0);" onclick="javascript:scriptScope.downloadImportLog();" title="<fmt:message key="dialog.admin.import.log" />">link</a></p>
							<p id="importExceptionMessage"><fmt:message key="dialog.admin.import.error" /> <a href="javascript:void(0);" onclick="javascript:scriptScope.showJobException();" title="<fmt:message key="dialog.admin.import.errorinfo" />">link</a></p>
							<table cellspacing="0">
								<tr>
									<td id="importInfoStartDateContainer"><fmt:message key="dialog.admin.import.startTime" /></td>
									<td id="importInfoBeginDate"></td></tr>
									<tr><td id="importInfoEndDateContainer"><fmt:message key="dialog.admin.import.endTime" /></td>
									<td id="importInfoEndDate"></td></tr>
									<tr><td id="importInfoNumImportedObjectContainer"></td>
									<td id="importInfoNumImportedObjects"></td></tr>
									<tr><td id="importInfoNumImportedAddressContainer"></td>
									<td id="importInfoNumImportedAddresses"></td></tr>
									<tr><td id="importProgressBarContainer" colspan=2><div dojoType="ProgressBar" id="importProgressBar" width="310" height="10" /></td></tr>
							</table>
							<span id="cancelImportProcessButton" class="button" style="height:20px !important;">
								<span style="float:right;">
									<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.import.cancel" />" onClick="javascript:scriptScope.cancelImport();"><fmt:message key="dialog.admin.import.cancel" /></button>
								</span>
							</span>
						</div> <!-- processInfoContent end -->
					</div> <!-- processInfo end -->
				</div> <!-- inputContainer end -->
				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="spacer"></div>
				<div class="inputContainer field grey w939">
					<div class="inputContainer">
						<span class="label"><label style="position:absolute;" for="importFile" onclick="javascript:dialog.showContextHelp(arguments[0], 8074, 'Import-Datei')"><fmt:message key="dialog.admin.import.file" />:</label>
							<input type="file" id="importFile" size="80" style="position:absolute;margin-left:90px;height: 21px;" />
						</span>
					</div>
					<div>
						<span class="label" style="margin-bottom:10.5px;"><label for="importFileType" style="position:absolute;" onclick="javascript:dialog.showContextHelp(arguments[0], 8074, 'Import-Datei')"><fmt:message key="dialog.admin.import.file.type" />:</label>
							<select dojoType="ingrid:Select" id="importFileType" style="position:absolute;margin-left:90px;width:300px">
								<option value="igc">InGrid Catalog</option>
                                <option value="csw202">CSW 2.0.2 AP ISO 1.0 (Single Metadata file)</option>
								<option value="arcgis1">ArcGIS ISO-Editor</option>
							</select>
						</span>
					</div>
					<div class="checkboxContainer">
						<input type="checkbox" id="publishImportedDatasetsCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8075, 'Importierte Datens&auml;tze ver&ouml;ffentlichen')"><fmt:message key="dialog.admin.import.publish" /></label>
					</div>
					<div class="checkboxContainer">
						<input type="checkbox" id="separateImportCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8076, 'Importierte Datens&auml;tze ausschliesslich unter dem gew&auml;hlten Importknoten anlegen')"><fmt:message key="dialog.admin.import.importToSubtree" /></label>
					</div>
				</div>

        <!-- IMPORT TEILBAUM START -->
        <!-- SPLIT CONTAINER START -->
        <div id="importTree">
          <span class="label"><label><fmt:message key="dialog.admin.import.tree" /></label></span>
          <div dojoType="ingrid:SplitContainer" id="importTreeContainer" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:auto;" sizeShare="22.5">
              <!-- LEFT HAND SIDE CONTENT IMPORT TEILBAUM START -->
              <div class="inputContainer grey noSpaceBelow">
              	<div dojoType="ContentPane" id="treeImportTreeContent">
                  <!-- tree components -->
                  <div dojoType="ingrid:TreeController" widgetId="treeImportTreeController" RpcUrl="server/treelistener.php"></div>
                  <div dojoType="ingrid:TreeListener" widgetId="treeImportTreeListener"></div>	
                  <div dojoType="ingrid:TreeDocIcons" widgetId="treeImportTreeDocIcons"></div>	
                  <div dojoType="ingrid:TreeDecorator" listener="treeImportTreeListener"></div>
                  
                  <!-- tree -->
                  <div dojoType="ingrid:Tree" listeners="treeImportTreeController;treeImportTreeListener;treeImportTreeDocIcons" widgetId="treeImportTree">
                  </div>
                  <div class="spacer"></div>
                </div>
              </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT IMPORT TEILBAUM END -->

            <!-- RIGHT HAND SIDE CONTENT IMPORT TEILBAUM START -->
            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:hidden;" sizeshare="77.5">

				<span class="entry">
					<span class="buttonCol subtreeImport" style="margin-top:39px;">
						<button dojoType="ingrid:Button" onClick="javascript:scriptScope.selectParentDatasetForTreeImport();">&nbsp;>&nbsp;</button>
						<button dojoType="ingrid:Button" onClick="javascript:scriptScope.selectParentAddressForTreeImport();">&nbsp;>&nbsp;</button>
					</span>
				</span>

              <div id="importTreeData" class="entry field">
                <span class="label"><label for="importTreeParentObject" onclick="javascript:dialog.showContextHelp(arguments[0], 8077, 'Ausgew&auml;hltes &uuml;bergeordnetes Objekt')"><fmt:message key="dialog.admin.import.parentObject" /></label></span>
                <span class="input spaceBelow"><input type="text" id="importTreeParentDataset" class="w628" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
                <span class="label"><label for="importTreeParentAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 8078, 'Ausgew&auml;hlte &uuml;bergeordnete Adresse')"><fmt:message key="dialog.admin.import.parentAddress" /></label></span>
                <span class="input"><input type="text" id="importTreeParentAddress" class="w628" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
            	</div>
            </div>
          </div>
          <!-- RIGHT HAND SIDE CONTENT IMPORT TEILBAUM END -->

			<div class="inputContainer">
				<span class="button w915" style="height:20px !important;">
					<span style="float:right;">
								<button id="importButton" dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.import.start" />" onClick="javascript:scriptScope.startImportProtocol();"><fmt:message key="dialog.admin.import.start" /></button>
					</span>
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.import.reset" />" onClick="javascript:scriptScope.resetImport();"><fmt:message key="dialog.admin.import.reset" /></button>
					</span>
					<span id="importLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
	            <div class="fill"></div>
			</div>
         </div>
        <!-- IMPORT TEILBAUM END -->
      </div>
    </div>
  </div>
  <!-- CONTENT END -->

</body>
</html>
