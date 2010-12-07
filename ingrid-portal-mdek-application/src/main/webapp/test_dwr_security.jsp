<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script src='/ingrid-portal-mdek-application/dwr/interface/SecurityService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>

<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>


<script type="text/javascript">
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");


getGroups = function() {
	SecurityService.getGroups(true, {
		callback: function(groupList) {
			dojo.widget.byId("groupTable").store.setData(groupList);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

getGroupDetails = function() {
	var selectedData = dojo.widget.byId("groupTable").getSelectedData();

	if (selectedData.length != 1) {
		return;
	}

	var group = selectedData[0];

	SecurityService.getGroupDetails(group.name, {
		callback: function(data) {
			updateGroup(group, data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

createGroup = function() {
	var group = { name:"testGroup" }

	SecurityService.createGroup(group, true, {
		callback: function(data) {
			dojo.widget.byId("groupTable").store.addData(data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});	
}

deleteGroup = function() {
	var selectedData = dojo.widget.byId("groupTable").getSelectedData();

	if (selectedData.length != 1) {
		return;
	}

	var group = selectedData[0];

	SecurityService.deleteGroup(group.id, {
		callback: function() {
			dojo.widget.byId("groupTable").store.removeData(group);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

storeGroup = function() {
	var selectedData = dojo.widget.byId("groupTable").getSelectedData();

	if (selectedData.length != 1) {
		return;
	}

	var group = selectedData[0];
	if (group.name.indexOf("_test") != -1) {
		group.name = group.name.substring(0, group.name.indexOf("_test"));
	} else {
		group.name = group.name+"_test";
	}

	SecurityService.storeGroup(group, true, {
		callback: function(data) {
			updateGroup(group, data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

createCatalogAdmin = function() {
	var catAdmin = {
		addressUuid: "3866462F-B449-11D2-9A86-080000507261",
		groupIds: ["247308"],	// Administrators
		role: "1",			// IDC_ROLE_CATALOG_ADMINISTRATOR
		parentUserId: null
	}

	SecurityService.createUser(catAdmin, true, {
		callback: function(data) {
			dojo.widget.byId("catAdminTable").store.setData([data]);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});	
}

getCatalogAdmin = function() {
	SecurityService.getCatalogAdmin( {
		callback: function(data) {
			dojo.widget.byId("catAdminTable").store.setData([data]);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});	
}

deleteCatalogAdmin = function() {
	SecurityService.getCatalogAdmin( {
		callback: function(data) {
			SecurityService.deleteUser(data.id, {
				callback: function(data) {
					dojo.widget.byId("catAdminTable").store.clearData();
				},
				errorHandler: function(errMsg, err) {
					dojo.debug(errMsg);
					dojo.debugShallow(err);
				}
			});			
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

getCurrentPortalUserData = function() {
	SecurityService.getCurrentPortalUserData({
		callback: function(userData) {
			dojo.widget.byId("portalUserTable").store.setData([userData]);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});

}

function updateGroup(group, newData) {
	var groupStore = dojo.widget.byId("groupTable").store;
	groupStore.update(group, "id", newData.id);
	groupStore.update(group, "name", newData.name);
	groupStore.update(group, "lastEditor", newData.lastEditor);
	groupStore.update(group, "creationTime", newData.creationTime);
	groupStore.update(group, "modificationTime", newData.modificationTime);
}


</script>
</head>

<body>
	<h1>Group test</h1>
	<button dojoType="Button" onclick="javascript:getGroups();">Get Groups</button>
	<button dojoType="Button" onclick="javascript:getGroupDetails();">Get Group Details</button>
	<button dojoType="Button" onclick="javascript:createGroup();">Create Group</button>
	<button dojoType="Button" onclick="javascript:storeGroup();">Store Group</button>
	<button dojoType="Button" onclick="javascript:deleteGroup();">Delete Group</button>

	<h1>Group Table</h1>
	<table id="groupTable" valueField="id" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y %H:%m:%S" minRows="4" cellspacing="0" class="filteringTable interactive readonly">
		<thead>
			<tr>
				<th field="id" dataType="String" width="80">Identifier</th>
 				<th field="name" dataType="String" width="200">Name</th>
 				<th field="lastEditor" dataType="String" width="200">Ge&auml;ndert von</th>
 				<th field="creationTime" dataType="Date" width="200">Erstellt am</th>
 				<th field="modificationTime" dataType="Date" width="200">Ge&auml;ndert am</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>


	<h1>User test</h1>
	<h2>Catalog Admin</h2>
	<button dojoType="Button" onclick="javascript:createCatalogAdmin();">Create Catalog Administrator</button>
	<button dojoType="Button" onclick="javascript:getCatalogAdmin();">Get Catalog Administrator</button>
	<button dojoType="Button" onclick="javascript:deleteCatalogAdmin();">Delete Catalog Administrator</button>

	<table id="catAdminTable" valueField="id" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y %H:%m:%S" minRows="1" cellspacing="0" class="filteringTable interactive readonly">
		<thead>
			<tr>
				<th field="id" dataType="String" width="80">Identifier</th>
 				<th field="addressUuid" dataType="String" width="250">Address UUID</th>
 				<th field="groupId" dataType="String" width="80">Group ID</th>
 				<th field="role" dataType="String" width="80">Role</th>
 				<th field="parentUserId" dataType="String" width="120">Parent User ID</th>
 				<th field="lastEditor" dataType="String" width="250">Ge&auml;ndert von</th>
 				<th field="creationTime" dataType="Date" width="200">Erstellt am</th>
 				<th field="modificationTime" dataType="Date" width="200">Ge&auml;ndert am</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h2>Portal User</h2>
	<button dojoType="Button" onclick="javascript:getCurrentPortalUserData();">Get current portal user data</button>

	<table id="portalUserTable" valueField="id" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y %H:%m:%S" minRows="1" cellspacing="0" class="filteringTable interactive readonly">
		<thead>
			<tr>
				<th field="id" dataType="String" width="80">Identifier</th>
 				<th field="addressUuid" dataType="String" width="250">Address UUID</th>
 				<th field="plugId" dataType="String" width="100">Plug ID</th>
 				<th field="portalLogin" dataType="String" width="80">Portal Login</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

</body>
</html>
