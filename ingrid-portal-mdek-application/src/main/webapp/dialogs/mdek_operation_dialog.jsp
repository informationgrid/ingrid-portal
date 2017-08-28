<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2017 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
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
    var pageOperationsDlg = null;

    require([
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/aspect",
        "dojo/query",
        "dojo/Deferred",
        "dojo/dom",
        "dojo/topic",
        "dojo/dom-style",
        "dojo/dom-class",
        "dijit/registry",
        "dijit/form/ValidationTextBox",
        "dijit/form/ComboBox",
        "dijit/form/SimpleTextarea",
        "ingrid/grid/CustomGrid",
        "ingrid/layoutCreator",
        "ingrid/dialog",
        "ingrid/grid/CustomGridEditors",
        "ingrid/grid/CustomGridFormatters",
        "ingrid/utils/Syslist",
        "ingrid/utils/Store",
        "ingrid/utils/Grid",
        "ingrid/utils/Events"
    ], function(array, lang, on, aspect, query, Deferred, dom, topic, style, domClass,
            registry, ValidationTextBox, ComboBox, SimpleTextarea, CustomGrid,
            layoutCreator, dialog, GridEditors, GridFormatters,
            UtilSyslist, UtilStore, UtilGrid, UtilEvents) {

            var isNewOperation = false;

            var inputElements =
                ["operationsName", "operationsNameSelect", "operationsDescription", "operationsPlatform", "operationsCall",
                "operationsParameter", "operationsAddress", "operationsDependencies", "saveButton"
            ]; //, "cancelButton"];

            // NOTICE: "operationsName" OR "operationsNameSelect" dependent from type of service is added below !
            var requiredElements = [
                ["operationsPlatform", "operationsPlatformLabel"],
                ["operationsAddress", "operationsAddressLabel"]
            ];

            var _opNameIsTextInput = null;

            // data passed by caller of this dialog
            var caller = {};

            var customParams = null;
            
            var dialog = null;

            var global = this;

            on(_container_, "Load", function() {
                dialog = this;
                customParams = this.customParams;

                // take over passed data
                if (customParams) {
                    caller = customParams;
                }

                createDOMElements()
                .then(init)
                .then(disableGUIOnWrongPermission);

                console.log("Publishing event: '/afterInitDialog/Operations'");
                topic.publish("/afterInitDialog/Operations");
            });

            function createDOMElements() {
                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                var def = layoutCreator.createSelectBox("operationsNameSelect", null, storeProps, initOperationNameInput);

                var operationsPlatformStructure = [{
                    field: 'title',
                    name: "<fmt:message key='dialog.operation.opName' />",
                    width: "340px",
                    type: GridEditors.SelectboxEditor,
                    options: [],
                    values: [],
                    editable: true,
                    listId: 5180,
                    formatter: dojo.partial(GridFormatters.SyslistCellFormatter, 5180)
                }];
                layoutCreator.createDataGrid("operationsPlatform", null, operationsPlatformStructure, null);

                var operationsParameterStructure = [{
                    field: 'name',
                    name: "<fmt:message key='dialog.operation.name' />",
                    width: '105px',
                    editable: true
                }, {
                    field: 'direction',
                    name: "<fmt:message key='dialog.operation.direction' />",
                    width: '100px',
                    type: GridEditors.SelectboxEditor,
                    options: ["Eingabe", "Ausgabe", "Ein- und Ausgabe"],
                    values: ["Eingabe", "Ausgabe", "Ein- und Ausgabe"],
                    editable: true
                }, {
                    field: 'description',
                    name: "<fmt:message key='dialog.operation.description' />",
                    width: '210px',
                    editable: true
                }, {
                    field: 'optional',
                    name: "<fmt:message key='dialog.operation.optional' />",
                    width: '65px',
                    type: GridEditors.SelectboxEditor,
                    options: ["Nein", "Ja"], // will be filled later, when syslists are loaded
                    values: [0, 1],
                    editable: true,
                    formatter: GridFormatters.ListCellFormatter
                }, {
                    field: 'multiple',
                    name: "<fmt:message key='dialog.operation.multiplicity' />",
                    width: "213px",
                    type: GridEditors.SelectboxEditor,
                    options: ["Nein", "Ja"], // will be filled later, when syslists are loaded
                    values: [0, 1],
                    editable: true,
                    formatter: GridFormatters.ListCellFormatter
                }];
                layoutCreator.createDataGrid("operationsParameter", null, operationsParameterStructure, null);

                var operationsAddressStructure = [{
                    field: 'title',
                    name: "<fmt:message key='dialog.operation.opName' />",
                    width: "340px",
                    editable: true
                }];
                layoutCreator.createDataGrid("operationsAddress", null, operationsAddressStructure, null);
                layoutCreator.createDataGrid("operationsDependencies", null, operationsAddressStructure, null);

                return def;
            }

            function initOperationNameInput() {
                var def = new Deferred();
                var serviceTypeWidget = registry.byId("ref3ServiceType");
                var serviceType = "" + serviceTypeWidget.get("value");

                console.debug("serviceType = " + serviceType);
                if (serviceType != "5" && serviceType != "6") {
                    registry.byId("operationsName").domNode.style.display = "none";
                    requiredElements.push(["operationsNameSelect", "operationsNameLabel"]);

                    var listId = getSysListIdForServiceType(serviceType);
                    var def2 = UtilSyslist.readSysListData(listId);
                    def2.then(function(syslistData) {
                        // add empty item at the beginning
                        var emptyData = [
                            ["", -1]
                        ];
                        def.resolve(emptyData.concat(syslistData));
                    });
                    _opNameIsTextInput = false;
                } else {
                    requiredElements.push(["operationsName", "operationsNameLabel"]);
                    _opNameIsTextInput = true;
                    def.resolve([]);
                }
                return def;
            }

            function getSysListIdForServiceType(serviceType) {
                var listId = 5110;
                if (serviceType == "1") // CSW
                    listId = 5105;
                else if (serviceType == "2") // WMS
                    listId = 5110;
                else if (serviceType == "3") // WFS
                    listId = 5120;
                else if (serviceType == "4") // WCTS
                    listId = 5130;
                return listId;
            }

            function resetRequiredElements() {
                array.forEach(requiredElements, function(element) {
                    domClass.remove(dom.byId(element[1]), "important");
                });
            }

            function disableGUIOnWrongPermission() {
                if (global.currentUdk.writePermission === false) {
                    disableInputElements();
                    registry.byId("saveButton").set("disabled", true);
                    //      registry.byId("cancelButton").disable();
                } else {
                    enableInputElements();
                }
            }

            function disableInputElements() {
                array.forEach(inputElements, function(item) {
                    var widget = registry.byId(item);
                    if (widget instanceof CustomGrid)
                        UtilGrid.updateOption(item, 'editable', false);
                    else
                        widget.set("disabled", true);
                });
            }

            function enableInputElements() {
                array.forEach(inputElements, function(item) {
                    var widget = registry.byId(item);
                    if (widget instanceof CustomGrid) {
                        UtilGrid.updateOption(item, 'editable', true);
                    } else {
                        widget.set("disabled", false);
                    }
                });
            }

            function resetInputElements() {
                resetRequiredElements();

                isNewOperation = true;
                array.forEach(inputElements, function(item) {
                    var widget = registry.byId(item);
                    if (widget instanceof ValidationTextBox || widget instanceof ComboBox || widget instanceof SimpleTextarea) {
                        widget.set("value", "");
                    } else if (widget instanceof CustomGrid) {
                        UtilGrid.setTableData(item, []);
                    }
                });
            }

            function displayOperation(op) {
                if (_opNameIsTextInput)
                    registry.byId("operationsName").set("value", op.name);
                else {
                    var selectWidget = registry.byId("operationsNameSelect");
                    selectWidget.set('value', UtilSyslist.getSyslistEntryKey(getSysListIdForServiceType(registry.byId("ref3ServiceType").get("value")), op.name));
                }

                registry.byId("operationsDescription").set("value", op.description);
                UtilStore.updateWriteStore("operationsPlatform", op.platform);
                registry.byId("operationsCall").set("value", op.methodCall);
                UtilStore.updateWriteStore("operationsParameter", op.paramList);
                UtilStore.updateWriteStore("operationsAddress", op.addressList);
                UtilStore.updateWriteStore("operationsDependencies", op.dependencies);
            }

            function init() {
                if (_opNameIsTextInput) {
                    style.set("operationsNameSelect", "display", "none");
                } else {
                    // make sure select box drop down is closed when closing the dialog, otherwise
                    // it cannot be destroyed correctly
                    aspect.before(dialog, "hide", function() {
                        registry.byId("operationsNameSelect").closeDropDown(true);
                    });
                }
                
                // adapt GUI and data dependent from passed data (create new op or edit op ?)
                if (caller.selectedRow) {
                    // EDIT ROW !

                    // we clone row, so edit process does not affect original row (cancel may be clicked)
                    displayOperation(lang.clone(caller.selectedRow));
                    isNewOperation = false;
                    registry.byId("saveButton").set("label", "<fmt:message key='dialog.links.apply' />");

                } else {
                    // NEW ROW !

                    resetInputElements();
                    isNewOperation = true;
                    registry.byId("saveButton").set("label", "<fmt:message key='general.add' />");
                    
                    // set Webservice as default entry for inspire dataset (REDMINE-87)
                    if (registry.byId("isInspireRelevant").checked) {
                        UtilGrid.addTableDataRow( "operationsPlatform", {
                            title: "6" // WebServices
                        } );
                    }
                }

                // Init table validators
                aspect.after(UtilGrid.getTable("operationsParameter"), "onDataChanged", function() {
                    var self = this;
    
                    var checkIfEmpty = function(value, row, col) {
                        var cell = query(".slick-row[row$=" + row + "] .c" + col, "operationsParameter")[0];
                        if (value === undefined || lang.trim(value + "") === "") {
                            // mark cell as missing input
                            domClass.add(cell, "importantBackground");
							self.addInvalidCell( { row: row, column: col }, true );
                        } else {
                            domClass.remove(cell, "importantBackground");
                        }
                    };
					this.resetInvalidCells();
                    array.forEach(this.getData(), function(item, i) {
                        checkIfEmpty(item.name, i, 0);
                        checkIfEmpty(item.optional, i, 3);
                        checkIfEmpty(item.multiple, i, 4);
                    }, this);
                });
            }

            function getOperation() {
                var operation = {};
                if (_opNameIsTextInput)
                    operation.name = registry.byId("operationsName").get("value");
                else {
                    var selectWidget = registry.byId("operationsNameSelect");
                    // NOTICE: getValue() delivers id and is null if nothing selected
                    operation.nameId = selectWidget.get("value");
                    if (operation.nameId) {
                        // and this one is the label displayed and is set as name in op.
                        // NOTICE: causes NPE if nothing selected !!!!!
                        operation.name = selectWidget.get("displayedValue");
                    }
                }
                operation.description = registry.byId("operationsDescription").get("value");
                operation.platform = UtilGrid.getTableData("operationsPlatform");

                operation.methodCall = registry.byId("operationsCall").get("value");
                operation.paramList = UtilGrid.getTableData("operationsParameter");

                operation.addressList = UtilGrid.getTableData("operationsAddress");

                operation.dependencies = UtilGrid.getTableData("operationsDependencies");

                return operation;
            }

            function isValidOperation(op) {
                resetRequiredElements();

                var valid = true;

                // check for validation errors
                if (query(".importantBackground", "operations").length > 0)
                    valid = false;

                array.forEach(requiredElements, function(element) {
                    var widget = registry.byId(element[0]);
                    if (widget instanceof CustomGrid) {
                        if (!widget.validateNonEmptyRows()) valid = false;
                        
                    } else {
                        var val = widget.get("value");
                        if (!val || val === "") {
                            domClass.add(dom.byId(element[1]), "important");
                            valid = false;
                        }
                    }
                });

                var tmpValid = true;
                if (op.platform.length === 0) {
                    tmpValid = false;
                } else {
                    if (array.some(op.platform, function(platf) {
                        return (typeof(platf.title) == "undefined" || platf.title === null || lang.trim(platf.title + "").length === 0);
                    })) {
                        tmpValid = false;
                    }
                }
                if (!tmpValid) {
                    domClass.add(dom.byId("operationsPlatformLabel"), "important");
                    valid = false;
                }

                tmpValid = true;
                if (op.addressList.length === 0) {
                    tmpValid = false;
                } else {
                    if (array.some(op.addressList, function(addr) {
                        return (typeof(addr.title) == "undefined" || addr.title === null || lang.trim(addr.title + "").length === 0);
                    })) {
                        tmpValid = false;
                    }
                }
                if (!tmpValid) {
                    domClass.add(dom.byId("operationsAddressLabel"), "important");
                    valid = false;
                }

                return valid;
            }

            // Save/Add Button onClick function
            //
            function saveOperation() {
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
                        var i;
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
            function resetDialog() {
                closeThisDialog();
            }

            function closeThisDialog() {
                registry.byId("pageDialog").hide();
            }

            /**
             * PUBLIC METHODS
             */

            pageOperationsDlg = {
                resetDialog: resetDialog,
                saveOperation: saveOperation
            };

        });

    </script> 
</head>

<body>

  <div id="operations" class="">
    <div id="winNavi" style="top:0; height:20px;">
			<a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-9#maintanance-of-objects-9', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
	  </div>
	  <div id="operationsContent" class="content">

      <!-- LEFT HAND SIDE CONTENT START -->
      <div id="operationForm">
          <div class="inputContainer field">
            <span class="outer required"><div>
            <span class="label"><label id="operationsNameLabel" for="operationsName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5201)"><fmt:message key="dialog.operation.opName" />*</label></span>
    		<span class="input">
    			<input id="operationsNameSelect" required="true" style="width:100%;"/>
    			<input type="text" maxLength="255" id="operationsName" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox"/>
    		</span>
            </div></span>
            
            <div class="inputContainer">
              <span class="outer halfWidth required"><div>
                <span class="label"><label id="operationsAddressLabel" for="operationsAddress" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5206)"><fmt:message key="dialog.operation.address" />*</label></span>
                <div class="tableContainer">
    			  <div id="operationsAddress" autoHeight="3" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
              <span class="outer halfWidth required"><div>
                <span class="label"><label id="operationsPlatformLabel" for="operationsPlatform" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5203)"><fmt:message key="dialog.operation.platforms" />*</label></span>
                <div class="tableContainer">
    			  <div id="operationsPlatform" autoHeight="3" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
              <div class="fill"></div>
            </div>
            
            <div class="inputContainer">
              <span class="outer"><div>
              <span class="label"><label id="operationsParameterLabel" for="operationsParameter" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5205)"><fmt:message key="dialog.operation.parameter" /></label></span>
              <div class="tableContainer">
    			<div id="operationsParameter" interactive="true"></div>
              </div></div>
              </span>
            </div>
    
            <span class="outer"><div>
            <span class="label"><label for="operationsCall" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5204)"><fmt:message key="dialog.operation.call" /></label></span>
            <span class="input"><input type="text" maxLength="255" id="operationsCall" name="operationsCall" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" /></span>
            </div></span>

            <div class="inputContainer">
              <span class="outer halfWidth"><div>
                <span class="label"><label for="operationsDescription" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5202)"><fmt:message key="dialog.operation.description" /></label></span>
                <span class="input">
                    <input type="text" id="operationsDescription" rows="4" style="width:100%;" data-dojo-type="dijit/form/SimpleTextarea" />            
                </span></div>
              </span>
              <span class="outer halfWidth"><div>
                <span class="label"><label for="operationsDependencies" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5207)"><fmt:message key="dialog.operation.dependencies" /></label></span>
                <div class="tableContainer">
    			  <div id="operationsDependencies" autoHeight="3" interactive="true" class="hideTableHeader"></div>
                </div></div>
              </span>
            </div>
      	  </div>
    
          <div class="inputContainer">
            <span class="button" style="height:20px !important;">
            	<span style="float:right;"><button data-dojo-type="dijit/form/Button" id="cancelButton" onclick="pageOperationsDlg.resetDialog()"><fmt:message key="dialog.operation.cancel" /></button></span>
            	<span style="float:right;"><button data-dojo-type="dijit/form/Button" type="button" id="saveButton" onclick="pageOperationsDlg.saveOperation()"><fmt:message key="dialog.operation.add" /></button></span>
            </span>
      	  </div>
      	  <div class="fill"></div>
      </div>
      <!-- LEFT HAND SIDE CONTENT END -->
    </div>
  </div>
</body>
</html>
