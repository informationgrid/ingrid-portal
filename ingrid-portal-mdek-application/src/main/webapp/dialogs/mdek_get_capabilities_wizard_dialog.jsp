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
var scopeCapWiz = _container_;

var thisDialog = dijit.byId("subPageDialog");
if (thisDialog == undefined)
    thisDialog = dijit.byId("pageDialog");
    

new dojox.layout.ContentPane({
    title: "results",
    layoutAlign: "client",
    href: "dialogs/mdek_wizard_result.jsp?c="+userLocale,
    preload: "true",
    scriptHasHooks: true,
    executeScripts: true
}, "resultsContainer");

dojo.connect(_container_, "onLoad", function() {
    scopeCapWiz.showLoadingZone();
    
    // Pressing 'enter' on the url input field is equal to a start button click
    dojo.connect(dijit.byId("assistantURL").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scopeCapWiz.startRequest();
            }
	});
    
	dijit.byId("objectClass").setValue("Class3");
	
    // start the request when sub-container is loaded
    dojo.connect(dijit.byId("resultsContainer"), "onLoad", function() {
    	scopeCapWiz.hideLoadingZone();
    	dijit.byId("assistantURL").focus();
    	
    	// auto execute url if it's been given
    	if (scopeCapWiz.customParams && scopeCapWiz.customParams.url) {
    	    dijit.byId("assistantURL").set("value", scopeCapWiz.customParams.url);
            scopeCapWiz.startRequest();
    	}
    });
    
    dojo.connect( dijit.byId("assistantCheckAll"), "onClick", function() {
        var checkedValue = dijit.byId("assistantCheckAll").checked;
        scopeWizardResults.checkAll( checkedValue );
    } );

    console.log("Publishing event: '/afterInitDialog/CapabilitiesWizard'");
    dojo.publish("/afterInitDialog/CapabilitiesWizard");
});


scopeCapWiz.startRequest = function() {
	var url = dijit.byId("assistantURL").getValue();
	var capUrl = UtilString.addCapabilitiesParameter("2", url);
	console.debug(url + " -> " + capUrl);
	
	var setOperationValues = function(capBean) {
	    var data = scopeCapWiz.prepareData(capBean);
	    dojo.removeClass( dojo.byId("assistantCheckAllContainer"), "hide" );
	    dojo.addClass( dojo.byId("btnWizardCancel"), "hide" );
	    dojo.addClass( dojo.byId("btnWizardSearch"), "hide" );
	    dijit.byId( "assistantURL" ).set( "disabled", true );
	    scopeWizardResults.updateInputFields(data, {title: capBean.title, content: null});
	    scopeWizardResults.showResults(true, false, true);
	}
	
	dojo.byId("assistantGetCapRequestedUrl").innerHTML = "("+capUrl+")";
	igeEvents.getCapabilities(capUrl, setOperationValues, scopeCapWiz);
}

scopeCapWiz.prepareData = function(bean) {
    console.log("Bean: ", bean);
    
    bean.indexedDocument = { description: bean.description }
    bean.thesaTopics = [];
    bean.locationTopics = [];
    bean.eventTopics = [];
    bean.events = []; 
    bean.CRS = [];
    bean.CRSunknown = [];
    
    // Bounding boxes
    dojo.forEach(bean.boundingBoxes, function(box) {
        box.boundingBox = [ box.longitude1, box.latitude1, box.longitude2, box.latitude2 ];
        bean.locationTopics.push( box );
    });
    
    // CRS
    dojo.forEach(bean.spatialReferenceSystems, function(crs) {
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
        dojo.forEach( bean.timeSpans, function( ref ) {
            ref.date1 = ref.from;
            ref.date2 = ref.to;
            bean.eventTopics.push(ref);
        });
    }
    
    // addresses
    bean.addresses = [ bean.address ];
    
    return bean;    
}

scopeCapWiz.showLoadingZone = function() {
	dojo.byId('assistantGetCapLoadingZone').style.visibility = "visible";
	dijit.byId("assistantURL").set("disabled", true);
}

scopeCapWiz.hideLoadingZone = function() {
	dojo.byId('assistantGetCapLoadingZone').style.visibility = "hidden";
	dijit.byId("assistantURL").set("disabled", false);
}

</script>
</head>

<body>

<div dojoType="dijit.layout.ContentPane" style="height:650px">

	<div class="assistant">
		<div class="content">
			<!-- HEADER CONTENT START -->
            <div style="/*position: absolute; z-index: 1000; width: 100%; right: 0px;*/">
    			<div class="inputContainer field grey">
        			<div id="winNavi" style="top:0; padding-bottom: 0;">
                        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=creation-of-objects-3#creation-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                    </div>
    			    <span class="outer"><div style="padding:15px !important;">
    				<span class="label"><label for="assistantURL" onclick="javascript:dialog.showContextHelp(arguments[0], 8061)"><fmt:message key="dialog.wizard.getCap.url" /></label></span>
    				<span class="input"><input type="text" id="assistantURL" name="assistantURL" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                    </div></span>
                    <div class="fill"></div>
    			</div>
    
    			<div class="inputContainer grey" style="height:30px; padding: 5px 0px ! important;">
    			    <span id="assistantCheckAllContainer" style="float:left; margin-top:10px; margin-left:15px;" class="checkboxContainer hide"><label class="inActive input"><input type="checkbox" name="assistantCheckAll" id="assistantCheckAll" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.checkAll" /></label></span>
    		        <span id="btnWizardCancel" style="float:right; margin-top:5px;"><button dojoType="dijit.form.Button" title="<fmt:message key="dialog.wizard.getCap.cancel" />" onClick="javascript:scopeWizardResults.closeThisDialog();"><fmt:message key="dialog.wizard.getCap.cancel" /></button></span>
    		        <span id="btnWizardSearch" style="float:right; margin-top:5px;"><button dojoType="dijit.form.Button" type="button" title="<fmt:message key="dialog.wizard.getCap.start" />" onClick="javascript:scopeCapWiz.startRequest();"><fmt:message key="dialog.wizard.getCap.start" /></button></span>
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
