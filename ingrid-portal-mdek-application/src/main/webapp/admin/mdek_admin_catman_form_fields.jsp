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
    
var pageAdditionalFields = _container_;

require([
        "dojo/on",
        "dojo/dom",
        "dojo/query",
        "dojo/dom-class",
        "dojo/dom-style",
        "dojo/dom-construct",
        "dojo/Deferred",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dijit/registry",
        "dojo/data/ItemFileReadStore",
        "dijit/Toolbar",
        "dijit/form/Button",
        "dijit/layout/ContentPane",
        "dijit/Tooltip",
        "dijit/form/Select",
        "ingrid/layoutCreator",
        "ingrid/message",
        "ingrid/utils/LoadingZone",
        "ingrid/utils/General",
        "ingrid/utils/Syslist",
        "ingrid/utils/String",
        "ingrid/utils/UI",
        "ingrid/dialog"
], function(on, dom, query, domClass, style, construct, Deferred, array, lang, registry, ItemFileReadStore, Toolbar, Button, ContentPane, Tooltip, Select, layoutCreator, message, LoadingZone, UtilGeneral, UtilSyslist, UtilString, UtilUI, dialog) {

        
        _container_.onLoadDeferred.then(init);
        var formTypeStore;

        function init() {
            var url = userLocale == "de" ? "js/data/additionalFormTypes_de.json" : "js/data/additionalFormTypes.json";
            formTypeStore = new ItemFileReadStore({url: url});
            createToolBar();
            reload(true);
        }
        
        function createToolBar() {
            var toolbar = new Toolbar({}, "formFieldsToolBar");
            toolbar.set("class", "form_fields_top");
        
            var button = new Button({label:"<fmt:message key='dialog.admin.additionalfields.btnNewRubric' />", onClick: pageAdditionalFields.addRubric});
            button.set("style", "float:right;");
            toolbar.addChild(button);
            button = new Button({id: "btnSaveProfileTop", label:"<fmt:message key='dialog.admin.additionalfields.btnSave' />", onClick: pageAdditionalFields.saveProfile});
            button.set("style", "float:right;");
            toolbar.addChild(button);
            button = new Button({label:"<fmt:message key='dialog.admin.additionalfields.btnReload' />", onClick: dojo.partial(reload,true)});
            button.set("style", "float:right;");
            toolbar.addChild(button);
            
        }
        
        function checkBoxLegacyVisibility() {
            var b = registry.byId("checkBoxLegacyFieldsButton");
            if (!b.get("checked")) {
                query(".legacyControl").forEach(function(element) {
                	domClass.remove( element, "hide" );
                });
            } else {
                query(".legacyControl").forEach(function(element) {
                	domClass.add( element, "hide" );
                });
            }
            // combine with other checkbox
            hideEmtyRubrics();
        }
        
        /**
         * get for each rubric the number of direct children not being a legacyControl
         * if this number is 1 (just the rubric div element!) then we can hide the rubric
         */
        function hideEmtyRubrics() {
            var b = registry.byId("checkBoxHideRubricsButton");
            
            var allRubrics = query(".rubricDiv");
            array.forEach(allRubrics, function(rubric) {
                var rubricParent = rubric.parentNode;
                var nonLegacyChildren = query("> .dijitContentPane:not(.hide)", rubricParent).length;
                
                if (nonLegacyChildren === 0 && b.get("checked")) {
                    domClass.add(rubricParent, "hide");
                    // also hide "add new control" row!
                    domClass.add(rubricParent.nextSibling, "hide");
                } else {
                	domClass.remove(rubricParent, "hide");
                	domClass.remove(rubricParent.nextSibling, "hide");
                }
            });
        }
        
        function reload(fromServer, /*Array*/rubrics, pos) {
            if (fromServer) {
                CatalogService.getProfileData({
                    callback: function(data) {
                        if (!data) {
                            displayErrorMessage("Profile corrupted or not created! Please update DB with UDK Importer!");
                            return;
                        }
                        // remember data for use in editing and later storing
                        hideSaveHint();
                        pageAdditionalFields.profileData = data;
                        reload(false);
                        dialog.showInfoMessage("loaded", "infoFormFields");
                    },
                    errorHandler:function(mes, obj) {
                        displayErrorMessage(obj);
                    }
                });
            } else {
                var previousScrollPosition = dom.byId("catalogueFormFields").scrollTop;
                if (rubrics) {
                    array.forEach(rubrics, function(rubric) {
                        registry.byId("admin_"+rubric.id).destroyRecursive();
                    });
                } else {
                    clearContent();
                }
                console.debug("pos: " + pos);
                var countRubric= 0;
                var firstRubric = null;
                var lastRubric = null;
                
                array.forEach(rubrics ? rubrics : pageAdditionalFields.profileData.rubrics, function(rubric) {
                    //console.debug(rubric);
                    countRubric++;
                    if (firstRubric == null && !rubric.isLegacy) {
                        firstRubric = rubric;
                    }
        
                    if (lastRubric == null && countRubric == pageAdditionalFields.profileData.rubrics.length) {
                        lastRubric = rubric;
                    }
                    
                    var rubricDiv = writeRubric(rubric, firstRubric, lastRubric);
                    // render all available elements
                    var countControl = 0;
                    var firstControl = null;
                    var lastControl = null;
                    array.forEach(rubric.controls, function(control) {
                        countControl++;
                        if (firstControl == null && !control.isLegacy) {
                            firstControl = control;
                        }
        
                        if (lastControl == null && countControl == rubric.controls.length) {
                            lastControl = control;
                        }
                        writeControl(control, rubricDiv, null, null, countControl, firstControl, lastControl);
                    });
                    
                    // render input for new elements
                    if (!rubrics)
                        writeNewControl(rubricDiv);
                    else {
                        //console.debug("place rubric: ");
                        //console.debug(rubric);
                        //console.debug(" to pos: " + pos);
                        construct.place("admin_"+rubric.id, "catalogueFormFields", pos * 2); // times 2 because of new element row!
                    }
                });
                checkBoxLegacyVisibility();
                hideEmtyRubrics();
                // check layout after data has changed inside the border container
                registry.byId("contentContainer").resize();
                dom.byId("catalogueFormFields").scrollTop = previousScrollPosition;
            }
        }
        
        function writeRubric(rubric, firstRubric, lastRubric) {
            var mostOuterDiv = ContentPane({id:"admin_"+rubric.id, 'class':"expanded"});
            //on(mostOuterDiv, "onMouseEnter", showButtons);
            //on(mostOuterDiv, "onMouseLeave", hideButtons);
            
            var outerDiv = document.createElement("div");
            domClass.add(outerDiv, "rubricDiv");
            var innerSpanTitle = document.createElement("span");
            domClass.add(innerSpanTitle, "rubricLabel");
            if (rubric.isLegacy)
                innerSpanTitle.innerHTML = message.get("ui.obj."+rubric.id+".title");
            else
                innerSpanTitle.innerHTML = rubric.label[userLocale];
                
            construct.create("a", {href: "#", onclick:"pageAdditionalFields.toggleRubric('admin_"+rubric.id+"')", innerHTML:"<div class='image18px titleIcon'></div>", style:"margin:2px 5px 0 0;"}, outerDiv);
            
            var innerSpanButtons = document.createElement("div");
            domClass.add(innerSpanButtons, "rubricButtons");
            createButtons(rubric, innerSpanButtons, null, null, firstRubric, lastRubric);
        
            outerDiv.appendChild(innerSpanButtons);
            outerDiv.appendChild(innerSpanTitle);
            outerDiv.appendChild(layoutCreator.createDivElement("fill"));
            mostOuterDiv.domNode.appendChild(outerDiv);
            construct.place(mostOuterDiv.domNode, "catalogueFormFields");
            return mostOuterDiv.domNode;
        }
        
        function writeControl(control, putHere, editFunction, deleteFunction, countControl, firstControl, lastControl, newControl) {
            var outerDiv = ContentPane({id:"admin_"+control.id});

            if (countControl % 2 === 0) {
                outerDiv.set("class", "row_odd border_left_right");
            } else {
                outerDiv.set("class", "row_even border_left_right");
            }
            
            var innerSpanTitle = document.createElement("span");
            domClass.add(innerSpanTitle, "controlLabel");
            var innerSpanTitlePadded = document.createElement("span");
            // add icon
            innerSpanTitlePadded.appendChild(createControlIcon(control));
            
            if (control.isLegacy) {
                // mark control for easier hiding/showing
                domClass.add(outerDiv.domNode, "legacyControl");
                innerSpanTitlePadded.innerHTML += UtilUI.getDescriptionForGuiId(control.id.substr("uiElement".length));// + ".description");
            } else
                innerSpanTitlePadded.innerHTML += control.label[userLocale] == "" ? control.label.en : control.label[userLocale];
            
            // add quick info for non legacy elements
            if (control.type != "legacyControl") {
                innerSpanTitlePadded.appendChild(createQuickInfo(control));
            }
            
            var innerSpanButtons = document.createElement("span");
            domClass.add(innerSpanButtons, "rubricButtons");
        
            if(newControl){
                createButtons(control, innerSpanButtons, editFunction, deleteFunction, null, null, newControl);
            }else{
                createButtons(control, innerSpanButtons, editFunction, deleteFunction, firstControl, lastControl);
            }
            
            innerSpanTitle.appendChild(innerSpanTitlePadded);
            outerDiv.domNode.appendChild(innerSpanButtons);
            outerDiv.domNode.appendChild(innerSpanTitle);
            outerDiv.domNode.appendChild(layoutCreator.createDivElement("fill"));
            if (putHere)
                construct.place(outerDiv.domNode, putHere);
                
            // add javascript info if available
            // create tooltip only after connectId-node is inserted into DOM!!!
            appendJsInfo(innerSpanTitlePadded, control);
            appendIdfInfo(innerSpanTitlePadded, control);
            return outerDiv.domNode;
        }
        
        function appendJsInfo(appendTo, control) {
            if (control.scriptedProperties && lang.trim(control.scriptedProperties).length > 0) {
                var tipId = control.id+"JsTooltip";
                construct.create("span", {
                    id: tipId,
                    'class': "tooltip",
                    innerHTML: "(JS)"
                }, appendTo);
                var tip = new Tooltip({connectId:[tipId], label:"<code class='javascript'>"+control.scriptedProperties.replace(/\n/g, "<br>")+"</code>"});
                on(tip, "onShow", function() { query("code").forEach(dojox.highlight.init);});
                 
            }
        }
        
        function appendIdfInfo(appendTo, control) {
            if (control.scriptedCswMapping && lang.trim(control.scriptedCswMapping).length > 0) {
                var tipId = control.id+"IdfTooltip";
                construct.create("span", {
                    id: tipId,
                    'class': "tooltip",
                    innerHTML: "(IDF)"
                }, appendTo);
                var tip = new Tooltip({connectId:[tipId], label:"<code class='javascript'>"+control.scriptedCswMapping.replace(/\n/g, "<br>")+"</code>"});
                on(tip, "onShow", function() { query("code").forEach(dojox.highlight.init);});
                 
            }
        }
        
        function createButtons(item, whereInDOM, editFunction, deleteFunction, firstItem, lastItem, newControl) {
            var isRubric = item.controls != undefined;
            var color = "";
            var external = false;
            if (editFunction != undefined)
                external = true;
            
            // use default methods for edit and delete actions if none were defined
            if (!external) {
                editFunction = editElement;
                deleteFunction = deleteElement;
            }
        
            if(isRubric){
                color = "formFieldsWhite";
            }else{
                color = "formFieldsBlue";
            }
            
            if (!item.isLegacy || !isRubric) {
                var editButton = new Button({
                    label: "Edit",
                    iconClass: color + " formFieldsEdit",
                    showLabel: false
                }).placeAt(whereInDOM);
                editButton.onClick = dojo.partial(editFunction, item);
            }
            // do not show a delete button for legacy controls and rubrics
            if (!item.isLegacy) {
                var deleteButton = new Button({
                    label: "Delete",
                    iconClass: color + " formFieldsDelete",
                    showLabel: false
                }).placeAt(whereInDOM);
                deleteButton.onClick = dojo.partial(deleteFunction, item.id);
            }
        
            // add up and down buttons for non legacy controls
            if (!external && !item.isLegacy) {
                var upButton = new Button({
                    label: "Up",
                    iconClass: color + " formFieldsUp",
                    showLabel: false
                }).placeAt(whereInDOM);
                upButton.onClick = dojo.partial(moveUp, item.id);
        
                var downButton = new Button({
                    label: "Down",
                    iconClass: color + " formFieldsDown",
                    showLabel: false
                }).placeAt(whereInDOM);
                downButton.onClick = dojo.partial(moveDown, item.id);
                
                if(item == firstItem){
                    upButton.set("style", "visibility:hidden;");
                }
                if(item == lastItem){
                    downButton.set("style", "visibility:hidden;");
                }
            } else {
                if(newControl){
                    var upButton = new Button({
                        label: "Up",
                        iconClass: color + " formFieldsUp",
                        showLabel: false
                    }).placeAt(whereInDOM);
                    upButton.onClick = dojo.partial(moveUp, item.id);
                    upButton.setAttribute("style", "visibility:visible;");
                    
                    var downButton = new Button({
                        label: "Down",
                        iconClass: color + " formFieldsDown",
                        showLabel: false
                    }).placeAt(whereInDOM);
                    downButton.onClick = dojo.partial(moveDown, item.id);
                    
                    downButton.setAttribute("style", "visibility:hidden;");  
                }
            }
        }
        
        function createControlIcon(control) {
            var image = document.createElement("div");
            
            if(control.type != "legacyControl"){
                image.setAttribute("alt", "ui.additionalFields.title." + control.type);
                domClass.add(image, "formFieldsImage "+ control.type.toLowerCase());
            } else {
                image.setAttribute("style", "float:left;");
            }
            return image;
        }
        
        /*
        showButtons = function() {
            //console.debug("entered id: " + this.id);
            var buttons = query("#"+this.id+" .rubricButtons")[0];
            //dojo.fadeIn({node: buttons}).play();
            //style(buttons, "visibility", "visible");
        }
        
        hideButtons = function() {
            var buttons = query("#"+this.id+" .rubricButtons")[0];
            //dojo.fadeOut({node: buttons}).play();
            //style(buttons, "visibility", "hidden");
        }*/
        
        function writeNewControl(whereInDOM) {
            // put new element outside the rubric div so that new controls will appear above
            // the new-button
            var outerDiv = document.createElement("div");
            outerDiv.setAttribute("class", "rubricBottom");
            var innerSpanTitle = document.createElement("span");
            //domClass.add(innerSpanTitle, "controlLabel");
            var innerSpanTitlePadded = document.createElement("span");
            innerSpanTitlePadded.setAttribute("style", "padding: 0 10px 0 147px; display:block;");
            
            var selectBox = new Select({
                id: whereInDOM.id + "_select",
                store: formTypeStore,
                searchAttr: "name",
                style: "width:328px;float:left;margin-top:4px;"
            }).placeAt(innerSpanTitlePadded);
            selectBox.startup();
            selectBox.set('value', "textControl"); // set default selected to text
            
            var innerSpanButtons = document.createElement("span");
            //domClass.add(innerSpanButtons, "rubricButtons");
            innerSpanButtons.setAttribute("style", "visibility:visible; padding: 2px 40px;");//opacity:1;filter: alpha(opacity = 100);"); // this button is always visible!
            var newButton = new Button({
                id: whereInDOM.id + "_button",
                label: "<fmt:message key='dialog.admin.additionalfields.btnNewControl' />"//"New"
            }).placeAt(innerSpanButtons);
            
            newButton.onClick = dojo.partial(addElement, whereInDOM.id);
            
            innerSpanTitle.appendChild(innerSpanTitlePadded);
            outerDiv.appendChild(innerSpanTitle);
            outerDiv.appendChild(innerSpanButtons);
            outerDiv.appendChild(layoutCreator.createDivElement("fill"));
            construct.place(outerDiv, whereInDOM.parentNode);
        }
        
        function createQuickInfo(control) {
            var height = "";
            var info = document.createElement("span");
            domClass.add(info, "comment");
            if (control.numLines && control.numLines > 1)
                height = ", height: "+control.numLines+" rows";
            info.innerHTML = "(width: "+control.width+control.widthUnit+height+")";
            return info;
            
        }
        
        function doActionOnData(id, actionFunction) {
            var rubricPos = 0;
            var lastRubricIsLegacy = true;
            var rubrics = pageAdditionalFields.profileData.rubrics;
            array.some(rubrics, function(rubric, pos) {
                var thisPos=0;
                var lastIsLegacy = true;
                
                // check if a rubric is to be moved
                if (rubric.id == id) {
                    var result = actionFunction(rubrics, rubricPos, lastRubricIsLegacy);
                    if (result === true) {
                        // repaint all data
                        reload(false);
                        return true;
                    }
                }
                
                if (!rubric.isLegacy)
                    lastRubricIsLegacy = false;
                rubricPos++;
                
                // check the controls of this rubric
                var updateRubrics = [];
                if (array.some(rubric.controls, function(control){
                    // remember always previous node
                    if (control.id == id) {
                        var result = actionFunction(rubric.controls, thisPos, lastIsLegacy);
                        if (result === true) {
                            updateRubrics.push(rubric);
                            return true;
                        }
                    }
                    if (!control.isLegacy)
                        lastIsLegacy = false;
                    thisPos++;
                })) {
                    // repaint all data
                    reload(false, updateRubrics, pos);
                    return true;
                }
                else
                    return false;
            });
        }
        
        function changeControl(data, oldId) {
            var exchangeData = function(sections, pos) {
                // overwrite modified item
                sections[pos] = data;
                return true;
            };
            
            doActionOnData(oldId, exchangeData);
            
        }
        
        function moveUp(nodeToMove) {
            var moveUpFct = function(sections, pos, lastIsLegacy) {
                if (pos > 0 && !lastIsLegacy) { // and previous rubric is not legacy!
                    var itemToMove = sections[pos];
                    sections.splice(pos, 1);
                    sections.splice(pos - 1, 0, itemToMove);
                    return true;
                }
                return false;
            };
            
            doActionOnData(nodeToMove, moveUpFct);
        }
        
        function moveDown(nodeToMove) {
            var moveDownFct = function(sections, pos) {
                if (pos < sections.length-1) {
                    var itemToMove = sections[pos];
                    sections.splice(pos, 1);
                    sections.splice(pos + 1, 0, itemToMove);
                    return true;
                }
                return false;
            };
            
            doActionOnData(nodeToMove, moveDownFct);
        }
        
        function clearContent() {
            registry.byId("catalogueFormFields").destroyRecursive();
            
            new ContentPane({
                id: "catalogueFormFields",
                'class': "content",
                style: "margin: 5px 15px; padding: 5px;"
            }).placeAt("outerDiv", 0);
        }
        
        function editElement(item) {
            console.debug("Edit element with ID: " + item.id);
            //profileObjectToEdit = item;
            var def = new Deferred();
            dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.editField' />", "admin/dialogs/mdek_admin_catman_editField_dialog.jsp", 650, 600, true, {
                resultHandler: def,
                item: item,
                edit: true,
                type: item.type ? item.type : "rubric"
            });
            
            def.then(function(data) {
                changeControl(data, item.id);
                showSaveHint();
            });
        }
        
        function deleteElement(id) {
            console.debug("Delete element with ID: " + id);
            var deleteIt = function(sections, pos) {
                sections.splice(pos, 1);
                return true;
            };
            
            UtilGeneral.askUserAndInvokeOrCancel("<fmt:message key='dialog.admin.catalog.management.additionalFields.info.delete' />", function() {
                doActionOnData(id, deleteIt);
                showSaveHint();
            });
        }
        
        function addElement(insideRubric) {
            var type = registry.byId(insideRubric+"_select").get('value');
            var item = {id:"newControl", isMandatory: false, label: {}, helpMessage: {}, indexName: "", width: "100", scriptedProperties: "", scriptedCswMapping: ""};
            
            item.type = type;
            
            if (type == "tableControl") {
                item.columns = [];
                item.numTableRows = 4;
                item.$dwrClassName = "TableControl";
                delete item.indexName;
            } else if (type == "thesaurusControl") {
                item.numTableRows = 4;
                item.$dwrClassName = "ThesaurusControl";
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
            } else if (type == "checkboxControl") {
                item.$dwrClassName = "CheckboxControl";
            }
            
            var def = new Deferred();
            dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.addField' />", "admin/dialogs/mdek_admin_catman_editField_dialog.jsp", 650, 600, true, {
                resultHandler: def,
                item: item,
                edit: false,
                type: item.type ? item.type : "rubric"
            });
            
            def.then(function(data) {
                var numControls = 0;
                console.debug("create new control");
                array.forEach(pageAdditionalFields.profileData.rubrics, function(rubric) {
                    if ("admin_"+rubric.id == insideRubric) {
                        if (rubric.controls == undefined)
                            rubric.controls = [];
                        rubric.controls.push(data);
                        numControls = rubric.controls.length;
                    }
                });
                writeControl(data, dom.byId(insideRubric), null, null, numControls, null, data);
                // repaint since previous last element is now second to last!
                var pos = 0;
                var r = array.filter(pageAdditionalFields.profileData.rubrics, function(item, index) { if (item.id == "additionalFields") { pos = index; return true;} return false; });
                reload(false, r, pos);
                showSaveHint();
            });
        }
        
        function addRubric() {
            console.debug("add element to:" + this);
            var item = {id:"newRubric", label: {de:"Neue Rubrik"}};
            
            var def = new Deferred();
            dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.addRubric' />", "admin/dialogs/mdek_admin_catman_editField_dialog.jsp", 650, 600, true, {
                resultHandler: def,
                item: item,
                type: "rubric"
            });
            
            def.then(function(data) {
                console.debug("create new rubric");
                pageAdditionalFields.profileData.rubrics.push(data);
                writeNewControl(writeRubric(data));
                showSaveHint();
            });
            
        }
        
        function showSaveHint() {
            domClass.add(registry.byId("btnSaveProfileTop").domNode, "attention");
            domClass.add(registry.byId("btnSaveProfileBottom").domNode, "attention");
        }
        
        function hideSaveHint() {
            domClass.remove(registry.byId("btnSaveProfileTop").domNode, "attention");
            domClass.remove(registry.byId("btnSaveProfileBottom").domNode, "attention");
        }
        
        function toggleRubric(rubricId, mode) {
            //rubricId = "admin_"+rubricId; 
            if ((!mode || mode == "collapse") && domClass.contains(rubricId, "expanded")) {
                domClass.remove(rubricId, "expanded");
                // hide add button
                domClass.add(dom.byId(rubricId).nextSibling, "hideSub");
            } else if (mode != "collapse"){
                domClass.add(rubricId, "expanded");
                // show add button
                domClass.remove(dom.byId(rubricId).nextSibling, "hideSub");
            }
        }
        
        function toggleExpandAllRubrics() {
            var mode = registry.byId("checkBoxExpandAllRubrics").get("checked") ? "expand" : "collapse";
            console.debug("mode: " + mode);
            var allRubrics = query(".rubricDiv");
            array.forEach(allRubrics, function(rubric) {
                toggleRubric(rubric.parentNode.id, mode);
            });
        }
        
        function saveProfile() {
            CatalogService.saveProfileData(pageAdditionalFields.profileData, {
                callback: function() {
                    console.debug("profile saved");
                    hideSaveHint();
                    dialog.showInfoMessage("saved", "infoFormFields");
                    dialog.show("Info", "<fmt:message key='dialog.admin.catalog.management.additionalFields.info.save' />", dialog.INFO);
                },
                errorHandler:function(mes, obj) {
                    displayErrorMessage(obj);
                }
            });
        }

        /**
        * PUBLIC METHODS
        */
        pageAdditionalFields.showSaveHint = showSaveHint;
        pageAdditionalFields.writeControl = writeControl;
        pageAdditionalFields.addRubric = addRubric;
        pageAdditionalFields.reload = reload;
        pageAdditionalFields.saveProfile = saveProfile;
        pageAdditionalFields.checkBoxLegacyVisibility = checkBoxLegacyVisibility;
        pageAdditionalFields.hideEmtyRubrics = hideEmtyRubrics;
        pageAdditionalFields.toggleExpandAllRubrics = toggleExpandAllRubrics;
        pageAdditionalFields.toggleRubric = toggleRubric;

});
</script>
</head>

<body>

<!-- CONTENT START -->
    <div id="formFields" data-dojo-type="dijit/layout/BorderContainer" class="" gutters="false" style="height:100%;min-width:750px;">
        
        <div data-dojo-type="dijit/layout/ContentPane" region="top" style="height:60px;">
            <div id="formFieldsToolBar">
                <div style="float:left; padding: 2px 10px 0;">
                    <div id="checkBoxLegacyFieldsButton" data-dojo-type="dijit/form/CheckBox" name="<fmt:message key='dialog.admin.additionalfields.hide.legacy' />" onclick="pageAdditionalFields.checkBoxLegacyVisibility()" type="checkbox"></div>
                    <fmt:message key='dialog.admin.additionalfields.hide.legacy' />
                </div>
                <div style="float:left; padding: 2px 10px 0;">
                    <div id="checkBoxHideRubricsButton" data-dojo-type="dijit/form/CheckBox" name="<fmt:message key='dialog.admin.additionalfields.hide.empty.rubrics' />" onclick="pageAdditionalFields.hideEmtyRubrics()" type="checkbox"></div>
                    <fmt:message key='dialog.admin.additionalfields.hide.empty.rubrics' />
                </div>
                <div style="float:left; padding: 2px 10px 0;">
                    <div id="checkBoxExpandAllRubrics" data-dojo-type="dijit/form/CheckBox" checked name="<fmt:message key='dialog.admin.additionalfields.expand.all.rubrics' />" onclick="pageAdditionalFields.toggleExpandAllRubrics()" type="checkbox"></div>
                    <fmt:message key='dialog.admin.additionalfields.expand.all.rubrics' />
                </div> 
            </div>
            <div id="winNavi" style="padding: 5px 35px;">
                <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-5#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
        </div>
        <div id="outerDiv" data-dojo-type="dijit/layout/ContentPane" region="center" style="top:30px;">
            <div id="catalogueFormFields" data-dojo-type="dijit/layout/ContentPane" class="content">
                <!-- CONTENT START -->
                <!-- CONTENT END -->
            </div>
        </div>
        <div data-dojo-type="dijit/layout/ContentPane" region="bottom" class="form_fields_bottom">
            <div data-dojo-type="dijit/layout/ContentPane" style="top: 30px; padding: 5px 0;">
                <button data-dojo-type="dijit/form/Button" onclick="pageAdditionalFields.addRubric()"
                    style="float: right;">
                    <fmt:message key="dialog.admin.additionalfields.btnNewRubric" />
                </button>
                <button id="btnSaveProfileBottom" data-dojo-type="dijit/form/Button"
                    onclick="pageAdditionalFields.saveProfile()" style="float: right;">
                    <fmt:message key="dialog.admin.additionalfields.btnSave" />
                </button>
                <button data-dojo-type="dijit/form/Button" onclick="pageAdditionalFields.reload(true)"
                    style="float: right;">
                    <fmt:message key="dialog.admin.additionalfields.btnReload" />
                </button>
                <div id="infoFormFields" style="float: right; color: grey; line-height: 27px;"></div>
            </div>
        </div>
    </div>

</body>
</html>
