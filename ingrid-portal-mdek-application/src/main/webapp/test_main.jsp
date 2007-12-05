<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script
	src='/ingrid-portal-mdek-application/dwr/interface/EntryService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>

<!-- <script type="text/javascript" src="js/objectload.js"></script>  -->


<script>
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.TreeSelectorV3");
dojo.require("dojo.widget.Textbox");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.Form");
dojo.require("ingrid.widget.Tree");
dojo.require("ingrid.widget.TreeNode");
dojo.require("ingrid.widget.TreeContextMenu");
dojo.require("ingrid.widget.TreeController");
dojo.require("ingrid.widget.TreeDocIcons");
dojo.require("ingrid.widget.TreeListener");
dojo.require("ingrid.widget.TreeDecorator");	
dojo.require("ingrid.widget.TreeExpandOnSelect");	

dojo.addOnLoad(function()
{
//  initEventTest();
  initTree();
  initTest();
});


function initTree() {
  var contextMenu1 = dojo.widget.byId('contextMenu1');
  contextMenu1.treeController = dojo.widget.byId('treeController');
  contextMenu1.addItem('new', 'addChild', function(menuItem) {createItemClicked(menuItem)});
  contextMenu1.addItem('preview', 'open', 'previewItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem('cut', 'cut', 'cutItemClicked');
  contextMenu1.addItem('copy', 'copy', 'copySingleItemClicked');
  contextMenu1.addItem('paste', 'copy', 'copyItemClicked');
  contextMenu1.addItem('paste as node', 'paste', 'pasteItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem('mark deleted', 'detach', 'markDeletedItemClicked');
//  contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', 'deleteItemClicked');

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem('new', 'addChild', function(menuItem) {createItemClicked(menuItem)});

  // attach node selection handler
  var treeListener = dojo.widget.byId('treeListener');
  dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelected");
  
  // Load children of the node from server
  // Overwritten to work with dwr.
  var treeController = dojo.widget.byId('treeController');
  treeController.loadRemote = function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();
		
		EntryService.getSubTree(node.id, node.nodeAppType, 1, {
  			callback:function(res) { deferred.callback(res); },
			timeout:5000,
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});

		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { dojo.debug("Callback Error"); alert(res.message); });
		return deferred;
	};
}

//function initEventTest()
//{
//  dojo.event.connect('around', dojo.event, 'connect', function(invocation) {
//      dojo.debug("Connecting event:");
//      for (i in invocation.args)
//      {
//		var ao = invocation.args[i];
//		if(dojo.lang.isString(ao.srcFunc))
//		  dojo.debug("  srcFunc: "+ao.srcFunc);
//		if(dojo.lang.isString(ao.adviceFunc))
//		  dojo.debug("  adviceFunc: "+ao.adviceFunc);
//		if(dojo.lang.isString(ao.aroundFunc))
//		  dojo.debug("  aroundFunc: "+ao.aroundFunc);
//      }
//      var result = invocation.proceed();
//      return result;
//    }
//  );
//}


function initTest() {
// Test der Dojo Textbox. Es wird ein Beispieltext in die textbox geschrieben
  var myObj = {textBox: "Some text from Dojo"};
  dojo.widget.byId('myForm').setValues(myObj);
//  myObj=dojo.widget.byId('myForm').getValues();

// Test eines Dojo Buttons.
//  var myButton = dojo.widget.byId('myButton');
//  dojo.event.connect(myButton, 'onClick', function(){
//	alert("Button Clicked");
//  });

// Auflisten der treeSelector Events
// var selector = dojo.widget.getWidgetById('treeSelector');
//  for(eventName in selector.eventNames) { // selector.eventNames = {treeSelector/select, treeSelector/deselect, treeSelector/dblselect}
//    dojo.event.topic.subscribe(
//      selector.eventNames[eventName],
//      new reporter('selector'),
//      'go'
//    );
//  }

// Auflisten der tree Events
// dojo.debug("--- Tree Events BEGIN ---");
// var tree = dojo.widget.getWidgetById('tree');
//  for(eventName in tree.eventNames) {
//    dojo.debug(tree.eventNames[eventName]);
//  }
// dojo.debug("--- Tree Events END ---");

// ----
// Es gibt mehrere Moeglichkeiten die Tree Events zu empfangen:
// Entweder ueber einen treeSelector oder einen treeListener
// Die treeListener Methode sieht 'sauberer' aus. Wozu benoetigt
// man den selector?  
// ----
  var treeListener = dojo.widget.byId('treeListener');
  dojo.event.topic.subscribe(treeListener.eventNames.select, "listenerTest");
// -- Connect calls a second function if the first function is called.
//  dojo.event.connect("listenerTest", "anotherFunc");


//  var selector = dojo.widget.getWidgetById('treeSelector');
//  dojo.event.topic.subscribe('treeSelector/select', new testFunc(), 'doStuff');
}

function listenerTest(message)
{
//  dojo.debug("Node AppType: "+ message.node.nodeAppType);
//  dojo.debug("Node DocType: "+ message.node.nodeDocType);
//  dojo.debug("  Node Title: "+ message.node.title);
//  dojo.debug("     Node ID: "+ message.node.id);

  // Hier werden Daten über den ausgewählten Baumknoten vom 'EntryService' angefordert.
  EntryService.getNodeData(message.node.id, message.node.nodeAppType, 'false',
    {
      callback:readNodeData,	// Callback Funktion die aufgerufen wird sobald die Daten geholt wurden
      timeout:5000,
      errorHandler:function(message) {alert("Timeout while waiting for nodeData: " + message); }
    }
  );
}

// Diese callback Funktion wird aufgerufen wenn ein neuer Knoten im Baum ausgewählt wurde  
function readNodeData(nodeData)
{
  dojo.debug("Node Data received (from callback): [ID="+nodeData.id +", Type="+nodeData.nodeAppType+", title="+nodeData.title+"]");

  var myObj = {textBox: nodeData.id};
  dojo.widget.byId('myForm').setValues(myObj);
}

var testFunc = function() {
  this.doStuff = function(message) {
    dojo.debug("Node selected: "+message.node);
    myObj = {textBox: message.node};
    dojo.widget.byId('myForm').setValues(myObj);
  }
}

//var reporter = function(reporter) {
//  this.name = eventName;
//  this.go = function(message) {
//    var rep = [ reporter + " -- event: "+this.name ];
//    for(i in message) rep.push(i+": "+message[i]);
//    dojo.debug(rep.join(', '));
//  }
//}


// initially load data (first hierachy level) from server 
EntryService.getSubTree(null, null, 1, 
	function (str) {
		var tree = dojo.widget.byId('tree');
		tree.setChildren(str);
	});

</script>


<body>

<!-- tree components -->
<div dojoType="TreeSelectorV3" widgetId="treeSelector"></div>

<div dojoType="ingrid:TreeController" widgetId="treeController"
	RpcUrl="server/treelistener.php"></div>
<div dojoType="ingrid:TreeListener" widgetId="treeListener"></div>
<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIcons"></div>
<div dojoType="ingrid:TreeDecorator" listener="treeListener"></div>

<!-- context menus -->
<div dojoType="ingrid:TreeContextMenu" toggle="plain"
	contextMenuForWindow="false" widgetId="contextMenu1"></div>
<div dojoType="ingrid:TreeContextMenu" toggle="plain"
	contextMenuForWindow="false" widgetId="contextMenu2"></div>

<!-- tree -->
<div dojoType="ingrid:Tree"
	listeners="treeSelector;treeController;treeListener;contextMenu1;contextMenu2;treeDocIcons"
	widgetId="tree"></div>

<!-- input Form -->
<form dojoType="Form" id="myForm">
  Node ID:<input dojoType="Textbox" name="textBox" value="Text Box" widgetId="textBox"/>
</form>

<!-- <button dojoType="Button" widgetID="myButton">Click me!</button>  -->
</body>
</html>
