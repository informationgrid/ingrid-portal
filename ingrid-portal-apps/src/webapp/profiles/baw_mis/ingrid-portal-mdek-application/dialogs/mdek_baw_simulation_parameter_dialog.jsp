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
        "dijit/registry",
        "dojo/dom-class",
        "dojo/on",
        "dojo/query",
        "ingrid/dialog",
        "ingrid/hierarchy/requiredChecks",
        "ingrid/layoutCreator",
        "ingrid/message",
        "ingrid/utils/Store"
    ], function (registry, domClass, on, query, warnDialog, checks, layoutCreator, message, UtilStore) {

        var caller = {};
        var dialog = null;
        var isRowBeingEdited = false;
        var hasDiscreteValues = true;

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
                hasDiscreteValues = row.hasDiscreteValues;

                registry.byId("simParamNameInDialog").set("value", row.simParamName);
                registry.byId("simParamTypeInDialog").set("value", row.simParamType);
                registry.byId("simParamUnitsInDialog").set("value", row.simParamUnit);

                var values = row.values;
                if (hasDiscreteValues) {
                    var tableId = "simParamDiscreteValuesTable";
                    var tableData = registry.byId(tableId).data;

                    if (values instanceof Array) {
                        values.forEach(function (v) {
                            tableData.push({paramValue: v});
                        });
                    } else {
                        tableData.push({paramValue: values});
                    }
                    UtilStore.updateWriteStore(tableId, tableData);
                } else {
                    registry.byId("simParamValueRangeRadio").set("value", "on");
                    registry.byId("simParamValueMinInput").set("value", values[0]);
                    registry.byId("simParamValueMaxInput").set("value", values[1]);
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

            layoutCreator.createSelectBox("simParamTypeInDialog", null, storeProps, function() {
                return UtilSyslist.getSyslistEntry(3950004);
            });

            var structure = [
                {
                    field: "paramValue",
                    name: message.get("ui.obj.baw.simulation.parameter.dialog.table.column.value"),
                    editable: true,
                    isMandatory: true,
                    style: "width: auto"
                }
            ];

            var deferred = layoutCreator.createDataGrid("simParamDiscreteValuesTable", null, structure, null);

            on(registry.byId("simParamDiscreteValuesRadio"), "Change", function (newVal) {
                if (newVal) {
                    hasDiscreteValues = true;

                    domClass.add("uiElementSimParamDiscreteValues", "required");
                    domClass.remove("uiElementSimParamDiscreteValues", "hide");

                } else {
                    hasDiscreteValues = false;

                    domClass.remove("uiElementSimParamDiscreteValues", "required");
                    domClass.add("uiElementSimParamDiscreteValues", "hide");
                }
            });

            on(registry.byId("simParamValueRangeRadio"), "Change", function (newVal) {
                if (newVal) {
                    domClass.remove("uiElementSimParamValueRange", "hide");
                    domClass.add("simParamValueMin", "required");
                    domClass.add("simParamValueMax", "required");
                } else {
                    domClass.add("uiElementSimParamValueRange", "hide");
                    domClass.remove("simParamValueMin", "required");
                    domClass.remove("simParamValueMax", "required");
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
            if (hasDiscreteValues) {
                var valuesTable = registry.byId("simParamDiscreteValuesTable");
                if (valuesTable.data.length === 0) {
                    valid = false;
                    checks.setErrorLabel("simParamDiscreteValuesTable");
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

            var tableId = "simParamTable";
            var tableData = registry.byId(tableId).data;

            var simParamName = registry.byId("simParamNameInDialog").get("value");
            var simParamType = registry.byId("simParamTypeInDialog").get("value");
            var simParamUnit = registry.byId("simParamUnitsInDialog").get("value");
            var values = [];
            if (hasDiscreteValues) {
                values = registry.byId("simParamDiscreteValuesTable").data.map(function (row) {
                    return row.paramValue;
                });
            } else {
                var min = registry.byId("simParamValueMinInput").get("value");
                var max = registry.byId("simParamValueMaxInput").get("value");
                values.push(min);
                values.push(max);
            }
            var valuesString = require("ingrid/hierarchy/behaviours/baw_mis/bawUiGeneral").simulationParameterValueArrayToString(values, hasDiscreteValues);

            if (isRowBeingEdited) {
                var row = caller.selectedRow;
                row["simParamName"] = simParamName;
                row["simParamType"] = simParamType;
                row["simParamUnit"] = simParamUnit;
                row["simParamValue"] = valuesString;
                row["hasDiscreteValues"] = hasDiscreteValues;
                row["values"] = values;
            } else {
                tableData.push({
                    simParamName: simParamName,
                    simParamType: simParamType,
                    simParamUnit: simParamUnit,
                    simParamValue: valuesString,
                    hasDiscreteValues: hasDiscreteValues,
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
                    <label for="simParamNameInDialog">
                        <fmt:message key="ui.obj.baw.simulation.parameter.name" />*
                    </label>
                </span>
                <span class="input">
                    <input id="simParamNameInDialog" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/ValidationTextBox">
                </span>
            </div>
        </span>
        <span class="outer required">
            <div>
                <span class="label">
                    <label for="simParamTypeInDialog">
                        <fmt:message key="ui.obj.baw.simulation.parameter.role" />*
                    </label>
                </span>
                <span class="input">
                    <input autoComplete="false" style="width:100%;" listId="3950004" id="simParamTypeInDialog" />
                </span>
            </div>
        </span>
        <span class="outer required">
            <div class="checkboxContainer input">
                <span>
                    <input type="radio" id="simParamDiscreteValuesRadio" name="simParamValuesDiscreteOrRange" class="radio" checked="checked" data-dojo-type="dijit/form/RadioButton" />
                    <label class="inactive" for="simParamDiscreteValuesRadio">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.discrete" />
                    </label>
                </span>
                <span>
                    <input type="radio" id="simParamValueRangeRadio" name="simParamValuesDiscreteOrRange" class="radio" data-dojo-type="dijit/form/RadioButton" />
                    <label class="inactive" for="simParamValueRangeRadio">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.range" />
                    </label>
                </span>
            </div>
        </span>
        <!-- Discrete values table start -->
        <span id="uiElementSimParamDiscreteValues" class="outer required">
            <div>
                <span class="label">
                    <label for="simParamDiscreteValuesTable">
                        <fmt:message key="ui.obj.baw.simulation.parameter.value.discrete" />*
                    </label>
                </span>
                <span class="input">
                    <div id="simParamDiscreteValuesTable" interactive="true" autoHeight="3"></div>
                </span>
            </div>
        </span>
        <!-- Discrete values table end -->
        <!-- Value range start -->
        <span id="uiElementSimParamValueRange" class="hide">
            <span id="simParamValueMin" class="outer">
                <div>
                    <span class="label">
                        <label for="simParamValueMin">
                            <fmt:message key="ui.obj.baw.simulation.parameter.value.min" />*
                        </label>
                    </span>
                    <span class="input">
                        <input id="simParamValueMinInput" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/NumberTextBox">
                    </span>
                </div>
            </span>
            <span id="simParamValueMax" class="outer">
                <div>
                    <span class="label">
                        <label for="simParamValueMax">
                            <fmt:message key="ui.obj.baw.simulation.parameter.value.max" />*
                        </label>
                    </span>
                    <span class="input">
                        <input id="simParamValueMaxInput" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/NumberTextBox">
                    </span>
                </div>
            </span>
        </span>
        <!-- Value range end -->
        <span class="outer">
            <div>
                <span class="label">
                    <label for="simParamUnits">
                        <fmt:message key="ui.obj.baw.simulation.parameter.units" />
                    </label>
                </span>
                <span class="input">
                    <input id="simParamUnitsInDialog" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/ValidationTextBox">
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

