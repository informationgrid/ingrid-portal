<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
	<title><fmt:message key="dialog.popup.form.fields.link" /></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="author" content="wemove digital solutions" />
    <meta name="copyright" content="wemove digital solutions GmbH" />
    
    <script src="js/jsLint/fullwebjslint.js"></script>
    
<script type="text/javascript">
	dojo.require("dijit.form.Select");
	dojo.require("dijit.form.SimpleTextarea");
    dojo.require("dijit.form.NumberTextBox");
    dojo.require("dijit.form.CheckBox");
    dojo.require("dojox.highlight");
    dojo.require("dojox.highlight.languages.javascript");
	
scopeAdminFormFields = _container_;

_container_.onLoadDeferred.addCallback(init);

function init() {
    var url = userLocale == "de" ? "js/data/additionalFormTypes_de.json" : "js/data/additionalFormTypes.json";
    scopeAdminFormFields.formTypeStore = new dojo.data.ItemFileReadStore({url: url});
    scopeAdminFormFields.createToolBar();
	scopeAdminFormFields.reload(true);
}

scopeAdminFormFields.createToolBar = function() {
    var toolbar = new dijit.Toolbar({}, "formFieldsToolBar");
    toolbar.setAttribute("class", "form_fields_top");

    var button = new dijit.form.Button({label:"<fmt:message key='dialog.admin.additionalfields.btnNewRubric' />", onClick: scopeAdminFormFields.addRubric});
    button.setAttribute("style", "float:right;");
    toolbar.addChild(button);
    button = new dijit.form.Button({id: "btnSaveProfileTop", label:"<fmt:message key='dialog.admin.additionalfields.btnSave' />", onClick: scopeAdminFormFields.saveProfile});
    button.setAttribute("style", "float:right;");
    toolbar.addChild(button);
    button = new dijit.form.Button({label:"<fmt:message key='dialog.admin.additionalfields.btnReload' />", onClick: dojo.partial(scopeAdminFormFields.reload,true)});
    button.setAttribute("style", "float:right;");
    toolbar.addChild(button);
    
}

scopeAdminFormFields.checkBoxLegacyVisibility = function() {
    var b = dijit.byId("checkBoxLegacyFieldsButton");
    if (!b.get("checked")) {
        dojo.query(".legacyControl").forEach(function(element) {
           element.style.display = "";
        });
    } else {
        dojo.query(".legacyControl").forEach(function(element) {
           element.style.display = "none";
        });
    }
}

/**
 * get for each rubric the number of direct children not being a legacyControl
 * if this number is 1 (just the rubric div element!) then we can hide the rubric
 */
scopeAdminFormFields.hideEmtyRubrics = function() {
    var b = dijit.byId("checkBoxHideRubricsButton");
    
    var allRubrics = dojo.query(".rubricDiv");
    dojo.forEach(allRubrics, function(rubric) {
        var rubricParent = rubric.parentNode;
        var nonLegacyChildren = dojo.query("> div:not(.legacyControl)", rubricParent).length;
        
        if (nonLegacyChildren == 1 && b.get("checked")) {
            dojo.style(rubricParent, "display", "none");
            // also hide "add new control" row!
            dojo.style(rubricParent.nextSibling, "display", "none");
        } else {
            dojo.style(rubricParent, "display", "block");
            dojo.style(rubricParent.nextSibling, "display", "block");
        }
    });
}

scopeAdminFormFields.reload = function(fromServer, /*Array*/rubrics, pos) {
    if (fromServer) {
        CatalogService.getProfileData({
            callback: function(data) {
                if (!data) {
                    displayErrorMessage("Profile corrupted or not created! Please update DB with UDK Importer!");
                    return;
                }
                // remember data for use in editing and later storing
                scopeAdminFormFields.hideSaveHint();
                scopeAdminFormFields.profileData = data;
                scopeAdminFormFields.reload(false);
                showInfoMessage("loaded", "infoFormFields");
            },
            errorHandler:function(mes, obj) {
                displayErrorMessage(obj);
            }
        });
    } else {
        var previousScrollPosition = dojo.byId("catalogueFormFields").scrollTop;
        if (rubrics) {
            dojo.forEach(rubrics, function(rubric) {
                dijit.byId("admin_"+rubric.id).destroyRecursive()
            });
        } else {
            scopeAdminFormFields.clearContent();
        }
        console.debug("pos: " + pos);
        var countRubric= 0;
        var firstRubric = null;
        var lastRubric = null;
        
        dojo.forEach(rubrics ? rubrics : scopeAdminFormFields.profileData.rubrics, function(rubric) {
            //console.debug(rubric);
        	countRubric++;
        	if(firstRubric == null && !rubric.isLegacy){
                firstRubric = rubric;
			}

        	if(lastRubric == null && countRubric == scopeAdminFormFields.profileData.rubrics.length){
				lastRubric = rubric;
			}
			
        	var rubricDiv = scopeAdminFormFields.writeRubric(rubric, firstRubric, lastRubric);
            // render all available elements
            var countControl = 0;
            var firstControl = null;
            var lastControl = null;
            dojo.forEach(rubric.controls, function(control) {
                countControl++;
            	if(firstControl == null && !control.isLegacy){
    				firstControl = control;
    			}

            	if(lastControl == null && countControl == rubric.controls.length){
    				lastControl = control;
    			}
            	scopeAdminFormFields.writeControl(control, rubricDiv, null, null, countControl, firstControl, lastControl);
           });
            
            // render input for new elements
            if (!rubrics) 
                scopeAdminFormFields.writeNewControl(rubricDiv);
            else {
                //console.debug("place rubric: ");
                //console.debug(rubric);
                //console.debug(" to pos: " + pos);
                dojo.place("admin_"+rubric.id, "catalogueFormFields", pos * 2); // times 2 because of new element row!
            }
        });
        scopeAdminFormFields.checkBoxLegacyVisibility();
        scopeAdminFormFields.hideEmtyRubrics();
        // check layout after data has changed inside the border container
        dijit.byId("contentContainer").resize();
        dojo.byId("catalogueFormFields").scrollTop = previousScrollPosition;
    }
}

scopeAdminFormFields.writeRubric = function(rubric, firstRubric, lastRubric) {
	var mostOuterDiv = dijit.layout.ContentPane({id:"admin_"+rubric.id, 'class':"expanded"});
    //dojo.connect(mostOuterDiv, "onMouseEnter", scopeAdminFormFields.showButtons);
    //dojo.connect(mostOuterDiv, "onMouseLeave", scopeAdminFormFields.hideButtons);
	
	var outerDiv = document.createElement("div");
    dojo.addClass(outerDiv, "rubricDiv");
	var innerSpanTitle = document.createElement("span");
	dojo.addClass(innerSpanTitle, "rubricLabel");
    if (rubric.isLegacy)
        innerSpanTitle.innerHTML = message.get("ui.obj."+rubric.id+".title");
    else
        innerSpanTitle.innerHTML = rubric.label[userLocale];
        
    dojo.create("a", {href: "javascript:scopeAdminFormFields.toggleRubric('admin_"+rubric.id+"');", innerHTML:"<div class='image18px titleIcon'></div>", style:"margin:2px 5px 0 0;"}, outerDiv);
	
	var innerSpanButtons = document.createElement("div");
	dojo.addClass(innerSpanButtons, "rubricButtons");
    scopeAdminFormFields.createButtons(rubric, innerSpanButtons, null, null, firstRubric, lastRubric);

    outerDiv.appendChild(innerSpanButtons);
    outerDiv.appendChild(innerSpanTitle);
	outerDiv.appendChild(createDivElement("fill"));
	mostOuterDiv.domNode.appendChild(outerDiv);
	dojo.place(mostOuterDiv.domNode, "catalogueFormFields");
	return mostOuterDiv.domNode;
}

scopeAdminFormFields.writeControl = function(control, putHere, editFunction, deleteFunction, countControl, firstControl, lastControl, newControl) {
	var outerDiv = dijit.layout.ContentPane({id:"admin_"+control.id});
	//outerDiv.connect(outerDiv, "onMouseEnter", scopeAdminFormFields.showButtons);
	//outerDiv.connect(outerDiv, "onMouseLeave", scopeAdminFormFields.hideButtons);
	if(countControl % 2 == 0){
		outerDiv.setAttribute("class", "row_odd border_left_right");
	}else{
		outerDiv.setAttribute("class", "row_even border_left_right");
	}
	
    var innerSpanTitle = document.createElement("span");
    dojo.addClass(innerSpanTitle, "controlLabel");
    var innerSpanTitlePadded = document.createElement("span");
    // add icon
    innerSpanTitlePadded.appendChild(scopeAdminFormFields.createControlIcon(control));
    
    if (control.isLegacy) {
        // mark control for easier hiding/showing
        dojo.addClass(outerDiv.domNode, "legacyControl");
        innerSpanTitlePadded.innerHTML += UtilUI.getDescriptionForGuiId(control.id.substr("uiElement".length));// + ".description");
    } else 
        innerSpanTitlePadded.innerHTML += control.label[userLocale] == "" ? control.label.en : control.label[userLocale];
    
    // add quick info for non legacy elements
    if (control.type != "legacyControl") {
		innerSpanTitlePadded.appendChild(scopeAdminFormFields.createQuickInfo(control));
	}
    
    var innerSpanButtons = document.createElement("span");
    dojo.addClass(innerSpanButtons, "rubricButtons");

	if(newControl){
    	scopeAdminFormFields.createButtons(control, innerSpanButtons, editFunction, deleteFunction, null, null, newControl);
    }else{
    	scopeAdminFormFields.createButtons(control, innerSpanButtons, editFunction, deleteFunction, firstControl, lastControl);
    }
    
    innerSpanTitle.appendChild(innerSpanTitlePadded);
    outerDiv.domNode.appendChild(innerSpanButtons);
    outerDiv.domNode.appendChild(innerSpanTitle);
	outerDiv.domNode.appendChild(createDivElement("fill"));
	if (putHere)
        dojo.place(outerDiv.domNode, putHere);
        
    // add javascript info if available
    // create tooltip only after connectId-node is inserted into DOM!!!
    scopeAdminFormFields.appendJsInfo(innerSpanTitlePadded, control);
    return outerDiv.domNode;
}

scopeAdminFormFields.appendJsInfo = function(appendTo, control) {
    if (control.scriptedProperties && dojo.trim(control.scriptedProperties).length > 0) {
        var tipId = control.id+"JsTooltip";
        dojo.create("span", {
            id: tipId,
            'class': "tooltip",
            innerHTML: "(JS)"
        }, appendTo);
        var tip = new dijit.Tooltip({connectId:[tipId], label:"<code class='javascript'>"+/*scopeAdminFormFields.convertToHTML(*/control.scriptedProperties/*)*/+"</code>"});
        dojo.connect(tip, "onShow", function() { dojo.query("code").forEach(dojox.highlight.init);});
         
    }
}

scopeAdminFormFields.createButtons = function(item, whereInDOM, editFunction, deleteFunction, firstItem, lastItem, newControl) {
	var isRubric = item.controls != undefined;
    var color = "";
	var external = false;
	if (editFunction != undefined)
	    var external = true;
	
	// use default methods for edit and delete actions if none were defined
    if (!external) {
		var editFunction = scopeAdminFormFields.editElement;
		var deleteFunction = scopeAdminFormFields.deleteElement;
	}

	if(isRubric){
		color = "formFieldsWhite";
	}else{
		color = "formFieldsBlue";
	}
	
    if (!item.isLegacy || !isRubric) {
		var editButton = new dijit.form.Button({
			label: "Edit",
			iconClass: color + " formFieldsEdit",
			showLabel: false
		}).placeAt(whereInDOM);
		editButton.onClick = dojo.partial(editFunction, item);
	}
	// do not show a delete button for legacy controls and rubrics
    if (!item.isLegacy) {
		var deleteButton = new dijit.form.Button({
			label: "Delete",
			iconClass: color + " formFieldsDelete",
			showLabel: false
		}).placeAt(whereInDOM);
		deleteButton.onClick = dojo.partial(deleteFunction, item.id);
	}

	// add up and down buttons for non legacy controls
	if (!external && !item.isLegacy) {
		var upButton = new dijit.form.Button({
			label: "Up",
			iconClass: color + " formFieldsUp",
			showLabel: false
		}).placeAt(whereInDOM);
		upButton.onClick = dojo.partial(scopeAdminFormFields.moveUp, item.id);

		var downButton = new dijit.form.Button({
			label: "Down",
			iconClass: color + " formFieldsDown",
	        showLabel: false
		}).placeAt(whereInDOM);
		downButton.onClick = dojo.partial(scopeAdminFormFields.moveDown, item.id);
		
		if(item == firstItem){
			upButton.setAttribute("style", "visibility:hidden;"); 
		}
		if(item == lastItem){
			downButton.setAttribute("style", "visibility:hidden;"); 
		}
	} else {
		if(newControl){
			var upButton = new dijit.form.Button({
				label: "Up",
				iconClass: color + " formFieldsUp",
				showLabel: false
			}).placeAt(whereInDOM);
			upButton.onClick = dojo.partial(scopeAdminFormFields.moveUp, item.id);
			upButton.setAttribute("style", "visibility:visible;");
			
			var downButton = new dijit.form.Button({
				label: "Down",
				iconClass: color + " formFieldsDown",
		        showLabel: false
			}).placeAt(whereInDOM);
			downButton.onClick = dojo.partial(scopeAdminFormFields.moveDown, item.id);
			
			downButton.setAttribute("style", "visibility:hidden;");  
		}
	}
}

scopeAdminFormFields.createControlIcon = function(control) {
	var image = document.createElement("div");
	
    if(control.type != "legacyControl"){
		image.setAttribute("alt", "ui.additionalFields.title." + control.type);
	    dojo.addClass(image, "formFieldsImage "+ control.type.toLowerCase());
	} else {
		image.setAttribute("style", "float:left;");
	}
    return image;
}

/*
scopeAdminFormFields.showButtons = function() {
	//console.debug("entered id: " + this.id);
	var buttons = dojo.query("#"+this.id+" .rubricButtons")[0];
    //dojo.fadeIn({node: buttons}).play();
    //dojo.style(buttons, "visibility", "visible");
}

scopeAdminFormFields.hideButtons = function() {
	var buttons = dojo.query("#"+this.id+" .rubricButtons")[0];
    //dojo.fadeOut({node: buttons}).play();
   	//dojo.style(buttons, "visibility", "hidden");
}*/

scopeAdminFormFields.writeNewControl = function(whereInDOM) {
        // put new element outside the rubric div so that new controls will appear above
        // the new-button
        var outerDiv = document.createElement("div");
        outerDiv.setAttribute("class", "rubricBottom")
        var innerSpanTitle = document.createElement("span");
        //dojo.addClass(innerSpanTitle, "controlLabel");
        var innerSpanTitlePadded = document.createElement("span");
        innerSpanTitlePadded.setAttribute("style", "padding: 0 10px 0 147px; display:block;");
        
        var selectBox = new dijit.form.Select({
            id: whereInDOM.id + "_select",
            store: scopeAdminFormFields.formTypeStore,
            searchAttr: "name",
            style: "width:328px;float:left;margin-top:4px;"
        }).placeAt(innerSpanTitlePadded);
        selectBox.startup();
		selectBox.set('value', "textControl"); // set default selected to text
        
        var innerSpanButtons = document.createElement("span");
        //dojo.addClass(innerSpanButtons, "rubricButtons");
		innerSpanButtons.setAttribute("style", "visibility:visible; padding: 2px 40px;");//opacity:1;filter: alpha(opacity = 100);"); // this button is always visible!
        var newButton = new dijit.form.Button({
            id: whereInDOM.id + "_button",
            label: "<fmt:message key='dialog.admin.additionalfields.btnNewControl' />"//"New"
        }).placeAt(innerSpanButtons);
        
        newButton.onClick = dojo.partial(scopeAdminFormFields.addElement, whereInDOM.id);
        
        innerSpanTitle.appendChild(innerSpanTitlePadded);
        outerDiv.appendChild(innerSpanTitle);
        outerDiv.appendChild(innerSpanButtons);
        outerDiv.appendChild(createDivElement("fill"));
     	dojo.place(outerDiv, whereInDOM.parentNode);
}

scopeAdminFormFields.createQuickInfo = function(control) {
	var height = "";
	var info = document.createElement("span");
	dojo.addClass(info, "comment");
	if (control.numLines && control.numLines > 1)
	   height = ", height: "+control.numLines+" rows";
	info.innerHTML = "(width: "+control.width+control.widthUnit+height+")";
	return info;
	
}

scopeAdminFormFields.doActionOnData = function(id, actionFunction) {
	var rubricPos = 0;
    var lastRubricIsLegacy = true;
    var rubrics = scopeAdminFormFields.profileData.rubrics;
    dojo.some(rubrics, function(rubric, pos) {
        var thisPos=0;
        var lastIsLegacy = true;
        
        // check if a rubric is to be moved
        if (rubric.id == id) {
            var result = actionFunction(rubrics, rubricPos, lastRubricIsLegacy);
            if (result == true) {
                // repaint all data
                scopeAdminFormFields.reload(false);
                return true;
            }
        }
        
        if (!rubric.isLegacy) 
            lastRubricIsLegacy = false;
        rubricPos++;
        
        // check the controls of this rubric
        var updateRubrics = [];
        if (dojo.some(rubric.controls, function(control){
            // remember always previous node
            if (control.id == id) {
				var result = actionFunction(rubric.controls, thisPos, lastIsLegacy);
				if (result == true) {
                    updateRubrics.push(rubric);
                    return true;
                }
            }
            if (!control.isLegacy) 
                lastIsLegacy = false;
            thisPos++;
        })) {
            // repaint all data
            scopeAdminFormFields.reload(false, updateRubrics, pos);
            return true;
        }
        else 
            return false;
    });
}

scopeAdminFormFields.changeControl = function(data, oldId) {
	var exchangeData = function(sections, pos, lastIsLegacy) {
		// overwrite modified item
		sections[pos] = data;
		return true;
	};
	
	scopeAdminFormFields.doActionOnData(oldId, exchangeData);
	
}

scopeAdminFormFields.moveUp = function(nodeToMove) {
	var moveUp = function(sections, pos, lastIsLegacy) {
        if (pos > 0 && !lastIsLegacy) { // and previous rubric is not legacy!
			var itemToMove = sections[pos];
			sections.splice(pos, 1);
			sections.splice(pos - 1, 0, itemToMove);
			return true;
		}
		return false;
    };
	
	scopeAdminFormFields.doActionOnData(nodeToMove, moveUp);
}

scopeAdminFormFields.moveDown = function(nodeToMove) {
	var moveDown = function(sections, pos, lastIsLegacy) {
        if (pos < sections.length-1) {
            var itemToMove = sections[pos];
            sections.splice(pos, 1);
            sections.splice(pos + 1, 0, itemToMove);
            return true;
        }
        return false;
    };
    
    scopeAdminFormFields.doActionOnData(nodeToMove, moveDown);
}

scopeAdminFormFields.clearContent = function() {
	dijit.byId("catalogueFormFields").destroyRecursive();
	
	new dijit.layout.ContentPane({
        id: "catalogueFormFields",
		'class': "content",
        style: "margin: 5px 15px; padding: 5px;"
    }).placeAt("outerDiv", 0);
}

scopeAdminFormFields.editElement = function(item) {
	console.debug("Edit element with ID: " + item.id);
	//profileObjectToEdit = item;
	var def = new dojo.Deferred();
    dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.editField' />", "admin/dialogs/mdek_admin_catman_editField_dialog.jsp", 650, 600, true, {
        resultHandler: def,
		item: item,
		edit: true,
        type: item.type ? item.type : "rubric"
    });
	
	def.addCallback(function(data) {
        scopeAdminFormFields.changeControl(data, item.id);
        scopeAdminFormFields.showSaveHint();
    });
}

scopeAdminFormFields.deleteElement = function(id) {
    console.debug("Delete element with ID: " + id);
	var deleteIt = function(sections, pos, lastIsLegacy) {
        sections.splice(pos, 1);
        return true;
    };
    
    UtilGeneral.askUserAndInvokeOrCancel("<fmt:message key='dialog.admin.catalog.management.additionalFields.info.delete' />", function() {
        scopeAdminFormFields.doActionOnData(id, deleteIt);
        scopeAdminFormFields.showSaveHint();
    });
}

scopeAdminFormFields.addElement = function(insideRubric) {
	var type = dijit.byId(insideRubric+"_select").get('value');
	var item = {id:"newControl", isMandatory: false, label: {}, helpMessage: {}, indexName: "", width: "100", scriptedProperties: "", scriptedCswMapping: ""};
    
	item.type = type;
	
	if (type == "tableControl") {
		item.columns = [];
		item.numTableRows = 4;
		item.$dwrClassName = "TableControl";
        delete item.indexName;
	} else if (type == "textControl") {
		item.numLines = 1;
		item.$dwrClassName = "TextControl";
	} else if (type == "selectControl") {  
		item.options = {};
        item.$dwrClassName = "SelectControl";
	} else if (type == "dateControl") {
        item.$dwrClassName = "DateControl";
	} else if (type == "numberControl") {
        item.unit = {};
        item.$dwrClassName = "NumberControl";
	}
	
    var def = new dojo.Deferred();
    dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.addField' />", "admin/dialogs/mdek_admin_catman_editField_dialog.jsp", 650, 600, true, {
        resultHandler: def,
        item: item,
        edit: false,
        type: item.type ? item.type : "rubric"
    });
    
    def.addCallback(function(data) {
        var numControls = 0;
        console.debug("create new control");
		dojo.forEach(scopeAdminFormFields.profileData.rubrics, function(rubric) {
			if ("admin_"+rubric.id == insideRubric) {
                if (rubric.controls == undefined)
                    rubric.controls = [];
				rubric.controls.push(data);
                numControls = rubric.controls.length;
			}
		});
        scopeAdminFormFields.writeControl(data, dojo.byId(insideRubric), null, null, numControls, null, data);
        // repaint since previous last element is now second to last!
        var pos = 0;
        var r = dojo.filter(scopeAdminFormFields.profileData.rubrics, function(item, index) { if (item.id == "additionalFields") { pos = index; return true;} return false; })
        scopeAdminFormFields.reload(false, r, pos);
        scopeAdminFormFields.showSaveHint();
    });
}

scopeAdminFormFields.addRubric = function() {
    console.debug("add element to:" + this);
	var item = {id:"newRubric", label: {de:"Neue Rubrik"}};
    
	var def = new dojo.Deferred();
    dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.addRubric' />", "admin/dialogs/mdek_admin_catman_editField_dialog.jsp", 650, 600, true, {
        resultHandler: def,
		item: item,
        type: "rubric"
    });
	
	def.addCallback(function(data) {
		console.debug("create new rubric");
        scopeAdminFormFields.profileData.rubrics.push(data);
		scopeAdminFormFields.writeNewControl(scopeAdminFormFields.writeRubric(data));
        scopeAdminFormFields.showSaveHint();
	});
	
}

scopeAdminFormFields.showSaveHint = function() {
    dojo.addClass(dijit.byId("btnSaveProfileTop").domNode, "attention");
    dojo.addClass(dijit.byId("btnSaveProfileBottom").domNode, "attention");
}

scopeAdminFormFields.hideSaveHint = function() {
    dojo.removeClass(dijit.byId("btnSaveProfileTop").domNode, "attention");
    dojo.removeClass(dijit.byId("btnSaveProfileBottom").domNode, "attention");
}

scopeAdminFormFields.changeCheckBox = function(id) {
   	if (dijit.byId(id).checked) {
   		dijit.byId(id).setValue(false);
    }else{
    	dijit.byId(id).setValue(true);
    }
}

scopeAdminFormFields.toggleRubric = function(rubricId, mode) {
    //rubricId = "admin_"+rubricId; 
    if ((!mode || mode == "collapse") && dojo.hasClass(rubricId, "expanded")) {
        dojo.removeClass(rubricId, "expanded");
        // hide add button
        dojo.addClass(dojo.byId(rubricId).nextSibling, "hideSub");
    } else if (mode != "collapse"){
        dojo.addClass(rubricId, "expanded");
        // show add button
        dojo.removeClass(dojo.byId(rubricId).nextSibling, "hideSub");
    }
}

scopeAdminFormFields.toggleExpandAllRubrics = function() {
    var mode = dijit.byId("checkBoxExpandAllRubrics").get("checked") ? "expand" : "collapse";
    console.debug("mode: " + mode);
    var allRubrics = dojo.query(".rubricDiv");
    dojo.forEach(allRubrics, function(rubric) {
        scopeAdminFormFields.toggleRubric(rubric.parentNode.id, mode)
    });
}

scopeAdminFormFields.convertToHTML = function(noBreaks) {
    noBreaks = noBreaks.replace(/\r\n/g,"[-LB-]");
    noBreaks = noBreaks.replace(/\n/g,"[-LB-]");
    noBreaks = noBreaks.replace(/\r/g,"[-LB-]");
    
    re1 = /\s+/g;
    noBreaks = noBreaks.replace(re1," ");
    
    
    //if(jbrTag != 0 || jbrTag !=  false){
    re4 = /\[-LB-\]\[-LB-\]/gi;
    noBreaks = noBreaks.replace(re4,"<br />\r\n<br />\r\n");
    //}else{
    //re4 = /\[-LB-\]\[-LB-\]/gi;
    //noBreaks = noBreaks.replace(re4,"</p><p>");
    //}
    
    //if(jpTag == 0 || jpTag ==  false){
    //re5 = /\[-LB-\]/gi;
    //noBreaks = noBreaks.replace(re5,linebs+"\r\n");
    //}else{
    re5 = /\[-LB-\]/gi;
    noBreaks = noBreaks.replace(re5," ");
    //}
    
    //if(jbrTag == 0 || jbrTag ==  false){
    //noBreaks ='<p>'+noBreaks+'</p>';
    //}
    
    noBreaks = noBreaks.replace("<p><\/p>","");
    noBreaks = noBreaks.replace("\r\n\r\n","");
    noBreaks = noBreaks.replace(/<\/p><p>/g,"</p>\r\n\r\n<p>");
    return noBreaks;
}

scopeAdminFormFields.saveProfile = function() {
	CatalogService.saveProfileData(scopeAdminFormFields.profileData, {
        callback: function(data) {
            console.debug("profile saved");
            scopeAdminFormFields.hideSaveHint();
            showInfoMessage("saved", "infoFormFields");
            dialog.show("Info", "<fmt:message key='dialog.admin.catalog.management.additionalFields.info.save' />", dialog.INFO);
        },
        errorHandler:function(mes, obj) {
            displayErrorMessage(obj);
        }
    });
}

</script>
</head>

<body>

<!-- CONTENT START -->
	<div id="formFields" dojoType="dijit.layout.BorderContainer" class="" gutters="false" style="height:100%;min-width:750px;">
		
        <div dojoType="dijit.layout.ContentPane" region="top" style="height:60px;">
            <div id="formFieldsToolBar">
            	<div style="float:left; padding: 2px 10px 0;">
	            	<div id="checkBoxLegacyFieldsButton" dojotype="dijit.form.CheckBox" name="<fmt:message key='dialog.admin.additionalfields.hide.legacy' />" 
					       type="checkbox">
					       <script type="dojo/method" event="onClick" args="evt">
							scopeAdminFormFields.checkBoxLegacyVisibility(); 
							</script>      
					</div>
					<fmt:message key='dialog.admin.additionalfields.hide.legacy' />
				</div>
				<div style="float:left; padding: 2px 10px 0;">
					<div id="checkBoxHideRubricsButton" dojotype="dijit.form.CheckBox" name="<fmt:message key='dialog.admin.additionalfields.hide.empty.rubrics' />" 
					       type="checkbox">
					       <script type="dojo/method" event="onClick" args="evt">
							scopeAdminFormFields.hideEmtyRubrics(); 
							</script> 
					</div>
					<fmt:message key='dialog.admin.additionalfields.hide.empty.rubrics' />
				</div>
                <div style="float:left; padding: 2px 10px 0;">
                    <div id="checkBoxExpandAllRubrics" dojotype="dijit.form.CheckBox" checked name="<fmt:message key='dialog.admin.additionalfields.expand.all.rubrics' />" 
                           type="checkbox">
                           <script type="dojo/method" event="onClick" args="evt">
                            scopeAdminFormFields.toggleExpandAllRubrics(); 
                            </script> 
                    </div>
                    <fmt:message key='dialog.admin.additionalfields.expand.all.rubrics' />
                </div> 
            </div>
            <div id="winNavi" style="padding: 5px 35px;">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-5#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
        </div>
		<div id="outerDiv" dojoType="dijit.layout.ContentPane" region="center" style="top:30px;">
            <div id="catalogueFormFields" dojoType="dijit.layout.ContentPane" class="content">
			    <!-- CONTENT START -->
                <!-- CONTENT END -->
			</div>
        </div>
        <div dojoType="dijit.layout.ContentPane" region="bottom" class="form_fields_bottom">
			<div dojoType="dijit.layout.ContentPane" style="top:30px; padding: 5px 0;">
				<button dojoType="dijit.form.Button" style="float:right;">
			                <fmt:message key="dialog.admin.additionalfields.btnNewRubric" />
			                <script type="dojo/method" event="onClick" args="evt">
                    scopeAdminFormFields.addRubric();
                </script>
			            </button>
			   		<button id="btnSaveProfileBottom" dojoType="dijit.form.Button" style="float:right;">
			                <fmt:message key="dialog.admin.additionalfields.btnSave" />
			                <script type="dojo/method" event="onClick" args="evt">
                    scopeAdminFormFields.saveProfile();
                </script>
	            </button>
	                     <button dojoType="dijit.form.Button" style="float:right;">
			                <fmt:message key="dialog.admin.additionalfields.btnReload" />
			                <script type="dojo/method" event="onClick" args="evt">
                    scopeAdminFormFields.reload(true);
                </script>
			            </button>
				<div id="infoFormFields" style="float: right;color: grey;line-height: 27px;"></div>
			</div>
            
		</div>
	</div>

</body>
</html>
