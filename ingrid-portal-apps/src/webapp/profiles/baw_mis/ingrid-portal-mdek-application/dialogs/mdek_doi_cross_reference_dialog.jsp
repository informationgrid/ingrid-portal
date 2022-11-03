<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
<title><fmt:message key="dialog.doi.xref.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">

    var dialogDoiCrossReference = null;

    require([
        "dijit/form/DateTextBox",
        "dijit/registry",
        "dojo/dom-class",
        "dojo/on",
        "dojo/query",
        "ingrid/dialog",
        "ingrid/hierarchy/requiredChecks",
        "ingrid/layoutCreator",
        "ingrid/message",
        "ingrid/utils/Store"
    ], function (DateTextBox, registry, domClass, on, query, warnDialog, checks, layoutCreator, message, UtilStore) {

        var caller = {};
        var dialog = null;
        var isRowBeingEdited = false;

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
                registry.byId("xrefTitle").set("value", row.doiCrossReferenceTitle);
                registry.byId("xrefDate").set("value", row.doiCrossReferenceDate);
                registry.byId("xrefIdentifier").set("value", row.doiCrossReferenceIdentifier);
                registry.byId("xrefPublisher").set("value", row.doiCrossReferencePublisher);

                const authorsTableId = "xrefAuthorsTable";
                var authorTableData = registry.byId(authorsTableId).data;
                row.doiCrossReferenceAuthor.split(";")
                    .forEach(function (row) {
                        const tokens = row.split(",");
                        authorTableData.push({
                            xrefAuthorFamilyName: tokens[0].trim(),
                            xrefAuthorGivenName: tokens[1].trim()
                        });
                    });
                UtilStore.updateWriteStore(authorsTableId, authorTableData);
            }
        }

        function createDOMElements() {
            var storeProps = {
                data: {
                    identifier: '1',
                    label: '0'
                }
            };

            new DateTextBox({
                style: "width: 100%;"
            }, "xrefDate");

            var structure = [
                {
                    field: "xrefAuthorFamilyName",
                    name: message.get("ui.obj.doi.xref.author.table.column.family.name") + "*",
                    editable: true,
                    isMandatory: true,
                    width: "200px"
                },
                {
                    field: "xrefAuthorGivenName",
                    name: message.get("ui.obj.doi.xref.author.table.column.given.name") + "*",
                    editable: true,
                    isMandatory: true,
                    width: "auto"
                }
            ];

            var deferred = layoutCreator.createDataGrid("xrefAuthorsTable", null, structure, null);
            registry.byId("xrefAuthorsTable").reinitLastColumn(true);
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
            const authorsTableId = "xrefAuthorsTable";
            const authorsTable = registry.byId(authorsTableId);
            if (authorsTable.data.length === 0) {
                valid = false;
                checks.setErrorLabel(authorsTableId);
            }
            authorsTable.data.forEach(function (row) {
                if (!row.xrefAuthorFamilyName || !row.xrefAuthorGivenName) {
                    valid = false;
                    checks.setErrorLabel(authorsTableId);
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

        function submit() {
            if (!validateInputElements()) {
                warnDialog.show('<fmt:message key="general.error" />', '<fmt:message key="links.fillRequiredFieldsHint" />', warnDialog.WARNING);
                return;
            }

            const tableId = "doiCrossReferenceTable";
            const tableData = registry.byId(tableId).data;

            const xrefTitle = registry.byId("xrefTitle").get("value");
            const xrefDate = registry.byId("xrefDate").get("value");
            const xrefIdentifier = registry.byId("xrefIdentifier").get("value");
            const xrefPublisher = registry.byId("xrefPublisher").get("value");

            const xrefAuthorData = registry.byId("xrefAuthorsTable").data;
            const xrefAuthors = xrefAuthorData.map(function (row) {
                return row.xrefAuthorFamilyName.trim() + ", " + row.xrefAuthorGivenName.trim();
            }).join("; ");


            if (isRowBeingEdited) {
                const row = caller.selectedRow;
                row["doiCrossReferenceTitle"] = xrefTitle;
                row["doiCrossReferenceDate"] = xrefDate;
                row["doiCrossReferenceAuthor"] = xrefAuthors;
                row["doiCrossReferenceIdentifier"] = xrefIdentifier;
                row["doiCrossReferencePublisher"] = xrefPublisher;
            } else {
                tableData.push({
                    doiCrossReferenceTitle: xrefTitle,
                    doiCrossReferenceDate: xrefDate,
                    doiCrossReferenceAuthor: xrefAuthors,
                    doiCrossReferenceIdentifier: xrefIdentifier,
                    doiCrossReferencePublisher: xrefPublisher
                });
            }
            UtilStore.updateWriteStore(tableId, tableData);
            _container_.hide();
        }

        function cancel() {
            _container_.hide();
        }

        dialogDoiCrossReference = {
            cancel: cancel,
            submit: submit
        };
    })
</script>
</head>

<body>
<div id="doiCrossReferenceContentPane" data-dojo-type="dijit/layout/ContentPane">
    <!-- CONTENT START -->
    <div class="inputContainer">
        <span class="outer required">
            <div>
                <span class="label">
                    <label for="xrefTitle">
                        <fmt:message key="ui.obj.doi.xref.title" />*
                    </label>
                </span>
                <span class="input">
                    <input id="xrefTitle" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/ValidationTextBox">
                </span>
            </div>
        </span>
        <span class="outer required halfWidth">
            <div>
                <span class="label">
                    <label for="xrefDate">
                        <fmt:message key="ui.obj.doi.xref.date" />*
                    </label>
                </span>
                <span class="input">
                    <div id="xrefDate"></div>
                </span>
            </div>
        </span>
        <span class="outer required halfWidth">
            <div>
                <span class="label">
                    <label for="xrefIdentifier">
                        <fmt:message key="ui.obj.doi.xref.identifier" />*
                    </label>
                </span>
                <span class="input">
                    <input id="xrefIdentifier" style="width: 100%;" maxLength="255" required="true" data-dojo-type="dijit/form/ValidationTextBox">
                </span>
            </div>
        </span>
        <span class="outer">
            <div>
                <span class="label">
                    <label for="xrefPublisher">
                        <fmt:message key="ui.obj.doi.xref.publisher" />
                    </label>
                </span>
                <span class="input">
                    <input id="xrefPublisher" style="width: 100%;" maxLength="255" data-dojo-type="dijit/form/ValidationTextBox">
                </span>
            </div>
        </span>
        <!-- Authors table start -->
        <span id="uiElementXrefAuthors" class="outer required">
            <div>
                <span class="label">
                    <label for="xrefAuthorsTable">
                        <fmt:message key="ui.obj.doi.xref.author" />*
                    </label>
                </span>
                <span class="input">
                    <div id="xrefAuthorsTable" interactive="true" autoHeight="3"></div>
                </span>
            </div>
        </span>
        <!-- Authors table end -->
        <div class="fill">
        </div>
    </div>
    <!-- CONTENT END -->
    <div>
        <div class="dijitDialogPaneActionBar" style="margin: unset">
            <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogDoiCrossReference.submit();}" id="ok"><fmt:message key="general.ok" /></button>
            <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogDoiCrossReference.cancel();}" id="cancel"><fmt:message key="general.cancel" /></button>
        </div>
    </div>
</div>

</body>

