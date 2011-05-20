<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
var scriptScope = this;

dojo.connect(_container_, "onLoad", function(){
	var msgDiv = dojo.byId("messageDiv");
	msgDiv.innerHTML = "<fmt:message key='dialog.admin.users.selectUser' />";

	initUserList();
});

dojo.connect(_container_, "onUnLoad", function(){
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (dijit.byId("pageDialog").customParams.resultHandler.fired == -1) {
		dijit.byId("pageDialog").customParams.resultHandler.errback();
	}
});

function initUserList() {

	SecurityService.getPortalUsers( {
        preHook: scriptScope.showLoading,
        postHook: scriptScope.endLoading,
		callback: function(userList) {
			var list = [];
			for (var i in userList) {
				list.push([userList[i], userList[i]]);
			}

			UtilStore.updateWriteStore("userList", list, { identifier: '1', label: '0'});

			// make sure that nothing is selected (or appears to be) 
			//dijit.byId("userList").setLabel("");
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			console.debug(errMsg);
		}
	});
}

scriptScope.showLoading = function() {
    dojo.style("importUserLoadingZone", "visibility", "visible");
    dijit.byId("userList").set("disabled", true);
    dijit.byId("importUser").set("disabled", true);
}

scriptScope.endLoading = function() {
    dojo.style("importUserLoadingZone", "visibility", "hidden");
    dijit.byId("userList").set("disabled", false);
    dijit.byId("importUser").set("disabled", false);
}

// 'Yes Button' onClick function
scriptScope.yesButtonFunc = function() {
	var selectedUser = dijit.byId("userList").getValue();

	if (selectedUser != null && selectedUser != "") {
		dijit.byId("pageDialog").customParams.resultHandler.callback(selectedUser);
	} else {
		dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.users.userNotFoundError' />", dialog.WARNING);
		dijit.byId("pageDialog").customParams.resultHandler.errback();
	}
	dijit.byId("pageDialog").hide();
}


// 'No Button' onClick function
scriptScope.noButtonFunc = function() {
	dijit.byId("pageDialog").customParams.resultHandler.errback();
	dijit.byId("pageDialog").hide();
}

</script>
</head>



<body>
<div dojoType="dijit.layout.ContentPane" class="content grey">
    <span class="outer">
        <div>
            <span class="label">
                <label id="messageDiv"></label>
            </span>
            <div class="input">
                <span class="input">
                    <input dojoType="dijit.form.Select" maxHeight="150" style="width:100%; margin:0;" id="userList" />
                </span>
            </div>
        </div>
    </span>
    <div class="inputContainer grey" style="height:30px;">
        <span style="float:right; margin-top:5px;">
            <button dojoType="dijit.form.Button" title="<fmt:message key="general.cancel" />" onClick="javascript:scriptScope.noButtonFunc();">
                <fmt:message key="general.cancel" />
            </button>
        </span>
        <span style="float:right; margin-top:5px;">
            <button dojoType="dijit.form.Button" id="importUser" type="button" title="<fmt:message key="general.apply" />" onClick="javascript:scriptScope.yesButtonFunc();">
                <fmt:message key="general.apply" />
            </button>
        </span>
        <span id="importUserLoadingZone" style="float:right; margin-top:5px; z-index: 100; visibility:hidden;">
            <img src="img/ladekreis.gif" />
        </span>
    </div>
</div>
</body>
</html>
