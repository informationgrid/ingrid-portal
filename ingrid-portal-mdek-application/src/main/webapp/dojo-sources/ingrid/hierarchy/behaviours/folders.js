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
  "dojo/query",
  "dojo/topic",
  "dijit/MenuItem",
  "dijit/form/Button",
  "dijit/registry",
  "ingrid/message",
  "ingrid/utils/Syslist",
  "ingrid/utils/Tree"
], function(declare, array, lang, domClass, query, topic, MenuItem, Button, registry, message, Syslist, TreeUtils) {

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

      // add button to create document wizard dialog
      /*topic.subscribe("/afterInitDialog/ChooseWizard", function(data) {
        var pos = data.types.indexOf(message.get("tree.folder"));
        if (pos !== -1) data.types.splice(pos, 1);

        data.buttons.push({
          label: message.get("tree.folder.create"),
          callback: function(closeDialog) {
            registry.byId("objectClass").set("value", "Class1000");
            registry.byId("objectName").set("value", message.get("tree.folder.new"));
            closeDialog();
          }
        });
      });*/

      // handle toolbar when folder is selected
      // -> only disable toolbar buttons that are not needed (be careful with IgeToolbar-Class-behaviour)
      var self = this;
      topic.subscribe("/onPageInitialized", function(page) {
        if (page === "Hiearchy") {
          self.handleNodeSelect();

          // add creation of folder to context menu
          self._addToContextMenu();

          // add toolbar buttons
          self._addToolbarButton();
        }
      });

    },

    handleNodeSelect: function() {
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
        } else {
          // if we selected another node
          registry.byId("toolbarBtnNewFolder").set("disabled", false);
        }
      });
    },

    _addToContextMenu: function() {
      var HierarchyTreeActions = require("ingrid/tree/HierarchyTreeActions");
      var self = this;
      HierarchyTreeActions.menu.addChild(new MenuItem({
        id: "menuItemNewFolder",
        label: message.get("tree.folder.create"),
        onClick: function () {
          self._createNewFolder(HierarchyTreeActions.menu.clickedNode.item.id);
        }
      }), 2);
    },

    _createNewFolder: function(parentUuid) {
      if (parentUuid === "objectRoot") parentUuid = null;

      ObjectService.createNewNode(null, function(objNode) {
        objNode.nodeAppType = "O";
        objNode.objectClass = "1000";
        objNode.parentUuid = parentUuid;
        objNode.objectName = message.get("tree.folder.new");
        ObjectService.saveNodeData(objNode, "true", false, {
          callback: function(res) {
            // refresh tree node to show newly created folder
            var tree = registry.byId("dataTree");
            var node = tree.getNodesByItem(objNode.parentUuid ? objNode.parentUuid : "objectRoot" )[0];
            tree.refreshChildren(node)
              .then(function() {
                return tree._expandNode(node);
              })
              .then(function() {
                TreeUtils.selectNode("dataTree", res.uuid, true);

                topic.publish("/loadRequest", {
                  id: res.uuid,
                  appType: res.nodeAppType,
                  node: tree.getNodesByItem(res.uuid)[0].item
                });
              });
          },
          errorHandler: function(err) {
            console.error("Error saving folder node: ", err);
          }
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
          self._createNewFolder(tree.selectedNode.item.id);
        }
      };
      registry.byId("myToolBar").addChild(new Button(params), 1);
    }

  })();
});