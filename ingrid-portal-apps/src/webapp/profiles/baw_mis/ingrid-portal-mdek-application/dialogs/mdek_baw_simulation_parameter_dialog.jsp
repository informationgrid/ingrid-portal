<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or  as soon they will be
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
<title>Simulationsparameter/-Zustandsgröße</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">

    var dialogBawSimulationParam = null;

    require([
        "dijit/form/NumberTextBox",
        "dijit/registry",
        "dojo/dom-class",
        "dojo/on",
        "dojo/query",
        "ingrid/dialog",
        "ingrid/hierarchy/requiredChecks",
        "ingrid/grid/CustomGridEditors",
        "ingrid/grid/CustomGridFormatters",
        "ingrid/layoutCreator",
        "ingrid/message",
        "ingrid/utils/Store"
    ], function (NumberTextBox, registry, domClass, on, query, warnDialog, checks, Editors, Formatters, layoutCreator, message, UtilStore) {

        // TODO duplicate code from bawUiGeneral.js
        const DISCRETE_NUMERIC = "DISCRETE_NUMERIC";
        const DISCRETE_STRING = "DISCRETE_STRING";
        const RANGE_NUMERIC = "RANGE_NUMERIC";
        const bawUiGeneralModule = require("ingrid/hierarchy/behaviours/baw_mis/bawUiGeneral");

        var caller = {};
        var dialog = null;
        var isRowBeingEdited = false;
        var simParamValueType = DISCRETE_NUMERIC;

        on(_container_, "Load", function () {
            dialog = this;
            if (this.customParams) {
                caller = this.customParams;
            }

            createDOMElements()
                .then(init());
        });

        function init() {
            if (caller && caller.selectedRow) {
                isRowBeingEdited = true;

                var row = caller.selectedRow;
                simParamValueType = row.simParamValueType;

                registry.byId("spName").set("value", row.simParamName);
                registry.byId("spType").set("value", row.simParamType);
                registry.byId("spUnits").set("value", row.simParamUnit);

                var values = row.values;
                if (simParamValueType === RANGE_NUMERIC) {
                    registry.byId("spRangeValsRadio").set("value", "on");
                    registry.byId("spValueMinInput").set("value", values[0]);
                    registry.byId("spValueMaxInput").set("value", values[1]);
                } else {
                    var tableId;
                    if (simParamValueType === DISCRETE_NUMERIC) {
                        tableId = "spDiscreteNumValsTable";
                        registry.byId("spDiscreteNumValsRadio").set("value", "on");
                    } else {
                        tableId = "spDiscreteStrValsTable";
                        registry.byId("spDiscreteStrValsRadio").set("value", "on");
                    }
                    var tableData = registry.byId(tableId).data;

                    if (values instanceof Array) {
                        values.forEach(function (v) {
                            tableData.push({paramValue: v});
                        });
                    } else {
                        tableData.push({paramValue: values});
                    }
                    UtilStore.updateWriteStore(tableId, tableData);
                }
            }
        }

        function createDOMElements() {
            var storeProps = {
                data: {
                    identifier: '1',
                    label: '0'
                }
            };

            var formatter = function (value, constraints) {
                var displayedValue = this.get('displayedValue');
                if (Math.abs(dojo.number.parse(value) - dojo.number.parse(displayedValue)) < 1.0e-6) {
                    return displayedValue;
                } else {
                    return dojo.number.format(value);
                }
            };
            var parser = function (value, constraints) {
                return dojo.number.parse(value);
            };
            registry.byId("spValueMinInput").parse = parser;
            registry.byId("spValueMaxInput").format = formatter;
            registry.byId("spValueMaxInput").parse = parser;
            registry.byId("spValueMinInput").format = formatter;

            layoutCreator.createSelectBox("spType", null, storeProps, function() {
                return UtilSyslist.getSyslistEntry(3950004);
            });

            var structure = [
                {
                    field: "paramValue",
                    name: message.get("ui.obj.baw.simulation.parameter.dialog.table.column.value"),
                    editable: true,
                    isMandatory: true,
                    type: Editors.DecimalCellEditor,
                    widgetClass: NumberTextBox,
                    formatter: Formatters.LocalizedNumberFormatter,
                    style: "width: auto"
                }
            ];

            layoutCreator.createDataGrid("spDiscreteNumValsTable", null, structure, null);

            structure = [
                {
                    field: "paramValue",
                    name: message.get("ui.obj.baw.simulation.parameter.dialog.table.column.value"),
                    editable: true,
                    isMandatory: true,
                    style: "width: auto"
                }
            ];

            var deferred = layoutCreator.createDataGrid("spDiscreteStrValsTable", null, structure, null);

            on(registry.byId("spDiscreteNumValsRadio"), "Change", function (newVal) {
                if (newVal) {
                    simParamValueType = DISCRETE_NUMERIC;

                    domClass.add("uiElementSpDiscreteVals", "required");
                    domClass.remove("uiElementSpDiscreteVals", "hidden");
                } else {
                    domClass.remove("uiElementSpDiscreteVals", "required");
                    domClass.add("uiElementSpDiscreteVals", "hidden");
                }
            });
            on(registry.byId("spDiscreteStrValsRadio"), "Change", function (newVal) {
                if (newVal) {
                    simParamValueType = DISCRETE_STRING;

                    domClass.add("uiElementSpDiscreteStrVals", "required");
                    domClass.remove("uiElementSpDiscreteStrVals", "hidden");
                    domClass.add("uiElementSpUnits", "hidden");

                    registry.byId("spDiscreteStrValsTable").reinitLastColumn(true);
                } else {
                    domClass.remove("uiElementSpDiscreteStrVals", "required");
                    domClass.add("uiElementSpDiscreteStrVals", "hidden");
                    domClass.remove("uiElementSpUnits", "hidden");
                }
            });
            on(registry.byId("spRangeValsRadio"), "Change", function (newVal) {
                if (newVal) {
                    simParamValueType = RANGE_NUMERIC;

                    domClass.remove("uiElementSpRangeVals", "hidden");
                    domClass.add("spValueMin", "required");
                    domClass.add("spValueMax", "required");
                } else {
                    domClass.add("uiElementSpRangeVals", "hidden");
                    domClass.remove("spValueMin", "required");
                    domClass.remove("spValueMax", "required");
                }
            });

            return deferred;
        }

        function validateInputElements() {
            resetRequiredInputElements();
            var visibleRequiredElements = query(".dijitDialogPaneContent .required .dijitTextBox", "pageDialog")
                .map(function(item) {
                    return item.getAttribute("widgetid");
                });

            // Check the Textboxen first
            var valid = true;
            dojo.forEach(visibleRequiredElements, function(id) {
                var value = dijit.byId(id).get("value");
                if ((value !== 0 && !value) // Value isn't numeric 0 or falsy e.g. null and undefined
                        || !("" + value).trim()) { // numeric values need to be converted to strings, else trim() fails
                    valid = false;
                    checks.setErrorLabel(id);
                }
            });

            // Also check the table for values
            if (simParamValueType === DISCRETE_NUMERIC) {
                var valuesTable = registry.byId("spDiscreteNumValsTable");
                if (valuesTable.data.length === 0) {
                    valid = false;
                    checks.setErrorLabel("spDiscreteNumValsTable");
                }
            } else if (simParamValueType === DISCRETE_STRING) {
                var valuesTable = registry.byId("spDiscreteStrValsTable");
                if (valuesTable.data.length === 0) {
                    valid = false;
                    checks.setErrorLabel("spDiscreteStrValsTable");
                }
            }
            return valid;
        }

        // resets marked fields with invalid input
        function resetRequiredInputElements() {
            query(".important").forEach(function(item) {
                domClass.remove(item, "important");
            });
        }

        function submit() {
            if (!validateInputElements()) {
                warnDialog.show('<fmt:message key="general.error" />', '<fmt:message key="links.fillRequiredFieldsHint" />', warnDialog.WARNING);
                return;
            }

            const tableId = "simParamTable";
            const tableData = registry.byId(tableId).data;

            const simParamName = registry.byId("spName").get("value");
            const simParamType = registry.byId("spType").get("value");
            const simParamUnits = registry.byId("spUnits").get("value");
            var values = [];
            if (simParamValueType === DISCRETE_NUMERIC || simParamValueType === DISCRETE_STRING) {
                const valuesTableId = simParamValueType === DISCRETE_NUMERIC ? "spDiscreteNumValsTable" : "spDiscreteStrValsTable";
                values = registry.byId(valuesTableId).data.map(function (row) {
                    return row.paramValue;
                });
            } else {
                const min = registry.byId("spValueMinInput").get("value");
                const max = registry.byId("spValueMaxInput").get("value");
                values.push(min);
                values.push(max);
            }
            var valuesString = bawUiGeneralModule.simulationParameterValueArrayToString(values, simParamValueType);

            if (isRowBeingEdited) {
                const row = caller.selectedRow;
                row["simParamName"] = simParamName;
                row["simParamType"] = simParamType;
                row["simParamUnit"] = simParamUnits;
                row["simParamValue"] = valuesString;
                row["simParamValueType"] = simParamValueType;
                row["values"] = values;
            } else {
                tableData.push({
                    simParamName: simParamName,
                    simParamType: simParamType,
                    simParamUnit: simParamUnits,
                    simParamValue: valuesString,
                    simParamValueType: simParamValueType,
                    values: values
                });
            }
            UtilStore.updateWriteStore(tableId, tableData);
            _container_.hide();
        }

        function cancel() {
            _container_.hide();
        }

        dialogBawSimulationParam = {
            cancel: cancel,
            submit: submit
        };
    })
</script>
</head>

<body>
<div id="bawDgsContentPane" data-dojo-type="dijit/layout/ContentPane">
    <!-- CONTENT START -->
    <div class="inputContainer">
        <span class="outer required">
            <div>
                <span class="label">
                    <label for="spName">
                        <fmt:message key="ui.obj.baw.simulation.parameter.name" />*
                    </label>
                </span>
                <span class="input">
                    <input id="spName" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/ValidationTextBox">
                </span>
            </div>
        </span>
        <span class="outer required">
            <div>
                <span class="label">
                    <label for="spType">
                        <fmt:message key="ui.obj.baw.simulation.parameter.role" />*
                    </label>
                </span>
                <span class="input">
                    <input autoComplete="false" style="width:100%;" listId="3950004" id="spType" />
                </span>
            </div>
        </span>
        <span class="outer required">
            <div class="checkboxContainer input">
                <span>
                    <input type="radio" id="spDiscreteNumValsRadio" name="simParamValuesDiscreteOrRange" class="radio" checked="checked" data-dojo-type="dijit/form/RadioButton" />
                    <label class="inactive" for="spDiscreteNumValsRadio">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.discrete.numbers" />
                    </label>
                </span>
                <span>
                    <input type="radio" id="spDiscreteStrValsRadio" name="simParamValuesDiscreteOrRange" class="radio" data-dojo-type="dijit/form/RadioButton" />
                    <label class="inactive" for="spDiscreteStrValsRadio">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.discrete.strings" />
                    </label>
                </span>
                <span>
                    <input type="radio" id="spRangeValsRadio" name="simParamValuesDiscreteOrRange" class="radio" data-dojo-type="dijit/form/RadioButton" />
                    <label class="inactive" for="spRangeValsRadio">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.range" />
                    </label>
                </span>
            </div>
        </span>
        <!-- Discrete numerical values table start -->
        <span id="uiElementSpDiscreteVals" class="outer required">
            <div>
                <span class="label">
                    <label for="spDiscreteNumValsTable">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.discrete" />*
                    </label>
                </span>
                <span class="input">
                    <div id="spDiscreteNumValsTable" interactive="true" autoHeight="3"></div>
                </span>
            </div>
        </span>
        <!-- Discrete numerical values table end -->
        <!-- Discrete string values table start -->
        <span id="uiElementSpDiscreteStrVals" class="outer hidden">
            <div>
                <span class="label">
                    <label for="spDiscreteStrValsTable">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.discrete" />*
                    </label>
                </span>
                <span class="input">
                    <div id="spDiscreteStrValsTable" interactive="true" autoHeight="3"></div>
                </span>
            </div>
        </span>
        <!-- Discrete string values table end -->
        <!-- Value range start -->
        <span id="uiElementSpRangeVals" class="hidden">
            <span id="spValueMin" class="outer">
                <div>
                    <span class="label">
                        <label for="spValueMinInput">
                            <fmt:message key="ui.obj.baw.simulation.parameter.value.min" />*
                        </label>
                    </span>
                    <span class="input">
                        <input id="spValueMinInput" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/NumberTextBox">
                    </span>
                </div>
            </span>
            <span id="spValueMax" class="outer">
                <div>
                    <span class="label">
                        <label for="spValueMaxInput">
                            <fmt:message key="ui.obj.baw.simulation.parameter.value.max" />*
                        </label>
                    </span>
                    <span class="input">
                        <input id="spValueMaxInput" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/NumberTextBox">
                    </span>
                </div>
            </span>
        </span>
        <!-- Value range end -->
        <span id="uiElementSpUnits" class="outer">
            <div>
                <span class="label">
                    <label for="spUnits">
                        <fmt:message key="ui.obj.baw.simulation.parameter.units" />
                    </label>
                </span>
                <span class="input">
                    <input id="spUnits" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/ValidationTextBox">
                </span>
            </div>
        </span>
        <div class="fill">
        </div>
    </div>
    <!-- CONTENT END -->
    <div>
        <div class="dijitDialogPaneActionBar" style="margin: unset">
            <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogBawSimulationParam.submit();}" id="ok"><fmt:message key="general.ok" /></button>
            <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogBawSimulationParam.cancel();}" id="cancel"><fmt:message key="general.cancel" /></button>
        </div>
    </div>
</div>

</body>

