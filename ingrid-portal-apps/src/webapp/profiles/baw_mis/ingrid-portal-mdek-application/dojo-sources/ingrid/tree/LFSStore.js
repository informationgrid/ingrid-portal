/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
    "dojo/Deferred",
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/store/util/QueryResults",
    "dojo/store/Memory",
    "ingrid/utils/Security",
    "ingrid/utils/Address",
    "ingrid/utils/String"
], function(Deferred, declare, array, QueryResults, Memory, UtilSecurity, UtilAddress, UtilString) {

    return declare("ingrid.store.LFSStore", Memory, {
        // summary:
        //		This is a basic store for RESTful communicating with a server through JSON
        //		formatted data. It implements dojo/store/api/Store.

        storeType: "receipt",

        rootPromise: null,

        query: function(query, options) {
            // summary:
            //		Queries the store for objects. This will trigger a GET request to the server, with the
            //		query added as a query string.
            // query: Object
            //		The query to use for retrieving objects from the store.
            // options: __QueryOptions?
            //		The optional arguments to apply to the resultset.
            // returns: dojo/store/api/Store.QueryResults
            //		The results of the query, extended with iterative methods.

            // if there is no parent then we need to get a virtual root node
            if (query.parent === null) {
                return QueryResults([this._getRootNodes()]);
            }

            if (query.parent === "lfsReceiptRoot" || query.parent === "lfsFilingRoot") {
                return QueryResults(this._getSubRootNodes());
            }

            var queryDef = new Deferred();

            queryDef = this.getSubTree(query)
                .then(function(children) {
                    array.forEach(children, function(child) {
                        child.parent = query.parent;
                    });
                    return children;
                }, displayErrorMessage);
            return QueryResults(queryDef);
        },

        /**
         * OBJECT / ADDRESS - FUNCTIONS
         */

        getSubTree: function(item) {
            var deferred = new Deferred();

            LFSService.getSubTree(item.path, {
                callback: function(/*Array*/res) {
                    for (var i = 0; i < res.length; i++) {
                        res[i].id = res[i].name;

                        var path = item.path ? item.path + res[i].id : res[i].id;
                        path += res[i].type === "container" ? "/" : "";
                        res[i].path = path;
                    }
                    deferred.resolve(res);
                },
                errorHandler: function(message) {
                    deferred.reject(message);
                }
            });
            return deferred.promise;
        },

        _getRootNodes: function() {
            if (this.storeType === "receipt")
                return {
                    id: "lfsReceiptRoot",
                    name: "LFS-Eingang",
                    type: "container",
                    path: "",
                    isRoot: true
                };
            else if (this.storeType === "filing")
                return {
                    id: "lfsFilingRoot",
                    name: "LFS-Ablage",
                    path: "",
                    isRoot: true
                };
            else
                throw "Unknown storeType: " + this.storeType

        },

        _getSubRootNodes: function () {
            if (this.storeType === "receipt")
                return [{
                    id: "kaReceipt",
                    name: "KA",
                    type: "container",
                    path: "KA/Eingang/KA/",
                    isRoot: true
                }, {
                    id: "hhReceipt",
                    name: "HH",
                    type: "container",
                    path: "HH/Eingang/HH/"
                }];
            else if (this.storeType === "filing") {
                return [{
                    id: "kaFiling",
                    name: "KA",
                    type: "container",
                    path: "KA/Ablage/KA/",
                    isRoot: true
                }, {
                    id: "hhFiling",
                    name: "HH",
                    type: "container",
                    path: "HH/Ablage/HH/"
                }];
            }
        }
    });

});
