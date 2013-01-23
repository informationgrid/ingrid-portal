<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script src='/ingrid-portal-mdek-application/dwr/interface/CTService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
    var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
    var isRelease = false;
</script>
<script type="text/javascript" src="dojo-src/release/dojo/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
<script type="text/javascript" src="js/config.js"></script>
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dijit/themes/claro/claro.css" />
</head>

<script>
dojo.require("dijit.form.Button");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.NumberTextBox");

dojo.addOnLoad(function()
{
  init();
});

function init() {
    dijit.byId("fromSRS").set('value',"GEO_WGS84");
    dijit.byId("toSRS").set('value',"GK3");
}

function transform() {
    var fromSRS = dijit.byId("fromSRS").get('value');
    console.debug("fromSRS: " + fromSRS);
    var toSRS = dijit.byId("toSRS").get('value');
    console.debug("toSRS: " + toSRS);
    var coord = {
        longitude1: dijit.byId("longitude1").get('value'),
        latitude1: dijit.byId("latitude1").get('value'),
        longitude2: dijit.byId("longitude2").get('value'),
        latitude2: dijit.byId("latitude2").get('value')
    };

    CTService.getCoordinates(fromSRS, toSRS, coord, {
            callback: function(res){
                var coord = res.coordinate;
                dijit.byId("dstLongitude1").set('value',coord.longitude1);
                dijit.byId("dstLatitude1").set('value',coord.latitude1);
                dijit.byId("dstLongitude2").set('value',coord.longitude2);
                dijit.byId("dstLatitude2").set('value',coord.latitude2);
            },
            timeout:5000,
            exceptionHandler:function(message) {alert(message); },
            errorHandler:function(message) {alert(message); }
        }
    );
}

</script>

<body>
    <label for="fromSRS">Source Spatial Reference System:</label>
    <select dojoType="dijit.form.ComboBox" id="fromSRS" style="width:200px;">
        <option>GEO_WGS84</option>
        <option>GEO_DHDN</option>
        <option>GK2</option>
        <option>GK3</option>
        <option>GK4</option>
        <option>GK5</option>
        <option>UTM32e</option>
        <option>UTM33e</option>
        <option>UTM32s</option>
        <option>UTM33s</option>
        <option>LAMGe</option>
    </select><br/>
    <label for="toSRS">Target Spatial Reference System:</label>
    <select dojoType="dijit.form.ComboBox" id="toSRS" style="width:200px;">
        <option>GEO_WGS84</option>
        <option>GEO_DHDN</option>
        <option>GK2</option>
        <option>GK3</option>
        <option>GK4</option>
        <option>GK5</option>
        <option>UTM32e</option>
        <option>UTM33e</option>
        <option>UTM32s</option>
        <option>UTM33s</option>
        <option>LAMGe</option>
    </select><br/>
<label for="longitude1">Longitude of coordinate one:</label>
<input type="text" id="longitude1" value="10" dojoType="dijit.form.NumberTextBox" /><br/>
<label for="latitude1">Latitude of coordinate one:</label>
<input type="text" id="latitude1" value="50" dojoType="dijit.form.NumberTextBox" /><br/>
<label for="longitude2">Longitude of coordinate two:</label>
<input type="text" id="longitude2" value="11" dojoType="dijit.form.NumberTextBox" /><br/>
<label for="latitude2">Latitude of coordinate two:</label>
<input type="text" id="latitude2" value="51" dojoType="dijit.form.NumberTextBox" /><br/>

<button dojoType="dijit.form.Button" title="Transform" onClick="transform();">Transform</button>

<p>Transformed coordinate:</p>
<label for="dstLongitude1">Result Longitude one:</label>
<input type="text" id="dstLongitude1" dojoType="dijit.form.NumberTextBox"/><br/>
<label for="dstLatitude1">Result Latitude one:</label>
<input type="text" id="dstLatitude1" dojoType="dijit.form.NumberTextBox"/><br/>
<label for="dstLongitude2">Result Longitude two:</label>
<input type="text" id="dstLongitude2" dojoType="dijit.form.NumberTextBox"/><br/>
<label for="dstLatitude2">Result Latitude two:</label>
<input type="text" id="dstLatitude2" dojoType="dijit.form.NumberTextBox"/><br/>

</body>
</html>
