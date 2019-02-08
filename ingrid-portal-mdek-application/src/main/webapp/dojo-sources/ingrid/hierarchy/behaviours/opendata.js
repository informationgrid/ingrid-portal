/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
  "dojo/Deferred",
  "dojo/cookie",
  "dojo/dom",
  "dojo/dom-class",
  "dojo/on",
  "dojo/topic",
  "dijit/registry",
  "ingrid/message",
  "ingrid/dialog",
  "ingrid/utils/Grid",
  "ingrid/utils/Syslist"
], function (declare, array, Deferred, cookie, dom, domClass, on, topic, registry, message, dialog, UtilGrid, UtilSyslist) {

  return declare(null, {
    title: "Open Data",
    description: "Bei der Aktivierung der Checkbox \"Open Data\" werden folgende Verhalten hinzugefügt. Anzeige der Kategorien als Pflichtfeld. Außerdem muss ein Verweis vom Typ \"Datendownload\" existieren.",
    defaultActive: true,
    COOKIE_HIDE_OPEN_DATA_HINT: "ingrid.open.data.hint",
    run: function () {
      // hide open-data checkbox for classes 0 and 4
      topic.subscribe("/onObjectClassChange", function (data) {
        if (data.objClass === "Class0" || data.objClass === "Class4" || data.objClass === "Class20") {
          domClass.add(dom.byId("uiElement6010"), "hide");
          // also uncheck openData checkbox, so that categories table must not be
          // displayed according to the state
          registry.byId("isOpenData").set("checked", false);
        } else {
          domClass.remove(dom.byId("uiElement6010"), "hide");
        }
      });

      // this event will also execute when object is loaded
      // here we have to make sure the categories table is shown/removed correctly
      on(registry.byId("isOpenData"), "Change", function (isChecked) {

        if (isChecked) {

          // show categories
          domClass.remove("uiElement6020", "hide");

          // make field mandatory
          domClass.add("uiElement6020", "required");

          // add check for url reference of type download when publishing
          // we check name and not id cause is combo box ! id not adapted yet if not saved !
          this.openDataLinkCheck = topic.subscribe("/onBeforeObjectPublish", function ( /*Array*/ notPublishableIDs) {
            // get name of codelist entry for entry-id "9990" = "Download of data"/"Datendownload"
            var entryNameDownload = UtilSyslist.getSyslistEntryName(2000, 9990);
            var data = UtilGrid.getTableData("linksTo");
            var containsDownloadLink = array.some(data, function (item) {
              if (item.relationTypeName == entryNameDownload) return true;
            });
            if (!containsDownloadLink)
              notPublishableIDs.push(["linksTo", message.get("validation.error.missing.download.link")]);
          });
        } else {
          // hide categories
          domClass.add("uiElement6020", "hide");

          // revert field mandatory
          domClass.remove("uiElement6020", "required");

          // unregister from check for download link
          if (this.openDataLinkCheck)
            this.openDataLinkCheck.remove();
        }
      });

      // behavior when the checkbox is actively clicked by a user
      // only actions that shall modify data once and has nothing to do with the view!
      var thisBehaviour = this;
      on(registry.byId("isOpenData"), "Click", function (/*evnt*/) {
        var isChecked = this.checked;
        if (isChecked) {
          var def = new Deferred();
          var self = this;
          if (cookie(thisBehaviour.COOKIE_HIDE_OPEN_DATA_HINT) !== "true") {
            dialog.show(message.get("dialog.general.info"), message.get("hint.selectOpenData"), dialog.INFO,
              [
                {
                  caption: message.get("general.ok.hide.next.time"), type: "checkbox",
                  action: function (newValue) {
                    cookie(thisBehaviour.COOKIE_HIDE_OPEN_DATA_HINT, newValue, { expires: 730 });
                  }
                },
                {
                  caption: message.get("general.cancel"),
                  action: function () {
                    // reset checkbox state
                    self.set("checked", false);
                  }
                },
                {
                  caption: message.get("general.ok"),
                  action: function () {
                    def.resolve();
                  }
                }
              ]);
          } else {
            def.resolve();
          }

          def.then(function () {
            // automatically replace access constraint with "keine"
            var data = [{ title: UtilSyslist.getSyslistEntryName(6010, 1) }];
            UtilGrid.setTableData('availabilityAccessConstraints', data);
          });

        } else {
          // remove all categories
          UtilGrid.setTableData("categoriesOpenData", []);
        }
      });
    }
  })();
});
