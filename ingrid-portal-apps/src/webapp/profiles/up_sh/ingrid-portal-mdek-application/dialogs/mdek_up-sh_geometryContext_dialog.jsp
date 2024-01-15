<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2024 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <title>Geometry Context</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="author" content="wemove digital solutions"/>
    <meta name="copyright" content="wemove digital solutions GmbH"/>
    <script>

        var pageLinksDialog = _container_;
        require(["dojo/_base/lang",
            "dojo/_base/array",
            "dojo/on",
            "dojo/Deferred",
            "dojo/promise/all",
            "dojo/dom",
            "dojo/topic",
            "dojo/dom-class",
            "dojo/dom-style",
            "dojo/query",
            "dijit/registry",
            "dojo/data/ItemFileWriteStore",
            "ingrid/layoutCreator",
            "ingrid/message",
            "ingrid/utils/String",
            "ingrid/utils/Store",
            "ingrid/utils/UDK",
            "ingrid/utils/List",
            "ingrid/utils/Grid",
            "ingrid/utils/UI",
            "ingrid/hierarchy/requiredChecks"
        ], function (lang, array, on, Deferred, all, dom, topic, domClass, style, query, registry, ItemFileWriteStore, layoutCreator, message, UtilString, UtilStore, UtilUdk, UtilList, UtilGrid, UtilUI, checks) {

            // data passed by caller of this dialog
            var caller = {};
            var prevValue = null;

            on(_container_, "Load", function () {
                init();
                initNominal();
                on(contextType, "change", handleTypeChange);
            });

            function init() {

                if (_container_.customParams) {
                    caller = _container_.customParams;
                }
                prevValue = caller.selectedRow || {};

                updateFields(prevValue);

                var attributesStructure = [
                    {field: 'key', name: message.get("upsh.attributes.code"), width: '200px', editable: true},
                    {field: 'value', name: message.get("upsh.attributes.definition"), width: 'auto', editable: true}
                ];
                layoutCreator.createDataGrid("nokisGeometryContextAttributes", null, attributesStructure, function() {
                    var def = new Deferred();
                    try {
                        def.resolve(prevValue && prevValue.attributes ? JSON.parse(prevValue.attributes) : []);
                    } catch (e) {
                        console.error("Error parsing attributes", prevValue);
                        def.resolve([]);
                    }
                    return def;
                });

                on(registry.byId("ok"), "click", applyDialog);
                on(registry.byId("cancel"), "click", closeDialog);
            }

            function updateFields(value) {

                if (value.featureType !== undefined) {
                    contextType.set("value", value.featureType);
                }
                if (value.min !== undefined) {
                    registry.byId("contextMin").set("value", dojo.number.format(value.min));
                }
                if (value.max !== undefined) {
                    registry.byId("contextMax").set("value", dojo.number.format(value.max));
                }
                if (value.unit !== undefined) {
                    registry.byId("contextUnit").set("value", value.unit);
                }
            }

            function handleTypeChange(type) {

                query(".errorInfoBlock", "dlgGeometryContent")
                    .forEach(function(item) { item.remove ? item.remove() : item.parentNode.removeChild(item); });

                switch (type) {
                    case "ordinal":
                        return initOrdinal();
                    case "nominal":
                        return initNominal();
                    case "scalar":
                        return initScalar();
                    case "other":
                        return initOther();
                }
            }

            function initOrdinal() {
                domClass.remove('minMaxWrapper', 'hide');
                domClass.add('unitWrapper', 'hide');
            }

            function initNominal() {
                domClass.add('minMaxWrapper', 'hide');
                domClass.add('unitWrapper', 'hide');
            }

            function initScalar() {
                domClass.remove('minMaxWrapper', 'hide');
                domClass.remove('unitWrapper', 'hide');
            }

            function initOther() {
                domClass.add('minMaxWrapper', 'hide');
                domClass.add('unitWrapper', 'hide');
            }

            function validate(data) {
                if (data.featureType === "scalar" && !data.unit) {
                    checks.setErrorLabel("unitWrapper", "Dieses Feld muss ausgefüllt sein");
                    return false;
                }

                return true;
            }

            function applyDialog() {
                var featureType = contextType.value;
                var withMinMax = featureType === "ordinal" || featureType === "scalar";
                var withUnit = featureType === "scalar";

                var row = {
                    featureType: featureType,
                    min: withMinMax ? registry.byId("contextMin").get("value") : null,
                    max: withMinMax ? registry.byId("contextMax").get("value") : null,
                    unit: withUnit ? registry.byId("contextUnit").get("value") : null,
                    attributes: JSON.stringify(registry.byId("nokisGeometryContextAttributes").data)
                };

                if (!validate(row)) {
                    return;
                }

                var data = Object.assign(prevValue, row);

                // Add or Update
                if (caller.rowIndex === undefined) {
                    UtilGrid.addTableDataRow(caller.gridId, data);
                } else {
                    UtilGrid.updateTableDataRow(caller.gridId, caller.rowIndex, data);
                }

                closeDialog();
            }

            function closeDialog() {
                registry.byId("pageDialog").hide();
            }
        });
    </script>
</head>
<body>
<div id="dlgGeometryContent" class="content">


    <span class="outer required">
        <div>
            <span class="label">
                <label>Typ*</label>
            </span>
            <span class="input">
                <select data-dojo-id="contextType" name="type" style="width: 25%" data-dojo-type="dijit/form/Select">
                    <option value="nominal" selected="selected">nominal</option>
                    <option value="ordinal">ordinal</option>
                    <option value="scalar">skalar</option>
                    <option value="other">sonstiges</option>
                </select>
            </span>
        </div>
    </span>

    <hr>

    <div id="minMaxWrapper">
        <span class="outer halfWidth">
            <div>
                <span class="label">
                    <label for="contextMin">Min</label>
                </span>
                <span class="input">
                    <input id="contextMin" type="text" style="width: 100%"
                           data-dojo-type="dijit/form/NumberTextBox"
                           name="min"/>
                </span>
            </div>
        </span>

        <span class="outer halfWidth">
            <div>
                <span class="label">
                    <label for="contextMax">Max</label>
                </span>
                <span class="input">
                    <input id="contextMax" type="text" style="width: 100%"
                           data-dojo-type="dijit/form/NumberTextBox"
                           name="max"/>
                </span>
            </div>
        </span>
    </div>

    <span id="unitWrapper" class="outer required">
        <div>
            <span class="label">
                <label for="contextUnit">Einheit*</label>
            </span>
            <span class="input">
                <input id="contextUnit" type="text" style="width: 100%"
                       data-dojo-type="dijit/form/ValidationTextBox"
                       name="unit"/>
            </span>
        </div>
    </span>

    <span id="generalAddressTableLabel" class="label left">
                <label for="nokisGeometryContextAttributes">
                    <span id="generalAddressTableLabelText">Attribute</span>
                </label>
            </span>
    <div id="generalAddressTable" class="input tableContainer clear">
        <div id="nokisGeometryContextAttributes" autoHeight="4" interactive="true">
        </div>
    </div>

    <span class="button">
        <div class="dijitDialogPaneActionBar">
            <button data-dojo-type="dijit/form/Button" type="button" id="cancel">
                <fmt:message key="general.cancel" />
            </button>
            <button data-dojo-type="dijit/form/Button" type="button" id="ok">
                <fmt:message key="general.apply" />
            </button>
        </div>
    </span>
</div>

</body>
