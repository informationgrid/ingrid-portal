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
dojo.require("ingrid.widget.ValidationTextbox");

dojo.addOnLoad(function() {});

function toggleTableContainer() {
	dojo.html.toggleDisplay(dojo.byId("tableInputContainer"));
	dojo.html.toggleDisplay(dojo.byId("tableInputContainer"));
}

function testToggle() {
	dojo.html.toggleDisplay(dojo.byId("testInputContainer"));
	
	setTimeout("toggleTableContainer()", 1);
//	toggleTableContainer();
//	setTimeout("toggleTableContainer()", 8000);
}

</script>

<link rel="StyleSheet" href="css/main.css" type="text/css" />
<link rel="StyleSheet" href="css/recherche.css" type="text/css" />
<link rel="StyleSheet" href="css/erfassung.css" type="text/css" />

<style type="text/css">
	@import url(css/scrolling_table.css);
</style>
<!--[if IE]>
<style type="text/css">
	@import url(css/scrolling_table.ie.css);
</style>
<![endif]-->


<body>
<div id="general" class="contentBlock firstBlock">
	<div class="titleBar">
		<div class="titleIcon"><a href="javascript:testToggle('general');" title="Nur Pflichtfelder aufklappen"><img id="generalRequiredToggle" src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	</div>
	<div id="generalContent" class="content">

		<div id="testInputContainer" class="inputContainer notRequired">
			<span class="label"><label for="generalShortDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 'Kurzbezeichnung')">Kurzbezeichnung</label></span>
			<span class="input"><input type="text" id="generalShortDesc" name="generalShortDesc" class="w668" dojoType="ingrid:ValidationTextBox" /></span>
		</div>

		<div id="tableInputContainer" class="inputContainer noSpaceBelow" style="display:block;">

			<span id="generalAddressTableLabel" class="label required"><label for="generalAddressTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Adressen')">Adressen*</label></span>
			<div id="generalAddressTable" class="tableContainer headHiddenRows4 full">
				<table id="generalAddress" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
					<thead>
						<tr>
							<th nosort="true" field="nameOfRelation" dataType="String"></th>
							<th nosort="true" field="icon" dataType="String"></th>
							<th nosort="true" field="linkLabel" dataType="String">Namen</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

</body>
</html>
