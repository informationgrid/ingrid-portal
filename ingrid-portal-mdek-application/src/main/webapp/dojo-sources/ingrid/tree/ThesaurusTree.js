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
define("ingrid/tree/ThesaurusTree", [
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/window",
    "dojo/Deferred",
    "dijit/Tree",
    "ingrid/tree/SnsStore",
    "ingrid/utils/Tree",
    "dojo/store/Observable",
    "dijit/tree/ObjectStoreModel"
], function(declare, array, lang, wnd, Deferred, Tree, SnsStore, UtilTree, Observable, ObjectStoreModel) {

    var ThesaurusTree = declare( "ingrid.tree.ThesaurusTree", [ Tree ], {

        postMixInProperties: function() {
            var memoryStore = new SnsStore ( {
                data: [],
                serviceType: this.service,
                rootUrl: this.rootUrl,
                getChildren: function(object){
                    // Add a getChildren() method to store for the data model where
                    // children objects point to their parent (aka relational model)
                    return this.query({parent: object.topicId});
                }
            } );

            var observableStore = new Observable( memoryStore );

            this.model = new ObjectStoreModel( {
                store: observableStore,
                // rootId: "root_"+id,
                deferItemLoadingUntilExpand: true,
                childrenAttrs: [ "children" ],
                labelAttr: "title",
                query: { parent: null },
                mayHaveChildren: function(item) {
                    return item.isFolder;
                }
            } );

            this.inherited( arguments );
        },

        
        refreshChildren: function(/*TreeNode*/node) {
            // reinitialize for refresh
            node._expandDeferred = undefined;
            node._loadDeferred = undefined;
            
            // remove node from cache so that it is requested from the server again
            for(var id in this.model.childrenCache){
                delete this.model.childrenCache[id];
            }
            
            // expand node and fetch children
            var self = this;
            
            return node.setChildItems([])
            .then(function() { return self.model.store.getChildren(node.item); })
            .then(lang.hitch(node, node.setChildItems));
            //.then(function() { return self._expandNode(node); });
            
        },
     

        getService: function() {
            return this.model.store.service;
        },

        // Expands the tree to the topic with id 'topicID'
        // A valid topicID can be acquired by calling SNSService.findTopic()
        expandToTopicWithId: function(topicId) {
            var def = new Deferred();
            // var treePane = this.treeWidget;
            var _this = this;
            var service = this.model.store.service;
            service.getSubTopicsWithRoot(topicId, 0, 'up', userLocale, {
                // preHook: this.showLoadingZone,
                // postHook: this.hideLoadingZone,
                callback: function(res) {
                    _this.showStatus("");
                    //var topTerm = _this._getTopTermNode(res[0]);
                    console.debug("expandPath");
                    var path = _this.preparePath(res[0]);
                    _this.set("paths", [path])
                    .then(function() {
                        var node = UtilTree.getNodeById(_this.id, res[0].topicId);
                        wnd.scrollIntoView(node.domNode);
                        def.resolve();
                    });
                },
                errorHandler: function(msg) {
                    _this.showStatus("<fmt:message key='sns.connectionError' />");
                    def.reject();
                },
                exceptionHandler: function(msg) {
                    _this.showStatus("<fmt:message key='sns.connectionError' />");
                    def.reject();
                }
            });

            return def;
        },

        preparePath: function(node) {
            var path = [];
            while (node) {
                path.unshift(node.topicId);
                node = node.children[0];
            }
            path.unshift("THESAURUS-ROOT");
            return path;
        },

        showStatus: function(msg) {}
        
    } );

    return ThesaurusTree;
} );