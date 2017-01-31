<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
        "dojo/dom-construct",
        "dojo/on",
        "dojo/dom",
        "dojo/query",
        "dojo/topic",
        "dijit/form/RadioButton",
        "ingrid/dialog",
        "dijit/registry"
    ], function(array, construct, on, dom, query, topic, RadioButton, dialog, registry) {
        var thisDialog = _container_;

        on(_container_, "Load", function () {
            var types = sysLists[8000];

            console.log("Publishing event: '/afterInitDialog/ChooseWizard'");
            topic.publish("/afterInitDialog/ChooseWizard", { types: types });

            createObjectTypesRadioBoxes(types);
        });

        function createObjectTypesRadioBoxes(types) {
            array.forEach(types, function(item) {
                var wrapper = construct.create("div", {
                    "class": "spaceBelow"
                });
                var radio = new RadioButton({
                    value: item[1],
                    name: "assistantRadioSelect",
                    showLabel: true
                });
                construct.place(radio.domNode, wrapper);
                construct.create("label", {
                    for: radio.id,
                    "class": "inActive",
                    innerHTML: " " + item[0]
                }, wrapper);
                construct.place(wrapper, "wizardObjTypes");
            });
        }

        function createObject() {
            var type = query("input[type=radio][name=assistantRadioSelect]:checked")[0].value;

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
                registry.byId("objectClass").set("value", "Class" + type);
            }
        }

        function closeThisDialog() {
            thisDialog.hide();
        }

        pageCreateWizard.createObject = createObject;

    });
</script>
</head>

<body>

	<div layoutAlign="client">
		<div class="content">
			<div>
				<fmt:message key="dialog.wizard.select.title" />
			</div>
			<br>

    		<span style="float:left;">
    			<div class="checkboxContainer" id="resultList">
                    <h2 class="spaceBelow"><fmt:message key="dialog.wizard.objectTypes" /></h2>
                    <div id="wizardObjTypes">
                        <!-- dynamically created checkboxes to represent the different types to be created -->
                    </div>

                    <h2 class="spaceBelow"><fmt:message key="dialog.wizard.assistents" /></h2>
					<div class="spaceBelow">
						<input type="radio" name="assistantRadioSelect" id="assistantRadioSelect1" value="assistantCreate" data-dojo-type="dijit/form/RadioButton" style="vertical-align: text-bottom;" checked>
						<label for="assistantRadioSelect1" class="inActive"><fmt:message key="dialog.wizard.select.create" /></label>
					</div>
					<div class="spaceBelow">
						<input type="radio" name="assistantRadioSelect" id="assistantRadioSelect2" value="assistantGetCap" data-dojo-type="dijit/form/RadioButton" style="vertical-align: text-bottom;">
						<label for="assistantRadioSelect2" class="inActive"><fmt:message key="dialog.wizard.select.getCap" /></label>
					</div>
    			</div>
    		</span>
    		
			<div id="dialogButtonBar" class="dijitDialogPaneActionBar inputContainer grey" style="height:37px;">
		        <span style="float:right; padding:5px 0;"><button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.wizard.select.continue" />" onclick="pageCreateWizard.createObject()"><fmt:message key="dialog.wizard.select.continue" /></button></span>
			</div>
	  	</div>
	</div>

</body>
</html>
