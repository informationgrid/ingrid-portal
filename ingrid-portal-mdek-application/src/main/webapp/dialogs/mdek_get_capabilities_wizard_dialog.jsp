<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.capabilities.wizard.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var dialogGetCapWizard;

require([
    "dojo/on",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/_base/array",
    "dijit/registry",
    "dojo/keys",
    "dojo/topic",
    "dojox/layout/ContentPane",
    "ingrid/utils/String",
    "ingrid/IgeEvents"
], function(on, dom, domClass, array, registry, keys, topic, ContentPane, UtilString, igeEvents) {

    var customParams = _container_.customParams;
    var assistantUrl = null;

    new ContentPane({
        title: "results",
        layoutAlign: "client",
        href: "dialogs/mdek_wizard_result.jsp?c="+userLocale,
        preload: "true",
        scriptHasHooks: true,
        executeScripts: true
    }, "resultsContainer");

    on(_container_, "Load", function() {
        showLoadingZone();
        var self = this;
        assistantUrl = registry.byId("assistantURL");
        
        // Pressing 'enter' on the url input field is equal to a start button click
        on(assistantUrl.domNode, "keypress", function(event) {
            if (event.keyCode == keys.ENTER) {
                startRequest();
            }
        });
        
        registry.byId("objectClass").setValue("Class3");
        
        // start the request when sub-container is loaded
        on(registry.byId("resultsContainer"), "Load", function() {
            hideLoadingZone();
            assistantUrl.set("disabled", false);
            assistantUrl.focus();

            dialogWizardResults.thisDialog = self;
            dialogGetCapWizard.closeThisDialog = dialogWizardResults.closeThisDialog;
            
            // auto execute url if it's been given
            if (customParams && customParams.url) {
                registry.byId("assistantURL").set("value", customParams.url);
                startRequest();
            }
        });
        
        on( registry.byId("assistantCheckAll"), "click", function() {
            var checkedValue = registry.byId("assistantCheckAll").checked;
            dialogWizardResults.checkAll( checkedValue );
        } );

        console.log("Publishing event: '/afterInitDialog/CapabilitiesWizard'");
        topic.publish("/afterInitDialog/CapabilitiesWizard");
    });


    function startRequest() {
        var url = assistantUrl.getValue();
        var capUrl = UtilString.addCapabilitiesParameter("2", url);
        console.debug(url + " -> " + capUrl);
        
        var setOperationValues = function(capBean) {
            var data = prepareData(capBean);
            domClass.remove( dom.byId("assistantCheckAllContainer"), "hide" );
            domClass.add( dom.byId("btnWizardCancel"), "hide" );
            domClass.add( dom.byId("btnWizardSearch"), "hide" );
            assistantUrl.set( "disabled", true );
            dialogWizardResults.updateInputFields(data, {title: capBean.title, content: null});
            dialogWizardResults.showResults(true, false, true);
        };
        
        dom.byId("assistantGetCapRequestedUrl").innerHTML = "("+capUrl+")";
        igeEvents.getCapabilities(capUrl, dialogGetCapWizard).then(
            setOperationValues,
            function() {
                assistantUrl.set("disabled", false);
            });
    }

    function prepareData(bean) {
        console.log("Bean: ", bean);
        
        bean.indexedDocument = { description: bean.description };
        bean.thesaTopics = [];
        bean.locationTopics = [];
        bean.eventTopics = [];
        bean.events = [];
        bean.CRS = [];
        bean.CRSunknown = [];
        
        // Bounding boxes
        array.forEach(bean.boundingBoxes, function(box) {
            box.boundingBox = [ box.longitude1, box.latitude1, box.longitude2, box.latitude2 ];
            bean.locationTopics.push( box );
        });
        
        // CRS
        array.forEach(bean.spatialReferenceSystems, function(crs) {
            if (crs.id != "-1") {
                bean.CRS.push(crs);
            } else {
                bean.CRSunknown.push(crs);
            }
        });
        
        // add new Date if no time ref has been found in capabilities!
        if ( bean.timeReferences && bean.timeReferences.length > 0 ) {
            bean.events = bean.timeReferences;
        } else {
            bean.events.push({ date: new Date(), type: "1" });
        }
        
        if ( bean.timeSpans ) {
            array.forEach( bean.timeSpans, function( ref ) {
                ref.date1 = ref.from;
                ref.date2 = ref.to;
                bean.eventTopics.push(ref);
            });
        }
        
        // addresses
        bean.addresses = [ bean.address ];
        
        return bean;
    }

    function showLoadingZone() {
        dom.byId('assistantGetCapLoadingZone').style.visibility = "visible";
        registry.byId("assistantURL").set("disabled", true);
    }

    function hideLoadingZone() {
        dom.byId('assistantGetCapLoadingZone').style.visibility = "hidden";
    }

    dialogGetCapWizard = {
        showLoadingZone: showLoadingZone,
        hideLoadingZone: hideLoadingZone,
        // closeThisDialog: scopeWizardResults.closeThisDialog,
        startRequest: startRequest
    };
});

</script>
</head>

<body>

<div data-dojo-type="dijit/layout/ContentPane" style="height:650px">

    <div class="assistant">
        <div class="content">
            <!-- HEADER CONTENT START -->
            <div style="/*position: absolute; z-index: 1000; width: 100%; right: 0px;*/">
                <div class="inputContainer field grey">
                    <div id="winNavi" style="top:0; padding-bottom: 0;">
                        <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=creation-of-objects-3#creation-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
                    </div>
                    <span class="outer"><div style="padding:15px !important;">
                    <span class="label"><label for="assistantURL" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8061)"><fmt:message key="dialog.wizard.getCap.url" /></label></span>
                    <span class="input"><input type="text" id="assistantURL" name="assistantURL" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" /></span>
                    </div></span>
                    <div class="fill"></div>
                </div>
    
                <div class="inputContainer grey" style="height:30px; padding: 5px 0px ! important;">
                    <span id="assistantCheckAllContainer" style="float:left; margin-top:10px; margin-left:15px;" class="checkboxContainer hide"><label class="inActive input"><input type="checkbox" name="assistantCheckAll" id="assistantCheckAll" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.checkAll" /></label></span>
                    <span id="btnWizardCancel" style="float:right; margin-top:5px;"><button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.wizard.getCap.cancel" />" onclick="dialogGetCapWizard.closeThisDialog()"><fmt:message key="dialog.wizard.getCap.cancel" /></button></span>
                    <span id="btnWizardSearch" style="float:right; margin-top:5px;"><button data-dojo-type="dijit/form/Button" type="button" title="<fmt:message key="dialog.wizard.getCap.start" />" onclick="dialogGetCapWizard.startRequest()"><fmt:message key="dialog.wizard.getCap.start" /></button></span>
                    <span id="assistantGetCapLoadingZone" style="float:right; margin-top:6px; z-index: 100; visibility:hidden">
                        <span id="assistantGetCapRequestedUrl" class="comment"></span>
                        <img src="img/ladekreis.gif" />
                    </span>
                </div>
            </div>
            <!-- HEADER CONTENT END -->
            
            <div class="fill"></div>
            <div id="resultsContainer"></div>
        </div>
    </div>
</div>

</body>
</html>
