<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
    <script type="text/javascript">
        var pageSearchTerms = _container_;
        
        require([
            "dojo/on",
            "dojo/aspect",
            "dojo/dom",
            "ingrid/layoutCreator",
            "ingrid/utils/LoadingZone",
            "ingrid/utils/Store",
            "ingrid/utils/PageNavigation",
            "ingrid/utils/List",
            "ingrid/utils/UI",
            "ingrid/dialog"
        ], function(on, aspect, dom, layoutCreator, LoadingZone, UtilStore, navigation, UtilList, UtilUI, dialog) {

            var resultsPerPage = 20;
            var pageNav = new navigation.PageNavigation({
                resultsPerPage: resultsPerPage,
                infoSpan: dom.byId("snsUpdateResultInfo"),
                pagingSpan: dom.byId("snsUpdateResultPaging")
            });
            
            var snsUpdateResult = [];
            
			createDOMElements();
			
			on(_container_, "Load", function(){
				refreshSNSUpdateProcessInfo();
				aspect.after(pageNav, "onPageSelected", updateSNSResultTable);
			});
                
			
			function createDOMElements(){
				var resultListThesaurusTableStructure = [
					{field: 'label',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.description' />",width: '254px', editable: true},
					{field: 'type',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.type' />",width: '100px', editable: true},
					{field: 'action',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.action' />",width: '450px', editable: true},
					{field: 'objects',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.objects' />",width: '80px', editable: true},
					{field: 'addresses',name: "<fmt:message key='dialog.admin.catalog.management.searchTerms.addresses' />",width: 'auto', editable: true}		
				];
			    
				layoutCreator.createDataGrid("resultListThesaurusTable", null, resultListThesaurusTableStructure, null);
			}
            
			pageSearchTerms.startSNSUpdateJob = function(){
                CatalogManagementService.startSNSUpdateJob(userLocale, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(){
                        setTimeout(refreshSNSUpdateProcessInfo, 1000);
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.searchTerms.jobStartHint' />", dialog.INFO);
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("error: " + errMsg);
                    }
                });
            };
            
			pageSearchTerms.cancelSNSUpdateJob = function(){
                CatalogManagementService.stopSNSUpdateJob({
                    callback: function(){
                        console.debug("Job stopped.");
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("error: " + errMsg);
                    }
                });
            };
            
            
            function refreshSNSUpdateProcessInfo(){
                CatalogManagementService.getSNSUpdateJobInfo({
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(jobInfo){
                        updateSNSUpdateJobInfo(jobInfo);
                        if (!jobFinished(jobInfo)) {
                            setTimeout(refreshSNSUpdateProcessInfo, 1000);
                        }
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.log("Error: " + message);
                        // If there's a timeout try again
                        if (err.message != "USER_LOGIN_ERROR") {
                            setTimeout(refreshSNSUpdateProcessInfo, 1000);
                        } else {
                            //LoadingZone.hide();
                        }
                    }
                });
            };
            
            function updateSNSUpdateJobInfo(jobInfo){
                if (jobFinished(jobInfo)) {
                    LoadingZone.hide();
                    
                    dom.byId("snsUpdateProcessInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.searchTerms.updateLastProcessInfo' />";
                    //dojo.html.setVisibility("cancelSNSUpdateProcessButton", false);
                    //dojo.html.setVisibility("snsUpdateProcessStatusRow", false);
                    dom.byId("cancelSNSUpdateProcessButton").style.visibility = "hidden";
                    dom.byId("snsUpdateProcessStatusRow").style.visibility = "hidden";
                    
                    if (jobInfo.startTime == null) {
                        // Job was never executed
                        dom.byId("snsUpdateProcessStart").innerHTML = "";
                        dom.byId("snsUpdateProcessEnd").innerHTML = "";
                        dom.byId("snsUpdateProcessNumEntities").innerHTML = "";
                        
                    }
                    else {
                        dom.byId("snsUpdateProcessStart").innerHTML = jobInfo.startTime;
                        dom.byId("snsUpdateProcessEnd").innerHTML = jobInfo.endTime;
                        dom.byId("snsUpdateProcessNumEntities").innerHTML = jobInfo.numEntities;
                    }
                    
                    var numTerms = jobInfo.snsUpdateResults.length;
                    UtilList.addSNSTopicLabels(jobInfo.snsUpdateResults);
                    snsUpdateResult = jobInfo.snsUpdateResults;
                    
                    pageNav.reset();
                    pageNav.setTotalNumHits(numTerms);
                    updateSNSResultTable();
                    
                }
                else {
                    LoadingZone.show();
                    
                    //dojo.html.setVisibility("cancelSNSUpdateProcessButton", true);
                    //dojo.html.setVisibility("snsUpdateProcessStatusRow", true);
                    dom.byId("cancelSNSUpdateProcessButton").style.visibility = "visible";
                    dom.byId("snsUpdateProcessStatusRow").style.visibility = "visible";
                    
                    dom.byId("snsUpdateProcessInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.searchTerms.updateCurrentProcessInfo' />";
                    
                    if (jobInfo.description != null && jobInfo.description.indexOf("snsUpdateJob") != -1) {
                        dom.byId("snsUpdateProcessStatus").innerHTML = "<fmt:message key='dialog.admin.catalog.management.searchTerms.stateSNSUpdate' />";
                        
                    }
                    else {
                        dom.byId("snsUpdateProcessStatus").innerHTML = jobInfo.description;
                    }
                    
                    dom.byId("snsUpdateProcessStart").innerHTML = jobInfo.startTime;
                    dom.byId("snsUpdateProcessEnd").innerHTML = "";
                    dom.byId("snsUpdateProcessNumEntities").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
                }
            }
            
            function jobFinished(jobInfo){
                return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
            }
            
            /* function showLoadingZone(){
                dom.byId("snsUpdateLoadingZone").style.visibility = "visible";
                
            }
            
            function hideLoadingZone(){
                dom.byId("snsUpdateLoadingZone").style.visibility = "hidden";
            } */
            
            // Paging
            function updateSNSResultTable(){
                var startHit = pageNav.getStartHit();
                var currentView = snsUpdateResult.slice(startHit, startHit + resultsPerPage);
				UtilStore.updateWriteStore("resultListThesaurusTable", currentView);
                pageNav.updateDomNodes();
            }
            
            pageSearchTerms.downloadAsCSV = function(){
                CatalogManagementService.getSNSUpdateResultAsCSV({
                    callback: function(csvFile){
                        dwr.engine.openInDownload(csvFile);
                    }
                });
            }
            });
        </script>
    </head>
    <body>
    <!-- CONTENT START -->
    <div id="contentSection" class="contentBlockWhite">
        <div id="searchTermsContent" class="content">
            <!-- INFO START -->
	            <div id="winNavi" style="top:0;">
	               <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-7#overall-catalog-management-7', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	           </div>
            <div class="inputContainer" style="padding-bottom: 10px;">
                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.startUpdate" />" onclick="pageSearchTerms.startSNSUpdateJob();">
                            <fmt:message key="dialog.admin.catalog.management.searchTerms.startUpdate" />
                        </button>
                <span id="snsUpdateLoadingZone" style="margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span>
            </div>
            <div class="inputContainer noSpaceBelow">
                <div id="searchTermsInfo" class="infobox">
                    <span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span><span class="title" id="snsUpdateProcessInfo"></span>
                    <a href="#" onclick="require('ingrid/utils/UI').toggleInfo('searchTermsInfo')" title="<fmt:message key="general.info.open" />"><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
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
                                <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.cancel" />" onclick="pageSearchTerms.cancelSNSUpdateJob();">
                                    <fmt:message key="dialog.admin.catalog.management.searchTerms.cancel" />
                                </button>
                            </span></span>
                    </div>
            </div>
            <!-- INFO END --><!-- LEFT HAND SIDE CONTENT START -->
            <div class="inputContainer noSpaceBelow">
                <!-- TABLE START -->
                <div class="inputContainer noSpaceBelow">
                    <span class="functionalLink" style="margin-top:0"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="pageSearchTerms.downloadAsCSV();" title="<fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.searchTerms.saveAsCSV" /></a></span>
                    <div class="listInfo" style="clear:both;">
                        <span id="snsUpdateResultInfo" class="searchResultsInfo">&nbsp;</span>
                        <span id="snsUpdateResultPaging" class="searchResultsPaging">&nbsp;</span>
                        <div class="fill">
                        </div>
                    </div>
                    <div id="resultListThesaurusTable" autoHeight="20" contextMenu="none" defaultHideScrollbar="true">
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
