<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
            "ingrid/utils/Store",
            "ingrid/tree/LFSTree"
        ], function (NumberTextBox, registry, dom, domClass, on, all, query, warnDialog, checks, Editors, Formatters, layoutCreator, message, UtilStore, LFSTree) {

            var caller = {};
            var dialog = null;

            on(_container_, "Load", function () {
                dialog = this;
                if (this.customParams) {
                    caller = this.customParams;
                }

                createDOMElements().then(init());
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
                defs.push(layoutCreator.createComboBox("linksToDataType", null, storeProps, function () {
                    return UtilSyslist.getSyslistEntry(1320);
                }));

                // storeProps data attributes overwritten by json data
                storeProps = {
                    data: {
                        identifier: 'abbreviation',
                        label: 'label'
                    }
                };
                defs.push(layoutCreator.createFilteringSelect("linksToURLType", null, storeProps, null, "js/data/urlReferenceTypes.json"));
                storeProps = {
                    data: {
                        identifier: 'id',
                        label: 'name'
                    }
                };

                new LFSTree({treeType: 'receipt', showRoot: false}, "treeReceipt");
                new LFSTree({treeType: 'filing', showRoot: false}, "treeFiling");

                return all(defs);
            }

            function init() {
                // Init the radio buttons onclick functions
                on(registry.byId("radioLfsReceipt"), "Click", function() {
                    showReceipt();
                });
                on(registry.byId("radioLfsFiling"), "Click", function() {
                    showFiling();
                });
            }

            function showReceipt() {
                console.log("Receipt")
                dom.byId("lfsReceipt").style.display = "";
                dom.byId("lfsFiling").style.display = "none";
            }

            function showFiling() {
                console.log("Filing")
                dom.byId("lfsReceipt").style.display = "none";
                dom.byId("lfsFiling").style.display = "";

            }

            function submit() {

            }

            function cancel() {
                _container_.hide();
            }

            dialogLfsLink = {
                cancel: cancel,
                submit: submit
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
                        <span id="linksFromFieldNameLabel" class="label">
                            <label for="linksFromFieldName"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2000)">
                                <fmt:message key="dialog.lfs.link.name"/>*
                            </label>
                        </span>
                        <span class="input">
                            <input id="linksFromFieldName" style="width: 100%;" maxLength="255"
                                   required="true">
                        </span>
                    </div>
                </span>
                <span class="outer" style="width:50%;">
                    <div>
                        <span class="label">
                            <label id="linksToDataTypeLabel"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2240)">
                                <fmt:message key="dialog.links.dataType"/>
                            </label>
                        </span>
                        <span class="input">
                            <input id="linksToDataType" style="width: 100%;">
                        </span>
                    </div>
                </span>
                <span class="outer" style="width:50%;">
                    <div>
                        <span class="label">
                            <label for="linksToURLType"
                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2251)">
                                <fmt:message key="dialog.links.urlType"/>
                            </label>
                        </span>
                        <span class="input">
                            <input id="linksToURLType" style="width: 100%;">
                        </span>
                    </div>
                </span>

                <span id="inputLinkTypeRadioButtons">
                    <span class="outer required">
                        <div>
                            <div class="checkboxContainer input">
                                <span>
                                    <input type="radio" name="lfsType" data-dojo-type="dijit/form/RadioButton" id="radioLfsReceipt" class="radio" value="receipt" checked/>
                                    <label class="inActive" for="radioLfsReceipt">
                                        <fmt:message key="dialog.lfs.link.receipt" />
                                    </label>
                                </span>
                                <span>
                                    <input type="radio" name="lfsType" data-dojo-type="dijit/form/RadioButton" id="radioLfsFiling" class="radio" value="filing" />
                                    <label class="inActive" for="radioLfsFiling">
                                        <fmt:message key="dialog.lfs.link.filing" />
                                    </label>
                                </span>
                            </div>
                        </div>
                    </span>
                </span>

                <span class="outer">
                    <!-- LFS RECEIPT -->
                    <div id="lfsReceipt" class="outlined" style="overflow: auto; max-height: 300px">
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
                                   required="true">
                        </span>
                    </div>
                </span>
            </div>
        </div>
    </div>
    <!-- CONTENT END -->
    <div>
        <div class="dijitDialogPaneActionBar" style="margin: unset">
            <button data-dojo-type="dijit/form/Button" type="button"
                    data-dojo-props="onClick:function(){dialogLfsLink.submit();}" id="ok"><fmt:message
                    key="general.add"/></button>
            <button data-dojo-type="dijit/form/Button" type="button"
                    data-dojo-props="onClick:function(){dialogLfsLink.cancel();}" id="cancel"><fmt:message
                    key="general.cancel"/></button>
        </div>
    </div>
</div>

</body>

