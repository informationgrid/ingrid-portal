/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/has",
    "dojo/on",
    "dojo/aspect",
    "dojo/query",
    "dojo/Deferred",
    "dojo/topic",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojox/validate",
    "dojox/widget/Standby",
    "dijit/registry",
    "dijit/Tooltip",
    "dijit/form/Button",
    "dijit/form/ValidationTextBox",
    "dijit/form/SimpleTextarea",
    "dijit/form/CheckBox",
    "dijit/form/RadioButton",
    "dijit/form/NumberTextBox",
    "dijit/form/DateTextBox",
    "dijit/layout/TabContainer",
    "dijit/layout/ContentPane",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist",
    "ingrid/utils/List",
    "ingrid/utils/Grid",
    "ingrid/utils/Thesaurus",
    "ingrid/utils/Catalog",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/layoutCreator",
    "ingrid/hierarchy/rules",
    "ingrid/hierarchy/dirty",
    "ingrid/hierarchy/behaviours.user",
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/validation"
], function(declare, lang, array, has, on, aspect, query, Deferred, topic, dom, domClass, style, validate, Standby,
            registry, Tooltip, Button, ValidationTextBox, SimpleTextarea, CheckBox, RadioButton, NumberTextBox, DateTextBox,
            TabContainer, ContentPane,
            UtilUI, UtilSyslist, UtilList, UtilGrid, UtilThesaurus, UtilCatalog,
            message, dialog, layoutCreator, rules, dirty, behaviour, igeEvents, gridEditors, gridFormatters, validator) {

        return declare(null, {
            objectTemplateCreated: false,

            deferredCreation: null,

            additionalFieldWidgets: [],

            create: function() {
                // clean up and just return deferredCreation promise without the need
                // of objectTemplateCreated!
                this.deferredCreation = new Deferred();
                if (this.objectTemplateCreated) {
                    this.deferredCreation.resolve();
                    return this.deferredCreation;
                }

                domClass.add("loadBlockDiv", "blockerFull");

                // mark the object template as already created so that it is
                // created only once
                this.objectTemplateCreated = true;

                UtilUI.initBlockerDivInfo("createObjects", 2, message.get("general.init"));
                UtilUI.updateBlockerDivInfo("createObjects");
                setTimeout(lang.hitch(this, this.createPartOne), 30);
                return this.deferredCreation;
            },

            createPartOne: function() {

                console.debug("create form elements for objects");

                this.createInfoHeader();

                this.createGeneralInfo();

                this.createFachBezugClass1();

                this.createRefClass1DQ();

                this.createFachBezugClass2();

                this.createFachBezugClass3();

                this.createFachBezugClass4();

                this.createFachBezugClass5();

                this.createFachBezugClass6();

                console.debug("createRaumbezug");
                this.createRaumbezug();

                this.createZeitbezug();

                console.debug("createExtraInfo");
                this.createExtraInfo();

                this.createAvailability();

                console.debug("createThesaurus");
                this.createThesaurus();

                this.createReferences();
                console.debug("initAdditionalFields");
                var defAddFields = this.initAdditionalFields()
                    .then(this.connectDirtyFlagsEvents);
                
                console.debug("init CTS");
                // apply atomatic transformation of bounding box if selected in table
                this.initCTS();

                // apply special validation for necessary components
                console.debug("apply Validations");
                var self = this;
                defAddFields.then(this.applyDefaultConnections)
                .then(setVisibilityOfFields)
                .then(igeEvents.selectUDKClass) // update view according to initial chosen class
                .then(function() {
                    // add a '*' to all labels and display them if an element is required 
                    query(".outer label", "contentFrameBodyObject").forEach(function(item) {
                        item.innerHTML = lang.trim(item.innerHTML) + '<span class=\"requiredSign\">*</span>';
                    });
                    
                    // mark all the content of a special marked container 
                    query(".oneClickMark", "dataFormContainer").on("click", function() {
                        UtilUI.selectTextInContainer( this );
                    });

                    console.debug("toggle");
                    // show only required fields initially
                    igeEvents.toggleFields(undefined, "showRequired");
                    UtilUI.updateBlockerDivInfo("createObjects");

                    // tell the calling function that we are finished and can proceed
                    self.deferredCreation.resolve();

                    domClass.remove("loadBlockDiv", "blockerFull");
                })
                .then(null, function(err) {
                    console.error("Error", err);
                    displayErrorMessage(err);
                });

            },

            createInfoHeader: function() {
                var objectName = new ValidationTextBox({
                    maxLength: 255,
                    style: "width:100%;"
                }, "objectName");
                on( objectName, "keyup", function() {
                    if (this.displayedValue.length === 255) {
                        console.warn("It's not possible to add more than 255 characters for object name.");
                        Tooltip.show(message.get("general.field.too.long.255"), this.domNode, ["below"]);
                    }
                });

                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createSelectBox("objectClass", null, storeProps, function() {
                    // add a prefix for each value to stay compatible!
                    return UtilSyslist.getSyslistEntry(UtilSyslist.listIdObjectClass, "Class");
                });
                registry.byId("objectClass").onChange = lang.hitch(igeEvents, igeEvents.selectUDKClass);

                layoutCreator.createFilteringSelect("objectOwner", null, storeProps, null);
                var owner = registry.byId("objectOwner");
                
                // show a busy state when lazy loading data
                var standby = new Standby({target: "objectOwner"});
                document.body.appendChild(standby.domNode);
            	standby.startup();
                
                /* make select box lazy loading */
                owner.isLoaded = function() {
	            	return this._isLoaded;
	            }
                owner._startSearchFromInput = function() {
                	this.item = undefined;
	            	if (this.isLoaded()) {
	            		this._startSearch(this.focusNode.value);
	            	} else {
	            		standby.show();
	            		UtilCatalog.updateResponsibleUserObjectList(currentUdk).then( lang.hitch( this, function() {
	            			standby.hide();
		            		this._isLoaded = true;
		            		this._startSearch(this.focusNode.value);
	            		} ) );
	            	}
	            }
                owner.loadDropDown = function(/*Function*/ loadCallback){
                	standby.show();
                	UtilCatalog.updateResponsibleUserObjectList(currentUdk).then( lang.hitch( this, function() {
                		standby.hide();
	            		this._isLoaded = true;
	            		this._startSearchAll(); 
	            	} ) );
	    		};

                new ValidationTextBox({
                    maxLength: 255,
                    style: "width:100%;"
                }, "parentIdentifier");
                
            },

            createGeneralInfo: function() {
                new ValidationTextBox({
                    maxLength: 255,
                    style: "width:100%;"
                }, "generalShortDesc");

                new SimpleTextarea({
                    //required: true, // will be handled by ourselves
                    rows: 10,
                    style: "width: 100%;"
                }, "generalDesc");

                var structure = [{
                    field: 'nameOfRelation',
                    name: 'name',
                    width: '120px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    //values: [],
                    editable: true,
                    listId: 505
                    //!!!			formatter: validator.emptyOrNullValidation
                    /*
                    formatter: function(value){
                        return UtilList.getSelectDisplayValue(this, value);
                    }*/
                }, {
                    field: 'icon',
                    name: 'i',
                    width: '23px',
                    editable: false /*!!!formatter: validator.emptyOrNullValidation*/
                }, {
                    field: 'linkLabel',
                    name: 'label',
                    width: '565px',
                    editable: false /*!!!formatter: validator.emptyOrNullValidation*/
                }];

                console.debug("generalAddress");
                layoutCreator.createDataGrid("generalAddress", null, structure, null);

                var previewImage = new ValidationTextBox({
                    maxLength: 1024,
                    style: "width:100%;"
                }, "generalPreviewImage");
                
                new ValidationTextBox({
                    maxLength: 255,
                    style: "width:100%;"
                }, "previewImageDescription");

                // show a tooltip when hovering over image
                previewImage.tooltip = new Tooltip({
                    connectId: ["generalPreviewImage"],
                    label: message.get("general.image.not.found"),
                    showDelay: 600,
                    position: ['above']
                });

                on(previewImage, "Change", function(value) {
                    //this.tooltip.destroyRecursive();
                    this.tooltip.label = "<img style='min-width:150px; max-width: 300px; max-height: 300px;' src='" + value + "' alt='" + message.get('general.image.not.found') + ": " + value + "' />";
                });

                on(previewImage.domNode, "mouseout", function() {
                    Tooltip.hide(dom.byId("generalPreviewImage"));
                });

                var thesaurusInspireStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '708px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 6100,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6100)
                }];
                layoutCreator.createDataGrid("thesaurusInspire", null, thesaurusInspireStructure, null);

                new CheckBox({}, "isInspireRelevant");
                new RadioButton({
                    checked: true,
                    value: "true",
                    name: "isInspireConform"
                }, "isInspireConform").startup();
                new RadioButton({
                    checked: false,
                    value: "false",
                    name: "isInspireConform"
                }, "notInspireConform").startup();
                
                new CheckBox({}, "isOpenData");
                new CheckBox({}, "isAdvCompatible");

                var categoriesStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '348px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 6400,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6400)
                }];
                layoutCreator.createDataGrid("categoriesOpenData", null, categoriesStructure, null);
            },

            createFachBezugClass1: function() {

                var tabRef1 = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref1BasisTabContainer");

                var tabRef1Tab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type1.technicalBasisTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref1BasisText");

                var tabRef1Tab2 = new ContentPane({
                    title: message.get("ui.obj.type1.technicalBasisTable.tab.links")
                }, "ref1BasisTab2");

                var ref1Structure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref1BasisLink", null, ref1Structure, null);

                tabRef1.addChild(tabRef1Tab1);
                tabRef1.addChild(tabRef1Tab2);
                tabRef1.startup();

                tabRef1.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref1BasisTab2"));

                new ValidationTextBox({
                    maxLength: 255,
                    style: "width:100%;"
                }, "ref1ObjectIdentifier");

                var button = new Button({
                    style: "margin-right: 0px;"
                }, "ref1ObjectIdentifierAddButton");
                button._inputFieldWidget = registry.byId("ref1ObjectIdentifier");
                button.onClick = igeEvents.setObjectUuid;

                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createSelectBox("ref1DataSet", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(525);
                });

                new NumberTextBox({
                    style: "width:25%;",
                    constraints: {
                        min: 0,
                        max: 100
                    }
                }, "ref1Coverage");

                var ref1RepresentStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '348px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 526,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 526)
                }];
                layoutCreator.createDataGrid("ref1Representation", null, ref1RepresentStructure, null);

                layoutCreator.createFilteringSelect("ref1VFormatTopology", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(528);
                });

                var ref1VFormatDetailsStructure = [{
                    field: 'geometryType',
                    name: message.get("ui.obj.type1.vectorFormat.detailsTable.header.geoType"),
                    width: '120px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 515,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 515)
                }, {
                    field: 'numElements',
                    name: message.get("ui.obj.type1.vectorFormat.detailsTable.header.elementCount"),
                    width: '328px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref1VFormatDetails", null, ref1VFormatDetailsStructure, null);

                new SimpleTextarea({
                    style: "width: 100%;"
                }, "boundingPolygon");

                var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
                additionalFields.push(registry.byId("boundingPolygon"));
                array.forEach(["boundingPolygon"], lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                var ref1SpatialSystemStructure = [{
                    field: 'title',
                    name: 'System',
                    width: '708px',
                    editable: true,
                    type: gridEditors.ComboboxEditor,
                    listId: 100,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 100)
                }];
                layoutCreator.createDataGrid("ref1SpatialSystem", null, ref1SpatialSystemStructure, null);

                
                /* Add spatialRepresentationInfo (REDMINE-381) */
                new CheckBox({}, "ref1TransfParamAvail");
                new NumberTextBox({style: "width:100%;"}, "ref1NumDimensions");
                layoutCreator.createFilteringSelect("ref1AxisDimName", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(529);
                });
                new NumberTextBox({style: "width:100%;"}, "ref1AxisDimSize");
                new ValidationTextBox({style: "width:100%;"}, "ref1CellGeometry");
                
                var geoRectified = new RadioButton({
                    checked: true,
                    value: "true",
                    name: "isGeoRectified"
                }, "isGeoRectified");
                geoRectified.startup();
                var geoReferenced = new RadioButton({
                    checked: false,
                    value: "false",
                    name: "isGeoRectified"
                }, "isGeoReferenced");
                geoReferenced.startup();
                
                new CheckBox({}, "ref1GridFormatRectCheckpoint");
                new ValidationTextBox({style: "width:100%;"}, "ref1GridFormatRectDescription");
                new ValidationTextBox({style: "width:100%;"}, "ref1GridFormatRectCornerPoint");

                layoutCreator.createSelectBox("ref1GridFormatRectPointInPixel", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(2100);
                });
                
                new CheckBox({}, "ref1GridFormatRefControlpoint");
                new CheckBox({}, "ref1GridFormatRefOrientationParam");
                new ValidationTextBox({style: "width:100%;"}, "ref1GridFormatRefGeoreferencedParam");

                on(geoRectified, "change", function(checked) {
                    if (checked) {
                        domClass.remove("geoRectifiedWrapper", "hide");
                        domClass.add("geoReferencedWrapper", "hide");
                    } else {
                        domClass.add("geoRectifiedWrapper", "hide");
                        domClass.remove("geoReferencedWrapper", "hide");
                    }
                });
                
                var ref1ScaleStructure = [{
                    field: 'scale',
                    name: message.get("ui.obj.type1.scaleTable.header.scale"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'groundResolution',
                    name: message.get("ui.obj.type1.scaleTable.header.groundResolution"),
                    width: '285px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'scanResolution',
                    name: message.get("ui.obj.type1.scaleTable.header.scanResolution"),
                    width: '318px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }];
                layoutCreator.createDataGrid("ref1Scale", null, ref1ScaleStructure, null);

                new NumberTextBox({
                    style: "width:100%;"
                }, "ref1AltAccuracy");
                new NumberTextBox({
                    style: "width:100%;"
                }, "ref1PosAccuracy");
                new NumberTextBox({
                    style: "width:100%;"
                }, "ref1GridPosAccuracy");

                var tabSymbols = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref1SymbolsTabContainer");

                tabSymbols.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref1SymbolsTab2"));

                var tabSymbolsTab1 = new ContentPane({
                    title: message.get("ui.obj.type1.symbolCatTable.tab.text")
                }, "ref1SymbolsTab1");

                var ref1SymbolsTextStructure = [{
                    field: 'title',
                    name: message.get("ui.obj.type1.symbolCatTable.header.title"),
                    width: '435px',
                    editable: true,
                    type: gridEditors.ComboboxEditor,
                    listId: 3555,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 3555) /*!!!, formatter: validator.emptyOrNullValidation*/
                }, {
                    field: 'date',
                    name: message.get("ui.obj.type1.symbolCatTable.header.date"),
                    width: '120px',
                    type: gridEditors.DateCellEditor,
                    editable: true,
                    formatter: gridFormatters.DateCellFormatter
                }, {
                    field: 'version',
                    name: message.get("ui.obj.type1.symbolCatTable.header.version"),
                    width: '153px',
                    editable: true /*!!!, formatter: validator.emptyOrNullValidation*/
                }];
                layoutCreator.createDataGrid("ref1SymbolsText", null, ref1SymbolsTextStructure, null);

                var tabSymbolsTab2 = new ContentPane({
                    title: message.get("ui.obj.type1.symbolCatTable.tab.links")
                }, "ref1SymbolsTab2");

                var ref1SymbolsLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref1SymbolsLink", null, ref1SymbolsLinkStructure, null);
                // no immediate dialog on row click !
                //	UtilGrid.addRowSelectionCallback("ref1SymbolsLink", this.openLinkDialog, { filter:3555 });

                tabSymbols.addChild(tabSymbolsTab1);
                tabSymbols.addChild(tabSymbolsTab2);
                tabSymbols.startup();

                var tabKeys = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref1KeysTabContainer");

                var tabKeysTab1 = new ContentPane({
                    title: message.get("ui.obj.type1.keyCatTable.tab.text")
                }, "ref1KeysTab1");

                var ref1KeysTextStructure = [{
                    field: 'title',
                    name: message.get("ui.obj.type1.keyCatTable.header.title"),
                    width: '435px',
                    editable: true,
                    type: gridEditors.ComboboxEditor,
                    listId: 3535,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 3535)
                }, {
                    field: 'date',
                    name: message.get("ui.obj.type1.keyCatTable.header.date"),
                    width: '120px',
                    type: gridEditors.DateCellEditor,
                    editable: true,
                    formatter: gridFormatters.DateCellFormatter
                }, {
                    field: 'version',
                    name: message.get("ui.obj.type1.keyCatTable.header.version"),
                    width: '153px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref1KeysText", null, ref1KeysTextStructure, null);

                var tabKeysTab2 = new ContentPane({
                    title: message.get("ui.obj.type1.keyCatTable.tab.links")
                }, "ref1KeysTab2");

                var ref1KeysLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref1KeysLink", null, ref1KeysLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref1KeysLink", this.openLinkDialog, { filter:3535 });

                tabKeys.addChild(tabKeysTab1);
                tabKeys.addChild(tabKeysTab2);
                tabKeys.startup();

                tabKeys.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref1KeysTab2"));

                var ref1ServiceLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref1ServiceLink", null, ref1ServiceLinkStructure, null);

                (new Button({}, "btnAddServiceLink")).onClick = lang.hitch(igeEvents, igeEvents.addServiceLink);

                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref1ServiceLink", this.openLinkDialog, { filter:5066 });

                var tabDataBasis = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref1DataBasisTabContainer");

                var tabDataBasisTab1 = new SimpleTextarea({
                    title: "Text",
                    "class": "textAreaFull"
                }, "ref1DataBasisText");

                var tabDataBasisTab2 = new ContentPane({
                    title: message.get("ui.obj.type1.dataBasisTable.tab.links")
                }, "ref1DataBasisTab2");

                var ref1DataBasisLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref1DataBasisLink", null, ref1DataBasisLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref1DataBasisLink", this.openLinkDialog, { filter:3570 });
                tabDataBasis.addChild(tabDataBasisTab1);
                tabDataBasis.addChild(tabDataBasisTab2);
                tabDataBasis.startup();

                tabDataBasis.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref1DataBasisTab2"));

                var ref1DataStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '708px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref1Data", null, ref1DataStructure, null);

                var tabRef1Process = new TabContainer({
                    id: "ref1ProcessTabContainer",
                    style: "width: 100%;",
                    doLayout: false
                }).placeAt("ref1ProcessTabContainerSpace");

                var tabRef1ProcessTab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type1.processTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref1ProcessText");
                var tabRef1ProcessTab2 = new ContentPane({
                    title: message.get("ui.obj.type1.processTable.tab.links")
                }, "ref1ProcessTab2");

                var ref1ProcessLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref1ProcessLink", null, ref1ProcessLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref1ProcessLink", this.openLinkDialog, { filter:3515 });
                tabRef1Process.addChild(tabRef1ProcessTab1);
                tabRef1Process.addChild(tabRef1ProcessTab2);
                tabRef1Process.startup();

                tabRef1Process.watch("selectedChildWidget", function(event, object, selectedTabWidget) {
                    UtilUI.toggleFunctionalLink( "ref1ProcessTab2", event, object, selectedTabWidget );
                    registry.byId("ref1ProcessLink").reinitLastColumn(true);
                });
            },

            createRefClass1DQ: function() {
                var dq109TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7109
                    values: [],
                    editable: true,
                    listId: 7109,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7109)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    constraints: {
                        min: 0
                    },
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq109Table", null, dq109TableStructure, null);

                var dq112TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7112
                    values: [],
                    editable: true,
                    listId: 7112,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7112)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq112Table", null, dq112TableStructure, null);

                var dq113TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7113
                    values: [],
                    editable: true,
                    listId: 7113,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7113)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq113Table", null, dq113TableStructure, null);

                var dq114TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7114
                    values: [],
                    editable: true,
                    listId: 7114,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7114)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq114Table", null, dq114TableStructure, null);

                var dq115TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7115
                    values: [],
                    editable: true,
                    listId: 7115,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7115)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq115Table", null, dq115TableStructure, null);

                var dq120TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7120
                    values: [],
                    editable: true,
                    listId: 7120,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7120)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq120Table", null, dq120TableStructure, null);

                var dq125TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7125
                    values: [],
                    editable: true,
                    listId: 7125,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7125)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq125Table", null, dq125TableStructure, null);

                var dq126TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7126
                    values: [],
                    editable: true,
                    listId: 7126,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7126)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq126Table", null, dq126TableStructure, null);

                var dq127TableStructure = [{
                    field: 'nameOfMeasure',
                    name: message.get("ui.obj.dq.table.header1"),
                    width: '300px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded listId=7127
                    values: [],
                    editable: true,
                    listId: 7127,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 7127)
                }, {
                    field: 'resultValue',
                    name: message.get("ui.obj.dq.table.header2"),
                    width: '105px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'measureDescription',
                    name: message.get("ui.obj.dq.table.header3"),
                    width: '303px',
                    editable: true
                }];
                layoutCreator.createDataGrid("dq127Table", null, dq127TableStructure, null);
            },

            createFachBezugClass2: function() {

                new SimpleTextarea({
                    maxLength: 255,
                    style: "width:100%;"
                }, "ref2Author");
                new ValidationTextBox({
                    maxLength: 255,
                    style: "width:100%;"
                }, "ref2Publisher");
                new ValidationTextBox({
                    maxLength: 80,
                    style: "width:100%;"
                }, "ref2PublishedIn");
                new ValidationTextBox({
                    maxLength: 80,
                    style: "width:100%;"
                }, "ref2PublishLocation");

                new ValidationTextBox({
                    maxLength: 40,
                    style: "width:100%;"
                }, "ref2PublishedInIssue");
                new ValidationTextBox({
                    maxLength: 20,
                    style: "width:100%;"
                }, "ref2PublishedInPages");
                new ValidationTextBox({
                    maxLength: 20,
                    style: "width:100%;"
                }, "ref2PublishedInYear");
                new ValidationTextBox({
                    maxLength: 40,
                    style: "width:100%;"
                }, "ref2PublishedISBN");
                new ValidationTextBox({
                    maxLength: 80,
                    style: "width:100%;"
                }, "ref2PublishedPublisher");

                var ref2LocationTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref2LocationTabContainer");

                var ref2LocationTab1 = new SimpleTextarea({
                    maxLength: 80,
                    title: message.get("ui.obj.type2.locationTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref2LocationText");

                var ref2LocationTab2 = new ContentPane({
                    title: message.get("ui.obj.type2.locationTable.tab.links")
                }, "ref2LocationTab2");

                var ref2LocationLinkStructure = [{
                    field: 'icon',
                    name: 'title',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'date',
                    width: '325px'
                }];
                layoutCreator.createDataGrid("ref2LocationLink", null, ref2LocationLinkStructure, null);

                ref2LocationTabContainer.addChild(ref2LocationTab1);
                ref2LocationTabContainer.addChild(ref2LocationTab2);
                ref2LocationTabContainer.startup();

                ref2LocationTabContainer.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref2LocationTab2"));

                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createComboBox("ref2DocumentType", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(3385);
                });

                var ref2BaseDataTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref2BaseDataTabContainer");

                var ref2BaseDataTab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type2.generalDataTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref2BaseDataText");

                var ref2BaseDataTab2 = new ContentPane({
                    title: message.get("ui.obj.type2.generalDataTable.tab.links")
                }, "ref2BaseDataTab2");

                var ref2BaseDataLinkStructure = [{
                    field: 'icon',
                    name: 'title',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'date',
                    width: '325px'
                }];
                layoutCreator.createDataGrid("ref2BaseDataLink", null, ref2BaseDataLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref2BaseDataLink", this.openLinkDialog, { filter:3345 });

                ref2BaseDataTabContainer.addChild(ref2BaseDataTab1);
                ref2BaseDataTabContainer.addChild(ref2BaseDataTab2);
                ref2BaseDataTabContainer.startup();

                ref2BaseDataTabContainer.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref2BaseDataTab2"));

                new SimpleTextarea({
                    maxLength: 255,
                    "class": "textAreaFull"
                }, "ref2BibData");
                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "ref2Explanation");
            },

            createFachBezugClass3: function() {
                var ref3ServiceTypeTableStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '708px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    listId: 5200,
                    editable: true,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 5200)
                    //formatter: function(value){
                    //	return UtilList.getSelectDisplayValue(this, value);
                    //}
                }];
                layoutCreator.createDataGrid("ref3ServiceTypeTable", null, ref3ServiceTypeTableStructure, null);

                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createSelectBox("ref3ServiceType", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(5100);
                });
                rules.applyRuleServiceType();

                rules.applyRuleDownloadService();
                new CheckBox({}, "ref3IsAtomDownload");

                var couplingTypes = function() {
                    var def = new Deferred();
                    var data = [{
                        value: "tight",
                        label: message.get("ui.obj.type3.coupling.type.tight")
                    }, {
                        value: "mixed",
                        label: message.get("ui.obj.type3.coupling.type.mixed")
                    }, {
                        value: "loose",
                        label: message.get("ui.obj.type3.coupling.type.loose")
                    }];

                    def.resolve(data);
                    return def;
                };
                var storeProps2 = {
                    data: {
                        identifier: 'value',
                        label: 'label'
                    }
                };
                layoutCreator.createSelectBox("ref3CouplingType", null, storeProps2, couplingTypes);

                var ref3ServiceVersionStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '348px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 5152,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6400)
                }];
                layoutCreator.createDataGrid("ref3ServiceVersion", null, ref3ServiceVersionStructure, null);

                var ref3ScaleStructure = [{
                    field: 'scale',
                    name: message.get("ui.obj.type3.scaleTable.header.scale"),
                    width: '100px',
                    editable: true
                }, {
                    field: 'groundResolution',
                    name: message.get("ui.obj.type3.scaleTable.header.groundResolution"),
                    width: '285px',
                    editable: true
                }, {
                    field: 'scanResolution',
                    name: message.get("ui.obj.type3.scaleTable.header.scanResolution"),
                    width: '323px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref3Scale", null, ref3ScaleStructure, null);

                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "ref3SystemEnv");
                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "ref3History");

                var ref3BaseDataTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref3BaseDataTabContainer");

                var ref3BaseDataTab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type2.generalDataTable.tab.text"),
                    "class": "textAreaFull noValidate"
                }, "ref3BaseDataText");

                var ref3MethodTab2 = new ContentPane({
                    title: message.get("ui.obj.type2.generalDataTable.tab.links")
                }, "ref3MethodTab2");

                var ref3BaseDataLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '325px'
                }];
                layoutCreator.createDataGrid("ref3BaseDataLink", null, ref3BaseDataLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref3BaseDataLink", this.openLinkDialog, { filter:3210 });
                (new Button({}, "btnAddDataLink")).onClick = igeEvents.addDataLink;

                ref3BaseDataTabContainer.addChild(ref3MethodTab2);
                ref3BaseDataTabContainer.addChild(ref3BaseDataTab1);
                ref3BaseDataTabContainer.startup();

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 5
                }, "ref3Explanation");

                var ref3OperationStructure = [{
                    field: 'name',
                    name: message.get("ui.obj.type3.operationTable.header.name"),
                    width: '165px'
                }, {
                    field: 'addressList',
                    name: message.get("ui.obj.type3.operationTable.header.address"),
                    formatter: lang.partial(gridFormatters.FirstEntryFormatter, "title"),
                    width: '543px'
                }];
                layoutCreator.createDataGrid("ref3Operation", null, ref3OperationStructure, null);

                var button = new Button({
                    style: "margin-right: 0px;"
                }, "updateDatasetWithCapabilities");
                button.onClick = lang.hitch(igeEvents, igeEvents.updateDataset);

                new CheckBox({}, "ref3HasAccessConstraint");
            },

            createFachBezugClass4: function() {
                var ref4ParticipantsTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref4ParticipantsTabContainer");

                var ref4ParticipantsTab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type2.locationTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref4ParticipantsText");

                var ref4ParticipantsTab2 = new ContentPane({
                    title: message.get("ui.obj.type2.locationTable.tab.links")
                }, "ref4ParticipantsTab2");

                var ref4ParticipantsLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '325px'
                }];
                layoutCreator.createDataGrid("ref4ParticipantsLink", null, ref4ParticipantsLinkStructure, null);

                ref4ParticipantsTabContainer.addChild(ref4ParticipantsTab1);
                ref4ParticipantsTabContainer.addChild(ref4ParticipantsTab2);
                ref4ParticipantsTabContainer.startup();

                ref4ParticipantsTabContainer.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref4ParticipantsTab2"));

                var ref4PMTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref4PMTabContainer");

                var ref4PMTab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type4.projectManagerTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref4PMText");

                var ref4PMTab2 = new ContentPane({
                    title: message.get("ui.obj.type4.projectManagerTable.tab.links")
                }, "ref4PMTab2");

                /*var ref4ParticipantsLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '325px'
                }];*/
                layoutCreator.createDataGrid("ref4PMLink", null, ref4ParticipantsLinkStructure, null);

                ref4PMTabContainer.addChild(ref4PMTab1);
                ref4PMTabContainer.addChild(ref4PMTab2);
                ref4PMTabContainer.startup();

                ref4PMTabContainer.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref4PMTab2"));

                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "ref4Explanation");

            },

            createFachBezugClass5: function() {
                var tabKeys = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref5KeysTabContainer");

                var tabKeysTab1 = new ContentPane({
                    title: message.get("ui.obj.type5.keyCatTable.tab.text")
                }, "ref5KeysTab1");

                var ref5KeysTextStructure = [{
                    field: 'title',
                    name: message.get("ui.obj.type5.keyCatTable.header.title"),
                    width: '435px',
                    editable: true,
                    type: gridEditors.ComboboxEditor,
                    listId: 3535,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 3535)
                }, {
                    field: 'date',
                    name: message.get("ui.obj.type5.keyCatTable.header.date"),
                    width: '120px',
                    type: gridEditors.DateCellEditor,
                    editable: true,
                    formatter: gridFormatters.DateCellFormatter
                }, {
                    field: 'version',
                    name: message.get("ui.obj.type5.keyCatTable.header.version"),
                    width: '153px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref5KeysText", null, ref5KeysTextStructure, null);

                var tabKeysTab2 = new ContentPane({
                    title: message.get("ui.obj.type5.keyCatTable.tab.links")
                }, "ref5KeysTab2");

                var ref5KeysLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("ref5KeysLink", null, ref5KeysLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref5KeysLink", this.openLinkDialog, { filter:3109 });

                tabKeys.addChild(tabKeysTab1);
                tabKeys.addChild(tabKeysTab2);
                tabKeys.startup();

                tabKeys.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref5KeysTab2"));

                var ref5dbContentStructure = [{
                    field: 'parameter',
                    name: message.get("ui.obj.type5.contentTable.header.parameter"),
                    width: '335px',
                    editable: true
                }, {
                    field: 'additionalData',
                    name: message.get("ui.obj.type5.contentTable.header.additionalData"),
                    width: '373px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref5dbContent", null, ref5dbContentStructure, null);


                var ref5MethodTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref5MethodTabContainer");

                var ref5MethodTab1 = new SimpleTextarea({
                    title: message.get("ui.obj.type5.methodTable.tab.text"),
                    "class": "textAreaFull"
                }, "ref5MethodText");

                var ref5MethodTab2 = new ContentPane({
                    title: message.get("ui.obj.type5.methodTable.tab.links")
                }, "ref5MethodTab2");

                var ref5MethodLinkStructure = [{
                    field: 'icon',
                    name: '&nbsp;',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '320px'
                }];
                layoutCreator.createDataGrid("ref5MethodLink", null, ref5MethodLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref5MethodLink", this.openLinkDialog, { filter:3100 });

                ref5MethodTabContainer.addChild(ref5MethodTab1);
                ref5MethodTabContainer.addChild(ref5MethodTab2);
                ref5MethodTabContainer.startup();

                ref5MethodTabContainer.watch("selectedChildWidget", lang.partial(UtilUI.toggleFunctionalLink, "ref5MethodTab2"));

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 5
                }, "ref5Explanation");

            },

            createFachBezugClass6: function() {
                console.debug("createFachBezugClass6");
                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createFilteringSelect("ref6ServiceType", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(5300);
                });

                var ref6ServiceVersionStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '348px',
                    editable: true
                }];
                layoutCreator.createDataGrid("ref6ServiceVersion", null, ref6ServiceVersionStructure, null);
                console.debug("ref6ser");

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 5
                }, "ref6SystemEnv");

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 5
                }, "ref6History");

                var ref6BaseDataTabContainer = new TabContainer({
                    style: "width: 100%;",
                    doLayout: false
                }, "ref6BaseDataTabContainer");

                var ref6BaseDataTab1 = new SimpleTextarea({
                    "class": "textAreaFull",
                    title: message.get("ui.obj.type6.generalDataTable.tab.text")
                }, "ref6BaseDataText");

                var ref6BaseDataTab2 = new ContentPane({
                    title: message.get("ui.obj.type6.generalDataTable.tab.links")
                }, "ref6MethodTab2");

                var ref6BaseDataLinkStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'Objekte',
                    width: '325px'
                }];
                console.debug("ref6base");
                layoutCreator.createDataGrid("ref6BaseDataLink", null, ref6BaseDataLinkStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("ref6BaseDataLink", this.openLinkDialog, { filter:3210 });
                console.debug("ref6baseE");
                ref6BaseDataTabContainer.addChild(ref6BaseDataTab1);
                ref6BaseDataTabContainer.addChild(ref6BaseDataTab2);
                ref6BaseDataTabContainer.startup();

                ref6BaseDataTabContainer.watch("selectedChildWidget", function(event, object, selectedTabWidget) {
                    UtilUI.toggleFunctionalLink( "ref6MethodTab2", event, object, selectedTabWidget );
                    registry.byId("ref6BaseDataLink").reinitLastColumn(true);
                });

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 5
                }, "ref6Explanation");

                var ref6UrlListStructure = [{
                    field: 'name',
                    name: message.get("ui.obj.type6.urlList.header.name"),
                    width: '225px',
                    editable: true
                }, {
                    field: 'url',
                    name: message.get("ui.obj.type6.urlList.header.url"),
                    width: '225px',
                    editable: true
                }, {
                    field: 'urlDescription',
                    name: message.get("ui.obj.type6.urlList.header.urlDescription"),
                    width: '258px',
                    editable: true
                }];
                console.debug("ref6UrlList");
                layoutCreator.createDataGrid("ref6UrlList", null, ref6UrlListStructure, null);
                //UtilGrid.getTable("ref6UrlList").onCellChange.subscribe(addServiceUrlValidation);
                //UtilGrid.getTable("ref6UrlList").onAddNewRow.subscribe(addServiceUrlValidation);
            },

            createRaumbezug: function() {
                var spatialRefAdminUnitStructure = [{
                    field: 'label',
                    name: message.get("ui.obj.spatial.geoThesTable.header.name"),
                    width: '331px'
                }, {
                    field: 'longitude1',
                    name: message.get("ui.obj.spatial.geoThesTable.header.longitude1"),
                    width: '90px',
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'latitude1',
                    name: message.get("ui.obj.spatial.geoThesTable.header.latitude1"),
                    width: '90px',
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'longitude2',
                    name: message.get("ui.obj.spatial.geoThesTable.header.longitude2"),
                    width: '90px',
                    formatter: gridFormatters.LocalizedNumberFormatter
                }, {
                    field: 'latitude2',
                    name: message.get("ui.obj.spatial.geoThesTable.header.latitude2"),
                    width: '107px',
                    formatter: gridFormatters.LocalizedNumberFormatter
                }];
                layoutCreator.createDataGrid("spatialRefAdminUnit", null, spatialRefAdminUnitStructure, null);

                var spatialData = function() {
                    var def = new Deferred();
                    var data = [{
                        value: "GEO_WGS84",
                        label: message.get("dialog.research.ext.obj.coordinates.wgs84")
                    }, {
                        value: "GEO_DHDN",
                        label: message.get("dialog.research.ext.obj.coordinates.geobp")
                    }, {
                        value: "GK2",
                        label: message.get("dialog.research.ext.obj.coordinates.gk2")
                    }, {
                        value: "GK3",
                        label: message.get("dialog.research.ext.obj.coordinates.gk3")
                    }, {
                        value: "GK4",
                        label: message.get("dialog.research.ext.obj.coordinates.gk4")
                    }, {
                        value: "GK5",
                        label: message.get("dialog.research.ext.obj.coordinates.gk5")
                    }, {
                        value: "UTM32e",
                        label: message.get("dialog.research.ext.obj.coordinates.utm32w")
                    }, {
                        value: "UTM33e",
                        label: message.get("dialog.research.ext.obj.coordinates.utm33w")
                    }, {
                        value: "UTM32s",
                        label: message.get("dialog.research.ext.obj.coordinates.utm32s")
                    }, {
                        value: "UTM33s",
                        label: message.get("dialog.research.ext.obj.coordinates.utm33s")
                    }, {
                        value: "LAMGe",
                        label: message.get("dialog.research.ext.obj.coordinates.lamgw")
                    }];

                    def.resolve(data);
                    return def;
                };
                var storeProps = {
                    data: {
                        identifier: 'value',
                        label: 'label'
                    }
                };
                layoutCreator.createSelectBox("spatialRefAdminUnitSelect", null, storeProps, spatialData);
                registry.byId("spatialRefAdminUnitSelect").set("value", "GEO_WGS84");


                var emptyData = function() {
                    var def = new Deferred();
                    var data = [{
                        longitude1: "",
                        latitude1: "",
                        longitude2: "",
                        latitude2: ""
                    }];

                    def.resolve(data);
                    return def;
                };

                var spatialRefAdminUnitCoordsStructure = [{
                    field: 'longitude1',
                    name: 'longitude1',
                    width: '86px'
                }, {
                    field: 'latitude1',
                    name: 'latitude1',
                    width: '86px'
                }, {
                    field: 'longitude2',
                    name: 'longitude2',
                    width: '86px'
                }, {
                    field: 'latitude2',
                    name: 'latitude2',
                    width: '85px'
                }];
                layoutCreator.createDataGrid("spatialRefAdminUnitCoords", null, spatialRefAdminUnitCoordsStructure, emptyData);

                // set constraints for WGS84 (see http://technet.microsoft.com/de-de/library/dd220425.aspx)
                var latitudeConstraint = {
                    min: -90,
                    max: 90
                };
                var longitudeConstraint = {
                    min: -180,
                    max: 180
                };
                var spatialRefLocationStructure = [{
                    field: 'name',
                    name: message.get("ui.obj.spatial.geoTable.header.name"),
                    width: '331px',
                    editable: true,
                    type: gridEditors.ComboboxEditor,
                    options: [],
                    listId: 1100,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 1100)
                    // formatter: lang.partial(validator.emptyOrNullValidation, "spatialRefLocation")
                }, {
                    field: 'longitude1',
                    name: message.get("ui.obj.spatial.geoTable.header.longitude1"),
                    width: '90px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    constraints: longitudeConstraint,
                    formatter: gridFormatters.LocalizedNumberFormatter /*!!!, formatter: minMaxBoundingBoxValidation*/
                }, {
                    field: 'latitude1',
                    name: message.get("ui.obj.spatial.geoTable.header.latitude1"),
                    width: '90px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    constraints: latitudeConstraint,
                    formatter: gridFormatters.LocalizedNumberFormatter /*!!!, formatter: minMaxBoundingBoxValidation*/
                }, {
                    field: 'longitude2',
                    name: message.get("ui.obj.spatial.geoTable.header.longitude2"),
                    width: '90px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    constraints: longitudeConstraint,
                    formatter: gridFormatters.LocalizedNumberFormatter /*!!!, formatter: minMaxBoundingBoxValidation*/
                }, {
                    field: 'latitude2',
                    name: message.get("ui.obj.spatial.geoTable.header.latitude2"),
                    width: '107px',
                    editable: true,
                    type: gridEditors.DecimalCellEditor,
                    constraints: latitudeConstraint,
                    formatter: gridFormatters.LocalizedNumberFormatter /*!!!, formatter: minMaxBoundingBoxValidation*/
                }];
                layoutCreator.createDataGrid("spatialRefLocation", null, spatialRefLocationStructure, null);
                aspect.after(UtilGrid.getTable("spatialRefLocation"), "onCellChange", function(res, args) {
                    var msg = args[0];

                    var updateRow = function(rowPos, lon1, lat1, lon2, lat2) {
                        var row = UtilGrid.getTableData("spatialRefLocation")[rowPos];
                        row.longitude1 = lon1;
                        row.latitude1 = lat1;
                        row.longitude2 = lon2;
                        row.latitude2 = lat2;
                        UtilGrid.updateTableDataRow("spatialRefLocation", rowPos, row);
                    };

                    if (msg.cell === 0) {
                        var data = UtilSyslist.getSyslistEntryData(1100, msg.item.name);
                        console.debug("syslist data: " + data);
                        if (data && data.trim() !== "") {
                            // replace "," with "." for correct data format
                            data = data.replace(/,/g, ".");
                            var splittedData = data.split(" ");
                            updateRow(msg.row, 
                                parseFloat(splittedData[0]), 
                                parseFloat(splittedData[1]), 
                                parseFloat(splittedData[2]), 
                                parseFloat(splittedData[3])
                            );
                            
                        } else if (UtilSyslist.getSyslistEntryKey(1100, msg.item.name) !== msg.item.name) {
                            updateRow(msg.row, null, null, null, null);

                        }
                    }
                });

                (new Button({}, "btnGetSpatialRefLocationFromParent")).onClick = igeEvents.getSpatialRefLocationFromParent;

                layoutCreator.createSelectBox("spatialRefLocationSelect", null, storeProps, spatialData);
                registry.byId("spatialRefLocationSelect").set("value", "GEO_WGS84");

                layoutCreator.createDataGrid("spatialRefLocationCoords", null, spatialRefAdminUnitCoordsStructure, emptyData);

                new NumberTextBox({
                    style: "width:80px;"
                }, "spatialRefAltMin");
                new NumberTextBox({
                    style: "width:80px;"
                }, "spatialRefAltMax");

                storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createFilteringSelect("spatialRefAltMeasure", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(102);
                });

                layoutCreator.createComboBox("spatialRefAltVDate", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(101);
                });

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 8
                }, "spatialRefExplanation");

            },

            createZeitbezug: function() {
                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                var timeRefTableStructure = [{
                    field: 'date',
                    name: message.get("ui.obj.time.timeRefTable.header.date"),
                    width: '120px',
                    type: gridEditors.DateCellEditor,
                    editable: true,
                    formatter: gridFormatters.DateCellFormatter
                }, {
                    field: 'type',
                    name: message.get("ui.obj.time.timeRefTable.header.type"),
                    width: '228px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 502,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 502)
                }];
                layoutCreator.createDataGrid("timeRefTable", null, timeRefTableStructure, null);

                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 7
                }, "timeRefExplanation");

                var timeTypeData = function() {
                    var def = new Deferred();
                    var data = [
                        [
                            [message.get("dialog.research.ext.obj.content.time.at")],
                            ["am"]
                        ],
                        [
                            [message.get("dialog.research.ext.obj.content.time.since")],
                            ["seit"]
                        ],
                        [
                            [message.get("dialog.research.ext.obj.content.time.until")],
                            ["bis"]
                        ],
                        [
                            [message.get("dialog.research.ext.obj.content.time.fromto")],
                            ["von"]
                        ]
                    ];

                    def.resolve(data);
                    return def;
                };

                var storePropsTimeType = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createFilteringSelect("timeRefType", null, storePropsTimeType, timeTypeData);
                new DateTextBox({
                    style: "width: 100%;",
                    onChange: function() {
                        if (arguments[0]) registry.byId('timeRefDate2').constraints.min = arguments[0];
                    }
                }, "timeRefDate1");
                new DateTextBox({
                    style: "width: 100%;",
                    onChange: function() {
                        if (arguments[0]) registry.byId('timeRefDate1').constraints.max = arguments[0];
                    }
                }, "timeRefDate2");

                storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createFilteringSelect("timeRefStatus", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(523);
                });

                layoutCreator.createFilteringSelect("timeRefPeriodicity", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(518);
                });

                var interval = new NumberTextBox({
                    style: "width: 65%;"
                }, "timeRefIntervalNum");
                // set periodicity to "kontinuierlich" when val has been entered
                on(interval, "KeyUp", function() {
                    registry.byId("timeRefPeriodicity").set("value", 1);
                });

                layoutCreator.createFilteringSelect("timeRefIntervalUnit", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(1230);
                });
            },


            createExtraInfo: function() {
                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createSelectBox("extraInfoLangMetaData", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(99999999);
                });

                var extraInfoLangDataStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '348px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 99999999,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 99999999)
                }];
                layoutCreator.createDataGrid("extraInfoLangData", null, extraInfoLangDataStructure, null);

                layoutCreator.createSelectBox("extraInfoPublishArea", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(3571);
                });

                layoutCreator.createFilteringSelect("extraInfoCharSetData", null, storeProps, function() {
                    return UtilSyslist.getSyslistEntry(510);
                });

                //layoutCreator.createSelectBox("extraInfoConformityLevelEditor", null, storeProps, function(){
                //    return UtilSyslist.getSyslistEntry(6000);
                //});

                //new ValidationTextBox({}, "extraInfoConformitySpecificationEditor");

                var extraInfoConformityTableStructure = [{
                    field: 'specification',
                    name: message.get("ui.obj.additionalInfo.conformityTable.header.specification"),
                    width: '395px',
                    editable: false,
                    formatter: gridFormatters.ConformityCellFormatter
                }, {
                    field: 'level',
                    name: message.get("ui.obj.additionalInfo.conformityTable.header.level"),
                    width: '150px',
                    editable: false,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6000)
                }, {
                    field: 'publicationDate',
                    name: message.get("ui.obj.additionalInfo.conformityTable.header.publicationDate"),
                    width: '150px',
                    editable: false,
                    formatter: gridFormatters.DateCellFormatter
                }, {
                    field: 'isInspire',
                    width: '0px',
                    hidden: true,
                    editable: false,
                    formatter: gridFormatters.BoolCellFormatter
                }];
                layoutCreator.createDataGrid("extraInfoConformityTable", null, extraInfoConformityTableStructure, null);

                var extraInfoLegalBasicsTableStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '470px',
                    type: gridEditors.ComboboxEditor,
                    editable: true,
                    listId: 1350,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 1350)
                }];
                layoutCreator.createDataGrid("extraInfoLegalBasicsTable", null, extraInfoLegalBasicsTableStructure, null);

                var extraInfoXMLExportTableStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '225px',
                    type: gridEditors.ComboboxEditor,
                    editable: true,
                    listId: 1370,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 1370)
                }];
                layoutCreator.createDataGrid("extraInfoXMLExportTable", null, extraInfoXMLExportTableStructure, null);

                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "extraInfoPurpose");
                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "extraInfoUse");

            },

            createAvailability: function() {
                var availabilityAccessConstraintsStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '348px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 6010,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6010)
                }];
                layoutCreator.createDataGrid("availabilityAccessConstraints", null, availabilityAccessConstraintsStructure, null);

                var availabilityUseAccessConstraintsStructure = [{
                    field: 'title',
                    name: message.get("ui.obj.availability.useAccessConstraintsTable.header.name"),
                    width: '348px',
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 6500,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6500)
                }, {
                    field: 'source',
                    name: message.get("ui.obj.availability.useAccessConstraintsTable.header.source"),
                    editable: true
                }];
                layoutCreator.createDataGrid("availabilityUseAccessConstraints", null, availabilityUseAccessConstraintsStructure, null);
                
                new SimpleTextarea({
                    "class": "textAreaFull",
                    rows: 5
                }, "availabilityUseConstraints");

                var availabilityDataFormatStructure = [{
                    field: 'name',
                    name: message.get("ui.obj.availability.dataFormatTable.header.name"),
                    width: '100px',
                    type: gridEditors.ComboboxEditor,
                    editable: true,
                    listId: 1320,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 1320)
                }, {
                    field: 'version',
                    name: message.get("ui.obj.availability.dataFormatTable.header.version"),
                    width: '125px',
                    editable: true
                }, {
                    field: 'compression',
                    name: message.get("ui.obj.availability.dataFormatTable.header.compression"),
                    width: '205px',
                    editable: true
                }, {
                    field: 'pixelDepth',
                    name: message.get("ui.obj.availability.dataFormatTable.header.depth"),
                    width: '278px',
                    editable: true
                }];
                layoutCreator.createDataGrid("availabilityDataFormat", null, availabilityDataFormatStructure, null);

                var availabilityMediaOptionsStructure = [{
                    field: 'name',
                    name: message.get("ui.obj.availability.mediaOptionTable.header.type"),
                    width: '250px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 520,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 520)
                }, {
                    field: 'transferSize',
                    name: message.get("ui.obj.availability.mediaOptionTable.header.amount"),
                    width: '250px',
                    editable: true
                }, {
                    field: 'location',
                    name: message.get("ui.obj.availability.mediaOptionTable.header.location"),
                    width: '208px',
                    editable: true
                }];
                layoutCreator.createDataGrid("availabilityMediaOptions", null, availabilityMediaOptionsStructure, null);

                new SimpleTextarea({
                    "class": "textAreaFull"
                }, "availabilityOrderInfo");

                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                layoutCreator.createComboBox("availabilityDataFormatInspire", null, lang.clone(storeProps), function() {
                    return UtilSyslist.getSyslistEntry(6300);
                });

            },


            createThesaurus: function() {
                var advProductGroupStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '708px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 8010,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 8010)
                }];
                layoutCreator.createDataGrid("advProductGroup", null, advProductGroupStructure, null);

                var thesaurusTopicsStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '708px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 527,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 527)
                }];
                layoutCreator.createDataGrid("thesaurusTopics", null, thesaurusTopicsStructure, null);

                var thesaurusTermsStructure = [{
                    field: 'label',
                    name: 'label',
                    width: '550px'
                }, {
                    field: 'sourceString',
                    name: 'sourceString',
                    width: '158px'
                }];
                layoutCreator.createDataGrid("thesaurusTerms", null, thesaurusTermsStructure, null);

                var freeTermsWidget = new SimpleTextarea({
                    "class": "textAreaFull"
                }, "thesaurusFreeTerms");

                var button = new Button({
                    style: "margin-right: 0px;"
                }, "thesaurusFreeTermsAddButton");
                // button._inputFieldWidget = registry.byId("thesaurusFreeTerms");
                // button._termListWidget = "thesaurusTerms";
                on(button, "Click", function() {
                    var termList = UtilThesaurus.parseQueryTerm(freeTermsWidget.get('value'));
                    var callerInfo = { id: this.id, _termListWidget: "thesaurusTerms" };
                    lang.hitch(igeEvents, igeEvents.addKeywords(termList, callerInfo));
                    freeTermsWidget.attr("value", "", true);
                });

                new CheckBox({}, "thesaurusEnvExtRes");


                var thesaurusEnvTopicsStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '675px',
                    type: gridEditors.SelectboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    values: [],
                    editable: true,
                    listId: 1410,
                    formatter: lang.partial(gridFormatters.SyslistCellFormatter, 1410)
                }];
                layoutCreator.createDataGrid("thesaurusEnvTopics", null, thesaurusEnvTopicsStructure, null);

            },


            createReferences: function() {
                var linksToStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '435px'
                }, {
                    field: 'relationTypeName',
                    name: 'relationTypeName',
                    width: '250px'
                }];
                layoutCreator.createDataGrid("linksTo", null, linksToStructure, null);
                // no immediate dialog on row click !
                //    UtilGrid.addRowSelectionCallback("linksTo", this.openLinkDialog);

                (new Button({}, "btnGetLinksToFromParent")).onClick = igeEvents.getLinksToFromParent;

                // relation type filter as selection list: content dependent from class !
                // initialize with empty store !
                var storeProps = {
                    data: {
                        identifier: 'entryId',
                        label: 'name'
                    }
                };
                layoutCreator.createSelectBox("linksToRelationTypeFilter", null, storeProps, null);

                // load content of relation type filter when object class changes !
                // Also triggered on initial setting of object class !
                topic.subscribe("/onObjectClassChange", igeEvents.setLinksToRelationTypeFilterContent);

                // filter table content on change of relation type filter
                on(registry.byId("linksToRelationTypeFilter"), "Change", igeEvents.filterLinksToViaRelationType);


                var linksFromStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'linkLabel',
                    name: 'linkLabel',
                    width: '685px'
                }];
                layoutCreator.createDataGrid("linksFrom", null, linksFromStructure, null);
            },
            
            // called now via context menue
            // openOperationDialog: function(dialogData) {
            //     dialog.showPage(message.get("dialog.operations.title"), 'dialogs/mdek_operation_dialog.jsp?c=' + userLocale, 735, 745, true, dialogData);
            // },

            // Initialize the Coordinate Transformation Service
            initCTS: function() {

                /* Coordinate Update Handler.
                 * This object connects two tables:
                 * A source table containing coordinates (Coordinate objects).
                 * A destination table containing Coordinate objects and a srs (Spatial Reference System) entry.
                 * When the source table onSelect method is invoked the update handler does the following things:
                 *  1. updateCoordinates() is called.
                 *     This Method fetches the currently selected object from the source table and all
                 *     srs identifiers from the target table. For each entry in the target table a coordinate transformation
                 *     request is sent to the CT Service.
                 *  2. The result of the CT Service request is redirected to the updateDestinationStore method.
                 *     This method gets the correct entry from the target table and updates the stored Coordinate
                 *
                 * args is an object containing two values:  args = {srcTable: FilteringTable, dstTable: FilteringTable}
                 *  srcTable - The table containing the coordinates which should be converted
                 *  dstTable - The 'slave' table containing transformed coordinates
                 *
                 * Usage:
                 *  simply create a new CoordinateUpdateHandler like this:
                 *   new CoordinateUpdateHandler({srcTable: registry.byId("src"), dstTable: registry.byId("dst")});
                 *
                 */
                function CoordinateUpdateHandler(args) {
                    this.srcTable = args.srcTable;
                    this.srcSelect = registry.byId(args.srcSelect);
                    this.dstTable = args.dstTable;

                    this.updateDestinationStore = function(ctsResponse) {
                        var coord = ctsResponse.coordinate;
                        if (coord === null)
                            this.showError();
                        else {
                            //var data = UtilGrid.getTableData(this.dstTable)[0];
                            UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude1', coord.longitude1);
                            UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude1', coord.latitude1);
                            UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude2', coord.longitude2);
                            UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude2', coord.latitude2);
                        }
                    };

                    this.clearDstStore = function() {
                        //var data = UtilGrid.getTableData(this.dstTable)[0];
                        var clearValue = "";
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude1', clearValue);
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude1', clearValue);
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude2', clearValue);
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude2', clearValue);
                    };

                    this.showError = function() {
                        //var data = UtilGrid.getTableData(this.dstTable)[0];
                        var clearValue = "D.n.e.";
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude1', clearValue);
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude1', clearValue);
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude2', clearValue);
                        UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude2', clearValue);
                    };

                    this.updateCoordinates = function() {
                        console.debug("update coordinates: " + this.srcTable);
                        var fromSRS = "GEO_WGS84";
                        var selectedData = UtilGrid.getSelectedData(this.srcTable); //.selection.getSelected();//this.srcTable.getSelectedData();
                        if (!selectedData[0] || selectedData[0] === null)
                            return;

                        this.clearDstStore();
                        if (selectedData.length != 1) {
                            return;
                        }
                        var coords = selectedData[0];
                        var toSRS = this.srcSelect.get('value');
                        var realNumberFormat = {
                            format: ["#?.#?????????????????????", "#?????????????????????", "-#?.#?????????????????????", "-#?????????????????????", "#?,#?????????????????????", "-#?,#?????????????????????"]
                        };
                        if (coords && toSRS && validate.isNumberFormat(coords.longitude1, realNumberFormat) && validate.isNumberFormat(coords.longitude2, realNumberFormat) && validate.isNumberFormat(coords.latitude1, realNumberFormat) && validate.isNumberFormat(coords.latitude2, realNumberFormat)) {
                            //				console.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
                            var _this = this;
                            CTService.getCoordinates(fromSRS, toSRS, coords, {
                                preHook: function() {
                                    style.set("loadingZone", "visibility", "visible");
                                },
                                postHook: function() {
                                    style.set("loadingZone", "visibility", "hidden");
                                },
                                callback: lang.hitch(_this, _this.updateDestinationStore),
                                timeout: 8000,
                                errorHandler: function(message) {
                                    UtilUI.exitLoadingState();
                                    console.error(message);
                                    _this.showError();
                                }
                            });
                        }
                    };
                    //on(this.srcTable, "onSelected", lang.hitch(this, this.updateCoordinates));
                    aspect.after(UtilGrid.getTable(this.srcTable), "onSelectedRowsChanged", lang.hitch(this, this.updateCoordinates));
                    on(this.srcSelect, "Change", lang.hitch(this, this.updateCoordinates));
                } // StoreUpdateHandler


                // Connect all coordinate tables:
                new CoordinateUpdateHandler({
                    srcTable: "spatialRefAdminUnit",
                    srcSelect: "spatialRefAdminUnitSelect",
                    dstTable: "spatialRefAdminUnitCoords"
                });

                new CoordinateUpdateHandler({
                    srcTable: "spatialRefLocation",
                    srcSelect: "spatialRefLocationSelect",
                    dstTable: "spatialRefLocationCoords"
                });

            },

            initAdditionalFields: function() {
                console.debug("call function to create dynamic additional fields");
                var self = this;
                var def = createAdditionalFieldsDynamically().then(function() {
                    // remember additional fields for data handling
                    query(".additionalField").forEach(function(item) {
                        self.additionalFieldWidgets.push(registry.getEnclosingWidget(item));
                    });
                });

                return def;
            },

            connectDirtyFlagsEvents: function() {
                // Connect the widgets onChange methods to the setDirtyFlag Method
                // These elements are defined in rules_checker.js!
                var excludeFields = ["spatialRefAdminUnitSelect", "spatialRefAdminUnitCoords", "spatialRefLocationSelect",
                    "spatialRefLocationCoords", "thesaurusFreeTerms", "linksToRelationTypeFilter"
                ];

                var allFields = query(".dijitTextBox, .dijitSelect", "sectionTopObject").map(function(item) {
                    return item.getAttribute("widgetid");
                });
                allFields = allFields.concat(query(".ui-widget, .input .dijitCheckBoxInput", "contentFrameBodyObject").map(function(item) {
                    return item.getAttribute("id");
                }));
                allFields = allFields.concat(query("span .input .dijitTextBox, span .input .dijitSelect, .dijitTabPane.dijitTextBox", "contentFrameBodyObject").map(function(item) {
                    return item.getAttribute("widgetid");
                }));

                // exclude fields that aren't saved
                var filteredFields = array.filter(allFields, function(field) {
                    return array.indexOf(excludeFields, field) == -1;
                });
                array.forEach(filteredFields, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            },

            applyDefaultConnections: function() {
                // textareas do not have a validate function so we have to implement this
                // on our own
                var textAreas = query(".dijitTextArea", "contentFrameBodyObject");
                array.forEach(textAreas, function(element) {
                    var widget = registry.getEnclosingWidget(element);
                    widget.validate = function() {
                        return this.validator();
                    };
                    widget.validator = function() {
                        if (this.required && this.get("value") == "") {
                            domClass.add(this.domNode, "importantBackground");
                            return false;
                        } else {
                            domClass.remove(this.domNode, "importantBackground");
                            return true;
                        }
                    };
                });

                // onResize fix for IE9
                if (has("ie") > 8) {
                    on(window, "resize", function() {
                        registry.byId("dataFormContainer").layout();
                    });
                }

                // add validation to thesaurus (an error should only occur from old data!)
                UtilGrid.getTable("thesaurusTerms").validate = validator.thesaurusValidation;

                // send inspire notifications
                aspect.after(registry.byId("thesaurusInspire"), "onDataChanged", function() {
                    topic.publish("/onInspireTopicChange", {
                        topics: UtilList.tableDataToList(this.getData())
                    });
                });

                // sync tables with same store (or subset of it)
                aspect.after(UtilGrid.getTable("generalAddress"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["ref4ParticipantsLink", "ref4PMLink", "ref2LocationLink"])));
                aspect.after(UtilGrid.getTable("ref4ParticipantsLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["generalAddress"])));
                aspect.after(UtilGrid.getTable("ref4PMLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["generalAddress"])));
                aspect.after(UtilGrid.getTable("ref2LocationLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["generalAddress"])));

                aspect.after(UtilGrid.getTable("linksTo"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["ref1BasisLink", "ref1SymbolsLink", "ref1KeysLink",
                    /*"ref1ServiceLink",*/
                    "ref1DataBasisLink", "ref1ProcessLink",
                    "ref2BaseDataLink", /*"ref3BaseDataLink",*/ "ref5KeysLink",
                    "ref5MethodLink", "ref6BaseDataLink"
                ])));
                aspect.after(UtilGrid.getTable("ref1BasisLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref1SymbolsLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref1KeysLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                //on(UtilGrid.getTable("ref1ServiceLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref1DataBasisLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref1ProcessLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref2BaseDataLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                //on(UtilGrid.getTable("ref3BaseDataLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref5KeysLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref5MethodLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                aspect.after(UtilGrid.getTable("ref6BaseDataLink"), "onDeleteItems", lang.hitch(UtilGrid, lang.partial(UtilGrid.synchedDelete, ["linksTo"])));
                
                // activate default behaviour
                UtilCatalog.getOverrideBehavioursDef().then(function(data) {
                    // mark behaviours with override values
                    array.forEach(data, function(item) {
                        if (behaviour[item.id]) {
                            behaviour[item.id].override = item.active;
                            if (item.params) {
                                array.forEach(item.params, function(p) {
                                    var behaviourParam = array.filter(behaviour[item.id].params, function(param) { return param.id === p.id; })[0];
                                    lang.mixin(behaviourParam, p);
                                });
                            }
                        }
                    });
                    for (var behave in behaviour) {
                        // ignore invalid or address behaviours
                        if (!behaviour[behave].title || behaviour[behave].forAddress) continue; 
                        
                        // run behaviour if 
                        // 1) activated by default and not overridden
                        // 2) activate if explicitly overridden
                        // 3) ignore system behaviours, which were already executed
                        if (behaviour[behave].type !== "SYSTEM" &&
                                (
                                    (behaviour[behave].defaultActive && behaviour[behave].override === undefined)
                                    || behaviour[behave].override === true
                                )) {
                            console.debug("execute behaviour: " + behave);
                            try {
                                behaviour[behave].run();
                            } catch (error) {
                                console.error("Could not execute behaviour: " + behave, error);
                            }
                        }
                    }
                }, function(error) {
                    console.error("Error getting override behaviours:", error);
                });
            }
        })();

        // return public methods
        //    return {
        //        create: objectLayout.create
        //    };

    });
