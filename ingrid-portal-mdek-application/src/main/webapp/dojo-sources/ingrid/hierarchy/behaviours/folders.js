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
  "dojo/_base/array",
  "dojo/_base/lang",
  "dojo/dom-class",
  "dojo/on",
  "dojo/query",
  "dojo/topic",
  "dijit/MenuItem",
  "dijit/form/Button",
  "dijit/registry",
  "ingrid/message",
  "ingrid/utils/Syslist",
  "ingrid/utils/Tree",
  "dojo/NodeList-traverse"
], function(declare, array, lang, domClass, on, query, topic, MenuItem, Button, registry, message, Syslist, TreeUtils) {

  return declare(null, {
    title: "Ordnerstruktur in Hierarchiebaum",
    description: "Fügt die Auswahl einer Klasse vom Typ Ordner hinzu, so dass Daten besser strukturiert werden können.",
    defaultActive: true,
    type: "SYSTEM",
    run: function() {
      // add new object class
      // delay execution to give other behaviours time to manipulate list id
      topic.subscribe("/additionalSyslistsLoaded", function() {
        var objectClasses = sysLists[Syslist.listIdObjectClass];
        if (objectClasses) {
          objectClasses.push([message.get("tree.folder"), "1000", "N", ""]);
        } else {
          alert("Syslist not found: " + Syslist.listIdObjectClass);
        }
      });

      // handle folder class selection
      topic.subscribe("/onObjectClassChange", function(data) {
        if (data.objClass === "Class1000") {
          domClass.add("contentFrameBodyObject", "hide");

        } else {
          domClass.remove("contentFrameBodyObject", "hide");
        }
      });
      topic.subscribe("/onAddressClassChange", function(data) {
        if (data.addressClass === 1000) {
          domClass.add("contentFrameBodyAddress", "hide");
          query("#addressTypeLabel").parent().addClass("hide");
          query("#addressOwnerLabel").parent().addClass("hide");
          query("label[for=addressTitle]").addContent(message.get("ui.adr.general.title") + "*", "only");
          registry.byId("addressTitle").set("disabled", false);

        } else {
          domClass.remove("contentFrameBodyAddress", "hide");
          query("#addressTypeLabel").parent().removeClass("hide");
          query("#addressOwnerLabel").parent().removeClass("hide");
          query("label[for=addressTitle]").addContent(message.get("ui.adr.header.addressTitle") + "*", "only");
          registry.byId("addressTitle").set("disabled", true);
          registry.byId("dataFormContainer").resize();
        }
      });

      // handle toolbar when folder is selected
      // -> only disable toolbar buttons that are not needed (be careful with IgeToolbar-Class-behaviour)
      var self = this;

      topic.subscribe("/onPageInitialized", function(page) {
        if (page === "Hiearchy") {
          var HierarchyTreeActions = require("ingrid/tree/HierarchyTreeActions");
          self.handleNodeSelect();

          // add creation of folder to context menu
          self._addToContextMenu();

          // handle actions on root node and folders directly beneath it
          on(HierarchyTreeActions.menu, "open", function() {
            var node = registry.byId("dataTree").selectedNode;
            
            if (node.item.id === "objectRoot" || node.item.id === "addressRoot") {
              domClass.remove("menuItemNewFolder", "hidden");
              registry.byId("menuItemNewFolder").set("disabled", false);

            } else if (node.item.id === "addressFreeRoot") {
              registry.byId("menuItemNewFolder").set("disabled", true);

            } else if (node.item.objectClass === 1000) {
              registry.byId("menuItemPreview").set("disabled", true);

            } else {
              registry.byId("menuItemNewFolder").set("disabled", false);
            }
          });

          // add toolbar buttons
          self._addToolbarButton();
        }
      });

    },

    handleNodeSelect: function() {
      var self = this;
      topic.subscribe("/selectNode", function(message) {
        // do not handle if another tree was selected!
        if (message.id && message.id != "dataTree") return;

        var selectedNode = message.node;

        // if we selected a folder
        if (selectedNode.objectClass === 1000) {

          var enabledButtons = ["toolbarBtnNewDoc", "toolbarBtnNewFolder", "toolbarBtnCut", "toolbarBtnCopy", "toolbarBtnCopySubTree", "toolbarBtnPaste", "toolbarBtnSave", "toolbarBtnDelSubTree", "toolbarBtnHelp"];
          var toolbarButtons = query("#myToolBar .dijitButton");
          array.forEach(toolbarButtons, function(btn) {
            if (enabledButtons.indexOf(btn.getAttribute("widgetid")) === -1) {
              registry.getEnclosingWidget(btn).set("disabled", true);
            }
          });
        }

        // do not allow to create a folder under free addresses!
        var disable = self.isFolderDisabledForNode(selectedNode);
        registry.byId("toolbarBtnNewFolder").set("disabled", disable);
      });
    },

    isFolderDisabledForNode: function(node) {
      var excludedId = "addressFreeRoot";

      // check if we have write permission and node is not excluded
      if (!node.userWriteTreePermission || node.id === excludedId) return true;

      // check all parents
      var parentId = node.parent;
      while (parentId && parentId !== excludedId) {
            var parentNode = TreeUtils.getNodeById("dataTree", parentId);
            parentId = parentNode.item.parent;
      }
      return parentId !== null && parentId !== undefined;
    },

    _addToContextMenu: function() {
      var HierarchyTreeActions = require("ingrid/tree/HierarchyTreeActions");
      var self = this;
      HierarchyTreeActions.menu.addChild(new MenuItem({
        id: "menuItemNewFolder",
        label: message.get("tree.folder.create"),
        onClick: function () {
          var nodeItem = HierarchyTreeActions.menu.clickedNode.item;
          self._createNewFolder(nodeItem.id, nodeItem.nodeAppType);
        }
      }), 1);
    },

    _createNewFolder: function(parentUuid, type) {
      console.log("new folder");
      if (parentUuid === "objectRoot" || parentUuid === "addressRoot") parentUuid = null;

      if (type === "O") {
        this._createNewObjectFolder(parentUuid);
      } else {
        this._createNewAddressFolder(parentUuid);
      }
    },

    _createNewObjectFolder: function(parentUuid) {
      var self = this;
      
      ObjectService.createNewNode(parentUuid, {
        callback: function(objNode) {
          objNode.nodeAppType = "O";
          objNode.objectClass = "1000";
          objNode.parentUuid = parentUuid;
          objNode.objectName = message.get("tree.folder.new");
          ObjectService.saveNodeData(objNode, "true", false, {
            callback: function(res) {
              // refresh tree node to show newly created folder
              var tree = registry.byId("dataTree");
              var node = tree.getNodesByItem(objNode.parentUuid ? objNode.parentUuid : "objectRoot" )[0];
              self._updateTree(tree, node, res);
            },
            errorHandler: function(err) {
              console.error("Error saving folder node: ", err);
              displayErrorMessage(err);
            }
          });
        },
        errorHandler: function(err) {
          console.error("Error creating folder node: ", err);
          displayErrorMessage(err);
        }
      });
    },

    _createNewAddressFolder: function(parentUuid) {
      var self = this;

      AddressService.createNewAddress(parentUuid, {
        callback: function(addressNode) {
          addressNode.nodeAppType = "A";
          addressNode.addressClass = "1000";
          addressNode.nodeDocType = "Class1000_B";
          // no folder under address free root!!!
          addressNode.parentUuid = parentUuid === "addressRoot" ? null : parentUuid;
          addressNode.name = message.get("tree.folder.new");
          AddressService.saveAddressData(addressNode, "true", false, {
            callback: function(res) {
              // refresh tree node to show newly created folder
              var tree = registry.byId("dataTree");
              var node = tree.getNodesByItem(addressNode.parentUuid ? addressNode.parentUuid : "addressRoot" )[0];
              self._updateTree(tree, node, res);
            },
            errorHandler: function(err) {
              console.error("Error saving folder node: ", err);
              displayErrorMessage(err);
            }
          });
        },
        errorHandler: function(err) {
          console.error("Error creating folder node: ", err);
          displayErrorMessage(err);
        }
      });
    },

    _updateTree: function(tree, parentNode, folderNode) {
      tree.refreshChildren(parentNode)
        .then(function() {
          return tree._expandNode(parentNode);
        })
        .then(function() {
          TreeUtils.selectNode("dataTree", folderNode.uuid, true);

          topic.publish("/loadRequest", {
            id: folderNode.uuid,
            appType: folderNode.nodeAppType,
            node: tree.getNodesByItem(folderNode.uuid)[0].item
          });
        });
    },

    _addToolbarButton: function() {
      var self = this;
      var tree = registry.byId("dataTree");
      var params = {
        id: "toolbarBtnNewFolder",
        label: message.get("ui.toolbar.NewFolderCaption"),
        style: "float: left",
        showLabel: false,
        disabled: true,
        iconClass: "image18px tabIconNewFolder",
        onClick: function() {
          var nodeItem = tree.selectedNode.item;
          self._createNewFolder(nodeItem.id, nodeItem.nodeAppType);
        }
      };
      registry.byId("myToolBar").addChild(new Button(params), 1);
    }

  })();
});