/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
define("ingrid/hierarchy/behaviours.user", ["dojo/_base/lang", "dojo/dom",
        "ingrid/hierarchy/behaviours", "ingrid/utils/UI", "ingrid/hierarchy/rules",
        "dojo/dom-construct", "ingrid/layoutCreator", "ingrid/grid/CustomGridEditors",
        "ingrid/grid/CustomGridFormatters", "ingrid/utils/Syslist", "dijit/registry"


], function(lang, dom, behaviours, UtilUI, Rules, construct, layoutCreator, gridEditors, gridFormatters, syslist, registry) {

    return lang.mixin(behaviours, {
        bawDmqsBehaviour: {
            title: "BAW-MIS spezifisches Verhalten",
            description: "Beschreibung des Verhaltens",
            defaultActive: true,
            run: function() {
              try {
            	  
              // load custom syslists
              UtilSyslist.readSysListData(10103).then(function(entry) {
            	  sysLists[10103] = entry;
            	  
                  // refresh table to display the right values since the syslist might not be loaded
                  var c = registry.byId("simModelTypeTable");
                  if (c) {
                	  c.invalidateAllRows();
                      c.render();
                  }
              });
              // load custom syslists
              UtilSyslist.readSysListData(10104).then(function(entry) {
            	  sysLists[10104] = entry;
            	  
                  // refresh table to display the right values since the syslist might not be loaded
                  var c = registry.byId("simParamTable");
                  if (c) {
                	  c.invalidateAllRows();
                      c.render();
                  }
              });
            	  

              // hide elemene that cannot be set hidden in GUI "Zusaetzliche Felder"
              // hide "Vorschaugrafik"
              UtilUI.setHide('uiElement5100');
              // hide Vectorgrafik Rahmen
              UtilUI.setHide('ref1VFormat');
              // hide section "Dataquality"
              Rules.applyRule7 = function(){console.log("deactivate rule 7")};
              UtilUI.setHide('refClass1DQ');
              // deactivate event for showing dataquality on object class change (see generated rules)
              dojo.subscribe("/onObjectClassChange", function(c) {
                UtilUI.setHide("refClass1DQ");
              });
              UtilUI.setHide('objectClassLabel');
              //hide object class
              UtilUI.setHide(dom.byId('objectClassLabel').parentNode)

              } catch(err)  {
                console.error(err);
              }
            }
         }
    } );
});
