<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">

var pageImport = null;

require([
    "dojo/_base/array",
    "dojo/on",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/string",
    "dijit/registry",
    "ingrid/utils/LoadingZone",
    "ingrid/utils/General",
    "ingrid/utils/String",
    "ingrid/dialog",
    "ingrid/tree/MetadataTree"
], function(array, on, dom, domClass, style, string, registry, LoadingZone, UtilGeneral, UtilString, dialog, MetadataTree) {
            
        var isStartUp = true;
        
        var importTreeSelectedParentDataset = null;
        var importTreeSelectedParentAddress = null;
        
        // Callback for the dwr calls to ImportService.importEntities
        // We don't set a timeout since we don't know how long the fileupload will take.
        // The java method returns when the fileupload is finished. The file is then transferred to the backend and the
        // import job is started. Since we don't know how long this takes, we have to estimate a timespan (3s for now).
        // After that we start querying the backend for information about the current running job
        var importServiceCallback = {
            preHook: LoadingZone.show,
            postHook: LoadingZone.hide,
            callback: function(res){
                console.debug("importServiceCallback");
                refreshImportProcessInfo();
            },
            errorHandler: function(msg, err) {
                console.debug("ERROR in importServiceCallback");
                console.ebug(err);
                LoadingZone.hide();
                if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
                } else {
                    displayErrorMessage(err);
                }
            }
        };
        
        var importTransformationCallback = {
            preHook: LoadingZone.show,
            postHook: LoadingZone.hide,
            callback: function(res){
                checkImportTransformationStatus();
            },
            errorHandler: function(msg, err) {
                console.debug("ERROR in importTransformationCallback");
                //console.debugShallow(err);
                LoadingZone.hide();
                if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
                } else {
                    displayErrorMessage(err);
                }
            }
        };
        
        
        on(_container_, "Load", function(){
            initTree();
            initCheckboxBehaviour();
            refreshImportProcessInfo();
        });
        
        function initCheckboxBehaviour() {
            console.debug("init checkboxes");
            // Only one of the checkboxes is allowed to be active
            // var pubImportCheckbox = registry.byId("publishImportedDatasetsCheckbox");
            // var sepImportCheckbox = registry.byId("radioSeparateImport");
            var importFileType    = registry.byId("importFileType");
        
            /*on(pubImportCheckbox, "click", function(){
                if (sepImportCheckbox.checked) {
                    sepImportCheckbox.setValue(false);
                }
            });
            on(sepImportCheckbox, "click", function(){
                if (pubImportCheckbox.checked) {
                    pubImportCheckbox.setValue(false);
                }
            });*/
            
            on(importFileType, "Change", function(value){
                if (value == "igc") {
                    domClass.add("publishImportedDatasets", "hide");
                } else {
                    domClass.remove("publishImportedDatasets", "hide");
                }
            });
            
            importFileType.set("value", "igc");
            console.debug("init checkboxes ... finished");
        }
        
        function resetImport() {
            registry.byId("importTreeParentDataset").setValue("");
            registry.byId("importTreeParentAddress").setValue("");
            registry.byId("publishImportedDatasetsCheckbox").setValue(false);
            registry.byId("radioSeparateImport").setValue(false);
            registry.byId("importFileType").setValue("igc");
            importTreeSelectedParentDataset = null;
            importTreeSelectedParentAddress = null;
        }
        
        function selectParentDatasetForTreeImport() {
            var selectedNode = registry.byId("treeImportTree").selectedNode;
        
            if (selectedNode && selectedNode.item.nodeAppType == "O" && selectedNode.item.id != "objectRoot") {
                registry.byId("importTreeParentDataset").setValue(selectedNode.item.title);
                importTreeSelectedParentDataset = selectedNode.item;
        
            } else {
                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.import.selectNodeError' />", dialog.WARNING);
            }
        }
        
        function selectParentAddressForTreeImport() {
            var selectedNode = registry.byId("treeImportTree").selectedNode;
        
            if (selectedNode && selectedNode.item.nodeAppType == "A" && selectedNode.item.objectClass === 0) {
                registry.byId("importTreeParentAddress").setValue(selectedNode.item.title);
                importTreeSelectedParentAddress = selectedNode.item;
        
            } else {
                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.import.selectInstitutionError' />", dialog.WARNING);
            }
        };
        
        function downloadImportLog() {
            ImportService.getLastImportLog({
                callback: function(importLog) {
                    dwr.engine.openInDownload(importLog);
                },
                errorHandler: function(errMsg, err) {
                    displayErrorMessage(err);
                }
            });
        }
        
        function showJobException() {
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
        
        function startTreeImport(force) {
            var file                    = dwr.util.getValue("importFile");
            var fileType                = registry.byId("importFileType").getValue();
            var parentObjectUuid        = (importTreeSelectedParentDataset !== null && importTreeSelectedParentDataset.id != "objectRoot") ? importTreeSelectedParentDataset.id : null;
            var parentAddressUuid       = (importTreeSelectedParentAddress !== null) ? importTreeSelectedParentAddress.id : null;
            var publishImportedDatasets = registry.byId("publishImportedDatasetsCheckbox").checked;
            var separateImport          = registry.byId("radioSeparateImport").checked;
            var copyNodeIfPresent       = force; // will be set to true if necessary and wished by the user
        
            console.debug("file: "+file);
            console.debug("parent object uuid: "+parentObjectUuid);
            console.debug("parent address uuid: "+parentAddressUuid);
        
            ImportService.startImportThread(file, fileType, parentObjectUuid, parentAddressUuid, publishImportedDatasets, separateImport, copyNodeIfPresent, {
                callback: function(res){
                    refreshImportProcessInfo();
                },
                errorHandler: function(msg, err){
                    refreshImportProcessInfo();
                    LoadingZone.hide();
                    if (msg.indexOf("USER_HAS_RUNNING_JOBS") != -1) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='operation.error.userHasRunningJobs' />", dialog.WARNING);
                    }
                }
            });
        }
        
        function startTreeImportProtocol() {
            var file = dwr.util.getValue("importFile");
            var fileType = registry.byId("importFileType").getValue();
            var parentObjectUuid = (importTreeSelectedParentDataset !== null && importTreeSelectedParentDataset.id != "objectRoot") ? importTreeSelectedParentDataset.id : null;
            var parentAddressUuid = (importTreeSelectedParentAddress !== null) ? importTreeSelectedParentAddress.id : null;
            var publishImportedDatasets = registry.byId("publishImportedDatasetsCheckbox").checked;
            var separateImport = registry.byId("radioSeparateImport").checked;
        
            console.debug("file: "+file);
            console.debug("parent object uuid: "+parentObjectUuid);
            console.debug("parent address uuid: "+parentAddressUuid);
        
            ImportService.importEntities(file, fileType, parentObjectUuid, parentAddressUuid, publishImportedDatasets, separateImport, {
                callback: function(res) {
                    checkImportTransformationStatus();
                },
                errorHandler: function(msg, err){
                    LoadingZone.hide();
                    displayErrorMessage(err);
                }
            });
            
        }
        
        function startImport() {
            if (dom.byId("importFile").value && validImportNodesSelected()) {
                startTreeImportProtocol();
            } else {
                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.import.selectParentsError' />", dialog.WARNING);
            }
        }
        
        function validImportNodesSelected() {
            return (importTreeSelectedParentDataset !== null
                    && importTreeSelectedParentDataset.id !== "objectRoot"
                    && importTreeSelectedParentAddress !== null);
        }
        
        function cancelImport() {
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
            new MetadataTree({showRoot: false}, "treeImportTree");
        }
        
        //Reload the process info for the last run from the backend
        function refreshImportProcessInfo() {
            ImportService.getImportInfo({
                callback: function(importInfo){
                    console.debug("refreshImportProcessInfo callback");
                    updateImportInfo(importInfo);
                    if (!jobFinished(importInfo)) {
                        setTimeout(refreshImportProcessInfo, 2000);
                    } else {
                        // do not execute on startup
                        if (isStartUp) {
                            isStartUp = false;
                            return;
                        }
                        if (importInfo.exception) {
                            console.debug("Error showing importInfo:");
                            console.debug(importInfo);
                            if (importInfo.exception.message.indexOf("IMPORT_OBJECTS_ALREADY_EXIST") != -1) {
                                handleImportError(importInfo.exception.mdekError.errorInfo);
                            }
                        }
                        
                        // also show a dialog with information
                        if (importInfo.frontendMessages) {
                            var log = importInfo.frontendMessages.replace(/\n/g, '<br />');
                            dialog.show("<fmt:message key='general.info' />", "<fmt:message key='dialog.admin.import.lastProcessInfo' /><br/><br/>" + log, dialog.INFO, null, 850);
                        }
                    }
                },
                errorHandler: function(message, err) {
                    console.debug("ERROR refreshImportProcessInfo callback");
                    displayErrorMessage(err);
                    console.debug("Error: "+ message);
                    // If there's a timeout try again
                    if (err.message != "USER_LOGIN_ERROR") {
                        setTimeout(refreshImportProcessInfo, 2000);
                    }
                }
            });
        }
        
        //Check for transformation status
        function checkImportTransformationStatus() {
            console.debug("check protocol now");
            ImportService.getProtocolInfo({
                callback: function(infoBean){
                    updateReadInputInfo(infoBean);
                    console.debug("Status: " + infoBean.finished);
                    if (infoBean.finished !== true) {
                        console.debug("call again later");
                        setTimeout(checkImportTransformationStatus, 2000);
                    } else {
                        showProtocol(infoBean);
                    }
                }
            });
        }
        
        function showProtocol(infoBean) {
            if(infoBean.inputType != "igc"){
                var protocolMessageToHtml = returnToBr(infoBean.protocol);
                if(protocolMessageToHtml !== null && protocolMessageToHtml.length > 0){
                    dialog.show("<fmt:message key='protocol.title' />", protocolMessageToHtml, dialog.MESSAGE, [{caption:"<fmt:message key='protocol.import' />",action:function(){startTreeImport(false);}}, {caption:"<fmt:message key='protocol.cancel' />",action:dialog.CLOSE_ACTION}], 500, 500);
                }else{
                    dialog.show("<fmt:message key='protocol.title' />", "<fmt:message key='protocol.empty' />", dialog.MESSAGE, [{caption:"<fmt:message key='protocol.import' />",action:function(){startTreeImport(false);}}, {caption:"<fmt:message key='protocol.cancel' />",action:dialog.CLOSE_ACTION}], 500, 500);
                }
            } else {
                startTreeImport(false);
            }
        }
        
        function updateReadInputInfo(infoBean) {
            style.set("importExceptionMessage", "display", "none");
            style.set("importInfoDownload", "display", "none");
            style.set("importInfoStartDateContainer", "display", "none");
            style.set("importInfoBeginDate", "display", "none");
            style.set("importInfoEndDateContainer", "display", "none");
            style.set("importInfoEndDate", "display", "none");
            style.set("importInfoNumImportedAddressContainer", "display", "none");
            style.set("importInfoNumImportedAddresses", "display", "none");
            style.set("importProgressBarContainer", "display", "none");
            
            dom.byId("importInfoNumImportedObjectContainer").innerHTML = "<fmt:message key='dialog.admin.import.progress' />";
            
            // conversion is finished
            if (infoBean.finished === true) {
                registry.byId('importButton').set("disabled", false);
                dom.byId("importInfoTitle").innerHTML ="<fmt:message key='dialog.admin.import.lastProcessInfo' />";
                //style.set("importProgressBarContainer", "display", "block");
                dom.byId("importInfoNumImportedObjects").innerHTML = "";
                //style.set("importInfoNumImportedObjects", "display", "none");
            // conversion is still in progress
            } else {
                registry.byId('importButton').set("disabled", true);
                dom.byId("importInfoTitle").innerHTML = "<fmt:message key='dialog.admin.import.currentProcessInfo' />";
                style.set("importInfoNumImportedObjectContainer", "display", "");
                style.set("importProgressBarContainer", "display", "block");
                
                dom.byId("importInfoNumImportedObjects").innerHTML = infoBean.dataProcessed + " / 100%";
                
                var progressBar = registry.byId("importProgressBar");
                progressBar.set("maximum", 100);
                progressBar.set("value", infoBean.dataProcessed);
            }
        }
        
        function updateImportInfo(importInfo) {
            console.debug("updateImportInfo");
            dom.byId("importInfoNumImportedObjectContainer").innerHTML = "<fmt:message key='dialog.admin.import.numObjects' />";
            dom.byId("importInfoNumImportedAddressContainer").innerHTML = "<fmt:message key='dialog.admin.import.numAddresses' />";
        
            style.set("importInfoStartDateContainer", "display", "");
            style.set("importInfoBeginDate", "display", "block");
            style.set("importInfoNumImportedAddresses", "display", "");
        
            console.debug("job finished?");
            style.set("importMoreContainer", "display", "none");
            if (jobFinished(importInfo)) {
                dom.byId("importInfoTitle").innerHTML = "<fmt:message key='dialog.admin.import.lastProcessInfo' />";
                style.set("importProgressBarContainer","display","none");
                style.set("cancelImportProcessButton","display","none");
                style.set("importMoreInfo","display","none");
        
                if (importInfo.exception) {
                    style.set("importExceptionMessage","display","block");
                    style.set("importInfoDownload","display","none");
                    style.set("importInfoEndDateContainer","display","none");
                    
                    style.set("importInfoNumImportedObjectContainer","display","none");
                    //style.set("importInfoNumImportedObjects","display","none");
                    style.set("importInfoNumImportedAddressContainer","display","none");
                    dom.byId("importInfoNumImportedObjects").innerHTML = "";
                    dom.byId("importInfoNumImportedAddresses").innerHTML = "";
                    dom.byId("importMoreInfo").innerHTML = addErrorInfo(importInfo.exception);
                    style.set("importMoreContainer", "display", "block");
        
                } else if (importInfo.endTime) {
                    style.set("importExceptionMessage","display","none");
                    style.set("importInfoDownload","display","block");
                    style.set("importInfoEndDateContainer","display","");
                    
                    style.set("importInfoEndDate","display","block");
                    style.set("importInfoNumImportedObjectContainer","display","");
                    style.set("importInfoNumImportedAddressContainer","display","");
                    dom.byId("importInfoNumImportedObjects").innerHTML = importInfo.numProcessedObjects;
                    dom.byId("importInfoNumImportedAddresses").innerHTML = importInfo.numProcessedAddresses;
                    dom.byId("importMoreInfo").innerHTML = importInfo.frontendMessages ? importInfo.frontendMessages.replace(/\n/g, '<br />') : "<fmt:message key='dialog.admin.import.noMessages' />";
                    style.set("importMoreContainer", "display", "block");
        
                } else {
                    // No job has been started yet
                    style.set("importExceptionMessage","display","none");
                    style.set("importInfoDownload","display","none");
                    style.set("importInfoEndDateContainer","display","none");
                    
                    style.set("importInfoNumImportedObjectContainer","display","none");
                    style.set("importInfoNumImportedAddressContainer","display","none");
                    dom.byId("importInfoNumImportedObjects").innerHTML = "";
                    dom.byId("importInfoNumImportedAddresses").innerHTML = "";
                }
            } else {
                dom.byId("importInfoTitle").innerHTML = "<fmt:message key='dialog.admin.import.currentProcessInfo' />";
                style.set("importExceptionMessage","display","none");
                style.set("importInfoDownload","display","none");
                style.set("importInfoEndDateContainer","display","");
                
                style.set("cancelImportProcessButton","display","block");
                style.set("importInfoNumImportedAddressContainer","display","");
                style.set("importInfoNumImportedObjectContainer","display","");
                style.set("importProgressBarContainer","display","block");
        
                dom.byId("importInfoNumImportedObjects").innerHTML = importInfo.numProcessedObjects + " / " + importInfo.numObjects;
                dom.byId("importInfoNumImportedAddresses").innerHTML = importInfo.numProcessedAddresses + " / " + importInfo.numAddresses;
        
                var progressBar = registry.byId("importProgressBar");
                progressBar.set("maximum", importInfo.numEntities);
                progressBar.set("value", importInfo.numProcessedEntities);
            }
        
            console.debug("start time");
            if (importInfo.startTime) {
                dom.byId("importInfoBeginDate").innerHTML = UtilString.getDateString(importInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
            } else {
                dom.byId("importInfoBeginDate").innerHTML = "";
            }
        
            if (importInfo.endTime) {
                dom.byId("importInfoEndDate").innerHTML = UtilString.getDateString(importInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
            } else {
                dom.byId("importInfoEndDate").innerHTML = "";
            }
            //console.debug(importInfo);
        }
        
        function jobFinished(importInfo) {
            // endTime != null -> job has an end time means it's done
            // exception != null -> job has an exception -> done
            // startTime == null -> no start time means something went wrong (no previous job exists?)
            return (importInfo.endTime !== null || importInfo.exception !== null || importInfo.startTime === null);
        }
        
        function _prepareErrorInfo(errorInfo) {
            var msg = "<ul>";
            var entities = errorInfo.objEntities ? errorInfo.objEntities : errorInfo.addrEntities;
            
            if (entities) {
                array.forEach(entities, function(obj) {
                    msg += "<li>" + obj.title + "</li>";
                });
            }
            msg += "</ul>";
            return msg;
        }
        
        function handleImportError(errorInfo) {
            var msg = _prepareErrorInfo(errorInfo);
            dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.import.alreadyExistError' />", [msg]), dialog.WARNING, [{caption:"<fmt:message key='general.no' />",action:dialog.CLOSE_ACTION}, {caption:"<fmt:message key='general.yes' />",action:function(){startTreeImport(true);}}]);
        }
        
        function addErrorInfo(exception) {
            if (exception.message.indexOf("IMPORT_OBJECTS_ALREADY_EXIST") != -1) {
                var msg = string.substitute("<fmt:message key='dialog.admin.import.alreadyExistErrorInfo' />", [_prepareErrorInfo(exception.mdekError.errorInfo)]);
                return msg;
            } else {
                return exception.message;
            }
        }
        
        /* function showLoadingZone() {
            style.set("importLoadingZone","visibility","visible");
        }
        
        function hideLoadingZone() {
            style.set("importLoadingZone","visibility","hidden");
        } */
        
        function toggleMoreInfo() {
            var currentState = style.get("importMoreInfo", "display");
            if (currentState == "none") {
                style.set("importMoreInfo","display","block");
            } else {
                style.set("importMoreInfo","display","none");
            }
            
        }
        
        function returnToBr(string) {
            if(string !== null){
                return string.replace(/[\r\n]/g, "<br />");
            }else{
                return null;
            }
        }

        /**
         * PUBLIC METHODS
         */
        
        pageImport = {
            cancelImport: cancelImport,
            downloadImportLog: downloadImportLog,
            showJobException: showJobException,
            toggleMoreInfo: toggleMoreInfo,
            selectParentDatasetForTreeImport: selectParentDatasetForTreeImport,
            selectParentAddressForTreeImport: selectParentAddressForTreeImport,
            startImport: startImport,
            resetImport: resetImport
        };
    });
</script>
</head>

<body>

    <!-- CONTENT START -->
    <div class="contentBlockWhite">
         <div class="inputContainer">
            <div id="winNavi" style="top:0px;">
                    <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=import-export-2#import-export-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
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
                        <fmt:message key="dialog.admin.import.result" /><a href="#" onclick="pageImport.downloadImportLog()" title="<fmt:message key="dialog.admin.import.log" />"><fmt:message key="general.link" /></a>
                    </p>
                    <p id="importExceptionMessage">
                        <fmt:message key="dialog.admin.import.error" /><a href="#" onclick="pageImport.showJobException()" title="<fmt:message key="dialog.admin.import.errorinfo" />"><fmt:message key="general.link" /></a>
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
                                <div data-dojo-type="dijit/ProgressBar" id="importProgressBar" width="310px" height="10" />
                            </td>
                        </tr>
                    </table>
                    <div id="importMoreContainer">
                        <p><a href="javascript:void(0);" onclick="pageImport.toggleMoreInfo();" title="<fmt:message key="dialog.admin.import.log" />"><fmt:message key="dialog.admin.import.moreInfo" /></a></p>
                        <p id="importMoreInfo" style="display:none;"></p>
                    </div>
                    <span id="cancelImportProcessButton" class="button" style="height:20px !important;">
                        <span style="float:right;">
                            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.import.cancel" />" onclick="pageImport.cancelImport();">
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
                        <label for="importFile" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8074)">
                            <fmt:message key="dialog.admin.import.file" />:
                        </label>
                    </td>
                    <td nowrap>
                        <input type="file" id="importFile" size="80" style="width: 100%;"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="importFileType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8174)">
                            <fmt:message key="dialog.admin.import.file.type" />:
                        </label>
                    </td>
                    <td>
                        <!-- Select box all in one line ! Due to strange bug with dojo and firefox/chrome newest versions ! ??? -->
                        <select data-dojo-type="dijit/form/Select" id="importFileType" style="width:100%;"><option value="igc">InGrid Catalog</option><option value="csw202">CSW 2.0.2 AP ISO 1.0 (Single Metadata file / ZIP Archive)</option><option value="arcgis1">ArcGIS ISO-Editor (Single Metadata file / ZIP Archive)</option></select>
                    </td>
                </tr>
                <tr id="publishImportedDatasets" class="hide">
                    <td  colspan="2">
                        <input type="checkbox" id="publishImportedDatasetsCheckbox" data-dojo-type="dijit/form/CheckBox" />
                        <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8075)">
                            <fmt:message key="dialog.admin.import.publish" />
                        </label>
                    </td>
                </tr>
                <tr>
                    <td  colspan="2">
                        <input type="radio" name="importType" id="radioOverwrite" data-dojo-type="dijit/form/RadioButton" value="overwrite" checked />
                        <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8209)">
                            <fmt:message key="dialog.admin.import.overwrite" />
                        </label>
                    </td>
                </tr>
                <tr>
                    <td  colspan="2">
                        <input type="radio" name="importType" id="radioSeparateImport" data-dojo-type="dijit/form/RadioButton" value="separate" />
                        <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8076)">
                            <fmt:message key="dialog.admin.import.importToSubtree" />
                        </label>
                    </td>
                </tr>
            </table><!-- IMPORT TEILBAUM START -->
           <div class="spacer"></div>
            <!-- SPLIT CONTAINER START -->
            <div id="importTree">
                <span class="label" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8085)">
                    <label>
                        <fmt:message key="dialog.admin.import.tree" />
                    </label>
                </span><!-- <div data-dojo-type="ingrid:SplitContainer" id="importTreeContainer" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">-->
                <div data-dojo-type="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" id="importTreeContainer" style="height:410px;">
                    <div data-dojo-type="dijit/layout/ContentPane" region="leading" splitter="true" class="inputContainer grey" style="width:200px;">
                        <!-- LEFT HAND SIDE CONTENT IMPORT TEILBAUM START -->
                        <div id="treeImportTree"></div>
                    </div>
                    <!-- LEFT HAND SIDE CONTENT IMPORT TEILBAUM END -->
                    <!-- RIGHT HAND SIDE CONTENT IMPORT TEILBAUM START -->
                    <div data-dojo-type="dijit/layout/ContentPane" region="center" class="inputContainer tab grey">
                        <table style="width:100%;">
                            <tr>
                                <td style="width:5%;"></td>
                                <td style="width:95%;"><label for="importTreeParentObject" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8077)">
                                    <fmt:message key="dialog.admin.import.parentObject" />
                                </label></td>
                            </tr>
                            <tr>
                                <td><button data-dojo-type="dijit/form/Button" onclick="pageImport.selectParentDatasetForTreeImport()">
                                        &nbsp;>&nbsp;
                                    </button>
                                </td>
                                <td><input type="text" id="importTreeParentDataset" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" /></td>
                            </tr>
                            <tr>
                                <td></td>
                                <td><label for="importTreeParentAddress" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8078)">
                                        <fmt:message key="dialog.admin.import.parentAddress" />
                                    </label>
                                </td>
                            </tr>
                            <tr>
                                <td><button data-dojo-type="dijit/form/Button" onclick="pageImport.selectParentAddressForTreeImport()">
                                        &nbsp;>&nbsp;
                                    </button>
                                </td>
                                <td><input type="text" id="importTreeParentAddress" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" /></td>
                            </tr>
                            
                        </table>
                    </div>
                </div><!-- RIGHT HAND SIDE CONTENT IMPORT TEILBAUM END -->
                <div class="inputContainer">
                    <span class="button">
                        <span style="float:right;">
                            <button id="importButton" data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.import.start" />" onclick="pageImport.startImport()">
                                <fmt:message key="dialog.admin.import.start" />
                            </button>
                        </span>
                        <span style="float:right;">
                            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.import.reset" />" onclick="pageImport.resetImport()">
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
