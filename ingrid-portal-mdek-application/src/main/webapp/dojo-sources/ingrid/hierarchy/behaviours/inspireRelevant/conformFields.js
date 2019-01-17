/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "ingrid/utils/Syslist",
    "ingrid/hierarchy/behaviours/utils"
], function(declare, array, aspect, string, on, topic, domClass, registry, message, UtilGrid, UtilUI, UtilSyslist, utils) {
    
    return declare(null, {
        title : "Konform-Auswahl",
        description : "Wenn aktiviert, dann wird für den Typ Geodatensatz beim Aktivieren der Checkbox \"INSPIRE-relevant\", eine zusätzliche Auswahl angeboten, ob dieser Datensatz konform oder nicht ist.",
        defaultActive : true,
        category: "INSPIRE relevant",
        specificationName: null,
        specificationNameInspireRichtlinie: null,
        // allow digital representations: Vector, Grid, Text-Table, TIN
        allowedDigitalRepresentations: ["1", "2", "3", "4"],
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

                        // Make inspire themes required
                        domClass.add("uiElement5064", "required");
                        domClass.remove("uiElement5064", "optional");
                        registry.byId("thesaurusInspire").reinitLastColumn();

                        // Also make conformity a required field
                        domClass.add("uiElementN024", "required");

                        // add events implicitly
                        var isConform = registry.byId("isInspireConform").checked;
                        if (isConform) {
                            self.handleInspireConform();
                        } else {
                            // do not call this handler here since it's called because of the change event
                            // self.handleNotInspireConform();
                        }
                    } else {
                        domClass.add("uiElement6001", "hidden");

                        // Make inspire themes optional
                        domClass.remove("uiElement5064", "required");
                        domClass.add("uiElement5064", "optional");
                        domClass.remove("uiElement5064", "show");

                        // Conformity is optional for non-INSPIRE fields
                        domClass.remove("uiElementN024", "required");

                        // make digital representation optional
                        domClass.remove( "uiElement5062", "required" );

                        // make encoding schema optional
                        domClass.remove( "uiElement1315", "required" );

                        // remove all conform/not conform events
                        utils.removeEvents(self.eventsConform);
                        utils.removeEvents(self.eventsNotConform);
                    }
                }),

                // set conform option and handle modifications when conform was checked implicitly
                on(inspireRelevantWidget, "Click", function(isChecked) {
                    if (this.checked) {
                        registry.byId("isInspireConform").set("checked", true);
                        self.handleClickConform();
                    }
                }),

                // if conform was explicitly clicked
                registry.byId("isInspireConform").on("click", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        self.handleClickConform();
                    }
                }),

                // if not conform was explicitly clicked
                registry.byId("notInspireConform").on("click", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        // add conformity "VERORDNUNG (EG) Nr. 1089/2010 - INSPIRE Durchführungsbestimmung Interoperabilität von Geodatensätzen und -diensten"
                        // with not evaluated level
                        utils.addConformity(false, self.specificationName, "3");

                        // remove INSPIRE Richtlinie
                        utils.removeConformity(self.specificationNameInspireRichtlinie);
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

        handleClickConform: function() {
            // add conformity "VERORDNUNG (EG) Nr. 1089/2010 - INSPIRE Durchführungsbestimmung Interoperabilität von Geodatensätzen und -diensten"
            // with conform level
            utils.addConformity(true, this.specificationName, "1");

            // remove INSPIRE Richtlinie
            utils.removeConformity(this.specificationNameInspireRichtlinie);
        },

        handleInspireConform: function() {
            // prevent multiple executions which can occur during click/change event (especially implicit ones)
            if (this.eventsConform.length > 0) {
                return;
            }

            console.log("konform");
            var self = this;
            var missingMessage = string.substitute(message.get("validation.specification.conform.missing"), [self.specificationName]);

            // remove events set from non conform radio box
            utils.removeEvents(this.eventsNotConform);

            // make digital representation required
            domClass.add( "uiElement5062", "required" );

            // make encoding schema required
            domClass.add( "uiElement1315", "required" );

            this.eventsConform.push(
                // added conformity must not be modified or deleted
                self.addEventSpecificationDelete(),
                
                on(registry.byId("extraInfoConformityTable"), "CellChange", function(msg) {
                    // if our spec changed AND was valid before
                    var rule1 = msg.oldItem && msg.oldItem.specification === self.specificationName && msg.item.specification !== self.specificationName;
                    var rule2 = msg.item.specification === self.specificationName && msg.item.level !== "1";
                    if (rule1 || rule2) {
                        var isInspire = false;
                        var publicationDate = null;
                        var entryData = UtilSyslist.getSyslistEntryData(6005, self.specificationName);
                        if (entryData == null) { // No entry in list 6005
                            entryData = UtilSyslist.getSyslistEntryData(6006, self.specificationName);
                        } else {
                            isInspire = true;
                        }
                        if (entryData != null) { // Found an entry in list 6005 or 6006
                            publicationDate = new Date(entryData);
                        }
                        // TODO is this the right approach?
                        UtilGrid.updateTableDataRow( "extraInfoConformityTable", msg.row, {
                            isInspire: isInspire,
                            specification: self.specificationName,
                            level: "1",
                            publicationDate: publicationDate
                        });
                        UtilUI.showToolTip( "extraInfoConformityTable", message.get("validation.levelOfSpecification.conform") );
                    }
                }),

                // Digitale Präsentation - Prüfung, ob Eintrag erfolgt
                on(registry.byId("ref1Representation"), "CellChange", function(msg) {
                    if (self.allowedDigitalRepresentations.indexOf(msg.item.title) === -1) {
                        if (msg.oldItem) {
                            UtilGrid.updateTableDataRow( "ref1Representation", msg.row, msg.oldItem );
                        } else {
                            UtilGrid.removeTableDataRow( "ref1Representation", [msg.row] );
                        }
                        UtilUI.showToolTip( "ref1Representation", message.get("validation.digitalRepresentation.conform") );
                    }
                }),

                // onPublish: Validierung entsprechend den Voreinstellungen:
                //      Spezifikation - inhalt. Prüfung
                //      Grad der Spezifikation - inhalt. Prüfung
                //      Kodierungsschema - Prüfung, ob Eintrag erfolgt
                topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
                    var requiredSpecification = UtilGrid.getTableData("extraInfoConformityTable")
                        .filter(function(item) { return item.specification === self.specificationName; });

                    if (requiredSpecification.length === 0) {
                        notPublishableIDs.push( ["extraInfoConformityTable", missingMessage] );
                    }

                    array.some(requiredSpecification, function(spec) {
                        if (spec.level != "1") {
                            notPublishableIDs.push( ["extraInfoConformityTable", missingMessage] );
                            return true;
                        }
                    });

                    // check that an INSPIRE CRS was added
                    var hasInspireCrs = UtilGrid.getTableData("ref1SpatialSystem")
                        .filter(function(item) { return item.title.toLowerCase().indexOf("(inspire)") !== -1; });
                    
                    if (hasInspireCrs.length === 0) {
                        notPublishableIDs.push( ["ref1SpatialSystem", message.get("validation.spatial.system.inspire.missing")] );
                    }

                })
            );
        },
        
        handleNotInspireConform: function() {
            console.log("nicht konform");
            // remove events set from conform radio box
            utils.removeEvents(this.eventsConform);

            // make digital representation optional
            domClass.remove( "uiElement5062", "required" );

            // make encoding schema optional
            domClass.remove( "uiElement1315", "required" );

            var self = this;
            var missingMessage = string.substitute(message.get("validation.specification.missing"), [self.specificationName]);

            this.eventsNotConform.push(
                // conformity level must be "not conform" or "not evaluated"
                on(registry.byId("extraInfoConformityTable"), "CellChange", function(msg) {
                    // if our spec changed AND was valid before
                    var rule1 = msg.oldItem && msg.oldItem.specification === self.specificationName && msg.item.specification !== self.specificationName;
                    var rule2 = msg.item.specification === self.specificationName && msg.item.level === "1";
                    if (rule1 || rule2) {
                        var isInspire = false;
                        var publicationDate = null;
                        var entryData = UtilSyslist.getSyslistEntryData(6005, self.specificationName);
                        if (entryData == null) { // No entry in list 6005
                            entryData = UtilSyslist.getSyslistEntryData(6006, self.specificationName);
                        } else {
                            isInspire = true;
                        }
                        if (entryData != null) { // Found an entry in list 6005 or 6006
                            publicationDate = new Date(entryData);
                        }
                        // TODO is this the right approach?
                        UtilGrid.updateTableDataRow( "extraInfoConformityTable", msg.row, {
                            isInspire: isInspire,
                            specification: self.specificationName,
                            level: "3",
                            publicationDate: publicationDate
                        });
                        UtilUI.showToolTip( "extraInfoConformityTable", message.get("validation.levelOfSpecification.notConform") );
                    }
                }),

                // the specification must not be deleted
                self.addEventSpecificationDelete(),

                // INSPIRE-Thema-abhängige Voreinstellung des Kodierungsschemas entfernen
                // => via behaviour!


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
                        if (spec.level === "1") {
                            notPublishableIDs.push( ["extraInfoConformityTable", missingMessage] );
                            return true;
                        }
                    });
                })
            );
        },

        addEventSpecificationDelete: function() {
            var self = this;
            return on(registry.byId("extraInfoConformityTable"), "DeleteItems", function(msg) {
                var numConformSpecs = UtilGrid.getTableData("extraInfoConformityTable")
                    .filter(function(item) { return item.specification === self.specificationName; })
                    .length;

                // if our specification is not there anymore, try to recover it if it was one of the deleted ones
                if (numConformSpecs === 0) {
                    // at least one of our specification must be there
                    array.forEach(msg.items, function(item) {
                        if (item.specification === self.specificationName) {
                            UtilGrid.addTableDataRow( "extraInfoConformityTable", item );
                            UtilUI.showToolTip( "extraInfoConformityTable", string.substitute(message.get("validation.specification.deleted"), [self.specificationName]) );
                        }
                    });
                }
            });
        },
        
        /**
         * remove all registered events
         */
        unregister: function() {
            utils.removeEvents(this.events);
            utils.removeEvents(this.eventsConform);
            utils.removeEvents(this.eventsNotConform);
        }
    })();
});
