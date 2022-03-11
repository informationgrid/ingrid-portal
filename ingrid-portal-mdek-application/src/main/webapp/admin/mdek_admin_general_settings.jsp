<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <script type="text/javascript">
		
        var pageGeneralSettings = _container_;
        
    	require([
            "dojo/string",
            "dijit/registry",
            "ingrid/utils/Catalog",
            "ingrid/dialog",
            "dijit/form/CheckBox",
            "dijit/form/NumberTextBox"
        ], function(string, registry, UtilCatalog, dialog) {

            var def = UtilCatalog.getGenericValuesDef([UtilCatalog.AUTOSAVE_INTERVAL, UtilCatalog.SESSION_REFRESH_INTERVAL]);
            
            def.then(function(valueMap){
                pageGeneralSettings.setGeneralValues(valueMap);
            }, function(msg){
                console.error("error: " + msg);
                dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.generalError' />", [msg]), dialog.WARNING, null, 350, 350);
            });
            
            pageGeneralSettings.setGeneralValues = function(valueMap){
                pageGeneralSettings.setAutosaveValues(valueMap);
                pageGeneralSettings.setSessionRefreshValues(valueMap);
            }
            
            pageGeneralSettings.setAutosaveValues = function(valueMap){
                if (valueMap[UtilCatalog.AUTOSAVE_INTERVAL] && valueMap[UtilCatalog.AUTOSAVE_INTERVAL] > 0) {
                    var autosaveEnabled = true;
                    var autosaveInterval = valueMap[UtilCatalog.AUTOSAVE_INTERVAL];
                    
                    registry.byId("autosaveCheckbox").set("value", autosaveEnabled);
                    registry.byId("autosaveInterval").set("value", autosaveInterval);
                    
                }
                else {
                    registry.byId("autosaveCheckbox").set("value", false);
                    registry.byId("autosaveInterval").set("value", 10);
                }
            }
            
            pageGeneralSettings.setSessionRefreshValues = function(valueMap){
                if (valueMap[UtilCatalog.SESSION_REFRESH_INTERVAL] && valueMap[UtilCatalog.SESSION_REFRESH_INTERVAL] > 0) {
                    var sessionRefreshEnabled = true;
                    var sessionRefreshInterval = valueMap[UtilCatalog.SESSION_REFRESH_INTERVAL];
                    
                    registry.byId("sessionRefreshCheckbox").set("value", sessionRefreshEnabled);
                    registry.byId("sessionRefreshInterval").set("value", sessionRefreshInterval);
                    
                }
                else {
                    registry.byId("sessionRefreshCheckbox").set("value", false);
                    registry.byId("sessionRefreshInterval").set("value", 10);
                }
            }
            
            pageGeneralSettings.saveGeneralValues = function(){
                var valueMap = {};
                pageGeneralSettings.addAutosaveValuesToMap(valueMap);
                pageGeneralSettings.addSessionRefreshValuesToMap(valueMap);
                
                UtilCatalog.storeGenericValuesDef(valueMap)
                .then(function(){
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.generalSettingsUpdated' />", dialog.INFO);
                }, function(msg){
                    console.error("error: " + msg);
                    dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.generalError' />", [msg]), dialog.WARNING, null, 350, 350);
                });
            }
            
            pageGeneralSettings.addAutosaveValuesToMap = function(valueMap){
                if (registry.byId("autosaveCheckbox").checked) {
                    valueMap[UtilCatalog.AUTOSAVE_INTERVAL] = registry.byId("autosaveInterval").get("value");
                    
                }
                else {
                    valueMap[UtilCatalog.AUTOSAVE_INTERVAL] = 0;
                }
            }
            
            pageGeneralSettings.addSessionRefreshValuesToMap = function(valueMap){
                if (registry.byId("sessionRefreshCheckbox").checked) {
                    valueMap[UtilCatalog.SESSION_REFRESH_INTERVAL] = registry.byId("sessionRefreshInterval").get("value");
                    
                }
                else {
                    valueMap[UtilCatalog.SESSION_REFRESH_INTERVAL] = 0;
                }
            }
            
            pageGeneralSettings.showLoadingZone = function(){
                dom.byId("generalSettingsLoadingZone").style.visible = true;
            }
            
            pageGeneralSettings.hideLoadingZone = function(){
                dom.byId("generalSettingsLoadingZone").style.visible = false;
            }
    	    
    	});
        
    </script>
</head>
<body>
    <div id="contentSection" class="contentBlockWhite top">
        <div id="winNavi">
            <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=catalog-administration-3#catalog-administration-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
        </div>
        <div class="content">
            <!-- LEFT HAND SIDE CONTENT START -->
            <div class="spacer">
            </div>
            <div class="spacer">
            </div>
            <div class="inputContainer field grey">
                <span class="label">
                    <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8059)">
                        <fmt:message key="dialog.admin.catalog.general.settings" />
                    </label>
                </span>
                <div class="checkboxContainer">
                    <span class="input"><input type="checkbox" id="autosaveCheckbox" data-dojo-type="dijit/form/CheckBox" />
                        <label class="inActive">
                            <fmt:message key="dialog.admin.catalog.general.autosave" />
							<input id="autosaveInterval" class="w033" style="width:33px;" min="1" max="60" maxlength="10" data-dojo-type="dijit/form/NumberTextBox" /><fmt:message key="dialog.admin.catalog.general.minutes" />
                        </label>
                    </span><span class="input"><input type="checkbox" id="sessionRefreshCheckbox" data-dojo-type="dijit/form/CheckBox" />
                        <label class="inActive">
                            <fmt:message key="dialog.admin.catalog.general.sessionRefresh" />
							<input id="sessionRefreshInterval" class="w033" style="width:33px;" min="1" max="60" maxlength="10" data-dojo-type="dijit/form/NumberTextBox" /><fmt:message key="dialog.admin.catalog.general.minutes" />
                        </label>
                    </span>
                </div>
                <div class="spacerField">
                </div>
            </div>
            <div class="inputContainer">
                <span class="button"><span style="float:right;">
                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.general.save" />" onclick="pageGeneralSettings.saveGeneralValues();">
                            <fmt:message key="dialog.admin.catalog.general.save" />
                        </button>
                    </span><span id="generalSettingsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
            </div><!-- LEFT HAND SIDE CONTENT END -->
        </div>
    </div><!-- CONTENT END -->
</body>
</html>
