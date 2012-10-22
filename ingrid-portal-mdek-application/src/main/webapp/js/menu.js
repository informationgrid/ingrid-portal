igeMenuBar = new Object();
/*********************************************************
 * OBJECT
 *********************************************************/

dojo.require("dijit.MenuBar");
dojo.require("dijit.MenuBarItem");
dojo.require("dijit.PopupMenuBarItem");


igeMenuBar.selectChild = function(child){
	// set label of menu item to show where we are
	dojo.byId("currentPageName").innerHTML = igeMenuBar.getMenuString(child);
	
	// switch to menu page
    dijit.byId("stackContainer").selectChild(dijit.byId(child));
}

igeMenuBar.getMenuString = function(entry) {
	return message.get("general.page") + ": " + message.get("page.breadcrumb."+entry);
}

igeMenuBar.create = function(pane) {
	var menu = new dijit.MenuBar({}).placeAt(pane.domNode);
	menu.addChild(new dijit.MenuBarItem({
        label: "<div class='image18px iconDashboard'></div>",
        //label: "<img src='img/home.gif'>",
        //'class': "image18px iconDashboard",
        //iconClass: "image18px iconDashboard",
		//style: "background-image: url(../img/ic_help.gif);",
		onClick: function(){
			igeMenuBar.selectChild("pageDashboard");
		}
    }));
	menu.addChild(new dijit.MenuBarItem({
        label: message.get("menu.main.hierarchyAcquisition"),
		onClick: function(){
			igeMenuBar.selectChild("pageHierarchy");
		}
    }));
    
    var researchMenu = new dijit.Menu({});
	researchMenu.addChild(new dijit.MenuItem({
        label: message.get("menu.main.research.search"),
		onClick: function(){
			igeMenuBar.selectChild("pageResearch");
		}
    }));
    researchMenu.addChild(new dijit.MenuItem({
        label: message.get("menu.main.research.thesaurusNavigator"),
        onClick: function(){
            igeMenuBar.selectChild("pageResearchThesaurus");
        }
    }));
    researchMenu.addChild(new dijit.MenuItem({
        label: message.get("menu.main.research.databaseSearch"),
        onClick: function(){
            igeMenuBar.selectChild("pageResearchDB");
        }
    }));
    menu.addChild(new dijit.PopupMenuBarItem({
        label: message.get("menu.main.research"),
        popup: researchMenu 
    })); 
    
    var qualityMenu = new dijit.Menu({});
	qualityMenu.addChild(new dijit.MenuItem({
        label: message.get("menu.main.qualityAssurance.editor"),
		onClick: function(){
			igeMenuBar.selectChild("pageQualityEditor");
		}
    }));
   if (UtilQA.isQAActive() && UtilSecurity.isCurrentUserQA()) {
        qualityMenu.addChild(new dijit.MenuItem({
            label: message.get("menu.main.qualityAssurance.qa"),
            onClick: function(){
                igeMenuBar.selectChild("pageQualityAssurance");
            }
        }));
    }
    
    menu.addChild(new dijit.PopupMenuBarItem({
        label: message.get("menu.main.qualityAssurance"),
        popup: qualityMenu 
    }));   
    
	menu.addChild(new dijit.MenuBarItem({
        label: message.get("menu.main.statistics"),
		onClick: function(){
			igeMenuBar.selectChild("pageStatistics");
		}
    }));
	
	//---------------------------------------
	// add the Administration with submenus
	//---------------------------------------
	
	// a separator
	menu.addChild(new dijit.MenuBarItem({label:"|", disabled:true, style:"padding:0 3px;"}));
	
	//---------------------------------------
	var adminMenuCatalog = new dijit.Menu({});
	adminMenuCatalog.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.catalog.settings"),
		onClick: function(){
			igeMenuBar.selectChild("catalogSettings");
		}
    }));
	adminMenuCatalog.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.catalog.generalSettings"),
		onClick: function(){
			igeMenuBar.selectChild("generalSettings");
		}
    }));
	
	//---------------------------------------
	var adminMenuUserManagement = new dijit.Menu({});
	adminMenuUserManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.user.userAdmin"),
		onClick: function(){
			igeMenuBar.selectChild("userManagement");
		}
    }));
	adminMenuUserManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.user.groupAdmin"),
		onClick: function(){
			igeMenuBar.selectChild("groupManagement");
		}
    }));
	adminMenuUserManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.user.permissions"),
		onClick: function(){
			igeMenuBar.selectChild("permissionOverview");
		}
    }));
	
	//---------------------------------------
	var adminMenuCatManagement = new dijit.Menu({});
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.analysis"),
		onClick: function(){
			igeMenuBar.selectChild("adminAnalysis");
		}		
    }));
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.duplicates"),
		onClick: function(){
			igeMenuBar.selectChild("adminDublet");
		}	
    }));
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.urls"),
		onClick: function(){
			igeMenuBar.selectChild("adminURL");
		}	
    }));
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.codelists"),
		onClick: function(){
			igeMenuBar.selectChild("adminCodeLists");
		}	
    }));
	/*adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: "Zusätzliche Felder",
		onClick: function(){
			igeMenuBar.selectChild("adminAdditionalFields");
		}	
    }));*/
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.additionalFields"),
        onClick: function(){
            igeMenuBar.selectChild("adminFormFields");
        }   
    }));
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.deleteAddress"),
		onClick: function(){
			igeMenuBar.selectChild("adminDeleteAddress");
		}	
    }));
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.searchTerms"),
		onClick: function(){
			igeMenuBar.selectChild("adminSearchTerms");
		}	
    }));
	adminMenuCatManagement.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.management.spatialSearchTerms"),
		onClick: function(){
			igeMenuBar.selectChild("adminLocations");
		}	
    }));

	//---------------------------------------
	var adminMenuImport = new dijit.Menu({});
	adminMenuImport.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.importExport.export"),
		onClick: function(){
			igeMenuBar.selectChild("adminExport");
		}
    }));
	adminMenuImport.addChild(new dijit.MenuItem({
        label: message.get("menu.admin.main.importExport.import"),
		onClick: function(){
			igeMenuBar.selectChild("adminImport");
		}
    }));
	
	//menu.addChild(new dijit.MenuSeparator());
	
    // only show all menus to mdek admin!
    if (currentUser.role == 1) {
        menu.addChild(new dijit.PopupMenuBarItem({
            label: message.get("menu.admin.main.catalog"),
            popup: adminMenuCatalog
        }));
    }
    
    // no user management for Authors
    if (currentUser.role != 3) {
        menu.addChild(new dijit.PopupMenuBarItem({
            label: message.get("menu.admin.main.user"),
            popup: adminMenuUserManagement
        }));
    }
    
    // only all menus for cat-admin
    if (currentUser.role == 1) {
        menu.addChild(new dijit.PopupMenuBarItem({
            label: message.get("menu.admin.main.management"),
            popup: adminMenuCatManagement
        }));
        
        menu.addChild(new dijit.PopupMenuBarItem({
            label: message.get("menu.admin.main.importExport"),
            popup: adminMenuImport
        }));
    }
}


// ------------------------- Context Menus -------------------------


// holds all different context menus
var contextMenu = {};

function initContextMenu(gridProperties) {
	var type = gridProperties.contextMenu;
    var menu = new dijit.Menu({id:"contextMenu_"+type});
    
    if (type == "DUPLICATE_GRID") {
        menu.addChild(new dijit.MenuItem({
            label: message.get('contextmenu.table.showInTree'),
            onClick: selectObjectInTree
        }));
        menu.addChild(new dijit.MenuItem({
            label: message.get('contextmenu.table.jumpToObject'),
            onClick: function() {
                menuEventHandler.handleSelectNodeInTree(clickedSlickGrid.getDataItem(clickedRow).uuid, "O");
            }
        }));
    } else {
        menu.addChild(new dijit.MenuItem({
            label: message.get('contextmenu.table.selectAll'),
            onClick: function() {
                        var rows = [];
                        var length = clickedSlickGrid.getData().length;
                        for (var i=0; i<length; i++) {
                            rows.push(i);
                        }
                        clickedSlickGrid.setSelectedRows(rows);
                    }
        }));
        
        menu.addChild(new dijit.MenuItem({
            id: "menuDeselectAll_"+type,
            label: message.get('contextmenu.table.deselectAll'),
            onClick: function() {
                        clickedSlickGrid.setSelectedRows([]);
                    }
        }));
        
        menu.addChild(new dijit.MenuSeparator());
        
        menu.addChild(new dijit.MenuItem({
            id: "menuRemoveSelected_"+type,
            label: message.get('contextmenu.table.removeSelected'),
            onClick: function() {
                        UtilGrid.removeTableDataRow(clickedSlickGrid.id, clickedSlickGrid.getSelectedRows());
                        clickedSlickGrid.setSelectedRows([]);
                    }
        }));
        
        menu.addChild(new dijit.MenuItem({
            id: "menuRemoveClicked_"+type,
            label: message.get('contextmenu.table.removeClicked'),
            onClick: function() {
                        UtilGrid.removeTableDataRow(clickedSlickGrid.id, [clickedRow]);
                    }
        }));
        
        // connect the special menu for the root node
        dojo.connect(menu, "_openMyself", this, function(e) {
            var findGrid = function(element) {
                while (element) {
                    if (dojo.hasClass(element, "ui-widget")) {
                        return element.id;
                    }
                    element = element.parentNode;
                }                
            }
            
            // all items have to be disabled if user has no permission if hierarchy page is used!
            var isHierarchyPage = dijit.byId("stackContainer").selectedChildWidget.id == "pageHierarchy";
            
            if (!isHierarchyPage || currentUdk.writePermission) {
                var grid = findGrid(e.target);
                var somethingIsSelected = UtilGrid.getSelectedRowIndexes(grid).length > 0;
                var somethingIsCopied   = UtilGrid.getTable(grid).copiedAddress != null;
                
                dojo.forEach(menu.getChildren(), function(i) {
                    // reset item to active first!
                    if (i.declaredClass != "dijit.MenuSeparator") i.setDisabled(false);
                    
                    if (!somethingIsSelected) {
                        if (i.id.indexOf("menuDeselectAll") != -1) i.setDisabled(true);
                        if (i.id.indexOf("menuRemoveSelected") != -1) i.setDisabled(true);
                    }
                    if (!clickedSlickGrid.getData()[clickedRow]) {
                        if (i.id.indexOf("menuRemoveClicked") != -1) i.setDisabled(true);
                        if (i.id.indexOf("menuEditClicked") != -1) i.setDisabled(true);
                        if (i.id.indexOf("menuShowAddress") != -1) i.setDisabled(true);
                        if (i.id.indexOf("menuCopyAddress") != -1) i.setDisabled(true);
                    }
                    if (!somethingIsCopied) {
                        if (i.id.indexOf("menuPasteAddress") != -1) i.setDisabled(true);
                    }
                });
            } else {
                dojo.forEach(menu.getChildren(), function(i) {
                    if (i.declaredClass != "dijit.MenuSeparator")
                        i.setDisabled(true);
                });
            }
        });
    }
    
    if (type == "GENERAL_ADDRESS") {
        menu.addChild(new dijit.MenuSeparator());
        menu.addChild(new dijit.MenuItem({
            id: "menuCopyAddress_"+type,
            label: message.get('contextmenu.table.copyAddress'),
            onClick: function() {
                var rowData = clickedSlickGrid.getData()[clickedRow];
                clickedSlickGrid.copiedAddress = rowData;
                console.debug(clickedSlickGrid.copiedAddress);
                //dialog.showPage('Adresse', 'dialogs/mdek_address_preview_dialog.html', 500, 240, false, { data:rowData });
            }
        }));
        menu.addChild(new dijit.MenuItem({
            id: "menuPasteAddress_"+type,
            label: message.get('contextmenu.table.pasteAddress'),
            onClick: function() {
                var rowData = clickedSlickGrid.getData()[clickedRow];
                console.debug(rowData);
                // make a copy of the address which is going to be copied
                var copyClone = dojo.clone(clickedSlickGrid.copiedAddress);
                // ignore role if address is being replaced
                if (rowData) {
                    // remember role first
                    var type = rowData.nameOfRelation;
                    UtilGrid.updateTableDataRow(clickedSlickGrid.id, clickedRow, copyClone);
                    UtilGrid.updateTableDataRowAttr(clickedSlickGrid.id, clickedRow, "nameOfRelation", type);
                } else {
                    UtilGrid.addTableDataRow(clickedSlickGrid.id, copyClone);
                }
            }
        }));
        menu.addChild(new dijit.MenuSeparator());
        menu.addChild(new dijit.MenuItem({
            id: "menuShowAddress_"+type,
            label: message.get('contextmenu.table.showAddress'),
            onClick: function() {
                var rowData = clickedSlickGrid.getData()[clickedRow];
                console.debug(rowData);
                dialog.showPage('Adresse', 'dialogs/mdek_address_preview_dialog.html', 500, 240, false, { data:rowData });
            }
        }));
    }
    
    if (type == "EDIT_OPERATION") {
        menu.addChild(new dijit.MenuSeparator());
        menu.addChild(new dijit.MenuItem({
            id: "menuEditClicked_"+type,
            label: message.get('contextmenu.table.editClicked'),
            onClick: function() {
                var rowData = clickedSlickGrid.getData()[clickedRow];
                ingridObjectLayout.openOperationDialog({ gridId:clickedSlickGridProperties.id, selectedRow:rowData });
            }
        }));
    }
    
    if (type == "EDIT_LINK") {
        menu.addChild(new dijit.MenuSeparator());
        menu.addChild(new dijit.MenuItem({
            id: "menuEditClicked_"+type,
            label: message.get('contextmenu.table.editClicked'),
            onClick: function() {
            	console.debug(clickedSlickGridProperties);
            	if (clickedSlickGridProperties.id == "ref1ServiceLink") {
            	    UtilUI.showNoModifiableTable();
            	    return;
            	}
                var rowData = clickedSlickGrid.getData()[clickedRow];
                ingridObjectLayout.openLinkDialog({ gridId:clickedSlickGridProperties.id, filter:clickedSlickGridProperties.relation_filter, selectedRow:rowData });
            }
        }));
    }
    
    contextMenu[type] = menu;
    return menu;
}

function _getClickedRowFromTarget(target, iteration) {
    var attr = target.parentNode.getAttribute("row");
    if (attr == null && iteration > 0)
        return _getClickedRowFromTarget(target.parentNode, iteration-1);
    return attr;
}

function createContextMenu(grid, gridId, gridProperties) {
    var type = gridProperties.contextMenu;
    if (type == undefined)
        type = gridProperties.contextMenu = "DEFAULT";
    else if (type == "none") {
    	return;
    }

	dojo.connect(grid.domNode, "oncontextmenu", grid, function (e) {
        e.preventDefault();
        var cell = this.getCellFromEvent(e);
        clickedRow = _getClickedRowFromTarget(e.target, 5); // look for row property within next 5 parents of target
        clickedSlickGrid = this; // needed for event when menuitem was clicked
        clickedSlickGridProperties = gridProperties;
    });
    
    var m = contextMenu[type];
    if (m == undefined) {
        console.debug("create new context menu");
        m = initContextMenu(gridProperties);
    }
    m.bindDomNode(dojo.byId(gridId));
}
