<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.operationTable.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />


<script type="text/javascript">
var constraintsScriptScope = _container_;

dojo.connect(constraintsScriptScope, "onLoad", function(){
    var def = createDOMElements();
});

/*
dojo.connect(constraintsScriptScope, "onUnload", function(){
    dijit.byId("availabilityUseConstraints").invalidate();
});
*/

function createDOMElements() {
    var Structure = [
        {field: 'name', name: "<fmt:message key='dialog.useConstraints.valueColumn' />",width: 703-scrollBarWidth+'px'}
    ];
    createDataGrid("constraintsList", null, Structure, null);

    var def = readSysListData(6020);
    def.then(function(syslistData) {
        var tableData = UtilSyslist.convertSysListToTableData(syslistData);
        UtilGrid.setTableData("constraintsList", tableData);
    });

    return def;
}

function readSysListData(listId) {
    var def = new dojo.Deferred();

    var languageCode = UtilCatalog.getCatalogLanguage();
    CatalogService.getSysLists([listId], languageCode, {
        callback: function(res) {
            def.callback(res[listId]);
    }});

    return def;
}

</script> 
</head>

<body>
  <div class="">
<!-- NO Help
    <div id="winNavi" style="top:0; height:20px;">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-11#maintanance-of-objects-11', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
    </div>
-->
    <div id="constraintsContent" class="content">
        <!-- CONTENT START -->
        <div class="inputContainer field grey">
            <div class="tableContainer">
                <div id="constraintsList"></div>
            </div>
        </div>
        <!-- CONTENT END -->
    </div>
  </div>
</body>
</html>
