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