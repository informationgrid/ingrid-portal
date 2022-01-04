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
<title><fmt:message key="dialog.popup.object.wizard.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var dialogCreateObjectWiz = null;

require([
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/on",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/Deferred",
    "dojo/DeferredList",
    "dojo/topic",
    "dojo/keys",
    "dijit/registry",
    "dojox/layout/ContentPane",
    "ingrid/utils/Catalog",
    "ingrid/utils/Grid",
    "ingrid/utils/Thesaurus",
    "ingrid/utils/Store",
    "ingrid/utils/List",
    "ingrid/dialog"
], function(lang, array, on, dom, domClass, style, Deferred, DeferredList, topic, keys, registry, ContentPane, catalog, UtilGrid, UtilThesaurus, UtilStore, UtilList, dialog) {

    var thisDialog = _container_;


    new ContentPane({
        title: "results",
        layoutAlign: "client",
        href: "dialogs/mdek_wizard_result.jsp?c="+userLocale,
        preload: "true",
        scriptHasHooks: true,
        executeScripts: true
    }, "resultsContainer");

    on(_container_, "Load", function(){
        init();
        var self = this;
        console.log("Publishing event: '/afterInitDialog/CreateObjectWizard'");
        topic.publish("/afterInitDialog/CreateObjectWizard");

        // start the request when sub-container is loaded
        on(registry.byId("resultsContainer"), "Load", function() {
            dialogWizardResults.thisDialog = self;
        });
    });


    function init() {
        console.debug("init");
        registry.byId("assistantURL").setValue("https://");
        // registry.byId("assistantNumWords").setValue(1000);
        registry.byId("assistantHtmlContentNumWords").setValue(100);

        // Pressing 'enter' on the input field is equal to a button click
        on(registry.byId("assistantURL").domNode, "keypress", function(event) {
            if (event.keyCode == keys.ENTER) {
                startSearch();
            }
        });
        // Pressing 'enter' on the input field is equal to a button click
        /*on(registry.byId("assistantNumWords").domNode, "keypress", function(event) {
            if (event.keyCode == keys.ENTER) {
                startSearch();
            }
        });*/
        // Pressing 'enter' on the input field is equal to a button click
        on(registry.byId("assistantHtmlContentNumWords").domNode, "keypress", function(event) {
            if (event.keyCode == keys.ENTER) {
                startSearch();
            }
        });
        
        on(registry.byId("assistantCheckAll"), "click", function() {
            var checkedValue = registry.byId("assistantCheckAll").checked;
            dialogWizardResults.checkAll( checkedValue );
        });

    }

    // needed for checkbox-ids
    // distinguish between inspire topics and other ones
    // since it can happen that they have the same name it's important to
    // classify them so that the checkboxes can be identified
    function getThesaurusId(topic) {
        if (topic.source == "INSPIRE") {
            return "inspire_"+topic.title;
        } else {
            return topic.topicId;
        }
    }

    function createNewUrl() {
        var newUrl = {};

        //newUrl.Id = 0;
        newUrl.relationType = 9999;
        newUrl.relationTypeName = "unspezifischer Verweis";
        var urlName = lang.trim(registry.byId("assistantHtmlTitle").getValue());
        newUrl.name = urlName.length != 0 ? urlName : "Internet-Verweis";
        newUrl.url = lang.trim(registry.byId("assistantURL").getValue());
        newUrl.urlType = "";
        newUrl.description = "";

        return newUrl;
    }


    function showLoadingZone() {
        style.set("createObjectWizardLoadingZone", "visibility", "visible");
    }

    function hideLoadingZone() {
        style.set("createObjectWizardLoadingZone", "visibility", "hidden");
    }


    // SNS Start search button function
    // Reads the url from the input field and executes a SNS autoClassify request
    function startSearch() {
        dialogWizardResults.hideResults();
        dialogWizardResults.resetInputFields();
        var url = lang.trim(registry.byId("assistantURL").getValue());
        // var numWords = registry.byId("assistantNumWords").getValue();
        var htmlContentNumWords = registry.byId("assistantHtmlContentNumWords").getValue();

        var showDescription = registry.byId("assistantIncludeMetaTagCheckbox").checked;
        var showHtmlContent = registry.byId("assistantIncludeHtmlContentCheckbox").checked;

        var autoClassifyDef = autoClassifyUrl(url, /*numWords*/1000);
        var getHtmlContentDef = getHtmlContent(url, htmlContentNumWords);
        var getHtmlTitleDef = getHtmlTitle(url);

        showLoadingZone();
        new DeferredList([autoClassifyDef, getHtmlContentDef, getHtmlTitleDef])
        .then(function (resultList) {
            if (resultList[0][0] === false || resultList[1][0] === false || resultList[2][0] === false) {
                handleError(resultList[0], resultList[1]);

            } else {
                var topicMap = resultList[0][1];
                var htmlContent = resultList[1][1];
                var htmlTitle = resultList[2][1];
        
                if ((topicMap.indexedDocument === null || topicMap.indexedDocument.description === null) && topicMap.thesaTopics === null && topicMap.locationTopics === null && topicMap.eventTopics === null) {
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.wizard.create.snsNoResultError' />", dialog.INFO);

                } else {
                    console.debug(topicMap);
                    console.debug(htmlContent);
                    console.debug(htmlTitle);
                    dialogWizardResults.updateInputFields(topicMap, {title: htmlTitle, content: htmlContent}, true);
                    dialogWizardResults.showResults(showDescription, showHtmlContent);
                }
            }
            thisDialog.resize();
            domClass.remove( dom.byId("assistantCheckAllContainer"), "hide" );
            hideLoadingZone();
        }, function(err) {
            console.debug("l1 Error: "+err);
            hideLoadingZone();
        });
    }


    function handleError(autoClassifyResult, getHtmlContentResult) {
        var err = null;
        // If an sns error exists, display an error message
        if (autoClassifyResult[0] === false) {
            err = autoClassifyResult[1];

            if (err.message.indexOf("SNS_TIMEOUT") != -1) {
                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='sns.timeoutError' />", dialog.WARNING);

            } else if (err.message.indexOf("SNS_INVALID_URL") != -1) {
                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.wizard.create.snsInvalidPageError' />", dialog.WARNING);

            } else {
                displayErrorMessage(err);
            }

        } else if (getHtmlContentResult[0] === false) {
            // Handle the exception which was thrown while parsing 
            err = getHtmlContentResult[1];

            displayErrorMessage(err);
        }
    }

    function autoClassifyUrl(url, numWords) {
        var def = new Deferred();
        var catLocale = catalog.getCatalogLanguage();
        
        SNSService.autoClassifyURL(url, numWords, null, false, 100, catLocale, {
            callback: function(topicMap) {
                def.resolve(topicMap);
            },

            errorHandler: function(msg) {
                def.reject(new Error(msg));
            }
        });

        return def;
    }

    function getHtmlContent(url, maxWords) {
        var def = new Deferred();

        HttpService.parseHtml(url, maxWords, {
            callback: function(htmlContent) {
                def.resolve(htmlContent);
            },

            errorHandler: function(msg) {
                def.reject(new Error(msg));
            }
        });

        return def;
    }

    function getHtmlTitle(url) {
        var def = new Deferred();

        HttpService.getHtmlTitle(url, {
            callback: function(title) {
                def.resolve(title);
            },

            errorHandler: function(msg) {
                def.reject(new Error(msg));
            }
        });

        return def;
    }

    dialogCreateObjectWiz = {
        startSearch: startSearch
    };
});

</script>
</head>

<body>
    <div data-dojo-type="dijit/layout/ContentPane" style="height:650px">
        <div id="assistant" class="">
        <div id="assistantContent" class="content">
            <div id="winNavi" style="top:0px; right: 3px;">
                    <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=creation-of-objects-2#creation-of-objects-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <!-- LEFT HAND SIDE CONTENT START -->
            <div class="inputContainer field grey" style="padding:10px !important;">
                <span class="outer">
                    <div>
                        <span class="label"><label for="assistantURL"
                                                   onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8063)"><fmt:message
                                key="dialog.wizard.create.url"/></label></span>
                        <span class="input"><input type="text" id="assistantURL" name="assistantURL" style="width:100%;"
                                                   data-dojo-type="dijit/form/ValidationTextBox"/></span>
                    </div>
                </span>
                <span class="outer">
                    <div>
                        <span>

                            <div class="checkboxContainer">
                                <span class="input">
                                    <input type="checkbox" name="assistantIncludeMetaTagCheckbox"
                                           id="assistantIncludeMetaTagCheckbox" data-dojo-type="dijit/form/CheckBox" checked/>
                                    <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8065, 'Beschreibung')"><fmt:message
                                            key="dialog.wizard.create.showDescription"/></label>
                                </span>
                                <span class="input">
                                    <input type="checkbox" name="assistantIncludeHtmlContentCheckbox"
                                           id="assistantIncludeHtmlContentCheckbox" data-dojo-type="dijit/form/CheckBox"
                                           checked/>
                                    <label style="cursor:default;">
                                        <fmt:message key="dialog.wizard.create.showNumWords.1"/>
                                        <input data-dojo-type="dijit/form/NumberTextBox" min="0" max="10000" maxlength="5"
                                               id="assistantHtmlContentNumWords"/> <fmt:message
                                            key="dialog.wizard.create.showNumWords.2"/>
                                    </label>
                                </span>
                            </div>
                        </span>
                    </div>
                    <div class="fill"></div>
                </span>
            </div>

            <div class="inputContainerFooter">
                <span class="button">
                    <span>
                        <button id="createObjWizardStartButton" type="button" style="float:right" data-dojo-type="dijit/form/Button" onclick="dialogCreateObjectWiz.startSearch();" title="<fmt:message key="dialog.wizard.create.start" />"><fmt:message key="dialog.wizard.create.start" /></button>
                    </span>
                    <span id="assistantCheckAllContainer" style="float:left; margin-top:5px; margin-left:15px;" class="checkboxContainer hide"><label class="inActive input"><input type="checkbox" name="assistantCheckAll" id="assistantCheckAll" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.checkAll" /></label></span>
                    <span id="createObjectWizardLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                        <img src="img/ladekreis.gif" />
                    </span>
                </span>
            </div>
            <div class="fill"></div>
            <div id="resultsContainer"></div>
        </div>
    </div>
    
    </div>
</body>
</html>
