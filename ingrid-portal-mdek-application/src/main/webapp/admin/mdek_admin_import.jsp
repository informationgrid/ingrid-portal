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
dojo.require("dijit.ProgressBar");
dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
dojo.require("ingrid.dijit.CustomTree");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.ValidationTextBox");

var scriptScopeImport = _container_;

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
        console.debug("importServiceCallback");
		refreshImportProcessInfo();
	},
	errorHandler: function(msg, err) {
		hideLoadingZone();
		if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
			dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
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
        //console.debugShallow(err);
        hideLoadingZone();
        if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
        } else {
            displayErrorMessage(err);
        }
    }
}


dojo.connect(_container_, "onLoad", function(){
	initTree();
	initCheckboxBehaviour();
	refreshImportProcessInfo();
});

function initCheckboxBehaviour() {
	console.debug("init checkboxes");
	// Only one of the checkboxes is allowed to be active
	var pubImportCheckbox = dijit.byId("publishImportedDatasetsCheckbox");
	var sepImportCheckbox = dijit.byId("separateImportCheckbox");
	var importFileType    = dijit.byId("importFileType");

	dojo.connect(pubImportCheckbox, "onClick", function(){
		if (sepImportCheckbox.checked) {
			sepImportCheckbox.setValue(false);
		}
	});
	dojo.connect(sepImportCheckbox, "onClick", function(){
		if (pubImportCheckbox.checked) {
			pubImportCheckbox.setValue(false);
		}
	});
	
	importFileType.setValue("igc");
	console.debug("init checkboxes ... finished");
}

scriptScopeImport.resetImport = function() {
	dijit.byId("importTreeParentDataset").setValue("");
	dijit.byId("importTreeParentAddress").setValue("");
	dijit.byId("publishImportedDatasetsCheckbox").setValue(false);
	dijit.byId("separateImportCheckbox").setValue(false);
	dijit.byId("importFileType").setValue("igc");
	importTreeSelectedParentDataset = null;
	importTreeSelectedParentAddress = null;
}

scriptScopeImport.selectParentDatasetForTreeImport = function() {
	var selectedNode = dijit.byId("treeImportTree").selectedNode;

	if (selectedNode && selectedNode.item.nodeAppType[0] == "O" && selectedNode.item.uuid[0] != "objectRoot") {
		dijit.byId("importTreeParentDataset").setValue(selectedNode.item.title[0]);
		importTreeSelectedParentDataset = selectedNode.item;

	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.import.selectNodeError' />", dialog.WARNING);
	}
}

scriptScopeImport.selectParentAddressForTreeImport = function() {
	var selectedNode = dijit.byId("treeImportTree").selectedNode;

	if (selectedNode && selectedNode.item.nodeAppType[0] == "A" && selectedNode.item.objectClass[0] == 0) {
		dijit.byId("importTreeParentAddress").setValue(selectedNode.item.title[0]);
		importTreeSelectedParentAddress = selectedNode.item;

	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.import.selectInstitutionError' />", dialog.WARNING);
	}
}

scriptScopeImport.downloadImportLog = function() {
	ImportService.getLastImportLog({
		callback: function(importLog) {
			dwr.engine.openInDownload(importLog);
		},
		errorHandler: function(errMsg, err) {
			displayErrorMessage(err);
		}		
	});
}

scriptScopeImport.showJobException = function() {
	ImportService.getImportInfo({
		callback: function(importInfo){
            console.debug("showJobException callback");
	    	dialog.show("<fmt:message key='general.error' />", UtilGeneral.getStackTrace(importInfo.exception), dialog.INFO, null, 800, 600);
		},
		errorHandler: function(message, err) {
            console.debug("showJobException errback");
		    displayErrorMessage(err);
			console.debug("Error: "+ message);
		}
	});
}

function startTreeImport() {
	var file = dwr.util.getValue("importFile");
	var fileType = dijit.byId("importFileType").getValue();
	var parentObjectUuid = (importTreeSelectedParentDataset != null && importTreeSelectedParentDataset.uuid[0] != "objectRoot") ? importTreeSelectedParentDataset.uuid[0] : null;
	var parentAddressUuid = (importTreeSelectedParentAddress != null) ? importTreeSelectedParentAddress.uuid[0] : null;
	var publishImportedDatasets = dijit.byId("publishImportedDatasetsCheckbox").checked;
	var separateImport = dijit.byId("separateImportCheckbox").checked;

	console.debug("file: "+file);
	console.debug("parent object uuid: "+parentObjectUuid);
	console.debug("parent address uuid: "+parentAddressUuid);

	//ImportService.importEntities(file, fileType, parentObjectUuid, parentAddressUuid, publishImportedDatasets, separateImport, importServiceCallback);
    ImportService.startImportThread(file, fileType, parentObjectUuid, parentAddressUuid, publishImportedDatasets, separateImport, {
        callback: function(res){
            refreshImportProcessInfo();
        },
        errorHandler: function(msg, err){
            hideLoadingZone();
            if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
            }
            else {
                displayErrorMessage(err);
            }
        }
    });
}

function startTreeImportProtocol() {
    var file = dwr.util.getValue("importFile");
    var fileType = dijit.byId("importFileType").getValue();
    var parentObjectUuid = (importTreeSelectedParentDataset != null && importTreeSelectedParentDataset.uuid[0] != "objectRoot") ? importTreeSelectedParentDataset.uuid[0] : null;
    var parentAddressUuid = (importTreeSelectedParentAddress != null) ? importTreeSelectedParentAddress.uuid[0] : null;
    var publishImportedDatasets = dijit.byId("publishImportedDatasetsCheckbox").checked;
    var separateImport = dijit.byId("separateImportCheckbox").checked;

    console.debug("file: "+file);
    console.debug("parent object uuid: "+parentObjectUuid);
    console.debug("parent address uuid: "+parentAddressUuid);

    ImportService.importEntities(file, fileType, parentObjectUuid, parentAddressUuid, publishImportedDatasets, separateImport, {
        errorHandler: function(msg, err){
            hideLoadingZone();
            displayErrorMessage(err);
        }
    });
    
    //console.debug("check periodically status");
    setTimeout("checkImportTransformationStatus()", 3000);
}

scriptScopeImport.startImport = function() {
	if (dojo.byId("importFile").value && validImportNodesSelected()) {
		startTreeImportProtocol();
	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.import.selectParentsError' />", dialog.WARNING);
	}
}

function validImportNodesSelected() {
	return (importTreeSelectedParentDataset != null
			&& importTreeSelectedParentDataset.uuid[0] != "objectRoot"
			&& importTreeSelectedParentAddress != null);
}

scriptScopeImport.cancelImport = function() {
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
    createCustomTree("treeImportTree", null, "id", "title", loadTreeData);
}

function loadTreeData(node, callback_function) {
	var parentItem = node.item;
	var prefix = "import_";
	var store = dijit.byId("treeImportTree").model.store;
	var def = UtilTree.getSubTree(parentItem, prefix.length);
	
	def.addCallback(function(data){
	    if (parentItem.root) {
			dojo.forEach(data, function(entry){
				entry.uuid = entry.id;
				entry.id = prefix + entry.id;
		        store.newItem(entry);
			});
	    }
	    else {
	        dojo.forEach(data, function(entry){
				entry.uuid = entry.id;
				entry.id = prefix+entry.id;
	            store.newItem(entry, {
	                parent: parentItem,
	                attribute: "children"
	            });
	        });
	    }
	    callback_function();
	});
	def.addErrback(function(res){
	    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
	    console.debug(res);
	    return res;
	});
	return def;
}

//Reload the process info for the last run from the backend
refreshImportProcessInfo = function() {
	ImportService.getImportInfo({
		callback: function(importInfo){
            console.debug("refreshImportProcessInfo callback");
			updateImportInfo(importInfo);
			if (!jobFinished(importInfo)) {
				setTimeout("refreshImportProcessInfo()", 2000);
			}
		},
		errorHandler: function(message, err) {
            console.debug("refreshImportProcessInfo callback");
		    displayErrorMessage(err);
			console.debug("Error: "+ message);
			// If there's a timeout try again
			if (err.message != "USER_LOGIN_ERROR") {
			    setTimeout("refreshImportProcessInfo()", 2000);
			}
		}
	});
}

//Check for transformation status
checkImportTransformationStatus = function() {
    console.debug("check protocol now");
    ImportService.getProtocolInfo({
        callback: function(infoBean){
            updateReadInputInfo(infoBean);
            console.debug("Status: " + infoBean.finished);
            if (infoBean.finished != true) {
                console.debug("call again later");
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
            dialog.show("<fmt:message key='protocol.title' />", protocolMessageToHtml, dialog.MESSAGE, [{caption:"<fmt:message key='protocol.import' />",action:function(){startTreeImport()}}, {caption:"<fmt:message key='protocol.cancel' />",action:dialog.CLOSE_ACTION}], 500, 500);
        }else{
            dialog.show("<fmt:message key='protocol.title' />", "<fmt:message key='protocol.empty' />", dialog.MESSAGE, [{caption:"<fmt:message key='protocol.import' />",action:function(){startTreeImport()}}, {caption:"<fmt:message key='protocol.cancel' />",action:dialog.CLOSE_ACTION}], 500, 500);
        }
    } else {
        startTreeImport();
    }
}

function updateReadInputInfo(infoBean) {
    dojo.style("importExceptionMessage", "display", "none");
    dojo.style("importInfoDownload", "display", "none");
    dojo.style("importInfoStartDateContainer", "display", "none");
    dojo.style("importInfoBeginDate", "display", "none");
    dojo.style("importInfoEndDateContainer", "display", "none");
    dojo.style("importInfoEndDate", "display", "none");
    dojo.style("importInfoNumImportedAddressContainer", "display", "none");
    dojo.style("importInfoNumImportedAddresses", "display", "none");
    dojo.style("importProgressBarContainer", "display", "none");
    
    dojo.byId("importInfoNumImportedObjectContainer").innerHTML = "<fmt:message key='dialog.admin.import.progress' />";
    
    // conversion is finished
    if (infoBean.finished == true) {
        dijit.byId('importButton').set("disabled", false);
        dojo.byId("importInfoTitle").innerHTML ="<fmt:message key='dialog.admin.import.lastProcessInfo' />";
        //dojo.style("importProgressBarContainer", "display", "block");
        dojo.byId("importInfoNumImportedObjects").innerHTML = "";
        //dojo.style("importInfoNumImportedObjects", "display", "none");
    // conversion is still in progress
    } else {
        dijit.byId('importButton').set("disabled", true);
        dojo.byId("importInfoTitle").innerHTML = "<fmt:message key='dialog.admin.import.currentProcessInfo' />";
        dojo.style("importInfoNumImportedObjectContainer", "display", "");
        dojo.style("importProgressBarContainer", "display", "block");
        
        dojo.byId("importInfoNumImportedObjects").innerHTML = infoBean.dataProcessed + " / 100%";
        
        var progressBar = dijit.byId("importProgressBar");
        progressBar.set("maximum", 100);
        progressBar.set("value", infoBean.dataProcessed);
    }
}

function updateImportInfo(importInfo) {
    console.debug("updateImportInfo");
	dojo.byId("importInfoNumImportedObjectContainer").innerHTML = "<fmt:message key='dialog.admin.import.numObjects' />";
    dojo.byId("importInfoNumImportedAddressContainer").innerHTML = "<fmt:message key='dialog.admin.import.numAddresses' />";

    dojo.style("importInfoStartDateContainer", "display", "");
    dojo.style("importInfoBeginDate", "display", "block");
    dojo.style("importInfoNumImportedAddresses", "display", "");

    console.debug("job finished?");
	if (jobFinished(importInfo)) {
		dojo.byId("importInfoTitle").innerHTML = "<fmt:message key='dialog.admin.import.lastProcessInfo' />";
		dojo.style("importProgressBarContainer","display","none");
		dojo.style("cancelImportProcessButton","display","none");

		if (importInfo.exception) {
			dojo.style("importExceptionMessage","display","block");
			dojo.style("importInfoDownload","display","none");
			dojo.style("importInfoEndDateContainer","display","none");
            
            dojo.style("importInfoNumImportedObjectContainer","display","none");
            //dojo.style("importInfoNumImportedObjects","display","none");
            dojo.style("importInfoNumImportedAddressContainer","display","none");
            dojo.byId("importInfoNumImportedObjects").innerHTML = "";
            dojo.byId("importInfoNumImportedAddresses").innerHTML = "";

		} else if (importInfo.endTime) {
			dojo.style("importExceptionMessage","display","none");
			dojo.style("importInfoDownload","display","block");
			dojo.style("importInfoEndDateContainer","display","");
            
            dojo.style("importInfoEndDate","display","block");
            dojo.style("importInfoNumImportedObjectContainer","display","");
            dojo.style("importInfoNumImportedAddressContainer","display","");
            dojo.byId("importInfoNumImportedObjects").innerHTML = importInfo.numProcessedObjects;
            dojo.byId("importInfoNumImportedAddresses").innerHTML = importInfo.numProcessedAddresses;


		} else {
			// No job has been started yet
			dojo.style("importExceptionMessage","display","none");
			dojo.style("importInfoDownload","display","none");
			dojo.style("importInfoEndDateContainer","display","none");
            
			dojo.style("importInfoNumImportedObjectContainer","display","none");
            dojo.style("importInfoNumImportedAddressContainer","display","none");
            dojo.byId("importInfoNumImportedObjects").innerHTML = "";
            dojo.byId("importInfoNumImportedAddresses").innerHTML = "";
		}
	} else {
		dojo.byId("importInfoTitle").innerHTML = "<fmt:message key='dialog.admin.import.currentProcessInfo' />";
		dojo.style("importExceptionMessage","display","none");
		dojo.style("importInfoDownload","display","none");
		dojo.style("importInfoEndDateContainer","display","");
		
        dojo.style("cancelImportProcessButton","display","block");
        dojo.style("importInfoNumImportedAddressContainer","display","");
        dojo.style("importInfoNumImportedObjectContainer","display","");  
		dojo.style("importProgressBarContainer","display","block");

        dojo.byId("importInfoNumImportedObjects").innerHTML = importInfo.numProcessedObjects + " / " + importInfo.numObjects;
        dojo.byId("importInfoNumImportedAddresses").innerHTML = importInfo.numProcessedAddresses + " / " + importInfo.numAddresses;

		var progressBar = dijit.byId("importProgressBar");
		progressBar.set("maximum", importInfo.numEntities);
		progressBar.set("value", importInfo.numProcessedEntities);
	}

    console.debug("start time");
	if (importInfo.startTime) {
		dojo.byId("importInfoBeginDate").innerHTML = UtilString.getDateString(importInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	} else {
		dojo.byId("importInfoBeginDate").innerHTML = "";
	}

	if (importInfo.endTime) {
		dojo.byId("importInfoEndDate").innerHTML = UtilString.getDateString(importInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	} else {
		dojo.byId("importInfoEndDate").innerHTML = "";
	}
    //console.debug(importInfo);
}

function jobFinished(importInfo) {
	// endTime != null -> job has an end time means it's done
	// exception != null -> job has an exception -> done
	// startTime == null -> no start time means something went wrong (no previous job exists?)
	return (importInfo.endTime != null || importInfo.exception != null || importInfo.startTime == null);
}

function showLoadingZone() {
    dojo.style("importLoadingZone","visibility","visible");
}

function hideLoadingZone() {
    dojo.style("importLoadingZone","visibility","hidden");
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
    <div class="contentBlockWhite">
    	 <div class="inputContainer">
        	<div id="winNavi" style="top:0px;">
					<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=import-export-2#import-export-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
			</div>
		   <div class="spacer"></div>
           <div id="ImportProcessInfo" class="infobox">
	            <span class="icon">
                    <img src="img/ic_info_download.gif" width="16" height="16" alt="Info" />
                </span>
                <span id="importInfoTitle" class="title">
                </span>
                <div id="ImportProcessInfoContent">
                    <p id="importInfoDownload">
                        <fmt:message key="dialog.admin.import.result" /><a href="javascript:void(0);" onclick="javascript:scriptScopeImport.downloadImportLog();" title="<fmt:message key="dialog.admin.import.log" />"><fmt:message key="general.link" /></a>
                    </p>
                    <p id="importExceptionMessage">
                        <fmt:message key="dialog.admin.import.error" /><a href="javascript:void(0);" onclick="javascript:scriptScopeImport.showJobException();" title="<fmt:message key="dialog.admin.import.errorinfo" />"><fmt:message key="general.link" /></a>
                    </p>
                    <table cellspacing="0">
                        <tr>
                            <td id="importInfoStartDateContainer">
                                <fmt:message key="dialog.admin.import.startTime" />
                            </td>
                            <td id="importInfoBeginDate">
                            </td>
                        </tr>
                        <tr>
                            <td id="importInfoEndDateContainer">
                                <fmt:message key="dialog.admin.import.endTime" />
                            </td>
                            <td id="importInfoEndDate">
                            </td>
                        </tr>
                        <tr>
                            <td id="importInfoNumImportedEntitiesContainer">
                            </td>
                            <td id="importInfoNumImportedEntities">
                            </td>
                        </tr>
                        <tr>
                            <tr><td id="importInfoNumImportedObjectContainer"></td>
                            <td id="importInfoNumImportedObjects"></td></tr>
                            <tr><td id="importInfoNumImportedAddressContainer"></td>
                            <td id="importInfoNumImportedAddresses"></td></tr>
                            <td id="importProgressBarContainer" colspan=2>
                                <div dojoType="dijit.ProgressBar" id="importProgressBar" width="310px" height="10" />
                            </td>
                        </tr>
                    </table>
                    <span id="cancelImportProcessButton" class="button" style="height:20px !important;">
                        <span style="float:right;">
                            <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.import.cancel" />" onClick="javascript:scriptScopeImport.cancelImport();">
                                <fmt:message key="dialog.admin.import.cancel" />
                            </button>
                        </span>
                    </span>
                </div><!-- processInfoContent end -->
            </div><!-- processInfo end -->
        </div><!-- inputContainer end -->
        <div class="spacer"></div>
        <div id="import" class="content">
            <!-- LEFT HAND SIDE CONTENT START -->
            <table class="inputContainer field grey">
                <tr>
                    <td nowrap>
                        <label for="importFile" onclick="javascript:dialog.showContextHelp(arguments[0], 8074)">
                            <fmt:message key="dialog.admin.import.file" />:
                        </label>
                    </td>
                    <td nowrap>
                        <input type="file" id="importFile" size="80" style="width: 100%;"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="importFileType" onclick="javascript:dialog.showContextHelp(arguments[0], 8174)">
                            <fmt:message key="dialog.admin.import.file.type" />:
                        </label>
                    </td>
                    <td>
                        <select dojoType="dijit.form.Select" id="importFileType" style="width:100%;">
                            <option value="igc">InGrid Catalog</option>
                            <option value="csw202">CSW 2.0.2 AP ISO 1.0 (Single Metadata file / ZIP Archive)</option>
                            <option value="arcgis1">ArcGIS ISO-Editor (Single Metadata file / ZIP Archive)</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td  colspan="2">
                        <input type="checkbox" id="publishImportedDatasetsCheckbox" dojoType="dijit.form.CheckBox" />
                        <label onclick="javascript:dialog.showContextHelp(arguments[0], 8075)">
                            <fmt:message key="dialog.admin.import.publish" />
                        </label>
                    </td>
                </tr>
                <tr>
                    <td  colspan="2">
                        <input type="checkbox" id="separateImportCheckbox" dojoType="dijit.form.CheckBox" />
                        <label onclick="javascript:dialog.showContextHelp(arguments[0], 8076)">
                            <fmt:message key="dialog.admin.import.importToSubtree" />
                        </label>
                    </td>
                </tr>
            </table><!-- IMPORT TEILBAUM START -->
           <div class="spacer"></div>
            <!-- SPLIT CONTAINER START -->
            <div id="importTree">
                <span class="label" onclick="javascript:dialog.showContextHelp(arguments[0], 8085)">
                    <label>
                        <fmt:message key="dialog.admin.import.tree" />
                    </label>
                </span><!-- <div dojoType="ingrid:SplitContainer" id="importTreeContainer" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">-->
                <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" id="importTreeContainer" style="height:410px;">
                    <div dojoType="dijit.layout.ContentPane" region="leading" splitter="true" class="inputContainer grey" style="width:200px;">
                        <!-- LEFT HAND SIDE CONTENT IMPORT TEILBAUM START -->
                        <div id="treeImportTree"></div>
                    </div>
                    <!-- LEFT HAND SIDE CONTENT IMPORT TEILBAUM END -->
                    <!-- RIGHT HAND SIDE CONTENT IMPORT TEILBAUM START -->
                    <div dojoType="dijit.layout.ContentPane" region="center" class="inputContainer tab grey">
                        <table style="width:100%;">
                            <tr>
                                <td style="width:5%;"></td>
                                <td style="width:95%;"><label for="importTreeParentObject" onclick="javascript:dialog.showContextHelp(arguments[0], 8077)">
                                    <fmt:message key="dialog.admin.import.parentObject" />
                                </label></td>
                            </tr>
                            <tr>
                                <td><button dojoType="dijit.form.Button" onClick="javascript:scriptScopeImport.selectParentDatasetForTreeImport();">
                                        &nbsp;>&nbsp;
                                    </button>
                                </td>
                                <td><input type="text" id="importTreeParentDataset" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width:100%;" /></td>
                            </tr>
                            <tr>
                                <td></td>
                                <td><label for="importTreeParentAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 8078)">
                                        <fmt:message key="dialog.admin.import.parentAddress" />
                                    </label>
                                </td>
                            </tr>
                            <tr>
                                <td><button dojoType="dijit.form.Button" onClick="javascript:scriptScopeImport.selectParentAddressForTreeImport();">
                                        &nbsp;>&nbsp;
                                    </button>
                                </td>
                                <td><input type="text" id="importTreeParentAddress" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width:100%;" /></td>
                            </tr>
                            
                        </table>
                    </div>
                </div><!-- RIGHT HAND SIDE CONTENT IMPORT TEILBAUM END -->
                <div class="inputContainer">
                    <span class="button">
                        <span style="float:right;">
                            <button id="importButton" dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.import.start" />" onClick="javascript:scriptScopeImport.startImport();">
                                <fmt:message key="dialog.admin.import.start" />
                            </button>
                        </span>
                        <span style="float:right;">
                            <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.import.reset" />" onClick="javascript:scriptScopeImport.resetImport();">
                                <fmt:message key="dialog.admin.import.reset" />
                            </button>
                        </span>
                        <span id="importLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                            <img src="img/ladekreis.gif" />
                        </span>
                    </span>
                    <div class="fill">
                    </div>
                </div>
            </div>
            <!-- IMPORT TEILBAUM END -->
        </div>
    </div>
  <!-- CONTENT END -->

</body>
</html>
