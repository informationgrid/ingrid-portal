<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script src='/ingrid-portal-mdek-application/dwr/interface/CatalogService.js'></script>
<script type="text/javascript">
var scriptScope = this;

// Storage for the current catalog. We need to get the uuid from somewhere
var currentCatalogData = null;

_container_.addOnLoad(function() {
	// Fill input fields with data from the current catalog

	var def = initSysLists();
	def.addCallback(function(res) {
		dojo.debug("update fields");
		if (catalogData != null) {
			updateInputFields(catalogData);
			currentCatalogData = catalogData;
		} else {
			scriptScope.reloadCatalogData();
		}
	
	});

	var checkbox = dojo.widget.byId("adminCatalogExpire");
	var inputField = dojo.widget.byId("adminCatalogExpiryDuration");

	dojo.event.connectOnce(checkbox, "onClick", function() {
		checkbox.checked ? inputField.enable() : inputField.disable();
	});

});


function initSysLists() {
	dojo.debug("Getting SysLists");
	var def = new dojo.Deferred();

	var selectWidgetIDs = ["adminCatalogLanguage", "adminCatalogCountry"]; 

	// Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
	var languageCode = UtilCatalog.getCatalogLanguage();

	var lstIds = [];
	dojo.lang.forEach(selectWidgetIDs, function(item){
		lstIds.push(dojo.widget.byId(item).listId);
	});

	CatalogService.getSysLists(lstIds, languageCode, {
		//preHook: UtilDWR.enterLoadingState,
		//postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			dojo.lang.forEach(selectWidgetIDs, function(widgetId) {
				var selectWidget = dojo.widget.byId(widgetId);
				dojo.debug("adding syslist-id: ");
				dojo.debug(selectWidget.listId);
				var selectWidgetData = res[selectWidget.listId];
				
				// Sort list by the display values
				selectWidgetData.sort(function(a, b) {
					return UtilString.compareIgnoreCase(a[0], b[0]);
				});
				
				selectWidget.dataProvider.setData(selectWidgetData);	
			});
			def.callback();
		},
		errorHandler:function(mes){
//			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}

function updateInputFields(catalogData) {
	dojo.widget.byId("adminCatalogName").setValue(catalogData.catalogName);
	dojo.widget.byId("adminCatalogPartnerName").setValue(catalogData.partnerName);
	dojo.widget.byId("adminCatalogProviderName").setValue(catalogData.providerName);
	dojo.widget.byId("adminCatalogCountry").setValue(catalogData.countryCode);
	dojo.widget.byId("adminCatalogLanguage").setValue(catalogData.languageCode);
	dojo.widget.byId("adminCatalogSpatialRef").setValue(catalogData.location.name);
	dojo.widget.byId("adminCatalogSpatialRef").location = catalogData.location;
	if (catalogData.workflowControl == "Y") {
		dojo.widget.byId("adminCatalogWorkflowControl").setValue(true);
	} else {
		dojo.widget.byId("adminCatalogWorkflowControl").setValue(false);
	}
	if (catalogData.expiryDuration != null && catalogData.expiryDuration > 0) {
		dojo.widget.byId("adminCatalogExpire").setValue(true);
		dojo.widget.byId("adminCatalogExpiryDuration").enable();
		dojo.widget.byId("adminCatalogExpiryDuration").setValue(catalogData.expiryDuration);
	} else {
		dojo.widget.byId("adminCatalogExpire").setValue(false);
		dojo.widget.byId("adminCatalogExpiryDuration").setValue("0");
		dojo.widget.byId("adminCatalogExpiryDuration").disable();
	}
}

scriptScope.reloadCatalogData = function() {
	CatalogService.getCatalogData({
		callback: function(res) {
			// Update catalog Data
			updateInputFields(res);
			currentCatalogData = res;
		},
		errorHandler:function(mes){
			dialog.show(message.get("general.error"), message.get("dialog.loadCatalogError"), dialog.WARNING);
			dojo.debug(mes);
		}
	});
}

scriptScope.saveCatalogData = function() {
	var newCatalogData = {};
	newCatalogData.uuid = currentCatalogData.uuid;
	newCatalogData.catalogName = dojo.widget.byId("adminCatalogName").getValue();
	newCatalogData.partnerName = dojo.widget.byId("adminCatalogPartnerName").getValue();
	newCatalogData.providerName = dojo.widget.byId("adminCatalogProviderName").getValue();
	newCatalogData.countryCode = dojo.widget.byId("adminCatalogCountry").getValue();
	newCatalogData.languageCode = dojo.widget.byId("adminCatalogLanguage").getValue();
	newCatalogData.location = dojo.widget.byId("adminCatalogSpatialRef").location;
	newCatalogData.expiryDuration = (dojo.widget.byId("adminCatalogExpire").checked ? dojo.widget.byId("adminCatalogExpiryDuration").getValue() : "0");
	newCatalogData.workflowControl = dojo.widget.byId("adminCatalogWorkflowControl").checked ? "Y" : "N";

	if (!isValidCatalog(newCatalogData)) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.catalog.requiredFieldsHint"), dialog.WARNING);
		return;
	}
	dojo.debugShallow(newCatalogData);

	CatalogService.storeCatalogData(newCatalogData, {
		callback: function(res) {
			// Update catalog Data
			updateInputFields(res);
			catalogData = res;
			currentCatalogData = res;
			initPageHeader();
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.saveSuccess"), dialog.INFO);

		},
		errorHandler:function(errMsg, err){
			if (errMsg.indexOf("USER_HAS_NO_PERMISSION_ON_ENTITY") != -1) {
				dialog.show(message.get("general.error"), message.get("dialog.admin.catalog.permissionError"), dialog.WARNING);				

			} else {
				dialog.show(message.get("general.error"), message.get("dialog.storeCatalogError"), dialog.WARNING);
			}

			dojo.debug(errMsg);
		}
	});	
}

scriptScope.selectSpatialReference = function() {
	var def = new dojo.Deferred();
	dialog.showPage(message.get("dialog.admin.catalog.selectLocation.title"), "mdek_admin_catalog_spatial_reference_dialog.jsp", 530, 230, true, { resultHandler: def });

	def.addCallback(function(result) {
		var spatialRefWidget = dojo.widget.byId("adminCatalogSpatialRef");
		spatialRefWidget.setValue(result.name);
		spatialRefWidget.location = result;
	});
}

function isValidCatalog(cat) {
	return (dojo.string.trim(cat.countryCode).length != 0
		 && dojo.string.trim(cat.languageCode).length != 0
		 && dojo.string.trim(cat.location.name).length != 0
		 && dojo.validate.isInteger(cat.expiryDuration)
		 && dojo.validate.isInRange(cat.expiryDuration, { min:0, max:2147483647 }));
}

</script>
</head>

<body>
<!-- CONTENT START -->
	<div dojoType="ContentPane" layoutAlign="client">
	
		<div id="contentSection" class="contentBlockWhite top">
			<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=catalog-administration-1#catalog-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="Hilfe">[?]</a>
			</div>
			<div id="adminCatalog" class="content">

				<!-- LEFT HAND SIDE CONTENT START -->
				<div class="spacer"></div>
				<div class="spacer"></div>
				<div class="inputContainer field grey noSpaceBelow">
					<span class="label"><label for="adminCatalogName" onclick="javascript:dialog.showContextHelp(arguments[0], 8001, 'Name des Kataloges')"><fmt:message key="dialog.admin.catalog.catalogName" /></label></span>
					<span class="input spaceBelow"><input type="text" id="adminCatalogName" class="w640" dojoType="ingrid:ValidationTextBox" /></span>

					<span class="label"><label for="adminCatalogPartnerName" onclick="javascript:dialog.showContextHelp(arguments[0], 8002, 'Name des Partners')"><fmt:message key="dialog.admin.catalog.partnerName" /></label></span>
					<span class="input spaceBelow"><input type="text" id="adminCatalogPartnerName" class="w640" dojoType="ingrid:ValidationTextBox" /></span>
		
					<span class="label"><label for="adminCatalogProviderName" onclick="javascript:dialog.showContextHelp(arguments[0], 8003, 'Name des Anbieters')"><fmt:message key="dialog.admin.catalog.providerName" /></label></span>
					<span class="input spaceBelow"><input type="text" id="adminCatalogProviderName" class="w640" dojoType="ingrid:ValidationTextBox" /></span>
		
					<span class="label required"><label for="adminCatalogCountry" onclick="javascript:dialog.showContextHelp(arguments[0], 8004, 'Staat')"><fmt:message key="dialog.admin.catalog.state" />*</label></span>
					<span class="input"><input class="spaceBelow" dojoType="ingrid:Select" required="true" style="width:622px;" listId="6200" id="adminCatalogCountry" /></span>

					<span class="label required"><label for="adminCatalogLanguage" onclick="javascript:dialog.showContextHelp(arguments[0], 8005, 'Katalogsprache')"><fmt:message key="dialog.admin.catalog.language" />*</label></span>
					<span class="input"><input class="spaceBelow" dojoType="ingrid:Select" required="true" style="width:622px;" disabled="true" listId="99999999" id="adminCatalogLanguage" /></span>

					<span class="label required"><label for="adminCatalogSpatialRef" onclick="javascript:dialog.showContextHelp(arguments[0], 8006, 'Raumbezug')"><fmt:message key="dialog.admin.catalog.location" />*</label></span>
					<span class="functionalLink marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" onClick="javascript:scriptScope.selectSpatialReference();" title="Raumbezug ausw&auml;hlen [Popup]"><fmt:message key="dialog.admin.catalog.locationLink" /></a></span>
					<span class="input spaceBelow"><input type="text" required="true" widgetId="adminCatalogSpatialRef" class="w640" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
					
					<div class="checkboxContainer">
						<span class="input"><input type="checkbox" id="adminCatalogWorkflowControl" dojoType="Checkbox" /><label class="inActive"><fmt:message key="dialog.admin.catalog.activateWorkflowControl" /></label></span>
						<span class="input"><input type="checkbox" id="adminCatalogExpire" dojoType="Checkbox" /><label class="inActive"><fmt:message key="dialog.admin.catalog.expireAfter" /> <input widgetId="adminCatalogExpiryDuration" class="w033" min="1" max="2147483647" maxlength="10" dojoType="IntegerTextbox" /> <fmt:message key="dialog.admin.catalog.days" /></label></span>
					</div>
		
					<div class="spacerField"></div>
				</div>
		<!-- 
						<div class="inputContainer">
		          			<span class="button w644"><a href="#" class="buttonBlue" title="Speichern">Speichern</a><a href="#" class="buttonBlue" title="Abbrechen">Abbrechen</a></span>
						</div>
		 -->
				<div class="inputContainer">
					<span class="button w644" style="height:20px !important;">
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="Speichern" onClick="javascript:scriptScope.saveCatalogData();"><fmt:message key="dialog.admin.catalog.save" /></button>
						</span>
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="Zur&uuml;cksetzen" onClick="javascript:scriptScope.reloadCatalogData();"><fmt:message key="dialog.admin.catalog.reset" /></button>
						</span>
						<span id="adminCatalogLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
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
