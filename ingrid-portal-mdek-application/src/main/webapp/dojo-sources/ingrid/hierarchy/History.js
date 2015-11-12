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
    "ingrid/menu"
], function(declare, lang, topic, menu) {
    return declare(null, {
        // the stack that holds our history of visited objects/addresses
        stack: [],
        
        // a pointer to show were we are in the history stack
        pointer: -1,
        
        // when loading a node by back-Button, we don't want to add it to the stack!
        ignoreNextPush: false,
        
        constructor: function() {
            topic.subscribe("/loadRequest", lang.hitch(this, this.pushToStack));
            //topic.subscribe("/previousNode", this.goBack);
            //topic.subscribe("/nextNode", this.goNext);
        },
        
        pushToStack: function(msg) {
            if (this.ignoreNextPush) {
                this.ignoreNextPush = false;
                return;
            }
            
            // remove everything after the pointer to discard nodes from history, when we came back
            this.stack.splice( this.pointer+1 );
            
            var node = {
                id: msg.id,
                type: msg.appType,
                title: msg.node.title
            };
            this.stack.push( node );
            this.pointer++;
        },
        
        popFromStack: function() {
            this.stack.pop();
        },
        
        goBack: function() {
            var node = this.stack[ this.pointer-1 ];
            this.ignoreNextPush = true;
            menu.handleSelectNodeInTree(node.id, node.type);
            if (this.pointer > 0) this.pointer--;
        },
        
        goNext: function() {
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
        }
        
    })();
});