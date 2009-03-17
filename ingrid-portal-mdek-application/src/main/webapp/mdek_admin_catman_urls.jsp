<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	// Use the same store for both tables. The First table has to be reinitialised so the new store
	// gets registered properly
	var mainStore = dojo.widget.byId("urlListTable1").store;
	
	dojo.widget.byId("urlListTable2").store = mainStore;
	dojo.widget.byId("urlListTable2").initialize();


	var filterTable = dojo.widget.byId("urlListTable2");
	filterTable.setFilter("errorCode", function(errorCode) { return ("VALID" != errorCode); });

	dojo.html.hide(dojo.byId("urlsProgressBarContainer"));
	refreshUrlProcessInfo();
});


scriptScope.startUrlsJob = function() {
	dojo.widget.byId("urlListTable1").store.clearData();
//	dojo.widget.byId("urlListTable2").store.clearData();

	CatalogManagementService.startUrlValidatorJob({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function() {
			setTimeout("refreshUrlProcessInfo();", 2000);
		},
		errorHandler: function(msg, err) {
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
		}
	});
}

scriptScope.cancelUrlsJob = function() {
	CatalogManagementService.stopUrlValidatorJob({
		errorHandler: function(msg, err) {
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
		}
	});
}

scriptScope.replaceUrl = function() {
	var urlTable = getCurrentTable();
	var selectedData = urlTable.getSelectedData();
	var replaceUrl = dojo.string.trim(dojo.widget.byId("urlReplace").getValue());
	// TODO send data to backend

	if (selectedData && selectedData.length != 0 && replaceUrl.length != 0) {
		var sourceUrls = [];
		dojo.lang.forEach(selectedData, function(element) {
			sourceUrls.push( { objectUuid: element.objectUuid, url:element.url } );
		});

		var def = replaceUrlDef(sourceUrls, replaceUrl);
		def.addCallback(function() { return updateDBUrlJobInfoDef(sourceUrls, replaceUrl); });
		def.addCallback(refreshUrlProcessInfo);
		def.addCallback(function() { dialog.show(message.get("general.hint"), message.get("dialog.admin.management.urls.success"), dialog.INFO) });
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
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
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
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback();
		}
	});

	return def;
}

function getCurrentTable() {
	var currentTab = dojo.widget.byId("urlLists").selectedChild;
	if (currentTab == "urlList1") {
		return dojo.widget.byId("urlListTable1");

	} else if (currentTab == "urlList2") {
		return dojo.widget.byId("urlListTable2");

	} else {
		dojo.debug("unknown tab selected: '"+currentTab+"'");
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
			dojo.debug("Error: "+msg);
			// If there's a timeout try again
			setTimeout("refreshUrlProcessInfo();", 3000);
		}
	});
}

function jobFinished(jobInfo) {
	// Job is finished if it has never been started (startTime == null) or it has an end time (endTime != null)
	return (jobInfo.startTime == null || jobInfo.endTime != null);
}

function updateUrlJobInfo(jobInfo) {
	if (jobFinished(jobInfo)) {
		dojo.byId("urlsInfoTitle").innerHTML = message.get("dialog.admin.management.urls.lastProcessInfo");
		dojo.html.hide(dojo.byId("urlsProgressBarContainer"));
		dojo.html.hide(dojo.byId("cancelUrlsProcessButton"));
		dojo.html.hide(dojo.byId("urlsInfoNumProcessedUrlsContainer"));
		dojo.byId("urlsInfoNumProcessedUrls").innerHTML = "";

		if (jobInfo.endTime) {
			dojo.html.show(dojo.byId("urlsInfoEndDateContainer"));
		}

	} else {
		dojo.byId("urlsInfoTitle").innerHTML = message.get("dialog.admin.management.urls.currentProcessInfo");
		dojo.html.hide(dojo.byId("urlsInfoEndDateContainer"));
		dojo.html.show(dojo.byId("cancelUrlsProcessButton"));
		dojo.html.show(dojo.byId("urlsInfoNumProcessedUrlsContainer"));
		dojo.html.show(dojo.byId("urlsProgressBarContainer"));
		dojo.byId("urlsInfoNumProcessedUrls").innerHTML = jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;

		var progressBar = dojo.widget.byId("urlsProgressBar");
		progressBar.setMaxProgressValue(jobInfo.numEntities);
		progressBar.setProgressValue(jobInfo.numProcessedEntities);
	}

	if (jobInfo.startTime) {
		dojo.byId("urlsInfoBeginDate").innerHTML = jobInfo.startTime.toLocaleString();
	} else {
		dojo.byId("urlsInfoBeginDate").innerHTML = "";
	}

	if (jobInfo.endTime) {
		dojo.byId("urlsInfoEndDate").innerHTML = jobInfo.endTime.toLocaleString();
	} else {
		dojo.byId("urlsInfoEndDate").innerHTML = "";
	}
}


function updateUrlTables(urlObjectReferenceList) {
	if (urlObjectReferenceList) {
		UtilList.addIcons(urlObjectReferenceList);
		for (var i = 0; i < urlObjectReferenceList.length; ++i) {
			urlObjectReferenceList[i].Id = i;
			addUrlTableInfo(urlObjectReferenceList[i]);
		}

		dojo.widget.byId("urlListTable1").store.setData(urlObjectReferenceList);
//		dojo.widget.byId("urlListTable2").store.setData(urlObjectReferenceList);
		dojo.widget.byId("urlListTable2").applyFilters();
		dojo.widget.byId("urlListTable2").render();
	} else {
		dojo.widget.byId("urlListTable1").store.clearData();
//		dojo.widget.byId("urlListTable2").store.clearData();
	}
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
    dojo.html.setVisibility(dojo.byId("replaceUrlLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("replaceUrlLoadingZone"), "hidden");
}

</script>
</head>

<body>
<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">
	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="#" title="Hilfe">[?]</a>
		</div>
		<div id="urlsContent" class="content">

			<!-- INFO START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div class="inputContainer w965">
				<button dojoType="ingrid:Button" title="Pr&uuml;fung starten" onClick="javascript:scriptScope.startUrlsJob();"><fmt:message key="dialog.admin.catalog.management.urls.refresh" /></button>
			</div>

			<div class="inputContainer noSpaceBelow">
				<div id="urlsProcessInfo" class="infobox w941">
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
								<tr><td id="urlsProgressBarContainer" colspan=2><div dojoType="ProgressBar" id="urlsProgressBar" width="310" height="10" /></td></tr>
						</table>
						<span id="cancelUrlsProcessButton" class="button" style="height:20px !important;">
							<span style="float:right;">
								<button dojoType="ingrid:Button" title="Prozess abbrechen" onClick="javascript:scriptScope.cancelUrlsJob();"><fmt:message key="dialog.admin.catalog.management.urls.cancel" /></button>
							</span>
						</span>
					</div> <!-- processInfoContent end -->
				</div> <!-- processInfo end -->
			</div> <!-- inputContainer end -->


			<!-- LEFT HAND SIDE CONTENT START -->
			<div id="urlListContainer" class="inputContainer noSpaceBelow">
				<span class="label"><fmt:message key="dialog.admin.catalog.management.urls.result" /></span>
				<div id="urlLists" dojoType="ingrid:TabContainer" style="height:480px; width:1200px;" selectedChild="urlList1">

					<!-- TAB 1 START -->
					<div id="urlList1" dojoType="ContentPane" label="<fmt:message key="dialog.admin.catalog.management.urls.allUrls" />">
						<div class="tableContainer rows20">
							<table id="urlListTable1" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable interactive" customContextMenu="true">
								<thead>
									<tr>
										<th field="errorCode" dataType="String" width="106"><fmt:message key="dialog.admin.catalog.management.urls.error" /></th>
										<th field="icon" dataType="String" width="18"></th>
										<th field="objectName" dataType="String" width="214"><fmt:message key="dialog.admin.catalog.management.urls.objectName" /></th>
										<th field="url" dataType="String" width="394" sort="asc">URL</th>
										<th field="urlReferenceDescription" dataType="String" width="241"><fmt:message key="dialog.admin.catalog.management.urls.description" /></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div> <!-- TAB 1 END -->
        		
					<!-- TAB 2 START -->
					<div id="urlList2" dojoType="ContentPane" label="<fmt:message key="dialog.admin.catalog.management.urls.invalidUrls" />">
						<div class="tableContainer rows20">
							<table id="urlListTable2" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable interactive" customContextMenu="true">
								<thead>
									<tr>
										<th field="errorCode" dataType="String" width="106"><fmt:message key="dialog.admin.catalog.management.urls.error" /></th>
										<th field="icon" dataType="String" width="18"></th>
										<th field="objectName" dataType="String" width="214"><fmt:message key="dialog.admin.catalog.management.urls.objectName" /></th>
										<th field="url" dataType="String" width="394" sort="asc">URL</th>
										<th field="urlReferenceDescription" dataType="String" width="241"><fmt:message key="dialog.admin.catalog.management.urls.description" /></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div> <!-- TAB 2 END -->
				</div>
			</div>
			<div class="inputContainer grey field h058 noSpaceBelow" style="width:980px;">
				<span class="label"><label for="urlReplace" onclick="javascript:dialog.showContextHelp(arguments[0], 'Markierte URLs durch folgende URL ersetzen')"><fmt:message key="dialog.admin.catalog.management.urls.replaceUrlsWith" /></label></span>
				<span class="input" style="position:relative;">
					<input type="text" id="urlReplace" maxlength="255" class="w829 nextToButton" dojoType="ingrid:ValidationTextBox" />
				</span>
				<span style="position:relative; top:-1px; margin-right:15px; float:right;">
					<button dojoType="ingrid:Button" title="Ersetzen" onClick="javascript:scriptScope.replaceUrl();"><fmt:message key="dialog.admin.catalog.management.urls.replace" /></button>
				</span>
				<span id="replaceUrlLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
					<img src="img/ladekreis.gif" />
				</span>
				<div class="fill"></div>
			</div>
		</div>
	</div>
</div> <!-- CONTENT END -->

</body>
</html>
