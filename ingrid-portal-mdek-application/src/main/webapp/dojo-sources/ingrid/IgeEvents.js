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
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/Deferred",
    "dojo/DeferredList",
    "dojo/ready",
    "dojo/window",
    "dojo/topic",
    "dojo/string",
    "dojo/dom",
    "dojo/query",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/store/Memory",
    "dijit/registry",
    "ingrid/utils/UI",
    "ingrid/utils/List",
    "ingrid/utils/Address",
    "ingrid/utils/Syslist",
    "ingrid/utils/Catalog",
    "ingrid/utils/Grid",
    "ingrid/utils/Thesaurus",
    "ingrid/utils/General",
    "ingrid/utils/String",
    "ingrid/hierarchy/rules",
    "ingrid/hierarchy/dirty",
    "ingrid/message",
    "ingrid/dialog"
], function(declare, array, lang, Deferred, DeferredList, ready, wnd, topic, string, dom, query, domClass, style, Memory, registry,
    UtilUI, UtilList, UtilAddress, UtilSyslist, UtilCatalog, UtilGrid, UtilThesaurus, UtilGeneral, UtilString,
    rules, dirty, message, dialog) {

    var lib = declare(null, {

        previousClass: null,

        global: this,

        toggleContainer: ["refClass1", "refClass1DQ", "refClass2", "refClass3", "refClass4", "refClass5", "refClass6"],
        toggleContainerPrefix: "ref",
        toggleContainerAddress: ["headerAddressType0", "headerAddressType1", "headerAddressType2", "headerAddressType3"],
        toggleContainerAddressPrefix: "header",

        handleSelectNode: function(message) {
            if (!message.node || message.id != "dataTree") return;

            if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
                style.set("sectionTopObject", "display", "none");
                style.set("contentFrameBodyObject", "display", "none");
                style.set("sectionTopAddress", "display", "none");
                style.set("contentFrameBodyAddress", "display", "none");
                style.set("contentNone", "display", "block");
            } else if (message.node.nodeAppType == "A") {
                style.set("sectionTopObject", "display", "none");
                style.set("contentFrameBodyObject", "display", "none");
                style.set("sectionTopAddress", "display", "block");
                style.set("contentFrameBodyAddress", "display", "block");
                style.set("contentNone", "display", "none");
            } else if (message.node.nodeAppType == "O") {
                style.set("sectionTopObject", "display", "block");
                style.set("contentFrameBodyObject", "display", "block");
                style.set("sectionTopAddress", "display", "none");
                style.set("contentFrameBodyAddress", "display", "none");
                style.set("contentNone", "display", "none");
            }
        },

        selectUDKClass: function(val) {
            if (val) {
                this.setSelectedClass(val);
                topic.publish("/onObjectClassChange", {
                    objClass: val,
                    previousClass: this.previousClass
                });
                var self = this;
                setTimeout(function() {
                    self.refreshTabContainers("contentFrameBodyObject");
                }, 100);
                this.previousClass = val;
            }
        },

        selectUDKAddressType: function(addressType) {
            console.debug("change addressType" + addressType);
            var val = UtilAddress.getAddressClass(addressType);
            if (val != -1) {
                this.setSelectedClass("AddressType" + val);
                var self = this;
                setTimeout(function() {
                    self.refreshTabContainers("contentFrameBodyAddress");
                }, 100);
            }
        },

        setSelectedClass: function( /* name of the object class/address type */ clazz) {
            console.debug("selected class: " + clazz);
            var div;
            var isObjectClass = true;
            // hide all classes first
            // and show new one
            if (clazz.indexOf("AddressType") == -1) {
                array.forEach(this.toggleContainer, function(container) {
                    domClass.add(container, "hide");
                });
                div = dom.byId(this.toggleContainerPrefix + clazz);
            } else {
                array.forEach(this.toggleContainerAddress, function(container) {
                    domClass.add(container, "hide");
                });
                div = dom.byId(this.toggleContainerAddressPrefix + clazz);
                isObjectClass = false;
            }

            //var div = dom.byId(toggleContainerPrefix + clazz);
            // Class0 not exists as widget (means no Fachbezug) so we have to check here
            if (div)
                domClass.remove(div, "hide");

            // hide section 'Verfuegbarkeit' if 'Organisationseinheit/Fachaufgabe' (Class0) is selected
            // fields do not need to be emptied, since it's not mapped in MDEK-Mapper for class 0! 
            var availabilityContainer = dom.byId('availability');
            if (availabilityContainer) {
                if (clazz == "Class0")
                    domClass.add(availabilityContainer, "hide");
                else if (isObjectClass)
                    domClass.remove(availabilityContainer, "hide");
            }

            // show conformity-table only for class 1 and 3 
            if (clazz == "Class1" || clazz == "Class3") {
                domClass.remove("uiElementN024", "hide");
                domClass.add("uiElementN024", "required");
            } else if (isObjectClass) {
                domClass.remove("uiElementN024", "required");
                domClass.add("uiElementN024", "hide");
                // Here we also delete DATA !!!! Necessary ???
                UtilGrid.setTableData("extraInfoConformityTable", []);
            }

            // class specials !

            // Fields only mandatory for Geoinformation/Karte(1)
            // NOTICE: div excluded from normal show/hide mechanism (displaytype="exclude")
            if (clazz == "Class1") {
                // "Kodierungsschema der geographischen Daten" 
                UtilUI.setMandatory(dom.byId("uiElement1315"));

                // show / hide DQ input dependent from INSPIRE Thema !
                rules.applyRule7();

            } else if (isObjectClass) {
                // "Kodierungsschema der geographischen Daten" only in class 1
                UtilUI.setHide(dom.byId("uiElement1315"));
                registry.byId("availabilityDataFormatInspire").set("value", "");
            }

            // Fields only mandatory for Geoinformation/Karte(1) and Geodatendienst(3)
            if (clazz == "Class1" || clazz == "Class3") {
                // "Raumbezugssystem"
                UtilUI.setMandatory(dom.byId("uiElement3500"));
                //style("uiElement3500", "display", "block");

                // change general address label
                this.setGeneralAddressLabel(true);

                // after an object is loaded or class was changed we need to run external actions if any
                topic.publish("/onInspireTopicChange", {
                    topics: UtilList.tableDataToList(registry.byId("thesaurusInspire").getData())
                });

            } else if (isObjectClass) {
                // DO NOT HIDE to avoid vanishing field ...
                UtilUI.setOptional(dom.byId("uiElement3500"));

                // change general address label
                this.setGeneralAddressLabel(false);
            }
        },

        setGeneralAddressLabel: function( /*boolean*/ isClass1Or3) {
            if (isClass1Or3)
                dom.byId("generalAddressTableLabelText").innerHTML = message.get('ui.obj.general.addressTable.title.class1And3');
            else
                dom.byId("generalAddressTableLabelText").innerHTML = message.get('ui.obj.general.addressTable.title');
        },

        refreshTabContainers: function(section) {
            query(".rubric:not(.hide) span:not(.hide) .dijitTabContainer", section).forEach(function(node) {
                registry.byId(node.id).resize();
            });
        },

        toggleFields: function(section, /* optional */ mode, /* optional flag */ refreshContainers) {
            //  dojo.debug("toggleFields("+section+", "+mode+")");

            if (typeof(refreshContainers) == "undefined") {
                refreshContainers = true;
            }

            var sectionElement;

            if (typeof(section) != "undefined" && section != "Object" && section != "Address") {
                sectionElement = dom.byId(section);
                // var allSpanElements = sectionElement.getElementsByTagName("span");
                // var allDivElements = sectionElement.getElementsByTagName("div");

                //      dojo.debug("number of div elements: "+allDivElements.length);
                //      dojo.debug("number of span elements: "+allSpanElements.length);

                if (typeof(mode) == "undefined") {
                    if (domClass.contains(sectionElement, "expanded")) {
                        mode = "showRequired";
                    } else {
                        mode = "showAll";
                    }
                }

                if (mode == "showAll") {
                    domClass.add(sectionElement, "expanded");
                    // refresh grids and tab container when making visible
                    query(".optional .dijitTabContainer", sectionElement).forEach(function(node) {
                        registry.byId(node.id).resize();
                    });
                    // make sure the grids are rendered correctly
                    query(".ui-widget", sectionElement).forEach(function(node) {
                        registry.byId(node.id).reinitLastColumn();
                    });

                } else if (mode == "showRequired") {
                    domClass.remove(sectionElement, "expanded");
                }

                //      dojo.debug("mode: "+mode);
                //      dojo.debug("sectionElement will be expanded: "+sectionElement.isExpanded);

                if (sectionElement.getElementsByTagName('img')[0] && sectionElement.getElementsByTagName('img')[0].src.indexOf("expand") != -1) {
                    //var btnImage = sectionElement.getElementsByTagName('img')[0];
                    //var link = sectionElement.getElementsByTagName('a')[0];
                    this.toggleButton(section, mode);
                }

            } else {
                var sectionDivId;

                if (section == "Address" || this.global.currentUdk && this.global.currentUdk.nodeAppType == "A") {
                    sectionDivId = "contentFrameBodyAddress";
                } else {
                    sectionDivId = "contentFrameBodyObject";
                }
                var sectionDiv = dom.byId(sectionDivId);

                if (!mode) {
                    if (sectionDiv.isExpanded === false || typeof(sectionDiv.isExpanded) == "undefined") {
                        mode = "showAll";
                        sectionDiv.isExpanded = true;

                    } else {
                        mode = "showRequired";
                        sectionDiv.isExpanded = false;
                    }
                }

                //var toggleBtn = registry.byId('toggleFieldsBtn');
                //var btnImage = toggleBtn.domNode.getElementsByTagName('img')[0];
                this.toggleButton(section, mode);

                var self = this;
                query(".rubric:not(.hide)", sectionDivId).forEach(function(section) {
                    //          console.log("calling toggleFields("+section.id+", "+mode+").");
                    self.toggleFields(section.id, mode, false);
                });

                return;
            }

        },

        toggleButton: function(section, mode) {
            if (!section) {
                var divIcon = query("#toggleFieldsBtn .tabIconExpand")[0];
                if (mode == "showAll") {
                    domClass.add(divIcon, "tabIconExpandOn");
                } else {
                    domClass.remove(divIcon, "tabIconExpandOn");
                }
            }
        },

        // pass termList: list of strings or [] in JS
        // pass caller: e.g. { id:"getCapabilitiesWizard", _termListWidget:"thesaurusTerms" }
        addKeywords: function(termList, caller) {
            var defKeywords = new Deferred();
            var inspireTopics = [];

            // For object search terms...
            if ((caller.id == "thesaurusFreeTermsAddButton" || caller.id == "getCapabilitiesWizard") &&
                termList &&
                termList.length > 0) {

                // is Inspire-table available in this class?
                var hasInspireTable = !domClass.contains("uiElement5064", "hide");

                // Check if the termList contains inspire topics
                // If so, tell the user and wait for confirmation. Then proceed with the normal search
                if (hasInspireTable) {
                    termList = array.filter(termList, function(t) {
                        var iTopic = UtilThesaurus.isInspireTopic(t);
                        if (iTopic) {
                            inspireTopics.push(iTopic);
                            return false;
                        } else {
                            return true;
                        }
                    });
                }
            }
            console.debug("term list: " + termList);
            console.debug("inspire topics: " + inspireTopics);

            var def = new Deferred();
            // add inspire topics to the list
            if (inspireTopics.length > 0) {
                UtilThesaurus.addInspireTopics(inspireTopics);
                var displayText = string.substitute(message.get("dialog.addInspireTopics.message"), [inspireTopics.join(", ")]);
                dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.INFO, [{
                    caption: message.get("general.ok"),
                    action: function() {
                        return def.resolve();
                    }
                }]);
                //        }
            } else {
                // Otherwise just execute the callback to continue
                def.resolve();
            }

            if (termList && termList.length > 0) {
                var self = this;
                def.then(function() {
                    // search for topics in SNS and put results into findTopicDefList
                    self.analyzeKeywords(termList).then(function(topics) {
                        UtilThesaurus.handleFindTopicsResult(termList, topics, caller._termListWidget);
                        defKeywords.resolve();
                    });
                });
            } else {
                defKeywords.resolve();
            }

            // expand rubric so that new entries are visible
            this.toggleFields('thesaurus', "showAll");
            return defKeywords;
        },

        analyzeKeywords: function(termList) {
            var def = new Deferred();
            if (termList && termList.length > 0) {
                var findTopicDefList = [];
                UtilUI.initBlockerDivInfo("keywords", termList.length, message.get("general.add.keywords"));
                for (var i = 0; i < termList.length; ++i) {
                    findTopicDefList.push(UtilThesaurus.findTopicsDef(termList[i]));
                }

                UtilUI.enterLoadingState();
                var self = this;
                new DeferredList(findTopicDefList, false, false, true)
                .then(function(resultList) {
                    UtilUI.exitLoadingState();
                    var snsTopicList = [];
                    array.forEach(resultList, function(item) {
                        // TODO handle sns timeout errors?
                        snsTopicList.push(item[1]);
                    });
                    def.resolve(snsTopicList);
                }, function(err) {
                    UtilUI.exitLoadingState();
                    self.handleFindTopicsError(err);
                    def.reject();
                });

            } else {
                def.resolve([]);
            }
            return def;
        },

        setObjectUuid: function() {
            var fieldWidget = this._inputFieldWidget;
            var fieldValue = fieldWidget.getValue();

            // extract namespace from field content (Namespace#ID)
            var myNamespace = "";
            var fieldValues = fieldValue.split("#");
            if (fieldValues.length > 1) {
                myNamespace = fieldValues[0] + "#";
            }

            // generate UUID
            var def = UtilGeneral.generateUUID();
            def.then(function(uuid) {
                var newValue = myNamespace + uuid;
                // and set in field
                fieldWidget.attr("value", newValue, true);
            });
        },


        //        UtilUI._uiElementsActiveA = true;
        //        UtilUI._uiElementsActiveO = true;
        disableInputOnWrongPermission: function(node) {
            var message = {
                node: node
            };
            var hasWritePermission = message.node.writePermission;
            console.debug("received notification: handle permission on form: " + hasWritePermission);

            var nodeType = message.node.nodeAppType.toUpperCase();
            var activeDiv = "contentFrameBodyObject";

            if (nodeType == "A")
                activeDiv = "contentFrameBodyAddress";
            else if (nodeType != "O")
                return;

            // TODO: check if the query still does what it is supposed to do!!!
            // are there non-div elemente sometimes???
            var allInputs = query(
                ".input > div, .input > textarea," +       // normal inputs and textareas
                ".input > table.dijitSelect," +            // SELECT box
                ".dijitTabPaneWrapper textarea," +         // text area within tab panes
                ".input > table .dijitTextBox", activeDiv); // additional field number spinner

            var elementsActive = UtilUI["_uiElementsActive" + nodeType];
            if (hasWritePermission) {
                if (elementsActive === false) {
                    // elements that always shall be disabled
                    var ignoreElements = ["headerAddressType1Institution", "headerAddressType2Institution"];

                    array.forEach(allInputs, function(input) {
                        if (array.indexOf(ignoreElements, input.id) != -1)
                            return;

                        if (UtilGrid.getTable(input.id)) {
                            UtilGrid.updateOption(input.id, "editable", true);
                            domClass.remove(input.id, "disabled");
                        } else {
                            var widget = registry.getEnclosingWidget(input);
                            // is it a table element then disable input differently
                            widget.set("disabled", false);
                        }
                    });

                    // enable links
                    query(".functionalLink a", activeDiv).forEach(UtilUI.enableHtmlLink);

                    // enable all buttons
                    this._toggleButtonsAccessibility(false);

                    // enable header
                    this._toggleHeaderAccessibility(false);

                    UtilUI["_uiElementsActive" + nodeType] = true;
                }

            } else {
                if (elementsActive === undefined || elementsActive === true) {
                    //var allInputs = query(".required .input >, .optional .input >, .show .input >", activeDiv);
                    array.forEach(allInputs, function(input) {
                        if (UtilGrid.getTable(input.id)) {
                            UtilGrid.updateOption(input.id, "editable", false);
                            domClass.add(input.id, "disabled");
                        } else {
                            var widget = registry.getEnclosingWidget(input);
                            // is it a table element then disable input differently
                            widget.set("disabled", true);
                        }
                    });

                    // disable links
                    query(".functionalLink a", activeDiv).forEach(UtilUI.disableHtmlLink);

                    // disable all buttons
                    this._toggleButtonsAccessibility(true);

                    // disable header
                    this._toggleHeaderAccessibility(true);

                    UtilUI["_uiElementsActive" + nodeType] = false;
                }
            }
        },

        _toggleButtonsAccessibility: function( /*boolean*/ disable) {
            var buttons = query(".dijitButton", "contentFrameBodyObject");
            array.forEach(buttons, function(button) {
                var widget = registry.byId(button.getAttribute("widgetid"));
                if (widget)
                    widget.set("disabled", disable);
            });
        },

        _toggleHeaderAccessibility: function( /*boolean*/ disable) {
            var widgets = ["objectName", "objectClass", "objectOwner", /*"addressTitle","addressType",*/ "addressOwner"];

            array.forEach(widgets, function(w) {
                var wid = registry.byId(w);
                if (wid)
                    wid.set("disabled", disable);
            });
        },

        nextError: 0,
        showNextError: function() {
            var errors = query(".importantBackground");

            if (errors === 0) {
                // remove error message
                return;
            }

            // scroll to the position
            wnd.scrollIntoView(errors[this.nextError % errors.length]);

            // update error menu entry in toolbar

        },

        addServiceLink: function() {
            // check for changes that need to be saved first
            var def = require("ingrid/IgeActions").checkForUnsavedChanges(null, message.get("dialog.save.for.remote.reference"));
            // TODO: check this error-function!!!
            var loadErrback = function() {
                if (typeof(resultHandler) != "undefined") {
                    resultHandler.reject("Vor Auswahl des Dienst-Objektes bitte Speichern.");
                }
            };

            var readyToLink = function(result) {
                if (result === undefined || result == "SAVE" || result == "DISCARD") {
                    var def2 = new Deferred();
                    // open dialog to choose service from
                    // check mdek_hierarchy.jsp for called sub-functions and variables
                    var caller = {
                        gridId: 'ref1ServiceLink'
                    };
                    dialog.showPage(message.get("dialog.links.selectObject.title"), 'dialogs/mdek_links_select_object_dialog.jsp?c=' + userLocale, 522, 520, true, {
                        // custom parameters
                        additionalText: message.get("dialog.links.selectObject.coupling.info"),
                        resultHandler: def2,
                        caller: caller
                    });

                    var self = this;
                    def2.then(function(obj) {
                        console.debug("It's time to jump!");
                        var oldObject = self.global.currentUdk;
                        // jump to selected service if "submit" button was pressed (ignoring the change)
                        var defOpenRemoteObject = require("ingrid/menu").handleSelectNodeInTree(obj.uuid, "O");

                        // add data object to link table using complete reference where UUID was exchange
                        defOpenRemoteObject.then(function() {
                            console.debug("remote object opened and loaded");
                            var currentLink = obj;
                            currentLink.uuid = oldObject.uuid;
                            currentLink.title = oldObject.title;
                            currentLink.objectClass = "1";
                            currentLink.relationType = "3600";
                            currentLink.relationTypeName = "Verweis zu Daten";
                            UtilList.addObjectLinkLabels([currentLink], true);
                            UtilList.addIcons([currentLink]);
                            UtilGrid.addTableDataRow("ref3BaseDataLink", currentLink);

                            // also add to "global" link table in "Verweise"
                            // -> not anymore INGRID32-190
                            // UtilGrid.addTableDataRow("linksTo", currentLink);

                            // open section and jump to table and set dirty flag!
                            setTimeout(function() {
                                self.toggleFields('refClass3', 'showAll');
                                wnd.scrollIntoView("ref3BaseDataLink");
                                dirty.setDirtyFlag();
                            }, 200);

                            // show an info about what just happened
                            dialog.show(message.get("dialog.remoteReferenceChange"), string.substitute(message.get("dialog.remoteReferenceChange.text"), [self.global.currentUdk.title, oldObject.title]), dialog.INFO);
                        });
                    },
                    // dialog was cancelled -> IS NOT CALLED WHEN CLICKED ON 'X'!!!
                    function() {
                        console.debug("Dialog closed without selection of a service.");
                    });
                }
            };

            def.then(lang.hitch(this, readyToLink), loadErrback);
        },

        addDataLink: function() {
            var def = new Deferred();

            var caller = {
                gridId: 'ref3BaseDataLink'
            };
            
            dialog.showPage(message.get("dialog.links.selectObject.title"), 'dialogs/mdek_links_select_object_dialog.jsp?c=' + userLocale, 522, 520, true, {
                // custom parameters
                //additionalText: message.get("dialog.links.selectObject.coupling.info"),
                resultHandler: def,
                caller: caller
            });

            def.then(function(obj) {
                    var currentLink = obj;
                    currentLink.relationType = 3600;
                    currentLink.relationTypeName = "Verweis zu Daten";
                    UtilList.addObjectLinkLabels([currentLink], true);
                    UtilList.addIcons([currentLink]);
                    UtilGrid.addTableDataRow("ref3BaseDataLink", currentLink);

                    // also add to "global" link table in "Verweise"
                    // -> not anymore INGRID32-190
                    // UtilGrid.addTableDataRow("linksTo", currentLink);
                },
                // dialog was cancelled -> IS NOT CALLED WHEN CLICKED ON 'X'!!!
                function() {
                    console.debug("Dialog closed without selection of a data object.");
                });
        },

        getCapabilities: function(url, scope) {
            var def = new Deferred();
            GetCapabilitiesService.getCapabilities(url, {
                preHook: scope.showLoadingZone,
                postHook: scope.hideLoadingZone,
                callback: function(res) {
                    def.resolve(res);
                },
                errorHandler: function(errMsg, err) {
                    scope.hideLoadingZone();
                    console.error("Error: " + errMsg);
                    displayErrorMessage(err);
                    def.reject();
                }
            });
            return def.promise;
        },

        getCapabilityUrl: function() {
            var url = null;
            var data = UtilGrid.getTableData("ref3Operation");
            var type = registry.byId("ref3ServiceType").value;
            array.some(data, function(item) {
                if (item.name == "GetCapabilities") {
                    // check parameters for service type
                    var serviceType = null;
                    array.some(item.paramList, function(param) {
                        if (param.description == "Service type") {
                            serviceType = param.name; // e.g. "SERVICE=CSW"
                            return true;
                        }
                    });
                    url = item.addressList[0].title;
                    url = UtilString.addCapabilitiesParameter(type, url, serviceType);
                }
            });

            return url;
        },


        /**
         * The filter is created by the getCapabilities assistant which defines, which values are
         * used from the bean to update the metadata.
         */
        _updateFormFromCapabilities: function(capBean, filter) {
            registry.byId("generalShortDesc").setValue(capBean.title);
            var serviceTitle = lang.trim(capBean.title + "");
            if (serviceTitle.length != 0) {
                registry.byId("objectName").setValue(serviceTitle);
            }

            registry.byId("generalDesc").setValue(capBean.description);
        },

        updateDataset: function() {
            var url = this.getCapabilityUrl();

            if (!url) {
                dialog.show(message.get('general.hint'), message.get('hint.noCapabilityUrlFound'), dialog.INFO);
                return;
            }

            dialog.showPage("<fmt:message key='dialog.wizard.getCap.title' />", "dialogs/mdek_get_capabilities_wizard_dialog.jsp", 755, 750, true, {
                url: url
            });

            /*
            var def = new Deferred();
            dialog.show(message.get("general.warning"), message.get('warning.update.capabilities'), dialog.WARNING, [
                { caption: message.get("general.cancel"),  action: function() { } },
                { caption: message.get("general.ok"), action: function() { def.resolve(); } }
            ]);    
            
            def.then(function() {
            
                showLoadingZone = function() {dom.byId('updateGetCapLoadingZone').style.visibility = "visible";}
                hideLoadingZone = function() {dom.byId('updateGetCapLoadingZone').style.visibility = "hidden";}
                
                var setOperationValues = function(capBean) {
                    _updateFormFromCapabilities(capBean);
                    dialog.show(message.get('general.hint'), message.get('hint.datasetUpdatedFromCapabilities'), dialog.INFO);
                }
                
                getCapabilities(url, setOperationValues);
            });*/
        },

        getLinksToFromParent: function() {
            // load parent object
            var self = this;
            ObjectService.getNodeData(this.global.currentUdk.parentUuid, "O", "true", {
                //preHook: dojo.partial(UtilDWR.enterLoadingState, "updateGetLinksToFromParent"),
                //postHook: dojo.partial(UtilDWR.exitLoadingState, "updateGetLinksToFromParent"),
                callback: function(objNodeData) {
                    // get linksTo-references
                    var linkTableData = self._prepareObjectAndUrlReferences(objNodeData);

                    // add parent references to current object
                    // request UNFILTERED data ! Get full data store !
                    var data = UtilGrid.getTableData("linksTo", true);
                    // filter those entries that are not already present in the current object
                    var entriesToAdd = array.filter(linkTableData, function(item) {
                        if (!array.some(data,
                            function(d) {
                                var sameRelationType = (d.relationTypeName == item.relationTypeName);
                                var sameEntity = null;
                                if (d.url) {
                                    sameEntity = (d.url == item.url);
                                } else {
                                    sameEntity = (d.uuid == item.uuid);
                                }
                                return (sameRelationType && sameEntity);
                            })) {
                            return true;
                        }
                        return false;
                    });


                    if (entriesToAdd.length === 0) {
                        UtilUI.showToolTip("linksTo", message.get('hint.noEntriesFromParent'));
                    } else {
                        // add entries
                        array.forEach(entriesToAdd, function(entry) {
                            UtilGrid.addTableDataRow("linksTo", entry);
                        });
                    }
                }
            });
        },

        getSpatialRefLocationFromParent: function() {
            // load parent object
            ObjectService.getNodeData(this.global.currentUdk.parentUuid, "O", "true", {
                //preHook: dojo.partial(UtilDWR.enterLoadingState, "updateGetSpatialRefLocationFromParent"),
                //postHook: dojo.partial(UtilDWR.exitLoadingState, "updateGetSpatialRefLocationFromParent"),
                callback: function(objNodeData) {
                    // get linksTo-references
                    var spatialTableData = objNodeData.spatialRefLocationTable;

                    // add parent references to current object
                    var data = UtilGrid.getTableData("spatialRefLocation");
                    // filter those entries that are not already present in the current object
                    var entriesToAdd = array.filter(spatialTableData, function(item) {
                        if (!array.some(data, function(d) {
                            return d.name == item.name;
                        })) {
                            return true;
                        }
                        return false;
                    });


                    if (entriesToAdd.length === 0) {
                        UtilUI.showToolTip("spatialRefLocation", message.get('hint.noEntriesFromParent'));
                    } else {
                        // add entries
                        array.forEach(entriesToAdd, function(entry) {
                            UtilGrid.addTableDataRow("spatialRefLocation", entry);
                        });
                    }
                }
            });
        },

        getAddressDataFromParent: function() {
            var def = new Deferred();
            dialog.show(message.get("general.warning"), message.get('warning.address.inherit'), dialog.WARNING, [{
                caption: message.get("general.cancel"),
                action: function() {}
            }, {
                caption: message.get("general.ok"),
                action: function() {
                    def.resolve();
                }
            }]);

            var self = this;
            def.then(function() {
                // load parent address
                AddressService.getAddressData(self.global.currentUdk.parentUuid, "true", {
                    callback: function(addrNodeData) {

                        registry.byId("addressStreet").attr("value", addrNodeData.street, true);
                        registry.byId("addressCountry").attr("value", addrNodeData.countryCode == -1 ? null : addrNodeData.countryCode, true);
                        registry.byId("addressZipCode").attr("value", addrNodeData.postalCode, true);
                        registry.byId("addressCity").attr("value", addrNodeData.city, true);
                        registry.byId("addressPOBox").attr("value", addrNodeData.pobox, true);
                        registry.byId("addressZipPOBox").attr("value", addrNodeData.poboxPostalCode, true);

                        UtilUI.showToolTip("spatialRefLocation", message.get('hint.addressFromParentAdded'));
                    }
                });
            });
        },

        _prepareObjectAndUrlReferences: function(nodeData) {
            var objLinkTable = nodeData.linksToObjectTable;
            var urlLinkTable = nodeData.linksToUrlTable;

            var url = this._filterPreviewImage(urlLinkTable);
            registry.byId("generalPreviewImage").set("value", url);

            var linkTable = objLinkTable.concat(urlLinkTable);
            // Replace relationTypeName with name from according syslist entry. Leave it if it's a free entry.
            array.forEach(linkTable, function(entry) {
                var entryName = UtilSyslist.getSyslistEntryName(2000, entry.relationType);
                if (entryName != entry.relationType) {
                    entry.relationTypeName = entryName;
                }
            });

            UtilList.addObjectLinkLabels(linkTable, true);
            UtilList.addUrlLinkLabels(linkTable);
            UtilList.addIcons(linkTable);

            return linkTable;
        },

        _filterPreviewImage: function(urlList) {
            var foundObjectIndex = null;
            array.some(urlList, function(urlObject, index) {
                if (urlObject.relationType == 9000) {
                    foundObjectIndex = index;
                    return true;
                }
                return false;
            });

            if (foundObjectIndex !== null) {
                var url = urlList[foundObjectIndex].url;
                urlList.splice(foundObjectIndex, 1);
                return url;
            } else {
                return "";
            }
        },

        // load content when object class changes ! Also triggered on initial setting of object class !
        setLinksToRelationTypeFilterContent: function(fullObjectClass) {
            // Class1, ... is passed
            var objectClass = fullObjectClass.objClass.substring(5);

            // set up syslist data and filter according to object class
            UtilSyslist.readSysListData(2000).then(function(listItems) {
                var syslist2000Items = UtilSyslist.convertSysListToTableData(listItems);

                var idList = [];
                array.forEach(syslist2000Items, function(entry) {
                    // "data" of list item contains relevant classes
                    if (entry.data) {
                        var containsClass = array.indexOf(entry.data.split(','), objectClass) !== -1;
                        if (containsClass) idList.push(entry.entryId);
                    }
                });

                var initialItems = [{
                    // add " " prefix to be first entry in list !
                    name: " " + message.get("ui.listentry.noFilter"),
                    // "0" not used in syslist !
                    entryId: "0",
                    isDefault: true,
                    // We set this entry for all classes (to be conform with data field, but not needed anymore)
                    data: "0,1,2,3,4,5,6"
                }];
                var filteredItems = array.filter(syslist2000Items, function(item) {
                    return array.some(idList, function(id) {
                        return id == item.entryId;
                    });
                });

                // Set new items in store
                var newItems = initialItems.concat(filteredItems);
                var storeProps = {
                    searchAttr: "name",
                    data: {
                        label: "name",
                        identifier: "entryId",
                        items: newItems
                    }
                };
                registry.byId("linksToRelationTypeFilter").set("store", new Memory(storeProps));
                //            console.debug("/onObjectClassChange -> New items for relation type filter");
                //            console.debug(newItems);
            });
        },

        filterLinksToViaRelationType: function(filterKey) {
            console.debug("filterLinksToViaRelationType relationType: " + filterKey);

            if (filterKey == "0") {
                // "Kein Filter" selected
                UtilGrid.getTable("linksTo").setRowFilter(null);
            } else if (UtilGeneral.hasValue(filterKey)) {
                UtilGrid.getTable("linksTo").setRowFilter({
                    relationType: filterKey
                });
            } else {
                // undefined state at initialization
                UtilGrid.getTable("linksTo").setRowFilter(null);
            }

            //    console.debug("Table FULL Data ->");
            //    console.debug(UtilGrid.getTable("linksTo").getData(true));
            //    console.debug("Table FILTERED Data ->");
            //    console.debug(UtilGrid.getTable("linksTo").getData());

            UtilGrid.getTable("linksTo").invalidate();
        }
    })();

    // backward compatibility
    igeEvents = {
        refreshTabContainers: lib.refreshTabContainers
    };

    return lib;
});