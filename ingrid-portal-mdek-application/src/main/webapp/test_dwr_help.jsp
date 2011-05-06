<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script src='/ingrid-portal-mdek-application/dwr/interface/HelpService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>

<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>


<script type="text/javascript">
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");
</script>

<script type="text/javascript">
scriptScope = this;

scriptScope.getAllHelpEntries = function() {

	HelpService.getAllHelpEntries( {
		callback: function(data) {
			dojo.widget.byId("helpTable").store.setData(data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}


scriptScope.getHelpEntry = function() {

	HelpService.getHelpEntry( 3515, 1, {
		callback: function(data) {
			dojo.widget.byId("helpTable").store.setData([data]);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

</script>
</head>

<body>
	<h1>Help Test</h1>
	<button dojoType="Button" onclick="javascript:scriptScope.getHelpEntry();">Get sample HelpEntry</button>
	<button dojoType="Button" onclick="javascript:scriptScope.getAllHelpEntries();">Get all HelpEntries</button>

	<table id="helpTable" valueField="id" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y %H:%m:%S" minRows="4" cellspacing="0" class="filteringTable interactive readonly">
		<thead>
			<tr>
				<th field="id" dataType="String" width="10">Identifier</th>
 				<th field="version" dataType="String" width="100">Version</th>
 				<th field="guiId" dataType="String" width="100">guiId</th>
 				<th field="entityClass" dataType="String" width="100">Klasse</th>
 				<th field="language" dataType="String" width="100">Sprache</th>
 				<th field="name" dataType="String" width="300">Name</th>
 				<th field="helpText" dataType="String" width="300">Hilfetext</th>
 				<th field="sample" dataType="String" width="100">Beispiel</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</body>
</html>
