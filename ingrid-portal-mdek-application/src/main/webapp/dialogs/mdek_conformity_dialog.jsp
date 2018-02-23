<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
    <title><fmt:message key="dialog.popup.???.link" /></title><!-- TODO -->
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="author" content="wemove digital solutions" />
    <meta name="copyright" content="wemove digital solutions GmbH" />
    <script type="text/javascript">
        var dialogConformity = null;
        require([
            "dijit/form/DateTextBox",
            "dijit/registry",
            "dojo/on",
            "dojo/query",
            "ingrid/dialog",
            "ingrid/hierarchy/requiredChecks",
            "ingrid/layoutCreator",
            "ingrid/utils/Store",
            "ingrid/utils/Syslist"
        ], function(DateTextBox, registry, on, query, dialog, checks, layoutCreator, UtilStore, UtilSyslist) {
                var inspireDateTextBox = null;
                var freeDateTextBox = null;
                createDOMElements();

                function createDOMElements() {

                    var storeProps = {
                        data: {
                            identifier: '1',
                            label: '0'
                        }
                    };
                    layoutCreator.createFilteringSelect("conformitySpecificationInspireFieldName", null, storeProps, function() {
                        return UtilSyslist.getSyslistEntry(6005);
                    });
                    on(registry.byId("conformitySpecificationInspireFieldName"), "Change", handleInspireConformitySpecificationChange);

                    layoutCreator.createFilteringSelect("conformityLevelInspireFieldName", null, storeProps, function() {
                        return UtilSyslist.getSyslistEntry(6000);
                    });
                    inspireDateTextBox = new DateTextBox({
                        disabled: true,
                        style: "width: 100%;"
                    }, "conformityDateInspireFieldName");




                    layoutCreator.createFilteringSelect("conformitySpecificationFreeFieldName", null, storeProps, function() {
                        return UtilSyslist.getSyslistEntry(6006);
                    });
                    on(registry.byId("conformitySpecificationFreeFieldName"), "Change", handleFreeConformitySpecificationChange);

                    layoutCreator.createFilteringSelect("conformityLevelFreeFieldName", null, storeProps, function() {
                        return UtilSyslist.getSyslistEntry(6000);
                    });
                    freeDateTextBox = new DateTextBox({
                        style: "width: 100%;"
                    }, "conformityDateFreeFieldName");
                }

                function handleInspireConformitySpecificationChange(conformityId) {
                    var typeName = UtilSyslist.getSyslistEntryName(6005, conformityId);
                    var typeData = UtilSyslist.getSyslistEntryData(6005, typeName);
                    inspireDateTextBox.set('value', typeData);
                }
                function handleFreeConformitySpecificationChange(conformityId) {
                    var typeName = UtilSyslist.getSyslistEntryName(6006, conformityId);
                    var typeData = UtilSyslist.getSyslistEntryData(6006, typeName);
                    freeDateTextBox.set('value', typeData);
                }

                function submit() {
                    // collect result from correct tab
                    var isInspire = registry.byId("dialogConformityTab").selectedChildWidget === registry.byId("dialogConformityInspireTab");
                    var publicationDate, level, specification;

                    if (isInspire) {
                        specification = registry.byId("conformitySpecificationInspireFieldName").get("item")[0];
                        level = registry.byId("conformityLevelInspireFieldName").get("value");
                        publicationDate = registry.byId("conformityDateInspireFieldName").get("value");
                    } else {
                        specification = registry.byId("conformitySpecificationFreeFieldName").get("item")[0];
                        level = registry.byId("conformityLevelFreeFieldName").get("value");
                        publicationDate = registry.byId("conformityDateFreeFieldName").get("value");
                    }

                    // add data to conformity table
                    if (!validateInputElements()) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='links.fillRequiredFieldsHint' />", dialog.WARNING);
                    } else {
                        var row = {
                            isInspire: isInspire,
                            specification: specification,
                            level: level,
                            publicationDate: publicationDate
                        };
                        var conformityTable = registry.byId("extraInfoConformityTable");
                        var conformityData = conformityTable.data;
                        conformityData.push(row);
                        UtilStore.updateWriteStore("extraInfoConformityTable", conformityData);

                        _container_.hide();
                    }
                }

               function cancel() {
                    _container_.hide();
                }

                // marks fields if wrong input
                function validateInputElements() {
                    var valid = true;

                    var visibleRequiredElements = query(".dijitTabContainerTopChildWrapper.dijitVisible .required .dijitSelect, .dijitTabContainerTopChildWrapper.dijitVisible .required .dijitTextBox", "pageDialog").map(function(item) {return item.getAttribute("widgetid");});

                    dojo.forEach(visibleRequiredElements, function(id){
                        var value = dijit.byId(id).get("value");
                        if (!value || value === "") {
                            valid = false;
                            checks.setErrorLabel(id);
                        }
                    });

                    return valid;
                }

              /**
               * PUBLIC METHODS
               */
                dialogConformity = {
                    cancel: cancel,
                    submit: submit
                }
        });
    </script>
</head>
<body>
    <div data-dojo-type="dijit/layout/ContentPane">
    	<div>
            	<a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-2#maintanance-of-objects-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
        </div>
       <div class="content">
            <!-- LEFT HAND SIDE CONTENT START -->
            <div id="dialogConformityTab" data-dojo-type="dijit/layout/TabContainer" style="height:510px; overflow:visible;">
                <!-- TAB 1 START -->
                <div id="dialogConformityInspireTab" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder grey" title="<fmt:message key="dialog.conformity.tab.inspire" />">
                    <div class="inputContainer field grey">
                        <span class="outer required">
                            <div>
                                <span class="label">
                                    <label class="inActive" for="conformitySpecificationInspireFieldName">
                                        <fmt:message key="dialog.conformity.inspire.specification" />*
                                    </label>
                                </span>
                                <span class="input">
                                    <input id="conformitySpecificationInspireFieldName" style="width: 100%;" maxLength="255" autoComplete="false" required="true">
                                </span>
                            </div>
                        </span>
                        <span class="outer required" style="width: 75%">
                            <div>
                                <span class="label">
                                    <label class="inActive" for="conformityLevelInspireFieldName">
                                        <fmt:message key="dialog.conformity.inspire.level" />*
                                    </label>
                                </span>
                                <span class="input">
                                    <input id="conformityLevelInspireFieldName" style="width: 100%;" maxLength="255" required="true">
                                </span>
                            </div>
                        </span>
                        <span class="outer required" style="width: 25%">
                            <div>
                                <span class="label">
                                    <label class="inActive" for="conformityDateInspireFieldName">
                                        <fmt:message key="dialog.conformity.inspire.publicationDate" />*
                                    </label>
                                </span>
                                <span class="input">
                                    <input id="conformityDateInspireFieldName" style="width: 100%;" maxLength="255" required="true">
                                </span>
                            </div>
                        </span>
                    </div>
                </div><!-- TAB 1 END -->
                <!-- TAB 2 START -->
				<div data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" style="width: 100%;" title="<fmt:message key="dialog.conformity.tab.freetext" />">
                    <div data-dojo-type="dijit/layout/ContentPane" class="inputContainer grey" style="height: 450px; padding:0px !important;" >
                        <span class="outer required">
                            <div>
                                <span class="label">
                                    <label class="inActive" for="conformitySpecificationFreeFieldName">
                                        <fmt:message key="dialog.conformity.free.specification" />*
                                    </label>
                                </span>
                                <span class="input">
                                    <input id="conformitySpecificationFreeFieldName" style="width: 100%;" maxLength="255" required="true">
                                </span>
                            </div>
                        </span>
                        <span class="outer required" style="width: 75%">
                            <div>
                                <span class="label">
                                    <label class="inActive" for="conformityLevelFreeFieldName">
                                        <fmt:message key="dialog.conformity.free.level" />*
                                    </label>
                                </span>
                                <span class="input">
                                    <input id="conformityLevelFreeFieldName" style="width: 100%;" maxLength="255" required="true">
                                </span>
                            </div>
                        </span>
                        <span class="outer required" style="width: 25%">
                            <div>
                                <span class="label">
                                    <label class="inActive" for="conformityDateFreeFieldName">
                                        <fmt:message key="dialog.conformity.free.publicationDate" />*
                                    </label>
                                </span>
                                <span class="input">
                                    <input id="conformityDateFreeFieldName" style="width: 100%;" maxLength="255" required="true">
                                </span>
                            </div>
                        </span>
                    </div>
                </div><!-- TAB 2 END -->
            </div><!-- LEFT HAND SIDE CONTENT END -->
            <div class="dijitDialogPaneActionBar">
                <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogConformity.submit();}" id="ok"><fmt:message key="general.ok" /></button>
                <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogConformity.cancel();}" id="cancel"><fmt:message key="general.cancel" /></button>
            </div>
        </div>
    </div>
</body>
</html>
