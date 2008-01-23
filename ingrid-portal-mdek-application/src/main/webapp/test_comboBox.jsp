<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script src='/ingrid-portal-mdek-application/dwr/interface/EntryService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>

<script>
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("ingrid.widget.ComboBox");

dojo.addOnLoad(function() {
	var dp = dojo.widget.byId("testEditor").dataProvider;

	var def = new dojo.Deferred();

	var kw = {
		adviceType: "after",
		srcObj: dp,
		srcFunc: "setData",
		adviceObj: def,
		adviceFunc: "callback",
		once: true
	}
	
	def.addCallback(function() {
		dojo.event.kwDisconnect(kw);
		setDP();
	});

	dojo.event.kwConnect(kw);
});

function setDP() {
	EntryService.getUiListValues({
		callback: function(res) {
			var values = [];
			dojo.lang.forEach(res.ui_freeSpatialReferences, function(item) {
				values.push([item]);
			});
			var editor = dojo.widget.byId("testEditor");
			editor.dataProvider.setData(values);
//			dojo.debugShallow(values);
		}
	});
}

function getValue() {
	var tableData = dojo.widget.byId("testTable").store.getData();
	dojo.lang.forEach(tableData, function(item) {
		dojo.debug("Value: "+item.name);
	});
}
</script>


<body>

<div class="tableContainer rows5">
	<div class="cellEditors" id="testTableEditors">
		<div dojoType="ingrid:ComboBox" toggle="plain" dataUrl="js/data/spatialLocation.js" style="width:300px;" widgetId="testEditor"></div>
	</div>
	<table id="testTable" dojoType="ingrid:FilteringTable" minRows="4" headClass="fixedHeader" tbodyClass="scrollContent rows4" cellspacing="0" class="filteringTable interactive full">
		<thead>
	    	<tr>
	        	<th field="name" dataType="String" width="315" editor="testEditor">Name</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<button dojoType="Button" widgetID="setDPButton" onclick="setDP">Set DP Data</button>
<button dojoType="Button" onclick="getValue">Get Value</button>

</body>
</html>
