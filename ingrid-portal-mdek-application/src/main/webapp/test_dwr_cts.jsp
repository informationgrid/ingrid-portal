<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script
	src='/ingrid-portal-mdek-application/dwr/interface/CTService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>


<script>
dojo.require("dojo.widget.ComboBox");
dojo.require("dojo.widget.RealNumberTextbox");

dojo.addOnLoad(function()
{
  init();
});

function init() {
	dojo.widget.byId("fromSRS").setValue("GEO84");
	dojo.widget.byId("toSRS").setValue("GK3");
}

function transform() {
	var fromSRS = dojo.widget.byId("fromSRS").getValue();
	var toSRS = dojo.widget.byId("toSRS").getValue();
	var coord = {
		longitude1: dojo.widget.byId("longitude1").getValue(),
		latitude1: dojo.widget.byId("latitude1").getValue(),
		longitude2: dojo.widget.byId("longitude2").getValue(),
		latitude2: dojo.widget.byId("latitude2").getValue()
	};

	CTService.getCoordinates(fromSRS, toSRS, coord, {
			callback: function(res){
				var coord = res.coordinate;
				dojo.widget.byId("dstLongitude1").setValue(coord.longitude1);
				dojo.widget.byId("dstLatitude1").setValue(coord.latitude1);
				dojo.widget.byId("dstLongitude2").setValue(coord.longitude2);
				dojo.widget.byId("dstLatitude2").setValue(coord.latitude2);
			},
			timeout:5000,
			errorHandler:function(message) {alert(message); }
		}
	);
}

</script>

<body>
<div>
	<label for="fromSRS">Source Spatial Reference System:</label>
	<select dojoType="ComboBox" id="fromSRS" style="width:200px;">
		<option>GEO84</option>
		<option>GEO_BESSEL_POTSDAM</option>
		<option>GK2</option>
		<option>GK3</option>
		<option>GK4</option>
		<option>GK5</option>
		<option>UTM32w</option>
		<option>UTM33w</option>
		<option>UTM32s</option>
		<option>UTM33s</option>
		<option>LAMGw</option>
	</select>
	<label for="toSRS">Target Spatial Reference System:</label>
	<select dojoType="ComboBox" id="toSRS" style="width:200px;">
		<option>GEO84</option>
		<option>GEO_BESSEL_POTSDAM</option>
		<option>GK2</option>
		<option>GK3</option>
		<option>GK4</option>
		<option>GK5</option>
		<option>UTM32w</option>
		<option>UTM33w</option>
		<option>UTM32s</option>
		<option>UTM33s</option>
		<option>LAMGw</option>
	</select>
</div>
<label for="longitude1">Longitude of coordinate one:</label>
<input type="text" id="longitude1" value="10" dojoType="RealNumberTextbox" /><br>
<label for="latitude1">Latitude of coordinate one:</label>
<input type="text" id="latitude1" value="50" dojoType="RealNumberTextbox" /><br>
<label for="longitude2">Longitude of coordinate two:</label>
<input type="text" id="longitude2" value="11" dojoType="RealNumberTextbox" /><br>
<label for="latitude2">Latitude of coordinate two:</label>
<input type="text" id="latitude2" value="51" dojoType="RealNumberTextbox" /><br>

<button dojoType="Button" title="Transform" onClick="transform">Transform</button>

<p>Transformed coordinate:</p>
<label for="dstLongitude1">Result Longitude one:</label>
<input type="text" id="dstLongitude1" dojoType="RealNumberTextbox"/><br>
<label for="dstLatitude1">Result Latitude one:</label>
<input type="text" id="dstLatitude1" dojoType="RealNumberTextbox"/><br>
<label for="dstLongitude2">Result Longitude two:</label>
<input type="text" id="dstLongitude2" dojoType="RealNumberTextbox"/><br>
<label for="dstLatitude2">Result Latitude two:</label>
<input type="text" id="dstLatitude2" dojoType="RealNumberTextbox"/><br>

</body>
</html>
