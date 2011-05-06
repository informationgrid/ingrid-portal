<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

</head>

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
dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.ComboBox");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");
dojo.require("ingrid.widget.ValidationTextbox");

dojo.addOnLoad(function() {});

function setValues() {
	var data = [{Id: 0, title:"Titel 1", date:new Date(), version: "Version 1"},
				{Id: 1, title:"Titel 2", date:new Date(), version: "Version 2"}];

	dojo.widget.byId("testTable").store.setData(data);
}

</script>


<body>
<!-- Filtering Table Widget -->
<div class="tableContainer rows4">
	<div class="cellEditors" id="testTableEditors">
    	<div dojoType="ingrid:ComboBox" toggle="plain" style="width:77px;" listId="3555" id="tableComboBox"></div>
        <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="tableDatePicker"></div>
        <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="tableTextbox"></div>
	</div>
    <table id="testTable" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="3" headClass="fixedHeader" tbodyClass="scrollContent rows3" cellspacing="0" class="filteringTable interactive nosort full">
	    <thead>
			<tr>
		    	<th field="title" dataType="String" width="110" editor="tableComboBox">Titel</th>
		    	<th field="date" dataType="Date" width="120" editor="tableDatePicker">Datum</th>
		    	<th field="version" dataType="String" width="445" editor="tableTextbox">Version</th>
			</tr>
		</thead>
	    <tbody>
	    </tbody>
	</table>
</div>

<button dojoType="Button" onclick="setValues">Set Values</button>
<button dojoType="Button" onclick="getValues">Get Values</button>

</body>
</html>
