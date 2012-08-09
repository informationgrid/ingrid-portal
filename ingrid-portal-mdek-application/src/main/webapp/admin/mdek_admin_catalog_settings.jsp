<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
            dojo.require("dijit.form.Select");
			dojo.require("dijit.form.CheckBox");
			dojo.require("dijit.form.NumberTextBox");
			
			// extend the typical widget to accept listIds for syslists
            // deprecated! used a different way now ... or?
			dojo.extend(dijit._Widget, { listId:"0" });
			
            var scriptScopeCatSettings = _container_;
			//var catalogData = null;
            
            // Storage for the current catalog. We need to get the uuid from somewhere
            var currentCatalogData = null;
            
            dojo.connect(_container_, "onLoad", function(){
				// create select boxes for syslists
				var storeProps = {identifier: '1', label: '0'};
				createSelectBox("adminCatalogCountry", null, storeProps, null);
				createSelectBox("adminCatalogLanguage", null, storeProps, null);
				
                // Fill input fields with data from the current catalog
                //var def = 
				initSysLists();
                console.debug("update fields");
                if (catalogData != null) {
                    updateInputFields(catalogData);
                    currentCatalogData = catalogData;
                }
                else {
                    reloadCatalogData();
                }
                    
                var checkbox = dijit.byId("adminCatalogExpire");
                var inputField = dijit.byId("adminCatalogExpiryDuration");
                
                dojo.connect(checkbox, "onClick", function(){ //!!!connectOnce
                    checkbox.checked ? inputField.set('disabled', false) : inputField.set('disabled', true);
                });
                
            });
            
            
            function initSysLists(){
                var selectWidgetIDs = ["adminCatalogLanguage", "adminCatalogCountry"];
				
				dojo.forEach(selectWidgetIDs, function(widgetId){
                    var selectWidget = dijit.byId(widgetId);
                    console.debug("adding syslist-id: ");
                    console.debug(selectWidget.listId);
                    var selectWidgetData = sysLists[selectWidget.listId];
                    
                    // Sort list by the display values
                    /*selectWidgetData.sort(function(a, b){
                        return UtilString.compareIgnoreCase(a[0], b[0]);
                    });*/
                    
					var updatedStore = new dojo.data.ItemFileReadStore({
						data: {	identifier: '1', label: '0', items: dojo.clone(selectWidgetData) }
					});
					selectWidget.setStore(updatedStore);
                });
            }
            
            function updateInputFields(catalogData){
                dijit.byId("adminCatalogName").setValue(catalogData.catalogName);
                dijit.byId("adminCatalogNamespace").setValue(catalogData.catalogNamespace);
                dijit.byId("adminCatalogPartnerName").setValue(catalogData.partnerName);
                dijit.byId("adminCatalogProviderName").setValue(catalogData.providerName);
                dijit.byId("adminCatalogCountry").setValue(catalogData.countryCode);
                dijit.byId("adminCatalogLanguage").setValue(catalogData.languageCode);
                dijit.byId("adminCatalogSpatialRef").setValue(catalogData.location.name);
                dijit.byId("adminCatalogSpatialRef").location = catalogData.location;
                if (catalogData.workflowControl == "Y") {
                    dijit.byId("adminCatalogWorkflowControl").setValue(true);
                }
                else {
                    dijit.byId("adminCatalogWorkflowControl").setValue(false);
                }
                if (catalogData.expiryDuration != null && catalogData.expiryDuration > 0) {
                    dijit.byId("adminCatalogExpire").setValue(true);
                    dijit.byId("adminCatalogExpiryDuration").set('disabled', false);
                    dijit.byId("adminCatalogExpiryDuration").setValue(catalogData.expiryDuration);
                }
                else {
                    dijit.byId("adminCatalogExpire").setValue(false);
                    dijit.byId("adminCatalogExpiryDuration").setValue("0");
                    dijit.byId("adminCatalogExpiryDuration").set('disabled', true);
                }
            }
            
            scriptScopeCatSettings.reloadCatalogData = function() {
                CatalogService.getCatalogData({
                    callback: function(res){
                        // Update catalog Data
                        updateInputFields(res);
                        currentCatalogData = res;
                        catalogData = res;
                    },
                    errorHandler: function(mes){
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.loadCatalogError' />", dialog.WARNING);
                        console.debug(mes);
                    }
                });
            }
            
            scriptScopeCatSettings.saveCatalogData = function(){
                var newCatalogData = {};
                newCatalogData.uuid = currentCatalogData.uuid;
                newCatalogData.catalogName = dijit.byId("adminCatalogName").getValue();
                newCatalogData.catalogNamespace = dijit.byId("adminCatalogNamespace").getValue();
                newCatalogData.partnerName = dijit.byId("adminCatalogPartnerName").getValue();
                newCatalogData.providerName = dijit.byId("adminCatalogProviderName").getValue();
                newCatalogData.countryCode = dijit.byId("adminCatalogCountry").getValue();
                newCatalogData.languageCode = dijit.byId("adminCatalogLanguage").getValue();
                newCatalogData.location = dijit.byId("adminCatalogSpatialRef").location;
                newCatalogData.expiryDuration = (dijit.byId("adminCatalogExpire").checked ? dijit.byId("adminCatalogExpiryDuration").getValue() : "0");
                newCatalogData.workflowControl = dijit.byId("adminCatalogWorkflowControl").checked ? "Y" : "N";
                console.debug("validating");
                if (!isValidCatalog(newCatalogData)) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.requiredFieldsHint' />", dialog.WARNING);
                    return;
                }
                console.debug("storing");
                CatalogService.storeCatalogData(newCatalogData, {
                    callback: function(res){
                        // Update catalog Data
                        updateInputFields(res);
                        catalogData = res;
                        currentCatalogData = res;
                        initPageHeader();
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.saveSuccess' />", dialog.INFO);
                        
                    },
                    errorHandler: function(errMsg, err){
                        if (errMsg.indexOf("USER_HAS_NO_PERMISSION_ON_ENTITY") != -1) {
                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.permissionError' />", dialog.WARNING);
                            
                        }
                        else {
                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.storeCatalogError' />", dialog.WARNING);
                        }
                        
                        console.debug(errMsg);
                    }
                });
            }
            
            scriptScopeCatSettings.selectSpatialReference = function(){
                var def = new dojo.Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.catalog.selectLocation.title' />", "admin/dialogs/mdek_admin_catalog_spatial_reference_dialog.jsp", 530, 230, true, {
                    resultHandler: def
                });
                
                def.addCallback(function(result){
                    var spatialRefWidget = dijit.byId("adminCatalogSpatialRef");
                    spatialRefWidget.setValue(result.name);
                    spatialRefWidget.location = result;
                });
            }
            
            function isValidCatalog(cat){
            	if(cat.location.name) // can be undefined now
                return (dojo.trim(cat.countryCode).length != 0 &&
                dojo.trim(cat.languageCode).length != 0 &&
                dojo.trim(cat.location.name).length != 0/* &&
                dojo.validate.isInteger(cat.expiryDuration) &&
                dojo.validate.isInRange(cat.expiryDuration, {
                    min: 0,
                    max: 2147483647
                })*/);
            	else
            		return false;
            }
            
        </script>
    </head>
    <body>
        <!-- CONTENT START -->
        <!--<div dojoType="dijit.layout.ContentPane" layoutAlign="client">-->
            <div id="contentSection" class="contentBlockWhite">
                <div id="adminCatalog" class="content">
                    <!-- LEFT HAND SIDE CONTENT START -->
                    <div class="inputContainer field grey noSpaceBelow">
                        <div id="winNavi" style="top:0;">
                            <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=catalog-administration-1#catalog-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                        </div>
                        <span class="outer"><div>
                        <span class="label">
                            <label for="adminCatalogName" onclick="javascript:dialog.showContextHelp(arguments[0], 8001)">
                                <fmt:message key="dialog.admin.catalog.catalogName" />
                            </label>
                        </span>
						<span class="input"><input type="text" id="adminCatalogName" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                        </div></span>
                        <span class="outer"><div>
                        <span class="label">
                            <label for="adminCatalogNamespace" onclick="javascript:dialog.showContextHelp(arguments[0], 8100)">
                                <fmt:message key="dialog.admin.catalog.catalogNamespace" />
                            </label>
                        </span>
                        <span class="input"><input type="text" id="adminCatalogNamespace" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                        </div></span>
						<span class="outer"><div>
						    <span class="label">
                            <label for="adminCatalogPartnerName" onclick="javascript:dialog.showContextHelp(arguments[0], 8002)">
                                <fmt:message key="dialog.admin.catalog.partnerName" />
                            </label>
                        </span>
						<span class="input"><input type="text" id="adminCatalogPartnerName" style="width:100%;" dojoType="dijit.form.ValidationTextBox" />
						</span>
                        </div></span>
						<span class="outer"><div>
						    <span class="label">
                            <label for="adminCatalogProviderName" onclick="javascript:dialog.showContextHelp(arguments[0], 8003)">
                                <fmt:message key="dialog.admin.catalog.providerName" />
                            </label>
                        </span>
						<span class="input"><input type="text" id="adminCatalogProviderName" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
						</div></span>
                        <span class="outer"><div>
						    <span class="label required">
                            <label for="adminCatalogCountry" onclick="javascript:dialog.showContextHelp(arguments[0], 8004)">
                                <fmt:message key="dialog.admin.catalog.state" />*
                            </label>
                        </span>
                        <span class="input">
							<div id="adminCatalogCountry" listId="6200" required="true" style="width:100%;" maxHeight="150"></div>
							<!--<div dojoType="dojo.data.ItemFileWriteStore" jsId="storeCountry" data="countryData"></div>
							<input class="spaceBelow" dojoType="dijit.form.Select" store="storeCountry" required="true" style="width:100%;" maxHeight="150" listId="6200" id="adminCatalogCountry" />-->
						</span>
						</div></span>
                        <span class="outer"><div>
						    <span class="label required">
                            <label for="adminCatalogLanguage" onclick="javascript:dialog.showContextHelp(arguments[0], 8005)">
                                <fmt:message key="dialog.admin.catalog.language" />*
                            </label>
                        </span>
						<span class="input">
							<div id="adminCatalogLanguage" listId="99999999" required="true" style="width:100%;" maxHeight="150" disabled="true"></div>
							<!--<div dojoType="dojo.data.ItemFileWriteStore" jsId="storeLanguage" data="langData"></div>
							<input class="spaceBelow" dojoType="dijit.form.Select" store="storeLanguage" required="true" style="width:100%;" maxHeight="150" disabled="true" listId="99999999" id="adminCatalogLanguage" />-->
						</span>
                        </div></span>
						<span class="outer"><div>
						    <span class="label required">
                            <label for="adminCatalogSpatialRef" onclick="javascript:dialog.showContextHelp(arguments[0], 8006)">
                                <fmt:message key="dialog.admin.catalog.location" />*
                            </label>
                        </span>
						<span class="functionalLink marginRight">
							<img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
							<a href="javascript:void(0);" onClick="javascript:scriptScopeCatSettings.selectSpatialReference();" title="<fmt:message key="dialog.admin.catalog.locationLink" /> [Popup]"><fmt:message key="dialog.admin.catalog.locationLink" /></a>
						</span>
						<span class="input">
							<input type="text" required="true" id="adminCatalogSpatialRef" style="width:100%;" disabled="true" dojoType="dijit.form.ValidationTextBox" />
						</span>
                        </div></span>
                        <span class="outer "><div>
                        <div class="checkboxContainer">
                            <span class="input">
                            	<input type="checkbox" id="adminCatalogWorkflowControl" dojoType="dijit.form.CheckBox" />
                                <label for="adminCatalogWorkflowControl" class="inActive">
                                    <fmt:message key="dialog.admin.catalog.activateWorkflowControl" />
                                </label>
                            </span>
							<span class="input">
								<input type="checkbox" id="adminCatalogExpire" dojoType="dijit.form.CheckBox" />
                                <label class="inActive">
                                    <fmt:message key="dialog.admin.catalog.expireAfter" />
									<input id="adminCatalogExpiryDuration" style="width:33px !important;" min="1" max="2147483647" maxlength="10" dojoType="dijit.form.NumberTextBox" />
									<fmt:message key="dialog.admin.catalog.days" />
                                </label>
                            </span>
                        </div>
                        </div></span>
                        <div class="fill"></div>
                    </div>
                    <!--
                    <div class="inputContainer">
                    <span class="button w644"><a href="#" class="buttonBlue" title="<fmt:message key="dialog.admin.catalog.save" />"><fmt:message key="dialog.admin.catalog.save" /></a><a href="#" class="buttonBlue" title="<fmt:message key="dialog.wizard.create.cancel" />"><fmt:message key="dialog.wizard.create.cancel" /></a></span>
                    </div>
                    -->
                    <div class="inputContainer">
                        <span class="button"><span style="float:right;">
                                <button id="adminCS_save" dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.save" />" onClick="javascript:scriptScopeCatSettings.saveCatalogData();">
                                    <fmt:message key="dialog.admin.catalog.save" />
                                </button>
                            </span><span style="float:right;">
                                <button id="adminCS_reset" dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.reset" />" onClick="javascript:scriptScopeCatSettings.reloadCatalogData();">
                                    <fmt:message key="dialog.admin.catalog.reset" />
                                </button>
                            </span><span id="adminCatalogLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                    </div><!-- LEFT HAND SIDE CONTENT END -->
                </div>
            </div>
        <!--</div>--><!-- CONTENT END -->
    </body>
</html>