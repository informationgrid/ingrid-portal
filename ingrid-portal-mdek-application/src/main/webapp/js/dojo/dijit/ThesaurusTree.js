dojo.provide("ingrid.dijit.ThesaurusTree");

dojo.require("ingrid.dijit.CustomTree");

dojo.declare("ingrid.dijit.ThesaurusTree", null, {
    domId: null,
    nodeId: "uniqueId",
    nodeLabel: "title",
    treeWidget: null,
    loadingDivId: null,
    
    constructor: function(args) {
        this.domId = args.domId;
        if (args.loadingDivId) this.loadingDivId = args.loadingDivId;
        if (args.showLoadingZone) this.showLoadingZone = args.showLoadingZone;
        if (args.hideLoadingZone) this.hideLoadingZone = args.hideLoadingZone;
        if (args.showStatus) this.showStatus = args.showStatus;
        createCustomTree(this.domId, null, this.nodeId, this.nodeLabel, dojo.hitch(this, this._loadSNSData));
        this.treeWidget = dijit.byId(this.domId);
    },
    
    _loadSNSData: function(node, callback_function) {
        var _this = this;
        var deferred = new dojo.Deferred();
        
        // if it's the initialization
        if (node.item.root) {
            SNSService.getRootTopics({
                preHook: function(){
                    _this.showLoadingZone();
                },
                postHook: function(){
                    _this.hideLoadingZone();
                },
                callback: function(res){
                    if (!_this.treeWidget) return;
                    _this.showStatus("");
                    _this._handleRootTopics(res);
                    callback_function();
                },
                timeout: 0,
                errorHandler: function(msg){
                    deferred.errback(msg);
                    _this.showStatus("<fmt:message key='sns.connectionError' />");
                },
                exceptionHandler: function(msg){
                    deferred.errback(msg);
                    _this.showStatus("<fmt:message key='sns.connectionError' />");
                }
            });
        }
        else {
            SNSService.getSubTopics(node.item.topicId[0], '2', 'down', {
                preHook: function(){
                    _this.showLoadingZone();
                },
                postHook: function(){
                    _this.hideLoadingZone();
                },
                callback: function(res){
                    if (!_this.treeWidget) return;
                    deferred.callback(res);
                    _this.showStatus("");
                    callback_function();
                },
                timeout: 0,
                errorHandler: function(msg){
                    console.debug(msg);
                    _this.showStatus("<fmt:message key='sns.connectionError' />");
                    deferred.errback(msg);
                }
            });
            
            deferred.addCallback(function(res){
                UtilList.addSNSTopicLabels(res);
                var tree = dijit.byId(_this.domId);
                for (i in res) {
                    res[i]._title = res[i].title;
                    res[i].title = res[i].label;
                    res[i].isFolder = (res[i].children.length > 0);
                    res[i].nodeDocType = res[i].type;
                    res[i].children = [];
                    
                    // node id must be unique, but topicId can occur in different branches of the tree
                    // add parent id to the id
                    res[i].uniqueId = node.item.topicId + "." + res[i].topicId;
                    
                    // make node not selectable if not Descriptor
                    if (res[i].nodeDocType == "NODE_LABEL" || res[i].nodeDocType == "NON_DESCRIPTOR") {
                        res[i].labelClass = "TreeNodeNotSelectable";
                        res[i].viewMouseOver = function(){
                        };
                    }
                    // add node to tree
                    tree.model.store.newItem(res[i], {
                        parent: node.item,
                        attribute: "children"
                    });
                }
            });
        }
        
        deferred.addErrback(function(res){
            alert(res.message);
        });
        return deferred;
    },
    
    // Callback Handler for the root topics.
    // This function gets called when the tree is initialized.
    // @param topicList - List of Topics from the SNSService   
    //
    // The function adds the topics in 'topicList' as children to the tree 
    _handleRootTopics: function(topicList) {
        for (var i in topicList) {
            topicList[i].isFolder = true;
            topicList[i].nodeDocType = topicList[i].type;
            // Top Terms are not selectable. Add the proper class to make them grey
            topicList[i].labelClass = "TreeNodeNotSelectable";
            topicList[i].uniqueId = topicList[i].topicId;
            
            this.treeWidget.model.store.newItem(topicList[i]);
        }
    },
    
    // Expands the tree to the topic with id 'topicID'
    // A valid topicID can be acquired by calling SNSService.findTopic()
    expandToTopicWithId: function(topicID) {
        var def = new dojo.Deferred();
        var treePane = this.treeWidget;
        var _this = this;
        SNSService.getSubTopicsWithRoot(topicID, '0', 'up', {
            preHook: function(){
                _this.showLoadingZone();
            },
            postHook: function(){
                _this.hideLoadingZone();
            },
            callback: function(res){
                _this.showStatus("");
                var topTerm = _this._getTopTermNode(res[0]);
                console.debug("expandPath");
                _this._expandPath(treePane.rootNode, topTerm, res[0], def);
            },
            timeout: 0,
            errorHandler: function(msg){
                _this.showStatus("<fmt:message key='sns.connectionError' />");
            },
            exceptionHandler: function(msg){
                _this.showStatus("<fmt:message key='sns.connectionError' />");
            }
        });
        
        return def;
    },
    
    // Expands the tree
    // This is an internal function and should not be called. Call expandToTopicWithId(topicID) instead
    // @param tree - Tree/TreeNode. Current position in the tree   
    // @param currentNode - SNSTopic. The current node we are looking for to expand
    // @param targetNode - SNSTopic. Last node in the tree. return when we found this node. 
    //
    // The function iterates over tree.children and locates a TreeNode with topicID == currentNode.topicID
    // This node is then expanded and passed to expandPath recursively in a callback function.
    _expandPath: function(tree, currentNode, targetNode, def) {
        
        // Break Condition
        if (currentNode.topicId == targetNode.topicId) {
            // Mark the target node as selected
            dojo.some(tree.getChildren(), function(child) {
                if (child.item.topicId == currentNode.topicId) {
                    this.treeWidget._selectNode(child);
                    if (tree._expandDeferred) {
                        tree._expandDeferred.addCallback(function() {
                            //console.debug("jump to node 500ms delayed, since animation might not be done yet");
                            setTimeout(function() {dojo.window.scrollIntoView(child.domNode)}, 500);
                        });
                    }
                    def.callback();
                    return true;
                }
            }, this);
            return;
        }
        
        // Iterate over tree.children and locate the node next node in the path
        dojo.some(tree.getChildren(), function(curTreeNode) {
            if (curTreeNode.item.topicId == currentNode.topicId) {
                // A node with the correct topicId was found. If the node is not expanded and
                // it's children have not been loaded yet, load the children via callback and
                // do the recursion afterwards.
                if (!curTreeNode.isExpanded && curTreeNode.getChildren().length == 0) {
                    // dojo.debug('Passed the following node to callback: '+curTreeNode.topicId+' '+curTreeNode.widgetId);
                    var widgetId = curTreeNode.id;
                    this.treeWidget._expandNode(curTreeNode).addCallback(dojo.hitch(this, function(res){
                        // dojo.debug('Callback got the following node: '+dijit.byId(widgetId).topicId+' '+widgetId);
                        this._expandPath(dijit.byId(widgetId), currentNode.parents[0], targetNode, def);
                    }));
                    return true;
                }
                // If the children have been loaded, expand the node and continue with the recursion
                else {
                    this.treeWidget._expandNode(curTreeNode);
                    this._expandPath(curTreeNode, currentNode.parents[0], targetNode, def);
                }
            }
        }, this);
    },
    
    
    // Walks through a 'SNSTopic' structure (from SNSService.getSubTopics()) and returns a TOP_TERM
    _getTopTermNode: function(node) {
        if (node.type == 'TOP_TERM') {
            return node;
        }
        else {
            if (node.children != null && node.children.length != 0) {
                return this._getTopTermNode(node.children[0]);
            }
            else {
                dojo.debug('Error in getTopTermNode: No parent of type TOP_TERM found.');
                return node;
            }
        }
    },
    
    destroy: function() {
        console.debug("destroy tree");
        this.loadingDivId = null;
        this.treeWidget.destroy();
        this.treeWidget = null;
    },    
    
    showLoadingZone: function() { if (this.loadingDivId) dojo.style(this.loadingDivId, "visibility", "visible"); },
    
    hideLoadingZone: function() { if (this.loadingDivId) dojo.style(this.loadingDivId, "visibility", "hidden"); },
    
    showStatus: function(msg) {},
});