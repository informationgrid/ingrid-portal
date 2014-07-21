<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
var dialogCreateAddress = null;

require([
    "dojo/on",
    "dojo/dom",
    "dijit/registry",
    "dojo/topic",
    "ingrid/utils/Store",
    "ingrid/utils/Events"
], function(on, dom, registry, topic, UtilStore, UtilEvents) {

    var customParams = _container_.customParams;
    var self = _container_;

    on(_container_, "Load", function(){

        var msgDiv = dom.byId("messageDiv");
        msgDiv.innerHTML = "<fmt:message key='dialog.createAddressMessage' />";
        var parentClass;
        if (typeof(customParams.parentClass) != "undefined") {
            parentClass = customParams.parentClass;

        } else if (customParams.parentId == "addressRoot") {
            parentClass = -1;

        } else if (customParams.parentId == "addressFreeRoot") {
            // This should never be the case...
            parentClass = -2;
        }

        // Init the select box dp
        var addressClassWidget = registry.byId("addressClassSelect");
        var valueList = [];
        switch (parentClass) {
            case -2:    // Free Address Root
                valueList.push(["<fmt:message key='address.type.custom' />", "3"]);
                break;

            case -1: // Root Address
                valueList.push(["<fmt:message key='address.type.institution' />", "0"]);
                valueList.push(["<fmt:message key='address.type.custom' />", "3"]);
                break;

            case 0: // Institution
                valueList.push(["<fmt:message key='address.type.institution' />", "0"]);
                valueList.push(["<fmt:message key='address.type.unit' />", "1"]);
                valueList.push(["<fmt:message key='address.type.person' />", "2"]);
                break;

            case 1: // Unit
                valueList.push(["<fmt:message key='address.type.unit' />", "1"]);
                valueList.push(["<fmt:message key='address.type.person' />", "2"]);
                break;

            case 2: // Person (a person must not have any subAddresses)
                console.error("Error in select address class dialog - A 'person' is not allowed to have any sub addresses!");
                break;

            case 3: // Custom Address (a custom address must not have any subAddresses)
                console.error("Error in select address class dialog - A 'custom address' is not allowed to have any sub addresses!");
                break;

            default:
                console.error("Error in select address class dialog - Unknown parent address type: "+parentClass);
                break;
        }
        //addressClassWidget.dataProvider.setData(valueList);
        UtilStore.updateWriteStore("addressClassSelect", valueList, {label:'0', identifier:'1'});
        addressClassWidget.setValue(valueList[0][1]);

        console.log("Publishing event: '/afterInitDialog/AddressSelectClass'");
        topic.publish("/afterInitDialog/AddressSelectClass");
    });

    // 'Yes Button' onClick function
    function yesButtonFunc() {
        if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/AddressSelectClass")) return;
        // Callback with selected address class
        var caller = customParams.resultHandler;
        var value = parseInt(registry.byId("addressClassSelect").getValue());
        self.hide();
        caller.resolve(value);
    }

    // 'No Button' onClick function
    function noButtonFunc() {
        customParams.resultHandler.reject();
        self.hide();
    }

    dialogCreateAddress = {
        yesButtonFunc: yesButtonFunc,
        noButtonFunc: noButtonFunc
    };
});

</script>
</head>

<body>
    <div id="contentPane" layoutAlign="client" class="">
        <div id="dialogContent" class="content">
            <div id="messageDiv" class="field" style="padding-bottom: 5px;">
            </div>

            <div>
              <input data-dojo-type="dijit/form/Select" autoComplete="false" style="width:100%;" id="addressClassSelect" />
            </div>

            <div id="dialogButtonBar" class="dijitDialogPaneActionBar inputContainer grey" style="height:37px;">
                <span style="float:left; padding:5px 0;"><button id="cancelNewAddress" data-dojo-type="dijit/form/Button" title="<fmt:message key='general.cancel' />" onclick="dialogCreateAddress.noButtonFunc()"><fmt:message key="general.cancel" /></button></span>
                <span style="float:right; padding:5px 0;"><button id="addAddress" data-dojo-type="dijit/form/Button" title="<fmt:message key='general.addresses.add' />" onclick="dialogCreateAddress.yesButtonFunc()"><fmt:message key="general.addresses.add" /></button></span>
            </div>
        </div>
    </div>
</body>
</html>
