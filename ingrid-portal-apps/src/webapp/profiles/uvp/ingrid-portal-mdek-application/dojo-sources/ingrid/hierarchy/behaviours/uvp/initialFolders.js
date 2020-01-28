/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
define(["dojo/_base/declare",
    "dojo/Deferred",
    "ingrid/dialog",
    "ingrid/message",
    "ingrid/utils/Catalog"
], function(declare, Deferred, dialog, message, Catalog) {

    return declare(null, {
        title: "UVP: Initiale Ordner",
        description: "Anlegen der initialen Ordner, falls diese noch nicht bereits vorhanden sind.",
        defaultActive: true,
        category: "UVP",
        type: "SYSTEM", // execute on IGE page load
        initFlagName: "uvp_initialized",
        run: function() {
            var self = this;
            this.getInitFlag().then(function(isInitialized) {
                console.log("init flag: ", isInitialized);

                // if database has not been initialized for UVP yet, then do it now
                // and create root folders
                if (!isInitialized) {
                    self.createFolders()
                        .then( self.createNegativeUvp );
                    self.storeInitFlag();

                // if it was initialized before but an update is necessary, like support
                // for negative UVP
                } else if (isInitialized === "1") {
                    console.log("UVP: add new category 'negative UVP'")
                    self.createNegativeUvp();
                    self.storeInitFlag();
                }

            });
        },

        createFolders: function() {
            var def = new Deferred();
            var def2 = new Deferred();
            var def3 = new Deferred();
            var self = this;

            ObjectService.createNewNode(null, function(objNode) {
                objNode.nodeAppType = "O";
                objNode.objectClass = "1000";
                objNode.objectName = message.get("uvp.form.categories.uvp");
                ObjectService.saveNodeData(objNode, "true", false, {
                    callback: def.resolve,
                    errorHandler: self.handleCreateError
                });
            });
            ObjectService.createNewNode(null, function(objNode) {
                objNode.nodeAppType = "O";
                objNode.objectClass = "1000";
                objNode.objectName = message.get("uvp.form.categories.uvpInFront");
                def.then(function() {
                    ObjectService.saveNodeData(objNode, "true", false, {
                        callback: def2.resolve,
                        errorHandler: self.handleCreateError
                    });
                });
            });
            ObjectService.createNewNode(null, function(objNode) {
                objNode.nodeAppType = "O";
                objNode.objectClass = "1000";
                objNode.objectName = message.get("uvp.form.categories.uvpForeign");
                def2.then(function() {
                    ObjectService.saveNodeData(objNode, "true", false, {
                        callback: def3.resolve,
                        errorHandler: self.handleCreateError
                    });
                });
            });

            return def3;
        },

        createNegativeUvp: function() {
            var def = new Deferred();
            var self = this;
            ObjectService.createNewNode(null, {
                callback: function(objNode) {
                    objNode.nodeAppType = "O";
                    objNode.objectClass = "1000";
                    objNode.objectName = message.get("uvp.form.categories.uvpNegative");
                    ObjectService.saveNodeData(objNode, "true", false, {
                        callback: def.resolve,
                        errorHandler: self.handleCreateError
                    });
                },
                errorHandler: self.handleCreateError
            });
            return def;
        },

        handleCreateError: function(error) {
            console.error("Error during initial folder creation for UVP:", error);
            dialog.show(message.get("general.error"), message.get("uvp.error.init"), dialog.WARNING, null, null, null, error ? error.stack : null);
        },

        getInitFlag: function() {
            var self = this;
            return Catalog.getGenericValuesDef([this.initFlagName])
                .then(function(values) { return values[self.initFlagName]; });
        },

        storeInitFlag: function() {
            var obj = {};
            obj[this.initFlagName] = 2;
            Catalog.storeGenericValuesDef(obj);
        }

    })();
});
