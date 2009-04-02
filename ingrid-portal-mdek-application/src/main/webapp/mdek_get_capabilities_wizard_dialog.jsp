<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V3</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {

	dojo.widget.byId("objectClass").setValue("Class3");

    // Pressing 'enter' on the url input field is equal to a start button click
    dojo.event.connect(dojo.widget.byId("assistantURL").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startRequest();
            }
	});


});

function setOperationValues(capBean) {
//	dojo.debugShallow(capBean);

	dojo.widget.byId("generalShortDesc").setValue(capBean.title);
	var serviceTitle = dojo.string.trim(capBean.title+"");
	if (serviceTitle.length != 0) {
		dojo.widget.byId("objectName").setValue(serviceTitle);
	}

	dojo.widget.byId("generalDesc").setValue(capBean.description);
	if (capBean.serviceType.indexOf("CSW") != -1) {
		dojo.widget.byId("ref3ServiceType").setValue(1);		
		dojo.widget.byId("ref3ServiceTypeTable").store.setData(UtilList.addTableIndices(UtilList.listToTableData([207])));

	} else if (capBean.serviceType.indexOf("WMS") != -1) {
		dojo.widget.byId("ref3ServiceType").setValue(2);		
		dojo.widget.byId("ref3ServiceTypeTable").store.setData(UtilList.addTableIndices(UtilList.listToTableData([202])));

	} else if (capBean.serviceType.indexOf("WFS") != -1) {
		dojo.widget.byId("ref3ServiceType").setValue(3);		
		dojo.widget.byId("ref3ServiceTypeTable").store.setData(UtilList.addTableIndices(UtilList.listToTableData([201])));

	} else if (capBean.serviceType.indexOf("WCTS") != -1) {
		dojo.widget.byId("ref3ServiceType").setValue(4);		
//		dojo.widget.byId("ref3ServiceTypeTable").store.setData(UtilList.addTableIndices(UtilList.listToTableData([207])));

	} else if (capBean.serviceType.indexOf("WCS") != -1) {	// WCS does not exist yet
		dojo.widget.byId("ref3ServiceType").setValue(6);
		dojo.widget.byId("ref3ServiceTypeTable").store.setData(UtilList.addTableIndices(UtilList.listToTableData([203])));
		dojo.widget.byId("ref3Explanation").setValue("WCS Service");
	}

	dojo.widget.byId("ref3ServiceVersion").store.setData(UtilList.addTableIndices(UtilList.listToTableData(capBean.versions)));

	// Prepare the operation table for display.
	// Add table indices to the main obj and paramList
	// Add table indices and convert to tableData: platform, addressList and dependencies
	if (capBean.operations) {
		for (var i = 0; i < capBean.operations.length; ++i) {
			UtilList.addTableIndices(capBean.operations[i].paramList);
			capBean.operations[i].platform = UtilList.addTableIndices(UtilList.listToTableData(capBean.operations[i].platform));
			capBean.operations[i].addressList = UtilList.addTableIndices(UtilList.listToTableData(capBean.operations[i].addressList));
			capBean.operations[i].dependencies = UtilList.addTableIndices(UtilList.listToTableData(capBean.operations[i].dependencies));		
		}
	}	
	dojo.widget.byId("ref3Operation").store.setData(UtilList.addTableIndices(capBean.operations));

	scriptScope.closeDialog();
}


scriptScope.startRequest = function() {
	var url = dojo.widget.byId("assistantURL").getValue();
	dojo.debug(url);

	GetCapabilitiesService.getCapabilities(url, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(res) { setOperationValues(res); },
		errorHandler:function(errMsg, err) {
			hideLoadingZone();
			dojo.debug("Error: "+errMsg);
			displayErrorMessage(err);
		}
	});
}

scriptScope.closeDialog = function() {
	_container_.closeWindow();
}


function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("assistantGetCapLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("assistantGetCapLoadingZone"), "hidden");
}

</script>
</head>

<body>

<div dojoType="ContentPane">

	<div class="contentBlockWhite top fullBlock">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 8089)" title="Hilfe">[?]</a>
		</div>
		<div class="content">

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div class="inputContainer field grey noSpaceBelow fullField">
				<span class="label"><label for="assistantURL" onclick="javascript:dialog.showContextHelp(arguments[0], 8061, 'Capabilities URL')"><fmt:message key="dialog.wizard.getCap.url" /></label></span>
				<span class="input"><input type="text" id="assistantURL" name="assistantURL" class="w640" dojoType="ingrid:ValidationTextBox" /></span>
				<div class="spacerField"></div>
			</div>

			<div class="inputContainer grey noSpaceBelow full" style="height:30px;">
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="Abbrechen" onClick="javascript:scriptScope.closeDialog();"><fmt:message key="dialog.wizard.getCap.cancel" /></button></span>
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="Start" onClick="javascript:scriptScope.startRequest();"><fmt:message key="dialog.wizard.getCap.start" /></button></span>
				<span id="assistantGetCapLoadingZone" style="float:right; margin-top:6px; z-index: 100; visibility:hidden">
					<img src="img/ladekreis.gif" />
				</span>
			</div>
			<!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div>
</div>

</body>
</html>
