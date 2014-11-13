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
define(["dojo/_base/declare", "dijit/tree/ForestStoreModel"], function(declare, ForestStoreModel) {
    return declare(ForestStoreModel, {
        newItemsAvailable: false,
        
        manualTreeNotification: false,
        
        mayHaveChildren: function(/*dojo.data.Item*/item){
            if (item.isFolder == "true") {
                return true;
            }
        },
        
        onNewItem: function(/* dojo.data.Item */item, /* Object */ parentInfo){
            // do not handle the notification of the tree here
            // it's better to inform the tree when all children are added to the store
            // so that tree only is informed once
            if (this.manualTreeNotification) {
                this.newItemsAvailable = true;
                return;
            } else {
                this.inherited(arguments);
            }
        },
        
        notfiyOnNewChildren: function(parentInfo, def){
            var _this = this;
            this._requeryTop();
            _this.getChildren(parentInfo.item, lang.hitch(this, function(children){
                _this.onChildrenChange(parentInfo.item, children);
                def.resolve();
            }));
            this.newItemsAvailable = false;
        }
    })();
});

