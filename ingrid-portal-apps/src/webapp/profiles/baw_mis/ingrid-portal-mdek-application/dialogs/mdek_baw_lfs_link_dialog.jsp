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
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <title>Simulationsparameter/-Zustandsgröße</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <meta name="author" content="wemove digital solutions"/>
    <meta name="copyright" content="wemove digital solutions GmbH"/>
    <script>

        var dialogLfsLink = null;

        require([
            "dijit/form/NumberTextBox",
            "dijit/registry",
            "dojo/Deferred",
            "dojo/dom",
            "dojo/dom-class",
            "dojo/on",
            "dojo/promise/all",
            "dojo/query",
            "ingrid/dialog",
            "ingrid/hierarchy/requiredChecks",
            "ingrid/grid/CustomGridEditors",
            "ingrid/grid/CustomGridFormatters",
            "ingrid/layoutCreator",
            "ingrid/message",
            "ingrid/utils/Grid",
            "ingrid/utils/Store",
            "ingrid/tree/LFSTree"
        ], function (NumberTextBox, registry, Deferred, dom, domClass, on, all, query, dialog, checks, Editors, Formatters, layoutCreator, message, UtilGrid, UtilStore, LFSTree) {

            var caller = {};

            on(_container_, "Load", function () {
                var currentData;
                if (this.customParams) {
                    caller = this.customParams;
                    currentData = this.customParams.selectedRow;
                }

                createDOMElements()
                    .then(init(currentData !== undefined))
                    .then(setValues(currentData));
            });

            function createDOMElements() {
                var defs = [];

                // Datenformat
                storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
                defs.push(layoutCreator.createComboBox("lfsLinkDataType", null, storeProps, function () {
                    return UtilSyslist.getSyslistEntry(1320);
                }));

                // storeProps data attributes overwritten by json data
                storeProps = {
                    data: {
                        identifier: 'abbreviation',
                        label: 'label'
                    }
                };
                defs.push(layoutCreator.createFilteringSelect("lfsLinkURLType", null, storeProps, null, "js/data/urlReferenceTypes.json"));
                storeProps = {
                    data: {
                        identifier: 'id',
                        label: 'name'
                    }
                };

                var treeReceipt = new LFSTree({treeType: 'receipt', showRoot: false}, "treeReceipt");
                var treeFiling = new LFSTree({treeType: 'filing', showRoot: false}, "treeFiling");
                
                // disable multiselection in trees
                treeReceipt.dndController.singular = true;
                treeFiling.dndController.singular = true;

                return all(defs);
            }

            function init(editMode) {
                if (editMode) {
                    initForEdit();
                } else {
                    initForAdd();
                }
            }

            function initForEdit() {
                var btnAssign = registry.byId("lfs-dialog-apply");
                var radioReceipt = registry.byId("radioLfsReceipt");
                var radioFiling = registry.byId("radioLfsFiling");
                var linkName = registry.byId("lfsLinkName");

                radioFiling.set("checked", true);
                radioFiling.set("disabled", true);
                radioReceipt.set("disabled", true);

                dom.byId("previousLink").innerHTML = caller.selectedRow.link;
                btnAssign.domNode.style.display = "";
                btnAssign.set("disabled", true);

                var updateApplyButtonState = function () {
                    console.log("check apply button state");
                    var nameIsSet = linkName.value.trim().length > 0;
                    if (nameIsSet) {
                        btnAssign.set("disabled", false);
                    } else {
                        btnAssign.set("disabled", true);
                    }
                };
                on(linkName, "Change", updateApplyButtonState);
            }

            function initForAdd() {
                var btnAssign = registry.byId("lfs-dialog-add");
                var radioReceipt = registry.byId("radioLfsReceipt");
                var treeReceipt = registry.byId("treeReceipt");
                var treeFiling = registry.byId("treeFiling");
                var linkName = registry.byId("lfsLinkName");

                btnAssign.domNode.style.display = "";
                btnAssign.set("disabled", true);

                var updateApplyButtonState = function () {
                    console.log("check apply button state");
                    var nameIsSet = linkName.value.trim().length > 0;
                    var receiptChosen = radioReceipt.checked && treeReceipt.selectedItem;
                    var filingChosen = !radioReceipt.checked && treeFiling.selectedItem;
                    if (nameIsSet && (receiptChosen || filingChosen)) {
                        btnAssign.set("disabled", false);
                    } else {
                        btnAssign.set("disabled", true);
                    }
                };
                on(linkName, "Change", updateApplyButtonState);
                on(treeReceipt, "Click", updateApplyButtonState);
                on(treeFiling, "Click", updateApplyButtonState);

                showReceipt();

                // Init the radio buttons onclick functions
                on(radioReceipt, "Click", function () {
                    showReceipt();
                    updateApplyButtonState();
                });
                on(registry.byId("radioLfsFiling"), "Click", function () {
                    showFiling();
                    updateApplyButtonState();
                });

            }

            function setValues(data) {
                if (!data) {
                    return;
                }

                setTimeout(function () {
                    registry.byId("lfsLinkName").set("value", data.name);
                    registry.byId("lfsLinkExplanation").set("value", data.explanation);
                    registry.byId("lfsLinkDataType").set("value", data.fileFormat);
                    registry.byId("lfsLinkURLType").set("value", data.urlType);
                }, 100);

            }

            function showReceipt() {
                dom.byId("lfsReceipt").style.display = "";
                dom.byId("lfsFiling").style.display = "none";
            }

            function showFiling() {
                dom.byId("lfsReceipt").style.display = "none";
                dom.byId("lfsFiling").style.display = "";

            }

            function submit() {
                var radioReceipt = registry.byId("radioLfsReceipt");
                if (radioReceipt.checked) {
                    showConfirmDialog().then(function () {
                        var bwastrId = "" + getBawStrID();
                        // 0-pad the bwastrId
                        while (bwastrId.length < 4) {
                            bwastrId = "0" + bwastrId;
                        }

                        var psp = registry.byId("bawAuftragsnummer").value;
                        var file = registry.byId("treeReceipt").selectedItem.path;
                        LFSService.initMove(bwastrId, psp, file, {
                            preHook: function () {
                                domClass.remove("lfsDialogLoadingZone", "hide");
                            },
                            postHook: function () {
                                domClass.add("lfsDialogLoadingZone", "hide");
                            },
                            callback: function (response) {
                                console.log("Response initMove:", response);
                                addLinkToTable(response.target);
                                closeDialog();
                            },
                            exceptionHandler: function (msg) {
                                showError(msg);
                            }
                        });
                    });
                } else {
                    var link = registry.byId("treeFiling").selectedItem.path;
                    addLinkToTable(link);
                    closeDialog();
                }

            }

            function submitEdit() {
                var row = caller.selectedRow;
                row.name = registry.byId("lfsLinkName").value;
                row.fileFormat = registry.byId("lfsLinkDataType").value;
                row.explanation = registry.byId("lfsLinkExplanation").value;
                row.urlType = registry.byId("lfsLinkURLType").value;
                UtilGrid.updateTableDataRow("lfsLinkTable", clickedRow, row);
                closeDialog();
            }

            function getBawStrID() {
                var item = registry.byId("bwastrTable").data[0];
                return item.bwastr_name;
            }

            function showConfirmDialog() {
                var deferred = new Deferred();

                var titleText = message.get("dialog.general.info");
                var displayText = message.get("dialog.lfs.move.warn");
                dialog.show(titleText, displayText, dialog.WARNING, [{
                    caption: message.get("general.cancel"),
                    action: function () {
                    }
                }, {
                    caption: message.get("general.ok"),
                    action: function () {
                        deferred.resolve();
                    }
                }]);

                return deferred;
            }

            function showError(message) {
                console.error(message);
                var readableMessage = getReadableErrorMessage(message);
                dialog.show("Fehler", "Bei der Verarbeitung ist ein Fehler aufgetreten: " + readableMessage, dialog.WARNING);
            }

            function getReadableErrorMessage(msg) {
                if (msg.indexOf("invalid PSP number") !== -1) {
                    return message.get("error.invalid.psp.number");
                } else if (msg.indexOf("BWaStrId specified") !== -1) {
                    return message.get("error.no.bwastrid.specified");
                } else if (msg.indexOf("error while moving") !== -1) {
                    return message.get("error.moving.object") + ":<br>" + msg;
                } else {
                    return msg;
                }
            }

            function addLinkToTable(link) {
                var item = {
                    link: removePrefix(link),
                    name: registry.byId("lfsLinkName").value,
                    explanation: registry.byId("lfsLinkExplanation").value,
                    fileFormat: registry.byId("lfsLinkDataType").value,
                    urlType: registry.byId("lfsLinkURLType").value,
                };
                UtilGrid.addTableDataRow("lfsLinkTable", item);
            }

            /**
             * Remove the prefix "HH/Ablage/" and "KA/Ablage/"
             * @param link
             * @returns {string}
             */
            function removePrefix(link) {
                var startsWithHH = link.indexOf("HH/Ablage/") === 0;
                var startsWithKA = link.indexOf("KA/Ablage/") === 0;
                if (startsWithHH || startsWithKA) {
                    return link.substring(10);
                } else {
                    console.warn("Link does not start with 'HH/Ablage/' or 'KA/Ablage/'");
                    return link;
                }
            }

            function closeDialog() {
                _container_.hide();
            }

            function cancel() {
                closeDialog();
            }

            dialogLfsLink = {
                cancel: cancel,
                submit: submit,
                submitEdit: submitEdit
            };
        })
    </script>
</head>

<body>
<div data-dojo-type="dijit/layout/ContentPane">
    <div id="links">
        <div id="winNavi" style="right:10px;">
            <a href="javascript:void(0);"
               onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-7#maintanance-of-objects-7', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');"
               title="<fmt:message key="general.help" />">[?]</a>
        </div>
        <div id="linksContent" class="content">
            <!-- CONTENT START -->
            <div class="inputContainer">
                <span class="outer required">
                    <div>
                        <span id="lfsLinkNameLabel" class="label">
                            <label for="lfsLinkName"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2000)">
                                <fmt:message key="dialog.lfs.link.name"/>*
                            </label>
                        </span>
                        <span class="input">
                            <input id="lfsLinkName" style="width: 100%;" maxLength="255"
                                   data-dojo-type="dijit/form/ValidationTextBox"
                                   required="true">
                        </span>
                    </div>
                </span>
                <span class="outer" style="width:50%;">
                    <div>
                        <span class="label">
                            <label for="lfsLinkDataType"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2240)">
                                <fmt:message key="dialog.links.dataType"/>
                            </label>
                        </span>
                        <span class="input">
                            <input id="lfsLinkDataType" style="width: 100%;">
                        </span>
                    </div>
                </span>
                <span class="outer" style="width:50%;">
                    <div>
                        <span class="label">
                            <label for="lfsLinkURLType"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2251)">
                                <fmt:message key="dialog.links.urlType"/>
                            </label>
                        </span>
                        <span class="input">
                            <input id="lfsLinkURLType" style="width: 100%;">
                        </span>
                    </div>
                </span>

                <span id="inputLinkTypeRadioButtons">
                    <span class="outer required">
                        <div>
                            <div class="checkboxContainer input">
                                <span>
                                    <input type="radio" name="lfsType" data-dojo-type="dijit/form/RadioButton"
                                           id="radioLfsReceipt" class="radio" value="receipt" checked/>
                                    <label class="inActive" for="radioLfsReceipt">
                                        <fmt:message key="dialog.lfs.link.receipt"/>
                                    </label>
                                </span>
                                <span>
                                    <input type="radio" name="lfsType" data-dojo-type="dijit/form/RadioButton"
                                           id="radioLfsFiling" class="radio" value="filing"/>
                                    <label class="inActive" for="radioLfsFiling">
                                        <fmt:message key="dialog.lfs.link.filing"/>
                                    </label>
                                </span>
                            </div>
                        </div>
                    </span>
                </span>

                <span id="treeWrapper" class="outer required">
                    <div class="label">Dateiauswahl*</div>

                    <!-- PREVIOUS VALUE -->
                    <div id="previousLink"></div>

                    <!-- LFS RECEIPT -->
                    <div id="lfsReceipt" class="outlined" style="overflow: auto; max-height: 300px; display: none">
                        <span class="outer required">
                            <div>
                                <div id="treeReceipt"></div>
                            </div>
                        </span>
                        <div class="fill"></div>
                    </div>

                    <!-- LFS FILING -->
                    <div id="lfsFiling" class="outlined" style="overflow: auto; max-height: 300px; display: none">
                        <span class="outer required">
                            <div>
                                <div id="treeFiling"></div>
                            </div>
                        </span>
                        <div class="fill"></div>
                    </div>
                </span>

                <span class="outer">
                    <div>
                        <span class="label">
                            <label for="lfsLinkExplanation"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 0)">
                                <fmt:message key="dialog.lfs.link.explanation"/>
                            </label>
                        </span>
                        <span class="input">
                            <input id="lfsLinkExplanation" style="width: 100%;" maxLength="255"
                                   data-dojo-type="dijit/form/ValidationTextBox"
                                   required="false">
                        </span>
                    </div>
                </span>
            </div>
        </div>
    </div>
    <!-- CONTENT END -->
    <div>
        <div class="dijitDialogPaneActionBar" style="margin: unset">
            <span id="lfsDialogLoadingZone" style="vertical-align: middle;" class="hide">
                <img src="img/ladekreis.gif"/>
            </span>
            <button data-dojo-type="dijit/form/Button" type="button" id="lfs-dialog-apply" style="display: none"
                    data-dojo-props="onClick:function(){dialogLfsLink.submitEdit();}"><fmt:message
                    key="general.apply"/></button>
            <button data-dojo-type="dijit/form/Button" type="button" id="lfs-dialog-add" style="display: none"
                    data-dojo-props="onClick:function(){dialogLfsLink.submit();}"><fmt:message
                    key="general.add"/></button>
            <button data-dojo-type="dijit/form/Button" type="button"
                    data-dojo-props="onClick:function(){dialogLfsLink.cancel();}" id="cancel"><fmt:message
                    key="general.cancel"/></button>
        </div>
    </div>
</div>

</body>

