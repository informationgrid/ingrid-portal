<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
        
        var pageCatSettings = _container_;
            
        require([
            "dojo/_base/array",
            "dojo/_base/lang",
            "dojo/Deferred",
            "dijit/registry",
            "dijit/form/Select",
            "dijit/form/CheckBox",
            "dijit/form/NumberTextBox",
            "dojo/on",
            "ingrid/layoutCreator",
            "ingrid/dialog",
            "ingrid/init",
            "ingrid/utils/Syslist",
            "ingrid/utils/List",
            "ingrid/utils/Catalog"
        ], function(array, lang, Deferred, registry, Select, CheckBox, NumberTextBox, on, layoutCreator, dialog, init, UtilSyslist, UtilList, UtilCatalog) {
            
            console.log("catalog settings");
        
            // extend the typical widget to accept listIds for syslists
            // deprecated! used a different way now ... or?
            //dojo.extend(dijit._Widget, { listId:"0" });
            
            
            // Storage for the current catalog. We need to get the uuid from somewhere
            pageCatSettings.currentCatalogData = null;
            
            on(_container_, "Load", function() {
                try {
                    // create select boxes for syslists
                    var storeProps = { data: { identifier: '1', label: '0' } };
                    layoutCreator.createSelectBox("adminCatalogCountry", null, storeProps, function() {
                        return UtilSyslist.getSyslistEntry(6200);
                    });
                    layoutCreator.createSelectBox("adminCatalogLanguage", null, storeProps,  function() {
                        return UtilSyslist.getSyslistEntry(99999999);
                    });

                    console.debug("update fields");
                    if (UtilCatalog.catalogData !== null) {
                        updateInputFields(UtilCatalog.catalogData);
                        pageCatSettings.currentCatalogData = UtilCatalog.catalogData;
                    }
                    else {
                        reloadCatalogData();
                    }
                        
                    var checkbox = registry.byId("adminCatalogExpire");
                    var inputField = registry.byId("adminCatalogExpiryDuration");
                    
                    on(checkbox, "click", function(){ //!!!connectOnce
                        checkbox.checked ? inputField.set('disabled', false) : inputField.set('disabled', true);
                    });
                    
                } catch (err) {
                    console.error("error in init", err);
                }
                
            });
            
            function updateInputFields(catalogData) {
                registry.byId("adminCatalogName").setValue(catalogData.catalogName);
                registry.byId("adminCatalogNamespace").setValue(catalogData.catalogNamespace);
                registry.byId("adminCatalogPartnerName").setValue(catalogData.partnerName);
                registry.byId("adminCatalogProviderName").setValue(catalogData.providerName);
                registry.byId("adminCatalogCountry").setValue(catalogData.countryCode);
                registry.byId("adminCatalogLanguage").setValue(catalogData.languageCode);
                registry.byId("adminCatalogSpatialRef").setValue(catalogData.location.name);
                registry.byId("adminCatalogSpatialRef").location = catalogData.location;
                registry.byId("adminCatalogAtomDownload").setValue(catalogData.atomUrl);
                if (catalogData.workflowControl == "Y") {
                    registry.byId("adminCatalogWorkflowControl").setValue(true);
                }
                else {
                    registry.byId("adminCatalogWorkflowControl").setValue(false);
                }
                if (catalogData.expiryDuration !== null && catalogData.expiryDuration > 0) {
                    registry.byId("adminCatalogExpire").setValue(true);
                    registry.byId("adminCatalogExpiryDuration").set('disabled', false);
                    registry.byId("adminCatalogExpiryDuration").setValue(catalogData.expiryDuration);
                }
                else {
                    registry.byId("adminCatalogExpire").setValue(false);
                    registry.byId("adminCatalogExpiryDuration").setValue("0");
                    registry.byId("adminCatalogExpiryDuration").set('disabled', true);
                }
            }
            
            function reloadCatalogData() {
                CatalogService.getCatalogData({
                    callback: function(res){
                        // Update catalog Data
                        updateInputFields(res);
                        pageCatSettings.currentCatalogData = res;
                        UtilCatalog.catalogData = res;
                    },
                    errorHandler: function(mes){
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.loadCatalogError' />", dialog.WARNING);
                        console.error(mes);
                    }
                });
            }
            
            function saveCatalogData() {
                var newCatalogData = {};
                newCatalogData.uuid = pageCatSettings.currentCatalogData.uuid;
                newCatalogData.catalogName = registry.byId("adminCatalogName").getValue();
                newCatalogData.catalogNamespace = registry.byId("adminCatalogNamespace").getValue();
                newCatalogData.partnerName = registry.byId("adminCatalogPartnerName").getValue();
                newCatalogData.providerName = registry.byId("adminCatalogProviderName").getValue();
                newCatalogData.countryCode = registry.byId("adminCatalogCountry").getValue();
                newCatalogData.languageCode = registry.byId("adminCatalogLanguage").getValue();
                newCatalogData.location = registry.byId("adminCatalogSpatialRef").location;
                newCatalogData.atomUrl = registry.byId("adminCatalogAtomDownload").getValue();
                // add a slash at the end
                if (dojo.lastIndexOf(newCatalogData.atomUrl, "/") != newCatalogData.atomUrl.length-1) newCatalogData.atomUrl += "/";
                newCatalogData.expiryDuration = (registry.byId("adminCatalogExpire").checked ? registry.byId("adminCatalogExpiryDuration").getValue() : "0");
                newCatalogData.workflowControl = registry.byId("adminCatalogWorkflowControl").checked ? "Y" : "N";
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
                        UtilCatalog.catalogData = res;
                        pageCatSettings.currentCatalogData = res;
                        // init.initPageHeader();
                        init.initCatalogData();
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.saveSuccess' />", dialog.INFO);
                        
                    },
                    errorHandler: function(errMsg) {
                        if (errMsg.indexOf("USER_HAS_NO_PERMISSION_ON_ENTITY") != -1) {
                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.permissionError' />", dialog.WARNING);
                            
                        }
                        else {
                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.storeCatalogError' />", dialog.WARNING);
                        }
                        
                        console.error(errMsg);
                    }
                });
            }
            
            function selectSpatialReference() {
                var def = new Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.catalog.selectLocation.title' />", "admin/dialogs/mdek_admin_catalog_spatial_reference_dialog.jsp", 530, 230, true, {
                    resultHandler: def
                });
                
                def.then(function(result){
                    var spatialRefWidget = registry.byId("adminCatalogSpatialRef");
                    UtilList.addSNSLocationLabels([result]);
                    spatialRefWidget.setValue(result.label);
                    spatialRefWidget.location = result;
                });
            }
            
            function isValidCatalog(cat) {
                if(cat.location.name) // can be undefined now
                    return (lang.trim(cat.countryCode).length !== 0 &&
                    lang.trim(cat.languageCode).length !== 0 &&
                    lang.trim(cat.location.name).length !== 0 &&
                    lang.trim(cat.catalogNamespace).length !== 0/* &&
                    dojo.validate.isInteger(cat.expiryDuration) &&
                    dojo.validate.isInRange(cat.expiryDuration, {
                        min: 0,
                        max: 2147483647
                    })*/);
                else
                    return false;
            }

            /*
             *  PUBLIC METHODS
             */
            pageCatSettings = {
                selectSpatialReference: selectSpatialReference,
                saveCatalogData: saveCatalogData,
                reloadCatalogData: reloadCatalogData
            };
        
        });
            
        </script>
    </head>
    <body>
        <!-- CONTENT START -->
        <!--<div data-dojo-type="dijit/layout/ContentPane" layoutAlign="client">-->
            <div id="contentSection" class="contentBlockWhite">
                <div id="adminCatalog" class="content">
                    <!-- LEFT HAND SIDE CONTENT START -->
                    <div class="inputContainer field grey noSpaceBelow">
                        <div id="winNavi" style="top:0;">
                            <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=catalog-administration-1#catalog-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
                        </div>
                        <span class="outer"><div>
                        <span class="label">
                            <label for="adminCatalogName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8001)">
                                <fmt:message key="dialog.admin.catalog.catalogName" />
                            </label>
                        </span>
                        <span class="input"><input type="text" id="adminCatalogName" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" /></span>
                        </div></span>
                        <span class="outer"><div>
                        <span class="label">
                            <label for="adminCatalogNamespace" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8100)">
                                <fmt:message key="dialog.admin.catalog.catalogNamespace" />*
                            </label>
                        </span>
                        <span class="input"><input type="text" id="adminCatalogNamespace" style="width:100%;" required="true" tooltipPosition="below" data-dojo-type="dijit/form/ValidationTextBox" /></span>
                        </div></span>
                        <span class="outer"><div>
                            <span class="label">
                            <label for="adminCatalogPartnerName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8002)">
                                <fmt:message key="dialog.admin.catalog.partnerName" />
                            </label>
                        </span>
                        <span class="input"><input type="text" id="adminCatalogPartnerName" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" />
                        </span>
                        </div></span>
                        <span class="outer"><div>
                            <span class="label">
                            <label for="adminCatalogProviderName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8003)">
                                <fmt:message key="dialog.admin.catalog.providerName" />
                            </label>
                        </span>
                        <span class="input"><input type="text" id="adminCatalogProviderName" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" /></span>
                        </div></span>
                        <span class="outer"><div>
                            <span class="label required">
                            <label for="adminCatalogCountry" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8004)">
                                <fmt:message key="dialog.admin.catalog.state" />*
                            </label>
                        </span>
                        <span class="input">
                            <div id="adminCatalogCountry" listId="6200" required="true" style="width:100%;" maxHeight="150"></div>
                            <!--<div data-dojo-type="dojo.data.ItemFileWriteStore" jsId="storeCountry" data="countryData"></div>
                            <input class="spaceBelow" data-dojo-type="dijit/form/Select" store="storeCountry" required="true" style="width:100%;" maxHeight="150" listId="6200" id="adminCatalogCountry" />-->
                        </span>
                        </div></span>
                        <span class="outer"><div>
                            <span class="label required">
                            <label for="adminCatalogLanguage" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8005)">
                                <fmt:message key="dialog.admin.catalog.language" />*
                            </label>
                        </span>
                        <span class="input">
                            <div id="adminCatalogLanguage" listId="99999999" required="true" style="width:100%;" maxHeight="150" disabled="true"></div>
                            <!--<div data-dojo-type="dojo.data.ItemFileWriteStore" jsId="storeLanguage" data="langData"></div>
                            <input class="spaceBelow" data-dojo-type="dijit/form/Select" store="storeLanguage" required="true" style="width:100%;" maxHeight="150" disabled="true" listId="99999999" id="adminCatalogLanguage" />-->
                        </span>
                        </div></span>
                        <span class="outer"><div>
                            <span class="label required left">
                            <label for="adminCatalogSpatialRef" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8006)">
                                <fmt:message key="dialog.admin.catalog.location" />*
                            </label>
                        </span>
                        <span class="functionalLink marginRight">
                            <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
                            <a href="javascript:void(0);" onclick="pageCatSettings.selectSpatialReference();" title="<fmt:message key="dialog.admin.catalog.locationLink" /> [Popup]"><fmt:message key="dialog.admin.catalog.locationLink" /></a>
                        </span>
                        <span class="input">
                            <input type="text" required="true" id="adminCatalogSpatialRef" style="width:100%;" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" />
                        </span>
                        </div></span>
                        <span class="outer">
                            <div>
                                <span class="label left">
                                    <label for="adminCatalogSpatialRef" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8091)">
                                        <fmt:message key="dialog.admin.catalog.atomDownloadService" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" required="false" id="adminCatalogAtomDownload" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" />
                                </span>
                            </div>
                        </span>
                        <span class="outer "><div>
                        <div class="checkboxContainer">
                            <span class="input">
                                <input type="checkbox" id="adminCatalogWorkflowControl" data-dojo-type="dijit/form/CheckBox" />
                                <label for="adminCatalogWorkflowControl" class="inActive">
                                    <fmt:message key="dialog.admin.catalog.activateWorkflowControl" />
                                </label>
                            </span>
                            <span class="input">
                                <input type="checkbox" id="adminCatalogExpire" data-dojo-type="dijit/form/CheckBox" />
                                <label class="inActive">
                                    <fmt:message key="dialog.admin.catalog.expireAfter" />
                                    <input id="adminCatalogExpiryDuration" style="width:33px !important;" min="1" max="2147483647" maxlength="10" data-dojo-type="dijit/form/NumberTextBox" />
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
                                <button id="adminCS_save" data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.save" />" onclick="pageCatSettings.saveCatalogData()" >
                                    <fmt:message key="dialog.admin.catalog.save" />
                                </button>
                            </span><span style="float:right;">
                                <button id="adminCS_reset" data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.reset" />" onclick="pageCatSettings.reloadCatalogData()" >
                                    <fmt:message key="dialog.admin.catalog.reset" />
                                </button>
                            </span><span id="adminCatalogLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                    </div><!-- LEFT HAND SIDE CONTENT END -->
                </div>
            </div>
        <!--</div>--><!-- CONTENT END -->
    </body>
</html>