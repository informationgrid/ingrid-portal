<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="Raumbezug festlegen" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<style type="text/css">
.floatLeft {
    float: left;
}

.bottomRight {
    margin-top: -50px !important;
    margin-left: 373px;
}

html> /**/ body .bottomRight {
    margin-top: 58px !important;
    margin-left: 373px;
}
</style>

<script type="text/javascript">

    var pageSpatialDlg;

    require([
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/keys",
        "dojo/dom",
        "dojo/topic",
        "dijit/registry",
        "dijit/form/CheckBox",
        "ingrid/utils/Grid",
        "ingrid/utils/Events"
    ], function(array, lang, on, keys, dom, topic, registry, CheckBox, UtilGrid, UtilEvents) {

            var checkboxArray = [];
            on(_container_, "Load", function() {
                // Enter key on the ValdiationTextbox has to start a search:
                var inputField = registry.byId("locationTextBox");
                on(inputField.domNode, "keypress",
                    function(event) {
                        if (event.keyCode == keys.ENTER) {
                            findLocationTopics();
                        }
                    }
                );
                console.log("Publishing event: '/afterInitDialog/SpatialNavigator'");
                topic.publish("/afterInitDialog/SpatialNavigator");
            });
            //});

            on(_container_, "Unload", function() {
                deleteComboboxWidgets();
            });

            function disableUiElements() {
                registry.byId("addLocationTopicsButton").set("disabled", true);
                registry.byId("findLocationTopicsButton").set("disabled", true);
            }

            function enableUiElements() {
                registry.byId("addLocationTopicsButton").set("disabled", false);
                registry.byId("findLocationTopicsButton").set("disabled", false);
            }

            function showLoadingZone() {
                dom.byId('spatialLoadingZone').style.visibility = "visible";
            }

            function hideLoadingZone() {
                dom.byId('spatialLoadingZone').style.visibility = "hidden";
            }

            function showNoResults() {
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "<fmt:message key='ui.obj.spatial.noResultsHint' />";
                }
                dom.byId("resultLabel").style.visibility = "hidden";
            }

            function showLoading() {
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    //      resultDiv.innerHTML = "<fmt:message key='spatial.loadingHint' />";
                    resultDiv.innerHTML = "";
                }
            }

            function showError() {
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    resultDiv.innerHTML = "<fmt:message key='ui.obj.spatial.connectionError' />";
                }
            }

            function deleteComboboxWidgets() {
                while (checkboxArray.length > 0) {
                    var checkBox = checkboxArray.pop();
                    checkBox.destroy();
                }
            }

            function resetResultDiv() {
                var resultDiv = dom.byId("resultList");
                if (resultDiv) {
                    while (resultDiv.lastChild)
                        resultDiv.removeChild(resultDiv.lastChild);
                }
                dom.byId("resultLabel").style.visibility = "hidden";
            }

            function setResultList(topicList) {
                if (topicList !== null && topicList.length > 0) {
                    resetResultDiv();
                    for (var i in topicList) {
                        var checkboxDiv = dom.byId("resultList");
                        if (checkboxDiv) {
                            var label = topicList[i].name;
                            if (label === null) continue;
                            if (topicList[i].type !== null) {
                                label += ", " + topicList[i].type;
                            }
                            var checkBox = new CheckBox({
                                id: topicList[i].topicId,
                                name: label,
                                topic: topicList[i]
                            });
                            checkboxArray.push(checkBox);
                            var divElement = document.createElement("div");

                            var linkElement = document.createElement("a");
                            linkElement.setAttribute("href", "#");
                            linkElement.topicId = topicList[i].topicId;
                            linkElement.onclick = function() {
                                findAssociatedLocations(this.topicId);
                            };
                            linkElement.setAttribute("href", "#");
                            linkElement.innerHTML = label;

                            checkboxDiv.appendChild(divElement);
                            divElement.appendChild(checkBox.domNode);
                            divElement.appendChild(linkElement);
                        }
                    }
                    dom.byId("resultLabel").style.visibility = "visible";
                } else {
                    showNoResults();
                }
            }

            // This function queries the SNSService for similar location topics
            function findAssociatedLocations(topicId) {
                resetResultDiv();
                deleteComboboxWidgets();
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
                    timeout: 0,
                    errorHandler: showError
                });
            }

            // 'Search Button' onClick function
            // This function queries the SNSService for location topics
            function findLocationTopics() {
                var queryTerm = registry.byId("locationTextBox").getValue();

                // If input is blank, do nothing
                queryTerm = lang.trim(queryTerm);
                if (queryTerm.length === 0)
                    return;

                resetResultDiv();
                deleteComboboxWidgets();
                showLoading();

                SNSService.getLocationTopics(queryTerm, "beginsWith", null, userLocale, {
                    preHook: function() {
                        showLoadingZone();
                        disableUiElements();
                    },
                    postHook: function() {
                        hideLoadingZone();
                        enableUiElements();
                    },
                    callback: setResultList,
                    timeout: 0,
                    errorHandler: showError
                });
            }

            // 'Add Button' onClick function
            // This function adds the location topics to the main geothesaurus table
            function addLocationTopics() {
                if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/SpatialNavigator")) return;

                array.forEach(checkboxArray, function(item) {
                    if (!item.checked) {
                        return;
                    }

                    var topic = item.topic;
                    var topicId = item.topic.topicId;

                    var storedTopic = null;
                    var storeData = UtilGrid.getTableData("spatialRefAdminUnit");
                    for (var i = 0; i < storeData.length; ++i) {
                        if (storeData[i].topicId == topicId) {
                            storedTopic = i;
                            break;
                        }
                    }

                    var myTopicLabel = topic.name;
                    if (topic.type !== null) {
                        myTopicLabel = myTopicLabel + ", " + topic.type;
                    }

                    if (storedTopic !== null) {
                        // Element is already in the table. Update the topic.
                        // Update elements that are displayed in the table
                        UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "label", myTopicLabel);
                        if (topic.boundingBox) {
                            UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "longitude1", topic.boundingBox[0]);
                            UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "latitude1", topic.boundingBox[1]);
                            UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "longitude2", topic.boundingBox[2]);
                            UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "latitude2", topic.boundingBox[3]);
                        }
                        // Update elements that aren't displayed in the table
                        // same as above with SlickGrid ... but can be optimized!
                        UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "nativeKey", topic.nativeKey);
                        UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "name", topic.name);
                        UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "topicType", topic.type);
                        UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopic, "topicTypeId", topic.typeId);

                    } else {
                        // The topic was not found in the result list. Create a new key and
                        // add the topic to the list
                        //var key = UtilStore.getNewKey(store);
                        if (topic.boundingBox) {
                            UtilGrid.addTableDataRow("spatialRefAdminUnit", {
                                //Id: key,
                                topicId: topic.topicId,
                                label: myTopicLabel,
                                name: topic.name,
                                longitude1: topic.boundingBox[0],
                                latitude1: topic.boundingBox[1],
                                longitude2: topic.boundingBox[2],
                                latitude2: topic.boundingBox[3],
                                nativeKey: topic.nativeKey,
                                topicType: topic.type,
                                topicTypeId: topic.typeId
                            });
                        } else {
                            UtilGrid.addTableDataRow("spatialRefAdminUnit", {
                                //Id: key,
                                topicId: topic.topicId,
                                label: myTopicLabel,
                                name: topic.name,
                                nativeKey: topic.nativeKey,
                                topicType: topic.type,
                                topicTypeId: topic.typeId
                            });
                        }
                    }
                });
                registry.byId("pageDialog").hide();
            }

            pageSpatialDlg = {
                addLocationTopics: addLocationTopics,
                findLocationTopics: findLocationTopics
            };
        }
    );
</script>
</head>

<body>
    <div id="catalogueSpatialRef" class="">
        <div id="winNavi" style="top:0;">
            <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-3#maintanance-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
        </div>
        <div id="spatialRefContent" class="content">
            <!-- CONTENT START -->
            <div class="inputContainer">
                <span class="outer halfWidth"><div>
                    <span class="label"><label for="locationTextBox" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7021)"><fmt:message key="dialog.spatialNavigator.title" /></label></span>
                    <div class="input">
                        <input type="text" id="locationTextBox" size="20" name="locationTextBox" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" />
                    </div>
                    </div>
                </span>
                <span class="outer halfWidth"><div>
                    <span class="label">&nbsp;</span>
                    <div class="input" style="float:right;">
                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.spatialNavigator.search" />" id="findLocationTopicsButton" style="margin:0;" onclick="pageSpatialDlg.findLocationTopics" type="button"><fmt:message key="dialog.spatialNavigator.search" /></button>
                    </div>
                    </div>
                </span>
                
                <span class="outer" style="width:60%;"><div>
                    <span id="resultLabel" class="label" style="visibility:hidden;"><fmt:message key="dialog.spatialNavigator.select" /></span>
    
                    <span class="input">
                        <div id="resultList" style="width: 100%; height: 90px; overflow: auto;"></div>
                    </span>
                    </div>
                </span>
                <span class="outer" style="width:40%; margin-top:53px;"><div>
                    <span class="input">
                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.spatialNavigator.apply" />" style="float:right;" id="addLocationTopicsButton" onclick="pageSpatialDlg.addLocationTopics()">
                            <fmt:message key="dialog.spatialNavigator.apply" />
                        </button>
                        <span id="spatialLoadingZone" style="float:right; z-index: 100; margin: 3px 3px;visibility:hidden;">
                            <img src="img/ladekreis.gif" />
                        </span>
                    </span>
                    </div>  
                </span>
                <div class="fill"></div>
            </div>
            <!-- CONTENT END -->
        </div>
    </div>

</body>
</html>
