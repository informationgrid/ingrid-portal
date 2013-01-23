<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    dojo.require("ingrid.dijit.CustomTree");
    dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");

var scriptScopePermission = _container_;

createDOMElements();

dojo.connect(_container_, "onLoad", function() {
    dojo.connect(dijit.byId("permissionOverviewTreeObj"), "onClick", scriptScopePermission.displayPermissionsForObject);
    dojo.connect(dijit.byId("permissionOverviewTreeAdr"), "onClick", scriptScopePermission.displayPermissionsForAddress);
});

// This method is executed when an object in the tree is selected 
// The argument is an event with an attached treeNode (event.node)
scriptScopePermission.displayPermissionsForObject = function(item, node){
	var uuid = item.uuid[0];
	//var objPermission = dijit.byId("rightsObjectsUserList");
	if (uuid == null || uuid == "objectRoot") {
		UtilGrid.setTableData("rightsObjectsUserList", []);
		return;
	}

	SecurityService.getUsersWithPermissionForObject(uuid, false, true, {
		callback: function(userList) {
			UtilGrid.setTableData("rightsObjectsUserList", []);
			for (var i in userList) {
				addPermissionsToUser(userList[i]);
				userList[i].title = UtilAddress.createAddressTitle(userList[i].address);

				UtilGrid.addTableDataRow("rightsObjectsUserList", userList[i]);
			}
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			console.debug(errMsg);
		}
	});
}


// This method is executed when an address in the tree is selected 
// The argument is an event with an attached treeNode (event.node)
scriptScopePermission.displayPermissionsForAddress = function(item, node){
	var uuid = item.uuid[0];
	//var adrPermission = dijit.byId("rightsAddressesUserList");

	if (uuid == null || uuid == "addressRoot" || uuid == "addressFreeRoot") {
		UtilGrid.setTableData("rightsAddressesUserList", []);
		return;
	}

	SecurityService.getUsersWithPermissionForAddress(uuid, false, true, {
		callback: function(userList) {
			UtilGrid.setTableData("rightsAddressesUserList", []);
			for (var i in userList) {
				addPermissionsToUser(userList[i]);
				userList[i].title = UtilAddress.createAddressTitle(userList[i].address);

				UtilGrid.addTableDataRow("rightsAddressesUserList", userList[i]);
			}
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			console.debug(errMsg);
		}
	});
}


function createDOMElements() {
	createCustomTree("permissionOverviewTreeObj", null, "id", "title", expandLoadObjects);
	createCustomTree("permissionOverviewTreeAdr", null, "id", "title", expandLoadAddresses);
	
	var rightsObjectsUserListStructure = [
		{field: 'title',name: "<fmt:message key='dialog.admin.permissions.userName' />", sortable: true, width: 280-scrollBarWidth+'px'},
		{field: 'roleName',name: "<fmt:message key='dialog.admin.permissions.role' />",width: '150px'},
		{field: 'writeSingle',name: "<fmt:message key='dialog.admin.permissions.single' />",width: '100px'},
		{field: 'writeTree',name: "<fmt:message key='dialog.admin.permissions.tree' />",width: '100px'},
        {field: 'writeSubNode',name: "<fmt:message key='dialog.admin.permissions.subnodes' />",width: '100px'},
		{field: 'qa',name: "<fmt:message key='dialog.admin.permissions.qa' />",width: '100px'}
    ];
    createDataGrid("rightsObjectsUserList", null, rightsObjectsUserListStructure, null);
	
	createDataGrid("rightsAddressesUserList", null, dojo.clone(rightsObjectsUserListStructure), null);
	
}

function expandLoadObjects(node, callback_function){
	var parentItem = node.item;
	var prefix = "permAdmin_";
	var store = dijit.byId("permissionOverviewTreeObj").model.store;
	/*if (!parentItem.root) {
		id = parentItem.id[0].substring(5);
		//id = id.substring(5);
		type = parentItem.nodeAppType[0];
	}*/
	var def = UtilTree.getSubTree(parentItem, prefix.length);
	
	def.addCallback(function(data){
	    // just use the object node here!
	    if (parentItem.root) {
			data[0].uuid = data[0].id;
			data[0].id = prefix + data[0].id;
	        store.newItem(data[0]);
	    }
	    else {
	        //parentItem.id = "groupAdmin_"+parentItem.id;
	        dojo.forEach(data, function(entry){
				entry.uuid = entry.id;
				entry.id = prefix+entry.id;
	            store.newItem(entry, {
	                parent: parentItem,
	                attribute: "children"
	            });
	        });
	    }
	    callback_function();
	});
	def.addErrback(function(res){
	    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
	    console.debug(res);
	    return res;
	});
	return def;
}
	
function expandLoadAddresses(node, callback_function){
	var parentItem = node.item;
	var prefix = "permAdmin_";
	var store = dijit.byId("permissionOverviewTreeAdr").model.store;
	
	var def = UtilTree.getSubTree(parentItem, prefix.length);
	
	def.addCallback(function(data){
	    //return _this.loadProcessResponse(node, data);
	    // just use the object node here!
	    if (parentItem.root) {
	        var origId = data[1].id;
			//data[1].id = prefix + data[1].id;
			data[1].uuid = data[1].id;
			data[1].id = prefix + data[1].id;
	        store.newItem(data[1]);
	        // remove prefix from the item of the new node widget
	        //dijit.byId(data[1].id).item.id = origId;
	    }
	    else {
	        dojo.forEach(data, function(entry){
	            var origId = entry.id;
				//entry.id = prefix+entry.id;
				entry.uuid = entry.id;
				entry.id = prefix+entry.id;
	            store.newItem(entry, {
	                parent: parentItem,
	                attribute: "children"
	            });
	            // remove prefix from the item of the new node widget
	            //dijit.byId(entry.id).item.id = origId;
	        });
	    }
	    callback_function();
	});
	def.addErrback(function(res){
	    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
	    console.debug(res);
	    return res;
	});
	return def;
}

// Helper function that adds html tags to a user object, for display in the result table.
function addPermissionsToUser(user) {
	for (var i in user.permissions) {
		if (user.permissions[i] == "WRITE_SINGLE") {
			user.writeSingle = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";

		} else if (user.permissions[i] == "WRITE_TREE") {
			user.writeTree = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
        } else if (user.permissions[i] == "WRITE_SUBNODE") {
            user.writeSubNode = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
		} else if (user.permissions[i] == "QUALITY_ASSURANCE") {
			user.qa = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";

//		} else if (user.permission[i] == "CREATE_ROOT") {
//			user.writeSingle = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
		}
	}
}

</script>


</head>
<body>
    <div class="contentBlockWhite top">
      <div id="winNavi" style="top:0;">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=user-administration-3#user-administration-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
  	  </div>
  	  <div id="rightsAdmin" class="content">

        <!-- CONTENT BLOCK 1 START -->
        <!-- SPLIT CONTAINER START -->
        <div>
          <span class="label required"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8025)"><fmt:message key="dialog.admin.permissions.objectPermissions" /></label></span>
          <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" style="height:250px;" id="rightsObjects">
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
            <div dojoType="dijit.layout.ContentPane" region="leading" splitter="true" class="inputContainer grey" style="width: 200px;">
              	<div class="inputContainer">
					<div id="permissionOverviewTreeObj"></div>
				</div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->

            <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
            <div dojoType="dijit.layout.ContentPane" region="center" class="inputContainer" style="padding:5px;">
       
              <div id="rightsObjectsUser" class="inputContainer grey" style="width:100%;">
				<div class="tableContainer">
					<div id="rightsObjectsUserList" autoHeight="9" contextMenu="none"></div>
					</div>
            	</div>
            </div>
            <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
          </div>
        </div>
        <!-- CONTENT BLOCK 1 END -->

        <div class="spacer"></div>

        <!-- CONTENT BLOCK 2 START -->
        <!-- SPLIT CONTAINER START -->
        <div style="padding-top:15px;">
          <span class="label required"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8026)"><fmt:message key="dialog.admin.permissions.addressPermissions" /></label></span>
          <div dojoType="dijit.layout.BorderContainer" id="rightsAddresses" orientation="horizontal" style="height:250px;" layoutAlign="client">
            <!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
            <div dojoType="dijit.layout.ContentPane" region="leading" splitter="true" class="inputContainer grey" style="width: 200px;">
				<div class="inputContainer">
              		<div id="permissionOverviewTreeAdr"></div>
				</div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->

            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
            <div dojoType="dijit.layout.ContentPane" region="center" class="inputContainer" style="padding:5px;">
       
              <div id="rightsAddressesUser" class="inputContainer grey" style="">
				<div class="tableContainer">
					<div id="rightsAddressesUserList" autoHeight="9" contextMenu="none"></div>
            		</div>
            	</div>
            </div>
            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
          </div>
      </div>
      <!-- CONTENT BLOCK 2 END -->
    </div>
    </div>
  <!-- CONTENT END -->

</body>
</html>
