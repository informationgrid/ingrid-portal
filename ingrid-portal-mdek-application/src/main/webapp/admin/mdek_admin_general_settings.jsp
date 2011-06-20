<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
        	dojo.require("dijit.form.CheckBox");
			dojo.require("dijit.form.NumberTextBox");
			
            var scriptScopeGeneral = _container_;
            loadAndSetGeneralValues();
            
            function loadAndSetGeneralValues(){
                var def = UtilCatalog.getGenericValuesDef([UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL, UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL]);
                
                def.addCallback(function(valueMap){
                    setGeneralValues(valueMap);
                });
                
                def.addErrback(function(msg){
                    console.debug("error: " + msg);
                    dialog.show("<fmt:message key='general.error' />", dojo.string.substituteParams("<fmt:message key='dialog.generalError' />", msg), dialog.WARNING, null, 350, 350);
                });
            }
            
            function setGeneralValues(valueMap){
                setAutosaveValues(valueMap);
                setSessionRefreshValues(valueMap);
            }
            
            function setAutosaveValues(valueMap){
                if (valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] && valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] > 0) {
                    var autosaveEnabled = true;
                    var autosaveInterval = valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL];
                    
                    dijit.byId("autosaveCheckbox").setValue(autosaveEnabled);
                    dijit.byId("autosaveInterval").setValue(autosaveInterval);
                    
                }
                else {
                    dijit.byId("autosaveCheckbox").setValue(false);
                    dijit.byId("autosaveInterval").setValue(10);
                }
            }
            
            function setSessionRefreshValues(valueMap){
                if (valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] && valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] > 0) {
                    var sessionRefreshEnabled = true;
                    var sessionRefreshInterval = valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL];
                    
                    dijit.byId("sessionRefreshCheckbox").setValue(sessionRefreshEnabled);
                    dijit.byId("sessionRefreshInterval").setValue(sessionRefreshInterval);
                    
                }
                else {
                    dijit.byId("sessionRefreshCheckbox").setValue(false);
                    dijit.byId("sessionRefreshInterval").setValue(10);
                }
            }
            
            scriptScopeGeneral.saveGeneralValues = function(){
                var valueMap = {};
                addAutosaveValuesToMap(valueMap);
                addSessionRefreshValuesToMap(valueMap);
                
                var def = UtilCatalog.storeGenericValuesDef(valueMap);
                def.addCallback(function(){
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.generalSettingsUpdated' />", dialog.INFO);
                });
                def.addErrback(function(msg){
                    console.debug("error: " + msg);
                    dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.generalError' />", [msg]), dialog.WARNING, null, 350, 350);
                });
            }
            
            function addAutosaveValuesToMap(valueMap){
                if (dijit.byId("autosaveCheckbox").checked) {
                    valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] = dijit.byId("autosaveInterval").getValue();
                    
                }
                else {
                    valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL] = 0;
                }
            }
            
            function addSessionRefreshValuesToMap(valueMap){
                if (dijit.byId("sessionRefreshCheckbox").checked) {
                    valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] = dijit.byId("sessionRefreshInterval").getValue();
                    
                }
                else {
                    valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL] = 0;
                }
            }
            
            function showLoadingZone(){
                dojo.byId("generalSettingsLoadingZone").style.visible = true;
            }
            
            function hideLoadingZone(){
                dojo.byId("generalSettingsLoadingZone").style.visible = false;
            }
            
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
                        <label onclick="javascript:dialog.showContextHelp(arguments[0], 8059)">
                            <fmt:message key="dialog.admin.catalog.general.settings" />
                        </label>
                    </span>
                    <div class="checkboxContainer">
                        <span class="input"><input type="checkbox" id="autosaveCheckbox" dojoType="dijit.form.CheckBox" />
                            <label class="inActive">
                                <fmt:message key="dialog.admin.catalog.general.autosave" />
								<input id="autosaveInterval" class="w033" style="width:33px;" min="1" max="60" maxlength="10" dojoType="dijit.form.NumberTextBox" /><fmt:message key="dialog.admin.catalog.general.minutes" />
                            </label>
                        </span><span class="input"><input type="checkbox" id="sessionRefreshCheckbox" dojoType="dijit.form.CheckBox" />
                            <label class="inActive">
                                <fmt:message key="dialog.admin.catalog.general.sessionRefresh" />
								<input id="sessionRefreshInterval" class="w033" style="width:33px;" min="1" max="60" maxlength="10" dojoType="dijit.form.NumberTextBox" /><fmt:message key="dialog.admin.catalog.general.minutes" />
                            </label>
                        </span>
                    </div>
                    <div class="spacerField">
                    </div>
                </div>
                <div class="inputContainer">
                    <span class="button"><span style="float:right;">
                            <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.general.save" />" onClick="javascript:scriptScopeGeneral.saveGeneralValues();">
                                <fmt:message key="dialog.admin.catalog.general.save" />
                            </button>
                        </span><span id="generalSettingsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                </div><!-- LEFT HAND SIDE CONTENT END -->
            </div>
        </div><!-- CONTENT END -->
    </body>
</html>