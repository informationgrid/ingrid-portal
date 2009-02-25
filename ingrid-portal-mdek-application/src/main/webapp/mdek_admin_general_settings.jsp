<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">
var scriptScope = this;
_container_.addOnLoad(function() {
	loadAndSetGeneralValues();
});

function loadAndSetGeneralValues() {
	var def = UtilCatalog.getGenericValuesDef([
	                                       	UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL,
	                                       	UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL
	                                       	]);

	def.addCallback(function (valueMap) {
		setGeneralValues(valueMap);
	});

	def.addErrback(function(msg) {
		dojo.debug("error: "+msg);
		dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), msg), dialog.WARNING, null, 350, 350);				
	});
}

function setGeneralValues(valueMap) {
	setAutosaveValues(valueMap);
	setSessionRefreshValues(valueMap);
}

function setAutosaveValues(valueMap) {
	if (valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] && valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] > 0) {
		var autosaveEnabled = true;
		var autosaveInterval = valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL];

		dojo.widget.byId("autosaveCheckbox").setValue(autosaveEnabled);
		dojo.widget.byId("autosaveInterval").setValue(autosaveInterval);

	} else {
		dojo.widget.byId("autosaveCheckbox").setValue(false);
		dojo.widget.byId("autosaveInterval").setValue(10);
	}
}

function setSessionRefreshValues(valueMap) {
	if (valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] && valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] > 0) {
		var sessionRefreshEnabled = true;
		var sessionRefreshInterval = valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL];

		dojo.widget.byId("sessionRefreshCheckbox").setValue(sessionRefreshEnabled);
		dojo.widget.byId("sessionRefreshInterval").setValue(sessionRefreshInterval);

	} else {
		dojo.widget.byId("sessionRefreshCheckbox").setValue(false);
		dojo.widget.byId("sessionRefreshInterval").setValue(10);
	}
}

scriptScope.saveGeneralValues = function() {
	var valueMap = {};
	addAutosaveValuesToMap(valueMap);
	addSessionRefreshValuesToMap(valueMap);

	var def = UtilCatalog.storeGenericValuesDef(valueMap);
	def.addCallback(function() {
		dialog.show(message.get("general.hint"), message.get("dialog.admin.generalSettingsUpdated"), dialog.INFO);
	});
	def.addErrback(function(msg) {
		dojo.debug("error: "+msg);
		dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), msg), dialog.WARNING, null, 350, 350);				
	});
}

function addAutosaveValuesToMap(valueMap) {
	if (dojo.widget.byId("autosaveCheckbox").checked) {
		valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] = dojo.widget.byId("autosaveInterval").getValue();

	} else {
		valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] = 0;
	}
}

function addSessionRefreshValuesToMap(valueMap) {
	if (dojo.widget.byId("sessionRefreshCheckbox").checked) {
		valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] = dojo.widget.byId("sessionRefreshInterval").getValue();

	} else {
		valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] = 0;
	}
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("generalSettingsLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("generalSettingsLoadingZone"), "hidden");
}

</script>

</head>

<body>

<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 8027)" title="Hilfe">[?]</a>
		</div>
		<div class="content">
	
			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div class="inputContainer field grey">
				<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8010)"><fmt:message key="dialog.admin.catalog.general.settings" /></label></span>

				<div class="checkboxContainer">
					<span class="input"><input type="checkbox" id="autosaveCheckbox" dojoType="Checkbox" /><label class="inActive"><fmt:message key="dialog.admin.catalog.general.autosave" /> <input id="autosaveInterval" class="w033" min="1" max="60" maxlength="10" dojoType="IntegerTextbox" /> <fmt:message key="dialog.admin.catalog.general.minutes" /></label></span>
					<span class="input"><input type="checkbox" id="sessionRefreshCheckbox" dojoType="Checkbox" /><label class="inActive"><fmt:message key="dialog.admin.catalog.general.sessionRefresh" /> <input id="sessionRefreshInterval" class="w033" min="1" max="60" maxlength="10" dojoType="IntegerTextbox" /> <fmt:message key="dialog.admin.catalog.general.minutes" /></label></span>
				</div>

				<div class="spacerField"></div>

			</div>

			<div class="inputContainer">
				<span class="button w644" style="height:20px !important;">
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="Speichern" onClick="javascript:scriptScope.saveGeneralValues();"><fmt:message key="dialog.admin.catalog.general.save" /></button>
					</span>
					<span id="generalSettingsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>
			<!-- LEFT HAND SIDE CONTENT END -->        
		</div>

	</div>
</div>
<!-- CONTENT END -->
</body>
</html>
