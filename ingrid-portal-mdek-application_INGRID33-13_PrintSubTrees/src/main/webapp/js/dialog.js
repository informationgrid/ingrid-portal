/*
 * InGrid dialogs. Opened with static methods
 */

dojo.require("ingrid.dijit.CustomDialog");
dojo.require("dojox.layout.FloatingPane");
//dojo.require("dojox.widget.Dialog");
//dojo.require("dijit.Dialog");

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
	// Limit max window size to the current viewport size
	var viewPort = dijit.getViewport();
	if (viewPort.w < width) { width = viewPort.w; }
	if (viewPort.h < height) { height = viewPort.h; }

	var dialogWnd = dijit.byId("pageDialog");

	var dlgId = "pageDialog";
	// create the dialog for showing pages if it wasn't already created
	if (dijit.byId("pageDialog") != null)
		dlgId = "subPageDialog";
		
    dialogWnd = new ingrid.dijit.CustomDialog({
        id: dlgId,
        title: caption,
        showTitle: true,
        draggable: true,
        //sizeDuration: 10, // no animation?
        style: "width: " + width + "px;",// height: " + height + "px;",
        //dimensions: [width, height],
        //viewportPadding: 50,
        href: url,
        modal: true,
        refocus: false,
        scriptHasHooks: true,
        //sizeToViewport: false,
        executeScripts: true,
        customParams: customParams
    });
    
	dojo.connect(dialogWnd, "onHide", function(){
		closeDialog(dlgId);
		if (dojo.isIE > 8) {
		    if (dijit.byId("dataFormContainer"))
		        dijit.byId("dataFormContainer").layout();
		}
	});

	dialogWnd.startup();
	dialogWnd.show();
    dojo.style(dlgId+"_underlay", "display", "block");
    
    // resize dialog into viewport if it's too heigh
    //var windowHeight = dojo.window.getBox().h;
    //dojo.connect(dialogWnd, "onLoad", function(){
    //    if (dojo.style(dlgId, "height") > windowHeight)
    //        dojo.style(dlgId, "height", (windowHeight)+"px");
    //});
    
    // disconnect the onScroll-event, which allows scrolling on dialogs if higher than viewport
    dojo.disconnect(dijit.byId(dlgId)._modalconnects[0]);
}


/*
 * Show the context help
 */
dialog.showContextHelp = function(e, guiId, caption /* optional */) {
	// Check if guiId is a string or int
	// If it's a string (deprecated), display it
	var errorText = dojo.string.substitute(message.get("dialog.help.helpMessageNotFound"), [guiId+""]);
	if (caption) {
		errorText += "<br>"+ message.get("dialog.help.fieldName") +": '"+caption+"'";
	}

    // if guiId is a string then use this string as help message
    // normally used for additional fields!
	if (typeof guiId == "string") {
        var def = new dojo.Deferred();
        var manualMessage      = {};

        // IE work around
        // If "e" is undefined use the global "event" variable
        e = e || event;
        var target = e.currentTarget || e.srcElement;
    
        manualMessage.name     = target[dojo.isFF ? "textContent" : "innerText"];
        manualMessage.helpText = guiId;
        guiId                  = "-1";
        def.callback(manualMessage);
    } else {
    
        // Fetch the help message from the server
        var def = UtilUdk.loadHelpMessage(guiId);
    }
	var mouseX = getMousePos(e)[0];
	var mouseY = getMousePos(e)[1];
    
	def.addErrback(function(err){
		dialog.show(message.get('general.hint'), errorText, dialog.INFO);
	});

	def.addCallback(function(helpMessage) {
		if (typeof(caption) == "undefined") {
			caption = helpMessage.name;
		}

		var dlg = dijit.byId('ContextHelpDlg');
		if (dlg) {
			// If the dialog already exists, update the caption&message and display it
			dlg.attr("title", caption);
			dlg.attr("style", "left: "+(mouseX + 15)+"px; top: "+(mouseY + 15)+"px; height: auto;");
            dlg.set("content", _createContextHelpContent(helpMessage, guiId));
		
		} else {
            var contentDiv = dojo.create("div", {innerHTML: "<div>"+_createContextHelpContent(helpMessage, guiId)+"</div>"}, dojo.body());
            dlg = new dojox.layout.FloatingPane({ //ingrid.dijit.CustomDialog({
                id:    "ContextHelpDlg",
                title: caption,
                dockable: false,
                resizable: true,
                style: "position:absolute; width: 300px; height: 300px; left: "+(mouseX + 15)+"px; top: "+(mouseY + 15)+"px;"
            }, contentDiv);
            dlg.startup();
		}

        // do not make dialog higher than 400px initially
        var height = dojo.style(dlg.domNode, "height");
        if (height > 400) {
            dojo.style(dlg.domNode, "height", 400+"px");
            height = 400;
        }
        // move dialog into viewport if it's partially outside
        var windowHeight = dojo.window.getBox().h;
        if ((mouseY + height) > windowHeight)
            dojo.style(dlg.domNode, "top", (windowHeight-height-20)+"px");
	});
}

function _createContextHelpContent(helpMessage, guiId) {
    var text = "<div style='padding:10px;'>" + helpMessage.helpText; 
    
    if (helpMessage.sample && helpMessage.sample.length != 0)
        text += "<br/><br/><strong>Beispiel:</strong><br/>"+helpMessage.sample;
        
    if (guiId && guiId != -1)
        text += "<br/><br/>Feld ID: "+guiId
       
    text += "</div>";
    return text;
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
	// define params
	if (!width) width = 310;
	if (!height) height = 155;
	
	if (dijit.byId("InfoDialog")) {
	    dijit.byId("InfoDialog").hide();
		dijit.byId("InfoDialog").destroyRecursive();
	}
	
	/*dialogWnd = new dojox.widget.Dialog({
		id:    "InfoDialog",
        title: caption,
		showTitle: true,
		modal: true,
		dimensions: [width, height]
        //style: "width: "+width+"px;"// height: "+height+"px;"
    });*/
	dialogWnd = new ingrid.dijit.CustomDialog({
        id:    "InfoDialog",
        title: caption,
        showTitle: true,
        modal: true,
        refocus: false,
        //dimensions: [width, height]
        style: "width: "+width+"px;"// height: "+height+"px;"
    });
	
	dojo.addClass(dialogWnd.titleBar, type);
    
	var content = '<div class="dijitDialogPaneContentArea">';
	if (type == dialog.INFO)
		text = '<strong>' + text + '</strong>';
	content += '<div class="popupContent xNoScroll yScroll" >' + text + '<span class="buttons" id="InfoDialog_buttons"></span></div>'; // style="height:'+height+'px;"
	content += '</div>';
	content += '<div id="dialogButtonBar" class="dijitDialogPaneActionBar"></div>';
	
	dialogWnd.set("content", content); 	
	
  // add buttons
  if (btnActions) {
    for(i=0;i<btnActions.length;i++)
    {
		var button = new dijit.form.Button({label:btnActions[i].caption}).placeAt(dojo.byId("dialogButtonBar"));//buttonSpan.domNode);//, dlgId+'_buttons');
		
		var closeAction = function() {dijit.byId("InfoDialog").hide()};
		if (btnActions[i].action != dialog.CLOSE_ACTION) {
			// call close action and given function afterwards (this order prevents the glasspane being closed 
			// for following modal dialogs)
			// we get the action through the event target, which shild have the same id as the button's widget id
			button.onClick = btnActions[i].action;
			button.type = "submit";
		} else 
	       button.onClick = closeAction;
    }
  } else {
    // add ok button, if no button is given
    var button = new dijit.form.Button({
		label: message.get('general.ok'),
		onClick: function(){
			dijit.byId("InfoDialog").hide();
			//dijit.byId(dlgId).destroyRecursive();
			//closeDialog(dlgId);
		}
	}).placeAt(dojo.byId("dialogButtonBar"));
  }
  
	dialogWnd.startup();
	dialogWnd.show();  
    button.focus();
    return dialogWnd;
}

function showInfoMessage(msg, divId) {
    var div = dojo.byId(divId);
    div.innerHTML = msg;
    div.style.visibility = "visible";
    setTimeout(function() {div.style.visibility = "hidden";}, 3000);
    
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

function closeDialog(dlgId) {
	console.debug("destroy dialog: " + dlgId);
	var dlgWidget = dijit.byId(dlgId);
	//dlgWidget.hide();
	setTimeout(function() {dlgWidget.destroyRecursive();}, 300);
}
