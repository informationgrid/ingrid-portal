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
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");

dojo.addOnLoad(function()
{
});

function setValue() {
	var table = dojo.widget.byId("testTable");
	var store = dojo.widget.byId("testTable").store;
	store.setData([{Id: 0, identifier: "ident 1", name: "Test Entry 1", hiddenValue: "show"},
				   {Id: 1, identifier: "ident 2", name: "Test Entry 2", hiddenValue: "show"},
				   {Id: 2, identifier: "ident 3", name: "Test Entry 3", hiddenValue: "hide"},
				   {Id: 3, identifier: "ident 4", name: "Test Entry 4"}]);

	table.applyFilters();	
	table.render();
};

function setFirstFilterFunc() {
	var table = dojo.widget.byId("testTable");
	var filterFunc = function(value) {
		if (value == "Test Entry 2")
			return false;
		else
			return true;
	};

	table.setFilter("name", filterFunc);
	table.render();
};

function setSecondFilterFunc() {
	var table = dojo.widget.byId("testTable");
	var filterFunc = function(value) {
		if (value == "hide")
			return false;
		else
			return true;
	};

	table.setFilter("hiddenValue", filterFunc);
	table.render();
};

function clearFirstFilterFunc() {
	var table = dojo.widget.byId("testTable");
	table.clearFilter("name");
}

function clearSecondFilterFunc() {
	var table = dojo.widget.byId("testTable");
	table.clearFilter("hiddenValue");
}

function clearFiltersFunc() {
	var table = dojo.widget.byId("testTable");
	table.clearFilters();
};
</script>


<body>
<!-- Filtering Table Widget -->
<div class="inputContainer">
<table id="testTable" dojoType="ingrid:FilteringTable" minRows="4" headClass="fixedHeader" tbodyClass="scrollContent rows4" cellspacing="0" class="filteringTable interactive thirdInside2">
	<thead>
		<tr>
			<th field="identifier" dataType="String" width="120">Identifier</th>
 			<th field="name" dataType="String" width="200">Name</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
</div>

<button dojoType="Button" widgetID="setValueButton" onclick="setValue">Set Value</button>
<button dojoType="Button" widgetID="firstFilterButton" onclick="setFirstFilterFunc">Set name Filter Func</button>
<button dojoType="Button" widgetID="secondFilterButton" onclick="setSecondFilterFunc">Set hiddenValue Filter Func</button>
<button dojoType="Button" widgetID="clearFirstFilterButton" onclick="clearFirstFilterFunc">Clear name Filter Func</button>
<button dojoType="Button" widgetID="clearSecondFilterButton" onclick="clearSecondFilterFunc">Clear hiddenValue Filter Func</button>
<button dojoType="Button" widgetID="clearFilterButton" onclick="clearFiltersFunc">Clear all Filter Funcs</button>

</body>
</html>
