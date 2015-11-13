/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
    "dojo/topic",
    "dojo/on",
    "dijit/Menu",
    "dijit/MenuItem",
    "dijit/popup",
    "ingrid/menu"
], function(declare, lang, topic, on, Menu, MenuItem, popup, menu) {
    return declare(null, {
        // the stack that holds our history of visited objects/addresses
        stack: [],
        
        // maximum of nodes in stack
        maxSize: 20,
        
        // a pointer to show were we are in the history stack
        pointer: -1,
        
        // when loading a node by back-Button, we don't want to add it to the stack!
        ignoreNextPush: false,
        
        // the popup showing the last/next nodes
        popupMenu: null,
        
        constructor: function() {
            topic.subscribe("/loadRequest", lang.hitch(this, this.pushToStack));
        },
        
        pushToStack: function(msg) {
            if (this.ignoreNextPush) {
                this.ignoreNextPush = false;
                return;
            }
            
            // if the last node was loaded again -> ignore
            if (this.stack.length !== 0 && msg.id === this.stack[this.stack.length-1].id) return;
            
            // remove everything after the pointer to discard nodes from history, when we came back
            this.stack.splice( this.pointer+1 );
            
            var node = {
                id: msg.id,
                type: msg.appType,
                title: msg.node.title
            };
            this.stack.push( node );
            
            // if stack gets too big, then remove first item
            if (this.stack.length > this.maxSize) {
                this.stack.shift();
            } else {
                this.pointer++;
            }
        },
        
        goBack: function(evt) {
            // in case of a long press this shall not be executed
            if (evt.ignore) return;
            
            // close the popup if it's still open
            popup.close(this.popupMenu);
            
            var node = this.stack[ this.pointer-1 ];
            this.ignoreNextPush = true;
            menu.handleSelectNodeInTree(node.id, node.type);
            if (this.pointer > 0) this.pointer--;
        },
        
        goNext: function(evt) {
            // in case of a long press this shall not be executed
            if (evt.ignore) return;
            
            // close the popup if it's still open
            popup.close(this.popupMenu);
            
            var node = this.stack[ this.pointer+1 ];
            this.ignoreNextPush = true;
            menu.handleSelectNodeInTree(node.id, node.type);
            if (this.hasNext()) this.pointer++;
        },
        
        hasNext: function() {
            return this.pointer < (this.stack.length-1);
        },
        
        hasPrevious: function() {
            return this.pointer > 0;
        },
        
        showNextEntries: function(parent) {
            this._showEntries(parent, true);
        },
        
        showPreviousEntries: function(parent) {
            this._showEntries(parent, false);
        },
        
        _showEntries: function(parent, forNextEntries) {
            this.popupMenu = new Menu({});
            var self = this;
            var start = forNextEntries ? (this.pointer+1) : 1;
            var end = forNextEntries ? (this.stack.length-1) : this.pointer;
            for (var i=start; i<=end; i++) {
                var item = forNextEntries ? this.stack[ i ] : this.stack[ end-i ];
                this.popupMenu.addChild(new MenuItem({
                    label: item.title,
                    item: item,
                    onClick: function() {
                        menu.handleSelectNodeInTree(this.item.id, this.item.type);
                        self.pointer = self.stack.indexOf( this.item );
                    }
                }));
            }
            
            // when the parent looses focus we have to close the popup
            var parentBlur = on.once( parent, "blur", function() {
                popup.close(self.popupMenu);
            } );
            
            popup.open({
                popup: self.popupMenu,
                parent: parent,
                around: parent.domNode,
                maxHeight: 200,
                onExecute: function(){
                    self.ignoreNextPush = true;
                    parentBlur.remove();
                    popup.close(self.popupMenu);
                },
                onClose: function() {
                    self.popupMenu.destroy();
                    self.popupMenu = null;
                }
            });
            
        }
        
    })();
});