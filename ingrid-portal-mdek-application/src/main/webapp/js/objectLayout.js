ingridObjectLayout = new Object();

objectTemplateCreated = false;


ingridObjectLayout.create = function(){
    ingridObjectLayout.deferredCreation = new dojo.Deferred();
    if (objectTemplateCreated) {
        ingridObjectLayout.deferredCreation.callback();
        return ingridObjectLayout.deferredCreation;
    }
    
    // mark the object template as already created so that it is
    // created only once
    objectTemplateCreated = true;
    
    UtilUI.updateBlockerDivInfo(0);
    setTimeout(dojo.hitch(ingridObjectLayout, ingridObjectLayout.createPartOne), 30);
    return ingridObjectLayout.deferredCreation;
}

ingridObjectLayout.createPartOne = function(){

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
    this.initAdditionalFields();
    console.debug("connect dirty flags");
	this.connectDirtyFlagsEvents();
    console.debug("init CTS");	
	// apply atomatic transformation of bounding box if selected in table
	initCTS();
	
	// apply special validation for necessary components
	console.debug("apply Validations");
    this.applyDefaultConnections();
	
    console.debug("visibility!");
    setVisibilityOfFields(); 
    
	// update view according to initial chosen class
    console.debug("select class");
	igeEvents.selectUDKClass();
    
	// from rules_required.js
    //console.debug("apply Rules");
    // done within profile now!
    //applyRules();
    
    // add a '*' to all labels and display them if an element is required 
    dojo.query(".outer label", "contentFrameBodyObject").forEach("item.innerHTML = dojo.trim(item.innerHTML) + '<span class=\"requiredSign\">*</span>';");
    
    console.debug("toggle");
    // show only required fields initially
    igeEvents.toggleFields(undefined, "showRequired");
    UtilUI.updateBlockerDivInfo(100);
    
    // tell the calling function that we are finished and can proceed
    ingridObjectLayout.deferredCreation.callback();
    
}

ingridObjectLayout.createInfoHeader = function() {
    new dijit.form.ValidationTextBox({
		style: "width:100%;"
    }, "objectName");
    
	var datastoreObjectClasses = function() {
		var def = new dojo.Deferred();
		var data = [
			{value: "Class0", label: message.get("dialog.research.ext.obj.class0")},
			{value: "Class1", label: message.get("dialog.research.ext.obj.class1")},
			{value: "Class2", label: message.get("dialog.research.ext.obj.class2")},
			{value: "Class3", label: message.get("dialog.research.ext.obj.class3")},
			{value: "Class4", label: message.get("dialog.research.ext.obj.class4")},
			{value: "Class5", label: message.get("dialog.research.ext.obj.class5")},
            {value: "Class6", label: message.get("dialog.research.ext.obj.class6")}            	
		];
		
		def.callback(data);
		return def;
	}
	
	var storeProps = {data: {identifier: 'value',label: 'label'}};
	createSelectBox("objectClass", null, storeProps, datastoreObjectClasses);
	dijit.byId("objectClass").onChange = igeEvents.selectUDKClass;
    
	var storeProps = {data: {identifier: '1',label: '0'}};
	createSelectBox("objectOwner", null, storeProps, null);
}

ingridObjectLayout.createGeneralInfo = function(){
    new dijit.form.ValidationTextBox({
        style: "width:100%;"
    }, "generalShortDesc");
    
    new dijit.form.SimpleTextarea({
		//required: true,
        rows: 10,
        style: "width: 100%;"
    }, "generalDesc");
    
    var structure = [
		{field: 'nameOfRelation',name: 'name',width: '120px',
        	type: ComboboxEditor,
        	options: [], // will be filled later, when syslists are loaded
        	//values: [],
        	editable: true,
            listId: 505
//!!!			formatter: emptyOrNullValidation
/*
	        formatter: function(value){
	            return UtilList.getSelectDisplayValue(this, value);
	        }*/
    	}, 
		{field: 'icon',name: 'i',width: '23px', editable: false/*!!!formatter: emptyOrNullValidation*/},
		{field: 'linkLabel',name: 'label',width: 565-scrollBarWidth+'px', editable: false/*!!!formatter: emptyOrNullValidation*/}
	];
    
    console.debug("generalAddress");
    createDataGrid("generalAddress", null, structure, null);
    
    var thesaurusInspireStructure = [
        {field: 'title',name: 'title',width: 708-scrollBarWidth+'px',
            type: SelectboxEditor,
            options: [], // will be filled later, when syslists are loaded
            values: [],
            editable: true,
            listId: 6100,
            formatter: dojo.partial(SyslistCellFormatter, 6100)
        }
    ];
    createDataGrid("thesaurusInspire", null, thesaurusInspireStructure, null);
    
    new dijit.form.CheckBox({}, "isInspireRelevant");
}

ingridObjectLayout.createFachBezugClass1 = function(){
    
    var tabRef1 = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref1BasisTabContainer");
    
	var tabRef1Tab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type1.technicalBasisTable.tab.text"), "class": "textAreaFull"}, "ref1BasisText");
    
    var tabRef1Tab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.technicalBasisTable.tab.links")
    }, "ref1BasisTab2");
    
    var ref1Structure = [
	   {field: 'icon',name: 'icon',width: '23px'}, 
	   {field: 'linkLabel',name: 'linkLabel',width: 685-scrollBarWidth-12+'px'}
	];
    createDataGrid("ref1BasisLink", null, ref1Structure, null);
    
    tabRef1.addChild(tabRef1Tab1);
    tabRef1.addChild(tabRef1Tab2);
    tabRef1.startup();
	
	dojo.connect(tabRef1, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref1BasisTab2"));
	
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref1ObjectIdentifier");
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createSelectBox("ref1DataSet", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(525);
    });
    
    new dijit.form.NumberTextBox({style:"width:25%;", constraints:{min:0, max:100}}, "ref1Coverage");
    
    var ref1RepresentStructure = [
	   {field: 'title',name: 'title',width: 348-scrollBarWidth+'px',type: SelectboxEditor,
	       options: [], // will be filled later, when syslists are loaded
           values: [],
           editable: true,
            listId: 526,
           formatter: dojo.partial(SyslistCellFormatter, 526)
	   }
	];
    createDataGrid("ref1Representation", null, ref1RepresentStructure, null);
    
    createFilteringSelect("ref1VFormatTopology", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(528);
    });
    
    //new dijit.form.NumberTextBox({}, "elementNumberEditor");
    var ref1VFormatDetailsStructure = [{
        field: 'geometryType',
        name: message.get("ui.obj.type1.vectorFormat.detailsTable.header.geoType"),
        width: '120px',
        type: SelectboxEditor,
        options: [], // will be filled later, when syslists are loaded
        values: [],
        editable: true,
        listId: 515,
        formatter: dojo.partial(SyslistCellFormatter, 515)
    }, {
        field: 'numElements',
        name: message.get("ui.obj.type1.vectorFormat.detailsTable.header.elementCount"),
        width: 330-scrollBarWidth+'px',
        editable: true
    }];
    createDataGrid("ref1VFormatDetails", null, ref1VFormatDetailsStructure, null);
    
    
    createComboBox("ref1SpatialSystem", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(100);
    });
	
    var ref1ScaleStructure = [{
        field: 'scale',
        name: message.get("ui.obj.type1.scaleTable.header.scale"),
        width: '105px',
        editable: true
    }, {
        field: 'groundResolution',
        name: message.get("ui.obj.type1.scaleTable.header.groundResolution"),
        width: '285px',
        editable: true
    }, {
        field: 'scanResolution',
        name: message.get("ui.obj.type1.scaleTable.header.scanResolution"),
        width: 318-scrollBarWidth+'px',
        editable: true
    }];
    createDataGrid("ref1Scale", null, ref1ScaleStructure, null);
    
    new dijit.form.NumberTextBox({style:"width:100%;"}, "ref1AltAccuracy");
    new dijit.form.NumberTextBox({style:"width:100%;"}, "ref1PosAccuracy");
    
    var tabSymbols = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref1SymbolsTabContainer");
	
	dojo.connect(tabSymbols, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref1SymbolsTab2"));
    
    var tabSymbolsTab1 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.symbolCatTable.tab.text")
    }, "ref1SymbolsTab1");
    
    var ref1SymbolsTextStructure = [
		{field: 'title',name: message.get("ui.obj.type1.symbolCatTable.header.title"),width: '435px',editable: true,
            type: ComboboxEditor,
            listId: 3555,
            formatter: dojo.partial(SyslistCellFormatter, 3555)/*!!!, formatter: emptyOrNullValidation*/}, 
		{field: 'date',name: message.get("ui.obj.type1.symbolCatTable.header.date"),width: '120px',	type: DateCellEditor,
        	editable: true,
            formatter: DateCellFormatter
    	}, 
		{field: 'version',name: message.get("ui.obj.type1.symbolCatTable.header.version"),width: 153-scrollBarWidth-12+'px',editable: true/*!!!, formatter: emptyOrNullValidation*/}];
    createDataGrid("ref1SymbolsText", null, ref1SymbolsTextStructure, null);
    
    var tabSymbolsTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.symbolCatTable.tab.links")
    }, "ref1SymbolsTab2");
	
		var ref1SymbolsLinkStructure = [
			{field: 'icon',name: 'icon',width: '23px'}, 
			{field: 'linkLabel',name: 'linkLabel',width: 685-scrollBarWidth-12+'px'}
		];
	    createDataGrid("ref1SymbolsLink", null, ref1SymbolsLinkStructure, null);
    
    tabSymbols.addChild(tabSymbolsTab1);
    tabSymbols.addChild(tabSymbolsTab2);
    tabSymbols.startup();
	
    var tabKeys = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref1KeysTabContainer");
    
    var tabKeysTab1 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.keyCatTable.tab.text")
    }, "ref1KeysTab1");
    
    var ref1KeysTextStructure = [{
        field: 'title',
        name: message.get("ui.obj.type1.keyCatTable.header.title"),
        width: '435px',
        editable: true,
        type: ComboboxEditor,
        listId: 3535,
        formatter: dojo.partial(SyslistCellFormatter, 3535)
    }, {
        field: 'date',
        name: message.get("ui.obj.type1.keyCatTable.header.date"),
        width: '120px',
		type: DateCellEditor,
        editable: true,
        formatter: DateCellFormatter
    }, {
        field: 'version',
        name: message.get("ui.obj.type1.keyCatTable.header.version"),
        width: 153-scrollBarWidth-12+'px',
        editable: true
    }];
    createDataGrid("ref1KeysText", null, ref1KeysTextStructure, null);
    
    var tabKeysTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.keyCatTable.tab.links")
    }, "ref1KeysTab2");
    
    var ref1KeysLinkStructure = [{
        field: 'icon',
        name: 'icon',
        width: '23px'
    }, {
        field: 'linkLabel',
        name: 'linkLabel',
        width: 685-scrollBarWidth-12+'px'
    }];
    createDataGrid("ref1KeysLink", null, ref1KeysLinkStructure, null);
    
    tabKeys.addChild(tabKeysTab1);
    tabKeys.addChild(tabKeysTab2);
    tabKeys.startup();
	
	dojo.connect(tabKeys, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref1KeysTab2"));
    
    var ref1ServiceLinkStructure = [{
        field: 'icon',
        name: 'icon',
        width: '23px'
    }, {
        field: 'linkLabel',
        name: 'linkLabel',
        width: 685-scrollBarWidth+'px'
    }];
    createDataGrid("ref1ServiceLink", null, ref1ServiceLinkStructure, null);
    
    var tabDataBasis = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref1DataBasisTabContainer");
    
    /*var tabDataBasisTab1 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.dataBasisTable.tab.text")
    }, "ref1DataBasisTab1");*/
    
    var tabDataBasisTab1 = new dijit.form.SimpleTextarea({title: "Text", "class": "textAreaFull"}, "ref1DataBasisText");
    
    var tabDataBasisTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.dataBasisTable.tab.links")
    }, "ref1DataBasisTab2");
    
    var ref1DataBasisLinkStructure = [{
        field: 'icon',
        name: 'icon',
        width: '23px'
    }, {
        field: 'linkLabel',
        name: 'linkLabel',
        width: 685-scrollBarWidth-12+'px'
    }];
    createDataGrid("ref1DataBasisLink", null, ref1DataBasisLinkStructure, null);
    tabDataBasis.addChild(tabDataBasisTab1);
    tabDataBasis.addChild(tabDataBasisTab2);
    tabDataBasis.startup();
	
	dojo.connect(tabDataBasis, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref1DataBasisTab2"));
    
    var ref1DataStructure = [{
        field: 'title',
        name: 'title',
		width: 708-scrollBarWidth+'px',
        editable: true
    }];
    createDataGrid("ref1Data", null, ref1DataStructure, null);
    
    var tabRef1Process = new dijit.layout.TabContainer({
        id: "ref1ProcessTabContainer",
        style: "width: 100%;",
        doLayout: false
    }).placeAt("ref1ProcessTabContainerSpace");//, "ref1ProcessTabContainer"); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    /*var tabRef1ProcessTab1 = new dijit.layout.ContentPane({
        title: "Text"
    }, "ref1ProcessTab1");*/
    var tabRef1ProcessTab1 = new dijit.form.SimpleTextarea({title:message.get("ui.obj.type1.processTable.tab.text"), "class": "textAreaFull"}, "ref1ProcessText");
    var tabRef1ProcessTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type1.processTable.tab.links")
    }, "ref1ProcessTab2");
    
    var ref1ProcessLinkStructure = [{
        field: 'icon',
        name: 'icon',
        width: '23px'
    }, {
        field: 'linkLabel',
        name: 'linkLabel',
        width: 685-scrollBarWidth-12+'px'
    }];
    createDataGrid("ref1ProcessLink", null, ref1ProcessLinkStructure, null);
    tabRef1Process.addChild(tabRef1ProcessTab1);
    tabRef1Process.addChild(tabRef1ProcessTab2);
    tabRef1Process.startup();
	
	dojo.connect(tabRef1Process, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref1ProcessTab2"));
}

ingridObjectLayout.createRefClass1DQ = function() {
    var dq109TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7109
            values: [],
            editable: true,
            listId: 7109,
            formatter: dojo.partial(SyslistCellFormatter, 7109)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq109Table", null, dq109TableStructure, null);
    
    var dq110TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7110
            values: [],
            editable: true,
            listId: 7110,
            formatter: dojo.partial(SyslistCellFormatter, 7110)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq110Table", null, dq110TableStructure, null);
    
    var dq112TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7112
            values: [],
            editable: true,
            listId: 7112,
            formatter: dojo.partial(SyslistCellFormatter, 7112)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq112Table", null, dq112TableStructure, null);
    
    var dq113TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7113
            values: [],
            editable: true,
            listId: 7113,
            formatter: dojo.partial(SyslistCellFormatter, 7113)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq113Table", null, dq113TableStructure, null);
    
    var dq114TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7114
            values: [],
            editable: true,
            listId: 7114,
            formatter: dojo.partial(SyslistCellFormatter, 7114)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq114Table", null, dq114TableStructure, null);
    
    var dq115TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7115
            values: [],
            editable: true,
            listId: 7115,
            formatter: dojo.partial(SyslistCellFormatter, 7115)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq115Table", null, dq115TableStructure, null);
    
    var dq117TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7117
            values: [],
            editable: true,
            listId: 7117,
            formatter: dojo.partial(SyslistCellFormatter, 7117)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq117Table", null, dq117TableStructure, null);
    
    var dq120TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7120
            values: [],
            editable: true,
            listId: 7120,
            formatter: dojo.partial(SyslistCellFormatter, 7120)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq120Table", null, dq120TableStructure, null);
    
    var dq125TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7125
            values: [],
            editable: true,
            listId: 7125,
            formatter: dojo.partial(SyslistCellFormatter, 7125)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq125Table", null, dq125TableStructure, null);
    
    var dq126TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7126
            values: [],
            editable: true,
            listId: 7126,
            formatter: dojo.partial(SyslistCellFormatter, 7126)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq126Table", null, dq126TableStructure, null);
    
    var dq127TableStructure = [
        {field: 'nameOfMeasure',name: message.get("ui.obj.dq.table.header1"),width: '300px',
            type: ComboboxEditor,
            options: [], // will be filled later, when syslists are loaded listId=7127
            values: [],
            editable: true,
            listId: 7127,
            formatter: dojo.partial(SyslistCellFormatter, 7127)
        }, 
        {field: 'resultValue',name: message.get("ui.obj.dq.table.header2"),width: '105px', editable: true,
            type: DecimalCellEditor, widgetClass: dijit.form.NumberTextBox, formatter: LocalizedNumberFormatter},
        {field: 'measureDescription',name: message.get("ui.obj.dq.table.header3"),width: 303-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("dq127Table", null, dq127TableStructure, null);
}

ingridObjectLayout.createFachBezugClass2 = function(){

    new dijit.form.SimpleTextarea({style:"width:100%;"}, "ref2Author");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2Publisher");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishedIn");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishLocation");
    
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishedInIssue");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishedInPages");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishedInYear");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishedISBN");
    new dijit.form.ValidationTextBox({style:"width:100%;"}, "ref2PublishedPublisher");
    
    var ref2LocationTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref2LocationTabContainer");
    
    var ref2LocationTab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type2.locationTable.tab.text"), "class": "textAreaFull"}, "ref2LocationText");
    
    var ref2LocationTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type2.locationTable.tab.links")
    }, "ref2LocationTab2");
    
    var ref2LocationLinkStructure = [{
        field: 'icon',
        name: 'title',
        width: '23px'
    }, {
        field: 'linkLabel',
        name: 'date',
        width: 325-scrollBarWidth-12+'px'
    }];
    createDataGrid("ref2LocationLink", null, ref2LocationLinkStructure, null);
    
    ref2LocationTabContainer.addChild(ref2LocationTab1);
    ref2LocationTabContainer.addChild(ref2LocationTab2);
    ref2LocationTabContainer.startup();
	
	dojo.connect(ref2LocationTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref2LocationTab2"));
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createComboBox("ref2DocumentType", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(3385);
    });
    
    var ref2BaseDataTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref2BaseDataTabContainer");
    
	var ref2BaseDataTab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type2.generalDataTable.tab.text"), "class": "textAreaFull"}, "ref2BaseDataText");
    
    var ref2BaseDataTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type2.generalDataTable.tab.links")
    }, "ref2BaseDataTab2");
    
    var ref2BaseDataLinkStructure = [{
        field: 'icon',
        name: 'title',
        width: '23px'
    }, {
        field: 'linkLabel',
        name: 'date',
        width: 325-scrollBarWidth-12+'px'
    }];
    createDataGrid("ref2BaseDataLink", null, ref2BaseDataLinkStructure, null);
    
    ref2BaseDataTabContainer.addChild(ref2BaseDataTab1);
    ref2BaseDataTabContainer.addChild(ref2BaseDataTab2);
    ref2BaseDataTabContainer.startup();
	
	dojo.connect(ref2BaseDataTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref2BaseDataTab2"));
    
    new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "ref2BibData");
    new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "ref2Explanation");
}

ingridObjectLayout.createFachBezugClass3 = function(){
    var ref3ServiceTypeTableStructure = [
		{field: 'title',name: 'title',width: 708-scrollBarWidth+'px',
        type: SelectboxEditor,
        options: [], // will be filled later, when syslists are loaded
        values: [],
        listId: 5200,
        editable: true,
        formatter: dojo.partial(SyslistCellFormatter, 5200)
        //formatter: function(value){
		//	return UtilList.getSelectDisplayValue(this, value);
        //}
    }];
    createDataGrid("ref3ServiceTypeTable", null, ref3ServiceTypeTableStructure, null);
    
	var storeProps = {data: {identifier: '1',label: '0'}};
    createSelectBox("ref3ServiceType", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(5100);
    });
    
	var ref3ServiceVersionStructure = [{
        field: 'title',name: 'title',width: 348-scrollBarWidth+'px',
		editable: true
    }];
    createDataGrid("ref3ServiceVersion", null, ref3ServiceVersionStructure, null);
	
	var ref3ScaleStructure = [
		{field: 'scale',name: message.get("ui.obj.type3.scaleTable.header.scale"),width: '100px',editable: true},
		{field: 'groundResolution',name: message.get("ui.obj.type3.scaleTable.header.groundResolution"),width: '285px',editable: true},
    	{field: 'scanResolution',name: message.get("ui.obj.type3.scaleTable.header.scanResolution"),width: 323-scrollBarWidth+'px',editable: true}
	];
    createDataGrid("ref3Scale", null, ref3ScaleStructure, null);
	
    new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "ref3SystemEnv");
	new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "ref3History");
	
	var ref3BaseDataTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref3BaseDataTabContainer");
    
    var ref3BaseDataTab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type2.generalDataTable.tab.text"), "class": "textAreaFull"}, "ref3BaseDataText");
	
	var ref3MethodTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type2.generalDataTable.tab.links")
    }, "ref3MethodTab2");
	
	var ref3BaseDataLinkStructure = [
		{field: 'icon',name: 'icon',width: '23px'},
		{field: 'linkLabel',name: 'linkLabel',width: 325-scrollBarWidth-12+'px'}
	];
    createDataGrid("ref3BaseDataLink", null, ref3BaseDataLinkStructure, null);

	ref3BaseDataTabContainer.addChild(ref3BaseDataTab1);
    ref3BaseDataTabContainer.addChild(ref3MethodTab2);
    ref3BaseDataTabContainer.startup();
	
	dojo.connect(ref3BaseDataTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref3MethodTab2"));
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:5}, "ref3Explanation");
	
	var ref3OperationStructure = [
		{field: 'name',name: message.get("ui.obj.type3.operationTable.header.name") ,width: '165px'},
		{field: 'description',name: message.get("ui.obj.type3.operationTable.header.description"),width: 543-scrollBarWidth+'px'}
	];
    createDataGrid("ref3Operation", null, ref3OperationStructure, null);
    
    
    new dijit.form.CheckBox({}, "ref3HasAccessConstraint");
}

ingridObjectLayout.createFachBezugClass4 = function(){
	var ref4ParticipantsTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref4ParticipantsTabContainer");
    
    var ref4ParticipantsTab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type2.locationTable.tab.text"), "class": "textAreaFull"}, "ref4ParticipantsText");
		
	var ref4ParticipantsTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type2.locationTable.tab.links")
    }, "ref4ParticipantsTab2");
	
		var ref4ParticipantsLinkStructure = [
			{field: 'icon',name: 'icon',width: '23px'},
			{field: 'linkLabel',name: 'linkLabel',width: 325-scrollBarWidth-12+'px'}
		];
	    createDataGrid("ref4ParticipantsLink", null, ref4ParticipantsLinkStructure, null);
		
	ref4ParticipantsTabContainer.addChild(ref4ParticipantsTab1);
    ref4ParticipantsTabContainer.addChild(ref4ParticipantsTab2);
    ref4ParticipantsTabContainer.startup();
	
	dojo.connect(ref4ParticipantsTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref4ParticipantsTab2"));
	
	var ref4PMTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref4PMTabContainer");
    
    var ref4PMTab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type4.projectManagerTable.tab.text"), "class": "textAreaFull"}, "ref4PMText");
		
	var ref4PMTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type4.projectManagerTable.tab.links")
    }, "ref4PMTab2");
	
		var ref4ParticipantsLinkStructure = [
			{field: 'icon',name: 'icon',width: '23px'},
			{field: 'linkLabel',name: 'linkLabel',width: 325-scrollBarWidth-12+'px'}
		];
	    createDataGrid("ref4PMLink", null, ref4ParticipantsLinkStructure, null);
		
	ref4PMTabContainer.addChild(ref4PMTab1);
    ref4PMTabContainer.addChild(ref4PMTab2);
    ref4PMTabContainer.startup();
	
	dojo.connect(ref4PMTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref4PMTab2"));
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "ref4Explanation");
	
}

ingridObjectLayout.createFachBezugClass5 = function(){
	var ref5dbContentStructure = [
		{field: 'parameter',name: message.get("ui.obj.type5.contentTable.header.parameter"),width: '335px', editable: true},
		{field: 'additionalData',name: message.get("ui.obj.type5.contentTable.header.additionalData"),width: 373-scrollBarWidth+'px', editable: true}
	];
    createDataGrid("ref5dbContent", null, ref5dbContentStructure, null);
	
	
	var ref5MethodTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref5MethodTabContainer");
    
    var ref5MethodTab1 = new dijit.form.SimpleTextarea({title: message.get("ui.obj.type5.methodTable.tab.text"), "class": "textAreaFull"}, "ref5MethodText");
	
	var ref5MethodTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type5.methodTable.tab.links")
    }, "ref5MethodTab2");
	
		var ref5MethodLinkStructure = [
			{field: 'icon',name: '&nbsp;',width: '23px'},
			{field: 'linkLabel',name: 'linkLabel',width: 325-scrollBarWidth-12+'px'}
		];
	    createDataGrid("ref5MethodLink", null, ref5MethodLinkStructure, null);
	
	ref5MethodTabContainer.addChild(ref5MethodTab1);
    ref5MethodTabContainer.addChild(ref5MethodTab2);
    ref5MethodTabContainer.startup();
	
	dojo.connect(ref5MethodTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref5MethodTab2"));
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:5}, "ref5Explanation");
	
}

ingridObjectLayout.createFachBezugClass6 = function(){
    console.debug("createFachBezugClass6");
    var storeProps = {data: {identifier: '1',label: '0'}};
    createFilteringSelect("ref6ServiceType", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(5300);
    });
    
    var ref6ServiceVersionStructure = [
        {field: 'title',name: 'title',width: 348-scrollBarWidth+'px', editable: true}
    ];
    createDataGrid("ref6ServiceVersion", null, ref6ServiceVersionStructure, null);
    console.debug("ref6ser");
    
    new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:5}, "ref6SystemEnv");
    
    new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:5}, "ref6History");
    
    var ref6BaseDataTabContainer = new dijit.layout.TabContainer({
        style: "width: 100%;",
        doLayout: false
    }, "ref6BaseDataTabContainer");
    
    var ref6BaseDataTab1 = new dijit.form.SimpleTextarea({"class": "textAreaFull", title:message.get("ui.obj.type6.generalDataTable.tab.text")}, "ref6BaseDataText");
    
    var ref6BaseDataTab2 = new dijit.layout.ContentPane({
        title: message.get("ui.obj.type6.generalDataTable.tab.links")
    }, "ref6MethodTab2");
    
    var ref6BaseDataLinkStructure = [
        {field: 'icon',name: 'icon',width: '23px'},
        {field: 'linkLabel',name: 'Objekte',width: 325-scrollBarWidth-12+'px'}
    ];
    console.debug("ref6base");
    createDataGrid("ref6BaseDataLink", null, ref6BaseDataLinkStructure, null);
    console.debug("ref6baseE");
    ref6BaseDataTabContainer.addChild(ref6BaseDataTab1);
    ref6BaseDataTabContainer.addChild(ref6BaseDataTab2);
    ref6BaseDataTabContainer.startup();
    
    dojo.connect(ref6BaseDataTabContainer, "selectChild", dojo.partial(UtilUI.toggleFunctionalLink, "ref6MethodTab2"));
    
    new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:5}, "ref6Explanation");
    
    var ref6UrlListStructure = [
        {field: 'name',name: message.get("ui.obj.type6.urlList.header.name"),width: '225px', editable: true},
        {field: 'url',name: message.get("ui.obj.type6.urlList.header.url"),width: '225px', editable: true},
        {field: 'urlDescription',name: message.get("ui.obj.type6.urlList.header.urlDescription"),width: 258-scrollBarWidth+'px', editable: true}
    ];
    console.debug("ref6UrlList");
    createDataGrid("ref6UrlList", null, ref6UrlListStructure, null);
    //UtilGrid.getTable("ref6UrlList").onCellChange.subscribe(addServiceUrlValidation);
    //UtilGrid.getTable("ref6UrlList").onAddNewRow.subscribe(addServiceUrlValidation);
}

ingridObjectLayout.createRaumbezug = function() {
	var spatialRefAdminUnitStructure = [
		{field: 'label',name: message.get("ui.obj.spatial.geoThesTable.header.name") ,width: '331px'},
		{field: 'longitude1',name: message.get("ui.obj.spatial.geoThesTable.header.longitude1"),width: '90px', formatter: LocalizedNumberFormatter},
		{field: 'latitude1',name: message.get("ui.obj.spatial.geoThesTable.header.latitude1"),width: '90px', formatter: LocalizedNumberFormatter},
		{field: 'longitude2',name: message.get("ui.obj.spatial.geoThesTable.header.longitude2"),width: '90px', formatter: LocalizedNumberFormatter},
		{field: 'latitude2',name: message.get("ui.obj.spatial.geoThesTable.header.latitude2"),width: 107-scrollBarWidth+'px', formatter: LocalizedNumberFormatter}
	];
    createDataGrid("spatialRefAdminUnit", null, spatialRefAdminUnitStructure, null);
	
	//new dijit.form.Select({},"spatialRefAdminUnitSelect");
	var spatialData = function() {
		var def = new dojo.Deferred();
		var data = [
			{value: "GEO84", label: message.get("dialog.research.ext.obj.coordinates.wgs84")},
			{value: "GEO_BESSEL_POTSDAM", label: message.get("dialog.research.ext.obj.coordinates.geobp")},
			{value: "GK2", label: message.get("dialog.research.ext.obj.coordinates.gk2")},
			{value: "GK3", label: message.get("dialog.research.ext.obj.coordinates.gk3")},
			{value: "GK4", label: message.get("dialog.research.ext.obj.coordinates.gk4")},
			{value: "GK5", label: message.get("dialog.research.ext.obj.coordinates.gk5")},
			{value: "UTM32W", label: message.get("dialog.research.ext.obj.coordinates.utm32w")},
			{value: "UTM33W", label: message.get("dialog.research.ext.obj.coordinates.utm33w")},
			{value: "UTM32S", label: message.get("dialog.research.ext.obj.coordinates.utm32s")},
			{value: "UTM33S", label: message.get("dialog.research.ext.obj.coordinates.utm33s")},
			{value: "LAMGW", label: message.get("dialog.research.ext.obj.coordinates.lamgw")}
		];
		
		def.callback(data);
		return def;
	}
	var storeProps = {data: {identifier: 'value',label: 'label'}};
	createSelectBox("spatialRefAdminUnitSelect", null, storeProps, spatialData);
    dijit.byId("spatialRefAdminUnitSelect").set("value", "GEO84");
    
	
	var emptyData = function() {
		var def = new dojo.Deferred();
		var data = [
			{longitude1: "", latitude1: "", longitude2: "", latitude2: ""}
		];
		
		def.callback(data);
		return def;
	}
	
	var spatialRefAdminUnitCoordsStructure = [
		{field: 'longitude1',name: 'longitude1',width: '86px'},
		{field: 'latitude1',name: 'latitude1',width: '86px'},
		{field: 'longitude2',name: 'longitude2',width: '86px'},
		{field: 'latitude2',name: 'latitude2',width: '85px'}
	];
    createDataGrid("spatialRefAdminUnitCoords", null, spatialRefAdminUnitCoordsStructure, emptyData);
	
    // set constraints for WGS84 (see http://technet.microsoft.com/de-de/library/dd220425.aspx)
    var latitudeConstraint  = {min:-90, max:90};
    var longitudeConstraint = {min:-180, max:180};
	var spatialRefLocationStructure = [
		{field: 'name',name: message.get("ui.obj.spatial.geoTable.header.name"),width: '331px', editable: true, type: ComboboxEditor,
			options: [],
            listId: 1100,
            formatter: dojo.partial(SyslistCellFormatter, 1100),
			formatter: dojo.partial(emptyOrNullValidation, "spatialRefLocation")
		},
		{field: 'longitude1',name: message.get("ui.obj.spatial.geoTable.header.longitude1"),width: '90px', editable: true, type: DecimalCellEditor, constraints: longitudeConstraint, formatter: LocalizedNumberFormatter/*!!!, formatter: minMaxBoundingBoxValidation*/},
		{field: 'latitude1',name: message.get("ui.obj.spatial.geoTable.header.latitude1"),width: '90px', editable: true, type: DecimalCellEditor, constraints: latitudeConstraint, formatter: LocalizedNumberFormatter/*!!!, formatter: minMaxBoundingBoxValidation*/},
		{field: 'longitude2',name: message.get("ui.obj.spatial.geoTable.header.longitude2"),width: '90px', editable: true, type: DecimalCellEditor, constraints: longitudeConstraint, formatter: LocalizedNumberFormatter/*!!!, formatter: minMaxBoundingBoxValidation*/},
		{field: 'latitude2',name: message.get("ui.obj.spatial.geoTable.header.latitude2"),width: 107-scrollBarWidth+'px', editable: true, type: DecimalCellEditor, constraints: latitudeConstraint, formatter: LocalizedNumberFormatter/*!!!, formatter: minMaxBoundingBoxValidation*/}
	];
    createDataGrid("spatialRefLocation", null, spatialRefLocationStructure, null);
    // UtilGrid.getTable("spatialRefLocation").onCellChange.subscribe(minMaxBoundingBoxValidation); // done in profile!!!
    
	//new dijit.form.Select({},"spatialRefLocationSelect");
	createSelectBox("spatialRefLocationSelect", null, storeProps, spatialData);
    dijit.byId("spatialRefLocationSelect").set("value", "GEO84");
	
	createDataGrid("spatialRefLocationCoords", null, spatialRefAdminUnitCoordsStructure, emptyData);
	
	new dijit.form.NumberTextBox({style:"width:80px;"}, "spatialRefAltMin");
	new dijit.form.NumberTextBox({style:"width:80px;"}, "spatialRefAltMax");
	
	var storeProps = {data: {identifier: '1',label: '0'}};
	createFilteringSelect("spatialRefAltMeasure", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(102);
    });
	
	createComboBox("spatialRefAltVDate", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(101);
    });
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:8}, "spatialRefExplanation");
	
}

ingridObjectLayout.createZeitbezug = function() {
	var storeProps = {data: {identifier: '1',label: '0'}};
	var timeRefTableStructure = [
		{field: 'date',name: message.get("ui.obj.time.timeRefTable.header.date"),width: '120px',
			type: DateCellEditor,
	        editable: true,
	        formatter: DateCellFormatter
		},
		{field: 'type',name: message.get("ui.obj.time.timeRefTable.header.type"),width: 228-scrollBarWidth+'px',
			type: SelectboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 502,
	        formatter: dojo.partial(SyslistCellFormatter, 502)
		}
	];
    createDataGrid("timeRefTable", null, timeRefTableStructure, null);
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull", rows:7}, "timeRefExplanation");
	
	var timeTypeData = function() {
		var def = new dojo.Deferred();
		/*var data = [
			{value: "am", label: message.get("dialog.research.ext.obj.content.time.at")},
			{value: "seit", label: message.get("dialog.research.ext.obj.content.time.since")},
			{value: "bis", label: message.get("dialog.research.ext.obj.content.time.until")},
			{value: "von", label: message.get("dialog.research.ext.obj.content.time.fromto")}
		];*/
		var data = [
			[[message.get("dialog.research.ext.obj.content.time.at")], ["am"]],
			[[message.get("dialog.research.ext.obj.content.time.since")], ["seit"]],
			[[message.get("dialog.research.ext.obj.content.time.until")], ["bis"]],
			[[message.get("dialog.research.ext.obj.content.time.fromto")], ["von"]]
		];
		
		def.callback(data);
		return def;
	}
	
	//new dijit.form.Select({}, "timeRefType");
	var storePropsTimeType = {data: {identifier: '1',label: '0'}};
	createFilteringSelect("timeRefType", null, storePropsTimeType, timeTypeData);
	new dijit.form.DateTextBox({style: "width: 100%;", onChange: function(){if (arguments[0]) dijit.byId('timeRefDate2').constraints.min = arguments[0]}}, "timeRefDate1");
	new dijit.form.DateTextBox({style: "width: 100%;", onChange: function(){if (arguments[0]) dijit.byId('timeRefDate1').constraints.max = arguments[0]}}, "timeRefDate2");
	
	var storeProps = {data: {identifier: '1',label: '0'}};
	createFilteringSelect("timeRefStatus", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(523);
    });
	
	createFilteringSelect("timeRefPeriodicity", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(518);
    });
	
	new dijit.form.NumberTextBox({style: "width: 65%;"}, "timeRefIntervalNum");
	
	createFilteringSelect("timeRefIntervalUnit", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(1230);
    });
}


ingridObjectLayout.createExtraInfo = function() {
	var storeProps = {data: {identifier: '1',label: '0'}};
	createSelectBox("extraInfoLangMetaData", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(99999999);
    });
	
	createSelectBox("extraInfoLangData", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(99999999);
    });
	
	createSelectBox("extraInfoPublishArea", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(3571);
    });
    
    createSelectBox("extraInfoCharSetData", null, storeProps, function(){
        return UtilSyslist.getSyslistEntry(510);
    });
	
	//createSelectBox("extraInfoConformityLevelEditor", null, storeProps, function(){
    //    return UtilSyslist.getSyslistEntry(6000);
    //});
	
	//new dijit.form.ValidationTextBox({}, "extraInfoConformitySpecificationEditor");
	
	//new dijit.form.DateTextBox({}, "extraInfoConformityDatePicker");
	
	var extraInfoConformityTableStructure = [
		{field: 'level',name: message.get("ui.obj.additionalInfo.conformityTable.header.level"),width: '150px',
			type: SelectboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 6000,
	        formatter: dojo.partial(SyslistCellFormatter, 6000)
		},
		{field: 'specification',name: message.get("ui.obj.additionalInfo.conformityTable.header.specification"),width: '414px', editable: true},
		{field: 'date',name: message.get("ui.obj.additionalInfo.conformityTable.header.date"),width: 144-scrollBarWidth+'px',
			type: DateCellEditor,
	        editable: true,
            formatter: DateCellFormatter
		}
	];
    createDataGrid("extraInfoConformityTable", null, extraInfoConformityTableStructure, null);
	
	var extraInfoLegalBasicsTableStructure = [
		{field: 'title',name: 'title',width: 470-scrollBarWidth+'px',
			type: ComboboxEditor,
	        editable: true,
            listId: 1350,
            formatter: dojo.partial(SyslistCellFormatter, 1350)
		}
	];
    createDataGrid("extraInfoLegalBasicsTable", null, extraInfoLegalBasicsTableStructure, null);
	
	var extraInfoXMLExportTableStructure = [
		{field: 'title',name: 'title',width: 225-scrollBarWidth+'px',
			type: ComboboxEditor,
	        editable: true,
	        listId: 1370,
            formatter: dojo.partial(SyslistCellFormatter, 1370)
		}
	];
    createDataGrid("extraInfoXMLExportTable", null, extraInfoXMLExportTableStructure, null);
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "extraInfoPurpose");
	new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "extraInfoUse");
    
}

ingridObjectLayout.createAvailability = function() {
	var availabilityAccessConstraintsStructure = [
		{field: 'title',name: 'title',width: 348-scrollBarWidth+'px',
			type: ComboboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 6010,
            formatter: dojo.partial(SyslistCellFormatter, 6010)
		}
	];
    createDataGrid("availabilityAccessConstraints", null, availabilityAccessConstraintsStructure, null);
    
    var availabilityUseConstraintsStructure = [
        {field: 'title',name: 'title',width: 348-scrollBarWidth+'px',editable: true}
    ];
    createDataGrid("availabilityUseConstraints", null, availabilityUseConstraintsStructure, null);
	
	var availabilityDataFormatStructure = [
		{field: 'name',name: message.get("ui.obj.availability.dataFormatTable.header.name"),width: '100px',
			type: ComboboxEditor,
	        editable: true,
            listId: 1320,
            formatter: dojo.partial(SyslistCellFormatter, 1320)
		},
		{field: 'version',name: message.get("ui.obj.availability.dataFormatTable.header.version"),width: '125px',editable: true},
		{field: 'compression',name: message.get("ui.obj.availability.dataFormatTable.header.compression"),width: '205px',editable: true},
		{field: 'pixelDepth',name: message.get("ui.obj.availability.dataFormatTable.header.depth"),width: 278-scrollBarWidth+'px',editable: true}
	];
    createDataGrid("availabilityDataFormat", null, availabilityDataFormatStructure, null);
	
	var availabilityMediaOptionsStructure = [
		{field: 'name',name: message.get("ui.obj.availability.mediaOptionTable.header.type"),width: '250px',
			type: SelectboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 520,
            formatter: dojo.partial(SyslistCellFormatter, 520)
		},
		{field: 'transferSize',name: message.get("ui.obj.availability.mediaOptionTable.header.amount"),width: '250px',editable: true},
		{field: 'location',name: message.get("ui.obj.availability.mediaOptionTable.header.location"),width: 208-scrollBarWidth+'px',editable: true}
	];
    createDataGrid("availabilityMediaOptions", null, availabilityMediaOptionsStructure, null);
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "availabilityOrderInfo");
    
    var storeProps = {data: {identifier: '1',label: '0'}};
    createComboBox("availabilityDataFormatInspire", null, dojo.clone(storeProps), function(){
        return UtilSyslist.getSyslistEntry(6300);
    });
	
}


ingridObjectLayout.createThesaurus = function() {
	var thesaurusTopicsStructure = [
		{field: 'title',name: 'title',width: 708-scrollBarWidth+'px',
			type: SelectboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 527,
            formatter: dojo.partial(SyslistCellFormatter, 527)
		}
	];
    createDataGrid("thesaurusTopics", null, thesaurusTopicsStructure, null);
	
	var thesaurusTermsStructure = [
		{field: 'label',name: 'label',width: '550px'},
		{field: 'sourceString',name: 'sourceString',width: 158-scrollBarWidth+'px'}
	];
    createDataGrid("thesaurusTerms", null, thesaurusTermsStructure, null);
	
	new dijit.form.SimpleTextarea({"class": "textAreaFull"}, "thesaurusFreeTerms");
	
	var button = new dijit.form.Button({style:"margin-right: 0px;"}, "thesaurusFreeTermsAddButton");
	button._inputFieldWidget = dijit.byId("thesaurusFreeTerms");
	button._termListWidget = "thesaurusTerms";
	button.onClick = igeEvents.executeSearch;
	
	new dijit.form.CheckBox({}, "thesaurusEnvExtRes");
	
	
	var thesaurusEnvTopicsStructure = [
		{field: 'title',name: 'title',width: 348-scrollBarWidth+'px',
			type: SelectboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 1410,
            formatter: dojo.partial(SyslistCellFormatter, 1410)
		}
	];
    createDataGrid("thesaurusEnvTopics", null, thesaurusEnvTopicsStructure, null);
	
	var thesaurusEnvCatsStructure = [
		{field: 'title',name: 'title',width: 348-scrollBarWidth+'px',
			type: SelectboxEditor,
	        options: [], // will be filled later, when syslists are loaded
	        values: [],
	        editable: true,
            listId: 1400,
            formatter: dojo.partial(SyslistCellFormatter, 1400)
		}
	];
    createDataGrid("thesaurusEnvCats", null, thesaurusEnvCatsStructure, null);
	
}


ingridObjectLayout.createReferences = function(){
    var linksToStructure = [
		{field: 'icon',name: 'icon',width: '23px'}, 
		{field: 'linkLabel',name: 'linkLabel',width: 325-scrollBarWidth+'px'}
	];
    createDataGrid("linksTo", null, linksToStructure, null);
    
    var linksFromStructure = [
		{field: 'icon',name: 'icon',width: '23px'}, 
		{field: 'linkLabel',name: 'linkLabel',width: 325-scrollBarWidth+'px'}
	];
    createDataGrid("linksFrom", null, linksFromStructure, null);
}


// Initialize the Coordinate Transformation Service
function initCTS() {
	
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
	 *   new CoordinateUpdateHandler({srcTable: dijit.byId("src"), dstTable: dijit.byId("dst")});
	 *
	 */
	function CoordinateUpdateHandler(args){
		this.srcTable = args.srcTable;
		this.srcSelect = dijit.byId(args.srcSelect);
		this.dstTable = args.dstTable;
		
		this.updateDestinationStore = function(ctsResponse) {
            var coord = ctsResponse.coordinate;
            if (coord == null) 
                this.showError();
            else {
                //var data = UtilGrid.getTableData(this.dstTable)[0];
                UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude1', coord.longitude1);
                UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude1', coord.latitude1);
                UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude2', coord.longitude2);
                UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude2', coord.latitude2);
            }
		}

		this.clearDstStore = function() {
			//var data = UtilGrid.getTableData(this.dstTable)[0];
			var clearValue = "";
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude1', clearValue);
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude1', clearValue);
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude2', clearValue);
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude2', clearValue);
		}

		this.showError = function() {
			//var data = UtilGrid.getTableData(this.dstTable)[0];
			var clearValue = "D.n.e.";
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude1', clearValue);
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude1', clearValue);
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'longitude2', clearValue);
			UtilGrid.updateTableDataRowAttr(this.dstTable, 0, 'latitude2', clearValue);
		}

		this.updateCoordinates = function() {
			console.debug("update coordinates: " + this.srcTable);
			var fromSRS = "GEO84";
			var selectedData = UtilGrid.getSelectedData(this.srcTable);//.selection.getSelected();//this.srcTable.getSelectedData();
			if (!selectedData[0] || selectedData[0] == null)
				return;
				
			this.clearDstStore();
			if (selectedData.length != 1) {
				return;
			}
			var coords = selectedData[0];
			var toSRS = this.srcSelect.getValue();
			var realNumberFormat = { format: ["#?.#?????????????????????", "#?????????????????????", "-#?.#?????????????????????", "-#?????????????????????", "#?,#?????????????????????", "-#?,#?????????????????????"]};
			if (coords && toSRS
					&& dojox.validate.isNumberFormat(coords.longitude1, realNumberFormat)
					&& dojox.validate.isNumberFormat(coords.longitude2, realNumberFormat)
					&& dojox.validate.isNumberFormat(coords.latitude1, realNumberFormat)
					&& dojox.validate.isNumberFormat(coords.latitude2, realNumberFormat)) {
//				console.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
				var _this = this;
				CTService.getCoordinates(fromSRS, toSRS, coords, {
						preHook: function() {dojo.style("loadingZone", "visibility", "visible");},
						postHook: function() {dojo.style("loadingZone", "visibility", "hidden");},
						callback: dojo.hitch(this, this.updateDestinationStore),
						timeout:8000,
						errorHandler:function(message) {
							UtilDWR.exitLoadingState();
							console.debug(message);
							_this.showError();
						}
					}
				);
			}
		}
		//dojo.connect(this.srcTable, "onSelected", dojo.hitch(this, this.updateCoordinates));
        dojo.connect(UtilGrid.getTable(this.srcTable), "onSelectedRowsChanged", dojo.hitch(this, this.updateCoordinates));
		dojo.connect(this.srcSelect, "onChange", dojo.hitch(this, this.updateCoordinates));
	}	// StoreUpdateHandler

	
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

}

ingridObjectLayout.initAdditionalFields = function() {
	var def = new dojo.Deferred();

	//var language = UtilCatalog.getCatalogLanguage();
    
	console.debug("call function to create dynamic additional fields");
	createAdditionalFieldsDynamically(); 
    
    // remember additional fields for data handling
    dojo.query(".additionalField").forEach(function(item) {
            additionalFieldWidgets.push(dijit.getEnclosingWidget(item));
    });
	
	return def;
}

ingridObjectLayout.connectDirtyFlagsEvents = function() {
	// Connect the widgets onChange methods to the setDirtyFlag Method
	// These elements are defined in rules_checker.js!
    var excludeFields = ["spatialRefAdminUnitSelect", "spatialRefAdminUnitCoords", "spatialRefLocationSelect", 
        "spatialRefLocationCoords", "thesaurusFreeTerms"];
    
    var allFields = dojo.query(".dijitTextBox, .dijitSelect", "sectionTopObject").map(function(item) {return item.getAttribute("widgetid");});
    allFields = allFields.concat(dojo.query(".ui-widget, .input .dijitCheckBoxInput", "contentFrameBodyObject").map(function(item) {return item.getAttribute("id");}));
    allFields = allFields.concat(dojo.query("span .input .dijitTextBox, span .input .dijitSelect, .dijitTabPane.dijitTextBox", "contentFrameBodyObject").map(function(item) {return item.getAttribute("widgetid");}));
    
    // exclude fields that aren't saved
    var filteredFields = dojo.filter(allFields, function(field) {
        return dojo.indexOf(excludeFields, field) == -1;
    });
    dojo.forEach(filteredFields, _connectWidgetWithDirtyFlag);
}

ingridObjectLayout.applyDefaultConnections = function() {
    // textareas do not have a validate function so we have to implement this
    // on our own
    var textAreas = dojo.query(".dijitTextArea", "contentFrameBodyObject");
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

    // sync tables with same store (or subset of it)
    dojo.connect(UtilGrid.getTable("generalAddress"), "onDeleteItems", dojo.partial(UtilGrid.synchedDelete, ["ref4ParticipantsLink", "ref4PMLink", "ref2LocationLink"]));
    dojo.connect(UtilGrid.getTable("ref4ParticipantsLink"), "onDeleteItems", dojo.partial(UtilGrid.synchedDelete, ["generalAddress"]));
    dojo.connect(UtilGrid.getTable("ref4PMLink"), "onDeleteItems", dojo.partial(UtilGrid.synchedDelete, ["generalAddress"]));
    dojo.connect(UtilGrid.getTable("ref2LocationLink"), "onDeleteItems", dojo.partial(UtilGrid.synchedDelete, ["generalAddress"]));
    //dojo.connect(UtilGrid.getTable("linksTo"), "onDeleteItems", dojo.partial(UtilGrid.synchedDelete, ["generalAddress"])); // onSetData -> more complicated

}
