<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
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

require(["dojo/on", "dojo/dom", "dojo/topic", "ingrid/dialog", "dijit/registry"],
    function(on, dom, topic, dialog, registry) {
		on(_container_, "Load", function() {
			console.log("Publishing event: '/afterInitDialog/ChooseWizard'");
		    topic.publish("/afterInitDialog/ChooseWizard");
		});

        function openWizard() {
            var generalWizardSelected = dom.byId("assistantRadioSelect1").checked;

            closeThisDialog();
            if (generalWizardSelected) {
                dialog.showPage("<fmt:message key='dialog.wizard.create.title' />", "dialogs/mdek_create_object_wizard_dialog.jsp", 755, 750, true);

            } else {
                //_container_.closeWindow();
                dialog.showPage("<fmt:message key='dialog.wizard.getCap.title' />", "dialogs/mdek_get_capabilities_wizard_dialog.jsp", 755, 750, true);
            }
        }

        function closeThisDialog() {
            registry.byId("pageDialog").hide();
        }

        pageCreateWizard.openWizard = openWizard;
        pageCreateWizard.closeThisDialog = closeThisDialog;

    }
);

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
    			<div class="checkboxContainer" id="resultList" style="width: 320px; height: 55px; overflow: auto;">
					<div class="spaceBelow">
						<input type="radio" name="assistantRadioSelect" id="assistantRadioSelect1" style="vertical-align: text-bottom;" checked>
						<label for="assistantRadioSelect1" class="inActive"><fmt:message key="dialog.wizard.select.create" /></label>
					</div>
					<div>
						<input type="radio" name="assistantRadioSelect" id="assistantRadioSelect2" style="vertical-align: text-bottom;">
						<label for="assistantRadioSelect2" class="inActive"><fmt:message key="dialog.wizard.select.getCap" /></label>
					</div>
    			</div>
    		</span>
    		
			<div id="dialogButtonBar" class="dijitDialogPaneActionBar inputContainer grey" style="height:37px;">
		        <span style="float:right; padding:5px 0;"><button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.wizard.select.cancel" />" onclick="pageCreateWizard.closeThisDialog()"><fmt:message key="dialog.wizard.select.cancel" /></button></span>
		        <span style="float:left; padding:5px 0;"><button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.wizard.select.continue" />" onclick="pageCreateWizard.openWizard()"><fmt:message key="dialog.wizard.select.continue" /></button></span>
			</div>
	  	</div>
	</div>

</body>
</html>
