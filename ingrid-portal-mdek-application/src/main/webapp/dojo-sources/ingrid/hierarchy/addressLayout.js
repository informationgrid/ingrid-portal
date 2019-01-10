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
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/query",
    "dojo/dom-class",
    "dojo/Deferred",
    "dojo/on",
    "dojox/widget/Standby",
    "dijit/registry",
    "dijit/form/Button",
    "dijit/form/ValidationTextBox",
    "dijit/form/SimpleTextarea",
    "ingrid/utils/Catalog",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist",
    "ingrid/utils/Thesaurus",
    "ingrid/message",
    "ingrid/layoutCreator",
    "ingrid/hierarchy/behaviours.user",
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/validation",
    "ingrid/hierarchy/dirty"
], function(declare, lang, array, query, domClass, Deferred, on, Standby, registry, Button, ValidationTextBox, SimpleTextarea,
            UtilCatalog, UtilUI, UtilSyslist, UtilThesaurus,
            message, layoutCreator, behaviour, igeEvents, gridEditors, gridFormatters, validator, dirty) {
        
    return declare( null, {
        
        addressTemplateCreated: false,
        
        deferredCreation: null,
        
        create: function(){
            this.deferredCreation = new Deferred();
            if (this.addressTemplateCreated) {
                this.deferredCreation.resolve();
                return this.deferredCreation;
            }

            domClass.add("loadBlockDiv", "blockerFull");
            
            // mark the object template as already created so that it is
            // created only once
            this.addressTemplateCreated = true;
            
            UtilUI.initBlockerDivInfo( "createAddresses", 2, message.get("general.init") );
            UtilUI.updateBlockerDivInfo( "createAddresses" );
            setTimeout(lang.hitch(this, this.createPartOne), 10);
            return this.deferredCreation;
        },
        
        
        createPartOne: function() {
            console.debug("create form elements for addresses");
            this.createInfoHeader();
            
            this.createType0();
            this.createType1();
            this.createType2();
            this.createType3();
            
            this.createAddressAndTasks();
            
            this.createAddressThesaurus();
            
            this.createAdditionalInfo();
            
            console.debug("associated");
            this.createAssociatedObj();
            
            //this.assignContextMenu();
            console.debug("validation");
            this.applyValidations();

            this.applyBehaviours();
            
            //this.initSysLists();
            console.debug("dirty");
            this.connectDirtyFlagsEvents();
            
            // add a '*' to all labels and display them if an element is required 
            query(".outer label", "contentFrameBodyAddress").forEach(function(item) {
                item.innerHTML = lang.trim(item.innerHTML) + '<span class=\"requiredSign\">*</span>';
            });
            
            // mark all the content of a special marked container 
            query(".oneClickMark", "dataFormContainer").on("click", function() {
                UtilUI.selectTextInContainer( this );
            });
            
            console.debug("toggle");
            // show only required fields initially
            igeEvents.toggleFields(undefined, "showRequired");
            
            UtilUI.updateBlockerDivInfo( "createAddresses" );
            
            // tell the calling function that we are finished and can proceed
            this.deferredCreation.resolve();

            domClass.remove("loadBlockDiv", "blockerFull");
        },
        
        
        createInfoHeader: function () {
            //new dijit.layout.ContentPane({id:"headerFrameAddress"}, "sectionTopAddress");
            new ValidationTextBox({maxLength: 255, style: "width:100%;", disabled: 'disabled'}, "addressTitle");
            var addrClassText = new ValidationTextBox({style: "width:100%;", disabled: 'disabled'}, "addressType");
            //on(addrClassText, "onChange", igeEvents.selectUDKAddressType);
            addrClassText.onChange = lang.hitch(igeEvents, igeEvents.selectUDKAddressType);
            
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createFilteringSelect("addressOwner", null, storeProps, null);
        
            var owner = registry.byId("addressOwner");
            
            // show a busy state when lazy loading data
            var standby = new Standby({target: "addressOwner"});
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
            		UtilCatalog.updateResponsibleUserAddressList(currentUdk).then( lang.hitch( this, function() {
            			standby.hide();
	            		this._isLoaded = true;
	            		this._startSearch(this.focusNode.value);
            		} ) );
            	}
            }
            owner.loadDropDown = function(/*Function*/ loadCallback){
            	standby.show();
            	UtilCatalog.updateResponsibleUserAddressList(currentUdk).then( lang.hitch( this, function() {
            		standby.hide();
            		this._isLoaded = true;
            		this._startSearchAll(); 
            	} ) );
    		};
            
        },
        
        
        createType0: function() {
            new SimpleTextarea({"class": "textAreaFull", required: true}, "headerAddressType0Unit");
        },
        
        createType1: function(){
            new SimpleTextarea({"class": "textAreaFull", disabled:"disabled"}, "headerAddressType1Institution");
            new SimpleTextarea({'class':"textAreaFull", required: true}, "headerAddressType1Unit");
        },
        
        createType2: function(){
            new SimpleTextarea({"class": "textAreaFull", disabled:"disabled"}, "headerAddressType2Institution");
            new ValidationTextBox({style: "width:100%;",required: true}, "headerAddressType2Lastname");
            new ValidationTextBox({style: "width:100%;"}, "headerAddressType2Firstname");
            
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createComboBox("headerAddressType2Style", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(4300);
            });
            layoutCreator.createComboBox("headerAddressType2Title", null, lang.clone(storeProps), function(){
                return UtilSyslist.getSyslistEntry(4305);
            });
            new dijit.form.CheckBox({}, "headerAddressType2HideAddress");
        },
        
        createType3: function(){
            new ValidationTextBox({style: "width:100%;",required: true}, "headerAddressType3Lastname");
            new ValidationTextBox({style: "width:100%;"}, "headerAddressType3Firstname");
            
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createComboBox("headerAddressType3Style", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(4300);
            });
            layoutCreator.createComboBox("headerAddressType3Title", null, lang.clone(storeProps), function(){
                return UtilSyslist.getSyslistEntry(4305);
            });
            
            new SimpleTextarea({"class": "textAreaFull"}, "headerAddressType3Institution");
        },
        
        createAddressAndTasks: function(){
            var addressComStructure = [
                {field: 'medium',name: message.get('ui.adr.details.communicationTable.header.medium'),width: '80px',
                type: gridEditors.ComboboxEditor,
                options: [], // will be filled later, when syslists are loaded
                //values: [],
                editable: true,
                listId: 4430,
                formatter: dojo.partial(gridFormatters.SyslistCellFormatter, 4430)},
                {field: 'value',name: message.get("ui.adr.details.communicationTable.header.value"),width: '268px', editable: true}
            ];
            layoutCreator.createDataGrid("addressCom", null, addressComStructure, null);
            
            new ValidationTextBox({style: "width:100%;"}, "addressStreet");
            new ValidationTextBox({style: "width:100%;"}, "addressZipCode");
            new ValidationTextBox({style: "width:100%;"}, "addressCity");
            new ValidationTextBox({style: "width:100%;"}, "addressPOBox");
            new ValidationTextBox({style: "width:100%;"}, "addressZipPOBox");
            
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createFilteringSelect("addressCountry", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(6200);
            });
            
            layoutCreator.createComboBox("addressAdministrativeArea", null, lang.clone(storeProps), function(){
                return UtilSyslist.getSyslistEntry(6250);
            });
            
            var button = new Button({}, "buttonGetAddressFromParent");
            on(button, "Click", lang.hitch(igeEvents, igeEvents.getAddressDataFromParent));
            
            //DELETED TextArea: "addressNotes" (INGRID33-10)
            new SimpleTextarea({"class": "textAreaFull"}, "addressTasks");
            
            new ValidationTextBox({style: "width:100%;"}, "addressHoursOfService");
            
        },
        
        createAddressThesaurus: function(){
            var thesaurusTermsAddressStructure = [
                {field: 'label',name: 'label',width: '600px'},
                {field: 'sourceString',name: 'sourceString',width: '108px'}
            ];
            layoutCreator.createDataGrid("thesaurusTermsAddress", null, thesaurusTermsAddressStructure, null);
            
            var freeTermsWidget = new SimpleTextarea({"class": "textAreaFull"}, "thesaurusFreeTermInputAddress");
            
            var button = new Button({}, "thesaurusFreeTermsAddressAddButton");
            on(button, "Click", function() {
                var termList = UtilThesaurus.parseQueryTerm(freeTermsWidget.get('value'));
                var callerInfo = { id: this.id, _termListWidget: "thesaurusTermsAddress" };
                lang.hitch(igeEvents, igeEvents.addKeywords(termList, callerInfo));
                freeTermsWidget.attr("value", "", true);
            });

        },
        
        createAdditionalInfo: function() {
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createSelectBox("extraInfoPublishAreaAddress0", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(3571);
            });
            layoutCreator.createSelectBox("extraInfoPublishAreaAddress1", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(3571);
            });
            layoutCreator.createSelectBox("extraInfoPublishAreaAddress2", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(3571);
            });
            layoutCreator.createSelectBox("extraInfoPublishAreaAddress3", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(3571);
            });
        },
        
        createAssociatedObj: function() {
            var associatedObjNameStructure = [
                {field: 'icon',name: 'icon',width: '23px'},
                {field: 'linkLabel',name: 'linkLabel',width: '685px;'}
            ];
            layoutCreator.createDataGrid("associatedObjName", null, associatedObjNameStructure, null);
        },
        
        
        applyValidations: function(){
            validator.communicationValidation();

            // textareas do not have a validate function so we have to implement this
            // on our own
            var textAreas = query(".dijitTextArea", "contentFrameBodyAddress");
            array.forEach(textAreas, function(element) {
                dijit.getEnclosingWidget(element).validate = function() {
                    if (this.required && this.get("value") == "") {
                        domClass.add(this.domNode, "importantBackground");
                        return false;
                    } else {
                        domClass.remove(this.domNode, "importantBackground");
                        return true;
                    }
                };
            });
            
            validator.applyBeforeAddressPublishValidation();
        },

        applyBehaviours: function() {
            // activate default behaviour
            UtilCatalog.getOverrideBehavioursDef().then(function(data) {
                // mark behaviours with override values
                array.forEach(data, function(item) {
                    behaviour[item.id].override = item.active;
                });
                for (var behave in behaviour) {
                    // ignore invalid and non-address behaviours
                    if (!behaviour[behave].title || !behaviour[behave].forAddress) continue;
                    
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
                        behaviour[behave].run();
                    }
                }
            }, function(error) {
                console.error("Error executing behvaiour:", error);
            });
        },
        
        connectDirtyFlagsEvents: function() {
            // Connect the widgets onChange methods to the setDirtyFlag Method

            var excludeFields = ["thesaurusFreeTermInputAddress"];
            
            var allFields = query(".dijitTextBox, .dijitSelect", "sectionTopAddress").map(function(item) {
                return item.getAttribute("widgetid");
            });
            allFields = allFields.concat(query(".ui-widget, .input .dijitCheckBoxInput", "contentFrameBodyAddress").map(function(item) {
                return item.getAttribute("id");
            }));
            allFields = allFields.concat(query("span .input .dijitTextBox, span .input .dijitSelect, .dijitTabPane.dijitTextBox", "contentFrameBodyAddress").map(function(item) {
                return item.getAttribute("widgetid");
            }));

            // exclude fields that aren't saved
            var filteredFields = array.filter(allFields, function(field) {
                return array.indexOf(excludeFields, field) == -1;
            });
            array.forEach(filteredFields, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        }
    })();
});
