ingridAddressLayout = new Object();
addressTemplateCreated = false;

ingridAddressLayout.create = function(){
    ingridAddressLayout.deferredCreation = new dojo.Deferred();
    if (addressTemplateCreated) {
        ingridAddressLayout.deferredCreation.callback();
        return ingridAddressLayout.deferredCreation;
    }
    
    // mark the object template as already created so that it is
    // created only once
    addressTemplateCreated = true;
    
    UtilUI.updateBlockerDivInfo(0);
    setTimeout(dojo.hitch(ingridAddressLayout, ingridAddressLayout.createPartOne), 10);
    return ingridAddressLayout.deferredCreation;
}


ingridAddressLayout.createPartOne = function() {
    console.debug("create form elements for addresses");
    this.createInfoHeader();
    
    this.createType0();
    this.createType1();
    this.createType2();
    this.createType3();
    
    this.createAddressAndTasks();
    
    this.createAddressThesaurus();
    
    console.debug("associated");
    this.createAssociatedObj();
    
    //this.assignContextMenu();
    console.debug("validation");
    this.applyValidations();
    
    //this.initSysLists();
    console.debug("dirty");
    this.connectDirtyFlagsEvents();
    
    // add a '*' to all labels and display them if an element is required 
    dojo.query(".outer label", "contentFrameBodyAddress").forEach("item.innerHTML = dojo.trim(item.innerHTML) + '<span class=\"requiredSign\">*</span>';");
    
    console.debug("toggle");
    // show only required fields initially
    igeEvents.toggleFields(undefined, "showRequired");
    
    UtilUI.updateBlockerDivInfo(100);
    
    // tell the calling function that we are finished and can proceed
    ingridAddressLayout.deferredCreation.callback();
}


ingridAddressLayout.createInfoHeader = function () {
    //new dijit.layout.ContentPane({id:"headerFrameAddress"}, "sectionTopAddress");
    new dijit.form.ValidationTextBox({style: "width:100%;", disabled: 'disabled'}, "addressTitle");
    var addrClassText = new dijit.form.ValidationTextBox({style: "width:100%;", disabled: 'disabled'}, "addressType");
    //dojo.connect(addrClassText, "onChange", igeEvents.selectUDKAddressType);
    addrClassText.onChange = igeEvents.selectUDKAddressType;
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createSelectBox("addressOwner", null, storeProps, null);

    //new dijit.layout.ContentPane({}, "contentFrameAddress");
}


ingridAddressLayout.createType0 = function() {
    new dijit.form.SimpleTextarea({"class": "textAreaFull", required: true}, "headerAddressType0Unit");
}

ingridAddressLayout.createType1 = function(){
    new dijit.form.SimpleTextarea({"class": "textAreaFull", disabled:"disabled"}, "headerAddressType1Institution");
    new dijit.form.SimpleTextarea({'class':"textAreaFull", required: true}, "headerAddressType1Unit");
}

ingridAddressLayout.createType2 = function(){
    new dijit.form.SimpleTextarea({"class": "textAreaFull", disabled:"disabled"}, "headerAddressType2Institution");
    new dijit.form.ValidationTextBox({style: "width:100%;",required: true}, "headerAddressType2Lastname");
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "headerAddressType2Firstname");
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createComboBox("headerAddressType2Style", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(4300);
    });
    createComboBox("headerAddressType2Title", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(4305);
    });
    new dijit.form.CheckBox({}, "headerAddressType2HideAddress");
}

ingridAddressLayout.createType3 = function(){
    new dijit.form.ValidationTextBox({style: "width:100%;",required: true}, "headerAddressType3Lastname");
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "headerAddressType3Firstname");
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createComboBox("headerAddressType3Style", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(4300);
    });
    createComboBox("headerAddressType3Title", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(4305);
    });
    
    new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "headerAddressType3Institution");
}

ingridAddressLayout.createAddressAndTasks = function(){
    var addressComStructure = [
        {field: 'medium',name: message.get('ui.adr.details.communicationTable.header.medium'),width: '80px',
        type: ComboboxEditor,
        options: [], // will be filled later, when syslists are loaded
        //values: [],
        editable: true,
        listId: 4430,
        formatter: dojo.partial(SyslistCellFormatter, 4430)},        
        {field: 'value',name: message.get("ui.adr.details.communicationTable.header.value"),width: 268-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("addressCom", null, addressComStructure, null);
    
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "addressStreet");
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "addressZipCode");
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "addressCity");
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "addressPOBox");
    new dijit.form.ValidationTextBox({style: "width:100%;"}, "addressZipPOBox");
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createFilteringSelect("addressCountry", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(6200);
    });
    
    var button = new dijit.form.Button({}, "buttonGetAddressFromParent");
    button.onClick = igeEvents.getAddressDataFromParent;
    
    //DELETED TextArea: "addressNotes" (INGRID33-10)
    new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "addressTasks");
}

ingridAddressLayout.createAddressThesaurus = function(){
    var thesaurusTermsAddressStructure = [
        {field: 'label',name: 'label',width: '600px'},
        {field: 'sourceString',name: 'sourceString',width: 108-scrollBarWidth+'px'}
    ];
    createDataGrid("thesaurusTermsAddress", null, thesaurusTermsAddressStructure, null);
    
    new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "thesaurusFreeTermInputAddress");
    
    var button = new dijit.form.Button({}, "thesaurusFreeTermsAddressAddButton");
    button._inputFieldWidget = dijit.byId("thesaurusFreeTermInputAddress");
    button._termListWidget = "thesaurusTermsAddress";
    button.onClick = igeEvents.executeSearch;
}

ingridAddressLayout.createAssociatedObj = function() {
    var associatedObjNameStructure = [
        {field: 'icon',name: 'icon',width: '23px'},
        {field: 'linkLabel',name: 'linkLabel',width: 685-scrollBarWidth+'px;'}
    ];
    createDataGrid("associatedObjName", null, associatedObjNameStructure, null);
}

/*
ingridAddressLayout.initSysLists = function() {
    var sysTableElements = [["addressCom", 4430, 0]];
    
    dojo.forEach(sysTableElements, function(el){
        dojo.forEach(sysLists[el[1]], function(item){
            dijit.byId(el[0]).structure[el[2]].options.push(item[0]);
            //if (el[0] != "generalAddress" && el[0] != "availabilityDataFormat" && el[0] != "extraInfoXMLExportTable" && el[0] != "extraInfoLegalBasicsTable")
            //dijit.byId(el[0]).structure[el[2]].values.push(item[1]);
        });
    });
}*/

ingridAddressLayout.assignContextMenu = function() {
    var tablesWithContextMenu = ["addressCom", "thesaurusTermsAddress"];
    generalContextMenu = createGeneralTableContextMenu(tablesWithContextMenu);
}

ingridAddressLayout.applyValidations = function(){
    communicationValidation();
    
    // Address fields which must not be empty
    // done with required attribut within widget! When validate() is called it must not be emtpy!
    /*dijit.byId("headerAddressType0Unit").validator = function(value) { return dojo.trim(value) != ""; };
    dijit.byId("headerAddressType1Unit").validator = function(value) { return dojo.trim(value) != ""; };
    dijit.byId("headerAddressType2Lastname").validator = function(value) { return dojo.trim(value) != ""; };
    dijit.byId("headerAddressType3Lastname").validator = function(value){ return dojo.trim(value) != ""; };*/

    // textareas do not have a validate function so we have to implement this
    // on our own
    var textAreas = dojo.query(".dijitTextArea", "contentFrameBodyAddress");
    dojo.forEach(textAreas, function(element) {
        dijit.getEnclosingWidget(element).validate = function() {
            if (this.required && this.get("value") == "") {
                dojo.addClass(this.domNode, "importantBackground");
                return false;
            } else {
                dojo.removeClass(this.domNode, "importantBackground");
                return true;
            }
        };
    });
    
    applyBeforeAddressPublishValidation();
}

ingridAddressLayout.connectDirtyFlagsEvents = function() {
    // Connect the widgets onChange methods to the setDirtyFlag Method
    // These elements are defined in rules_checker.js!
    
    dojo.forEach(adrUiInputElements, _connectWidgetWithDirtyFlag);
    dojo.forEach(adrClass0UiInputElements, _connectWidgetWithDirtyFlag);
    dojo.forEach(adrClass1UiInputElements, _connectWidgetWithDirtyFlag);
    dojo.forEach(adrClass2UiInputElements, _connectWidgetWithDirtyFlag);
    dojo.forEach(adrClass3UiInputElements, _connectWidgetWithDirtyFlag);
}