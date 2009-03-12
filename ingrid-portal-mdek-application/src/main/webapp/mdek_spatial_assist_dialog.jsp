<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Raumbezug festlegen</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	dojo.widget.byId("spatialAssistCS").setLabel("");
});
_container_.addOnUnload(function() {

});


function validateInput() {
	if (dojo.widget.byId("spatialAssistCS").getValue() == ""
	 || dojo.widget.byId("spatialAssistRef").getValue() == ""
	 || !dojo.widget.byId("spatialAssistLon1").isValid()
	 || !dojo.widget.byId("spatialAssistLat1").isValid()
	 || !dojo.widget.byId("spatialAssistLon2").isValid()
	 || !dojo.widget.byId("spatialAssistLat2").isValid())
		return false;

	return true;
}

this.addLocation = function() {
	if (!validateInput()) {
	  	dialog.show(message.get("general.hint"), message.get("dialog.fillAllFieldsHint"), dialog.WARNING);
		return;
	}

	var location = dojo.widget.byId("spatialAssistRef").getValue();
	var fromSRS = dojo.widget.byId("spatialAssistCS").getValue();
	var toSRS = "GEO84";
	var coords = {longitude1: dojo.widget.byId("spatialAssistLon1").getValue(), 
				  latitude1: dojo.widget.byId("spatialAssistLat1").getValue(),
				  longitude2: dojo.widget.byId("spatialAssistLon2").getValue(),
				  latitude2: dojo.widget.byId("spatialAssistLat2").getValue()
				 };

	CTService.getCoordinates(fromSRS, toSRS, coords, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,

		callback: function(res) {
			var dstStore = dojo.widget.byId("spatialRefLocation").store;
			if (res.coordinate) {
				dstStore.addData({
					Id: UtilStore.getNewKey(dstStore),
					name: location,
					longitude1: res.coordinate.longitude1,
					latitude1: res.coordinate.latitude1,
					longitude2: res.coordinate.longitude2,
					latitude2: res.coordinate.latitude2					
				});
			} else {
				dialog.show(message.get('general.error'), message.get('cts.transformError'), dialog.WARNING);
				_container_.closeWindow();
			}
			_container_.closeWindow();
		},
		timeout:8000,
		errorHandler:function(err) {
			dojo.debug("Error: "+err);
			dialog.show(message.get('general.error'), message.get('cts.serviceError'), dialog.WARNING, null, 300, 170);
			_container_.closeWindow();
		}
	});
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("ctsLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("ctsLoadingZone"), "hidden");
}

</script>
</head>

<body>

<div dojoType="ContentPane">

	<div class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 7022)" title="Hilfe">[?]</a>
		</div>
		<div id="spatialAssistContent" class="content">

			<div class="spacer"></div>
			<div class="spacer"></div>
			<!-- CONTENT START -->
			<div class="inputContainer w478 noSpaceBelow">
			<table cellspacing="0">
				<tbody>
					<tr>
						<td class="label"><label for="spatialAssistRef" onclick="javascript:dialog.showContextHelp(arguments[0], 7023, 'Freier Raumbezug')"><fmt:message key="dialog.spatialAssist.customLocation" /></label></td>
						<td colspan="2"><input type="text" dojoType="ingrid:ValidationTextbox" id="spatialAssistRef" class="w248"/></td>
					</tr>
					<tr>
						<td class="label"><label for="spatialAssistLon1" onclick="javascript:dialog.showContextHelp(arguments[0], 7024, 'Unten Links: Rechtswert / L&auml;nge')"><fmt:message key="dialog.spatialAssist.longitude1" /></label></td>
						<td colspan="2"><input type="text" dojoType="RealNumberTextbox" id="spatialAssistLon1" class="w248"/></td>
					</tr>
					<tr>
						<td class="label"><label for="spatialAssistLat1" onclick="javascript:dialog.showContextHelp(arguments[0], 7025, 'Unten Links: Hochwert / Breite')"><fmt:message key="dialog.spatialAssist.latitude1" /></label></td>
						<td colspan="2"><input type="text" dojoType="RealNumberTextbox" id="spatialAssistLat1" class="w248"/></td>
					</tr>
					<tr>
						<td class="label"><label for="spatialAssistLon2" onclick="javascript:dialog.showContextHelp(arguments[0], 7026, 'Oben Rechts: Rechtswert / L&auml;nge')"><fmt:message key="dialog.spatialAssist.longitude2" /></label></td>
						<td colspan="2"><input type="text" dojoType="RealNumberTextbox" id="spatialAssistLon2" class="w248"/></td>
					</tr>
					<tr>
						<td class="label"><label for="spatialAssistLat2" onclick="javascript:dialog.showContextHelp(arguments[0], 7027, 'Oben Rechts: Hochwert / Breite')"><fmt:message key="dialog.spatialAssist.latitude2" /></label></td>
						<td colspan="2"><input type="text" dojoType="RealNumberTextbox" id="spatialAssistLat2" class="w248"/></td>
					</tr>
					<tr>
						<td class="label"><label for="spatialAssistCS" onclick="javascript:dialog.showContextHelp(arguments[0], 7028, 'Koordinatensystem')"><fmt:message key="dialog.spatialAssist.coordSys" /></label></td>
						<td colspan="2">
							<div id="spatialAssistCS" dojoType="ingrid:Select" toggle="plain" dataUrl="js/data/spatialReferenceSystems.js" style="width:230px;" />
						</td>
					</tr>
				</tbody>
		  	</table>

			<span style="float:right;">
				<button dojoType="ingrid:Button" title="Hinzuf&uuml;gen" onClick="javascript:scriptScope.addLocation();"><fmt:message key="dialog.spatialAssist.add" /></button>
			</span>
			<span id="ctsLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden;">
				<img src="img/ladekreis.gif" />
			</span>

			</div>
	  		<!-- CONTENT END -->
		</div>
	</div>
</div>

</body>
</html>
