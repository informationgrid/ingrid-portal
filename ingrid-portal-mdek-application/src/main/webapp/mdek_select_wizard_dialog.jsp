<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V3</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {});

scriptScope.openWizard = function() {
	var generalWizardSelected = dojo.byId("assistantRadioSelect1").checked;

	scriptScope.closeDialog();
	if (generalWizardSelected) {
		dialog.showPage(message.get("dialog.wizard.create.title"), "mdek_create_object_wizard_dialog.jsp", 755, 600, true);

	} else {
		_container_.closeWindow();
		dialog.showPage(message.get("dialog.wizard.getCap.title"), "mdek_get_capabilities_wizard_dialog.jsp", 755, 195, true);
	}
}

scriptScope.closeDialog = function() {
	_container_.closeWindow();
}

</script>
</head>

<body>

<div dojoType="ContentPane">
	<div layoutAlign="client" class="contentBlockWhite top">
		<div class="content">
			<div class="w296">
				<fmt:message key="dialog.wizard.select.title" />
			</div>
			<br>

    		<span style="float:left;">
    			<div class="checkboxContainer" id="resultList" style="width: 320px; height: 35px; overflow: auto;">
					<div>
						<input type="radio" name="assistantRadioSelect" id="assistantRadioSelect1" style="margin-right: 3px;" checked />
						<fmt:message key="dialog.wizard.select.create" />
					</div>
					<div>
						<input type="radio" name="assistantRadioSelect" id="assistantRadioSelect2" style="margin-right: 3px;">
							<fmt:message key="dialog.wizard.select.getCap" />
						</input>
					</div>
    			</div>
    		</span>
			<div class="inputContainer grey noSpaceBelow half" style="height:30px;">
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="<fmt:message key="dialog.wizard.select.cancel" />" onClick="javascript:scriptScope.closeDialog();"><fmt:message key="dialog.wizard.select.cancel" /></button></span>
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="<fmt:message key="dialog.wizard.select.continue" />" onClick="javascript:scriptScope.openWizard();"><fmt:message key="dialog.wizard.select.continue" /></button></span>
			</div>
	  	</div>
	</div>
</div>

</body>
</html>
