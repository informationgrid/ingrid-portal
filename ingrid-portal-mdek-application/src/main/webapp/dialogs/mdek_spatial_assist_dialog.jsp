<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title><fmt:message key="dialog.popup.spatialreference.set.link" /></title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">
            var dialogSpatialAssist;

            require([
                "dojo/_base/array",
                "dojo/on",
                "dojo/dom",
                "dojo/dom-class",
                "dojo/topic",
                "dijit/registry",
                "dojo/store/Memory",
                "ingrid/dialog",
                "ingrid/utils/Grid",
                "ingrid/utils/Events",
                "ingrid/utils/General",
                "ingrid/grid/CustomGrid"
            ], function(array, on, dom, domClass, topic, registry, Memory, dialog, UtilGrid, UtilEvents, UtilGeneral, CustomGrid) {

                    // coordinates not mandatory, see INGRID-2089 
                    var requiredElements = [
                        ["spatialAssistRef", "spatialAssistRefLabel"]
                    ];

                    on(_container_, "Load", function() {
                        registry.byId("spatialAssistCS").setStore(registry.byId("spatialRefLocationSelect").store); // spatialReferenceStore
                        resetRequiredElements();

                        console.log("Publishing event: '/afterInitDialog/SpatialAssistant'");
                        topic.publish("/afterInitDialog/SpatialAssistant");
                    });

                    function resetRequiredElements() {
                        array.forEach(requiredElements, function(element) {
                            domClass.remove(dom.byId(element[1]), "important");
                        });
                    }

                    function validateInput() {
                        resetRequiredElements();

                        var valid = true;
                        array.forEach(requiredElements, function(element) {
                            var widget = registry.byId(element[0]);
                            if (!(widget instanceof CustomGrid)) {
                                var val = widget.getValue();
                                if (!UtilGeneral.hasValue(val)) {
                                    domClass.add(dom.byId(element[1]), "important");
                                    valid = false;
                                }
                            }
                        });

                        return valid;
                    }

                    function addLocation() {
                        if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/SpatialAssistant")) return;

                        if (!validateInput()) {
                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.fillAllFieldsHint' />", dialog.WARNING);
                            return;
                        }

                        var location = registry.byId("spatialAssistRef").getValue();
                        var fromSRS = registry.byId("spatialAssistCS").getValue();
                        var toSRS = "GEO_WGS84";
                        var coords = {
                            longitude1: registry.byId("spatialAssistLon1").getValue(),
                            latitude1: registry.byId("spatialAssistLat1").getValue(),
                            longitude2: registry.byId("spatialAssistLon2").getValue(),
                            latitude2: registry.byId("spatialAssistLat2").getValue()
                        };

                        if (UtilGeneral.hasValue(fromSRS) &&
                            (UtilGeneral.hasValue(coords.longitude1) ||
                                UtilGeneral.hasValue(coords.latitude1) ||
                                UtilGeneral.hasValue(coords.longitude2) ||
                                UtilGeneral.hasValue(coords.latitude2))) {

                            CTService.getCoordinates(fromSRS, toSRS, coords, {
                                preHook: showLoadingZone,
                                postHook: hideLoadingZone,

                                callback: function(res) {
                                    if (res.coordinate) {
                                        UtilGrid.addTableDataRow("spatialRefLocation", {
                                            name: location,
                                            longitude1: res.coordinate.longitude1,
                                            latitude1: res.coordinate.latitude1,
                                            longitude2: res.coordinate.longitude2,
                                            latitude2: res.coordinate.latitude2
                                        });
                                    } else {
                                        if (res.errorMsg) {
                                            dialog.show("<fmt:message key='general.error' />", res.errorMsg, dialog.WARNING);
                                        } else {
                                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='cts.transformError' />", dialog.WARNING);
                                        }
                                    }
                                    registry.byId("pageDialog").hide();

                                },
                                timeout: 8000,
                                errorHandler: function(err) {
                                    console.debug("Error: " + err);
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='cts.serviceError' />", dialog.WARNING, null, 300, 170);
                                    registry.byId("pageDialog").hide();
                                }
                            });
                        } else {
                            UtilGrid.addTableDataRow("spatialRefLocation", {
                                name: location,
                                longitude1: "",
                                latitude1: "",
                                longitude2: "",
                                latitude2: ""
                            });
                            registry.byId("pageDialog").hide();
                        }
                    }

                    function showLoadingZone() {
                        dom.byId('ctsLoadingZone').style.visibility = "visible";
                        registry.byId("addSpatial").set("disabled", true);
                    }

                    function hideLoadingZone() {
                        dom.byId('ctsLoadingZone').style.visibility = "hidden";
                        registry.byId("addSpatial").set("disabled", false);
                    }

                    /**
                     * PUBLIC METHODS
                     */
                    
                    dialogSpatialAssist = {
                        addLocation: addLocation
                    };

                }
            );

        </script>
    </head>
    <body>
        <div class="">
            <div id="winNavi" style="top:0; height:18px;">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-4#maintanance-of-objects-4', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <div id="spatialAssistContent" class="content">
                <!-- CONTENT START -->
                <div class="inputContainer">
                    <table cellspacing="3">
                        <tbody>
                            <tr>
                                <td class="label" nowrap="nowrap">
                                    <label id="spatialAssistRefLabel" for="spatialAssistRef" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7023)">
                                        <fmt:message key="dialog.spatialAssist.customLocation" />
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" data-dojo-type="dijit/form/ValidationTextBox" id="spatialAssistRef" style="width:100%;" />
                                </td>
                            </tr>
                            <tr>
                                <td class="label" nowrap="nowrap">
                                    <label for="spatialAssistLon1" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7024)">
                                        <fmt:message key="dialog.spatialAssist.longitude1" />
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" data-dojo-type="dijit/form/NumberTextBox" id="spatialAssistLon1" style="width:100%;" />
                                </td>
                            </tr>
                            <tr>
                                <td class="label" nowrap="nowrap">
                                    <label for="spatialAssistLat1" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7025)">
                                        <fmt:message key="dialog.spatialAssist.latitude1" />
                                    </label>
                                </td>
                                <td colspan="2" nowrap="nowrap">
                                    <input type="text" data-dojo-type="dijit/form/NumberTextBox" id="spatialAssistLat1" style="width:100%;" />
                                </td>
                            </tr>
                            <tr>
                                <td class="label" nowrap="nowrap">
                                    <label for="spatialAssistLon2" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7026)">
                                        <fmt:message key="dialog.spatialAssist.longitude2" />
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" data-dojo-type="dijit/form/NumberTextBox" id="spatialAssistLon2" style="width:100%;" />
                                </td>
                            </tr>
                            <tr>
                                <td class="label" nowrap="nowrap">
                                    <label for="spatialAssistLat2" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7027)">
                                        <fmt:message key="dialog.spatialAssist.latitude2" />
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" data-dojo-type="dijit/form/NumberTextBox" id="spatialAssistLat2" style="width:100%;" />
                                </td>
                            </tr>
                            <tr>
                                <td class="label" nowrap="nowrap">
                                    <label for="spatialAssistCS" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7028)">
                                        <fmt:message key="dialog.spatialAssist.coordSys" />
                                    </label>
                                </td>
                                <td colspan="2">
                               	<div data-dojo-type="dojo/store/Memory" jsId="spatialReferenceStore"></div>
                                <div id="spatialAssistCS" data-dojo-type="dijit/form/Select" toggle="plain" data-dojo-props="store:spatialReferenceStore, labelAttr:'label'" style="width:100%;" />
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <span style="float:right;">
                        <button data-dojo-type="dijit/form/Button" id="addSpatial" title="<fmt:message key="dialog.spatialAssist.add" />" onclick="dialogSpatialAssist.addLocation()">
                            <fmt:message key="dialog.spatialAssist.add" />
                        </button>
                    </span><span id="ctsLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden;"><img src="img/ladekreis.gif" /></span>
                    <div class="fill"></div>
                </div><!-- CONTENT END -->
            </div>
        </div>
    </body>
</html>