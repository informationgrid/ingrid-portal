/*
 * This proxy is used to read from and write to the different gui elements.
 * Additional checks are performed to ensure that no data is lost in the process (e.g.
 * asking the user if unsaved changes should really be discarded)
 *
 * The proxy is called indirectly via the following topics (topic.publish(topic, message)):
 *   topic = '/loadRequest' - argument: {id: nodeUuid, appType: appType, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node
 *     appType  - 'A' for Address, 'O' for Object
 *     resultHandler - (optional) A Deferred which is called when the request has been processed
 *
 *   topic = '/createObjectRequest' - argument: {id: parentUuid, resultHandler: deferred}
 *     parentUuid - The Uuid of the objects parent
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/createAddressRequest' - argument: {id: parentUuid, addressClass: int, resultHandler: deferred}
 *     parentUuid - The Uuid of the addresses parent
 *     addressClass - The class of the newly created address
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/saveRequest' - argument:
 *     resultHandler - A Deferred which is called when the request has been processed
 *	   forcePublicationCondition - Tell the backend to adjust the publication condition of subnodes
 *
 *   topic = '/publishObjectRequest' - argument: {resultHandler: deferred}
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/publishAddressRequest' - argument: {resultHandler: deferred}
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/deleteRequest' - argument: {id: nodeUuid, forceDelete: bool, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be deleted
 *     forceDelete - The object is deleted even if it is referenced by other objects
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/deleteWorkingCopyRequest' - argument: {id: nodeUuid, forceDelete: bool, resultHandler: deferred}
 *     nodeUuid - The Uuid of the working copy node which should be deleted
 *     forceDelete - The object is deleted even if it is referenced by other objects
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/canCutObjectRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be marked for a cut operation
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/canCutAddressRequest' - argument: {id: nodeUuid, resultHandler: deferred}
 *     nodeUuid - The Uuid of the address which should be marked for a cut operation
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/canCopyObjectRequest' - argument: {id: nodeUuid, copyTree: boolean resultHandler: deferred}
 *     nodeUuid - The Uuid of the node which should be marked for a copied operation
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/canCopyAddressRequest' - argument: {id: nodeUuid, copyTree: boolean resultHandler: deferred}
 *     nodeUuid - The Uuid of the address which should be marked for a copied operation
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/cutObjectRequest' - argument: {srcId: srcUuid, dstId: dstUuid, resultHandler: deferred}
 *     srcId - The Uuid of the node which should be cut
 *     dstId - The Uuid of the target node where the srcNode should be attached
 *	   forcePublicationCondition - Tell the backend to adjust the publication condition of subnodes
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/cutAddressRequest' - argument: {srcId: srcUuid, dstId: dstUuid, resultHandler: deferred}
 *     srcId - The Uuid of the address which should be cut
 *     dstId - The Uuid of the target address where the srcNode should be attached
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/copyObjectRequest' - argument: {srcId: srcUuid, dstId: dstUuid, copyTree: boolean, resultHandler: deferred}
 *     srcId - The Uuid of the node which should be copied
 *     dstId - The Uuid of the target node where the srcNode should be attached
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/copyAddressRequest' - argument: {srcId: srcUuid, dstId: dstUuid, copyTree: boolean, resultHandler: deferred}
 *     srcId - The Uuid of the address which should be copied
 *     dstId - The Uuid of the target address where the srcNode should be attached
 *     copyTree - specifies whether the complete tree should be copied or only the selected node
 *     resultHandler - A Deferred which is called when the request has been processed
 *
 *   topic = '/getObjectPathRequest' - argument: {id: nodeUuid, resultHandler: deferred, ignoreDirtyFlag: bool}
 *     nodeUuid - The Uuid of the target Node
 *     resultHandler - A Deferred which is called when the request has been processed
 *     ignoreDirtyFlag - boolean value indicating if the dirty flag should be ignored
 *
 *   topic = '/getAddressPathRequest' - argument: {id: nodeUuid, resultHandler: deferred, ignoreDirtyFlag: bool}
 *     nodeUuid - The Uuid of the target Address
 *     resultHandler - A Deferred which is called when the request has been processed
 *     ignoreDirtyFlag - boolean value indicating if the dirty flag should be ignored
 *
 *   topic = '/changePublicationCondition' - argument: {id: nodeUuid, useWorkingCopy: bool, publicationCondition: integer, forcePublicationCondition: bool, resultHandler: deferred}
 *     nodeUuid - The Uuid of the object which should be changed
 *     useWorkingCopy - save (true) or publish (false) the object
 *     publicationCondition - new PublicationCondition (1-3)
 *     forcePublicationCondition - Tell the backend to adjust the publication condition of published(!) subnodes
 *     resultHandler - A Deferred which is called when the request has been processed
 */

define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/Deferred",
    "dojo/DeferredList",
    "dojo/ready",
    "dojo/query",
    "dojo/topic",
    "dojo/string",
    "dojo/dom",
    "dojo/dom-style",
    "dijit/registry",
    "dijit/form/FilteringSelect",
    "dijit/form/ComboBox",
    "dijit/form/DateTextBox",
    "dijit/form/CheckBox",
    "ingrid/IgeEvents",
    "ingrid/hierarchy/objectLayout",
    "ingrid/hierarchy/addressLayout",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/UI",
    "ingrid/utils/Address",
    "ingrid/utils/List",
    "ingrid/utils/Tree",
    "ingrid/utils/Store",
    "ingrid/utils/String",
    "ingrid/utils/Syslist",
    "ingrid/utils/Grid",
    "ingrid/utils/General",
    "ingrid/utils/Dom",
    "ingrid/utils/Security",
    "ingrid/hierarchy/dirty",
    "ingrid/grid/CustomGrid",
    "ingrid/hierarchy/rules",
    "ingrid/hierarchy/requiredChecks"
], function(declare, lang, array, Deferred, DeferredList, ready, query, topic, string, dom, style, registry, FilteringSelect, ComboBox, DateTextBox, CheckBox, igeEvents,
    ingridObjectLayout, ingridAddressLayout, message, dialog, UtilUI, UtilAddress, UtilList, UtilTree, UtilStore, UtilString, UtilSyslist, UtilGrid, UtilGeneral, UtilDOM, UtilSecurity, dirty,
    CustomGrid, rules, checks) {
    return declare(null, {

        // remember the previous class for better conditions on class change
        previousClass: null,

        global: this,

        dataTree: registry.byId("dataTree"),

        // In this.global.currentUdk we cache the currently loaded udk in it's original representation.
        // Changes are not tracked here! We need it to access static information that is not
        // displayed in the gui (e.g. nodeUUID).
        //currentUdk: {},

        // This list holds all additionalField widgets (if any exist) for easy access
        // The list is initialized in init.js/initAdditionalFields
        additionalFieldWidgets: [],

        isSettingData: false,

        // TODO Move Dirty Flag handling to another file? 
        constructor: function() {
            var self = this;

            this.global.currentUdk = {};

            ready(function() {
                // Common requests
                topic.subscribe("/loadRequest", lang.hitch(self, self.handleLoadRequest));
                topic.subscribe("/saveRequest", lang.hitch(self, self.handleSaveRequest));
                topic.subscribe("/deleteRequest", lang.hitch(self, self.handleDeleteRequest));
                topic.subscribe("/deleteWorkingCopyRequest", lang.hitch(self, self.handleDeleteWorkingCopyRequest));
                topic.subscribe("/changePublicationCondition", lang.hitch(self, self.handleChangePublicationCondition));
                topic.subscribe("/inheritAddressToChildren", lang.hitch(self, self.handleInheritAddressToChildren));

                topic.subscribe("/selectNode", igeEvents.handleSelectNode);


                // Object requests
                topic.subscribe("/publishObjectRequest", lang.hitch(self, self.handlePublishObjectRequest));
                topic.subscribe("/createObjectRequest", lang.hitch(self, self.handleCreateObjectRequest));
                topic.subscribe("/canCutObjectRequest", lang.hitch(self, self.handleCanCutObjectRequest));
                topic.subscribe("/canCopyObjectRequest", lang.hitch(self, self.handleCanCopyObjectRequest));
                topic.subscribe("/cutObjectRequest", lang.hitch(self, self.handleCutObjectRequest));
                topic.subscribe("/copyObjectRequest", lang.hitch(self, self.handleCopyObjectRequest));

                // Address requests
                topic.subscribe("/publishAddressRequest", lang.hitch(self, self.handlePublishAddressRequest));
                topic.subscribe("/createAddressRequest", lang.hitch(self, self.handleCreateAddressRequest));
                topic.subscribe("/canCutAddressRequest", lang.hitch(self, self.handleCanCutAddressRequest));
                topic.subscribe("/canCopyAddressRequest", lang.hitch(self, self.handleCanCopyAddressRequest));
                topic.subscribe("/cutAddressRequest", lang.hitch(self, self.handleCutAddressRequest));
                topic.subscribe("/copyAddressRequest", lang.hitch(self, self.handleCopyAddressRequest));

                // QA requests
                topic.subscribe("/forwardObjectToQARequest", lang.hitch(self, self.handleForwardObjectToQARequest));
                topic.subscribe("/forwardAddressToQARequest", lang.hitch(self, self.handleForwardAddressToQARequest));
                topic.subscribe("/forwardObjectToAuthorRequest", lang.hitch(self, self.handleForwardObjectToAuthorRequest));
                topic.subscribe("/forwardAddressToAuthorRequest", lang.hitch(self, self.handleForwardAddressToAuthorRequest));


                // Set initial values
                dirty.dirtyFlag = false;
            });

            // since onChange-event is called via a setTimeout
            // the setting of data is in an unpredictable order and time
            // while setting data is going on we do not set the dirty flag
            // after 100ms without any new onChange-event, we allow to set
            // the dirty flag again
            //_onSetDirtyFlag = null;
        },



        // This function has to be called before any UI functions that are about to change the
        // state of the tree or the currently displayed dataset. It checks if there are unsaved changes
        // and queries the user if he wants to save changes, discard them or abort.
        // The function returns a deferred object users can attach callbacks to.
        // The deferred object signals an error if the user canceled the operation. No state changes
        // should be done in this case.
        // @arg nodeId - optional parameter that specifies the node that is about to be loaded
        checkForUnsavedChanges: function(nodeId, extraInfo) {
            if (this.hasUnsavedChanged()) {
                return this.showUnsavedChangesDialog(nodeId, extraInfo);
            } else {
                var def = new Deferred();
                def.resolve();
                return def.promise;
            }
        },

        hasUnsavedChanged: function() {
            // If the current user does not have write permission on the current obj/adr, don't display a dialog,
            // clear the dirty flag and return as normal
            if (this.global.currentUdk.writePermission === false && this.global.currentUdk.uuid != "newNode") {
                lang.hitch(dirty, dirty.resetDirtyFlag);
                return false;
            }

            return dirty.dirtyFlag;
        },

        showUnsavedChangesDialog: function(nodeId, extraInfo) {
            var deferred = new Deferred();
            var displayText = "";
            if (this.global.currentUdk.nodeAppType == "O")
                displayText = message.get("dialog.object.saveChangesHint");
            else
                displayText = message.get("dialog.address.saveChangesHint");

            if (extraInfo) {
                displayText += "<br/><br/>" + message.get("dialog.save.extra.hint") + extraInfo;
            }

            var self = this;
            dialog.show(message.get("dialog.saveChangesTitle"), displayText, dialog.INFO, [{
                caption: message.get("general.cancel"),
                action: function() {
                    UtilTree.selectNode("dataTree", self.global.currentUdk.uuid);
                    deferred.reject("LOAD_CANCELLED");
                }
            }, {
                caption: message.get("general.no"),
                action: function() {
                    lang.hitch(dirty, dirty.resetDirtyFlag);
                    deferred.resolve("DISCARD");
                }
            }, {
                caption: message.get("general.yes"),
                action: function() {
                    var def = new Deferred();
                    def.then(function() {
                        deferred.resolve("SAVE");
                    }, function(errMsg) {
                        deferred.reject(errMsg);
                    });

                    topic.publish("/saveRequest", {
                        resultHandler: def
                    });
                }
            }]);

            // If the user was editing a newly created node and he wants to discard the changes
            // delete the newly created node.
            if (this.global.currentUdk.uuid == "newNode" && nodeId != "newNode") {
                deferred.then(function(arg) {
                    if (arg == "DISCARD") {
                        console.debug("Discarding the newly created node.");
                        var newNode = UtilTree.getNodeById("dataTree", "newNode");
                        //newNode.destroy();
                        if (newNode !== null) {
                            // a revert creates problems here when adding new address, discarding it and 
                            // creating it again and then do a save!
                            UtilTree.deleteNode("dataTree", "newNode");
                        } else {
                            console.debug("NewNode shouldn't be null! There must have been an error before!");
                        }
                    }
                });
            }

            return deferred.promise;
        },


        handleLoadRequest: function(msg) {
            console.debug("About to be loaded: " + msg.id);
            var nodeId = msg.id;
            var nodeAppType = msg.appType;
            var resultHandler = msg.resultHandler;
            // var deferred = this.checkForUnsavedChanges();
            var deferred2 = new Deferred();

            // TODO Check if we are in a state where it's safe to load data.
            //      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
            // var loadErrback = function(err) {
            //     if (typeof(resultHandler) != "undefined") {
            //         resultHandler.reject(err);
            //     }
            // };

            var self = this;
            // deferred.then(function() {
                // Don't process newNode and objectRoot load requests.
                if (msg.id == "newNode" || msg.id == "objectRoot" || msg.id == "addressRoot" || msg.id == "addressFreeRoot") {
                    topic.publish("/selectNode", {
                        id: "dataTree",
                        node: msg.node
                    });
                    self.global.currentUdk = {};
                    msg.resultHandler.resolve();
                } else {
                    deferred2.resolve();
                }
            // }, loadErrback);

            var loadCallback = function() {
                console.debug("udkDataProxy calling ObjectService.getNodeData(" + nodeId + ", " + nodeAppType + ")");
                // ---- DWR call to load the data ----
                if (nodeAppType == "O") {
                    ObjectService.getNodeData(nodeId, nodeAppType, "false", {
                        preHook: UtilUI.enterLoadingState,
                        callback: function(res) {
                            console.debug("in callback");
                            if (res !== null) {
                                console.debug("set data");
                                self._setData(res)
                                    .then(function() {
                                        console.debug("update Tree");
                                        self._updateTree(res);
                                        checks.resetRequiredFields();
                                        console.debug("call resultHandler");
                                        if (resultHandler)
                                            resultHandler.resolve();
                                        console.debug("reset dirty flag after 0.1s");
                                        // since onChange events are fired asynchronously 
                                        // we have to wait a bit to reset the dirty flag 
                                        //setTimeout(dirty.resetDirtyFlag, 100);
                                        setTimeout(lang.hitch(dirty, dirty.resetDirtyFlag), 100);

                                        console.debug("on after load");
                                        self.onAfterLoad();
                                        console.debug("exit loading state");
                                        UtilUI.exitLoadingState();
                                    });
                            } else {
                                //							console.debug(resultHandler);
                                if (typeof(resultHandler) != "undefined") {
                                    resultHandler.reject("Error loading object. The object with the specified id doesn't exist!");
                                }
                            }
                            return res;
                        },
                        errorHandler: function(message, err) {
                            UtilUI.exitLoadingState();
                            console.error("Error in js/js: Error while waiting for nodeData: " + message);
                            console.debug(err);
                            resultHandler.reject(new Error(message));
                        }
                    });
                } else if (nodeAppType == "A") {
                    AddressService.getAddressData(nodeId, "false", {
                        preHook: UtilUI.enterLoadingState,
                        callback: function(res) {
                            if (res !== null) {
                                self._setData(res)
                                    .then(function() {
                                        self._updateTree(res);
                                        checks.resetRequiredFields();
                                        if (resultHandler)
                                            resultHandler.resolve();
                                        // since onChange events are fired asynchronously 
                                        // we have to wait a bit to reset the dirty flag 
                                        setTimeout(lang.hitch(dirty, dirty.resetDirtyFlag), 100);
                                        //lang.hitch(dirty, dirty.resetDirtyFlag);
                                        self.onAfterLoad();
                                        UtilUI.exitLoadingState();
                                    });
                            } else {
                                if (typeof(resultHandler) != "undefined") {
                                    resultHandler.reject("Error loading Address. The Address with the specified id doesn't exist!");
                                }
                            }
                            return res;
                        },
                        errorHandler: function(message) {
                            UtilUI.exitLoadingState();
                            console.error("Error in js/js: Error while waiting for addressData: " + message);
                            resultHandler.reject(new Error(message));
                        }
                    });
                }
            };

            deferred2.then(loadCallback);
        },

        handleCreateObjectRequest: function(msg) {
            console.debug("create Object Request");
            var nodeId = msg.id;
            if (nodeId == "objectRoot") {
                nodeId = null;
            }

            var self = this;

            // TODO Check if we are in a state where it's safe to create a node?
            //      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
            var deferred = this.checkForUnsavedChanges();
            var loadErrback = function(err) {
                msg.resultHandler.reject(err);
            };
            console.debug("createNode under id: " + nodeId);
            var loadCallback = function() {
                ObjectService.createNewNode(nodeId, {
                    preHook: UtilUI.enterLoadingState,
                    postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        msg.resultHandler.resolve(res);
                        console.debug("set data");
                        self._setData(res)
                        .then(lang.hitch(dirty, dirty.resetDirtyFlag)); // we must set dirty flag here!
                    },
                    //				timeout:10000,
                    errorHandler: function(message) {
                        UtilUI.exitLoadingState();
                        //					msg.resultHandler.reject("Error in js/js: Error while creating a new node.");
                        console.debug("Error in js/js: Error while creating a new node.");
                        msg.resultHandler.reject(message);
                    }
                });
            };
            deferred.then(loadCallback, loadErrback);
        },

        handleCreateAddressRequest: function(msg) {
            console.debug("handleCreateAddressRequest()");

            var nodeId = msg.id + "";
            if (msg.id == "addressRoot" || msg.id == "addressFreeRoot") {
                nodeId = null;
            }

            var self = this;

            // TODO Check if we are in a state where it's safe to create a node?
            //      If we are, load the data. If not delay the call and bounce back the message (e.g. query user).
            var deferred = this.checkForUnsavedChanges();
            var loadErrback = function(err) {
                msg.resultHandler.reject(err);
            };
            var loadCallback = function() {
                AddressService.createNewAddress(nodeId, {
                    preHook: UtilUI.enterLoadingState,
                    postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        res.addressClass = msg.addressClass;
                        if (res.addressClass === 0) {
                            res.nodeDocType = "Institution_B";
                        } else if (res.addressClass == 1) {
                            res.nodeDocType = "InstitutionUnit_B";
                        } else if (res.addressClass == 2) {
                            res.nodeDocType = "InstitutionPerson_B";
                        } else if (res.addressClass == 3) {
                            res.nodeDocType = "PersonAddress_B";
                        }

                        msg.resultHandler.resolve(res);
                        self._setData(res)
                            .then(lang.hitch(dirty, dirty.resetDirtyFlag));
                    },
                    //				timeout:10000,
                    errorHandler: function(message) {
                        UtilUI.exitLoadingState();
                        //					msg.resultHandler.reject("Error in js/js: Error while creating a new address.");
                        console.debug("Error in js/js: Error while creating a new address.");
                        msg.resultHandler.reject(new Error(message));
                    }
                });
            };
            deferred.then(loadCallback, loadErrback);
        },


        handleSaveRequest: function(msg) {
            if (this.global.currentUdk.nodeAppType == "O")
                this._handleSaveObjectRequest(msg);
            else if (this.global.currentUdk.nodeAppType == "A")
                this._handleSaveAddressRequest(msg);
        },

        _handleSaveAddressRequest: function(msg) {
            // Address validity check	
            var valid = checks.checkValidityOfAddressInputElements();
            if (valid != "VALID") {
                if (msg && msg.resultHandler) {
                    msg.resultHandler.reject(new Error(valid));
                }
                return;
            }

            // Construct an MdekAddressBean from the available data
            var nodeData = this._getData();

            // Deferred obj for the main save operation. The passed resulthandler is called with the appropriate result
            var onSaveDef = new Deferred();
            var self = this;
            onSaveDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, function(err) {
                msg.resultHandler.reject(err);
            });


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling AddressService.saveAddressData(" + nodeData.uuid + ", true)");
            AddressService.saveAddressData(nodeData, "true", false, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onSaveDef.resolve(res);
                },
                //			timeout:10000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while saving addressData:");
                    onSaveDef.reject(err);
                }
            });
        },


        _handleSaveObjectRequest: function(msg) {
            var valid = checks.checkValidityOfInputElements();
            if (valid != "VALID") {
                if (msg && msg.resultHandler) {
                    msg.resultHandler.reject(new Error(valid));
                }
                return;
            }

            // Determine if the publication condition should be adjusted (forced) for subnodes
            var forcePubCond = false;
            if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
                forcePubCond = msg.forcePublicationCondition;
            }

            // Construct an MdekDataBean from the available data
            var nodeData = this._getData();

            // Deferred obj for the main save operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onSaveDef = new Deferred();
            onSaveDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, function(err) {
                msg.resultHandler.reject(err);
            });


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling ObjectService.saveNodeData(" + nodeData.uuid + ", true, " + forcePubCond + ")");
            ObjectService.saveNodeData(nodeData, "true", forcePubCond, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onSaveDef.resolve(res);
                },
                //			timeout:10000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    // Check for the publication condition error
                    // TODO: A normal save operation shouldn't trigger it?
                    if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
                        var onForceSaveDef = new Deferred();

                        // If the user wants to save the object anyway, set force save and start another request
                        onForceSaveDef.then(function() {
                            msg.forcePublicationCondition = true;
                            topic.publish("/saveRequest", msg);
                            // If the user cancelled the operation notify the result handler
                        }, onSaveDef.reject);

                        // Display the 'publication condition' dialog with the attached resultHandler
                        //					dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"SAVE", resultHandler:onForceSaveDef});
                        var displayText = message.get("operation.hint.publicationConditionSaveHint");
                        dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                            caption: message.get("general.cancel"),
                            action: function() { /**onForceSaveDef.reject();*/ }
                        }, {
                            caption: message.get("general.save"),
                            action: function() {
                                onForceSaveDef.resolve();
                            }
                        }]);

                    } else {
                        console.debug("Error in js/js: Error while saving nodeData:");
                        onSaveDef.reject(err);
                    }
                }
            });
        },


        handlePublishObjectRequest: function(msg) {
            // Construct an MdekDataBean from the available data
            var nodeData = this._getData();

            var forcePubCond = false;
            if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
                forcePubCond = msg.forcePublicationCondition;
            }

            // Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onPublishDef = new Deferred();
            onPublishDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, function(err) {
                if (err.cause && err.cause.message)
                    msg.resultHandler.reject(err.cause.message);
                else
                    msg.resultHandler.reject(err);
            });


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling ObjectService.saveNodeData(" + nodeData.uuid + ", false, " + forcePubCond + ")");
            ObjectService.saveNodeData(nodeData, "false", forcePubCond, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    // refresh children
                    UtilTree.refreshChildren("dataTree");
                    onPublishDef.resolve(res);
                },
                //			timeout:10000,
                errorHandler: function(err, ex) {
                    UtilUI.exitLoadingState();
                    // Check for the publication condition error
                    if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
                        var onForcePublishDef = new Deferred();

                        // If the user wants to publish the object anyway, set force publish and start another request
                        onForcePublishDef.then(function() {
                            msg.forcePublicationCondition = true;
                            topic.publish("/publishObjectRequest", msg);
                            // If the user cancelled the operation notify the result handler
                        }, onPublishDef.reject);

                        // Display the 'publication condition' dialog with the attached resultHandler
                        //					dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"SAVE", resultHandler:onForcePublishDef});
                        var displayText = message.get("operation.hint.publicationConditionSaveHint");
                        dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                            caption: message.get("general.cancel"),
                            action: function() { /**onForcePublishDef.reject();*/ }
                        }, {
                            caption: message.get("general.save"),
                            action: function() {
                                onForcePublishDef.resolve();
                            }
                        }], 382, 220);

                    } else {
                        console.debug("Error in js/js: Error while publishing nodeData:");
                        onPublishDef.reject(ex);
                    }
                }
            });
        },

        handlePublishAddressRequest: function(msg) {
            // Construct an MdekDataBean from the available data
            var nodeData = this._getData();

            var forcePubCond = false;
            if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
                forcePubCond = msg.forcePublicationCondition;
            }

            // Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onPublishDef = new Deferred();
            onPublishDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, function(err) {
                msg.resultHandler.reject(err);
            });


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling AddressService.saveAddressData(" + nodeData.uuid + ", false)");
            AddressService.saveAddressData(nodeData, "false", forcePubCond, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    // refresh children
                    UtilTree.refreshChildren("dataTree");
                    onPublishDef.resolve(res);
                },
                //			timeout:10000,
                errorHandler: function(err, msg) {
                    UtilUI.exitLoadingState();

                    if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
                        //var onForcePublishDef = new Deferred();

                        // If the user wants to publish the object anyway, set force publish and start another request
                        // To keep it simple let this do the user manually!!! (INGRID33-12)
                        /*onForcePublishDef.then(function() {
                                msg.forcePublicationCondition = true;
                                topic.publish("/publishAddressRequest", [msg]);
                            });
                            // If the user cancelled the operation notify the result handler
                            onForcePublishDef.addErrback(onPublishDef.reject);
                            */
                        // Display the 'publication condition' dialog with the attached resultHandler
                        //                  dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"SAVE", resultHandler:onForcePublishDef});
                        var displayText = message.get("operation.hint.publicationConditionSaveHint.address");
                        dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                                caption: message.get("general.ok"),
                                action: function() {}
                            }
                        ], 382, 220);

                    } else {
                        console.debug("Error in js/js: Error while publishing address:");
                        onPublishDef.reject(msg);
                    }
                }
            });
        },

        handleDeleteWorkingCopyRequest: function(msg) {
            if (msg.id == "newNode") {
                msg.resultHandler.resolve();
                return;
            }

            var nodeAppType = UtilTree.getNodeById("dataTree", msg.id).item.nodeAppType;
            if (nodeAppType == "O")
                this._handleDeleteObjectWorkingCopyRequest(msg);
            else if (nodeAppType == "A")
                this._handleDeleteAddressWorkingCopyRequest(msg);
        },

        _handleDeleteAddressWorkingCopyRequest: function(msg) {
            // var title = registry.byId(msg.id).title;

            var forceDelete = false;
            if (msg && typeof(msg.forceDelete) != "undefined") {
                forceDelete = msg.forceDelete;
            }

            var self = this;
            console.debug("udkDataProxy calling AddressService.deleteAddressWorkingCopy(" + msg.id + ", " + forceDelete + ")");
            AddressService.deleteAddressWorkingCopy(msg.id, forceDelete, "false", {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    if (res !== null) {
                        self._setData(res)
                            .then(function() {
                                self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                            });
                    }
                    msg.resultHandler.resolve(res);
                },
                //			timeout:10000,
                errorHandler: function(errMsg, err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while deleting address working copy: " + errMsg);
                    // Wrap the dwr error in a javscript Error object
                    //var e = new Error(errMsg);
                    //e.message = err;
                    msg.resultHandler.reject(err);
                }
            });
        },

        _handleDeleteObjectWorkingCopyRequest: function(msg) {
            console.debug("udkDataProxy calling ObjectService.deleteObjectWorkingCopy(" + msg.id + ")");
            var title = UtilTree.getNodeById("dataTree", msg.id).title;

            var forceDelete = false;
            if (msg && typeof(msg.forceDelete) != "undefined") {
                forceDelete = msg.forceDelete;
            }

            var self = this;
            ObjectService.deleteObjectWorkingCopy(msg.id, forceDelete, "false", {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    if (res !== null) {
                        self._setData(res)
                            .then(function() {
                                self.updateTreeAfterNewData(res, msg.id, msg.resultHandler);
                            });
                    }
                    msg.resultHandler.resolve(res);
                },
                //			timeout:10000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    if (err.indexOf("ENTITY_REFERENCED_BY_OBJ") != -1) {
                        var onForceDeleteDef = new Deferred();

                        // If the user wants to delete the object anyway, set force delete and start another request
                        onForceDeleteDef.then(function() {
                            msg.forceDelete = true;
                            self.handleDeleteRequest(msg);
                            //topic.publish("/cutObjectRequest", msg);
                            // If the user cancelled the operation notify the result handler
                        }, msg.resultHandler.reject);

                        // Display the 'force delete' dialog with the attached resultHandler
                        //					dialog.showPage(message.get("general.warning"), "mdek_forceDelete_dialog.html", 382, 220, true, {nodeAppType:"O", nodeTitle:title, resultHandler:onForceDeleteDef});
                        var displayText = string.substitute(message.get("operation.hint.forceDeleteObjectHint"), [title]);

                        dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                            caption: message.get("general.no"),
                            action: function() {
                                onForceDeleteDef.reject();
                            }
                        }, {
                            caption: message.get("general.yes"),
                            action: function() {
                                onForceDeleteDef.resolve();
                            }
                        }]);
                    } else {
                        console.debug("Error in js/js: Error while deleting object: " + err);
                        msg.resultHandler.reject(err);
                    }
                }
            });
        },


        handleDeleteRequest: function(msg) {
            var node = UtilTree.getNodeById("dataTree", msg.id);
            var nodeAppType = node.item.nodeAppType;
            var title = node.item.title;

            var forceDelete = false;
            if (msg && msg.forceDelete && typeof(msg.forceDelete) != "undefined") {
                forceDelete = msg.forceDelete;
            }

            var self = this;
            if (nodeAppType == "O") {
                console.debug("udkDataProxy calling ObjectService.deleteNode(" + msg.id + ", " + forceDelete + ")");
                ObjectService.deleteNode(msg.id, forceDelete, "false", {
                    preHook: UtilUI.enterLoadingState,
                    postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        msg.resultHandler.resolve(res);
                    },
                    //				timeout:10000,
                    errorHandler: function(err) {
                        UtilUI.exitLoadingState();
                        if (err.indexOf("ENTITY_REFERENCED_BY_OBJ") != -1) {
                            var onForceDeleteDef = new Deferred();

                            // If the user wants to delete the object anyway, set force delete and start another request
                            onForceDeleteDef.then(function() {
                                msg.forceDelete = true;
                                self.handleDeleteRequest(msg);
                                //topic.publish("/cutObjectRequest", msg);
                                // If the user cancelled the operation notify the result handler
                            }, function() {
                                msg.resultHandler.reject("OPERATION_CANCELLED");
                            });

                            // Display the 'force delete' dialog with the attached resultHandler
                            //						dialog.showPage(message.get("general.warning"), "mdek_forceDelete_dialog.html", 382, 220, true, {nodeAppType:"O", nodeTitle:title, resultHandler:onForceDeleteDef});
                            var displayText = string.substitute(message.get("operation.hint.forceDeleteObjectHint"), [title]);

                            dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                                caption: message.get("general.no"),
                                action: function() {
                                    onForceDeleteDef.reject();
                                }
                            }, {
                                caption: message.get("general.yes"),
                                action: function() {
                                    onForceDeleteDef.resolve();
                                }
                            }]);

                        } else {
                            console.debug("Error in js/js: Error while deleting object: " + err);
                            msg.resultHandler.reject(err);
                        }
                    }
                });

            } else if (nodeAppType == "A") {
                console.debug("udkDataProxy calling AddressService.deleteAddress(" + msg.id + ")");
                AddressService.deleteAddress(msg.id, forceDelete, "false", {
                    preHook: UtilUI.enterLoadingState,
                    postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        msg.resultHandler.resolve(res);
                    },
                    //				timeout:10000,
                    errorHandler: function(errMsg, err) {
                        UtilUI.exitLoadingState();
                        //					console.debug("Error in js/js: Error while deleting address: "+err);
                        // Wrap the dwr error in a javscript Error object
                        //var e = new Error(errMsg);
                        //e.message = err;
                        msg.resultHandler.reject(err);
                    }
                });
            }
        },

        handleChangePublicationCondition: function(msg) {
            var forcePubCondition = false;
            if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
                forcePubCondition = msg.forcePublicationCondition;
            }

            var useWorkingCopy = true;
            if (msg && typeof(msg.useWorkingCopy) != "undefined") {
                useWorkingCopy = msg.useWorkingCopy;
            }

            var self = this;
            console.debug("udkDataProxy calling ObjectService.saveObjectPublicationCondition(" + msg.id + ", useWorkingCopy=" + useWorkingCopy + ", forcePubCondition=" + forcePubCondition + ")");
            ObjectService.saveObjectPublicationCondition(msg.id, useWorkingCopy, msg.publicationCondition, forcePubCondition, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    msg.resultHandler.resolve(res);
                },
                errorHandler: function(err, errObj) {
                    UtilUI.exitLoadingState();
                    // Check for the publication condition error
                    if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
                        var onForceSaveDef = new Deferred();
                        // If the user wants to save the object anyway, set force save and start another request
                        onForceSaveDef.then(function() {
                            msg.forcePublicationCondition = true;
                            self.handleChangePublicationCondition(msg);
                        }, function(err) {
                            msg.resultHandler.reject(err);
                        });

                        // Display the 'publication condition' dialog with the attached resultHandler
                        var displayText = message.get("operation.hint.publicationConditionSaveHint");
                        dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                            caption: message.get("general.cancel"),
                            action: function() { /**onForceSaveDef.reject();*/ }
                        }, {
                            caption: message.get("general.save"),
                            action: function() {
                                onForceSaveDef.resolve();
                            }
                        }]);

                    } else {
                        console.debug("Error in js/eventSubscriber.js: handleChangePublicationCondition:");
                        msg.resultHandler.reject(errObj);
                    }
                }
            });
        },

        handleInheritAddressToChildren: function(msg) {
            // check for unsaved changes if parent node was clicked! 
            var isChild = UtilDOM.activeNodeHasParent(msg.id);

            var deferred = null;
            if (isChild) {
                deferred = this.checkForUnsavedChanges();
            } else {
                deferred = new Deferred();
                deferred.resolve();
            }

            deferred.then(function() {
                console.debug("udkDataProxy calling AddressService.inheritAddressToChildren(" + msg.id + ")");
                AddressService.inheritAddressToChildren(msg.id, {
                    preHook: UtilUI.enterLoadingState,
                    postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        if (isChild) {
                            // reload node
                            topic.publish("/loadRequest", {
                                id: this.global.currentUdk.uuid,
                                appType: this.global.currentUdk.nodeAppType
                                /*,
                                node: item,
                                resultHandler: deferred*/
                            });
                        }
                        msg.resultHandler.resolve(res);
                    },
                    errorHandler: function(err) {
                        UtilUI.exitLoadingState();
                        displayErrorMessage(err);
                    }
                });
            });
        },


        handleCanCutObjectRequest: function(msg) {
            console.debug("udkDataProxy calling ObjectService.canCutNode(" + msg.id + ")");

            ObjectService.canCutObject(msg.id, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function() {
                    msg.resultHandler.resolve();
                },
                //			timeout:10000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while marking a node for a cut operation: " + err);
                    displayErrorMessage(err);
                    msg.resultHandler.reject(err);
                }
            });
        },

        handleCanCutAddressRequest: function(msg) {
            console.debug("udkDataProxy calling AddressService.canCutAddress(" + msg.id + ")");

            AddressService.canCutAddress(msg.id, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function() {
                    msg.resultHandler.resolve();
                },
                //			timeout:10000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while marking an address for a cut operation: " + err);
                    msg.resultHandler.reject(err);
                }
            });
        },


        handleCanCopyObjectRequest: function(msg) {
            console.debug("udkDataProxy calling ObjectService.canCopyNode(" + msg.nodeIds + ", " + msg.copyTree + ")");

            ObjectService.canCopyObjects(msg.nodeIds, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function() {
                    msg.resultHandler.resolve();
                },
                //			timeout:30000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while marking a node for a copy operation: " + err);
                    msg.resultHandler.reject(err);
                }
            });
        },

        handleCanCopyAddressRequest: function(msg) {
            console.debug("udkDataProxy calling AddressService.canCopyAddress(" + msg.nodeIds + ", " + msg.copyTree + ")");

            AddressService.canCopyAddresses(msg.nodeIds, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function() {
                    msg.resultHandler.resolve();
                },
                //			timeout:30000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while marking an address for a copy operation: " + err);
                    msg.resultHandler.reject(err);
                }
            });
        },

        handleCutObjectRequest: function(msg) {
            if (msg.dstId == "objectRoot") {
                msg.dstId = null;
            }

            var forcePubCond = false;
            if (msg && typeof(msg.forcePublicationCondition) != "undefined") {
                forcePubCond = msg.forcePublicationCondition;
            }

            console.debug("udkDataProxy calling ObjectService.moveNode(" + msg.srcIds + ", " + msg.parentUuids + ", " + msg.dstId + ", " + forcePubCond + ")");
            ObjectService.moveNodes(msg.srcIds, msg.parentUuids, msg.dstId, forcePubCond, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    msg.resultHandler.resolve(res);
                },
                //			timeout:30000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    // Check for the publication condition error
                    if (err.indexOf("SUBTREE_HAS_LARGER_PUBLICATION_CONDITION") != -1) {
                        var onForceMoveDef = new Deferred();

                        // If the user wants to publish the object anyway, set force publish and start another request
                        onForceMoveDef.then(function() {
                            msg.forcePublicationCondition = true;
                            topic.publish("/cutObjectRequest", msg);
                            // If the user cancelled the operation notify the result handler
                        }, msg.resultHandler.reject);

                        // Display the 'publication condition' dialog with the attached resultHandler
                        //					dialog.showPage(message.get("general.warning"), "mdek_pubCond_dialog.html", 382, 220, true, {operation:"MOVE", resultHandler:onForceMoveDef});
                        var displayText = message.get("operation.hint.publicationConditionMoveHint");
                        dialog.show(message.get("general.warning"), displayText, dialog.WARNING, [{
                            caption: message.get("general.cancel"),
                            action: function() {
                                onForceMoveDef.reject();
                            }
                        }, {
                            caption: message.get("general.move"),
                            action: function() {
                                onForceMoveDef.resolve();
                            }
                        }], 382, 220);

                    } else {
                        console.debug("Error in js/js: Error while moving nodeData:");
                        msg.resultHandler.reject(err);
                    }
                }
            });
        },

        handleCutAddressRequest: function(msg) {
            var srcIds = msg.srcIds;
            var dstId = msg.dstId;
            var parentUuids = msg.parentUuids;
            var moveToFreeAddress = false;

            if (dstId == "addressRoot") {
                dstId = null;
            } else if (dstId == "addressFreeRoot") {
                dstId = null;
                moveToFreeAddress = true;
            }

            console.debug("udkDataProxy calling AddressService.moveAddress(" + srcIds + ", " + parentUuids + ", " + dstId + ", " + moveToFreeAddress + ")");
            AddressService.moveAddresses(srcIds, parentUuids, dstId, moveToFreeAddress, false, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    msg.resultHandler.resolve(res);
                },
                //			timeout:30000,
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while moving address:");
                    msg.resultHandler.reject(err);
                }
            });
        },

        handleCopyObjectRequest: function(msg) {
            console.debug("udkDataProxy calling ObjectService.copyNode(" + msg.srcIds + ", " + msg.dstId + ", " + msg.copyTree + ")");

            var srcIds = msg.srcIds;
            var dstId = msg.dstId;

            if (dstId == "objectRoot") {
                dstId = null;
            }

            var onCopyDef = new Deferred();
            onCopyDef.then(function(res) {
                // Copy operation was successful. Pass the copied node to the result handler
                msg.resultHandler.resolve(res);
            }, msg.resultHandler.reject);

            ObjectService.copyNodes(srcIds, dstId, msg.copyTree, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onCopyDef.resolve(res);
                },
                timeout: 3000, // Wait three seconds for the call to finish and display the 'please wait' dialog afterwards 
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    if (err == "Timeout") {
                        var onCopyOpFinishedDef = new Deferred();
                        // TODO we need to return some information about the copied node!
                        onCopyOpFinishedDef.then(function(res) {
                            if (res == "JOB_CANCELLED") {
                                onCopyDef.resolve(null);
                            } else {
                                onCopyDef.resolve(res);
                            }
                        }, onCopyDef.reject);
                        dialog.showPage(message.get("general.hint"), "dialogs/mdek_waitForJob_dialog.jsp?c=" + userLocale, 350, 155, true, {
                            resultHandler: onCopyOpFinishedDef
                        });
                    } else {
                        console.debug("Error in js/js: Error while copying nodes: " + err);
                        onCopyDef.reject(err);
                    }
                }
            });
        },

        handleCopyAddressRequest: function(msg) {
            var srcIds = msg.srcIds;
            var dstId = msg.dstId;
            var copyToFreeAddress = false;

            if (dstId == "addressRoot") {
                dstId = null;
            } else if (dstId == "addressFreeRoot") {
                dstId = null;
                copyToFreeAddress = true;
                msg.copyTree = false;
            }

            var onCopyDef = new Deferred();
            onCopyDef.then(function(res) {
                // Copy operation was successful. Pass the copied node to the result handler
                msg.resultHandler.resolve(res);
            }, msg.resultHandler.reject);

            console.debug("udkDataProxy calling AddressService.copyAddress(" + msg.srcId + ", " + msg.dstId + ", " + msg.copyTree + ", " + copyToFreeAddress + ")");
            AddressService.copyAddresses(srcIds, dstId, msg.copyTree, copyToFreeAddress, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onCopyDef.resolve(res);
                },
                timeout: 3000, // Wait three seconds for the call to finish and display the 'please wait' dialog afterwards 
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    if (err == "Timeout") {
                        var onCopyOpFinishedDef = new Deferred();
                        // TODO we need to return some information about the copied node!
                        onCopyOpFinishedDef.then(function(res) {
                            if (res == "JOB_CANCELLED") {
                                onCopyDef.resolve(null);
                            } else {
                                onCopyDef.resolve(res);
                            }
                        }, onCopyDef.reject);
                        dialog.showPage(message.get("general.hint"), "dialogs/mdek_waitForJob_dialog.jsp?c=" + userLocale, 350, 155, true, {
                            resultHandler: onCopyOpFinishedDef
                        });
                    } else {
                        console.debug("Error in js/js: Error while copying addresses: " + err);
                        onCopyDef.reject(err);
                    }
                }
            });
        },

        handleForwardObjectToQARequest: function(msg) {
            // Construct an MdekDataBean from the available data
            var nodeData = this._getData();

            // Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onForwardDef = new Deferred();
            onForwardDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, msg.resultHandler.reject);

            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling ObjectService.assignObjectToQA(" + nodeData.uuid + ")");
            ObjectService.assignObjectToQA(nodeData, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onForwardDef.resolve(res);
                },
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while assigning node to QA: " + err);
                    onForwardDef.reject(err);
                }
            });
        },

        handleForwardAddressToQARequest: function(msg) {
            // Construct an MdekAddressBean from the available data
            var nodeData = this._getData();

            // Deferred adr for the main publish operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onPublishDef = new Deferred();
            onPublishDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, msg.resultHandler.reject);


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling AddressService.assignAddressToQA(" + nodeData.uuid + ")");
            AddressService.assignAddressToQA(nodeData, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onPublishDef.resolve(res);
                },
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while assigning address to QA: " + err);
                    onPublishDef.reject(err);
                }
            });
        },


        handleForwardObjectToAuthorRequest: function(msg) {
            // Construct an MdekDataBean from the available data
            var nodeData = this._getData();

            // Deferred obj for the main publish operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onForwardDef = new Deferred();
            onForwardDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, msg.resultHandler.reject);


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling ObjectService.reassignObjectToAuthor(" + nodeData.uuid + ")");
            ObjectService.reassignObjectToAuthor(nodeData, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onForwardDef.resolve(res);
                },
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while reassigning node to Author: " + err);
                    onForwardDef.reject(err);
                }
            });
        },

        updateTreeAfterNewData: function(res, uuid, callback) {
            this._updateTree(res, uuid);
            this.onAfterSave();
            callback.resolve(res);
            setTimeout(lang.hitch(dirty, dirty.resetDirtyFlag), 100);
        },

        handleForwardAddressToAuthorRequest: function(msg) {
            // Construct an MdekAddressBean from the available data
            var nodeData = this._getData();

            // Deferred adr for the main publish operation. The passed resulthandler is called with the appropriate result
            var self = this;
            var onPublishDef = new Deferred();
            onPublishDef.then(function(res) {
                self._setData(res)
                    .then(function() {
                        self.updateTreeAfterNewData(res, nodeData.uuid, msg.resultHandler);
                    });
            }, msg.resultHandler.reject);


            // ---- DWR call to store the data ----
            console.debug("udkDataProxy calling AddressService.reassignAddressToAuthor(" + nodeData.uuid + ")");
            AddressService.reassignAddressToAuthor(nodeData, {
                preHook: UtilUI.enterLoadingState,
                postHook: UtilUI.exitLoadingState,
                callback: function(res) {
                    onPublishDef.resolve(res);
                },
                errorHandler: function(err) {
                    UtilUI.exitLoadingState();
                    console.debug("Error in js/js: Error while reassigning address to Author: " + err);
                    onPublishDef.reject(err);
                }
            });
        },


        /*handleGetObjectPathRequest: function(msg) {
            var loadErrback = function() {
                msg.resultHandler.reject();
            };
            var loadCallback = function() {
                console.debug("udkDataProxy calling ObjectService.getPathToObject(" + msg.id + ")");
                ObjectService.getPathToObject(msg.id, {
                    //preHook: UtilUI.enterLoadingState,
                    //postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        msg.resultHandler.resolve(res);
                    },
                    //				timeout:10000,
                    errorHandler: function(errorMessage) {
                        //UtilUI.exitLoadingState();
                        console.debug("Error in js/js: Error while getting path to node: " + errorMessage);
                        dialog.show(message.get("general.warning"), string.substitute(message.get("general.warning.unknownId"), [msg.id]), dialog.WARNING);
                        msg.resultHandler.reject();
                    }
                });
            };

            if (msg.ignoreDirtyFlag) {
                // If the dirty flag is ignored, the request can be started
                loadCallback();
            } else {
                // Otherwise check for unsaved changes and start the request afterwards
                var deferred = this.checkForUnsavedChanges();
                deferred.then(loadCallback, loadErrback);
            }
        },*/

        // event.connect point. Called when data has been saved 
        onAfterSave: function() {},
        onAfterPublish: function() {},
        onAfterLoad: function() {},

        _setData: function(nodeData) {
            // set a flag so that no dirty flag is set!
            this.isSettingData = true;

            this.global.currentUdk = nodeData;

            // -- We check if we received an Address or Object and call the corresponding function --
            var def = null;
            switch (nodeData.nodeAppType.toUpperCase()) {
                case "A":
                    // show container to correctly layout the elements
                    style.set(dom.byId("contentFrameBodyAddress"), "display", "block");
                    def = ingridAddressLayout.create()
                    .then(lang.partial(this._initResponsibleUserAddressList, nodeData))
                    .then(lang.hitch(this, lang.partial(this._setAddressData, nodeData)));
                    break;
                case "O":
                    // show container to correctly layout the elements
                    style.set(dom.byId("contentFrameBodyObject"), "display", "block");
                    // additional fields will be determined once during initialization!
                    ingridObjectLayout.additionalFieldWidgets = this.additionalFieldWidgets;
                    def = ingridObjectLayout.create()
                    .then(lang.partial(this._initResponsibleUserObjectList, nodeData))
                    .then(lang.hitch(this, lang.partial(this._setObjectData, nodeData)));
                    break;
                default:
                    console.debug("Error in _setData - Node Type must be \'A\' or \'O\'!");
                    break;
            }

            def.then(lang.hitch(igeEvents, lang.partial(igeEvents.disableInputOnWrongPermission, nodeData)));
            // flag is resetted in loading function
            //def.then(dirty.resetDirtyFlag);

            return def;
        },

        _setAddressData: function(nodeData) {
            // Set the address type list values depending on the parent class
            var parentClass = nodeData.parentClass;
            if (parentClass === null) {
                if (nodeData.addressClass == 3)
                    parentClass = -2;
                else
                    parentClass = -1;
            }

            // show inherit button only if node has a parent
            if (nodeData.parentUuid === null) {
                UtilUI.disableElement("buttonGetAddressFromParent");
            } else if (nodeData.writePermission) {
                UtilUI.enableElement("buttonGetAddressFromParent");
            }

            // ------------------ Header ------------------
            registry.byId("addressTitle").attr("value", UtilAddress.createAddressTitle(nodeData), true);
            registry.byId("addressType").attr("value", UtilAddress.getAddressType(nodeData.addressClass), true); // call onChange when changing this value!
            registry.byId("addressOwner").attr("value", nodeData.addressOwner, true);

            var workStateStr = message.get("general.workState." + nodeData.workState);
            dom.byId("addressWorkState").innerHTML = (nodeData.isMarkedDeleted ? workStateStr + "<br>(" + message.get("general.state.markedDeleted") + ")" : workStateStr);

            dom.byId("addressCreationTime").innerHTML = nodeData.creationTime;
            dom.byId("addressModificationTime").innerHTML = nodeData.modificationTime;

            if (nodeData.lastEditor !== null && UtilAddress.hasValidTitle(nodeData.lastEditor)) {
                dom.byId("addressLastEditor").innerHTML = UtilAddress.createAddressTitle(nodeData.lastEditor);
            } else {
                dom.byId("addressLastEditor").innerHTML = message.get("general.unknown");
            }

            if (nodeData.writePermission === true) {
                dom.byId("permissionAdrLock").style.display = "none";
            } else {
                dom.byId("permissionAdrLock").style.display = "block";
            }

            // ------------------ Address and Function ------------------
            registry.byId("addressStreet").attr("value", nodeData.street, true);
            registry.byId("addressCountry").attr("value", nodeData.countryCode == -1 ? null : nodeData.countryCode, true);
            registry.byId("addressZipCode").attr("value", nodeData.postalCode, true);
            registry.byId("addressCity").attr("value", nodeData.city, true);
            registry.byId("addressPOBox").attr("value", nodeData.pobox, true);
            registry.byId("addressZipPOBox").attr("value", nodeData.poboxPostalCode, true);
            //DELETED: "addressNotes" (INGRID33-10)
            registry.byId("addressTasks").attr("value", nodeData.task, true);
            UtilStore.updateWriteStore("addressCom", nodeData.communication, false);

            // -- Thesaurus --

            UtilList.addSNSTopicLabels(nodeData.thesaurusTermsTable);
            UtilStore.updateWriteStore("thesaurusTermsAddress", nodeData.thesaurusTermsTable);

            // -- Links --
            var unpubLinkTable = nodeData.linksFromObjectTable;
            var pubLinkTable = nodeData.linksFromPublishedObjectTable;
            array.forEach(pubLinkTable, function(link) {
                link.pubOnly = true;
            });
            // AOR change
            var linkTable = pubLinkTable.concat(unpubLinkTable);

            // Initialize the object address reference table with the links received from the backend
            // TODO Modify to correct value when it's implemented in the backend
            var numReferences = nodeData.totalNumReferences || 0;
            UtilAddress.initObjectAddressReferenceTable(linkTable, numReferences);

            // Comments
            var commentData = UtilList.addDisplayDates(nodeData.commentTable);
            array.forEach(commentData, function(data, i) {
                commentData[i].title = UtilAddress.createAddressTitle(data.user);
            });

            currentUdk.commentStore = commentData;

            var institution = UtilAddress.determineInstitution(nodeData);

            //	var addressFields = ["headerAddressType0Institution", "headerAddressType0Unit", "headerAddressType1Institution", "headerAddressType1Unit",
            var addressFields = ["headerAddressType0Unit", "headerAddressType1Institution", "headerAddressType1Unit",
                "headerAddressType2Institution", "headerAddressType2Lastname", "headerAddressType2Firstname", "headerAddressType2Style",
                "headerAddressType2Title", "headerAddressType3Lastname", "headerAddressType3Firstname", "headerAddressType3Style",
                "headerAddressType3Title", "headerAddressType3Institution"
            ];

            array.forEach(addressFields, function(field) {
                registry.byId(field).attr("value", "", true);
            });

            // ------------------ Class specific content ------------------
            switch (nodeData.addressClass) {
                case 0:
                    //			registry.byId("headerAddressType0Institution").attr("value", institution);
                    registry.byId("headerAddressType0Unit").attr("value", nodeData.organisation, true);
                    // publication info
                    registry.byId("extraInfoPublishAreaAddress0").attr("value", nodeData.extraInfoPublishArea, true);
                    break;
                case 1:
                    registry.byId("headerAddressType1Institution").attr("value", institution, true);
                    registry.byId("headerAddressType1Unit").attr("value", nodeData.organisation, true);
                    // publication info
                    registry.byId("extraInfoPublishAreaAddress1").attr("value", nodeData.extraInfoPublishArea, true);
                    break;
                case 2:
                    registry.byId("headerAddressType2Institution").attr("value", institution, true);
                    registry.byId("headerAddressType2Lastname").attr("value", nodeData.name, true);
                    registry.byId("headerAddressType2Firstname").attr("value", nodeData.givenName, true);
                    registry.byId("headerAddressType2Style").attr("value", nodeData.nameForm, true);
                    registry.byId("headerAddressType2Title").attr("value", nodeData.titleOrFunction, true);
                    registry.byId("headerAddressType2HideAddress").attr("value", nodeData.hideAddress, true);
                    // publication info
                    registry.byId("extraInfoPublishAreaAddress2").attr("value", nodeData.extraInfoPublishArea, true);
                    break;
                case 3:
                    //registry.byId("headerAddressType3Lastname").attr("value", nodeData.name, true);
                    registry.byId("headerAddressType3Lastname").attr("value", nodeData.name, true);
                    registry.byId("headerAddressType3Firstname").attr("value", nodeData.givenName, true);
                    registry.byId("headerAddressType3Style").attr("value", nodeData.nameForm, true);
                    registry.byId("headerAddressType3Title").attr("value", nodeData.titleOrFunction, true);
                    registry.byId("headerAddressType3Institution").attr("value", nodeData.organisation, true);
                    // publication info
                    registry.byId("extraInfoPublishAreaAddress3").attr("value", nodeData.extraInfoPublishArea, true);
                    break;
                default:
                    console.debug("Error in _setAddressData - Address Class must be 0, 1, 2 or 3. Wrong value: " + nodeData.addressClass);
                    break;
            }

        },

        _setObjectData: function(nodeData) {
            /* 
             * 1. Set the data common to all objects which is:
             *   Header, General, Spatial, Time, Extra Info,
             *   Availability, Thesaurus and Links
             *
             * 2. Set the variable information depending on the object class
             *
             */

            // ------------------ Header ------------------

            if (nodeData.objectName === null)
                registry.byId("objectName").attr("value", message.get("tree.newNodeName"), true);
            else
                registry.byId("objectName").attr("value", nodeData.objectName, true);

            // onchange event
            registry.byId("objectClass").attr("value", "Class" + nodeData.objectClass, true);

            var workStateStr = message.get("general.workState." + nodeData.workState);
            dom.byId("workState").innerHTML = (nodeData.isMarkedDeleted ? workStateStr + "<br>(" + message.get("general.state.markedDeleted") + ")" : workStateStr);
            dom.byId("creationTime").innerHTML = nodeData.creationTime;
            dom.byId("modificationTime").innerHTML = nodeData.modificationTime;

            if (nodeData.lastEditor !== null && UtilAddress.hasValidTitle(nodeData.lastEditor)) {
                dom.byId("lastEditor").innerHTML = UtilAddress.createAddressTitle(nodeData.lastEditor);
            } else {
                dom.byId("lastEditor").innerHTML = message.get("general.unknown");
            }

            registry.byId("objectOwner").attr("value", nodeData.objectOwner, true);

            if (nodeData.writePermission === true) {
                style.set("permissionObjLock", "display", "none");
            } else {
                style.set("permissionObjLock", "display", "block");
            }

            // ------------------ Object Content ------------------
            //var formWidget = registry.byId("contentFormObject");
            //  console.debug("ContentFormObject before setting values: " + dojo.json.serialize(formWidget.getValues()));

            // --- General ---
            registry.byId("generalShortDesc").attr("value", nodeData.generalShortDescription, true);
            registry.byId("generalDesc").attr("value", nodeData.generalDescription, true);
            var addressTable = nodeData.generalAddressTable;
            //UtilList.addTableIndices(addressTable);
            UtilList.addIcons(addressTable);
            UtilAddress.addAddressTitles(addressTable);
            UtilList.addAddressLinkLabels(addressTable);
            UtilStore.updateWriteStore("generalAddress", addressTable);

            // Comments
            var commentData = UtilList.addDisplayDates(nodeData.commentTable);
            array.forEach(commentData, function(data, i) {
                commentData[i].title = UtilAddress.createAddressTitle(data.user);
            });
            //commentStore = UtilStore.updateWriteStore(commentStore, commentData, {label:"title"});
            currentUdk.commentStore = commentData;

            // -- Spatial --
            // The table containing entries from the sns is indexed by their topicID
            // The label is a combination of 'name' and 'topicType'
            UtilList.addSNSLocationLabels(nodeData.spatialRefAdminUnitTable);
            UtilList.markExpiredSNSLocations(nodeData.spatialRefAdminUnitTable);
            UtilStore.updateWriteStore("spatialRefAdminUnit", nodeData.spatialRefAdminUnitTable);
            // The table containing free entries needs generated indices
            UtilStore.updateWriteStore("spatialRefLocation", nodeData.spatialRefLocationTable);

            // need to set true here so that rule is applied onChange-event
            registry.byId("spatialRefAltMin").attr("value", nodeData.spatialRefAltMin, true);
            registry.byId("spatialRefAltMax").attr("value", nodeData.spatialRefAltMax, true);
            registry.byId("spatialRefAltMeasure").attr("value", nodeData.spatialRefAltMeasure, true);
            registry.byId("spatialRefAltVDate").attr("value", nodeData.spatialRefAltVDate, true);
            registry.byId("spatialRefExplanation").attr("value", nodeData.spatialRefExplanation, true);

            // -- Time --
            registry.byId("timeRefType").attr("value", nodeData.timeRefType, true);
            if (nodeData.timeRefType == "bis") {
                if (nodeData.timeRefDate2) {
                    registry.byId("timeRefDate1").attr("value", nodeData.timeRefDate2, true);
                } else {
                    registry.byId("timeRefDate1").attr("value", "", true);
                }
                registry.byId("timeRefDate2").attr("value", "", true);

            } else {
                if (nodeData.timeRefDate1) {
                    registry.byId("timeRefDate1").attr("value", nodeData.timeRefDate1, true);
                } else {
                    registry.byId("timeRefDate1").attr("value", "", true);
                }
                if (nodeData.timeRefType == "von" && nodeData.timeRefDate2) {
                    registry.byId("timeRefDate2").attr("value", nodeData.timeRefDate2, true);
                } else {
                    registry.byId("timeRefDate2").attr("value", "", true);
                }
            }

            registry.byId("timeRefStatus").attr("value", nodeData.timeRefStatus, true);
            registry.byId("timeRefPeriodicity").attr("value", nodeData.timeRefPeriodicity, true);
            registry.byId("timeRefIntervalNum").attr("value", nodeData.timeRefIntervalNum, true);
            // TODO Temporarily read the display value from the db till it is changed in the backend
            registry.byId("timeRefIntervalUnit").attr("displayedValue", UtilString.emptyIfNull(nodeData.timeRefIntervalUnit), false); //attr("value", timeRefValue);


            registry.byId("timeRefExplanation").attr("value", nodeData.timeRefExplanation, true);
            UtilStore.updateWriteStore("timeRefTable", nodeData.timeRefTable);

            // -- Extra Info --
            registry.byId("extraInfoLangMetaData").attr("value", nodeData.extraInfoLangMetaDataCode, true);
            registry.byId("extraInfoLangData").attr("value", nodeData.extraInfoLangDataCode, true);
            registry.byId("extraInfoPublishArea").attr("value", nodeData.extraInfoPublishArea, true);
            registry.byId("extraInfoCharSetData").attr("value", nodeData.extraInfoCharSetDataCode, false);
            UtilStore.updateWriteStore("extraInfoConformityTable", nodeData.extraInfoConformityTable);
            registry.byId("extraInfoPurpose").attr("value", nodeData.extraInfoPurpose, true);
            registry.byId("extraInfoUse").attr("value", nodeData.extraInfoUse, true);
            UtilStore.updateWriteStore("extraInfoXMLExportTable", UtilList.listToTableData(nodeData.extraInfoXMLExportTable));
            UtilStore.updateWriteStore("extraInfoLegalBasicsTable", UtilList.listToTableData(nodeData.extraInfoLegalBasicsTable));

            // -- Availability --
            UtilStore.updateWriteStore("availabilityAccessConstraints", UtilList.listToTableData(nodeData.availabilityAccessConstraints));
            registry.byId("availabilityUseConstraints").attr("value", nodeData.availabilityUseConstraints, true);
            registry.byId("availabilityOrderInfo").attr("value", nodeData.availabilityOrderInfo, true);
            UtilStore.updateWriteStore("availabilityDataFormat", nodeData.availabilityDataFormatTable);
            UtilStore.updateWriteStore("availabilityMediaOptions", nodeData.availabilityMediaOptionsTable);


            // -- Thesaurus --
            UtilList.addSNSTopicLabels(nodeData.thesaurusTermsTable);

            UtilStore.updateWriteStore("thesaurusTerms", nodeData.thesaurusTermsTable);
            UtilStore.updateWriteStore("thesaurusTopics", UtilList.listToTableData(nodeData.thesaurusTopicsList));
            UtilStore.updateWriteStore("thesaurusInspire", UtilList.listToTableData(nodeData.thesaurusInspireTermsList));
            UtilStore.updateWriteStore("thesaurusEnvTopics", UtilList.listToTableData(nodeData.thesaurusEnvTopicsList));
            registry.byId("thesaurusEnvExtRes").attr("value", nodeData.thesaurusEnvExtRes, true);


            // -- Links --
            var linkTable = igeEvents._prepareObjectAndUrlReferences(nodeData);

            UtilStore.updateWriteStore("linksTo", linkTable);
            if (nodeData.parentUuid === null) {
                UtilUI.disableHtmlLink("linkGetLinksToFromParent");
                UtilUI.disableHtmlLink("linkGetSpatialRefLocationFromParent");
            } else if (nodeData.writePermission) {
                UtilUI.enableHtmlLink("linkGetLinksToFromParent");
                UtilUI.enableHtmlLink("linkGetSpatialRefLocationFromParent");
            }

            var unpubLinkTable = nodeData.linksFromObjectTable;
            var pubLinkTable = nodeData.linksFromPublishedObjectTable;
            array.forEach(pubLinkTable, function(link) {
                link.pubOnly = true;
            });
            linkTable = pubLinkTable.concat(unpubLinkTable);

            UtilList.addObjectLinkLabels(linkTable, false);
            UtilList.addIcons(linkTable);
            UtilStore.updateWriteStore("linksFrom", linkTable);

            // update data grids that display a sub set of this reference table
            this._connectSharedStore();

            // Additional Fields
            // -- Clear all fields
            if (this.additionalFieldWidgets) {
                array.forEach(this.additionalFieldWidgets, function(currentFieldWidget) {
                    if (currentFieldWidget instanceof CustomGrid) {
                        currentFieldWidget.setData([]);
                        currentFieldWidget.invalidate();
                    } else if (currentFieldWidget instanceof CheckBox) {
                        currentFieldWidget.attr("value", false, true);
                    } else if (currentFieldWidget instanceof DateTextBox) {
                        currentFieldWidget.attr("value", null, true);
                    } else
                        currentFieldWidget.attr("value", "", true);
                });
            }

            // -- Set data
            var additionalFields = nodeData.additionalFields;
            if (additionalFields) {
                for (var index = 0; index < additionalFields.length; ++index) {
                    var currentField = additionalFields[index];
                    var currentFieldWidget = registry.byId(currentField.identifier);
                    if (currentFieldWidget instanceof CustomGrid) {
                        //console.debug("additional field cannot be set: " + currentField.identifier);
                        // it must be a slickGrid
                        var grid = gridManager[currentField.identifier];
                        if (grid === null) {
                            console.debug("additional field cannot be set: " + currentField.identifier);
                            break;
                        }
                        var rowData = [];
                        array.forEach(currentField.tableRows, function(row) {
                            var columnData = {};
                            array.forEach(row, function(col) {
                                if (col.listId == -1)
                                    columnData[col.identifier] = col.value;
                                else
                                    columnData[col.identifier] = col.listId;
                            });
                            rowData.push(columnData);
                        });
                        grid.setData(rowData);
                        grid.render();
                    } else {
                        // if additional field has been deleted then ignore it
                        if (currentFieldWidget) {
                            if (currentFieldWidget.store && currentField.listId != -1) {
                                // distinguish between combo and select box!
                                // combo boxes always cope with values instead of keys
                                if (currentFieldWidget instanceof ComboBox)
                                    currentFieldWidget.attr("value", currentField.value, true);
                                else
                                    currentFieldWidget.attr("value", currentField.listId, true);
                            } else if (currentFieldWidget instanceof DateTextBox) {
                                currentFieldWidget.attr("value", dojo.date.locale.parse(currentField.value, {
                                    datePattern: "dd.MM.yyyy",
                                    selector: "date"
                                }), true);
                            } else if (currentFieldWidget instanceof CheckBox) {
                                currentFieldWidget.attr("value", currentField.value == "true", true);
                            } else
                                currentFieldWidget.attr("value", currentField.value, true);
                        }
                    }
                }
            }

            // Clear all object classes
            this._setObjectDataClass0(nodeData);
            this._setObjectDataClass1(nodeData);
            this._setObjectDataClass2(nodeData);
            this._setObjectDataClass3(nodeData);
            this._setObjectDataClass4(nodeData);
            this._setObjectDataClass5(nodeData);
            this._setObjectDataClass6(nodeData);

        },

        _connectSharedStore: function() {
            var ids = [
                ["ref1KeysLink", "3535"],
                ["ref1DataBasisLink", "3570"],
                ["ref1BasisLink", "3520"],
                ["ref1SymbolsLink", "3555"],
                ["ref1ProcessLink", "3515"],
                ["ref2BaseDataLink", "3345"],
                ["ref3BaseDataLink", "3600"],
                ["ref5MethodLink", "3100"],
                ["ref5KeysLink", "3109"],
                ["ref6BaseDataLink", "3210"]
            ];
            var idsLinkAddresses = [
                ["ref2LocationLink", "3360"],
                ["ref4ParticipantsLink", "3410"],
                ["ref4PMLink", "3400"]
            ];

            // request UNFILTERED data ! Get full data store !
            var linksToTableData = UtilGrid.getTableData("linksTo", true);
            array.forEach(ids, function(id) {
                //UtilGrid.getTableData(id).setItems(linksToStore, "_id");
                UtilGrid.setTableData(id[0], array.filter(linksToTableData, function(item) {
                    return item.relationType == id[1];
                }));
                UtilGrid.getTable(id[0]).invalidate();
            });


            var linksToAddressesTableData = UtilGrid.getTableData("generalAddress");
            array.forEach(idsLinkAddresses, function(id) {
                //UtilGrid.getTableData(id).setItems(linksToAddresses, "_id");
                UtilGrid.setTableData(id[0], array.filter(linksToAddressesTableData, function(item) {
                    return item.typeOfRelation == id[1];
                }));
                UtilGrid.getTable(id[0]).invalidate();
            });

            // display linksFrom-objects in the following tables (bidirectional use!) -> ref1ServiceLink, ref3BaseDataLink
            //var bidirectionalIds = [["ref1ServiceLink", "5066"],  ["ref3BaseDataLink", "3210"]];
            var linksFromTableData = UtilGrid.getTableData("linksFrom");
            var dataFrom = array.filter(linksFromTableData, function(item) {
                return item.objectClass == "3" && item.relationType == "3600";
            });
            UtilGrid.setTableData("ref1ServiceLink", dataFrom);
            UtilGrid.getTable("ref1ServiceLink").invalidate();

            // remove type 3600 from linksTo and linksFrom table
            UtilGrid.setTableData("linksTo", array.filter(linksToTableData, function(item) {
                return item.relationType != "3600";
            }));
            UtilGrid.setTableData("linksFrom", array.filter(linksFromTableData, function(item) {
                return item.relationType != "3600";
            }));
        },

        _setObjectDataClass0: function() {},

        _setObjectDataClass1: function(nodeData) {
            registry.byId("isInspireRelevant").attr("value", nodeData.inspireRelevant, true);
            registry.byId("isOpenData").attr("value", nodeData.openData, false);
            UtilStore.updateWriteStore("categoriesOpenData", UtilList.listToTableData(nodeData.openDataCategories));
            registry.byId("ref1ObjectIdentifier").attr("value", nodeData.ref1ObjectIdentifier, true);
            registry.byId("ref1DataSet").attr("value", nodeData.ref1DataSet, true);
            registry.byId("ref1Coverage").attr("value", nodeData.ref1Coverage, true);
            registry.byId("ref1VFormatTopology").attr("value", nodeData.ref1VFormatTopology, true);

            // The spatial system table is a combobox that allows free entries but also entries associated with IDs
            // If the reference system ID == -1 then we receive a free entry, otherwise we have to resolve the id
            UtilStore.updateWriteStore("ref1SpatialSystem", UtilList.listToTableData(nodeData.ref1SpatialSystemTable));

            registry.byId("ref1AltAccuracy").attr("value", nodeData.ref1AltAccuracy, true);
            registry.byId("ref1PosAccuracy").attr("value", nodeData.ref1PosAccuracy, true);
            registry.byId("ref1BasisText").attr("value", nodeData.ref1BasisText, true);
            registry.byId("ref1DataBasisText").attr("value", nodeData.ref1DataBasisText, true);
            registry.byId("ref1ProcessText").attr("value", nodeData.ref1ProcessText, true);

            UtilStore.updateWriteStore("ref1Representation", UtilList.listToTableData(nodeData.ref1Representation));

            UtilStore.updateWriteStore("ref1Data", UtilList.listToTableData(nodeData.ref1Data));
            UtilStore.updateWriteStore("ref1VFormatDetails", nodeData.ref1VFormatDetails);
            UtilStore.updateWriteStore("ref1Scale", nodeData.ref1Scale);
            UtilStore.updateWriteStore("ref1SymbolsText", nodeData.ref1SymbolsText);
            UtilStore.updateWriteStore("ref1KeysText", nodeData.ref1KeysText);

            var dqUiTableElements = query("#ref1ContentDQTables span:not(.hide) .ui-widget", "contentFrameBodyObject").map(function(item) {
                return item.id;
            });
            array.forEach(dqUiTableElements, function(dqTableId) {
                UtilStore.updateWriteStore(dqTableId, nodeData[dqTableId]);
            });

            registry.byId("availabilityDataFormatInspire").attr("value", nodeData.availabilityDataFormatInspire, true);

        },

        _setObjectDataClass2: function(nodeData) {
            registry.byId("isOpenData").attr("value", nodeData.openData, true);
            UtilStore.updateWriteStore("categoriesOpenData", UtilList.listToTableData(nodeData.openDataCategories));
            registry.byId("ref2Author").attr("value", nodeData.ref2Author, true);
            registry.byId("ref2Publisher").attr("value", nodeData.ref2Publisher, true);
            registry.byId("ref2PublishedIn").attr("value", nodeData.ref2PublishedIn, true);
            registry.byId("ref2PublishLocation").attr("value", nodeData.ref2PublishLocation, true);
            registry.byId("ref2PublishedInIssue").attr("value", nodeData.ref2PublishedInIssue, true);
            registry.byId("ref2PublishedInPages").attr("value", nodeData.ref2PublishedInPages, true);
            registry.byId("ref2PublishedInYear").attr("value", nodeData.ref2PublishedInYear, true);
            registry.byId("ref2PublishedISBN").attr("value", nodeData.ref2PublishedISBN, true);
            registry.byId("ref2PublishedPublisher").attr("value", nodeData.ref2PublishedPublisher, true);
            registry.byId("ref2LocationText").attr("value", nodeData.ref2LocationText, true);
            registry.byId("ref2DocumentType").attr("value", nodeData.ref2DocumentType, true);
            registry.byId("ref2BaseDataText").attr("value", nodeData.ref2BaseDataText, true);
            registry.byId("ref2BibData").attr("value", nodeData.ref2BibData, true);
            registry.byId("ref2Explanation").attr("value", nodeData.ref2Explanation, true);
        },

        _setObjectDataClass3: function(nodeData) {
            registry.byId("isInspireRelevant").attr("value", nodeData.inspireRelevant, true);
            registry.byId("isOpenData").attr("value", nodeData.openData, true);
            UtilStore.updateWriteStore("categoriesOpenData", UtilList.listToTableData(nodeData.openDataCategories));
            registry.byId("ref3ServiceType")._lastValueReported = nodeData.ref3ServiceType + "";
            registry.byId("ref3ServiceType").set("value", nodeData.ref3ServiceType, false);
            registry.byId("ref3IsAtomDownload").attr("value", nodeData.ref3AtomDownload, true);
            // manually call behavior to show atom checkk box or not
            // -> we've got the problem that another event wants to change metadata when service type has changed
            //    which should not happen when we load a dataset (but only when we activly change type!)
            rules._updateAtomFieldVisibility(nodeData.ref3ServiceType);

            registry.byId("ref3CouplingType").attr("value", nodeData.ref3CouplingType, true);
            registry.byId("ref3SystemEnv").attr("value", nodeData.ref3SystemEnv, true);
            registry.byId("ref3History").attr("value", nodeData.ref3History, true);
            registry.byId("ref3BaseDataText").attr("value", nodeData.ref3BaseDataText, true);
            registry.byId("ref3Explanation").attr("value", nodeData.ref3Explanation, true);

            //	console.debug("Setting service version to: "+UtilList.addTableIndices(UtilList.listToTableData(nodeData.ref3ServiceVersion)));

            UtilStore.updateWriteStore("ref3ServiceTypeTable", UtilList.listToTableData(nodeData.ref3ServiceTypeTable));

            UtilStore.updateWriteStore("ref3ServiceVersion", UtilList.listToTableData(nodeData.ref3ServiceVersion));

            UtilStore.updateWriteStore("ref3Scale", nodeData.ref3Scale);

            // Prepare the operation table for display.
            // Add table indices to the main obj and paramList
            // Add table indices and convert to tableData: platform, addressList and dependencies
            if (nodeData.ref3Operation) {
                for (var i = 0; i < nodeData.ref3Operation.length; ++i) {
                    //UtilList.addTableIndices(nodeData.ref3Operation[i].paramList);
                    nodeData.ref3Operation[i].platform = UtilList.listToTableData(nodeData.ref3Operation[i].platform);
                    nodeData.ref3Operation[i].addressList = UtilList.listToTableData(nodeData.ref3Operation[i].addressList);
                    nodeData.ref3Operation[i].dependencies = UtilList.listToTableData(nodeData.ref3Operation[i].dependencies);
                }
            }

            UtilStore.updateWriteStore("ref3Operation", nodeData.ref3Operation);
            registry.byId("ref3HasAccessConstraint").attr("value", nodeData.ref3HasAccessConstraint, true);
        },

        _setObjectDataClass4: function(nodeData) {
            registry.byId("ref4ParticipantsText").attr("value", nodeData.ref4ParticipantsText, true);
            registry.byId("ref4PMText").attr("value", nodeData.ref4PMText, true);
            registry.byId("ref4Explanation").attr("value", nodeData.ref4Explanation, true);
        },

        _setObjectDataClass5: function(nodeData) {
            registry.byId("isOpenData").attr("value", nodeData.openData, true);
            UtilStore.updateWriteStore("categoriesOpenData", UtilList.listToTableData(nodeData.openDataCategories));
            registry.byId("ref5MethodText").attr("value", nodeData.ref5MethodText, true);
            registry.byId("ref5Explanation").attr("value", nodeData.ref5Explanation, true);

            UtilStore.updateWriteStore("ref5KeysText", nodeData.ref5KeysText);
            UtilStore.updateWriteStore("ref5dbContent", nodeData.ref5dbContent);
        },

        _setObjectDataClass6: function(nodeData) {
            registry.byId("isInspireRelevant").attr("value", nodeData.inspireRelevant, true);
            registry.byId("isOpenData").attr("value", nodeData.openData, true);
            UtilStore.updateWriteStore("categoriesOpenData", UtilList.listToTableData(nodeData.openDataCategories));
            registry.byId("ref6ServiceType").attr("value", nodeData.ref6ServiceType, true);
            registry.byId("ref6SystemEnv").attr("value", nodeData.ref6SystemEnv, true);
            registry.byId("ref6History").attr("value", nodeData.ref6History, true);
            registry.byId("ref6BaseDataText").attr("value", nodeData.ref6BaseDataText, true);
            registry.byId("ref6Explanation").attr("value", nodeData.ref6Explanation, true);

            //  dojo.debug("Setting service version to: "+UtilList.addTableIndices(UtilList.listToTableData(nodeData.ref3ServiceVersion)));
            //console.debug("nodeData.ref6ServiceVersion: " + nodeData.ref6ServiceVersion);
            //console.debug("UtilList.listToTableData(nodeData.ref6ServiceVersion): " + UtilList.listToTableData(nodeData.ref6ServiceVersion));
            //console.debug("UtilList.addTableIndices(UtilList.listToTableData(nodeData.ref6ServiceVersion)): " + UtilList.listToTableData(nodeData.ref6ServiceVersion));
            UtilStore.updateWriteStore("ref6ServiceVersion", UtilList.listToTableData(nodeData.ref6ServiceVersion));

            UtilStore.updateWriteStore("ref6UrlList", nodeData.ref6UrlList);
        },


        /*******************************************
         * Methods for sending data to the backend *
         *******************************************/

        _getData: function() {
            var nodeData = {};

            nodeData.nodeAppType = this.global.currentUdk.nodeAppType;

            // -- We check which node needs to get saved --
            switch (nodeData.nodeAppType.toUpperCase()) {
                case "A":
                    this._getAddressData(nodeData);
                    break;
                case "O":
                    this._getObjectData(nodeData);
                    break;
                default:
                    console.debug("Error in _getData - Node Type must be \'A\' or \'O\'!");
                    break;
            }

            return (nodeData);
        },

        _getAddressData: function(nodeData) {

            // ------------- General Static Data -------------
            nodeData.uuid = this.global.currentUdk.uuid;
            nodeData.workState = this.global.currentUdk.workState;
            var parentUuid = this.dataTree.selectedNode.getParent().item.id;
            if (parentUuid != "addressRoot" && parentUuid != "addressFreeRoot") {
                nodeData.parentUuid = parentUuid;
            }
            nodeData.addressOwner = registry.byId("addressOwner").getValue();

            // ------------------ Header ------------------
            nodeData.addressClass = UtilAddress.getAddressClass();

            // ------------------ Address and Function ------------------
            nodeData.street = registry.byId("addressStreet").getValue();
            nodeData.countryCode = registry.byId("addressCountry").getValue();
            nodeData.countryName = registry.byId("addressCountry").get("displayedValue");
            //UtilList.getSelectDisplayValue(registry.byId("addressCountry"), registry.byId("addressCountry").get('value'));//registry.byId("addressCountry").getDisplayValue();
            nodeData.postalCode = registry.byId("addressZipCode").getValue();
            nodeData.city = registry.byId("addressCity").getValue();
            nodeData.pobox = registry.byId("addressPOBox").getValue();
            nodeData.poboxPostalCode = registry.byId("addressZipPOBox").getValue();
            //DELETED: "addressNotes" (INGRID33-10)
            nodeData.task = registry.byId("addressTasks").getValue();
            nodeData.communication = this._getTableData("addressCom");

            // replace syslist entries with name and leave it if it's a free entry
            array.forEach(nodeData.communication, function(entry) {
                entry.medium = UtilSyslist.getSyslistEntryName(4430, entry.medium);
            });

            // -- Thesaurus --
            nodeData.thesaurusTermsTable = this._getTableData("thesaurusTermsAddress");

            // -- Links --
            nodeData.linksFromObjectTable = this._getTableData("associatedObjName");

            // Comments
            //nodeData.commentTable = UtilStore.convertItemsToJS(commentStore, commentStore._arrayOfTopLevelItems);
            nodeData.commentTable = currentUdk.commentStore;


            // ------------------ Class specific content ------------------
            switch (nodeData.addressClass) {
                case 0:
                    //			nodeData.organisation = registry.byId("headerAddressType0Institution").getValue();
                    nodeData.organisation = registry.byId("headerAddressType0Unit").getValue();
                    // -- Extra Info --
                    nodeData.extraInfoPublishArea = registry.byId("extraInfoPublishAreaAddress0").getValue();
                    break;
                case 1:
                    //			registry.byId("headerAddressType1Institution").attr("value", nodeData.organisation);
                    nodeData.organisation = registry.byId("headerAddressType1Unit").getValue();
                    // -- Extra Info --
                    nodeData.extraInfoPublishArea = registry.byId("extraInfoPublishAreaAddress1").getValue();
                    break;
                case 2:
                    nodeData.name = registry.byId("headerAddressType2Lastname").getValue();
                    nodeData.givenName = registry.byId("headerAddressType2Firstname").getValue();
                    nodeData.nameForm = registry.byId("headerAddressType2Style").getValue();
                    nodeData.titleOrFunction = registry.byId("headerAddressType2Title").getValue();
                    nodeData.hideAddress = registry.byId("headerAddressType2HideAddress").checked ? true : false; // in case value is NULL!
                    // -- Extra Info --
                    nodeData.extraInfoPublishArea = registry.byId("extraInfoPublishAreaAddress2").getValue();
                    break;
                case 3:
                    nodeData.name = registry.byId("headerAddressType3Lastname").getValue();
                    nodeData.givenName = registry.byId("headerAddressType3Firstname").getValue();
                    nodeData.nameForm = registry.byId("headerAddressType3Style").getValue();
                    nodeData.titleOrFunction = registry.byId("headerAddressType3Title").getValue();
                    nodeData.organisation = registry.byId("headerAddressType3Institution").getValue();
                    // -- Extra Info --
                    nodeData.extraInfoPublishArea = registry.byId("extraInfoPublishAreaAddress3").getValue();
                    break;
                default:
                    console.debug("Error in _getAddressData - Address Class must be 0, 1, 2 or 3!");
                    break;
            }

            console.debug("------ ADDRESS DATA ------");
            console.debug(nodeData);
            console.debug("------ ADDRESS DATA END ------");
        },

        _getObjectData: function(nodeData) {
            /* 
             * 1. Get the static data that is not displayed in the gui which is:
             *    nodeUuid, orgObjId, id, hasChildren
             *
             * 2. Get the data common to all objects which is:
             *    Header, General, Spatial, Time, Extra Info,
             *    Availability, Thesaurus and Links
             *
             * 3. Get the variable information depending on the object class
             *
             */

            // ------------- General Static Data -------------
            nodeData.uuid = this.global.currentUdk.uuid;
            nodeData.orgObjId = this.global.currentUdk.orgObjId;
            nodeData.hasChildren = this.global.currentUdk.hasChildren; // Do we need to store this?
            nodeData.workState = this.global.currentUdk.workState;
            var parentUuid = this.dataTree.selectedNode.getParent().item.id;
            if (parentUuid != "objectRoot") {
                nodeData.parentUuid = parentUuid;
            }
            nodeData.objectOwner = registry.byId("objectOwner").getValue();

            // ------------------ Header ------------------
            nodeData.objectName = registry.byId("objectName").getValue();

            // ------------------ Object Content ------------------
            // --- General ---
            nodeData.generalShortDescription = registry.byId("generalShortDesc").getValue();
            nodeData.generalDescription = registry.byId("generalDesc").getValue();
            nodeData.objectClass = registry.byId("objectClass").getValue().substr(5, 1); // Value is a string: "Classx" where x is the class
            nodeData.generalAddressTable = this._getTableData("generalAddress");
            // Comments
            nodeData.commentTable = currentUdk.commentStore;

            // -- Spatial --
            nodeData.spatialRefAdminUnitTable = this._getTableData("spatialRefAdminUnit");
            nodeData.spatialRefLocationTable = this._getTableData("spatialRefLocation");

            nodeData.spatialRefAltMin = UtilGeneral.getNumberFromDijit("spatialRefAltMin");
            nodeData.spatialRefAltMax = UtilGeneral.getNumberFromDijit("spatialRefAltMax");
            nodeData.spatialRefAltMeasure = registry.byId("spatialRefAltMeasure").get("value");
            nodeData.spatialRefAltVDate = registry.byId("spatialRefAltVDate").get("value");
            nodeData.spatialRefExplanation = registry.byId("spatialRefExplanation").get("value");

            // -- Time --
            nodeData.timeRefType = registry.byId("timeRefType").getValue();
            var timeFrom = registry.byId("timeRefDate1").getValue();
            var timeTo = registry.byId("timeRefDate2").getValue();

            if (nodeData.timeRefType == "bis") {
                if (timeFrom !== "") {
                    nodeData.timeRefDate1 = null;
                    nodeData.timeRefDate2 = timeFrom;
                }
            } else if (nodeData.timeRefType == "am") {
                if (timeFrom !== "") {
                    nodeData.timeRefDate1 = timeFrom;
                    nodeData.timeRefDate2 = timeFrom;
                }
            } else if (nodeData.timeRefType == "seit") {
                if (timeFrom !== "") {
                    nodeData.timeRefDate1 = timeFrom;
                    nodeData.timeRefDate2 = null;
                }
            } else if (nodeData.timeRefType == "von") {
                if (timeFrom !== "") {
                    nodeData.timeRefDate1 = timeFrom;
                }
                if (timeTo !== "") {
                    nodeData.timeRefDate2 = timeTo;
                }
            }

            nodeData.timeRefStatus = registry.byId("timeRefStatus").getValue();
            nodeData.timeRefPeriodicity = registry.byId("timeRefPeriodicity").getValue();
            nodeData.timeRefIntervalNum = registry.byId("timeRefIntervalNum").get("displayedValue"); // will be mapped to String
            // TODO Temporarily store the display value in the database till it is changed in the backend
            nodeData.timeRefIntervalUnit = registry.byId("timeRefIntervalUnit").get("displayedValue");

            nodeData.timeRefExplanation = registry.byId("timeRefExplanation").getValue();
            nodeData.timeRefTable = this._getTableData("timeRefTable");

            // -- Extra Info --
            nodeData.extraInfoLangMetaDataCode = registry.byId("extraInfoLangMetaData").getValue();
            nodeData.extraInfoLangDataCode = registry.byId("extraInfoLangData").getValue();
            nodeData.extraInfoPublishArea = registry.byId("extraInfoPublishArea").getValue();
            nodeData.extraInfoCharSetDataCode = registry.byId("extraInfoCharSetData").getValue();
            nodeData.extraInfoConformityTable = this._getTableData("extraInfoConformityTable");
            nodeData.extraInfoPurpose = registry.byId("extraInfoPurpose").getValue();
            nodeData.extraInfoUse = registry.byId("extraInfoUse").getValue();

            nodeData.extraInfoXMLExportTable = UtilList.tableDataToList(this._getTableData("extraInfoXMLExportTable"));
            // var valuesExtraInfoLegalBasicsTable =
            //     nodeData.extraInfoLegalBasicsTable = UtilList.tableDataToList(this._getTableData("extraInfoLegalBasicsTable"));

            // -- Availability --
            //  nodeData.availabilityUsageLimitationTable = this._getTableData("availabilityUsageLimitationTable");
            nodeData.availabilityAccessConstraints = UtilList.tableDataToList(this._getTableData("availabilityAccessConstraints"));
            nodeData.availabilityUseConstraints = registry.byId("availabilityUseConstraints").getValue();

            nodeData.availabilityOrderInfo = registry.byId("availabilityOrderInfo").getValue();
            nodeData.availabilityDataFormatTable = this._getTableData("availabilityDataFormat");
            nodeData.availabilityMediaOptionsTable = this._getTableData("availabilityMediaOptions");

            // -- Thesaurus --
            nodeData.thesaurusTermsTable = this._getTableData("thesaurusTerms");
            nodeData.thesaurusTopicsList = UtilList.tableDataToList(this._getTableData("thesaurusTopics"));
            nodeData.thesaurusInspireTermsList = UtilList.tableDataToList(this._getTableData("thesaurusInspire"));
            nodeData.thesaurusEnvTopicsList = UtilList.tableDataToList(this._getTableData("thesaurusEnvTopics"));
            nodeData.thesaurusEnvExtRes = registry.byId("thesaurusEnvExtRes").checked;


            // -- Links --
            var linksToTable = this._getTableData("linksTo");
            // concat with other tables separated earlier
            if (nodeData.objectClass == "3") {
                linksToTable = linksToTable.concat(this._getTableData("ref3BaseDataLink"));
            }

            var objLinks = [];
            var urlLinks = [];
            array.forEach(linksToTable, function(link) {
                if (link.url) {
                    urlLinks.push(link);
                } else {
                    objLinks.push(link);
                }
            });

            // add url to preview image to url table
            var previewUrl = registry.byId("generalPreviewImage").getValue();
            if (previewUrl) urlLinks.push(UtilList.urlToListEntry(previewUrl));

            nodeData.linksToObjectTable = objLinks;
            nodeData.linksToUrlTable = urlLinks;
            nodeData.linksFromObjectTable = this._getTableData("linksFrom");

            // Additional Fields
            nodeData.additionalFields = [];

            if (this.additionalFieldWidgets) {
                for (var nr = 0; nr < this.additionalFieldWidgets.length; nr++) {
                    var currentField = this.additionalFieldWidgets[nr];
                    var identifier = currentField.id;

                    // check if field is a table and handle differently
                    if (currentField instanceof CustomGrid) {
                        // get column ids
                        var tableData = [];
                        var columnIds = [];

                        array.forEach(currentField.getColumns(), function(column) {
                            columnIds.push(column.field);
                        });

                        array.forEach(currentField.getData(), function(row) {
                            var rowData = [];
                            for (var j = 0; j < columnIds.length; j++) {
                                var value = row[columnIds[j]];
                                if (value === undefined || value === null) continue;

                                //if (value instanceof Date)
                                //    value = UtilString.getDateString(value, "dd.MM.yyyy");

                                var listId = "-1";
                                // get listId from structure element of grid in case it is a list
                                if (currentField.getColumns()[j].values) {
                                    var index = array.indexOf(currentField.getColumns()[j].values, value);
                                    if (index == -1) index = array.indexOf(currentField.getColumns()[j].options, value);
                                    if (index != -1) {
                                        listId = currentField.getColumns()[j].values[index];
                                        value = currentField.getColumns()[j].options[index];
                                    }
                                }

                                var columnData = {
                                    identifier: columnIds[j],
                                    value: value,
                                    listId: listId,
                                    tableRows: null
                                };
                                rowData.push(columnData);
                            }
                            tableData.push(rowData);
                        });

                        // add empty rows so that table can be made empty
                        if (tableData.length === 0) {
                            tableData.push([]);
                        }

                        nodeData.additionalFields.push({
                            identifier: currentField.id,
                            value: null,
                            listId: null,
                            tableRows: tableData
                        });
                    } else {
                        // if it's a select box we need to get listId and value
                        var value = null;
                        var listId = null;
                        if (currentField instanceof FilteringSelect ||
                            currentField instanceof ComboBox) {
                            listId = -1;
                            var item = this.additionalFieldWidgets[nr].item;
                            if (item !== null) {
                                listId = item.id[0];
                            }

                            // for lists to get value
                            value = this.additionalFieldWidgets[nr].get("value");

                        } else if (currentField instanceof DateTextBox) {
                            value = currentField.get("value");
                            if (value !== null)
                                value = UtilString.getDateString(currentField.get("value"), "dd.MM.yyyy");

                        } else if (currentField instanceof CheckBox) {
                            var isChecked = currentField.checked;
                            value = isChecked ? "true" : "false";

                        } else {
                            value = currentField.get("displayedValue");
                        }

                        if (value !== null && lang.trim(value + "").length !== 0) {
                            nodeData.additionalFields.push({
                                identifier: identifier,
                                value: value,
                                listId: listId,
                                tableRows: null
                            });
                        }
                    }
                }
            }

            // last modified date
            nodeData.modificationTime = dom.byId("modificationTime").innerHTML;

            // NOTICE: some stuff was moved from class specific domain map ("Fachbezug") to general sections (in GUI).
            // this stuff has to be processed here, before doing class specific stuff !

            // former class 1, now general "Raumbezug"
            // The spatial system table is a combobox that allows free entries but also entries associated with IDs
            // If we have a free entry the reference system ID = -1
            nodeData.ref1SpatialSystemTable = UtilList.tableDataToList(this._getTableData("ref1SpatialSystem"));

            // -- Check which object type was received and fill the appropriate fields --
            switch (nodeData.objectClass) {
                case "0":
                    this._getObjectDataClass0(nodeData);
                    break;
                case "1":
                    this._getObjectDataClass1(nodeData);
                    break;
                case "2":
                    this._getObjectDataClass2(nodeData);
                    break;
                case "3":
                    this._getObjectDataClass3(nodeData);
                    break;
                case "4":
                    this._getObjectDataClass4(nodeData);
                    break;
                case "5":
                    this._getObjectDataClass5(nodeData);
                    break;
                case "6":
                    this._getObjectDataClass6(nodeData);
                    break;
                case "7":
                    //_getObjectDataClass7(nodeData);
                    break;
                default:
                    console.debug("Error in _getObjectData - Object Class must be 0...7!");
                    break;
            }

            console.debug("------ OBJECT DATA ------");
            console.debug(nodeData);
            console.debug("------ OBJECT DATA END ------");
        },

        _getObjectDataClass0: function() {},

        _getObjectDataClass1: function(nodeData) {
            nodeData.inspireRelevant = registry.byId("isInspireRelevant").checked ? true : false; // in case value is NULL!
            nodeData.openData = registry.byId("isOpenData").checked ? true : false; // in case value is NULL!
            nodeData.openDataCategories = UtilList.tableDataToList(this._getTableData("categoriesOpenData"));
            nodeData.ref1ObjectIdentifier = registry.byId("ref1ObjectIdentifier").getValue();
            nodeData.ref1DataSet = registry.byId("ref1DataSet").getValue();
            nodeData.ref1Coverage = UtilGeneral.getNumberFromDijit("ref1Coverage");
            nodeData.ref1VFormatTopology = registry.byId("ref1VFormatTopology").getValue();

            nodeData.ref1AltAccuracy = UtilGeneral.getNumberFromDijit("ref1AltAccuracy");
            nodeData.ref1PosAccuracy = UtilGeneral.getNumberFromDijit("ref1PosAccuracy");
            nodeData.ref1BasisText = registry.byId("ref1BasisText").getValue();
            nodeData.ref1DataBasisText = registry.byId("ref1DataBasisText").getValue();
            nodeData.ref1ProcessText = registry.byId("ref1ProcessText").getValue();


            nodeData.ref1Representation = UtilList.tableDataToList(this._getTableData("ref1Representation"));
            nodeData.ref1Data = UtilList.tableDataToList(this._getTableData("ref1Data"));

            nodeData.ref1VFormatDetails = this._getTableData("ref1VFormatDetails");
            nodeData.ref1Scale = this._getTableData("ref1Scale");
            nodeData.ref1SymbolsText = this._getTableData("ref1SymbolsText");
            nodeData.ref1KeysText = this._getTableData("ref1KeysText");

            var dqUiTableElements = query("#ref1ContentDQTables span:not(.hide) .ui-widget", "contentFrameBodyObject").map(function(item) {
                return item.id;
            });
            array.forEach(dqUiTableElements, function(dqTableId) {
                // only map data of DQ tables shown ! remove other data !
                // -> only visible dqTables are selected!!! 
                //if (!domClass.contains(dqTableId, "hide")) {
                nodeData[dqTableId] = this._getTableData(dqTableId);
                //} else {
                //    UtilGrid.setTableData(dqTableId, []);
                //}
            }, this);

            nodeData.availabilityDataFormatInspire = registry.byId("availabilityDataFormatInspire").get("value");
        },

        _getObjectDataClass2: function(nodeData) {
            nodeData.openData = registry.byId("isOpenData").checked ? true : false; // in case value is NULL!
            nodeData.openDataCategories = UtilList.tableDataToList(this._getTableData("categoriesOpenData"));
            nodeData.ref2Author = registry.byId("ref2Author").getValue();
            nodeData.ref2Publisher = registry.byId("ref2Publisher").getValue();
            nodeData.ref2PublishedIn = registry.byId("ref2PublishedIn").getValue();
            nodeData.ref2PublishLocation = registry.byId("ref2PublishLocation").getValue();
            nodeData.ref2PublishedInIssue = registry.byId("ref2PublishedInIssue").getValue();
            nodeData.ref2PublishedInPages = registry.byId("ref2PublishedInPages").getValue();
            nodeData.ref2PublishedInYear = registry.byId("ref2PublishedInYear").getValue();
            nodeData.ref2PublishedISBN = registry.byId("ref2PublishedISBN").getValue();
            nodeData.ref2PublishedPublisher = registry.byId("ref2PublishedPublisher").getValue();
            nodeData.ref2LocationText = registry.byId("ref2LocationText").getValue();
            nodeData.ref2DocumentType = registry.byId("ref2DocumentType").getValue();
            nodeData.ref2BaseDataText = registry.byId("ref2BaseDataText").getValue();
            nodeData.ref2BibData = registry.byId("ref2BibData").getValue();
            nodeData.ref2Explanation = registry.byId("ref2Explanation").getValue();
        },


        _getObjectDataClass3: function(nodeData) {
            nodeData.inspireRelevant = registry.byId("isInspireRelevant").checked ? true : false; // in case value is NULL!
            nodeData.openData = registry.byId("isOpenData").checked ? true : false; // in case value is NULL!
            nodeData.openDataCategories = UtilList.tableDataToList(this._getTableData("categoriesOpenData"));
            nodeData.ref3ServiceType = registry.byId("ref3ServiceType").getValue();
            nodeData.ref3AtomDownload = registry.byId("ref3IsAtomDownload").checked ? true : false;
            nodeData.ref3CouplingType = registry.byId("ref3CouplingType").getValue();
            nodeData.ref3SystemEnv = registry.byId("ref3SystemEnv").getValue();
            nodeData.ref3History = registry.byId("ref3History").getValue();
            nodeData.ref3BaseDataText = registry.byId("ref3BaseDataText").getValue();
            nodeData.ref3Explanation = registry.byId("ref3Explanation").getValue();

            nodeData.ref3ServiceTypeTable = UtilList.tableDataToList(this._getTableData("ref3ServiceTypeTable"));
            nodeData.ref3ServiceVersion = UtilList.tableDataToList(this._getTableData("ref3ServiceVersion"));

            nodeData.ref3Scale = this._getTableData("ref3Scale");

            // Convert the containing operation tables to lists
            // Add table indices and convert to tableData: platform, addressList and dependencies
            nodeData.ref3Operation = [];
            var op = this._getTableData("ref3Operation");
            if (op) {
                for (var i = 0; i < op.length; ++i) {
                    var operationData = {};
                    operationData.name = op[i].name;
                    operationData.description = op[i].description;
                    //			operationData.operationsCall = op[i].operationsCall;
                    operationData.methodCall = op[i].methodCall;
                    operationData.paramList = dojo.isArray(op[i].paramList) ? op[i].paramList : [op[i].paramList];
                    operationData.platform = UtilList.tableDataToList(op[i].platform);
                    operationData.addressList = UtilList.tableDataToList(op[i].addressList);
                    operationData.dependencies = UtilList.tableDataToList(op[i].dependencies);

                    nodeData.ref3Operation.push(operationData);
                }
            }
            nodeData.ref3Explanation = registry.byId("ref3Explanation").getValue();
            nodeData.ref3HasAccessConstraint = registry.byId("ref3HasAccessConstraint").checked;
        },

        _getObjectDataClass4: function(nodeData) {
            nodeData.ref4ParticipantsText = registry.byId("ref4ParticipantsText").getValue();
            nodeData.ref4PMText = registry.byId("ref4PMText").getValue();
            nodeData.ref4Explanation = registry.byId("ref4Explanation").getValue();
        },

        _getObjectDataClass5: function(nodeData) {
            nodeData.openData = registry.byId("isOpenData").checked ? true : false; // in case value is NULL!
            nodeData.openDataCategories = UtilList.tableDataToList(this._getTableData("categoriesOpenData"));
            nodeData.ref5MethodText = registry.byId("ref5MethodText").getValue();
            nodeData.ref5Explanation = registry.byId("ref5Explanation").getValue();

            nodeData.ref5KeysText = this._getTableData("ref5KeysText");
            nodeData.ref5dbContent = this._getTableData("ref5dbContent");
        },

        _getObjectDataClass6: function(nodeData) {
            nodeData.inspireRelevant = registry.byId("isInspireRelevant").checked ? true : false;
            nodeData.openData = registry.byId("isOpenData").checked ? true : false; // in case value is NULL!
            nodeData.openDataCategories = UtilList.tableDataToList(this._getTableData("categoriesOpenData"));
            nodeData.ref6ServiceType = registry.byId("ref6ServiceType").getValue();
            nodeData.ref6SystemEnv = registry.byId("ref6SystemEnv").getValue();
            nodeData.ref6History = registry.byId("ref6History").getValue();
            nodeData.ref6BaseDataText = registry.byId("ref6BaseDataText").getValue();
            nodeData.ref6Explanation = registry.byId("ref6Explanation").getValue();

            console.debug("ref6ServiceVersion tabledata: " + this._getTableData("ref6ServiceVersion"));
            nodeData.ref6ServiceVersion = UtilList.tableDataToList(this._getTableData("ref6ServiceVersion"));
            console.debug("nodeData.ref6ServiceVersion: " + nodeData.ref6ServiceVersion);

            nodeData.ref6UrlList = this._getTableData("ref6UrlList");
        },



        /*******************************************
         *            Helper functions             *
         *******************************************/

        _initResponsibleUserObjectList: function(nodeData) {
            var def = new Deferred();

            if (nodeData.uuid == "newNode") {
                // var selectWidget = registry.byId("objectOwner");

                var parentUuid = nodeData.parentUuid;
                var self = this;
                if (parentUuid !== null) {
                    // new node && not root
                    SecurityService.getResponsibleUsersForNewObject(parentUuid, false, true, {
                        callback: function(userList) {
                            var list = [];
                            array.forEach(userList, function(user) {
                                var title = UtilAddress.createAddressTitle(user.address);
                                var uuid = user.address.uuid;
                                list.push([title, uuid]);
                            });
                            UtilStore.updateWriteStore("objectOwner", list, {
                                identifier: "1",
                                label: "0",
                                items: list
                            });

                            def.resolve(nodeData);
                        },
                        errorHandler: function(errMsg, err) {
                            console.debug(errMsg);
                            console.debug(err);
                            def.reject(err);
                        }
                    });
                } else {
                    // new root node
                    // get all users from the current users groups that have root permission and the catalog admin
                    var getUsersDef = UtilSecurity.getUsersFromCurrentGroupsWithRootPermission();
                    var getCatAdminDef = UtilSecurity.getCatAdmin();

                    var defList = new DeferredList([getUsersDef, getCatAdminDef], false, false, true);
                    defList.then(function(resultList) {
                        var list = [];

                        // Add all users from the current group
                        for (var i in resultList[0][1]) {
                            // Iterate over the users from the current group
                            var user = resultList[0][1][i];
                            var title = UtilAddress.createAddressTitle(user.address);
                            var uuid = user.address.uuid;
                            list.push([title, uuid]);
                        }

                        // Add the catalog administrator
                        // only if the current user is not the cat admin himself
                        if (UtilSecurity.currentUser.role != 1) {
                            var catAdmin = resultList[1][1];
                            var catAdminTitle = UtilAddress.createAddressTitle(catAdmin.address);
                            var catAdminUuid = catAdmin.address.uuid;
                            list.push([catAdminTitle, catAdminUuid]);
                        }

                        UtilStore.updateWriteStore("objectOwner", list, {
                            identifier: "1",
                            label: "0",
                            items: list
                        });
                        def.resolve(nodeData);
                    }, def.reject);
                }

                return def;
            }


            SecurityService.getUsersWithWritePermissionForObject(nodeData.uuid, false, false, {
                callback: function(userList) {
                    var list = [];
                    array.forEach(userList, function(user) {
                        var title = UtilAddress.createAddressTitle(user.address);
                        var uuid = user.address.uuid;
                        list.push([title, uuid]);
                    });
                    UtilStore.updateWriteStore("objectOwner", list, {
                        identifier: "1",
                        label: "0",
                        items: list
                    });
                    def.resolve(nodeData);
                },
                errorHandler: function(errMsg, err) {
                    console.debug(errMsg);
                    console.debug(err);
                    def.reject(err);
                }
            });

            return def;
        },

        _initResponsibleUserAddressList: function(nodeData) {
            var def = new Deferred();

            if (nodeData.uuid == "newNode") {
                // var selectWidget = registry.byId("addressOwner");

                var parentUuid = nodeData.parentUuid;
                var self = this;
                if (parentUuid !== null) {
                    // new node && not root
                    SecurityService.getResponsibleUsersForNewAddress(parentUuid, false, true, {
                        callback: function(userList) {
                            var list = [];
                            array.forEach(userList, function(user) {
                                var title = UtilAddress.createAddressTitle(user.address);
                                var uuid = user.address.uuid;
                                list.push([title, uuid]);
                            });
                            UtilStore.updateWriteStore("addressOwner", list, {
                                identifier: "1",
                                label: "0"
                            });
                            def.resolve(nodeData);
                        },
                        errorHandler: function(errMsg, err) {
                            console.debug(errMsg);
                            console.debug(err);
                            def.reject(err);
                        }
                    });
                } else {
                    // new root node
                    // get all users from the users groups that have root permission and the catalog admin
                    var getUsersDef = UtilSecurity.getUsersFromCurrentGroupsWithRootPermission();
                    var getCatAdminDef = UtilSecurity.getCatAdmin();

                    var defList = new DeferredList([getUsersDef, getCatAdminDef], false, false, true);
                    defList.then(function(resultList) {
                        var list = [];

                        // Add all users from the current group
                        for (var i in resultList[0][1]) {
                            // Iterate over the users from the current group
                            var user = resultList[0][1][i];
                            var title = UtilAddress.createAddressTitle(user.address);
                            var uuid = user.address.uuid;
                            list.push([title, uuid]);
                        }

                        // Add the catalog administrator
                        // only if the current user is not the cat admin himself
                        if (UtilSecurity.currentUser.role != 1) {
                            var catAdmin = resultList[1][1];
                            var catAdminTitle = UtilAddress.createAddressTitle(catAdmin.address);
                            var catAdminUuid = catAdmin.address.uuid;
                            list.push([catAdminTitle, catAdminUuid]);
                        }

                        UtilStore.updateWriteStore("addressOwner", list, {
                            identifier: "1",
                            label: "0"
                        });
                        def.resolve(nodeData);
                    }, function(errMsg, err) {
                        def.reject(err);
                    });
                }

                return def;
            }



            SecurityService.getUsersWithWritePermissionForAddress(nodeData.uuid, false, false, {
                callback: function(userList) {
                    var list = [];
                    array.forEach(userList, function(user) {
                        var title = UtilAddress.createAddressTitle(user.address);
                        var uuid = user.address.uuid;
                        list.push([title, uuid]);
                    });
                    UtilStore.updateWriteStore("addressOwner", list, {
                        label: "0",
                        identifier: "1"
                    });
                    def.resolve(nodeData);
                },
                errorHandler: function(errMsg, err) {
                    console.debug(errMsg);
                    console.debug(err);
                    def.reject(err);
                }
            });

            return def;
        },

        // Looks for the node widget with uuid = nodeData.uuid and updates the
        // tree data (label, type, etc.) according to the given nodeData
        _updateTree: function(nodeData, oldUuid) {
            console.debug("_updateTree(" + nodeData.uuid + ", " + oldUuid + ")");
            if (typeof(oldUuid) == "undefined" || oldUuid === null) {
                oldUuid = nodeData.uuid;
            }

            var title = "";
            var objClass;
            if (nodeData.nodeAppType == "O") {
                //title = dojo.string.escape("html", nodeData.objectName);
                // FIXME: escape function does not exist anymore
                title = nodeData.objectName;
                objClass = nodeData.objectClass;
            } else if (nodeData.nodeAppType == "A") {
                title = UtilAddress.createAddressTitle(nodeData);
                objClass = nodeData.addressClass;
            }

            // If we change the uuid (= widgetId) of a node the treeNode has to be created again
            // because otherwise dojo doesn't 'register' the changed widgetId 
            // Currently a changed uuid is only possible if a new Node is updated.
            var node,
                nodeItem;
            if (nodeData.uuid != oldUuid && oldUuid == "newNode") {
                var oldWidget = UtilTree.getNodeById("dataTree", oldUuid);
                var parent = oldWidget.getParent();

                UtilTree.deleteNode("dataTree", oldUuid);

                var newItem = {
                    isFolder: false,
                    isPublished: nodeData.isPublished,
                    nodeDocType: nodeData.nodeDocType,
                    publicationCondition: nodeData.extraInfoPublishArea,
                    title: title,
                    objectClass: objClass,
                    nodeAppType: nodeData.nodeAppType,
                    userWritePermission: nodeData.writePermission,
                    userMovePermission: nodeData.movePermission,
                    userWriteSinglePermission: nodeData.writeSinglePermission,
                    userWriteTreePermission: nodeData.writeTreePermission,
                    userWriteSubNodePermission: nodeData.writeSubNodePermission,
                    userWriteSubTreePermission: nodeData.writeSubTreePermission,
                    id: nodeData.uuid
                };

                UtilTree.addNode("dataTree", parent, newItem)
                .then(function() {
                    UtilTree.selectNode("dataTree", nodeData.uuid);
                });

            } else {
                //alert("Get widget: " + oldUuid);
                // find the node ... check all children of tree for item.id == oldUuid!
                console.debug("oldUuid: " + oldUuid);
                //                if (registry.byId(oldUuid)) {
                //                    node = registry.byId(oldUuid).item;
                //                }
                node = registry.byId("dataTree").selectedNode;
                if (node) {
                    nodeItem = node.item;
                    nodeItem.nodeDocType = nodeData.nodeDocType;
                    nodeItem.publicationCondition = nodeData.extraInfoPublishArea;
                    //registry.byId("treeDocIcons").setnodeDocTypeClass(node);
                    // this call updates the tree, so provide newest data in node before !
                    //registry.byId("dataTree").model.store.setValue(node, "title", title);
                    //query("#" + node.id + " .dijitTreeLabel")[0].innerHTML = title;
                    nodeItem.title = title;
                    nodeItem.objectClass = objClass;
                    nodeItem.id = nodeData.uuid;
                    // update permissions
                    nodeItem.isPublished = nodeData.isPublished;
                    nodeItem.workState = nodeData.workState;
                    nodeItem.userWritePermission = nodeData.writePermission;
                    nodeItem.userMovePermission = nodeData.movePermission;
                    nodeItem.userWriteSinglePermission = nodeData.writeSinglePermission;
                    nodeItem.userWriteTreePermission = nodeData.writeTreePermission;
                    nodeItem.userWriteSubNodePermission = nodeData.writeSubNodePermission;
                    nodeItem.userWriteSubTreePermission = nodeData.writeSubTreePermission;
                    UtilTree.updateNode("dataTree", null, nodeItem);
                } else {
                    console.debug("Error in _updateTree: TreeNode widget not found. ID: " + nodeData.uuid);
                }
            }
            if ( /*doSelectNode && */ nodeItem) {
                topic.publish("/selectNode", {
                    id: "dataTree",
                    node: nodeItem
                });
            }
        },

        // Returns an array representing the UNFILTERED data of the table with name 'tableName'.
        // ONLY CALLED FOR SAVING DATA !!!
        // The keys are stored in the fields named: 'Id' 
        _getTableData: function(tableName) {
            var widget = registry.byId(tableName);
            // if it's a slickgrid
            if (!widget) {
                // request UNFILTERED data ! Get full data store !
                return gridManager[tableName].getData(true);
            } else {
                //var store = widget.store;
                //return UtilStore.convertItemsToJS(store, store._arrayOfTopLevelItems);
                return widget.getData(true);
            }
            //return registry.byId(tableName).store._arrayOfTopLevelItems;
        }


    })();
});