<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>Add/Edit Column</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">
        	scriptScopeEditColumn = _container_;
			_container_.onLoadDeferred.addCallback(init);
            
            var syncRowsInTableProgress = false;
			
			function init() {
                columnTypeStore.url = userLocale == "de" ? "js/data/additionalFormTypes_de.json" : "js/data/additionalFormTypes.json";
                columnTypeStore.close();
                dijit.byId("formColumnType").setStore(columnTypeStore)
				scriptScopeEditColumn.fillFields(scriptScopeEditColumn.customParams.column);
				dojo.connect(dijit.byId("formColumnType"), "onChange", scriptScopeEditColumn.layoutForms);
			}
			
			scriptScopeEditColumn.fillFields = function(column) {
                // create table with data
                var formColumnOptionsStructure = [
                    {field: 'id',name: 'id',width: '30px',editable: false},
                    {field: 'value',name: 'option',width: '300px',editable: true}
                    ];
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    dijit.byId("formColumnOptions").addChild(new dijit.layout.ContentPane({id:"formColumnOptions_" + lang + "Container", title: lang, 'class': "innerPadding"}));
                    var divElement = document.createElement("div");
                    divElement.setAttribute("id", "formColumnOptions_" + lang);
                    dojo.byId("formColumnOptions_" + lang + "Container").appendChild(divElement);
                    var props = {interactive: "true", moveRows: "true"};
                    createDataGrid("formColumnOptions_" + lang, null, formColumnOptionsStructure, null, props);
                    
                    // add special action to events on store for synchronization
                    dojo.connect(UtilGrid.getTable("formColumnOptions_" + lang), "onAddNewRow", scriptScopeEditColumn.syncOptionTableRowsNew);
                    dojo.connect(UtilGrid.getTable("formColumnOptions_" + lang), "onDataChanged", scriptScopeEditColumn.syncOptionTableRowsDelete);
                    
                    // set localized label
                    var titleWidget = new dijit.form.TextBox({
                        id: "formColumnLabel_" + lang,
                        title: lang,
                        'class': "fullWidth"
                    });
                    dijit.byId("formColumnLabel").addChild(titleWidget);
                    if (column != undefined) {
                        if (column.options)
                            UtilGrid.setTableData("formColumnOptions_" + lang, column.options[lang]);
                            
                        dijit.byId("formColumnLabel_" + lang).set('value', column.label[lang]);
                    }
                });
                
				// if it's not a new column
				if (column != undefined) {
					dijit.byId("formColumnType").set('value', column.type);
					dijit.byId("formColumnField").set('value', column.id);
                    dijit.byId("formColumnField").set('disabled', true);
                    this.editMode = true;
					dijit.byId("formColumnWidth").set('value', column.width);
					dijit.byId("formColumnIndex").set('value', column.indexName);
                    //if (column.allowFreeEntries) {
                    dijit.byId("columnAsCombobox").set("checked", column.allowFreeEntries);
                    //}
                    dijit.byId("saveAddColumn").set("label", "<fmt:message key='dialog.admin.additionalfields.btnUpdate' />");
					
					// we need to resize the table delayed since the dialog opens
					// animated and hasn't the full size yet so that the table can 
					// set its layout
                    if (column.type == "selectControl") {
                    //    setTimeout(function(){
                            dijit.byId("formColumnOptions").resize();
                    //    }, 700);
                    }
					
				} else {
                    dijit.byId("formColumnWidth").set('value', 100);
                }
				//if (column == undefined || column.type != "selectControl") {
                    //dojo.style("span_formColumnOptions", "display", "none");
                //}
			}
            
            
           
            scriptScopeEditColumn.syncOptionTableRowsNew = function(msg) {
                if (syncRowsInTableProgress) 
                    return;
                    
                syncRowsInTableProgress = true;
                console.debug("sync rows");
                // get active tab for language of new item
                var activelang = dijit.byId("formColumnOptions").selectedChildWidget.title;
                var newId = getNewId(UtilGrid.getTableData("formColumnOptions_"+activelang));
                console.debug(msg);
                msg.item.id = newId;
                msg.item.lang = activelang;
                
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    if (activelang != lang) {
                        var otherLangItem = {id: newId,lang: lang,value: "?"};
                        UtilGrid.addTableDataRow("formColumnOptions_"+lang, otherLangItem);
                    }
                });
                
                UtilGrid.getTable("formColumnOptions_"+activelang).invalidate();
                
                syncRowsInTableProgress = false;
            }
            
            scriptScopeEditColumn.syncOptionTableRowsDelete = function(msg){
                // only sync once!
                // since this function also calls the onNew-event we need to ignore those
                if (syncRowsInTableProgress || msg.type != "deleted") 
                    return;
                
                var activelang = dijit.byId("formColumnOptions").selectedChildWidget.title;
                
                syncRowsInTableProgress = true;
                console.debug("sync rows delete");
                
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    if (activelang != lang) {
                        var toDelete = [];
                        //console.debug("check: " + item.id + " with " + deletedItem.id + " => " + (item.id[0] == deletedItem.id[0]));
                        dojo.forEach(msg.items, function(deleteMe) {
                            var pos = 0;
                            var data = UtilGrid.getTableData("formColumnOptions_" + lang);
                            dojo.some(data, function(item) {
                                //console.debug("check: " + item.id + " with " + deleteMe.id);
                                if (item.id == deleteMe.id) {
                                    toDelete.push(pos);
                                    return true;
                                }
                                pos++;
                            });
                        });
                        // delete from lang-table now
                        UtilGrid.removeTableDataRow("formColumnOptions_" + lang, toDelete);
                    }
                });
                
                syncRowsInTableProgress = false;
            }
            
			
			scriptScopeEditColumn.getListData = function(options) {
                var prepOptions = [];
                dojo.forEach(options, function(option) {
                    prepOptions.push({option: option});
                });
                return prepOptions;
			}

            scriptScopeEditColumn.layoutForms = function() {
				console.debug("update form");
				var type = dijit.byId("formColumnType").get('value');
				if (type == "selectControl") {
					dojo.style(dojo.byId("span_formColumnOptions"), "display", "block");
                    dijit.byId("formColumnOptions").resize();
				} else {
					dojo.style(dojo.byId("span_formColumnOptions"), "display", "none");
				}
			}
						
			scriptScopeEditColumn.save = function() {
                var exitDialog = true;
                var message = "";
				var column = {};
				column.type = dijit.byId("formColumnType").get('value');
                column.id = dijit.byId("formColumnField").get('value');
				column.label = {};
                column.width = dijit.byId("formColumnWidth").get('value');
                column.indexName = dijit.byId("formColumnIndex").get('value');
				column.widthUnit = "px";
                
                if (column.type == "selectControl") {
                    column.options = {};
                    column.allowFreeEntries = dijit.byId("columnAsCombobox").get("checked");
                }
                
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    column.label[lang] = dijit.byId("formColumnLabel_"+lang).get('value');
                    if (column.type == "selectControl") {
                       column.options[lang] = UtilGrid.getTableData("formColumnOptions_" + lang);
                    }
                });
				console.debug(column.options);
                
                if (!this.editMode && (dojo.trim(column.id).length == 0 || dojo.query("#formColumnsContent #admin_" + column.id).length > 0)) {
                    exitDialog = false;
                    message = "Bitte vergeben Sie eine eindeutige ID!";
                }
                
                if (!exitDialog) {
                    dialog.show("Speichern nicht möglich", message, dialog.WARNING);
                } else {
				    scriptScopeEditColumn.customParams.resultHandler.callback(column);
				    scriptScopeEditColumn.hide();
                }
			}
		</script>
    </head>
    <body>
        <div id="catalogueObject" class="content" style="height:100%;">
            <div id="winNavi" style="top:0;">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=overall-catalog-management-additionalfields-2#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
                    <!-- CONTENT START -->
                    <span class="outer required">
                        <div>
                            <span class="label">
                                <label for="formColumnType" onclick="javascript:dialog.showContextHelp(arguments[0], 10150)">
                                    <fmt:message key="dialog.admin.additionalfields.type" />*
                                </label>
                            </span>
                            <span class="input">
                            	<div jsId="columnTypeStore" dojoType="dojo.data.ItemFileReadStore" ></div>
                                <input dojoType="dijit.form.Select" query="{asColumn: 'true'}" id="formColumnType" style="width:100%;float:left;" store="columnTypeStore" >
                            </span>
                        </div>
                    </span>
                    <span class="outer required">
                        <div>
                            <span class="label">
                                <label for="formColumnField" onclick="javascript:dialog.showContextHelp(arguments[0], 10151)">
                                    <fmt:message key="dialog.admin.additionalfields.id" />*
                                </label>
                            </span>
                            <span class="input">
                                 <input dojoType="dijit.form.TextBox" class="fullWidth" id="formColumnField"/>
                            </span>
                        </div>
                    </span>
                    <span class="outer">
                        <div>
                            <span class="label">
                                <label for="formColumnLabel" onclick="javascript:dialog.showContextHelp(arguments[0], 10152)">
                                    <fmt:message key="dialog.admin.additionalfields.title" />
                                </label>
                            </span>
                            <span class="input">
                                <div dojoType="dijit.layout.TabContainer" doLayout="false" controllerWidget="dijit.layout.TabController" id="formColumnLabel" style="width:100%;" selected="de">
                                    <!--<input dojoType="dijit.form.TextBox" class="fullWidth" id="formColumnLabel_de" title="de"/>
                                    <input dojoType="dijit.form.TextBox" class="fullWidth" id="formColumnLabel_en" title="en"/>-->
                                </div>
                            </span>
                        </div>
                    </span>
					<span id="span_formColumnOptions" class="outer">
                        <div>
                            <span class="label">
                                <label for="formColumnOptions" onclick="javascript:dialog.showContextHelp(arguments[0], 10153)">
                                    <fmt:message key="dialog.admin.additionalfields.options" />
                                </label>
                            </span>
                            <div class="tableContainer">
                                <!--<div id="formColumnOptions" interactive="true" class="hideTableHeader"></div>-->
								<div id="formColumnOptions" dojoType="dijit.layout.TabContainer" controllerWidget="dijit.layout.TabController" doLayout="false"></div>
                            </div>
                            <span class="input">
                                <input id="columnAsCombobox" dojoType="dijit.form.CheckBox" value="required">
                                <label onclick="javascript:dialog.showContextHelp(arguments[0], 10156)">
                                    <fmt:message key="dialog.admin.additionalfields.allowFreeEntries" />
                                </label>
                            </span>
                        </div>
                    </span>
                    <span class="outer halfWidth">
                        <div>
                            <span class="label">
                                <label for="formColumnWidth" onclick="javascript:dialog.showContextHelp(arguments[0], 10154)">
                                    <fmt:message key="dialog.admin.additionalfields.width" />
                                </label>
                            </span>
                            <span class="input">
                                <table style="width:100%; margin: -2px 0 0 0;"><tr><td>
                                    <input dojoType="dijit.form.NumberTextBox" constraints="{min:0}" id="formColumnWidth" class="fullWidth" />
                                </td><td style='width:1px; padding-left:5px;'>px</td></tr>
                                </table>
                            </span>
                        </div>
                    </span>
					<span class="outer halfWidth">
                        <div>
                            <span class="label">
                                <label for="formColumnIndex" onclick="javascript:dialog.showContextHelp(arguments[0], 10155)">
                                    <fmt:message key="dialog.admin.additionalfields.index" />
                                </label>
                            </span>
                            <span class="input">
                                 <input dojoType="dijit.form.TextBox" id="formColumnIndex" class="fullWidth" />
                            </span>
                        </div>
                    </span>
                    
                    <!-- CONTENT END -->
                    <button dojoType="dijit.form.Button" style="float:left;">
                        <fmt:message key="dialog.admin.additionalfields.btnClose" />
                        <script type="dojo/method" event="onClick" args="evt">
                            scriptScopeEditColumn.hide();
                        </script>
                    </button>
                    <button id="saveAddColumn" dojoType="dijit.form.Button" style="float:right;" >
                        <fmt:message key="dialog.admin.additionalfields.btnAdd" />
                        <script type="dojo/method" event="onClick" args="evt">
                            scriptScopeEditColumn.save();
                        </script>
                    </button>
                    <div class="fill"></div>
        </div>
        
    </body>
</html>