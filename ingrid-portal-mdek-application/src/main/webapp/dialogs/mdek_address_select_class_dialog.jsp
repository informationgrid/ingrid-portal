<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2023 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
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
    "ingrid/utils/Events",
    "ingrid/utils/Tree"
], function(on, dom, registry, topic, UtilStore, UtilEvents, TreeUtil) {

    var customParams = _container_.customParams;
    var self = _container_;

    on(_container_, "Load", function(){

        var msgDiv = dom.byId("messageDiv");
        msgDiv.innerHTML = "<fmt:message key='dialog.createAddressMessage' />";

        var parent = getNonFolderParentItem(customParams.parentId);

        var parentClass;
        if (parent.objectClass !== null && parent.objectClass !== undefined) {
            parentClass = parent.objectClass;

        } else if (parent.id === "addressRoot") {
            parentClass = -1;

        } else if (parent.id === "addressFreeRoot") {
            // This should never be the case...
            parentClass = -2;
        }

        // Init the select box dp
        var addressClassWidget = registry.byId("addressClassSelect");
        var valueList = getOptionsForParentClass(parentClass, customParams.parentClass === 1000);

        //addressClassWidget.dataProvider.setData(valueList);
        UtilStore.updateWriteStore("addressClassSelect", valueList, {label:'0', identifier:'1'});
        addressClassWidget.setValue(valueList[0][1]);

        console.log("Publishing event: '/afterInitDialog/AddressSelectClass'");
        topic.publish("/afterInitDialog/AddressSelectClass");
    });

    function getNonFolderParentItem(parentId) {
        var parent = null;
        do {
            var parentNode = TreeUtil.getNodeById("dataTree", parentId);
            if (parentNode.item.objectClass !== 1000) {
                parent = parentNode.item;
            }
            parentId = parentNode.item.parent;

        } while (!parent);
        return parent;
    }

    function getOptionsForParentClass(parentClass, isFolder) {
        var valueList = [];
        switch (parentClass) {
            case -2:    // Free Address Root
                valueList.push(["<fmt:message key='address.type.custom' />", "3"]);
                break;

            case -1: // Root Address
                valueList.push(["<fmt:message key='address.type.institution' />", "0"]);
                if (!isFolder) {
                    valueList.push(["<fmt:message key='address.type.custom' />", "3"]);
                }
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
        return valueList;
    }

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
