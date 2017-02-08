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
  "dojo/dom-class",
  "dojo/query",
  "dojo/topic",
  "dijit/registry",
  "ingrid/message",
  "ingrid/utils/Syslist"
], function (declare, array, domClass, query, topic, registry, message, Syslist) {

  return declare(null, {
    title: "Ordnerstruktur in Hierarchiebaum",
    description: "Fügt die Auswahl einer Klasse vom Typ Ordner hinzu, so dass Daten besser strukturiert werden können.",
    defaultActive: true,
    type: "SYSTEM",
    run: function () {
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
      topic.subscribe("/onObjectClassChange", function (data) {
        if (data.objClass === "Class1000") {
          domClass.add("contentFrameBodyObject", "hide");

        } else {
          domClass.remove("contentFrameBodyObject", "hide");
        }
      });

      topic.subscribe("/afterInitDialog/ChooseWizard", function (data) {
        var pos = data.types.indexOf(message.get("tree.folder"));
        if (pos !== -1) data.types.splice(pos, 1);

        data.buttons.push( {
          label: message.get("tree.folder.create"),
          callback: function(closeDialog) {
            registry.byId("objectClass").set("value", "Class1000");
            registry.byId("objectName").set("value", message.get("tree.folder.new"));
            closeDialog();
          }
        });
      });

      // handle toolbar when folder is selected
      // -> only disable toolbar buttons that are not needed (be careful with IgeToolbar-Class-behaviour)
      var self = this;
      topic.subscribe("/onPageInitialized", function(page) {
        if (page === "Hiearchy") {
          self.handleFolderSelect();
        }
      });
    },

    handleFolderSelect: function() {
      topic.subscribe("/selectNode", function (message) {
        // do not handle if another tree was selected!
        if (message.id && message.id != "dataTree") return;

        var selectedNode = message.node;

        // if we didn't select a folder then leave
        if (selectedNode.objectClass !== 1000) return;

        var enabledButtons = ["toolbarBtnNewDoc", "toolbarBtnCut", "toolbarBtnCopy", "toolbarBtnCopySubTree", "toolbarBtnPaste", "toolbarBtnSave", "toolbarBtnDelSubTree", "toolbarBtnHelp"];
        var toolbarButtons = query("#myToolBar .dijitButton");
        array.forEach(toolbarButtons, function (btn) {
          if (enabledButtons.indexOf(btn.getAttribute("widgetid")) === -1) {
            registry.getEnclosingWidget(btn).set("disabled", true);
          }
        });
      });
    }

  })();
});