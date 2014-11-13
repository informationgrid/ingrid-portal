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
    "dojo/_base/declare",
    "dojo/dom",
    "dojo/dom-class",
    "ingrid/utils/Tree"
], function(declare, dom, domClass, UtilTree) {

    // backward compatibility
    if (!dojo.addClass) dojo.addClass = domClass.add;
    if (!dojo.removeClass) dojo.removeClass = domClass.remove;

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
                    parameter[this._replaceAttribute(domItem.attributes[i].nodeName)] = domItem.attributes[i].value;
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