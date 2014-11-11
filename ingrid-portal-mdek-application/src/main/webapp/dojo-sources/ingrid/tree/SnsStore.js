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
    "dojo/dom-style",
    "dojo/store/util/QueryResults",
    "dojo/store/Memory",
    "dijit/registry",
    "ingrid/utils/List",
    "ingrid/utils/Address",
    "ingrid/utils/String"
], function(Deferred, declare, array, style, QueryResults /*=====, Store =====*/ , Memory) {

    return declare("ingrid.store.SnsStore", Memory, {
        // summary:
        //		This is a basic store for RESTful communicating with a server through JSON
        //		formatted data. It implements dojo/store/api/Store.

        serviceType: null,

        service: SNSService,
        
        rootUrl: null,

        // rootPromise: null,

        constructor: function() {
            this.idProperty = "topicId";
        },

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

            // if there is no parent then we need to get a virtual root node
            if (query.parent === null) {
                return QueryResults([{topicId: "THESAURUS-ROOT", title: "THESAURUS-ROOT"}]);
            }

            var queryDef = this._loadSNSData(query)
                .then(function(children) {
                    // UtilList.addSNSTopicLabels(child);
                    array.forEach(children, function(child) {
                        child.parent = query.parent;
                        // delete child.children;
                        // mem.data.push( child );//, options );                        
                    });
                    return children;
                });
        
            return QueryResults(queryDef);
        },

        /**
         * THESAURUS - FUNCTIONS
         */

        _loadSNSData: function(query) {
                var _this = this;
                var deferred = new Deferred();
                // if it's the initialization
                if (query.parent == "THESAURUS-ROOT") {
                    var serviceCallbacks = {
                        preHook: _this.showLoadingZone,
                        postHook: _this.hideLoadingZone,
                        callback: function(res) {
                            _this._handleRootTopics(res);
                            deferred.resolve(res);
                            // if (!_this.treeWidget) return;
                            // _this.showStatus("");
                            // callback_function();
                        },
                        errorHandler: function(msg) {
                            deferred.reject(msg);
                            _this.showStatus("<fmt:message key='sns.connectionError' />");
                        },
                        exceptionHandler: function(msg) {
                            deferred.reject(msg);
                            _this.showStatus("<fmt:message key='sns.connectionError' />");
                        }
                    };
                    if (this.rootUrl)
                        this.service.getRootTopics(this.rootUrl, userLocale, serviceCallbacks);
                    else
                        this.service.getRootTopics(userLocale, serviceCallbacks);
                } else {
                    this.service.getSubTopics(this.rootUrl, query.parent, 2, 'down', userLocale, {
                        preHook: _this.showLoadingZone,
                        postHook: _this.hideLoadingZone,
                        callback: function(res) {
                            //_this.showStatus("");
                            _this._handleSubTopics(res);
                            deferred.resolve(res);                            
                        },
                        errorHandler: function(msg) {
                            console.debug(msg);
                            _this.showStatus("<fmt:message key='sns.connectionError' />");
                            deferred.reject(msg);
                        }
                    });
                }
                return deferred;
            },

            // Callback Handler for the root topics.
            // This function gets called when the tree is initialized.
            // @param topicList - List of Topics from the SNSService   
            //
            // The function adds the topics in 'topicList' as children to the tree 
            _handleRootTopics: function(topicList) {
                array.forEach(topicList, function (topic) {
                    topic.isFolder = true;
                    topic.nodeDocType = topic.type;
                    // Top Terms are not selectable. Add the proper class to make them grey
                    topic.labelClass = "TreeNodeNotSelectable";
                    // topic.uniqueId = topic.topicId;
                });
            },

            _handleSubTopics: function(topicList) {
                array.forEach(topicList, function (topic) {
                    topic.isFolder = (topic.children.length > 0);
                    // topic.label = topic.title;
                    // topic.nodeDocType = topic.type;
                    // topic.children = [];

                    // node id must be unique, but topicId can occur in different branches of the tree
                    // add parent id to the id
                    // topic.uniqueId = node.item.topicId + "." + topic.topicId;

                    // make node not selectable if not Descriptor
                    if (topic.nodeDocType == "NODE_LABEL" || topic.nodeDocType == "NON_DESCRIPTOR") {
                        topic.labelClass = "TreeNodeNotSelectable";
                    }
                    // topic.title = topic.label;
                });
            },

            // Expands the tree to the topic with id 'topicID'
            // A valid topicID can be acquired by calling SNSService.findTopic()
            expandToTopicWithId: function(topicID) {
                var def = new Deferred();
                var treePane = this.treeWidget;
                var _this = this;
                this.service.getSubTopicsWithRoot(this.rootUrl, topicID, 0, 'up', userLocale, {
                    preHook: function() {
                        _this.showLoadingZone();
                    },
                    postHook: function() {
                        _this.hideLoadingZone();
                    },
                    callback: function(res) {
                        _this.showStatus("");
                        var topTerm = _this._getTopTermNode(res[0]);
                        console.debug("expandPath");
                        _this._expandPath(treePane.rootNode, topTerm, res[0], def);
                    },
                    timeout: 0,
                    errorHandler: function(msg) {
                        _this.showStatus("<fmt:message key='sns.connectionError' />");
                    },
                    exceptionHandler: function(msg) {
                        _this.showStatus("<fmt:message key='sns.connectionError' />");
                    }
                });

                return def;
            },


            // Walks through a 'SNSTopic' structure (from SNSService.getSubTopics()) and returns a TOP_TERM
            _getTopTermNode: function(node) {
                if (node.type == 'TOP_TERM') {
                    return node;
                } else {
                    if (node.children != null && node.children.length != 0) {
                        return this._getTopTermNode(node.children[0]);
                    } else {
                        dojo.debug('Error in getTopTermNode: No parent of type TOP_TERM found.');
                        return node;
                    }
                }
            },

            showLoadingZone: function() {
                if (this.loadingDivId) style.set(this.loadingDivId, "visibility", "visible");
            },

            hideLoadingZone: function() {
                if (this.loadingDivId) style.set(this.loadingDivId, "visibility", "hidden");
            }

    });

});