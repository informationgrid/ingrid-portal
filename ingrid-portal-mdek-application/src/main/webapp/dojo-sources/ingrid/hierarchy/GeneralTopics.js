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
        "dojo/topic",
        "dojo/string",
        "ingrid/dialog",
        "ingrid/message"
    ], function(declare, topic, string, dialog, message) {
        return declare(null, {

            constructor: function() {
                topic.subscribe("/getObjectPathRequest", this.handleGetObjectPathRequest);
                topic.subscribe("/getAddressPathRequest", this.handleGetAddressPathRequest);
            },

            handleGetObjectPathRequest: function(msg) {
                // var loadErrback = function() {
                //     msg.resultHandler.reject();
                // };
                var loadCallback = function() {
                    console.debug("udkDataProxy calling ObjectService.getPathToObject(" + msg.id + ")");
                    ObjectService.getPathToObject(msg.id, {
                        callback: function(res) {
                            msg.resultHandler.resolve(res);
                        },
                        errorHandler: function(errorMessage) {
                            console.debug("Error in js/js: Error while getting path to node: " + errorMessage);
                            dialog.show(message.get("general.warning"), string.substitute(message.get("general.warning.unknownId"), [msg.id]), dialog.WARNING);
                            msg.resultHandler.reject();
                        }
                    });
                };

                loadCallback();
                //          if (msg.ignoreDirtyFlag) {
                //              // If the dirty flag is ignored, the request can be started
                //              loadCallback(); 
                //          } else {
                //              // Otherwise check for unsaved changes and start the request afterwards
                //              // TODO: call in other functions instead!!! 
                //              var deferred = this.checkForUnsavedChanges();
                //              deferred.then(loadCallback, loadErrback);
                //          }
            },

            handleGetAddressPathRequest: function(msg) {
                // var loadErrback = function() {
                //     msg.resultHandler.reject();
                // };
                var loadCallback = function() {
                    console.debug("udkDataProxy calling AddressService.getPathToAddress(" + msg.id + ")");
                    AddressService.getPathToAddress(msg.id, {
                        callback: function(res) {
                            msg.resultHandler.resolve(res);
                        },
                        errorHandler: function(message) {
                            console.debug("Error in js/js: Error while getting path to address: " + message);
                            msg.resultHandler.reject();
                        }
                    });
                };

                loadCallback();
                //          if (msg.ignoreDirtyFlag) {
                //              // If the dirty flag is ignored, the request can be started
                //              loadCallback(); 
                //          } else {
                //              // Otherwise check for unsaved changes and start the request afterwards
                //              var deferred = this.checkForUnsavedChanges();
                //              deferred.then(loadCallback, loadErrback);
            //          }
            }

        })();
    }
);