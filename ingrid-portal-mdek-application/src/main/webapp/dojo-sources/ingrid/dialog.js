/*
 * InGrid dialogs. Opened with static methods
 */

define([
    "dojo/_base/declare",
    "dojo/dom",
    "dojo/dom-style",
    "dojo/dom-construct",
    "dojo/has",
    "dojo/_base/window",
    "dojo/window",
    "dojo/Deferred",
    "dojo/on",
    "dojo/string",
    "dijit/registry",
    "dojox/widget/DialogSimple",
    "dojox/layout/FloatingPane",
    "dojo/dom-class",
    "ingrid/message",
    "dijit/form/Button",
    "dijit/form/CheckBox",
    "ingrid/utils/UDK"
], function(declare, dom, style, construct, has, wnd, win, Deferred, on, string, registry, Dialog, FloatingPane, domClass, message, Button, CheckBox, UtilUdk) {
    // different dialog types

    // different dialog actions

    return declare(null, {
        WARNING: "warning",
        INFO: "info",
        CLOSE_ACTION: "closeDlg",

        closeDialog: function(dlgId) {
            console.debug("destroy dialog: " + dlgId);
            var dlgWidget = registry.byId(dlgId);
            //dlgWidget.hide();
            setTimeout(function() {
                dlgWidget.destroyRecursive();
            }, 300);
        },

        showPage: function(caption, url, width, height, /* boolean */ modal, /* assoziative array of optional paramters, passed as ?key=value&... */ customParams) {
            // Limit max window size to the current viewport size
            var viewPort = win.getBox();
            if (viewPort.w < width) {
                width = viewPort.w - 120;
            }
            if (viewPort.h < height) {
                height = viewPort.h;
            }

            var dialogWnd = registry.byId("pageDialog");

            var dlgId = "pageDialog";
            // create the dialog for showing pages if it wasn't already created
            if (registry.byId("pageDialog") !== undefined)
                dlgId = "subPageDialog";

            dialogWnd = new Dialog({
                id: dlgId,
                title: caption,
                showTitle: true,
                draggable: true,
                //sizeDuration: 10, // no animation?
                style: "width: " + width + "px;",
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

            var self = this;
            on(dialogWnd, "Hide", function() {
                self.closeDialog(dlgId);
                /*if (dojo.isIE > 8) {
                    if (registry.byId("dataFormContainer"))
                        registry.byId("dataFormContainer").layout();
                }*/
            });

            dialogWnd.startup();
            dialogWnd.show();
            style.set(dlgId + "_underlay", "display", "block");

            // resize dialog into viewport if it's too heigh
            //var windowHeight = dojo.window.getBox().h;
            //on(dialogWnd, "onLoad", function(){
            //    if (style(dlgId, "height") > windowHeight)
            //        style(dlgId, "height", (windowHeight)+"px");
            //});

        },



        showContextHelp: function(e, guiId, caption /* optional */ ) {
            // Check if guiId is a string or int
            // If it's a string (deprecated), display it
            var errorText = string.substitute(message.get("dialog.help.helpMessageNotFound"), [guiId + ""]);
            if (caption) {
                errorText += "<br>" + message.get("dialog.help.fieldName") + ": '" + caption + "'";
            }

            // if guiId is a string then use this string as help message
            // normally used for additional fields!
            var def = null;
            if (typeof guiId == "string") {
                def = new Deferred();
                var manualMessage = {};

                // IE work around
                // If "e" is undefined use the global "event" variable
                e = e || event;
                var target = e.currentTarget || e.srcElement;

                manualMessage.name = target[dojo.isFF ? "textContent" : "innerText"];
                manualMessage.helpText = guiId;
                guiId = "-1";
                def.resolve(manualMessage);

            } else {
                // Fetch the help message from the server
                def = UtilUdk.loadHelpMessage(guiId);
            }
            var mouseX = this.getMousePos(e)[0];
            var mouseY = this.getMousePos(e)[1];

            var self = this;
            def.then(function(helpMessage) {
                if (typeof(caption) == "undefined") {
                    caption = helpMessage.name;
                }

                var dlg = registry.byId('ContextHelpDlg');
                if (dlg) {
                    // If the dialog already exists, update the caption&message and display it
                    dlg.attr("title", caption);
                    dlg.attr("style", "left: " + (mouseX + 15) + "px; top: " + (mouseY + 15) + "px; height: auto;");
                    dlg.set("content", self.createContextHelpContent(helpMessage, guiId));

                } else {
                    var contentDiv = construct.create("div", {
                        innerHTML: "<div>" + self.createContextHelpContent(helpMessage, guiId) + "</div>"
                    }, wnd.body());
                    dlg = new FloatingPane({ //ingrid.dijit.CustomDialog({
                        id: "ContextHelpDlg",
                        title: caption,
                        dockable: false,
                        resizable: true,
                        style: "position:absolute; width: 300px; height: 300px; left: " + (mouseX + 15) + "px; top: " + (mouseY + 15) + "px;"
                    }, contentDiv);
                    dlg.startup();
                }

                // do not make dialog higher than 400px initially
                var height = style.get(dlg.domNode, "height");
                if (height > 400) {
                    style.set(dlg.domNode, "height", 400 + "px");
                    height = 400;
                }
                // move dialog into viewport if it's partially outside
                var windowHeight = dojo.window.getBox().h;
                if ((mouseY + height) > windowHeight)
                    style.set(dlg.domNode, "top", (windowHeight - height - 20) + "px");
            }, function() {
                this.show(message.get('general.hint'), errorText, this.INFO);
            });
        },


        createContextHelpContent: function(helpMessage, guiId) {
            var text = "<div style='padding:10px;'>" + helpMessage.helpText;

            if (helpMessage.sample && helpMessage.sample.length !== 0)
                text += "<br/><br/><strong>" + message.get('general.example') + ":</strong><br/>" + helpMessage.sample;

            if (guiId && guiId != -1)
                text += "<br/><br/>" + message.get('general.fieldId') + ": " + guiId;

            text += "</div>";
            return text;
        },


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
        show: function(caption, text, /* dialog.WARNING|dialog.INFO */ type, /* array of objects with key, action properties */ btnActions, /* optional */ width, /* optional */ height, /* optional */ errorstack) {
            // define params
            if (!width) width = 310;
            if (!height) height = 155;

            if (registry.byId("InfoDialog")) {
                registry.byId("InfoDialog").hide();
                registry.byId("InfoDialog").destroyRecursive();
            }

            /*dialogWnd = new dojox.widget.Dialog({
                    id:    "InfoDialog",
                    title: caption,
                    showTitle: true,
                    modal: true,
                    dimensions: [width, height]
                    //style: "width: "+width+"px;"// height: "+height+"px;"
                });*/
            var dialogWnd = new Dialog({
                id: "InfoDialog",
                title: caption,
                showTitle: true,
                modal: true,
                refocus: false,
                //dimensions: [width, height]
                style: "width: " + width + "px;" // height: "+height+"px;"
            });

            domClass.add(dialogWnd.titleBar, type);

            var contentHeight = height < 250 ? "auto" : (height - 150);

            var content = '<div class="dijitDialogPaneContentArea popupContent" style="overflow:auto; height: ' + (contentHeight) + 'px;" >';
            if (type == this.INFO)
                text = '<strong>' + text + '</strong>';
            //content += '<div class="popupContent" style="word-wrap: break-word; height: ' + (contentHeight) + 'px;" >' + text + '</div>';
            content += text;
            if (errorstack) {
                content += "<pre>" + errorstack + "</pre>";
            }
            content += '</div>';
            content += '<div id="dialogButtonBar" class="dijitDialogPaneActionBar"></div>';

            dialogWnd.set("content", content);

            // add buttons
            var button = null;
            if (btnActions) {
                var closeAction = function() {
                    registry.byId("InfoDialog").hide();
                };

                for (var i = 0; i < btnActions.length; i++) {
                    if (btnActions[i].type == "checkbox") {
                        button = new CheckBox({
                            id: "chkbox_hideCopyHint",
                            style: "float:left;"
                        }).placeAt(dom.byId("dialogButtonBar"));
                        construct.create("label", {
                            "for": "chkbox_hideCopyHint",
                            "innerHTML": btnActions[i].caption,
                            "style": "float:left;",
                            "class": "inActive"
                        }, dom.byId("dialogButtonBar"));
                    } else {
                        button = new Button({
                            label: btnActions[i].caption
                        }).placeAt(dom.byId("dialogButtonBar"));
                    }

                    if (btnActions[i].action != this.CLOSE_ACTION) {
                        // call close action and given function afterwards (this order prevents the glasspane being closed 
                        // for following modal dialogs)
                        // we get the action through the event target, which shild have the same id as the button's widget id
                        if (btnActions[i].type == "checkbox")
                            button.onChange = btnActions[i].action;
                        else {
                            button.onClick = btnActions[i].action;
                            button.type = "submit";
                        }

                    } else
                        button.onClick = closeAction;
                }
            } else {
                // add ok button, if no button is given
                button = new Button({
                    label: message.get('general.ok'),
                    onClick: function() {
                        registry.byId("InfoDialog").hide();
                        //registry.byId(dlgId).destroyRecursive();
                        //closeDialog(dlgId);
                    }
                }).placeAt(dom.byId("dialogButtonBar"));
            }
            dialogWnd.startup();
            var promise = dialogWnd.show();
            dialogWnd.promise = promise;
            button.focus();
            return dialogWnd;
        },

        showInfoMessage: function(msg, divId) {
            var div = dom.byId(divId);
            div.innerHTML = msg;
            div.style.visibility = "visible";
            setTimeout(function() {
                div.style.visibility = "hidden";
            }, 3000);

        },


        /*
         * Position helpers
         */
        getMousePos: function(e) {
            var posX = 0;
            var posY = 0;
            if (!e) e = window.event;
            if (e.pageX || e.pageY) {
                posX = e.pageX;
                posY = e.pageY;
            } else if (e.clientX || e.clientY) {
                posX = e.clientX + document.body.scrollLeft;
                posY = e.clientY + document.body.scrollTop;
            }
            return [posX, posY];
        }
    })();
});