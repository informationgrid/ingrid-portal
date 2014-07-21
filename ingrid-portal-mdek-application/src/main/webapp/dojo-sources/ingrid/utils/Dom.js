define([
    "dojo/_base/declare",
    "dojo/dom",
    "dojo/dom-class",
    "ingrid/utils/Tree"
], function(declare, dom, domClass, UtilTree) {
    return declare(null, {

        attributeReplacerMap: {
            listid: "listId",
            maxheight: "maxHeight",
            maxlength: "maxLength",
            onrowcontextmenu: "onRowContextMenu",
            minrows: "minRows",
            autoheight: "autoHeight",
            selectionmode: "selectionMode",
            onchange: "onChange",
            contextmenu: "contextMenu",
            multiselect: "multiSelect",
            forcegridheight: "forceGridHeight",
            allowemptyrows: "allowEmptyRows",
            defaulthidescrollbar: "defaultHideScrollbar" //,
            //class: "\"class\""
        },

        // get all attributes of a dom node as a hashmap
        getHTMLAttributes: function(item) {
            var parameter = {};
            var domItem = dom.byId(item);
            if (domItem != null) {
                for (i = 0; i < domItem.attributes.length; i++) {
                    parameter[this._replaceAttribute(domItem.attributes[i].nodeName)] = domItem.attributes[i].nodeValue;
                }
            }
            return parameter;
        },

        // In XHTML all attributes should be lower case, however Dojo needs some attributes
        // which use upper case letters to be identified. So we need to remap them. 
        _replaceAttribute: function(attribute) {
            var lcAttr = attribute.toLowerCase();
            if (this.attributeReplacerMap[lcAttr])
                return this.attributeReplacerMap[lcAttr];
            return attribute;
        },

        // Search for a parent node with the uuid and check if
        // it has the active selected node as a child
        activeNodeHasParent: function(parentUuid) {
            if (!currentUdk) return false;

            var activeDomNode = UtilTree.getNodeById("dataTree", currentUdk.uuid).domNode.parentNode;
            while (activeDomNode) {
                if (activeDomNode.id == parentUuid) return true;
                activeDomNode = activeDomNode.parentNode;
            }
            return false;
        },

        findParentNodeWithClass: function(node, clazz) {
            if (!node) return null;
            if (domClass.contains(node, clazz))
                return node;
            else
                return this.findParentNodeWithClass(node.parentNode, clazz);
        }
    })();
});