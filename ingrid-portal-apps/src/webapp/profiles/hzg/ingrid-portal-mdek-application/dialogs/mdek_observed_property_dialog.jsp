<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
<title>Observed Property</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">

    var dialogObservedProperty = null;

    require([
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/keys",
        "dojo/dom",
        "dojo/dom-class",
        "dojo/promise/all",
        "dojo/query",
        "dojo/topic",
        "dijit/registry",
        "dijit/form/CheckBox",
        "ingrid/dialog",
        "ingrid/hierarchy/requiredChecks",
        "ingrid/layoutCreator",
        "ingrid/utils/Store"
    ], function(array, lang, on, keys, dom, domClass, all, query, topic, registry, CheckBox, warnDialog, checks, layoutCreator, UtilStore) {

            var caller = {};
            var dialog = null;
            var isRowBeingEdited = false; // Row already exists and is being edited

            var tblObsPropId = "observedPropertiesDataGrid";

            on(_container_, "Load", function() {
                dialog = this;
                if (this.customParams) {
                    caller = this.customParams;
                }

                init();
            });

            function init() {
                if (caller && caller.selectedRow) {
                    // Set relevant flag
                    isRowBeingEdited = true;

                    // Read existing values and write them in relevant fields
                    var row = caller.selectedRow;
                    registry.byId("observedPropertyNameInDialog").set("value", row.observedPropertyName);
                    registry.byId("observedPropertyXmlDescInDialog").set("value", row.observedPropertyXmlDescription);
                }
            }

            function submit() {
                // Add data to the table
                if (!validateInputElements()) {
                    warnDialog.show('<fmt:message key="general.error" />', '<fmt:message key="links.fillRequiredFieldsHint" />', warnDialog.WARNING);
                    return;
                }

                var tblObsProp = registry.byId(tblObsPropId);
                var tblObsPropData = tblObsProp.data;

                var obsPropName = registry.byId("observedPropertyNameInDialog").get("value");
                var obsPropXmlDesc = registry.byId("observedPropertyXmlDescInDialog").get("value");

                if (isRowBeingEdited) { // Update the row being edited
                    var row = caller.selectedRow;
                    row["observedPropertyName"] = obsPropName;
                    row["observedPropertyXmlDescription"] = obsPropXmlDesc;
                } else { // Create a new row
                    tblObsPropData.push({
                        "observedPropertyName": obsPropName,
                        "observedPropertyXmlDescription": obsPropXmlDesc
                    });
                }
                // Refresh the table
                UtilStore.updateWriteStore(tblObsPropId, tblObsPropData);

                // Hide the dialog
                _container_.hide();
            }

            function cancel() {
                _container_.hide();
            }

            function validateInputElements() {
                resetRequiredInputElements();

                var visibleRequiredElements = query(".dijitDialogPaneContent .required .dijitTextBox", "pageDialog")
                    .map(function(item) {
                        return item.getAttribute("widgetid");
                    });

                var valid = true;
                dojo.forEach(visibleRequiredElements, function(id) {
                    var value = dijit.byId(id).get("value");
                    if (!value || !value.trim()) {
                        valid = false;
                        checks.setErrorLabel(id);
                    }
                });

                return valid;
            }

            // resets marked fields with invalid input
            function resetRequiredInputElements() {
                query(".important").forEach(function(item) {
                    domClass.remove(item, "important");
                });
            }


            dialogObservedProperty = {
                cancel: cancel,
                submit: submit
            };
    });
</script>
</head>

<body>
    <div id="observedPropertyContentPane" data-dojo-type="dijit/layout/ContentPane">
        <!-- CONTENT START -->
        <div class="inputContainer">
            <span class="outer required">
                <div>
                    <span class="label">
                        <label for="observedPropertyNameInDialog">
                            <fmt:message key="dialog.observedProperty.name" />*
                        </label>
                    </span>
                    <span class="input">
                        <input id="observedPropertyNameInDialog" style="width: 100%" required="true" data-dojo-type="dijit/form/ValidationTextBox" />
                    </span>
                </div>
            </span>
            <span class="outer required">
                <div style="height: 200px;">
                    <span class="label">
                        <label for="observedPropertyXmlDescInDialog">
                            <fmt:message key="dialog.observedProperty.xmlDescription" />*
                        </label>
                    </span>
                    <span class="input">
                        <input type="text" mode="textarea" id="observedPropertyXmlDescInDialog" class="textAreaFull" data-dojo-type="dijit/form/SimpleTextarea" />
                    </span>
                </div>
            </span>
            <div class="fill"></div>
        </div>
        <!-- CONTENT END -->
        <div style="margin: 0px; padding-right: 10px; padding-bottom: 10px;">
            <div class="dijitDialogPaneActionBar">
                <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogObservedProperty.submit();}" id="ok"><fmt:message key="general.ok" /></button>
                <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogObservedProperty.cancel();}" id="cancel"><fmt:message key="general.cancel" /></button>
            </div>
        </div>
    </div>

</body>
</html>
