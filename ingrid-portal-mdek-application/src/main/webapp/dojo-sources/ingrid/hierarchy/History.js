/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
    "dojo/aspect",
    "dojo/topic",
    "dojo/on",
    "dijit/Menu",
    "dijit/MenuItem",
    "dijit/popup",
    "ingrid/menu"
], function(declare, lang, aspect, topic, on, Menu, MenuItem, popup, menu) {
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
            if (this.pointer > 0) this.pointer--;
            this._callerFunction( node );
        },
        
        goNext: function(evt) {
            // in case of a long press this shall not be executed
            if (evt.ignore) return;
            
            // close the popup if it's still open
            popup.close(this.popupMenu);
            
            var node = this.stack[ this.pointer+1 ];
            this.ignoreNextPush = true;
            if (this.hasNext()) this.pointer++;
            this._callerFunction( node );
        },
        
        hasNext: function() {
            return this.pointer < (this.stack.length-1);
        },
        
        hasPrevious: function() {
            return this.pointer > 0;
        },
        
        addPreviousButton: function(button) {
            this._addHistoryPopup( button, false );
        },
        
        addNextButton: function(button) {
            this._addHistoryPopup( button, true );
        },
        
        _addHistoryPopup: function(button, forNextItems) {
            var longPress = false;
            var self = this; 
            
            // prepare action for long press or normal click
            aspect.before( button, "onClick", function(evt) {
                if (longPress === null) {
                    evt.ignore = true;
                } else {
                    longPress = false;
                }
            } );
            // register a long press
            var waitForLongPress = null;
            on( button, "mousedown", function() {
                if (this.disabled) return;
                
                // remove an already set timeout, otherwise multiple clicks could be interpreted as a long press
                if (waitForLongPress) clearTimeout( waitForLongPress );
                longPress = true;
                
                // wait a bit, to registrate if the user did a long press 
                waitForLongPress = setTimeout(function() {
                    if (longPress) {
                        longPress = null;
                        if (forNextItems) {
                            self._showEntries(button, true);
                        } else {
                            self._showEntries(button, false);
                        }
                    }
                }, 500);
            } );
        },
        
        _callerFunction: function(item) {
            menu.handleSelectNodeInTree(item.id, item.type);
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
                        self.pointer = self.stack.indexOf( this.item );
                        self._callerFunction( this.item );
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
