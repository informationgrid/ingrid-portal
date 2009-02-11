/*
 * InGrid dialogs. Opened with static methods
 */

var dialog={};

// different dialog types
dialog.WARNING = "warning";
dialog.INFO = "info";

// different dialog actions
dialog.CLOSE_ACTION = "closeDlg";

/*
 * Show the context help
 */
dialog.showPage = function(caption, url, width, height, /* boolean */modal, /* assoziative array of optional paramters, passed as ?key=value&... */customParams)
{
  dojo.require("ingrid.widget.FloatingPane");

  // Limit max window size to the current viewport size
  var viewPort = dojo.html.getViewport();
  if (viewPort.width < width) { width = viewPort.width; }
  if (viewPort.height < height) { height = viewPort.height; }

  // define params
  var params = {};
  params['displayCloseAction'] = true;
  params['constrainToContainer'] = true;
  params['resizable'] = false;
  params['executeScripts'] = true;
  params['href'] = url;
  params['title'] = caption;
  params['width'] = width + "px";
  params['height'] = height + "px";
  params['customParams'] = customParams;

  // make the widget
  var div = document.createElement("div");
  div.style.position = "absolute";
  document.body.appendChild(div);
  div.style['width'] = width + "px";
  div.style['height'] = height + "px";
  div.style['left'] = getCenterX(width) + "px";
  div.style['top'] = getCenterY(height) + "px";
  var widget = dojo.widget.createWidget("ingrid:FloatingPane", params, div);

  if (modal)
  {
    // make the dialog modal by adding a glasspane
    showGlassPane(widget);
  }
}


/*
 * Show the context help
 */
dialog.showContextHelp = function(e, guiId, caption /* optional */) {
	// Check if guiId is a string or int
	// If it's a string (deprecated), display it
	var errorText = dojo.string.substituteParams(message.get("dialog.help.helpMessageNotFound"), guiId+"");
	if (caption) {
		errorText += "<br>"+ message.get("dialog.help.fieldName") +": '"+caption+"'";
	}

	if (typeof guiId == "string") {
		dialog.show(message.get('general.hint'), errorText, dialog.INFO);
		return;
	}

	// Fetch the help message from the server
	var def = UtilUdk.loadHelpMessage(guiId);

	var mouseX = getMousePos(e)[0];
	var mouseY = getMousePos(e)[1];

	def.addErrback(function(err){
		dialog.show(message.get('general.hint'), errorText, dialog.INFO);
	});

	def.addCallback(function(helpMessage) {
		dojo.require("ingrid.widget.FloatingPane");
		  
		if (typeof(caption) == "undefined") {
			caption = helpMessage.name;
		}

		var dlg = dojo.widget.byId('ContextHelpDlg');
		if (dlg) {
			// If the dialog already exists, update the caption&message and display it
			dlg.titleBarText.innerHTML = caption;
			dlg.containerNode.innerHTML = '<div class="popupContent" dojoAttachPoint="contentNode">'+helpMessage.helpText+'<br/><br/>Beispiel:<br/>'+helpMessage.sample+'<br/><br/>Feld ID: '+guiId+'</div>';
			dlg.domNode.style['left'] = mouseX + 30 + "px";
			dlg.domNode.style['top'] = mouseY + "px";
		
		} else {
			// define params
			var params = {};
			params['displayCloseAction'] = true;
			params['constrainToContainer'] = true;
			params['title'] = caption;
			params['widgetId'] = 'ContextHelpDlg';
			  
			    // define inner div
			var div = document.createElement("div");
			div.style.position = "absolute";
			div.innerHTML = '<div class="popupContent" dojoAttachPoint="contentNode">'+helpMessage.helpText+'<br/><br/>Beispiel:<br/>'+helpMessage.sample+'<br/><br/>Feld ID: '+guiId+'</div>';
			document.body.appendChild(div);
			div.style['width'] = "300px";
			div.style['height'] = "200px";
			div.style['left'] = mouseX + 30 + "px";
			div.style['top'] = mouseY + "px";
	    
			// make the widget
			var widget = dojo.widget.createWidget("ingrid:FloatingPane", params, div);
		}
	});
}

/*
 * Show a dialog
 * Example: dialog.show("TESTDIALOG", "TEST", 
 *                      dialog.INFO, [
 *                        {caption:"OK",action:function(){alert("OK")}},
 *                        {caption:"Cancel",action:dialog.CLOSE_ACTION}
 *                      ]
 *                     );
 *
 * dialog.show("TESTDIALOG", "TEST", dialog.INFO); is equivalent to dialog.show("TEST", dialog.INFO, [{caption:"OK",action:dialog.CLOSE_ACTION}]);

 */
dialog.show = function(caption, text, /* dialog.WARNING|dialog.INFO */type, /* array of objects with key, action properties */btnActions, /* optional */width, /* optional */height)
{
  dojo.require("ingrid.widget.FloatingPane");
  var dlgId = dialog.getNextId();

  // define params
  if (!width) width = 254;
  if (!height) height = 170;

  // Limit max window size to the current viewport size
  var viewPort = dojo.html.getViewport();
  if (viewPort.width < width) { width = viewPort.width; }
  if (viewPort.height < height) { height = viewPort.height; }

  var params = {};
  params['displayCloseAction'] = true;
  params['constrainToContainer'] = true;
  params['title'] = caption;
  params['hasShadow'] = true;
  params['resizable'] = false;
  params['widgetId'] = dlgId;

  // define inner div
  var div = document.createElement("div");
  div.style.position = "absolute";
  var content = '';
  if (type == dialog.WARNING)
    content += "<div class='popupWarning'>"+ message.get("dialog.general.warning") +"!</div>";
  if (type == dialog.INFO)
    text = '<strong>' + text + '</strong>';
  content += '<div class="popupContent xNoScroll yScroll" style="height:'+(height-85)+'px;">' + text + '<span class="buttons" id="'+dlgId+'_buttons"></span></div>';
  div.innerHTML = content;
  document.body.appendChild(div);
  div.style['width'] = width + "px";
  div.style['height'] = height + "px";
  div.style['left'] = getCenterX(width) + "px";
  div.style['top'] = getCenterY(height) + "px";

  // add buttons
  if (btnActions)
  {
    for(i=0;i<btnActions.length;i++)
    {
      var button = dojo.widget.createWidget("ingrid:Button", {caption:btnActions[i].caption});
  
      var closeAction = function() {dojo.widget.byId(dlgId).closeWindow()};
      if (btnActions[i].action != dialog.CLOSE_ACTION)
      {
        // call close action and given function afterwards (this order prevents the glasspane being closed 
        // for following modal dialogs)
        // we get the action through the event target, which shild have the same id as the button's widget id
        button.action = btnActions[i].action;
        button.onClick = function(e) {
        	dojo.lang.delayThese([closeAction, this.action]);
        };
      }
      else
        button.onClick = closeAction;
  
      // add button
//      dojo.byId(dlgId+'_buttons').appendChild(button.domNode);

      var buttonSpan = document.createElement("span");
      if (dojo.render.html.ie)				
      	buttonSpan.style['styleFloat'] = 'right'; // ie
      else
        buttonSpan.style['cssFloat'] = 'right';   // firefox

      buttonSpan.appendChild(button.domNode);
      dojo.byId(dlgId+'_buttons').appendChild(buttonSpan);
    }
  }
  else
  {
    // add ok button, if no button is given
    var button = dojo.widget.createWidget("ingrid:Button", {caption:message.get('general.ok')});
    button.onClick = function() {dojo.widget.byId(dlgId).closeWindow()};
    dojo.byId(dlgId+'_buttons').appendChild(button.domNode);
  }

  // make the widget
  var widget = dojo.widget.createWidget("ingrid:FloatingPane", params, div);

  // make the dialog modal by adding a glasspane
  showGlassPane(widget);

  // Focus the last button that is displayed (key 'enter' triggers the default action)
  button.domNode.focus();
}

/*
 * Find a dialog (finds the enclosing FloatingPane)
 */
dialog.find = function(/* html element */ node)
{
  var parent = node;
  while (parent) {
    if (dojo.html.hasClass(parent, 'dojoFloatingPane')) {
      var dlg = dojo.widget.byId(parent.id);
      return dlg;
    }
    parent = parent.parentNode;
  }
  return null;
}

/*
 * Close a dialog (finds the enclosing FloatingPane and closes it)
 */
dialog.close = function(/* html element */ node)
{
  var dlg = dialog.find(node);
  if (dlg)
    dlg.closeWindow();
}

/*
 * Id generator
 */
dialog.INDEX = 0;
dialog.getNextId = function()
{
  return 'dialog_'+(dialog.INDEX++);
}


/*
 * Position helpers
 */
function getMousePos(e)
{
  var posX = 0;
  var posY = 0;
  if (!e) var e = window.event;
  if (e.pageX || e.pageY)
  {
    posX = e.pageX;
    posY = e.pageY;
  }
  else if (e.clientX || e.clientY)
  {
    posX = e.clientX + document.body.scrollLeft;
    posY = e.clientY + document.body.scrollTop;
  }
  return[posX, posY];
}

function getCenterX(width)
{
  var x = (dojo.html.getViewport().width - width)/2;
  return x;
}

function getCenterY(height)
{
  var y = (dojo.html.getViewport().height - height)/2;
  return y;
}

/*
 * Modal dialog
 */
dialog.GLASSPANE = null;
function showGlassPane(/* widget with closWindo event */widget)
{
  if (dialog.GLASSPANE == null)
  {
    var glassPane = document.createElement("div");
    glassPane.style.position = "absolute";
    document.body.appendChild(glassPane);
    glassPane.style.backgroundColor = '#add9f9';
    glassPane.style.width = "3000px";
    glassPane.style.height = "2000px";
    glassPane.style.left = 0;
    glassPane.style.top = 0;
    glassPane.style.zIndex = 9;
    dojo.html.setOpacity(glassPane, 0.5);
    dialog.GLASSPANE = glassPane;

    // focus the pane
    glassPane.focus();
  }
  if (dialog.GLASSPANE.style.display != "block") {
  	dojo.event.connect(widget, "closeWindow", "hideGlassPane");
  	dialog.GLASSPANE.style.display = "block";
  }
}

function hideGlassPane()
{
  if (dialog.GLASSPANE != null)
  {
    dialog.GLASSPANE.style.display = "none";
  }
}

dialog.isGlassPaneVisible = function() {
	return (dialog.GLASSPANE != null && dialog.GLASSPANE.style.display == "block");
}