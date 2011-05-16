<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
            var scriptScopeSearchTerms = _container_;
            
            var resultsPerPage = 20;
            var pageNav = new PageNavigation({
                resultsPerPage: resultsPerPage,
                infoSpan: dojo.byId("snsUpdateResultInfo"),
                pagingSpan: dojo.byId("snsUpdateResultPaging")
            });
            
            var snsUpdateResult = [];
            
			createDOMElements();
			
			dojo.connect(_container_, "onLoad", function(){
				refreshSNSUpdateProcessInfo();
				dojo.connect(pageNav, "onPageSelected", updateSNSResultTable);
			});
                
			
			function createDOMElements(){
				var resultListThesaurusTableStructure = [
					{field: 'label',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.description' />",width: '254px', editable: true},
					{field: 'type',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.type' />",width: '100px', editable: true},
					{field: 'action',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.action' />",width: '450px', editable: true},
					{field: 'objects',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.objects' />",width: '80px', editable: true},
					{field: 'addresses',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.addresses' />",width: 'auto', editable: true}		
				];
			    
			    createDataGrid("resultListThesaurusTable", null, resultListThesaurusTableStructure, null);
			}
            
            scriptScopeSearchTerms.startSNSUpdateJob = function(){
                var file = dwr.util.getValue("snsUpdateFile");

                CatalogManagementService.startSNSUpdateJob(file, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(){
                        setTimeout("refreshSNSUpdateProcessInfo()", 1000);
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.searchTerms.jobStartHint' />", dialog.INFO);
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("error: " + errMsg);
                    }
                });
            }
            
            scriptScopeSearchTerms.cancelSNSUpdateJob = function(){
                CatalogManagementService.stopSNSUpdateJob({
                    callback: function(){
                        console.debug("Job stopped.");
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("error: " + errMsg);
                    }
                });
            }
            
            
            refreshSNSUpdateProcessInfo = function(){
                CatalogManagementService.getSNSUpdateJobInfo({
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(jobInfo){
                        updateSNSUpdateJobInfo(jobInfo);
                        if (!jobFinished(jobInfo)) {
                            setTimeout("refreshSNSUpdateProcessInfo()", 1000);
                        }
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.log("Error: " + message);
                        // If there's a timeout try again
                        if (err.message != "USER_LOGIN_ERROR") {
                            setTimeout("refreshSNSUpdateProcessInfo()", 1000);
                        } else {
                            //hideLoadingZone();
                        }
                    }
                });
            };
            
            function updateSNSUpdateJobInfo(jobInfo){
                if (jobFinished(jobInfo)) {
                    hideLoadingZone();
                    
                    dojo.byId("snsUpdateProcessInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.searchTerms.updateLastProcessInfo' />";
                    //dojo.html.setVisibility("cancelSNSUpdateProcessButton", false);
                    //dojo.html.setVisibility("snsUpdateProcessStatusRow", false);
                    dojo.byId("cancelSNSUpdateProcessButton").style.visibility = "hidden";
                    dojo.byId("snsUpdateProcessStatusRow").style.visibility = "hidden";
                    
                    if (jobInfo.startTime == null) {
                        // Job was never executed
                        dojo.byId("snsUpdateProcessStart").innerHTML = "";
                        dojo.byId("snsUpdateProcessEnd").innerHTML = "";
                        dojo.byId("snsUpdateProcessNumEntities").innerHTML = "";
                        
                    }
                    else {
                        dojo.byId("snsUpdateProcessStart").innerHTML = jobInfo.startTime;
                        dojo.byId("snsUpdateProcessEnd").innerHTML = jobInfo.endTime;
                        dojo.byId("snsUpdateProcessNumEntities").innerHTML = jobInfo.numEntities;
                    }
                    
                    var numTerms = jobInfo.snsUpdateResults.length;
                    UtilList.addSNSTopicLabels(jobInfo.snsUpdateResults);
                    snsUpdateResult = jobInfo.snsUpdateResults;
                    
                    pageNav.reset();
                    pageNav.setTotalNumHits(numTerms);
                    updateSNSResultTable();
                    
                }
                else {
                    showLoadingZone();
                    
                    //dojo.html.setVisibility("cancelSNSUpdateProcessButton", true);
                    //dojo.html.setVisibility("snsUpdateProcessStatusRow", true);
                    dojo.byId("cancelSNSUpdateProcessButton").style.visibility = "visible";
                    dojo.byId("snsUpdateProcessStatusRow").style.visibility = "visible";
                    
                    dojo.byId("snsUpdateProcessInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.searchTerms.updateCurrentProcessInfo' />";
                    
                    if (jobInfo.description != null && jobInfo.description.indexOf("snsUpdateJob") != -1) {
                        dojo.byId("snsUpdateProcessStatus").innerHTML = "<fmt:message key='dialog.admin.catalog.management.searchTerms.stateSNSUpdate' />";
                        
                    }
                    else {
                        dojo.byId("snsUpdateProcessStatus").innerHTML = jobInfo.description;
                    }
                    
                    dojo.byId("snsUpdateProcessStart").innerHTML = jobInfo.startTime;
                    dojo.byId("snsUpdateProcessEnd").innerHTML = "";
                    dojo.byId("snsUpdateProcessNumEntities").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
                }
            }
            
            function jobFinished(jobInfo){
                return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
            }
            
            function showLoadingZone(){
                dojo.byId("snsUpdateLoadingZone").style.visibility = "visible";
                
            }
            
            function hideLoadingZone(){
                dojo.byId("snsUpdateLoadingZone").style.visibility = "hidden";
            }
            
            // Paging
            function updateSNSResultTable(){
                var startHit = pageNav.getStartHit();
                var currentView = snsUpdateResult.slice(startHit, startHit + resultsPerPage);
                //dijit.byId("resultListThesaurusTable").store.setData(currentView);
				UtilStore.updateWriteStore("resultListThesaurusTable", currentView);
                pageNav.updateDomNodes();
            }
            
            scriptScopeSearchTerms.downloadAsCSV = function(){
                CatalogManagementService.getSNSUpdateResultAsCSV({
                    callback: function(csvFile){
                        dwr.engine.openInDownload(csvFile);
                    }
                });
            }
        </script>
    </head>
    <body>
    <!-- CONTENT START -->
    <div id="contentSection" class="contentBlockWhite">
        <div id="searchTermsContent" class="content">
            <!-- INFO START -->
            <div class="inputContainer grey field">
	            <div id="winNavi" style="top:0;">
	               <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=overall-catalog-management-7#overall-catalog-management-7', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	           </div>
                <span class="label">
                    <label for="importFile" onclick="javascript:dialog.showContextHelp(arguments[0], 8055)">
						<fmt:message key="dialog.admin.catalog.management.searchTerms.selectUpdateDataset" />
                    </label>
                </span>
				<span><input type="file" id="snsUpdateFile" name="snsUpdateFile" size="80" style="width:100%;"/></span>
                <br/>
                <br/>
                <span><fmt:message key="dialog.admin.catalog.management.searchTerms.updateHint" /></span>
            </div>
            <div class="inputContainer" style="padding-bottom: 10px;">
                <span class="button""><span style="float:right;">
                        <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.startUpdate" />" onClick="javascript:scriptScopeSearchTerms.startSNSUpdateJob();">
                            <fmt:message key="dialog.admin.catalog.management.searchTerms.startUpdate" />
                        </button>
                    </span><span id="snsUpdateLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
            </div>
            <div class="inputContainer noSpaceBelow">
                <div id="searchTermsInfo" class="infobox">
                    <span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span><span class="title" id="snsUpdateProcessInfo"></span>
                    <a href="javascript:toggleInfo('searchTermsInfo');" title="<fmt:message key="general.info.open" />"><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
                    <div id="searchTermsInfoContent">
                        <table cellspacing="0">
                            <tr id="snsUpdateProcessStatusRow">
                                <td>
                                    <fmt:message key="dialog.admin.catalog.management.searchTerms.status" />
                                </td>
                                <td id="snsUpdateProcessStatus">
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <fmt:message key="dialog.admin.catalog.management.searchTerms.startTime" />
                                </td>
                                <td id="snsUpdateProcessStart">
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <fmt:message key="dialog.admin.catalog.management.searchTerms.endTime" />
                                </td>
                                <td id="snsUpdateProcessEnd">
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <fmt:message key="dialog.admin.catalog.management.searchTerms.numTerms" />
                                </td>
                                <td id="snsUpdateProcessNumEntities">
                                </td>
                            </tr>
                        </table>
                        <span id="cancelSNSUpdateProcessButton" class="button"><span style="float:right;">
                                <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.cancel" />" onClick="javascript:scriptScopeSearchTerms.cancelSNSUpdateJob();">
                                    <fmt:message key="dialog.admin.catalog.management.searchTerms.cancel" />
                                </button>
                            </span></span>
                    </div>
            </div>
            <!-- INFO END --><!-- LEFT HAND SIDE CONTENT START -->
            <div class="inputContainer noSpaceBelow">
                <!-- TABLE START -->
                <div class="inputContainer noSpaceBelow">
                    <span class="functionalLink" style="margin-top:0"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScopeSearchTerms.downloadAsCSV();" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" /></a></span>
                    <div class="listInfo" style="clear:both;">
                        <span id="snsUpdateResultInfo" class="searchResultsInfo">&nbsp;</span>
                        <span id="snsUpdateResultPaging" class="searchResultsPaging">&nbsp;</span>
                        <div class="fill">
                        </div>
                    </div>
                    <div id="resultListThesaurusTable" autoHeight="20" contextMenu="none">
                    </div>
                </div>
                <!-- TABLE END -->
            </div>
            <!-- LEFT HAND SIDE CONTENT END -->
        </div>
    </div>
    </div>
    <!-- CONTENT END -->
</body>
</html>
