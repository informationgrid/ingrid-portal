<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript" src="dojo-src/dojo/dojo.js" djConfig="parseOnLoad:false"></script>
        <script type="text/javascript" src="dojo-src/custom/layer_forms.js"></script>
        <script type="text/javascript">
            /*dojo.require("dojox.grid.DataGrid");*/
            /*dojo.require("ingrid.dijit.CustomDataGrid");*/
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dojo.date.locale");
            dojo.require("dijit.form.DateTextBox");
            dojo.require("dijit.form.FilteringSelect");
            //dojo.require("ingrid.dijit.GridDateTextBox");
            //dojo.require("ingrid.dijit.tree.LazyTreeStoreModel"); 
            //dojo.require("ingrid.dijit.CustomTree");
            //dojo.require("ingrid.dijit.CustomDataGrid");
            dojo.require("dijit.layout.TabContainer");
            //dojo.require("ingrid.data.CustomItemFileWriteStore");
            
            dojo.require("dojox.grid.cells.dijit");
            
            testFunc = function() {
                console.debug("in testFunc");
                alert("okay");
            }
            
            var structure = [{
                field: 'name',
                name: 'name',
                width: '200px',
                type: dojox.grid.cells.ComboBox,
                options: ["a","b","c","d"], // will be filled later, when syslists are loaded
                //values: [],
                editable: true/*,
                formatter: function(value){
                    return UtilList.getSelectDisplayValue(this, value);
                }*/
            }, {
                field: 'icon',
                name: 'i(number)',
                width: '123px', editable: true, type: dojox.grid.cells._Widget,
                widgetClass: dijit.form.NumberTextBox
                //formatter: validateGrid
                //onChange: function(){console.debug("in cell editor");}
                /*constraint: {
                    format: ["#?.#??????", "#???????"]
                }*/
                //constraint: { type: 'decimal' },
            }, {
                field: 'linkLabel',
                name: 'label',
                width: '100%', editable: true//, formatter: validateGrid
            }];
            
            //createDataGrid("tableTest", null, structure, initTree);
            //createDataGrid("tableTest2", null, structure, initTree);
            
            /*dijit.byId("tableTest").setStore(dijit.byId("tableTest2").store);//, dijit.byId("tableTest").query);
            
            dojo.connect(dijit.byId("tableTest2"), "onApplyCellEdit", function(inValue, inRowIndex, inFieldIndex) {
                console.debug("cell edited: " + inValue +":" + inRowIndex +":"+inFieldIndex);
            });
            */
            new dijit.form.DateTextBox({onChange: function(){dijit.byId('myDate2').constraints.min = arguments[0]}}, "myDate");
            new dijit.form.DateTextBox({onChange:function() {dijit.byId('myDate').constraints.max = arguments[0]}}, "myDate2");
            
            //addMinMaxDateValidation("timeRefType", "myDate", "myDate2");
            
            /*
            var hierarchy = new dojox.layout.ContentPane({
                id: "test",
                title: "hierarchy",
                layoutAlign: "client",
                style: "padding: 0px; width: 100%; height: 100%;",
                href: "dialogs/mdek_spatial_navigator.jsp",
                //preload: "false",
                //doLayout: true,
                executeScripts: true
            }, "testPane");*/
            
            dojo.addOnLoad(function() {
                //testFunc();
                //def.addCallback(function() {
                    //gridMenu = createGeneralTableContextMenu(["tableTest","tableTest2"]);
                    //dijit.byId("tableTest").onRowContextMenu = onRowEvent;
                    //dijit.byId("tableTest2").onRowContextMenu = onRowEvent;
                    //dijit.byId("tableTest").startup();
                    //dijit.byId("tableTest").onDblClick = onClickEvent;
                    //dojo.connect(dijit.byId("tableTest").domNode, 'ondblclick', onClickEvent);
                    //dijit.byId("tableTest").onCellDblClickParent = dijit.byId("tableTest").onCellDblClick;
                    //dijit.byId("tableTest").onCellDblClick = onCellDouble;
                //});
                
                var storeProps = {data: {identifier: '1',label: '0'}};
                createComboBox("headerAddressType3StyleTest", null, storeProps, function(){
                    return UtilSyslist.getSyslistEntry(4300);
                });
                //var storeProps2 = {data: {identifier: '1',label: '0'}};
                createSelectBox("ref1DataSetTest", null, dojo.clone(storeProps), function(){
                    return UtilSyslist.getSyslistEntry(525);
                });
                createFilteringSelect("filteringSelectTest", null, dojo.clone(storeProps), function(){
                    return UtilSyslist.getSyslistEntry(525);
                });
                
                var validBox = new dijit.form.ValidationTextBox({value:""}, "validateText");
                
                validBox.validator = function(val) {
                    console.debug("validate: " + val);
                    if (val == "error") return false;
                    return true;                    
                };
                
                dojo.connect(validBox, "onKeyUp", function() {
                    console.debug("KeyUP!");
                });/*
                dojo.connect(validBox, "onChange", function() {
                    console.debug("onChange");
                });*/
                
                var tab1 = new dijit.layout.TabContainer({
                    style: "width: 500px; height: 129px"
                }, "formColumnOptionsTest");
                
                var formColumnOptionsStructure = [{
                        field: 'option', name: 'option', width: 'auto', editable: true
                    }];
                //var grid = createDataGridWidget("formColumnOptions_de", formColumnOptionsStructure, {});//, 
                    //dojo.partial(scriptScopeEditColumn.getListData, column.options[lang]));
                
                // add to tab container
                //var storeProps = {data: {identifier: 'option',label: 'option'}};
                //UtilStore.updateWriteStore("formColumnOptions_"+lang, scriptScopeEditColumn.getListData(column.options[lang]), storeProps);
                grid.title = "de";
                grid.style = "width:100px;height: 100px;";
                grid.startup();
                tab1.addChild(grid);
                
                tab1.startup();
                
                _connectWidgetWithDirtyFlag("filteringSelectTest");
                //_connectWidgetWithDirtyFlag("ref1DataSetTest");
                _connectWidgetWithDirtyFlag("headerAddressType3StyleTest");
                _connectWidgetWithDirtyFlag("tableTest");
                _connectWidgetWithDirtyFlag("tableTest2");
                _connectWidgetWithDirtyFlag("validateText");
                _connectWidgetWithDirtyFlag("myDate");
            });
            
            function initTree() {
                var def = new dojo.Deferred();
                var d = new Date();
                def.callback([{name: "hello", icon: "d", linkLabel:"1.9"}]);
                return def;
            }
            
            function setAStore() {
                var items = [{name: 'andre', icon: "e", linkLabel:"2.1"},{name: 'andre2', icon: "e2", linkLabel:"2.2"}];
                
                UtilStore.updateWriteStore(dijit.byId("tableTest2"), items);
                //dijit.byId("tableTest2").store = (dijit.byId("tableTest2"), items);
            }
            
            function showDialog() {
                var def = new dojo.Deferred();
                var displayText = dojo.string.substitute("<fmt:message key='dialog.addInspireTopics.message' />", ["bla"]);
                dialog.show("<fmt:message key='dialog.addInspireTopics.title' />", displayText, dialog.INFO, [
                    { caption: "<fmt:message key='general.ok' />", action: function() { return def.callback(); } }
                ]);
                def.addCallback(function(data) {console.debug("callback executed after dialog ok!");});
            }
            
            function isDirty(){
                alert(udkDataProxy.dirtyFlag);
            }
            
            function resetDirty(){
                udkDataProxy.resetDirtyFlag();
            }
        </script>
    </head>
    <body>
            Hallo Welt!
            <input type="text" value="test" id="validateText"/>
            <div id="myDate" type="text" value="" ></div>
            <div id="myDate2" type="text" value="" ></div>
            <div class="tableContainer rows4 full">
                <div id="tableTest" minRows="4" interactive="false" class="initHeightTable4Rows"></div><!--query="{name:'hello'}"-->
            </div>
            <div style="width:700px;">
                <div id="tableTest2" minRows="2" interactive="true" class="initHeightTable4Rows"></div>
            </div>
            <div id="testPane"></div>
            <button onclick="setAStore();">SetStore</button>
            <button onclick="showDialog();">ShowDialog</button>
            <button onclick="isDirty();">isDirty?</button>
            <button onclick="resetDirty();">resetDirty</button>
            <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage("<fmt:message key='ui.obj.links.linksTo.link' />", 'dialogs/mdek_links_dialog.jsp', 1000, 570, true);" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.links.linksTo.link" /></a></span>
            <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(<fmt:message key="dialog.operations.title' />", 'dialogs/mdek_operation_dialog.jsp', 735, 745, true);" title="<fmt:message key="dialog.popup.operationTable.link" /> [Popup]"><fmt:message key="ui.obj.type3.operationTable.link" /></a></span>
            <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(<fmt:message key="dialog.thesaurusNavigator.title' />", 'dialogs/mdek_thesaurus_dialog.jsp', 1010, 430, true, {dstTable: 'thesaurusTermsAddress'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]">
                <fmt:message key="ui.adr.thesaurus.terms.link.navigator" />
            </a></span>
            <div id="headerAddressType3StyleTest"></div>
            <div>
                <div style="display:inline;">
                    <span class="outer halfWidth">
                        <div>
                            <span class="label required"><label onclick="javascript:dialog.showContextHelp(arguments[0], 3220)" for="ref1DataSetTest">Select Box</label></span>
                            <div id="ref1DataSetTest" style="width:100%;float:left;"></div>
                        </div>
                    </span>
                </div>
            </div>
            <div id="filteringSelectTest"></div>
            <div id="formColumnOptionsTest"></div>
    </body>
</html>
            
