require(["dojo/_base/array", 
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
        "ingrid/utils/Grid", 
        "ingrid/utils/Syslist",
        "ingrid/grid/CustomGridEditors"
], function(array, Deferred, lang, topic, on, aspect, dom, domClass, registry, cookie, message, dialog, UtilGrid, UtilSyslist, Editors) {

    var openData = {
        run : function() {
            // disable free text in categories by choosing a selectbox editor
            var categories = registry.byId("categoriesOpenData");
            categories.columns[categories.columnsById.title].editor = Editors.SelectboxEditor;

            // always show categories
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class0") {
                    domClass.add("uiElement6020", "hide");
                } else {
                    domClass.remove("uiElement6020", "hide");
                }
            });
            
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
                            // automatically replace access constraint with "keine"
                            var data = [{title: UtilSyslist.getSyslistEntryName(6010, 1) }];
                            UtilGrid.setTableData('availabilityAccessConstraints', data);
                            
                            // set Anwendungseinschränkungen to "Datenlizenz Deutschland Namensnennung". Extract from syslist !
                            var entryNameLicense = UtilSyslist.getSyslistEntryName(6500, 1);
                            registry.byId("availabilityUseConstraints").attr("value", entryNameLicense, true);
                            
                            // set publication condition to Internet
                            registry.byId("extraInfoPublishArea").attr("value", 1, true);
                        });
                    }, function(rejected) {});
                    
                } else {
                    
                    // show info of already published version according to HmbTG
                    var def = that.isPublishedByHmbTG(currentUdk.uuid)
                    def.then(function(isPublished) {
                        if (isPublished) {
                            dialog.show(message.get("dialog.general.info"), HMBTG_INFO_TEXT_MODIFY, dialog.INFO);
                        }
                    });
                    
                    // remove all categories
                    UtilGrid.setTableData("categoriesOpenData", []);
                }
            });
            
            // HmbTG checkbox behaviour
            var hmbTGAddressCheck = null;
            var hmbTGDownloadCheck = null;
            on(registry.byId("publicationHmbTG"), "Change", function(isChecked) {
                if (isChecked) {
                    domClass.add("uiElement6020", "required");
                    hmbTGAddressCheck = that.addAddressCheck();
                    hmbTGDownloadCheck = that.addDownloadLinkCheck();
                    
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
                        
                    // remove required field unless open data checkbox is still selected
                    if (!registry.byId("isOpenData").checked) {
                        domClass.remove("uiElement6020", "required");
                    }
                }
                that.handleInformationSelect();
            });
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
                    registry.byId("availabilityUseConstraints").attr("value", entryNameLicense, true);
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
                    registry.byId("availabilityUseConstraints").attr("value", "");
                }
            });
            
            // open data checkbox behaviour
            var openDataAddressCheck = null;
            var openDataDownloadCheck = null;
            on(registry.byId("isOpenData"), "Change", function(isChecked) {
                var objClass = registry.byId("objectClass").get("value");

                if (isChecked) {
                    openDataAddressCheck = that.addAddressCheck();
                    // download link check already done in system behaviours
                    //openDataDownloadCheck = that.addDownloadLinkCheck();
                    
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
                    domClass.add("uiElement1315", "showOnlyExpanded"); // Kodierungsschema
                    
                } else {
                    
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
                    domClass.remove("uiElement1315", "showOnlyExpanded"); // Kodierungsschema

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
                infoSelect.set("displayedValue", "Ohne gesetzliche Verpflichtung");
                infoSelect.set("disabled", true);
            } else if (!isOpenDataChecked && !isHmbTGChecked) {
                domClass.add("uiElementAddInformationsgegenstand", "hide");
                infoSelect.set("displayedValue", "");
            }

            // as long as HmbTG is checked, we are allowed to modify the
            // selectbox
            if (isHmbTGChecked) {
                domClass.remove("uiElementAddInformationsgegenstand", "hide");
                infoSelect.set("disabled", false);
                domClass.add("uiElementAddInformationsgegenstand", "required");
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
                    UtilGrid.setTableData("thesaurusInspire", [{title: 99999}]); // "Kein INSPIRE-Thema"
                    UtilGrid.setTableData("extraInfoConformityTable", [{specification:"INSPIRE-Richtlinie", level:3}]); // "nicht evaluiert"
                    UtilGrid.setTableData("ref1SpatialSystem", [{title:"EPSG 25832: ETRS89 / UTM Zone 32N"}]);
                    registry.byId("availabilityDataFormatInspire").set("value", "Geographic Markup Language (GML)");
                }
            });
        }

    };

    openData.run();
});
