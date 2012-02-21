dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dijit.form.FilteringSelect");

TextCellEditor = function(args) {
	var input;
	var defaultValue;
	var scope = this;

	this.init = function() {
		this.input = new dijit.form.ValidationTextBox({id:"activeCell_"+args.grid.id, style:"width:100%;color:black; font-family: Verdana, Helvetica, Arial, sans-serif;" }).placeAt(args.container);
		this.input.focus();
	};

	this.destroy = function() {
		this.input.destroy();
		dojo.destroy(this.input.domNode);
	};

	this.focus = function() {
		this.input.focus();
	};

	this.getValue = function() {
		return this.input.get("value");
	};

	this.setValue = function(val) {
		this.input.set("value", val);
	};

	this.loadValue = function(item) {
		this.defaultValue = item[args.column.field] || "";
		this.input.set("value", this.defaultValue);
		dijit.selectInputText(this.input.textbox);
	};

	this.serializeValue = function() {
		return this.input.get("value");
	};

	this.applyValue = function(item, state) {
		item[args.column.field] = state;
	};

	this.isValueChanged = function() {
		return (!(this.input.get("value") == "" && this.defaultValue == null))
				&& (this.input.get("value") != this.defaultValue);
	};

	this.validate = function() {
		if (args.column.validator) {
			var validationResults = args.column.validator(this.input.get("value"));
			if (!validationResults.valid)
				return validationResults;
		}

		return {
			valid : true,
			msg : null
		};
	};

	this.init();
};

DecimalCellEditor = function(args) {
    var input;
    var defaultValue;
    var scope = this;

    this.init = function() {
        this.input = new dijit.form.NumberTextBox({id:"activeCell_"+args.grid.id, style: "width: 100%; color: black; font-family: Verdana, Helvetica, Arial, sans-serif;" }).placeAt(args.container);
        this.input.constraints.places = '0,10';
        var constraints = args.column.constraints; 
        if (constraints) {
            if (UtilGeneral.hasValue(constraints.min)) this.input.constraints.min = constraints.min;
            if (UtilGeneral.hasValue(constraints.max)) this.input.constraints.max = constraints.max;
            if (UtilGeneral.hasValue(constraints.places)) this.input.constraints.places = constraints.places;
        }
        this.input.focus();
    };

    this.destroy = function() {
        // hide Tooltip if any
        dijit.hideTooltip(this.input.domNode);
        this.input.destroy();
        dojo.destroy(this.input.domNode);
    };

    this.focus = function() {
        this.input.focus();
    };

    this.getValue = function() {
        return this.input.get("value");
    };

    this.setValue = function(val) {
        this.input.set("value", val);
    };

    this.loadValue = function(item) {
        this.defaultValue = item[args.column.field] || "";
        this.input.set("value", this.defaultValue);
        dijit.selectInputText(this.input.textbox);
    };

    this.serializeValue = function() {
        return this.input.get("value");
    };

    this.applyValue = function(item, state) {
        item[args.column.field] = state;
    };

    this.isValueChanged = function() {
        return (!(this.input.get("displayedValue") == "" && this.defaultValue == null))
                && (this.input.get("displayedValue") != this.defaultValue);
    };

    this.validate = function() {
        if (args.column.validator) {
            var validationResults = args.column.validator(this.input.get("value"));
            if (!validationResults.valid)
                return validationResults;
        }
        if (!this.input.validate()) {
            return {
                valid : false,
                msg : this.input.invalidMessage
            };
        }

        return {
            valid : true,
            msg : null
        };
    };

    this.init();
};

SelectboxEditor = function(args) {
    var options;
	var box;
    
    // initialize the UI
    this.init = function(){
        if (args.column.listId) 
            data = dojo.clone(sysLists[args.column.listId]);
        else {
            data = [];
            for (var i = 0; i < args.column.options.length; i++) {
                data[i] = [args.column.options[i], args.column.values[i]];
            }
        }
        
        var store = new dojo.data.ItemFileWriteStore({data: {items: data}});
        box = new dijit.form.FilteringSelect({id:"activeCell_"+args.grid.id, store: store, searchAttr: "0", maxHeight:"150", style:"width:100%; padding:0; color: black; font-family: 10px Verdana, Helvetica, Arial, sans-serif;"}).placeAt(args.container);
        box.store.fetch();
        //$(args.container).append(box.domNode);
        box.focus();
    }

    /*********** REQUIRED METHODS ***********/

    this.destroy = function() {
        // hide Tooltip if any
        dijit.hideTooltip(box.domNode);
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
        if (box.item == null)
            return "";
        return box.item[1][0];//("value"); //
    };

    this.loadValue = function(item) {
        // load the value(s) from the data item and update the UI
        // this method will be called immediately after the editor is initialized
        // it may also be called by the grid if if the row/cell being edited is updated via grid.updateRow/updateCell
        var search = item[args.column.field];
        var items = box.store._arrayOfTopLevelItems;
        var found = false;
        dojo.forEach(items, function(item, i) {
            if (item[1] == search) {
                box.set("value", i);
                found = true;
            }
        });
        if (!found)
            box.set("value", item[args.column.field]);
    };

    this.applyValue = function(item,state) {
        // deserialize the value(s) saved to "state" and apply them to the data item
        // this method may get called after the editor itself has been destroyed
        // treat it as an equivalent of a Java/C# "static" method - no instance variables should be accessed
        item[args.column.field] = state;
    };

    this.validate = function() {
        // validate user input and return the result along with the validation message, if any
        // if the input is valid, return {valid:true,msg:null}
        //return { valid: false, msg: "This field is required" };
        return { valid: true, msg: null };         
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
};

ComboboxEditor = function(args) {
    var box;
    var oldValue = "";
    
    // initialize the UI
    this.init = function(){
        var data = [];
        
        if (args.column.listId)
            data = dojo.clone(sysLists[args.column.listId]);
        else {
            if (args.column.options != undefined) {
                data = [];
                for (var i = 0; i < args.column.options.length; i++) {
                    data[i] = [args.column.options[i], args.column.values[i]];
                }
            } else {
                data = [];
            }
        }
        var store = new dojo.data.ItemFileWriteStore({data: {items: data}});
        box = new dijit.form.ComboBox({id:"activeCell_"+args.grid.id, store: store, searchAttr: "0", style:"width:100%; color: black; font-family: 10px Verdana, Helvetica, Arial, sans-serif;"}).placeAt(args.container);
        box.store.fetch();
        box.focus();
    }

    /*********** REQUIRED METHODS ***********/

    this.destroy = function() {
        // remove all data, events & dom elements created in the constructor
        box.destroy();
    };

    this.focus = function() {
        // set the focus on the main input control (if any)
        box.focus()
    };

    this.isValueChanged = function() {
        // return true if the value(s) being edited by the user has/have been changed
        if (box.get("value") == oldValue)
            return false;
        return true;
    };

    this.serializeValue = function() {
        // return the value(s) being edited by the user in a serialized form
        // can be an arbitrary object
        // the only restriction is that it must be a simple object that can be passed around even
        // when the editor itself has been destroyed
        return box.get("value"); //get("displayedValue")???
    };

    this.loadValue = function(item) {
        // load the value(s) from the data item and update the UI
        // this method will be called immediately after the editor is initialized
        // it may also be called by the grid if if the row/cell being edited is updated via grid.updateRow/updateCell
        oldValue = item[args.column.field];
        var items = box.store._arrayOfTopLevelItems;
        var found = false;
        dojo.forEach(items, function(item) {
            if (item[1] == oldValue) {
                box.set("value", item[0]);
                found = true;
            }
        });
        if (!found)
            box.set("value", item[args.column.field]);
    };

    this.applyValue = function(item,state) {
        // deserialize the value(s) saved to "state" and apply them to the data item
        // this method may get called after the editor itself has been destroyed
        // treat it as an equivalent of a Java/C# "static" method - no instance variables should be accessed
        item[args.column.field] = state;
    };

    this.validate = function() {
        // validate user input and return the result along with the validation message, if any
        // if the input is valid, return {valid:true,msg:null}
        //return { valid: false, msg: "This field is required" };
        return { valid: true, msg: null };
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
};

DateCellEditor = function(args){
    var calendar;
    var oldValue = "";
    
    this.init = function(){
        calendar = new dijit.form.DateTextBox({id:"activeCell_"+args.grid.id, style: "width: 100%; color: black; font-family: Verdana, Helvetica, Arial, sans-serif;"}).placeAt(args.container);
        calendar.set("value", new Date());
        calendar.focus();
        dojo.connect(calendar._picker.domNode, "onclick", this, function(evt){
            dojo.stopEvent(evt);
        });
    };
    
    this.destroy = function(){
        if (calendar._picker)
            dojo.destroy(calendar._picker.domNode.parentElement);
        calendar.destroy();
    };
    
    this.show = function(){
        //$.datepicker.dpDiv.stop(true, true).show();
    };
    
    this.hide = function(){
        //$.datepicker.dpDiv.stop(true, true).hide();
    };
    
    this.position = function(position){
    };
    
    this.focus = function(){
        //.focus();
    };
    
    this.loadValue = function(item){
        oldValue = item[args.column.field];
        if (oldValue == undefined)
            calendar.set("value", new Date());
        else
            calendar.set("value", oldValue);
    };
    
    this.serializeValue = function(){
        /*var value = calendar.get("value");
        if (value == null) {
            value = new Date();
            calendar.set("value", value);
        }*/
            
        return calendar.get("value");//value;
    };
    
    this.applyValue = function(item, state){
        item[args.column.field] = state;
    };
    
    this.isValueChanged = function(){
        //return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
        if (calendar.get("value") == oldValue)
            return false;
        return true;
    };
    
    this.validate = function(){
        return {
            valid: true,
            msg: null
        };
    };
    
    this.init();
};

DateCellEditorToString = function(args){
    var calendar;
    var oldValue = "";
    
    this.init = function(){
        calendar = new dijit.form.DateTextBox({id:"activeCell_"+args.grid.id, style: "width: 100%; color: black; font-family: Verdana, Helvetica, Arial, sans-serif;"}).placeAt(args.container);
        calendar.set("value", new Date());
        calendar.focus();
        dojo.connect(calendar._picker.domNode, "onclick", this, function(evt){
            dojo.stopEvent(evt);
        });
    };
    
    this.destroy = function(){
        if (calendar._picker)
            dojo.destroy(calendar._picker.domNode.parentElement);
        calendar.destroy();
    };
    
    this.show = function(){
    };
    
    this.hide = function(){
    };
    
    this.position = function(position){
    };
    
    this.focus = function(){
        calendar.focus();
    };
    
    this.loadValue = function(item){
        oldValue = dojo.date.locale.parse(item[args.column.field], {datePattern: "dd.MM.yyyy", selector: "date"});
        calendar.set("value", oldValue);
    };
    
    this.serializeValue = function(){
        var date = calendar.get("value");
        if (date == null)
            return "";
        return UtilString.getDateString(date, "dd.MM.yyyy");
    };
    
    this.applyValue = function(item, state){
        item[args.column.field] = state;
    };
    
    this.isValueChanged = function(){
        //return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
        if (calendar.get("value") == oldValue)
            return false;
        return true;
    };
    
    this.validate = function(){
        return {
            valid: true,
            msg: null
        };
    };
    
    this.init();
};

YesNoCheckboxCellEditor = function(args) {
    var select;
    var defaultValue;
    var scope = this;

    this.init = function() {
        select = new dijit.form.CheckBox({id:"activeCell_"+args.grid.id,'class':'editor-checkbox'}).placeAt(args.container);
        select.focus();
    };

    this.destroy = function() {
        select.destroy();
    };

    this.focus = function() {
        select.focus();
    };

    this.loadValue = function(item) {
        defaultValue = item[args.column.field];
        if (defaultValue)
            select.set("value", true);
        else
            select.set("value", false);
    };

    this.serializeValue = function() {
        return select.get("value") == "on";
    };

    this.applyValue = function(item,state) {
        item[args.column.field] = state;
    };

    this.isValueChanged = function() {
        return (select.get("value") != defaultValue);
    };

    this.validate = function() {
        return {
            valid: true,
            msg: null
        };
    };

    this.init();
};
