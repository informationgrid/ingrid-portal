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
    <title>Raumbezug festlegen</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="author" content="wemove digital solutions" />
    <meta name="copyright" content="wemove digital solutions GmbH" />
    <style type="text/css">
        .floatLeft {
            float: left;
        }
        
        input.radioButton {
            margin-right: 3px;
        }
    </style>
    <script type="text/javascript">
        var dialogCatalogSpatial = null;

        require([
            "dojo/_base/lang",
            "dojo/on",
            "dojo/dom",
            "dojo/keys",
            "dojo/dom-style",
            "dojo/dom-class",
            "dijit/registry"
        ], function(lang, on, dom, keys, style, domClass, registry) {

            var radioButtonArray = [];
            on(_container_, "Load", init);
            
            function init(){
                // Enter key on the ValdiationTextbox has to start a search:
                on(dom.byId("locationTextBox"), "keypress", function(event){
                    if (event.keyCode == keys.ENTER) {
                        findLocationTopics();
                    }
                });
            }
            
            function resetResultDiv(){
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    while (resultDiv.lastChild)
                        resultDiv.removeChild(resultDiv.lastChild);
                }
                dom.byId("resultLabel").style.visibility = "hidden";
            }
            
            function setResultList(topicList){
                if (topicList !== null && topicList.length > 0) {
                    resetResultDiv();
                    radioButtonArray = [];
                    var checkboxDiv = dom.byId("resultList");
                    for (var i in topicList) {
                        var label = topicList[i].name;
                        if (label === null)
                            continue;
                        if (topicList[i].type !== null) {
                            label += ", " + topicList[i].type;
                        }
                        var radioButton = document.createElement("input");
                        radioButton.setAttribute("type", "radio");
                        radioButton.setAttribute("name", "spatialReference");
                        radioButton.setAttribute("id", topicList[i].topicId);
                        radioButton.topic = topicList[i];
                        domClass.add(radioButton, "radioButton");
                        radioButtonArray.push(radioButton);
                        
                        var divElement = document.createElement("div");
                        /*
                         // Don't display a link, only text.
                         var linkElement = document.createElement("a");
                         linkElement.setAttribute("href", "javascript:void(0);");
                         linkElement.topicId = topicList[i].topicId;
                         linkElement.onclick = function() {
                         findAssociatedLocations(this.topicId);
                         }
                         linkElement.innerHTML = label;
                         */
                        var linkElement = document.createTextNode(label);
                        
                        checkboxDiv.appendChild(divElement);
                        divElement.appendChild(radioButton);
                        divElement.appendChild(linkElement);
                        
                    }
                    dom.byId("resultLabel").style.visibility = "visible";
                }
                else {
                    showNoResults();
                }
            }
            
            // This function queries the SNSService for similar location topics
            /*function findAssociatedLocations(topicId) {
                resetResultDiv();
                showLoading();
                
                SNSService.getLocationTopicsById(topicId, userLocale, {
                    preHook: function() {
                        showLoadingZone();
                        disableUiElements();
                    },
                    postHook: function() {
                        hideLoadingZone();
                        enableUiElements();
                    },
                    callback: setResultList,
                    errorHandler: showError,
                    timeout: 0
                });
            }*/
            
            // 'Search Button' onClick function
            // This function queries the SNSService for location topics
            function findLocationTopics(){
                var queryTerm = registry.byId("locationTextBox").getValue();
                
                // If input is blank, do nothing
                queryTerm = lang.trim(queryTerm);
                if (queryTerm.length === 0)
                    return;
                
                resetResultDiv();
                showLoading();
                
                SNSService.getLocationTopics(queryTerm, "exact", "/location/admin", userLocale, {
                    preHook: function() {
                        showLoadingZone();
                        disableUiElements();
                    },
                    postHook: function() {
                        hideLoadingZone();
                        enableUiElements();
                    },
                    callback: setResultList,
                    errorHandler: function(msg, err) {
                        displayErrorMessage(err);
                    },
                    timeout: 0
                });
            }
            
            // 'Add Button' onClick function
            // This function returns the selected topic via the attached resultHandler
            function addLocationTopics() {
            
                for (var i in radioButtonArray) {
                    if (radioButtonArray[i].checked) {
                        // Convert SNSLocationTopic to LocationBean
                        var location = {
                            topicId: radioButtonArray[i].topic.topicId,
                            name: radioButtonArray[i].topic.name,
                            type: "G",
                            nativeKey: radioButtonArray[i].topic.nativeKey,
                            topicType: radioButtonArray[i].topic.type,
                            topicTypeId: radioButtonArray[i].topic.typeId
                        };
                        
                        if (radioButtonArray[i].topic.boundingBox && radioButtonArray[i].topic.boundingBox.length == 4) {
                            location.longitude1 = radioButtonArray[i].topic.boundingBox[0];
                            location.latitude1 = radioButtonArray[i].topic.boundingBox[1];
                            location.longitude2 = radioButtonArray[i].topic.boundingBox[2];
                            location.latitude2 = radioButtonArray[i].topic.boundingBox[3];
                        }
                        console.debug("call callback");
                        //          _container_.customParams.resultHandler.resolve(radioButtonArray[i].topic);
                        registry.byId("pageDialog").customParams.resultHandler.resolve(location);
                    }
                }
                
                console.debug("close window");
                registry.byId("pageDialog").hide();
            }
            
            
            function disableUiElements(){
                registry.byId("addLocationTopicsButton").set("disabled", true);
                registry.byId("findLocationTopicsButton").set("disabled", true);
            }
            
            function enableUiElements(){
                registry.byId("addLocationTopicsButton").set("disabled", false);
                registry.byId("findLocationTopicsButton").set("disabled", false);
            }
            
            function showLoadingZone(){
                style.set("spatialLoadingZone", "visibility", "visible");
            }
            
            function hideLoadingZone(){
                style.set("spatialLoadingZone", "visibility", "hidden");
            }
            
            function showNoResults(){
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "<fmt:message key='ui.obj.spatial.noResultsHint' />";
                }
                dom.byId("resultLabel").style.visibility = "hidden";
            }
            
            function showLoading(){
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "";
                }
            }
            
            /*function showError(){
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "<fmt:message key='ui.obj.spatial.connectionError' />";
                }
            }*/

            /*
             *  PUBLIC METHODS
             */
            dialogCatalogSpatial = {
                findLocationTopics: findLocationTopics,
                addLocationTopics: addLocationTopics
            };

        });
        
    </script>
</head>
<body>
    <!--<div data-dojo-type="dijit/layout/ContentPane">-->
        <div id="catalogueSpatialRef">
            <div id="winNavi" style="position: absolute; right: 10px;">
                <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-3#maintanance-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <div id="spatialRefContent" class="content">
                <!-- CONTENT START -->
                <div class="inputContainer">
                    
                    <div class="input">
                        <span class="outer" style="width:60%;">
                            <div>
                                <span class="label">
                                    <label for="locationTextBox" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8008)">
                                        <fmt:message key="dialog.admin.catalog.selectLocation.setLocation" />
                                    </label>
                                </span>
                                <div class="input">
                                    <input type="text" id="locationTextBox" size="20" name="locationTextBox" style="width:292px;" data-dojo-type="dijit/form/ValidationTextBox" />
                                </div>
                                <p class="comment"><fmt:message key="dialog.spatialNavigator.searchHint" /></p>
                            </div>
                        </span>
                        <span class="outer" style="width:40%;">
                            <div>
                                <span style="float:right;">
                                    <span class="label">&nbsp;</span>
                                    <div class="input" style="float: right;">
                                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.selectLocation.search" />" id="findLocationTopicsButton" onclick="dialogCatalogSpatial.findLocationTopics()" type="button">
                                            <fmt:message key="dialog.admin.catalog.selectLocation.search" />
                                        </button>
                                    </div>
                                </span>
                            </div>
                        </span>
                    </div>
                    <div>
                        &nbsp
                    </div>
                    <span id="resultLabel" class="label" style="clear:both; visibility:hidden;">
                        <fmt:message key="dialog.admin.catalog.selectLocation.selection" />
                    </span>
                    <span class="floatLeft">
                        <div class="checkboxContainer" id="resultList" style="width: 364px; height: 90px; overflow: auto;">
                        </div>
                    </span>
                    <span class="button transparent" style="float:right;">
                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.selectLocation.apply" />" id="addLocationTopicsButton" onclick="dialogCatalogSpatial.addLocationTopics()">
                            <fmt:message key="dialog.admin.catalog.selectLocation.apply" />
                        </button>
                    </span>
                    <span id="spatialLoadingZone" style="float:right; z-index: 100; visibility:hidden;">
                        <img src="img/ladekreis.gif" />
                    </span>
                    <div class="fill"></div>
                </div><!-- CONTENT END -->
            </div>
        </div>
    <!--</div>-->
</body>
</html>