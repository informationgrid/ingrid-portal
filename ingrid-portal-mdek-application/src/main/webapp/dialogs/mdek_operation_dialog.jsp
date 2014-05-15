<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.operationTable.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />


<script type="text/javascript">
var operationsScriptScope = _container_;

var isNewOperation = false;

var inputElements = 
	["operationsName", "operationsNameSelect", "operationsDescription", "operationsPlatform", "operationsCall",
	 "operationsParameter", "operationsAddress", "operationsDependencies", "saveButton"];//, "cancelButton"];

// NOTICE: "operationsName" OR "operationsNameSelect" dependent from type of service is added below !
var requiredElements = [["operationsPlatform", "operationsPlatformLabel"],
					    ["operationsAddress", "operationsAddressLabel"]];

var _opNameIsTextInput = null;

// data passed by caller of this dialog
var caller = {};

dojo.connect(operationsScriptScope, "onLoad", function(){
    // take over passed data
    if (operationsScriptScope.customParams) {
        caller = operationsScriptScope.customParams;
    }

    var def = createDOMElements();
	def.then(init);
	def.then(disableGUIOnWrongPermission);

    console.log("Publishing event: '/afterInitDialog/Operations'");
    dojo.publish("/afterInitDialog/Operations");
});

/*
dojo.connect(operationsScriptScope, "onUnload", function(){
	UtilGrid.getTable(caller.gridId).invalidate();
});
*/

function createDOMElements() {
	var storeProps = {data: {identifier: '1',label: '0'}};
	var def = createSelectBox("operationsNameSelect", null, storeProps, initOperationNameInput);
	
	var operationsPlatformStructure = [
		{field: 'title',name: "<fmt:message key='dialog.operation.opName' />",width: 340-scrollBarWidth+'px',
            type: SelectboxEditor,
            options: [],
            values: [],
            editable: true,
            listId: 5180,
            formatter: dojo.partial(SyslistCellFormatter, 5180)
        }
	];
    createDataGrid("operationsPlatform", null, operationsPlatformStructure, null);
	
	var operationsParameterStructure = [
		{field: 'name',name: "<fmt:message key='dialog.operation.name' />",width: '105px', editable: true},
		{field: 'direction',name: "<fmt:message key='dialog.operation.direction' />",width: '100px',
			type: SelectboxEditor,
	        options: ["Eingabe", "Ausgabe", "Ein- und Ausgabe"],
            values: ["Eingabe", "Ausgabe", "Ein- und Ausgabe"],
	        editable: true
		},
		{field: 'description',name: "<fmt:message key='dialog.operation.description' />",width: '210px', editable: true},
		{field: 'optional',name: "<fmt:message key='dialog.operation.optional' />",width: '65px',
			type: SelectboxEditor,
	        options: ["Nein", "Ja"], // will be filled later, when syslists are loaded
	        values: [0,1],
	        editable: true,
			formatter: ListCellFormatter
		},
		{field: 'multiple',name: "<fmt:message key='dialog.operation.multiplicity' />",width: 213-scrollBarWidth+'px',
			type: SelectboxEditor,
	        options: ["Nein", "Ja"], // will be filled later, when syslists are loaded
	        values: [0,1],
	        editable: true,
			formatter: ListCellFormatter
		}
	];
    createDataGrid("operationsParameter", null, operationsParameterStructure, null);

	var operationsAddressStructure = [
		{field: 'title',name: "<fmt:message key='dialog.operation.opName' />",width: 340-scrollBarWidth+'px', editable: true}
	];
    createDataGrid("operationsAddress", null, operationsAddressStructure, null);
    createDataGrid("operationsDependencies", null, operationsAddressStructure, null);
	
	return def;
}

initOperationNameInput = function() {
	var def = new dojo.Deferred();
	var serviceTypeWidget = dijit.byId("ref3ServiceType");
	var serviceType = ""+serviceTypeWidget.getValue();

    console.debug("serviceType = " + serviceType);

	if (serviceType != "5" && serviceType != "6") {
		dijit.byId("operationsName").domNode.style.display = "none";
		requiredElements.push(["operationsNameSelect", "operationsNameLabel"]);

		var listId = getSysListIdForServiceType(serviceType);
        var def2 = UtilSyslist.readSysListData(listId);
        def2.then(function(syslistData) {
            // add empty item at the beginning
            var emptyData = [["",-1]];
            def.callback(emptyData.concat(syslistData));
        });
		_opNameIsTextInput = false;
	} else {
		dojo.style("operationsNameSelect", "display", "none");
		requiredElements.push(["operationsName", "operationsNameLabel"]);
		_opNameIsTextInput = true;
		def.callback([]);
	}
	return def;
}

getSysListIdForServiceType = function(serviceType) {
    var listId = 5110;
    if (serviceType == "1")         // CSW
        listId = 5105;
    else if (serviceType == "2")    // WMS
        listId = 5110;
    else if (serviceType == "3")    // WFS
        listId = 5120;
    else if (serviceType == "4")    // WCTS
        listId = 5130;
    return listId;
}

resetRequiredElements = function() {
	dojo.forEach(requiredElements, function(element) {
		dojo.removeClass(dojo.byId(element[1]), "important");		
	});
}

disableGUIOnWrongPermission = function() {
	if (currentUdk.writePermission == false) {
        disableInputElements();
		dijit.byId("saveButton").setDisabled(true);
//		dijit.byId("cancelButton").disable();
	} else {
        enableInputElements();
    }
}

disableInputElements = function() {
	dojo.forEach(inputElements, function(item) {
		var widget = dijit.byId(item);
		if (widget instanceof ingrid.dijit.CustomGrid)
			UtilGrid.updateOption(item, 'editable', false);
		else
			widget.setDisabled(true);
	});
}

enableInputElements = function() {
	dojo.forEach(inputElements, function(item) {
		var widget = dijit.byId(item);
		if (widget instanceof ingrid.dijit.CustomGrid) {
            UtilGrid.updateOption(item, 'editable', true);
        }
        else {
            widget.setDisabled(false);
        }
	});
}

resetInputElements = function() {
	resetRequiredElements();
	
	isNewOperation = true;
	dojo.forEach(inputElements, function(item) {
		var widget = dijit.byId(item);
		if (widget instanceof dijit.form.ValidationTextBox
		 || widget instanceof dijit.form.ComboBox
		 || widget instanceof dijit.form.SimpleTextarea) {
			widget.setValue("");
		} else if (widget instanceof ingrid.dijit.CustomGrid) {//instanceof ingrid.dijit.CustomDataGrid) {
			UtilGrid.setTableData(item, []);
		}
	});	
}

displayOperation = function(op) {
	if (_opNameIsTextInput)
		dijit.byId("operationsName").setValue(op.name);
	else {
		var selectWidget = dijit.byId("operationsNameSelect");
		selectWidget.set('value', UtilSyslist.getSyslistEntryKey(getSysListIdForServiceType(dijit.byId("ref3ServiceType").get("value")), op.name));
	}
	
	dijit.byId("operationsDescription").setValue(op.description);
	UtilStore.updateWriteStore("operationsPlatform", op.platform);
	dijit.byId("operationsCall").setValue(op.methodCall);
	UtilStore.updateWriteStore("operationsParameter", op.paramList);
	UtilStore.updateWriteStore("operationsAddress", op.addressList);
	UtilStore.updateWriteStore("operationsDependencies", op.dependencies);
}

init = function() {
    // adapt GUI and data dependent from passed data (create new op or edit op ?)
    if (caller.selectedRow) {
        // EDIT ROW !

        // we clone row, so edit process does not affect original row (cancel may be clicked)
        displayOperation(dojo.clone(caller.selectedRow));
        isNewOperation = false;
        dijit.byId("saveButton").setLabel("<fmt:message key='dialog.links.apply' />");

    } else {
        // NEW ROW !

        resetInputElements();
        isNewOperation = true;
        dijit.byId("saveButton").setLabel("<fmt:message key='general.add' />");
    }
    
	// Init table validators
    dojo.connect(UtilGrid.getTable("operationsParameter"), "onDataChanged", function(msg) {
        var self = this;
    
        var checkIfEmpty = function(value, row, col) {
            var cell = dojo.query(".slick-row[row$="+row+"] .c"+col, "operationsParameter")[0];
            if (value == undefined || dojo.trim(value+"") == "") {
                // mark cell as missing input
                self.addInvalidCell( { row: row, column: col }, true );
            }
        };
        
        this.resetInvalidCells();
        dojo.forEach(this.getData(), function(item, i) {
            checkIfEmpty(item.name, i, 0);
            checkIfEmpty(item.optional, i, 3);
            checkIfEmpty(item.multiple, i, 4);
        }, this);
    });
}

getOperation = function() {
	var operation = {};
	if (_opNameIsTextInput)
		operation.name = dijit.byId("operationsName").getValue();
	else {
        var selectWidget = dijit.byId("operationsNameSelect");
        // NOTICE: getValue() delivers id and is null if nothing selected
		operation.nameId = selectWidget.getValue();
        if (operation.nameId) {
            // and this one is the label displayed and is set as name in op.
            // NOTICE: causes NPE if nothing selected !!!!!
            operation.name = selectWidget.get("displayedValue");
        }
	}
	operation.description = dijit.byId("operationsDescription").getValue();
	operation.platform = UtilGrid.getTableData("operationsPlatform");
	
	operation.methodCall = dijit.byId("operationsCall").getValue();
	operation.paramList = UtilGrid.getTableData("operationsParameter");
	
	operation.addressList = UtilGrid.getTableData("operationsAddress");
	
	operation.dependencies = UtilGrid.getTableData("operationsDependencies");
	
	return operation;
}

isValidOperation = function(op) {
	resetRequiredElements();

	var valid = true;

    // check for validation errors
    if (dojo.query(".importantBackground", "operations").length > 0)
        valid = false;

	dojo.forEach(requiredElements, function(element) {
        var widget = dijit.byId(element[0]);
        if (!(widget instanceof ingrid.dijit.CustomGrid)) {
            var val = widget.getValue();
            if (!val || val == "") {
                dojo.addClass(dojo.byId(element[1]), "important");
                valid = false;
            }
        }
	});

    var tmpValid = true;
	if (op.platform.length == 0) {
		tmpValid = false;
	} else {
        if (dojo.some(op.platform, function(platf) {
                return (typeof(platf.title) == "undefined" || platf.title == null || dojo.trim(platf.title+"").length == 0); })) {
            tmpValid = false;
        }
    }
    if (!tmpValid) {
        dojo.addClass(dojo.byId("operationsPlatformLabel"), "important");
        valid = false;
    }
	
    tmpValid = true;
	if (op.addressList.length == 0) {
		tmpValid = false;
	} else {
        if (dojo.some(op.addressList, function(addr) {
                return (typeof(addr.title) == "undefined" || addr.title == null || dojo.trim(addr.title+"").length == 0); })) {
            tmpValid = false;
        }
    }
    if (!tmpValid) {
        dojo.addClass(dojo.byId("operationsAddressLabel"), "important");
        valid = false;
    }

	return valid;
}

// Save/Add Button onClick function
//
saveOperation = function() {
    if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/Operations")) return;

	var operation = getOperation();
	if (!isValidOperation(operation)) {
  		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='links.fillRequiredFieldsHint' />", dialog.WARNING);
		return;
	} else {
        if (isNewOperation) {
            // add NEW operation
            // No checks if the store already contains the current element.
            UtilGrid.addTableDataRow(caller.gridId, operation);

        } else {
            // EDIT existing operation
            var oldOp = caller.selectedRow;
            for (i in operation) {
                oldOp[i] = operation[i];
            }
        }

        var callerGrid = UtilGrid.getTable(caller.gridId);
        callerGrid.invalidate();
        callerGrid.notifyChangedData({});

        // save also closes dialog !
        closeThisDialog();
	}
}

// Cancel Button onClick function
//
resetDialog = function() {
	closeThisDialog();
}

closeThisDialog = function() {
	dijit.byId("pageDialog").hide();
}

</script> 
</head>

<body>

  <div id="operations" class="">
    <div id="winNavi" style="top:0; height:20px;">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-9#maintanance-of-objects-9', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	  </div>
	  <div id="operationsContent" class="content">

      <!-- LEFT HAND SIDE CONTENT START -->
      <div id="operationForm">
          <div class="inputContainer field">
            <span class="outer required"><div>
            <span class="label"><label id="operationsNameLabel" for="operationsName" onclick="javascript:dialog.showContextHelp(arguments[0], 5201)"><fmt:message key="dialog.operation.opName" />*</label></span>
    		<span class="input">
    			<input id="operationsNameSelect" required="true" style="width:100%;"/>
    			<input type="text" maxLength="255" id="operationsName" style="width:100%;" dojoType="dijit.form.ValidationTextBox"/>
    		</span>
            </div></span>
            
            <div class="inputContainer">
              <span class="outer halfWidth required"><div>
                <span class="label"><label id="operationsAddressLabel" for="operationsAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 5206)"><fmt:message key="dialog.operation.address" />*</label></span>
                <div class="tableContainer">
    			  <div id="operationsAddress" autoHeight="3" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
              <span class="outer halfWidth required"><div>
                <span class="label"><label id="operationsPlatformLabel" for="operationsPlatform" onclick="javascript:dialog.showContextHelp(arguments[0], 5203)"><fmt:message key="dialog.operation.platforms" />*</label></span>
                <div class="tableContainer">
    			  <div id="operationsPlatform" autoHeight="3" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
              <div class="fill"></div>
            </div>
            
            <div class="inputContainer">
              <span class="outer"><div>
              <span class="label"><label id="operationsParameterLabel" for="operationsParameter" onclick="javascript:dialog.showContextHelp(arguments[0], 5205)"><fmt:message key="dialog.operation.parameter" /></label></span>
              <div class="tableContainer">
    			<div id="operationsParameter" interactive="true"></div>
              </div></div>
              </span>
            </div>
    
            <span class="outer"><div>
            <span class="label"><label for="operationsCall" onclick="javascript:dialog.showContextHelp(arguments[0], 5204)"><fmt:message key="dialog.operation.call" /></label></span>
            <span class="input"><input type="text" maxLength="255" id="operationsCall" name="operationsCall" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
            </div></span>

            <div class="inputContainer">
              <span class="outer halfWidth"><div>
                <span class="label"><label for="operationsDescription" onclick="javascript:dialog.showContextHelp(arguments[0], 5202)"><fmt:message key="dialog.operation.description" /></label></span>
                <span class="input">
                    <input type="text" id="operationsDescription" rows="3" style="width:100%;" dojoType="dijit.form.SimpleTextarea" />            
                </span></div>
              </span>
              <span class="outer halfWidth"><div>
                <span class="label"><label for="operationsDependencies" onclick="javascript:dialog.showContextHelp(arguments[0], 5207)"><fmt:message key="dialog.operation.dependencies" /></label></span>
                <div class="tableContainer">
    			  <div id="operationsDependencies" autoHeight="3" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
            </div>
      	  </div>
    
          <div class="inputContainer">
            <span class="button" style="height:20px !important;">
            	<span style="float:right;"><button dojoType="dijit.form.Button" id="cancelButton" onClick="resetDialog"><fmt:message key="dialog.operation.cancel" /></button></span>
            	<span style="float:right;"><button dojoType="dijit.form.Button" type="button" id="saveButton" onClick="saveOperation();"><fmt:message key="dialog.operation.add" /></button></span>
            </span>
      	  </div>
      	  <div class="fill"></div>
      </div>
      <!-- LEFT HAND SIDE CONTENT END -->
    </div>
  </div>
</body>
</html>
