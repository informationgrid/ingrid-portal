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
define(["dojo/_base/declare", "dojo/_base/array", "dojo/_base/lang", "dojo/Deferred", "dojo/topic", "dojo/on", "dojo/aspect", "dojo/dom-class",
        "dijit/registry", "dijit/Menu", "dijit/MenuItem", "dijit/MenuSeparator",
        "ingrid/MenuActions", "ingrid/IgeActions",
        "ingrid/utils/QA", "ingrid/utils/Security", "ingrid/utils/Tree",
        "ingrid/message"
    ],
    function(declare, array, lang, Deferred, topic, on, aspect, domClass, registry, Menu, MenuItem, MenuSeparator,
        menuEventHandler, IgeActions, UtilQA, UtilSecurity, UtilTree, message) {

        return declare(null, {

            /******************** *************************************
             * HANDLER
             *********************************************************/

            clickHandler: function(oEvent) {
                // do not react on expand/children button click
                if (oEvent.target) {
                    if (oEvent.target.classList.contains("dijitTreeExpando")) return true;
                }
                    
                var item = this.getData()[this.selectedRows[0]];
                var doLoad = function() {
                    var deferred = new Deferred();
                    console.debug("Publishing event: /loadRequest(" + item.id + ", " + item.nodeAppType + ")");

                    deferred.then(function() {
                        console.debug("resize border container");
                        registry.byId("dataFormContainer").resize();

                    }, function(err) {
                        displayErrorMessage(err);
                    });

                    topic.publish("/loadRequest", {
                        id: item.id ? item.id : item.uuid,
                        appType: item.nodeAppType,
                        node: item,
                        resultHandler: deferred
                    });

                    return deferred.promise;
                };

                // check for multiple selection mode (with CTRL-key)
                if (oEvent.ctrlKey === true || oEvent.shiftKey === true) {

                } else {
                    if (IgeActions.hasUnsavedChanged()) {
                        // select previous selected node to 
                        //UtilTree.selectNode("dataTree", this.lastFocusedNode.item.id);
                        IgeActions.showUnsavedChangesDialog().then(function() {
                            // continue to the clicked object
                            //var widget = self.lastFocusedNode;
                            //UtilTree.selectNode("dataTree", widget.item.id);
                            //UtilTree.selectNode("dataTree", item.id);
                            doLoad();
                        }, function() {
                            // cancel and stay where we are
                        });
                    } else {
                        doLoad();
                    }
                    
                }
            }

        })();

    });
