<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

</head>

<style type="text/css">
	@import url(css/scrolling_table.css);
</style>
<!--[if IE]>
<style type="text/css">
	@import url(css/scrolling_table.ie.css);
</style>
<![endif]-->


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
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");
dojo.require("ingrid.widget.ValidationTextbox");

dojo.addOnLoad(function() {
	setValues();
});

function setValues() {
	var data = [{Id: 0, title:"Titel 1", name:"Name 1", version: "Version 1"},
				{Id: 1, title:"Titel 2", name:"Name 2", version: "Version 2"},
				{Id: 2, title:"Titel 3", name:"Name 3", version: "Version 3"},
				{Id: 3, title:"Titel 4", name:"Name 4", version: "Version 4"},
				{Id: 4, title:"Titel 5", name:"Name 5", version: "Version 5"}];

	dojo.widget.byId("testTable1").store.setData(data);
	dojo.widget.byId("testTable2").store.setData(data);
}

</script>


<body>
<!-- Filtering Table Widget -->
<div class="tableContainer rows4 full">
    <table id="testTable1" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
	    <thead>
			<tr>
		    	<th field="title" dataType="String">Titel</th>
		    	<th field="name" dataType="String">Datum</th>
		    	<th field="version" dataType="String">Version</th>
			</tr>
		</thead>
	    <tbody>
	    </tbody>
	</table>
</div>

<div class="tableContainer rows3 full">
    <table id="testTable2" dojoType="ingrid:FilteringTable" minRows="10" cellspacing="0" class="filteringTable nosort full">
	    <thead>
			<tr>
		    	<th field="title" dataType="String">Titel</th>
		    	<th field="name" dataType="String">Datum</th>
		    	<th field="version" dataType="String">Version</th>
			</tr>
		</thead>
	    <tbody>
	    </tbody>
	</table>
</div>

<div class="tableContainer rows2 full">
    <table id="testTable3" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort full">
	    <thead>
			<tr>
		    	<th field="title" dataType="String">Titel</th>
		    	<th field="name" dataType="String">Datum</th>
		    	<th field="version" dataType="String">Version</th>
			</tr>
		</thead>
	    <tbody>
	    </tbody>
	</table>
</div>

<div class="tableContainer rows4 full">
    <table id="testTable4" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" headClass="hidden" class="filteringTable nosort full">
	    <thead>
			<tr>
		    	<th field="title" dataType="String">Titel</th>
		    	<th field="name" dataType="String">Datum</th>
		    	<th field="version" dataType="String">Version</th>
			</tr>
		</thead>
		  <colgroup>
		    <col width="120">
		    <col width="23">
		    <col width="520">
		  </colgroup>
	    <tbody>
	    </tbody>
	</table>
</div>

<div class="tableContainer rows2 full">
    <table id="testTable5" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" headClass="hidden" class="filteringTable nosort full">
	    <thead>
			<tr>
		    	<th field="title" dataType="String">Titel</th>
		    	<th field="name" dataType="String">Datum</th>
		    	<th field="version" dataType="String">Version</th>
			</tr>
		</thead>
	    <tbody>
	    </tbody>
	</table>
</div>


</body>
</html>
