<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
    var pageImportUser;

    require([
        "dojo/on",
        "dojo/dom",
        "dojo/dom-style",
        "dijit/registry",
        "dojo/Deferred",
        "ingrid/layoutCreator",
        "ingrid/dialog"
    ], function(on, dom, style, registry, Deferred, layoutCreator, dialog) {

        on(_container_, "Load", function(){
            var msgDiv = dom.byId("messageDiv");
            msgDiv.innerHTML = "<fmt:message key='dialog.admin.users.selectUser' />";
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createFilteringSelect("userList", null, storeProps, initUserList);
            //initUserList();
        });

        function initUserList() {
            var def = new Deferred();

            SecurityService.getAvailableUsers( {
                preHook: showLoading,
                postHook: endLoading,
                callback: function(userList) {
                    var list = [];
                    for (var i in userList) {
                        list.push([userList[i], userList[i]]);
                    }

                    def.resolve(list);
                },
                errorHandler: function(errMsg, err) {
                    displayErrorMessage(err);
                    console.debug(errMsg);
                }
            });
            return def;
        }

        function showLoading() {
            style.set("importUserLoadingZone", "visibility", "visible");
            //registry.byId("userList").set("disabled", true);
            registry.byId("importUser").set("disabled", true);
        }

        function endLoading() {
            style.set("importUserLoadingZone", "visibility", "hidden");
            //registry.byId("userList").set("disabled", false);
            registry.byId("importUser").set("disabled", false);
        }

        // 'Yes Button' onClick function
        function yesButtonFunc() {
            var selectedUser = registry.byId("userList").getValue();

            if (selectedUser !== null && selectedUser !== "") {
                pageImportUser.parameter.resultHandler.resolve(selectedUser);
            } else {
                dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.users.userNotFoundError' />", dialog.WARNING);
                pageImportUser.parameter.resultHandler.reject();
            }
            registry.byId("pageDialog").hide();
        }


        // 'No Button' onClick function
        function noButtonFunc() {
            registry.byId("pageDialog").hide();
        }

        pageImportUser = {
            accept: yesButtonFunc,
            close: noButtonFunc,
            parameter: _container_.customParams
        };
    });


</script>
</head>



<body>
<div data-dojo-type="dijit/layout/ContentPane" class="content grey">
    <span class="outer">
        <div>
            <span class="label">
                <label id="messageDiv"></label>
            </span>
            <div class="input">
                <span class="input">
                    <input maxHeight="150" style="width:100%; margin:0;" id="userList" />
                </span>
            </div>
        </div>
    </span>
    <div class="inputContainer grey" style="height:30px;">
        <span style="float:right; margin-top:5px;">
            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="general.cancel" />" onclick="pageImportUser.close()">
                <fmt:message key="general.cancel" />
            </button>
        </span>
        <span style="float:right; margin-top:5px;">
            <button data-dojo-type="dijit/form/Button" id="importUser" type="button" title="<fmt:message key="general.apply" />" onclick="pageImportUser.accept()">
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
