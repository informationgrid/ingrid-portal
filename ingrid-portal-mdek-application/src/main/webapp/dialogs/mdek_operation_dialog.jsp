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
var dirtyFlag = null;

var inputElements = 
	["operationsName", "operationsNameSelect", "operationsDescription", "operationsPlatform", "operationsCall",
	 "operationsParameter", "operationsAddress", "operationsDependencies", "saveButton"];//, "cancelButton"];

var requiredElements = [/*["operationsName", "operationsNameLabel"],*/	// This element is added when the operation input is initialized
					    ["operationsPlatform", "operationsPlatformLabel"],
					    ["operationsAddress", "operationsAddressLabel"]];

var _opNameIsTextInput = null;


dirtyFlag = udkDataProxy.dirtyFlag;
dojo.connect(operationsScriptScope, "onLoad", function(){
	var def = createDOMElements();
	def.then(init);
	def.then(disableButtonsOnWrongPermission);
});

dojo.connect(operationsScriptScope, "onUnload", function(){
	//dirtyFlag ? udkDataProxy.setDirtyFlag() : udkDataProxy.resetDirtyFlag();
	UtilGrid.getTable("ref3Operation").invalidate();
});

function createDOMElements() {
    console.debug("create");
	var storeProps = {data: {identifier: '1',label: '0'}};
	var def = createFilteringSelect("operationsNameSelect", null, storeProps, initOperationNameInput);
	
	var operationsListStructure = [
		{field: 'name',name: "<fmt:message key='dialog.operation.name' />",width: '155px'},
		{field: 'description',name: "<fmt:message key='dialog.operation.description' />",width: 554-scrollBarWidth+'px'}
	];
    createDataGrid("operationsList", null, operationsListStructure, null);
	// use original store
    console.debug("use orig store");
	UtilGrid.setTableData("operationsList", UtilGrid.getTableData("ref3Operation"));
	
	var operationsPlatformStructure = [
		{field: 'title',name: "<fmt:message key='dialog.operation.opName' />",width: 347-scrollBarWidth+'px', editable: true}
	];
    console.debug("prepared platforms");
    createDataGrid("operationsPlatform", null, operationsPlatformStructure, null);
    console.debug("platforms created");
	
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
		{field: 'multiple',name: "<fmt:message key='dialog.operation.multiplicity' />",width: 229-scrollBarWidth+'px',
			type: SelectboxEditor,
	        options: ["Nein", "Ja"], // will be filled later, when syslists are loaded
	        values: [0,1],
	        editable: true,
			formatter: ListCellFormatter
		}
	];
    createDataGrid("operationsParameter", null, operationsParameterStructure, null);
    createDataGrid("operationsAddress", null, operationsPlatformStructure, null);
    createDataGrid("operationsDependencies", null, operationsPlatformStructure, null);
	
	return def;
}

initOperationNameInput = function() {
	var def = new dojo.Deferred();
	var serviceTypeWidget = dijit.byId("ref3ServiceType");
	var serviceType = ""+serviceTypeWidget.getValue();

	if (serviceType != "5" && serviceType != "6") {
		dijit.byId("operationsName").domNode.style.display = "none";
		requiredElements.push(["operationsNameSelect", "operationsNameLabel"]);
		var selectWidget = dijit.byId("operationsNameSelect");

		var listId = getSysListIdForServiceType(serviceType);

		var languageCode = UtilCatalog.getCatalogLanguage();
		CatalogService.getSysLists([listId], languageCode, {
			callback: function(res) {
				def.callback(res[listId]);
		}});
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

setDirtyFlag = function() {
	dirtyFlag = true;
}

connectStoreWithDirtyFlag = function(store)
{
	dojo.connect(store, "onAddData", this, "setDirtyFlag");
	dojo.connect(store, "onRemoveData", this, "setDirtyFlag");
	dojo.connect(store, "onUpdateField", this, "setDirtyFlag");
}

disableButtonsOnWrongPermission = function() {
	if (currentUdk.writePermission == false) {
		dijit.byId("newButton").setDisabled(true);
		dijit.byId("saveButton").setDisabled(true);
//		dijit.byId("cancelButton").disable();
	}
}

disableInputElements = function() {
	dojo.forEach(inputElements, function(item) {
		var widget = dijit.byId(item);
		console.debug(item);
		if (widget instanceof ingrid.dijit.CustomGrid)
			UtilGrid.updateOption(item, 'editable', false);
		else
			widget.setDisabled(true);
	});
}

enableInputElements = function() {
	dojo.forEach(inputElements, function(item) {
        console.debug("item: " + item);
		var widget = dijit.byId(item);
		if (widget instanceof ingrid.dijit.CustomGrid) {
            console.debug("enable: " + item);
            UtilGrid.updateOption(item, 'editable', true);
            console.debug("enabled");
        }
        else {
            console.debug("enable!: " + item);
            widget.setDisabled(false);
            console.debug("enabled!");
        }
	});
    console.debug("item: " + operationForm);
    dojo.style("operationForm", "visibility", "visible");
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
			//!!!widget.store.clearData();
			//widget.clear();
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
	disableInputElements();
	//initOperationNameInput();

	var opList = UtilGrid.getTable("operationsList");

	//dojo.connect(opList, "onSelected", function(e) {
	dojo.connect(opList, "onSelectedRowsChanged", function(row) {
		var op = UtilGrid.getSelectedData("operationsList");
		if (op.length == 1) {
			// create a new operation if an empty row was selected
			if (op[0] == null) {
				newOperation();
			}
			else {
				if (currentUdk.writePermission) {
					enableInputElements();
				}
				displayOperation(op[0]);
				isNewOperation = false;
				dijit.byId("saveButton").setLabel("<fmt:message key='general.save' />");
			}
		} else {
			disableInputElements();
			resetInputElements();
			isNewOperation = true;
			dijit.byId("saveButton").setLabel("<fmt:message key='general.add' />");
		}
	});

	// If a selected object was removed from the list, reset the input fields
    dojo.connect(opList, "onDataChanged", function(msg) {
        //if (dojo.some(opList.selection.getSelected(), function(item){ return item == obj.src; })) {
        if (msg.type == "deleted")
            resetInputElements();
    });

	//connectStoreWithDirtyFlag(dstStore);

	// Init table validators
    dojo.connect(UtilGrid.getTable("operationsParameter"), "onDataChanged", function(msg) {
        var checkIfEmpty = function(value, row, col) {
            var cell = dojo.query(".slick-row[row$="+row+"] .c"+col, "operationsParameter")[0];
            if (value == undefined || dojo.trim(value+"") == "") {
                // mark cell as missing input
                dojo.addClass(cell, "importantBackground");
            } else {
                dojo.removeClass(cell, "importantBackground");
            }
        };
        dojo.forEach(this.getData(), function(item, i) {
            checkIfEmpty(item.name, i, 0);
            checkIfEmpty(item.optional, i, 3);
            checkIfEmpty(item.multiple, i, 4);
        });
    });
	/*
	dijit.byId("operationsPlatform").setValidationFunctions([
		{target: "title", validateFunction: function(item) { return (item != null && dojo.trim(item) != ""); }}
	]);
	dijit.byId("operationsAddress").setValidationFunctions([
		{target: "title", validateFunction: function(item) { return (item != null && dojo.trim(item) != ""); }}
	]);*/
	//dijit.byId("operationsName").isValid = function() { return !this.isEmpty(); };
}

getOperation = function() {
	var operation = {};
	if (_opNameIsTextInput)
		operation.name = dijit.byId("operationsName").getValue();
	else {
		operation.name = dijit.byId("operationsNameSelect").get("displayedValue");
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
        console.debug("check: "+ element[0]);
        var widget = dijit.byId(element[0]);
        if (!(widget instanceof ingrid.dijit.CustomGrid)) {
            if (!widget.isValid()) {
                dojo.addClass(dojo.byId(element[1]), "important");
                valid = false;
            }
        }
	});

	if (op.platform.length == 0) {
		dojo.addClass(dojo.byId("operationsPlatformLabel"), "important");		
		valid = false;
	}
	
	if (op.addressList.length == 0) {
		dojo.addClass(dojo.byId("operationsAddressLabel"), "important");		
		valid = false;
	}

	return valid;
}

// New Button onClick function
//
newOperation = function() {
	UtilGrid.clearSelection("operationsList");
	enableInputElements();
	resetInputElements();
	//dijit.byId("operationsList").renderSelections();
	isNewOperation = true;
	dijit.byId("saveButton").setLabel("<fmt:message key='general.add' />");
}

// Save/Add Button onClick function
//
saveOperation = function() {
	var operation = getOperation();
	if (!isValidOperation(operation)) {
  		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='links.fillRequiredFieldsHint' />", dialog.WARNING);
		return;
	} else {
		var opList = dijit.byId("operationsList");
		if (isNewOperation) {
			//var key = UtilStore.getNewKey(opList.store);
			//operation.Id = key;
			var item = UtilGrid.addTableDataRow("operationsList", operation);
			UtilGrid.setSelection("operationsList", [UtilGrid.getTableData("operationsList").length-1]);
		} else {
			var oldOp = UtilGrid.getSelectedData("operationsList")[0];
			var row = UtilGrid.getSelectedRowIndexes("operationsList")[0];
			oldOp.name = operation.name;
			for (i in operation) {
				oldOp[i] = operation[i];
			}

			UtilGrid.updateTableDataRowAttr("operationsList", row, "name", operation.name);		
			UtilGrid.updateTableDataRowAttr("operationsList", row, "description", operation.description);
            dijit.byId("operationsList").resizeCanvas();
		}
	}
}

// Cancel Button onClick function
//
resetDialog = function() {
	UtilGrid.clearSelection("operationsList");
	disableInputElements();
	resetInputElements();
	
	isNewOperation = true;
	closeThisDialog();
}

closeThisDialog = function() {
	UtilGrid.getTable("ref3Operation").invalidate()
	dijit.byId("pageDialog").hide();
}

</script> 
</head>

<body>

  <div id="operations" class="">
    <div id="winNavi" style="top:0; height:20px;">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=maintanance-of-objects-9#maintanance-of-objects-9', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	  </div>
	  <div id="operationsContent" class="content">

      <!-- LEFT HAND SIDE CONTENT START -->
      <div class="inputContainer field grey">
        <div class="tableContainer">
        	<div id="operationsList"></div>
        </div>
      </div>

      <div class="inputContainer">
        <span class="button">
        	<span style="float:right;"><button dojoType="dijit.form.Button" type="button" id="newButton" onClick="newOperation"><fmt:message key="dialog.operation.new" /></button></span>
        </span>
  	  </div>
      <div id="operationForm" style="visibility:hidden;">
          <div class="inputContainer field">
            <span class="outer required"><div>
            <span class="label"><label id="operationsNameLabel" for="operationsName" onclick="javascript:dialog.showContextHelp(arguments[0], 5201)"><fmt:message key="dialog.operation.opName" />*</label></span>
    		<span class="input">
    			<input id="operationsNameSelect" required="true" style="width:100%;"/>
    			<input type="text" maxLength="120" id="operationsName" style="width:100%;" dojoType="dijit.form.ValidationTextBox"/>
    		</span>
            </div></span>
            
            <div class="inputContainer">
              <span class="outer halfWidth"><div>
                <span class="label"><label for="operationsDescription" onclick="javascript:dialog.showContextHelp(arguments[0], 5202)"><fmt:message key="dialog.operation.description" /></label></span>
                <span class="input">
    				<input type="text" id="operationsDescription" rows="3" style="width:100%;" dojoType="dijit.form.SimpleTextarea" />            
                </span></div>
              </span>
              <span class="outer halfWidth required"><div>
                <span class="label"><label id="operationsPlatformLabel" for="operationsPlatform" onclick="javascript:dialog.showContextHelp(arguments[0], 5203)"><fmt:message key="dialog.operation.platforms" />*</label></span>
                <div class="tableContainer">
    			  <div id="operationsPlatform" autoHeight="2" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
              <div class="fill"></div>
            </div>
            
            <span class="outer"><div>
            <span class="label"><label for="operationsCall" onclick="javascript:dialog.showContextHelp(arguments[0], 5204)"><fmt:message key="dialog.operation.call" /></label></span>
            <span class="input"><input type="text" maxLength="255" id="operationsCall" name="operationsCall" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
            </div></span>
            <div class="inputContainer">
              <span class="outer"><div>
              <span class="label"><label id="operationsParameterLabel" for="operationsParameter" onclick="javascript:dialog.showContextHelp(arguments[0], 5205)"><fmt:message key="dialog.operation.parameter" /></label></span>
              <div class="tableContainer">
    			<div id="operationsParameter" interactive="true"></div>
              </div></div>
              </span>
            </div>
    
            <div class="inputContainer">
              <span class="outer halfWidth required"><div>
                <span class="label"><label id="operationsAddressLabel" for="operationsAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 5206)"><fmt:message key="dialog.operation.address" />*</label></span>
                <div class="tableContainer">
    			  <div id="operationsAddress" autoHeight="2" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
              <span class="outer halfWidth"><div>
                <span class="label"><label for="operationsDependencies" onclick="javascript:dialog.showContextHelp(arguments[0], 5207)"><fmt:message key="dialog.operation.dependencies" /></label></span>
                <div class="tableContainer">
    			  <div id="operationsDependencies" autoHeight="2" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
            </div>
      	  </div>
    
          <div class="inputContainer">
            <span class="button" style="height:20px !important;">
            	<span style="float:right;"><button dojoType="dijit.form.Button" id="cancelButton" onClick="resetDialog"><fmt:message key="dialog.operation.cancel" /></button></span>
            	<span style="float:right;"><button dojoType="dijit.form.Button" type="button" id="saveButton" onClick="saveOperation"><fmt:message key="dialog.operation.add" /></button></span>
            </span>
      	  </div>
      	  <div class="fill"></div>
      </div>
      <!-- LEFT HAND SIDE CONTENT END -->
    </div>
  </div>
</body>
</html>
