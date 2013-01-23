<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.ProgressBar");
dojo.require("dijit.form.ValidationTextBox");
var scriptScopeUrls = _container_;

// Use the same store for both tables. The First table has to be reinitialised so the new store
// gets registered properly
//var mainStore = dijit.byId("urlListTable1").store;
//dijit.byId("urlListTable2").setStore(mainStore);

// ATTENTION: use two stores again since older/slower browser (IE7/FF2) have problems with big tables
//            only show first 100 entries so do a prefiltering on the data

//var filterTable = dijit.byId("urlListTable2");
//filterTable.setFilter("errorCode", function(errorCode) { return ("VALID" != errorCode); });

var urlListTable1Structure = [
	{field: 'errorCode',name: "<fmt:message key='dialog.admin.catalog.management.urls.error' />",width: '106px'},
	{field: 'icon',name: '&nbsp;',width: '23px'},
	{field: 'objectName',name: "<fmt:message key='dialog.admin.catalog.management.urls.objectName' />",width: '214px'},
	{field: 'url',name: 'URL',width: '374px'},
	{field: 'urlReferenceDescription',name: "<fmt:message key='dialog.admin.catalog.management.urls.description' />",width: '200px'}
];
createDataGrid("urlListTable1", null, urlListTable1Structure, null);
createDataGrid("urlListTable2", null, dojo.clone(urlListTable1Structure), null);
//var data = UtilGrid.getTableData("urlListTable1");
//UtilGrid.getTableData("urlListTable2").setItems(data);
//UtilGrid.getTableData("urlListTable2").setFilter(function(item) { return ("VALID" != item.errorCode); });

var urlListTable3Structure = [
    {field: 'errorCode',name: "<fmt:message key='dialog.admin.catalog.management.urls.error' />",width: '120px'},
    {field: 'count',name: "<fmt:message key='dialog.admin.catalog.management.urls.count' />",width: '500px'}
];
createDataGrid("urlListTable3", null, dojo.clone(urlListTable3Structure), null);
    
dojo.connect(_container_, "onLoad", function(){
    // only show textbox and button for replacing url if first two tables are selected
    dojo.connect(dijit.byId("urlList3"), "onShow", function() {
        dojo.style("replaceContainer", "display", "none");
    });
    dojo.connect(dijit.byId("urlList3"), "onHide", function() {
        dojo.style("replaceContainer", "display", "block");
    });

	// script global variables to remember tab names where the number of read URLs will be attached
    allUrlsTitle = dijit.byId("urlList1").title;
    allErrorUrlsTitle = dijit.byId("urlList2").title;
    summaryErrorUrlsTitle = dijit.byId("urlList3").title;
	//dojo.html.hide(dojo.byId("urlsProgressBarContainer"));
	dojo.style("urlsProgressBarContainer", "display", "none");
	refreshUrlProcessInfo();
});


scriptScopeUrls.startUrlsJob = function() {
	UtilGrid.setTableData("urlListTable1", []);
	UtilGrid.setTableData("urlListTable2", []);
    UtilGrid.setTableData("urlListTable3", []);

	CatalogManagementService.startUrlValidatorJob({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function() {
			setTimeout("refreshUrlProcessInfo();", 2000);
		},
		errorHandler: function(msg, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+msg);
		}
	});
}

scriptScopeUrls.cancelUrlsJob = function() {
	CatalogManagementService.stopUrlValidatorJob({
		errorHandler: function(msg, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+msg);
		}
	});
}

scriptScopeUrls.replaceUrl = function() {
    var currentTab   = getCurrentTabName();
	var urlTable     = getCurrentTable();
	var selectedData = UtilGrid.getSelectedData(urlTable);
	var replaceUrl   = dojo.trim(dijit.byId("urlReplace").getValue());
	// TODO send data to backend

	if (selectedData && selectedData.length != 0 && replaceUrl.length != 0) {
		var sourceUrls = [];
		dojo.forEach(selectedData, function(element) {
			sourceUrls.push( { objectUuid: element.objectUuid, url:element.url } );
		});

		var def = replaceUrlDef(sourceUrls, replaceUrl);
		def.addCallback(function() { return updateDBUrlJobInfoDef(sourceUrls, replaceUrl); });
		def.addCallback(refreshUrlProcessInfo);
		def.addCallback(function() { dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.management.urls.success' />", dialog.INFO) });
	}
}

function replaceUrlDef(sourceUrls, targetUrl) {
	var def = new dojo.Deferred();

	CatalogManagementService.replaceUrls(sourceUrls, targetUrl, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function() {
			def.callback();
		},
		errorHandler: function(msg, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+msg);
			def.errback();
		}
	});

	return def;
}

function updateDBUrlJobInfoDef(sourceUrls, targetUrl) {
	var def = new dojo.Deferred();

	CatalogManagementService.updateDBUrlJobInfo(sourceUrls, targetUrl, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function() {
			def.callback();
		},
		errorHandler: function(msg, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+msg);
			def.errback();
		}
	});

	return def;
}

function getCurrentTabName() {
    return dijit.byId("urlLists").selectedChildWidget.id;
}

function getCurrentTable() {
	var currentTab = getCurrentTabName();
	if (currentTab == "urlList1") {
		return "urlListTable1";

	} else if (currentTab == "urlList2") {
		return "urlListTable2";
    
    } else if (currentTab == "urlList3") {
        return "urlListTable3";

	} else {
		console.debug("unknown tab selected: '"+currentTab+"'");
		return null;
	}
}

refreshUrlProcessInfo = function() {
	CatalogManagementService.getUrlValidatorJobInfo({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(jobInfo) {
			updateUrlJobInfo(jobInfo);
			if (!jobFinished(jobInfo)) {
				setTimeout("refreshUrlProcessInfo();", 3000);
			} else {
				updateUrlTables(jobInfo.urlObjectReferences);
			}
		},
		errorHandler: function(msg, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+msg);
			// If there's a timeout try again
			if (err.message != "USER_LOGIN_ERROR") {
			    setTimeout("refreshUrlProcessInfo();", 3000);
			}
		}
	});
}

function jobFinished(jobInfo) {
	// Job is finished if it has never been started (startTime == null) or it has an end time (endTime != null)
	return (jobInfo.startTime == null || jobInfo.endTime != null);
}

function updateUrlJobInfo(jobInfo) {
	if (jobFinished(jobInfo)) {
		dojo.byId("urlsInfoTitle").innerHTML = "<fmt:message key='dialog.admin.management.urls.lastProcessInfo' />";
		dojo.style("urlsProgressBarContainer", "display", "none");
		dojo.style("cancelUrlsProcessButton", "display", "none");
		dojo.style("urlsInfoNumProcessedUrlsContainer", "display", "none");
		dojo.byId("urlsInfoNumProcessedUrls").innerHTML = "";

		if (jobInfo.endTime) {
			dojo.style("urlsInfoEndDateContainer", "display", "block");			
		}

	} else {
		dojo.byId("urlsInfoTitle").innerHTML = "<fmt:message key='dialog.admin.management.urls.currentProcessInfo' />";
		dojo.style("urlsInfoEndDateContainer", "display", "none");
		dojo.style("cancelUrlsProcessButton", "display", "block");
		dojo.style("urlsInfoNumProcessedUrlsContainer", "display", "block");
		dojo.style("urlsProgressBarContainer", "display", "block");
		dojo.byId("urlsInfoNumProcessedUrls").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;

		var progressBar = dijit.byId("urlsProgressBar");
		progressBar.set('maximum', jobInfo.numEntities);
		progressBar.set('progress', jobInfo.numProcessedEntities);
		progressBar.update();
	}

	if (jobInfo.startTime) {
		dojo.byId("urlsInfoBeginDate").innerHTML = UtilString.getDateString(jobInfo.startTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	} else {
		dojo.byId("urlsInfoBeginDate").innerHTML = "";
	}

	if (jobInfo.endTime) {
		dojo.byId("urlsInfoEndDate").innerHTML = UtilString.getDateString(jobInfo.endTime,"EEEE, dd. MMMM yyyy HH:mm:ss");
	} else {
		dojo.byId("urlsInfoEndDate").innerHTML = "";
	}
}


function updateUrlTables(urlObjectReferenceList) {
	if (urlObjectReferenceList) {
	    console.debug("list-size: " + urlObjectReferenceList.length);
		UtilList.addIcons(urlObjectReferenceList);
		
		// show the first 100 entries and first 100 errors
		var maxListSize   = 10000;
		var errors        = 0;
		var allErrors     = 0;
		var numUrls       = 0;
		
		var objList             = new Array();
		var objErrorList        = new Array();
        var objErrorSummary     = new Object();
        var objErrorSummaryList = new Array();
		
		for (var i = 0; i < urlObjectReferenceList.length; ++i) {
            addUrlTableInfo(urlObjectReferenceList[i]);
		    if (i<maxListSize) {
			    urlObjectReferenceList[i].objectName = "<a href='#' onclick='menuEventHandler.handleSelectNodeInTree(\""+urlObjectReferenceList[i].objectUuid+"\", \"O\"); return false;' title='"+urlObjectReferenceList[i].objectName+"' target='_new'>"+urlObjectReferenceList[i].objectName+"</a>";
			    objList.push(urlObjectReferenceList[i]);
			    numUrls++;
		    }
		    // just show in error table those who are not valid but have been checked for invalidity
		    if (urlObjectReferenceList[i].urlState.state != "VALID" && urlObjectReferenceList[i].urlState.state != "NOT_CHECKED") {
		        allErrors++;
		        if (errors < maxListSize) {		        
    		        if (i >= maxListSize) {
                        urlObjectReferenceList[i].objectName = "<a href='#' onclick='menuEventHandler.handleSelectNodeInTree(\""+urlObjectReferenceList[i].objectUuid+"\", \"O\"); return false;' title='"+urlObjectReferenceList[i].objectName+"' target='_new'>"+urlObjectReferenceList[i].objectName+"</a>";
    		        }
    		        objErrorList.push(urlObjectReferenceList[i]);
    		        errors++;
		        }
                if (!objErrorSummary[urlObjectReferenceList[i].errorCode]) {
                    objErrorSummary[urlObjectReferenceList[i].errorCode] = 0;
                }
                objErrorSummary[urlObjectReferenceList[i].errorCode]++;
		    }
		}
		
        for (errorCode in objErrorSummary) { 
            objErrorSummaryList.push({'errorCode':errorCode, 'count':objErrorSummary[errorCode]}); 
        }

		dijit.byId("urlList1").set('title', allUrlsTitle + " (" + /*numUrls + "/" +*/ urlObjectReferenceList.length + ")");
        dijit.byId("urlList2").set('title', allErrorUrlsTitle + " (" + /*errors + "/" +*/ allErrors + ")");
        dijit.byId("urlList3").set('title', summaryErrorUrlsTitle/* + " (" + allErrors + ")"*/);
    
        //UtilGrid.generateIDs(objList, "_id");
        UtilGrid.setTableData("urlListTable1", objList);
        UtilGrid.setTableData("urlListTable2", dojo.filter(objList, function(item) {return ("VALID" != item.errorCode);}));
        UtilGrid.setTableData("urlListTable3", objErrorSummaryList);
	} else {
		UtilGrid.setTableData("urlListTable1", []);
		UtilGrid.setTableData("urlListTable2", []);
        UtilGrid.setTableData("urlListTable3", []);
	}
	UtilGrid.getTable("urlListTable1").invalidate();
    UtilGrid.getTable("urlListTable2").invalidate();
    //UtilGrid.getTable("urlListTable3").invalidate();
	UtilGrid.getTable("urlListTable1").resizeCanvas();
	UtilGrid.getTable("urlListTable2").resizeCanvas();
    //UtilGrid.getTable("urlListTable3").resizeCanvas();
}

function addUrlTableInfo(urlObjectReference) {
	urlObjectReference.url = urlObjectReference.urlState.url;
	if (urlObjectReference.urlState.state == "HTTP_ERROR") {
		urlObjectReference.errorCode = urlObjectReference.urlState.state + "("+urlObjectReference.urlState.responseCode+")";
	} else {
		urlObjectReference.errorCode = urlObjectReference.urlState.state;
	}
}

function showLoadingZone() {
    dojo.style("replaceUrlLoadingZone", "visibility", "visible");
}

function hideLoadingZone() {
    dojo.style("replaceUrlLoadingZone", "visibility", "hidden");
}

</script>
</head>

<body>
<!-- CONTENT START -->
	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-3#overall-catalog-management-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<div id="urlsContent" class="content">

			<!-- INFO START -->
			<div class="inputContainer">
				<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.urls.refresh" />" onClick="javascript:scriptScopeUrls.startUrlsJob();"><fmt:message key="dialog.admin.catalog.management.urls.refresh" /></button>
			</div>

			<div class="inputContainer noSpaceBelow">
				<div id="urlsProcessInfo" class="infobox">
					<span class="icon"><img src="img/ic_info_download.gif" width="16" height="16" alt="Info" /></span>
					<span id="urlsInfoTitle" class="title"></span>
					<div id="urlsProcessInfoContent">
						<table cellspacing="0">
							<tr>
								<td><fmt:message key="dialog.admin.catalog.management.urls.startTime" /></td>
								<td id="urlsInfoBeginDate"></td></tr>
								<tr><td id="urlsInfoEndDateContainer"><fmt:message key="dialog.admin.catalog.management.urls.endTime" /></td>
								<td id="urlsInfoEndDate"></td></tr>
								<tr><td id="urlsInfoNumProcessedUrlsContainer"><fmt:message key="dialog.admin.catalog.management.urls.numUrls" /></td>
								<td id="urlsInfoNumProcessedUrls"></td></tr>
								<tr><td id="urlsProgressBarContainer" colspan=2><div dojoType="dijit.ProgressBar" id="urlsProgressBar" width="310" height="10" /></td></tr>
						</table>
						<span id="cancelUrlsProcessButton" class="button">
							<span style="float:right;">
								<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.urls.cancel" />" onClick="javascript:scriptScopeUrls.cancelUrlsJob();"><fmt:message key="dialog.admin.catalog.management.urls.cancel" /></button>
							</span>
						</span>
					</div> <!-- processInfoContent end -->
				</div> <!-- processInfo end -->
			</div> <!-- inputContainer end -->


			<!-- LEFT HAND SIDE CONTENT START -->
			<div id="urlListContainer" class="inputContainer noSpaceBelow" style="padding-top:15px;">
				<span class="label required"><fmt:message key="dialog.admin.catalog.management.urls.result" /></span>
				<div id="urlLists" dojoType="dijit.layout.TabContainer" style="height:500px;" selectedChild="urlList1">

					<!-- TAB 1 START -->
					<div id="urlList1" dojoType="dijit.layout.ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.allUrls" />">
					    <div>
							<div id="urlListTable1" autoHeight="20" forceGridHeight="true"  contextMenu="none" query=""></div>
                        </div>
					</div> <!-- TAB 1 END -->
        		
					<!-- TAB 2 START -->
					<div id="urlList2" dojoType="dijit.layout.ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.invalidUrls" />">
							<div id="urlListTable2" autoHeight="20" forceGridHeight="true"  contextMenu="none" query=""></div>
					</div> <!-- TAB 2 END -->
                    <!-- TAB 3 START -->
                    <div id="urlList3" dojoType="dijit.layout.ContentPane" class="innerPadding" title="<fmt:message key="dialog.admin.catalog.management.urls.summaryUrls" />">
                        <div id="urlListTable3" autoHeight="20" forceGridHeight="true" contextMenu="none"></div>
                    </div> <!-- TAB 3 END -->
				</div>
			</div>
			<div id="replaceContainer" class="inputContainer grey field">
			    <span class="outer"><div>
				<span class="label">
				    <label for="urlReplace" onclick="javascript:dialog.showContextHelp(arguments[0], 8034)"><fmt:message key="dialog.admin.catalog.management.urls.replaceUrlsWith" /></label>
                </span>
				<span class="input">
					<input type="text" id="urlReplace" maxLength="255" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/>
				</span>
				<span style="float:right;">
					<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.urls.replace" />" onClick="javascript:scriptScopeUrls.replaceUrl();"><fmt:message key="dialog.admin.catalog.management.urls.replace" /></button>
				</span>
				<span id="replaceUrlLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
					<img src="img/ladekreis.gif" />
				</span>
                </div></span>
                <div class="fill"></div>
			</div>
		</div>
	</div>
<!-- CONTENT END -->

</body>
</html>
