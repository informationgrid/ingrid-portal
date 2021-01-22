/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
    "dojo/_base/lang",
    "dojo/topic",
    "dojo/on",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dijit/registry",
    "dojo/cookie",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/hierarchy/behaviours",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist",
    "ingrid/hierarchy/behaviours/hmdk/formFields"
], function(declare, array, Deferred, lang, topic, on, aspect, dom, domClass, registry, cookie, message, dialog, behaviours, UtilGrid, UtilSyslist, formFields) {
    return lang.mixin(behaviours, {

        /**
         * Create fields for all the hmdk classes and show those fields according to the class.
         */
        hmdkFormFields: formFields,

        openDataHMDK : {
            title : "Open Data",
            description : "Neue Anforderungen seitens des Hamburger Transparenzportals, aber auch der Geodateninfrastruktur Hamburg, machen es erforderlich, dass die Funktionalitäten der beiden Checkboxen 'Open Data' und 'Veröffentlichung gemäß HmbTG' angepasst werden müssen.",
            category: "HMDK",
            defaultActive: false,
            run : function() {
                // disable free text in categories by choosing a selectbox editor
                var categories = registry.byId("categoriesOpenData");
                categories.columns[categories.columnsById.title].editor = this.SelectboxEditorDisplayValue;

                // always show categories
                topic.subscribe("/onObjectClassChange", function(data) {
                    if (data.objClass === "Class0") {
                        domClass.add("uiElement6020", "hide");
                    } else {
                        domClass.remove("uiElement6020", "hide");
                    }
                });

                // (REDMINE-315)
                topic.subscribe("/afterInitDialog/LinksDialog", function() {

                    var handleRequiredOfFileFormat = function(typeName) {
                        var typeKey = UtilSyslist.getSyslistEntryKey(2000, typeName);
                        var container = dom.byId("uiElement2240");

                        // If "Datendownload" then make it required
                        if (typeKey == 9990) {
                            domClass.add(container, "required");
                        } else {
                            domClass.remove(container, "required");
                        }
                    };

                    on(registry.byId("linksFromFieldName"), "Change", handleRequiredOfFileFormat);

                });

                this.handleDelete();
                this.handleModify();
                this.initialClass1();
                this.handleOpenDataChange();
                this.handlePublicationHmbTG();
                this.handleInformationSelect();
            },

            handlePublicationHmbTG: function() {
                var that = this;
                var COOKIE_NAME = "ingrid.hmbtg.hint";
                var HMBTG_INFO_TEXT_ACTIVATE = "Mit Aktivierung der Checkbox 'Veröffentlichung gemäß HmbTG' wird diese Metadatenbeschreibung und die dazugehörigen Ressourcen automatisch im Hamburger Transparenzportal veröffentlicht und dort 10 Jahre lang aufbewahrt. Soll die Metadatenbeschreibung dennoch nach HmbTG veröffentlicht werden?";
                var HMBTG_INFO_TEXT_MODIFY = "Dieser Datensatz ist bereits nach dem HmbTG im Transparenzportal veröffentlicht und bleibt auch nach Entfernen des Häkchens bei der Checkbox 'Veröffentlichung gemäß HmbTG' bis zum Ablauf der 10 Jahre im Transparenzportal veröffentlicht.";

                on(registry.byId("publicationHmbTG"), "Click", function(/*evnt*/) {
                    var isChecked = this.checked;
                    if (isChecked) {
                        var def = new Deferred();
                        var self = this;

                        // show first dialog
                        dialog.show(message.get("dialog.general.warning"), HMBTG_INFO_TEXT_ACTIVATE, dialog.INFO,
                            [
                                { caption: message.get("general.no"),
                                    action: function() {
                                        // reset checkbox state
                                        self.set("checked", false);
                                        def.reject();
                                    }
                                },
                                { caption: message.get("general.yes"),
                                    action: function() {
                                        // let the first dialog disappear first before showing the next one
                                        setTimeout(function() {
                                            def.resolve();
                                        }, 500);
                                    }
                                }
                            ]
                        );

                        def.then(function() {
                            var def2 = new Deferred();

                            // show second dialog if it was not disabled
                            if (cookie(COOKIE_NAME) !== "true") {
                                dialog.show(message.get("dialog.general.info"), message.get("hint.selectOpenData"), dialog.INFO,
                                    [
                                        { caption: message.get("general.ok.hide.next.time"), type: "checkbox",
                                            action: function(newValue) {
                                                cookie(COOKIE_NAME, newValue, {expires: 730});
                                            }
                                        },
                                        { caption: message.get("general.cancel"),
                                            action: function() {
                                                // reset checkbox state
                                                self.set("checked", false);
                                            }
                                        },
                                        { caption: message.get("general.ok"),
                                            action: function() {
                                                def2.resolve();
                                            }
                                        }
                                    ]);
                            } else {
                                def2.resolve();
                            }

                            def2.then(function() {
                                // only replace value if INSPIRE-relevant (#2222)
                                var isInspireRelevant = registry.byId("isInspireRelevant").get("checked");
                                if (isInspireRelevant) {
                                    // automatically replace access constraint with "keine"
                                    var data = [{title: UtilSyslist.getSyslistEntryName(6010, 1)}];
                                    UtilGrid.setTableData('availabilityAccessConstraints', data);
                                } else {
                                    // just empty table
                                    UtilGrid.setTableData('availabilityAccessConstraints', []);
                                }

                                // set Anwendungseinschränkungen to "Datenlizenz Deutschland Namensnennung". Extract from syslist !
                                var entryNameLicense = UtilSyslist.getSyslistEntryName(6500, 1);
                                UtilGrid.setTableData("availabilityUseAccessConstraints", [{title: entryNameLicense, source:'Freie und Hansestadt Hamburg, zuständige Behörde'}]);

                                // set publication condition to Internet
                                registry.byId("extraInfoPublishArea").attr("value", 1, true);
                            });
                        }, function(rejected) {});

                    } else {

                        // show info of already published version according to HmbTG
                        that.isPublishedByHmbTG(currentUdk.uuid)
                            .then(function(isPublished) {
                                if (isPublished) {
                                    dialog.show(message.get("dialog.general.info"), HMBTG_INFO_TEXT_MODIFY, dialog.INFO);
                                }
                            });

                        // remove all categories
                        UtilGrid.setTableData("categoriesOpenData", []);

                        // remove all "Informationgegenstände"
                        UtilGrid.setTableData("Informationsgegenstand", []);
                    }
                });

                // HmbTG checkbox behaviour
                var hmbTGAddressCheck = null;
                var hmbTGDownloadCheck = null;
                on(registry.byId("publicationHmbTG"), "Change", function(isChecked) {
                    if (isChecked) {
                        // only mandatory if INSPIRE-relevant (#2222)
                        var isInspireRelevant = registry.byId("isInspireRelevant").get("checked");
                        if (isInspireRelevant) {
                            domClass.add("uiElementN025", "required");
                        }
                        
                        domClass.add("uiElement6020", "required");

                        if (!hmbTGAddressCheck) hmbTGAddressCheck = that.addAddressCheck();
                        if (!hmbTGDownloadCheck) hmbTGDownloadCheck = that.addDownloadLinkCheck();

                    } else {
                        // unregister from check for download link
                        if (hmbTGAddressCheck) {
                            hmbTGAddressCheck.remove();
                            hmbTGAddressCheck = null;
                        }
                        if (hmbTGDownloadCheck) {
                            hmbTGDownloadCheck.remove();
                            hmbTGDownloadCheck = null;
                        }

                        domClass.remove("uiElementN025", "required");

                        // remove required field unless open data checkbox is still selected
                        if (!registry.byId("isOpenData").checked) {
                            domClass.remove("uiElement6020", "required");
                        }
                    }
                    that.handleInformationSelect();
                });

                on(registry.byId("isInspireRelevant"), "Change", function (isChecked) {
                    if (isChecked) {
                        // if publicationHmbTG is also checked then make access constraints required (#2222)
                        var isPublicationHmbTG = registry.byId("publicationHmbTG").get("checked");
                        if (isPublicationHmbTG) {
                            domClass.add("uiElementN025", "required");
                        }
                    } else {
                        domClass.remove("uiElementN025", "required");
                    }
                });
            },

            SelectboxEditorDisplayValue: function(args) {
                var options;
                var box;

                // initialize the UI
                this.init = function() {
                    var data;
                    if (args.column.listId)
                        data = lang.clone(sysLists[args.column.listId]);
                    else {
                        data = [];
                        for (var i = 0; i < args.column.options.length; i++) {
                            data[i] = [args.column.options[i], args.column.values[i]];
                        }
                    }

                    var store = new dojo.data.ItemFileWriteStore({
                        data: {
                            items: data
                        }
                    });
                    box = new dijit.form.FilteringSelect({
                        id: "activeCell_" + args.grid.id,
                        store: store,
                        searchAttr: "0",
                        maxHeight: "150",
                        style: "width:100%; padding:0; color: black; font-family: 10px Verdana, Helvetica, Arial, sans-serif;"
                    }).placeAt(args.container);
                    box.store.fetch();
                    //$(args.container).append(box.domNode);
                    box.focus();
                };

                /*********** REQUIRED METHODS ***********/

                this.destroy = function() {
                    // hide Tooltip if any
                    // Tooltip.hide(box.domNode);
                    // remove all data, events & dom elements created in the constructor
                    box.destroy();
                };

                this.focus = function() {
                    // set the focus on the main input control (if any)
                    box.focus();
                };

                this.isValueChanged = function() {
                    // return true if the value(s) being edited by the user has/have been changed
                    if (box.get("value") == "")
                        return false;
                    return true;
                };

                this.serializeValue = function() {
                    // return the value(s) being edited by the user in a serialized form
                    // can be an arbitrary object
                    // the only restriction is that it must be a simple object that can be passed around even
                    // when the editor itself has been destroyed
                    if (box.item === null)
                        return "";
                    return box.item[0][0]; // display text
                };

                this.loadValue = function(item) {
                    // load the value(s) from the data item and update the UI
                    // this method will be called immediately after the editor is initialized
                    // it may also be called by the grid if if the row/cell being edited is updated via grid.updateRow/updateCell
                    var search = item[args.column.field];
                    var items = box.store._arrayOfTopLevelItems;
                    var found = false;
                    array.forEach(items, function(item, i) {
                        if (item[0] == search) {
                            box.set("value", i);
                            found = true;
                        }
                    });
                    if (!found) {
                        array.forEach(items, function(item, i) {
                            if (item[1] == search) {
                                box.set("value", i);
                            }
                        });
                    }
                };

                this.applyValue = function(item, state) {
                    // deserialize the value(s) saved to "state" and apply them to the data item
                    // this method may get called after the editor itself has been destroyed
                    // treat it as an equivalent of a Java/C# "static" method - no instance variables should be accessed
                    item[args.column.field] = state;
                };

                this.validate = function() {
                    // validate user input and return the result along with the validation message, if any
                    // if the input is valid, return {valid:true,msg:null}
                    //return { valid: false, msg: "This field is required" };
                    return {
                        valid: true,
                        msg: null
                    };
                };


                /*********** OPTIONAL METHODS***********/

                this.hide = function() {
                    // if implemented, this will be called if the cell being edited is scrolled out of the view
                    // implement this is your UI is not appended to the cell itself or if you open any secondary
                    // selector controls (like a calendar for a datepicker input)
                };

                this.show = function() {
                    // pretty much the opposite of hide
                };

                this.position = function(cellBox) {
                    // if implemented, this will be called by the grid if any of the cell containers are scrolled
                    // and the absolute position of the edited cell is changed
                    // if your UI is constructed as a child of document BODY, implement this to update the
                    // position of the elements as the position of the cell changes
                    //
                    // the cellBox: { top, left, bottom, right, width, height, visible }
                };

                this.init();
            },

            handleOpenDataChange: function() {
                var that = this;
                // tick checkbox if "open data" has been selected (REDMINE-194)
                on(registry.byId("isOpenData"), "Click", function() {
                    if (this.checked) {
                        registry.byId("publicationHmbTG").set("value", true);

                        // set publication condition to Internet
                        registry.byId("extraInfoPublishArea").attr("value", 1, true);

                        // set Anwendungseinschränkungen to "Datenlizenz Deutschland Namensnennung". Extract from syslist !
                        var entryNameLicense = UtilSyslist.getSyslistEntryName(6500, 1);
                        UtilGrid.setTableData("availabilityUseAccessConstraints", [{title: entryNameLicense, source:'Freie und Hansestadt Hamburg, zuständige Behörde'}]);

                        var isInspireRelevant = registry.byId("isInspireRelevant").get("checked");
                        // remove all access constraints
                        var data = [];
                        if (isInspireRelevant) {
                            // add access constraint "keine"
                            data.push({ title: UtilSyslist.getSyslistEntryName(6010, 1) });
                        }
                        UtilGrid.setTableData('availabilityAccessConstraints', data);

                    } else {
                        // remove "keine" from access constraints
                        var data = UtilGrid.getTableData('availabilityAccessConstraints');
                        var posToRemove = 0;
                        var entryExists = array.some(data, function(item) {
                            if (item.title == "keine") {
                                return true;
                            }
                            posToRemove++;
                        });
                        if (entryExists) {
                            UtilGrid.removeTableDataRow('availabilityAccessConstraints', [posToRemove]);
                        }

                        // remove license set when open data was clicked
                        UtilGrid.setTableData("availabilityUseAccessConstraints", []);

                        // remove all categories
                        UtilGrid.setTableData("categoriesOpenData", []);
                    }
                });

                // open data checkbox behaviour (REDMINE-117)
                var openDataAddressCheck = null;
                var openDataDownloadCheck = null;
                on(registry.byId("isOpenData"), "Change", function(isChecked) {
                    var objClass = registry.byId("objectClass").get("value");

                    if (isChecked) {
                        if (!openDataAddressCheck) openDataAddressCheck = that.addAddressCheck();
                        // download link check already done in system behaviours
                        //openDataDownloadCheck = that.addDownloadLinkCheck();

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

                        // => LGV SPECIFIC CODE
                        // SHOW mandatory fields ONLY IF EXPANDED !
                        domClass.add("uiElement5064", "showOnlyExpanded"); // INSPIRE-Themen
                        domClass.add("uiElement3520", "showOnlyExpanded"); // Fachliche Grundlage
                        domClass.add("uiElement5061", "showOnlyExpanded"); // Datensatz/Datenserie
                        domClass.add("uiElement3565", "showOnlyExpanded"); // Datendefizit
                        domClass.add("uiElementN006", "showOnlyExpanded"); // Geothesaurus-Raumbezug
                        domClass.add("uiElement3500", "showOnlyExpanded"); // Raumbezugssystem
                        domClass.add("uiElement5041", "showOnlyExpanded"); // Sprache des Metadatensatzes
                        domClass.add("uiElement5042", "showOnlyExpanded"); // Sprache der Ressource
                        domClass.add("uiElementN024", "showOnlyExpanded"); // Konformität

                    } else {

                        // => LGV SPECIFIC CODE
                        // unregister from check for download link
                        if (this.openDataLinkCheck) {
                            this.openDataLinkCheck.remove();
                        }

                        if (openDataAddressCheck) {
                            openDataAddressCheck.remove();
                            openDataAddressCheck = null;
                        }
                        if (openDataDownloadCheck) {
                            openDataDownloadCheck.remove();
                            openDataDownloadCheck = null;
                        }

                        // show categories -> revert default behaviour
                        if (objClass !== "Class0") {
                            domClass.remove("uiElement6020", "hide");
                        }

                        // remove required field unless HmbTG checkbox is still selected
                        if (registry.byId("publicationHmbTG").checked) {
                            domClass.add("uiElement6020", "required");
                        } else {
                            domClass.remove("uiElement6020", "required");
                        }

                        // ALWAYS SHOW mandatory fields !
                        domClass.remove("uiElement5064", "showOnlyExpanded"); // INSPIRE-Themen
                        domClass.remove("uiElement3520", "showOnlyExpanded"); // Fachliche Grundlage
                        domClass.remove("uiElement5061", "showOnlyExpanded"); // Datensatz/Datenserie
                        domClass.remove("uiElement3565", "showOnlyExpanded"); // Datendefizit
                        domClass.remove("uiElementN006", "showOnlyExpanded"); // Geothesaurus-Raumbezug
                        domClass.remove("uiElement3500", "showOnlyExpanded"); // Raumbezugssystem
                        domClass.remove("uiElement5041", "showOnlyExpanded"); // Sprache des Metadatensatzes
                        domClass.remove("uiElement5042", "showOnlyExpanded"); // Sprache der Ressource
                        domClass.remove("uiElementN024", "showOnlyExpanded"); // Konformität

                        // Tab containers may be rendered for the first time and needs to be layouted
                        igeEvents.refreshTabContainers();

                    }
                    that.handleInformationSelect();
                });
            },

            isPublishedByHmbTG: function(uuid) {
                var def = new Deferred();
                var result = false;
                ObjectService.getPublishedNodeData(uuid, {
                    callback: function(data) {
                        array.some(data.additionalFields, function(field) {
                            if (field.identifier === "publicationHmbTG") {
                                result = (field.value === "true");
                                return true;
                            }
                        });
                        def.resolve(result);
                    },
                    errorHandler: function(msg, err) {
                        def.resolve(false);
                    }
                });
                return def.promise;
            },

            addAddressCheck: function() {
                return topic.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) {
                    // get name of codelist entry for entry-id "10" = "publisher"/"Herausgeber"
                    var entryNamePublisher = UtilSyslist.getSyslistEntryName(505, 10);

                    // check if entry already exists in table
                    var data = UtilGrid.getTableData("generalAddress");
                    var containsPublisher = array.some(data, function(item) { if (item.nameOfRelation == entryNamePublisher) return true; });
                    if (!containsPublisher)
                        notPublishableIDs.push(["generalAddress", "Es muss ein Herausgeber als Adresse angegeben sein."]);
                });
            },

            addDownloadLinkCheck: function() {
                // add check for url reference of type download when publishing
                // we check name and not id cause is combo box ! id not adapted yet if not saved !
                return topic.subscribe("/onBeforeObjectPublish", function( /*Array*/ notPublishableIDs) {
                    // get name of codelist entry for entry-id "9990" = "Download of data"/"Datendownload"
                    var entryNameDownload = UtilSyslist.getSyslistEntryName(2000, 9990);
                    var data = UtilGrid.getTableData("linksTo");
                    var containsDownloadLink = array.some(data, function(item) {
                        if (item.relationTypeName == entryNameDownload) return true;
                    });
                    if (!containsDownloadLink)
                        notPublishableIDs.push( ["linksTo", message.get("validation.error.missing.download.link")] );
                });
            },

            handleInformationSelect: function() {
                var infoSelect = registry.byId("Informationsgegenstand");
                var isOpenDataChecked = registry.byId("isOpenData").checked;
                var isHmbTGChecked = registry.byId("publicationHmbTG").checked;

                // if HmbTG is not set then disable "Informationsgegenstand"
                // and set value to "Ohne gesetzliche Verpflichtung"
                if (isOpenDataChecked && !isHmbTGChecked) {
                    domClass.remove("uiElementAddInformationsgegenstand", "hide");
                    infoSelect.setData([{ informationHmbTG: "hmbtg_20_ohne_veroeffentlichungspflicht" }]);
                    UtilGrid.updateOption("Informationsgegenstand", "editable", false);
                    infoSelect.reinitLastColumn();
                } else if (!isOpenDataChecked && !isHmbTGChecked) {
                    domClass.add("uiElementAddInformationsgegenstand", "hide");
                    infoSelect.setData([]);
                }

                // as long as HmbTG is checked, we are allowed to modify the
                // selectbox
                if (isHmbTGChecked) {
                    domClass.remove("uiElementAddInformationsgegenstand", "hide");
                    UtilGrid.updateOption("Informationsgegenstand", "editable", true);
                    domClass.add("uiElementAddInformationsgegenstand", "required");
                    infoSelect.reinitLastColumn();
                } else {
                    domClass.remove("uiElementAddInformationsgegenstand", "required");
                }
            },

            handleModify: function() {
                var self = this;
                var text = "Dieser Datensatz ist bereits nach dem HmbTG im Hamburger Transparenzportal veröffentlicht. Eine Änderung in der Metadatenbeschreibung führt zu einer neuen Version im Transparenzportal. Die alte Metadatenbeschreibung bleibt weiterhin im Transparenzportal veröffentlicht.";
                require(["ingrid/IgeActions"], function(igeAction) {
                    aspect.after(igeAction, "onAfterSave", function(){
                        // check if dataset has been published under HmbTG
                        var def = self.isPublishedByHmbTG(currentUdk.uuid);

                        // show info dialog if dataset was published (HmbTG)
                        def.then(function(isHmbTGPublished) {
                            if (isHmbTGPublished) {
                                dialog.show(message.get("dialog.general.info"), text, dialog.INFO);
                            }
                        });
                    });
                });
            },

            handleDelete: function() {
                var self = this;
                var text = "Dieser Datensatz ist bereits nach dem HmbTG im Hamburger Transparenzportal veröffentlicht und bleibt auch bei der Löschung der Metadatenbeschreibung aus dem HMDK bis zum Ablauf der 10 Jahre im Transparenzportal veröffentlicht.";
                require(["ingrid/MenuActions"], function(menuAction) {
                    aspect.around(menuAction, "_deleteNodes", function(origMethod){
                        return function(args){
                            // check if dataset has been published under HmbTG
                            var def = self.isPublishedByHmbTG(args[0].item.id);

                            // show info dialog if dataset was published (HmbTG)
                            def.then(function(isHmbTGPublished) {
                                if (isHmbTGPublished) {
                                    dialog.show(message.get("dialog.general.info"), text, dialog.INFO, [
                                        { caption: message.get("general.ok"),
                                            action: function() {
                                                setTimeout(function() {
                                                    lang.hitch(menuAction, lang.partial(origMethod, args))();
                                                }, 500);
                                            }
                                        }
                                    ]);
                                } else {
                                    lang.hitch(menuAction, lang.partial(origMethod, args))();
                                }
                            });
                        };
                    });
                });
            },

            initialClass1: function() {
                // default values when creating new objects of class 1 - geodata (REDMINE-119)
                topic.subscribe("/onObjectClassChange", function(data) {
                    if (currentUdk.uuid === "newNode" && data.objClass === "Class1") {
                        registry.byId("ref1BasisText").set("value", "keine Angabe");
                        registry.byId("ref1DataSet").set("value", "5"); // "Datensatz"
                        UtilGrid.setTableData("ref1SpatialSystem", [{title:"EPSG 25832: ETRS89 / UTM Zone 32N"}]);
                    }
                });
            }

        },

requireUseConstraintsHMDK: {
    title: "Nutzungsbedingungen - Pflichtfeld bei INSPIRE / Open Data",
    title_en: "Use Constraints - Required on INSPIRE / Open Data",
    description: "Das Feld \"Nutzungsbedingungen\" (ISO: useConstraints + useLimitation) wird verpflichtend, wenn die Checkbox \"Veröffentlichung gemäß HmbTG\", \"INSPIRE-relevanter Datensatz\" oder \"Open Data\" angeklickt wird.",
    description_en: "Input of field \"Use Constraints\" (ISO: useConstraints + useLimitation) is required if checkbox \"INSPIRE-relevant dataset\" or \"Open data\" is set.",
    issue: "https://dev.informationgrid.eu/redmine/issues/223",
    category: "HMDK",
    defaultActive: false,
    run: function () {

        // define our useConstraints handler
        var updateUseConstraintsBehaviour = function () {
            if (registry.byId("isInspireRelevant").checked ||
                registry.byId("isOpenData").checked ||
                registry.byId("publicationHmbTG").checked) {

                domClass.add("uiElementN027", "required");

            } else {
                domClass.remove("uiElementN027", "required");
            }
        };

        on(registry.byId("isInspireRelevant"), "Change", updateUseConstraintsBehaviour);
        on(registry.byId("isOpenData"), "Change", updateUseConstraintsBehaviour);
        on(registry.byId("publicationHmbTG"), "Change", updateUseConstraintsBehaviour);

        // initial check
        updateUseConstraintsBehaviour();
    }
}

    } );
});
