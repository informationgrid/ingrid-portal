/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    "ingrid/message",
    "ingrid/utils/Catalog"
], function(declare, Deferred, message, Catalog) {

    return declare(null, {
        title: "UVP: Initiale Ordner",
        description: "Anlegen der initialen Ordner, falls diese noch nicht bereits vorhanden sind.",
        defaultActive: true,
        type: "SYSTEM",
        initFlagName: "uvp_initialized",
        run: function() {
            var self = this;
            this.getInitFlag().then(function(isInitialized) {
                console.log("init flag: ", isInitialized);
                if (!isInitialized) {
                    self.createFolders();
                    self.storeInitFlag();
                }

            });
        },

        createFolders: function() {
            var def = new Deferred();
            var def2 = new Deferred();
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
            // ObjectService.createNewNode(null, function(objNode) {
            //     objNode.nodeAppType = "O";
            //     objNode.objectClass = "1000";
            //     objNode.objectName = message.get("uvp.form.categories.uvpNegative");
            //     def.then(function() {
            //         ObjectService.saveNodeData(objNode, "true", false, {
            //             callback: def2.resolve,
            //             errorHandler: self.handleCreateError
            //         });
            //     });
            // });
            ObjectService.createNewNode(null, function(objNode) {
                objNode.nodeAppType = "O";
                objNode.objectClass = "1000";
                objNode.objectName = message.get("uvp.form.categories.uvpForeign");
                def2.then(function() {
                    ObjectService.saveNodeData(objNode, "true", false, {
                        errorHandler: self.handleCreateError
                    });
                });
            });
        },

        handleCreateError: function(error) {
            console.error("Error during initial folder creation for UVP:", error);
        },

        getInitFlag: function() {
            var self = this;
            return Catalog.getGenericValuesDef([this.initFlagName])
                .then(function(values) { return values[self.initFlagName]; });
        },

        storeInitFlag: function() {
            var obj = {};
            obj[this.initFlagName] = 1;
            Catalog.storeGenericValuesDef(obj);
        }

    })();
});