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
            var scriptScope = this;
            var radioButtonArray = new Array();
   
            dojo.connect(_container_, "onLoad", init);
            
            dojo.connect(_container_, "onUnLoad", function(){
                if (_container_.customParams && _container_.customParams.resultHandler) {
                    if (_container_.customParams.resultHandler.fired == -1) {
                        _container_.customParams.resultHandler.errback();
                    }
                }
            });
            
            function init(){
                // Enter key on the ValdiationTextbox has to start a search:
                dojo.connect(dojo.byId("locationTextBox"), "onkeypress", function(event){
                    //console.debug("pressed: " + event.keyCode + " expected: " + dojo.keys.ENTER);
                    if (event.keyCode == dojo.keys.ENTER) {
                        scriptScope.findLocationTopics();
                    }
                });
            }
            
            function searchOnEnter(event) {
                console.debug("pressed: " + event.keyCode + " expected: " + dojo.keys.ENTER);
                if (event.keyCode == dojo.keys.ENTER) {
                    scriptScope.findLocationTopics();
                }
            }
            
            function resetResultDiv(){
                var resultDiv = dojo.byId("resultList");
                if (resultDiv) {
                    while (resultDiv.lastChild) 
                        resultDiv.removeChild(resultDiv.lastChild);
                }
                dojo.byId("resultLabel").style.visibility = "hidden";
            }
            
            function setResultList(topicList){
                if (topicList != null && topicList.length > 0) {
                    resetResultDiv();
                    radioButtonArray = new Array();
                    var checkboxDiv = dojo.byId("resultList");
                    for (var i in topicList) {
                        var label = topicList[i].name;
                        if (label == null) 
                            continue;
                        if (topicList[i].type != null) {
                            label += ", " + topicList[i].type;
                        }
                        var radioButton = document.createElement("input");
                        radioButton.setAttribute("type", "radio");
                        radioButton.setAttribute("name", "spatialReference");
                        radioButton.setAttribute("id", topicList[i].topicId);
                        radioButton.topic = topicList[i];
                        dojo.addClass(radioButton, "radioButton");
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
                    dojo.byId("resultLabel").style.visibility = "visible";
                }
                else {
                    showNoResults();
                }
            }
            
            // This function queries the SNSService for similar location topics
            function findAssociatedLocations(topicId){
                resetResultDiv();
                showLoading();
                
                SNSService.getLocationTopicsById(topicId, {
                    preHook: function(){
                        showLoadingZone();
                        disableUiElements();
                    },
                    postHook: function(){
                        hideLoadingZone();
                        enableUiElements();
                    },
                    callback: setResultList,
                    errorHandler: showError,
					timeout: 0
                });
            }
            
            // 'Search Button' onClick function
            // This function queries the SNSService for location topics
            scriptScope.findLocationTopics = function(){
                var queryTerm = dijit.byId("locationTextBox").getValue();
                
                // If input is blank, do nothing
                queryTerm = dojo.trim(queryTerm);
                if (queryTerm.length == 0) 
                    return;
                
                resetResultDiv();
                showLoading();
                
                SNSService.getLocationTopics(queryTerm, "beginsWith", "/location/admin", {
                    preHook: function(){
                        showLoadingZone();
                        disableUiElements();
                    },
                    postHook: function(){
                        hideLoadingZone();
                        enableUiElements();
                    },
                    callback: setResultList,
                    errorHandler: function(){alert("mist!");}, //showError,
					timeout: 0
                });
            }
            
            // 'Add Button' onClick function
            // This function returns the selected topic via the attached resultHandler
            scriptScope.addLocationTopics = function(){
            
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
                        }
                        
                        if (radioButtonArray[i].topic.boundingBox && radioButtonArray[i].topic.boundingBox.length == 4) {
                            location.longitude1 = radioButtonArray[i].topic.boundingBox[0];
                            location.latitude1 = radioButtonArray[i].topic.boundingBox[1];
                            location.longitude2 = radioButtonArray[i].topic.boundingBox[2];
                            location.latitude2 = radioButtonArray[i].topic.boundingBox[3];
                        }
                        console.debug("call callback");
                        //			_container_.customParams.resultHandler.callback(radioButtonArray[i].topic);
                        dijit.byId("pageDialog").customParams.resultHandler.callback(location);
                    }
                }
                
				console.debug("close window");
				dijit.byId("pageDialog").hide();
                //_container_.closeWindow();
            }
            
            
            function disableUiElements(){
                dijit.byId("addLocationTopicsButton").set("disabled", true);
                dijit.byId("findLocationTopicsButton").set("disabled", true);
            }
            
            function enableUiElements(){
                dijit.byId("addLocationTopicsButton").set("disabled", false);
                dijit.byId("findLocationTopicsButton").set("disabled", false);
            }
            
            function showLoadingZone(){
                dojo.style("spatialLoadingZone", "visibility", "visible");
            }
            
            function hideLoadingZone(){
                dojo.style("spatialLoadingZone", "visibility", "hidden");
            }
            
            function showNoResults(){
                var resultDiv = dojo.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "<fmt:message key='ui.obj.spatial.noResultsHint' />";
                }
                dojo.byId("resultLabel").style.visibility = "hidden";
            }
            
            function showLoading(){
                var resultDiv = dojo.byId("resultList");
                if (resultDiv) {
                    //		resultDiv.innerHTML = "<fmt:message key='spatial.loadingHint' />";
                    resultDiv.innerHTML = "";
                }
            }
            
            function showError(){
                var resultDiv = dojo.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "<fmt:message key='ui.obj.spatial.connectionError' />";
                }
            }
            
        </script>
    </head>
    <body>
        <!--<div dojoType="dijit.layout.ContentPane">-->
            <div id="catalogueSpatialRef">
                <div id="winNavi" style="position: absolute; right: 10px;">
                    <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=maintanance-of-objects-3#maintanance-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                </div>
                <div id="spatialRefContent" class="content">
                    <!-- CONTENT START -->
                    <div class="inputContainer">
                        <span class="label">
                            <label for="locationTextBox" onclick="javascript:dialog.showContextHelp(arguments[0], 8008)">
                                <fmt:message key="dialog.admin.catalog.selectLocation.setLocation" />
                            </label>
                        </span>
                        <div class="input">
                            <span style="position:relative; float:left; width:292px;">
								<input type="text" id="locationTextBox" size="20" name="locationTextBox" style="width:292px;" dojoType="dijit.form.ValidationTextBox" />
							</span>
							<span style="float:right;">
                                <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.selectLocation.search" />" id="findLocationTopicsButton" onClick="javascript:scriptScope.findLocationTopics();">
                                    <fmt:message key="dialog.admin.catalog.selectLocation.search" />
                                </button>
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
                            <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.selectLocation.apply" />" id="addLocationTopicsButton" onClick="javascript:scriptScope.addLocationTopics();">
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