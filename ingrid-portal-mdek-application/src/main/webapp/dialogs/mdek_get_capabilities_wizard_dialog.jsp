<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.capabilities.wizard.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

var thisDialog = dijit.byId("subPageDialog");
if (thisDialog == undefined)
    thisDialog = dijit.byId("pageDialog");

dojo.addOnLoad(function() {
	dojo.connect(thisDialog, "onLoad", function(){
		dijit.byId("objectClass").setValue("Class3");

	    // Pressing 'enter' on the url input field is equal to a start button click
	    dojo.connect(dijit.byId("assistantURL").domNode, "onkeypress",
	        function(event) {
	            if (event.keyCode == dojo.keys.ENTER) {
	                scriptScope.startRequest();
	            }
		});
	});
});


function setOperationValues(capBean) {
	dijit.byId("generalShortDesc").setValue(capBean.title);
	var serviceTitle = dojo.trim(capBean.title+"");
	if (serviceTitle.length != 0) {
		dijit.byId("objectName").setValue(serviceTitle);
	}

	dijit.byId("generalDesc").setValue(capBean.description);
	if (capBean.serviceType.indexOf("CSW") != -1) {
		dijit.byId("ref3ServiceType").setValue(1);		
		UtilStore.updateWriteStore("ref3ServiceTypeTable", UtilList.listToTableData([207]));

	} else if (capBean.serviceType.indexOf("WMS") != -1) {
		dijit.byId("ref3ServiceType").setValue(2);		
		UtilStore.updateWriteStore("ref3ServiceTypeTable", UtilList.listToTableData([202]));

	} else if (capBean.serviceType.indexOf("WFS") != -1) {
		dijit.byId("ref3ServiceType").setValue(3);		
		UtilStore.updateWriteStore("ref3ServiceTypeTable", UtilList.listToTableData([201]));
		
	} else if (capBean.serviceType.indexOf("WCTS") != -1) {
		dijit.byId("ref3ServiceType").setValue(4);		
//		dijit.byId("ref3ServiceTypeTable").store.setData(UtilList.addTableIndices(UtilList.listToTableData([207])));

	} else if (capBean.serviceType.indexOf("WCS") != -1) {	// WCS does not exist yet
		dijit.byId("ref3ServiceType").setValue(6);
		UtilStore.updateWriteStore("ref3ServiceTypeTable", UtilList.listToTableData([203]));
		dijit.byId("ref3Explanation").setValue("WCS Service");
	}

	UtilStore.updateWriteStore("ref3ServiceVersion", UtilList.listToTableData(capBean.versions));

	// Prepare the operation table for display.
	// Add table indices to the main obj and paramList
	// Add table indices and convert to tableData: platform, addressList and dependencies
	if (capBean.operations) {
		for (var i = 0; i < capBean.operations.length; ++i) {
			//UtilList.addTableIndices(capBean.operations[i].paramList);
			capBean.operations[i].platform = UtilList.listToTableData(capBean.operations[i].platform);
			capBean.operations[i].addressList = UtilList.listToTableData(capBean.operations[i].addressList);
			capBean.operations[i].dependencies = UtilList.listToTableData(capBean.operations[i].dependencies);		
		}
	}	
	UtilStore.updateWriteStore("ref3Operation", capBean.operations);

	scriptScope.closeThisDialog();
}


scriptScope.startRequest = function() {
	var url = dijit.byId("assistantURL").getValue();
	console.debug(url);

	GetCapabilitiesService.getCapabilities(url, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(res) {
		    if (res.versions == "1.3.0") {
		        dialog.show("<fmt:message key='general.warning' />", dojo.string.substitute("<fmt:message key='dialog.wizard.getCap.version.warning' />",[res.versions]), dialog.WARNING, [
		            { caption: "<fmt:message key='general.no' />",  action: function() { def.errback("CANCEL"); } },
                    { caption: "<fmt:message key='general.ok' />", action: function() { setOperationValues(res); } }
		        ]);
		    } else {
		    	setOperationValues(res);
		    }
		},
		errorHandler:function(errMsg, err) {
			hideLoadingZone();
			console.debug("Error: "+errMsg);
			displayErrorMessage(err);
		}
	});
}

scriptScope.closeThisDialog = function() {
	thisDialog.hide();
}

function showLoadingZone() {
	dojo.byId('assistantGetCapLoadingZone').style.visibility = "visible";
}

function hideLoadingZone() {
	dojo.byId('assistantGetCapLoadingZone').style.visibility = "hidden";
}

</script>
</head>

<body>

<div dojoType="dijit.layout.ContentPane">

	<div class="assistant">
		<div class="content">
			<!-- LEFT HAND SIDE CONTENT START -->
			<div id="winNavi" style="top:0;">
                    <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=creation-of-objects-3#creation-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
			<div class="inputContainer field grey">
			    <span class="outer"><div style="padding:15px !important;">
				<span class="label"><label for="assistantURL" onclick="javascript:dialog.showContextHelp(arguments[0], 8061)"><fmt:message key="dialog.wizard.getCap.url" /></label></span>
				<span class="input"><input type="text" id="assistantURL" name="assistantURL" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                </div></span>
                <div class="fill"></div>
			</div>

			<div class="inputContainer grey" style="height:30px; padding: 5px 0px ! important;">
		        <span style="float:right; margin-top:5px;"><button dojoType="dijit.form.Button" title="<fmt:message key="dialog.wizard.getCap.cancel" />" onClick="javascript:scriptScope.closeThisDialog();"><fmt:message key="dialog.wizard.getCap.cancel" /></button></span>
		        <span style="float:right; margin-top:5px;"><button dojoType="dijit.form.Button" type="button" title="<fmt:message key="dialog.wizard.getCap.start" />" onClick="javascript:scriptScope.startRequest();"><fmt:message key="dialog.wizard.getCap.start" /></button></span>
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
