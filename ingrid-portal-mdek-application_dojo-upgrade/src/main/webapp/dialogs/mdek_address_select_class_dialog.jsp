<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
var scriptScope = this;
scriptScope = _container_;;
dojo.connect(_container_, "onLoad", function(){
	var container = _container_;
	var msgDiv = dojo.byId("messageDiv");
//	msgDiv.innerHTML = dojo.string.substituteParams("<fmt:message key='dialog.addDescriptorsMessage' />", _container_.customParams.descriptorTitle);
	msgDiv.innerHTML = "<fmt:message key='dialog.createAddressMessage' />";
//	msgDiv.innerHTML += "<br> parentClass: " + _container_.customParams.parentClass;
//	msgDiv.innerHTML += "<br> parentId: " + _container_.customParams.parentId;

	var parentClass;
	if (typeof(container.customParams.parentClass) != "undefined") {
		parentClass = container.customParams.parentClass[0];

	} else if (container.customParams.parentId == "addressRoot") {
		parentClass = -1;

	} else if (container.customParams.parentId == "addressFreeRoot") {
		// This should never be the case...
		parentClass = -2;
	}

	// Init the select box dp
	var addressClassWidget = dijit.byId("addressClassSelect");
	var valueList = [];
	switch (parentClass) {
		case -2:	// Free Address Root
			valueList.push(["<fmt:message key='address.type.custom' />", "3"]);
			break;

		case -1: // Root Address
			valueList.push(["<fmt:message key='address.type.institution' />", "0"]);
			valueList.push(["<fmt:message key='address.type.custom' />", "3"]);
			break;

		case 0:	// Institution
		    valueList.push(["<fmt:message key='address.type.institution' />", "0"]);
			valueList.push(["<fmt:message key='address.type.unit' />", "1"]);
			valueList.push(["<fmt:message key='address.type.person' />", "2"]);
			break;

		case 1:	// Unit
			valueList.push(["<fmt:message key='address.type.unit' />", "1"]);
			valueList.push(["<fmt:message key='address.type.person' />", "2"]);
			break;

		case 2:	// Person (a person must not have any subAddresses)
			console.debug("Error in select address class dialog - A 'person' is not allowed to have any sub addresses!");
			break;

		case 3:	// Custom Address (a custom address must not have any subAddresses)
			console.debug("Error in select address class dialog - A 'custom address' is not allowed to have any sub addresses!");
			break;

		default:
			console.debug("Error in select address class dialog - Unknown parent address type: "+parentClass);
			break;
	}
	//addressClassWidget.dataProvider.setData(valueList);
	UtilStore.updateWriteStore("addressClassSelect", valueList, {label:'0', identifier:'1'});
	addressClassWidget.setValue(valueList[0][1]);
});

dojo.connect(_container_, "onUnload", function(){
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (this.customParams.resultHandler.fired == -1) {
		this.customParams.resultHandler.errback();
	}
});


// 'Yes Button' onClick function
scriptScope.yesButtonFunc = function() {
	// Callback with selected address class
	var caller = this.customParams.resultHandler;
    var value = parseInt(dijit.byId("addressClassSelect").getValue());
	this.hide();
    caller.callback(value);
}

// 'No Button' onClick function
scriptScope.noButtonFunc = function() {
	this.customParams.resultHandler.errback();
	this.hide();
}

</script>
</head>

<body>
	<div id="contentPane" layoutAlign="client" class="">
		<div id="dialogContent" class="content">
			<div id="messageDiv" class="field grey">
			</div>

			<div>
			  <input dojoType="dijit.form.Select" autoComplete="false" style="width:100%;" id="addressClassSelect" />
			</div>

			<div class="inputContainer grey" style="height:30px;">
		        <span style="float:right; margin-top:5px;"><button dojoType="dijit.form.Button" title="<fmt:message key='general.cancel' />" onClick="javascript:scriptScope.noButtonFunc();"><fmt:message key="general.cancel" /></button></span>
		        <span style="float:right; margin-top:5px;"><button dojoType="dijit.form.Button" title="<fmt:message key='general.addresses.add' />" onClick="javascript:scriptScope.yesButtonFunc();"><fmt:message key="general.addresses.add" /></button></span>
			</div>
	  	</div>
	</div>
</body>
</html>
