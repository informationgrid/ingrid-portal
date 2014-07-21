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

var dialogComments = null;

require([
    "dojo/on",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dijit/registry",
    "dojo/topic",
    "dojo/string",
    "dojo/_base/lang",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/utils/Grid",
    "ingrid/utils/Store",
    "ingrid/utils/Address",
    "ingrid/utils/General",
    "ingrid/utils/Security"
],
    function(on, aspect, dom, domClass, registry, topic, string, lang, dirty, layoutCreator, GridFormatters, UtilGrid, UtilStore, UtilAddress, UtilGeneral, UtilSecurity) {

        var global = this;

        on(_container_, "Load", function(){
            createDOMElements().then(init);
        });

        function createDOMElements() {
            var commentCommentsTableStructure = [
                {field: 'date',name: "<fmt:message key='dialog.comments.date'/>",width: '120px', formatter: GridFormatters.DateCellFormatter},
                {field: 'title',name: "<fmt:message key='dialog.comments.user'/>",width: '185px'},
                {field: 'comment',name: "<fmt:message key='dialog.comments.comment'/>",width: '670px'}
            ];
            return layoutCreator.createDataGrid("commentCommentsTable", null, commentCommentsTableStructure, null);
        }

        function init() {
            var srcStore = global.currentUdk.commentStore;
            
            // Set the comment table title
            var nodeTitle = "";
            if (global.currentUdk.nodeAppType == "O") {
                nodeTitle = registry.byId("objectName").getValue();
            } else if (global.currentUdk.nodeAppType == "A") {
                nodeTitle = registry.byId("addressTitle").getValue();
            }
            dom.byId("commentTableLabel").innerHTML = string.substitute("<fmt:message key='dialog.commentTitle' />", [nodeTitle]);
            //dstStore.setData(srcStore.getData());
            UtilStore.updateWriteStore("commentCommentsTable", srcStore);
        
            if (!global.currentUdk.writePermission) {
                registry.byId("addCommentButton").setDisabled(true);
                registry.byId("commentNewComment").setDisabled(true);
                domClass.add(registry.byId("commentCommentsTable").domNode, "readonly");
                domClass.remove(registry.byId("commentCommentsTable").domNode, "interactive");
                //registry.byId("commentCommentsTable").removeContextMenu();
            }
        
            aspect.after(UtilGrid.getTable("commentCommentsTable"), "onDataChanged", lang.hitch(dirty, dirty.setDirtyFlag));

            console.log("Publishing event: '/afterInitDialog/Comments'");
            topic.publish("/afterInitDialog/Comments");
        }

        function addComment() {
            var newComment = registry.byId("commentNewComment").getValue();
            newComment = lang.trim(newComment);

            if (UtilGeneral.hasValue(newComment)) {
                var userName = UtilAddress.createAddressTitle(UtilSecurity.currentUser.address);
                var newCommentBean = {comment: newComment, date: new Date(), user: {uuid: UtilSecurity.currentUser.addressUuid}, title: userName};

                UtilGrid.addTableDataRow("commentCommentsTable", newCommentBean);
                registry.byId("commentNewComment").set("value", "");
            }
        }

        /**
         * PUBLIC METHODS
         */
        dialogComments = {
            addComment: addComment
        };

    });

</script>
</head>

<body>


<div id="comment" class="">
    <div id="commentContent" class="content">
        <!-- CONTENT START -->
        <div class="inputContainer">
            <div id="winNavi" style="top:0px;">
                <a href="#" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-10#maintanance-of-objects-10', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <span id="commentTableLabel" class="label">
            </span>
            <div class="input tableContainer spaceBelow">
                <div id="commentCommentsTable" autoHeight="8">
                </div>
            </div>
            <div class="inputContainer">
                <span class="label">
                    <label for="commentNewComment" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7043)">
                        <fmt:message key="dialog.comments.newComment" />
                    </label>
                </span>
                <span class="input field grey">
                    <input type="text" id="commentNewComment" data-dojo-type="dijit/form/SimpleTextarea" class="textAreaFull"/>
                </span>
                <span class="button">
                    <span style="float:right;">
                        <button type="button" data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.comments.addComment" />" id="addCommentButton" onclick="dialogComments.addComment()">
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
