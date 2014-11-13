/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/on",
    "dojo/aspect",
    "dijit/form/CheckBox",
    "dijit/form/NumberTextBox",
    "dijit/form/DateTextBox",
    "dijit/form/ValidationTextBox",
    "dijit/form/SimpleTextarea",
    "dijit/form/Select",
    "dijit/form/ComboBox",
    "ingrid/grid/CustomGrid"
], function(declare, lang, on, aspect, CheckBox, NumberTextBox, DateTextBox, ValidationTextBox, SimpleTextarea, Select, ComboBox, CustomGrid) {
    return declare(null, {
        
        dirtyFlag: false,
        
        enableSetDirtyFlag: function() {
            isSettingData = false;
        },
        
        setDirtyFlag: function()
        {
//            if (isSettingData) {
//                // first clear an already set timeout
//                if (_onSetDirtyFlag) {
//                    clearTimeout(_onSetDirtyFlag);
//                }
//                _onSetDirtyFlag = setTimeout(this.enableSetDirtyFlag, 200);
//            } else {
                this.setDirtyFlagNow();
            //}
        },
        
        setDirtyFlagNow: function()
        {
            this.dirtyFlag = true;
        },
        
        resetDirtyFlag: function()
        {
            this.dirtyFlag = false;
        },
        
        
        _connectWidgetWithDirtyFlag: function(widgetId) {
            // We don't need to connect the 'Link' tables. If those tables are changed, the 'master' table (linksTo)
            // will be changed and set the dirty flag 
            var startPos = widgetId.length - "Link".length;
            if (startPos >= 0 && widgetId.lastIndexOf("Link") == startPos) {
            //if (dojo.string.endsWith(widgetId, "Link")) {
                return;
            }
        
            var widget = dijit.byId(widgetId);
            if (widget instanceof NumberTextBox) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof CheckBox) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof DateTextBox) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof ValidationTextBox) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof SimpleTextarea) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof Select) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof ComboBox) {
                on(widget, "Change", lang.hitch(this, this.setDirtyFlag));
            } else if (widget instanceof CustomGrid) {
                aspect.after(widget, "onDataChanged", lang.hitch(this, this.setDirtyFlag), true);
            } else {
                console.debug("Can't connect widget "+widgetId+" with dirty flag. Method not implemented for "+widget);
            }
        },
        
        _connectStoreWithDirtyFlag: function(store) {
            on(store, "Delete", lang.hitch(this, this.setDirtyFlag));
            on(store, "New", lang.hitch(this, this.setDirtyFlag));
            on(store, "Set", lang.hitch(this, this.setDirtyFlag));
        }
        
    })();
});