<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.comment.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var dirtyFlag = null;

var scriptScope = this;

scriptScope.createDOMElements = function() {
	var commentCommentsTableStructure = [
		{field: 'date',name: "<fmt:message key='dialog.comments.date'/>",width: '120px', formatter: DateCellFormatter},
		{field: 'title',name: "<fmt:message key='dialog.comments.user'/>",width: '185px'},
		{field: 'comment',name: "<fmt:message key='dialog.comments.comment'/>",width: '500px'}
    ];
    createDataGrid("commentCommentsTable", null, commentCommentsTableStructure, null);
}

dojo.addOnLoad(function() {
	
	scriptScope.createDOMElements();
	
	scriptScope.container = dijit.byId("pageDialog");
	dojo.connect(scriptScope.container, "onLoad", function(){
		dirtyFlag = udkDataProxy.dirtyFlag;
	
		var srcStore = commentStore;
		
		// Set the comment table title
		var nodeTitle = "";
		if (currentUdk.nodeAppType == "O") {
			nodeTitle = dijit.byId("objectName").getValue();
		} else if (currentUdk.nodeAppType == "A") {
			nodeTitle = dijit.byId("addressTitle").getValue();	
		}
		dojo.byId("commentTableLabel").innerHTML = dojo.string.substitute("<fmt:message key='dialog.commentTitle' />", [nodeTitle]);
		//dstStore.setData(srcStore.getData());
		UtilStore.updateWriteStore("commentCommentsTable", srcStore);
	
		if (!currentUdk.writePermission) {
			dijit.byId("addCommentButton").setDisabled(true);
			dijit.byId("commentNewComment").setDisabled(true);
			dojo.addClass(dijit.byId("commentCommentsTable").domNode, "readonly");
			dojo.removeClass(dijit.byId("commentCommentsTable").domNode, "interactive");
			//dijit.byId("commentCommentsTable").removeContextMenu();
		}
	
		var setDirtyFlag = function(){ dirtyFlag = true; }
		dojo.connect(UtilGrid.getTable("commentCommentsTable"), "onDataChanged", setDirtyFlag);
        
	});
	
	dojo.connect(scriptScope.container, "onUnload", function(){
		dirtyFlag ? udkDataProxy.setDirtyFlag() : udkDataProxy.resetDirtyFlag();
	});
});

scriptScope.addComment = function() {
	var newComment = dijit.byId("commentNewComment").getValue();
	newComment = dojo.trim(newComment);

	if (newComment != "") {
		var userName = UtilAddress.createAddressTitle(currentUser.address);
		var newCommentBean = {comment: newComment, date: new Date(), user: {uuid: currentUser.addressUuid}, title: userName};

		UtilGrid.addTableDataRow("commentCommentsTable", newCommentBean);
		dijit.byId("commentNewComment").setValue("");
	}
}


</script>
</head>

<body>


<div id="comment" class="">
    <div id="winNavi" style="top:0px;">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-10#maintanance-of-objects-10', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
    </div>
    <div id="commentContent" class="content">
        <!-- CONTENT START -->
        <div class="inputContainer">
            <span id="commentTableLabel" class="label">
            </span>
            <div class="input tableContainer">
                <div id="commentCommentsTable" autoHeight="8">
                </div>
            </div>
            <div class="inputContainer">
                <span class="label">
                    <label for="commentNewComment" onclick="javascript:dialog.showContextHelp(arguments[0], 7043)">
                        <fmt:message key="dialog.comments.newComment" />
                    </label>
                </span>
                <span class="input field grey">
                    <input type="text" id="commentNewComment" dojoType="dijit.form.SimpleTextarea" class="textAreaFull"/>
                </span>
                <span class="button">
                    <span style="float:right;">
                        <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.comments.addComment" />" id="addCommentButton" onClick="scriptScope.addComment">
                            <fmt:message key="dialog.comments.addComment" />
                        </button>
                    </span>
                </span>
                <div class="fill"></div>
            </div><!-- CONTENT END -->
        </div>
    </div>

</body>
</html>
