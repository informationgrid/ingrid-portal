<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<script type="text/javascript"> djConfig = { isDebug: true }; </script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>

<script type="text/javascript">
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.widget.Button");
</script>

<script type="text/javascript">

var dialog, contentpane;

function init(e) {
	// grab reference to widgets
	dialog = dojo.widget.byId("DialogContent");
	contentpane = dialog.children[0];

	// make dialog visible after it has parsed and inited all widgets (and other content)
	dojo.event.connect(contentpane, "onLoad", dialog, "show");

	// set up close dialog button
	var btn = document.getElementById("hider");
	dialog.setCloseControl(btn);
}
dojo.addOnLoad(init);

</script>

<style type="text/css">
body { font-family : sans-serif; }
.dojoDialog {
background : #eee;
border : 1px solid #999;
-moz-border-radius : 5px;
padding : 4px;
}

.dojoComboBox {
width: 20em;
}
</style>
</head>


<body>
<a href="javascript:contentpane.setUrl('test_button.html');">setUrl</a>

<div dojoType="dialog" id="DialogContent" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="50">
	<div dojoType="ContentPane" executeScripts="true"></div>
	<div align="right">
  		<input type="button" id="hider" value="OK" />
	</div>
</div>

</body>
</html>