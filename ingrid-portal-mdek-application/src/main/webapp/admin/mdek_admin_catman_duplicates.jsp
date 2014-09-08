<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">

var pageDuplicates = _container_;

require([
    "dojo/on",
    "dojo/aspect",
    "dojo/window",
    "dijit/registry",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/Deferred",
    "dojo/string",
    "ingrid/utils/Grid",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist",
    "ingrid/layoutCreator",
    "ingrid/tree/MetadataTree",
    "ingrid/utils/LoadingZone",
    "ingrid/dialog",
    /*needed for toggleInfo*/"ingrid/utils/UI"
], function(on, aspect, wnd, registry, lang, array, Deferred, string, UtilGrid, UtilStore, UtilSyslist, layoutCreator, MetadataTree, LoadingZone, dialog) {


            var duplicatesListData = {
                identifier: 'identifier',
                items: []
            };

            on(_container_, "Load", function() {
            	console.log("Page loaded: " + this.id);
                registry.byId("contentPane").resize();
                createDOMElements();
                initDuplicatesTable();
                // Load duplicates info on startup
                pageDuplicates.startDuplicatesJob();
            });

            function initDuplicatesTable() {
                var duplicatesTable = UtilGrid.getTable("duplicatesListTable");
                aspect.after(duplicatesTable, "onSelectedRowsChanged", function() {
                    fillData(UtilGrid.getSelectedData("duplicatesListTable")[0]);
                });
                // grid needs to be resized after page layout is ready
                setTimeout(function() {
                	duplicatesTable.reinitLastColumn(true);
                }, 300);
            }

            function fillData(data) {
            	if (data) {
                    registry.byId("duplicatesObjectName").set("value", data.title);
                    registry.byId("duplicatesObjectDescription").set("value", data.generalDescription);
                    registry.byId("duplicatesObjectClass").set("value", UtilSyslist.getSyslistEntryName(8000, data.objectClass));            		
            	}
            }

            // Switch to the tree view and select the node referenced by the menu action
            pageDuplicates.selectObjectInTree = function(menuItem) {
                registry.byId("duplicatesLists").selectChild("duplicatesList2");
                var objUuid = clickedSlickGrid.getDataItem(clickedRow).uuid;
                selectObjectInTreeByUuid(objUuid);
            };

            // Select the node with uuid in the tree
            function selectObjectInTreeByUuid(uuid) {
                ObjectService.getPathToObject(uuid, {
                    callback: function(path) {
                        //console.debug(path);
                        //var def = expandPathDef(path);
                        path.splice(0, 0, "objectRoot");
                        path.splice(0, 0, "ObjectsAndAddressesRoot");
                        var targetNodeId = path[path.length - 1];
                        registry.byId("duplicatesTree").set('paths', [path])
                            .then(selectNode);
                    },
                    errorHandler: function(msg, err) {
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                    }
                });
            }


            // Get a child from a node in the tree for the given uuid 
            /*function getChildFromNode(childUuid, node) {
                for (var i = 0; i < node.children.length; ++i) {
                    if (node.children[i].uuid == childUuid) {
                        return node.children[i];
                    }
                }
                return null;
            }
*/
            function selectNode() {
                var node = registry.byId("duplicatesTree").get("selectedNode");
                wnd.scrollIntoView(node.domNode);
                loadObject({
                    id: node.item.id
                });
            }

            function loadObject(data) {
                var uuid = data.id;
                if (uuid != "objectRoot" && uuid != "addressRoot" && uuid != "addressFreeRoot") {
                    ObjectService.getNodeData(uuid, "O", true, {
                        preHook: LoadingZone.show,
                        postHook: LoadingZone.hide,
                        callback: fillData
                    });
                }
            }

            function createDOMElements() {
                new MetadataTree({
                    showRoot: false,
                    onClick: loadObject
                }, "duplicatesTree");

                var duplicatesListTableStructure = [{
                    field: 'title',
                    name: 'title',
                    width: '258px'
                }];
                layoutCreator.createDataGrid("duplicatesListTable", null, duplicatesListTableStructure, null);
            }

            function getDuplicatesDef() {
                var def = new Deferred();

                CatalogManagementService.getDuplicateObjects({
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(duplicateList) {
                        def.resolve(duplicateList);
                    },
                    errorHandler: function(msg, err) {
                        LoadingZone.hide();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.reject();
                    }
                });

                return def;
            }

            pageDuplicates.startDuplicatesJob = function() {
                clearInputFields();

                getDuplicatesDef()
                    .then(function(duplicatesList) {
                        UtilStore.updateWriteStore("duplicatesListTable", duplicatesList);
                        UtilGrid.getTable("duplicatesListTable").resizeCanvas();
                    });
            };

            function clearInputFields() {
                registry.byId("duplicatesObjectName").set("value", "");
                registry.byId("duplicatesObjectDescription").set("value", "");
                registry.byId("duplicatesObjectClass").set("value", "");
                registry.byId("duplicatesListTable").setSelectedRows([]);
            }

            pageDuplicates.saveChanges = function() {
                // Get the new name from the name input field
                // Store the new value in the backend
                // If an error occured -> abort
                // If it was successful update the table, refresh the tree and select the updated node in the tree
                var newObjectName = lang.trim(registry.byId("duplicatesObjectName").getValue());
                var selectedRow = UtilGrid.getSelectedRowIndexes("duplicatesListTable")[0];
                var tree = registry.byId("duplicatesTree");

                var selectedTab = registry.byId("duplicatesLists").selectedChildWidget.id;
                var uuid = null;
                if (selectedTab === "duplicatesList1") {
                    uuid = UtilGrid.getSelectedData("duplicatesListTable")[0].uuid;

                } else {
                    uuid = tree.get("selectedNode").item.id;
                }

                if (newObjectName && uuid) {
                    var def = storeNewObjectNameDef(uuid, newObjectName);
                    def.then(function() {
                        UtilGrid.updateTableDataRowAttr("duplicatesListTable", selectedRow, "title", newObjectName);
                        tree.refreshChildren(tree.rootNode)
                            .then(function() {
                                if (selectedTab === "duplicatesList2") {
                                    selectObjectInTreeByUuid(uuid);
                                }
                            });
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.management.duplicates.success' />", dialog.INFO);
                    }, function(err) {
                        console.debug("Error: " + err);
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.generalError' />", [err + ""]), dialog.WARNING);
                    });

                } else {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.invalidObjectNameError' />", dialog.WARNING);
                }
            };

            function storeNewObjectNameDef(objUuid, objName) {
                var def = new Deferred();

                ObjectService.updateObjectTitle(objUuid, objName, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function() {
                        def.resolve();
                    },
                    errorHandler: function(msg, err) {
                        LoadingZone.hide();
                        console.debug("Error: " + msg);
                        def.reject();
                    }
                });

                return def;
            }

            /*         function showLoadingZone() {
            //dojo.html.setVisibility(dom.byId("duplicatesLoadingZone"), "visible");
            dom.byId("duplicatesLoadingZone").style.visibility = "visible";
        }
        
        function hideLoadingZone() {
            //dojo.html.setVisibility(dom.byId("duplicatesLoadingZone"), "hidden");
            dom.byId("duplicatesLoadingZone").style.visibility = "hidden";
        } */
        });
</script>
</head>

<body>

<!-- CONTENT START -->
	<div id="duplicatesContent" data-dojo-type="dijit.layout.BorderContainer" class="content" style="height:100%; border: 0;">

		<!-- INFO START -->
		<!-- LEFT HAND SIDE CONTENT START -->
		<div id="duplicatesListContainer" data-dojo-type="dijit/layout/ContentPane" region="leading" class="inputContainer" style="padding:5px;">
			<div id="duplicatesLists" data-dojo-type="dijit/layout/TabContainer" style="width: 270px; height: 100%;" selectedChild="duplicatesList1">
				<!-- TAB 1 START -->
				<div id="duplicatesList1" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.admin.catalog.management.duplicates.list" />">
                    <h3><fmt:message key="dialog.admin.catalog.management.duplicates.result" /></h3>
					<div class="tableContainer">
						<div id="duplicatesListTable" autoHeight="13" forceGridHeight="false" class="hideTableHeader" contextMenu="DUPLICATE_GRID"></div>
					</div>
				</div> <!-- TAB 1 END -->

        		<!-- TAB 2 START -->
				<div id="duplicatesList2" data-dojo-type="dijit/layout/ContentPane" class="grey" title="<fmt:message key="dialog.admin.catalog.management.duplicates.tree" />">

					<div class="inputContainer grey">
						<div id="duplicatesTree"></div>
					</div>
				</div> <!-- TAB 2 END -->
   
        	</div>
		</div>
		<!-- LEFT HAND SIDE CONTENT END -->

		<!-- RIGHT HAND SIDE CONTENT START -->
		<div id="duplicatesData" data-dojo-type="dijit/layout/ContentPane" region="center" class="inputContainer field">
			<div id="winNavi" style="top:0;">
                    <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-2#overall-catalog-management-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
			<div class="inputContainer grey">
			    <span class="outer"><div>
			    <span class="label"><label for="duplicatesObjectName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8030)"><fmt:message key="dialog.admin.catalog.management.duplicates.objectName" /></label></span>
				<span class="input spaceBelow"><input type="text" id="duplicatesObjectName" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;"/></span>
				</div></span>
				<span class="outer"><div>
				<span class="label"><label for="duplicatesObjectClass" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8031)"><fmt:message key="dialog.admin.catalog.management.duplicates.objectClass" /></label></span>
				<span class="input spaceBelow"><input type="text" id="duplicatesObjectClass" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" /></span>
				</div></span>
				<span class="outer"><div>
				<span class="label"><label for="duplicatesObjectDescription" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8032)"><fmt:message key="dialog.admin.catalog.management.duplicates.objectDescription" /></label></span>
           		<span class="input"><input type="text" mode="textarea" rows="6" id="duplicatesObjectDescription" disabled="true" data-dojo-type="dijit/form/SimpleTextarea" style="width:100%;" /></span> 
				</div></span>
				<div class="fill"></div>
			</div>

			<div class="inputContainer">
				<span class="button">
					<span style="float:right;">
						<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.duplicates.saveChanges" />" onclick="pageDuplicates.saveChanges()"><fmt:message key="dialog.admin.catalog.management.duplicates.saveChanges" /></button>
					</span>
					<span style="float:right;">
						<button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.duplicates.refresh" />" onclick="pageDuplicates.startDuplicatesJob()"><fmt:message key="dialog.admin.catalog.management.duplicates.refresh" /></button>
					</span>
					<span id="duplicatesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>
		</div> <!-- RIGHT HAND SIDE CONTENT END -->
	</div>
</body>
</html>
