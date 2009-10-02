<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	var msgDiv = dojo.byId("messageDiv");
	msgDiv.innerHTML = message.get("dialog.admin.users.selectUser");

	initUserList();
});

_container_.addOnUnload(function() {
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (_container_.customParams.resultHandler.fired == -1) {
		_container_.customParams.resultHandler.errback();
	}
});

function initUserList() {

	SecurityService.getPortalUsers( {
		callback: function(userList) {
			var list = [];
			for (var i in userList) {
				list.push([userList[i], userList[i]]);
			}

			dojo.widget.byId("userList").dataProvider.setData(list);
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}


// 'Yes Button' onClick function
scriptScope.yesButtonFunc = function() {
	var selectedUser = dojo.widget.byId("userList").getValue();

	if (selectedUser != null && selectedUser != "") {
		_container_.customParams.resultHandler.callback(selectedUser);
	} else {
		dialog.show(message.get('general.hint'), message.get("dialog.admin.users.userNotFoundError"), dialog.WARNING);
		_container_.customParams.resultHandler.errback();
	}
	_container_.closeWindow();
}


// 'No Button' onClick function
scriptScope.noButtonFunc = function() {
	_container_.customParams.resultHandler.errback();
	_container_.closeWindow();
}

</script>
</head>



<body>
<div dojoType="ContentPane">
	<div id="contentPane" layoutAlign="client" class="contentBlockWhite top">
		<div id="dialogContent" class="content">
			<div id="messageDiv" class="field grey w296">
			</div>
			<div class="grey">
				<span class="input" style="margin-left:20px;"><input dojoType="ingrid:Select" style="width:250px;" id="userList" /></span>
			</div>
			<div class="inputContainer grey noSpaceBelow half" style="height:30px;">
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="Abbrechen" onClick="javascript:scriptScope.noButtonFunc();"><fmt:message key="general.cancel" /></button></span>
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="&Uuml;bernehmen" onClick="javascript:scriptScope.yesButtonFunc();"><fmt:message key="general.apply" /></button></span>
			</div>
	  	</div>
	</div>
</div>
</body>
</html>
