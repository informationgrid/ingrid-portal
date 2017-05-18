/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/aspect",
    "dojo/string",
    "dojo/on",
    "dojo/topic",
    "dojo/dom-class",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist"
], function(declare, array, aspect, string, on, topic, domClass, registry, message, UtilGrid, UtilUI, UtilSyslist) {
    
    return declare(null, {
        title : "Konform-Auswahl",
        description : "Wenn aktiviert, dann wird für den Typ Geodatensatz beim Aktivieren der Checkbox \"INSPIRE-relevant\", eine zusätzliche Auswahl angeboten, ob dieser Datensatz konform oder nicht ist.",
        defaultActive : true,
        category: "INSPIRE relevant",
        specificationName: null,
        specificationNameInspireRichtlinie: null,
        events: [],
        eventsConform: [],
        eventsNotConform: [],
        run : function() {
            
            var changeEvent = null,
                clickEvent = null,
                self = this;

            this.specificationName = UtilSyslist.getSyslistEntryName(6005, 12);
            this.specificationNameInspireRichtlinie = UtilSyslist.getSyslistEntryName(6005, 13);
            
            // only for geo dataset
            topic.subscribe("/onObjectClassChange",  function(msg) {
                if (msg.objClass === "Class1") {
                    self.register();
                } else {
                    self.unregister();
                    // don't forget to hide the element
                    domClass.add("uiElement6001", "hidden");
                }
            });
        },

        /**
         * register all events for handling conform/not conform
         */
        register: function() {
            var inspireRelevantWidget = registry.byId("isInspireRelevant");
            var self = this;
            
            // when registering the event handler then the change might already has happened
            // so that we have to set here manually
            if (inspireRelevantWidget.checked) domClass.remove("uiElement6001", "hidden");

            this.events.push( 
                // show/hide radio boxes when inspire relevant was checked
                on(inspireRelevantWidget, "Change", function(isChecked) {
                    if (isChecked) {
                        domClass.remove("uiElement6001", "hidden");
                    } else {
                        domClass.add("uiElement6001", "hidden");
                    }
                }),

                // set conform option and handle modifications when conform was checked implicitly
                on(inspireRelevantWidget, "Click", function(isChecked) {
                    if (isChecked) {
                        registry.byId("isInspireConform").set("checked", true);
                        
                        // do everything that would happen on click! (see below)
                        self.onClickInspireConform();
                    }
                }),

                // if conform was explicitly clicked
                registry.byId("isInspireConform").on("click", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        self.onClickInspireConform();
                    }
                }),

                // if not conform was explicitly clicked
                registry.byId("notInspireConform").on("click", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        // add conformity "VERORDNUNG (EG) Nr. 1089/2010 - INSPIRE Durchführungsbestimmung Interoperabilität von Geodatensätzen und -diensten"
                        // with not evaluated level
                        self.addConformity(self.specificationName, 3);

                        // remove INSPIRE Richtlinie
                        self.removeConformity(self.specificationNameInspireRichtlinie);
                    }
                }),

                // if conform was changed
                registry.byId("isInspireConform").on("change", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        self.handleInspireConform();
                    }
                }),
            
                // if not conform was changed
                registry.byId("notInspireConform").on("change", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        self.handleNotInspireConform();
                    }
                })
            );
        },

        onClickInspireConform: function() {
            // add conformity "VERORDNUNG (EG) Nr. 1089/2010 - INSPIRE Durchführungsbestimmung Interoperabilität von Geodatensätzen und -diensten"
            // with conform level
            this.addConformity(this.specificationName, 1);
        },
        
        handleInspireConform: function() {
            console.log("konform");
            this.removeEvents(this.eventsNotConform);
            

        },
        
        handleNotInspireConform: function() {
            console.log("nicht konform");
            this.removeEvents(this.eventsConform);

            var self = this;
            var missingMessage = string.substitute(message.get("validation.specification.missing"), [self.specificationName]);

            this.eventsNotConform.push(
                // conformity level must be "not conform" or "not evaluated"
                on(registry.byId("extraInfoConformityTable"), "CellChange", function(msg) {
                    if (msg.item.specification === self.specificationName && msg.item.level == "1") {
                        if (msg.oldItem) {
                            UtilGrid.updateTableDataRow( "extraInfoConformityTable", msg.row, msg.oldItem );
                        } else {

                        }
                        UtilUI.showToolTip( "extraInfoConformityTable", message.get("validation.levelOfSpecification.notConform") );
                    }
                }),
                // the specification must not be deleted
                on(registry.byId("extraInfoConformityTable"), "DeleteItems", function(msg) {
                    array.forEach(msg.items, function(item) {
                        if (item.specification === self.specificationName) {
                            UtilGrid.addTableDataRow( "extraInfoConformityTable", item );
                            UtilUI.showToolTip( "extraInfoConformityTable", string.substitute(message.get("validation.specification.deleted"), [self.specificationName]) );
                        }
                    });
                }),

                // INSPIRE-Thema-abhängige Voreinstellung des Kodierungsschemas entfernen
                // onPublish: Validierung entsprechend den Voreinstellungen:
                //      Spezifikation - inhalt. Prüfung
                //      Grad der Spezifikation - inhalt. Prüfung
                topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
                    var requiredSpecification = UtilGrid.getTableData("extraInfoConformityTable")
                        .filter(function(item) { return item.specification === self.specificationName; });

                    if (requiredSpecification.length === 0) {
                        notPublishableIDs.push( ["extraInfoConformityTable", missingMessage] );
                        return true;
                    }

                    array.some(requiredSpecification, function(spec) {
                        if (spec.level == "1") {
                            notPublishableIDs.push( ["extraInfoConformityTable", missingMessage] );
                            return true;
                        }
                    });
                })
            );
        },
        
        addConformity: function(name, level) {
            console.log("Add conformity");
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            conformityData.push({
                specification: name,
                level: level
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeConformity: function(name) {
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeEvents: function(events) {
            array.forEach(this.eventsNotConform, function(event) {
                event.remove();
            });
            // empty array
            events.splice(null);
        },

        /**
         * remove all registered events
         */
        unregister: function() {
            this.removeEvents(this.events);
        }
    })();
});
