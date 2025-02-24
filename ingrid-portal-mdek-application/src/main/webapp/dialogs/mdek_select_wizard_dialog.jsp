<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2025 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
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
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.select.wizard.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
    var pageCreateWizard = this;

    require([
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojo/on",
        "dojo/dom",
        "dojo/query",
        "dojo/topic",
        "dijit/form/Button",
        "dijit/form/RadioButton",
        "dijit/registry",
        "ingrid/dialog",
        "ingrid/hierarchy/dirty",
        "ingrid/utils/Syslist",
        "ingrid/utils/Tree"
    ], function(array, lang, construct, on, dom, query, topic, Button, RadioButton, registry, dialog, dirty, Syslists, UtilTree) {
        var thisDialog = _container_;
        var alreadyChecked = false;

        on(_container_, "Load", function () {
            var types = Syslists.getObjectClassList();
            var assistants = [
                ['<fmt:message key="dialog.wizard.select.create" />', "assistantCreate"],
                ['<fmt:message key="dialog.wizard.select.getCap" />', "assistantGetCap"]
            ];
            var extraButtons = [];

            console.log("Publishing event: '/afterInitDialog/ChooseWizard'");
            var params = { types: types, assistants: assistants, buttons: extraButtons };
            topic.publish("/afterInitDialog/ChooseWizard", params);

            // check if there's only one option
            // in that case close dialog and select the only available option
            var singleHandled = handleSingleOption(params);
            if (singleHandled) return;

            addRadioBoxes(params.types, "wizardObjTypes");

            if (params.assistants.length > 0) {
                addRadioBoxes(params.assistants, "wizardAssistantTypes");
            } else {
                query("#wizardAssistantTypesContainer").addClass("hide");
                query("#wizardAssistantTypesContainer + div").addClass("hide");
            }

            addExtraButtons(extraButtons);

            // remove new node if cancel the dialog
            on( this, "Cancel", function() {

                // delete node from tree
                UtilTree.deleteNode("dataTree", "newNode");

                // select root node
                UtilTree.selectNode("dataTree", "objectRoot", true);

                // activate events when root node is selected (hide form)
                topic.publish("/selectNode", {
                    id: "dataTree",
                    node: { id: "objectRoot" }
                });

                // reset dirty flag
                lang.hitch(dirty, dirty.resetDirtyFlag)();
            });

            registry.byId("pageCreateWizardContainer").resize();
        });

        function setTreeIcon(clazz) {
            // TODO: should we better get the newNode-node? could be a race condition here
            var selectedNode = registry.byId("dataTree").selectedNode;
            var iconNode = query(".TreeIcon", selectedNode.domNode);
            iconNode[0].classList.forEach(function(cl) { if (cl.indexOf("TreeIconClass") === 0) iconNode.removeClass(cl); })
            iconNode.addClass("TreeIconClass" + clazz + "_B");
        }

        function addRadioBoxes(types, containerId) {
            array.forEach(types, function(item) {
                var wrapper = construct.create("div", {
                    "class": "spaceBelow"
                });
                var radio = new RadioButton({
                    value: item[1],
                    id: "assistantRadioSelect_" + item[1],
                    name: "assistantRadioSelect",
                    showLabel: true,
                    checked: !alreadyChecked
                });
                construct.place(radio.domNode, wrapper);
                construct.create("label", {
                    for: radio.id,
                    "class": "inActive",
                    innerHTML: " " + item[0]
                }, wrapper);
                construct.place(wrapper, containerId);
                alreadyChecked = true;
            });
        }

        function createObject() {
            var type = query("input[type=radio][name=assistantRadioSelect]:checked")[0].value;
            createObjectByType(type);
        }

        function createObjectByType(type) {
            closeThisDialog();

            // if an assistant is called
            if (type === "assistantCreate" || type === "assistantGetCap") {

                if (type === "assistantCreate") {
                    dialog.showPage("<fmt:message key='dialog.wizard.create.title' />", "dialogs/mdek_create_object_wizard_dialog.jsp", 755, 750, true);
                } else {
                    dialog.showPage("<fmt:message key='dialog.wizard.getCap.title' />", "dialogs/mdek_get_capabilities_wizard_dialog.jsp", 755, 750, true);
                }

            } else {
                // otherwise we just pre-set the object type 
                // set class delayed since new object request also sets class before
                setTimeout(function () {
                    registry.byId("objectClass").set("value", "Class" + type);
                    setTreeIcon(type);
                    topic.publish("/afterCloseDialog/ChooseWizard");
                }, 300);

            }

        }

        function addExtraButtons(buttons) {
            array.forEach(buttons, function(button) {
                var btn = new Button({
                    label: button.label,
                    onClick: lang.partial(button.callback, closeThisDialog)
                });
                construct.place(btn.domNode, "wizardExtraButtons");
            });
        }

        /**
         * If only one option is available then execute this one.
         * 
         * @returns true if a single operation was found and executed, otherwise false
         */
        function handleSingleOption(params) {
            if (params.types.length === 1 && params.assistants.length === 0 && params.buttons.length === 0) {
                createObjectByType(params.types[0][1]);
            } else if (params.types.length === 0 && params.assistants.length === 1 && params.buttons.length === 0) {
                createObjectByType(params.assistants[0]);
            } else if (params.types.length === 0 && params.assistants.length === 0 && params.buttons.length === 1) {
                closeThisDialog();
                params.buttons[0].callback(closeThisDialog);
            } else {
                return false;
            }

            return true;
        }
        function closeThisDialog() {
            thisDialog.hide();
            topic.publish("/selectNode", {
                id: "dataTree",
                node: { id: "newNode", nodeAppType: "O" }
            });
        }

        pageCreateWizard.createObject = createObject;

    });
</script>
</head>

<body>

    <div id="pageCreateWizardContainer" data-dojo-type="dijit/layout/BorderContainer" style="width: 100%; height: 300px;">
        <div data-dojo-type="dijit/layout/ContentPane" style="background-color: white" data-dojo-props="region:'top'">
            <fmt:message key="dialog.wizard.select.title" />
        </div>

        <div id="wizardAssistantTypesContainer" data-dojo-type="dijit/layout/ContentPane" style="padding: 10px" data-dojo-props="region:'left'">
            <h2 class="spaceBelow"><fmt:message key="dialog.wizard.assistents" /></h2>
            <div id="wizardAssistantTypes">
                <!-- dynamically created radio boxes to represent the different assistants -->
            </div>
        </div>
        
        <div data-dojo-type="dijit/layout/ContentPane" style="padding: 10px" data-dojo-props="region:'center'">
            <h2 class="spaceBelow"><fmt:message key="dialog.wizard.objectTypes" /></h2>
            <div id="wizardObjTypes">
                <!-- dynamically created radio boxes to represent the different types to be created -->
            </div>
        </div>
    </div>
    
    <div id="dialogButtonBar" class="dijitDialogPaneActionBar inputContainer grey" style="height:37px;">
        <span style="float:left; padding:5px 0;" id="wizardExtraButtons"></span>
        <span style="float:right; padding:5px 0;"><button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.wizard.select.continue" />" onclick="pageCreateWizard.createObject()"><fmt:message key="dialog.wizard.select.continue" /></button></span>
    </div>

</body>
</html>
