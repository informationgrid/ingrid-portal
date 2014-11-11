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
    "dojo/Deferred",
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/store/util/QueryResults",
    "dojo/store/Memory",
    "ingrid/utils/Security",
    "ingrid/utils/Address",
    "ingrid/utils/String"
], function(Deferred, declare, array, QueryResults, Memory, UtilSecurity, UtilAddress, UtilString) {

    return declare("ingrid.store.DwrStore", Memory, {
        // summary:
        //		This is a basic store for RESTful communicating with a server through JSON
        //		formatted data. It implements dojo/store/api/Store.

        storeType: "ObjectsAndAddresses",

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
            options = options || {};

            var mem = this;

            // if there is no parent then we need to get a virtual root node
            if (query.parent === null) {
                return QueryResults([this._getRootNode()]);
            }

            var queryDef = new Deferred();

            if (this.storeType === "Users") {
                // if root node has not been initialized yet
                if (this.rootPromise) {
                    this.rootPromise.then(function(rootNode) {
                        mem.rootPromise = null;
                        queryDef.resolve([rootNode]);
                    });
                } else {
                    var parentId = query.parent.substring(9);
                    queryDef = this.getSubUsers(parentId)
                        .then(function(children) {
                            array.forEach(children, function(child) {
                                child.parent = parentId;
                                // mem.data.push( child );//, options );                                
                            });
                            return children;
                        });
                }

            } else {
                queryDef = this.getSubTree(query)
                    .then(function(children) {
                        array.forEach(children, function(child) {
                            child.parent = query.parent;
                            // mem.data.push( child );//, options );                        
                        });
                        return children;
                    }, displayErrorMessage);
            }
            return QueryResults(queryDef);
        },

        /**
         * OBJECT / ADDRESS - FUNCTIONS
         */

        getSubTree: function(item) {
            var deferred = new Deferred();

            // if node is not root get the object and address node
            var isRoot = item.parent == "ObjectsAndAddressesRoot";
            var id = isRoot ? null : item.parent;
            var type = item.nodeAppType;

            TreeService.getSubTree(id, type, userLocale, {
                callback: function(res) {
                    deferred.resolve(res);
                },
                errorHandler: function(message) {
                    deferred.reject(message);
                }
            });
            return deferred.promise;
        },


        /**
         * USER - FUNCTIONS
         */

        getSubUsers: function(parentId) {
            var self = this;
            var def = new Deferred();
            SecurityService.getSubUsers(parentId, {
                callback: function(res) {
                    var userNodes = self._convertUserListToTreeNodes(res);
                    def.resolve(userNodes);
                }
            });
            return def.promise;
        },

        _convertUserListToTreeNodes: function(userList) {
            var treeNodes = [];
            var self = this;

            array.forEach(userList, function(user) {
                treeNodes.push(self._convertUserToTreeNode(user));
            });

            // Return a sorted list according to the users titles
            return treeNodes.sort(function(a, b) {
                return UtilString.compareIgnoreCase(a.title, b.title);
            });
        },

        _convertUserToTreeNode: function(user) {
            var portalLogin = "?";
            var notExists = true;
            // var self = this;
            if (user.userData !== null) {
                portalLogin = user.userData.portalLogin;
                notExists = false;
            }

            return {
                userId: user.id,
                parentUserId: user.parentUserId,
                addressUuid: user.addressUuid,
                title: UtilAddress.createAddressTitle(user.address),
                role: user.role,
                roleName: user.roleName,
                groupIds: user.groupIds,
                portalLogin: portalLogin,
                nodeDocType: UtilSecurity.getDocTypeForRole(user.role),
                isFolder: user.hasChildren,
                id: "TreeNode_" + user.id,
                noPortalLogin: notExists
            };
        },

        _getRootNode: function() {
            if (this.storeType === "ObjectsAndAddresses")
                return {
                    id: "ObjectsAndAddressesRoot",
                    title: "ROOT",
                    isRoot: true
                };
            else if (this.storeType === "Objects")
                return {
                    id: "objectRoot",
                    title: "Objects",
                    nodeAppType: "O"
                };
            else if (this.storeType === "Addresses")
                return {
                    id: "addressRoot",
                    title: "Addresses",
                    nodeAppType: "A"
                };
            else if (this.storeType === "Users") {
                var self = this;
                var def = new Deferred();
                UtilSecurity.getCatAdmin().then(function(user) {
                    def.resolve(self._convertUserToTreeNode(user));
                });
                self.rootPromise = def.promise;

                return {
                    id: "UsersRoot",
                    title: "Users"
                };
            }

        }
    });

});