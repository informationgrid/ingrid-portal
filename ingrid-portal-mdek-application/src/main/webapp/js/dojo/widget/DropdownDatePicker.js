dojo.provide("ingrid.widget.DropdownDatePicker");

dojo.require("dojo.widget.DropdownDatePicker");

/**
 * DropdownDatePicker
 */
dojo.widget.defineWidget(
	"ingrid.widget.DropdownDatePicker",
	dojo.widget.DropdownDatePicker,
{
  iconURL: dojo.uri.moduleUri("ingrid", "widget/templates/images/dateIcon.gif"),

  /*
   * Overriden to also accept localized date strings (necessary, when used as celleditor)
   */ 
  setValue: function(/*Date|String*/rfcDate) {
    if (rfcDate == "") {rfcDate = new Date();}
    try {
      dojo.widget.DropdownDatePicker.prototype.setValue.apply(this, arguments);
    }
    catch(e) {
      var inputDate = dojo.date.parse(rfcDate, {formatLength:this.formatLength, datePattern:this.displayFormat, selector:'dateOnly', locale:this.lang});			
      if(inputDate)
        this.setDate(inputDate);
    }
  },

  /*
   * Overriden to return a localized date string
   */ 
  getValue: function() {
    // be sure to have the current value in this.valueNode.value
    this.onInputChange();

    // get the value and format it
    var value = dojo.widget.DropdownDatePicker.prototype.getValue.apply(this, arguments);
    if (value) {
      var date = dojo.date.parse(value, {formatLength:this.formatLength, datePattern:'yyyy-MM-dd', selector:'dateOnly'});
	  return date;
    } else {
      return "";
    }
  },

  /*
   * Clears the input field and stored date
   */
  clearValue: function() {
	this.valueNode.value = "";
	this.datePicker.value = "";
	this._updateText();
  },
/*
	getDisplayValue: function() {
		// get the value and format it
		var date = dojo.widget.DropdownDatePicker.prototype.getDate.apply(this, arguments);
		if (date) {
			return dojo.date.format(date, {formatLength:this.formatLength, datePattern:this.displayFormat, selector:'dateOnly'});
		} else {
			return "";
		}
	},
*/

	onValueChanged: function(/*Date*/dateObj){
    try {
      // focus the editor if the value changed (usefull, when used as celleditor)
      this.inputNode.focus();
    }
    catch(e) {}
	},

	onShow: function(){
    ingrid.widget.DropdownDatePicker.superclass.onShow.apply(this, arguments);
    this.inputNode.focus();
	},
	onInputChange: function(){
		// summary: callback when user manually types a date into the <input> field
		if(this.dateFormat){
			dojo.deprecated("dojo.widget.DropdownDatePicker",
			"Cannot parse user input.  Must use displayFormat attribute instead of dateFormat.  See dojo.date.format for specification.", "0.5");
		}else{
			var input = dojo.string.trim(this.inputNode.value);
			if(input){
				var inputDate = dojo.date.parse(input,
						{formatLength:this.formatLength, datePattern:"dd.MM.yy", selector:'dateOnly', locale:this.lang});			
				if(inputDate){
					this.setDate(inputDate);
				}
			} else {
				this.valueNode.value = input;
			}
		}
		// If the date entered didn't parse, reset to the old date.  KISS, for now.
		//TODO: usability?  should we provide more feedback somehow? an error notice?
		// seems redundant to do this if the parse failed, but at least until we have validation,
		// this will fix up the display of entries like 01/32/2006
		if(input){ this._updateText(); }
	}
});

// extend DatePicker to use our style sheet
dojo.lang.extend(dojo.widget.DatePicker,
{
  templateCssPath:dojo.uri.moduleUri("ingrid", "widget/templates/DatePicker.css")
});
