define([
    "dojo/_base/declare",
    "ingrid/message",
    "dojo/Deferred",
    "dojo/promise/all",
    "ingrid/utils/LoadingZone",
    "ingrid/utils/UI"
], function(declare, message, Deferred, all, LoadingZone, UtilUI) {
    return declare(null, {

        // This object holds the current user permissions (See de.ingrid.mdek.beans.security.Permission for content)
        currentUserPermissions: [],

        // This object holds the groups of the current user (See de.ingrid.mdek.beans.security.Group for content)
        currentGroups: [],

        // This object holds the current user (See de.ingrid.mdek.beans.security.User for content)
        currentUser: {},

        // Retrieve the role name for a given role id
        getRoleName: function(roleId) {
            switch (roleId) {
                case 1:
                    return message.get("security.role.catalogAdmin");
                case 2:
                    return message.get("security.role.metadataAdmin");
                case 3:
                    return message.get("security.role.metadataAuthor");
                default:
                    return null;
            }
        },

        canCreateRootNodes: function() {
            if (this.currentUserPermissions === null || typeof(this.currentUserPermissions) == "undefined") {
                return false;
            }

            for (var i = 0; i < this.currentUserPermissions.length; ++i) {
                if (this.currentUserPermissions[i].permission == "CREATE_ROOT") {
                    return true;
                }
            }
            return false;
        },

        isCurrentUserQA: function() {
            if (this.currentUserPermissions === null || typeof(this.currentUserPermissions) == "undefined") {
                return false;
            }

            for (var i = 0; i < this.currentUserPermissions.length; ++i) {
                if (this.currentUserPermissions[i].permission == "QUALITY_ASSURANCE") {
                    return true;
                }
            }
            return false;
        },

        getUsersFromCurrentGroupsWithRootPermission: function() {
            var def = new Deferred();
            var getUsersFromGroupDefList = [];
            for (var i = 0; i < this.currentGroups.length; i++) {
                var hasRootPermission = false;
                for (var j = 0; j < this.currentGroups[i].groupPermissions.length; j++) {
                    if (this.currentGroups[i].groupPermissions[j] == "CREATE_ROOT") {
                        hasRootPermission = true;
                        break;
                    }
                }
                if (hasRootPermission) {
                    getUsersFromGroupDefList.push(this.getUsersFromGroup(this.currentGroups[i].name));
                }
            }
            UtilUI.enterLoadingState();
            all(getUsersFromGroupDefList)
            .then(function(resultList) {
                UtilUI.exitLoadingState();
                var userResultList = [];
                for (var i = 0; i < resultList.length; ++i) {
                    for (var j = 0; j < resultList[i].length; j++) {
                        userResultList.push(resultList[i][j]);
                    }
                }
                def.resolve(userResultList);
            }, function(errMsg, err) {
                UtilUI.exitLoadingState();
                console.debug("Error while calling getUserOfGroup: " + errMsg);
                def.reject(err);
            });

            return def;
        },

        getUsersFromGroup: function(groupName) {
            var def = new Deferred();

            SecurityService.getUsersOfGroup(groupName, {
                callback: function(userList) {
                    def.resolve(userList);
                },
                errback: function(errMsg, err) {
                    console.debug("Error while calling getUserOfGroup: " + errMsg);
                    def.reject(err);
                }
            });
            return def;
        },

        getCatAdmin: function() {
            var def = new Deferred();

            SecurityService.getCatalogAdmin({
                callback: function(userList) {
                    def.resolve(userList);
                },
                errback: function(errMsg, err) {
                    def.reject(err);
                }
            });
            return def;
        },

        getAllGroups: function(includeCatAdminGroup) {
            var deferred = new Deferred();

            SecurityService.getGroups(includeCatAdminGroup, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback: function(groupList) {
                    deferred.resolve(groupList);
                },
                errorHandler: function(errMsg, err) {
                    //hideLoadingZone();
                    displayErrorMessage(err);
                    console.debug(errMsg);
                    deferred.reject(err);
                }
            });

            return deferred;
        },

        getDocTypeForRole: function(roleId) {
            switch (roleId) {
                case 1:
                    return "KatalogAdmin";
                case 2:
                    return "MDAdmin";
                case 3:
                    return "MDAutor";
                default:
                    return null;
            }
        }
    })();
});